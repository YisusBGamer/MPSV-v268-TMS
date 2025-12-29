/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.冒險家.劍士類別;

import Client.MapleCharacter;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.stat.PlayerStats;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleSummon;
import Packet.AdelePacket;
import Packet.BuffPacket;
import Packet.MaplePacketCreator;
import Server.channel.handler.AttackInfo;
import Server.channel.handler.AttackMobInfo;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import tools.Randomizer;

public class 黑騎士
extends AbstractSkillHandler {
    public 黑騎士() {
        this.jobs = new MapleJob[]{MapleJob.槍騎兵, MapleJob.嗜血狂騎, MapleJob.黑騎士};
        for (Field field : Config.constants.skills.冒險家_技能群組.type_劍士.黑騎士.class.getDeclaredFields()) {
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
            case 1341501: {
                return 1341500;
            }
            case 1311019: {
                return 1310018;
            }
            case 1320019: 
            case 1320021: 
            case 1320022: 
            case 1320023: {
                return 1320016;
            }
            case 1321024: 
            case 1321025: 
            case 1321026: {
                return 1320011;
            }
            case 400011068: {
                return 400011069;
            }
            case 400011085: {
                return 400011047;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 1301013: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Beholder, effect.getLevel());
                return 1;
            }
            case 1301014: 
            case 1311019: 
            case 1321024: {
                monsterStatus.put(MonsterStatus.Stun, 21);
                return 1;
            }
            case 1301007: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.MaxHP, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.MaxMP, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 1311015: {
                statups.put(SecondaryStat.CrossOverChain, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 1310016: {
                statups.put(SecondaryStat.IndieCr, effect.getInfo().get((Object)MapleStatInfo.indieCr));
                statups.put(SecondaryStat.EPAD, effect.getInfo().get((Object)MapleStatInfo.epad));
                statups.put(SecondaryStat.EMAD, effect.getInfo().get((Object)MapleStatInfo.epad));
                statups.put(SecondaryStat.EPDD, effect.getInfo().get((Object)MapleStatInfo.epdd));
                return 1;
            }
            case 1321014: {
                monsterStatus.put(MonsterStatus.MagicCrash, 1);
                return 1;
            }
            case 1321015: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                statups.put(SecondaryStat.IndieBDR, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.v));
                statups.put(SecondaryStat.IgnoreTargetDEF, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                return 1;
            }
            case 1320016: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 1321020: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ReincarnationOnOff, 0);
                return 1;
            }
            case 1320021: 
            case 1320022: 
            case 1320023: {
                statups.put(SecondaryStat.ReincarnationMission, effect.getZ());
                return 1;
            }
            case 1320019: {
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                statups.put(SecondaryStat.Reincarnation, 1);
                return 1;
            }
            case 1321053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 1321054: {
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.ComboDrain, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400011004: {
                effect.getInfo().put(MapleStatInfo.time, effect.getW2());
                statups.put(SecondaryStat.IndieDamReduceR, effect.getW());
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                return 1;
            }
            case 400011068: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400011047: {
                statups.put(SecondaryStat.IndieBarrier, 0);
                statups.put(SecondaryStat.DarknessAura, effect.getU());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 1311015: {
                PlayerStats stats = applyfrom.getStat();
                if (stats != null) {
                    int qHP = stats.getCurrentMaxHP() * applier.effect.getQ() / 100;
                    if (stats.getHp() < qHP) {
                        applier.localstatups.put(SecondaryStat.CrossOverChain, (int)Math.ceil((double)(stats.getHp() * applier.effect.getX()) / (double)qHP));
                    }
                    applier.buffz = (stats.getCurrentMaxHP() - stats.getHp()) * applier.effect.getY() / 100;
                }
                return 1;
            }
            case 1310016: {
                MapleStatEffect effect = applyfrom.getSkillEffect(1320044);
                if (effect != null) {
                    applier.localstatups.put(SecondaryStat.EPAD, applier.localstatups.get(SecondaryStat.EPAD) + effect.getX());
                    applier.localstatups.put(SecondaryStat.EMAD, applier.localstatups.get(SecondaryStat.EMAD) + effect.getX());
                }
                return 1;
            }
            case 1321020: {
                Integer mode = (Integer)applyfrom.getTempValues().remove("ReincarnationMode");
                applier.localstatups.put(SecondaryStat.ReincarnationOnOff, mode == null ? 1 : mode);
                return 1;
            }
            case 1320021: 
            case 1320022: 
            case 1320023: {
                applier.duration = applier.effect.getU() * 1000;
                int nCount = applier.localstatups.getOrDefault(SecondaryStat.ReincarnationMission, applier.effect.getZ());
                MapleStatEffect effect = applyfrom.getSkillEffect(1320047);
                if (effect != null) {
                    nCount = nCount * (100 - effect.getZ()) / 100;
                }
                applier.localstatups.put(SecondaryStat.ReincarnationMission, nCount);
                applier.buffz = nCount;
                return 1;
            }
            case 1320019: {
                SecondaryStatValueHolder mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.ReincarnationOnOff);
                if (mbsvh == null || mbsvh.effect == null) {
                    return 0;
                }
                MapleStatEffect effect = applyfrom.getSkillEffect(mbsvh.effect.getSourceId() - 1000 + mbsvh.value);
                if (effect == null) {
                    return 0;
                }
                applier.duration = effect.getBuffDuration(applyfrom);
                effect.applyBuffEffect(applyfrom, applyto, applier.duration, applier.primary, applier.att, applier.passive, applier.pos);
                return 1;
            }
            case 1321053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(0x111B1D);
                applyto.dispelEffect(1221053);
                applyto.dispelEffect(1321053);
                applyto.dispelEffect(2121053);
                applyto.dispelEffect(2221053);
                applyto.dispelEffect(2321053);
                applyto.dispelEffect(3121053);
                applyto.dispelEffect(3221053);
                applyto.dispelEffect(3321041);
                applyto.dispelEffect(4221053);
                applyto.dispelEffect(4121053);
                applyto.dispelEffect(4341053);
                applyto.dispelEffect(5121053);
                applyto.dispelEffect(5221053);
                applyto.dispelEffect(5321053);
                return 1;
            }
            case 400011004: {
                if (!applier.primary) {
                    return 0;
                }
                return 1;
            }
            case 400011047: {
                if (!applier.primary) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.IndieBarrier, applyto.getStat().getCurrentMaxHP() * applier.effect.getY() / 100);
                applier.buffz = 0;
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterRegisterEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 1310016) {
            applyto.getEffects().values().stream().flatMap(Collection::stream).filter(mbsvh -> mbsvh.effect.getSourceId() == 1310016).forEach(mbsvh -> {
                mbsvh.sourceID = -2022125;
            });
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        SecondaryStatValueHolder mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.ReincarnationMission);
        if (applier.totalDamage > 0L && mbsvh != null && mbsvh.effect != null && mbsvh.value > 0 && (!applyto.isAlive() || applyto.isBoss())) {
            mbsvh.value = Math.max(mbsvh.value - 1, 0);
            if (mbsvh.value > 0) {
                applyfrom.send(BuffPacket.giveBuff(applyfrom, mbsvh.effect, Collections.singletonMap(SecondaryStat.ReincarnationMission, mbsvh.effect.getSourceId())));
            } else {
                applyfrom.reduceSkillCooldown(1320019, mbsvh.effect.getY() * 1000);
                applyfrom.dispelEffect(SecondaryStat.ReincarnationMission);
            }
        }
        if (applyfrom != null && applyto != null && applier.effect != null && applier.effect.getSourceId() == 400011085) {
            applyfrom.send(MaplePacketCreator.objSkillEffect(applyto.getObjectId(), applier.effect.getSourceId(), applyfrom.getId(), new Point(0, 0)));
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.ai.attackType != AttackInfo.AttackType.SummonedAttack && applier.ai.mobAttackInfo.size() > 0) {
            MapleSummon summon;
            MapleStatEffect effect = player.getSkillEffect(1321015);
            if (effect != null && player.isSkillCooling(1321015)) {
                player.reduceSkillCooldown(1321015, 350);
            }
            if ((effect = player.getSkillEffect(1320011)) != null && !player.isSkillCooling(effect.getSourceId()) && (summon = player.getSummonBySkillID(1301013)) != null) {
                int mobCount = effect.getInfo().get((Object)MapleStatInfo.mobCount);
                LinkedList oids = new LinkedList();
                for (AttackMobInfo ami : applier.ai.mobAttackInfo) {
                    oids.add(ami.mobId);
                    if (oids.size() < mobCount) continue;
                    break;
                }
                player.getMap().broadcastMessage(player, MaplePacketCreator.summonedBeholderRevengeAttack(player.getId(), summon.getObjectId(), oids), true);
                if (effect.getCooldown(player) > 0) {
                    player.registerSkillCooldown(effect, true);
                }
            }
            if ((400011068 == applier.ai.skillId || 400011069 == applier.ai.skillId) && applier.effect != null) {
                player.addHPMP(applier.effect.getW(), 0);
            }
            if (400011047 == applier.ai.skillId && applier.effect != null) {
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                int i = 0;
                for (AttackMobInfo ami : applier.ai.mobAttackInfo) {
                    MapleMonster mob = player.getMap().getMobObject(ami.mobId);
                    if (mob == null) continue;
                    Point pos = mob.getPosition();
                    for (int j = 0; j < 4; ++j) {
                        ForceAtomObject obj = new ForceAtomObject(player.getSpecialStat().gainForceCounter(), 15, i++, player.getId(), 0, applier.effect.getSourceId());
                        obj.Target = player.getId();
                        obj.Expire = 3500;
                        obj.Position = new Point(Randomizer.rand(-50, 50), 1);
                        obj.ObjPosition = new Point(pos.x + Randomizer.rand(-50, 50), pos.y);
                        obj.Idk5 = 1;
                        obj.B1 = true;
                        createList.add(obj);
                    }
                }
                if (!createList.isEmpty()) {
                    player.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)player.getId(), createList, (int)0), player.getPosition());
                    SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.DarknessAura);
                    if (mbsvh != null && mbsvh.z < mbsvh.effect.getS()) {
                        ++mbsvh.z;
                        player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(SecondaryStat.DarknessAura, mbsvh.effect.getSourceId())));
                    }
                }
                int maxHP = player.getStat().getCurrentMaxHP();
                if (player.getStat().getHp() < maxHP) {
                    player.addHPMP(applier.effect.getX(), 0);
                } else {
                    SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.IndieBarrier, 400011047);
                    if (mbsvh != null) {
                        mbsvh.value = Math.min(mbsvh.value + maxHP * applier.effect.getX() * applier.effect.getV() / 10000, maxHP * applier.effect.getY() / 100);
                        player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(SecondaryStat.IndieBarrier, mbsvh.effect.getSourceId())));
                    }
                }
            }
        }
        return 1;
    }
}

