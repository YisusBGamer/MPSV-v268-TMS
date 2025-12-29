/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.雷普族;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Opcode.header.OutHeader;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 亞克
extends AbstractSkillHandler {
    public 亞克() {
        this.jobs = new MapleJob[]{MapleJob.亞克, MapleJob.亞克1轉, MapleJob.亞克2轉, MapleJob.亞克3轉, MapleJob.亞克4轉};
        for (Field field : Config.constants.skills.亞克.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{150010079, 155101006, 150011005}) {
            if (chr.getLevel() < 200 && i == 150011005) continue;
            Skill skil = SkillFactory.getSkill(i);
            if (chr.getJob() < i / 10000 || skil == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 155141501: {
                return 155141500;
            }
            case 155141000: {
                return 155001100;
            }
            case 155001008: {
                return 155000007;
            }
            case 155001000: 
            case 155001001: {
                return 155001103;
            }
            case 155001009: 
            case 155001204: 
            case 155001205: {
                return 155001104;
            }
            case 155121041: {
                return 155121341;
            }
            case 155121006: 
            case 155121007: {
                return 155121306;
            }
            case 155121002: 
            case 155121003: 
            case 155121004: 
            case 155121005: 
            case 155121202: 
            case 155121215: {
                return 155121102;
            }
            case 155120001: {
                return 155120000;
            }
            case 155111006: {
                return 155111306;
            }
            case 155111003: 
            case 155111004: 
            case 155111005: 
            case 155111111: 
            case 155111202: 
            case 155111211: 
            case 155111212: {
                return 155111102;
            }
            case 155110001: {
                return 155110000;
            }
            case 155100009: {
                return 155101008;
            }
            case 155101114: 
            case 155101204: 
            case 155101214: {
                return 155101104;
            }
            case 155001202: {
                return 155001102;
            }
            case 155101002: 
            case 155101003: 
            case 155101013: 
            case 155101015: 
            case 155101101: 
            case 155101112: 
            case 155101200: 
            case 155101201: 
            case 155101212: {
                return 155101100;
            }
            case 400051035: {
                return 400051334;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 80000514: 
            case 150010241: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.q));
                statups.put(SecondaryStat.LPBattleMode, 1);
                return 1;
            }
            case 150011005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 155000007: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.SpecterMode, 1);
                return 1;
            }
            case 155101008: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ComingDeath, 1);
                return 1;
            }
            case 155101005: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 155121341: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 155121043: {
                statups.put(SecondaryStat.LPSpellAmplification, 1);
                return 1;
            }
            case 155120014: {
                statups.put(SecondaryStat.CombatFrenzy, 1);
                return 1;
            }
            case 155111306: {
                statups.put(SecondaryStat.NotDamaged, 1);
                statups.put(SecondaryStat.KeyDownMoving, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 155121306: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 155121003: {
                effect.getInfo().put(MapleStatInfo.bulletCount, 8);
                return 1;
            }
            case 155111003: {
                effect.getInfo().put(MapleStatInfo.bulletCount, 6);
                return 1;
            }
            case 155101002: {
                effect.getInfo().put(MapleStatInfo.bulletCount, 8);
                return 1;
            }
            case 155001000: {
                effect.getInfo().put(MapleStatInfo.bulletCount, 2);
                return 1;
            }
            case 155001202: 
            case 155101200: 
            case 155110001: 
            case 155111212: 
            case 155120001: 
            case 155121202: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.w).intValue() / 100.0);
                return 1;
            }
            case 155001001: {
                statups.put(SecondaryStat.Speed, effect.getInfo().get((Object)MapleStatInfo.speed));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                return 1;
            }
            case 155101003: {
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieCr, effect.getInfo().get((Object)MapleStatInfo.indieCr));
                return 1;
            }
            case 155111005: {
                statups.put(SecondaryStat.IndieEVAR, effect.getInfo().get((Object)MapleStatInfo.indieEvaR));
                statups.put(SecondaryStat.IndieBooster, 1);
                return 1;
            }
            case 155121005: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieBDR, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.indieIgnoreMobpdpR));
                return 1;
            }
            case 155001205: {
                statups.put(SecondaryStat.NewFlying, 1);
                effect.getInfo().put(MapleStatInfo.time, 3000);
                return 1;
            }
            case 155101006: {
                statups.put(SecondaryStat.SpecterMode, 1);
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                return 1;
            }
            case 155121042: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400051334: {
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 400051036: {
                statups.put(SecondaryStat.LPInfinitySpell, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 155101006: {
                MapleStatEffect skillEffect10 = chr.getSkillEffect(155000007);
                if (skillEffect10 == null) {
                    return 1;
                }
                if (chr.getBuffedIntValue(SecondaryStat.SpecterMode) > 0) {
                    chr.dispelEffect(155000007);
                    return 1;
                }
                skillEffect10.applyTo(chr);
                return 1;
            }
            case 155121006: {
                MapleStatEffect skillEffect6 = chr.getSkillEffect(155000007);
                if (skillEffect6 != null) {
                    skillEffect6.applyTo(chr);
                }
                return 1;
            }
            case 155001103: {
                int ao4 = slea.readByte();
                ArrayList<Integer> list16 = new ArrayList<Integer>();
                HashMap<Integer, Map<Integer, MapleForceAtom>> hashMap = new HashMap<Integer, Map<Integer, MapleForceAtom>>();
                for (int n1100 = 0; n1100 < ao4; ++n1100) {
                    list16.add(slea.readInt());
                }
                if (chr.getPureBeads() > 0) {
                    Map map2 = hashMap.computeIfAbsent(155001000, k -> new HashMap());
                    for (int n1101 = 0; n1101 < chr.getPureBeads(); ++n1101) {
                        map2.put(list16.size(), forceFactory.getMapleForce(chr, chr.getSkillEffect(155001000), 0));
                    }
                }
                if (chr.getFlameBeads() > 0) {
                    Map map3 = hashMap.computeIfAbsent(155101002, k -> new HashMap());
                    for (int n1102 = 0; n1102 < chr.getFlameBeads(); ++n1102) {
                        map3.put(list16.size(), forceFactory.getMapleForce(chr, chr.getSkillEffect(155101002), 0));
                    }
                }
                if (chr.getGaleBeads() > 0) {
                    Map map4 = hashMap.computeIfAbsent(155111003, k -> new HashMap());
                    for (int n1103 = 0; n1103 < chr.getGaleBeads(); ++n1103) {
                        map4.put(list16.size(), forceFactory.getMapleForce(chr, chr.getSkillEffect(155111003), 0));
                    }
                }
                if (chr.getAbyssBeads() > 0) {
                    Map map5 = hashMap.computeIfAbsent(155121003, k -> new HashMap());
                    for (int n1104 = 0; n1104 < chr.getAbyssBeads(); ++n1104) {
                        map5.put(list16.size(), forceFactory.getMapleForce(chr, chr.getSkillEffect(155121003), 0));
                    }
                }
                chr.getMap().broadcastMessage(chr, ForcePacket.showBeads(chr.getId(), hashMap), true);
                chr.addPureBeads(-5);
                chr.addFlameBeads(-5);
                chr.addGaleBeads(-5);
                chr.addAbyssBeads(-5);
                return 1;
            }
            case 155111207: {
                Map<Integer, Point> wreckagesMap = chr.getWreckagesMap();
                ArrayList<Integer> oids = new ArrayList<Integer>();
                for (MapleMapObject monster : chr.getMap().getMapObjectsInRange(chr.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER))) {
                    oids.add(monster.getObjectId());
                    if (oids.size() < applier.effect.getMobCount()) continue;
                    break;
                }
                if (!wreckagesMap.isEmpty() && !oids.isEmpty()) {
                    chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, oids, wreckagesMap.values())), true);
                    MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                    mplew.writeShort(OutHeader.LP_DEL_WRECKAGE.getValue());
                    mplew.writeInt(chr.getId());
                    mplew.writeInt(wreckagesMap.size());
                    mplew.write(0);
                    mplew.write(0);
                    wreckagesMap.keySet().forEach(mplew::writeInt);
                    chr.getMap().broadcastMessage(chr, mplew.getPacket(), true);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 80000514: 
            case 150010241: {
                int value = applyto.getBuffedIntValue(SecondaryStat.LPBattleMode);
                value = applier.passive ? Math.max(0, value - 1) : Math.min(applier.effect.getX(), value + 1);
                if (value > 0) {
                    applier.localstatups.put(SecondaryStat.IndieDamR, applier.localstatups.get(SecondaryStat.IndieDamR) + value * applier.effect.getY());
                    applier.localstatups.put(SecondaryStat.LPBattleMode, value);
                    return 1;
                }
                applier.overwrite = false;
                applier.localstatups.clear();
                return 1;
            }
            case 155101008: {
                if (applyto.getBuffedValue(SecondaryStat.ComingDeath) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 155121006: {
                applyto.getClient().announce(MaplePacketCreator.UserCreateAreaDotInfo(1, 155121006, applier.effect.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft())));
                return 1;
            }
            case 155120014: {
                applier.localstatups.put(SecondaryStat.CombatFrenzy, Math.min(applier.effect.getX(), applyto.getBuffedIntValue(SecondaryStat.CombatFrenzy) + 1));
                return 1;
            }
            case 155110000: 
            case 155120000: {
                applyto.registerSkillCooldown(applyto.getSkillEffect(155001102), true);
                return 1;
            }
            case 155121042: {
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
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null && applier.totalDamage > 0L) {
            switch (applier.effect.getSourceId()) {
                case 155001000: 
                case 155100009: 
                case 155101002: 
                case 155111003: 
                case 155111207: 
                case 155121003: {
                    break;
                }
                default: {
                    MapleStatEffect skillEffect26;
                    MapleStatEffect effecForBuffStat18;
                    if (player.getBuffedValue(SecondaryStat.SpecterMode) != null && (effecForBuffStat18 = player.getEffectForBuffStat(SecondaryStat.ComingDeath)) != null) {
                        List<MapleMapObject> mobs = player.getMap().getMapObjectsInRect(effecForBuffStat18.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 500), Collections.singletonList(MapleMapObjectType.MONSTER));
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        mobs.forEach(mob -> list.add(mob.getObjectId()));
                        if (!list.isEmpty()) {
                            player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(player, player.getSkillEffect(155100009), 0, list)), true);
                        }
                    }
                    if (player.getCheatTracker().getLastAttackSkill() != 155121102 && player.getCheatTracker().getLastAttackSkill() != 155001100 || applier.effect.getSourceId() == 155121102 || applier.effect.getSourceId() == 155001100 || (skillEffect26 = player.getSkillEffect(155120014)) == null) break;
                    skillEffect26.applyTo(player);
                    break;
                }
            }
            switch (applier.effect.getSourceId()) {
                case 155001100: {
                    player.addPureBeads(1);
                    return 1;
                }
                case 155101100: 
                case 155101101: {
                    player.addFlameBeads(1);
                    ExtraSkill eskill = new ExtraSkill(155101013, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    return 1;
                }
                case 155001102: {
                    if (player.getSkillEffect(155101006) != null || player.getSkillEffect(155000007) != null) {
                        // empty if block
                    }
                    return 1;
                }
                case 155111102: {
                    player.addGaleBeads(1);
                    return 1;
                }
                case 155121102: {
                    player.addAbyssBeads(1);
                    ExtraSkill eskill = new ExtraSkill(155101013, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    return 1;
                }
                case 155121306: {
                    if (player.getSkillEffect(155000007) != null) {
                        player.getSkillEffect(155000007).applyTo(player);
                    }
                    ExtraSkill eskill = new ExtraSkill(155121006, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    return 1;
                }
                case 155121202: {
                    ExtraSkill eskill = new ExtraSkill(155121215, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    return 1;
                }
                case 155101200: {
                    ExtraSkill eskill = new ExtraSkill(155101201, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    return 1;
                }
                case 400051334: {
                    MapleStatEffect skillEffect27 = player.getSkillEffect(155000007);
                    if (skillEffect27 != null && player.getBuffedValue(SecondaryStat.SpecterMode) == null) {
                        skillEffect27.applyTo(player);
                        break;
                    }
                    return 1;
                }
            }
        }
        return -1;
    }
}

