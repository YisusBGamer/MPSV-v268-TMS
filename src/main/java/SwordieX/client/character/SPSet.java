/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character;

import lombok.Generated;

public class SPSet {
    private byte jobLevel;
    private int sp;

    public SPSet() {
    }

    public SPSet(byte jobLevel, int sp) {
        this.jobLevel = jobLevel;
        this.sp = sp;
    }

    public void addSp(int sp) {
        this.setSp(this.getSp() + sp);
    }

    @Generated
    public void setJobLevel(byte jobLevel) {
        this.jobLevel = jobLevel;
    }

    @Generated
    public void setSp(int sp) {
        this.sp = sp;
    }

    @Generated
    public byte getJobLevel() {
        return this.jobLevel;
    }

    @Generated
    public int getSp() {
        return this.sp;
    }
}

