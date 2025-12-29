/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.FieldAttackObjInfo
 *  Net.server.maps.MapleMapFactory$QuickMove
 *  Net.server.maps.MapleMapFactory$QuickMoveMap
 *  Net.server.maps.MapleNodes$DirectionInfo
 *  Net.server.maps.MapleNodes$MapleNodeInfo
 *  Net.server.maps.MapleNodes$MapleNodeStopInfo
 *  Net.server.maps.MapleNodes$MaplePlatform
 *  Net.server.maps.TaggedObjRegenInfo
 */
package Net.server.maps;

import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.enums.MapleFieldType;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.server.InitializeServer;
import Net.server.MaplePortal;
import Net.server.life.AbstractLoadedMapleLife;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MapleNPC;
import Net.server.maps.FieldAttackObjInfo;
import Net.server.maps.MapleFoothold;
import Net.server.maps.MapleFootholdTree;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapFactory;
import Net.server.maps.MapleNodes;
import Net.server.maps.MapleQuickMove;
import Net.server.maps.MapleReactor;
import Net.server.maps.MapleReactorFactory;
import Net.server.maps.TaggedObjRegenInfo;
import Net.server.maps.field.ActionBarField;
import Net.server.maps.field.BingoGameField;
import Net.server.maps.field.BossLucidField;
import Net.server.maps.field.BossSerenField;
import Net.server.maps.field.BossWillField;
import Net.server.maps.field.OXQuizField;
import Net.server.maps.field.RJ;
import Net.server.maps.field.RN;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.awt.Point;
import java.awt.Rectangle;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;

/*
 * Exception performing whole class analysis ignored.
 */
public class MapleMapFactory {
    private static final MapleDataProvider source = MapleDataProviderFactory.getMap();
    private final HashMap<Integer, MapleMap> maps = new HashMap();
    private final HashMap<Integer, MapleMap> instanceMap = new HashMap();
    private final ReentrantLock lock = new ReentrantLock();
    private int channel;
    private static final Map<Integer, List<Integer>> linknpcs = new HashMap<Integer, List<Integer>>();
    private static final Map<Integer, String> mapName = new HashMap<Integer, String>();
    private static final Map<Integer, String> mapStreet = new HashMap<Integer, String>();

    public MapleMapFactory(int channel) {
        this.channel = channel;
    }
    // UI.wz\UIWindow2.img\EasyMove\
    public enum QuickMove {

        大亂鬥(0, 9070004, 30, "移動到可以和其他玩家比試實力的大亂鬥區域#c<戰鬥廣場-赤壁>#。\n#c30級以上可以移動"),
        怪物公園(1, 9071003, 20, "和隊員們齊心合力攻略強悍怪物的區域.\n移動到#c<怪物公園>#.\n#c一般怪物公園:  100級以上可參加\n 怪物競技場: 70級 ~ 200級"),
        次元之鏡(2, 9010022, 10, "使用可傳送到組隊任務等各種副本地圖的#c<次元之鏡>#。"),
        自由市場(3, 9000087, 0, "移動到可以跟其他玩家交易的 #c<自由市場>#。"),
        梅斯特鎮(4, 9000088, 35, "可移動到專業技術村#c<梅斯特鎮>#。\n#c等級35以上才能移動"),
        大陸移動碼頭(5, 9000086, 0, "傳送到最近的#c<大陸移動碼頭>#。"), //Boats, Airplanes
        計程車(6, 9000089, 0, "使用#c<計程車>#可將角色移動到附近主要地區。"), //Taxi, Camel
        戴彼得(7, 9010040, 10, ""),
        被派來的藍多普(8, 0, 10, ""),
        被派來的露西亞(9, 0, 10, ""),
        打工(10, 9010041, 30, "獲得打工的酬勞。"),
        末日風暴防具商店(11, 9010047, 30, ""),
        末日風暴武器商店(12, 9010048, 30, ""),
        皇家美髮(13, 9000123, 0, "可以讓比克·艾德華為你修剪一頭帥氣的髮型。"),
        皇家整形(14, 9000124, 0, "可以讓Dr·塑膠洛伊為你進行整型手術。"),
        冬季限量防具商店(15, 9000152, 30, ""),
        冬季限量武器商店(16, 9000153, 30, ""),
        琳(17, 9000366, 30, "能使用高級服務專用金幣跟琳購買道具。"),
        巨商月妙(18, 9001088, 30, ""),
        彌莎(19, 9000226, 10, ""),
        楓之谷拍賣場(20, 9030300, 0, "透過艾格利其可使用楓之谷拍賣場。"),
        布雷妮(21, 9062008, 0, "可跟楓幣商城負責人布雷妮小姐進行對話。"),
        收集器(22, 9001212, 0, "可以和收藏家交易。"),
        蕾雅(23, 2010011, 0, "透過蕾雅可以往#c公會本部<英雄之殿>#移動。"),
        副官MR_潘喬(24, 9010111, 0, "透過副官MR.潘喬可以使用楓之谷聯盟硬幣商店"),
        戰國露西亞(100, 9130033, 30, ""),//戰國商店
        戰國藍多普(101, 9130032, 30, ""),//戰國商店
        初音未來(102, 0, 30, "移動至初音未來合作特設地圖#c<初音未來的演唱會會場>#。"),
        慧拉(104, 9201594, 10, "透過慧拉可拜訪結婚小鎮。"),
        組隊遊戲(105, 0, 30, "可移動到組隊遊戲的特別地區"),
        ;
        private final int _value;
        public final int VALUE, NPC, MIN_LEVEL;
        public final String DESC;

        QuickMove(int value, int npc, int minLevel, String desc) {
            _value = 1 << this.ordinal();
            VALUE = value;
            NPC = npc;
            MIN_LEVEL = minLevel;
            DESC = desc;
        }

        public final int getValue() {
            return _value;
        }

        public final boolean check(int flag) {
            return (flag & _value) != 0;
        }

        public final MapleQuickMove getMapleQuickMove() {
            MapleQuickMove mqm = new MapleQuickMove();
            mqm.VALUE = VALUE;
            mqm.NPC = NPC;
            mqm.MIN_LEVEL = MIN_LEVEL;
            mqm.DESC = DESC;
            return mqm;
        }
    }
    public enum QuickMoveMap {

