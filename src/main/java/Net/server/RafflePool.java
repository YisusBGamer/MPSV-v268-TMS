/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.LotteryRandom
 */
package Net.server;

import Config.constants.ItemConstants;
import Database.DatabaseLoader;
import Net.server.MapleItemInformationProvider;
import Net.server.RaffleItem;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.DateUtil;
import tools.LotteryRandom;
import tools.Pair;

public class RafflePool {
    private static final List<Integer> allTypes = new LinkedList<Integer>();
    private static final List<RaffleItem> allItems = new LinkedList<RaffleItem>();
    private static final Map<Integer, List<RaffleItem>> items = new LinkedHashMap<Integer, List<RaffleItem>>();
    private static final Map<Integer, Integer> dates = new LinkedHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> periods = new LinkedHashMap<Integer, Integer>();
    private static final Map<Integer, List<Integer>> allPeriods = new LinkedHashMap<Integer, List<Integer>>();
    private static final Map<Integer, Integer> durations = new LinkedHashMap<Integer, Integer>();
    private static final Map<Integer, Map<Integer, Integer>> gainlogs = new LinkedHashMap<Integer, Map<Integer, Integer>>();
    private static final int ALL_REWARD = 0;
    private static final int MAIN_REWARD = 1;
    private static final int MINOR_REWARD = 2;

