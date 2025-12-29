/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Database.DatabaseConnectionEx
 *  Opcode.Opcode.GuildOpcode
 *  Packet.GuildPacket
 *  Server.world.WorldAllianceService
 *  Server.world.WorldGuildService
 *  Server.world.guild.MapleGuildCharacter
 *  Server.world.guild.MapleGuildResultOption
 *  SwordieX.enums.GuildRequestType
 *  SwordieX.enums.GuildResponseType
 */
package Server.channel.handler;

import Client.MapleCharacter;
import Client.MapleClient;
import Config.configs.ServerConfig;
import Database.DatabaseConnectionEx;
import Opcode.Opcode.GuildOpcode;
import Opcode.header.OutHeader;
import Packet.GuildPacket;
import Server.world.WorldAllianceService;
import Server.world.WorldGuildService;
import Server.world.guild.MapleGuild;
import Server.world.guild.MapleGuildCharacter;
import Server.world.guild.MapleGuildResultOption;
import SwordieX.enums.GuildRequestType;
import SwordieX.enums.GuildResponseType;
import connection.OutPacket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class GuildHandler {
    private static final Logger log = LoggerFactory.getLogger(GuildHandler.class);
    private static final Map<String, Pair<Integer, Long>> invited = new HashMap<String, Pair<Integer, Long>>();
    private static final List<Integer> ApplyIDs = new ArrayList<Integer>();
    private static final ReentrantReadWriteLock applyIDsLock = new ReentrantReadWriteLock();

    public static void addApplyIDs(int id) {
        applyIDsLock.readLock().lock();
        try {
            ApplyIDs.add(id);
        }
        finally {
            applyIDsLock.readLock().unlock();
        }
    }

    public static void removeApplyIDs(int id) {
        applyIDsLock.readLock().lock();
        try {
            if (ApplyIDs.contains(id)) {
                ApplyIDs.remove((Object)id);
            }
        }
        finally {
            applyIDsLock.readLock().unlock();
        }
    }

    public static void DenyGuildRequest(String from, MapleClient c) {
        MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null && invited.remove(c.getPlayer().getName().toLowerCase()) != null) {
            cfrom.getClient().announce(GuildPacket.denyGuildInvitation((String)c.getPlayer().getName()));
        }
    }

    public static void GuildApply(MaplePacketReader slea, MapleClient c) {
        if (c.getPlayer().getGuildId() > 0) {
            return;
        }
        if (ApplyIDs.contains(c.getPlayer().getId())) {
            c.getPlayer().dropMessage(1, "您已經在公會申請列表中，暫時無法進行此操作.");
            GuildHandler.removeApplyIDs(c.getPlayer().getId());
            return;
        }
        int guildId = slea.readInt();
        String info = slea.readMapleAsciiString();
        MapleGuildCharacter guildMember = new MapleGuildCharacter(c.getPlayer());
        guildMember.setGuildId(guildId);
        int ret = WorldGuildService.getInstance().addGuildApplyMember(guildMember, info);
        if (ret == 1) {
            GuildHandler.addApplyIDs(c.getPlayer().getId());
            c.announce(GuildPacket.newGuildMember((MapleGuildCharacter)guildMember, (String)info));
            c.announce(GuildPacket.genericGuildMessage((byte)((byte)GuildOpcode.GuildRes_JoinRequest_Done.getValue())));
        } else {
            c.announce(GuildPacket.genericGuildMessage((byte)((byte)GuildOpcode.GuildRes_JoinRequest_Unknown.getValue())));
            c.getPlayer().dropMessage(1, "申請加入公會出現錯誤，請稍後再試.");
        }
    }

    public static void AcceptGuildApply(MaplePacketReader slea, MapleClient c) {
        if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
            return;
        }
        int guildId = c.getPlayer().getGuildId();
        int amount = slea.readByte();
        for (int i = 0; i < amount; ++i) {
            int fromId = slea.readInt();
            MapleCharacter from = c.getChannelServer().getPlayerStorage().getCharacterById(fromId);
            if (from != null && from.getGuildId() <= 0) {
                from.setGuildId(guildId);
                from.setGuildRank((byte)5);
                int ret = WorldGuildService.getInstance().addGuildMember(from.getMGC());
                if (ret == 0) {
                    from.setGuildId(0);
                    continue;
                }
                from.getClient().announce(GuildPacket.sendGuildResult((MapleGuildResultOption)MapleGuildResultOption.loadGuild((MapleCharacter)from)));
                MapleGuild gs = WorldGuildService.getInstance().getGuild(guildId);
                for (byte[] pack : WorldAllianceService.getInstance().getAllianceInfo(gs.getAllianceId(), true)) {
                    if (pack == null) continue;
                    from.getClient().announce(pack);
                }
                from.saveGuildStatus();
                GuildHandler.respawnPlayer(from);
            }
            if (!ApplyIDs.contains(fromId)) continue;
            GuildHandler.removeApplyIDs(fromId);
            break;
        }
    }

    public static void DenyGuildApply(MaplePacketReader slea, MapleClient c) {
        if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
            return;
        }
        int guildId = c.getPlayer().getGuildId();
        int amount = slea.readByte();
        for (int i = 0; i < amount; ++i) {
            int fromId = slea.readInt();
            WorldGuildService.getInstance().denyGuildApplyMember(guildId, fromId);
            if (!ApplyIDs.contains(fromId)) continue;
            GuildHandler.removeApplyIDs(fromId);
        }
    }

    public static void Guild(MaplePacketReader slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        MapleGuild guild = chr.getGuild();
        int typeId = slea.readInt();
        GuildRequestType grt = GuildRequestType.getTypeByVal((int)typeId);
        if (grt == null) {
            log.warn(String.format("未知的工會Request類型 %d", typeId));
            return;
        }
        switch (typeId) {
            case 1: {
                String name = slea.readMapleAsciiString();
                if (!GuildHandler.isGuildNameAcceptable(name)) {
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_NameAlreadyUsed)));
                    return;
                }
                if (guild != null) {
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_AlreadyJoinedGuild)));
                    return;
                }
                MapleGuild checkName = WorldGuildService.getInstance().getGuildByName(name);
                if (checkName != null) {
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_NameAlreadyUsed)));
                    return;
                }
                if (WorldGuildService.getInstance().existCreatingGuildName(name)) {
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_CreatingGuildAlreadyUsed)));
                    return;
                }
                WorldGuildService.getInstance().addCreatingGuildName(name, chr.getId());
                c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)MapleGuildResultOption.createGuildAgreeReply((String)name)));
                break;
            }
            case 2: {
                String guildName = WorldGuildService.getInstance().getCreatingGuildName(chr.getId());
                WorldGuildService.getInstance().removeCreatingGuildName(guildName);
                int cost = ServerConfig.CHANNEL_CREATEGUILDCOST;
                if (chr.getGuildId() > 0) {
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_NameAlreadyUsed)));
                    return;
                }
                if (chr.getMeso() < (long)cost) {
                    chr.dropMessage(1, "你沒有足夠的楓幣創建一個公會。當前創建公會需要: " + cost + " 的楓幣.");
                    return;
                }
                if (chr.getGuildId() > 0) {
                    return;
                }
                if (chr.getMeso() < 5000000L) {
                    chr.dropMessage(1, "不具有資格創建公會,需有500萬楓幣創建費用。");
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_CancelGuildCreate)));
                    return;
                }
                int guildId = WorldGuildService.getInstance().createGuild(chr.getId(), guildName);
                if (guildId == 0) {
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_CreatingGuildError)));
                    return;
                }
                chr.gainMeso(-cost, true, true);
                try {
                    chr.setGuildId(guildId);
                    chr.setGuildRank((byte)1);
                    chr.saveGuildStatus();
                    WorldGuildService.getInstance().setGuildMemberOnline(chr.getMGC(), true, c.getChannel());
                }
                catch (Exception er) {
                    log.error(er.getMessage());
                    c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)new MapleGuildResultOption(GuildResponseType.Res_CreatingGuildErrorMoney)));
                    chr.gainMeso(cost, true, true);
                    return;
                }
                c.announce(GuildPacket.sendGuildResult((MapleGuildResultOption)MapleGuildResultOption.createGuild((MapleCharacter)chr)));
                chr.updateOneInfo(26011, "GuildID", String.valueOf(guildId));
                WorldGuildService.getInstance().gainGP(chr.getGuildId(), 500, chr.getId());
                chr.dropMessage(1, "恭喜你成功創建公會.");
                GuildHandler.respawnPlayer(chr);
                break;
            }
            case 18: {
                if (c.getPlayer().getGuild().getLevel() < 2 || c.getPlayer().getGuild().getGP() < 150000) {
                    c.outPacket(OutHeader.LP_GuildResult.getValue(), 80);
                    break;
                }
                c.getPlayer().dropMessage(5, "該功能目前維護當中.");
                break;
            }
            case 19: {
                String notice = slea.readMapleAsciiString();
                if (chr.getGuildId() <= 0 || chr.getGuildRank() != 1) {
                    c.getPlayer().dropMessage(-5, "你不具有公會,無法修改。");
                } else {
                    MaplePacketLittleEndianWriter Notice = new MaplePacketLittleEndianWriter();
                    Notice.writeShort(OutHeader.LP_GuildResult.getValue());
                    Notice.writeInt(88);
                    Notice.writeInt(c.getPlayer().getGuild().getId());
                    Notice.writeInt(c.getPlayer().getId());
                    Notice.writeMapleAsciiString(notice);
                    c.announce(Notice.getPacket());
                    break;
                }
            }
            case 20: {
                byte frist = slea.readByte();
                int two = slea.readInt();
                long three = slea.readLong();
                MaplePacketLittleEndianWriter setting = new MaplePacketLittleEndianWriter();
                setting.writeShort(OutHeader.LP_GuildResult.getValue());
                setting.writeInt(90);
                setting.writeInt(c.getPlayer().getGuild().getId());
                setting.writeInt(c.getPlayer().getId());
                setting.writeInt(frist);
                setting.writeInt(two);
                setting.writeLong(three);
                c.announce(setting.getPacket());
                break;
            }
            case 21: {
                if (c.getPlayer().getGuild().getGP() >= 1500) break;
                MaplePacketLittleEndianWriter publicity = new MaplePacketLittleEndianWriter();
                publicity.writeShort(OutHeader.LP_GuildResult.getValue());
                publicity.writeInt(94);
                c.announce(publicity.getPacket());
                break;
            }
            case 35: {
                String name = slea.readMapleAsciiString();
                MaplePacketLittleEndianWriter NotJoin = new MaplePacketLittleEndianWriter();
                NotJoin.writeShort(OutHeader.LP_GuildResult.getValue());
                NotJoin.writeInt(47);
                NotJoin.writeMapleAsciiString(name);
                c.announce(NotJoin.getPacket());
                break;
            }
            case 41: {
                int skillid = slea.readInt();
                byte skilllv = slea.readByte();
                int guildid = c.getPlayer().getGuildId();
                int leaderid = c.getPlayer().getGuild().getLeader().getId();
                long timestamp = 94354848000000000L;
                String playerName = c.getPlayer().getName();
                int updateGuildSkill = 106;
                MaplePacketLittleEndianWriter skill = new MaplePacketLittleEndianWriter();
                skill.writeShort(OutHeader.LP_GuildResult.getValue());
                skill.writeInt(updateGuildSkill);
                skill.writeInt(guildid);
                skill.writeInt(skillid);
                skill.writeInt(leaderid);
                skill.write((int)skilllv);
                skill.write(0);
                skill.writeLong(94354848000000000L);
                skill.writeMapleAsciiString(playerName);
                skill.writeShort(0);
                assert (skill.getPacket().length == 42);
                String sql = "INSERT INTO guildskills (guildid, skillid, level, timestamp, purchaser) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE level = VALUES(level), timestamp = VALUES(timestamp), purchaser = VALUES(purchaser)";
                try {
                    DatabaseConnectionEx.getInstance();
                    try (PreparedStatement ps = DatabaseConnectionEx.getConnection().prepareStatement(sql);){
                        ps.setInt(1, guildid);
                        ps.setInt(2, skillid);
                        ps.setInt(3, skilllv);
                        ps.setLong(4, timestamp);
                        ps.setString(5, playerName);
                        ps.executeUpdate();
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                c.announce(skill.getPacket());
                break;
            }
            case 44: {
                int gid = slea.readInt();
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.LP_GuildResult.getValue());
                mplew.writeInt(135);
                mplew.writeInt(gid);
                mplew.write(1);
                mplew.writeInt(774);
                mplew.writeInt(1196314761);
                mplew.writeInt(169478669);
                mplew.writeInt(0xD000000);
                mplew.writeInt(1380206665);
                mplew.writeInt(0x11000000);
                mplew.writeInt(0x11000000);
                mplew.writeInt(1544);
                mplew.writeInt(1198340864);
                mplew.writeInt(250);
                mplew.writeInt(1497919497);
                mplew.writeInt(889192563);
                mplew.writeInt(889192660);
                mplew.writeInt(1700659668);
                mplew.writeInt(2277);
                mplew.writeInt(1145681922);
                mplew.writeInt(-1925688255);
                mplew.writeInt(1808765821);
                mplew.writeInt(-954968740);
                mplew.writeInt(-69277889);
                mplew.writeInt(-1992844322);
                mplew.writeInt(-1861947850);
                mplew.writeInt(-1330073146);
                mplew.writeInt(-1769287966);
                mplew.writeInt(-824086086);
                mplew.writeInt(811526670);
                mplew.writeInt(146424194);
                mplew.writeInt(-1543217816);
                mplew.writeInt(-668222760);
                mplew.writeInt(-2002475608);
                mplew.writeInt(718060049);
                mplew.writeInt(158712075);
                mplew.writeInt(-800067398);
                mplew.writeInt(-1533383854);
                mplew.writeInt(-1687983658);
                mplew.writeInt(-140928716);
                mplew.writeInt(-976798226);
                mplew.writeInt(-1005513861);
                mplew.writeInt(-1082693885);
                mplew.writeInt(-34365721);
                mplew.writeInt(-824723679);
                mplew.writeInt(-1742731749);
                mplew.writeInt(1208375408);
                mplew.writeInt(-2040660005);
                mplew.writeInt(1410336801);
                mplew.writeInt(945983236);
                mplew.writeInt(-49050078);
                mplew.writeInt(-1212143035);
                mplew.writeInt(30277805);
                mplew.writeInt(-1229368304);
                mplew.writeInt(-2025683340);
                mplew.writeInt(516811548);
                mplew.writeInt(1194385534);
                mplew.writeInt(-340681580);
                mplew.writeInt(-2000702864);
                mplew.writeInt(63561910);
                mplew.writeInt(-1473603569);
                mplew.writeInt(-1023479806);
                mplew.writeInt(-262706587);
                mplew.writeInt(817328107);
                mplew.writeInt(1198574923);
                mplew.writeInt(-723494620);
                mplew.writeInt(-386297879);
                mplew.writeInt(1948858307);
                mplew.writeInt(1234000008);
                mplew.writeInt(1077079366);
                mplew.writeInt(1279700169);
                mplew.writeInt(-1534057047);
                mplew.writeInt(730185188);
                mplew.writeInt(510228348);
                mplew.writeInt(1127580199);
                mplew.writeInt(1496913170);
                mplew.writeInt(2138288542);
                mplew.writeInt(-1887923776);
                mplew.writeInt(1381271260);
                mplew.writeInt(-1641713102);
                mplew.writeInt(240520916);
                mplew.writeInt(1059271932);
                mplew.writeInt(474743363);
                mplew.writeInt(74498626);
                mplew.writeInt(-997175499);
                mplew.writeInt(2026886645);
                mplew.writeInt(21544885);
                mplew.writeInt(-1323880988);
                mplew.writeInt(234891581);
                mplew.writeInt(-1338933080);
                mplew.writeInt(609867260);
                mplew.writeInt(475144173);
                mplew.writeInt(-1087687534);
                mplew.writeInt(-43753514);
                mplew.writeInt(119843783);
                mplew.writeInt(974440129);
                mplew.writeInt(449328492);
                mplew.writeInt(205956261);
                mplew.writeInt(962464460);
                mplew.writeInt(-758464331);
                mplew.writeInt(1852716860);
                mplew.writeInt(-489821231);
                mplew.writeInt(-539521900);
                mplew.writeInt(-347093191);
                mplew.writeInt(-233211829);
                mplew.writeInt(1345309660);
                mplew.writeInt(52760282);
                mplew.writeInt(558086474);
                mplew.writeInt(-1607407241);
                mplew.writeInt(-890669205);
                mplew.writeInt(-1011120858);
                mplew.writeInt(-1726485553);
                mplew.writeInt(-395945893);
                mplew.writeInt(-1071190598);
                mplew.writeInt(844385345);
                mplew.writeInt(-475664571);
                mplew.writeInt(-2113970708);
                mplew.writeInt(-2075136729);
                mplew.writeInt(620208701);
                mplew.writeInt(1298907607);
                mplew.writeInt(-1316478512);
                mplew.writeInt(1331732463);
                mplew.writeInt(1518420766);
                mplew.writeInt(1032395430);
                mplew.writeInt(-1680126835);
                mplew.writeInt(1654276479);
                mplew.writeInt(990376368);
                mplew.writeInt(169916087);
                mplew.writeInt(1135085857);
                mplew.writeInt(1133807412);
                mplew.writeInt(596822218);
                mplew.writeInt(407748584);
                mplew.writeInt(973546756);
                mplew.writeInt(434506865);
                mplew.writeInt(616107209);
                mplew.writeInt(-1458595066);
                mplew.writeInt(-185388926);
                mplew.writeInt(503600211);
                mplew.writeInt(-1893408916);
                mplew.writeInt(-328766941);
                mplew.writeInt(-2080354439);
                mplew.writeInt(809818554);
                mplew.writeInt(-1136074702);
                mplew.writeInt(732639183);
                mplew.writeInt(-606666948);
                mplew.writeInt(-733239924);
                mplew.writeInt(-293986034);
                mplew.writeInt(-667849619);
                mplew.writeInt(1327875800);
                mplew.writeInt(554780496);
                mplew.writeInt(-1822782129);
                mplew.writeInt(832767128);
                mplew.writeInt(1690003788);
                mplew.writeInt(-352926615);
                mplew.writeInt(173057637);
                mplew.writeInt(-865475514);
                mplew.writeInt(42236532);
                mplew.writeInt(-962448103);
                mplew.writeInt(1092386689);
                mplew.writeInt(-1984803237);
                mplew.writeInt(234658309);
                mplew.writeInt(1305734686);
                mplew.writeInt(-954042181);
                mplew.writeInt(-239540506);
                mplew.writeInt(-1425786899);
                mplew.writeInt(1478931913);
                mplew.writeInt(1990215877);
                mplew.writeInt(-265372903);
                mplew.writeInt(12729376);
                mplew.writeInt(-1087769897);
                mplew.writeInt(1366195631);
                mplew.writeInt(-46604686);
                mplew.writeInt(2136943369);
                mplew.writeInt(1804678272);
                mplew.writeInt(-1849346706);
                mplew.writeInt(785962736);
                mplew.writeInt(853888586);
                mplew.writeInt(1700581094);
                mplew.writeInt(-818097415);
                mplew.writeInt(1218143990);
                mplew.writeInt(1622892252);
                mplew.writeInt(-17706989);
                mplew.writeInt(-1657895001);
                mplew.writeInt(613642160);
                mplew.writeInt(-2144489801);
                mplew.writeInt(-442605717);
                mplew.writeInt(-1808555067);
                mplew.writeInt(-1780953158);
                mplew.writeInt(1384415108);
                mplew.writeInt(-572325954);
                mplew.writeInt(629143062);
                mplew.writeInt(443839292);
                mplew.writeInt(78562896);
                mplew.writeInt(-317100064);
                mplew.writeInt(1424842798);
                mplew.writeInt(28550);
                mplew.writeInt(1162412032);
                mplew.writeInt(1118717006);
                mplew.writeInt(98912);
                mplew.write(0);
                c.announce(mplew.getPacket());
                break;
            }
            default: {
                log.warn(String.format("未處理的工會Request類型 %d", typeId));
                break;
            }
            case 47: {
                byte type = slea.readByte();
                slea.readByte();
                String searchInfo = slea.readMapleAsciiString();
                boolean equals = slea.readBool();
                boolean bUnk1 = slea.readBool();
                boolean bUnk2 = slea.readBool();
                boolean bUnk3 = slea.readBool();
                List<Pair<Integer, MapleGuild>> gui = WorldGuildService.getInstance().getGuildList();
                ArrayList<MapleGuild> guilds = new ArrayList<MapleGuild>();
                ArrayList guilds_list = new ArrayList();
                for (Pair g : gui) {
                    MapleGuildCharacter leaderObj = ((MapleGuild)g.getRight()).getLeader();
                    String gname = ((MapleGuild)g.getRight()).getName().toLowerCase();
                    if (type != 1 && type != 3 || !(!equals ? gname.contains(searchInfo.toLowerCase()) : gname.equals(searchInfo))) {
                        if (leaderObj == null || type != 2 && type != 3 || !(equals ? leaderObj.equals(searchInfo) : leaderObj.getName().toLowerCase().contains(searchInfo.toLowerCase()))) continue;
                    }
                    guilds.add((MapleGuild)g.getRight());
                }
                c.announce(GuildPacket.guildSearch_Results((int)type, (String)searchInfo, (boolean)equals, (boolean)bUnk1, (boolean)bUnk2, (boolean)bUnk3, guilds, guilds_list));
                break;
            }
        }
    }

    private static boolean isGuildNameAcceptable(String name) {
        return name.getBytes().length >= 1 && name.getBytes().length <= 24;
    }

    private static void respawnPlayer(MapleCharacter chr) {
        if (chr.getMap() == null) {
            return;
        }
        chr.getMap().broadcastMessage(GuildPacket.loadGuildName((MapleCharacter)chr));
        chr.getMap().broadcastMessage(GuildPacket.loadGuildIcon((MapleCharacter)chr));
    }

    public static void ChangeGuildNotice(MapleClient c, String[] Notice) {
        OutPacket say = new OutPacket(OutHeader.LP_GuildResult);
    }
}

