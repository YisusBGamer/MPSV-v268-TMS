/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps.field;

import Net.server.life.MapleLifeFactory;
import Net.server.maps.MapleMap;
import Packet.MobPacket;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import tools.Randomizer;

public class BossLucidField
extends MapleMap {
    private static final List<Point> phase1_pos = new ArrayList<Point>();
    private static final List<Point> phase2_pos = new ArrayList<Point>();
    private static final List<ALX> spiral = new ArrayList<ALX>();
    private static final List<ALX> bidirection = new ArrayList<ALX>();
    private static final List<ALX> spiralButterfly = new ArrayList<ALX>();
    private final AtomicInteger butterFlyCount = new AtomicInteger(0);
    private int stats = 0;
    private boolean showStep = true;

    public static void init() {
        try {
            ALX alx;
            for (MapleData zj : MapleDataProviderFactory.getEtc().getData("BossLucid.img").getChildByPath("Butterfly").getChildByPath("phase1_pos")) {
                phase1_pos.add(MapleDataTool.getPoint("pos", zj));
            }
            for (MapleData zj : MapleDataProviderFactory.getEtc().getData("BossLucid.img").getChildByPath("Butterfly").getChildByPath("phase2_pos")) {
                phase2_pos.add(MapleDataTool.getPoint("pos", zj));
            }
            MapleData shootInfo = MapleDataProviderFactory.getEtc().getData("BossLucid.img").getChildByPath("Shoot").getChildByPath("info");
            for (MapleData zj : shootInfo.getChildByPath("spiral")) {
                alx = new ALX();
                alx.shotAngle = MapleDataTool.getInt("ShotAngle", zj, 0);
                alx.shotAngleRate = MapleDataTool.getInt("ShotAngleRate", zj, 0);
                alx.shotAngleDiff = MapleDataTool.getInt("ShotAngleDiff", zj, 0);
                alx.shotSpeed = MapleDataTool.getInt("ShotSpeed", zj, 0);
                alx.shotInterval = MapleDataTool.getInt("ShotInterval", zj, 0);
                alx.shotCount = MapleDataTool.getInt("ShotCount", zj, 0);
                alx.bulletAngleRate = MapleDataTool.getInt("BulletAngleRate", zj, 0);
                alx.bulletSpeedRate = MapleDataTool.getInt("BulletSpeedRate", zj, 0);
                spiral.add(alx);
            }
            for (MapleData zj : shootInfo.getChildByPath("bidirection")) {
                alx = new ALX();
                alx.shotAngleRate = MapleDataTool.getInt("ShotAngleRate", zj, 0);
                alx.shotSpeed = MapleDataTool.getInt("ShotSpeed", zj, 0);
                alx.shotInterval = MapleDataTool.getInt("ShotInterval", zj, 0);
                alx.shotCount = MapleDataTool.getInt("ShotCount", zj, 0);
                bidirection.add(alx);
            }
            for (MapleData zj : shootInfo.getChildByPath("spiralButterfly")) {
                alx = new ALX();
                alx.shotAngle = MapleDataTool.getInt("ShotAngle", zj, 0);
                alx.shotAngleRate = MapleDataTool.getInt("ShotAngleRate", zj, 0);
                alx.shotAngleDiff = MapleDataTool.getInt("ShotAngleDiff", zj, 0);
                alx.shotSpeed = MapleDataTool.getInt("ShotSpeed", zj, 0);
                alx.shotInterval = MapleDataTool.getInt("ShotInterval", zj, 0);
                alx.shotCount = MapleDataTool.getInt("ShotCount", zj, 0);
                alx.bulletAngleRate = MapleDataTool.getInt("BulletAngleRate", zj, 0);
                alx.bulletSpeedRate = MapleDataTool.getInt("BulletSpeedRate", zj, 0);
                spiralButterfly.add(alx);
            }
        }
        catch (NullPointerException e) {
            log.error("[BossLucidField] wz/Etc.wz/BossLucid.img/Butterfly is not found.", e);
        }
    }

    public static ALX getSpiral(int n) {
        if (n >= spiral.size()) {
            return null;
        }
        return spiral.get(n);
    }

    public static ALX getSpiralButterFly(int n) {
        if (0 >= spiralButterfly.size()) {
            return null;
        }
        return spiralButterfly.get(0);
    }

    public static Point getPhasePos(boolean b, int n) {
        if (b && n < phase1_pos.size()) {
            return phase1_pos.get(n);
        }
        if (n < phase2_pos.size()) {
            return phase2_pos.get(n);
        }
        return new Point(0, 0);
    }

    public BossLucidField(int mapid, int channel, int returnMapId, float monsterRate) {
        super(mapid, channel, returnMapId, monsterRate);
    }

    public boolean isShowStep() {
        return this.showStep;
    }

    public void setShowStep(boolean showStep) {
        this.showStep = showStep;
    }

    @Override
    public final void actionButterfly(boolean b, int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Point c = new Point();
        switch (n) {
            case 3: {
                this.butterFlyCount.set(0);
                break;
            }
            case 2: {
                this.butterFlyCount.set(0);
                for (int i = 0; i < 40; ++i) {
                    if (this.getCharacters().isEmpty() || this.getCharactersSize() <= 0) continue;
                    list.add(this.getCharacters().get(Randomizer.nextInt(this.getCharactersSize())).getId());
                }
                break;
            }
            case 0: {
                c = BossLucidField.getPhasePos(b, this.butterFlyCount.getAndIncrement());
                if (this.butterFlyCount.get() != 5 && this.butterFlyCount.get() != 15 && this.butterFlyCount.get() != 25) break;
                this.changeHornState(true);
            }
        }
        this.actionHorn(true);
    }

    public final void onButterfly() {
        if (this.butterFlyCount.get() >= 40) {
            this.actionButterfly(this.getFieldType() == 147, 2);
            return;
        }
        this.actionButterfly(this.getFieldType() == 147, 0);
    }

    public final void actionHorn(boolean b) {
        if (b) {
            this.stats = 0;
            this.broadcastMessage(MobPacket.lucidSpecialHorn(false, this.stats, false));
            this.broadcastMessage(MobPacket.lucidSpecialHorn(true, this.stats + 1, false));
            this.actionButterfly(true, 3);
        }
    }

    public final void changeHornState(boolean b) {
        this.stats = Math.min(3, b ? this.getStats() + 1 : this.getStats() - 1);
        this.broadcastMessage(MobPacket.lucidSpecialHorn(true, this.stats, false));
    }

    public final void brokenSteps() {
        if (this.showStep) {
            String s = this.getLachelnList().get(Randomizer.nextInt(this.getLachelnList().size()));
            Point point = this.getLacheln().get(s);
            this.broadcastMessage(MobPacket.lucidFieldFoothold(this.showStep, this.getLachelnList()));
            if (Randomizer.isSuccess(40)) {
                this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8880192), point);
                List<String> list = Collections.singletonList(s);
                this.broadcastMessage(MobPacket.lucidFieldFootholdBreak(list));
            }
        }
    }

    public final int getStats() {
        return this.stats;
    }

    public final void setStats(int ait) {
        this.stats = ait;
    }

    public int getButterFlyCount() {
        return this.butterFlyCount.get();
    }

    public static final class ALX {
        public int shotAngle;
        public int shotAngleRate;
        public int shotAngleDiff;
        public int shotSpeed;
        public int shotInterval;
        public int shotCount;
        public int bulletAngleRate;
        public int bulletSpeedRate;
    }
}

