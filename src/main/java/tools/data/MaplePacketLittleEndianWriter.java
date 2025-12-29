/*
 * Decompiled with CFR 0.152.
 */
package tools.data;

import Config.constants.ServerConstants;
import Opcode.header.OutHeader;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import tools.HexTool;
import tools.data.WritableIntValueHolder;

public final class MaplePacketLittleEndianWriter {
    private final ByteArrayOutputStream baos;
    private static final Charset CHARSET = ServerConstants.MapleType.getByType(ServerConstants.MapleRegion).getCharset();
    private static final Map<String, byte[]> HexMap = new HashMap<String, byte[]>();

    public MaplePacketLittleEndianWriter() {
        this(32);
    }

    public MaplePacketLittleEndianWriter(int size) {
        this.baos = new ByteArrayOutputStream(size);
    }

    public MaplePacketLittleEndianWriter(OutHeader opcode) {
        this(32);
        this.writeShort(opcode.getValue());
    }

    public void writeZeroBytes(int i) {
        for (int x = 0; x < i; ++x) {
            this.baos.write(0);
        }
    }

    public void write(byte[] b) {
        this.baos.write(b, 0, b.length);
    }

    public void write(byte b) {
        this.baos.write(b);
    }

    public void write(int b) {
        this.baos.write((byte)b);
    }

    public void write(boolean b) {
        this.baos.write((byte)(b ? 1 : 0));
    }

    public int writeShort(int i) {
        this.baos.write((byte)(i & 0xFF));
        this.baos.write((byte)(i >>> 8 & 0xFF));
        return i;
    }

    public int writeInt(int i) {
        this.baos.write((byte)(i & 0xFF));
        this.baos.write((byte)(i >>> 8 & 0xFF));
        this.baos.write((byte)(i >>> 16 & 0xFF));
        this.baos.write((byte)(i >>> 24 & 0xFF));
        return i;
    }

    public void writeInt(long n) {
        this.baos.write((byte)(n & 0xFFL));
        this.baos.write((byte)(n >>> 8 & 0xFFL));
        this.baos.write((byte)(n >>> 16 & 0xFFL));
        this.baos.write((byte)(n >>> 24 & 0xFFL));
    }

    public void writeReversedInt(long l) {
        this.baos.write((byte)(l >>> 32 & 0xFFL));
        this.baos.write((byte)(l >>> 40 & 0xFFL));
        this.baos.write((byte)(l >>> 48 & 0xFFL));
        this.baos.write((byte)(l >>> 56 & 0xFFL));
    }

    public void writeAsciiString(String s) {
        this.write(s.getBytes(CHARSET));
    }

    public void writeAsciiString(String s, int max) {
        if (s == null) {
            return;
        }
        byte[] bytes = s.getBytes(CHARSET);
        int len = Math.min(s.getBytes(CHARSET).length, max);
        this.write(bytes);
        for (int i = len; i < max; ++i) {
            this.write(0);
        }
    }

    public void writeMapleNameString(String s) {
        if (s.getBytes().length > 13) {
            s = s.substring(0, 13);
        }
        this.writeAsciiString(s);
        for (int x = s.getBytes().length; x < 13; ++x) {
            this.write(0);
        }
    }

    public void writeMapleAsciiString(String s) {
        if (s == null) {
            this.writeShort(0);
            return;
        }
        this.writeShort(s.getBytes(CHARSET).length);
        this.writeAsciiString(s);
    }

    public void writeMapleAsciiString(String s, int max) {
        this.writeShort(max);
        this.writeAsciiString(s, max);
    }

    public void writeMapleAsciiString(String[] arrstring) {
        int n2 = 0;
        for (String string : arrstring) {
            if (string == null) continue;
            n2 += string.getBytes(CHARSET).length;
        }
        if (n2 < 1) {
            this.writeShort(0);
            return;
        }
        this.writeShort((short)(n2 + arrstring.length - 1));
        for (int i = 0; i < arrstring.length; ++i) {
            if (arrstring[i] != null) {
                this.writeAsciiString(arrstring[i]);
            }
            if (i >= arrstring.length - 1) continue;
            this.write(0);
        }
    }

    public void writePos(Point s) {
        this.writeShort(s.x);
        this.writeShort(s.y);
    }

    public void writePosInt(Point s) {
        this.writeInt(s.x);
        this.writeInt(s.y);
    }

    public void writeRect(Rectangle s) {
        this.writeInt(s.x);
        this.writeInt(s.y);
        this.writeInt(s.x + s.width);
        this.writeInt(s.y + s.height);
    }

    public void writeLong(long l) {
        this.writeInt((int)l);
        this.writeReversedInt(l);
    }

    public void writeReversedLong(long l) {
        this.writeReversedInt(l);
        this.writeInt((int)l);
    }

    public void writeBool(boolean b) {
        this.write(b ? 1 : 0);
    }

    public void writeReversedBool(boolean b) {
        this.write(b ? 0 : 1);
    }

    public void writeDouble(double b) {
        this.writeLong(Double.doubleToLongBits(b));
    }

    public void writeHexString(String s) {
        this.write(HexMap.computeIfAbsent(s, k -> HexTool.getByteArrayFromHexString(s)));
    }

    public void writeOpcode(WritableIntValueHolder op) {
        this.writeShort(op.getValue());
    }

    public void writeFile(File file) {
        byte[] bytes = new byte[]{};
        if (file != null && file.exists()) {
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                System.err.println("檔案太大");
            } else {
                bytes = new byte[(int)length];
                try (FileInputStream is = new FileInputStream(file);){
                    int numRead = is.read(bytes);
                    this.writeInt(numRead);
                    this.write(bytes);
                }
                catch (IOException e) {
                    System.err.println("讀取檔案失敗:" + String.valueOf(e));
                }
            }
        } else {
            this.writeInt(0);
        }
    }

    public void writeZigZagVarints(int b) {
        b = b << 1 ^ b >> 31;
        this.writeVarints(b);
    }

    public void writeVarints(int b) {
        while (true) {
            if ((b & 0xFFFFFF80) == 0) break;
            this.write(b & 0x7F | 0x80);
            b >>= 7;
        }
        this.write(b);
    }

    public void writeReversedVarints(int b) {
        while (true) {
            if ((b & 0xFFFFFF80) == 0) break;
            this.write((b & 0x7F) << 1 | 1);
            b >>= 7;
        }
        this.write(b << 1);
    }

    public byte[] getPacket() {
        return this.baos.toByteArray();
    }

    public String toString() {
        return HexTool.toString(this.baos.toByteArray());
    }
}

