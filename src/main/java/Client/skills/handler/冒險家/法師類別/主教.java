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
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.JobConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.buffs.MapleStatEffectFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleSummon;
import Opcode.Opcode.EffectOpcode;
import Packet.AdelePacket;
import Packet.BuffPacket;
import Packet.EffectPacket;
import Packet.SummonPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 主教
extends AbstractSkillHandler {
    public 主教() {
        this.jobs = new MapleJob[]{MapleJob.僧侶, MapleJob.祭司, MapleJob.主教};
        for (Field field : Config.constants.skills.冒險家_技能群組.type_法師.主教.class.getDeclaredFields()) {
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
            case 2341504: 
            case 2341505: 
            case 2341506: {
                return 2341500;
            }
            case 2341001: {
                return 2341000;
            }
            case 2301010: {
                return 2301002;
            }
            case 2311014: {
                return 2311011;
            }
            case 2311015: 
            case 2311017: {
                return 2311001;
            }
            case 2310013: {
                return 2311009;
            }
            case 2321016: {
                return 2321015;
            }
            case 2321055: {
                return 2321052;
            }
            case 400021033: 
            case 400021052: {
                return 400021032;
            }
            case 400021077: {
                return 400021070;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 2300009: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BlessEnsenble, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 2301010: {
                monsterStatus.put(MonsterStatus.IndieMDR, effect.getX());
                return 1;
            }
            case 2301004: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.Bless, effect.getLevel());
                return 1;
            }
            case 2311002: 
            case 2311011: 
            case 2311014: 
            case 400021032: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 2311012: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AntiMagicShell, effect.getU());
                return 1;
            }
            case 2311015: {
                statups.put(SecondaryStat.TriumphFeather, 1);
                return 1;
            }
            case 2311003: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.HolySymbol, effect.getX());
                return 1;
            }
            case 2311007: {
                statups.put(SecondaryStat.TeleportMasteryOn, 1);
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 2311016: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.TeleportMasteryRange, 1);
                return 1;
            }
            case 2311009: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.HolyMagicShell, effect.getInfo().get((Object)MapleStatInfo.x));
                effect.getInfo().put(MapleStatInfo.cooltime, effect.getInfo().get((Object)MapleStatInfo.y));
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.z).intValue() / 100.0);
                return 1;
            }
            case 2310013: {
                statups.put(SecondaryStat.HolyMagicShellReUse, 1);
                return 1;
            }
            case 2321015: {
                statups.put(SecondaryStat.HolyWater, 0);
                return 1;
            }
            case 2321016: {
                statups.put(SecondaryStat.IndiePMdR, effect.getU());
                statups.put(SecondaryStat.IndieHitDamR, effect.getV());
                statups.put(SecondaryStat.HolyBlood, 1);
                return 1;
            }
            case 2321006: {
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 2321004: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                effect.setMpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.prop));
                statups.put(SecondaryStat.Infinity, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 2321003: {
                effect.setDebuffTime(effect.getSubTime() * 1000);
                monsterStatus.put(MonsterStatus.BahamutLightElemAddDam, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 2321005: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieMHP, effect.getInfo().get((Object)MapleStatInfo.indieMhp));
                statups.put(SecondaryStat.IndieMMP, effect.getInfo().get((Object)MapleStatInfo.indieMmp));
                statups.put(SecondaryStat.AdvancedBless, effect.getInfo().get((Object)MapleStatInfo.mpConReduce));
                return 1;
            }
            case 2320011: {
                effect.getInfo().put(MapleStatInfo.time, 5000);
                statups.put(SecondaryStat.ArcaneAim, 1);
                return 1;
            }
            case 2321055: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.HeavensDoor, 1);
                statups.put(SecondaryStat.HeavensDoorNotTime, 1);
                return 1;
            }
            case 2321053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 2321054: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.VengeanceOfAngel, 1);
                return 1;
            }
            case 400021003: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.BishopPray, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400021033: {
                effect.setDebuffTime(effect.getSubTime() * 1000);
                monsterStatus.put(MonsterStatus.BahamutLightElemAddDam, effect.getY());
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 400021052: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, 0);
                return 1;
            }
            case 400021077: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400021086: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.CannonShooter_BFCannonBall, 0);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 2311001: {
                applier.pos = slea.readPos();
                chr.setPosition(applier.pos);
                return 1;
            }
            case 2321015: {
                SecondaryStatValueHolder mbsvh = chr.getBuffStatValueHolder(SecondaryStat.HolyWater);
                if (mbsvh == null) {
                    return 0;
                }
                short nCount = slea.readShort();
                for (int i = 0; i < Math.min(nCount, applier.effect.getW()); ++i) {
                    if (mbsvh.value <= 0) {
                        mbsvh.value = 0;
                        break;
                    }
                    --mbsvh.value;
                    applier.effect.applyAffectedArea(chr, slea.readPosInt());
                }
                return 1;
            }
            case 2321003: {
                chr.dispelEffect(400021032);
                chr.dispelEffect(400021033);
                return 1;
            }
            case 400021032: 
            case 400021033: {
                chr.dispelEffect(2321003);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 2301002: 
            case 2321007: {
                SecondaryStatValueHolder mbsvh2 = applyfrom.getBuffStatValueHolder(SecondaryStat.HolyBlood);
                int hpValue = 0;
                if (applier.effect.getSourceId() == 2301002) {
                    hpValue = MapleStatEffectFactory.makeHealHP((double)applier.effect.getHp() / 100.0, applyfrom.getStat().getTotalMagic(), 3.0, 5.0);
                }
                Point p = applyfrom.getPosition();
                Rectangle rect = applier.effect.getSourceId() == 2321007 ? MapleStatEffectFactory.calculateBoundingBox(p, applyfrom.isFacingLeft(), new Point(-25, -37), new Point(25, 38), applier.effect.getInfo().get((Object)MapleStatInfo.range)) : applier.effect.calculateBoundingBox(p);
                int count = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersInRect(rect)) {
                    if (chr != applyfrom && chr.getParty() != applyfrom.getParty()) continue;
                    if (applier.effect.getSourceId() == 2321007) {
                        hpValue = chr.getStat().getCurrentMaxHP() * applier.effect.getHp() / 100;
                    } else if (chr.getBuffStatValueHolder(SecondaryStat.BanMap) != null) {
                        hpValue = -hpValue;
                    } else if (chr.getStat().getHp() < chr.getStat().getCurrentMaxHP()) {
                        ++count;
                    }
                    if (mbsvh2 != null && mbsvh2.effect != null) {
                        hpValue -= hpValue * mbsvh2.effect.getQ() / 100;
                    }
                    chr.addHP(hpValue);
                }
                if (count > 0) {
                    applier.cooldown -= count * applier.effect.getY() * 1000;
                }
                return 1;
            }
            case 2311012: {
                applier.cooldown = 0;
                return 1;
            }
            case 2311001: {
                Rectangle rect = applier.effect.calculateBoundingBox(applyfrom.getPosition());
                int count = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersInRect(rect)) {
                    if (chr != applyfrom && chr.getParty() != applyfrom.getParty()) continue;
                    LinkedList<SecondaryStatValueHolder> mbsvhs = new LinkedList();
                    for (Map.Entry<SecondaryStat, List<SecondaryStatValueHolder>> entry : chr.getAllEffects().entrySet()) {
                        if (!entry.getKey().isNormalDebuff() && !entry.getKey().isCriticalDebuff()) continue;
                        entry.getValue().stream().filter(mbsvh -> mbsvh.effect instanceof MobSkill).forEach(mbsvhs::add);
                    }
                    if (mbsvhs.size() <= 0) continue;
                    ++count;
                    mbsvhs.forEach(mbsvh -> chr.cancelEffect(mbsvh.effect, mbsvh.startTime));
                }
                for (MapleMapObject obj : applyfrom.getMap().getMonstersInRect(rect)) {
                    MapleMonster mob = (MapleMonster)obj;
                    LinkedList skills = new LinkedList();
                    for (List<MonsterEffectHolder> mehs : mob.getAllEffects().values()) {
                        mehs.stream().filter(meh -> meh.effect instanceof MobSkill && !skills.contains(meh.effect.getSourceId())).forEach(meh -> skills.add(meh.effect.getSourceId()));
                    }
                    Iterator<List<MonsterEffectHolder>> iterator = skills.iterator();
                    while (iterator.hasNext()) {
                        int skill = (Integer)((Object)iterator.next());
                        mob.removeEffect(0, skill);
                    }
                }
                if (count > 0) {
                    applier.cooldown -= applier.effect.getY() * count * 1000;
                    applyfrom.reduceSkillCooldown(2311012, applier.effect.getDuration() * count);
                }
                return 1;
            }
            case 2321016: {
                int intValue = applyfrom.getStat().getTotalInt();
                if (intValue >= applier.effect.getS()) {
                    applier.cooldown -= intValue / applier.effect.getS() * applier.effect.getW2() * 1000;
                    applier.cooldown = Math.max(applier.cooldown, applier.effect.getInfo().get((Object)MapleStatInfo.v2) * 1000);
                }
                return 1;
            }
            case 2321006: {
                int duration = applier.effect.getBuffDuration(applyfrom);
                int count = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersInRect(applier.effect.calculateBoundingBox(applyfrom.getPosition()))) {
                    if (applyfrom == chr || applyfrom.getParty() != chr.getParty() || chr.isAlive()) continue;
                    chr.heal();
                    applier.effect.applyBuffEffect(applyfrom, chr, duration, false, false, true, null);
                    ++count;
                }
                if (count <= 0) {
                    applier.cooldown = 0;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect instanceof MobSkill) {
            SecondaryStatValueHolder mbsvh2;
            boolean isCriticalDebuff = false;
            for (SecondaryStat stat : applier.localstatups.keySet()) {
                if (!stat.isCriticalDebuff()) continue;
                isCriticalDebuff = true;
                break;
            }
            if (isCriticalDebuff && (mbsvh2 = applyto.getBuffStatValueHolder(SecondaryStat.AntiMagicShell)) != null && mbsvh2.value > 0) {
                applyto.registerSkillCooldown(mbsvh2.effect, true);
                applyto.dispelEffect(SecondaryStat.AntiMagicShell);
                return 0;
            }
            return -1;
        }
        switch (applier.effect.getSourceId()) {
            case 2300009: {
                int count;
                if (applyfrom.getParty() != null) {
                    count = (int)applyfrom.getParty().getMembers().stream().filter(member -> member.getChr() != null && member.getFieldID() == applyfrom.getMapId() && member.getChr().getPosition().distance(applyfrom.getPosition()) <= 700.0).filter(member -> member.getChr().getAllEffects().values().stream().flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new)).stream().anyMatch(mbsvh -> mbsvh != null && mbsvh.effect != null && this.containsSkill(mbsvh.effect.getSourceId()) && mbsvh.effect.isPartyBuff())).count();
                } else {
                    int n = count = applyfrom.getChrBuffStatValueHolder(applyfrom.getId()).stream().anyMatch(mbsvh -> mbsvh != null && mbsvh.effect != null && mbsvh.effect.isPartyBuff()) ? 1 : 0;
                }
                if (count <= 0) {
                    applyfrom.dispelEffect(2300009);
                    return 0;
                }
                MapleStatEffect effect = applyfrom.getSkillEffect(2320013);
                if (effect == null) {
                    effect = applier.effect;
                }
                applier.localstatups.put(SecondaryStat.BlessEnsenble, count * effect.getX());
                return 1;
            }
            case 2301004: {
                SecondaryStatValueHolder mbsvh3 = applyto.getBuffStatValueHolder(SecondaryStat.AdvancedBless);
                if (mbsvh3 != null) {
                    applyto.cancelEffect(mbsvh3.effect, mbsvh3.startTime);
                }
                return 1;
            }
            case 2311012: {
                applier.buffz = 0;
                return 1;
            }
            case 2311003: {
                MapleStatEffect effect;
                SecondaryStatValueHolder mbsvh4;
                if (!applier.primary && (mbsvh4 = applyto.getBuffStatValueHolder(SecondaryStat.HolySymbol, applier.effect.getSourceId())) != null) {
                    LinkedHashMap<SecondaryStat, Integer> statups = new LinkedHashMap<SecondaryStat, Integer>();
                    statups.put(SecondaryStat.HolySymbol, applier.effect.getSourceId());
                    applier.buffz = mbsvh4.z;
                    if (applier.buffz == 0) {
                        mbsvh4.value = applier.effect.getX() * applier.effect.getY() / 100;
                        mbsvh4.DropRate = 0;
                    } else {
                        MapleStatEffect effect2;
                        mbsvh4.value = applier.effect.getX();
                        if (applyfrom != null && (effect2 = applyfrom.getSkillEffect(2320048)) != null) {
                            mbsvh4.DropRate = effect2.getV();
                        }
                    }
                    mbsvh4 = applyto.getBuffStatValueHolder(SecondaryStat.IndieAsrR, applier.effect.getSourceId());
                    if (mbsvh4 != null) {
                        mbsvh4.value = applier.buffz == 0 ? 0 : 10;
                        statups.put(SecondaryStat.IndieAsrR, applier.effect.getSourceId());
                    }
                    if ((mbsvh4 = applyto.getBuffStatValueHolder(SecondaryStat.IndieTerR, applier.effect.getSourceId())) != null) {
                        mbsvh4.value = applier.buffz == 0 ? 0 : 10;
                        statups.put(SecondaryStat.IndieTerR, applier.effect.getSourceId());
                    }
                    applyto.send(BuffPacket.giveBuff(applyto, applier.effect, statups));
                    applyto.getStat().recalcLocalStats(false, applyto);
                    return 0;
                }
                applier.buffz = 1;
                if (applyfrom == applyto && (effect = applyfrom.getSkillEffect(2320046)) != null) {
                    applier.localstatups.put(SecondaryStat.HolySymbol, applier.localstatups.get(SecondaryStat.HolySymbol) + effect.getY());
                }
                if ((effect = applyfrom.getSkillEffect(2320047)) != null) {
                    applier.localstatups.put(SecondaryStat.IndieAsrR, effect.getASRRate());
                    applier.localstatups.put(SecondaryStat.IndieTerR, effect.getTERRate());
                }
                return 1;
            }
            case 2311007: {
                applier.duration = 2100000000;
                return 1;
            }
            case 2311009: {
                if (applyto.getBuffStatValueHolder(2310013) != null) {
                    return 0;
                }
                SkillFactory.getSkill(2310013).getEffect(1).applyBuffEffect(applyfrom, applyto, applier.effect.getY() * 1000, false, false, true, null);
                int hpValue = applyto.getStat().getCurrentMaxHP() * applier.effect.getZ() / 100;
                SecondaryStatValueHolder mbsvh5 = applyfrom.getBuffStatValueHolder(SecondaryStat.HolyBlood);
                if (mbsvh5 != null && mbsvh5.effect != null) {
                    hpValue -= hpValue * mbsvh5.effect.getQ() / 100;
                }
                applyto.addHP(hpValue);
                MapleStatEffect effect = applyfrom.getSkillEffect(2320043);
                if (effect != null) {
                    applier.localstatups.put(SecondaryStat.HolyMagicShell, applier.localstatups.get(SecondaryStat.HolyMagicShell) + effect.getX());
                }
                applier.buffz = applier.effect.getW();
                effect = applyfrom.getSkillEffect(2320045);
                if (effect != null) {
                    applier.buffz += effect.getW();
                }
                return 1;
            }
            case 2321015: {
                int count;
                SecondaryStatValueHolder mbsvh6 = applyfrom.getBuffStatValueHolder(SecondaryStat.HolyWater);
                int n = count = mbsvh6 == null ? 0 : mbsvh6.value;
                if (!applier.primary) {
                    ++count;
                }
                count = Math.min(Math.max(0, count), applier.effect.getW());
                if (mbsvh6 != null) {
                    mbsvh6.value = count;
                }
                if (count <= 0) {
                    applyfrom.dispelEffect(SecondaryStat.HolyWater);
                    return 0;
                }
                applier.duration = 2100000000;
                applier.localstatups.put(SecondaryStat.HolyWater, count);
                return 1;
            }
            case 2321016: {
                int intValue = applyfrom.getStat().getTotalInt();
                if (intValue >= applier.effect.getS()) {
                    applier.localstatups.put(SecondaryStat.IndiePMdR, Math.min(applier.effect.getS2(), applier.localstatups.get(SecondaryStat.IndiePMdR) + intValue / applier.effect.getS() * applier.effect.getU2()));
                }
                return 1;
            }
            case 2321006: {
                if (applyfrom == applyto) {
                    return 0;
                }
                return 1;
            }
            case 2321005: {
                SecondaryStatValueHolder mbsvh7 = applyto.getBuffStatValueHolder(SecondaryStat.Bless);
                if (mbsvh7 != null) {
                    applyto.cancelEffect(mbsvh7.effect, mbsvh7.startTime);
                }
                applier.buffz = applier.effect.getX();
                MapleStatEffect effect = applyfrom.getSkillEffect(2320049);
                if (effect != null) {
                    applier.buffz += effect.getX();
                    applier.localstatups.put(SecondaryStat.IndiePAD, effect.getX());
                    applier.localstatups.put(SecondaryStat.IndieMAD, effect.getY());
                }
                if ((effect = applyfrom.getSkillEffect(2320051)) != null) {
                    applier.localstatups.put(SecondaryStat.IndieMHP, applier.localstatups.get(SecondaryStat.IndieMHP) + effect.getIndieMHp());
                    applier.localstatups.put(SecondaryStat.IndieMMP, applier.localstatups.get(SecondaryStat.IndieMMP) + effect.getIndieMMp());
                }
                return 1;
            }
            case 2320011: {
                if (applyto.getBuffedValue(SecondaryStat.ArcaneAim) != null) {
                    applier.localstatups.put(SecondaryStat.ArcaneAim, Math.min(applier.effect.getY(), applyto.getBuffedIntValue(SecondaryStat.ArcaneAim) + 1));
                }
                return 1;
            }
            case 2321053: {
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
            case 2321055: {
                if (applyto.getBuffStatValueHolder(SecondaryStat.HeavensDoorNotTime) != null) {
                    return 0;
                }
                if (applier.primary) {
                    applier.effect.applyBuffEffect(applyfrom, applyto, applier.effect.getDuration(), false, false, true, null);
                    applier.duration = 2100000000;
                    applier.localstatups.remove(SecondaryStat.HeavensDoorNotTime);
                } else {
                    applier.localstatups.remove(SecondaryStat.HeavensDoor);
                }
                return 1;
            }
            case 400021003: {
                if (applier.primary) {
                    if (applyfrom != applyto) {
                        return 0;
                    }
                } else {
                    applier.duration = 2000;
                    applier.localstatups.clear();
                    int intValue = applyfrom.getStat().getTotalInt();
                    if (applyfrom != applyto && intValue >= applier.effect.getU()) {
                        applier.localstatups.put(SecondaryStat.IndieBooster, Math.max(-(applier.effect.getU() / intValue), applier.effect.getV()));
                    }
                    applier.localstatups.put(SecondaryStat.IndiePMdR, applier.effect.getQ());
                    if (applyfrom != applyto && intValue >= applier.effect.getQ2()) {
                        applier.localstatups.put(SecondaryStat.IndiePMdR, Math.min(applier.localstatups.get(SecondaryStat.IndiePMdR) + applier.effect.getQ2() / intValue, applier.effect.getW()));
                    }
                }
                return 1;
            }
            case 400021032: {
                if (!applier.primary) {
                    return 0;
                }
                SkillFactory.getSkill(400021033).getEffect(applier.effect.getLevel()).applyBuffEffect(applyfrom, applyto, applier.duration, applier.primary, applier.att, applier.passive, applier.pos);
                return 1;
            }
            case 400021033: 
            case 400021086: {
                if (!applier.primary) {
                    return 0;
                }
                return 1;
            }
            case 400021052: {
                MapleStatEffect effect = SkillFactory.getSkill(400021032).getEffect(applier.effect.getLevel());
                if (effect == null) {
                    return 0;
                }
                int intValue = applyfrom.getStat().getTotalInt();
                applier.localstatups.put(SecondaryStat.IndiePMdR, effect.getW());
                if (applyfrom != applyto && intValue >= effect.getAttackCount()) {
                    applier.localstatups.put(SecondaryStat.IndiePMdR, Math.min(applier.localstatups.get(SecondaryStat.IndiePMdR) + effect.getAttackCount() / intValue, effect.getDamage()));
                }
                applyto.addHPMP(effect.getY(), 0);
                applyto.send(EffectPacket.showSkillAffected(-1, 400021032, effect.getLevel(), 0));
                return 1;
            }
            case 400021077: {
                if (applyfrom == applyto && !applier.effect.calculateBoundingBox(applier.pos).contains(applyfrom.getPosition())) {
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterRegisterEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 2311003: {
                MapleStatEffect effect;
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.HolySymbol, applier.effect.getSourceId());
                if (mbsvh != null && mbsvh.z == 1 && (effect = applyfrom.getSkillEffect(2320048)) != null) {
                    mbsvh.DropRate = effect.getV();
                }
                return 1;
            }
            case 2321015: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.HolyWater);
                if (mbsvh != null && mbsvh.sourceID == applier.effect.getSourceId()) {
                    mbsvh.sourceID = 0;
                }
                return 1;
            }
            case 2321005: {
                MapleStatEffect effect;
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.AdvancedBless, applier.effect.getSourceId());
                if (mbsvh != null && (effect = applyfrom.getSkillEffect(2320050)) != null) {
                    mbsvh.BDR = effect.getInfo().get((Object)MapleStatInfo.bdR);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyMonsterEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.effect == null) {
            return -1;
        }
        if (applier.effect.getSourceId() == 2311007) {
            applier.prop = applier.effect.getSubProp();
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effect = applyfrom.getSkillEffect(2300000);
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
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect effect;
        SecondaryStatValueHolder mbsvh;
        if (applier.effect == null) {
            return -1;
        }
        if (applier.ai.mobAttackInfo.size() > 0 && 2311017 != applier.ai.skillId && (mbsvh = player.getBuffStatValueHolder(SecondaryStat.TriumphFeather)) != null && !player.isSkillCooling(2311017)) {
            Point p = applier.ai.skillposition != null ? applier.ai.skillposition : (applier.ai.position != null ? applier.ai.position : player.getPosition());
            List<MapleMapObject> mobs = player.getMap().getMonstersInRect(SkillFactory.getSkill(2311017).getEffect(mbsvh.effect.getLevel()).calculateBoundingBox(p));
            if (!mobs.isEmpty()) {
                player.registerSkillCooldown(2311017, mbsvh.effect.getU(), true);
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                for (int i = 0; i < mbsvh.effect.getBulletCount(); ++i) {
                    ForceAtomObject obj = new ForceAtomObject(player.getSpecialStat().gainForceCounter(), 37, i, player.getId(), Randomizer.rand(-360, 360), 2311017);
                    obj.EnableDelay = 60;
                    obj.Expire = mbsvh.effect.getW() * 1000;
                    obj.Position = new Point(0, 1);
                    obj.ObjPosition = new Point(p);
                    obj.ObjPosition.x += Randomizer.rand(-100, 100);
                    obj.ObjPosition.y += Randomizer.rand(-100, -20);
                    createList.add(obj);
                    MapleMonster mob = (MapleMonster)mobs.get(i % mobs.size());
                    if (mob == null) continue;
                    obj.Target = mob.getObjectId();
                }
                if (!createList.isEmpty()) {
                    player.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)player.getId(), createList, (int)0), player.getPosition());
                }
            }
        }
        if (applier.effect.getSourceId() == 2321007 && !applier.ai.mobAttackInfo.isEmpty() && (effect = player.getSkillEffect(2321015)) != null) {
            int count = (Integer)player.getTempValues().getOrDefault("天使之箭累計次數", 0) + 1;
            if (count >= effect.getU()) {
                count = 0;
                effect.applyBuffEffect(player, player, 2100000000, false, false, true, null);
            }
            player.getTempValues().put("天使之箭累計次數", count);
        }
        return -1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        if (!applier.overwrite) {
            switch (applier.effect.getSourceId()) {
                case 400021032: 
                case 400021033: {
                    MapleStatEffect effect;
                    if (player.getBuffStatValueHolder(400021032) != null || player.getBuffStatValueHolder(400021033) != null || (effect = player.getSkillEffect(2321003)) == null) break;
                    effect.applyTo(player, true);
                }
            }
        }
        return -1;
    }

    public static void handlePassive(MapleCharacter chr, int numTimes) {
        SecondaryStatValueHolder mbsvh;
        if (numTimes % 4 == 0) {
            MapleSummon summonBySkillID;
            MapleCharacter fchr;
            int buffz;
            mbsvh = chr.getBuffStatValueHolder(SecondaryStat.HolySymbol, 2311003);
            if (mbsvh != null && mbsvh.effect != null && chr.getMap() != null && mbsvh.fromChrID != chr.getId() && mbsvh.z != (buffz = (fchr = chr.getMap().getPlayerObject(mbsvh.fromChrID)) != null && mbsvh.effect.calculateBoundingBox2(fchr.getPosition()).contains(chr.getPosition()) ? 1 : 0)) {
                mbsvh.z = buffz;
                mbsvh.effect.applyBuffEffect(fchr, chr, mbsvh.getLeftTime(), false, false, true, null);
            }
            if ((summonBySkillID = chr.getSummonBySkillID(400021033)) != null && !chr.getMap().getMonstersInRange(chr.getPosition(), 500.0).isEmpty()) {
                chr.getClient().announce(SummonPacket.SummonedAssistAttackRequest(chr.getId(), summonBySkillID.getObjectId(), 0));
            }
        }
        if (numTimes % 2 == 0 && JobConstants.is主教(chr.getJob()) && (mbsvh = chr.getBuffStatValueHolder(SecondaryStat.BishopPray)) != null) {
            for (MapleCharacter tchr : chr.getMap().getCharactersInRect(mbsvh.effect.calculateBoundingBox(chr.getPosition()))) {
                if (tchr == null || tchr != chr && tchr.getParty() != chr.getParty()) continue;
                mbsvh.effect.applyBuffEffect(chr, tchr, 2000, false, false, true, null);
            }
        }
    }
}

