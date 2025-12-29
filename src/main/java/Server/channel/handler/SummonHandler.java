/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Net.server.unknown.SummonedMagicAltarInfo
 *  Net.server.unknown.SummonedMagicAltarInfo$SubInfo
 *  Packet.AdelePacket
 *  SwordieX.client.party.Party
 *  SwordieX.client.party.PartyMember
 */
package Server.channel.handler;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.skills.SummonSkillEntry;
import Config.constants.SkillConstants;
import Config.constants.enums.UserChatMessageType;
import Net.server.MapleItemInformationProvider;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleDragon;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleSkillPet;
import Net.server.maps.MapleSummon;
import Net.server.maps.SummonMovementType;
import Net.server.movement.LifeMovementFragment;
import Net.server.unknown.SummonedMagicAltarInfo;
import Opcode.Opcode.EffectOpcode;
import Packet.AdelePacket;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import Server.channel.handler.AttackInfo;
import Server.channel.handler.AttackMobInfo;
import Server.channel.handler.DamageParse;
import Server.channel.handler.MovementParse;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.MaplePacketReader;

public class SummonHandler {
    private static final Logger log = LoggerFactory.getLogger(MovementParse.class);

    public static void MoveDragon(MaplePacketReader slea, MapleCharacter chr) {
        int gatherDuration = slea.readInt();
        int nVal1 = slea.readInt();
        Point mPos = slea.readPos();
        Point oPos = slea.readPos();
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 5);
        if (chr != null && chr.getDragon() != null && res.size() > 0) {
            MovementParse.updatePosition(res, chr.getDragon(), 0);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, SummonPacket.moveDragon(chr.getDragon(), gatherDuration, nVal1, mPos, oPos, res), chr.getPosition());
            }
        }
    }

    public static void DragonFly(MaplePacketReader slea, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.getDragon() == null) {
            return;
        }
        int type = slea.readInt();
        int mountId = type == 0 ? slea.readInt() : 0;
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.showDragonFly(chr.getId(), type, mountId), chr.getPosition());
    }

    public static void MoveSummon(MaplePacketReader slea, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int objid = slea.readInt();
        MapleMapObject obj = chr.getMap().getMapObject(objid, MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        if (obj instanceof MapleDragon) {
            SummonHandler.MoveDragon(slea, chr);
            return;
        }
        MapleSummon sum = (MapleSummon)obj;
        if (sum.getOwnerId() != chr.getId() || sum.getSkillLevel() <= 0 || sum.getMovementType() == SummonMovementType.STOP) {
            return;
        }
        int gatherDuration = slea.readInt();
        int nVal1 = slea.readInt();
        Point mPos = slea.readPos();
        Point oPos = slea.readPos();
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 4);
        MovementParse.updatePosition(res, sum, 0);
        if (res.size() > 0) {
            chr.getMap().broadcastMessage(chr, SummonPacket.moveSummon(chr.getId(), sum.getObjectId(), gatherDuration, nVal1, mPos, oPos, res), sum.getPosition());
        }
    }

    public static void DamageSummon(MaplePacketReader slea, MapleCharacter chr) {
        MapleStatEffect effect;
        if (chr == null || !chr.isAlive() || chr.getMap() == null) {
            return;
        }
        slea.readInt();
        int sumoid = slea.readInt();
        MapleSummon summon = chr.getMap().getSummonByOid(sumoid);
        if (summon == null || summon.getOwnerId() != chr.getId() || summon.getSkillLevel() <= 0 || !chr.isAlive()) {
            return;
        }
        byte type = slea.readByte();
        int damage = slea.readInt();
        int monsterIdFrom = slea.readInt();
        boolean b = slea.readByte() != 0;
        MapleMonster monster = null;
        if (slea.available() >= 4L && (monster = chr.getMap().getMonsterByOid(slea.readInt())) == null) {
            return;
        }
        if (damage > 0) {
            if (monster != null && summon.getSkillId() == 3221014 && (effect = summon.getEffect()) != null) {
                AttackInfo ai = new AttackInfo();
                ai.skillId = summon.getSkillId();
                ai.display = 134;
                ai.numAttackedAndDamage = (byte)17;
                ai.mobCount = 1;
                ai.hits = 1;
                long theDmg = (long)effect.getY() * (long)damage / 100L;
                AttackMobInfo mai = new AttackMobInfo();
                mai.mobId = monster.getObjectId();
                mai.damages = new long[]{theDmg};
                ai.mobAttackInfo.add(mai);
                chr.getMap().broadcastMessage(chr, SummonPacket.summonAttack(chr, sumoid, ai, true), true);
                monster.damage(chr, summon.getSkillId(), theDmg, false);
            }
            summon.addSummonHp(-damage);
            if (summon.getSummonHp() <= 0) {
                chr.dispelEffect(summon.getSkillId());
            }
        }
        if (summon.getSkillId() == 4341006 && (effect = chr.getSkillEffect(4330009)) != null && chr.getCheatTracker().canNextShadowDodge()) {
            effect.unprimaryPassiveApplyTo(chr);
        }
        chr.getMap().broadcastMessage(chr, SummonPacket.damageSummon(chr.getId(), sumoid, type, damage, monsterIdFrom, b), false);
    }

    public static void UserSummonAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        SecondaryStatValueHolder holder;
        MapleStatEffect effect;
        int objid;
        MapleMap map = chr.getMap();
        MapleMapObject obj = map.getMapObject(objid = slea.readInt(), MapleMapObjectType.SUMMON);
        if (!(obj instanceof MapleSummon)) {
            chr.dropMessage(5, "召喚獸的持續時間到而消失..");
            return;
        }
        MapleSummon summon = (MapleSummon)obj;
        AttackInfo ai = DamageParse.parseSummonAttack(slea, chr);
        if (ai == null) {
            c.sendEnableActions();
            return;
        }
        if (summon.getOwnerId() != chr.getId() || summon.getSkillLevel() <= 0 || !chr.isAlive()) {
            chr.dropMessage(5, "出現錯誤.");
            return;
        }
        int skillId = summon.getSkillId();
        switch (summon.getSkillId()) {
            case 1301013: {
                SecondaryStatValueHolder mbsvh = chr.getBuffStatValueHolder(SecondaryStat.Beholder);
                if (mbsvh == null) break;
                skillId = mbsvh.sourceID;
                if (ai.skillId <= 0) break;
                skillId = ai.skillId;
                break;
            }
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                int jaguar = chr.getSpecialStat().getJaguarSkillID();
                if (jaguar <= 0) break;
                break;
            }
            case 400011065: {
                chr.removeSummon(summon, 5);
                break;
            }
            case 400021033: {
                ai.skillId = 400021033;
            }
        }
        int linkSkillId = SkillConstants.getLinkedAttackSkill(skillId);
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return;
        }
        ai.attackType = AttackInfo.AttackType.SummonedAttack;
        SummonSkillEntry sse = SkillFactory.getSummonData(skillId);
        int attackCount = 0;
        if (sse != null) {
            attackCount = sse.attackCount;
            ai.summonMobCount = sse.mobCount;
        }
        if (ai.skillId <= 0) {
            ai.skillId = linkSkillId;
            effect = chr.getSkillEffect(linkSkillId);
        } else {
            effect = chr.getSkillEffect(ai.skillId);
        }
        if (effect == null && (holder = chr.getBuffStatValueHolder(SecondaryStat.IndieBuffIcon, linkSkillId)) != null) {
            effect = holder.effect;
        }
        if (effect == null) {
            chr.dropMessage(5, "召喚獸攻擊處理出錯。effect==null:" + (effect == null) + " 技能ID：" + skillId);
            return;
        }
        switch (effect.getSourceId()) {
            case 1301014: 
            case 1310018: 
            case 1321024: {
                if (chr.isSkillCooling(1301014)) {
                    return;
                }
                int cooldown = effect.getCooldown(chr);
                if (cooldown <= 0) break;
                chr.registerSkillCooldown(1301014, cooldown, true);
                break;
            }
            case 400011054: 
            case 400021066: {
                if (chr.isSkillCooling(effect.getSourceId()) || effect.getCooldown(chr) <= 0) break;
                chr.registerSkillCooldown(effect, true);
            }
        }
        attackCount = Math.max(attackCount, effect.getAttackCount(chr));
        ai.summonMobCount = Math.max(ai.summonMobCount, effect.getMobCount(chr));
        DamageParse.calcDamage(ai, chr, attackCount, effect);
        chr.getMap().broadcastMessage(chr, SummonPacket.summonAttack(chr, summon.getObjectId(), ai, false), false);
        DamageParse.applyAttack(ai, skill, chr, effect, true);
        chr.getSpecialStat().setJaguarSkillID(0);
        switch (summon.getSkillId()) {
            case 35121011: 
            case 42100010: 
            case 400011029: 
            case 400051023: {
                chr.removeSummon(summon, ai.display);
            }
        }
        if (ai.unInt1 > 0 && linkSkillId == 35111002) {
            chr.removeSummonBySkillID(35111002, 0);
            chr.removeSummonBySkillID(35111002, 0);
            chr.removeSummonBySkillID(35111002, 0);
        }
    }

    public static void RemoveSummon(MaplePacketReader slea, MapleClient c) {
        int objid = slea.readInt();
        MapleMapObject obj = c.getPlayer().getMap().getMapObject(objid, MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof MapleSummon)) {
            return;
        }
        MapleSummon summon = (MapleSummon)obj;
        if (summon.getOwnerId() != c.getPlayer().getId() || summon.getSkillLevel() <= 0) {
            c.getPlayer().dropMessage(5, "移除召喚獸出現錯誤.");
            return;
        }
        if (c.getPlayer().isDebug()) {
            c.getPlayer().dropSpouseMessage(UserChatMessageType.管理員對話, "收到移除召喚獸信息 - 召喚獸技能ID: " + summon.getSkillId() + " 技能名字 " + SkillFactory.getSkillName(summon.getSkillId()));
        }
        if (summon.getSkillId() == 35111002) {
            return;
        }
        c.getPlayer().getMap().broadcastMessage(SummonPacket.removeSummon(summon, false));
        c.getPlayer().getMap().removeMapObject(summon);
        c.getPlayer().removeVisibleMapObjectEx(summon);
        c.getPlayer().removeSummon(summon);
        c.getPlayer().dispelSkill(summon.getSkillId());
        if (summon.is天使召喚獸()) {
            int buffId = summon.getSkillId() % 10000 == 1087 ? 2022747 : (summon.getSkillId() % 10000 == 1179 ? 2022823 : 2022746);
            c.getPlayer().dispelBuff(buffId);
        }
        if (summon.getSkillId() == 400051011) {
            c.getPlayer().send(MaplePacketCreator.userBonusAttackRequest(400051011, 0, Collections.emptyList()));
        }
    }

    public static void SummonedSkill(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        MapleMapObject obj = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (!(obj instanceof MapleSummon)) {
            return;
        }
        MapleSummon sum = (MapleSummon)obj;
        int skillId = slea.readInt();
        chr.updateTick(slea.readInt());
        if (slea.available() > 0L) {
            slea.readByte();
        }
        Skill skill = SkillFactory.getSkill(skillId);
        if (sum.getOwnerId() != chr.getId() || sum.getSkillLevel() <= 0 || !chr.isAlive() || skill == null) {
            return;
        }
        MapleStatEffect effect = skill.getEffect(SkillConstants.is召喚獸戒指(sum.getSkillId()) ? 1 : chr.getSkillLevel(SkillConstants.getLinkedAttackSkill(skillId)));
        if (effect == null) {
            chr.dropMessage(6, "[Summon Skill] Skill effect is null, SkillId:" + skillId);
            return;
        }
        if (chr.isDebug()) {
            chr.dropMessage(6, "[Summon Skill] " + String.valueOf(effect));
        }
        switch (skillId) {
            case 1301013: {
                int hpHeal = Math.min(2000, effect.getHp() * chr.getLevel());
                if (chr.getSkillLevel(1320045) > 0) {
                    hpHeal = chr.getStat().getCurrentMaxHP() * 10 / 100;
                }
                chr.addHPMP(hpHeal, 0, false, true);
                chr.getMap().broadcastMessage(chr, SummonPacket.summonSkill(chr.getId(), sum.getObjectId(), 13), true);
                chr.send(EffectPacket.encodeUserEffectLocal(skillId, EffectOpcode.UserEffect_SkillAffected, chr.getLevel(), effect.getLevel()));
                chr.getMap().broadcastMessage(chr, EffectPacket.onUserEffectRemote(chr, skillId, EffectOpcode.UserEffect_SkillAffected, chr.getLevel(), effect.getLevel()), false);
                break;
            }
            case 1310016: {
                effect.unprimaryPassiveApplyTo(chr);
                chr.getMap().broadcastMessage(chr, SummonPacket.summonSkill(chr.getId(), sum.getObjectId(), 12), true);
                chr.send(EffectPacket.encodeUserEffectLocal(skillId, EffectOpcode.UserEffect_SkillAffected, chr.getLevel(), effect.getLevel()));
                chr.getMap().broadcastMessage(chr, EffectPacket.onUserEffectRemote(chr, skillId, EffectOpcode.UserEffect_SkillAffected, chr.getLevel(), effect.getLevel()), false);
                break;
            }
            case 35121009: {
                if (!chr.canSummon(2000)) break;
                for (int i = 0; i < 3; ++i) {
                    chr.getSkillEffect(35121011).applyTo(chr, sum.getPosition(), true);
                }
                break;
            }
            case 35111008: 
            case 35120002: {
                Party party = chr.getParty();
                Rectangle rect = effect.calculateBoundingBox(sum.getPosition(), sum.isFacingLeft());
                if (party != null) {
                    for (PartyMember member : party.getMembers()) {
                        if (member.getChr() == null || member.getChr().getMap() != chr.getMap() || !rect.contains(member.getChr().getPosition())) continue;
                        member.getChr().addHPMP(effect.getHcHp(), 0);
                    }
                    break;
                }
                if (!rect.contains(chr.getPosition())) break;
                chr.addHPMP(effect.getHcHp(), 0);
                break;
            }
            case 400051022: {
                for (int j = 0; j < 2; ++j) {
                    if (chr.getSummonCountBySkill(400051023) > 10) continue;
                    chr.getSkillEffect(400051023).applyTo(chr, sum.getPosition(), true);
                }
                break;
            }
            case 400041038: {
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(chr, effect, 0)), true);
                break;
            }
            case 152111007: {
                chr.getMap().broadcastMessage(chr, SummonPacket.SummonedSkillState(sum, 1), true);
                effect.applyTo(chr);
                sum.setState(1, 0);
                chr.getMap().broadcastMessage(chr, SummonPacket.SummonedSkillState(sum, 2), true);
                chr.getMap().broadcastMessage(chr, SummonPacket.SummonedStateChange(sum, 3, 0, 0), true);
                break;
            }
            case 152121005: {
                if (chr.getSummonCountBySkill(152121006) >= 5) break;
                chr.getSkillEffect(152121006).applyTo(chr, sum.getPosition(), true);
                break;
            }
            case 400021032: {
                SkillFactory.getSkill(400021052).getEffect(effect.getLevel()).unprimaryPassiveApplyTo(chr);
                break;
            }
            case 5201012: 
            case 5210015: {
                slea.readPosInt();
                slea.readByte();
                int nCount = slea.readInt();
                LinkedList<Integer> mobids = new LinkedList<Integer>();
                for (int i = 0; i < nCount; ++i) {
                    mobids.add(slea.readInt());
                }
                ArrayList<ForceAtomObject> createList = new ArrayList<ForceAtomObject>();
                for (int i = 0; i < 2; ++i) {
                    MapleMonster mob;
                    ForceAtomObject fobj = new ForceAtomObject(chr.getSpecialStat().gainForceCounter(), 34, i, chr.getId(), 0, 5201017);
                    fobj.Idk3 = 1;
                    if (!mobids.isEmpty()) {
                        fobj.Target = (Integer)mobids.get(i % mobids.size());
                    }
                    fobj.CreateDelay = 450;
                    fobj.EnableDelay = 480;
                    fobj.Idk1 = 10;
                    fobj.Expire = 4000;
                    fobj.Position = new Point(0, 1);
                    Point p = null;
                    if (fobj.Target > 0 && (mob = chr.getMap().getMobObject(fobj.Target)) != null) {
                        p = new Point(mob.getPosition());
                    }
                    if (p == null) {
                        p = new Point(chr.getPosition());
                    }
                    fobj.ObjPosition = new Point(p);
                    createList.add(fobj);
                }
                if (!createList.isEmpty()) {
                    chr.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)chr.getId(), createList, (int)0), chr.getPosition());
                }
                chr.send(EffectPacket.showSkillAffected(-1, skillId, effect.getLevel(), 0));
                chr.getMap().broadcastMessage(chr, EffectPacket.showSkillAffected(chr.getId(), skillId, effect.getLevel(), 0), false);
                break;
            }
            default: {
                MapleStatEffect eff;
                if (!SkillConstants.is召喚獸戒指(sum.getSkillId())) break;
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int em = SkillConstants.eM(skillId);
                if (em <= 0 || (eff = ii.getItemEffect(em)) == null) break;
                eff.applyTo(chr, null, true);
            }
        }
        c.announce(EffectPacket.showSkillAffected(0, skillId, 1, 0));
        chr.getMap().broadcastMessage(chr, EffectPacket.showSkillAffected(chr.getId(), skillId, 1, 0), chr.getPosition());
    }

    public static void SkillPetMove(MaplePacketReader slea, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int index = slea.readInt();
        slea.readByte();
        int gatherDuration = slea.readInt();
        int nVal1 = slea.readInt();
        Point mPos = slea.readPos();
        Point oPos = slea.readPos();
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 7);
        if (res != null && !res.isEmpty()) {
            if (slea.available() != 0L) {
                log.error("slea.available() != 0 (花狐移動錯誤) 封包: " + slea.toString(true));
                return;
            }
            MapleSkillPet skillPet = chr.getSkillPet();
            if (skillPet == null || skillPet.getObjectId() != index) {
                return;
            }
            MovementParse.updatePosition(res, skillPet, 0);
            chr.getMap().broadcastMessage(chr, SummonPacket.moveSkillPet(chr.getId(), index, gatherDuration, nVal1, mPos, oPos, res), false);
        }
    }

    public static void FoxManActionSetUseRequest(MaplePacketReader slea, MapleCharacter chr) {
        Point pos = slea.readPos();
        int skillType = slea.readInt();
        byte unk2 = slea.readByte();
        byte unk3 = slea.readByte();
        byte unk4 = slea.readByte();
        MapleSkillPet haku = chr.getHaku();
        if (haku == null || haku.getState() != 2) {
            return;
        }
        Skill bHealing = SkillFactory.getSkill(42120011);
        int bHealingLvl = chr.getTotalSkillLevel(bHealing);
        boolean forth = true;
        if (bHealingLvl <= 0 || bHealing == null) {
            bHealing = SkillFactory.getSkill(42101002);
            bHealingLvl = chr.getTotalSkillLevel(bHealing);
            forth = false;
        }
        if (bHealingLvl <= 0 || bHealing == null) {
            return;
        }
        int skillId = 0;
        switch (skillType) {
            case 1: {
                if (forth) {
                    skillId = 42121020;
                    break;
                }
                skillId = 42101020;
                break;
            }
            case 3: {
                if (forth) {
                    skillId = 42121021;
                    break;
                }
                skillId = 42101021;
                break;
            }
            case 4: {
                if (forth) {
                    skillId = 42121022;
                    break;
                }
                skillId = 42101022;
                break;
            }
            case 5: {
                if (forth) {
                    skillId = 42121023;
                    break;
                }
                skillId = 42101023;
                break;
            }
            default: {
                chr.dropMessage(1, "[Error]請將訊息反饋給管理員:\r\n[" + pos.x + "," + pos.y + "][" + skillType + "][" + unk2 + "][" + unk3 + "][" + unk4 + "]");
                return;
            }
        }
        if (chr.isSkillCooling(skillId)) {
            return;
        }
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return;
        }
        MapleStatEffect effect = skill.getEffect(bHealingLvl);
        if (effect == null) {
            return;
        }
        effect.applyTo(chr);
        if (effect.getCooldown(chr) > 0) {
            chr.registerSkillCooldown(skillId, System.currentTimeMillis(), effect.getCooldown(chr));
        }
        chr.getMap().broadcastMessage(chr, SummonPacket.changeFoxManStace(chr.getId()), true);
        chr.send(EffectPacket.showHakuSkillUse(skillType, -1, bHealingLvl));
        chr.getMap().broadcastMessage(chr, EffectPacket.showHakuSkillUse(skillType, chr.getId(), bHealingLvl), false);
    }

    public static void SummonedMagicAltar(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int oid = slea.readInt();
        MapleMapObject obj = player.getMap().getMapObject(oid, MapleMapObjectType.SUMMON);
        if (!(obj instanceof MapleSummon)) {
            return;
        }
        SummonedMagicAltarInfo smai = new SummonedMagicAltarInfo();
        smai.action = slea.readByte();
        int skillId = ((MapleSummon)obj).getSkillId();
        if (skillId == 400021092) {
            smai.a7 = slea.readInt();
        }
        smai.skillId = slea.readInt();
        smai.skillLv = slea.readInt();
        smai.a1 = slea.readInt();
        smai.a2 = slea.readInt();
        smai.a3 = slea.readInt();
        smai.position = slea.readPos();
        smai.area = slea.readRect();
        smai.a4 = slea.readInt();
        smai.a5 = slea.readByte();
        smai.a6 = slea.readInt();
        ArrayList list = new ArrayList();
        int nCount = slea.readInt();
        for (int i = 0; i < nCount; ++i) {
            SummonedMagicAltarInfo.SubInfo sub = new SummonedMagicAltarInfo.SubInfo();
            slea.readInt();
            slea.readInt();
            sub.a1 = slea.readInt();
            sub.a8 = slea.readShort();
            sub.position = slea.readPos();
            sub.a2 = slea.readInt();
            sub.a3 = slea.readByte();
            if (slea.readByte() > 0) {
                sub.b1 = true;
                sub.a4 = slea.readInt();
                sub.a5 = slea.readInt();
            }
            if (slea.readByte() > 0) {
                sub.b2 = true;
                sub.a6 = slea.readInt();
                sub.a7 = slea.readInt();
            }
            list.add(sub.a1);
            smai.subSummon.add(sub);
        }
        c.announce(MaplePacketCreator.VSkillObjectAction(smai.skillId, smai.skillLv, 1, (Integer)list.getFirst()));
        player.getMap().broadcastMessage(player, SummonPacket.SummonedMagicAltar(player.getId(), oid, smai), false);
    }

    public static void SummonedAction(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int oid = slea.readInt();
        MapleMapObject obj = player.getMap().getMapObject(oid, MapleMapObjectType.SUMMON);
        if (!(obj instanceof MapleSummon)) {
            return;
        }
        MapleSummon summon = (MapleSummon)obj;
        if (summon.getOwnerId() != player.getId() || summon.getSkillLevel() <= 0 || !player.isAlive()) {
            return;
        }
        player.getSpecialStat().setJaguarSkillID(0);
    }

    public static void SummonedSarahAction(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int oid = slea.readInt();
        MapleMapObject obj = player.getMap().getMapObject(oid, MapleMapObjectType.SUMMON);
        if (!(obj instanceof MapleSummon)) {
            return;
        }
        MapleStatEffect effect = null;
        if (player.getSkillEffect(152110002) != null) {
            effect = player.getSkillEffect(152110002);
        } else if (player.getSkillEffect(152100002) != null) {
            effect = player.getSkillEffect(152100002);
        }
        if (effect != null) {
            effect.applyTo(player);
        }
    }

    public static void SummonedJavelinAction(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int oid = slea.readInt();
        MapleMapObject obj = player.getMap().getMapObject(oid, MapleMapObjectType.SUMMON);
        if (!(obj instanceof MapleSummon)) {
            return;
        }
        slea.readInt();
        int skillId = slea.readInt();
        if (player.getSkillEffect(skillId) != null && !player.isSkillCooling(skillId)) {
            player.registerSkillCooldown(player.getSkillEffect(skillId), true);
            c.announce(SummonPacket.SummonedCrystalAttack((MapleSummon)obj, skillId));
        }
    }

    public static void SkillPetAction(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        slea.readInt();
        player.updateTick(slea.readInt());
        byte val1 = slea.readByte();
        byte val2 = slea.readByte();
        if (player.getSkillPet() == null) {
            return;
        }
        player.getMap().broadcastMessage(player, SummonPacket.SkillPetAction(player.getId(), player.getSkillPet().getObjectId(), val1, val2), false);
    }

    public static void SubSummon(MaplePacketReader slea, MapleCharacter player) {
    }
}

