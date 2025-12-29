/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.unknown.SummonedMagicAltarInfo
 *  Net.server.unknown.SummonedMagicAltarInfo$SubInfo
 */
package Packet;

import Client.MapleCharacter;
import Net.server.maps.MapleDragon;
import Net.server.maps.MapleSkillPet;
import Net.server.maps.MapleSummon;
import Net.server.movement.LifeMovementFragment;
import Net.server.unknown.SummonedMagicAltarInfo;
import Opcode.header.OutHeader;
import Packet.PacketHelper;
import Server.channel.handler.AttackInfo;
import Server.channel.handler.AttackMobInfo;
import java.awt.Point;
import java.util.List;
import tools.data.MaplePacketLittleEndianWriter;

public class SummonPacket {
    public static byte[] spawnSummon(MapleSummon summon) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedEnterField.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(summon.getSkillId());
        mplew.writeInt(summon.getOwnerLevel());
        mplew.writeInt(summon.getSkillLevel());
        mplew.writePos(summon.getPosition());
        mplew.write(summon.getStance());
        mplew.writeShort(summon.getCurrentFH());
        mplew.write(summon.getMovementType().getValue());
        mplew.write(summon.getAttackType());
        mplew.write(summon.getAnimated());
        if (summon.is大漩渦()) {
            mplew.writeInt(summon.getMobid());
        } else {
            mplew.writeInt(0);
        }
        mplew.write(0);
        mplew.write(1);
        mplew.writeInt(summon.getShadow());
        mplew.writeInt(0);
        mplew.writeBool(summon.showCharLook());
        if (summon.showCharLook()) {
            summon.getOwner().getAvatarLook().encode(mplew, true);
        }
        switch (summon.getSkillId()) {
            case 35111002: {
                boolean v8 = false;
                mplew.write(v8);
                if (!v8) break;
                int v33 = 0;
                do {
                    mplew.writeShort(0);
                    mplew.writeShort(0);
                } while (++v33 < 3);
                break;
            }
            case 14111024: 
            case 14121054: 
            case 14121055: 
            case 14121056: 
            case 131001017: 
            case 131002017: 
            case 131003017: 
            case 400011005: 
            case 400031007: 
            case 400031008: 
            case 400031009: 
            case 400041028: {
                int x = 0;
                int y = 0;
                switch (summon.getSkillId()) {
                    case 400011005: {
                        x = 60;
                        y = 30;
                        break;
                    }
                    case 400031007: {
                        x = 300;
                        y = 41000;
                        break;
                    }
                    case 400031008: {
                        x = 600;
                        y = 41000;
                        break;
                    }
                    case 400031009: {
                        x = 900;
                        y = 41000;
                        break;
                    }
                    case 14111024: 
                    case 131001017: {
                        x = 400;
                        y = 30;
                        break;
                    }
                    case 14121055: 
                    case 131002017: {
                        x = 800;
                        y = 60;
                        break;
                    }
                    case 14121056: 
                    case 131003017: {
                        x = 1200;
                        y = 90;
                    }
                }
                mplew.writeInt(x);
                mplew.writeInt(y);
                break;
            }
            case 42111003: {
                mplew.writeShort(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
                break;
            }
            case 151111001: {
                mplew.writeInt(-1);
            }
        }
        mplew.write(0);
        int duration = summon.getDuration();
        mplew.writeInt(duration == 2100000000 ? 0 : duration);
        mplew.write(summon.getSkillId() == 42111101 || summon.getSkillId() == 42111102 || summon.getSkillId() == 42111003 ? 0 : 1);
        mplew.writeInt(summon.isFacingLeft() ? 1 : 0);
        mplew.writeInt(summon.getMoveRange());
        mplew.writeInt(0);
        if (summon.getSkillId() >= 33001007 && summon.getSkillId() - 33001007 <= 8) {
            mplew.writeBool(duration < 2100000000);
            mplew.write(duration < 2100000000 ? 500 : 0);
        }
        boolean b = summon.getSkillId() == 152101000 || summon.getSkillId() == 164121008;
        mplew.writeBool(b);
        if (b) {
            mplew.writeInt(summon.getAcState1());
            mplew.writeInt(summon.getAcState2());
        }
        int[] array = switch (summon.getSkillId()) {
            case 400051038, 400051052, 400051053 -> new int[]{400051038, 400051052, 400051053};
            default -> new int[]{};
        };
        mplew.writeInt(array.length);
        for (int i : array) {
            mplew.writeInt(i);
        }
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.write(false);
        switch (summon.getSkillId()) {
            case 152101000: {
                mplew.writeInt(Math.min(summon.getAcState2(), 3));
                for (int j = 0; j < Math.min(summon.getAcState2(), 3); ++j) {
                    mplew.writeInt(j + 1);
                    mplew.writeInt(summon.getState(j));
                }
                break;
            }
            case 42111101: 
            case 42111102: {
                mplew.write(0);
                break;
            }
            case 42111103: {
                mplew.writeInt(-1);
            }
        }
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] removeSummon(MapleSummon summon, boolean animated) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedLeaveField.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.write(animated ? (byte)5 : (byte)summon.getRemoveStatus());
        return mplew.getPacket();
    }

    public static byte[] moveSummon(int chrId, int oid, int gatherDuration, int nVal1, Point mPos, Point oPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedMove.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(oid);
        PacketHelper.serializeMovementList(mplew, gatherDuration, nVal1, mPos, oPos, moves, null);
        return mplew.getPacket();
    }

    public static byte[] summonAttack(MapleCharacter chr, int summonOid, AttackInfo ai, boolean darkFlare) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedAttack.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(summonOid);
        mplew.writeInt(chr.getLevel());
        mplew.write(ai.display);
        mplew.write(ai.numAttackedAndDamage);
        for (AttackMobInfo mai : ai.mobAttackInfo) {
            if (mai.damages == null) continue;
            mplew.writeInt(mai.mobId);
            if (mai.mobId <= 0) continue;
            mplew.write(7);
            for (long damage : mai.damages) {
                mplew.writeLong(damage);
            }
        }
        mplew.writeBool(darkFlare);
        mplew.write(0);
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeShort(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static byte[] summonSkill(int chrId, int summonSkillId, int newStance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedSkill.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] SummonedMagicAltar(int cid, int oid, SummonedMagicAltarInfo smai) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_SummonedSkill);
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.write(smai.action);
        mplew.writeInt(0);
        if (smai.skillId == 400021092) {
            mplew.writeInt(smai.a7);
        }
        mplew.writeInt(smai.skillId);
        mplew.writeInt(smai.skillLv);
        mplew.writeInt(smai.a1);
        mplew.writeInt(smai.a2);
        mplew.writeInt(smai.a3);
        mplew.writePos(smai.position);
        mplew.writeRect(smai.area);
        mplew.writeInt(smai.a4);
        mplew.write(smai.a5);
        mplew.writeInt(smai.a6);
        mplew.writeInt(smai.subSummon.size());
        for (SummonedMagicAltarInfo.SubInfo sub : smai.subSummon) {
            mplew.writeInt(smai.skillId);
            mplew.writeInt(smai.skillLv);
            mplew.writeInt(sub.a1);
            mplew.writeShort(sub.a8);
            mplew.writePos(sub.position);
            mplew.writeInt(sub.a2);
            mplew.write(sub.a3);
            mplew.writeBool(sub.b1);
            if (sub.b1) {
                mplew.writeInt(sub.a4);
                mplew.writeInt(sub.a5);
            }
            mplew.writeBool(sub.b2);
            if (!sub.b2) continue;
            mplew.writeInt(sub.a6);
            mplew.writeInt(sub.a7);
        }
        return mplew.getPacket();
    }

    public static byte[] damageSummon(int chrId, int sumoid, int type, int damage, int monsterIdFrom, boolean b) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedAttackDone.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(sumoid);
        mplew.write(type);
        mplew.writeInt(damage);
        mplew.writeInt(monsterIdFrom);
        mplew.writeBool(b);
        return mplew.getPacket();
    }

    public static byte[] summonedSetAbleResist(int chrId, int sumoid, byte b) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedSetAbleResist.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(sumoid);
        mplew.write(b);
        return mplew.getPacket();
    }

    public static byte[] SummonedAssistAttackRequest(int cid, int summonoid, int n) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedAssistAttackRequest.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonoid);
        mplew.write(n);
        return mplew.getPacket();
    }

    public static byte[] spawnDragon(MapleDragon dragon) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DragonEnterField.getValue());
        mplew.writeInt(dragon.getOwner());
        mplew.writeInt(dragon.getPosition().x);
        mplew.writeInt(dragon.getPosition().y);
        mplew.write(dragon.getStance());
        mplew.writeShort(0);
        mplew.writeShort(dragon.getJobId());
        return mplew.getPacket();
    }

    public static byte[] removeDragon(int chrId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DragonLeaveField.getValue());
        mplew.writeInt(chrId);
        return mplew.getPacket();
    }

    public static byte[] moveDragon(MapleDragon dragon, int gatherDuration, int nVal1, Point mPos, Point oPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DragonMove.getValue());
        mplew.writeInt(dragon.getOwner());
        PacketHelper.serializeMovementList(mplew, gatherDuration, nVal1, mPos, oPos, moves, null);
        return mplew.getPacket();
    }

    public static byte[] spawnSkillPet(MapleSkillPet lw) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SkillPetTransferField.getValue());
        mplew.writeInt(lw.getOwner());
        mplew.writeInt(lw.getObjectId());
        mplew.writeInt(lw.getSkillId());
        mplew.write(lw.getState());
        mplew.writePos(lw.getPosition());
        mplew.write(lw.getStance());
        mplew.writeShort(lw.getCurrentFH());
        return mplew.getPacket();
    }

    public static byte[] moveSkillPet(int cid, int oid, int gatherDuration, int nVal1, Point mPos, Point oPos, List<LifeMovementFragment> move) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SkillPetMove.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        PacketHelper.serializeMovementList(mplew, gatherDuration, nVal1, mPos, oPos, move, null);
        return mplew.getPacket();
    }

    public static byte[] SkillPetAction(int cid, int oid, byte val1, byte val2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SkillPetAction.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.write(val1);
        mplew.write(val2);
        mplew.writeMapleAsciiString("");
        return mplew.getPacket();
    }

    public static byte[] FoxManEnterField(MapleSkillPet lw) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FoxManEnterField.getValue());
        mplew.writeInt(lw.getOwner());
        mplew.writeShort(0);
        mplew.writePos(lw.getPosition());
        mplew.write(lw.getStance());
        mplew.writeShort(lw.getCurrentFH());
        mplew.writeInt(lw.getSpecialState());
        mplew.writeInt(lw.getWeapon());
        return mplew.getPacket();
    }

    public static byte[] changeFoxManStace(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FoxManExclResult.getValue());
        mplew.writeInt(cid);
        return mplew.getPacket();
    }

    public static byte[] summonGost(int chrId, int summonOid1, int summonOid2, int skillLevel, Point pos_x, short pos_y) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SUMMON_GHOST.getValue());
        mplew.writeInt(chrId);
        mplew.writeShort(skillLevel);
        mplew.writeInt(summonOid1);
        mplew.writeInt(summonOid2);
        mplew.writePos(pos_x);
        mplew.writeShort(pos_y - 110);
        mplew.writeShort(pos_y - 50);
        return mplew.getPacket();
    }

    public static byte[] SummonedSkillState(MapleSummon summon, int i) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedSkillState.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(i);
        if (i == 2) {
            mplew.writeInt(Math.min(summon.getAcState2(), 3));
            for (i = 0; i < Math.min(summon.getAcState2(), 3); ++i) {
                mplew.writeInt(i + 1);
                mplew.writeInt(summon.getState(i));
            }
        }
        return mplew.getPacket();
    }

    public static byte[] SummonedForceMove(MapleSummon summon, int skillId, int skillLevel, Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedForceMove.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(skillId);
        mplew.writeInt(skillLevel);
        mplew.writeInt(pos.x);
        mplew.writeInt(pos.y);
        return mplew.getPacket();
    }

    public static byte[] SummonedForceReturn(int ownerId, int objectId, Point position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_SummonedForceReturn);
        mplew.writeInt(ownerId);
        mplew.writeInt(objectId);
        mplew.writePosInt(position);
        return mplew.getPacket();
    }

    public static byte[] SummonedStateChange(MapleSummon summon, int n, int s1, int s2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedStateChange.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(n);
        switch (n) {
            case 1: 
            case 2: {
                mplew.writeInt(s1);
                mplew.writeInt(s2);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] SummonedSpecialEffect(MapleSummon summon, int i) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedSpecialEffect.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(i);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] SummonedCrystalAttack(MapleSummon summon, int skillID) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SummonedCrystalAttack.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(skillID);
        return mplew.getPacket();
    }

    public static byte[] SummonMagicCircleAttack(MapleSummon summon, int n, Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_SummonedMagicCircleAttack);
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(n);
        mplew.writeBool(true);
        mplew.writePosInt(pos);
        return mplew.getPacket();
    }

    public static byte[] FoxManShowChangeEffect(MapleSkillPet pet) {
        MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
        hh.writeOpcode(OutHeader.LP_FoxManShowChangeEffect);
        hh.writeInt(pet.getOwner());
        return hh.getPacket();
    }

    public static byte[] FoxManLeaveField(MapleSkillPet pet) {
        MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
        hh.writeOpcode(OutHeader.LP_FoxManLeaveField);
        hh.writeInt(pet.getOwner());
        return hh.getPacket();
    }

    public static byte[] SkillPetState(MapleSkillPet pet) {
        MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
        hh.writeOpcode(OutHeader.LP_SkillPetState);
        hh.writeInt(pet.getOwner());
        hh.writeInt(pet.getObjectId());
        hh.write(pet.getState());
        return hh.getPacket();
    }
}

