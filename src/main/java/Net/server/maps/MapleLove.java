/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Config.constants.GameConstants;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.MaplePacketCreator;
import java.awt.Point;

public class MapleLove
extends MapleMapObject {
    private final Point pos;
    private final MapleCharacter owner;
    private final String text;
    private final int ft;
    private final int itemid;

    public MapleLove(MapleCharacter owner, Point pos, int ft, String text, int itemid) {
        this.owner = owner;
        this.pos = pos;
        this.text = text;
        this.ft = ft;
        this.itemid = itemid;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.LOVE;
    }

    @Override
    public int getRange() {
        return GameConstants.maxViewRange();
    }

    @Override
    public Point getPosition() {
        return this.pos.getLocation();
    }

    @Override
    public void setPosition(Point position) {
        throw new UnsupportedOperationException();
    }

    public MapleCharacter getOwner() {
        return this.owner;
    }

    public int getItemId() {
        return this.itemid;
    }

    @Override
    public void sendSpawnData(MapleClient c) {
        c.announce(MaplePacketCreator.spawnLove(this.getObjectId(), this.itemid, this.owner.getName(), this.text, this.pos, this.ft));
    }

    @Override
    public void sendDestroyData(MapleClient c) {
        c.announce(MaplePacketCreator.removeLove(this.getObjectId(), this.itemid));
    }
}

