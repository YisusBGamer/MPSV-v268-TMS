/*
 * Decompiled with CFR 0.152.
 */
package Server.cashshop;

import Config.configs.ServerConfig;
import Server.ServerType;
import Server.channel.PlayerStorage;
import Server.netty.ServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CashShopServer {
    private static final Logger log = LoggerFactory.getLogger(CashShopServer.class);
    private static ServerConnection init;
    private static PlayerStorage players;
    private static boolean finishedShutdown;
    private static short port;

    public static void run_startup_configurations() {
        port = ServerConfig.CASH_PORT;
        players = new PlayerStorage(-10);
        try {
            init = new ServerConnection(port, 0, -10, ServerType.CashShopServer);
            init.run();
            log.info("[CASH SHOP] Start CashShopServer。");
        }
        catch (Exception e) {
            throw new RuntimeException("商城伺服器綁定連接埠 " + port + " 失敗", e);
        }
    }

    public static short getPort() {
        return port;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public static int getConnectedClients() {
        return CashShopServer.getPlayerStorage().getConnectedClients();
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        log.info("正在關閉商城伺服器...");
        players.disconnectAll();
        log.info("商城伺服器解除連接埠綁定...");
        init.close();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }

    public static String getCashBlockedMsg(int itemId) {
        switch (itemId) {
            case 5050000: 
            case 5060003: 
            case 5072000: 
            case 5073000: 
            case 5074000: 
            case 5076000: 
            case 5077000: 
            case 5079001: 
            case 5079002: 
            case 5360000: 
            case 5360014: 
            case 5360015: 
            case 5360016: 
            case 5390000: 
            case 5390001: 
            case 5390002: 
            case 5390003: 
            case 5390004: 
            case 5390005: 
            case 5390006: 
            case 5390007: 
            case 5390008: 
            case 5390010: {
                return "該道具只能通過NPC購買.";
            }
        }
        return "該道具禁止購買.";
    }

    static {
        finishedShutdown = false;
    }
}

