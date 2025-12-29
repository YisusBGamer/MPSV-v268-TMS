/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.PredictCardFactory$PredictCard
 *  Net.server.PredictCardFactory$PredictCardComment
 */
package Net.server;

import Net.server.PredictCardFactory;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.util.HashMap;
import java.util.Map;
import tools.Randomizer;

public class PredictCardFactory {
    private static final PredictCardFactory instance = new PredictCardFactory();
    protected final MapleDataProvider etcData = MapleDataProviderFactory.getEtc();
    protected final Map<Integer, PredictCard> predictCard = new HashMap<Integer, PredictCard>();
    protected final Map<Integer, PredictCardComment> predictCardComment = new HashMap<Integer, PredictCardComment>();

    public static PredictCardFactory getInstance() {
        return instance;
    }

    public void initialize() {
        if (!this.predictCard.isEmpty() || !this.predictCardComment.isEmpty()) {
            return;
        }
        MapleData infoData = this.etcData.getData("PredictCard.img");
        for (MapleData cardDat : infoData) {
            if (cardDat.getName().equals("comment")) continue;
            PredictCard card = new PredictCard();
            card.name = MapleDataTool.getString("name", cardDat, "");
            card.comment = MapleDataTool.getString("comment", cardDat, "");
            this.predictCard.put(Integer.parseInt(cardDat.getName()), card);
        }
        MapleData commentData = infoData.getChildByPath("comment");
        for (MapleData commentDat : commentData) {
            PredictCardComment comment = new PredictCardComment();
            comment.worldmsg0 = MapleDataTool.getString("0", commentDat, "");
            comment.worldmsg1 = MapleDataTool.getString("1", commentDat, "");
            comment.score = MapleDataTool.getIntConvert("score", commentDat, 0);
            comment.effectType = MapleDataTool.getIntConvert("effectType", commentDat, 0);
            this.predictCardComment.put(Integer.parseInt(commentDat.getName()), comment);
        }
    }

    public PredictCard getPredictCard(int id) {
        if (!this.predictCard.containsKey(id)) {
            return null;
        }
        return this.predictCard.get(id);
    }

    public PredictCardComment getPredictCardComment(int id) {
        if (!this.predictCardComment.containsKey(id)) {
            return null;
        }
        return this.predictCardComment.get(id);
    }

    public PredictCardComment RandomCardComment() {
        return this.getPredictCardComment(Randomizer.nextInt(this.predictCardComment.size()));
    }

    public int getCardCommentSize() {
        return this.predictCardComment.size();
    }

    public static class PredictCard {

        public String name, comment;
    }

    public static class PredictCardComment {

        public int score, effectType;
        public String worldmsg0, worldmsg1;
    }
}

