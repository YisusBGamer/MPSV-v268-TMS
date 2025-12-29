/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.MapleNodes$DirectionInfo
 *  Net.server.maps.MapleNodes$Environment
 *  Net.server.maps.MapleNodes$MapleNodeInfo
 *  Net.server.maps.MapleNodes$MaplePlatform
 *  Net.server.maps.MapleNodes$MonsterPoint
 */
package Net.server.maps;

import Net.server.maps.MapleNodes;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.Pair;

public class MapleNodes {
    private final Map<Integer, MapleNodeInfo> nodes = new LinkedHashMap<Integer, MapleNodeInfo>();
    private final List<Rectangle> areas = new ArrayList<Rectangle>();
    private final List<MaplePlatform> platforms = new ArrayList<MaplePlatform>();
    private final List<MonsterPoint> monsterPoints;
    private final List<Integer> skillIds = new ArrayList<Integer>();
    private final List<Pair<Integer, Integer>> mobsToSpawn;
    private final List<Pair<Point, Integer>> guardiansToSpawn;
    private final List<Pair<String, Integer>> flags;
    private List<Environment> environments;
    private final List<DirectionInfo> directionInfo = new ArrayList<DirectionInfo>();
    private final int mapid;
    private int nodeStart = -1;
    private boolean firstHighest = true;
    private boolean show;
    private String name;

    public static class MapleNodeInfo {

        public final int node;
        public final int key;
        public final int x;
        public final int y;
        public final int attr;
        public final List<Integer> edge;
        public int nextNode = -1;
        public final MapleNodeStopInfo stopInfo;

        public MapleNodeInfo(int node, int key, int x, int y, int attr, List<Integer> edge, MapleNodeStopInfo stopInfo) {
            this.node = node;
            this.key = key;
            this.x = x;
            this.y = y;
            this.attr = attr;
            this.edge = edge;
            this.stopInfo = stopInfo;
        }
    }

    public static class DirectionInfo {

        public final int x;
        public final int y;
        public final int key;
        public final boolean forcedInput;
        public final List<String> eventQ = new ArrayList<>();

        public DirectionInfo(int key, int x, int y, boolean forcedInput) {
            this.key = key;
            this.x = x;
            this.y = y;
            this.forcedInput = forcedInput;
        }
    }

    public static class MaplePlatform {

        public final String name;
        public final int start;
        public final int speed;
        public final int x1;
        public final int y1;
        public final int x2;
        public final int y2;
        public final int r;
        public final List<Integer> SN;

        public MaplePlatform(String name, int start, int speed, int x1, int y1, int x2, int y2, int r, List<Integer> SN) {
            this.name = name;
            this.start = start;
            this.speed = speed;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.r = r;
            this.SN = SN;
        }
    }

    public static class MonsterPoint {

        public final int x;
        public final int y;
        public final int fh;
        public final int cy;
        public final int team;

        public MonsterPoint(int x, int y, int fh, int cy, int team) {
            this.x = x;
            this.y = y;
            this.fh = fh;
            this.cy = cy;
            this.team = team;
        }
    }

    public static class Environment {

        public boolean isShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        private int x, y;
        private boolean show;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static class MapleNodeStopInfo {
        public String PA;
        public int PB;
        public int PC;
        public int PD;
        public boolean PE;
        private final boolean PF;
        public boolean PG;
        public List<Pair<String, String>> PH = new ArrayList<>();


        public MapleNodeStopInfo(final String pa, final int n, final int pc, final int pd, final boolean pe, final boolean pf, final boolean pg, final List<Pair<String, String>> ph) {
            this.PA = pa;
            this.PB = n * 1000;
            this.PC = pc;
            this.PD = pd;
            this.PE = pe;
            this.PF = pf;
            this.PG = pg;
            this.PH = ph;
        }
    }

    public MapleNodes(int mapid) {
        this.monsterPoints = new ArrayList<MonsterPoint>();
        this.mobsToSpawn = new ArrayList<Pair<Integer, Integer>>();
        this.guardiansToSpawn = new ArrayList<Pair<Point, Integer>>();
        this.flags = new ArrayList<Pair<String, Integer>>();
        this.environments = new ArrayList<Environment>();
        this.mapid = mapid;
    }

