package connection.packet;

import Opcode.header.OutHeader;
import connection.OutPacket;

public class UserLocal {

    public static OutPacket inGameDirectionEvent(InGameDirectionEvent igdr) {
        OutPacket outPacket = new OutPacket(OutHeader.LP_UserInGameDirectionEvent);

        outPacket.encode(igdr);

        return outPacket;
    }
}
