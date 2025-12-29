/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.皇家騎士團;

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
import Packet.EffectPacket;
import java.lang.reflect.Field;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 暗夜行者
extends AbstractSkillHandler {
    public 暗夜行者() {
        this.jobs = new MapleJob[]{MapleJob.暗夜行者1轉, MapleJob.暗夜行者2轉, MapleJob.暗夜行者3轉, MapleJob.暗夜行者4轉};
        for (Field field : Config.constants.skills.皇家騎士團_技能群組.暗夜行者.class.getDeclaredFields()) {
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
        super.baseSkills(chr, applier);
        for (int i : ss = new int[]{0x989779, 14001026, 14001024}) {
            Skill skill = SkillFactory.getSkill(i);
            if (chr.getJob() < i / 10000 || skill == null || chr.getSkillLevel(skill) > 0) continue;
            applier.skillMap.put(skill.getId(), new SkillEntry(1, skill.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 14001026: {
                return 10001254;
            }
            case 14001032: {
                return 10001253;
            }
            case 500004072: {
                return 400041008;
            }
            case 500004073: {
                return 400041028;
            }
            case 500004074: {
                return 400041037;
            }
            case 500004075: {
                return 400041059;
            }
            case 14141501: 
            case 14141502: {
                return 14141500;
            }
            case 14141001: 
            case 14141002: 
            case 14141003: {
                return 14141000;
            }
            case 14101029: {
                return 14101028;
            }
            case 14101021: {
                return 14101020;
            }
            case 14111021: {
                return 14111020;
            }
            case 14121002: {
                return 14121001;
            }
            case 14000027: 
            case 14000028: 
            case 14000029: {
                return 14001027;
            }
            case 14120011: {
                return 14120009;
            }
            case 14121055: 
            case 14121056: {
                return 14121054;
            }
            case 400040008: 
            case 400041019: {
                return 400041008;
            }
            case 400041060: {
                return 400041059;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 14000028: {
                effect.getInfo().put(MapleStatInfo.mobCount, 3);
                return 1;
            }
            case 14001021: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ElementDarkness, 1);
                return 1;
            }
            case 14001022: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieSpeed, 40);
                statups.put(SecondaryStat.IndieJump, 20);
                return 1;
            }
            case 14001027: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.NightWalkerBat, 1);
                return 1;
            }
            case 14111024: {
                statups.put(SecondaryStat.ShadowServant, 1);
                return 1;
            }
            case 14111030: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ReviveOnce, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 14121016: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndieDotHealHP, 1);
                return 1;
            }
            case 14121004: {
                statups.put(SecondaryStat.Stance, 100);
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 14120009: {
                statups.put(SecondaryStat.SiphonVitality, 1);
                statups.put(SecondaryStat.IncMaxHP, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 14121052: {
                statups.put(SecondaryStat.Dominion, 1);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 14121054: {
                statups.put(SecondaryStat.ShadowIllusion, 1);
                return 1;
            }
            case 14000027: {
                effect.getInfo().put(MapleStatInfo.time, 60000);
                return 1;
            }
            case 14001023: {
                statups.put(SecondaryStat.DarkSight, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 14101022: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 14121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400041008: {
                statups.put(SecondaryStat.ShadowSpear, effect.getLevel());
                return 1;
            }
            case 400041037: {
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 14001031: {
                statups.put(SecondaryStat.DarkSight, effect.getX());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 14121054) {
            chr.getSkillEffect(14111024).applyTo(chr);
            chr.getSkillEffect(14121055).applyTo(chr);
            chr.getSkillEffect(14121056).applyTo(chr);
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 14001027: {
                if (applyto.getBuffedIntValue(SecondaryStat.NightWalkerBat) > 0) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 14120009: {
                int min3 = Math.min(5, applyto.getBuffedIntValue(SecondaryStat.SiphonVitality) + 1);
                int y = applier.effect.getY();
                if (applyto.getSkillLevel(14120049) > 0) {
                    y += 300;
                }
                if (applyto.getSkillLevel(14120050) > 0) {
                    applier.localstatups.put(SecondaryStat.IndiePDD, min3 * 100);
                }
                if (applyto.getSkillLevel(14120051) > 0) {
                    applier.localstatups.put(SecondaryStat.IndieAsrR, 2 * min3);
                }
                applier.localstatups.put(SecondaryStat.SiphonVitality, min3);
                applier.localstatups.put(SecondaryStat.IncMaxHP, y * min3);
                return 1;
            }
            case 14000027: {
                applier.b7 = false;
                if (applyto.getSummonCountBySkill(14000027) >= 2 + (applyto.getSkillLevel(14100027) > 0 ? 1 : 0) + (applyto.getSkillLevel(0xD74D4D) > 0 ? 1 : 0) + (applyto.getSkillLevel(14120008) > 0 ? 1 : 0)) {
                    return 0;
                }
                return 1;
            }
            case 14111030: {
                if (applier.passive) {
                    applier.localstatups.clear();
                    applier.duration = 2000;
                    applier.localstatups.put(SecondaryStat.NotDamaged, 1);
                    applyto.getClient().announce(EffectPacket.show黑暗重生(-1, 14111030, 3000));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.show黑暗重生(applyto.getId(), 14111030, 3000), false);
                }
                return 1;
            }
            case 14121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000 || applyto.getJob() / 1000 == 5) {
                    return 0;
                }
                applyto.dispelEffect(11121053);
                applyto.dispelEffect(12121053);
                applyto.dispelEffect(13121053);
                applyto.dispelEffect(14121053);
                applyto.dispelEffect(15121053);
                applyto.dispelEffect(51121053);
                return 1;
            }
            case 400041037: {
                applier.localstatups.put(SecondaryStat.IndiePMdR, Math.min(applier.effect.getQ(), applier.effect.getY() * applyfrom.getSpecialStat().getShadowBite()));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applyfrom.getEffectForBuffStat(SecondaryStat.ShadowSpear) != null && applyto.isAlive() && applier.effect != null && applier.effect.getSourceId() != 14121003 && applier.effect.getSourceId() != 400040008 && applyfrom.getCheatTracker().canNextBonusAttack(3000L)) {
            applyfrom.getSkillEffect(400040008).applyAffectedArea(applyfrom, applyto.getPosition());
        }
        if (applier.effect != null && applier.effect.getSourceId() == 400041037 && applyto != null) {
            if (applyto.isBoss()) {
                applyfrom.getSpecialStat().addShadowBite(applier.effect.getDuration(), applier.effect.getW());
            } else if (!applyto.isAlive()) {
                applyfrom.getSpecialStat().addShadowBite(applier.effect.getDuration(), 1);
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat7 = player.getEffectForBuffStat(SecondaryStat.NightWalkerBat);
        if (applier.totalDamage > 0L && effecForBuffStat7 != null && applier.effect != null && applier.effect.getBulletCount() > 1) {
            player.getCheatTracker().addShadowBat();
            MapleStatEffect skillEffect12 = player.getSkillEffect(14000027);
            if (skillEffect12 != null && player.getCheatTracker().canSpawnShadowBat()) {
                skillEffect12.applyTo(player);
            }
        }
        return 1;
    }
}

