/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character.keys;

import lombok.Generated;

public class Keymapping {
    private byte type;
    private int action;

    public Keymapping(byte type, int action) {
        this.type = type;
        this.action = action;
    }

    @Generated
    public void setType(byte type) {
        this.type = type;
    }

    @Generated
    public void setAction(int action) {
        this.action = action;
    }

    @Generated
    public byte getType() {
        return this.type;
    }

    @Generated
    public int getAction() {
        return this.action;
    }
}

