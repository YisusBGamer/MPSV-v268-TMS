/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.冒險家.海盜類別;

import Client.MapleCharacter;
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
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.maps.ForceAtomObject;
import Opcode.header.OutHeader;
import Packet.AdelePacket;
import Packet.EffectPacket;
import Packet.MaplePacketCreator;
import Server.channel.handler.AttackMobInfo;
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

public class 拳霸
extends AbstractSkillHandler {
    public 拳霸() {
        this.jobs = new MapleJob[]{MapleJob.打手, MapleJob.格鬥家, MapleJob.拳霸};
        for (Field field : Config.constants.skills.冒險家_技能群組.拳霸.class.getDeclaredFields()) {
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
            case 5141501: 
            case 5141502: 
            case 5141503: 
            case 5141504: 
            case 5141505: {
                return 5141500;
            }
            case 5141000: {
                return 5121007;
            }
            case 5101022: {
                return 5100021;
            }
            case 5100018: 
            case 5101019: 
            case 5101023: {
                return 5101017;
            }
            case 5111021: {
                return 5110016;
            }
            case 5110018: 
            case 5110020: 
            case 5111019: {
                return 5111017;
            }
            case 5120024: 
            case 5120029: 
            case 5121023: 
            case 5121025: {
                return 5120022;
            }
            case 5121027: {
                return 5120026;
            }
            case 5120021: {
                return 5121013;
            }
            case 5121055: {
                return 5121052;
            }
            case 400051003: 
            case 400051004: 
            case 400051005: {
                return 400051002;
            }
            case 400051071: {
                return 400051070;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 5101017: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SeaSerpent, 1);
                return 1;
            }
            case 5111017: {
                statups.put(SecondaryStat.SerpentStone, 2);
                return 1;
            }
            case 5110020: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 5110000: {
                effect.getInfo().put(MapleStatInfo.time, 3000);
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 5120028: {
                effect.getInfo().put(MapleStatInfo.cooltimeMS, 500);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 5121009: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.PartyBooster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 5121010: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.ViperTimeLeap, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 5121054: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 5111002: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 5111007: 
            case 5120012: {
                statups.put(SecondaryStat.Dice, 0);
                return 1;
            }
            case 5121015: {
                statups.put(SecondaryStat.IndiePADR, effect.getInfo().get((Object)MapleStatInfo.indiePadR));
                return 1;
            }
            case 5121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 5121052: {
                effect.setOverTime(true);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 5121055: {
                statups.put(SecondaryStat.UnityOfPower, 0);
                return 1;
            }
            case 400051002: {
                statups.put(SecondaryStat.TransformOverMan, effect.getInfo().get((Object)MapleStatInfo.w));
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.indiePMdR));
                return 1;
            }
            case 400051015: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SerpentScrew, effect.getV());
                return 1;
            }
            case 400051042: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CannonShooter_BFCannonBall, 0);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400051015) {
            if (applier.primary) {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(5121054);
                if (mbsvh != null) {
                    applier.cooldown = applier.cooldown * (100 - (mbsvh.effect == null ? 50 : mbsvh.effect.getY())) / 100;
                }
            } else {
                applier.cooldown = 0;
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 5111017: {
                MapleStatEffect effect = SkillFactory.getSkill(5110020).getEffect(applier.effect.getLevel());
                if (applier.primary) {
                    if (applyto.getBuffedIntValue(SecondaryStat.SerpentStone) < applier.effect.getU() || effect == null) {
                        return 0;
                    }
                    applyto.dispelEffect(SecondaryStat.SerpentStone);
                    effect.applyBuffEffect(applyfrom, applyto, applier.effect.getBuffDuration(applyfrom), false, false, true, null);
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.SerpentStone, Math.min(applier.effect.getU(), applyto.getBuffedIntValue(SecondaryStat.SerpentStone) + 1));
                if (effect != null && applier.localstatups.get(SecondaryStat.SerpentStone) >= applier.effect.getU()) {
                    if ("1".equals(applyto.getOneInfo(1544, String.valueOf(5111017)))) {
                        applyto.dispelEffect(SecondaryStat.SerpentStone);
                        effect.applyBuffEffect(applyfrom, applyto, applier.effect.getBuffDuration(applyfrom), false, false, true, null);
                    }
                    return 0;
                }
                return 1;
            }
            case 5121010: {
                if (applyto.getBuffedValue(SecondaryStat.ViperTimeLeap) != null) {
                    return 0;
                }
                applyto.clearCooldown(true);
                return 1;
            }
            case 5121055: {
                applier.localstatups.put(SecondaryStat.UnityOfPower, Math.min(applier.effect.getU(), applyto.getBuffedIntValue(SecondaryStat.UnityOfPower) + 1));
                return 1;
            }
            case 5111007: {
                int dice = Randomizer.nextInt(6) + 1;
                if (applyto.getSpecialStat().getRemoteDice() > 0) {
                    dice = applyto.getSpecialStat().getRemoteDice();
                    applyto.getSpecialStat().setRemoteDice(-1);
                }
                if (dice == 1) {
                    applyto.reduceSkillCooldown(5111007, 90000);
                }
                applyto.send(MaplePacketCreator.spouseMessage(UserChatMessageType.系統, "幸運骰子 技能發動[" + dice + "]號效果。"));
                applier.localstatups.put(SecondaryStat.Dice, dice);
                applyto.getClient().announce(EffectPacket.showDiceEffect(-1, applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false), false);
                return 1;
            }
            case 5120012: {
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
                boolean seven = applyfrom.getSkillEffect(5120044) != null;
                int prop = 0;
                Object chance = applyfrom.getTempValues().remove("雙倍幸運骰子_再一次機會");
                if (chance instanceof Boolean && ((Boolean)chance).booleanValue()) {
                    prop = 100;
                } else {
                    effect = applyfrom.getSkillEffect(5120045);
                    if (effect != null) {
                        prop = effect.getProp();
                    }
                }
                int[] array = new int[1 + (trueSource == 400051000 ? 1 : 0) + (applier.effect.makeChanceResult(applyto) ? 1 : 0)];
                for (int i2 = 0; i2 < array.length; ++i2) {
                    if (i2 == 0 && remote > 0) {
                        array[i2] = remote;
                        continue;
                    }
                    array[i2] = Randomizer.rand(Randomizer.isSuccess(prop) ? 4 : 1, seven ? 7 : 6);
                    if (array.length != 3 || array[0] != array[1] || array[1] != array[2] || !Randomizer.isSuccess(50)) continue;
                    array[i2] = Randomizer.rand(Randomizer.isSuccess(prop) ? 4 : 1, seven ? 7 : 6);
                }
                int buffId = 0;
                for (i = 0; i < array.length; ++i) {
                    if (array[i] == 1) {
                        applyto.reduceSkillCooldown(5120012, 90000);
                    }
                    buffId += array[i] * (int)Math.pow(10.0, i);
                    if (array[i] <= 0) continue;
                    applyto.send(MaplePacketCreator.spouseMessage(UserChatMessageType.系統, "雙倍幸運骰子 技能發動[" + array[i] + "]號效果。"));
                }
                if ((array.length < 2 || array.length < 3 && trueSource == 400051000) && (effect = applyfrom.getSkillEffect(5120043)) != null && effect.makeChanceResult(applyfrom)) {
                    applyto.cancelSkillCooldown(5120012);
                    applyfrom.getTempValues().put("雙倍幸運骰子_再一次機會", true);
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
            case 5121027: {
                if (applier.primary) {
                    ForceAtomObject obj = new ForceAtomObject(applyto.getSpecialStat().gainForceCounter(), 36, 0, applyto.getId(), 0, applier.effect.getSourceId());
                    obj.EnableDelay = 1080;
                    obj.Idk1 = 30;
                    obj.Expire = 5000;
                    obj.Position = new Point(0, 15);
                    obj.ObjPosition = new Point(applier.pos != null ? applier.pos : applyto.getPosition());
                    applyto.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)applyto.getId(), Collections.singletonList(obj), (int)0), applyto.getPosition());
                }
                return 1;
            }
            case 5121053: {
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
            case 400051002: {
                int value = applyto.getBuffedIntValue(SecondaryStat.TransformOverMan);
                if (applyto.getBuffedValue(SecondaryStat.TransformOverMan) == null) {
                    return 1;
                }
                applier.localstatups.put(SecondaryStat.TransformOverMan, --value);
                if (value <= 0) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 400051003: {
                if (applier.primary) {
                    applyto.getSkillEffect(400051002).applyTo(applyto);
                }
                return 0;
            }
            case 400051015: {
                if (!applier.primary) {
                    return 0;
                }
                return 1;
            }
            case 400051042: {
                int nCount = applyto.getBuffedIntValue(SecondaryStat.CannonShooter_BFCannonBall) + (!applier.att ? 1 : -1);
                if (nCount < 0 || nCount > applier.effect.getY()) {
                    return 0;
                }
                applier.duration = 2100000000;
                applier.localstatups.put(SecondaryStat.CannonShooter_BFCannonBall, nCount);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterRegisterEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400051015) {
            SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.SerpentScrew, applier.effect.getSourceId());
            if (mbsvh != null) {
                mbsvh.AttackBossCount = applier.effect.getQ();
                mbsvh.NormalMobKillCount = applier.effect.getW2();
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        SecondaryStatValueHolder mbsvh;
        MapleStatEffect effect = applyfrom.getSkillEffect(5110000);
        if (effect != null) {
            effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
        }
        if (applier.effect != null && !applyto.isBoss() && !applyto.isAlive() && applier.effect.getSourceId() == 400051015 && (mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.SerpentScrew)) != null) {
            --mbsvh.NormalMobKillCount;
            if (mbsvh.NormalMobKillCount <= 0) {
                mbsvh.NormalMobKillCount = applier.effect.getW2();
                applyfrom.reduceSkillCooldown(400051015, applier.effect.getW());
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (!applier.ai.mobAttackInfo.isEmpty()) {
            SecondaryStatValueHolder mbsvh;
            MapleStatEffect effect;
            if (!player.isSkillCooling(5120028) && (effect = player.getSkillEffect(5120028)) != null) {
                player.registerSkillCooldown(effect, true);
                effect.applyBuffEffect(player, player, effect.getBuffDuration(player), false, false, true, null);
            }
            if (applier.effect != null) {
                List<Pair<Long, Integer>> debuffInfos = null;
                mbsvh = player.getBuffStatValueHolder(SecondaryStat.IndieBuffIcon, 5121052);
                if (mbsvh != null && mbsvh.effect != null) {
                    effect = null;
                    Skill skill = SkillFactory.getSkill(5121055);
                    if (skill != null) {
                        effect = skill.getEffect(mbsvh.effect.getLevel());
                    }
                    if (effect != null) {
                        debuffInfos = (List<Pair<Long, Integer>>) player.getTempValues().getOrDefault("海龍標記Debuff", new LinkedList<>());
                        long now = System.currentTimeMillis();
                        block3: for (AttackMobInfo ami : applier.ai.mobAttackInfo) {
                            for (Pair pair : debuffInfos) {
                                if ((Long)pair.left <= now || (Integer)pair.right != ami.mobId) continue;
                                effect.applyBuffEffect(player, player, effect.getBuffDuration(player), false, false, true, null);
                                continue block3;
                            }
                        }
                    }
                }
                if (applier.effect.getSourceId() == 5121025) {
                    if (debuffInfos == null) {
                        debuffInfos = (List<Pair<Long, Integer>>) player.getTempValues().getOrDefault("海龍標記Debuff", new LinkedList<>());
                    }
                    long endTime = System.currentTimeMillis() + (long)applier.effect.getDuration();
                    for (AttackMobInfo ami : applier.ai.mobAttackInfo) {
                        boolean found = false;
                        for (Pair pair : debuffInfos) {
                            if ((Integer)pair.right != ami.mobId) continue;
                            pair.left = endTime;
                            found = true;
                        }
                        if (found) continue;
                        debuffInfos.add(new Pair<Long, Integer>(endTime, ami.mobId));
                    }
                    拳霸.sendViperMark(player);
                    player.getTempValues().put("海龍標記Debuff", debuffInfos);
                }
            }
            if (applier.effect != null && applier.effect.getSourceId() == 400051015 && (mbsvh = player.getBuffStatValueHolder(SecondaryStat.SerpentScrew)) != null) {
                boolean normal = false;
                for (AttackMobInfo ami : applier.ai.mobAttackInfo) {
                    if (MapleLifeFactory.getMonsterStats(ami.templateID).isBoss()) {
                        --mbsvh.AttackBossCount;
                        if (mbsvh.AttackBossCount > 0) continue;
                        mbsvh.AttackBossCount = applier.effect.getQ();
                        normal = true;
                        continue;
                    }
                    normal = true;
                }
                if (normal) {
                    --mbsvh.value;
                }
                if (mbsvh.value <= 0) {
                    player.dispelEffect(applier.effect.getSourceId());
                }
            }
        }
        if (applier.effect != null) {
            switch (applier.effect.getSourceId()) {
                case 0x4DD5D4: 
                case 5111002: 
                case 5121007: 
                case 5121016: {
                    MapleStatEffect effect;
                    if (player.getBuffStatValueHolder(5110020) != null) {
                        int skillID = 0;
                        if (applier.effect.getSourceId() == 5121007) {
                            if (player.getSkillEffect(5121027) != null) {
                                skillID = 5121027;
                            }
                        } else {
                            skillID = 5111019;
                        }
                        if (skillID > 0) {
                            player.dispelEffect(5110020);
                            ExtraSkill eskill = new ExtraSkill(skillID, applier.ai.skillposition != null ? applier.ai.skillposition : (applier.ai.position != null ? applier.ai.position : player.getPosition()));
                            eskill.TriggerSkillID = applier.effect.getSourceId();
                            eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
                            eskill.Value = 1;
                            player.send(MaplePacketCreator.RegisterExtraSkill(5110018, Collections.singletonList(eskill)));
                        }
                    }
                    if (player.getBuffStatValueHolder(SecondaryStat.SeaSerpent) == null || player.isSkillCooling(5101023)) break;
                    if (applier.effect.getSourceId() == 5121007) {
                        effect = player.getSkillEffect(5121025);
                    } else {
                        effect = player.getSkillEffect(5121023);
                        if (effect == null) {
                            effect = player.getSkillEffect(5111021);
                        }
                        if (effect == null) {
                            effect = player.getSkillEffect(5101019);
                        }
                    }
                    if (effect == null) break;
                    int cooldown = effect.getCooldown();
                    SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(5121054);
                    if (mbsvh != null) {
                        cooldown = cooldown * (100 - (mbsvh.effect == null ? 50 : mbsvh.effect.getY())) / 100;
                    }
                    player.registerSkillCooldown(5101023, cooldown, true);
                    ExtraSkill eskill = new ExtraSkill(effect.getSourceId(), applier.ai.skillposition != null ? applier.ai.skillposition : (applier.ai.position != null ? applier.ai.position : player.getPosition()));
                    eskill.TriggerSkillID = applier.effect.getSourceId();
                    eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
                    eskill.Value = 1;
                    player.send(MaplePacketCreator.RegisterExtraSkill(5101017, Collections.singletonList(eskill)));
                    effect = player.getSkillEffect(5111017);
                    if (effect == null) break;
                    effect.applyBuffEffect(player, player, 2100000000, false, false, true, null);
                }
            }
        }
        return -1;
    }

    public static void sendViperMark(MapleCharacter chr) {
        Object obj = chr.getTempValues().get("海龍標記Debuff");
        if (obj == null) {
            return;
        }
        long now = System.currentTimeMillis();
        List<Pair> debuffInfos = (List)obj;
        Iterator iterator = debuffInfos.iterator();
        while (iterator.hasNext()) {
            Pair pair = (Pair)iterator.next();
            if (now < (Long)pair.getLeft()) continue;
            iterator.remove();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(OutHeader.SnipeStatSet);
        mplew.writeInt(5121025);
        mplew.write(!debuffInfos.isEmpty());
        if (!debuffInfos.isEmpty()) {
            mplew.writeInt(debuffInfos.size());
            for (Pair pair : debuffInfos) {
                mplew.writeInt((Integer)pair.getRight());
                mplew.writeInt(1);
                mplew.writeInt(0);
                mplew.writeInt(Math.max(0L, (Long)pair.left - now));
                mplew.writeInt(0);
            }
        } else {
            chr.getTempValues().remove("海龍標記Debuff");
        }
        chr.send(mplew.getPacket());
    }
}

