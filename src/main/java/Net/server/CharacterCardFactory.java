/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import Client.CardData;
import Config.constants.SkillConstants;
import Database.DatabaseLoader;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Triple;

public class CharacterCardFactory {
    private static final Logger log = LoggerFactory.getLogger(CharacterCardFactory.class);
    private static final CharacterCardFactory instance = new CharacterCardFactory();
    protected final MapleDataProvider etcData = MapleDataProviderFactory.getEtc();
    protected final Map<Integer, Integer> cardEffects = new HashMap<Integer, Integer>();
    protected final Map<Integer, List<Integer>> uniqueEffects = new HashMap<Integer, List<Integer>>();

    public static CharacterCardFactory getInstance() {
        return instance;
    }

    public void initialize() {
        MapleData data = this.etcData.getData("CharacterCard.img");
        for (MapleData Card : data.getChildByPath("Card")) {
            int skillId = MapleDataTool.getIntConvert("skillID", Card, 0);
            if (skillId <= 0) continue;
            this.cardEffects.put(Integer.parseInt(Card.getName()), skillId);
        }
        for (MapleData Deck : data.getChildByPath("Deck")) {
            boolean uniqueEffect = MapleDataTool.getIntConvert("uniqueEffect", Deck, 0) > 0;
            int skillId = MapleDataTool.getIntConvert("skillID", Deck, 0);
            if (!uniqueEffect) continue;
            ArrayList<Integer> ids = new ArrayList<Integer>();
            for (MapleData reqCardID : Deck.getChildByPath("reqCardID")) {
                ids.add(MapleDataTool.getIntConvert(reqCardID));
            }
            if (skillId <= 0 || ids.isEmpty()) continue;
            this.uniqueEffects.put(skillId, ids);
        }
    }

    public Triple<Integer, Integer, Integer> getCardSkill(int job, int level) {
        int skillid = this.cardEffects.get(job / 10);
        if (skillid <= 0) {
            return null;
        }
        return new Triple<Integer, Integer, Integer>(skillid - 71000000, skillid, SkillConstants.getCardSkillLevel(level));
    }

    public List<Integer> getUniqueSkills(List<Integer> special) {
        LinkedList<Integer> ret = new LinkedList<Integer>();
        for (Map.Entry<Integer, List<Integer>> m : this.uniqueEffects.entrySet()) {
            if (!m.getValue().contains(special.get(0)) || !m.getValue().contains(special.get(1)) || !m.getValue().contains(special.get(2))) continue;
            ret.add(m.getKey());
        }
        return ret;
    }

    public boolean isUniqueEffects(int skillId) {
        return this.uniqueEffects.containsKey(skillId);
    }

    public int getRankSkill(int level) {
        return SkillConstants.getCardSkillLevel(level) + 71001099;
    }

    public boolean canHaveCard(int level, int job) {
        return level >= 30 && this.cardEffects.get(job / 10) != null;
    }

    public Map<Integer, CardData> loadCharacterCards(int accId, int serverId) {
        LinkedHashMap<Integer, CardData> cards = new LinkedHashMap<Integer, CardData>();
        Map<Integer, Pair<Short, Short>> inf = this.loadCharactersInfo(accId, serverId);
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM `character_cards` WHERE `accid` = ?");){
            ps.setInt(1, accId);
            try (ResultSet rs = ps.executeQuery();){
                int deck1 = 0;
                int deck2 = 3;
                int deck3 = 6;
                while (rs.next()) {
                    int chrId = rs.getInt("characterid");
                    Pair<Short, Short> x = inf.get(chrId);
                    if (x == null || !this.canHaveCard(x.getLeft().shortValue(), x.getRight().shortValue())) continue;
                    int position = rs.getInt("position");
                    if (position < 4) {
                        cards.put(++deck1, new CardData(chrId, x.getLeft().shortValue(), x.getRight()));
                        continue;
                    }
                    if (position > 3 && position < 7) {
                        cards.put(++deck2, new CardData(chrId, x.getLeft().shortValue(), x.getRight()));
                        continue;
                    }
                    cards.put(++deck3, new CardData(chrId, x.getLeft().shortValue(), x.getRight()));
                }
            }
        }
        catch (SQLException e) {
            log.error("Failed to load character cards. Reason: ", e);
        }
        for (int i = 1; i <= 9; ++i) {
            cards.computeIfAbsent(i, k -> new CardData(0, 0, (short) 0));
        }
        return cards;
    }

    public Map<Integer, Pair<Short, Short>> loadCharactersInfo(int accId, int serverId) {
        LinkedHashMap<Integer, Pair<Short, Short>> chars = new LinkedHashMap<Integer, Pair<Short, Short>>();
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, level, job FROM characters WHERE accountid = ? AND world = ?");){
            ps.setInt(1, accId);
            ps.setInt(2, serverId);
            try (ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    chars.put(rs.getInt("id"), new Pair<Short, Short>(rs.getShort("level"), rs.getShort("job")));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("error loading characters info. reason: " + String.valueOf(e));
        }
        return chars;
    }
}

