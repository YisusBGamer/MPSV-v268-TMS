/*
 * Decompiled with CFR 0.152.
 */
package Client.skills;

import Client.MapleJob;
import Client.skills.Skill;
import Client.skills.SummonSkillEntry;
import Client.status.MonsterStatus;
import Config.configs.Config;
import Config.configs.ServerConfig;
import Config.constants.JobConstants;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.server.InitializeServer;
import Net.server.buffs.MapleStatEffect;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.awt.Point;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;
import tools.StringUtil;
import tools.Triple;

public class SkillFactory {
    private static final Logger log = LoggerFactory.getLogger(SkillFactory.class);
    private static final MapleData delayData = MapleDataProviderFactory.getCharacter().getData("00002000.img");
    private static final MapleData stringData = MapleDataProviderFactory.getString().getData("Skill.img");
    private static final MapleDataProvider datasource = MapleDataProviderFactory.getSkill();
    private static final Map<Integer, Skill> skills = new HashMap<Integer, Skill>();
    private static final Map<Integer, CraftingEntry> craftings = new HashMap<Integer, CraftingEntry>();
    private static final List<Integer> finalAttackSkills = new LinkedList<Integer>();
    private static final Map<String, Integer> delays = new HashMap<String, Integer>();
    private static final Map<Integer, String> skillName = new HashMap<Integer, String>();
    private static final Map<Integer, SummonSkillEntry> summonSkillInformation = new HashMap<Integer, SummonSkillEntry>();
    private static final Map<Integer, Integer> mountIds = new HashMap<Integer, Integer>();
    private static final Map<Integer, FamiliarEntry> familiarInformation = new HashMap<Integer, FamiliarEntry>();
    private static final Map<Integer, List<Integer>> skillsByJob = new HashMap<Integer, List<Integer>>();
    private static final Lock nameStringLock = new ReentrantLock();
    public static final Map<Integer, Integer> memorySkills = new HashMap<Integer, Integer>();

