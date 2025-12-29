/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  Client.inventory.PetFlag
 *  Config.constants.ItemConstants$卷軸
 *  tools.LotteryRandom
 */
package Net.server;

import Client.MapleCharacter;
import Client.MapleTraitType;
import Client.VMatrixOption;
import Client.inventory.EnhanceResultType;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.MapleInventoryIdentifier;
import Client.inventory.MapleInventoryType;
import Client.inventory.MapleWeapon;
import Client.inventory.NirvanaFlame;
import Client.inventory.PetFlag;
import Client.inventory.StarForce;
import Client.skills.SkillFactory;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.enums.UserChatMessageType;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.server.InitializeServer;
import Net.server.ScriptedItem;
import Net.server.StructAndroid;
import Net.server.StructCrossHunterShop;
import Net.server.StructExclusiveEquip;
import Net.server.StructFamiliar;
import Net.server.StructItemOption;
import Net.server.StructSetItem;
import Net.server.StructSetItemStat;
import Net.server.VCoreDataEntry;
import Net.server.buffs.MapleStatEffect;
import Net.server.buffs.MapleStatEffectFactory;
import Net.server.collection.SoulCollectionEntry;
import Net.server.factory.MobCollectionFactory;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataEntry;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import Plugin.provider.MapleDataType;
import Server.auction.AuctionServer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;
import tools.LotteryRandom;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;
import tools.Triple;
import tools.json.JSONObject;

public class MapleItemInformationProvider {
    private static final Logger log = LoggerFactory.getLogger(MapleItemInformationProvider.class);
    private static final MapleItemInformationProvider instance = new MapleItemInformationProvider();
    protected final MapleDataProvider chrData = MapleDataProviderFactory.getCharacter();
    protected final MapleDataProvider etcData = MapleDataProviderFactory.getEtc();
    protected final MapleDataProvider itemData = MapleDataProviderFactory.getItem();
    protected final MapleDataProvider stringData = MapleDataProviderFactory.getString();
    protected final MapleDataProvider effectData = MapleDataProviderFactory.getEffect();
    protected final Map<Integer, StructFamiliar> familiars = new HashMap<Integer, StructFamiliar>();
    protected final Map<Integer, LinkedList<StructItemOption>> familiar_option = new HashMap<Integer, LinkedList<StructItemOption>>();
    protected final Map<Integer, StructSetItem> familiarSets = new HashMap<Integer, StructSetItem>();
    protected final Map<Integer, Map<Integer, Float>> familiarTable_pad = new HashMap<Integer, Map<Integer, Float>>();
    protected final Map<Integer, Map<Integer, Short>> familiarTable_rchance = new HashMap<Integer, Map<Integer, Short>>();
    protected final Map<Integer, Integer> familiarTable_fee_reinforce = new HashMap<Integer, Integer>();
    protected final Map<Integer, Integer> familiarTable_fee_evolve = new HashMap<Integer, Integer>();
    protected final Map<String, List<Triple<String, Point, Point>>> afterImage = new HashMap<String, List<Triple<String, Point, Point>>>();
    protected final Map<Integer, MapleStatEffect> itemEffects = new HashMap<Integer, MapleStatEffect>();
    protected final Map<Integer, MapleStatEffect> itemEffectsEx = new HashMap<Integer, MapleStatEffect>();
    protected final Map<Integer, Integer> mobIds = new HashMap<Integer, Integer>();
    protected final Map<Integer, Pair<Integer, Integer>> potLife = new HashMap<Integer, Pair<Integer, Integer>>();
    protected final Map<Integer, StructAndroid> androidInfo = new HashMap<Integer, StructAndroid>();
    protected final Map<Integer, StructCrossHunterShop> crossHunterShop = new HashMap<Integer, StructCrossHunterShop>();
    protected final Map<Integer, Integer> exclusiveEquip = new HashMap<Integer, Integer>();
    protected final Map<Integer, StructExclusiveEquip> exclusiveEquipInfo = new HashMap<Integer, StructExclusiveEquip>();
    protected final Map<Integer, Integer> soulSkill = new TreeMap<Integer, Integer>();
    protected final Map<Integer, ArrayList<Integer>> tempOption = new TreeMap<Integer, ArrayList<Integer>>();
    protected final Map<Integer, Pair<Integer, Integer>> socketReqLevel = new TreeMap<Integer, Pair<Integer, Integer>>();
    protected final Map<Integer, Integer> damageSkinBox = new TreeMap<Integer, Integer>(Integer::compareTo);
    protected final Map<Integer, Integer> damageSkinBox_invert = new TreeMap<Integer, Integer>(Integer::compareTo);
    protected final List<Integer> setItemInfoEffs = new ArrayList<Integer>();
    protected final Map<Integer, VCoreDataEntry> coreDatas = new HashMap<Integer, VCoreDataEntry>();
    protected final Map<Integer, Map<Integer, Triple<Integer, Integer, Integer>>> vcores = new HashMap<Integer, Map<Integer, Triple<Integer, Integer, Integer>>>();
    protected final Map<Integer, List<Pair<Integer, String>>> coreJobSkills = new HashMap<Integer, List<Pair<Integer, String>>>();
    private static final Map<Integer, String> itemName = new HashMap<Integer, String>();
    private static final Map<Integer, String> itemDesc = new HashMap<Integer, String>();
    private static final Map<Integer, String> itemMsg = new HashMap<Integer, String>();
    private static final Map<Integer, Map<String, Object>> itemDataCache = new HashMap<Integer, Map<String, Object>>();
    private static final Map<Integer, List<StructItemOption>> potentialData = new HashMap<Integer, List<StructItemOption>>();
    private static final Map<Integer, Map<Integer, StructItemOption>> socketData = new HashMap<Integer, Map<Integer, StructItemOption>>();
    private static final Map<Integer, StructSetItem> setItemInfo = new HashMap<Integer, StructSetItem>();
    private static final Map<Integer, Map<Integer, Map<String, Integer>>> sealedEquipInfo = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
    private final Map<Integer, Integer> soulItems = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> soulSkills = new HashMap<Integer, Integer>();
    private final Map<Integer, SoulCollectionEntry> soulCollections = new HashMap<Integer, SoulCollectionEntry>();

    public static MapleItemInformationProvider getInstance() {
        return instance;
    }

    public List<Integer> getSetItemInfoEffs() {
        return this.setItemInfoEffs;
    }

