/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.弓手類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Opcode.header.OutHeader;
import Packet.EffectPacket;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 神射手
extends AbstractSkillHandler {
    public 神射手() {
        this.jobs = new MapleJob[]{MapleJob.弩弓手, MapleJob.狙擊手, MapleJob.神射手};
        for (Field field : Config.constants.skills.神射手.class.getDeclaredFields()) {
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
            case 3241501: {
                return 3241500;
            }
            case 3241001: 
            case 3241002: {
                return 3241000;
            }
            case 3211020: {
                return 3211019;
            }
            case 3221019: 
            case 3221027: {
                return 3220020;
            }
            case 3211017: {
                return 3210016;
            }
            case 3221022: 
            case 3221023: 
            case 3221024: 
            case 3221025: 
            case 3221026: {
                return 3220021;
            }
            case 400031010: {
                return 400031006;
            }
            case 400031016: {
                return 400031015;
            }
            case 400031026: 
            case 400031027: {
                return 400031025;
            }
            case 400031056: {
                return 400031055;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 3210016: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.EnhancePiercing, 0);
                return 1;
            }
            case 3211005: {
                effect.setDebuffTime(effect.getX() * 1000);
                monsterStatus.put(MonsterStatus.Freeze, 1);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 3211019: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 3210013: {
                statups.put(SecondaryStat.PowerTransferGauge, 0);
                return 1;
            }
            case 3221014: {
                effect.setDebuffTime(effect.getSubTime() * 1000);
                monsterStatus.put(MonsterStatus.Stun, 1);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 3221002: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.SharpEyes, (effect.getX() << 8) + effect.getY());
                return 1;
            }
            case 3221053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 3221022: {
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getX());
                statups.put(SecondaryStat.IndiePMdR, effect.getZ());
                return 1;
            }
            case 3221054: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.indieIgnoreMobpdpR));
                statups.put(SecondaryStat.IgnoreTargetDEF, 0);
                statups.put(SecondaryStat.BullsEye, (effect.getX() << 8) + effect.getY());
                return 1;
            }
            case 400031006: {
                statups.put(SecondaryStat.CursorSniping, effect.getX());
                return 1;
            }
            case 400031015: {
                statups.put(SecondaryStat.SplitArrow, 1);
                return 1;
            }
            case 400031055: {
                statups.put(SecondaryStat.RepeatinCartrige, effect.getX());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 3221014) {
            applier.pos = slea.readPos();
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 3210016: {
                applier.localstatups.put(SecondaryStat.EnhancePiercing, Math.min(applyto.getBuffedIntValue(SecondaryStat.EnhancePiercing) + applier.effect.getX(), applier.effect.getY()));
                return 1;
            }
            case 0x30FF03: {
                LinkedList<SecondaryStatValueHolder> mbsvhs = new LinkedList();
                for (Map.Entry<SecondaryStat, List<SecondaryStatValueHolder>> entry : applyto.getAllEffects().entrySet()) {
                    if (!entry.getKey().isNormalDebuff() && !entry.getKey().isCriticalDebuff()) continue;
                    entry.getValue().stream().filter(mbsvh -> mbsvh.effect instanceof MobSkill).forEach(mbsvhs::add);
                }
                if (mbsvhs.size() > 0) {
                    mbsvhs.forEach(mbsvh -> applyto.cancelEffect(mbsvh.effect, mbsvh.startTime));
                }
                return 1;
            }
            case 3221002: {
                applier.buffz = 0;
                MapleStatEffect effect = applyfrom.getSkillEffect(3220044);
                if (effect != null) {
                    applier.buffz = effect.getIndieIgnoreMobpdpR();
                }
                if ((effect = applyfrom.getSkillEffect(3220045)) != null) {
                    applier.localstatups.put(SecondaryStat.SharpEyes, applier.localstatups.get(SecondaryStat.SharpEyes) + (effect.getX() << 8));
                }
                return 1;
            }
            case 3210013: {
                if (applyto.getBuffedValue(SecondaryStat.PowerTransferGauge) == null) {
                    applyto.send(EffectPacket.showBlessOfDarkness(-1, 3210013));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showBlessOfDarkness(applyto.getId(), 3210013), false);
                }
                applier.localstatups.put(SecondaryStat.PowerTransferGauge, applyto.getStat().getCurrentMaxHP() * applier.effect.getZ() / 100);
                return 1;
            }
            case 3221053: {
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
            case 400031006: {
                int value;
                if (!applier.primary && !applier.att) {
                    applier.localstatups.clear();
                    applier.localstatups.put(SecondaryStat.IndieNotDamaged, 1);
                    return 1;
                }
                SecondaryStatValueHolder mbsvh2 = applyto.getBuffStatValueHolder(SecondaryStat.CursorSniping);
                if (mbsvh2 != null) {
                    value = mbsvh2.value - 1;
                    applier.duration = mbsvh2.getLeftTime();
                } else {
                    value = applier.localstatups.get(SecondaryStat.CursorSniping);
                }
                applier.localstatups.put(SecondaryStat.CursorSniping, value);
                if (value <= 0) {
                    applier.overwrite = false;
                    applier.duration = 0;
                }
                return 1;
            }
            case 400031055: {
                int value;
                SecondaryStatValueHolder mbsvh3 = applyto.getBuffStatValueHolder(SecondaryStat.RepeatinCartrige);
                if (mbsvh3 != null) {
                    mbsvh3.z = value = mbsvh3.z - 1;
                    applier.duration = mbsvh3.getLeftTime();
                } else {
                    value = applier.localstatups.get(SecondaryStat.RepeatinCartrige) * applier.effect.getV();
                }
                applier.buffz = value;
                if ((double)value % (double)applier.effect.getV() != 0.0) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.RepeatinCartrige, value / applier.effect.getV());
                if (value <= 0) {
                    applier.overwrite = false;
                    applier.duration = 0;
                }
                return 1;
            }
            case 400031056: {
                applyfrom.getSkillEffect(400031055).applyBuffEffect(applyfrom, applyto, 0, applier.primary, applier.att, applier.passive, applier.pos);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effect;
        if (applier.totalDamage > 0L && (effect = applyfrom.getSkillEffect(3210013)) != null) {
            effect.unprimaryPassiveApplyTo(applyfrom);
        }
        if ((effect = applyfrom.getSkillEffect(3210001)) != null && Randomizer.isSuccess(effect.getX())) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.LP_MobSpecialEffectBySkill.getValue());
            mplew.writeInt(applyto.getObjectId());
            mplew.writeInt(effect.getSourceId());
            mplew.writeInt(applyfrom.getId());
            mplew.writeShort(0);
            applyfrom.getMap().broadcastMessage(applyfrom, mplew.getPacket(), true);
            applyfrom.addHPMP(effect.getZ(), effect.getZ());
        }
        if ((effect = applyfrom.getSkillEffect(3221025)) != null && (applier.effect.getSourceId() == 3221007 || applier.effect.getSourceId() == 3221025)) {
            Pair<Long, Integer> debuffInfo = (Pair<Long, Integer>) applyfrom.getTempValues().getOrDefault("必殺狙擊Debuff", new Pair(0L, 0));
            if (applier.effect.getSourceId() == 3221025) {
                if (applyto.isAlive()) {
                    debuffInfo.left = System.currentTimeMillis() + (long)effect.getDuration();
                    debuffInfo.right = applyto.getObjectId();
                    applyfrom.getTempValues().put("必殺狙擊Debuff", debuffInfo);
                    神射手.sendSnipeStatSet(applyfrom);
                }
            } else {
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(OutHeader.SnipeExtraAttack);
                mplew.writeInt(3221026);
                mplew.writeInt(0);
                mplew.writeInt(1);
                mplew.writeInt(debuffInfo.getRight());
                mplew.writeInt(761);
                applyfrom.send(mplew.getPacket());
                debuffInfo.left = 0L;
                神射手.sendSnipeStatSet(applyfrom);
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect effect;
        if (applier.ai.skillId != 3201011 && applier.ai.skillId != 3221024 && applier.ai.skillId != 3221027 && applier.ai.skillId != 3221007 && applier.ai.mobAttackInfo.size() > 0 && (effect = player.getSkillEffect(3221022)) != null && effect.getSkill().getSkillList().contains(applier.ai.skillId) && !player.isSkillCooling(3221022)) {
            player.registerSkillCooldown(3221022, 500, true);
            effect.applyBuffEffect(player, player, effect.getBuffDuration(player), false, false, true, null);
        }
        switch (applier.ai.skillId) {
            case 3211017: 
            case 3221024: 
            case 3221025: {
                player.dispelEffect(SecondaryStat.EnhancePiercing);
            }
        }
        if (applier.ai.mobAttackInfo.size() > 0 && (effect = player.getSkillEffect(3210016)) != null && (applier.ai.skillId == 3201011 || applier.ai.skillId == 3221027 || applier.ai.skillId == 3221007 && player.getSkillEffect(3220021) != null)) {
            effect.applyBuffEffect(player, player, effect.getBuffDuration(player), false, false, true, null);
        }
        if (applier.effect != null && applier.effect.getSourceId() == 400031010 && (effect = player.getEffectForBuffStat(SecondaryStat.CursorSniping)) != null) {
            effect.applyBuffEffect(player, player, effect.getBuffDuration(player), false, true, true, null);
        }
        return 1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        if (!applier.overwrite && applier.effect.getSourceId() == 400031006 && applier.localstatups.containsKey(SecondaryStat.CursorSniping)) {
            applier.effect.applyBuffEffect(player, player, 2000, false, false, true, null);
        }
        return -1;
    }

    public static void sendSnipeStatSet(MapleCharacter chr) {
        Object obj = chr.getTempValues().get("必殺狙擊Debuff");
        if (obj == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Pair debuffInfo = (Pair)obj;
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(OutHeader.SnipeStatSet);
        mplew.writeInt(3221025);
        if (now < (Long)debuffInfo.getLeft()) {
            mplew.write(1);
            mplew.writeInt(1);
            mplew.writeInt((Integer)debuffInfo.getRight());
            mplew.writeInt(1);
            mplew.writeInt(0);
            mplew.writeInt(Math.max(0L, (Long)debuffInfo.getLeft() - now));
            mplew.writeInt(783);
        } else {
            mplew.write(0);
            chr.getTempValues().remove("必殺狙擊Debuff");
        }
        chr.send(mplew.getPacket());
    }
}

