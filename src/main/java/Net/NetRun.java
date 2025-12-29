/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  lombok.Generated
 */
package Net;

import Client.inventory.MapleInventoryIdentifier;
import Client.skills.SkillFactory;
import Config.configs.Config;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Net.server.AutobanManager;
import Net.server.CharacterCardFactory;
import Net.server.InitializeServer;
import Net.server.MTSStorage;
import Net.server.MapleDailyGift;
import Net.server.MapleItemInformationProvider;
import Net.server.MapleOverrideData;
import Net.server.PredictCardFactory;
import Net.server.ShutdownServer;
import Net.server.Timer;
import Net.server.carnival.MapleCarnivalFactory;
import Net.server.cashshop.CashItemFactory;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonsterInformationProvider;
import Net.server.life.PlayerNPC;
import Net.server.maps.MapleMapFactory;
import Net.server.shop.MapleShopFactory;
import Opcode.header.InHeader;
import Opcode.header.OutHeader;
import Plugin.provider.loaders.CashData;
import Plugin.provider.loaders.SkillData;
import Plugin.script.ReactorManager;
import Server.auction.AuctionServer;
import Server.cashshop.CashShopServer;
import Server.channel.ChannelServer;
import Server.login.LoginInformationProvider;
import Server.login.LoginServer;
import Server.world.WorldRespawnService;
import Server.world.guild.MapleGuild;
import SwordieX.enums.WorldId;
import SwordieX.util.Util;
import SwordieX.world.World;
import connection.crypto.MapleCrypto;
import connection.netty.ChannelHandler;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetRun {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(NetRun.class);
    private static final LocalDateTime loadStartTime = LocalDateTime.now();

    public static void main(String[] args) throws Exception {
//        System.setProperty("debug", "dev");
        Config.load();
        log.info("SpiritMS_268");
        Timer.startAll();
        Server.main(args);
        Server.getInstance().setOnline(true);
        LoginServer.setOn();
        OutHeader.startCheck();
        InHeader.startCheck();
        boolean dbInitOK = InitializeServer.initServer();
        if (!dbInitOK) {
            log.error("服務器端口初始化錯誤。");
            return;
        }
        ArrayList<CompletableFuture<Void>> loadTasks = new ArrayList<CompletableFuture<Void>>();
        if ("true".equalsIgnoreCase(System.getProperty("low.performance"))) {
            MapleItemInformationProvider.getInstance().runItems();
            MapleItemInformationProvider.getInstance().loadPotentialData();
            MapleMapFactory.loadAllLinkNpc();
            MapleMapFactory.loadAllMapName();
            CashItemFactory.getInstance().initialize();
            MapleMonsterInformationProvider.getInstance().load();
            MapleLifeFactory.loadQuestCounts();
        }
        CompletableFuture<Void> initAllDataFuture = CompletableFuture.runAsync(() -> {
            try {
                InitializeServer.initAllData((task, now, total) -> {
                    if (total.get() > 0) {
                        log.info("[初始化伺服器] 已加載 {} {} / {}", task, now, total.get());
                    }
                    if (now == total.get()) {
                        log.info("[初始化伺服器] initAllData() 所有任務已完成。");
                    }
                });
            }
            catch (Exception e) {
                log.error("[初始化伺服器] initAllData() 發生錯誤：", e);
            }
        });
        loadTasks.add(initAllDataFuture);
        loadTasks.add(CompletableFuture.runAsync(MapleOverrideData::init));
        loadTasks.add(CompletableFuture.runAsync(() -> ShutdownServer.getInstance().setShutdown(false)));
        loadTasks.add(CompletableFuture.runAsync(MapleDailyGift::initialize));
        loadTasks.add(CompletableFuture.runAsync(CashShopServer::run_startup_configurations));
        loadTasks.add(CompletableFuture.runAsync(AuctionServer.getInstance()::init));
        loadTasks.add(CompletableFuture.runAsync(MapleGuild::loadAll));
        loadTasks.add(CompletableFuture.runAsync(CashData::loadCashCommodities));
        loadTasks.add(CompletableFuture.runAsync(CashData::loadCashOldCommodities));
        loadTasks.add(CompletableFuture.runAsync(CashData::loadCashPackages));
        loadTasks.add(CompletableFuture.runAsync(CashItemFactory::getInstance));
        loadTasks.add(CompletableFuture.runAsync(LoginInformationProvider::getInstance));
        loadTasks.add(CompletableFuture.runAsync(CharacterCardFactory.getInstance()::initialize));
        loadTasks.add(CompletableFuture.runAsync(MTSStorage::load));
        loadTasks.add(CompletableFuture.runAsync(PredictCardFactory::getInstance));
        loadTasks.add(CompletableFuture.runAsync(MapleInventoryIdentifier::getInstance));
        loadTasks.add(CompletableFuture.runAsync(PlayerNPC::loadAll));
        loadTasks.add(CompletableFuture.runAsync(MapleCarnivalFactory::getInstance));
        loadTasks.add(CompletableFuture.runAsync(ItemConstants.TapJoyReward::init));
        loadTasks.add(CompletableFuture.runAsync(SkillFactory::loadSkillData));
        loadTasks.add(CompletableFuture.runAsync(SkillFactory::loadDelays));
        loadTasks.add(CompletableFuture.runAsync(SkillFactory::loadMemorySkills));
        loadTasks.add(CompletableFuture.runAsync(SkillFactory::getAllSkills));
        loadTasks.add(CompletableFuture.runAsync(SkillData::loadAllSkills));
        loadTasks.add(CompletableFuture.runAsync(LoginServer::runStartupConfigurations));
        CompletableFuture.allOf(loadTasks.toArray(new CompletableFuture[0])).join();
        log.info("所有數據加載任務均已完成。總共耗時：{} 秒。", (Object)Duration.between(loadStartTime, LocalDateTime.now()).toSeconds());
        ChannelServer.startChannel_Main();
        log.info("頻道服務器已啟動，等待連接中。");
        WorldRespawnService.getInstance();
        log.info("怪物重生系統已啟動，等待執行中。");
        ReactorManager.getInstance().clearDrops();
        log.info("反應堆掉落系統已清除掉落資料。");
        MapleShopFactory.getInstance().clear();
        MapleMonsterInformationProvider.getInstance().clearDrops();
        Timer.CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000L);
        log.info("服務器啟動完成，等待連接中。");
    }

    public static class Server
    extends Properties {
        private boolean online = false;
        private static final Server instance = new Server();
        private static final Set<Integer> users = new HashSet<Integer>();
        private static final List<World> worldList = new ArrayList<World>();

        public static Server getInstance() {
            return instance;
        }

        public List<World> getWorlds() {
            return worldList;
        }

        public World getWorldById(int id) {
            return Util.findWithPred(this.getWorlds(), w -> w.getWorldId().getVal() == id);
        }

        public static void init(String[] args) {
            System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
            ChannelHandler.initHandlers(false);
            MapleCrypto.initialize((short)268);
            worldList.add(new World(WorldId.getByVal(ServerConfig.WORLD_ID), ServerConfig.SERVER_NAME, ServerConfig.CHANNELS_PER_WORLD, ServerConfig.LOGIN_EVENTMESSAGE));
        }

        public static void main(String[] args) {
            Server.init(args);
        }

        @Generated
        public boolean isOnline() {
            return this.online;
        }

        @Generated
        public void setOnline(boolean online) {
            this.online = online;
        }
    }
}

