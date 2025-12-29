/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.shops.HiredFisher
 */
package Server.channel.handler;

import Net.server.shops.HiredFisher;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HiredFisherStorage {
    private final Logger log = LoggerFactory.getLogger(HiredFisherStorage.class);
    private int running_FisherID = 0;
    private final ReentrantReadWriteLock fisherLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock.ReadLock fReadLock = this.fisherLock.readLock();
    private final ReentrantReadWriteLock.WriteLock fWriteLock = this.fisherLock.writeLock();
    private final HashMap<Integer, HiredFisher> hiredFishers = new HashMap();
    private final int channel;

    public HiredFisherStorage(int channel) {
        this.channel = channel;
    }

    public void saveAllFisher() {
        this.fWriteLock.lock();
        try {
            for (Map.Entry<Integer, HiredFisher> it : this.hiredFishers.entrySet()) {
                it.getValue().saveItems();
            }
        }
        finally {
            this.fWriteLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeAllFisher() {
        int ret = 0;
        long start = System.currentTimeMillis();
        this.fWriteLock.lock();
        try {
            for (Map.Entry<Integer, HiredFisher> it : this.hiredFishers.entrySet()) {
                it.getValue().closeShop(true, false);
                ++ret;
            }
        }
        catch (Exception e) {
            this.log.error("關閉僱傭釣手出現錯誤...", e);
        }
        finally {
            this.fWriteLock.unlock();
        }
        System.out.println("頻道 " + this.channel + " 共保存僱傭釣手: " + ret + " | 耗時: " + (System.currentTimeMillis() - start) + " 毫秒.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsFisher(int accId, int chrId) {
        boolean contains = false;
        this.fReadLock.lock();
        try {
            for (HiredFisher hf : this.hiredFishers.values()) {
                if (hf.getOwnerAccId() != accId || hf.getOwnerId() != chrId) continue;
                contains = true;
                break;
            }
        }
        finally {
            this.fReadLock.unlock();
        }
        return contains;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HiredFisher getHiredFisher(int accId, int chrId) {
        this.fReadLock.lock();
        try {
            for (HiredFisher it : this.hiredFishers.values()) {
                if (it.getOwnerAccId() != accId || it.getOwnerId() != chrId) continue;
                HiredFisher hiredFisher = it;
                return hiredFisher;
            }
        }
        finally {
            this.fReadLock.unlock();
        }
        return null;
    }

    public int addFisher(HiredFisher hiredFisher) {
        this.fWriteLock.lock();
        try {
            ++this.running_FisherID;
            this.hiredFishers.put(this.running_FisherID, hiredFisher);
            int n = this.running_FisherID;
            return n;
        }
        finally {
            this.fWriteLock.unlock();
        }
    }

    public void removeFisher(HiredFisher hFisher) {
        this.fWriteLock.lock();
        try {
            this.hiredFishers.remove(hFisher.getId());
        }
        finally {
            this.fWriteLock.unlock();
        }
    }

    public int getChannel() {
        return this.channel;
    }
}

