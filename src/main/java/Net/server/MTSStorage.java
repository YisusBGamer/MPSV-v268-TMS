/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.MTSCart
 *  Net.server.MTSStorage$MTSItemInfo
 */
package Net.server;

import Client.inventory.Item;
import Client.inventory.ItemLoader;
import Client.inventory.MapleInventoryType;
import Config.constants.ItemConstants;
import Config.constants.ServerConstants;
import Database.DatabaseLoader;
import Net.server.MTSCart;
import Net.server.MTSStorage;
import Net.server.MapleItemInformationProvider;
import Packet.MTSCSPacket;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import tools.Pair;

public class MTSStorage {
    private static MTSStorage instance;
    private final Map<Integer, MTSCart> idToCart;
    private final AtomicInteger packageId;
    private final Map<Integer, MTSItemInfo> buyNow;
    private final ReentrantReadWriteLock mutex;
    private final ReentrantReadWriteLock cart_mutex;
    private long lastUpdate = System.currentTimeMillis();
    private boolean end = false;

    public MTSStorage() {
        this.idToCart = new LinkedHashMap<Integer, MTSCart>();
        this.buyNow = new LinkedHashMap<Integer, MTSItemInfo>();
        this.packageId = new AtomicInteger(1);
        this.mutex = new ReentrantReadWriteLock();
        this.cart_mutex = new ReentrantReadWriteLock();
    }

    public static MTSStorage getInstance() {
        return instance;
    }

    public static void load() {
        if (instance == null) {
            instance = new MTSStorage();
            instance.loadBuyNow();
        }
    }

    public boolean check(int packageid) {
        return this.getSingleItem(packageid) != null;
    }

    public boolean checkCart(int packageid, int charID) {
        MTSItemInfo item = this.getSingleItem(packageid);
        return item != null && item.getCharacterId() != charID;
    }

