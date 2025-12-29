/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.江湖;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
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
import tools.data.MaplePacketReader;

public class 墨玄
extends AbstractSkillHandler {
    public 墨玄() {
        this.jobs = new MapleJob[]{MapleJob.墨玄, MapleJob.墨玄1轉, MapleJob.墨玄2轉, MapleJob.墨玄3轉, MapleJob.墨玄4轉};
        for (Field field : Config.constants.skills.墨玄.class.getDeclaredFields()) {
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
        Skill skil;
        int[] ss;
        int level = chr.getLevel();
        for (int i : ss = new int[]{170000001, 170001000, 170001005}) {
            if (i == 170001005 && level < 200) continue;
            int skillLevel = i == 170000001 ? Math.min(10, level / 20 + 1) : 1;
            skil = SkillFactory.getSkill(i);
            if (skil == null || chr.getSkillLevel(skil) >= skillLevel) continue;
            applier.skillMap.put(i, new SkillEntry(skillLevel, 1, -1L));
        }
        if (chr.getJob() >= MapleJob.墨玄1轉.getId()) {
            int[] fixskills;
            for (int f : fixskills = new int[]{175101004, 175000006}) {
                skil = SkillFactory.getSkill(f);
                if (skil == null || chr.getSkillLevel(skil) > 0 || chr.getMasterLevel(skil) > 0) continue;
                applier.skillMap.put(f, new SkillEntry(0, (byte)(skil.getMasterLevel() == 0 ? skil.getMaxLevel() : skil.getMasterLevel()), SkillFactory.getDefaultSExpiry(skil)));
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 175001002: 
            case 175121001: {
                return 175000001;
            }
            case 175101001: 
            case 175121002: {
                return 175100000;
            }
            case 175101003: {
                return 175101001;
            }
            case 175111001: {
                return 175111002;
            }
            case 175121003: 
            case 175121004: {
                return 175121003;
            }
            case 175000005: {
                return 175001004;
            }
            case 175101005: 
            case 175101006: {
                return 175101004;
            }
            case 175111003: {
                return 175111002;
            }
            case 175121006: 
            case 175121018: {
                return 175121005;
            }
            case 175121008: 
            case 175121017: {
                return 175121007;
            }
            case 400051085: {
                return 400051084;
            }
            case 400051087: {
                return 400051086;
            }
            case 400051090: 
            case 400051091: 
            case 400051092: {
                return 400051089;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 170001005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 175101007: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CannonShooter_BFCannonBall, 0);
                return 1;
            }
            case 175100012: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 175121042: {
                effect.getInfo().put(MapleStatInfo.time, 60000);
                statups.put(SecondaryStat.MukHyun_IM_GI_EUNG_BYEON, 1);
                statups.put(SecondaryStat.IndieDamR, 10);
                return 1;
            }
            case 175121009: {
                statups.put(SecondaryStat.MukHyun_HO_SIN_GANG_GI, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 175101005: {
                effect.getInfo().put(MapleStatInfo.time, 5000);
                statups.put(SecondaryStat.MukHyunRepeat, 1);
                return 1;
            }
            case 175111004: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 175121008: {
                statups.put(SecondaryStat.IndiePeriodicalSkillActivation, 47);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                return 1;
            }
            case 175121040: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 400051086: {
                statups.put(SecondaryStat.IndiePeriodicalSkillActivation, 20);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                return 1;
            }
            case 400051088: {
                statups.put(SecondaryStat.IndieCr, effect.getInfo().get((Object)MapleStatInfo.indieCr));
                statups.put(SecondaryStat.IndieCD, effect.getInfo().get((Object)MapleStatInfo.indieCD));
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 175121007: {
                int godPower = 0;
                int time = 30000;
                if (chr.getSkillLevel(175110010) > 0) {
                    godPower = (Integer)chr.getTempValues().getOrDefault("GodPower", 0);
                    ++godPower;
                    godPower = Math.min(5, godPower);
                    chr.getTempValues().put("GodPower", godPower);
                    if (chr.getSkillLevel(175120039) > 0) {
                        time += 10000;
                    }
                }
                return 1;
            }
            case 175121041: 
            case 400051084: {
                int godPower = 0;
                int time = 30000;
                if (chr.getSkillLevel(175110010) > 0) {
                    godPower = applier.effect.getSourceId() == 175121041 ? 5 : 0;
                    chr.getTempValues().put("GodPower", godPower);
                    if (chr.getSkillLevel(175120039) > 0) {
                        time += 10000;
                    }
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 170001000: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 175101007: {
                int value = applyto.getBuffedIntValue(SecondaryStat.CannonShooter_BFCannonBall) + (applier.passive ? 1 : -1);
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.CannonShooter_BFCannonBall);
                if (!applier.primary || value < 0 || mbsvh != null && System.currentTimeMillis() < mbsvh.startTime + (long)applier.effect.getT() * 1000L && applier.passive || value > applier.effect.getY()) {
                    return 0;
                }
                applier.duration = 2100000000;
                applier.localstatups.put(SecondaryStat.CannonShooter_BFCannonBall, value);
                return 1;
            }
            case 175101004: {
                MapleStatEffect skillEffect = applyto.getSkillEffect(175101005);
                if (skillEffect != null) {
                    skillEffect.applyBuffEffect(applyto, skillEffect.getBuffDuration(applyto), true);
                }
                return 1;
            }
            case 175101005: {
                if (applyto.hasBuffSkill(175101005)) {
                    applyto.dispelEffect(175101005);
                    return 0;
                }
                return 1;
            }
            case 175111004: {
                return 1;
            }
            case 175121007: {
                MapleStatEffect skillEffect;
                if (!applyto.hasBuffSkill(400051086) && (skillEffect = applyto.getSkillEffect(175121008)) != null) {
                    skillEffect.applyBuffEffect(applyto, skillEffect.getBuffDuration(applyto), true);
                }
                return 1;
            }
            case 400051086: {
                if (applyto.hasBuffSkill(175121008)) {
                    applyto.dispelEffect(175121008);
                }
                return 1;
            }
            case 400051088: {
                applier.startChargeTime = 1L;
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect effect;
        if (!applier.overwrite && applier.effect.getSourceId() == 400051086 && (effect = player.getSkillEffect(175121007)) != null) {
            effect.applyTo(player, true);
        }
        return -1;
    }
}

