/*
 * Decompiled with CFR 0.152.
 */
package Client.skills;

import Client.skills.SkillFactory;
import Client.skills.SkillInfo;
import Client.skills.SkillMesInfo;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassFetcher;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Net.server.buffs.MapleStatEffect;
import Net.server.buffs.MapleStatEffectFactory;
import Net.server.life.Element;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataTool;
import Plugin.provider.MapleDataType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Randomizer;

public class Skill {
    private static final Logger log = LoggerFactory.getLogger(Skill.class);
    private final List<MapleStatEffect> effects = new ArrayList<MapleStatEffect>();
    private final List<Pair<String, Byte>> requiredSkill = new ArrayList<Pair<String, Byte>>();
    private String name = "";
    private final boolean isSwitch = false;
    private final Map<Integer, Integer> bonusExpInfo = new HashMap<Integer, Integer>();
    private int id;
    private String psdDamR = "";
    private String targetPlus = "";
    private String minionAttack = "";
    private String minionAbility = "";
    private Element element = Element.NEUTRAL;
    private List<MapleStatEffect> pvpEffects = null;
    private List<Integer> animation = null;
    private int hyper = 0;
    private int hyperStat = 0;
    private int reqLev = 0;
    private int animationTime = 0;
    private int masterLevel = 0;
    private int maxLevel = 0;
    private int delay = 0;
    private int trueMax = 0;
    private int eventTamingMob = 0;
    private int skillType = 0;
    private int fixLevel;
    private int disableNextLevelInfo;
    private int psd = 0;
    private List<Integer> psdSkills = null;
    private int setItemReason;
    private int setItemPartsCount;
    private int maxDamageOver = 999999;
    private int ppRecovery = 0;
    private boolean invisible = false;
    private boolean chargeSkill = false;
    private boolean timeLimited = false;
    private boolean combatOrders = false;
    private boolean pvpDisabled = false;
    private boolean magic = false;
    private boolean casterMove = false;
    private boolean chargingSkill;
    private boolean passiveSkill;
    private boolean selfDestructMinion;
    private boolean rapidAttack;
    private boolean pushTarget = false;
    private boolean pullTarget = false;
    private boolean buffSkill = false;
    private boolean summon = false;
    private boolean notRemoved = false;
    private boolean disable = false;
    private boolean hasMasterLevelProperty = false;
    private boolean petPassive = false;
    private boolean finalAttack = false;
    private boolean soulSkill = false;
    private boolean notCooltimeReduce = false;
    private boolean notCooltimeReset = false;
    private boolean showSummonedBuffIcon = false;
    public static Map<String, Integer> Info = new HashMap<String, Integer>();
    public Map<SkillInfo, String> info = new HashMap<SkillInfo, String>();
    public List<SkillMesInfo> MesList = new ArrayList<SkillMesInfo>();
    public static Map<String, List<Integer>> SkillMes = new HashMap<String, List<Integer>>();
    public static List<Integer> SkillMeList = new ArrayList<Integer>();
    public boolean notIncBuffDuration = false;
    private boolean mesToBoss = false;
    private boolean mobSkill;
    private int vehicleID;
    private boolean profession;
    private boolean ignoreCounter;
    private int hitTime;
    private boolean recipe;
    private int vSkill;
    private static final Lock delayDataLock = new ReentrantLock();
    public static List<Integer> skillList = new ArrayList<Integer>();

    public Skill() {
    }