    public static void loadDelays() {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            if (InitializeServer.WzSqlName.wz_delays.check(con)) {
                SqlTool.queryAndGetList(con, "SELECT * FROM `wz_delays`", rs -> delays.put(rs.getString("name"), rs.getInt("del")));
            } else {
                InitializeServer.WzSqlName.wz_delays.drop(con);
                SqlTool.update(con, "CREATE TABLE `wz_delays` (`id` int(11) NOT NULL AUTO_INCREMENT,`name` text NOT NULL,`del` int(11) NOT NULL,PRIMARY KEY (`id`))");
                int del = 0;
                for (MapleData delay : delayData) {
                    if (delay.getName().equals("info")) continue;
                    String name = delay.getName();
                    delays.put(delay.getName(), del++);
                    SqlTool.update(con, "INSERT INTO `wz_delays` (`name`,`del`) VALUES (?, ?)", name, del);
                }
                InitializeServer.WzSqlName.wz_delays.update(con);
            }
            return null;
        });
    }

    public static void loadSkillData() {
        MapleDataDirectoryEntry root = datasource.getRoot();
        ExecutorService initExecutor = Executors.newCachedThreadPool();
        ArrayList<Future> futures = new ArrayList();
        for (MapleDataFileEntry topDir : root.getFiles()) {
            futures.add(initExecutor.submit(() -> {
                block21: {
                    block22: {
                        block20: {
                            if (topDir.getName().length() > 10) break block20;
                            for (MapleData data : datasource.getData(topDir.getName())) {
                                if (!data.getName().equals("skill")) continue;
                                for (MapleData data2 : data) {
                                    if (data2 == null) continue;
                                    Integer skillid = Integer.parseInt(data2.getName());
                                    List job = skillsByJob.computeIfAbsent(skillid / 10000, k -> new ArrayList());
                                    job.add(skillid);
                                    String name = SkillFactory.getName(skillid, stringData);
                                    skillName.put(skillid, name);
                                    Skill skil = Skill.loadFromData(skillid, data2, delayData);
                                    skills.put(skillid, skil);
                                    MapleData summon_data = null;
                                    if (skillid == 400011065 || skillid == 351110002 || skillid == 42100010 || skillid == 400041038) {
                                        summon_data = data2.getChildByPath("summon/die/info");
                                    }
                                    if (summon_data == null) {
                                        summon_data = data2.getChildByPath("summon/attack2/info");
                                    }
                                    if (summon_data == null) {
                                        summon_data = data2.getChildByPath("summon/attack1/info");
                                    }
                                    if (summon_data != null) {
                                        SummonSkillEntry sse = new SummonSkillEntry();
                                        sse.type = (byte)MapleDataTool.getInt("type", summon_data, 0);
                                        sse.mobCount = (byte)MapleDataTool.getInt("mobCount", summon_data, 1);
                                        sse.attackCount = (byte)MapleDataTool.getInt("attackCount", summon_data, 1);
                                        sse.targetPlus = (byte)MapleDataTool.getInt("targetPlus", summon_data, 1);
                                        if (summon_data.getChildByPath("range/lt") != null) {
                                            MapleData ltd = summon_data.getChildByPath("range/lt");
                                            sse.lt = (Point)ltd.getData();
                                            sse.rb = (Point)summon_data.getChildByPath("range/rb").getData();
                                        } else {
                                            sse.lt = new Point(-100, -100);
                                            sse.rb = new Point(100, 100);
                                        }
                                        for (MapleData effect : summon_data) {
                                            if (effect.getChildren().size() <= 0) continue;
                                            Iterator iterator = effect.iterator();
                                            while (iterator.hasNext()) {
                                                MapleData effectEntry = (MapleData)iterator.next();
                                                sse.delay += MapleDataTool.getIntConvert("delay", effectEntry, 0);
                                            }
                                        }
                                        MapleData childByPath = data2.getChildByPath("summon/attack1");
                                        if (childByPath != null) {
                                            for (MapleData effect : childByPath) {
                                                sse.delay += MapleDataTool.getIntConvert("delay", effect, 0);
                                            }
                                        }
                                        summonSkillInformation.put(skillid, sse);
                                    }
                                    for (MapleData data3 : data2) {
                                        if (!data3.getName().equals("vehicleID")) continue;
                                        int vehicleID = MapleDataTool.getInt("vehicleID", data2, 0);
                                        mountIds.put(skillid, vehicleID);
                                    }
                                }
                            }
                            break block21;
                        }
                        if (!topDir.getName().startsWith("Familiar")) break block22;
                        for (MapleData data : datasource.getData(topDir.getName())) {
                            FamiliarEntry skil = new FamiliarEntry();
                            skil.prop = (byte)MapleDataTool.getInt("prop", data, 0);
                            skil.time = (byte)MapleDataTool.getInt("time", data, 0);
                            skil.attackCount = (byte)MapleDataTool.getInt("attackCount", data, 1);
                            skil.targetCount = (byte)MapleDataTool.getInt("targetCount", data, 1);
                            skil.speed = (byte)MapleDataTool.getInt("speed", data, 1);
                            boolean bl = skil.knockback = MapleDataTool.getInt("knockback", data, 0) > 0 || MapleDataTool.getInt("attract", data, 0) > 0;
                            if (data.getChildByPath("lt") != null) {
                                skil.lt = (Point)data.getChildByPath("lt").getData();
                                skil.rb = (Point)data.getChildByPath("rb").getData();
                            }
                            if (MapleDataTool.getInt("stun", data, 0) > 0) {
                                skil.status.add(MonsterStatus.Stun);
                            }
                            if (MapleDataTool.getInt("slow", data, 0) > 0) {
                                skil.status.add(MonsterStatus.Speed);
                            }
                            int familiarid = Integer.parseInt(data.getName());
                            familiarInformation.put(familiarid, skil);
                        }
                        break block21;
                    }
                    if (!topDir.getName().startsWith("Recipe")) break block21;
                    for (MapleData data : datasource.getData(topDir.getName())) {
                        Integer skillid = Integer.parseInt(data.getName());
                        CraftingEntry skil = new CraftingEntry(skillid, (byte)MapleDataTool.getInt("incFatigability", data, 0), (byte)MapleDataTool.getInt("reqSkillLevel", data, 0), (byte)MapleDataTool.getInt("incSkillProficiency", data, 0), MapleDataTool.getInt("needOpenItem", data, 0) > 0, MapleDataTool.getInt("period", data, 0));
                        skil.setRecipe(true);
                        for (MapleData d : data.getChildByPath("target")) {
                            skil.targetItems.add(new Triple<Integer, Integer, Integer>(MapleDataTool.getInt("item", d, 0), MapleDataTool.getInt("count", d, 0), MapleDataTool.getInt("probWeight", d, 0)));
                        }
                        for (MapleData d : data.getChildByPath("recipe")) {
                            skil.reqItems.put(MapleDataTool.getInt("item", d, 0), MapleDataTool.getInt("count", d, 0));
                        }
                        craftings.put(skillid, skil);
                    }
                }
            }));
        }
        Collections.reverse(finalAttackSkills);
        for (Future future : futures) {
            future.getClass();
        }
    }

    public static void reloadSkills(int skillid) {
        String path = skillid / 10000 + ".img";
        if (skillid >= 80000000 && skillid < 90000000) {
            path = skillid / 100 + ".img";
        }
        if (datasource.getData(path) != null) {
            Skill skil = Skill.loadFromData(skillid, datasource.getData(path).getChildByPath("skill").getChildByPath(String.valueOf(skillid)), delayData);
            skills.put(skillid, skil);
        }
    }

    public static void loadMemorySkills() {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM memoryskills");
             ResultSet rs = ps.executeQuery();){
            while (rs.next()) {
                int skillId = rs.getInt("skillid");
                Skill skill = SkillFactory.getSkill(skillId);
                if (memorySkills.containsKey(skillId) || skill == null || skill.getSkillByJobBook(skillId) == -1) continue;
                memorySkills.put(skillId, skill.getSkillByJobBook(skillId));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getSkillDefaultData(int skillid, String name) {
        MapleData skill;
        MapleData skillData = datasource.getData(skillid / 10000 + ".img");
        if (skillData != null && (skill = skillData.getChildByPath("skill").getChildByPath(String.valueOf(skillid)).getChildByPath("common")) != null) {
            for (MapleData data : skill) {
                if (!data.getName().equals(name)) continue;
                return String.valueOf(data.getData());
            }
        }
        return null;
    }

    public static int getIdFromSkillId(int skillId) {
        return memorySkills.getOrDefault(skillId, 0);
    }

    public static boolean isMemorySkill(int skillId) {
        return memorySkills.containsKey(skillId);
    }

    public static List<Integer> getSkillsByJob(int jobId) {
        List<Integer> ret = null;
        try {
            ret = skillsByJob.get(jobId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static List<Integer> getSkillsByLowerJob(int jobId) {
        ArrayList<Integer> ret = null;
        for (MapleJob mj : MapleJob.values()) {
            if (JobConstants.is零轉職業(mj.getId()) || !JobConstants.isSameJob(mj.getId(), jobId) || mj.getId() > jobId) continue;
            List<Integer> skills = SkillFactory.getSkillsByJob(mj.getId());
            if (ret == null && skills != null) {
                ret = new ArrayList<Integer>();
            }
            if (skills == null) continue;
            ret.addAll(skills);
        }
        return ret;
    }

    public static List<Integer> getSkillsBySameJob(int jobId) {
        ArrayList<Integer> ret = null;
        for (MapleJob mj : MapleJob.values()) {
            if (JobConstants.is零轉職業(mj.getId()) || !JobConstants.isSameJob(mj.getId(), jobId)) continue;
            List<Integer> skills = SkillFactory.getSkillsByJob(mj.getId());
            if (ret == null && skills != null) {
                ret = new ArrayList<Integer>();
            }
            ret.addAll(skills);
        }
        return ret;
    }

    public static String getSkillName(int id) {
        return skillName.get(id);
    }

    public static Integer getDelay(String id) {
        Delay delay = Delay.fromString(id);
        if (delay != null) {
            return delay.i;
        }
        return delays.get(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String getName(int id, MapleData stringData) {
        String strId = Integer.toString(id);
        strId = StringUtil.getLeftPaddedStr(strId, '0', 7);
        nameStringLock.lock();
        try {
            MapleData skillroot = stringData.getChildByPath(strId);
            if (skillroot != null) {
                String string = MapleDataTool.getString(skillroot.getChildByPath("name"), "");
                return string;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            nameStringLock.unlock();
        }
        return "";
    }

    public static String getDesc(int id) {
        String strId = Integer.toString(id);
        MapleData skillroot = stringData.getChildByPath(strId = StringUtil.getLeftPaddedStr(strId, '0', 7));
        if (skillroot != null) {
            return MapleDataTool.getString(skillroot.getChildByPath("desc"), "");
        }
        return "";
    }

    public static String getH(int id) {
        String strId = Integer.toString(id);
        MapleData skillroot = stringData.getChildByPath(strId = StringUtil.getLeftPaddedStr(strId, '0', 7));
        if (skillroot != null) {
            return MapleDataTool.getString(skillroot.getChildByPath("h"), "");
        }
        return "";
    }

    public static SummonSkillEntry getSummonData(int skillid) {
        return summonSkillInformation.get(skillid);
    }

    public static Map<Integer, String> getAllSkills() {
        return skillName;
    }

    public static Skill getSkill(int skillid) {
        Skill skill = null;
        if (skillid >= 92000000 && skillid < 100000000 && craftings.containsKey(skillid)) {
            skill = craftings.get(skillid);
        }
        if (skill == null) {
            skill = skills.get(skillid);
        }
        return skill;
    }

    public static MapleStatEffect getSkillEffect(int skillId, int level) {
        Skill skill = SkillFactory.getSkill(skillId);
        return skill == null ? null : skill.getEffect(level);
    }

    public static long getDefaultSExpiry(Skill skill) {
        if (skill == null) {
            return -1L;
        }
        return skill.isTimeLimited() ? System.currentTimeMillis() + 2592000000L : -1L;
    }

    public static CraftingEntry getCraft(int id) {
        CraftingEntry ret = null;
        try {
            ret = craftings.get(id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static FamiliarEntry getFamiliar(int id) {
        return familiarInformation.getOrDefault(id, null);
    }

    public static boolean isBlockedSkill(int skillId) {
        return skillId > 0 && Arrays.stream(ServerConfig.WORLD_BLOCKSKILLS.split(",")).anyMatch(it -> it.equals(String.valueOf(skillId)));
    }

    public static boolean addBlockedSkill(int skillId) {
        if (SkillFactory.isBlockedSkill(skillId)) {
            return false;
        }
        ServerConfig.WORLD_BLOCKSKILLS = ServerConfig.WORLD_BLOCKSKILLS + "," + skillId;
        Config.setProperty("world.blockskills", ServerConfig.WORLD_BLOCKSKILLS);
        return true;
    }

    public static int getTeachSkill(String name) {
        for (Map.Entry<Integer, String> entry : SkillFactory.getAllSkills().entrySet()) {
            try {
                if (entry.getValue() == null || !entry.getValue().endsWith(name) || entry.getKey() < 80000000 || entry.getKey() >= 90000000) continue;
                return entry.getKey();
            }
            catch (Exception e) {
                System.out.println(entry.getKey());
            }
        }
        return -1;
    }

    public static int getMountLinkId(int mountid) {
        return mountIds.getOrDefault(mountid, 0);
    }

    public static List<Integer> getFinalAttackSkills() {
        return finalAttackSkills;
    }

    public static boolean isFinalAttackSkills(Integer skillid) {
        return finalAttackSkills.contains(skillid);
    }

    public static enum Delay {
        walk1(0),
        walk2(1),
        stand1(2),
        stand2(3),
        alert(4),
        swingO1(5),
        swingO2(6),
        swingO3(7),
        swingOF(8),
        swingT1(9),
        swingT2(10),
        swingT3(11),
        swingTF(12),
        swingP1(13),
        swingP2(14),
        swingPF(15),
        stabO1(16),
        stabO2(17),
        stabOF(18),
        stabT1(19),
        stabT2(20),
        stabTF(21),
        swingD1(22),
        swingD2(23),
        stabD1(24),
        swingDb1(25),
        swingDb2(26),
        swingC1(27),
        swingC2(28),
        rushBoom(28),
        tripleBlow(25),
        quadBlow(26),
        deathBlow(27),
        finishBlow(28),
        finishAttack(29),
        finishlink(30),
        finishlink2(30),
        shoot1(31),
        shoot2(32),
        shootF(33),
        shootDb2(40),
        shotC1(41),
        dash(37),
        dash2(38),
        proneStab(41),
        prone(42),
        heal(43),
        fly(44),
        jump(45),
        sit(46),
        rope(47),
        dead(48),
        ladder(49),
        rain(50),
        alert2(52),
        alert3(53),
        alert4(54),
        alert5(55),
        alert6(56),
        alert7(57),
        ladder2(58),
        rope2(59),
        shoot6(60),
        magic1(61),
        magic2(62),
        magic3(63),
        magic5(64),
        magic6(65),
        explosion(65),
        burster1(66),
        burster2(67),
        savage(68),
        avenger(69),
        assaulter(70),
        prone2(71),
        assassination(72),
        assassinationS(73),
        tornadoDash(76),
        tornadoDashStop(76),
        tornadoRush(76),
        rush(77),
        rush2(78),
        brandish1(79),
        brandish2(80),
        braveSlash(81),
        braveslash1(81),
        braveslash2(81),
        braveslash3(81),
        braveslash4(81),
        darkImpale(97),
        sanctuary(82),
        meteor(83),
        paralyze(84),
        blizzard(85),
        genesis(86),
        blast(88),
        smokeshell(89),
        showdown(90),
        ninjastorm(91),
        chainlightning(92),
        holyshield(93),
        resurrection(94),
        somersault(95),
        straight(96),
        eburster(97),
        backspin(98),
        eorb(99),
        screw(100),
        doubleupper(101),
        dragonstrike(102),
        doublefire(103),
        triplefire(104),
        fake(105),
        airstrike(106),
        edrain(107),
        octopus(108),
        backstep(109),
        shot(110),
        rapidfire(110),
        fireburner(112),
        coolingeffect(113),
        fist(114),
        timeleap(115),
        homing(117),
        ghostwalk(118),
        ghoststand(119),
        ghostjump(120),
        ghostproneStab(121),
        ghostladder(122),
        ghostrope(123),
        ghostfly(124),
        ghostsit(125),
        cannon(126),
        torpedo(127),
        darksight(128),
        bamboo(129),
        pyramid(130),
        wave(131),
        blade(132),
        souldriver(133),
        firestrike(134),
        flamegear(135),
        stormbreak(136),
        vampire(137),
        swingT2PoleArm(139),
        swingP1PoleArm(140),
        swingP2PoleArm(141),
        doubleSwing(142),
        tripleSwing(143),
        fullSwingDouble(144),
        fullSwingTriple(145),
        overSwingDouble(146),
        overSwingTriple(147),
        rollingSpin(148),
        comboSmash(149),
        comboFenrir(150),
        comboTempest(151),
        finalCharge(152),
        finalBlow(154),
        finalToss(155),
        magicmissile(156),
        lightningBolt(157),
        dragonBreathe(158),
        breathe_prepare(159),
        dragonIceBreathe(160),
        icebreathe_prepare(161),
        blaze(162),
        fireCircle(163),
        illusion(164),
        magicFlare(165),
        elementalReset(166),
        magicRegistance(167),
        magicBooster(168),
        magicShield(169),
        recoveryAura(170),
        flameWheel(171),
        killingWing(172),
        OnixBlessing(173),
        Earthquake(174),
        soulStone(175),
        dragonThrust(176),
        ghostLettering(177),
        darkFog(178),
        slow(179),
        mapleHero(180),
        Awakening(181),
        flyingAssaulter(182),
        tripleStab(183),
        fatalBlow(184),
        slashStorm1(185),
        slashStorm2(186),
        bloodyStorm(187),
        flashBang(188),
        upperStab(189),
        bladeFury(190),
        chainPull(192),
        chainAttack(192),
        owlDead(193),
        monsterBombPrepare(195),
        monsterBombThrow(195),
        finalCut(196),
        finalCutPrepare(196),
        suddenRaid(198),
        fly2(199),
        fly2Move(200),
        fly2Skill(201),
        knockback(202),
        rbooster_pre(206),
        rbooster(206),
        rbooster_after(206),
        crossRoad(209),
        nemesis(210),
        tank(217),
        tank_laser(221),
        siege_pre(223),
        tank_siegepre(223),
        sonicBoom(226),
        darkLightning(228),
        darkChain(229),
        cyclone_pre(0),
        cyclone(0),
        glacialchain(247),
        flamethrower(233),
        flamethrower_pre(233),
        flamethrower2(234),
        flamethrower_pre2(234),
        gatlingshot(239),
        gatlingshot2(240),
        drillrush(241),
        earthslug(242),
        rpunch(243),
        clawCut(244),
        swallow(247),
        swallow_attack(247),
        swallow_loop(247),
        flashRain(249),
        OnixProtection(264),
        OnixWill(265),
        phantomBlow(266),
        comboJudgement(267),
        arrowRain(268),
        arrowEruption(269),
        iceStrike(270),
        swingT2Giant(273),
        cannonJump(295),
        swiftShot(296),
        giganticBackstep(298),
        mistEruption(299),
        cannonSmash(300),
        cannonSlam(301),
        flamesplash(302),
        noiseWave(306),
        superCannon(310),
        jShot(312),
        demonSlasher(313),
        bombExplosion(314),
        cannonSpike(315),
        speedDualShot(316),
        strikeDual(317),
        bluntSmash(319),
        crossPiercing(320),
        piercing(321),
        elfTornado(323),
        immolation(324),
        multiSniping(327),
        windEffect(328),
        elfrush(329),
        elfrush2(329),
        dealingRush(334),
        maxForce0(336),
        maxForce1(337),
        maxForce2(338),
        maxForce3(339),
        iceAttack1(274),
        iceAttack2(275),
        iceSmash(276),
        iceTempest(277),
        iceChop(278),
        icePanic(279),
        iceDoubleJump(280),
        shockwave(292),
        demolition(293),
        snatch(294),
        windspear(295),
        windshot(296);

        public final int i;

        private Delay(int i) {
            this.i = i;
        }

        public static Delay fromString(String s) {
            for (Delay b : Delay.values()) {
                if (!b.name().equalsIgnoreCase(s)) continue;
                return b;
            }
            return null;
        }
    }

    public static class CraftingEntry
    extends Skill {
        public final List<Triple<Integer, Integer, Integer>> targetItems = new ArrayList<Triple<Integer, Integer, Integer>>();
        public final Map<Integer, Integer> reqItems = new HashMap<Integer, Integer>();
        public boolean needOpenItem;
        public int period;
        public byte incFatigability;
        public byte reqSkillLevel;
        public byte incSkillProficiency;

        public CraftingEntry() {
        }

        public CraftingEntry(int id, byte incFatigability, byte reqSkillLevel, byte incSkillProficiency, boolean needOpenItem, int period) {
            super(id);
            this.incFatigability = incFatigability;
            this.reqSkillLevel = reqSkillLevel;
            this.incSkillProficiency = incSkillProficiency;
            this.needOpenItem = needOpenItem;
            this.period = period;
        }
    }

    public static class FamiliarEntry {
        public final EnumSet<MonsterStatus> status = EnumSet.noneOf(MonsterStatus.class);
        public byte prop;
        public byte time;
        public byte attackCount;
        public byte targetCount;
        public byte speed;
        public Point lt;
        public Point rb;
        public boolean knockback;

        public boolean makeChanceResult() {
            return this.prop >= 100 || Randomizer.nextInt(100) < this.prop;
        }
    }
}

