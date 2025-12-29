/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Config.configs.OpcodeConfig
 */
package Server.netty;

import Client.MapleClient;
import Config.$Crypto.MapleAESOFB;
import Config.configs.Config;
import Config.configs.OpcodeConfig;
import Config.configs.ServerConfig;
import Config.constants.ServerConstants;
import Opcode.header.OutHeader;
import Server.MapleServerHandler;
import Server.ServerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.HexTool;
import tools.StringUtil;
import tools.data.ByteArrayByteStream;
import tools.data.MaplePacketReader;

public class MaplePacketEncoder
extends MessageToByteEncoder<Object> {
    private static final Logger log = LoggerFactory.getLogger("DebugWindows");
    private final ServerType type;

    public MaplePacketEncoder(ServerType type) {
        this.type = type;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf buffer) throws Exception {
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null) {
            OutHeader op;
            MapleAESOFB send_crypto = client.getSendCrypto();
            byte[] input = (byte[])message;
            if (Config.isDevelop() || ServerConstants.isLogPacket()) {
                int packetLen = input.length;
                int pHeader = this.readFirstShort(input);
                Object pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                pHeaderStr = pHeader + "(0x" + StringUtil.getLeftPaddedStr((String)pHeaderStr, '0', 4) + ")";
                op = OutHeader.valueOf(OutHeader.getOpcodeName(pHeader));
                Object tab = "";
                for (int i = 4; i > op.name().length() / 8; --i) {
                    tab = (String)tab + "\t";
                }
                StringBuilder RecvTo = new StringBuilder();
                String t = packetLen >= 10 ? (packetLen >= 100 ? (packetLen >= 1000 ? "" : " ") : "  ") : "   ";
                RecvTo.append("\r\n[LP]\t").append(op.name()).append((String)tab).append("\t包頭:").append((String)pHeaderStr).append(t).append("[").append(packetLen).append("字元]");
                if (client.getPlayer() != null) {
                    RecvTo.append("角色名:").append(client.getPlayer().getName());
                }
                RecvTo.append("\r\n");
                RecvTo.append(HexTool.toString(input)).append("\r\n").append(HexTool.toStringFromAscii(input));
                if (ServerConstants.isLogPacket()) {
                    MapleServerHandler.AllPacketLog.info(RecvTo.toString());
                }
                if (Config.isDevelop() && !OpcodeConfig.isblock((String)op.name(), (boolean)true)) {
                    log.trace(RecvTo.toString());
                }
            }
            byte[] unencrypted = new byte[input.length];
            System.arraycopy(input, 0, unencrypted, 0, input.length);
            Lock mutex = client.getLock();
            mutex.lock();
            try {
                byte[] header;
                int pHeader = this.readFirstShort(input);
                op = OutHeader.valueOf(OutHeader.getOpcodeName(pHeader));
                long len = unencrypted.length;
                if (op == OutHeader.LP_UserHP) {
                    len |= 0x80000000L;
                }
                if (op == OutHeader.LP_Message) {
                    MaplePacketReader reader = new MaplePacketReader(new ByteArrayByteStream(input));
                    reader.skip(2);
                    byte type = reader.readByte();
                    if (type == 3) {
                        len |= 0x80000000L;
                    }
                }
                if ((header = send_crypto.getPacketHeader(len)).length > 4) {
                    // empty if block
                }
                if (this.type == ServerType.LoginServer) {
                    send_crypto.crypt(unencrypted);
                } else {
                    send_crypto.crypt(unencrypted, true);
                }
                int encryptCode = MaplePacketEncoder.getEncryptCode(client.getSessionIPAddress());
                if (encryptCode != 0) {
                    int i = 0;
                    while (i < header.length) {
                        int n = i++;
                        header[n] = (byte)(header[n] ^ encryptCode);
                    }
                    i = 0;
                    while (i < unencrypted.length) {
                        int n = i++;
                        unencrypted[n] = (byte)(unencrypted[n] ^ encryptCode);
                    }
                }
                buffer.writeBytes(header);
                buffer.writeBytes(unencrypted);
            }
            finally {
                mutex.unlock();
            }
        } else {
            byte[] bytes = (byte[])message;
            int encryptCode = MaplePacketEncoder.getEncryptCode(ctx.channel().remoteAddress().toString().split(":")[0].replace("/", ""));
            if (encryptCode != 0) {
                int i = 0;
                while (i < bytes.length) {
                    int n = i++;
                    bytes[n] = (byte)(bytes[n] ^ encryptCode);
                }
            }
            buffer.writeBytes(bytes);
        }
    }

    private static int getEncryptCode(String ip) {
        int encryptCode = Integer.decode(System.getProperty("packetEncryptCode", "0"));
        if (encryptCode != 0) {
            for (String host : ServerConfig.noEncryptHost_List) {
                if (!ip.equalsIgnoreCase(ServerConstants.getHostAddress(host))) continue;
                return 0;
            }
        }
        return encryptCode;
    }

    private int readFirstShort(byte[] arr) {
        return new MaplePacketReader(new ByteArrayByteStream(arr)).readShort();
    }
}

