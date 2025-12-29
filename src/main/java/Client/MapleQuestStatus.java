/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Net.server.life.MapleLifeFactory;
import Net.server.quest.MapleQuest;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MapleQuestStatus {
    private MapleQuest quest;
    private byte status;
    private Map<Integer, Integer> killedMobs = null;
    private int npc;
    private long completionTime;
    private int forfeited = 0;
    private String customData;
    private int fromChrID = -1;

    public MapleQuestStatus(MapleQuest quest, int status) {
        this.quest = quest;
        this.setStatus((byte)status);
        this.completionTime = System.currentTimeMillis();
        if (status == 1 && !quest.getRelevantMobs().isEmpty()) {
            this.registerMobs();
        }
    }

    public MapleQuestStatus(MapleQuest quest, byte status, int npc) {
        this.quest = quest;
        this.setStatus(status);
        this.setNpc(npc);
        this.completionTime = System.currentTimeMillis();
        if (status == 1 && !quest.getRelevantMobs().isEmpty()) {
            this.registerMobs();
        }
    }

    public MapleQuest getQuest() {
        return this.quest;
    }

    public void setQuest(int qid) {
        this.quest = MapleQuest.getInstance(qid);
    }

    public byte getStatus() {
        return this.status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public int getNpc() {
        return this.npc;
    }

    public void setNpc(int npc) {
        this.npc = npc;
    }

    public boolean isCustom() {
        switch (this.quest.getId()) {
            case 99997: 
            case 99998: 
            case 99999: 
            case 111111: 
            case 111112: 
            case 122200: 
            case 122210: 
            case 122221: 
            case 122223: 
            case 122224: 
            case 122500: 
            case 122501: 
            case 122700: 
            case 122800: 
            case 122900: 
            case 122901: 
            case 122902: 
            case 123000: 
            case 123455: 
            case 123456: 
            case 123457: 
            case 150001: {
                return true;
            }
        }
        return false;
    }

    private void registerMobs() {
        this.killedMobs = new LinkedHashMap<Integer, Integer>();
        for (int i : this.quest.getRelevantMobs().keySet()) {
            this.killedMobs.put(i, 0);
        }
    }

    private int maxMob(int mobid) {
        for (Map.Entry<Integer, Integer> qs : this.quest.getRelevantMobs().entrySet()) {
            if (qs.getKey() != mobid) continue;
            return qs.getValue();
        }
        return 0;
    }

    public boolean mobKilled(int id, int skillID) {
        if (this.quest != null && this.quest.getSelectedSkillID() > 0 && this.quest.getSelectedSkillID() != skillID) {
            return false;
        }
        Integer mob = this.killedMobs.get(id);
        if (mob != null) {
            int mo = this.maxMob(id);
            if (mob >= mo) {
                return false;
            }
            this.killedMobs.put(id, Math.min(mob + 1, mo));
            return true;
        }
        for (Map.Entry<Integer, Integer> mo : this.killedMobs.entrySet()) {
            if (!MapleLifeFactory.exitsQuestCount(mo.getKey(), id)) continue;
            int mobb = this.maxMob(mo.getKey());
            if (mo.getValue() >= mobb) {
                return false;
            }
            this.killedMobs.put(mo.getKey(), Math.min(mo.getValue() + 1, mobb));
            return true;
        }
        return false;
    }

    public void setMobKills(int id, int count) {
        if (this.killedMobs == null) {
            this.registerMobs();
        }
        this.killedMobs.put(id, count);
    }

    public boolean hasMobKills() {
        return this.killedMobs != null && this.killedMobs.size() > 0;
    }

    public int getMobKills(int id) {
        return this.killedMobs.getOrDefault(id, 0);
    }

    public Map<Integer, Integer> getMobKills() {
        return this.killedMobs;
    }

    public long getCompletionTime() {
        return this.completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public int getForfeited() {
        return this.forfeited;
    }

    public void setForfeited(int forfeited) {
        if (forfeited < this.forfeited) {
            throw new IllegalArgumentException("Can't set forfeits to something lower than before.");
        }
        this.forfeited = forfeited;
    }

    public String getCustomData() {
        return this.customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public boolean isWorldShare() {
        return this.fromChrID > -1;
    }

    public int getFromChrID() {
        return this.fromChrID;
    }

    public void setFromChrID(int fromChrID) {
        this.fromChrID = fromChrID;
    }
}

