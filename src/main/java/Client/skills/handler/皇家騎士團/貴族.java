/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.皇家騎士團;

import Client.MapleCharacter;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.JobConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import java.lang.reflect.Field;
import java.util.Map;

public class 貴族
extends AbstractSkillHandler {
    public 貴族() {
        this.jobs = new MapleJob[]{MapleJob.貴族};
        for (Field field : Config.constants.skills.貴族.class.getDeclaredFields()) {
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
        if (JobConstants.is皇家騎士團(chr.getJobWithSub())) {
            Skill skill;
            int[] ss;
            for (int i : ss = new int[]{10001005, 10001244, 10001245, 10001254, 10000250, 10001254}) {
                if (chr.getLevel() < 200 && i == 10001005 || (skill = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skill) > 0) continue;
                applier.skillMap.put(skill.getId(), new SkillEntry(1, skill.getMaxMasterLevel(), -1L));
            }
            if (JobConstants.getJobNumber(chr.getJob()) == 4 && (skill = SkillFactory.getSkill(chr.getJob() * 10000 + 1000)) != null && chr.getSkillEntry(skill.getId()) == null) {
                applier.skillMap.put(skill.getId(), new SkillEntry(0, skill.getMaxLevel(), -1L));
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        if (skillId == 10001253) {
            return 10001254;
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 10001005: {
                effect.setRangeBuff(true);
            }
            case 10001075: {
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 10001245) {
            applyto.changeMap(applier.effect.getX(), 0);
            return 1;
        }
        return -1;
    }
}

