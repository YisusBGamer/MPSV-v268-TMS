/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.client.party.Party
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
import Config.constants.skills.冒險家_技能群組.type_法師.冰雷;
import Net.server.MapleStatInfo;
import Net.server.Timer;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.Element;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.MapleFoothold;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Opcode.Opcode.EffectOpcode;
import Packet.EffectPacket;
import Packet.MobPacket;
import SwordieX.client.party.Party;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 冰雷大魔導士
extends AbstractSkillHandler {
    public 冰雷大魔導士() {
        this.jobs = new MapleJob[]{MapleJob.冰雷巫師, MapleJob.冰雷魔導士, MapleJob.冰雷大魔導士};
        for (Field field : 冰雷.class.getDeclaredFields()) {
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
            case 0x2233DD: 
            case 2241503: 
            case 2241504: {
                return 2241500;
            }
            case 2241001: {
                return 2241000;
            }
            case 2211015: {
                return 2211011;
            }
            case 2220014: {
                return 2221007;
            }
            case 2221055: 
            case 2221056: {
                return 2221054;
            }
            case 400020002: {
                return 400021002;
            }
            case 400021031: 
            case 400021040: {
                return 400021030;
            }
            case 400021112: {
                return 400021094;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 2201008: 
            case 2211002: 
            case 2211014: 
            case 2221007: 
            case 2221012: {
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.s));
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.time) * 2);
                return 1;
            }
            case 2201009: {
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.s));
                statups.put(SecondaryStat.ChillingStep, 1);
                return 1;
            }
            case 2201001: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieMAD, effect.getInfo().get((Object)MapleStatInfo.indieMad));
                return 1;
            }
            case 2211012: {
                statups.put(SecondaryStat.AntiMagicShell, 1);
                return 1;
            }
            case 2211011: 
            case 2211015: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 2211007: {
                statups.put(SecondaryStat.TeleportMasteryOn, 1);
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 2211017: 
            case 2221045: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.TeleportMasteryRange, 1);
                return 1;
            }
            case 2221006: {
                monsterStatus.put(MonsterStatus.Stun, 1);
                return 1;
            }
            case 2221011: {
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                statups.put(SecondaryStat.IndieIgnorePCounter, 1);
                monsterStatus.put(MonsterStatus.IndiePDR, effect.getInfo().get((Object)MapleStatInfo.y));
                monsterStatus.put(MonsterStatus.IndieMDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 2221004: {
                effect.setHpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                effect.setMpR((double)effect.getInfo().get((Object)MapleStatInfo.y).intValue() / 100.0);
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.prop));
                statups.put(SecondaryStat.Infinity, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 2221005: {
                effect.setDebuffTime(effect.getInfo().get((Object)MapleStatInfo.subTime) * 1000);
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.s));
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 2221053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 2220010: {
                effect.getInfo().put(MapleStatInfo.time, 5000);
                statups.put(SecondaryStat.ArcaneAim, 1);
                return 1;
            }
            case 2221054: {
                effect.setDebuffTime(effect.getInfo().get((Object)MapleStatInfo.time));
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.s));
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndieTerR, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.w));
                statups.put(SecondaryStat.IceAura, 1);
                return 1;
            }
            case 2221055: {
                statups.put(SecondaryStat.IndieTerR, effect.getInfo().get((Object)MapleStatInfo.z));
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.w));
                statups.put(SecondaryStat.IceAuraZone, 1);
                return 1;
            }
            case 2221056: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                return 1;
            }
            case 400020002: {
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.s));
                return 1;
            }
            case 400021067: {
                effect.setDebuffTime(effect.getSubTime() * 1000);
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.s));
                return 1;
            }
            case 400021031: {
                effect.getInfo().put(MapleStatInfo.time, 250);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400021030) {
            applier.pos = slea.readPos();
            slea.readByte();
            int size = slea.readInt();
            for (int i = 0; i < size; ++i) {
                Point pos1 = slea.readPos();
                Timer.MapTimer.getInstance().schedule(() -> chr.getSkillEffect(400021031).applyAffectedArea(chr, pos1), (long)i * 200L);
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 2211012) {
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
                if (mbsvh.z == 0) {
                    mbsvh.effect.unprimaryPassiveApplyTo(applyto);
                    applyto.registerSkillCooldown(mbsvh.effect, true);
                }
                return 0;
            }
            return -1;
        }
        switch (applier.effect.getSourceId()) {
            case 2211012: {
                if (applier.primary) {
                    applier.duration = 2100000000;
                    applier.buffz = 0;
                } else {
                    applier.buffz = applier.duration;
                }
                return 1;
            }
            case 2201009: 
            case 2211007: {
                applier.duration = 2100000000;
                return 1;
            }
            case 2211011: {
                SecondaryStatValueHolder buffStatValueHolder = applyfrom.getBuffStatValueHolder(2211015);
                if (buffStatValueHolder != null) {
                    applyfrom.cancelEffect(buffStatValueHolder.effect, true, buffStatValueHolder.startTime);
                }
                return 1;
            }
            case 2211015: {
                applyfrom.dispelEffect(2211011);
                return 1;
            }
            case 2220010: {
                if (applyto.getBuffedValue(SecondaryStat.ArcaneAim) != null) {
                    applier.localstatups.put(SecondaryStat.ArcaneAim, Math.min(applier.effect.getY(), applyto.getBuffedIntValue(SecondaryStat.ArcaneAim) + 1));
                }
                return 1;
            }
            case 2221053: {
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
            case 2221054: {
                if (applyfrom == applyto) {
                    applyfrom.dispelEffect(2221055);
                    applyfrom.dispelEffect(2221056);
                } else {
                    if (applyfrom.getBuffStatValueHolder(2221055) != null) {
                        return 0;
                    }
                    applier.localstatups.remove(SecondaryStat.IceAura);
                }
                return 1;
            }
            case 2221055: {
                if (applyfrom == applyto) {
                    applyfrom.dispelEffect(2221054);
                    if (applier.primary) {
                        SkillFactory.getSkill(2221056).getEffect(applier.effect.getLevel()).applyBuffEffect(applyfrom, applier.duration, false);
                        return 0;
                    }
                } else {
                    if (applyfrom.getBuffStatValueHolder(2221054) != null) {
                        return 0;
                    }
                    applier.localstatups.remove(SecondaryStat.IceAuraZone);
                }
                applier.duration = 2100000000;
                return 1;
            }
            case 400021030: {
                applier.b3 = true;
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
        switch (applier.effect.getSourceId()) {
            case 2221007: {
                applier.prop = 100;
                return 1;
            }
            case 2211007: {
                applier.prop = applier.effect.getSubProp();
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MonsterEffectHolder meh;
        MapleStatEffect effect = applyfrom.getSkillEffect(2200000);
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
        if (applier.effect == null) {
            return -1;
        }
        if (applier.effect.getSourceId() != 2211011 && applier.effect.getSourceId() != 2211015 && applier.effect.getSkill().getElement() == Element.雷 && (meh = applyto.getEffectHolder(MonsterStatus.Speed)) != null && meh.z > 0) {
            int eVal = (int)Math.ceil((double)meh.value / (double)meh.z);
            --meh.z;
            meh.value = Math.min(meh.value - eVal, 0);
            applyfrom.getMap().broadcastMessage(MobPacket.mobStatSet(applyto, Collections.singletonMap(MonsterStatus.Speed, meh.sourceID)), applyfrom.getPosition());
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect skillEffect3;
        if (applier.effect != null && applier.effect.getSourceId() == 400021002 && (skillEffect3 = player.getSkillEffect(400020002)) != null) {
            Rectangle rect = applier.effect.calculateBoundingBox(player.getPosition());
            LinkedList<Rectangle> allArea = new LinkedList<Rectangle>();
            for (MapleFoothold fh : player.getMap().getFootholds().getAllRelevants()) {
                if (fh.isWall()) continue;
                Point p = new Point((fh.getX1() + fh.getX2()) / 2, (fh.getY1() + fh.getY2()) / 2);
                p.y += 30;
                Rectangle area = skillEffect3.calculateBoundingBox(p);
                if (!rect.getBounds().contains(area)) continue;
                boolean found = false;
                for (Rectangle rArea : allArea) {
                    if (!rArea.getBounds().intersects(area) && !rArea.getBounds().equals(area)) continue;
                    found = true;
                    break;
                }
                if (found) continue;
                allArea.add(area);
                skillEffect3.applyAffectedArea(player, p);
            }
        }
        return 1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        if (!applier.overwrite) {
            switch (applier.effect.getSourceId()) {
                case 2211015: {
                    MapleStatEffect effect = player.getSkillEffect(2211011);
                    if (effect == null) break;
                    effect.applyTo(player, true);
                    break;
                }
                case 2221056: {
                    MapleStatEffect effect = player.getSkillEffect(2221054);
                    if (effect == null) break;
                    effect.applyTo(player, true);
                    break;
                }
            }
        }
        return -1;
    }

    public static void handleIceReiki(MapleCharacter chr) {
        SecondaryStatValueHolder mbsvh;
        if (JobConstants.is冰雷(chr.getJob()) && (mbsvh = chr.getBuffStatValueHolder(SecondaryStat.IceAura)) != null) {
            if (mbsvh.effect != null && chr.getStat().getMp() >= mbsvh.effect.getMpCon()) {
                Party party;
                chr.addMP(-mbsvh.effect.getMpCon());
                int mobCount = 15;
                int CurrCount = 0;
                int duration = mbsvh.effect.getMobDebuffDuration(chr);
                Rectangle bounds = mbsvh.effect.calculateBoundingBox(chr.getPosition(), chr.isFacingLeft());
                List<MapleMapObject> affected = chr.getMap().getMapObjectsInRect(bounds, Collections.singletonList(MapleMapObjectType.MONSTER));
                for (MapleMapObject mo : affected) {
                    MapleMonster monster = (MapleMonster)mo;
                    if (mbsvh.effect.getMonsterStatus().size() <= 0 || monster == null) continue;
                    if (++CurrCount >= mobCount) break;
                    mbsvh.effect.applyMonsterEffect(chr, monster, duration);
                }
                if ((party = chr.getParty()) != null) {
                    List<MapleMapObject> affectedC = chr.getMap().getMapObjectsInRect(bounds, Collections.singletonList(MapleMapObjectType.PLAYER));
                    for (MapleMapObject obj : affectedC) {
                        MapleCharacter tchr = (MapleCharacter)obj;
                        if (tchr == null || tchr == chr || party.getPartyMemberByID(tchr.getId()) == null || tchr.getBuffStatValueHolder(2221054) != null) continue;
                        mbsvh.effect.applyBuffEffect(chr, tchr, mbsvh.effect.getBuffDuration(chr), false, false, true, null);
                    }
                }
            } else {
                chr.dispelEffect(SecondaryStat.IceAura);
            }
        } else {
            MapleCharacter fchr;
            mbsvh = chr.getBuffStatValueHolder(2221054);
            if (!(mbsvh == null || (fchr = chr.getMap().getPlayerObject(mbsvh.fromChrID)) != null && fchr.getParty() == chr.getParty() && fchr.getBuffStatValueHolder(SecondaryStat.IceAura) != null && mbsvh.effect.calculateBoundingBox(fchr.getPosition()).contains(fchr.getPosition()))) {
                chr.dispelEffect(2221054);
            }
        }
    }
}

