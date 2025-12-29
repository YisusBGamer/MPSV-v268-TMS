/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps.field;

import Net.server.maps.MapleMap;

public class BossSerenField
extends MapleMap {
    private int boss = 0;
    private final int time = 4;
    private final int sunlightValue = 0;

    public static void init() {
    }

    public BossSerenField(int mapid, int channel, int returnMapId, float monsterRate) {
        super(mapid, channel, returnMapId, monsterRate);
    }

    public final void setSeren(int bossid) {
        this.boss = bossid;
    }

    public final int getBoss() {
        return this.boss;
    }

    public final int getCurrentDayTime() {
        return 4;
    }

    public final int getsunlightValue() {
        return 0;
    }

    public final int getDayTime() {
        return 4;
    }
}

