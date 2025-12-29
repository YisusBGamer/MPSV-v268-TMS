/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.CalcDamage$1
 */
package Client;

import Client.CalcDamage;
import Client.MapleCharacter;
import Client.MonsterEffectHolder;
import Client.SecondaryStat;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.stat.PlayerStats;
import Client.status.MonsterStatus;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.Element;
import Net.server.life.ElementalEffectiveness;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMapObjectType;
import Server.channel.handler.AttackInfo;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Objects;
import tools.Randomizer;
import tools.newCRand32;

public class CalcDamage {
    private final newCRand32 rand = new newCRand32();

    public final void setSeed(int s1, int s2, int s3) {
        this.rand.seed(s1, s2, s3);
    }

    public static double randomInRange(long randomNum, long max, long min) {
        double value;
        double newRandNum = (double)randomNum - (double)(new BigInteger(String.valueOf(randomNum)).multiply(new BigInteger("1801439851")).shiftRight(32).longValue() >>> 22) * 1.0E7;
        if (min != max) {
            if (min > max) {
                long temp = max;
                max = min;
                min = temp;
            }
            value = (double)(max - min) * newRandNum / 9999999.0 + (double)min;
        } else {
            value = max;
        }
        return value;
    }

    public final long random() {
        return this.rand.random();
    }

    public final long getRandomDamage(MapleCharacter chr, boolean crit) {
        long[] array = new long[11];
        for (int i = 0; i < 11; ++i) {
            array[i] = this.rand.random();
        }
        boolean cr2 = CalcDamage.randomInRange(array[Randomizer.nextInt(11)], 100L, 0L) < (double)chr.getStat().critRate;
        double damage = CalcDamage.randomInRange(array[Randomizer.nextInt(11)], chr.getStat().getCurrentMaxBaseDamage(), chr.getStat().getCurrentMinBaseDamage());
        if (crit || cr2) {
            damage += damage * (CalcDamage.randomInRange(array[Randomizer.nextInt(11)], 20L, 50L) + chr.getStat().criticalDamage) / 100.0;
        }
        return (long)Math.min(damage, 1.0E10);
    }

    public final double calcDamage(MapleCharacter chr, AttackInfo ai, int idx, MapleMonster monster, boolean isBoss) {
        return this.calcDamage(chr, ai, idx, chr.getStat().getCurrentMaxBaseDamage(), monster, isBoss, false);
    }

