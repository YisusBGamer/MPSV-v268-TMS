/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Plugin.script.binding;

import Client.inventory.Equip;
import Client.inventory.FamiliarCard;
import Client.inventory.Item;
import Client.inventory.MaplePet;
import Config.constants.ItemConstants;
import Net.server.MapleItemInformationProvider;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptItem {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptItem.class);
    private Item item;

    public ScriptItem(Item item) {
        this.item = item;
    }

    public Item copy() {
        return this.getItem().copy();
    }

    public int getItemId() {
        return this.getItem().getItemId();
    }

    public void setItemId(int id) {
        this.getItem().setItemId(id);
    }

    public byte getItemType() {
        return ItemConstants.getInventoryType(this.getItem().getItemId()).getType();
    }

    public Equip asEquip() {
        return MapleItemInformationProvider.getInstance().getEquipById(this.getItem().getItemId());
    }

    public MaplePet asPet() {
        return this.getItem().getPet();
    }

    public FamiliarCard asFamiliarCard() {
        return this.getItem().getFamiliarCard();
    }

    public int getAttribute() {
        return this.getItem().getAttribute();
    }

    public void setAttribute(int attribute) {
        this.getItem().setAttribute(attribute);
    }

    public short getQuantity() {
        return this.getItem().getQuantity();
    }

    public void setQuantity(short quantity) {
        this.getItem().setQuantity(quantity);
    }

    public long getDateExpire() {
        return this.getItem().getExpiration();
    }

    public void setExpiration(long expire) {
        this.getItem().setExpiration(expire);
    }

    public int getSN() {
        return this.getItem().getSN();
    }

    public void setSN(int sn) {
        this.getItem().setSN(sn);
    }

    public long getInventoryId() {
        return this.getItem().getInventoryId();
    }

    public void setInventoryId(long ui) {
        this.getItem().setInventoryId(ui);
    }

    public boolean isTradeAvailable() {
        return MapleItemInformationProvider.getInstance().isTradeAvailable(this.getItem().getItemId());
    }

    public boolean isTradeBlock() {
        return MapleItemInformationProvider.getInstance().isTradeBlock(this.getItem().getItemId());
    }

    public boolean isAccountSharable() {
        return MapleItemInformationProvider.getInstance().isAccountShared(this.getItem().getItemId());
    }

    public String getItemName() {
        return this.getItem().getName();
    }

    public boolean isCash() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return ii.isCash(this.getItem().getItemId());
    }

    public int getOption(int pos) {
        Equip equip = (Equip)this.getItem();
        return equip.getPotential(pos, pos > 3);
    }

    public void setOption(int pos, int option) {
        Equip equip = (Equip)this.getItem();
        equip.setPotential(option, pos, pos > 3);
    }

    @Generated
    public Item getItem() {
        return this.item;
    }

    @Generated
    public void setItem(Item item) {
        this.item = item;
    }
}

