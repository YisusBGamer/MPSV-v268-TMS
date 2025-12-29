/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.英雄團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.MonsterEffectHolder;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tools.data.MaplePacketReader;

public class 隱月
extends AbstractSkillHandler {
    public 隱月() {
        this.jobs = new MapleJob[]{MapleJob.隱月, MapleJob.隱月1轉, MapleJob.隱月2轉, MapleJob.隱月3轉, MapleJob.隱月4轉};
        for (Field field : Config.constants.skills.隱月.class.getDeclaredFields()) {
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
        int[] fixskills;
        Skill skil;
        int[] ss;
        for (int i : ss = new int[]{20051284, 20050285, 20050074, 20051005}) {
            if (chr.getLevel() < 200 && i == 20051005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        for (int f : fixskills = new int[]{25001002}) {
            skil = SkillFactory.getSkill(f);
            if (chr.getJob() < f / 10000 || skil == null || chr.getSkillLevel(skil) > 0 || chr.getMasterLevel(skil) > 0) continue;
            applier.skillMap.put(f, new SkillEntry(0, skil.getMasterLevel() == 0 ? skil.getMaxLevel() : skil.getMasterLevel(), SkillFactory.getDefaultSExpiry(skil)));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        return switch (skillId) {
            case 25141501, 25141502, 25141503, 25141504, 25141505, 25141506 -> 25141500;
            case 25141000 -> 25121005;
            case 25100011, 25100012 -> 25101013;
            case 25120018 -> 25111005;
            case 500004096 -> 400051010;
            case 500004097 -> 400051022;
            case 500004098 -> 400051043;
            case 500004099 -> 400051078;
            case 25101003, 25101012 -> 25101004;
            case 25000001 -> 25001000;
            case 25001006 -> 25001204;
            case 25100010 -> 25100009;
            case 25110013, 25111012 -> 25111005;
            case 25111211 -> 25111209;
            case 25120115 -> 25120110;
            case 25110001, 25110002, 25110003 -> 25111000;
            case 25000003 -> 25001002;
            case 25100001, 25100002 -> 25101000;
            case 25121133 -> 25121131;
            case 25121055 -> 25121030;
            case 400051023 -> 400051022;
            case 400051079 -> 400051078;
            case 25120017 -> 25121209;
            default -> -1;
        };
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 25001007: {
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 20051005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 25111209: {
                statups.put(SecondaryStat.ReviveOnce, effect.getInfo().get((Object)MapleStatInfo.x));
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                effect.setOverTime(true);
                return 1;
            }
            case 20050286: 
            case 80000169: {
                effect.setOverTime(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.PreReviveOnce, effect.getInfo().get((Object)MapleStatInfo.prop));
                return 1;
            }
            case 25101009: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.HiddenPossession, 1);
                return 1;
            }
            case 25121209: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SpiritGuard, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 25100002: {
                monsterStatus.put(MonsterStatus.Speed, -effect.getInfo().get((Object)MapleStatInfo.y).intValue());
                return 1;
            }
            case 25121007: {
                monsterStatus.put(MonsterStatus.SeperateSoulP, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 25120002: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 25110210: {
                monsterStatus.put(MonsterStatus.ACC, -effect.getInfo().get((Object)MapleStatInfo.y).intValue());
                monsterStatus.put(MonsterStatus.EVA, -effect.getInfo().get((Object)MapleStatInfo.z).intValue());
                monsterStatus.put(MonsterStatus.AddDamSkill2, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 25121006: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 25121132: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 25121131: {
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieBDR, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.indieIgnoreMobpdpR));
                statups.put(SecondaryStat.HiddenHyperLinkMaximization, 1);
                return 1;
            }
            case 400051010: {
                statups.put(SecondaryStat.TempSecondaryStat, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400051022: {
                statups.put(SecondaryStat.IndieBuffIcon, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 25100009: {
                List<Integer> oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, chr.getSkillEffect(25100010), 0, oids)), true);
                return 1;
            }
            case 25120017: {
                if (chr.isSkillCooling(25120017)) {
                    return 1;
                }
                chr.registerSkillCooldown(applier.effect.getSourceId(), applier.effect.getCooldown(), true);
                chr.getSkillEffect(25121209).applyTo(chr);
                return 1;
            }
            case 25120110: {
                List<Integer> oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, chr.getSkillEffect(25120115), 0, oids)), true);
                return 1;
            }
            case 400051010: {
                chr.clearCooldown(true);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 25101009: {
                if (applyto.getBuffedValue(SecondaryStat.HiddenPossession) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 25121209: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.SpiritGuard);
                if (mbsvh == null) {
                    return 1;
                }
                int value = mbsvh.value;
                int n = value = applier.passive ? Math.max(0, value - 1) : Math.min(applier.effect.getX(), value + 1);
                if (value > 0) {
                    applier.duration = mbsvh.getLeftTime();
                    applier.localstatups.put(SecondaryStat.SpiritGuard, value);
                    return 1;
                }
                applier.overwrite = false;
                applier.localstatups.clear();
                return 1;
            }
            case 25121132: {
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
            case 400051023: {
                applier.cancelEffect = false;
                applier.b7 = false;
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect skillEffect16 = applyfrom.getSkillEffect(25110210);
        if (applier.totalDamage > 0L && skillEffect16 != null) {
            skillEffect16.applyMonsterEffect(applyfrom, applyto, skillEffect16.getMobDebuffDuration(applyfrom));
        }
        long currentTimeMillis = System.currentTimeMillis();
        MapleStatEffect effect = applier.effect;
        if (effect != null && applyto.isAlive() && applyto.getStats().isMobile()) {
            if (effect.getSourceId() == 25101012 && applyto.getEffectHolder(MonsterStatus.WindBreakerPinpointPierce) == null) {
                EnumMap<MonsterStatus, MonsterEffectHolder> statups = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
                statups.put(MonsterStatus.WindBreakerPinpointPierce, new MonsterEffectHolder(applyfrom.getId(), 1, currentTimeMillis, effect.calcMobDebuffDuration(effect.getDuration(), applyfrom), applyfrom.getSkillEffect(25100011)));
                ((MonsterEffectHolder)statups.get((Object)MonsterStatus.WindBreakerPinpointPierce)).z = effect.getX();
                applyto.registerEffect(statups);
                LinkedHashMap<MonsterStatus, Integer> writeStatups = new LinkedHashMap<MonsterStatus, Integer>();
                for (MonsterStatus stat : statups.keySet()) {
                    writeStatups.put(stat, 25100011);
                }
                applyfrom.getMap().broadcastMessage(MobPacket.mobStatSet(applyto, writeStatups), applyto.getPosition());
            } else if (effect.getSourceId() == 25121007 && applyto.getEffectHolder(MonsterStatus.SeperateSoulP) == null && applyto.getEffectHolder(MonsterStatus.SeperateSoulC) == null) {
                MapleMonster monster = MapleLifeFactory.getMonster(applyto.getId());
                assert (monster != null);
                monster.setHp(applyto.getHp());
                monster.registerKill(effect.getMobDebuffDuration(applyfrom));
                MonsterEffectHolder meh = new MonsterEffectHolder(applyfrom.getId(), effect.getX(), currentTimeMillis, effect.getMobDebuffDuration(applyfrom), effect);
                meh.moboid = applyto.getObjectId();
                monster.getEffects().computeIfAbsent(MonsterStatus.SeperateSoulC, k -> new LinkedList()).add(meh);
                monster.setSeperateSoulSrcOID(applyto.getObjectId());
                monster.setSoul(true);
                monster.setPosition(applyto.getPosition());
                applyfrom.getMap().spawnMonster(monster, -1, false);
                applyto.setSeperateSoulSrcOID(monster.getObjectId());
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect eff = player.getEffectForBuffStat(SecondaryStat.TempSecondaryStat);
        if (eff != null && player.getCheatTracker().canNextElementalFocus()) {
            player.getClient().announce(MaplePacketCreator.RegisterElementalFocus(SkillConstants.hn()));
            player.getClient().announce(MaplePacketCreator.UserElementalFocusResult(player.getId(), eff.getSourceId()));
        }
        MapleStatEffect effecForBuffStat11 = player.getEffectForBuffStat(SecondaryStat.HiddenPossession);
        if (applier.totalDamage > 0L && effecForBuffStat11 != null) {
            ArrayList<Integer> list2;
            List<MapleMapObject> mobs;
            MapleForceFactory mff = MapleForceFactory.getInstance();
            if (player.getSkillEffect(25100009) != null && player.getSkillEffect(25100009).makeChanceResult()) {
                mobs = player.getMap().getMapObjectsInRect(effecForBuffStat11.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 100), Collections.singletonList(MapleMapObjectType.MONSTER));
                list2 = new ArrayList<Integer>();
                for (MapleMapObject mob : mobs) {
                    if (list2.size() >= player.getSkillEffect(25100009).getBulletCount()) break;
                    list2.add(mob.getObjectId());
                }
                if (!list2.isEmpty()) {
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, player.getSkillEffect(25100010), 0, list2)), true);
                }
            }
            if (player.getSkillEffect(25120110) != null && player.getSkillEffect(25120110).makeChanceResult()) {
                mobs = player.getMap().getMapObjectsInRect(effecForBuffStat11.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 100), Collections.singletonList(MapleMapObjectType.MONSTER));
                list2 = new ArrayList();
                for (MapleMapObject mob : mobs) {
                    if (list2.size() >= player.getSkillEffect(25120110).getBulletCount()) break;
                    list2.add(mob.getObjectId());
                }
                if (!list2.isEmpty()) {
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, player.getSkillEffect(25120115), 0, list2)), true);
                }
            }
        }
        return 1;
    }
}