    public final double calcDamage(MapleCharacter chr, AttackInfo ai, int idx, double maxBaseDamage, MapleMonster monster, boolean isBoss, boolean isCritical) {
        MonsterEffectHolder holder;
        Skill skill;
        MapleStatEffect effect;
        long limitBreak = 10000000000L;
        double elementDamR = 1.0;
        PlayerStats stat = chr.getStat();
        if (ai.skillId > 0) {
            int skillLevel = chr.getSkillLevel(SkillConstants.getLinkedAttackSkill(ai.skillId));
            Skill skill2 = SkillFactory.getSkill(ai.skillId);
            assert (skill2 != null);
            limitBreak = Math.max(limitBreak, (long)skill2.getMaxDamageOver());
            MapleStatEffect effect2 = skill2.getEffect(skillLevel);
            Element element = skill2.getElement();
            if (element != null) {
                ElementalEffectiveness effectiveness = monster.getStats().getEffectiveness(element);
                if (Objects.requireNonNull(effectiveness) == ElementalEffectiveness.免疫) {
                    elementDamR = (1.0 + (double)chr.getStat().ignoreElement) / 100.0;
                } else {
                    double eiValue = effectiveness.getValue();
                    eiValue += eiValue * (double)chr.getStat().ignoreElement / 100.0;
                    switch (element.getValue()) {
                        case 1: {
                            elementDamR = eiValue * (double)(stat.getElementFire() + stat.getElementBoost(element)) / 100.0;
                            break;
                        }
                        case 2: {
                            elementDamR = eiValue * (double)(stat.getElementIce() + stat.getElementBoost(element)) / 100.0;
                            break;
                        }
                        case 3: {
                            elementDamR = eiValue * (double)(stat.getElementLight() + stat.getElementBoost(element)) / 100.0;
                            break;
                        }
                        case 4: {
                            elementDamR = eiValue * (double)(stat.getElementPsn() + stat.getElementBoost(element)) / 100.0;
                            break;
                        }
                        default: {
                            elementDamR = eiValue * (double)(stat.getElementDef() + stat.getElementBoost(element)) / 100.0;
                        }
                    }
                }
            }
            if (effect2 != null) {
                double skillDamR = (double)effect2.getDamage() + (double)(effect2.getDamage() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                switch (ai.skillId) {
                    case 1311011: {
                        if (ai.unInt1 <= 0) break;
                        skillDamR = effect2.getY();
                        break;
                    }
                    case 3201011: {
                        skillDamR += (double)effect2.getDamage() * ((double)idx * 20.0) / 100.0;
                        break;
                    }
                    case 4100012: 
                    case 0x3EDDD3: {
                        skillDamR += (double)(effect2.getX() * chr.getLevel());
                        break;
                    }
                    case 3101005: {
                        skillDamR = effect2.getX();
                        break;
                    }
                    case 22141017: 
                    case 22170070: {
                        skillDamR += skillDamR * (double)effect2.getW() * 3.0 / 100.0;
                        break;
                    }
                    case 33001007: 
                    case 33001008: 
                    case 33001009: 
                    case 33001010: 
                    case 33001011: 
                    case 33001012: 
                    case 33001013: 
                    case 33001014: 
                    case 33001015: 
                    case 33001016: 
                    case 33101115: 
                    case 33111015: 
                    case 33121017: 
                    case 33121155: 
                    case 33121255: {
                        skillDamR = effect2.getY() + Math.min(chr.getLevel(), 180) * effect2.getDamage();
                        break;
                    }
                    case 33000036: {
                        skillDamR = effect2.getY() + Math.min(chr.getLevel(), 180) * effect2.getDamage();
                        break;
                    }
                    case 35121003: {
                        if (ai.attackType == AttackInfo.AttackType.BodyAttack) {
                            skillDamR = (double)effect2.getY() + (double)(effect2.getY() * stat.getSkillDamageIncrease(ai.skillId)) * 1.2 / 100.0;
                            break;
                        }
                        if (ai.attackType != AttackInfo.AttackType.SummonedAttack) break;
                        skillDamR *= 2.0;
                        break;
                    }
                    case 35121009: {
                        if (chr.getSkillEffect(ai.skillId) != null) {
                            effect2 = chr.getSkillEffect(ai.skillId);
                        }
                        skillDamR = (double)effect2.getSelfDestruction() + (double)(effect2.getSelfDestruction() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                        break;
                    }
                    case 36001005: {
                        skillDamR *= 2.0;
                        break;
                    }
                    case 36121002: {
                        if (chr.getSkillEffect(ai.skillId) != null) {
                            effect2 = chr.getSkillEffect(ai.skillId);
                        }
                        skillDamR = (double)effect2.getDamage() + (double)(effect2.getDamage() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                        break;
                    }
                    case 51001011: 
                    case 51001012: 
                    case 51001013: 
                    case 51111011: 
                    case 51111012: 
                    case 131001000: 
                    case 131001004: 
                    case 131001101: 
                    case 131001102: 
                    case 131001103: 
                    case 131001104: 
                    case 131001208: {
                        effect2 = skill2.getEffect(chr.getLevel());
                        skillDamR = (double)effect2.getDamage() + (double)(effect2.getDamage() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                        break;
                    }
                    case 131000016: {
                        effect2 = SkillFactory.getSkill(131002016).getEffect(chr.getLevel());
                        skillDamR = (double)effect2.getDamage() + (double)(effect2.getDamage() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                        break;
                    }
                    case 400011007: 
                    case 400011008: 
                    case 400011009: 
                    case 400011018: {
                        skillDamR *= 2.0;
                        break;
                    }
                    case 400051009: 
                    case 400051012: {
                        skillDamR *= 1.5;
                        break;
                    }
                    case 400021017: {
                        effect2 = SkillFactory.getSkill(400021018).getEffect(chr.getLevel());
                        skillDamR = (double)effect2.getDamage() + (double)(effect2.getDamage() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                        break;
                    }
                    case 400011012: {
                        effect2 = SkillFactory.getSkill(ai.skillId).getEffect(chr.getLevel());
                        skillDamR = (double)effect2.getDamage() + (double)(effect2.getDamage() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                        break;
                    }
                    case 400051022: 
                    case 400051023: {
                        skillDamR = effect2.getV();
                        break;
                    }
                    case 37110001: {
                        skillDamR = (double)effect2.getQ2() + (double)(effect2.getQ2() * stat.getSkillDamageIncrease(ai.skillId)) / 100.0;
                    }
                }
                if (JobConstants.is卡蒂娜(chr.getJob())) {
                    Integer buffedValue = chr.getBuffedValue(SecondaryStat.WeaponVariety);
                    MapleStatEffect effecForBuffStat = chr.getEffectForBuffStat(SecondaryStat.WeaponVariety);
                    if (buffedValue != null && effecForBuffStat != null) {
                        skillDamR *= skillDamR * (double)effecForBuffStat.getX() / 100.0;
                    }
                }
                maxBaseDamage = maxBaseDamage * skillDamR / 100.0;
            }
        }
        if ((effect = (skill = SkillFactory.getSkill(1110009)).getEffect(chr.getSkillLevel(skill))) != null && (monster.getEffectHolder(MonsterStatus.Stun) != null || monster.getEffectHolder(MonsterStatus.Darkness) != null || monster.getEffectHolder(MonsterStatus.Freeze) != null)) {
            maxBaseDamage *= (100.0 + (double)effect.getX()) / 100.0;
        }
        if (JobConstants.is冒險家法師(chr.getJob())) {
            if (chr.getJob() >= 211 && chr.getJob() <= 212) {
                skill = SkillFactory.getSkill(0x203230);
            } else if (chr.getJob() >= 221 && chr.getJob() <= 222) {
                skill = SkillFactory.getSkill(2210000);
            }
            assert (skill != null);
            effect = skill.getEffect(chr.getSkillLevel(skill));
            if (effect != null && (monster.getEffectHolder(MonsterStatus.Burned) != null || monster.getEffectHolder(MonsterStatus.Speed) != null || monster.getEffectHolder(MonsterStatus.Stun) != null || monster.getEffectHolder(MonsterStatus.Darkness) != null || monster.getEffectHolder(MonsterStatus.Freeze) != null)) {
                maxBaseDamage *= (100.0 + (double)effect.getZ()) / 100.0;
            }
        }
        if (JobConstants.is惡魔殺手(chr.getJob()) && (effect = chr.getSkillEffect(31110006)) != null && (monster.getEffectHolder(MonsterStatus.Burned) != null || monster.getEffectHolder(MonsterStatus.Speed) != null || monster.getEffectHolder(MonsterStatus.Stun) != null || monster.getEffectHolder(MonsterStatus.Darkness) != null || monster.getEffectHolder(MonsterStatus.Freeze) != null)) {
            maxBaseDamage *= (100.0 + (double)effect.getX()) / 100.0;
        }
        if (JobConstants.is米哈逸(chr.getJob()) && (effect = chr.getSkillEffect(51000003)) != null && (monster.getEffectHolder(MonsterStatus.Burned) != null || monster.getEffectHolder(MonsterStatus.Speed) != null || monster.getEffectHolder(MonsterStatus.Stun) != null || monster.getEffectHolder(MonsterStatus.Darkness) != null || monster.getEffectHolder(MonsterStatus.Freeze) != null)) {
            maxBaseDamage *= (100.0 + (double)effect.getX()) / 100.0;
        }
        if ((holder = monster.getEffectHolder(MonsterStatus.AddDamSkill2)) != null) {
            maxBaseDamage += maxBaseDamage * (double)holder.value / 100.0;
        }
        if ((holder = monster.getEffectHolder(MonsterStatus.TotalDamParty)) != null) {
            maxBaseDamage += maxBaseDamage * (double)holder.value / 100.0;
        }
        if ((effect = chr.getEffectForBuffStat(SecondaryStat.GuidedBullet)) != null) {
            maxBaseDamage += maxBaseDamage * (double)effect.getX() / 100.0;
        }
        if ((holder = monster.getEffectHolder(MonsterStatus.TotalDamParty)) != null) {
            maxBaseDamage += maxBaseDamage * (double)holder.value / 100.0;
        }
        if (JobConstants.is箭神(chr.getJob())) {
            effect = chr.getEffectForBuffStat(SecondaryStat.BowMasterMortalBlow);
            int buffedIntValue = chr.getBuffedIntValue(SecondaryStat.BowMasterMortalBlow);
            if (effect != null && buffedIntValue >= effect.getX()) {
                maxBaseDamage += maxBaseDamage * (double)effect.getY() / 100.0;
            }
        } else if (JobConstants.is神射手(chr.getJob())) {
            double incFinalDam = 1.0;
            effect = chr.getSkillEffect(3220015);
            if (effect != null) {
                double maxfd = effect.getDamR();
                incFinalDam += incFinalDam * maxfd / 100.0;
            }
            if ((effect = chr.getSkillEffect(3220016)) != null && chr.getMap().getMapObjectsInRange(monster.getPosition(), 80.0, Collections.singletonList(MapleMapObjectType.MONSTER)).size() <= 1) {
                incFinalDam += incFinalDam * (double)effect.getDamR() / 100.0;
            }
            maxBaseDamage += maxBaseDamage * incFinalDam;
        }
        double mobPDR = monster.getStats().getPDRate();
        double counteredDamR = Math.max(0.0, 100.0 - (mobPDR - mobPDR * chr.getStat().getIgnoreMobpdpR(ai.skillId) / 100.0));
        double crDam = 50.0 + stat.criticalDamage;
        if (isCritical) {
            maxBaseDamage += maxBaseDamage * crDam / 100.0;
        }
        if (isBoss) {
            maxBaseDamage += maxBaseDamage * (double)stat.bossDamageR / (100.0 + stat.incDamR);
        }
        maxBaseDamage *= elementDamR;
        maxBaseDamage += maxBaseDamage * (double)stat.getSkillDamageIncrease_5th(SkillConstants.getLinkedAttackSkill(ai.skillId)) / 100.0;
        maxBaseDamage = Math.max(1.0, maxBaseDamage * (counteredDamR / 100.0));
        int dl = chr.getLevel() - monster.getMobLevel();
        maxBaseDamage = dl > 20 ? (maxBaseDamage *= 1.2) : (dl < -20 ? (maxBaseDamage *= 0.8) : (maxBaseDamage += maxBaseDamage * ((double)dl / 100.0)));
        return Math.min(Math.max(1.0, maxBaseDamage), (double)limitBreak);
    }
}

