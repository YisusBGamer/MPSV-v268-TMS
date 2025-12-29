/*
 * Decompiled with CFR 0.152.
 */
package Net.server.reward;

public class RewardDropEntry {
    public int itemId;
    public int chance;
    public int quantity;
    public int msgType;
    public int period;
    public int state;

    public RewardDropEntry() {
    }

    public RewardDropEntry(int itemId, int chance, int quantity, int msgType, int period, int state) {
        this.itemId = itemId;
        this.chance = chance;
        this.quantity = quantity;
        this.msgType = msgType;
        this.period = period;
        this.state = state;
    }
}

