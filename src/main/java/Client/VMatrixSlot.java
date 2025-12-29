/*
 * Decompiled with CFR 0.152.
 */
package Client;

public class VMatrixSlot {
    private int slot;
    private int index = -1;
    private int extend = 0;
    private int unlock = 0;

    public VMatrixSlot() {
    }

    public VMatrixSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getExtend() {
        return this.extend;
    }

    public void setExtend(int extend) {
        this.extend = extend;
    }

    public int getUnlock() {
        return this.unlock;
    }

    public void setUnlock(int unlock) {
        this.unlock = unlock;
    }
}

