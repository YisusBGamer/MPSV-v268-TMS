/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.quest.MapleQuest$1
 *  Net.server.quest.MapleQuestAction
 *  Net.server.quest.MapleQuestActionType
 *  Net.server.quest.MapleQuestRequirement
 *  Net.server.quest.MapleQuestRequirementType
 */
package Net.server.quest;

import Client.MapleCharacter;
import Client.MapleQuestStatus;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.enums.UserChatMessageType;
import Net.server.quest.MapleQuest;
import Net.server.quest.MapleQuestAction;
import Net.server.quest.MapleQuestActionType;
import Net.server.quest.MapleQuestRequirement;
import Net.server.quest.MapleQuestRequirementType;
import Opcode.Opcode.EffectOpcode;
import Packet.EffectPacket;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Pair;

public class MapleQuest
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private static final List<MapleQuest> BulbQuest = new LinkedList<MapleQuest>();
    protected static final Map<Integer, MapleQuest> quests = new LinkedHashMap<Integer, MapleQuest>();
    protected final List<MapleQuestRequirement> startReqs = new LinkedList<MapleQuestRequirement>();
    protected final List<MapleQuestRequirement> completeReqs = new LinkedList<MapleQuestRequirement>();
    protected final List<MapleQuestAction> startActs = new LinkedList<MapleQuestAction>();
    protected final List<MapleQuestAction> completeActs = new LinkedList<MapleQuestAction>();
    protected final Map<String, List<Pair<String, Pair<String, Integer>>>> partyQuestInfo = new LinkedHashMap<String, List<Pair<String, Pair<String, Integer>>>>();
    protected final Map<Integer, Integer> relevantMobs = new LinkedHashMap<Integer, Integer>();
    protected final Map<Integer, Integer> questItems = new LinkedHashMap<Integer, Integer>();
    protected int id;
    protected String name = "";
    private boolean autoStart = false;
    private boolean autoPreComplete = false;
    private boolean repeatable = false;
    private boolean blocked = false;
    private boolean autoAccept = false;
    private boolean autoComplete = false;
    private boolean selfStart = false;
    private int viewMedalItem = 0;
    private int selectedSkillID = 0;
    private String startscript = "";
    private String endscript = "";

    protected MapleQuest(int id) {
        this.id = id;
    }

    private static MapleQuest loadQuest(ResultSet rs, PreparedStatement psr, PreparedStatement psa, PreparedStatement pss, PreparedStatement psq, PreparedStatement psi, PreparedStatement psp) throws SQLException {
        MapleQuest ret = new MapleQuest(rs.getInt("questid"));
        ret.name = rs.getString("name");
        ret.autoStart = rs.getInt("autoStart") > 0;
        ret.autoPreComplete = rs.getInt("autoPreComplete") > 0;
        ret.autoAccept = rs.getInt("autoAccept") > 0;
        ret.autoComplete = rs.getInt("autoComplete") > 0;
        ret.viewMedalItem = rs.getInt("viewMedalItem");
        ret.selectedSkillID = rs.getInt("selectedSkillID");
        ret.blocked = rs.getInt("blocked") > 0;
        ret.selfStart = rs.getInt("selfStart") > 0;
        psr.setInt(1, ret.id);
        ResultSet rse = psr.executeQuery();
        MapleQuestRequirement jobStart = null;
        MapleQuestRequirement jobComplete = null;
        while (rse.next()) {
            MapleQuestRequirementType type = MapleQuestRequirementType.getByWZName(rse.getString("name"));
            MapleQuestRequirement req = new MapleQuestRequirement(ret, type, rse);
            switch (type) {
                case dayN:
                case dayByDay:
                case interval:
                    ret.repeatable = true;
                    break;
                case normalAutoStart:
                    ret.repeatable = true;
                    ret.autoStart = true;
                    break;
                case startscript:
                    ret.startscript = rse.getString("stringStore");
                    if (ret.startscript == null) ret.startscript = "";
                    break;
                case endscript:
                    ret.endscript = rse.getString("stringStore");
                    if (ret.endscript == null) ret.endscript = "";
                    break;
                case mob:
                    for (Pair<Integer, Integer> mob : req.getDataStore()) {
                        ret.relevantMobs.put(mob.left, mob.right);
                    }
                    break;
                case item:
                    for (Pair<Integer, Integer> it : req.getDataStore()) {
                        ret.questItems.put(it.left, it.right);
                    }
                    break;
                case job:
                case job_CN:
                case job_TW: {
                    boolean start = rse.getInt("type") == 0;
                    if (start) {
                        if (jobStart == null) {
                            jobStart = req;
                        } else {
                            List<Pair<Integer, Integer>> stores = new LinkedList<>(jobStart.getDataStore());
                            for (Pair<Integer, Integer> pair : req.getDataStore()) {
                                boolean found = false;
                                for (Pair<Integer, Integer> p : jobStart.getDataStore()) {
                                    if (p.getLeft() == pair.getLeft() && p.getRight() == pair.getRight()) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    stores.add(pair);
                                }
                            }
                            jobStart.getDataStore().clear();
                            for (Pair<Integer, Integer> pair : stores) {
                                jobStart.getDataStore().add(pair);
                            }
                        }
                    } else {
                        if (jobComplete == null) {
                            jobComplete = req;
                        } else {
                            List<Pair<Integer, Integer>> stores = new LinkedList<>(jobComplete.getDataStore());
                            for (Pair<Integer, Integer> pair : req.getDataStore()) {
                                boolean found = false;
                                for (Pair<Integer, Integer> p : jobComplete.getDataStore()) {
                                    if (p.getLeft() == pair.getLeft() && p.getRight() == pair.getRight()) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    stores.add(pair);
                                }
                            }
                            jobComplete.getDataStore().clear();
                            for (Pair<Integer, Integer> pair : stores) {
                                jobComplete.getDataStore().add(pair);
                            }
                        }
                    }
                    break;
                }
            }
            if (req.getType() != MapleQuestRequirementType.job) {
                if (rse.getInt("type") == 0) {
                    ret.startReqs.add(req);
                } else {
                    ret.completeReqs.add(req);
                }
            }
        }
        if (jobStart != null) {
            ret.startReqs.add(jobStart);
        }
        if (jobComplete != null) {
            ret.completeReqs.add(jobComplete);
        }
        if (ret.isSelfStart()) {
            BulbQuest.add(ret);
        }
        rse.close();
        psa.setInt(1, ret.id);
        rse = psa.executeQuery();
        while (rse.next()) {
            MapleQuestActionType ty = MapleQuestActionType.getByWZName((String)rse.getString("name"));
            if (rse.getInt("type") == 0) {
                if (ty == MapleQuestActionType.item && ret.id == 7103) continue;
                ret.startActs.add(new MapleQuestAction(ty, rse, ret, pss, psq, psi));
                continue;
            }
            if (ty == MapleQuestActionType.item && ret.id == 7102) continue;
            ret.completeActs.add(new MapleQuestAction(ty, rse, ret, pss, psq, psi));
        }
        rse.close();
        psp.setInt(1, ret.id);
        rse = psp.executeQuery();
        while (rse.next()) {
            if (!ret.partyQuestInfo.containsKey(rse.getString("rank"))) {
                ret.partyQuestInfo.put(rse.getString("rank"), new ArrayList());
            }
            ret.partyQuestInfo.get(rse.getString("rank")).add(new Pair<String, Pair<String, Integer>>(rse.getString("mode"), new Pair<String, Integer>(rse.getString("property"), rse.getInt("value"))));
        }
        rse.close();
        return ret;
    }

    public static void initQuests(Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_questdata");
            PreparedStatement psr = con.prepareStatement("SELECT * FROM wz_questreqdata WHERE questid = ?");
            PreparedStatement psa = con.prepareStatement("SELECT * FROM wz_questactdata WHERE questid = ?");
            PreparedStatement pss = con.prepareStatement("SELECT * FROM wz_questactskilldata WHERE uniqueid = ?");
            PreparedStatement psq = con.prepareStatement("SELECT * FROM wz_questactquestdata WHERE uniqueid = ?");
            PreparedStatement psi = con.prepareStatement("SELECT * FROM wz_questactitemdata WHERE uniqueid = ?");
            PreparedStatement psp = con.prepareStatement("SELECT * FROM wz_questpartydata WHERE questid = ?");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                quests.put(rs.getInt("questid"), MapleQuest.loadQuest(rs, psr, psa, pss, psq, psi, psp));
            }
            rs.close();
            ps.close();
            psr.close();
            psa.close();
            pss.close();
            psq.close();
            psi.close();
            psp.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MapleQuest getInstance(int id) {
        return quests.computeIfAbsent(id, MapleQuest::new);
    }

    public static Collection<MapleQuest> getAllInstances() {
        return quests.values();
    }

    public static List<MapleQuest> GetBulbQuest() {
        return BulbQuest;
    }

    public List<Pair<String, Pair<String, Integer>>> getInfoByRank(String rank) {
        return this.partyQuestInfo.get(rank);
    }

    public boolean isPartyQuest() {
        return this.partyQuestInfo.size() > 0;
    }

    public int getSelectedSkillID() {
        return this.selectedSkillID;
    }

    public String getName() {
        return this.name;
    }

    public List<MapleQuestAction> getCompleteActs() {
        return this.completeActs;
    }

    public boolean canStart(MapleCharacter chr, Integer npcid) {
        if (chr.getQuest(this).getStatus() != 0) {
            if (chr.getQuest(this).getStatus() != 2) {
                if (chr.isAdmin()) {
                    chr.dropMessage(6, "[Quest Start] canStart - status != 0 && status != 2");
                }
                return false;
            }
            if (!this.repeatable) {
                if (chr.isAdmin()) {
                    chr.dropMessage(6, "[Quest Start] canStart - status != 0 && repeatable == false");
                }
                return false;
            }
        }
        if (this.blocked && !chr.isGm()) {
            if (chr.isAdmin()) {
                chr.dropMessage(6, "[Quest Start] canStart - blocked");
            }
            return false;
        }
        for (MapleQuestRequirement r : this.startReqs) {
            if (r.check(chr, npcid)) continue;
            if (chr.isAdmin()) {
                chr.dropMessage(6, "[Quest Start] canStart - check false");
            }
            return false;
        }
        return true;
    }

    public boolean canComplete(MapleCharacter chr, Integer npcid) {
        if (chr.getQuest(this).getStatus() != 1) {
            return false;
        }
        if (this.blocked && !chr.isGm()) {
            return false;
        }
        if (this.autoComplete && npcid != null && this.viewMedalItem <= 0) {
            return true;
        }
        for (MapleQuestRequirement r : this.completeReqs) {
            if (r.check(chr, npcid)) continue;
            return false;
        }
        return true;
    }

    public void restoreLostItem(MapleCharacter chr, int itemid) {
        if (this.blocked && !chr.isGm()) {
            return;
        }
        for (MapleQuestAction action : this.startActs) {
            if (action.restoreLostItem(chr, itemid)) break;
        }
    }

    public void start(MapleCharacter chr, int npc) {
        this.start(chr, npc, false);
    }

    public void start(MapleCharacter chr, int npc, boolean isWorldShare) {
        if (chr.isDebug()) {
            chr.dropMessage(6, "[開始任務] " + String.valueOf(this) + " NPC: " + npc + " autoStart：" + this.autoStart + " NPC exist: " + MapleQuest.checkNPCOnMap(chr, npc) + " canStart: " + this.canStart(chr, npc));
        }
        if (this.autoStart || MapleQuest.checkNPCOnMap(chr, npc)) {
            for (MapleQuestAction a : this.startActs) {
                if (a.checkEnd(chr, null)) continue;
                if (chr.isDebug()) {
                    chr.dropMessage(6, "開始任務 checkEnd 錯誤...");
                }
                return;
            }
            if (this.getMedalItem() > 0 && ItemConstants.類型.勳章(this.getMedalItem()) && chr.haveItem(this.getMedalItem()) && chr.getQuestStatus(this.getId()) != 2) {
                this.forceComplete(chr, npc);
                return;
            }
            if (!this.startscript.isEmpty()) {
                chr.getScriptManager().startQuestSScript(npc, this.id);
            } else {
                this.forceStart(chr, npc, null, isWorldShare);
            }
        }
    }

    public void complete(MapleCharacter chr, int npc) {
        this.complete(chr, npc, false);
    }

    public void complete(MapleCharacter chr, int npc, boolean isWorldShare) {
        this.complete(chr, npc, null, isWorldShare);
    }

    public void complete(MapleCharacter chr, int npc, Integer selection) {
        this.complete(chr, npc, selection, false);
    }

    public void complete(MapleCharacter chr, int npc, Integer selection, boolean isWorldShare) {
        if (chr.getMap() != null && (this.autoComplete || this.autoPreComplete || MapleQuest.checkNPCOnMap(chr, npc)) && this.canComplete(chr, npc)) {
            for (MapleQuestAction action : this.completeActs) {
                if (action.checkEnd(chr, selection)) continue;
                return;
            }
            this.forceComplete(chr, npc, selection, isWorldShare);
        }
    }

    public void reset(MapleCharacter chr) {
        MapleQuestStatus status = chr.getQuest(this);
        status.setStatus((byte)0);
        chr.updateQuest(status);
    }

    public void forfeit(MapleCharacter chr) {
        if (chr.getQuest(this).getStatus() != 1) {
            return;
        }
        MapleQuestStatus oldStatus = chr.getQuest(this);
        MapleQuestStatus newStatus = new MapleQuestStatus(this, 0);
        newStatus.setForfeited(oldStatus.getForfeited() + 1);
        newStatus.setCompletionTime(oldStatus.getCompletionTime());
        newStatus.setFromChrID(oldStatus.getFromChrID());
        chr.updateQuest(newStatus);
    }

    public void forceStart(MapleCharacter chr, int npc, String customData) {
        this.forceStart(chr, npc, customData, false);
    }

    public void forceStart(MapleCharacter chr, int npc, String customData, boolean isWorldShare) {
        if (chr.isDebug()) {
            chr.dropSpouseMessage(UserChatMessageType.青, "[Start] 開始任務 任務ID： " + this.getId() + " 任務Npc: " + npc);
        }
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 1, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompletionTime(chr.getQuest(this).getCompletionTime());
        newStatus.setCustomData(customData);
        if (isWorldShare) {
            newStatus.setFromChrID(chr.getId());
        }
        chr.updateQuest(newStatus);
        for (MapleQuestAction action : this.startActs) {
            action.runStart(chr, null);
        }
    }

    public void forceComplete(MapleCharacter chr, int npc) {
        this.forceComplete(chr, npc, false);
    }

    public void forceComplete(MapleCharacter chr, int npc, boolean isWorldShare) {
        this.forceComplete(chr, npc, null, isWorldShare);
    }

    public void forceComplete(MapleCharacter chr, int npc, Integer selection, boolean isWorldShare) {
        if (chr.isDebug() && chr != null) {
            chr.dropSpouseMessage(UserChatMessageType.青, "[任務完成] " + String.valueOf(this) + " Npc: " + npc + " selection:" + selection);
        }
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte)2, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        if (isWorldShare) {
            newStatus.setFromChrID(chr.getId());
        }
        chr.updateQuest(newStatus);
        for (MapleQuestAction action : this.completeActs) {
            action.runEnd(chr, selection);
        }
        chr.send(EffectPacket.showSpecialEffect(EffectOpcode.UserEffect_QuestComplete));
        chr.getMap().broadcastMessage(chr, EffectPacket.showForeignEffect(chr.getId(), EffectOpcode.UserEffect_QuestComplete), false);
    }

    public int getId() {
        return this.id;
    }

    public Map<Integer, Integer> getRelevantMobs() {
        return this.relevantMobs;
    }

    private static boolean checkNPCOnMap(MapleCharacter player, int npcId) {
        return JobConstants.is龍魔導士(player.getJob()) && npcId == 1013000 || JobConstants.is惡魔殺手(player.getJob()) && npcId == 0 || JobConstants.is精靈遊俠(player.getJob()) && npcId == 0 || npcId == 2159421 || npcId == 3000018 || npcId == 9010000 || npcId >= 2161000 && npcId <= 2161011 || npcId == 9000040 || npcId == 9000066 || npcId == 2010010 || npcId == 1032204 || npcId == 0 || player.getMap() != null && player.getMap().containsNPC(npcId);
    }

    public int getMedalItem() {
        return this.viewMedalItem;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public int getAmountofItems(int itemId) {
        return this.questItems.get(itemId) != null ? this.questItems.get(itemId) : 0;
    }

    public String getStartScript() {
        return this.startscript;
    }

    public String getEndScript() {
        return this.endscript;
    }

    public boolean isSelfStart() {
        return this.selfStart;
    }

    public List<MapleQuestRequirement> getStartReqs() {
        return this.startReqs;
    }

    public List<MapleQuestRequirement> getCompleteReqs() {
        return this.completeReqs;
    }

    public List<MapleQuestAction> getStartActs() {
        return this.startActs;
    }

    public Map<String, List<Pair<String, Pair<String, Integer>>>> getPartyQuestInfo() {
        return this.partyQuestInfo;
    }

    public Map<Integer, Integer> getQuestItems() {
        return this.questItems;
    }

    public boolean isAutoStart() {
        return this.autoStart;
    }

    public boolean isAutoPreComplete() {
        return this.autoPreComplete;
    }

    public boolean isRepeatable() {
        return this.repeatable;
    }

    public boolean isAutoAccept() {
        return this.autoAccept;
    }

    public boolean isAutoComplete() {
        return this.autoComplete;
    }

    public int getViewMedalItem() {
        return this.viewMedalItem;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public void setAutoPreComplete(boolean autoPreComplete) {
        this.autoPreComplete = autoPreComplete;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setAutoAccept(boolean autoAccept) {
        this.autoAccept = autoAccept;
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    public void setSelfStart(boolean selfStart) {
        this.selfStart = selfStart;
    }

    public void setViewMedalItem(int viewMedalItem) {
        this.viewMedalItem = viewMedalItem;
    }

    public void setSelectedSkillID(int selectedSkillID) {
        this.selectedSkillID = selectedSkillID;
    }

    public String toString() {
        return this.getName() + "(" + this.getId() + ")";
    }
}

