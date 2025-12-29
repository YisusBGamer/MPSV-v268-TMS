/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.AramiaFireWorks
 *  Net.server.shops.HiredFisher
 *  Net.server.shops.HiredMerchant
 *  Server.world.CheaterData
 */
package Server.channel;

import Client.MapleCharacter;
import Config.configs.ServerConfig;
import Net.server.events.MapleCoconut;
import Net.server.events.MapleEvent;
import Net.server.events.MapleEventType;
import Net.server.events.MapleFitness;
import Net.server.events.MapleOla;
import Net.server.events.MapleOxQuiz;
import Net.server.events.MapleSnowball;
import Net.server.events.MapleSurvival;
import Net.server.life.PlayerNPC;
import Net.server.maps.AramiaFireWorks;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapFactory;
import Net.server.market.MarketEngine;
import Net.server.shops.HiredFisher;
import Net.server.shops.HiredMerchant;
import Packet.MaplePacketCreator;
import Plugin.script.binding.ScriptEvent;
import Server.ServerType;
import Server.channel.MerchantStorage;
import Server.channel.PlayerStorage;
import Server.channel.handler.HiredFisherStorage;
import Server.login.LoginServer;
import Server.netty.ServerConnection;
import Server.world.CheaterData;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelServer {
    private static final Logger log = LoggerFactory.getLogger(ChannelServer.class);
    private static final Map<Integer, ChannelServer> instances = new HashMap<Integer, ChannelServer>();
    public static long serverStartTime;
    private final MapleMapFactory mapFactory;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<MapleEventType, MapleEvent>(MapleEventType.class);
    private int channel;
    private final int flags = 0;
    private final MarketEngine me = new MarketEngine();
    private final List<PlayerNPC> playerNPCs = new LinkedList<PlayerNPC>();
    private ServerConnection init;
    private int doubleExp = 1;
    private short port;
    private volatile boolean shutdown = false;
    private volatile boolean finishedShutdown = false;
    private volatile boolean MegaphoneMuteState = false;
    private PlayerStorage players;
    private MerchantStorage merchants;
    private HiredFisherStorage fishers;
    private ScriptEvent eventSM;
    private int eventmap = -1;
    private final AtomicInteger runningIdx = new AtomicInteger(0);
    private static final ExecutorService saveExecutor;

    private ChannelServer(int channel) {
        this.channel = channel;
        this.mapFactory = new MapleMapFactory(channel);
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<Integer>(instances.keySet());
    }

    public static ChannelServer newInstance(int channel) {
        return new ChannelServer(channel);
    }

    public static ChannelServer getInstance(int channel) {
        return instances.get(channel);
    }

    public static List<ChannelServer> getAllInstances() {
        return new ArrayList<ChannelServer>(instances.values());
    }

    public static void startChannel_Main() {
        serverStartTime = System.currentTimeMillis();
        int ch = Math.min(ServerConfig.CHANNELS_PER_WORLD, 40);
        for (int i = 1; i <= ch; ++i) {
            ChannelServer.newInstance(i).run_startup_configurations();
        }
        log.info("所有頻道已啟動完成.");
    }

    public static Map<Integer, Integer> getChannelLoad() {
        return instances.values().stream().collect(Collectors.toMap(ChannelServer::getChannel, ChannelServer::getConnectedClients));
    }

    public static MapleCharacter getCharacterById(int id) {
        return instances.values().stream().map(cserv -> cserv.getPlayerStorage().getCharacterById(id)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static MapleCharacter getCharacterByName(String name) {
        return instances.values().stream().map(cserv -> cserv.getPlayerStorage().getCharacterByName(name)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static int getChannelStartPort() {
        return ServerConfig.CHANNEL_START_PORT;
    }

    public void loadEvents() {
        if (!this.events.isEmpty()) {
            return;
        }
        this.events.put(MapleEventType.CokePlay, new MapleCoconut(this.channel, MapleEventType.CokePlay));
        this.events.put(MapleEventType.Coconut, new MapleCoconut(this.channel, MapleEventType.Coconut));
        this.events.put(MapleEventType.Fitness, new MapleFitness(this.channel, MapleEventType.Fitness));
        this.events.put(MapleEventType.OlaOla, new MapleOla(this.channel, MapleEventType.OlaOla));
        this.events.put(MapleEventType.OxQuiz, new MapleOxQuiz(this.channel, MapleEventType.OxQuiz));
        this.events.put(MapleEventType.Snowball, new MapleSnowball(this.channel, MapleEventType.Snowball));
        this.events.put(MapleEventType.Survival, new MapleSurvival(this.channel, MapleEventType.Survival));
    }

    public MapleEvent getEvent(MapleEventType t) {
        return this.events.get((Object)t);
    }

    public void run_startup_configurations() {
        this.setChannel(this.channel);
        try {
            this.players = new PlayerStorage(this.channel);
            this.merchants = new MerchantStorage(this.channel);
            this.fishers = new HiredFisherStorage(this.channel);
            this.port = (short)(ChannelServer.getChannelStartPort() + this.channel - 1);
            this.init = new ServerConnection(this.port, 0, this.channel, ServerType.ChannelServer);
            this.init.run();
            log.info("CHANNEL {} listening on port: {}", (Object)this.channel, (Object)this.port);
            this.loadEvents();
        }
        catch (Exception e) {
            throw new RuntimeException("綁定連接埠: " + this.port + " 失敗 (ch: " + this.getChannel() + ")", e);
        }
    }

    public void shutdown() {
        if (this.finishedShutdown) {
            return;
        }
        this.shutdown = true;
        log.info("Channel {} saving map information", (Object)this.channel);
        this.mapFactory.getAllMaps().forEach(MapleMap::saveBreakTimeFieldStep);
        log.info("Channel {} cleaning event scripts", (Object)this.channel);
        log.info("Channel {} unbinding port", (Object)this.channel);
        this.init.close();
        instances.remove(this.channel);
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(int channel) {
        instances.put(channel, this);
        LoginServer.addChannel(channel);
    }

    public MapleMapFactory getMapFactory() {
        return this.mapFactory;
    }

    public void addPlayer(MapleCharacter chr) {
        this.getPlayerStorage().registerPlayer(chr);
    }

    public PlayerStorage getPlayerStorage() {
        if (this.players == null) {
            this.players = new PlayerStorage(this.channel);
        }
        return this.players;
    }

    public void removePlayer(MapleCharacter chr) {
        this.removePlayer(chr.getId());
    }

    public void removePlayer(int idz) {
        this.getPlayerStorage().deregisterPlayer(idz);
    }

    public String getServerMessage() {
        return ServerConfig.EVENT_MSG;
    }

    public void setServerMessage(String newMessage) {
        this.broadcastPacket(MaplePacketCreator.serverMessage(newMessage));
    }

    public void broadcastPacket(byte[] data) {
        this.getPlayerStorage().broadcastPacket(data);
    }

    public void broadcastSmegaPacket(byte[] data) {
        this.getPlayerStorage().broadcastSmegaPacket(data);
    }

    public void broadcastGMPacket(byte[] data) {
        this.getPlayerStorage().broadcastGMPacket(data);
    }

    public boolean isShutdown() {
        return this.shutdown;
    }

    public int getLoadedMaps() {
        return this.mapFactory.getLoadedMaps();
    }

    public ScriptEvent getEventSM() {
        return this.eventSM;
    }

    public int getBaseExpRate() {
        return ServerConfig.CHANNEL_RATE_BASEEXP;
    }

    public int getExpRate() {
        return ServerConfig.CHANNEL_RATE_EXP;
    }

    public void setExpRate(int rate) {
        ServerConfig.CHANNEL_RATE_EXP = rate;
    }

    public int getMesoRate() {
        return ServerConfig.CHANNEL_RATE_MESO;
    }

    public void setMesoRate(int rate) {
        ServerConfig.CHANNEL_RATE_MESO = rate;
    }

    public int getDropRate() {
        return ServerConfig.CHANNEL_RATE_DROP;
    }

    public void setDropRate(int rate) {
        ServerConfig.CHANNEL_RATE_DROP = rate;
    }

    public int getDropgRate() {
        return ServerConfig.CHANNEL_RATE_GLOBALDROP;
    }

    public void setDropgRate(int rate) {
        ServerConfig.CHANNEL_RATE_GLOBALDROP = rate;
    }

    public int getDoubleExp() {
        if (this.doubleExp < 0 || this.doubleExp > 2) {
            return 1;
        }
        return this.doubleExp;
    }

    public void setDoubleExp(int doubleExp) {
        this.doubleExp = doubleExp < 0 || doubleExp > 2 ? 1 : doubleExp;
    }

    public void closeAllMerchants() {
        this.merchants.closeAllMerchants();
    }

    public int addMerchant(HiredMerchant hMerchant) {
        return this.merchants.addMerchant(hMerchant);
    }

    public void removeMerchant(HiredMerchant hMerchant) {
        this.merchants.removeMerchant(hMerchant);
    }

    public boolean containsMerchant(int accId) {
        return this.merchants.containsMerchant(accId);
    }

    public boolean containsMerchant(int accId, int chrId) {
        return this.merchants.containsMerchant(accId, chrId);
    }

    public List<HiredMerchant> searchMerchant(int itemSearch) {
        return this.merchants.searchMerchant(itemSearch);
    }

    public HiredMerchant getHiredMerchants(int accId, int chrId) {
        return this.merchants.getHiredMerchants(accId, chrId);
    }

    public void closeAllFisher() {
        this.fishers.closeAllFisher();
    }

    public int addFisher(HiredFisher hiredFisher) {
        return this.fishers.addFisher(hiredFisher);
    }

    public void removeFisher(HiredFisher hiredFisher) {
        this.fishers.removeFisher(hiredFisher);
    }

    public boolean containsFisher(int accId, int chrId) {
        return this.fishers.containsFisher(accId, chrId);
    }

    public HiredFisher getHiredFisher(int accId, int chrId) {
        return this.fishers.getHiredFisher(accId, chrId);
    }

    public void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public boolean getMegaphoneMuteState() {
        return this.MegaphoneMuteState;
    }

    public int getEvent() {
        return this.eventmap;
    }

    public void setEvent(int ze) {
        this.eventmap = ze;
    }

    public Collection<PlayerNPC> getAllPlayerNPC() {
        return this.playerNPCs;
    }

    public void addPlayerNPC(PlayerNPC npc) {
        if (this.playerNPCs.contains(npc)) {
            return;
        }
        this.playerNPCs.add(npc);
        this.getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public void removePlayerNPC(PlayerNPC npc) {
        if (this.playerNPCs.contains(npc)) {
            this.playerNPCs.remove(npc);
            this.getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }

    public String getServerName() {
        return ServerConfig.SERVER_NAME;
    }

    public void setServerName(String sn) {
        ServerConfig.SERVER_NAME = sn;
    }

    public String getTrueServerName() {
        return ServerConfig.SERVER_NAME.substring(0, ServerConfig.SERVER_NAME.length() - 3);
    }

    public int getPort() {
        return this.port;
    }

    public void setShutdown() {
        this.shutdown = true;
        this.finishedShutdown = true;
        log.info("頻道 " + this.channel + " 已關閉完成.");
    }

    public boolean hasFinishedShutdown() {
        return this.finishedShutdown;
    }

    public int getTempFlag() {
        return 0;
    }

    public int getConnectedClients() {
        return this.getPlayerStorage().getConnectedClients();
    }

    public List<CheaterData> getCheaters() {
        List<CheaterData> cheaters = this.getPlayerStorage().getCheaters();
        Collections.sort(cheaters);
        return cheaters;
    }

    public List<CheaterData> getReports() {
        List<CheaterData> cheaters = this.getPlayerStorage().getReports();
        Collections.sort(cheaters);
        return cheaters;
    }

    public void broadcastPacket(ByteBuffer data) {
        this.getPlayerStorage().broadcastPacket(data.array());
    }

    public void broadcastSmegaPacket(ByteBuffer data) {
        this.getPlayerStorage().broadcastSmegaPacket(data.array());
    }

    public void broadcastGMPacket(ByteBuffer data) {
        this.getPlayerStorage().broadcastGMPacket(data.array());
    }

    public void broadcastMapAreaMessage(int area, ByteBuffer message) {
        for (MapleMap load : this.getMapFactory().getAllMaps()) {
            if (load.getId() / 10000000 != area || load.getCharactersSize() <= 0) continue;
            load.broadcastMessage(message.array());
        }
    }

    public void startMapEffect(String msg, int itemId) {
        this.startMapEffect(msg, itemId, 10);
    }

    public void startMapEffect(String msg, int itemId, int time) {
        for (MapleMap load : this.getMapFactory().getAllMaps()) {
            if (load.getCharactersSize() <= 0) continue;
            load.startMapEffect(msg, itemId, time);
        }
    }

    public AramiaFireWorks getFireWorks() {
        return this.getFireWorks();
    }

    public boolean isConnected(String name) {
        return this.getPlayerStorage().getCharacterByName(name) != null;
    }

    public MarketEngine getMarket() {
        return this.me;
    }

    public int getSessionIdx() {
        return this.runningIdx.getAndIncrement();
    }

    public static ExecutorService getSaveExecutor() {
        return saveExecutor;
    }

    public ChannelType getChannelType() {
        String[] chList = new String[]{};
        int i = 0;
        for (String chArrs : chList) {
            for (String string : chArrs.split(",")) {
            }
            ++i;
        }
        return ChannelType.NORMAL;
    }

    public static void main(String[] args) {
        ChannelServer.startChannel_Main();
    }

    static {
        saveExecutor = Executors.newSingleThreadExecutor();
    }

    public static enum ChannelType {
        NORMAL(1),
        CHAOS(2),
        ABNORMAL(4),
        MVP_BRONZE(8),
        MVP_SILVER(16),
        MVP_GOLD(32),
        MVP_DIAMOND(64),
        MVP_RED(128);

        private final int type;

        private ChannelType(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public static ChannelType getByType(int type) {
            for (ChannelType ct : ChannelType.values()) {
                if (ct.getType() != type) continue;
                return ct;
            }
            return NORMAL;
        }

        public boolean check(int type) {
            return (type & this.getType()) != 0;
        }
    }
}

