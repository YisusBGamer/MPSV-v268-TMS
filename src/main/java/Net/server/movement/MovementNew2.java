/*
 * Decompiled with CFR 0.152.
 */
package Net.server.movement;

import Net.server.movement.MovementBase;
import tools.data.MaplePacketLittleEndianWriter;

public class MovementNew2
extends MovementBase {
    public MovementNew2(int command, int elapse, int moveAction, byte forcedStop) {
        super(command, moveAction, elapse, forcedStop);
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter mplew) {
        mplew.write(this.getCommand());
        mplew.writeInt(this.getUnk2());
        mplew.write(this.getMoveAction());
        mplew.writeShort(this.getElapse());
        mplew.write(this.getForcedStop());
    }
}

