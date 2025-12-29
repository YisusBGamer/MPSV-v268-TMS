/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

public enum ItemAttribute {
    Seal(1),
    NonSlip(2),
    ColdProof(4),
    TradeBlock(8),
    TradeOnce(16),
    GetCharm(32),
    AndroidActivated(64),
    Crafted(128),
    NonCurse(256),
    LuckyChance(512),
    CutUsed(1024),
    Exclusive(2048),
    AccountSharable(4096),
    ProtectRUC(8192),
    ProtectScroll(16384),
    RegressScroll(32768),
    Hyalinize(0x20000000),
    AnimaCube(0x40000000);

    private final int i;

    private ItemAttribute(int i) {
        this.i = i;
    }

    public int getValue() {
        return this.i;
    }

    public boolean check(int flag) {
        return (flag & this.i) == this.i;
    }
}

