/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.冒險家.盜賊類別;

import Client.MapleCharacter;
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
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.AdelePacket;
import Packet.BuffPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tools.Randomizer;

public class 夜使者
extends AbstractSkillHandler {
    public 夜使者() {
        this.jobs = new MapleJob[]{MapleJob.刺客, MapleJob.暗殺者, MapleJob.夜使者};
        for (Field field : Config.constants.skills.冒險家_技能群組.夜使者.class.getDeclaredFields()) {
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
            case 4141501: 
            case 4141502: {
                return 4141500;
            }
            case 4141001: {
                return 4141000;
            }
            case 4101014: {
                return 4101013;
            }
            case 4100012: {
                return 0x3E9393;
            }
            case 4121020: {
                return 4121017;
            }
            case 0x3EDDD3: {
                return 4120018;
            }
            case 400041016: 
            case 400041017: 
            case 400041018: {
                return 400041001;
            }
            case 400041062: {
                return 400041061;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 0x3E9393: 
            case 4120018: {
                effect.setOverTime(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.NightLordMark, 1);
                effect.setDebuffTime(effect.getDotTime() * 1000);
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 4111002: {
                statups.put(SecondaryStat.ShadowPartner, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 4121015: {
                monsterStatus.put(MonsterStatus.IndiePDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.PAD, effect.getInfo().get((Object)MapleStatInfo.z));
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 4121017: {
                effect.setDebuffTime(effect.getDuration());
                monsterStatus.put(MonsterStatus.Showdown, effect.getInfo().get((Object)MapleStatInfo.x));
                effect.getInfo().put(MapleStatInfo.time, effect.getS2() * 1000);
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 4121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 4121054: {
                statups.clear();
                statups.put(SecondaryStat.BleedingToxin, 1);
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 400041001: {
                statups.put(SecondaryStat.NightLord_SpreadThrow, 1);
                return 1;
            }
            case 400041038: {
                effect.getInfo().put(MapleStatInfo.bulletCount, effect.getInfo().get((Object)MapleStatInfo.bulletCount) * 5 + effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400041061: {
                statups.put(SecondaryStat.ThrowBlasting, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 0x3E9393: {
                if (applyto.getBuffedValue(SecondaryStat.NightLordMark) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 400041061: {
                applyto.cancelSkillCooldown(400041062);
                return 1;
            }
            case 4121017: {
                if (applyto.getBuffStatValueHolder(4121017) != null) {
                    return 0;
                }
                Point p = applier.pos != null ? applier.pos : applyto.getPosition();
                List<MapleMapObject> mobs = applyto.getMap().getMonstersInRect(SkillFactory.getSkill(4121020).getEffect(applier.effect.getLevel()).calculateBoundingBox(p));
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                for (int i = 0; i < applier.effect.getU(); ++i) {
                    MapleMonster mob;
                    ForceAtomObject obj = new ForceAtomObject(applyto.getSpecialStat().gainForceCounter(), 33, i, applyto.getId(), Randomizer.rand(-360, 360), 4121020);
                    obj.Idk3 = 1;
                    obj.CreateDelay = 810;
                    obj.EnableDelay = 930;
                    obj.Idk1 = 30;
                    obj.Expire = 2810;
                    obj.Position = new Point(0, 1);
                    obj.ObjPosition = new Point(p);
                    obj.ObjPosition.x += Randomizer.rand(-100, 100);
                    obj.ObjPosition.y += Randomizer.rand(-100, -20);
                    createList.add(obj);
                    if (mobs.isEmpty() || (mob = (MapleMonster)mobs.get(i % mobs.size())) == null) continue;
                    obj.Target = mob.getObjectId();
                }
                if (!createList.isEmpty()) {
                    applyto.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)applyto.getId(), createList, (int)0), applyto.getPosition());
                }
                return 1;
            }
            case 4121053: {
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
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effect;
        if (applier.totalDamage > 0L) {
            MapleStatEffect effect2;
            effect = applyfrom.getEffectForBuffStat(SecondaryStat.NightLordMark);
            if (effect != null && (effect2 = applyfrom.getSkillEffect(4120018)) != null) {
                effect = effect2;
            }
            if (applier.effect == null || applier.effect.getSourceId() != 4100012 && applier.effect.getSourceId() != 0x3EDDD3) {
                MonsterEffectHolder meh;
                if (effect == null) {
                    meh = applyto.getEffectHolder(applyfrom.getId(), MonsterStatus.Burned, 0x3E9393);
                    if (meh == null) {
                        meh = applyto.getEffectHolder(applyfrom.getId(), MonsterStatus.Burned, 4120018);
                    }
                } else {
                    meh = applyto.getEffectHolder(applyfrom.getId(), MonsterStatus.Burned, effect.getSourceId());
                    if (meh == null) {
                        if (applyto.isAlive()) {
                            effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
                        } else if (effect.makeChanceResult(applyfrom)) {
                            meh = new MonsterEffectHolder(effect.getSourceId(), effect.getLevel(), 1);
                            meh.effect = effect;
                        }
                    }
                }
                if (meh != null && meh.effect != null) {
                    List<MapleMapObject> mobs = applyfrom.getMap().getMapObjectsInRange(applyto.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER));
                    List<Integer> list = mobs.stream().map(MapleMapObject::getObjectId).collect(Collectors.toList());
                    applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(applyfrom, meh.effect, applyto.getObjectId(), list, applyto.getPosition())), true);
                    applyto.removeEffect(applyfrom.getId(), meh.sourceID);
                }
            }
        }
        if (this.containsJob(applyfrom.getJobWithSub()) && applier.totalDamage > 0L && (effect = applyfrom.getSkillEffect(4110011)) != null) {
            MapleStatEffect skillEffect8 = applyfrom.getSkillEffect(4120011);
            if (skillEffect8 != null) {
                effect = skillEffect8;
            }
            effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
        }
        if (applier.totalDamage > 0L && applyto.isAlive() && (effect = applyfrom.getEffectForBuffStat(SecondaryStat.BleedingToxin)) != null) {
            effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.totalDamage > 0L) {
            Skill skill = SkillFactory.getSkill(400041061);
            if (applier.effect != null && skill != null && player.getSkillLevel(skill) > 0) {
                MapleStatEffect effect;
                int skillLevel;
                int add_skillId = 0;
                int cooldownSkillId = 0;
                int add_skillValue = 0;
                for (int nSkill : skill.getSkillList()) {
                    if (nSkill != applier.effect.getSourceId()) continue;
                    add_skillId = 400041062;
                    cooldownSkillId = 400041062;
                    add_skillValue = 1;
                    break;
                }
                if (add_skillId != 0 && (skillLevel = SkillConstants.getLinkedAttackSkill(400041061)) > 0 && (skill = SkillFactory.getSkill(400041062)) != null && (effect = skill.getEffect(player.getSkillLevel(skillLevel))) != null && !player.isSkillCooling(cooldownSkillId)) {
                    SecondaryStatValueHolder holder = player.getBuffStatValueHolder(SecondaryStat.ThrowBlasting);
                    if (holder == null || holder.value <= 0) {
                        player.registerSkillCooldown(cooldownSkillId, effect.getSubTime(), true);
                    } else {
                        add_skillId = 400041079;
                        add_skillValue = Math.max(2, Math.min(4, applier.ai.mobCount));
                        holder.value -= add_skillValue;
                        if (holder.value <= 0) {
                            player.dispelBuff(400041061);
                        } else {
                            player.send(BuffPacket.giveBuff(player, holder.effect, Collections.singletonMap(SecondaryStat.ThrowBlasting, holder.sourceID)));
                        }
                    }
                    ExtraSkill eskill = new ExtraSkill(add_skillId, new Point(applier.ai.mobAttackInfo.get((int)0).hitX, applier.ai.mobAttackInfo.get((int)0).hitY));
                    eskill.Value = add_skillValue;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.send(MaplePacketCreator.RegisterExtraSkill(400041061, Collections.singletonList(eskill)));
                }
            }
        }
        return -1;
    }
}

