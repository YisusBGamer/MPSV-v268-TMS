/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.data.StringObfuscation
 */
package Handler.Telemetry;

import Client.MapleClient;
import Handler.Handler;
import Opcode.header.InHeader;
import connection.InPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.StringObfuscation;

public class TelemetryHandler {
    private static final Logger logger = LoggerFactory.getLogger("ClientLog");

    @Handler(op=InHeader.PROCESS_REPORT)
    public static byte[] handleProcessReport(MapleClient client, InPacket get) {
        int type = get.decodeInt();
        System.out.println("type = " + type);
        if (type == 24) {
            int rand = get.decodeInt();
            int len = get.decodeInt() ^ rand;
            byte[] buff = get.decodeArr(len);
            TelemetryHandler.decryptProcessReport(len, rand, buff);
            InPacket iPacket = new InPacket(buff);
            int opc = iPacket.decodeInt();
            byte[] obs = iPacket.decodeRawString();
            String deobs = StringObfuscation.DeobfuscateRor((byte[])obs, (int)3);
            System.out.println(opc + "," + deobs);
            return obs;
        }
        if (type == 49) {
            String str = get.decodeString();
            System.out.println(str);
        } else if (type == 76) {
            int a = get.decodeInt();
            int b = get.decodeInt();
            System.out.println(a + "," + b);
        } else if (type == 85) {
            int a = get.decodeInt();
            System.out.println(a);
        }
        return new byte[0];
    }

    @Handler(op=InHeader.CHEAT_ENGINE)
    public static void handleCheatEngine(MapleClient client, InPacket iPacket) {
        int type = iPacket.decodeInt();
        byte[] raw = iPacket.decodeRawString();
        String clog = StringObfuscation.DeobfuscateRor((byte[])raw, (int)3);
        logger.info("客戶端回報：" + clog);
    }

    public static void decryptProcessReport(int len, int rand, byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf, 0, len);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int i = 0;
        if (len >= 4) {
            int v23 = 0;
            do {
                int currentInt = buffer.getInt();
                int xorResult = (currentInt + v23 ^ rand) + (rand >>> 6) + rand + 1285153577;
                int decryptedInt = xorResult ^ 0xA0A0B0B0;
                buffer.putInt(buffer.position() - 4, decryptedInt);
                v23 += 4 * rand;
            } while ((i += 4) + 4 <= len);
        }
        if (i < len) {
            do {
                byte currentByte = buffer.get();
                byte decryptedByte = (byte)(currentByte - i * rand + (rand >>> 6) + 39 ^ rand >>> 1);
                decryptedByte = (byte)(decryptedByte ^ 0xBC);
                buffer.put(buffer.position() - 1, decryptedByte);
            } while (++i < len);
        }
    }
}

