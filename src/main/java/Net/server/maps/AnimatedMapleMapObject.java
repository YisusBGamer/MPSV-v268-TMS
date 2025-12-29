/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Net.server.maps.MapleMapObject;

public abstract class AnimatedMapleMapObject
extends MapleMapObject {
    private int stance;
    private int homeFH;
    private int currentFh;
    private long lastMoveTime;

    public long getLastMoveTime() {
        return this.lastMoveTime;
    }

    public void setLastMoveTime(int n) {
        this.lastMoveTime = System.currentTimeMillis() + (long)n;
    }

    public int getHomeFH() {
        return this.homeFH;
    }

    public int getCurrentFH() {
        return this.currentFh;
    }

    public void setHomeFH(int bua) {
        this.homeFH = bua;
    }

    public void setCurrentFh(int bub) {
        this.currentFh = bub;
    }

    public int getStance() {
        return this.stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public boolean isFacingLeft() {
        return this.getStance() % 2 != 0;
    }

    public int getFacingDirection() {
        return Math.abs(this.getStance() % 2);
    }
}

