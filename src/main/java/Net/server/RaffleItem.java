/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

public class RaffleItem {
    private int id;
    private int period;
    private int itemId;
    private int quantity;
    private int chance;
    private int type;
    private boolean smega;
    private boolean allow;

    public RaffleItem(int id, int period, int itemId, int quantity, int chance, boolean smega, int type, boolean allow) {
        this.id = id;
        this.period = period;
        this.itemId = itemId;
        this.quantity = quantity;
        this.chance = chance;
        this.smega = smega;
        this.type = type;
        this.allow = allow;
    }

    public int getId() {
        return this.id;
    }

    public int getPeriod() {
        return this.period;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getChance() {
        return this.chance;
    }

    public boolean isSmega() {
        return this.smega;
    }

    public int getType() {
        return this.type;
    }

    public boolean isAllow() {
        return this.allow;
    }

    public void setId(int value) {
        this.id = value;
    }

    public void setPeriod(int value) {
        this.period = value;
    }

    public void setItemId(int value) {
        this.itemId = value;
    }

    public void setQuantity(int value) {
        this.quantity = value;
    }

    public void setChance(int value) {
        this.chance = value;
    }

    public void setSmega(boolean value) {
        this.smega = value;
    }

    public void setType(int value) {
        this.type = value;
    }

    public void setAllow(boolean value) {
        this.allow = value;
    }
}

