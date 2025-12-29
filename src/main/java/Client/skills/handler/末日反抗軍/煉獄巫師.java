/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.末日反抗軍;

import Client.MapleCharacter;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleSummon;
import Packet.SummonPacket;
import java.lang.reflect.Field;
import java.util.Map;

public class 煉獄巫師
extends AbstractSkillHandler {
    public 煉獄巫師() {
        this.jobs = new MapleJob[]{MapleJob.煉獄巫師1轉, MapleJob.煉獄巫師2轉, MapleJob.煉獄巫師3轉, MapleJob.煉獄巫師4轉};
        for (Field field : Config.constants.skills.煉獄巫師.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 32141000: {
                return 32001014;
            }
            case 500004112: {
                return 400021006;
            }
            case 500004113: {
                return 400021047;
            }
            case 500004114: {
                return 400021069;
            }
            case 500004115: {
                return 400021087;
            }
            case 32001018: {
                return 32001002;
            }
            case 32110020: {
                return 32111016;
            }
            case 32121011: {
                return 32121004;
            }
            case 32120055: {
                return 32121052;
            }
            case 400021007: {
                return 400021006;
            }
            case 400021088: 
            case 400021089: 
            case 400021113: {
                return 400021087;
            }
            case 32141501: {
                return 32141500;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 32111016: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.DarkLighting, 1);
                return 1;
            }
            case 32001014: 
            case 32100010: 
            case 32110017: 
            case 32120019: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BMageDeath, 0);
                return 1;
            }
            case 32001016: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BMageAuraYellow, effect.getLevel());
                statups.put(SecondaryStat.IndieSpeed, effect.getInfo().get((Object)MapleStatInfo.indieSpeed));
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                return 1;
            }
            case 32101009: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BMageAuraDrain, effect.getLevel());
                statups.put(SecondaryStat.AranDrain, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 32111012: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.DamAbsorbShield, effect.getInfo().get((Object)MapleStatInfo.y));
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.indieAsrR));
                statups.put(SecondaryStat.BMageAuraBlue, effect.getLevel());
                return 1;
            }
            case 32121017: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.BMageAuraDark, effect.getLevel());
                return 1;
            }
            case 32121018: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BMageAuraDebuff, effect.getLevel());
                monsterStatus.put(MonsterStatus.BMageDebuff, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 32121010: {
                statups.clear();
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Enrage, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.EnrageCr, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.EnrageCrDamMin, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 32121006: {
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 32101005: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 32101001: 
            case 32121004: {
                monsterStatus.put(MonsterStatus.Seal, 1);
                return 1;
            }
            case 32111021: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.TeleportMasteryRange, 1);
                return 1;
            }
            case 32121056: {
                statups.put(SecondaryStat.AttackCountX, 2);
                return 1;
            }
            case 32121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400021006: {
                statups.put(SecondaryStat.BattlePvP_Helena_Mark, effect.getLevel());
                monsterStatus.put(MonsterStatus.BMageDebuff, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400021047: {
                statups.put(SecondaryStat.CannonShooter_BFCannonBall, 0);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 32001016: 
            case 32101009: 
            case 32111012: 
            case 32121017: 
            case 32121018: {
                if (applyto.getSkillEffect(32001016) != null) {
                    applyto.dispelEffect(SecondaryStat.BMageAuraYellow);
                }
                if (applyto.getSkillEffect(32101009) != null) {
                    applyto.dispelEffect(SecondaryStat.BMageAuraDrain);
                }
                if (applyto.getSkillEffect(32111012) != null) {
                    applyto.dispelEffect(SecondaryStat.BMageAuraBlue);
                }
                if (applyto.getSkillEffect(32121017) != null) {
                    applyto.dispelEffect(SecondaryStat.BMageAuraDark);
                }
                if (applyto.getSkillEffect(32121018) != null) {
                    applyto.dispelEffect(SecondaryStat.BMageAuraDebuff);
                }
                if (applier.effect.getSourceId() == 32121017 && applyto.getSkillEffect(32120060) != null) {
                    applier.localstatups.put(SecondaryStat.IndieBDR, applier.effect.getIndieBDR());
                }
                return 1;
            }
            case 32121006: {
                if (applier.primary) {
                    return 1;
                }
                return 0;
            }
            case 32111016: {
                if (applier.passive) {
                    return 0;
                }
                return 1;
            }
            case 32001014: 
            case 32100010: 
            case 32110017: 
            case 32120019: {
                MapleSummon summon;
                int maxValue = 0;
                MapleStatEffect eff = applyto.getSkillEffect(32001014);
                if (eff != null) {
                    maxValue = eff.getX();
                }
                if ((eff = applyto.getSkillEffect(32100010)) != null) {
                    maxValue = eff.getX();
                }
                if ((eff = applyto.getSkillEffect(32110017)) != null) {
                    maxValue = eff.getX();
                }
                if ((eff = applyto.getSkillEffect(32120019)) != null) {
                    maxValue = eff.getX();
                }
                if (applyto.getEffectForBuffStat(SecondaryStat.AttackCountX) != null) {
                    maxValue = 1;
                }
                int value = applyto.getBuffedIntValue(SecondaryStat.BMageDeath);
                if (applyto.getBuffedValue(SecondaryStat.BMageDeath) != null) {
                    applier.localstatups.clear();
                    applier.applySummon = false;
                    ++value;
                }
                value = Math.min(value, maxValue);
                applier.localstatups.put(SecondaryStat.BMageDeath, value);
                if (value == maxValue && applyto.getCheatTracker().canNext死神契約() && (summon = applyto.getSummonBySkillID(applier.effect.getSourceId())) != null) {
                    applier.localstatups.put(SecondaryStat.BMageDeath, 0);
                    applyto.getClient().announce(SummonPacket.SummonedAssistAttackRequest(applyto.getId(), summon.getObjectId(), 0));
                }
                return 1;
            }
            case 32121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(31221053);
                applyto.dispelEffect(37121053);
                applyto.dispelEffect(32121053);
                applyto.dispelEffect(33121053);
                applyto.dispelEffect(35121053);
                return 1;
            }
            case 400021006: {
                MapleStatEffect eff = applyto.getSkillEffect(32001016);
                if (eff != null) {
                    applier.localstatups.putAll(eff.getStatups());
                }
                if ((eff = applyto.getSkillEffect(32101009)) != null) {
                    applier.localstatups.putAll(eff.getStatups());
                }
                if ((eff = applyto.getSkillEffect(32111012)) != null) {
                    applier.localstatups.putAll(eff.getStatups());
                }
                if ((eff = applyto.getSkillEffect(32121017)) != null) {
                    applier.localstatups.putAll(eff.getStatups());
                }
                if ((eff = applyto.getSkillEffect(32121018)) != null) {
                    applier.localstatups.putAll(eff.getStatups());
                }
                return 1;
            }
            case 400021047: {
                applier.applySummon = !applier.passive;
                applier.localstatups.clear();
                if (applier.applySummon) {
                    applier.localstatups.put(SecondaryStat.IndieBuffIcon, 1);
                }
                int value = applyto.getBuffedIntValue(SecondaryStat.CannonShooter_BFCannonBall) + (applier.passive ? 1 : -1);
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.CannonShooter_BFCannonBall);
                if (!applier.primary || value < 0 || mbsvh != null && System.currentTimeMillis() < mbsvh.startTime + 500L) {
                    return 0;
                }
                applier.maskedDuration = 2100000000;
                applier.maskedstatups.put(SecondaryStat.CannonShooter_BFCannonBall, value);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat10 = applyfrom.getEffectForBuffStat(SecondaryStat.BMageDeath);
        if (effecForBuffStat10 != null && (applier.effect == null || applier.effect.getSourceId() != effecForBuffStat10.getSourceId())) {
            effecForBuffStat10.applyTo(applyfrom, true);
        }
        if (!applyto.isAlive() && applyfrom.getSkillLevel(32101009) > 0) {
            MapleStatEffect eff = applyfrom.getSkillEffect(32101009);
            int toHeal = eff.getKillRecoveryR();
            applyfrom.addHPMP((int)((double)toHeal / 100.0 * (double)applyfrom.getStat().getCurrentMaxHP()), 0, false, true);
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect eff = player.getEffectForBuffStat(SecondaryStat.ComboDrain);
        if (applier.totalDamage > 0L && eff != null && player.getCheatTracker().canNextBonusAttack(5000L)) {
            player.addHPMP((int)Math.min((long)player.getStat().getCurrentMaxHP() * 15L / 100L, applier.totalDamage / 100L), 0, false, true);
        }
        return 1;
    }
}

