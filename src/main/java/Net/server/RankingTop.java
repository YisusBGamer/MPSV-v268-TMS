/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.RankingTop$CharNameAndId
 */
package Net.server;

import Client.MapleCharacter;
import Database.DatabaseLoader;
import Net.server.RankingTop;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RankingTop {
    private static final Logger log = LoggerFactory.getLogger(RankingTop.class);
    private static final RankingTop instance = new RankingTop();
    private final Map<String, List<CharNameAndId>> rankcache = new HashMap<String, List<CharNameAndId>>();

    private RankingTop() {
        this.initAll();
    }

    public static RankingTop getInstance() {
        return instance;
    }

    public final void initAll() {
        this.rankcache.clear();
    }

    public final List<CharNameAndId> getRanking(String rankingname) {
        return this.getRanking(rankingname, 10);
    }

    public final List<CharNameAndId> getRanking(String rankingname, int previous) {
        return this.getRanking(rankingname, 10, true);
    }

    public final List<CharNameAndId> getRanking(String rankingname, int previous, boolean repeatable) {
        LinkedList<CharNameAndId> ret = null;
        if (!this.rankcache.containsKey(rankingname)) {
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                ResultSet rs;
                try (PreparedStatement ps = con.prepareStatement("SELECT r.accountid, r.characterid, r.rankingname, r.value, r.time, c.name, c.gender FROM (SELECT * FROM rankingtop ORDER BY value DESC) r, characters AS c WHERE r.characterid = c.id AND r.rankingname = ? " + (repeatable ? "" : "GROUP BY r.characterid") + " ORDER BY r.value DESC, r.time DESC LIMIT ?;");){
                    ps.setString(1, rankingname);
                    ps.setInt(2, previous);
                    rs = ps.executeQuery();
                    ret = new LinkedList<CharNameAndId>();
                    while (rs.next()) {
                        ret.add(new CharNameAndId(rs.getInt("accountid"), rs.getInt("characterid"), rs.getString("rankingname"), rs.getInt("value"), rs.getTimestamp("time"), rs.getString("name"), rs.getInt("gender")));
                    }
                }
                rs.close();
            }
            catch (SQLException ex) {
                log.error("getRanking", ex);
            }
        } else {
            return this.rankcache.get(rankingname);
        }
        this.rankcache.put(rankingname, ret);
        return ret;
    }

    public final void insertRanking(MapleCharacter player, String rankingname, int value) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO rankingtop (id, accountid, characterid, rankingname, value, time) VALUES (DEFAULT, ?, ?, ?, ?, CURRENT_TIMESTAMP)");){
            ps.setInt(1, player.getAccountID());
            ps.setInt(2, player.getId());
            ps.setString(3, rankingname);
            ps.setInt(4, value);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            log.error("insertRanking", ex);
        }
    }

    public static final class CharNameAndId {

        public final String name, rankingname;
        public final int accountid, characterid, value, gender;
        public final Timestamp time;

        public CharNameAndId(final int accountid, final int characterid, final String rankingname, final int value, final Timestamp time, String name, int gender) {
            super();
            this.accountid = accountid;
            this.characterid = characterid;
            this.rankingname = rankingname;
            this.value = value;
            this.time = time;
            this.name = name;
            this.gender = gender;
        }
    }
}

