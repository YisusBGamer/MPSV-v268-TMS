/*
 * Decompiled with CFR 0.152.
 */
package tools.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import tools.data.ByteStream;

public class RandomAccessByteStream
implements ByteStream {
    private int pos = 0;
    private final RandomAccessFile raf;
    private long bytesRead = 0L;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public RandomAccessByteStream(RandomAccessFile raf) {
        this.raf = raf;
    }

    @Override
    public final long getPosition() {
        return this.pos;
    }

    @Override
    public final void seek(long offset) throws IOException {
        this.pos = (int)offset;
        this.raf.seek(offset);
    }

    @Override
    public long getBytesRead() {
        return this.bytesRead;
    }

    @Override
    public final int readByte() {
        try {
            byte temp = (byte)this.raf.read();
            ++this.pos;
            ++this.bytesRead;
            return temp & 0xFF;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final String toString(boolean b) {
        return this.toString();
    }

    @Override
    public final long available() {
        try {
            return this.raf.length() - this.raf.getFilePointer();
        }
        catch (IOException e) {
            System.err.println("ERROR" + String.valueOf(e));
            return 0L;
        }
    }

    public final byte[] toByteArray() throws IOException {
        int nRead;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[16384];
        while ((nRead = this.raf.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public final void write(int b) {
        try {
            this.raf.write(b);
            ++this.pos;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void write(byte[] b) {
        for (int x = 0; x < b.length; ++x) {
            this.write(b[x]);
        }
    }

    public final void writeShort(int i) {
        this.write((byte)(i & 0xFF));
        this.write((byte)(i >>> 8 & 0xFF));
    }

    public final void writeInt(int i) {
        this.write((byte)(i & 0xFF));
        this.write((byte)(i >>> 8 & 0xFF));
        this.write((byte)(i >>> 16 & 0xFF));
        this.write((byte)(i >>> 24 & 0xFF));
    }

    public final void writeAsciiString(String s) {
        this.write(s.getBytes(CHARSET));
    }

    public final void writeLong(long l) {
        this.write((byte)(l & 0xFFL));
        this.write((byte)(l >>> 8 & 0xFFL));
        this.write((byte)(l >>> 16 & 0xFFL));
        this.write((byte)(l >>> 24 & 0xFFL));
        this.write((byte)(l >>> 32 & 0xFFL));
        this.write((byte)(l >>> 40 & 0xFFL));
        this.write((byte)(l >>> 48 & 0xFFL));
        this.write((byte)(l >>> 56 & 0xFFL));
    }

    public final void close() throws IOException {
        this.raf.close();
    }
}

