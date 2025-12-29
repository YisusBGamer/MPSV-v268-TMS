/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Net.server.maps.MapleFoothold;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MapleFootholdTree {
    private static final byte maxDepth = 8;
    private final List<MapleFoothold> footholds = new LinkedList<MapleFoothold>();
    private final Point p1;
    private final Point p2;
    private final Point center;
    private MapleFootholdTree nw = null;
    private MapleFootholdTree ne = null;
    private MapleFootholdTree sw = null;
    private MapleFootholdTree se = null;
    private int depth = 0;
    private int maxDropX;
    private int minDropX;

    public MapleFootholdTree(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    public MapleFootholdTree(Point p1, Point p2, int depth) {
        this.p1 = p1;
        this.p2 = p2;
        this.depth = depth;
        this.center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    public void insert(MapleFoothold f) {
        if (this.depth == 0) {
            if (f.getX1() > this.maxDropX) {
                this.maxDropX = f.getX1();
            }
            if (f.getX1() < this.minDropX) {
                this.minDropX = f.getX1();
            }
            if (f.getX2() > this.maxDropX) {
                this.maxDropX = f.getX2();
            }
            if (f.getX2() < this.minDropX) {
                this.minDropX = f.getX2();
            }
        }
        if (this.depth == 8 || f.getX1() >= this.p1.x && f.getX2() <= this.p2.x && f.getY1() >= this.p1.y && f.getY2() <= this.p2.y) {
            this.footholds.add(f);
        } else {
            if (this.nw == null) {
                this.nw = new MapleFootholdTree(this.p1, this.center, this.depth + 1);
                this.ne = new MapleFootholdTree(new Point(this.center.x, this.p1.y), new Point(this.p2.x, this.center.y), this.depth + 1);
                this.sw = new MapleFootholdTree(new Point(this.p1.x, this.center.y), new Point(this.center.x, this.p2.y), this.depth + 1);
                this.se = new MapleFootholdTree(this.center, this.p2, this.depth + 1);
            }
            if (f.getX2() <= this.center.x && f.getY2() <= this.center.y) {
                this.nw.insert(f);
            } else if (f.getX1() > this.center.x && f.getY2() <= this.center.y) {
                this.ne.insert(f);
            } else if (f.getX2() <= this.center.x && f.getY1() > this.center.y) {
                this.sw.insert(f);
            } else {
                this.se.insert(f);
            }
        }
    }

    public List<MapleFoothold> getAllRelevants() {
        return this.getAllRelevants(new LinkedList<MapleFoothold>());
    }

    private List<MapleFoothold> getAllRelevants(List<MapleFoothold> list) {
        list.addAll(this.footholds);
        if (this.nw != null) {
            this.nw.getAllRelevants(list);
            this.ne.getAllRelevants(list);
            this.sw.getAllRelevants(list);
            this.se.getAllRelevants(list);
        }
        return list;
    }

    private List<MapleFoothold> getRelevants(Point p) {
        return this.getRelevants(p, new LinkedList<MapleFoothold>());
    }

    private List<MapleFoothold> getRelevants(Point p, List<MapleFoothold> list) {
        list.addAll(this.footholds);
        if (this.nw != null) {
            if (p.x <= this.center.x && p.y <= this.center.y) {
                this.nw.getRelevants(p, list);
            } else if (p.x > this.center.x && p.y <= this.center.y) {
                this.ne.getRelevants(p, list);
            } else if (p.x <= this.center.x && p.y > this.center.y) {
                this.sw.getRelevants(p, list);
            } else {
                this.se.getRelevants(p, list);
            }
        }
        return list;
    }

    public MapleFoothold findBelow(Point p) {
        Point point = new Point(p.x, p.y - 1);
        List<MapleFoothold> relevants = this.getRelevants(point);
        LinkedList<MapleFoothold> xMatches = new LinkedList<MapleFoothold>();
        for (MapleFoothold fh : relevants) {
            if (fh.getX1() > point.x || fh.getX2() < point.x) continue;
            xMatches.add(fh);
        }
        Collections.sort(xMatches);
        for (MapleFoothold fh : xMatches) {
            if (fh.isWall()) continue;
            if (fh.getY1() != fh.getY2()) {
                double s1 = Math.abs(fh.getY2() - fh.getY1());
                double s2 = Math.abs(fh.getX2() - fh.getX1());
                double s4 = Math.abs(point.x - fh.getX1());
                double alpha = Math.atan(s2 / s1);
                double beta = Math.atan(s1 / s2);
                double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
                int calcY = fh.getY2() < fh.getY1() ? fh.getY1() - (int)s5 : fh.getY1() + (int)s5;
                if (calcY < point.y) continue;
                return fh;
            }
            if (fh.getY1() < point.y) continue;
            return fh;
        }
        return null;
    }
}

