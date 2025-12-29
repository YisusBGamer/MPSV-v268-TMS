/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Plugin.provider.loaders;

import Config.configs.Config;
import Config.constants.ServerConstants;
import Net.server.cashshop.CashItemInfo;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import SwordieX.util.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CashData {
    private static final Logger log = LoggerFactory.getLogger(CashData.class);
    private static MapleDataProvider data;
    private static MapleData commodities;
    private static Map<Integer, CashItemInfo> itemStats;
    private static Map<Integer, Integer> idLookup;
    private static Map<Integer, CashItemInfo> oldItemStats;
    private static Map<Integer, Integer> oldIdLookup;
    private static Map<Integer, List<Integer>> itemPackage;
    private static List<Integer> blockRefundableItemId;

    public static boolean isOnSalePackage(int snId) {
        return snId >= 170200002 && snId <= 170200013;
    }

    public static void saveCashCommodities(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(String.format("%s/CashCommodities.dat", dir));
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return;
        }
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(itemStats.size());
            for (Map.Entry<Integer, CashItemInfo> entry : itemStats.entrySet()) {
                dataOutputStream.writeInt(entry.getKey());
                dataOutputStream.writeUTF(entry.getValue().getName());
                dataOutputStream.writeInt(entry.getValue().getItemId());
                dataOutputStream.writeInt(entry.getValue().getCount());
                dataOutputStream.writeInt(entry.getValue().getPrice());
                dataOutputStream.writeInt(entry.getValue().getMeso());
                dataOutputStream.writeInt(entry.getValue().getOriginalPrice());
                dataOutputStream.writeInt(entry.getValue().getPeriod());
                dataOutputStream.writeInt(entry.getValue().getGender());
                dataOutputStream.writeInt(entry.getValue().getTermStart());
                dataOutputStream.writeInt(entry.getValue().getTermEnd());
                dataOutputStream.writeInt(entry.getValue().getMileageRate());
                dataOutputStream.writeInt(entry.getValue().getLimitMax());
                dataOutputStream.write(entry.getValue().getCsClass());
                dataOutputStream.write(entry.getValue().getPriority());
                dataOutputStream.writeBoolean(entry.getValue().onSale());
                dataOutputStream.writeBoolean(entry.getValue().isBonus());
                dataOutputStream.writeBoolean(entry.getValue().isRefundable());
                dataOutputStream.writeBoolean(entry.getValue().isDiscount());
                dataOutputStream.writeBoolean(entry.getValue().isOnlyMileage());
            }
            dataOutputStream.writeInt(idLookup.size());
            for (Map.Entry<Integer, Integer> il : idLookup.entrySet()) {
                dataOutputStream.writeInt(il.getKey());
                dataOutputStream.writeInt(il.getValue());
            }
            dataOutputStream.writeInt(blockRefundableItemId.size());
            for (Integer n : blockRefundableItemId) {
                dataOutputStream.writeInt(n);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCashCommodities(File file) {
        if (!file.exists()) {
            CashData.loadDatFromWz();
            return;
        }
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int i;
            int maxcount = dataInputStream.readInt();
            for (i = 0; i < maxcount; ++i) {
                int SN = dataInputStream.readInt();
                String wzName = dataInputStream.readUTF();
                int itemId = dataInputStream.readInt();
                int count = dataInputStream.readInt();
                int price = dataInputStream.readInt();
                int meso = dataInputStream.readInt();
                int originalPrice = dataInputStream.readInt();
                int period = dataInputStream.readInt();
                int gender = dataInputStream.readInt();
                int termStart = dataInputStream.readInt();
                int termEnd = dataInputStream.readInt();
                int mileageRate = dataInputStream.readInt();
                int LimitMax = dataInputStream.readInt();
                byte csClass = dataInputStream.readByte();
                byte priority = dataInputStream.readByte();
                boolean onSale = dataInputStream.readBoolean();
                boolean bonus = dataInputStream.readBoolean();
                boolean refundable = dataInputStream.readBoolean();
                boolean discount = dataInputStream.readBoolean();
                boolean onlyMileage = dataInputStream.readBoolean();
                CashItemInfo cii = new CashItemInfo(wzName, itemId, count, price, originalPrice, meso, SN, period, gender, csClass, priority, termStart, termEnd, onSale, bonus, refundable, discount, mileageRate, onlyMileage, LimitMax);
                itemStats.put(SN, cii);
            }
            maxcount = dataInputStream.readInt();
            for (i = 0; i < maxcount; ++i) {
                idLookup.put(dataInputStream.readInt(), dataInputStream.readInt());
            }
            maxcount = dataInputStream.readInt();
            for (i = 0; i < maxcount; ++i) {
                blockRefundableItemId.add(dataInputStream.readInt());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCashCommodities() {
        long start = System.currentTimeMillis();
        String dir = ServerConstants.DAT_DIR + "/cash/CashCommodities.dat";
        File f = new File(dir);
        CashData.loadCashCommodities(f);
    }

    private static void loadCashCommoditiesFromWz() {
        blockRefundableItemId.clear();
        HashMap<Integer, Integer> fixId = new HashMap<Integer, Integer>();
        for (MapleData field : commodities.getChildren()) {
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
            boolean onSale = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 || CashData.isOnSalePackage(SN);
            boolean bonus = MapleDataTool.getIntConvert("Bonus", field, 0) > 0;
            boolean refundable = MapleDataTool.getIntConvert("Refundable", field, 0) == 0;
            boolean discount = MapleDataTool.getIntConvert("discount", field, 0) > 0;
            int mileageRate = MapleDataTool.getIntConvert("mileageRate", field, 0);
            boolean onlyMileage = MapleDataTool.getIntConvert("onlyMileage", field, 0) >= 0;
            int LimitMax = MapleDataTool.getIntConvert("LimitMax", field, 0);
            if (onSale) {
                // empty if block
            }
            CashItemInfo stats = new CashItemInfo(field.getName(), itemId, count, price, originalPrice, meso, SN, period, gender, csClass, priority, termStart, termEnd, onSale, bonus, refundable, discount, mileageRate, onlyMileage, LimitMax);
            if (SN <= 0) continue;
            itemStats.put(SN, stats);
            if (idLookup.containsKey(itemId)) {
                fixId.put(SN, itemId);
                blockRefundableItemId.add(itemId);
            }
            idLookup.put(itemId, SN);
        }
    }

    public static void saveCashPackages(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(String.format("%s/CashPackages.dat", dir));
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(itemPackage.size());
            for (Map.Entry<Integer, List<Integer>> ip : itemPackage.entrySet()) {
                dataOutputStream.writeInt(ip.getKey());
                dataOutputStream.writeInt(ip.getValue().size());
                for (Integer dat : ip.getValue()) {
                    dataOutputStream.writeInt(dat);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCashPackages(File file) {
        if (!file.exists()) {
            CashData.loadDatFromWz();
            return;
        }
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int maxcount = dataInputStream.readInt();
            for (int i = 0; i < maxcount; ++i) {
                int key = dataInputStream.readInt();
                int itemCount = dataInputStream.readInt();
                ArrayList<Integer> packageItems = new ArrayList<Integer>();
                for (int j = 0; j < itemCount; ++j) {
                    packageItems.add(dataInputStream.readInt());
                }
                itemPackage.put(key, packageItems);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCashPackages() {
        long start = System.currentTimeMillis();
        String dir = ServerConstants.DAT_DIR + "/cash/CashPackages.dat";
        File f = new File(dir);
        CashData.loadCashPackages(f);
    }

    private static void loadCashPackagesFromWz() {
        MapleData packageData = data.getData("CashPackage.img");
        for (MapleData root : packageData.getChildren()) {
            if (root.getChildByPath("SN") == null) continue;
            ArrayList<Integer> packageItems = new ArrayList<Integer>();
            for (MapleData dat : root.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(dat));
            }
            itemPackage.put(Integer.parseInt(root.getName()), packageItems);
        }
    }

    public static void saveCashOldCommodities(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(String.format("%s/CashOldCommodities.dat", dir));
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(oldItemStats.size());
            for (Map.Entry<Integer, CashItemInfo> entry : oldItemStats.entrySet()) {
                dataOutputStream.writeInt(entry.getKey());
                dataOutputStream.writeUTF(entry.getValue().getName());
                dataOutputStream.writeInt(entry.getValue().getItemId());
                dataOutputStream.writeInt(entry.getValue().getCount());
                dataOutputStream.writeInt(entry.getValue().getPrice());
                dataOutputStream.writeInt(entry.getValue().getMeso());
                dataOutputStream.writeInt(entry.getValue().getOriginalPrice());
                dataOutputStream.writeInt(entry.getValue().getPeriod());
                dataOutputStream.writeInt(entry.getValue().getGender());
                dataOutputStream.writeInt(entry.getValue().getTermStart());
                dataOutputStream.writeInt(entry.getValue().getTermEnd());
                dataOutputStream.writeInt(entry.getValue().getMileageRate());
                dataOutputStream.writeInt(entry.getValue().getLimitMax());
                dataOutputStream.write(entry.getValue().getCsClass());
                dataOutputStream.write(entry.getValue().getPriority());
                dataOutputStream.writeBoolean(entry.getValue().onSale());
                dataOutputStream.writeBoolean(entry.getValue().isBonus());
                dataOutputStream.writeBoolean(entry.getValue().isRefundable());
                dataOutputStream.writeBoolean(entry.getValue().isDiscount());
                dataOutputStream.writeBoolean(entry.getValue().isOnlyMileage());
            }
            dataOutputStream.writeInt(oldIdLookup.size());
            for (Map.Entry<Integer, Integer> entry : oldIdLookup.entrySet()) {
                dataOutputStream.writeInt(entry.getKey());
                dataOutputStream.writeInt((Integer)entry.getValue());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCashOldCommodities(File file) {
        if (!file.exists()) {
            CashData.loadDatFromWz();
            return;
        }
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int i;
            int maxcount = dataInputStream.readInt();
            for (i = 0; i < maxcount; ++i) {
                int SN = dataInputStream.readInt();
                String wzName = dataInputStream.readUTF();
                int itemId = dataInputStream.readInt();
                int count = dataInputStream.readInt();
                int price = dataInputStream.readInt();
                int meso = dataInputStream.readInt();
                int originalPrice = dataInputStream.readInt();
                int period = dataInputStream.readInt();
                int gender = dataInputStream.readInt();
                int termStart = dataInputStream.readInt();
                int termEnd = dataInputStream.readInt();
                int mileageRate = dataInputStream.readInt();
                int LimitMax = dataInputStream.readInt();
                byte csClass = dataInputStream.readByte();
                byte priority = dataInputStream.readByte();
                boolean onSale = dataInputStream.readBoolean();
                boolean bonus = dataInputStream.readBoolean();
                boolean refundable = dataInputStream.readBoolean();
                boolean discount = dataInputStream.readBoolean();
                boolean onlyMileage = dataInputStream.readBoolean();
                CashItemInfo cii = new CashItemInfo(wzName, itemId, count, price, originalPrice, meso, SN, period, gender, csClass, priority, termStart, termEnd, onSale, bonus, refundable, discount, mileageRate, onlyMileage, LimitMax);
                oldItemStats.put(SN, cii);
            }
            maxcount = dataInputStream.readInt();
            for (i = 0; i < maxcount; ++i) {
                oldIdLookup.put(dataInputStream.readInt(), dataInputStream.readInt());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCashOldCommodities() {
        long start = System.currentTimeMillis();
        String dir = ServerConstants.DAT_DIR + "/cash/CashOldCommodities.dat";
        File f = new File(dir);
        CashData.loadCashOldCommodities(f);
    }

    private static void loadCashOldCommoditiesFromWz() {
        MapleDataDirectoryEntry root = data.getRoot();
        for (MapleDataEntry mapleDataEntry : root.getFiles()) {
            if (!mapleDataEntry.getName().startsWith("OldCommodity")) continue;
            MapleData Commodity = data.getData(mapleDataEntry.getName());
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
                boolean onSale = MapleDataTool.getIntConvert("OnSale", field, 0) > 0 || CashData.isOnSalePackage(SN);
                boolean bonus = MapleDataTool.getIntConvert("Bonus", field, 0) >= 0;
                boolean refundable = MapleDataTool.getIntConvert("Refundable", field, 0) == 0;
                boolean discount = MapleDataTool.getIntConvert("discount", field, 0) >= 0;
                int mileageRate = MapleDataTool.getIntConvert("mileageRate", field, 0);
                boolean onlyMileage = MapleDataTool.getIntConvert("onlyMileage", field, 0) >= 0;
                int LimitMax = MapleDataTool.getIntConvert("LimitMax", field, 0);
                CashItemInfo stats = new CashItemInfo(field.getName(), itemId, count, price, originalPrice, meso, SN, period, gender, csClass, priority, termStart, termEnd, onSale, bonus, refundable, discount, mileageRate, onlyMileage, LimitMax);
                if (SN <= 0) continue;
                oldItemStats.put(SN, stats);
                oldIdLookup.put(itemId, SN);
            }
        }
    }

    public static void generateDatFiles() {
        System.out.println("Started generating Cash Commodities data.");
        long start = System.currentTimeMillis();
        CashData.loadCashCommoditiesFromWz();
        CashData.saveCashCommodities(ServerConstants.DAT_DIR + "/cash");
        System.out.println(String.format("Completed generating Cash Commodities data in %dms.", System.currentTimeMillis() - start));
        System.out.println("Started generating Cash Packages data.");
        start = System.currentTimeMillis();
        CashData.loadCashPackagesFromWz();
        CashData.saveCashPackages(ServerConstants.DAT_DIR + "/cash");
        System.out.println(String.format("Completed generating Cash Packages data in %dms.", System.currentTimeMillis() - start));
        System.out.println("Started generating Cash Old Commodity data.");
        start = System.currentTimeMillis();
        CashData.loadCashOldCommoditiesFromWz();
        CashData.saveCashOldCommodities(ServerConstants.DAT_DIR + "/cash");
        System.out.println(String.format("Completed generating Cash Old Commodity data in %dms.", System.currentTimeMillis() - start));
        System.out.println("Started loading Cash common data.");
        start = System.currentTimeMillis();
        CashData.clear();
        System.out.println(String.format("Completed loaded Cash common data in %dms.", System.currentTimeMillis() - start));
    }

    public static void main(String[] args) {
        Config.load();
        MapleDataProviderFactory.init();
        CashData.loadDatFromWz();
    }

    public static void loadDatFromWz() {
        data = MapleDataProviderFactory.getEtc();
        commodities = data.getData("Commodity.img");
        CashData.generateDatFiles();
    }

    public static void clear() {
        CashData.loadCashCommodities();
        CashData.loadCashPackages();
        CashData.loadCashOldCommodities();
    }

    @Generated
    public static Map<Integer, CashItemInfo> getItemStats() {
        return itemStats;
    }

    @Generated
    public static Map<Integer, Integer> getIdLookup() {
        return idLookup;
    }

    @Generated
    public static Map<Integer, CashItemInfo> getOldItemStats() {
        return oldItemStats;
    }

    @Generated
    public static Map<Integer, Integer> getOldIdLookup() {
        return oldIdLookup;
    }

    @Generated
    public static Map<Integer, List<Integer>> getItemPackage() {
        return itemPackage;
    }

    @Generated
    public static List<Integer> getBlockRefundableItemId() {
        return blockRefundableItemId;
    }

    static {
        itemStats = new HashMap<Integer, CashItemInfo>();
        idLookup = new HashMap<Integer, Integer>();
        oldItemStats = new HashMap<Integer, CashItemInfo>();
        oldIdLookup = new HashMap<Integer, Integer>();
        itemPackage = new HashMap<Integer, List<Integer>>();
        blockRefundableItemId = new LinkedList<Integer>();
    }
}

