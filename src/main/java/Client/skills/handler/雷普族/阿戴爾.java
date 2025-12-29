/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 */
package Client.skills.handler.雷普族;

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
import Config.constants.JobConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleSummon;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 阿戴爾
extends AbstractSkillHandler {
    public 阿戴爾() {
        this.jobs = new MapleJob[]{MapleJob.阿戴爾, MapleJob.阿戴爾1轉, MapleJob.阿戴爾2轉, MapleJob.阿戴爾3轉, MapleJob.阿戴爾4轉};
        for (Field field : Config.constants.skills.阿戴爾.class.getDeclaredFields()) {
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
        int level = chr.getLevel();
        for (int i : ss = new int[]{150021000, 150020079, 150020006, 150021005}) {
            if (level < 200 && i == 150021005 || level < 20 && i == 150020006) continue;
            skil = SkillFactory.getSkill(i);
            int skillLv = 1;
            if (i == 150020006) {
                skillLv = (int)Math.floor(Math.min(level, 200) / 20);
            }
            if (skil == null || chr.getSkillLevel(skil) >= skillLv) continue;
            applier.skillMap.put(i, new SkillEntry(skillLv, skil.getMaxMasterLevel(), -1L));
        }
        for (int f : fixskills = new int[]{151001004}) {
            skil = SkillFactory.getSkill(f);
            if (chr.getJob() < f / 10000 || skil == null || chr.getSkillLevel(skil) > 0 || chr.getMasterLevel(skil) > 0) continue;
            applier.skillMap.put(f, new SkillEntry(0, skil.getMasterLevel() == 0 ? skil.getMaxLevel() : skil.getMasterLevel(), SkillFactory.getDefaultSExpiry(skil)));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 151141501: {
                return 151141500;
            }
            case 151141000: 
            case 151141001: {
                return 151121000;
            }
            case 151141003: {
                return 151101013;
            }
            case 151141002: {
                return 151001001;
            }
            case 400011106: 
            case 400011107: {
                return 400011105;
            }
            case 151101004: 
            case 151101010: {
                return 151101003;
            }
            case 151101008: {
                return 151101006;
            }
            case 151001003: {
                return 151001002;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 150021005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 151121040: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 151101005: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 151101006: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LWCreation, 1);
                return 1;
            }
            case 151101013: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LWWonder, 1);
                return 1;
            }
            case 151001004: {
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.NewFlying, 1);
                return 1;
            }
            case 151100017: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LWSwordGauge, 0);
                return 1;
            }
            case 151101010: {
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.y));
                statups.put(SecondaryStat.LWResonanceBuff, 1);
                return 1;
            }
            case 151111005: {
                statups.put(SecondaryStat.LefWarriorNobility, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 151121001: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Curse, 1);
                return 1;
            }
            case 151100002: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                return 1;
            }
            case 151121004: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.q) * 1000);
                statups.put(SecondaryStat.IndieApplySuperStance, 1);
                statups.put(SecondaryStat.IndieAllHitDamR, -effect.getInfo().get((Object)MapleStatInfo.x).intValue());
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.AntiMagicShell, 1);
                statups.put(SecondaryStat.KeyDownEnable, 1);
                statups.put(SecondaryStat.LWDike, 1);
                return 1;
            }
            case 400011108: {
                effect.getInfo().put(MapleStatInfo.time, 4000);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 400011109: {
                statups.put(SecondaryStat.IndieDamR, effect.getY());
                statups.put(SecondaryStat.LWRestore, 1);
                return 1;
            }
            case 400011136: {
                statups.put(SecondaryStat.DevilishPower, effect.getLevel());
                return 1;
            }
            case 151121042: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 151121041: {
                effect.getInfo().put(MapleStatInfo.time, effect.getZ());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 151101003: 
            case 151101004: 
            case 151101010: {
                int objectId = slea.readInt();
                MapleSummon summon = chr.getMap().getSummonByOid(objectId);
                if (summon != null && summon.getOwnerId() == chr.getId()) {
                    chr.getMap().broadcastMessage(SummonPacket.removeSummon(summon, false));
                    chr.getMap().removeMapObject(summon);
                    chr.removeVisibleMapObjectEx(summon);
                    chr.removeSummon(summon);
                    chr.dispelSkill(summon.getSkillId());
                }
                return 1;
            }
            case 151001001: 
            case 151111003: 
            case 400011108: {
                List oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
                chr.handleAdeleObjectSword(applier.effect, oids.reversed());
                return 1;
            }
            case 151100002: {
                applier.pos = slea.readPos();
                return 1;
            }
            case 151101006: {
                chr.handleAdeleObjectSword(applier.effect, null);
                return 1;
            }
            case 151121041: {
                Rectangle[] rectangles = new Rectangle[applier.ai.skillSpawnInfo.size()];
                for (int i2 = 0; i2 < applier.ai.skillSpawnInfo.size(); ++i2) {
                    rectangles[i2] = applier.effect.calculateBoundingBox2(applier.ai.skillSpawnInfo.get(i2).getRight(), false);
                }
                if (rectangles.length > 0) {
                    c.announce(MaplePacketCreator.UserAreaInfosPrepare(applier.effect.getSourceId(), 0, rectangles));
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 150021000: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 151100002: {
                if (applyto.getSummonCountBySkill(151100002) >= applier.effect.getX()) {
                    applyto.removeSummonBySkillID(applier.effect.getSourceId(), 1);
                }
                applier.cancelEffect = false;
                return 1;
            }
            case 151101006: {
                if (applyto.getBuffStatValueHolder(151101006) != null) {
                    applyto.dispelEffect(151101006);
                    return 0;
                }
                return 1;
            }
            case 151101013: {
                if (applyto.getBuffStatValueHolder(151101013) != null) {
                    applyto.dispelEffect(151101013);
                    return 0;
                }
                return 1;
            }
            case 151121001: {
                if (applyto.getLinkMobObjectID() <= 0) {
                    return 0;
                }
                return 1;
            }
            case 151121042: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(151121042);
                applyto.dispelEffect(152121042);
                applyto.dispelEffect(155121042);
                applyto.dispelEffect(154121042);
                return 1;
            }
            case 400011136: {
                int count = 0;
                for (ForceAtomObject sword : applyto.getForceAtomObjects().values()) {
                    if (sword.SkillId != applyto.getJob()) continue;
                    ++count;
                }
                applier.localstatups.put(SecondaryStat.DevilishPower, count);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.totalDamage > 0L && applier.effect != null && applyto.isAlive() && applier.effect.getSourceId() == 151121001) {
            applyfrom.setLinkMobObjectID(applyto.getObjectId());
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null) {
            MapleStatEffect effect;
            if (applier.effect.getSourceId() == 151101000 || applier.effect.getSourceId() == 151111000 || applier.effect.getSourceId() == 151121000 || applier.effect.getSourceId() == 151121002) {
                if (player.getBuffedValue(SecondaryStat.LWCreation) != null && !player.isSkillCooling(151101007) && (effect = player.getSkillEffect(151101007)) != null) {
                    int jobGrade = JobConstants.getJobGrade(player.getJob());
                    int coolTime = effect.getSubTime();
                    if (jobGrade >= 3) {
                        coolTime -= effect.getS();
                    }
                    if (jobGrade >= 4) {
                        coolTime -= effect.getS();
                    }
                    player.registerSkillCooldown(effect.getSourceId(), coolTime, true);
                    player.handleAdeleObjectSword(effect, Collections.emptyList());
                }
                if (player.getBuffedValue(SecondaryStat.LWWonder) != null && !player.isSkillCooling(151001001) && applier.totalDamage > 0L && (effect = player.getSkillEffect(151001001)) != null) {
                    player.registerSkillCooldown(effect, true);
                    player.handleAdeleObjectSword(effect, Collections.emptyList());
                }
                if (applier.totalDamage > 0L && (effect = player.getSkillEffect(151100017)) != null) {
                    player.handleAdeleCharge(effect.getS());
                }
            }
            if ((effect = player.getSkillEffect(151100002)) != null) {
                boolean ret = false;
                switch (applier.effect.getSourceId()) {
                    case 151101006: {
                        ret = applier.effect.getQ() >= 100 || Randomizer.nextInt(100) < applier.effect.getQ();
                        break;
                    }
                    case 151111003: 
                    case 151121003: 
                    case 400011108: {
                        ret = applier.effect.makeChanceResult(player);
                    }
                }
                if (ret) {
                    effect.applyTo(player);
                }
            }
            if (applier.totalDamage > 0L && applier.effect.getSourceId() == 151111002 && (effect = player.getSkillEffect(151111002)) != null) {
                player.handleAdeleCharge(effect.getY());
            }
        }
        return 1;
    }
}

