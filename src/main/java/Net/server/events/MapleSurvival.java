/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.field.ClockPacket
 *  connection.packet.FieldPacket
 */
package Net.server.events;

import Client.MapleCharacter;
import Net.server.Timer;
import Net.server.events.MapleEvent;
import Net.server.events.MapleEventType;
import Packet.MaplePacketCreator;
import SwordieX.field.ClockPacket;
import connection.packet.FieldPacket;
import java.util.concurrent.ScheduledFuture;

public class MapleSurvival
extends MapleEvent {
    protected final long time = 360000L;
    protected long timeStarted = 0L;
    protected ScheduledFuture<?> olaSchedule;

    public MapleSurvival(int channel, MapleEventType type) {
        super(channel, type);
    }

    @Override
    public void finished(MapleCharacter chr) {
        MapleSurvival.givePrize(chr);
    }

    @Override
    public void onMapLoad(MapleCharacter chr) {
        super.onMapLoad(chr);
        if (this.isTimerStarted()) {
            chr.send(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)((int)(this.getTimeLeft() / 1000L)))));
        }
    }

    @Override
    public void startEvent() {
        this.unreset();
        super.reset();
        this.broadcast(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)360L)));
        this.timeStarted = System.currentTimeMillis();
        this.olaSchedule = Timer.EventTimer.getInstance().schedule(() -> {
            for (int i = 0; i < this.type.mapids.length; ++i) {
                for (MapleCharacter chr : this.getMap(i).getCharacters()) {
                    this.warpBack(chr);
                }
                this.unreset();
            }
        }, this.time);
        this.broadcast(MaplePacketCreator.serverNotice(0, "The portal has now opened. Press the up arrow key at the portal to enter."));
        this.broadcast(MaplePacketCreator.serverNotice(0, "Fall down once, and never get back up again! Get to the top without falling down!"));
    }

    public boolean isTimerStarted() {
        return this.timeStarted > 0L;
    }

    public long getTime() {
        return 360000L;
    }

    public void resetSchedule() {
        this.timeStarted = 0L;
        if (this.olaSchedule != null) {
            this.olaSchedule.cancel(false);
        }
        this.olaSchedule = null;
    }

    @Override
    public void reset() {
        super.reset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(false);
    }

    @Override
    public void unreset() {
        super.unreset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(true);
    }

    public long getTimeLeft() {
        return 360000L - (System.currentTimeMillis() - this.timeStarted);
    }
}

