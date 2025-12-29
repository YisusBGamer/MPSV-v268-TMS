/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.皇家騎士團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Opcode.header.OutHeader;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;

public class 閃雷悍將
extends AbstractSkillHandler {
    public 閃雷悍將() {
        this.jobs = new MapleJob[]{MapleJob.閃雷悍將1轉, MapleJob.閃雷悍將2轉, MapleJob.閃雷悍將3轉, MapleJob.閃雷悍將4轉};
        for (Field field : Config.constants.skills.皇家騎士團_技能群組.閃雷悍將.class.getDeclaredFields()) {
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
            case 15141501: {
                return 15141500;
            }
            case 15141000: 
            case 15141001: {
                return 15121001;
            }
            case 500004076: {
                return 400051007;
            }
            case 500004077: {
                return 400051016;
            }
            case 500004078: {
                return 400051044;
            }
            case 500004079: {
                return 400051058;
            }
            case 15101026: {
                return 15101021;
            }
            case 400051013: {
                return 400051007;
            }
            case 400051045: {
                return 400051044;
            }
            case 400051059: 
            case 400051060: 
            case 400051061: 
            case 400051062: 
            case 400051063: 
            case 400051064: 
            case 400051065: 
            case 400051066: 
            case 400051067: {
                return 400051058;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 15121054: {
                statups.put(SecondaryStat.StrikerHyperElectric, 1);
                return 1;
            }
            case 15001022: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CygnusElementSkill, 1);
                statups.put(SecondaryStat.IgnoreTargetDEF, 5);
                return 1;
            }
            case 15121004: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ShadowPartner, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 15111022: 
            case 15120003: {
                effect.setOverTime(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 15100029: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 15121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 15121005: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400051007: {
                statups.put(SecondaryStat.LightningUnion, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400051044: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.StrikerComboStack, 0);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 15001022: {
                MapleStatEffect skillEffect12;
                if (!applier.passive) {
                    return 1;
                }
                applier.localstatups.clear();
                applier.b3 = false;
                applier.maskedDuration = 30000;
                applier.buffz = Math.min(applyto.getBuffedIntZ(SecondaryStat.IgnoreTargetDEF) + 1, applier.effect.getAttackCount(applyto));
                applier.maskedstatups.put(SecondaryStat.IgnoreTargetDEF, applier.buffz * (applyto.getSkillEffect(15121054) != null ? 9 : applier.effect.getX()));
                SecondaryStatValueHolder buffStatValueHolder11 = applyto.getBuffStatValueHolder(SecondaryStat.CygnusElementSkill);
                if (buffStatValueHolder11 != null) {
                    applier.duration = buffStatValueHolder11.getLeftTime();
                }
                if ((skillEffect12 = applyto.getSkillEffect(15121004)) != null && applyto.isSkillCooling(15121004)) {
                    applyto.reduceSkillCooldown(15121004, skillEffect12.getY() * 1000);
                }
                return 1;
            }
            case 15111022: 
            case 15120003: {
                applier.localstatups.put(SecondaryStat.IndieDamR, applyto.getBuffedIntZ(SecondaryStat.IgnoreTargetDEF) * applier.effect.getY());
                return 1;
            }
            case 15121054: {
                applyto.cancelSkillCooldown(15111022);
                applyto.cancelSkillCooldown(15120003);
                return 1;
            }
            case 15121053: {
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
            case 400051007: {
                if (applier.passive) {
                    return 0;
                }
                return 1;
            }
            case 400051044: {
                applier.duration = 2100000000;
                applier.localstatups.put(SecondaryStat.StrikerComboStack, Math.min(applier.effect.getX(), Math.max(0, applyto.getBuffedIntValue(SecondaryStat.StrikerComboStack) + 1)));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat9;
        if (this.containsJob(applyfrom.getJobWithSub()) && (effecForBuffStat9 = applyfrom.getEffectForBuffStat(SecondaryStat.CygnusElementSkill)) != null && effecForBuffStat9.makeChanceResult(applyfrom) && applier.effect != null && applier.effect.getSourceId() != 15111022 && applier.effect.getSourceId() != 15120003 && applier.effect.getSourceId() != 15121001) {
            effecForBuffStat9.unprimaryPassiveApplyTo(applyfrom);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect eff = player.getEffectForBuffStat(SecondaryStat.LightningUnion);
        if (applier.effect != null && applier.totalDamage > 0L && eff != null) {
            MapleClient client = player.getClient();
            int sourceID = applier.effect.getSourceId();
            int sourceID2 = eff.getSourceId();
            int level = eff.getLevel();
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.ThunderSkillAction.getValue());
            mplew.writeInt(sourceID);
            mplew.writeInt(sourceID2);
            mplew.writeInt(level);
            mplew.writeInt(0);
            client.announce(mplew.getPacket());
        }
        MapleStatEffect skillEffect13 = player.getSkillEffect(400051044);
        if (applier.effect != null && applier.totalDamage > 0L && applier.ai.raytheonPike > 0 && skillEffect13 != null) {
            if (player.getBuffedIntValue(SecondaryStat.StrikerComboStack) >= 8) {
                LinkedList<ExtraSkill> eskills = new LinkedList<ExtraSkill>();
                for (int i = 0; i < 7; ++i) {
                    ExtraSkill eskill = new ExtraSkill(i == 0 ? 400051044 : 400051045, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    eskills.add(eskill);
                }
                player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(400051044, eskills));
                player.dispelEffect(SecondaryStat.StrikerComboStack);
                return 1;
            }
            skillEffect13.unprimaryPassiveApplyTo(player);
        }
        return 1;
    }
}

