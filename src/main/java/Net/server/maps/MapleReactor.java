/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleClient;
import Config.constants.GameConstants;
import Net.server.Timer;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleReactorStats;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.awt.Rectangle;
import tools.Pair;

public class MapleReactor
extends MapleMapObject {
    private int rid;
    private MapleReactorStats stats;
    private byte state = 0;
    private byte facingDirection = 0;
    private int delay = -1;
    private MapleMap map;
    private String name = "";
    private boolean timerActive = false;
    private boolean alive = true;
    private boolean custom = false;
    private boolean pqAction = false;
    private Point srcPos = new Point();
    private short hitStart;
    private byte properEventIdx;
    private byte stateEnd;
    private int ownerID;
    private long gatherTime;

    public MapleReactor(MapleReactorStats stats, int rid) {
        this.stats = stats;
        this.rid = rid;
    }

    public Point getSrcPos() {
        return this.srcPos;
    }

    public void setSrcPos(Point srcPos) {
        this.srcPos = srcPos;
    }

    public int getRid() {
        return this.rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public MapleReactorStats getStats() {
        return this.stats;
    }

    public void setStats(MapleReactorStats stats) {
        this.stats = stats;
    }

    @Override
    public boolean isCustom() {
        return this.custom;
    }

    @Override
    public void setCustom(boolean c) {
        this.custom = c;
    }

    public byte getFacingDirection() {
        return this.facingDirection;
    }

    public void setFacingDirection(byte facingDirection) {
        this.facingDirection = facingDirection;
    }

    public boolean isTimerActive() {
        return this.timerActive;
    }

    public void setTimerActive(boolean active) {
        this.timerActive = active;
    }

    public int getReactorId() {
        return this.rid;
    }

    public byte getState() {
        return this.state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.REACTOR;
    }

    @Override
    public int getRange() {
        return GameConstants.maxViewRange();
    }

    public int getReactorType() {
        return this.stats.getType(this.state);
    }

    public byte getTouch() {
        return this.stats.canTouch(this.state);
    }

    public MapleMap getMap() {
        return this.map;
    }

    public void setMap(MapleMap map) {
        this.map = map;
    }

    public Pair<Integer, Integer> getReactItem() {
        return this.stats.getReactItem(this.state);
    }

    public boolean isPqAction() {
        return this.pqAction;
    }

    public void setPqAction(boolean pqAction) {
        this.pqAction = pqAction;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(MaplePacketCreator.reactorLeaveField(this));
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.announce(MaplePacketCreator.spawnReactor(this));
    }

    public void forceStartReactor(MapleClient c) {
        c.getPlayer().getScriptManager().startReactorScript(this);
    }

    public void forceHitReactor(byte newState) {
        this.setState(newState);
        this.setTimerActive(false);
        this.map.broadcastMessage(MaplePacketCreator.triggerReactor(this));
    }

    public void hitReactor(MapleClient c) {
        this.hitReactor(0, (short)0, c);
    }

    public void delayedDestroyReactor(long delay) {
        Timer.MapTimer.getInstance().schedule(() -> this.map.destroyReactor(this.getObjectId()), delay);
    }

    public void hitReactor(int charPos, short stance, MapleClient c) {
        this.setHitStart(stance);
        if (this.stats.getType(this.state) < 999 && this.stats.getType(this.state) != -1 && this.stats.getType(this.state) != 2) {
            byte oldState = this.state;
            this.state = this.stats.getNextState(this.state);
            if (this.stats.getNextState(this.state) == -1 || this.stats.getType(this.state) == 999) {
                if ((this.stats.getType(this.state) < 100 || this.stats.getType(this.state) == 999) && this.delay > 0) {
                    this.map.destroyReactor(this.getObjectId());
                } else {
                    this.map.broadcastMessage(MaplePacketCreator.triggerReactor(this));
                }
                c.getPlayer().getScriptManager().startReactorScript(this);
            } else {
                if (!(this.rid != 9239001 && this.rid != 2008009 || this.isPqAction())) {
                    this.setSrcPos(this.getPosition());
                    this.setPqAction(true);
                    this.ownerID = c.getPlayer().getId();
                } else if (this.isPqAction()) {
                    this.setPqAction(false);
                    c.getPlayer().setReactor(null);
                    this.ownerID = 0;
                }
                boolean done = false;
                this.map.broadcastMessage(MaplePacketCreator.triggerReactor(this));
                if (this.rid != 1058018 && this.rid != 1058019 && (this.stats.getType(this.state) == 9 || this.stats.getType(this.state) == 11 || this.stats.getType(this.state) == 100)) {
                    c.getPlayer().getScriptManager().startReactorScript(this);
                    done = true;
                }
                if (this.state == this.stats.getNextState(this.state) || this.getReactorId() == 9250140 || this.getReactorId() == 2618000 || this.getReactorId() == 2309000) {
                    if (!done && this.rid > 200011) {
                        c.getPlayer().getScriptManager().startReactorScript(this);
                    }
                    done = true;
                }
                if (this.stats.getTimeOut(this.state) > 0) {
                    if (!done && this.rid > 200011) {
                        c.getPlayer().getScriptManager().startReactorScript(this);
                    }
                    this.scheduleSetState(this.state, (byte)0, this.stats.getTimeOut(this.state));
                }
            }
        }
    }

    public Rectangle getArea() {
        int height = this.stats.getBR().y - this.stats.getTL().y;
        int width = this.stats.getBR().x - this.stats.getTL().x;
        int origX = this.getPosition().x + this.stats.getTL().x;
        int origY = this.getPosition().y + this.stats.getTL().y;
        return new Rectangle(origX, origY, width, height);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "反應堆 工作ID:" + this.getObjectId() + " ReactorID: " + this.rid + " 坐標: " + this.getPosition().x + "/" + this.getPosition().y + " 狀態: " + this.state + " 類型: " + this.stats.getType(this.state);
    }

    public void delayedHitReactor(MapleClient c, long delay) {
        Timer.MapTimer.getInstance().schedule(() -> this.hitReactor(c), delay);
    }

    public void scheduleSetState(byte oldState, byte newState, long delay) {
        Timer.MapTimer.getInstance().schedule(() -> {
            if (this.state == oldState) {
                this.forceHitReactor(newState);
            }
        }, delay);
    }

    public short getHitStart() {
        return this.hitStart;
    }

    public void setHitStart(short hitStart) {
        this.hitStart = hitStart;
    }

    public byte getProperEventIdx() {
        return this.properEventIdx;
    }

    public void setProperEventIdx(byte properEventIdx) {
        this.properEventIdx = properEventIdx;
    }

    public byte getStateEnd() {
        return this.stateEnd;
    }

    public void setStateEnd(byte stateEnd) {
        this.stateEnd = stateEnd;
    }

    public int getOwnerID() {
        return this.ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public void setGatherTime(long gatherTime) {
        this.gatherTime = gatherTime;
    }

    public long getGatherTime() {
        return this.gatherTime;
    }
}

