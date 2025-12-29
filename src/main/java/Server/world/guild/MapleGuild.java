/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MapleCharacterUtil
 *  Packet.GuildPacket
 *  Server.world.WorldAllianceService
 *  Server.world.WorldBroadcastService
 *  Server.world.WorldGuildService
 *  Server.world.guild.GuildLoad
 *  Server.world.guild.MapleBBSReply
 *  Server.world.guild.MapleBBSThread
 *  Server.world.guild.MapleBBSThread$ThreadComparator
 *  Server.world.guild.MapleGuild$BCOp
 *  Server.world.guild.MapleGuildAlliance
 *  Server.world.guild.MapleGuildCharacter
 *  Server.world.guild.MapleGuildSkill
 *  lombok.Generated
 */
package Server.world.guild;

import Client.MapleCharacterUtil;
import Client.skills.SkillFactory;
import Database.DatabaseLoader;
import Net.server.buffs.MapleStatEffect;
import Packet.GuildPacket;
import Packet.MaplePacketCreator;
import Packet.PacketHelper;
import Packet.UIPacket;
import Server.world.WorldAllianceService;
import Server.world.WorldBroadcastService;
import Server.world.WorldGuildService;
import Server.world.guild.GuildLoad;
import Server.world.guild.MapleBBSReply;
import Server.world.guild.MapleBBSThread;
import Server.world.guild.MapleGuild;
import Server.world.guild.MapleGuildAlliance;
import Server.world.guild.MapleGuildCharacter;
import Server.world.guild.MapleGuildSkill;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;
import tools.data.MaplePacketLittleEndianWriter;

