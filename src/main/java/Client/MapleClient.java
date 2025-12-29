/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MapleCharacterUtil
 *  Server.cashshop.handler.BuyCashItemHandler
 *  Server.login.handler.AutoRegister
 *  Server.world.WorldGuildService
 *  Server.world.WorldMessengerService
 */
package Client;

import Client.CardData;
import Client.LoginCrypto;
import Client.LoginCryptoLegacy;
import Client.MapleAntiMacro;
import Client.MapleCharacter;
import Client.MapleCharacterUtil;
import Client.MapleEnumClass;
import Client.MapleQuestStatus;
import Config.$Crypto.MapleAESOFB;
import Config.configs.ServerConfig;
import Config.constants.ServerConstants;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.NetRun;
import Net.auth.Auth;
import Net.server.CharacterCardFactory;
import Net.server.ShutdownServer;
import Net.server.commands.PlayerRank;
import Net.server.maps.MapleMap;
import Net.server.quest.MapleQuest;
import Net.server.shops.IMaplePlayerShop;
import Opcode.header.InHeader;
import Opcode.header.OutHeader;
import Packet.MaplePacketCreator;
import Packet.UIPacket;
import Server.cashshop.CashShopServer;
import Server.cashshop.handler.BuyCashItemHandler;
import Server.channel.ChannelServer;
import Server.login.LoginServer;
import Server.login.handler.AutoRegister;
import Server.netty.MaplePacketDecoder;
import Server.world.WorldBuddyService;
import Server.world.WorldFindService;
import Server.world.WorldGuildService;
import Server.world.WorldMessengerService;
import Server.world.guild.MapleGuildCharacter;
import Server.world.messenger.MapleMessengerCharacter;
import SwordieX.world.World;
import com.alibaba.druid.pool.DruidPooledConnection;
import connection.Packet;
import connection.packet.Login;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Randomizer;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class MapleClient
implements Serializable {
    public static final byte LOGIN_NOTLOGGEDIN = 0;
    public static final byte LOGIN_SERVER_TRANSITION = 1;
    public static final byte LOGIN_LOGGEDIN = 2;
    public static final byte CHANGE_CHANNEL = 3;
    public static final byte ENTERING_PIN = 4;
    public static final byte PIN_CORRECT = 5;
    public static final byte LOGIN_CS_LOGGEDIN = 6;
    public static final AttributeKey<MapleClient> CLIENT_KEY = AttributeKey.newInstance("Client");
    private static final Logger log = LoggerFactory.getLogger(MapleClient.class);
    private static final long serialVersionUID = 9179541993413738569L;
    private static final Lock login_mutex = new ReentrantLock(true);
    private final transient Lock mutex = new ReentrantLock(true);
    private final transient Lock npc_mutex = new ReentrantLock();
    private final transient List<Integer> allowedChar = new LinkedList<Integer>();
    private final transient Map<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();
    private final Map<Integer, Pair<Integer, Short>> charInfo = new LinkedHashMap<Integer, Pair<Integer, Short>>();
    private final List<String> proesslist = new ArrayList<String>();
    public transient short loginAttempt = 0;
    private transient MapleAESOFB send;
    private transient MapleAESOFB receive;
    private transient Channel session;
    private MapleCharacter player;
    private int channel = 1;
    private int accId = -1;
    private int worldId;
    private int birthday;
    private int charslots = Math.min(53, ServerConfig.CHANNEL_PLAYER_MAXCHARACTERS);
    private int cardslots = 3;
    private boolean loggedIn = false;
    private boolean serverTransition = false;
    private transient Calendar tempban = null;
    private String accountName;
    private boolean monitored = false;
    private boolean receiving = true;
    private int gmLevel;
    private int maplePoint;
    private byte greason = 1;
    private byte gender = (byte)-1;
    private transient String mac = "00-00-00-00-00-00";
    private transient List<String> maclist = new LinkedList<String>();
    private transient ScheduledFuture<?> idleTask = null;
    private transient String secondPassword;
    private transient String salt2;
    private transient String tempIP = "";
    private long lastNpcClick = 0L;
    private long sessionId;
    private final byte loginattempt = 0;
    private Triple<String, String, Boolean> tempinfo = null;
    private final Map<Short, Short> encryptedOpcodes = new LinkedHashMap<Short, Short>();
    private int sessionIdx;
    private String name;
    private final AtomicInteger aliveCheckCount = new AtomicInteger(0);
    private ScheduledFuture aliveCheckSchedule = null;
    private volatile Boolean disconnecting = false;
    private final Object disconnectLock = new Object();
    private static final Map<Short, Short> Opcodes = new LinkedHashMap<Short, Short>();
    private static byte[] OpcodeEncryptPacket = null;
    private MapleClient c;

    public MapleClient() {
        this.MapleClient();
    }

    public MapleClient MapleClient() {
        return this;
    }

    public MapleClient(MapleAESOFB send, MapleAESOFB receive, Channel session) {
        this.send = send;
        this.receive = receive;
        this.session = session;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static byte unban(String charname) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");){
            int accid;
            ps.setString(1, charname);
            try (ResultSet rs = ps.executeQuery();){
                if (!rs.next()) {
                    byte by = -1;
                    return by;
                }
                accid = rs.getInt(1);
            }
            try (PreparedStatement psu = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");){
                psu.setInt(1, accid);
                psu.executeUpdate();
                return 0;
            }
        }
        catch (SQLException e) {
            log.error("Error while unbanning", e);
            return -2;
        }
    }

    public static String getLogMessage(MapleClient cfor, String message) {
        return MapleClient.getLogMessage(cfor, message, new Object[0]);
    }

    public static String getLogMessage(MapleCharacter cfor, String message) {
        return MapleClient.getLogMessage(cfor == null ? null : cfor.getClient(), message);
    }

    public static String getLogMessage(MapleCharacter cfor, String message, Object ... parms) {
        return MapleClient.getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
    }

    public static String getLogMessage(MapleClient cfor, String message, Object ... parms) {
        StringBuilder builder = new StringBuilder();
        if (cfor != null) {
            if (cfor.getPlayer() != null) {
                builder.append("<");
                builder.append(MapleCharacterUtil.makeMapleReadable((String)cfor.getPlayer().getName()));
                builder.append(" (角色ID: ");
                builder.append(cfor.getPlayer().getId());
                builder.append(")> ");
            }
            if (cfor.getAccountName() != null) {
                builder.append("(賬號: ");
                builder.append(cfor.getAccountName());
                builder.append(") ");
            }
        }
        builder.append(message);
        for (Object parm : parms) {
            int start = builder.indexOf("{}");
            builder.replace(start, start + 2, parm.toString());
        }
        return builder.toString();
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static int findAccIdForCharacterName(String charName) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            int n;
            block15: {
                PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
                try {
                    ps.setString(1, charName);
                    ResultSet rs = ps.executeQuery();
                    int ret = -1;
                    if (rs.next()) {
                        ret = rs.getInt("accountid");
                    }
                    n = ret;
                    if (ps == null) break block15;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return n;
        }
        catch (SQLException e) {
            log.error("findAccIdForCharacterName SQL error", e);
            return -1;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static byte unbanIPMacs(String charname) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                byte by = -1;
                return by;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                byte by = -1;
                return by;
            }
            String sessionIP = rs.getString("sessionIP");
            String macs = rs.getString("macs");
            rs.close();
            ps.close();
            byte ret = 0;
            if (sessionIP != null) {
                PreparedStatement psa = con.prepareStatement("DELETE FROM ipbans WHERE ip LIKE ?");
                psa.setString(1, sessionIP);
                psa.execute();
                psa.close();
                ret = (byte)(ret + 1);
            }
            if (macs != null) {
                String[] macz;
                for (String mac : macz = macs.split(", ")) {
                    if (mac.equals("")) continue;
                    PreparedStatement psa = con.prepareStatement("DELETE FROM macbans WHERE mac = ?");
                    psa.setString(1, mac);
                    psa.execute();
                    psa.close();
                }
                ret = (byte)(ret + 1);
            }
            byte by = ret;
            return by;
        }
        catch (SQLException e) {
            log.error("Error while unbanning", e);
            return -2;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static byte unHellban(String charname) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                byte by = -1;
                return by;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                byte by = -1;
                return by;
            }
            String sessionIP = rs.getString("sessionIP");
            String email = rs.getString("email");
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE email = ?" + (sessionIP == null ? "" : " OR sessionIP = ?"));
            ps.setString(1, email);
            if (sessionIP != null) {
                ps.setString(2, sessionIP);
            }
            ps.execute();
            ps.close();
            byte by = 0;
            return by;
        }
        catch (SQLException e) {
            log.error("Error while unbanning", e);
            return -2;
        }
    }

    public static String getAccInfo(String accname, boolean admin) {
        StringBuilder ret = new StringBuilder("帳號 " + accname + " 的信息 -");
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, accname);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int banned = rs.getInt("banned");
                ret.append(" 狀態: ");
                ret.append(banned > 0 ? "已封" : "正常");
                ret.append(" 封號理由: ");
                ret.append(banned > 0 ? rs.getString("banreason") : "(無描述)");
                if (admin) {
                    ret.append(" 樂豆點: ");
                    ret.append(rs.getInt("ACash"));
                    ret.append(" 楓點: ");
                    ret.append(rs.getInt("mPoints"));
                }
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex) {
            log.error("獲取玩家封號理由信息出錯", ex);
        }
        return ret.toString();
    }
    public static String getAccInfoByName(String charname, boolean admin) {
        try {
            Connection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();

            ResultSet rs;
            label198: {
                StringBuilder ret;
                label199: {
                    String var9;
                    try {
                        PreparedStatement ps;
                        label201: {
                            label202: {
                                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");

                                try {
                                    label203: {
                                        ps.setString(1, charname);
                                        rs = ps.executeQuery();

                                        int accid;
                                        label183: {
                                            try {
                                                if (rs.next()) {
                                                    accid = rs.getInt(1);
                                                    break label183;
                                                }

                                                rs = null;
                                            } catch (Throwable var17) {
                                                if (rs != null) {
                                                    try {
                                                        rs.close();
                                                    } catch (Throwable var13) {
                                                        var17.addSuppressed(var13);
                                                    }
                                                }

                                                throw var17;
                                            }

                                            if (rs != null) {
                                                rs.close();
                                            }
                                            break label202;
                                        }

                                        if (rs != null) {
                                            rs.close();
                                        }

                                        PreparedStatement psu = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");

                                        label172: {
                                            try {
                                                label205: {
                                                    psu.setInt(1, accid);
                                                    rs = ps.executeQuery();

                                                    try {
                                                        if (!rs.next()) {
                                                            ret = null;
                                                            break label205;
                                                        }

                                                        ret = new StringBuilder("玩家 " + charname + " 的帳號信息 -");
                                                        int banned = rs.getInt("banned");
                                                        if (admin) {
                                                            ret.append(" 賬號: ");
                                                            ret.append(rs.getString("name"));
                                                        }

                                                        ret.append(" 狀態: ");
                                                        ret.append(banned > 0 ? "已封" : "正常");
                                                        ret.append(" 封號理由: ");
                                                        ret.append(banned > 0 ? rs.getString("banreason") : "(無描述)");
                                                        var9 = ret.toString();
                                                    } catch (Throwable var15) {
                                                        if (rs != null) {
                                                            try {
                                                                rs.close();
                                                            } catch (Throwable var14) {
                                                                var15.addSuppressed(var14);
                                                            }
                                                        }

                                                        throw var15;
                                                    }

                                                    if (rs != null) {
                                                        rs.close();
                                                    }
                                                    break label172;
                                                }

                                                if (rs != null) {
                                                    rs.close();
                                                }
                                            } catch (Throwable var16) {
                                                if (psu != null) {
                                                    try {
                                                        psu.close();
                                                    } catch (Throwable var12) {
                                                        var16.addSuppressed(var12);
                                                    }
                                                }

                                                throw var16;
                                            }

                                            if (psu != null) {
                                                psu.close();
                                            }
                                            break label203;
                                        }

                                        if (psu != null) {
                                            psu.close();
                                        }
                                        break label201;
                                    }
                                } catch (Throwable var18) {
                                    if (ps != null) {
                                        try {
                                            ps.close();
                                        } catch (Throwable var11) {
                                            var18.addSuppressed(var11);
                                        }
                                    }

                                    throw var18;
                                }

                                if (ps != null) {
                                    ps.close();
                                }
                                break label199;
                            }

                            if (ps != null) {
                                ps.close();
                            }
                            break label198;
                        }

                        if (ps != null) {
                            ps.close();
                        }
                    } catch (Throwable var19) {
                        if (con != null) {
                            try {
                                con.close();
                            } catch (Throwable var10) {
                                var19.addSuppressed(var10);
                            }
                        }

                        throw var19;
                    }

                    if (con != null) {
                        con.close();
                    }

                    return var9;
                }

                if (con != null) {
                    con.close();
                }

                return ret.toString();
            }

            if (con != null) {
                con.close();
            }

            return rs.toString();
        } catch (SQLException var20) {
            SQLException ex = var20;
            log.error("獲取玩家封號理由信息出錯", ex);
            return null;
        }
    }

    /*
     * Exception decompiling
     */
