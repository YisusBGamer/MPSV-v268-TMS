/*
 * Decompiled with CFR 0.152.
 */
package Net.server.movement;

import Net.server.movement.MovementBase;
import tools.data.MaplePacketLittleEndianWriter;

public class MovementTeleport
extends MovementBase {
    public MovementTeleport(int command, int elapse, int moveAction, byte forcedStop) {
        super(command, moveAction, elapse, forcedStop);
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter lew) {
        lew.write(this.getCommand());
        lew.writePos(this.getPosition());
        lew.writeShort(this.getFH());
        lew.writeInt(this.getUnk2());
        lew.write(this.getMoveAction());
        lew.writeShort(this.getElapse());
        lew.write(this.getForcedStop());
    }
}

