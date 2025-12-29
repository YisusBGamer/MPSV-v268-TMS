/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.util.Position
 *  SwordieX.util.Rect
 */
package connection;

import Config.constants.ServerConstants;
import SwordieX.util.Position;
import SwordieX.util.Rect;
import SwordieX.util.Util;
import connection.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import java.util.Arrays;
import tools.data.MaplePacketReader;

public class InPacket
extends Packet {
    private final ByteBuf byteBuf;
    private boolean loopback;
    private short packetID;
    private static final Charset CHARSET = ServerConstants.MapleType.getByType(ServerConstants.MapleRegion).getCharset();

    public InPacket(ByteBuf byteBuf) {
        super(byteBuf.array());
        this.byteBuf = byteBuf.copy();
    }

    public InPacket() {
        this(Unpooled.buffer());
    }

    public InPacket(byte[] data) {
        this(Unpooled.copiedBuffer(data));
    }

    @Override
    public int getLength() {
        return this.byteBuf.capacity();
    }

    @Override
    public byte[] getData() {
        return this.byteBuf.array();
    }

    @Override
    public InPacket clone() {
        return new InPacket(this.byteBuf);
    }

    public byte decodeByte() {
        return this.byteBuf.readByte();
    }

    public short decodeUByte() {
        return this.byteBuf.readUnsignedByte();
    }

    public boolean decodeBoolean() {
        return this.byteBuf.readBoolean();
    }

    public byte[] decodeArr(int amount) {
        byte[] arr = new byte[amount];
        if (amount > this.byteBuf.readableBytes()) {
            throw new IndexOutOfBoundsException("緩衝區中沒有足夠的位元組來讀取.");
        }
        for (int i = 0; i < amount; ++i) {
            arr[i] = this.byteBuf.readByte();
        }
        return arr;
    }

    public int decodeInt() {
        return this.byteBuf.readIntLE();
    }

    public short decodeShort() {
        return this.byteBuf.readShortLE();
    }

    public String decodeString(int amount) {
        return new String(this.decodeArr(amount), CHARSET);
    }

    public String decodeString() {
        short amount = this.decodeShort();
        if (amount > this.byteBuf.readableBytes()) {
            throw new IndexOutOfBoundsException("緩衝區中沒有足夠的位元組來讀取.");
        }
        return this.decodeString(amount);
    }

    public byte[] decodeRawString() {
        short amount = this.decodeShort();
        if (amount > this.byteBuf.readableBytes()) {
            throw new IndexOutOfBoundsException("緩衝區中沒有足夠的位元組來讀取.");
        }
        return this.decodeArr(amount);
    }

    @Override
    public String toString() {
        return Util.readableByteArray(Arrays.copyOfRange(this.getData(), this.getData().length - this.getUnreadAmount(), this.getData().length));
    }

    public String toString(int length) {
        return Util.readableByteArray(Arrays.copyOfRange(this.getData(), length, this.getData().length));
    }

    public long decodeLong() {
        return this.byteBuf.readLongLE();
    }

    public Position decodePosition() {
        return new Position((int)this.decodeShort(), (int)this.decodeShort());
    }

    public Position decodePositionInt() {
        return new Position(this.decodeInt(), this.decodeInt());
    }

    public Rect decodeShortRect() {
        return new Rect(this.decodePosition(), this.decodePosition());
    }

    public Rect decodeIntRect() {
        return new Rect(this.decodePositionInt(), this.decodePositionInt());
    }

    public int decodeZigZagVarints() {
        int n = this.decodeVarints();
        return n >> 1 ^ -(n & 1);
    }

    public int decodeVarints() {
        int ret = 0;
        int offset = 0;
        byte n;
        while (((n = this.decodeByte()) & 0x80) == 128) {
            ret |= (n & 0x7F) << offset;
            offset += 7;
        }
        return ret |= n << offset;
    }

    public int decodeReversedVarints() {
        int ret = 0;
        int offset = 0;
        byte n;
        while (((n = this.decodeByte()) & 1) == 1) {
            ret |= (n & 0xFE) >> 1 << offset;
            offset += 7;
        }
        return ret |= n >> 1 << offset;
    }

    public int getUnreadAmount() {
        return this.byteBuf.readableBytes();
    }

    @Override
    public void release() {
        this.byteBuf.release();
    }

    public int readerIndex() {
        return this.byteBuf.readerIndex();
    }

    public void readerIndex(int var1) {
        this.byteBuf.readerIndex(var1);
    }

    public MaplePacketReader toPacketReader() {
        MaplePacketReader reader = new MaplePacketReader(this.byteBuf.array());
        reader.seek(this.readerIndex());
        return reader;
    }

    public boolean isLoopback() {
        return this.loopback;
    }

    public void setLoopback(boolean loopback) {
        this.loopback = loopback;
    }

    public short getPacketID() {
        return this.packetID;
    }
}

