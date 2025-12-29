/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.inventory.MapleInventory;
import Client.inventory.MapleInventoryType;
import Net.server.maps.AnimatedMapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.SummonPacket;
import java.awt.Point;

public class MapleSkillPet
extends AnimatedMapleMapObject {
    private final int owner;
    private final int jobid;
    private int weapon;
    private final int skillid;
    private final boolean stats;
    private boolean show;
    private Point pos = new Point(0, 0);
    private int state;
    private int specialState = 1;

    public MapleSkillPet(MapleCharacter owner) {
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        this.skillid = 40020109;
        this.show = true;
        this.stats = false;
        this.state = 1;
        this.setPosition(owner.getPosition());
        this.setStance(owner.getFH());
        MapleInventory equipped = owner.getInventory(MapleInventoryType.EQUIPPED);
        this.weapon = equipped == null || equipped.getItem((short)-5200) == null ? 0 : equipped.getItem((short)-5200).getItemId();
    }

    public int getOwner() {
        return this.owner;
    }

    public int getJobId() {
        return this.jobid;
    }

    public void setWeapon(int id) {
        this.weapon = id;
    }

    public int getWeapon() {
        return this.weapon;
    }

    public int getSkillId() {
        return this.skillid;
    }

    public boolean getStats() {
        return this.stats;
    }

    public boolean isShow() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SKILLPET;
    }

    public final Point getPos() {
        return this.pos;
    }

    public final void setPos(Point pos) {
        this.pos = pos;
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (this.state == 2) {
            client.announce(SummonPacket.FoxManEnterField(this));
        }
        client.announce(SummonPacket.spawnSkillPet(this));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
    }

    public final void update(MapleCharacter chr) {
        if (chr.getMap() != null) {
            if (this.state >= 2) {
                chr.getMap().broadcastMessage(chr, SummonPacket.FoxManEnterField(this), true);
                chr.getMap().broadcastMessage(chr, SummonPacket.FoxManShowChangeEffect(this), true);
            }
            chr.getMap().broadcastMessage(chr, SummonPacket.SkillPetState(this), true);
            if (this.state == 1) {
                chr.getMap().broadcastMessage(chr, SummonPacket.FoxManLeaveField(this), true);
            }
        }
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSpecialState() {
        return this.specialState;
    }

    public void setSpecialState(int specialState) {
        this.specialState = specialState;
    }
}