//    public static String getAccInfoByName(String charname, boolean admin) {
//        /*
//         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
//         *
//         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
//         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
//         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
//         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
//         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
//         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
//         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
//         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
//         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
//         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
//         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
//         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
//         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
//         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
//         *     at org.benf.cfr.reader.Main.main(Main.java:54)
//         */
//        throw new IllegalStateException("Decompilation failed");
//    }

    public MapleAESOFB getReceiveCrypto() {
        return this.receive;
    }

    public MapleAESOFB getSendCrypto() {
        return this.send;
    }

    public void write(Packet msg) {
        this.announce(msg.getData());
    }

    public void send(Packet msg) {
        this.announce(msg.getData());
    }

    public void announce(byte[] array) {
        if (this.session == null || ShutdownServer.getInstance().isShutdown()) {
            return;
        }
        this.session.writeAndFlush(array);
    }

    public void ctx(int header, Object ... data) {
        MaplePacketLittleEndianWriter ctx = new MaplePacketLittleEndianWriter();
        ctx.writeShort(header);
        for (Object obj : data) {
            if (obj instanceof Integer) {
                ctx.writeInt((Integer)obj);
                continue;
            }
            if (obj instanceof Short) {
                ctx.writeShort(((Short)obj).shortValue());
                continue;
            }
            if (obj instanceof Byte) {
                ctx.write((Byte)obj);
                continue;
            }
            if (!(obj instanceof String)) continue;
            ctx.writeHexString((String)obj);
        }
        this.announce(ctx.getPacket());
    }

    public void outPacket(int header, Object ... data) {
        MaplePacketLittleEndianWriter packet = new MaplePacketLittleEndianWriter();
        packet.writeShort(header);
        for (Object obj : data) {
            if (obj instanceof Integer) {
                packet.writeInt((Integer)obj);
                continue;
            }
            if (obj instanceof Long) {
                packet.writeLong((Long)obj);
                continue;
            }
            if (obj instanceof Short) {
                packet.writeShort(((Short)obj).shortValue());
                continue;
            }
            if (obj instanceof Byte) {
                packet.write((Byte)obj);
                continue;
            }
            if (!(obj instanceof String)) continue;
            packet.writeHexString((String)obj);
        }
        this.announce(packet.getPacket());
    }

    public void sendEnableActions() {
        if (this.session == null || this.player == null || ShutdownServer.getInstance().isShutdown()) {
            return;
        }
        this.player.enableActions(true);
    }

    public void sendEnableActions(boolean useTriggerForUI) {
        if (this.session == null || this.player == null || ShutdownServer.getInstance().isShutdown()) {
            return;
        }
        this.player.enableActions(useTriggerForUI);
    }

    public Channel getSession() {
        return this.session;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public Lock getLock() {
        return this.mutex;
    }

    public Lock getNPCLock() {
        return this.npc_mutex;
    }

    public MapleCharacter getPlayer() {
        return this.player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public void createdChar(int id) {
        this.allowedChar.add(id);
    }

    public boolean login_Auth(int id) {
        return this.allowedChar.contains(id);
    }

    public List<MapleCharacter> loadCharacters(int serverId) {
        LinkedList<MapleCharacter> chars = new LinkedList<MapleCharacter>();
        Map<Integer, CardData> cards = CharacterCardFactory.getInstance().loadCharacterCards(this.accId, serverId);
        for (CharNameAndId cni : this.loadCharactersInternal(serverId)) {
            MapleCharacter chr = MapleCharacter.loadCharFromDB(cni.id, this, false, cards);
            chars.add(chr);
            this.charInfo.put(chr.getId(), new Pair<Integer, Short>(chr.getLevel(), chr.getJob()));
            if (this.login_Auth(chr.getId())) continue;
            this.allowedChar.add(chr.getId());
        }
        return chars;
    }

    public void updateCharacterCards(Map<Integer, Integer> cids) {
        if (this.charInfo.isEmpty()) {
            return;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `character_cards` WHERE `accid` = ?");){
                ps.setInt(1, this.accId);
                ps.executeUpdate();
            }
            try (PreparedStatement psu = con.prepareStatement("INSERT INTO `character_cards` (accid, worldid, characterid, position) VALUES (?, ?, ?, ?)");){
                for (Map.Entry<Integer, Integer> ii : cids.entrySet()) {
                    Pair<Integer, Short> info = this.charInfo.get(ii.getValue());
                    if (info == null || ii.getValue() == 0 || !CharacterCardFactory.getInstance().canHaveCard(info.getLeft(), info.getRight().shortValue())) continue;
                    psu.setInt(1, this.accId);
                    psu.setInt(2, this.worldId);
                    psu.setInt(3, ii.getValue());
                    psu.setInt(4, ii.getKey());
                    psu.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            log.error("Failed to update character cards. Reason:", e);
        }
    }

    public int getCharacterJob(int cid) {
        if (this.charInfo.containsKey(cid)) {
            return this.charInfo.get(cid).getRight().shortValue();
        }
        return -1;
    }

    public boolean canMakeCharacter(int serverId) {
        return this.loadCharactersSize(serverId) < this.getAccCharSlots();
    }

    public List<String> loadCharacterNames(int serverId) {
        LinkedList<String> chars = new LinkedList<String>();
        for (CharNameAndId cni : this.loadCharactersInternal(serverId)) {
            chars.add(cni.name);
        }
        return chars;
    }

    private List<CharNameAndId> loadCharactersInternal(int serverId) {
        LinkedList<CharNameAndId> chars = new LinkedList<CharNameAndId>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, name FROM characters WHERE accountid = ? AND world = ? ORDER BY position, id");){
            ps.setInt(1, this.accId);
            ps.setInt(2, serverId);
            try (ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
                    LoginServer.getLoginAuth(rs.getInt("id"));
                }
            }
        }
        catch (SQLException e) {
            log.error("error loading characters internal", e);
        }
        return chars;
    }

    public int loadCharactersSize(int serverId) {
        int chars = 0;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM characters WHERE accountid = ? AND world = ?");){
            ps.setInt(1, this.accId);
            ps.setInt(2, serverId);
            try (ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    chars = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            log.error("error loading characters size", e);
        }
        return chars;
    }

    public boolean isLoggedIn() {
        return this.loggedIn && this.accId >= 0;
    }

    private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
        Calendar lTempban = Calendar.getInstance();
        Timestamp tempbanTime = rs.getTimestamp("tempban");
        if (tempbanTime == null || tempbanTime.equals(Timestamp.valueOf("1970-01-01 00:00:01"))) {
            lTempban.setTimeInMillis(0L);
            return lTempban;
        }
        Calendar today = Calendar.getInstance();
        lTempban.setTimeInMillis(tempbanTime.getTime());
        if (today.getTimeInMillis() < lTempban.getTimeInMillis()) {
            return lTempban;
        }
        lTempban.setTimeInMillis(0L);
        return lTempban;
    }

    public Calendar getTempBanCalendar() {
        return this.tempban;
    }

    public byte getBanType() {
        return this.greason;
    }

    public boolean hasBannedIP() {
        boolean ret = false;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')");){
            ps.setString(1, this.getSessionIPAddress());
            try (ResultSet rs = ps.executeQuery();){
                rs.next();
                if (rs.getInt(1) > 0) {
                    ret = true;
                }
            }
        }
        catch (SQLException ex) {
            log.error("Error checking ip bans", ex);
        }
        return ret;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String macData) {
        if (macData.equalsIgnoreCase("00-00-00-00-00-00") || macData.length() != 17) {
            return;
        }
        this.mac = macData;
    }

    public boolean hasBannedMac() {
        if (this.mac.equalsIgnoreCase("00-00-00-00-00-00") || this.mac.length() != 17) {
            return false;
        }
        boolean ret = false;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM macbans WHERE mac = ?");){
            ps.setString(1, this.mac);
            try (ResultSet rs = ps.executeQuery();){
                rs.next();
                if (rs.getInt(1) > 0) {
                    ret = true;
                }
            }
        }
        catch (SQLException e) {
            log.error("Error checking mac bans", e);
        }
        return ret;
    }

    public void banMacs() {
        this.banMacs(this.mac);
    }

    public void banMacs(String macData) {
        if (macData.equalsIgnoreCase("00-00-00-00-00-00") || macData.length() != 17) {
            return;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)");){
            ps.setString(1, macData);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            log.error("Error banning MACs", e);
        }
    }

    public void updateMacs() {
        this.updateMacs(this.mac);
    }

    public void updateMacs(String macData) {
        if (macData.equalsIgnoreCase("00-00-00-00-00-00") || macData.length() != 17) {
            return;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?");){
            ps.setString(1, macData);
            ps.setInt(2, this.accId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            log.error("Error saving MACs", e);
        }
    }

    public int finishLogin() {
        login_mutex.lock();
        try {
            byte state = this.getLoginState();
            if (state > 0) {
                this.loggedIn = false;
                int n = 7;
                return n;
            }
            this.updateLoginState(2, this.getSessionIPAddress());
        }
        finally {
            login_mutex.unlock();
        }
        return 0;
    }

    public void clearInformation() {
        this.accountName = null;
        this.accId = -1;
        this.secondPassword = null;
        this.salt2 = null;
        this.gmLevel = 0;
        this.maplePoint = 0;
        this.loggedIn = false;
        this.mac = "00-00-00-00-00-00";
        this.maclist.clear();
        this.player = null;
    }

    public int changePassword(String oldpwd, String newpwd) {
        int ret = -1;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, this.getAccountName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean updatePassword = false;
                String passhash = rs.getString("password");
                String salt = rs.getString("salt");
                if (passhash == null || passhash.isEmpty()) {
                    ret = -1;
                } else if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(oldpwd, passhash)) {
                    ret = 0;
                    updatePassword = true;
                } else if (oldpwd.equals(passhash)) {
                    ret = 0;
                    updatePassword = true;
                } else if (salt == null && LoginCrypto.checkSha1Hash(passhash, oldpwd)) {
                    ret = 0;
                    updatePassword = true;
                } else if (LoginCrypto.checkSaltedSha512Hash(passhash, oldpwd, salt)) {
                    ret = 0;
                    updatePassword = true;
                } else {
                    ret = -1;
                }
                if (updatePassword) {
                    try (PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?");){
                        String newSalt = LoginCrypto.makeSalt();
                        pss.setString(1, LoginCrypto.makeSaltedSha512Hash(newpwd, newSalt));
                        pss.setString(2, newSalt);
                        pss.setInt(3, this.accId);
                        pss.executeUpdate();
                    }
                }
            }
            ps.close();
            rs.close();
        }
        catch (SQLException e) {
            log.error("修改遊戲帳號密碼出現錯誤.\r\n", e);
        }
        return ret;
    }

    public MapleEnumClass.AuthReply login(String login, String pwd, boolean ipMacBanned, boolean useKey) {
        MapleEnumClass.AuthReply loginok;
        block50: {
            loginok = MapleEnumClass.AuthReply.GAME_ACCOUNT_NOT_LANDED;
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");){
                ps.setString(1, login);
                try (ResultSet rs = ps.executeQuery();){
                    if (rs.next()) {
                        int banned = rs.getInt("banned");
                        String passhash = rs.getString("password");
                        String salt = rs.getString("salt");
                        String oldSession = rs.getString("SessionIP");
                        this.accountName = login;
                        this.accId = rs.getInt("id");
                        this.secondPassword = rs.getString("2ndpassword");
                        this.salt2 = rs.getString("salt2");
                        this.gmLevel = rs.getInt("gm");
                        this.greason = rs.getByte("greason");
                        this.tempban = this.getTempBanCalendar(rs);
                        this.gender = rs.getByte("gender");
                        this.maclist = new LinkedList<String>();
                        String macStrs = rs.getString("maclist");
                        if (macStrs != null) {
                            String[] macData;
                            for (String macData1 : macData = macStrs.split(",")) {
                                if (macData1.length() != 17) continue;
                                this.maclist.add(macData1);
                            }
                        }
                        if (this.secondPassword != null && this.salt2 != null) {
                            this.secondPassword = LoginCrypto.rand_r(this.secondPassword);
                        }
                        ps.close();
                        if (useKey) {
                            loginok = MapleEnumClass.AuthReply.GAME_LOGIN_SUCCESSFUL;
                            break block50;
                        }
                        if ((banned > 0 || this.tempban != null && this.tempban.getTimeInMillis() > System.currentTimeMillis()) && this.gmLevel == 0) {
                            loginok = MapleEnumClass.AuthReply.GAME_ACCOUNT_BANNED;
                            break block50;
                        }
                        if (banned == -1) {
                            this.unban();
                        }
                        boolean updatePasswordHash = false;
                        if (passhash == null || passhash.isEmpty()) {
                            if (oldSession != null && !oldSession.isEmpty()) {
                                this.loggedIn = this.getSessionIPAddress().equals(oldSession);
                                loginok = this.loggedIn ? MapleEnumClass.AuthReply.GAME_LOGIN_SUCCESSFUL : MapleEnumClass.AuthReply.GAME_PASSWORD_ERROR;
                                updatePasswordHash = this.loggedIn;
                            } else {
                                loginok = MapleEnumClass.AuthReply.GAME_PASSWORD_ERROR;
                                this.loggedIn = false;
                            }
                        } else if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(pwd, passhash)) {
                            loginok = MapleEnumClass.AuthReply.GAME_LOGIN_SUCCESSFUL;
                            updatePasswordHash = true;
                        } else if (pwd.equals(passhash)) {
                            loginok = MapleEnumClass.AuthReply.GAME_LOGIN_SUCCESSFUL;
                            updatePasswordHash = true;
                        } else if (salt == null && LoginCrypto.checkSha1Hash(passhash, pwd)) {
                            loginok = MapleEnumClass.AuthReply.GAME_LOGIN_SUCCESSFUL;
                            updatePasswordHash = true;
                        } else if (LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt)) {
                            loginok = MapleEnumClass.AuthReply.GAME_LOGIN_SUCCESSFUL;
                        } else {
                            this.loggedIn = false;
                            loginok = MapleEnumClass.AuthReply.GAME_PASSWORD_ERROR;
                        }
                        if (updatePasswordHash) {
                            try (PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?");){
                                String newSalt = LoginCrypto.makeSalt();
                                pss.setString(1, LoginCrypto.makeSaltedSha512Hash(pwd, newSalt));
                                pss.setString(2, newSalt);
                                pss.setInt(3, this.accId);
                                pss.executeUpdate();
                            }
                        }
                        if (loginok == MapleEnumClass.AuthReply.GAME_LOGIN_SUCCESSFUL) {
                            byte loginstate;
                            if (Server.world.World.Client.isStuck(this, this.accId)) {
                                this.updateLoginState(0);
                            }
                            if ((loginstate = this.getLoginState()) > 0) {
                                this.loggedIn = false;
                                loginok = MapleEnumClass.AuthReply.GAME_CONNECTING_ACCOUNT;
                            }
                        }
                        break block50;
                    }
                    if (ServerConfig.AUTORIGISTER && AutoRegister.createAccount((String)login, (String)pwd)) {
                        loginok = this.login(login, pwd, ipMacBanned, useKey);
                    }
                }
            }
            catch (SQLException e) {
                log.error("登錄遊戲帳號出現錯誤. 賬號: " + login + " \r\n", e);
            }
        }
        return loginok;
    }

    public boolean CheckSecondPassword(String in) {
        if (this.secondPassword == null) {
            try (DruidPooledConnection con2 = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                PreparedStatement ps = con2.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                ps.setInt(1, this.accId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    this.secondPassword = rs.getString("2ndpassword");
                    if (this.secondPassword != null && rs.getString("salt2") != null) {
                        this.secondPassword = LoginCrypto.rand_r(this.secondPassword);
                    }
                    this.salt2 = rs.getString("salt2");
                }
            }
            catch (SQLException con2) {
                // empty catch block
            }
            if (this.secondPassword == null) {
                log.error("讀取二次密碼錯誤");
                return false;
            }
        }
        boolean allow = false;
        boolean updatePasswordHash = false;
        if (LoginCryptoLegacy.isLegacyPassword(this.secondPassword) && LoginCryptoLegacy.checkPassword(in, this.secondPassword)) {
            allow = true;
            updatePasswordHash = true;
        } else if (this.salt2 == null && LoginCrypto.checkSha1Hash(this.secondPassword, in)) {
            allow = true;
            updatePasswordHash = true;
        } else if (in.equals(this.secondPassword)) {
            allow = true;
            updatePasswordHash = true;
        } else if (LoginCrypto.checkSaltedSha512Hash(this.secondPassword, in, this.salt2)) {
            allow = true;
        }
        if (updatePasswordHash) {
            this.setSecondPassword(in);
            return this.updateSecondPassword();
        }
        return allow;
    }

    private void unban() {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");){
            ps.setInt(1, this.accId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            log.error("Error while unbanning", e);
        }
    }

    public int getAccID() {
        return this.accId;
    }

    public void setAccID(int id) {
        this.accId = id;
    }

    public void updateLoginState(int newstate) {
        this.updateLoginState(newstate, this.getSessionIPAddress());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateLoginState(int newstate, String SessionID) {
        block21: {
            try {
                try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                     PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");){
                    ps.setInt(1, newstate);
                    ps.setString(2, SessionID);
                    ps.setInt(3, this.getAccID());
                    ps.executeUpdate();
                }
                if (newstate == 0) {
                    this.loggedIn = false;
                    this.serverTransition = false;
                    break block21;
                }
                this.serverTransition = newstate == 1 || newstate == 3;
            }
            catch (Exception e) {
                try {
                    log.error("Error updating login state", e);
                    if (newstate == 0) {
                        this.loggedIn = false;
                        this.serverTransition = false;
                    }
                    this.serverTransition = newstate == 1 || newstate == 3;
                }
                catch (Throwable throwable) {
                    if (newstate == 0) {
                        this.loggedIn = false;
                        this.serverTransition = false;
                    } else {
                        this.serverTransition = newstate == 1 || newstate == 3;
                        this.loggedIn = !this.serverTransition;
                    }
                    throw throwable;
                }
                this.loggedIn = !this.serverTransition;
            }
            this.loggedIn = !this.serverTransition;
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public boolean updateSecondPassword() {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            boolean bl;
            block14: {
                PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ?, `salt2` = ? WHERE id = ?");
                try {
                    String newSalt = LoginCrypto.makeSalt();
                    ps.setString(1, LoginCrypto.rand_s(LoginCrypto.makeSaltedSha512Hash(this.secondPassword, newSalt)));
                    ps.setString(2, newSalt);
                    ps.setInt(3, this.accId);
                    ps.executeUpdate();
                    bl = true;
                    if (ps == null) break block14;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return bl;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public byte getLoginState() {
        try {
            Connection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();

            byte state;
            label118: {
                byte var5;
                try {
                    label119: {
                        PreparedStatement ps;
                        label120: {
                            ps = con.prepareStatement("SELECT loggedin, lastlogin, banned, `birthday` + 0 AS `bday` FROM accounts WHERE id = ?");

                            try {
                                label121: {
                                    ps.setInt(1, this.getAccID());
                                    ResultSet rs = ps.executeQuery();

                                    label105: {
                                        try {
                                            if (!rs.next()) {
                                                state = 0;
                                                break label105;
                                            }

                                            this.birthday = rs.getInt("bday");
                                            state = rs.getByte("loggedin");
                                            if ((state == 1 || state == 3) && rs.getTimestamp("lastlogin").getTime() + 20000L < System.currentTimeMillis()) {
                                                state = 0;
                                                this.updateLoginState(state, this.getSessionIPAddress());
                                            }

                                            this.loggedIn = state == 2;
                                            var5 = state;
                                        } catch (Throwable var9) {
                                            if (rs != null) {
                                                try {
                                                    rs.close();
                                                } catch (Throwable var8) {
                                                    var9.addSuppressed(var8);
                                                }
                                            }

                                            throw var9;
                                        }

                                        if (rs != null) {
                                            rs.close();
                                        }
                                        break label121;
                                    }

                                    if (rs != null) {
                                        rs.close();
                                    }
                                    break label120;
                                }
                            } catch (Throwable var10) {
                                if (ps != null) {
                                    try {
                                        ps.close();
                                    } catch (Throwable var7) {
                                        var10.addSuppressed(var7);
                                    }
                                }

                                throw var10;
                            }

                            if (ps != null) {
                                ps.close();
                            }
                            break label119;
                        }

                        if (ps != null) {
                            ps.close();
                        }
                        break label118;
                    }
                } catch (Throwable var11) {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (Throwable var6) {
                            var11.addSuppressed(var6);
                        }
                    }

                    throw var11;
                }

                if (con != null) {
                    con.close();
                }

                return var5;
            }

            if (con != null) {
                con.close();
            }

            return state;
        } catch (SQLException var12) {
            SQLException e = var12;
            this.loggedIn = false;
            log.error("error getting login state", e);
            return 0;
        }
    }

    /*
     * Exception decompiling
     */
/*
    public byte getLoginState() {
        */
/*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 5 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         *//*

        throw new IllegalStateException("Decompilation failed");
    }
*/

    public boolean checkBirthDate(int date) {
        return this.birthday == date;
    }

    public void removalTask(boolean shutdown) {
        try {
            MapleQuestStatus stat2;
            MapleQuestStatus stat1;
            this.player.removeAllEffect();
            if (this.player.getMarriageId() > 0) {
                stat1 = this.player.getQuestNoAdd(MapleQuest.getInstance(160001));
                stat2 = this.player.getQuestNoAdd(MapleQuest.getInstance(160002));
                if (stat1 != null && stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
                    if (stat2 != null && stat2.getCustomData() != null) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            if (this.player.getMapId() == 993073000 && !this.player.isIntern()) {
                stat1 = this.player.getQuestNAdd(MapleQuest.getInstance(123455));
                stat2 = this.player.getQuestNAdd(MapleQuest.getInstance(123456));
                if (stat1.getCustomData() == null) {
                    stat1.setCustomData(String.valueOf(System.currentTimeMillis()));
                } else if (stat2.getCustomData() == null) {
                    stat2.setCustomData("0");
                } else {
                    int seconds = Integer.parseInt(stat2.getCustomData()) - (int)((System.currentTimeMillis() - Long.parseLong(stat1.getCustomData())) / 1000L);
                    if (seconds < 0) {
                        seconds = 0;
                    }
                    stat2.setCustomData(String.valueOf(seconds));
                }
            }
            this.player.changeRemoval(true);
            IMaplePlayerShop shop = this.player.getPlayerShop();
            if (shop != null) {
                shop.removeVisitor(this.player);
                if (shop.isOwner(this.player)) {
                    if (shop.getShopType() == 1 && shop.isAvailable() && !shutdown) {
                        shop.setOpen(true);
                    } else {
                        shop.closeShop(true, !shutdown);
                    }
                }
            }
            this.player.setMessenger(null);
            MapleAntiMacro.stopAnti(this.player.getName());
            if (this.player.getMap() != null) {
                if (shutdown || this.getChannelServer() != null && this.getChannelServer().isShutdown()) {
                    int questID = -1;
                    switch (this.player.getMapId()) {
                        case 240060200: {
                            questID = 160100;
                            break;
                        }
                        case 240060201: {
                            questID = 160103;
                            break;
                        }
                        case 280030000: 
                        case 280030100: {
                            questID = 160101;
                            break;
                        }
                        case 280030001: {
                            questID = 160102;
                            break;
                        }
                        case 270050100: {
                            questID = 160104;
                            break;
                        }
                        case 105100300: 
                        case 105100400: {
                            questID = 160106;
                            break;
                        }
                        case 211070000: 
                        case 211070100: 
                        case 211070101: 
                        case 211070110: {
                            questID = 160107;
                            break;
                        }
                        case 551030200: {
                            questID = 160108;
                            break;
                        }
                        case 271040100: {
                            questID = 160109;
                        }
                    }
                    if (questID > 0) {
                        this.player.getQuestNAdd(MapleQuest.getInstance(questID)).setCustomData("0");
                    }
                } else if (this.player.isAlive()) {
                    switch (this.player.getMapId()) {
                        case 220080001: 
                        case 541010100: 
                        case 541020800: {
                            this.player.getMap().addDisconnected(this.player.getId());
                        }
                    }
                }
                this.player.getMap().userLeaveField(this.player);
            }
        }
        catch (Throwable e) {
            log.error("error removalTask", e);
        }
    }

    public void disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        this.disconnect(RemoveInChannelServer, fromCS, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public void disconnect(boolean RemoveInChannelServer, boolean fromCS, boolean shutdown) {
        if (this.disconnecting.booleanValue()) {
            return;
        }
        synchronized (this.disconnectLock) {
            if (this.disconnecting.booleanValue()) {
                return;
            }
            this.disconnecting = true;
        }
        if (this.aliveCheckSchedule != null) {
            this.aliveCheckSchedule.cancel(true);
            this.aliveCheckSchedule = null;
        }
        if (this.player != null) {
            MapleMap map = this.player.getMap();
            int idz = this.player.getId();
            int messengerId = this.player.getMessenger() == null ? 0 : this.player.getMessenger().getId();
            int gid = this.player.getGuildId();
            BuddyList chrBuddy = this.player.getBuddylist();
            MapleMessengerCharacter chrMessenger = new MapleMessengerCharacter(this.player);
            MapleGuildCharacter chrGuild = this.player.getMGC();
            this.removalTask(shutdown);
            LoginServer.getLoginAuth(this.player.getId());
            if (!fromCS) {
                this.player.expirationTask(true);
            }
            this.player.saveToDB(true, fromCS);
            if (shutdown) {
                this.player = null;
                this.receiving = false;
                return;
            }
            this.player.setOnline(false);
            if (!fromCS) {
                ChannelServer ch = ChannelServer.getInstance(map == null ? this.channel : map.getChannel());
                int chz = WorldFindService.getInstance().findChannel(idz);
                if (chz < -1) {
                    this.disconnect(RemoveInChannelServer, true);
                    return;
                }
                try {
                    if (chz == -1 || ch == null || ch.isShutdown()) {
                        this.player = null;
                        return;
                    }
                    if (messengerId > 0) {
                        WorldMessengerService.getInstance().leaveMessenger(messengerId, chrMessenger);
                    }
                    if (this.player.getParty() != null && this.player.getParty().getPartyLeaderID() == idz) {
                        this.player.getParty().changeLeaderDC();
                    }
                    if (chrBuddy != null) {
                        if (!this.serverTransition) {
                            WorldBuddyService.getInstance().loggedOff(idz, this.channel, chrBuddy.getBuddyIds());
                        } else {
                            WorldBuddyService.getInstance().loggedOn(idz, this.channel, chrBuddy.getBuddyIds());
                        }
                    }
                    if (gid > 0 && chrGuild != null) {
                        WorldGuildService.getInstance().setGuildMemberOnline(chrGuild, false, -1);
                    }
                }
                catch (Exception e) {
                    MapleClient.log.error(MapleClient.getLogMessage(this, "ERROR") + String.valueOf(e));
                }
                finally {
                    if (RemoveInChannelServer && ch != null && this.player != null) {
                        ch.removePlayer(this.player.getId());
                    }
                    this.player = null;
                }
            } else {
                int ch = WorldFindService.getInstance().findChannel(idz);
                if (ch > 0) {
                    this.disconnect(RemoveInChannelServer, false);
                    return;
                }
                try {
                    if (!this.serverTransition) {
                        WorldBuddyService.getInstance().loggedOff(idz, this.channel, chrBuddy.getBuddyIds());
                    } else {
                        WorldBuddyService.getInstance().loggedOn(idz, this.channel, chrBuddy.getBuddyIds());
                    }
                    if (gid > 0 && chrGuild != null) {
                        WorldGuildService.getInstance().setGuildMemberOnline(chrGuild, false, -1);
                    }
                    if (this.player != null) {
                        this.player.setMessenger(null);
                    }
                }
                catch (Exception e) {
                    MapleClient.log.error(MapleClient.getLogMessage(this, "ERROR") + String.valueOf(e));
                }
                finally {
                    if (RemoveInChannelServer && ch == -10) {
                        CashShopServer.getPlayerStorage().deregisterPlayer(idz);
                    }
                    this.player = null;
                }
            }
        }
        if (!this.serverTransition && this.isLoggedIn()) {
            if (!shutdown) {
                this.updateLoginState(0, this.getSessionIPAddress());
            }
            this.session.attr(MapleClient.CLIENT_KEY).set(null);
            this.session.attr(MaplePacketDecoder.DECODER_STATE_KEY).set(null);
            this.session.close();
        }
        this.engines.clear();
    }

    public String getSessionIPAddress() {
        if (this.session == null || !this.session.isActive()) {
            return "0.0.0.0";
        }
        return this.session.remoteAddress().toString().split(":")[0].replace("/", "");
    }

    public String getSessionLocalIPAddress() {
        if (this.session == null || !this.session.isActive()) {
            return "0.0.0.0";
        }
        return this.session.localAddress().toString().split(":")[0].replace("/", "");
    }

    public boolean CheckIPAddress() {
        if (this.accId < 0) {
            return false;
        }
        boolean canlogin = true;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT SessionIP, banned FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            try (ResultSet rs = ps.executeQuery();){
                if (rs.next() && rs.getInt("banned") > 0) {
                    canlogin = false;
                }
            }
        }
        catch (SQLException e) {
            log.error("Failed in checking IP address for Client.", e);
        }
        return canlogin;
    }

    public void DebugMessage(StringBuilder sb) {
        sb.append(this.getSession().remoteAddress());
        sb.append(" 是否連接: ");
        sb.append(this.getSession().isActive());
        sb.append(" 是否斷開: ");
        sb.append(!this.getSession().isOpen());
        sb.append(" 密匙狀態: ");
        sb.append(this.getSession().attr(CLIENT_KEY) != null);
        sb.append(" 登錄狀態: ");
        sb.append(this.isLoggedIn());
        sb.append(" 是否有角色: ");
        sb.append(this.getPlayer() != null);
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }

    public byte getGender() {
        return this.gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET gender = ? WHERE id = ?");){
            ps.setByte(1, gender);
            ps.setInt(2, this.accId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            log.error("保存角色性別出錯", e);
        }
    }

    public String getSecondPassword() {
        return this.secondPassword;
    }

    public void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public void setSalt2(String salt2) {
        this.salt2 = salt2;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public boolean checkSecuredAccountName(String accountName) {
        if (this.getAccountName().length() != accountName.length()) {
            return false;
        }
        for (int i = 0; i < accountName.length(); ++i) {
            if (accountName.charAt(i) == '*' || accountName.charAt(i) == this.accountName.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public String getSecurityAccountName() {
        StringBuilder sb = new StringBuilder(this.accountName);
        if (sb.length() >= 4) {
            sb.replace(1, 3, "**");
        } else if (sb.length() >= 3) {
            sb.replace(1, 2, "*");
        }
        if (sb.length() > 4) {
            sb.replace(sb.length() - 1, sb.length(), "*");
        }
        return sb.toString();
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public SwordieX.world.World getWorld() {
        return NetRun.Server.getInstance().getWorldById(this.getWorldId());
    }

    public int getWorldId() {
        return this.worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public void pongReceived() {
        this.aliveCheckCount.set(0);
        if (this.aliveCheckSchedule != null) {
            this.aliveCheckSchedule.cancel(true);
            this.aliveCheckSchedule = null;
        }
        if (this.getPlayer() != null) {
            this.write(Login.sendPingCheckResultClientToGame());
        }
    }

    public boolean isIntern() {
        return this.gmLevel >= PlayerRank.實習管理員.getSpLevel();
    }

    public boolean isGm() {
        return this.gmLevel >= PlayerRank.遊戲管理員.getSpLevel();
    }

    public boolean isSuperGm() {
        return this.gmLevel >= PlayerRank.超級管理員.getSpLevel();
    }

    public boolean isAdmin() {
        return this.gmLevel >= PlayerRank.伺服管理員.getSpLevel();
    }

    public int getGmLevel() {
        return this.gmLevel;
    }

    public boolean hasGmLevel(int level) {
        return this.gmLevel >= level;
    }

    public void setGmLevel(int level) {
        this.gmLevel = level;
    }

    public void updateGmLevel() {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET gm = ? WHERE id = ?");){
            ps.setInt(1, this.gmLevel);
            ps.setInt(2, this.accId);
            ps.executeUpdate();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public ScheduledFuture<?> getIdleTask() {
        return this.idleTask;
    }

    public void setIdleTask(ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;
    }

    public int getAccCharSlots() {
        block27: {
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM character_slots WHERE accid = ? AND worldid = ?");){
                ps.setInt(1, this.accId);
                ps.setInt(2, this.worldId);
                try (ResultSet rs = ps.executeQuery();){
                    if (rs.next()) {
                        this.charslots = Math.min(53, rs.getInt("charslots"));
                        break block27;
                    }
                    this.charslots = Math.min(53, this.charslots);
                    try (PreparedStatement psu = con.prepareStatement("INSERT INTO character_slots (accid, worldid, charslots) VALUES (?, ?, ?)");){
                        psu.setInt(1, this.accId);
                        psu.setInt(2, this.worldId);
                        psu.setInt(3, this.charslots);
                        psu.executeUpdate();
                    }
                }
            }
            catch (SQLException e) {
                log.error("獲取帳號可創建角色數量出現錯誤", e);
            }
        }
        return this.charslots;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public boolean gainAccCharSlot() {
        if (this.getAccCharSlots() >= 53) {
            return false;
        }
        ++this.charslots;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            boolean bl;
            block15: {
                PreparedStatement ps = con.prepareStatement("UPDATE character_slots SET charslots = ? WHERE worldid = ? AND accid = ?");
                try {
                    ps.setInt(1, this.charslots);
                    ps.setInt(2, this.worldId);
                    ps.setInt(3, this.accId);
                    ps.executeUpdate();
                    bl = true;
                    if (ps == null) break block15;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return bl;
        }
        catch (SQLException e) {
            log.error("增加帳號可創建角色數量出現錯誤", e);
            return false;
        }
    }

    public int getAccCardSlots() {
        block27: {
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?");){
                ps.setInt(1, this.accId);
                ps.setInt(2, this.worldId);
                try (ResultSet rs = ps.executeQuery();){
                    if (rs.next()) {
                        this.cardslots = rs.getInt("cardSlots");
                        break block27;
                    }
                    try (PreparedStatement psu = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, cardSlots) VALUES (?, ?, ?)");){
                        psu.setInt(1, this.accId);
                        psu.setInt(2, this.worldId);
                        psu.setInt(3, this.cardslots);
                        psu.executeUpdate();
                    }
                }
            }
            catch (SQLException e) {
                log.error("獲取帳號下的角色卡數量出現錯誤", e);
            }
        }
        return this.cardslots;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public boolean gainAccCardSlot() {
        if (this.getAccCardSlots() >= 9) {
            return false;
        }
        ++this.cardslots;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            boolean bl;
            block15: {
                PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET cardSlots = ? WHERE worldId = ? AND accId = ?");
                try {
                    ps.setInt(1, this.cardslots);
                    ps.setInt(2, this.worldId);
                    ps.setInt(3, this.accId);
                    ps.executeUpdate();
                    bl = true;
                    if (ps == null) break block15;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return bl;
        }
        catch (SQLException e) {
            log.error("增加角色卡的數量出現錯誤", e);
            return false;
        }
    }

    public boolean isMonitored() {
        return this.monitored;
    }

    public void setMonitored(boolean m) {
        this.monitored = m;
    }

    public boolean isReceiving() {
        return this.receiving;
    }

    public void setReceiving(boolean m) {
        this.receiving = m;
    }

    public String getTempIP() {
        return this.tempIP;
    }

    public void setTempIP(String s) {
        this.tempIP = s;
    }

    public boolean isLocalhost() {
        return ServerConstants.isIPLocalhost(this.getSessionIPAddress());
    }

    public boolean hasCheck(int accid) {
        boolean ret = false;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");){
            ps.setInt(1, accid);
            try (ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    ret = rs.getInt("check") > 0;
                }
            }
        }
        catch (SQLException e) {
            log.error("Error checking ip Check", e);
        }
        return ret;
    }

    public void setScriptEngine(String name, ScriptEngine e) {
        this.engines.put(name, e);
    }

    public ScriptEngine getScriptEngine(String name) {
        return this.engines.get(name);
    }

    public void removeScriptEngine(String name) {
        this.engines.remove(name);
    }

    public boolean canClickNPC() {
        return this.lastNpcClick + 500L < System.currentTimeMillis();
    }

    public void setClickedNPC() {
        this.lastNpcClick = System.currentTimeMillis();
    }

    public void removeClickedNPC() {
        this.lastNpcClick = 0L;
    }

    public boolean hasCheckMac(String macData) {
        return !macData.equalsIgnoreCase("00-00-00-00-00-00") && macData.length() == 17 && !this.maclist.isEmpty() && this.maclist.contains(macData);
    }

    public void setTempInfo(String login, String pwd, boolean isBanned) {
        this.tempinfo = new Triple<String, String, Boolean>(login, pwd, isBanned);
    }

    public Triple<String, String, Boolean> getTempInfo() {
        return this.tempinfo;
    }

    public void addProcessName(String process) {
        this.proesslist.add(process);
    }

    public boolean hasProcessName(String process) {
        for (String p : this.proesslist) {
            if (!p.startsWith(process)) continue;
            return true;
        }
        return this.proesslist.contains(process);
    }

    public void dropMessage(String message) {
        this.announce(MaplePacketCreator.serverNotice(1, message));
    }

    public boolean modifyCSPoints(int type, int quantity) {
        switch (type) {
            case 1: {
                if (this.getACash() + quantity < 0) {
                    return false;
                }
                this.setACash(this.getACash() + quantity);
                break;
            }
            case 2: {
                if (quantity < 0 && ServerConfig.mileageAsMaplePoint) {
                    int mileage = this.getMileage();
                    if (mileage >= Math.abs(quantity)) {
                        this.modifyMileage(quantity);
                        BuyCashItemHandler.addCashshopLog((MapleClient)this, (int)0, (int)5440000, (int)1, (int)0, (int)Math.abs(quantity), (int)1, (String)"里程兌換楓點");
                        return true;
                    }
                    if (this.getMaplePoints(true) + mileage + quantity < 0) {
                        return false;
                    }
                    this.modifyMileage(-mileage);
                    BuyCashItemHandler.addCashshopLog((MapleClient)this, (int)0, (int)5440000, (int)1, (int)0, (int)Math.abs(mileage), (int)1, (String)"里程兌換楓點");
                    quantity = mileage + quantity;
                }
                if (this.getMaplePoints(true) + quantity < 0) {
                    return false;
                }
                this.setMaplePoints(this.getMaplePoints(true) + quantity);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    public int getCSPoints(int type) {
        switch (type) {
            case 1: {
                return this.getACash();
            }
            case 2: {
                return this.getMaplePoints();
            }
        }
        return 0;
    }

    public int getACash() {
        int point = 0;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT ACash FROM accounts WHERE id = ?");){
            ps.setInt(1, this.getAccID());
            try (ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    point = rs.getInt("ACash");
                }
            }
        }
        catch (SQLException e) {
            log.error("獲取角色樂豆點失敗。" + String.valueOf(e));
        }
        return point;
    }

    public void setACash(int point) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET ACash = ? WHERE id = ?");){
            ps.setInt(1, point);
            ps.setInt(2, this.getAccID());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            log.error("獲取角色樂豆點失敗。" + String.valueOf(e));
        }
    }

    public int getMaplePoints() {
        return this.getMaplePoints(false);
    }

    public int getMaplePoints(boolean onlyMPoint) {
        int point = this.maplePoint;
        if (!onlyMPoint && ServerConfig.mileageAsMaplePoint) {
            point += this.getMileage();
        }
        return point;
    }

    public void setMaplePoints(int point) {
        this.maplePoint = point;
    }

    public List<Pair<Triple<Integer, Integer, Integer>, Long>> getMileageRechargeRecords() {
        LinkedList<Pair<Triple<Integer, Integer, Integer>, Long>> recordList = new LinkedList<Pair<Triple<Integer, Integer, Integer>, Long>>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("DELETE A FROM mileage_recharge_record A INNER JOIN (SELECT id FROM mileage_recharge_record WHERE accId = ? ORDER BY Time DESC LIMIT " + ServerConfig.mileageMonthlyLimitMax * 2 + "," + ServerConfig.mileageMonthlyLimitMax * 2 + ") B ON A.id=B.id");
            ps.setInt(1, this.getAccID());
            ps.execute();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM mileage_recharge_record WHERE accId = ? AND mileage > 0 ORDER BY Time DESC LIMIT " + ServerConfig.mileageMonthlyLimitMax);
            ps.setInt(1, this.getAccID());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recordList.add(new Pair<Triple<Integer, Integer, Integer>, Long>(new Triple<Integer, Integer, Integer>(rs.getInt("mileage"), rs.getInt("type"), rs.getInt("status")), rs.getTimestamp("Time").getTime()));
            }
            ps.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        return recordList;
    }

    public List<Pair<Triple<Integer, Integer, Integer>, Long>> getMileagePurchaseRecords() {
        LinkedList<Pair<Triple<Integer, Integer, Integer>, Long>> recordList = new LinkedList<Pair<Triple<Integer, Integer, Integer>, Long>>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("DELETE A FROM cashshop_log A INNER JOIN (SELECT id FROM cashshop_log WHERE accId = ? ORDER BY Time DESC LIMIT 1000,1000) B ON A.id=B.id");
            ps.setInt(1, this.getAccID());
            ps.execute();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM cashshop_log WHERE accId = ? AND mileage > 0 ORDER BY Time DESC LIMIT 100");
            ps.setInt(1, this.getAccID());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recordList.add(new Pair<Triple<Integer, Integer, Integer>, Long>(new Triple<Integer, Integer, Integer>(rs.getInt("mileage"), rs.getInt("itemId"), rs.getInt("SN")), rs.getTimestamp("Time").getTime()));
            }
            ps.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        return recordList;
    }

    public List<Pair<Integer, Long>> getMileageRecords() {
        LinkedList<Pair<Integer, Long>> recordList = new LinkedList<Pair<Integer, Long>>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("DELETE FROM mileage_record WHERE mileage <= 0 OR Time IS NULL OR Time <= CURDATE()");
            ps.execute();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM mileage_record WHERE accId = ? AND mileage > 0 ORDER BY Time DESC");
            ps.setInt(1, this.getAccID());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recordList.add(new Pair<Integer, Long>(rs.getInt("mileage"), rs.getTimestamp("Time").getTime()));
            }
            ps.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        return recordList;
    }

    public int getMileage() {
        List<Pair<Integer, Long>> recordList = this.getMileageRecords();
        int point = 0;
        for (Pair<Integer, Long> record : recordList) {
            point += record.getLeft().intValue();
        }
        return point;
    }

    public int rechargeMileage(int quantity, int type, boolean limitMax, String log) {
        if (quantity <= 0) {
            return quantity == 0 ? 0 : -1;
        }
        int result = this.modifyMileage(quantity, limitMax);
        if (result != 0) {
            return result;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("INSERT INTO `mileage_recharge_record` (accId, mileage, type, log) VALUES (?, ?, ?, ?)");
            ps.setInt(1, this.getAccID());
            ps.setInt(2, quantity);
            ps.setInt(3, type);
            if (log == null) {
                log = type == 1 ? "購買儲值" : "活動儲值";
            }
            ps.setString(4, log);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            return -1;
        }
        return 0;
    }

    public int modifyMileage(int quantity) {
        return this.modifyMileage(quantity, true);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int modifyMileage(int quantity, boolean limitMax) {
        int point;
        ResultSet rs;
        PreparedStatement ps;
        List<Pair<Integer, Long>> recordList = null;
        if (quantity == 0) {
            return 0;
        }
        if (quantity < 0) {
            recordList = this.getMileageRecords();
            int point2 = 0;
            for (Pair<Integer, Long> record : recordList) {
                point2 += record.getLeft().intValue();
            }
            if (point2 < Math.abs(quantity)) {
                return -1;
            }
        } else if (limitMax && (ServerConfig.mileageDailyLimitMax > 0 || ServerConfig.mileageMonthlyLimitMax > 0)) {
            try (DruidPooledConnection con2 = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                int mileageDaily = 0;
                int mileageMonthly = 0;
                if (ServerConfig.mileageDailyLimitMax > 0) {
                    ps = con2.prepareStatement("SELECT SUM(mileage) FROM mileage_recharge_record WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m%d') = DATE_FORMAT(CURDATE(), '%Y%m%d')");
                    ps.setInt(1, this.getAccID());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        point = rs.getInt(1);
                        if (point >= ServerConfig.mileageDailyLimitMax) {
                            int n = 1;
                            return n;
                        }
                        if (point + quantity > ServerConfig.mileageDailyLimitMax) {
                            quantity = ServerConfig.mileageDailyLimitMax - point;
                        }
                        mileageDaily = point + quantity;
                    }
                    ps.close();
                }
                if (ServerConfig.mileageMonthlyLimitMax > 0) {
                    ps = con2.prepareStatement("SELECT SUM(mileage) FROM mileage_recharge_record WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m') = DATE_FORMAT(CURDATE(), '%Y%m')");
                    ps.setInt(1, this.getAccID());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        point = rs.getInt(1);
                        if (point >= ServerConfig.mileageMonthlyLimitMax) {
                            int n = 2;
                            return n;
                        }
                        if (point + quantity > ServerConfig.mileageMonthlyLimitMax) {
                            quantity = ServerConfig.mileageMonthlyLimitMax - point;
                        }
                        mileageMonthly = point + quantity;
                    }
                    ps.close();
                }
                this.announce(UIPacket.addPopupSay(9030200, 1100, "里程上限：" + (String)(ServerConfig.mileageDailyLimitMax > 0 ? "\r\n每日：(" + mileageDaily + "/" + ServerConfig.mileageDailyLimitMax + ")" : "") + (String)(ServerConfig.mileageMonthlyLimitMax > 0 ? "\r\n每月：(" + mileageMonthly + "/" + ServerConfig.mileageMonthlyLimitMax + ")" : ""), ""));
            }
            catch (SQLException con2) {
                // empty catch block
            }
        }
        Calendar date = Calendar.getInstance();
        date.add(2, 1);
        date.set(5, 0);
        date.set(11, 9);
        date.set(12, 0);
        date.set(13, 0);
        date.set(14, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            if (quantity < 0) {
                ps = con.prepareStatement("SELECT mileage FROM mileage_record WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m%d') = ?");
                ps.setInt(1, this.getAccID());
                ps.setString(2, format.format(date.getTime()));
                rs = ps.executeQuery();
                if (rs.next()) {
                    point = 0;
                    for (Pair<Integer, Long> record : recordList) {
                        if (!format.format(new Date(record.getRight())).equalsIgnoreCase(format.format(date.getTime()))) continue;
                        point = record.getLeft();
                        break;
                    }
                    if (point + quantity > 0) {
                        ps = con.prepareStatement("UPDATE mileage_record SET mileage = mileage + ? WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m%d') = ?");
                        ps.setInt(1, quantity);
                        ps.setInt(2, this.getAccID());
                        ps.setString(3, format.format(date.getTime()));
                        ps.executeUpdate();
                        int n = 0;
                        return n;
                    }
                    ps = con.prepareStatement("DELETE FROM mileage_record WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m%d') = ?");
                    ps.setInt(1, this.getAccID());
                    ps.setString(2, format.format(date.getTime()));
                    ps.execute();
                    ps.close();
                    quantity = point + quantity;
                    if (quantity == 0) {
                        int n = 0;
                        return n;
                    }
                }
            }
            date.add(2, 1);
            ps = con.prepareStatement("SELECT mileage FROM mileage_record WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m%d') = ?");
            ps.setInt(1, this.getAccID());
            ps.setString(2, format.format(date.getTime()));
            rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                if (quantity < 0) {
                    point = 0;
                    for (Pair<Integer, Long> record : recordList) {
                        if (!format.format(new Date(record.getRight())).equalsIgnoreCase(format.format(date.getTime()))) continue;
                        point = record.getLeft();
                        break;
                    }
                    if (point + quantity <= 0) {
                        ps = con.prepareStatement("DELETE FROM mileage_record WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m%d') = ?");
                        ps.setInt(1, this.getAccID());
                        ps.setString(2, format.format(date.getTime()));
                        ps.execute();
                        ps.close();
                        int n = 0;
                        return n;
                    }
                }
                ps = con.prepareStatement("UPDATE mileage_record SET mileage = mileage + ? WHERE accId = ? AND DATE_FORMAT(Time, '%Y%m%d') = ?");
                ps.setInt(1, quantity);
                ps.setInt(2, this.getAccID());
                ps.setString(3, format.format(date.getTime()));
            } else {
                ps.close();
                if (quantity < 0) {
                    int n = -1;
                    return n;
                }
                ps = con.prepareStatement("INSERT INTO `mileage_record` (accId, mileage, Time) VALUES (?, ?, ?)");
                ps.setInt(1, this.getAccID());
                ps.setInt(2, quantity);
                ps.setTimestamp(3, new Timestamp(date.getTimeInMillis()));
            }
            ps.execute();
            ps.close();
            return 0;
        }
        catch (SQLException e) {
            return -1;
        }
    }

    public Map<String, String> getAccInfoFromDB() {
        HashMap<String, String> ret = new HashMap<String, String>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");){
            ps.setInt(1, this.accId);
            try (ResultSet rs = ps.executeQuery();){
                ResultSetMetaData metaData = rs.getMetaData();
                if (rs.next()) {
                    for (int i = 1; i <= metaData.getColumnCount(); ++i) {
                        String result = "";
                        result = rs.getString(metaData.getColumnName(i));
                        ret.put(metaData.getColumnName(i), result);
                    }
                }
            }
        }
        catch (SQLException e) {
            log.error("獲取帳號數據失敗", e);
        }
        return ret;
    }

    public void decryptOpcode(byte[] packet) {
        int b1 = packet[0] & 0xFF;
        int b2 = packet[1] & 0xFF;
        short op = (short)((b2 << 8) + b1);
        if (this.encryptedOpcodes != null && this.encryptedOpcodes.containsKey(op)) {
            short nop = this.encryptedOpcodes.get(op);
            packet[0] = (byte)(nop & 0xFF);
            packet[1] = (byte)(nop >> 8 & 0xFF);
        }
    }

    public byte[] getEncryptOpcodesData(String key) {
        if (Opcodes.isEmpty() || OpcodeEncryptPacket == null) {
            OpcodeEncryptPacket = this.getEncryptOpcodesData(key.getBytes());
            Opcodes.putAll(this.encryptedOpcodes);
        } else {
            this.encryptedOpcodes.clear();
            this.encryptedOpcodes.putAll(Opcodes);
        }
        return OpcodeEncryptPacket;
    }

    public byte[] getEncryptOpcodesData(byte[] keyBytes) {
        StringBuilder string = new StringBuilder();
        this.encryptedOpcodes.clear();
        short maxcount = (short)(InHeader.Count.getValue() - (InHeader.CP_BEGIN_USER.getValue() + 1));
        string.append(String.format("%d", maxcount));
        this.encryptedOpcodes.put(maxcount, InHeader.CP_BEGIN_USER.getValue());
        for (short i = InHeader.CP_BEGIN_USER.getValue(); i < InHeader.Count.getValue(); i = (short)(i + 1)) {
            short rand = 0;
            while (rand == 0 || this.encryptedOpcodes.containsKey(rand)) {
                rand = (short)Randomizer.rand(InHeader.CP_BEGIN_USER.getValue(), Short.MAX_VALUE);
            }
            String randStr = String.format("|%d", rand);
            if (this.encryptedOpcodes.containsKey(rand)) continue;
            this.encryptedOpcodes.put(rand, i);
            string.append(randStr);
        }
        String ss = "1653|38598|48942|30411|60254|40376|51497|17129|29308|57303|40586|55402|45814|17543|27594|59662|9843|52544|21560|60070|44826|54220|61085|29938|6095|42909|51509|28408|25363|12816|10595|13901|46667|5235|13530|17847|50864|49708|46210|39774|6927|46619|25216|27434|41094|12835|34686|49575|39673|13331|21198|29788|28863|30629|8611|27564|60396|14590|6135|5143|59767|11333|51948|16601|52625|59901|29797|5111|20850|63598|42591|19373|7490|37816|28663|64469|16913|24316|16853|38425|58304|19707|15963|45424|20181|26795|11546|43920|49197|38379|27195|39760|50405|48271|58509|55239|58375|31420|16200|38189|58098|56939|59269|18009|11977|63694|10953|6544|30721|45024|26376|38568|10494|26943|52082|20408|22763|37213|12599|57977|44702|62524|6975|39759|31527|5733|35616|50185|63817|24035|40875|49921|35024|29381|63939|53697|29090|52796|17939|20915|57360|27659|21918|8961|58188|55824|14397|30997|13473|18085|24061|12427|11388|37545|50976|42898|44745|61132|20174|41245|22141|10702|19438|54559|58039|50609|20449|59066|59610|10837|55066|59015|13981|43248|21652|26833|45511|56053|6457|25490|46636|10886|14986|35339|28046|33417|12580|15050|52202|35020|19913|43138|41417|43720|16250|34420|14794|49968|6239|44046|16994|35863|45002|7977|41000|58704|57634|9934|54714|60945|51606|5474|10864|52004|28827|36232|57473|25300|29070|56442|55803|13970|10991|45126|18312|35516|33107|30488|10067|37437|6737|45383|50059|27397|27495|26938|45345|33312|47762|7393|47671|51314|54960|16652|50162|11314|53315|24471|10391|42493|6561|51771|54838|28509|34428|25856|7186|63049|7069|13554|61652|20515|40070|31597|41711|27022|17596|49545|37510|24380|59784|35568|61616|33990|57615|10622|65116|30317|53650|29747|50486|29931|47327|63979|24461|42942|12282|39179|31115|25634|16620|31063|40862|8283|56513|51334|22711|54854|18059|41563|6211|37786|51878|45080|17736|45410|10664|34852|25290|59408|38714|6408|29778|59428|40800|41386|56701|35597|44700|23211|10438|23468|22127|44264|37667|47330|14936|22395|28960|37316|53011|18715|13572|23121|21082|54987|44779|16285|61820|22744|28722|30566|14544|21290|51654|18393|26592|15429|36622|7607|11643|46347|8139|8787|22864|31958|41335|52017|35681|41227|44794|47834|56039|26924|6682|20121|38532|56227|14126|57325|45665|18868|63356|12898|21890|38097|45007|30753|13887|24332|21297|54837|12776|60948|21452|23378|33721|14077|40246|56740|47766|47905|10338|10319|23919|39565|21634|52698|39587|37673|17973|57799|35402|46588|48774|60568|18120|38224|14823|37367|20173|35427|27082|13162|53046|64810|15178|37509|58222|14086|21100|16490|54087|31330|20836|23176|53145|40141|13963|22217|61755|9521|36052|13723|6511|39988|28594|5433|44064|19771|47770|54772|32944|32052|20744|30881|48414|28727|23990|13568|33498|11251|34568|13099|26926|53050|64036|55912|26011|7893|7197|48227|13696|6488|13842|37077|59207|41266|22826|10555|31021|21885|65531|60319|36099|46680|38925|62475|24749|7791|29324|40907|22602|57120|27001|27680|42897|21665|59308|6240|33064|51704|38472|21446|6694|48875|43849|39750|12494|65185|40344|50067|14090|16850|52046|44375|36085|6578|9548|11632|37032|20807|15996|62795|29039|44164|31236|10986|28554|39616|20314|48211|34708|41183|51950|8885|53422|46274|33688|43505|32407|21978|35441|32016|55262|56499|44881|52751|38975|64750|35444|37569|18753|37275|63543|41868|5544|10716|25447|25559|42832|18754|64204|29738|36074|39186|60926|10744|39634|15311|32862|15225|62961|36602|9936|46113|39567|33514|63276|28996|42480|37620|8565|59849|41277|51670|17761|60126|50582|20889|63563|51964|33851|18915|50402|19101|41760|39234|49088|48071|30136|6261|59194|37242|35524|54544|29568|47516|58639|15773|11102|16789|56261|60395|45432|48134|58810|9112|32538|16903|40263|53633|36481|30950|53503|48197|30660|56808|57994|44022|33535|13012|57050|40809|9282|41211|7730|43944|36482|35718|15082|51324|25335|26426|63157|16098|5503|11859|28634|38813|50390|18231|57375|25323|19536|21137|19095|16648|56951|60775|17949|60235|50296|17654|47416|32192|26171|45982|38005|37835|21702|48547|22149|10000|47093|9015|32950|51046|45688|34757|60764|37570|61471|23821|23508|53106|28214|42331|32276|57017|24002|53311|42664|13921|38260|22844|48122|63572|57842|51901|21300|52552|30989|43859|31284|13147|64755|14192|64125|43726|43231|14050|28701|39398|30581|41625|14417|31020|14256|30115|32281|51637|37993|46381|49205|6982|53171|27420|8525|14067|43696|46376|48173|39438|49411|55358|29581|14395|42418|27401|60570|42288|64075|57066|27973|35928|17132|58642|60648|39309|20662|11605|60623|52688|42841|61797|37142|37650|6876|27045|48551|54286|35552|55464|23644|36211|30970|51790|59879|9724|65264|40186|55045|25069|18791|53689|48498|25894|19895|60820|6336|37330|20348|25461|8059|26139|6966|48747|56066|11972|14774|48871|16707|28748|13520|44946|11018|20530|23818|11368|38505|59196|37312|53666|35077|37307|50426|40845|6845|55148|39379|56006|16725|56538|60438|6848|40677|10927|42187|37418|29107|36455|19966|24868|21207|57399|54384|16522|27569|45310|40098|26913|18516|54385|41886|24653|24021|11573|45338|64966|56598|15645|39285|17117|32198|27450|11507|28593|38778|35189|58785|38370|31697|8601|63931|12834|24089|20867|62370|5722|40384|64857|64814|33117|53322|40717|29912|47587|54523|47456|32921|58953|20303|6917|8046|8040|29846|65171|49476|32433|19372|35088|20106|13176|46305|50798|24424|30403|50336|53657|26042|23058|21877|14393|32520|52339|8981|29280|26578|42231|27094|48135|37434|36704|44067|24717|22678|64126|30767|30622|59141|56737|56666|53364|15980|45879|47473|6610|49054|41797|44204|57772|53716|14117|63343|44048|55903|41027|40726|24134|17936|51522|23672|7174|15394|36975|31683|16374|64475|57426|30669|18898|16895|45019|6302|28851|38225|36412|29936|17435|62677|37960|12469|32075|37172|40860|50223|42005|29133|14797|55928|50374|23551|57491|30026|14829|49510|61897|23021|45481|59500|39660|14095|57832|42912|36612|56347|60189|24886|19703|16649|52957|35957|20430|49077|64864|42732|41503|52459|9343|22804|16841|44842|11537|49071|62755|40504|45091|52583|41637|40696|40488|23022|24174|13297|25072|63365|35822|56745|38093|42463|5755|7486|54229|7266|39481|7326|17133|56406|46669|41287|58933|38254|31567|47372|37610|41102|48149|46624|31570|46414|41870|24240|15392|29793|17309|63437|7223|37999|42693|9670|37453|5980|42731|60907|25326|9836|34658|17814|38080|35993|45058|40808|32347|49307|50989|41627|14526|34074|20806|14358|20900|48382|27740|12591|44634|13810|16576|52354|35344|18412|45234|54863|59953|7854|35362|7792|26290|29750|55789|38775|7657|15556|46974|8033|46673|50386|41438|55195|31986|26731|12014|64117|6600|53306|54081|13900|26349|20755|54767|36151|54078|49426|29746|29451|37408|26977|38597|39378|19997|20210|41164|18202|59462|19678|48393|36918|26593|58852|33752|63164|6109|34570|41792|8650|11066|5062|38373|52215|21932|52250|38673|32063|60701|33295|13849|36035|26900|8599|41779|64103|14030|48728|54613|59746|28355|65187|12322|13183|22682|30988|45738|42873|24515|54888|12822|31004|12313|53979|47431|6838|39574|48207|38603|34580|53736|65094|45574|41912|30591|28730|8615|53521|39184|44383|42272|38741|41167|19173|42399|61992|53759|27927|59936|20232|25624|46179|65006|15418|40963|21604|20718|12018|33240|19998|52294|63266|46685|27891|15907|61418|14472|10971|35030|63682|28871|43042|53654|61915|11755|45898|61059|24631|15486|39687|10280|44471|48351|7333|24074|43740|60861|15697|22614|31347|62177|24000|24450|63604|36276|21428|43722|6506|31805|63089|40245|40330|28230|28065|61316|19314|58919|20657|21058|7188|60538|44061|34601|10546|47277|30043|13711|43431|21492|14389|21096|23368|24548|40773|41144|29674|13100|8765|55867|35567|58769|26418|43623|62781|63753|58772|60312|6939|37833|19234|52972|11190|61763|36302|40103|5750|65109|10990|65183|5626|39642|31668|53279|62290|21153|51162|43664|19377|25643|62330|56282|63271|55782|8190|41198|12089|15370|56736|16119|11385|15743|29144|29838|52801|56259|46462|42214|41114|28066|43506|60286|37642|20778|13569|24272|7873|57633|65153|34569|59368|54320|36278|6800|23875|26009|49021|26997|46828|9335|36415|37057|30917|35535|57586|35999|15315|31356|29860|55324|65407|25738|14574|22107|59397|12446|46590|33022|9805|54306|49272|64692|49044|20988|23682|8729|38858|48541|16060|6629|29285|51252|36165|43103|41390|25566|12500|27116|58357|54210|8351|57087|42238|20640|54805|48731|43773|43059|30964|50663|38829|10419|48979|21423|26930|54475|56552|35591|42405|38651|21283|65374|21018|47891|26460|26283|45650|61973|41379|6416|25121|46260|51051|61129|60678|49019|21447|54637|7132|46355|22245|35092|48984|58671|50179|26098|10431|12920|7697|7772|48342|52076|11362|16888|27864|32638|57127|16722|59763|64507|17822|30561|44326|29916|47458|48622|33921|33637|30681|21388|11809|61274|18690|41175|43600|12269|34207|52917|34325|38455|37003|33750|32910|57169|45877|48788|24941|45640|60638|64562|15551|59460|29238|14313|17879|25510|12473|33289|55289|25175|9742|17708|31775|56853|48371|6802|56444|58730|53835|15160|25472|60539|46908|25033|55164|63078|54038|16695|55587|56449|16229|48027|21983|34086|29186|32524|60818|35803|27409|31108|54510|8713|51703|8460|45737|33225|22790|57052|25588|31029|64689|25061|5868|20769|53029|38959|41423|5091|37444|6984|26069|26915|5981|39568|31673|29501|32454|26745|19641|59206|31217|55656|56655|55564|17165|34440|11487|8306|52945|29608|64118|5803|59607|10602|28601|24376|62971|64994|21660|11922|47341|47537|20169|35514|48761|52369|30154|48422|42203|21699|22179|9300|26738|7097|45858|17226|27338|29234|36747|59331|19809|51114|41959|21213|13062|26422|12616|41883|49279|17690|58152|45799|61585|36311|61089|29672|8208|57589|36628|32780|16171|56337|29254|35186|59914|45299|19787|47400|20260|6733|46072|23223|10295|6427|65046|64471|38419|29058|58976|52262|48814|58992|6866|36787|20350|56447|63514|49439|53147|22516|60757|27838|57676|42094|5181|32320|10093|59764|23588|29113|58050|43179|59063|15382|50991|55489|61959|31543|36044|31176|26999|48847|24455|48710|42768|39849|12630|28538|33362|10351|55025|38227|41013|11673|14646|28077|25905|20964|59106|18023";
        String[] sp = ss.split("\\|");
        for (int i = 1; i < sp.length - 1; i = (int)((short)(i + 1))) {
            this.encryptedOpcodes.put((short)Integer.parseInt(sp[i]), (short)(InHeader.CP_BEGIN_USER.getValue() + i));
        }
        try {
            MaplePacketLittleEndianWriter encodeData = new MaplePacketLittleEndianWriter();
            encodeData.writeAsciiString(ss);
            Cipher cipher = Cipher.getInstance("DESede");
            byte[] dKey = new byte[24];
            System.arraycopy(keyBytes, 0, dKey, 0, Math.min(dKey.length, keyBytes.length));
            if (keyBytes.length < dKey.length) {
                System.arraycopy(dKey, 0, dKey, keyBytes.length, dKey.length - keyBytes.length);
            }
            cipher.init(1, new SecretKeySpec(dKey, "DESede"));
            byte[] crypted = cipher.doFinal(encodeData.getPacket());
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.LP_OpcodeEncryption.getValue());
            mplew.writeInt(crypted.length);
            mplew.write(crypted);
            return mplew.getPacket();
        }
        catch (Exception ex) {
            log.error("EncryptedOpcodes Error!", ex);
            return new byte[0];
        }
    }

    public String getRefCode() {
        if (!Auth.checkPermission("InviteRebate")) {
            return null;
        }
        String refCode = SqlTool.queryAndGet("select ref_code from accounts where id = ?", rs -> rs.getString(1), this.accId);
        if (refCode == null) {
            while (refCode == null) {
                char[] ss = new char[6];
                for (int i = 0; i < ss.length; ++i) {
                    int f = (int)(Math.random() * 3.0);
                    ss[i] = f == 0 ? (char)(65.0 + Math.random() * 14.0) : (f == 1 ? (char)(80.0 + Math.random() * 11.0) : (char)(49.0 + Math.random() * 9.0));
                }
                refCode = new String(ss);
                if (SqlTool.queryAndGet("select ref_code from accounts where ref_code = ?", rs -> rs.getString(1), refCode) == null) continue;
                refCode = null;
            }
            SqlTool.update("UPDATE accounts SET ref_code = ? WHERE id = ?", refCode, this.accId);
        }
        return refCode;
    }

    public int getRefCount(int chargeAmount) {
        if (!Auth.checkPermission("InviteRebate")) {
            return 0;
        }
        try {
            return SqlTool.queryAndGet("select count(accounts.id) from accounts, hypay where accounts.name = hypay.accname and accounts.up_id = ? and hypay.payUsed >= ?", rs -> rs.getInt(1), this.accId, chargeAmount);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public int getUpRefCount(int chargeAmount) {
        if (!Auth.checkPermission("InviteRebate")) {
            return 0;
        }
        try {
            return SqlTool.queryAndGet("select count(accounts.id) from accounts, hypay where accounts.name = hypay.accname and accounts.up_id = ? and hypay.payUsed >= ?", rs -> rs.getInt(1), this.getUpId(), chargeAmount);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public int getUpId() {
        if (!Auth.checkPermission("InviteRebate")) {
            return -1;
        }
        try {
            return SqlTool.queryAndGet("select up_id from accounts where id = ?", rs -> rs.getInt(1), this.accId);
        }
        catch (Exception e) {
            return -1;
        }
    }

    public int setUpRefCode(String upRefCode) {
        if (!Auth.checkPermission("InviteRebate")) {
            return -1;
        }
        String upName = SqlTool.queryAndGet("select name from accounts where ref_code = ? and id <> ?", rs -> rs.getString(1), upRefCode, this.accId);
        if (upName == null) {
            return 1;
        }
        int upId = SqlTool.queryAndGet("select id from accounts where ref_code = ? and id <> ?", rs -> rs.getInt(1), upRefCode, this.accId);
        try {
            SqlTool.update("UPDATE accounts SET up_ref_code = ? WHERE id = ?", upRefCode, this.accId);
            SqlTool.update("UPDATE accounts SET up_id = ? WHERE id = ?", upId, this.accId);
            SqlTool.update("UPDATE accounts SET up_name = ? WHERE id = ?", upName, this.accId);
            SqlTool.update("UPDATE accounts SET ref_time = CURRENT_TIMESTAMP() WHERE id = ?", this.accId);
        }
        catch (Exception e) {
            return -1;
        }
        return 0;
    }

    public void setSessionIdx(int sessionIdx) {
        this.sessionIdx = sessionIdx;
    }

    public int getSessionIdx() {
        return this.sessionIdx;
    }

    public void dispose() {
        this.announce(MaplePacketCreator.ExclRequest());
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains(Object o) {
        return false;
    }

    public Iterator<Triple<Point, Integer, List<Rectangle>>> iterator() {
        return null;
    }

    public Object[] toArray() {
        return new Object[0];
    }

    public <T> T[] toArray(T[] a) {
        return null;
    }

    public boolean add(Triple<Point, Integer, List<Rectangle>> pointIntegerListTriple) {
        return false;
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        return false;
    }

    public boolean addAll(Collection<? extends Triple<Point, Integer, List<Rectangle>>> c) {
        return false;
    }

    public boolean addAll(int index, Collection<? extends Triple<Point, Integer, List<Rectangle>>> c) {
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        return false;
    }

    public boolean retainAll(Collection<?> c) {
        return false;
    }

    public void clear() {
    }

    public Triple<Point, Integer, List<Rectangle>> get(int index) {
        return null;
    }

    public Triple<Point, Integer, List<Rectangle>> set(int index, Triple<Point, Integer, List<Rectangle>> element) {
        return null;
    }

    public void add(int index, Triple<Point, Integer, List<Rectangle>> element) {
    }

    public Triple<Point, Integer, List<Rectangle>> remove(int index) {
        return null;
    }

    public int indexOf(Object o) {
        return 0;
    }

    public int lastIndexOf(Object o) {
        return 0;
    }

    public ListIterator<Triple<Point, Integer, List<Rectangle>>> listIterator() {
        return null;
    }

    public ListIterator<Triple<Point, Integer, List<Rectangle>>> listIterator(int index) {
        return null;
    }

    public List<Triple<Point, Integer, List<Rectangle>>> subList(int fromIndex, int toIndex) {
        return null;
    }

    public MapleCharacter getRandomCharacter() {
        MapleCharacter chr = null;
        ArrayList<MapleCharacter> players = new ArrayList<MapleCharacter>();
        if (this.getPlayer().getMap().getCharacters().size() > 0) {
            players.addAll(this.getPlayer().getMap().getCharacters());
            Collections.addAll(players, new MapleCharacter[0]);
            Collections.shuffle(players);
            for (MapleCharacter chr3 : players) {
                if (!chr3.isAlive()) continue;
                chr = chr3;
                break;
            }
        } else {
            return null;
        }
        return chr;
    }

    public String getName() {
        return this.name;
    }

    protected static class CharNameAndId {
        public String name;
        public final int id;

        public CharNameAndId(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }
}

