/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.曉之陣;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
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
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 劍豪
extends AbstractSkillHandler {
    public 劍豪() {
        this.jobs = new MapleJob[]{MapleJob.劍豪, MapleJob.劍豪1轉, MapleJob.劍豪2轉, MapleJob.劍豪3轉, MapleJob.劍豪4轉};
        for (Field field : Config.constants.skills.劍豪.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{40011289, 40010000, 40010067, 40011005, 40011288, 40011291, 40011292, 40011290, 40011227, 41001010}) {
            if (chr.getLevel() < 200 && i == 40011005) continue;
            Skill skil = SkillFactory.getSkill(i);
            if (chr.getJob() < i / 10000 || skil == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(skil.getMaxLevel(), skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 500004184: {
                return 400011026;
            }
            case 500004185: {
                return 400011029;
            }
            case 500004186: {
                return 400011104;
            }
            case 500004187: {
                return 400011138;
            }
            case 41141001: 
            case 41141002: 
            case 41141003: {
                return 41141000;
            }
            case 41141501: 
            case 41141502: 
            case 41141503: 
            case 41141504: {
                return 41141500;
            }
            case 400011138: {
                return 40011289;
            }
            case 40011291: {
                return 40011292;
            }
            case 41001015: {
                return 41001013;
            }
            case 41001012: {
                return 41001011;
            }
            case 41101014: {
                return 41101012;
            }
            case 41111017: {
                return 41111001;
            }
            case 41111013: {
                return 41111003;
            }
            case 41111016: 
            case 41111018: {
                return 41111015;
            }
            case 41101015: {
                return 41101013;
            }
            case 41121020: {
                return 41121017;
            }
            case 41121021: {
                return 41121018;
            }
            case 41121022: {
                return 41121002;
            }
            case 41001004: 
            case 41001005: 
            case 41001009: {
                return 41001000;
            }
            case 41101008: 
            case 41101009: 
            case 41101011: {
                return 41101000;
            }
            case 41111011: 
            case 41111012: 
            case 41111014: {
                return 41111000;
            }
            case 41120013: 
            case 41121011: 
            case 41121012: 
            case 41121016: {
                return 41121000;
            }
            case 41001006: 
            case 41001007: 
            case 41001008: {
                return 41001002;
            }
            case 41001014: {
                return 41001010;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 40011005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 40011289: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 40011288: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BladeStancePower, 1);
                return 1;
            }
            case 40011291: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMmpR));
                statups.put(SecondaryStat.IndieMMPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.indieIgnoreMobpdpR));
                statups.put(SecondaryStat.IndiePADR, effect.getInfo().get((Object)MapleStatInfo.indiePadR));
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 40011292: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BladeStanceMode, 1);
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                statups.put(SecondaryStat.IndieBDR, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                return 1;
            }
            case 41100003: {
                statups.put(SecondaryStat.SelfHyperBodyIncPAD, effect.getInfo().get((Object)MapleStatInfo.padX));
                statups.put(SecondaryStat.SelfHyperBodyMaxHP, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.SelfHyperBodyMaxMP, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 41110006: {
                statups.put(SecondaryStat.RWCombination, effect.getInfo().get((Object)MapleStatInfo.damR));
                return 1;
            }
            case 41121002: {
                statups.put(SecondaryStat.CriticalBuffAdd, effect.getInfo().get((Object)MapleStatInfo.prop));
                return 1;
            }
            case 41120006: {
                statups.put(SecondaryStat.EvasionUpgrade, effect.getInfo().get((Object)MapleStatInfo.t));
                return 1;
            }
            case 41120003: {
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndieTerR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 41121015: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KenjiCounter, 0);
                return 1;
            }
            case 41121054: {
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.TerR, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.AsrR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 41001010: {
                statups.put(SecondaryStat.DamR, effect.getInfo().get((Object)MapleStatInfo.damR));
                statups.put(SecondaryStat.SkillDeployment, 1);
                return 1;
            }
            case 41001013: 
            case 41001015: 
            case 41101012: {
                effect.getInfo().put(MapleStatInfo.prop, 100);
                effect.getInfo().put(MapleStatInfo.time, 2300);
                monsterStatus.put(MonsterStatus.RiseByToss, 1);
                return 1;
            }
            case 41121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 41121001: {
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 41101005: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400011029: {
                statups.put(SecondaryStat.IndiePMdR, 0);
                return 1;
            }
            case 41120007: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 41121017: 
            case 41121020: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 41121018: 
            case 41121021: {
                monsterStatus.put(MonsterStatus.TotalDamParty, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 400011029: {
                chr.applyHayatoStance(-200);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 40011227: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 400011029: {
                if (!applier.passive) {
                    return 1;
                }
                int min9 = Math.min(5, applyto.getBuffedIntValue(SecondaryStat.SummonProp) + 1);
                applier.localstatups.put(SecondaryStat.IndiePMdR, applier.effect.getW() * min9);
                applier.localstatups.put(SecondaryStat.SummonProp, min9);
                if (min9 >= 5) {
                    applier.applySummon = false;
                }
                return 1;
            }
            case 40011291: {
                applyto.dispelEffect(40011292);
                return 1;
            }
            case 400011026: {
                applyto.dispelEffect(400011026);
                return 1;
            }
            case 40011288: 
            case 40011292: {
                applyto.dispelEffect(40011291);
                return 1;
            }
            case 41121015: {
                if (!applier.primary) {
                    return 0;
                }
                return 1;
            }
            case 41110006: {
                applier.buffz = Math.min(applyto.getBuffedIntZ(SecondaryStat.EvasionMaster) + 1, applier.effect.getX());
                applier.localstatups.put(SecondaryStat.EvasionMaster, applier.buffz * applier.effect.getDamR());
                return 1;
            }
            case 41121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(41121053);
                applyto.dispelEffect(42121053);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect skillEffect17;
        if (!applyto.isAlive()) {
            if (applyfrom.getBuffedIntValue(SecondaryStat.BladeStanceMode) <= 0) {
                applyfrom.applyHayatoStance(5);
            }
            if (applier.effect != null && applier.effect.getSourceId() == 400011104) {
                applyfrom.applyHayatoStance(applier.effect.getU());
            }
        } else {
            MapleStatEffect g112 = applyfrom.getSkillEffect(41110007);
            if (applyfrom.getSkillEffect(41120007) != null) {
                g112 = applyfrom.getSkillEffect(41120007);
            }
            if (g112 != null) {
                g112.applyMonsterEffect(applyfrom, applyto, g112.getMobDebuffDuration(applyfrom));
            }
        }
        if ((skillEffect17 = applyfrom.getSkillEffect(41110009)) != null && skillEffect17.makeChanceResult(applyfrom) && Randomizer.nextInt(100) < applyfrom.getStat().critRate) {
            applyfrom.addHPMP(skillEffect17.getX(), 0);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null) {
            switch (applier.effect.getSourceId()) {
                case 40011290: {
                    player.applyHayatoStance(-player.getSpecialStat().getHayatoPoint());
                    return 1;
                }
                case 400011104: {
                    player.applyHayatoStance(applier.effect.getQ());
                    return 1;
                }
                case 41121052: 
                case 400011026: {
                    player.applyHayatoStance(applier.effect.getX());
                    return 1;
                }
                case 400011029: {
                    MapleStatEffect effecForBuffStat13 = player.getEffectForBuffStat(SecondaryStat.KenjiShadowAttackBuff);
                    if (applier.totalDamage > 0L && effecForBuffStat13 != null) {
                        effecForBuffStat13.applyTo(player, true);
                    }
                    return 1;
                }
            }
            if (player.getBuffedIntValue(SecondaryStat.BladeStanceMode) > 0) {
                player.applyHayatoStance(2);
            }
        }
        return 1;
    }
}

