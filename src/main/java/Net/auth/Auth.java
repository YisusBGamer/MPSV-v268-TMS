/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.auth.Auth$AuthReply
 *  Net.auth.client.AuthServer
 *  Net.auth.packet.AuthPacket
 *  Net.auth.util.RSAUtil
 */
package Net.auth;

import Net.auth.Auth;
import Net.auth.client.AuthServer;
import Net.auth.packet.AuthPacket;
import Net.auth.util.RSAUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.MaplePacketReader;

/*
 * Exception performing whole class analysis ignored.
 */
public final class Auth {
    private static final Logger log = LoggerFactory.getLogger(Auth.class);
    private static Integer limit;
    private static Long flag;
    private static byte[] mapleAESKey;
    private static byte[] opcodeEncryptionData;
    private static final byte[] encryptedUUID;
    private static final byte[] encryptedMachineCode;
    private static String signedUUID;
    private static String signedMachineCode;
    private static long deadLine;
    private static final boolean startFinish = false;
    static final Map<String, String> CLOUD_SCRIPTS;
    static final Set<String> PERMISSIONS;
    static final Set<Integer> FORBIDDEN_MOBS;

    static void handleMachineCodeResult(MaplePacketReader pr, AuthServer c) {
        byte type = pr.readByte();
        if (type > AuthReply.values().length || type < 0) {
            c.disconnect();
            return;
        }
        switch (AuthReply.values()[type].ordinal()) {
            case 4: {
                signedMachineCode = pr.readMapleAsciiString();
                signedUUID = pr.readMapleAsciiString();
                if (!Auth.checkSign()) break;
                log.info("Successfully authorized.");
                c.announce(AuthPacket.startServerRequest((long)c.getTick()));
                return;
            }
            case 1: {
                try {
                    Class.forName("Net.auth.Register").getDeclaredConstructor(AuthServer.class).newInstance(c);
                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalAccessError();
                }
            }
            case 3: {
                log.info("Your authorization has been banned.");
                break;
            }
            case 2: {
                log.info("Outdated authorization.");
                break;
            }
        }
        c.disconnect();
        System.exit(0);
    }

    static void handleAuthChangeResult(MaplePacketReader pr, AuthServer c) {
        byte type = pr.readByte();
        if (type > AuthReply.values().length || type < 0) {
            c.disconnect();
            return;
        }
        switch (AuthReply.values()[type].ordinal()) {
            case 5: {
                signedMachineCode = pr.readMapleAsciiString();
                signedUUID = pr.readMapleAsciiString();
                if (!Auth.checkSign()) break;
                log.info("Success.");
                c.announce(AuthPacket.startServerRequest((long)c.getTick()));
                return;
            }
            case 6: {
                long time = pr.readLong();
                log.info("Next authorized change time: " + new SimpleDateFormat("yyyyMMdd").format(new Date(time)));
                break;
            }
            case 1: {
                log.info("You are not authorized. Please contact for authorization.");
                break;
            }
            case 0: {
                log.info("Invalid serial number.");
                break;
            }
            case 3: {
                log.info("You have been banned from the service.");
                break;
            }
            case 2: {
                log.info("Authorization has expired. Please contact for renewal.");
                break;
            }
        }
        c.disconnect();
        System.exit(0);
    }

    static boolean checkSign() {
        if (signedUUID == null || signedMachineCode == null) {
            return false;
        }
        try {
            return RSAUtil.verify((byte[])encryptedUUID, (String)signedUUID) && RSAUtil.verify((byte[])encryptedMachineCode, (String)signedMachineCode);
        }
        catch (Exception e) {
            throw new RuntimeException("RSA Verify Error");
        }
    }

    public static void startServer() {
        if (!Auth.checkSign()) {
            throw new IllegalStateException();
        }
        log.info("AUTH_KEY-無效。伺服器無法啟動。");
    }

    private static boolean AuthKEY() {
        String key = "OQKW-OQKW-OQKW-OQKW-48";
        return key.equals(key);
    }

    public static int getLimit() {
        return limit;
    }

    public static void setLimit(int limit) {
        Auth.limit = limit;
    }

    public static long getFlag() {
        return flag;
    }

    static void setFlag(long flag) {
        Auth.flag = flag;
    }

    public static byte[] getMapleAESKey() {
        return mapleAESKey;
    }

    static void setMapleAESKey(byte[] mapleAESKey) {
        Auth.mapleAESKey = mapleAESKey;
    }

    public static byte[] getOpcodeEncryptionData() {
        return opcodeEncryptionData;
    }

    static void setOpcodeEncryptionData(byte[] opcodeEncryptionData) {
        Auth.opcodeEncryptionData = opcodeEncryptionData;
    }

    public static byte[] getEncryptedUUID() {
        return encryptedUUID;
    }

    public static byte[] getEncryptedMachineCode() {
        return encryptedMachineCode;
    }

    static void setDeadLine(long deadLine) {
        Auth.deadLine = deadLine;
    }

    public static long getDeadLine() {
        return deadLine;
    }

    public static void reportAttackError(int skillId, byte[] packet) {
        AuthServer.getInstance().announce(AuthPacket.reportAttackErrorRequest((int)skillId, (byte[])packet));
    }

    public static boolean checkPermission(String key) {
        return true;
    }

    public static String getCloudScript(String path) {
        return CLOUD_SCRIPTS.get(path);
    }

    public static boolean isForbiddenMob(int mobId) {
        return FORBIDDEN_MOBS.contains(mobId);
    }

    static {
        mapleAESKey = null;
        opcodeEncryptionData = null;
        signedUUID = null;
        signedMachineCode = null;
        CLOUD_SCRIPTS = new HashMap<String, String>();
        PERMISSIONS = new HashSet<String>();
        FORBIDDEN_MOBS = new HashSet<Integer>();
        try {
            encryptedMachineCode = new byte[0];
            encryptedUUID = new byte[0];
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private enum AuthReply {
        InValidKey,
        UnAuthorized,
        Outdated,
        Banned,
        Success,
        Changed,
        Forbidden,
    }

}

