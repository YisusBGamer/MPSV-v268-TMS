/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.client.party.Party
 *  SwordieX.client.party.PartyMember
 *  SwordieX.field.fieldeffect.FieldEffect
 *  connection.packet.FieldPacket
 */
package Net.server.buffs;

import Client.MapleCharacter;
import Client.MapleCoolDownValueHolder;
import Client.MapleTraitType;
import Client.MonsterEffectHolder;
import Client.Reborn;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.skills.SkillMesInfo;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.skills.handler.SkillClassFetcher;
import Client.stat.PlayerStats;
import Client.status.MonsterStatus;
import Config.configs.FireRangbConfig;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Config.constants.enums.UserChatMessageType;
import Net.auth.Auth;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffectFactory;
import Net.server.life.Element;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.MapleAffectedArea;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleSummon;
import Net.server.maps.SummonMovementType;
import Net.server.maps.TownPortal;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import Packet.BuffPacket;
import Packet.EffectPacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import Packet.SummonPacket;
import Packet.UIPacket;
import Server.channel.ChannelServer;
import Server.world.World;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import SwordieX.field.fieldeffect.FieldEffect;
import connection.packet.FieldPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Randomizer;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class MapleStatEffect
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private static final Logger log = LoggerFactory.getLogger(MapleStatEffect.class);
    Map<MapleStatInfo, Integer> info;
    Map<MapleStatInfo, Double> infoD;
    private int debuffTime = 0;
    private Map<MapleTraitType, Integer> traits;
    private boolean overTime;
    boolean partyBuff = false;
    boolean rangeBuff = false;
    private boolean notRemoved;
    private boolean notIncBuffDuration;
    private EnumMap<SecondaryStat, Integer> statups;
    private List<Pair<Integer, Integer>> availableMap;
    private Map<MonsterStatus, Integer> monsterStatus;
    private Point lt;
    private Point rb;
    private Point lt2;
    private Point rb2;
    private Point lt3;
    private Point rb3;
    private int level;
    private List<SecondaryStat> cureDebuffs;
    private List<Integer> petsCanConsume;
    private List<Integer> familiars;
    private List<Integer> randomPickup;
    private List<Triple<Integer, Integer, Integer>> rewardItem;
    private byte slotCount;
    private byte slotPerLine;
    private byte recipeUseCount;
    private byte recipeValidDay;
    private byte reqSkillLevel;
    private byte effectedOnAlly;
    private byte effectedOnEnemy;
    private byte type;
    private byte preventslip;
    private byte immortal;
    private byte bs;
    private short ignoreMob;
    private short mesoR;
    private short thaw;
    private short lifeId;
    private short imhp;
    private short immp;
    private short inflation;
    private short useLevel;
    private short indiePdd;
    private short indieMdd;
    private short mobSkill;
    private short mobSkillLevel;
    private double hpR;
    private double mpR;
    private int sourceid;
    private int recipe;
    private int moveTo;
    private int moneyCon;
    private int morphId = 0;
    private int expinc;
    private int exp;
    private int consumeOnPickup;
    private int charColor;
    private int interval;
    private int rewardMeso;
    private int totalprob;
    private int cosmetic;
    private int expBuff;
    private int itemup;
    private int mesoup;
    private int cashup;
    private int berserk;
    private int illusion;
    private int booster;
    private int berserk2;
    private boolean ruleOn;
    private boolean bxi = false;
    private boolean hit;

    public void applyPassive(MapleCharacter applyto, MapleMapObject obj) {
        if (this.makeChanceResult()) {
            switch (this.sourceid) {
                case 0x200B20: 
                case 2200000: 
                case 2300000: {
                    int absorbMp;
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    MapleMonster mob = (MapleMonster)obj;
                    if (mob.getStats().isBoss() || (absorbMp = Math.min((int)((double)mob.getMobMaxMp() * ((double)this.getX() / 100.0)), mob.getMp())) <= 0) break;
                    mob.setMp(mob.getMp() - absorbMp);
                    applyto.getStat().setMp(applyto.getStat().getMp() + absorbMp);
                    applyto.getClient().announce(EffectPacket.encodeUserEffectLocal(this.sourceid, EffectOpcode.UserEffect_SkillUse, applyto.getLevel(), this.level));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.onUserEffectRemote(applyto, this.sourceid, EffectOpcode.UserEffect_SkillUse, applyto.getLevel(), this.level), false);
                }
            }
        }
    }

    public boolean applyTo(MapleCharacter chr) {
        return this.applyTo(chr, chr, true, null, this.getBuffDuration(chr), false);
    }

    public boolean applyTo(MapleCharacter chr, int duration) {
        return this.applyTo(chr, chr, true, null, duration, false);
    }

    public boolean applyTo(MapleCharacter chr, boolean passive) {
        return this.applyTo(chr, chr, true, null, this.getBuffDuration(chr), passive);
    }

    public boolean unprimaryApplyTo(MapleCharacter chr, Point pos) {
        return this.applyTo(chr, chr, false, pos, this.getBuffDuration(chr), false);
    }

    public boolean unprimaryApplyTo(MapleCharacter chr, Point pos, boolean passive) {
        return this.applyTo(chr, chr, false, pos, this.getBuffDuration(chr), passive);
    }

    public boolean unprimaryPassiveApplyTo(MapleCharacter chr) {
        return this.applyTo(chr, chr, false, null, this.getBuffDuration(chr), true);
    }

    public boolean attackApplyTo(MapleCharacter chr, boolean passive, Point pos) {
        return this.applyTo(chr, chr, this.getBuffDuration(chr), false, true, passive, pos);
    }

    public boolean applyTo(MapleCharacter chr, Point pos, boolean passive) {
        return this.applyTo(chr, chr, true, pos, this.getBuffDuration(chr), passive);
    }

    public boolean applyTo(MapleCharacter chr, Point pos) {
        return this.applyTo(chr, chr, this.getBuffDuration(chr), true, false, false, pos);
    }

    public boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos, int newDuration) {
        return this.applyTo(applyfrom, applyto, primary, pos, newDuration, false);
    }

    public boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos, int newDuration, boolean passive) {
        return this.applyTo(applyfrom, applyto, newDuration, primary, false, passive, pos);
    }

    public boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, int newDuration, boolean primary, boolean att, boolean passive, Point pos) {
        block114: {
            block115: {
                int soulMpCon;
                int debuffDuration;
                int ppRecovery;
                int powerCon;
                int fixCoolTime;
                int sourceid = this.getSourceId();
                if (sourceid == 9001004 && applyto.isHidden()) {
                    applyto.cancelEffect(this, false, -1L);
                    return true;
                }
                int cooldown = this.getCooldown(applyfrom);
                int level = this.level;
                PlayerStats playerStats = applyfrom.getStat();
                int itemConNo = this.info.get((Object)MapleStatInfo.itemConNo);
                int itemCon = this.info.get((Object)MapleStatInfo.itemCon);
                AbstractSkillHandler sh = this.getSkillHandler();
                int result = -1;
                if (sh != null) {
                    SkillClassApplier applier = new SkillClassApplier();
                    applier.effect = this;
                    applier.duration = newDuration;
                    applier.primary = primary;
                    applier.att = att;
                    applier.passive = passive;
                    applier.cooldown = cooldown;
                    applier.pos = pos;
                    result = sh.onApplyTo(applyfrom, applyto, applier);
                    if (result == 0) {
                        return false;
                    }
                    if (result == 1) {
                        newDuration = applier.duration;
                        primary = applier.primary;
                        att = applier.att;
                        passive = applier.passive;
                        cooldown = applier.cooldown;
                        pos = applier.pos;
                    }
                }
                if (itemConNo != 0 && !applyto.inPVP()) {
                    if (!applyto.haveItem(itemCon, itemConNo, false, true)) {
                        return false;
                    }
                    MapleInventoryManipulator.removeById(applyto.getClient(), ItemConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
                }
                boolean rapidAttack = SkillConstants.isRapidAttackSkill(sourceid);
                if (!passive) {
                    int mpChange;
                    int hpChange;
                    int hpHeal = 0;
                    int hpcost = 0;
                    int mpHeal = 0;
                    int mpcost = 0;
                    boolean busihua2 = applyfrom.getBuffedValue(SecondaryStat.BanMap) != null;
                    int effhp = this.getHp();
                    if (effhp != 0 && sourceid != 2321007 && sourceid != 2301002 && sourceid != 400021070) {
                        if (!this.isSkill()) {
                            hpHeal += this.alchemistModifyVal(applyfrom, effhp, true);
                            if (busihua2) {
                                hpHeal /= 2;
                            }
                        } else {
                            hpHeal += MapleStatEffectFactory.makeHealHP((double)effhp / 100.0, playerStats.getTotalMagic(), 3.0, 5.0);
                            if (busihua2) {
                                hpHeal = -hpHeal;
                            }
                        }
                    }
                    if (this.hpR != 0.0) {
                        hpHeal += this.getHpMpChange(applyfrom, true) / (busihua2 ? 2 : 1);
                    }
                    MapleStatEffect eff = applyfrom.getEffectForBuffStat(SecondaryStat.Thaw);
                    if (!(this.getHpRCon() == 0 || eff != null && eff.isSkill())) {
                        hpcost += (int)((double)(this.getHpRCon() * playerStats.getCurrentMaxHP()) / 100.0);
                    }
                    if (!(this.getHpCon() == 0 || eff != null && eff.isSkill())) {
                        hpcost += this.getHpCon();
                    }
                    if (JobConstants.isNotMpJob(applyfrom.getJob()) || applyfrom.getBuffedIntValue(SecondaryStat.OverloadMode) > 0) {
                        mpHeal = 0;
                    } else {
                        int effmp = this.getMp();
                        if (effmp != 0) {
                            mpHeal += this.alchemistModifyVal(applyfrom, effmp, true);
                        }
                        if (this.mpR != 0.0) {
                            mpHeal += this.getHpMpChange(applyfrom, false);
                        }
                    }
                    int mpCon = this.getMpCon();
                    if (JobConstants.is惡魔殺手(applyfrom.getJob())) {
                        int forceCon = this.getForceCon(applyfrom);
                        mpcost = applyfrom.getBuffedValue(SecondaryStat.InfinityForce) != null ? 0 : forceCon;
                    } else if (mpCon != 0 && (!JobConstants.is夜光(applyfrom.getJob()) || !this.isHit() || sourceid % 1000 / 100 != 2 || applyfrom.getBuffSource(SecondaryStat.Larkness) != 20040217 && applyfrom.getBuffSource(SecondaryStat.Larkness) != 20040219)) {
                        mpcost += (int)((double)(mpCon - mpCon * playerStats.mpconReduce / 100) * ((double)applyfrom.getStat().incMpCon / 100.0));
                    }
                    SecondaryStatValueHolder mb = applyfrom.getBuffStatValueHolder(SecondaryStat.TeleportMasteryOn);
                    if (sourceid == 2001009 && mb != null) {
                        mpcost += mb.effect.getY();
                    }
                    if (JobConstants.is凱殷(applyfrom.getJob())) {
                        switch (sourceid) {
                            case 63101100: 
                            case 63101104: 
                            case 63111103: 
                            case 63121102: 
                            case 63121140: {
                                applyfrom.dispelEffect(63101001);
                                break;
                            }
                            case 400031061: 
                            case 400031065: 
                            case 400031066: {
                                mpcost = 0;
                            }
                        }
                    }
                    if (JobConstants.is虎影(applyfrom.getJob()) && applyfrom.getSkillEffect(164000010) != null) {
                        int atGauge1Con = this.getAtGauge1Con();
                        int atGauge2Con = this.getAtGauge2Con();
                        int atGauge2Inc = this.getAtGauge2Inc();
                        int scrollDiff = 0;
                        if (atGauge2Con > 0) {
                            scrollDiff = -atGauge2Con;
                        } else if (atGauge2Inc > 0 && applyfrom.getSkillEffect(164110014) != null) {
                            scrollDiff = atGauge2Inc;
                        }
                        applyfrom.handleHoYoungValue(-atGauge1Con, scrollDiff);
                    }
                    if (applyfrom.getBuffedValue(SecondaryStat.Wizard_OverloadMana) != null) {
                        if (JobConstants.isNotMpJob(applyfrom.getJob())) {
                            hpcost += playerStats.getCurrentMaxHP() / 100;
                        } else {
                            mpcost += playerStats.getCurrentMaxMP() * 2 / 100;
                        }
                    }
                    if (hpHeal > 0 && !this.is血腥盛宴() && applyto.getEffectForBuffStat(SecondaryStat.Frenzy) != null) {
                        hpHeal = Math.min(playerStats.getCurrentMaxHP() / 100, hpHeal);
                    }
                    if (sourceid == 400011010 && applyto.getBuffedValue(SecondaryStat.Frenzy) != null) {
                        hpcost = 0;
                    }
                    if (hpcost > 0 && applyto.getStat().getHp() <= Math.abs(hpcost)) {
                        hpcost = 0;
                    }
                    if (applyfrom == applyto) {
                        hpChange = hpHeal - hpcost;
                        mpChange = mpHeal - mpcost;
                        if (hpcost > 0 && sourceid == 400011112) {
                            applyfrom.getTempValues().put("亡靈HP消耗", hpcost);
                        }
                    } else {
                        hpChange = hpHeal;
                        mpChange = mpHeal;
                    }
                    applyto.addHPMP(hpChange, Math.min(mpChange, applyto.getStat().getCurrentMaxMP() * applyto.getStat().mpRecover_limit / 100), !rapidAttack && !att);
                }
                if (!this.isSkill()) {
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    int value = ii.getItemProperty(-sourceid, "spec/charismaEXP", 0);
                    if (value > 0) {
                        applyto.getTrait(MapleTraitType.charisma).addExp(value, applyto);
                    }
                    if ((value = ii.getItemProperty(-sourceid, "spec/insightEXP", 0).intValue()) > 0) {
                        applyto.getTrait(MapleTraitType.insight).addExp(value, applyto);
                    }
                    if ((value = ii.getItemProperty(-sourceid, "spec/willEXP", 0).intValue()) > 0) {
                        applyto.getTrait(MapleTraitType.will).addExp(value, applyto);
                    }
                    if ((value = ii.getItemProperty(-sourceid, "spec/craftEXP", 0).intValue()) > 0) {
                        applyto.getTrait(MapleTraitType.craft).addExp(value, applyto);
                    }
                    if ((value = ii.getItemProperty(-sourceid, "spec/senseEXP", 0).intValue()) > 0) {
                        applyto.getTrait(MapleTraitType.sense).addExp(value, applyto);
                    }
                    if ((value = ii.getItemProperty(-sourceid, "spec/charmEXP", 0).intValue()) > 0) {
                        applyto.getTrait(MapleTraitType.charm).addExp(value, applyto);
                    }
                }
                int n = fixCoolTime = this.statups == null ? 0 : this.statups.getOrDefault(SecondaryStat.FixCoolTime, 0);
                if (fixCoolTime > 0) {
                    List<MapleCoolDownValueHolder> coolDowns = applyto.getCooldowns();
                    for (MapleCoolDownValueHolder mc : coolDowns) {
                        Skill skil;
                        int leftTime = mc.getLeftTime();
                        if (leftTime <= fixCoolTime || (skil = SkillFactory.getSkill(mc.skillId)) == null || skil.isVSkill()) continue;
                        applyto.reduceSkillCooldown(mc.skillId, leftTime - fixCoolTime);
                    }
                }
                if (primary && (powerCon = this.getPowerCon()) > 0 && applyfrom.getBuffedValue(SecondaryStat.AmaranthGenerator) == null) {
                    if (applyfrom.getBuffedIntValue(SecondaryStat.SurplusSupply) < powerCon) {
                        return false;
                    }
                    applyfrom.applyXenonEnegy(-powerCon);
                }
                if ((ppRecovery = this.info.get((Object)MapleStatInfo.ppRecovery).intValue()) > 0) {
                    applyto.handlePPCount(ppRecovery);
                }
                int ppCon = this.getPPCon();
                if ((sourceid == 142101003 || sourceid == 400021074) && ppCon > 0) {
                    applyto.handlePPCount(-ppCon);
                }
                if (this.isReturnScroll()) {
                    this.applyReturnScroll(applyto);
                }
                if (this.recipe > 0) {
                    if (applyto.getSkillLevel(this.recipe) > 0 || applyto.getProfessionLevel(this.recipe / 10000 * 10000) < this.reqSkillLevel) {
                        return false;
                    }
                    applyto.changeSingleSkillLevel(SkillFactory.getCraft(this.recipe), Integer.MAX_VALUE, (int)this.recipeUseCount, this.recipeValidDay > 0 ? System.currentTimeMillis() + (long)this.recipeValidDay * 24L * 60L * 60L * 1000L : -1L);
                }
                Skill skill = null;
                if (this.isSkill()) {
                    skill = SkillFactory.getSkill(sourceid);
                }
                if ((this) instanceof MobSkill) {
                    MobSkill mSkill = (MobSkill)this;
                    if (mSkill.getEmotion() != -1) {
                        applyto.send(UIPacket.UserEmotionLocal(mSkill.getEmotion(), newDuration));
                    }
                    for (SecondaryStat stat : this.statups.keySet()) {
                        if (stat == SecondaryStat.GiantBossDeathCnt || applyto.getBuffedValue(stat) == null) continue;
                        return false;
                    }
                }
                if (this instanceof MobSkill || primary || (att || passive) && !rapidAttack && skill != null && !skill.isChargeSkill()) {
                    this.applyBuffEffect(applyfrom, applyto, newDuration, primary, att, passive, pos);
                }
                if (this.is時空門()) {
                    applyto.removeAllTownPortal();
                    applyto.notifyChanges();
                    applyto.setTownPortalLeaveTime(System.currentTimeMillis() + (long)this.info.get((Object)MapleStatInfo.time).intValue());
                    TownPortal townPortal = new TownPortal(applyto, sourceid);
                    if (townPortal.getTownPortal() != null) {
                        applyto.getMap().spawnTownPortal(townPortal);
                        applyto.addTownPortal(townPortal);
                        TownPortal townPortalInTown = new TownPortal(townPortal);
                        applyto.addTownPortal(townPortalInTown);
                        townPortalInTown.getTownMap().spawnTownPortal(townPortalInTown);
                        applyto.notifyChanges();
                    } else {
                        applyto.dropMessage(5, "無法使用時空門，村莊不可容納。");
                    }
                }
                if ((debuffDuration = this.getMobDebuffDuration(applyfrom)) > 0 && debuffDuration != 2100000000 && primary && !this.monsterStatus.isEmpty() && this.getMobCount() > 0 && (sourceid == 80011540 || this.getAttackCount() <= 0) && this.info.get((Object)MapleStatInfo.hcReflect) <= 0 && sourceid != 5311004) {
                    this.applyToMonster(applyfrom, debuffDuration);
                }
                if (primary && this.isMist()) {
                    this.applyAffectedArea(applyto, pos);
                }
                if (this.cureDebuffs != null) {
                    for (SecondaryStat stat : this.cureDebuffs) {
                        applyfrom.dispelEffect(stat);
                    }
                }
                if (this.is楓葉淨化()) {
                    applyto.dispelEffect(SecondaryStat.BanMap);
                    applyto.dispelEffect(SecondaryStat.Attract);
                    applyto.dispelEffect(SecondaryStat.StopPortion);
                    applyto.dispelEffect(SecondaryStat.DispelItemOption);
                    applyto.dispelEffect(SecondaryStat.ReverseInput);
                }
                if (sourceid == 5311004) {
                    int value = applyto.getBuffedIntValue(SecondaryStat.Roulette);
                    cooldown = value == 2 ? 0 : (cooldown /= 2);
                }
                if (80011492 == sourceid) {
                    Equip eq = null;
                    for (Item item : applyfrom.getInventory(MapleInventoryType.EQUIPPED).listById(0x110120)) {
                        if (!((Equip)item).isMvpEquip()) continue;
                        eq = (Equip)item;
                        break;
                    }
                    int maxStep = 10;
                    boolean canBurningAllField = false;
                    int enhanceNum = 0;
                    if (eq != null) {
                        boolean forever;
                        canBurningAllField = true;
                        boolean bl = forever = eq.getExpiration() < 0L;
                        if (!forever && eq.getStarForceLevel() < 10) {
                            enhanceNum = 1;
                            cooldown = FireRangbConfig.FIRE_MAP_COOLDOWN_1_9;
                            maxStep = FireRangbConfig.FIRE_MAP_STAGE_1_9;
                        } else if (!forever && eq.getStarForceLevel() < 15) {
                            enhanceNum = 10;
                            cooldown = FireRangbConfig.FIRE_MAP_COOLDOWN_10_14;
                            maxStep = FireRangbConfig.FIRE_MAP_STAGE_10_14;
                        } else if (!forever && eq.getStarForceLevel() < 20) {
                            enhanceNum = 15;
                            cooldown = FireRangbConfig.FIRE_MAP_COOLDOWN_15_19;
                            maxStep = FireRangbConfig.FIRE_MAP_STAGE_15_19;
                        } else if (!forever && eq.getStarForceLevel() < 25) {
                            enhanceNum = 20;
                            cooldown = FireRangbConfig.FIRE_MAP_COOLDOWN_20_24;
                            maxStep = FireRangbConfig.FIRE_MAP_STAGE_20_24;
                        } else if (!forever && eq.getStarForceLevel() >= 25) {
                            enhanceNum = 25;
                            cooldown = FireRangbConfig.FIRE_MAP_COOLDOWN_25_29;
                            maxStep = FireRangbConfig.FIRE_MAP_STAGE_25_29;
                        } else if (!forever && eq.getStarForceLevel() >= 30) {
                            enhanceNum = 30;
                            cooldown = FireRangbConfig.FIRE_MAP_COOLDOWN_30;
                            maxStep = FireRangbConfig.FIRE_MAP_STAGE_30;
                        } else {
                            enhanceNum = 31;
                            cooldown = FireRangbConfig.FIRE_MAP_COOLDOWN_30;
                            maxStep = FireRangbConfig.FIRE_MAP_STAGE_30;
                        }
                    }
                    if (applyfrom.getMap() == null) {
                        return false;
                    }
                    if (!canBurningAllField && !applyfrom.getMap().isBreakTimeField()) {
                        applyfrom.dropSpouseMessage(UserChatMessageType.系統, "只能在燃燒場地內使用。");
                        return false;
                    }
                    if (applyfrom.getMap().getBreakTimeFieldStep() >= maxStep) {
                        applyfrom.dropSpouseMessage(UserChatMessageType.系統, "只能在低於" + maxStep + "階段的燃燒場地內使用。");
                        return false;
                    }
                    if (enhanceNum > 0) {
                        cooldown *= 1000;
                    }
                    applyfrom.dropSpouseMessage(UserChatMessageType.系統, "系統檢測觸發:釋放燃燒之戒-冷卻30分鐘！");
                    applyfrom.send(EffectPacket.onUserEffectRemote(null, this.getSourceId(), EffectOpcode.UserEffect_SkillUse, applyfrom.getLevel(), this.getLevel()));
                    applyfrom.getMap().broadcastMessage(applyfrom, EffectPacket.onUserEffectRemote(applyfrom, this.getSourceId(), EffectOpcode.UserEffect_SkillUse, applyfrom.getLevel(), this.getLevel()), false);
                    applyfrom.getMap().broadcastMessage(applyfrom, FieldPacket.fieldEffect((FieldEffect)FieldEffect.playSound((String)"Sound/FieldSkill.img/100011/1/laser", (int)100, (int)0, (int)0)), true);
                    applyfrom.getMap().setBreakTimeFieldStep(maxStep);
                    applyfrom.getMap().updateBreakTimeField();
                    applyfrom.getMap().broadcastMessage(applyfrom.getMap().getBreakTimeFieldStepPacket());
                }
                if (sourceid == 400011038) {
                    cooldown = 0;
                } else if (sourceid == 400051000) {
                    if (passive) {
                        cooldown = 0;
                    }
                } else if (80011540 == sourceid) {
                    Item eq = null;
                    for (Item item : applyfrom.getInventory(MapleInventoryType.EQUIPPED).listById(1033000)) {
                        if (!((Equip)item).isMvpEquip()) continue;
                        eq = (Equip)item;
                        break;
                    }
                    if (eq != null) {
                        boolean forever;
                        int enhanceNum = 0;
                        boolean bl = forever = eq.getExpiration() < 0L;
                        if (!forever && !applyfrom.isSilverMvp() || ((Equip)eq).getStarForceLevel() < 15) {
                            enhanceNum = 1;
                            cooldown = 105;
                        } else if (!forever && !applyfrom.isGoldMvp() || ((Equip)eq).getStarForceLevel() < 20) {
                            enhanceNum = 15;
                            cooldown = 100;
                        } else if (!forever && !applyfrom.isDiamondMvp() || ((Equip)eq).getStarForceLevel() < 25) {
                            enhanceNum = 20;
                            cooldown = 95;
                        } else {
                            enhanceNum = 25;
                            cooldown = 90;
                        }
                        if (enhanceNum > 0) {
                            // empty if block
                        }
                        cooldown *= 1000;
                    }
                } else if (80011273 == sourceid && Auth.checkPermission("MVPEquip_1113220")) {
                    Item eq = null;
                    for (Item item : applyfrom.getInventory(MapleInventoryType.EQUIPPED).listById(1113220)) {
                        if (!((Equip)item).isMvpEquip()) continue;
                        eq = (Equip)item;
                        break;
                    }
                    if (eq != null) {
                        boolean forever;
                        boolean bl = forever = eq.getExpiration() < 0L;
                        cooldown = !forever && !applyfrom.isSilverMvp() || ((Equip)eq).getStarForceLevel() < 15 ? 4 : (!forever && !applyfrom.isGoldMvp() || ((Equip)eq).getStarForceLevel() < 20 ? 3 : (!forever && !applyfrom.isDiamondMvp() || ((Equip)eq).getStarForceLevel() < 25 ? 2 : 0));
                        cooldown *= 1000;
                    }
                }
                if ((skill == null || !skill.isChargeSkill() || !SkillConstants.isKeydownSkillCancelGiveCD(sourceid) && applyfrom.getKeyDownSkill_Time() == 0L) && (att && !passive || primary) && applyfrom == applyto && cooldown > 0 && !applyfrom.isSkillCooling(sourceid)) {
                    applyfrom.registerSkillCooldown(SkillConstants.getCooldownLinkSourceId(sourceid), cooldown, true);
                }
                if (primary && (soulMpCon = this.getSoulMpCon()) > 0) {
                    if (applyto.getSoulMP() < (ServerConfig.JMS_SOULWEAPON_SYSTEM ? applyto.getMaxSoulMP() : soulMpCon)) {
                        return false;
                    }
                    applyto.checkSoulState(true);
                }
                if (applyfrom != applyto) break block114;
                if (!this.isRangeBuff()) break block115;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersInRect(this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft()))) {
                    if (applyfrom.getId() == chr.getId()) continue;
                    this.applyTo(applyfrom, chr, newDuration, primary, att, passive, pos);
                }
                break block114;
            }
            Party party = applyfrom.getParty();
            if (party == null || !this.isPartyBuff()) break block114;
            Rectangle rect = this.calculateBoundingBox(pos != null ? pos : applyfrom.getPosition(), applyfrom.isFacingLeft());
            for (PartyMember member : party.getMembers()) {
                if (member.getCharID() == applyfrom.getId() || member.getChr() == null || member.getChr().getMap() != applyfrom.getMap() || !rect.contains(member.getChr().getPosition()) || !member.getChr().isAlive()) continue;
                this.applyTo(applyfrom, member.getChr(), newDuration, primary, att, passive, pos);
            }
        }
        return true;
    }

    public void applyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, int newDuration, boolean primary, boolean att, boolean passive, Point pos) {
        MapleStatEffect effect;
        int sourceid = this.getSourceId();
        int level = this.getLevel();
        if (!primary && SkillConstants.isRapidAttackSkill(sourceid) || passive && !primary && this.getSummonMovementType() != null && this.statups.size() <= 1) {
            return;
        }
        if (this instanceof MobSkill && (this.statups.containsKey(SecondaryStat.ReverseInput) || this.statups.containsKey(SecondaryStat.Seal)) && (effect = applyto.getSkillEffect(80011158)) != null) {
            effect.unprimaryPassiveApplyTo(applyto);
            return;
        }
        int localDuration = newDuration;
        int maskedDuration = 0;
        Map<SecondaryStat, Integer> localstatups = new EnumMap<SecondaryStat, Integer>(this.statups);
        Map<SecondaryStat, Integer> maskedstatups = new EnumMap<SecondaryStat, Integer>(SecondaryStat.class);
        Map<SecondaryStat, Pair<Integer, Integer>> sendstatups = new EnumMap<SecondaryStat, Pair<Integer, Integer>>(SecondaryStat.class);
        long currentTimeMillis = System.currentTimeMillis();
        long startChargeTime = 0L;
        int direction = 1;
        boolean b3 = false;
        boolean b4 = true;
        boolean b5 = true;
        boolean overwrite = true;
        boolean cancelEffect = true;
        boolean b7 = true;
        boolean applySummon = true;
        int buffz = this.getZ();
        AbstractSkillHandler sh = this.getSkillHandler();
        if (sh == null) {
            sh = SkillClassFetcher.getHandlerByJob(applyto.getJobWithSub());
        }
        int result = -1;
        if (sh != null) {
            SkillClassApplier applier = new SkillClassApplier();
            applier.effect = this;
            applier.primary = primary;
            applier.att = att;
            applier.passive = passive;
            applier.pos = pos;
            applier.duration = localDuration;
            applier.maskedDuration = maskedDuration;
            applier.localstatups = localstatups;
            applier.maskedstatups = maskedstatups;
            applier.sendstatups = sendstatups;
            applier.startChargeTime = startChargeTime;
            applier.b3 = b3;
            applier.b4 = b4;
            applier.b5 = b5;
            applier.overwrite = overwrite;
            applier.cancelEffect = cancelEffect;
            applier.b7 = b7;
            applier.applySummon = applySummon;
            applier.buffz = buffz;
            result = sh.onApplyBuffEffect(applyfrom, applyto, applier);
            if (result == 0) {
                return;
            }
            if (result == 1) {
                primary = applier.primary;
                att = applier.att;
                passive = applier.passive;
                pos = applier.pos;
                localDuration = applier.duration;
                maskedDuration = applier.maskedDuration;
                localstatups = applier.localstatups;
                maskedstatups = applier.maskedstatups;
                sendstatups = applier.sendstatups;
                startChargeTime = applier.startChargeTime;
                b3 = applier.b3;
                b4 = applier.b4;
                b5 = applier.b5;
                overwrite = applier.overwrite;
                cancelEffect = applier.cancelEffect;
                b7 = applier.b7;
                applySummon = applier.applySummon;
                buffz = applier.buffz;
            }
        }
        if (result == -1) {
            switch (sourceid) {
                case -2023519: {
                    localstatups = Reborn.getStatups(applyto.getReborns());
                    localDuration = 2100000000;
                    break;
                }
                case 800: {
                    MapleStatEffect mapleStatEffect = this;
                    if (!(mapleStatEffect instanceof MobSkill)) break;
                    MobSkill effect2 = (MobSkill)mapleStatEffect;
                    localstatups.put(SecondaryStat.GiantBossDeathCnt, Math.min(applyto.getBuffedIntValue(SecondaryStat.GiantBossDeathCnt) + 1, effect2.getLimit()));
                    if ((Integer)localstatups.get(SecondaryStat.GiantBossDeathCnt) < effect2.getLimit()) break;
                    applyto.dispelEffect(SecondaryStat.GiantBossDeathCnt);
                    if (applyto.isAlive()) {
                        applyto.addHPMP(-100, 0);
                    }
                    return;
                }
                case 80010040: {
                    b3 = true;
                    break;
                }
                case 80011158: {
                    b3 = true;
                    b5 = false;
                    applyto.dispelEffect(SecondaryStat.ReverseInput);
                    applyto.dispelEffect(SecondaryStat.Seal);
                    applyto.dropMessage(-5, "黑翼胸章的潛在力量在保護著你。");
                    break;
                }
                case 80011159: {
                    b3 = true;
                    b5 = false;
                    break;
                }
                case 400011066: {
                    SecondaryStatValueHolder mbsvh;
                    if (!passive || (mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.HitStackDamR)) == null) break;
                    buffz = Math.min(applyto.getBuffedIntZ(SecondaryStat.HitStackDamR) + 1, this.getY());
                    localDuration = mbsvh.getLeftTime();
                    localstatups.put(SecondaryStat.HitStackDamR, 1);
                    break;
                }
                case 80002888: {
                    if (att) {
                        return;
                    }
                    buffz = 0;
                    break;
                }
                case 80012015: {
                    SecondaryStatValueHolder holder = applyto.getBuffStatValueHolder(SecondaryStat.ErdaStack);
                    if (holder == null) break;
                    holder.value = 1;
                    applyto.send(BuffPacket.giveBuff(applyto, holder.effect, Collections.singletonMap(SecondaryStat.ErdaStack, holder.sourceID)));
                    break;
                }
                default: {
                    Object eff;
                    if (GameConstants.getMountItem(sourceid, applyto) <= 0) break;
                    if (!primary) {
                        return;
                    }
                    if (sourceid == 35001002 && (eff = applyto.getSkillEffect(35120000)) != null) {
                        localstatups.put(SecondaryStat.EMHP, ((MapleStatEffect)eff).getEnhancedHP());
                        localstatups.put(SecondaryStat.EMMP, ((MapleStatEffect)eff).getEnhancedMP());
                        localstatups.put(SecondaryStat.EPAD, ((MapleStatEffect)eff).getEnhancedWatk());
                        localstatups.put(SecondaryStat.EPDD, ((MapleStatEffect)eff).getEnhancedWdef());
                    }
                    applyto.dispelEffect(SecondaryStat.RideVehicle);
                    localDuration = 2100000000;
                    localstatups.put(SecondaryStat.RideVehicle, GameConstants.getMountItem(sourceid, applyto));
                }
            }
        }
        if (applyto.isInvincible() && sourceid == 1010) {
            if (applyto.isGm()) {
                applyto.dropMessage(9, "定制技能 - GM無敵[原技能: 金剛不壞](在角色無敵狀態才會生效)");
            }
            localstatups.clear();
            localstatups.put(SecondaryStat.DojangInvincible, 1);
            localstatups.put(SecondaryStat.HitStackDamR, 1);
            localstatups.put(SecondaryStat.IndieStance, 100);
        }
        if (80011247 == sourceid) {
            localstatups.put(SecondaryStat.DawnShield_ExHP, applyto.getStat().getCurrentMaxHP());
        }
        if (80011248 == sourceid) {
            localstatups.put(SecondaryStat.DawnShield_ExHP, applyto.getStat().getCurrentMaxHP());
        }
        if (cancelEffect) {
            applyto.cancelEffect(this, overwrite, -1L, localstatups);
        }
        if (cancelEffect && !maskedstatups.isEmpty()) {
            applyto.cancelEffect(this, overwrite, -1L, maskedstatups);
        }
        EnumMap<SecondaryStat, Integer> writeStatups = new EnumMap<SecondaryStat, Integer>(SecondaryStat.class);
        for (Map.Entry entry : sendstatups.entrySet()) {
            writeStatups.put((SecondaryStat)entry.getKey(), (Integer)((Pair)entry.getValue()).getLeft());
        }
        if (!sendstatups.isEmpty()) {
            applyto.registerEffect(sendstatups, buffz, applyfrom.getId(), currentTimeMillis, startChargeTime, localDuration, new CancelEffectAction(applyto, this, currentTimeMillis, localstatups));
        } else if (localDuration > 0 && !localstatups.isEmpty()) {
            applyto.registerEffect(this, localstatups, buffz, applyfrom.getId(), currentTimeMillis, startChargeTime, localDuration, new CancelEffectAction(applyto, this, currentTimeMillis, localstatups));
        }
        if (maskedDuration > 0 && !maskedstatups.isEmpty()) {
            long startTime = System.currentTimeMillis() + 1L;
            applyto.registerEffect(this, maskedstatups, buffz, applyfrom.getId(), startTime, startChargeTime, maskedDuration, new CancelEffectAction(applyto, this, startTime, maskedstatups));
            localstatups.putAll(maskedstatups);
        }
        if (sh != null) {
            SkillClassApplier applier = new SkillClassApplier();
            applier.effect = this;
            applier.primary = primary;
            applier.att = att;
            applier.passive = passive;
            applier.pos = pos;
            applier.duration = localDuration;
            applier.maskedDuration = maskedDuration;
            applier.localstatups = localstatups;
            applier.maskedstatups = maskedstatups;
            applier.sendstatups = sendstatups;
            applier.startTime = currentTimeMillis;
            applier.startChargeTime = startChargeTime;
            applier.b3 = b3;
            applier.b4 = b4;
            applier.b5 = b5;
            applier.overwrite = overwrite;
            applier.cancelEffect = cancelEffect;
            applier.b7 = b7;
            applier.applySummon = applySummon;
            applier.buffz = buffz;
            result = sh.onAfterRegisterEffect(applyfrom, applyto, applier);
            if (result == 0) {
                return;
            }
            if (result == 1) {
                primary = applier.primary;
                att = applier.att;
                passive = applier.passive;
                pos = applier.pos;
                localDuration = applier.duration;
                maskedDuration = applier.maskedDuration;
                localstatups = applier.localstatups;
                maskedstatups = applier.maskedstatups;
                sendstatups = applier.sendstatups;
                currentTimeMillis = applier.startTime;
                startChargeTime = applier.startChargeTime;
                b3 = applier.b3;
                b4 = applier.b4;
                b5 = applier.b5;
                overwrite = applier.overwrite;
                cancelEffect = applier.cancelEffect;
                b7 = applier.b7;
                applySummon = applier.applySummon;
                buffz = applier.buffz;
            }
        }
        if (!(this instanceof MobSkill) && this.isSkill() && 400051088 != sourceid) {
            Skill skill = SkillFactory.getSkill(sourceid);
            boolean bl = b7 = skill != null && skill.isInvisible();
        }
        if (localstatups.size() > 1 && (!b7 || SkillConstants.is召喚獸戒指(sourceid))) {
            localstatups.remove(SecondaryStat.IndieBuffIcon);
        }
        for (SecondaryStat secondaryStat : localstatups.keySet()) {
            if (writeStatups.containsKey(secondaryStat)) continue;
            writeStatups.put(secondaryStat, sourceid);
        }
        EnumMap<SecondaryStat, Integer> foreignStatups = new EnumMap<SecondaryStat, Integer>(SecondaryStat.class);
        for (Map.Entry entry : localstatups.entrySet()) {
            if (!SkillConstants.isShowForgenBuff((SecondaryStat)entry.getKey()) || entry.getKey() == SecondaryStat.GuidedBullet || entry.getKey() == SecondaryStat.Booster) continue;
            foreignStatups.put((SecondaryStat)entry.getKey(), (Integer)entry.getValue());
        }
        if (applySummon && this.getSummonMovementType() != null) {
            this.applySummonEffect(applyto, pos, localDuration, applyto.getSpecialStat().getMaelstromMoboid(), currentTimeMillis);
        }
        if (!foreignStatups.isEmpty()) {
            applyto.getMap().broadcastMessage(BuffPacket.giveForeignBuff(applyto, foreignStatups));
        }
        if (!localstatups.isEmpty()) {
            applyto.getClient().announce(BuffPacket.giveBuff(applyto, this, writeStatups));
        }
        if (!(this instanceof MobSkill)) {
            if (b3 || applyfrom != applyto) {
                if (applyfrom == applyto && b5) {
                    applyto.getClient().announce(this.isSkill() ? EffectPacket.showBuffEffect(applyto, false, sourceid, level, direction, pos) : EffectPacket.showBuffItemEffect(-1, sourceid));
                } else {
                    applyto.getClient().announce(this.isSkill() ? EffectPacket.showSkillAffected(-1, sourceid, level, direction) : EffectPacket.showBuffItemEffect(-1, sourceid));
                }
            }
            if (b4 && (this.isSkill() && primary || SkillConstants.isRapidAttackSkill(sourceid))) {
                if (applyfrom == applyto) {
                    applyto.getMap().broadcastMessage(applyto, this.isSkill() ? EffectPacket.showBuffEffect(applyto, true, sourceid, level, direction, pos) : EffectPacket.showBuffItemEffect(applyto.getId(), sourceid), applyto.getPosition());
                    return;
                }
                applyto.getMap().broadcastMessage(applyto, this.isSkill() ? EffectPacket.showSkillAffected(applyto.getId(), sourceid, level, direction) : EffectPacket.showBuffItemEffect(applyto.getId(), sourceid), applyto.getPosition());
            }
        }
    }

    public AbstractSkillHandler getSkillHandler() {
        return SkillClassFetcher.getHandlerBySkill(this.sourceid);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applySummonEffect(MapleCharacter applyto, Point pos, int duration, int mobOid, long startTime) {
        SummonMovementType movementType;
        AbstractSkillHandler handler = this.getSkillHandler();
        int handleRes = -1;
        if (handler != null) {
            SkillClassApplier applier = new SkillClassApplier();
            applier.effect = this;
            applier.pos = pos;
            applier.duration = duration;
            applier.mobOid = mobOid;
            applier.startTime = startTime;
            handleRes = handler.onApplySummonEffect(applyto, applier);
            if (handleRes == 0) {
                return;
            }
            if (handleRes == 1) {
                pos = applier.pos;
                duration = applier.duration;
                mobOid = applier.mobOid;
                startTime = applier.startTime;
            }
        }
        if ((movementType = this.getSummonMovementType()) == null) {
            return;
        }
        int sourceid = this.sourceid;
        if (applyto.isDebug()) {
            applyto.dropDebugMessage(1, "[Spawn Summon] Effect:" + String.valueOf(this));
        }
        int limit = 1;
        switch (sourceid) {
            case 42100010: 
            case 152121006: {
                limit = 5;
                break;
            }
            case 42111003: {
                limit = 2;
                break;
            }
            case 5320011: {
                limit = applyto.getSkillLevel(5320045) > 0 ? 2 : 1;
                break;
            }
            case 0x111B1F: {
                limit = 3;
            }
        }
        if (MapleSummon.getSummonMaxCount(sourceid) != 1) {
            try {
                long timeNow = System.currentTimeMillis();
                List<MapleSummon> summons = applyto.getSummonsReadLock();
                ListIterator<MapleSummon> summonIterator = summons.listIterator(summons.size());
                int summonCount = limit;
                while (summonIterator.hasPrevious()) {
                    int maxCount;
                    MapleSummon summon = summonIterator.previous();
                    if (summon.getSkillId() != sourceid && summon.getParentSummon() != sourceid || (maxCount = summon.getSummonMaxCount()) != 1 && summon.getCreateTime() + (long)summon.getDuration() > timeNow && (maxCount == -1 || summonCount++ < maxCount)) continue;
                    if (applyto.isDebug()) {
                        applyto.dropDebugMessage(1, "[Summon] Remove Summon Effect:" + String.valueOf(summon.getEffect()));
                    }
                    applyto.getMap().disappearMapObject(summon);
                    summonIterator.remove();
                }
            }
            finally {
                applyto.unlockSummonsReadLock();
            }
            List<SecondaryStatValueHolder> holderList = applyto.getEffects().get(SecondaryStat.IndieBuffIcon);
            if (holderList != null) {
                List<Integer> linkSummons = MapleSummon.getLinkSummons(sourceid);
                linkSummons.add(sourceid);
                Iterator<SecondaryStatValueHolder> holderIterator = holderList.iterator();
                while (holderIterator.hasNext()) {
                    SecondaryStatValueHolder mbsvh = holderIterator.next();
                    if (mbsvh == null || mbsvh.effect == null) continue;
                    for (int ls : linkSummons) {
                        if (mbsvh.effect.getSourceId() != ls || applyto.getSummonBySkillID(ls) != null) continue;
                        mbsvh.cancel();
                        holderIterator.remove();
                        if (!applyto.isDebug()) continue;
                        applyto.dropDebugMessage(1, "[BUFF] Deregister:" + String.valueOf(SecondaryStat.IndieBuffIcon));
                    }
                }
                if (holderList.isEmpty()) {
                    applyto.getEffects().remove(SecondaryStat.IndieBuffIcon);
                }
            }
        }
        int[] oidArray = new int[2];
        Point summonTeamPos = new Point();
        for (int stance = 0; stance < limit; ++stance) {
            if (pos == null) {
                pos = applyto.getPosition();
            }
            switch (sourceid) {
                case 5320011: {
                    pos = new Point(pos.x + stance * -90, pos.y);
                    break;
                }
                case 42100010: {
                    pos = new Point(pos.x + (applyto.isFacingLeft() ? -100 : 100) * stance, pos.y);
                    break;
                }
                case 42111003: {
                    pos = new Point(pos.x + (stance == 0 ? -400 : 800), pos.y);
                }
            }
            MapleSummon summon = new MapleSummon(applyto, this, pos, movementType, duration, this.getRange(), mobOid, startTime);
            if (this.info.get((Object)MapleStatInfo.hcSummonHp) > 0) {
                summon.setSummonHp(this.getX());
            }
            switch (sourceid) {
                case 42111003: {
                    summon.setStance(stance);
                    break;
                }
                case 3221014: {
                    summon.setSummonHp(this.getX());
                    break;
                }
                case 5320011: {
                    if (stance != 1) break;
                    summon.setShadow(5320045);
                    break;
                }
                case 14000027: {
                    summon.setShadow(applyto.getSkillLevel(14120008) > 0 ? 14120008 : (applyto.getSkillLevel(0xD74D4D) > 0 ? 0xD74D4D : (applyto.getSkillLevel(14100027) > 0 ? 14100027 : 0)));
                    break;
                }
                case 35111002: {
                    break;
                }
                case 151100002: {
                    summon.setCurrentFh(0);
                }
            }
            applyto.addSummon(summon);
            summon.setAnimated(1);
            applyto.getMap().spawnMapObject(-1, summon, null);
            summon.setAnimated(0);
            List<Integer> summons = applyto.getSummonsOIDsBySkillID(35111002);
            if (sourceid == 35111002 && summons.size() >= 3) {
                applyto.getClient().announce(MaplePacketCreator.teslaTriangle(applyto.getId(), summons.get(0), summons.get(1), summons.get(2)));
            }
            if (sourceid == 42111003) {
                oidArray[stance] = summon.getObjectId();
                if (stance == 0) {
                    summonTeamPos.x = summon.getPosition().x;
                } else {
                    summonTeamPos.y = summon.getPosition().x;
                }
                if (stance == 1) {
                    applyto.getMap().broadcastMessage(applyto, SummonPacket.summonGost(applyto.getId(), oidArray[0], oidArray[1], this.level, summonTeamPos, (short)summon.getPosition().getY()), true);
                }
            }
            if (sourceid == 400041028) {
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.LP_SummonedAvatarSync.getValue());
                mplew.writeInt(summon.getOwnerId());
                if (summon.showCharLook()) {
                    summon.getOwner().getAvatarLook().encode(mplew, true);
                    applyto.getClient().announce(mplew.getPacket());
                }
            }
            if (sourceid == 162101003 || sourceid == 162121012) {
                int[] skills = new int[9];
                int[] list = switch (sourceid) {
                    case 162121012 -> new int[]{162121013, 162121014};
                    default -> new int[]{162101004};
                };
                for (int i = 0; i < skills.length; ++i) {
                    skills[i] = list[Randomizer.nextInt(list.length)];
                }
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.SUMMON_SKILLS.getValue());
                mplew.writeInt(sourceid);
                mplew.writeInt(skills.length);
                for (int skillid : skills) {
                    mplew.writeInt(skillid);
                }
                applyto.send(mplew.getPacket());
            }
            applyto.getSpecialStat().setMaelstromMoboid(0);
        }
        if (sourceid != 151100002) {
            applyto.sendEnableActions();
        }
    }

    public boolean applyReturnScroll(MapleCharacter applyto) {
        if (this.moveTo == -1 || this.sourceid == -2031010 || this.sourceid == -2030021) {
            return false;
        }
        MapleMap target = null;
        boolean nearest = false;
        if (this.moveTo == 999999999) {
            nearest = true;
            if (applyto.getMap().getReturnMapId() != 999999999) {
                target = applyto.getMap().getReturnMap();
            }
        } else {
            target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(this.moveTo);
            if (target.getId() == 931050500 && target != applyto.getMap()) {
                applyto.changeMap(target, target.getPortal(0));
                return true;
            }
        }
        if (target == null || target == applyto.getMap() || nearest && applyto.getMap().isTown()) {
            return false;
        }
        applyto.changeMap(target, target.getPortal(0));
        return true;
    }

    public boolean is靈魂之石() {
        return this.isSkill() && this.sourceid == 22181003;
    }

    public void w(boolean bl2) {
        this.bxi = bl2;
    }

    public boolean jR() {
        if (this.lt == null || this.rb == null || !this.bxi) {
            return this.is靈魂之石();
        }
        return this.bxi;
    }

    public Rectangle getBounds() {
        return this.calculateBoundingBox(new Point(0, 0));
    }

    public Rectangle calculateBoundingBox(Point posFrom) {
        return this.calculateBoundingBox(posFrom, false);
    }

    public Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        return this.calculateBoundingBox(posFrom, facingLeft, 0);
    }

    public Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft, int addedRange) {
        return MapleStatEffectFactory.calculateBoundingBox(posFrom, facingLeft, this.lt, this.rb, this.info.get((Object)MapleStatInfo.range) + addedRange);
    }

    public Rectangle getBounds2() {
        return this.calculateBoundingBox2(new Point(0, 0));
    }

    public Rectangle calculateBoundingBox2(Point posFrom) {
        return this.calculateBoundingBox2(posFrom, false);
    }

    public Rectangle calculateBoundingBox2(Point posFrom, boolean facingLeft) {
        return this.calculateBoundingBox2(posFrom, facingLeft, 0);
    }

    public Rectangle calculateBoundingBox2(Point posFrom, boolean facingLeft, int addedRange) {
        return MapleStatEffectFactory.calculateBoundingBox(posFrom, facingLeft, this.lt2, this.rb2, this.info.get((Object)MapleStatInfo.range) + addedRange);
    }

    public Rectangle getBounds3() {
        return this.calculateBoundingBox3(new Point(0, 0));
    }

    public Rectangle calculateBoundingBox3(Point posFrom) {
        return this.calculateBoundingBox3(posFrom, false);
    }

    public Rectangle calculateBoundingBox3(Point posFrom, boolean facingLeft) {
        return this.calculateBoundingBox3(posFrom, facingLeft, 0);
    }

    public Rectangle calculateBoundingBox3(Point posFrom, boolean facingLeft, int addedRange) {
        return MapleStatEffectFactory.calculateBoundingBox(posFrom, facingLeft, this.lt3, this.rb3, this.info.get((Object)MapleStatInfo.range) + addedRange);
    }

    public double getMaxDistance() {
        int maxX = Math.max(Math.abs(this.lt == null ? 0 : this.lt.x), Math.abs(this.rb == null ? 0 : this.rb.x));
        int maxY = Math.max(Math.abs(this.lt == null ? 0 : this.lt.y), Math.abs(this.rb == null ? 0 : this.rb.y));
        return Math.pow(Math.pow(maxX, 2.0) + Math.pow(maxY, 2.0), 0.5);
    }

    public void silentApplyBuff(MapleCharacter chr, long starttime, int localDuration, Map<SecondaryStat, Integer> statup, int chrId) {
        int maskedDuration = 0;
        int newDuration = (int)(starttime + (long)localDuration - System.currentTimeMillis());
        if (this.sourceid == 2121004 || this.sourceid == 2221004 || this.sourceid == 2321004) {
            maskedDuration = this.alchemistModifyVal(chr, 4000, false);
        }
        chr.registerEffect(this, statup, 0, chrId, starttime, 0L, maskedDuration > 0 ? maskedDuration : newDuration, new CancelEffectAction(chr, this, starttime, statup));
    }

    public void applyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, int newDuration) {
        this.applyBuffEffect(applyfrom, applyto, newDuration, primary, false, false, null);
    }

    public void applyBuffEffect(MapleCharacter applyfrom, int newDuration, boolean passive) {
        this.applyBuffEffect(applyfrom, applyfrom, newDuration, false, false, passive, null);
    }

    private int getHpMpChange(MapleCharacter applyfrom, boolean hpchange) {
        int change = 0;
        if (this.hpR != 0.0 || this.mpR != 0.0) {
            double healHpRate;
            double d = healHpRate = hpchange ? this.hpR : this.mpR;
            if (applyfrom.isDebug()) {
                applyfrom.dropMessage(-5, (hpchange ? "[H" : "[M") + "P Rate]  Default: " + healHpRate);
            }
            int maxChange = healHpRate < 1.0 ? Math.min(49999, (int)Math.floor(99999.0 * healHpRate)) : 99999;
            int current = hpchange ? applyfrom.getStat().getCurrentMaxHP() : applyfrom.getStat().getCurrentMaxMP();
            change = Math.abs((int)((double)current * healHpRate)) > Math.abs(maxChange) ? maxChange : (int)((double)current * healHpRate);
        }
        return change;
    }

    public int alchemistModifyVal(MapleCharacter chr, int val, boolean withX) {
        Skill s = SkillFactory.getSkill(this.sourceid);
        if (s != null && s.isHyperSkill()) {
            return val;
        }
        if (this.isSkill()) {
            return val * (100 + (withX ? chr.getStat().skillRecoveryUP : chr.getStat().incBuffTime + (this.getSummonMovementType() == null ? 0 : chr.getStat().BuffUP_Summon))) / 100;
        }
        return val * (100 + (withX ? chr.getStat().itemRecoveryUP : chr.getStat().BuffUP)) / 100;
    }

    public void setLt(Point Lt) {
        this.lt = Lt;
    }

    public void setRb(Point Rb) {
        this.rb = Rb;
    }

    public void setLt2(Point Lt) {
        this.lt2 = Lt;
    }

    public void setRb2(Point Rb) {
        this.rb2 = Rb;
    }

    public void setLt3(Point Lt) {
        this.lt3 = Lt;
    }

    public void setRb3(Point Rb) {
        this.rb3 = Rb;
    }

    public Skill getSkill() {
        return SkillFactory.getSkill(this.sourceid);
    }

    public boolean isGmBuff() {
        switch (this.sourceid) {
            case 9001000: 
            case 9001001: 
            case 9001002: 
            case 9001003: 
            case 9001005: 
            case 9001008: 
            case 9101000: 
            case 9101001: 
            case 9101002: 
            case 9101003: 
            case 9101005: 
            case 9101008: 
            case 10001075: {
                return true;
            }
        }
        return JobConstants.is零轉職業(this.sourceid / 10000) && this.sourceid % 10000 == 1005;
    }

    public boolean isInflation() {
        return this.inflation > 0;
    }

    public int getInflation() {
        return this.inflation;
    }

    public boolean ke() {
        switch (this.sourceid) {
            case 1301014: 
            case 2121054: 
            case 2321008: 
            case 24121054: 
            case 33111011: 
            case 80001242: {
                return true;
            }
        }
        return false;
    }

    public boolean isMonsterBuff() {
        switch (this.sourceid) {
            case 1121016: 
            case 1211013: 
            case 1221014: 
            case 1321014: 
            case 2301002: 
            case 4321002: 
            case 5011002: 
            case 11111023: 
            case 25100002: 
            case 33001025: 
            case 35111008: 
            case 51111005: 
            case 90001002: 
            case 90001003: 
            case 90001004: 
            case 90001005: 
            case 90001006: {
                return this.isSkill();
            }
        }
        return false;
    }

    public boolean isPartyBuff() {
        if (this.lt == null || this.rb == null) {
            return false;
        }
        return this.partyBuff;
    }

    public boolean isRangeBuff() {
        if (this.lt == null || this.rb == null) {
            return false;
        }
        return this.rangeBuff;
    }

    public boolean is幻影() {
        return this.isSkill() && this.sourceid == 14121054;
    }

    public boolean is蓄能系統() {
        return this.isSkill() && this.sourceid == 30020232;
    }

    public int getHp() {
        return this.info.get((Object)MapleStatInfo.hp);
    }

    public int getHpFX() {
        return this.info.get((Object)MapleStatInfo.hpFX);
    }

    public int getMp() {
        return this.info.get((Object)MapleStatInfo.mp);
    }

    public int getMpCon() {
        return this.info.get((Object)MapleStatInfo.mpCon);
    }

    public int getEpCon() {
        return this.info.get((Object)MapleStatInfo.epCon);
    }

    public int getDotInterval() {
        return this.info.get((Object)MapleStatInfo.dotInterval);
    }

    public int getDOTStack() {
        return this.info.get((Object)MapleStatInfo.dotSuperpos);
    }

    public double getHpR() {
        return this.hpR;
    }

    public double getMpR() {
        return this.mpR;
    }

    public int getMastery() {
        return this.info.get((Object)MapleStatInfo.mastery);
    }

    public int getPad() {
        return this.info.get((Object)MapleStatInfo.pad);
    }

    public int getPadR() {
        return this.info.get((Object)MapleStatInfo.padR);
    }

    public int getMad() {
        return this.info.get((Object)MapleStatInfo.mad);
    }

    public int getWdef() {
        return this.info.get((Object)MapleStatInfo.pdd);
    }

    public int getWdef2Dam() {
        return this.info.get((Object)MapleStatInfo.pdd2dam);
    }

    public int getMdef() {
        return this.info.get((Object)MapleStatInfo.mdd);
    }

    public int getAcc() {
        return this.info.get((Object)MapleStatInfo.acc);
    }

    public int getAcc2Dam() {
        return this.info.get((Object)MapleStatInfo.acc2dam);
    }

    public int getAvoid() {
        return this.info.get((Object)MapleStatInfo.eva);
    }

    public int getSpeed() {
        return this.info.get((Object)MapleStatInfo.speed);
    }

    public int getJump() {
        return this.info.get((Object)MapleStatInfo.jump);
    }

    public int getSpeedMax() {
        return this.info.get((Object)MapleStatInfo.speedMax);
    }

    public int getPsdSpeed() {
        return this.info.get((Object)MapleStatInfo.psdSpeed);
    }

    public int getPsdJump() {
        return this.info.get((Object)MapleStatInfo.psdJump);
    }

    public int getDuration() {
        return this.info.get((Object)MapleStatInfo.time);
    }

    public int getBuffDuration(MapleCharacter applyfrom) {
        if (this instanceof MobSkill) {
            return this.calcDebuffBuffDuration(this.getDuration(), applyfrom);
        }
        if (this.getSummonMovementType() != null) {
            return this.getSummonDuration(applyfrom);
        }
        return this.calcBuffDuration(this.getDuration(), applyfrom);
    }

    public int calcBuffDuration(int duration, MapleCharacter applyfrom) {
        if (this.getSummonMovementType() != null) {
            return this.calcSummonDuration(duration, applyfrom);
        }
        int time = 0;
        if (duration < 2100000000 && this.isSkill()) {
            Skill skill = SkillFactory.getSkill(this.sourceid);
            if (80002280 == this.sourceid) {
                MapleStatEffect effect = applyfrom.getSkillEffect(20010294);
                if (effect == null || !JobConstants.is龍魔導士(applyfrom.getJob())) {
                    effect = applyfrom.getSkillEffect(80000369);
                }
                if (effect != null) {
                    time += (int)Math.floor((double)(duration * effect.getX()) / 100.0);
                }
            } else if (!(skill == null || this.notIncBuffDuration || 80001140 != this.sourceid && !skill.canBeLearnedBy(applyfrom.getJobWithSub()) || skill.isHyperSkill() || skill.isVSkill())) {
                time += duration * applyfrom.getStat().incBuffTime / 100;
            }
            time += applyfrom.getStat().getDuration(this.sourceid);
        }
        return duration + time;
    }

    public int calcDebuffBuffDuration(int duration, MapleCharacter applyfrom) {
        if (duration == 2100000000 || this.statups.containsKey(SecondaryStat.Lapidification)) {
            return duration;
        }
        return duration * Math.max(100 - Math.min(70, applyfrom.getStat().asrR * 6 / 10 + 25), 10) / 100;
    }

    public int getSummonDuration(MapleCharacter applyfrom) {
        return this.calcSummonDuration(this.getDuration(), applyfrom);
    }

    public int calcSummonDuration(int duration, MapleCharacter applyfrom) {
        int time = 0;
        if (duration < 2100000000 && this.isSkill()) {
            Skill skill = SkillFactory.getSkill(this.sourceid);
            if (skill != null && !this.notIncBuffDuration && skill.canBeLearnedBy(applyfrom.getJobWithSub()) && !skill.isHyperSkill() && !skill.isVSkill()) {
                time += duration * applyfrom.getStat().summonTimeR / 100;
            }
            time += applyfrom.getStat().getDuration(this.sourceid);
        }
        return duration + time;
    }

    public void setDebuffTime(int time) {
        this.debuffTime = time;
    }

    public int getDebuffTime() {
        if (this.debuffTime > 0) {
            return this.debuffTime;
        }
        return this.getDuration();
    }

    public int getMobDebuffDuration(MapleCharacter applyfrom) {
        int duration = this.debuffTime;
        if (duration <= 0) {
            duration = this.getDotTime(applyfrom);
            if (duration <= 300) {
                duration *= 1000;
            }
            if (duration <= 0) {
                duration = this.getDuration();
            } else {
                return duration;
            }
        }
        return this.calcMobDebuffDuration(duration, applyfrom);
    }

    public int calcMobDebuffDuration(int duration, MapleCharacter applyfrom) {
        return duration;
    }

    public int getSubTime() {
        return this.info.get((Object)MapleStatInfo.subTime);
    }

    public boolean isOverTime() {
        return this.overTime;
    }

    public boolean isNotRemoved() {
        return this.notRemoved;
    }

    public EnumMap<SecondaryStat, Integer> getStatups() {
        return this.statups;
    }

    public EnumMap<SecondaryStat, Integer> getWriteStatups() {
        return statups.keySet().parallelStream().collect(Collectors.toMap(it -> it, it -> getSourceId(), (a, b) -> b, () -> new EnumMap<>(SecondaryStat.class)));
    }


    public boolean sameSource(MapleStatEffect effect) {
        return effect != null && this.sourceid == effect.sourceid && this.isSkill() == effect.isSkill();
    }

    public int getQ() {
        return this.info.get((Object)MapleStatInfo.q);
    }

    public int getQ2() {
        return this.info.get((Object)MapleStatInfo.q2);
    }

    public int getS() {
        return this.info.get((Object)MapleStatInfo.s);
    }

    public int getS2() {
        return this.info.get((Object)MapleStatInfo.s2);
    }

    public int getT() {
        return this.info.get((Object)MapleStatInfo.t);
    }

    public int getU() {
        return this.info.get((Object)MapleStatInfo.u);
    }

    public int getV() {
        return this.info.get((Object)MapleStatInfo.v);
    }

    public int getW() {
        return this.info.get((Object)MapleStatInfo.w);
    }

    public int getW2() {
        return this.info.get((Object)MapleStatInfo.w2);
    }

    public int getX() {
        return this.info.get((Object)MapleStatInfo.x);
    }

    public int getY() {
        return this.info.get((Object)MapleStatInfo.y);
    }

    public int getZ() {
        return this.info.get((Object)MapleStatInfo.z);
    }

    public int getDamage() {
        if (this.sourceid == 33121017) {
            return this.info.get((Object)MapleStatInfo.y);
        }
        return this.info.get((Object)MapleStatInfo.damage);
    }

    public int getMadR() {
        return this.info.get((Object)MapleStatInfo.madR);
    }

    public int getPVPDamage() {
        return this.info.get((Object)MapleStatInfo.PVPdamage);
    }

    public int getAttackCount() {
        return this.info.get((Object)MapleStatInfo.attackCount);
    }

    public int getAttackCount(MapleCharacter applyfrom) {
        return this.info.get((Object)MapleStatInfo.attackCount) + applyfrom.getStat().getAttackCount(this.sourceid);
    }

    public int getBulletCount() {
        return this.info.get((Object)MapleStatInfo.bulletCount);
    }

    public int getBulletCount(MapleCharacter applyfrom) {
        return this.info.get((Object)MapleStatInfo.bulletCount) + applyfrom.getStat().getSkillBulletCount(this.sourceid);
    }

    public int getBulletConsume() {
        return this.info.get((Object)MapleStatInfo.bulletConsume);
    }

    public int getMobCount() {
        return this.info.get((Object)MapleStatInfo.mobCount);
    }

    public int getMobCount(MapleCharacter applyfrom) {
        return this.info.get((Object)MapleStatInfo.mobCount) + applyfrom.getStat().getMobCount(this.sourceid);
    }

    public int getMoneyCon() {
        return this.moneyCon;
    }

    public int getCooltimeReduceR() {
        return this.info.get((Object)MapleStatInfo.coolTimeR);
    }

    public int getMesoAcquisition() {
        return this.info.get((Object)MapleStatInfo.mesoR);
    }

    public int getCooldown() {
        int cooldown = this.info.get((Object)MapleStatInfo.cooltime) * 1000;
        if (cooldown <= 0 && this.info.get((Object)MapleStatInfo.cooltimeMS) > 0) {
            cooldown = this.info.get((Object)MapleStatInfo.cooltimeMS);
        }
        return cooldown;
    }

    public int getCooldown(MapleCharacter applyfrom) {
        Skill skill = SkillFactory.getSkill(this.sourceid);
        int cooldown = this.getCooldown();
        if (!this.isSkill() || skill == null) {
            return cooldown;
        }
        Skill sourceSkill = null;
        int linkId = SkillConstants.getCooldownLinkSourceId(this.sourceid);
        if (linkId != this.sourceid) {
            sourceSkill = skill;
            skill = SkillFactory.getSkill(linkId);
            cooldown = skill.getEffect(this.getLevel()).getCooldown();
        }
        if (applyfrom != null) {
            int fixCoolTime;
            Object sb;
            if (1320019 == this.sourceid) {
                MapleStatEffect effect;
                SecondaryStatValueHolder mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.ReincarnationOnOff);
                if (mbsvh != null && mbsvh.effect != null && (effect = applyfrom.getSkillEffect(mbsvh.effect.getSourceId() - 1000 + mbsvh.value)) != null) {
                    return effect.getCooldown(applyfrom);
                }
            } else {
                if (this.is平衡技能() && (applyfrom.getBuffSource(SecondaryStat.Larkness) == 27121054 || applyfrom.getBuffSource(SecondaryStat.Larkness) == 20040219)) {
                    return 0;
                }
                if (this.sourceid == 3111013 || applyfrom.getBuffedValue(SecondaryStat.StrikerHyperElectric) != null && (this.sourceid == 15111022 || this.sourceid == 15120003)) {
                    return 0;
                }
            }
            if (!skill.isNotCooltimeReduce() && !skill.isNotCooltimeReset() && Randomizer.isSuccess(applyfrom.getStat().nocoolProp)) {
                if (applyfrom.isDebug()) {
                    sb = String.valueOf(skill) + " 冷卻時間 => 默認: " + cooldown + "ms" + cooldown + "  [內潛: 依 " + applyfrom.getStat().nocoolProp + "%機率, 沒有冷卻時間 最終時間: 0ms";
                    applyfrom.dropMessage(-5, (String)sb);
                }
                return 0;
            }
            if (JobConstants.is幻影俠盜(applyfrom.getJob())) {
                int cooltime = SkillConstants.getStolenHyperSkillColltime(skill.getId()) * 1000;
                if (skill.getId() == 20031205) {
                    int count = 0;
                    if (applyfrom.getTempValues().get("skill" + skill.getId()) != null) {
                        count = Integer.parseInt(applyfrom.getTempValues().get("skill" + skill.getId()).toString());
                    }
                    cooldown = this.getCooldown() * count;
                }
            } else if (cooldown > 0) {
                int equipReduceCoolTime;
                sb = new StringBuilder(String.valueOf(skill) + " 冷卻時間 => 默認: " + cooldown + "ms");
                cooldown = (int)((double)cooldown * ((double)(100 - applyfrom.getStat().getReduceCooltimeRate(skill.getId())) / 100.0));
                if (applyfrom.getStat().getReduceCooltimeRate(skill.getId()) > 0) {
                    ((StringBuilder)sb).append(" [超級技能減少: ").append(applyfrom.getStat().getReduceCooltimeRate(skill.getId())).append("%]");
                }
                if (applyfrom.getStat().getCooltimeReduceR() > 0 && cooldown > 1000) {
                    cooldown = (int)((double)cooldown * ((double)(100 - applyfrom.getStat().getCooltimeReduceR()) / 100.0));
                    cooldown = Math.max(1000, cooldown);
                    ((StringBuilder)sb).append(" [楓之谷聯盟減少: ").append(applyfrom.getStat().getCooltimeReduceR()).append("%]");
                }
                if (!skill.isHyperSkill() && (equipReduceCoolTime = applyfrom.getStat().getReduceCooltime()) > 0) {
                    boolean hasReduceCoolTime = false;
                    if (cooldown > 10000) {
                        int time = (int)Math.min(Math.ceil((double)(cooldown - 10000) / 1000.0), (double)equipReduceCoolTime);
                        cooldown -= time * 1000;
                        equipReduceCoolTime -= time;
                        ((StringBuilder)sb).append(" [裝備減少時間: ").append(time).append("秒]");
                        hasReduceCoolTime = true;
                    }
                    if (cooldown > 5000 && equipReduceCoolTime > 0) {
                        cooldown = (int)((double)cooldown * ((double)(100 - equipReduceCoolTime * 5) / 100.0));
                        ((StringBuilder)sb).append(" [裝備減少時間: ").append(equipReduceCoolTime * 5).append("%]");
                        hasReduceCoolTime = true;
                    }
                    if (hasReduceCoolTime) {
                        cooldown = Math.max(5000, cooldown);
                    }
                }
                if (applyfrom.isDebug()) {
                    ((StringBuilder)sb).append(" 最終時間: ").append(cooldown).append("ms");
                    applyfrom.dropMessage(-5, ((StringBuilder)sb).toString());
                }
            }
            if (!skill.isVSkill() && (fixCoolTime = applyfrom.getBuffedIntValue(SecondaryStat.FixCoolTime)) > 0 && cooldown > fixCoolTime) {
                cooldown = fixCoolTime;
            }
        }
        return cooldown;
    }

    public Map<MonsterStatus, Integer> getMonsterStatus() {
        return this.monsterStatus;
    }

    public int getBerserk() {
        return this.berserk;
    }

    public boolean is必殺狙擊() {
        return this.isSkill() && this.sourceid == 3221007;
    }

    public boolean is平衡技能() {
        return this.isSkill() && (this.sourceid == 27111303 || this.sourceid == 27121303);
    }

    public boolean is潛入() {
        return this.isSkill() && (this.sourceid == 20021001 || this.sourceid == 20031001 || this.sourceid == 30001001 || this.sourceid == 30011001 || this.sourceid == 30021001 || this.sourceid == 60001001 || this.sourceid == 60011001);
    }

    public boolean is魔力無限() {
        return this.isSkill() && (this.sourceid == 2121004 || this.sourceid == 2221004 || this.sourceid == 2321004);
    }

    public boolean is騎乘技能_() {
        return this.isSkill() && (SkillConstants.is騎乘技能(this.sourceid) || this.sourceid == 80001000 || SkillFactory.getMountLinkId(this.sourceid) > 0);
    }

    public boolean is騎乘技能() {
        return this.isSkill() && (this.is騎乘技能_() || GameConstants.getMountItem(this.sourceid, null) != 0) && !this.is合金盔甲();
    }

    public boolean is時空門() {
        return this.sourceid == 2311002 || this.sourceid == 400001001 || this.sourceid % 10000 == 8001;
    }

    public boolean is鬥氣爆發() {
        return this.isSkill() && this.sourceid == 1121010;
    }

    public boolean is惡魔衝擊() {
        return this.isSkill() && this.sourceid == 31121001;
    }

    public boolean isCharge() {
        switch (this.sourceid) {
            case 21101006: {
                return this.isSkill();
            }
        }
        return false;
    }

    public boolean isPoison() {
        return this.info.get((Object)MapleStatInfo.dot) > 0 && this.info.get((Object)MapleStatInfo.dotTime) > 0;
    }

    private boolean isMist() {
        switch (this.sourceid) {
            case 1076: 
            case 2100010: 
            case 2111003: 
            case 2221055: 
            case 2311011: 
            case 4121015: 
            case 4221006: 
            case 12111005: 
            case 12121005: 
            case 13111024: 
            case 13120007: 
            case 14111006: 
            case 21121068: 
            case 22161003: 
            case 22170064: 
            case 22170093: 
            case 25111012: 
            case 25111206: 
            case 25121055: 
            case 32121006: 
            case 33111013: 
            case 33121016: 
            case 35111008: 
            case 35120002: 
            case 35121010: 
            case 35121052: 
            case 36121007: 
            case 37110001: 
            case 42001101: 
            case 42101005: 
            case 42111004: 
            case 42121005: 
            case 51120057: 
            case 61121105: 
            case 61121116: 
            case 80001431: 
            case 100001261: 
            case 101120104: 
            case 131001107: 
            case 131001207: 
            case 131001307: 
            case 152121041: 
            case 155121006: 
            case 162121043: 
            case 400001017: 
            case 400010010: 
            case 400011060: 
            case 400011061: 
            case 400020002: 
            case 400020046: 
            case 400020051: 
            case 400021012: 
            case 400021031: 
            case 400021040: 
            case 400021041: 
            case 400021049: 
            case 400021050: 
            case 400030002: 
            case 400031012: 
            case 400041041: 
            case 400051025: 
            case 400051026: 
            case 400051084: {
                return true;
            }
        }
        return false;
    }

    private boolean is楓葉淨化() {
        switch (this.sourceid) {
            case 1121011: 
            case 1221012: 
            case 1321010: 
            case 2121008: 
            case 2221008: 
            case 2321009: 
            case 3121009: 
            case 0x30FF03: 
            case 3221008: 
            case 3321024: 
            case 4121009: 
            case 4221008: 
            case 4341008: 
            case 5121008: 
            case 5221010: 
            case 5321006: 
            case 21121008: 
            case 22171069: 
            case 23121008: 
            case 24121009: 
            case 27121010: 
            case 32121008: 
            case 33121008: 
            case 35121008: 
            case 36121009: 
            case 37121007: 
            case 41121004: 
            case 42121007: 
            case 61121015: 
            case 61121220: 
            case 63121010: 
            case 65121010: 
            case 100001261: 
            case 142121007: 
            case 151121006: 
            case 154121006: 
            case 164121010: 
            case 400001009: {
                return this.isSkill();
            }
        }
        return false;
    }

    public boolean is矛之鬥氣() {
        return this.isSkill() && this.sourceid == 21100019;
    }

    public boolean isMorph() {
        return this.morphId > 0;
    }

    public int getMorph() {
        return this.morphId;
    }

    public boolean is凱撒終極型態() {
        return this.isSkill() && (this.sourceid == 61111008 || this.sourceid == 61120008);
    }

    public boolean is凱撒超終極型態() {
        return this.isSkill() && this.sourceid == 61121053;
    }

    public boolean is超越攻擊狀態() {
        switch (this.sourceid) {
            case 31011000: 
            case 31201000: 
            case 31211000: 
            case 31221000: {
                return this.isSkill();
            }
        }
        return false;
    }

    public int getMorph(MapleCharacter chr) {
        int morph = this.getMorph();
        switch (morph) {
            case 1000: 
            case 1001: 
            case 1003: {
                return morph + (chr.getGender() == 1 ? 100 : 0);
            }
        }
        return morph;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isSummonSkill() {
        Skill summon = SkillFactory.getSkill(this.sourceid);
        return this.isSkill() && summon != null && summon.isSummonSkill();
    }

    public SummonMovementType getSummonMovementType() {
        if (!this.isSkill() || !this.isSummonSkill()) {
            return null;
        }
        if (this.sourceid == 400031000 && this.sourceid == 400031000) {
            return null;
        }
        if (this.is戒指技能()) {
            return SummonMovementType.WALK;
        }
        switch (this.sourceid) {
            case 3111002: 
            case 3120012: 
            case 3211002: 
            case 3220012: 
            case 5211001: 
            case 5220002: 
            case 5220023: 
            case 5220024: 
            case 5220025: 
            case 11111029: 
            case 13111004: 
            case 14001027: 
            case 14121054: 
            case 33101008: 
            case 35111005: 
            case 35111011: 
            case 51121016: 
            case 131001007: 
            case 131001022: 
            case 131001025: 
            case 131002022: 
            case 131003022: 
            case 131003023: 
            case 131004022: 
            case 131004023: 
            case 131005022: 
            case 131005023: 
            case 131006022: 
            case 131006023: 
            case 154110010: 
            case 400001022: 
            case 400001039: 
            case 400001064: 
            case 400021069: 
            case 400021071: 
            case 400021073: 
            case 400021095: 
            case 400041044: {
                return null;
            }
            case 0x111B1F: 
            case 2111013: 
            case 2211015: 
            case 2311014: 
            case 3111017: 
            case 3211019: 
            case 3221014: 
            case 4111007: 
            case 4211007: 
            case 4341006: 
            case 5211014: 
            case 5221022: 
            case 5221027: 
            case 5221029: 
            case 5320011: 
            case 5321003: 
            case 5321004: 
            case 5321052: 
            case 12111022: 
            case 13111024: 
            case 13120007: 
            case 14111010: 
            case 14121003: 
            case 21111034: 
            case 22171052: 
            case 22171081: 
            case 33111003: 
            case 35101012: 
            case 35111002: 
            case 35111008: 
            case 35120002: 
            case 35121003: 
            case 35121009: 
            case 35121010: 
            case 36121002: 
            case 36121013: 
            case 36121014: 
            case 42001100: 
            case 42100000: 
            case 42100010: 
            case 42110000: 
            case 42111003: 
            case 42120001: 
            case 61111002: 
            case 61111220: 
            case 80002230: 
            case 80002888: 
            case 80002889: 
            case 80011261: 
            case 131001019: 
            case 131001107: 
            case 131001307: 
            case 151100002: 
            case 151111001: 
            case 151120035: 
            case 154121041: 
            case 162101003: 
            case 162101006: 
            case 162121012: 
            case 162121015: 
            case 164121006: 
            case 164121008: 
            case 164121011: 
            case 400001019: 
            case 400001032: 
            case 400001033: 
            case 400001034: 
            case 400001035: 
            case 400011057: 
            case 400011065: 
            case 400021005: 
            case 400021047: 
            case 400021054: 
            case 400021063: 
            case 400021067: 
            case 400031047: 
            case 400031049: 
            case 400031051: 
            case 400041033: 
            case 400041038: 
            case 400041050: 
            case 400041087: 
            case 400041088: 
            case 400051011: 
            case 400051017: 
            case 400051022: 
            case 500001001: 
            case 500001002: 
            case 500001003: 
            case 500001004: {
                return SummonMovementType.STOP;
            }
            case 400051023: {
                return SummonMovementType.固定一段距離;
            }
            case 3311009: 
            case 14000027: 
            case 23111008: 
            case 23111009: 
            case 23111010: 
            case 33101011: 
            case 42111101: 
            case 42111102: 
            case 131002015: 
            case 152101008: 
            case 164111007: {
                return SummonMovementType.FLY;
            }
            case 35121011: 
            case 162101012: {
                return SummonMovementType.WALK_RANDOM;
            }
            case 1301013: 
            case 2121005: 
            case 2211011: 
            case 2221005: 
            case 2321003: 
            case 3111005: 
            case 3211005: 
            case 12000022: 
            case 12001004: 
            case 12100026: 
            case 12100028: 
            case 0xB8C8C8: 
            case 0xB8C8CC: 
            case 12111004: 
            case 12120007: 
            case 12120017: 
            case 14001005: 
            case 32001014: 
            case 32100010: 
            case 32110017: 
            case 32120019: 
            case 35111001: 
            case 35111009: 
            case 35111010: 
            case 152001003: 
            case 152121005: 
            case 400001012: 
            case 400001013: 
            case 400011001: 
            case 400021018: 
            case 400021032: 
            case 400021033: 
            case 400021114: 
            case 400031016: 
            case 400041052: 
            case 400051009: {
                return SummonMovementType.WALK;
            }
            case 14111024: 
            case 14121055: 
            case 14121056: 
            case 400011005: {
                return SummonMovementType.WALK_CLONE;
            }
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                return SummonMovementType.JAGUAR;
            }
            case 101100100: 
            case 101100101: 
            case 400011012: 
            case 400011013: 
            case 400011014: {
                return SummonMovementType.FIX_V_MOVE;
            }
            case 42111103: 
            case 152101000: 
            case 400011002: 
            case 400041028: {
                return SummonMovementType.固定跟隨攻擊;
            }
            case 400051038: 
            case 400051052: 
            case 400051053: {
                return SummonMovementType.UNKNOWN_16;
            }
            case 400051068: {
                return SummonMovementType.UNKNOWN_17;
            }
        }
        return SummonMovementType.WALK_SMART;
    }

    public boolean is戒指技能() {
        return SkillConstants.is召喚獸戒指(this.sourceid);
    }

    public boolean isSkill() {
        return this.sourceid >= 0;
    }

    public boolean is合金盔甲() {
        return this.isSkill() && (this.sourceid == 35001002 || this.sourceid == 35111003);
    }

    public boolean is召喚美洲豹() {
        return this.isSkill() && SkillConstants.is美洲豹(this.sourceid);
    }

    public boolean is拔刀姿勢() {
        return this.isSkill() && this.sourceid == 40011288;
    }

    public boolean makeChanceResult() {
        return this.getProp() >= 100 || Randomizer.nextInt(100) < this.getProp();
    }

    public boolean makeChanceResult(MapleCharacter chr) {
        return Randomizer.nextInt(100) < this.getProp(chr);
    }

    public int getProp() {
        return this.info.get((Object)MapleStatInfo.prop);
    }

    public int getProp(MapleCharacter chr) {
        int prop = this.getProp();
        return prop + chr.getStat().getAddSkillProp(this.sourceid);
    }

    public int getSubProp() {
        return this.info.get((Object)MapleStatInfo.subProp);
    }

    public int getIgnoreMobpdpR() {
        return this.info.get((Object)MapleStatInfo.ignoreMobpdpR);
    }

    public int getEnhancedHP() {
        return this.info.get((Object)MapleStatInfo.emhp);
    }

    public int getEnhancedMP() {
        return this.info.get((Object)MapleStatInfo.emmp);
    }

    public int getEnhancedWatk() {
        return this.info.get((Object)MapleStatInfo.epad);
    }

    public int getEnhancedMatk() {
        return this.info.get((Object)MapleStatInfo.emad);
    }

    public int getEnhancedWdef() {
        return this.info.get((Object)MapleStatInfo.epdd);
    }

    public int getEnhancedMdef() {
        return this.info.get((Object)MapleStatInfo.emdd);
    }

    public int getDot() {
        return this.info.get((Object)MapleStatInfo.dot);
    }

    public int getDotTime() {
        return this.info.get((Object)MapleStatInfo.dotTime);
    }

    public int getDotTime(MapleCharacter chr) {
        int dotTime = this.getDotTime() + chr.getStat().getDotTime(this.sourceid);
        return dotTime + dotTime * chr.getStat().incDotTime / 100;
    }

    public int getCritical() {
        return this.info.get((Object)MapleStatInfo.cr);
    }

    public int getCriticalMax() {
        return this.info.get((Object)MapleStatInfo.criticaldamageMax);
    }

    public int getCriticalMin() {
        return this.info.get((Object)MapleStatInfo.criticaldamageMin);
    }

    public int getArRate() {
        return this.info.get((Object)MapleStatInfo.ar);
    }

    public int getASRRate() {
        return this.info.get((Object)MapleStatInfo.asrR);
    }

    public int getTERRate() {
        return this.info.get((Object)MapleStatInfo.terR);
    }

    public int getDamR() {
        return this.info.get((Object)MapleStatInfo.damR);
    }

    public int getDamPlus() {
        return this.info.get((Object)MapleStatInfo.damPlus);
    }

    public int getDAMRate_5th() {
        return this.info.get((Object)MapleStatInfo.damR_5th);
    }

    public int getMdR() {
        return this.info.get((Object)MapleStatInfo.mdR);
    }

    public int getPdR() {
        return this.info.get((Object)MapleStatInfo.pdR);
    }

    public short getMesoRate() {
        return this.mesoR;
    }

    public int getEXP() {
        return this.exp;
    }

    public int getWdefToMdef() {
        return this.info.get((Object)MapleStatInfo.pdd2mdd);
    }

    public int getMdefToWdef() {
        return this.info.get((Object)MapleStatInfo.mdd2pdd);
    }

    public int getAvoidToHp() {
        return this.info.get((Object)MapleStatInfo.eva2hp);
    }

    public int getAccToMp() {
        return this.info.get((Object)MapleStatInfo.acc2mp);
    }

    public int getStrToDex() {
        return this.info.get((Object)MapleStatInfo.str2dex);
    }

    public int getDexToStr() {
        return this.info.get((Object)MapleStatInfo.dex2str);
    }

    public int getIntToLuk() {
        return this.info.get((Object)MapleStatInfo.int2luk);
    }

    public int getLukToDex() {
        return this.info.get((Object)MapleStatInfo.luk2dex);
    }

    public int getHpToDamageX() {
        return this.info.get((Object)MapleStatInfo.mhp2damX);
    }

    public int getMpToDamageX() {
        return this.info.get((Object)MapleStatInfo.mmp2damX);
    }

    public int getLv2mhp() {
        return this.info.get((Object)MapleStatInfo.lv2mhp);
    }

    public int getLv2mmp() {
        return this.info.get((Object)MapleStatInfo.lv2mmp);
    }

    public int getLevelToDamageX() {
        return this.info.get((Object)MapleStatInfo.lv2damX);
    }

    public int getLevelToWatk() {
        return this.info.get((Object)MapleStatInfo.lv2pad);
    }

    public int getLevelToMatk() {
        return this.info.get((Object)MapleStatInfo.lv2mad);
    }

    public int getLevelToWatkX() {
        return this.info.get((Object)MapleStatInfo.lv2pdX);
    }

    public int getLevelToMatkX() {
        return this.info.get((Object)MapleStatInfo.lv2mdX);
    }

    public int getEXPLossRate() {
        return this.info.get((Object)MapleStatInfo.expLossReduceR);
    }

    public int getBuffTimeRate() {
        return this.info.get((Object)MapleStatInfo.bufftimeR);
    }

    public int getSuddenDeathR() {
        return this.info.get((Object)MapleStatInfo.suddenDeathR);
    }

    public int getSummonTimeInc() {
        return this.info.get((Object)MapleStatInfo.summonTimeR);
    }

    public int getMPConsumeEff() {
        return this.info.get((Object)MapleStatInfo.mpConEff);
    }

    public int getPadX() {
        return this.info.get((Object)MapleStatInfo.padX);
    }

    public int getMadX() {
        return this.info.get((Object)MapleStatInfo.madX);
    }

    public int getMhpR() {
        return this.info.get((Object)MapleStatInfo.mhpR);
    }

    public int getMmpR() {
        return this.info.get((Object)MapleStatInfo.mmpR);
    }

    public int getIgnoreMobDamR() {
        return this.info.get((Object)MapleStatInfo.ignoreMobDamR);
    }

    public int getIndieIgnoreMobpdpR() {
        return this.info.get((Object)MapleStatInfo.indieIgnoreMobpdpR);
    }

    public int getDamAbsorbShieldR() {
        return this.info.get((Object)MapleStatInfo.damAbsorbShieldR);
    }

    public int getConsume() {
        return this.consumeOnPickup;
    }

    public int getSelfDestruction() {
        return this.info.get((Object)MapleStatInfo.selfDestruction);
    }

    public int getGauge() {
        return this.info.get((Object)MapleStatInfo.gauge);
    }

    public int getCharColor() {
        return this.charColor;
    }

    public List<Integer> getPetsCanConsume() {
        return this.petsCanConsume;
    }

    public boolean isReturnScroll() {
        return !this.isSkill() && this.moveTo != -1;
    }

    public int getRange() {
        return this.info.get((Object)MapleStatInfo.range);
    }

    public int getER() {
        return this.info.get((Object)MapleStatInfo.er);
    }

    public int getPrice() {
        return this.info.get((Object)MapleStatInfo.price);
    }

    public int getExtendPrice() {
        return this.info.get((Object)MapleStatInfo.extendPrice);
    }

    public int getPeriod() {
        return this.info.get((Object)MapleStatInfo.period);
    }

    public int getReqGuildLevel() {
        return this.info.get((Object)MapleStatInfo.reqGuildLevel);
    }

    public int getExpR() {
        return this.info.get((Object)MapleStatInfo.expR).byteValue();
    }

    public short getLifeID() {
        return this.lifeId;
    }

    public short getUseLevel() {
        return this.useLevel;
    }

    public byte getSlotCount() {
        return this.slotCount;
    }

    public byte getSlotPerLine() {
        return this.slotPerLine;
    }

    public int getStr() {
        return this.info.get((Object)MapleStatInfo.str);
    }

    public int getStrX() {
        return this.info.get((Object)MapleStatInfo.strX);
    }

    public int getStrFX() {
        return this.info.get((Object)MapleStatInfo.strFX);
    }

    public int getStrRate() {
        return this.info.get((Object)MapleStatInfo.strR);
    }

    public int getDex() {
        return this.info.get((Object)MapleStatInfo.dex);
    }

    public int getDexX() {
        return this.info.get((Object)MapleStatInfo.dexX);
    }

    public int getDexFX() {
        return this.info.get((Object)MapleStatInfo.dexFX);
    }

    public int getDexR() {
        return this.info.get((Object)MapleStatInfo.dexR);
    }

    public int getInt() {
        return this.info.get((Object)MapleStatInfo.int_);
    }

    public int getIntX() {
        return this.info.get((Object)MapleStatInfo.intX);
    }

    public int getIntFX() {
        return this.info.get((Object)MapleStatInfo.intFX);
    }

    public int getIntRate() {
        return this.info.get((Object)MapleStatInfo.intR);
    }

    public int getLuk() {
        return this.info.get((Object)MapleStatInfo.luk);
    }

    public int getLukX() {
        return this.info.get((Object)MapleStatInfo.lukX);
    }

    public int getLukFX() {
        return this.info.get((Object)MapleStatInfo.lukFX);
    }

    public int getLukRate() {
        return this.info.get((Object)MapleStatInfo.lukR);
    }

    public int getMaxHpX() {
        return this.info.get((Object)MapleStatInfo.mhpX);
    }

    public int getMaxMpX() {
        return this.info.get((Object)MapleStatInfo.mmpX);
    }

    public int getAccX() {
        return this.info.get((Object)MapleStatInfo.accX);
    }

    public int getPercentAcc() {
        return this.info.get((Object)MapleStatInfo.accR);
    }

    public int getAvoidX() {
        return this.info.get((Object)MapleStatInfo.evaX);
    }

    public int getPercentAvoid() {
        return this.info.get((Object)MapleStatInfo.evaR);
    }

    public int getPddX() {
        return this.info.get((Object)MapleStatInfo.pddX);
    }

    public int getMdefX() {
        return this.info.get((Object)MapleStatInfo.mddX);
    }

    public int getIndieMHp() {
        return this.info.get((Object)MapleStatInfo.indieMhp);
    }

    public int getIndieMMp() {
        return this.info.get((Object)MapleStatInfo.indieMmp);
    }

    public int getIndieMhpR() {
        return this.info.get((Object)MapleStatInfo.indieMhpR);
    }

    public int getIndieMmpR() {
        return this.info.get((Object)MapleStatInfo.indieMmpR);
    }

    public int getIndieAllStat() {
        return this.info.get((Object)MapleStatInfo.indieAllStat);
    }

    public int getIndieCr() {
        return this.info.get((Object)MapleStatInfo.indieCr);
    }

    public int getEpdd() {
        return this.info.get((Object)MapleStatInfo.epad);
    }

    public int getNocoolProp() {
        return this.info.get((Object)MapleStatInfo.nocoolProp);
    }

    public short getIndiePdd() {
        return this.indiePdd;
    }

    public short getIndieMdd() {
        return this.indieMdd;
    }

    public int getIndieDamR() {
        return this.info.get((Object)MapleStatInfo.indieDamR);
    }

    public int getIndieBooster() {
        return this.info.get((Object)MapleStatInfo.indieBooster);
    }

    public byte getType() {
        return this.type;
    }

    public int getBossDamage() {
        return this.info.get((Object)MapleStatInfo.bdR);
    }

    public int getMobCountDamage() {
        return this.info.get((Object)MapleStatInfo.mobCountDamR);
    }

    public int getInterval() {
        return this.interval;
    }

    public List<Pair<Integer, Integer>> getAvailableMaps() {
        return this.availableMap;
    }

    public int getPddR() {
        return this.info.get((Object)MapleStatInfo.pddR);
    }

    public int getMDEFRate() {
        return this.info.get((Object)MapleStatInfo.mddR);
    }

    public int getKillSpree() {
        return this.info.get((Object)MapleStatInfo.kp);
    }

    public int getMaxDamageOver() {
        return this.info.get((Object)MapleStatInfo.MDamageOver);
    }

    public int getIndieMaxDamageOver() {
        return this.info.get((Object)MapleStatInfo.indieMaxDamageOver);
    }

    public int getCostMpRate() {
        return this.info.get((Object)MapleStatInfo.costmpR);
    }

    public int getMPConReduce() {
        return this.info.get((Object)MapleStatInfo.mpConReduce);
    }

    public int getIndieMaxDF() {
        return this.info.get((Object)MapleStatInfo.MDF);
    }

    public int getTargetPlus() {
        return this.info.get((Object)MapleStatInfo.targetPlus);
    }

    public int getTargetPlus_5th() {
        return this.info.get((Object)MapleStatInfo.targetPlus_5th);
    }

    public int getForceCon() {
        return this.info.get((Object)MapleStatInfo.forceCon);
    }

    public int getAtGauge1Con() {
        return this.info.get((Object)MapleStatInfo.atGauge1Con);
    }

    public int getAtGauge2Con() {
        return this.info.get((Object)MapleStatInfo.atGauge2Con);
    }

    public int getAtGauge2Inc() {
        return this.info.get((Object)MapleStatInfo.atGauge2Inc);
    }

    public int getAtSkillType() {
        return this.info.get((Object)MapleStatInfo.atSkillType);
    }

    public int getForceCon(MapleCharacter chr) {
        int forceCon = this.getForceCon();
        return forceCon - forceCon * chr.getStat().getSkillReduceForceCon(this.sourceid) / 100;
    }

    public int getReduceForceR() {
        return this.info.get((Object)MapleStatInfo.reduceForceR);
    }

    public int getSoulMpCon() {
        return this.info.get((Object)MapleStatInfo.soulmpCon);
    }

    public int getPPCon() {
        return this.info.get((Object)MapleStatInfo.ppCon);
    }

    public int getKillRecoveryR() {
        return this.info.get((Object)MapleStatInfo.killRecoveryR);
    }

    public boolean isOnRule() {
        return this.ruleOn;
    }

    public boolean is疾風() {
        return this.isSkill() && this.sourceid == 15120003 || this.sourceid == 15111022;
    }

    public void applyAffectedArea(MapleCharacter chr, Point pos) {
        if (pos == null) {
            pos = chr.getPosition();
        }
        MapleAffectedArea area = new MapleAffectedArea(this.calculateBoundingBox(new Point(pos.x, pos.y + (this.sourceid == 400010010 ? 40 : 0)), chr.isFacingLeft()), chr, this, new Point(pos));
        if (chr.isDebug()) {
            chr.dropSpouseMessage(UserChatMessageType.公告, "[Affected Area]技能：" + String.valueOf(area.getEffect()) + " 持續時間：" + area.getDuration());
        }
        if (!area.isPoisonMist() && this.sourceid != 2321015 && this.sourceid != 400051076 && this.sourceid != 400041000) {
            chr.getMap().removeAffectedArea(chr.getId(), this.sourceid);
        }
        chr.getMap().createAffectedArea(area);
    }

    public void applyToMonster(MapleCharacter chr, int duration) {
        int n2 = 0;
        for (MapleMapObject monster : chr.getMap().getMapObjectsInRect(this.calculateBoundingBox(chr.getPosition(), chr.isFacingLeft()), Collections.singletonList(MapleMapObjectType.MONSTER))) {
            if (this.makeChanceResult(chr) && n2 < this.getMobCount()) {
                this.applyMonsterEffect(chr, (MapleMonster)monster, duration);
            }
            ++n2;
        }
    }

    public boolean applyMonsterEffect(MapleCharacter chr, MapleMonster monster, int duration) {
        if (monster == null || !monster.isAlive() || !this.isSkill()) {
            return false;
        }
        int prop = this.getProp(chr);
        Map<MonsterStatus, Integer> localstatups = new EnumMap<MonsterStatus, Integer>(this.monsterStatus);
        AbstractSkillHandler handler = this.getSkillHandler();
        if (handler == null) {
            handler = SkillClassFetcher.getHandlerByJob(chr.getJobWithSub());
        }
        int handleRes = -1;
        if (handler != null) {
            SkillClassApplier applier = new SkillClassApplier();
            applier.effect = this;
            applier.duration = duration;
            applier.prop = prop;
            applier.localmobstatups = localstatups;
            handleRes = handler.onApplyMonsterEffect(chr, monster, applier);
            if (handleRes == 0) {
                return false;
            }
            if (handleRes == 1) {
                prop = applier.prop;
                duration = applier.duration;
                localstatups = applier.localmobstatups;
            }
        }
        Skill skill = this.getSkill();
        int sourceid = this.sourceid;
        if (skill == null) {
            return false;
        }
        switch (monster.getStats().getEffectiveness(skill.getElement())) {
            case 免疫: 
            case 增強: {
                return false;
            }
        }
        if (monster.getStats().isEscort() || monster.isFake() || monster.getStats().isNoDoom() && localstatups.containsKey(MonsterStatus.Venom)) {
            return false;
        }
        EnumMap<MonsterStatus, MonsterEffectHolder> statups = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
        if (prop == 0 || Randomizer.isSuccess(prop)) {
            block40: for (Map.Entry<MonsterStatus, Integer> entry : localstatups.entrySet()) {
                MonsterStatus status = entry.getKey();
                if (!skill.isMesToBoss() && !skill.MesList.contains((Object)SkillMesInfo.restrict) && SkillConstants.isMoveImpactStatus(status) && (monster.isBoss() || monster.getStats().isIgnoreMoveImpact()) && (skill.getId() == 4121015 && chr.getSkillLevel(4120048) <= 0 && monster.isBoss() || skill.getId() != 4121015 && skill.getId() != 142121031)) continue;
                if (!SkillConstants.isSmiteStatus(status) || sourceid == 164121044 || sourceid != 80011540 && monster.canNextSmite() || sourceid == 80011540 && monster.canNextLucidSmite()) {
                    MonsterEffectHolder myPoison;
                    boolean b;
                    switch (sourceid) {
                        case 32121018: {
                            if (!monster.isBuffed(status)) break;
                            continue block40;
                        }
                        case 4110011: 
                        case 4120011: 
                        case 4210010: 
                        case 4220011: 
                        case 4320005: 
                        case 4340012: {
                            switch (monster.getStats().getEffectiveness(Element.毒)) {
                                case 免疫: 
                                case 增強: {
                                    return false;
                                }
                            }
                            break;
                        }
                        case 64121016: {
                            sourceid = 64121011;
                            break;
                        }
                        case 164111008: {
                            if (status != MonsterStatus.Morph || !monster.isBoss()) break;
                            continue block40;
                        }
                    }
                    int value = entry.getValue();
                    int z = 0;
                    if (status == MonsterStatus.Speed && JobConstants.is冰雷(chr.getJob())) {
                        z = 1;
                        Object obj = chr.getTempValues().remove("冰雪之精神攻擊數量");
                        if (obj instanceof Boolean && ((Boolean)obj).booleanValue()) {
                            z = 3;
                        }
                    }
                    if (status == MonsterStatus.Morph) {
                        value = Randomizer.nextBoolean() ? 2400500 : 2400501;
                    }
                    switch (sourceid) {
                        case 4121017: {
                            MapleStatEffect eff = chr.getSkillEffect(0x3EDDED);
                            if (eff == null) break;
                            value += eff.getX();
                            break;
                        }
                        case 4121015: {
                            MapleStatEffect eff = chr.getSkillEffect(0x3EDDEE);
                            if (eff != null) {
                                switch (status) {
                                    case IndiePDR: {
                                        value += eff.getX();
                                        break;
                                    }
                                    case PAD: {
                                        value += eff.getZ();
                                    }
                                }
                            }
                            if ((eff = chr.getSkillEffect(4120047)) == null) break;
                            switch (status) {
                                case Speed: {
                                    value += eff.getY();
                                }
                            }
                        }
                    }
                    block23 : switch (sourceid) {
                        case 400041025: {
                            b = true;
                            break;
                        }
                        case 2321007: {
                            z = 1;
                        }
                        case 3120017: 
                        case 33000036: 
                        case 36110005: 
                        case 101110103: 
                        case 152000010: {
                            b = monster.getEffectHolder(status, status.isIndieStat() ? sourceid : -1) != null;
                            break;
                        }
                        case 14001021: {
                            b = monster.getEffectHolder(chr.getId(), MonsterStatus.Burned, sourceid) != null;
                            break;
                        }
                        default: {
                            switch (status) {
                                case CurseTransition: {
                                    b = true;
                                    break block23;
                                }
                                case Speed: {
                                    b = JobConstants.is冰雷(chr.getJob());
                                    break block23;
                                }
                            }
                            b = false;
                        }
                    }
                    if (b && (myPoison = monster.getEffectHolder(chr.getId(), status, status.isIndieStat() ? sourceid : -1)) != null) {
                        switch (sourceid) {
                            case 2321007: {
                                z = Math.min(5, myPoison.z + z);
                                break;
                            }
                            case 152000010: {
                                int max = 1;
                                if (chr.getSkillEffect(152100012) != null) {
                                    max = 5;
                                } else if (chr.getSkillEffect(152110010) != null) {
                                    max = 3;
                                }
                                value = Math.min(max, myPoison.value + value);
                                break;
                            }
                            default: {
                                if (status == MonsterStatus.Speed && JobConstants.is冰雷(chr.getJob())) {
                                    z = Math.min(5, myPoison.z + z);
                                    value = Math.max(-75, myPoison.value + value);
                                    break;
                                }
                                value = status == MonsterStatus.CurseTransition ? Math.min(myPoison.effect == null ? 5 : myPoison.effect.getX(), myPoison.value + value) : Math.min(3, myPoison.value + 1);
                            }
                        }
                    }
                    int localDuration = duration;
                    switch (sourceid) {
                        case 4321002: {
                            if (!monster.isBoss()) break;
                            localDuration = localDuration * 50 / 100;
                        }
                    }
                    MonsterEffectHolder holder = new MonsterEffectHolder(chr.getId(), value, System.currentTimeMillis(), localDuration, this);
                    if (status == MonsterStatus.AddDamSkill && this.getSourceId() == 23121003) {
                        holder.sourceID = 23121000;
                    }
                    holder.z = z;
                    holder.moboid = monster.getSeperateSoulSrcOID();
                    if (status == MonsterStatus.Burned) {
                        this.setDotData(chr, holder);
                        value = holder.value;
                    }
                    statups.put(status, holder);
                    if (!chr.isDebug()) continue;
                    chr.dropDebugMessage(0, "[MobBuff] Register Stat:" + String.valueOf(status) + " value:" + value);
                    continue;
                }
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.LP_MobTimeResist.getValue());
                mplew.writeInt(monster.getObjectId());
                mplew.writeInt(1);
                mplew.writeInt(sourceid);
                mplew.writeShort(92);
                mplew.writeInt(chr.getId());
                mplew.write(1);
                chr.getMap().broadcastMessage(chr, mplew.getPacket(), true);
            }
            if (localstatups.containsKey(MonsterStatus.MagicCrash) || sourceid == 24121007) {
                ArrayList<MonsterStatus> list = new ArrayList<MonsterStatus>();
                list.add(MonsterStatus.PGuardUp);
                list.add(MonsterStatus.MGuardUp);
                list.add(MonsterStatus.PowerUp);
                list.add(MonsterStatus.MagicUp);
                list.add(MonsterStatus.HardSkin);
                if (sourceid == 24121007) {
                    list.add(MonsterStatus.PCounter);
                    list.add(MonsterStatus.MCounter);
                }
                monster.removeEffect(list);
            }
            if (chr.isDebug()) {
                chr.dropDebugMessage(1, "[MobBuff] Register Effect:" + String.valueOf(this) + " Duration:" + duration);
            }
        }
        if (!statups.isEmpty()) {
            monster.registerEffect(statups);
            LinkedHashMap<MonsterStatus, Integer> writeStatups = new LinkedHashMap<MonsterStatus, Integer>();
            for (MonsterStatus stat : statups.keySet()) {
                writeStatups.put(stat, sourceid);
            }
            chr.getMap().broadcastMessage(MobPacket.mobStatSet(monster, writeStatups), monster.getPosition());
        }
        return !statups.isEmpty();
    }

    public void setDotData(MapleCharacter chr, MonsterEffectHolder holder) {
        long damage = Math.min(chr.getCalcDamage().getRandomDamage(chr, false) * (long)this.getDot() / 100L, Integer.MAX_VALUE);
        holder.value = (int)damage * holder.value;
        holder.dotInterval = this.getDotInterval() * 1000;
        holder.dotDamage = damage;
        holder.localDuration = this.getDotTime(chr) * 1000;
        holder.dotSuperpos = this.info.get((Object)MapleStatInfo.dotSuperpos);
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public Map<MapleStatInfo, Integer> getInfo() {
        return this.info;
    }

    public Map<MapleStatInfo, Double> getInfoD() {
        return this.infoD;
    }

    public Map<MapleTraitType, Integer> getTraits() {
        return this.traits;
    }

    public List<Pair<Integer, Integer>> getAvailableMap() {
        return this.availableMap;
    }

    public Point getLt() {
        return this.lt;
    }

    public Point getRb() {
        return this.rb;
    }

    public Point getLt2() {
        return this.lt2;
    }

    public Point getRb2() {
        return this.rb2;
    }

    public Point getLt3() {
        return this.lt3;
    }

    public Point getRb3() {
        return this.rb3;
    }

    public List<SecondaryStat> getCureDebuffs() {
        return this.cureDebuffs;
    }

    public List<Integer> getFamiliars() {
        return this.familiars;
    }

    public List<Integer> getRandomPickup() {
        return this.randomPickup;
    }

    public List<Triple<Integer, Integer, Integer>> getRewardItem() {
        return this.rewardItem;
    }

    public byte getRecipeUseCount() {
        return this.recipeUseCount;
    }

    public byte getRecipeValidDay() {
        return this.recipeValidDay;
    }

    public byte getReqSkillLevel() {
        return this.reqSkillLevel;
    }

    public byte getEffectedOnAlly() {
        return this.effectedOnAlly;
    }

    public byte getEffectedOnEnemy() {
        return this.effectedOnEnemy;
    }

    public byte getPreventslip() {
        return this.preventslip;
    }

    public byte getImmortal() {
        return this.immortal;
    }

    public byte getBs() {
        return this.bs;
    }

    public short getIgnoreMob() {
        return this.ignoreMob;
    }

    public int getDropR() {
        return this.info.get((Object)MapleStatInfo.dropR);
    }

    public int getMesoR() {
        return this.info.get((Object)MapleStatInfo.mesoR);
    }

    public short getThaw() {
        return this.thaw;
    }

    public short getImhp() {
        return this.imhp;
    }

    public short getImmp() {
        return this.immp;
    }

    public short getMobSkill() {
        return this.mobSkill;
    }

    public short getMobSkillLevel() {
        return this.mobSkillLevel;
    }

    public int getSourceId() {
        return this.sourceid;
    }

    public int getRecipe() {
        return this.recipe;
    }

    public int getMoveTo() {
        return this.moveTo;
    }

    public int getMorphId() {
        return this.morphId;
    }

    public int getExpinc() {
        return this.expinc;
    }

    public int getConsumeOnPickup() {
        return this.consumeOnPickup;
    }

    public int getRewardMeso() {
        return this.rewardMeso;
    }

    public int getTotalprob() {
        return this.totalprob;
    }

    public int getCosmetic() {
        return this.cosmetic;
    }

    public int getExpBuff() {
        return this.expBuff;
    }

    public int getItemup() {
        return this.itemup;
    }

    public int getMesoup() {
        return this.mesoup;
    }

    public int getCashup() {
        return this.cashup;
    }

    public int getIllusion() {
        return this.illusion;
    }

    public int getBooster() {
        return this.booster;
    }

    public int getBerserk2() {
        return this.berserk2;
    }

    public boolean isRuleOn() {
        return this.ruleOn;
    }

    public boolean isBxi() {
        return this.bxi;
    }

    public boolean isHit() {
        return this.hit;
    }

    public void setInfo(Map<MapleStatInfo, Integer> info) {
        this.info = info;
    }

    public void setInfoD(Map<MapleStatInfo, Double> info) {
        this.infoD = info;
    }

    public void setTraits(Map<MapleTraitType, Integer> traits) {
        this.traits = traits;
    }

    public void setOverTime(boolean overTime) {
        this.overTime = overTime;
    }

    public void setPartyBuff(boolean partyBuff) {
        this.partyBuff = partyBuff;
    }

    public void setRangeBuff(boolean rangeBuff) {
        this.rangeBuff = rangeBuff;
    }

    public void setNotRemoved(boolean notRemoved) {
        this.notRemoved = notRemoved;
    }

    public void setNotIncBuffDuration(boolean notIncBuffDuration) {
        this.notIncBuffDuration = notIncBuffDuration;
    }

    public void setStatups(EnumMap<SecondaryStat, Integer> statups) {
        this.statups = statups;
    }

    public void setAvailableMap(List<Pair<Integer, Integer>> availableMap) {
        this.availableMap = availableMap;
    }

    public void setMonsterStatus(Map<MonsterStatus, Integer> monsterStatus) {
        this.monsterStatus = monsterStatus;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCureDebuffs(List<SecondaryStat> cureDebuffs) {
        this.cureDebuffs = cureDebuffs;
    }

    public void setPetsCanConsume(List<Integer> petsCanConsume) {
        this.petsCanConsume = petsCanConsume;
    }

    public void setFamiliars(List<Integer> familiars) {
        this.familiars = familiars;
    }

    public void setRandomPickup(List<Integer> randomPickup) {
        this.randomPickup = randomPickup;
    }

    public void setRewardItem(List<Triple<Integer, Integer, Integer>> rewardItem) {
        this.rewardItem = rewardItem;
    }

    public void setSlotCount(byte slotCount) {
        this.slotCount = slotCount;
    }

    public void setSlotPerLine(byte slotPerLine) {
        this.slotPerLine = slotPerLine;
    }

    public void setRecipeUseCount(byte recipeUseCount) {
        this.recipeUseCount = recipeUseCount;
    }

    public void setRecipeValidDay(byte recipeValidDay) {
        this.recipeValidDay = recipeValidDay;
    }

    public void setReqSkillLevel(byte reqSkillLevel) {
        this.reqSkillLevel = reqSkillLevel;
    }

    public void setEffectedOnAlly(byte effectedOnAlly) {
        this.effectedOnAlly = effectedOnAlly;
    }

    public void setEffectedOnEnemy(byte effectedOnEnemy) {
        this.effectedOnEnemy = effectedOnEnemy;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setPreventslip(byte preventslip) {
        this.preventslip = preventslip;
    }

    public void setImmortal(byte immortal) {
        this.immortal = immortal;
    }

    public void setBs(byte bs) {
        this.bs = bs;
    }

    public void setIgnoreMob(short ignoreMob) {
        this.ignoreMob = ignoreMob;
    }

    public void setMesoR(short mesoR) {
        this.mesoR = mesoR;
    }

    public void setThaw(short thaw) {
        this.thaw = thaw;
    }

    public void setLifeId(short lifeId) {
        this.lifeId = lifeId;
    }

    public void setImhp(short imhp) {
        this.imhp = imhp;
    }

    public void setImmp(short immp) {
        this.immp = immp;
    }

    public void setInflation(short inflation) {
        this.inflation = inflation;
    }

    public void setUseLevel(short useLevel) {
        this.useLevel = useLevel;
    }

    public void setIndiePdd(short indiePdd) {
        this.indiePdd = indiePdd;
    }

    public void setIndieMdd(short indieMdd) {
        this.indieMdd = indieMdd;
    }

    public void setMobSkill(short mobSkill) {
        this.mobSkill = mobSkill;
    }

    public void setMobSkillLevel(short mobSkillLevel) {
        this.mobSkillLevel = mobSkillLevel;
    }

    public void setHpR(double hpR) {
        this.hpR = hpR;
    }

    public void setMpR(double mpR) {
        this.mpR = mpR;
    }

    public void setSourceid(int sourceid) {
        this.sourceid = sourceid;
    }

    public void setRecipe(int recipe) {
        this.recipe = recipe;
    }

    public void setMoveTo(int moveTo) {
        this.moveTo = moveTo;
    }

    public void setMoneyCon(int moneyCon) {
        this.moneyCon = moneyCon;
    }

    public void setMorphId(int morphId) {
        this.morphId = morphId;
    }

    public void setExpinc(int expinc) {
        this.expinc = expinc;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setConsumeOnPickup(int consumeOnPickup) {
        this.consumeOnPickup = consumeOnPickup;
    }

    public void setCharColor(int charColor) {
        this.charColor = charColor;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setRewardMeso(int rewardMeso) {
        this.rewardMeso = rewardMeso;
    }

    public void setTotalprob(int totalprob) {
        this.totalprob = totalprob;
    }

    public void setCosmetic(int cosmetic) {
        this.cosmetic = cosmetic;
    }

    public void setExpBuff(int expBuff) {
        this.expBuff = expBuff;
    }

    public void setItemup(int itemup) {
        this.itemup = itemup;
    }

    public void setMesoup(int mesoup) {
        this.mesoup = mesoup;
    }

    public void setCashup(int cashup) {
        this.cashup = cashup;
    }

    public void setBerserk(int berserk) {
        this.berserk = berserk;
    }

    public void setIllusion(int illusion) {
        this.illusion = illusion;
    }

    public void setBooster(int booster) {
        this.booster = booster;
    }

    public void setBerserk2(int berserk2) {
        this.berserk2 = berserk2;
    }

    public void setRuleOn(boolean ruleOn) {
        this.ruleOn = ruleOn;
    }

    public void setBxi(boolean bxi) {
        this.bxi = bxi;
    }

    public void applyToParty(MapleMap map, MapleCharacter player) {
        if (player.getParty() != null) {
            for (MapleCharacter chr : map.getPartyMembersInRange(player.getParty(), player.getPosition(), 500)) {
                if (chr.getBuffStatValueHolder(this.sourceid) != null) continue;
                this.applyBuffEffect(player, chr, 10000, true, false, true, chr.getPosition());
            }
        }
        if (!this.monsterStatus.isEmpty()) {
            this.applyToMonster(player, 5000);
        }
    }

    public boolean MakeDebuffChanceResult() {
        return this.info.get((Object)MapleStatInfo.subProp) >= 100 || Randomizer.nextInt(100) < this.info.get((Object)MapleStatInfo.subProp) || this.info.get((Object)MapleStatInfo.prop) >= 100 || Randomizer.nextInt(100) < this.info.get((Object)MapleStatInfo.prop) || this.info.get((Object)MapleStatInfo.hcProp) >= 100 || Randomizer.nextInt(100) < this.info.get((Object)MapleStatInfo.hcProp);
    }

    public Integer getHcTime() {
        return this.info.get((Object)MapleStatInfo.hcTime);
    }

    public int gethcSubProp() {
        return this.info.get((Object)MapleStatInfo.hcSubProp);
    }

    public int getHpRCon() {
        return this.info.get((Object)MapleStatInfo.hpRCon);
    }

    public int getHpCon() {
        return this.info.get((Object)MapleStatInfo.hpCon);
    }

    public int getPowerCon() {
        return this.info.get((Object)MapleStatInfo.powerCon);
    }

    public int getIndieBDR() {
        return this.info.get((Object)MapleStatInfo.indieBDR);
    }

    public int getIndiePMdR() {
        return this.info.get((Object)MapleStatInfo.indiePMdR);
    }

    public int getCriticalDamage() {
        return this.info.get((Object)MapleStatInfo.criticaldamage);
    }

    public int getStanceProp() {
        return this.info.get((Object)MapleStatInfo.stanceProp);
    }

    public int getU2() {
        return this.info.get((Object)MapleStatInfo.u2);
    }

    public int getHcHp() {
        return this.info.get((Object)MapleStatInfo.hcHp);
    }

    public boolean isNotIncBuffDuration() {
        return this.notIncBuffDuration;
    }

    public boolean is血腥盛宴() {
        return this.isSkill() && (this.sourceid == 400011062 || this.sourceid == 400011063 || this.sourceid == 400011064);
    }

    public String toString() {
        return (this.isSkill() ? SkillFactory.getSkillName(this.sourceid) : MapleItemInformationProvider.getInstance().getName(Math.abs(this.sourceid))) + "[" + this.sourceid + "] Level：" + this.getLevel();
    }

    public static class CancelEffectAction
    implements Runnable {
        private final MapleStatEffect effect;
        private WeakReference<MapleCharacter> target;
        private final long startTime;
        private final Map<SecondaryStat, Integer> statup;
        private int targetID;

        public CancelEffectAction(MapleCharacter target, MapleStatEffect effect, long startTime, Map<SecondaryStat, Integer> statup) {
            this.effect = effect;
            this.target = new WeakReference<MapleCharacter>(target);
            this.startTime = startTime;
            this.statup = statup;
            this.targetID = target.getId();
        }

        public void changeTarget(MapleCharacter target) {
            this.target = new WeakReference<MapleCharacter>(target);
            this.targetID = target.getId();
        }

        @Override
        public void run() {
            MapleCharacter realTarget = (MapleCharacter)this.target.get();
            if (realTarget != null) {
                realTarget.cancelEffect(this.effect, false, this.startTime, this.statup);
            }
            World.TemporaryStat.CancelStat(this.targetID, new LinkedList<SecondaryStat>(this.statup.keySet()), this.effect, this.startTime);
        }
    }
}

