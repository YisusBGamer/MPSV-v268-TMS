/*
 * Decompiled with CFR 0.152.
 */
package Net.server.life;

import Client.inventory.MapleInventoryType;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Database.DatabaseLoader;
import Net.server.MapleItemInformationProvider;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterStats;
import Net.server.life.MonsterDropEntry;
import Net.server.life.MonsterGlobalDropEntry;
import Net.server.reward.RewardDropEntry;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.loaders.StringData;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;

public class MapleMonsterInformationProvider {
    private static final Logger log = LoggerFactory.getLogger(MapleMonsterInformationProvider.class);
    private static final MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();
    private static final MapleDataProvider stringDataWZ = MapleDataProviderFactory.getString();
    private static final MapleData mobStringData = stringDataWZ.getData("MonsterBook.img");
    private final Map<Integer, List<MonsterDropEntry>> drops = new TreeMap<Integer, List<MonsterDropEntry>>();
    private final List<MonsterGlobalDropEntry> globaldrops = new ArrayList<MonsterGlobalDropEntry>();
    private final Map<Integer, Map<Integer, List<RewardDropEntry>>> specialdrops = new HashMap<Integer, Map<Integer, List<RewardDropEntry>>>();

    public static MapleMonsterInformationProvider getInstance() {
        return instance;
    }

    public Map<Integer, List<MonsterDropEntry>> getAllDrop() {
        return this.drops;
    }

    public List<MonsterGlobalDropEntry> getGlobalDrop() {
        return this.globaldrops;
    }

    public Map<Integer, Map<Integer, List<RewardDropEntry>>> getFishDrop() {
        return this.specialdrops;
    }

    public void setDropData(int mobid, List<MonsterDropEntry> dropEntries) {
        this.setDropData(String.valueOf(mobid), dropEntries);
    }

