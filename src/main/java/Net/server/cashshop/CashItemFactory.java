/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.cashshop.CashItemCategory
 *  Net.server.cashshop.CashItemForSql
 */
package Net.server.cashshop;

import Database.DatabaseLoader;
import Net.server.cashshop.CashItemCategory;
import Net.server.cashshop.CashItemForSql;
import Net.server.cashshop.CashItemInfo;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import Plugin.provider.loaders.CashData;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;

public class CashItemFactory {
    private static final Logger log = LoggerFactory.getLogger(CashItemFactory.class);
    private static final int[] bestItems = new int[]{30200045, 50000080, 30200066, 50400016, 30100092};
    private static final CashItemFactory instance = new CashItemFactory();
    private final List<CashItemCategory> categories = new LinkedList<CashItemCategory>();
    private final Map<Integer, CashItemInfo.CashModInfo> itemMods = new HashMap<Integer, CashItemInfo.CashModInfo>();
    private final Map<Integer, CashItemForSql> menuItems = new HashMap<Integer, CashItemForSql>();
    private final Map<Integer, CashItemForSql> categoryItems = new HashMap<Integer, CashItemForSql>();
    private Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();
    private Map<Integer, Integer> idLookup = new HashMap<Integer, Integer>();
    private final Map<Integer, CashItemInfo> oldItemStats = new HashMap<Integer, CashItemInfo>();
    private final Map<Integer, Integer> oldIdLookup = new HashMap<Integer, Integer>();
    private Map<Integer, List<Integer>> itemPackage = new HashMap<Integer, List<Integer>>();
    private final Map<Integer, List<Pair<Integer, Integer>>> openBox = new HashMap<Integer, List<Pair<Integer, Integer>>>();
    private final MapleDataProvider data = MapleDataProviderFactory.getEtc();
    private final MapleData commodities = this.data.getData("Commodity.img");
    private List<Integer> blockRefundableItemId = new LinkedList<Integer>();
    private final Map<Integer, Byte> baseNewItems = new HashMap<Integer, Byte>();

    public static CashItemFactory getInstance() {
        return instance;
    }

