/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Client.CardData;
import Client.MapleCharacter;
import Client.MapleClient;
import Config.constants.SkillConstants;
import Net.server.CharacterCardFactory;
import connection.OutPacket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Triple;

public class MapleCharacterCards {
    private final List<Pair<Integer, Integer>> skills = new ArrayList<Pair<Integer, Integer>>();
    private Map<Integer, CardData> cards = new LinkedHashMap<Integer, CardData>();

    public Map<Integer, CardData> getCards() {
        return this.cards;
    }

    public void setCards(Map<Integer, CardData> cads) {
        this.cards = cads;
    }

    public List<Pair<Integer, Integer>> getCardEffects() {
        return this.skills;
    }

    public void calculateEffects() {
        List<Integer> uid;
        this.skills.clear();
        int deck1amount = 0;
        int deck2amount = 0;
        int deck3amount = 0;
        int reqRank1 = 0;
        int reqRank2 = 0;
        int reqRank3 = 0;
        LinkedList<Integer> cardSkillIds1 = new LinkedList<Integer>();
        LinkedList<Integer> cardSkillIds2 = new LinkedList<Integer>();
        LinkedList<Integer> cardSkillIds3 = new LinkedList<Integer>();
        CharacterCardFactory cardFactory = CharacterCardFactory.getInstance();
        for (Map.Entry<Integer, CardData> cardInfo : this.cards.entrySet()) {
            if (cardInfo.getValue().chrId <= 0) continue;
            Triple<Integer, Integer, Integer> skillData = cardFactory.getCardSkill(cardInfo.getValue().job, cardInfo.getValue().level);
            if (cardInfo.getKey() < 4) {
                if (skillData != null) {
                    cardSkillIds1.add(skillData.getLeft());
                    this.skills.add(new Pair<Integer, Integer>(skillData.getMid(), skillData.getRight()));
                }
                ++deck1amount;
                if (reqRank1 != 0 && reqRank1 <= cardInfo.getValue().level) continue;
                reqRank1 = cardInfo.getValue().level;
                continue;
            }
            if (cardInfo.getKey() > 3 && cardInfo.getKey() < 7) {
                if (skillData != null) {
                    cardSkillIds2.add(skillData.getLeft());
                    this.skills.add(new Pair<Integer, Integer>(skillData.getMid(), skillData.getRight()));
                }
                ++deck2amount;
                if (reqRank2 != 0 && reqRank2 <= cardInfo.getValue().level) continue;
                reqRank2 = cardInfo.getValue().level;
                continue;
            }
            if (skillData != null) {
                cardSkillIds3.add(skillData.getLeft());
                this.skills.add(new Pair<Integer, Integer>(skillData.getMid(), skillData.getRight()));
            }
            ++deck3amount;
            if (reqRank3 != 0 && reqRank3 <= cardInfo.getValue().level) continue;
            reqRank3 = cardInfo.getValue().level;
        }
        if (deck1amount == 3 && cardSkillIds1.size() == 3) {
            uid = cardFactory.getUniqueSkills(cardSkillIds1);
            for (int i : uid) {
                this.skills.add(new Pair<Integer, Integer>(i, SkillConstants.getCardSkillLevel(reqRank1)));
            }
            this.skills.add(new Pair<Integer, Integer>(cardFactory.getRankSkill(reqRank1), 1));
        }
        if (deck2amount == 3 && cardSkillIds2.size() == 3) {
            uid = cardFactory.getUniqueSkills(cardSkillIds2);
            for (int i : uid) {
                this.skills.add(new Pair<Integer, Integer>(i, SkillConstants.getCardSkillLevel(reqRank2)));
            }
            this.skills.add(new Pair<Integer, Integer>(cardFactory.getRankSkill(reqRank2), 1));
        }
        if (deck3amount == 3 && cardSkillIds3.size() == 3) {
            uid = cardFactory.getUniqueSkills(cardSkillIds3);
            for (int i : uid) {
                this.skills.add(new Pair<Integer, Integer>(i, SkillConstants.getCardSkillLevel(reqRank3)));
            }
            this.skills.add(new Pair<Integer, Integer>(cardFactory.getRankSkill(reqRank3), 1));
        }
    }

    public void recalcLocalStats(MapleCharacter chr) {
        int pos = -1;
        for (Map.Entry<Integer, CardData> x : this.cards.entrySet()) {
            if (x.getValue().chrId != chr.getId()) continue;
            pos = x.getKey();
            break;
        }
        if (pos != -1) {
            if (!CharacterCardFactory.getInstance().canHaveCard(chr.getLevel(), chr.getJob())) {
                this.cards.remove(pos);
            } else {
                this.cards.put(pos, new CardData(chr.getId(), chr.getLevel(), chr.getJob()));
            }
        }
        this.calculateEffects();
    }

    public void loadCards(MapleClient c, boolean channelserver) {
        this.cards = CharacterCardFactory.getInstance().loadCharacterCards(c.getAccID(), c.getWorldId());
        if (channelserver) {
            this.calculateEffects();
        }
    }

    public void connectData(OutPacket out) {
        if (this.cards.isEmpty()) {
            out.encodeZero(108);
            return;
        }
        int poss = 0;
        for (CardData i : this.cards.values()) {
            if (++poss > 9) {
                return;
            }
            out.encodeInt(i.chrId);
            out.encodeInt(i.level);
            out.encodeInt(i.job);
        }
    }
}

