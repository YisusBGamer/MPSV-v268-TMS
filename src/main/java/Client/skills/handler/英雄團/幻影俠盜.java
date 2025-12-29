/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.英雄團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.SavedLocationType;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 幻影俠盜
extends AbstractSkillHandler {
    public 幻影俠盜() {
        this.jobs = new MapleJob[]{MapleJob.幻影俠盜, MapleJob.幻影俠盜1轉, MapleJob.幻影俠盜2轉, MapleJob.幻影俠盜3轉, MapleJob.幻影俠盜4轉};
        for (Field field : Config.constants.skills.幻影俠盜.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int baseSkills(MapleCharacter chr, SkillClassApplier applier) {
        Skill skil;
        int[] ss;
        for (int i : ss = new int[]{20031005, 20031203, 20031205, 20030206, 20031207, 20031208, 20031260}) {
            if (chr.getLevel() < 200 && i == 20031005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        if (chr.getJob() == MapleJob.幻影俠盜4轉.getId()) {
            skil = SkillFactory.getSkill(20031210);
            if (skil != null && chr.getSkillLevel(skil) <= 0) {
                applier.skillMap.put(20031210, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
            }
            if ((skil = SkillFactory.getSkill(20031209)) != null && chr.getSkillLevel(skil) > 0) {
                applier.skillMap.put(20031209, new SkillEntry(0, 0, -1L));
            }
        } else {
            skil = SkillFactory.getSkill(20031209);
            if (skil != null && chr.getSkillLevel(skil) <= 0) {
                applier.skillMap.put(20031209, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
            }
            if ((skil = SkillFactory.getSkill(20031210)) != null && chr.getSkillLevel(skil) > 0) {
                applier.skillMap.put(20031210, new SkillEntry(0, 0, -1L));
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 24141501: 
            case 24141502: {
                return 24141500;
            }
            case 24141000: {
                return 24121005;
            }
            case 500004092: {
                return 400041009;
            }
            case 500004093: {
                return 400041022;
            }
            case 500004094: {
                return 400041040;
            }
            case 500004095: {
                return 400041055;
            }
            case 24001005: {
                return 24001002;
            }
            case 24111008: {
                return 24111006;
            }
            case 24121010: {
                return 24121003;
            }
            case 24121011: {
                return 24120002;
            }
            case 24120055: {
                return 24121052;
            }
            case 400041010: 
            case 400041011: 
            case 400041012: 
            case 400041013: 
            case 400041014: 
            case 400041015: {
                return 400041009;
            }
            case 400041023: 
            case 400041024: 
            case 400041080: {
                return 400041022;
            }
            case 400041045: 
            case 400041046: {
                return 400041040;
            }
            case 400041056: {
                return 400041055;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 20031005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 20031209: 
            case 20031210: {
                statups.put(SecondaryStat.Judgement, 0);
                return 1;
            }
            case 20031205: {
                statups.put(SecondaryStat.Invisible, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndieCooltimeReduce, effect.getInfo().get((Object)MapleStatInfo.indieCooltimeReduce));
                return 1;
            }
            case 24121004: {
                statups.put(SecondaryStat.DamR, effect.getInfo().get((Object)MapleStatInfo.damR));
                statups.put(SecondaryStat.IgnoreTargetDEF, effect.getInfo().get((Object)MapleStatInfo.damR));
                return 1;
            }
            case 24111002: {
                effect.getInfo().put(MapleStatInfo.time, 900000);
                statups.put(SecondaryStat.ReviveOnce, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 24121010: {
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 24121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400041011: {
                statups.put(SecondaryStat.DotHealHPPerSecond, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.DotHealMPPerSecond, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400041012: {
                return 1;
            }
            case 400041014: {
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.indieAsrR));
                return 1;
            }
            case 400041015: {
                statups.put(SecondaryStat.DotHealHPPerSecond, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.DotHealMPPerSecond, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400041009: {
                statups.put(SecondaryStat.KeyDownMoving, 50);
                return 1;
            }
            case 400041010: {
                effect.getInfo().put(MapleStatInfo.bulletCount, 60);
                return 1;
            }
            case 20031203: {
                effect.setMoveTo(effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400041040: {
                statups.put(SecondaryStat.PhantomMarkOfPhantomTarget, 1);
                statups.put(SecondaryStat.PhantomMarkOfPhantomOwner, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 20031203: {
                chr.saveLocation(SavedLocationType.CRYSTALGARDEN);
                return 1;
            }
            case 20031209: 
            case 20031210: {
                if (chr.getJudgementStack() > 0) {
                    chr.setJudgementStack(0);
                    chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0)), true);
                    return 1;
                }
                c.announce(MaplePacketCreator.sendSkillUseResult(false, 0));
                return 1;
            }
            case 20031205: {
                chr.getSkillEffect(20031205).unprimaryPassiveApplyTo(chr);
                return 1;
            }
            case 400041022: {
                MapleForceAtom mfa = forceFactory.getMapleForce(chr, applier.effect, 0);
                byte unk1 = slea.readByte();
                byte stat = slea.readByte();
                byte unk2 = slea.readByte();
                byte unk3 = slea.readByte();
                mfa.setFirstMobID(slea.readInt());
                slea.skip(3);
                mfa.getRect().x = slea.readInt();
                mfa.getRect().y = slea.readInt();
                chr.putAtomAttackRecord(400041080, 0);
                chr.putAtomAttackRecord(400041022, 0);
                chr.getTempValues().put("useBlackJack", false);
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(mfa), true);
                if (stat == 2) {
                    chr.getClient().announce(MaplePacketCreator.SetForceAtomTarget(400041023, c.getPlayer().getId(), 3, mfa.getFirstMobID()));
                    chr.getTempValues().put("useBlackJack", true);
                }
                return 1;
            }
            case 400041040: {
                LinkedList<ExtraSkill> eskills = new LinkedList<ExtraSkill>();
                for (int i = 0; i < 7; ++i) {
                    ExtraSkill eskill = new ExtraSkill(i < 6 ? 400041045 : 400041046, chr.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = chr.isFacingLeft() ? 0 : 1;
                    eskills.add(eskill);
                }
                chr.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), eskills));
                chr.dispelEffect(400041040);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 20031203: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 20031209: 
            case 20031210: {
                int dice = Randomizer.nextInt(applier.effect.getSourceId() == 20031209 ? 2 : 4);
                applier.buffz = applier.effect.getV();
                switch (dice) {
                    case 0: {
                        applier.buffz = applier.effect.getV();
                        break;
                    }
                    case 1: {
                        applier.buffz = applier.effect.getW();
                        break;
                    }
                    case 2: {
                        applier.buffz = applier.effect.getX() * 100 + applier.effect.getY();
                        break;
                    }
                    case 3: {
                        ++dice;
                        applier.buffz = applier.effect.getZ();
                    }
                }
                applier.localstatups.put(SecondaryStat.Judgement, dice + 1);
                applyto.getClient().announce(EffectPacket.showDiceEffect(-1, applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false), false);
                return 1;
            }
            case 24111002: {
                if (!applier.primary) {
                    applier.localstatups.clear();
                    applier.duration = 4000;
                    applier.localstatups.put(SecondaryStat.NotDamaged, 1);
                    applyto.registerSkillCooldown(applier.effect, true);
                }
                return 1;
            }
            case 24121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(21121053);
                applyto.dispelEffect(22171082);
                applyto.dispelEffect(27121053);
                applyto.dispelEffect(23121053);
                applyto.dispelEffect(24121053);
                applyto.dispelEffect(25121132);
                return 1;
            }
            case 400041011: 
            case 400041012: 
            case 400041013: 
            case 400041014: 
            case 400041015: {
                applyto.getClient().announce(applier.effect.isSkill() ? EffectPacket.showBuffEffect(applyto, false, 400041009, applier.effect.getLevel(), 1, applier.pos) : EffectPacket.showBuffItemEffect(-1, applier.effect.getSourceId()));
                applier.b3 = true;
                return 1;
            }
            case 400041040: {
                if (!applier.primary) {
                    applier.localstatups.put(SecondaryStat.PhantomMarkOfPhantomTarget, Math.min(7, Math.max(0, applyto.getBuffedIntValue(SecondaryStat.PhantomMarkOfPhantomTarget) + 1)));
                    return 1;
                }
                applier.overwrite = false;
                applier.localstatups.clear();
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.effect != null && applier.effect.getSourceId() == 24121052) {
            applier.effect.applyAffectedArea(applyfrom, applyto.getPosition());
        }
        MapleStatEffect skillEffect18 = applyfrom.getSkillEffect(400041040);
        if (applier.effect != null && this.containsJob(applier.effect.getSourceId() / 10000) && applier.effect.getSourceId() % 10000 / 1000 == 1 && skillEffect18 != null) {
            if (applyto.getObjectId() != applyfrom.getLinkMobObjectID()) {
                applyfrom.setLinkMobObjectID(applyto.getObjectId());
                applyfrom.dispelEffect(400041040);
            }
            skillEffect18.unprimaryPassiveApplyTo(applyfrom);
        }
        return 1;
    }
}

