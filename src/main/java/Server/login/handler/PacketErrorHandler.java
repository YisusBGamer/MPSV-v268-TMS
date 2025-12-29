/*
 * Decompiled with CFR 0.152.
 */
package Server.login.handler;

import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Config.configs.Config;
import Config.constants.SkillConstants;
import Opcode.header.OutHeader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.HexTool;
import tools.StringUtil;
import tools.data.MaplePacketReader;

public class PacketErrorHandler {
    private static final Logger log = LoggerFactory.getLogger("PacketErrorLog");
    private static final List<OutHeader> alreadyLoggedOpcode = new LinkedList<OutHeader>();

    public static void handlePacket(MaplePacketReader slea, MapleClient c) {
        if (slea.available() >= 14L) {
            short mode = slea.readShort();
            String type_str = "Unknown?!";
            switch (mode) {
                case 1: {
                    type_str = "SendBackupPacket";
                    break;
                }
                case 2: {
                    type_str = "Crash Report";
                    break;
                }
                case 3: {
                    type_str = "Exception";
                    break;
                }
            }
            int errorcode = slea.readInt();
            String errorcodeHex = "0x" + StringUtil.getLeftPaddedStr(String.valueOf(errorcode), '0', 8);
            short badPacketSize = slea.readShort();
            slea.skip(4);
            short pHeader = slea.readShort();
            String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
            pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
            OutHeader op = PacketErrorHandler.lookupSend(pHeader);
            if (!Config.isDevelop() && op != OutHeader.UNKNOWN) {
                if (alreadyLoggedOpcode.contains(op)) {
                    return;
                }
                alreadyLoggedOpcode.add(op);
            }
            int packetLen = (int)slea.available() + 2;
            byte[] packet = slea.read((int)slea.available());
            String AccountName = "null";
            String charName = "null";
            String charLevel = "null";
            Object charJob = "null";
            String Map2 = "null";
            try {
                AccountName = c.getAccountName();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                charName = c.getPlayer().getName();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                charLevel = String.valueOf(c.getPlayer().getLevel());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                charJob = MapleJob.getTrueNameById(c.getPlayer().getJobWithSub()) + "(" + String.valueOf(c.getPlayer().getJob()) + ")";
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                Map2 = c.getPlayer().getMap().toString();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            Object tab = "";
            for (int i = 4; i > op.name().length() / 8; --i) {
                tab = (String)tab + "\t";
            }
            String t = packetLen >= 10 ? (packetLen >= 100 ? (packetLen >= 1000 ? "" : " ") : "  ") : "   ";
            log.error("\r\n帳號:" + AccountName + "\r\n角色:" + charName + "(等級:" + charLevel + ")\r\n職業:" + (String)charJob + "\r\n地圖:" + Map2 + "\r\n錯誤類型: " + type_str + "(" + mode + ")\n錯誤碼: " + errorcodeHex + "(" + errorcode + ")\n\r\n[LP]\t" + op.name() + (String)tab + " \t包頭:" + pHeader + "(0x" + pHeaderStr + ")" + t + "[" + (badPacketSize - 4) + "字元]\r\n\r\n" + (String)(packet.length < 1 ? "" : HexTool.toString(packet) + "\r\n") + (String)(packet.length < 1 ? "" : HexTool.toStringFromAscii(packet) + "\r\n"));
        }
    }

    private static OutHeader lookupSend(int val) {
        for (OutHeader op : OutHeader.values()) {
            if (op.getValue() != val) continue;
            return op;
        }
        return OutHeader.UNKNOWN;
    }

    public static void handleErrorPacket(MaplePacketReader slea, OutHeader op, StringBuilder sb) {
        if (Objects.requireNonNull(op) == OutHeader.LP_UserEnterField) {
            slea.readLong();
            int chrId = slea.readInt();
            int level = slea.readInt();
            String chrName = slea.readMapleAsciiString();
            String ultExplorer = slea.readMapleAsciiString();
            int guildId = slea.readInt();
            String guildName = slea.readMapleAsciiString();
            short guildLogoBG = slea.readShort();
            byte guildLogoBGColor = slea.readByte();
            short guildLogo = slea.readShort();
            byte guildLogoColor = slea.readByte();
            int guildId2 = slea.readInt();
            slea.readInt();
            byte gender = slea.readByte();
            int popularity = slea.readInt();
            slea.readInt();
            slea.readByte();
            slea.readInt();
            sb.append("Caused by player: ").append(chrName).append("\r\n");
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < 33; ++i) {
                int mask = slea.readInt();
                for (SecondaryStat cts : SecondaryStat.values()) {
                    if (!SkillConstants.isShowForgenBuff(cts) || cts.getPosition() != i || (cts.getValue() & mask) == 0 || SecondaryStat.getSpawnList().containsKey(cts)) continue;
                    list.add(cts.name());
                }
            }
            sb.append("BuffStat: ").append(list);
        }
    }

    public static void main(String[] args) {
        String packetStr = "10 D2 C6 B0 C8 B3 D7 01 FE 4E DD 00 78 00 00 00 0A 00 41 41 41 44 44 45 45 45 4C 45 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 A2 15 00 00 00 00 3C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 A1 00 00 04 08 00 00 00 04 00 00 00 00 00 00 00 10 00 00 01 00 00 00 00 00 00 08 00 00 00 00 00 00 00 00 00 00 FC 1F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 96 57 76 E4 00 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 01 37 4A 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 64 08 3B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 0C 3A C7 00 00 08 3B 00 00 00 14 BE 00 00 05 C4 E2 0F 00 06 03 31 10 00 07 91 5C 10 00 08 38 83 10 00 0A 13 A9 14 00 0B 50 82 12 00 15 9E 71 11 00 23 98 2A 12 00 FF FF FF FF 00 00 00 00 50 82 12 00 13 A9 14 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7B 56 02 00 00 00 00 00 00 00 00 00 00 FF 00 00 00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 FF FF 00 00 00 00 00 00 E7 00 EA 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 00 00 9B 0A 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF";
        MaplePacketReader slea = new MaplePacketReader(HexTool.getByteArrayFromHexString(packetStr));
        StringBuilder sb = new StringBuilder();
        PacketErrorHandler.handleErrorPacket(slea, OutHeader.LP_UserEnterField, sb);
        System.out.println(sb);
    }
}