public class MapleGuild
implements Serializable {
    public static final long serialVersionUID = 6322150443228168192L;
    private static final Logger log = LoggerFactory.getLogger(MapleGuild.class);
    private final List<MapleGuildCharacter> members = new CopyOnWriteArrayList<MapleGuildCharacter>();
    private final List<MapleGuildCharacter> applyMembers = new ArrayList<MapleGuildCharacter>();
    private final Map<Integer, String> applyInfos = new LinkedHashMap<Integer, String>();
    private final Map<Integer, MapleGuildSkill> guildSkills = new HashMap<Integer, MapleGuildSkill>();
    private final String[] rankTitles = new String[10];
    private final int[] rankAuthority = new int[10];
    private final Map<Integer, MapleBBSThread> bbs = new HashMap<Integer, MapleBBSThread>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final int[] guildExp = new int[]{0, 15000, 60000, 135000, 240000, 375000, 540000, 735000, 960000, 1215000, 1500000, 1815000, 2160000, 2535000, 2940000, 3375000, 3840000, 4335000, 4860000, 5415000, 6000000, 6615000, 7260000, 7935000, 8640000};
    private String name;
    private String notice;
    private int id;
    private int gP;
    private int contribution;
    private int logo;
    private int logoColor;
    private int leaderId;
    private int capacity;
    private int logoBG;
    private int logoBGColor;
    private int signature;
    private int level;
    private int activities;
    private int onlineTime;
    private int age;
    private byte[] imageLogo = null;
    private boolean bDirty = true;
    private boolean proper = true;
    private boolean allowJoin = true;
    private int joinSetting = 0;
    private int reqLevel = 0;
    private int allianceId = 0;
    private int invitedId = 0;
    private boolean init = false;
    private boolean changed = false;
    private boolean changed_skills = false;
    private MapleGuildCharacter leader = null;

    public MapleGuild(int guildid) {
        this(guildid, null);
    }

    public MapleGuild(int guildid, Map<Integer, Map<Integer, MapleBBSReply>> replies) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds WHERE guildid = ?");
            ps.setInt(1, guildid);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
                rs.close();
                ps.close();
                this.id = -1;
                return;
            }
            this.id = guildid;
            this.name = rs.getString("name");
            this.gP = rs.getInt("GP");
            this.imageLogo = rs.getBytes("imageLogo");
            this.logo = rs.getInt("logo");
            this.logoColor = rs.getInt("logoColor");
            this.logoBG = rs.getInt("logoBG");
            this.logoBGColor = rs.getInt("logoBGColor");
            this.capacity = rs.getInt("capacity");
            this.rankTitles[0] = rs.getString("rank1title");
            this.rankTitles[1] = rs.getString("rank2title");
            this.rankTitles[2] = rs.getString("rank3title");
            this.rankTitles[3] = rs.getString("rank4title");
            this.rankTitles[4] = rs.getString("rank5title");
            this.rankAuthority[0] = rs.getInt("rank1authority");
            if (this.rankAuthority[0] != -1) {
                this.rankAuthority[0] = -1;
            }
            this.rankAuthority[1] = rs.getInt("rank2authority");
            this.rankAuthority[2] = rs.getInt("rank3authority");
            this.rankAuthority[3] = rs.getInt("rank4authority");
            this.rankAuthority[4] = rs.getInt("rank5authority");
            this.leaderId = rs.getInt("leader");
            this.notice = rs.getString("notice");
            this.signature = rs.getInt("signature");
            this.allianceId = rs.getInt("alliance");
            this.allowJoin = rs.getBoolean("allow_join");
            this.activities = rs.getInt("activities");
            this.onlineTime = rs.getInt("online_time");
            this.age = rs.getInt("age");
            rs.close();
            ps.close();
            MapleGuildAlliance alliance = WorldAllianceService.getInstance().getAlliance(this.allianceId);
            if (alliance == null) {
                this.allianceId = 0;
            }
            ps = con.prepareStatement("SELECT id, name, level, job, guildrank, guildContribution, alliancerank FROM characters WHERE guildid = ? ORDER BY guildrank ASC, name ASC", 1008);
            ps.setInt(1, guildid);
            rs = ps.executeQuery();
            if (!rs.first()) {
                System.err.println("公會ID: " + this.id + " 沒有成員，系統自動解散該公會。");
                rs.close();
                ps.close();
                this.writeToDB(true);
                this.proper = false;
                return;
            }
            boolean leaderCheck = false;
            byte gFix = 0;
            byte aFix = 0;
            do {
                int chrId = rs.getInt("id");
                byte gRank = rs.getByte("guildrank");
                byte aRank = rs.getByte("alliancerank");
                if (chrId == this.leaderId) {
                    leaderCheck = true;
                    if (gRank != 1) {
                        gRank = 1;
                        gFix = 1;
                    }
                    if (alliance != null) {
                        if (alliance.getLeaderId() == chrId && aRank != 1) {
                            aRank = 1;
                            aFix = 1;
                        } else if (alliance.getLeaderId() != chrId && aRank != 2) {
                            aRank = 2;
                            aFix = 2;
                        }
                    }
                } else {
                    if (gRank == 1) {
                        gRank = 2;
                        gFix = 2;
                    }
                    if (aRank < 3) {
                        aRank = 3;
                        aFix = 3;
                    }
                }
                this.members.add(new MapleGuildCharacter(chrId, (short)rs.getShort("level"), rs.getString("name"), (byte)-1, rs.getInt("job"), (byte)gRank, rs.getInt("guildContribution"), (byte)aRank, guildid, false));
            } while (rs.next());
            rs.close();
            ps.close();
            if (!leaderCheck) {
                System.err.println("會長[ " + this.leaderId + " ]沒有在公會ID為 " + this.id + " 的公會中，系統自動解散這個公會。");
                this.writeToDB(true);
                this.proper = false;
                return;
            }
            if (gFix > 0) {
                ps = con.prepareStatement("UPDATE characters SET guildrank = ? WHERE id = ?");
                ps.setByte(1, gFix);
                ps.setInt(2, this.leaderId);
                ps.executeUpdate();
                ps.close();
            }
            if (aFix > 0) {
                ps = con.prepareStatement("UPDATE characters SET alliancerank = ? WHERE id = ?");
                ps.setByte(1, aFix);
                ps.setInt(2, this.leaderId);
                ps.executeUpdate();
                ps.close();
            }
            ps = con.prepareStatement("SELECT * FROM bbs_threads WHERE guildid = ? ORDER BY localthreadid DESC");
            ps.setInt(1, guildid);
            rs = ps.executeQuery();
            while (rs.next()) {
                int tID = rs.getInt("localthreadid");
                MapleBBSThread thread = new MapleBBSThread(tID, rs.getString("name"), rs.getString("startpost"), rs.getLong("timestamp"), guildid, rs.getInt("postercid"), rs.getInt("icon"));
                if (replies != null && replies.containsKey(rs.getInt("threadid"))) {
                    thread.replies.putAll(replies.get(rs.getInt("threadid")));
                }
                this.bbs.put(tID, thread);
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM guildskills WHERE guildid = ?");
            ps.setInt(1, guildid);
            rs = ps.executeQuery();
            while (rs.next()) {
                int skillId = rs.getInt("skillid");
                if (skillId < 91000000) {
                    rs.close();
                    ps.close();
                    System.err.println("非公會技能ID: " + skillId + " 在公會ID為 " + this.id + " 的公會中，系統自動解散該公會。");
                    this.writeToDB(true);
                    this.proper = false;
                    return;
                }
                this.guildSkills.put(skillId, new MapleGuildSkill(skillId, rs.getInt("level"), rs.getLong("timestamp"), rs.getString("purchaser"), ""));
            }
            rs.close();
            ps.close();
            this.level = this.calculateLevel();
        }
        catch (SQLException se) {
            log.error("[MapleGuild] 從數據庫中加載公會信息出錯." + String.valueOf(se));
        }
    }

    public static void loadAll() {
        LinkedHashMap<Integer, Map<Integer, MapleBBSReply>> replies = new LinkedHashMap<Integer, Map<Integer, MapleBBSReply>>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bbs_replies");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int tID = rs.getInt("threadid");
                Map reply = replies.computeIfAbsent(tID, k -> new HashMap());
                reply.put(reply.size(), new MapleBBSReply(reply.size(), rs.getInt("postercid"), rs.getString("content"), rs.getLong("timestamp")));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT guildid FROM guilds");
            rs = ps.executeQuery();
            while (rs.next()) {
                WorldGuildService.getInstance().addLoadedGuild(new MapleGuild(rs.getInt("guildid"), replies));
            }
            rs.close();
            ps.close();
        }
        catch (SQLException se) {
            log.error("[MapleGuild] 從數據庫中加載公會信息出錯." + String.valueOf(se));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadAll(Object toNotify) {
        LinkedHashMap<Integer, Map<Integer, MapleBBSReply>> replies = new LinkedHashMap<Integer, Map<Integer, MapleBBSReply>>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bbs_replies");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int tID = rs.getInt("threadid");
                Map reply = replies.computeIfAbsent(tID, k -> new HashMap());
                reply.put(reply.size(), new MapleBBSReply(reply.size(), rs.getInt("postercid"), rs.getString("content"), rs.getLong("timestamp")));
            }
            rs.close();
            ps.close();
            boolean cont = false;
            ps = con.prepareStatement("SELECT guildid FROM guilds");
            rs = ps.executeQuery();
            while (rs.next()) {
                GuildLoad.QueueGuildForLoad((int)rs.getInt("guildid"), replies);
                cont = true;
            }
            rs.close();
            ps.close();
            if (!cont) {
                return;
            }
        }
        catch (SQLException se) {
            log.error("[MapleGuild] 從數據庫中加載公會信息出錯." + String.valueOf(se));
        }
        AtomicInteger FinishedThreads = new AtomicInteger(0);
        GuildLoad.Execute((Object)toNotify);
        Object object = toNotify;
        synchronized (object) {
            try {
                toNotify.wait();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        while (FinishedThreads.incrementAndGet() != 6) {
            object = toNotify;
            synchronized (object) {
                try {
                    toNotify.wait();
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static int createGuild(int leaderId, String name) {
        if (name.length() > 12) {
            return 0;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                rs.close();
                ps.close();
                int n2 = 0;
                return n2;
            }
            ps.close();
            rs.close();
            ps = con.prepareStatement("INSERT INTO guilds (`leader`, `name`, `signature`, `alliance`) VALUES (?, ?, ?, 0)", 1);
            ps.setInt(1, leaderId);
            ps.setString(2, name);
            ps.setInt(3, (int)(System.currentTimeMillis() / 1000L));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int ret = 0;
            if (rs.next()) {
                ret = rs.getInt(1);
            }
            rs.close();
            ps.close();
            int n = ret;
            return n;
        }
        catch (SQLException se) {
            log.error("[MapleGuild] 創建公會信息出錯." + String.valueOf(se));
            return 0;
        }
    }

    public static void setOfflineGuildStatus(int guildId, byte guildrank, int contribution, byte alliancerank, int chrId) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ?, guildContribution = ?, alliancerank = ? WHERE id = ?");
            ps.setInt(1, guildId);
            ps.setInt(2, guildrank);
            ps.setInt(3, contribution);
            ps.setInt(4, alliancerank);
            ps.setInt(5, chrId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException se) {
            System.out.println("SQLException: " + se.getLocalizedMessage());
        }
    }

    public final void writeToDB(boolean bDisband) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            if (!bDisband) {
                int i;
                StringBuilder buf = new StringBuilder("UPDATE guilds SET GP = ?, imageLogo = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ?, ");
                for (i = 1; i < 11; ++i) {
                    buf.append("rank").append(i).append("title = ?, ");
                }
                for (i = 1; i < 11; ++i) {
                    buf.append("rank").append(i).append("authority = ?, ");
                }
                buf.append("capacity = ?, notice = ?, alliance = ?, allow_join = ?, activities = ?, online_time = ?, age = ?, leader = ? WHERE guildid = ?");
                PreparedStatement ps = con.prepareStatement(buf.toString());
                ps.setInt(1, this.gP);
                if (this.imageLogo == null || this.imageLogo.length <= 0) {
                    ps.setNull(2, -3);
                } else {
                    ps.setBytes(2, this.imageLogo);
                }
                ps.setInt(3, this.logo);
                ps.setInt(4, this.logoColor);
                ps.setInt(5, this.logoBG);
                ps.setInt(6, this.logoBGColor);
                ps.setString(7, this.rankTitles[0]);
                ps.setString(8, this.rankTitles[1]);
                ps.setString(9, this.rankTitles[2]);
                ps.setString(10, this.rankTitles[3]);
                ps.setString(11, this.rankTitles[4]);
                ps.setString(12, this.rankTitles[5]);
                ps.setString(13, this.rankTitles[6]);
                ps.setString(14, this.rankTitles[7]);
                ps.setString(15, this.rankTitles[8]);
                ps.setString(16, this.rankTitles[9]);
                ps.setInt(17, this.rankAuthority[0]);
                ps.setInt(18, this.rankAuthority[1]);
                ps.setInt(19, this.rankAuthority[2]);
                ps.setInt(20, this.rankAuthority[3]);
                ps.setInt(21, this.rankAuthority[4]);
                ps.setInt(22, this.rankAuthority[5]);
                ps.setInt(23, this.rankAuthority[6]);
                ps.setInt(24, this.rankAuthority[7]);
                ps.setInt(25, this.rankAuthority[8]);
                ps.setInt(26, this.rankAuthority[9]);
                ps.setInt(27, this.capacity);
                ps.setString(28, this.notice);
                ps.setInt(29, this.allianceId);
                ps.setBoolean(30, this.allowJoin);
                ps.setInt(31, this.activities);
                ps.setInt(32, this.onlineTime);
                ps.setInt(33, this.age);
                ps.setInt(34, this.leaderId);
                ps.setInt(35, this.id);
                ps.executeUpdate();
                ps.close();
                if (this.changed) {
                    ps = con.prepareStatement("DELETE FROM bbs_threads WHERE guildid = ?");
                    ps.setInt(1, this.id);
                    ps.execute();
                    ps.close();
                    ps = con.prepareStatement("DELETE FROM bbs_replies WHERE guildid = ?");
                    ps.setInt(1, this.id);
                    ps.execute();
                    ps.close();
                    PreparedStatement pse = con.prepareStatement("INSERT INTO bbs_replies (`threadid`, `postercid`, `timestamp`, `content`, `guildid`) VALUES (?, ?, ?, ?, ?)");
                    ps = con.prepareStatement("INSERT INTO bbs_threads(`postercid`, `name`, `timestamp`, `icon`, `startpost`, `guildid`, `localthreadid`) VALUES(?, ?, ?, ?, ?, ?, ?)", 1);
                    ps.setInt(6, this.id);
                    for (MapleBBSThread bb : this.bbs.values()) {
                        ps.setInt(1, bb.ownerID);
                        ps.setString(2, bb.name);
                        ps.setLong(3, bb.timestamp);
                        ps.setInt(4, bb.icon);
                        ps.setString(5, bb.text);
                        ps.setInt(7, bb.localthreadID);
                        ps.execute();
                        ResultSet rs = ps.getGeneratedKeys();
                        if (!rs.next()) {
                            rs.close();
                            continue;
                        }
                        int ourId = rs.getInt(1);
                        rs.close();
                        pse.setInt(5, this.id);
                        for (MapleBBSReply r : bb.replies.values()) {
                            pse.setInt(1, ourId);
                            pse.setInt(2, r.ownerID);
                            pse.setLong(3, r.timestamp);
                            pse.setString(4, r.content);
                            pse.addBatch();
                        }
                    }
                    pse.executeBatch();
                    pse.close();
                    ps.close();
                }
                if (this.changed_skills) {
                    ps = con.prepareStatement("DELETE FROM guildskills WHERE guildid = ?");
                    ps.setInt(1, this.id);
                    ps.execute();
                    ps.close();
                    ps = con.prepareStatement("INSERT INTO guildskills(`guildid`, `skillid`, `level`, `timestamp`, `purchaser`) VALUES(?, ?, ?, ?, ?)");
                    ps.setInt(1, this.id);
                    for (MapleGuildSkill i2 : this.guildSkills.values()) {
                        ps.setInt(2, i2.getSkillId());
                        ps.setByte(3, (byte)i2.getLevel());
                        ps.setLong(4, i2.getTimestamp());
                        ps.setString(5, i2.getPurchaser());
                        ps.execute();
                    }
                    ps.close();
                }
                this.changed_skills = false;
                this.changed = false;
            } else {
                MapleGuildAlliance alliance;
                PreparedStatement ps = con.prepareStatement("DELETE FROM bbs_threads WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.execute();
                ps.close();
                ps = con.prepareStatement("DELETE FROM bbs_replies WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.execute();
                ps.close();
                ps = con.prepareStatement("DELETE FROM guildskills WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.execute();
                ps.close();
                ps = con.prepareStatement("DELETE FROM guilds WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.executeUpdate();
                ps.close();
                if (this.allianceId > 0 && (alliance = WorldAllianceService.getInstance().getAlliance(this.allianceId)) != null) {
                    alliance.removeGuild(this.id, false);
                }
                this.broadcast(GuildPacket.guildDisband((int)this.id));
            }
        }
        catch (SQLException se) {
            log.error("[MapleGuild] 保存公會信息出錯." + String.valueOf(se));
        }
    }

    public MapleGuildCharacter getLeader() {
        if (this.leader == null) {
            for (MapleGuildCharacter mgc : this.members) {
                if (mgc.getId() != this.leaderId) continue;
                this.leader = mgc;
                return mgc;
            }
            return null;
        }
        return this.leader;
    }

    public String getNotice() {
        if (this.notice == null) {
            return "";
        }
        return this.notice;
    }
    private enum BCOp {

        NONE, DISBAND, EMBELMCHANGE, CHAT
    }

    public void broadcast(byte[] packet) {
        this.broadcast(packet, -1, BCOp.NONE);
    }

    public void broadcast(byte[] packet, int exception) {
        this.broadcast(packet, exception, BCOp.NONE);
    }

    public void broadcast(byte[] packet, int exceptionId, BCOp bcop) {
        this.broadcast(packet, -1, exceptionId, bcop);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcast(byte[] packet, int changerID, int exceptionId, BCOp bcop) {
        this.lock.writeLock().lock();
        try {
            this.buildNotifications();
        }
        finally {
            this.lock.writeLock().unlock();
        }
        this.lock.readLock().lock();
        try {
            for (MapleGuildCharacter mgc : this.members) {
                if (bcop == BCOp.DISBAND) {
                    if (mgc.isOnline()) {
                        WorldGuildService.getInstance().setGuildAndRank(mgc.getId(), 0, 5, 0, 5);
                        continue;
                    }
                    MapleGuild.setOfflineGuildStatus(0, (byte)5, 0, (byte)5, mgc.getId());
                    continue;
                }
                if ((!mgc.isOnline() || mgc.getId() == exceptionId) && (!mgc.isOnline() || bcop != BCOp.CHAT)) continue;
                if (bcop == BCOp.EMBELMCHANGE) {
                    WorldGuildService.getInstance().changeEmblem(this.id, changerID, mgc.getId(), this);
                    continue;
                }
                WorldBroadcastService.getInstance().sendGuildPacket(mgc.getId(), packet, exceptionId, this.id, bcop == BCOp.CHAT);
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    private void buildNotifications() {
        if (!this.bDirty) {
            return;
        }
        LinkedList<Integer> mem = new LinkedList<Integer>();
        for (MapleGuildCharacter mgc : this.members) {
            if (!mgc.isOnline()) continue;
            if (mem.contains(mgc.getId()) || mgc.getGuildId() != this.id) {
                this.members.remove(mgc);
                continue;
            }
            mem.add(mgc.getId());
        }
        this.bDirty = false;
    }

    public void setOnline(int chrId, boolean online, int channel) {
        boolean isBroadcast = true;
        for (MapleGuildCharacter mgc : this.members) {
            if (mgc.getGuildId() != this.id || mgc.getId() != chrId) continue;
            if (mgc.isOnline() == online) {
                isBroadcast = false;
            }
            mgc.setOnline(online);
            mgc.setChannel((byte)channel);
            break;
        }
        if (isBroadcast) {
            this.broadcast(GuildPacket.guildMemberOnline((int)this.id, (int)chrId, (boolean)online), chrId);
            if (this.allianceId > 0) {
                WorldAllianceService.getInstance().sendGuild(GuildPacket.allianceMemberOnline((int)this.allianceId, (int)this.id, (int)chrId, (boolean)online), this.id, this.allianceId);
            }
        }
        this.bDirty = true;
        this.init = true;
    }

    public String getRankTitle(int rank) {
        return this.rankTitles[rank - 1];
    }

    public int getRankAuthority(int rank) {
        return this.rankAuthority[rank - 1];
    }

    public void setAllianceId(int a) {
        this.allianceId = a;
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET alliance = ? WHERE guildid = ?");
            ps.setInt(1, a);
            ps.setInt(2, this.id);
            ps.execute();
            ps.close();
        }
        catch (SQLException e) {
            log.error("[MapleGuild] 保存公會聯盟信息出錯." + String.valueOf(e));
        }
    }

    public void setInvitedId(int iid) {
        this.invitedId = iid;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int addGuildMember(MapleGuildCharacter newMember) {
        this.lock.writeLock().lock();
        try {
            if (this.members.size() >= this.capacity) {
                int n = 0;
                return n;
            }
            for (int i = this.members.size() - 1; i >= 0; --i) {
                if (this.members.get(i).getGuildRank() >= 5 && this.members.get(i).getName().compareTo(newMember.getName()) >= 0) continue;
                this.members.add(i + 1, newMember);
                this.bDirty = true;
                break;
            }
            for (MapleGuildCharacter mgcc : this.applyMembers) {
                if (mgcc.getId() != newMember.getId()) continue;
                this.applyMembers.remove(mgcc);
                break;
            }
            this.applyInfos.remove(newMember.getId());
        }
        finally {
            this.lock.writeLock().unlock();
        }
        this.broadcast(GuildPacket.newGuildMember((MapleGuildCharacter)newMember, null));
        this.gainGP(500, true, newMember.getId());
        if (this.allianceId > 0) {
            WorldAllianceService.getInstance().sendGuild(this.allianceId);
        }
        return 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int addGuildMember(int chrId) {
        MapleGuildCharacter newMember = this.getApplyMGC(chrId);
        if (newMember == null) {
            return 0;
        }
        this.applyMembers.remove(newMember);
        this.applyInfos.remove(chrId);
        this.lock.writeLock().lock();
        try {
            if (this.members.size() >= this.capacity) {
                int n = 0;
                return n;
            }
            for (int i = this.members.size() - 1; i >= 0; --i) {
                if (this.members.get(i).getGuildRank() >= 5 && this.members.get(i).getName().compareTo(newMember.getName()) >= 0) continue;
                this.members.add(i + 1, newMember);
                this.bDirty = true;
                break;
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        this.gainGP(500, true, newMember.getId());
        this.broadcast(GuildPacket.newGuildMember((MapleGuildCharacter)newMember, null));
        if (this.allianceId > 0) {
            WorldAllianceService.getInstance().sendGuild(this.allianceId);
        }
        return 1;
    }

    public int addGuildApplyMember(MapleGuildCharacter applyMember, String info) {
        if (this.getApplyMGC(applyMember.getId()) != null) {
            return 0;
        }
        this.applyMembers.add(applyMember);
        this.applyInfos.put(applyMember.getId(), info);
        this.broadcast(GuildPacket.newGuildMember((MapleGuildCharacter)applyMember, (String)info));
        return 1;
    }

    public int denyGuildApplyMember(int fromId) {
        MapleGuildCharacter applyMember = this.getApplyMGC(fromId);
        if (applyMember == null) {
            return 0;
        }
        this.applyMembers.remove(applyMember);
        this.applyInfos.remove(fromId);
        return 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void leaveGuild(MapleGuildCharacter mgc) {
        this.lock.writeLock().lock();
        try {
            for (MapleGuildCharacter mgcc : this.members) {
                if (mgcc.getId() != mgc.getId()) continue;
                this.broadcast(GuildPacket.memberLeft((MapleGuildCharacter)mgcc, (boolean)false));
                this.bDirty = true;
                this.gainGP(mgcc.getGuildContribution() > 0 ? -mgcc.getGuildContribution() : -500);
                this.members.remove(mgcc);
                if (mgc.isOnline()) {
                    WorldGuildService.getInstance().setGuildAndRank(mgcc.getId(), 0, 5, 0, 5);
                } else {
                    MapleGuild.setOfflineGuildStatus(0, (byte)5, 0, (byte)5, mgcc.getId());
                }
                break;
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        if (this.bDirty && this.allianceId > 0) {
            WorldAllianceService.getInstance().sendGuild(this.allianceId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void expelMember(MapleGuildCharacter initiator, String name, int chrId) {
        this.lock.writeLock().lock();
        try {
            for (MapleGuildCharacter mgc : this.members) {
                if (mgc.getId() != chrId || initiator.getGuildRank() >= mgc.getGuildRank()) continue;
                this.broadcast(GuildPacket.memberLeft((MapleGuildCharacter)mgc, (boolean)true));
                this.bDirty = true;
                this.gainGP(mgc.getGuildContribution() > 0 ? -mgc.getGuildContribution() : -500);
                if (mgc.isOnline()) {
                    WorldGuildService.getInstance().setGuildAndRank(chrId, 0, 5, 0, 5);
                } else {
                    MapleCharacterUtil.sendNote((int)mgc.getId(), (String)initiator.getName(), (String)"被公會除名了。", (int)0);
                    MapleGuild.setOfflineGuildStatus(0, (byte)5, 0, (byte)5, chrId);
                }
                this.members.remove(mgc);
                break;
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        if (this.bDirty && this.allianceId > 0) {
            WorldAllianceService.getInstance().sendGuild(this.allianceId);
        }
    }

    public void changeARank() {
        this.changeARank(false);
    }

    public void changeARank(boolean leader) {
        if (this.allianceId <= 0) {
            return;
        }
        for (MapleGuildCharacter mgc : this.members) {
            byte newRank = 3;
            if (this.leaderId == mgc.getId()) {
                newRank = (byte)(leader ? 1 : 2);
            }
            if (mgc.isOnline()) {
                WorldGuildService.getInstance().setGuildAndRank(mgc.getId(), this.id, (int)mgc.getGuildRank(), mgc.getGuildContribution(), (int)newRank);
            } else {
                MapleGuild.setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank, mgc.getId());
            }
            mgc.setAllianceRank(newRank);
        }
        WorldAllianceService.getInstance().sendGuild(this.allianceId);
    }

    public void changeARank(int newRank) {
        if (this.allianceId <= 0) {
            return;
        }
        for (MapleGuildCharacter mgc : this.members) {
            if (mgc.isOnline()) {
                WorldGuildService.getInstance().setGuildAndRank(mgc.getId(), this.id, (int)mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
            } else {
                MapleGuild.setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), (byte)newRank, mgc.getId());
            }
            mgc.setAllianceRank((byte)newRank);
        }
        WorldAllianceService.getInstance().sendGuild(this.allianceId);
    }

    public boolean changeARank(int chrId, int newRank) {
        if (this.allianceId <= 0) {
            return false;
        }
        for (MapleGuildCharacter mgc : this.members) {
            if (chrId != mgc.getId()) continue;
            if (mgc.isOnline()) {
                WorldGuildService.getInstance().setGuildAndRank(chrId, this.id, (int)mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
            } else {
                MapleGuild.setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), (byte)newRank, chrId);
            }
            mgc.setAllianceRank((byte)newRank);
            WorldAllianceService.getInstance().sendGuild(this.allianceId);
            return true;
        }
        return false;
    }

    public void changeGuildLeader(int newLeader) {
        if (this.changeRank(newLeader, 1) && this.changeRank(this.leaderId, 2)) {
            if (this.allianceId > 0) {
                byte aRank = this.getMGC(this.leaderId).getAllianceRank();
                if (aRank == 1) {
                    WorldAllianceService.getInstance().changeAllianceLeader(this.allianceId, newLeader, true);
                } else {
                    this.changeARank(newLeader, aRank);
                }
                this.changeARank(this.leaderId, 3);
            }
            this.broadcast(GuildPacket.guildLeaderChanged((int)this.id, (int)this.leaderId, (int)newLeader, (int)this.allianceId));
            this.leaderId = newLeader;
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                PreparedStatement ps = con.prepareStatement("UPDATE guilds SET leader = ? WHERE guildid = ?");
                ps.setInt(1, newLeader);
                ps.setInt(2, this.id);
                ps.execute();
                ps.close();
            }
            catch (SQLException e) {
                log.error("[MapleGuild] Saving leaderid ERROR." + String.valueOf(e));
            }
        }
    }

    public boolean changeRank(int chrId, int newRank) {
        for (MapleGuildCharacter mgc : this.members) {
            if (chrId != mgc.getId()) continue;
            if (mgc.isOnline()) {
                WorldGuildService.getInstance().setGuildAndRank(chrId, this.id, newRank, mgc.getGuildContribution(), (int)mgc.getAllianceRank());
            } else {
                MapleGuild.setOfflineGuildStatus(this.id, (byte)newRank, mgc.getGuildContribution(), mgc.getAllianceRank(), chrId);
            }
            mgc.setGuildRank((byte)newRank);
            this.broadcast(GuildPacket.changeRank((MapleGuildCharacter)mgc));
            return true;
        }
        return false;
    }

    public void setGuildNotice(int cid, String notice) {
        this.notice = notice;
        this.broadcast(GuildPacket.guildNotice((int)this.id, (int)cid, (String)notice));
    }

    public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
        for (MapleGuildCharacter member : this.members) {
            if (member.getId() != mgc.getId()) continue;
            int old_level = member.getLevel();
            int old_job = member.getJobId();
            member.setJobId(mgc.getJobId());
            member.setLevel((int)((short)mgc.getLevel()));
            if (mgc.getLevel() > old_level) {
                this.gainGP((mgc.getLevel() - old_level) * mgc.getLevel(), false, mgc.getId());
            }
            if (old_level != mgc.getLevel()) {
                this.broadcast(MaplePacketCreator.sendLevelup(false, mgc.getLevel(), mgc.getName()), mgc.getId());
            }
            if (old_job != mgc.getJobId()) {
                this.broadcast(MaplePacketCreator.sendJobup(false, mgc.getJobId(), mgc.getName()), mgc.getId());
            }
            this.broadcast(GuildPacket.guildMemberLevelJobUpdate((MapleGuildCharacter)mgc));
            if (this.allianceId <= 0) break;
            WorldAllianceService.getInstance().sendGuild(GuildPacket.updateAlliance((MapleGuildCharacter)mgc, (int)this.allianceId), this.id, this.allianceId);
            break;
        }
    }

    public void changeGradeNameAndAuthority(int changerID, byte gradeIndex, String name, int authority) {
        if (gradeIndex == 1) {
            authority = -1;
        }
        this.rankTitles[gradeIndex - 1] = name;
        this.rankAuthority[gradeIndex - 1] = authority;
        this.broadcast(GuildPacket.gradeNameAndAuthorityChange((int)this.id, (int)changerID, (String[])this.rankTitles, (int[])this.rankAuthority));
    }

    public void disbandGuild() {
        this.writeToDB(true);
        this.broadcast(null, -1, BCOp.DISBAND);
    }

    public void setGuildEmblem(int changerID, short bg, byte bgcolor, short logo, byte logocolor) {
        this.logoBG = bg;
        this.logoBGColor = bgcolor;
        this.logo = logo;
        this.logoColor = logocolor;
        this.imageLogo = null;
        this.broadcast(null, changerID, -1, BCOp.EMBELMCHANGE);
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET imageLogo = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ? WHERE guildid = ?");
            ps.setNull(1, -3);
            ps.setInt(2, logo);
            ps.setInt(3, this.logoColor);
            ps.setInt(4, this.logoBG);
            ps.setInt(5, this.logoBGColor);
            ps.setInt(6, this.id);
            ps.execute();
            ps.close();
        }
        catch (SQLException e) {
            log.error("[MapleGuild] Saving guild logo / BG colo ERROR." + String.valueOf(e));
        }
    }

    public void setGuildEmblem(int changerID, byte[] imageMark) {
        if (imageMark == null || imageMark.length <= 0 || imageMark.length > 60000) {
            return;
        }
        this.logoBG = 0;
        this.logoBGColor = 0;
        this.logo = 0;
        this.logoColor = 0;
        this.imageLogo = imageMark;
        this.broadcast(null, changerID, -1, BCOp.EMBELMCHANGE);
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET imageLogo = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ? WHERE guildid = ?");
            ps.setBytes(1, this.imageLogo);
            ps.setInt(2, this.logo);
            ps.setInt(3, this.logoColor);
            ps.setInt(4, this.logoBG);
            ps.setInt(5, this.logoBGColor);
            ps.setInt(6, this.id);
            ps.execute();
            ps.close();
        }
        catch (SQLException e) {
            log.error("[MapleGuild] Saving guild logo / BG colo ERROR." + String.valueOf(e));
        }
    }

    public MapleGuildCharacter getMGC(int chrId) {
        for (MapleGuildCharacter mgc : this.members) {
            if (mgc.getId() != chrId) continue;
            return mgc;
        }
        return null;
    }

    public MapleGuildCharacter getApplyMGC(int chrId) {
        for (MapleGuildCharacter mgc : this.applyMembers) {
            if (mgc.getId() != chrId) continue;
            return mgc;
        }
        return null;
    }

    public boolean increaseCapacity(boolean trueMax) {
        if (this.capacity >= (trueMax ? 200 : 100) || this.capacity + 5 > (trueMax ? 200 : 100)) {
            return false;
        }
        if (trueMax && this.gP < 25000) {
            return false;
        }
        if (trueMax && this.gP - 25000 < this.getGuildExpNeededForLevel(this.getLevel() - 1)) {
            return false;
        }
        this.capacity += 5;
        this.broadcast(GuildPacket.guildCapacityChange((int)this.id, (int)this.capacity));
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET capacity = ? WHERE guildid = ?");
            ps.setInt(1, this.capacity);
            ps.setInt(2, this.id);
            ps.execute();
            ps.close();
        }
        catch (SQLException e) {
            log.error("[MapleGuild] Saving guild capacity ERROR." + String.valueOf(e));
        }
        return true;
    }

    public void gainGP(int amount) {
        this.gainGP(amount, true, -1);
    }

    public void gainGP(int amount, boolean broadcast) {
        this.gainGP(amount, broadcast, -1);
    }

    public void gainGP(int amount, boolean broadcast, int chrId) {
        MapleGuildCharacter mgc;
        if (amount == 0) {
            return;
        }
        if (amount + this.gP < 0) {
            amount = -this.gP;
        }
        this.gP += amount;
        this.level = this.calculateLevel();
        this.broadcast(GuildPacket.updateGuildInfo((int)this.id, (int)this.gP, (int)this.level));
        if (chrId > 0 && amount > 0 && (mgc = this.getMGC(chrId)) != null) {
            mgc.setGuildContribution(mgc.getGuildContribution() + amount);
            if (mgc.isOnline()) {
                WorldGuildService.getInstance().setGuildAndRank(chrId, this.id, (int)mgc.getGuildRank(), mgc.getGuildContribution(), (int)mgc.getAllianceRank());
            } else {
                MapleGuild.setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), mgc.getAllianceRank(), chrId);
            }
            this.broadcast(GuildPacket.updatePlayerContribution((int)this.id, (int)chrId, (int)mgc.getGuildContribution()));
        }
        if (broadcast) {
            this.broadcast(UIPacket.getGPMsg(amount));
        }
    }

    public Collection<MapleGuildSkill> getSkills() {
        return this.guildSkills.values();
    }

    public int getSkillLevel(int skillId) {
        if (!this.guildSkills.containsKey(skillId)) {
            return 0;
        }
        return this.guildSkills.get(skillId).getLevel();
    }

    public boolean activateSkill(int skillId, int changerID, String name) {
        if (!this.guildSkills.containsKey(skillId)) {
            return false;
        }
        MapleGuildSkill ourSkill = this.guildSkills.get(skillId);
        MapleStatEffect effect = SkillFactory.getSkill(skillId).getEffect(ourSkill.getLevel());
        if (ourSkill.getTimestamp() > System.currentTimeMillis() || effect.getPeriod() <= 0) {
            return false;
        }
        ourSkill.setTimestamp(System.currentTimeMillis() + (long)effect.getPeriod() * 60000L);
        ourSkill.setActivator(name);
        this.broadcast(GuildPacket.guildSkillPurchased((int)this.id, (int)skillId, (int)changerID, (int)ourSkill.getSkillId(), (long)ourSkill.getTimestamp(), (String)ourSkill.getPurchaser(), (String)name));
        return true;
    }

    public boolean purchaseSkill(int skillId, String name, int chrId) {
        MapleStatEffect effect = SkillFactory.getSkill(skillId).getEffect(this.getSkillLevel(skillId) + 1);
        if (effect.getReqGuildLevel() > this.getLevel() || effect.getLevel() <= this.getSkillLevel(skillId)) {
            return false;
        }
        MapleGuildSkill ourSkill = this.guildSkills.get(skillId);
        if (ourSkill == null) {
            ourSkill = new MapleGuildSkill(skillId, effect.getLevel(), 0L, name, name);
            this.guildSkills.put(skillId, ourSkill);
        } else {
            ourSkill.setLevel(effect.getLevel());
            ourSkill.setPurchaser(name);
            ourSkill.setActivator(name);
        }
        if (effect.getPeriod() <= 0) {
            ourSkill.setTimestamp(-1L);
        } else {
            ourSkill.setTimestamp(System.currentTimeMillis() + (long)effect.getPeriod() * 60000L);
        }
        this.changed_skills = true;
        this.gainGP(1000, true, chrId);
        this.broadcast(GuildPacket.guildSkillPurchased((int)this.id, (int)skillId, (int)chrId, (int)ourSkill.getLevel(), (long)ourSkill.getTimestamp(), (String)name, (String)name));
        return true;
    }

    public final int calculateLevel() {
        for (int i = 1; i < 10; ++i) {
            if (this.gP >= this.getGuildExpNeededForLevel(i)) continue;
            return i;
        }
        return 10;
    }

    public int getGuildExpNeededForLevel(int levelx) {
        if (levelx < 0 || levelx >= this.guildExp.length) {
            return Integer.MAX_VALUE;
        }
        return this.guildExp[levelx];
    }

    public void addMemberData(MaplePacketLittleEndianWriter mplew) {
        ArrayList<MapleGuildCharacter> players = new ArrayList<MapleGuildCharacter>();
        for (MapleGuildCharacter mgc : this.members) {
            if (mgc.getId() != this.leaderId) continue;
            players.add(mgc);
        }
        for (MapleGuildCharacter mgc : this.members) {
            if (mgc.getId() == this.leaderId) continue;
            players.add(mgc);
        }
        if (players.size() != this.members.size()) {
            System.out.println("公會成員信息加載錯誤 - 實際加載: " + players.size() + " 應當加載: " + this.members.size());
        }
        mplew.writeShort(this.applyMembers.size());
        for (MapleGuildCharacter mgc : this.applyMembers) {
            mgc.encodeData(mplew);
        }
        mplew.writeShort(players.size());
        for (MapleGuildCharacter mgc : players) {
            mgc.encodeData(mplew);
        }
    }

    public Collection<MapleGuildCharacter> getMembers() {
        return Collections.unmodifiableCollection(this.members);
    }

    public void encodeInfoData(MaplePacketLittleEndianWriter mplew) {
        mplew.writeInt(this.id);
        mplew.write(this.level);
        mplew.writeMapleAsciiString(this.name);
        MapleGuildCharacter gLeader = this.getLeader();
        mplew.writeMapleAsciiString(gLeader == null ? "" : gLeader.getName());
        mplew.writeShort(this.members.size());
        mplew.writeShort(gLeader == null ? 1 : gLeader.getLevel());
        mplew.write(false);
        mplew.writeLong(0L);
        mplew.write(this.allowJoin);
        mplew.writeMapleAsciiString(this.getNotice());
        mplew.writeInt(this.activities);
        mplew.writeInt(this.onlineTime);
        mplew.writeInt(this.age);
        mplew.write(false);
    }

    public void encode(MaplePacketLittleEndianWriter mplew) {
        int i;
        mplew.writeInt(this.getId());
        mplew.writeMapleAsciiString(this.getName());
        for (int i2 = 1; i2 <= 10; ++i2) {
            mplew.writeMapleAsciiString(this.getRankTitle(i2));
            mplew.writeInt(this.getRankAuthority(i2));
        }
        this.addMemberData(mplew);
        mplew.writeInt(this.getCapacity());
        mplew.writeShort(this.getLogoBG());
        mplew.write(this.getLogoBGColor());
        mplew.writeShort(this.getLogo());
        mplew.write(this.getLogoColor());
        mplew.writeMapleAsciiString(this.getNotice());
        mplew.writeInt(this.getContribution());
        mplew.writeInt(this.getGP());
        mplew.write(this.getLevel());
        mplew.writeInt(this.getAllianceId() > 0 ? this.getAllianceId() : 0);
        mplew.writeInt(this.getGP());
        mplew.writeInt(Integer.parseInt(DateUtil.getCurrentDate().replaceAll("-", "")));
        mplew.write(this.isAllowJoin());
        mplew.writeLong(PacketHelper.getTime(-2L));
        mplew.writeInt(this.getActivities());
        mplew.writeInt(this.getOnlineTime());
        mplew.writeInt(this.getAge());
        mplew.writeShort(this.guildSkills.size());
        for (MapleGuildSkill skill : this.getSkills()) {
            mplew.writeInt(skill.getSkillId());
            skill.encode(mplew);
        }
        mplew.write(this.isAllowJoin() ? 1 : -1);
        if (this.isAllowJoin()) {
            mplew.write(this.getJoinSetting());
            mplew.writeInt(this.getReqLevel());
        }
        this.encodeImageLogo(mplew);
        mplew.write(1);
        mplew.write(1);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        for (i = 0; i < 10; ++i) {
            mplew.writeInt(3);
        }
        for (i = 0; i < 16; ++i) {
            mplew.writeInt(0);
        }
        mplew.write(0);
        mplew.writeLong(150842304000000000L);
        mplew.writeLong(150842304000000000L);
        mplew.writeLong(94354848000000000L);
        mplew.writeShort(0);
        mplew.writeShort(0);
    }

    public void encodeSkill(MaplePacketLittleEndianWriter mplew) {
    }

    public void encodeImageLogo(MaplePacketLittleEndianWriter mplew) {
        byte[] logo = this.getImageLogo();
        mplew.writeInt(logo == null ? 0 : logo.length);
        if (logo != null && logo.length > 0) {
            mplew.write(logo);
        }
        mplew.writeInt(0);
    }

    public List<MapleBBSThread> getBBS() {
        ArrayList<MapleBBSThread> ret = new ArrayList<MapleBBSThread>(this.bbs.values());
        ret.sort((Comparator<MapleBBSThread>)new MapleBBSThread.ThreadComparator());
        return ret;
    }

    public int addBBSThread(String title, String text, int icon, boolean bNotice, int posterID) {
        int add = this.bbs.get(0) == null ? 1 : 0;
        this.changed = true;
        int ret = bNotice ? 0 : Math.max(1, this.bbs.size() + add);
        this.bbs.put(ret, new MapleBBSThread(ret, title, text, System.currentTimeMillis(), this.id, posterID, icon));
        return ret;
    }

    public void editBBSThread(int localthreadid, String title, String text, int icon, int posterID, int guildRank) {
        MapleBBSThread thread = this.bbs.get(localthreadid);
        if (thread != null && (thread.ownerID == posterID || guildRank <= 2)) {
            this.changed = true;
            thread.setTitle(title);
            thread.setText(text);
            thread.setIcon(icon);
            thread.setTimestamp(System.currentTimeMillis());
        }
    }

    public void deleteBBSThread(int localthreadid, int posterID, int guildRank) {
        MapleBBSThread thread = this.bbs.get(localthreadid);
        if (thread != null && (thread.ownerID == posterID || guildRank <= 2)) {
            this.changed = true;
            this.bbs.remove(localthreadid);
        }
    }

    public void addBBSReply(int localthreadid, String text, int posterID) {
        MapleBBSThread thread = this.bbs.get(localthreadid);
        if (thread != null) {
            this.changed = true;
            thread.replies.put(thread.replies.size(), new MapleBBSReply(thread.replies.size(), posterID, text, System.currentTimeMillis()));
        }
    }

    public void deleteBBSReply(int localthreadid, int replyid, int posterID, int guildRank) {
        MapleBBSReply reply;
        MapleBBSThread thread = this.bbs.get(localthreadid);
        if (thread != null && (reply = (MapleBBSReply)thread.replies.get(replyid)) != null && (reply.ownerID == posterID || guildRank <= 2)) {
            this.changed = true;
            thread.replies.remove(replyid);
        }
    }

    public boolean hasSkill(int id) {
        return this.guildSkills.containsKey(id);
    }

    @Generated
    public int[] getGuildExp() {
        return this.guildExp;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public int getId() {
        return this.id;
    }

    @Generated
    public int getGP() {
        return this.gP;
    }

    @Generated
    public int getContribution() {
        return this.contribution;
    }

    @Generated
    public void setLogo(int logo) {
        this.logo = logo;
    }

    @Generated
    public int getLogo() {
        return this.logo;
    }

    @Generated
    public void setLogoColor(int logoColor) {
        this.logoColor = logoColor;
    }

    @Generated
    public int getLogoColor() {
        return this.logoColor;
    }

    @Generated
    public int getLeaderId() {
        return this.leaderId;
    }

    @Generated
    public int getCapacity() {
        return this.capacity;
    }

    @Generated
    public void setLogoBG(int logoBG) {
        this.logoBG = logoBG;
    }

    @Generated
    public int getLogoBG() {
        return this.logoBG;
    }

    @Generated
    public void setLogoBGColor(int logoBGColor) {
        this.logoBGColor = logoBGColor;
    }

    @Generated
    public int getLogoBGColor() {
        return this.logoBGColor;
    }

    @Generated
    public int getSignature() {
        return this.signature;
    }

    @Generated
    public int getLevel() {
        return this.level;
    }

    @Generated
    public void setActivities(int activities) {
        this.activities = activities;
    }

    @Generated
    public int getActivities() {
        return this.activities;
    }

    @Generated
    public void setOnlineTime(int onlineTime) {
        this.onlineTime = onlineTime;
    }

    @Generated
    public int getOnlineTime() {
        return this.onlineTime;
    }

    @Generated
    public void setAge(int age) {
        this.age = age;
    }

    @Generated
    public int getAge() {
        return this.age;
    }

    @Generated
    public void setImageLogo(byte[] imageLogo) {
        this.imageLogo = imageLogo;
    }

    @Generated
    public byte[] getImageLogo() {
        return this.imageLogo;
    }

    @Generated
    public void setBDirty(boolean bDirty) {
        this.bDirty = bDirty;
    }

    @Generated
    public boolean isBDirty() {
        return this.bDirty;
    }

    @Generated
    public boolean isProper() {
        return this.proper;
    }

    @Generated
    public void setAllowJoin(boolean allowJoin) {
        this.allowJoin = allowJoin;
    }

    @Generated
    public boolean isAllowJoin() {
        return this.allowJoin;
    }

    @Generated
    public void setJoinSetting(int joinSetting) {
        this.joinSetting = joinSetting;
    }

    @Generated
    public int getJoinSetting() {
        return this.joinSetting;
    }

    @Generated
    public void setReqLevel(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    @Generated
    public int getReqLevel() {
        return this.reqLevel;
    }

    @Generated
    public int getAllianceId() {
        return this.allianceId;
    }

    @Generated
    public int getInvitedId() {
        return this.invitedId;
    }

    @Generated
    public boolean isInit() {
        return this.init;
    }

    @Generated
    public boolean isChanged() {
        return this.changed;
    }

    @Generated
    public boolean isChanged_skills() {
        return this.changed_skills;
    }
}

