/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.盜賊類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 影武者
extends AbstractSkillHandler {
    public 影武者() {
        this.jobs = new MapleJob[]{MapleJob.盜賊_影武, MapleJob.下忍, MapleJob.中忍, MapleJob.上忍, MapleJob.隱忍, MapleJob.影武者};
        for (Field field : Config.constants.skills.冒險家_技能群組.影武者.class.getDeclaredFields()) {
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
            case 4361501: 
            case 4361502: 
            case 4361503: {
                return 4361500;
            }
            case 4361000: {
                return 4341009;
            }
            case 4301007: {
                return 4300008;
            }
            case 4321007: {
                return 4320008;
            }
            case 4331012: {
                return 4331006;
            }
            case 400040006: {
                return 400041006;
            }
            case 400041043: {
                return 400041042;
            }
            case 400041076: 
            case 400041077: 
            case 400041078: {
                return 400041075;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 4321002: {
                monsterStatus.put(MonsterStatus.TotalDamParty, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 0x41EEEE: {
                monsterStatus.put(MonsterStatus.Seal, 1);
                return 1;
            }
            case 0x421211: {
                statups.put(SecondaryStat.DarkSight, effect.getLevel());
                statups.put(SecondaryStat.Speed, 1);
                return 1;
            }
            case 4341002: {
                effect.getInfo().put(MapleStatInfo.time, 60000);
                effect.setHpR((double)(-effect.getInfo().get((Object)MapleStatInfo.x).intValue()) / 100.0);
                statups.put(SecondaryStat.FinalCut, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 4331006: {
                effect.setDebuffTime(effect.getDuration());
                effect.getInfo().put(MapleStatInfo.time, (int)(effect.getInfoD().get((Object)MapleStatInfo.t) * 1000.0));
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 4331012: {
                effect.setDebuffTime(SkillFactory.getSkill(4331006).getEffect(effect.getLevel()).getDebuffTime());
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 4330009: {
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                return 1;
            }
            case 4331002: {
                statups.put(SecondaryStat.ShadowPartner, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 4341053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 4341052: {
                statups.put(SecondaryStat.Asura, effect.getInfo().get((Object)MapleStatInfo.time) * 1000);
                return 1;
            }
            case 4341054: {
                statups.clear();
                statups.put(SecondaryStat.WindBreakerFinal, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400041006: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.ignoreMobpdpR));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 4341006: {
                applier.pos = slea.readPos();
                return 1;
            }
            case 400041021: {
                c.announce(MaplePacketCreator.sendSkillUseResult(true, applier.effect.getSourceId()));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 1282: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 4330009: {
                applyto.getClient().announce(MaplePacketCreator.sendCritAttack());
                return 1;
            }
            case 4331006: {
                applier.b4 = false;
                return 1;
            }
            case 4341002: {
                if (applier.primary || !applier.passive) {
                    return 0;
                }
                applier.maskedDuration = 3000;
                applier.maskedstatups.put(SecondaryStat.NotDamaged, 1);
                if (applyto.getBuffedValue(SecondaryStat.FinalCut) != null && !applyto.getCheatTracker().canNext絕殺刃()) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                    applier.localstatups.put(SecondaryStat.FinalCut, applier.effect.getY());
                    applier.duration -= applier.effect.getV() * 1000;
                    return 1;
                }
                applyto.getCheatTracker().setNext絕殺刃(System.currentTimeMillis() + (long)applier.effect.getV() * 1000L);
                applyto.send(影武者.DummyPacket((short)139, "00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 46 00 00 00 FA 15 42 00 40 0D 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 01 00 00 00 00 09"));
                applyto.send(影武者.DummyPacket((short)139, "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 8C 00 0A 3D 42 00 60 EA 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 00 00 00 00 0A"));
                return 1;
            }
            case 4341053: {
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
            case 4341054: {
                if (applier.passive) {
                    return 0;
                }
                return 1;
            }
            case 4341011: {
                applyto.reduceSkillCooldownRate(4341002, applier.effect.getX());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect skillEffect11;
        if (this.containsJob(applyfrom.getJobWithSub()) && applier.totalDamage > 0L && (skillEffect11 = applyfrom.getSkillEffect(4320005)) != null) {
            MapleStatEffect skillEffect12 = applyfrom.getSkillEffect(4340012);
            if (skillEffect12 != null) {
                skillEffect11 = skillEffect12;
            }
            skillEffect11.applyMonsterEffect(applyfrom, applyto, skillEffect11.getDotTime() * 1000);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.totalDamage > 0L) {
            MapleStatEffect eff = player.getEffectForBuffStat(SecondaryStat.DarkSight);
            if (eff != null && eff.getSourceId() == 4001003 && this.containsJob(player.getJobWithSub()) && (eff = player.getSkillEffect(0x421211)) != null && !eff.makeChanceResult(player)) {
                player.dispelEffect(SecondaryStat.DarkSight);
            }
            if (applier.effect != null) {
                MapleStatEffect addSkillEffect;
                Skill skill;
                int skillLevel;
                int add_skillId = 0;
                int cooldownSkillId = 0;
                int add_skillValue = 0;
                switch (applier.effect.getSourceId()) {
                    case 4341004: {
                        add_skillId = 400041076;
                        cooldownSkillId = 400041075;
                        add_skillValue = 6;
                        break;
                    }
                    case 4341009: {
                        add_skillId = 400041078;
                        cooldownSkillId = 400041075;
                        add_skillValue = 5;
                        break;
                    }
                    default: {
                        add_skillId = 0;
                    }
                }
                if (add_skillId != 0 && (skillLevel = SkillConstants.getLinkedAttackSkill(cooldownSkillId)) > 0 && (skill = SkillFactory.getSkill(cooldownSkillId)) != null && (addSkillEffect = skill.getEffect(player.getSkillLevel(skillLevel))) != null && !player.isSkillCooling(cooldownSkillId)) {
                    player.registerSkillCooldown(cooldownSkillId, addSkillEffect.getCooldown(player), true);
                    ExtraSkill eskill = new ExtraSkill(add_skillId, player.getPosition());
                    eskill.Value = add_skillValue;
                    eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
                    player.send(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                }
            }
        }
        if (applier.effect != null && applier.effect.getSourceId() == 4341002) {
            applier.effect.applyBuffEffect(player, player, applier.effect.getBuffDuration(player), false, false, true, player.getPosition());
        }
        if (applier.effect != null && applier.effect.getSourceId() == 400041042) {
            ExtraSkill eskill = new ExtraSkill(400041043, player.getPosition());
            eskill.Value = 1;
            eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
            player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
        }
        return -1;
    }

    public static byte[] DummyPacket(short header, Object ... data) {
        MaplePacketLittleEndianWriter PACKET = new MaplePacketLittleEndianWriter();
        PACKET.writeShort(header);
        for (Object obj : data) {
            if (obj instanceof Integer) {
                PACKET.writeInt((Integer)obj);
                continue;
            }
            if (obj instanceof Short) {
                PACKET.writeShort(((Short)obj).shortValue());
                continue;
            }
            if (obj instanceof Byte) {
                PACKET.write((Byte)obj);
                continue;
            }
            if (!(obj instanceof String)) continue;
            PACKET.writeHexString((String)obj);
        }
        return PACKET.getPacket();
    }
}

