/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character.keys;

import SwordieX.client.character.keys.Keymapping;
import connection.OutPacket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import tools.data.MaplePacketLittleEndianWriter;

public class FuncKeyMap {
    private Map<Integer, Keymapping> keymaps;
    private boolean changed = false;
    private int slot = 0;
    private int mode = 1;
    public static final int MAX_LAYOUT = 3;
    private int maxKeyBinds = 89;
    private int maxCombination = 10;

    public FuncKeyMap(Map<Integer, Keymapping> keymaps) {
        this.keymaps = keymaps;
    }

    public static void init(Connection con, int charid, boolean oldkey) throws SQLException {
        int[] array1 = new int[]{1, 2, 3, 4, 5, 6, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 29, 31, 34, 35, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 50, 56, 57, 59, 60, 61, 63, 64, 65, 66, 70};
        int[] array2 = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 4};
        int[] array3 = new int[]{46, 10, 12, 13, 18, 23, 8, 5, 0, 4, 27, 30, 39, 1, 41, 19, 14, 15, 52, 2, 17, 11, 3, 20, 26, 16, 22, 9, 50, 51, 6, 31, 29, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106, 47};
        int[] new_array1 = new int[]{1, 20, 21, 22, 23, 25, 26, 27, 29, 34, 35, 36, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 49, 50, 52, 56, 57, 59, 60, 61, 63, 64, 65, 66, 70, 71, 73, 79, 82, 83};
        int[] new_array2 = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4};
        int[] new_array3 = new int[]{46, 27, 30, 0, 1, 19, 14, 15, 52, 17, 11, 8, 3, 20, 26, 16, 22, 9, 50, 51, 2, 31, 29, 5, 7, 4, 53, 54, 100, 101, 102, 103, 104, 105, 106, 47, 12, 13, 23, 10, 18};
        PreparedStatement ps = con.prepareStatement("INSERT INTO keymap (characterid, `slot`, `key`, `type`, `action`) VALUES (?, ?, ?, ?, ?)");
        ps.setInt(1, charid);
        ps.setInt(2, 0);
        int keylength = oldkey ? array1.length : new_array1.length;
        for (int j = 0; j < keylength; ++j) {
            ps.setInt(3, oldkey ? array1[j] : new_array1[j]);
            ps.setInt(4, oldkey ? array2[j] : new_array2[j]);
            ps.setInt(5, oldkey ? array3[j] : new_array3[j]);
            ps.execute();
        }
        ps.close();
    }

    public static Map<Integer, FuncKeyMap> load(Connection con, int charid, int slot, Map<Integer, FuncKeyMap> funcKeyMaps) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ? AND slot = ?");
        ps.setInt(1, charid);
        ps.setInt(2, slot);
        ResultSet rs = ps.executeQuery();
        HashMap<Integer, Keymapping> keymaps = new HashMap<Integer, Keymapping>();
        while (rs.next()) {
            keymaps.put(rs.getInt("key"), new Keymapping(rs.getByte("type"), rs.getInt("action")));
        }
        funcKeyMaps.put(slot, new FuncKeyMap(keymaps));
        rs.close();
        ps.close();
        return funcKeyMaps;
    }

    public void save(Connection con, int charid, int slot) throws SQLException {
        if (!this.changed) {
            return;
        }
        PreparedStatement ps = con.prepareStatement("DELETE FROM keymap WHERE characterid = ? AND slot = ?");
        ps.setInt(1, charid);
        ps.setInt(2, slot);
        ps.execute();
        ps.close();
        if (this.keymaps.isEmpty()) {
            return;
        }
        ps = con.prepareStatement("INSERT INTO keymap VALUES (DEFAULT, ?, ?, ?, ?, ?)");
        ps.setInt(1, charid);
        ps.setInt(2, slot);
        for (Map.Entry<Integer, Keymapping> keybinding : this.keymaps.entrySet()) {
            ps.setInt(3, keybinding.getKey());
            ps.setByte(4, keybinding.getValue().getType());
            ps.setInt(5, keybinding.getValue().getAction());
            ps.execute();
        }
        ps.close();
    }

    public void encode(MaplePacketLittleEndianWriter mplew) {
        OutPacket outPacket = new OutPacket();
        this.encode(outPacket);
        mplew.write(outPacket.getData());
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(this.keymaps.isEmpty() ? 1 : 0);
        if (!this.keymaps.isEmpty()) {
            Keymapping binding;
            int i;
            for (i = 0; i < this.mode; ++i) {
                for (int x = 0; x < this.maxKeyBinds; ++x) {
                    binding = this.keymaps.get(x);
                    if (binding != null) {
                        outPacket.encodeByte(binding.getType());
                        outPacket.encodeInt(binding.getAction());
                        continue;
                    }
                    outPacket.encodeByte(0);
                    outPacket.encodeInt(0);
                }
            }
            for (i = 0; i < this.maxCombination; ++i) {
                binding = this.keymaps.get(102 + i);
                if (binding != null) {
                    outPacket.encodeByte(binding.getType());
                    outPacket.encodeInt(binding.getAction());
                    continue;
                }
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
            }
        }
    }

    @Generated
    public void setKeymaps(Map<Integer, Keymapping> keymaps) {
        this.keymaps = keymaps;
    }

    @Generated
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Generated
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Generated
    public void setMode(int mode) {
        this.mode = mode;
    }

    @Generated
    public void setMaxKeyBinds(int maxKeyBinds) {
        this.maxKeyBinds = maxKeyBinds;
    }

    @Generated
    public void setMaxCombination(int maxCombination) {
        this.maxCombination = maxCombination;
    }

    @Generated
    public Map<Integer, Keymapping> getKeymaps() {
        return this.keymaps;
    }

    @Generated
    public boolean isChanged() {
        return this.changed;
    }

    @Generated
    public int getSlot() {
        return this.slot;
    }

    @Generated
    public int getMode() {
        return this.mode;
    }

    @Generated
    public int getMaxKeyBinds() {
        return this.maxKeyBinds;
    }

    @Generated
    public int getMaxCombination() {
        return this.maxCombination;
    }
}

