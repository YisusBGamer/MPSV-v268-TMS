/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.SpecialChair
 *  Client.SpecialChairTW
 *  Client.skills.KSPsychicSkillEntry
 *  Net.server.Obstacle
 *  Net.server.SkillCustomInfo
 *  Net.server.life.SpawnPointAreaBoss
 *  Net.server.maps.FieldAttackObjInfo
 *  Net.server.maps.ForceAtomObject
 *  Net.server.maps.MapleMap$ActivateItemReactor
 *  Net.server.maps.MapleMap$AreaRunnable
 *  Net.server.maps.MapleMist
 *  Net.server.maps.MapleNodes$DirectionInfo
 *  Net.server.maps.MapleNodes$MapleNodeInfo
 *  Net.server.maps.MapleNodes$MaplePlatform
 *  Net.server.maps.MapleNodes$MonsterPoint
 *  Net.server.maps.MapleSwordNode
 *  Net.server.maps.TaggedObjRegenInfo
 *  Packet.AdelePacket
 *  Packet.AndroidPacket
 *  Packet.CField
 *  Packet.GuildPacket
 *  Packet.PetPacket
 *  Packet.WillPacket
 *  Server.BossEventHandler.Angel
 *  Server.BossEventHandler.BlackMage
 *  Server.BossEventHandler.Caning
 *  Server.BossEventHandler.Demian.Demian
 *  Server.BossEventHandler.Dunkel
 *  Server.BossEventHandler.Dusk.Dusk
 *  Server.BossEventHandler.Jin.JinHillah
 *  Server.BossEventHandler.Latus
 *  Server.BossEventHandler.Lucid
 *  Server.BossEventHandler.Seren
 *  Server.BossEventHandler.Will
 *  Server.BossEventHandler.kalos
 *  Server.BossEventHandler.spawnX.MapleFlyingSword
 *  Server.world.WorldBroadcastService
 *  SwordieX.client.party.Party
 *  SwordieX.client.party.PartyMember
 *  SwordieX.field.ClockPacket
 *  SwordieX.field.fieldeffect.FieldEffect
 *  connection.packet.FieldPacket
 *  lombok.Generated
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleQuestStatus;
import Client.MonsterFamiliar;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.SpecialChair;
import Client.SpecialChairTW;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleAndroid;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.inventory.NirvanaFlame;
import Client.skills.KSPsychicSkillEntry;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.stat.DeadDebuff;
import Config.configs.EquipConfig;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.ServerConstants;
import Config.constants.enums.ScriptType;
import Config.constants.enums.UserChatMessageType;
import Config.constants.skills.冒險家_技能群組.暗影神偷;
import Database.DatabaseLoader;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.MaplePortal;
import Net.server.Obstacle;
import Net.server.SkillCustomInfo;
import Net.server.Timer;
import Net.server.life.ForcedMobStat;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterInformationProvider;
import Net.server.life.MapleNPC;
import Net.server.life.MonsterDropEntry;
import Net.server.life.MonsterGlobalDropEntry;
import Net.server.life.SpawnPoint;
import Net.server.life.SpawnPointAreaBoss;
import Net.server.life.Spawns;
import Net.server.maps.FieldAttackObjInfo;
import Net.server.maps.FieldLimitType;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleAffectedArea;
import Net.server.maps.MapleExtractor;
import Net.server.maps.MapleFieldAttackObj;
import Net.server.maps.MapleFoothold;
import Net.server.maps.MapleFootholdTree;
import Net.server.maps.MapleLove;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapEffect;
import Net.server.maps.MapleMapFactory;
import Net.server.maps.MapleMapItem;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleMist;
import Net.server.maps.MapleNodes;
import Net.server.maps.MapleQuickMove;
import Net.server.maps.MapleRandomPortal;
import Net.server.maps.MapleReactor;
import Net.server.maps.MapleReactorFactory;
import Net.server.maps.MapleRuneStone;
import Net.server.maps.MapleSkillPet;
import Net.server.maps.MapleSummon;
import Net.server.maps.MapleSwordNode;
import Net.server.maps.MechDoor;
import Net.server.maps.TaggedObjRegenInfo;
import Net.server.maps.TownPortal;
import Net.server.quest.MapleQuest;
import Packet.AdelePacket;
import Packet.AndroidPacket;
import Packet.CField;
import Packet.EffectPacket;
import Packet.GuildPacket;
import Packet.InventoryPacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import Packet.NPCPacket;
import Packet.PetPacket;
import Packet.SummonPacket;
import Packet.UIPacket;
import Packet.WillPacket;
import Plugin.provider.loaders.StringData;
import Plugin.script.ScriptManager;
import Plugin.script.binding.ScriptEvent;
import Server.BossEventHandler.Angel;
import Server.BossEventHandler.BlackMage;
import Server.BossEventHandler.Caning;
import Server.BossEventHandler.Demian.Demian;
import Server.BossEventHandler.Dunkel;
import Server.BossEventHandler.Dusk.Dusk;
import Server.BossEventHandler.Jin.JinHillah;
import Server.BossEventHandler.Latus;
import Server.BossEventHandler.Lucid;
import Server.BossEventHandler.Seren;
import Server.BossEventHandler.Will;
import Server.BossEventHandler.kalos;
import Server.BossEventHandler.spawnX.MapleFlyingSword;
import Server.BossEventHandler.spider.spider;
import Server.channel.ChannelServer;
import Server.login.JobType;
import Server.world.WorldBroadcastService;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import SwordieX.field.ClockPacket;
import SwordieX.field.fieldeffect.FieldEffect;
import com.alibaba.druid.pool.DruidPooledConnection;
import connection.packet.FieldPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;
import tools.Pair;
import tools.Randomizer;
import tools.Triple;

public class MapleMap {
    public static Logger log = LoggerFactory.getLogger("MapleMap");
    private final List<MapleCharacter> characters = new CopyOnWriteArrayList<MapleCharacter>();
    private final List<Spawns> monsterSpawn = new ArrayList<Spawns>();
    private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final Lock runningOidLock = new ReentrantLock();
    private final List<Integer> hideNpc = new ArrayList<Integer>();
    private final AtomicInteger runningOid = new AtomicInteger(500000);
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final AtomicInteger spawnedForcesOnMap = new AtomicInteger(0);
    private final AtomicInteger spawnedAffectedAreaOnMap = new AtomicInteger(0);
    private final Map<Integer, MaplePortal> portals = new HashMap<Integer, MaplePortal>();
    private final Map<Integer, Map<Integer, List<Pair<Integer, Integer>>>> kspsychicObjects = new LinkedHashMap<Integer, Map<Integer, List<Pair<Integer, Integer>>>>();
    private final ReentrantReadWriteLock kspsychicLock = new ReentrantReadWriteLock();
    private final Map<Integer, Integer> ksultimates = new LinkedHashMap<Integer, Integer>();
    private final float monsterRate;
    private final int channel;
    private final int mapid;
    private final transient Map<Integer, SkillCustomInfo> customInfo = new LinkedHashMap<Integer, SkillCustomInfo>();
    private final List<Integer> dced = new ArrayList<Integer>();
    private final int x = 0;
    private final Map<Integer, Pair<String, Point>> objTag = new HashMap<Integer, Pair<String, Point>>();
    private final Map<String, Point> lacheln = new HashMap<String, Point>();
    private final List<Pair<String, Point>> syncFH = new ArrayList<Pair<String, Point>>();
    private List<Point> LucidDream = new ArrayList<Point>();
    private final ReentrantLock mobControllerLock = new ReentrantLock();
    private final List<TaggedObjRegenInfo> taggedObjRegenInfo = new ArrayList<TaggedObjRegenInfo>();
    private final List<FieldAttackObjInfo> fieldAttackObjInfo = new ArrayList<FieldAttackObjInfo>();
    private final ReentrantLock objectMoveLock = new ReentrantLock();
    private final List<Rectangle> randRect = new ArrayList<Rectangle>();
    private final AtomicInteger butterflyCount = new AtomicInteger(0);
    private final Map<String, Integer> incSpawnMob = new HashMap<String, Integer>();
    private final Map<String, Integer> eachIncSpawnMob = new HashMap<String, Integer>();
    public List<MapleQuickMove> QUICK_MOVE;
    private List<MapleMonster> RealSpawns = new ArrayList<MapleMonster>();
    private final int stigmaDeath = 0;
    private MapleFootholdTree footholds = null;
    private float recoveryRate;
    private MapleMapEffect mapEffect;
    private short decHP = 0;
    private short createMobInterval = (short)7000;
    private short top = 0;
    private short bottom = 0;
    private short left = 0;
    private boolean isBlackMage3thSkilled = false;
    private short right = 0;
    private int consumeItemCoolTime = 0;
    private int protectItem = 0;
    private int decHPInterval = 10000;
    private int returnMapId;
    private int timeLimit;
    private int fieldLimit;
    private int maxRegularSpawn = 0;
    private int fixedMob;
    private int forcedReturnMap = 999999999;
    private int instanceid = -1;
    private int lvForceMove = 0;
    private int lvLimit = 0;
    private int qrLimit = 0;
    private int permanentWeather = 0;
    private int partyBonusRate = 0;
    private boolean town;
    private boolean clock;
    private boolean personalShop;
    private boolean miniMapOnOff;
    private boolean everlast = false;
    private boolean dropsDisabled = false;
    private boolean gDropsDisabled = false;
    private boolean soaring = false;
    private boolean isSpawns = true;
    private String onUserEnter;
    private String onFirstUserEnter;
    private List<Point> spawnPoints = new ArrayList<Point>();
    private long lastSpawnTime = 0L;
    private long lastHurtTime = 0L;
    private MapleNodes nodes;
    private Map<Integer, MapleSwordNode> swordNodes;
    private long spawnRuneTime = 0L;
    private int fieldType;
    private boolean entrustedFishing;
    private int decHPr;
    private int limitMobID;
    private int eliteCount;
    private int eliteBossCount;
    private int darkEliteCount;
    private String mapMark;
    private int decMobIntervalR = 0;
    private Pair<Integer, Triple<String, String, String>> dynamicObj = null;
    private int breakTimeFieldStep = -1;
    private long breakTimeFieldTime = 0L;
    private long breakTimeFieldLastTime = 0L;
    private int ownerId = -1;
    private long ownerStartTime = 0L;
    private List<String> areaCtrls;
    private int barrier;
    private int barrierArc;
    private int barrierAut = 0;
    private Map<Integer, Map<Integer, SpecialChairTW>> specialChairTWs;
    private int fieldLevel = -1;
    private ScriptEvent event = null;
    private MapleCharacter player;
    private int areaBroadcastMobId = -1;
    private long sandGlassTime = 0L;
    private int candles = 0;
    private int lightCandles = 0;
    private int reqTouched = 0;
    private boolean userFirstEnter = false;
    private String fieldScript = "";
    private ScriptManager scriptManager;
    private MapleClient c;
    private MapleCharacter chr;
    private final Map<String, ScheduledFuture<?>> timers;
    private ScheduledExecutorService timerInstance;

    public MapleMap(int mapid, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = channel;
        this.returnMapId = returnMapId;
        if (this.returnMapId == 999999999) {
            this.returnMapId = mapid;
        }
        this.monsterRate = GameConstants.getPartyPlay(mapid) > 0 ? (monsterRate - 1.0f) * 2.5f + 1.0f : monsterRate;
        EnumMap objsMap = new EnumMap(MapleMapObjectType.class);
        EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<MapleMapObjectType, ReentrantReadWriteLock>(MapleMapObjectType.class);
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        this.mapobjects = Collections.unmodifiableMap(objsMap);
        this.mapobjectlocks = Collections.unmodifiableMap(objlockmap);
        this.timers = new ConcurrentHashMap();
        this.timerInstance = Executors.newScheduledThreadPool(8);
        this.startMapEffect();
    }

    public static void CreateObstacle(MapleMonster monster, List<Obstacle> obs) {
        monster.getMap().broadcastMessage(MobPacket.createObstacle(monster, obs, (byte)0));
    }

