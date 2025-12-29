/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

public class StructCrossHunterShop {
    private final int itemId;
    private final int tokenPrice;
    private final int potentialGrade;

    public StructCrossHunterShop(int itemId, int tokenPrice, int potentialGrade) {
        this.itemId = itemId;
        this.tokenPrice = tokenPrice;
        this.potentialGrade = potentialGrade;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getTokenPrice() {
        return this.tokenPrice;
    }

    public int getPotentialGrade() {
        return this.potentialGrade;
    }
}

