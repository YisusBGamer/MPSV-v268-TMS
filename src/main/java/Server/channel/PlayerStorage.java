/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MapleCharacterUtil
 *  Server.world.CheaterData
 */
package Server.channel;

import Client.MapleCharacter;
import Client.MapleCharacterUtil;
import Config.configs.ServerConfig;
import Net.server.ShutdownServer;
import Net.server.Timer;
import Plugin.gui.GuiManager;
import Server.world.CharacterTransfer;
import Server.world.CheaterData;
import Server.world.WorldFindService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerStorage {
    private static final Logger log = LoggerFactory.getLogger(PlayerStorage.class);
    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final Lock readLock = this.mutex.readLock();
    private final Lock writeLock = this.mutex.writeLock();
    private final ReentrantReadWriteLock mutex2 = new ReentrantReadWriteLock();
    private final Lock connectcheckReadLock = this.mutex2.readLock();
    private final Lock pendingWriteLock = this.mutex2.writeLock();
    private final Map<String, MapleCharacter> nameToChar = new LinkedHashMap<String, MapleCharacter>();
    private final Map<Integer, MapleCharacter> idToChar = new LinkedHashMap<Integer, MapleCharacter>();
    private final Map<Integer, CharacterTransfer> PendingCharacter = new HashMap<Integer, CharacterTransfer>();
    private final int channel;

    public PlayerStorage(int channel) {
        this.channel = channel;
        Timer.PingTimer.getInstance().register(new PersistingTask(), 60000L);
        Timer.PingTimer.getInstance().register(new ConnectChecker(), 60000L);
    }

    public ArrayList<MapleCharacter> getAllCharacters() {
        this.readLock.lock();
        try {
            ArrayList<MapleCharacter> arrayList = new ArrayList<MapleCharacter>(this.idToChar.values());
            return arrayList;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public void registerPlayer(MapleCharacter chr) {
        this.writeLock.lock();
        try {
            this.nameToChar.put(chr.getName().toLowerCase(), chr);
            this.idToChar.put(chr.getId(), chr);
            if (ServerConfig.updatePlayerInGUI) {
                GuiManager.onlineStatusChanged(this.channel, this.getConnectedClients());
            }
        }
        finally {
            this.writeLock.unlock();
        }
        WorldFindService.getInstance().register(chr.getId(), chr.getName(), this.channel);
    }

    public void registerPendingPlayer(CharacterTransfer chr, int playerId) {
        this.writeLock.lock();
        try {
            this.PendingCharacter.put(playerId, chr);
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public final void deregisterPendingPlayerByAccountId(int accountId) {
        this.writeLock.lock();
        try {
            this.deregisterPendingPlayerByAccountId_noLock(accountId);
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public final void deregisterPendingPlayerByAccountId_noLock(int accountId) {
        LinkedList<Integer> toRemoveIds = new LinkedList<Integer>();
        Collection<CharacterTransfer> chars = this.PendingCharacter.values();
        for (CharacterTransfer transfer : chars) {
            if (transfer.accountid != accountId) continue;
            toRemoveIds.add(transfer.characterid);
        }
        Iterator<Integer> iterator = toRemoveIds.iterator();
        while (iterator.hasNext()) {
            int charid = (Integer)((Object)iterator.next());
            this.PendingCharacter.remove(charid);
        }
    }

    public void deregisterPlayer(MapleCharacter chr) {
        WorldFindService.getInstance().forceDeregister(chr.getId(), this.removePlayer(chr.getId()));
    }

    public void deregisterPlayer(int idz) {
        WorldFindService.getInstance().forceDeregister(idz, this.removePlayer(idz));
    }

    public void disconnectPlayer(MapleCharacter chr) {
        WorldFindService.getInstance().forceDeregisterEx(chr.getId(), this.removePlayer(chr.getId()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String removePlayer(int idz) {
        String namez = null;
        this.writeLock.lock();
        try {
            LinkedList<String> toRemoveNTC = new LinkedList<String>();
            for (Map.Entry<String, MapleCharacter> entry : this.nameToChar.entrySet()) {
                if (entry.getValue() != null && entry.getValue().getId() != idz) continue;
                toRemoveNTC.add(entry.getKey());
                if (entry.getValue().getId() != idz) continue;
                namez = entry.getKey();
            }
            for (String name : toRemoveNTC) {
                this.nameToChar.remove(name);
            }
            MapleCharacter chr = this.idToChar.remove(idz);
            if (chr != null) {
                chr.saveOnlineTime();
            }
            GuiManager.onlineStatusChanged(this.channel, this.getConnectedClients());
        }
        finally {
            this.writeLock.unlock();
        }
        return namez;
    }

    public CharacterTransfer getPendingCharacter(int playerId) throws IOException {
        this.writeLock.lock();
        try {
            CharacterTransfer characterTransfer = this.PendingCharacter.remove(playerId);
            return characterTransfer;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public MapleCharacter getCharacterByName(String name) {
        this.readLock.lock();
        try {
            MapleCharacter mapleCharacter = this.nameToChar.get(name.toLowerCase());
            return mapleCharacter;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public MapleCharacter getCharacterById(int id) {
        this.readLock.lock();
        try {
            MapleCharacter mapleCharacter = this.idToChar.get(id);
            return mapleCharacter;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public int getConnectedClients() {
        return this.idToChar.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<CheaterData> getCheaters() {
        ArrayList<CheaterData> cheaters = new ArrayList<CheaterData>();
        this.readLock.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (chr.getCheatTracker().getPoints() <= 0) continue;
                cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), MapleCharacterUtil.makeMapleReadable((String)chr.getName()) + " ID: " + chr.getId() + " (" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary()));
            }
        }
        finally {
            this.readLock.unlock();
        }
        return cheaters;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<CheaterData> getReports() {
        ArrayList<CheaterData> cheaters = new ArrayList<CheaterData>();
        this.readLock.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (chr.getReportPoints() <= 0) continue;
                cheaters.add(new CheaterData(chr.getReportPoints(), MapleCharacterUtil.makeMapleReadable((String)chr.getName()) + " ID: " + chr.getId() + " (" + chr.getReportPoints() + ") " + chr.getReportSummary()));
            }
        }
        finally {
            this.readLock.unlock();
        }
        return cheaters;
    }

    public void disconnectAll() {
        this.disconnectAll(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disconnectAll(boolean checkGM) {
        this.writeLock.lock();
        try {
            ArrayList<MapleCharacter> characters = new ArrayList<MapleCharacter>(this.nameToChar.values());
            for (MapleCharacter chr : characters) {
                if (chr.isGm() && checkGM) continue;
                chr.getClient().disconnect(false, false, true);
                if (chr.getClient().getSession() != null && chr.getClient().getSession().isActive()) {
                    chr.getClient().getSession().close();
                }
                this.deregisterPlayer(chr);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getOnlinePlayers(boolean byGM) {
        StringBuilder sb = new StringBuilder();
        if (byGM) {
            this.readLock.lock();
            try {
                for (MapleCharacter mapleCharacter : this.nameToChar.values()) {
                    sb.append(MapleCharacterUtil.makeMapleReadable((String)mapleCharacter.getName()));
                    sb.append(", ");
                }
            }
            finally {
                this.readLock.unlock();
            }
        }
        this.readLock.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (chr.isGm()) continue;
                sb.append(MapleCharacterUtil.makeMapleReadable((String)chr.getName()));
                sb.append(", ");
            }
        }
        finally {
            this.readLock.unlock();
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcastPacket(byte[] data) {
        this.readLock.lock();
        try {
            for (MapleCharacter mapleCharacter : this.nameToChar.values()) {
                mapleCharacter.getClient().announce(data);
            }
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcastSmegaPacket(byte[] data) {
        this.readLock.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (!chr.getClient().isLoggedIn() || !chr.getSmega()) continue;
                chr.send(data);
            }
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcastGMPacket(byte[] data) {
        this.readLock.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (!chr.getClient().isLoggedIn() || !chr.isIntern()) continue;
                chr.send(data);
            }
        }
        finally {
            this.readLock.unlock();
        }
    }

    public class PersistingTask
    implements Runnable {
        @Override
        public void run() {
            PlayerStorage.this.pendingWriteLock.lock();
            try {
                long currenttime = System.currentTimeMillis();
                PlayerStorage.this.PendingCharacter.entrySet().removeIf(next -> currenttime - ((CharacterTransfer)next.getValue()).TranferTime > 1800000L);
            }
            finally {
                PlayerStorage.this.pendingWriteLock.unlock();
            }
        }
    }

    private class ConnectChecker
    implements Runnable {
        private ConnectChecker() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            PlayerStorage.this.connectcheckReadLock.lock();
            PlayerStorage.this.writeLock.lock();
            try {
                MapleCharacter player;
                if (ShutdownServer.getInstance().isShutdown()) {
                    return;
                }
                Iterator<MapleCharacter> chrit = PlayerStorage.this.nameToChar.values().iterator();
                LinkedHashMap<Integer, MapleCharacter> disconnectList = new LinkedHashMap<Integer, MapleCharacter>();
                while (chrit.hasNext()) {
                    player = chrit.next();
                    if (player == null || player.getClient() != null && player.getClient().getSession() != null && player.getClient().getSession().isActive()) continue;
                    disconnectList.put(player.getId(), player);
                }
                Iterator dcitr = disconnectList.values().iterator();
                while (dcitr.hasNext()) {
                    player = (MapleCharacter)dcitr.next();
                    if (player == null) continue;
                    if (player.getClient() != null && player.getClient().getSession() != null) {
                        player.getClient().getSession().close();
                    }
                    PlayerStorage.this.disconnectPlayer(player);
                    dcitr.remove();
                }
            }
            finally {
                PlayerStorage.this.writeLock.unlock();
                PlayerStorage.this.connectcheckReadLock.unlock();
            }
        }
    }
}

