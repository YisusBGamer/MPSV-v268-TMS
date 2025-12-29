/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.弓手類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleSummon;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import Packet.BuffPacket;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 開拓者
extends AbstractSkillHandler {
    public 開拓者() {
        this.jobs = new MapleJob[]{MapleJob.開拓者1轉, MapleJob.開拓者2轉, MapleJob.開拓者3轉, MapleJob.開拓者4轉};
        for (Field field : Config.constants.skills.開拓者.class.getDeclaredFields()) {
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
            case 3341501: 
            case 3341502: 
            case 3341503: 
            case 3341504: {
                return 3341500;
            }
            case 3341001: {
                return 3341000;
            }
            case 3341003: {
                return 3341002;
            }
            case 3011006: 
            case 3011007: 
            case 3011008: {
                return 3011005;
            }
            case 3301004: {
                return 3301003;
            }
            case 3301009: {
                return 3301008;
            }
            case 3311013: {
                return 3310001;
            }
            case 3311003: {
                return 3311002;
            }
            case 3311011: {
                return 3311010;
            }
            case 3321003: 
            case 3321004: 
            case 3321005: 
            case 3321006: 
            case 3321007: {
                return 3320002;
            }
            case 3321015: 
            case 3321016: 
            case 3321017: 
            case 3321018: 
            case 3321019: 
            case 3321020: 
            case 3321021: {
                return 3321014;
            }
            case 3321036: 
            case 3321037: 
            case 3321038: 
            case 3321039: 
            case 3321040: {
                return 3321035;
            }
            case 400031038: 
            case 400031039: 
            case 400031040: 
            case 400031041: 
            case 400031042: 
            case 400031043: {
                return 400031037;
            }
            case 400031035: {
                return 400031034;
            }
            case 400031047: 
            case 400031048: 
            case 400031049: 
            case 400031050: 
            case 400031051: {
                return 400031057;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 3010001: {
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.IndieCr, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 3300000: 
            case 3320000: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.RelicGauge, 0);
                statups.put(SecondaryStat.PathFinderAncientGuidance, -1);
                return 1;
            }
            case 3011004: 
            case 3300002: 
            case 3321003: 
            case 3341004: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LastUseSkillAttr, 1);
                return 1;
            }
            case 3301003: 
            case 3301004: 
            case 3310001: 
            case 3311013: 
            case 3321004: 
            case 3321005: 
            case 3341000: 
            case 3341001: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LastUseSkillAttr, 2);
                return 1;
            }
            case 3300001: {
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.IndiePDDR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 3310000: {
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 3311002: 
            case 3311003: 
            case 3321006: 
            case 3321007: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CannonShooter_BFCannonBall, 0);
                statups.put(SecondaryStat.LastUseSkillAttr, 3);
                return 1;
            }
            case 3320001: {
                monsterStatus.put(MonsterStatus.CurseTransition, 1);
                return 1;
            }
            case 3320008: {
                effect.getInfo().put(MapleStatInfo.time, effect.getZ());
                statups.put(SecondaryStat.TempSecondaryStat, effect.getY());
                return 1;
            }
            case 3321038: {
                statups.put(SecondaryStat.Stance, 100);
                return 1;
            }
            case 3321034: 
            case 400031047: 
            case 400031049: 
            case 400031051: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 3310006: {
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.indiePMdR));
                return 1;
            }
            case 3311012: {
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.s));
                return 1;
            }
            case 3321014: 
            case 3321016: 
            case 3321018: 
            case 3321020: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LastUseSkillAttr, 0);
                return 1;
            }
            case 3321022: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.SharpEyes, (effect.getX() << 8) + effect.getY());
                return 1;
            }
            case 3321041: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400031041: 
            case 400031042: 
            case 400031043: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                return 1;
            }
            case 400031038: {
                statups.put(SecondaryStat.IndieDamReduceR, 1);
                statups.put(SecondaryStat.IndieAllHitDamR, -effect.getX());
                return 1;
            }
            case 400031037: 
            case 400031039: 
            case 400031040: {
                statups.put(SecondaryStat.IndieDamReduceR, 1);
                return 1;
            }
            case 400031048: {
                effect.getInfo().put(MapleStatInfo.time, 4000);
                statups.put(SecondaryStat.RelicUnboundD, 1);
                return 1;
            }
            case 400031000: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.GuidedArrow, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 3011004: {
                List<MapleMapObject> objects = chr.getMap().getMapObjectsInRange(chr.getPosition(), 633.0, Collections.singletonList(MapleMapObjectType.MONSTER));
                if (objects.isEmpty()) break;
                List<Integer> selectedMobOids = objects.stream().limit(applier.effect.getMobCount(chr)).map(MapleMapObject::getObjectId).collect(Collectors.toList());
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(chr, applier.effect, 0, selectedMobOids)), true);
                break;
            }
            case 3300002: 
            case 3321003: 
            case 3341004: 
            case 400004571: {
                MapleStatEffect effect;
                applier.pos = slea.readPos();
                List<Integer> oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
                if (!oids.isEmpty()) {
                    chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, oids)), true);
                }
                if ((effect = chr.getSkillEffect(3310004)) != null && chr.getBuffedIntValue(SecondaryStat.LastUseSkillAttr) == 2 && effect.makeChanceResult(chr)) {
                    ArrayList<Integer> mobOids = new ArrayList<Integer>();
                    if (!oids.isEmpty()) {
                        for (int i2 = 0; i2 < effect.getBulletCount() + (chr.getBuffStatValueHolder(3321034) != null ? 1 : 0); ++i2) {
                            mobOids.add(oids.get(i2 % oids.size()));
                        }
                        MapleForceAtom mapleForce = forceFactory.getMapleForce(chr, effect, 0, mobOids, chr.getPosition());
                        chr.getMap().broadcastMessage(ForcePacket.forceAtomCreate(mapleForce), chr.getPosition());
                    }
                }
                if (chr.getSpecialStat().getGuidedArrow() != null) {
                    chr.dispelEffect(SecondaryStat.GuidedArrow);
                }
                MapleForceAtom force = forceFactory.getMapleForce(chr, applier.effect, 0, Collections.singletonList(0));
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(force), true);
                chr.getSpecialStat().setGuidedArrow(force);
                return 1;
            }
            case 3311009: {
                applier.pos = slea.readPos();
                return 1;
            }
            case 400031051: {
                if (slea.readByte() != 0) {
                    MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(OutHeader.SummonedSkillUseRequest);
                    mplew.writeInt(400031051);
                    mplew.writePosInt(chr.getPosition());
                    mplew.write(0);
                    mplew.writeInt(0);
                    chr.send(mplew.getPacket());
                    applier.effect = null;
                    return 1;
                }
                int nCount = slea.readInt();
                if (nCount <= 0) {
                    return 0;
                }
                for (int i3 = 0; i3 < nCount; ++i3) {
                    applier.effect.applyBuffEffect(chr, chr, applier.effect.getSummonDuration(chr), false, false, false, slea.readPos());
                    slea.skip(3);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (!applier.passive) {
            int forceCon;
            int force = applyfrom.getBuffedIntValue(SecondaryStat.RelicGauge);
            if (force >= (forceCon = applier.effect.getForceCon()) && applier.effect.getSourceId() == 400031034) {
                forceCon = force;
            }
            if (force < forceCon) {
                return 0;
            }
            this.handleRelicsGain(applyfrom, -forceCon);
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        MapleStatEffect effect;
        if (applier.effect instanceof MobSkill) {
            int maxValue;
            SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.IndieAsrR, 3311012);
            if (mbsvh != null && mbsvh.value < (maxValue = mbsvh.effect.getS() + mbsvh.effect.getX() * mbsvh.effect.getY())) {
                mbsvh.value = Math.min(maxValue, mbsvh.value + mbsvh.effect.getX());
                applyto.send(BuffPacket.giveBuff(applyto, mbsvh.effect, Collections.singletonMap(SecondaryStat.IndieAsrR, mbsvh.effect.getSourceId())));
            }
            return -1;
        }
        switch (applier.effect.getSourceId()) {
            case 1297: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 3011004: 
            case 3300002: 
            case 3301003: 
            case 3301004: 
            case 3310001: 
            case 3311013: 
            case 3321003: 
            case 3321004: 
            case 3321005: {
                effect = applyto.getSkillEffect(3320008);
                if (effect == null || applyto.getBuffedIntValue(SecondaryStat.LastUseSkillAttr) != 3) break;
                effect.applyBuffEffect(applyto, applyto, effect.getBuffDuration(applyto), false, false, true, null);
            }
        }
        switch (applier.effect.getSourceId()) {
            case 3300000: 
            case 3320000: {
                applier.buffz = 0;
                return 1;
            }
            case 3311002: 
            case 3311003: 
            case 3321006: 
            case 3321007: {
                if (applier.att) {
                    return 0;
                }
                int value = Math.min(applier.effect.getY(), Math.max(0, applyto.getBuffedIntValue(SecondaryStat.CannonShooter_BFCannonBall) + (!applier.primary && applier.passive ? 1 : -1)));
                applier.localstatups.put(SecondaryStat.CannonShooter_BFCannonBall, value);
                if (!applier.primary || applier.passive) {
                    applier.localstatups.remove(SecondaryStat.LastUseSkillAttr);
                }
                return 1;
            }
            case 3310006: {
                applyto.addHPMP(applier.effect.getY(), applier.effect.getY());
                return 1;
            }
            case 3321022: {
                applier.buffz = 0;
                effect = applyfrom.getSkillEffect(3320026);
                if (effect != null) {
                    applier.buffz = effect.getIndieIgnoreMobpdpR();
                }
                if ((effect = applyfrom.getSkillEffect(3320027)) != null) {
                    applier.localstatups.put(SecondaryStat.SharpEyes, applier.localstatups.get(SecondaryStat.SharpEyes) + (effect.getX() << 8));
                }
                return 1;
            }
            case 3321034: {
                this.handleRelicsGain(applyto, 1000);
                return 1;
            }
            case 3321041: {
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
            case 400031036: {
                applyto.dispelEffect(3311009);
                return 1;
            }
            case 400031037: {
                if (applyfrom == applyto && applier.primary) {
                    applier.effect.applyAffectedArea(applyto, applier.pos);
                    effect = SkillFactory.getSkill(400031041).getEffect(applier.effect.getLevel());
                    effect.applyBuffEffect(applyfrom, applyto, effect.getBuffDuration(applyfrom), applier.primary, applier.att, applier.passive, applier.pos);
                }
                return 1;
            }
            case 400031039: {
                if (applyfrom == applyto && applier.primary) {
                    applier.effect.applyAffectedArea(applyto, applier.pos);
                    effect = SkillFactory.getSkill(400031042).getEffect(applier.effect.getLevel());
                    effect.applyBuffEffect(applyfrom, applyto, effect.getBuffDuration(applyfrom), applier.primary, applier.att, applier.passive, applier.pos);
                }
                return 1;
            }
            case 400031040: {
                if (applyfrom == applyto && applier.primary) {
                    applier.effect.applyAffectedArea(applyto, applier.pos);
                    effect = SkillFactory.getSkill(400031043).getEffect(applier.effect.getLevel());
                    effect.applyBuffEffect(applyfrom, applyto, effect.getBuffDuration(applyfrom), applier.primary, applier.att, applier.passive, applier.pos);
                }
                return 1;
            }
            case 400031048: {
                if (!applier.primary) {
                    return 0;
                }
                return 1;
            }
            case 400031051: {
                if (applier.primary) {
                    return 0;
                }
                return 1;
            }
        }
        if (applier.localstatups.containsKey(SecondaryStat.LastUseSkillAttr) && applier.localstatups.get(SecondaryStat.LastUseSkillAttr) != 0) {
            SecondaryStatValueHolder mbsvh;
            int time = 0;
            MapleStatEffect effect2 = applyto.getSkillEffect(3320000);
            if (effect2 == null) {
                effect2 = applyto.getSkillEffect(3300000);
                if (effect2 != null) {
                    time = (int)(effect2.getInfoD().get((Object)MapleStatInfo.t) * 1000.0);
                }
            } else {
                time = effect2.getW() * 1000;
            }
            if (time > 0 && (mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.LastUseSkillAttr)) != null && mbsvh.value != applier.localstatups.get(SecondaryStat.LastUseSkillAttr)) {
                applyto.reduceSkillCooldown(3301008, time);
                applyto.reduceSkillCooldown(3311010, time);
                applyto.reduceSkillCooldown(3321012, time);
                applyto.reduceSkillCooldown(3321014, time);
                applyto.reduceSkillCooldown(3321035, time);
            }
        }
        return -1;
    }

    @Override
    public int onAfterRegisterEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        SecondaryStatValueHolder mbsvh;
        if (applier.localstatups.containsKey(SecondaryStat.RelicGauge) && (mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.RelicGauge)) != null) {
            mbsvh.sourceID = 0;
        }
        if (applier.localstatups.containsKey(SecondaryStat.PathFinderAncientGuidance) && (mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.PathFinderAncientGuidance)) != null) {
            mbsvh.sourceID = applyto.getJob();
        }
        if (applier.localstatups.containsKey(SecondaryStat.LastUseSkillAttr) && (mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.LastUseSkillAttr)) != null) {
            mbsvh.sourceID = applyto.getSkillEffect(3320000) != null ? 2 : 1;
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        SecondaryStatValueHolder mbsvh;
        if (applier.effect == null) {
            return -1;
        }
        switch (applier.effect.getSourceId()) {
            case 3301008: {
                List<MapleMapObject> mobs = applyfrom.getMap().getMapObjectsInRange(applyfrom.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER));
                ArrayList<Integer> mobOids = new ArrayList<Integer>();
                for (int i = 0; i < 2; ++i) {
                    mobOids.add(mobs.get(Randomizer.nextInt(mobs.size())).getObjectId());
                }
                applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(applyfrom, applyfrom.getSkillEffect(3301009), applyto.getId(), mobOids, applyto.getPosition())), true);
                break;
            }
            case 3321014: 
            case 3321016: 
            case 3321018: 
            case 3321020: {
                MapleStatEffect effect = applyfrom.getSkillEffect(3320001);
                if (effect == null) break;
                effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
            }
        }
        if (applyto.isAlive() && (mbsvh = applyfrom.getBuffStatValueHolder(3320008)) != null && mbsvh.effect != null) {
            if (mbsvh.value > 0) {
                --mbsvh.value;
                MapleStatEffect effect = applyfrom.getSkillEffect(3320001);
                if (effect != null && mbsvh.effect.makeChanceResult(applyfrom)) {
                    effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
                }
            }
            if (mbsvh.value <= 0) {
                applyfrom.dispelEffect(3320008);
            } else {
                applyfrom.send(BuffPacket.giveBuff(applyfrom, mbsvh.effect, Collections.singletonMap(SecondaryStat.TempSecondaryStat, -1)));
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect == null) {
            return -1;
        }
        MapleStatEffect effect = player.getSkillEffect(3320000);
        if (effect == null) {
            effect = player.getSkillEffect(3300000);
        }
        if (!applier.ai.mobAttackInfo.isEmpty() && effect != null) {
            switch (applier.effect.getSourceId()) {
                case 3301003: 
                case 3301004: 
                case 3310001: 
                case 3311002: 
                case 3311003: 
                case 3311013: 
                case 3321004: 
                case 3321005: 
                case 3321006: 
                case 3321007: {
                    this.handleRelicsGain(player, effect.getY());
                    break;
                }
                case 3011004: 
                case 3300002: 
                case 3321003: {
                    this.handleRelicsGain(player, effect.getX());
                    break;
                }
                case 3311009: {
                    this.handleRelicsGain(player, effect.getS());
                    break;
                }
                case 400031036: {
                    this.handleRelicsGain(player, effect.getV());
                }
            }
        }
        MapleForceFactory mff = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 3321014: 
            case 3321016: 
            case 3321018: 
            case 3321020: {
                player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(applier.effect.getSourceId() + 1, 600, Collections.emptyList()));
                break;
            }
            case 3301003: 
            case 3301004: 
            case 3310001: 
            case 3311013: 
            case 3321004: 
            case 3321005: {
                effect = player.getSkillEffect(3300005);
                if (applier.ai.mobAttackInfo.isEmpty() || effect == null || player.getBuffedIntValue(SecondaryStat.LastUseSkillAttr) != 1 || !effect.makeChanceResult(player)) break;
                List<MapleMapObject> mobs = player.getMap().getMapObjectsInRange(player.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER));
                ArrayList<Integer> mobOids = new ArrayList<Integer>();
                if (mobs.isEmpty()) break;
                for (int i = 0; i < effect.getBulletCount() + (player.getBuffStatValueHolder(3321034) != null ? 1 : 0); ++i) {
                    mobOids.add(mobs.get(i % mobs.size()).getObjectId());
                }
                MapleForceAtom mapleForce = mff.getMapleForce(player, effect, 0, mobOids, player.getPosition());
                player.getMap().broadcastMessage(ForcePacket.forceAtomCreate(mapleForce), player.getPosition());
            }
        }
        if (applier.ai.skillId != 400031048 && !applier.ai.mobAttackInfo.isEmpty() && player.getSummonBySkillID(400031047) != null && player.getBuffStatValueHolder(400031048) == null) {
            player.getSkillEffect(400031048).applyTo(player);
        }
        if (applier.ai.skillId != 400031050 && applier.ai.skillId != 400031051) {
            boolean cd1 = player.isSkillCooling(400031049);
            boolean cd2 = player.isSkillCooling(400031051);
            for (MapleSummon sum : player.getSummonsReadLock()) {
                int value = -1;
                int b = 0;
                switch (sum.getSkillId()) {
                    case 400031049: {
                        if (cd1) break;
                        value = 400031049;
                        if (player.isSkillCooling(400031049)) break;
                        player.registerSkillCooldown(400031049, 1000, false);
                        break;
                    }
                    case 400031051: {
                        if (cd2) break;
                        value = 0;
                        b = 1;
                        if (player.isSkillCooling(400031051)) break;
                        player.registerSkillCooldown(400031051, 4000, false);
                    }
                }
                if (value < 0) continue;
                player.send(MaplePacketCreator.summonedBeholderRevengeInfluence(player.getId(), sum.getObjectId(), value, b));
            }
            player.unlockSummonsReadLock();
        }
        return 1;
    }

    public void handleRelicsGain(MapleCharacter player, int value) {
        int maxValue;
        if (value == 0) {
            return;
        }
        MapleStatEffect effect = player.getSkillEffect(3320000);
        if (effect == null) {
            effect = player.getSkillEffect(3300000);
            maxValue = effect.getU();
        } else {
            maxValue = effect.getV();
        }
        if (player.getBuffStatValueHolder(SecondaryStat.RelicGauge) == null || player.getBuffStatValueHolder(SecondaryStat.PathFinderAncientGuidance) == null) {
            effect.applyBuffEffect(player, player, 2100000000, false, false, true, null);
        }
        LinkedHashMap<SecondaryStat, Integer> statups = new LinkedHashMap<SecondaryStat, Integer>();
        SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.RelicGauge);
        if (mbsvh != null) {
            if (mbsvh.value > maxValue) {
                mbsvh.value = maxValue;
            }
            value = Math.min(maxValue - mbsvh.value, value);
            mbsvh.value = Math.max(0, mbsvh.value + value);
            if (mbsvh.effect != effect) {
                mbsvh.effect = effect;
            }
            statups.put(SecondaryStat.RelicGauge, -1);
        } else {
            value = 0;
        }
        if (value > 0 && (mbsvh = player.getBuffStatValueHolder(SecondaryStat.PathFinderAncientGuidance)) != null) {
            MapleStatEffect eff;
            mbsvh.z = Math.min(maxValue, mbsvh.z + value);
            if (mbsvh.effect != effect) {
                mbsvh.effect = effect;
            }
            if (mbsvh.z >= maxValue && (eff = player.getSkillEffect(3310006)) != null) {
                mbsvh.z = 0;
                eff.applyBuffEffect(player, player, eff.getBuffDuration(player), false, false, true, null);
                player.send(EffectPacket.encodeUserEffectLocal(eff.getSourceId(), EffectOpcode.UserEffect_SkillUse, player.getLevel(), eff.getLevel()));
                player.getMap().broadcastMessage(player, EffectPacket.onUserEffectRemote(player, eff.getSourceId(), EffectOpcode.UserEffect_SkillUse, player.getLevel(), eff.getLevel()), false);
            }
            statups.put(SecondaryStat.PathFinderAncientGuidance, -1);
        }
        if (!statups.isEmpty()) {
            player.send(BuffPacket.giveBuff(player, effect, statups));
        }
    }
}

