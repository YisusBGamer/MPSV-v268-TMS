/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.江湖;

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
import Config.constants.skills.菈菈;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import java.lang.reflect.Field;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 琳恩
extends AbstractSkillHandler {
    public 琳恩() {
        this.jobs = new MapleJob[]{MapleJob.琳恩, MapleJob.琳恩1轉, MapleJob.琳恩2轉, MapleJob.琳恩3轉, MapleJob.琳恩4轉};
        for (Field field : 菈菈.class.getDeclaredFields()) {
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
        int level = chr.getLevel();
        for (int i : ss = new int[]{170011000, 170011002, 170011001, 170011005}) {
            if (i == 170011005 && level < 200) continue;
            int skillLevel = 1;
            Skill skil = SkillFactory.getSkill(i);
            if (skil == null || chr.getSkillLevel(skil) >= skillLevel) continue;
            applier.skillMap.put(i, new SkillEntry(skillLevel, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 172000011: {
                return 172000001;
            }
            case 172100011: {
                return 172100001;
            }
            case 172111002: {
                return 172111001;
            }
            case 172111007: {
                return 172111006;
            }
            case 172110011: {
                return 172110001;
            }
            case 172121044: {
                return 172121043;
            }
            case 400021135: 
            case 400021136: {
                return 400021138;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 172111009: {
                effect.setPartyBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 90000);
                statups.put(SecondaryStat.IndieEVAR, 90000);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        applier.effect.getSourceId();
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 170011000: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
        }
        return -1;
    }
}

