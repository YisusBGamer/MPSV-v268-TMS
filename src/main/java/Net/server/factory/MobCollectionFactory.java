/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.field.fieldeffect.FieldEffect
 *  connection.packet.FieldPacket
 */
package Net.server.factory;

import Client.MapleCharacter;
import Net.server.collection.MobCollectionGroup;
import Net.server.collection.MobCollectionRecord;
import Net.server.collection.MobCollectionReward;
import Net.server.collection.MonsterCollection;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Packet.MaplePacketCreator;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataTool;
import SwordieX.field.fieldeffect.FieldEffect;
import connection.packet.FieldPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tools.Pair;
import tools.Randomizer;

public class MobCollectionFactory {
    private static final Map<Integer, MobCollectionRecord> MobCollectionRecordData = new HashMap<Integer, MobCollectionRecord>();
    private static final Map<Integer, List<MonsterCollection>> MobCollectionData = new HashMap<Integer, List<MonsterCollection>>();
    private static final Map<String, MonsterCollection> MobKeyData = new HashMap<String, MonsterCollection>();
    private static final Map<Integer, Map<Integer, MonsterCollection>> hL = new HashMap<Integer, Map<Integer, MonsterCollection>>();
    private static final Map<Integer, Map<Integer, List<Pair<Integer, Integer>>>> ExplorationRewardIcons = new HashMap<Integer, Map<Integer, List<Pair<Integer, Integer>>>>();

    public static void init(MapleData mapleData) {
        for (MapleData data : mapleData) {
            if (!data.getName().matches("^\\d+$")) continue;
            int no = Integer.valueOf(data.getName());
            MobCollectionRecord p = new MobCollectionRecord();
            p.recordID = MapleDataTool.getInt("info/recordID", data, 0);
            MapleData t = data.getChildByPath("info/clearQuest");
            if (t != null) {
                for (MapleData c1302 : t) {
                    if (c1302.getName().length() >= 2) continue;
                    p.rewards.add(new Pair<Integer, Integer>(MapleDataTool.getInt("clearCount", c1302, 0), MapleDataTool.getInt("rewardID", c1302, 0)));
                }
            }
            for (MapleData mobCollectionInfoData : data) {
                if (!mobCollectionInfoData.getName().matches("^\\d+$")) continue;
                MobCollectionReward mbr = new MobCollectionReward();
                int no2 = Integer.valueOf(mobCollectionInfoData.getName());
                mbr.recordID = MapleDataTool.getInt("info/recordID", mobCollectionInfoData, 0);
                mbr.rewardCount = MapleDataTool.getInt("info/rewardCount", mobCollectionInfoData, 1);
                mbr.rewardID = MapleDataTool.getInt("info/rewardID", mobCollectionInfoData, 0);
                for (MapleData mobCollectionGroupData : mobCollectionInfoData.getChildByPath("group")) {
                    MobCollectionGroup mobCollectionGroup = new MobCollectionGroup();
                    int groupId = Integer.valueOf(mobCollectionGroupData.getName());
                    mobCollectionGroup.exploraionCycle = MapleDataTool.getInt("exploraionCycle", mobCollectionGroupData, 0);
                    mobCollectionGroup.exploraionReward = MapleDataTool.getInt("exploraionReward", mobCollectionGroupData, 0);
                    mobCollectionGroup.recordID = MapleDataTool.getInt("recordID", mobCollectionGroupData, 0);
                    mobCollectionGroup.rewardID = MapleDataTool.getInt("rewardID", mobCollectionGroupData, 0);
                    for (MapleData mobCollectionData : mobCollectionGroupData.getChildByPath("mob")) {
                        int type;
                        Map<Integer, MonsterCollection> map;
                        MonsterCollection mobCollection = new MonsterCollection();
                        int intValue4 = Integer.valueOf(mobCollectionData.getName());
                        int id = MapleDataTool.getInt("id", mobCollectionData, 0);
                        List<MonsterCollection> list = MobCollectionData.get(id);
                        if (list == null) {
                            list = new ArrayList<MonsterCollection>();
                        }
                        if ((map = hL.get(type = MapleDataTool.getInt("type", mobCollectionData, 0))) == null) {
                            map = new HashMap<Integer, MonsterCollection>();
                            hL.put(type, map);
                        }
                        MapleDataTool.getInt("starRank", mobCollectionData, 0);
                        String eliteName = MapleDataTool.getString("eliteName", mobCollectionData, "");
                        mobCollection.collectionId = no;
                        mobCollection.bP = no2;
                        mobCollection.groupId = groupId;
                        mobCollection.g0 = intValue4;
                        mobCollection.mobId = id;
                        mobCollection.eliteName = eliteName;
                        mobCollection.type = type;
                        mobCollection.me = 1L << 31 - intValue4 * 3 % 32;
                        mobCollection.gZ = (int)Math.floor((double)intValue4 * 3.0 / 32.0);
                        mobCollection.hj = no2 + 100 * (no + 1000);
                        mobCollectionGroup.mobCollections.put(intValue4, mobCollection);
                        list.add(mobCollection);
                        map.put(id, mobCollection);
                        MobKeyData.put(mobCollection.getMobkey(), mobCollection);
                        MobCollectionData.put(id, list);
                    }
                    mbr.rewardGroup.put(groupId, mobCollectionGroup);
                }
                p.mobCollectionRewards.put(no2, mbr);
            }
            MobCollectionRecordData.put(no, p);
        }
        Optional.ofNullable(mapleData.getChildByPath("ExplorationRewardIcon")).ifPresent(d -> {
            for (MapleData data : d) {
                int intValue5 = Integer.valueOf(data.getName());
                HashMap hashMap = new HashMap();
                for (MapleData c1307 : data) {
                    ArrayList<Pair<Integer, Integer>> list2 = new ArrayList<Pair<Integer, Integer>>();
                    int intValue6 = Integer.valueOf(c1307.getName());
                    for (MapleData c1308 : c1307) {
                        list2.add(new Pair<Integer, Integer>(MapleDataTool.getInt("item", c1308, 0), MapleDataTool.getInt("count", c1308, 0)));
                    }
                    hashMap.put(intValue6, list2);
                }
                ExplorationRewardIcons.put(intValue5, hashMap);
            }
        });
    }

