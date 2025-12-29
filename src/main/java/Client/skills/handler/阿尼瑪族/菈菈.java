/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.阿尼瑪族;

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
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleAffectedArea;
import Net.server.quest.MapleQuest;
import Packet.AdelePacket;
import Packet.BuffPacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 菈菈
extends AbstractSkillHandler {
    public 菈菈() {
        this.jobs = new MapleJob[]{MapleJob.菈菈, MapleJob.菈菈1轉, MapleJob.菈菈2轉, MapleJob.菈菈3轉, MapleJob.菈菈4轉};
        for (Field field : Config.constants.skills.菈菈.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{160011074, 160010000, 160011075, 160011005}) {
            if (chr.getLevel() < 200 && i == 160011005) continue;
            int skillLevel = 1;
            Skill skil = SkillFactory.getSkill(i);
            if (skil == null || chr.getSkillLevel(skil) >= skillLevel) continue;
            applier.skillMap.put(i, new SkillEntry(skillLevel, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 162141501: {
                return 162141500;
            }
            case 162141000: {
                return 162001000;
            }
            case 500004176: {
                return 400021122;
            }
            case 500004177: {
                return 400021123;
            }
            case 500004178: {
                return 400021129;
            }
            case 500004179: {
                return 400021130;
            }
            case 162001004: {
                return 162000003;
            }
            case 162101003: 
            case 162101004: {
                return 162100002;
            }
            case 162101006: 
            case 162101007: {
                return 162100005;
            }
            case 162101009: 
            case 162101010: 
            case 162101011: {
                return 162100008;
            }
            case 80003059: 
            case 162111001: {
                return 162111000;
            }
            case 162111010: {
                return 162111002;
            }
            case 162111004: {
                return 162111003;
            }
            case 162121003: 
            case 162121004: {
                return 162120002;
            }
            case 162121006: 
            case 162121007: {
                return 162120005;
            }
            case 162121009: 
            case 162121010: {
                return 162120008;
            }
            case 162121012: 
            case 162121013: 
            case 162121014: 
            case 162121015: 
            case 162121016: 
            case 162121017: 
            case 162121018: 
            case 162121019: {
                return 162120011;
            }
            case 162121021: {
                return 162120020;
            }
            case 162121044: {
                return 162121043;
            }
            case 400021124: 
            case 400021125: 
            case 400021126: 
            case 400021127: 
            case 400021128: {
                return 400021123;
            }
            case 400021131: {
                return 400021130;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 80003058: 
            case 160010001: {
                statups.put(SecondaryStat.IndieNBDR, effect.getW());
                return 1;
            }
            case 80003070: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AMLinkSkill, 1);
                return 1;
            }
            case 160011005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 162101013: {
                statups.put(SecondaryStat.Booster, effect.getX());
                return 1;
            }
            case 162001005: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AMStoneShield, effect.getX());
                return 1;
            }
            case 162101000: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AMEarthVeinOnOff, 1);
                return 1;
            }
            case 162101012: {
                statups.put(SecondaryStat.AMPlanting, 0);
                return 1;
            }
            case 162101009: 
            case 162121017: {
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
            }
            case 162101003: 
            case 162101006: 
            case 162121012: 
            case 162121015: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 162110007: {
                statups.put(SecondaryStat.IndiePMdR, effect.getX());
                return 1;
            }
            case 162111006: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AMEVTeleport, 0);
                return 1;
            }
            case 162121042: {
                statups.put(SecondaryStat.AMArtificialEarthVein, 0);
                return 1;
            }
            case 162111000: 
            case 162111003: {
                statups.clear();
                return 1;
            }
            case 162111004: {
                statups.put(SecondaryStat.IndieDamR, effect.getIndieDamR());
                return 1;
            }
            case 162111001: {
                statups.put(SecondaryStat.IndieJump, effect.getInfo().get((Object)MapleStatInfo.indieJump));
                statups.put(SecondaryStat.IndieSpeed, effect.getInfo().get((Object)MapleStatInfo.indieSpeed));
                statups.put(SecondaryStat.IndieBooster, effect.getIndieBooster());
                return 1;
            }
            case 80003059: {
                statups.put(SecondaryStat.NewFlying, 1);
                return 1;
            }
            case 162121003: {
                statups.put(SecondaryStat.AMAbsorptionRiver, 1);
                return 1;
            }
            case 162121006: {
                statups.put(SecondaryStat.AMAbsorptionWind, 1);
                return 1;
            }
            case 162121009: {
                statups.put(SecondaryStat.AMAbsorptionSun, 1);
                return 1;
            }
            case 162121022: {
                effect.getInfo().put(MapleStatInfo.time, effect.getQ() * 1000);
                statups.put(SecondaryStat.IndieAllHitDamR, -effect.getX());
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.AntiMagicShell, 1);
                statups.put(SecondaryStat.KeyDownEnable, 1);
                return 1;
            }
            case 162120038: {
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                return 1;
            }
            case 162121041: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 162121043: {
                monsterStatus.put(MonsterStatus.AreaPDR, effect.getW());
                statups.put(SecondaryStat.IndieCD, effect.getX());
                statups.put(SecondaryStat.IndieBDR, effect.getIndieBDR());
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                return 1;
            }
            case 162121044: {
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
            case 160011075: {
                chr.setQuestAdd(MapleQuest.getInstance(7786), (byte)0, "sw=1");
                String statData = chr.getOneInfo(7786, "sw");
                statData = statData == null || statData.equals("0") ? String.valueOf(1) : String.valueOf(0);
                chr.updateOneInfo(7786, "sw", statData, true);
                chr.getMap().broadcastMessage(MaplePacketCreator.showHoyoungHide(chr.getId(), Integer.valueOf(statData) == 1));
                return 1;
            }
            case 162101003: 
            case 162101006: 
            case 162121012: 
            case 162121015: {
                applier.pos = slea.readPos();
                return 1;
            }
            case 162101009: {
                MapleStatEffect effect = chr.getSkillEffect(162101010);
                if (effect != null) {
                    effect.applyAffectedArea(chr, applier.pos != null ? applier.pos : chr.getPosition());
                }
                return 1;
            }
            case 162121017: {
                MapleStatEffect effect = chr.getSkillEffect(162121018);
                if (effect != null) {
                    effect.applyAffectedArea(chr, applier.pos != null ? applier.pos : chr.getPosition());
                }
                return 1;
            }
            case 162101011: 
            case 162121019: {
                if (chr.getMap().getAffectedAreaByChr(chr.getId(), applier.effect.getSourceId() == 162121019 ? 162121018 : 162101010) == null) {
                    return 0;
                }
                slea.readInt();
                int x = slea.readInt();
                int y = slea.readInt();
                slea.readByte();
                Map<Integer, ForceAtomObject> objsMap = chr.getForceAtomObjects();
                ArrayList<ForceAtomObject> removeList = new ArrayList<ForceAtomObject>();
                Iterator<Map.Entry<Integer, ForceAtomObject>> iterator = objsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, ForceAtomObject> obj = iterator.next();
                    if (obj.getValue().SkillId != applier.effect.getSourceId()) continue;
                    removeList.add(obj.getValue());
                    iterator.remove();
                }
                if (!removeList.isEmpty()) {
                    chr.getMap().broadcastMessage(AdelePacket.ForceAtomObjectRemove((int)chr.getId(), removeList, (int)1), chr.getPosition());
                }
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                for (int i = 0; i < 5; ++i) {
                    ForceAtomObject obj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), 21, i + 1, chr.getId(), 0, applier.effect.getSourceId());
                    obj.EnableDelay = 750;
                    obj.Expire = 4000;
                    obj.Position = new Point(0, 1);
                    obj.ObjPosition = new Point(x + Randomizer.rand(-200, 200), y + Randomizer.rand(-100, -90));
                    obj.addX(x);
                    obj.addX(y);
                    objsMap.put(obj.Idx, obj);
                    createList.add(obj);
                }
                if (!createList.isEmpty()) {
                    chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
                }
                return 1;
            }
            case 162101001: 
            case 162121000: {
                MapleStatEffect effect = chr.getSkillEffect(162110007);
                if (effect != null) {
                    effect.applyTo(chr);
                }
                return 1;
            }
            case 162111005: {
                int nCount = slea.readByte();
                for (int i = 0; i < nCount; ++i) {
                    slea.readInt();
                }
                slea.readByte();
                slea.readShort();
                applier.pos = slea.readPosInt();
                slea.readByte();
                nCount = Math.min(applier.effect.getZ(), Math.max(applier.effect.getBulletCount(), nCount));
                int type = -1;
                Map<Integer, ForceAtomObject> objsMap = chr.getForceAtomObjects();
                for (ForceAtomObject obj : objsMap.values()) {
                    if (162101000 != obj.SkillId || !obj.ObjPosition.equals(applier.pos) || obj.ValueList == null || obj.ValueList.size() <= 0) continue;
                    type = (Integer)obj.ValueList.get(0);
                    break;
                }
                if (type == 1 || type == 2 || type == 4 || type == 8) {
                    ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                    for (int i = 0; i < nCount; ++i) {
                        ForceAtomObject obj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), type == 1 ? 22 : (type == 2 ? 25 : (type == 8 ? 27 : 26)), i, chr.getId(), 0, applier.effect.getSourceId());
                        obj.CreateDelay = 600;
                        obj.EnableDelay = 1140;
                        obj.Expire = 4000;
                        obj.Position = new Point(0, 1);
                        obj.ObjPosition = new Point(applier.pos.x + Randomizer.rand(-150, 150), applier.pos.y + Randomizer.rand(-200, -100));
                        objsMap.put(obj.Idx, obj);
                        createList.add(obj);
                    }
                    if (!createList.isEmpty()) {
                        chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
                    }
                }
                return 1;
            }
            case 162121010: {
                int[] oids = new int[slea.readByte()];
                for (int i = 0; i < oids.length; ++i) {
                    oids[i] = slea.readInt();
                }
                slea.skip(3);
                applier.pos = slea.readPosInt();
                slea.readByte();
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                for (int i = 0; i < 5; ++i) {
                    ForceAtomObject obj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), 23, i, chr.getId(), Randomizer.nextInt(10) * 10, applier.effect.getSourceId());
                    if (oids.length > 0) {
                        obj.Target = oids[Randomizer.nextInt(oids.length)];
                    }
                    obj.CreateDelay = 1110;
                    obj.EnableDelay = 1200;
                    obj.Expire = 4000;
                    obj.Position = new Point(80, 1);
                    obj.ObjPosition = new Point(applier.pos.x + Randomizer.rand(-150, 150), applier.pos.y + Randomizer.rand(-200, -200));
                    obj.addX(applier.pos.x);
                    obj.addX(applier.pos.y);
                    chr.getForceAtomObjects().put(obj.Idx, obj);
                    createList.add(obj);
                }
                if (!createList.isEmpty()) {
                    chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
                }
                return 1;
            }
            case 162121042: {
                ForceAtomObject obj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), 20, 0, chr.getId(), 0, 162101000);
                obj.CreateDelay = 540;
                obj.Idk1 = 1;
                obj.Position = new Point(0, 1);
                obj.Idk2 = 1;
                obj.ObjPosition = applier.pos;
                obj.B1 = true;
                obj.addX(8);
                obj.addX(applier.ai.unInt1);
                chr.getForceAtomObjects().put(obj.Idx, obj);
                chr.send(AdelePacket.ForceAtomObject((int)chr.getId(), Collections.singletonList(obj), (int)0));
                return 1;
            }
            case 400021122: {
                int[] oids = new int[slea.readByte()];
                for (int i = 0; i < oids.length; ++i) {
                    oids[i] = slea.readInt();
                }
                slea.skip(3);
                applier.pos = slea.readPosInt();
                slea.readByte();
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                for (int i = 0; i < 5; ++i) {
                    ForceAtomObject obj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), applier.ai.unInt1 == 8 ? 30 : (applier.ai.unInt1 == 4 ? 29 : (applier.ai.unInt1 == 2 ? 28 : 24)), i, chr.getId(), 0, applier.effect.getSourceId());
                    if (oids.length > 0) {
                        obj.Target = oids[Randomizer.nextInt(oids.length)];
                    }
                    obj.CreateDelay = 660;
                    obj.EnableDelay = 1320;
                    obj.Expire = 4800;
                    obj.Position = new Point(0, 1);
                    obj.ObjPosition = new Point(applier.pos.x + Randomizer.rand(-150, 150), applier.pos.y + Randomizer.rand(-200, -200));
                    obj.addX(applier.pos.x);
                    obj.addX(applier.pos.y);
                    chr.getForceAtomObjects().put(obj.Idx, obj);
                    createList.add(obj);
                }
                if (!createList.isEmpty()) {
                    chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
                }
                return 1;
            }
            case 400021123: {
                LinkedList<ExtraSkill> eskills = new LinkedList<ExtraSkill>();
                for (int i = 5; i > 0; --i) {
                    ExtraSkill eskill = new ExtraSkill(applier.effect.getSourceId() + i, applier.pos);
                    int n = eskill.FaceLeft = applier.ai.left ? 1 : 0;
                    eskill.Delay = i == 5 ? 5850 : (i == 4 ? 3150 : (i == 3 ? 2250 : (i == 2 ? 1560 : 630)));
                    eskill.Value = i == 5 ? 8 : 4;
                    eskills.add(eskill);
                }
                chr.send(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), eskills));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 160011074: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 162101012: {
                if (applier.passive) {
                    SecondaryStatValueHolder mbsvh;
                    applier.applySummon = false;
                    applier.localstatups.remove(SecondaryStat.IndieBuffIcon);
                    int newDuration = applier.duration;
                    applier.duration = 2100000000;
                    int changeValue = 1;
                    if (applier.primary) {
                        changeValue = -1;
                    }
                    if ((mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.AMPlanting)) == null) {
                        applier.localstatups.put(SecondaryStat.AMPlanting, Math.max(0, Math.min(applyto.getBuffedIntValue(SecondaryStat.AMPlanting) + changeValue, applier.effect.getW2())));
                    } else {
                        mbsvh.value += changeValue;
                        mbsvh.startTime = System.currentTimeMillis();
                        applyfrom.send(BuffPacket.giveBuff(applyfrom, mbsvh.effect, Collections.singletonMap(SecondaryStat.AMPlanting, mbsvh.effect.getSourceId())));
                    }
                    if (applier.primary) {
                        applier.effect.applyBuffEffect(applyfrom, applyto, newDuration, true, false, false, applier.pos);
                    }
                    if (mbsvh != null) {
                        return 0;
                    }
                } else {
                    applier.localstatups.remove(SecondaryStat.AMPlanting);
                }
                return 1;
            }
            case 162110007: {
                applyfrom.addHPMP(applier.effect.getU(), applier.effect.getU());
                MapleStatEffect eff = applyfrom.getSkillEffect(162120037);
                if (eff != null) {
                    int value = applier.localstatups.remove(SecondaryStat.IndiePMdR);
                    applier.localstatups.put(SecondaryStat.IndieJump, eff.getY());
                    applier.localstatups.put(SecondaryStat.IndieSpeed, eff.getX());
                    applier.localstatups.put(SecondaryStat.IndiePMdR, value);
                }
                return 1;
            }
            case 162101000: {
                if (applyfrom.getBuffStatValueHolder(162101000) != null) {
                    applyfrom.dispelEffect(162101000);
                    return 0;
                }
                return 1;
            }
            case 162111006: {
                if (!applier.primary && applier.passive) {
                    return 0;
                }
                int changeValue = applier.primary && applier.passive ? 1 : -1;
                applier.duration = 2100000000;
                applier.localstatups.put(SecondaryStat.AMEVTeleport, Math.max(0, Math.min(applyto.getBuffedIntValue(SecondaryStat.AMEVTeleport) + changeValue, applier.effect.getV())));
                return 1;
            }
            case 162121042: {
                if (applier.passive) {
                    int changeValue = 1;
                    if (applier.primary) {
                        changeValue = -1;
                    }
                    applier.duration = 2100000000;
                    applier.localstatups.put(SecondaryStat.AMArtificialEarthVein, Math.max(0, Math.min(applyto.getBuffedIntValue(SecondaryStat.AMArtificialEarthVein) + changeValue, applier.effect.getV())));
                }
                return 1;
            }
            case 162111000: 
            case 162111003: {
                applier.effect.applyAffectedArea(applyto, applier.pos != null ? applier.pos : applyto.getPosition());
                MapleStatEffect eff = applyto.getSkillEffect(162110007);
                if (eff != null) {
                    eff.applyTo(applyto);
                }
                return 1;
            }
            case 80003059: {
                int oid = 0;
                for (MapleAffectedArea mist : applyfrom.getMap().getAllAffectedAreasThreadsafe()) {
                    MapleCharacter playerObject;
                    if (mist.getEffect().getSourceId() != 162111000 || (playerObject = applyfrom.getMap().getPlayerObject(mist.getOwnerId())) == null || (playerObject.getParty() == null || playerObject.getParty() != applyto.getParty()) && applyto != playerObject) continue;
                    oid = mist.getObjectId();
                    break;
                }
                applier.buffz = oid;
                return 1;
            }
            case 162111005: {
                MapleStatEffect eff = applyto.getSkillEffect(162110007);
                if (eff != null) {
                    eff.applyTo(applyto);
                }
                return 1;
            }
            case 162111002: {
                ForceAtomObject obj;
                LinkedList<Integer> idxs = new LinkedList<Integer>();
                Map<Integer, ForceAtomObject> objsMap = applyfrom.getForceAtomObjects();
                for (ForceAtomObject obj2 : objsMap.values()) {
                    if (162111002 != obj2.SkillId) continue;
                    int idx = obj2.DataIndex == 32 ? (Integer)obj2.ValueList.get(0) : obj2.Idx;
                    if (idxs.contains(idx)) {
                        idxs.remove((Object)idx);
                        continue;
                    }
                    idxs.add(idx);
                }
                if (!idxs.isEmpty()) {
                    obj = new ForceAtomObject(applyfrom.getSpecialStat().gainForceCounter(), 32, 0, applyfrom.getId(), 0, 162111002);
                    obj.Idk1 = 20;
                    obj.Position = new Point(0, 1);
                    obj.ObjPosition = new Point(applier.pos.x, applier.pos.y);
                    objsMap.put(obj.Idx, obj);
                    return 1;
                }
                obj = new ForceAtomObject(applyfrom.getSpecialStat().gainForceCounter(), 31, 0, applyfrom.getId(), 0, 162111002);
                obj.Expire = 20000;
                obj.Idk1 = 20;
                obj.Position = new Point(0, 1);
                obj.ObjPosition = new Point(applier.pos.x, applier.pos.y);
                obj.addX(0);
                MapleStatEffect eff = applyto.getSkillEffect(162110007);
                if (eff != null) {
                    eff.applyTo(applyto);
                }
                objsMap.put(obj.Idx, obj);
                applyfrom.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)applyfrom.getId(), Collections.singletonList(obj), (int)0), applyfrom.getPosition());
                return 1;
            }
            case 162121022: {
                MapleStatEffect eff = applyfrom.getSkillEffect(162120038);
                if (eff != null) {
                    eff.applyTo(applyfrom, applier.duration + eff.getDuration());
                }
                return 1;
            }
            case 162120038: {
                applier.localstatups.put(SecondaryStat.IndieBarrier, applyfrom.getStat().getCurrentMaxHP() * applier.effect.getX() / 100);
                return 1;
            }
            case 162121043: {
                MapleStatEffect eff;
                if (applier.primary && (eff = applyto.getSkillEffect(162121044)) != null) {
                    eff.applyTo(applyto);
                }
                return 1;
            }
            case 400021122: {
                MapleStatEffect eff = applyto.getSkillEffect(162110007);
                if (eff != null && applyto.getBuffStatValueHolder(162110007) == null) {
                    eff.applyTo(applyto);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.totalDamage > 0L) {
            switch (applier.effect.getSourceId()) {
                case 162001000: 
                case 162121021: {
                    int prop = 0;
                    MapleStatEffect eff = player.getSkillEffect(162000003);
                    if (eff != null) {
                        MapleStatEffect eff1 = player.getSkillEffect(162120026);
                        if (eff1 != null) {
                            eff = eff1;
                        }
                        prop = eff.getProp();
                    }
                    if (Randomizer.isSuccess(prop)) {
                        ExtraSkill eskill = new ExtraSkill(162001004, new Point(applier.ai.mobAttackInfo.get((int)0).hitX, applier.ai.mobAttackInfo.get((int)0).hitY));
                        eskill.Value = 1;
                        eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
                        player.send(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    }
                    LinkedHashMap<SecondaryStat, Pair<Integer, Integer>> sMap = new LinkedHashMap<SecondaryStat, Pair<Integer, Integer>>();
                    sMap.put(SecondaryStat.AMAbsorptionRiver, new Pair<Integer, Integer>(162121004, 1));
                    sMap.put(SecondaryStat.AMAbsorptionWind, new Pair<Integer, Integer>(162121007, 7));
                    sMap.put(SecondaryStat.AMAbsorptionSun, new Pair<Integer, Integer>(162121010, 5));
                    for (Map.Entry entry : sMap.entrySet()) {
                        int sourceid = (Integer)((Pair)entry.getValue()).getLeft();
                        if (player.getBuffStatValueHolder((SecondaryStat)entry.getKey()) == null || player.isSkillCooling(sourceid)) continue;
                        player.registerSkillCooldown(sourceid, 2500, true);
                        ExtraSkill eskill = new ExtraSkill((Integer)((Pair)entry.getValue()).getLeft(), new Point(applier.ai.mobAttackInfo.get((int)0).hitX, applier.ai.mobAttackInfo.get((int)0).hitY));
                        eskill.Value = (Integer)((Pair)entry.getValue()).getRight();
                        eskill.FaceLeft = (applier.ai.direction & 0x80) != 0 ? 1 : 0;
                        player.send(MaplePacketCreator.RegisterExtraSkill(sourceid, Collections.singletonList(eskill)));
                    }
                    return 1;
                }
            }
        }
        return -1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        if (!applier.overwrite && applier.effect.getSourceId() == 162101000) {
            Map<Integer, ForceAtomObject> objsMap = player.getForceAtomObjects();
            ArrayList<ForceAtomObject> removeList = new ArrayList<ForceAtomObject>();
            Iterator<Map.Entry<Integer, ForceAtomObject>> iterator = objsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ForceAtomObject> obj = iterator.next();
                if (obj.getValue().SkillId != 162101000) continue;
                removeList.add(obj.getValue());
                iterator.remove();
            }
            if (!removeList.isEmpty()) {
                player.getMap().broadcastMessage(AdelePacket.ForceAtomObjectRemove((int)player.getId(), removeList, (int)1), player.getPosition());
            }
        }
        return -1;
    }
}

