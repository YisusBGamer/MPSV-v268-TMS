/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.login.handler.MapleBalloon
 */
package Server.login;

import Config.configs.ServerConfig;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataProviderFactory;
import Server.ServerType;
import Server.login.handler.MapleBalloon;
import Server.netty.ServerConnection;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Quadruple;
import tools.Randomizer;

public class LoginServer {
    private static final Logger log = LoggerFactory.getLogger(LoginServer.class);
    private static final List<MapleBalloon> lBalloon = new ArrayList<MapleBalloon>();
    private static final HashMap<Integer, Quadruple<String, String, Integer, String>> loginAuth = new HashMap();
    private static final HashMap<String, Pair<String, Integer>> loginAuthKey = new HashMap();
    private static short port;
    private static ServerConnection init;
    private static Map<Integer, Integer> load;
    private static int usersOn;
    private static boolean finishedShutdown;
    private static final Map<String, List<Integer>> worldSelectBGs;
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    public static void putLoginAuth(int chrid, String ip, String tempIp, int channel, String mac) {
        loginAuth.put(chrid, new Quadruple<String, String, Integer, String>(ip, tempIp, channel, mac));
    }

    public static Quadruple<String, String, Integer, String> getLoginAuth(int chrid) {
        return loginAuth.remove(chrid);
    }

    public static void putLoginAuthKey(String key, String account, int channel) {
        loginAuthKey.put(key, new Pair<String, Integer>(account, channel));
    }

    public static Pair<String, Integer> getLoginAuthKey(String account, boolean remove) {
        if (remove) {
            return loginAuthKey.remove(account);
        }
        return loginAuthKey.get(account);
    }

    public static void addChannel(int channel) {
        load.put(channel, 0);
    }

    public static void removeChannel(int channel) {
        load.remove(channel);
    }

    public static void runStartupConfigurations() {
        LoginServer.loadWorldSelectBGs();
        LoginServer.initServerConnection(ServerConfig.LOGIN_PORT);
        LoginServer.initServerConnection(ServerConfig.LOGIN_PORT_備用);
    }

    private static void loadWorldSelectBGs() {
        MapleData data = MapleDataProviderFactory.getMap().getData("Obj/login.img").getChildByPath("WorldSelect");
        for (MapleData dat : data.getChildren()) {
            List ls;
            if (dat == null || dat.getChildren().size() <= 0 || (ls = dat.getChildren().stream().map(MapleDataEntity::getName).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList())).isEmpty()) continue;
            worldSelectBGs.put(dat.getName(), ls);
        }
    }

    private static void initServerConnection(short port) {
        try {
            init = new ServerConnection(port, -1, -1, ServerType.LoginServer);
            init.run();
            log.info("Login server listening on port: {}", (Object)port);
        }
        catch (Exception e) {
            log.error("Failed to bind login server to port: " + port, e);
        }
    }

    public static void shutdown() {
        if (!finishedShutdown) {
            log.info("Shutting down login server...");
            init.close();
            finishedShutdown = true;
        }
    }

    public static String getServerName() {
        return ServerConfig.SERVER_NAME;
    }

    public static String getTrueServerName() {
        return ServerConfig.SERVER_NAME.substring(0, ServerConfig.SERVER_NAME.length() - 3);
    }

    public static String getEventMessage() {
        return ServerConfig.LOGIN_EVENTMESSAGE;
    }

    public static void setEventMessage(String newMessage) {
        ServerConfig.LOGIN_EVENTMESSAGE = newMessage;
    }

    public static byte getFlag() {
        return ServerConfig.WORLD_ID;
    }

    public static void setFlag(byte newflag) {
        ServerConfig.WORLD_ID = newflag;
    }

    public static Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(Map<Integer, Integer> load_, int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static int getUserLimit() {
        return ServerConfig.LOGIN_USERLIMIT;
    }

    public static void setUserLimit(int newLimit) {
        ServerConfig.LOGIN_USERLIMIT = newLimit;
    }

    public static int getUsersOn() {
        return usersOn;
    }

    public static List<MapleBalloon> getBalloons() {
        return lBalloon;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }

    public static void setOn() {
        finishedShutdown = false;
    }

    public static String getRandomWorldSelectBG() {
        int day = Calendar.getInstance().get(7);
        List filteredKeys = worldSelectBGs.keySet().stream().filter(s -> !s.equalsIgnoreCase("default") && !s.equalsIgnoreCase("signboard")).filter(s -> day == 1 ? s.toLowerCase().contains("sundaymaple") : !s.toLowerCase().contains("sundaymaple")).collect(Collectors.toList());
        if (filteredKeys.isEmpty()) {
            return "default";
        }
        return (String)filteredKeys.get(Randomizer.nextInt(filteredKeys.size()));
    }

    static {
        load = new HashMap<Integer, Integer>();
        usersOn = 0;
        finishedShutdown = true;
        worldSelectBGs = new HashMap<String, List<Integer>>();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
    }
}