        弓箭手村(100000000, QuickMove.大陸移動碼頭.getValue() | QuickMove.計程車.getValue()),
        魔法森林(101000000, QuickMove.大陸移動碼頭.getValue() | QuickMove.計程車.getValue()),
        勇士之村(102000000, QuickMove.大陸移動碼頭.getValue() | QuickMove.計程車.getValue()),
        墮落城市(103000000, QuickMove.大陸移動碼頭.getValue() | QuickMove.計程車.getValue()),
        維多利亞港(104000000, QuickMove.大陸移動碼頭.getValue() | QuickMove.計程車.getValue()),
        奇幻村(105000000, QuickMove.大陸移動碼頭.getValue() | QuickMove.計程車.getValue()),
        鯨魚號(120000100, QuickMove.大陸移動碼頭.getValue() | QuickMove.計程車.getValue()),
        耶雷弗(130000000, QuickMove.大陸移動碼頭.getValue()),
        瑞恩(140000000, QuickMove.大陸移動碼頭.getValue()),
        天空之城(200000000, QuickMove.大陸移動碼頭.getValue()),
        冰原雪域(211000000, 0),
        玩具城(220000000, QuickMove.大陸移動碼頭.getValue()),
        地球防禦本部(221000000, QuickMove.大陸移動碼頭.getValue()),
        童話村(222000000, QuickMove.大陸移動碼頭.getValue()),
        水之都(230000000, 0),
        神木村(240000000, QuickMove.大陸移動碼頭.getValue()),
        克里提亞斯(241000100, 0),
        桃花仙境(250000000, QuickMove.大陸移動碼頭.getValue()),
        靈藥幻境(251000000, 0),
        納希沙漠(260000000, QuickMove.大陸移動碼頭.getValue()),
        瑪迦提亞(261000000, 0),
        埃德爾斯坦(310000000, QuickMove.大陸移動碼頭.getValue()),
        萬神殿(400000000, 0),
        天堂(310070000, 0),
        被遺棄的露營地(105300000, 0),
        野蠻之星(402000000, 0),
        無名村落(450001000, 0),
        反轉城市(450014050, 0),
        啾啾村(450002000, 0),
        嚼嚼村落(450015060, 0),
        拉契爾恩(450003000, 0),
        魔菈斯(450006130, 0),
        艾斯佩拉(450007040, 0),
        賽拉斯(450016000, 0),
        月之橋(450009300, 0),
        苦痛迷宮(450011320, 0),
        利曼(450012300, 0),
        賽爾尼溫(410000500, 0),
        阿爾克斯(410003010, 0),
        奧迪溫(410007000, 0),
        ;

        private final int map;
        private final int npc;
        private final int generalNpc =
                QuickMove.怪物公園.getValue()
                        | QuickMove.次元之鏡.getValue()
                        | QuickMove.自由市場.getValue()
                        | QuickMove.梅斯特鎮.getValue()
                        | QuickMove.楓之谷拍賣場.getValue()
                        | QuickMove.布雷妮.getValue()
                        | QuickMove.收集器.getValue()
                        | QuickMove.蕾雅.getValue()
                        | QuickMove.副官MR_潘喬.getValue();

        private QuickMoveMap(int map, int npc) {
            this.map = map;
            this.npc = npc | generalNpc;
        }

        public int getMap() {
            return map;
        }

        public int getNPCFlag() {
            return npc;
        }
    }
    public static void loadAllMapName() {
        MapleData nameData = MapleDataProviderFactory.getString().getData("Map.img");
        for (MapleData mapleData : nameData) {
            for (MapleData data : mapleData) {
                for (MapleData subdata : data) {
                    if (subdata.getName().equalsIgnoreCase("mapName")) {
                        mapName.put(Integer.valueOf(data.getName()), subdata.getData().toString());
                    }
                    if (!subdata.getName().equalsIgnoreCase("streetName")) continue;
                    mapStreet.put(Integer.valueOf(data.getName()), subdata.getData().toString());
                }
            }
        }
    }

    public static String getMapName(int mapid) {
        if (mapName.containsKey(mapid)) {
            return mapName.get(mapid);
        }
        return "";
    }

    public static Map<Integer, String> getMapNames() {
        return mapName;
    }

    public static String getMapStreetName(int mapid) {
        if (mapStreet.containsKey(mapid)) {
            return mapStreet.get(mapid);
        }
        return "";
    }

    public static Map<Integer, String> getMapStreetNames() {
        return mapStreet;
    }

