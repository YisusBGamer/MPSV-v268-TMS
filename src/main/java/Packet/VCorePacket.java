/*
 * Decompiled with CFR 0.152.
 */
package Packet;

import Client.MapleCharacter;
import Client.VCoreSkillEntry;
import Client.VMatrixSlot;
import Opcode.header.OutHeader;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;

public final class VCorePacket {
    public static byte[] updateVCoreList(MapleCharacter player, boolean b, int n, int n2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.VCORE_LIST_UPDATE.getValue());
        VCorePacket.writeVCoreSkillData(mplew, player);
        mplew.writeInt(b ? 1 : 0);
        if (b) {
            mplew.writeInt(n);
            if (n != 2 && n != 4) {
                mplew.writeInt(n2);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] showVCoreSkillExpResult(int n1, int expEnforce, int currLevel, int newLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.VCORE_SKILLEXP_RESULT.getValue());
        mplew.writeInt(n1);
        mplew.writeInt(expEnforce);
        mplew.writeInt(currLevel);
        mplew.writeInt(newLevel);
        return mplew.getPacket();
    }

    public static byte[] addVCorePieceResult(int piece) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.VCORE_ADD_PIECE_RESULT.getValue());
        mplew.writeInt(piece);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] addVCoreSkillResult(int vcoreid, int level, int skill1, int skill2, int skill3, int nCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.VCORE_ADD_SKILL_RESULT.getValue());
        mplew.writeInt(vcoreid);
        mplew.writeInt(level);
        mplew.writeInt(skill1);
        mplew.writeInt(skill2);
        mplew.writeInt(skill3);
        mplew.writeInt(nCount);
        return mplew.getPacket();
    }

    public static void writeVCoreSkillData(MaplePacketLittleEndianWriter mplew, MapleCharacter player) {
        Map<Integer, VCoreSkillEntry> vcoreSkills = player.getVCoreSkill();
        mplew.writeInt(vcoreSkills.size());
        for (Map.Entry<Integer, VCoreSkillEntry> entry : vcoreSkills.entrySet()) {
            mplew.writeInt(entry.getKey());
            mplew.writeInt(1814680564);
            mplew.writeInt(entry.getValue().getVcoreid());
            mplew.writeInt(entry.getValue().getLevel());
            mplew.writeInt(entry.getValue().getExp());
            mplew.writeInt(entry.getValue().getSlot());
            mplew.writeInt(entry.getValue().getSkill1());
            mplew.writeInt(entry.getValue().getSkill2());
            mplew.writeInt(entry.getValue().getSkill3());
            mplew.writeInt(entry.getValue().getIndex());
            mplew.writeLong(150842304000000000L);
            mplew.write(0);
        }
        mplew.writeInt(player.getVMatrixSlot().size());
        for (Map.Entry<Integer, VMatrixSlot> entry : player.getVMatrixSlot().entrySet()) {
            mplew.writeInt(((VMatrixSlot)entry.getValue()).getIndex());
            mplew.writeInt(entry.getKey());
            mplew.writeInt(((VMatrixSlot)entry.getValue()).getExtend());
            mplew.write(((VMatrixSlot)entry.getValue()).getUnlock());
        }
    }

    public static byte[] showVCoreWindowVerifyResult(boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.VCORE_LIST_CHECK_2SPW.getValue());
        mplew.writeInt(3);
        mplew.write(success);
        return mplew.getPacket();
    }

    public static byte[] showVCoreItemUseEffect(int vcoreid, int level, int skill1, int skill2, int skill3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.VCORE_ITEM_USE_EFFECT.getValue());
        mplew.writeInt(vcoreid);
        mplew.writeInt(level);
        mplew.writeInt(skill1);
        mplew.writeInt(skill2);
        mplew.writeInt(skill3);
        mplew.writeInt(vcoreid == 40000000 ? 2 : (vcoreid == 0x989698 ? 1 : 0));
        return mplew.getPacket();
    }

    public static byte[] ArcAutLevelUpEffect(int ArcSlot) {
        MaplePacketLittleEndianWriter EffectPacket2 = new MaplePacketLittleEndianWriter();
        EffectPacket2.writeShort(OutHeader.ARC_AUT_LEVEL_UP_EFFECT.getValue());
        EffectPacket2.writeInt(1);
        EffectPacket2.writeInt(0);
        EffectPacket2.writeInt(-ArcSlot);
        return EffectPacket2.getPacket();
    }
}

