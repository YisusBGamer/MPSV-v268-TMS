/*
 * Decompiled with CFR 0.152.
 */
package Client;

public class MapleReward {
    public static final int 道具 = 1;
    public static final int 現金道具 = 2;
    public static final int 楓點 = 3;
    public static final int 楓幣 = 4;
    public static final int 經驗 = 5;
    private final int id;
    private int type;
    private int itemId;
    private long amount;
    private long from;
    private long to;
    private String desc;

    public MapleReward(int id, long start, long end, int type, long amount, int itemId, String desc) {
        this.id = id;
        this.from = start;
        this.to = end;
        this.type = type;
        this.amount = amount;
        this.itemId = itemId;
        this.desc = desc;
    }

    public void setFromDate(long from) {
        this.from = from;
    }

    public void setToDate(long to) {
        this.to = to;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return this.id;
    }

    public long getReceiveDate() {
        return this.from;
    }

    public long getExpireDate() {
        return this.to;
    }

    public int getType() {
        return this.type;
    }

    public long getAmount() {
        return this.amount;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getDesc() {
        return this.desc;
    }
}

