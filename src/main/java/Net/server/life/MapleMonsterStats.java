/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.life.MapleLifeFactory$loseItem
 */
package Net.server.life;

import Config.constants.GameConstants;
import Net.server.life.BanishInfo;
import Net.server.life.Element;
import Net.server.life.ElementalEffectiveness;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MobAttackInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Triple;

public class MapleMonsterStats {
    private final int id;
    private final EnumMap<Element, ElementalEffectiveness> resistance = new EnumMap(Element.class);
    private final Map<String, Integer> animationTimes = new HashMap<String, Integer>();
    private final List<Triple<Integer, Integer, Integer>> skills = new ArrayList<Triple<Integer, Integer, Integer>>();
    private final List<MobAttackInfo> mobAttacks = new ArrayList<MobAttackInfo>();
    private Pair<Integer, Integer> bodyDisease = null;
    private byte cp;
    private byte selfDestruction_action;
    private byte tagColor;
    private byte tagBgColor;
    private byte rareItemDropLevel;
    private byte HPDisplayType;
    private byte summonType;
    private byte category;
    private short level;
    private short charismaEXP;
    private long hp;
    private long finalMaxHP;
    private int exp;
    private int mp;
    private int removeAfter;
    private int buffToGive;
    private int fixedDamage;
    private int selfDestruction_hp;
    private int dropItemPeriod;
    private int point;
    private int eva;
    private int acc;
    private int userCount;
    private int physicalAttack;
    private int magicAttack;
    private int speed;
    private int partyBonusR;
    private int pushed;
    private int link;
    private int weaponPoint;
    private int PDRate;
    private int MDRate;
    private int smartPhase;
    private int patrolRange;
    private int patrolDetectX;
    private int patrolSenseX;
    private int rewardSprinkleCount;
    private int rewardSprinkleSpeed;
    private boolean boss;
    private boolean undead;
    private boolean publicReward;
    private boolean firstAttack;
    private boolean isExplosiveReward;
    private boolean mobile;
    private boolean fly;
    private boolean onlyNormalAttack;
    private boolean friendly;
    private boolean noDoom;
    private boolean invincible;
    private boolean partyBonusMob;
    private boolean changeable;
    private boolean escort;
    private boolean removeOnMiss;
    private boolean skeleton;
    private boolean patrol;
    private boolean ignoreMoveImpact;
    private boolean rewardSprinkle;
    private boolean defenseMob;
    private String name;
    private String mobType;
    private Map<String, Integer> hitParts = new HashMap<String, Integer>();
    private List<Integer> revives = new ArrayList<Integer>();
    private List<Pair<Point, Point>> mobZone = new ArrayList<Pair<Point, Point>>();
    private Pair<Integer, Integer> cool = null;
    private List<MapleLifeFactory.loseItem> loseItem = null;
    private int HpLinkMob;
    private final List<BanishInfo> banish = new ArrayList<BanishInfo>();
    private TransMobs transMobs;

