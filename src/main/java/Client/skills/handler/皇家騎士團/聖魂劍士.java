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
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.maps.MapleSummon;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public class 聖魂劍士
extends AbstractSkillHandler {
    private MapleCharacter chr;

    public MapleCharacter getChr() {
        return this.chr;
    }

    public 聖魂劍士() {
        this.jobs = new MapleJob[]{MapleJob.聖魂劍士1轉, MapleJob.聖魂劍士2轉, MapleJob.聖魂劍士3轉, MapleJob.聖魂劍士4轉};
        for (Field field : Config.constants.skills.皇家騎士團_技能群組.聖魂劍士.class.getDeclaredFields()) {
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
        Skill skill = SkillFactory.getSkill(10000246);
        if (skill != null && chr.getSkillLevel(skill) <= 0) {
            applier.skillMap.put(skill.getId(), new SkillEntry(1, skill.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 500004060: {
                return 400011142;
            }
            case 500004061: {
                return 400011055;
            }
            case 500004062: {
                return 400011088;
            }
            case 500004063: {
                return 400011048;
            }
            case 11141502: 
            case 11141503: {
                return 11141500;
            }
            case 11001029: {
                return 11001028;
            }
            case 400011056: 
            case 400011065: {
                return 400011055;
            }
            case 11121102: {
                return 11121157;
            }
            case 11121014: {
                return 11121014;
            }
            case 11121013: {
                return 11121004;
            }
            case 11121055: {
                return 11121054;
            }
            case 400011022: 
            case 400011023: {
                return 400011005;
            }
            case 400011089: {
                return 400011088;
            }
            case 400011049: {
                return 400011048;
            }
            case 11001126: 
            case 11100128: 
            case 11110128: 
            case 11120117: 
            case 11141100: {
                return 11001226;
            }
            case 11100228: 
            case 11110228: 
            case 11120217: 
            case 0xAA0050: {
                return 11001025;
            }
            case 11111130: {
                return 11111230;
            }
            case 11001024: {
                return 11001025;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 11001022: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ElementSoul, effect.getLevel());
                statups.put(SecondaryStat.CosmicForge, 1);
                return 1;
            }
            case 11100034: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                effect.getInfo().put(MapleStatInfo.pad, 7);
                statups.put(SecondaryStat.CosmicForge, 1);
                return 1;
            }
            case 11110031: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                effect.getInfo().put(MapleStatInfo.pad, 6);
                statups.put(SecondaryStat.CosmicForge, 1);
                return 1;
            }
            case 11120019: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                effect.getInfo().put(MapleStatInfo.pad, 5);
                statups.put(SecondaryStat.CosmicForge, 1);
                return 1;
            }
            case 11001024: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.PoseType, 1);
                return 1;
            }
            case 11001025: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.PoseType, 2);
                return 1;
            }
            case 11101031: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.GlimmeringTime, 1);
                return 1;
            }
            case 11101032: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.PoseType, 1);
                return 1;
            }
            case 11101033: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.PoseType, 2);
                return 1;
            }
            case 11121054: {
                statups.put(SecondaryStat.ACCR, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieScriptBuff, effect.getInfo().get((Object)MapleStatInfo.indieMaxDamageOver));
                return 1;
            }
            case 11120009: {
                statups.put(SecondaryStat.BuckShot, effect.getLevel());
                statups.put(SecondaryStat.IndieCr, effect.getInfo().get((Object)MapleStatInfo.indieCr));
                return 1;
            }
            case 11120010: {
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 11111023: {
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.MDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.TrueSight, 1);
                return 1;
            }
            case 11100024: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 11121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400011005: {
                statups.put(SecondaryStat.TempSecondaryStat, 1);
                return 1;
            }
            case 400011055: {
                statups.put(SecondaryStat.Elysion, effect.getLevel());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 11001025: {
                applyto.dispelEffect(11001024);
                MapleStatEffect eff = applyto.getSkillEffect(11101033);
                if (eff != null) {
                    applier.localstatups.putAll(eff.getStatups());
                }
                return 1;
            }
            case 11001024: {
                applyto.dispelEffect(11001025);
                MapleStatEffect eff = applyto.getSkillEffect(11101032);
                if (eff != null) {
                    applier.localstatups.putAll(eff.getStatups());
                }
                return 1;
            }
            case 11101031: {
                int value = applyto.getBuffedIntValue(SecondaryStat.PoseType);
                if (value <= 0) {
                    return 0;
                }
                MapleStatEffect eff = applyto.getSkillEffect(value == 1 ? 11110032 : 11110033);
                if (eff != null) {
                    eff.applyTo(applyto);
                }
                return 1;
            }
            case 11121014: {
                applier.b4 = false;
                return 1;
            }
            case 11121053: {
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
            case 400011055: {
                if (applier.passive) {
                    return 0;
                }
                applyto.reduceSkillCooldown(11121055, 9999);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect skillEffect8;
        int mode;
        if (applier.effect != null && player.getBuffedValue(SecondaryStat.GlimmeringTime) != null && (mode = SkillConstants.getSoulMasterAttackMode(applier.effect.getSourceId())) > 0 && (skillEffect8 = player.getSkillEffect(mode == 1 ? 11120009 : 11120010)) != null) {
            skillEffect8.unprimaryPassiveApplyTo(player);
        }
        if (player.getCheatTracker().canNextBonusAttack(5000L) && player.getBuffStatValueHolder(SecondaryStat.TempSecondaryStat, 400011005) != null) {
            if (player.getBuffedIntValue(SecondaryStat.PoseType) == 1) {
                player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400011022, 0, Collections.emptyList()));
            } else {
                player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400011023, 0, Collections.emptyList()));
            }
        }
        if (player.getBuffStatValueHolder(11101031) != null) {
            if (player.getBuffStatValueHolder(11001024) != null) {
                player.dispelEffect(11001024);
                player.getSkillEffect(11001025).applyTo(player);
            } else {
                player.dispelEffect(11001025);
                player.getSkillEffect(11001024).applyTo(player);
            }
        }
        if (applier.effect != null && applier.effect.getSourceId() == 400011056) {
            MapleSummon summonBySkillID = player.getSummonBySkillID(400011065);
            if (summonBySkillID != null) {
                Rectangle a = applier.effect.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 500);
                if (a.contains(summonBySkillID.getPosition())) {
                    summonBySkillID.setAcState1(summonBySkillID.getAcState1() + 1);
                    player.getMap().broadcastMessage(player, SummonPacket.SummonedSkillState(summonBySkillID, 1), true);
                }
                player.getSummonsOIDsBySkillID(400011065).size();
                return 1;
            }
            player.getSkillEffect(400011065).applyTo(player, new Point(player.getPosition().x + (player.isFacingLeft() ? -100 : 100), player.getPosition().y));
        }
        return 1;
    }
}

