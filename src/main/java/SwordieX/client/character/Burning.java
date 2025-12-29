/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character;

import SwordieX.util.FileTime;
import lombok.Generated;

public class Burning {
    public static final int 無 = 0;
    public static final int 超級燃燒 = 1;
    public static final int 燃燒加速器 = 2;
    public static final int 極限燃燒 = 3;
    private FileTime startTime;
    private FileTime endTime;
    private int startLv;
    private int endLv;
    private int burningType;

    public Burning(int burningType, int startLv, int endLv, FileTime startTime, FileTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLv = startLv;
        this.endLv = endLv;
        this.burningType = burningType;
    }

    @Generated
    public void setStartTime(FileTime startTime) {
        this.startTime = startTime;
    }

    @Generated
    public void setEndTime(FileTime endTime) {
        this.endTime = endTime;
    }

    @Generated
    public void setStartLv(int startLv) {
        this.startLv = startLv;
    }

    @Generated
    public void setEndLv(int endLv) {
        this.endLv = endLv;
    }

    @Generated
    public void setBurningType(int burningType) {
        this.burningType = burningType;
    }

    @Generated
    public FileTime getStartTime() {
        return this.startTime;
    }

    @Generated
    public FileTime getEndTime() {
        return this.endTime;
    }

    @Generated
    public int getStartLv() {
        return this.startLv;
    }

    @Generated
    public int getEndLv() {
        return this.endLv;
    }

    @Generated
    public int getBurningType() {
        return this.burningType;
    }
}

