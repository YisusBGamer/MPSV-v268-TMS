/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

import Client.inventory.Equip;
import Client.inventory.FamiliarCard;
import Client.inventory.ItemAttribute;
import Client.inventory.MapleInventoryIdentifier;
import Client.inventory.MaplePet;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Net.server.MapleItemInformationProvider;
import Plugin.provider.loaders.StringData;
import SwordieX.util.FileTime;
import connection.OutPacket;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Item
implements Comparable<Item> {
    private static Logger log = LoggerFactory.getLogger("Item");
    private int itemID;
    private short position;
    private short quantity;
    private int attribute;
    private long expiration = -1L;
    private long inventoryitemid = -1L;
    private FamiliarCard familiarCard = null;
    private MaplePet pet = null;
    private int sn;
    private String owner = "";
    private String GameMaster_log = "";
    private String giftFrom = "";
    private int familiarid = 0;
    private short espos;
    private short extendSlot = (short)-1;
    private long uniqueid;

    public Item(int itemID, short position, short quantity, int attribute, int sn, short espos) {
        this.itemID = itemID;
        this.position = position;
        this.quantity = quantity;
        this.attribute = attribute;
        this.sn = sn;
    }

    public Item(int itemID, short position, short quantity, int attribute) {
        this.itemID = itemID;
        this.position = position;
        this.quantity = quantity;
        this.attribute = attribute;
        this.sn = -1;
    }

    public Item(int itemID, short position, short quantity) {
        this.itemID = itemID;
        this.position = position;
        this.quantity = quantity;
        this.sn = -1;
    }

    public Item copyWithQuantity(short quantitys) {
        Item ret = new Item(this.itemID, this.position, quantitys, this.attribute, this.sn, this.espos);
        ret.pet = this.pet;
        ret.owner = this.owner;
        ret.sn = this.sn;
        ret.GameMaster_log = this.GameMaster_log;
        ret.expiration = this.expiration;
        ret.giftFrom = this.giftFrom;
        ret.extendSlot = this.extendSlot;
        return ret;
    }

    public Item copy() {
        Item ret = new Item(this.itemID, this.position, this.quantity, this.attribute, this.sn, this.espos);
        ret.pet = this.pet;
        ret.owner = this.owner;
        ret.sn = this.sn;
        ret.GameMaster_log = this.GameMaster_log;
        ret.expiration = this.expiration;
        ret.giftFrom = this.giftFrom;
        ret.familiarCard = this.familiarCard;
        ret.familiarid = this.familiarid;
        ret.extendSlot = this.extendSlot;
        return ret;
    }

    public int getItemId() {
        return this.itemID;
    }

    public void setItemId(int id) {
        this.itemID = id;
    }

    public short getPosition() {
        return this.position;
    }

    public void setPosition(short position) {
        this.position = position;
        if (this.pet != null) {
            this.pet.setInventoryPosition(position);
        }
    }

    public int getAttribute() {
        return this.attribute;
    }

    public int getCAttribute() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int ret = this.attribute;
        if (ServerConfig.CAN_CUT_ITEMS_LIST.contains(this.itemID)) {
            if (!ItemAttribute.TradeOnce.check(ret)) {
                if (ii.isTradeBlock(this.itemID)) {
                    if (!ii.isTradeAvailable(this.itemID) && !ii.isPKarmaEnabled(this.itemID)) {
                        ret |= ItemAttribute.AnimaCube.getValue();
                    }
                } else if (ii.isEquipTradeBlock(this.itemID)) {
                    if (ItemAttribute.TradeBlock.check(ret)) {
                        ret |= ItemAttribute.AnimaCube.getValue();
                    }
                } else if (!ii.isTradeAvailable(this.itemID)) {
                    ret |= ItemAttribute.AnimaCube.getValue();
                    ret |= ItemAttribute.TradeBlock.getValue();
                }
            }
        } else if (ServerConfig.ACCOUNT_SHARE_ITEMS_LIST.contains(this.itemID)) {
            if (!(ItemAttribute.AccountSharable.check(ret) || ii.isAccountShared(this.itemID) || ii.isSharableOnce(this.itemID) || ii.isShareTagEnabled(this.itemID))) {
                ret |= ItemAttribute.AccountSharable.getValue();
                if (!ii.isTradeBlock(this.itemID)) {
                    ret |= ItemAttribute.TradeBlock.getValue();
                }
                if (!ii.isCash(this.itemID)) {
                    ret |= ItemAttribute.TradeOnce.getValue();
                }
            }
        } else if (this instanceof Equip && ((Equip)this).isMvpEquip()) {
            ret |= ItemAttribute.TradeBlock.getValue();
            ret |= ItemAttribute.TradeOnce.getValue();
        }
        return ret;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public short getQuantity() {
        return this.quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public byte getType() {
        return 2;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void removeAttribute(int flag) {
        this.attribute &= ~flag;
    }

    public void addAttribute(int flag) {
        this.attribute |= flag;
    }

    public long getTrueExpiration() {
        return this.expiration;
    }

    public long getExpiration() {
        return this.expiration <= 0L ? this.expiration : this.expiration / 1000L * 1000L;
    }

    public void setExpiration(long expire) {
        this.expiration = expire;
    }

    public String getGMLog() {
        return this.GameMaster_log;
    }

    public void setGMLog(String GameMaster_log) {
        this.GameMaster_log = GameMaster_log;
    }

    public int getFamiliarid() {
        return this.familiarid;
    }

    public void setFamiliarid(int familiarid) {
        this.familiarid = familiarid;
    }

    public FamiliarCard getFamiliarCard() {
        return this.familiarCard;
    }

    public void setFamiliarCard(FamiliarCard familiarCard) {
        this.familiarCard = familiarCard;
    }

    public boolean hasSetOnlyId() {
        return this.sn <= 0 && !MapleItemInformationProvider.getInstance().isCash(this.itemID) && this.itemID / 1000000 == 1;
    }

    public int getSN() {
        if (this.sn <= 0 && ItemConstants.類型.裝備(this.itemID)) {
            this.sn = MapleInventoryIdentifier.getInstance();
        }
        return this.sn;
    }

    public void setSN(int sn) {
        this.sn = sn;
    }

    public long getInventoryId() {
        return this.inventoryitemid;
    }

    public void setInventoryId(long ui) {
        this.inventoryitemid = ui;
    }

    public MaplePet getPet() {
        return this.pet;
    }

    public void setPet(MaplePet pet) {
        this.pet = pet;
        if (pet != null && this.sn != pet.getUniqueId() && pet.getUniqueId() > 0) {
            this.sn = pet.getUniqueId();
        }
    }

    public String getGiftFrom() {
        return this.giftFrom;
    }

    public void setGiftFrom(String gf) {
        this.giftFrom = gf;
    }

    public short getESPos() {
        return this.espos;
    }

    public void setESPos(short espos) {
        this.espos = espos;
    }

    public short getExtendSlot() {
        return this.extendSlot;
    }

    public void setExtendSlot(short extendSlot) {
        this.extendSlot = extendSlot;
    }

    public boolean isSkillSkin() {
        return this.itemID / 1000 == 1603;
    }

    @Override
    public int compareTo(Item other) {
        if (Math.abs(this.position) < Math.abs(other.getPosition())) {
            return -1;
        }
        if (Math.abs(this.position) == Math.abs(other.getPosition())) {
            return 0;
        }
        return 1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        Item ite = (Item)obj;
        return this.sn == ite.getSN() && this.itemID == ite.getItemId() && this.quantity == ite.getQuantity() && Math.abs(this.position) == Math.abs(ite.getPosition());
    }

    public String toString() {
        return "物品: " + StringData.getItemStringById(this.itemID) + "(" + this.itemID + ")[" + this.quantity + "個]";
    }

    public String getName() {
        return StringData.getItemStringById(this.itemID);
    }

    public final long getUniqueId() {
        return this.uniqueid;
    }

    public void setUniqueId(long ui) {
        this.uniqueid = ui;
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(this.getPet() != null ? (byte)3 : (byte)this.getType());
        if (this.getPet() == null && this.getType() == 2) {
            this.encodeBaseRaw(outPacket);
            outPacket.encodeShort(this.getQuantity());
            outPacket.encodeString(this.getOwner(), 15);
            outPacket.encodeShort(this.getAttribute());
            outPacket.encodeByte(0);
            if (ItemConstants.類型.飛鏢(this.getItemId()) || ItemConstants.類型.子彈(this.getItemId()) || this.getItemId() / 10000 == 287 || this.getItemId() == 4001886 || ItemConstants.isSetupExpRate(this.getItemId())) {
                outPacket.encodeLong(this.getSN());
            }
            outPacket.encodeInt(0);
            int familiarid = ItemConstants.getFamiliarByItemID(this.getItemId());
            FamiliarCard fc = this.getFamiliarCard();
            outPacket.encodeInt(familiarid);
            outPacket.encodeShort(familiarid > 0 && fc != null ? (short)fc.getLevel() : (short)1);
            outPacket.encodeShort(familiarid > 0 && fc != null ? fc.getSkill() : (short)0);
            outPacket.encodeShort(familiarid > 0 && fc != null ? (short)fc.getLevel() : (short)1);
            outPacket.encodeShort(familiarid > 0 && fc != null ? fc.getOption(0) : 0);
            outPacket.encodeShort(familiarid > 0 && fc != null ? fc.getOption(1) : 0);
            outPacket.encodeShort(familiarid > 0 && fc != null ? fc.getOption(2) : 0);
            outPacket.encodeByte(familiarid > 0 && fc != null ? fc.getGrade() : (byte)0);
        } else if (this.getPet() != null) {
            this.encodePetRaw(outPacket, this.getPet(), true);
        }
    }

    public boolean encodeBaseRaw(OutPacket outPacket) {
        int itemId = this.getItemId();
        outPacket.encodeInt(this.getItemId());
        boolean hasUniqueId = MapleItemInformationProvider.getInstance().isCash(itemId) && this.getSN() > 0 && !ItemConstants.類型.結婚戒指(itemId) && !ItemConstants.類型.機器人(itemId);
        outPacket.encodeByte(hasUniqueId ? (byte)1 : 0);
        if (hasUniqueId) {
            outPacket.encodeLong(this.getSN());
        }
        outPacket.encodeFT(ItemConstants.類型.寵物(itemId) ? FileTime.fromType(FileTime.Type.MAX_TIME) : FileTime.fromLong(this.getExpiration()));
        outPacket.encodeInt(this.getExtendSlot());
        outPacket.encodeByte((byte)1);
        return hasUniqueId;
    }

    public void encodePetRaw(OutPacket outPacket, MaplePet pet, boolean active) {
        this.encodeBaseRaw(outPacket);
        outPacket.encodeString(pet.getName(), 13);
        outPacket.encodeByte(pet.getLevel());
        outPacket.encodeShort(pet.getCloseness());
        outPacket.encodeByte(100);
        long timeNow = System.currentTimeMillis();
        long expiration = this.getExpiration();
        FileTime dateDead = expiration < 0L ? FileTime.fromType(FileTime.Type.PERMANENT) : (expiration <= timeNow ? FileTime.fromType(FileTime.Type.MAX_TIME) : FileTime.fromLong(expiration));
        outPacket.encodeFT(dateDead);
        outPacket.encodeShort(ItemAttribute.RegressScroll.check(this.getCAttribute()) ? ItemAttribute.RegressScroll.getValue() : 0);
        outPacket.encodeShort(pet.getFlags());
        outPacket.encodeInt(Math.max(pet.getSecondsLeft(), 0));
        short nAttribute = 0;
        if (ItemAttribute.TradeOnce.check(this.getCAttribute())) {
            nAttribute = (short)(nAttribute | 1);
        }
        if (!pet.isCanPickup()) {
            nAttribute = (short)(nAttribute | 2);
        }
        outPacket.encodeShort(nAttribute);
        outPacket.encodeByte(pet.getSummoned() ? pet.getSummonedValue() : (byte)0);
        outPacket.encodeInt(pet.getAddSkill());
        IntStream.range(0, pet.getBuffSkills().length).map(i -> active ? pet.getBuffSkill(i) : 0).forEach(outPacket::encodeInt);
        outPacket.encodeInt(-1);
        outPacket.encodeShort(100);
        outPacket.encodeShort(0);
        outPacket.encodeInt(0);
    }
}

