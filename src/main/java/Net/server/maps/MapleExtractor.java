/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.MaplePacketCreator;

public class MapleExtractor
extends MapleMapObject {
    public final int owner;
    public final int timeLeft;
    public final int itemId;
    public final int fee;
    public final long startTime;
    public final String ownerName;

    public MapleExtractor(MapleCharacter owner, int itemId, int fee, int timeLeft) {
        this.owner = owner.getId();
        this.itemId = itemId;
        this.fee = fee;
        this.ownerName = owner.getName();
        this.startTime = System.currentTimeMillis();
        this.timeLeft = timeLeft;
        this.setPosition(owner.getPosition());
    }

    public int getTimeLeft() {
        return this.timeLeft;
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.announce(MaplePacketCreator.makeExtractor(this.owner, this.ownerName, this.getPosition(), this.getTimeLeft(), this.itemId, this.fee));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(MaplePacketCreator.removeExtractor(this.owner));
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.EXTRACTOR;
    }
}