    public static Map<Integer, Map<Integer, List<Pair<Integer, Integer>>>> getExplorationRewardIcons() {
        return ExplorationRewardIcons;
    }

    public static Map<Integer, MobCollectionRecord> getMobCollectionData() {
        return MobCollectionRecordData;
    }

    public static int getIdByGrade(int n) {
        switch (n) {
            case 0: {
                return 20;
            }
            case 1: {
                return 21;
            }
            case 2: {
                return 22;
            }
            case 3: {
                return 23;
            }
            case 4: {
                return 24;
            }
        }
        return 0;
    }

    public static int getCountByGrade(int n) {
        switch (n) {
            case 0: 
            case 1: {
                return 0;
            }
            case 2: {
                return 150;
            }
            case 3: {
                return 300;
            }
            case 4: {
                return 600;
            }
        }
        return -1;
    }

    public static void tryCollect(MapleCharacter player, MapleMonster monster) {
        List<MonsterCollection> list;
        if (!player.getInfoQuest(18821).isEmpty()) {
            player.updateInfoQuest(18821, null);
        }
        if ((list = MobCollectionData.get(monster.getId())) != null) {
            boolean isBoss = monster.getStats().isBoss();
            for (MonsterCollection mobCollection : list) {
                boolean b = false;
                if (!mobCollection.eliteName.isEmpty()) {
                    if (monster.getEliteMobActive().isEmpty()) {
                        return;
                    }
                    for (int integer : monster.getEliteMobActive()) {
                        if (!MapleLifeFactory.getEliteMonEff(integer).equals(mobCollection.eliteName)) continue;
                        b = true;
                        break;
                    }
                }
                if (mobCollection.type > 2 && isBoss) {
                    b = true;
                }
                if (Randomizer.nextInt(10000) > 80 || player.getCheatTracker().inMapAttackMinutes < 3 && !b) continue;
                MobCollectionFactory.collectionGet(player, mobCollection);
            }
        }
    }

    public static void doneCollection(MapleCharacter player) {
        MobCollectionData.values().forEach(list -> list.forEach(mobCollection -> MobCollectionFactory.collectionGet(player, mobCollection)));
    }

    public static void registerMobCollection(MapleCharacter chr, int n) {
        List<MonsterCollection> list = MobCollectionData.get(n);
        if (list != null) {
            list.forEach(mobCollection -> MobCollectionFactory.collectionGet(chr, mobCollection));
        }
    }

