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

public class Auth {
    @Handler(op=InHeader.CP_SECURITY_REQUEST)
    public static void VerifyTimePulse(MapleClient c, InPacket inPacket) {
        int i;
        OutPacket say = new OutPacket(OutHeader.LP_SECURITY_REQUEST.getValue());
        byte[] bytes = new byte[3];
        for (i = 0; i < 3; ++i) {
            bytes[i] = (byte)(inPacket.decodeByte() + i + 1);
            if (bytes[i] > 127) {
                bytes[i] = (byte)(bytes[i] - 255 - 1);
            }
            say.encodeByte(bytes[i]);
        }
        say.encodeArr(new byte[5]);
        for (i = 0; i < 3; ++i) {
            bytes[i] = (byte)(bytes[i] + 1);
            if (bytes[i] > 127) {
                bytes[i] = (byte)(bytes[i] - 255 - 1);
            }
            say.encodeByte(bytes[i]);
        }
        say.encodeArr(new byte[5]);
        c.write(say);
    }

    @Handler(op=InHeader.CP_REQUEST_STATUS_CHECK)
    public static void VerifyTimePulseNext(MapleClient c, InPacket inPacket) {
        OutPacket say = new OutPacket(OutHeader.LP_REQUEST_STATUS_CHECK.getValue());
        int NextType = inPacket.decodeInt();
        inPacket.decodeInt();
        boolean action = inPacket.decodeBoolean();
        say.encodeInt(NextType);
        say.encodeBoolean(action);
        c.write(say);
    }

    @Handler(op=InHeader.CP_WARP_TO_MAP_REF)
    public static void ref(MapleClient c, InPacket inPacket) {
        OutPacket say = new OutPacket(OutHeader.LP_UserSoulEffect.getValue());
        int NextType = inPacket.decodeInt();
        say.encodeInt(NextType);
        say.encodeInt(0);
        c.write(say);
    }

    @Handler(op=InHeader.CP_Client_Crash_rep)
    public static void rep(MapleClient c, InPacket inPacket) {
        OutPacket say = new OutPacket(OutHeader.LP_UseAttack);
        say.encodeInt(0);
        c.write(say);
    }

    @Handler(op=InHeader.CP_ChangeMapCheckingPacket)
    public static void changeMapUnk(MapleClient c, InPacket inPacket) {
        OutPacket say = new OutPacket(OutHeader.LP_ChangeMapCheckingPacket);
        say.encodeInt(c.getPlayer().getId());
        say.encodeByte(1);
        c.write(say);
    }
}

