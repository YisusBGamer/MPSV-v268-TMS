/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Packet.AuctionPacket
 */
package Server.auction;

import Client.MapleCharacter;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.ItemLoader;
import Client.inventory.MapleInventoryIdentifier;
import Client.inventory.MapleInventoryType;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Database.DatabaseException;
import Database.DatabaseLoader;
import Database.mapper.AuctionItemMapper;
import Database.tools.SqlTool;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.Timer;
import Packet.AuctionPacket;
import Packet.MaplePacketCreator;
import Server.ServerType;
import Server.auction.AuctionItem;
import Server.channel.PlayerStorage;
import Server.netty.ServerConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;

public class AuctionServer {
    public static final Map<Integer, Map<Integer, Map<Integer, Map<Integer, Pair<Integer, Integer>>>>> auctions = new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer, Pair<Integer, Integer>>>>>();
    public static final AtomicLong runningSNID;
    private static final Logger log;
    private static final AuctionServer instance;
    public final Map<Long, AuctionItem> items = new TreeMap(Comparator.reverseOrder());
    private final Map<Integer, List<Integer>> collections = new HashMap<Integer, List<Integer>>();
    public final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock sqlLock = new ReentrantLock();
    private ServerConnection init;
    private PlayerStorage players;
    private boolean finishedShutdown = false;
    private short port;
    private short channel;
    private short world;
    private ScheduledFuture schedule;

    public static AuctionServer getInstance() {
        return instance;
    }

    public void init() {
        this.port = ServerConfig.AUCTION_PORT;
        this.world = 0;
        this.channel = (short)-20;
        this.players = new PlayerStorage(-20);
        this.schedule = Timer.ExpiredTimer.getInstance().schedule(new ExpiredCheckThread(), 60000L);
        this.lock.lock();
        List<AuctionItem> auctionItems = SqlTool.queryAndGetList("SELECT * FROM `auction` WHERE `world` = ?", new AuctionItemMapper(), this.world);
        try {
            for (AuctionItem ai : auctionItems) {
                Iterator<Pair<Item, MapleInventoryType>> iterator;
                Map<Long, Pair<Item, MapleInventoryType>> map = ItemLoader.拍賣道具.loadItems(false, ai.id);
                if (!map.isEmpty() && (iterator = map.values().iterator()).hasNext()) {
                    ai.item = (Item)iterator.next().left;
                }
                this.items.put(ai.id, ai);
            }
        }
        catch (Exception e) {
            throw new DatabaseException(e);
        }
        finally {
            this.lock.unlock();
        }
        this.run();
    }

    public void run() {
        try {
            this.init = new ServerConnection(this.port, 0, -20, ServerType.AuctionServer);
            this.init.run();
        }
        catch (Exception e) {
            throw new RuntimeException("拍賣場伺服器綁定連接埠 " + this.port + " 失敗", e);
        }
    }

    public void updateAuctionItem(AuctionItem auctionItem) {
        this.lock.lock();
        try {
            SqlTool.update("UPDATE `auction` SET `number` = ?, `other_id` = ?, `other` = ?, `state` = ?, `startdate` = ?, `expiredate` = ?, `donedate` = ? WHERE `id` = ?", auctionItem.number, auctionItem.other_id, auctionItem.other, auctionItem.state, auctionItem.startdate, auctionItem.expiredate, auctionItem.donedate, auctionItem.id);
        }
        finally {
            this.lock.unlock();
        }
    }

