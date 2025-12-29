/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.life.MapleLifeFactory$loseItem
 *  lombok.Generated
 */
package Net.server.life;

import Config.constants.GameConstants;
import Config.constants.ServerConstants;
import Config.constants.enums.Holiday;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.auth.Auth;
import Net.server.InitializeServer;
import Net.server.life.AbstractLoadedMapleLife;
import Net.server.life.BanishInfo;
import Net.server.life.Element;
import Net.server.life.ElementalEffectiveness;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterStats;
import Net.server.life.MapleNPC;
import Net.server.life.MobAttackInfo;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataEntry;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import Plugin.provider.MapleDataType;
import Server.world.World;
import SwordieX.util.Util;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;
import tools.Triple;

public class MapleLifeFactory {
    private static final MapleDataProvider data = MapleDataProviderFactory.getMob();
    private static final MapleDataProvider npcData = MapleDataProviderFactory.getNpc();
    private static final MapleDataProvider stringDataWZ = MapleDataProviderFactory.getString();
    private static final MapleDataProvider etcDataWZ = MapleDataProviderFactory.getEtc();
    private static final MapleDataProvider effectDataWZ = MapleDataProviderFactory.getEffect();
    private static final Logger log = LoggerFactory.getLogger(MapleLifeFactory.class);
    private static final MapleData mobStringData = stringDataWZ.getData("Mob.img");
    private static final MapleData npcStringData = stringDataWZ.getData("Npc.img");
    private static final MapleData npclocData = etcDataWZ.getData("NpcLocation.img");
    private static final Map<Integer, String> npcNames = new HashMap<Integer, String>();
    protected static final Map<Integer, String> npcScriptNames = new HashMap<Integer, String>();
    protected static final List<Integer> npcShops = new LinkedList<Integer>();
    protected static final List<Integer> npcTrunks = new LinkedList<Integer>();
    private static final Map<Integer, Boolean> mobIds = new HashMap<Integer, Boolean>();
    private static final Map<Integer, MapleMonsterStats> monsterStats = new HashMap<Integer, MapleMonsterStats>();
    private static final Map<Integer, Integer> NPCLoc = new HashMap<Integer, Integer>();
    private static final Map<Integer, List<Integer>> questCount = new HashMap<Integer, List<Integer>>();
    private static final Map<Integer, String> EliteMobs = new HashMap<Integer, String>();
    private static MapleDataProvider datasource;
    private static MapleDataProvider questDatasource;
    public static Map<Integer, String> mobStrings;

    public static Map<Integer, String> getMobStrings() {
        return mobStrings;
    }

    public static String getMonsterName(int mobid) {
        return MapleLifeFactory.getMobStrings().getOrDefault(mobid, "");
    }

    public static Map<Integer, String> getMonsterNames() {
        return mobStrings;
    }

    public static void CreatMonsterStr() {
        log.info("Started generating string mobs data.");
        long start = System.currentTimeMillis();
        datasource = MapleDataProviderFactory.getString();
        questDatasource = MapleDataProviderFactory.getQuest();
        MapleLifeFactory.loadMobStringsFromWz();
        MapleLifeFactory.saveMobStrings(ServerConstants.DAT_DIR + "/strings");
        log.info(String.format("Completed generating string mobs data in %dms.", System.currentTimeMillis() - start));
    }

    public static void CreatNpcStr() {
        log.info("Started generating string ncps data.");
        long start = System.currentTimeMillis();
        datasource = MapleDataProviderFactory.getString();
        questDatasource = MapleDataProviderFactory.getQuest();
        MapleLifeFactory.loadMobStringsFromWz();
        MapleLifeFactory.saveMobStrings(ServerConstants.DAT_DIR + "/strings");
        log.info(String.format("Completed generating string ncps data in %dms.", System.currentTimeMillis() - start));
    }

