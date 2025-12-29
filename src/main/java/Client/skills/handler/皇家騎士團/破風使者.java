/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.皇家騎士團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceAtom;
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
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.ForcePacket;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import tools.Randomizer;
import tools.Triple;
import tools.data.MaplePacketReader;

public class 破風使者
extends AbstractSkillHandler {
    public 破風使者() {
        this.jobs = new MapleJob[]{MapleJob.破風使者1轉, MapleJob.破風使者2轉, MapleJob.破風使者3轉, MapleJob.破風使者4轉};
        for (Field field : Config.constants.skills.皇家騎士團_技能群組.破風使者.class.getDeclaredFields()) {
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
        super.baseSkills(chr, applier);
        Skill skill = SkillFactory.getSkill(0x989777);
        if (skill != null && chr.getSkillLevel(skill) <= 0) {
            applier.skillMap.put(skill.getId(), new SkillEntry(1, skill.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 13141501: 
            case 13141502: 
            case 13141503: 
            case 13141504: 
            case 13141505: {
                return 13141500;
            }
            case 13141000: {
                return 13121001;
            }
            case 500004068: {
                return 400031003;
            }
            case 500004069: {
                return 400031022;
            }
            case 500004070: {
                return 400031030;
            }
            case 500004071: {
                return 400031058;
            }
            case 13100027: {
                return 13101022;
            }
            case 13110027: {
                return 13110022;
            }
            case 13120010: {
                return 13120003;
            }
            case 400031004: {
                return 400031003;
            }
            case 400031031: {
                return 400031030;
            }
            case 400031059: {
                return 400031058;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 13001022: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CygnusElementSkill, 1);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 13101022: 
            case 13110022: 
            case 13120003: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.TriflingWhimOnOff, 1);
                return 1;
            }
            case 400031000: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.GuidedArrow, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 13121004: {
                statups.put(SecondaryStat.IllusionStep, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.ACCR, effect.getInfo().get((Object)MapleStatInfo.y));
                statups.put(SecondaryStat.DEXR, effect.getInfo().get((Object)MapleStatInfo.prop));
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                return 1;
            }
            case 13121055: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 30000);
                statups.put(SecondaryStat.StormBringer, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 13111024: {
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.z));
                return 1;
            }
            case 13120007: {
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.w));
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.z));
                return 1;
            }
            case 13111021: {
                effect.getInfo().put(MapleStatInfo.time, 210000000);
                monsterStatus.put(MonsterStatus.AddDamSkill, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 13121005: {
                statups.put(SecondaryStat.SharpEyes, (effect.getInfo().get((Object)MapleStatInfo.x) << 8) + effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 13121052: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 13121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 13121017: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 20000);
                statups.put(SecondaryStat.StormBringer, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400031003: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.HowlingGaleStack, 0);
                return 1;
            }
            case 400031030: {
                statups.put(SecondaryStat.WindBreakerStormGuard, effect.getInfo().get((Object)MapleStatInfo.w));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 13101022: {
                if (chr.getSkillEffect(13120003) != null) {
                    applier.effect = chr.getSkillEffect(13120003);
                    return 1;
                }
                if (chr.getSkillEffect(13110022) != null) {
                    applier.effect = chr.getSkillEffect(13110022);
                    return 1;
                }
                return 1;
            }
            case 400031022: {
                ArrayList<Integer> moboids = new ArrayList<Integer>();
                for (MapleMapObject obj : chr.getMap().getMapObjectsInRange(chr.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER))) {
                    moboids.add(obj.getObjectId());
                    if (moboids.size() < applier.effect.getMobCount()) continue;
                    break;
                }
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, moboids)), true);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 13121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000 || applyto.getJob() / 1000 == 5) {
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
            case 400031003: {
                int value = Math.min(applyto.getBuffedIntValue(SecondaryStat.HowlingGaleStack) + (applier.passive ? 1 : -1), 2);
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.HowlingGaleStack);
                if (!applier.primary || value < 0 || applier.passive && mbsvh != null && System.currentTimeMillis() < mbsvh.startTime + (long)applier.effect.getX() * 700L) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.HowlingGaleStack, value);
                return 1;
            }
            case 400031030: {
                SecondaryStatValueHolder buffStatValueHolder32;
                if (applier.primary || (buffStatValueHolder32 = applyto.getBuffStatValueHolder(SecondaryStat.WindBreakerStormGuard)) == null) {
                    return 1;
                }
                int max2 = Math.max(applyto.getBuffedIntValue(SecondaryStat.WindBreakerStormGuard) - 1, 0);
                if (max2 > 0) {
                    applier.duration = buffStatValueHolder32.getLeftTime();
                    applier.localstatups.put(SecondaryStat.WindBreakerStormGuard, max2);
                    return 1;
                }
                applier.overwrite = false;
                applier.localstatups.clear();
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat5;
        MapleForceFactory mmf = MapleForceFactory.getInstance();
        if (this.containsJob(applyfrom.getJobWithSub()) && (effecForBuffStat5 = applyfrom.getEffectForBuffStat(SecondaryStat.StormBringer)) != null && applyto.isAlive() && effecForBuffStat5.makeChanceResult(applyfrom) && applier.effect != null) {
            boolean n5 = false;
            applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(mmf.getMapleForce(applyfrom, effecForBuffStat5, 0)), true);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleForceFactory mff = MapleForceFactory.getInstance();
        MapleStatEffect effecForBuffStat6 = player.getEffectForBuffStat(SecondaryStat.TriflingWhimOnOff);
        if (effecForBuffStat6 != null && applier.effect != null) {
            MapleStatEffect skillEffect10;
            List<MapleMapObject> mobs = player.getMap().getMapObjectsInRect(applier.effect.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 500), Collections.singletonList(MapleMapObjectType.MONSTER));
            ArrayList<Integer> list = new ArrayList<Integer>();
            player.getMap().getAllMonster().forEach(mob -> {
                if (mob.isBoss()) {
                    list.add(mob.getObjectId());
                }
            });
            mobs.forEach(mob -> list.add(mob.getObjectId()));
            MapleStatEffect skillEffect9 = player.getSkillEffect(13110022);
            if (skillEffect9 != null) {
                effecForBuffStat6 = skillEffect9;
            }
            if ((skillEffect10 = player.getSkillEffect(13120003)) != null) {
                effecForBuffStat6 = skillEffect10;
            }
            if (!list.isEmpty() && effecForBuffStat6.getSourceId() != SkillConstants.getLinkedAttackSkill(applier.effect.getSourceId())) {
                if (effecForBuffStat6.makeChanceResult(player)) {
                    MapleStatEffect skillEffect11 = player.getSkillEffect(effecForBuffStat6.getSourceId() == 13120003 ? effecForBuffStat6.getSourceId() + 7 : effecForBuffStat6.getSourceId() + 5);
                    if (skillEffect11 != null) {
                        int times = effecForBuffStat6.getX();
                        for (int i = 0; i < times; ++i) {
                            player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, skillEffect11, 0, list)), true);
                        }
                    }
                } else if (Randomizer.nextInt(100) <= effecForBuffStat6.getSubProp()) {
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, effecForBuffStat6, 0, list)), true);
                }
            }
        }
        MapleStatEffect effecForBuffStat9 = player.getEffectForBuffStat(SecondaryStat.WindBreakerStormGuard);
        if (applier.totalDamage > 0L && effecForBuffStat9 != null && player.getCheatTracker().canNextAllRocket(400031031, effecForBuffStat9.getW2() * 1000)) {
            List<MapleMapObject> mapObjectsInRange2 = player.getMap().getMapObjectsInRange(player.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER));
            ArrayList list3 = new ArrayList();
            mapObjectsInRange2.forEach(sx2 -> list3.add(sx2.getObjectId()));
            if (!list3.isEmpty()) {
                ArrayList<Triple<Integer, Integer, Map<Integer, MapleForceAtom>>> list4 = new ArrayList<Triple<Integer, Integer, Map<Integer, MapleForceAtom>>>();
                for (int l = 0; l < effecForBuffStat9.getQ2(); ++l) {
                    list4.add(new Triple<Integer, Integer, Map<Integer, MapleForceAtom>>(400031031, 51, Collections.singletonMap((Integer)list3.get(Randomizer.nextInt(list3.size())), mff.getMapleForce(player, player.getSkillEffect(400031031), 0))));
                }
                player.getMap().broadcastMessage(player, ForcePacket.forceTeleAtomCreate(player.getId(), 400031031, list4), true);
            }
        }
        return 1;
    }
}

