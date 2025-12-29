/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.client.party.PartyMember
 */
package Client.skills.handler.冒險家.劍士類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.skills.ExtraSkill;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Opcode.header.OutHeader;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Server.channel.handler.AttackMobInfo;
import SwordieX.client.party.PartyMember;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 聖騎士
extends AbstractSkillHandler {
    public 聖騎士() {
        this.jobs = new MapleJob[]{MapleJob.見習騎士, MapleJob.騎士, MapleJob.聖騎士};
        for (Field field : Config.constants.skills.冒險家_技能群組.type_劍士.聖騎士.class.getDeclaredFields()) {
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
            case 1241000: {
                return 1221009;
            }
            case 1221020: 
            case 1221021: {
                return 1221019;
            }
            case 1221023: {
                return 1220022;
            }
            case 1221055: {
                return 1221054;
            }
            case 400011053: {
                return 400011052;
            }
            case 400011132: {
                return 400011131;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 1201015: {
                effect.setDebuffTime(effect.getY() * 1000);
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 1201013: {
                effect.setDebuffTime(effect.getY() * 1000);
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 1200014: 
            case 1220010: {
                statups.put(SecondaryStat.ElementalCharge, 1);
                return 1;
            }
            case 1211010: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.x).intValue() / 100.0);
                statups.put(SecondaryStat.Restoration, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 1211013: {
                monsterStatus.put(MonsterStatus.IndiePDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.IndieMDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.PAD, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.MAD, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.Blind, effect.getInfo().get((Object)MapleStatInfo.z));
                return 1;
            }
            case 1211014: {
                statups.clear();
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.KnightsAura, effect.getLevel());
                return 1;
            }
            case 1211011: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.CombatOrders, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 1210001: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 1210016: {
                statups.clear();
                statups.put(SecondaryStat.BlessingArmor, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.BlessingArmorIncPAD, effect.getInfo().get((Object)MapleStatInfo.epad));
                return 1;
            }
            case 1221014: {
                monsterStatus.put(MonsterStatus.MagicCrash, 1);
                break;
            }
            case 1221015: {
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.indiePMdR));
                return 1;
            }
            case 1221016: {
                statups.clear();
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 1221052: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                monsterStatus.put(MonsterStatus.Smite, 1);
                return 1;
            }
            case 1221053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 1221054: {
                statups.clear();
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                statups.put(SecondaryStat.IndieIgnorePCounter, 1);
                return 1;
            }
            case 400011003: {
                statups.put(SecondaryStat.PairingUser, 1);
                return 1;
            }
            case 400011052: {
                effect.getInfo().put(MapleStatInfo.time, effect.getV() * 1000);
                statups.put(SecondaryStat.BlessedHammer, 1);
                return 1;
            }
            case 400011053: {
                statups.put(SecondaryStat.BlessedHammerActive, effect.getLevel());
                return 1;
            }
            case 400011072: {
                statups.put(SecondaryStat.IndieApplySuperStance, 1);
                statups.put(SecondaryStat.IndieAllHitDamR, -effect.getInfo().get((Object)MapleStatInfo.x).intValue());
                statups.put(SecondaryStat.GrandCross, 1);
                statups.put(SecondaryStat.ImmuneStun, 1);
                return 1;
            }
            case 400011131: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CannonShooter_BFCannonBall, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 1221016: {
                MapleCharacter effChr = null;
                for (MapleCharacter ochr : chr.getMap().getCharactersInRect(applier.effect.calculateBoundingBox(chr.getOldPosition(), chr.isFacingLeft()))) {
                    if (ochr.getParty() == null || ochr.getParty().getId() != chr.getParty().getId() || ochr.isAlive() || ochr.getId() == chr.getId() || effChr != null && !(ochr.getPosition().distance(chr.getPosition()) < effChr.getPosition().distance(chr.getPosition()))) continue;
                    effChr = ochr;
                }
                if (effChr == null) {
                    return 0;
                }
                effChr.heal();
                applier.effect.applyTo(chr, effChr, true, null, applier.effect.getDuration());
                return 1;
            }
            case 400011131: {
                if (chr.getBuffedIntValue(SecondaryStat.CannonShooter_BFCannonBall) > 0) {
                    LinkedList<Integer> toMobOid = new LinkedList<Integer>();
                    int nCount = slea.readByte();
                    for (int i = 0; i < nCount; ++i) {
                        toMobOid.add(slea.readInt());
                    }
                    MapleForceAtom force = MapleForceFactory.getInstance().getMapleForce(chr, applier.effect, 0, toMobOid);
                    chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(force), true);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 1201015: 
            case 1211018: 
            case 1221009: 
            case 1221019: {
                int value = applyto.getBuffedIntValue(SecondaryStat.ElementalCharge);
                int skillID = applyto.getSkillLevel(1220010) > 0 ? 1220010 : 1200014;
                MapleStatEffect eff = applyto.getEffectForBuffStat(SecondaryStat.ElementalCharge);
                if (eff == null) {
                    eff = applyto.getSkillEffect(1220010);
                    if (eff == null) {
                        eff = applyto.getSkillEffect(1200014);
                    }
                } else if (value < eff.getX() * eff.getZ()) {
                    value = Math.min(value + eff.getX(), eff.getX() * eff.getZ());
                    applyto.setBuffStatValue(SecondaryStat.ElementalCharge, skillID, value);
                }
                if (value <= eff.getX() * eff.getZ()) {
                    eff.applyBuffEffect(applyto, applyto, eff.getBuffDuration(applyto), false, false, true, null);
                    eff = applyto.getSkillEffect(400011052);
                    if (eff != null) {
                        eff.applyBuffEffect(applyto, applyto, 2100000000, true, false, true, null);
                    }
                }
                return 1;
            }
            case 1211010: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.Restoration);
                if (mbsvh != null) {
                    applier.localstatups.put(SecondaryStat.Restoration, Math.min(mbsvh.value + applier.effect.getY(), applier.effect.getY() * 5));
                }
                applyto.addHPMP((int)((double)Math.max(applier.effect.getX() - (mbsvh == null ? 0 : mbsvh.value), 10) / 100.0 * (double)applyto.getStat().getCurrentMaxHP()), 0, false);
                return 1;
            }
            case 1200014: 
            case 1220010: {
                if (applyto.getBuffedValue(SecondaryStat.ElementalCharge) != null) {
                    applier.localstatups.put(SecondaryStat.ElementalCharge, applyto.getBuffedValue(SecondaryStat.ElementalCharge));
                } else {
                    applier.localstatups.put(SecondaryStat.ElementalCharge, applier.effect.getX());
                }
                return 1;
            }
            case 1221016: {
                if (applyfrom == applyto) {
                    return 0;
                }
                return 1;
            }
            case 1221053: {
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
            case 400011003: {
                if (applyfrom.getParty() == null) {
                    return 1;
                }
                if (applyto != applyfrom) {
                    applier.localstatups.put(SecondaryStat.PairingUser, applyfrom.getId());
                    return 1;
                }
                for (PartyMember a3 : applyfrom.getParty().getMembers()) {
                    if (a3.getChr() == null || a3.getFieldID() != applyfrom.getMapId() || a3.getChannel() != applyfrom.getClient().getChannel() || a3.getCharID() == applyfrom.getId() || !applier.effect.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft()).contains(a3.getChr().getPosition())) continue;
                    applier.effect.applyTo(applyfrom, a3.getChr(), applier.duration, applier.primary, false, applier.passive, applyfrom.getPosition());
                    applier.localstatups.put(SecondaryStat.PairingUser, a3.getCharID());
                    break;
                }
                return 1;
            }
            case 400011052: {
                if (!applier.primary) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.BlessedHammer, Math.min(5, applyto.getBuffedIntValue(SecondaryStat.BlessedHammer) + 1));
                return 1;
            }
            case 400011053: {
                if (!applier.primary) {
                    return 0;
                }
                return 1;
            }
            case 400011131: {
                if (!applier.primary) {
                    return 0;
                }
                Integer value = applyfrom.getBuffedValue(SecondaryStat.CannonShooter_BFCannonBall);
                if (value != null) {
                    applier.localstatups.put(SecondaryStat.CannonShooter_BFCannonBall, Math.max(0, value - 1));
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAttack(MapleCharacter player, MapleMonster monster, SkillClassApplier applier) {
        if (applier.theSkill.getId() == 400011131) {
            ExtraSkill eskill = new ExtraSkill(400011132, monster.getPosition());
            eskill.FaceLeft = -1;
            eskill.Value = 1;
            player.send(MaplePacketCreator.RegisterExtraSkill(400011131, Collections.singletonList(eskill)));
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.ai.skillId == 400011131) {
            applier.passive = true;
        }
        if (applier.ai.skillId == 1221019) {
            ExtraSkill eskill = new ExtraSkill(1221020, applier.ai.position);
            eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
            eskill.Delay = 180;
            eskill.Value = 1;
            player.send(MaplePacketCreator.RegisterExtraSkill(applier.ai.skillId, Collections.singletonList(eskill)));
        }
        MapleStatEffect effect = player.getSkillEffect(1220022);
        if (applier.ai.skillId == 1221009 && effect != null) {
            MaplePacketLittleEndianWriter mplew;
            Pair object;
            Map<Integer, Pair<Long, Integer>>  divineJudgmentInfos = (Map)player.getTempValues().computeIfAbsent("神聖審判計數", k -> new LinkedHashMap());
            LinkedList<Integer> attacks = new LinkedList<Integer>();
            long time = System.currentTimeMillis();
            int duration = effect.getMobDebuffDuration(player);
            for (AttackMobInfo ami : applier.ai.mobAttackInfo) {
                if (!divineJudgmentInfos.containsKey(ami.mobId) || (Long)((Pair)divineJudgmentInfos.get(ami.mobId)).getLeft() <= time) {
                    divineJudgmentInfos.put(ami.mobId, new Pair<Long, Integer>(time + (long)duration, 1));
                    continue;
                }
                ((Pair)divineJudgmentInfos.get((Object)Integer.valueOf((int)ami.mobId))).left = time + (long)duration;
                if ((Integer)((Pair)divineJudgmentInfos.get((Object)Integer.valueOf((int)ami.mobId))).right + 1 >= effect.getX()) {
                    attacks.add(ami.mobId);
                    ((Pair)divineJudgmentInfos.get((Object)Integer.valueOf((int)ami.mobId))).right = 0;
                    continue;
                }
                object = (Pair)divineJudgmentInfos.get(ami.mobId);
                Integer.valueOf((Integer)((Pair)object).right + 1);
                ((Pair)object).right = ((Pair)object).right;
            }
            Iterator<Map.Entry<Integer, Pair<Long, Integer>>> iterator = divineJudgmentInfos.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                if ((Long)((Pair)entry.getValue()).getLeft() > time && player.getMap().getMobObject((Integer)entry.getKey()) != null) continue;
                iterator.remove();
            }
            if (attacks.size() > 0) {
                mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.DivineJudgmentAttack.getValue());
                mplew.writeInt(1221023);
                mplew.writeInt(5);
                mplew.writeInt(0);
                mplew.writeInt(attacks.size());
                for (int oid : attacks) {
                    mplew.writeInt(oid);
                    mplew.writeInt(342);
                }
                player.send(mplew.getPacket());
            }
            mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.DivineJudgmentStatSet.getValue());
            mplew.write(true);
            mplew.writeInt(divineJudgmentInfos.size());
            for (int oid : divineJudgmentInfos.keySet()) {
                mplew.writeInt(oid);
            }
            mplew.writeInt(divineJudgmentInfos.size());
            for (Map.Entry entry : divineJudgmentInfos.entrySet()) {
                mplew.writeInt((Integer)entry.getKey());
                mplew.writeInt((Integer)((Pair)entry.getValue()).getRight());
                mplew.writeInt(time - ((Long)((Pair)entry.getValue()).getLeft() - (long)duration));
                mplew.writeInt(duration);
            }
            player.send(mplew.getPacket());
            iterator = divineJudgmentInfos.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                if ((Integer)((Pair)entry.getValue()).getRight() > 0) continue;
                iterator.remove();
            }
        }
        return 1;
    }
}