    public Skill(int id) {
        this.id = id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Skill loadFromData(int id, MapleData data, MapleData delayData) {
        MapleData dat;
        MapleData reqDataRoot;
        MapleData level2;
        String d;
        boolean isHit;
        MapleData psdSkill;
        boolean showSkill = false;
        if (showSkill) {
            System.out.println("正在解析技能id: " + id + " 名字: " + SkillFactory.getSkillName(id));
            log.trace("正在解析技能id: " + id + " 名字: " + SkillFactory.getSkillName(id), (Object)true);
        }
        Skill ret = new Skill(id);
        ret.name = SkillFactory.getSkillName(id);
        int skillType = MapleDataTool.getInt("skillType", data, -1);
        String elem = MapleDataTool.getString("elemAttr", data, null);
        ret.element = elem != null ? Element.getFromChar(elem.charAt(0)) : Element.NEUTRAL;
        ret.skillType = skillType;
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        MapleData effect = data.getChildByPath("effect");
        MapleData common = data.getChildByPath("common");
        MapleData info = data.getChildByPath("info");
        MapleData info2 = data.getChildByPath("info2");
        MapleData hit = data.getChildByPath("hit");
        MapleData ball = data.getChildByPath("ball");
        ret.mobSkill = data.getChildByPath("mob") != null;
        ret.summon = data.getChildByPath("summon") != null;
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        if (ret.masterLevel > 0) {
            ret.hasMasterLevelProperty = true;
        }
        ret.psd = MapleDataTool.getInt("psd", data, 0);
        if (ret.psd == 1 && (psdSkill = data.getChildByPath("psdSkill")) != null) {
            ret.psdSkills = new ArrayList<Integer>();
            data.getChildByPath("psdSkill").getChildren().forEach(it -> ret.psdSkills.add(Integer.valueOf(it.getName())));
        }
        ret.notRemoved = MapleDataTool.getInt("notRemoved", data, 0) > 0;
        ret.notIncBuffDuration = MapleDataTool.getInt("notIncBuffDuration", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.combatOrders = MapleDataTool.getInt("combatOrders", data, 0) > 0;
        ret.fixLevel = MapleDataTool.getInt("fixLevel", data, 0);
        ret.disable = MapleDataTool.getInt("disable", data, 0) > 0;
        ret.disableNextLevelInfo = MapleDataTool.getInt("disableNextLevelInfo", data, 0);
        ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
        ret.vehicleID = MapleDataTool.getInt("vehicleID", data, 0);
        ret.hyper = MapleDataTool.getInt("hyper", data, 0);
        ret.hyperStat = MapleDataTool.getInt("hyperStat", data, 0);
        ret.reqLev = MapleDataTool.getInt("reqLev", data, 0);
        ret.petPassive = MapleDataTool.getInt("petPassive", data, 0) > 0;
        ret.setItemReason = MapleDataTool.getInt("setItemReason", data, 0);
        ret.setItemPartsCount = MapleDataTool.getInt("setItemPartsCount", data, 0);
        ret.ppRecovery = MapleDataTool.getInt("ppRecovery", data, 0);
        ret.notCooltimeReduce = MapleDataTool.getInt("notCooltimeReduce", data, 0) > 0;
        ret.notCooltimeReset = MapleDataTool.getInt("notCooltimeReset", data, 0) > 0;
        ret.showSummonedBuffIcon = MapleDataTool.getInt("showSummonedBuffIcon", data, 0) > 0;
        ret.profession = id / 10000 >= 9200 && id / 10000 <= 9204;
        ret.vSkill = MapleDataTool.getInt("vSkill", data, ret.isVSkill() ? 1 : -1);
        if (info != null) {
            ret.pvpDisabled = MapleDataTool.getInt("pvp", info, 1) <= 0;
            ret.magic = MapleDataTool.getInt("magicDamage", info, 0) > 0;
            ret.casterMove = MapleDataTool.getInt("casterMove", info, 0) > 0;
            ret.pushTarget = MapleDataTool.getInt("pushTarget", info, 0) > 0;
            ret.pullTarget = MapleDataTool.getInt("pullTarget", info, 0) > 0;
            ret.rapidAttack = MapleDataTool.getInt("rapidAttack", info, 0) > 0;
            ret.minionAttack = MapleDataTool.getString("minionAttack", info, "");
            ret.minionAbility = MapleDataTool.getString("minionAbility", info, "");
            ret.selfDestructMinion = MapleDataTool.getInt("selfDestructMinion", info, 0) > 0;
            boolean bl = ret.chargingSkill = MapleDataTool.getInt("chargingSkill", info, 0) > 0 || MapleDataTool.getInt("keydownThrowing", info, 0) > 0 || id == 2221011;
        }
        if (info2 != null) {
            ret.ignoreCounter = MapleDataTool.getInt("ignoreCounter", info2, 0) > 0;
        }
        MapleData action_ = data.getChildByPath("action");
        boolean action = false;
        if (action_ == null && data.getChildByPath("prepare/action") != null) {
            action_ = data.getChildByPath("prepare/action");
            action = true;
        }
        boolean isBuff = effect != null && hit == null && ball == null;
        boolean bl = isHit = hit != null;
        if (action_ != null && (d = action ? MapleDataTool.getString(action_, null) : MapleDataTool.getString("0", action_, null)) != null) {
            isBuff |= d.equals("alert2");
            delayDataLock.lock();
            try {
                MapleData dd = delayData.getChildByPath(d);
                if (dd != null) {
                    for (MapleData del : dd) {
                        ret.delay += Math.abs(MapleDataTool.getInt("delay", del, 0));
                    }
                    if (ret.delay > 30) {
                        ret.delay = (int)Math.round((double)ret.delay * 11.0 / 16.0);
                        ret.delay -= ret.delay % 30;
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                delayDataLock.unlock();
            }
            if (SkillFactory.getDelay(d) != null) {
                ret.animation = new ArrayList<Integer>();
                ret.animation.add(SkillFactory.getDelay(d));
                if (!action) {
                    for (MapleData ddc : action_) {
                        try {
                            String c;
                            if (ddc.getType() != MapleDataType.STRING || MapleDataTool.getString(ddc, d).equals(d) || ddc.getName().contentEquals("delay") || SkillFactory.getDelay(c = MapleDataTool.getString(ddc)) == null) continue;
                            ret.animation.add(SkillFactory.getDelay(c));
                        }
                        catch (Exception e) {
                            throw new RuntimeException(String.valueOf(ret.getId()), e);
                        }
                    }
                }
            }
        }
        boolean bl2 = ret.chargeSkill = data.getChildByPath("keydown") != null;
        if (info != null) {
            info.getChildren().forEach(mapleData -> {
                if (mapleData.getName().equals("finalAttack") && ((Number)mapleData.getData()).intValue() == 1 && !SkillFactory.getFinalAttackSkills().contains(id)) {
                    ret.finalAttack = true;
                    SkillFactory.getFinalAttackSkills().add(id);
                }
            });
        }
        if (!ret.chargeSkill) {
            switch (id) {
                case 2221012: {
                    ret.chargeSkill = true;
                }
            }
        }
        MapleData levelData = data.getChildByPath("level");
        if (common != null) {
            ret.maxLevel = MapleDataTool.getInt("maxLevel", common, 1);
            ret.trueMax = ret.maxLevel + (ret.combatOrders ? 2 : (ret.vSkill == 2 ? 10 : (ret.vSkill == 1 ? 5 : 0)));
            if (levelData != null) {
                for (MapleData leve : levelData) {
                    ret.effects.add(MapleStatEffectFactory.loadSkillEffectFromData(ret, (MapleData)leve, id, isHit, isBuff, ret.summon, Byte.parseByte(leve.getName()), null, ret.notRemoved, ret.notIncBuffDuration));
                }
            } else {
                int i;
                ret.soulSkill = common.getChildByPath("soulmpCon") != null;
                ret.psdDamR = MapleDataTool.getString("damR", common, "");
                ret.targetPlus = MapleDataTool.getString("targetPlus", common, "");
                for (i = 1; i < 3; ++i) {
                    if (data.getChildByPath("info" + (i == 1 ? "" : String.valueOf(i))) == null) continue;
                    for (MapleData mapleData2 : data.getChildByPath("info" + (i == 1 ? "" : String.valueOf(i))).getChildren()) {
                        try {
                            SkillInfo Sinfo = SkillInfo.valueOf(mapleData2.getName());
                            switch (Sinfo) {
                                case incDamToStunTarget: {
                                    break;
                                }
                                case affectedSkillEffect: 
                                case mes: {
                                    String[] keys;
                                    String key = mapleData2.getData().toString();
                                    for (String key1 : keys = key.split("&&")) {
                                        try {
                                            ret.MesList.add(SkillMesInfo.getInfo(key1));
                                        }
                                        catch (Exception e) {
                                            System.err.println("加載錯誤技能:" + id);
                                        }
                                    }
                                    break;
                                }
                                case mesToBoss: {
                                    ret.mesToBoss = true;
                                }
                            }
                            if (Sinfo == null) continue;
                            ret.info.put(Sinfo, mapleData2.getData().toString());
                        }
                        catch (Exception e) {
                            System.err.println(id);
                            e.printStackTrace();
                        }
                    }
                }
                for (i = 1; i <= ret.trueMax; ++i) {
                    ret.effects.add(MapleStatEffectFactory.loadSkillEffectFromData(ret, common, id, isHit, isBuff, ret.summon, i, "x", ret.notRemoved, ret.notIncBuffDuration));
                }
                ret.maxDamageOver = MapleDataTool.getInt("MDamageOver", common, 999999);
            }
        } else if (levelData != null) {
            for (MapleData leve : levelData) {
                ret.effects.add(MapleStatEffectFactory.loadSkillEffectFromData(ret, (MapleData)leve, id, isHit, isBuff, ret.summon, Byte.parseByte(leve.getName()), null, ret.notRemoved, ret.notIncBuffDuration));
            }
            ret.maxLevel = ret.effects.size();
            ret.trueMax = ret.effects.size();
        }
        boolean loadPvpSkill = false;
        if (loadPvpSkill && (level2 = data.getChildByPath("PVPcommon")) != null) {
            ret.pvpEffects = new ArrayList<MapleStatEffect>();
            for (int i = 1; i <= ret.trueMax; ++i) {
                ret.pvpEffects.add(MapleStatEffectFactory.loadSkillEffectFromData(ret, level2, id, isHit, isBuff, ret.summon, i, "x", ret.notRemoved, ret.notIncBuffDuration));
            }
        }
        if ((reqDataRoot = data.getChildByPath("req")) != null) {
            for (MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill.add(new Pair<String, Byte>(reqData.getName(), (byte)MapleDataTool.getInt(reqData, 1)));
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        ret.hitTime = 0;
        if (hit != null) {
            for (Object hitEntry : hit) {
                ret.hitTime += MapleDataTool.getIntConvert("delay", (MapleData)hitEntry, 0);
            }
        }
        if ((dat = data.getChildByPath("skillList")) != null) {
            for (MapleData da : dat.getChildren()) {
                skillList.add(MapleDataTool.getInt(da, 0));
            }
        }
        ret.buffSkill = isBuff;
        switch (id) {
            case 27000207: 
            case 27001100: 
            case 27001201: {
                ret.masterLevel = ret.maxLevel;
            }
        }
        MapleData growthInfo = data.getChildByPath("growthInfo/level");
        if (growthInfo != null) {
            for (MapleData expData : growthInfo.getChildren()) {
                ret.bonusExpInfo.put(Integer.parseInt(expData.getName()), MapleDataTool.getInt("maxExp", expData, 100000000));
            }
        }
        return ret;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public MapleStatEffect getEffect(int level) {
        return this.getEffect(false, level);
    }

    public MapleStatEffect getPVPEffect(int level) {
        return this.getEffect(true, level - 1);
    }

    private MapleStatEffect getEffect(boolean ispvp, int level) {
        List<MapleStatEffect> effects;
        List<MapleStatEffect> list = effects = ispvp ? this.pvpEffects : this.effects;
        if (effects.size() < level) {
            if (effects.size() > 0) {
                return effects.get(effects.size() - 1);
            }
            return null;
        }
        if (level <= 0) {
            return null;
        }
        return effects.get(level - 1);
    }

    public int getSkillType() {
        return this.skillType;
    }

    public List<Integer> getAllAnimation() {
        return this.animation;
    }

    public int getAnimation() {
        if (this.animation == null) {
            return -1;
        }
        return this.animation.get(Randomizer.nextInt(this.animation.size()));
    }

    public void setAnimation(List<Integer> animation) {
        this.animation = animation;
    }

    public List<Integer> getPsdSkills() {
        return this.psdSkills;
    }

    public int getPsd() {
        return this.psd;
    }

    public String getPsdDamR() {
        return this.psdDamR;
    }

    public String getTargetPlus() {
        return this.targetPlus;
    }

    public boolean isPVPDisabled() {
        return this.pvpDisabled;
    }

    public boolean isChargeSkill() {
        return this.chargeSkill;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public boolean isNotRemoved() {
        return this.notRemoved;
    }

    public boolean isRapidAttack() {
        return this.rapidAttack;
    }

    public boolean isPassiveSkill() {
        return this.passiveSkill;
    }

    public boolean isChargingSkill() {
        return this.chargingSkill;
    }

    public boolean hasRequiredSkill() {
        return this.requiredSkill.size() > 0;
    }

    public List<Pair<String, Byte>> getRequiredSkills() {
        return this.requiredSkill;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public int getTrueMax() {
        return this.trueMax;
    }

    public boolean combatOrders() {
        return this.combatOrders;
    }

    public boolean canBeLearnedBy(int job) {
        int jobGrade;
        int skillForJob = this.id / 10000;
        if (JobConstants.is零轉職業(skillForJob)) {
            return skillForJob == JobConstants.getBeginner((short)job);
        }
        int skillGrade = JobConstants.getJobGrade(skillForJob);
        if (skillGrade > (jobGrade = JobConstants.getJobGrade(job))) {
            return false;
        }
        if (JobConstants.is冒險家(skillForJob)) {
            if (JobConstants.getJobBranch(skillForJob) != JobConstants.getJobBranch(job)) {
                return false;
            }
            if (skillForJob / 10 % 10 != 0 && skillForJob / 10 % 10 != job / 10 % 10) {
                return false;
            }
            switch (this.id) {
                case 4000012: 
                case 4001011: 
                case 4001013: {
                    return JobConstants.is影武者(job);
                }
                case 4000000: 
                case 4000005: 
                case 4000010: 
                case 4001003: 
                case 4001014: 
                case 4001334: 
                case 4001344: 
                case 154001001: 
                case 400001023: {
                    return JobConstants.is盜賊(job) && !JobConstants.is影武者(job);
                }
            }
            return true;
        }
        if (JobConstants.is皇家騎士團(skillForJob) || JobConstants.is末日反抗軍(skillForJob)) {
            if (JobConstants.is惡魔殺手(skillForJob)) {
                return JobConstants.is惡魔殺手(job);
            }
            if (JobConstants.is惡魔復仇者(skillForJob)) {
                return JobConstants.is惡魔復仇者(job);
            }
            return skillForJob / 100 == job / 100;
        }
        return JobConstants.getBeginner((short)skillForJob) == JobConstants.getBeginner((short)job);
    }

    public boolean isTimeLimited() {
        return this.timeLimited;
    }

    public boolean isFourthJob() {
        return SkillConstants.isMasterLevelSkill(this.id);
    }

    public boolean isVSkill() {
        return this.id >= 400000000 && this.id < 400060000;
    }

    public Element getElement() {
        return this.element;
    }

    public int getAnimationTime() {
        return this.animationTime;
    }

    public boolean getDisable() {
        return this.disable;
    }

    public int getFixLevel() {
        return this.fixLevel;
    }

    public int getMasterLevel() {
        return this.masterLevel;
    }

    public int getMaxMasterLevel() {
        return this.masterLevel <= 0 ? 0 : this.maxLevel;
    }

    public int getDisableNextLevelInfo() {
        return this.disableNextLevelInfo;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getTamingMob() {
        return this.eventTamingMob;
    }

    public int getHyper() {
        return this.hyper;
    }

    public int getReqLevel() {
        return this.reqLev;
    }

    public int getMaxDamageOver() {
        return this.maxDamageOver;
    }

    public int getBonusExpInfo(int level) {
        if (this.bonusExpInfo.isEmpty()) {
            return -1;
        }
        if (this.bonusExpInfo.containsKey(level)) {
            return this.bonusExpInfo.get(level);
        }
        return -1;
    }

    public Map<Integer, Integer> getBonusExpInfo() {
        return this.bonusExpInfo;
    }

    public boolean isMagic() {
        return this.magic;
    }

    public boolean isMovement() {
        return this.casterMove;
    }

    public boolean isPush() {
        return this.pushTarget;
    }

    public boolean isPull() {
        return this.pullTarget;
    }

    public boolean isBuffSkill() {
        return this.buffSkill;
    }

    public boolean isSummonSkill() {
        return this.summon;
    }

    public boolean isNonAttackSummon() {
        return this.summon && this.minionAttack.isEmpty() && (this.minionAbility.isEmpty() || this.minionAbility.equals("taunt"));
    }

    public boolean isNonExpireSummon() {
        return this.selfDestructMinion;
    }

    public boolean isHyperSkill() {
        return this.hyper > 0 && this.reqLev > 0;
    }

    public boolean isHyperStat() {
        return this.hyperStat > 0;
    }

    public boolean isGuildSkill() {
        int jobId = this.id / 10000;
        return jobId == 9100;
    }

    public boolean isBeginnerSkill() {
        int jobId = this.id / 10000;
        return JobConstants.notNeedSPSkill(jobId);
    }

    public boolean isAdminSkill() {
        int jobId = this.id / 10000;
        return jobId == 800 || jobId == 900;
    }

    public boolean isInnerSkill() {
        int jobId = this.id / 10000;
        return jobId == 7000;
    }

    public boolean isSpecialSkill() {
        int jobId = this.id / 10000;
        return jobId == 7000 || jobId == 7100 || jobId == 8000 || jobId == 9000 || jobId == 9100 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
    }

    public int getSkillByJobBook() {
        return this.getSkillByJobBook(this.id);
    }

    public int getSkillByJobBook(int skillid) {
        int n2 = skillid / 10000;
        if (n2 / 1000 > 0 || n2 < 100) {
            return -1;
        }
        int cj = SkillConstants.dY(n2);
        if (cj == 4 && skillid % 10000 == 1054) {
            return 5;
        }
        return cj;
    }

    public boolean isPetPassive() {
        return this.petPassive;
    }

    public int getSetItemReason() {
        return this.setItemReason;
    }

    public int geSetItemPartsCount() {
        return this.setItemPartsCount;
    }

    public boolean isSwitch() {
        return false;
    }

    public boolean isTeachSkills() {
        return SkillConstants.isTeachSkills(this.id);
    }

    public boolean isLinkSkills() {
        return SkillConstants.isLinkSkills(this.id);
    }

    public boolean is老技能() {
        switch (this.id) {
            case 11100025: 
            case 11100026: {
                return true;
            }
        }
        return false;
    }

    public boolean isAngelSkill() {
        return SkillConstants.is天使祝福戒指(this.id);
    }

    public boolean isLinkedAttackSkill() {
        return SkillConstants.isLinkedAttackSkill(this.id);
    }

    public boolean isDefaultSkill() {
        return this.getFixLevel() > 0;
    }

    public int getPPRecovery() {
        return this.ppRecovery;
    }

    public boolean isSoulSkill() {
        return this.soulSkill;
    }

    public void setSoulSkill(boolean soulSkill) {
        this.soulSkill = soulSkill;
    }

    public boolean isNotCooltimeReduce() {
        return this.notCooltimeReduce;
    }

    public boolean isNotCooltimeReset() {
        return this.notCooltimeReset;
    }

    public boolean isMesToBoss() {
        return this.mesToBoss;
    }

    public boolean isMobSkill() {
        return this.mobSkill;
    }

    public int getVehicleID() {
        return this.vehicleID;
    }

    public boolean isProfession() {
        return this.profession;
    }

    public boolean isIgnoreCounter() {
        return this.ignoreCounter;
    }

    public int getHitTime() {
        return this.hitTime;
    }

    public AbstractSkillHandler getHandler() {
        return SkillClassFetcher.getHandlerBySkill(this.id);
    }

    public void setRecipe(boolean recipe) {
        this.recipe = recipe;
    }

    public boolean isRecipe() {
        return this.recipe;
    }

    public String toString() {
        return SkillFactory.getSkillName(this.id) + "(" + this.id + ")";
    }

    public boolean isSummonedBuffIcon() {
        return this.showSummonedBuffIcon;
    }

    public boolean isInfo(SkillInfo info) {
        return this.info.containsKey((Object)info);
    }

    public boolean getMesInfo(SkillMesInfo info) {
        for (SkillMesInfo skillMesInfo : this.MesList) {
            if (skillMesInfo != info) continue;
            return true;
        }
        return false;
    }

    public List<Integer> getSkillList() {
        return skillList;
    }
}

