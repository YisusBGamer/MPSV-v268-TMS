/*
 * Decompiled with CFR 0.152.
 */
package tools;

import Config.constants.ServerConstants;
import java.io.ByteArrayOutputStream;
import tools.StringUtil;

public class HexTool {
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toString(byte byteValue) {
        int tmp = byteValue << 8;
        char[] retstr = new char[]{HEX[tmp >> 12 & 0xF], HEX[tmp >> 8 & 0xF]};
        return String.valueOf(retstr);
    }

    public static String toString(int intValue) {
        return Integer.toHexString(intValue);
    }

    public static String toString(byte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            return "";
        }
        StringBuilder hexed = new StringBuilder();
        for (byte aByte : bytes) {
            hexed.append(HexTool.toString(aByte));
            hexed.append(' ');
        }
        return hexed.substring(0, hexed.length() - 1);
    }

    public static String toStringFromAscii(byte[] bytes) {
        byte[] ret = new byte[bytes.length];
        for (int x = 0; x < bytes.length; ++x) {
            ret[x] = bytes[x] < 32 && bytes[x] >= 0 ? 46 : bytes[x];
        }
        try {
            return new String(ret, ServerConstants.MapleType.getByType(ServerConstants.MapleRegion).getCharset());
        }
        catch (Exception exception) {
            return "";
        }
    }

    public static String toPaddedStringFromAscii(byte[] bytes) {
        String str = HexTool.toStringFromAscii(bytes);
        StringBuilder ret = new StringBuilder(str.length() * 3);
        for (int i = 0; i < str.length(); ++i) {
            ret.append(str.charAt(i));
            ret.append("  ");
        }
        return ret.toString();
    }

    public static byte[] getByteArrayFromHexString(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int nexti = 0;
        int nextb = 0;
        boolean highoc = true;
        block0: while (true) {
            int number = -1;
            while (number == -1) {
                if (nexti == hex.length()) break block0;
                char chr = hex.charAt(nexti);
                number = chr >= '0' && chr <= '9' ? chr - 48 : (chr >= 'a' && chr <= 'f' ? chr - 97 + 10 : (chr >= 'A' && chr <= 'F' ? chr - 65 + 10 : -1));
                ++nexti;
            }
            if (highoc) {
                nextb = number << 4;
                highoc = false;
                continue;
            }
            highoc = true;
            baos.write(nextb |= number);
        }
        return baos.toByteArray();
    }

    public static String getOpcodeToString(int op) {
        Object hexString = Integer.toHexString(op).toUpperCase();
        while (((String)hexString).length() < 4) {
            hexString = "0" + (String)hexString;
        }
        if (((String)hexString).length() > 4) {
            hexString = ((String)hexString).substring(((String)hexString).length() - 4);
        }
        return "0x" + (String)hexString;
    }

    public static String getBuffStatToString(int buffstat) {
        String ret = "0x";
        ret = (String)ret + StringUtil.getLeftPaddedStr(Integer.toHexString(buffstat).toUpperCase(), '0', 1);
        return ret;
    }

    public static String getSubstring(String pStr, int pStart, int pEnd) {
        byte[] b = ((String)pStr).getBytes();
        pStr = "";
        int i = pStart - 1;
        int point = pStart - 1;
        while (i < pEnd && i < b.length) {
            if (i == pEnd - 1 && b[i] < 0) {
                int length = i - point + 1;
                if (length % 2 == 1) {
                    --length;
                }
                pStr = (String)pStr + new String(b, point, length);
            }
            if (b[++i] < 0) continue;
            pStr = (String)pStr + new String(b, point, i - point);
            point = i;
        }
        return pStr;
    }
}

