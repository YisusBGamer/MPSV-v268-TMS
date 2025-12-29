/*
 * Decompiled with CFR 0.152.
 */
package Handler.Player;

import Client.MapleCharacter;
import Client.MapleClient;
import Handler.Handler;
import Opcode.header.InHeader;
import Opcode.header.OutHeader;
import connection.InPacket;
import connection.OutPacket;

public class PlayerHandler {
    public static OutPacket OutLockPacket(MapleClient c) {
        OutPacket say = new OutPacket(OutHeader.USE_ITEM_LOCK_V261_1376);
        return say;
    }

    @Handler(op=InHeader.R_USER_DROP_MESO)
    public static void DropMeso(MapleCharacter chr, InPacket inPacket) {
        short unk = inPacket.decodeShort();
        short unk1 = inPacket.decodeShort();
        int meso = inPacket.decodeInt();
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getPosition(), chr, chr, true, (byte)0);
        chr.getCheatTracker().checkDrop(true);
    }

    @Handler(op=InHeader.BLACK_MAGIC_RECV)
    public static void useActionBar(MapleClient c, InPacket inPacket) {
        if (c.getPlayer().getKeyValue("BlackMage") == "0") {
            c.ctx(144, "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 3F BE C4 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 00 00 00 00 00 1E");
            c.getPlayer().setKeyValue("BlackMage", "1");
        } else {
            c.ctx(144, "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 3F BE C4 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 00 00 00 00 00 17");
            c.getPlayer().setKeyValue("BlackMage", "0");
        }
    }

    @Handler(op=InHeader.CP_SelPotentialPath)
    public static void selPlayerPotenPath(MapleClient c, InPacket inPacket) {
        OutPacket outPacket = new OutPacket(OutHeader.LP_SelCharPotentialSetPath);
        byte path = inPacket.decodeByte();
        outPacket.encodeByte(true);
        outPacket.encodeByte(path);
        c.write(outPacket);
    }
}

