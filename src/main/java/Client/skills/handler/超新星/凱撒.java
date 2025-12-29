/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.超新星;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 凱撒
extends AbstractSkillHandler {
    public 凱撒() {
        this.jobs = new MapleJob[]{MapleJob.凱撒, MapleJob.凱撒1轉, MapleJob.凱撒2轉, MapleJob.凱撒3轉, MapleJob.凱撒4轉};
        for (Field field : Config.constants.skills.凱撒.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{60001005, 60001216, 60001217, 60001218, 60000219, 60001296}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 60001005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 61141501: {
                return 61141500;
            }
            case 61141000: 
            case 61141001: {
                return 61121100;
            }
            case 500004136: {
                return 400011012;
            }
            case 500004137: {
                return 400011058;
            }
            case 500004138: {
                return 400011079;
            }
            case 500004139: {
                return 400011118;
            }
            case 61001004: 
            case 61001005: 
            case 61110212: 
            case 61120219: {
                return 61001000;
            }
            case 61001006: 
            case 61001207: 
            case 61111221: {
                return 61001002;
            }
            case 61111111: 
            case 61111218: {
                return 61111100;
            }
            case 61111220: {
                return 61111002;
            }
            case 61121116: 
            case 61121124: 
            case 61121221: 
            case 61121223: 
            case 61121225: {
                return 61121104;
            }
            case 61111114: 
            case 61120008: {
                return 61111008;
            }
            case 61121026: 
            case 61121203: {
                return 61121102;
            }
            case 61121201: {
                return 61121100;
            }
            case 61110211: {
                return 61101002;
            }
            case 61121217: {
                return 61120007;
            }
            case 61111217: {
                return 61101101;
            }
            case 61111219: {
                return 61111101;
            }
            case 61111215: {
                return 61001101;
            }
            case 61120018: 
            case 61121222: {
                return 61121105;
            }
            case 61111216: {
                return 61101100;
            }
            case 61121220: {
                return 61121015;
            }
            case 61111113: {
                return 61111100;
            }
            case 400011013: 
            case 400011014: {
                return 400011012;
            }
            case 400011059: 
            case 400011060: 
            case 400011061: {
                return 400011058;
            }
            case 400011080: 
            case 400011081: 
            case 400011082: {
                return 400011079;
            }
            case 400011119: 
            case 400011120: 
            case 400011130: {
                return 400011118;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 60001005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 60001216: 
            case 60001217: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ReshuffleSwitch, 0);
                return 1;
            }
            case 61101002: 
            case 61110211: 
            case 61120007: 
            case 61121217: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.StopForceAtomInfo, effect.getLevel());
                return 1;
            }
            case 61101004: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                return 1;
            }
            case 61111002: 
            case 61111220: {
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 61111003: {
                statups.put(SecondaryStat.AsrR, effect.getInfo().get((Object)MapleStatInfo.terR));
                statups.put(SecondaryStat.TerR, effect.getInfo().get((Object)MapleStatInfo.asrR));
                return 1;
            }
            case 61121009: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.PartyBarrier, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 60000219: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SmashStack, 0);
                break;
            }
            case 61111008: 
            case 61120008: 
            case 61121053: {
                effect.getInfo().put(MapleStatInfo.time, 60000);
                statups.put(SecondaryStat.IgnoreAllCounter, 1);
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.indiePMdR));
                statups.put(SecondaryStat.Morph, effect.getInfo().get((Object)MapleStatInfo.morph));
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.prop));
                statups.put(SecondaryStat.CriticalBuff, effect.getInfo().get((Object)MapleStatInfo.cr));
                return 1;
            }
            case 61121054: {
                statups.put(SecondaryStat.IndieIgnorePCounter, 1);
                statups.put(SecondaryStat.IgnorePImmune, 1);
                statups.put(SecondaryStat.IndiePAD, 30);
                statups.put(SecondaryStat.Booster, 4);
                return 1;
            }
            case 61111101: {
                effect.getInfo().put(MapleStatInfo.time, 2000);
            }
            case 61101101: 
            case 61111217: 
            case 61111219: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 61111100: 
            case 61111111: 
            case 61111113: 
            case 61111218: {
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.z));
                return 1;
            }
            case 400011058: 
            case 400011059: {
                effect.getInfo().put(MapleStatInfo.bulletCount, 5);
                return 1;
            }
            case 400011118: 
            case 400011119: 
            case 400011120: 
            case 400011130: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 61121054: {
                chr.clearCooldown(true);
                return 1;
            }
            case 400011012: {
                chr.getSkillEffect(400011013).applyTo(chr, new Point(chr.getPosition().x + 100, chr.getPosition().y));
                chr.getSkillEffect(400011014).applyTo(chr, new Point(chr.getPosition().x - 100, chr.getPosition().y));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 60001296: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 60000219: {
                applier.localstatups.put(SecondaryStat.SmashStack, Math.min(applyto.getBuffedIntValue(SecondaryStat.SmashStack) + 10, 700));
                return 1;
            }
            case 61111008: 
            case 61120008: {
                applyto.setBuffStatValue(SecondaryStat.SmashStack, 60000219, 0);
                applier.localstatups.put(SecondaryStat.SmashStack, 0);
                return 1;
            }
            case 61101002: 
            case 61110211: 
            case 61120007: 
            case 61121217: {
                if (!applier.primary) {
                    return 0;
                }
                Item weapon = applyto.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
                applier.buffz = weapon == null ? 0 : weapon.getItemId();
                return 1;
            }
            case 60001216: {
                applyto.dispelEffect(60001217);
                return 1;
            }
            case 60001217: {
                applyto.dispelEffect(60001216);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect skillEffect20;
        if (this.containsJob(applyfrom.getJobWithSub()) && applyfrom.getBuffedValue(SecondaryStat.Morph) == null && (skillEffect20 = applyfrom.getSkillEffect(60000219)) != null) {
            skillEffect20.unprimaryPassiveApplyTo(applyfrom);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect skillEffect15;
        if (applier.effect != null && applier.effect.getSourceId() == 61121104 && (skillEffect15 = player.getSkillEffect(61121116)) != null) {
            skillEffect15.applyAffectedArea(player, player.getPosition());
        }
        return 1;
    }
}

