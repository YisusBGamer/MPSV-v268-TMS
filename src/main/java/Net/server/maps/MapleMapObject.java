/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleClient;
import Client.SecondaryStat;
import Net.server.maps.MapleMapObjectType;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class MapleMapObject {
    private final Point position = new Point();
    private int objectId;
    private int dwOwnerID = 0;
    private boolean custom;

    public Point getPosition() {
        return new Point(this.position);
    }

    public int getDwOwnerID() {
        return this.dwOwnerID;
    }

    public Point getTruePosition() {
        return this.position;
    }

    public void setDwOwnerID(int dwOwnerID) {
        this.dwOwnerID = dwOwnerID;
    }

    public boolean isCustom() {
        return this.custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public int getObjectId() {
        return this.objectId;
    }

    public void setObjectId(int id) {
        this.objectId = id;
    }

    public Rectangle getBounds() {
        return new Rectangle(this.getPosition().x - 50, this.getPosition().y - 37, 50, 75);
    }

    public abstract MapleMapObjectType getType();

    public abstract int getRange();

    public abstract void sendSpawnData(MapleClient var1);

    public abstract void sendDestroyData(MapleClient var1);

    public String toString() {
        return "Type:" + this.getType().name() + " ObjectID:" + this.objectId;
    }

    public boolean getDiseases(SecondaryStat i) {
        return false;
    }
}

