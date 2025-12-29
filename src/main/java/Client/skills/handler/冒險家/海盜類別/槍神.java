/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.海盜類別;

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
import Config.constants.enums.UserChatMessageType;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.MapleSummon;
import Packet.EffectPacket;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 槍神
extends AbstractSkillHandler {
    public 槍神() {
        this.jobs = new MapleJob[]{MapleJob.槍手, MapleJob.神槍手, MapleJob.槍神};
        for (Field field : Config.constants.skills.冒險家_技能群組.槍神.class.getDeclaredFields()) {
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
            case 5241501: 
            case 5241502: {
                return 5241500;
            }
            case 5241001: {
                return 5241000;
            }
            case 5201017: {
                return 5201012;
            }
            case 5201005: {
                return 5201011;
            }
            case 5211019: 
            case 5211020: 
            case 5211021: {
                return 5210015;
            }
            case 5221022: 
            case 5221027: 
            case 0x4FAAA4: 
            case 5221029: {
                return 5220019;
            }
            case 5221026: {
                return 5221017;
            }
            case 400051049: 
            case 400051050: {
                return 400051040;
            }
            case 400051081: {
                return 400051073;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 5211007: 
            case 5220014: {
                statups.put(SecondaryStat.Dice, 0);
                return 1;
            }
            case 5201012: 
            case 5210015: 
            case 5211019: 
            case 5221022: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 5221015: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.GuidedBullet, 1);
                return 1;
            }
            case 5221053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 5220020: {
                effect.getInfo().put(MapleStatInfo.prob, 100);
                return 1;
            }
            case 5220055: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.QuickDraw, 1);
                return 1;
            }
            case 5221004: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 5221018: {
                statups.put(SecondaryStat.IndieEVA, effect.getInfo().get((Object)MapleStatInfo.indieEva));
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.indieAsrR));
                statups.put(SecondaryStat.IndieTerR, effect.getInfo().get((Object)MapleStatInfo.indieTerR));
                statups.put(SecondaryStat.IndiePADR, effect.getInfo().get((Object)MapleStatInfo.indiePadR));
                statups.put(SecondaryStat.DamR, 0);
                statups.put(SecondaryStat.TerR, 0);
                return 1;
            }
            case 5220012: {
                effect.getInfo().put(MapleStatInfo.cooltimeMS, 500);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 5220019: {
                effect.getInfo().put(MapleStatInfo.time, 120000);
                statups.put(SecondaryStat.SpiritLink, 0);
                return 1;
            }
            case 5221054: {
                statups.put(SecondaryStat.IgnoreMobDamR, effect.getW());
                statups.put(SecondaryStat.UnwearyingRun, effect.getW());
                return 1;
            }
            case 400051006: {
                statups.put(SecondaryStat.BulletParty, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 5210015: {
                slea.readPos();
                slea.skip(3);
                slea.readPosInt();
                applier.pos = slea.readPosInt();
                return 1;
            }
            case 5221022: {
                int value = (Integer)chr.getTempValues().getOrDefault("海盜砲擊艇", 0);
                if (value == 0) {
                    applier.effect.applyTo(chr, chr.getPosition(), true);
                    chr.getTempValues().put("海盜砲擊艇", 1);
                } else {
                    MapleStatEffect effect;
                    Skill skill = SkillFactory.getSkill(5221027);
                    if (skill != null && (effect = skill.getEffect(applier.effect.getLevel())) != null) {
                        effect.applyTo(chr, chr.getPosition(), true);
                    }
                    chr.getTempValues().put("海盜砲擊艇", 0);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 5221013: {
                if (applyfrom.getSkillEffect(400051040) != null && applyfrom.getCooldownLeftTime(400051040) < 8000) {
                    applyfrom.registerSkillCooldown(400051040, 8000, true);
                }
                return 1;
            }
            case 400051073: {
                if (applier.att || !applier.passive) {
                    applier.cooldown = 0;
                }
                return 1;
            }
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect instanceof MobSkill) {
            boolean isDebuff = false;
            for (SecondaryStat secondaryStat : applier.localstatups.keySet()) {
                if (!secondaryStat.isNormalDebuff() && !secondaryStat.isCriticalDebuff()) continue;
                isDebuff = true;
                break;
            }
            if (isDebuff) {
                List<MapleSummon> sums = applyto.getSummonsReadLock();
                try {
                    for (MapleSummon sum : sums) {
                        if (sum.getSkillId() != 5210015 && sum.getSkillId() != 5211019 || sum.isResist()) continue;
                        sum.setResist(true);
                        applyto.getMap().broadcastMessage(SummonPacket.summonedSetAbleResist(sum.getOwnerId(), sum.getObjectId(), (byte)0));
                        int n = 0;
                        return n;
                    }
                }
                finally {
                    applyto.unlockSummonsReadLock();
                }
            }
            return -1;
        }
        switch (applier.effect.getSourceId()) {
            case 5211007: {
                int dice = Randomizer.nextInt(6) + 1;
                if (applyto.getSpecialStat().getRemoteDice() > 0) {
                    dice = applyto.getSpecialStat().getRemoteDice();
                    applyto.getSpecialStat().setRemoteDice(-1);
                }
                if (dice == 1) {
                    applyto.reduceSkillCooldown(5211007, 90000);
                }
                applyto.send(MaplePacketCreator.spouseMessage(UserChatMessageType.系統, "幸運骰子 技能發動[" + dice + "]號效果。"));
                applier.localstatups.put(SecondaryStat.Dice, dice);
                applyto.getClient().announce(EffectPacket.showDiceEffect(-1, applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), applier.effect.getSourceId(), applier.effect.getLevel(), dice, -1, false), false);
                return 1;
            }
            case 5220014: {
                int i = 0;
                int n = 0;
                int remote = 0;
                int trueSource = applier.effect.getSourceId();
                int n2 = applier.effect.getLevel();
                MapleStatEffect effect = applyfrom.getSkillEffect(400051000);
                if (effect != null) {
                    remote = applyfrom.getBuffedIntValue(SecondaryStat.LoadedDice);
                    trueSource = effect.getSourceId();
                    n = effect.getLevel();
                }
                boolean seven = applyfrom.getSkillEffect(5220044) != null;
                int prop = 0;
                Object chance = applyfrom.getTempValues().remove("雙倍幸運骰子_再一次機會");
                if (chance instanceof Boolean && ((Boolean)chance).booleanValue()) {
                    prop = 100;
                } else {
                    effect = applyfrom.getSkillEffect(5220045);
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
                        applyto.reduceSkillCooldown(5220014, 90000);
                    }
                    buffId += array[i] * (int)Math.pow(10.0, i);
                    if (array[i] <= 0) continue;
                    applyto.send(MaplePacketCreator.spouseMessage(UserChatMessageType.系統, "雙倍幸運骰子 技能發動[" + array[i] + "]號效果。"));
                }
                if ((array.length < 2 || array.length < 3 && trueSource == 400051000) && (effect = applyfrom.getSkillEffect(5220043)) != null && effect.makeChanceResult(applyfrom)) {
                    applyto.cancelSkillCooldown(5220014);
                    applyfrom.getTempValues().put("雙倍幸運骰子_再一次機會", true);
                }
                if (trueSource == 400051000) {
                    applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, n, -1, 1, false));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, n, -1, 1, false), false);
                    for (i = 0; i < array.length; ++i) {
                        if (array[i] <= 0) continue;
                        applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, n, array[i], i == array.length - 1 ? 0 : -1, i != 0));
                        applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, n, array[i], i == array.length - 1 ? 0 : -1, i != 0), false);
                    }
                    applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, n, -1, 2, false));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, n, -1, 2, false), false);
                } else {
                    applyto.getClient().announce(EffectPacket.showDiceEffect(-1, trueSource, n, buffId, -1, false));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), trueSource, n, buffId, -1, false), false);
                }
                applier.localstatups.put(SecondaryStat.Dice, buffId);
                return 1;
            }
            case 5221015: 
            case 5221022: 
            case 5221027: {
                if (!applier.passive) {
                    return 0;
                }
                return 1;
            }
            case 5210015: {
                applyto.dispelEffect(5201012);
                return 1;
            }
            case 5220019: {
                Object value = applyfrom.getTempValues().remove("船員指令");
                if (!(value instanceof Integer)) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.SpiritLink, (int)((Integer)value));
                applier.cancelEffect = false;
                return 1;
            }
            case 5221053: {
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
            case 5221054: {
                applyto.addHPMP(applier.effect.getZ(), 0);
                return 1;
            }
            case 400051006: {
                if (applier.att) {
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplySummonEffect(MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 5210015: {
                MapleStatEffect effect;
                Skill skill = SkillFactory.getSkill(5211019);
                if (skill != null && (effect = skill.getEffect(applier.effect.getLevel())) != null) {
                    effect.applySummonEffect(applyto, new Point(applier.pos.x + 170, applier.pos.y), applier.duration, applyto.getSpecialStat().getMaelstromMoboid(), applier.startTime);
                }
                if ((effect = applyto.getSkillEffect(5220019)) != null) {
                    applyto.getTempValues().put("船員指令", applier.effect.getSourceId());
                    effect.applyBuffEffect(applyto, applyto, effect.getBuffDuration(applyto), false, false, false, null);
                }
                return 1;
            }
            case 5211019: {
                MapleStatEffect effect = applyto.getSkillEffect(5220019);
                if (effect != null) {
                    applyto.getTempValues().put("船員指令", applier.effect.getSourceId());
                    effect.applyBuffEffect(applyto, applyto, effect.getBuffDuration(applyto), false, false, false, null);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.totalDamage > 0L && applier.effect != null && applyto.isAlive() && applier.effect.getSourceId() == 5221015) {
            MapleStatEffect effect;
            applyfrom.setLinkMobObjectID(applyto.getObjectId());
            applier.effect.unprimaryPassiveApplyTo(applyfrom);
            if ("1".equals(applyfrom.getOneInfo(1544, String.valueOf(5221029))) && (effect = applyfrom.getSkillEffect(5221029)) != null && !applyfrom.isSkillCooling(5221029)) {
                effect.applyTo(applyfrom, applyto.getPosition());
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (!applier.ai.mobAttackInfo.isEmpty()) {
            MapleStatEffect effect;
            if (!player.isSkillCooling(5220012) && (effect = player.getSkillEffect(5220012)) != null) {
                player.registerSkillCooldown(effect, true);
                effect.applyBuffEffect(player, player, effect.getBuffDuration(player), false, false, true, null);
            }
            if (player.getBuffedValue(SecondaryStat.QuickDraw) == null) {
                effect = player.getSkillEffect(5220055);
                if (effect != null && effect.makeChanceResult(player)) {
                    effect.unprimaryPassiveApplyTo(player);
                }
            } else if (applier.effect != null) {
                switch (applier.effect.getSourceId()) {
                    case 5221013: 
                    case 5221016: 
                    case 5221052: 
                    case 400051021: {
                        player.dispelEffect(SecondaryStat.QuickDraw);
                    }
                }
            }
            if (applier.ai.skillId == 400051073 || applier.ai.skillId == 400051081) {
                ExtraSkill eskill = new ExtraSkill(400051081, applier.ai.pos);
                eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
                eskill.TriggerSkillID = applier.ai.unInt1;
                eskill.Value = 1;
                player.send(MaplePacketCreator.RegisterExtraSkill(applier.ai.skillId, Collections.singletonList(eskill)));
            }
        }
        return 1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 5210015) {
            player.dispelEffect(5220019);
        }
        return -1;
    }
}

