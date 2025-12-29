/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.弓手類別;

import Client.MapleCharacter;
import Client.MapleCoolDownValueHolder;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Server.channel.handler.AttackInfo;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

public class 箭神
extends AbstractSkillHandler {
    public 箭神() {
        this.jobs = new MapleJob[]{MapleJob.獵人, MapleJob.遊俠, MapleJob.箭神};
        for (Field field : Config.constants.skills.冒險家_技能群組.箭神.class.getDeclaredFields()) {
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
            case 3141001: {
                return 3141000;
            }
            case 3141501: 
            case 3141502: {
                return 3141500;
            }
            case 3111016: {
                return 3111015;
            }
            case 3111018: {
                return 3111017;
            }
            case 95001000: {
                return 3111013;
            }
            case 3100010: {
                return 3101009;
            }
            case 3120017: {
                return 3120022;
            }
            case 400030002: {
                return 400031002;
            }
            case 400031021: {
                return 400031020;
            }
            case 400031029: {
                return 400031028;
            }
            case 400031054: {
                return 400031053;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 3101009: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.QuiverCatridge, 1);
                return 1;
            }
            case 3111015: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.FlashMirage, 1);
                return 1;
            }
            case 3111005: {
                effect.setDebuffTime(4000);
                monsterStatus.put(MonsterStatus.Stun, 1);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 3111017: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 3110001: {
                statups.put(SecondaryStat.BowMasterMortalBlow, 0);
                return 1;
            }
            case 3110012: {
                statups.put(SecondaryStat.BowMasterConcentration, 0);
                return 1;
            }
            case 3121002: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.SharpEyes, (effect.getX() << 8) + effect.getY());
                return 1;
            }
            case 3121052: {
                monsterStatus.put(MonsterStatus.IndieSlow, Math.abs(effect.getS()));
                return 1;
            }
            case 3121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 3121054: {
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.Preparation, 1);
                return 1;
            }
            case 400031002: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400030002: {
                effect.getInfo().put(MapleStatInfo.time, 2500);
                return 1;
            }
            case 400031020: {
                statups.put(SecondaryStat.TempSecondaryStat, 1);
                return 1;
            }
            case 400031028: {
                statups.put(SecondaryStat.QuiverFullBurst, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400031053: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ShadowShield, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect instanceof MobSkill) {
            applyto.dispelEffect(SecondaryStat.BowMasterConcentration);
            applyto.send(EffectPacket.showBlessOfDarkness(-1, 3110012));
            applyto.getMap().broadcastMessage(applyto, EffectPacket.showBlessOfDarkness(applyto.getId(), 3110012), false);
            return -1;
        }
        switch (applier.effect.getSourceId()) {
            case 3101009: {
                if (applier.primary) {
                    int mode = 1;
                    if (applyfrom.getSkillEffect(3120022) != null && applyfrom.getBuffedIntValue(SecondaryStat.QuiverCatridge) == 1) {
                        mode = 2;
                        applier.localstatups.put(SecondaryStat.QuiverCatridge, 2);
                    }
                    applyto.send(EffectPacket.showSkillMode(-1, applier.effect.getSourceId(), mode, 0));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showSkillMode(applyto.getId(), applier.effect.getSourceId(), mode, 0), false);
                }
                return 1;
            }
            case 3111015: {
                if (applier.primary) {
                    if (applyto.getBuffedValue(SecondaryStat.FlashMirage) != null) {
                        applyto.dispelEffect(3111015);
                        return 0;
                    }
                } else {
                    int value = applyto.getBuffedIntValue(SecondaryStat.FlashMirage) + 1;
                    MapleStatEffect effect = applyto.getSkillEffect(3120021);
                    if (effect == null) {
                        effect = applier.effect;
                    }
                    if (value > effect.getU()) {
                        value = effect.getU();
                        if (!applyto.isSkillCooling(3111016)) {
                            applyto.registerSkillCooldown(3111016, applier.effect.getCooldown(applyfrom), true);
                            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                            mplew.writeShort(OutHeader.SpeedMirageAttack.getValue());
                            mplew.writeInt(effect.getW());
                            mplew.writeInt(0);
                            applyto.send(mplew.getPacket());
                        }
                    }
                    applier.localstatups.put(SecondaryStat.FlashMirage, value);
                }
                return 1;
            }
            case 3110001: {
                if (applyto.getBuffStatValueHolder(SecondaryStat.IndieDamR, 3110001) != null) {
                    return 0;
                }
                int value = applyto.getBuffedIntValue(SecondaryStat.BowMasterMortalBlow) + 1;
                if (value > applier.effect.getX()) {
                    applyto.dispelEffect(3110001);
                    applier.localstatups.clear();
                    applier.localstatups.put(SecondaryStat.IndieDamR, applier.effect.getY());
                    applier.localstatups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                } else {
                    applier.duration = 2100000000;
                    applier.localstatups.put(SecondaryStat.BowMasterMortalBlow, value);
                }
                return 1;
            }
            case 3110012: {
                applier.localstatups.put(SecondaryStat.BowMasterConcentration, Math.min(applyto.getBuffedIntValue(SecondaryStat.BowMasterConcentration) + 1, 100 / applier.effect.getX()));
                applyto.send(EffectPacket.showBuffEffect(applyto, false, applier.effect.getSourceId(), applyto.getLevel(), 0, null));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showBuffEffect(applyto, true, applier.effect.getSourceId(), applyto.getLevel(), 0, null), false);
                return 1;
            }
            case 3121002: {
                applier.buffz = 0;
                MapleStatEffect effect = applyfrom.getSkillEffect(3120044);
                if (effect != null) {
                    applier.buffz = effect.getIndieIgnoreMobpdpR();
                }
                if ((effect = applyfrom.getSkillEffect(3120045)) != null) {
                    applier.localstatups.put(SecondaryStat.SharpEyes, applier.localstatups.get(SecondaryStat.SharpEyes) + (effect.getX() << 8));
                }
                return 1;
            }
            case 3120018: {
                applier.b3 = true;
                applier.duration = 3000;
                return 1;
            }
            case 3121053: {
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
            case 400031020: {
                if (!applier.primary) {
                    return 0;
                }
                int duration = applier.effect.calcBuffDuration(applier.effect.getS() * 1000, applyfrom);
                applyfrom.getTempValues().put("殘影之矢時間", new Pair<Long, Integer>(System.currentTimeMillis(), duration));
                applyfrom.send(MaplePacketCreator.InhumanSpeedAttackeRequest(applyfrom.getId(), (byte)1, duration));
                return 1;
            }
            case 400031053: {
                applier.buffz = 0;
                if (!applier.primary) {
                    SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.ShadowShield);
                    if (mbsvh == null || mbsvh.z >= applier.effect.getX()) {
                        return 0;
                    }
                    applier.buffz = Math.min(applier.effect.getX(), mbsvh.z + 1);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 3100001: 
            case 3100010: 
            case 3111005: 
            case 3111016: 
            case 3120008: 
            case 3120017: 
            case 95001000: 
            case 400031020: 
            case 400031021: 
            case 400031054: {
                return -1;
            }
        }
        MapleForceFactory mff = MapleForceFactory.getInstance();
        SecondaryStatValueHolder mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.QuiverCatridge);
        if (mbsvh != null && mbsvh.effect != null && mbsvh.value == 1) {
            MapleStatEffect effect;
            int prop;
            if (applyfrom.getSkillEffect(3120017) == null) {
                prop = mbsvh.effect.getU();
                effect = mbsvh.effect;
            } else {
                effect = applyfrom.getSkillEffect(3120022);
                prop = effect.getU();
            }
            if (effect != null && Randomizer.isSuccess(prop)) {
                applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(mff.getMapleForce(applyfrom, effect, 0, applyto.getObjectId(), null, null)), true);
            }
        }
        if (applyto != null && applyfrom.getBuffStatValueHolder(SecondaryStat.IndieDamR, 400031002) != null && applyfrom.getCheatTracker().canNextBonusAttack(5000L)) {
            applyfrom.getSkillEffect(400030002).applyTo(applyfrom);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        List<MapleMapObject> objs;
        MapleStatEffect effecForBuffStat6;
        MapleStatEffect effect;
        SecondaryStatValueHolder mbsvh;
        if (applier.ai.skillId == 95001000 || applier.ai.skillId == 3100001 || applier.ai.skillId == 3120008 || applier.ai.attackType == AttackInfo.AttackType.SummonedAttack) {
            return -1;
        }
        MapleForceFactory mff = MapleForceFactory.getInstance();
        if (applier.ai.mobAttackInfo.size() > 0) {
            mbsvh = player.getBuffStatValueHolder(SecondaryStat.QuiverCatridge);
            if (mbsvh != null) {
                if (mbsvh.value == 2 && (effect = player.getSkillEffect(3120022)) != null && Randomizer.isSuccess(effect.getW())) {
                    player.addHPMP(player.getStat().getCurrentMaxHP() * effect.getX() / 100, 0, false);
                    player.send(EffectPacket.showBlessOfDarkness(-1, 3101009));
                    player.getMap().broadcastMessage(player, EffectPacket.showBlessOfDarkness(player.getId(), 3101009), false);
                }
            } else {
                effect = player.getSkillEffect(3101009);
                if (effect != null) {
                    effect.unprimaryPassiveApplyTo(player);
                }
            }
        }
        if (applier.ai.attackType == AttackInfo.AttackType.ShootAttack && applier.ai.mobAttackInfo.size() > 0 && (effect = player.getSkillEffect(3110001)) != null) {
            int value = player.getBuffedIntValue(SecondaryStat.BowMasterMortalBlow) + 1;
            if (value >= effect.getX()) {
                for (int i = 0; i < Math.min(applier.ai.mobAttackInfo.size(), 2); ++i) {
                    MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                    mplew.writeShort(OutHeader.LP_MobSpecialEffectBySkill.getValue());
                    mplew.writeInt(applier.ai.mobAttackInfo.get((int)i).mobId);
                    mplew.writeInt(effect.getSourceId());
                    mplew.writeInt(player.getId());
                    mplew.writeShort(0);
                    player.getMap().broadcastMessage(player, mplew.getPacket(), true);
                }
            }
            effect.unprimaryPassiveApplyTo(player);
        }
        if (applier.ai.mobAttackInfo.size() > 0 && (effect = player.getSkillEffect(3110012)) != null) {
            effect.unprimaryPassiveApplyTo(player);
        }
        if (applier.ai.attackType == AttackInfo.AttackType.ShootAttack && applier.ai.mobAttackInfo.size() > 0 && (mbsvh = player.getBuffStatValueHolder(3111015)) != null) {
            boolean apply = true;
            switch (SkillConstants.getLinkedAttackSkill(applier.ai.skillId)) {
                case 3111013: 
                case 3121020: 
                case 400031002: 
                case 400031020: 
                case 400031028: 
                case 400031053: {
                    int value = (Integer)player.getTempValues().getOrDefault("閃光幻象暴風技能累計", 0) + 1;
                    if (value >= mbsvh.effect.getU2()) {
                        value = 0;
                    } else {
                        apply = false;
                    }
                    player.getTempValues().put("閃光幻象暴風技能累計", value);
                }
            }
            if (apply) {
                mbsvh.effect.unprimaryPassiveApplyTo(player);
            }
        }
        if (applier.ai.attackType == AttackInfo.AttackType.ShootAttack && applier.ai.mobAttackInfo.size() > 0 && (effect = player.getSkillEffect(3120018)) != null) {
            MapleCoolDownValueHolder mcvh = player.getSkillCooldowns().get(3120018);
            if (mcvh == null || mcvh.getLeftTime() <= 0) {
                player.registerSkillCooldown(3120018, effect.getY() * 1000, true);
                player.send(EffectPacket.showBuffEffect(player, false, applier.effect.getSourceId(), player.getLevel(), 1, null));
                player.getMap().broadcastMessage(player, EffectPacket.showBuffEffect(player, true, applier.effect.getSourceId(), player.getLevel(), 1, null), false);
            } else if (mcvh.getLeftTime() > 1000) {
                player.registerSkillCooldown(3120018, Math.max(1000, mcvh.getLeftTime() - effect.getW() * 1000), true);
            }
        }
        if (applier.ai.attackType == AttackInfo.AttackType.ShootAttack && SkillConstants.getLinkedAttackSkill(applier.ai.skillId) != 400031020 && applier.ai.mobAttackInfo.size() > 0) {
            mbsvh = player.getBuffStatValueHolder(SecondaryStat.TempSecondaryStat, 400031020);
            if (mbsvh != null) {
                Pair<Long, Integer> timeInfo;
                Object timeDat = player.getTempValues().getOrDefault("殘影之矢時間", null);
                long timeNow = System.currentTimeMillis();
                int duration = mbsvh.effect.calcBuffDuration(mbsvh.effect.getS() * 1000, player);
                if (timeDat == null) {
                    timeInfo = new Pair<Long, Integer>(timeNow - 1000L, duration);
                } else {
                    timeInfo = (Pair<Long, Integer>) timeDat;
                    duration += ((Integer)timeInfo.getRight()).intValue();
                }
                if (timeNow - (Long)timeDat >= 1000L) {
                    timeInfo.left = timeNow;
                    duration = (int)((long)duration - Math.max(0L, timeNow - (Long)timeDat));
                    player.send(MaplePacketCreator.InhumanSpeedAttackeRequest(player.getId(), (byte)1, duration));
                }
                timeInfo.right = duration;
                player.getTempValues().put("殘影之矢時間", timeInfo);
            } else if (player.isSkillCooling(400031020) && (effect = player.getSkillEffect(400031020)) != null) {
                int value = (Integer)player.getTempValues().getOrDefault("殘影之矢技能累計", 0) + 1;
                if (value >= effect.getU()) {
                    value = 0;
                    player.send(EffectPacket.showBuffEffect(player, false, 400031021, player.getLevel(), 1, null));
                    player.getMap().broadcastMessage(player, EffectPacket.showBuffEffect(player, true, 400031021, player.getLevel(), 1, null), false);
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, SkillFactory.getSkill(400031021).getEffect(effect.getLevel()), 0, 0, Collections.emptyList(), player.getPosition())), true);
                }
                player.getTempValues().put("殘影之矢技能累計", value);
            }
        }
        if ((effecForBuffStat6 = player.getEffectForBuffStat(SecondaryStat.QuiverFullBurst)) != null && player.getCheatTracker().canNextAllRocket(400031029, 2500)) {
            Iterator<MapleMapObject> iterator2 = player.getMap().getMapObjectsInRange(player.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER)).iterator();
            for (int n8 = 0; n8 < effecForBuffStat6.getMobCount() && iterator2.hasNext(); ++n8) {
                player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, effecForBuffStat6, 0, 0, null, iterator2.next().getPosition())), true);
            }
        }
        if (applier.ai.attackType == AttackInfo.AttackType.ShootAttack && applier.ai.mobAttackInfo.size() > 0 && (mbsvh = player.getBuffStatValueHolder(SecondaryStat.ShadowShield)) != null && mbsvh.effect != null && mbsvh.z > 0 && !player.isSkillCooling(400031054) && (effect = SkillFactory.getSkill(400031054).getEffect(mbsvh.effect.getLevel())) != null && !(objs = player.getMap().getMonstersInRect(effect.calculateBoundingBox(player.getPosition(), player.isFacingLeft()))).isEmpty()) {
            LinkedList<Integer> toMobOid = new LinkedList<Integer>();
            for (int i = 0; i < effect.getBulletCount(); ++i) {
                toMobOid.add(objs.get(i % objs.size()).getObjectId());
            }
            Point p = new Point(player.getPosition());
            player.registerSkillCooldown(400031054, (int)(mbsvh.effect.getInfoD().get((Object)MapleStatInfo.t) * 1000.0), false);
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(OutHeader.LP_UserEffectLocal);
            mplew.write(EffectOpcode.UserEffect_SkillSpecial.getValue());
            mplew.writeInt(mbsvh.effect.getSourceId());
            mplew.writeInt(mbsvh.effect.getLevel());
            mplew.writePosInt(p);
            player.send(mplew.getPacket());
            p.y -= 100;
            player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, effect, 0, 0, toMobOid, p)), true);
        }
        return 1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        if (!applier.overwrite && applier.effect.getSourceId() == 400031020) {
            player.getTempValues().remove("殘影之矢時間");
            player.send(MaplePacketCreator.InhumanSpeedAttackeRequest(player.getId(), (byte)0, 0));
        }
        return -1;
    }
}

