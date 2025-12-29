/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.超新星;

import Client.MapleCharacter;
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
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.quest.MapleQuest;
import Opcode.header.OutHeader;
import Packet.BuffPacket;
import Packet.EffectPacket;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;

public class 卡蒂娜
extends AbstractSkillHandler {
    public 卡蒂娜() {
        this.jobs = new MapleJob[]{MapleJob.卡蒂娜, MapleJob.卡蒂娜1轉, MapleJob.卡蒂娜2轉, MapleJob.卡蒂娜3轉, MapleJob.卡蒂娜4轉};
        for (Field field : Config.constants.skills.卡蒂娜.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{60021005, 60021217, 60020216}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 60021005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        if (chr.getQuestStatus(34624) != 2) {
            MapleQuest.getInstance(34624).forceComplete(chr, 0);
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 64141501: 
            case 64141502: {
                return 64141500;
            }
            case 64141001: 
            case 64141002: 
            case 64141003: {
                return 64141000;
            }
            case 500004144: {
                return 400041035;
            }
            case 500004145: {
                return 400041033;
            }
            case 500004146: {
                return 400041041;
            }
            case 500004147: {
                return 400041074;
            }
            case 64101009: {
                return 64100004;
            }
            case 64111013: {
                return 64110005;
            }
            case 64121020: {
                return 64120006;
            }
            case 64001014: {
                return 64001003;
            }
            case 64001013: {
                return 64001002;
            }
            case 400041034: {
                return 400041033;
            }
            case 400041036: {
                return 400041035;
            }
            case 64121012: 
            case 64121013: 
            case 64121014: 
            case 64121015: 
            case 64121017: 
            case 64121018: 
            case 64121019: {
                return 64121001;
            }
            case 64121011: 
            case 64121016: {
                return 64121003;
            }
            case 64121022: 
            case 64121023: 
            case 64121024: {
                return 64121021;
            }
            case 64111012: {
                return 64111004;
            }
            case 64101008: {
                return 64101002;
            }
            case 64001007: 
            case 64001008: 
            case 64001009: 
            case 64001010: 
            case 64001011: 
            case 64001012: {
                return 64001000;
            }
            case 64001006: {
                return 64001001;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 60021005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 64001001: {
                effect.getInfo().put(MapleStatInfo.time, 1000);
                statups.put(SecondaryStat.NextAttackEnhance, effect.getLevel());
                return 1;
            }
            case 64101003: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 64121002: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 64121053: {
                statups.put(SecondaryStat.TempSecondaryStat, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 64100004: 
            case 64110005: 
            case 64120006: {
                statups.put(SecondaryStat.WeaponVariety, 1);
                return 1;
            }
            case 64001010: 
            case 64001011: {
                effect.getInfo().put(MapleStatInfo.time, 500);
                statups.put(SecondaryStat.DarkSight, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 64111003: {
                monsterStatus.put(MonsterStatus.PDR, effect.getW());
                return 1;
            }
            case 64121001: {
                effect.getInfo().put(MapleStatInfo.time, 90000);
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 400041035: {
                statups.put(SecondaryStat.ChainArtsFury, effect.getInfo().get((Object)MapleStatInfo.w2));
                return 1;
            }
            case 64121016: {
                effect.getInfo().put(MapleStatInfo.time, 15000);
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 400041074: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.WeaponVarietyFinale, 0);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 60021217: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 64100004: {
                applier.localstatups.put(SecondaryStat.WeaponVariety, Math.min(3, applyto.getBuffedIntValue(SecondaryStat.WeaponVariety) + 1));
                if (applyto.getCheatTracker().canNextBonusAttack(500L)) {
                    applyto.getClient().announce(MaplePacketCreator.userBonusAttackRequest(64101009, 0, Collections.emptyList()));
                }
                return 1;
            }
            case 64110005: {
                applier.localstatups.put(SecondaryStat.WeaponVariety, Math.min(6, applyto.getBuffedIntValue(SecondaryStat.WeaponVariety) + 1));
                if (applyto.getCheatTracker().canNextBonusAttack(500L)) {
                    applyto.getClient().announce(MaplePacketCreator.userBonusAttackRequest(64111013, 0, Collections.emptyList()));
                }
                return 1;
            }
            case 64120006: {
                applier.localstatups.put(SecondaryStat.WeaponVariety, Math.min(8, applyto.getBuffedIntValue(SecondaryStat.WeaponVariety) + 1));
                if (applyto.getCheatTracker().canNextBonusAttack(500L)) {
                    applyto.getClient().announce(MaplePacketCreator.userBonusAttackRequest(64121020, 0, Collections.emptyList()));
                }
                return 1;
            }
            case 64001010: 
            case 64001011: {
                applier.b3 = true;
                return 1;
            }
            case 400041074: {
                if (!applier.primary) {
                    return 0;
                }
                applier.buffz = 0;
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applyto != null) {
            if (applyfrom.getEffectForBuffStat(SecondaryStat.ChainArtsFury) != null && applyfrom.getCheatTracker().canNextElementalFocus()) {
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.ChainRageAction.getValue());
                mplew.writeInt(applyto.getPosition().x);
                mplew.writeInt(applyto.getPosition().y);
                applyfrom.getClient().announce(mplew.getPacket());
            }
            if (applier.effect != null && applier.effect.getSourceId() == 64001009) {
                applyfrom.getClient().announce(EffectPacket.showBuffEffect(applyfrom, false, 64001009, applier.effect.getLevel(), applyto.getObjectId(), applyto.getPosition()));
                applyfrom.getMap().broadcastMessage(applyfrom, EffectPacket.showBuffEffect(applyfrom, true, 64001009, applier.effect.getLevel(), applyto.getObjectId(), applyfrom.getPosition()), applyto.getPosition());
            }
        }
        return 1;
    }

    public static boolean eG(int n) {
        switch (n) {
            case 64001002: 
            case 64101001: 
            case 64101002: 
            case 64111002: 
            case 64111003: 
            case 64111004: 
            case 64121003: 
            case 64121021: {
                return true;
            }
        }
        return false;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        int lastId;
        MapleStatEffect weaponChangeEffect;
        int linkId = SkillConstants.getLinkedAttackSkill(applier.effect.getSourceId());
        if (卡蒂娜.eG(linkId) && (weaponChangeEffect = player.getSkillEffect(64120006) != null ? player.getSkillEffect(64120006) : (player.getSkillEffect(64110005) != null ? player.getSkillEffect(64110005) : player.getSkillEffect(64100004))) != null && !player.isSkillCooling(weaponChangeEffect.getSourceId()) && (lastId = SkillConstants.getLinkedAttackSkill(player.getCheatTracker().getLastAttackSkill())) != linkId) {
            weaponChangeEffect.applyTo(player);
        }
        SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.WeaponVarietyFinale);
        if (applier.totalDamage > 0L && mbsvh != null && mbsvh.effect != null) {
            if (linkId == 64121002 || linkId == 64121001 || linkId == 64121052 || linkId == 400041035) {
                mbsvh.startTime = mbsvh.startTime - (linkId == 400041035 ? 1000L : 2000L);
            }
            if (linkId == 64100004 || linkId == 64110005 || linkId == 64120006) {
                boolean update = false;
                if (mbsvh.z >= 3) {
                    if (mbsvh.value > 0) {
                        mbsvh.value = Math.max(0, mbsvh.value - 1);
                        mbsvh.startTime = System.currentTimeMillis();
                        mbsvh.z = 0;
                        update = true;
                        ExtraSkill eskill = new ExtraSkill(400041074, player.getPosition());
                        eskill.Value = 5;
                        eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                        player.send(MaplePacketCreator.RegisterExtraSkill(400041074, Collections.singletonList(eskill)));
                    }
                } else {
                    mbsvh.z = Math.min(3, mbsvh.z + 1);
                    update = true;
                }
                if (update) {
                    player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(SecondaryStat.WeaponVarietyFinale, mbsvh.effect.getSourceId())));
                }
            }
        }
        return 1;
    }
}

