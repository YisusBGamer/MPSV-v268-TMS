/*
 * Decompiled with CFR 0.152.
 */
package connection.netty;

import Client.MapleCharacter;
import Client.MapleClient;
import Handler.Auth;
import Handler.EnterCashShopHandler;
import Handler.Handler;
import Handler.Login.LoginHandler;
import Handler.Monster.MonsterHandler;
import Handler.OverseasHandler;
import Handler.PartyHandler;
import Handler.Player.PlayerHandler;
import Handler.Telemetry.TelemetryHandler;
import Handler.UIHandler;
import Handler.skillHandler;
import Handler.warpToGameHandler;
import Opcode.header.InHeader;
import Server.MapleServerHandler;
import Server.channel.handler.GuildHandler;
import connection.InPacket;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.ByteArrayByteStream;
import tools.data.ByteStream;
import tools.data.MaplePacketReader;

public class ChannelHandler {
    private static final Logger log = LoggerFactory.getLogger(ChannelHandler.class);
    private static final Map<InHeader, Method> handlers = new HashMap<InHeader, Method>();

    public static void initHandlers(boolean mayOverride) {
        long start = System.currentTimeMillis();
        try {
            for (Class<?> clazz : ChannelHandler.getHandlerClasses()) {
                for (Method method : clazz.getMethods()) {
                    Handler handler = method.getAnnotation(Handler.class);
                    if (handler == null) continue;
                    boolean error = false;
                    if (method.getParameterTypes().length >= 2) {
                        if (method.getParameterTypes()[0] != MapleCharacter.class && method.getParameterTypes()[0] != MapleClient.class) {
                            error = true;
                        }
                        if (method.getParameterTypes()[1] != InPacket.class) {
                            error = true;
                        }
                        if (method.getParameterTypes().length == 3 && method.getParameterTypes()[2] != InHeader.class) {
                            error = true;
                        }
                        if (error) {
                            log.error("Invalid parameter type of handler: {}", (Object)method);
                        }
                    }
                    ChannelHandler.registerHandler(handler, method, mayOverride);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Initialized " + handlers.size() + " handlers in " + (System.currentTimeMillis() - start) + "ms.");
    }

    private static void registerHandler(Handler handler, Method method, boolean mayOverride) {
        InHeader[] headers;
        InHeader header = handler.op();
        if (header != InHeader.UNKNOWN) {
            if (handlers.containsKey(header) && !mayOverride) {
                throw new IllegalArgumentException(String.format("Multiple handlers found for header %s! Had method %s, but also found %s.", header, handlers.get(header).getName(), method.getName()));
            }
            handlers.put(header, method);
        }
        for (InHeader h : headers = handler.ops()) {
            handlers.put(h, method);
        }
    }

    private static Class<?>[] getHandlerClasses() {
        return new Class[]{LoginHandler.class, OverseasHandler.class, Auth.class, PartyHandler.class, UIHandler.class, GuildHandler.class, MapleClient.class, skillHandler.class, warpToGameHandler.class, PlayerHandler.class, MonsterHandler.class, EnterCashShopHandler.class, TelemetryHandler.class};
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean handlePacket(MapleServerHandler serverHandler, MaplePacketReader slea, MapleClient c) {
        block13: {
            byte[] now = new byte[]{};
            ByteStream bs = slea.getByteStream();
            if (bs instanceof ByteArrayByteStream) {
                now = ((ByteArrayByteStream)bs).getBytes();
            }
            InPacket inPacket = new InPacket(now);
            MapleCharacter chr = c.getPlayer();
            if (inPacket.getLength() < 2) {
                log.error("Packet too short to read opcode");
                return false;
            }
            short op = inPacket.decodeShort();
            InHeader inHeader = InHeader.valueOf(InHeader.getOpcodeName(op));
            Method method = handlers.get(inHeader);
            try {
                if (method == null) break block13;
                Class<?> clazz = method.getParameterTypes()[0];
                try {
                    if (method.getParameterTypes().length == 3) {
                        method.invoke(serverHandler, chr, inPacket, inHeader);
                    } else if (clazz == MapleClient.class) {
                        method.invoke(serverHandler, c, inPacket);
                    } else if (clazz == MapleCharacter.class) {
                        method.invoke(serverHandler, chr, inPacket);
                    } else {
                        log.error("Unhandled first param type of handler " + method.getName() + ", type = " + String.valueOf(clazz));
                    }
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                boolean bl = true;
                return bl;
            }
            finally {
                inPacket.release();
            }
        }
        return false;
    }
}

