/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.world.CharacterIdChannelPair
 */
package Server.world;

import Client.MapleCharacter;
import Server.channel.ChannelServer;
import Server.world.CharacterIdChannelPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldFindService {
    private static final Logger log = LoggerFactory.getLogger(WorldFindService.class);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final HashMap<Integer, Integer> idToChannel = new HashMap();
    private final HashMap<String, Integer> nameToChannel = new HashMap();

    private WorldFindService() {
    }

    public static WorldFindService getInstance() {
        return SingletonHolder.instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void register(int chrId, String chrName, int channel) {
        this.lock.writeLock().lock();
        try {
            this.idToChannel.put(chrId, channel);
            this.nameToChannel.put(chrName.toLowerCase(), channel);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        if (channel == -10) {
            System.out.println("玩家連接 - 角色ID: " + chrId + " 名字: " + chrName + " 進入購物商場");
        } else if (channel == -20) {
            System.out.println("玩家連接 - 角色ID: " + chrId + " 名字: " + chrName + " 進入拍賣場");
        } else if (channel > -1) {
            System.out.println("玩家連接 - 角色ID: " + chrId + " 名字: " + chrName + " 頻道: " + channel);
        } else {
            System.out.println("玩家連接 - 角色ID: " + chrId + " 未處理的頻道...");
        }
    }

    public void forceDeregister(int chrId) {
        this.lock.writeLock().lock();
        try {
            this.idToChannel.remove(chrId);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("玩家離開 - 角色ID: " + chrId);
    }

    public void forceDeregister(String chrName) {
        this.lock.writeLock().lock();
        try {
            if (chrName != null) {
                this.nameToChannel.remove(chrName.toLowerCase());
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("玩家離開 - 角色名字: " + chrName);
    }

    public void forceDeregister(int chrId, String chrName) {
        this.lock.writeLock().lock();
        try {
            this.idToChannel.remove(chrId);
            if (chrName != null) {
                this.nameToChannel.remove(chrName.toLowerCase());
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("玩家離開 - 角色ID: " + chrId + " 名字: " + chrName);
    }

    public void forceDeregisterEx(int chrId, String chrName) {
        this.lock.writeLock().lock();
        try {
            this.idToChannel.remove(chrId);
            if (chrName != null) {
                this.nameToChannel.remove(chrName.toLowerCase());
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        System.out.println("清理卡號玩家 - 角色ID: " + chrId + " 名字: " + chrName);
    }

    public int findChannel(int chrId) {
        Integer ret;
        this.lock.readLock().lock();
        try {
            ret = this.idToChannel.get(chrId);
        }
        finally {
            this.lock.readLock().unlock();
        }
        if (ret != null) {
            if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) {
                this.forceDeregister(chrId);
                return -1;
            }
            return ret;
        }
        return -1;
    }

    public int findChannel(String chrName) {
        Integer ret;
        this.lock.readLock().lock();
        try {
            ret = chrName == null ? null : this.nameToChannel.get(chrName.toLowerCase());
        }
        finally {
            this.lock.readLock().unlock();
        }
        if (ret != null) {
            if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) {
                this.forceDeregister(chrName);
                return -1;
            }
            return ret;
        }
        return -1;
    }

    public CharacterIdChannelPair[] multiBuddyFind(int charIdFrom, int[] characterIds) {
        ArrayList<CharacterIdChannelPair> foundsChars = new ArrayList<CharacterIdChannelPair>(characterIds.length);
        for (int i : characterIds) {
            int channel = this.findChannel(i);
            if (channel <= 0) continue;
            foundsChars.add(new CharacterIdChannelPair(i, channel));
        }
        Collections.sort(foundsChars);
        return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
    }

    public MapleCharacter findCharacterByName(String name) {
        int ch = this.findChannel(name);
        if (ch > 0) {
            return ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name);
        }
        return null;
    }

    public MapleCharacter findCharacterById(int id) {
        int ch = this.findChannel(id);
        if (ch > 0) {
            return ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(id);
        }
        return null;
    }

    private static class SingletonHolder {
        protected static final WorldFindService instance = new WorldFindService();

        private SingletonHolder() {
        }
    }
}

