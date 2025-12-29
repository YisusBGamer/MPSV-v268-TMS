/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.provider.wz.WzHeader
 */
package Plugin.provider.wz.util;

import Plugin.provider.wz.WzHeader;
import tools.data.ByteStream;
import tools.data.MaplePacketReader;

public class WzLittleEndianAccessor
extends MaplePacketReader {
    public int hash;
    public WzHeader header;
    private static final byte[] encKey = null;

    public WzLittleEndianAccessor(ByteStream bs) {
        super(bs);
    }

    public WzLittleEndianAccessor(byte[] data) {
        super(data);
    }

    public String readStringAtOffset(long offset) {
        return this.readStringAtOffset(offset, false);
    }

    public String readStringAtOffset(long offset, boolean readByte) {
        long currentOffset = this.getPosition();
        this.seek(offset);
        if (readByte) {
            this.readByte();
        }
        String returnString = this.readString();
        this.seek(currentOffset);
        return returnString;
    }

    private String readString() {
        int smallLength = this.readByte();
        if (smallLength == 0) {
            return "";
        }
        StringBuilder retString = new StringBuilder();
        if (smallLength > 0) {
            int mask = 43690;
            int length = smallLength == 127 ? this.readInt() : smallLength;
            if (length <= 0) {
                return "";
            }
            for (int i = 0; i < length; ++i) {
                short encryptedChar = this.readShort();
                encryptedChar = (short)(encryptedChar ^ (short)mask);
                encryptedChar = (short)(encryptedChar ^ (short)(((encKey == null ? 0 : encKey[i * 2 + 1]) << 8) + (encKey == null ? 0 : encKey[i * 2])));
                retString.append((char)encryptedChar);
                ++mask;
            }
        } else {
            int mask = -86;
            int length = smallLength == -128 ? this.readInt() : -smallLength;
            if (length < 0) {
                return "";
            }
            for (int i = 0; i < length; ++i) {
                byte encryptedChar = this.readByte();
                encryptedChar = (byte)(encryptedChar ^ mask);
                encryptedChar = (byte)(encryptedChar ^ (encKey == null ? (byte)0 : encKey[i]));
                retString.append((char)encryptedChar);
                mask = (byte)(mask + 1);
            }
        }
        return retString.toString();
    }

    public int readCompressedInt() {
        byte sb = this.readByte();
        if (sb == -128) {
            return this.readInt();
        }
        return sb;
    }

    public long readLongValue() {
        byte b = this.readByte();
        if (b == -128) {
            return this.readLong();
        }
        return b;
    }

    public float readFloatValue() {
        byte b = this.readByte();
        if (b == -128) {
            return this.readFloat();
        }
        return 0.0f;
    }

    public long readOffset() {
        long offset = this.getPosition();
        offset = offset - (long)this.header.FStart ^ 0xFFFFFFFFFFFFFFFFL;
        offset *= (long)this.hash;
        int distance = (int)(offset -= 1478246253L) & 0x1F;
        offset = offset << distance | offset >> 32 - distance;
        long encryptedOffset = this.readUInt();
        offset ^= encryptedOffset;
        return offset += (long)this.header.FStart * 2L;
    }

    public String readStringBlock(long offset) {
        byte b = this.readByte();
        switch (b) {
            case 0: 
            case 3: 
            case 4: 
            case 115: {
                return this.readString();
            }
            case 1: 
            case 2: 
            case 27: {
                return this.readStringAtOffset((long)this.readInt() + offset);
            }
        }
        throw new RuntimeException("Unknown extension image identifier: " + b + " at offset " + (this.getPosition() - offset));
    }

    static {
        Object var0 = null;
    }
}

