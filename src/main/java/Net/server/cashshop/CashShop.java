/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.inventory.MapleRing
 */
package Net.server.cashshop;

import Client.MapleClient;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.ItemLoader;
import Client.inventory.MapleInventoryIdentifier;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.inventory.MapleRing;
import Config.constants.ItemConstants;
import Database.DatabaseLoader;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.cashshop.CashItemFactory;
import Net.server.cashshop.CashItemInfo;
import Packet.MTSCSPacket;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tools.DateUtil;
import tools.Pair;

public class CashShop
implements Serializable {
    private static final long serialVersionUID = 231541893513373579L;
    private final ItemLoader factory = ItemLoader.現金道具;
    private final List<Item> inventory = new ArrayList<Item>();
    private final List<Integer> uniqueids = new ArrayList<Integer>();
    private int accountId;
    private int characterId;

    public CashShop() {
    }

    public CashShop(int accountId, int characterId, int jobType) throws SQLException {
        this.accountId = accountId;
        this.characterId = characterId;
        for (Pair<Item, MapleInventoryType> item : this.factory.loadItems(false, accountId).values()) {
            this.inventory.add(item.getLeft());
        }
    }

    public int getItemsSize() {
        return this.inventory.size();
    }

    public List<Item> getInventory() {
        return this.inventory;
    }

    public Item findByCashId(long cashId) {
        for (Item item : this.inventory) {
            if ((long)item.getSN() != cashId) continue;
            return item;
        }
        return null;
    }

    public void checkExpire(MapleClient c) {
        ArrayList<Item> toberemove = new ArrayList<Item>();
        for (Item item : this.inventory) {
            if (item == null || ItemConstants.類型.寵物(item.getItemId()) || item.getExpiration() <= 0L || item.getExpiration() >= System.currentTimeMillis()) continue;
            toberemove.add(item);
        }
        if (toberemove.size() > 0) {
            for (Item item : toberemove) {
                this.removeFromInventory(item);
                c.announce(MTSCSPacket.cashItemExpired(item.getSN()));
            }
            toberemove.clear();
        }
    }

    public Item toItem(CashItemInfo cItem) {
        return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getItemId(), -1), "");
    }

    public Item toItem(CashItemInfo cItem, String gift) {
        return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getItemId(), -1), gift);
    }

    public Item toItem(CashItemInfo cItem, int uniqueid) {
        return this.toItem(cItem, uniqueid, "");
    }

    public Item toItem(CashItemInfo cItem, int uniqueid, String gift) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        long period = cItem.getPeriod();
        Item ret = null;
        if (ItemConstants.getInventoryType(cItem.getItemId(), false) == MapleInventoryType.EQUIP) {
            MapleRing ring;
            Equip eq = MapleItemInformationProvider.getInstance().getEquipById(cItem.getItemId(), uniqueid);
            if (period > 0L) {
                eq.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            eq.setGMLog("商城購買 " + cItem.getSN() + " 時間 " + DateUtil.getCurrentDate());
            eq.setGiftFrom(gift);
            if (ItemConstants.類型.特效裝備(cItem.getItemId()) && uniqueid > 0 && (ring = MapleRing.loadFromDb((int)uniqueid)) != null) {
                eq.setRing(ring);
            }
            ret = eq.copy();
        } else {
            Item item = new Item(cItem.getItemId(), (short)0, (short)cItem.getCount(), 0, uniqueid, (short) 0);
            if (ItemConstants.類型.寵物(cItem.getItemId())) {
                period = ii.getLife(cItem.getItemId());
                MaplePet pet = MaplePet.createPet(cItem.getItemId(), uniqueid);
                if (pet != null) {
                    item.setPet(pet);
                    item.getPet().setInventoryPosition(item.getPosition());
                }
            }
            if (cItem.getItemId() == 5211047 || cItem.getItemId() == 5360014) {
                item.setExpiration(System.currentTimeMillis() + 10800000L);
            } else if (cItem.getItemId() == 5211060) {
                item.setExpiration(System.currentTimeMillis() + 0x6DDD00L);
            } else if (period > 0L) {
                item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            item.setGMLog("商城購買 " + cItem.getSN() + " 時間 " + DateUtil.getCurrentDate());
            item.setGiftFrom(gift);
            ret = item.copy();
        }
        return ret;
    }

    public void addToInventory(Item item) {
        this.inventory.add(item);
    }

    public void removeFromInventory(Item item) {
        this.inventory.remove(item);
    }

    public void gift(int recipient, String from, String message, int sn) {
        this.gift(recipient, from, message, sn, 0);
    }

    public void gift(int recipient, String from, String message, int sn, int uniqueid) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipient);
            ps.setString(2, from);
            ps.setString(3, message);
            ps.setInt(4, sn);
            ps.setInt(5, uniqueid);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public List<Pair<Item, String>> loadGifts() {
        ArrayList<Pair<Item, String>> gifts = new ArrayList<Pair<Item, String>>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CashItemInfo cItem = CashItemFactory.getInstance().getItem(rs.getInt("sn"));
                if (cItem == null) continue;
                Item item = this.toItem(cItem, rs.getInt("uniqueid"), rs.getString("from"));
                gifts.add(new Pair<Item, String>(item, rs.getString("message")));
                this.uniqueids.add(item.getSN());
                List<Integer> packages = CashItemFactory.getInstance().getPackageItems(cItem.getItemId());
                if (packages != null && packages.size() > 0) {
                    for (int packageItem : packages) {
                        CashItemInfo pack = CashItemFactory.getInstance().getSimpleItem(packageItem);
                        if (pack == null) continue;
                        this.addToInventory(this.toItem(pack, rs.getString("from")));
                    }
                    continue;
                }
                this.addToInventory(item);
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            ps.executeUpdate();
            ps.close();
            this.save(con);
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return gifts;
    }

    public boolean canSendNote(int uniqueid) {
        return this.uniqueids.contains(uniqueid);
    }

    public void sendedNote(long uniqueid) {
        this.uniqueids.removeIf(aLong -> (long)aLong.intValue() == uniqueid);
    }

    public void save(Connection con) throws SQLException {
        ArrayList<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<Pair<Item, MapleInventoryType>>();
        for (Item item : this.inventory) {
            itemsWithType.add(new Pair<Item, MapleInventoryType>(item, ItemConstants.getInventoryType(item.getItemId())));
        }
        this.factory.saveItems(con, itemsWithType, this.accountId);
    }
}