    public final void changeAuctionItemWorld(AuctionItem auctionItem) {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            this.sqlLock.lock();
            try {
                SqlTool.update(con, "DELETE FROM `auction` WHERE `id` = ? ", auctionItem.id);
                SqlTool.update(con, "INSERT INTO `auction` (`id`, `world`, `accounts_id`, `characters_id`, `owner`, `other_id`, `other`, `itemid`, `number`, `type`, `price`, `state`, `startdate`, `expiredate`, `donedate`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", auctionItem.id, this.world, auctionItem.accounts_id, auctionItem.characters_id, auctionItem.owner, auctionItem.other_id, auctionItem.other, auctionItem.itemid, auctionItem.number, auctionItem.type, auctionItem.price, auctionItem.state, auctionItem.startdate, auctionItem.expiredate, auctionItem.donedate);
                if (auctionItem.item != null) {
                    ItemLoader.拍賣道具.saveItems(con, Collections.singletonList(new Pair<Item, MapleInventoryType>(auctionItem.item, ItemConstants.getInventoryType(auctionItem.itemid))), auctionItem.id);
                }
            }
            finally {
                this.sqlLock.unlock();
            }
            return null;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final AuctionItem terminateById(MapleCharacter player, long n) {
        this.lock.lock();
        try {
            AuctionItem auctionItem = this.items.get(n);
            if (auctionItem != null && auctionItem.characters_id == player.getId() && auctionItem.state == 0) {
                auctionItem.state = 4;
                auctionItem.donedate = System.currentTimeMillis();
                AuctionItem auctionItem2 = auctionItem;
                return auctionItem2;
            }
            AuctionItem auctionItem3 = null;
            return auctionItem3;
        }
        finally {
            this.lock.unlock();
        }
    }

    public final List<AuctionItem> getAllOnsaleItemByPlayerId(int n) {
        this.lock.lock();
        try {
            List<AuctionItem> list = this.items.values().parallelStream().filter(item -> item.characters_id == n && item.state == 0).collect(Collectors.toList());
            return list;
        }
        finally {
            this.lock.unlock();
        }
    }

    public final List<AuctionItem> getAllCollectionItemByPlayerId(int n) {
        this.lock.lock();
        try {
            List<AuctionItem> list = this.items.values().parallelStream().filter(item -> item.characters_id == n && item.state == 0).collect(Collectors.toList());
            return list;
        }
        finally {
            this.lock.unlock();
        }
    }

    public final int getItemCountByPlayerId(int n) {
        this.lock.lock();
        try {
            int n2 = (int)this.items.values().parallelStream().filter(item -> item.characters_id == n && (item.state == 0 || item.state == 4)).count();
            return n2;
        }
        finally {
            this.lock.unlock();
        }
    }

    public final List<AuctionItem> getAllNotOnsaleItemByPlayerId(int n) {
        this.lock.lock();
        try {
            List<AuctionItem> list = this.items.values().parallelStream().filter(item -> item.characters_id == n && item.state > 0).collect(Collectors.toList());
            return list;
        }
        finally {
            this.lock.unlock();
        }
    }

    public final int getAllNotOnsaleItemCountByPlayerId(int n) {
        this.lock.lock();
        try {
            int n2 = (int)this.items.values().parallelStream().filter(item -> item.characters_id == n && item.state > 0 && item.state <= 5).count();
            return n2;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<AuctionItem> findItem(String fstr, int minItemId, int maxItemId, int minLevel, int maxLevel, int grade) {
        this.lock.lock();
        try {
            ArrayList<AuctionItem> list = new ArrayList<AuctionItem>();
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            for (AuctionItem item : this.items.values()) {
                if (item.state != 0 || item.item == null || !fstr.isEmpty() && !ii.getName(item.itemid).contains(fstr) || ii.getReqLevel(item.itemid) < minLevel || ii.getReqLevel(item.itemid) > maxLevel || item.itemid < minItemId || item.itemid > maxItemId) continue;
                if (grade != -1 && item.item instanceof Equip) {
                    if ((((Equip)item.item).getState(false) & grade) != 0) continue;
                    list.add(item);
                    continue;
                }
                list.add(item);
            }
            ArrayList<AuctionItem> arrayList = list;
            return arrayList;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<AuctionItem> findBuyerItemByLevel(int minLevel, int maxLevel) {
        this.lock.lock();
        try {
            ArrayList<AuctionItem> list = new ArrayList<AuctionItem>();
            MapleItemInformationProvider s283 = MapleItemInformationProvider.getInstance();
            for (AuctionItem item : this.items.values()) {
                if (item.state != 2 || s283.getReqLevel(item.itemid) < minLevel || s283.getReqLevel(item.itemid) > maxLevel) continue;
                list.add(item);
            }
            ArrayList<AuctionItem> arrayList = list;
            return arrayList;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final AuctionItem getItemBySN(long n) {
        this.lock.lock();
        try {
            AuctionItem auctionItem = this.items.get(n);
            return auctionItem;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void buy(MapleCharacter player, int auctionOptType, long n, int number) {
        if (number < 0 || number > Short.MAX_VALUE) {
            player.getClient().getSession().close();
        }
        this.lock.lock();
        try {
            AuctionItem ai = this.items.get(n);
            if (ai != null && ai.characters_id != player.getId() && ai.state == 0 && ai.item != null) {
                long n2 = ai.price * (long)number;
                if ((ai.type == 2 ? (long)player.getCSPoints(2) : player.getMeso()) < ai.price) {
                    player.getClient().announce(AuctionPacket.auctionResult((int)auctionOptType, (int)(ai.type == 2 ? 900 : 106)));
                    return;
                }
                if (!MapleInventoryManipulator.checkSpace(player.getClient(), ai.itemid, number, "")) {
                    player.getClient().announce(AuctionPacket.auctionResult((int)auctionOptType, (int)1));
                    return;
                }
                if (number > ai.number) {
                    player.getClient().announce(AuctionPacket.auctionResult((int)auctionOptType, (int)102));
                    return;
                }
                if (player.getMeso() < n2) {
                    player.getClient().announce(AuctionPacket.auctionResult((int)auctionOptType, (int)106));
                    return;
                }
                player.gainMeso(-n2, false);
                Item copy = ai.item.copy();
                boolean b584 = false;
                long akd = ai.price;
                if (number < ai.item.getQuantity() && copy.getType() == 2 && !ItemConstants.類型.可充值道具(copy.getItemId())) {
                    copy.setQuantity((short)number);
                    ai.item.setQuantity((short)(ai.item.getQuantity() - number));
                    ai.number = ai.item.getQuantity();
                    b584 = true;
                } else {
                    ai.state = 3;
                    ai.item = null;
                    ai.price = akd * (long)ai.number;
                    ai.number = 0;
                }
                if (ItemAttribute.TradeOnce.check(copy.getAttribute())) {
                    copy.removeAttribute(ItemAttribute.TradeOnce.getValue());
                }
                if (ItemAttribute.CutUsed.check(copy.getAttribute())) {
                    copy.removeAttribute(ItemAttribute.CutUsed.getValue());
                }
                if (copy.getType() == 2) {
                    copy.setSN(MapleInventoryIdentifier.getInstance());
                }
                AuctionItem b586 = new AuctionItem();
                b586.id = runningSNID.getAndIncrement();
                b586.accounts_id = player.getAccountID();
                b586.characters_id = player.getId();
                b586.owner = player.getName();
                b586.state = 2;
                b586.type = ai.type;
                b586.itemid = copy.getItemId();
                if (ItemConstants.類型.可充值道具(copy.getItemId())) {
                    b586.number = 1;
                    b586.price = akd;
                } else {
                    b586.number = copy.getQuantity();
                    b586.price = akd * (long)copy.getQuantity();
                }
                b586.startdate = ai.startdate;
                b586.expiredate = System.currentTimeMillis() + 31536000000L;
                b586.donedate = System.currentTimeMillis();
                b586.item = copy;
                b586.other_id = ai.characters_id;
                b586.other = ai.owner;
                if (b584) {
                    AuctionItem ain = new AuctionItem();
                    new AuctionItem().id = runningSNID.getAndIncrement();
                    ain.accounts_id = ai.accounts_id;
                    ain.characters_id = ai.characters_id;
                    ain.owner = ai.owner;
                    ain.state = 3;
                    ain.type = ai.type;
                    ain.itemid = copy.getItemId();
                    ain.price = ai.price * (long)copy.getQuantity();
                    ain.startdate = ai.startdate;
                    ain.expiredate = System.currentTimeMillis() + 31536000000L;
                    ain.donedate = System.currentTimeMillis();
                    ain.number = copy.getQuantity();
                    ain.other_id = player.getId();
                    ain.other = player.getName();
                    this.changeAuctionItemWorld(ain);
                    this.items.put(ain.id, ain);
                } else {
                    ai.donedate = System.currentTimeMillis();
                }
                this.changeAuctionItemWorld(ai);
                this.changeAuctionItemWorld(b586);
                this.items.put(b586.id, b586);
                player.getClient().announce(MaplePacketCreator.showCharCash(player));
                player.getClient().announce(AuctionPacket.updateAuctionItemInfo((int)auctionOptType, (AuctionItem)b586));
            } else {
                player.getClient().announce(AuctionPacket.auctionResult((int)auctionOptType, (int)102));
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public PlayerStorage getPlayerStorage() {
        return this.players;
    }

    public short getPort() {
        return this.port;
    }

    public void shutdown() {
        if (this.finishedShutdown) {
            return;
        }
        log.info("正在關閉拍賣場伺服器...");
        this.players.disconnectAll();
        log.info("拍賣場伺服器解除連接埠綁定...");
        this.init.close();
        if (this.schedule != null) {
            this.schedule.cancel(false);
            this.schedule = null;
        }
        this.finishedShutdown = true;
    }

    static {
        log = LoggerFactory.getLogger(AuctionServer.class);
        instance = new AuctionServer();
        runningSNID = new AtomicLong(DatabaseLoader.DatabaseConnection.domain(con -> {
            ResultSet rs = SqlTool.query(con, "SELECT MAX(id) FROM `auction`");
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        }, "讀取MaxAuctionId異常", true) + 1L);
    }

    private final class ExpiredCheckThread
    implements Runnable {
        private ExpiredCheckThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            AuctionServer.this.lock.lock();
            try {
                ArrayList expiredItems = new ArrayList();
                long currentTimeMillis = System.currentTimeMillis();
                AuctionServer.this.items.forEach((n5, auctionItem) -> {
                    if (auctionItem.state == 0) {
                        if (currentTimeMillis >= auctionItem.expiredate) {
                            auctionItem.state = 4;
                            auctionItem.donedate = currentTimeMillis;
                            auctionItem.expiredate = currentTimeMillis + 31536000000L;
                            AuctionServer.this.changeAuctionItemWorld((AuctionItem)auctionItem);
                        }
                    } else if (currentTimeMillis >= auctionItem.expiredate) {
                        expiredItems.add(n5);
                    }
                });
                expiredItems.forEach(id -> {
                    AuctionServer.this.items.remove(id);
                    AuctionServer.this.sqlLock.lock();
                    try {
                        SqlTool.update("DELETE FROM `auction` WHERE `id` = ? ", id);
                    }
                    finally {
                        AuctionServer.this.sqlLock.unlock();
                    }
                });
            }
            finally {
                AuctionServer.this.lock.unlock();
            }
        }
    }
}

