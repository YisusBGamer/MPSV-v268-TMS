/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.雷普族;

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
import java.lang.reflect.Field;
import java.util.Map;

public class 卡莉
extends AbstractSkillHandler {
    public 卡莉() {
        this.jobs = new MapleJob[]{MapleJob.卡莉, MapleJob.卡莉1轉, MapleJob.卡莉2轉, MapleJob.卡莉3轉, MapleJob.卡莉4轉};
        for (Field field : Config.constants.skills.卡莉.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{150030079, 150031074, 150031005}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 150031005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 154101005: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 154111000: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SummonChakri, 1);
                return 1;
            }
            case 154110010: {
                effect.getInfo().put(MapleStatInfo.time, 30000);
                statups.put(SecondaryStat.SummonChakri, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 154121004: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 3000);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 154121042: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 154111007: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.HideAttack, 1);
                return 1;
            }
            case 154121003: {
                effect.getInfo().put(MapleStatInfo.time, 2000);
                statups.put(SecondaryStat.DarkSight, effect.getLevel());
                statups.put(SecondaryStat.Speed, 1);
                return 1;
            }
            case 154121043: {
                effect.getInfo().put(MapleStatInfo.time, 30000);
                effect.getInfo().put(MapleStatInfo.coolTimeR, 50);
                effect.getInfo().put(MapleStatInfo.damR, 30);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 154121042: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(151121042);
                applyto.dispelEffect(152121042);
                applyto.dispelEffect(155121042);
                applyto.dispelEffect(154121042);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 154141501: 
            case 154141502: 
            case 154141503: {
                return 154141500;
            }
            case 154141000: {
                return 154121000;
            }
            case 154001001: {
                return 154001001;
            }
            case 154001002: {
                return 154001002;
            }
            case 154101002: {
                return 154101001;
            }
            case 154101004: 
            case 154101009: 
            case 154101010: {
                return 154100003;
            }
            case 154111000: {
                return 154110010;
            }
            case 154111004: 
            case 154111011: {
                return 154110003;
            }
            case 154121009: 
            case 154121011: {
                return 154121003;
            }
            case 400041083: {
                return 400041082;
            }
            case 400041085: 
            case 400041086: {
                return 400041084;
            }
            case 400041088: {
                return 400041087;
            }
            case 400041090: 
            case 400041091: {
                return 400041089;
            }
        }
        return -1;
    }
}

