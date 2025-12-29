/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import Client.MapleClient;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.ItemLoader;
import Client.inventory.MapleInventoryType;
import Config.constants.ItemConstants;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.server.MapleItemInformationProvider;
import Packet.NPCPacket;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;

public class MapleTrunk {
    private static final Logger log = LoggerFactory.getLogger(MapleTrunk.class);
    private final int storageId;
    private final int accountId;
    private final List<Item> items;
    private final Map<MapleInventoryType, List<Item>> typeItems = new EnumMap<MapleInventoryType, List<Item>>(MapleInventoryType.class);
    private short slots;
    private int storageNpcId;
    private boolean changed = false;
    private boolean pwdChecked = false;

    private MapleTrunk(int storageId, short slots, Long meso, int accountId) {
        this.storageId = storageId;
        this.slots = slots;
        this.accountId = accountId;
        this.items = new LinkedList<Item>();
        if (this.slots > 128) {
            this.slots = (short)128;
            this.changed = true;
        }
    }

    public static int create(int accountId) throws SQLException {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO `storages` (`accountid`, `slots`, `meso`) VALUES (?, ?, ?)", 1);){
            ps.setInt(1, accountId);
            ps.setInt(2, 4);
            ps.setInt(3, 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys();){
                if (rs.next()) {
                    int storageid = rs.getInt(1);
                    ps.close();
                    rs.close();
                    int n = storageid;
                    return n;
                }
            }
        }
        throw new SQLException("Inserting char failed.");
    }

    public static MapleTrunk loadOrCreateFromDB(int accountId) {
        MapleTrunk ret = null;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM storages WHERE accountid = ?");
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int storeId = rs.getInt("storageid");
                ret = new MapleTrunk(storeId, rs.getShort("slots"), rs.getLong("meso"), accountId);
                rs.close();
                ps.close();
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                for (Pair<Item, MapleInventoryType> mit : ItemLoader.倉庫道具.loadItems(false, accountId).values()) {
                    Item item = mit.getLeft();
                    if (item.getItemId() / 1000000 == 1 && ii.isDropRestricted(item.getItemId()) && !ItemAttribute.TradeOnce.check(item.getAttribute())) {
                        item.addAttribute((short)ItemAttribute.TradeOnce.getValue());
                    }
                    ret.items.add(item);
                }
            } else {
                int storeId = MapleTrunk.create(accountId);
                ret = new MapleTrunk(storeId,  (short)4, 0L, accountId);
                rs.close();
                ps.close();
            }
        }
        catch (SQLException ex) {
            log.error("Error loading storage. accId=" + accountId, ex);
        }
        return ret;
    }

    public void saveToDB() {
        this.saveToDB(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveToDB(Connection con) {
        if (!this.changed) {
            return;
        }
        boolean needcolse = false;
        try {
            if (con == null) {
                con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            }
            PreparedStatement ps = con.prepareStatement("UPDATE storages SET slots = ? WHERE storageid = ?");
            ps.setInt(1, this.slots);
            ps.setInt(2, this.storageId);
            ps.executeUpdate();
            ps.close();
            ArrayList<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<Pair<Item, MapleInventoryType>>();
            for (Item item : this.items) {
                itemsWithType.add(new Pair<Item, MapleInventoryType>(item, ItemConstants.getInventoryType(item.getItemId())));
            }
            ItemLoader.倉庫道具.saveItems(con, itemsWithType, this.accountId);
            this.changed = false;
        }
        catch (SQLException ex) {
            log.error("Error saving storage", ex);
        }
        finally {
            if (needcolse) {
                try {
                    con.close();
                }
                catch (SQLException e) {
                    log.error("Error saving storage", e);
                }
            }
        }
    }

    public Item getItem(short slot) {
        if (slot >= this.items.size() || slot < 0) {
            return null;
        }
        return this.items.get(slot);
    }

    public Item takeOut(short slot) {
        this.changed = true;
        Item ret = this.items.remove(slot);
        MapleInventoryType type = ItemConstants.getInventoryType(ret.getItemId());
        this.typeItems.put(type, new ArrayList<Item>(this.filterItems(type)));
        return ret;
    }

    public void store(Item item) {
        this.changed = true;
        this.items.add(item);
        MapleInventoryType type = ItemConstants.getInventoryType(item.getItemId());
        this.typeItems.put(type, new ArrayList<Item>(this.filterItems(type)));
    }

    public void arrange() {
        this.items.sort(Comparator.comparingInt(Item::getItemId));
        for (MapleInventoryType type : MapleInventoryType.values()) {
            this.typeItems.put(type, new ArrayList<Item>(this.items));
        }
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    private List<Item> filterItems(MapleInventoryType type) {
        LinkedList<Item> ret = new LinkedList<Item>();
        for (Item item : this.items) {
            if (ItemConstants.getInventoryType(item.getItemId()) != type) continue;
            ret.add(item);
        }
        return ret;
    }

    public short getSlot(MapleInventoryType type, short slot) {
        short ret = 0;
        List<Item> it = this.typeItems.get((Object)type);
        if (it == null || slot >= it.size() || slot < 0) {
            return -1;
        }
        for (Item item : this.items) {
            if (item == it.get(slot)) {
                return ret;
            }
            ret = (short)(ret + 1);
        }
        return -1;
    }

    public void secondPwdRequest(MapleClient c, int npcId) {
        c.announce(NPCPacket.getStoragePwd(npcId == -1));
        if (npcId != -1) {
            this.storageNpcId = npcId;
        }
    }

    public void sendStorage(MapleClient c) {
        this.items.sort((o1, o2) -> {
            if (ItemConstants.getInventoryType(o1.getItemId()).getType() < ItemConstants.getInventoryType(o2.getItemId()).getType()) {
                return -1;
            }
            if (ItemConstants.getInventoryType(o1.getItemId()) == ItemConstants.getInventoryType(o2.getItemId())) {
                return 0;
            }
            return 1;
        });
        for (MapleInventoryType type : MapleInventoryType.values()) {
            this.typeItems.put(type, new ArrayList<Item>(this.items));
        }
        c.announce(NPCPacket.getStorage(this.storageNpcId, this.slots, this.items, this.getMeso()));
    }

    public void update(MapleClient c) {
        c.announce(NPCPacket.arrangeStorage(this.slots, this.items, true));
    }

    public void sendStored(MapleClient c, MapleInventoryType type) {
        c.announce(NPCPacket.storeStorage(this.slots, type, (Collection<Item>)this.typeItems.get((Object)type)));
    }

    public void sendTakenOut(MapleClient c, MapleInventoryType type) {
        c.announce(NPCPacket.takeOutStorage(this.slots, type, (Collection<Item>)this.typeItems.get((Object)type)));
    }

    public long getMeso() {
        return SqlTool.queryAndGet("SELECT meso FROM storages WHERE accountid = ?", rs -> rs.getLong(1), this.accountId);
    }

    public long getMesoForUpdate(Connection con) {
        Long meso = SqlTool.queryAndGet(con, "SELECT meso FROM storages WHERE accountid = ? FOR UPDATE", rs -> rs.getLong(1), this.accountId);
        if (meso == null) {
            return 0L;
        }
        return meso;
    }

    public void setMeso(Connection con, long meso) {
        if (meso < 0L) {
            return;
        }
        this.changed = true;
        SqlTool.update(con, "UPDATE storages SET meso = ? WHERE accountid = ?", meso, this.accountId);
    }

    public Item findById(int itemId) {
        for (Item item : this.items) {
            if (item.getItemId() != itemId) continue;
            return item;
        }
        return null;
    }

    public void sendMeso(MapleClient c) {
        c.announce(NPCPacket.mesoStorage(this.slots, this.getMeso()));
    }

    public boolean isFull() {
        return this.items.size() >= this.slots;
    }

    public int getSlots() {
        return this.slots;
    }

    public void setSlots(short set) {
        this.changed = true;
        this.slots = set;
    }

    public void increaseSlots(byte gain) {
        this.changed = true;
        this.slots = (short)(this.slots + gain);
    }

    public int getNpcId() {
        return this.storageNpcId;
    }

    public void close() {
        this.pwdChecked = false;
        this.typeItems.clear();
    }

    public void setPwdChecked(boolean value) {
        this.pwdChecked = value;
    }

    public boolean isPwdChecked() {
        return this.pwdChecked;
    }
}