    public static byte[] getDuskObtacles(MapleMonster monster, int rand) {
        ArrayList<Obstacle> obs = new ArrayList<Obstacle>();
        Obstacle ob = null;
        if (rand == 0) {
            ob = new Obstacle(65, new Point(291, -1055), new Point(291, -157), 36, 15, 0, 164, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(412, -1055), new Point(412, -157), 36, 15, 0, 478, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-226, -1055), new Point(-226, -157), 36, 15, 0, 897, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(574, -1055), new Point(574, -157), 36, 15, 0, 1476, 400, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 1) {
            ob = new Obstacle(67, new Point(373, -1055), new Point(373, -157), 36, 15, 0, 294, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-139, -1055), new Point(-139, -157), 36, 15, 0, 866, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(554, -1055), new Point(554, -157), 36, 15, 0, 846, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-370, -1055), new Point(-370, -157), 36, 15, 0, 916, 400, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 2) {
            ob = new Obstacle(66, new Point(-187, -1055), new Point(-187, -157), 36, 15, 0, 151, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-569, -1055), new Point(-569, -157), 36, 15, 0, 1047, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-472, -1055), new Point(-472, -157), 36, 15, 0, 1333, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-206, -1055), new Point(-206, -157), 36, 15, 0, 1124, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-123, -1055), new Point(-123, -157), 36, 15, 0, 689, 401, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 3) {
            ob = new Obstacle(67, new Point(-545, -1055), new Point(-545, -157), 36, 15, 0, 352, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(563, -1055), new Point(563, -157), 36, 15, 0, 1407, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-151, -1055), new Point(-151, -157), 36, 15, 0, 407, 600, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 4) {
            ob = new Obstacle(65, new Point(-151, -1055), new Point(-151, -157), 36, 15, 0, 1133, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-69, -1055), new Point(-69, -157), 36, 15, 0, 641, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-7, -1055), new Point(-7, -157), 36, 15, 0, 1442, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(315, -1055), new Point(315, -157), 36, 15, 0, 1280, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(137, -1055), new Point(137, -157), 36, 15, 0, 1398, 601, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 5) {
            ob = new Obstacle(66, new Point(580, -1055), new Point(580, -157), 36, 15, 0, 787, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(230, -1055), new Point(230, -157), 36, 15, 0, 209, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-405, -1055), new Point(-405, -157), 36, 15, 0, 448, 801, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 6) {
            ob = new Obstacle(66, new Point(-318, -1055), new Point(-318, -157), 36, 15, 0, 662, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-438, -1055), new Point(-438, -157), 36, 15, 0, 1351, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-614, -1055), new Point(-614, -157), 36, 15, 0, 437, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(41, -1055), new Point(41, -157), 36, 15, 0, 794, 600, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 7) {
            ob = new Obstacle(66, new Point(-96, -1055), new Point(-96, -157), 36, 15, 0, 692, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-58, -1055), new Point(-58, -157), 36, 15, 0, 798, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(33, -1055), new Point(33, -157), 36, 15, 0, 330, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-37, -1055), new Point(-37, -157), 36, 15, 0, 1028, 800, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 8) {
            ob = new Obstacle(67, new Point(-358, -1055), new Point(-358, -157), 36, 15, 0, 323, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-395, -1055), new Point(-395, -157), 36, 15, 0, 263, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-391, -1055), new Point(-391, -157), 36, 15, 0, 908, 801, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 9) {
            ob = new Obstacle(65, new Point(-176, -1055), new Point(-176, -157), 36, 15, 0, 638, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(199, -1055), new Point(199, -157), 36, 15, 0, 603, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-128, -1055), new Point(-128, -157), 36, 15, 0, 577, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(35, -1055), new Point(35, -157), 36, 15, 0, 841, 600, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 10) {
            ob = new Obstacle(67, new Point(-25, -1055), new Point(-25, -157), 36, 15, 0, 156, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-416, -1055), new Point(-416, -157), 36, 15, 0, 1284, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(147, -1055), new Point(147, -157), 36, 15, 0, 261, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-273, -1055), new Point(-273, -157), 36, 15, 0, 1092, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(612, -1055), new Point(612, -157), 36, 15, 0, 323, 401, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 11) {
            ob = new Obstacle(65, new Point(-374, -1055), new Point(-374, -157), 36, 15, 0, 999, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-157, -1055), new Point(-157, -157), 36, 15, 0, 245, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(45, -1055), new Point(45, -157), 36, 15, 0, 283, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(376, -1055), new Point(376, -157), 36, 15, 0, 623, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(431, -1055), new Point(431, -157), 36, 15, 0, 1298, 401, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 12) {
            ob = new Obstacle(66, new Point(-41, -1055), new Point(-41, -157), 36, 15, 0, 1397, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-194, -1055), new Point(-194, -157), 36, 15, 0, 304, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-114, -1055), new Point(-114, -157), 36, 15, 0, 1057, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-495, -1055), new Point(-495, -157), 36, 15, 0, 1165, 800, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 13) {
            ob = new Obstacle(67, new Point(136, -1055), new Point(136, -157), 36, 15, 0, 795, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-162, -1055), new Point(-162, -157), 36, 15, 0, 999, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(578, -1055), new Point(578, -157), 36, 15, 0, 613, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(79, -1055), new Point(79, -157), 36, 15, 0, 474, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-614, -1055), new Point(-614, -157), 36, 15, 0, 215, 800, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 14) {
            ob = new Obstacle(65, new Point(-192, -1055), new Point(-192, -157), 36, 15, 0, 1015, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(548, -1055), new Point(548, -157), 36, 15, 0, 1160, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-180, -1055), new Point(-180, -157), 36, 15, 0, 528, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-614, -1055), new Point(-614, -157), 36, 15, 0, 1009, 801, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 15) {
            ob = new Obstacle(66, new Point(-243, -1055), new Point(-243, -157), 36, 15, 0, 740, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(372, -1055), new Point(372, -157), 36, 15, 0, 1289, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(14, -1055), new Point(14, -157), 36, 15, 0, 1386, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(612, -1055), new Point(612, -157), 36, 15, 0, 297, 801, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 16) {
            ob = new Obstacle(67, new Point(199, -1055), new Point(199, -157), 36, 15, 0, 873, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(248, -1055), new Point(248, -157), 36, 15, 0, 683, 800, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 17) {
            ob = new Obstacle(65, new Point(-411, -1055), new Point(-411, -157), 36, 15, 0, 733, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(432, -1055), new Point(432, -157), 36, 15, 0, 284, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-614, -1055), new Point(-614, -157), 36, 15, 0, 896, 601, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 18) {
            ob = new Obstacle(66, new Point(-211, -1055), new Point(-211, -157), 36, 15, 0, 1292, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(237, -1055), new Point(237, -157), 36, 15, 0, 606, 600, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-310, -1055), new Point(-310, -157), 36, 15, 0, 1002, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(338, -1055), new Point(338, -157), 36, 15, 0, 1087, 601, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 19) {
            ob = new Obstacle(65, new Point(-229, -1055), new Point(-229, -157), 36, 15, 0, 763, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-165, -1055), new Point(-165, -157), 36, 15, 0, 136, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-408, -1055), new Point(-408, -157), 36, 15, 0, 370, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-411, -1055), new Point(-411, -157), 36, 15, 0, 401, 600, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 20) {
            ob = new Obstacle(66, new Point(-246, -1055), new Point(-246, -157), 36, 15, 0, 528, 601, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(607, -1055), new Point(607, -157), 36, 15, 0, 1018, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-114, -1055), new Point(-114, -157), 36, 15, 0, 575, 801, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(65, new Point(-133, -1055), new Point(-133, -157), 36, 15, 0, 485, 800, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(-63, -1055), new Point(-63, -157), 36, 15, 0, 685, 600, 1, 898, 0);
            obs.add(ob);
        } else if (rand == 21) {
            ob = new Obstacle(67, new Point(570, -1055), new Point(570, -157), 36, 15, 0, 697, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(-256, -1055), new Point(-256, -157), 36, 15, 0, 1078, 401, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(67, new Point(361, -1055), new Point(361, -157), 36, 15, 0, 1285, 400, 1, 898, 0);
            obs.add(ob);
            ob = new Obstacle(66, new Point(612, -1055), new Point(612, -157), 36, 15, 0, 796, 601, 1, 898, 0);
            obs.add(ob);
        }
        if (!obs.isEmpty()) {
            // empty if block
        }
        return new byte[0];
    }

    public MapleCharacter getChr() {
        return this.chr;
    }

    public int getNumMonsters() {
        return this.mapobjects.get((Object)MapleMapObjectType.MONSTER).size();
    }

    public List<spider> getWebInRange(Point from, double rangeSq, List<MapleMapObjectType> MapObject_types) {
        List<MapleMapObject> mapobject = this.getMapObjectsInRange(from, rangeSq);
        ArrayList<spider> webs = new ArrayList<spider>();
        for (int i = 0; i < mapobject.size(); ++i) {
            if (mapobject.get(i).getType() != MapleMapObjectType.WEB) continue;
            webs.add((spider)mapobject.get(i));
        }
        return webs;
    }

    public List<spider> getAllspider() {
        return this.getWebInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.WEB));
    }

    public int getCustomValue0(int skillid) {
        if (this.customInfo.containsKey(skillid)) {
            return (int)this.customInfo.get(skillid).getValue();
        }
        return 0;
    }

    public int getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public void setFixedMob(int fm) {
        this.fixedMob = fm;
    }

    public int getForceMove() {
        return this.lvForceMove;
    }

    public void setForceMove(int fm) {
        this.lvForceMove = fm;
    }

    public int getLevelLimit() {
        return this.lvLimit;
    }

    public void setLevelLimit(int fm) {
        this.lvLimit = fm;
    }

    public int getQuestLimit() {
        return this.qrLimit;
    }

    public void setQuestLimit(int fm) {
        this.qrLimit = fm;
    }

    public void setSoaring(boolean b) {
        this.soaring = b;
    }

    public boolean canSoar() {
        return this.soaring;
    }

    public void toggleDrops() {
        this.dropsDisabled = !this.dropsDisabled;
    }

    public void setDrops(boolean b) {
        this.dropsDisabled = b;
    }

    public void toggleGDrops() {
        this.gDropsDisabled = !this.gDropsDisabled;
    }

    public int getId() {
        return this.mapid;
    }

    public MapleMap getReturnMap() {
        ChannelServer channelServer = ChannelServer.getInstance(this.channel);
        return channelServer == null ? null : channelServer.getMapFactory().getMap(this.returnMapId);
    }

    public int getReturnMapId() {
        return this.returnMapId;
    }

    public void setReturnMapId(int rmi) {
        this.returnMapId = rmi;
    }

    public int getForcedReturnId() {
        return this.forcedReturnMap;
    }

    public MapleMap getForcedReturnMap() {
        ChannelServer channelServer = ChannelServer.getInstance(this.channel);
        return channelServer == null ? null : channelServer.getMapFactory().getMap(this.forcedReturnMap);
    }

    public void setForcedReturnMap(int map) {
        this.forcedReturnMap = map;
    }

    public float getRecoveryRate() {
        return this.recoveryRate;
    }

    public void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getFieldLimit() {
        return this.fieldLimit;
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getMapName() {
        return StringData.getMapStringById(this.mapid);
    }

    public String getStreetName() {
        return MapleMapFactory.getMapStreetName(this.mapid);
    }

    public String getFirstUserEnter() {
        return this.onFirstUserEnter;
    }

    public void setFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public String getUserEnter() {
        return this.onUserEnter;
    }

    public void setUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public boolean hasClock() {
        return this.clock;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public boolean isTown() {
        return this.town;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public boolean allowPersonalShop() {
        return this.personalShop;
    }

    public void setPersonalShop(boolean personalShop) {
        this.personalShop = personalShop;
    }

    public boolean getEverlast() {
        return this.everlast;
    }

    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public int getDecHP() {
        return this.decHP;
    }

    public void setDecHP(int delta) {
        if (delta > 0 || this.mapid == 749040100) {
            this.lastHurtTime = System.currentTimeMillis();
        }
        this.decHP = (short)delta;
    }

    public int getDecHPInterval() {
        return this.decHPInterval;
    }

    public void setDecHPInterval(int delta) {
        this.decHPInterval = delta;
    }

    public int getProtectItem() {
        return this.protectItem;
    }

    public void setProtectItem(int delta) {
        this.protectItem = delta;
    }

    public boolean isNpcHide(int npcID) {
        return this.hideNpc.contains(npcID);
    }

    public void addHideNpc(int npcID) {
        this.hideNpc.add(npcID);
    }

    public boolean isMiniMapOnOff() {
        return this.miniMapOnOff;
    }

    public void setMiniMapOnOff(boolean on) {
        this.miniMapOnOff = on;
    }

    public List<Point> getSpawnPoints() {
        return this.spawnPoints;
    }

    public void setSpawnPoints(List<Point> Points) {
        this.spawnPoints = Points;
    }

    public int getReqTouched() {
        return this.reqTouched;
    }

    public void setReqTouched(int reqTouched) {
        this.reqTouched = reqTouched;
    }

    public int getLightCandles() {
        return this.lightCandles;
    }

    public void setLightCandles(int lightCandles) {
        this.lightCandles = lightCandles;
    }

    public int getCandles() {
        return this.candles;
    }

    public void setCandles(int candles) {
        this.candles = candles;
    }

    public long getSandGlassTime() {
        return this.sandGlassTime;
    }

    public void setSandGlassTime(long sandGlassTime) {
        this.sandGlassTime = sandGlassTime;
    }

    public List<MapleCharacter> getAllChracater() {
        return this.getAllCharactersThreadsafe();
    }

    public List<MapleCharacter> getAllCharactersThreadsafe() {
        ArrayList<MapleCharacter> ret = new ArrayList<MapleCharacter>();
        for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
            ret.add((MapleCharacter)mmo);
        }
        return ret;
    }

    public List<MapleMapObject> getCharactersAsMapObjects() {
        return this.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCurrentPartyId() {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter chr = (MapleCharacter)_mmo;
                if (chr.getParty() == null) continue;
                int n = chr.getParty().getId();
                return n;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return -1;
    }

    public void addMapObject(MapleMapObject mapobject) {
        MapleSummon summon;
        if (mapobject == null) {
            return;
        }
        this.runningOidLock.lock();
        try {
            if (mapobject.getObjectId() == 0) {
                mapobject.setObjectId(this.runningOid.getAndIncrement());
            }
        }
        finally {
            this.runningOidLock.unlock();
        }
        this.mapobjectlocks.get((Object)mapobject.getType()).writeLock().lock();
        try {
            this.mapobjects.get((Object)mapobject.getType()).put(mapobject.getObjectId(), mapobject);
            if (mapobject.getType() == MapleMapObjectType.MONSTER) {
                this.spawnedMonstersOnMap.incrementAndGet();
            }
        }
        finally {
            this.mapobjectlocks.get((Object)mapobject.getType()).writeLock().unlock();
        }
        if (mapobject.getType() == MapleMapObjectType.SUMMON && mapobject instanceof MapleSummon && (summon = (MapleSummon)mapobject) != null && summon.getSkillId() == 80011261) {
            Iterator<Item> iterator;
            MapleCharacter chr = summon.getOwner();
            Equip eq = null;
            if (chr != null && (iterator = chr.getInventory(MapleInventoryType.EQUIPPED).listById(1202193).iterator()).hasNext()) {
                Item item = iterator.next();
                eq = (Equip)item;
            }
            int incSpawnMobR = 50;
            this.decMobIntervalR = 50;
            if (chr != null && (eq == null || eq.isMvpEquip())) {
                int eachIncSpawnMobR;
                int enhanceNum = 0;
                if (eq == null) {
                    enhanceNum = 1;
                    incSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_ENC_1_4;
                    eachIncSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_LITTLE_1_4;
                    this.decMobIntervalR = EquipConfig.RUNE_SPAWN_SPEED_TIME_1_4;
                } else {
                    boolean forever = eq.getExpiration() < 0L;
                    if (!forever && !chr.isSilverMvp() || eq.getStarForceLevel() < 10) {
                        enhanceNum = 5;
                        incSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_ENC_5_9;
                        eachIncSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_LITTLE_5_9;
                        this.decMobIntervalR = EquipConfig.RUNE_SPAWN_SPEED_TIME_5_9;
                    } else if (!forever && !chr.isGoldMvp() || eq.getStarForceLevel() < 20) {
                        enhanceNum = 10;
                        incSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_ENC_10_19;
                        eachIncSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_LITTLE_10_19;
                        this.decMobIntervalR = EquipConfig.RUNE_SPAWN_SPEED_TIME_10_19;
                    } else if (!forever && !chr.isDiamondMvp() || eq.getStarForceLevel() < 29) {
                        enhanceNum = 20;
                        incSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_ENC_20_29;
                        eachIncSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_LITTLE_20_29;
                        this.decMobIntervalR = EquipConfig.RUNE_SPAWN_SPEED_TIME_20_29;
                    } else {
                        enhanceNum = 30;
                        incSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_ENC_30;
                        eachIncSpawnMobR = EquipConfig.RUNE_SPAWN_SPEED_LITTLE_30;
                        this.decMobIntervalR = EquipConfig.RUNE_SPAWN_SPEED_TIME_30;
                    }
                }
                if (enhanceNum > 0) {
                    chr.dropSpouseMessage(UserChatMessageType.系統, "MVP" + (enhanceNum == 1 ? "消耗型" : "") + "輪迴碑石" + (String)(enhanceNum > 2 ? "[" + enhanceNum + "★]" : "") + "效果啟動，怪物重生時間-" + this.decMobIntervalR + "% 最大怪物量+" + incSpawnMobR + "% 單次生怪量 +" + eachIncSpawnMobR + "%");
                }
                this.setEachIncSpawnMobR("輪迴", eachIncSpawnMobR);
            }
            this.setIncSpawnMobR("輪迴", incSpawnMobR);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery) {
        this.addMapObject(mapobject);
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter chr = (MapleCharacter)_mmo;
                if (!(chr.getPosition().distance(mapobject.getPosition()) <= (double)mapobject.getRange())) continue;
                packetbakery.sendPackets(chr.getClient());
                chr.addVisibleMapObjectEx(mapobject);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void spawnAndAddRangedMapObject(MapleMapObject mapobject) {
        this.addMapObject(mapobject);
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter chr = (MapleCharacter)_mmo;
                if (!(chr.getPosition().distance(mapobject.getPosition()) <= (double)mapobject.getRange())) continue;
                chr.addVisibleMapObject(mapobject);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    public void removeMapObject(MapleMapObject obj) {
        MapleAffectedArea area;
        MapleSummon summon;
        this.mapobjectlocks.get((Object)obj.getType()).writeLock().lock();
        try {
            MapleMapObject object = (MapleMapObject)this.mapobjects.get((Object)obj.getType()).remove(obj.getObjectId());
            if (object == obj && object.getType() == MapleMapObjectType.MONSTER) {
                this.spawnedMonstersOnMap.decrementAndGet();
            }
        }
        finally {
            this.mapobjectlocks.get((Object)obj.getType()).writeLock().unlock();
        }
        if (obj instanceof MapleSummon && (summon = (MapleSummon)obj).getSkillId() == 80011261) {
            this.removeIncSpawnMobR("輪迴");
            this.removeEachIncSpawnMobR("輪迴");
            this.decMobIntervalR = 0;
        }
        if (obj instanceof MapleAffectedArea && (area = (MapleAffectedArea)obj).isNeedHandle()) {
            for (MapleCharacter chr : this.getCharacters()) {
                area.handleEffect(chr, -2);
                area.handleMonsterEffect(this, -2);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeRangedMapObject(MapleMapObject obj) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter chr = (MapleCharacter)_mmo;
                if (!chr.isMapObjectVisible(obj)) continue;
                chr.removeVisibleMapObject(obj);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    public Point calcPointBelow(Point initial) {
        MapleFoothold fh = this.footholds.findBelow(initial);
        if (fh == null) {
            return new Point(initial);
        }
        int dropY = fh.getY1();
        int x = initial.x;
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            double s5 = Math.cos(Math.atan(s2 / s1)) * ((double)Math.abs(x - fh.getX1()) / Math.cos(Math.atan(s1 / s2)));
            dropY = fh.getY2() < fh.getY1() ? fh.getY1() - (int)s5 : fh.getY1() + (int)s5;
        }
        if (x < this.left + 30) {
            x = this.left + 30;
        }
        if (x > this.right - 30) {
            x = this.right - 30;
        }
        return new Point(x, dropY);
    }

    public Point calcDropPos(Point initial, Point fallback) {
        Point ret = this.calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    public void dropFromMonster(MapleCharacter chr, MapleMonster mob, int delay, boolean instanced, boolean steal) {
        Item idrop;
        if (mob == null || chr == null || ChannelServer.getInstance(this.channel) == null || this.dropsDisabled || mob.dropsDisabled() || chr.getPyramidSubway() != null || ServerConfig.WORLD_BANDROPITEM) {
            return;
        }
        if (this.event != null && this.event.isPractice() || chr.getEventInstance() != null && chr.getEventInstance().isPractice()) {
            return;
        }
        int maxSize = 4999;
        if (!instanced && this.mapobjects.get((Object)MapleMapObjectType.ITEM).size() >= maxSize) {
            this.removeDropsDelay();
            if (chr.isAdmin()) {
                this.removeDrops();
                chr.dropDebugMessage(1, "[系統提示] 當前地圖的道具數量達到 " + maxSize + " 系統已自動清理掉所有地上的物品信息.");
                this.removeDrops();
            }
        }
        if (ServerConfig.TESPIA && ServerConfig.MULTIPLAYER_TEST && (chr.getMap() == null || chr.getMap().getMapObjectsInRange(chr.getPosition(), chr.getRange(), Collections.singletonList(MapleMapObjectType.PLAYER)).size() < 2)) {
            chr.dropMessage(-1, "由於需要測試多人BUG, 測試服必須附近有其他玩家才會掉寶。");
            if (!chr.isIntern()) {
                return;
            }
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte droptype = (byte)(mob.getStats().isExplosiveReward() ? 3 : (mob.getStats().isPublicReward() ? 2 : (chr.getParty() != null ? 1 : 0)));
        int mobpos = mob.getPosition().x;
        int curseRate = 100 - (chr != null && chr.getRuneUseCooldown() > 0 ? 0 : this.getRuneCurseRate());
        DeadDebuff deadDebuff = DeadDebuff.getDebuff(chr, -1);
        if (deadDebuff != null) {
            curseRate = Math.max(0, curseRate - deadDebuff.DecDropR);
        }
        float mesoServerRate = (float)(ChannelServer.getInstance(this.channel).getMesoRate() * curseRate) / 100.0f;
        float dropServerRate = (float)(ChannelServer.getInstance(this.channel).getDropRate() * curseRate) / 100.0f;
        float globalServerRate = (float)(ChannelServer.getInstance(this.channel).getDropgRate() * curseRate) / 100.0f;
        int d = 1;
        Point pos = new Point(0, mob.getPosition().y);
        MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        ArrayList<MonsterDropEntry> drops = new ArrayList<MonsterDropEntry>(mi.retrieveDrop(mob.getId()));
        if (drops.isEmpty()) {
            mi.addMeso(mob, drops);
        }
        if (Math.abs(mob.getMobLevel() - chr.getLevel()) <= 20) {
            drops.addAll(mi.retrieveDrop(9101025));
        }
        if (mob.isEliteMob()) {
            drops.addAll(mi.retrieveDrop(9101067));
        }
        if (mob.getEliteType() == 2) {
            drops.addAll(mi.retrieveDrop(9101064));
        }
        if (mob.getMap() != null && mob.getMap().getBreakTimeFieldStep() > 0 && !mob.isBoss()) {
            drops.addAll(mi.retrieveDrop(9101114));
        }
        if (mob.getMap() != null && mob.getMap().getBarrier() > 0 && !mob.isBoss()) {
            drops.addAll(mi.retrieveDrop(9101084));
        }
        LinkedList<MapleCharacter> attackers = new LinkedList<MapleCharacter>();
        for (MapleMonster.AttackerEntry ae : mob.getAttackers()) {
            if (ae == null) continue;
            for (int cid : ae.getAttackers()) {
                MapleCharacter character = this.getPlayerObject(cid);
                if (character == null) continue;
                attackers.add(character);
            }
        }
        boolean mesoDropped = false;
        boolean pointDropped = false;
        if (drops.size() > 0) {
            Collections.shuffle(drops);
            for (MonsterDropEntry de : drops) {
                if (!de.channels.isEmpty() && !de.channels.contains(this.getChannel()) || de.itemId != -1 && de.itemId == mob.getStolen()) continue;
                LinkedList<MapleCharacter> rewardChrs = new LinkedList<MapleCharacter>();
                if (de.onlySelf) {
                    rewardChrs = new LinkedList(attackers);
                }
                if (rewardChrs.isEmpty()) {
                    rewardChrs.add(chr);
                }
                for (MapleCharacter character : rewardChrs) {
                    int finalDropR = (int)((double)((float)de.chance * dropServerRate) * character.getDropMod() * (double)character.getStat().getDropBuff() / 100.0);
                    if (Randomizer.nextInt(999999) >= finalDropR || mesoDropped && droptype != 3 && de.itemId == 0 || de.itemId / 10000 == 238) continue;
                    pos.x = droptype == 3 ? mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * d / 2)) : mobpos + (d % 2 == 0 ? 20 * (d + 1) / 2 : -(20 * d / 2));
                    if (de.itemId == 0) {
                        int mesos = Randomizer.nextInt(1 + Math.abs(de.maximum - de.minimum)) + de.minimum;
                        if (mesos <= 0) continue;
                        int meso = (int)((double)(mesos * character.getStat().getMesoBuff()) / 100.0 * character.getDropMod() * (double)mesoServerRate);
                        if (character.isAdmin() && ServerConfig.DROP_NOTICE_ON_OFF) {
                            character.dropDebugMessage(1, "[怪物掉落] 楓幣 " + meso + " dropRate：" + (double)finalDropR / 10000.0 + "%");
                        }
                        this.spawnMobMesoDrop(meso, this.calcDropPos(pos, mob.getPosition()), mob, character, false, droptype, delay);
                        mesoDropped = true;
                        d = (byte)(d + 1);
                        continue;
                    }
                    if (de.itemId < 0) {
                        pointDropped = true;
                        int level = mob.getMobLevel();
                        if (level < ServerConfig.mobPointMinLv || de.itemId < -3) continue;
                        int point = Randomizer.rand(de.minimum, de.maximum);
                        if (character.isAdmin() && ServerConfig.DROP_NOTICE_ON_OFF) {
                            character.dropDebugMessage(1, "[怪物掉落] " + (de.itemId == -1 ? "樂豆" : (de.itemId == -2 ? "楓葉點數" : "里程")) + " " + point + "點 dropRate：" + (double)finalDropR / 10000.0 + "%");
                        }
                        this.spawnMobPointDrop(Math.abs(de.itemId), point, this.calcDropPos(pos, mob.getPosition()), mob, character, false, droptype, delay);
                        continue;
                    }
                    if (ItemConstants.getInventoryType(de.itemId, false) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats(ii.getEquipById(de.itemId));
                        if (Randomizer.isSuccess(30) && !ii.isCash(de.itemId)) {
                            NirvanaFlame.randomState((Equip)idrop, 0);
                        }
                    } else {
                        int range = Math.abs(de.maximum - de.minimum);
                        idrop = new Item(de.itemId, (short)0, (short)(de.maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.minimum : 1), 0);
                    }
                    if (de.period > 0) {
                        long period = de.period;
                        if (period < 1000L) {
                            period *= 86400000L;
                        }
                        idrop.setExpiration(System.currentTimeMillis() + period);
                    }
                    idrop.setGMLog("怪物掉落: " + mob.getId() + " 地圖: " + this.mapid + " 時間: " + DateUtil.getCurrentDate());
                    if (ItemConstants.isNoticeItem(de.itemId)) {
                        this.broadcastMessage(MaplePacketCreator.serverNotice(6, "[掉寶提示] 玩家 " + character.getName() + " 在 " + character.getMap().getMapName() + " 殺死 " + mob.getStats().getName() + " 掉落道具 " + ii.getName(de.itemId)));
                    }
                    if (character.isAdmin() && ServerConfig.DROP_NOTICE_ON_OFF) {
                        character.dropDebugMessage(1, "[怪物掉落] " + String.valueOf(idrop) + " dropRate：" + (double)finalDropR / 10000.0 + "%");
                    }
                    MapleMapItem mdrop = new MapleMapItem(idrop, this.calcDropPos(pos, mob.getPosition()), mob, character, droptype, false, de.questid);
                    if (de.onlySelf) {
                        mdrop.setOnlySelfID(character.getId());
                    }
                    this.spawnMobDrop(mdrop, mob, character);
                    d = (byte)(d + 1);
                }
            }
        }
        ArrayList<MonsterGlobalDropEntry> globalEntry = new ArrayList<MonsterGlobalDropEntry>(mi.getGlobalDrop());
        Collections.shuffle(globalEntry);
        for (MonsterGlobalDropEntry de : globalEntry) {
            if (!de.channels.isEmpty() && !de.channels.contains(this.getChannel()) || de.minMobLevel > 0 && de.minMobLevel > mob.getMobLevel() || de.maxMobLevel > 0 && de.maxMobLevel < mob.getMobLevel() || de.chance == 0) continue;
            LinkedList<MapleCharacter> rewardChrs = new LinkedList<MapleCharacter>();
            if (de.onlySelf) {
                rewardChrs = new LinkedList(attackers);
            }
            if (rewardChrs.isEmpty()) {
                rewardChrs.add(chr);
            }
            for (MapleCharacter character : rewardChrs) {
                int finalDropR = (int)((float)de.chance * globalServerRate);
                if (Randomizer.nextInt(999999) >= finalDropR || !(de.continent < 0 || de.continent < 10 && this.mapid / 100000000 == de.continent || de.continent < 100 && this.mapid / 10000000 == de.continent) && (de.continent >= 1000 || this.mapid / 1000000 != de.continent) || this.gDropsDisabled || mesoDropped && droptype != 3 && de.itemId == 0) continue;
                pos.x = droptype == 3 ? mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * d / 2)) : mobpos + (d % 2 == 0 ? 20 * (d + 1) / 2 : -(20 * d / 2));
                if (de.itemId == 0) {
                    int mesos = Randomizer.nextInt(1 + Math.abs(de.Maximum - de.Minimum)) + de.Minimum;
                    if (mesos <= 0) continue;
                    int meso = (int)((double)(mesos * character.getStat().getMesoBuff()) / 100.0 * character.getDropMod() * (double)mesoServerRate);
                    if (character.isAdmin() && ServerConfig.DROP_NOTICE_ON_OFF) {
                        character.dropDebugMessage(1, "[全域怪物掉落] 楓幣 " + meso + " dropRate：" + (double)finalDropR / 10000.0 + "%");
                    }
                    this.spawnMobMesoDrop(meso, this.calcDropPos(pos, mob.getPosition()), mob, character, false, droptype, delay);
                    mesoDropped = true;
                    d = (byte)(d + 1);
                    continue;
                }
                if (de.itemId < 0) {
                    int level;
                    if (pointDropped || (level = mob.getMobLevel()) < ServerConfig.mobPointMinLv || de.itemId < -3) continue;
                    int point = Randomizer.rand(de.Minimum, de.Maximum);
                    if (character.isAdmin() && ServerConfig.DROP_NOTICE_ON_OFF) {
                        character.dropDebugMessage(1, "[全域怪物掉落] " + (de.itemId == -1 ? "樂豆" : (de.itemId == -2 ? "楓葉點數" : "里程")) + " " + point + "點 dropRate：" + (double)finalDropR / 10000.0 + "%");
                    }
                    this.spawnMobPointDrop(Math.abs(de.itemId), point, this.calcDropPos(pos, mob.getPosition()), mob, character, false, droptype, delay);
                    continue;
                }
                if (ItemConstants.getInventoryType(de.itemId, false) == MapleInventoryType.EQUIP) {
                    idrop = ii.randomizeStats(ii.getEquipById(de.itemId));
                    if (Randomizer.isSuccess(30) && !ii.isCash(de.itemId)) {
                        NirvanaFlame.randomState((Equip)idrop, 0);
                    }
                } else {
                    idrop = new Item(de.itemId, (short) 0, (short)(de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1), 0);
                }
                if (de.period > 0) {
                    long period = de.period;
                    if (period < 1000L) {
                        period *= 86400000L;
                    }
                    idrop.setExpiration(System.currentTimeMillis() + period);
                }
                idrop.setGMLog("怪物掉落: " + mob.getId() + " 地圖: " + this.mapid + " (Global) 時間: " + DateUtil.getCurrentDate());
                if (ItemConstants.isNoticeItem(de.itemId)) {
                    this.broadcastMessage(MaplePacketCreator.serverNotice(6, "[掉寶提示] 玩家 " + character.getName() + " 在 " + character.getMap().getMapName() + " 殺死 " + mob.getStats().getName() + " 掉落道具 " + ii.getName(de.itemId)));
                }
                if (character.isAdmin() && ServerConfig.DROP_NOTICE_ON_OFF) {
                    character.dropDebugMessage(1, "[全域怪物掉落] " + String.valueOf(idrop) + " dropRate：" + (double)finalDropR / 10000.0 + "%");
                }
                MapleMapItem mdrop = new MapleMapItem(idrop, this.calcDropPos(pos, mob.getPosition()), mob, character, droptype, false, de.questid);
                if (de.onlySelf) {
                    mdrop.setOnlySelfID(character.getId());
                }
                this.spawnMobDrop(mdrop, mob, character);
                d = (byte)(d + 1);
            }
        }
    }

    public void monsterSelfDestruct(MapleMonster monster) {
        monster.setHp(0L);
        this.killMonster(monster, null, false, false, (byte)2, 0);
    }

    public void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean unRevives, byte animation, int lastSkill) {
        monster.setAnimation(animation);
        if (!unRevives) {
            monster.spawnRevives();
        }
        if (animation == 1 && chr != null && chr.getLevel() >= this.getFieldLevel() - 20) {
            long cooltime;
            if (chr.getRuneUseCooldown() <= 0 && !ServerConfig.RUNE_CLOSE) {
                this.respawnRune();
            }
            boolean randomPortalSpawned = false;
            if (!chr.isBountyHunterCoolDown()) {
                cooltime = 5000L;
                if (Randomizer.isSuccess(30) && (randomPortalSpawned = this.spawnRandomPortal(chr.getId(), MapleRandomPortal.Type.PolloFritto))) {
                    chr.dropMessage(-1, "出現賞金獵人的傳送點！");
                    cooltime = 900000L;
                }
                chr.setBountyHunterTime(cooltime);
            }
            if (!randomPortalSpawned && !chr.isFireWolfCoolDown()) {
                cooltime = 5000L;
                if (Randomizer.isSuccess(30) && (randomPortalSpawned = this.spawnRandomPortal(chr.getId(), MapleRandomPortal.Type.Inferno))) {
                    chr.dropMessage(-1, "出現往烈燄戰狼的巢穴的傳送點！");
                    cooltime = 900000L;
                }
                chr.setFireWolfTime(cooltime);
            }
        }
        if (animation == 1 && chr != null && monster.getMobLevel() <= chr.getLevel() + 20 && monster.getMobLevel() >= 30 && !chr.inEvent() && !this.isBossMap()) {
            if (monster.getEliteGrade() >= 0) {
                if (8644631 == monster.getId()) {
                    this.darkEliteCount = 0;
                }
                if (!chr.inEvent()) {
                    ++this.eliteBossCount;
                    if (this.eliteBossCount < 10) {
                        this.startMapEffect("這個地方充滿了黑暗氣息,好像有什麼事情即將發生的樣子.", 5120228, 0);
                    } else {
                        this.startMapEffect("黑暗氣息尚未消失,這個地方變的更加陰森.", 5120228, 0);
                    }
                }
            } else {
                ++this.eliteCount;
                if (!(this.eliteCount < ServerConfig.ELITE_COUNT || Randomizer.nextInt(1000) >= 100 || monster.getEliteGrade() > 0 || chr.checkEvent() || monster.isFake() || monster.isSoul() || monster.isSpongeMob())) {
                    MapleMonster elite;
                    this.eliteCount = 0;
                    if (this.darkEliteCount >= 10) {
                        MapleMonster randomPortalSpawned = MapleLifeFactory.getEliteMonster(8644631, monster.getStats(), 2);
                    }
                    if (this.eliteBossCount >= 10 && Randomizer.nextInt(1000) < 100 * (this.eliteBossCount - 10)) {
                        this.eliteBossCount = 0;
                        ChannelServer chs = ChannelServer.getInstance(this.channel);
                        Object eliteBossEM = null;
                        ++this.darkEliteCount;
                    }
                    if ((elite = MapleLifeFactory.getEliteMonster(monster.getId())) != null) {
                        elite.registerKill(300000L);
                        this.spawnMonsterOnGroundBelow(elite, monster.getPosition());
                        if (elite.getId() == 8644631) {
                            this.startMapEffect("散發著黑暗出現了黑暗傳令。", 5120228, 0);
                        } else {
                            this.startMapEffect("強大怪物伴隨著黑暗氣息一同出現.", 5120228, 0);
                        }
                    }
                }
            }
        }
        this.disappearMapObject(monster);
        monster.killBy(chr);
        monster.killed();
        int lastKill = monster.getLastKill();
        if (lastKill != -1) {
            monster.killGainExp(chr, lastSkill);
        }
        if (monster.getBuffToGive() >= 0) {
            this.getCharacters().forEach(c1073 -> MapleItemInformationProvider.getInstance().getItemEffect(monster.getBuffToGive()).applyTo((MapleCharacter)c1073));
        }
        monster.getEffects().clear();
        int mobid = monster.getId();
        if (this.mapid / 10000 != 92507) {
            if (mobid == 8810018 && this.mapid == 240060200) {
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "經過無數次的挑戰，終於擊破了闇黑龍王的遠征隊！你們才是龍之林的真正英雄~"));
            } else if (mobid == 8810122 && this.mapid == 240060201) {
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "經過無數次的挑戰，終於擊破了進階闇黑龍王的遠征隊！你們才是龍之林的真正英雄~"));
            } else if (mobid == 8820001 && this.mapid == 270050100) {
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "憑借永不疲倦的熱情打敗皮卡啾的遠征隊啊！你們是真正的時間的勝者！"));
            } else if (mobid == 8820212 && this.mapid == 270051100) {
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "憑借永不疲倦的熱情打敗混沌皮卡啾的遠征隊啊！你們是真正的時間的勝者！"));
            } else if (mobid == 8850011 && this.mapid == 271040200 || mobid == 8850012 && this.mapid == 271040100) {
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "被黑魔法師黑化的西格諾斯女皇終於被永不言敗的遠征隊打倒! 混沌世界得以淨化!"));
            } else if (mobid / 100000 == 93 && chr != null && chr.getMapId() / 1000000 == 955 && this.getMonsters().isEmpty()) {
                switch (chr.getMapId() % 1000 / 100) {
                    case 1: 
                    case 2: {
                        chr.send(MaplePacketCreator.showEffect("aswan/clear"));
                        chr.send(MaplePacketCreator.playSound("Party1/Clear"));
                        break;
                    }
                    case 3: {
                        chr.send(MaplePacketCreator.showEffect("aswan/clearF"));
                        chr.send(MaplePacketCreator.playSound("Party1/Clear"));
                        chr.dropMessage(-1, "你已經通過了所有回合。請通過傳送口移動到外部。");
                    }
                }
            }
        }
        if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            List<MapleMonster> list = this.getMonsters();
            for (MapleMonster mapleMonster : list) {
                if (mapleMonster.getId() < 8800003 || mapleMonster.getId() > 8800010) continue;
                makeZakReal = false;
                break;
            }
            if (makeZakReal) {
                for (MapleMapObject mapleMapObject : list) {
                    MapleMonster mons = (MapleMonster)mapleMapObject;
                    if (mons.getId() != 8800000) continue;
                    Point pos = mons.getPosition();
                    this.killAllMonsters(true);
                    this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), pos);
                    break;
                }
            }
        } else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            List<MapleMonster> list = this.getMonsters();
            for (MapleMonster mapleMonster : list) {
                if (mapleMonster.getId() < 8800103 || mapleMonster.getId() > 8800110) continue;
                makeZakReal = false;
                break;
            }
            if (makeZakReal) {
                for (MapleMonster mapleMonster : list) {
                    if (mapleMonster.getId() != 8800100) continue;
                    Point pos = mapleMonster.getPosition();
                    this.killAllMonsters(true);
                    this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos);
                    break;
                }
            }
        } else if (mobid >= 8800023 && mobid <= 8800030) {
            boolean makeZakReal = true;
            List<MapleMonster> list = this.getMonsters();
            for (MapleMonster mapleMonster : list) {
                if (mapleMonster.getId() < 8800023 || mapleMonster.getId() > 8800030) continue;
                makeZakReal = false;
                break;
            }
            if (makeZakReal) {
                for (MapleMonster mapleMonster : list) {
                    if (mapleMonster.getId() != 8800020) continue;
                    Point pos = mapleMonster.getPosition();
                    this.killAllMonsters(true);
                    this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800022), pos);
                    break;
                }
            }
        } else if (mobid >= 9400903 && mobid <= 9400910) {
            boolean makeZakReal = true;
            List<MapleMonster> list = this.getMonsters();
            for (MapleMonster mapleMonster : list) {
                if (mapleMonster.getId() < 9400903 || mapleMonster.getId() > 9400910) continue;
                makeZakReal = false;
                break;
            }
            if (makeZakReal) {
                for (MapleMonster mapleMonster : list) {
                    if (mapleMonster.getId() != 9400900) continue;
                    Point pos = mapleMonster.getPosition();
                    this.killAllMonsters(true);
                    this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400900), pos);
                    break;
                }
            }
        } else if (mobid == 8820008) {
            for (MapleMapObject mapleMapObject : this.getMonsters()) {
                MapleMonster mons = (MapleMonster)mapleMapObject;
                if (mons.getLinkOid() == monster.getObjectId()) continue;
                this.killMonster(mons, chr, false, false, animation, 0);
            }
        } else if (mobid >= 8820010 && mobid <= 8820014) {
            for (MapleMapObject mapleMapObject : this.getMonsters()) {
                MapleMonster mons = (MapleMonster)mapleMapObject;
                if (mons.getId() == 8820000 || mons.getId() == 8820001 || mons.getObjectId() == monster.getObjectId() || !mons.isAlive() || mons.getLinkOid() != monster.getObjectId()) continue;
                this.killMonster(mons, chr, false, false, animation, 0);
            }
        } else if (mobid == 8820108) {
            for (MapleMapObject mapleMapObject : this.getMonsters()) {
                MapleMonster mons = (MapleMonster)mapleMapObject;
                if (mons.getLinkOid() == monster.getObjectId()) continue;
                this.killMonster(mons, chr, false, false, animation, 0);
            }
        } else if (mobid >= 8820300 && mobid <= 8820304) {
            for (MapleMapObject mapleMapObject : this.getMonsters()) {
                MapleMonster mons = (MapleMonster)mapleMapObject;
                if (mons.getId() == 8820100 || mons.getId() == 8820212 || mons.getObjectId() == monster.getObjectId() || !mons.isAlive() || mons.getLinkOid() != monster.getObjectId()) continue;
                this.killMonster(mons, chr, false, false, animation, 0);
            }
        } else if (monster.isSpongeMob()) {
            for (MapleMapObject mapleMapObject : this.getMonsters()) {
                MapleMonster mons = (MapleMonster)mapleMapObject;
                if (mons.getLinkOid() == monster.getObjectId() && mons.getSponge() != monster) continue;
                this.killMonster(mons, chr, false, true, animation, 0);
            }
        }
        this.eventMobkillCheck(mobid, chr);
        if (!monster.isSoul() && withDrops && lastKill != -1) {
            int n = 0;
            Skill skill;
            MapleCharacter drop;
            if (lastKill <= 0) {
                drop = chr;
            } else {
                drop = this.getPlayerObject(lastKill);
                if (drop == null) {
                    drop = chr;
                }
            }
            int n2 = 200;
            if (lastSkill > 0 && (skill = SkillFactory.getSkill(lastSkill)) != null) {
                n = skill.getDelay() / 3;
            }
            this.dropFromMonster(drop, monster, n, false, false);
        }
    }

    public void eventMobkillCheck(int n2, MapleCharacter player) {
        if (player == null) {
            return;
        }
        ScriptEvent eim = player.getEventInstance();
        if (n2 / 100000 == 98 && player.getMapId() / 10000000 == 95 && this.getMonsters().isEmpty()) {
            switch (player.getMapId() % 1000 / 100) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: {
                    player.send_other(MaplePacketCreator.showEffect("monsterPark/clear"), true);
                    break;
                }
                case 5: {
                    if (player.getMapId() / 1000000 == 952) {
                        player.send_other(MaplePacketCreator.showEffect("monsterPark/clearF"), true);
                        break;
                    }
                    player.send_other(MaplePacketCreator.showEffect("monsterPark/clear"), true);
                    break;
                }
                case 6: {
                    player.send_other(MaplePacketCreator.showEffect("monsterPark/clearF"), true);
                }
            }
            player.send_other(MaplePacketCreator.showEffect("Party1/Clear"), true);
        } else if (n2 / 100000 == 93 && player.getMapId() / 1000000 == 955 && this.getMonsters().isEmpty()) {
            switch (player.getMapId() % 1000 / 100) {
                case 1: 
                case 2: {
                    player.send_other(MaplePacketCreator.showEffect("aswan/clear"), true);
                    player.send_other(MaplePacketCreator.playSound("Party1/Clear"), true);
                    break;
                }
                case 3: {
                    player.send_other(MaplePacketCreator.showEffect("aswan/clearF"), true);
                    player.send_other(MaplePacketCreator.playSound("Party1/Clear"), true);
                    player.dropMessage(-1, "你已經通過了所有回合。請通過傳送口移動到外部。");
                }
            }
        } else if (n2 / 100000 == 93 && (player.getMapId() == 921160200 || player.getMapId() == 921160400) && this.getAllMonster().isEmpty()) {
            this.startMapEffect("請快點移動到下一張地圖。", 5120053);
        } else if (n2 / 100000 == 93 && player.getMapId() / 10000 == 24008 && this.getAllMonster().isEmpty() && eim != null) {
            player.send_other(MaplePacketCreator.showEffect("quest/party/clear"), true);
            player.send_other(MaplePacketCreator.playSound("Party1/Clear"), true);
        }
        boolean bl2 = false;
        switch (player.getMapId()) {
            case 811000100: {
                if (eim == null || !this.getAllMonster().isEmpty() || !eim.getVariable("stage1").equals("1")) break;
                bl2 = true;
                eim.setVariable("stage1", "clear");
                break;
            }
            case 811000200: {
                if (eim == null || !this.getAllMonster().isEmpty() || !eim.getVariable("stage2").equals("5")) break;
                bl2 = true;
                eim.setVariable("stage2", "clear");
                break;
            }
            case 811000300: {
                if (eim == null || !this.getAllMonster().isEmpty()) break;
                if (eim.getVariable("stage3").equals("2") && n2 == 9450014) {
                    bl2 = true;
                    eim.setVariable("stage3", "clear");
                    break;
                }
                if (eim.getVariable("stage3").equals("0")) break;
                break;
            }
            case 811000400: {
                if (eim == null || !this.getAllMonster().isEmpty() || !eim.getVariable("stage4").equals("1")) break;
                bl2 = true;
                eim.setVariable("stage4", "clear");
            }
        }
        if (bl2) {
            player.send_other(MaplePacketCreator.showEffect("aswan/clear"), true);
            player.send_other(MaplePacketCreator.playSound("Party1/Clear"), true);
            this.showPortalEffect("clear2", 1);
            this.showPortalEffect("clear1", 1);
            this.showObjectEffect("gate");
        }
    }

    public List<MapleReactor> getAllReactor() {
        return this.getAllReactorsThreadsafe();
    }

    public List<MapleReactor> getAllReactorsThreadsafe() {
        ArrayList ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values().parallelStream().map(mmo -> (MapleReactor)mmo).collect(Collectors.toCollection(ArrayList::new));
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleRuneStone> getAllRune() {
        return this.getAllRuneThreadsafe();
    }

    public List<MapleRuneStone> getAllRuneThreadsafe() {
        ArrayList ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.RUNE).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.RUNE).values().parallelStream().map(mmo -> (MapleRuneStone)mmo).collect(Collectors.toCollection(ArrayList::new));
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.RUNE).readLock().unlock();
        }
        return ret;
    }

    public List<MapleSummon> getAllSummonsThreadsafe() {
        ArrayList ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.SUMMON).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.SUMMON).values().parallelStream().filter(mmo -> mmo instanceof MapleSummon).map(mmo -> (MapleSummon)mmo).collect(Collectors.toCollection(ArrayList::new));
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.SUMMON).readLock().unlock();
        }
        return ret;
    }

    public List<TownPortal> getAllDoor() {
        return this.getAllTownPortalsThreadsafe();
    }

    public List<TownPortal> getAllTownPortalsThreadsafe() {
        ArrayList ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.TOWN_PORTAL).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.TOWN_PORTAL).values().parallelStream().filter(mmo -> mmo instanceof TownPortal).map(mmo -> (TownPortal)mmo).collect(Collectors.toCollection(ArrayList::new));
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.TOWN_PORTAL).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllRandomPortalThreadsafe() {
        ArrayList ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.RANDOM_PORTAL).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.RANDOM_PORTAL).values().parallelStream().filter(mmo -> mmo instanceof MapleRandomPortal).collect(Collectors.toCollection(ArrayList::new));
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.RANDOM_PORTAL).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMechDoorsThreadsafe() {
        ArrayList ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.TOWN_PORTAL).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.TOWN_PORTAL).values().parallelStream().filter(mmo -> mmo instanceof MechDoor).collect(Collectors.toCollection(ArrayList::new));
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.TOWN_PORTAL).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMerchant() {
        return this.getAllHiredMerchantsThreadsafe();
    }

    public List<MapleMapObject> getAllHiredMerchantsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.HIRED_MERCHANT).readLock().lock();
        try {
            ret.addAll(this.mapobjects.get((Object)MapleMapObjectType.HIRED_MERCHANT).values());
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.HIRED_MERCHANT).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMonster> getAllMonster() {
        return this.getAllMonstersThreadsafe(false);
    }

    public List<MapleMonster> getMonsters() {
        return this.getAllMonstersThreadsafe(true);
    }

    public List<MapleMonster> getAllMonstersThreadsafe(boolean filter) {
        List<MapleMonster> ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.MONSTER).values().parallelStream().filter(mmo -> filter || !((MapleMonster)mmo).getStats().getName().contains("dummy") && !((MapleMonster)mmo).getStats().isFriendly()).map(MapleMonster.class::cast).collect(Collectors.toList());
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getAllUniqueMonsters() {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.MONSTER).values()) {
                int theId = ((MapleMonster)mmo).getId();
                if (ret.contains(theId)) continue;
                ret.add(theId);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    public void killAllMonsters(boolean animate) {
        this.killAllMonsters(null, animate);
    }

    public void killAllMonsters(MapleCharacter chr, boolean animate) {
        this.killAllMonsters(chr, false, animate);
    }

    public void killAllMonsters(MapleCharacter chr, boolean withDrops, boolean animate) {
        for (MapleMonster i298 : this.getMonsters()) {
            if (withDrops) {
                i298.setLastKill(chr.getId());
            }
            this.killMonster(i298, chr, withDrops, true, (byte)(animate ? 1 : 2), 0);
        }
    }

    public void killMonster(int mobID) {
        for (MapleMapObject mapleMapObject : this.getMonsters()) {
            MapleMonster monster = (MapleMonster)mapleMapObject;
            if (monster.getId() != mobID) continue;
            this.killMonster(monster, null, false, false, (byte)1, 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String MapDebug_Log() {
        StringBuilder sb = new StringBuilder("Defeat time : ");
        sb.append(DateUtil.getNowTime());
        sb.append(" | Mapid : ").append(this.mapid);
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            sb.append(" Users [").append(this.getCharacters().size()).append("] | ");
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter mc = (MapleCharacter)_mmo;
                sb.append(mc.getName()).append(", ");
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void limitReactor(int rid, int num) {
        ArrayList<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        LinkedHashMap<Integer, Integer> contained = new LinkedHashMap<Integer, Integer>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor)obj;
                if (contained.containsKey(mr.getReactorId())) {
                    if ((Integer)contained.get(mr.getReactorId()) >= num) {
                        toDestroy.add(mr);
                        continue;
                    }
                    contained.put(mr.getReactorId(), (Integer)contained.get(mr.getReactorId()) + 1);
                    continue;
                }
                contained.put(mr.getReactorId(), 1);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            this.destroyReactor(mr.getObjectId());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroyReactors(int first, int last) {
        ArrayList<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor)obj;
                if (mr.getReactorId() < first || mr.getReactorId() > last) continue;
                toDestroy.add(mr);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            this.destroyReactor(mr.getObjectId());
        }
    }

    public void destroyReactor(int oid) {
        MapleReactor reactor = this.getReactorByOid(oid);
        if (reactor == null) {
            return;
        }
        this.broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
        reactor.setAlive(false);
        this.removeMapObject(reactor);
        reactor.setTimerActive(false);
        if (reactor.getDelay() > 0) {
            Timer.MapTimer.getInstance().schedule(() -> this.respawnReactor(reactor), reactor.getDelay());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reloadReactors() {
        ArrayList<MapleReactor> toSpawn = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor reactor = (MapleReactor)obj;
                this.broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor r : toSpawn) {
            this.removeMapObject(r);
            if (r.isCustom()) continue;
            this.respawnReactor(r);
        }
    }

    public void resetReactors() {
        this.setReactorState((byte)0);
    }

    public void setReactorState() {
        this.setReactorState((byte)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReactorState(byte state) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor)obj).forceHitReactor(state);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReactorState(String name, byte state) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                if (!Objects.equals(((MapleReactor)obj).getName(), name)) continue;
                ((MapleReactor)obj).forceHitReactor(state);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReactorDelay(int state) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor)obj).setDelay(state);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public void shuffleReactors() {
        this.shuffleReactors(0, 9999999);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shuffleReactors(int first, int last) {
        MapleReactor mr;
        ArrayList<Point> points = new ArrayList<Point>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                mr = (MapleReactor)obj;
                if (mr.getReactorId() < first || mr.getReactorId() > last) continue;
                points.add(mr.getPosition());
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        Collections.shuffle(points);
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                mr = (MapleReactor)obj;
                if (mr.getReactorId() < first || mr.getReactorId() > last) continue;
                mr.setPosition((Point)points.remove(points.size() - 1));
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public void updateMonsterController(MapleMonster monster) {
        this.mobControllerLock.lock();
        try {
            monster.updateController(this.getCharacters());
        }
        finally {
            this.mobControllerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
        this.mapobjectlocks.get((Object)type).readLock().lock();
        try {
            MapleMapObject mapleMapObject = this.mapobjects.get((Object)type).get(oid);
            return mapleMapObject;
        }
        finally {
            this.mapobjectlocks.get((Object)type).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsNPC(int npcid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mapleMapObject : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                MapleNPC n = (MapleNPC)mapleMapObject;
                if (n.getId() != npcid) continue;
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleNPC getNPCById(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mapleMapObject : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                MapleNPC n = (MapleNPC)mapleMapObject;
                if (n.getId() != id) continue;
                MapleNPC mapleNPC = n;
                return mapleNPC;
            }
            return null;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    @Deprecated
    public MapleMonster getMonsterById(int id) {
        return this.getMobObjectByID(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleMonster getMobObjectByID(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            MapleMonster ret = null;
            for (MapleMapObject mapleMapObject : this.mapobjects.get((Object)MapleMapObjectType.MONSTER).values()) {
                MapleMonster n = (MapleMonster)mapleMapObject;
                if (n.getId() != id) continue;
                ret = n;
                break;
            }
            return ret;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int countMonsterById(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            int ret = 0;
            for (MapleMapObject mapleMapObject : this.mapobjects.get((Object)MapleMapObjectType.MONSTER).values()) {
                MapleMonster n = (MapleMonster)mapleMapObject;
                if (n.getId() != id) continue;
                ++ret;
            }
            int n = ret;
            return n;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleReactor getReactorById(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            MapleReactor ret = null;
            for (MapleMapObject mapleMapObject : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor n = (MapleReactor)mapleMapObject;
                if (n.getReactorId() != id) continue;
                ret = n;
                break;
            }
            return ret;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public MapleMonster getMonsterByOid(int oid) {
        return this.getMobObject(oid);
    }

    public MapleSummon getSummonByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.SUMMON);
        if (mmo == null) {
            return null;
        }
        return (MapleSummon)mmo;
    }

    public MapleNPC getNPCByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (MapleNPC)mmo;
    }

    public MapleReactor getReactorByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (MapleReactor)mmo;
    }

    public MonsterFamiliar getFamiliarByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.FAMILIAR);
        if (mmo == null) {
            return null;
        }
        return (MonsterFamiliar)mmo;
    }

    public MapleAffectedArea getAffectedAreaByChr(int id, int sourceid) {
        for (MapleAffectedArea mist : this.getAllAffectedAreasThreadsafe()) {
            if (mist.getOwnerId() != id || mist.getEffect().getSourceId() != sourceid) continue;
            return mist;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleAffectedArea> getAffectedAreaObject(int ownerID, int skillID) {
        ArrayList<MapleAffectedArea> ret = new ArrayList<MapleAffectedArea>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.AFFECTED_AREA).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.AFFECTED_AREA).values()) {
                MapleAffectedArea area = (MapleAffectedArea)mmo;
                if (area.getOwnerId() != ownerID || area.getSourceSkill().getId() != skillID) continue;
                ret.add(area);
            }
            ArrayList<MapleAffectedArea> arrayList = ret;
            return arrayList;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.AFFECTED_AREA).readLock().unlock();
        }
    }

    public void spawnMapObject(int chrID, MapleMapObject obj, byte[] packet) {
        this.addMapObject(obj);
        this.objectMove(chrID, obj, packet);
    }

    public void disappearMapObject(MapleMapObject obj) {
        this.removeMapObject(obj);
        this.removeRangedMapObject(obj);
    }

    public boolean removeAffectedArea(int id, int sourceid) {
        boolean succ = false;
        for (MapleAffectedArea area : this.getAffectedAreaObject(id, sourceid)) {
            area.cancel();
            this.disappearMapObject(area);
            succ = true;
        }
        return succ;
    }

    public void removeAllAffectedAreaByChr(int id) {
        for (MapleAffectedArea mist : this.getAllAffectedAreasThreadsafe()) {
            if (mist.getOwnerId() != id) continue;
            mist.cancel();
            this.disappearMapObject(mist);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void objectMove(int chrID, MapleMapObject obj, byte[] packet) {
        this.objectMoveLock.lock();
        try {
            for (MapleMapObjectType type : MapleMapObjectType.values()) {
                this.mapobjectlocks.get((Object)type).readLock().lock();
                try {
                    for (MapleMapObject mmo : this.mapobjects.get((Object)type).values()) {
                        if (mmo == null) continue;
                        boolean b = this.getId() / 1000 == 921174 || obj.getPosition().distance(mmo.getPosition()) < (double)mmo.getRange();
                        try {
                            MapleCharacter chr;
                            switch (obj.getType()) {
                                case PLAYER: {
                                    chr = (MapleCharacter)obj;
                                    if (mmo.getDwOwnerID() > 0 && mmo.getDwOwnerID() != chr.getId()) break;
                                    if (b) {
                                        if (chr.isMapObjectVisible(mmo)) break;
                                        chr.addVisibleMapObject(mmo);
                                        break;
                                    }
                                    if (!chr.isMapObjectVisible(mmo)) break;
                                    chr.removeVisibleMapObject(mmo);
                                    break;
                                }
                                case MONSTER: {
                                    MapleAffectedArea area;
                                    MapleMonster monster = (MapleMonster)obj;
                                    if (mmo.getType() == MapleMapObjectType.AFFECTED_AREA && !(area = (MapleAffectedArea)mmo).isMobMist() && monster.getEffectHolder(area.getSkillID()) != null && !area.getBounds().contains(monster.getPosition())) {
                                        monster.removeEffect(area.getOwnerId(), area.getSkillID());
                                    }
                                    if (mmo.getType() != MapleMapObjectType.PLAYER || monster.getZoneDataType() <= 0) break;
                                    monster.checkMobZone((MapleCharacter)mmo);
                                    break;
                                }
                            }
                            if (Objects.requireNonNull(mmo.getType()) != MapleMapObjectType.PLAYER) continue;
                            chr = (MapleCharacter)mmo;
                            if (obj.getDwOwnerID() > 0 && obj.getDwOwnerID() != chr.getId()) continue;
                            if (b) {
                                if (!chr.isMapObjectVisible(obj)) {
                                    chr.addVisibleMapObject(obj);
                                }
                                if (packet == null || chr.getId() == chrID) continue;
                                chr.getClient().announce(packet);
                                continue;
                            }
                            if (!chr.isMapObjectVisible(obj)) continue;
                            chr.removeVisibleMapObject(obj);
                        }
                        catch (Exception ex) {
                            log.error("Object Move Error:" + String.valueOf(mmo) + " Map: " + String.valueOf(this) + "!", ex);
                        }
                    }
                }
                finally {
                    this.mapobjectlocks.get((Object)type).readLock().unlock();
                }
            }
        }
        finally {
            this.objectMoveLock.unlock();
        }
    }

    public MapleAffectedArea getAffectedAreaByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.AFFECTED_AREA);
        if (mmo == null) {
            return null;
        }
        return (MapleAffectedArea)mmo;
    }

    private class AreaRunnable implements Runnable {
        private final MapleAffectedArea area;
        private int numTimes = 0;

        public AreaRunnable(MapleAffectedArea area) {
            this.area = area;
        }

        @Override
        public void run() {
            numTimes++;
            handleAffectedArea(area, numTimes);
        }
    }

    public ScheduledFuture affectedAreaScheduled(MapleAffectedArea area) {
        ScheduledFuture<?> schedule = null;
        if (area.isNeedHandle()) {
            schedule = Timer.MapTimer.getInstance().register((Runnable)new AreaRunnable( area), 1000L, 100L);
        }
        return schedule;
    }

    public void createAffectedArea(MapleAffectedArea area) {
        this.addMapObject(area);
        this.objectMove(-1, area, null);
        area.setPoisonSchedule(this.affectedAreaScheduled(area));
        area.setSchedule(Timer.MapTimer.getInstance().schedule(() -> {
            Skill skill;
            this.disappearMapObject(area);
            area.cancel();
            if (area.getSkillID() == 400051025 && area.getEffect() != null && (skill = SkillFactory.getSkill(400051026)) != null) {
                skill.getEffect(area.getEffect().getLevel()).applyAffectedArea(area.getOwner(), area.getPosition());
            }
        }, area.getDuration()));
    }

    public MapleNodes getNodez() {
        return this.nodes;
    }

    public void setNodez(MapleNodes nodes) {
        this.nodes = nodes;
    }

    public List<MapleFlyingSword> getAllFlyingSwordsThreadsafe() {
        ArrayList<MapleFlyingSword> ret = new ArrayList<MapleFlyingSword>();
        for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.SWORD).values()) {
            ret.add((MapleFlyingSword)mmo);
        }
        return ret;
    }

    public MapleCharacter getCharacter(int cid) {
        MapleCharacter ret = null;
        for (MapleCharacter chr : this.characters) {
            if (chr.getId() != cid) continue;
            return chr;
        }
        return ret;
    }

    public void spawnMist(final MapleMist mist, boolean fake) {
        spawnAndAddRangedMapObject((MapleMapObject) mapobjects, new DelayedPacketCreation() {
            public void sendPackets(MapleClient c) {
                mist.sendSpawnData(c);
            }
        });
        if (mist.getStartTime() == 0L) {
            mist.setStartTime(System.currentTimeMillis());
        }
    }


    public MapleFlyingSword getFlyingSword(int objid) {
        for (MapleFlyingSword mfs : this.getAllFlyingSwordsThreadsafe()) {
            if (mfs.getObjectId() != objid) continue;
            return mfs;
        }
        return null;
    }

    public void spawnspider(final spider web) {
        spawnAndAddRangedMapObject(web, new DelayedPacketCreation() {
            public void sendPackets(MapleClient c) {
                web.sendSpawnData(c);
            }
        });
    }

    public void handleAffectedArea(MapleAffectedArea area, int numTimes) {
        for (MapleCharacter chr : this.getCharacters()) {
            area.handleEffect(chr, numTimes);
        }
        if (area.getSkillID() == 2221055) {
            return;
        }
        area.handleMonsterEffect(this, numTimes);
    }

    public void removeSummon(int oid) {
        MapleSummon summon = (MapleSummon)this.getMapObject(oid, MapleMapObjectType.SUMMON);
        this.removeMapObject(summon);
        this.broadcastMessage(SummonPacket.removeSummon(summon, false));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleReactor getReactorByName(String name) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getName().equalsIgnoreCase(name)) {
                    return mr;
                }
            }
            return null;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }



    public void spawnNpc(int id, Point pos) {
        MapleNPC npc = MapleLifeFactory.getNPC(id, this.mapid);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setCurrentFh(this.getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        this.addMapObject(npc);
        this.broadcastMessage(NPCPacket.spawnNPC(npc));
    }

    public void spawnNpcForPlayer(MapleClient c, int id, Point pos) {
        MapleNPC npc = MapleLifeFactory.getNPC(id, this.mapid);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setOwnerid(c.getPlayer().getId());
        npc.setCurrentFh(this.getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        this.addMapObject(npc);
        c.announce(NPCPacket.spawnNPC(npc));
    }

    public void removeNpc(int npcid) {
        this.removeNpc(npcid, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeNpc(int npcid, int ownerid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).writeLock().lock();
        try {
            Iterator<MapleMapObject> itr = this.mapobjects.get((Object)MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC)itr.next();
                if (!npc.isCustom() || npcid != -1 && npc.getId() != npcid || npc.getId() != 0 && npc.getOwnerid() != ownerid) continue;
                if (!npc.isHidden() && !this.isNpcHide(npc.getId())) {
                    this.broadcastMessage(NPCPacket.removeNPCController(npc.getObjectId(), false));
                }
                this.broadcastMessage(NPCPacket.removeNPC(npc.getObjectId()));
                itr.remove();
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideNpc(int npcid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mapleMapObject : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                MapleNPC npc = (MapleNPC)mapleMapObject;
                if (npcid != -1 && npc.getId() != npcid) continue;
                this.broadcastMessage(NPCPacket.removeNPCController(npc.getObjectId(), false));
                this.broadcastMessage(NPCPacket.removeNPC(npc.getObjectId()));
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideNpc(MapleClient c, int npcid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mapleMapObject : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                MapleNPC npc = (MapleNPC)mapleMapObject;
                if (npcid != -1 && npc.getId() != npcid) continue;
                c.announce(NPCPacket.removeNPCController(npc.getObjectId(), false));
                c.announce(NPCPacket.removeNPC(npc.getObjectId()));
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public void spawnReactorOnGroundBelow(MapleReactor mob, Point pos) {
        mob.setPosition(pos);
        mob.setCustom(true);
        this.spawnReactor(mob);
    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos, int spawnType) {
        mob.setPosition(this.calcPointBelow(new Point(pos.x, pos.y - 1)));
        this.spawnMonster(mob, spawnType);
    }

    public void spawnMonster(MapleMonster monster, Point point, int n) {
        monster.setPosition(this.calcPointBelow(new Point(point.x, point.y - 1)));
        this.spawnMonster(monster, n);
    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        mob.setPosition(this.calcPointBelow(new Point(pos.x, pos.y - 1)));
        this.spawnMonster(mob, -2);
    }

    public void spawnZakum(int x, int y, long maxhp) {
        int[] zakpart;
        Point pos = new Point(-51, 85);
        MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        mainb.getStats().setChange(true);
        mainb.setForcedMobStat(120);
        mainb.changeHP(maxhp);
        this.spawnFakeMonster(mainb);
        for (int i : zakpart = new int[]{8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010}) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setForcedMobStat(120);
            part.getStats().setChange(true);
            part.setPosition(spos);
            this.spawnMonster(part, -2);
        }
    }

    public void spawnChaosZakum(int x, int y, long maxhp) {
        int[] zakpart;
        Point pos = new Point(-51, 85);
        MapleMonster mainb = MapleLifeFactory.getMonster(8800100);
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        mainb.getStats().setChange(true);
        mainb.setForcedMobStat(160);
        if (maxhp > 0L) {
            mainb.changeHP(maxhp);
        }
        this.spawnFakeMonster(mainb);
        for (int i : zakpart = new int[]{8800103, 8800104, 8800105, 8800106, 8800107, 8800108, 8800109, 8800110}) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            this.spawnMonster(part, -2);
        }
    }

    public final void spawnSimpleZakum(int x, int y, long maxhp) {
        Point point = new Point(-10, -215);
        MapleMonster mainb = MapleLifeFactory.getMonster(8800020);
        Point calcPointBelow = this.calcPointBelow(new Point(point.x, point.y - 1));
        mainb.setPosition(calcPointBelow);
        mainb.setFake(true);
        if (maxhp > 0L) {
            mainb.setForcedMobStat(55);
            mainb.getStats().setChange(true);
            mainb.changeHP(maxhp);
        }
        this.spawnFakeMonster(mainb);
        int[] zakpart = new int[]{8800023, 8800024, 8800025, 8800026, 8800027, 8800028, 8800029, 8800030};
        int length = zakpart.length;
        for (int i = 0; i < length; ++i) {
            MapleMonster part = MapleLifeFactory.getMonster(zakpart[i]);
            part.setPosition(calcPointBelow);
            this.spawnMonster(part, -2);
        }
    }

    public void spawnPinkZakum(int x, int y) {
        int[] zakpart;
        Point pos = new Point(x, y);
        MapleMonster mainb = MapleLifeFactory.getMonster(9400900);
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        this.spawnFakeMonster(mainb);
        for (int i : zakpart = new int[]{9400903, 9400904, 9400905, 9400906, 9400907, 9400908, 9400909, 9400910}) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            this.spawnMonster(part, -2);
        }
    }

    public void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mob.setPosition(spos);
        this.spawnFakeMonster(mob);
    }

    private void checkRemoveAfter(MapleMonster monster) {
        int ra = monster.getStats().getRemoveAfter();
        if (ra > 0 && monster.getLinkCID() <= 0) {
            monster.registerKill((long)ra * 1000L);
        }
    }

    public void spawnRevives(MapleMonster monster, int oid) {
        if (monster.getStats().isMobile()) {
            MapleFoothold d = this.getFootholds().findBelow(monster.getPosition());
            monster.setCurrentFh(d != null ? d.getId() : 0);
        }
        monster.setMap(this);
        monster.setLinkOid(oid);
        monster.setAppearType(monster.getStats().getSummonType() <= 1 ? (short)-3 : (short)monster.getStats().getSummonType());
        this.spawnAndAddRangedMapObject(monster);
        this.objectMove(-1, monster, null);
        monster.setNewSpawn(false);
        this.checkRemoveAfter(monster);
        this.updateMonsterController(monster);
    }

    public void spawnMonster(MapleMonster monster, int spawnType) {
        this.spawnMonster(monster, spawnType, false);
    }

    public void spawnMonster(MapleMonster monster, int spawnType, boolean newSpawn) {
        ChannelServer ch;
        if (monster.getMap() == null && monster.getStats().isPatrol()) {
            monster.setPatrolScopeX1(monster.getPosition().x);
            monster.setPatrolScopeX2(monster.getPosition().x + monster.getStats().getPatrolRange());
        }
        if (!monster.getStats().isFlyMobile() && monster.getStats().isMobile()) {
            MapleFoothold d = this.getFootholds().findBelow(monster.getPosition());
            monster.setCurrentFh(d != null ? d.getId() : 0);
        }
        if ((ch = ChannelServer.getInstance(this.channel)).getChannelType() != ChannelServer.ChannelType.NORMAL) {
            ForcedMobStat changeStats = monster.getForcedMobStat();
            if (changeStats == null) {
                monster.setForcedMobStat(monster.getStats());
                changeStats = monster.getForcedMobStat();
            }
            int level = changeStats.getLevel();
            long hp = monster.getMobMaxHp();
            long exp = changeStats.getExp();
            int watk = changeStats.getWatk();
            int matk = changeStats.getMatk();
            int pdrate = changeStats.getPDRate();
            int mdrate = changeStats.getMDRate();
            int scale = monster.getScale();
            changeStats.setLevel(Math.min(level, ServerConfig.CHANNEL_PLAYER_MAXLEVEL));
            monster.changeHP(hp < 0L ? Long.MAX_VALUE : hp);
            changeStats.setExp(exp < 0L ? 0L : exp);
            changeStats.setWatk(watk < 0 ? Integer.MAX_VALUE : watk);
            changeStats.setMatk(matk < 0 ? Integer.MAX_VALUE : matk);
            changeStats.setPDRate(pdrate < 0 ? Integer.MAX_VALUE : pdrate);
            changeStats.setMDRate(mdrate < 0 ? Integer.MAX_VALUE : mdrate);
            monster.setScale(scale);
            monster.setForcedMobStat(changeStats);
        }
        monster.setMap(this);
        monster.setAppearType((short)(monster.getStats().getSummonType() <= 1 || monster.getStats().getSummonType() == 27 || newSpawn ? spawnType : (int)monster.getStats().getSummonType()));
        this.spawnAndAddRangedMapObject(monster);
        this.objectMove(-1, monster, null);
        monster.setNewSpawn(newSpawn);
        this.checkRemoveAfter(monster);
        this.updateMonsterController(monster);
    }

    public void spawnMonsterWithEffect(MapleMonster monster, int effect, Point pos) {
        if (!monster.getStats().isFlyMobile() && monster.getStats().isMobile()) {
            MapleFoothold d = this.getFootholds().findBelow(monster.getPosition());
            monster.setCurrentFh(d != null ? d.getId() : 0);
        } else {
            monster.setPosition(new Point(pos.x, pos.y - 1));
        }
        this.spawnMonster(monster, effect, true);
    }

    public void spawnFakeMonster(MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        this.spawnAndAddRangedMapObject(monster, c -> c.announce(MobPacket.spawnMonster(monster)));
        this.updateMonsterController(monster);
    }

    public void spawnReactor(MapleReactor reactor) {
        reactor.setMap(this);
        this.spawnAndAddRangedMapObject(reactor, c -> c.announce(MaplePacketCreator.spawnReactor(reactor)));
    }

    private void respawnReactor(MapleReactor reactor) {
        if (!this.isSecretMap() && reactor.getReactorId() >= 100000 && reactor.getReactorId() <= 200011) {
            int newRid = (reactor.getReactorId() < 200000 ? 100000 : 200000) + Randomizer.nextInt(11);
            int prop = reactor.getReactorId() % 100;
            if (Randomizer.nextInt(22) <= prop && newRid % 100 < 10) {
                ++newRid;
            }
            if (Randomizer.nextInt(110) <= prop && newRid % 100 < 11) {
                ++newRid;
            }
            ArrayList<Point> toSpawnPos = new ArrayList<Point>(this.spawnPoints);
            for (MapleMapObject mapleMapObject : this.getAllReactorsThreadsafe()) {
                MapleReactor reactor2l = (MapleReactor)mapleMapObject;
                if (toSpawnPos.isEmpty()) continue;
                toSpawnPos.remove(reactor2l.getPosition());
            }
            MapleReactor newReactor = new MapleReactor(MapleReactorFactory.getReactor(newRid), newRid);
            newReactor.setPosition(toSpawnPos.isEmpty() ? reactor.getPosition() : (Point)toSpawnPos.get(Randomizer.nextInt(toSpawnPos.size())));
            newReactor.setDelay(newRid % 100 == 11 ? 60000 : 5000);
            this.spawnReactor(newReactor);
        } else {
            reactor.setState((byte)0);
            reactor.setAlive(true);
            this.spawnReactor(reactor);
        }
    }

    public void spawnRune(MapleRuneStone rune) {
        rune.setMap(this);
        rune.setSpawnTime(System.currentTimeMillis());
        this.spawnAndAddRangedMapObject(rune, c -> {
            c.announce(MaplePacketCreator.spawnRuneStone(rune));
            c.sendEnableActions();
        });
    }

    public void respawnRune() {
        if (this.getFieldLevel() < 30 || this.getRunesSize() != 0 || this.isTown() || this.isBossMap() || this.eliteCount < ServerConfig.ELITE_COUNT / 2) {
            return;
        }
        if (this.getCharactersSize() > 0) {
            if (System.currentTimeMillis() - this.spawnRuneTime > 600000L) {
                ArrayList<Point> spawnPos = new ArrayList<Point>(this.spawnPoints);
                MapleRuneStone runeStone = new MapleRuneStone(Randomizer.nextInt(10));
                for (MapleReactor y2 : this.getAllReactorsThreadsafe()) {
                    if (spawnPos.isEmpty() || !spawnPos.contains(y2.getPosition())) continue;
                    spawnPos.remove(y2.getPosition());
                }
                if (!spawnPos.isEmpty()) {
                    this.setRuneTime();
                    runeStone.setPosition((Point)spawnPos.get(Randomizer.nextInt(spawnPos.size())));
                    this.spawnRune(runeStone);
                }
            }
        } else {
            this.setRuneTime();
        }
    }

    public void spawnSkillPet(MapleSkillPet skillpet) {
        this.spawnAndAddRangedMapObject(skillpet, c -> skillpet.sendSpawnData(c));
    }

    public void setRuneTime() {
        this.spawnRuneTime = System.currentTimeMillis();
    }

    public void removeRune(MapleRuneStone rune, MapleCharacter chr, boolean noText) {
        MapleRuneStone curseRune = this.getCurseRune();
        this.removeMapObject(rune);
        chr.send(MaplePacketCreator.removeRuneStone(chr.getId(), rune.getCurseRate(), chr.getLevel() < this.getFieldLevel() - 20, noText));
        this.broadcastMessage(chr, MaplePacketCreator.removeRuneStone(chr.getId(), 0, false, true), false);
        this.broadcastRuneCurseMessage(MaplePacketCreator.RemoveRuneCurseMsg("已解除菁英 Boss的詛咒！！"));
    }

    public MapleRuneStone getCurseRune() {
        MapleRuneStone theRune = null;
        for (MapleRuneStone rune : this.getAllRune()) {
            if (theRune != null && rune.getCurseStage() <= theRune.getCurseStage()) continue;
            theRune = rune;
        }
        return theRune;
    }

    public int getRuneCurseRate() {
        MapleRuneStone theRune = this.getCurseRune();
        return theRune == null ? 0 : theRune.getCurseRate();
    }

    public void showRuneCurseStage() {
        this.showRuneCurseStage(null);
    }

    public void showRuneCurseStage(MapleClient c) {
        MapleRuneStone theRune = this.getCurseRune();
        if (theRune != null && theRune.getCurseStage() > 0) {
            String curseMsg = "需要解放輪來解開精英Boss的詛咒！！\\n詛咒 " + theRune.getCurseStage() + "階段 : 套用獲得經驗值、道具掉落率 " + theRune.getCurseRate() + "%減少效果中";
            if (c != null) {
                c.announce(MaplePacketCreator.sendRuneCurseMsg(curseMsg));
            } else {
                this.broadcastRuneCurseMessage(MaplePacketCreator.sendRuneCurseMsg(curseMsg));
            }
        }
    }

    public boolean isSecretMap() {
        switch (this.mapid) {
            case 910001001: 
            case 910001002: 
            case 910001003: 
            case 910001004: 
            case 910001005: 
            case 910001006: 
            case 910001007: 
            case 910001008: 
            case 910001009: 
            case 910001010: {
                return true;
            }
        }
        return false;
    }

    public void spawnTownPortal(TownPortal door) {
        this.spawnAndAddRangedMapObject(door, c -> {
            door.sendSpawnData(c);
            c.sendEnableActions();
        });
        door.setState(1);
    }

    public boolean spawnRandomPortal(int cid, MapleRandomPortal.Type portalType) {
        if (this.getFieldLevel() <= 0 || this.isTown() || this.isBossMap() || this.eliteCount < ServerConfig.ELITE_COUNT / 2) {
            return false;
        }
        ArrayList<Point> spawnPos = new ArrayList<Point>(this.spawnPoints);
        Iterator itr = spawnPos.iterator();
        while (itr.hasNext()) {
            Point position = (Point)itr.next();
            for (MapleMapObject obj : this.getAllRandomPortalThreadsafe()) {
                MapleRandomPortal portalObj = (MapleRandomPortal)obj;
                if (cid != portalObj.getOwerid() || !obj.getPosition().equals(position)) continue;
                itr.remove();
            }
        }
        if (spawnPos.size() > 0) {
            Collections.shuffle(spawnPos);
            MapleRandomPortal portal = new MapleRandomPortal(portalType, this.mapid, cid, 60000, (Point)spawnPos.get(0));
            this.spawnAndAddRangedMapObject(portal, c -> {
                portal.sendSpawnData(c);
                c.announce(MaplePacketCreator.getFieldVoice("Field.img/StarPlanet/cashTry"));
                c.sendEnableActions();
            });
            return true;
        }
        return false;
    }

    public void spawnMechDoor(MechDoor door) {
        this.spawnAndAddRangedMapObject(door, c -> {
            c.announce(MaplePacketCreator.spawnMechDoor(door, true));
            c.sendEnableActions();
        });
    }

    public void spawnSummon(MapleSummon summon) {
        summon.setMap(this);
        this.spawnAndAddRangedMapObject(summon, c -> {
            if (c.getPlayer() == null) {
                return;
            }
            if (!summon.isChangedMap() || summon.getOwnerId() == c.getPlayer().getId()) {
                summon.sendSpawnData(c);
            }
        });
    }

    public void spawnFamiliar(MonsterFamiliar familiar) {
        this.spawnAndAddRangedMapObject(familiar, c -> {
            if (familiar != null && c.getPlayer() != null) {
                familiar.sendSpawnData(c);
            }
        });
    }

    public void spawnExtractor(MapleExtractor ex) {
        this.spawnAndAddRangedMapObject(ex, ex::sendSpawnData);
    }

    public void spawnLove(MapleLove love) {
        this.spawnAndAddRangedMapObject(love, love::sendSpawnData);
        Timer.MapTimer tMan = Timer.MapTimer.getInstance();
        tMan.schedule(() -> {
            this.broadcastMessage(MaplePacketCreator.removeLove(love.getObjectId(), love.getItemId()));
            this.removeMapObject(love);
        }, 3600000L);
    }

    public void spawnAffectedArea(MapleAffectedArea mist, int duration, boolean fake) {
        this.spawnAndAddRangedMapObject(mist, mist::sendSpawnData);
        mist.setCancelTask(duration);
    }

    public void disappearingItemDrop(MapleMapObject dropper, MapleCharacter owner, Item item, Point pos) {
        Point droppos = this.calcDropPos(pos, pos);
        MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte)1, false);
        drop.setEnterType((byte)3);
        this.broadcastMessage(InventoryPacket.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)3), drop.getPosition());
    }

    public void spawnMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        Point droppos = this.calcDropPos(position, position);
        MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        this.spawnAndAddRangedMapObject(mdrop, c -> c.announce(InventoryPacket.dropItemFromMapObject(mdrop, dropper.getPosition(), droppos, (byte)1)));
        if (!this.everlast) {
            mdrop.registerExpire(120000L);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000L);
            }
        }
    }

    public void spawnMesoDropEx(int meso, Point dropfrom, Point dropto, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        Point droppos = this.calcDropPos(dropto, dropto);
        droppos.x = Randomizer.nextBoolean() ? (droppos.x -= Randomizer.rand(0, 20)) : (droppos.x += Randomizer.rand(0, 20));
        MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        this.spawnAndAddRangedMapObject(mdrop, c -> c.announce(InventoryPacket.dropItemFromMapObject(mdrop, dropfrom, droppos, (byte)1)));
        mdrop.registerExpire(120000L);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
        }
    }

    public void spawnMobMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype, int delay) {
        this.spawnMobMesoDrop(meso, position, dropper, owner, playerDrop, droptype, delay, 0);
    }

    public void spawnMobMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype, int delay, int skillID) {
        int mesoRate;
        ChannelServer ch = ChannelServer.getInstance(this.channel);
        if (ch != null && ch.getChannelType() != ChannelServer.ChannelType.NORMAL && (meso = (int)((double)meso * ((double)(mesoRate = 100) / 100.0))) < 0) {
            meso = Integer.MAX_VALUE;
        }
        MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);
        mdrop.setSkill(skillID);
        mdrop.setDelay(delay);
        this.spawnAndAddRangedMapObject(mdrop, c -> c.announce(InventoryPacket.dropItemFromMapObject(mdrop, dropper.getPosition(), position, (byte)1)));
        mdrop.registerExpire(120000L);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
        }
    }

    public void spawnMobPointDrop(int toCharge, int point, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype, int delay) {
        int itemId;
        if (!ServerConfig.mobPointNeedPickup) {
            this.pickupPoint(toCharge, point, owner);
            return;
        }
        switch (toCharge) {
            case 1: {
                itemId = 2435892;
                break;
            }
            case 2: {
                itemId = 2432107;
                break;
            }
            case 3: {
                itemId = 2431872;
                break;
            }
            default: {
                return;
            }
        }
        MapleMapItem mdrop = new MapleMapItem(toCharge, new Item(itemId,  (short)0, (short)point), position, dropper, owner, droptype, playerDrop);
        mdrop.setDelay(delay);
        this.spawnAndAddRangedMapObject(mdrop, c -> c.announce(InventoryPacket.dropItemFromMapObject(mdrop, dropper.getPosition(), position, (byte)1)));
        mdrop.registerExpire(120000L);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
        }
    }

    public void pickupPoint(int toCharge, int point, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        if (toCharge == 3) {
            chr.modifyMileage(point, 2, true, true, "從地圖[" + String.valueOf(this) + "]掉落");
        } else {
            chr.modifyCSPoints(toCharge, point, true);
        }
        Party party = chr.getParty();
        if (party != null) {
            MapleCharacter pchr = null;
            ArrayList<MapleCharacter> mpApplicable = new ArrayList<MapleCharacter>();
            for (PartyMember partychar : party.getMembers()) {
                pchr = this.getCharacterById(partychar.getCharID());
                if (pchr == null || !pchr.isAlive() || !pchr.getCheatTracker().isAttacking() || Math.abs(this.getFieldLevel() - partychar.getLevel()) > 20 && Math.abs(chr.getLevel() - partychar.getLevel()) > 5) continue;
                mpApplicable.add(pchr);
            }
            if (mpApplicable.size() > 1) {
                for (MapleCharacter achr : mpApplicable) {
                    if (toCharge == 3) {
                        achr.modifyMileage(ServerConfig.ptyPointModifier, 2, false, true, "隊伍從地圖[" + String.valueOf(this) + "]掉落");
                    } else {
                        achr.modifyCSPoints(toCharge, ServerConfig.ptyPointModifier, false);
                    }
                    achr.dropMessage(6, "組隊獲得" + ServerConfig.ptyPointModifier + (toCharge == 1 ? "樂豆點" : (toCharge == 2 ? "楓點" : "里程")) + "。");
                }
            }
        }
    }

    public void spawnMobDrop(MapleMapItem mdrop, MapleMonster mob, MapleCharacter chr) {
        if (mdrop == null) {
            return;
        }
        this.spawnAndAddRangedMapObject(mdrop, c -> {
            if (c != null && c.getPlayer() != null && (mdrop.getQuest() <= 0 || c.getPlayer().getQuestStatus(mdrop.getQuest()) == 1 && c.getPlayer().needQuestItem(mdrop.getQuest(), mdrop.getItemId())) && mdrop.getItemId() / 10000 != 238) {
                if (mdrop.getOnlySelfID() >= 0 && c.getPlayer().getId() != mdrop.getOnlySelfID()) {
                    return;
                }
                c.announce(InventoryPacket.dropItemFromMapObject(mdrop, mob != null ? mob.getPosition() : (chr != null ? chr.getPosition() : new Point()), mdrop.getPosition(), (byte)1));
            }
        });
        if (mob != null && chr != null && mob.getStats().getWeaponPoint() > 0 && JobConstants.is神之子(chr.getJob())) {
            chr.gainWeaponPoint(mob.getStats().getWeaponPoint());
        }
        mdrop.registerExpire(120000L);
        if (mdrop.getOwnType() == 0 || mdrop.getOwnType() == 1) {
            mdrop.registerFFA(30000L);
        }
        if (chr != null) {
            this.activateItemReactors(mdrop, chr.getClient());
        }
    }

    public void spawnRandDrop() {
        if (this.mapid != 910000000 || this.channel != 1) {
            return;
        }
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject o : this.mapobjects.get((Object)MapleMapObjectType.ITEM).values()) {
                if (((MapleMapItem)o).getRand() <= 0) continue;
                return;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
        Timer.MapTimer.getInstance().schedule(() -> {
            Point pos = new Point(Randomizer.nextInt(800) + 531, -806);
            int theItem = Randomizer.nextInt(1000);
            int itemid = theItem < 950 ? GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)] : (theItem < 990 ? GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)] : GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)]);
            this.spawnAutoDrop(itemid, pos, 0, 0);
        }, 20000L);
    }

    public void spawnAutoDrop(int itemid, Point pos, int dropSpeed, int sourceOId) {
        Item idrop;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ItemConstants.getInventoryType(itemid, false) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats(ii.getEquipById(itemid));
            if (Randomizer.isSuccess(30) && !ii.isCash(itemid)) {
                NirvanaFlame.randomState((Equip)idrop, 0);
            }
        } else {
            idrop = new Item(itemid, (short) 0, (short)1, 0);
        }
        idrop.setGMLog("自動掉落 " + itemid + " 地圖 " + this.mapid);
        MapleMapItem mdrop = new MapleMapItem(idrop, pos);
        mdrop.setDropSpeed(dropSpeed);
        if (dropSpeed > 0) {
            mdrop.setDropMotionType(1);
        }
        mdrop.setSourceOID(sourceOId);
        mdrop.setCollisionPickUp(ii.isRunOnPickup(itemid) || ii.isConsumeOnPickup(itemid));
        if (mdrop.isCollisionPickUp() && mdrop.getDropMotionType() != 0) {
            mdrop.registerExpire(3000L);
        } else if (itemid / 10000 != 291) {
            mdrop.registerExpire(120000L);
        }
        this.spawnAndAddRangedMapObject(mdrop, c -> c.announce(InventoryPacket.dropItemFromMapObject(mdrop, pos, pos, (byte)1)));
        this.broadcastMessage(InventoryPacket.dropItemFromMapObject(mdrop, pos, pos, (byte)0));
    }

    public void spawnItemDrop(MapleMapObject dropper, MapleCharacter owner, Item item, Point pos, boolean ffaDrop, boolean playerDrop) {
        Point droppos = this.calcDropPos(pos, pos);
        MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte)2, playerDrop);
        this.spawnAndAddRangedMapObject(drop, c -> c.announce(InventoryPacket.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)1)));
        this.broadcastMessage(InventoryPacket.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)0));
        if (!this.everlast) {
            drop.registerExpire(120000L);
            this.activateItemReactors(drop, owner.getClient());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void activateItemReactors(MapleMapItem drop, MapleClient c) {
        Item item = drop.getItem();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject o : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor react = (MapleReactor)o;
                if (react.getReactorType() != 100 || item.getItemId() != GameConstants.getCustomReactItem(react.getReactorId(), react.getReactItem().getLeft()) || react.getReactItem().getRight().intValue() != item.getQuantity() || !react.getArea().contains(drop.getPosition()) || react.isTimerActive()) continue;
                Timer.MapTimer.getInstance().schedule((Runnable)new ActivateItemReactor(this, drop, react, c), 5000L);
                react.setTimerActive(true);
                break;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    private class ActivateItemReactor implements Runnable {
        MapleMap mapleMap;
        private final MapleMapItem mapitem;
        private final MapleReactor reactor;
        private final MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        public ActivateItemReactor(MapleMap mapleMap, MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
            this.mapleMap = mapleMap;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId(), mapitem.getType()) && !mapitem.isPickedUp()) {
                mapitem.expire(MapleMap.this);
                reactor.hitReactor(c);
                reactor.setTimerActive(false);

                if (reactor.getDelay() > 0) {
                    Timer.MapTimer.getInstance().schedule(() -> reactor.forceHitReactor((byte) 0), reactor.getDelay());
                }
            } else {
                reactor.setTimerActive(false);
            }
        }
    }


    public int getItemsSize() {
        return !this.mapobjects.containsKey((Object)MapleMapObjectType.ITEM) ? 0 : this.mapobjects.get((Object)MapleMapObjectType.ITEM).size();
    }

    public int getExtractorSize() {
        return !this.mapobjects.containsKey((Object)MapleMapObjectType.EXTRACTOR) ? 0 : this.mapobjects.get((Object)MapleMapObjectType.EXTRACTOR).size();
    }

    public int getMobsSize() {
        return !this.mapobjects.containsKey((Object)MapleMapObjectType.MONSTER) ? 0 : this.mapobjects.get((Object)MapleMapObjectType.MONSTER).size();
    }

    public int getRunesSize() {
        return !this.mapobjects.containsKey((Object)MapleMapObjectType.RUNE) ? 0 : this.mapobjects.get((Object)MapleMapObjectType.RUNE).size();
    }

    public List<MapleMapItem> getAllItems() {
        return this.getAllItemsThreadsafe();
    }

    public boolean isBlackMage3thSkill() {
        return this.isBlackMage3thSkilled;
    }

    public void setBlackMage3thSkill(boolean f) {
        this.isBlackMage3thSkilled = f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapItem> getAllItemsThreadsafe() {
        ArrayList<MapleMapItem> ret = new ArrayList<MapleMapItem>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.ITEM).values()) {
                ret.add((MapleMapItem)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Point getPointOfItem(int itemid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.ITEM).values()) {
                MapleMapItem mm = (MapleMapItem)mmo;
                if (mm.getItem() == null || mm.getItem().getItemId() != itemid) continue;
                Point point = mm.getPosition();
                return point;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleAffectedArea> getAllAffectedAreasThreadsafe() {
        ArrayList<MapleAffectedArea> ret = new ArrayList<MapleAffectedArea>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.AFFECTED_AREA).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.AFFECTED_AREA).values()) {
                ret.add((MapleAffectedArea)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.AFFECTED_AREA).readLock().unlock();
        }
        return ret;
    }

    public void returnEverLastItem(MapleCharacter chr) {
        for (MapleMapObject mapleMapObject : this.getAllItemsThreadsafe()) {
            MapleMapItem item = (MapleMapItem)mapleMapObject;
            if (item.getOwnerID() != chr.getId()) continue;
            item.setPickedUp(true);
            this.broadcastMessage(InventoryPacket.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getPosition());
            if (item.getMeso() > 0) {
                chr.gainMeso(item.getMeso(), false);
            } else {
                MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
            }
            this.removeMapObject(item);
        }
    }

    public void nextNodeAction(int mobid, int time) {
        Timer.MapTimer.getInstance().schedule(() -> {
            if (this.getMobObjectByID(mobid) != null) {
                this.broadcastMessage(MobPacket.mobEscortStopEndPermission(this.getMobObjectByID(mobid).getObjectId()));
            }
        }, time);
    }

    public MapleMapEffect getMapEffect() {
        return this.mapEffect;
    }

    public void startMapEffect(String msg, int itemId) {
        this.startMapEffect(msg, itemId, false);
    }

    public void startMapEffect(String msg, int itemId, boolean jukebox) {
        if (this.mapEffect != null) {
            if (this.mapEffect.getScheduledFuture() != null) {
                this.mapEffect.getScheduledFuture().cancel(false);
            }
            this.broadcastMessage(this.mapEffect.makeDestroyData());
            this.mapEffect = null;
        }
        this.mapEffect = new MapleMapEffect(msg, itemId);
        this.mapEffect.setJukebox(jukebox);
        this.broadcastMessage(this.mapEffect.makeStartData());
        this.mapEffect.setScheduledFuture(Timer.MapTimer.getInstance().schedule(() -> {
            if (this.mapEffect != null) {
                this.broadcastMessage(this.mapEffect.makeDestroyData());
                this.mapEffect = null;
                this.startMapEffect();
            }
        }, jukebox ? 300000L : 15000L));
    }

    public void startPredictCardMapEffect(String msg, int itemId, int effectType) {
        this.startMapEffect(msg, itemId, 30, effectType);
    }

    public void startMapEffect(String msg, int itemId, int time) {
        this.startMapEffect(msg, itemId, time, -1);
    }

    public void startMapEffect(String msg, int itemId, int time, int effectType) {
        if (this.mapEffect != null) {
            if (this.mapEffect.getScheduledFuture() != null) {
                this.mapEffect.getScheduledFuture().cancel(false);
            }
            this.broadcastMessage(this.mapEffect.makeDestroyData());
            this.mapEffect = null;
        }
        if (time <= 0) {
            time = 5;
        }
        this.mapEffect = new MapleMapEffect(msg, itemId, effectType);
        this.mapEffect.setJukebox(false);
        this.broadcastMessage(this.mapEffect.makeStartData());
        this.mapEffect.setScheduledFuture(Timer.MapTimer.getInstance().schedule(() -> {
            if (this.mapEffect != null) {
                this.broadcastMessage(this.mapEffect.makeDestroyData());
                this.mapEffect = null;
                this.startMapEffect();
            }
        }, (long)time * 1000L));
    }

    public void startMapEffect() {
        if (ServerConfig.MAP_EFFECT <= 0 || this.mapEffect != null) {
            return;
        }
        this.mapEffect = new MapleMapEffect("", ServerConfig.MAP_EFFECT);
        this.broadcastMessage(this.mapEffect.makeStartData());
    }

    public void startExtendedMapEffect(String msg, int itemId) {
        this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, true));
        Timer.MapTimer.getInstance().schedule(() -> {
            this.broadcastMessage(MaplePacketCreator.removeMapEffect());
            this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, false));
        }, 60000L);
    }

    public void startSimpleMapEffect(String msg, int itemId) {
        this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, true));
    }

    public void showScriptProgressMessage(String s) {
        this.broadcastMessage(UIPacket.getTopMsg(s));
    }

    public void showScriptProgressItemMessage(int n, String s) {
        this.broadcastMessage(UIPacket.ScriptProgressItemMessage(n, s));
    }

    public void setStaticScreenMessage(int n, String s, boolean b) {
        this.broadcastMessage(UIPacket.getMidMsg(n, s, !b));
    }

    public void offStaticScreenMessage() {
        this.broadcastMessage(UIPacket.offStaticScreenMessage());
    }

    public void showWeatherEffectNotice(String s, int n, int n2) {
        this.broadcastMessage(UIPacket.showWeatherEffectNotice(s, n, n2, false));
    }

    public void showWeatherEffectNoticeY(String s, int n, int n2, int n3) {
        this.broadcastMessage(UIPacket.WeatherEffectNoticeY(s, n, n2, n3));
    }

    public void showObjectEffect(String s) {
        this.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.getFieldEffectFromObject((String)s)));
    }

    public void showScreenEffect(String s) {
        this.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.getFieldEffectScreen((String)s)));
    }

    public void showScreenDelayedEffect(String s, int n) {
        this.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.getFieldBackgroundEffectFromWz((String)s, (int)n)));
    }

    public void showScreenAutoLetterBox(String s, int n) {
        this.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.getFieldEffectFromWz((String)s, (int)n)));
    }

    public void showScreenTopScreenDelayedEffect(String s, int n) {
        this.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.getOffFieldEffectFromWz((String)s, (int)n)));
    }

    public void playFieldSound(String s) {
        this.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.playSound((String)s, (int)100, (int)0, (int)0)));
    }

    public void showPortalEffect(String s, int n) {
        this.broadcastMessage(MaplePacketCreator.ShowPortal(s, n));
    }

    public void startJukebox(String msg, int itemId) {
        this.startMapEffect(msg, itemId, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    public void userEnterField(MapleCharacter chr) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).writeLock().lock();
        try {
            this.mapobjects.get((Object)MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr);
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).writeLock().unlock();
        }
        if (!chr.isAlive()) {
            chr.heal();
        }
        chr.getSpecialStat().resetFieldSkillCounters();
        chr.send(MaplePacketCreator.serverMessage(chr.getClient().getChannelServer().getServerMessage()));
        chr.setChangeTime();
        chr.setInGameCurNode(false);
        if (GameConstants.isTeamMap(this.mapid) && !chr.inPVP()) {
            chr.setTeam(this.getAndSwitchTeam() ? 0 : 1);
        }
        this.broadcastMessage(chr, MaplePacketCreator.spawnPlayerMapobject(chr), true);
        this.broadcastMessage(chr, EffectPacket.getEffectSwitch(chr.getId(), chr.getEffectSwitch()), true);
        if (chr.getGuild() != null && chr.getGuild().getImageLogo() != null && chr.getGuild().getImageLogo().length > 0) {
            this.broadcastMessage(chr, GuildPacket.loadGuildIcon((MapleCharacter)chr), false);
        }
        if (chr.getBuffedValue(SecondaryStat.LWCreation) != null && chr.getBuffedValue(SecondaryStat.LWSwordGauge) != null) {
            ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
            for (ForceAtomObject sword : chr.getForceAtomObjects().values()) {
                if (sword == null) continue;
                createList.add(sword);
            }
            this.broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
        }
        if (this.areaCtrls != null && !this.areaCtrls.isEmpty()) {
            chr.send(UIPacket.setAreaControl(null, null, this.areaCtrls));
        }
        if (this.mapid == 450005000) {
            MapleQuestStatus status = new MapleQuestStatus(MapleQuest.getInstance(34479), 1);
            MapleQuestStatus whileTreeStatue = chr.getQuest(MapleQuest.getInstance(34477));
            if (whileTreeStatue != null && whileTreeStatue.getStatus() == 2) {
                status.setStatus((byte)2);
                status.setCompletionTime(whileTreeStatue.getCompletionTime());
            }
            chr.send(MaplePacketCreator.updateQuest(status));
        }
        int breakTimeFieldStep = this.getBreakTimeFieldStep();
        this.updateBreakTimeField();
        if (breakTimeFieldStep == this.getBreakTimeFieldStep() && breakTimeFieldStep > 0) {
            chr.send(this.getBreakTimeFieldStepPacket());
        }
        if (this.isMiniMapOnOff()) {
            chr.send(UIPacket.showFreeMarketMiniMap(this.isMiniMapOnOff()));
        }
        if (this.dynamicObj != null) {
            chr.send(UIPacket.sendDynamicObj(false, this.dynamicObj));
        }
        chr.send(UIPacket.showFreeMarketMiniMap(true));
        this.sendObjectPlacement(chr);
        chr.getScriptManager().startOnFirstUserScript(this.onFirstUserEnter);
        if (!this.onUserEnter.isEmpty()) {
            chr.setDirection(-1);
            if (chr != null) {
                chr.getScriptManager().startOnUserScript(this.onUserEnter);
            }
        }
        GameConstants.achievementRatio(chr.getClient());
        MaplePet[] pets = chr.getSpawnPets();
        for (int i = 0; i < 3; ++i) {
            if (pets[i] == null || !pets[i].getSummoned()) continue;
            pets[i].setPos(chr.getPosition());
            chr.petUpdateStats(pets[i], true);
            chr.send(PetPacket.showPet((MapleCharacter)chr, (MaplePet)pets[i], (boolean)false, (byte)0, (boolean)true));
            chr.send(PetPacket.loadExceptionList((MapleCharacter)chr, (MaplePet)pets[i]));
        }
        if (chr.getAndroid() != null) {
            chr.getAndroid().setPos(chr.getPosition());
            this.broadcastMessage(AndroidPacket.spawnAndroid((MapleCharacter)chr, (MapleAndroid)chr.getAndroid()));
        }
        if (chr.getParty() != null) {
            chr.notifyChanges();
            chr.updatePartyMemberHP();
            chr.receivePartyMemberHP();
        }
        LinkedList<MapleQuickMove> qmList = new LinkedList<MapleQuickMove>();
        if (!chr.isInBlockedMap() && !chr.inEvent() && 875999999 != this.mapid) {
            Map<Integer, Integer> foreverBuffs;
            int 艾爾達斯的祝福;
            DeadDebuff deadDebuff;
            for (MapleQuickMove mapleQuickMove : this.QUICK_MOVE) {
                if (mapleQuickMove.TESTPIA && !ServerConfig.TESPIA || chr.getGmLevel() < mapleQuickMove.GM_LEVEL || chr.getLevel() < mapleQuickMove.MIN_LEVEL) continue;
                qmList.add(mapleQuickMove);
            }
            if (chr.getMap().getId() == 310000000 || chr.getMap().getId() == 220000000 || chr.getMap().getId() == 100000000 || chr.getMap().getId() == 250000000 || chr.getMap().getId() == 240000000 || chr.getMap().getId() == 104000000 || chr.getMap().getId() == 103000000 || chr.getMap().getId() == 102000000 || chr.getMap().getId() == 101000000 || chr.getMap().getId() == 120000000 || chr.getMap().getId() == 260000000 || chr.getMap().getId() == 200000000 || chr.getMap().getId() == 230000000) {
                chr.send(MaplePacketCreator.setQuickMoveInfo(qmList));
            }
            chr.send(MaplePacketCreator.Unknown_42D());
            chr.send(NPCPacket.sendNpcHide(this.hideNpc));
            List<MapleSummon> ss = chr.getSummonsReadLock();
            try {
                for (MapleSummon mapleSummon : ss) {
                    mapleSummon.setPosition(chr.getPosition());
                    chr.addVisibleMapObjectEx(mapleSummon);
                    this.spawnSummon(mapleSummon);
                }
            }
            finally {
                chr.unlockSummonsReadLock();
            }
            if (this.mapEffect != null) {
                this.mapEffect.sendStartData(chr.getClient());
            }
            if (this.timeLimit > 0 && this.getForcedReturnMap() != null) {
                chr.startMapTimeLimitTask(this.timeLimit, this.getForcedReturnMap());
            }
            if (chr.getBuffedValue(SecondaryStat.RideVehicle) != null && !JobConstants.is末日反抗軍(chr.getJob()) && FieldLimitType.TAMINGMOBLIMIT.check(this.fieldLimit)) {
                chr.dispelEffect(SecondaryStat.RideVehicle);
            }
            if (this.hasClock()) {
                Calendar calendar = Calendar.getInstance();
                chr.send(FieldPacket.clock((ClockPacket)ClockPacket.hmsClock((byte)1, (byte)((byte)calendar.get(11)), (byte)((byte)calendar.get(12)), (byte)((byte)calendar.get(13)))));
            }
            if (this.getMobSizeByID() > 0 && (this.mapid == 280030001 || this.mapid == 240060201 || this.mapid == 280030000 || this.mapid == 280030100 || this.mapid == 240060200 || this.mapid == 220080001 || this.mapid == 541020800 || this.mapid == 541010100)) {
                String var6_17 = "";
                String string = "Bgm09/TimeAttack";
                switch (this.mapid) {
                    case 240060200: 
                    case 240060201: {
                        String string2 = "Bgm14/HonTale";
                        break;
                    }
                    case 280030000: 
                    case 280030001: 
                    case 280030100: {
                        String string3 = "Bgm06/FinalFight";
                    }
                }
                chr.send(MaplePacketCreator.musicChange((String)var6_17));
            }
            if (this.mapid == 450008750 || this.mapid == 450008950) {
                chr.setMoonGauge(Math.max(0, chr.getMoonGauge() - 5));
                chr.clearWeb = 2;
                chr.getMap().broadcastMessage(WillPacket.addMoonGauge((int)(chr.getMoonGauge() + 5)));
                chr.getMap().broadcastMessage(WillPacket.cooldownMoonGauge((int)5000));
                Timer.MapTimer.getInstance().schedule(() -> {
                    chr.clearWeb = 0;
                }, 5000L);
            }
            if (this.mapid == 914000000 || this.mapid == 927000000) {
                chr.getMap().broadcastMessage(MaplePacketCreator.temporaryStats_Aran());
            }
            if (this.mapid == 450008150 || this.mapid == 450008750) {
                chr.getMap().broadcastMessage(WillPacket.setMoonGauge((int)1));
                chr.getMap().broadcastMessage(WillPacket.addMoonGauge((int)chr.getMoonGauge()));
            }
            if (this.mapid == 450008250 || this.mapid == 450008850) {
                chr.getMap().broadcastMessage(WillPacket.setMoonGauge((int)1));
                chr.getMap().broadcastMessage(WillPacket.addMoonGauge((int)chr.getMoonGauge()));
            }
            if (this.mapid == 450008350 || this.mapid == 450008950) {
                chr.getMap().broadcastMessage(WillPacket.setMoonGauge((int)1));
                chr.getMap().broadcastMessage(WillPacket.addMoonGauge((int)chr.getMoonGauge()));
            }
            if (this.mapid == 105100300 && chr.getLevel() >= 91) {
                chr.send(MaplePacketCreator.temporaryStats_Balrog(chr));
            }
            if (!this.lacheln.isEmpty()) {
                chr.send(MobPacket.lucidFieldFoothold(true, this.getLachelnList()));
            }
            if (!this.syncFH.isEmpty()) {
                chr.send(MaplePacketCreator.DynamicObjUrusSync(this.syncFH));
            }
            chr.send(MaplePacketCreator.temporaryStats_Reset());
            if (JobConstants.is龍魔導士(chr.getJob()) && chr.getJob() >= 2200) {
                if (chr.getDragon() == null) {
                    chr.makeDragon();
                } else {
                    chr.getDragon().setPosition(chr.getPosition());
                }
                if (chr.getDragon() != null) {
                    this.broadcastMessage(SummonPacket.spawnDragon(chr.getDragon()));
                }
            }
            if (JobConstants.is陰陽師(chr.getJob())) {
                if (chr.getHaku() == null) {
                    chr.initHaku();
                } else {
                    chr.getHaku().setPosition(chr.getPosition());
                }
                this.spawnSkillPet(chr.getHaku());
            }
            boolean bl = false;
            for (JobType jt : JobType.values()) {
                if (this.mapid != jt.mapId) continue;
                boolean bl2 = true;
                break;
            }
            if (this.permanentWeather > 0) {
                chr.send(MaplePacketCreator.startMapEffect("", this.permanentWeather, false));
            }
            if (this.getPlatforms().size() > 0) {
                chr.send(MaplePacketCreator.getMovingPlatforms(this));
            }
            if (this.partyBonusRate > 0) {
                // empty if block
            }
            if (this.isTown()) {
                chr.dispelEffect(SecondaryStat.Dance);
            }
            if (!this.canSoar()) {
                chr.dispelEffect(SecondaryStat.Flying);
            }
            if (chr.getJob() >= 2400 && chr.getJob() <= 2412) {
                chr.updateJudgementStack();
            }
            if (chr.getRuneUseCooldown() <= 0) {
                this.showRuneCurseStage(chr.getClient());
            }
            if (this.getSpawnPoints().size() > 0 && (deadDebuff = DeadDebuff.getDebuff(chr, 1)) != null) {
                chr.send(MaplePacketCreator.sendCTX_DEAD_BUFF_MESSAGE());
            }
            if (chr.getSkillLevel(艾爾達斯的祝福 = 80011993) > 0) {
                SecondaryStatValueHolder holder = chr.getBuffStatValueHolder(艾爾達斯的祝福);
                if (this.barrierArc > 0) {
                    if (holder == null) {
                        SkillFactory.getSkill(艾爾達斯的祝福).getEffect(1).applyTo(chr);
                    }
                } else if (holder != null) {
                    chr.dispelBuff(艾爾達斯的祝福);
                }
            }
            if (!GameConstants.isDojo(this.mapid)) {
                // empty if block
            }
            if ((foreverBuffs = chr.getForeverBuffs()) != null && !foreverBuffs.isEmpty()) {
                for (Map.Entry<Integer, Integer> skill : foreverBuffs.entrySet()) {
                    Skill skil;
                    if (chr.getBuffStatValueHolder(skill.getKey()) != null || (skil = SkillFactory.getSkill(skill.getKey())) == null || skil.getEffect(skill.getValue()) == null) continue;
                    skil.getEffect(skill.getValue()).applyTo(chr, chr, false, null, 2100000000);
                }
            }
            chr.giveRebornBuff();
        }
    }

    public int getNumItems() {
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            int n = this.mapobjects.get((Object)MapleMapObjectType.ITEM).size();
            return n;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
    }

    public int getMobSizeByID() {
        return this.getMobSizeByID(-1);
    }

    public int getMobSizeByID(int mobid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            if (mobid == -1) {
                int n = this.mapobjects.get((Object)MapleMapObjectType.MONSTER).size();
                return n;
            }
            int n = (int)this.mapobjects.get((Object)MapleMapObjectType.MONSTER).entrySet().stream().filter(entry -> ((MapleMonster)entry.getValue()).getId() == mobid).count();
            return n;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public boolean getEMByMap(MapleCharacter player) {
        String em;
        switch (this.mapid) {
            case 105100400: {
                em = "BossBalrog_EASY";
                break;
            }
            case 105100300: {
                em = "BossBalrog_NORMAL";
                break;
            }
            case 280030000: 
            case 280030100: {
                em = "ZakumBattle";
                break;
            }
            case 240060200: {
                em = "HorntailBattle";
                break;
            }
            case 280030001: {
                em = "ChaosZakum";
                break;
            }
            case 240060201: {
                em = "ChaosHorntail";
                break;
            }
            case 270050100: {
                em = "PinkBeanBattle";
                break;
            }
            case 270051100: {
                em = "ChaosPinkBean";
                break;
            }
            case 802000111: {
                em = "NamelessMagicMonster";
                break;
            }
            case 802000211: {
                em = "Vergamot";
                break;
            }
            case 802000311: {
                em = "2095_tokyo";
                break;
            }
            case 802000411: {
                em = "Dunas";
                break;
            }
            case 802000611: {
                em = "Nibergen";
                break;
            }
            case 802000711: {
                em = "Dunas2";
                break;
            }
            case 802000801: 
            case 802000802: 
            case 802000803: {
                em = "CoreBlaze";
                break;
            }
            case 802000821: 
            case 802000823: {
                em = "Aufhaven";
                break;
            }
            case 211070100: 
            case 211070101: 
            case 211070110: {
                em = "VonLeonBattle";
                break;
            }
            case 551030200: {
                em = "ScarTarBattle";
                break;
            }
            case 271040100: {
                em = "CygnusBattle";
                break;
            }
            case 689013000: {
                em = "PinkZakum";
                break;
            }
            case 262031300: 
            case 262031310: {
                em = "Hillah_170";
                break;
            }
            case 272030400: 
            case 272030420: {
                em = "ArkariumBattle";
                break;
            }
            default: {
                return false;
            }
        }
        String ScriptName = player.getEventInstance().getScriptName();
        return ScriptName != null && ScriptName.equals(em);
    }

    public void userLeaveField(MapleCharacter chr) {
        chr.getTempValues().remove("MobZoneState");
        for (MapleAffectedArea area : this.getAllAffectedAreasThreadsafe()) {
            area.handleEffect(chr, -2);
        }
        if (chr.getBuffStatValueHolder(400021114) != null) {
            chr.dispelEffect(400021114);
        }
        if (chr.getSpecialStat().getPoolMakerCount() > 0) {
            chr.getSpecialStat().setPoolMakerCount(0);
            chr.send(MaplePacketCreator.poolMakerInfo(false, 0, 0));
            this.removeAffectedArea(chr.getId(), 400051076);
        }
        if (this.everlast) {
            this.returnEverLastItem(chr);
        }
        this.removeMapObject(chr);
        chr.checkFollow();
        chr.removeExtractor();
        this.broadcastMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));
        if (chr.getHaku() != null) {
            this.removeMapObject(chr.getHaku());
            chr.getHaku().setObjectId(0);
        }
        this.removeVisibleSummon(chr);
        if (this.mapid == 109020001) {
            chr.canTalk(true);
        }
        chr.leaveMap(this);
        if (this.getOwner() == chr.getId()) {
            this.setOwner(-1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeVisibleSummon(MapleCharacter chr) {
        ArrayList<MapleSummon> toCancel = new ArrayList();
        List<MapleSummon> listSummons = chr.getSummonsReadLock();
        try {
            listSummons.forEach(summon -> {
                this.broadcastMessage(SummonPacket.removeSummon(summon, true));
                this.removeMapObject((MapleMapObject)summon);
                chr.removeVisibleMapObjectEx((MapleMapObject)summon);
                if (summon.isChangeMapCanceled()) {
                    toCancel.add(summon);
                } else {
                    summon.setChangedMap(true);
                }
            });
        }
        finally {
            chr.unlockSummonsReadLock();
        }
        toCancel.forEach(summon -> {
            chr.dispelSkill(summon.getSkillId());
            chr.removeSummon((MapleSummon)summon);
        });
    }

    public void broadcastGmLvMessage(MapleCharacter source, byte[] packet) {
        this.broadcastGmLvMessage(source, packet, true);
    }

    public void broadcastGmLvMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        this.broadcastMessage(source, packet, Double.POSITIVE_INFINITY, source == null ? null : source.getPosition(), repeatToSource, new Pair<Integer, Integer>(source == null ? 1 : source.getGmLevel(), Integer.MAX_VALUE));
    }

    public void broadcastBelowGmLvMessage(MapleCharacter source, byte[] packet) {
        this.broadcastBelowGmLvMessage(source, packet, true);
    }

    public void broadcastBelowGmLvMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        this.broadcastMessage(source, packet, Double.POSITIVE_INFINITY, source == null ? null : source.getPosition(), repeatToSource, new Pair<Integer, Integer>(0, source == null ? 0 : Math.max(source.getGmLevel() - 1, 0)));
    }

    public void broadcastMessage(byte[] packet) {
        this.broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null, false);
    }

    public void broadcastMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        this.broadcastMessage(source, packet, Double.POSITIVE_INFINITY, source.getPosition(), repeatToSource);
    }

    public void broadcastMessage(byte[] packet, Point rangedFrom) {
        this.broadcastMessage(null, packet, GameConstants.maxViewRange(), rangedFrom, false);
    }

    public void broadcastMessage(MapleCharacter source, byte[] packet, Point rangedFrom) {
        this.broadcastMessage(source, packet, GameConstants.maxViewRange(), rangedFrom, true);
    }

    public void broadcastMessage(MapleCharacter source, byte[] packet, Point rangedFrom, boolean repeatToSource) {
        this.broadcastMessage(source, packet, GameConstants.maxViewRange(), rangedFrom, repeatToSource);
    }

    public void broadcastMessage(MapleCharacter source, byte[] packet, double range, Point rangedFrom, boolean repeatToSource) {
        this.broadcastMessage(source, packet, range, rangedFrom, repeatToSource, new Pair<Integer, Integer>(0, Integer.MAX_VALUE));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcastMessage(MapleCharacter source, byte[] packet, double range, Point rangedFrom, boolean repeatToSource, Pair<Integer, Integer> allowGmLevel) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter chr = (MapleCharacter)_mmo;
                if (!repeatToSource && chr == source || chr.getGmLevel() < allowGmLevel.getLeft() || chr.getGmLevel() > allowGmLevel.getRight() || source != null && source.isHidden() && source.getGmLevel() > chr.getGmLevel() || range < Double.POSITIVE_INFINITY && rangedFrom != null && rangedFrom.distance(chr.getPosition()) > range) continue;
                chr.send(packet);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcastRuneCurseMessage(byte[] packet) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter chr = (MapleCharacter)_mmo;
                if (chr.getRuneUseCooldown() > 0) continue;
                chr.send(packet);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    private void sendObjectPlacement(MapleCharacter chr) {
        if (chr == null || chr.getClient() == null) {
            return;
        }
        this.getMapObjectsInRange(chr.getPosition(), chr.getRange(), GameConstants.rangedMapobjectTypes).forEach(o -> {
            if (o.getType() == MapleMapObjectType.REACTOR && !((MapleReactor)o).isAlive()) {
                return;
            }
            if (chr == o) {
                return;
            }
            o.sendSpawnData(chr.getClient());
            chr.addVisibleMapObjectEx((MapleMapObject)o);
        });
    }

    public List<MaplePortal> getPortalsInRange(Point from, double range) {
        return this.portals.values().stream().filter(type -> from.distance(type.getPosition()) <= range && type.getTargetMapId() != this.mapid && type.getTargetMapId() != 999999999).collect(Collectors.toList());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapObject> getMapObjectsInRange(Point from, double range) {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            this.mapobjectlocks.get((Object)type).readLock().lock();
            try {
                this.mapobjects.get((Object)type).values().stream().filter(mmo -> from.distance(mmo.getPosition()) <= range).forEach(ret::add);
            }
            finally {
                this.mapobjectlocks.get((Object)type).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double range) {
        return this.getMapObjectsInRange(from, range, Collections.singletonList(MapleMapObjectType.ITEM));
    }

    public List<MapleMapObject> getMonstersInRange(Point from, double range) {
        return this.getMapObjectsInRange(from, range, Collections.singletonList(MapleMapObjectType.MONSTER));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapObject> getMapObjectsInRange(Point from, double range, List<MapleMapObjectType> MapObject_types) {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObjectType type : MapObject_types) {
            this.mapobjectlocks.get((Object)type).readLock().lock();
            try {
                this.mapobjects.get((Object)type).values().stream().filter(mmo -> from.distance(mmo.getPosition()) <= range).forEach(ret::add);
            }
            finally {
                this.mapobjectlocks.get((Object)type).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRect(Rectangle box) {
        return this.getMapObjectsInRect(box, Collections.singletonList(MapleMapObjectType.ITEM));
    }

    public List<MapleMapObject> getMonstersInRect(Rectangle box) {
        return this.getMapObjectsInRect(box, Collections.singletonList(MapleMapObjectType.MONSTER));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapObject> getMapObjectsInRect(Rectangle box, List<MapleMapObjectType> MapObject_types) {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObjectType type : MapObject_types) {
            this.mapobjectlocks.get((Object)type).readLock().lock();
            try {
                this.mapobjects.get((Object)type).values().stream().filter(mmo -> box.contains(mmo.getPosition())).forEach(ret::add);
            }
            finally {
                this.mapobjectlocks.get((Object)type).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleCharacter> getCharactersInRect(Rectangle box) {
        List<MapleCharacter> ret;
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values().stream().map(it -> (MapleCharacter)it).filter(chr -> box.contains(chr.getPosition())).collect(Collectors.toList());
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleCharacter> getPlayersInRectAndInList(Rectangle box, List<MapleCharacter> chrList) {
        List<MapleCharacter> character;
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            character = this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values().stream().map(it -> (MapleCharacter)it).filter(a -> chrList.contains(a) && box.contains(a.getPosition())).collect(Collectors.toList());
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return character;
    }

    public void addPortal(MaplePortal myPortal) {
        this.portals.put(myPortal.getId(), myPortal);
    }

    public MaplePortal getPortal(String portalname) {
        return this.portals.values().stream().filter(port -> port.getName().equals(portalname)).findFirst().orElse(null);
    }

    public MaplePortal getPortal(int portalid) {
        return this.portals.get(portalid);
    }

    public List<MaplePortal> getPortalSP() {
        return this.portals.values().stream().filter(port -> port.getName().equals("sp")).collect(Collectors.toCollection(LinkedList::new));
    }

    public void resetPortals() {
        for (MaplePortal port : this.portals.values()) {
            port.setPortalState(true);
        }
    }

    public MapleFootholdTree getFootholds() {
        return this.footholds;
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public int getNumSpawnPoints() {
        return this.monsterSpawn.size();
    }

    public void loadMonsterRate() {
        int spawnSize = this.monsterSpawn.size();
        this.maxRegularSpawn = (int)Math.ceil((float)spawnSize * this.monsterRate);
        if (this.fixedMob > 0) {
            this.maxRegularSpawn = this.fixedMob;
        } else if (this.maxRegularSpawn <= 2) {
            this.maxRegularSpawn = 2;
        }
        LinkedList newSpawn = new LinkedList();
        LinkedList newBossSpawn = new LinkedList();
        this.monsterSpawn.stream().filter(s -> s.getCarnivalTeam() < 2).forEach(s -> {
            if (s.getMonster().isBoss()) {
                newBossSpawn.add(s);
            } else {
                newSpawn.add(s);
            }
        });
        this.monsterSpawn.clear();
        this.monsterSpawn.addAll(newBossSpawn);
        this.monsterSpawn.addAll(newSpawn);
        if (spawnSize > 0 && GameConstants.isForceRespawn(this.mapid)) {
            this.createMobInterval = (short)15000;
        }
    }

    public SpawnPoint addMonsterSpawn(MapleMonster monster, int mobTime, byte carnivalTeam, String msg) {
        if (monster == null) {
            return null;
        }
        Point newpos = this.calcPointBelow(monster.getPosition());
        --newpos.y;
        SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
        if (carnivalTeam > -1) {
            this.monsterSpawn.add(0, sp);
        } else {
            this.monsterSpawn.add(sp);
        }
        return sp;
    }

    public void addAreaMonsterSpawn(MapleMonster monster, Point pos1, Point pos2, Point pos3, int mobTime, String msg, boolean shouldSpawn, boolean sendWorldMsg) {
        pos1 = this.calcPointBelow(pos1);
        pos2 = this.calcPointBelow(pos2);
        pos3 = this.calcPointBelow(pos3);
        if (pos1 != null) {
            --pos1.y;
        }
        if (pos2 != null) {
            --pos2.y;
        }
        if (pos3 != null) {
            --pos3.y;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: mapid " + this.mapid + ", monster " + monster.getId() + " could not be spawned.");
            return;
        }
        if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        this.monsterSpawn.add((Spawns)new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg, shouldSpawn, sendWorldMsg));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getPlayerIDs(int count) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        List<MapleCharacter> characters = this.getCharacters();
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (int i = 0; i < count; ++i) {
                list.add(characters.get(Randomizer.nextInt(characters.size())).getId());
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return list;
    }

    public List<MapleCharacter> getCharacters() {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            List<MapleCharacter> list = this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values().stream().map(it -> (MapleCharacter)it).collect(Collectors.toList());
            return list;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleCharacter getCharacterByName(String id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter mc = (MapleCharacter)_mmo;
                if (!mc.getName().equalsIgnoreCase(id)) continue;
                MapleCharacter mapleCharacter = mc;
                return mapleCharacter;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return null;
    }

    @Deprecated
    public MapleCharacter getCharacterById(int id) {
        return this.getPlayerObject(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleCharacter getPlayerObject(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter mc = (MapleCharacter)_mmo;
                if (mc.getId() != id) continue;
                MapleCharacter mapleCharacter = mc;
                return mapleCharacter;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return null;
    }

    public void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
        if (chr == null) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) {
            if (mo.getPosition().distance(chr.getPosition()) <= (double)mo.getRange()) {
                chr.addVisibleMapObjectEx(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else if (mo.getPosition().distance(chr.getPosition()) > (double)mo.getRange()) {
            chr.removeVisibleMapObjectEx(mo);
            mo.sendDestroyData(chr.getClient());
        } else if (mo.getType() == MapleMapObjectType.MONSTER && chr.getPosition().distance(mo.getPosition()) <= (double)GameConstants.maxViewRange_Half()) {
            this.updateMonsterController((MapleMonster)mo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter mc = (MapleCharacter)_mmo;
                this.updateMapObjectVisibility(mc, monster);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = this.getPortal(0);
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : this.portals.values()) {
            double distance = portal.getPosition().distance(from);
            if (portal.getType() < 0 || portal.getType() > 2 || !(distance < shortestDistance) || portal.getTargetMapId() != 999999999) continue;
            closest = portal;
            shortestDistance = distance;
        }
        return closest;
    }

    public MaplePortal findClosestPortal(Point from) {
        MaplePortal closest = this.getPortal(0);
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : this.portals.values()) {
            double distance = portal.getPosition().distance(from);
            if (!(distance < shortestDistance)) continue;
            closest = portal;
            shortestDistance = distance;
        }
        return closest;
    }

    public MaplePortal getRandomSpawnpoint() {
        ArrayList<MaplePortal> spawnPoints_ = new ArrayList<MaplePortal>();
        for (MaplePortal portal : this.portals.values()) {
            if (portal.getType() < 0 || portal.getType() > 2) continue;
            spawnPoints_.add(portal);
        }
        MaplePortal portal = (MaplePortal)spawnPoints_.get(new Random().nextInt(spawnPoints_.size()));
        return portal != null ? portal : this.getPortal(0);
    }

    public int getMapObjectSize() {
        return this.mapobjects.size();
    }

    public int getCharactersSize() {
        int ret = 0;
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            ret = this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values().size();
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return ret;
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(this.portals.values());
    }

    public int getSpawnedMonstersOnMap() {
        return this.spawnedMonstersOnMap.get();
    }

    public int getSpawnedForcesOnMap() {
        return this.spawnedForcesOnMap.incrementAndGet();
    }

    public int getSpawnedAffectedAreaOnMap() {
        return this.spawnedAffectedAreaOnMap.incrementAndGet();
    }

    public void checkRemoveDeadMob() {
        for (MapleMonster mob : this.getAllMonster()) {
            if (mob.isAlive()) continue;
            this.killMonster(mob);
        }
    }

    public void respawn(boolean force) {
        this.respawn(force, System.currentTimeMillis());
    }

    public void respawn(boolean force, long now) {
        block16: {
            block17: {
                this.checkRemoveDeadMob();
                this.lastSpawnTime = now;
                if (!force) break block17;
                int numShouldSpawn = this.monsterSpawn.size() - this.spawnedMonstersOnMap.get();
                if (numShouldSpawn <= 0) break block16;
                int spawned = 0;
                for (Spawns spawnPoint : this.monsterSpawn) {
                    spawnPoint.spawnMonster(this);
                    if (++spawned < numShouldSpawn) continue;
                    break block16;
                }
                break block16;
            }
            int extraSpawnNum = 0;
            ChannelServer ch = ChannelServer.getInstance(this.channel);
            if (ch != null) {
                switch (ch.getChannelType()) {
                    case ABNORMAL: 
                    case MVP_BRONZE: {
                        this.incSpawnMob.put("特別頻道", 10);
                        break;
                    }
                    case MVP_SILVER: {
                        this.incSpawnMob.put("特別頻道", 20);
                        break;
                    }
                    case MVP_GOLD: {
                        this.incSpawnMob.put("特別頻道", 30);
                        break;
                    }
                    case MVP_DIAMOND: {
                        this.incSpawnMob.put("特別頻道", 40);
                        break;
                    }
                    case MVP_RED: {
                        this.incSpawnMob.put("特別頻道", 50);
                    }
                }
            } else {
                this.incSpawnMob.remove("特別頻道");
            }
            Iterator<Integer> iterator = this.incSpawnMob.values().iterator();
            while (iterator.hasNext()) {
                float rate = iterator.next().intValue();
                extraSpawnNum += (int)Math.floor((double)rate / 100.0 * (double)this.maxRegularSpawn);
            }
            int maxSpawnNum = GameConstants.isForceRespawn(this.mapid) ? this.monsterSpawn.size() : Math.min(this.maxRegularSpawn + extraSpawnNum, this.monsterSpawn.size() * 3);
            int eachSpawnNum = this.monsterSpawn.size();
            Iterator<Integer> iterator2 = this.eachIncSpawnMob.values().iterator();
            while (iterator2.hasNext()) {
                float rate = iterator2.next().intValue();
                eachSpawnNum += (int)Math.floor((double)rate / 100.0 * (double)maxSpawnNum);
            }
            int numShouldSpawn = Math.min(Math.min(eachSpawnNum, (int)((double)this.monsterSpawn.size() * 1.5)), maxSpawnNum - this.spawnedMonstersOnMap.get());
            if (numShouldSpawn > 0) {
                int spawned = 0;
                ArrayList<Spawns> randomSpawn = new ArrayList<Spawns>(this.monsterSpawn);
                Collections.shuffle(randomSpawn);
                block10: for (int i = 0; i < (numShouldSpawn > randomSpawn.size() ? 2 : 1); ++i) {
                    for (Spawns spawnPoint : randomSpawn) {
                        if (!this.isSpawns && spawnPoint.getMobTime() > 0) continue;
                        if (spawnPoint.shouldSpawn(this.lastSpawnTime) || GameConstants.isForceRespawn(this.mapid) || this.monsterSpawn.size() < 10 && this.maxRegularSpawn > this.monsterSpawn.size() && this.partyBonusRate > 0 && (this.limitMobID <= 0 || this.limitMobID == spawnPoint.getMonster().getId())) {
                            spawnPoint.spawnMonster(this);
                            ++spawned;
                        }
                        if (spawned < numShouldSpawn) continue;
                        continue block10;
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getSnowballPortal() {
        int[] teamss = new int[2];
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter chr = (MapleCharacter)_mmo;
                if (chr.getPosition().y > -80) {
                    teamss[0] = teamss[0] + 1;
                    continue;
                }
                teamss[1] = teamss[1] + 1;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        }
        return "st00";
    }

    public boolean isDisconnected(int id) {
        return this.dced.contains(id);
    }

    public void addDisconnected(int id) {
        this.dced.add(id);
    }

    public void resetDisconnected() {
        this.dced.clear();
    }

    public void disconnectAll() {
        for (MapleCharacter chr : this.getCharacters()) {
            if (chr.isGm()) continue;
            chr.getClient().disconnect(true, false);
            chr.getClient().getSession().close();
        }
    }

    public List<MapleNPC> getAllNPCs() {
        return this.getAllNPCsThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleNPC> getAllNPCsThreadsafe() {
        ArrayList<MapleNPC> ret = new ArrayList<MapleNPC>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                ret.add((MapleNPC)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
        return ret;
    }

    public void resetNPCs() {
        this.removeNpc(-1);
    }

    public void resetPQ(int level) {
        this.resetFully();
        for (MapleMonster mons : this.getMonsters()) {
            mons.setForcedMobStat(level);
        }
        this.resetSpawnLevel(level);
    }

    public void resetSpawnLevel(int level) {
        for (Spawns spawn : this.monsterSpawn) {
            if (!(spawn instanceof SpawnPoint)) continue;
            ((SpawnPoint)spawn).setLevel(level);
        }
    }

    public void resetFully(ScriptEvent eventInstance) {
        this.resetFully(true);
        for (MapleMonster monster : this.getAllMonster()) {
            monster.setEventInstance(eventInstance);
        }
    }

    public void resetFully(ScriptEvent eventInstance, int newLevel) {
        this.resetFully(true);
        for (MapleMonster monster : this.getAllMonster()) {
            monster.setEventInstance(eventInstance);
            monster.setForcedMobStat(newLevel);
        }
        this.resetSpawnLevel(newLevel);
    }

    public void resetFully() {
        this.resetFully(true);
    }

    public void resetFully(boolean respawn) {
        this.killAllMonsters(false);
        this.reloadReactors();
        this.removeDrops();
        this.resetNPCs();
        this.resetSpawns();
        this.resetDisconnected();
        this.resetPortals();
        this.limitMobID = 0;
        if (respawn) {
            this.respawn(true);
        }
    }

    public void obtacleFall(int count, int type1, int type2) {
        this.broadcastMessage(MaplePacketCreator.createObtacleAtom(count, type1, type2, this.getChr().getMap()));
    }

    public void upDropAttack(int count, int type1, int type2) {
        this.broadcastMessage(MaplePacketCreator.UpDropAttack(count, type1, type2, this.getChr().getMap()));
    }

    public void removeDrops() {
        List<MapleMapItem> mapItems = this.getAllItemsThreadsafe();
        for (MapleMapItem mapItem : mapItems) {
            mapItem.expire(this);
        }
    }

    public void removeDropsDelay() {
        List<MapleMapItem> mapItems = this.getAllItemsThreadsafe();
        int delay = 0;
        int i = 0;
        for (MapleMapItem mapItem : mapItems) {
            if (++i < 50) {
                mapItem.expire(this);
                continue;
            }
            ++delay;
            if (mapItem.hasFFA()) {
                mapItem.registerFFA((long)delay * 20L);
                continue;
            }
            mapItem.registerExpire((long)delay * 30L);
        }
    }

    public void resetAllSpawnPoint(int mobid, int mobTime) {
        LinkedList<Spawns> AllSpawnPoints = new LinkedList<Spawns>(this.monsterSpawn);
        this.resetFully();
        this.monsterSpawn.clear();
        for (Spawns spawnPoint : AllSpawnPoints) {
            MapleMonster newMons = MapleLifeFactory.getMonster(mobid);
            newMons.setF(spawnPoint.getF());
            newMons.setCurrentFh(spawnPoint.getFh());
            newMons.setPosition(spawnPoint.getPosition());
            this.addMonsterSpawn(newMons, mobTime, (byte)-1, null);
        }
        this.loadMonsterRate();
    }

    public void resetSpawns() {
        boolean changed = false;
        Iterator<Spawns> AllSpawnPoints = this.monsterSpawn.iterator();
        while (AllSpawnPoints.hasNext()) {
            if (AllSpawnPoints.next().getCarnivalId() <= -1) continue;
            AllSpawnPoints.remove();
            changed = true;
        }
        this.setSpawns(true);
        if (changed) {
            this.loadMonsterRate();
        }
    }

    public boolean makeCarnivalSpawn(int team, MapleMonster newMons, int num) {
        MapleNodes.MonsterPoint ret = null;
        for (MapleNodes.MonsterPoint mp : this.nodes.getMonsterPoints()) {
            if (mp.team != team && mp.team != -1) continue;
            Point newpos = this.calcPointBelow(new Point(mp.x, mp.y));
            --newpos.y;
            boolean found = false;
            for (Spawns s : this.monsterSpawn) {
                if (s.getCarnivalId() <= -1 || mp.team != -1 && s.getCarnivalTeam() != mp.team || s.getPosition().x != newpos.x || s.getPosition().y != newpos.y) continue;
                found = true;
                break;
            }
            if (found) continue;
            ret = mp;
            break;
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0);
            newMons.setCurrentFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50);
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            SpawnPoint sp = this.addMonsterSpawn(newMons, 1, (byte)team, null);
            sp.setCarnival(num);
        }
        return ret != null;
    }

    public boolean makeCarnivalReactor(int team, int num) {
        MapleReactor old = this.getReactorByName(team + String.valueOf(num));
        if (old != null && old.getState() < 5) {
            return false;
        }
        Point guardz = null;
        List<MapleReactor> react = this.getAllReactorsThreadsafe();
        for (Pair<Point, Integer> guard : this.nodes.getGuardians()) {
            if ((Integer)guard.right != team && (Integer)guard.right != -1) continue;
            boolean found = false;
            for (MapleReactor r : react) {
                if (r.getPosition().x != ((Point)guard.left).x || r.getPosition().y != ((Point)guard.left).y || r.getState() >= 5) continue;
                found = true;
                break;
            }
            if (found) continue;
            guardz = (Point)guard.left;
            break;
        }
        if (guardz != null) {
            MapleReactor my = new MapleReactor(MapleReactorFactory.getReactor(9980000 + team), 9980000 + team);
            my.setState((byte)1);
            my.setName(team + String.valueOf(num));
            this.spawnReactorOnGroundBelow(my, guardz);
        }
        return guardz != null;
    }

    public void blockAllPortal() {
        for (MaplePortal p : this.portals.values()) {
            p.setPortalState(false);
        }
    }

    public int getAndAddObjectId() {
        this.runningOidLock.lock();
        try {
            int n = this.runningOid.getAndIncrement();
            return n;
        }
        finally {
            this.runningOidLock.unlock();
        }
    }

    public boolean getAndSwitchTeam() {
        return this.getCharactersSize() % 2 != 0;
    }

    public int getChannel() {
        return this.channel;
    }

    public int getConsumeItemCoolTime() {
        return this.consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int ciit) {
        this.consumeItemCoolTime = ciit;
    }

    public int getPermanentWeather() {
        return this.permanentWeather;
    }

    public void setPermanentWeather(int pw) {
        this.permanentWeather = pw;
    }

    public List<MapleNodes.MaplePlatform> getPlatforms() {
        return this.nodes.getPlatforms();
    }

    public Collection<MapleNodes.MapleNodeInfo> getNodes() {
        return this.nodes.getNodes();
    }

    public void setNodes(MapleNodes mn) {
        this.nodes = mn;
    }

    public MapleNodes.MapleNodeInfo getNode(int index) {
        return this.nodes.getNode(index);
    }

    public boolean isLastNode(int index) {
        return this.nodes.isLastNode(index);
    }

    public List<Rectangle> getAreas() {
        return this.nodes.getAreas();
    }

    public Rectangle getArea(int index) {
        return this.nodes.getArea(index);
    }

    public int getNumPlayersInArea(int index) {
        return this.getNumPlayersInRect(this.getArea(index));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumPlayersInRect(Rectangle rect) {
        int ret = 0;
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter character = (MapleCharacter)_mmo;
                if (!rect.contains(character.getPosition())) continue;
                ++ret;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return ret;
    }

    public int getNumPlayersItemsInArea(int index) {
        return this.getNumPlayersItemsInRect(this.getArea(index));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumPlayersItemsInRect(Rectangle rect) {
        int ret = this.getNumPlayersInRect(rect);
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.ITEM).values()) {
                if (!rect.contains(mmo.getPosition())) continue;
                ++ret;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public List<Pair<Integer, Integer>> getMobsToSpawn() {
        return this.nodes.getMobsToSpawn();
    }

    public List<Integer> getSkillIds() {
        return this.nodes.getSkillIds();
    }

    public boolean canSpawn(long now) {
        if (!this.isSpawns) {
            return false;
        }
        if (this.lastSpawnTime <= 0L) {
            this.lastSpawnTime = System.currentTimeMillis();
            return false;
        }
        int createMobIntervalTime = this.createMobInterval - (int)Math.floor((double)this.decMobIntervalR / 100.0 * (double)this.createMobInterval);
        return this.lastSpawnTime + (long)Math.max(this.decMobIntervalR >= 100 ? 0 : 2000, createMobIntervalTime) < now;
    }

    public boolean canHurt(long now) {
        if (this.lastHurtTime > 0L && this.lastHurtTime + (long)this.decHPInterval < now) {
            this.lastHurtTime = now;
            return true;
        }
        return false;
    }

    public void resetShammos(MapleClient c) {
        this.killAllMonsters(true);
        this.broadcastMessage(MaplePacketCreator.serverNotice(5, "A player has moved too far from Shammos. Shammos is going back to the start."));
        Timer.EtcTimer.getInstance().schedule(() -> {
            if (c.getPlayer() != null) {
                c.getPlayer().changeMap(this, this.getPortal(0));
                if (this.getCharacters().size() > 1) {
                    c.getPlayer().getScriptManager().startOnFirstUserScript("shammos_Fenter");
                }
            }
        }, 500L);
    }

    public int getInstanceId() {
        return this.instanceid;
    }

    public void setInstanceId(int ii) {
        this.instanceid = ii;
    }

    public int getPartyBonusRate() {
        return this.partyBonusRate;
    }

    public void setPartyBonusRate(int ii) {
        this.partyBonusRate = ii;
    }

    public short getTop() {
        return this.top;
    }

    public void setTop(int ii) {
        this.top = (short)ii;
    }

    public short getBottom() {
        return this.bottom;
    }

    public void setBottom(int ii) {
        this.bottom = (short)ii;
    }

    public short getLeft() {
        return this.left;
    }

    public void setLeft(int ii) {
        this.left = (short)ii;
    }

    public short getRight() {
        return this.right;
    }

    public void setRight(int ii) {
        this.right = (short)ii;
    }

    public List<Pair<Point, Integer>> getGuardians() {
        return this.nodes.getGuardians();
    }

    public MapleNodes.DirectionInfo getDirectionInfo(int i) {
        return this.nodes.getDirection(i);
    }

    public boolean isMarketMap() {
        return this.mapid >= 910000000 && this.mapid <= 910000017;
    }

    public boolean isPvpMaps() {
        return this.isPvpMap() || this.isPartyPvpMap() || this.isGuildPvpMap();
    }

    public boolean isPvpMap() {
        return ServerConstants.isPvpMap(this.mapid);
    }

    public boolean isPartyPvpMap() {
        return this.mapid == 910000019 || this.mapid == 910000020;
    }

    public boolean isGuildPvpMap() {
        return this.mapid == 910000021 || this.mapid == 910000022;
    }

    public boolean isBossMap() {
        switch (this.mapid) {
            case 105100300: 
            case 105100400: 
            case 211070100: 
            case 211070101: 
            case 211070110: 
            case 220080001: 
            case 240040700: 
            case 240060200: 
            case 240060201: 
            case 262031300: 
            case 262031310: 
            case 270050100: 
            case 271040100: 
            case 271040200: 
            case 272030400: 
            case 272030420: 
            case 280030000: 
            case 280030001: 
            case 280030100: 
            case 300030310: 
            case 551030200: 
            case 802000111: 
            case 802000211: 
            case 802000311: 
            case 802000411: 
            case 802000611: 
            case 802000711: 
            case 802000801: 
            case 802000802: 
            case 802000803: 
            case 802000821: 
            case 802000823: {
                return true;
            }
        }
        return false;
    }

    public void checkMoveMonster(Point from, boolean fly, MapleCharacter chr) {
        if (this.maxRegularSpawn <= 2 || this.monsterSpawn.isEmpty() || (double)this.monsterRate <= 1.0 || chr == null) {
            return;
        }
        int check = (int)((double)(fly ? 70 : 60) / 100.0 * (double)this.maxRegularSpawn);
        if (this.getMonstersInRange(from, 71.0).size() >= check) {
            for (MapleMapObject obj : this.getMonstersInRange(from, Double.POSITIVE_INFINITY)) {
                MapleMonster mob = (MapleMonster)obj;
                this.killMonster(mob, chr, false, false, (byte)1, 0);
            }
        }
    }

    public void createdFieldAttackObject(MapleFieldAttackObj attackObj) {
        this.addMapObject(attackObj);
        this.objectMove(-1, attackObj, null);
        attackObj.setSchedule(Timer.MapTimer.getInstance().schedule(() -> {
            this.disappearMapObject(attackObj);
            attackObj.cancel();
        }, attackObj.getDuration()));
        attackObj.setState(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleFieldAttackObj> getFieldAttackObject(MapleCharacter chr) {
        ArrayList<MapleFieldAttackObj> ret = new ArrayList<MapleFieldAttackObj>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.FIELD_ATTACK_OBJ).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.FIELD_ATTACK_OBJ).values()) {
                MapleFieldAttackObj obj = (MapleFieldAttackObj)mmo;
                if (obj.getOwnerId() != chr.getId()) continue;
                ret.add(obj);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.FIELD_ATTACK_OBJ).readLock().unlock();
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleFieldAttackObj> getFieldAttackObject(MapleCharacter chr, int state) {
        ArrayList<MapleFieldAttackObj> ret = new ArrayList<MapleFieldAttackObj>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.FIELD_ATTACK_OBJ).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.FIELD_ATTACK_OBJ).values()) {
                MapleFieldAttackObj obj = (MapleFieldAttackObj)mmo;
                if (obj.getState() != state || obj.getOwnerId() != chr.getId()) continue;
                ret.add(obj);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.FIELD_ATTACK_OBJ).readLock().unlock();
        }
        return ret;
    }

    public void addKSPsychicObject(int chrid, int skillid, List<KSPsychicSkillEntry> infos) {
        HashMap ksobj = new HashMap();
        ArrayList<Pair<Integer, Integer>> objs = new ArrayList<Pair<Integer, Integer>>();
        for (KSPsychicSkillEntry ksse : infos) {
            objs.add(new Pair<Integer, Integer>(ksse.getOid(), ksse.getMobOid() != 0 ? ksse.getMobOid() : (int)ksse.getObjectid()));
        }
        ksobj.put(skillid, objs);
        this.kspsychicObjects.put(chrid, ksobj);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int removeKSPsychicObject(int chrid, int skillid, int moboid) {
        int oid = -1;
        this.kspsychicLock.writeLock().lock();
        try {
            if (!this.kspsychicObjects.containsKey(chrid)) {
                int n = oid;
                return n;
            }
            if (!this.kspsychicObjects.get(chrid).containsKey(skillid)) {
                int n = oid;
                return n;
            }
            Iterator<Pair<Integer, Integer>> it = this.kspsychicObjects.get(chrid).get(skillid).iterator();
            while (it.hasNext()) {
                Pair<Integer, Integer> ks = it.next();
                if (ks.getRight() != moboid) continue;
                oid = ks.getLeft();
                it.remove();
            }
        }
        finally {
            this.kspsychicLock.writeLock().unlock();
        }
        return oid;
    }

    public void addKSUltimateSkill(int chrid, int moboid) {
        this.ksultimates.put(chrid, moboid);
    }

    public void removeKSUltimateSkill(int chrid, int moboid) {
        this.ksultimates.remove(chrid, moboid);
    }

    public boolean isKSUltimateSkill(int chrid, int moboid) {
        return this.ksultimates.containsKey(chrid) && this.ksultimates.get(chrid) == moboid;
    }

    public Point getRandomPos(Point pos) {
        MapleFoothold fh;
        pos = new Point(pos);
        List<MapleFoothold> relevants = this.getFootholds().getAllRelevants();
        int size = relevants.size();
        if (size > 0 && (fh = relevants.get(Randomizer.nextInt(size))) != null) {
            boolean b = fh.getX1() > fh.getX2();
            int z = Randomizer.rand(b ? fh.getX2() : fh.getX1(), b ? fh.getX1() : fh.getX2());
            boolean b2 = fh.getY1() > fh.getY2();
            pos = new Point(z, Randomizer.rand(b2 ? fh.getY2() : fh.getY1(), b2 ? fh.getY1() : fh.getY2()));
        }
        return pos;
    }

    public Point getRandomPoint() {
        ArrayList arrayList = new ArrayList();
        this.getFootholds().getAllRelevants().forEach(p2 -> {
            int n2 = p2.getX1();
            int n3 = p2.getX2();
            int n4 = p2.getY1();
            int n5 = p2.getY2();
            int n6 = 0;
            if (n2 > n4) {
                n6 = n2;
                n2 = n4;
                n4 = n6;
            }
            if (n3 > n5) {
                n6 = n3;
                n3 = n5;
                n5 = n6;
            }
            arrayList.add(new Point(Randomizer.rand(n2, n4), Randomizer.rand(n3, n5)));
        });
        return (Point)arrayList.get(Randomizer.nextInt(arrayList.size()));
    }

    public void setEntrustedFishing(boolean entrustedFishing) {
        this.entrustedFishing = entrustedFishing;
    }

    public boolean allowFishing() {
        return this.entrustedFishing;
    }

    public Map<Integer, Pair<String, Point>> getObjTag() {
        return this.objTag;
    }

    public Point getObjTag(String s) {
        for (Pair<String, Point> pair : this.objTag.values()) {
            if (!s.contains((CharSequence)pair.left)) continue;
            return new Point((Point)pair.right);
        }
        return new Point();
    }

    public Map<String, Point> getLacheln() {
        return this.lacheln;
    }

    public List<String> getLachelnList() {
        return new ArrayList<String>(this.lacheln.keySet());
    }

    public List<Pair<String, Point>> getSyncFH() {
        return this.syncFH;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapItem> getStealMesoObject(MapleCharacter chr, int bulletCount, int range) {
        this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            final ArrayList<MapleMapItem> list = new ArrayList<>();
            final Iterator<MapleMapObject> iterator = this.mapobjects.get(MapleMapObjectType.ITEM).values().iterator();
            while (iterator.hasNext()) {
                final MapleMapItem item;
                if ((item = (MapleMapItem) iterator.next()).getMeso() > 0 && (item.getSkill() == 暗影神偷.勇者掠奪術 || item.getSkill() == 暗影神偷.血腥掠奪術) && item.getOwnerID() == chr.getId() && (range == -1 || chr.getPosition().distance(item.getPosition()) <= range)) {
                    list.add(item);
                }
                if (bulletCount != -1 && list.size() >= bulletCount) {
                    break;
                }
            }
            return list;
        } finally {
            this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
    }
    
//    public List<MapleMapItem> getStealMesoObject(MapleCharacter chr, int bulletCount, int range) {
//        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
//        try {
//            ArrayList<MapleMapItem> list = new ArrayList<MapleMapItem>();
//
//            for (MapleMapItem arrayList : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
//                if (!(arrayList.getMeso() <= 0 || arrayList.getSkill() != 4211003 && arrayList.getSkill() != 4221018 || arrayList.getOwnerID() != chr.getId() || range != -1 && !(chr.getPosition().distance(arrayList.getPosition()) <= (double)range))) {
//                    list.add(arrayList);
//                }
//                if (bulletCount == -1 || list.size() < bulletCount) continue;
//                break;
//            }
//            ArrayList<MapleMapItem> arrayList = list;
//            return arrayList;
//        }
//        finally {
//            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
//        }
//    }

    public MapleMonster getMobObject(int objectID) {
        MapleMapObject mapObject = this.getMapObject(objectID, MapleMapObjectType.MONSTER);
        if (mapObject == null) {
            return null;
        }
        return (MapleMonster)mapObject;
    }

    public MapleRandomPortal getRandomPortalObject(int objectID) {
        MapleMapObject mapObject = this.getMapObject(objectID, MapleMapObjectType.RANDOM_PORTAL);
        if (mapObject == null) {
            return null;
        }
        return (MapleRandomPortal)mapObject;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MaplePortal getFreePortal(MapleCharacter owner) {
        ArrayList<MaplePortal> list = new ArrayList<MaplePortal>();
        for (MaplePortal portal : this.portals.values()) {
            if (portal.getType() != 6) continue;
            list.add(portal);
        }
        list.sort(Comparator.comparingInt(MaplePortal::getId));
        this.mapobjectlocks.get((Object)MapleMapObjectType.TOWN_PORTAL).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.TOWN_PORTAL).values()) {
                if (!(obj instanceof TownPortal)) continue;
                TownPortal door = (TownPortal)obj;
                if (door.getOwner() != null && door.getOwner().getParty() != null && owner != null && owner.getParty() != null && owner.getParty().getId() == door.getOwner().getParty().getId()) {
                    MaplePortal maplePortal = null;
                    return maplePortal;
                }
                list.remove(door.getTownPortal());
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.TOWN_PORTAL).readLock().unlock();
        }
        if (list.size() <= 0) {
            return null;
        }
        return (MaplePortal)list.get(Randomizer.nextInt(list.size()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapItem> getKannaSigliObject(MapleCharacter player, int size) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            ArrayList<MapleMapItem> list = new ArrayList<MapleMapItem>();
            long limit = size;
            for (MapleMapObject it : this.mapobjects.get((Object)MapleMapObjectType.ITEM).values()) {
                MapleMapItem mapleMapItem = (MapleMapItem)it;
                if (mapleMapItem.getItem() == null || mapleMapItem.getItemId() != 4033270 || mapleMapItem.getOwnerID() != player.getId() || !(player.getPosition().distance(mapleMapItem.getPosition()) <= 500.0)) continue;
                if (limit-- == 0L) break;
                list.add(mapleMapItem);
            }
            ArrayList<MapleMapItem> arrayList = list;
            return arrayList;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkMapItemExpire(long now) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).writeLock().lock();
        try {
            Iterator<MapleMapObject> iterator = this.mapobjects.get((Object)MapleMapObjectType.ITEM).values().iterator();
            while (iterator.hasNext()) {
                MapleMapItem mapItem = (MapleMapItem)iterator.next();
                if (mapItem.shouldExpire(now)) {
                    mapItem.setAnimation(mapItem.getDropMotionType() != 0 ? 6 : 0);
                    iterator.remove();
                    this.removeRangedMapObject(mapItem);
                    continue;
                }
                if (!mapItem.shouldFFA(now)) continue;
                mapItem.setOwnType((byte)2);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).writeLock().unlock();
        }
    }

    public void checkMobKill() {
        ArrayList<MapleMonster> list = new ArrayList<MapleMonster>();
        long currentTimeMillis = System.currentTimeMillis();
        for (MapleMonster monster : this.getAllMonster()) {
            if (!monster.shouldKill(currentTimeMillis)) continue;
            list.add(monster);
        }
        if (!list.isEmpty()) {
            for (MapleMonster monster : list) {
                this.killMonster(monster, null, false, false, monster.getStats().getSelfD() < 0 ? (byte)1 : monster.getStats().getSelfD(), 0);
            }
        }
    }

    public List<FieldAttackObjInfo> getFieldAttackObjInfo() {
        return this.fieldAttackObjInfo;
    }

    public List<TaggedObjRegenInfo> getTaggedObjRegenInfo() {
        return this.taggedObjRegenInfo;
    }

    public void handleMapObject() {
        if (!this.fieldAttackObjInfo.isEmpty()) {
            for (FieldAttackObjInfo objInfo : this.fieldAttackObjInfo) {
                if (System.currentTimeMillis() <= objInfo.nextHandleTime) continue;
                objInfo.nextHandleTime = System.currentTimeMillis() + (long)(objInfo.regenTime + objInfo.destroyTime) * 1000L;
                MapleFieldAttackObj obj = new MapleFieldAttackObj(objInfo.id, 0, objInfo.destroyTime * 1000);
                obj.setSide(objInfo.flip);
                obj.setPosition(this.getObjTag(objInfo.regenObjTag));
                this.createdFieldAttackObject(obj);
            }
        }
        if (!this.taggedObjRegenInfo.isEmpty()) {
            ArrayList<TaggedObjRegenInfo> list = new ArrayList<TaggedObjRegenInfo>();
            for (TaggedObjRegenInfo regenInfo : this.taggedObjRegenInfo) {
                if (regenInfo.isVisible() && System.currentTimeMillis() > regenInfo.ake) {
                    regenInfo.setVisible(false);
                    regenInfo.akd = System.currentTimeMillis() + (long)regenInfo.getRegenTime() * 1000L;
                    list.add(regenInfo);
                    continue;
                }
                TaggedObjRegenInfo a161 = regenInfo;
                if (a161.isVisible() || System.currentTimeMillis() <= a161.akd) continue;
                regenInfo.setVisible(true);
                regenInfo.ake = System.currentTimeMillis() + (long)regenInfo.getRemoveTime() * 1000L;
                list.add(regenInfo);
            }
            if (!list.isEmpty()) {
                this.broadcastMessage(MaplePacketCreator.SetMapTaggedObjectSmoothVisible(list));
            }
        }
        long timeNow = System.currentTimeMillis();
        int mapWidth = Math.abs(this.getRight() - this.getLeft());
        int mapHeight = Math.abs(this.getBottom() - this.getTop());
        for (MapleMonster monster : this.getMonsters()) {
            if (monster.getStats().isRewardSprinkle()) {
                int x = (int)monster.getPosition().getX();
                x += mapWidth - Randomizer.nextInt(2 * mapWidth);
                x = Math.min(Math.max(x, this.getLeft() + 50), this.getRight() - 50);
                int y = Math.min(Math.max(this.getTop() + monster.getRewardSprinkleCount() * (Randomizer.nextInt(mapHeight / 40) + 5), this.getTop() + 50), this.getBottom() - 50);
                this.broadcastMessage(MobPacket.mobMoveControl(monster.getObjectId(), new Point(x, y)));
            }
            if (monster.shouldDrop(timeNow)) {
                monster.doDropItem(timeNow);
                continue;
            }
            if (monster.getStats() == null || !monster.getStats().isRewardSprinkle() || monster.getRewardSprinkleCount() > 0) continue;
            monster.cancelDropItem();
        }
    }

    public Integer getCustomValue(int skillid) {
        if (this.customInfo.containsKey(skillid)) {
            return (int)this.customInfo.get(skillid).getValue();
        }
        return null;
    }

    public void removeCustomInfo(int skillid) {
        this.customInfo.remove(skillid);
    }

    public void setCustomInfo(int skillid, int value, int time) {
        if (this.getCustomValue(skillid) != null) {
            this.removeCustomInfo(skillid);
        }
        this.customInfo.put(skillid, new SkillCustomInfo((long)value, (long)time));
    }

    public int getDecHPr() {
        return this.decHPr;
    }

    public void setDecHPr(int decHPr) {
        this.decHPr = decHPr;
    }

    public List<Rectangle> getRandRect() {
        return this.randRect;
    }

    public void actionButterfly(boolean b, int n) {
    }

    public int getLimitMobID() {
        return this.limitMobID;
    }

    public void setLimitMobID(int limitMobID) {
        this.limitMobID = limitMobID;
    }

    public void forceTrigger(String reactorName, byte state) {
        for (MapleReactor reactor : this.getAllReactor()) {
            if (!reactor.getName().equalsIgnoreCase(reactorName)) continue;
            reactor.forceHitReactor(state);
        }
    }

    public void forceTriggerStateEnd(String reactorName, byte state, byte stateEnd) {
        for (MapleReactor reactor : this.getAllReactor()) {
            if (!reactor.getName().equalsIgnoreCase(reactorName)) continue;
            reactor.setStateEnd(stateEnd);
            reactor.forceHitReactor(state);
        }
    }

    public void forceTrigger(int reactorId, byte newState) {
        for (MapleReactor reactor : this.getAllReactor()) {
            if (reactor.getReactorId() != reactorId) continue;
            reactor.forceHitReactor(newState);
        }
    }

    public int getReactorStat(String s) {
        for (MapleReactor reactor : this.getAllReactor()) {
            if (!reactor.getName().equals(s)) continue;
            return reactor.getState();
        }
        return -999;
    }

    public int getEventMobSize() {
        int n = 0;
        for (MapleMonster mapleMonster : this.getAllMonster()) {
            if (mapleMonster.getStats().getType().equalsIgnoreCase("6H")) continue;
            ++n;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleCharacter> getPartyMembersInRange(Party party, Point position, int i) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            ArrayList<MapleCharacter> list = new ArrayList<MapleCharacter>();
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                assert (mmo instanceof MapleCharacter);
                MapleCharacter player = (MapleCharacter)mmo;
                if (player.getParty() == null || player.getParty().getId() != party.getId() || !(position.distance(player.getPosition()) < (double)i)) continue;
                list.add(player);
            }
            ArrayList<MapleCharacter> arrayList = list;
            return arrayList;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
    }

    public String getMapMark() {
        return this.mapMark;
    }

    public void setMapMark(String mapMark) {
        this.mapMark = mapMark;
    }

    public Map<Integer, Map<Integer, SpecialChairTW>> getSpecialChairTWs() {
        return this.specialChairTWs;
    }

    public void setSpecialChairTWs(Map<Integer, Map<Integer, SpecialChairTW>> specialChairTWs) {
        this.specialChairTWs = specialChairTWs;
    }

    public int getCreateMobInterval() {
        return this.createMobInterval;
    }

    public void setCreateMobInterval(short createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public int getIncSpawnMobR(String type) {
        return this.incSpawnMob.getOrDefault(type, 0);
    }

    public void setIncSpawnMobR(String type, int rate) {
        this.incSpawnMob.put(type, rate);
    }

    public void removeIncSpawnMobR(String type) {
        this.incSpawnMob.remove(type);
    }

    public int getEachIncSpawnMobR(String type) {
        return this.eachIncSpawnMob.getOrDefault(type, 0);
    }

    public void setEachIncSpawnMobR(String type, int rate) {
        this.eachIncSpawnMob.put(type, rate);
    }

    public void removeEachIncSpawnMobR(String type) {
        this.eachIncSpawnMob.remove(type);
    }

    public int getFieldLevel() {
        if (this.fieldLevel == -1) {
            LinkedList<Integer> lvArr = new LinkedList<Integer>();
            LinkedList<Integer> mobArr = new LinkedList<Integer>();
            for (Spawns mob : this.monsterSpawn) {
                if (mob == null || mob.getMonster() == null || mobArr.contains(mob.getMonster().getId()) || mob.getMobTime() < 0) continue;
                if (!lvArr.contains(mob.getMonster().getLevel())) {
                    lvArr.add(Integer.valueOf(mob.getMonster().getLevel()));
                }
                mobArr.add(mob.getMonster().getId());
            }
            int totalLevel = 0;
            Iterator iterator = lvArr.iterator();
            while (iterator.hasNext()) {
                int lv = (Integer)iterator.next();
                totalLevel += lv;
            }
            this.fieldLevel = mobArr.size() == 0 ? 0 : (int)Math.floor(totalLevel / mobArr.size());
        }
        return this.fieldLevel;
    }

    public int getBreakTimeFieldStep() {
        return this.breakTimeFieldStep;
    }

    public void setBreakTimeFieldStep(int step) {
        this.breakTimeFieldStep = step;
    }

    public boolean isBreakTimeField() {
        return !this.isTown() && this.getFieldLevel() != 0 && this.getFieldLevel() >= 100;
    }

    public void updateBreakTimeField() {
        int lastStep = this.breakTimeFieldStep;
        if (this.breakTimeFieldStep < 0) {
            lastStep = 0;
        }
        long eachTime = 600000L;
        long now = System.currentTimeMillis();
        if (this.breakTimeFieldLastTime == 0L) {
            int sqlBreakTimeFieldStep = -1;
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM breaktimefield WHERE `world` = ? AND `channel` = ? AND `map` = ?");){
                ps.setInt(1, 0);
                ps.setInt(2, this.channel);
                ps.setInt(3, this.mapid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    sqlBreakTimeFieldStep = rs.getInt("breakTimeFieldStep");
                }
                rs.close();
            }
            catch (SQLException ex) {
                throw new Error("讀取燃燒場地資料出錯.", ex);
            }
            this.breakTimeFieldStep = !this.isBreakTimeField() ? 0 : (sqlBreakTimeFieldStep < 0 ? ServerConfig.MAX_BREAKTIMEFIELD_STEP : sqlBreakTimeFieldStep);
        } else {
            long time;
            if (this.breakTimeFieldTime / eachTime != (long)this.breakTimeFieldStep) {
                this.breakTimeFieldTime = (long)(this.breakTimeFieldStep + 1) * eachTime - 1000L;
            }
            if ((time = now - this.breakTimeFieldLastTime) > 0L) {
                this.breakTimeFieldTime = this.isBreakTimeField() && time >= eachTime ? Math.min((long)(ServerConfig.MAX_BREAKTIMEFIELD_STEP + 1) * eachTime - 1000L, this.breakTimeFieldTime + time) : Math.max(0L, this.breakTimeFieldTime - time);
                this.breakTimeFieldStep = (int)(this.breakTimeFieldTime / eachTime);
            }
        }
        this.breakTimeFieldLastTime = now;
        if (this.breakTimeFieldStep != lastStep && this.breakTimeFieldStep >= 0) {
            this.broadcastMessage(this.getBreakTimeFieldStepPacket());
        }
    }

    public void saveBreakTimeFieldStep() {
        PreparedStatement ps;
        DruidPooledConnection con;
        if (!this.isBreakTimeField()) {
            return;
        }
        this.updateBreakTimeField();
        try {
            con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            try {
                ps = con.prepareStatement("DELETE FROM breaktimefield WHERE `world` = ? AND `channel` = ? AND `map` = ?");
                try {
                    ps.setInt(1, 0);
                    ps.setInt(2, this.channel);
                    ps.setInt(3, this.mapid);
                    ps.executeUpdate();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            finally {
                if (con != null) {
                    con.close();
                }
            }
        }
        catch (SQLException ex) {
            throw new Error("移除燃燒場地資料出錯.", ex);
        }
        if (this.breakTimeFieldStep >= 10 || this.breakTimeFieldStep < 0) {
            return;
        }
        try {
            con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            try {
                ps = con.prepareStatement("INSERT INTO breaktimefield (world, channel, map, breakTimeFieldStep) VALUES (?, ?, ?, ?)");
                try {
                    ps.setInt(1, 0);
                    ps.setInt(2, this.channel);
                    ps.setInt(3, this.mapid);
                    ps.setInt(4, this.breakTimeFieldStep);
                    ps.executeUpdate();
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
            finally {
                if (con != null) {
                    con.close();
                }
            }
        }
        catch (SQLException ex) {
            throw new Error("儲存燃燒場地資料出錯.", ex);
        }
    }

    public byte[] getBreakTimeFieldStepPacket() {
        if (this.breakTimeFieldStep <= 0) {
            return EffectPacket.showCombustionMessage("#fn哥德 ExtraBold##fs26#          消滅燃燒的範圍！！   ", 1500, -200);
        }
        return EffectPacket.showCombustionMessage(String.format("#fn哥德 ExtraBold##fs26#          燃燒%d階段 : 經驗值追加贈送 %d0%%！！   ", this.breakTimeFieldStep, this.breakTimeFieldStep), 1500, -200);
    }

    public void setDynamicObj(String sBGM, String sFrame, String sEffect) {
        this.dynamicObj = new Pair<Integer, Triple<String, String, String>>(2, new Triple<String, String, String>(sBGM, sFrame, sEffect));
        this.broadcastMessage(UIPacket.sendDynamicObj(true, this.dynamicObj));
    }

    public void setDynamicObj() {
        this.dynamicObj = new Pair<Integer, Triple<String, String, String>>(3, null);
        this.broadcastMessage(UIPacket.sendDynamicObj(true, this.dynamicObj));
    }

    public void removeDynamicObj() {
        this.dynamicObj = null;
        this.broadcastMessage(UIPacket.sendDynamicObj(false, this.dynamicObj));
    }

    public int getOwner() {
        return this.ownerId;
    }

    public void setOwner(int chrId) {
        this.ownerId = chrId;
        this.ownerStartTime = System.currentTimeMillis();
        this.broadcastMessage(EffectPacket.showCombustionMessage("#fn哥德 ExtraBold##fs26#          防搶圖已" + (chrId > -1 ? "開啟" : "解除") + "！！   ", 4000, -100));
    }

    public long getOwnerStartTime() {
        return this.ownerStartTime;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setOwner(int chrId, int point) {
        if (!this.canEnterField(chrId) || this.ownerId == chrId) {
            return false;
        }
        MapleCharacter owner = null;
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject _mmo : this.mapobjects.get((Object)MapleMapObjectType.PLAYER).values()) {
                MapleCharacter mc = (MapleCharacter)_mmo;
                if (mc.getId() == chrId) {
                    owner = mc;
                    continue;
                }
                if (mc.isIntern()) continue;
                boolean bl = false;
                return bl;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).readLock().unlock();
        }
        if (owner == null || owner.checkEvent() || owner.getCSPoints(-1) < point) {
            return false;
        }
        if (owner.getCSPoints(2) >= point) {
            owner.modifyCSPoints(2, -point);
        } else {
            owner.modifyCSPoints(2, -owner.getCSPoints(2));
            owner.modifyCSPoints(1, -(point -= owner.getCSPoints(2)));
        }
        this.setOwner(chrId);
        return true;
    }

    public boolean canEnterField(int chrId) {
        if (this.ownerId == -1) {
            return true;
        }
        return this.ownerId == chrId || System.currentTimeMillis() - this.ownerStartTime >= 1800000L;
    }

    public List<String> getAreaControls() {
        return this.areaCtrls;
    }

    public void setAreaControls(List<String> list) {
        this.areaCtrls = list;
    }

    public int getBarrier() {
        return this.barrier;
    }

    public void setBarrier(int val) {
        this.barrier = val;
    }

    public int getBarrierArc() {
        return this.barrierArc;
    }

    public void setBarrierArc(int val) {
        this.barrierArc = val;
    }

    public int getBarrierAut() {
        return this.barrierAut;
    }

    public void setBarrierAut(int val) {
        this.barrierAut = val;
    }

    public void removeSpecialChair(SpecialChair var1, int var2) {
        ArrayList<Integer> var3 = new ArrayList<Integer>();
        boolean var4 = false;
        if (var1.V() == var2) {
            var4 = true;
            for (int i : var1.vt()) {
                if (i <= 0) continue;
                var3.add(i);
            }
            var1.clear();
        } else {
            var1.oj(var2);
            var3.add(var2);
        }
        Iterator<Integer> iterator = var3.iterator();
        while (iterator.hasNext()) {
            SpecialChair var10004;
            boolean var10003;
            int integer = (Integer)iterator.next();
            MapleCharacter var10 = this.getPlayerObject(integer);
            if (var10 == null) continue;
            var10.setChair(null);
            var10.setSpecialChair(null);
            var10.send(MaplePacketCreator.UserSitResult(var10.getId(), -1));
            if (!var4) {
                var10003 = true;
                var10004 = var1;
            } else {
                var10003 = false;
                var10004 = var1;
            }
            this.broadcastMessage(MaplePacketCreator.SpecialChairSitResult(integer, false, var10003, var10004));
            this.broadcastMessage(MaplePacketCreator.UserSetActivePortableChair(var10), var10.getPosition());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeSpecialChairTW(int var1, int var2) {
        ArrayList<Integer> var3 = new ArrayList<Integer>();
        Map<Integer, Map<Integer, SpecialChairTW>> map = this.specialChairTWs;
        synchronized (map) {
            SpecialChairTW var10;
            Map<Integer, SpecialChairTW> var9 = this.specialChairTWs.get(var1);
            if (var9 != null && (var10 = var9.remove(var2)) != null) {
                MapleCharacter var31;
                for (Integer integer : var10.vu().keySet()) {
                    var3.add(integer);
                    var31 = this.getPlayerObject(integer);
                    if (var31 == null) continue;
                    var31.setChair(null);
                    var31.setSpecialChairTW(null);
                    var31.getClient().announce(MaplePacketCreator.UserSitResult(integer, -1));
                    this.broadcastMessage(MaplePacketCreator.UserSetActivePortableChair(var31), var31.getPosition());
                    this.broadcastMessage(MaplePacketCreator.SpecialChairTWRemove(1, integer, 0));
                    this.broadcastMessage(MaplePacketCreator.SpecialChairSitResult(integer, false, false, null));
                }
                for (Integer var13 : var10.vv().keySet()) {
                    var3.add(var13);
                    var31 = this.getPlayerObject(var13);
                    if (var31 == null) continue;
                    var31.setChair(null);
                    var31.setSpecialChairTW(null);
                    var31.getClient().announce(MaplePacketCreator.UserSitResult(var13, -1));
                    this.broadcastMessage(MaplePacketCreator.UserSetActivePortableChair(var31), var31.getPosition());
                    this.broadcastMessage(MaplePacketCreator.SpecialChairTWRemove(1, var13, 0));
                    this.broadcastMessage(MaplePacketCreator.SpecialChairSitResult(var13, false, false, null));
                }
                var10.clear();
            }
        }
        this.broadcastMessage(MaplePacketCreator.SpecialChairTWSitResult(0, this.specialChairTWs, var3));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeSpecialChairTW(int var1, int var2, int var3) {
        Map<Integer, Map<Integer, SpecialChairTW>> map = this.specialChairTWs;
        synchronized (map) {
            SpecialChairTW var8;
            Map<Integer, SpecialChairTW> var7 = this.specialChairTWs.get(var1);
            if (var7 != null && (var8 = var7.get(var2)) != null) {
                MapleCharacter var9;
                if (var8.vv().containsKey(var3)) {
                    var8.om(var3);
                }
                if (var8.vu().containsKey(var3)) {
                    var8.oj(var3);
                }
                if ((var9 = this.getPlayerObject(var3)) != null) {
                    var9.setChair(null);
                    var9.setSpecialChairTW(null);
                    var9.getClient().announce(MaplePacketCreator.UserSitResult(var9.getId(), -1));
                    this.broadcastMessage(MaplePacketCreator.UserSetActivePortableChair(var9), var9.getPosition());
                    this.broadcastMessage(MaplePacketCreator.SpecialChairSitResult(var3, false, false, null));
                    this.broadcastMessage(MaplePacketCreator.SpecialChairTWRemove(1, var9.getId(), 0));
                }
            }
        }
        this.broadcastMessage(MaplePacketCreator.SpecialChairTWSitResult(0, this.specialChairTWs, Collections.singletonList(var3)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void specialChair$C(int var1, int var2, int var3) {
        Map<Integer, Map<Integer, SpecialChairTW>> map = this.specialChairTWs;
        synchronized (map) {
            SpecialChairTW var8;
            Map<Integer, SpecialChairTW> var7 = this.specialChairTWs.get(var1);
            if (var7 != null && (var8 = var7.get(var2)) != null && var8.ok(var3)) {
                this.broadcastMessage(MaplePacketCreator.SpecialChairTWInviteResult(1, var3, 1));
                this.broadcastMessage(MaplePacketCreator.SpecialChairTWSitResult(0, this.specialChairTWs, Collections.emptyList()));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void specialChair$D(int var1, int var2, int var3) {
        Map<Integer, Map<Integer, SpecialChairTW>> map = this.specialChairTWs;
        synchronized (map) {
            SpecialChairTW var8;
            Map<Integer, SpecialChairTW> var7 = this.specialChairTWs.get(var1);
            if (var7 != null && (var8 = var7.get(var2)) != null) {
                var8.ol(var3);
            }
        }
        this.broadcastMessage(MaplePacketCreator.SpecialChairTWSitResult(0, this.specialChairTWs, Collections.emptyList()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void specialChair$b(SpecialChairTW var1) {
        Map<Integer, Map<Integer, SpecialChairTW>> map = this.specialChairTWs;
        synchronized (map) {
            Map<Integer, SpecialChairTW> var3 = this.specialChairTWs.get(var1.getItemId());
            if (var3 == null) {
                var3 = new HashMap<Integer, SpecialChairTW>();
                this.specialChairTWs.put(var1.getItemId(), var3);
            }
            var3.put(var1.V(), var1);
        }
        this.broadcastMessage(MaplePacketCreator.SpecialChairTWSitResult(0, this.specialChairTWs, Collections.EMPTY_LIST));
    }

    public ScriptEvent getEvent() {
        return this.event;
    }

    public void setEvent(ScriptEvent eim) {
        this.event = eim;
    }

    public boolean getSpawns() {
        return this.isSpawns;
    }

    public void setSpawns(boolean b) {
        this.isSpawns = b;
        if (b) {
            this.lastSpawnTime = System.currentTimeMillis();
        }
    }

    public int getAreaBroadcastMobId() {
        return this.areaBroadcastMobId;
    }

    public void setAreaBroadcastMobId(int id) {
        this.areaBroadcastMobId = id;
    }

    public void startAreaBroadcastMob(int id) {
        this.setAreaBroadcastMobId(id);
        this.broadcastAreaMob(0);
    }

    public void stopAreaBroadcastMob() {
        this.setAreaBroadcastMobId(-1);
        this.broadcastAreaMob(1);
    }

    public void broadcastAreaMob(int mode) {
        ChannelServer ch = ChannelServer.getInstance(this.channel);
        if (ch != null) {
            ch.broadcastMapAreaMessage(this.mapid / 10000000, ByteBuffer.wrap(MaplePacketCreator.bossMessage(mode, this.mapid, this.areaBroadcastMobId)));
        }
    }

    public String spawnDebug() {
        String sb = "Mobs in map : " + this.getMobsSize() + " spawnedMonstersOnMap: " + String.valueOf(this.spawnedMonstersOnMap) + " spawnpoints: " + this.monsterSpawn.size() + " maxRegularSpawn: " + this.maxRegularSpawn + " actual monsters: " + this.getMobSizeByID() + " monster rate: " + this.monsterRate + " fixed: " + this.fixedMob + " isSpawns: " + this.isSpawns + " createMobInterval: " + this.createMobInterval + " decMobIntervalR: " + this.decMobIntervalR;
        return sb;
    }

    private ScriptManager getScriptManager() {
        return this.scriptManager;
    }

    public void setScriptManager(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    public String getFieldScript() {
        return this.fieldScript;
    }

    public void setFieldScript(String fieldScript) {
        this.fieldScript = fieldScript;
    }

    public void startFieldScript() {
        this.startScript(this.getFieldScript());
    }

    public void startScript(String script) {
        if (!"".equalsIgnoreCase(script)) {
            if (this.getScriptManager() == null) {
                this.setScriptManager(new ScriptManager(this));
            }
            log.debug(String.format("Starting field Plugin.script %s.", script));
            this.scriptManager.startScript(this.getId(), script, ScriptType.Map);
        }
    }

    public final void spawnMonsterWithEffectBelow(MapleMonster mob, Point pos, int effect) {
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        this.spawnMonsterWithEffect(mob, effect, spos);
    }

    public void killMonster(MapleMonster monster, int effect) {
        if (monster == null) {
            return;
        }
        monster.setHp(0L);
        if (monster.getLinkCID() <= 0 && !GameConstants.isContentsMap(this.getId())) {
            monster.spawnRevives();
        }
        if (this.RealSpawns.contains(monster)) {
            this.RealSpawns.remove(monster);
        }
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), effect));
        this.removeMapObject(monster);
        this.spawnedMonstersOnMap.decrementAndGet();
        monster.killed();
    }

    public void killMonster(MapleMonster monster) {
        if (monster == null) {
            return;
        }
        monster.setHp(0L);
        this.RealSpawns.remove(monster);
        if (monster.getLinkCID() <= 0 && !GameConstants.isContentsMap(this.getId())) {
            monster.spawnRevives();
        }
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), monster.getStats().getSelfD() < 0 ? 1 : (int)monster.getStats().getSelfD()));
        this.removeMapObject(monster);
        this.spawnedMonstersOnMap.decrementAndGet();
        monster.killed();
    }

    public final void killMonsterType(MapleMonster mob, int type) {
        if (mob != null && mob.isAlive()) {
            if (this.RealSpawns.contains(mob)) {
                this.RealSpawns.remove(mob);
            }
            this.removeMapObject(mob);
            mob.killed();
            this.spawnedMonstersOnMap.decrementAndGet();
            this.broadcastMessage(MobPacket.killMonster(mob.getObjectId(), type));
            this.broadcastMessage(MobPacket.stopControllingMonster(mob));
        }
    }

    public List<MapleMonster> getRealSpawns() {
        return this.RealSpawns;
    }

    public void setRealSpawns(List<MapleMonster> RealSpawns) {
        this.RealSpawns = RealSpawns;
    }

    public final byte[] spawnMonster_sSack(MapleMonster mob, Point pos, int spawnType) {
        mob.setPosition(this.calcPointBelow(new Point(pos.x, pos.y - 1)) == null ? new Point(pos.x, pos.y) : this.calcPointBelow(new Point(pos.x, pos.y - 1)));
        this.spawnMonster(mob, spawnType);
        return new byte[0];
    }

    public void obstacleFall(int count, int type, int type2) {
        this.broadcastMessage(MaplePacketCreator.createObtacleAtom(count, type, type2, this));
    }

    public void BossLatusObstacleFall() {
        Latus.CreatAtomAttack_chaos((MapleMap)this);
    }

    public void spawnMonsterOnGroundBelowBlackMage(MapleMonster mob, Point pos) {
        mob.setFh(3);
        mob.setF(3);
        mob.getMap().broadcastMessage(this.spawnMonster_sSack(mob, pos, mob.getId() == 8880512 ? 1 : -2));
    }

    public void removeMist(MapleMonster mist) {
        if (this.getMapObject(mist.getObjectId(), MapleMapObjectType.MIST) != null) {
            this.broadcastMessage(CField.removeMist((MapleMonster)mist));
            this.removeMapObject(mist);
        }
    }

    public void spawnSpiderWeb(spider web) {
        spawnAndAddRangedMapObject(web, new DelayedPacketCreation() {
            public void sendPackets(MapleClient c) {
                web.sendSpawnData(c);
            }
        });    }

    public String toString() {
        return "'" + this.getStreetName() + " : " + this.getMapName() + "'(" + this.getId() + ")";
    }

    public void startFieldEvent() {
        for (MapleCharacter chr : this.getCharacters()) {
            switch (chr.getMapId()) {
                case 410002120: {
                    Seren.start((MapleCharacter)chr, (MapleMonster)((MapleMonster)chr.getMap().getAllMonster().getFirst()));
                    break;
                }
                case 410002160: {
                    Seren.startField2((MapleCharacter)chr, (MapleMonster)((MapleMonster)chr.getMap().getAllMonster().getFirst()));
                    Seren.initDayUI((MapleCharacter)chr);
                    break;
                }
                case 160040000: {
                    Angel.start((MapleCharacter)chr);
                    break;
                }
                case 350160100: {
                    Demian.start((MapleCharacter)chr);
                    break;
                }
                case 450009400: 
                case 450009450: {
                    Dusk.start((MapleCharacter)chr);
                    break;
                }
                case 450004100: 
                case 450004150: {
                    Lucid.start((MapleCharacter)chr);
                    break;
                }
                case 450004200: 
                case 450004250: {
                    Lucid.startField2((MapleCharacter)chr);
                    break;
                }
                case 450013100: {
                    BlackMage.start((MapleCharacter)chr);
                    break;
                }
                case 450013300: {
                    BlackMage.start2((MapleCharacter)chr);
                    break;
                }
                case 450013500: {
                    BlackMage.start3((MapleCharacter)chr);
                    break;
                }
                case 450013700: {
                    BlackMage.start4((MapleCharacter)chr);
                    break;
                }
                case 450008150: {
                    Will.start((MapleCharacter)chr);
                    break;
                }
                case 410007140: 
                case 410007180: 
                case 410007220: {
                    Caning.startEvent_Field((MapleCharacter)chr);
                    break;
                }
                case 450010930: {
                    JinHillah.start((MapleCharacter)chr);
                    break;
                }
                case 450012210: {
                    Dunkel.start((MapleCharacter)chr);
                    break;
                }
                case 410006020: {
                    kalos.start((MapleCharacter)chr);
                    break;
                }
            }
        }
    }

    public void endFieldEvent() {
        this.timerInstance.shutdownNow();
    }

    public void 史烏地圖機制() {
        this.timerInstance.scheduleAtFixedRate(() -> {
            for (MapleCharacter chr : this.getCharacters()) {
                chr.dropSpouseMessage(UserChatMessageType.getByType(1), "你在史烏地圖中 - 機制A - " + chr.getMap().getInstanceId());
            }
        }, 0L, 10000L, TimeUnit.MILLISECONDS);
        this.timerInstance.scheduleAtFixedRate(() -> {
            for (MapleCharacter chr : this.getCharacters()) {
                chr.dropSpouseMessage(UserChatMessageType.getByType(1), "你在史烏地圖中 - 機制B - " + chr.getMap().getInstanceId());
            }
        }, 0L, 10000L, TimeUnit.MILLISECONDS);
    }

    public List<Point> getLucidDream() {
        return this.LucidDream;
    }

    public void setLucidDream(List<Point> LucidDream) {
        this.LucidDream = LucidDream;
    }

    @Generated
    public boolean isUserFirstEnter() {
        return this.userFirstEnter;
    }

    @Generated
    public void setUserFirstEnter(boolean userFirstEnter) {
        this.userFirstEnter = userFirstEnter;
    }

    @Generated
    public Map<String, ScheduledFuture<?>> getTimers() {
        return this.timers;
    }

    @Generated
    public ScheduledExecutorService getTimerInstance() {
        return this.timerInstance;
    }

    @Generated
    public void setTimerInstance(ScheduledExecutorService timerInstance) {
        this.timerInstance = timerInstance;
    }

    private static interface DelayedPacketCreation {
        public void sendPackets(MapleClient var1);
    }
}

