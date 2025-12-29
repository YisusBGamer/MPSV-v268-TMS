/*
 * Decompiled with CFR 0.152.
 */
package Net.server.shop;

import Client.inventory.Item;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MapleShopItem {
    private int buyLimit;
    private int buyLimitWorldAccount;
    private int itemId;
    private long price;
    private int tokenItemID;
    private int tokenPrice;
    private int period;
    private int potentialGrade;
    private int category;
    private int minLevel;
    private int maxLevel;
    private Item rebuy;
    private int position;
    private int pointQuestID;
    private int pointPrice;
    private long sellStart;
    private long sellEnd;
    private long[] resetInfo;
    private short quantity;
    private short buyable;
    private byte resetType;

    private MapleShopItem() {
    }

    public MapleShopItem(Item rebuy, long price, short buyable) {
        this.itemId = rebuy.getItemId();
        this.price = price;
        this.tokenItemID = 0;
        this.tokenPrice = 0;
        this.period = 0;
        this.potentialGrade = 0;
        this.category = 0;
        this.minLevel = 0;
        this.maxLevel = 0;
        this.position = -1;
        this.rebuy = rebuy;
        this.pointQuestID = 0;
        this.pointPrice = 0;
        this.sellStart = -2L;
        this.sellEnd = -1L;
        this.buyLimit = 0;
        this.buyLimitWorldAccount = 0;
        this.quantity = buyable;
        this.buyable = buyable;
    }

    public MapleShopItem(int itemId, long price, int position, int tokenItemID, int tokenPrice, int pointQuestID, int pointPrice, int itemPeriod, int potentialGrade, int tabIndex, int minLevel, int maxLevel, long sellStart, long sellEnd, int buyLimit, int buyLimitWorldAccount, short buyable) {
        this.itemId = itemId;
        this.price = price;
        this.tokenItemID = tokenItemID;
        this.tokenPrice = tokenPrice;
        this.period = itemPeriod;
        this.potentialGrade = potentialGrade;
        this.category = tabIndex;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.position = position;
        this.rebuy = null;
        this.pointQuestID = pointQuestID;
        this.pointPrice = pointPrice;
        this.sellStart = sellStart;
        this.sellEnd = sellEnd;
        this.buyLimit = buyLimit;
        this.buyLimitWorldAccount = buyLimitWorldAccount;
        this.quantity = 1;
        this.buyable = buyable;
    }

    public int getBuyLimit() {
        return this.buyLimit;
    }

    public int getBuyLimitWorldAccount() {
        return this.buyLimitWorldAccount;
    }

    public int getItemId() {
        return this.itemId;
    }

    public long getPrice() {
        return this.price;
    }

    public int getTokenItemID() {
        return this.tokenItemID;
    }

    public int getTokenPrice() {
        return this.tokenPrice;
    }

    public int getCategory() {
        return this.category;
    }

    public int getPeriod() {
        return this.period;
    }

    public int getPotentialGrade() {
        return this.potentialGrade;
    }

    public Item getRebuy() {
        return this.rebuy;
    }

    public int getMinLevel() {
        return this.minLevel;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean isRechargeableItem() {
        int n = this.itemId / 10000;
        return n == 233 || n == 207;
    }

    public int getPointQuestID() {
        return this.pointQuestID;
    }

    public int getPointPrice() {
        return this.pointPrice;
    }

    public long getSellStart() {
        return this.sellStart;
    }

    public long getSellEnd() {
        return this.sellEnd;
    }

    public long[] getResetInfo() {
        return this.resetInfo;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity > 1 ? quantity : (short)1;
    }

    public short getQuantity() {
        return this.quantity;
    }

    public short getBuyable() {
        return this.buyable;
    }

    public void setResetInfo(List<Long> resetInfo) {
        this.resetInfo = resetInfo.stream().mapToLong(Long::longValue).toArray();
    }

    public static MapleShopItem createFromSql(ResultSet rs, int pos) throws SQLException {
        MapleShopItem item = new MapleShopItem();
        item.itemId = rs.getInt("nItemID");
        item.price = rs.getLong("nPrice");
        item.tokenItemID = rs.getInt("nTokenItemID");
        item.tokenPrice = rs.getInt("nTokenPrice");
        item.pointQuestID = rs.getInt("nPointQuestID");
        item.pointPrice = rs.getInt("nPointPrice");
        item.period = rs.getInt("nItemPeriod");
        item.potentialGrade = rs.getInt("nPotentialGrade");
        item.category = rs.getInt("nTabIndex");
        item.minLevel = rs.getShort("nLevelLimitedMin");
        item.maxLevel = rs.getShort("nLevelLimitedMax");
        item.buyLimit = rs.getInt("nBuyLimit");
        item.buyLimitWorldAccount = rs.getInt("nBuyLimitWorldAccount");
        item.sellStart = rs.getLong("ftSellStart");
        item.sellEnd = rs.getLong("ftSellEnd");
        item.setQuantity(rs.getShort("nQuantity"));
        item.position = pos;
        return item;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public byte getResetType() {
        return this.resetType;
    }

    public void setResetType(byte resetType) {
        this.resetType = resetType;
    }
}

