/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Net.server.shop;

import Config.constants.ItemConstants;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.server.MapleItemInformationProvider;
import Net.server.shop.MapleShop;
import Net.server.shop.MapleShopItem;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleShopFactory {
    private static final Logger log = LoggerFactory.getLogger(MapleShopFactory.class);
    private static final Set<Integer> rechargeableItems = new LinkedHashSet<Integer>();
    private static final Set<Integer> blockedItems = new LinkedHashSet<Integer>();
    private static final MapleShopFactory instance = new MapleShopFactory();
    private final Map<Integer, MapleShop> shops = new TreeMap<Integer, MapleShop>();
    private final Map<Integer, Integer> shopIDs = new ConcurrentHashMap<Integer, Integer>();

    public void loadShopData() {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM shops");
             ResultSet rs = ps.executeQuery();){
            while (rs.next()) {
                this.shopIDs.put(rs.getInt("shopid"), rs.getInt("npcid"));
            }
        }
        catch (SQLException e) {
            log.error("加載NPC商店錯誤", e);
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (Map.Entry<Integer, Integer> entry : this.shopIDs.entrySet()) {
            MapleShop shop = new MapleShop(entry.getKey(), entry.getValue());
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM `shopitems` WHERE `shops_id` = ? ORDER BY `position` ASC");){
                ps.setInt(1, entry.getKey());
                try (ResultSet rs = ps.executeQuery();){
                    int pos = 0;
                    ArrayList<Integer> recharges = new ArrayList<Integer>();
                    for (int nItemID : rechargeableItems) {
                        if (!ii.itemExists(nItemID)) continue;
                        recharges.add(nItemID);
                    }
                    while (rs.next()) {
                        MapleShopItem item;
                        int id = rs.getInt("id");
                        int itemId = rs.getInt("nItemID");
                        if (!ii.itemExists(itemId) || blockedItems.contains(itemId)) continue;
                        if ((item = MapleShopItem.createFromSql(rs, pos++)).getBuyLimit() > 0 || item.getBuyLimitWorldAccount() > 0) {
                            int resetType = rs.getByte("resetType");
                            if (resetType < -7) {
                                resetType = 0;
                            } else if (resetType < 0 && resetType >= -7) {
                                int dayOfWeek = Math.abs(resetType);
                                dayOfWeek = dayOfWeek == 7 ? 1 : ++dayOfWeek;
                                resetType = 3;
                                LinkedList<Long> resetInfo = new LinkedList<Long>();
                                Calendar cal = Calendar.getInstance();
                                cal.add(5, -7);
                                cal.set(11, 0);
                                cal.set(12, 0);
                                cal.set(13, 0);
                                cal.set(14, 0);
                                for (int i = 0; i < 5; ++i) {
                                    if (i > 0) {
                                        cal.add(5, 7);
                                    }
                                    cal.set(7, dayOfWeek);
                                    resetInfo.add(cal.getTime().getTime());
                                }
                                item.setResetInfo(resetInfo);
                            } else if (resetType == 3) {
                                item.setResetInfo(SqlTool.queryAndGetList(con, "SELECT * FROM `shopitems_resetinfo` WHERE `shopitems_id` = ?", rs2 -> rs2.getLong("resettime"), id));
                            }
                            item.setResetType((byte)resetType);
                        }
                        shop.addItem(item);
                        if (!ItemConstants.類型.可充值道具(itemId) || !rechargeableItems.contains(item.getItemId())) continue;
                        recharges.remove((Object)item.getItemId());
                    }
                    for (Integer recharge : recharges) {
                        shop.addItem(new MapleShopItem(recharge, 0L, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2L, -1L, 0, 0, (short) 0));
                    }
                }
            }
            catch (SQLException e) {
                log.error("添加商店數據錯誤", e);
            }
            this.shops.put(entry.getKey(), shop);
        }
        log.trace("商店數據加載完成.");
    }

    public void clear() {
        this.shopIDs.clear();
        this.shops.clear();
        this.loadShopData();
    }

    public MapleShop getShop(int shopId) {
        return this.shops.get(shopId);
    }

    public void setShopData(String shopid, MapleShop shop) {
        if (shopid == null) {
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO shops VALUES (?, ?, ?)");){
                ps.setInt(1, shop.getId());
                ps.setInt(2, shop.getNpcId());
                ps.setString(3, "NPC");
                ps.execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                int shopId = Integer.parseInt(shopid);
                SqlTool.update(con, "DELETE FROM `shops` WHERE `shopid` = ?", shopId);
                SqlTool.update(con, "DELETE FROM `shopitems` WHERE `shops_id` = ?", shopId);
                SqlTool.update(con, "INSERT INTO `shops` VALUES (?, ?, ?)", shopId, shop.getNpcId(), "NPC");
                for (MapleShopItem item : shop.getItems()) {
                    SqlTool.update(con, "INSERT INTO `shopitems` (`nItemID`, `nPrice`, `nQuantity`, `position`, `nTokenItemID`, `nTokenPrice`, `nPointQuestID`, `nPointPrice`, `nItemPeriod`, `nPotentialGrade`, `nTabIndex`, `nLevelLimitedMin`, `nLevelLimitedMax`, `ftSellStart`, `ftSellEnd`, `nBuyLimit`, `nBuyLimitWorldAccount`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", item.getItemId(), item.getPrice(), item.getQuantity(), item.getPosition(), item.getTokenItemID(), item.getTokenPrice(), item.getPointQuestID(), item.getPointPrice(), item.getPeriod(), item.getPotentialGrade(), item.getCategory(), item.getMinLevel(), item.getMaxLevel(), item.getSellStart(), item.getSellEnd(), item.getBuyLimit(), item.getBuyLimitWorldAccount());
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public MapleShop getShopForNPC(int npcId) {
        if (!this.shopIDs.containsValue(npcId)) {
            return null;
        }
        int shopId = 0;
        for (Map.Entry<Integer, Integer> entry : this.shopIDs.entrySet()) {
            if (npcId != entry.getValue()) continue;
            shopId = entry.getKey();
        }
        return this.shops.get(shopId);
    }

    public Map<Integer, MapleShop> getAllShop() {
        return this.shops;
    }

    @Generated
    public static MapleShopFactory getInstance() {
        return instance;
    }

    static {
        rechargeableItems.add(2070000);
        rechargeableItems.add(2070001);
        rechargeableItems.add(2070002);
        rechargeableItems.add(2070003);
        rechargeableItems.add(2070004);
        rechargeableItems.add(2070005);
        rechargeableItems.add(2070006);
        rechargeableItems.add(2070007);
        rechargeableItems.add(2070008);
        rechargeableItems.add(2070009);
        rechargeableItems.add(2070010);
        rechargeableItems.add(2070011);
        rechargeableItems.add(2070012);
        rechargeableItems.add(2070013);
        rechargeableItems.add(2070015);
        rechargeableItems.add(2070016);
        rechargeableItems.add(2070019);
        rechargeableItems.add(2070020);
        rechargeableItems.add(2070021);
        rechargeableItems.add(2070022);
        rechargeableItems.add(2070023);
        rechargeableItems.add(2070024);
        rechargeableItems.add(2070026);
        rechargeableItems.add(2330000);
        rechargeableItems.add(2330001);
        rechargeableItems.add(2330002);
        rechargeableItems.add(2330003);
        rechargeableItems.add(2330004);
        rechargeableItems.add(2330005);
        rechargeableItems.add(2330006);
        rechargeableItems.add(2330007);
        rechargeableItems.add(2330008);
        rechargeableItems.add(2330016);
        blockedItems.add(4170023);
        blockedItems.add(4170024);
        blockedItems.add(4170025);
        blockedItems.add(4170028);
        blockedItems.add(4170029);
        blockedItems.add(4170031);
        blockedItems.add(4170032);
        blockedItems.add(4170033);
    }
}

