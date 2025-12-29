/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.client.party.PartyMember
 */
package Client.skills.handler.阿尼瑪族;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.force.MapleForceFactory;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleSummon;
import Net.server.quest.MapleQuest;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import SwordieX.client.party.PartyMember;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 虎影
extends AbstractSkillHandler {
    public 虎影() {
        this.jobs = new MapleJob[]{MapleJob.虎影, MapleJob.虎影1轉, MapleJob.虎影2轉, MapleJob.虎影3轉, MapleJob.虎影4轉};
        for (Field field : Config.constants.skills.虎影.class.getDeclaredFields()) {
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
        int[] fixskills;
        Skill skil;
        int[] ss;
        for (int i : ss = new int[]{160001074, 160000000, 160001075, 160001005}) {
            if (chr.getLevel() < 200 && i == 160001005) continue;
            int skillLevel = 1;
            skil = SkillFactory.getSkill(i);
            if (skil == null || chr.getSkillLevel(skil) >= skillLevel) continue;
            applier.skillMap.put(i, new SkillEntry(skillLevel, skil.getMaxMasterLevel(), -1L));
        }
        for (int f : fixskills = new int[]{164001004}) {
            skil = SkillFactory.getSkill(f);
            if (chr.getJob() < f / 10000 || skil == null || chr.getSkillLevel(skil) > 0 || chr.getMasterLevel(skil) > 0) continue;
            applier.skillMap.put(f, new SkillEntry(0, skil.getMasterLevel() == 0 ? skil.getMaxLevel() : skil.getMasterLevel(), SkillFactory.getDefaultSExpiry(skil)));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 164141501: 
            case 164141502: {
                return 164141500;
            }
            case 164141000: 
            case 164141002: 
            case 164141003: 
            case 164141004: {
                return 164121000;
            }
            case 164141005: 
            case 164141007: 
            case 164141008: 
            case 164141009: 
            case 164141010: {
                return 164111003;
            }
            case 164141011: 
            case 164141012: 
            case 164141013: {
                return 164121003;
            }
            case 500004180: {
                return 400041048;
            }
            case 500004181: {
                return 400041050;
            }
            case 500004182: {
                return 400041052;
            }
            case 500004183: {
                return 400041063;
            }
            case 164001002: {
                return 164001001;
            }
            case 164100000: 
            case 164101001: 
            case 164101002: {
                return 164101000;
            }
            case 164101004: {
                return 164101003;
            }
            case 164101006: {
                return 164100006;
            }
            case 164110000: 
            case 164111001: 
            case 164111002: 
            case 164111009: 
            case 164111010: 
            case 164111015: {
                return 164111000;
            }
            case 164110003: 
            case 164111004: 
            case 164111005: 
            case 164111006: 
            case 164111016: {
                return 164111003;
            }
            case 164120000: 
            case 164121001: 
            case 164121002: 
            case 164121013: {
                return 164121000;
            }
            case 164121004: {
                return 164121003;
            }
            case 164121011: 
            case 164121012: {
                return 164121006;
            }
            case 164121015: {
                return 164121008;
            }
            case 164120007: {
                return 164121007;
            }
            case 164121044: {
                return 164121043;
            }
            case 400041049: {
                return 400041048;
            }
            case 400041051: {
                return 400041050;
            }
            case 400041053: {
                return 400041052;
            }
            case 400041064: 
            case 400041065: 
            case 400041066: 
            case 400041067: 
            case 400041068: {
                return 400041063;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 160001005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 164101005: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 164001001: {
                monsterStatus.put(MonsterStatus.MobLock, 1);
                return 1;
            }
            case 164001004: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.time) / 1000);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.NewFlying, 1);
                return 1;
            }
            case 164100012: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.time) / 1000);
                return 1;
            }
            case 164101006: {
                statups.put(SecondaryStat.DarkSight, 1);
                return 1;
            }
            case 164101003: {
                statups.put(SecondaryStat.AnimaThiefCloneAttack, 1);
                return 1;
            }
            case 164111003: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 164121041: {
                effect.getInfo().put(MapleStatInfo.subTime, effect.getInfo().get((Object)MapleStatInfo.subTime) / 1000);
                statups.put(SecondaryStat.IndiePMdR, effect.getIndiePMdR());
                statups.put(SecondaryStat.MiracleDrug, 1);
                return 1;
            }
            case 164121042: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.updatableTime));
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 164121044: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 164121007: {
                statups.put(SecondaryStat.IndiePMdR, effect.getIndiePMdR());
                statups.put(SecondaryStat.AnimaThiefButterfly, 1);
                return 1;
            }
            case 164111008: {
                monsterStatus.put(MonsterStatus.Morph, 1);
                monsterStatus.put(MonsterStatus.IndiePDR, effect.getX());
                return 1;
            }
            case 400041052: {
                statups.put(SecondaryStat.AnimaThiefMetaphysics, 1);
                statups.put(SecondaryStat.IndieDamR, effect.getIndieDamR());
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 400041048: {
                statups.put(SecondaryStat.AnimaThiefFifthCloneAttack, 1);
                return 1;
            }
            case 400041050: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 164111002: {
                MapleStatEffect skillEffect = chr.getSkillEffect(164101006);
                if (skillEffect != null) {
                    skillEffect.unprimaryPassiveApplyTo(chr);
                }
                return 1;
            }
            case 160001075: {
                chr.setQuestAdd(MapleQuest.getInstance(7786), (byte)0, "sw=1");
                String statData = chr.getOneInfo(7786, "sw");
                statData = statData == null || statData.equals("0") ? String.valueOf(1) : String.valueOf(0);
                chr.updateOneInfo(7786, "sw", statData, true);
                chr.getMap().broadcastMessage(MaplePacketCreator.showHoyoungHide(chr.getId(), Integer.valueOf(statData) == 1));
                return 1;
            }
            case 164121006: 
            case 164121011: {
                applier.pos = slea.readPos();
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 160001074: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 164001004: {
                applier.buffz = 50 + applyfrom.getStat().getSkillCustomVal(164001004);
                return 1;
            }
            case 164101006: {
                applyto.send(EffectPacket.showBuffEffect(applyto, false, applier.effect.getSourceId(), applier.effect.getLevel(), 1, null));
                return 1;
            }
            case 164121006: 
            case 164121011: 
            case 164121012: {
                applier.overwrite = false;
                applyto.removeSummon(164121011);
                return 1;
            }
            case 164121015: {
                MapleSummon summon = applyto.getSummonBySkillID(164121008);
                if (summon != null && summon.getAcState2() > 0) {
                    MapleStatEffect skillEffect = applyto.getSkillEffect(164121008);
                    if (skillEffect == null) {
                        return 1;
                    }
                    int hpMpHeal = summon.getAcState2() * skillEffect.getY();
                    if (applyto.getParty() == null) {
                        applyto.addHPMP(hpMpHeal, hpMpHeal);
                    } else {
                        for (PartyMember mpc : applyto.getParty().getMembers()) {
                            MapleCharacter pchr = mpc.getChr();
                            if (pchr == null) continue;
                            pchr.addHPMP(hpMpHeal, hpMpHeal);
                        }
                    }
                    applyto.send(EffectPacket.showHoYoungHeal(-1, applier.effect.getSourceId(), summon.getPosition(), summon.getAcState2()));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showHoYoungHeal(applyto.getId(), applier.effect.getSourceId(), summon.getPosition(), summon.getAcState2()), false);
                }
                applyto.dispelEffect(164121008);
                return 1;
            }
            case 400041052: {
                if (applier.att) {
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleSummon summon;
        if (applier.effect != null && applier.effect.getSourceId() == 164121008 && (summon = applyfrom.getSummonBySkillID(applier.effect.getSourceId())) != null) {
            if (summon.getAcState1() < applier.effect.getZ()) {
                summon.setAcState1(summon.getAcState1() + (applyto.isBoss() ? applier.effect.getW() : 1));
            } else {
                summon.setAcState1(0);
                summon.setAcState2(Math.min(summon.getAcState2() + 1, applier.effect.getX()));
            }
            applyfrom.getMap().broadcastMessage(applyfrom, SummonPacket.SummonedStateChange(summon, 2, summon.getAcState1(), summon.getAcState2()), true);
            applyfrom.getMap().broadcastMessage(applyfrom, SummonPacket.SummonedSpecialEffect(summon, 3), true);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect skillEffect;
        MapleStatEffect effect = player.getEffectForBuffStat(SecondaryStat.DarkSight);
        if (effect != null) {
            if (effect.getSourceId() != 9001004) {
                player.dispelEffect(SecondaryStat.DarkSight);
            }
        } else if (applier.effect != null && (skillEffect = player.getSkillEffect(164101006)) != null) {
            switch (applier.effect.getSourceId()) {
                case 164101002: 
                case 164111006: {
                    skillEffect.unprimaryPassiveApplyTo(player);
                }
            }
        }
        if (applier.effect != null && applier.ai.mobCount > 0) {
            int i;
            List<MapleMapObject> mobs;
            if (applier.effect.getAtSkillType() > 0 && player.getSkillEffect(164000010) != null) {
                int state = player.handleHoYoungState(applier.effect.getAtSkillType());
                int runeHeal = 5 * state + 5;
                player.handleHoYoungValue(runeHeal, 0);
            }
            MapleForceFactory mff = MapleForceFactory.getInstance();
            if (player.getBuffStatValueHolder(SecondaryStat.AnimaThiefCloneAttack) != null && player.getCheatTracker().canNext幻影分身符() && (effect = (effect = player.getEffectForBuffStat(SecondaryStat.AnimaThiefFifthCloneAttack)) == null ? player.getSkillEffect(164101004) : player.getSkillEffect(400041049)) != null) {
                mobs = player.getMap().getMapObjectsInRect(effect.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 100), Collections.singletonList(MapleMapObjectType.MONSTER));
                ArrayList<Integer> list2 = new ArrayList<Integer>();
                if (!mobs.isEmpty()) {
                    for (i = 0; i < effect.getY(); ++i) {
                        list2.add(mobs.get(Randomizer.nextInt(mobs.size())).getObjectId());
                    }
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, effect, 0, list2)), true);
                }
            }
            if (player.getBuffStatValueHolder(SecondaryStat.AnimaThiefButterfly) != null && applier.effect.getSourceId() != 164120007 && player.getCheatTracker().canNext蝶梦() && (effect = player.getSkillEffect(164120007)) != null && (mobs = player.getMap().getMapObjectsInRange(player.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER))).size() > 0) {
                ArrayList<Integer> mobOids = new ArrayList<Integer>();
                for (i = 0; i < player.getSkillEffect(164121007).getU2() && i < mobs.size(); ++i) {
                    mobOids.add(mobs.get(Randomizer.nextInt(mobs.size())).getObjectId());
                }
                player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, effect, 0, mobOids, player.getPosition())), true);
            }
        }
        return 1;
    }
}