    public static void loadAllLinkNpc() {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            if (InitializeServer.WzSqlName.wz_maplinknpcs.check(con)) {
                SqlTool.queryAndGetList(con, "SELECT * FROM `wz_maplinknpcs`", rs -> {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    for (String npcid : rs.getString("npcids").split(",")) {
                        list.add(Integer.parseInt(npcid));
                    }
                    linknpcs.put(rs.getInt("mapid"), list);
                    return null;
                });
            } else {
                InitializeServer.WzSqlName.wz_maplinknpcs.drop(con);
                SqlTool.update(con, "CREATE TABLE `wz_maplinknpcs` (`mapid` int NOT NULL,`npcids` text NOT NULL,PRIMARY KEY (`mapid`))");
                for (MapleDataDirectoryEntry directoryEntry : source.getRoot().getSubdirectories()) {
                    if (!directoryEntry.getName().equals("Map")) continue;
                    for (MapleDataDirectoryEntry directoryEntry1 : directoryEntry.getSubdirectories()) {
                        if (!directoryEntry1.getName().startsWith("Map")) continue;
                        for (MapleDataFileEntry fileEntry : directoryEntry1.getFiles()) {
                            for (MapleData life : source.getData(directoryEntry.getName() + "/" + directoryEntry1.getName() + "/" + fileEntry.getName())) {
                                if (!life.getName().equals("life")) continue;
                                ArrayList<Integer> npcids = new ArrayList<Integer>();
                                StringBuilder s = new StringBuilder();
                                boolean b = false;
                                for (MapleData mapleData : life) {
                                    String type;
                                    MapleData type1 = mapleData.getChildByPath("type");
                                    if (type1 == null || (type = MapleDataTool.getString(type1)) == null || !type.equals("n")) continue;
                                    int npcid = MapleDataTool.getIntConvert(mapleData.getChildByPath("id"), 0);
                                    npcids.add(npcid);
                                    if (b) {
                                        s.append(",");
                                    } else {
                                        b = true;
                                    }
                                    s.append(npcid);
                                }
                                if (npcids.isEmpty()) continue;
                                int mapid = Integer.valueOf(fileEntry.getName().substring(0, 9));
                                linknpcs.put(mapid, npcids);
                                SqlTool.update(con, "INSERT INTO `wz_maplinknpcs` (`mapid`,`npcids`) VALUES (?,?)", mapid, s.toString());
                            }
                        }
                    }
                }
                InitializeServer.WzSqlName.wz_maplinknpcs.update(con);
            }
            return null;
        });
    }

    public static Map<Integer, List<Integer>> getAllLinkNpc() {
        return linknpcs;
    }

    private static String getMapXMLName(int mapid) {
        String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9);
        mapName = "Map/Map" + mapid / 100000000 + "/" + (String)mapName + ".img";
        return mapName;
    }

    public MapleMap getMap(int mapid) {
        return this.getMap(mapid, true, true, true);
    }

    public MapleMap getMap(int mapid, boolean respawns, boolean npcs) {
        return this.getMap(mapid, respawns, npcs, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleMap getMap(int mapid, boolean respawns, boolean npcs, boolean reactors) {
        if (mapid == 0) {
            return null;
        }
        Integer omapid = mapid;
        MapleMap map = this.maps.get(omapid);
        if (map == null) {
            this.lock.lock();
            try {
                MapleData objtag;
                MapleData mobRate;
                MapleData mapData;
                map = this.maps.get(omapid);
                if (map != null) {
                    MapleMap mapleMap = map;
                    return mapleMap;
                }
                try {
                    mapData = source.getData(MapleMapFactory.getMapXMLName(mapid));
                }
                catch (Exception e) {
                    mapData = null;
                }
                if (mapData == null) {
                    MapleMap e = null;
                    return e;
                }
                int linkMapId = -1;
                MapleData link = mapData.getChildByPath("info/link");
                if (link != null) {
                    linkMapId = MapleDataTool.getIntConvert("info/link", mapData);
                    mapData = source.getData(MapleMapFactory.getMapXMLName(linkMapId));
                }
                float monsterRate = 0.0f;
                if (respawns && (mobRate = mapData.getChildByPath("info/mobRate")) != null) {
                    monsterRate = ((Float)mobRate.getData()).floatValue();
                }
                int fieldType = MapleDataTool.getInt(mapData.getChildByPath("info/fieldType"), 0);
                int returnMapId = MapleDataTool.getInt("info/returnMap", mapData);
                switch (MapleFieldType.getByType(fieldType)) {
                    case FIELDTYPE_SEREN_STAGE1: 
                    case FIELDTYPE_SEREN_STAGE2: {
                        map = new BossSerenField(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    case FIELDTYPE_WILL_DIFFRACTION: 
                    case FIELDTYPE_WILL_MIRRORCAGE: 
                    case FIELDTYPE_WILL_BLOODCAGE: {
                        map = new BossWillField(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    case FIELDTYPE_LUCIDDREAM: 
                    case FIELDTYPE_LUCIDBROKEN: {
                        map = new BossLucidField(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    case FIELDTYPE_CAPTURE_THE_FLAG: {
                        map = new ActionBarField(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    case FIELDTYPE_HUNDREDBINGO: {
                        map = new BingoGameField(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    case FIELDTYPE_HUNDREDOXQUIZ: {
                        map = new OXQuizField(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    case FIELDTYPE_CAPTAINNOMADBATTLE: {
                        map = new RJ(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    case FIELDTYPE_CAPTAINNOMAD: {
                        map = new RN(mapid, this.channel, returnMapId, monsterRate);
                        break;
                    }
                    default: {
                        int freeMarket = mapid % 910000000;
                        map = freeMarket >= 1 && freeMarket <= 22 ? new MapleMap(GameConstants.getOverrideChangeToMap(mapid), this.channel, mapid, monsterRate) : new MapleMap(mapid, this.channel, returnMapId, monsterRate);
                    }
                }
                map.setFieldType(fieldType);
                map.setLevelLimit(MapleDataTool.getInt(mapData.getChildByPath("info/lvLimit"), 1));
                map.setQuestLimit(MapleDataTool.getInt(mapData.getChildByPath("info/qrLimit"), 0));
                map.setBarrier(MapleDataTool.getInt(mapData.getChildByPath("info/barrier"), 0));
                map.setBarrierArc(MapleDataTool.getInt(mapData.getChildByPath("info/barrierArc"), 0));
                map.setBarrierAut(MapleDataTool.getInt(mapData.getChildByPath("info/barrierAut"), 0));
                map.setMapMark(MapleDataTool.getString(mapData.getChildByPath("info/mapMark"), ""));
                this.loadPortals(map, mapData.getChildByPath("portal"));
                map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
                map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
                map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
                map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
                if (mapData.getChildByPath("areaCtrl") != null) {
                    LinkedList<String> allAreaCtrls = new LinkedList<String>();
                    for (MapleData areaRoot : mapData.getChildByPath("areaCtrl")) {
                        allAreaCtrls.add(areaRoot.getName());
                    }
                    map.setAreaControls(allAreaCtrls);
                }
                LinkedList<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
                Point lBound = new Point();
                Point uBound = new Point();
                for (MapleData footRoot : mapData.getChildByPath("foothold")) {
                    Iterator iterator = footRoot.iterator();
                    while (iterator.hasNext()) {
                        MapleData footCat = (MapleData)iterator.next();
                        for (MapleData footHold : footCat) {
                            MapleFoothold fh = new MapleFoothold(new Point(MapleDataTool.getInt(footHold.getChildByPath("x1"), 0), MapleDataTool.getInt(footHold.getChildByPath("y1"), 0)), new Point(MapleDataTool.getInt(footHold.getChildByPath("x2"), 0), MapleDataTool.getInt(footHold.getChildByPath("y2"), 0)), Integer.parseInt(footHold.getName()));
                            fh.setPrev((short)MapleDataTool.getInt(footHold.getChildByPath("prev"), 0));
                            fh.setNext((short)MapleDataTool.getInt(footHold.getChildByPath("next"), 0));
                            if (fh.getX1() < lBound.x) {
                                lBound.x = fh.getX1();
                            }
                            if (fh.getX2() > uBound.x) {
                                uBound.x = fh.getX2();
                            }
                            if (fh.getY1() < lBound.y) {
                                lBound.y = fh.getY1();
                            }
                            if (fh.getY2() > uBound.y) {
                                uBound.y = fh.getY2();
                            }
                            allFootholds.add(fh);
                        }
                    }
                }
                MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
                for (MapleFoothold foothold : allFootholds) {
                    fTree.insert(foothold);
                }
                map.setFootholds(fTree);
                if (map.getTop() == 0) {
                    map.setTop(lBound.y);
                }
                if (map.getBottom() == 0) {
                    map.setBottom(uBound.y);
                }
                if (map.getLeft() == 0) {
                    map.setLeft(lBound.x);
                }
                if (map.getRight() == 0) {
                    map.setRight(uBound.x);
                }
                int bossid = -1;
                String msg = null;
                if (mapData.getChildByPath("info/timeMob") != null) {
                    bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
                    msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
                }
                try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM spawns WHERE mid = ?");
                    ps.setInt(1, omapid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int sqlid = rs.getInt("idd");
                        int sqlf = rs.getInt("f");
                        boolean sqlhide = false;
                        String sqltype = rs.getString("type");
                        int sqlfh = rs.getInt("fh");
                        int sqlcy = rs.getInt("cy");
                        int sqlrx0 = rs.getInt("rx0");
                        int sqlrx1 = rs.getInt("rx1");
                        int sqlx = rs.getInt("x");
                        int sqly = rs.getInt("y");
                        int sqlmobTime = rs.getInt("mobtime");
                        AbstractLoadedMapleLife sqlmyLife = this.loadLife(sqlid, sqlf, sqlhide, sqlfh, sqlcy, sqlrx0, sqlrx1, sqlx, sqly, sqltype, mapid);
                        switch (sqltype) {
                            case "n": {
                                map.addMapObject(sqlmyLife);
                                break;
                            }
                            case "m": {
                                MapleMonster monster = (MapleMonster)sqlmyLife;
                                map.addMonsterSpawn(monster, sqlmobTime, (byte)-1, null);
                            }
                        }
                    }
                }
                catch (SQLException e) {
                    System.out.println("讀取SQL刷Npc和刷新怪物出錯.");
                }
                ArrayList<Point> herbRocks = new ArrayList<Point>();
                short lowestLevel = 200;
                short highestLevel = 0;
                Map<Integer, Set<Integer>> hideinfo = ServerConfig.WORLD_HIDENPCS_MAP;
                for (MapleData life : mapData.getChildByPath("life")) {
                    String type = MapleDataTool.getString(life.getChildByPath("type"), "n");
                    String limited = MapleDataTool.getString("limitedname", life, "");
                    if (!npcs && type.equals("n") || !limited.isEmpty() && limited.contains(map.getMapMark())) continue;
                    String id = MapleDataTool.getString(life.getChildByPath("id"));
                    AbstractLoadedMapleLife myLife = this.loadLife(life, id, type, mapid);
                    if (myLife instanceof MapleMonster && !GameConstants.isNoSpawn(mapid)) {
                        MapleMonster mob = (MapleMonster)myLife;
                        herbRocks.add(map.addMonsterSpawn(mob, MapleDataTool.getInt("mobTime", life, 0), (byte)MapleDataTool.getInt("team", life, -1), mob.getId() == bossid ? msg : null).getPosition());
                        if (mob.getStats().getLevel() > highestLevel && !mob.getStats().isBoss()) {
                            highestLevel = mob.getStats().getLevel();
                        }
                        if (mob.getStats().getLevel() >= lowestLevel || mob.getStats().isBoss()) continue;
                        lowestLevel = mob.getStats().getLevel();
                        continue;
                    }
                    if (!(myLife instanceof MapleNPC) || hideinfo.containsKey(-1) && hideinfo.get(-1).contains(myLife.getId()) || hideinfo.containsKey(mapid) && hideinfo.get(mapid).contains(myLife.getId())) continue;
                    if (!limited.isEmpty() && !ServerConfig.WORLD_LIMITEDNAMES_LIST.contains(limited)) {
                        map.addHideNpc(myLife.getId());
                        continue;
                    }
                    map.addMapObject(myLife);
                }
                this.addAreaBossSpawn(map);
                map.setCreateMobInterval((short)MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), ServerConfig.ServerSpawnMobSec * 1000));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
                map.loadMonsterRate();
                map.setNodes(this.loadNodes(mapid, mapData));
                map.setSpawnPoints(herbRocks);
                if (reactors && mapData.getChildByPath("reactor") != null) {
                    for (MapleData reactor : mapData.getChildByPath("reactor")) {
                        String id = MapleDataTool.getString(reactor.getChildByPath("id"));
                        if (id == null) continue;
                        map.spawnReactor(this.loadReactor(reactor, id, (byte)MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                    }
                }
                map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
                map.setUserEnter(mapid == 993073000 ? "jail" : MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
                if (reactors && herbRocks.size() > 0 && highestLevel >= 30 && map.getFirstUserEnter().equals("") && map.getUserEnter().equals("")) {
                    ArrayList<Integer> allowedSpawn = new ArrayList<Integer>(24);
                    allowedSpawn.add(100011);
                    allowedSpawn.add(200011);
                    if (highestLevel >= 100) {
                        for (int i = 0; i < 10; ++i) {
                            for (int x = 0; x < 4; ++x) {
                                allowedSpawn.add(100000 + i);
                                allowedSpawn.add(200000 + i);
                            }
                        }
                    } else {
                        int i = lowestLevel % 10 > highestLevel % 10 ? 0 : lowestLevel % 10;
                        while (i < highestLevel % 10) {
                            for (int x = 0; x < 4; ++x) {
                                allowedSpawn.add(100000 + i);
                                allowedSpawn.add(200000 + i);
                            }
                            ++i;
                        }
                    }
                    int numSpawn = Randomizer.nextInt(allowedSpawn.size()) / 6;
                    for (int i = 0; i < numSpawn && !herbRocks.isEmpty(); ++i) {
                        int idd = (Integer)allowedSpawn.get(Randomizer.nextInt(allowedSpawn.size()));
                        int theSpawn = Randomizer.nextInt(herbRocks.size());
                        MapleReactor myReactor = new MapleReactor(MapleReactorFactory.getReactor(idd), idd);
                        myReactor.setPosition((Point)herbRocks.get(theSpawn));
                        myReactor.setDelay(idd % 100 == 11 ? 60000 : 5000);
                        map.spawnReactor(myReactor);
                        herbRocks.remove(theSpawn);
                    }
                }
                map.setClock(mapData.getChildByPath("clock") != null);
                map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
                map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
                map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
                map.setPersonalShop(MapleDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0);
                map.setEntrustedFishing(MapleDataTool.getInt(mapData.getChildByPath("info/entrustedFishing"), 0) > 0);
                map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
                map.setDecHP(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
                map.setDecHPInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
                map.setProtectItem(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
                map.setForcedReturnMap(mapid == 0 ? 999999999 : MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
                map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
                map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
                map.setMiniMapOnOff(MapleDataTool.getInt(mapData.getChildByPath("info/miniMapOnOff"), 0) > 0);
                map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0f));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
                map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
                if (mapData.getChildByPath("info/fieldAttackObj") != null) {
                    for (MapleData d : mapData.getChildByPath("info/fieldAttackObj")) {
                        String regenObjTag = MapleDataTool.getString("regenObjTag", d, "");
                        int objid = MapleDataTool.getIntConvert("id", d, -1);
                        int regenTime = MapleDataTool.getIntConvert("regenTime", d, 0);
                        boolean flip = MapleDataTool.getIntConvert("flip", d, 0) > 0;
                        int destroyTime = MapleDataTool.getIntConvert("destroyTime", d, 0);
                        if (objid == -1) continue;
                        map.getFieldAttackObjInfo().add(new FieldAttackObjInfo(regenObjTag, objid, regenTime, flip, destroyTime));
                    }
                }
                if (mapData.getChildByPath("info/taggedObjRegenInfo") != null) {
                    for (MapleData d : mapData.getChildByPath("info/taggedObjRegenInfo")) {
                        TaggedObjRegenInfo info = new TaggedObjRegenInfo();
                        info.setRemoveTime(MapleDataTool.getIntConvert("removeTime", d, 0));
                        info.setTag(MapleDataTool.getString("sTag", d, ""));
                        info.setRegenTime(MapleDataTool.getIntConvert("regenTime", d, 0));
                        info.setFootHoldOffY(MapleDataTool.getIntConvert("footHoldOffY", d, 0));
                        map.getTaggedObjRegenInfo().add(info);
                    }
                }
                if ((objtag = mapData.getChildByPath("5/obj")) != null) {
                    for (MapleData d : objtag) {
                        int idx = Integer.valueOf(d.getName());
                        String s = MapleDataTool.getString(d.getChildByPath("tags"), null);
                        if (s == null) continue;
                        map.getObjTag().put(idx, new Pair<String, Point>(s, new Point(MapleDataTool.getInt(d.getChildByPath("x"), 0), MapleDataTool.getInt(d.getChildByPath("y"), 0))));
                    }
                }
                switch (mapid) {
                    case 450004250: 
                    case 450004550: 
                    case 450004850: {
                        MapleData lucidMapData = mapData.getChildByPath("5/obj");
                        if (lucidMapData == null) break;
                        for (MapleData d : lucidMapData) {
                            String s = MapleDataTool.getString(d.getChildByPath("name"), null);
                            if (s == null) continue;
                            map.getLacheln().put(s, new Point(MapleDataTool.getInt(d.getChildByPath("x"), 0), MapleDataTool.getInt(d.getChildByPath("y"), 0) - 3));
                        }
                        break;
                    }
                    case 280030000: 
                    case 280030100: 
                    case 280030200: {
                        MapleData zakumMapData = mapData.getChildByPath("5/obj");
                        if (zakumMapData == null) break;
                        for (MapleData d : zakumMapData) {
                            String s = MapleDataTool.getString(d.getChildByPath("name"), null);
                            if (s == null) continue;
                            map.getSyncFH().add(new Pair<String, Point>(s, new Point(MapleDataTool.getInt(d.getChildByPath("x"), 0), MapleDataTool.getInt(d.getChildByPath("y"), 0))));
                        }
                        break;
                    }
                }
                this.initDefaultQuickMove(map);
                map.startFieldScript();
                this.maps.put(omapid, map);
            }
            finally {
                this.lock.unlock();
            }
        }
        return map;
    }



    private void initDefaultQuickMove(MapleMap map) {
        if (ServerConfig.QUICK_MOVE_LIST.isEmpty()) {
            map.QUICK_MOVE = new LinkedList<MapleQuickMove>();
            int npcs = 0;
            for (QuickMoveMap quickMoveMap : QuickMoveMap.values()) {
                if (quickMoveMap.getMap() != map.getId()) continue;
                npcs = quickMoveMap.getNPCFlag();
                break;
            }
            for (QuickMove qm : QuickMove.values()) {
                if (qm.check(npcs)) {
                    map.QUICK_MOVE.add(qm.getMapleQuickMove());
                }
            }
        } else {
            map.QUICK_MOVE = ServerConfig.QUICK_MOVE_LIST;
        }
    }

    public void loadQuickMove() {
        for (MapleMap map : this.maps.values()) {
            this.initDefaultQuickMove(map);
        }
    }

    public MapleMap getInstanceMap(int instanceid) {
        return this.instanceMap.get(instanceid);
    }

    public void removeInstanceMap(int instanceid) {
        this.lock.lock();
        try {
            if (this.isInstanceMapLoaded(instanceid)) {
                this.instanceMap.remove(instanceid);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void removeMap(int instanceid) {
        this.lock.lock();
        try {
            if (this.isMapLoaded(instanceid)) {
                this.maps.remove(instanceid);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleMap CreateInstanceMap(int mapid, boolean respawns, boolean npcs, boolean reactors, int instanceid) {
        MapleData mobRate;
        this.lock.lock();
        try {
            if (this.isInstanceMapLoaded(instanceid)) {
                MapleMap mapleMap = this.getInstanceMap(instanceid);
                return mapleMap;
            }
        }
        finally {
            this.lock.unlock();
        }
        MapleData mapData = null;
        try {
            mapData = source.getData(MapleMapFactory.getMapXMLName(mapid));
        }
        catch (Exception e) {
            return null;
        }
        if (mapData == null) {
            return null;
        }
        MapleData link = mapData.getChildByPath("info/link");
        if (link != null) {
            mapData = source.getData(MapleMapFactory.getMapXMLName(MapleDataTool.getIntConvert("info/link", mapData)));
        }
        float monsterRate = 0.0f;
        if (respawns && (mobRate = mapData.getChildByPath("info/mobRate")) != null) {
            monsterRate = ((Float)mobRate.getData()).floatValue();
        }
        MapleMap map = new MapleMap(mapid, this.channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);
        this.loadPortals(map, mapData.getChildByPath("portal"));
        map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
        map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
        map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
        map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
        map.setFieldType(MapleDataTool.getInt(mapData.getChildByPath("info/fieldType"), 0));
        map.setLevelLimit(MapleDataTool.getInt(mapData.getChildByPath("info/lvLimit"), 1));
        map.setQuestLimit(MapleDataTool.getInt(mapData.getChildByPath("info/qrLimit"), 0));
        if (mapData.getChildByPath("areaCtrl") != null) {
            LinkedList<String> allAreaCtrls = new LinkedList<String>();
            for (MapleData areaRoot : mapData.getChildByPath("areaCtrl")) {
                allAreaCtrls.add(areaRoot.getName());
            }
            map.setAreaControls(allAreaCtrls);
        }
        LinkedList<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
        Point lBound = new Point();
        Point uBound = new Point();
        for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            Iterator iterator = footRoot.iterator();
            while (iterator.hasNext()) {
                MapleData footCat = (MapleData)iterator.next();
                for (MapleData footHold : footCat) {
                    MapleFoothold fh = new MapleFoothold(new Point(MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))), new Point(MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))), Integer.parseInt(footHold.getName()));
                    fh.setPrev((short)MapleDataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext((short)MapleDataTool.getInt(footHold.getChildByPath("next")));
                    if (fh.getX1() < lBound.x) {
                        lBound.x = fh.getX1();
                    }
                    if (fh.getX2() > uBound.x) {
                        uBound.x = fh.getX2();
                    }
                    if (fh.getY1() < lBound.y) {
                        lBound.y = fh.getY1();
                    }
                    if (fh.getY2() > uBound.y) {
                        uBound.y = fh.getY2();
                    }
                    allFootholds.add(fh);
                }
            }
        }
        MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
        for (MapleFoothold fh : allFootholds) {
            fTree.insert(fh);
        }
        map.setFootholds(fTree);
        if (map.getTop() == 0) {
            map.setTop(lBound.y);
        }
        if (map.getBottom() == 0) {
            map.setBottom(uBound.y);
        }
        if (map.getLeft() == 0) {
            map.setLeft(lBound.x);
        }
        if (map.getRight() == 0) {
            map.setRight(uBound.x);
        }
        int bossid = -1;
        String msg = null;
        if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM spawns WHERE mid = ?");
            ps.setInt(1, mapid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int sqlid = rs.getInt("idd");
                int sqlf = rs.getInt("f");
                boolean sqlhide = false;
                String sqltype = rs.getString("type");
                int sqlfh = rs.getInt("fh");
                int sqlcy = rs.getInt("cy");
                int sqlrx0 = rs.getInt("rx0");
                int sqlrx1 = rs.getInt("rx1");
                int sqlx = rs.getInt("x");
                int sqly = rs.getInt("y");
                int sqlmobTime = rs.getInt("mobtime");
                AbstractLoadedMapleLife sqlmyLife = this.loadLife(sqlid, sqlf, sqlhide, sqlfh, sqlcy, sqlrx0, sqlrx1, sqlx, sqly, sqltype, mapid);
                if (sqltype.equals("n")) {
                    map.addMapObject(sqlmyLife);
                    continue;
                }
                if (!sqltype.equals("m")) continue;
                MapleMonster monster = (MapleMonster)sqlmyLife;
                map.addMonsterSpawn(monster, sqlmobTime, (byte)-1, null);
            }
        }
        catch (SQLException e) {
            System.out.println("讀取SQL刷Npc和刷新怪物出錯.");
        }
        ArrayList<Point> spawnPoints = new ArrayList<Point>();
        short lowestLevel = 200;
        short highestLevel = 0;
        Map<Integer, Set<Integer>> hideinfo = ServerConfig.WORLD_HIDENPCS_MAP;
        for (MapleData life : mapData.getChildByPath("life")) {
            String type = MapleDataTool.getString(life.getChildByPath("type"), "n");
            String limited = MapleDataTool.getString("limitedname", life, "");
            if (!npcs && type.equals("n") || !limited.isEmpty() && limited.contains(map.getMapMark())) continue;
            String id = MapleDataTool.getString(life.getChildByPath("id"));
            AbstractLoadedMapleLife myLife = this.loadLife(life, id, type, mapid);
            if (myLife instanceof MapleMonster && !GameConstants.isNoSpawn(mapid)) {
                MapleMonster mob = (MapleMonster)myLife;
                int mobTime = MapleDataTool.getInt("mobTime", life, 7000);
                Point spawnPosition = map.addMonsterSpawn(mob, mobTime, (byte)MapleDataTool.getInt("team", life, -1), mob.getId() == bossid ? msg : null).getPosition();
                spawnPoints.add(spawnPosition);
                if (mob.getStats().getLevel() > highestLevel && !mob.getStats().isBoss()) {
                    highestLevel = mob.getStats().getLevel();
                }
                if (mob.getStats().getLevel() >= lowestLevel || mob.getStats().isBoss()) continue;
                lowestLevel = mob.getStats().getLevel();
                continue;
            }
            if (!(myLife instanceof MapleNPC)) continue;
            MapleNPC npc = (MapleNPC)myLife;
            if (hideinfo.containsKey(-1) && hideinfo.get(-1).contains(npc.getId()) || hideinfo.containsKey(mapid) && hideinfo.get(mapid).contains(npc.getId())) continue;
            if (!limited.isEmpty() && !ServerConfig.WORLD_LIMITEDNAMES_LIST.contains(limited)) {
                map.addHideNpc(npc.getId());
                continue;
            }
            map.addMapObject(npc);
        }
        this.addAreaBossSpawn(map);
        map.setCreateMobInterval((short)7000);
        map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
        map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
        map.loadMonsterRate();
        map.setNodes(this.loadNodes(mapid, mapData));
        map.setSpawnPoints(spawnPoints);
        if (reactors && mapData.getChildByPath("reactor") != null) {
            for (MapleData reactor : mapData.getChildByPath("reactor")) {
                String id = MapleDataTool.getString(reactor.getChildByPath("id"));
                if (id == null) continue;
                map.spawnReactor(this.loadReactor(reactor, id, (byte)MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
            }
        }
        map.setClock(MapleDataTool.getInt(mapData.getChildByPath("info/clock"), 0) > 0);
        map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
        map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
        map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
        map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
        map.setDecHP(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
        map.setDecHPr(MapleDataTool.getInt(mapData.getChildByPath("info/decHPr"), 0));
        map.setDecHPInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
        map.setProtectItem(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
        map.setForcedReturnMap(MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
        map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
        map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
        map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
        map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
        map.setFieldScript(MapleDataTool.getString(mapData.getChildByPath("info/fieldScript"), ""));
        map.setMiniMapOnOff(MapleDataTool.getInt(mapData.getChildByPath("info/miniMapOnOff"), 0) > 0);
        map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0f));
        map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
        map.setInstanceId(instanceid);
        this.lock.lock();
        try {
            this.instanceMap.put(instanceid, map);
        }
        finally {
            this.lock.unlock();
        }
        return map;
    }

    public int getLoadedMaps() {
        return this.maps.size();
    }

    public boolean isMapLoaded(int mapId) {
        return this.maps.containsKey(mapId);
    }

    public boolean isInstanceMapLoaded(int instanceid) {
        return this.instanceMap.containsKey(instanceid);
    }

    public void clearLoadedMap() {
        this.lock.lock();
        try {
            this.maps.clear();
        }
        finally {
            this.lock.unlock();
        }
    }

    public List<MapleMap> getAllLoadedMaps() {
        ArrayList<MapleMap> ret = new ArrayList<MapleMap>();
        this.lock.lock();
        try {
            ret.addAll(this.maps.values());
            ret.addAll(this.instanceMap.values());
        }
        finally {
            this.lock.unlock();
        }
        return ret;
    }

    public Collection<MapleMap> getAllMaps() {
        return this.maps.values();
    }

    private AbstractLoadedMapleLife loadLife(MapleData life, String id, String type, int mapid) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(Integer.parseInt(id), type, mapid);
        if (myLife == null) {
            return null;
        }
        myLife.setCy(MapleDataTool.getInt(life.getChildByPath("cy")));
        MapleData dF = life.getChildByPath("f");
        if (dF != null) {
            myLife.setF(MapleDataTool.getInt(dF));
        }
        myLife.setCurrentFh(MapleDataTool.getInt(life.getChildByPath("fh")));
        myLife.setRx0(MapleDataTool.getInt(life.getChildByPath("rx0")));
        myLife.setRx1(MapleDataTool.getInt(life.getChildByPath("rx1")));
        myLife.setPosition(new Point(MapleDataTool.getIntConvert(life.getChildByPath("x")), MapleDataTool.getIntConvert(life.getChildByPath("y"))));
        if (MapleDataTool.getInt("hide", life, 0) == 1 && myLife instanceof MapleNPC) {
            myLife.setHide(true);
        }
        return myLife;
    }

    private AbstractLoadedMapleLife loadLife(int id, int f, boolean hide, int fh, int cy, int rx0, int rx1, int x, int y, String type, int mapid) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(id, type, mapid);
        if (myLife == null) {
            return null;
        }
        myLife.setCy(cy);
        myLife.setF(f);
        myLife.setCurrentFh(fh);
        myLife.setRx0(rx0);
        myLife.setRx1(rx1);
        myLife.setPosition(new Point(x, y));
        myLife.setHide(hide);
        return myLife;
    }

    private MapleReactor loadReactor(MapleData reactor, String id, byte FacingDirection) {
        MapleReactor myReactor = new MapleReactor(MapleReactorFactory.getReactor(Integer.parseInt(id)), Integer.parseInt(id));
        myReactor.setFacingDirection(FacingDirection);
        myReactor.setPosition(new Point(MapleDataTool.getInt(reactor.getChildByPath("x")), MapleDataTool.getInt(reactor.getChildByPath("y"))));
        myReactor.setDelay(MapleDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
        myReactor.setName(MapleDataTool.getString(reactor.getChildByPath("name"), ""));
        return myReactor;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    private void addAreaBossSpawn(MapleMap map) {
        int monsterid = -1;
        int mobtime = -1;
        String msg = null;
        boolean shouldSpawn = true;
        boolean sendWorldMsg = false;
        Point pos1 = null;
        Point pos2 = null;
        Point pos3 = null;
        switch (map.getId()) {
            case 931000500: 
            case 931000502: {
                mobtime = 3600;
                monsterid = 9304005;
                msg = "美洲豹棲息地出現 劍齒豹 ，喜歡此坐騎的狂豹獵人職業可以前往抓捕。";
                pos1 = new Point(-872, -332);
                pos2 = new Point(409, -572);
                pos3 = new Point(-131, 0);
                shouldSpawn = false;
                sendWorldMsg = true;
                break;
            }
            case 931000501: 
            case 931000503: {
                mobtime = 7200;
                monsterid = 9304006;
                msg = "美洲豹棲息地出現 雪豹 ，喜歡此坐騎的狂豹獵人職業可以前往抓捕。";
                pos1 = new Point(-872, -332);
                pos2 = new Point(409, -572);
                pos3 = new Point(-131, 0);
                shouldSpawn = false;
                sendWorldMsg = true;
            }
        }
        if (monsterid > 0) {
            map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(monsterid), pos1, pos2, pos3, mobtime, msg, shouldSpawn, sendWorldMsg);
        }
    }

    private void loadPortals(MapleMap map, MapleData port) {
        if (port == null) {
            return;
        }
        int nextDoorPortal = 128;
        for (MapleData portal : port.getChildren()) {
            MaplePortal myPortal = new MaplePortal(MapleDataTool.getInt(portal.getChildByPath("pt")));
            myPortal.setName(MapleDataTool.getString(portal.getChildByPath("pn")));
            myPortal.setTarget(MapleDataTool.getString(portal.getChildByPath("tn")));
            myPortal.setTargetMapId(MapleDataTool.getInt(portal.getChildByPath("tm")));
            myPortal.setPosition(new Point(MapleDataTool.getInt(portal.getChildByPath("x")), MapleDataTool.getInt(portal.getChildByPath("y"))));
            String script = MapleDataTool.getString("script", portal, null);
            if (script != null && script.equals("")) {
                script = null;
            }
            myPortal.setScriptName(script);
            if (myPortal.getType() == 6) {
                myPortal.setId(nextDoorPortal);
                ++nextDoorPortal;
            } else {
                myPortal.setId(Integer.parseInt(portal.getName()));
            }
            map.addPortal(myPortal);
        }
    }

    private MapleNodes loadNodes(int mapid, MapleData mapData) {
        MapleNodes nodeInfo = new MapleNodes(mapid);
        if (mapData.getChildByPath("nodeInfo") != null) {
            for (MapleData node : mapData.getChildByPath("nodeInfo")) {
                try {
                    if (node.getName().equals("start")) {
                        nodeInfo.setNodeStart(MapleDataTool.getInt((MapleData)node, 0));
                        continue;
                    }
                    ArrayList<Integer> edges = new ArrayList<Integer>();
                    if (node.getChildByPath("edge") != null) {
                        for (MapleData edge : node.getChildByPath("edge")) {
                            edges.add(MapleDataTool.getInt(edge, -1));
                        }
                    }
                    MapleData stopInfoData = node.getChildByPath("stopInfo");
                    MapleNodes.MapleNodeStopInfo stopInfo = null;
                    if (stopInfoData != null) {
                        ArrayList<Pair<String, String>> list4 = new ArrayList<Pair<String, String>>();
                        int b3 = MapleDataTool.getInt(stopInfoData.getChildByPath("stopDuration"), -1);
                        String a = MapleDataTool.getString(stopInfoData.getChildByPath("scriptName"), "");
                        int b4 = MapleDataTool.getInt(stopInfoData.getChildByPath("sayTic"), -1);
                        int b5 = MapleDataTool.getInt(stopInfoData.getChildByPath("chatBalloon"), -1);
                        boolean b6 = MapleDataTool.getInt(stopInfoData.getChildByPath("isWeather"), 0) > 0;
                        boolean b7 = MapleDataTool.getInt(stopInfoData.getChildByPath("isRepeat"), 0) > 0;
                        boolean b8 = MapleDataTool.getInt(stopInfoData.getChildByPath("isRandom"), 0) > 0;
                        MapleData sayInfoData = stopInfoData.getChildByPath("sayInfo");
                        if (sayInfoData != null) {
                            for (MapleData zj4 : sayInfoData) {
                                list4.add(new Pair<String, String>(MapleDataTool.getString(zj4.getChildByPath("say"), ""), MapleDataTool.getString(zj4.getChildByPath("say"), "")));
                            }
                        }
                        stopInfo = new MapleNodes.MapleNodeStopInfo(a, b3, b4, b5, b6, b7, b8, list4);
                    }
                    MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(Integer.parseInt(node.getName()), MapleDataTool.getIntConvert("key", (MapleData)node, 0), MapleDataTool.getIntConvert("x", (MapleData)node, 0), MapleDataTool.getIntConvert("y", (MapleData)node, 0), MapleDataTool.getIntConvert("attr", (MapleData)node, 0), edges, stopInfo);
                    nodeInfo.addNode(mni);
                }
                catch (NumberFormatException edges) {}
            }
            nodeInfo.sortNodes();
        }
        for (int i = 1; i <= 7; ++i) {
            if (mapData.getChildByPath(String.valueOf(i)) == null || mapData.getChildByPath(i + "/obj") == null) continue;
            for (MapleData node : mapData.getChildByPath(i + "/obj")) {
                if (node.getChildByPath("SN_count") != null && node.getChildByPath("speed") != null) {
                    int sn_count = MapleDataTool.getIntConvert("SN_count", node, 0);
                    String name2 = MapleDataTool.getString("name", node, "");
                    int speed = MapleDataTool.getIntConvert("speed", node, 0);
                    if (sn_count <= 0 || speed <= 0 || name2.equals("")) continue;
                    ArrayList<Integer> SN = new ArrayList<Integer>();
                    for (int x = 0; x < sn_count; ++x) {
                        SN.add(MapleDataTool.getIntConvert("SN" + x, node, 0));
                    }
                    MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(name2, MapleDataTool.getIntConvert("start", node, 2), speed, MapleDataTool.getIntConvert("x1", node, 0), MapleDataTool.getIntConvert("y1", node, 0), MapleDataTool.getIntConvert("x2", node, 0), MapleDataTool.getIntConvert("y2", node, 0), MapleDataTool.getIntConvert("r", node, 0), SN);
                    nodeInfo.addPlatform(mni);
                    continue;
                }
                if (node.getChildByPath("tags") == null) continue;
                String name = MapleDataTool.getString("tags", node, "");
                nodeInfo.addFlag(new Pair<>(name, name.endsWith("3") ? 1 : 0)); //idk, no indication in wz

            }
        }
        if (mapData.getChildByPath("area") != null) {
            for (MapleData area : mapData.getChildByPath("area")) {
                int x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
                int y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
                int x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
                int y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
                Rectangle mapArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                nodeInfo.addMapleArea(mapArea);
            }
        }
        if (mapData.getChildByPath("CaptureTheFlag") != null) {
            MapleData mc = mapData.getChildByPath("CaptureTheFlag");
            for (MapleData area : mc) {
                nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("FlagPositionX")), MapleDataTool.getInt(area.getChildByPath("FlagPositionY"))), area.getName().startsWith("Red") ? 0 : 1);
            }
        }
        if (mapData.getChildByPath("directionInfo") != null) {
            MapleData mc = mapData.getChildByPath("directionInfo");
            for (MapleData area : mc) {
                MapleNodes.DirectionInfo di = new MapleNodes.DirectionInfo(Integer.parseInt(area.getName()), MapleDataTool.getInt("x", area, 0), MapleDataTool.getInt("y", area, 0), MapleDataTool.getInt("forcedInput", area, 0) > 0);
                if (area.getChildByPath("EventQ") != null) {
                    for (MapleData event : area.getChildByPath("EventQ")) {
                        di.eventQ.add(MapleDataTool.getString(event));
                    }
                } else {
                    System.out.println("[loadNodes] 地圖: " + mapid + " 沒有找到EventQ.");
                }
                nodeInfo.addDirection(Integer.parseInt(area.getName()), di);
            }
        }
        if (mapData.getChildByPath("monsterCarnival") != null) {
            MapleData mc = mapData.getChildByPath("monsterCarnival");
            if (mc.getChildByPath("mobGenPos") != null) {
                for (MapleData area : mc.getChildByPath("mobGenPos")) {
                    nodeInfo.addMonsterPoint(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y")), MapleDataTool.getInt(area.getChildByPath("fh")), MapleDataTool.getInt(area.getChildByPath("cy")), MapleDataTool.getInt("team", area, -1));
                }
            }
            if (mc.getChildByPath("mob") != null) {
                for (MapleData area : mc.getChildByPath("mob")) {
                    nodeInfo.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")), MapleDataTool.getInt(area.getChildByPath("spendCP")));
                }
            }
            if (mc.getChildByPath("guardianGenPos") != null) {
                for (MapleData area : mc.getChildByPath("guardianGenPos")) {
                    nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y"))), MapleDataTool.getInt("team", area, -1));
                }
            }
            if (mc.getChildByPath("skill") != null) {
                for (MapleData area : mc.getChildByPath("skill")) {
                    nodeInfo.addSkillId(MapleDataTool.getInt(area));
                }
            }
        }
        return nodeInfo;
    }
}

