/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.shops.AbstractPlayerStore$BoughtItem
 *  Net.server.shops.MaplePlayerShopItem
 */
package Net.server.shops;

import Client.MapleCharacter;
import Client.MapleClient;
import Net.server.shops.AbstractPlayerStore;
import Net.server.shops.MaplePlayerShopItem;
import java.util.List;
import tools.Pair;

public interface IMaplePlayerShop {
    public static final byte HIRED_MERCHANT = 1;
    public static final byte PLAYER_SHOP = 2;
    public static final byte OMOK = 3;
    public static final byte MATCH_CARD = 4;
    public static final byte HIRED_FISHER = 75;

    public String getOwnerName();

    public String getDescription();

    public void setDescription(String var1);

    public List<Pair<Byte, MapleCharacter>> getVisitors();

    public List<MaplePlayerShopItem> getItems();

    public boolean isOpen();

    public void setOpen(boolean var1);

    public boolean saveItems();

    public boolean removeItem(int var1);

    public boolean isOwner(MapleCharacter var1);

    public byte getShopType();

    public byte getVisitorSlot(MapleCharacter var1);

    public byte getFreeSlot();

    public int getItemId();

    public long getMeso();

    public void setMeso(long var1);

    public int getOwnerId();

    public int getOwnerAccId();

    public void addItem(MaplePlayerShopItem var1);

    public void removeFromSlot(int var1);

    public void broadcastToVisitors(byte[] var1);

    public void addVisitor(MapleCharacter var1);

    public void removeVisitor(MapleCharacter var1);

    public void removeAllVisitors(int var1, int var2);

    public void buy(MapleClient var1, int var2, short var3);

    public void closeShop(boolean var1, boolean var2);

    public String getPassword();

    public int getMaxSize();

    public int getSize();

    public int getGameType();

    public void update();

    public boolean isAvailable();

    public void setAvailable(boolean var1);

    public List<AbstractPlayerStore.BoughtItem> getBoughtItems();

    public List<Pair<String, Byte>> getMessages();

    public int getMapId();

    public int getChannel();
}

