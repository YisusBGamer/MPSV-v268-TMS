/*
 * Decompiled with CFR 0.152.
 */
package connection.packet;

import Opcode.header.OutHeader;
import connection.OutPacket;

public class MapLoadable {
    public static OutPacket setMapTaggedObjectVisisble(String MapTagedObjectTag) {
        OutPacket outPacket = new OutPacket(OutHeader.LP_SetMapTagedObjectVisible);
        outPacket.encodeString(MapTagedObjectTag);
        return outPacket;
    }
}

