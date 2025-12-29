/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.world.WorldAllianceService
 *  Server.world.WorldGuildService
 */
package Net.server;

import Database.DatabaseLoader;
import Net.server.Timer;
import Server.auction.AuctionServer;
import Server.cashshop.CashShopServer;
import Server.channel.ChannelServer;
import Server.login.LoginServer;
import Server.world.WorldAllianceService;
import Server.world.WorldGuildService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownServer
implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ShutdownServer.class);
    private static final ShutdownServer instance = new ShutdownServer();
    private int time = 0;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public static ShutdownServer getInstance() {
        return instance;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public void run() {
        this.shutdown.set(true);
        log.info("正在準備關閉伺服端 " + String.valueOf(this.shutdown));
        if (!this.running.compareAndSet(false, true)) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(ChannelServer.getChannelStartPort());
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            new Thread(() -> {
                try {
                    cs.setServerMessage("遊戲伺服器將關閉維護，請玩家安全下線...");
                    cs.setShutdown();
                    cs.getPlayerStorage().disconnectAll();
                    cs.shutdown();
                    countDownLatch.countDown();
                }
                catch (Exception e) {
                    log.error("關閉伺服端錯誤", e);
                }
            }).start();
        }
        try {
            countDownLatch.await();
            ChannelServer.getSaveExecutor().shutdown();
            try {
                while (!ChannelServer.getSaveExecutor().awaitTermination(1L, TimeUnit.SECONDS)) {
                }
            }
            catch (InterruptedException e) {}
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            new Thread(() -> {
                try {
                    cs.shutdown();
                }
                catch (Exception e) {
                    log.error("關閉伺服端錯誤", e);
                }
            }).start();
        }
        WorldGuildService.getInstance().save();
        WorldAllianceService.getInstance().save();
        LoginServer.shutdown();
        CashShopServer.shutdown();
        AuctionServer.getInstance().shutdown();
        System.out.println("正在關閉時鐘線程...");
        Timer.WorldTimer.getInstance().stop();
        Timer.MapTimer.getInstance().stop();
        Timer.BuffTimer.getInstance().stop();
        Timer.CoolDownTimer.getInstance().stop();
        Timer.CloneTimer.getInstance().stop();
        Timer.EventTimer.getInstance().stop();
        Timer.EtcTimer.getInstance().stop();
        Timer.PingTimer.getInstance().stop();
        System.out.println("正在關閉資料庫連接...");
        DatabaseLoader.closeAll();
        System.out.println("伺服端關閉完成...");
        log.info("伺服端關閉完成");
        this.running.set(false);
    }

    public void setShutdown(boolean b) {
        this.shutdown.set(b);
    }

    public boolean isShutdown() {
        return this.shutdown.get();
    }

    public boolean isRunning() {
        return this.running.get();
    }
}

