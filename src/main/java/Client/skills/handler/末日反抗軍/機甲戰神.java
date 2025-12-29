/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.末日反抗軍;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.force.MapleForceFactory;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.enums.UserChatMessageType;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 機甲戰神
extends AbstractSkillHandler {
    public 機甲戰神() {
        this.jobs = new MapleJob[]{MapleJob.機甲戰神1轉, MapleJob.機甲戰神2轉, MapleJob.機甲戰神3轉, MapleJob.機甲戰神4轉};
        for (Field field : Config.constants.skills.機甲戰神.class.getDeclaredFields()) {
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
        int[] ss;
        super.baseSkills(chr, applier);
        for (int i : ss = new int[]{30001068, 30000227}) {
            Skill skil = SkillFactory.getSkill(i);
            if (skil == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 35141501: 
            case 35141503: {
                return 35141500;
            }
            case 35141000: 
            case 35141001: 
            case 35141002: {
                return 35121015;
            }
            case 500004120: {
                return 400051009;
            }
            case 500004121: {
                return 400051017;
            }
            case 500004122: {
                return 400051041;
            }
            case 500004123: {
                return 400051068;
            }
            case 30001078: 
            case 30001079: {
                return 30001068;
            }
            case 35111007: {
                return 35111006;
            }
            case 35121016: 
            case 35121019: {
                return 35121015;
            }
            case 35121011: {
                return 35121009;
            }
            case 400051012: {
                return 400051009;
            }
            case 400051051: {
                return 400051041;
            }
            case 400051069: {
                return 400051068;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 35001002: 
            case 35120000: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Mechanic, effect.getLevel());
                statups.put(SecondaryStat.IndieBooster, 1);
                statups.put(SecondaryStat.IndieSpeed, effect.getInfo().get((Object)MapleStatInfo.indieSpeed));
                return 1;
            }
            case 35111003: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Mechanic, effect.getLevel());
                statups.put(SecondaryStat.CriticalBuff, effect.getInfo().get((Object)MapleStatInfo.cr));
                return 1;
            }
            case 30000227: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.HiddenPieceOn, 1);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                statups.put(SecondaryStat.IndieMMPR, effect.getInfo().get((Object)MapleStatInfo.indieMmpR));
                return 1;
            }
            case 35101006: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 35101007: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Guard, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 35111008: 
            case 35120002: {
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 35111002: 
            case 35111015: {
                monsterStatus.put(MonsterStatus.Seal, 1);
                return 1;
            }
            case 35111013: 
            case 35120014: {
                statups.put(SecondaryStat.Dice, 0);
                return 1;
            }
            case 35121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400051009: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 400051017: {
                effect.getInfo().put(MapleStatInfo.time, 2000);
                return 1;
            }
            case 35121055: {
                statups.put(SecondaryStat.BombTime, 1);
                return 1;
            }
            case 400051041: {
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                statups.put(SecondaryStat.IndieIgnorePCounter, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 35101002: 
            case 35110017: {
                List<Integer> oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, oids)), true);
                return 1;
            }
            case 35111002: {
                if (chr.getSummonCountBySkill(35111002) < 2) {
                    applier.effect.applyBuffEffect(chr, applier.effect.getSummonDuration(chr), false);
                    return 0;
                }
                return 1;
            }
            case 400051017: {
                ArrayList<Integer> oids = new ArrayList<Integer>();
                for (MapleMapObject mapleMapObject : chr.getMap().getMapObjectsInRange(chr.getPosition(), 775.0, Collections.singletonList(MapleMapObjectType.MONSTER))) {
                    oids.add(mapleMapObject.getObjectId());
                    if (oids.size() < applier.effect.getMobCount()) continue;
                    return 1;
                }
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, oids)), true);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 35111013: {
                int dice = Randomizer.nextInt(6) + 1;
                if (applyto.getSpecialStat().getRemoteDice() > 0) {
                    dice = applyto.getSpecialStat().getRemoteDice();
                    applyto.getSpecialStat().setRemoteDice(-1);
                }
                if (dice == 1) {
                    applyto.reduceSkillCooldown(35111013, 90000);
                }
                applyto.send(MaplePacketCreator.spouseMessage(UserChatMessageType.系統, "幸運骰子 技能發動[" + dice + "]號效果。"));
                applier.localstatups.put(SecondaryStat.Dice, dice);
                applyto.getClient().announce(EffectPacket.showDiceEffect(-1, applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false), false);
                return 1;
            }
            case 35120014: {
                int i;
                int remote = 0;
                int trueSource = applier.effect.getSourceId();
                int trueLevel = applier.effect.getLevel();
                MapleStatEffect effect = applyfrom.getSkillEffect(400051000);
                if (effect != null) {
                    remote = applyfrom.getBuffedIntValue(SecondaryStat.LoadedDice);
                    trueSource = effect.getSourceId();
                    trueLevel = effect.getLevel();
                }
                int[] array = new int[1 + (trueSource == 400051000 ? 1 : 0) + (applier.effect.makeChanceResult(applyto) ? 1 : 0)];
                for (int i2 = 0; i2 < array.length; ++i2) {
                    if (i2 == 0 && remote > 0) {
                        array[i2] = remote;
                        continue;
                    }
                    array[i2] = Randomizer.rand(1, 6);
                    if (array.length != 3 || array[0] != array[1] || array[1] != array[2] || !Randomizer.isSuccess(50)) continue;
                    array[i2] = Randomizer.rand(1, 6);
                }
                int buffId = 0;
                for (i = 0; i < array.length; ++i) {
                    if (array[i] == 1) {
                        applyto.reduceSkillCooldown(35120014, 90000);
                    }
                    buffId += array[i] * (int)Math.pow(10.0, i);
                    if (array[i] <= 0) continue;
                    applyto.send(MaplePacketCreator.spouseMessage(UserChatMessageType.系統, "雙倍幸運骰子 技能發動[" + array[i] + "]號效果。"));
                }
                if (trueSource == 400051000) {
                    applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, trueLevel, -1, 1, false));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, trueLevel, -1, 1, false), false);
                    for (i = 0; i < array.length; ++i) {
                        if (array[i] <= 0) continue;
                        applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, trueLevel, array[i], i == array.length - 1 ? 0 : -1, i != 0));
                        applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, trueLevel, array[i], i == array.length - 1 ? 0 : -1, i != 0), false);
                    }
                    applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, trueLevel, -1, 2, false));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, trueLevel, -1, 2, false), false);
                } else {
                    applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, trueLevel, buffId, -1, false));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, trueLevel, buffId, -1, false), false);
                }
                applier.localstatups.put(SecondaryStat.Dice, buffId);
                return 1;
            }
            case 35121053: {
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
            case 35120002: {
                applier.localstatups.remove(SecondaryStat.IndiePMdR);
                if (applier.passive) {
                    applier.localstatups.clear();
                    applier.maskedDuration = applier.duration;
                    applier.duration = 0;
                    applier.maskedstatups.put(SecondaryStat.IndiePMdR, applier.effect.getZ());
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null && applier.effect.getSourceId() == 35121052 && !applier.passive) {
            applier.effect.applyTo(player);
        }
        return 1;
    }
}