    /*
     * WARNING - void declaration
     */
    public void runEtc() {
        int i;
        MapleDataDirectoryEntry e = (MapleDataDirectoryEntry) this.etcData.getRoot().getEntry("Android");
        for (MapleDataEntry mapleDataEntry : e.getFiles()) {
            int type;
            MapleData iz = this.etcData.getData("Android/" + mapleDataEntry.getName());
            StructAndroid android = new StructAndroid();
            android.type = type = Integer.parseInt(mapleDataEntry.getName().substring(0, 4));
            android.gender = MapleDataTool.getIntConvert("info/gender", iz, 0);
            android.shopUsable = MapleDataTool.getIntConvert("info/shopUsable", iz, 0) == 1;
            for (MapleData mapleData : iz.getChildByPath("costume/skin")) {
                android.skin.add(MapleDataTool.getInt(mapleData, 2000));
            }
            for (MapleData mapleData : iz.getChildByPath("costume/hair")) {
                android.hair.add(MapleDataTool.getInt(mapleData, android.gender == 0 ? 20101 : 21101));
            }
            for (MapleData mapleData : iz.getChildByPath("costume/face")) {
                android.face.add(MapleDataTool.getInt(mapleData, android.gender == 0 ? 30110 : 31510));
            }
            this.androidInfo.put(type, android);
        }
        MapleData shopData = this.etcData.getData("CrossHunterChapter.img").getChildByPath("Shop");
        for (MapleData dat : shopData) {
            int key = Integer.parseInt(dat.getName());
            StructCrossHunterShop shop = new StructCrossHunterShop(MapleDataTool.getIntConvert("itemId", (MapleData) dat, 0), MapleDataTool.getIntConvert("tokenPrice", (MapleData) dat, -1), MapleDataTool.getIntConvert("potentialGrade", (MapleData) dat, 0));
            this.crossHunterShop.put(key, shop);
        }
        MapleData mapleData = this.etcData.getData("ItemPotLifeInfo.img");
        for (MapleData d : mapleData) {
            if (d.getChildByPath("info") == null || MapleDataTool.getInt("type", d.getChildByPath("info"), 0) != 1)
                continue;
            this.potLife.put(MapleDataTool.getInt("counsumeItem", d.getChildByPath("info"), 0), new Pair<Integer, Integer>(Integer.parseInt(d.getName()), d.getChildByPath("level").getChildren().size()));
        }
        Optional.ofNullable(this.etcData.getData("AuctionData.img")).ifPresent(auctionData -> {
            for (MapleData a1372 : auctionData) {
                MapleData b14;
                HashMap hashMap10 = new HashMap();
                if (!a1372.getName().startsWith("ItemCategory_") || (b14 = auctionData.getChildByPath("ItemDetailCategory_" + a1372.getName().replace("ItemCategory_", ""))) == null)
                    continue;
                for (MapleData anA1372 : a1372) {
                    MapleData b15 = b14.getChildByPath(anA1372.getName());
                    if (b15 == null) continue;
                    HashMap hashMap11 = new HashMap();
                    for (MapleData a1373 : b15) {
                        HashMap<Integer, Pair<Integer, Integer>> hashMap12 = new HashMap<Integer, Pair<Integer, Integer>>();
                        for (MapleData a1374 : a1373) {
                            if (a1374.getChildren().isEmpty() || a1374.getName().length() >= 4) continue;
                            hashMap12.put(Integer.parseInt(a1374.getName()), new Pair<Integer, Integer>(MapleDataTool.getIntConvert("begin", a1374), MapleDataTool.getIntConvert("end", a1374)));
                        }
                        hashMap11.put(hashMap11.size(), hashMap12);
                    }
                    hashMap10.put(hashMap10.size(), hashMap11);
                }
                AuctionServer.auctions.put(AuctionServer.auctions.size(), hashMap10);
            }
        });
        ArrayList<Triple<String, Point, Point>> thePointK = new ArrayList<Triple<String, Point, Point>>();
        ArrayList<Triple<String, Point, Point>> thePointA = new ArrayList<Triple<String, Point, Point>>();
        MapleDataDirectoryEntry a = (MapleDataDirectoryEntry) this.chrData.getRoot().getEntry("Afterimage");
        for (MapleDataEntry mapleDataEntry : a.getFiles()) {
            MapleData iz = this.chrData.getData("Afterimage/" + mapleDataEntry.getName());
            ArrayList thePoint = new ArrayList();
            HashMap<String, Pair<Point, Point>> dummy = new HashMap<String, Pair<Point, Point>>();
            for (MapleData mapleData2 : iz) {
                for (MapleData xD : mapleData2) {
                    Point lt;
                    if (xD.getName().contains("prone") || xD.getName().contains("double") || xD.getName().contains("triple") || (mapleDataEntry.getName().contains("bow") || mapleDataEntry.getName().contains("Bow")) && !xD.getName().contains("shoot") || (mapleDataEntry.getName().contains("gun") || mapleDataEntry.getName().contains("cannon")) && !xD.getName().contains("shot"))
                        continue;
                    if (dummy.containsKey(xD.getName())) {
                        if (xD.getChildByPath("lt") != null) {
                            lt = (Point) xD.getChildByPath("lt").getData();
                            Point ourLt = (Point) ((Pair) dummy.get((Object) xD.getName())).left;
                            if (lt.x < ourLt.x) {
                                ourLt.x = lt.x;
                            }
                            if (lt.y < ourLt.y) {
                                ourLt.y = lt.y;
                            }
                        }
                        if (xD.getChildByPath("rb") == null) continue;
                        Point rb = (Point) xD.getChildByPath("rb").getData();
                        Point ourRb = (Point) ((Pair) dummy.get((Object) xD.getName())).right;
                        if (rb.x > ourRb.x) {
                            ourRb.x = rb.x;
                        }
                        if (rb.y <= ourRb.y) continue;
                        ourRb.y = rb.y;
                        continue;
                    }
                    lt = null;
                    Object rb = null;
                    if (xD.getChildByPath("lt") != null) {
                        lt = (Point) xD.getChildByPath("lt").getData();
                    }
                    if (xD.getChildByPath("rb") != null) {
                        rb = (Point) xD.getChildByPath("rb").getData();
                    }
                    dummy.put(xD.getName(), new Pair<Point, Point>(lt, (Point) rb));
                }
            }
            for (Map.Entry entry : dummy.entrySet()) {
                if (((String) entry.getKey()).length() > 2 && ((String) entry.getKey()).substring(((String) entry.getKey()).length() - 2, ((String) entry.getKey()).length() - 1).equals("D")) {
                    thePointK.add(new Triple<String, Point, Point>((String) entry.getKey(), (Point) ((Pair) entry.getValue()).left, (Point) ((Pair) entry.getValue()).right));
                    continue;
                }
                if (((String) entry.getKey()).contains("PoleArm")) {
                    thePointA.add(new Triple<String, Point, Point>((String) entry.getKey(), (Point) ((Pair) entry.getValue()).left, (Point) ((Pair) entry.getValue()).right));
                    continue;
                }
                thePoint.add(new Triple<String, Point, Point>((String) entry.getKey(), (Point) ((Pair) entry.getValue()).left, (Point) ((Pair) entry.getValue()).right));
            }
            this.afterImage.put(mapleDataEntry.getName().substring(0, mapleDataEntry.getName().length() - 4), thePoint);
        }
        this.afterImage.put("katara", thePointK);
        this.afterImage.put("aran", thePointA);
        MapleData exclusiveEquipData = this.etcData.getData("ExclusiveEquip.img");
        for (MapleData dat : exclusiveEquipData) {
            StructExclusiveEquip structExclusiveEquip = new StructExclusiveEquip();
            int exId = Integer.parseInt(dat.getName());
            String msg = MapleDataTool.getString("msg", dat, "");
            msg = msg.replace("\\r\\n", "\r\n");
            msg = msg.replace("-------<", "---<");
            msg = msg.replace(">------", ">---");
            structExclusiveEquip.id = exId;
            structExclusiveEquip.msg = msg;
            for (MapleData level : dat.getChildByPath("item")) {
                int itemId = MapleDataTool.getInt(level);
                structExclusiveEquip.itemIDs.add(itemId);
                this.exclusiveEquip.put(itemId, exId);
            }
            this.exclusiveEquipInfo.put(exId, structExclusiveEquip);
        }
        StructExclusiveEquip structExclusiveEquip = new StructExclusiveEquip();
        int exId = 100;
        structExclusiveEquip.msg = "只能佩戴一個\r\n老公老婆的戒指。";
        for (i = 1112446; i <= 1112495; ++i) {
            structExclusiveEquip.itemIDs.add(i);
            this.exclusiveEquip.put(i, exId);
        }
        this.exclusiveEquipInfo.put(exId, structExclusiveEquip);
        StructExclusiveEquip structExclusiveEquip2 = new StructExclusiveEquip();
        exId = 101;
        structExclusiveEquip2.msg = "只能佩戴一個\r\n不速之客的戒指。";
        for (i = 1112435; i <= 1112439; ++i) {
            structExclusiveEquip2.itemIDs.add(i);
            this.exclusiveEquip.put(i, exId);
        }
        this.exclusiveEquipInfo.put(exId, structExclusiveEquip2);
        StructExclusiveEquip structExclusiveEquip3 = new StructExclusiveEquip();
        exId = 102;
        structExclusiveEquip3.msg = "只能佩戴一個\r\n十字旅團的戒指。";
        for (i = 1112599; i <= 1112613; ++i) {
            structExclusiveEquip3.itemIDs.add(i);
            this.exclusiveEquip.put(i, exId);
        }
        this.exclusiveEquipInfo.put(exId, structExclusiveEquip3);
        this.damageSkinBox.put(2431965, 0);
        this.damageSkinBox_invert.put(0, 2431965);
        this.damageSkinBox.put(2438159, 0);
        this.damageSkinBox_invert.put(0, 2438159);
        for (MapleData d : this.etcData.getData("DamageSkin.img")) {
            int skinID = Integer.parseInt(d.getName());
            int n = MapleDataTool.getInt("extractID", d, 0);
            if (n > 0) {
                this.damageSkinBox.put(n, skinID);
                this.damageSkinBox_invert.put(skinID, n);
                continue;
            }
            if (this.damageSkinBox_invert.containsKey(skinID)) continue;
            this.damageSkinBox_invert.put(skinID, -1);
        }
        String damageStringProperties = "2434692=13;5680395=40;2435424=109;2435425=110;2435427=102;2435428=103;2435429=104;2630302=1370;2630303=1370;2630305=1371;2630306=1371;2631750=1405;2631753=1404;2435430=111;2435431=1065;2435432=113;2435433=114;2436521=150;2436522=154;2436528=152;2436529=153;2631402=266;2436891=16;2436530=93;2436531=44;2434004=1041;2435456=1308;2438719=1337;2436543=83;2439801=1362;2439802=1362;2436545=134;2436546=131;2436547=130;2436548=125;2439806=1363;2439807=1363;2434375=1045;2435461=95;2438726=141;2436553=1299;2631797=273;2435473=96;2435474=99;2435477=34;2435478=12;2436560=156;2436561=119;2436206=141;2434390=38;2434391=39;2436212=143;2436215=144;2436578=157;2631452=267;2434040=15;2435490=36;2435491=35;2435493=106;2630010=228;2435140=81;2435141=82;2630381=231;2630384=237;2631478=1399;2631479=1399;2438414=196;2435157=85;2630391=1372;2435158=86;2438416=197;2630392=1372;2435159=87;2438418=198;2630394=1373;2630395=1373;2631481=1400;2631482=1400;2439506=14;2439507=11;2437691=185;2437696=1298;2437697=1299;2632210=1421;2435160=1057;2435161=1059;2632213=1422;2435162=84;2438420=199;2438421=28;2435166=91;2435168=24;2435169=20;2439875=1366;2439876=1367;2436258=146;2436259=147;2631493=269;2434081=1030;2631134=263;2631137=50;2631138=20;2435170=15;2435171=46;2435172=1;2435173=8;2435174=11;2435175=14;2435176=16;2435177=17;2435179=83;2439521=1354;2439522=1354;2436268=145;2435182=1064;2435184=92;2439895=1364;2439896=1364;2439898=1365;2631150=1390;2439899=1365;2631151=1390;2631153=1393;2631154=1393;2432803=47;2432804=1004;2437009=3;2438460=200;2438461=14;2438466=1322;2438468=1323;2439552=1355;2439553=1355;2439555=1356;2439556=1356;2437022=1311;2437023=173;2437024=174;2438470=1365;2438472=1325;2433913=26;2438476=1241;2433919=15;2631184=265;2439212=170;2439579=16;2438491=201;2439580=168;2438143=191;2438144=192;2438148=1287;2438150=1302;2438157=7;2439256=209;2437086=1272;2432526=11;2439264=210;2439265=211;2433980=27;2433981=28;2439277=212;2434734=52;2434741=27;2434742=28;2435832=121;2435833=122;2435834=17;2435835=86;2435836=81;2435837=74;2435838=75;2435839=123;2435840=124;2435841=125;2435849=98;2631814=274;2432591=1000;2432592=13;2435850=91;2631821=1086;2631826=1408;2631829=1409;2436951=1310;2630744=252;2436952=170;2436953=171;2630746=253;2630748=254;2630750=255;2630752=256;2630754=257;2436611=158;2630766=233;2436612=159;2630770=1382;2630771=1382;2435538=16;2630773=1383;2630774=1383;2433362=35;2436621=93;2436622=87;2630780=258;2630421=239;2630435=240;2631884=275;2437735=187;2437736=188;2630447=1374;2631892=276;2434129=14;2630450=1375;2439926=224;2439928=225;2434130=2;2434499=1061;2435222=93;2438844=1302;2436679=161;2437761=143;2434147=1042;2437767=157;2438855=1322;2436680=162;2436681=163;2436682=164;2436683=1134;2436684=1230;2436323=14;2630478=241;2436687=87;2436688=4;2434157=1043;2630480=242;2630482=243;2630484=244;2630486=245;2438872=208;2630132=227;2439608=198;2438880=206;2438884=207;2438529=202;2439616=218;2435278=23;2630154=1368;2630155=1368;2630157=1369;2630158=1369;2437469=161;2435293=94;2436023=128;2630179=229;2436024=129;2436026=130;2436027=131;2436028=132;2436029=133;2437470=162;2438568=124;2436034=39;2436035=15;2436036=26;2436038=11;2437482=184;2438579=182;2436041=77;2436042=78;2436043=1059;2436044=1057;2436045=134;2439665=219;2437495=180;2437496=181;2437498=182;2438580=164;2438581=161;2438582=152;2438583=1305;2438584=124;2438585=114;2438586=1064;2438587=111;2438588=1057;2438589=1307;2436057=91;2438590=46;2438591=27;2438593=1327;2438595=1328;2438597=1329;2439681=1291;2439683=21;2439685=139;2439698=1358;2439699=1358;2437164=176;2432603=4;2436083=1304;2436084=1305;2436085=1306;2432972=8;2432973=17;2433709=1030;2436096=83;2436097=1307;2436098=85;2436099=1309;2437187=114;2437188=103;2437189=102;2433710=8;2433715=23;2437190=106;2432637=5;2432638=4;2432639=11;2439374=1351;2439375=1351;2439377=1352;2439378=1352;2432640=14;2434824=53;2432658=5;2432659=4;2439392=214;2439394=215;2439396=1019;2439398=1017;2432660=11;2432661=14;2433777=52;2439064=188;2439065=42;2439066=157;2439067=171;2439068=75;2439069=36;2432695=1001;2630838=1386;2630839=1386;2434871=1063;2439070=104;2439071=74;2434873=1064;2434875=1065;2434877=1066;2434879=1067;2434519=17;2630841=1387;2630842=1387;2631930=1410;2631933=1411;2434528=41;2434529=42;2435972=127;2434530=43;2434542=47;2434546=44;2433104=1;2436724=122;2630517=1343;2433105=11;2436725=133;2433106=5;2433107=12;2433112=1007;2433113=1008;2630529=1376;2631611=270;2434566=2;2438918=1339;2438919=1339;2434574=45;2434575=46;2438920=1340;2438921=1340;2438922=1341;2438923=1341;2438924=193;2435667=92;2438925=176;2438926=184;2438927=1342;2438928=1343;2630543=1377;2433132=13;2631992=1416;2631995=1417;2437846=119;2437847=1072;2437848=1073;2435670=1059;2437849=1074;2435671=81;2435672=83;2435313=95;2435316=100;2630553=246;2436400=148;2630555=247;2436763=127;2436764=128;2630557=248;2436765=132;2436766=121;2630559=249;2436767=84;2437851=143;2437854=189;2435325=48;2435687=53;2435326=1057;2435688=78;2435689=77;2630561=250;2631655=1331;2631658=1403;2434248=34;2435331=96;2435332=97;2435333=98;2435334=99;2630213=230;2436785=167;2437515=183;2437878=50;2432084=1;2630225=233;2630587=1381;2436437=1086;2437527=27;2438973=1343;2435356=25;2630590=1380;2435357=1061;2435358=1049;2439701=1359;2630236=232;2439702=1359;2439709=1355;2434273=35;2434274=36;2438626=183;2439710=1352;2630245=1322;2630246=1262;2433197=1012;2433199=9;2434289=37;2436100=5;2436101=23;2436103=138;2435023=7;2435024=23;2435025=26;2435026=27;2435027=34;2435028=35;2435029=36;2630263=234;2630265=235;2630267=236;2630269=1030;2435030=76;2438656=1331;2435037=1070;2438660=1332;2438662=1333;2435043=77;2438663=1334;2435044=78;2438664=1334;2435045=5;2435046=1023;2435047=80;2438667=1336;2438668=1336;2436131=139;2438676=205;2436140=140;2439400=1322;2439768=223;2439407=216;2631388=1392;2631389=1392;2437238=177;2437239=179;2631391=1393;2631392=1393;2631035=1388;2437243=178;2631036=1388;2437244=82;2631038=1389;2631039=1389;2431966=1;2438352=193;2439441=1353;2432710=15;2437275=113;2433804=24;2631090=260;2631091=261;2631097=262;2432748=1002;2432749=50;2632185=1418;2632188=1419;2439489=192;2439130=1348;2439131=1348;2439133=1349;2439134=1349;2434950=74;2434951=75;2439155=1345;2439156=1345;2439158=1346;2434600=1055;2434601=115;2439163=1346;2439165=1347;2439166=1347;2433883=1040;2435703=85;2435704=86;2438095=1291;2433898=1017;2435724=84;2435725=1064;2435726=20;2435727=46;2436810=168;2433558=1021;2630970=259;2433568=1028;2433569=1027;2434654=48;2434655=49;2434659=1056;2433570=1026;2433571=1025;2433572=1024;2434661=50;2433588=1023;2434672=50;2435408=101;2432153=4;2435411=48;2432154=5;2435412=49;2630653=148";
        StringBuilder sb = new StringBuilder();
        Arrays.stream(damageStringProperties.split(";")).map(it -> it.split("=")).forEach(it -> {
            int itemId = Integer.parseInt(it[0]);
            int skinId = Integer.parseInt(it[1]);
            if (this.damageSkinBox_invert.getOrDefault(skinId, -2) == -2) {
                return;
            }
            if (!this.damageSkinBox.containsKey(itemId) || !this.damageSkinBox_invert.containsKey(skinId)) {
                if (!this.damageSkinBox.containsKey(itemId)) {
                    this.damageSkinBox.put(itemId, skinId);
                }
                if (!this.damageSkinBox_invert.containsKey(skinId) || this.damageSkinBox_invert.get(skinId) == -1) {
                    this.damageSkinBox_invert.put(skinId, itemId);
                }
                sb.append(itemId).append("=").append(skinId).append(";");
            }
        });
        if (!damageStringProperties.equals(sb.substring(0, sb.toString().length() - 1))) {
            System.err.println("damageSkin:" + sb.substring(0, sb.toString().length() - 1));
        }
        DatabaseLoader.DatabaseConnection.domain(con -> {
            List<Pair> list = SqlTool.queryAndGetList("SELECT `itemid`, `skinid` FROM `damageskin`", rs -> new Pair<Integer, Integer>(rs.getInt("itemid"), rs.getInt("skinid")));
            for (Pair it : list) {
                if (this.damageSkinBox_invert.getOrDefault(it.right, -2) == -2) {
                    SqlTool.update(con, "DELETE FROM `damageskin` WHERE `itemid` = ? AND `skinid` = ?", it.left, it.right);
                    System.err.println("從WZ資料找不到ID為[ " + String.valueOf(it.right) + " ]的傷害皮膚，配置的道具ID[ " + String.valueOf(it.left) + " ]，從資料庫清理這個配置");
                    continue;
                }
                if ((Integer) it.left <= 0 || this.damageSkinBox.containsKey(it.left)) {
                    SqlTool.update(con, "DELETE FROM `damageskin` WHERE `itemid` = ? AND `skinid` = ?", it.left, it.right);
                    if ((Integer) it.left <= 0) continue;
                    System.err.println("WZ資料已經存在[ skin:" + String.valueOf(it.left) + " ]的傷害皮膚配置，從資料庫清理這個配置");
                    continue;
                }
                this.damageSkinBox.put((Integer) it.left, (Integer) it.right);
                if (this.damageSkinBox_invert.containsKey(it.right) && this.damageSkinBox_invert.get(it.right) != -1)
                    continue;
                this.damageSkinBox_invert.put((Integer) it.right, (Integer) it.left);
            }
            return null;
        });
        for (MapleData mapleData3 : this.effectData.getData("ItemEff.img")) {
            this.setItemInfoEffs.add(Integer.valueOf(mapleData3.getName()));
        }
        Collections.reverse(this.setItemInfoEffs);
        Optional.ofNullable(this.etcData.getData("VMatrixOption.img")).ifPresent(vmatrixData -> {
            Optional.ofNullable(vmatrixData.getChildByPath("info")).ifPresent(info -> {
                VMatrixOption.SlotMax = MapleDataTool.getInt("slotMax", info, 500);
                VMatrixOption.EquipSlotMin = MapleDataTool.getInt("equipSlotMin", info, 4);
                VMatrixOption.EquipSlotMax = MapleDataTool.getInt("equipSlotMax", info, 14);
                VMatrixOption.SpecialSlotMax = MapleDataTool.getInt("specialSlotMax", info, 1);
                VMatrixOption.ExtendLevel = MapleDataTool.getInt("extendLevel", info, 5);
                VMatrixOption.ExtendAF = MapleDataTool.getInt("extendAF", info, 0);
                VMatrixOption.GradeMax = MapleDataTool.getInt("gradeMax", info, 25);
                VMatrixOption.TotalGradeMax = MapleDataTool.getInt("totalGradeMax", info, 50);
                VMatrixOption.CraftSkillCoreCost = MapleDataTool.getInt("craftSkillCoreCost", info, 140);
                VMatrixOption.CraftEnchantCoreCost = MapleDataTool.getInt("craftEnchantCoreCost", info, 70);
                VMatrixOption.CraftSpecialCoreCost = MapleDataTool.getInt("craftSpecialCoreCost", info, 250);
                VMatrixOption.CraftGemstoneCost = MapleDataTool.getInt("craftGemstoneCost", info, 35);
                VMatrixOption.MatrixPointResetMeso = MapleDataTool.getInt("matrixPointResetMeso", info, 1000000);
                VMatrixOption.EquipSlotEnhanceMax = MapleDataTool.getInt("equipSlotEnhanceMax", info, 5);
            });
            Optional.ofNullable(vmatrixData.getChildByPath("slotExpansionMeso")).ifPresent(slotem -> slotem.forEach(data -> {
                HashMap<Integer, Long> map = new HashMap<Integer, Long>();
                for (MapleData d : data) {
                    map.put(Integer.parseInt(d.getName()), MapleDataTool.getLong(d, 278440000L));
                }
                VMatrixOption.SlotExpansionMeso.put(Integer.parseInt(data.getName()), map);
            }));
        });
        MobCollectionFactory.init(this.etcData.getData("mobCollection.img"));
        MapleData vcoreData = this.etcData.getData("VCore.img");
        for (MapleData coreData : vcoreData.getChildByPath("CoreData")) {
            MapleData connectSkill1;
            VCoreDataEntry vCoreDataEntry = new VCoreDataEntry();
            vCoreDataEntry.setId(Integer.valueOf(coreData.getName()));
            vCoreDataEntry.setName(MapleDataTool.getString("name", coreData, ""));
            vCoreDataEntry.setDesc(MapleDataTool.getString("desc", coreData, ""));
            vCoreDataEntry.setType(MapleDataTool.getInt("type", coreData, 0));
            vCoreDataEntry.setMaxLevel(MapleDataTool.getInt("maxLevel", coreData, 0));
            vCoreDataEntry.setNobAbleGemStone(MapleDataTool.getInt("nobAbleGemStone", coreData, 0) == 1);
            vCoreDataEntry.setNotAbleCraft(MapleDataTool.getInt("notAbleCraft", coreData, 0) == 1);
            vCoreDataEntry.setDisassemble(MapleDataTool.getInt("noDisassemble", coreData, 0) == 1);
            MapleData job = coreData.getChildByPath("job");
            if (job != null) {
                for (Object jobData : job) {
                    String sJob = MapleDataTool.getString((MapleData) jobData, "");
                    if (sJob.isEmpty()) continue;
                    vCoreDataEntry.addJob(sJob);
                }
            }
            if ((connectSkill1 = coreData.getChildByPath("connectSkill")) != null) {
                for (MapleData connectSkill : connectSkill1) {
                    int anInt = MapleDataTool.getInt(connectSkill, 0);
                    if (anInt > 0) {
                        vCoreDataEntry.getConnectSkill().add(anInt);
                    }
                }
            }
            this.coreDatas.put(vCoreDataEntry.getId(), vCoreDataEntry);
        }
        boolean bl = false;
        for (MapleData enforcement : vcoreData.getChildByPath("Enforcement")) {
            int var13_46 = 0;
            HashMap<Integer, Triple<Integer, Integer, Integer>> map = new HashMap<Integer, Triple<Integer, Integer, Integer>>();
            for (MapleData subdata : enforcement) {
                map.put(Integer.valueOf(subdata.getName()), new Triple<Integer, Integer, Integer>(MapleDataTool.getInt("nextExp", subdata, 0), MapleDataTool.getInt("expEnforce", subdata, 0), MapleDataTool.getInt("extract", subdata, 0)));
            }
            this.vcores.put((int) (++var13_46), map);
        }
        for (MapleData jobSkill : vcoreData.getChildByPath("JobSkill")) {
            if (!jobSkill.getName().matches("^\\d+$")) continue;
            for (MapleData subdata : jobSkill) {
                this.coreJobSkills.computeIfAbsent(Integer.parseInt(jobSkill.getName()), k -> new ArrayList()).add(new Pair<Integer, String>(MapleDataTool.getInt("id", subdata, 0), MapleDataTool.getString("name", subdata, "")));
            }
        }
        Optional.ofNullable(this.etcData.getData("SoulCollection.img")).ifPresent(soulCollectionData -> {
            for (MapleData data : soulCollectionData) {
                int id = Integer.valueOf(data.getName());
                SoulCollectionEntry entry = new SoulCollectionEntry();
                int soulSkill = MapleDataTool.getInt("soulSkill", data, -1);
                int soulSkillH = MapleDataTool.getInt("soulSkillH", data, -1);
                MapleData soulList = data.getChildByPath("soulList");
                if (soulList == null) continue;
                entry.setSoulSkill(soulSkill);
                entry.setSoulSkillH(soulSkillH);
                this.soulSkills.put(soulSkill, id);
                this.soulSkills.put(soulSkillH, id);
                for (MapleData soul : soulList) {
                    int soulid = Integer.valueOf(soul.getName());
                    int a3 = MapleDataTool.getInt("0", soul, -1);
                    int a4 = MapleDataTool.getInt("1", soul, -1);
                    if (Integer.parseInt(soul.getName()) == 8 && soulSkillH > 0 && a3 > 0) {
                        this.soulItems.put(a3, soulSkillH);
                        entry.getItems().put(a3, soulid);
                        continue;
                    }
                    if (a3 > 0) {
                        this.soulItems.put(a3, soulSkill);
                        entry.getItems().put(a3, soulid);
                    }
                    if (a4 <= 0) continue;
                    this.soulItems.put(a4, soulSkill);
                    entry.getItems().put(a4, soulid);
                }
                this.soulCollections.put(id, entry);
            }
        });
    }