    public static void loadMobStrings() {
        long start = System.currentTimeMillis();
        File file = new File(ServerConstants.DAT_DIR + "/strings/mobs.dat");
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; ++i) {
                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();
                MapleLifeFactory.getMobStrings().put(id, name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info(String.format("Loaded mob strings from data file in %dms.", System.currentTimeMillis() - start));
    }

    private static void saveMobStrings(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(dir + "/mobs.dat");
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(MapleLifeFactory.getMobStrings().size());
            for (Map.Entry<Integer, String> entry : MapleLifeFactory.getMobStrings().entrySet()) {
                int id = entry.getKey();
                String name = entry.getValue();
                dataOutputStream.writeInt(id);
                dataOutputStream.writeUTF(name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMobStringsFromWz() {
        log.info("Started loading mob strings from wz.");
        long start = System.currentTimeMillis();
        for (MapleData mainNode : datasource.getData("Mob.img")) {
            int id = Integer.parseInt(mainNode.getName());
            for (MapleData infoNode : mainNode.getChildren()) {
                String name = infoNode.getName();
                String value = "";
                if (infoNode.getData() != null) {
                    value = infoNode.getData().toString();
                }
                switch (name) {
                    case "name": {
                        MapleLifeFactory.getMobStrings().put(id, value);
                    }
                }
            }
        }
        log.info(String.format("Loaded mob strings in %dms.", System.currentTimeMillis() - start));
    }

    public static void loadNpcStringsFromWz() {
        log.info("Started loading mob strings from wz.");
        long start = System.currentTimeMillis();
        for (MapleData mainNode : datasource.getData("Npc.img")) {
            int id = Integer.parseInt(mainNode.getName());
            Iterator<MapleData> iterator = mainNode.getChildren().iterator();
            if (!iterator.hasNext()) continue;
            MapleData infoNode = iterator.next();
            String name = infoNode.getName();
            MapleLifeFactory.getMobStrings().put(id, name);
        }
        log.info(String.format("Loaded mob strings in %dms.", System.currentTimeMillis() - start));
    }

    public static AbstractLoadedMapleLife getLife(int id, String type, int mapid) {
        if (type.equalsIgnoreCase("n")) {
            return MapleLifeFactory.getNPC(id, mapid);
        }
        if (type.equalsIgnoreCase("m")) {
            return MapleLifeFactory.getMonster(id);
        }
        System.err.println("Unknown Life type: " + type);
        return null;
    }

    public static boolean isBoss(int mobid) {
        return mobIds.containsKey(mobid) && mobIds.get(mobid) != false;
    }

    public static boolean checkMonsterIsExist(int mobid) {
        return mobIds.containsKey(mobid);
    }

    public static int getNPCLocation(int npcid) {
        if (NPCLoc.containsKey(npcid)) {
            return NPCLoc.get(npcid);
        }
        int map = MapleDataTool.getIntConvert(npcid + "/0", npclocData, -1);
        NPCLoc.put(npcid, map);
        return map;
    }

    public static void loadQuestCounts() {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            if (InitializeServer.WzSqlName.wz_questcount.check(con)) {
                SqlTool.queryAndGetList(con, "SELECT * FROM `wz_questcount`", rs -> {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    for (String count : rs.getString("info").split(",")) {
                        list.add(Integer.parseInt(count));
                    }
                    questCount.put(rs.getInt("id"), list);
                    return null;
                });
            } else {
                InitializeServer.WzSqlName.wz_questcount.drop(con);
                SqlTool.update(con, "CREATE TABLE `wz_questcount` (`id` int NOT NULL,`info` text NOT NULL,PRIMARY KEY (`id`))");
                for (MapleDataDirectoryEntry mapz : data.getRoot().getSubdirectories()) {
                    if (!mapz.getName().equals("QuestCountGroup")) continue;
                    for (MapleDataFileEntry entry : mapz.getFiles()) {
                        int id = Integer.parseInt(entry.getName().substring(0, entry.getName().length() - 4));
                        MapleData da = data.getData("QuestCountGroup/" + entry.getName());
                        if (da == null || da.getChildByPath("info") == null) continue;
                        StringBuilder s = new StringBuilder();
                        boolean b = false;
                        ArrayList<Integer> info = new ArrayList<Integer>();
                        for (MapleData d : da.getChildByPath("info")) {
                            int count = MapleDataTool.getInt(d, 0);
                            info.add(count);
                            if (b) {
                                s.append(",");
                            } else {
                                b = true;
                            }
                            s.append(count);
                        }
                        questCount.put(id, info);
                        SqlTool.update(con, "INSERT INTO `wz_questcount` (`id`,`info`) VALUES (?,?)", id, s.toString());
                    }
                }
                InitializeServer.WzSqlName.wz_questcount.update(con);
            }
            if (InitializeServer.WzSqlName.wz_npcnames.check(con)) {
                SqlTool.queryAndGetList(con, "SELECT * FROM `wz_npcnames`", rs -> npcNames.put(rs.getInt("npcid"), rs.getString("name")));
            } else {
                InitializeServer.WzSqlName.wz_npcnames.drop(con);
                SqlTool.update(con, "CREATE TABLE `wz_npcnames` (`npcid` int NOT NULL,`name` text NOT NULL,PRIMARY KEY (`npcid`))");
                for (MapleData c : npcStringData) {
                    if (c.getName().contains("pack_ignore")) continue;
                    int nid = Integer.parseInt(c.getName());
                    String n = StringUtil.getLeftPaddedStr(nid + ".img", '0', 11);
                    try {
                        String name;
                        if (npcData.getData(n) == null || (name = MapleDataTool.getString("name", c, "MISSINGNO")).contains("MapleTV") || name.contains("嬰兒月妙")) continue;
                        npcNames.put(nid, name);
                        SqlTool.update(con, "INSERT INTO `wz_npcnames` (`npcid`,`name`) VALUES (?,?)", nid, name);
                    }
                    catch (RuntimeException ex) {
                        log.error("", ex);
                    }
                }
                InitializeServer.WzSqlName.wz_npcnames.update(con);
            }
            return null;
        });
        for (MapleDataEntry mapleDataEntry : npcData.getRoot().getFiles()) {
            if (!mapleDataEntry.getName().toLowerCase().replace(".img", "").matches("^\\d+$")) continue;
            MapleData data = npcData.getData(mapleDataEntry.getName());
            if (data == null) {
                System.err.println("讀取NPC資料出錯, img檔案" + mapleDataEntry.getName());
                continue;
            }
            int nid = Integer.parseInt(mapleDataEntry.getName().toLowerCase().replace(".img", ""));
            MapleData info = data.getChildByPath("info");
            if (info == null) continue;
            MapleData script = info.getChildByPath("script");
            if (script != null) {
                for (MapleData dat : script) {
                    String scriptName = MapleDataTool.getString(dat);
                    if (scriptName == null) {
                        scriptName = MapleDataTool.getString("script", dat);
                    }
                    if (scriptName == null) continue;
                    npcScriptNames.put(nid, scriptName);
                    break;
                }
            }
            if (MapleDataTool.getIntConvert("shop", info, 0) == 1) {
                npcShops.add(nid);
            }
            if (MapleDataTool.getIntConvert("trunkPut", info, -1) != 0) continue;
            npcTrunks.add(nid);
        }
    }

    public static void initEliteMonster() {
        for (MapleData data : effectDataWZ.getData("EliteMobEff.img")) {
            if (data.getName().length() >= 3) continue;
            String modifier = MapleDataTool.getString("modifier", data, "");
            for (MapleData d : data.getChildByPath("skill")) {
                EliteMobs.put(Integer.valueOf(d.getName()), modifier);
            }
        }
    }

    public static boolean exitsQuestCount(int mo, int id) {
        List<Integer> ret = questCount.get(mo);
        return ret != null && ret.contains(id);
    }

    public static MapleMonster makeMob(int mobId) {
        if (Auth.isForbiddenMob(mobId)) {
            return null;
        }
        MapleMonsterStats stats = MapleLifeFactory.getMonsterStats(mobId);
        if (stats == null) {
            return null;
        }
        return new MapleMonster(mobId, stats);
    }

    public static MapleMonster getMonster(int mobId) {
        if (Auth.isForbiddenMob(mobId)) {
            return null;
        }
        MapleMonsterStats stats = MapleLifeFactory.getMonsterStats(mobId);
        if (stats == null) {
            return null;
        }
        return new MapleMonster(mobId, stats);
    }

    public static MapleMonster getEliteMonster(int mobId) {
        return MapleLifeFactory.getEliteMonster(mobId, MapleLifeFactory.getMonsterStats(mobId));
    }

    public static MapleMonster getEliteMonster(int mobId, MapleMonsterStats stats) {
        return MapleLifeFactory.getEliteMonster(mobId, stats, -1);
    }

    public static MapleMonster getEliteMonster(int mobId, MapleMonsterStats stats, int eliteGrade) {
        return MapleLifeFactory.getEliteMonster(mobId, stats, eliteGrade, 1);
    }

    public static MapleMonster getEliteMonster(int mobId, MapleMonsterStats stats, int eliteGrade, int eliteType) {
        if (stats == null) {
            return null;
        }
        MapleMonster monster = new MapleMonster(mobId, MapleLifeFactory.getMonsterStats(mobId));
        monster.setEliteGrade(eliteGrade == -1 ? Randomizer.rand(0, 2) : eliteGrade);
        monster.setEliteType(eliteType);
        int a = Randomizer.rand(1, 3);
        ArrayList<Integer> list = new ArrayList<Integer>(EliteMobs.keySet());
        for (int i = 0; i < a; ++i) {
            int n = (Integer)list.get(Randomizer.nextInt(list.size()));
            if (monster.getEliteMobActive().contains(n)) continue;
            monster.getEliteMobActive().add(n);
        }
        monster.setForcedMobStat(stats);
        if (mobId == 8644631) {
            monster.changeHP(monster.getMobMaxHp() * 200L);
            monster.setMaxMP(monster.getMobMaxMp() * 200);
            monster.getForcedMobStat().setExp(Math.min(Integer.MAX_VALUE, monster.getForcedMobStat().getExp() * 80L));
            monster.getForcedMobStat().setWatk(monster.getForcedMobStat().getWatk() * 3);
            monster.getForcedMobStat().setMatk(monster.getForcedMobStat().getMDRate() * 3);
            monster.getForcedMobStat().setPDRate(monster.getForcedMobStat().getPDRate() * 50);
            monster.getForcedMobStat().setMDRate(monster.getForcedMobStat().getMDRate() * 50);
            monster.getForcedMobStat().setPushed(2100000000);
            monster.getForcedMobStat().setSpeed(175);
            monster.setScale(200);
        } else if (eliteType > 1) {
            monster.changeHP(monster.getMobMaxHp() * 400L);
            monster.setMaxMP(monster.getMobMaxMp());
            monster.getForcedMobStat().setExp(Math.min(Integer.MAX_VALUE, monster.getForcedMobStat().getExp() * 160L));
            monster.getForcedMobStat().setWatk(monster.getForcedMobStat().getWatk() * 2);
            monster.getForcedMobStat().setMatk(monster.getForcedMobStat().getMDRate() * 2);
            monster.getForcedMobStat().setPDRate(monster.getForcedMobStat().getPDRate() * 2);
            monster.getForcedMobStat().setMDRate(monster.getForcedMobStat().getMDRate() * 2);
            monster.getForcedMobStat().setPushed(2100000000);
            monster.getForcedMobStat().setSpeed(140);
        } else {
            int hpRate;
            monster.getForcedMobStat().setExp((long)Math.min(2.14748365E9f, (float)monster.getMobExp() * (switch (monster.getEliteGrade()) {
                case 1 -> {
                    hpRate = 45;
                    yield 22.5f;
                }
                case 2 -> {
                    hpRate = 60;
                    yield 30.0f;
                }
                default -> {
                    hpRate = 30;
                    yield 15.0f;
                }
            })));
            monster.changeHP(monster.getMobMaxHp() * (long)hpRate);
            monster.setScale(200);
            if (World.getHoliday() == Holiday.Halloween) {
                monster.setShowMobId(9010196 + Randomizer.nextInt(3));
            }
        }
        return monster;
    }

    public static String getEliteMonEff(int id) {
        return EliteMobs.getOrDefault(id, "MISSINGNO");
    }

    public static MapleMonsterStats getMonsterStats(int mobId) {
        MapleMonsterStats stats = monsterStats.get(mobId);
        if (!monsterStats.containsKey(mobId)) {
            try {
                int bodyDisease;
                MapleData monsterHitPartsToSlot;
                MapleData mobZoneInfo;
                MapleData reviveInfo;
                MapleData banishData;
                MapleData selfd;
                MapleData monsterData = data.getData(StringUtil.getLeftPaddedStr(mobId + ".img", '0', 11));
                if (monsterData == null) {
                    monsterStats.put(mobId, null);
                    return null;
                }
                MapleData monsterInfoData = monsterData.getChildByPath("info");
                stats = new MapleMonsterStats(mobId);
                String maxHpName = "1";
                if (monsterInfoData.getChildByPath("maxHP") != null) {
                    maxHpName = monsterInfoData.getChildByPath("maxHP").getData().toString();
                }
                if (monsterInfoData == null) {
                    return stats;
                }
                if (maxHpName.contains("?")) {
                    stats.setHp(Integer.MAX_VALUE);
                } else if (maxHpName.endsWith("\r\n")) {
                    stats.setHp(Long.parseLong(maxHpName.substring(0, maxHpName.indexOf("\r\n"))));
                } else {
                    stats.setHp(GameConstants.getPartyPlayHP(mobId) > 0 ? (long)GameConstants.getPartyPlayHP(mobId) : Long.parseLong(maxHpName));
                }
                stats.setFinalMaxHP(MapleDataTool.getLong("?", monsterInfoData, stats.getFinalMaxHP()));
                stats.setFinalMaxHP(stats.getFinalMaxHP());
                stats.setType(MapleDataTool.getString("mobType", monsterInfoData, ""));
                stats.setMp(MapleDataTool.getIntConvert("maxMP", monsterInfoData, 0));
                stats.setExp(MapleDataTool.getIntConvert("exp", monsterInfoData, 0));
                stats.setLevel((short)MapleDataTool.getIntConvert("level", monsterInfoData, 1));
                stats.setWeaponPoint((short)MapleDataTool.getIntConvert("wp", monsterInfoData, 0));
                stats.setCharismaEXP((short)MapleDataTool.getIntConvert("charismaEXP", monsterInfoData, 0));
                stats.setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", monsterInfoData, 0));
                stats.setrareItemDropLevel((byte)MapleDataTool.getIntConvert("rareItemDropLevel", monsterInfoData, 0));
                stats.setFixedDamage(MapleDataTool.getIntConvert("fixedDamage", monsterInfoData, -1));
                stats.setOnlyNormalAttack(MapleDataTool.getIntConvert("onlyNormalAttack", monsterInfoData, 0) > 0);
                stats.setBoss(MapleDataTool.getIntConvert("boss", monsterInfoData, 0) > 0 || mobId == 8810018 || mobId == 9410066 || mobId >= 0x866E86 && mobId <= 8810122);
                stats.setExplosiveReward(MapleDataTool.getIntConvert("explosiveReward", monsterInfoData, 0) > 0);
                stats.setUndead(MapleDataTool.getIntConvert("undead", monsterInfoData, 0) > 0);
                stats.setEscort(MapleDataTool.getIntConvert("escort", monsterInfoData, 0) > 0);
                stats.setPartyBonus(GameConstants.getPartyPlayHP(mobId) > 0 || MapleDataTool.getIntConvert("partyBonusMob", monsterInfoData, 0) > 0);
                stats.setPartyBonusRate(MapleDataTool.getIntConvert("partyBonusR", monsterInfoData, 0));
                if (mobStringData.getChildByPath(String.valueOf(mobId)) != null) {
                    stats.setName(MapleDataTool.getString("name", mobStringData.getChildByPath(String.valueOf(mobId)), "MISSINGNO"));
                }
                stats.setBuffToGive(MapleDataTool.getIntConvert("buff", monsterInfoData, -1));
                stats.setChange(MapleDataTool.getIntConvert("changeableMob", monsterInfoData, 0) > 0);
                stats.setFriendly(MapleDataTool.getIntConvert("damagedByMob", monsterInfoData, 0) > 0);
                stats.setNoDoom(MapleDataTool.getIntConvert("noDoom", monsterInfoData, 0) > 0);
                stats.setPublicReward(MapleDataTool.getIntConvert("publicReward", monsterInfoData, 0) > 0);
                stats.setCP((byte)MapleDataTool.getIntConvert("getCP", monsterInfoData, 0));
                stats.setPoint(MapleDataTool.getIntConvert("point", monsterInfoData, 0));
                stats.setDropItemPeriod(MapleDataTool.getIntConvert("dropItemPeriod", monsterInfoData, 0));
                stats.setPhysicalAttack(MapleDataTool.getIntConvert("PADamage", monsterInfoData, 0));
                stats.setUserCount(MapleDataTool.getIntConvert("userCount", monsterInfoData, 0));
                stats.setMagicAttack(MapleDataTool.getIntConvert("MADamage", monsterInfoData, 0));
                stats.setPDRate(MapleDataTool.getIntConvert("PDRate", monsterInfoData, 0));
                stats.setMDRate(MapleDataTool.getIntConvert("MDRate", monsterInfoData, 0));
                stats.setAcc(MapleDataTool.getIntConvert("acc", monsterInfoData, 0));
                stats.setEva(MapleDataTool.getIntConvert("eva", monsterInfoData, 0));
                stats.setSummonType((byte)MapleDataTool.getIntConvert("summonType", monsterInfoData, 0));
                stats.setHpLinkMob(MapleDataTool.getIntConvert("HpLinkMob", monsterInfoData, 0));
                if (mobId == 8880512) {
                    stats.setSummonType((byte)1);
                }
                stats.setCategory((byte)MapleDataTool.getIntConvert("category", monsterInfoData, 0));
                stats.setSpeed(MapleDataTool.getIntConvert("speed", monsterInfoData, 0));
                stats.setPushed(MapleDataTool.getIntConvert("pushed", monsterInfoData, 0));
                stats.setRemoveOnMiss(MapleDataTool.getIntConvert("removeOnMiss", monsterInfoData, 0) > 0);
                stats.setSkeleton(MapleDataTool.getIntConvert("skeleton", monsterInfoData, 0) > 0);
                stats.setInvincible(MapleDataTool.getIntConvert("invincible", monsterInfoData, 0) > 0);
                stats.setSmartPhase(MapleDataTool.getIntConvert("smartPhase", monsterInfoData, 0));
                stats.setIgnoreMoveImpact(MapleDataTool.getIntConvert("ignoreMoveImpact", monsterInfoData, 0) > 0);
                stats.setRewardSprinkle(MapleDataTool.getIntConvert("rewardSprinkle", monsterInfoData, 0) > 0);
                stats.setRewardSprinkleCount(MapleDataTool.getIntConvert("rewardSprinkleCount", monsterInfoData, 0));
                stats.setRewardSprinkleSpeed(MapleDataTool.getIntConvert("rewardSprinkleSpeed", monsterInfoData, 0));
                stats.setDefenseMob(MapleDataTool.getIntConvert("defenseMob", monsterInfoData, 0) == 1);
                MapleData special = monsterInfoData.getChildByPath("coolDamage");
                if (special != null) {
                    int coolDmg = MapleDataTool.getIntConvert("coolDamage", monsterInfoData);
                    int coolProb = MapleDataTool.getIntConvert("coolDamageProb", monsterInfoData, 0);
                    stats.setCool(new Pair<Integer, Integer>(coolDmg, coolProb));
                }
                if ((special = monsterInfoData.getChildByPath("loseItem")) != null) {
                    for (MapleData liData : special.getChildren()) {
                        stats.addLoseItem(new loseItem(MapleDataTool.getInt(liData.getChildByPath("id")), (byte)MapleDataTool.getInt(liData.getChildByPath("prop")), (byte)MapleDataTool.getInt(liData.getChildByPath("x"))));
                    }
                }
                if ((selfd = monsterInfoData.getChildByPath("selfDestruction")) != null) {
                    stats.setSelfDHP(MapleDataTool.getIntConvert("hp", selfd, 0));
                    stats.setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", selfd, stats.getRemoveAfter()));
                    stats.setSelfD((byte)MapleDataTool.getIntConvert("action", selfd, -1));
                } else {
                    stats.setSelfD((byte)-1);
                }
                MapleData patrol = monsterInfoData.getChildByPath("patrol");
                if (patrol != null) {
                    stats.setPatrol(true);
                    stats.setPatrolRange(MapleDataTool.getInt("range", patrol, 0));
                    stats.setPatrolDetectX(MapleDataTool.getInt("detectX", patrol, 0));
                    stats.setPatrolSenseX(MapleDataTool.getInt("senseX", patrol, 0));
                }
                MapleData firstAttackData = monsterInfoData.getChildByPath("firstAttack");
                int firstAttack = 0;
                if (firstAttackData != null) {
                    firstAttack = firstAttackData.getType() == MapleDataType.FLOAT ? Math.round(MapleDataTool.getFloat(firstAttackData)) : MapleDataTool.getInt(firstAttackData);
                }
                stats.setFirstAttack(firstAttack > 0);
                if (stats.isBoss() || MapleLifeFactory.isDmgSponge(mobId)) {
                    if (monsterInfoData.getChildByPath("hpTagColor") == null || monsterInfoData.getChildByPath("hpTagBgcolor") == null) {
                        stats.setTagColor(0);
                        stats.setTagBgColor(0);
                    } else {
                        stats.setTagColor(MapleDataTool.getIntConvert("hpTagColor", monsterInfoData));
                        stats.setTagBgColor(MapleDataTool.getIntConvert("hpTagBgcolor", monsterInfoData));
                    }
                }
                if ((banishData = monsterInfoData.getChildByPath("ban")) != null) {
                    for (MapleData d : banishData.getChildByPath("banMap").getChildren()) {
                        MapleData banMsgData = banishData.getChildByPath("banMsg");
                        stats.addBanishInfo(new BanishInfo(banMsgData == null ? null : MapleDataTool.getString(banMsgData), MapleDataTool.getInt("field", d, -1), MapleDataTool.getString("portal", d, "sp")));
                    }
                }
                if ((reviveInfo = monsterInfoData.getChildByPath("revive")) != null) {
                    LinkedList<Integer> revives = new LinkedList<Integer>();
                    for (Object bdata : reviveInfo) {
                        revives.add(MapleDataTool.getInt((MapleData)bdata));
                    }
                    stats.setRevives(revives);
                }
                if ((mobZoneInfo = monsterInfoData.getChildByPath("mobZone")) != null) {
                    LinkedList<Pair<Point, Point>> mobZone = new LinkedList<Pair<Point, Point>>();
                    for (MapleData bdata : mobZoneInfo) {
                        MapleData ltData = bdata.getChildByPath("lt");
                        MapleData rbData = bdata.getChildByPath("rb");
                        if (ltData == null || rbData == null) continue;
                        mobZone.add(new Pair<Point, Point>((Point)ltData.getData(), (Point)rbData.getData()));
                    }
                    stats.setMobZone(mobZone);
                }
                MapleData trans = monsterInfoData.getChildByPath("trans");
                MapleMonsterStats.TransMobs transMobs = null;
                if (trans != null) {
                    ArrayList<Integer> mobids = new ArrayList<Integer>();
                    ArrayList<Pair<Integer, Integer>> arrayList = new ArrayList<Pair<Integer, Integer>>();
                    if (trans.getChildByPath("0") != null) {
                        mobids.add(MapleDataTool.getInt(trans.getChildByPath("0"), -1));
                    }
                    if (trans.getChildByPath("1") != null) {
                        mobids.add(MapleDataTool.getInt(trans.getChildByPath("1"), -1));
                    }
                    int time = MapleDataTool.getInt(trans.getChildByPath("time"), 0);
                    int cooltime = MapleDataTool.getInt(trans.getChildByPath("cooltime"), 0);
                    int hpTriggerOn = MapleDataTool.getInt(trans.getChildByPath("hpTriggerOn"), 0);
                    int hpTriggerOff = MapleDataTool.getInt(trans.getChildByPath("hpTriggerOff"), 0);
                    int withMob = MapleDataTool.getInt(trans.getChildByPath("withMob"), -1);
                    if (trans.getChildByPath("skill") != null) {
                        for (MapleData data : trans.getChildByPath("skill")) {
                            arrayList.add(new Pair<Integer, Integer>(MapleDataTool.getInt("skill", data, 0), MapleDataTool.getInt("level", data, 0)));
                        }
                    }
                    transMobs = new MapleMonsterStats.TransMobs(mobids, arrayList, time, cooltime, hpTriggerOn, hpTriggerOff, withMob);
                }
                stats.setTransMobs(transMobs);
                MapleData monsterSkillData = monsterInfoData.getChildByPath("skill");
                if (monsterSkillData != null) {
                    int i = 0;
                    ArrayList<Triple<Integer, Integer, Integer>> skills = new ArrayList<Triple<Integer, Integer, Integer>>();
                    while (monsterSkillData.getChildByPath(Integer.toString(i)) != null) {
                        skills.add(new Triple<Integer, Integer, Integer>(MapleDataTool.getInt(i + "/skill", monsterSkillData, 0), MapleDataTool.getInt(i + "/level", monsterSkillData, 0), MapleDataTool.getInt(i + "/skillAfter", monsterSkillData, 0)));
                        ++i;
                    }
                    stats.setSkills(skills);
                }
                if ((monsterHitPartsToSlot = monsterData.getChildByPath("HitParts")) != null && monsterHitPartsToSlot.getChildren().size() > 0) {
                    for (MapleData d : monsterHitPartsToSlot) {
                        int n = 0;
                        Iterator ii = d.iterator();
                        if (ii.hasNext()) {
                            n = MapleDataTool.getInt("durability", (MapleData)ii.next(), 0);
                        }
                        stats.getHitParts().put(d.getName(), n);
                    }
                }
                MapleLifeFactory.decodeElementalString(stats, MapleDataTool.getString("elemAttr", monsterInfoData, ""));
                int link = MapleDataTool.getIntConvert("link", monsterInfoData, 0);
                stats.setLink(link);
                if (link != 0) {
                    monsterData = data.getData(StringUtil.getLeftPaddedStr(link + ".img", '0', 11));
                }
                if (monsterData != null) {
                    for (MapleData idata : monsterData) {
                        if (idata.getName().equals("info")) continue;
                        int delay = 0;
                        for (MapleData pic : idata.getChildren()) {
                            delay += MapleDataTool.getIntConvert("delay", pic, 0);
                        }
                        stats.setAnimationTime(idata.getName(), delay);
                    }
                    int i = 0;
                    while (true) {
                        String[] info = new String[]{"attack" + i + "/info", "info/attack/" + i};
                        MobAttackInfo ret = null;
                        for (String s : info) {
                            MapleData attackData = monsterData.getChildByPath(s);
                            if (attackData == null) continue;
                            if (ret == null) {
                                ret = new MobAttackInfo();
                            }
                            ret.setDeadlyAttack(attackData.getChildByPath("deadlyAttack") != null);
                            ret.setMpBurn(MapleDataTool.getInt("mpBurn", attackData, 0));
                            int diseaseSkill = MapleDataTool.getInt("disease", attackData, 0);
                            int diseaseLevel = MapleDataTool.getInt("level", attackData, 0);
                            ret.setDiseaseSkill(diseaseSkill);
                            ret.setDiseaseLevel(diseaseLevel);
                            ret.setMpCon(MapleDataTool.getInt("conMP", attackData, 0));
                            ret.attackAfter = MapleDataTool.getInt("attackAfter", attackData, 0);
                            ret.PADamage = MapleDataTool.getInt("PADamage", attackData, 0);
                            ret.MADamage = MapleDataTool.getInt("MADamage", attackData, 0);
                            ret.magic = MapleDataTool.getInt("magic", attackData, 0) > 0;
                            boolean bl = ret.isElement = attackData.getChildByPath("elemAttr") != null;
                            if (attackData.getChildByPath("range") == null) continue;
                            ret.range = MapleDataTool.getInt("range/r", attackData, 0);
                            if (attackData.getChildByPath("range/lt") == null || attackData.getChildByPath("range/rb") == null) continue;
                            ret.lt = (Point)attackData.getChildByPath("range/lt").getData();
                            ret.rb = (Point)attackData.getChildByPath("range/rb").getData();
                        }
                        if (ret == null) break;
                        stats.addMobAttack(ret);
                        ++i;
                    }
                }
                if ((bodyDisease = MapleDataTool.getInt("bodyDisease", monsterInfoData, 0)) > 0) {
                    stats.setBodyDisease(new Pair<Integer, Integer>(bodyDisease, MapleDataTool.getInt("bodyDiseaseLevel", monsterInfoData, 1)));
                }
                int hpdisplaytype = -1;
                if (stats.getTagColor() > 0) {
                    hpdisplaytype = 0;
                } else if (stats.isFriendly()) {
                    hpdisplaytype = 1;
                } else if (mobId >= 0x8DE8D8 && mobId <= 9300215) {
                    hpdisplaytype = 2;
                } else if (!stats.isBoss() || mobId == 9410066 || stats.isPartyBonus()) {
                    hpdisplaytype = 3;
                }
                stats.setHPDisplayType((byte)hpdisplaytype);
                monsterStats.put(mobId, stats);
            }
            catch (Exception e) {
                log.error("getMonsterStats error:" + mobId, e);
                return null;
            }
        }
        return stats;
    }

    public static void decodeElementalString(MapleMonsterStats stats, String elemAttr) {
        for (int i = 0; i < elemAttr.length(); i += 2) {
            stats.setEffectiveness(Element.getFromChar(elemAttr.charAt(i)), ElementalEffectiveness.getByNumber(Integer.valueOf(String.valueOf(elemAttr.charAt(i + 1)))));
        }
    }

    private static boolean isDmgSponge(int mid) {
        switch (mid) {
            case 8810018: 
            case 0x866E86: 
            case 8810119: 
            case 0x866E88: 
            case 8810121: 
            case 8810122: 
            case 8820009: 
            case 8820010: 
            case 8820011: 
            case 8820012: 
            case 8820013: 
            case 8820014: 
            case 8820108: 
            case 8820109: 
            case 8820110: 
            case 8820111: 
            case 8820112: 
            case 8820113: 
            case 8820114: 
            case 8820300: 
            case 8820301: 
            case 8820302: 
            case 8820303: 
            case 8820304: {
                return true;
            }
        }
        return false;
    }

    public static MapleNPC getNPC(int nid, int mapid) {
        MapleData move;
        String name = MapleLifeFactory.getNpcName(nid);
        if (name == null) {
            return null;
        }
        MapleNPC npc = new MapleNPC(nid, name, mapid);
        StringBuilder sb = new StringBuilder(String.valueOf(nid)).append(".img");
        while (sb.length() < 11) {
            sb.insert(0, '0');
        }
        MapleData data = npcData.getData(sb.toString());
        if (data == null) {
            System.err.println("讀取NPC資料出錯, img檔案" + String.valueOf(sb));
            return npc;
        }
        MapleData info = data.getChildByPath("info");
        if (info != null) {
            npc.setMove(MapleDataTool.getInt(info.getChildByPath("forceMove"), 0) > 0);
        }
        if ((move = data.getChildByPath("move")) != null) {
            npc.setMove(true);
        }
        return npc;
    }

    public static Map<Integer, String> getNpcNames() {
        return npcNames;
    }

    public static String getNpcName(int nid) {
        return npcNames.get(nid);
    }

    public static int getRandomNPC() {
        int ret = 0;
        ArrayList<Integer> vals = new ArrayList<Integer>(npcNames.keySet());
        while (ret <= 0) {
            ret = (Integer)vals.get(Randomizer.nextInt(vals.size()));
            if (!npcNames.get(ret).contains("MISSINGNO")) continue;
            ret = 0;
        }
        return ret;
    }

    public static String getNpcScriptName(int npcId) {
        return npcScriptNames.getOrDefault(npcId, null);
    }

    public static boolean isNpcShop(int npcId) {
        return npcShops.contains(npcId);
    }

    public static boolean isNpcTrunk(int npcId) {
        return npcTrunks.contains(npcId);
    }

    @Generated
    public static List<Integer> getNpcTrunks() {
        return npcTrunks;
    }

    static {
        mobStrings = new HashMap<Integer, String>();
    }
    public static class loseItem {

        private final int id;
        private final byte chance;
        private final byte x;

        private loseItem(int id, byte chance, byte x) {
            this.id = id;
            this.chance = chance;
            this.x = x;
        }

        public int getId() {
            return id;
        }

        public byte getChance() {
            return chance;
        }

        public byte getX() {
            return x;
        }
    }
}

