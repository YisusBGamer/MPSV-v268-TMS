/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleClient;
import Config.constants.GameConstants;
import Net.server.maps.AnimatedMapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Opcode.header.OutHeader;
import java.util.concurrent.ScheduledFuture;
import tools.data.MaplePacketLittleEndianWriter;

public final class MapleFieldAttackObj
extends AnimatedMapleMapObject {
    private final int ownerid;
    private boolean side;
    private ScheduledFuture<?> schedule = null;
    private int state;
    private final int duration;

    public MapleFieldAttackObj(int state, int ownerid, int duration) {
        this.ownerid = ownerid;
        this.state = state;
        this.duration = duration;
    }

    public void cancel() {
        if (this.schedule != null) {
            this.schedule.cancel(true);
            this.schedule = null;
        }
    }

    public int getOwnerId() {
        return this.ownerid;
    }

    public boolean getSide() {
        return this.side;
    }

    public void setSide(boolean side) {
        this.side = side;
    }

    public ScheduledFuture<?> getSchedule() {
        return this.schedule;
    }

    public void setSchedule(ScheduledFuture<?> s) {
        this.schedule = s;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int value) {
        this.state = value;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.FIELD_ATTACK_OBJ;
    }

    @Override
    public int getRange() {
        return GameConstants.maxViewRange();
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FIELDATTACKOBJ_CREATE.getValue());
        mplew.writeInt(this.getObjectId());
        mplew.writeInt(this.state);
        mplew.writeInt(this.ownerid);
        mplew.writeInt(0);
        mplew.writeBool(false);
        mplew.writePosInt(this.getPosition());
        mplew.writeBool(this.side);
        client.announce(mplew.getPacket());
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FIELDATTACKOBJ_REMOVE_BYLIST.getValue());
        mplew.writeInt(1);
        mplew.writeInt(this.getObjectId());
        client.announce(mplew.getPacket());
    }
}

