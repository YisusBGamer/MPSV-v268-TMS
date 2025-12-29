/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.BuddylistEntry
 *  Client.CharacterNameAndId
 *  Packet.BuddyListPacket
 */
package Client;

import Client.BuddylistEntry;
import Client.CharacterNameAndId;
import Client.MapleClient;
import Database.DatabaseLoader;
import Packet.BuddyListPacket;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuddyList
implements Serializable {
    private static final long serialVersionUID = 1413738569L;
    private final Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<Integer, BuddylistEntry>();
    private byte capacity;
    private boolean changed = false;

    public BuddyList(byte capacity) {
        this.capacity = capacity;
    }

    public boolean contains(int characterId) {
        return this.buddies.containsKey(characterId);
    }

    public boolean containsVisible(int characterId) {
        BuddylistEntry ble = this.buddies.get(characterId);
        return ble != null && ble.isVisible();
    }
    public enum BuddyAddResult {

        好友列表已滿, 已經是好友關係, 添加好友成功
    }

    public enum BuddyOperation {

        添加好友, 刪除好友
    }

    public byte getCapacity() {
        return this.capacity;
    }

    public void setCapacity(byte capacity) {
        this.capacity = capacity;
    }

    public BuddylistEntry get(int characterId) {
        return this.buddies.get(characterId);
    }

    public BuddylistEntry get(String characterName) {
        String lowerCaseName = characterName.toLowerCase();
        for (BuddylistEntry ble : this.buddies.values()) {
            if (!ble.getName().toLowerCase().equals(lowerCaseName)) continue;
            return ble;
        }
        return null;
    }

    public void put(BuddylistEntry entry) {
        this.buddies.put(entry.getCharacterId(), entry);
        this.changed = true;
    }

    public void remove(int characterId) {
        this.buddies.remove(characterId);
        this.changed = true;
    }

    public Collection<BuddylistEntry> getBuddies() {
        return this.buddies.values();
    }

    public boolean isFull() {
        return this.buddies.size() >= this.capacity;
    }

    public int[] getBuddyIds() {
        int[] buddyIds = new int[this.buddies.size()];
        int i = 0;
        for (BuddylistEntry ble : this.buddies.values()) {
            if (!ble.isVisible()) continue;
            buddyIds[i++] = ble.getCharacterId();
        }
        return buddyIds;
    }

    public void loadFromTransfer(List<CharacterNameAndId> data) {
        for (CharacterNameAndId qs : data) {
            this.put(new BuddylistEntry(qs.getName(), qs.getId(), qs.getGroup(), -1, qs.isVisible()));
        }
    }

    public void loadFromDb(int characterId) throws SQLException {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, c.name AS buddyname, b.groupname FROM buddies AS b, characters AS c WHERE c.id = b.buddyid AND b.characterid = ?");){
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    this.put(new BuddylistEntry(rs.getString("buddyname"), rs.getInt("buddyid"), rs.getString("groupname"), -1, rs.getInt("pending") != 1));
                }
            }
        }
    }

    public void addBuddyRequest(MapleClient c, int cidFrom, String nameFrom, int channelFrom, int levelFrom, int jobFrom) {
        this.put(new BuddylistEntry(nameFrom, cidFrom, "未指定群組", channelFrom, false));
        c.announce(BuddyListPacket.aarequestBuddylistAdd((int)cidFrom, (String)nameFrom, (int)channelFrom, (int)levelFrom, (int)jobFrom));
    }

    public void setChanged(boolean v) {
        this.changed = v;
    }

    public boolean changed() {
        return this.changed;
    }
}

