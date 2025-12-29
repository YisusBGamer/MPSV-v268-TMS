/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Client.MapleUnionBoardEntry;
import Client.MapleUnionEntry;
import Config.constants.JobConstants;
import Net.server.MapleUnionData;
import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import tools.Triple;

public class MapleUnion {
    private final Map<Integer, MapleUnionEntry> allUnions = new HashMap<Integer, MapleUnionEntry>();
    private final Map<Integer, MapleUnionEntry> fightingUnions = new HashMap<Integer, MapleUnionEntry>();
    private final Map<Integer, Board> boards = new HashMap<Integer, Board>();
    private final Map<Integer, Integer> skills = new HashMap<Integer, Integer>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final int[] addStats = new int[16];
    private int state;

    public MapleUnion() {
        this.reload();
    }

    private void reload() {
        this.lock.writeLock().lock();
        try {
            for (int i = 0; i < this.addStats.length; ++i) {
                this.addStats[i] = 0;
            }
            this.skills.clear();
            this.boards.clear();
            for (Map.Entry<Integer, MapleUnionBoardEntry> entry : MapleUnionData.getInstance().getBoardInfo().entrySet()) {
                this.boards.put(entry.getKey(), new Board(this, entry.getKey(), entry.getValue().getXPos(), entry.getValue().getYPos(), entry.getValue().getGroupIndex()));
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update() {
        this.lock.writeLock().lock();
        try {
            this.reload();
            for (Map.Entry<Integer, MapleUnionEntry> entry : this.fightingUnions.entrySet()) {
                Triple<Integer, Integer, Integer> cardInfo;
                Map<Integer, Point> sizeInfo;
                int level;
                MapleUnionData data = MapleUnionData.getInstance();
                MapleUnionEntry union = entry.getValue();
                MapleUnionBoardEntry boardEntry = MapleUnionData.getInstance().getBoardInfo().get(union.getBoardIndex());
                if (boardEntry == null) continue;
                int xPos = boardEntry.getXPos();
                int yPos = boardEntry.getYPos();
                int rotate = union.getRotate();
                int job = union.getJob();
                int rank = data.getCharacterRank(job, level = union.getLevel());
                if (rank < 0 || (sizeInfo = data.getSizeInfo(JobConstants.getJobBranch(union.getJob()), rank)) == null || (cardInfo = data.getCardInfo(job, level)) == null) continue;
                int skillId = (Integer)cardInfo.mid;
                if (this.skills.containsKey(skillId)) {
                    if (this.skills.get(skillId) < rank) {
                        this.skills.put(skillId, rank);
                    }
                } else {
                    this.skills.put(skillId, rank);
                }
                for (Map.Entry<Integer, Point> entry2 : sizeInfo.entrySet()) {
                    Board board;
                    int n = rotate % 1000;
                    int count = rotate / 1000;
                    int x = entry2.getValue().x;
                    int y = entry2.getValue().y;
                    switch (count) {
                        case 1: {
                            x *= -1;
                            break;
                        }
                        case 2: {
                            y *= -1;
                            break;
                        }
                        case 3: {
                            x *= -1;
                            y *= -1;
                        }
                    }
                    if ((board = this.getBoardByPos(xPos + (int)Math.round((double)x * Math.cos(Math.toRadians(n)) - (double)y * Math.sin(Math.toRadians(n))), yPos + (int)Math.round((double)x * Math.sin(Math.toRadians(n)) + (double)y * Math.cos(Math.toRadians(n))))) == null) continue;
                    board.setActive(true);
                }
            }
            for (Board board : this.boards.values()) {
                if (!board.isActive()) continue;
                int n = board.getGroupIndex();
                this.addStats[n] = this.addStats[n] + 1;
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public int getTotalLevel() {
        int level = 0;
        for (MapleUnionEntry union : this.allUnions.values()) {
            level += union.getLevel();
        }
        return level;
    }

    public int getLevel() {
        int level = 0;
        List allLv = this.allUnions.values().stream().map(MapleUnionEntry::getLevel).collect(Collectors.toCollection(LinkedList::new));
        Collections.sort(allLv, Collections.reverseOrder());
        int i = 0;
        Iterator iterator = allLv.iterator();
        while (iterator.hasNext()) {
            int lv = (Integer)iterator.next();
            if (++i > 40) break;
            level += lv;
        }
        return level;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private Board getBoardByPos(int x, int y) {
        for (Board board : this.boards.values()) {
            if (board.getXPos() != x || board.getYPos() != y) continue;
            return board;
        }
        return null;
    }

    public Map<Integer, Integer> getSkills() {
        this.lock.readLock().lock();
        try {
            Map<Integer, Integer> map = this.skills;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Integer, MapleUnionEntry> getFightingUnions() {
        this.lock.readLock().lock();
        try {
            Map<Integer, MapleUnionEntry> map = this.fightingUnions;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Integer, MapleUnionEntry> getAllUnions() {
        this.lock.readLock().lock();
        try {
            Map<Integer, MapleUnionEntry> map = this.allUnions;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int[] getAddStats() {
        return this.addStats;
    }

    public class Board {
        private final int index;
        private final int xPos;
        private final int yPos;
        private boolean active = false;
        private final int groupIndex;

        public Board(MapleUnion this$0, int index, int xPos, int yPos, int groupIndex) {
            this.index = index;
            this.xPos = xPos;
            this.yPos = yPos;
            this.groupIndex = groupIndex;
        }

        public int getGroupIndex() {
            return this.groupIndex;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return this.active;
        }

        public int getYPos() {
            return this.yPos;
        }

        public int getXPos() {
            return this.xPos;
        }
    }
}

