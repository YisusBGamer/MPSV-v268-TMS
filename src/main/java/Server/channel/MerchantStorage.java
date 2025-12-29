/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.shops.HiredMerchant
 */
package Server.channel;

import Net.server.shops.HiredMerchant;
import Server.channel.ChannelServer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantStorage {
    private static final Logger log = LoggerFactory.getLogger(ChannelServer.class);
    private final int channel;
    private final Map<Integer, HiredMerchant> merchants = new HashMap<Integer, HiredMerchant>();
    private int running_MerchantID = 0;
    private ReentrantReadWriteLock merchLock = null;
    private ReentrantReadWriteLock.ReadLock mcReadLock = null;
    private ReentrantReadWriteLock.WriteLock mcWriteLock = null;

    MerchantStorage(int channel) {
        this.channel = channel;
        this.merchLock = new ReentrantReadWriteLock(true);
        this.mcReadLock = this.merchLock.readLock();
        this.mcWriteLock = this.merchLock.writeLock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeAllMerchants() {
        int ret = 0;
        long Start2 = System.currentTimeMillis();
        this.mcWriteLock.lock();
        try {
            Iterator<Map.Entry<Integer, HiredMerchant>> hmit = this.merchants.entrySet().iterator();
            while (hmit.hasNext()) {
                hmit.next().getValue().closeShop(true, false);
                hmit.remove();
                ++ret;
            }
        }
        catch (Exception e) {
            log.error("關閉僱傭商店出現錯誤..." + String.valueOf(e));
        }
        finally {
            this.mcWriteLock.unlock();
        }
        log.info("頻道 " + this.channel + " 共保存僱傭商店: " + ret + " | 耗時: " + (System.currentTimeMillis() - Start2) + " 毫秒.");
    }

    public int addMerchant(HiredMerchant hMerchant) {
        this.mcWriteLock.lock();
        try {
            ++this.running_MerchantID;
            this.merchants.put(this.running_MerchantID, hMerchant);
            int n = this.running_MerchantID;
            return n;
        }
        finally {
            this.mcWriteLock.unlock();
        }
    }

    public void removeMerchant(HiredMerchant hMerchant) {
        this.mcWriteLock.lock();
        try {
            this.merchants.remove(hMerchant.getStoreId());
        }
        finally {
            this.mcWriteLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsMerchant(int accId) {
        boolean contains = false;
        this.mcReadLock.lock();
        try {
            for (HiredMerchant hm : this.merchants.values()) {
                if (hm.getOwnerAccId() != accId) continue;
                contains = true;
                break;
            }
        }
        finally {
            this.mcReadLock.unlock();
        }
        return contains;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsMerchant(int accId, int chrId) {
        boolean contains = false;
        this.mcReadLock.lock();
        try {
            for (HiredMerchant hm : this.merchants.values()) {
                if (hm.getOwnerAccId() != accId || hm.getOwnerId() != chrId) continue;
                contains = true;
                break;
            }
        }
        finally {
            this.mcReadLock.unlock();
        }
        return contains;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<HiredMerchant> searchMerchant(int itemSearch) {
        LinkedList<HiredMerchant> list = new LinkedList<HiredMerchant>();
        this.mcReadLock.lock();
        try {
            for (HiredMerchant hm : this.merchants.values()) {
                if (hm.searchItem(itemSearch).size() <= 0) continue;
                list.add(hm);
            }
        }
        finally {
            this.mcReadLock.unlock();
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HiredMerchant getHiredMerchants(int accId, int chrId) {
        this.mcReadLock.lock();
        try {
            for (HiredMerchant hm : this.merchants.values()) {
                if (hm.getOwnerAccId() != accId || hm.getOwnerId() != chrId) continue;
                HiredMerchant hiredMerchant = hm;
                return hiredMerchant;
            }
        }
        finally {
            this.mcReadLock.unlock();
        }
        return null;
    }
}

