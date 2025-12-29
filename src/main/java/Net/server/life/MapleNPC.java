/*
 * Decompiled with CFR 0.152.
 */
package Net.server.life;

import Client.MapleClient;
import Net.server.life.AbstractLoadedMapleLife;
import Net.server.maps.MapleMapObjectType;
import Net.server.shop.MapleShopFactory;
import Packet.NPCPacket;

public class MapleNPC
extends AbstractLoadedMapleLife {
    private final int mapid;
    private String name = "MISSINGNO";
    private boolean custom = false;
    private int ownerid = 0;
    private boolean move;

    public MapleNPC(int id, String name, int mapid) {
        super(id);
        this.name = name;
        this.mapid = mapid;
    }

    public boolean hasShop() {
        return MapleShopFactory.getInstance().getShopForNPC(this.getId()) != null;
    }

    public void sendShop(MapleClient c) {
        MapleShopFactory.getInstance().getShopForNPC(this.getId()).sendShop(c);
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (this.getId() < 9901000 && this.getId() != 9000069 && this.getId() != 9000133) {
            client.announce(NPCPacket.spawnNPC(this));
            client.announce(NPCPacket.spawnNPCRequestController(this, true));
        }
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(NPCPacket.removeNPC(this.getObjectId()));
        if (!this.isHidden() && client.getPlayer() != null && client.getPlayer().getMap() != null && client.getPlayer().getMap().isNpcHide(this.getId())) {
            client.announce(NPCPacket.removeNPCController(this.getObjectId(), false));
        }
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.NPC;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    @Override
    public boolean isCustom() {
        return this.custom;
    }

    @Override
    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public int getMapid() {
        return this.mapid;
    }

    public int getOwnerid() {
        return this.ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }

    public boolean isMove() {
        return this.move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}