    private static void collectionGet(MapleCharacter player, MonsterCollection mobCollection) {
        int gz = mobCollection.gZ;
        long me = mobCollection.me;
        long[] c = MobCollectionFactory.getCollectionFlag(player, mobCollection.hj, mobCollection.bP);
        if ((me & c[gz]) == 0L) {
            int n = gz;
            c[n] = c[n] | me;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; ++i) {
                Object s = Long.toHexString(c[i]);
                for (int j = ((String)s).length(); j < 8; ++j) {
                    s = "0" + (String)s;
                }
                sb.append((String)s);
            }
            int n2 = 0;
            String countinfo = player.getWorldShareInfo(18821, "count");
            if (countinfo != null && countinfo.length() > 0) {
                try {
                    n2 = Integer.parseInt(countinfo);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            player.updateWorldShareInfo(18821, "count", String.valueOf(n2));
            player.updateWorldShareInfo(18821, "lc", mobCollection.getMobkey());
            player.updateMobCollection(mobCollection.hj, String.valueOf(mobCollection.bP), sb.toString());
            player.send(MaplePacketCreator.showMobCollectionComplete(13, null, 0, 0));
            player.send(FieldPacket.fieldEffect((FieldEffect)FieldEffect.getFieldEffectFromWz((String)"Effect/BasicEff.img/monsterCollectionGet", (int)0)));
            player.send(MaplePacketCreator.monsterBookMessage(String.valueOf(mobCollection) + " 已經新增入怪物收藏品中了。"));
        }
    }

    public static void handleRandCollection(MapleCharacter player, int n) {
        Map<Integer, MonsterCollection> map = hL.get(n);
        if (map != null && !map.isEmpty()) {
            MonsterCollection[] array = map.values().toArray(new MonsterCollection[0]);
            MobCollectionFactory.collectionGet(player, array[Randomizer.nextInt(array.length)]);
        }
    }

    public static boolean checkMobCollection(MapleCharacter player, int n) {
        Iterator<MonsterCollection> iterator;
        List<MonsterCollection> list = MobCollectionData.get(n);
        return list != null && (iterator = list.iterator()).hasNext() && MobCollectionFactory.checkMobCollection(player, iterator.next());
    }

    public static boolean checkMobCollection(MapleCharacter player, String s) {
        MonsterCollection mobCollection = MobKeyData.get(s);
        return mobCollection != null && MobCollectionFactory.checkMobCollection(player, mobCollection);
    }

    private static boolean checkMobCollection(MapleCharacter player, MonsterCollection mobCollection) {
        return mobCollection.type != 8 && (mobCollection.me & MobCollectionFactory.getCollectionFlag(player, mobCollection.hj, mobCollection.bP)[mobCollection.gZ]) != 0L;
    }

    public static boolean gainCollectionReward(MapleCharacter player, MobCollectionGroup mobCollectionGroup) {
        boolean b = true;
        Iterator<MonsterCollection> iterator = mobCollectionGroup.mobCollections.values().iterator();
        while (iterator.hasNext()) {
            MonsterCollection mc;
            MonsterCollection mobCollection = mc = iterator.next();
            if (mobCollection.type == 8 || (mobCollection.me & MobCollectionFactory.getCollectionFlag(player, mobCollection.hj, mobCollection.bP)[mobCollection.gZ]) != 0L) continue;
            b = false;
            break;
        }
        return b && mobCollectionGroup.mobCollections.size() > 0;
    }

    private static long[] getCollectionFlag(MapleCharacter player, int n, int i) {
        String mobCollection = player.getMobCollection(n, String.valueOf(i));
        long[] array = new long[6];
        if (mobCollection != null && mobCollection.length() == 48) {
            for (i = 0; i < 6; ++i) {
                array[i] = Long.parseLong(mobCollection.substring(i << 3, i + 1 << 3), 16);
            }
        } else {
            for (i = 0; i < 6; ++i) {
                array[i] = 0L;
            }
        }
        return array;
    }

    public static int getMobCollectionStatus(MapleCharacter player, MobCollectionRecord mobCollectionRecord) {
        int n = 0;
        for (MobCollectionReward b1150 : mobCollectionRecord.mobCollectionRewards.values()) {
            for (MobCollectionGroup mobCollectionGroup : b1150.rewardGroup.values()) {
                for (MonsterCollection l1160 : mobCollectionGroup.mobCollections.values()) {
                    if (l1160.type == 8 || (l1160.me & MobCollectionFactory.getCollectionFlag(player, l1160.hj, l1160.bP)[l1160.gZ]) == 0L) continue;
                    ++n;
                }
            }
        }
        return n;
    }

    public static MonsterCollection getRandomMonsterCollection(MapleCharacter player) {
        MonsterCollection monsterCollection = null;
        ArrayList<MonsterCollection> list = new ArrayList<MonsterCollection>();
        for (List<MonsterCollection> vals : MobCollectionData.values()) {
            for (MonsterCollection mc : vals) {
                if (MobCollectionFactory.checkMobCollection(player, mc) || mc.type >= 8) continue;
                list.add(mc);
            }
        }
        if (!list.isEmpty()) {
            monsterCollection = (MonsterCollection)list.get(Randomizer.nextInt(list.size()));
        }
        return monsterCollection;
    }
}

