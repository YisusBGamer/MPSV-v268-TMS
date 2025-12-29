/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.皇家騎士團;

import Client.MapleCharacter;
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
import java.lang.reflect.Field;
import java.util.Map;

public class 米哈逸
extends AbstractSkillHandler {
    public 米哈逸() {
        this.jobs = new MapleJob[]{MapleJob.米哈逸, MapleJob.米哈逸1轉, MapleJob.米哈逸2轉, MapleJob.米哈逸3轉, MapleJob.米哈逸4轉};
        for (Field field : Config.constants.skills.皇家騎士團_技能群組.米哈逸.class.getDeclaredFields()) {
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
        int[] ss;
        for (int i : ss = new int[]{50001005, 50001245, 50000250}) {
            Skill skill;
            if (chr.getLevel() < 200 && i == 50001005 || (skill = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skill) > 0) continue;
            applier.skillMap.put(skill.getId(), new SkillEntry(1, skill.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 51141501: 
            case 51141502: {
                return 51141500;
            }
            case 51141000: 
            case 51141001: {
                return 51121009;
            }
            case 500004132: {
                return 400011011;
            }
            case 500004133: {
                return 400011032;
            }
            case 500004134: {
                return 400011083;
            }
            case 500004135: {
                return 400011127;
            }
            case 51111011: 
            case 51111012: {
                return 51110009;
            }
            case 51001005: 
            case 51001007: 
            case 51001008: 
            case 51001009: 
            case 51001010: 
            case 51001011: 
            case 51001012: 
            case 51001013: {
                return 51001006;
            }
            case 400011033: 
            case 400011034: 
            case 400011035: 
            case 400011036: 
            case 400011037: {
                return 400011032;
            }
            case 400011084: {
                return 400011083;
            }
            case 400011050: 
            case 400011086: 
            case 400011087: 
            case 400011128: {
                return 400011127;
            }
            case 51101007: {
                return 51101006;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 51001004: 
            case 51101006: 
            case 51101007: {
                return 1;
            }
            case 51111008: {
                effect.setPartyBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                effect.getInfo().put(MapleStatInfo.damR, 5);
                statups.put(SecondaryStat.IndieDotHealMP, -5);
            }
            case 50001005: {
                effect.setRangeBuff(true);
                return 1;
            }
            case 50001075: {
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 50001214: {
                statups.put(SecondaryStat.MichaelStanceLink, 0);
                return 1;
            }
            case 80001140: {
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.prop));
                return 1;
            }
            case 400011066: {
                statups.clear();
                statups.put(SecondaryStat.DamAbsorbShield, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400011127: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 25000);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.IncMaxHP, 98);
                statups.put(SecondaryStat.IndieDrainHP, 7);
                return 1;
            }
            case 51101003: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 51001005: {
                effect.getInfo().put(MapleStatInfo.time, 4000);
                statups.put(SecondaryStat.BodyRectGuardPrepare, 1);
                statups.put(SecondaryStat.ManaReflection, 1);
                statups.put(SecondaryStat.NotDamaged, 4);
                statups.put(SecondaryStat.PAD, 5);
                return 1;
            }
            case 51111004: {
                statups.put(SecondaryStat.AsrR, effect.getInfo().get((Object)MapleStatInfo.y));
                statups.put(SecondaryStat.TerR, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.DDR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 51111005: {
                monsterStatus.put(MonsterStatus.MagicCrash, 1);
                return 1;
            }
            case 51110014: {
                monsterStatus.put(MonsterStatus.Seal, 1);
                return 1;
            }
            case 51121006: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Enrage, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.EnrageCr, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.EnrageCrDamMin, effect.getInfo().get((Object)MapleStatInfo.y) - 1);
                statups.put(SecondaryStat.IndiePADR, 25);
                statups.put(SecondaryStat.IndieBDR, 10);
                return 1;
            }
            case 51101005: 
            case 51121009: {
                monsterStatus.put(MonsterStatus.Blind, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 51121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400011011: {
                statups.put(SecondaryStat.Michael_RhoAias, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400011083: {
                statups.put(SecondaryStat.IndieCr, effect.getInfo().get((Object)MapleStatInfo.indieCr));
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.indieIgnoreMobpdpR));
                statups.put(SecondaryStat.IndiePADR, effect.getInfo().get((Object)MapleStatInfo.indiePadR));
                statups.put(SecondaryStat.MichaelSwordOfLight, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 50001245: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 50001214: {
                if (!applier.passive) {
                    return 1;
                }
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.MichaelStanceLink);
                if (mbsvh == null) {
                    return 0;
                }
                int max = Math.max(0, mbsvh.value - 1);
                if (max > 0) {
                    applier.duration = mbsvh.getLeftTime();
                    applier.localstatups.put(SecondaryStat.MichaelStanceLink, max);
                    return 1;
                }
                applier.overwrite = false;
                applier.localstatups.clear();
                return 1;
            }
            case 51111004: {
                MapleStatEffect eff = applyto.getSkillEffect(51120045);
                if (eff != null) {
                    applier.localstatups.put(SecondaryStat.TerR, applier.effect.getY() + eff.getY());
                    applier.localstatups.put(SecondaryStat.AsrR, applier.effect.getZ() + eff.getZ());
                }
                if ((eff = applyto.getSkillEffect(51120044)) != null) {
                    applier.localstatups.put(SecondaryStat.DDR, applier.effect.getX() + eff.getX());
                }
                return 1;
            }
            case 51001005: 
            case 51001007: 
            case 51001008: 
            case 51001009: 
            case 51001010: {
                applier.b3 = true;
                applyto.registerSkillCooldown(51001006, 6000, true);
                return 1;
            }
            case 51001011: 
            case 51001012: 
            case 51001013: 
            case 51111011: 
            case 51111012: {
                applier.localstatups.put(SecondaryStat.RoyalGuardState, Math.min(applyto.getBuffedIntValue(SecondaryStat.RoyalGuardState) + 1, 5));
                applier.localstatups.put(SecondaryStat.IndiePAD, applier.effect.getW());
                applyto.dispelEffect(SecondaryStat.RoyalGuardState);
                applier.maskedstatups.put(SecondaryStat.NotDamaged, 1);
                applier.maskedDuration = 2000;
                return 1;
            }
            case 51121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000 || applyto.getJob() / 1000 == 1) {
                    return 0;
                }
                applyto.dispelEffect(11121053);
                applyto.dispelEffect(12121053);
                applyto.dispelEffect(13121053);
                applyto.dispelEffect(14121053);
                applyto.dispelEffect(15121053);
                applyto.dispelEffect(51121053);
                return 1;
            }
        }
        return -1;
    }
}

