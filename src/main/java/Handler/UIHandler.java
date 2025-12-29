/*
 * Decompiled with CFR 0.152.
 */
package Handler;

import Client.MapleClient;
import Config.constants.enums.ScriptType;
import Handler.Handler;
import Opcode.header.InHeader;
import Opcode.header.OutHeader;
import connection.InPacket;
import connection.OutPacket;

public class UIHandler {
    @Handler(op=InHeader.CP_CHECK_PLAYER_STATUS)
    public static void getCharStatUI(MapleClient c, InPacket inPacket) {
        OutPacket packet = new OutPacket(OutHeader.LP_CharacterInfo);
        packet.encodeArr(new byte[9]);
        packet.encodeBoolean(true);
        c.write(packet);
    }

    @Handler(op=InHeader.RECV_STACK_COOKIE_2)
    public static void loadscriptItem(MapleClient c, InPacket inPacket) {
        int str = inPacket.decodeInt();
        int useCount = inPacket.decodeInt();
        String consume = "consume_" + str;
        c.getPlayer().getScriptManager().startScript(0, consume, ScriptType.Item);
        c.getPlayer().removeItem(str, useCount);
        c.getPlayer().setKeyValue("use", "" + useCount);
    }

    @Handler(op=InHeader.CP_CHAR_USE_WARP_ITEM)
    public static void useWarpItem(MapleClient c, InPacket inPacket) {
        OutPacket packet = new OutPacket(OutHeader.LP_CHAR_USE_WARP_ITEM);
        int warpCrc = inPacket.decodeInt();
        int warpType = inPacket.decodeInt();
        int unk = inPacket.decodeInt();
        int mapid = inPacket.decodeInt();
        packet.encodeInt(warpCrc);
        packet.encodeInt(warpType);
        packet.encodeInt(0);
        c.write(packet);
    }

    @Handler(op=InHeader.USER_USE_PARTY_KEYBOARD)
    public static void CharUsePkeyBoard(MapleClient c, InPacket inPacket) {
        OutPacket packet = new OutPacket(OutHeader.LP_PartyCandidateResult);
        packet.encodeByte((byte)0);
        c.write(packet);
    }

    @Handler(op=InHeader.USER_SHOW_TITLE)
    public static void ShowTitle(MapleClient c, InPacket inPacket) {
        c.getPlayer().getScriptManager().startNpcScript(9900000, 0, "BossUIEventNotice");
    }

    @Handler(op=InHeader.CTX_PLAYER_ENTER_EVENT)
    public static void chechEventStart(MapleClient c, InPacket inPacket) {
        short BossType = inPacket.decodeShort();
        inPacket.decodeInt();
        short isNormal = inPacket.decodeShort();
        if (isNormal == 1) {
            c.getPlayer().getScriptManager().startNpcScript(2184000, 0, "Boss_EventUI/" + BossType + "_BossEvent_NORMAL");
        } else {
            c.getPlayer().getScriptManager().startNpcScript(9900000, 0, "Boss_EventUI/" + BossType + "_BossEvent_HARD");
        }
    }
}

