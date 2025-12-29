/*
 * Decompiled with CFR 0.152.
 */
package Net.server.life;

import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterStats;
import Net.server.maps.MapleMap;
import java.awt.Point;

public abstract class Spawns {
    public abstract MapleMonsterStats getMonster();

    public abstract byte getCarnivalTeam();

    public abstract boolean shouldSpawn(long var1);

    public abstract int getCarnivalId();

    public abstract MapleMonster spawnMonster(MapleMap var1);

    public abstract int getMobTime();

    public abstract Point getPosition();

    public abstract int getF();

    public abstract int getFh();
}

