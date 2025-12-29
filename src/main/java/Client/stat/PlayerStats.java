/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.world.WorldGuildService
 *  Server.world.guild.MapleGuildSkill
 *  lombok.Generated
 */
package Client.stat;

import Client.MapleCharacter;
import Client.MapleStat;
import Client.MapleTrait;
import Client.MapleTraitType;
import Client.SecondaryStat;
import Client.hexa.HexaFactory;
import Client.hexa.HexaStatCoreEntry;
import Client.hexa.MapleHexaStat;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Client.inventory.MapleWeapon;
import Client.inventory.ModifyInventory;
import Client.skills.InnerSkillEntry;
import Client.skills.SkillFactory;
import Client.stat.EquipRecalcableStats;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.collection.SoulCollectionEntry;
import Net.server.life.Element;
import Packet.BuffPacket;
import Packet.EffectPacket;
import Packet.InventoryPacket;
import Plugin.provider.loaders.SkillData;
import Server.world.WorldGuildService;
import Server.world.guild.MapleGuild;
import Server.world.guild.MapleGuildSkill;
import SwordieX.client.character.skills.Skill;
import SwordieX.enums.BaseStat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;

public final class PlayerStats {
    static final Logger log = LoggerFactory.getLogger(PlayerStats.class);
    public static final int[] pvpSkills = new int[]{1000007, 2000007, 3000006, 4000010, 5000006, 5010004, 11000006, 12000006, 13000005, 14000006, 15000005, 21000005, 22000002, 23000004, 31000005, 32000012, 33000004, 35000005};
    private long nextRecalcStats;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<Integer, List<Integer>> setHandling = new HashMap<Integer, List<Integer>>();
    private final Map<Integer, Integer> add_skill_damage = new HashMap<Integer, Integer>();
    public final Map<Integer, Pair<Integer, Integer>> hpRecover_onAttack = new HashMap<Integer, Pair<Integer, Integer>>();
    private final Map<Integer, Integer> add_skill_duration = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_attackCount = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_targetPlus = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_bossDamage = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_dotTime = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_prop = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_coolTimeR = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_ignoreMobpdpR = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_damage_5th = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_bulletCount = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_reduceForceCon = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> add_skill_custom_val = new HashMap<Integer, Integer>();
    public short str;
    public short dex;
    public short luk;
    public short int_;
    public int hp;
    public int mp;
    public int maxhp;
    public int maxmp;
    public int apAddMaxHp;
    public int apAddMaxMp;
    public boolean hasClone;
    public boolean hasPartyBonus;
    public int expBuff;
    public int dropBuff;
    public int mesoBuff;
    public int cashBuff;
    public int expRPerM;
    public int plusExpRate;
    public double mesoGuard;
    public double mesoGuardMeso;
    public double incRewardProp;
    public int recoverHP;
    public int recoverMP;
    public int mpconReduce;
    public int incMesoRate;
    public int incDropRate;
    public int incMpCon;
    public int incMesoProp;
    public int reduceCooltime;
    public int cooltimeReduceR;
    public int suddenDeathR;
    public int expLossReduceR;
    public int mpRestore;
    public int hpRecover_Percent;
    public int itemRecoveryUP;
    public int BuffUP;
    public int skillRecoveryUP;
    public int incBuffTime;
    public int summonTimeR;
    public int incAllskill;
    public int combatOrders;
    public int BuffUP_Summon;
    public int dodgeChance;
    public int nocoolProp;
    public int asrR;
    public int terR;
    public int pickRate;
    public int pvpDamage;
    public int hpRecoverTime = 0;
    public int mpRecoverTime = 0;
    public int dot;
    public int incDotTime;
    public int pvpRank;
    public int pvpExp;
    public int damX;
    public int incMaxDamage;
    public int incMaxDF;
    public int gauge_x;
    public int mpconMaxPercent;
    public int expCardRate;
    public int dropCardRate;
    public int wdef;
    public int defRange;
    int trueMastery;
    public int attackSpeed;
    public int speed;
    public int speedMax;
    public int jump;
    public short critRate;
    public double criticalDamage;
    public short incCritDamage;
    public int stanceProp;
    public int percent_wdef;
    public int percent_mdef;
    public int incMaxHPR;
    public int incMaxMPR;
    public int incStrR;
    public int incDexR;
    public int incIntR;
    public int incLukR;
    public int percent_acc;
    public int incPadR;
    public int incMadR;
    public double ignoreMobpdpR;
    public double percent_damage;
    public double incDamR;
    public int bossDamageR;
    public int ignore_mob_damage_rate;
    public double damAbsorbShieldR;
    public double incFinalDamage;
    public int raidenCount;
    public int raidenPorp;
    public int healHPR;
    public int healMPR;
    private byte passive_mastery;
    private int localstr;
    private int localdex;
    private int localluk;
    private int localint;
    private int localmaxhp;
    private int localmaxmp;
    private int addmaxhp;
    private int addmaxmp;
    private int lv2mmp;
    private int indieStrFX;
    private int indieDexFX;
    private int indieLukFX;
    private int indieIntFX;
    private int indieMhpFX;
    private int indieMmpFX;
    private int indiePadFX;
    private int indieMadFX;
    private int mad;
    private int pad;
    private long localbasedamage_max;
    private long localbasedamage_min;
    private long localmaxbasepvpdamage;
    private long localmaxbasepvpdamageL;
    public int maxBeyondLoad;
    public int hpRecover_limit;
    public int mpRecover_limit;
    private int finalAttackSkill;
    public int incAttackCount;
    private int incEXPr;
    public int ignoreElement;
    public int reduceForceR;
    public int mpcon_eachSecond;
    private int betamaxhp;
    private int betamaxmp;
    private int kannaLinkDamR;
    private int summoned;
    private int arc;
    private final EquipRecalcableStats equipstats = new EquipRecalcableStats();
    private int aut;

    public long getCurrentMaxHp() {
        return this.localmaxhp;
    }

    public long getCurrentMaxMp(MapleCharacter chr) {
        return this.localmaxmp;
    }

    public void recalcLocalStats(MapleCharacter chra) {
        this.recalcLocalStats(false, chra);
    }

    public void resetLocalStats() {
        this.apAddMaxHp = 0;
        this.apAddMaxMp = 0;
        this.wdef = 0;
        this.damX = 0;
        this.addmaxhp = 0;
        this.addmaxmp = 0;
        this.lv2mmp = 0;
        this.localdex = this.getDex();
        this.localint = this.getInt();
        this.localstr = this.getStr();
        this.localluk = this.getLuk();
        this.indieDexFX = 0;
        this.indieIntFX = 0;
        this.indieStrFX = 0;
        this.indieLukFX = 0;
        this.indieMhpFX = 0;
        this.indieMmpFX = 0;
        this.speed = 100;
        this.speedMax = 140;
        this.jump = 100;
        this.asrR = 0;
        this.terR = 0;
        this.dot = 0;
        this.incDotTime = 0;
        this.trueMastery = 0;
        this.stanceProp = 0;
        this.percent_wdef = 0;
        this.percent_mdef = 0;
        this.incMaxHPR = 0;
        this.incMaxMPR = 0;
        this.incStrR = 0;
        this.incDexR = 0;
        this.incIntR = 0;
        this.incLukR = 0;
        this.percent_acc = 0;
        this.incPadR = 0;
        this.incMadR = 0;
        this.ignoreMobpdpR = 0.0;
        this.critRate = (short)5;
        this.criticalDamage = 0.0;
        this.incDamR = 0.0;
        this.bossDamageR = 0;
        this.mad = 0;
        this.pad = 0;
        this.dodgeChance = 0;
        this.nocoolProp = 0;
        this.pvpDamage = 0;
        this.mesoGuard = 50.0;
        this.mesoGuardMeso = 0.0;
        this.percent_damage = 0.0;
        this.expBuff = 0;
        this.cashBuff = 0;
        this.dropBuff = 0;
        this.mesoBuff = 0;
        this.expRPerM = 0;
        this.plusExpRate = 0;
        this.expCardRate = 100;
        this.dropCardRate = 100;
        this.recoverHP = 0;
        this.recoverMP = 0;
        this.mpconReduce = 0;
        this.incMpCon = 100;
        this.incMesoProp = 0;
        this.incMesoRate = 0;
        this.incDropRate = 0;
        this.reduceCooltime = 0;
        this.cooltimeReduceR = 0;
        this.summonTimeR = 0;
        this.suddenDeathR = 0;
        this.expLossReduceR = 0;
        this.incRewardProp = 0.0;
        this.hpRecover_Percent = 0;
        this.mpRestore = 0;
        this.pickRate = 0;
        this.incMaxDamage = 0;
        this.hasPartyBonus = false;
        this.hasClone = false;
        this.itemRecoveryUP = 0;
        this.BuffUP = 0;
        this.skillRecoveryUP = 0;
        this.incBuffTime = 0;
        this.BuffUP_Summon = 0;
        this.incMaxDF = 0;
        this.incAllskill = 0;
        this.combatOrders = 0;
        this.add_skill_damage.clear();
        this.setHandling.clear();
        this.add_skill_bossDamage.clear();
        this.add_skill_duration.clear();
        this.add_skill_attackCount.clear();
        this.add_skill_targetPlus.clear();
        this.add_skill_dotTime.clear();
        this.add_skill_prop.clear();
        this.add_skill_coolTimeR.clear();
        this.add_skill_ignoreMobpdpR.clear();
        this.add_skill_damage_5th.clear();
        this.add_skill_bulletCount.clear();
        this.add_skill_reduceForceCon.clear();
        this.raidenCount = 0;
        this.raidenPorp = 0;
        this.ignore_mob_damage_rate = 0;
        this.damAbsorbShieldR = 0.0;
        this.mpconMaxPercent = 0;
        this.maxBeyondLoad = 20;
        this.hpRecover_limit = 100;
        this.mpRecover_limit = 100;
        this.finalAttackSkill = 0;
        this.incFinalDamage = 1.0;
        this.hpRecover_onAttack.clear();
        this.incAttackCount = 0;
        this.incEXPr = 0;
        this.ignoreElement = 0;
        this.reduceForceR = 0;
        this.mpcon_eachSecond = 0;
        this.kannaLinkDamR = 0;
        this.hpRecoverTime = 0;
        this.arc = 0;
        this.aut = 0;
    }

    private void sumEquipLocalStats() {
        this.wdef += this.equipstats.wdef;
        this.addmaxhp += this.equipstats.addmaxhp;
        this.addmaxmp += this.equipstats.addmaxmp;
        this.localdex += this.equipstats.localdex;
        this.localint += this.equipstats.localint;
        this.localstr += this.equipstats.localstr;
        this.localluk += this.equipstats.localluk;
        this.indieDexFX += this.equipstats.indieDexFX;
        this.indieIntFX += this.equipstats.indieIntFX;
        this.indieStrFX += this.equipstats.indieStrFX;
        this.indieLukFX += this.equipstats.indieLukFX;
        this.indieMhpFX += this.equipstats.indieMhpFX;
        this.indieMmpFX += this.equipstats.indieMmpFX;
        this.speed += this.equipstats.speed;
        this.jump += this.equipstats.jump;
        this.asrR += this.equipstats.asr;
        this.terR += this.equipstats.terR;
        this.percent_wdef += this.equipstats.percent_wdef;
        this.incMaxHPR += this.equipstats.incMaxHPR;
        this.incMaxMPR += this.equipstats.incMaxMPR;
        this.incStrR += this.equipstats.incStrR;
        this.incDexR += this.equipstats.incDexR;
        this.incIntR += this.equipstats.incIntR;
        this.incLukR += this.equipstats.incLukR;
        this.incPadR += this.equipstats.incPadR;
        this.incMadR += this.equipstats.incMadR;
        this.critRate = (short)(this.critRate + this.equipstats.critRate);
        this.criticalDamage += (double)this.equipstats.criticalDamage;
        this.incDamR += this.equipstats.incDamR;
        this.bossDamageR += this.equipstats.bossDamageR;
        this.mad += this.equipstats.mad;
        this.pad += this.equipstats.pad;
        this.pvpDamage += this.equipstats.pvpDamage;
        this.recoverHP += this.equipstats.recoverHP;
        this.recoverMP += this.equipstats.recoverMP;
        this.mpconReduce += this.equipstats.mpconReduce;
        this.incMesoProp += this.equipstats.incMesoProp;
        this.reduceCooltime += this.equipstats.reduceCooltime;
        this.incRewardProp += this.equipstats.incRewardProp;
        this.incBuffTime += this.equipstats.incBuffTime;
        this.incMaxDF += this.equipstats.incMaxDF;
        this.incAllskill += this.equipstats.incAllskill;
        this.incAttackCount += this.equipstats.incAttackCount;
        this.addIgnoreMobpdpR(this.equipstats.ignoreMobpdpR);
        this.expCardRate = this.equipstats.expCardRate;
        this.dropCardRate = this.equipstats.dropCardRate;
        this.arc += this.equipstats.arc;
        this.aut += this.equipstats.aut;
    }

