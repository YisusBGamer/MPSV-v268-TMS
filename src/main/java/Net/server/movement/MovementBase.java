/*
 * Decompiled with CFR 0.152.
 */
package Net.server.movement;

import Net.server.movement.LifeMovement;
import java.awt.Point;
import tools.data.MaplePacketLittleEndianWriter;

public class MovementBase
implements LifeMovement {
    private Point position;
    private Point pixelsPerSecond;
    private Point offset;
    private int elapse;
    private int moveAction;
    private int command;
    private int fh;
    private int stat;
    private int unk2;
    private int unk3;
    private short footStart;
    private short unk1;
    private byte forcedStop;

    public MovementBase(int command, int stat) {
        this.setCommand(command);
        this.setStat(stat);
    }

    public MovementBase(int command, int moveAction, int elapse, byte forcedStop) {
        this.setCommand(command);
        this.setMoveAction(moveAction);
        this.setElapse(elapse);
        this.setForcedStop(forcedStop);
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter mplew) {
        mplew.write(this.getCommand());
        mplew.write(this.getMoveAction());
        mplew.writeShort(this.getElapse());
        mplew.write(this.getForcedStop());
    }

    public final int getCommand() {
        return this.command;
    }

    public final void setCommand(int command) {
        this.command = command;
    }

    @Override
    public final int getElapse() {
        return this.elapse;
    }

    public final void setElapse(int elapse) {
        this.elapse = elapse;
    }

    @Override
    public final int getMoveAction() {
        return this.moveAction;
    }

    public final void setMoveAction(int moveAction) {
        this.moveAction = moveAction;
    }

    public final byte getForcedStop() {
        return this.forcedStop;
    }

    public final void setForcedStop(byte forcedStop) {
        this.forcedStop = forcedStop;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getPixelsPerSecond() {
        return this.pixelsPerSecond;
    }

    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    public Point getOffset() {
        return this.offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public int getFH() {
        return this.fh;
    }

    public void setFH(short fh) {
        this.fh = fh;
    }

    public short getFootStart() {
        return this.footStart;
    }

    public void setFootStart(short footStart) {
        this.footStart = footStart;
    }

    public final int getStat() {
        return this.stat;
    }

    public final void setStat(int stat) {
        this.stat = stat;
    }

    public void setUnk1(short unk1) {
        this.unk1 = unk1;
    }

    public short getUnk1() {
        return this.unk1;
    }

    public int getUnk2() {
        return this.unk2;
    }

    public void setUnk2(int unk2) {
        this.unk2 = unk2;
    }

    public int getUnk3() {
        return this.unk3;
    }

    public void setUnk3(int unk3) {
        this.unk3 = unk3;
    }
}