    public MapleMonsterStats(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public long getHp() {
        return this.hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public int getMp() {
        return this.mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public short getLevel() {
        return this.level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public int getWeaponPoint() {
        return this.weaponPoint;
    }

    public void setWeaponPoint(int wp) {
        this.weaponPoint = wp;
    }

    public short getCharismaEXP() {
        return this.charismaEXP;
    }

    public void setCharismaEXP(short leve) {
        this.charismaEXP = leve;
    }

    public byte getSelfD() {
        return this.selfDestruction_action;
    }

    public void setSelfD(byte selfDestruction_action) {
        this.selfDestruction_action = selfDestruction_action;
    }

    public void setSelfDHP(int selfDestruction_hp) {
        this.selfDestruction_hp = selfDestruction_hp;
    }

    public int getSelfDHp() {
        return this.selfDestruction_hp;
    }

    public int getFixedDamage() {
        return this.fixedDamage;
    }

    public void setFixedDamage(int damage) {
        this.fixedDamage = damage;
    }

    public int getPushed() {
        return this.pushed;
    }

    public void setPushed(int damage) {
        this.pushed = damage;
    }

    public final int getUserCount() {
        return this.userCount;
    }

    public final void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getPhysicalAttack() {
        return this.physicalAttack;
    }

    public void setPhysicalAttack(int PhysicalAttack) {
        this.physicalAttack = PhysicalAttack;
    }

    public int getMagicAttack() {
        return this.magicAttack;
    }

    public void setMagicAttack(int MagicAttack) {
        this.magicAttack = MagicAttack;
    }

    public int getEva() {
        return this.eva;
    }

    public void setEva(int eva) {
        this.eva = eva;
    }

    public int getAcc() {
        return this.acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getPartyBonusRate() {
        return this.partyBonusR;
    }

    public void setPartyBonusRate(int speed) {
        this.partyBonusR = speed;
    }

    public void setOnlyNormalAttack(boolean onlyNormalAttack) {
        this.onlyNormalAttack = onlyNormalAttack;
    }

    public boolean getOnlyNoramlAttack() {
        return this.onlyNormalAttack;
    }

    public List<BanishInfo> getBanishInfo() {
        return this.banish;
    }

    public void addBanishInfo(BanishInfo banish) {
        this.banish.add(banish);
    }

    public int getRemoveAfter() {
        return this.removeAfter;
    }

    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }

    public byte getrareItemDropLevel() {
        return this.rareItemDropLevel;
    }

    public void setrareItemDropLevel(byte rareItemDropLevel) {
        this.rareItemDropLevel = rareItemDropLevel;
    }

    public boolean isBoss() {
        return this.boss;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    public boolean isPublicReward() {
        return this.publicReward;
    }

    public void setPublicReward(boolean publicReward) {
        this.publicReward = publicReward;
    }

    public boolean isEscort() {
        return this.escort;
    }

    public void setEscort(boolean ffaL) {
        this.escort = ffaL;
    }

    public boolean isExplosiveReward() {
        return this.isExplosiveReward;
    }

    public int getHpLinkMob() {
        return this.HpLinkMob;
    }

    public void setHpLinkMob(int hpLinkMob) {
        this.HpLinkMob = hpLinkMob;
    }

    public void setExplosiveReward(boolean isExplosiveReward) {
        this.isExplosiveReward = isExplosiveReward;
    }

    public void setAnimationTime(String name, int delay) {
        this.animationTimes.put(name, delay);
    }

    public int getAnimationTime(String name) {
        Integer ret = this.animationTimes.get(name);
        if (ret == null) {
            return 500;
        }
        return ret;
    }

    public boolean isMobile() {
        return this.animationTimes.containsKey("move") || this.animationTimes.containsKey("fly");
    }

    public boolean isFlyMobile() {
        return this.animationTimes.containsKey("flyingMove") || this.animationTimes.containsKey("fly");
    }

    public boolean isFly() {
        return this.fly;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public List<Integer> getRevives() {
        return this.revives;
    }

    public void setRevives(List<Integer> revives) {
        this.revives = revives;
    }

    public boolean getUndead() {
        return this.undead;
    }

    public void setUndead(boolean undead) {
        this.undead = undead;
    }

    public byte getSummonType() {
        return this.summonType;
    }

    public void setSummonType(byte selfDestruction) {
        this.summonType = selfDestruction;
    }

    public byte getCategory() {
        return this.category;
    }

    public void setCategory(byte selfDestruction) {
        this.category = selfDestruction;
    }

    public int getPDRate() {
        return this.PDRate;
    }

    public void setPDRate(int selfDestruction) {
        this.PDRate = selfDestruction;
    }

    public int getMDRate() {
        return this.MDRate;
    }

    public void setMDRate(int selfDestruction) {
        this.MDRate = selfDestruction;
    }

    public EnumMap<Element, ElementalEffectiveness> getElements() {
        return this.resistance;
    }

    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        this.resistance.put(e, ee);
    }

    public void removeEffectiveness(Element e) {
        this.resistance.remove((Object)e);
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = this.resistance.get((Object)e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.正常;
        }
        return elementalEffectiveness;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.mobType;
    }

    public void setType(String mobt) {
        this.mobType = mobt;
    }

    public Map<String, Integer> getHitParts() {
        return this.hitParts;
    }

    public void setHitParts(Map<String, Integer> hitParts) {
        this.hitParts = hitParts;
    }

    public byte getTagColor() {
        return this.tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = (byte)tagColor;
    }

    public byte getTagBgColor() {
        return this.tagBgColor;
    }

    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = (byte)tagBgColor;
    }

    public List<Triple<Integer, Integer, Integer>> getSkills() {
        return Collections.unmodifiableList(this.skills);
    }

    public void setSkills(List<Triple<Integer, Integer, Integer>> skill_) {
        for (Triple<Integer, Integer, Integer> skill : skill_) {
            this.skills.add(skill);
        }
    }

    public int getSkillSize() {
        return this.skills.size();
    }

    public boolean hasSkill(int skillId, int level) {
        for (Triple<Integer, Integer, Integer> skill : this.skills) {
            if (skill.getLeft() != skillId || skill.getMid() != level) continue;
            return true;
        }
        return false;
    }

    public boolean isFirstAttack() {
        return this.firstAttack;
    }

    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    public byte getCP() {
        return this.cp;
    }

    public void setCP(byte cp) {
        this.cp = cp;
    }

    public int getPoint() {
        return this.point;
    }

    public void setPoint(int cp) {
        this.point = cp;
    }

    public boolean isFriendly() {
        return this.friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public void setInvincible(boolean invin) {
        this.invincible = invin;
    }

    public int isSmartPhase() {
        return this.smartPhase;
    }

    public void setSmartPhase(int smartPhase) {
        this.smartPhase = smartPhase;
    }

    public void setChange(boolean invin) {
        this.changeable = invin;
    }

    public boolean isChangeable() {
        return this.changeable;
    }

    public boolean isPartyBonus() {
        return this.partyBonusMob;
    }

    public void setPartyBonus(boolean invin) {
        this.partyBonusMob = invin;
    }

    public boolean isNoDoom() {
        return this.noDoom;
    }

    public void setNoDoom(boolean doom) {
        this.noDoom = doom;
    }

    public int getBuffToGive() {
        return this.buffToGive;
    }

    public void setBuffToGive(int buff) {
        this.buffToGive = buff;
    }

    public int getLink() {
        return this.link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public byte getHPDisplayType() {
        return this.HPDisplayType;
    }

    public void setHPDisplayType(byte HPDisplayType) {
        this.HPDisplayType = HPDisplayType;
    }

    public int getDropItemPeriod() {
        return this.dropItemPeriod;
    }

    public void setDropItemPeriod(int d) {
        this.dropItemPeriod = d;
    }

    public void setRemoveOnMiss(boolean removeOnMiss) {
        this.removeOnMiss = removeOnMiss;
    }

    public boolean removeOnMiss() {
        return this.removeOnMiss;
    }

    public Pair<Integer, Integer> getCool() {
        return this.cool;
    }

    public void setCool(Pair<Integer, Integer> cool) {
        this.cool = cool;
    }

    public List<Pair<Point, Point>> getMobZone() {
        return this.mobZone;
    }

    public void setMobZone(List<Pair<Point, Point>> mobZone) {
        this.mobZone = mobZone;
    }

    public boolean isSkeleton() {
        return this.skeleton;
    }

    public void setSkeleton(boolean skeleton) {
        this.skeleton = skeleton;
    }

    public List<MapleLifeFactory.loseItem> loseItem() {
        return this.loseItem;
    }

    public void addLoseItem(MapleLifeFactory.loseItem li) {
        if (this.loseItem == null) {
            this.loseItem = new LinkedList<MapleLifeFactory.loseItem>();
        }
        this.loseItem.add(li);
    }

    public void addMobAttack(MobAttackInfo ma) {
        this.mobAttacks.add(ma);
    }

    public MobAttackInfo getMobAttack(int attack) {
        if (attack >= this.mobAttacks.size() || attack < 0) {
            return null;
        }
        return this.mobAttacks.get(attack);
    }

    public List<MobAttackInfo> getMobAttacks() {
        return this.mobAttacks;
    }

    public void setBodyDisease(Pair<Integer, Integer> disease) {
        this.bodyDisease = disease;
    }

    public Pair<Integer, Integer> getBodyDisease() {
        return this.bodyDisease;
    }

    public int dropsMesoCount() {
        if (this.getRemoveAfter() != 0 || this.isInvincible() || this.getOnlyNoramlAttack() || this.getDropItemPeriod() > 0 || this.getCP() > 0 || this.getPoint() > 0 || this.getFixedDamage() > 0 || this.getSelfD() != -1 || this.getPDRate() <= 0 || this.getMDRate() <= 0) {
            return 0;
        }
        int mobId = this.getId() / 100000;
        if (GameConstants.getPartyPlayHP(this.getId()) > 0 || mobId == 97 || mobId == 95 || mobId == 93 || mobId == 91 || mobId == 90) {
            return 0;
        }
        if (this.isExplosiveReward()) {
            return 7;
        }
        if (this.isBoss()) {
            return 2;
        }
        return 1;
    }

    public TransMobs getTransMobs() {
        return this.transMobs;
    }

    public void setTransMobs(TransMobs transMobs) {
        this.transMobs = transMobs;
    }

    public void setPatrol(boolean patrol) {
        this.patrol = patrol;
    }

    public boolean isPatrol() {
        return this.patrol;
    }

    public void setPatrolRange(int patrolRange) {
        this.patrolRange = patrolRange;
    }

    public int getPatrolRange() {
        return this.patrolRange;
    }

    public void setPatrolDetectX(int patrolDetectX) {
        this.patrolDetectX = patrolDetectX;
    }

    public int getPatrolDetectX() {
        return this.patrolDetectX;
    }

    public void setPatrolSenseX(int patrolSenseX) {
        this.patrolSenseX = patrolSenseX;
    }

    public int getPatrolSenseX() {
        return this.patrolSenseX;
    }

    public void setIgnoreMoveImpact(boolean ignoreMoveImpact) {
        this.ignoreMoveImpact = ignoreMoveImpact;
    }

    public boolean isIgnoreMoveImpact() {
        return this.ignoreMoveImpact;
    }

    public void setFinalMaxHP(long finalMaxHP) {
        this.finalMaxHP = finalMaxHP;
    }

    public long getFinalMaxHP() {
        return this.finalMaxHP;
    }

    public boolean isRewardSprinkle() {
        return this.rewardSprinkle;
    }

    public void setRewardSprinkle(boolean b) {
        this.rewardSprinkle = b;
    }

    public int getRewardSprinkleCount() {
        return this.rewardSprinkleCount;
    }

    public void setRewardSprinkleCount(int count) {
        this.rewardSprinkleCount = count;
    }

    public int getRewardSprinkleSpeed() {
        return this.rewardSprinkleSpeed;
    }

    public void setRewardSprinkleSpeed(int speed) {
        this.rewardSprinkleSpeed = speed;
    }

    public void setDefenseMob(boolean b) {
        this.defenseMob = b;
    }

    public boolean isDefenseMob() {
        return this.defenseMob;
    }

    public static class TransMobs {
        private final List<Integer> mobids;
        private final List<Pair<Integer, Integer>> skills;
        private final int time;
        private final int cooltime;
        private final int hpTriggerOn;
        private final int hpTriggerOff;
        private int withMob = 0;

        public TransMobs(List<Integer> mobids, List<Pair<Integer, Integer>> skills, int time, int cooltime, int hpTriggerOn, int hpTriggerOff, int withMob) {
            this.mobids = mobids;
            this.skills = skills;
            this.time = time;
            this.cooltime = cooltime;
            this.hpTriggerOn = hpTriggerOn;
            this.hpTriggerOff = hpTriggerOff;
            this.withMob = withMob;
        }

        public List<Integer> getMobids() {
            return this.mobids;
        }

        public List<Pair<Integer, Integer>> getSkills() {
            return this.skills;
        }

        public int getTime() {
            return this.time;
        }

        public int getCooltime() {
            return this.cooltime;
        }

        public int getHpTriggerOn() {
            return this.hpTriggerOn;
        }

        public int getHpTriggerOff() {
            return this.hpTriggerOff;
        }

        public int getWithMob() {
            return this.withMob;
        }
    }
}

