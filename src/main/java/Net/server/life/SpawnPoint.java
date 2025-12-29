/*
 * Decompiled with CFR 0.152.
 */
package Net.server.life;

import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterStats;
import Net.server.life.Spawns;
import Net.server.maps.MapleMap;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

public class SpawnPoint
extends Spawns {
    private final MapleMonsterStats monster;
    private final Point pos;
    private final int mobTime;
    private final int fh;
    private final int f;
    private final int id;
    private final AtomicInteger spawnedMonsters = new AtomicInteger(0);
    private final String msg;
    private final byte carnivalTeam;
    private long nextPossibleSpawn;
    private int carnival = -1;
    private int level = -1;

    public SpawnPoint(MapleMonster monster, Point pos, int mobTime, byte carnivalTeam, String msg) {
        this.monster = monster.getStats();
        this.pos = pos;
        this.id = monster.getId();
        this.fh = monster.getCurrentFH();
        this.f = monster.getF();
        this.mobTime = mobTime < 0 ? -1 : mobTime * 1000;
        this.carnivalTeam = carnivalTeam;
        this.msg = msg;
        this.nextPossibleSpawn = System.currentTimeMillis();
    }

    public void setCarnival(int c) {
        this.carnival = c;
    }

    public void setLevel(int c) {
        this.level = c;
    }

    @Override
    public int getF() {
        return this.f;
    }

    @Override
    public int getFh() {
        return this.fh;
    }

    @Override
    public Point getPosition() {
        return this.pos;
    }

    @Override
    public MapleMonsterStats getMonster() {
        return this.monster;
    }

    @Override
    public byte getCarnivalTeam() {
        return this.carnivalTeam;
    }

    @Override
    public int getCarnivalId() {
        return this.carnival;
    }

    @Override
    public boolean shouldSpawn(long time) {
        if (this.mobTime < 0) {
            return false;
        }
        return (this.mobTime == 0 && this.monster.isMobile() || this.spawnedMonsters.get() <= 0) && this.spawnedMonsters.get() <= 1 && this.nextPossibleSpawn <= time;
    }

    @Override
    public MapleMonster spawnMonster(MapleMap map) {
        MapleMonster mob = new MapleMonster(this.id, this.monster);
        mob.setPosition(this.pos);
        mob.setCy(this.pos.y);
        mob.setRx0(this.pos.x - 50);
        mob.setRx1(this.pos.x + 50);
        mob.setCurrentFh(this.fh);
        mob.setF(this.f);
        mob.setCarnivalTeam(this.carnivalTeam);
        if (this.level > -1) {
            mob.setForcedMobStat(this.level);
        }
        this.spawnedMonsters.incrementAndGet();
        mob.addListener(() -> {
            this.nextPossibleSpawn = System.currentTimeMillis();
            if (this.mobTime > 0) {
                this.nextPossibleSpawn += (long)this.mobTime;
            }
            this.spawnedMonsters.decrementAndGet();
        });
        map.spawnMonster(mob, -2);
        if (this.msg != null) {
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, this.msg));
        }
        return mob;
    }

    @Override
    public int getMobTime() {
        return this.mobTime;
    }
}