    public static void checkPool() {
        RafflePool.loadAllTypes();
        for (int type : allTypes) {
            long checkTime;
            long startTime;
            if (!dates.containsKey(type) || !periods.containsKey(type) || !durations.containsKey(type) || (startTime = DateUtil.getStringToTime(String.valueOf(dates.get(type)) + "0000")) > (checkTime = DateUtil.getStringToTime(DateUtil.getPreDate("d", -durations.get(type).intValue()).replace("-", "") + "0000"))) continue;
            allPeriods.remove(type);
            RafflePool.loadAllPeriods();
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                int nextPeriods = periods.get(type);
                int i = 0;
                while ((double)i <= Math.floor((checkTime - startTime) / (long)(durations.get(type) * 86400000))) {
                    if (allPeriods.containsKey(type)) {
                        for (int p : allPeriods.get(type)) {
                            if (p <= nextPeriods) continue;
                            nextPeriods = p;
                            break;
                        }
                        if (nextPeriods == periods.get(type)) {
                            nextPeriods = 1;
                        }
                    } else {
                        nextPeriods = 1;
                        break;
                    }
                    periods.put(type, nextPeriods);
                    ++i;
                }
                try (PreparedStatement ps = con.prepareStatement("UPDATE raffle_period SET period = ?, start_date = ? WHERE type = ?");){
                    ps.setInt(1, nextPeriods);
                    ps.setInt(2, DateUtil.getTime() / 100);
                    ps.setInt(3, type);
                    ps.execute();
                }
                PreparedStatement ps = con.prepareStatement("DELETE FROM raffle_log WHERE type = ?");
                try {
                    ps.setInt(1, type);
                    ps.execute();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
            allItems.clear();
            dates.remove(type);
            periods.remove(type);
            durations.remove(type);
            items.remove(type);
            gainlogs.remove(type);
            RafflePool.loadAllItems();
            RafflePool.loadPeriods();
            RafflePool.loadItems();
            RafflePool.loadLogs();
        }
    }

    public static void reload() {
        allTypes.clear();
        RafflePool.loadAllTypes();
        allItems.clear();
        RafflePool.loadAllItems();
        allPeriods.clear();
        RafflePool.loadAllPeriods();
        dates.clear();
        periods.clear();
        durations.clear();
        RafflePool.loadPeriods();
        items.clear();
        RafflePool.loadItems();
        gainlogs.clear();
        RafflePool.loadLogs();
    }

    public static void loadAllTypes() {
        if (!allTypes.isEmpty()) {
            return;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            for (RaffleType rt : RaffleType.values()) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE raffle_pool SET type = ? WHERE type = ?");){
                    ps.setInt(1, rt.getItemId());
                    ps.setInt(2, rt.ordinal());
                    ps.execute();
                }
                PreparedStatement ps = con.prepareStatement("UPDATE raffle_period SET type = ? WHERE type = ?");
                try {
                    ps.setInt(1, rt.getItemId());
                    ps.setInt(2, rt.ordinal());
                    ps.execute();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
                ps = con.prepareStatement("UPDATE raffle_log SET type = ? WHERE type = ?");
                try {
                    ps.setInt(1, rt.getItemId());
                    ps.setInt(2, rt.ordinal());
                    ps.execute();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT type from raffle_pool GROUP BY type");){
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    allTypes.add(rs.getInt("type"));
                }
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void loadAllItems() {
        if (!allItems.isEmpty()) {
            return;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM raffle_pool");){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                allItems.add(new RaffleItem(rs.getInt("id"), rs.getInt("period"), rs.getInt("itemId"), rs.getInt("quantity"), rs.getInt("chance"), rs.getInt("smega") != 0, rs.getInt("type"), rs.getInt("allow") == 1));
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void loadItems() {
        RafflePool.loadAllTypes();
        for (int type : allTypes) {
            if (items.containsKey(type)) continue;
            for (RaffleItem ri : allItems) {
                if (!ri.isAllow() || ri.getType() != type || !periods.containsKey(type) || ri.getPeriod() > 0 && periods.get(type) >= 0 && ri.getPeriod() != periods.get(type).intValue()) continue;
                if (!items.containsKey(type)) {
                    items.put(type, new LinkedList());
                }
                if (items.get(type).contains(ri)) continue;
                items.get(type).add(ri);
            }
        }
    }

    public static void loadPeriods() {
        RafflePool.loadAllTypes();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            for (int type : allTypes) {
                if (periods.containsKey(type)) continue;
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM raffle_period WHERE type = ? LIMIT 1");){
                    ps.setInt(1, type);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        periods.put(type, rs.getInt("period"));
                        durations.put(type, rs.getInt("duration"));
                        dates.put(type, rs.getInt("start_date"));
                    }
                }
                if (periods.containsKey(type)) continue;
                periods.put(type, 1);
                durations.put(type, type == 5537000 ? 1 : 7);
                dates.put(type, DateUtil.getTime() / 100);
                PreparedStatement ps = con.prepareStatement("INSERT INTO raffle_period(type, period, duration, start_date) VALUES (?, ?, ?, ?)");
                try {
                    ps.setInt(1, type);
                    ps.setInt(2, periods.get(type));
                    ps.setInt(3, durations.get(type));
                    ps.setInt(4, dates.get(type));
                    ps.execute();
                }
                finally {
                    if (ps == null) continue;
                    ps.close();
                }
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void loadAllPeriods() {
        RafflePool.loadAllTypes();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            for (int type : allTypes) {
                if (allPeriods.containsKey(type)) continue;
                PreparedStatement ps = con.prepareStatement("SELECT type, period FROM raffle_pool WHERE type = ? AND period >= 0 GROUP BY period");
                try {
                    ps.setInt(1, type);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (!allPeriods.containsKey(type)) {
                            allPeriods.put(type, new LinkedList());
                        }
                        if (allPeriods.get(type).contains(rs.getInt("period"))) continue;
                        allPeriods.get(type).add(rs.getInt("period"));
                        Collections.sort(allPeriods.get(type));
                    }
                }
                finally {
                    if (ps == null) continue;
                    ps.close();
                }
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void loadLogs() {
        RafflePool.loadAllTypes();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            for (int type : allTypes) {
                if (gainlogs.containsKey(type)) continue;
                PreparedStatement ps = con.prepareStatement("SELECT * FROM raffle_log WHERE type = ? AND period = ?");
                try {
                    ps.setInt(1, type);
                    ps.setInt(2, periods.get(type));
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) continue;
                    if (!gainlogs.containsKey(type)) {
                        gainlogs.put(type, new LinkedHashMap());
                    }
                    gainlogs.get(type).put(rs.getInt("itemId"), rs.getInt("quantity"));
                }
                finally {
                    if (ps == null) continue;
                    ps.close();
                }
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static List<RaffleItem> getAllItems() {
        RafflePool.loadAllItems();
        if (allItems.isEmpty()) {
            return new LinkedList<RaffleItem>();
        }
        return new LinkedList<RaffleItem>(allItems);
    }

    public static List<RaffleItem> getAllItems(int type) {
        if (!items.containsKey(type)) {
            RafflePool.loadAllItems();
            RafflePool.loadAllPeriods();
            RafflePool.loadPeriods();
            RafflePool.loadItems();
            RafflePool.loadLogs();
        }
        if (!items.containsKey(type) || items.get(type) == null) {
            return new LinkedList<RaffleItem>();
        }
        return items.get(type);
    }

    public static List<RaffleItem> getItems(int type) {
        return RafflePool.getItems(0, type);
    }

    public static List<RaffleItem> getItems(int itemType, int type) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<RaffleItem> its = switch (itemType) {
            case 1 -> RafflePool.getMainReward(type);
            case 2 -> RafflePool.getMinorReward(type);
            default -> RafflePool.getAllItems(type);
        };
        LinkedList<RaffleItem> itemList = new LinkedList<RaffleItem>(its);
        if (!its.isEmpty()) {
            for (RaffleItem ri : its) {
                if (!(ii.itemExists(ri.getItemId()) || ii.isSkinExist(ri.getItemId()) || ii.isHairExist(ri.getItemId()) || ii.isFaceExist(ri.getItemId()))) {
                    itemList.remove(ri);
                }
                if (ri.getQuantity() < 0 || ri.getQuantity() != 0 && (!gainlogs.containsKey(ri.getType()) || !gainlogs.get(ri.getType()).containsKey(ri.getItemId()) || ri.getQuantity() > gainlogs.get(ri.getType()).get(ri.getItemId()))) continue;
                itemList.remove(ri);
            }
        }
        return itemList;
    }

    public static RaffleItem randomItem(int type) {
        return RafflePool.randomItem(type, -1);
    }

    public static RaffleItem randomItem(int type, int nGender) {
        RafflePool.checkPool();
        List<RaffleItem> itemList = RafflePool.getItems(1, type);
        if (2028394 == type) {
            itemList.addAll(RafflePool.getItems(1, 5060048));
        }
        itemList.addAll(RafflePool.getItems(2, type));
        LotteryRandom lr = RafflePool.getRandomByGender(itemList, nGender);
        RaffleItem itR = null;
        if (lr.size() > 0) {
            itR = (RaffleItem)lr.random();
        }
        return itR != null && RafflePool.gainRaffle(itR) ? itR : null;
    }

    private static LotteryRandom getRandomByGender(List<RaffleItem> itemList, int nGender) {
        LotteryRandom random = new LotteryRandom();
        if (itemList.isEmpty()) {
            return random;
        }
        for (RaffleItem rf : itemList) {
            if (ItemConstants.類型.getGender(rf.getItemId()) != nGender && ItemConstants.類型.getGender(rf.getItemId()) < 2 && nGender >= 0) continue;
            random.addData((Object)rf, rf.getChance());
        }
        return random;
    }

    public static boolean gainRaffle(RaffleItem item) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!(ii.itemExists(item.getItemId()) || ii.isSkinExist(item.getItemId()) || ii.isHairExist(item.getItemId()) || ii.isFaceExist(item.getItemId()))) {
            return false;
        }
        if (item.getQuantity() >= 0) {
            int amount;
            if (item.getQuantity() == 0) {
                return false;
            }
            int n = amount = !gainlogs.containsKey(item.getType()) || !gainlogs.get(item.getType()).containsKey(item.getItemId()) ? 0 : gainlogs.get(item.getType()).get(item.getItemId());
            if (item.getQuantity() <= amount) {
                return false;
            }
            ++amount;
            if (!gainlogs.containsKey(item.getType())) {
                gainlogs.put(item.getType(), new LinkedHashMap());
            }
            gainlogs.get(item.getType()).put(item.getItemId(), amount);
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM raffle_log WHERE type = ? AND period = ? AND itemId = ?");){
                    ps.setInt(1, item.getType());
                    ps.setInt(2, item.getPeriod());
                    ps.setInt(3, item.getItemId());
                    ps.execute();
                }
                PreparedStatement ps = con.prepareStatement("INSERT INTO raffle_log(type, period, itemId, quantity) VALUES (?, ?, ?, ?)");
                try {
                    ps.setInt(1, item.getType());
                    ps.setInt(2, item.getPeriod());
                    ps.setInt(3, item.getItemId());
                    ps.setInt(4, amount);
                    ps.execute();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
        return true;
    }

    public static List<RaffleItem> getMainReward(int type) {
        RafflePool.checkPool();
        List<RaffleItem> all = RafflePool.getAllItems(type);
        LinkedList<RaffleItem> mainRewards = new LinkedList<RaffleItem>();
        for (RaffleItem ri : all) {
            if (ri.getPeriod() <= 0 || !ri.isSmega()) continue;
            mainRewards.add(ri);
        }
        return mainRewards;
    }

    public static List<RaffleItem> getMinorReward(int type) {
        RafflePool.checkPool();
        List<RaffleItem> all = RafflePool.getAllItems(type);
        LinkedList<RaffleItem> minorRewards = new LinkedList<RaffleItem>();
        for (RaffleItem ri : all) {
            if (ri.getPeriod() != 0 && ri.isSmega()) continue;
            minorRewards.add(ri);
        }
        return minorRewards;
    }

    public static long getNextPeriodDate(int type) {
        RafflePool.checkPool();
        if (!durations.containsKey(type) || !dates.containsKey(type)) {
            RafflePool.loadAllItems();
            RafflePool.loadAllPeriods();
            RafflePool.loadPeriods();
            RafflePool.loadItems();
            RafflePool.loadLogs();
        }
        if (!durations.containsKey(type) || !dates.containsKey(type)) {
            return DateUtil.getStringToTime(DateUtil.getPreDate("d", 1).replace("-", "") + "0000") - 60000L;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtil.getStringToTime(String.valueOf(dates.get(type)) + "0000"));
        calendar.add(5, durations.get(type));
        calendar.set(14, 0);
        return calendar.getTimeInMillis() - 60000L;
    }

    public static int getDate(int type) {
        if (!dates.containsKey(type)) {
            RafflePool.loadPeriods();
        }
        return dates.get(type);
    }

    public static int getPeriod(int type) {
        if (!periods.containsKey(type)) {
            RafflePool.loadPeriods();
        }
        return periods.get(type);
    }

    public static int getDuration(int type) {
        if (!durations.containsKey(type)) {
            RafflePool.loadPeriods();
        }
        return durations.get(type);
    }

    public static void setDate(int type, int value) {
        dates.put(type, value);
    }

    public static void setPeriod(int type, int value) {
        periods.put(type, value);
    }

    public static void setDuration(int type, int value) {
        durations.put(type, value);
    }

    public static List<Integer> getAllPeriod(int type) {
        if (!allPeriods.containsKey(type)) {
            RafflePool.loadAllPeriods();
        }
        return allPeriods.get(type);
    }

    public static List<Integer> getAllType() {
        RafflePool.loadAllTypes();
        return allTypes;
    }

    public static Map<Integer, Pair<List<Integer>, List<Integer>>> getRoyalCouponList() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        LinkedHashMap<Integer, Pair<List<Integer>, List<Integer>>> couponList = new LinkedHashMap<Integer, Pair<List<Integer>, List<Integer>>>();
        for (int couponId : RafflePool.getAllType()) {
            if (couponId / 1000 == 5151 || couponId / 100 == 51521 || couponId / 100 == 51523 || (couponId < 5150000 || couponId >= 5153000) && couponId / 1000 != 5154) continue;
            if (!couponList.containsKey(couponId)) {
                couponList.put(couponId, new Pair(new LinkedList(), new LinkedList()));
            }
            for (RaffleItem ri : RafflePool.getAllItems(couponId)) {
                if (RafflePool.getPeriod(couponId) != ri.getPeriod() && ri.getPeriod() != 0 || !ii.isHairExist(ri.getItemId()) && !ii.isFaceExist(ri.getItemId()) || ri.getItemId() >= 1000000) continue;
                int nStyleGenderCode = ItemConstants.類型.getGender(ri.getItemId());
                if (nStyleGenderCode == 0 || nStyleGenderCode >= 2) {
                    ((List)((Pair)couponList.get(couponId)).getLeft()).add(ri.getItemId());
                }
                if (nStyleGenderCode != 1 && nStyleGenderCode < 2) continue;
                ((List)((Pair)couponList.get(couponId)).getRight()).add(ri.getItemId());
            }
        }
        return couponList;
    }

    public static enum RaffleType {
        黃金蘋果(5060048),
        魔法畫框(5060086),
        幸運的金色寶箱(2028394),
        幸運的銀色寶箱(2028393),
        傷害字型(5060049),
        萌獸卡牌包(5537000),
        艾比寶箱(5060032),
        潘朵拉寶箱(5060028),
        幸運寶箱(5060057),
        幸運紅包(0x282182),
        魔法豎琴(5060025);

        private final int itemId;

        private RaffleType(int id) {
            this.itemId = id;
        }

        public int getItemId() {
            return this.itemId;
        }
    }
}