    public void setNodeStart(int ns) {
        this.nodeStart = ns;
    }

    public boolean isShow() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void addDirection(int key, DirectionInfo d) {
        this.directionInfo.add(key, d);
    }

    public DirectionInfo getDirection(int key) {
        if (key >= this.directionInfo.size()) {
            return null;
        }
        return this.directionInfo.get(key);
    }

    public List<Pair<String, Integer>> getFlags() {
        return this.flags;
    }

    public void addFlag(Pair<String, Integer> f) {
        this.flags.add(f);
    }

    public void addNode(MapleNodeInfo mni) {
        this.nodes.put(mni.key, mni);
    }

    public Collection<MapleNodeInfo> getNodes() {
        return new ArrayList<MapleNodeInfo>(this.nodes.values());
    }

    public MapleNodeInfo getNode(int index) {
        int i = 1;
        for (MapleNodeInfo x : this.getNodes()) {
            if (i == index) {
                return x;
            }
            ++i;
        }
        return null;
    }

    public boolean isLastNode(int index) {
        return index == this.nodes.size();
    }

    private int getNextNode(MapleNodeInfo mni) {
        if (mni == null) {
            return -1;
        }
        this.addNode(mni);
        int ret = -1;
        Iterator iterator = mni.edge.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            if (this.nodes.containsKey(i)) continue;
            if (ret != -1 && (this.mapid / 100 == 9211204 || this.mapid / 100 == 9320001)) {
                if (!this.firstHighest) {
                    ret = Math.min(ret, i);
                    continue;
                }
                this.firstHighest = false;
                ret = Math.max(ret, i);
                break;
            }
            ret = i;
        }
        mni.nextNode = ret;
        return ret;
    }

    public void sortNodes() {
        if (this.nodes.size() <= 0 || this.nodeStart < 0) {
            return;
        }
        HashMap<Integer, MapleNodeInfo> unsortedNodes = new HashMap<Integer, MapleNodeInfo>(this.nodes);
        int nodeSize = unsortedNodes.size();
        this.nodes.clear();
        int nextNode = this.getNextNode((MapleNodeInfo)unsortedNodes.get(this.nodeStart));
        while (this.nodes.size() != nodeSize && nextNode >= 0) {
            nextNode = this.getNextNode((MapleNodeInfo)unsortedNodes.get(nextNode));
        }
    }

    public void addMapleArea(Rectangle rec) {
        this.areas.add(rec);
    }

    public List<Rectangle> getAreas() {
        return new ArrayList<Rectangle>(this.areas);
    }

    public Rectangle getArea(int index) {
        return this.getAreas().get(index);
    }

    public void addPlatform(MaplePlatform mp) {
        this.platforms.add(mp);
    }

    public List<MaplePlatform> getPlatforms() {
        return new ArrayList<MaplePlatform>(this.platforms);
    }

    public List<MonsterPoint> getMonsterPoints() {
        return this.monsterPoints;
    }

    public void addMonsterPoint(int x, int y, int fh, int cy, int team) {
        this.monsterPoints.add(new MonsterPoint(x, y, fh, cy, team));
    }

    public void addMobSpawn(int mobId, int spendCP) {
        this.mobsToSpawn.add(new Pair<Integer, Integer>(mobId, spendCP));
    }

    public List<Pair<Integer, Integer>> getMobsToSpawn() {
        return this.mobsToSpawn;
    }

    public void addGuardianSpawn(Point guardian, int team) {
        this.guardiansToSpawn.add(new Pair<Point, Integer>(guardian, team));
    }

    public List<Pair<Point, Integer>> getGuardians() {
        return this.guardiansToSpawn;
    }

    public List<Integer> getSkillIds() {
        return this.skillIds;
    }

    public void addSkillId(int z) {
        this.skillIds.add(z);
    }

    public List<Environment> getEnvironments() {
        return this.environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }
}

