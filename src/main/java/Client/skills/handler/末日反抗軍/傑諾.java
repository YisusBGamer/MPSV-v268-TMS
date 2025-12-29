/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.PortableChair
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.末日反抗軍;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.MonsterEffectHolder;
import Client.PortableChair;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
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
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.AdelePacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 傑諾
extends AbstractSkillHandler {
    public 傑諾() {
        this.jobs = new MapleJob[]{MapleJob.傑諾, MapleJob.傑諾1轉, MapleJob.傑諾2轉, MapleJob.傑諾3轉, MapleJob.傑諾4轉};
        for (Field field : Config.constants.skills.傑諾.class.getDeclaredFields()) {
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
        if (chr.getLevel() >= 10) {
            int[] ss;
            for (int i : ss = new int[]{30021005, 30020232, 30020234, 30021235, 30021236, 30021237, 30020240, 30020300}) {
                Skill skil;
                if (chr.getLevel() < 200 && i == 30021005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
                applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 36141501: 
            case 36141502: 
            case 36141503: {
                return 36141500;
            }
            case 36141000: 
            case 36141001: {
                return 36121001;
            }
            case 36141002: 
            case 36141003: {
                return 36121011;
            }
            case 500004124: {
                return 400041007;
            }
            case 500004125: {
                return 400041029;
            }
            case 500004126: {
                return 400041044;
            }
            case 500004127: {
                return 400041057;
            }
            case 36101008: 
            case 36101009: {
                return 36101000;
            }
            case 36111009: 
            case 36111010: {
                return 36111000;
            }
            case 36121013: 
            case 36121014: {
                return 36121002;
            }
            case 36121011: 
            case 36121012: {
                return 36121001;
            }
            case 36110004: {
                return 36111004;
            }
            case 36121055: {
                return 36121052;
            }
            case 400041031: {
                return 400041029;
            }
            case 400041047: {
                return 400041044;
            }
            case 400041058: {
                return 400041057;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 36100002: {
                statups.put(SecondaryStat.IndieCr, 2);
                return 1;
            }
            case 30021005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 30020232: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.SurplusSupply, 1);
                return 1;
            }
            case 30021237: {
                effect.getInfo().put(MapleStatInfo.time, 30000);
                statups.put(SecondaryStat.NewFlying, 1);
                return 1;
            }
            case 36001005: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.XenonRoketRunch, 1);
                return 1;
            }
            case 36001002: {
                statups.clear();
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                return 1;
            }
            case 36111004: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.XenonAegisSystem, 1);
                return 1;
            }
            case 36121003: {
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.indiePMdR));
                statups.put(SecondaryStat.BdR, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                return 1;
            }
            case 36120004: {
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IgnoreTargetDEF, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 36121014: {
                statups.clear();
                statups.put(SecondaryStat.DEXR, effect.getInfo().get((Object)MapleStatInfo.evaR));
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                return 1;
            }
            case 36121054: {
                statups.put(SecondaryStat.AmaranthGenerator, 1);
                return 1;
            }
            case 36121052: {
                effect.getInfo().put(MapleStatInfo.subTime, effect.getInfo().get((Object)MapleStatInfo.time) * 1000);
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.y) * 1000);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.w));
                monsterStatus.put(MonsterStatus.PDR, -effect.getInfo().get((Object)MapleStatInfo.x).intValue());
                monsterStatus.put(MonsterStatus.MDR, -effect.getInfo().get((Object)MapleStatInfo.x).intValue());
                return 1;
            }
            case 400041029: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.OverloadMode, effect.getInfo().get((Object)MapleStatInfo.w));
                return 1;
            }
            case 36121053: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                monsterStatus.put(MonsterStatus.MagicCrash, 1);
                return 1;
            }
            case 36110005: {
                monsterStatus.put(MonsterStatus.EVA, -8);
                monsterStatus.put(MonsterStatus.Blind, 8);
                monsterStatus.put(MonsterStatus.Explosion, 1);
                return 1;
            }
            case 36111006: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ShadowPartner, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 400041007: {
                statups.put(SecondaryStat.IndieNotDamaged, 0);
                statups.put(SecondaryStat.MegaSmasher, -1);
                return 1;
            }
            case 400041044: {
                statups.put(SecondaryStat.EVAR, 1);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400041057: {
                statups.put(SecondaryStat.XenonBursterLaser, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 36111008: {
                if (chr.getSkillEffect(30020232) != null) {
                    chr.applyXenonEnegy(applier.effect.getX());
                }
                return 1;
            }
            case 400041058: {
                slea.readInt();
                applier.pos = slea.readPosInt();
                if (chr.getBuffStatValueHolder(SecondaryStat.XenonBursterLaser) != null) {
                    chr.dispelEffect(SecondaryStat.XenonBursterLaser);
                    if (!applier.ai.skillTargetList.isEmpty()) {
                        ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                        int idx = 1;
                        int[] list = new int[]{-45, -70, -135, -165, -150, -35, -55, -50, -160, -48, -140, -167, -40, -170, 52, -10, -15, -170, -165, -175, -5, -20, -25, -160, -30, -155, -150, -32, -177, -38};
                        for (Pair<Integer, Integer> pair : applier.ai.skillTargetList) {
                            for (int i = 0; i < (Integer)pair.right; ++i) {
                                ForceAtomObject sword = new ForceAtomObject(idx, 9, idx - 1, chr.getId(), 0, 400041058);
                                sword.Position = new Point(applier.pos.x + Randomizer.nextInt(200), applier.pos.y + 46);
                                sword.ObjPosition = new Point(applier.pos.x, applier.pos.y);
                                sword.Expire = 10000;
                                sword.Target = (Integer)pair.left;
                                sword.CreateDelay = 150 + i * 30;
                                for (int x : list) {
                                    sword.addX(x);
                                }
                                createList.add(sword);
                                ++idx;
                            }
                        }
                        if (!createList.isEmpty()) {
                            chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
                        }
                    }
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 30021235: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 36001005: {
                if (!applier.primary) {
                    return 0;
                }
                if (applyto.getBuffedValue(SecondaryStat.XenonRoketRunch) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 30020232: {
                applier.localstatups.put(SecondaryStat.SurplusSupply, Math.min(SkillConstants.dY(applyto.getJob()) * 5 + applyto.getBuffedIntValue(SecondaryStat.OverloadMode), Math.max(0, applyto.getBuffedIntValue(SecondaryStat.SurplusSupply))));
                return 1;
            }
            case 36111004: {
                if (applyto.getBuffedValue(SecondaryStat.XenonAegisSystem) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 36111006: {
                applyto.getSpecialStat().setShadowHP(applyto.getStat().getCurrentMaxHP() * applier.effect.getX());
                return 1;
            }
            case 36121007: {
                if (applier.primary) {
                    applyto.setChair(new PortableChair(3010587));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.UserSetActivePortableChair(applyto), false);
                    applyto.getClient().announce(MaplePacketCreator.showSitOnTimeCapsule());
                }
                return 1;
            }
            case 36121054: {
                applyfrom.applyXenonEnegy(20);
                return 1;
            }
            case 400041007: {
                if (applier.passive) {
                    SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.MegaSmasher);
                    if (mbsvh != null) {
                        applier.duration += Math.min((int)(System.currentTimeMillis() - mbsvh.startTime) / (applier.effect.getY() * 1000), applier.effect.getZ()) * 1000;
                    }
                    applier.localstatups.put(SecondaryStat.MegaSmasher, 1);
                    break;
                }
                applier.localstatups.remove(SecondaryStat.IndieNotDamaged);
                applier.duration = 2100000000;
                return 1;
            }
            case 400041057: {
                applier.buffz = 0;
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MonsterEffectHolder meh = applyto.getEffectHolder(MonsterStatus.Explosion);
        if (meh != null && meh.value >= 3) {
            if (applier.effect != null) {
                applyfrom.getClient().announce(ForcePacket.UserExplosionAttack(applyto));
                applyto.removeEffect(applyfrom.getId(), 36110005);
            }
        } else {
            MapleStatEffect skillEffect19 = applyfrom.getSkillEffect(36110005);
            if (skillEffect19 != null && applier.effect != null && applier.effect.getSourceId() != 36110005) {
                skillEffect19.applyMonsterEffect(applyfrom, applyto, skillEffect19.getY() * 1000);
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleForceFactory mff = MapleForceFactory.getInstance();
        MapleStatEffect effecForBuffStat11 = player.getEffectForBuffStat(SecondaryStat.XenonRoketRunch);
        if (applier.totalDamage > 0L && effecForBuffStat11 != null && player.getCheatTracker().canNext追縱火箭() && (applier.effect == null || applier.effect.getSourceId() != 36001005)) {
            List<MapleMapObject> mobs = player.getMap().getMapObjectsInRect(effecForBuffStat11.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 100), Collections.singletonList(MapleMapObjectType.MONSTER));
            ArrayList<Integer> list2 = new ArrayList<Integer>();
            mobs.forEach(mob -> list2.add(mob.getObjectId()));
            if (!list2.isEmpty()) {
                player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, effecForBuffStat11, 0, list2)), true);
            }
        }
        return 1;
    }
}

