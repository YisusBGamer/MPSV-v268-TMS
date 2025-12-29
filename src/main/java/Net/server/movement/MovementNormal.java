/*
 * Decompiled with CFR 0.152.
 */
package Net.server.movement;

import Net.server.movement.MovementBase;
import tools.data.MaplePacketLittleEndianWriter;

public final class MovementNormal
extends MovementBase {
    public MovementNormal(int command, int elapse, int moveAction, byte forcedStop) {
        super(command, moveAction, elapse, forcedStop);
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter mplew) {
        mplew.write(this.getCommand());
        mplew.writePos(this.getPosition());
        mplew.writePos(this.getPixelsPerSecond());
        mplew.writeShort(this.getFH());
        if (this.getCommand() == 15 || this.getCommand() == 17) {
            mplew.writeShort(this.getFootStart());
        }
        mplew.writePos(this.getOffset());
        mplew.writeShort(this.getUnk1());
        mplew.write(this.getMoveAction());
        mplew.writeShort(this.getElapse());
        mplew.write(this.getForcedStop());
    }
}

