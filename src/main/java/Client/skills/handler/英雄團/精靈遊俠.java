/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.英雄團;

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
import Config.constants.GameConstants;
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.maps.ForceAtomObject;
import Packet.AdelePacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 精靈遊俠
extends AbstractSkillHandler {
    public 精靈遊俠() {
        this.jobs = new MapleJob[]{MapleJob.精靈遊俠, MapleJob.精靈遊俠1轉, MapleJob.精靈遊俠2轉, MapleJob.精靈遊俠3轉, MapleJob.精靈遊俠4轉};
        for (Field field : Config.constants.skills.精靈遊俠.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{20020111, 20020112, 20020109, 20021005}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 20021005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 23141501: 
            case 23141502: 
            case 23141503: {
                return 23141500;
            }
            case 23141000: 
            case 23141001: 
            case 23141002: {
                return 23121000;
            }
            case 500004088: {
                return 400031007;
            }
            case 500004089: {
                return 400031017;
            }
            case 500004090: {
                return 400031024;
            }
            case 500004091: {
                return 400031044;
            }
            case 23001005: {
                return 23001002;
            }
            case 23101007: {
                return 23101001;
            }
            case 23111009: 
            case 23111010: {
                return 23111008;
            }
            case 400031008: 
            case 400031009: 
            case 400031011: {
                return 400031007;
            }
            case 23121015: {
                return 23121014;
            }
            case 400031018: 
            case 400031019: {
                return 400031017;
            }
            case 400031045: {
                return 400031044;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 20021005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 20021110: 
            case 80001040: {
                effect.setMoveTo(effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 23111002: {
                monsterStatus.put(MonsterStatus.TotalDamParty, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 23110004: {
                statups.put(SecondaryStat.AddAttackCount, 0);
                return 1;
            }
            case 23121004: {
                statups.put(SecondaryStat.EMHP, effect.getInfo().get((Object)MapleStatInfo.emhp));
                statups.put(SecondaryStat.DamR, effect.getInfo().get((Object)MapleStatInfo.damR));
                statups.put(SecondaryStat.IndiePADR, effect.getInfo().get((Object)MapleStatInfo.indiePadR));
                return 1;
            }
            case 23111005: {
                statups.put(SecondaryStat.DamAbsorbShield, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.TerR, effect.getInfo().get((Object)MapleStatInfo.terR));
                statups.put(SecondaryStat.AsrR, effect.getInfo().get((Object)MapleStatInfo.terR));
                return 1;
            }
            case 23111008: {
                effect.setDebuffTime(effect.getSubTime() * 1000);
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 23111009: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 23121002: {
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 23121003: {
                monsterStatus.put(MonsterStatus.DodgeBodyAttack, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.AddDamSkill, 1);
                return 1;
            }
            case 23121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 23121054: {
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                return 1;
            }
            case 400031007: {
                statups.put(SecondaryStat.TempSecondaryStat, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400031017: {
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.IndiePADR, effect.getInfo().get((Object)MapleStatInfo.indiePadR));
                return 1;
            }
            case 400031044: {
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                statups.put(SecondaryStat.MercedesRoyalKnight, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 23111008) {
            if (IntStream.of(23111008, 23111009, 23111010).filter(k -> chr.getSummonBySkillID(k) != null).count() >= 2L) {
                c.announce(MaplePacketCreator.sendSkillUseResult(false, 0));
                return 0;
            }
            int n4 = 23111008 + Randomizer.nextInt(3);
            if (n4 != applier.effect.getSourceId()) {
                applier.effect = chr.getSkillEffect(n4);
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 20021110: 
            case 80001040: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 23110004: {
                if (applier.passive) {
                    applier.localstatups.clear();
                    applier.maskedDuration = applier.effect.getSubTime() * 1000;
                    applier.maskedstatups.put(SecondaryStat.AddAttackCount, Math.min(applyto.getBuffedIntValue(SecondaryStat.AddAttackCount) + 1, applier.effect.getY()));
                }
                return 1;
            }
            case 23111005: {
                MapleStatEffect eff = applyfrom.getSkillEffect(23120046);
                if (eff != null) {
                    applier.localstatups.put(SecondaryStat.DamAbsorbShield, applier.effect.getX() + eff.getX());
                }
                if ((eff = applyfrom.getSkillEffect(23120047)) != null) {
                    applier.localstatups.put(SecondaryStat.AsrR, applier.effect.getASRRate() + eff.getX());
                }
                if ((eff = applyfrom.getSkillEffect(23120048)) != null) {
                    applier.localstatups.put(SecondaryStat.TerR, applier.effect.getTERRate() + eff.getX());
                }
                return 1;
            }
            case 23121053: {
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
            case 400031007: {
                applyto.getSkillEffect(400031008).applyTo(applyto);
                applyto.getSkillEffect(400031009).applyTo(applyto);
                return 1;
            }
            case 400031008: 
            case 400031009: {
                applier.b7 = false;
                return 1;
            }
            case 400031011: {
                applier.b3 = false;
                return 1;
            }
            case 400031017: {
                applier.localstatups.put(SecondaryStat.RideVehicle, GameConstants.getMountItem(applier.effect.getSourceId(), applyto));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        ForceAtomObject sword;
        MapleStatEffect effecForBuffStat12;
        MapleStatEffect skillEffect13 = player.getSkillEffect(23110004);
        if (applier.totalDamage > 0L && skillEffect13 != null && applier.effect != null && SkillConstants.getLinkedAttackSkill(applier.effect.getSourceId()) != SkillConstants.getLinkedAttackSkill(player.getCheatTracker().getLastAttackSkill())) {
            skillEffect13.unprimaryPassiveApplyTo(player);
        }
        if ((effecForBuffStat12 = player.getEffectForBuffStat(SecondaryStat.TempSecondaryStat)) != null && player.getCheatTracker().canNextBonusAttack((long)effecForBuffStat12.getS2() * 1000L) && player.getBuffStatValueHolder(SecondaryStat.TempSecondaryStat, 400031007) != null) {
            player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400031011, 0, Collections.emptyList()));
        }
        if (player.getLastAttackSkillId() == 23121002 && (applier.ai.skillId == 23111001 || applier.ai.skillId == 23111003 || applier.ai.skillId == 23121011 || applier.ai.skillId == 23121052)) {
            if (effecForBuffStat12 != null) {
                player.reduceSkillCooldown(400031007, 1000);
            }
            player.reduceSkillCooldown(23121002, 1000);
        } else if (player.getLastAttackSkillId() == 23111002 && (applier.ai.skillId == 23121011 || applier.ai.skillId == 23111000 || applier.ai.skillId == 23121002 || applier.ai.skillId == 23121003 || applier.ai.skillId == 23121052)) {
            if (effecForBuffStat12 != null) {
                player.reduceSkillCooldown(400031007, 1000);
            }
            player.reduceSkillCooldown(23111002, 1000);
        } else if (player.getLastAttackSkillId() == 23121052 && (applier.ai.skillId == 23121011 || applier.ai.skillId == 23111000 || applier.ai.skillId == 23111002 || applier.ai.skillId == 23121002 || applier.ai.skillId == 23121003)) {
            if (effecForBuffStat12 != null) {
                player.reduceSkillCooldown(400031007, 1000);
            }
            player.reduceSkillCooldown(23121052, 1000);
        }
        Map<Integer, ForceAtomObject> swordsMap = player.getForceAtomObjects();
        if (!player.isSkillCooling(400031045)) {
            ArrayList<ForceAtomObject> removeList = new ArrayList<ForceAtomObject>();
            Iterator<Map.Entry<Integer, ForceAtomObject>> iterator = swordsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ForceAtomObject> sword1 = iterator.next();
                if (sword1.getValue().SkillId != 400031045) continue;
                removeList.add(sword1.getValue());
                iterator.remove();
            }
            if (!removeList.isEmpty()) {
                player.getMap().broadcastMessage(AdelePacket.ForceAtomObjectRemove((int)player.getId(), removeList, (int)1), player.getPosition());
            }
        }
        if (applier.totalDamage > 0L && applier.ai.mobAttackInfo.size() > 0 && applier.ai.skillId != 400031045 && player.getBuffStatValueHolder(400031044) != null && !player.isSkillCooling(400031045)) {
            player.registerSkillCooldown(player.getSkillEffect(400031045), true);
            ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
            for (int i = 0; i < 4; ++i) {
                sword = new ForceAtomObject(player.getSpecialStat().gainForceCounter(), 14, 0, player.getId(), 0, 400031045);
                sword.Position = new Point(applier.ai.mobAttackInfo.get((int)0).hitX + Randomizer.nextInt(200), applier.ai.mobAttackInfo.get((int)0).hitY + 46);
                sword.ObjPosition = new Point(applier.ai.mobAttackInfo.get((int)0).hitX, applier.ai.mobAttackInfo.get((int)0).hitY);
                sword.Expire = 10000;
                sword.Target = applier.ai.mobAttackInfo.get((int)0).mobId;
                swordsMap.put(sword.Idx, sword);
                createList.add(sword);
            }
            if (!createList.isEmpty()) {
                player.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)player.getId(), createList, (int)0), player.getPosition());
            }
        }
        return 1;
    }
}

