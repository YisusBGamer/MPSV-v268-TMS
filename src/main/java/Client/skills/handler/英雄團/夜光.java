/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.英雄團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 夜光
extends AbstractSkillHandler {
    public 夜光() {
        this.jobs = new MapleJob[]{MapleJob.夜光, MapleJob.夜光1轉, MapleJob.夜光2轉, MapleJob.夜光3轉, MapleJob.夜光4轉};
        for (Field field : Config.constants.skills.夜光.class.getDeclaredFields()) {
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
        if (chr.getLevel() >= 10) {
            int[] fixskills;
            Skill skill;
            int[] ss;
            for (int i : ss = new int[]{20041005, 20040216, 20040217, 20040219, 20040221, 20041222}) {
                if (chr.getLevel() < 200 && i == 20041005 || (skill = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skill) > 0) continue;
                applier.skillMap.put(i, new SkillEntry(1, skill.getMaxMasterLevel(), -1L));
            }
            for (int f : fixskills = new int[]{27001100, 27001201, 27000106, 27000207}) {
                skill = SkillFactory.getSkill(f);
                if (chr.getJob() < f / 10000 || skill == null || chr.getSkillLevel(skill) > 0 || chr.getMasterLevel(skill) > 0) continue;
                applier.skillMap.put(f, new SkillEntry(0, skill.getMasterLevel() == 0 ? skill.getMaxLevel() : skill.getMasterLevel(), SkillFactory.getDefaultSExpiry(skill)));
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 27141501: {
                return 27141500;
            }
            case 27141000: {
                return 27121303;
            }
            case 500004100: {
                return 400021005;
            }
            case 500004101: {
                return 400021041;
            }
            case 500004102: {
                return 400021071;
            }
            case 500004103: {
                return 400021105;
            }
            case 27001008: {
                return 27001002;
            }
            case 27120211: {
                return 27121201;
            }
            case 400021049: 
            case 400021050: {
                return 400021041;
            }
            case 400021106: 
            case 400021107: 
            case 400021108: 
            case 400021109: 
            case 400021110: {
                return 400021105;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 20041005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 20040216: 
            case 20040217: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Larkness, 1);
                return 1;
            }
            case 20040219: 
            case 20040220: {
                statups.put(SecondaryStat.Larkness, 2);
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.prop));
                return 1;
            }
            case 27101202: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KeyDownAreaMoving, 1);
                return 1;
            }
            case 27111101: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.x).intValue() / 1000.0);
                return 1;
            }
            case 27111008: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.TeleportMasteryRange, 1);
                return 1;
            }
            case 27110007: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LifeTidal, 2);
                return 1;
            }
            case 27111004: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AntiMagicShell, effect.getU());
                return 1;
            }
            case 27111006: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.EMAD, effect.getX());
                return 1;
            }
            case 27121006: {
                statups.put(SecondaryStat.ElementalReset, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 27120005: {
                statups.put(SecondaryStat.StackBuff, 1);
                return 1;
            }
            case 27121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 27101101: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 27121052: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 400021071: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SwordBaptism, 0);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 27111009: {
                if (chr.getLarkness() <= 0) {
                    chr.getSkillEffect(20040219).unprimaryPassiveApplyTo(chr);
                    chr.setLarknessDiraction(1);
                }
                if (chr.getLarkness() >= 10000) {
                    chr.getSkillEffect(20040219).unprimaryPassiveApplyTo(chr);
                    chr.setLarknessDiraction(2);
                }
                chr.updateLarknessStack();
                return 1;
            }
            case 27121054: {
                MapleStatEffect effect = chr.getSkillEffect(20040219);
                effect.applyTo(chr, chr, true, null, effect.getDuration(), false);
                return 1;
            }
            case 400021041: 
            case 400021049: 
            case 400021050: {
                MapleStatEffect eff = chr.getSkillEffect(400021041);
                if (eff == null) {
                    return 1;
                }
                if (!chr.isSkillCooling(eff.getSourceId())) {
                    chr.registerSkillCooldown(eff.getSourceId(), eff.getCooldown(chr), true);
                    return 1;
                }
                if (chr.isSkillCooling(eff.getSourceId())) {
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 27110007: {
                applier.localstatups = Collections.singletonMap(SecondaryStat.LifeTidal, applyto.getStat().getLifeTidal());
                return 1;
            }
            case 27120005: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.StackBuff);
                applier.buffz = applyto.getBuffedIntZ(SecondaryStat.StackBuff);
                if (mbsvh == null) {
                    return 1;
                }
                if (applier.buffz < applier.effect.getX()) {
                    applier.buffz = Math.min(applier.buffz + 1, applier.effect.getX());
                    applier.duration = mbsvh.getLeftTime();
                    applier.localstatups.put(SecondaryStat.StackBuff, applier.buffz * applier.effect.getDamR());
                    return 1;
                }
                return 0;
            }
            case 20040219: {
                applyto.cancelSkillCooldown(27111303);
                applyto.cancelSkillCooldown(27121303);
                if (!applier.passive) {
                    applier.b3 = true;
                    applyto.setTruthGate(true);
                    applyto.dispelEffect(SecondaryStat.Larkness);
                    return 1;
                }
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.Larkness);
                if (mbsvh == null) {
                    return 1;
                }
                applyto.setTruthGate(false);
                if (mbsvh.x == 2) {
                    applier.duration = mbsvh.getLeftTime();
                }
                return 1;
            }
            case 20040216: 
            case 20040217: {
                applyto.dispelEffect(SecondaryStat.Larkness);
                return 1;
            }
            case 27121054: {
                applyto.setLarknessDiraction(1);
                applyto.setLarkness(0);
                applyto.updateLarknessStack();
                return 1;
            }
            case 27121053: {
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
            case 400021005: {
                applyto.setTruthGate(false);
                applyto.getSkillEffect(20040219).unprimaryApplyTo(applyto, null, true);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (this.containsJob(applyfrom.getJobWithSub()) && applyfrom.getJob() != 2700 && applier.effect != null) {
            MapleStatEffect skillEffect15;
            int type = applier.effect.getSourceId() % 1000 / 100;
            if (applyfrom.getEffectForBuffStat(SecondaryStat.Larkness) == null && (skillEffect15 = applyfrom.getSkillEffect(20040216)) != null) {
                skillEffect15.unprimaryPassiveApplyTo(applyfrom);
                applyfrom.setLarknessDiraction(2);
                applyfrom.addLarkness(10000);
                applyfrom.updateLarknessStack();
            }
            switch (type) {
                case 1: {
                    if (type == applyfrom.getLarknessDiraction()) break;
                    if (applyfrom.getBuffedIntValue(SecondaryStat.Larkness) != 2) {
                        applyfrom.addLarkness(-(Randomizer.nextInt(100) + 80));
                        if (applyfrom.getLarkness() <= 0) {
                            applyfrom.setLarknessDiraction(3);
                        }
                        applyfrom.updateLarknessStack();
                    }
                    applyfrom.addHPMP(1, 0);
                    break;
                }
                case 2: {
                    if (type == applyfrom.getLarknessDiraction() || applyfrom.getBuffedIntValue(SecondaryStat.Larkness) == 2) break;
                    applyfrom.addLarkness(Randomizer.nextInt(100) + 80);
                    if (applyfrom.getLarkness() >= 10000) {
                        applyfrom.setLarknessDiraction(3);
                    }
                    applyfrom.updateLarknessStack();
                }
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat9 = player.getEffectForBuffStat(SecondaryStat.StackBuff);
        if (applier.totalDamage > 0L && effecForBuffStat9 != null && applier.effect != null && effecForBuffStat9.makeChanceResult(player)) {
            effecForBuffStat9.unprimaryPassiveApplyTo(player);
        }
        return 1;
    }
}

