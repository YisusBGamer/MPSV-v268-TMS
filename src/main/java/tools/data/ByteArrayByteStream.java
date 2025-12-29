/*
 * Decompiled with CFR 0.152.
 */
package tools.data;

import java.io.IOException;
import tools.HexTool;
import tools.data.ByteStream;

public final class ByteArrayByteStream
implements ByteStream {
    private final byte[] arr;
    private int pos = 0;
    private long bytesRead = 0L;

    public ByteArrayByteStream(byte[] arr) {
        this.arr = arr;
    }

    @Override
    public long getPosition() {
        return this.pos;
    }

    @Override
    public void seek(long offset) throws IOException {
        this.pos = (int)offset;
    }

    @Override
    public long getBytesRead() {
        return this.bytesRead;
    }

    @Override
    public int readByte() {
        ++this.bytesRead;
        return this.arr[this.pos++] & 0xFF;
    }

    @Override
    public String toString() {
        return this.toString(false);
    }

    @Override
    public String toString(boolean b) {
        String nows = HexTool.toString(this.getNowBytes());
        StringBuilder ret = new StringBuilder();
        if (b) {
            ret.append("\r\n所有: ");
            ret.append(HexTool.toString(this.getBytes()));
            ret.append("\r\n現在: ");
            ret.append(nows);
            byte[] nowBytes = this.getNowBytes();
            if (nowBytes.length >= 2) {
                int len = nowBytes.length;
                short lastShort = (short)(nowBytes[len - 2] & 0xFF | (nowBytes[len - 1] & 0xFF) << 8);
                ret.append("\r\n錯誤包頭號碼: ").append(lastShort);
            }
            return ret.toString();
        }
        ret.append("\r\n封包: ").append(nows);
        return ret.toString();
    }

    @Override
    public long available() {
        return this.arr.length - this.pos;
    }

    public byte[] getBytes() {
        return this.arr;
    }

    public byte[] getNowBytes() {
        byte[] now = new byte[]{};
        if (this.arr.length - this.pos > 0) {
            now = new byte[this.arr.length - this.pos];
            System.arraycopy(this.arr, this.pos, now, 0, this.arr.length - this.pos);
        }
        return now;
    }
}

