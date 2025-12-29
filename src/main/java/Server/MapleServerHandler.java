/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.MaplePacketHandler
 */
package Server;

import Client.MapleClient;
import Config.$Crypto.MapleAESOFB;
import Config.configs.Config;
import Config.configs.ServerConfig;
import Config.constants.ServerConstants;
import Net.server.ShutdownServer;
import Opcode.header.InHeader;
import Packet.LoginPacket;
import Server.MaplePacketHandler;
import Server.PacketProcessor;
import Server.ServerType;
import Server.cashshop.CashShopServer;
import Server.channel.ChannelServer;
import Server.login.LoginServer;
import Server.netty.MaplePacketDecoder;
import Server.world.World;
import connection.netty.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Randomizer;
import tools.data.ByteArrayByteStream;
import tools.data.MaplePacketReader;

public final class MapleServerHandler
extends ChannelInboundHandlerAdapter {
    public static final Map<String, Long> blockIPList = new HashMap<String, Long>();
    public static final Logger AllPacketLog = LoggerFactory.getLogger("AllPackets");
    public static final Logger BuffPacketLog = LoggerFactory.getLogger("BuffPackets");
    public static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static final boolean preventIpAttack = true;
    private static final Logger log = LoggerFactory.getLogger(MapleServerHandler.class);
    private static final Logger handlerLog = LoggerFactory.getLogger("HandlePacket");
    private static final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap<String, Pair<Long, Byte>>();
    private static final Logger ExceptionLog = LoggerFactory.getLogger("Exceptions");
    private static final Set<Short> UNKNOWN_PACKET = new HashSet<Short>();
    private static final Map<ServerType, MaplePacketHandler[]> handlers = new LinkedHashMap<ServerType, MaplePacketHandler[]>();
    private static long lastTrackerClearTime = 0L;
    private final int world;
    private final int channel;
    private final ServerType type;
    private static final List<InHeader> alreadyLoggedOpcode = new LinkedList<InHeader>();

    public MapleServerHandler(int world, int channel, ServerType type) {
        this.world = world;
        this.channel = channel;
        this.type = type;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ShutdownServer.getInstance().isShutdown()) {
            ctx.channel().close();
            return;
        }
        String address = ctx.channel().remoteAddress().toString().split(":")[0];
        if (blockIPList.containsKey(address)) {
            if (blockIPList.get(address) <= System.currentTimeMillis()) {
                ctx.channel().close();
                return;
            }
            blockIPList.remove(address);
        }
        if (this.type == ServerType.LoginServer && !address.equals("/127.0.0.1")) {
            byte count;
            this.checkLastTrackerClear();
            Pair<Long, Byte> track = tracker.get(address);
            if (track == null) {
                count = 1;
            } else {
                count = (Byte)track.right;
                long difference = System.currentTimeMillis() - (Long)track.left;
                if (difference < 10000L) {
                    count = (byte)(count + 1);
                }
                if (count > 5) {
                    blockIPList.put(address, System.currentTimeMillis() + 600000L);
                    tracker.remove(address);
                    ctx.channel().close();
                    return;
                }
            }
            tracker.put(address, new Pair<Long, Byte>(System.currentTimeMillis(), count));
        }
        if (this.serverIsShutdown()) {
            ctx.channel().close();
            return;
        }
        byte[] ivRecv = new byte[]{(byte)(Math.random() * 255.0), (byte)(Math.random() * 255.0), (byte)(Math.random() * 255.0), (byte)(Math.random() * 255.0)};
        byte[] ivSend = new byte[]{(byte)(Math.random() * 255.0), (byte)(Math.random() * 255.0), (byte)(Math.random() * 255.0), (byte)(Math.random() * 255.0)};
        MapleAESOFB sendCypher = new MapleAESOFB(ivSend, (short)268, true);
        MapleAESOFB recvCypher = new MapleAESOFB(ivRecv, (short)268, false);
        MapleClient client = new MapleClient(sendCypher, recvCypher, ctx.channel());
        client.setSessionId(Randomizer.nextLong());
        client.setChannel(this.channel);
        client.setWorldId(this.world);
        int encryptCode = Integer.decode(System.getProperty("ivEncryptCode", "0"));
        if (encryptCode != 0) {
            for (String host : ServerConfig.noEncryptHost_List) {
                if (!address.replace("/", "").equalsIgnoreCase(ServerConstants.getHostAddress(host))) continue;
                encryptCode = 0;
            }
        }
        byte[] fakeIVRecv = new byte[ivRecv.length];
        System.arraycopy(ivRecv, 0, fakeIVRecv, 0, ivRecv.length);
        byte[] fakeIVSend = new byte[ivSend.length];
        System.arraycopy(ivSend, 0, fakeIVSend, 0, ivSend.length);
        if (encryptCode != 0) {
            int i = 0;
            while (i < fakeIVRecv.length) {
                int n = i++;
                fakeIVRecv[n] = (byte)(fakeIVRecv[n] ^ encryptCode);
            }
            i = 0;
            while (i < fakeIVSend.length) {
                int n = i++;
                fakeIVSend[n] = (byte)(fakeIVSend[n] ^ encryptCode);
            }
        }
        ctx.channel().writeAndFlush(LoginPacket.getHello((short)268, fakeIVSend, fakeIVRecv, this.type));
        if (this.channel > -1) {
            client.setSessionIdx(ChannelServer.getInstance(this.channel).getSessionIdx());
        } else {
            client.setSessionIdx(0);
        }
        ctx.channel().attr(MapleClient.CLIENT_KEY).set(client);
        StringBuilder sb = new StringBuilder();
        if (this.channel > -1) {
            sb.append(String.format("[Channel Server] Channel %d : ", this.channel));
        } else if (this.type == ServerType.CashShopServer) {
            sb.append("[Cash Server] ");
        } else if (this.type == ServerType.ChatServer) {
            sb.append("[Chat Server] ");
        } else {
            sb.append("[Login Server] ");
        }
        World.Client.addClient(client);
        sb.append(String.format("GET PLAYER TCP %s", ctx.channel().remoteAddress()));
        System.out.println(sb);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        try {
            if (client != null) {
                client.disconnect(true, ServerType.CashShopServer.equals((Object)this.type));
                World.Client.removeClient(client);
            }
        }
        catch (Throwable t) {
            log.error("連接異常關閉", t);
        }
        finally {
            ctx.channel().attr(MapleClient.CLIENT_KEY).set(null);
            ctx.channel().attr(MaplePacketDecoder.DECODER_STATE_KEY).set(null);
            ctx.channel().close();
            log.error("已断开");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MaplePacketReader slea = new MaplePacketReader(new ByteArrayByteStream((byte[])msg));
        if (slea.available() < 2L) {
            return;
        }
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (ChannelHandler.handlePacket(this, slea, client)) {
            return;
        }
        MapleServerHandler.handlePacket(slea, client, this.type);
    }

    private static void handlePacket(MaplePacketReader slea, MapleClient client, ServerType type) throws IOException {
        if (client == null || !client.isReceiving()) {
            return;
        }
        short packetId = slea.readShort();
        InHeader opcode = MapleServerHandler.lookupRecv(packetId);
        if (opcode == null) {
            System.err.printf("[In] %d(0x%s)%n", packetId, Integer.toHexString(packetId));
        } else {
            PacketProcessor.getProcessor(opcode, slea, type, client);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        MapleClient client;
        if (cause.getMessage().contains("強制關閉來自異常的連線。")) {
            ctx.channel().close();
            return;
        }
        if (Config.isDevelop() && (client = ctx.channel().attr(MapleClient.CLIENT_KEY).get()) != null) {
            if (client.getPlayer() != null) {
                ExceptionLog.error("帳號：" + client.getAccountName() + " 角色：" + client.getPlayer().getName() + " 地圖：" + client.getPlayer().getMapId(), cause);
            } else {
                ExceptionLog.error("帳號：" + client.getAccountName(), cause);
            }
        }
        if (cause instanceof IOException || cause instanceof ClassCastException) {
            ctx.channel().close();
            return;
        }
        client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null) {
            if (client.getPlayer() != null) {
                client.getPlayer().saveToDB(true, this.type == ServerType.CashShopServer);
                log.error("發現異常，角色：" + client.getPlayer().getName() + " 地圖：" + client.getPlayer().getMapId(), cause);
            } else {
                log.error("發現異常，帳號：" + client.getAccountName(), cause);
            }
        }
        ctx.channel().close();
    }

    private void checkLastTrackerClear() {
        if (System.currentTimeMillis() - lastTrackerClearTime >= 3600000L) {
            lastTrackerClearTime = System.currentTimeMillis();
            tracker.clear();
        }
    }

    private static InHeader lookupRecv(short header) {
        InHeader recv = InHeader.valueOf(InHeader.getOpcodeName(header));
        return recv == null ? InHeader.UNKNOWN : recv;
    }

    public static void reloadHandlers() {
        handlers.clear();
    }

    private boolean serverIsShutdown() {
        if (this.channel > -1) {
            return ChannelServer.getInstance(this.channel).isShutdown();
        }
        if (this.type == ServerType.CashShopServer) {
            return CashShopServer.isShutdown();
        }
        if (this.type == ServerType.LoginServer) {
            return LoginServer.isShutdown();
        }
        return false;
    }
}

