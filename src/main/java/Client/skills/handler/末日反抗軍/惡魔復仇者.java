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
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Packet.ForcePacket;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tools.data.MaplePacketReader;

public class 惡魔復仇者
extends AbstractSkillHandler {
    public 惡魔復仇者() {
        this.jobs = new MapleJob[]{MapleJob.惡魔復仇者1轉, MapleJob.惡魔復仇者2轉, MapleJob.惡魔復仇者3轉, MapleJob.惡魔復仇者4轉};
        for (Field field : Config.constants.skills.惡魔復仇者.class.getDeclaredFields()) {
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
        if (chr.getLevel() >= 10) {
            int[] ss;
            for (int i : ss = new int[]{30010242, 30010230, 30010231, 30010232}) {
                Skill skil = SkillFactory.getSkill(i);
                if (skil == null || chr.getSkillLevel(skil) > 0) continue;
                applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 30010230: {
                return 30010242;
            }
            case 30010241: 
            case 80000050: {
                return 30010242;
            }
            case 31241501: 
            case 31241502: {
                return 31241500;
            }
            case 31241000: 
            case 31241001: {
                return 31221001;
            }
            case 500004108: {
                return 400011010;
            }
            case 500004109: {
                return 400011038;
            }
            case 500004110: {
                return 400011090;
            }
            case 500004111: {
                return 400011112;
            }
            case 31011004: 
            case 31011005: 
            case 31011006: 
            case 31011007: {
                return 31011000;
            }
            case 31201007: 
            case 31201008: 
            case 31201009: 
            case 31201010: {
                return 31201000;
            }
            case 31211007: 
            case 31211008: 
            case 31211009: 
            case 31211010: {
                return 31211000;
            }
            case 31211011: {
                return 31211002;
            }
            case 31221009: 
            case 31221010: 
            case 31221011: 
            case 31221012: {
                return 31221000;
            }
            case 31221014: {
                return 31221001;
            }
            case 400010010: {
                return 400011010;
            }
            case 400011062: 
            case 400011063: 
            case 400011064: {
                return 400011038;
            }
            case 400011102: {
                return 400011090;
            }
            case 400011113: 
            case 400011114: 
            case 400011115: 
            case 400011129: {
                return 400011112;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 30010230: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.OverloadCount, 1);
                return 1;
            }
            case 30010242: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.LifeTidal, 3);
                return 1;
            }
            case 31011001: {
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                return 1;
            }
            case 31211001: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                return 1;
            }
            case 31221054: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 31211003: {
                statups.put(SecondaryStat.AsrR, effect.getInfo().get((Object)MapleStatInfo.y));
                statups.put(SecondaryStat.TerR, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.DamAbsorbShield, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 31211004: {
                statups.put(SecondaryStat.DiabolikRecovery, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                return 1;
            }
            case 31201002: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 31221002: {
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.MDR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 31221003: {
                monsterStatus.put(MonsterStatus.Seal, 1);
                return 1;
            }
            case 31221053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400011010: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                effect.getInfo().put(MapleStatInfo.cooltime, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.Frenzy, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400011038: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SiphonVitalityBarrier, 1);
                return 1;
            }
            case 400011102: {
                statups.put(SecondaryStat.DevilishPower, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400011112: {
                statups.put(SecondaryStat.RevenantGauge, 1);
                return 1;
            }
            case 400011129: {
                effect.getInfo().put(MapleStatInfo.time, 38500);
                statups.put(SecondaryStat.DeathDance, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 31221001: {
                slea.readMapleAsciiString();
                slea.skip(4);
                applier.pos = slea.readPos();
                List<Integer> oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
                if (!oids.isEmpty()) {
                    chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, oids)), true);
                }
                return 1;
            }
            case 400011010: {
                if (chr.getBuffedValue(SecondaryStat.Frenzy) != null) {
                    chr.registerSkillCooldown(applier.effect, true);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 30010242: {
                applier.buffz = applyto.getStat().getMaxHp();
                return 1;
            }
            case 31011000: 
            case 31201000: 
            case 31211000: 
            case 31221000: {
                if (!applier.passive) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.Exceed, Math.min(applyto.getBuffedIntValue(SecondaryStat.Exceed) + 1, 4));
                return 1;
            }
            case 30010230: {
                int value = applyto.getBuffedIntValue(SecondaryStat.OverloadCount) + 1;
                int lastAttack = applyto.getCheatTracker().getLastAttackSkill();
                if (lastAttack > 0 && applyto.getBuffSource(SecondaryStat.OverloadCount) > 0 && SkillConstants.getLinkedAttackSkill(lastAttack) != applyto.getBuffSource(SecondaryStat.Exceed)) {
                    ++value;
                }
                applier.localstatups.put(SecondaryStat.OverloadCount, Math.min(value, applyto.getStat().maxBeyondLoad));
                return 1;
            }
            case 31011001: {
                int value = applyto.getBuffedIntValue(SecondaryStat.OverloadCount);
                applyto.setBuffStatValue(SecondaryStat.OverloadCount, 30010230, value / 2 - 1);
                applyto.getSkillEffect(30010230).unprimaryPassiveApplyTo(applyto);
                int hpRecover = applyto.getStat().getCurrentMaxHP() * applier.effect.getX() / 100 * value / applyto.getStat().maxBeyondLoad;
                if (!applyto.isDebug() && hpRecover > 0 && applyto.getEffectForBuffStat(SecondaryStat.Frenzy) != null) {
                    hpRecover = Math.min(applyto.getStat().getCurrentMaxHP() / 100, hpRecover);
                }
                applyto.addHPMP(hpRecover, 0, false);
                MapleStatEffect eff = applyto.getSkillEffect(31210006);
                if (eff != null) {
                    applier.localstatups.put(SecondaryStat.IndiePMdR, eff.getY() * value / applyto.getStat().maxBeyondLoad);
                } else {
                    applier.localstatups.put(SecondaryStat.IndiePMdR, applier.effect.getIndiePMdR() * value / applyto.getStat().maxBeyondLoad);
                }
                return 1;
            }
            case 31211003: {
                MapleStatEffect eff = applyto.getSkillEffect(31220046);
                if (eff != null) {
                    applier.localstatups.put(SecondaryStat.DamAbsorbShield, applier.effect.getX() + eff.getX());
                }
                if ((eff = applyto.getSkillEffect(31220047)) != null) {
                    applier.localstatups.put(SecondaryStat.AsrR, applier.effect.getY() + eff.getX());
                }
                if ((eff = applyto.getSkillEffect(31220048)) != null) {
                    applier.localstatups.put(SecondaryStat.TerR, applier.effect.getZ() + eff.getX());
                }
                return 1;
            }
            case 31221053: {
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
            case 400011010: {
                if (!applier.passive && applyto.getBuffedValue(SecondaryStat.Frenzy) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                    return 1;
                }
                applier.buffz = (100 - applyto.getStat().getHPPercent()) / applier.effect.getU();
                applier.localstatups.put(SecondaryStat.Frenzy, Math.max(1, applier.buffz));
                return 1;
            }
            case 400011038: {
                if (!applier.primary) {
                    return 0;
                }
                applier.overwrite = false;
                applier.startChargeTime = System.currentTimeMillis();
                applier.localstatups.put(SecondaryStat.IndieDamReduceR, (int)applier.startChargeTime);
                return 1;
            }
            case 400011090: {
                if (applyfrom.hasSummonBySkill(400011090)) {
                    applyto.cancelEffect(applier.effect, applier.overwrite, -1L, applier.localstatups);
                    applyfrom.getSkillEffect(400011102).applyBuffEffect(applyfrom, 7000, applier.primary);
                    return 0;
                }
                return 1;
            }
            case 400011112: {
                applier.buffz = (Integer)applyfrom.getTempValues().getOrDefault("亡靈HP消耗", 0);
                applyfrom.getTempValues().remove("亡靈HP消耗");
                return 1;
            }
            case 400011129: {
                applier.buffz = (Integer)applyfrom.getTempValues().getOrDefault("亡靈怒氣", 0);
                applyfrom.getTempValues().remove("亡靈怒氣");
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        int value = player.getBuffedIntValue(SecondaryStat.OverloadCount);
        MapleStatEffect eff = player.getSkillEffect(31210005);
        if (eff != null && value > 0) {
            value = Math.min(0, value - eff.getX());
        }
        if (applier.hpHeal > 0) {
            applier.hpHeal -= applier.hpHeal * value / 100;
        }
        if (applier.effect == null) {
            return -1;
        }
        if (player.getSkillEffect(30010230) != null) {
            int linkSkill = SkillConstants.getLinkedAttackSkill(applier.effect.getSourceId());
            if (applier.effect != null && (linkSkill == 31011000 || linkSkill == 31221000 || linkSkill == 31201000 || linkSkill == 31211000)) {
                player.getSkillEffect(linkSkill).unprimaryPassiveApplyTo(player);
                player.getSkillEffect(30010230).unprimaryPassiveApplyTo(player);
            }
            if (applier.effect != null && applier.effect.getSourceId() == 31221052) {
                int n = player.getBuffedIntValue(SecondaryStat.OverloadCount) + 4;
                player.setBuffStatValue(SecondaryStat.OverloadCount, 30010230, n);
                player.getSkillEffect(30010230).unprimaryPassiveApplyTo(player);
            }
            if (applier.effect != null && applier.totalDamage > 0L) {
                switch (applier.effect.getSourceId()) {
                    case 400011062: 
                    case 400011063: 
                    case 400011064: {
                        player.addHPMP(applier.effect.getX(), 0);
                        player.registerSkillCooldown(player.getSkillEffect(400011038), true);
                    }
                }
            }
        }
        return 1;
    }
}