    public void loadSetItemData() {
        MapleData setsData = this.etcData.getData("SetItemInfo.img");
        for (MapleData dat : setsData) {
            StructSetItem SetItem = new StructSetItem();
            SetItem.setItemID = Integer.parseInt(dat.getName());
            SetItem.setItemName = MapleDataTool.getString("setItemName", dat, "");
            SetItem.completeCount = (byte) MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (MapleData level : dat.getChildByPath("ItemID")) {
                if (level.getType() != MapleDataType.INT) {
                    for (MapleData leve : level) {
                        if (leve.getName().equals("representName") || leve.getName().equals("typeName")) continue;
                        try {
                            SetItem.itemIDs.add(MapleDataTool.getIntConvert(leve));
                        } catch (Exception e) {
                            System.err.println("出錯數據： leve = " + String.valueOf(leve.getData()));
                        }
                    }
                    continue;
                }
                SetItem.itemIDs.add(MapleDataTool.getInt(level));
            }
            for (MapleData level : dat.getChildByPath("Effect")) {
                StructSetItemStat SetItemStat = new StructSetItemStat();
                SetItemStat.incSTR = MapleDataTool.getIntConvert("incSTR", level, 0);
                SetItemStat.incDEX = MapleDataTool.getIntConvert("incDEX", level, 0);
                SetItemStat.incINT = MapleDataTool.getIntConvert("incINT", level, 0);
                SetItemStat.incLUK = MapleDataTool.getIntConvert("incLUK", level, 0);
                SetItemStat.incMHP = MapleDataTool.getIntConvert("incMHP", level, 0);
                SetItemStat.incMMP = MapleDataTool.getIntConvert("incMMP", level, 0);
                SetItemStat.incMHPr = MapleDataTool.getIntConvert("incMHPr", level, 0);
                SetItemStat.incMMPr = MapleDataTool.getIntConvert("incMMPr", level, 0);
                SetItemStat.incACC = MapleDataTool.getIntConvert("incACC", level, 0);
                SetItemStat.incEVA = MapleDataTool.getIntConvert("incEVA", level, 0);
                SetItemStat.incPDD = MapleDataTool.getIntConvert("incPDD", level, 0);
                SetItemStat.incMDD = MapleDataTool.getIntConvert("incMDD", level, 0);
                SetItemStat.incPAD = MapleDataTool.getIntConvert("incPAD", level, 0);
                SetItemStat.incMAD = MapleDataTool.getIntConvert("incMAD", level, 0);
                SetItemStat.incJump = MapleDataTool.getIntConvert("incJump", level, 0);
                SetItemStat.incSpeed = MapleDataTool.getIntConvert("incSpeed", level, 0);
                SetItemStat.incAllStat = MapleDataTool.getIntConvert("incAllStat", level, 0);
                SetItemStat.incPQEXPr = MapleDataTool.getIntConvert("incPQEXPr", level, 0);
                SetItemStat.incPVPDamage = MapleDataTool.getIntConvert("incPVPDamage", level, 0);
                SetItemStat.option1 = MapleDataTool.getIntConvert("Option/1/option", level, 0);
                SetItemStat.option2 = MapleDataTool.getIntConvert("Option/2/option", level, 0);
                SetItemStat.option3 = MapleDataTool.getIntConvert("Option/3/option", level, 0);
                SetItemStat.option1Level = MapleDataTool.getIntConvert("Option/1/level", level, 0);
                SetItemStat.option2Level = MapleDataTool.getIntConvert("Option/2/level", level, 0);
                SetItemStat.option3Level = MapleDataTool.getIntConvert("Option/3/level", level, 0);
                SetItemStat.skillId = MapleDataTool.getIntConvert("activeSkill/0/id", level, 0);
                SetItemStat.skillLevel = MapleDataTool.getIntConvert("activeSkill/0/level", level, 0);
                SetItem.setItemStat.put(Integer.parseInt(level.getName()), SetItemStat);
            }
            setItemInfo.put(SetItem.setItemID, SetItem);
        }
    }

    public void loadPotentialData() {
        MapleData potsData = this.itemData.getData("ItemOption.img");
        for (MapleData dat : potsData) {
            LinkedList<StructItemOption> items = new LinkedList<StructItemOption>();
            for (MapleData potLevel : dat.getChildByPath("level")) {
                StructItemOption item = new StructItemOption();
                item.opID = Integer.parseInt(dat.getName());
                item.optionType = MapleDataTool.getIntConvert("info/optionType", dat, 0);
                item.reqLevel = MapleDataTool.getIntConvert("info/reqLevel", dat, 0);
                item.opString = MapleDataTool.getString("info/string", dat, "");
                for (MapleData potData : potLevel) {
                    if (!StructItemOption.types.contains(potData.getName())) {
                        StructItemOption.types.add(potData.getName());
                    }
                    if (potData.getName().equals("face")) {
                        item.face = MapleDataTool.getString(potData, "");
                        continue;
                    }
                    item.data.put(potData.getName(), MapleDataTool.getIntConvert(potData, 0));
                }
                switch (item.opID) {
                    case 31001:
                    case 31002:
                    case 31003:
                    case 31004: {
                        item.data.put("skillID", item.opID - 23001);
                        break;
                    }
                    case 41005:
                    case 41006:
                    case 41007: {
                        item.data.put("skillID", item.opID - 33001);
                    }
                }
                items.add(item);
            }
            potentialData.put(Integer.valueOf(dat.getName()), items);
        }
    }

    public void loadSocketData() {
        MapleData nebuliteData = this.itemData.getData("Install/0306.img");
        for (MapleData dat : nebuliteData) {
            StructItemOption item = new StructItemOption();
            item.opID = Integer.parseInt(dat.getName());
            item.optionType = MapleDataTool.getInt("optionType", dat.getChildByPath("socket"), 0);
            for (MapleData info : dat.getChildByPath("socket/option")) {
                String optionString = MapleDataTool.getString("optionString", info, "");
                int level = MapleDataTool.getInt("level", info, 0);
                if (level <= 0) continue;
                item.data.put(optionString, level);
            }
            switch (item.opID) {
                case 3063370: {
                    item.data.put("skillID", 8000);
                    break;
                }
                case 3063380: {
                    item.data.put("skillID", 8001);
                    break;
                }
                case 3063390: {
                    item.data.put("skillID", 8002);
                    break;
                }
                case 3063400: {
                    item.data.put("skillID", 8003);
                    break;
                }
                case 3064470: {
                    item.data.put("skillID", 8004);
                    break;
                }
                case 3064480: {
                    item.data.put("skillID", 8005);
                    break;
                }
                case 3064490: {
                    item.data.put("skillID", 8006);
                }
            }
            socketData.computeIfAbsent(ItemConstants.getNebuliteGrade(item.opID), key -> new HashMap());
            socketData.get(ItemConstants.getNebuliteGrade(item.opID)).put(item.opID, item);
        }
    }

    public void loadFamiliarItems() {
        MapleDataDirectoryEntry f = (MapleDataDirectoryEntry) this.chrData.getRoot().getEntry("Familiar");
        MapleData familiarItemData = this.itemData.getData("Consume/0287.img");
        for (MapleDataEntry mapleDataEntry : f.getFiles()) {
            int id = Integer.parseInt(mapleDataEntry.getName().substring(0, mapleDataEntry.getName().length() - 4));
            for (MapleData info : this.chrData.getData("Familiar/" + mapleDataEntry.getName())) {
                if (!info.getName().equals("info")) continue;
                int skillid = MapleDataTool.getIntConvert("skill/id", info, 0);
                int effectAfter = MapleDataTool.getIntConvert("skill/effectAfter", info, 0);
                String FAttribute = MapleDataTool.getString("FAttribute", info, null);
                int FCategory = MapleDataTool.getIntConvert("FCategory", info, 0);
                int range = MapleDataTool.getIntConvert("range", info, 0);
                int mobid = MapleDataTool.getIntConvert("MobID", info, 0);
                int monsterCardID = MapleDataTool.getIntConvert("monsterCardID", info, 0);
                byte grade = (byte) MapleDataTool.getIntConvert("0" + monsterCardID + "/info/grade", familiarItemData, 0);
                this.familiars.put(id, new StructFamiliar(id, skillid, effectAfter, mobid, monsterCardID, grade, FAttribute, FCategory, range));
            }
        }
        MapleData familiarTableData = this.etcData.getData("FamiliarTable.img");
        for (MapleData d : familiarTableData) {
            if (d.getName().equals("stat")) {
                for (MapleData subd : d) {
                    HashMap<Integer, Float> stats = new HashMap<Integer, Float>();
                    for (MapleData stat : subd) {
                        stats.put(Integer.valueOf(stat.getName()), Float.valueOf(MapleDataTool.getFloat(stat.getChildByPath("pad"), 0.0f)));
                    }
                    this.familiarTable_pad.put(Integer.valueOf(subd.getName()), stats);
                }
                continue;
            }
            if (d.getName().equals("reinforce_chance")) {
                for (MapleData subd : d) {
                    HashMap<Integer, Short> chances = new HashMap<Integer, Short>();
                    for (MapleData chance : subd) {
                        chances.put(Integer.valueOf(chance.getName()), (Short) chance.getData());
                    }
                    this.familiarTable_rchance.put(Integer.valueOf(subd.getName()), chances);
                }
                continue;
            }
            if (!d.getName().equals("fee")) continue;
            for (MapleData level : d.getChildByPath("reinforce").getChildren()) {
                this.familiarTable_fee_reinforce.put(Integer.parseInt(level.getName()), MapleDataTool.getInt(level, 0));
            }
            for (MapleData level : d.getChildByPath("evolve").getChildren()) {
                this.familiarTable_fee_evolve.put(Integer.parseInt(level.getName()), MapleDataTool.getInt(level, 0));
            }
        }
        MapleData mapleData = this.etcData.getData("FamiliarSet.img");
        for (MapleData sub : mapleData.getChildren()) {
            StructSetItem SetItem = new StructSetItem();
            SetItem.setItemID = Integer.parseInt(sub.getName());
            SetItem.setItemName = MapleDataTool.getString("setName", sub, "");
            for (MapleData familiar : sub.getChildByPath("familiarList").getChildren()) {
                SetItem.itemIDs.add(MapleDataTool.getInt(familiar, 0));
            }
            SetItem.completeCount = (byte) SetItem.itemIDs.size();
            MapleData statsData = sub.getChildByPath("stats");
            StructSetItemStat SetItemStat = new StructSetItemStat();
            SetItemStat.incSpeed = MapleDataTool.getInt("incSpeed", statsData, 0);
            SetItemStat.incJump = MapleDataTool.getInt("incJump", statsData, 0);
            SetItemStat.incMHP = MapleDataTool.getInt("incMHP", statsData, 0);
            SetItemStat.incMMP = MapleDataTool.getInt("incMMP", statsData, 0);
            SetItemStat.incSTR = MapleDataTool.getInt("incSTR", statsData, 0);
            SetItemStat.incDEX = MapleDataTool.getInt("incDEX", statsData, 0);
            SetItemStat.incINT = MapleDataTool.getInt("incINT", statsData, 0);
            SetItemStat.incLUK = MapleDataTool.getInt("incLUK", statsData, 0);
            SetItemStat.incDEX = MapleDataTool.getInt("incDEX", statsData, 0);
            SetItemStat.incDEX = MapleDataTool.getInt("incDEX", statsData, 0);
            SetItemStat.incPAD = MapleDataTool.getInt("incPAD", statsData, 0);
            SetItemStat.incMAD = MapleDataTool.getInt("incMAD", statsData, 0);
            SetItemStat.incAllStat = MapleDataTool.getInt("incAllStat", statsData, 0);
            SetItem.setItemStat.put(Integer.valueOf(SetItem.completeCount), SetItemStat);
            this.familiarSets.put(SetItem.setItemID, SetItem);
        }
        for (MapleData d : this.itemData.getData("FamiliarOption.img")) {
            LinkedList<StructItemOption> options = new LinkedList<StructItemOption>();
            for (MapleData subd : d.getChildByPath("level")) {
                StructItemOption option = new StructItemOption();
                option.opID = Integer.parseInt(d.getName());
                option.optionType = MapleDataTool.getInt("info/optionType", d, 0);
                option.reqLevel = MapleDataTool.getInt("info/reqLevel", d, 0);
                option.opString = MapleDataTool.getString("info/string", d, "");
                for (MapleData subdData : subd) {
                    if (!StructItemOption.types.contains(subdData.getName())) {
                        StructItemOption.types.add(subdData.getName());
                    }
                    if (subdData.getName().equals("face")) {
                        option.face = MapleDataTool.getString(subdData, "");
                        continue;
                    }
                    option.data.put(subdData.getName(), MapleDataTool.getIntConvert(subdData, 0));
                }
                options.add(option);
            }
            this.familiar_option.put(Integer.parseInt(d.getName()), options);
        }
    }

