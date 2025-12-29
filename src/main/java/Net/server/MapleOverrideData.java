/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import Database.DatabaseLoader;
import Database.tools.SqlTool;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class MapleOverrideData {
    public static final Map<Integer, Map<String, String>> overridedata = new HashMap<Integer, Map<String, String>>();

    public static void init() {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement("SELECT `skillid`,`key`,`value` FROM `wz_override`");
                 ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    int skillId = rs.getInt("skillid");
                    overridedata.computeIfAbsent(skillId, k -> new HashMap()).put(rs.getString("key"), rs.getString("value"));
                }
            }
            return null;
        });
    }

    public static void save() {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            SqlTool.update(con, "DROP TABLE IF EXISTS `wz_override`");
            SqlTool.update(con, "CREATE TABLE `wz_override`(`id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,`skillid` int(11) NOT NULL,`key` varchar(20) NOT NULL,`value` varchar(20) NULL,PRIMARY KEY (`id`))DEFAULT CHARACTER SET=utf8;");
            overridedata.forEach((s, d) -> d.forEach((k, v) -> SqlTool.update(con, "INSERT INTO `wz_override`(`skillid`,`key`,`value`) VALUES (?,?,?)", s, k, v)));
            return null;
        });
    }

    public static String getOverrideValue(int skillid, String name) {
        String value = null;
        if (overridedata.containsKey(skillid) && overridedata.get(skillid).containsKey(name)) {
            value = overridedata.get(skillid).get(name);
        }
        return value == null ? "" : value;
    }
}

