/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.海盜類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.enums.UserChatMessageType;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Opcode.header.OutHeader;
import Packet.EffectPacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 重砲指揮官
extends AbstractSkillHandler {
    public 重砲指揮官() {
        this.jobs = new MapleJob[]{MapleJob.砲手, MapleJob.重砲兵, MapleJob.重砲兵隊長, MapleJob.重砲指揮官};
        for (Field field : Config.constants.skills.重砲指揮官.class.getDeclaredFields()) {
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
            case 5341000: {
                return 5321012;
            }
            case 5341501: {
                return 5341500;
            }
            case 5300007: {
                return 5301001;
            }
            case 5310004: {
                return 5311004;
            }
            case 5310011: {
                return 5311010;
            }
            case 5311014: 
            case 5311015: {
                return 5311013;
            }
            case 5320011: {
                return 5321004;
            }
            case 400051025: 
            case 400051026: {
                return 400051024;
            }
            case 400051039: 
            case 400051052: 
            case 400051053: {
                return 400051038;
            }
            case 400051075: 
            case 400051076: 
            case 400051077: {
                return 400051074;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 5011002: {
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.z));
                return 1;
            }
            case 5301003: 
            case 5320008: {
                statups.put(SecondaryStat.IndieMHP, effect.getInfo().get((Object)MapleStatInfo.indieMhp));
                statups.put(SecondaryStat.IndieMMP, effect.getInfo().get((Object)MapleStatInfo.indieMmp));
                statups.put(SecondaryStat.IndieACC, effect.getInfo().get((Object)MapleStatInfo.indieAcc));
                statups.put(SecondaryStat.IndieEVA, effect.getInfo().get((Object)MapleStatInfo.indieEva));
                statups.put(SecondaryStat.IndieJump, effect.getInfo().get((Object)MapleStatInfo.indieJump));
                statups.put(SecondaryStat.IndieSpeed, effect.getInfo().get((Object)MapleStatInfo.indieSpeed));
                statups.put(SecondaryStat.IndieAllStat, effect.getInfo().get((Object)MapleStatInfo.indieAllStat));
                return 1;
            }
            case 5311010: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 5311004: {
                effect.setDebuffTime(effect.getV() * 1000);
                monsterStatus.put(MonsterStatus.Burned, 1);
                monsterStatus.put(MonsterStatus.IndieSlow, 90);
                statups.put(SecondaryStat.IndieCD, 0);
                statups.put(SecondaryStat.Roulette, 0);
                return 1;
            }
            case 5311005: 
            case 5320007: {
                statups.put(SecondaryStat.Dice, 0);
                return 1;
            }
            case 5311013: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CannonShooter_MiniCannonBall, 0);
                return 1;
            }
            case 5321003: {
                effect.setDebuffTime(effect.getSubTime() * 1000);
                monsterStatus.put(MonsterStatus.Stun, 1);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 5321004: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 5321053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 5321054: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieSpeed));
                statups.put(SecondaryStat.BuckShot, 1);
                return 1;
            }
            case 400051024: {
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 400051008: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CannonShooter_BFCannonBall, 0);
                return 1;
            }
            case 400051077: {
                statups.put(SecondaryStat.IndieDamR, effect.getIndieDamR());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 5011007: {
                String statData = chr.getOneInfo(7786, "sw");
                statData = statData == null || statData.equals("0") ? String.valueOf(1) : String.valueOf(0);
                chr.updateOneInfo(7786, "sw", statData, true);
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(OutHeader.CallMonkey);
                mplew.writeInt(chr.getId());
                mplew.write(Integer.parseInt(statData));
                chr.getMap().broadcastMessage(mplew.getPacket());
                return 0;
            }
            case 5321003: 
            case 5321004: 
            case 400051038: {
                slea.readMapleAsciiString();
                slea.skip(4);
                applier.pos = slea.readPos();
                return 1;
            }
            case 400051025: {
                applier.pos = new Point(slea.readPosInt());
                c.announce(MaplePacketCreator.UserCreateAreaDotInfo(c.getPlayer().getMap().getAndAddObjectId(), applier.effect.getSourceId(), applier.effect.calculateBoundingBox(applier.pos, true)));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 1283: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 5311004: {
                int dice = Randomizer.nextInt(4) + 1;
                applyto.getClient().announce(EffectPacket.showDiceEffect(-1, applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false), false);
                if (dice == 2) {
                    applier.localstatups.put(SecondaryStat.IndieCD, applier.effect.getS());
                }
                applier.localstatups.put(SecondaryStat.Roulette, dice);
                return 1;
            }
            case 5311005: {
                int dice = Randomizer.nextInt(6) + 1;
                if (applyto.getSpecialStat().getRemoteDice() > 0) {
                    dice = applyto.getSpecialStat().getRemoteDice();
                    applyto.getSpecialStat().setRemoteDice(-1);
                }
                if (dice == 1) {
                    applyto.reduceSkillCooldown(5311005, 90000);
                }
                applyto.send(MaplePacketCreator.spouseMessage(UserChatMessageType.系統, "幸運骰子 技能發動[" + dice + "]號效果。"));
                applier.localstatups.put(SecondaryStat.Dice, dice);
                applyto.getClient().announce(EffectPacket.showDiceEffect(-1, applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false), false);
                return 1;
            }
            case 5320007: {
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
                        applyto.reduceSkillCooldown(5320007, 90000);
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
            case 5311013: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.CannonShooter_MiniCannonBall);
                int value = (mbsvh == null ? 0 : mbsvh.value) + (applier.passive ? 1 : -1);
                if (!applier.primary || value < 0 || value > applier.effect.getY() || mbsvh != null && applier.passive && System.currentTimeMillis() < mbsvh.startTime + (long)applier.effect.getW() * 1000L) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.CannonShooter_MiniCannonBall, value);
                if (!applier.passive) {
                    ExtraSkill eskill = new ExtraSkill(!applier.att ? 5311014 : 5311015, applier.pos);
                    eskill.FaceLeft = eskill.SkillID == 5311014 ? (applyfrom.isFacingLeft() ? 1 : 0) : -1;
                    eskill.Delay = eskill.SkillID == 5311014 ? 330 : 360;
                    eskill.Value = 1;
                    applyfrom.send(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                }
                return 1;
            }
            case 5321053: {
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
            case 5321054: {
                if (applyto.getBuffStatValueHolder(applier.effect.getSourceId()) != null) {
                    applyto.dispelEffect(applier.effect.getSourceId());
                    return 0;
                }
                return 1;
            }
            case 400051008: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.CannonShooter_BFCannonBall);
                int value = (mbsvh == null ? 0 : mbsvh.value) + (applier.passive ? 1 : -1);
                if (!applier.primary || value < 0 || value > applier.effect.getY() || mbsvh != null && applier.passive && System.currentTimeMillis() < mbsvh.startTime + (long)applier.effect.getQ() * 1000L) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.CannonShooter_BFCannonBall, value);
                return 1;
            }
            case 400051074: {
                applyto.getSpecialStat().setPoolMakerCount(applier.effect.getY());
                applyto.send(MaplePacketCreator.poolMakerInfo(true, applyto.getSpecialStat().getPoolMakerCount(), applier.effect.getCooldown()));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplySummonEffect(MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 5321004: {
                MapleStatEffect effect;
                applier.pos.x += 25;
                Skill skill = SkillFactory.getSkill(5320011);
                if (skill != null && (effect = skill.getEffect(applier.effect.getLevel())) != null) {
                    effect.applySummonEffect(applyto, new Point(applier.pos.x - 90, applier.pos.y), applier.duration, applyto.getSpecialStat().getMaelstromMoboid(), applier.startTime);
                }
                return 1;
            }
            case 400051038: {
                MapleStatEffect effect;
                Skill skill = SkillFactory.getSkill(400051052);
                if (skill != null && (effect = skill.getEffect(applier.effect.getLevel())) != null) {
                    effect.applySummonEffect(applyto, applier.pos, applier.duration, applyto.getSpecialStat().getMaelstromMoboid(), applier.startTime);
                }
                if ((skill = SkillFactory.getSkill(400051053)) != null && (effect = skill.getEffect(applier.effect.getLevel())) != null) {
                    effect.applySummonEffect(applyto, applier.pos, applier.duration, applyto.getSpecialStat().getMaelstromMoboid(), applier.startTime);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyMonsterEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 5311004) {
            int value = applyfrom.getBuffedIntValue(SecondaryStat.Roulette);
            if (value == 3) {
                applier.localmobstatups.remove(MonsterStatus.Burned);
                applier.prop = applier.effect.getW();
            } else if (value == 4) {
                applier.localmobstatups.remove(MonsterStatus.IndieSlow);
                applier.prop = 0;
            } else {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        SecondaryStatValueHolder mbsvh;
        if (applier.effect != null && applier.totalDamage > 0L && (mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.Roulette)) != null && mbsvh.effect != null && mbsvh.value >= 3 && mbsvh.value <= 4) {
            int duration = mbsvh.value == 4 ? mbsvh.effect.getDotTime() : mbsvh.effect.getDebuffTime();
            mbsvh.effect.applyMonsterEffect(applyfrom, applyto, mbsvh.effect.calcMobDebuffDuration(duration, applyfrom));
        }
        if (applier.totalDamage > 0L && applier.effect != null && (mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.CannonShooter_MiniCannonBall)) != null && mbsvh.effect != null && mbsvh.value > 0 && "1".equals(applyfrom.getOneInfo(1544, String.valueOf(5311013))) && !applyfrom.isSkillCooling(mbsvh.effect.getSourceId())) {
            switch (applier.effect.getSourceId()) {
                case 5311014: 
                case 5311015: 
                case 5320011: 
                case 5321003: 
                case 5321004: 
                case 400051038: 
                case 400051039: 
                case 400051052: 
                case 400051053: {
                    break;
                }
                default: {
                    mbsvh.effect.applyTo(applyfrom, applyfrom, mbsvh.effect.getBuffDuration(applyfrom), true, true, false, applyto.getPosition());
                }
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null && applier.ai.skillId == 400051075 && player.isSkillCooling(400051074)) {
            if (player.getSpecialStat().getPoolMakerCount() > 0) {
                player.getSpecialStat().setPoolMakerCount(player.getSpecialStat().getPoolMakerCount() - 1);
                player.send(MaplePacketCreator.poolMakerInfo(player.getSpecialStat().getPoolMakerCount() > 0, player.getSpecialStat().getPoolMakerCount(), player.getCooldownLeftTime(400051074)));
                if (applier.effect != null && applier.effect.getSourceId() == 400051075 && player.getMap().getAffectedAreaObject(player.getId(), 400051076).size() < 2) {
                    player.getSkillEffect(400051076).applyAffectedArea(player, applier.ai.skillposition);
                }
            } else {
                player.getSpecialStat().setPoolMakerCount(0);
                player.send(MaplePacketCreator.poolMakerInfo(false, 0, 0));
            }
        }
        return 1;
    }
}

