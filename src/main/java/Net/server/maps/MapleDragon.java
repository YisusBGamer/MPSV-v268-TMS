/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Net.server.maps.AnimatedMapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.SummonPacket;

public class MapleDragon
extends AnimatedMapleMapObject {
    private int owner;
    private int jobid;

    public MapleDragon(MapleCharacter owner) {
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        if (this.jobid < 2200 || this.jobid > 2218) {
            throw new RuntimeException("試圖生成1個寶貝龍的信息，但角色不是龍神職業.");
        }
        this.setPosition(owner.getPosition());
        this.setStance(4);
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.announce(SummonPacket.spawnDragon(this));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(SummonPacket.removeDragon(this.owner));
    }

    public int getOwner() {
        return this.owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getJobId() {
        return this.jobid;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }
}

