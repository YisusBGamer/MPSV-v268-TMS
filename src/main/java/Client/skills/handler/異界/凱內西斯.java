/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.異界;

import Client.MapleCharacter;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.Map;

public class 凱內西斯
extends AbstractSkillHandler {
    public 凱內西斯() {
        this.jobs = new MapleJob[]{MapleJob.凱內西斯, MapleJob.凱內西斯1轉, MapleJob.凱內西斯2轉, MapleJob.凱內西斯3轉, MapleJob.凱內西斯4轉};
        for (Field field : Config.constants.skills.凱內西斯.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{140001289, 140001290, 142001007, 140001005}) {
            if (chr.getLevel() < 200 && i == 140001005) continue;
            Skill skil = SkillFactory.getSkill(i);
            if (chr.getJob() < i / 10000 || skil == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(skil.getMaxLevel(), skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 142141000: {
                return 142001002;
            }
            case 500004156: {
                return 400021008;
            }
            case 500004157: {
                return 400021048;
            }
            case 500004158: {
                return 400021074;
            }
            case 500004159: {
                return 400021096;
            }
            case 142110003: 
            case 142110015: {
                return 142111002;
            }
            case 142120001: 
            case 142120002: 
            case 142120014: {
                return 142120000;
            }
            case 142120030: {
                return 142121030;
            }
            case 142100001: {
                return 142100000;
            }
            case 142110001: {
                return 142110000;
            }
            case 142100008: {
                return 142101002;
            }
            case 142000006: {
                return 142001004;
            }
            case 142120015: {
                return 142121008;
            }
            case 400020009: 
            case 400020010: 
            case 400020011: 
            case 400021009: 
            case 400021010: 
            case 400021011: {
                return 400021008;
            }
            case 400021053: {
                return 400021048;
            }
            case 400021075: 
            case 400021076: {
                return 400021074;
            }
            case 400021097: 
            case 400021098: 
            case 400021104: {
                return 400021096;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 140001005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 142001007: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KinesisPsychicEnergeShield, 1);
                return 1;
            }
            case 142001003: {
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                return 1;
            }
            case 142111006: 
            case 142120003: {
                monsterStatus.put(MonsterStatus.IndiePDR, -effect.getInfo().get((Object)MapleStatInfo.s).intValue());
                monsterStatus.put(MonsterStatus.IndieMDR, -effect.getInfo().get((Object)MapleStatInfo.s).intValue());
                monsterStatus.put(MonsterStatus.IndieSlow, -effect.getInfo().get((Object)MapleStatInfo.s).intValue());
                monsterStatus.put(MonsterStatus.PsychicGroundMark, effect.getInfo().get((Object)MapleStatInfo.s));
                return 1;
            }
            case 142001000: 
            case 142100000: 
            case 142110000: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 142111010: {
                statups.put(SecondaryStat.NewFlying, 1);
                return 1;
            }
            case 142121016: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieStatRBasic, 150);
                return 1;
            }
            case 142121032: {
                statups.put(SecondaryStat.KinesisPsychicOver, 1);
                return 1;
            }
            case 400021008: {
                statups.put(SecondaryStat.Kinesis_DustTornado, 3);
                return 1;
            }
            case 142121031: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 400021096: {
                statups.put(SecondaryStat.KinesisLawOfGravity, 2);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 140001290: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 142001007: {
                if (applyto.getBuffedValue(SecondaryStat.KinesisPsychicEnergeShield) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 142111010: {
                applier.duration += 500;
                return 1;
            }
            case 142121008: {
                applyto.handlePPCount(Math.max((30 - applyto.getSpecialStat().getPP()) / 2, 1));
                return 1;
            }
            case 142120001: {
                MapleStatEffect eff = applyto.getSkillEffect(142120035);
                if (eff != null) {
                    eff.applyTo(applyto);
                }
                return 1;
            }
            case 142121004: {
                int add = Math.max(1, applyto.getSpecialStat().getMindBreakCount()) * applier.effect.getIndiePMdR();
                applier.localstatups.put(SecondaryStat.IndiePMdR, add);
                if (applyto.getSkillEffect(142120041) != null) {
                    applier.localstatups.put(SecondaryStat.IndiePMdR, add * 2);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applyfrom.hasBuffSkill(142121004)) {
            applyfrom.getSpecialStat().setMindBreakCount(Math.min(applyfrom.getSkillEffect(142121004).getW(), applyfrom.getSpecialStat().getMindBreakCount() + (applyto.isBoss() ? 5 : 1)));
            applyfrom.getSkillEffect(142121004).applyTo(applyfrom, null, true);
        }
        if (applier.effect != null && applyto != null && applyfrom != null) {
            switch (applier.effect.getSourceId()) {
                case 400021096: {
                    applyfrom.getSkillEffect(400021104).applyAffectedArea(applyfrom, applyfrom.getPosition());
                    break;
                }
                case 400021098: {
                    applyfrom.getMap().broadcastMessage(MaplePacketCreator.objSkillEffect(applyto.getObjectId(), 400021098, applyfrom.getId(), applyto.getPosition()));
                }
            }
        }
        return 1;
    }
}

