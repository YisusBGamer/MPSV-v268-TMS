/*
 * Decompiled with CFR 0.152.
 */
package Server.login;

import Client.MapleClient;
import Client.MapleEnumClass;
import Config.configs.ServerConfig;
import Handler.Login.LoginHandler;
import Net.server.Timer;
import Packet.LoginPacket;
import Packet.MaplePacketCreator;
import Server.channel.ChannelServer;
import Server.login.LoginServer;
import SwordieX.enums.LoginType;
import connection.packet.Login;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginWorker {
    private static final Logger log = LoggerFactory.getLogger(LoginWorker.class);
    private static long lastUpdate = 0L;

    public static void registerClient(MapleClient c, boolean useKey) {
        if (ServerConfig.WORLD_ONLYADMIN && !c.isGm() && !c.isLocalhost()) {
            c.announce(MaplePacketCreator.serverNotice(1, "服務器維護中."));
            c.announce(LoginPacket.getLoginFailed(MapleEnumClass.AuthReply.GAME_DEFINITION_INFO));
            return;
        }
        if (System.currentTimeMillis() - lastUpdate > 600000L) {
            lastUpdate = System.currentTimeMillis();
            Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) {
                lastUpdate = 0L;
                c.announce(LoginPacket.getLoginFailed(MapleEnumClass.AuthReply.GAME_CONNECTING_ACCOUNT));
                return;
            }
            double loadFactor = 32766.0 / ((double)LoginServer.getUserLimit() / (double)load.size() / 100.0);
            for (Map.Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue().intValue();
                load.put(entry.getKey(), Math.min(32766, (int)((double)entry.getValue().intValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }
        if (c.finishLogin() == 0) {
            if (useKey) {
                LoginHandler.handleLogoutWorld(c, null);
                c.write(Login.sendCheckPasswordResult(LoginType.Success, c, true));
            } else {
                c.write(Login.sendCheckPasswordResult(LoginType.Success, c, false));
                LoginHandler.handleLogoutWorld(c, null);
                LoginHandler.handleWorldListRequest(c, null);
            }
            c.setIdleTask(Timer.PingTimer.getInstance().schedule(c.getSession()::close, 6000000L));
        } else {
            c.announce(LoginPacket.getLoginFailed(MapleEnumClass.AuthReply.GAME_CONNECTING_ACCOUNT));
        }
    }
}