    public void setDropData(String mobid, List<MonsterDropEntry> dropEntries) {
        this.update(Integer.parseInt(mobid), dropEntries);
        List<MonsterDropEntry> dropInfo = this.drops.computeIfAbsent(Integer.parseInt(mobid), k -> new LinkedList());
        dropInfo.clear();
        try {
            MapleMonsterStats mons;
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid = ?");){
                ps.setInt(1, Integer.parseInt(mobid));
                try (ResultSet rs = ps.executeQuery();){
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int itemId = rs.getInt("itemid");
                        int chance = rs.getInt("chance");
                        if (itemId > 0 && !ii.itemExists(itemId)) continue;
                        MonsterDropEntry dropEntry = new MonsterDropEntry(id, itemId, chance, rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getInt("questid"), rs.getBoolean("onlySelf"), rs.getInt("period"), "數據庫");
                        String[] split = rs.getString("channel").replace("[", "").replace("]", "").replace(" ", "").split(",");
                        Set collect = Arrays.stream(split).filter(it -> !it.isEmpty()).map(Integer::valueOf).collect(Collectors.toSet());
                        dropEntry.channels.addAll(collect);
                        dropInfo.add(dropEntry);
                    }
                }
            }
            boolean hasMeso = false;
            for (MonsterDropEntry mde : dropInfo) {
                if (mde.itemId != 0) continue;
                hasMeso = true;
                break;
            }
            if (!hasMeso && (mons = MapleLifeFactory.getMonsterStats(Integer.parseInt(mobid))) != null) {
                this.addMeso(mons, (List<MonsterDropEntry>)dropInfo);
            }
        }
        catch (SQLException e) {
            log.error("導入怪物爆率錯誤", e);
        }
    }

    public void setGlobalDropData(String id, MonsterGlobalDropEntry monsterGlobalDropEntry) {
        this.updateGlobal(Integer.parseInt(id), monsterGlobalDropEntry);
        this.globaldrops.clear();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM drop_data_global");
                 ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    int itemId = rs.getInt("itemid");
                    if (itemId > 0 && !ii.itemExists(itemId)) continue;
                    MonsterGlobalDropEntry e = new MonsterGlobalDropEntry(rs.getInt("id"), rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("continent"), rs.getByte("dropType"), rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getInt("questid"), rs.getInt("min_mob_level"), rs.getInt("max_mob_level"), rs.getBoolean("onlySelf"), rs.getInt("period"), "數據庫");
                    String[] split = rs.getString("channel").replace("[", "").replace("]", "").replace(" ", "").split(",");
                    Set collect = Arrays.stream(split).filter(it -> !it.isEmpty()).map(Integer::valueOf).collect(Collectors.toSet());
                    e.channels.addAll(collect);
                    this.globaldrops.add(e);
                }
            }
        }
        catch (SQLException e) {
            log.error("導入怪物爆率錯誤", e);
        }
    }

    public void removeDropData(int mobid) {
        this.drops.remove(mobid);
        this.update(mobid, null);
    }

    public void update(int mobid, List<MonsterDropEntry> dropEntries) {
        DruidPooledConnection con2;
        try {
            con2 = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            try {
                PreparedStatement ps = con2.prepareStatement("DELETE FROM drop_data WHERE dropperid = ?");
                try {
                    ps.setInt(1, mobid);
                    ps.execute();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            finally {
                if (con2 != null) {
                    con2.close();
                }
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        if (dropEntries != null) {
            try {
                con2 = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                try {
                    PreparedStatement ps = con2.prepareStatement("INSERT INTO drop_data VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    try {
                        dropEntries.forEach(monsterDropEntry -> {
                            try {
                                ps.setInt(1, mobid);
                                ps.setInt(2, monsterDropEntry.itemId);
                                ps.setInt(3, monsterDropEntry.minimum);
                                ps.setInt(4, monsterDropEntry.maximum);
                                ps.setInt(5, monsterDropEntry.questid);
                                ps.setInt(6, monsterDropEntry.chance);
                                String itemName = MapleItemInformationProvider.getInstance().getName(monsterDropEntry.itemId);
                                switch (monsterDropEntry.itemId) {
                                    case 0: {
                                        itemName = "楓幣";
                                        break;
                                    }
                                    case -1: {
                                        itemName = "樂豆點";
                                        break;
                                    }
                                    case -2: {
                                        itemName = "楓葉點數";
                                        break;
                                    }
                                    case -3: {
                                        itemName = "里程";
                                    }
                                }
                                ps.setString(7, itemName);
                                ps.setString(8, monsterDropEntry.channels.toString());
                                ps.setBoolean(9, monsterDropEntry.onlySelf);
                                ps.setInt(10, monsterDropEntry.period);
                                ps.execute();
                            }
                            catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    finally {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                }
                finally {
                    if (con2 != null) {
                        con2.close();
                    }
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    public void updateGlobal(int id, MonsterGlobalDropEntry monsterGlobalDropEntry) {
        PreparedStatement ps;
        DruidPooledConnection con2;
        try {
            con2 = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            try {
                ps = con2.prepareStatement("DELETE FROM drop_data_global WHERE id = ?");
                try {
                    ps.setInt(1, id);
                    ps.execute();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            finally {
                if (con2 != null) {
                    con2.close();
                }
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        if (monsterGlobalDropEntry != null) {
            try {
                con2 = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                try {
                    ps = con2.prepareStatement("INSERT INTO drop_data_global VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    try {
                        try {
                            ps.setInt(1, monsterGlobalDropEntry.continent);
                            ps.setInt(2, monsterGlobalDropEntry.dropType);
                            ps.setInt(3, monsterGlobalDropEntry.itemId);
                            ps.setInt(4, monsterGlobalDropEntry.Minimum);
                            ps.setInt(5, monsterGlobalDropEntry.Maximum);
                            ps.setInt(6, monsterGlobalDropEntry.questid);
                            ps.setInt(7, monsterGlobalDropEntry.chance);
                            String itemName = MapleItemInformationProvider.getInstance().getName(monsterGlobalDropEntry.itemId);
                            switch (monsterGlobalDropEntry.itemId) {
                                case 0: {
                                    itemName = "楓幣";
                                    break;
                                }
                                case -1: {
                                    itemName = "樂豆點";
                                    break;
                                }
                                case -2: {
                                    itemName = "楓葉點數";
                                    break;
                                }
                                case -3: {
                                    itemName = "里程";
                                }
                            }
                            ps.setString(8, itemName);
                            ps.setString(9, monsterGlobalDropEntry.channels.toString());
                            ps.setInt(10, monsterGlobalDropEntry.minMobLevel);
                            ps.setInt(11, monsterGlobalDropEntry.maxMobLevel);
                            ps.setBoolean(12, monsterGlobalDropEntry.onlySelf);
                            ps.setInt(13, monsterGlobalDropEntry.period);
                            ps.execute();
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    finally {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                }
                finally {
                    if (con2 != null) {
                        con2.close();
                    }
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    public void load() {
        try {
            ResultSet rs;
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            Map<Integer, List<MonsterDropEntry>> tmpDropInfo = new HashMap<>();
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps2 = con.prepareStatement("SELECT * FROM drop_data");
                 ResultSet resultSet = ps2.executeQuery();){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int dropperId = resultSet.getInt("dropperid");
                    int itemId = resultSet.getInt("itemid");
                    int chance = resultSet.getInt("chance");
                    if (itemId > 0 && !ii.itemExists(itemId)) continue;
                    List dropList = tmpDropInfo.containsKey(dropperId) ? (List)tmpDropInfo.get(dropperId) : new ArrayList();
                    MonsterDropEntry dropEntry = new MonsterDropEntry(id, itemId, chance, resultSet.getInt("minimum_quantity"), resultSet.getInt("maximum_quantity"), resultSet.getInt("questid"), resultSet.getBoolean("onlySelf"), resultSet.getInt("period"), "數據庫");
                    String[] split = resultSet.getString("channel").replace("[", "").replace("]", "").replace(" ", "").split(",");
                    Set collect = Arrays.stream(split).filter(it -> !it.isEmpty()).map(Integer::valueOf).collect(Collectors.toSet());
                    dropEntry.channels.addAll(collect);
                    dropList.add(dropEntry);
                    tmpDropInfo.put(dropperId, dropList);
                }
            }
            for (Map.Entry<Integer, List<MonsterDropEntry>> entry : tmpDropInfo.entrySet()) {


                MapleMonsterStats mons;
                boolean hasMeso = false;
                for (MonsterDropEntry dropEntry : entry.getValue()) {

                    if (dropEntry.itemId != 0) continue;
                    hasMeso = true;
                    break;
                }
                if (hasMeso || (mons = MapleLifeFactory.getMonsterStats((Integer)entry.getKey())) == null) continue;
                this.addMeso(mons, (List<MonsterDropEntry>)((List)entry.getValue()));
            }
            this.drops.putAll(tmpDropInfo);
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM drop_data_global");){
                rs = preparedStatement.executeQuery();
                try {
                    while (rs.next()) {
                        int itemId = rs.getInt("itemid");
                        if (itemId > 0 && !ii.itemExists(itemId)) continue;
                        MonsterGlobalDropEntry e = new MonsterGlobalDropEntry(rs.getInt("id"), rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("continent"), rs.getByte("dropType"), rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getInt("questid"), rs.getInt("min_mob_level"), rs.getInt("max_mob_level"), rs.getBoolean("onlySelf"), rs.getInt("period"), "數據庫");
                        String[] split = rs.getString("channel").replace("[", "").replace("]", "").replace(" ", "").split(",");
                        Set collect = Arrays.stream(split).filter(it -> !it.isEmpty()).map(Integer::valueOf).collect(Collectors.toSet());
                        e.channels.addAll(collect);
                        this.globaldrops.add(e);
                    }
                }
                finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            }
            DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            try (PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM drop_data_special");){
                rs = preparedStatement.executeQuery();
                try {
                    while (rs.next()) {
                        int itemId = rs.getInt("itemid");
                        if (itemId > 0 && !ii.itemExists(itemId)) continue;
                        int mapid = rs.getInt("mapid");
                        int dropperid = rs.getInt("dropperid");
                        Map<Integer, List<RewardDropEntry>> drop_data_special = specialdrops.computeIfAbsent(mapid, k -> new HashMap<>());
                        List<RewardDropEntry> list = drop_data_special.computeIfAbsent(mapid, k -> new ArrayList<>());
                        list.add(new RewardDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("quantity"), rs.getInt("msgType"), rs.getInt("period"), rs.getInt("state")));
                        drop_data_special.put(dropperid, list);
                        this.specialdrops.put(mapid, drop_data_special);
                    }
                }
                finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            }
            finally {
                if (con != null) {
                    con.close();
                }
            }
        }
        catch (SQLException e) {
            log.error("導入怪物爆率錯誤", e);
        }
    }

    public List<MonsterDropEntry> retrieveDrop(int monsterId) {
        return this.drops.getOrDefault(monsterId, Collections.emptyList());
    }

    public List<RewardDropEntry> retrieveSpecialDrop(int mapid, int dropperId) {
        Map<Integer, List<RewardDropEntry>> map = this.specialdrops.get(mapid);
        if (map != null) {
            return map.getOrDefault(dropperId, Collections.emptyList());
        }
        return Collections.emptyList();
    }

    public RewardDropEntry getReward(int mapid, int dropperId) {
        List<RewardDropEntry> dropEntry = this.retrieveSpecialDrop(0, dropperId);
        dropEntry.addAll(this.retrieveSpecialDrop(mapid, dropperId));
        int chance = (int)Math.floor(Math.random() * 1000.0);
        ArrayList<RewardDropEntry> ret = new ArrayList<RewardDropEntry>();
        for (RewardDropEntry de : dropEntry) {
            if (de.chance < chance) continue;
            ret.add(de);
        }
        if (ret.isEmpty()) {
            return null;
        }
        Collections.shuffle(ret);
        return (RewardDropEntry)ret.get(Randomizer.nextInt(ret.size()));
    }

    public void addMeso(MapleMonsterStats mons, List<MonsterDropEntry> ret) {
        this.addMeso(ret, mons.getId(), mons.getLevel(), mons.isBoss(), mons.isPartyBonus(), mons.dropsMesoCount());
    }

    public void addMeso(MapleMonster mob, List<MonsterDropEntry> ret) {
        this.addMeso(ret, mob.getId(), mob.getMobLevel(), mob.isBoss(), mob.getStats().isPartyBonus(), mob.getStats().dropsMesoCount());
    }

    private void addMeso(List<MonsterDropEntry> ret, int id, int level, boolean isBoss, boolean isPartyBonous, int dropsMesoCount) {
        if (!ServerConfig.ADD_DEFAULT_MESO) {
            return;
        }
        double divided = level < 100 ? (level < 10 ? (double)level : 10.0) : (double)level / 10.0;
        int maxMeso = level * (int)Math.ceil((double)level / divided);
        if (isBoss && !isPartyBonous) {
            maxMeso *= 3;
        }
        for (int i = 0; i < dropsMesoCount; ++i) {
            if (id >= 9600086 && id <= 9600098) {
                int meso = (int)Math.floor(Math.random() * 500.0 + 1000.0);
                ret.add(new MonsterDropEntry(0, 0, 20000, (int)Math.floor(0.46 * (double)meso), meso, 0, false, 0, "楓幣添加"));
                continue;
            }
            ret.add(new MonsterDropEntry(0, 0, isBoss && !isPartyBonous ? 800000 : (isPartyBonous ? 600000 : 400000), (int)Math.floor(0.66 * (double)maxMeso), maxMeso, 0, false, 0, "楓幣添加"));
        }
    }

    public void clearDrops() {
        this.drops.clear();
        this.globaldrops.clear();
        this.specialdrops.clear();
        this.load();
    }

    public boolean contains(List<MonsterDropEntry> e, int toAdd) {
        for (MonsterDropEntry f : e) {
            if (f.itemId != toAdd) continue;
            return true;
        }
        return false;
    }

    public String getDrops(int item, boolean showId) {
        LinkedList<Integer> dropsfound = new LinkedList<Integer>();
        for (int mobId : StringData.mobStrings.keySet()) {
            for (MonsterDropEntry b : this.retrieveDrop(mobId)) {
                if (b.itemId != item || dropsfound.contains(mobId)) continue;
                dropsfound.add(mobId);
            }
        }
        String droplist = "";
        Iterator iterator = dropsfound.iterator();
        while (iterator.hasNext()) {
            int c = (Integer)iterator.next();
            droplist = (String)droplist + "#o" + c + (String)(showId ? "#(" + c + ")" : "") + "\r\n";
        }
        return droplist;
    }

    public int chanceLogic(int itemId) {
        if (ItemConstants.getInventoryType(itemId, false) == MapleInventoryType.EQUIP) {
            return 8000;
        }
        if (ItemConstants.getInventoryType(itemId) == MapleInventoryType.SETUP || ItemConstants.getInventoryType(itemId) == MapleInventoryType.CASH) {
            return 500;
        }
        switch (itemId / 10000) {
            case 204: {
                return 1800;
            }
            case 207: 
            case 233: {
                return 3000;
            }
            case 229: {
                return 400;
            }
            case 401: 
            case 402: {
                return 5000;
            }
            case 403: {
                return 4000;
            }
        }
        return 8000;
    }
}

