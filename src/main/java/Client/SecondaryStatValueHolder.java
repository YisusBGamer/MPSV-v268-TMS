/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Net.server.Timer;
import Net.server.buffs.MapleStatEffect;
import java.util.concurrent.ScheduledFuture;

public class SecondaryStatValueHolder {
    public MapleStatEffect effect;
    public long startTime;
    public int localDuration;
    public int fromChrID;
    public int value;
    public int sourceID;
    public int x;
    public int z;
    public int DropRate = 0;
    public int BDR = 0;
    public int AttackBossCount = 0;
    public int NormalMobKillCount = 0;
    private final long startChargeTime;
    public MapleStatEffect.CancelEffectAction CancelAction;
    public ScheduledFuture<?> schedule;

    public SecondaryStatValueHolder(int value, int sourceID) {
        this.value = value;
        this.sourceID = sourceID;
        this.startChargeTime = 0L;
        this.startTime = System.currentTimeMillis();
    }

    public SecondaryStatValueHolder(int chrID, int value, int z, long startTime, long startChargeTime, int localDuration, MapleStatEffect effect, MapleStatEffect.CancelEffectAction cancelAction) {
        this.effect = effect;
        this.startTime = startTime;
        this.value = value;
        this.localDuration = localDuration;
        this.fromChrID = chrID;
        this.startChargeTime = startChargeTime;
        this.sourceID = effect.getSourceId();
        this.x = effect.getX();
        this.z = z;
        this.CancelAction = cancelAction;
        if (cancelAction != null) {
            this.schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, localDuration);
        }
    }

    public final long getStartChargeTime() {
        return this.startChargeTime;
    }

    public int getLeftTime() {
        if (this.localDuration < 2100000000) {
            return Math.max((int)(this.startTime + (long)this.localDuration - System.currentTimeMillis()), 0);
        }
        return 2100000000;
    }

    public void cancel() {
        if (this.schedule != null) {
            this.schedule.cancel(false);
            this.schedule = null;
        }
    }
}