    public void runItems() {
        if (!itemDataCache.isEmpty()) {
            return;
        }
        AtomicBoolean loadFromSql = new AtomicBoolean(false);
        DatabaseLoader.DatabaseConnection.domain(con -> {
            loadFromSql.set(InitializeServer.WzSqlName.wz_itemdata.check(con));
            return null;
        });
        if (loadFromSql.get()) {
            DatabaseLoader.DatabaseConnection.domain(con -> {
                SqlTool.queryAndGetList(con, "SELECT * FROM `wz_itemdata`", rs -> {
                    int itemid = rs.getInt("itemid");
                    String string = rs.getString("data");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(string);
                    } catch (RuntimeException e) {
                        log.error("parse json error from wz_itemdata itemId:{}, data:{}", itemid, string);
                    }
                    if (jsonObject != null) {

                        itemDataCache.put(itemid, jsonObject.toMap());
                        String name = rs.getString("name");
                        String desc = rs.getString("desc");
                        String msg = rs.getString("msg");
                        if (!name.equals("")) {
                            itemName.put(itemid, name);
                        }
                        if (!desc.equals("")) {
                            itemDesc.put(itemid, desc);
                        }
                        if (!msg.equals("")) {
                            itemMsg.put(itemid, msg);
                        }
                    }
                    return null;
                });
                return null;
            });
        } else {
            ArrayList<MapleData> mapleDatas = new ArrayList<MapleData>();
            block12:
            for (MapleDataFileEntry filedata : this.stringData.getRoot().getFiles()) {
                switch (filedata.getName()) {
                    case "Eqp.img": {
                        MapleData data = this.stringData.getData(filedata.getName()).getChildByPath("Eqp");
                        for (MapleData typedata : data.getChildren()) {
                            mapleDatas.addAll(typedata.getChildren());
                        }
                        continue block12;
                    }
                    case "Consume.img":
                    case "Ins.img":
                    case "Etc.img":
                    case "Cash.img":
                    case "Pet.img": {
                        MapleData data = filedata.getName().startsWith("Etc") ? this.stringData.getData(filedata.getName()).getChildByPath("Etc") : this.stringData.getData(filedata.getName());
                        mapleDatas.addAll(data.getChildren());
                        break;
                    }
                }
            }
            for (MapleData namedata : mapleDatas) {
                int itemid = Integer.parseInt(namedata.getName());
                String name = MapleDataTool.getString(namedata.getChildByPath("name"), "");
                String desc = MapleDataTool.getString(namedata.getChildByPath("desc"), "");
                String msg = MapleDataTool.getString(namedata.getChildByPath("msg"), "");
                itemName.put(itemid, name);
                itemDesc.put(itemid, desc);
                itemMsg.put(itemid, msg);
            }
            LinkedList<MapleDataProvider> dataProviders = new LinkedList<MapleDataProvider>();
            dataProviders.add(this.chrData);
            dataProviders.add(this.itemData);
            for (MapleDataProvider dataProvider : dataProviders) {
                for (MapleDataFileEntry topData : dataProvider.getRoot().getFiles()) {
                    if (!dataProvider.equals(this.chrData) || !topData.getName().matches("^\\d+\\.img$")) continue;
                    this.addItemDataToRedis(dataProvider.getData(topData.getName()), false);
                }
                for (MapleDataDirectoryEntry topDir : dataProvider.getRoot().getSubdirectories()) {
                    boolean isSpecial = topDir.getName().equals("Special");
                    if (topDir.getName().equalsIgnoreCase("Afterimage")) continue;
                    for (MapleDataFileEntry ifile : topDir.getFiles()) {
                        MapleData iz = dataProvider.getData(topDir.getName() + "/" + ifile.getName());
                        if (dataProvider.equals(this.chrData) || topDir.getName().equals("Pet")) {
                            if (iz.getName().equalsIgnoreCase("CommonFaceCN.img") || iz.getName().equalsIgnoreCase("LinkCashWeaponData.img"))
                                continue;
                            this.addItemDataToRedis(iz, false);
                            continue;
                        }
                        for (MapleData data : iz) {
                            this.addItemDataToRedis(data, isSpecial);
                        }
                    }
                }
            }
            DatabaseLoader.DatabaseConnection.domain(con -> {
                InitializeServer.WzSqlName.wz_itemdata.drop(con);
                SqlTool.update(con, "CREATE TABLE `wz_itemdata` (`itemid` int NOT NULL,`data` mediumtext NOT NULL,`name` text NOT NULL,`desc` text NOT NULL,`msg` text NOT NULL,PRIMARY KEY (`itemid`))");
                for (Map.Entry<Integer, Map<String, Object>> data : itemDataCache.entrySet()) {
                    SqlTool.update(con, "INSERT INTO `wz_itemdata` (`itemid`,`data`,`name`,`desc`,`msg`) VALUES (?,?,?,?,?)", data.getKey(), new JSONObject(data.getValue()).toString(), itemName.getOrDefault(data.getKey(), ""), itemDesc.getOrDefault(data.getKey(), ""), itemMsg.getOrDefault(data.getKey(), ""));
                }
                InitializeServer.WzSqlName.wz_itemdata.update(con);
                return null;
            });
        }
    }

    private void addItemDataToRedis(MapleData data, boolean isSpecial) {
        HashMap<String, Object> info = new HashMap<String, Object>();
        String id = data.getName().endsWith(".img") ? data.getName().substring(0, data.getName().length() - 4).trim() : data.getName().trim();
        if (!id.matches("^\\d+$")) {
            return;
        }
        int itemid = Integer.parseInt(id);
        try {
            for (MapleData mapleData : data) {
                if (isSpecial) {
                    this.putSpecialItemInfo(info, mapleData, itemid);
                    continue;
                }
                switch (mapleData.getName()) {
                    case "info":
                    case "req":
                    case "consumeItem":
                    case "mob":
                    case "replace":
                    case "skill":
                    case "reward":
                    case "spec":
                    case "specEx": {
                        info.put(mapleData.getName(), MapleDataTool.getAllMapleData(mapleData));
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (info.isEmpty()) {
            return;
        }
        itemDataCache.put(itemid, info);
    }

    private void putSpecialItemInfo(Map<String, Object> info, MapleData mapleData, int itemid) {
        if (mapleData.getName().equalsIgnoreCase("name")) {
            itemName.put(itemid, MapleDataTool.getString(mapleData));
        } else if (mapleData.getName().equalsIgnoreCase("desc")) {
            itemDesc.put(itemid, MapleDataTool.getString(mapleData));
        } else if (mapleData.getName().equalsIgnoreCase("icon")) {
            HashMap<String, Object> subinfos = new HashMap<String, Object>();
            if (mapleData.getChildren().isEmpty()) {
                String link = mapleData.getData().toString();
                if (!link.isEmpty()) {
                    String[] split = link.split("/");
                    int splitLength = split.length;
                    for (int i = 0; i < splitLength; ++i) {
                        String s = split[i];
                        if (i != 1 || !StringUtil.isNumber(s)) continue;
                        subinfos.put("_inlink", Integer.valueOf(s));
                        break;
                    }
                }
            } else {
                block1:
                for (MapleData mapleData1 : mapleData.getChildren()) {
                    String link;
                    boolean isHash = mapleData1.getName().equals("_hash");
                    boolean isInLink = mapleData1.getName().equals("_inlink");
                    boolean isOutLink = mapleData1.getName().equals("_outlink");
                    if (isHash) {
                        subinfos.put(mapleData1.getName(), mapleData1.getData().toString());
                        continue;
                    }
                    if (!isInLink && !isOutLink || (link = mapleData1.getData().toString()).isEmpty()) continue;
                    String[] split = link.split("/");
                    for (int i = 0; i < split.length; ++i) {
                        if ((!isInLink || i != 0) && (!isOutLink || i != 3) || !StringUtil.isNumber(split[i])) continue;
                        subinfos.put(mapleData1.getName(), Integer.valueOf(split[i]));
                        continue block1;
                    }
                }
            }
            if (!subinfos.isEmpty()) {
                info.put("info", subinfos);
            }
        }
    }

    public List<StructItemOption> getPotentialInfo(int potId) {
        return potentialData.get(potId);
    }

    public Map<Integer, List<StructItemOption>> getAllPotentialInfo() {
        return this.getPotentialInfos(-1);
    }

    public Map<Integer, List<StructItemOption>> getPotentialInfos(int potId) {
        HashMap<Integer, List<StructItemOption>> ret = new HashMap<Integer, List<StructItemOption>>();
        for (Map.Entry<Integer, List<StructItemOption>> entry : potentialData.entrySet()) {
            if (potId != -1 && entry.getKey() < potId) continue;
            ret.put(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    public String resolvePotentialId(int itemId, int potId) {
        int potLevel;
        int eqLevel = this.getReqLevel(itemId);
        List<StructItemOption> potInfo = this.getPotentialInfo(potId);
        if (eqLevel == 0) {
            potLevel = 1;
        } else {
            potLevel = (eqLevel + 1) / 10;
            ++potLevel;
        }
        if (potId <= 0) {
            return "沒有潛能屬性";
        }
        StructItemOption itemOption = potInfo.get(potLevel - 1);
        String ret = itemOption.opString;
        for (int i = 0; i < itemOption.opString.length(); ++i) {
            int j;
            if (itemOption.opString.charAt(i) != '#') continue;
            for (j = i + 2; j < itemOption.opString.length() && itemOption.opString.substring(i + 1, j).matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_一-龥]+$"); ++j) {
            }
            String curParam = itemOption.opString.substring(i, j);
            String curParamStripped = j != itemOption.opString.length() || itemOption.opString.charAt(itemOption.opString.length() - 1) == '%' ? curParam.substring(1, curParam.length() - 1) : curParam.substring(1);
            String paramValue = Integer.toString(itemOption.get(curParamStripped));
            if (curParam.charAt(curParam.length() - 1) == '%') {
                paramValue = paramValue.concat("%");
            }
            ret = ret.replace(curParam, paramValue);
        }
        return ret;
    }

    public StructItemOption getSocketInfo(int socketId) {
        int grade = ItemConstants.getNebuliteGrade(socketId);
        StructItemOption ret = socketData.get(grade).get(socketId);
        if (grade == -1 || ret == null) {
            return null;
        }
        return ret;
    }

    public Map<Integer, StructItemOption> getAllSocketInfo(int grade) {
        Map<Integer, StructItemOption> ret = new HashMap<Integer, StructItemOption>();
        try {
            ret = socketData.get(grade);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Collection<Integer> getMonsterBookList() {
        return this.mobIds.values();
    }

    public Map<Integer, Integer> getMonsterBook() {
        return this.mobIds;
    }

    public Integer getItemIdByMob(int mobId) {
        return this.mobIds.get(mobId);
    }

    public Pair<Integer, Integer> getPot(int f) {
        return this.potLife.get(f);
    }

    public StructFamiliar getFamiliar(int f) {
        return this.familiars.get(f);
    }

    public Map<Integer, StructFamiliar> getFamiliars() {
        return this.familiars;
    }

    public Map<Integer, String> getAllItemNames() {
        return itemName;
    }

    public long getAllItemSize() {
        return itemDataCache.size();
    }

    public StructAndroid getAndroidInfo(int i) {
        return this.androidInfo.get(i);
    }

    protected MapleData getItemData(int itemId) {
        MapleData ret = null;
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = this.itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = this.itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                }
                if (!iFile.getName().equals(idStr.substring(1) + ".img")) continue;
                ret = this.itemData.getData(topDir.getName() + "/" + iFile.getName());
                return ret;
            }
        }
        root = this.chrData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (!iFile.getName().equals(idStr + ".img")) continue;
                ret = this.chrData.getData(topDir.getName() + "/" + iFile.getName());
                return ret;
            }
        }
        return ret;
    }

    public Point getItemLt(int itemId) {
        MapleData item = this.getItemData(itemId);
        Point pData = (Point) item.getChildByPath("info/lt").getData();
        return pData;
    }

    public Point getItemRb(int itemId) {
        MapleData item = this.getItemData(itemId);
        Point pData = (Point) item.getChildByPath("info/rb").getData();
        return pData;
    }

    public <T> T getItemProperty(int itemId, String path, T defaultValue) {
        Map data = itemDataCache.get(itemId);
        if (data == null) {
            return defaultValue;
        }
        String[] loop = path.split("/");
        Object ret = null;
        for (String key : loop) {
            if (data.containsKey(key)) {
                if (!(data.get(key) instanceof Map)) {
                    ret = data.get(key);
                    break;
                }
            } else {
                return defaultValue;
            }
            data = (Map) data.get(key);
        }
        if (ret == null) {
            ret = data;
        }
        if (ret != null && defaultValue != null && ret.getClass() != defaultValue.getClass()) {
            if (defaultValue instanceof Integer) {
                ret = Integer.valueOf(ret.toString());
            } else if (defaultValue instanceof Double) {
                ret = Double.valueOf(ret.toString());
            }
        }
        return (T) ret;
    }

    public short getSlotMax(int itemId) {
        if (ServerConfig.ITEM_MAXSLOT_MAP.containsKey(itemId) && ServerConfig.ITEM_MAXSLOT_MAP.get(itemId) > 0) {
            return ServerConfig.ITEM_MAXSLOT_MAP.get(itemId);
        }
        int ret = this.getItemProperty(itemId, "info/slotMax", ItemConstants.類型.裝備(itemId) ? 1 : 100);
        return (short) (ret == 1 && ItemConstants.類型.消耗(itemId) ? 100 : ret);
    }

    public int getFamiliarID(int itemId) {
        for (Map.Entry<Integer, StructFamiliar> entry : this.familiars.entrySet()) {
            if (entry.getValue().getMonsterCardID() != itemId) continue;
            return entry.getKey();
        }
        return 0;
    }

    public int getPrice(int itemId) {
        if (this.getItemProperty(itemId, "info/autoPrice", -1) == 1 && this.getItemProperty(itemId, "info/price", 0) == 0) {
            return this.getItemProperty(itemId, "info/lv", -1) * 2;
        }
        return this.getItemProperty(itemId, "info/price", 0);
    }

    public double getUnitPrice(int itemId) {
        double unitPrice = this.getItemProperty(itemId, "info/unitPrice", -1.0);
        return unitPrice == 0.0 ? 1.0 : unitPrice;
    }

    protected int rand(int min, int max) {
        return Math.abs(Randomizer.rand(min, max));
    }

    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        try {
            for (Map.Entry<String, Integer> stat : sta.entrySet()) {
                switch (stat.getKey()) {
                    case "incSTRMin": {
                        equip.setStr((short) (equip.getStr() + this.rand(stat.getValue(), sta.get("incSTRMax"))));
                        break;
                    }
                    case "incDEXMin": {
                        equip.setDex((short) (equip.getDex() + this.rand(stat.getValue(), sta.get("incDEXMax"))));
                        break;
                    }
                    case "incINTMin": {
                        equip.setInt((short) (equip.getInt() + this.rand(stat.getValue(), sta.get("incINTMax"))));
                        break;
                    }
                    case "incLUKMin": {
                        equip.setLuk((short) (equip.getLuk() + this.rand(stat.getValue(), sta.get("incLUKMax"))));
                        break;
                    }
                    case "incPADMin": {
                        equip.setPad((short) (equip.getPad() + this.rand(stat.getValue(), sta.get("incPADMax"))));
                        break;
                    }
                    case "incPDDMin": {
                        equip.setPdd((short) (equip.getPdd() + this.rand(stat.getValue(), sta.get("incPDDMax"))));
                        break;
                    }
                    case "incMADMin": {
                        equip.setMad((short) (equip.getMad() + this.rand(stat.getValue(), sta.get("incMADMax"))));
                        break;
                    }
                    case "incMDDMin": {
                        equip.setMdd((short) (equip.getMdd() + this.rand(stat.getValue(), sta.get("incMDDMax"))));
                        break;
                    }
                    case "incACCMin": {
                        equip.setAcc((short) (equip.getAcc() + this.rand(stat.getValue(), sta.get("incACCMax"))));
                        break;
                    }
                    case "incEVAMin": {
                        equip.setAvoid((short) (equip.getAvoid() + this.rand(stat.getValue(), sta.get("incEVAMax"))));
                        break;
                    }
                    case "incSpeedMin": {
                        equip.setSpeed((short) (equip.getSpeed() + this.rand(stat.getValue(), sta.get("incSpeedMax"))));
                        break;
                    }
                    case "incJumpMin": {
                        equip.setJump((short) (equip.getJump() + this.rand(stat.getValue(), sta.get("incJumpMax"))));
                        break;
                    }
                    case "incMHPMin": {
                        equip.setHp((short) (equip.getHp() + this.rand(stat.getValue(), sta.get("incMHPMax"))));
                        break;
                    }
                    case "incMMPMin": {
                        equip.setMp((short) (equip.getMp() + this.rand(stat.getValue(), sta.get("incMMPMax"))));
                        break;
                    }
                    case "incMaxHPMin": {
                        equip.setHp((short) (equip.getHp() + this.rand(stat.getValue(), sta.get("incMaxHPMax"))));
                        break;
                    }
                    case "incMaxMPMin": {
                        equip.setMp((short) (equip.getMp() + this.rand(stat.getValue(), sta.get("incMaxMPMax"))));
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return equip;
    }

    public <T> T getEquipAdditions(int itemId) {
        return this.getEquipAdditions(itemId, "");
    }

    public <T> T getEquipAdditions(int itemId, String path) {
        return this.getItemProperty(itemId, "info/addition/" + path, null);
    }

    public Map<String, Map<String, Integer>> getEquipIncrements(int itemId) {
        return this.getItemProperty(itemId, "info/level/info", null);
    }

    public List<Integer> getEquipSkills(int itemId) {
        Map<?, ?> data = getItemProperty(itemId, "info/level/case", null);
        if (data == null) {
            return null;
        }
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (Map.Entry<?, ?> entry : data.entrySet()) {
            if (entry.getKey().equals("Skill") && entry.getValue() instanceof Map<?, ?>) {
                for (Map.Entry<?, ?> subentry : ((Map<?, ?>) entry.getValue()).entrySet()) {
                    if (subentry.getValue() instanceof Map<?, ?> && ((Map<?, ?>) subentry.getValue()).containsKey("id")) {
                        ret.add((Integer) ((Map<?, ?>) subentry.getValue()).get("id"));
                    }
                }
            }
        }
        return ret;
    }

    public int getEquipmentSkillsFixLevel(int itemId) {
        return this.getItemProperty(itemId, "info/level/fixLevel", 0);
    }

    public List<Pair<Integer, Integer>> getEquipmentSkills(int itemId) {
        Map<?, ?> data = getItemProperty(itemId, "info/level/case/0/1/EquipmentSkill", null);
        if (data != null) {
            ArrayList<Pair<Integer, Integer>> ret = new ArrayList<Pair<Integer, Integer>>();
            for (Map.Entry entry : data.entrySet()) {
                Map map = (Map) entry.getValue();
                ret.add(new Pair<Integer, Integer>((Integer) map.get("id"), (Integer) map.get("level")));
            }
            return ret;
        }
        return Collections.emptyList();
    }

    public List<Integer> getBonusExps(int itemId) {
        Map<?, ?> data = getItemProperty(itemId, "info/bonusExp", null);
        if (data != null) {
            ArrayList<Integer> ret = new ArrayList<Integer>();
            for (Map.Entry entry : data.entrySet()) {
                Map map = (Map) entry.getValue();
                ret.add((Integer) map.get("incExpR"));
            }
            return ret;
        }
        return Collections.emptyList();
    }

    public boolean canEquip(Equip eqp, int level, int job, int fame, int str, int dex, int luk, int int_, int supremacy) {
        boolean result = false;
        if (JobConstants.is管理員(job)) {
            return true;
        }
        if (level + supremacy >= eqp.getTotalReqLevel() && str >= this.getReqStr(eqp.getItemId()) && dex >= this.getReqDex(eqp.getItemId()) && luk >= this.getReqLuk(eqp.getItemId()) && int_ >= this.getReqInt(eqp.getItemId())) {
            Integer fameReq = this.getReqPOP(eqp.getItemId());
            result = ServerConfig.WORLD_EQUIPCHECKFAME ? (fameReq != null ? fame >= fameReq : fame >= 0) : fameReq == null || fame >= fameReq || fameReq == 0;
        } else if (level + supremacy >= eqp.getTotalReqLevel() && JobConstants.is惡魔復仇者(job)) {
            result = true;
        }
        if (result) {
            result = this.canEquipByJob(eqp.getItemId(), job);
        }
        return result;
    }

    public boolean canEquipByJob(int itemid, int job) {
        int reqJob = this.getReqJob(itemid);
        boolean result = false;
        if (reqJob <= 0) {
            result = true;
        } else if (JobConstants.is劍士(job)) {
            result = (reqJob & 1) != 0;
        } else if (JobConstants.is法師(job)) {
            result = (reqJob & 2) != 0;
        } else if (JobConstants.is弓箭手(job)) {
            result = (reqJob & 4) != 0;
        } else if (JobConstants.is傑諾(job)) {
            result = (reqJob & 8) != 0 || (reqJob & 0x10) != 0;
        } else if (JobConstants.is盜賊(job)) {
            result = (reqJob & 8) != 0;
        } else if (JobConstants.is海盜(job)) {
            boolean bl = result = (reqJob & 0x10) != 0;
        }
        if (result) {
            int reqSpecJob = this.getReqSpecJob(itemid);
            if (reqSpecJob > 0) {
                result = reqSpecJob == job / 100;
            } else if (MapleWeapon.調節器.check(itemid)) {
                result = JobConstants.is阿戴爾(job);
            } else if (MapleWeapon.閃亮克魯.check(itemid)) {
                result = JobConstants.is夜光(job);
            } else if (MapleWeapon.靈魂射手.check(itemid)) {
                result = JobConstants.is天使破壞者(job);
            } else if (MapleWeapon.魔劍.check(itemid)) {
                result = JobConstants.is惡魔復仇者(job);
            } else if (MapleWeapon.雙刀.check(itemid)) {
                result = JobConstants.is影武者(job);
            }
        }
        return result;
    }

    public int getReqLevel(int itemId) {
        return this.getItemProperty(itemId, "info/reqLevel", 0);
    }

    public int getReqJob(int itemId) {
        return this.getItemProperty(itemId, "info/reqJob", 0);
    }

    public int getReqSpecJob(int itemId) {
        int reqSpecJob = this.getItemProperty(itemId, "info/reqSpecJob", 0);
        return reqSpecJob == 0 ? this.getItemProperty(itemId, "info/reqJob2", 0) : reqSpecJob;
    }

    public int getReqStr(int itemId) {
        return this.getItemProperty(itemId, "info/reqSTR", 0);
    }

    public int getReqDex(int itemId) {
        return this.getItemProperty(itemId, "info/reqDEX", 0);
    }

    public int getReqInt(int itemId) {
        return this.getItemProperty(itemId, "info/reqINT", 0);
    }

    public int getReqLuk(int itemId) {
        return this.getItemProperty(itemId, "info/reqLUK", 0);
    }

    public Integer getReqPOP(int itemId) {
        return this.getItemProperty(itemId, "info/reqPOP", null);
    }

    public int getSlots(int itemId) {
        return this.getItemProperty(itemId, "info/tuc", 0);
    }

    public Integer getSetItemID(int itemId) {
        return this.getItemProperty(itemId, "info/setItemID", 0);
    }

    public boolean isEpicItem(int itemId) {
        return this.getItemProperty(itemId, "info/epicItem", 0) == 1;
    }

    public boolean isJokerToSetItem(int itemId) {
        return this.getItemProperty(itemId, "info/jokerToSetItem", 0) == 1;
    }

    public Map<Integer, StructSetItem> getSetItems() {
        return setItemInfo;
    }

    public StructSetItem getSetItem(int setItemId) {
        return setItemInfo.get(setItemId);
    }

    public Map<Integer, Integer> getScrollReqs(int itemId) {
        return this.getItemProperty(itemId, "req", null);
    }

    public int getScrollSuccess(int itemId) {
        return this.getScrollSuccess(itemId, 0);
    }

    public int getScrollSuccess(int itemId, int def) {
        return this.getItemProperty(itemId, "info/success", def);
    }

    public int getScrollCursed(int itemId) {
        return this.getItemProperty(itemId, "info/cursed", 0);
    }

    public Map<String, Integer> getItemBaseInfo(int itemId) {
        Map<?, ?> data = getItemProperty(itemId, "info", null);
        if (data == null) {
            return null;
        }
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        for (Map.Entry entry : data.entrySet()) {
            if (!(entry.getValue() instanceof Integer)) continue;
            ret.put(String.valueOf(entry.getKey()), (Integer) entry.getValue());
        }
        return ret;
    }

    public Item scrollEquipWithId(Item equip, Item scroll, boolean whiteScroll, MapleCharacter chr, int vegas) {
        if (equip.getType() == 1) {
            int success;
            int succ;
            int scrollId = scroll.getItemId();
            if (ItemConstants.類型.裝備強化卷軸(scrollId)) {
                return this.scrollEnhance(equip, scroll, chr);
            }
            if (ItemConstants.類型.潛能卷軸(scrollId)) {
                return this.scrollPotential(equip, scroll, chr);
            }
            if (ItemConstants.類型.附加潛能卷軸(scrollId)) {
                return this.scrollPotentialAdd(equip, scroll, chr);
            }
            if (ItemConstants.類型.回真卷軸(scrollId)) {
                return this.scrollResetEquip(equip, scroll, chr);
            }
            if (ItemConstants.類型.輪迴星火(scrollId)) {
                return this.scrollUpgradeItemEx(equip, scroll, chr, scrollId / 100 == 50645);
            }
            Equip nEquip = (Equip) equip;
            Map<String, Integer> data = getItemProperty(scrollId, "info", null);
            int n = succ = ItemConstants.類型.提升卷(scrollId) && !ItemConstants.類型.武器攻擊力卷軸(scrollId) ? ItemConstants.卷軸.getSuccessTablet((int) scrollId, (int) nEquip.getCurrentUpgradeCount()) : this.getScrollSuccess(scrollId);
            if (succ <= 0 && scrollId != 2040727 && scrollId != 2041058 && ItemConstants.類型.特殊卷軸(scrollId)) {
                succ = 100;
            }
            int curse = ItemConstants.類型.提升卷(scrollId) && !ItemConstants.類型.武器攻擊力卷軸(scrollId) ? ItemConstants.卷軸.getCurseTablet((int) scrollId, (int) nEquip.getCurrentUpgradeCount()) : this.getScrollCursed(scrollId);
            int craft = ItemConstants.類型.白衣卷軸(scrollId) ? 0 : chr.getTrait(MapleTraitType.craft).getLevel() / 10;
            int lucksKey = ItemAttribute.LuckyChance.check(equip.getAttribute()) ? 10 : 0;
            boolean equipScrollSuccess = EnhanceResultType.SCROLL_SUCCESS.check(nEquip.getEnchantBuff());
            int n2 = success = equipScrollSuccess ? 100 : succ + lucksKey + craft + this.getSuccessRates(scroll.getItemId());
            success += vegas == 5610000 && success == 10 ? 20 : (vegas == 5610001 && success == 60 ? 30 : 0);
            if (chr.isAdmin()) {
                chr.dropSpouseMessage(UserChatMessageType.黑_黃, (ItemConstants.類型.特殊卷軸(scrollId) ? "特殊" : "普通") + "卷軸 - 默認幾率: " + succ + "% 傾向加成: " + craft + "% 幸運狀態加成: " + lucksKey + "% 最終概率: " + success + "% 失敗消失幾率: " + curse + "%");
            }
            if (ItemAttribute.LuckyChance.check(equip.getAttribute()) && (!ItemConstants.類型.特殊卷軸(scrollId) || scrollId == 2040727 || scrollId == 2041058)) {
                equip.removeAttribute(ItemAttribute.LuckyChance.getValue());
            }
            if (Randomizer.nextInt(100) <= success) {
                if (data != null) {
                    LotteryRandom lr = new LotteryRandom();
                    block0:
                    switch (scrollId) {
                        case 2612061:
                        case 2613050: {
                            data.put("PAD", Randomizer.rand(9, 12));
                            break;
                        }
                        case 2612062:
                        case 2613051: {
                            data.put("MAD", Randomizer.rand(9, 12));
                            break;
                        }
                        case 2048817:
                        case 2615031:
                        case 2616061: {
                            data.put("PAD", Randomizer.rand(5, 7));
                            break;
                        }
                        case 2046856:
                        case 2048804: {
                            lr.addData((Object) 5, 15);
                            lr.addData((Object) 4, 85);
                            data.put("PAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                        case 2048818:
                        case 2615032:
                        case 2616062: {
                            data.put("MAD", Randomizer.rand(5, 7));
                            break;
                        }
                        case 2046857:
                        case 2048805: {
                            lr.addData((Object) 5, 15);
                            lr.addData((Object) 4, 85);
                            data.put("MAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                        case 2046170:
                        case 2046171:
                        case 2046907:
                        case 2046909: {
                            data.put("PAD", Randomizer.rand(7, 10));
                            break;
                        }
                        case 2046908:
                        case 2046910: {
                            data.put("MAD", Randomizer.rand(7, 10));
                            break;
                        }
                        case 2612010:
                        case 2613000: {
                            data.put("PAD", Randomizer.rand(8, 11));
                            break;
                        }
                        case 2048856: {
                            data.put("PAD", Randomizer.rand(10, 15));
                            break;
                        }
                        case 2048857: {
                            data.put("MAD", Randomizer.rand(10, 15));
                            break;
                        }
                        case 2612099:
                        case 2613086: {
                            data.put("PAD", Randomizer.rand(15, 20));
                            data.put("STR", Randomizer.rand(15, 20));
                            data.put("INT", Randomizer.rand(15, 20));
                            data.put("DEX", Randomizer.rand(15, 20));
                            data.put("LUK", Randomizer.rand(15, 20));
                            break;
                        }
                        case 2612100:
                        case 2613087: {
                            data.put("MAD", Randomizer.rand(15, 20));
                            data.put("STR", Randomizer.rand(15, 20));
                            data.put("INT", Randomizer.rand(15, 20));
                            data.put("DEX", Randomizer.rand(15, 20));
                            data.put("LUK", Randomizer.rand(15, 20));
                            break;
                        }
                        case 2615070:
                        case 2616234: {
                            data.put("PAD", Randomizer.rand(10, 15));
                            break;
                        }
                        case 2615071:
                        case 2616235: {
                            data.put("PAD", Randomizer.rand(10, 15));
                            break;
                        }
                        case 2613001: {
                            data.put("MAD", Randomizer.rand(8, 11));
                            break;
                        }
                        case 2048830:
                        case 2615054:
                        case 2616218: {
                            int i;
                            int[] props = new int[]{1, 2, 4, 6, 31, 28, 13, 7, 5, 2, 1};
                            for (i = 0; i < props.length; ++i) {
                                lr.addData((Object) (5 + i), props[i]);
                            }
                            data.put("PAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                        case 2048831:
                        case 2615055:
                        case 2616219: {
                            int i;
                            int[] props = new int[]{1, 2, 4, 6, 31, 28, 13, 7, 5, 2, 1};
                            for (i = 0; i < props.length; ++i) {
                                lr.addData((Object) (5 + i), props[i]);
                            }
                            data.put("MAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                        case 2612082:
                        case 2612083:
                        case 2613070:
                        case 2613071: {
                            int i;
                            int[] props = new int[]{1, 2, 4, 6, 31, 28, 13, 7, 5, 2, 1};
                            for (i = 0; i < props.length; ++i) {
                                lr.addData((Object) (10 + i), props[i]);
                            }
                            data.put("STR", Integer.valueOf((Integer) lr.random()));
                            data.put("INT", Integer.valueOf((Integer) lr.random()));
                            data.put("DEX", Integer.valueOf((Integer) lr.random()));
                            data.put("LUK", Integer.valueOf((Integer) lr.random()));
                            switch (scrollId) {
                                case 2612082:
                                case 2613070: {
                                    data.put("PAD", Integer.valueOf((Integer) lr.random()));
                                    break block0;
                                }
                            }
                            data.put("MAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                        case 2612089:
                        case 2612090:
                        case 2613076:
                        case 2613077: {
                            int i;
                            int[] props = new int[]{4, 6, 31, 30, 14, 7, 5, 2, 1};
                            for (i = 0; i < props.length; ++i) {
                                lr.addData((Object) (12 + i), props[i]);
                            }
                            data.put("STR", Integer.valueOf((Integer) lr.random()));
                            data.put("INT", Integer.valueOf((Integer) lr.random()));
                            data.put("DEX", Integer.valueOf((Integer) lr.random()));
                            data.put("LUK", Integer.valueOf((Integer) lr.random()));
                            switch (scrollId) {
                                case 2612089:
                                case 2613076: {
                                    data.put("PAD", Integer.valueOf((Integer) lr.random()));
                                    break block0;
                                }
                            }
                            data.put("MAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                        case 2048848:
                        case 2615060:
                        case 2616224: {
                            int i;
                            int[] props = new int[]{4, 6, 31, 30, 14, 7, 5, 2, 1};
                            for (i = 0; i < props.length; ++i) {
                                lr.addData((Object) (7 + i), props[i]);
                            }
                            data.put("PAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                        case 2048849:
                        case 2615061:
                        case 2616225: {
                            int i;
                            int[] props = new int[]{4, 6, 31, 30, 14, 7, 5, 2, 1};
                            for (i = 0; i < props.length; ++i) {
                                lr.addData((Object) (7 + i), props[i]);
                            }
                            data.put("MAD", Integer.valueOf((Integer) lr.random()));
                            break;
                        }
                    }
                }
                switch (scrollId) {
                    case 2049000:
                    case 2049001:
                    case 2049002:
                    case 2049003:
                    case 2049004:
                    case 2049005:
                    case 2049024:
                    case 2049025: {
                        if (nEquip.getCurrentUpgradeCount() + nEquip.getRestUpgradeCount() >= this.getSlots(nEquip.getItemId()) + nEquip.getTotalHammer())
                            break;
                        nEquip.setRestUpgradeCount((byte) (nEquip.getRestUpgradeCount() + 1));
                        break;
                    }
                    case 2049006:
                    case 2049007:
                    case 2049008: {
                        if (nEquip.getCurrentUpgradeCount() + nEquip.getRestUpgradeCount() >= this.getSlots(nEquip.getItemId()) + nEquip.getTotalHammer())
                            break;
                        nEquip.setRestUpgradeCount((byte) (nEquip.getRestUpgradeCount() + 2));
                        break;
                    }
                    case 2040727: {
                        nEquip.addAttribute(ItemAttribute.NonSlip.getValue());
                        break;
                    }
                    case 2041058: {
                        nEquip.addAttribute(ItemAttribute.ColdProof.getValue());
                        break;
                    }
                    case 2610200: {
                        if (ItemAttribute.Hyalinize.check(nEquip.getAttribute())) {
                            nEquip.removeAttribute(ItemAttribute.Hyalinize.getValue());
                            break;
                        }
                        nEquip.addAttribute(ItemAttribute.Hyalinize.getValue());
                        break;
                    }
                    default: {
                        if (ItemConstants.類型.混沌卷軸(scrollId)) {
                            int increase = 0;
                            int stat = ItemConstants.卷軸.getChaosNumber((int) scrollId);
                            int n3 = ItemConstants.類型.樂觀混沌卷軸(scrollId) || this.isNegativeScroll(scrollId) ? 1 : (increase = Randomizer.nextBoolean() ? 1 : -1);
                            if (nEquip.getTotalStr() > 0) {
                                nEquip.setStr((short) (nEquip.getStr() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalDex() > 0) {
                                nEquip.setDex((short) (nEquip.getDex() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalInt() > 0) {
                                nEquip.setInt((short) (nEquip.getInt() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalLuk() > 0) {
                                nEquip.setLuk((short) (nEquip.getLuk() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalPad() > 0) {
                                nEquip.setPad((short) (nEquip.getPad() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalPdd() > 0) {
                                nEquip.setPdd((short) (nEquip.getPdd() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalMad() > 0) {
                                nEquip.setMad((short) (nEquip.getMad() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalMdd() > 0) {
                                nEquip.setMdd((short) (nEquip.getMdd() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalAcc() > 0) {
                                nEquip.setAcc((short) (nEquip.getAcc() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalAvoid() > 0) {
                                nEquip.setAvoid((short) (nEquip.getAvoid() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalSpeed() > 0) {
                                nEquip.setSpeed((short) (nEquip.getSpeed() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalJump() > 0) {
                                nEquip.setJump((short) (nEquip.getJump() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalHp() > 0) {
                                nEquip.setHp((short) (nEquip.getHp() + (Randomizer.nextInt(stat) + 1) * increase));
                            }
                            if (nEquip.getTotalMp() <= 0) break;
                            nEquip.setMp((short) (nEquip.getMp() + (Randomizer.nextInt(stat) + 1) * increase));
                            break;
                        }
                        if (ItemConstants.類型.幸運日卷軸(scroll.getItemId()) || ItemConstants.類型.保護卷軸(scroll.getItemId())) {
                            if (ItemConstants.類型.幸運日卷軸(scroll.getItemId())) {
                                nEquip.addAttribute(ItemAttribute.LuckyChance.getValue());
                            }
                            if (!ItemConstants.類型.保護卷軸(scroll.getItemId())) break;
                            nEquip.addAttribute(ItemAttribute.NonCurse.getValue());
                            break;
                        }
                        if (ItemConstants.類型.安全卷軸(scroll.getItemId())) {
                            nEquip.addAttribute(ItemAttribute.ProtectRUC.getValue());
                            break;
                        }
                        if (ItemConstants.類型.卷軸保護卡(scroll.getItemId())) {
                            nEquip.addAttribute(ItemAttribute.ProtectScroll.getValue());
                            break;
                        }
                        if (ItemConstants.類型.恢復卡(scroll.getItemId())) {
                            nEquip.addAttribute(ItemAttribute.RegressScroll.getValue());
                            break;
                        }
                        if (ItemConstants.類型.白衣卷軸(scrollId)) {
                            int recover = this.getRecover(scrollId);
                            int slots = this.getSlots(nEquip.getItemId());
                            if (nEquip.getCurrentUpgradeCount() + nEquip.getRestUpgradeCount() < slots + nEquip.getTotalHammer()) {
                                nEquip.setRestUpgradeCount((byte) (nEquip.getRestUpgradeCount() + recover));
                                break;
                            }
                            if (!chr.isAdmin()) break;
                            chr.dropMessage(-9, "砸卷錯誤：不存在卷軸升級次數 " + (recover == 0) + " 超過可恢復次數上限 " + (nEquip.getCurrentUpgradeCount() + nEquip.getRestUpgradeCount() >= slots + nEquip.getTotalHammer()));
                            break;
                        }
                        for (Map.Entry entry : data.entrySet()) {
                            if (!(entry.getValue() instanceof Integer)) continue;
                            String key = ((String) entry.getKey()).toUpperCase();
                            key = key.startsWith("INC") ? key.substring(3) : key;
                            Integer value = (Integer) entry.getValue();
                            switch (key) {
                                case "STR": {
                                    nEquip.setStr((short) (nEquip.getStr() + value));
                                    break;
                                }
                                case "DEX": {
                                    nEquip.setDex((short) (nEquip.getDex() + value));
                                    break;
                                }
                                case "INT": {
                                    nEquip.setInt((short) (nEquip.getInt() + value));
                                    break;
                                }
                                case "LUK": {
                                    nEquip.setLuk((short) (nEquip.getLuk() + value));
                                    break;
                                }
                                case "PAD": {
                                    nEquip.setPad((short) (nEquip.getPad() + value));
                                    break;
                                }
                                case "PDD": {
                                    nEquip.setPdd((short) (nEquip.getPdd() + value));
                                    break;
                                }
                                case "MAD": {
                                    nEquip.setMad((short) (nEquip.getMad() + value));
                                    break;
                                }
                                case "MDD": {
                                    nEquip.setMdd((short) (nEquip.getMdd() + value));
                                    break;
                                }
                                case "ACC": {
                                    nEquip.setAcc((short) (nEquip.getAcc() + value));
                                    break;
                                }
                                case "EVA": {
                                    nEquip.setAvoid((short) (nEquip.getAvoid() + value));
                                    break;
                                }
                                case "SPEED": {
                                    nEquip.setSpeed((short) (nEquip.getSpeed() + value));
                                    break;
                                }
                                case "JUMP": {
                                    nEquip.setJump((short) (nEquip.getJump() + value));
                                    break;
                                }
                                case "MHP": {
                                    nEquip.setHp((short) (nEquip.getHp() + value));
                                    break;
                                }
                                case "MMP": {
                                    nEquip.setMp((short) (nEquip.getMp() + value));
                                }
                            }
                        }
                    }
                }
                if (!ItemConstants.類型.白衣卷軸(scrollId) && !ItemConstants.類型.特殊卷軸(scrollId)) {
                    if (ItemAttribute.ProtectRUC.check(nEquip.getAttribute())) {
                        nEquip.removeAttribute(ItemAttribute.ProtectRUC.getValue());
                    }
                    int scrollUseSlots = ItemConstants.類型.阿斯旺卷軸(scrollId) ? this.getSlots(scrollId) : 1;
                    nEquip.setRestUpgradeCount((byte) (nEquip.getRestUpgradeCount() - scrollUseSlots));
                    nEquip.setCurrentUpgradeCount((byte) (nEquip.getCurrentUpgradeCount() + scrollUseSlots));
                }
            } else {
                if (!(whiteScroll || ItemConstants.類型.白衣卷軸(scrollId) || ItemConstants.類型.特殊卷軸(scrollId))) {
                    if (ItemAttribute.ProtectRUC.check(nEquip.getAttribute())) {
                        nEquip.removeAttribute(ItemAttribute.ProtectRUC.getValue());
                        chr.dropSpouseMessage(UserChatMessageType.黑_黃, "由於卷軸的效果，升級次數沒有減少。");
                    } else if (!MapleItemInformationProvider.getInstance().hasSafetyShield(scrollId)) {
                        int scrollUseSlots = ItemConstants.類型.阿斯旺卷軸(scrollId) ? this.getSlots(scrollId) : 1;
                        nEquip.setRestUpgradeCount((byte) (nEquip.getRestUpgradeCount() - scrollUseSlots));
                    }
                }
                if (Randomizer.nextInt(99) + 1 < curse) {
                    return null;
                }
            }
        }
        return equip;
    }

    public Item scrollEnhance(Item equip, Item scroll, MapleCharacter chr) {
        if (!ItemConstants.類型.裝備強化卷軸(scroll.getItemId())) {
            return equip;
        }
        if (equip.getType() != 1) {
            return equip;
        }
        Equip nEquip = (Equip) equip;
        int maxUpgrade = this.getItemProperty(scroll.getItemId(), "info/forceUpgradeWz2/maxUpgrade", 0);
        if (nEquip.getStarForceLevel() >= ItemConstants.卷軸.getMaxEnhance((int) nEquip.getItemId()) || maxUpgrade != 0 && nEquip.getStarForceLevel() >= maxUpgrade) {
            return equip;
        }
        int scrollId = scroll.getItemId();
        boolean noCursed = this.isNoCursedScroll(scrollId);
        int scrollForceUpgrade = this.getForceUpgrade(scrollId);
        int incForce = this.getItemProperty(scrollId, "info/forceUpgradeWz2/incForce", 0);
        int succ = this.getScrollSuccess(scrollId);
        int curse = noCursed ? 0 : 100;
        int craft = chr.getTrait(MapleTraitType.craft).getLevel() / 10;
        if (scrollForceUpgrade == 0 && succ == 0) {
            boolean simple = scroll.getItemId() == 2049301 || scroll.getItemId() == 2049307;
            int start = simple ? 80 : 100;
            byte second = simple ? (byte) 7 : 9;
            byte level = nEquip.getStarForceLevel();
            succ = level <= second ? start - 10 * level : 5 / (level - second);
        }
        int success = succ + succ * craft / 100;
        if (chr.isAdmin()) {
            chr.dropSpouseMessage(UserChatMessageType.黑_黃, (String) (scrollForceUpgrade > 0 ? "星力" + scrollForceUpgrade + "星強化卷" : (incForce > 0 ? "追加" + incForce + "星強化券" : "裝備強化卷軸")) + " - 默認幾率: " + succ + "% 傾向加成: " + craft + "% 最終幾率: " + success + "% 失敗消失幾率: " + curse + "% 卷軸是否失敗不消失裝備: " + noCursed);
        }
        if (!Randomizer.isSuccess(success)) {
            return Randomizer.isSuccess(curse) ? null : nEquip;
        }
        if (scrollForceUpgrade > 0) {
            nEquip.setStarForceLevel((byte) scrollForceUpgrade);
        } else {
            nEquip.setStarForceLevel((byte) (nEquip.getStarForceLevel() + (incForce > 0 ? incForce : 1)));
        }
        return nEquip;
    }

    public Item scrollPotential(Item equip, Item scroll, MapleCharacter chr) {
        String scrollType;
        if (equip.getType() != 1) {
            return equip;
        }
        Equip nEquip = (Equip) equip;
        int scrollId = scroll.getItemId();
        int success = this.getScrollSuccess(scrollId, 0);
        if (ItemConstants.類型.潛能卷軸(scroll.getItemId())) {
            block0:
            switch (scroll.getItemId() / 100) {
                case 20494: {
                    switch (scroll.getItemId()) {
                        case 2049404:
                        case 2049405:
                        case 2049414:
                        case 2049415:
                        case 2049421:
                        case 2049423:
                        case 2049424:
                        case 2049426:
                        case 2049427: {
                            scrollType = "專用";
                            break block0;
                        }
                    }
                    if (scroll.getItemId() >= 2049427 && scroll.getItemId() <= 2049446) {
                        scrollType = "專用";
                        break;
                    }
                    scrollType = "普通";
                    break;
                }
                case 20497: {
                    int nId = scroll.getItemId() % 100 / 10;
                    switch (nId) {
                        case 0:
                        case 1:
                        case 3: {
                            scrollType = "稀有";
                            break block0;
                        }
                        case 4:
                        case 5:
                        case 6:
                        case 9: {
                            scrollType = "罕見";
                            break block0;
                        }
                        case 8: {
                            scrollType = "傳說";
                            break block0;
                        }
                    }
                    scrollType = "未知";
                    break;
                }
                default: {
                    scrollType = "未知";
                    break;
                }
            }
        } else {
            return nEquip;
        }
        if (success == 0) {
            switch (scroll.getItemId()) {
                case 2049402:
                case 2049404:
                case 2049405:
                case 2049406:
                case 2049414:
                case 2049415:
                case 2049417:
                case 2049419:
                case 2049423: {
                    success = 100;
                    break;
                }
                case 2049400:
                case 2049407:
                case 2049412: {
                    success = 90;
                    break;
                }
                case 2049421:
                case 2049424: {
                    success = 80;
                    break;
                }
                case 2049401:
                case 2049408:
                case 2049416: {
                    success = 70;
                    break;
                }
                default: {
                    if (scroll.getItemId() < 2049427 || scroll.getItemId() > 2049446) break;
                    success = 100;
                }
            }
        }
        if (chr.isAdmin()) {
            chr.dropSpouseMessage(UserChatMessageType.黑_黃, scrollType + "潛能附加卷軸 - 成功機率  =  " + success + "%");
        }
        if (success <= 0) {
            chr.dropMessage(1, "卷軸道具: " + scroll.getItemId() + " - " + this.getName(scroll.getItemId()) + " 成功幾率為: " + success + " 該卷軸可能還未修復。");
            chr.sendEnableActions();
            return nEquip;
        }
        if (!("稀有".equalsIgnoreCase(scrollType) || "罕見".equalsIgnoreCase(scrollType) || "傳說".equalsIgnoreCase(scrollType) || nEquip.getState(false) == 0)) {
            return nEquip;
        }
        if (Randomizer.nextInt(100) >= success) {
            return nEquip;
        }
        if ("稀有".equalsIgnoreCase(scrollType)) {
            nEquip.renewPotential(2, false);
        } else if ("罕見".equalsIgnoreCase(scrollType)) {
            nEquip.renewPotential(3, false);
        } else if ("傳說".equalsIgnoreCase(scrollType)) {
            nEquip.renewPotential(4, false);
        } else if (scrollId == 2049419) {
            nEquip.renewPotential(true, false);
        } else {
            nEquip.renewPotential(false);
        }
        return nEquip;
    }

    public Item scrollPotentialAdd(Item equip, Item scroll, MapleCharacter chr) {
        if (equip.getType() != 1) {
            return equip;
        }
        Equip nEquip = (Equip) equip;
        int scrollId = scroll.getItemId();
        int success = this.getScrollSuccess(scrollId, 0);
        if (!ItemConstants.類型.附加潛能卷軸(scroll.getItemId())) {
            chr.sendEnableActions();
            return nEquip;
        }
        if (success <= 0) {
            chr.dropMessage(1, "卷軸道具: " + scrollId + " - " + this.getName(scrollId) + " 成功幾率為: " + success + " 該卷軸可能還未修復。");
            chr.sendEnableActions();
            return nEquip;
        }
        if (chr.isAdmin()) {
            chr.dropSpouseMessage(UserChatMessageType.黑_黃, "附加潛能附加卷軸 - 成功機率  =  " + success + "%");
        }
        if (Randomizer.isSuccess(success)) {
            if (scrollId == 2048306) {
                nEquip.renewPotential(true, true);
            } else if (scrollId / 10 == 204973) {
                nEquip.renewPotential(2, true);
            } else {
                nEquip.renewPotential(true);
            }
        }
        return nEquip;
    }

    public Item scrollResetEquip(Item equip, Item scroll, MapleCharacter chr) {
        boolean perfectReset;
        int lucksKey;
        if (equip.getType() != 1) {
            return equip;
        }
        Equip nEquip = (Equip) equip;
        int scrollId = scroll.getItemId();
        int succe = scrollId == 5064200 ? 100 : this.getScrollSuccess(scrollId);
        int curse = this.getScrollCursed(scrollId);
        int craft = chr.getTrait(MapleTraitType.craft).getLevel() / 10;
        int n = lucksKey = ItemAttribute.LuckyChance.check(equip.getAttribute()) ? 10 : 0;
        if (ItemAttribute.LuckyChance.check(equip.getAttribute())) {
            equip.removeAttribute(ItemAttribute.LuckyChance.getValue());
        }
        int success = succe + craft + lucksKey;
        boolean bl = perfectReset = scrollId == 5064200 || this.getItemProperty(scrollId, "info/perfectReset", 0) == 1;
        if (chr.isAdmin()) {
            chr.dropSpouseMessage(UserChatMessageType.黑_黃, "還原卷軸 - 默認幾率: " + succe + "% 傾向加成: " + craft + "% 幸運卷軸狀態加成: " + lucksKey + "% 最終幾率: " + success + "% 失敗消失幾率: " + curse + "%");
        }
        if (Randomizer.nextInt(100) < success) {
            return this.resetEquipStats(nEquip, (byte) (perfectReset ? 0 : -1));
        }
        if (Randomizer.nextInt(100) < curse) {
            return null;
        }
        return nEquip;
    }

    public Item scrollUpgradeItemEx(Item equip, Item scroll, MapleCharacter chr, boolean cs) {
        if (equip.getType() != 1) {
            return equip;
        }
        Equip nEquip = (Equip) equip;
        long oldFlag = nEquip.getFlameFlag();
        int scrollId = scroll.getItemId();
        long flag = NirvanaFlame.randomStateFromJson(nEquip, scrollId);
        if (flag > -1L) {
            if (ItemConstants.類型.暗黑輪迴星火(scrollId)) {
                chr.setKeyValue("nirvana_flame_state_old", String.valueOf(oldFlag));
                chr.setKeyValue("nirvana_flame_state_equip_position", String.valueOf(nEquip.getPosition()));
                chr.setKeyValue("nirvana_flame_state_new", String.valueOf(flag));
                chr.setKeyValue("nirvana_flame_state_itemId", String.valueOf(scrollId));
            } else {
                nEquip.setFlameFlag(flag);
            }
        }
        return nEquip;
    }

    public Item scrollSealedEquip(Item equip, Item scroll, MapleCharacter chr) {
        if (equip.getType() != 1) {
            return equip;
        }
        Equip nEquip = (Equip) equip;
        if (!nEquip.isSealedEquip()) {
            chr.dropSpouseMessage(UserChatMessageType.黑_黃, "該裝備不是漩渦裝備，無法解除封印。");
            return equip;
        }
        return this.updateSealedEquip(nEquip, this.getSealedEquipInfo(equip.getItemId(), nEquip.getSealedLevel()), true);
    }

    public Equip updateSealedEquip(Equip equip, Map<String, Integer> sealedInfo, boolean up) {
        if (sealedInfo == null) {
            return equip;
        }
        int n = up ? 1 : -1;
        for (Map.Entry<String, Integer> info : sealedInfo.entrySet()) {
            if (info.getKey().endsWith("STR")) {
                equip.setStr((short) (equip.getStr() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("DEX")) {
                equip.setDex((short) (equip.getDex() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("INT")) {
                equip.setInt((short) (equip.getInt() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("LUK")) {
                equip.setLuk((short) (equip.getLuk() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("PDD")) {
                equip.setPdd((short) (equip.getPdd() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("MDD")) {
                equip.setMdd((short) (equip.getMdd() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("MHP")) {
                equip.setHp((short) (equip.getHp() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("MMP")) {
                equip.setMp((short) (equip.getMp() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("PAD")) {
                equip.setPad((short) (equip.getPad() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("MAD")) {
                equip.setMad((short) (equip.getMad() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("ACC")) {
                equip.setAcc((short) (equip.getAcc() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("EVA")) {
                equip.setAvoid((short) (equip.getAvoid() + info.getValue() * n));
                continue;
            }
            if (info.getKey().endsWith("IMDR")) {
                equip.setIgnorePDR((short) (equip.getIgnorePDR() + info.getValue() * n));
                continue;
            }
            if (!info.getKey().endsWith("BDR") && !info.getKey().endsWith("bdR")) continue;
            equip.setBossDamage((short) (equip.getBossDamage() + info.getValue() * n));
        }
        equip.setSealedLevel((byte) (equip.getSealedLevel() + 1));
        equip.setSealedExp(0L);
        return equip;
    }

    public Equip resetEquipStats(Equip oldEquip, byte resetType) {
        Equip newEquip = this.getEquipById(oldEquip.getItemId());
        newEquip.setPlatinumHammer(oldEquip.getPlatinumHammer());
        newEquip.setRestUpgradeCount((byte) (newEquip.getRestUpgradeCount() + oldEquip.getPlatinumHammer()));
        newEquip.setCharmEXP(oldEquip.getCharmEXP());
        newEquip.setEnchantBuff(oldEquip.getEnchantBuff());
        newEquip.setState(oldEquip.getState(false), false);
        newEquip.setState(oldEquip.getState(true), true);
        newEquip.setPotential1(oldEquip.getPotential1());
        newEquip.setPotential2(oldEquip.getPotential2());
        newEquip.setPotential3(oldEquip.getPotential3());
        newEquip.setPotential4(oldEquip.getPotential4());
        newEquip.setPotential5(oldEquip.getPotential5());
        newEquip.setPotential6(oldEquip.getPotential6());
        newEquip.setSocket1(oldEquip.getSocket1());
        newEquip.setSocket2(oldEquip.getSocket2());
        newEquip.setSocket3(oldEquip.getSocket3());
        newEquip.setItemSkin(oldEquip.getItemSkin());
        newEquip.setSoulOptionID(oldEquip.getSoulOptionID());
        newEquip.setSoulSocketID(oldEquip.getSoulSocketID());
        newEquip.setSoulOption(oldEquip.getSoulOption());
        newEquip.setSoulSkill(oldEquip.getSoulSkill());
        newEquip.setPosition(oldEquip.getPosition());
        newEquip.setQuantity(oldEquip.getQuantity());
        newEquip.setAttribute(oldEquip.getAttribute());
        newEquip.setOwner(oldEquip.getOwner());
        newEquip.setGMLog(oldEquip.getGMLog());
        newEquip.setExpiration(oldEquip.getTrueExpiration());
        newEquip.setSN(oldEquip.getSN());
        newEquip.setKarmaCount(oldEquip.getKarmaCount());
        switch (resetType) {
            case 0: {
                if (ItemAttribute.TradeBlock.check(newEquip.getAttribute())) {
                    newEquip.removeAttribute(ItemAttribute.TradeBlock.getValue());
                }
                if (!ItemAttribute.AnimaCube.check(newEquip.getAttribute())) break;
                newEquip.removeAttribute(ItemAttribute.AnimaCube.getValue());
                break;
            }
            case 1: {
                newEquip.setStarForce(new StarForce(oldEquip.getStarForce()));
            }
            default: {
                newEquip.setNirvanaFlame(new NirvanaFlame(oldEquip.getNirvanaFlame()));
            }
        }
        return newEquip;
    }

    public Equip getEquipById(int equipId) {
        return this.getEquipById(equipId, -1);
    }

    public Equip getEquipById(int equipId, int ringId) {
        if (this.isCash(equipId) && ringId <= -1) {
            ringId = MapleInventoryIdentifier.getInstance();
        }
        Map<?, ?> data = this.getItemProperty(equipId, "info", null);
        Equip ret = new Equip(equipId, (short) 0, ringId, 0, (short) 0);
        if (data == null) {
            return ret;
        }
        short stats = ItemConstants.getStat(equipId, 0);
        if (stats > 0) {
            ret.setStr(stats);
            ret.setDex(stats);
            ret.setInt(stats);
            ret.setLuk(stats);
        }
        if ((stats = ItemConstants.getATK(equipId, 0)) > 0) {
            ret.setPad(stats);
            ret.setMad(stats);
        }
        if ((stats = ItemConstants.getHpMp(equipId, 0)) > 0) {
            ret.setHp(stats);
            ret.setMp(stats);
        }
        if ((stats = ItemConstants.getDEF(equipId, 0)) > 0) {
            ret.setPdd(stats);
            ret.setMdd(stats);
        }
        for (Map.Entry entry : data.entrySet()) {
            Number value;
            if (!StringUtil.isNumber(entry.getValue().toString())) continue;
            String key = ((String) entry.getKey()).toUpperCase();
            key = key.startsWith("INC") ? key.substring(3) : key;
            try {
                value = Integer.valueOf(entry.getValue().toString());
            } catch (Exception e) {
                value = Double.parseDouble(entry.getValue().toString());
            }
            switch (key) {
                case "STR": {
                    ret.setStr(value.shortValue());
                    break;
                }
                case "DEX": {
                    ret.setDex(value.shortValue());
                    break;
                }
                case "INT": {
                    ret.setInt(value.shortValue());
                    break;
                }
                case "LUK": {
                    ret.setLuk(value.shortValue());
                    break;
                }
                case "PAD": {
                    ret.setPad(value.shortValue());
                    break;
                }
                case "PDD": {
                    ret.setPdd(value.shortValue());
                    break;
                }
                case "MAD": {
                    ret.setMad(value.shortValue());
                    break;
                }
                case "MDD": {
                    ret.setMdd(value.shortValue());
                    break;
                }
                case "ACC": {
                    ret.setAcc(value.shortValue());
                    break;
                }
                case "EVA": {
                    ret.setAvoid(value.shortValue());
                    break;
                }
                case "SPEED": {
                    ret.setSpeed(value.shortValue());
                    break;
                }
                case "JUMP": {
                    ret.setJump(value.shortValue());
                    break;
                }
                case "MHP": {
                    ret.setHp(value.shortValue());
                    break;
                }
                case "MMP": {
                    ret.setMp(value.shortValue());
                    break;
                }
                case "TUC": {
                    ret.setRestUpgradeCount(value.byteValue());
                    break;
                }
                case "CRAFT": {
                    ret.setHands(value.shortValue());
                    break;
                }
                case "DURABILITY": {
                    ret.setDurability(value.intValue());
                    break;
                }
                case "CHARMEXP": {
                    ret.setCharmEXP(value.shortValue());
                    break;
                }
                case "PVPDAMAGE": {
                    ret.setPVPDamage(value.shortValue());
                    break;
                }
                case "BDR": {
                    ret.setBossDamage(value.shortValue());
                    break;
                }
                case "IMDR": {
                    ret.setIgnorePDR(value.shortValue());
                    break;
                }
                case "DAMR": {
                    ret.setTotalDamage(value.shortValue());
                    break;
                }
                case "ARC": {
                    ret.setARC(value.shortValue());
                    break;
                }
                case "reqLevel": {
                    ret.setReqLevel(value.shortValue());
                }
            }
        }
        Object cash = data.get("cash");
        if (cash != null && Boolean.valueOf(cash.toString()).booleanValue() && ret.getCharmEXP() <= 0) {
            int exp = 0;
            int identifier = equipId / 10000;
            if (ItemConstants.類型.武器(equipId) || identifier == 106) {
                exp = 60;
            } else if (identifier == 100) {
                exp = 50;
            } else if (ItemConstants.類型.飾品(equipId) || identifier == 102 || identifier == 108 || identifier == 107) {
                exp = 40;
            } else if (identifier == 104 || identifier == 105 || identifier == 110) {
                exp = 30;
            }
            ret.setCharmEXP((short) exp);
        }
        ret.setSN(ringId);
        return ret;
    }

    protected short getRandStatFusion(short defaultValue, int value1, int value2) {
        if (defaultValue == 0) {
            return 0;
        }
        int range = (value1 + value2) / 2 - defaultValue;
        int rand = Randomizer.nextInt(Math.abs(range) + 1);
        return (short) (defaultValue + (range < 0 ? -rand : rand));
    }

    protected short getRandStat(short defaultValue, int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        int lMaxRange = (int) Math.min(Math.ceil((double) defaultValue * 0.1), (double) maxRange);
        return (short) (defaultValue - lMaxRange + Randomizer.nextInt(lMaxRange * 2 + 1));
    }

    protected short getRandStatAbove(short defaultValue, int maxRange) {
        if (defaultValue <= 0) {
            return 0;
        }
        int lMaxRange = (int) Math.min(Math.ceil((double) defaultValue * 0.1), (double) maxRange);
        return (short) (defaultValue + Randomizer.nextInt(lMaxRange + 1));
    }

    public Equip randomizeStats(Equip equip) {
        equip.setStr(this.getRandStat(equip.getStr(), 5));
        equip.setDex(this.getRandStat(equip.getDex(), 5));
        equip.setInt(this.getRandStat(equip.getInt(), 5));
        equip.setLuk(this.getRandStat(equip.getLuk(), 5));
        equip.setMad(this.getRandStat(equip.getMad(), 5));
        equip.setPad(this.getRandStat(equip.getPad(), 5));
        equip.setAcc(this.getRandStat(equip.getAcc(), 5));
        equip.setAvoid(this.getRandStat(equip.getAvoid(), 5));
        equip.setJump(this.getRandStat(equip.getJump(), 5));
        equip.setHands(this.getRandStat(equip.getHands(), 5));
        equip.setSpeed(this.getRandStat(equip.getSpeed(), 5));
        equip.setPdd(this.getRandStat(equip.getPdd(), 10));
        equip.setMdd(this.getRandStat(equip.getMdd(), 10));
        equip.setHp(this.getRandStat(equip.getHp(), 10));
        if (!ItemConstants.isDemonShield(equip.getItemId())) {
            equip.setMp(this.getRandStat(equip.getMp(), 10));
        }
        equip.setSealedLevel((byte) (equip.isSealedEquip() ? 1 : 0));
        equip.setBossDamage((short) this.getBossDamageRate(equip.getItemId()));
        equip.setIgnorePDR((short) this.getIgnoreMobDmageRate(equip.getItemId()));
        equip.setTotalDamage((short) this.getTotalDamage(equip.getItemId()));
        equip.setPotential1(this.getOption(equip.getItemId(), 1));
        equip.setPotential2(this.getOption(equip.getItemId(), 2));
        equip.setPotential3(this.getOption(equip.getItemId(), 3));
        return equip;
    }

    public Equip randomizeStats_Above(Equip equip) {
        equip.setStr(this.getRandStatAbove(equip.getStr(), 5));
        equip.setDex(this.getRandStatAbove(equip.getDex(), 5));
        equip.setInt(this.getRandStatAbove(equip.getInt(), 5));
        equip.setLuk(this.getRandStatAbove(equip.getLuk(), 5));
        equip.setMad(this.getRandStatAbove(equip.getMad(), 5));
        equip.setPad(this.getRandStatAbove(equip.getPad(), 5));
        equip.setAcc(this.getRandStatAbove(equip.getAcc(), 5));
        equip.setAvoid(this.getRandStatAbove(equip.getAvoid(), 5));
        equip.setJump(this.getRandStatAbove(equip.getJump(), 5));
        equip.setHands(this.getRandStatAbove(equip.getHands(), 5));
        equip.setSpeed(this.getRandStatAbove(equip.getSpeed(), 5));
        equip.setPdd(this.getRandStatAbove(equip.getPdd(), 10));
        equip.setMdd(this.getRandStatAbove(equip.getMdd(), 10));
        equip.setHp(this.getRandStatAbove(equip.getHp(), 10));
        equip.setMp(this.getRandStatAbove(equip.getMp(), 10));
        equip.setSealedLevel((byte) (equip.isSealedEquip() ? 1 : 0));
        equip.setBossDamage((short) this.getBossDamageRate(equip.getItemId()));
        equip.setIgnorePDR((short) this.getIgnoreMobDmageRate(equip.getItemId()));
        equip.setTotalDamage((short) this.getTotalDamage(equip.getItemId()));
        equip.setPotential1(this.getOption(equip.getItemId(), 1));
        equip.setPotential2(this.getOption(equip.getItemId(), 2));
        equip.setPotential3(this.getOption(equip.getItemId(), 3));
        return equip;
    }

    public Equip fuse(Equip equip1, Equip equip2) {
        if (equip1.getItemId() != equip2.getItemId()) {
            return equip1;
        }
        Equip equip = this.getEquipById(equip1.getItemId());
        equip.setStr(this.getRandStatFusion(equip.getStr(), equip1.getStr(), equip2.getStr()));
        equip.setDex(this.getRandStatFusion(equip.getDex(), equip1.getDex(), equip2.getDex()));
        equip.setInt(this.getRandStatFusion(equip.getInt(), equip1.getInt(), equip2.getInt()));
        equip.setLuk(this.getRandStatFusion(equip.getLuk(), equip1.getLuk(), equip2.getLuk()));
        equip.setMad(this.getRandStatFusion(equip.getMad(), equip1.getMad(), equip2.getMad()));
        equip.setPad(this.getRandStatFusion(equip.getPad(), equip1.getPad(), equip2.getPad()));
        equip.setAcc(this.getRandStatFusion(equip.getAcc(), equip1.getAcc(), equip2.getAcc()));
        equip.setAvoid(this.getRandStatFusion(equip.getAvoid(), equip1.getAvoid(), equip2.getAvoid()));
        equip.setJump(this.getRandStatFusion(equip.getJump(), equip1.getJump(), equip2.getJump()));
        equip.setHands(this.getRandStatFusion(equip.getHands(), equip1.getHands(), equip2.getHands()));
        equip.setSpeed(this.getRandStatFusion(equip.getSpeed(), equip1.getSpeed(), equip2.getSpeed()));
        equip.setPdd(this.getRandStatFusion(equip.getPdd(), equip1.getPdd(), equip2.getPdd()));
        equip.setMdd(this.getRandStatFusion(equip.getMdd(), equip1.getMdd(), equip2.getMdd()));
        equip.setHp(this.getRandStatFusion(equip.getHp(), equip1.getHp(), equip2.getHp()));
        equip.setMp(this.getRandStatFusion(equip.getMp(), equip1.getMp(), equip2.getMp()));
        return equip;
    }

    public int get休彼德蔓徽章點數(int itemId) {
        switch (itemId) {
            case 1182000: {
                return 3;
            }
            case 1182001: {
                return 5;
            }
            case 1182002: {
                return 7;
            }
            case 1182003: {
                return 9;
            }
            case 1182004: {
                return 13;
            }
            case 1182005: {
                return 16;
            }
        }
        return 0;
    }

    public Equip randomize休彼德蔓徽章(Equip equip) {
        int stats = this.get休彼德蔓徽章點數(equip.getItemId());
        if (stats > 0) {
            int prob = equip.getItemId() - 1182000;
            if (Randomizer.nextInt(15) <= prob) {
                equip.setStr((short) Randomizer.nextInt(stats + prob));
            }
            if (Randomizer.nextInt(15) <= prob) {
                equip.setDex((short) Randomizer.nextInt(stats + prob));
            }
            if (Randomizer.nextInt(15) <= prob) {
                equip.setInt((short) Randomizer.nextInt(stats + prob));
            }
            if (Randomizer.nextInt(15) <= prob) {
                equip.setLuk((short) Randomizer.nextInt(stats + prob));
            }
            if (Randomizer.nextInt(30) <= prob) {
                equip.setPad((short) Randomizer.nextInt(stats));
            }
            if (Randomizer.nextInt(10) <= prob) {
                equip.setPdd((short) Randomizer.nextInt(stats * 8));
            }
            if (Randomizer.nextInt(30) <= prob) {
                equip.setMad((short) Randomizer.nextInt(stats));
            }
            if (Randomizer.nextInt(10) <= prob) {
                equip.setMdd((short) Randomizer.nextInt(stats * 8));
            }
            if (Randomizer.nextInt(8) <= prob) {
                equip.setAcc((short) Randomizer.nextInt(stats * 5));
            }
            if (Randomizer.nextInt(8) <= prob) {
                equip.setAvoid((short) Randomizer.nextInt(stats * 5));
            }
            if (Randomizer.nextInt(10) <= prob) {
                equip.setSpeed((short) Randomizer.nextInt(stats));
            }
            if (Randomizer.nextInt(10) <= prob) {
                equip.setJump((short) Randomizer.nextInt(stats));
            }
            if (Randomizer.nextInt(8) <= prob) {
                equip.setHp((short) Randomizer.nextInt(stats * 10));
            }
            if (Randomizer.nextInt(8) <= prob) {
                equip.setMp((short) Randomizer.nextInt(stats * 10));
            }
        }
        return equip;
    }

    public int getTotalStat(Equip equip) {
        return equip.getTotalStr() + equip.getTotalDex() + equip.getTotalInt() + equip.getTotalLuk() + equip.getTotalMad() + equip.getTotalPad() + equip.getTotalJump() + equip.getTotalHands() + equip.getTotalSpeed() + equip.getTotalHp() + equip.getTotalMp() + equip.getTotalPdd();
    }

    public Equip setPotentialState(Equip equip, int state) {
        if (equip.getState(false) == 0) {
            if (state == 1) {
                equip.setPotential1(-17);
            } else if (state == 2) {
                equip.setPotential1(-18);
            } else if (state == 3) {
                equip.setPotential1(-19);
            } else if (state == 4) {
                equip.setPotential1(-20);
            } else {
                equip.setPotential1(-17);
            }
        }
        return equip;
    }

    public MapleStatEffect getItemEffect(int itemId) {
        MapleStatEffect ret = this.itemEffects.get(itemId);
        if (ret == null) {
            MapleData item = this.getItemData(itemId);
            if (item == null || item.getChildByPath("spec") == null) {
                return null;
            }
            ret = MapleStatEffectFactory.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            this.itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public MapleStatEffect getItemEffectEX(int itemId) {
        MapleStatEffect ret = this.itemEffectsEx.get(itemId);
        if (ret == null) {
            MapleData item = this.getItemData(itemId);
            if (item == null || item.getChildByPath("specEx") == null) {
                return null;
            }
            ret = MapleStatEffectFactory.loadItemEffectFromData(item.getChildByPath("specEx"), itemId);
            this.itemEffectsEx.put(itemId, ret);
        }
        return ret;
    }

    public int getCreateId(int id) {
        return this.getItemProperty(id, "info/create", 0);
    }

    public int getCardMobId(int id) {
        return this.getItemProperty(id, "info/mob", 0);
    }

    public int getBagType(int id) {
        return this.getItemProperty(id, "info/bagType", 0);
    }

    public int getWatkForProjectile(int itemId) {
        return this.getItemProperty(itemId, "info/incPAD", 0);
    }

    public boolean canScroll(int scrollid, int itemid) {
        return scrollid / 100 % 100 == itemid / 10000 % 100 || ItemConstants.類型.心臟(itemid) || scrollid / 1000 % 100 == itemid / 100 % 100 || itemid / 1000 == 1802 && scrollid / 100 == 20488;
    }

    public String getName(int itemId) {
        String name = itemName.get(itemId);
        switch (itemId / 1000) {
            case 0: {
                if (name != null && !((String) name).isEmpty()) break;
                name = itemName.get(itemId + 12000);
                break;
            }
            case 2: {
                if (name == null || ((String) name).isEmpty()) {
                    name = itemName.get(itemId + 10000);
                }
                if (name == null || ((String) name).isEmpty()) break;
                name = (String) name + "(身體)";
                break;
            }
            case 12: {
                if (name == null || ((String) name).isEmpty()) break;
                name = (String) name + "(頭部)";
            }
        }
        return name;
    }

    public String getDesc(int itemId) {
        return itemDesc.get(itemId);
    }

    public String getMsg(int itemId) {
        return itemMsg.get(itemId);
    }

    public short getItemMakeLevel(int itemId) {
        return this.getItemProperty(itemId, "info/lv", 0).shortValue();
    }

    public boolean cantSell(int itemId) {
        return this.getItemProperty(itemId, "info/notSale", 0) == 1;
    }

    public boolean isLogoutExpire(int itemId) {
        return this.getItemProperty(itemId, "info/expireOnLogout", 0) == 1;
    }

    public boolean isPickupBlocked(int itemId) {
        return this.getItemProperty(itemId, "info/pickUpBlock", 0) == 1;
    }

    public boolean isPickupRestricted(int itemId) {
        return (this.getItemProperty(itemId, "info/only", 0) == 1 || ItemConstants.isPickupRestricted(itemId)) && itemId != 4001168;
    }

    public boolean isAccountShared(int itemId) {
        return this.getItemProperty(itemId, "info/accountSharable", 0) == 1;
    }

    public boolean isQuestItem(int itemId) {
        return this.getItemProperty(itemId, "info/quest", 0) == 1 && itemId / 10000 != 301;
    }

    public boolean isDropRestricted(int itemId) {
        return this.getItemProperty(itemId, "info/quest", 0) == 1 || this.getItemProperty(itemId, "info/tradeBlock", 0) == 1 || ItemConstants.isDropRestricted(itemId);
    }

    public boolean isTradeBlock(int itemId) {
        return this.getItemProperty(itemId, "info/tradeBlock", 0) == 1;
    }

    public boolean isShareTagEnabled(int itemId) {
        return this.getItemProperty(itemId, "info/accountShareTag", 0) == 1;
    }

    public boolean isSharableOnce(int itemId) {
        return this.getItemProperty(itemId, "info/sharableOnce", 0) == 1;
    }

    public boolean isMobHP(int itemId) {
        return this.getItemProperty(itemId, "info/mobHP", 0) == 1;
    }

    public boolean isEquipTradeBlock(int itemId) {
        return this.getItemProperty(itemId, "info/equipTradeBlock", 0) == 1;
    }

    public boolean isActivatedSocketItem(int itemId) {
        return this.getItemProperty(itemId, "info/nActivatedSocket", 0) == 1;
    }

    public boolean isSuperiorEquip(int itemId) {
        return this.getItemProperty(itemId, "info/superiorEqp", 0) == 1;
    }

    public boolean isOnlyEquip(int itemId) {
        return this.getItemProperty(itemId, "info/onlyEquip", 0) == 1;
    }

    public int getStateChangeItem(int itemId) {
        return this.getItemProperty(itemId, "info/stateChangeItem", 0);
    }

    public int getMeso(int itemId) {
        return this.getItemProperty(itemId, "info/meso", 0);
    }

    public boolean isTradeAvailable(int itemId) {
        return this.getItemProperty(itemId, "info/tradeAvailable", 0) == 1;
    }

    public boolean isPKarmaEnabled(int itemId) {
        return this.getItemProperty(itemId, "info/tradeAvailable", 0) == 2;
    }

    public Pair<Integer, List<Map<String, String>>> getRewardItem(int itemId) {
        Map<?, ?> data = getItemProperty(itemId, "reward", null);
        if (data == null) {
            return null;
        }
        ArrayList ret = new ArrayList();
        int totalprob = 0;
        for (Map.Entry entry : data.entrySet()) {
            HashMap rewards = new HashMap();
            ((Map) entry.getValue()).forEach((o, o2) -> rewards.put((String) o, o2 instanceof Integer ? String.valueOf(o2) : (String) o2));
            ret.add(rewards);
            totalprob += rewards.containsKey("prob") ? Integer.valueOf((String) rewards.get("prob")) : 0;
        }
        return new Pair<Integer, List<Map<String, String>>>(totalprob, ret);
    }

    public Pair<Integer, Map<Integer, Integer>> questItemInfo(int itemId) {
        Integer questId = this.getItemProperty(itemId, "info/questId", 0);
        Map<?, ?> data = this.getItemProperty(itemId, "info/consumeItem", null);
        if (data == null) {
            return null;
        }
        HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
        for (Map.Entry<?, ?> entry : data.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map subentry = (Map) entry.getValue();
                ret.put((Integer) subentry.get(0), (Integer) subentry.get(1));
                continue;
            }
            ret.put((Integer) entry.getValue(), 1);
        }
        return new Pair<Integer, Map<Integer, Integer>>(questId, ret);
    }

    public Map<String, String> replaceItemInfo(int itemId) {
        Map data = this.getItemProperty(itemId, "info/replace", new HashMap());
        if (data == null) {
            return null;
        }
        return data;
    }

    public List<Triple<String, Point, Point>> getAfterImage(String after) {
        return this.afterImage.get(after);
    }

    public String getAfterImage(int itemId) {
        return this.getItemProperty(itemId, "info/afterImage", null);
    }

    public boolean itemExists(int itemId) {
        return ItemConstants.getInventoryType(itemId) != MapleInventoryType.UNDEFINED && itemDataCache.containsKey(itemId);
    }

    public boolean isCash(int itemId) {
        return ItemConstants.getInventoryType(itemId, false) == MapleInventoryType.CASH || itemId / 1000000 == 9 || String.valueOf(this.getItemProperty(itemId, "info/cash", "0")).equals("1");
    }

    public int getExpCardRate(int itemId) {
        return this.getItemProperty(itemId, "info/rate", 0);
    }

    public int getExpCardMinLevel(int itemId) {
        return this.getItemProperty(itemId, "info/minLevel", 1);
    }

    public int getExpCardMaxLevel(int itemId) {
        return this.getItemProperty(itemId, "info/maxLevel", 274);
    }

    public boolean isExpOrDropCardTime(int itemId) {
        Map<Integer, String> data = getItemProperty(itemId, "info/time", null);
        if (data == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/ShangHai"));
        String day = DateUtil.getDayInt(cal.get(7));
        HashMap<String, String> times = new HashMap<String, String>();
        for (String childdata : data.values()) {
            String[] time = childdata.split(":");
            times.put(time[0], time[1]);
        }
        cal.get(7);
        if (times.containsKey(day)) {
            String[] hourspan = ((String) times.get(day)).split("-");
            int starthour = Integer.parseInt(hourspan[0]);
            int endhour = Integer.parseInt(hourspan[1]);
            if (cal.get(11) >= starthour && cal.get(11) <= endhour) {
                return true;
            }
        }
        return false;
    }

    public ScriptedItem getScriptedItemInfo(int itemId) {
        return new ScriptedItem(this.getItemProperty(itemId, "spec/npc", 0), this.getItemProperty(itemId, "spec/Plugin.script", ""), this.getItemProperty(itemId, "spec/runOnPickup", 0) == 1);
    }

    public StructCrossHunterShop getCrossHunterShop(int key) {
        if (this.crossHunterShop.containsKey(key)) {
            return this.crossHunterShop.get(key);
        }
        return null;
    }

    public boolean isFloatCashItem(int itemId) {
        return itemId / 10000 == 512 && this.getItemProperty(itemId, "info/floatType", 0) == 1;
    }

    public short getPetFlagInfo(int itemId) {
        short flag = 0;
        if (itemId / 10000 != 500) {
            return flag;
        }
        if (!this.itemExists(itemId)) {
            return flag;
        }
        if (this.getItemProperty(itemId, "info/pickupItem", 0) == 1) {
            flag = (short) (flag | PetFlag.PET_PICKUP_ITEM.getValue());
        }
        if (this.getItemProperty(itemId, "info/longRange", 0) == 1) {
            flag = (short) (flag | PetFlag.PET_LONG_RANGE.getValue());
        }
        if (this.getItemProperty(itemId, "info/pickupAll", 0) == 1) {
            flag = (short) (flag | PetFlag.PET_DROP_SWEEP.getValue());
        }
        if (this.getItemProperty(itemId, "info/sweepForDrop", 0) == 1) {
            flag = (short) (flag | PetFlag.PET_PICKUP_ALL.getValue());
        }
        if (this.getItemProperty(itemId, "info/consumeHP", 0) == 1) {
            flag = (short) (flag | PetFlag.PET_CONSUME_HP.getValue());
        }
        if (this.getItemProperty(itemId, "info/consumeMP", 0) == 1) {
            flag = (short) (flag | PetFlag.PET_CONSUME_MP.getValue());
        }
        if (this.getItemProperty(itemId, "info/autoBuff", 0) == 1) {
            flag = (short) (flag | PetFlag.LP_PET_AUTO_BUFF.getValue());
        }
        return (short) (flag | ServerConfig.PET_DEFAULT_FLAG);
    }

    public int getPetSetItemID(int itemId) {
        if (itemId / 10000 != 500) {
            return -1;
        }
        return this.getItemProperty(itemId, "info/setItemID", 0);
    }

    public int getItemIncMHPr(int itemId) {
        return this.getItemProperty(itemId, "info/incMHPr", 0);
    }

    public int getItemIncMMPr(int itemId) {
        return this.getItemProperty(itemId, "info/incMMPr", 0);
    }

    public int getSuccessRates(int itemId) {
        if (itemId / 10000 != 204) {
            return 0;
        }
        return this.getItemProperty(itemId, "info/successRates/0", 0);
    }

    public int getForceUpgrade(int itemId) {
        return this.getItemProperty(itemId, "info/forceUpgrade", 0);
    }

    public boolean hasSafetyShield(int itemId) {
        return this.getItemProperty(itemId, "info/safetyShield", 0) == 1;
    }

    public Pair<Integer, Integer> getChairRecovery(int itemId) {
        if (itemId / 10000 != 301) {
            return null;
        }
        return new Pair<Integer, Integer>(this.getItemProperty(itemId, "info/recoveryHP", 0), this.getItemProperty(itemId, "info/recoveryMP", 0));
    }

    public int getBossDamageRate(int itemId) {
        return this.getItemProperty(itemId, "info/bdR", 0);
    }

    public int getIgnoreMobDmageRate(int itemId) {
        return this.getItemProperty(itemId, "info/imdR", 0);
    }

    public int getTotalDamage(int itemId) {
        return this.getItemProperty(itemId, "info/damR", 0);
    }

    public int getOption(int itemId, int level) {
        return this.getItemProperty(itemId, "info/option/" + (level - 1) + "option", 0);
    }

    public int getAndroidType(int itemId) {
        if (itemId / 10000 != 166) {
            return 0;
        }
        return this.getItemProperty(itemId, "info/android", 1);
    }

    public boolean isNoCursedScroll(int itemId) {
        return this.getItemProperty(itemId, "info/noCursed", 0) == 1;
    }

    public boolean isNegativeScroll(int itemId) {
        return this.getItemProperty(itemId, "info/noNegative", 0) == 1;
    }

    public int getRecover(int itemId) {
        return this.getItemProperty(itemId, "info/recover", 0);
    }

    public boolean isExclusiveEquip(int itemId) {
        return this.exclusiveEquip.containsKey(itemId);
    }

    public StructExclusiveEquip getExclusiveEquipInfo(int itemId) {
        int exclusiveId;
        if (this.exclusiveEquip.containsKey(itemId) && this.exclusiveEquipInfo.containsKey(exclusiveId = this.exclusiveEquip.get(itemId).intValue())) {
            return this.exclusiveEquipInfo.get(exclusiveId);
        }
        return null;
    }

    public Map<String, Integer> getSealedEquipInfo(int itemId, int level) {
//        return sealedEquipInfo.computeIfAbsent(itemId, key -> this.getItemProperty((int)key, "info/sealed/info", new HashMap())).getOrDefault(String.valueOf(level), null);
        return sealedEquipInfo.computeIfAbsent(itemId, key -> getItemProperty(key, "info/sealed/info", new HashMap<>())).getOrDefault(String.valueOf(level), null);
    }

    public int getSkillSkinFormSkillId(int itemId) {
        if (itemId / 1000 != 1603) {
            return 0;
        }
        return this.getItemProperty(itemId, "info/skillID", 0);
    }

    public int getInLinkID(int itemId) {
        Integer linkid = this.getItemProperty(itemId, "info/_inlink", 0);
        if (linkid == 0) {
            linkid = this.getItemProperty(itemId, "info/_outlink", 0);
        }
        return linkid != 0 && itemId != linkid ? this.getInLinkID(linkid) : itemId;
    }

    public Map<String, Integer> getBookSkillID(int itemId) {
        return this.getItemProperty(itemId, "info/skill", new HashMap());
    }

    public int getReqEquipLevelMax(int itemId) {
        return this.getItemProperty(itemId, "info/reqEquipLevelMax", ServerConfig.CHANNEL_PLAYER_MAXLEVEL);
    }

    public boolean isSkinExist(int id) {
        return ItemConstants.類型.膚色(id) && itemDataCache.containsKey(id % 100 + 12000);
    }

    public boolean isHairExist(int id) {
        if (String.valueOf(id).length() == 8) {
            id /= 1000;
        }
        return ItemConstants.類型.髮型(id) && itemDataCache.containsKey(id);
    }

    public boolean isFaceExist(int id) {
        if (String.valueOf(id).length() == 8) {
            id /= 1000;
        }
        return ItemConstants.類型.臉型(id) && itemDataCache.containsKey(id);
    }

    public Pair<Integer, Integer> getSocketReqLevel(int itemId) {
        int socketId = itemId % 1000 + 1;
        if (!this.socketReqLevel.containsKey(socketId)) {
            MapleData skillOptionData = this.itemData.getData("SkillOption.img");
            MapleData socketData = skillOptionData.getChildByPath("socket");
            int reqLevelMax = MapleDataTool.getIntConvert(socketId + "/reqLevelMax", socketData, ServerConfig.CHANNEL_PLAYER_MAXLEVEL);
            int reqLevelMin = MapleDataTool.getIntConvert(socketId + "/reqLevelMin", socketData, 70);
            this.socketReqLevel.put(socketId, new Pair<Integer, Integer>(reqLevelMax, reqLevelMin));
        }
        return this.socketReqLevel.get(socketId);
    }

    public int getSoulSkill(int itemId) {
        int soulName = itemId % 1000 + 1;
        if (!this.soulSkill.containsKey(soulName)) {
            MapleData skillOptionData = this.itemData.getData("SkillOption.img");
            MapleData skillData = skillOptionData.getChildByPath("skill");
            int skillId = MapleDataTool.getIntConvert(soulName + "/skillId", skillData, 0);
            this.soulSkill.put(soulName, skillId);
        }
        return this.soulSkill.get(soulName);
    }

    public SoulCollectionEntry getSoulCollection(int soulid) {
        return this.soulCollections.get(soulid);
    }

    public ArrayList<Integer> getTempOption(int itemId) {
        int soulName = itemId % 1000 + 1;
        if (!this.tempOption.containsKey(soulName)) {
            MapleData skillOptionData = this.itemData.getData("SkillOption.img");
            MapleData tempOptionData = skillOptionData.getChildByPath("skill/" + soulName + "/tempOption");
            ArrayList<Integer> pots = new ArrayList<Integer>();
            for (MapleData pot : tempOptionData) {
                pots.add(MapleDataTool.getIntConvert("id", pot, 1));
            }
            this.tempOption.put(soulName, pots);
        }
        return this.tempOption.get(soulName);
    }

    public Map<Integer, Map<Integer, Float>> getFamiliarTable_pad() {
        return this.familiarTable_pad;
    }

    public Map<Integer, Map<Integer, Short>> getFamiliarTable_rchance() {
        return this.familiarTable_rchance;
    }

    public Map<Integer, LinkedList<StructItemOption>> getFamiliar_option() {
        return this.familiar_option;
    }

    public Map<Integer, Integer> getDamageSkinBox() {
        return this.damageSkinBox;
    }

    public int getDamageSkinItemId(int n) {
        return this.damageSkinBox_invert.getOrDefault(n, -1);
    }

    public Map<Integer, VCoreDataEntry> getCoreDatas() {
        return this.coreDatas;
    }

    public VCoreDataEntry getCoreData(int id) {
        return this.coreDatas.get(id);
    }

    public Map<String, List<VCoreDataEntry>> getCoreDatasByType(int type, boolean indieJob, boolean ableGemStone) {
        HashMap<String, List<VCoreDataEntry>> coreDatas = new HashMap<String, List<VCoreDataEntry>>();
        for (VCoreDataEntry vCoreDataEntry : this.coreDatas.values()) {
            if (type != vCoreDataEntry.getType() || !ableGemStone && vCoreDataEntry.isNobAbleGemStone() || indieJob && vCoreDataEntry.getJobs().size() != 1)
                continue;
            for (String job : vCoreDataEntry.getJobs()) {
                coreDatas.computeIfAbsent(job, k -> new ArrayList()).add(vCoreDataEntry);
            }
        }
        return coreDatas;
    }

    public List<VCoreDataEntry> getCoreDatasByJob(int type, String job) {
        return this.getCoreDatasByJob(type, job, false);
    }

    public List<VCoreDataEntry> getCoreDatasByJob(int type, String job, boolean indieJob) {
        return this.getCoreDatasByJob(type, job, indieJob, false);
    }

    public List<VCoreDataEntry> getCoreDatasByJob(int type, String job, boolean indieJob, boolean ableGemStone) {
        return this.getCoreDatasByType(type, indieJob, ableGemStone).getOrDefault(job, new LinkedList());
    }

    public Map<Integer, Map<Integer, Triple<Integer, Integer, Integer>>> getVcores() {
        return this.vcores;
    }

    public Map<Integer, Triple<Integer, Integer, Integer>> getVcores(int type) {
        return this.vcores.get(type);
    }

    public Map<Integer, List<Pair<Integer, String>>> getCoreJobSkills() {
        return this.coreJobSkills;
    }

    public List<Pair<Integer, String>> getCoreJobSkill(int job) {
        return this.coreJobSkills.getOrDefault(job, new LinkedList());
    }

    public int getLife(int itemId) {
        return this.getItemProperty(itemId, "info/life", 90);
    }

    public Map<Integer, Integer> getSoulSkills() {
        return this.soulSkills;
    }

    public boolean isRunOnPickup(int itemId) {
        ScriptedItem si = this.getScriptedItemInfo(itemId);
        return si != null && si.runOnPickup();
    }

    public MapleStatEffect getNickItemEffect(int nickItemID) {
        int nickSkill = this.getItemProperty(nickItemID, "info/nickSkill", 0);
        return nickSkill == 0 ? null : SkillFactory.getSkillEffect(nickSkill, 1);
    }

    public boolean isNickSkillTimeLimited(int nickItemID) {
        int nickSkillTimeLimited = this.getItemProperty(nickItemID, "info/nickSkillTimeLimited", 0);
        return nickSkillTimeLimited == 1;
    }

    public int getLimitedLife(int itemId) {
        return this.getItemProperty(itemId, "info/limitedLife", 0);
    }

    public boolean isNoPetEquipStatMoveItem(int itemId) {
        return this.getItemProperty(itemId, "info/noPetEquipStatMoveItem", 0) == 1;
    }

    public boolean isFixedPotential(int itemId) {
        return this.getItemProperty(itemId, "info/fixedPotential", 0) == 1;
    }

    public boolean isOnly(int itemId) {
        return 2432107 == itemId || this.getItemProperty(itemId, "info/only", 0) == 1;
    }

    public boolean isChoice(int itemId) {
        return this.getItemProperty(itemId, "info/choice", 0) == 1;
    }

    public int getIncCharmExp(int itemId) {
        return this.getItemProperty(itemId, "info/incCharmExp", 0);
    }

    public boolean isConsumeOnPickup(int itemId) {
        return this.getItemProperty(itemId, "spec/onsumeOnPickup", 0) == 1;
    }

    public boolean isBossReward(int itemId) {
        return this.getItemProperty(itemId, "info/bossReward", 0) == 1;
    }
}

