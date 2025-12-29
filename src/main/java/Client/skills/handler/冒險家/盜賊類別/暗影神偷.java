/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.冒險家.盜賊類別;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleForceType;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.inventory.Item;
import Client.skills.ExtraSkill;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.JobConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMapItem;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Opcode.Opcode.EffectOpcode;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 暗影神偷
extends AbstractSkillHandler {
    public 暗影神偷() {
        this.jobs = new MapleJob[]{MapleJob.俠盜, MapleJob.神偷, MapleJob.暗影神偷};
        for (Field field : Config.constants.skills.冒險家_技能群組.暗影神偷.class.getDeclaredFields()) {
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
            case 4241501: 
            case 4241502: {
                return 4241500;
            }
            case 4241001: 
            case 4241002: 
            case 4241003: {
                return 4241000;
            }
            case 4201016: {
                return 4200015;
            }
            case 4221016: {
                return 4221014;
            }
            case 4210014: {
                return 4211006;
            }
            case 4220021: 
            case 4221018: 
            case 4221019: 
            case 4221020: {
                return 4221018;
            }
            case 400041003: 
            case 400041004: 
            case 400041005: {
                return 400041002;
            }
            case 400041026: 
            case 400041027: {
                return 400041025;
            }
            case 400041070: 
            case 400041071: 
            case 400041072: 
            case 400041073: {
                return 400041069;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 4201017: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ThiefSteal, 1);
                return 1;
            }
            case 4211003: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.PickPocket, 1);
                return 1;
            }
            case 4221018: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.PickPocket, 1);
                return 1;
            }
            case 0x404140: {
                statups.put(SecondaryStat.ShadowPartner, effect.getX());
                return 1;
            }
            case 4221014: {
                effect.getInfo().put(MapleStatInfo.time, 45000);
                statups.put(SecondaryStat.Exceed, 1);
                return 1;
            }
            case 4221016: {
                effect.getInfo().put(MapleStatInfo.time, 10000);
                statups.put(SecondaryStat.Shadower_Assassination, 1);
                return 1;
            }
            case 4221020: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BloodyExplosion, 1);
                return 1;
            }
            case 4221053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 4221054: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.FlipTheCoin, 1);
                return 1;
            }
            case 400041002: 
            case 400041003: 
            case 400041004: 
            case 400041005: {
                effect.getInfo().put(MapleStatInfo.time, 5000);
                statups.put(SecondaryStat.Shadower_ShadowAssault, 3);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 4211016: {
                MapleStatEffect effect;
                if (chr.isSkillCooling(4001003) || (effect = chr.getSkillEffect(4001003)) == null) {
                    return 0;
                }
                effect.applyTo(chr);
                return 1;
            }
            case 4211006: 
            case 4221019: {
                MapleStatEffect effect;
                ArrayList<Integer> moboids = new ArrayList<Integer>();
                MapleMapObject bossMob = null;
                for (MapleMapObject mapleMapObject : chr.getMap().getMapObjectsInRange(chr.getPosition(), applier.effect.getRange(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                    moboids.add(mapleMapObject.getObjectId());
                    if (!((MapleMonster)mapleMapObject).isBoss() || bossMob != null && ((MapleMonster)bossMob).getMaxHP() >= ((MapleMonster)mapleMapObject).getMaxHP()) continue;
                    bossMob = (MapleMonster)mapleMapObject;
                }
                if (bossMob != null) {
                    moboids.clear();
                    moboids.add(bossMob.getObjectId());
                }
                if (moboids.isEmpty()) {
                    return 0;
                }
                List<MapleMapItem> stealMesoObject = chr.getMap().getStealMesoObject(chr, applier.effect.getBulletCount(), applier.effect.getRange());
                for (MapleMapItem item : stealMesoObject) {
                    item.setEnterType((byte)0);
                    chr.getMap().disappearMapObject(item);
                }
                if (stealMesoObject.isEmpty()) {
                    return 0;
                }
                MapleForceAtom mapleForceAtom = new MapleForceAtom();
                mapleForceAtom.setOwnerId(chr.getId());
                mapleForceAtom.setBulletItemID(0);
                mapleForceAtom.setArriveDir(0);
                mapleForceAtom.setArriveRange(500);
                mapleForceAtom.setForcedTarget(null);
                mapleForceAtom.setFirstMobID(0);
                ArrayList<Integer> oids = new ArrayList<Integer>();
                for (int i = 0; i < applier.effect.getMobCount(); ++i) {
                    oids.add((Integer)moboids.get(i % moboids.size()));
                }
                mapleForceAtom.setToMobOid(oids);
                if (applier.effect.getSourceId() == 4211006) {
                    mapleForceAtom.setSkillId(4210014);
                    mapleForceAtom.setForceType(MapleForceType.楓幣炸彈.ordinal());
                } else if (applier.effect.getSourceId() == 4221019) {
                    mapleForceAtom.setSkillId(4220021);
                    mapleForceAtom.setForceType(75);
                }
                mapleForceAtom.setInfo(forceFactory.getForceInfo_楓幣炸彈(chr, stealMesoObject, 1000));
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(mapleForceAtom), true);
                chr.send(EffectPacket.encodeUserEffectLocal(applier.effect.getSourceId(), EffectOpcode.UserEffect_SkillUse, chr.getLevel(), applier.effect.getLevel()));
                chr.getMap().broadcastMessage(chr, EffectPacket.onUserEffectRemote(chr, applier.effect.getSourceId(), EffectOpcode.UserEffect_SkillUse, chr.getLevel(), applier.effect.getLevel()), false);
                if (chr.getBuffStatValueHolder(4221020) == null && (effect = chr.getSkillEffect(4221020)) != null && applier.effect.getSourceId() == 4221019) {
                    effect.applyBuffEffect(chr, chr, 2100000000, false, false, true, null);
                }
                return 1;
            }
            case 4221052: {
                c.announce(MaplePacketCreator.sendSkillUseResult(true, applier.effect.getSourceId()));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 4211003: 
            case 4221018: {
                if (applier.effect.getSourceId() != applyto.getBuffSource(SecondaryStat.PickPocket)) {
                    List<MapleMapItem> stealMesoObject = applyto.getMap().getStealMesoObject(applyto, -1, -1);
                    for (MapleMapItem item : stealMesoObject) {
                        applyto.getMap().disappearMapObject(item);
                    }
                }
                if (applier.passive) {
                    applier.buffz = applyto.getBuffedIntZ(SecondaryStat.PickPocket);
                    List<MapleMapItem> stealMesos = applyto.getMap().getStealMesoObject(applyto, applier.effect.getY(), -1);
                    if (applier.buffz != stealMesos.size()) {
                        applier.buffz = stealMesos.size();
                        return 1;
                    }
                } else {
                    applier.buffz = 0;
                }
                return !applier.passive ? 1 : 0;
            }
            case 4221016: {
                if (applier.passive) {
                    Object z = applyfrom.getTempValues().remove("致命暗殺減益OID");
                    int oid = z instanceof Integer ? (Integer)z : 0;
                    SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.Shadower_Assassination);
                    if (mbsvh != null && mbsvh.z == oid) {
                        applier.localstatups.put(SecondaryStat.Shadower_Assassination, Math.min(mbsvh.value + 1, 3));
                    }
                    applier.buffz = oid;
                    return 1;
                }
                return 0;
            }
            case 4221053: {
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
            case 4221054: {
                int value = Math.min(applyto.getBuffedIntValue(SecondaryStat.FlipTheCoin), applier.effect.getY() + 1);
                if (!JobConstants.is幻影俠盜(applyto.getJob())) {
                    value = value < applier.effect.getY() + 1 ? value + 1 : value;
                    applier.localstatups.put(SecondaryStat.FlipTheCoin, value);
                }
                return 1;
            }
            case 400041002: 
            case 400041003: 
            case 400041004: 
            case 400041005: {
                applier.b4 = false;
                if (!applier.primary) {
                    return 0;
                }
                int value = applyto.getBuffedIntValue(SecondaryStat.Shadower_ShadowAssault);
                if (applyto.getBuffedValue(SecondaryStat.Shadower_ShadowAssault) == null) {
                    return 1;
                }
                applyto.dispelEffect(SecondaryStat.Shadower_ShadowAssault);
                applier.localstatups.put(SecondaryStat.Shadower_ShadowAssault, --value);
                if (value <= 0) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 400041069: {
                List<Integer> exList = Arrays.asList(400041073, 400041071, 400041070, 400041072, 400041071, 400041070, 400041072, 400041071, 400041070, 400041072, 400041071, 400041070);
                LinkedList<ExtraSkill> eskills = new LinkedList<ExtraSkill>();
                for (int skill : exList) {
                    ExtraSkill eskill = new ExtraSkill(skill, applyto.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = applyto.isFacingLeft() ? 0 : 1;
                    eskills.add(eskill);
                }
                applyto.send(MaplePacketCreator.RegisterExtraSkill(400041069, eskills));
                return 0;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.totalDamage > 0L) {
            MapleStatEffect effect = applyfrom.getEffectForBuffStat(SecondaryStat.ThiefSteal);
            if (effect != null && applyto.isShouldDropAssassinsMark() && Randomizer.isSuccess(effect.getZ())) {
                applyto.setShouldDropAssassinsMark(false);
                MapleMapItem mdrop = new MapleMapItem(new Item(!applyto.isBoss() ? 2431835 : 2431850, (short)0, (short) 1), applyto.getPosition(), applyto, applyfrom, (byte)0, false, 0);
                mdrop.setOnlySelfID(applyfrom.getId());
                mdrop.setSourceOID(applyto.getObjectId());
                applyfrom.getMap().spawnMobDrop(mdrop, applyto, applyfrom);
            }
            if ((effect = applyfrom.getEffectForBuffStat(SecondaryStat.PickPocket)) != null) {
                int prop = effect.getProp(applyfrom);
                if (applier.effect.getSourceId() == 4221017) {
                    prop = prop * applier.effect.getX() / 100;
                }
                for (int i = 0; i < applier.effect.getAttackCount(); ++i) {
                    if (!Randomizer.isSuccess(prop) || applyfrom.getBuffedIntZ(SecondaryStat.PickPocket) >= effect.getY()) continue;
                    Point p = new Point(applyto.getPosition());
                    p.y = applyfrom.getMap().getFootholds().findBelow((Point)applyto.getPosition()).getPoint1().y;
                    applyfrom.getMap().spawnMobMesoDrop(1, p, applyto, applyfrom, true, (byte)0, 0, effect.getSourceId());
                    effect.unprimaryPassiveApplyTo(applyfrom);
                }
            }
            if ((effect = applyfrom.getSkillEffect(4210010)) != null) {
                MapleStatEffect effect1 = applyfrom.getSkillEffect(4220011);
                if (effect1 != null) {
                    effect = effect1;
                }
                effect.applyMonsterEffect(applyfrom, applyto, effect.getDotTime() * 1000);
            }
            if (applier.effect != null && applier.effect.getSourceId() == 4221016) {
                applyfrom.dispelEffect(4221020);
                if (applyfrom.getSkillEffect(400041025) != null) {
                    applyfrom.getTempValues().put("致命暗殺減益OID", applyto.getObjectId());
                    applier.effect.unprimaryPassiveApplyTo(applyfrom);
                }
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect eff = player.getEffectForBuffStat(SecondaryStat.DarkSight);
        if (eff != null && eff.getSourceId() == 4001003 && (eff = player.getSkillEffect(4210015)) != null) {
            int prop;
            int n = prop = player.getMap().getAllAffectedAreasThreadsafe().stream().anyMatch(mist -> (mist.getSkillID() == 4221006 || mist.getSkillID() == 4221052) && mist.getOwnerId() == player.getId() && mist.getArea().contains(player.getPosition())) ? 100 : eff.getProp(player);
            if (applier.effect != null && prop != 100) {
                switch (applier.effect.getSourceId()) {
                    case 4221014: {
                        prop = 100;
                        break;
                    }
                    case 4221016: {
                        prop = 0;
                    }
                }
            }
            if (!Randomizer.isSuccess(prop)) {
                player.dispelEffect(SecondaryStat.DarkSight);
            }
        }
        if (!applier.ai.mobAttackInfo.isEmpty() && (eff = player.getEffectForBuffStat(SecondaryStat.FlipTheCoin)) != null) {
            eff.applyBuffEffect(player, player, 2100000000, false, false, true, null);
        }
        return 1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 4211003: 
            case 4221018: {
                if (applier.overwrite) break;
                List<MapleMapItem> stealMesoObject = player.getMap().getStealMesoObject(player, -1, -1);
                for (MapleMapItem item : stealMesoObject) {
                    player.getMap().disappearMapObject(item);
                }
                break;
            }
        }
        return -1;
    }
}

