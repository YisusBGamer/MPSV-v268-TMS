/*
 * Decompiled with CFR 0.152.
 */
package tools.data;

import Config.constants.ServerConstants;
import connection.InPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.ByteArrayByteStream;
import tools.data.ByteStream;

public class MaplePacketReader {
    private static final Logger log = LoggerFactory.getLogger(MaplePacketReader.class);
    private final ByteStream bs;
    private static final Charset CHARSET = ServerConstants.MapleType.getByType(ServerConstants.MapleRegion).getCharset();
    private short header;

    public MaplePacketReader(ByteStream bs) {
        this.bs = bs;
    }

    public MaplePacketReader(byte[] data) {
        this.bs = new ByteArrayByteStream(data);
    }

    public ByteStream getByteStream() {
        return this.bs;
    }

    public void seek(long offset) {
        try {
            this.bs.seek(offset);
        }
        catch (IOException e) {
            System.err.println("Seek failed" + String.valueOf(e));
        }
    }

    public long getPosition() {
        return this.bs.getPosition();
    }

    public void skip(int num) {
        this.seek(this.getPosition() + (long)num);
    }

    public short getHeader() {
        return this.header;
    }

    public void setHeader(short header) {
        this.header = header;
    }

    public int readByteAsInt() {
        return this.bs.readByte();
    }

    public byte readByte() {
        return (byte)this.bs.readByte();
    }

    public int readInt() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        int byte3 = this.bs.readByte();
        int byte4 = this.bs.readByte();
        return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    public final long readUInt() {
        int value = this.readInt();
        long value2 = 0L;
        if (value < 0) {
            value2 += 0x80000000L;
        }
        return (long)(value & Integer.MAX_VALUE) + value2;
    }

    public short readShort() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        return (short)((byte2 << 8) + byte1);
    }

    public int readUShort() {
        int value = this.readShort();
        if (value < 0) {
            value += 65536;
        }
        return value;
    }

    public char readChar() {
        return (char)this.readShort();
    }

    public long readLong() {
        long byte1 = this.bs.readByte();
        long byte2 = this.bs.readByte();
        long byte3 = this.bs.readByte();
        long byte4 = this.bs.readByte();
        long byte5 = this.bs.readByte();
        long byte6 = this.bs.readByte();
        long byte7 = this.bs.readByte();
        long byte8 = this.bs.readByte();
        return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    public float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }

    public String readAsciiString(int n) {
        byte[] ret = new byte[n];
        for (int x = 0; x < n; ++x) {
            ret[x] = this.readByte();
        }
        try {
            return new String(ret, CHARSET);
        }
        catch (Exception e) {
            log.error("readAsciiString", e);
            return "";
        }
    }

    public final String readNullTerminatedAsciiString() {
        byte b;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((b = this.readByte()) != 0) {
            baos.write(b);
        }
        byte[] buf = baos.toByteArray();
        char[] chrBuf = new char[buf.length];
        for (int x = 0; x < buf.length; ++x) {
            chrBuf[x] = (char)buf[x];
        }
        return String.valueOf(chrBuf);
    }

    public long getBytesRead() {
        return this.bs.getBytesRead();
    }

    public String readMapleAsciiString() {
        short size = this.readShort();
        if (size < 0) {
            throw new NegativeArraySizeException("readMapleAsciiString size=" + size);
        }
        return this.readAsciiString(size);
    }

    public Point readPos() {
        short x = this.readShort();
        short y = this.readShort();
        return new Point(x, y);
    }

    public Point readPosInt() {
        int x = this.readInt();
        int y = this.readInt();
        return new Point(x, y);
    }

    public Rectangle readRect() {
        int x = this.readInt();
        int y = this.readInt();
        return new Rectangle(x, y, this.readInt() - x, this.readInt() - y);
    }

    public int readZigZagVarints() {
        int n = this.readVarints();
        return n >> 1 ^ -(n & 1);
    }

    public int readVarints() {
        int ret = 0;
        int offset = 0;
        int n;
        while (((n = this.readByteAsInt()) & 0x80) == 128) {
            ret |= (n & 0x7F) << offset;
            offset += 7;
        }
        return ret |= n << offset;
    }

    public int readReversedVarints() {
        int ret = 0;
        int offset = 0;
        int n;
        while (((n = this.readByteAsInt()) & 1) == 1) {
            ret |= (n & 0xFE) >> 1 << offset;
            offset += 7;
        }
        return ret |= n >> 1 << offset;
    }

    public byte[] read(int num) {
        byte[] ret = new byte[num];
        for (int x = 0; x < num; ++x) {
            ret[x] = this.readByte();
        }
        return ret;
    }

    public long available() {
        return this.bs.available();
    }

    public InPacket toInPacket() {
        ByteStream byteStream = this.bs;
        if (byteStream instanceof ByteArrayByteStream) {
            ByteArrayByteStream babs = (ByteArrayByteStream)byteStream;
            InPacket inPacket = new InPacket(babs.getBytes());
            inPacket.readerIndex((int)babs.getPosition());
            return inPacket;
        }
        return null;
    }

    public String toString() {
        return this.bs.toString();
    }

    public String toString(boolean b) {
        return this.bs.toString(b);
    }

    public boolean readBool() {
        return this.readByte() > 0;
    }
}

