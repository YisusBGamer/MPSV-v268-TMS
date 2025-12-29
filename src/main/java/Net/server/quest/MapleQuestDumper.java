/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.quest.MapleQuestActionType
 *  Net.server.quest.MapleQuestRequirementType
 */
package Net.server.quest;

import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.server.InitializeServer;
import Net.server.quest.MapleQuest;
import Net.server.quest.MapleQuestActionType;
import Net.server.quest.MapleQuestRequirementType;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import tools.Pair;

public class MapleQuestDumper
implements Serializable {
    private static final MapleQuestDumper instance = new MapleQuestDumper();
    private static final Map<Integer, MapleQuest> questInfo = new HashMap<Integer, MapleQuest>();
    private int id;

    public void loadQuest() {
        InitializeServer.WzSqlName[] wzs = new InitializeServer.WzSqlName[]{InitializeServer.WzSqlName.wz_questdata, InitializeServer.WzSqlName.wz_questactitemdata, InitializeServer.WzSqlName.wz_questactskilldata, InitializeServer.WzSqlName.wz_questactquestdata, InitializeServer.WzSqlName.wz_questreqdata, InitializeServer.WzSqlName.wz_questpartydata, InitializeServer.WzSqlName.wz_questactdata};
        DatabaseLoader.DatabaseConnection.domain(con -> {
            if (!InitializeServer.WzSqlName.wz_questdata.check(con)) {
                for (InitializeServer.WzSqlName wz : wzs) {
                    wz.drop(con);
                }
                SqlTool.update(con, "CREATE TABLE `wz_questactdata` (`id` int(11) NOT NULL AUTO_INCREMENT,`questid` int(11) NOT NULL DEFAULT '0',`name` varchar(127) NOT NULL DEFAULT '',`type` tinyint(1) NOT NULL DEFAULT '0',`intStore` int(11) NOT NULL DEFAULT '0',`applicableJobs` varchar(2048) NOT NULL DEFAULT '',`uniqueid` int(11) NOT NULL DEFAULT '0',PRIMARY KEY (`id`),KEY `quests_ibfk_2` (`questid`)) DEFAULT CHARSET=utf8");
                SqlTool.update(con, "CREATE TABLE `wz_questactitemdata` (`id` int(11) NOT NULL AUTO_INCREMENT,`itemid` int(11) NOT NULL DEFAULT '0',`count` smallint(5) NOT NULL DEFAULT '0',`period` int(11) NOT NULL DEFAULT '0',`gender` tinyint(1) NOT NULL DEFAULT '2',`job` int(11) NOT NULL DEFAULT '-1',`jobEx` int(11) NOT NULL DEFAULT '-1',`prop` int(11) NOT NULL DEFAULT '-1',`uniqueid` int(11) NOT NULL DEFAULT '0',PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8");
                SqlTool.update(con, "CREATE TABLE `wz_questactquestdata` (`id` int(11) NOT NULL AUTO_INCREMENT,`quest` int(11) NOT NULL DEFAULT '0',`state` tinyint(1) NOT NULL DEFAULT '2',`uniqueid` int(11) NOT NULL DEFAULT '0',PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8");
                SqlTool.update(con, "CREATE TABLE `wz_questactskilldata` (`id` int(11) NOT NULL AUTO_INCREMENT,`skillid` int(11) NOT NULL DEFAULT '0',`skillLevel` int(11) NOT NULL DEFAULT '-1',`masterLevel` int(11) NOT NULL DEFAULT '-1',`uniqueid` int(11) NOT NULL DEFAULT '0',PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8");
                SqlTool.update(con, "CREATE TABLE `wz_questdata` (`questid` int(11) NOT NULL,`name` varchar(1024) NOT NULL DEFAULT '',`autoStart` tinyint(1) NOT NULL DEFAULT '0',`autoPreComplete` tinyint(1) NOT NULL DEFAULT '0',`viewMedalItem` int(11) NOT NULL DEFAULT '0',`selectedSkillID` int(11) NOT NULL DEFAULT '0',`blocked` tinyint(1) NOT NULL DEFAULT '0',`autoAccept` tinyint(1) NOT NULL DEFAULT '0',`autoComplete` tinyint(1) NOT NULL DEFAULT '0',`selfStart` tinyint(1) NOT NULL DEFAULT '0',PRIMARY KEY (`questid`)) DEFAULT CHARSET=utf8");
                SqlTool.update(con, "CREATE TABLE `wz_questpartydata` (`id` int(11) NOT NULL AUTO_INCREMENT,`questid` int(11) NOT NULL DEFAULT '0',`rank` varchar(1) NOT NULL DEFAULT '',`mode` varchar(13) NOT NULL DEFAULT '',`property` varchar(255) NOT NULL DEFAULT '',`value` int(11) NOT NULL DEFAULT '0',PRIMARY KEY (`id`),KEY `quests_ibfk_7` (`questid`)) DEFAULT CHARSET=utf8");
                SqlTool.update(con, "CREATE TABLE `wz_questreqdata` (`id` int(11) NOT NULL AUTO_INCREMENT,`questid` int(11) NOT NULL DEFAULT '0',`name` varchar(127) NOT NULL DEFAULT '',`type` tinyint(1) NOT NULL DEFAULT '0',`stringStore` varchar(1024) NOT NULL DEFAULT '',`intStoresFirst` varchar(1024) NOT NULL DEFAULT '',`intStoresSecond` varchar(4096) NOT NULL DEFAULT '',PRIMARY KEY (`id`),KEY `quests_ibfk_1` (`questid`)) DEFAULT CHARSET=utf8");
                this.dumpQuests(con);
                for (InitializeServer.WzSqlName wz : wzs) {
                    wz.update(con);
                }
            }
            MapleQuest.initQuests(con);
            return null;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dumpQuests(Connection con) throws SQLException {
        PreparedStatement psai = con.prepareStatement("INSERT INTO wz_questactitemdata(uniqueid, itemid, count, period, gender, job, jobEx, prop) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement psas = con.prepareStatement("INSERT INTO wz_questactskilldata(uniqueid, skillid, skillLevel, masterLevel) VALUES (?, ?, ?, ?)");
        PreparedStatement psaq = con.prepareStatement("INSERT INTO wz_questactquestdata(uniqueid, quest, state) VALUES (?, ?, ?)");
        PreparedStatement ps = con.prepareStatement("INSERT INTO wz_questdata(questid, name, autoStart, autoPreComplete, viewMedalItem, selectedSkillID, blocked, autoAccept, autoComplete, selfStart) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement psr = con.prepareStatement("INSERT INTO wz_questreqdata(questid, type, name, stringStore, intStoresFirst, intStoresSecond) VALUES (?, ?, ?, ?, ?, ?)");
        PreparedStatement psq = con.prepareStatement("INSERT INTO wz_questpartydata(questid, `rank`, mode, property, `value`) VALUES(?,?,?,?,?)");
        PreparedStatement psa = con.prepareStatement("INSERT INTO wz_questactdata(questid, type, name, intStore, applicableJobs, uniqueid) VALUES (?, ?, ?, ?, ?, ?)");
        try {
            this.dumpQuests(psai, psas, psaq, ps, psr, psq, psa);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            psai.executeBatch();
            psai.close();
            psas.executeBatch();
            psas.close();
            psaq.executeBatch();
            psaq.close();
            psa.executeBatch();
            psa.close();
            psr.executeBatch();
            psr.close();
            psq.executeBatch();
            psq.close();
            ps.executeBatch();
            ps.close();
        }
    }

    /*
     * Could not resolve type clashes
     */
    public void dumpQuests(PreparedStatement psai, PreparedStatement psas, PreparedStatement psaq, PreparedStatement ps, PreparedStatement psr, PreparedStatement psq, PreparedStatement psa) throws Exception {
        MapleDataProvider questData = MapleDataProviderFactory.getQuest();
        MapleData checkz = questData.getData("Check.img");
        if (checkz == null) {
            System.err.println("Check.img not found in quest data. Please ensure the file is present and correctly named.");
            return;
        }
        for (MapleData qz : checkz.getChildren()) {
            this.id = Integer.parseInt(qz.getName());
            ps.setInt(1, this.id);
        }
        MapleData actz = questData.getData("Act.img");
        MapleData infoz = questData.getData("QuestInfo.img");
        MapleData pinfoz = questData.getData("PQuest.img");
        int uniqueid = 0;
        for (MapleData qz : checkz.getChildren()) {
            this.id = Integer.parseInt(qz.getName());
            ps.setInt(1, this.id);
            for (int i = 0; i < 2; ++i) {
                MapleData actData;
                MapleData reqData = qz.getChildByPath(String.valueOf(i));
                if (reqData != null) {
                    psr.setInt(1, this.id);
                    psr.setInt(2, i);
                    for (MapleData req2 : reqData.getChildren()) {
                        if (MapleQuestRequirementType.getByWZName((String)req2.getName()) == MapleQuestRequirementType.UNDEFINED) continue;
                        psr.setString(3, req2.getName());
                        if (req2.getName().equals("fieldEnter")) {
                            psr.setString(4, String.valueOf(MapleDataTool.getIntConvert("0", (MapleData)req2, 0)));
                        } else if (req2.getName().equals("end") || req2.getName().equals("startscript") || req2.getName().equals("endscript")) {
                            psr.setString(4, MapleDataTool.getString((MapleData)req2, ""));
                        } else {
                            psr.setString(4, String.valueOf(MapleDataTool.getInt((MapleData)req2, 0)));
                        }
                        StringBuilder intStore1 = new StringBuilder();
                        StringBuilder intStore2 = new StringBuilder();
                        List<Pair<Object, Object>> dataStore = new LinkedList<>();
                        if (req2.getName().equals("job") || req2.getName().equals("job_CN") || req2.getName().equals("job_TW")) {
                            List<MapleData> child = req2.getChildren();
                            for (int x = 0; x < child.size(); ++x) {
                                dataStore.add(new Pair<>(i, MapleDataTool.getInt(child.get(x), -1)));
                            }
                        } else if (req2.getName().equals("skill")) {
                            List<MapleData> child = req2.getChildren();
                            for (int x = 0; x < child.size(); ++x) {
                                MapleData childdata = child.get(x);
                                if (childdata == null) continue;
                                dataStore.add(new Pair<>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0),
                                        MapleDataTool.getInt(childdata.getChildByPath("acquire"), 0)));                            }
                        } else if (req2.getName().equals("quest")) {
                            List<MapleData> child = req2.getChildren();
                            for (int x = 0; x < child.size(); ++x) {
                                MapleData childdata = child.get(x);
                                if (childdata == null) continue;
                                dataStore.add(new Pair<>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0),
                                        MapleDataTool.getInt(childdata.getChildByPath("state"), 0)));                            }
                        } else if (req2.getName().equals("infoex")) {
                            List<MapleData> child = req2.getChildren();
                            for (int x = 0; x < child.size(); ++x) {
                                MapleData childdata = child.get(x);
                                if (childdata == null) continue;
                                dataStore.add(new Pair<>(MapleDataTool.getString(childdata.getChildByPath("exVariable"), ""),
                                        MapleDataTool.getString(childdata.getChildByPath("value"), "")));                            }
                        } else if (req2.getName().equals("item") || req2.getName().equals("mob")) {
                            List<MapleData> child = req2.getChildren();
                            for (int x = 0; x < child.size(); ++x) {
                                MapleData childdata = child.get(x);
                                if (childdata == null) continue;
                                dataStore.add(new Pair<>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0),
                                        MapleDataTool.getInt(childdata.getChildByPath("count"), 0)));                            }
                        } else if (req2.getName().equals("mbcard")) {
                            List<MapleData> child = req2.getChildren();
                            for (int x = 0; x < child.size(); ++x) {
                                MapleData childdata = child.get(x);
                                if (childdata == null) continue;
                                dataStore.add(new Pair<>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0),
                                        MapleDataTool.getInt(childdata.getChildByPath("min"), 0)));                            }
                        } else if (req2.getName().equals("pet")) {
                            List<MapleData> child = req2.getChildren();
                            for (int x = 0; x < child.size(); ++x) {
                                MapleData childdata = child.get(x);
                                if (childdata == null) continue;
                                dataStore.add(new Pair<>(i,
                                        MapleDataTool.getInt(childdata.getChildByPath("id"), 0)));                            }
                        }
                        for (Pair data : dataStore) {
                            if (intStore1.length() > 0) {
                                intStore1.append(", ");
                                intStore2.append(", ");
                            }
                            intStore1.append(data.getLeft() instanceof String ? data.getLeft() : String.valueOf(data.getLeft()));
                            intStore2.append(data.getRight() instanceof String ? data.getRight() : String.valueOf(data.getRight()));
                        }
                        psr.setString(5, intStore1.toString());
                        psr.setString(6, intStore2.toString());
                        psr.addBatch();
                    }
                }
                if ((actData = actz.getChildByPath(this.id + "/" + i)) == null) continue;
                psa.setInt(1, this.id);
                psa.setInt(2, i);
                Iterator<MapleData> req2 = actData.getChildren().iterator();
                while (req2.hasNext()) {
                    MapleData act = (MapleData)req2.next();
                    if (MapleQuestActionType.getByWZName((String)act.getName()) == MapleQuestActionType.UNDEFINED) continue;
                    psa.setString(3, act.getName());
                    if (act.getName().equals("sp")) {
                        psa.setInt(4, MapleDataTool.getIntConvert("0/sp_value", act, 0));
                    } else {
                        psa.setInt(4, MapleDataTool.getInt(act, 0));
                    }
                    StringBuilder applicableJobs = new StringBuilder();
                    if (act.getName().equals("sp") || act.getName().equals("skill")) {
                        int index = 0;
                        while (act.getChildByPath(index + "/job") != null) {
                            for (MapleData d : act.getChildByPath(index + "/job")) {
                                if (applicableJobs.length() > 0) {
                                    applicableJobs.append(", ");
                                }
                                applicableJobs.append(MapleDataTool.getInt(d, 0));
                            }
                            ++index;
                        }
                    } else if (act.getChildByPath("job") != null) {
                        for (MapleData d : act.getChildByPath("job")) {
                            if (applicableJobs.length() > 0) {
                                applicableJobs.append(", ");
                            }
                            applicableJobs.append(MapleDataTool.getInt(d, 0));
                        }
                    }
                    psa.setString(5, applicableJobs.toString());
                    psa.setInt(6, -1);
                    if (act.getName().equals("item")) {
                        psa.setInt(6, ++uniqueid);
                        psai.setInt(1, uniqueid);
                        for (MapleData iEntry : act.getChildren()) {
                            psai.setInt(2, MapleDataTool.getInt("id", iEntry, 0));
                            psai.setInt(3, MapleDataTool.getInt("count", iEntry, 0));
                            psai.setInt(4, MapleDataTool.getInt("period", iEntry, 0));
                            psai.setInt(5, MapleDataTool.getInt("gender", iEntry, 2));
                            psai.setInt(6, MapleDataTool.getInt("job", iEntry, -1));
                            psai.setInt(7, MapleDataTool.getInt("jobEx", iEntry, -1));
                            if (iEntry.getChildByPath("prop") == null) {
                                psai.setInt(8, -2);
                            } else {
                                psai.setInt(8, MapleDataTool.getInt("prop", iEntry, -1));
                            }
                            psai.addBatch();
                        }
                    } else if (act.getName().equals("skill")) {
                        psa.setInt(6, ++uniqueid);
                        psas.setInt(1, uniqueid);
                        for (MapleData sEntry : act) {
                            psas.setInt(2, MapleDataTool.getInt("id", sEntry, 0));
                            psas.setInt(3, MapleDataTool.getInt("skillLevel", sEntry, 0));
                            psas.setInt(4, MapleDataTool.getInt("masterLevel", sEntry, 0));
                            psas.addBatch();
                        }
                    } else if (act.getName().equals("quest")) {
                        psa.setInt(6, ++uniqueid);
                        psaq.setInt(1, uniqueid);
                        for (MapleData sEntry : act) {
                            psaq.setInt(2, MapleDataTool.getInt("id", sEntry, 0));
                            psaq.setInt(3, MapleDataTool.getInt("state", sEntry, 0));
                            psaq.addBatch();
                        }
                    }
                    psa.addBatch();
                }
            }
            MapleData infoData = infoz.getChildByPath(String.valueOf(this.id));
            if (infoData != null) {
                ps.setString(2, MapleDataTool.getString("name", infoData, ""));
                ps.setInt(3, MapleDataTool.getInt("autoStart", infoData, 0) > 0 ? 1 : 0);
                ps.setInt(4, MapleDataTool.getInt("autoPreComplete", infoData, 0) > 0 ? 1 : 0);
                ps.setInt(5, MapleDataTool.getInt("viewMedalItem", infoData, 0));
                ps.setInt(6, MapleDataTool.getInt("selectedSkillID", infoData, 0));
                ps.setInt(7, MapleDataTool.getInt("blocked", infoData, 0));
                ps.setInt(8, MapleDataTool.getInt("autoAccept", infoData, 0));
                ps.setInt(9, MapleDataTool.getInt("autoComplete", infoData, 0));
                ps.setInt(10, MapleDataTool.getInt("selfStart", infoData, 0) > 0 ? 1 : 0);
            } else {
                ps.setString(2, "");
                ps.setInt(3, 0);
                ps.setInt(4, 0);
                ps.setInt(5, 0);
                ps.setInt(6, 0);
                ps.setInt(7, 0);
                ps.setInt(8, 0);
                ps.setInt(9, 0);
            }
            ps.addBatch();
            MapleData pinfoData = pinfoz.getChildByPath(String.valueOf(this.id));
            if (pinfoData == null || pinfoData.getChildByPath("rank") == null) continue;
            psq.setInt(1, this.id);
            for (MapleData d : pinfoData.getChildByPath("rank")) {
                psq.setString(2, d.getName());
                for (MapleData c : d) {
                    psq.setString(3, c.getName());
                    for (MapleData b : c) {
                        psq.setString(4, b.getName());
                        psq.setInt(5, MapleDataTool.getInt(b, 0));
                        psq.addBatch();
                    }
                }
            }
        }
    }

    public static MapleQuestDumper getInstance() {
        return instance;
    }
}

