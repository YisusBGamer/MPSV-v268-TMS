/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.MaplePacketCreator;
import java.awt.Point;

public class MechDoor
extends MapleMapObject {
    private int owner;
    private int partyid;
    private int id;

    public MechDoor() {
    }

    public MechDoor(MapleCharacter owner, Point pos, int id) {
        this.owner = owner.getId();
        this.partyid = owner.getParty() == null ? 0 : owner.getParty().getId();
        this.setPosition(pos);
        this.id = id;
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.announce(MaplePacketCreator.spawnMechDoor(this, false));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(MaplePacketCreator.removeMechDoor(this, false));
    }

    public int getOwnerId() {
        return this.owner;
    }

    public int getPartyId() {
        return this.partyid;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.TOWN_PORTAL;
    }
}

