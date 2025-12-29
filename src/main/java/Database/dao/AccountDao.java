/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.world.WorldGuildService
 */
package Database.dao;

import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Server.world.WorldGuildService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import tools.Pair;

public class AccountDao {
    public static List<Pair<Integer, Long>> getPendingDeleteChrId(int accId, int world) {
        return SqlTool.queryAndGetList("SELECT `character_id`, `time` FROM `accounts_deletechr` WHERE `account_id` = ? AND `world` = ?", rs -> new Pair<Integer, Long>(rs.getInt(1), rs.getTimestamp("time").getTime()), accId, world);
    }

    public static void clearOutdatedPendingDeleteChr(int accId, int worldId) {
        List<Pair<Integer, Long>> list = AccountDao.getPendingDeleteChrId(accId, worldId);
        for (Pair<Integer, Long> result : list) {
            if (System.currentTimeMillis() - result.getRight() <= 172800000L) continue;
            AccountDao.deregisterDeleteChr(accId, worldId, result.getLeft());
            AccountDao.deleteCharacter(accId, result.getLeft());
        }
    }

    public static void registerDeleteChr(int accId, int world, int chrId) {
        SqlTool.update("INSERT INTO `accounts_deletechr` (account_id, world, character_id) VALUES (?, ?, ?)", accId, world, chrId);
    }

    public static void deregisterDeleteChr(int accId, int world, int chrId) {
        SqlTool.update("DELETE FROM `accounts_deletechr` WHERE `account_id` = ? AND `world` = ? AND `character_id` = ?", accId, world, chrId);
    }

    public static int deleteCharacter(int accId, int cid) {
        Integer result = DatabaseLoader.DatabaseConnection.domain(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM characters WHERE id = ? AND accountid = ?");
            ps.setInt(1, cid);
            ps.setInt(2, accId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return 1;
            }
            if (rs.getInt("guildid") > 0) {
                if (rs.getInt("guildrank") == 1) {
                    rs.close();
                    ps.close();
                    return 1;
                }
                WorldGuildService.getInstance().deleteGuildCharacter(rs.getInt("guildid"), cid);
            }
            SqlTool.update(con, "DELETE FROM characters WHERE id = ?", cid);
            SqlTool.update(con, "DELETE FROM hiredmerch WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM mts_cart WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM mts_items WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM cheatlog WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM inventoryitems WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM famelog WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM famelog WHERE characterid_to = ?", cid);
            SqlTool.update(con, "DELETE FROM dueypackages WHERE RecieverId = ?", cid);
            SqlTool.update(con, "DELETE FROM wishlist WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM buddies WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM buddies WHERE buddyid = ?", cid);
            SqlTool.update(con, "DELETE FROM keymap WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM savedlocations WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM skills WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM skills WHERE teachId = ?", cid);
            SqlTool.update(con, "DELETE FROM familiars WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM skillmacros WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM queststatus WHERE characterid = ?", cid);
            SqlTool.update(con, "DELETE FROM inventoryslot WHERE characters_id = ?", cid);
            SqlTool.update(con, "DELETE FROM bank WHERE charid = ?", cid);
            SqlTool.update(con, "DELETE FROM bosslog WHERE characterid = ?", cid);
            return 0;
        }, "刪除角色出錯");
        return result == null ? 1 : result;
    }
}

