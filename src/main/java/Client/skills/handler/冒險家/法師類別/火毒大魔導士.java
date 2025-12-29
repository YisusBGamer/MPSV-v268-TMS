/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.冒險家.法師類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.MonsterEffectHolder;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.skills.冒險家_技能群組.type_法師.火毒;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.Element;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleAffectedArea;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Opcode.Opcode.EffectOpcode;
import Packet.AdelePacket;
import Packet.BuffPacket;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 火毒大魔導士
extends AbstractSkillHandler {
    public 火毒大魔導士() {
        this.jobs = new MapleJob[]{MapleJob.火毒巫師, MapleJob.火毒魔導士, MapleJob.火毒大魔導士};
        for (Field field : 火毒.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 2141501: 
            case 2141503: {
                return 2141500;
            }
            case 2141001: 
            case 2141002: 
            case 2141003: {
                return 2141000;
            }
            case 2141005: {
                return 2121003;
            }
            case 2100010: {
                return 2101010;
            }
            case 2111014: {
                return 2111013;
            }
            case 2120013: {
                return 2121007;
            }
            case 400021029: {
                return 400021028;
            }
            case 400021102: 
            case 400021103: {
                return 400021101;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 2100009: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.DotBasedBuff, 1);
                return 1;
            }
            case 2101005: 
            case 2111003: 
            case 2121052: 
            case 400021001: 
            case 400021028: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 2101001: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieMAD, effect.getInfo().get((Object)MapleStatInfo.indieMad));
                return 1;
            }
            case 2101010: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.WizardIgnite, 1);
                return 1;
            }
            case 2121005: {
                effect.getInfo().put(MapleStatInfo.summonCount, 1);
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 2111013: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 2111011: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AntiMagicShell, effect.getY());
                return 1;
            }
            case 2111007: {
                statups.put(SecondaryStat.TeleportMasteryOn, 1);
                monsterStatus.put(MonsterStatus.Stun, 1);
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 2111016: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.TeleportMasteryRange, 1);
                return 1;
            }
            case 2121006: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.time) * 2);
                monsterStatus.put(MonsterStatus.Stun, 1);
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 2121011: {
                monsterStatus.put(MonsterStatus.IndieSlow, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.DodgeBodyAttack, 1);
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 2121004: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                effect.setMpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.prop));
                statups.put(SecondaryStat.Infinity, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 2120010: {
                effect.getInfo().put(MapleStatInfo.time, 5000);
                statups.put(SecondaryStat.ArcaneAim, 1);
                return 1;
            }
            case 2121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 2121054: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                monsterStatus.put(MonsterStatus.Burned, 1);
                statups.put(SecondaryStat.FireAura, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 2121052: {
                chr.getSpecialStat().gainFieldSkillCounter(2121052);
                List oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).toList();
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                for (int i2 = 0; i2 < applier.effect.getBulletCount(); ++i2) {
                    ForceAtomObject obj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), 35, i2, chr.getId(), 0, applier.effect.getSourceId());
                    obj.Idk3 = 1;
                    if (!oids.isEmpty()) {
                        obj.Target = (Integer)oids.get(i2 % oids.size());
                    }
                    obj.CreateDelay = 480;
                    obj.EnableDelay = 720;
                    obj.Idk1 = 1;
                    obj.Expire = 5000;
                    obj.Position = new Point(0, 1);
                    obj.ObjPosition = new Point(chr.getPosition());
                    obj.ObjPosition.x += (int)(chr.getPosition().getX() + (double)Randomizer.rand(-100, 100));
                    obj.ObjPosition.y += (int)(chr.getPosition().getY() + (double)Randomizer.rand(-100, 100));
                    createList.add(obj);
                }
                if (!createList.isEmpty()) {
                    chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
                }
                chr.getTempValues().put("藍焰斬Count", createList.size());
                return 1;
            }
            case 400021001: {
                ArrayList<Integer> oids = new ArrayList<Integer>();
                for (MapleMapObject monster : chr.getMap().getMapObjectsInRange(chr.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER))) {
                    oids.add(monster.getObjectId());
                    if (oids.size() < applier.effect.getMobCount()) continue;
                    break;
                }
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, oids)), true);
                return 1;
            }
            case 400021102: {
                ForceAtomObject obj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), 38, 0, chr.getId(), 0, applier.effect.getSourceId());
                Pair spawninfo = null;
                if (!applier.ai.skillSpawnInfo.isEmpty()) {
                    spawninfo = (Pair)applier.ai.skillSpawnInfo.getFirst();
                }
                if (spawninfo != null) {
                    obj.Target = (Integer)spawninfo.getLeft();
                }
                obj.Expire = 10000;
                obj.Position = new Point(0, 1);
                obj.ObjPosition = new Point(chr.getPosition().getLocation());
                obj.ObjPosition.y -= 43;
                obj.addX(chr.getSpecialStat().getFieldSkillCounter(400021101));
                chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), Collections.singletonList(obj), (int)0), chr.getPosition());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 2111011) {
            applier.cooldown = 0;
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect instanceof MobSkill) {
            SecondaryStatValueHolder mbsvh;
            boolean isCriticalDebuff = false;
            for (SecondaryStat stat : applier.localstatups.keySet()) {
                if (!stat.isCriticalDebuff()) continue;
                isCriticalDebuff = true;
                break;
            }
            if (isCriticalDebuff && (mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.AntiMagicShell)) != null && mbsvh.value > 0) {
                int mpCon = applyto.getStat().getCurrentMaxHP() * mbsvh.effect.getX() / 100;
                if (applyto.getStat().getHp() < mpCon) {
                    return -1;
                }
                applyto.addMP(-mpCon, true);
                applyto.send(EffectPacket.showBlessOfDarkness(-1, mbsvh.effect.getSourceId()));
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showBlessOfDarkness(applyto.getId(), mbsvh.effect.getSourceId()), false);
                --mbsvh.value;
                if (mbsvh.value > 0) {
                    applyto.send(BuffPacket.giveBuff(applyto, mbsvh.effect, Collections.singletonMap(SecondaryStat.AntiMagicShell, mbsvh.effect.getSourceId())));
                } else {
                    applyto.dispelEffect(SecondaryStat.AntiMagicShell);
                    applyto.registerSkillCooldown(mbsvh.effect, true);
                }
                return 0;
            }
            return -1;
        }
        switch (applier.effect.getSourceId()) {
            case 2101010: {
                if (applyto.getBuffedValue(SecondaryStat.WizardIgnite) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 2100009: {
                int count = 0;
                for (MapleMapObject obj : applyfrom.getMap().getMapObjectsInRange(applyfrom.getPosition(), applier.effect.getRange(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                    if (((MapleMonster)obj).getEffectHolder(applyfrom.getId(), MonsterStatus.Burned) != null && ++count >= 5) break;
                }
                if (count <= 0) {
                    applyfrom.dispelEffect(2100009);
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.DotBasedBuff, count);
                return 0;
            }
            case 2111007: {
                applier.duration = 2100000000;
                return 1;
            }
            case 2120010: {
                if (applyto.getBuffedValue(SecondaryStat.ArcaneAim) != null) {
                    applier.localstatups.put(SecondaryStat.ArcaneAim, Math.min(applier.effect.getY(), applyto.getBuffedIntValue(SecondaryStat.ArcaneAim) + 1));
                }
                return 1;
            }
            case 400021101: {
                applyfrom.getSpecialStat().gainFieldSkillCounter(400021101);
                return -1;
            }
            case 2121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(0x111B1D);
                applyto.dispelEffect(1221053);
                applyto.dispelEffect(1321053);
                applyto.dispelEffect(2121053);
                applyto.dispelEffect(2221053);
                applyto.dispelEffect(2321053);
                applyto.dispelEffect(3121053);
                applyto.dispelEffect(3221053);
                applyto.dispelEffect(3321041);
                applyto.dispelEffect(4221053);
                applyto.dispelEffect(4121053);
                applyto.dispelEffect(4341053);
                applyto.dispelEffect(5121053);
                applyto.dispelEffect(5221053);
                applyto.dispelEffect(5321053);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyMonsterEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 2111007) {
            EnumMap<MonsterStatus, MonsterEffectHolder> statups = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
            int prop = applier.effect.getSubProp();
            long currentTimeMillis = System.currentTimeMillis();
            if (Randomizer.isSuccess(prop)) {
                statups.put(MonsterStatus.Stun, new MonsterEffectHolder(applyfrom.getId(), 1, currentTimeMillis, applier.effect.calcMobDebuffDuration(applier.effect.getDuration(), applyfrom), applier.effect));
            }
            if (Randomizer.isSuccess(prop = applier.effect.getProp())) {
                MonsterEffectHolder holder = new MonsterEffectHolder(applyfrom.getId(), 1, currentTimeMillis, applier.effect.getMobDebuffDuration(applyfrom), applier.effect);
                applier.effect.setDotData(applyfrom, holder);
                statups.put(MonsterStatus.Burned, holder);
            }
            if (!statups.isEmpty()) {
                applyto.registerEffect(statups);
                LinkedHashMap<MonsterStatus, Integer> writeStatups = new LinkedHashMap<MonsterStatus, Integer>();
                for (MonsterStatus stat : statups.keySet()) {
                    writeStatups.put(stat, applier.effect.getSourceId());
                }
                applyfrom.getMap().broadcastMessage(MobPacket.mobStatSet(applyto, writeStatups), applyto.getPosition());
            }
            return 0;
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat;
        MapleStatEffect effect = applyfrom.getSkillEffect(0x200B20);
        if (effect != null && effect.makeChanceResult(applyfrom)) {
            int rate = !applyto.getStats().isBoss() ? effect.getX() : effect.getY();
            int absorbMp = Math.min(applyto.getMobMaxMp() * rate / 100, applyto.getMp());
            if (absorbMp > 0) {
                applyto.setMp(applyto.getMp() - absorbMp);
                applyfrom.addMP(absorbMp);
                applyfrom.send(EffectPacket.encodeUserEffectLocal(effect.getSourceId(), EffectOpcode.UserEffect_SkillUse, applyfrom.getLevel(), effect.getLevel()));
                applyfrom.getMap().broadcastMessage(applyfrom, EffectPacket.onUserEffectRemote(applyfrom, effect.getSourceId(), EffectOpcode.UserEffect_SkillUse, applyfrom.getLevel(), effect.getLevel()), false);
            }
        }
        if (applier.effect != null && applier.effect.getSourceId() != 2100010 && (effecForBuffStat = applyfrom.getEffectForBuffStat(SecondaryStat.WizardIgnite)) != null && effecForBuffStat.makeChanceResult(applyfrom)) {
            Skill jk = SkillFactory.getSkill(applier.effect.getSourceId());
            MapleStatEffect skillEffect = applyfrom.getSkillEffect(2100010);
            if (skillEffect != null && jk.getElement() == Element.火) {
                skillEffect.applyAffectedArea(applyfrom, applyto.getPosition());
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null) {
            List<MapleMapObject> mobs;
            int nCount;
            if (applier.effect.getSourceId() == 2111003) {
                applier.effect.applyAffectedArea(player, new Point(applier.ai.position.x, applier.ai.position.y));
            }
            if (applier.effect.getSourceId() == 2111014 || applier.effect.getSkill().getElement() == Element.火) {
                Rectangle rect = applier.effect.getSourceId() == 2111014 ? applier.ai.rect : applier.effect.calculateBoundingBox(new Point(applier.ai.forcedX, applier.ai.forcedY), (applier.ai.direction & 0x80) != 0);
                for (MapleAffectedArea area : player.getMap().getAllAffectedAreasThreadsafe()) {
                    Rectangle aRect;
                    if (area.getSkillID() != 2111013 || !rect.contains(aRect = area.getBounds()) && !aRect.contains(rect) && !aRect.equals(rect) && !rect.intersects(aRect)) continue;
                    ExtraSkill eskill = new ExtraSkill(2111014, area.getPosition());
                    eskill.TriggerSkillID = applier.effect.getSourceId();
                    eskill.Delay = 240;
                    eskill.Value = 1;
                    eskill.TargetOID = area.getObjectId();
                    player.send(MaplePacketCreator.RegisterExtraSkill(2111013, Collections.singletonList(eskill)));
                }
            }
            if (applier.effect.getSourceId() == 2121003) {
                if (applier.ai.mobAttackInfo.size() > 0) {
                    player.cancelSkillCooldown(2121011);
                }
                if (applier.ai.mists != null) {
                    for (Integer id : applier.ai.mists) {
                        MapleAffectedArea mist = player.getMap().getAffectedAreaByOid((int)id);
                        if (mist == null || mist.getSkillID() != 2111003 || mist.getOwnerId() != player.getId()) continue;
                        mist.cancel();
                        player.getMap().disappearMapObject(mist);
                    }
                }
            }
            if (applier.effect.getSourceId() == 2121052 && player.getTempValues().containsKey("藍焰斬Count") && (nCount = ((Integer)player.getTempValues().get("藍焰斬Count")).intValue()) < applier.effect.getX() && !(mobs = player.getMap().getMonstersInRect(applier.effect.calculateBoundingBox(applier.ai.ptAttackRefPoint, false))).isEmpty()) {
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                int bulletCount = Math.min(2, applier.effect.getX() - nCount);
                for (int i = 0; i < bulletCount; ++i) {
                    ForceAtomObject obj = new ForceAtomObject(player.getSpecialStat().gainForceCounter(), 35, i, player.getId(), 0, applier.effect.getSourceId());
                    obj.Idk3 = 1;
                    obj.CreateDelay = 180;
                    obj.EnableDelay = 480;
                    obj.Idk1 = 1;
                    obj.Expire = 5000;
                    obj.Position = new Point(0, 1);
                    obj.addX(player.getSpecialStat().getFieldSkillCounter(2121052));
                    MapleMonster mob = (MapleMonster)mobs.get(i % mobs.size());
                    if (mob != null) {
                        obj.Target = mob.getObjectId();
                    }
                    obj.ObjPosition = new Point(applier.ai.skillposition);
                    obj.ObjPosition.x += Randomizer.rand(-100, 100);
                    obj.ObjPosition.y += Randomizer.rand(-100, 100);
                    createList.add(obj);
                }
                if (!createList.isEmpty()) {
                    player.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)player.getId(), createList, (int)0), player.getPosition());
                }
                player.getTempValues().put("藍焰斬Count", nCount + createList.size());
            }
        }
        return 1;
    }
}