    public MTSItemInfo getSingleItem(int packageid) {
        this.mutex.readLock().lock();
        try {
            MTSItemInfo mTSItemInfo = this.buyNow.get(packageid);
            return mTSItemInfo;
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToBuyNow(MTSCart cart, Item item, int price, int cid, String seller, long expiration) {
        int id;
        this.mutex.writeLock().lock();
        try {
            id = this.packageId.incrementAndGet();
            this.buyNow.put(id, new MTSItemInfo(price, item, seller, id, cid, expiration));
        }
        finally {
            this.mutex.writeLock().unlock();
        }
        cart.addToNotYetSold(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeFromBuyNow(int id, int cidBought, boolean check) {
        Item item = null;
        this.mutex.writeLock().lock();
        try {
            if (this.buyNow.containsKey(id)) {
                MTSItemInfo r = this.buyNow.get(id);
                if (!check || r.getCharacterId() == cidBought) {
                    item = r.getItem();
                    this.buyNow.remove(id);
                }
            }
        }
        finally {
            this.mutex.writeLock().unlock();
        }
        if (item != null) {
            this.cart_mutex.readLock().lock();
            try {
                for (Map.Entry<Integer, MTSCart> c : this.idToCart.entrySet()) {
                    c.getValue().removeFromCart(id);
                    c.getValue().removeFromNotYetSold(id);
                    if (c.getKey() != cidBought) continue;
                    c.getValue().addToInventory(item);
                }
            }
            finally {
                this.cart_mutex.readLock().unlock();
            }
        }
        return item != null;
    }

    private void loadBuyNow() {
        int lastPackage = 0;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM mts_items WHERE tab = 1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<Long, Pair<Item, MapleInventoryType>> items;
                lastPackage = rs.getInt("id");
                int charId = rs.getInt("characterid");
                if (!this.idToCart.containsKey(charId)) {
                    this.idToCart.put(charId, new MTSCart(charId));
                }
                if ((items = ItemLoader.拍賣道具.loadItems(false, lastPackage)) == null || items.size() <= 0) continue;
                for (Pair<Item, MapleInventoryType> i : items.values()) {
                    this.buyNow.put(lastPackage, new MTSItemInfo(rs.getInt("price"), i.getLeft(), rs.getString("seller"), lastPackage, charId, rs.getLong("expiration")));
                }
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.packageId.set(lastPackage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveBuyNow(boolean isShutDown) {
        Iterator<Object> iterator;
        if (this.end) {
            return;
        }
        this.end = isShutDown;
        if (isShutDown) {
            System.out.println("正在保存 MTS...");
        }
        Map<Integer, ArrayList<Item>> expire = new HashMap<>();
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        long now = System.currentTimeMillis();
        Map<Integer, ArrayList<Pair<Item, MapleInventoryType>>> items = new HashMap<>();
        this.mutex.writeLock().lock();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM mts_items WHERE tab = 1");
            preparedStatement.execute();
            preparedStatement.close();
            PreparedStatement preparedStatement2 = con.prepareStatement("INSERT INTO mts_items VALUES (?, ?, ?, ?, ?, ?)");
            for (MTSItemInfo m : this.buyNow.values()) {
                if (now > m.getEndingDate()) {
                    if (!expire.containsKey(m.getCharacterId())) {
                        expire.put(m.getCharacterId(), new ArrayList());
                    }
                    ((ArrayList)expire.get(m.getCharacterId())).add(m.getItem());
                    toRemove.add(m.getId());
                    items.put(m.getId(), null);
                    continue;
                }
                preparedStatement2.setInt(1, m.getId());
                preparedStatement2.setByte(2, (byte)1);
                preparedStatement2.setInt(3, m.getPrice());
                preparedStatement2.setInt(4, m.getCharacterId());
                preparedStatement2.setString(5, m.getSeller());
                preparedStatement2.setLong(6, m.getEndingDate());
                preparedStatement2.executeUpdate();
                if (!items.containsKey(m.getId())) {
                    items.put(m.getId(), new ArrayList());
                }
                ((ArrayList)items.get(m.getId())).add(new Pair<Item, MapleInventoryType>(m.getItem(), ItemConstants.getInventoryType(m.getItem().getItemId())));
            }
            Iterator<Integer> iterator1 = toRemove.iterator();
            while (iterator1.hasNext()) {
                int i = (Integer)iterator1.next();
                this.buyNow.remove(i);
            }
            preparedStatement2.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            this.mutex.writeLock().unlock();
        }
        if (isShutDown) {
            System.out.println("正在保存 MTS 道具信息...");
        }
        try {
            for (Map.Entry entry : items.entrySet()) {
                ItemLoader.拍賣道具.saveItems(null, (List)entry.getValue(), ((Integer)entry.getKey()).intValue());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (isShutDown) {
            System.out.println("正在保存 MTS carts...");
        }
        this.cart_mutex.writeLock().lock();
        try {
            for (Map.Entry<Integer, MTSCart> entry : this.idToCart.entrySet()) {

                Iterator<Integer> iterator1 = toRemove.iterator();
                while (iterator1.hasNext()) {
                    int i = (Integer)iterator1.next();
                    ((MTSCart)entry.getValue()).removeFromCart(i);
                    ((MTSCart)entry.getValue()).removeFromNotYetSold(i);
                }
                if (expire.containsKey(entry.getKey())) {
                    Integer key = entry.getKey();
                    ArrayList<Item> items1 = expire.get(key);
                    for (Item item : items1) {
                        ((MTSCart)entry.getValue()).addToInventory(item);
                    }
                }
                ((MTSCart)entry.getValue()).save();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            this.cart_mutex.writeLock().unlock();
        }
        this.lastUpdate = System.currentTimeMillis();
    }

    public void checkExpirations() {
        if (System.currentTimeMillis() - this.lastUpdate > 3600000L) {
            this.saveBuyNow(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MTSCart getCart(int characterId) {
        MTSCart ret;
        this.cart_mutex.readLock().lock();
        try {
            ret = this.idToCart.get(characterId);
        }
        finally {
            this.cart_mutex.readLock().unlock();
        }
        if (ret == null) {
            this.cart_mutex.writeLock().lock();
            try {
                ret = new MTSCart(characterId);
                this.idToCart.put(characterId, ret);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                this.cart_mutex.writeLock().unlock();
            }
        }
        return ret;
    }

    public byte[] getCurrentMTS(MTSCart cart) {
        this.mutex.readLock().lock();
        try {
            byte[] byArray = MTSCSPacket.sendMTS(this.getMultiItems(cart.getCurrentView(), cart.getPage()), cart.getTab(), cart.getType(), cart.getPage(), cart.getCurrentView().size());
            return byArray;
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getCurrentNotYetSold(MTSCart cart) {
        this.mutex.readLock().lock();
        try {
            ArrayList<MTSItemInfo> nys = new ArrayList<MTSItemInfo>();
            ArrayList<Integer> nyss = new ArrayList(cart.getNotYetSold());
            Iterator<Integer> iterator = nyss.iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                MTSItemInfo r = this.buyNow.get(i);
                if (r == null) {
                    cart.removeFromNotYetSold(i);
                    continue;
                }
                nys.add(r);
            }
            return MTSCSPacket.getNotYetSoldInv(nys);
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    public byte[] getCurrentTransfer(MTSCart cart, boolean changed) {
        return MTSCSPacket.getTransferInventory(cart.getInventory(), changed);
    }

    public List<MTSItemInfo> getMultiItems(List<Integer> items, int pageNumber) {
        int minSize;
        ArrayList<MTSItemInfo> ret = new ArrayList<MTSItemInfo>();
        ArrayList<Integer> cartt = new ArrayList<Integer>(items);
        if (pageNumber > cartt.size() / 16 + (cartt.size() % 16 > 0 ? 1 : 0)) {
            pageNumber = 0;
        }
        int maxSize = Math.min(cartt.size(), pageNumber * 16 + 16);
        for (int i = minSize = Math.min(cartt.size(), pageNumber * 16); i < maxSize && cartt.size() > i; ++i) {
            MTSItemInfo r = this.buyNow.get(cartt.get(i));
            if (r == null) {
                items.remove(i);
                cartt.remove(i);
                continue;
            }
            ret.add(r);
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getBuyNow(int type) {
        this.mutex.readLock().lock();
        try {
            if (type == 0) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>(this.buyNow.keySet());
                return arrayList;
            }
            ArrayList<MTSItemInfo> ret = new ArrayList<MTSItemInfo>(this.buyNow.values());
            ArrayList<Integer> rett = new ArrayList<Integer>();
            for (MTSItemInfo aRet : ret) {
                MTSItemInfo r = aRet;
                if (r == null || ItemConstants.getInventoryType(r.getItem().getItemId()).getType() != type) continue;
                rett.add(r.getId());
            }
            ArrayList<Integer> arrayList = rett;
            return arrayList;
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> getSearch(boolean item, String name, int type, int tab) {
        this.mutex.readLock().lock();
        try {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (tab != 1 || name.length() <= 0) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                return arrayList;
            }
            name = name.toLowerCase();
            ArrayList<MTSItemInfo> ret = new ArrayList<MTSItemInfo>(this.buyNow.values());
            ArrayList<Integer> rett = new ArrayList<Integer>();
            for (MTSItemInfo aRet : ret) {
                String thename;
                MTSItemInfo r = aRet;
                if (r == null || type != 0 && ItemConstants.getInventoryType(r.getItem().getItemId()).getType() != type || (thename = item ? ii.getName(r.getItem().getItemId()) : r.getSeller()) == null || !thename.toLowerCase().contains(name)) continue;
                rett.add(r.getId());
            }
            ArrayList<Integer> arrayList = rett;
            return arrayList;
        }
        finally {
            this.mutex.readLock().unlock();
        }
    }

    public List<MTSItemInfo> getCartItems(MTSCart cart) {
        return this.getMultiItems(cart.getCart(), cart.getPage());
    }

    public static class MTSItemInfo {

        private final int price;
        private final Item item;
        private final String seller;
        private final int id; //packageid
        private final int cid;
        private final long date;

        public MTSItemInfo(int price, Item item, String seller, int id, int cid, long date) {
            this.item = item;
            this.price = price;
            this.seller = seller;
            this.id = id;
            this.cid = cid;
            this.date = date;
        }

        public Item getItem() {
            return item;
        }

        public int getPrice() {
            return price;
        }

        public int getRealPrice() {
            return price + getTaxes();
        }

        public int getTaxes() {
            return ServerConstants.MTS_BASE + price * ServerConstants.MTS_TAX / 100;
        }

        public int getId() {
            return id;
        }

        public int getCharacterId() {
            return cid;
        }

        public long getEndingDate() {
            return date;
        }

        public String getSeller() {
            return seller;
        }
    }
}

