/*
 * Decompiled with CFR 0.152.
 */
package Net.server.movement;

import Net.server.movement.MovementBase;
import tools.data.MaplePacketLittleEndianWriter;

public final class MovementStatChange
extends MovementBase {
    public MovementStatChange(int command, int stat) {
        super(command, stat);
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter lew) {
        lew.write(this.getCommand());
        lew.write(this.getStat());
    }
}

