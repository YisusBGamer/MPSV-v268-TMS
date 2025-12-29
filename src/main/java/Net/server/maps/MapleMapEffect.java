/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleClient;
import Packet.MTSCSPacket;
import Packet.MaplePacketCreator;
import java.util.concurrent.ScheduledFuture;

public class MapleMapEffect {
    private String msg = "";
    private int itemId = 0;
    private int effectType = -1;
    private boolean active = true;
    private boolean jukebox = false;
    ScheduledFuture<?> scheduledFuture;

    public MapleMapEffect(String msg, int itemId) {
        this.msg = msg;
        this.itemId = itemId;
        this.effectType = -1;
    }

    public MapleMapEffect(String msg, int itemId, int effectType) {
        this.msg = msg;
        this.itemId = itemId;
        this.effectType = effectType;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return this.scheduledFuture;
    }

    public boolean isJukebox() {
        return this.jukebox;
    }

    public void setJukebox(boolean actie) {
        this.jukebox = actie;
    }

    public byte[] makeDestroyData() {
        return this.jukebox ? MTSCSPacket.playCashSong(0, "") : MaplePacketCreator.removeMapEffect();
    }

    public byte[] makeStartData() {
        return this.jukebox ? MTSCSPacket.playCashSong(this.itemId, this.msg) : MaplePacketCreator.startMapEffect(this.msg, this.itemId, this.effectType, this.active);
    }

    public void sendStartData(MapleClient c) {
        c.announce(this.makeStartData());
    }
}

