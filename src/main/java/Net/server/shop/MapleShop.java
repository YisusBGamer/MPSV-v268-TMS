/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.shop.MapleShopResponse
 *  Net.server.shop.NpcShopBuyLimit
 *  tools.FileoutputUtil
 */
package Net.server.shop;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Client.inventory.ModifyInventory;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Net.server.AutobanManager;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.buffs.MapleStatEffect;
import Net.server.shop.MapleShopItem;
import Net.server.shop.MapleShopResponse;
import Net.server.shop.NpcShopBuyLimit;
import Packet.InventoryPacket;
import Packet.NPCPacket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import tools.DateUtil;
import tools.FileoutputUtil;
import tools.Pair;

public class MapleShop {
    public static final Pattern LOG_PATTERN = Pattern.compile("^商店購買 (\\d+), .+$");
    private final int id;
    private final int npcId;
    private final List<MapleShopItem> items;
    private final List<Pair<Integer, String>> ranks = new ArrayList<Pair<Integer, String>>();
    private int shopItemId;

    public MapleShop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
        this.shopItemId = 0;
        this.items = new ArrayList<MapleShopItem>();
    }

    public void addItem(MapleShopItem item) {
        this.items.add(item);
    }

    public void removeItem(MapleShopItem item) {
        this.items.remove(item);
    }

    public List<MapleShopItem> getItems() {
        return this.items;
    }

    public List<MapleShopItem> getItems(MapleClient c) {
        ArrayList<MapleShopItem> itemsPlusRebuy = new ArrayList<MapleShopItem>(this.items);
        if (c.getPlayer().isScriptShop()) {
            for (MapleShopItem item : this.items) {
                if (!item.isRechargeableItem() || item.getPrice() != 0L) continue;
                itemsPlusRebuy.remove(item);
            }
        }
        int i = 0;
        for (MapleShopItem si : c.getPlayer().getRebuy()) {
            if (i >= 10) break;
            itemsPlusRebuy.add(si);
            ++i;
        }
        return itemsPlusRebuy;
    }

    public final int getBuyLimitItemIndex(int itemId) {
        for (MapleShopItem item : this.items) {
            if (item.isRechargeableItem() && item.getPrice() == 0L || item.getItemId() != itemId || item.getBuyLimit() <= 0 && item.getBuyLimitWorldAccount() <= 0) continue;
            return this.items.indexOf(item);
        }
        return -1;
    }

    public void sendShop(MapleClient c) {
        this.sendShop(c, false);
    }

    public void sendShop(MapleClient c, boolean fromScript) {
        this.sendShop(c, this.getNpcId(), fromScript);
    }

    public void sendShop(MapleClient c, int customNpc) {
        this.sendShop(c, customNpc, false);
    }

    public void sendShop(MapleClient c, int customNpc, boolean fromScript) {
        List mu;
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropDebugMessage(0, "[開啟" + (fromScript ? "腳本" : "") + "商店] 商店ID: " + this.id + " 商店NPC: " + customNpc);
        }
        c.getPlayer().setConversation(3);
        c.getPlayer().setShop(this);
        c.getPlayer().setScriptShop(fromScript);
        NpcShopBuyLimit buyLimit = c.getPlayer().getBuyLimit().get(this.id);
        if (buyLimit == null) {
            buyLimit = c.getPlayer().getAccountBuyLimit().get(this.id);
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (buyLimit != null && !(mu = buyLimit.getInfo()).isEmpty()) {
            Iterator iterator = mu.iterator();
            while (iterator.hasNext()) {
                int aMu = (Integer)iterator.next();
                int n = this.getBuyLimitItemIndex(aMu);
                if (n == -1) continue;
                list.add(n);
            }
            c.getPlayer().checkBuyLimit();
            c.announce(NPCPacket.ResetBuyLimitCount(this.id, list));
        }
        c.announce(NPCPacket.getNPCShop(customNpc, this, c));
    }

    public void sendItemShop(MapleClient c, int itemId) {
        this.shopItemId = itemId;
        this.sendShop(c);
    }

    public String getItemName(int itemId) {
        return MapleItemInformationProvider.getInstance().getName(itemId);
    }

    public void buy(MapleClient c, int itemId, short quantity, short position) {
        MapleCharacter player = c.getPlayer();
        MapleShopResponse response = MapleShopResponse.ShopRes_BuySuccess;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleShopItem shopItem = this.findBySlotAndId(c, itemId, position);
        long time = System.currentTimeMillis();
        int index = -1;
        if (quantity <= 0 || shopItem == null) {
            response = MapleShopResponse.ShopRes_BuyNoStock;
            c.announce(NPCPacket.confirmShopTransaction(response, this, c, index, -1));
            return;
        }
        if (shopItem.getSellStart() != -2L && shopItem.getSellStart() > time || shopItem.getSellEnd() != -1L && shopItem.getSellEnd() < time) {
            response = MapleShopResponse.ShopRes_BuyInvalidTime;
        } else if (c.getPlayer().getLevel() < shopItem.getMinLevel()) {
            response = MapleShopResponse.ShopRes_LimitLevel_More;
            index = shopItem.getMinLevel();
        } else if (shopItem.getMaxLevel() > 0 && c.getPlayer().getLevel() > shopItem.getMaxLevel()) {
            response = MapleShopResponse.ShopRes_LimitLevel_Less;
            index = shopItem.getMaxLevel();
        } else if (shopItem.getRebuy() != null) {
            if (c.getPlayer().getRebuy().isEmpty()) {
                c.sendEnableActions();
                return;
            }
            if (shopItem.getRebuy() == null) {
                response = MapleShopResponse.ShopRes_BuyNoStock;
            } else {
                long price = shopItem.getPrice();
                if (price > 0L && c.getPlayer().getMeso() < price) {
                    response = MapleShopResponse.ShopRes_BuyNoMoney;
                } else if (!MapleInventoryManipulator.checkSpace(c, itemId, quantity * shopItem.getQuantity(), "")) {
                    response = MapleShopResponse.ShopRes_NotEnoughSpace;
                } else {
                    index = c.getPlayer().getRebuy().indexOf(shopItem);
                    c.getPlayer().gainMeso(-price, false);
                    MapleInventoryManipulator.addbyItem(c, shopItem.getRebuy());
                    c.getPlayer().getRebuy().remove(shopItem);
                }
            }
        } else if (shopItem.getTokenItemID() > 0 && !player.haveItem(shopItem.getTokenItemID(), shopItem.getTokenPrice() * quantity)) {
            response = MapleShopResponse.ShopRes_BuyNoToken;
        } else if (shopItem.getPointPrice() > 0 && player.getQuestPoint(shopItem.getPointQuestID()) < shopItem.getPointPrice() * quantity) {
            response = MapleShopResponse.ShopRes_BuyNoPoint;
        } else if (shopItem.getPrice() > 0L && player.getMeso() < shopItem.getPrice() * (long)quantity) {
            response = MapleShopResponse.ShopRes_BuyNoMoney;
        } else if (shopItem.getBuyLimit() > 0 && player.getBuyLimit(this.id, itemId) + quantity > shopItem.getBuyLimit()) {
            response = MapleShopResponse.ShopRes_BuyNoStock;
        } else if (shopItem.getBuyLimitWorldAccount() > 0 && player.getAccountBuyLimit(this.id, itemId) + quantity > shopItem.getBuyLimitWorldAccount()) {
            response = MapleShopResponse.ShopRes_BuyNoStock;
        } else if (!MapleInventoryManipulator.checkSpace(c, itemId, quantity * shopItem.getQuantity(), "")) {
            response = MapleShopResponse.ShopRes_NotEnoughSpace;
        } else {
            if (shopItem.getCategory() >= 0) {
                boolean passed = true;
                int y = 0;
                for (Pair<Integer, String> i : this.getRanks()) {
                    if (c.getPlayer().haveItem((Integer)i.left, 1, true, true) && shopItem.getCategory() >= y) {
                        passed = true;
                        break;
                    }
                    ++y;
                }
                if (!passed) {
                    c.getPlayer().dropMessage(1, "You need a higher rank.");
                    c.sendEnableActions();
                    return;
                }
            }
            if (shopItem.getTokenItemID() > 0) {
                MapleInventoryManipulator.removeById(c, ItemConstants.getInventoryType(shopItem.getTokenItemID()), shopItem.getTokenItemID(), ItemConstants.類型.可充值道具(itemId) ? shopItem.getTokenPrice() : shopItem.getTokenPrice() * quantity, true, false);
                FileoutputUtil.logToFile((String)"logs/Data/代幣購買.txt", (String)("時間: " + FileoutputUtil.NowTime2() + " 帳號: " + c.getAccountName() + " 消耗: " + shopItem.getTokenPrice() * quantity + "個 " + this.getItemName(shopItem.getTokenItemID()) + shopItem.getTokenItemID() + "購買: " + quantity + "個 " + this.getItemName(itemId) + itemId + "\r\n"));
            }
            if (shopItem.getPointPrice() > 0) {
                int price;
                int n = price = ItemConstants.類型.可充值道具(itemId) ? shopItem.getPointPrice() : shopItem.getPointPrice() * quantity;
                if (player.getWorldShareInfo(shopItem.getPointQuestID(), "point") != null) {
                    player.gainWorldShareQuestPoint(shopItem.getPointQuestID(), -price);
                } else {
                    player.gainQuestPoint(shopItem.getPointQuestID(), -price);
                }
            }
            if (shopItem.getPrice() > 0L) {
                player.gainMeso(-(ItemConstants.類型.可充值道具(itemId) ? shopItem.getPrice() : shopItem.getPrice() * (long)quantity), false);
                FileoutputUtil.logToFile((String)"logs/Data/商店購買.txt", (String)("時間: " + FileoutputUtil.NowTime2() + " 帳號: " + c.getAccountName() + " 消耗楓幣: " + shopItem.getPrice() + "元 購買: " + quantity + "個 " + this.getItemName(itemId) + itemId + "\r\n"));
            }
            if (shopItem.getBuyLimit() > 0 || shopItem.getBuyLimitWorldAccount() > 0) {
                long resetTime = 0L;
                if (shopItem.getResetType() == 2) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(5, 1);
                    cal.set(11, 0);
                    cal.set(12, 0);
                    cal.set(13, 0);
                    cal.set(14, 0);
                    resetTime = cal.getTimeInMillis();
                } else if (shopItem.getResetType() == 3) {
                    long timeNow = System.currentTimeMillis();
                    for (long timeRS : shopItem.getResetInfo()) {
                        if (timeRS <= timeNow) continue;
                        resetTime = timeRS;
                        break;
                    }
                }
                if (shopItem.getBuyLimit() > 0) {
                    player.setBuyLimit(this.id, itemId, quantity, resetTime);
                    int buyLimit = player.getBuyLimit(this.id, itemId);
                    c.announce(NPCPacket.SetBuyLimitCount(this.id, position, itemId, buyLimit, resetTime));
                }
                if (shopItem.getBuyLimitWorldAccount() > 0) {
                    player.setAccountBuyLimit(this.id, itemId, quantity, resetTime);
                    int buyLimit = player.getAccountBuyLimit(this.id, itemId);
                    c.announce(NPCPacket.SetBuyLimitCount(this.id, position, itemId, buyLimit, resetTime));
                }
            }
            if (ItemConstants.類型.可充值道具(itemId)) {
                quantity = ii.getSlotMax(shopItem.getItemId());
            }
            MapleInventoryManipulator.addById(c, itemId, quantity * shopItem.getQuantity(), shopItem.getPeriod(), shopItem.getPotentialGrade(), "商店購買 " + this.id + ", " + this.npcId + " 時間 " + DateUtil.getCurrentDate());
        }
        c.announce(NPCPacket.confirmShopTransaction(response, this, c, index, itemId));
    }

    public void sell(MapleClient c, MapleInventoryType type, short slot, short quantity) {
        Item item;
        if (c.getPlayer().isScriptShop()) {
            c.getPlayer().dropMessage(1, "這個商店無法出售道具。");
            c.announce(NPCPacket.confirmShopTransaction(MapleShopResponse.ShopRes_SellSuccess, this, c, -1, -1));
            return;
        }
        if (quantity <= 0) {
            quantity = 1;
        }
        if ((item = c.getPlayer().getInventory(type).getItem(slot)) == null) {
            return;
        }
        if (ItemConstants.類型.飛鏢(item.getItemId()) || ItemConstants.類型.子彈(item.getItemId())) {
            quantity = item.getQuantity();
        }
        if (item.getItemId() == 4000463) {
            c.getPlayer().dropMessage(1, "該道具無法賣出.");
            c.announce(NPCPacket.confirmShopTransaction(MapleShopResponse.ShopRes_SellSuccess, this, c, -1, -1));
            return;
        }
        if (quantity < 0) {
            AutobanManager.getInstance().addPoints(c, 1000, 0L, "賣出道具 " + quantity + " " + item.getItemId() + " (" + type.name() + "/" + slot + ")");
            return;
        }
        if (ItemConstants.類型.可充值道具(item.getItemId())) {
            quantity = item.getQuantity();
        }
        short iQuant = item.getQuantity();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.cantSell(item.getItemId()) || ItemConstants.類型.寵物(item.getItemId())) {
            return;
        }
        if (quantity <= iQuant && (iQuant > 0 || ItemConstants.類型.可充值道具(item.getItemId()))) {
            double price = ItemConstants.類型.飛鏢(item.getItemId()) || ItemConstants.類型.子彈(item.getItemId()) ? ii.getUnitPrice(item.getItemId()) / (double)ii.getSlotMax(item.getItemId()) : (double)ii.getPrice(item.getItemId());
            long recvMesos = (long)Math.ceil(price * (double)quantity);
            if (c.getPlayer().getMeso() + recvMesos > ServerConfig.CHANNEL_PLAYER_MAXMESO) {
                c.getPlayer().dropMessage(1, "攜帶楓幣不能超過" + ServerConfig.CHANNEL_PLAYER_MAXMESO + ".");
                c.announce(NPCPacket.confirmShopTransaction(MapleShopResponse.ShopRes_SellSuccess, this, c, -1, -1));
                return;
            }
            List<MapleShopItem> rebuy = c.getPlayer().getRebuy();
            if (item.getQuantity() == quantity) {
                rebuy.add(new MapleShopItem(item.copy(), recvMesos, item.getQuantity()));
            } else {
                rebuy.add(new MapleShopItem(item.copyWithQuantity(quantity), recvMesos, quantity));
            }
            if (rebuy.size() > 10) {
                rebuy.remove(0);
            }
            MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
            if (price != -1.0 && recvMesos > 0L) {
                c.getPlayer().gainMeso(recvMesos, false);
            }
            c.announce(NPCPacket.confirmShopTransaction(MapleShopResponse.ShopRes_SellSuccess, this, c, -1, -1));
        }
    }

    public void recharge(MapleClient c, short slot) {
        if (c.getPlayer().isScriptShop()) {
            c.getPlayer().dropMessage(1, "這個商店無法儲值道具。");
            c.announce(NPCPacket.confirmShopTransaction(MapleShopResponse.ShopRes_RechargeSuccess, this, c, -1, -1));
            return;
        }
        MapleShopResponse response = MapleShopResponse.ShopRes_RechargeSuccess;
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (item == null || !ItemConstants.類型.飛鏢(item.getItemId()) && !ItemConstants.類型.子彈(item.getItemId())) {
            c.announce(NPCPacket.confirmShopTransaction(MapleShopResponse.ShopRes_RechargeIncorrectRequest, this, c, -1, -1));
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(item.getItemId());
        MapleStatEffect effect = null;
        if (ItemConstants.類型.飛鏢(item.getItemId())) {
            effect = c.getPlayer().getSkillEffect(4100000);
            if (effect == null) {
                effect = c.getPlayer().getSkillEffect(14100023);
            }
        } else if (ItemConstants.類型.子彈(item.getItemId())) {
            effect = c.getPlayer().getSkillEffect(5200000);
        }
        if (effect != null) {
            slotMax = (short)(slotMax + effect.getY());
        }
        if (item.getQuantity() >= slotMax) {
            response = MapleShopResponse.ShopRes_RechargeIncorrectRequest;
        }
        int price = (int)Math.round(ii.getUnitPrice(item.getItemId()) * (double)(slotMax - item.getQuantity()));
        if (c.getPlayer().getMeso() < (long)price) {
            response = MapleShopResponse.ShopRes_RechargeNoMoney;
        }
        if (response == MapleShopResponse.ShopRes_RechargeSuccess) {
            item.setQuantity(slotMax);
            c.getPlayer().gainMeso(-price, false, false);
            c.announce(InventoryPacket.modifyInventory(false, Collections.singletonList(new ModifyInventory(1, item))));
        }
        c.announce(NPCPacket.confirmShopTransaction(response, this, c, -1, -1));
    }

    protected MapleShopItem findBySlotAndId(int itemId, int slot) {
        MapleShopItem shopItem = this.items.get(slot);
        if (shopItem != null && shopItem.getItemId() == itemId) {
            return shopItem;
        }
        return null;
    }

    protected MapleShopItem findBySlotAndId(MapleClient c, int itemId, int pos) {
        List<MapleShopItem> items = this.getItems(c);
        if (pos >= items.size()) {
            return null;
        }
        MapleShopItem shopItem = items.get(pos);
        if (shopItem != null && shopItem.getItemId() == itemId) {
            return shopItem;
        }
        return null;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public int getId() {
        return this.id;
    }

    public int getShopItemId() {
        return this.shopItemId;
    }

    public List<Pair<Integer, String>> getRanks() {
        return this.ranks;
    }
}

