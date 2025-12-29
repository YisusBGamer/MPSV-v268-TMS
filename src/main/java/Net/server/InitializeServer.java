/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import Client.force.MapleForceFactory;
import Client.hexa.HexaFactory;
import Database.DatabaseException;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Net.server.MapleItemInformationProvider;
import Net.server.MapleUnionData;
import Net.server.cashshop.CashItemFactory;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonsterInformationProvider;
import Net.server.life.MobSkillFactory;
import Net.server.maps.MapleMapFactory;
import Net.server.maps.field.ActionBarField;
import Net.server.maps.field.BossLucidField;
import Net.server.maps.field.BossWillField;
import Net.server.quest.MapleQuestDumper;
import Net.server.shop.MapleShopFactory;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.loaders.SkillData;
import Plugin.provider.loaders.StringData;
import Plugin.script.ReactorManager;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializeServer {
    private static final Logger logger = LoggerFactory.getLogger(InitializeServer.class);
    private static final ExecutorService executor = Executors.newWorkStealingPool(2);
    private static final String LOG_TABLE_NAME = "systemupdatelog";
    public static boolean InitDataFinished = false;

    public static boolean initServer() {
        CompletableFuture<Boolean> initSettingFuture = CompletableFuture.supplyAsync(InitializeServer::initializeSetting, executor);
        CompletableFuture<Boolean> initUpdateLogFuture = CompletableFuture.supplyAsync(InitializeServer::initializeUpdateLog, executor);
        CompletableFuture<Boolean> initMySQLFuture = CompletableFuture.supplyAsync(InitializeServer::initializeMySQL, executor);
        CompletableFuture.allOf(initSettingFuture, initUpdateLogFuture, initMySQLFuture).join();
        executor.shutdown();
        return true;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static boolean executeSql(String sql, Object ... params) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            boolean bl;
            block15: {
                PreparedStatement ps = con.prepareStatement(sql);
                try {
                    for (int i = 0; i < params.length; ++i) {
                        ps.setObject(i + 1, params[i]);
                    }
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
        catch (SQLException ex) {
            logger.error("[EXCEPTION] SQL执行错误，请检查SQL服务器是否正常。", ex);
            return false;
        }
    }

    private static boolean initializeSetting() {
        return InitializeServer.executeSql("UPDATE `accounts` SET `loggedin` = 0, `check` = 0", new Object[0]);
    }

    private static boolean initializeUpdateLog() {
        if (!InitializeServer.checkTableExists(LOG_TABLE_NAME)) {
            InitializeServer.createTable(LOG_TABLE_NAME, "id INT(11) NOT NULL AUTO_INCREMENT, patchname VARCHAR(50) NOT NULL, lasttime TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (id)");
        }
        return InitializeServer.checkTableExists(LOG_TABLE_NAME);
    }

    /*
     * Exception decompiling
     */
    private static boolean checkTableExists(String tableName) {
        try (Connection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SHOW TABLES LIKE ?")) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking table existence: " + tableName, e);
            return false;
        }
    }

    private static void createTable(String tableName, String tableDefinition) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableDefinition + ")");){
            ps.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("创建表[" + tableName + "]时出错。", e);
        }
    }

    private static boolean checkTableExists_wz() {
        return DatabaseLoader.DatabaseConnection.domain(con -> {
            boolean exist = false;
            try (PreparedStatement ps = con.prepareStatement("SHOW TABLES LIKE 'wztosqllog'");
                 ResultSet rs = ps.executeQuery();){
                exist = rs.next();
            }
            catch (SQLException e) {
                logger.error("检查wztosqllog表时出错。", e);
            }
            if (!exist) {
                SqlTool.update(con, "DROP TABLE IF EXISTS `wztosqllog`");
                StringBuilder s = new StringBuilder("CREATE TABLE `wztosqllog` (").append("`version` SMALLINT NOT NULL, ").append("`hotfix_check` VARCHAR(40) NULL DEFAULT NULL, ");
                for (String name : WzSqlName.names()) {
                    s.append("`").append(name).append("` BOOLEAN NOT NULL, ");
                }
                s.append("PRIMARY KEY (`version`))");
                SqlTool.update(con, s.toString());
            }
            return exist;
        });
    }

    private static boolean initializeMySQL() {
        boolean allSuccess = true;
        for (UPDATE_PATCH patch : UPDATE_PATCH.values()) {
            if (InitializeServer.checkIsAppliedSQLPatch(patch.name()) || InitializeServer.applySQLPatch(patch.getSQL()) && InitializeServer.insertUpdateLog(patch.name())) continue;
            allSuccess = false;
        }
        return allSuccess;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static boolean checkIsAppliedSQLPatch(String name) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id FROM systemupdatelog WHERE patchname = ?");){
            ResultSet rs;
            block25: {
                ps.setString(1, name);
                try {
                    boolean bl;
                    rs = ps.executeQuery();
                    try {
                        if (!rs.next()) break block25;
                        bl = true;
                        if (rs == null) return bl;
                    }
                    catch (Throwable throwable) {
                        if (rs == null) throw throwable;
                        try {
                            rs.close();
                            throw throwable;
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                    rs.close();
                    return bl;
                }
                catch (Exception ex) {
                    SqlTool.update(con, "DROP TABLE IF EXISTS `systemupdatelog`");
                    InitializeServer.initializeUpdateLog();
                    boolean bl = InitializeServer.checkIsAppliedSQLPatch(name);
                    if (ps != null) {
                        ps.close();
                    }
                    if (con == null) return bl;
                    con.close();
                    return bl;
                }
            }
            if (rs == null) return false;
            rs.close();
            return false;
        }
        catch (SQLException e) {
            logger.error("检查SQL补丁[" + name + "]是否已应用时出错。", e);
        }
        return false;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static boolean insertUpdateLog(String patchname) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            boolean bl;
            block14: {
                PreparedStatement ps = con.prepareStatement("INSERT INTO systemupdatelog(id, patchname, lasttime) VALUES (DEFAULT, ?, CURRENT_TIMESTAMP)");
                try {
                    ps.setString(1, patchname);
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
        catch (SQLException ex) {
            logger.error("插入更新日志[" + patchname + "]时出错。", ex);
            return false;
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static boolean applySQLPatch(String sql) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            boolean bl;
            block14: {
                PreparedStatement ps = con.prepareStatement(sql);
                try {
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
            logger.error("应用SQL补丁时出错： " + sql, e);
            return false;
        }
    }

    public static void initAllData(DataCacheListener listener) {
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(0);
        ExecutorService executorService = Executors.newWorkStealingPool();
        ArrayList<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
        if (!"true".equalsIgnoreCase(System.getProperty("low.performance"))) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    MapleItemInformationProvider.getInstance().runItems();
                }
                catch (Exception e) {
                    logger.error("[TASK] runItems() 出错：", e);
                }
                finally {
                    listener.next("Item", count.incrementAndGet(), total);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    MapleItemInformationProvider.getInstance().loadPotentialData();
                }
                catch (Exception e) {
                    logger.error("[TASK] loadPotentialData() 出错：", e);
                }
                finally {
                    listener.next("Potential", count.incrementAndGet(), total);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    MapleMapFactory.loadAllLinkNpc();
                }
                catch (Exception e) {
                    logger.error("[TASK] MapleMapFactory.loadAllLinkNpc() 出错：", e);
                }
                finally {
                    listener.next("LinkNpc", count.incrementAndGet(), total);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    MapleMapFactory.loadAllMapName();
                }
                catch (Exception e) {
                    logger.error("[TASK] MapleMapFactory.loadAllMapName() 出错：", e);
                }
                finally {
                    listener.next("MapName", count.incrementAndGet(), total);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    CashItemFactory.getInstance().initialize();
                }
                catch (Exception e) {
                    logger.error("[TASK] CashItemFactory.initialize() 出错：", e);
                }
                finally {
                    listener.next("CashItem", count.incrementAndGet(), total);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    MapleMonsterInformationProvider.getInstance().load();
                }
                catch (Exception e) {
                    logger.error("[TASK] MapleMonsterInformationProvider.load() 出错：", e);
                }
                finally {
                    listener.next("Monster", count.incrementAndGet(), total);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    MapleLifeFactory.loadQuestCounts();
                }
                catch (Exception e) {
                    logger.error("[TASK] MapleLifeFactory.loadQuestCounts() 出错：", e);
                }
                finally {
                    listener.next("QuestCounts", count.incrementAndGet(), total);
                }
            }, executorService));
        }
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                StringData.load();
            }
            catch (Exception e) {
                logger.error("[TASK] StringData.load() 出错：", e);
            }
            finally {
                listener.next("String", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleItemInformationProvider.getInstance().loadSetItemData();
            }
            catch (Exception e) {
                logger.error("[TASK] loadSetItemData() 出错：", e);
            }
            finally {
                listener.next("SetItem", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleItemInformationProvider.getInstance().loadFamiliarItems();
            }
            catch (Exception e) {
                logger.error("[TASK] loadFamiliarItems() 出错：", e);
            }
            finally {
                listener.next("FamiliarItem", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleItemInformationProvider.getInstance().runEtc();
            }
            catch (Exception e) {
                logger.error("[TASK] runEtc() 出错：", e);
            }
            finally {
                listener.next("Etc", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleForceFactory.getInstance().initialize();
            }
            catch (Exception e) {
                logger.error("[TASK] MapleForceFactory.initialize() 出错：", e);
            }
            finally {
                listener.next("ForceSkill", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleShopFactory.getInstance().loadShopData();
            }
            catch (Exception e) {
                logger.error("[TASK] MapleShopFactory.loadShopData() 出错：", e);
            }
            finally {
                listener.next("Shop", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MobSkillFactory.initialize();
            }
            catch (Exception e) {
                logger.error("[TASK] MobSkillFactory.initialize() 出错：", e);
            }
            finally {
                listener.next("MobSkill", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleUnionData.getInstance().init();
            }
            catch (Exception e) {
                logger.error("[TASK] MapleUnionData.init() 出错：", e);
            }
            finally {
                listener.next("MapleUnion", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleLifeFactory.initEliteMonster();
            }
            catch (Exception e) {
                logger.error("[TASK] MapleLifeFactory.initEliteMonster() 出错：", e);
            }
            finally {
                listener.next("EliteMonster", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                ReactorManager.getInstance().loadDrops();
            }
            catch (Exception e) {
                logger.error("[TASK] ReactorManager.loadDrops() 出错：", e);
            }
            finally {
                listener.next("Drops", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                MapleQuestDumper.getInstance().loadQuest();
            }
            catch (Exception e) {
                logger.error("[TASK] MapleQuestDumper.loadQuest() 出错：", e);
            }
            finally {
                listener.next("Quest", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                BossWillField.init();
            }
            catch (Exception e) {
                logger.error("[TASK] BossWillField.init() 出错：", e);
            }
            finally {
                listener.next("WillField", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                BossLucidField.init();
            }
            catch (Exception e) {
                logger.error("[TASK] BossLucidField.init() 出错：", e);
            }
            finally {
                listener.next("LucidField", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                ActionBarField.init();
            }
            catch (Exception e) {
                logger.error("[TASK] ActionBarField.init() 出错：", e);
            }
            finally {
                listener.next("ActionBarField", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                HexaFactory.loadAllHexaSkill();
            }
            catch (Exception e) {
                logger.error("[TASK] HexaFactory.loadAllHexaSkill() 出错：", e);
            }
            finally {
                listener.next("HexaSkill", count.incrementAndGet(), total);
            }
        }, executorService));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                SkillData.loadAllSkills();
            }
            catch (Exception e) {
                logger.error("[TASK] SkillData.loadAllSkills() 出错：", e);
            }
            finally {
                listener.next("Skill", count.incrementAndGet(), total);
            }
        }, executorService));
        total.set(futures.size());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((result, error) -> {
            if (error != null) {
                logger.error("Error：", (Throwable)error);
            }
            executorService.shutdown();
        });
    }

    static enum UPDATE_PATCH {
        真實符文屬性("ALTER TABLE `inventoryequipment` ADD COLUMN `aut` smallint(6) NOT NULL DEFAULT 0 AFTER `arclevel`, ADD COLUMN `autexp` int(6) NOT NULL DEFAULT 0 AFTER `aut`, ADD COLUMN `autlevel` smallint(6) NOT NULL DEFAULT 0 AFTER `autexp`"),
        寵物第二BUFF欄位("ALTER TABLE `pets` ADD COLUMN `skillid2` int(11) NOT NULL DEFAULT '0' AFTER `skillid`"),
        公會V240擴充職位欄位("ALTER TABLE `guilds` MODIFY COLUMN `rank1authority` int(10) NOT NULL DEFAULT -1 AFTER `rank5title`, MODIFY COLUMN `rank2authority` int(10) NOT NULL DEFAULT 1663 AFTER `rank1authority`, MODIFY COLUMN `rank3authority` int(10) NOT NULL DEFAULT 1024 AFTER `rank2authority`, MODIFY COLUMN `rank4authority` int(10) NOT NULL DEFAULT 1024 AFTER `rank3authority`, MODIFY COLUMN `rank5authority` int(10) NOT NULL DEFAULT 1024 AFTER `rank4authority`, ADD COLUMN `rank6title` varchar(45) NOT NULL AFTER `rank5title`, ADD COLUMN `rank7title` varchar(45) NOT NULL AFTER `rank6title`, ADD COLUMN `rank8title` varchar(45) NOT NULL AFTER `rank7title`, ADD COLUMN `rank9title` varchar(45) NOT NULL AFTER `rank8title`, ADD COLUMN `rank10title` varchar(45) NOT NULL AFTER `rank9title`, ADD COLUMN `rank6authority` int(10) NOT NULL DEFAULT '1024' AFTER `rank5authority`, ADD COLUMN `rank7authority` int(10) NOT NULL DEFAULT '1024' AFTER `rank6authority`, ADD COLUMN `rank8authority` int(10) NOT NULL DEFAULT '1024' AFTER `rank7authority`, ADD COLUMN `rank9authority` int(10) NOT NULL DEFAULT '1024' AFTER `rank8authority`, ADD COLUMN `rank10authority` int(10) NOT NULL DEFAULT '1024' AFTER `rank9authority`"),
        移除商城道具extra_flags屬性("ALTER TABLE `cashshop_modified_items` DROP COLUMN `extra_flags`"),
        公會職位預設值("ALTER TABLE `guilds` MODIFY COLUMN `rank6title` varchar(45) NOT NULL DEFAULT '公會成員4' AFTER `rank5title`, MODIFY COLUMN `rank7title` varchar(45) NOT NULL DEFAULT '' AFTER `rank6title`, MODIFY COLUMN `rank8title` varchar(45) NOT NULL DEFAULT '' AFTER `rank7title`, MODIFY COLUMN `rank9title` varchar(45) NOT NULL DEFAULT '' AFTER `rank8title`, MODIFY COLUMN `rank10title` varchar(45) NOT NULL DEFAULT '' AFTER `rank9title`"),
        鍵位欄位擴充補丁("ALTER TABLE `keymap` ADD COLUMN `slot` tinyint(3) unsigned NOT NULL DEFAULT 0 AFTER `characterid`"),
        移除pokemon和monsterbook表("DROP TABLE IF EXISTS `pokemon`, `monsterbook`"),
        寵物Buff欄屬性("ALTER TABLE `pets` DROP COLUMN `skillid`, DROP COLUMN `skillid2`"),
        移除extendedslots表("DROP TABLE IF EXISTS `extendedslots`"),
        道具新增extendedSlot屬性("ALTER TABLE `inventoryitems` ADD COLUMN `extendSlot` int(11) NOT NULL DEFAULT -1 AFTER `espos`"),
        增加傳授次數屬性("ALTER TABLE `skills` ADD COLUMN `teachTimes` int(11) NOT NULL DEFAULT 0 AFTER `teachId`"),
        極限屬性欄位("CREATE TABLE IF NOT EXISTS `hyperstats` (`id` int(11) NOT NULL AUTO_INCREMENT, `charid` int(11) NOT NULL, `position` int(11) NOT NULL, `skillid` int(11) NOT NULL, `skilllevel` int(11) NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8"),
        移除character_coreauras表("DROP TABLE IF EXISTS `character_coreauras`"),
        移除角色髮型混色欄位("ALTER TABLE `characters` DROP COLUMN `basecolor`, DROP COLUMN `mixedcolor`, DROP COLUMN `probcolor`"),
        移除梳化間混色欄位("ALTER TABLE `salon` DROP COLUMN `basecolor`, DROP COLUMN `mixedcolor`, DROP COLUMN `probcolor`"),
        萌獸增加鎖定欄位("ALTER TABLE `familiars` ADD COLUMN `lock` tinyint(1) NOT NULL DEFAULT 0 AFTER `summon`"),
        增加HEXA屬性表("CREATE TABLE IF NOT EXISTS `sixstats` (`id` int(11) NOT NULL AUTO_INCREMENT, `charid` int(11) NOT NULL, `solt` int(11) NOT NULL, `preset` int(11) NOT NULL, `p0stat1` int(11) NOT NULL, `p0stat1lv` int(11) NOT NULL, `p0stat2` int(11) NOT NULL, `p0stat2lv` int(11) NOT NULL, `p0stat3` int(11) NOT NULL, `p0stat3lv` int(11) NOT NULL, `p1stat1` int(11) NOT NULL, `p1stat1lv` int(11) NOT NULL, `p1stat2` int(11) NOT NULL, `p1stat2lv` int(11) NOT NULL, `p1stat3` int(11) NOT NULL, `p1stat3lv` int(11) NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci"),
        增加HEXA技能表("CREATE TABLE IF NOT EXISTS `hexaskills` (`sid` int(11) NOT NULL AUTO_INCREMENT, `id` int(11) NOT NULL, `charid` int(11) NOT NULL, `skilllv` int(11) NOT NULL, PRIMARY KEY (`sid`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");

        private final String sql;

        private UPDATE_PATCH(String sql) {
            this.sql = sql;
        }

        public String getSQL() {
            return this.sql;
        }
    }

    @FunctionalInterface
    public static interface DataCacheListener {
        public void next(String var1, int var2, AtomicInteger var3);
    }

    public static enum WzSqlName {
        wz_delays,
        wz_skilldata,
        wz_skillsbyjob,
        wz_summonskill,
        wz_mountids,
        wz_familiarskill,
        wz_craftings,
        wz_finalattacks,
        wz_itemdata,
        wz_maplinknpcs,
        wz_questdata,
        wz_questactitemdata,
        wz_questactskilldata,
        wz_questactquestdata,
        wz_questreqdata,
        wz_questpartydata,
        wz_questactdata,
        wz_questcount,
        wz_npcnames,
        wz_mobskilldata;


        static String[] names() {
            WzSqlName[] values = WzSqlName.values();
            String[] names = new String[values.length];
            for (int i = 0; i < values.length; ++i) {
                names[i] = values[i].name();
            }
            return names;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        public boolean check(Connection con) {
            Class<WzSqlName> clazz = WzSqlName.class;
            // MONITORENTER : Net.server.InitializeServer$WzSqlName.class
            try {
                boolean bl = false;
                PreparedStatement ps = con.prepareStatement("SELECT * FROM `wztosqllog` WHERE `version` = ?");
                try {
                    String hotfixCheck;
                    block24: {
                        hotfixCheck = MapleDataProviderFactory.getHotfixCheck();
                        ps.setInt(1, 268);
                        try {
                            ResultSet rs;
                            block25: {
                                rs = ps.executeQuery();
                                if (!rs.next()) break block24;
                                if (!Objects.equals(rs.getString("hotfix_check"), hotfixCheck)) break block25;
                                bl = rs.getBoolean(this.name());
                                if (rs == null) return bl;
                                rs.close();
                                return bl;
                            }
                            try {
                                SqlTool.update(con, "DELETE FROM `wztosqllog` WHERE `version` = 268");
                            }
                            finally {
                                if (rs != null) {
                                    rs.close();
                                }
                            }
                        }
                        catch (Exception e) {
                            SqlTool.update(con, "DROP TABLE IF EXISTS `wztosqllog`");
                            InitializeServer.checkTableExists_wz();
                        }
                    }
                    StringBuilder s = new StringBuilder("INSERT INTO `wztosqllog` VALUES(").append(268).append(",").append((String)(hotfixCheck == null ? "NULL" : "\"" + hotfixCheck + "\""));
                    for (String name : WzSqlName.names()) {
                        s.append(",false");
                    }
                    try {
                        SqlTool.update(con, s.append(")").toString());
                    }
                    catch (DatabaseException e) {
                        SqlTool.update(con, "DROP TABLE IF EXISTS `wztosqllog`");
                    }
                    boolean bl2 = false;
                    return bl2;
                }
                finally {
                    if (ps == null) {
                        // MONITOREXIT : clazz
                        return bl;
                    }
                    ps.close();
                }
            }
            catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }

        public synchronized void update(Connection con) {
            SqlTool.update(con, "UPDATE `wztosqllog` SET `" + this.name() + "` = ? WHERE `version` = ?", true, (short)268);
        }

        public synchronized void drop(Connection con) {
            SqlTool.update(con, "DROP TABLE IF EXISTS `" + this.name() + "`");
        }
    }
}

