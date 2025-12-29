/*
 * Decompiled with CFR 0.152.
 */
package Net.server.movement;

import Net.server.movement.MovementBase;
import tools.data.MaplePacketLittleEndianWriter;

public final class MovementJump
extends MovementBase {
    public MovementJump(int command, int elapse, int moveAction, byte forcedStop) {
        super(command, moveAction, elapse, forcedStop);
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter mplew) {
        mplew.write(this.getCommand());
        mplew.writePos(this.getPixelsPerSecond());
        if (this.getCommand() == 21 || this.getCommand() == 22) {
            mplew.writeShort(this.getFootStart());
        }
        if (this.getCommand() == 62) {
            mplew.writePos(this.getOffset());
        }
        mplew.write(this.getMoveAction());
        mplew.writeShort(this.getElapse());
        mplew.write(this.getForcedStop());
    }
}

