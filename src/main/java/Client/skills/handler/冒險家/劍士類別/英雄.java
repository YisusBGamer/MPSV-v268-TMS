/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.劍士類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Packet.BuffPacket;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 英雄
extends AbstractSkillHandler {
    public 英雄() {
        this.jobs = new MapleJob[]{MapleJob.狂戰士, MapleJob.十字軍, MapleJob.英雄};
        for (Field field : Config.constants.skills.冒險家_技能群組.type_劍士.英雄.class.getDeclaredFields()) {
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
            case 500004000: {
                return 400011001;
            }
            case 500004002: {
                return 400011027;
            }
            case 500004003: {
                return 400011124;
            }
            case 500004001: {
                return 400011073;
            }
            case 1141001: {
                return 1141000;
            }
            case 1120013: 
            case 0x111711: {
                return 1121008;
            }
            case 400011125: 
            case 400011126: 
            case 400011127: {
                return 400011124;
            }
            case 0x111B1F: {
                return 0x111B1E;
            }
            case 400011002: {
                return 400011001;
            }
            case 400011140: {
                return 400011027;
            }
            case 400011074: 
            case 400011075: 
            case 400011076: {
                return 400011073;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 1101013: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ComboCounter, 1);
                return 1;
            }
            case 1101006: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndiePowerGuard, effect.getInfo().get((Object)MapleStatInfo.indiePowerGuard));
                return 1;
            }
            case 1111003: {
                statups.put(SecondaryStat.CrusaderPanic, 1);
                effect.setDebuffTime(effect.getV() * 20000);
                monsterStatus.put(MonsterStatus.Panic, 1);
                return 1;
            }
            case 1121015: {
                monsterStatus.put(MonsterStatus.Incizing, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 1121016: {
                monsterStatus.put(MonsterStatus.MagicCrash, 1);
                return 1;
            }
            case 1121010: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Enrage, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.EnrageCr, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.EnrageCrDamMin, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 0x111B1D: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 0x111B1E: {
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieCr, 30);
                statups.put(SecondaryStat.Stance, 100);
                statups.put(SecondaryStat.AsrR, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.TerR, effect.getInfo().get((Object)MapleStatInfo.y));
                statups.put(SecondaryStat.ComboCounter, 11);
                return 1;
            }
            case 0x111B1F: {
                effect.getInfo().put(MapleStatInfo.cooltimeMS, effect.getInfo().get((Object)MapleStatInfo.subTime));
                return 1;
            }
            case 400011027: {
                effect.getInfo().put(MapleStatInfo.time, 1680);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 400011140: {
                statups.put(SecondaryStat.IndieDamReduceR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 400011073: {
                statups.put(SecondaryStat.HeroComboInstinct, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 1141500: {
                effect.getInfo().put(MapleStatInfo.time, 7000);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400011001) {
            if (!chr.isSkillCooling(400011001)) {
                chr.registerSkillCooldown(400011001, applier.effect.getX() * 1000, true);
                return 1;
            }
            SecondaryStatValueHolder buffStatValueHolder2 = chr.getBuffStatValueHolder(400011001);
            if (buffStatValueHolder2 != null && chr.getSummonBySkillID(400011001) != null) {
                int w = buffStatValueHolder2.getLeftTime();
                chr.removeSummonBySkillID(400011001, 5);
                MapleStatEffect skillEffect7 = chr.getSkillEffect(400011002);
                if (skillEffect7 != null) {
                    skillEffect7.applyTo(chr, w);
                }
                return 0;
            }
            SecondaryStatValueHolder buffStatValueHolder3 = chr.getBuffStatValueHolder(400011002);
            if (buffStatValueHolder3 != null && chr.getSummonBySkillID(400011002) != null) {
                chr.removeSummonBySkillID(400011002, 5);
                MapleStatEffect skillEffect8 = chr.getSkillEffect(400011001);
                int w2 = buffStatValueHolder3.getLeftTime();
                if (skillEffect8 != null) {
                    skillEffect8.applyTo(chr, w2);
                }
                return 0;
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 0x111B1E: {
                applier.buffz = applier.effect.getU2();
                return 1;
            }
            case 0x111B1D: {
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
            case 400011001: {
                MapleStatEffect eff = applyfrom.getSkillEffect(1120003);
                if (eff == null) {
                    eff = applyfrom.getSkillEffect(1101013);
                }
                SecondaryStatValueHolder mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.ComboCounter);
                if (eff != null && mbsvh != null && mbsvh.value < eff.getX()) {
                    mbsvh.value = eff.getX();
                    applyfrom.send(BuffPacket.giveBuff(applyfrom, mbsvh.effect, Collections.singletonMap(SecondaryStat.ComboCounter, mbsvh.effect.getSourceId())));
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effect = applyfrom.getEffectForBuffStat(SecondaryStat.CrusaderPanic);
        if (effect != null && effect.makeChanceResult(applyfrom)) {
            effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleMonster mob;
        MapleStatEffect effect;
        SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.ComboCounter);
        if (mbsvh != null && mbsvh.effect != null && applier.totalDamage > 0L && applier.ai.skillId != 400011074 && applier.ai.skillId != 400011075 && applier.ai.skillId != 400011076) {
            effect = player.getSkillEffect(1110013);
            MapleStatEffect effectEnchant = player.getSkillEffect(1120003);
            if (effect == null) {
                effect = player.getSkillEffect(1101013);
            }
            if (effect != null) {
                int maxCombo;
                int n = maxCombo = effectEnchant != null ? effectEnchant.getX() : effect.getX();
                if (mbsvh.value < maxCombo + 1 && effect.makeChanceResult(player)) {
                    ++mbsvh.value;
                    if (effectEnchant != null && effectEnchant.makeChanceResult(player) && mbsvh.value < maxCombo + 1) {
                        ++mbsvh.value;
                    }
                    player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(SecondaryStat.ComboCounter, mbsvh.effect.getSourceId())));
                }
            }
        }
        if ((mbsvh = player.getBuffStatValueHolder(SecondaryStat.Stance)) != null && applier.ai.mobAttackInfo.size() > 0 && mbsvh.effect != null && mbsvh.effect.getSourceId() == 0x111B1E && mbsvh.z > 0 && (effect = player.getSkillEffect(0x111B1F)) != null && !player.isSkillCooling(0x111B1F) && (mob = player.getMap().getMobObject(applier.ai.mobAttackInfo.get((int)0).mobId)) != null) {
            --mbsvh.z;
            player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(SecondaryStat.Stance, mbsvh.effect.getSourceId())));
            effect.applyTo(player, mob.getPosition(), true);
        }
        if (applier.totalDamage > 0L && applier.effect != null && applier.effect.getSourceId() == 400011027) {
            player.getSkillEffect(400011140).applyTo(player, true);
        }
        if (player.getEffectForBuffStat(SecondaryStat.HeroComboInstinct) != null && player.getCheatTracker().canNextBonusAttack(3000L)) {
            player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400011074, 0, Collections.emptyList()));
            player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400011075, 0, Collections.emptyList()));
            player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400011076, 0, Collections.emptyList()));
        }
        return 1;
    }
}

