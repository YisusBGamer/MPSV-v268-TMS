/*
 * Decompiled with CFR 0.152.
 */
package Client.hexa;

import java.io.Serializable;

public class MapleHexaStat
implements Serializable {
    private int id;
    private int preset;
    private int solt;
    private int p0stat1;
    private int p0stat1lv;
    private int p0stat2;
    private int p0stat2lv;
    private int p0stat3;
    private int p0stat3lv;
    private int p1stat1;
    private int p1stat1lv;
    private int p1stat2;
    private int p1stat2lv;
    private int p1stat3;
    private int p1stat3lv;

    public MapleHexaStat(int solt, int perset) {
        this.solt = solt;
        this.preset = perset;
    }

    public void setStatPreset1(int stat1, int lv1, int stat2, int lv2, int stat3, int lv3) {
        this.p0stat1 = stat1;
        this.p0stat1lv = lv1;
        this.p0stat2 = stat2;
        this.p0stat2lv = lv2;
        this.p0stat3 = stat3;
        this.p0stat3lv = lv3;
    }

    public void setStatPreset2(int stat1, int lv1, int stat2, int lv2, int stat3, int lv3) {
        this.p1stat1 = stat1;
        this.p1stat1lv = lv1;
        this.p1stat2 = stat2;
        this.p1stat2lv = lv2;
        this.p1stat3 = stat3;
        this.p1stat3lv = lv3;
    }

    public int getPreset() {
        return this.preset;
    }

    public void setPreset(int preset) {
        this.preset = preset;
    }

    public int getSolt() {
        return this.solt;
    }

    public void setSolt(int solt) {
        this.solt = solt;
    }

    public int getMain0() {
        return this.p0stat1;
    }

    public int getMain0Lv() {
        return this.p0stat1lv;
    }

    public int getAddit0S1() {
        return this.p0stat2;
    }

    public int getAddit0S1Lv() {
        return this.p0stat2lv;
    }

    public int getAddit0S2() {
        return this.p0stat3;
    }

    public int getAddit0S2Lv() {
        return this.p0stat3lv;
    }

    public int getMain1() {
        return this.p1stat1;
    }

    public int getMain1Lv() {
        return this.p1stat1lv;
    }

    public int getAddit1S1() {
        return this.p1stat2;
    }

    public int getAddit1S1Lv() {
        return this.p1stat2lv;
    }

    public int getAddit1S2() {
        return this.p1stat3;
    }

    public int getAddit1S2Lv() {
        return this.p1stat3lv;
    }
}

