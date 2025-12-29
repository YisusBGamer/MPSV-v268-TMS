/*
 * Decompiled with CFR 0.152.
 */
package connection;

import SwordieX.util.Util;

public class Packet
implements Cloneable {
    private byte[] data;

    public Packet(byte[] data) {
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public int getLength() {
        if (this.data != null) {
            return this.data.length;
        }
        return 0;
    }

    public short getHeader() {
        if (this.data.length < 2) {
            return -1;
        }
        return (short)((this.data[0] & 0xFF) + ((this.data[1] & 0xFF) << 8));
    }

    public void setData(byte[] nD) {
        this.data = nD;
    }

    public byte[] getData() {
        return this.data;
    }

    public String toString() {
        if (this.data == null) {
            return "";
        }
        return "[Pck] | " + Util.readableByteArray(this.data);
    }

    public Packet clone() {
        return new Packet(this.data);
    }

    public void release() {
    }
}