    public void recalcLocalStats(boolean first_login, MapleCharacter chra) {
        this.recalcLocalStats(first_login, chra, -1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recalcLocalStatsInterrupt(boolean first_login, MapleCharacter chra, int flag) throws InterruptedException {
        if (this.lock.writeLock().tryLock(1L, TimeUnit.SECONDS)) {
            try {
                this.doRecalc(first_login, chra, flag);
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recalcLocalStats(boolean first_login, MapleCharacter chra, int flag) {
        this.lock.writeLock().lock();
        try {
            this.doRecalc(first_login, chra, flag);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - void declaration
     */
    private void doRecalc(boolean first_login, MapleCharacter chra, int flag) {
        int var12_25 = 0;
        MapleGuild g;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        this.resetLocalStats();
        if (chra.getHpApUsed() > 0) {
            if (JobConstants.is零轉職業(chra.getJob())) {
                this.apAddMaxHp += 12 * chra.getHpApUsed();
            } else if (JobConstants.is惡魔復仇者(chra.getJob())) {
                this.apAddMaxHp += 30 * chra.getHpApUsed();
            } else if (JobConstants.is狂狼勇士(chra.getJob()) || JobConstants.is煉獄巫師(chra.getJob())) {
                this.apAddMaxHp += 40 * chra.getHpApUsed();
            } else if (JobConstants.is劍士(chra.getJob())) {
                this.apAddMaxHp += 32 * chra.getHpApUsed();
            } else if (JobConstants.is法師(chra.getJob())) {
                this.apAddMaxHp += 10 * chra.getHpApUsed();
            } else if (JobConstants.is弓箭手(chra.getJob()) || JobConstants.is盜賊(chra.getJob())) {
                this.apAddMaxHp += 15 * chra.getHpApUsed();
            } else if (JobConstants.is海盜(chra.getJob())) {
                this.apAddMaxHp += 22 * chra.getHpApUsed();
            }
        }
        if (chra.getMpApUsed() > 0) {
            if (JobConstants.is零轉職業(chra.getJob())) {
                this.apAddMaxMp += 8 * chra.getMpApUsed();
            } else if (!JobConstants.isNotMpJob(chra.getJob())) {
                if (JobConstants.is劍士(chra.getJob())) {
                    this.apAddMaxMp += 4 * chra.getMpApUsed();
                } else if (JobConstants.is法師(chra.getJob())) {
                    this.apAddMaxMp += 30 * chra.getMpApUsed();
                } else if (JobConstants.is弓箭手(chra.getJob()) || JobConstants.is盜賊(chra.getJob()) || JobConstants.is海盜(chra.getJob())) {
                    this.apAddMaxMp += 10 * chra.getMpApUsed();
                }
            }
        }
        int localmaxhp_ = this.getMaxHp();
        int localmaxmp_ = this.getMaxMp();
        for (MapleTraitType t : MapleTraitType.values()) {
            chra.getTrait(t).clearLocalExp();
        }
        if (first_login || RecalcFlag.Equip.check(flag)) {
            this.equipstats.recalcLocalStats(first_login, chra);
        }
        this.sumEquipLocalStats();
        localmaxhp_ += this.equipstats.localmaxhp_;
        localmaxmp_ += this.equipstats.localmaxmp_;
        this.handlePassiveSkills(chra);
        this.handleHexaStat(chra);
        this.handleBuffStats(chra);
        this.CalcPassive_Mastery(chra, this.equipstats.wt);
        if (chra.getGuildId() > 0 && (g = WorldGuildService.getInstance().getGuild(chra.getGuildId())) != null && g.getSkills().size() > 0) {
            long now = System.currentTimeMillis();
            for (MapleGuildSkill gs : g.getSkills()) {
                Client.skills.Skill skill = SkillFactory.getSkill(gs.getSkillId());
                if (skill == null || gs.getTimestamp() <= now || gs.getActivator().length() <= 0) continue;
                MapleStatEffect e = skill.getEffect(gs.getLevel());
                this.critRate = (short)(this.critRate + e.getCritical());
                this.pad += e.getPadX();
                this.mad += e.getMadX();
                this.expBuff += e.getExpR();
                this.dodgeChance += e.getER();
                this.percent_wdef += e.getPddR();
                this.percent_mdef += e.getMDEFRate();
            }
        }
        LinkedList<Integer> cardSkills = new LinkedList<Integer>();
        for (Pair<Integer, Integer> ix : chra.getCharacterCard().getCardEffects()) {
            MapleStatEffect cardEff;
            Client.skills.Skill skill = SkillFactory.getSkill(ix.getLeft());
            if (skill == null || (cardEff = skill.getEffect(ix.getRight())) == null || cardSkills.contains(ix.getLeft()) && (Integer)ix.getLeft() < 71001100) continue;
            cardSkills.add((Integer)ix.getLeft());
            this.percent_wdef += cardEff.getPddR();
            this.incMaxHPR += cardEff.getMhpR();
            this.incMaxMPR += cardEff.getMmpR();
            this.critRate = (short)(this.critRate + cardEff.getCritical());
            this.criticalDamage += (double)cardEff.getCriticalDamage();
            this.itemRecoveryUP += cardEff.getMPConsumeEff();
            this.percent_acc += cardEff.getPercentAcc();
            this.dodgeChance += cardEff.getPercentAvoid();
            this.jump += cardEff.getPsdJump();
            this.speed += cardEff.getPsdSpeed();
            this.expLossReduceR += cardEff.getEXPLossRate();
            this.asrR += cardEff.getASRRate();
            this.suddenDeathR = (int)((double)this.suddenDeathR + (double)cardEff.getSuddenDeathR() * 0.5);
            this.incMesoProp += cardEff.getMesoAcquisition();
            this.localstr += cardEff.getStrX();
            this.localdex += cardEff.getDexX();
            this.localint += cardEff.getIntX();
            this.localluk += cardEff.getLukX();
            this.indieStrFX += cardEff.getStrFX();
            this.indieDexFX += cardEff.getDexFX();
            this.indieIntFX += cardEff.getIntFX();
            this.indieLukFX += cardEff.getLukFX();
            localmaxhp_ += cardEff.getMaxHpX();
            localmaxmp_ += cardEff.getMaxMpX();
            this.pad += cardEff.getPadX();
            this.mad += cardEff.getMadX();
            this.bossDamageR += cardEff.getBossDamage();
            this.cooltimeReduceR += cardEff.getCooltimeReduceR();
            this.incBuffTime += cardEff.getBuffTimeRate();
            this.addIgnoreMobpdpR(cardEff.getIgnoreMobpdpR());
            this.indiePadFX = (int)((double)this.indiePadFX + (double)(cardEff.getLevelToWatkX() * chra.getLevel()) * 0.5);
            this.indieMadFX = (int)((double)this.indieMadFX + (double)(cardEff.getLevelToMatkX() * chra.getLevel()) * 0.5);
            this.addDamAbsorbShieldR(cardEff.getIgnoreMobDamR());
            this.summonTimeR += cardEff.getSummonTimeInc();
        }
        cardSkills.clear();
        LinkedList<Integer> unionSkill = new LinkedList<Integer>();
        if (chra.getMapleUnion() != null && chra.getMapleUnion().getState() > 0) {
            for (Map.Entry entry : chra.getMapleUnion().getSkills().entrySet()) {
                MapleStatEffect unionEff;
                Client.skills.Skill skill = SkillFactory.getSkill((Integer)entry.getKey());
                if (skill == null || (unionEff = skill.getEffect((Integer)entry.getValue())) == null || unionSkill.contains(entry.getKey()) && (Integer)entry.getKey() < 71001100) continue;
                unionSkill.add((Integer)entry.getKey());
                this.percent_wdef += unionEff.getPddR();
                this.incMaxHPR += unionEff.getMhpR();
                this.incMaxMPR += unionEff.getMmpR();
                this.critRate = (short)(this.critRate + unionEff.getCritical());
                this.criticalDamage += (double)unionEff.getCriticalDamage();
                this.jump += unionEff.getPsdJump();
                this.speed += unionEff.getPsdSpeed();
                this.asrR += unionEff.getASRRate();
                this.localstr += unionEff.getStrX();
                this.localdex += unionEff.getDexX();
                this.localint += unionEff.getIntX();
                this.localluk += unionEff.getLukX();
                this.indieMhpFX += unionEff.getHpFX();
                this.indieStrFX += unionEff.getStrFX();
                this.indieDexFX += unionEff.getDexFX();
                this.indieIntFX += unionEff.getIntFX();
                this.indieLukFX += unionEff.getLukFX();
                this.addmaxhp += unionEff.getMaxHpX();
                this.addmaxmp += unionEff.getMaxMpX();
                this.pad += unionEff.getPadX();
                this.mad += unionEff.getMadX();
                this.bossDamageR += unionEff.getBossDamage();
                this.cooltimeReduceR += unionEff.getCooltimeReduceR();
                this.incBuffTime += unionEff.getBuffTimeRate();
                this.addIgnoreMobpdpR(unionEff.getIgnoreMobpdpR());
                this.indiePadFX += (int)((double)(unionEff.getLevelToWatkX() * chra.getLevel()) * 0.5);
                this.indieMadFX += (int)((double)(unionEff.getLevelToMatkX() * chra.getLevel()) * 0.5);
                this.addDamAbsorbShieldR(unionEff.getIgnoreMobDamR());
                this.summonTimeR += unionEff.getSummonTimeInc();
            }
            block24: for (int i = 0; i < chra.getMapleUnion().getAddStats().length; ++i) {
                String string = chra.getWorldShareInfo(18791, String.valueOf(i));
                int add = chra.getMapleUnion().getAddStats()[i];
                if (string == null || add <= 0) continue;
                switch (Integer.valueOf(string)) {
                    case 0: {
                        this.localstr += add * 5;
                        continue block24;
                    }
                    case 1: {
                        this.localdex += add * 5;
                        continue block24;
                    }
                    case 2: {
                        this.localint += add * 5;
                        continue block24;
                    }
                    case 3: {
                        this.localluk += add * 5;
                        continue block24;
                    }
                    case 4: {
                        this.pad += add;
                        continue block24;
                    }
                    case 5: {
                        this.mad += add;
                        continue block24;
                    }
                    case 6: {
                        this.addmaxhp += add * 250;
                        continue block24;
                    }
                    case 7: {
                        this.addmaxmp += add * 250;
                        continue block24;
                    }
                    case 8: {
                        this.criticalDamage += (double)add;
                        continue block24;
                    }
                    case 9: {
                        this.asrR += add;
                        continue block24;
                    }
                    case 10: {
                        continue block24;
                    }
                    case 11: {
                        this.critRate = (short)(this.critRate + add);
                        continue block24;
                    }
                    case 12: {
                        this.bossDamageR += add;
                        continue block24;
                    }
                    case 13: {
                        this.stanceProp += add;
                        continue block24;
                    }
                    case 14: {
                        this.incBuffTime += add;
                        continue block24;
                    }
                    case 15: {
                        this.addIgnoreMobpdpR(add);
                    }
                }
            }
        }
        unionSkill.clear();
        for (int i = 0; i < 3; ++i) {
            MapleStatEffect innerEffect;
            InnerSkillEntry innerSkillEntry = chra.getInnerSkills()[i];
            if (innerSkillEntry == null || (innerEffect = SkillFactory.getSkill(innerSkillEntry.getSkillId()).getEffect(innerSkillEntry.getSkillLevel())) == null) continue;
            this.wdef += innerEffect.getPddX();
            this.percent_wdef += innerEffect.getPddR();
            this.percent_mdef += innerEffect.getMDEFRate();
            this.incMaxHPR += innerEffect.getMhpR();
            this.incMaxMPR += innerEffect.getMmpR();
            this.dodgeChance += innerEffect.getPercentAvoid();
            this.nocoolProp += innerEffect.getNocoolProp();
            this.critRate = (short)(this.critRate + innerEffect.getCritical());
            this.criticalDamage += (double)innerEffect.getCriticalDamage();
            this.jump += innerEffect.getPsdJump();
            this.speed += innerEffect.getPsdSpeed();
            this.indieStrFX += innerEffect.getStrFX();
            this.indieDexFX += innerEffect.getDexFX();
            this.indieIntFX += innerEffect.getIntFX();
            this.indieLukFX += innerEffect.getLukFX();
            localmaxhp_ += innerEffect.getMaxHpX();
            localmaxmp_ += innerEffect.getMaxMpX();
            this.pad += innerEffect.getPadX();
            this.mad += innerEffect.getMadX();
            this.incBuffTime += innerEffect.getBuffTimeRate();
            this.incMesoRate += innerEffect.getMesoR();
            this.incDropRate += innerEffect.getDropR();
            if (innerEffect.getDexToStr() > 0) {
                this.indieStrFX = (int)((double)this.indieStrFX + Math.floor((float)(this.getDex() * innerEffect.getDexToStr()) / 100.0f));
            }
            if (innerEffect.getStrToDex() > 0) {
                this.indieDexFX = (int)((double)this.indieDexFX + Math.floor((float)(this.getStr() * innerEffect.getStrToDex()) / 100.0f));
            }
            if (innerEffect.getIntToLuk() > 0) {
                this.indieLukFX = (int)((double)this.indieLukFX + Math.floor((float)(this.getInt() * innerEffect.getIntToLuk()) / 100.0f));
            }
            if (innerEffect.getLukToDex() > 0) {
                this.indieDexFX = (int)((double)this.indieDexFX + Math.floor((float)(this.getLuk() * innerEffect.getLukToDex()) / 100.0f));
            }
            if (innerEffect.getLevelToWatk() > 0) {
                this.pad = (int)((double)this.pad + Math.floor(chra.getLevel() / innerEffect.getLevelToWatk()));
            }
            if (innerEffect.getLevelToMatk() > 0) {
                this.mad = (int)((double)this.mad + Math.floor(chra.getLevel() / innerEffect.getLevelToMatk()));
            }
            this.bossDamageR += innerEffect.getBossDamage();
        }
        this.calculateFame(first_login, chra);
        int multiThreadHPR = 0;
        boolean bl = false;
        if (JobConstants.is傑諾(chra.getJob())) {
            int[] skillIds;
            double d = (double)chra.getBuffedIntValue(SecondaryStat.SurplusSupply) / 100.0;
            this.localstr = (int)((double)this.localstr + d * (double)this.str);
            this.localdex = (int)((double)this.localdex + d * (double)this.dex);
            this.localluk = (int)((double)this.localluk + d * (double)this.luk);
            this.localint = (int)((double)this.localint + d * (double)this.int_);
            for (int id : skillIds = new int[]{30020234, 36000004, 36100007, 36110007, 36120010, 36120016}) {
                int bof;
                Client.skills.Skill bx = SkillFactory.getSkill(id);
                if (bx == null || (bof = chra.getSkillLevel(bx)) <= 0) continue;
                MapleStatEffect eff = bx.getEffect(bof);
                if (this.str >= eff.getX()) {
                    this.stanceProp += eff.getY();
                }
                if (this.dex >= eff.getX()) {
                    this.terR += eff.getY();
                    this.asrR += eff.getZ();
                }
                if (this.luk >= eff.getX()) {
                    this.dodgeChance += eff.getZ();
                }
                if (this.str < eff.getX() || this.dex < eff.getX() || this.luk < eff.getX()) continue;
                this.incDamR += (double)eff.getW();
                multiThreadHPR += eff.getS();
                var12_25 += eff.getS();
            }
        }
        localmaxhp_ += chra.getTrait(MapleTraitType.will).getLevel() / 5 * 100 + this.addmaxhp;
        localmaxhp_ = (int)((double)localmaxhp_ + (Math.floor((float)(this.incMaxHPR * localmaxhp_) / 100.0f) + (double)this.indieMhpFX));
        localmaxhp_ = (int)((double)localmaxhp_ + Math.floor((float)(multiThreadHPR * localmaxhp_) / 100.0f));
        this.localmaxhp = Math.min(ServerConfig.CHANNEL_PLAYER_MAXHP, Math.abs(Math.max(-ServerConfig.CHANNEL_PLAYER_MAXHP, localmaxhp_)));
        localmaxmp_ += chra.getTrait(MapleTraitType.sense).getLevel() / 5 * 100 + this.addmaxmp;
        localmaxmp_ = (int)((double)localmaxmp_ + (Math.floor((float)(this.incMaxMPR * localmaxmp_) / 100.0f) + (double)this.indieMmpFX));
        localmaxmp_ += this.lv2mmp;
        localmaxmp_ = (int)((double)localmaxmp_ + Math.floor((float)(var12_25 * localmaxmp_) / 100.0f));
        this.localmaxmp = Math.min(ServerConfig.CHANNEL_PLAYER_MAXMP, Math.abs(Math.max(-ServerConfig.CHANNEL_PLAYER_MAXMP, localmaxmp_)));
        if (JobConstants.is惡魔殺手(chra.getJob())) {
            this.localmaxmp = GameConstants.getMPByJob(chra.getJob());
            this.localmaxmp += this.incMaxDF;
        } else if (JobConstants.is神之子(chra.getJob())) {
            this.localmaxmp = 100 + this.incMaxDF;
        } else if (JobConstants.isNotMpJob(chra.getJob())) {
            this.localmaxmp = 10;
        }
        if (JobConstants.is神之子(chra.getJob()) && first_login) {
            this.betamaxhp = this.localmaxhp;
            this.betamaxmp = 100;
        }
        this.handleHPMPSkills(chra);
        this.localstr = (int)((double)this.localstr + (Math.floor((float)(this.localstr * this.incStrR) / 100.0f) + (double)this.indieStrFX));
        this.localdex = (int)((double)this.localdex + (Math.floor((float)(this.localdex * this.incDexR) / 100.0f) + (double)this.indieDexFX));
        this.localint = (int)((double)this.localint + (Math.floor((float)(this.localint * this.incIntR) / 100.0f) + (double)this.indieIntFX));
        this.localluk = (int)((double)this.localluk + (Math.floor((float)(this.localluk * this.incLukR) / 100.0f) + (double)this.indieLukFX));
        this.pad = (int)((double)this.pad + Math.floor((float)(this.pad * this.incPadR) / 100.0f));
        this.mad = (int)((double)this.mad + Math.floor((float)(this.mad * this.incMadR) / 100.0f));
        this.localint = (int)((double)this.localint + Math.floor((float)(this.localint * this.incMadR) / 100.0f));
        this.wdef = (int)((double)this.wdef + Math.floor((double)this.localstr * 1.5 + (double)(this.localdex + this.localluk) * 0.4));
        this.wdef += chra.getTrait(MapleTraitType.will).getLevel() / 5;
        int total_percent_def = this.percent_wdef + this.percent_mdef;
        this.wdef = (int)Math.min(99999.0, (double)this.wdef + Math.floor((float)(this.wdef * total_percent_def) / 100.0f));
        this.addIgnoreMobpdpR(chra.getTrait(MapleTraitType.charisma).getLevel() / 10);
        this.pvpDamage += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
        this.asrR += chra.getTrait(MapleTraitType.will).getLevel() / 5;
        this.recalcPVPRank(chra);
        if (!first_login) {
            int soulskill = 0;
            short soulOption = 0;
            Equip weapon = (Equip)chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(JobConstants.is神之子(chra.getJob()) && chra.isBeta() ? -10 : -11));
            if (weapon != null && weapon.getSoulOptionID() > 0) {
                try {
                    soulskill = ii.getSoulSkill(weapon.getSoulOptionID() - 1);
                    soulOption = weapon.getSoulOption();
                }
                catch (NullPointerException e) {
                    soulskill = 0;
                    soulOption = 0;
                }
            }
            ArrayList<SecondaryStat> list = new ArrayList<SecondaryStat>();
            list.add(SecondaryStat.SoulMP);
            list.add(SecondaryStat.FullSoulMP);
            if (chra.getSoulSkillID() > 0 && (soulskill != chra.getSoulSkillID() || soulOption != chra.getSoulOption())) {
                chra.setSoulSkillID(0);
                chra.setSoulOption((short)0);
                chra.setSoulMP(0);
                chra.setMaxSoulMP(0);
                chra.setShowSoulEffect(false);
                chra.changeSingleSkillLevel(SkillFactory.getSkill(chra.getSoulSkillID()), -1, 0, -1L);
                chra.getClient().announce(BuffPacket.temporaryStatReset(list, chra));
                chra.getMap().broadcastMessage(chra, BuffPacket.cancelForeignBuff(chra, list), false);
            }
            if (chra.getSoulSkillID() <= 0) {
                if (soulskill > 0) {
                    SoulCollectionEntry entry;
                    int soul = ii.getSoulSkills().getOrDefault(soulskill, -1);
                    int n7 = 1;
                    if (soul >= 0 && chra.getSoulCollection().containsKey(soul) && (entry = ii.getSoulCollection(soul)) != null && (entry.getItems().values().parallelStream().map(n -> (int)Math.pow(2.0, n.intValue())).reduce(0, (n2, n3) -> n2 | n3) & chra.getSoulCollection().get(soul)) > 0) {
                        ++n7;
                        chra.updateOneQuestInfo(26467, "skillid", String.valueOf(entry.getSoulSkill()));
                        chra.updateOneQuestInfo(26467, "skillidH", String.valueOf(entry.getSoulSkillH()));
                    }
                    chra.changeSingleSkillLevel(SkillFactory.getSkill(soulskill), 1, 0, -1L);
                }
                chra.setSoulSkillID(soulskill);
                if (weapon == null || weapon.getSoulSocketID() == 0) {
                    chra.setSoulOption((short)0);
                    chra.setMaxSoulMP(0);
                    chra.setSoulMP(0);
                } else {
                    chra.setSoulOption(soulOption);
                    chra.setMaxSoulMP(1000);
                    chra.setSoulMP(chra.getSoulMP());
                }
            }
        }
        if (first_login) {
            chra.silentEnforceMaxHpMp();
            this.relocHeal(chra);
        } else {
            chra.enforceMaxHpMp();
        }
        this.pad += (int)Math.floor((float)(this.pad * this.incPadR) / 100.0f);
        this.mad += (int)Math.floor((float)(this.mad * this.incMadR) / 100.0f);
        this.trueMastery = Math.min(100, this.trueMastery);
        this.stanceProp = Math.min(100, this.stanceProp);
        this.critRate = (short)Math.min(100, this.critRate);
        this.percent_damage += this.incFinalDamage + this.percent_damage * this.incFinalDamage / 100.0;
        this.speed = Math.min(this.speed, this.speedMax);
        this.calculateMaxBaseDamage(chra, this.equipstats.wt);
        if (this.hp > this.localmaxhp) {
            this.hp = this.localmaxhp;
        }
        if (this.mp > this.localmaxmp) {
            this.mp = this.localmaxmp;
        }
    }

    private void handleHPMPSkills(MapleCharacter chra) {
        MapleStatEffect eff;
        if (chra.getJob() == 2217 && (eff = chra.getSkillEffect(22170074)) != null && this.localmaxmp > 0) {
            double d = 0;
            double n = (double)this.mp * 100.0 / (double)this.localmaxmp;
            if (d >= (double)eff.getX() && n <= (double)eff.getY()) {
                this.incMadR += eff.getDamage();
            }
        }
    }

    public int getMesoBuff() {
        return 100 + this.mesoBuff + this.incMesoRate;
    }

    public int getDropBuff() {
        if (this.incRewardProp > 1.0) {
            this.incRewardProp = 1.0;
        }
        return 100 + this.dropBuff + this.incDropRate + (int)(this.incRewardProp * 100.0);
    }

    private void handlePassiveSkills(MapleCharacter chra) {
        Client.skills.Skill bx;
        int bof;
        MapleStatEffect eff;
        Item weapon = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
        Item shield = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
        MapleWeapon wt = weapon == null ? MapleWeapon.沒有武器 : MapleWeapon.getByItemID(weapon.getItemId());
        short job = chra.getJob();
        int elves = SkillConstants.getSkillByJob(12, job);
        int queen = SkillConstants.getSkillByJob(73, job);
        if (chra.getSkillLevel(queen) > chra.getSkillLevel(elves)) {
            eff = chra.getSkillEffect(queen);
            if (eff != null) {
                this.pad += eff.getX();
                this.mad += eff.getX();
            }
        } else {
            eff = chra.getSkillEffect(elves);
            if (eff != null) {
                this.pad += eff.getX();
                this.mad += eff.getX();
            }
        }
        if (chra.getLevel() >= 140) {
            eff = chra.getSkillEffect(80000406);
            if (eff != null) {
                this.incMaxDF += eff.getIndieMaxDF();
            }
            if ((bof = chra.getTotalSkillLevel(bx = SkillFactory.getSkill(80000409))) > 0 && bx != null && bx.isHyperStat()) {
                this.critRate = (short)(this.critRate + (bof <= 5 ? bof : 2 * bof - 5));
            }
            if ((bof = chra.getTotalSkillLevel(bx = SkillFactory.getSkill(80000414))) > 0 && bx != null) {
                this.bossDamageR += bof <= 5 ? 3 * bof : 4 * bof - 5;
            }
            if ((bof = chra.getTotalSkillLevel(bx = SkillFactory.getSkill(80000416))) > 0 && bx != null) {
                this.asrR += bof <= 5 ? bof : 2 * bof - 5;
            }
        }
        if (chra.getSkillEffect(80000269) != null) {
            block88: for (int i = 0; i < 5; ++i) {
                String questInfo2;
                String questInfo = chra.getQuestInfo(16345, i + "d");
                if (questInfo == null || "-1".equals(questInfo) || (questInfo2 = chra.getQuestInfo(16345, questInfo)) == null) continue;
                int intValue = Integer.valueOf(questInfo2);
                int n = -1;
                switch (questInfo) {
                    case "0": {
                        n = 0;
                        break;
                    }
                    case "1": {
                        n = 1;
                        break;
                    }
                    case "2": {
                        n = 2;
                        break;
                    }
                    case "3": {
                        n = 3;
                        break;
                    }
                    case "4": {
                        n = 4;
                        break;
                    }
                    case "5": {
                        n = 5;
                        break;
                    }
                    case "9": {
                        n = 6;
                        break;
                    }
                }
                switch (n) {
                    case 0: {
                        this.bossDamageR += intValue;
                        continue block88;
                    }
                    case 1: {
                        this.incBuffTime += intValue;
                        continue block88;
                    }
                    case 2: {
                        this.critRate = (short)(this.critRate + intValue);
                        continue block88;
                    }
                    case 3: {
                        this.criticalDamage += (double)intValue;
                        continue block88;
                    }
                    case 4: {
                        this.pad += intValue;
                        this.mad += intValue;
                        continue block88;
                    }
                    case 5: {
                        this.incDamR += (double)intValue;
                        continue block88;
                    }
                    case 6: {
                        this.localstr += intValue;
                        this.localdex += intValue;
                        this.localluk += intValue;
                        this.localint += intValue;
                    }
                }
            }
        }
        if (JobConstants.is皇家騎士團(chra.getJob())) {
            eff = chra.getSkillEffect(10000074);
            if (eff != null) {
                this.incMaxHPR += eff.getX();
                this.incMaxMPR += eff.getX();
            }
            if ((eff = chra.getSkillEffect(10000246)) != null) {
                this.localstr += chra.getLevel() / 2;
            }
            if ((eff = chra.getSkillEffect(0x989777)) != null) {
                this.localdex += chra.getLevel() / 2;
            }
            if ((eff = chra.getSkillEffect(0x989778)) != null) {
                this.localint += chra.getLevel() / 2;
            }
            if ((eff = chra.getSkillEffect(0x989779)) != null) {
                this.localluk += chra.getLevel() / 2;
            }
        }
        for (int nSkillId : chra.getSkills().keySet()) {
            bx = SkillFactory.getSkill(nSkillId);
            bof = chra.getTotalSkillLevel(bx);
            if (bof <= 0 || bx == null) continue;
            eff = bx.getEffect(bof);
            Skill sk = SkillData.getSkills().get(nSkillId);
            if (sk == null || !sk.isPsd() || !sk.getPsdSkills().isEmpty()) continue;
            for (MapleStatInfo mapleStatInfo : sk.getCommon().keySet()) {
                switch (mapleStatInfo) {
                    case str: 
                    case strX: 
                    case strFX: {
                        this.localstr += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case strR: {
                        this.incStrR += eff.getStrRate();
                        break;
                    }
                    case dex: 
                    case dexX: 
                    case dexFX: {
                        this.localdex += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case dexR: {
                        this.incDexR += eff.getDexR();
                        break;
                    }
                    case int_: 
                    case intX: 
                    case intFX: {
                        this.localint += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case intR: {
                        this.incIntR += eff.getIntRate();
                        break;
                    }
                    case luk: 
                    case lukX: 
                    case lukFX: {
                        this.localluk += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case lukR: {
                        this.incLukR += eff.getLukRate();
                        break;
                    }
                    case damR: {
                        this.incDamR += (double)eff.getDamR();
                        break;
                    }
                    case mdR: 
                    case pdR: {
                        if (nSkillId == 27120005 || nSkillId == 27000106) break;
                        this.addIncFinalDamage(eff.getInfo().get((Object)mapleStatInfo).intValue());
                        break;
                    }
                    case pad: 
                    case padX: {
                        this.pad += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case padR: {
                        this.incPadR += eff.getPadR();
                        break;
                    }
                    case pdd: 
                    case pddX: 
                    case mdd: 
                    case mddX: {
                        this.wdef += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case pddR: {
                        this.percent_wdef += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case mad: 
                    case madX: {
                        this.mad += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case madR: {
                        this.incMadR += eff.getMadR();
                        break;
                    }
                    case mddR: {
                        this.percent_mdef += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case asrR: {
                        this.asrR += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case terR: {
                        this.terR += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case stanceProp: {
                        this.stanceProp += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case mhpR: {
                        this.incMaxHPR += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case mmpR: {
                        this.incMaxMPR += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case MDF: {
                        this.incMaxDF += eff.getInfo().get((Object)mapleStatInfo).intValue();
                    }
                    case hp: {
                        this.recoverHP += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case mp: {
                        this.recoverMP += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case cr: {
                        this.critRate = (short)(this.critRate + eff.getInfo().get((Object)mapleStatInfo));
                        break;
                    }
                    case criticaldamage: {
                        this.criticalDamage += (double)eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case er: {
                        this.dodgeChance += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case bdR: {
                        this.bossDamageR += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case mobCountDamR: {
                        this.incDamR += (double)eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case speed: 
                    case psdSpeed: {
                        this.speed += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case speedMax: {
                        this.speedMax += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case jump: 
                    case psdJump: {
                        this.jump += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case ignoreMobpdpR: {
                        this.ignoreMobpdpR += (double)eff.getIgnoreMobpdpR();
                        break;
                    }
                    case mastery: {
                        this.trueMastery += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case ignoreMobDamR: {
                        this.addDamAbsorbShieldR(eff.getIgnoreMobDamR());
                        break;
                    }
                    case actionSpeed: {
                        this.attackSpeed += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case acc: 
                    case accX: {
                        break;
                    }
                    case ar: 
                    case accR: {
                        this.percent_acc += eff.getPercentAcc();
                        break;
                    }
                    case arcX: {
                        this.arc += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case expRPerM: {
                        this.expRPerM += eff.getInfo().get((Object)mapleStatInfo).intValue();
                        break;
                    }
                    case bufftimeR: {
                        this.incBuffTime += eff.getBuffTimeRate();
                        break;
                    }
                    case basicStatUp: {
                        int mainStats = eff.getInfo().get((Object)mapleStatInfo);
                        this.incStrR += mainStats;
                        this.incDexR += mainStats;
                        this.incIntR += mainStats;
                        this.incLukR += mainStats;
                        break;
                    }
                }
            }
            for (Map.Entry entry : sk.getInfo().entrySet()) {
                switch ((String)entry.getKey()) {
                    case "finalAttack": {
                        if (this.finalAttackSkill >= nSkillId) break;
                        this.finalAttackSkill = nSkillId;
                        break;
                    }
                    case "massSpell": {
                        break;
                    }
                    case "magicSteal": {
                        break;
                    }
                }
            }
        }
        switch (job) {
            case 110: 
            case 111: 
            case 112: {
                if (chra.getSkillEffect(1100000) == null || wt != MapleWeapon.單手斧 && wt != MapleWeapon.雙手斧) break;
                this.incDamR += 5.0;
                break;
            }
            case 1200: 
            case 1210: 
            case 1211: 
            case 1212: {
                eff = chra.getSkillEffect(10000256);
                if (eff != null) {
                    this.asrR += eff.getASRRate();
                    this.terR += eff.getTERRate();
                }
                if ((eff = chra.getSkillEffect(12000025)) != null) {
                    this.incMaxMPR += eff.getMmpR();
                    this.addmaxmp += eff.getLv2mmp() * chra.getLevel();
                    if (wt == MapleWeapon.短杖) {
                        this.critRate = (short)(this.critRate + 3);
                    }
                }
                if ((eff = chra.getSkillEffect(12100027)) != null) {
                    this.mad += eff.getX();
                }
                if ((eff = chra.getSkillEffect(12110025)) != null) {
                    this.incMpCon += eff.getX() - 100;
                    this.incFinalDamage += (double)(eff.getY() - 100);
                }
                if ((eff = chra.getSkillEffect(12110026)) != null) {
                    this.critRate = (short)(this.critRate + eff.getCritical());
                    this.criticalDamage += (double)eff.getCriticalDamage();
                }
                if ((eff = chra.getSkillEffect(0xB8C8CB)) != null) {
                    this.localint += eff.getIntX();
                }
                if ((eff = chra.getSkillEffect(12120009)) != null) {
                    this.mad += eff.getX();
                    this.percent_damage += (double)eff.getMdR();
                    this.criticalDamage += (double)eff.getCriticalDamage();
                }
                if ((eff = chra.getSkillEffect(12120008)) != null) {
                    this.asrR += eff.getASRRate();
                    this.terR += eff.getTERRate();
                }
                if ((eff = chra.getSkillEffect(12120045)) != null) {
                    this.addSkillAttackCount(12000026, eff.getAttackCount());
                    this.addSkillAttackCount(12100028, eff.getAttackCount());
                    this.addSkillAttackCount(0xB8C8CC, eff.getAttackCount());
                    this.addSkillAttackCount(12120017, eff.getAttackCount());
                    this.addSkillAttackCount(12120010, eff.getAttackCount());
                }
                if ((eff = chra.getSkillEffect(12120046)) == null) break;
                this.addSkillAttackCount(12120011, eff.getAttackCount());
                this.addSkillAttackCount(12121001, eff.getAttackCount());
                break;
            }
            case 2001: 
            case 2300: 
            case 2310: 
            case 2311: 
            case 2312: {
                eff = chra.getSkillEffect(23121004);
                if (eff == null) break;
                this.dodgeChance += eff.getProp();
                break;
            }
            case 2003: 
            case 2400: 
            case 2410: 
            case 2411: 
            case 2412: {
                if (chra.getSkillEffect(20030206) != null) {
                    chra.getTrait(MapleTraitType.insight).setLevel(20, chra);
                    chra.getTrait(MapleTraitType.craft).setLevel(20, chra);
                }
                if ((eff = chra.getSkillEffect(24000003)) != null) {
                    this.dodgeChance += eff.getX();
                }
                if ((eff = chra.getSkillEffect(24120002)) == null) break;
                this.dodgeChance += eff.getX();
            }
        }
    }

    private void handleHexaStat(MapleCharacter chra) {
        if (chra.getLevel() >= 260 && chra.getSkillLevel(500071000) > 0) {
            for (MapleHexaStat mhs : chra.getHexaStats().values()) {
                ArrayList<Pair<Integer, Integer>> types = new ArrayList<Pair<Integer, Integer>>();
                types.add(new Pair<Integer, Integer>(mhs.getPreset() == 1 ? mhs.getMain1() : mhs.getMain0(), mhs.getPreset() == 1 ? mhs.getMain1Lv() : mhs.getMain0Lv()));
                types.add(new Pair<Integer, Integer>(mhs.getPreset() == 1 ? mhs.getAddit1S1() : mhs.getAddit0S1(), mhs.getPreset() == 1 ? mhs.getAddit1S1Lv() : mhs.getAddit0S1Lv()));
                types.add(new Pair<Integer, Integer>(mhs.getPreset() == 1 ? mhs.getAddit1S2() : mhs.getAddit0S2(), mhs.getPreset() == 1 ? mhs.getAddit1S2Lv() : mhs.getAddit0S2Lv()));
                block19: for (int t = 0; t < types.size(); ++t) {
                    HexaStatCoreEntry hsce = t == 0 ? HexaFactory.getHexaMainStats((Integer)((Pair)types.get(t)).getLeft()) : HexaFactory.getAdditionalHexaStats((Integer)((Pair)types.get(t)).getLeft());
                    switch (hsce.getStat()) {
                        case "cdPerM": {
                            this.criticalDamage += (double)hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()).intValue();
                            continue block19;
                        }
                        case "bdRPerM": {
                            this.bossDamageR += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()).intValue();
                            continue block19;
                        }
                        case "ignoreMobpdpRPerM": {
                            this.ignoreMobpdpR += (double)hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()).intValue();
                            continue block19;
                        }
                        case "damRPerM": {
                            this.percent_damage += (double)hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()).intValue();
                            continue block19;
                        }
                        case "padX": {
                            this.pad += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()).intValue();
                            continue block19;
                        }
                        case "madX": {
                            this.mad += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()).intValue();
                            continue block19;
                        }
                        case "indieStat": {
                            short j = chra.getJob();
                            if (JobConstants.is冒險家劍士(j) || JobConstants.is拳霸(j) || JobConstants.is重砲指揮官(j) || JobConstants.is米哈逸(j) || JobConstants.is聖魂劍士(j) || JobConstants.is閃雷悍將(j) || JobConstants.is惡魔殺手(j) || JobConstants.is狂狼勇士(j) || JobConstants.is隱月(j) || JobConstants.is凱撒(j) || JobConstants.is劍豪(j) || JobConstants.is神之子(j) || JobConstants.is皮卡啾(j) || JobConstants.is爆拳槍神(j) || JobConstants.is雪吉拉(j) || JobConstants.is阿戴爾(j) || JobConstants.is亞克(j)) {
                                this.localstr += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()) * 100;
                                continue block19;
                            }
                            if (JobConstants.is冒險家法師(j) || JobConstants.is烈焰巫師(j) || JobConstants.is龍魔導士(j) || JobConstants.is夜光(j) || JobConstants.is陰陽師(j) || JobConstants.is煉獄巫師(j) || JobConstants.is凱內西斯(j) || JobConstants.is伊利恩(j) || JobConstants.is菈菈(j)) {
                                this.localint += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()) * 100;
                                continue block19;
                            }
                            if (JobConstants.is冒險家盜賊(j) || JobConstants.is暗夜行者(j) || JobConstants.is幻影俠盜(j) || JobConstants.is卡蒂娜(j) || JobConstants.is卡莉(j) || JobConstants.is虎影(j)) {
                                this.localluk += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()).intValue();
                                continue block19;
                            }
                            if (JobConstants.is冒險家弓箭手(j) || JobConstants.is槍神(j) || JobConstants.is破風使者(j) || JobConstants.is狂豹獵人(j) || JobConstants.is機甲戰神(j) || JobConstants.is精靈遊俠(j) || JobConstants.is天使破壞者(j) || JobConstants.is墨玄(j) || JobConstants.is凱殷(j)) {
                                this.localdex += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()) * 100;
                                continue block19;
                            }
                            if (JobConstants.is惡魔復仇者(j)) {
                                this.localmaxhp += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()) * 2100;
                                continue block19;
                            }
                            if (JobConstants.is傑諾(j)) {
                                this.localstr += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()) * 48;
                                this.localluk += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()) * 48;
                                this.localdex += hsce.getLevelAddStat().get(((Pair)types.get(t)).getRight()) * 48;
                                continue block19;
                            }
                            System.out.println("尚未設定HEXA屬性核心主屬性" + j);
                            continue block19;
                        }
                        default: {
                            System.out.println("未知的Hexa屬性核心能力值:" + hsce.getStat());
                        }
                    }
                }
            }
        }
    }

    private void handleBuffStats(MapleCharacter chra) {
        Integer buff = chra.getBuffedValue(SecondaryStat.IndiePAD);
        if (buff != null) {
            this.pad += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieMAD)) != null) {
            this.mad += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndiePDD)) != null) {
            this.wdef += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieMHP)) != null) {
            this.addmaxhp += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieMHPR)) != null) {
            this.incMaxHPR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieMMP)) != null) {
            this.addmaxmp += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieMMPR)) != null) {
            this.incMaxMPR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieACC)) != null) {
            // empty if block
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieJump)) != null) {
            this.jump += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieSpeed)) != null) {
            this.speed += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieAllStat)) != null) {
            this.localstr += buff.intValue();
            this.localdex += buff.intValue();
            this.localint += buff.intValue();
            this.localluk += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieEXP)) != null) {
            this.incEXPr += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieSTR)) != null) {
            this.localstr += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieDEX)) != null) {
            this.localdex += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieINT)) != null) {
            this.localint += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieLUK)) != null) {
            this.localluk += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieDamR)) != null) {
            this.incDamR += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieAsrR)) != null) {
            this.asrR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieTerR)) != null) {
            this.terR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieCr)) != null) {
            this.critRate = (short)(this.critRate + buff);
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieCD)) != null) {
            this.criticalDamage += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndiePDDR)) != null) {
            this.percent_wdef += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.DDR)) != null) {
            this.percent_wdef += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieBDR)) != null) {
            this.bossDamageR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieStance)) != null) {
            this.stanceProp += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieIgnoreMobpdpR)) != null) {
            this.addIgnoreMobpdpR(buff.intValue());
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndiePADR)) != null) {
            this.incPadR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.DamR)) != null) {
            this.incDamR += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieMADR)) != null) {
            this.incMadR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndiePMdR)) != null) {
            this.percent_damage += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.PAD)) != null) {
            this.pad += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.MAD)) != null) {
            this.mad += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.PDD)) != null) {
            this.wdef += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.Jump)) != null) {
            this.jump += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.Speed)) != null) {
            this.speed += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.MaxHP)) != null) {
            this.incMaxHPR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.MaxMP)) != null) {
            this.incMaxMPR += buff.intValue();
        }
        if ((buff = chra.getBuffedSkill_Y(SecondaryStat.DarkSight)) != null) {
            this.incDamR += (double)buff.intValue();
            this.bossDamageR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.STR)) != null) {
            this.localstr += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.DEX)) != null) {
            this.localdex += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.INT)) != null) {
            this.localint += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.LUK)) != null) {
            this.localluk += buff.intValue();
        }
        buff = chra.getBuffedValue(SecondaryStat.ComboCounter);
        MapleStatEffect effect = chra.getEffectForBuffStat(SecondaryStat.ComboCounter);
        if (buff != null && effect != null) {
            this.pad += (buff - 1) * effect.getY();
            this.bossDamageR += (buff - 1) * this.getSkillBossDamage(1101013);
            int skillLevel = chra.getSkillLevel(1120003);
            if (skillLevel > 0) {
                effect = SkillFactory.getSkill(1120003).getEffect(skillLevel);
                double n = (buff - 1) * (effect.getV() + (chra.getSkillLevel(1120043) > 0 ? 2 : 0));
                this.percent_damage += n + n * (double)effect.getV() / 100.0;
            } else {
                skillLevel = chra.getSkillLevel(1110013);
                if (skillLevel > 0) {
                    effect = SkillFactory.getSkill(1110013).getEffect(skillLevel);
                    int n = (buff - 1) * (effect.getDamR() + (chra.getSkillLevel(1120043) > 0 ? 2 : 0));
                    this.percent_damage += (double)n + (double)(n * effect.getDamR()) / 100.0;
                }
            }
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.HolySymbol)) != null) {
            this.incEXPr = buff;
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IndieStatR)) != null) {
            double value = (double)buff.intValue() / 100.0;
            this.localstr += (int)((double)this.str * value);
            this.localdex += (int)((double)this.dex * value);
            this.localint += (int)((double)this.int_ * value);
            this.localluk += (int)((double)this.luk * value);
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.Stance)) != null) {
            this.stanceProp += buff.intValue();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.Enrage)) != null) {
            switch (effect.getSourceId()) {
                case 1121010: {
                    this.incFinalDamage += (double)effect.getX();
                    break;
                }
                case 32121010: {
                    this.incDamR += (double)effect.getX();
                    break;
                }
                case 51121006: {
                    this.percent_damage += (double)effect.getX() + this.percent_damage * (double)effect.getX() / 100.0;
                }
            }
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.EnrageCrDamMin)) != null) {
            this.criticalDamage += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.AsrR)) != null) {
            this.asrR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.TerR)) != null) {
            this.terR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.CombatOrders)) != null) {
            this.combatOrders += buff.intValue();
        }
        buff = chra.getBuffedValue(SecondaryStat.ElementalCharge);
        effect = chra.getEffectForBuffStat(SecondaryStat.ElementalCharge);
        if (buff != null && effect != null) {
            int value = buff / effect.getX();
            this.pad += effect.getY() * value;
            this.asrR += 2 * value;
            this.incDamR += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.BlessingArmorIncPAD)) != null) {
            this.pad += buff.intValue();
        }
        buff = chra.getBuffedValue(SecondaryStat.IgnoreTargetDEF);
        effect = chra.getEffectForBuffStat(SecondaryStat.IgnoreTargetDEF);
        if (buff != null && effect != null) {
            this.addIgnoreMobpdpR(buff.intValue());
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.CrossOverChain)) != null) {
            this.percent_damage += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.EMHP)) != null) {
            this.addmaxhp += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.EMMP)) != null) {
            this.addmaxmp += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.EPAD)) != null) {
            this.pad += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.EMAD)) != null) {
            this.mad += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.EPDD)) != null) {
            this.wdef += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.DamAbsorbShield)) != null) {
            this.addDamAbsorbShieldR(buff);
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.DotBasedBuff)) != null) {
            this.incFinalDamage += (double)(buff * (chra.getJob() == 212 ? 5 : 3));
        }
        buff = chra.getBuffedValue(SecondaryStat.ArcaneAim);
        effect = chra.getEffectForBuffStat(SecondaryStat.ArcaneAim);
        if (effect != null && buff != null) {
            this.incDamR += (double)(buff * effect.getX());
        }
        buff = chra.getBuffedValue(SecondaryStat.Infinity);
        effect = chra.getEffectForBuffStat(SecondaryStat.Infinity);
        if (effect != null && buff != null) {
            this.incFinalDamage += (double)((buff - 1) * effect.getDamage());
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.ElementalReset)) != null) {
            this.ignoreElement += buff.intValue();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.AdvancedBless)) != null) {
            this.pad += effect.getX();
            this.mad += effect.getY();
            this.wdef += effect.getZ();
            this.mpconReduce += effect.getMPConReduce();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.Bless)) != null) {
            this.pad += effect.getX();
            this.mad += effect.getY();
            this.wdef += effect.getZ();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.VengeanceOfAngel)) != null) {
            this.ignoreElement += effect.getW();
            this.addSkillAttackCount(2321007, effect.getY());
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.BlessEnsenble)) != null) {
            this.incFinalDamage += (double)buff.intValue();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.Preparation)) != null) {
            this.bossDamageR += effect.getY();
            this.stanceProp += effect.getX();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.SharpEyes)) != null) {
            this.criticalDamage += (double)(buff & 0xFF);
            this.critRate = (short)(this.critRate + (buff >> 8 & 0xFF));
        }
        buff = chra.getBuffedValue(SecondaryStat.BowMasterConcentration);
        effect = chra.getEffectForBuffStat(SecondaryStat.BowMasterConcentration);
        if (buff != null && effect != null) {
            this.asrR += buff * effect.getX();
        }
        buff = chra.getBuffedZ(SecondaryStat.HitStackDamR);
        effect = chra.getEffectForBuffStat(SecondaryStat.HitStackDamR);
        if (buff != null && effect != null) {
            this.incDamR += (double)(buff * effect.getX());
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.ConvertAD)) != null) {
            this.pad += this.equipstats.weaponAttack * buff / 100;
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.DarkSight)) != null) {
            MapleStatEffect skillEffect;
            if (chra.getSkillLevel(4210015) > 0 && JobConstants.is暗影神偷(chra.getJob())) {
                this.incFinalDamage += 5.0;
            }
            if ((skillEffect = chra.getSkillEffect(0x421211)) != null) {
                this.incFinalDamage += (double)skillEffect.getY();
            }
            if (effect.getSourceId() == 400001023) {
                this.incFinalDamage += (double)effect.getY() + (double)effect.getY() * this.incFinalDamage / 100.0;
            }
        }
        buff = chra.getBuffedValue(SecondaryStat.CriticalGrowing);
        int buffedIntZ = chra.getBuffedIntZ(SecondaryStat.CriticalGrowing);
        if (buff != null) {
            this.critRate = (short)(this.critRate + buff);
            this.criticalDamage += (double)buffedIntZ;
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.Asura)) != null) {
            this.addIgnoreMobpdpR(buff.intValue());
        }
        effect = chra.getEffectForBuffStat(SecondaryStat.Dice);
        buff = chra.getBuffedValue(SecondaryStat.Dice);
        if (effect != null && buff != null) {
            int[] array;
            boolean b = false;
            if (buff >= 100) {
                b = true;
                array = new int[]{buff / 100};
            } else {
                array = buff >= 10 ? new int[]{buff / 10, buff % 10} : new int[]{buff};
            }
            int length = array.length;
            block23: for (int i = 0; i < length; ++i) {
                switch (array[i]) {
                    case 2: {
                        this.percent_wdef += effect.getPddR() * (b ? 2 : 1);
                        continue block23;
                    }
                    case 3: {
                        this.incMaxHPR += effect.getMhpR() * (b ? 2 : 1);
                        this.incMaxMPR += effect.getMmpR() * (b ? 2 : 1);
                        continue block23;
                    }
                    case 4: {
                        this.critRate = (short)(this.critRate + (short)(effect.getCritical() * (b ? 2 : 1)));
                        continue block23;
                    }
                    case 5: {
                        this.incDamR += (double)(effect.getDamR() * (b ? 2 : 1));
                        continue block23;
                    }
                    case 6: {
                        this.incEXPr += effect.getExpR() * (b ? 2 : 1);
                        continue block23;
                    }
                    case 7: {
                        this.addIgnoreMobpdpR(effect.getW() * (b ? 2 : 1));
                    }
                }
            }
        }
        if (chra.getBuffedIntValue(SecondaryStat.Roulette) == 1) {
            this.finalAttackSkill = 5310004;
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.QuickDraw)) != null && buff > 1) {
            this.incDamR += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.DEXR)) != null) {
            this.indieDexFX = (int)((double)this.indieDexFX + (double)(buff * this.dex) / 100.0);
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.CriticalBuff)) != null) {
            this.critRate = (short)(this.critRate + buff);
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IncMaxHP)) != null) {
            this.addmaxhp += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.IncMaxMP)) != null) {
            this.indieMmpFX += buff.intValue();
        }
        if (chra.getBuffedValue(SecondaryStat.StrikerHyperElectric) != null) {
            this.addSkillCooltimeReduce(15111022, 100);
            this.addSkillCooltimeReduce(15120003, 100);
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.AranDrain)) != null) {
            this.hpRecover_onAttack.put(effect.getSourceId(), new Pair<Integer, Integer>(effect.getX(), 100));
        }
        if (chra.getBuffedValue(SecondaryStat.IndieBuffIcon) != null) {
            this.asrR += 80;
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.WindBreakerFinal)) != null) {
            this.finalAttackSkill = effect.getSourceId();
        }
        effect = chra.getEffectForBuffStat(SecondaryStat.AddAttackCount);
        buff = chra.getBuffedValue(SecondaryStat.AddAttackCount);
        if (effect != null && buff != null) {
            this.incFinalDamage += (double)(effect.getX() * buff);
        }
        effect = chra.getEffectForBuffStat(SecondaryStat.Judgement);
        buff = chra.getBuffedValue(SecondaryStat.Judgement);
        if (effect != null && buff != null) {
            switch (buff) {
                case 1: {
                    this.critRate = (short)(this.critRate + effect.getV());
                    break;
                }
                case 2: {
                    this.dropBuff += effect.getW();
                    break;
                }
                case 3: {
                    this.asrR += effect.getX();
                    this.terR += effect.getY();
                    break;
                }
                case 5: {
                    this.hpRecover_onAttack.put(effect.getSourceId(), new Pair<Integer, Integer>(effect.getZ(), 100));
                }
            }
        }
        effect = chra.getEffectForBuffStat(SecondaryStat.LifeTidal);
        buff = chra.getBuffedValue(SecondaryStat.LifeTidal);
        if (effect != null && buff != null) {
            if (buff == 2) {
                this.critRate = (short)(this.critRate + effect.getProp());
            } else if (buff == 1) {
                this.incDamR += (double)effect.getX();
            }
        }
        buff = chra.getBuffedValue(SecondaryStat.StackBuff);
        effect = chra.getEffectForBuffStat(SecondaryStat.StackBuff);
        if (buff != null && effect != null && effect.getSourceId() == 27120005) {
            this.incDamR += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.InfinityForce)) != null) {
            this.reduceForceR = 100;
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.InfinityForce)) != null) {
            this.addSkillCooltimeReduce(31121003, 50);
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.DiabolikRecovery)) != null) {
            this.hpRecoverTime = effect.getW() * 1000;
            this.healHPR = effect.getX();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.DotHealHPPerSecond)) != null) {
            this.hpRecoverTime = 4000;
            this.healHPR = effect.getX();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.DotHealMPPerSecond)) != null) {
            this.mpRecoverTime = 4000;
            this.healMPR = effect.getX();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.AttackCountX)) != null) {
            this.addSkillAttackCount(32001000, effect.getAttackCount());
            this.addSkillAttackCount(32101000, effect.getAttackCount());
            this.addSkillAttackCount(32111002, effect.getAttackCount());
            this.addSkillAttackCount(32121002, effect.getAttackCount());
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.HowlingAttackDamage)) != null) {
            this.incPadR += buff.intValue();
            this.incMadR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.BeastForm)) != null) {
            this.incPadR += buff.intValue();
        }
        buff = chra.getBuffedValue(SecondaryStat.JaguarCount);
        effect = chra.getEffectForBuffStat(SecondaryStat.JaguarCount);
        if (buff != null && effect != null) {
            this.critRate = (short)(this.critRate + buff * effect.getY());
            this.criticalDamage += (double)(buff * effect.getZ());
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.JaguarSummoned)) != null) {
            this.asrR += effect.getASRRate();
            this.criticalDamage += (double)effect.getCriticalDamage();
        }
        if (chra.getBuffedValue(SecondaryStat.Mechanic) != null) {
            this.stanceProp += 100;
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.BombTime)) != null) {
            this.addSkillBulletCount(35110017, buff);
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.SurplusSupply)) != null) {
            this.incStrR += buff.intValue();
            this.incDexR += buff.intValue();
            this.incIntR += buff.intValue();
            this.incLukR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.SelfHyperBodyMaxHP)) != null) {
            this.incMaxHPR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.SelfHyperBodyMaxMP)) != null) {
            this.incMaxMPR += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.EvasionMaster)) != null) {
            this.incDamR += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.SelfHyperBodyIncPAD)) != null) {
            this.pad += buff.intValue();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.ReshuffleSwitch)) != null) {
            switch (effect.getSourceId()) {
                case 60001216: {
                    this.wdef += effect.getPddX();
                    this.incMaxHPR += effect.getMhpR();
                    int[] array3 = new int[]{61100005, 61110005, 61120010};
                    for (int j = 0; j < 3; ++j) {
                        effect = chra.getSkillEffect(array3[j]);
                        if (effect == null) continue;
                        this.wdef += effect.getPddX();
                        this.incMaxHPR += effect.getMhpR();
                    }
                    break;
                }
                case 60001217: {
                    this.pad += effect.getPadX();
                    this.critRate = (short)(this.critRate + effect.getCritical());
                    this.bossDamageR += effect.getBossDamage();
                    int[] array4 = new int[]{61100008, 61110010, 61120013};
                    for (int k = 0; k < 3; ++k) {
                        effect = chra.getSkillEffect(array4[k]);
                        if (effect == null) continue;
                        this.pad += effect.getPadX();
                        this.critRate = (short)(this.critRate + effect.getCritical());
                        this.bossDamageR += effect.getBossDamage();
                    }
                    break;
                }
            }
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.SmashStack)) != null) {
            if (buff >= 100) {
                this.speed += 5;
                this.jump += 10;
                this.stanceProp += 20;
            } else if (buff >= 300) {
                this.speed += 10;
                this.jump += 20;
                this.stanceProp += 40;
            }
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.SoulGazeCriDamR)) != null) {
            this.criticalDamage += (double)buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.AffinitySlug)) != null) {
            this.incDamR += (double)buff.intValue();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.Larkness)) != null && effect.getSourceId() == 20040219) {
            this.addSkillCooltimeReduce(27111303, 100);
            this.addSkillCooltimeReduce(27121303, 100);
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.FireAura)) != null) {
            this.mpcon_eachSecond -= effect.getMpCon();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.IceAura)) != null) {
            this.mpcon_eachSecond -= effect.getMpCon();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.BMageAuraYellow)) != null && JobConstants.is煉獄巫師(chra.getJob())) {
            this.mpcon_eachSecond -= effect.getMpCon();
        }
        if ((effect = chra.getEffectForBuffStat(SecondaryStat.Frenzy)) != null) {
            this.hpRecover_limit = effect.getW();
            this.percent_damage += (double)chra.getBuffedIntZ(SecondaryStat.Frenzy) + this.percent_damage * (double)chra.getBuffedIntZ(SecondaryStat.Frenzy) / 100.0;
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.ItemUpByItem)) != null) {
            this.dropBuff += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.DropRate)) != null) {
            this.dropBuff += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.ExpBuffRate)) != null) {
            this.expBuff += buff - 100;
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.PlusExpRate)) != null) {
            this.plusExpRate += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(SecondaryStat.OverloadMode)) != null) {
            this.mpRecover_limit = 0;
        }
    }

    public boolean checkEquipLevels(MapleCharacter chr, long gain) {
        boolean changed = false;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ArrayList<Equip> all = new ArrayList<Equip>(this.equipstats.equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(Math.min(eq.getItemEXP() + gain, Long.MAX_VALUE));
            if (eq.getEquipLevel() > lvlz) {
                for (int i = eq.getEquipLevel() - lvlz; i > 0; --i) {
                    Map<String, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                    short extra = eq.getYggdrasilWisdom();
                    if (extra == 1) {
                        inc.get(String.valueOf(lvlz + i - 1)).put("incSTRMin", 1);
                        inc.get(String.valueOf(lvlz + i - 1)).put("incSTRMax", 3);
                    } else if (extra == 2) {
                        inc.get(String.valueOf(lvlz + i - 1)).put("incDEXMin", 1);
                        inc.get(String.valueOf(lvlz + i - 1)).put("incDEXMax", 3);
                    } else if (extra == 3) {
                        inc.get(String.valueOf(lvlz + i - 1)).put("incINTMin", 1);
                        inc.get(String.valueOf(lvlz + i - 1)).put("incINTMax", 3);
                    } else if (extra == 4) {
                        inc.get(String.valueOf(lvlz + i - 1)).put("incLUKMin", 1);
                        inc.get(String.valueOf(lvlz + i - 1)).put("incLUKMax", 3);
                    }
                    if (inc != null && inc.containsKey(String.valueOf(lvlz + i - 1)) && inc.get(String.valueOf(lvlz + i - 1)) != null) {
                        eq = ii.levelUpEquip(eq, inc.get(String.valueOf(lvlz + i - 1)));
                    }
                    if (GameConstants.getStatFromWeapon(eq.getItemId()) != null || ItemConstants.getMaxLevel(eq.getItemId()) >= lvlz + i || !(Math.random() < 0.1) || eq.getIncSkill() > 0 || ii.getEquipSkills(eq.getItemId()) == null) continue;
                    for (int zzz : ii.getEquipSkills(eq.getItemId())) {
                        Client.skills.Skill skil = SkillFactory.getSkill(zzz);
                        if (skil == null || !skil.canBeLearnedBy(chr.getJobWithSub())) continue;
                        eq.setIncSkill(skil.getId());
                        chr.dropMessage(5, "武器：" + skil.getName() + " 已獲得新的等級提升！");
                    }
                }
                changed = true;
            }
            chr.forceUpdateItem(eq.copy());
        }
        if (changed) {
            chr.equipChanged();
            chr.send(EffectPacket.showItemLevelupEffect());
            chr.getMap().broadcastMessage(chr, EffectPacket.showForeignItemLevelupEffect(chr.getId()), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(MapleCharacter chr, int gain) {
        return this.checkEquipDurabilitys(chr, gain, false);
    }

    public boolean checkEquipDurabilitys(MapleCharacter chr, int gain, boolean aboveZero) {
        if (chr.inPVP()) {
            return true;
        }
        ArrayList<Equip> all = new ArrayList<Equip>(this.equipstats.durabilityHandling);
        for (Equip item : all) {
            if (item == null || item.getPosition() >= 0 != aboveZero) continue;
            item.setDurability(item.getDurability() + gain);
            if (item.getDurability() >= 0) continue;
            item.setDurability(0);
        }
        for (Equip eqq : all) {
            if (eqq != null && eqq.getDurability() == 0 && eqq.getPosition() < 0) {
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    chr.send(InventoryPacket.getInventoryFull());
                    chr.send(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                this.equipstats.durabilityHandling.remove(eqq);
                short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
                continue;
            }
            if (eqq == null) continue;
            chr.forceUpdateItem(eqq.copy());
        }
        return true;
    }

    public void checkEquipSealed(MapleCharacter chr, long gain) {
        ArrayList<Equip> all = new ArrayList<Equip>(this.equipstats.sealedEquipHandling);
        ArrayList<ModifyInventory> mods = new ArrayList<ModifyInventory>();
        for (Equip eqq : all) {
            if (eqq == null || GameConstants.canSealedLevelUp(eqq.getItemId(), eqq.getSealedLevel(), eqq.getSealedExp())) continue;
            eqq.gainSealedExp(gain);
            mods.add(new ModifyInventory(4, eqq));
        }
        chr.send(InventoryPacket.modifyInventory(true, mods, chr));
    }

    public void handleProfessionTool(MapleCharacter chra) {
        if (chra.getProfessionLevel(92000000) > 0 || chra.getProfessionLevel(92010000) > 0) {
            for (Item item : chra.getInventory(MapleInventoryType.EQUIP).newList()) {
                Equip equip = (Equip)item;
                if ((equip.getDurability() == 0 || equip.getItemId() / 10000 != 150 || chra.getProfessionLevel(92000000) <= 0) && (equip.getItemId() / 10000 != 151 || chra.getProfessionLevel(92010000) <= 0)) continue;
                if (equip.getDurability() > 0) {
                    this.equipstats.durabilityHandling.add(equip);
                }
                this.equipstats.harvestingTool = equip.getPosition();
                break;
            }
        }
    }

    private void CalcPassive_Mastery(MapleCharacter player, MapleWeapon wt) {
        int skil;
        if (wt == MapleWeapon.沒有武器) {
            this.passive_mastery = 0;
            return;
        }
        boolean acc = true;
        switch (wt) {
            case 弓: {
                skil = JobConstants.is破風使者(player.getJob()) ? 13100025 : 3100000;
                break;
            }
            case 拳套: {
                skil = player.getJob() >= 410 && player.getJob() <= 412 ? 4100000 : 14100023;
                break;
            }
            case 手杖: {
                skil = player.getTotalSkillLevel(24120006) > 0 ? 24120006 : 24100004;
                break;
            }
            case 加農砲: {
                skil = 5300005;
                break;
            }
            case 雙刀: 
            case 短劍: {
                skil = player.getJob() >= 430 && player.getJob() <= 434 ? 4300000 : 4200000;
                break;
            }
            case 弩: {
                skil = JobConstants.is末日反抗軍(player.getJob()) ? 33100000 : 3200000;
                break;
            }
            case 單手斧: 
            case 單手棍: {
                skil = JobConstants.is惡魔殺手(player.getJob()) ? 31100004 : (JobConstants.is聖魂劍士(player.getJob()) ? 11100025 : (JobConstants.is米哈逸(player.getJob()) ? 51100001 : (player.getJob() >= 110 && player.getJob() <= 112 ? 1100000 : 1200000)));
                break;
            }
            case 雙手斧: 
            case 單手劍: 
            case 雙手劍: 
            case 雙手棍: {
                skil = JobConstants.is凱撒(player.getJob()) ? 61100006 : (JobConstants.is聖魂劍士(player.getJob()) ? 11100025 : (JobConstants.is米哈逸(player.getJob()) ? 51100001 : (player.getJob() >= 110 && player.getJob() <= 112 ? 1100000 : 1200000)));
                break;
            }
            case 矛: {
                skil = JobConstants.is狂狼勇士(player.getJob()) ? 21100000 : 1300000;
                break;
            }
            case 槍: {
                skil = 1300000;
                break;
            }
            case 指虎: {
                skil = JobConstants.is閃雷悍將(player.getJob()) ? 15100023 : (JobConstants.is隱月(player.getJob()) ? 25100106 : 5100001);
                break;
            }
            case 火槍: {
                skil = JobConstants.is末日反抗軍(player.getJob()) ? 35100000 : 5200000;
                break;
            }
            case 雙弩槍: {
                skil = 23100005;
                break;
            }
            case 短杖: 
            case 長杖: {
                acc = false;
                skil = JobConstants.is末日反抗軍(player.getJob()) ? 32100006 : (player.getJob() <= 212 ? 2100006 : (player.getJob() <= 222 ? 2200006 : (player.getJob() <= 232 ? 2300006 : (player.getJob() <= 2000 ? 12100027 : 22110018))));
                break;
            }
            case 閃亮克魯: {
                acc = false;
                skil = 27100005;
                break;
            }
            case 靈魂射手: {
                skil = 65100003;
                break;
            }
            case 魔劍: {
                skil = 31200005;
                break;
            }
            case 能量劍: {
                skil = 36100006;
                break;
            }
            case 琉: {
                skil = 101000103;
                break;
            }
            case 璃: {
                skil = 101000203;
                break;
            }
            case 記憶長杖: {
                acc = false;
                skil = 172120000;
                break;
            }
            case ESP限製器: {
                acc = false;
                skil = 142100006;
                break;
            }
            case 鎖鍊: {
                skil = 64100005;
                break;
            }
            case 魔法護腕: {
                acc = false;
                skil = 152000006;
                break;
            }
            default: {
                this.passive_mastery = 0;
                return;
            }
        }
        this.trueMastery = wt.getBaseMastery();
        if (player.getSkillLevel(skil) <= 0) {
            return;
        }
        MapleStatEffect eff = player.getSkillEffect(skil);
        if (eff == null) {
            return;
        }
        this.passive_mastery = (byte)eff.getMastery();
        this.trueMastery += eff.getMastery();
        int n = -1;
        switch (player.getJob()) {
            case 112: {
                n = 1120003;
                break;
            }
            case 122: {
                n = 1220018;
                break;
            }
            case 132: {
                n = 1320018;
                break;
            }
            case 212: {
                n = 2121005;
                break;
            }
            case 222: {
                n = 2221005;
                break;
            }
            case 231: 
            case 232: {
                n = 2310008;
                break;
            }
            case 312: {
                n = 3120005;
                break;
            }
            case 322: {
                n = 3220004;
                break;
            }
            case 412: {
                n = 4120012;
                break;
            }
            case 422: {
                n = 4220012;
                break;
            }
            case 434: {
                n = 4340013;
                break;
            }
            case 512: {
                n = 5121015;
                break;
            }
            case 522: {
                n = 5220020;
                break;
            }
            case 532: {
                n = 5320009;
                break;
            }
            case 1112: {
                n = 11120007;
                break;
            }
            case 1212: {
                n = 12120009;
                break;
            }
            case 1312: {
                n = 13120006;
                break;
            }
            case 1412: {
                n = 14120005;
                break;
            }
            case 1512: {
                n = 15120006;
                break;
            }
            case 2112: {
                n = 21120001;
                break;
            }
            case 2217: 
            case 2218: {
                n = 22170071;
                break;
            }
            case 2312: {
                n = 23120009;
                break;
            }
            case 2412: {
                n = 24120006;
                break;
            }
            case 2512: {
                n = 25120113;
                break;
            }
            case 2712: {
                n = 27120007;
                break;
            }
            case 3112: {
                n = 31120008;
                break;
            }
            case 3212: {
                n = 32120016;
                break;
            }
            case 3312: {
                n = 33120000;
                break;
            }
            case 3512: {
                n = 35120000;
                break;
            }
            case 3612: {
                n = 36120006;
                break;
            }
            case 3712: {
                n = 37120010;
                break;
            }
            case 4212: {
                n = 42120009;
                break;
            }
            case 5112: {
                n = 51120001;
                break;
            }
            case 6112: {
                n = 61120012;
                break;
            }
            case 6412: {
                n = 64120008;
                break;
            }
            case 14212: {
                n = 142120013;
                break;
            }
            case 15212: {
                n = 152120015;
            }
        }
        if (n > 0 && (eff = player.getSkillEffect(n)) != null) {
            this.trueMastery -= eff.getMastery();
            this.trueMastery += eff.getMastery();
            if (n == 1220018) {
                switch (wt) {
                    case 單手棍: 
                    case 單手劍: {
                        this.trueMastery += 3;
                    }
                }
            }
        }
    }

    private void calculateFame(boolean first_login, MapleCharacter player) {
        player.getTrait(MapleTraitType.charm).addLocalExp(player.getFame());
        for (MapleTraitType t : MapleTraitType.values()) {
            MapleTrait trait = player.getTrait(t);
            if (!trait.recalcLevel() || first_login) continue;
            player.updateSingleStat(trait.getType().getStat(), trait.getLocalTotalExp());
        }
    }

    public short getCriticalRate() {
        this.lock.readLock().lock();
        try {
            short s = this.critRate;
            return s;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public double getCriticalDamage() {
        this.lock.readLock().lock();
        try {
            double d = this.criticalDamage;
            return d;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public byte passive_mastery() {
        return this.passive_mastery;
    }

    public double calculateMaxProjDamage(int projectileWatk, MapleCharacter chra) {
        int mainstat = 0;
        if (projectileWatk < 0) {
            return 0.0;
        }
        Item weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
        MapleWeapon weapon = weapon_item == null ? MapleWeapon.沒有武器 : MapleWeapon.getByItemID(weapon_item.getItemId());
        float maxProjDamage = weapon.getMaxDamageMultiplier(chra.getJob()) * (float)(4 * mainstat + (switch (weapon) {
            case MapleWeapon.弓, MapleWeapon.弩, MapleWeapon.火槍 -> {
                mainstat = this.localdex;
                yield this.localstr;
            }
            case MapleWeapon.拳套 -> {
                mainstat = this.localluk;
                yield this.localdex;
            }
            default -> {
                mainstat = 0;
                yield 0;
            }
        })) * ((float)projectileWatk / 100.0f);
        maxProjDamage = (float)((double)maxProjDamage + (double)maxProjDamage * (this.percent_damage / 100.0));
        return maxProjDamage;
    }

    private long calcBaseDamage(int mainStat, int secStat, int tertStat, int pad, double maxWeaponDamageMultiplier) {
        return (long)((double)((long)(tertStat + secStat) + 4L * (long)mainStat) / 100.0 * ((double)pad * maxWeaponDamageMultiplier));
    }

    private long calcHybridMaxDamage(int stat1, int stat2, int stat3, int stat4, int pad, double finalDamage) {
        return (long)(((double)stat1 * 3.5 + (double)stat2 * 3.5 + (double)stat3 * 3.5 + (double)stat4) / 100.0 * ((double)pad * finalDamage));
    }

    private long calcMaxDamageByHp(int rawHp, int totalHp, int str, int pad, double finalDamage) {
        return (long)(((double)((int)((double)rawHp / 3.5)) + 0.8 * (double)((int)((double)(totalHp - rawHp) / 3.5)) + (double)str) / 100.0 * ((double)pad * finalDamage));
    }

    public long getMinDamage(MapleCharacter chra) {
        return (long)((double)(this.getMaxDamage(chra) * (long)this.trueMastery) / 100.0);
    }

    public long getMaxDamage(MapleCharacter chra) {
        short job = chra.getJob();
        BaseStat mainStat = GameConstants.getMainStatForJob(job);
        int attStat = mainStat == BaseStat.inte ? this.mad : this.pad;
        int totalMainStat = 0;
        int totalSecStat = 0;
        if (JobConstants.is傑諾(job)) {
            return this.calcHybridMaxDamage(this.localstr, this.localdex, this.localluk, 0, attStat, this.equipstats.wt.getMaxDamageMultiplier(chra.getJob()));
        }
        switch (mainStat) {
            case str: {
                totalMainStat = this.localstr;
                totalSecStat = this.localdex;
                break;
            }
            case dex: {
                totalMainStat = this.localdex;
                totalSecStat = this.localstr;
                break;
            }
            case inte: {
                totalMainStat = this.localint;
                totalSecStat = this.localluk;
                break;
            }
            case luk: {
                totalMainStat = this.localluk;
                totalSecStat = this.localdex;
                break;
            }
            case mhp: {
                return this.calcMaxDamageByHp(this.localmaxhp, this.localmaxhp, this.localstr, attStat, this.equipstats.wt.getMaxDamageMultiplier(chra.getJob()));
            }
            default: {
                System.out.println("getMaxDamage錯誤");
            }
        }
        return (long)((double)this.calcBaseDamage(totalMainStat, totalSecStat, 0, attStat, this.equipstats.wt.getMaxDamageMultiplier(chra.getJob())) * this.percent_damage * (1.0 + this.incDamR / 100.0));
    }

    public void calculateMaxBaseDamage(MapleCharacter chra, MapleWeapon wt) {
        double damage;
        if (this.pad <= 0) {
            this.localbasedamage_max = 1L;
            this.localmaxbasepvpdamage = 1L;
            return;
        }
        short job = chra.getJob();
        int grade = JobConstants.getJobBranch(job);
        int ad = this.pad;
        int adfx = this.indiePadFX;
        if (grade == 2 && this.mad > 0) {
            ad = this.mad;
            adfx = this.indieMadFX;
        }
        if (grade != 2 || JobConstants.is凱內西斯(job) || JobConstants.is夜光(job)) {
            int addhp;
            int damstat = 0;
            switch (wt) {
                case 能量劍: {
                    damstat = (int)(3.5 * (double)(this.localstr + this.localdex + this.localluk));
                    break;
                }
                case 弓: 
                case 弩: 
                case 火槍: 
                case 雙弩槍: 
                case 靈魂射手: {
                    damstat = 4 * this.localdex + this.localstr;
                    break;
                }
                case 雙刀: 
                case 短劍: {
                    damstat = 4 * this.localluk + this.localdex + this.localstr;
                    if (grade == 4 || grade == 6) break;
                    damstat = this.localstr + this.localdex + this.localluk;
                    break;
                }
                case 拳套: 
                case 手杖: {
                    damstat = 4 * this.localluk + this.localdex;
                    if (JobConstants.is暗夜行者(job)) {
                        damstat = (int)((double)(4 * this.localluk) + 2.5 * (double)this.localdex);
                    }
                    if (job != 2003) break;
                    damstat = 4 * this.localstr + this.localdex;
                    break;
                }
                case 魔劍: {
                    damstat = 2 * this.getMaxHp() / 7 + this.localstr;
                    break;
                }
                default: {
                    damstat = grade == 2 ? 4 * this.localint + this.localluk : 4 * this.localstr + this.localdex;
                }
            }
            double weapondam = wt.getMaxDamageMultiplier(job);
            damage = weapondam * (double)ad * (double)damstat / 100.0;
            if (JobConstants.is惡魔復仇者(job) && (addhp = this.localmaxhp - this.getMaxHp()) > 0) {
                damage += weapondam * (double)ad * 2.0 * ((double)addhp / 7.0) / 100.0 * 0.8;
            }
        } else {
            damage = 1.0 * (double)ad * (double)(4 * this.localint + this.localluk) / 100.0;
        }
        damage = damage * (1.0 + this.incDamR / 100.0) * (1.0 + this.percent_damage / 100.0) + 0.5;
        damage += damage * (double)this.kannaLinkDamR / 100.0;
        this.localbasedamage_max = (long)(Math.floor(damage) + (double)adfx);
        this.localbasedamage_min = Math.round(damage * (double)this.trueMastery / 100.0) + (long)adfx;
    }

    public float getHealHP() {
        return this.healHPR;
    }

    public float getHealMP() {
        return this.healMPR;
    }

    public void relocHeal(MapleCharacter chra) {
        float recvRate;
        int lvl;
        Client.skills.Skill skill;
        short playerjob = chra.getJob();
        this.healHPR = 10 + this.recoverHP;
        this.healMPR = JobConstants.isNotMpJob(chra.getJob()) ? 0 : 3 + this.mpRestore + this.recoverMP + this.localint / 10;
        this.mpRecoverTime = 0;
        this.hpRecoverTime = 0;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (playerjob == 5111 || playerjob == 5112) {
            skill = SkillFactory.getSkill(51110000);
            int lvl2 = chra.getSkillLevel(skill);
            if (lvl2 > 0 && skill != null) {
                MapleStatEffect effect = skill.getEffect(lvl2);
                this.healHPR += effect.getHp();
                this.hpRecoverTime = 4000;
                this.healMPR += effect.getMp();
                this.mpRecoverTime = 4000;
            }
        } else if ((playerjob == 6111 || playerjob == 6112) && (lvl = chra.getSkillLevel(skill = SkillFactory.getSkill(61110006))) > 0 && skill != null) {
            MapleStatEffect effect = skill.getEffect(lvl);
            this.healHPR += effect.getX();
            this.hpRecoverTime = effect.getY();
            this.healMPR += effect.getX();
            this.mpRecoverTime = effect.getY();
        }
        if (chra.getChair() != null) {
            Pair<Integer, Integer> ret = ii.getChairRecovery(chra.getChair().getItemId());
            if (ret != null) {
                this.healHPR += ret.getLeft().intValue();
                if (this.hpRecoverTime == 0) {
                    this.hpRecoverTime = 4000;
                }
                this.healMPR += JobConstants.isNotMpJob(chra.getJob()) ? 0 : ret.getRight();
                if (this.mpRecoverTime == 0 && !JobConstants.isNotMpJob(chra.getJob())) {
                    this.hpRecoverTime = 4000;
                }
            }
        } else if (chra.getMap() != null && (recvRate = chra.getMap().getRecoveryRate()) > 0.0f) {
            this.healHPR = (int)((float)this.healHPR * recvRate);
            this.healMPR = (int)((float)this.healMPR * recvRate);
        }
    }

    public void zeroData(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, int mask, boolean beta) {
        if (JobConstants.is神之子(chr.getJob())) {
            mplew.writeShort(mask);
            if ((mask & 1) != 0) {
                mplew.writeBool(beta);
            }
            if ((mask & 2) != 0) {
                mplew.writeInt(this.betamaxhp);
            }
            if ((mask & 4) != 0) {
                mplew.writeInt(this.betamaxmp);
            }
            mplew.writeInt(0);
            if ((mask & 8) != 0) {
                mplew.write(chr.getSecondSkinColor());
            }
            if ((mask & 0x10) != 0) {
                mplew.writeInt(chr.getSecondHair());
            }
            if ((mask & 0x20) != 0) {
                mplew.writeInt(chr.getSecondFace());
            }
            if ((mask & 0x40) != 0) {
                mplew.writeInt(this.getMaxHp());
            }
            if ((mask & 0x80) != 0) {
                mplew.writeInt(this.getMaxMp());
            }
            if ((mask & 0x100) != 0) {
                String oneInfo = chr.getOneInfo(52999, "zeroMask");
                mplew.writeLong(oneInfo == null ? 0L : Long.parseLong(oneInfo));
            }
        }
    }

    public int getSkillIncrement(int skillID) {
        block5: {
            try {
                if (!this.lock.readLock().tryLock(1L, TimeUnit.SECONDS)) break block5;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                int n = this.equipstats.skillsIncrement.getOrDefault(skillID, 0);
                this.lock.readLock().unlock();
                return n;
            }
            catch (Throwable throwable) {
                this.lock.readLock().unlock();
                throw throwable;
            }
        }
        return 0;
    }

    public int getEquipmentSkill(int skillID) {
        block5: {
            try {
                if (!this.lock.readLock().tryLock(1L, TimeUnit.SECONDS)) break block5;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                int n = this.equipstats.equipmentSkills.getOrDefault(skillID, 0);
                this.lock.readLock().unlock();
                return n;
            }
            catch (Throwable throwable) {
                this.lock.readLock().unlock();
                throw throwable;
            }
        }
        return 0;
    }

    public int getElementBoost(Element key) {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.elemBoosts.getOrDefault((Object)key, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getSkillDamageIncrease(int key) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_damage.getOrDefault(key, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void heal_noUpdate(MapleCharacter chra) {
        this.setHp(this.getCurrentMaxHP(), chra);
        this.setMp(this.getCurrentMaxMP());
    }

    public void heal(MapleCharacter chra) {
        this.heal_noUpdate(chra);
        chra.updateSingleStat(MapleStat.HP, this.getCurrentMaxHP());
        chra.updateSingleStat(MapleStat.MP, this.getCurrentMaxMP());
    }

    public void recalcPVPRank(MapleCharacter chra) {
        this.pvpRank = 10;
        this.pvpExp = chra.getTotalBattleExp();
        for (int i = 0; i < 10; ++i) {
            if (this.pvpExp <= GameConstants.getPVPExpNeededForLevel(i + 1)) continue;
            --this.pvpRank;
            this.pvpExp -= GameConstants.getPVPExpNeededForLevel(i + 1);
        }
    }

    public int getLifeTidal() {
        return this.getHPPercent() >= this.getMPPercent() ? 2 : 1;
    }

    public int getHPPercent() {
        this.lock.readLock().lock();
        try {
            int n = this.localmaxhp > 0 ? this.hp * 100 / this.localmaxhp : 1;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getMPPercent() {
        this.lock.readLock().lock();
        try {
            int n = this.localmaxmp > 0 ? this.mp * 100 / this.localmaxmp : 1;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void init(MapleCharacter chra) {
        this.recalcLocalStats(chra);
    }

    public short getInt() {
        return this.int_;
    }

    public void setStr(short str, MapleCharacter chra) {
        this.str = str;
        this.recalcLocalStats(chra);
    }

    public void setDex(short dex, MapleCharacter chra) {
        this.dex = dex;
        this.recalcLocalStats(chra);
    }

    public void setLuk(short luk, MapleCharacter chra) {
        this.luk = luk;
        this.recalcLocalStats(chra);
    }

    public void setInt(short int_, MapleCharacter chra) {
        this.int_ = int_;
        this.recalcLocalStats(chra);
    }

    public int getHealHp() {
        return Math.max(this.localmaxhp - this.hp, 0);
    }

    public int getHealMp(int job) {
        if (JobConstants.isNotMpJob(job)) {
            return 0;
        }
        return Math.max(this.localmaxmp - this.mp, 0);
    }

    public void setHp(int newhp) {
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > this.localmaxhp) {
            thp = this.localmaxhp;
        }
        this.hp = thp;
    }

    public boolean setHp(int newhp, MapleCharacter chra) {
        return this.setHp(newhp, false, chra);
    }

    public boolean setHp(int newhp, boolean silent, MapleCharacter chra) {
        int oldHp = this.hp;
        this.setHp(newhp);
        if (chra != null && !silent) {
            chra.updatePartyMemberHP();
        }
        return this.hp != oldHp;
    }

    public boolean setMp(int newmp) {
        int oldMp = this.mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > this.localmaxmp) {
            tmp = this.localmaxmp;
        }
        this.mp = tmp;
        return this.mp != oldMp;
    }

    public void setInfo(int maxhp, int maxmp, int hp, int mp) {
        this.maxhp = maxhp;
        this.maxmp = maxmp;
        this.hp = hp;
        this.mp = mp;
    }

    public int getMaxHp() {
        return this.getMaxHp(true);
    }

    public int getMaxHp(boolean local) {
        if (local) {
            return Math.min(ServerConfig.CHANNEL_PLAYER_MAXHP, this.maxhp + this.apAddMaxHp);
        }
        return this.maxhp;
    }

    public int getMaxMp() {
        return this.getMaxMp(true);
    }

    public int getMaxMp(boolean local) {
        if (local) {
            return Math.min(ServerConfig.CHANNEL_PLAYER_MAXMP, this.maxmp + this.apAddMaxMp);
        }
        return this.maxmp;
    }

    public int getTotalDex() {
        this.lock.readLock().lock();
        try {
            int n = this.localdex;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getTotalInt() {
        this.lock.readLock().lock();
        try {
            int n = this.localint;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getTotalStr() {
        this.lock.readLock().lock();
        try {
            int n = this.localstr;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getTotalLuk() {
        this.lock.readLock().lock();
        try {
            int n = this.localluk;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getTotalMagic() {
        this.lock.readLock().lock();
        try {
            int n = this.mad;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getSpeed() {
        this.lock.readLock().lock();
        try {
            int n = this.speed;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getJump() {
        this.lock.readLock().lock();
        try {
            int n = this.jump;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getTotalWatk() {
        this.lock.readLock().lock();
        try {
            int n = this.pad;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getCurrentMaxHP() {
        this.lock.readLock().lock();
        try {
            int n = this.localmaxhp;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getCurrentMaxMP() {
        this.lock.readLock().lock();
        try {
            int n = this.localmaxmp;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getBetaMaxHP() {
        this.lock.readLock().lock();
        try {
            int n = this.betamaxhp;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void setBetaMaxHP(int hp) {
        this.lock.writeLock().lock();
        try {
            this.betamaxhp = hp;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public int getBetaMaxMP() {
        this.lock.readLock().lock();
        try {
            int n = this.betamaxmp;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public long getCurrentMaxBaseDamage() {
        this.lock.readLock().lock();
        try {
            long l = this.localbasedamage_max;
            return l;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public long getCurrentMinBaseDamage() {
        this.lock.readLock().lock();
        try {
            long l = this.localbasedamage_min;
            return l;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public float getCurrentMaxBasePVPDamage() {
        this.lock.readLock().lock();
        try {
            float f = this.localmaxbasepvpdamage;
            return f;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public float getCurrentMaxBasePVPDamageL() {
        this.lock.readLock().lock();
        try {
            float f = this.localmaxbasepvpdamageL;
            return f;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getStanceProp() {
        this.lock.readLock().lock();
        try {
            int n = this.stanceProp;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getCooltimeReduceR() {
        this.lock.readLock().lock();
        try {
            int n = this.cooltimeReduceR;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getSummonTimeR() {
        this.lock.readLock().lock();
        try {
            int n = this.summonTimeR;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getReduceCooltime() {
        this.lock.readLock().lock();
        try {
            int n = this.reduceCooltime;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getAttackCount(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_attackCount.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getMobCount(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_targetPlus.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getReduceCooltimeRate(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_coolTimeR.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public double getIgnoreMobpdpR() {
        this.lock.readLock().lock();
        try {
            double d = this.ignoreMobpdpR;
            return d;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double getIgnoreMobpdpR(int skillId) {
        this.lock.readLock().lock();
        try {
            int val = this.getSkillIgnoreMobpdpRate(skillId);
            double d = this.ignoreMobpdpR + (100.0 - this.ignoreMobpdpR) * ((double)val / 100.0);
            return d;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public double getDamageRate() {
        this.lock.readLock().lock();
        try {
            double d = this.incDamR;
            return d;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getBossDamageRate() {
        this.lock.readLock().lock();
        try {
            int n = this.bossDamageR;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getStarForce() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.starForce;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getArc() {
        this.lock.readLock().lock();
        try {
            int n = this.arc;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getSkillBossDamage(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_bossDamage.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getDuration(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_duration.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getDotTime(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_dotTime.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addSkillDamageIncrease(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_damage.merge(skillId, val, (a, b) -> a + b);
    }

    public void addSkillDamageIncrease_5th(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_damage_5th.merge(skillId, val, (a, b) -> a + b);
    }

    public int getSkillDamageIncrease_5th(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_damage_5th.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addSkillTargetPlus(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_targetPlus.merge(skillId, val, (a, b) -> a + b);
    }

    public void addSkillAttackCount(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_attackCount.merge(skillId, val, (a, b) -> a + b);
    }

    public void addSkillBossDamage(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_bossDamage.merge(skillId, val, (a, b) -> a + b);
    }

    public void addIgnoreMobpdpRate(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_ignoreMobpdpR.merge(skillId, val, (a, b) -> a + b);
    }

    public int getSkillIgnoreMobpdpRate(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_ignoreMobpdpR.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addSkillDuration(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_duration.merge(skillId, val, (a, b) -> a + b);
    }

    public void addSkillCustomVal(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_custom_val.merge(skillId, val, (a, b) -> a + b);
    }

    public int getSkillCustomVal(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_custom_val.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addDotTime(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_dotTime.merge(skillId, val, (a, b) -> a + b);
    }

    public void addSkillCooltimeReduce(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_coolTimeR.merge(skillId, val, (a, b) -> a + b);
    }

    public void addSkillProp(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_prop.merge(skillId, val, (a, b) -> a + b);
    }

    public int getGauge_x() {
        return this.gauge_x;
    }

    public int getAddSkillProp(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_prop.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addDamAbsorbShieldR(int val) {
        this.damAbsorbShieldR += (100.0 - this.damAbsorbShieldR) * ((double)val / 100.0);
    }

    public void addIgnoreMobpdpR(double val) {
        this.ignoreMobpdpR += (100.0 - this.ignoreMobpdpR) * (val / 100.0);
    }

    public double getIncFinalDamage() {
        this.lock.readLock().lock();
        try {
            double d = this.incFinalDamage;
            return d;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addIncFinalDamage(double val) {
        this.incFinalDamage = this.incFinalDamage * (100.0 + val) / 100.0;
    }

    public void addSkillBulletCount(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_bulletCount.merge(skillId, val, (a, b) -> a + b);
    }

    public int getSkillBulletCount(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.add_skill_bulletCount.getOrDefault(skillId, 0);
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getIncBuffTime() {
        this.lock.readLock().lock();
        try {
            int n = this.incBuffTime;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getFinalAttackSkill() {
        this.lock.readLock().lock();
        try {
            int n = this.finalAttackSkill;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Pair<Integer, Integer> getEquipSummon() {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(0, 0);
        this.lock.readLock().lock();
        try {
            List<Integer> equipSummons = this.equipstats.equipSummons;
            if (this.summoned > 0 && !equipSummons.contains(this.summoned)) {
                pair = new Pair<Integer, Integer>(this.summoned, 0);
                this.summoned = 0;
            } else if (this.summoned == 0 && !equipSummons.isEmpty()) {
                this.summoned = equipSummons.get(0);
                pair = new Pair<Integer, Integer>(this.summoned, 1);
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        return pair;
    }

    public String toString() {
        return "PlayerStats{ignoreMobpdpR=" + this.ignoreMobpdpR + ", incDamR=" + this.incDamR + ", bossDamageR=" + this.bossDamageR + ", localstr=" + this.localstr + ", localdex=" + this.localdex + ", localluk=" + this.localluk + ", localint=" + this.localint + ", localmaxhp=" + this.localmaxhp + ", localmaxmp=" + this.localmaxmp + ", localbasedamage_max=" + this.localbasedamage_max + ", localbasedamage_min=" + this.localbasedamage_min + "}";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getSkillReduceForceCon(int skillId) {
        this.lock.readLock().lock();
        try {
            int n = this.reduceForceR;
            if (this.add_skill_reduceForceCon.containsKey(skillId)) {
                n += this.add_skill_reduceForceCon.get(skillId).intValue();
            }
            int n2 = Math.min(100, n);
            return n2;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addSkillReduceForceCon(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        this.add_skill_reduceForceCon.merge(skillId, val, (a, b) -> a + b);
    }

    private boolean canNextRecalcStat() {
        long curr = System.currentTimeMillis();
        if (curr > this.nextRecalcStats) {
            this.nextRecalcStats = curr + 2000L;
            return true;
        }
        return false;
    }

    public int getRecallRingId() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.recallRingId;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getElementDef() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.element_def;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getElementIce() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.element_ice;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getElementFire() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.element_fire;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getElementLight() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.element_light;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getElementPsn() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.element_psn;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Integer, Pair<Integer, Integer>> getHPRecoverItemOption() {
        this.lock.readLock().lock();
        try {
            Map<Integer, Pair<Integer, Integer>> map = this.equipstats.hpRecover_itemOption;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Integer, Pair<Integer, Integer>> getMPRecoverItemOption() {
        this.lock.readLock().lock();
        try {
            Map<Integer, Pair<Integer, Integer>> map = this.equipstats.mpRecover_itemOption;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Integer, Pair<Integer, Integer>> getDamageReflect() {
        this.lock.readLock().lock();
        try {
            Map<Integer, Pair<Integer, Integer>> map = this.equipstats.DAMreflect;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getPassivePlus() {
        try {
            this.lock.readLock().tryLock(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            int n = this.equipstats.passivePlus;
            this.lock.readLock().unlock();
            return n;
        }
        catch (Throwable throwable) {
            this.lock.readLock().unlock();
            throw throwable;
        }
    }

    public int getHarvestingTool() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.harvestingTool;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean canFish() {
        this.lock.readLock().lock();
        try {
            boolean bl = this.equipstats.canFish;
            return bl;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean canFishVIP() {
        this.lock.readLock().lock();
        try {
            boolean bl = this.equipstats.canFishVIP;
            return bl;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Integer, List<Integer>> getEquipmentBonusExps() {
        this.lock.readLock().lock();
        try {
            Map map = this.equipstats.equipmentBonusExps;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getQuestBonus() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.questBonus;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int getLevelBonus() {
        this.lock.readLock().lock();
        try {
            int n = this.equipstats.levelBonus;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Generated
    public short getStr() {
        return this.str;
    }

    @Generated
    public short getDex() {
        return this.dex;
    }

    @Generated
    public short getLuk() {
        return this.luk;
    }

    @Generated
    public short getInt_() {
        return this.int_;
    }

    @Generated
    public int getHp() {
        return this.hp;
    }

    @Generated
    public int getMp() {
        return this.mp;
    }

    @Generated
    public int getIncEXPr() {
        return this.incEXPr;
    }

    @Generated
    public int getAut() {
        return this.aut;
    }

    public static enum RecalcFlag {
        FirstLogin,
        Equip;

        final int flag = 1 << this.ordinal();

        final boolean check(int n) {
            return (n & this.flag) != 0;
        }
    }
}

