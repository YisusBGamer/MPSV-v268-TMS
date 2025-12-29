/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.util.Position
 *  SwordieX.util.Rect
 *  connection.Encodable
 */
package connection;

import Config.constants.ServerConstants;
import Opcode.header.OutHeader;
import SwordieX.util.FileTime;
import SwordieX.util.Position;
import SwordieX.util.Rect;
import SwordieX.util.Util;
import connection.Encodable;
import connection.Packet;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutPacket
extends Packet {
    private byte[] baos;
    private int baosPtr = 0;
    private boolean loopback = false;
    private final boolean encryptedByShanda = false;
    private short op;
    private static final Charset CHARSET = ServerConstants.MapleType.getByType(ServerConstants.MapleRegion).getCharset();
    private static final Logger log = LoggerFactory.getLogger(OutPacket.class);

    public OutPacket(short op) {
        super(new byte[0]);
        this.baos = new byte[16];
        this.encodeShort(op);
        this.op = op;
    }

    public OutPacket(int op) {
        this((short)op);
    }

    public OutPacket() {
        this(new byte[16]);
    }

    public OutPacket(byte[] data) {
        super(data);
        this.baos = data;
    }

    public OutPacket(OutHeader header) {
        this(header.getValue());
    }

    @Override
    public short getHeader() {
        return this.op;
    }

    public void encodeByte(int b) {
        this.encodeByte((byte)b);
    }

    public final void encodeZero(int i) {
        for (int x = 0; x < i; ++x) {
            this.encodeByte((byte)0);
        }
    }

    public void encodeByte(byte b) {
        if (this.baosPtr >= this.baos.length) {
            byte[] newBaos = new byte[this.baos.length * 2];
            System.arraycopy(this.baos, 0, newBaos, 0, this.baos.length);
            this.baos = newBaos;
        }
        this.baos[this.baosPtr++] = b;
    }

    public void encodeArr(byte[] bArr) {
        for (byte b : bArr) {
            this.encodeByte(b);
        }
    }

    public void encodeArr(String arr) {
        this.encodeArr(Util.getByteArrayByString(arr));
    }

    public void encodeChar(char c) {
        this.encodeByte(c);
    }

    public void encodeByte(boolean b) {
        this.encodeByte(b ? 1 : 0);
    }

    public void encodeBoolean(boolean b) {
        this.encodeByte(b ? 1 : 0);
    }

    public void encodeShort(short s) {
        this.encodeByte(s & 0xFF);
        this.encodeByte(s >>> 8 & 0xFF);
    }

    public void encodeShortBE(short s) {
        this.encodeByte(s >>> 8 & 0xFF);
        this.encodeByte(s & 0xFF);
    }

    public void encodeIntBE(int i) {
        this.encodeShort(i >>> 16 & 0xFFFF);
        this.encodeShort(i & 0xFFFF);
    }

    public void encodeInt(int i) {
        this.encodeShort(i & 0xFFFF);
        this.encodeShort(i >>> 16 & 0xFFFF);
    }

    public void encodeLong(long l) {
        this.encodeInt((int)(l & 0xFFFFFFFFL));
        this.encodeInt((int)(l >>> 32 & 0xFFFFFFFFL));
    }

    public void encodeString(String s) {
        if (s == null) {
            s = "";
        }
        if (s.length() > Short.MAX_VALUE) {
            log.error("Tried to encode a string that is too big.");
            return;
        }
        byte[] bs = s.getBytes(CHARSET);
        this.encodeShort((short)bs.length);
        this.encodeString(s, (short)bs.length);
    }

    public void encodeString(String s, short length) {
        byte[] bs;
        if (s == null) {
            s = "";
        }
        if ((bs = s.getBytes(CHARSET)).length > 0) {
            this.encodeArr(bs);
        }
        for (int i = bs.length; i < length; ++i) {
            this.encodeByte((byte)0);
        }
    }

    @Override
    public void setData(byte[] nD) {
        super.setData(nD);
        this.baos = nD;
    }

    @Override
    public byte[] getData() {
        byte[] retArr = new byte[this.baosPtr];
        System.arraycopy(this.baos, 0, retArr, 0, this.baosPtr);
        return retArr;
    }

    @Override
    public Packet clone() {
        return new OutPacket(this.getData());
    }

    @Override
    public int getLength() {
        return this.getData().length;
    }

    public boolean isLoopback() {
        return this.loopback;
    }

    public boolean isEncryptedByShanda() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s, %s/0x%s\t| %s", OutHeader.getOpcodeName(this.op), this.op, Integer.toHexString(this.op).toUpperCase(), Util.readableByteArray(Arrays.copyOfRange(this.getData(), 2, this.getData().length)));
    }

    public void encodeShort(int value) {
        this.encodeShort((short)value);
    }

    public void encodeString(String name, int length) {
        this.encodeString(name, (short)length);
    }

    public void encodeFT(FileTime fileTime) {
        if (fileTime == null) {
            this.encodeLong(0L);
        } else {
            fileTime.encode(this);
        }
    }

    public void encodePosition(Position position) {
        if (position != null) {
            this.encodeShort(position.getX());
            this.encodeShort(position.getY());
        } else {
            this.encodeShort(0);
            this.encodeShort(0);
        }
    }

    public void encodeRectInt(Rect rect) {
        this.encodeInt(rect.getLeft());
        this.encodeInt(rect.getTop());
        this.encodeInt(rect.getRight());
        this.encodeInt(rect.getBottom());
    }

    public void encodePositionInt(Position position) {
        this.encodeInt(position.getX());
        this.encodeInt(position.getY());
    }

    public void encodeFT(long currentTime) {
        this.encodeFT(new FileTime(currentTime));
    }

    public void encodeTime(boolean dynamicTerm, int time) {
        this.encodeByte(dynamicTerm);
        this.encodeInt(time);
    }

    public void encodeTime(int time) {
        this.encodeByte(false);
        this.encodeInt(time);
    }

    @Override
    public void release() {
    }

    public void encodeFT(LocalDateTime localDateTime) {
        this.encodeFT(FileTime.fromDate(localDateTime));
    }

    public void encodeZigZagVarints(int b) {
        b = b << 1 ^ b >> 31;
        this.encodeVarints(b);
    }

    public void encodeVarints(int b) {
        while (true) {
            if ((b & 0xFFFFFF80) == 0) break;
            this.encodeByte(b & 0x7F | 0x80);
            b >>= 7;
        }
        this.encodeByte(b);
    }

    public void encodeReversedVarints(int b) {
        while (true) {
            if ((b & 0xFFFFFF80) == 0) break;
            this.encodeByte((b & 0x7F) << 1 | 1);
            b >>= 7;
        }
        this.encodeByte(b << 1);
    }

    public void encode(Encodable encodable) {
        encodable.encode(this);
    }

    public void setLoopback(boolean loopback) {
        this.loopback = loopback;
    }
}

