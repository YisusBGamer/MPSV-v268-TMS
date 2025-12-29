/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import Client.MapleUnionBoardEntry;
import Config.constants.JobConstants;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import tools.Triple;

public class MapleUnionData {
    private final Map<Integer, MapleUnionBoardEntry> BoardInfo = new HashMap<Integer, MapleUnionBoardEntry>();
    private final Map<Integer, Map<Integer, Map<Integer, Point>>> sizeInfo = new HashMap<Integer, Map<Integer, Map<Integer, Point>>>();
    private final Map<Integer, Integer> skillInfo = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> cardInfo = new HashMap<Integer, Integer>();
    private final Map<Integer, Map<Integer, MapleUnionRankData>> rankInfo = new HashMap<Integer, Map<Integer, MapleUnionRankData>>();

    public static MapleUnionData getInstance() {
        return InstanceHolder.instance;
    }

    public Map<Integer, Map<Integer, MapleUnionRankData>> getRankInfo() {
        return this.rankInfo;
    }

    public Map<Integer, Point> getSizeInfo(int jobGrade, int characterRank) {
        return this.sizeInfo.containsKey(jobGrade) ? (Map)this.sizeInfo.get(jobGrade).getOrDefault(characterRank, null) : null;
    }

    public Map<Integer, MapleUnionBoardEntry> getBoardInfo() {
        return this.BoardInfo;
    }

    public Triple<Integer, Integer, Integer> getCardInfo(int job, int level) {
        int skillId;
        if (this.cardInfo.containsKey(job / 10) && (skillId = this.cardInfo.get(job / 10).intValue()) > 0) {
            return new Triple<Integer, Integer, Integer>(skillId - 71000000, skillId, this.getCharacterRank(job, level));
        }
        return null;
    }

    public int getCharacterRank(int job, int level) {
        if (JobConstants.is神之子(job)) {
            if (level >= 130 && level < 160) {
                return 1;
            }
            if (level >= 160 && level < 180) {
                return 2;
            }
            if (level >= 180 && level < 200) {
                return 3;
            }
            if (level >= 200 && level < 250) {
                return 4;
            }
            if (level >= 250) {
                return 5;
            }
        } else {
            if (level >= 60 && level < 100) {
                return 1;
            }
            if (level >= 100 && level < 140) {
                return 2;
            }
            if (level >= 140 && level < 200) {
                return 3;
            }
            if (level >= 200 && level < 250) {
                return 4;
            }
            if (level >= 250) {
                return 5;
            }
        }
        return -1;
    }

    public void init() {
        MapleData unionData = MapleDataProviderFactory.getEtc().getData("mapleUnion.img");
        for (MapleData boardData : unionData.getChildByPath("BoardInfo")) {
            this.BoardInfo.put(Integer.parseInt(boardData.getName()), new MapleUnionBoardEntry(MapleDataTool.getInt("xPos", boardData, 0), MapleDataTool.getInt("yPos", boardData, 0), MapleDataTool.getInt("changeable", boardData, 0) > 0, MapleDataTool.getInt("groupIndex", boardData, 0), MapleDataTool.getInt("openLevel", boardData, 0)));
        }
        for (MapleData sizeData : unionData.getChildByPath("CharacterSize")) {
            int jobGrade = Integer.parseInt(sizeData.getName());
            Map<Integer, Map<Integer, Point>> map = sizeInfo.computeIfAbsent(jobGrade, key -> new HashMap<>());
            for (MapleData rankData : sizeData) {
                int rank = Integer.parseInt(rankData.getName());
                Map map2 = map.computeIfAbsent(rank, key -> new HashMap());
                for (MapleData info : rankData) {
                    map2.put(Integer.parseInt(info.getName()), MapleDataTool.getPoint(info));
                }
            }
        }
        for (MapleData skillData : unionData.getChildByPath("SkillInfo")) {
            this.skillInfo.put(Integer.parseInt(skillData.getName()), MapleDataTool.getInt("skillID", skillData, 0));
        }
        for (MapleData cardData : unionData.getChildByPath("Card")) {
            this.cardInfo.put(Integer.parseInt(cardData.getName()), MapleDataTool.getInt("skillID", cardData, 0));
        }
        for (MapleData rankData : unionData.getChildByPath("unionRank")) {
            String name = MapleDataTool.getString(rankData.getChildByPath("info/name"));
            Map<Integer, MapleUnionRankData> map = new HashMap<>();
            int rank = Integer.parseInt(rankData.getName());
            for (MapleData info : rankData) {
                if (info.getName().equals("info")) continue;
                int grade = Integer.parseInt(info.getName());
                map.put(grade, (new MapleUnionRankData(this, name, rank, grade, MapleDataTool.getInt("level", info, 0), MapleDataTool.getInt("attackerCount", info, 0), MapleDataTool.getInt("coinStackMax", info, 0))));
            }
            this.rankInfo.put(rank, map);
        }
    }

    private static class InstanceHolder {
        private static final MapleUnionData instance = new MapleUnionData();

        private InstanceHolder() {
        }
    }

    public class MapleUnionRankData {
        private final String name;
        private final int level;
        private final int attackerCount;
        private final int coinStackMax;
        private final int subRank;
        private final int rank;

        public MapleUnionRankData(MapleUnionData this$0, String name, int rank, int grade, int level, int attackerCount, int coinStackMax) {
            this.name = name;
            this.level = level;
            this.attackerCount = attackerCount;
            this.coinStackMax = coinStackMax;
            this.subRank = grade;
            this.rank = rank;
        }

        public int getRank() {
            return this.rank;
        }

        public int getAttackerCount() {
            return this.attackerCount;
        }

        public int getCoinStackMax() {
            return this.coinStackMax;
        }

        public int getSubRank() {
            return this.subRank;
        }

        public int getLevel() {
            return this.level;
        }

        public String toString() {
            return this.name + this.subRank + "階段";
        }
    }
}