    public void initialize() {
        this.blockRefundableItemId.clear();
        int onSaleSize = 0;
        HashMap<Integer, Integer> fixId = new HashMap<Integer, Integer>();
        for (MapleData mapleData : this.commodities.getChildren()) {
            int SN = MapleDataTool.getIntConvert("SN", mapleData, 0);
            int n = MapleDataTool.getIntConvert("ItemId", mapleData, 0);
            int count = MapleDataTool.getIntConvert("Count", mapleData, 1);
            int price = MapleDataTool.getIntConvert("Price", mapleData, 0);
            int meso = MapleDataTool.getIntConvert("Meso", mapleData, 0);
            int originalPrice = MapleDataTool.getIntConvert("originalPrice", mapleData, 0);
            int period = MapleDataTool.getIntConvert("Period", mapleData, 0);
            int gender = MapleDataTool.getIntConvert("Gender", mapleData, 2);
            byte csClass = (byte)MapleDataTool.getIntConvert("Class", mapleData, 0);
            byte priority = (byte)MapleDataTool.getIntConvert("Priority", mapleData, 0);
            int termStart = MapleDataTool.getIntConvert("termStart", mapleData, 0);
            int termEnd = MapleDataTool.getIntConvert("termEnd", mapleData, 0);
            boolean onSale = MapleDataTool.getIntConvert("OnSale", mapleData, 0) > 0 || this.isOnSalePackage(SN);
            boolean bonus = MapleDataTool.getIntConvert("Bonus", mapleData, 0) > 0;
            boolean refundable = MapleDataTool.getIntConvert("Refundable", mapleData, 0) == 0;
            boolean discount = MapleDataTool.getIntConvert("discount", mapleData, 0) > 0;
            int mileageRate = MapleDataTool.getIntConvert("mileageRate", mapleData, 0);
            boolean onlyMileage = MapleDataTool.getIntConvert("onlyMileage", mapleData, 0) >= 0;
            int LimitMax = MapleDataTool.getIntConvert("LimitMax", mapleData, 0);
            if (onSale) {
                ++onSaleSize;
            }
            CashItemInfo stats = new CashItemInfo(mapleData.getName(), n, count, price, originalPrice, meso, SN, period, gender, csClass, priority, termStart, termEnd, onSale, bonus, refundable, discount, mileageRate, onlyMileage, LimitMax);
            if (SN <= 0) continue;
            this.itemStats.put(SN, stats);
            if (this.idLookup.containsKey(n)) {
                fixId.put(SN, n);
                this.blockRefundableItemId.add(n);
            }
            this.idLookup.put(n, SN);
        }
        MapleData packageData = this.data.getData("CashPackage.img");
        for (MapleData root : packageData.getChildren()) {
            if (root.getChildByPath("SN") == null) continue;
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (MapleData dat : root.getChildByPath("SN").getChildren()) {
                arrayList.add(MapleDataTool.getIntConvert(dat));
            }
            this.itemPackage.put(Integer.parseInt(root.getName()), arrayList);
        }
        onSaleSize = 0;
        MapleDataDirectoryEntry mapleDataDirectoryEntry = this.data.getRoot();
        for (MapleDataEntry mapleDataEntry : mapleDataDirectoryEntry.getFiles()) {
            if (!mapleDataEntry.getName().startsWith("OldCommodity")) continue;
            MapleData Commodity = this.data.getData(mapleDataEntry.getName());
            for (MapleData field : Commodity.getChildren()) {
                int SN = MapleDataTool.getIntConvert("SN", field, 0);
                int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
                int count = MapleDataTool.getIntConvert("Count", field, 1);
                int price = MapleDataTool.getIntConvert("Price", field, 0);
                int meso = MapleDataTool.getIntConvert("Meso", field, 0);
                int originalPrice = MapleDataTool.getIntConvert("originalPrice", field, 0);
                int period = MapleDataTool.getIntConvert("Period", field, 0);
                int gender = MapleDataTool.getIntConvert("Gender", field, 2);
                byte csClass = (byte)MapleDataTool.getIntConvert("Class", field, 0);
                byte priority = (byte)MapleDataTool.getIntConvert("Priority", field, 0);
                int termStart = MapleDataTool.getIntConvert("termStart", field, 0);
                int termEnd = MapleDataTool.getIntConvert("termEnd", field, 0);
                boolean onSale = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 || this.isOnSalePackage(SN);
                boolean bonus = MapleDataTool.getIntConvert("Bonus", field, 0) >= 0;
                boolean refundable = MapleDataTool.getIntConvert("Refundable", field, 0) == 0;
                boolean discount = MapleDataTool.getIntConvert("discount", field, 0) >= 0;
                int mileageRate = MapleDataTool.getIntConvert("mileageRate", field, 0);
                boolean onlyMileage = MapleDataTool.getIntConvert("onlyMileage", field, 0) >= 0;
                int LimitMax = MapleDataTool.getIntConvert("LimitMax", field, 0);
                if (onSale) {
                    ++onSaleSize;
                }
                CashItemInfo stats = new CashItemInfo(field.getName(), itemId, count, price, originalPrice, meso, SN, period, gender, csClass, priority, termStart, termEnd, onSale, bonus, refundable, discount, mileageRate, onlyMileage, LimitMax);
                if (SN <= 0) continue;
                this.oldItemStats.put(SN, stats);
                this.oldIdLookup.put(itemId, SN);
            }
        }
        CashData.loadCashCommodities();
        this.itemStats = CashData.getItemStats();
        this.idLookup = CashData.getIdLookup();
        this.blockRefundableItemId = CashData.getBlockRefundableItemId();
        CashData.loadCashPackages();
        this.itemPackage = CashData.getItemPackage();
        CashData.loadCashOldCommodities();
        this.loadMoifiedItemInfo();
        this.loadRandomItemInfo();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            try (PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM cashshop_categories");
                 ResultSet rs = preparedStatement.executeQuery();){
                while (rs.next()) {
                    CashItemCategory cat = new CashItemCategory(rs.getInt("categoryid"), rs.getString("name"), rs.getInt("parent"), rs.getInt("flag"), rs.getInt("sold"));
                    this.categories.add(cat);
                }
            }
            catch (SQLException sQLException) {
                log.error("Failed to load cash shop categories. ", sQLException);
            }
            try (PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM cashshop_menuitems");
                 ResultSet rs = preparedStatement.executeQuery();){
                while (rs.next()) {
                    CashItemForSql item = new CashItemForSql(rs.getInt("category"), rs.getInt("subcategory"), rs.getInt("parent"), rs.getString("image"), rs.getInt("sn"), rs.getInt("itemid"), rs.getInt("flag"), rs.getInt("price"), rs.getInt("discountPrice"), rs.getInt("quantity"), rs.getInt("expire"), rs.getInt("gender"), rs.getInt("likes"));
                    this.menuItems.put(item.getSN(), item);
                }
            }
            catch (SQLException sQLException) {
                log.error("Failed to load cash shop menuitems. ", sQLException);
            }
            try (PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM cashshop_items");
                 ResultSet rs = preparedStatement.executeQuery();){
                while (rs.next()) {
                    CashItemForSql item = new CashItemForSql(rs.getInt("category"), rs.getInt("subcategory"), rs.getInt("parent"), rs.getString("image"), rs.getInt("sn"), rs.getInt("itemid"), rs.getInt("flag"), rs.getInt("price"), rs.getInt("discountPrice"), rs.getInt("quantity"), rs.getInt("expire"), rs.getInt("gender"), rs.getInt("likes"));
                    this.categoryItems.put(item.getSN(), item);
                }
            }
        }
        catch (SQLException e) {
            log.error("Failed to load cash shop items. ", e);
        }
    }

    public boolean isOnSalePackage(int snId) {
        return snId >= 170200002 && snId <= 170200013;
    }

    public void loadMoifiedItemInfo() {
        this.itemMods.clear();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
             ResultSet rs = ps.executeQuery();){
            while (rs.next()) {
                CashItemInfo.CashModInfo ret = new CashItemInfo.CashModInfo(rs.getInt("serial"), rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("csClass"), rs.getInt("termStart"), rs.getInt("termEnd"), rs.getInt("fameLimit"), rs.getInt("levelLimit"), rs.getInt("categories"), rs.getByte("bast_new") > 0);
                this.itemMods.put(ret.getSn(), ret);
                CashItemInfo cc = this.itemStats.get(ret.getSn());
                if (cc == null) continue;
                ret.toCItem(cc);
                if (!ret.isBase_new()) continue;
                this.baseNewItems.put(ret.getSn(), cc.getCsClass());
            }
        }
        catch (SQLException e) {
            log.error("cashshop_modified_items_error: ", e);
        }
    }

    public void loadRandomItemInfo() {
        this.openBox.clear();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_randombox");
             ResultSet rs = ps.executeQuery();){
            while (rs.next()) {
                List boxItems = this.openBox.computeIfAbsent(rs.getInt("randboxID"), integer -> new ArrayList());
                boxItems.add(new Pair<Integer, Integer>(rs.getInt("itemSN"), rs.getInt("count")));
            }
        }
        catch (SQLException e) {
            log.error("加載商城隨機箱子的訊息出錯.", e);
        }
    }

    public CashItemInfo getSimpleItem(int sn) {
        return this.itemStats.get(sn);
    }

    public Map<Integer, CashItemInfo> getAllItem() {
        return this.itemStats;
    }

    public boolean isBlockRefundableItemId(int itemId) {
        return this.blockRefundableItemId.contains(itemId);
    }

    public CashItemInfo.CashModInfo getModInfo(int sn) {
        return this.itemMods.get(sn);
    }

    public Map<Integer, CashItemInfo.CashModInfo> getAllModInfo() {
        return this.itemMods;
    }

    public Map<Integer, Byte> getAllBaseNewInfo() {
        return this.baseNewItems;
    }

    public CashItemInfo getItem(int sn) {
        return this.getItem(sn, true);
    }

    public CashItemInfo getItem(int sn, boolean checkSale) {
        CashItemInfo stats = this.itemStats.get(sn);
        CashItemInfo.CashModInfo z = this.getModInfo(sn);
        if (z != null && z.isShowUp()) {
            return z.toCItem(stats);
        }
        if (stats == null) {
            return null;
        }
        return checkSale && !stats.onSale() ? null : stats;
    }

    public CashItemForSql getMenuItem(int sn) {
        for (CashItemForSql ci : this.getMenuItems()) {
            if (ci.getSN() != sn) continue;
            return ci;
        }
        return null;
    }

    public CashItemForSql getAllItem(int sn) {
        for (CashItemForSql ci : this.getAllItems()) {
            if (ci.getSN() != sn) continue;
            return ci;
        }
        return null;
    }

    public List<Integer> getPackageItems(int itemId) {
        return this.itemPackage.get(itemId);
    }

    public Map<Integer, List<Pair<Integer, Integer>>> getRandomItemInfo() {
        return this.openBox;
    }

    public boolean hasRandomItem(int itemId) {
        return this.openBox.containsKey(itemId);
    }

    public List<Pair<Integer, Integer>> getRandomItem(int itemId) {
        return this.openBox.get(itemId);
    }

    public int[] getBestItems() {
        return bestItems;
    }

    public int getLinkItemId(int itemId) {
        switch (itemId) {
            case 5000029: 
            case 5000030: 
            case 5000032: 
            case 5000033: 
            case 5000035: {
                return 5000028;
            }
            case 5000048: 
            case 5000049: 
            case 5000050: 
            case 5000051: 
            case 5000052: {
                return 5000047;
            }
        }
        return itemId;
    }

    public int getSnFromId(int itemId) {
        if (this.idLookup.containsKey(itemId)) {
            return this.idLookup.get(itemId);
        }
        return 0;
    }

    public List<CashItemCategory> getCategories() {
        return this.categories;
    }

    public List<CashItemForSql> getMenuItems(int type) {
        LinkedList<CashItemForSql> items = new LinkedList<CashItemForSql>();
        for (CashItemForSql ci : this.menuItems.values()) {
            if (ci.getSubCategory() / 10000 != type) continue;
            items.add(ci);
        }
        return items;
    }

    public List<CashItemForSql> getMenuItems() {
        LinkedList<CashItemForSql> items = new LinkedList<CashItemForSql>();
        for (CashItemForSql ci : this.menuItems.values()) {
            items.add(ci);
        }
        return items;
    }

    public List<CashItemForSql> getAllItems(int type) {
        LinkedList<CashItemForSql> items = new LinkedList<CashItemForSql>();
        for (CashItemForSql ci : this.categoryItems.values()) {
            if (ci.getSubCategory() / 10000 != type) continue;
            items.add(ci);
        }
        return items;
    }

    public List<CashItemForSql> getAllItems() {
        LinkedList<CashItemForSql> items = new LinkedList<CashItemForSql>();
        for (CashItemForSql ci : this.categoryItems.values()) {
            items.add(ci);
        }
        return items;
    }

    public List<CashItemForSql> getCategoryItems(int subcategory) {
        LinkedList<CashItemForSql> items = new LinkedList<CashItemForSql>();
        for (CashItemForSql ci : this.categoryItems.values()) {
            if (ci.getSubCategory() != subcategory) continue;
            items.add(ci);
        }
        return items;
    }
}

