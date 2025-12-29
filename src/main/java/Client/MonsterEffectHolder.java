/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Net.server.buffs.MapleStatEffect;

public class MonsterEffectHolder {
    public MapleStatEffect effect;
    public long startTime;
    public int localDuration;
    public int fromChrID;
    public int value;
    public int sourceID;
    public int level;
    public int remain = 0;
    public int z = 0;
    public long nextDotTime;
    public long dotDamage;
    public int moboid;
    public int dotInterval;
    public int dotSuperpos;

    public MonsterEffectHolder(int sourceID, int level, int value) {
        this.sourceID = sourceID;
        this.level = level;
        this.value = value;
    }

    public MonsterEffectHolder(int chrID, int value, long startTime, int localDuration, MapleStatEffect effect) {
        this.effect = effect;
        this.startTime = startTime;
        this.value = value;
        this.localDuration = localDuration;
        this.fromChrID = chrID;
        this.sourceID = effect.getSourceId();
        this.level = effect.getLevel();
    }

    public final boolean canNextDot() {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.nextDotTime <= currentTimeMillis) {
            this.nextDotTime = currentTimeMillis + (long)this.dotInterval;
            return true;
        }
        return false;
    }

    public final long getLeftTime() {
        return Math.max(this.startTime + (long)this.localDuration - System.currentTimeMillis(), 0L);
    }

    public final long getCancelTime() {
        return this.startTime + (long)this.localDuration;
    }
}

