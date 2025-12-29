
package tools.wzlib;

public class Ms_Entry {
    private String name;
    private int checkSum;
    private int flags;
    private long startPos;
    private int size;
    private int sizeAligned;
    private int unknown1;
    private int unknown2;
    private byte[] key;
    private int calculatedCheckSum;

    public Ms_Entry(String name, int checkSum, int flags, int startPos, int size, int sizeAligned, int unk1, int unk2, byte[] key) {
        /* 17*/
        this.name = name;
        /* 18*/
        this.checkSum = checkSum;
        /* 19*/
        this.flags = flags;
        /* 20*/
        this.startPos = startPos;
        /* 21*/
        this.size = size;
        /* 22*/
        this.sizeAligned = sizeAligned;
        /* 23*/
        this.unknown1 = unk1;
        /* 24*/
        this.unknown2 = unk2;
        /* 25*/
        this.key = key;
    }

    public String getName() {
        /* 29*/
        return this.name;
    }

    public void setName(String name) {
        /* 33*/
        this.name = name;
    }

    public int getCheckSum() {
        /* 37*/
        return this.checkSum;
    }

    public void setCheckSum(int checkSum) {
        /* 41*/
        this.checkSum = checkSum;
    }

    public int getFlags() {
        /* 45*/
        return this.flags;
    }

    public void setFlags(int flags) {
        /* 49*/
        this.flags = flags;
    }

    public long getStartPos() {
        /* 53*/
        return this.startPos;
    }

    public void setStartPos(long startPos) {
        /* 57*/
        this.startPos = startPos;
    }

    public int getSize() {
        /* 61*/
        return this.size;
    }

    public void setSize(int size) {
        /* 65*/
        this.size = size;
    }

    public int getSizeAligned() {
        /* 69*/
        return this.sizeAligned;
    }

    public void setSizeAligned(int sizeAligned) {
        /* 73*/
        this.sizeAligned = sizeAligned;
    }

    public int getUnknown1() {
        /* 77*/
        return this.unknown1;
    }

    public void setUnknown1(int unknown1) {
        /* 81*/
        this.unknown1 = unknown1;
    }

    public int getUnknown2() {
        /* 85*/
        return this.unknown2;
    }

    public void setUnknown2(int unknown2) {
        /* 89*/
        this.unknown2 = unknown2;
    }

    public byte[] getKey() {
        /* 93*/
        return this.key;
    }

    public void setKey(byte[] key) {
        /* 97*/
        this.key = key;
    }

    public int getCalculatedCheckSum() {
        /*101*/
        return this.calculatedCheckSum;
    }

    public void setCalculatedCheckSum(int calculatedCheckSum) {
        /*105*/
        this.calculatedCheckSum = calculatedCheckSum;
    }
}

