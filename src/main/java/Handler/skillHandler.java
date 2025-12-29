/*
 * Decompiled with CFR 0.152.
 */
package Handler;

import Client.MapleClient;
import Handler.Handler;
import Opcode.header.InHeader;
import Opcode.header.OutHeader;
import connection.InPacket;
import connection.OutPacket;

public class skillHandler {
    @Handler(op=InHeader.CP_SPIRT_WEAPON)
    public static void skillAuto(MapleClient c, InPacket inPacket) {
        OutPacket outPacket = new OutPacket(OutHeader.LP_SPIRT_WEAPON);
        int skillID = inPacket.decodeInt();
        outPacket.encodeInt(0);
        outPacket.encodeByte(0);
        c.write(outPacket);
    }

    @Handler(op=InHeader.CP_DemonUseDargonAttack)
    public static void DemonSkillDargonStage(MapleClient c, InPacket inPacket) {
        OutPacket outPacket = new OutPacket(OutHeader.LP_TemporaryStatSet);
        int skillID = inPacket.decodeInt();
        switch (skillID) {
            case 31001006: {
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(16);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(1);
                outPacket.encodeInt(31000004);
                outPacket.encodeInt(2);
                outPacket.encodeInt(351278887);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeByte(0);
                outPacket.encodeByte(1);
                outPacket.encodeByte(1);
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
                outPacket.encodeByte(1);
                c.write(outPacket);
                break;
            }
            case 31001007: {
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(16);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(1);
                outPacket.encodeInt(31000004);
                outPacket.encodeInt(3);
                outPacket.encodeInt(351782676);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeByte(0);
                outPacket.encodeByte(1);
                outPacket.encodeByte(1);
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
                outPacket.encodeByte(1);
                c.write(outPacket);
                break;
            }
            case 31001008: {
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(16);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(1);
                outPacket.encodeInt(31000004);
                outPacket.encodeInt(4);
                outPacket.encodeInt(351853895);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeByte(0);
                outPacket.encodeByte(1);
                outPacket.encodeByte(1);
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
                outPacket.encodeByte(1);
                c.write(outPacket);
                break;
            }
            case 31000004: {
                c.outPacket(OutHeader.LP_TemporaryStatReset.getValue(), "01 01 01 00 00 00 00 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
            }
        }
    }
}

