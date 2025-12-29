/*
 * Decompiled with CFR 0.152.
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
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import java.lang.reflect.Field;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 狂狼勇士
extends AbstractSkillHandler {
    public 狂狼勇士() {
        this.jobs = new MapleJob[]{MapleJob.傳說, MapleJob.狂狼勇士1轉, MapleJob.狂狼勇士2轉, MapleJob.狂狼勇士3轉, MapleJob.狂狼勇士4轉};
        for (Field field : Config.constants.skills.狂狼勇士.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{20001005, 20000194, 20001295, 20001296}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 20001005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 21000011: {
                statups.put(SecondaryStat.Booster, effect.getX());
                statups.put(SecondaryStat.IndieSTR, 1);
                return 1;
            }
            case 21000014: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndiePDD, 20);
                statups.put(SecondaryStat.IndiePAD, 2);
                statups.put(SecondaryStat.IndieCr, 1);
            }
            case 21101006: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.WeaponCharge, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.q) * -1);
                return 1;
            }
            case 21100019: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ComboAbilityBuff, 10);
                return 1;
            }
            case 21110016: {
                statups.put(SecondaryStat.IndieBooster, 1);
                statups.put(SecondaryStat.AdrenalinBoost, effect.getInfo().get((Object)MapleStatInfo.w));
                return 1;
            }
            case 21101005: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AranDrain, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 21121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 21111030: {
                statups.put(SecondaryStat.AdrenalinActivate, 1);
                return 1;
            }
            case 21110031: {
                statups.put(SecondaryStat.IndieCr, 60);
                return 1;
            }
            case 21120002: {
                statups.put(SecondaryStat.IndiePADR, 30);
                statups.put(SecondaryStat.IndieMHPR, 30);
                statups.put(SecondaryStat.IndieCr, 100);
                return 1;
            }
            case 400011016: {
                statups.put(SecondaryStat.MahaInstall, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.TempSecondaryStat, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 21121058) {
            chr.getSkillEffect(21110016).applyTo(chr);
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 21000006: 
            case 21000007: 
            case 21001010: {
                applyto.gainAranCombo(2, true);
                return 1;
            }
            case 21001012: {
                applyto.gainAranCombo(1, true);
                return 1;
            }
            case 21001013: {
                applyto.gainAranCombo(1, true);
                return 1;
            }
            case 21101004: 
            case 21110031: 
            case 21111032: 
            case 21111033: 
            case 21120002: 
            case 21121016: 
            case 21121017: 
            case 21121022: 
            case 21141000: 
            case 21141001: 
            case 21141002: 
            case 21141003: 
            case 400010070: {
                applyto.gainAranCombo(5, true);
                return 1;
            }
            case 21111017: {
                applyto.gainAranCombo(4, true);
                return 1;
            }
            case 21121006: {
                applyto.gainAranCombo(8, true);
                return 1;
            }
            case 20001296: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 21101005: {
                if (applyto.getBuffedIntValue(SecondaryStat.AranDrain) > 0) {
                    applier.b3 = false;
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 21100019: {
                MapleStatEffect skillEffect13 = applyto.getSkillEffect(21110000);
                int min4 = Math.min(applyto.getAranCombo() / 50, applier.effect.getX());
                if (min4 <= 0 || applyto.getBuffedIntValue(SecondaryStat.ComboAbilityBuff) >= applier.effect.getX()) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.IndiePAD, min4 * applier.effect.getY());
                applier.localstatups.put(SecondaryStat.IndiePDD, min4 * applier.effect.getZ());
                applier.localstatups.put(SecondaryStat.IndieSpeed, min4 * applier.effect.getW());
                if (skillEffect13 != null) {
                    applier.localstatups.put(SecondaryStat.IndieCr, min4 * skillEffect13.getY());
                    applier.localstatups.put(SecondaryStat.IndiePAD, min4 * (applier.effect.getY() + skillEffect13.getZ()));
                }
                applier.localstatups.put(SecondaryStat.ComboAbilityBuff, min4);
                return 1;
            }
            case 400011016: {
                applyto.getMap().removeAffectedArea(applyto.getId(), 21121068);
                applyto.gainAranCombo(100, true);
                return 1;
            }
            case 21121068: {
                applyto.removeDebuffs();
                applyto.addHPMP(applier.effect.getW(), applier.effect.getW());
                return 1;
            }
            case 21121053: {
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
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect eff = applyfrom.getSkillEffect(21101006);
        if (eff != null && applyfrom.getBuffedValue(SecondaryStat.WeaponCharge) != null) {
            eff.applyMonsterEffect(applyfrom, applyto, eff.getY() * 1000);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        return 1;
    }
}

