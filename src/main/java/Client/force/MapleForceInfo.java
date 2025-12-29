/*
 * Decompiled with CFR 0.152.
 */
package Client.force;

import java.awt.Point;

public class MapleForceInfo {
    private int key;
    private int inc;
    private int firstImpact;
    private int secondImpact;
    private int angle;
    private int startDelay;
    private Point position;
    private long time;
    private int maxHitCount;
    private int effectIdx;
    public Point pos2;

    public final int getEffectIdx() {
        return this.effectIdx;
    }

    public final void setEffectIdx(int effectIdx) {
        this.effectIdx = effectIdx;
    }

    public final int getMaxHitCount() {
        return this.maxHitCount;
    }

    public final void setMaxHitCount(int maxHitCount) {
        this.maxHitCount = maxHitCount;
    }

    public final long getTime() {
        return this.time;
    }

    public final void setTime(long time) {
        this.time = time;
    }

    public final Point getPosition() {
        return this.position;
    }

    public final void setPosition(Point position) {
        this.position = position;
    }

    public final int getStartDelay() {
        return this.startDelay;
    }

    public final void setStartDelay(int startDelay) {
        this.startDelay = startDelay;
    }

    public final int getAngle() {
        return this.angle;
    }

    public final void setAngle(int angle) {
        this.angle = angle;
    }

    public final int getSecondImpact() {
        return this.secondImpact;
    }

    public final void setSecondImpact(int secondImpact) {
        this.secondImpact = secondImpact;
    }

    public final int getFirstImpact() {
        return this.firstImpact;
    }

    public final void setFirstImpact(int firstImpact) {
        this.firstImpact = firstImpact;
    }

    public final int getInc() {
        return this.inc;
    }

    public final void setInc(int inc) {
        this.inc = inc;
    }

    public final int getKey() {
        return this.key;
    }

    public final void setKey(int key) {
        this.key = key;
    }
}

