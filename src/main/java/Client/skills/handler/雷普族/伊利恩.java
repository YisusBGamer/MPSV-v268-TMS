/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.雷普族;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.maps.MapleSummon;
import Net.server.quest.MapleQuest;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 伊利恩
extends AbstractSkillHandler {
    public 伊利恩() {
        this.jobs = new MapleJob[]{MapleJob.伊利恩, MapleJob.伊利恩1轉, MapleJob.伊利恩2轉, MapleJob.伊利恩3轉, MapleJob.伊利恩4轉};
        for (Field field : Config.constants.skills.伊利恩.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{150000079, 150001016, 150001005}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 150001005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        if (chr.getQuestStatus(34900) != 2) {
            MapleQuest.getInstance(34900).forceComplete(chr, 0);
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 152141501: 
            case 152141502: 
            case 152141503: 
            case 152141504: 
            case 152141505: 
            case 152141506: {
                return 152141500;
            }
            case 152141001: {
                return 152141000;
            }
            case 152141006: {
                return 152141004;
            }
            case 152141002: {
                return 152001001;
            }
            case 152141005: {
                return 152110004;
            }
            case 152001005: {
                return 152001004;
            }
            case 152000009: {
                return 152000007;
            }
            case 152000010: {
                return 152100012;
            }
            case 152121006: {
                return 152121005;
            }
            case 152101000: 
            case 152101004: {
                return 152101003;
            }
            case 152001002: 
            case 152110004: {
                return 152001001;
            }
            case 152120002: 
            case 152120003: {
                return 152120001;
            }
            case 152120017: {
                return 152120008;
            }
            case 400021062: {
                return 400021061;
            }
            case 400021064: 
            case 400021065: {
                return 400021063;
            }
            case 400021100: 
            case 400021111: {
                return 400021099;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 80000268: 
            case 150000017: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.LefMageLinkSkill, 1);
                return 1;
            }
            case 150001005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 152101000: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                return 1;
            }
            case 152000009: {
                statups.put(SecondaryStat.LefBuffMastery, 1);
                statups.put(SecondaryStat.IndiePAD, 0);
                statups.put(SecondaryStat.IndieMAD, 0);
                statups.put(SecondaryStat.IndieBooster, 0);
                return 1;
            }
            case 152000010: {
                monsterStatus.put(MonsterStatus.LefDebuff, 1);
                return 1;
            }
            case 152101007: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 152121011: {
                statups.put(SecondaryStat.IndieDamReduceR, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.LefFastCharge, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 152121043: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 152001002: 
            case 152120003: {
                effect.getInfo().put(MapleStatInfo.time, 2000);
                statups.put(SecondaryStat.NextSpecificSkillDamageUp, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 152001005: {
                effect.getInfo().put(MapleStatInfo.time, 1000);
                statups.put(SecondaryStat.NewFlying, 1);
                return 1;
            }
            case 152111003: {
                statups.put(SecondaryStat.NewFlying, 1);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.LefGloryWing, 1);
                return 1;
            }
            case 152111014: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.TeleportMasteryRange, 1);
                return 1;
            }
            case 152001003: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 152120014: {
                effect.getInfo().put(MapleStatInfo.time, 10000);
                statups.put(SecondaryStat.CrystalChargeBuffIcon, 1);
                return 1;
            }
            case 152111007: {
                effect.getInfo().put(MapleStatInfo.time, 15000);
                statups.put(SecondaryStat.HarmonyLink, 1);
                return 1;
            }
            case 152120017: {
                effect.getInfo().put(MapleStatInfo.bulletCount, 5);
                return 1;
            }
            case 152121042: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400021061: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400021062: {
                effect.getInfo().put(MapleStatInfo.cooltime, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 152001001: 
            case 152110004: 
            case 152120001: {
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, Collections.singletonList(0))), true);
                return 1;
            }
            case 152101003: {
                MapleSummon summon = chr.getSummonBySkillID(152101000);
                MapleSummon summon1 = chr.getSummonBySkillID(152101008);
                Point point3 = new Point(chr.getPosition().x + (chr.isFacingLeft() ? -360 : 360), chr.getPosition().y);
                if (summon != null) {
                    c.announce(SummonPacket.SummonedForceMove(summon, 152101003, chr.getSkillLevel(152101003), point3));
                }
                if (summon1 != null) {
                    c.announce(SummonPacket.SummonedForceMove(summon1, 152101008, chr.getSkillLevel(152101008), point3));
                }
                return 1;
            }
            case 152101004: {
                MapleSummon summonBySkillID4 = chr.getSummonBySkillID(152101000);
                if (summonBySkillID4 != null) {
                    int ownerId = summonBySkillID4.getOwnerId();
                    int objectId = summonBySkillID4.getObjectId();
                    Point position = chr.getPosition();
                    c.announce(SummonPacket.SummonedForceReturn(ownerId, objectId, position));
                }
                return 1;
            }
            case 152121005: {
                MapleSummon summon = chr.getSummonBySkillID(152101000);
                if (summon != null) {
                    summon.setState(2, 0);
                    c.announce(SummonPacket.SummonedSkillState(summon, 2));
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 150001016: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 152101000: {
                return 1;
            }
            case 152001003: {
                applier.localstatups.remove(SecondaryStat.Speed);
                return 1;
            }
            case 152000009: {
                int n = 0;
                MapleStatEffect eff = applyto.getSkillEffect(152000007);
                if (eff != null) {
                    n = eff.getX();
                }
                if ((eff = applyto.getSkillEffect(152110008)) != null) {
                    n = eff.getX();
                }
                if ((eff = applyto.getSkillEffect(152120012)) != null) {
                    n = eff.getX();
                }
                if (n <= 0) {
                    return 0;
                }
                int value = 2 * applier.effect.getX() + 4 * applier.effect.getY() + 6 * applier.effect.getZ() + applier.effect.getW() * 10;
                applier.localstatups.put(SecondaryStat.LefBuffMastery, applier.effect.getLevel());
                applier.localstatups.put(SecondaryStat.IndiePAD, value);
                applier.localstatups.put(SecondaryStat.IndieMAD, value);
                applier.localstatups.put(SecondaryStat.IndieBooster, -applier.effect.getW());
                if (!applier.passive) {
                    return 1;
                }
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.LefBuffMastery);
                if (mbsvh != null) {
                    applier.duration = mbsvh.getLeftTime();
                    return 1;
                }
                applier.overwrite = false;
                applier.localstatups.clear();
                return 1;
            }
            case 152121043: {
                applier.duration += applyto.getBuffedIntValue(SecondaryStat.LefBuffMastery) * 1000;
                return 1;
            }
            case 152111003: {
                if (!applier.passive) {
                    applier.buffz = 1;
                    return 1;
                }
                applier.buffz = 0;
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.LefGloryWing);
                if (mbsvh != null) {
                    applier.duration = mbsvh.getLeftTime();
                }
                return 1;
            }
            case 152100011: {
                applier.b5 = false;
                applyto.getClient().announce(EffectPacket.showBlessOfDarkness(-1, applier.effect.getSourceId()));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showBlessOfDarkness(applyto.getId(), applier.effect.getSourceId()), false);
                return 1;
            }
            case 152121005: {
                applyto.dispelBuff(152001003);
                applyto.dispelBuff(152101008);
                return 1;
            }
            case 152121042: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(151121042);
                applyto.dispelEffect(152121042);
                applyto.dispelEffect(155121042);
                applyto.dispelEffect(154121042);
                return 1;
            }
            case 400021068: {
                int n43 = applyto.getBuffedIntValue(SecondaryStat.CannonShooter_BFCannonBall) + (applier.passive ? 1 : -1);
                SecondaryStatValueHolder buffStatValueHolder30 = applyto.getBuffStatValueHolder(SecondaryStat.CannonShooter_BFCannonBall);
                if (n43 < 0 || applier.primary && buffStatValueHolder30 != null && System.currentTimeMillis() < buffStatValueHolder30.startTime + (long)applier.effect.getQ() * 1000L && applier.passive || n43 > applier.effect.getY()) {
                    return 0;
                }
                applier.duration = 2100000000;
                applier.localstatups.put(SecondaryStat.CannonShooter_BFCannonBall, n43);
                if (applier.passive && !applier.primary) {
                    applier.pos = applyto.getSummonBySkillID(152101000).getPosition();
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat17;
        MapleSummon summonBySkillID3;
        MapleSummon summonBySkillID2;
        MapleStatEffect eff = player.getEffectForBuffStat(SecondaryStat.LefGloryWing);
        if (eff != null && applier.effect != null && applier.effect.getSourceId() == 152121007) {
            eff.unprimaryPassiveApplyTo(player);
        }
        if (applier.effect != null && applier.effect.getSourceId() == 152101006 && (summonBySkillID2 = player.getSummonBySkillID(152101000)) != null) {
            summonBySkillID2.setState(0, 0);
            player.getClient().announce(SummonPacket.SummonedSkillState(summonBySkillID2, 2));
        }
        if (applier.effect != null && (applier.effect.getSourceId() == 152100002 || applier.effect.getSourceId() == 152110002)) {
            player.registerSkillCooldown(applier.effect, true);
        }
        if (applier.effect != null && applier.effect.getSourceId() == 400021062 && (summonBySkillID3 = player.getSummonBySkillID(152101000)) != null) {
            player.registerSkillCooldown(applier.effect, true);
            player.getClient().announce(SummonPacket.SummonedStateChange(summonBySkillID3, 3, 0, 0));
        }
        if ((effecForBuffStat17 = player.getEffectForBuffStat(SecondaryStat.LPMagicCircuitFullDrive)) != null && applier.effect != null && applier.effect.getSourceId() != 400001038 && player.getCheatTracker().canNextBonusAttack((long)effecForBuffStat17.getX() * 1000L)) {
            player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400001038, 0, Collections.emptyList()));
            effecForBuffStat17.unprimaryPassiveApplyTo(player);
        }
        return 1;
    }
}

