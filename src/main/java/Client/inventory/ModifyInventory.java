/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

import Client.inventory.Item;
import Config.constants.ItemConstants;

public class ModifyInventory {
    private final int mode;
    private Item item;
    private short oldPos;
    private short indicator;
    private boolean switchSrcDst = false;

    public ModifyInventory(int mode, Item item) {
        this.mode = mode;
        this.item = item.copy();
    }

    public ModifyInventory(int mode, Item item, short oldPos) {
        this.mode = mode;
        this.item = item.copy();
        this.oldPos = oldPos;
    }

    public ModifyInventory(int mode, Item item, short oldPos, short indicator, boolean switchSrcDst) {
        this.mode = mode;
        this.item = item.copy();
        this.oldPos = oldPos;
        this.indicator = indicator;
        this.switchSrcDst = switchSrcDst;
    }

    public int getMode() {
        if ((this.getInventoryType() == 2 || this.getInventoryType() == 3 || this.getInventoryType() == 4) && this.item.getPosition() > 10000) {
            switch (this.mode) {
                case 0: {
                    return 9;
                }
                case 1: {
                    return 6;
                }
                case 2: {
                    return 5;
                }
                case 3: {
                    return 7;
                }
            }
        }
        return this.mode;
    }

    public int getInventoryType() {
        return ItemConstants.getInventoryType(this.item.getItemId()).getType();
    }

    public short getPosition() {
        return this.item.getPosition();
    }

    public short getOldPosition() {
        return this.oldPos;
    }

    public short getIndicator() {
        return this.indicator;
    }

    public boolean switchSrcDst() {
        return this.switchSrcDst;
    }

    public short getQuantity() {
        return this.item.getQuantity();
    }

    public Item getItem() {
        return this.item;
    }

    public void clear() {
        this.item = null;
    }
}

