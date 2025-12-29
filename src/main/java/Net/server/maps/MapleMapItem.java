/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.InventoryPacket;
import java.awt.Point;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import tools.Randomizer;

public class MapleMapItem
extends MapleMapObject {
    private final int ownerID;
    private final boolean playerDrop;
    private final ReentrantLock lock = new ReentrantLock();
    protected Item item;
    private MapleMapObject dropper;
    protected int meso = 0;
    protected int pointType = -1;
    protected int questid = -1;
    private byte ownType;
    private boolean pickedUp = false;
    private long nextExpiry = 0L;
    private long nextFFA = 0L;
    private int skill;
    private byte enterType = 1;
    private int delay;
    private int animation = 1;
    private int pickUpID;
    private int onlySelfID = -1;
    private int nDropMotionType = 0;
    private int nDropSpeed = 0;
    private int nRand = 0;
    private int sourceOID = 0;
    private boolean bCollisionPickUp = false;

    public MapleMapItem(Item item, Point position, MapleMapObject dropper, MapleCharacter owner, byte ownType, boolean playerDrop) {
        this.setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.ownerID = owner.getId();
        this.ownType = ownType;
        this.playerDrop = playerDrop;
    }

    public MapleMapItem(Item item, Point position, MapleMapObject dropper, MapleCharacter owner, byte ownType, boolean playerDrop, int questid) {
        this.setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.ownerID = owner.getId();
        this.ownType = ownType;
        this.playerDrop = playerDrop;
        this.questid = questid;
    }

    public MapleMapItem(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, byte ownType, boolean playerDrop) {
        this.setPosition(position);
        this.item = null;
        this.dropper = dropper;
        this.ownerID = owner.getId();
        this.meso = meso;
        this.ownType = ownType;
        this.playerDrop = playerDrop;
    }

    public MapleMapItem(int pointType, Item item, Point position, MapleMapObject dropper, MapleCharacter owner, byte ownType, boolean playerDrop) {
        this.setPosition(position);
        this.item = item;
        this.dropper = dropper;
        this.ownerID = owner.getId();
        this.pointType = pointType;
        this.ownType = ownType;
        this.playerDrop = playerDrop;
    }

    public MapleMapItem(Item item, Point position) {
        this.setPosition(position);
        this.item = item;
        this.ownerID = 0;
        this.ownType = (byte)2;
        this.playerDrop = false;
        this.nRand = Randomizer.nextInt(150) + 50;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item z) {
        this.item = z;
    }

    public int getQuest() {
        return this.questid;
    }

    public int getItemId() {
        if (this.getMeso() > 0) {
            return this.meso;
        }
        if (this.item == null) {
            return -1;
        }
        return this.item.getItemId();
    }

    public MapleMapObject getDropper() {
        return this.dropper;
    }

    public int getOwnerID() {
        return this.ownerID;
    }

    public int getMeso() {
        return this.meso;
    }

    public boolean isPlayerDrop() {
        return this.playerDrop;
    }

    public boolean isPickedUp() {
        return this.pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public byte getOwnType() {
        return this.ownType;
    }

    public void setOwnType(byte z) {
        this.ownType = z;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.ITEM;
    }

    @Override
    public int getRange() {
        return GameConstants.maxViewRange();
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if ((this.getMeso() > 0 || this.getMeso() <= 0 && this.item != null) && (this.questid <= 0 || client.getPlayer().getQuestStatus(this.questid) == 1 && client.getPlayer().needQuestItem(this.questid, this.item.getItemId()))) {
            if (this.getOnlySelfID() >= 0 && (client == null || client.getPlayer() == null || client.getPlayer().getId() != this.getOnlySelfID())) {
                return;
            }
            client.announce(InventoryPacket.dropItemFromMapObject(this, this.getPosition(), this.getPosition(), (byte)2));
        }
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(InventoryPacket.removeItemFromMap(this.getObjectId(), this.getAnimation(), this.getPickUpID()));
    }

    public Lock getLock() {
        return this.lock;
    }

    public void registerExpire(long time) {
        this.nextExpiry = System.currentTimeMillis() + time;
    }

    public void registerFFA(long time) {
        this.nextFFA = System.currentTimeMillis() + time;
    }

    public boolean shouldExpire(long now) {
        return !this.pickedUp && this.nextExpiry > 0L && this.nextExpiry < now;
    }

    public boolean shouldFFA(long now) {
        return !this.pickedUp && this.ownType < 2 && this.nextFFA > 0L && this.nextFFA < now;
    }

    public boolean hasFFA() {
        return this.nextFFA > 0L;
    }

    public void expire(MapleMap map) {
        this.pickedUp = true;
        map.broadcastMessage(InventoryPacket.removeItemFromMap(this.getObjectId(), 0, 0));
        map.removeMapObject(this);
    }

    public int getState() {
        if (this.getMeso() > 0) {
            return 0;
        }
        if (ItemConstants.getInventoryType(this.item.getItemId(), false) != MapleInventoryType.EQUIP) {
            return 0;
        }
        Equip equip = (Equip)this.item;
        int state = equip.getState(false);
        int addstate = equip.getState(true);
        if (state <= 0 || state >= 17) {
            int n = state = (state -= 16) < 0 ? 0 : state;
        }
        if (addstate <= 0 || addstate >= 17) {
            addstate = (addstate -= 16) < 0 ? 0 : addstate;
        }
        return state > addstate ? state : addstate;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public int getSkill() {
        return this.skill;
    }

    public byte getEnterType() {
        return this.enterType;
    }

    public void setEnterType(byte enterType) {
        this.enterType = enterType;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getAnimation() {
        return this.animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public void setPickUpID(int pickUpID) {
        this.pickUpID = pickUpID;
    }

    public int getPickUpID() {
        return this.pickUpID;
    }

    public void setOnlySelfID(int onlySelfID) {
        this.onlySelfID = onlySelfID;
    }

    public int getOnlySelfID() {
        return this.onlySelfID;
    }

    public int getDropMotionType() {
        return this.nDropMotionType;
    }

    public void setDropMotionType(int type) {
        this.nDropMotionType = type;
    }

    public int getDropSpeed() {
        return this.nDropSpeed;
    }

    public void setDropSpeed(int speed) {
        this.nDropSpeed = speed;
    }

    public int getRand() {
        return this.nRand;
    }

    public void setRand(int rand) {
        this.nRand = rand;
    }

    public int getSourceOID() {
        return this.sourceOID;
    }

    public void setSourceOID(int oid) {
        this.sourceOID = oid;
    }

    public boolean isCollisionPickUp() {
        return this.bCollisionPickUp;
    }

    public void setCollisionPickUp(boolean b) {
        this.bCollisionPickUp = b;
    }

    public int getPointType() {
        return this.pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }
}

