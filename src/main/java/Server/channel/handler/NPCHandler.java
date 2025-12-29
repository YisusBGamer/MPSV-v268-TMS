/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.RockPaperScissors
 *  Server.world.WorldBroadcastService
 */
package Server.channel.handler;

import Client.MapleAntiMacro;
import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleQuestStatus;
import Client.RockPaperScissors;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.MapleInventoryType;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.enums.NpcMessageType;
import Config.constants.enums.QuestRequestType;
import Config.constants.enums.UserChatMessageType;
import Database.DatabaseLoader;
import Net.server.AutobanManager;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.MapleTrunk;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleNPC;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleQuickMove;
import Net.server.movement.LifeMovementFragment;
import Net.server.quest.MapleQuest;
import Net.server.shop.MapleShop;
import Net.server.shop.MapleShopFactory;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import Packet.EffectPacket;
import Packet.MaplePacketCreator;
import Packet.NPCPacket;
import Packet.PacketHelper;
import Plugin.provider.loaders.StringData;
import Plugin.script.ScriptManager;
import Server.channel.handler.MovementParse;
import Server.world.WorldBroadcastService;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class NPCHandler {
    private static final Logger log = LoggerFactory.getLogger(NPCHandler.class);

    public static void NPCAnimation(MaplePacketReader slea, MapleClient c) {
        int npcOid;
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        if (c.getPlayer().getLastChangeMapTime() > 0L && System.currentTimeMillis() - c.getPlayer().getLastChangeMapTime() < 1000L) {
            return;
        }
        MapleMap map = c.getPlayer().getMap();
        MapleNPC npc = map.getNPCByOid(npcOid = slea.readInt());
        if (npc == null) {
            return;
        }
        byte type1 = slea.readByte();
        byte type2 = slea.readByte();
        int n1 = slea.readInt();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FieldNpcAction.getValue());
        mplew.writeInt(npcOid);
        mplew.write(type1);
        mplew.write(type2);
        mplew.writeInt(n1);
        if (npc.isMove() && slea.available() >= 17L) {
            int gatherDuration = slea.readInt();
            int nVal1 = slea.readInt();
            Point mPos = slea.readPos();
            Point oPos = slea.readPos();
            List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 10);
            MovementParse.updatePosition(res, npc, 0);
            PacketHelper.serializeMovementList(mplew, gatherDuration, nVal1, mPos, oPos, res, null);
        }
        map.objectMove(-1, npc, mplew.getPacket());
    }

    public static void NPCShop(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        MapleShop shop;
        if (chr == null || chr.getMap() == null) {
            return;
        }
        if (chr.getConversation() != 3 || (shop = chr.getShop()) == null) {
            c.sendEnableActions();
            return;
        }
        byte bmode = slea.readByte();
        switch (bmode) {
            case 0: {
                short position = slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                slea.skip(10);
                shop.buy(c, itemId, quantity, position);
                break;
            }
            case 1: {
                short slot = slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                shop.sell(c, ItemConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case 2: {
                short slot = slea.readShort();
                shop.recharge(c, slot);
                break;
            }
            case 3: {
                chr.setConversation(0);
                chr.setShop(null);
                break;
            }
            default: {
                c.sendEnableActions();
            }
        }
    }

    public static void NPCTalk(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || !c.canClickNPC()) {
            c.sendEnableActions();
            return;
        }
        int npcID = slea.readInt();
        MapleNPC npc = c.getPlayer().getMap().getNPCByOid(npcID);
        if (npc == null) {
            return;
        }
        if (chr.hasBlockedInventory()) {
            chr.dropMessage(5, "已經與NPC對話當中,可以使用@ea解除卡對話狀態。");
            c.sendEnableActions();
            return;
        }
        if (MapleAntiMacro.isAntiNow(chr.getName())) {
            chr.dropMessage(5, "被使用測謊機時無法操作。");
            c.sendEnableActions();
            return;
        }
        chr.setCurrenttime(System.currentTimeMillis());
        if (chr.getCurrenttime() - chr.getLasttime() < chr.getDeadtime()) {
            if (chr.isGm()) {
                chr.dropMessage(5, "對話或按鈕過快。");
            }
            c.sendEnableActions();
            return;
        }
        chr.setLasttime(System.currentTimeMillis());
        c.getPlayer().updateTick(slea.readInt());
        MapleShop shop = MapleShopFactory.getInstance().getShopForNPC(npc.getId());
        if (shop != null) {
            shop.sendShop(c);
        } else if (MapleLifeFactory.isNpcTrunk(npc.getId())) {
            Object npcScriptInfo = StringData.getNpcStringById(npc.getId());
            if ("MISSINGNO".equalsIgnoreCase((String)npcScriptInfo)) {
                npcScriptInfo = "";
            }
            npcScriptInfo = ((String)npcScriptInfo).isEmpty() ? String.valueOf(npc.getId()) : (String)npcScriptInfo + "(" + npc.getId() + ")";
            if (chr.isAdmin()) {
                chr.dropSpouseMessage(UserChatMessageType.青, "[" + (String)npcScriptInfo + "] 使用倉庫。");
            }
            chr.setConversation(6);
            chr.getTrunk().secondPwdRequest(c, npc.getId());
        } else if (chr.getScriptManager().startNpcScript(npc.getId(), npcID, null) != null) {
            chr.getScriptManager().startScript(npcID, MapleLifeFactory.getNpcScriptName(npcID), null);
        } else if (MapleLifeFactory.getNpcScriptName(npcID) != null) {
            chr.getScriptManager().startScript(npcID, "npcs/" + MapleLifeFactory.getNpcScriptName(npcID), null);
        }
    }

    public static void QuestAction(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.hasBlockedInventory()) {
            return;
        }
        byte action = slea.readByte();
        int questId = slea.readInt();
        if (MapleAntiMacro.isAntiNow(chr.getName())) {
            chr.dropMessage(5, "被使用測謊機時無法操作。");
            c.sendEnableActions();
            return;
        }
        QuestRequestType op = QuestRequestType.getQTFromByte(action);
        if (op == null) {
            if (chr.isDebug()) {
                chr.dropMessage(5, "Unknown QuestRequestType: " + action);
            }
            return;
        }
        MapleQuest quest = MapleQuest.getInstance(questId);
        switch (op) {
            case QuestReq_LostItem: {
                slea.readInt();
                quest.restoreLostItem(chr, slea.readInt());
                break;
            }
            case QuestReq_AcceptQuest: {
                int npc = slea.readInt();
                if (!quest.getStartScript().isEmpty()) break;
                quest.start(chr, npc);
                break;
            }
            case QuestReq_CompleteQuest: {
                int npc = slea.readInt();
                int selection = slea.readInt();
                if (!quest.getEndScript().isEmpty()) {
                    return;
                }
                if (slea.available() >= 4L) {
                    quest.complete(chr, npc, slea.readInt());
                    break;
                }
                if (selection >= 0) {
                    quest.complete(chr, npc, selection);
                    break;
                }
                quest.complete(chr, npc);
                break;
            }
            case QuestReq_ResignQuest: {
                if (GameConstants.canForfeit(quest.getId())) {
                    quest.forfeit(chr);
                    if (!chr.isDebug()) break;
                    chr.dropMessage(6, "[任務系統] 放棄系統任務 " + String.valueOf(quest));
                    break;
                }
                chr.dropMessage(1, "無法放棄這個任務.");
                break;
            }
            case QuestReq_OpeningScript: {
                chr.getScriptManager().startQuestSScript(slea.readInt(), questId);
                break;
            }
            case QuestReq_CompleteScript: {
                chr.getScriptManager().startQuestEScript(slea.readInt(), questId);
                break;
            }
            case QuestReq_LaterStep: {
                if (chr.getQuestStatus(questId) != 2) break;
                chr.send(EffectPacket.showSpecialEffect(EffectOpcode.UserEffect_QuestComplete));
                chr.getMap().broadcastMessage(chr, EffectPacket.showForeignEffect(chr.getId(), EffectOpcode.UserEffect_QuestComplete), false);
            }
        }
    }

    public static void Storage(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getTrunk() == null) {
            return;
        }
        if (MapleAntiMacro.isAntiNow(chr.getName())) {
            chr.dropMessage(5, "被使用測謊機時無法操作。");
            c.sendEnableActions();
            return;
        }
        if (chr.getConversation() != 6) {
            c.sendEnableActions();
            return;
        }
        byte mode = slea.readByte();
        MapleTrunk storage = chr.getTrunk();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (storage == null || !storage.isPwdChecked() && mode != 3) {
            chr.setConversation(0);
            c.sendEnableActions();
            return;
        }
        switch (mode) {
            case 3: {
                String secondPwd = slea.readMapleAsciiString();
                if (c.CheckSecondPassword(secondPwd)) {
                    storage.setPwdChecked(true);
                    storage.sendStorage(c);
                    break;
                }
                storage.secondPwdRequest(c, -1);
                break;
            }
            case 4: {
                byte type = slea.readByte();
                short slot = slea.readByte();
                slot = storage.getSlot(MapleInventoryType.getByType(type), slot);
                Item item = storage.getItem(slot);
                if (item != null) {
                    int meso;
                    if (ii.isPickupRestricted(item.getItemId()) && chr.getItemQuantity(item.getItemId(), true) > 0) {
                        c.announce(NPCPacket.getStorageError((byte)12));
                        return;
                    }
                    int n = meso = storage.getNpcId() == 9030100 || storage.getNpcId() == 9031016 ? 1000 : 0;
                    if (chr.getMeso() < (long)meso) {
                        c.announce(NPCPacket.getStorageError((byte)11));
                        return;
                    }
                    if (MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        int accShareItem;
                        item = storage.takeOut(slot);
                        int flag = item.getAttribute();
                        if ((flag & (accShareItem = ItemAttribute.AccountSharable.getValue() | ItemAttribute.CutUsed.getValue())) != accShareItem) {
                            flag &= ~ItemAttribute.TradeOnce.getValue();
                            flag &= ~ItemAttribute.CutUsed.getValue();
                            flag &= ~ItemAttribute.AccountSharable.getValue();
                        }
                        if (ii.isSharableOnce(item.getItemId())) {
                            flag |= ItemAttribute.TradeBlock.getValue();
                        }
                        item.setAttribute(flag);
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                        if (meso > 0) {
                            chr.gainMeso(-meso, false);
                        }
                        storage.sendTakenOut(c, ItemConstants.getInventoryType(item.getItemId()));
                        break;
                    }
                    c.announce(NPCPacket.getStorageError((byte)10));
                    break;
                }
                log.info("[作弊] " + chr.getName() + " (等級 " + chr.getLevel() + ") 試圖從倉庫取出不存在的道具.");
                WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM消息] 玩家: " + chr.getName() + " (等級 " + chr.getLevel() + ") 試圖從倉庫取出不存在的道具."));
                c.sendEnableActions();
                break;
            }
            case 5: {
                int meso;
                short slot = slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                if (quantity < 1) {
                    AutobanManager.getInstance().autoban(c, "試圖存入到倉庫的道具數量: " + quantity + " 道具ID: " + itemId);
                    return;
                }
                if (storage.isFull()) {
                    c.announce(NPCPacket.getStorageError((byte)17));
                    return;
                }
                MapleInventoryType type = ItemConstants.getInventoryType(itemId);
                if (chr.getInventory(type).getItem(slot) == null) {
                    c.sendEnableActions();
                    return;
                }
                int n = meso = storage.getNpcId() == 9030100 || storage.getNpcId() == 9031016 ? 500 : 100;
                if (chr.getMeso() < (long)meso) {
                    c.announce(NPCPacket.getStorageError((byte)16));
                    return;
                }
                Item item = chr.getInventory(type).getItem(slot).copy();
                int flag = item.getCAttribute();
                if (ItemConstants.類型.寵物(item.getItemId())) {
                    c.announce(NPCPacket.getStorageError((byte)18));
                    return;
                }
                if (ItemAttribute.Seal.check(flag)) {
                    c.sendEnableActions();
                    return;
                }
                if (!(!ItemAttribute.TradeBlock.check(flag) && !ii.isTradeBlock(item.getItemId()) || ItemAttribute.AccountSharable.check(flag) || ItemAttribute.TradeOnce.check(flag) || ItemAttribute.CutUsed.check(flag))) {
                    c.sendEnableActions();
                    return;
                }
                if (ii.isPickupRestricted(item.getItemId()) && storage.findById(item.getItemId()) != null) {
                    c.sendEnableActions();
                    return;
                }
                if (item.getItemId() == itemId && (item.getQuantity() >= quantity || ItemConstants.類型.可充值道具(itemId))) {
                    if (ItemConstants.類型.可充值道具(itemId)) {
                        quantity = item.getQuantity();
                    }
                    MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                    chr.gainMeso(-meso, false, false);
                    item.setQuantity(quantity);
                    storage.store(item);
                    storage.sendStored(c, ItemConstants.getInventoryType(itemId));
                    break;
                }
                AutobanManager.getInstance().addPoints(c, 1000, 0L, "試圖存入到倉庫的道具: " + itemId + " 數量: " + quantity + " 當前玩家用道具: " + item.getItemId() + " 數量: " + item.getQuantity());
                break;
            }
            case 6: {
                storage.arrange();
                storage.update(c);
                break;
            }
            case 7: {
                DatabaseLoader.DatabaseConnection.domain(con -> {
                    long meso = slea.readLong();
                    long storageMesos = storage.getMesoForUpdate(con);
                    long playerMesos = chr.getMeso();
                    if (meso > 0L && storageMesos >= meso || meso < 0L && playerMesos >= -meso) {
                        if (meso < 0L && storageMesos - meso < 0L) {
                            meso = -(ServerConfig.CHANNEL_PLAYER_MAXMESO - storageMesos);
                            if (-meso > playerMesos) {
                                return null;
                            }
                        } else if (meso > 0L && playerMesos + meso < 0L) {
                            meso = ServerConfig.CHANNEL_PLAYER_MAXMESO - playerMesos;
                            if (meso > storageMesos) {
                                return null;
                            }
                        } else {
                            if (meso + playerMesos > ServerConfig.CHANNEL_PLAYER_MAXMESO) {
                                chr.dropMessage(1, "楓幣將達到系統上限，不能取出。");
                                c.announce(NPCPacket.getStorageError((byte)25));
                                return null;
                            }
                            storage.setMeso(con, storageMesos - meso);
                            chr.gainMeso(meso, false, false);
                        }
                    } else {
                        AutobanManager.getInstance().addPoints(c, 1000, 0L, "Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")");
                    }
                    return null;
                });
                storage.sendMeso(c);
                break;
            }
            case 8: {
                storage.close();
                chr.setConversation(0);
                break;
            }
            default: {
                System.out.println("未處理的倉庫操作，模式: " + mode);
            }
        }
    }

    public static void userScriptMessageAnswer(MaplePacketReader slea, MapleClient c) {
        int action;
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        if (player.getConversation() != 1) {
            return;
        }
        ScriptManager sm = player.getScriptManager();
        int npcid = slea.readInt();
        byte lastType = slea.readByte();
        NpcMessageType nmt = NpcMessageType.getByVal(lastType);
        if (nmt == null) {
            c.getPlayer().dropMessage(1, "Unknown NpcMessageType:" + lastType);
            return;
        }
        int selection = -1;
        long answer = 0L;
        String ans = null;
        switch (nmt) {
            case Say: {
                slea.readInt();
                slea.readMapleAsciiString();
                action = slea.readByte();
                break;
            }
            case AskAccept: 
            case AskYesNo: {
                action = slea.readByte();
                selection = action;
                break;
            }
            case AskMenu: 
            case AskSlideMenu: {
                int n = action = slea.readByte() > 0 ? 1 : -1;
                if (action <= 0) break;
                answer = Math.max(slea.readInt(), 0);
                selection = (int)answer;
                break;
            }
            case AskText: 
            case AskBoxtext: 
            case OnAskNumberUseKeyPad: {
                String returnText;
                int n = action = slea.readByte() > 0 ? 1 : -1;
                if (action <= 0) break;
                ans = returnText = slea.readMapleAsciiString();
                break;
            }
            case AskNumber: {
                int n = action = slea.readByte() > 0 ? 1 : -1;
                if (action <= 0) break;
                answer = Math.max(slea.readLong(), 0L);
                selection = (int)answer;
                break;
            }
            case AskAvatar: {
                int n = action = slea.readByte() > 0 ? 1 : -1;
                if (action <= 0) break;
                slea.readByte();
                slea.readByte();
                answer = slea.readByte();
                selection = (int)answer;
                break;
            }
            case AskAndroid: {
                int n = action = slea.readByte() > 0 ? 1 : -1;
                if (action <= 0) break;
                answer = slea.readByte();
                selection = (int)answer;
                break;
            }
            case AskPet: 
            case AskPetAll: {
                int n = action = slea.readByte() > 0 ? 1 : -1;
                if (action <= 0) break;
                answer = slea.readLong();
                break;
            }
            case AskAngelicBuster: {
                action = slea.readByte();
                answer = action;
                selection = (int)answer;
                break;
            }
            case AskAvatarZero: {
                slea.readByte();
                int n = action = slea.readByte() > 0 ? 1 : -1;
                if (action <= 0) break;
                answer = slea.readByte();
                selection = (int)answer;
                break;
            }
            case AskAvatarMixColor: {
                action = 1;
                break;
            }
            case AskConfirmAvatarChange: 
            case AskAvatarRandomMixColor: {
                action = 1;
                answer = slea.readByte();
                break;
            }
            case Monologue: 
            case OnAskScreenShinningStarMsg: {
                action = 1;
                break;
            }
            case PlayMovieClip: {
                answer = slea.readByte();
                action = answer == -1L ? -1 : 1;
                selection = (int)answer;
                break;
            }
            case AskSelectMenu: {
                action = slea.readByte();
                answer = slea.readByte();
                selection = (int)answer;
                break;
            }
            case AskIngameDirection: {
                slea.readByte();
                action = slea.readByte();
                break;
            }
            default: {
                action = slea.readByte();
            }
        }
        if (sm.isActive(sm.getLastActiveScriptType())) {
            sm.handleAction(sm.getLastActiveScriptType(), nmt, action, answer, ans);
            return;
        }
        if (action == -1) {
            sm.dispose();
            return;
        }
        switch (sm.getLastActiveScriptType()) {
            case Npc: 
            case Item: 
            case onUserEnter: 
            case onFirstUserEnter: 
            case Command: {
                break;
            }
            case QuestStart: {
                break;
            }
        }
    }

    public static void repairAll(MapleClient c) {
        MapleInventoryType[] types;
        int price = 0;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        HashMap<Equip, Integer> eqs = new HashMap<Equip, Integer>();
        for (MapleInventoryType type : types = new MapleInventoryType[]{MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED}) {
            for (Item item : c.getPlayer().getInventory(type).newList()) {
                Map<String, Integer> eqStats;
                Equip eq;
                if (!(item instanceof Equip) || (eq = (Equip)item).getDurability() < 0 || !(eqStats = ii.getItemBaseInfo(eq.getItemId())).containsKey("durability") || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) continue;
                double rPercentage = 100.0 - Math.ceil((double)eq.getDurability() * 1000.0 / ((double)eqStats.get("durability").intValue() * 10.0));
                eqs.put(eq, eqStats.get("durability"));
                price += (int)Math.ceil(rPercentage * ii.getUnitPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
            }
        }
        if (eqs.size() <= 0 || c.getPlayer().getMeso() < (long)price) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        for (Map.Entry eqqz : eqs.entrySet()) {
            Equip ez = (Equip)eqqz.getKey();
            ez.setDurability((Integer)eqqz.getValue());
            c.getPlayer().forceUpdateItem(ez.copy());
        }
    }

    public static void repair(MaplePacketReader slea, MapleClient c) {
        if (slea.available() < 4L) {
            return;
        }
        int position = slea.readInt();
        MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        Item item = c.getPlayer().getInventory(type).getItem((byte)position);
        if (item == null) {
            return;
        }
        Equip eq = (Equip)item;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<String, Integer> eqStats = ii.getItemBaseInfo(item.getItemId());
        if (eq.getDurability() < 0 || !eqStats.containsKey("durability") || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        double rPercentage = 100.0 - Math.ceil((double)eq.getDurability() * 1000.0 / ((double)eqStats.get("durability").intValue() * 10.0));
        int price = (int)Math.ceil(rPercentage * ii.getUnitPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
        if (c.getPlayer().getMeso() < (long)price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        c.getPlayer().forceUpdateItem(eq.copy());
    }

    public static void UpdateQuest(MaplePacketReader slea, MapleClient c) {
        MapleQuest quest = MapleQuest.getInstance(slea.readShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

    public static void UseItemQuest(MaplePacketReader slea, MapleClient c) {
        short slot = slea.readShort();
        int itemId = slea.readInt();
        Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
        int qid = slea.readInt();
        MapleQuest quest = MapleQuest.getInstance(qid);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, Map<Integer, Integer>> questItemInfo = null;
        boolean found = false;
        for (Item i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 != 422 || (questItemInfo = ii.questItemInfo(i.getItemId())) == null || questItemInfo.getLeft() != qid || questItemInfo.getRight() == null || !questItemInfo.getRight().containsKey(itemId)) continue;
            found = true;
            break;
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            int newData = slea.readInt();
            MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == 1) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, questItemInfo.getRight().get(item.getItemId()).shortValue(), false);
            }
        }
    }

    public static void RPSGame(MaplePacketReader slea, MapleClient c) {
        if (slea.available() == 0L || c.getPlayer() == null || c.getPlayer().getMap() == null || !c.getPlayer().getMap().containsNPC(9000019)) {
            if (c.getPlayer() != null && c.getPlayer().getRPS() != null) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        byte mode = slea.readByte();
        switch (mode) {
            case 0: 
            case 5: {
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000L) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                    break;
                }
                c.announce(MaplePacketCreator.getRPSMode((byte)8, -1, -1, -1));
                break;
            }
            case 1: {
                if (c.getPlayer().getRPS() != null && c.getPlayer().getRPS().answer(c, (int)slea.readByte())) break;
                c.announce(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                break;
            }
            case 2: {
                if (c.getPlayer().getRPS() != null && c.getPlayer().getRPS().timeOut(c)) break;
                c.announce(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                break;
            }
            case 3: {
                if (c.getPlayer().getRPS() != null && c.getPlayer().getRPS().nextRound(c)) break;
                c.announce(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
                break;
            }
            case 4: {
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                    break;
                }
                c.announce(MaplePacketCreator.getRPSMode((byte)13, -1, -1, -1));
            }
        }
    }

    public static void OpenQuickMoveNpc(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int npcid = slea.readInt();
        if (c == null || chr == null || chr.hasBlockedInventory() || chr.isInBlockedMap() || chr.inEvent() || 875999999 == chr.getMapId() || npcid == 0) {
            chr.dropMessage(5, "觸發定身需要解卡請輸入@EA");
            return;
        }
        for (MapleQuickMove mqm : chr.getMap().QUICK_MOVE) {
            if (mqm.TESTPIA && !ServerConfig.TESPIA || chr.getGmLevel() < mqm.GM_LEVEL || mqm.NPC != npcid) continue;
            if (chr.getLevel() < mqm.MIN_LEVEL) {
                chr.dropMessage(-1, "未達到可使用等級。");
                return;
            }
            chr.getScriptManager().startNpcScript(mqm.NPC, 0, mqm.SCRIPT);
            break;
        }
    }

    public static void OpenQuickMoveNpcScript(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int selection = slea.readInt();
        int qmSize = 0;
        if (chr.getMap().QUICK_MOVE != null) {
            for (MapleQuickMove qm : chr.getMap().QUICK_MOVE) {
                if (qm.TESTPIA && !ServerConfig.TESPIA || chr.getGmLevel() < qm.GM_LEVEL) continue;
                ++qmSize;
            }
        }
        if (c == null || chr == null || chr.hasBlockedInventory() || chr.isInBlockedMap() || chr.inEvent() || 875999999 == chr.getMapId() || qmSize < selection + 1) {
            chr.dropMessage(5, "觸發定身需要解卡請輸入@EA");
            return;
        }
        MapleQuickMove mqm = chr.getMap().QUICK_MOVE.get(selection);
        if (mqm.TESTPIA && !ServerConfig.TESPIA || chr.getGmLevel() < mqm.GM_LEVEL) {
            return;
        }
        if (chr.getLevel() < mqm.MIN_LEVEL) {
            chr.dropMessage(-1, "未達到可使用等級。");
            return;
        }
        if (mqm.NPC == 0 && (mqm.SCRIPT == null || mqm.SCRIPT.isEmpty())) {
            chr.dropMessage(-1, "這個選項無法使用，請回報給管理員。");
            return;
        }
        chr.getScriptManager().startNpcScript(mqm.NPC, 0, mqm.SCRIPT);
    }

    public static void ExitGaintBoss(MapleClient c, MapleCharacter player) {
        player.getScriptManager().startNpcScript(9390124, 0, null);
    }
}

