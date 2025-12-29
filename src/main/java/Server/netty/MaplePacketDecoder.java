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
import Config.constants.ServerConstants;
import Opcode.header.InHeader;
import Server.MapleServerHandler;
import Server.ServerType;
import Server.login.handler.LoginPasswordHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.HexTool;
import tools.StringUtil;
import tools.data.ByteArrayByteStream;
import tools.data.MaplePacketReader;

public class MaplePacketDecoder
extends ByteToMessageDecoder {
    public static final AttributeKey<DecoderState> DECODER_STATE_KEY = AttributeKey.newInstance("MaplePacketDecoder");
    private static final Logger log = LoggerFactory.getLogger("DebugWindows");
    private final ServerType type;

    public MaplePacketDecoder(ServerType type) {
        this.type = type;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> message) throws Exception {
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        DecoderState decoderState = ctx.channel().attr(DECODER_STATE_KEY).get();
        if (decoderState == null) {
            decoderState = new DecoderState();
            ctx.channel().attr(DECODER_STATE_KEY).set(decoderState);
        }
        boolean crypt = false;
        if (in.readableBytes() >= 4 && decoderState.packetlength == -1) {
            int packetHeader = in.readInt();
            if (decoderState.tempPacket != 0) {
                decoderState.packetlength = MapleAESOFB.getLongPacketLength(decoderState.tempPacket, packetHeader);
                decoderState.tempPacket = 0;
            } else {
                int packetid = packetHeader >> 16 & 0xFFFF;
                if (this.type == ServerType.LoginServer && !client.isLoggedIn() && packetid == 26985) {
                    int packetlength = ((packetHeader & 0xFF) << 8) + (packetHeader >> 8 & 0xFF);
                    byte[] packet = new byte[packetlength];
                    in.readBytes(packet);
                    MaplePacketReader slea = new MaplePacketReader(new ByteArrayByteStream(packet));
                    LoginPasswordHandler.handlePacket(slea, client);
                    return;
                }
                if (packetHeader == -65536) {
                    crypt = true;
                }
                if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                    ctx.channel().disconnect();
                    return;
                }
                if (crypt) {
                    decoderState.packetlength = in.readableBytes();
                } else {
                    int len = MapleAESOFB.getPacketLength(packetHeader);
                    if (len == 65280) {
                        decoderState.tempPacket = packetHeader;
                    } else {
                        decoderState.packetlength = len;
                    }
                }
            }
        } else if (in.readableBytes() < 4 && decoderState.packetlength == -1) {
            return;
        }
        if (in.readableBytes() >= decoderState.packetlength) {
            byte[] decryptedPacket = new byte[decoderState.packetlength];
            in.readBytes(decryptedPacket);
            decoderState.packetlength = -1;
            if (!crypt) {
                client.getReceiveCrypto().crypt(decryptedPacket);
            } else if (this.readFirstShort(decryptedPacket) != InHeader.CTX_ENTER_ACCOUNT.getValue()) {
                return;
            }
            client.decryptOpcode(decryptedPacket);
            message.add(decryptedPacket);
            if (Config.isDevelop() || ServerConstants.isLogPacket()) {
                int packetLen = decryptedPacket.length;
                short pHeader = this.readFirstShort(decryptedPacket);
                Object pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                pHeaderStr = pHeader + "(0x" + StringUtil.getLeftPaddedStr((String)pHeaderStr, '0', 4) + ")";
                InHeader op = InHeader.valueOf(InHeader.getOpcodeName(pHeader));
                if (op == null) {
                    op = InHeader.UNKNOWN;
                }
                Object tab = "";
                for (int i = 4; i > op.name().length() / 8; --i) {
                    tab = (String)tab + "\t";
                }
                StringBuilder recvString = new StringBuilder();
                String t = packetLen >= 10 ? (packetLen >= 100 ? (packetLen >= 1000 ? "" : " ") : "  ") : "   ";
                recvString.append("\r\n").append("[CP]\t").append(op.name()).append((String)tab).append("\t包頭:").append((String)pHeaderStr).append(t).append("[").append(packetLen).append("字元]");
                if (client.getPlayer() != null) {
                    recvString.append("角色名:").append(client.getPlayer().getName());
                }
                recvString.append("\r\n");
                recvString.append(HexTool.toString(decryptedPacket)).append("\r\n");
                recvString.append(HexTool.toStringFromAscii(decryptedPacket));
                if (ServerConstants.isLogPacket()) {
                    MapleServerHandler.AllPacketLog.info(recvString.toString());
                }
                if (Config.isDevelop() && !OpcodeConfig.isblock((String)op.name(), (boolean)false)) {
                    log.trace(recvString.toString());
                }
            }
        }
    }

    private short readFirstShort(byte[] arr) {
        return new MaplePacketReader(new ByteArrayByteStream(arr)).readShort();
    }

    private static class DecoderState {
        int packetlength = -1;
        int tempPacket = 0;

        private DecoderState() {
        }
    }
}

