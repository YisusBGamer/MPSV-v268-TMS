/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Config.constants.enums.InGameDirectionEventType
 *  Database.DatabaseConnectionEx
 *  Net.server.commands.GMCommand$PacketUI
 *  Net.server.quest.MapleQuestRequirement
 *  Net.server.quest.MapleQuestRequirementType
 *  Packet.CField
 *  Server.world.WorldAllianceService
 *  Server.world.WorldBroadcastService
 *  Server.world.WorldGuildService
 *  SwordieX.client.party.Party
 *  SwordieX.client.party.PartyMember
 *  SwordieX.client.party.PartyResult
 *  SwordieX.field.ClockPacket
 *  SwordieX.field.fieldeffect.FieldEffect
 *  SwordieX.overseas.extraequip.ExtraEquipResult
 *  SwordieX.util.Position
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  connection.packet.FieldPacket
 *  connection.packet.InGameDirectionEvent
 *  connection.packet.OverseasPacket
 *  connection.packet.UserLocal
 *  connection.packet.WvsContext
 *  lombok.Generated
 */
package Plugin.script.binding;

import Client.MapleAntiMacro;
import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.MapleReward;
import Client.MapleStat;
import Client.MapleTraitType;
import Client.MonsterFamiliar;
import Client.SecondaryStat;
import Client.hexa.MapleHexaSkill;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.MapleInventory;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.stat.MapleHyperStats;
import Config.configs.Config;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.enums.InGameDirectionEventType;
import Config.constants.enums.UserChatMessageType;
import Database.DatabaseConnectionEx;
import Database.DatabaseLoader;
import Database.tools.SqlTool;
import Handler.warpToGameHandler;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.MaplePortal;
import Net.server.buffs.MapleStatEffect;
import Net.server.commands.GMCommand;
import Net.server.factory.MobCollectionFactory;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterInformationProvider;
import Net.server.life.MapleNPC;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.field.ActionBarField;
import Net.server.quest.MapleQuest;
import Net.server.quest.MapleQuestRequirement;
import Net.server.quest.MapleQuestRequirementType;
import Net.server.shop.MapleShopFactory;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import Packet.CField;
import Packet.EffectPacket;
import Packet.InventoryPacket;
import Packet.MTSCSPacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import Packet.NPCPacket;
import Packet.UIPacket;
import Plugin.provider.loaders.StringData;
import Plugin.script.binding.PlayerScriptInteraction;
import Plugin.script.binding.ScriptEvent;
import Plugin.script.binding.ScriptField;
import Plugin.script.binding.ScriptItem;
import Server.channel.ChannelServer;
import Server.channel.handler.InterServerHandler;
import Server.world.WorldAllianceService;
import Server.world.WorldBroadcastService;
import Server.world.WorldGuildService;
import Server.world.guild.MapleGuild;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import SwordieX.client.party.PartyResult;
import SwordieX.field.ClockPacket;
import SwordieX.field.fieldeffect.FieldEffect;
import SwordieX.overseas.extraequip.ExtraEquipResult;
import SwordieX.util.Position;
import SwordieX.world.World;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import connection.packet.FieldPacket;
import connection.packet.InGameDirectionEvent;
import connection.packet.OverseasPacket;
import connection.packet.UserLocal;
import connection.packet.WvsContext;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

public class ScriptPlayer
extends PlayerScriptInteraction {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptPlayer.class);
    private int level;
    private boolean changed_wishlist;
    private boolean changed_trocklocations;
    private boolean changed_skillmacros;
    private boolean changed_savedlocations;
    private boolean changed_questinfo;
    private boolean changed_worldshareinfo;
    private boolean changed_skills;
    private boolean changed_reports;
    private boolean changed_vcores;
    private boolean changed_innerSkills;
    private boolean changed_keyValue;
    private boolean changed_buylimit;
    private boolean changed_accountbuylimit;
    private boolean changed_soulcollection;
    private boolean changed_mobcollection;
    private boolean changed_familiars;
    private Map<Integer, MonsterFamiliar> familiars;
    private MonsterFamiliar summonedFamiliar;
    private transient MapleMap map;
    private final Map<String, Object> tempValues = new HashMap<String, Object>();

    public ScriptPlayer(MapleCharacter player) {
        super(player);
    }

    public void gainErda(int itemid) {
        if (this.getPlayer() != null) {
            this.getPlayer().gainErda(itemid);
        }
    }

    public void teachskill(int skillid, int level) {
        Skill sklil = SkillFactory.getSkill(skillid);
        if (level > sklil.getMaxLevel()) {
            level = (byte)sklil.getMaxLevel();
        }
        this.getPlayer().changeSingleSkillLevel(sklil, level, sklil.getMasterLevel());
        if (this.getPlayer().isGm()) {
            this.getPlayer().dropMessage(40, "[SKILL] skillID: " + skillid + " 技能添加成功。");
        }
    }

    public void addEdraSoul(int amount) {
        if (this.getPlayer() != null) {
            this.getPlayer().addEdraSoul(amount);
        }
    }

    public void addByItem(Item item) {
        MapleInventoryManipulator.addbyItem(this.getClient(), item);
    }

    public void setFace(int face) {
        this.getPlayer().setFace(face);
        this.getPlayer().updateSingleStat(MapleStat.FACE, face);
    }

    public void setHair(int hair) {
        this.getPlayer().setHair(hair);
        this.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
    }

    public void setSkin(int skin) {
        this.getPlayer().setSkinColor((byte)skin);
        this.getPlayer().updateSingleStat(MapleStat.SKIN, skin);
    }

    public void setAndroidFace(int face) {
        this.getPlayer().getAndroid().setFace(face);
        this.getPlayer().updateAndroidLook();
    }

    public void setAndroidHair(int hair) {
        this.getPlayer().getAndroid().setHair(hair);
        this.getPlayer().updateAndroidLook();
    }

    public void setAndroidSkin(int skin) {
        this.getPlayer().getAndroid().setSkin(skin);
        this.getPlayer().updateAndroidLook();
    }

    private static void inc(Map<SecondaryStat, Integer> map, SecondaryStat stat, int val) {
        map.merge(stat, val, (a, b) -> a + b);
    }

    public int getId() {
        return this.getPlayer().getId();
    }

    public void dissociateClient() {
        this.getClient().disconnect(true, true);
    }

    public void addPopupSay(int npcId, int duration, String msg, String sound) {
        this.getClient().announce(UIPacket.addPopupSay(npcId, duration, msg, sound));
    }

    public void addPopupSay(int npcId, int showTimer, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_AddPopupSay.getValue());
        mplew.writeInt(npcId);
        mplew.writeInt(showTimer);
        mplew.writeMapleAsciiString(msg);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(300);
        mplew.writeInt(1);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        this.getClient().announce(mplew.getPacket());
    }

    public int getItemQuantity(int item) {
        return this.getPlayer().getItemQuantity(item);
    }

    public void setMaxHp(int hp) {
        this.getPlayer().getStat().setInfo(hp, this.getPlayer().getStat().getMaxMp(false), hp, this.getPlayer().getStat().getMp());
        this.getPlayer().updateSingleStat(MapleStat.HP, hp);
        this.getPlayer().updateSingleStat(MapleStat.MAX_HP, hp);
        this.getPlayer().getStat().recalcLocalStats(this.getPlayer());
    }

    public void setMaxMp(int hp) {
        this.getPlayer().getStat().setInfo(this.getPlayer().getStat().getMaxHp(false), hp, this.getPlayer().getStat().getHp(), hp);
        this.getPlayer().updateSingleStat(MapleStat.MP, hp);
        this.getPlayer().updateSingleStat(MapleStat.MAX_MP, hp);
        this.getPlayer().getStat().recalcLocalStats(this.getPlayer());
    }

    public void addPQLog(String key) {
        this.getPlayer().setPQLog(key);
    }

    public void addPQLog(String key, int value, int resetDays) {
        this.getPlayer().setPQLog(key, 0, value, resetDays);
    }

    public int getPQLog(String key) {
        return this.getPlayer().getPQLog(key);
    }

    public void resetPQLog(String key) {
        this.getPlayer().resetPQLog(key);
    }

    public String getQuestRecordEx(int quest) {
        if (!this.getPlayer().getInfoQuest_Map().containsKey(quest) || MapleQuest.getInstance(quest) == null) {
            return null;
        }
        return this.getPlayer().getInfoQuest_Map().get(quest);
    }

    public String getQuestRecord(int quest, String key) {
        return this.getPlayer().getQuestInfo(quest, key);
    }

    public String getQuestEntryData(int quest) {
        return this.getPlayer().getQuest(quest).getCustomData();
    }

    public void updateQuestRecordEx(int questid, String data) {
        this.getPlayer().updateInfoQuest(questid, data);
    }

    public void updateQuestRecordEx(int questid, int data) {
        this.getPlayer().updateInfoQuest(questid, String.valueOf(data));
    }

    public void updateQuestRecord(int questid, String key, String value) {
        this.getPlayer().updateOneQuestInfo(questid, key, value);
    }

    public void updateQuestRecord(int questid, String key, int value) {
        this.getPlayer().updateOneQuestInfo(questid, key, String.valueOf(value));
    }

    public void updateQuestRecord(int questid, int key, String value) {
        this.getPlayer().updateOneQuestInfo(questid, String.valueOf(key), value);
    }

    public void updateQuestRecord(int questid, int key, int value) {
        this.getPlayer().updateOneQuestInfo(questid, String.valueOf(key), String.valueOf(value));
    }

    public void setQuestData(int quest, String data) {
        this.getPlayer().getQuest(quest).setCustomData(data);
    }

    public void cancelItemEffect(int itemId) {
        this.getClient().getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(itemId), false, -1L);
    }

    public void cancelSkillEffect(int skillId) {
        if (this.getPlayer().getSkillEffect(skillId) != null) {
            this.getClient().getPlayer().cancelEffect(this.getPlayer().getSkillEffect(skillId), false, -1L);
        }
    }

    public boolean canGainItem(int itemId, int quantity) {
        return MapleInventoryManipulator.checkSpace(this.getClient(), itemId, quantity, "");
    }

    public void changeMap(int mapId) {
        if (this.getPlayer().getEventInstance() != null && this.getPlayer().getEventInstance().getFields().get(mapId) != null) {
            this.getPlayer().changeMap(this.getPlayer().getEventInstance().getFields().get(mapId).getMap());
            this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
            return;
        }
        this.changeMap(mapId, 0);
        this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
    }

    public void changeMap(int mapId, int portal) {
        if (this.getPlayer().getEventInstance() != null && this.getPlayer().getEventInstance().getFields().get(mapId) != null) {
            this.getPlayer().changeMap(this.getPlayer().getEventInstance().getFields().get(mapId).getMap());
            this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
            return;
        }
        this.getPlayer().changeMap(mapId, portal);
        this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
    }

    public void changeMap(int mapId, String portal) {
        MapleMap map = this.getClient().getChannelServer().getMapFactory().getMap(mapId);
        if (map == null) {
            return;
        }
        MaplePortal portalPos = map.getPortal(portal);
        if (portalPos == null) {
            return;
        }
        if (this.getPlayer().getEventInstance() != null && this.getPlayer().getEventInstance().getFields().get(mapId) != null) {
            this.getPlayer().changeMap(this.getPlayer().getEventInstance().getFields().get(mapId).getMap(), portalPos);
            this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
            return;
        }
        this.getPlayer().changeMap(map, portalPos);
        this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
    }

    public void changeMap(MapleMap map) {
        if (this.getPlayer().getEventInstance() != null && this.getPlayer().getEventInstance().getFields().get(map.getId()) != null) {
            this.getPlayer().changeMap(this.getPlayer().getEventInstance().getFields().get(map.getId()).getMap());
            this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
            return;
        }
        this.getPlayer().changeMap(map);
        this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
    }

    public void changeMap(ScriptField map) {
        if (this.getPlayer().getEventInstance() != null && this.getPlayer().getEventInstance().getFields().get(map.getId()) != null) {
            this.getPlayer().changeMap(this.getPlayer().getEventInstance().getFields().get(map.getId()).getMap());
            this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
            return;
        }
        this.getPlayer().changeMap(map.getMap());
        this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
    }

    public void changeMap(MapleMap map, int x, int y) {
        Point pos = new Point(x, y);
        this.getPlayer().changeMapToPosition(map, pos);
        this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
    }

    public void changeChannelAndMap(int channel, int mapId) {
        this.getPlayer().changeChannel(channel);
        this.getPlayer().changeMap(mapId, 0);
        this.getPlayer().write(warpToGameHandler.EquipRuneSetting());
    }

    public void startQuest(int questId) {
        MapleQuest quest = MapleQuest.getInstance(questId);
        int npc_id = 9330072;
        for (MapleQuestRequirement qr : quest.getStartReqs()) {
            if (qr.getType() != MapleQuestRequirementType.npc) continue;
            npc_id = qr.getIntStore();
            break;
        }
        quest.forceStart(this.getPlayer(), npc_id, "");
    }

    public void startQuest(int questId, int npcId) {
        MapleQuest quest = MapleQuest.getInstance(questId);
        int npc_id = npcId;
        for (MapleQuestRequirement qr : quest.getStartReqs()) {
            if (qr.getType() != MapleQuestRequirementType.npc) continue;
            npc_id = qr.getIntStore();
            break;
        }
        quest.forceStart(this.getPlayer(), npc_id, "");
    }

    public void startQuest(int questId, int npcId, String data) {
        MapleQuest quest = MapleQuest.getInstance(questId);
        int npc_id = npcId;
        for (MapleQuestRequirement qr : quest.getStartReqs()) {
            if (qr.getType() != MapleQuestRequirementType.npc) continue;
            npc_id = qr.getIntStore();
            break;
        }
        quest.forceStart(this.getPlayer(), npc_id, data);
    }

    public void completeQuest(int questId, int npcId) {
        MapleQuest quest = MapleQuest.getInstance(questId);
        int npc_id = npcId;
        for (MapleQuestRequirement qr : quest.getCompleteReqs()) {
            if (qr.getType() != MapleQuestRequirementType.npc) continue;
            npc_id = qr.getIntStore();
            break;
        }
        quest.forceComplete(this.getPlayer(), npc_id, false);
    }

    public void forfeitQuest(int questId) {
        MapleQuest.getInstance(questId).forfeit(this.getPlayer());
    }

    public boolean createAlliance(String allianceName) {
        Party pt = this.getPlayer().getParty();
        MapleCharacter otherChar = this.getClient().getChannelServer().getPlayerStorage().getCharacterById(((PartyMember)pt.getMembers().get(1)).getCharID());
        if (otherChar == null || otherChar.getId() == this.getPlayer().getId()) {
            return false;
        }
        try {
            return WorldAllianceService.getInstance().createAlliance(allianceName, this.getPlayer().getId(), otherChar.getId(), this.getPlayer().getGuildId(), otherChar.getGuildId());
        }
        catch (Exception re) {
            log.error("createAlliance 錯誤", re);
            return false;
        }
    }

    public int getAllianceId() {
        MapleGuild guild = WorldGuildService.getInstance().getGuild(this.getPlayer().getGuildId());
        return guild.getAllianceId();
    }

    public int getAllianceCapacity() {
        MapleGuild guild = WorldGuildService.getInstance().getGuild(this.getPlayer().getGuildId());
        return WorldAllianceService.getInstance().getAlliance(guild.getAllianceId()).getCapacity();
    }

    public String[] getAllianceRank() {
        MapleGuild guild = WorldGuildService.getInstance().getGuild(this.getPlayer().getGuildId());
        return WorldAllianceService.getInstance().getAlliance(guild.getAllianceId()).getRanks();
    }

    public int getGuildId() {
        return this.getPlayer().getGuildId();
    }

    public int getGuildCapacity() {
        return this.getPlayer().getGuild().getCapacity();
    }

    public int getGuildContribution() {
        return this.getPlayer().getGuildContribution();
    }

    public boolean hasGuild() {
        return this.getPlayer().getGuild() != null;
    }

    public int getGuildRank() {
        return this.getPlayer().getGuildRank();
    }

    public int createGuild(String name) {
        Party pt = this.getPlayer().getParty();
        if (pt.getPartyLeaderID() != this.getPlayer().getId()) {
            return -1;
        }
        try {
            return WorldGuildService.getInstance().createGuild(this.getPlayer().getId(), name);
        }
        catch (Exception re) {
            log.error("createGuild 錯誤", re);
            return -1;
        }
    }

    public void disbandGuild() {
        this.getPlayer().getGuild().disbandGuild();
    }

    public void dropAlertNotice(String message) {
        this.getPlayer().dropAlertNotice(message);
    }

    public void dropMessage(int type, String message) {
        this.getPlayer().dropMessage(type, message);
    }

    public int getLevel() {
        return this.getPlayer().getLevel();
    }

    public void setLevel(int newLevel) {
        this.getPlayer().setLevel(newLevel - 1);
        this.getPlayer().levelUp(true);
    }

    public void gainAp(short gain) {
        this.getPlayer().gainAp(gain);
    }

    public void gainBuddySlots(short gain) {
        this.getPlayer().setBuddyCapacity((byte)gain);
    }

    public void gainCloseness(short gain) {
        MaplePet pet = this.getPlayer().getSpawnPet(0);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + gain * ServerConfig.CHANNEL_RATE_TRAIT);
            this.getPlayer().petUpdateStats(pet, true);
        }
    }

    public void gainInventorySlots(byte type, int addSlot) {
        MapleInventory inv = this.getPlayer().getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte)addSlot);
        this.getPlayer().send(InventoryPacket.updateInventorySlotLimit(type, (byte)inv.getSlotLimit()));
    }

    public void gainExp(long gain) {
        this.getPlayer().gainExp(gain, true, true, true);
    }

    public boolean gainItem(int itemId, int quantity) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!ii.itemExists(itemId)) {
            this.getPlayer().dropMessage(5, itemId + " 這個道具不存在.");
            return false;
        }
        if (ItemConstants.getInventoryType(itemId).getType() == 1 || ItemConstants.類型.寵物(itemId) || ItemConstants.類型.寵物裝備(itemId)) {
            MapleInventory equipedIv = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
            for (int i = 0; i < quantity; ++i) {
                Equip equip = ii.getEquipById(itemId);
                MapleInventoryManipulator.addbyItem(this.getClient(), equip);
                equipedIv.addFromDB(equip);
            }
            this.getPlayer().send(MaplePacketCreator.getShowItemGain(itemId, quantity));
        } else {
            MapleInventory equipedIv = this.getPlayer().getInventory(ItemConstants.getInventoryType(itemId, false));
            Item item = new Item(itemId,  (short)0, (short)quantity, 0);
            MapleInventoryManipulator.addbyItem(this.getClient(), item);
            this.getPlayer().send(MaplePacketCreator.getShowItemGain(itemId, quantity));
            equipedIv.addFromDB(item);
        }
        return true;
    }

    public boolean gainItem(int itemId, short quantity, long duration) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!ii.itemExists(itemId)) {
            this.getPlayer().dropMessage(5, itemId + " 這個道具不存在.");
            return false;
        }
        if (ItemConstants.getInventoryType(itemId).getType() == 1 || ItemConstants.類型.寵物(itemId) || ItemConstants.類型.寵物裝備(itemId)) {
            MapleInventory equipedIv = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
            for (int i = 0; i < quantity; ++i) {
                Equip equip = ii.getEquipById(itemId);
                if (duration > 0L) {
                    if (duration < 1000L) {
                        equip.setExpiration(System.currentTimeMillis() + duration * 24L * 60L * 60L * 1000L);
                    } else {
                        equip.setExpiration(System.currentTimeMillis() + duration);
                    }
                }
                MapleInventoryManipulator.addbyItem(this.getClient(), equip);
                equipedIv.addFromDB(equip);
            }
            this.getPlayer().send(MaplePacketCreator.getShowItemGain(itemId, quantity));
        } else {
            MapleInventory equipedIv = this.getPlayer().getInventory(ItemConstants.getInventoryType(itemId).getType());
            Item item = new Item(itemId,  (short)0, (short)quantity, 0);
            if (duration > 0L) {
                if (duration < 1000L) {
                    item.setExpiration(System.currentTimeMillis() + duration * 24L * 60L * 60L * 1000L);
                } else {
                    item.setExpiration(System.currentTimeMillis() + duration);
                }
            }
            MapleInventoryManipulator.addbyItem(this.getClient(), item);
            equipedIv.addFromDB(item);
            this.getPlayer().send(MaplePacketCreator.getShowItemGain(itemId, quantity));
        }
        return true;
    }

    public void updateItem(Item item) {
        this.getPlayer().forceUpdateItem(item);
    }

    public void updateItem(short slot, Item item) {
        this.getPlayer().forceUpdateItem(this.getPlayer().getInventory(ItemConstants.getInventoryType(item.getItemId())).getItem(slot));
    }

    public boolean gainPetItem(int itemId, int day) {
        MaplePet pet;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!ii.itemExists(itemId)) {
            this.getPlayer().dropMessage(5, itemId + " 這個道具不存在.");
            return false;
        }
        short flag = (short)ItemAttribute.Seal.getValue();
        if (ItemConstants.類型.寵物(itemId)) {
            pet = MaplePet.createPet(itemId);
            if (pet != null && day == -1 && (day = ii.getLife(itemId)) < 0) {
                day = 0;
            }
        } else {
            pet = null;
        }
        Item item = new Item(itemId,  (short)0,  (short)1, 0);
        item.setPet(pet);
        if (day > 0) {
            if (day < 1000) {
                item.setExpiration(System.currentTimeMillis() + (long)day * 24L * 60L * 60L * 1000L);
            } else {
                item.setExpiration(System.currentTimeMillis() + (long)day);
            }
        }
        return MapleInventoryManipulator.addbyItem(this.getClient(), item);
    }

    public boolean gainItem(Item item) {
        MapleInventory equipedIv = this.getPlayer().getInventory(ItemConstants.getInventoryType(item.getItemId()).getType());
        boolean result = MapleInventoryManipulator.addbyItem(this.getClient(), item);
        equipedIv.addFromDB(item);
        return result;
    }

    public void gainMeso(long gain) {
        this.getPlayer().gainMeso(gain, true, true);
    }

    public void gainSp(int skillbook, short gain) {
        this.getPlayer().gainSP(gain, skillbook);
    }

    public void gainSp(short gain) {
        this.getPlayer().gainSP(gain);
    }

    public int getAccountId() {
        return this.getPlayer().getAccountID();
    }

    public int getCharacterId() {
        return this.getPlayer().getId();
    }

    public int getAmountOfItem(int itemId) {
        return this.getPlayer().getItemQuantity(itemId, false);
    }

    public int getAmountOfItem(int itemId, boolean checkEquipped) {
        return this.getPlayer().getItemQuantity(itemId, checkEquipped);
    }

    public byte getBuddyCapacity() {
        return this.getPlayer().getBuddyCapacity();
    }

    public int getChannel() {
        return this.getClient().getChannel();
    }

    public short getDex() {
        return this.getPlayer().getStat().getDex();
    }

    public int getFace() {
        return this.getPlayer().getFace();
    }

    public short getFreeSlots(byte type) {
        return this.getPlayer().getInventory(type).getNumFreeSlot();
    }

    public int getGender() {
        return this.getPlayer().getGender();
    }

    @Override
    public ScriptEvent getEvent() {
        return this.getPlayer().getEventInstance();
    }

    @Override
    public ScriptEvent getEvent(String name) {
        if (name.equals(this.getPlayer().getEventInstance().getName())) {
            return this.getPlayer().getEventInstance();
        }
        return null;
    }

    public void setEvent(ScriptEvent event) {
        this.getPlayer().setEventInstance(event);
        if (event != null) {
            this.getPlayer().send(MaplePacketCreator.practiceMode(event.isPracticeMode()));
        }
    }

    public void showTimer(double seconds) {
        long sec = (long)Math.ceil(seconds * 1000.0);
        this.getClient().announce(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)sec)));
    }

    public void showTimer(int time, int elapseTime) {
        this.getPlayer().send(FieldPacket.clock((ClockPacket)ClockPacket.TimerInfoEx((int)time, (int)elapseTime)));
    }

    public void pauseShowTimer(boolean type, int time, int elapseTime) {
        this.getPlayer().send(FieldPacket.clock((ClockPacket)ClockPacket.pauseTimer((boolean)type, (int)time, (int)elapseTime)));
    }

    public void showTimer(boolean left, int seconds) {
        this.getClient().announce(FieldPacket.clock((ClockPacket)ClockPacket.shiftTimer((boolean)left, (int)seconds)));
    }

    public void closeTimer() {
        this.getClient().announce(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)-1L)));
    }

    public void setDeathCount(int nDeathCount) {
        this.getPlayer().setDeathCount(nDeathCount);
        this.getPlayer().send(CField.setDeathCount((MapleCharacter)this.getPlayer(), (int)nDeathCount));
        this.getPlayer().getMap().broadcastMessage(CField.setDeathCount((MapleCharacter)this.getPlayer(), (int)nDeathCount));
        if (nDeathCount <= 0) {
            this.closeUI(94);
        }
    }

    public void showDeathCount() {
        this.getPlayer().send(CField.setDeathCount((MapleCharacter)this.getPlayer(), (int)this.getPlayer().getDeathCount()));
        this.getPlayer().getMap().broadcastMessage(CField.setDeathCount((MapleCharacter)this.getPlayer(), (int)this.getPlayer().getDeathCount()));
    }

    public boolean hasItem(int itemId) {
        return this.getPlayer().haveItem(itemId);
    }

    public boolean hasItem(int itemId, int quantity) {
        return this.getPlayer().haveItem(itemId, quantity);
    }

    public String getName() {
        return this.getPlayer().getName();
    }

    public int getNowOnlineTime() {
        return this.getPlayer().getNowOnlineTime();
    }

    public int getOnlineTime() {
        return this.getPlayer().getOnlineTime();
    }

    public int getSkillLevel(int skillId) {
        return this.getPlayer().getSkillLevel(skillId);
    }

    public void changeSkillLevel(int skillId, byte newLevel) {
        Skill sk = SkillFactory.getSkill(skillId);
        if (sk != null) {
            this.getPlayer().changeSkillLevel(sk, newLevel, sk.getMasterLevel());
        }
    }

    public int getSp(int skillId) {
        return this.getPlayer().getRemainingSp();
    }

    public short getSubJob() {
        return this.getPlayer().getSubcategory();
    }

    public void setSubJob(short var1) {
        this.getPlayer().setSubcategory(var1);
    }

    public short getJob() {
        return this.getPlayer().getJob();
    }

    public void setJob(int var1) {
        this.getPlayer().changeJob(var1);
    }

    public int getWorld() {
        return this.getPlayer().getWorld();
    }

    public String getWorldShareRecord(int quest) {
        return this.getPlayer().getWorldShareInfo(quest);
    }

    public String getWorldShareRecord(int quest, String key) {
        return this.getPlayer().getWorldShareInfo(quest, key);
    }

    public void updateWorldShareRecord(int questid, String data) {
        this.getPlayer().updateWorldShareInfo(questid, data);
    }

    public void updateWorldShareRecord(int questid, String key, String value) {
        this.getPlayer().updateWorldShareInfo(questid, key, value);
    }

    public boolean hasEquipped(int itemId) {
        return this.getPlayer().hasEquipped(itemId);
    }

    public boolean hasMeso(long min) {
        return this.getPlayer().getMeso() >= min;
    }

    public void loseMesos(long quantity) {
        this.getPlayer().gainMeso(-1L * quantity, true, true);
    }

    public long getMeso() {
        return this.getPlayer().getMeso();
    }

    public long getCash() {
        return this.getPlayer().getCSPoints(1);
    }

    public long getPoint() {
        return this.getPlayer().getCSPoints(2);
    }

    public int getHyPay(int type) {
        return this.getPlayer().getHyPay(type);
    }

    public void modifyCashShopCurrency(int type, int value) {
        this.getPlayer().modifyCSPoints(type, value);
    }

    public void increaseAllianceCapacity() {
        WorldAllianceService.getInstance().changeAllianceCapacity(this.getPlayer().getGuild().getAllianceId());
    }

    public void setHp(int hp) {
        this.getPlayer().addHP(hp);
    }

    public void increaseMaxHp(int delta) {
        this.getPlayer().getStat().setInfo(this.getPlayer().getStat().getMaxHp(false) + delta, this.getPlayer().getStat().getMaxMp(false), this.getPlayer().getStat().getMaxHp(false) + delta, this.getPlayer().getStat().getMp());
        this.getPlayer().updateSingleStat(MapleStat.HP, this.getPlayer().getStat().getMaxHp(false) + delta);
        this.getPlayer().updateSingleStat(MapleStat.MAX_HP, this.getPlayer().getStat().getMaxHp(false) + delta);
        this.getPlayer().getStat().recalcLocalStats(this.getPlayer());
    }

    public void increaseMaxMp(int delta) {
        this.getPlayer().getStat().setInfo(this.getPlayer().getStat().getMaxHp(false), this.getPlayer().getStat().getMaxMp(false) + delta, this.getPlayer().getStat().getHp(), this.getPlayer().getStat().getMaxMp(false) + delta);
        this.getPlayer().updateSingleStat(MapleStat.MP, this.getPlayer().getStat().getMaxMp(false) + delta);
        this.getPlayer().updateSingleStat(MapleStat.MAX_MP, this.getPlayer().getStat().getMaxMp(false) + delta);
        this.getPlayer().getStat().recalcLocalStats(this.getPlayer());
    }

    public boolean isGm() {
        return this.getPlayer().isGm();
    }

    @Override
    public boolean isQuestCompleted(int questId) {
        return this.getPlayer().getQuestStatus(questId) == 2;
    }

    @Override
    public boolean isQuestStarted(int questId) {
        return this.getPlayer().getQuestStatus(questId) == 1;
    }

    public void loseInvSlot(byte type, short slot) {
        this.getPlayer().getInventory(type).removeSlot(slot);
    }

    public void loseItem(int itemId) {
        this.getPlayer().removeItem(itemId);
    }

    public void loseItem(int itemId, int quantity) {
        this.getPlayer().removeItem(itemId, quantity);
    }

    public void maxSkills() {
        this.getPlayer().maxSkillsByJob(this.getJob());
    }

    public void openUI(int uiId) {
        this.getPlayer().send(UIPacket.sendOpenWindow(uiId));
    }

    public void closeUI(int uiId) {
        this.getPlayer().send(UIPacket.sendCloseWindow(uiId));
    }

    public void openUIWithOption(int uiId, int option) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserOpenUIWithOption.getValue());
        mplew.writeInt(uiId);
        mplew.writeInt(option);
        mplew.writeInt(0);
        this.getClient().announce(mplew.getPacket());
    }

    public void openURL(String sURL) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserOpenURL.getValue());
        mplew.write((byte)0);
        mplew.write((byte)1);
        mplew.writeMapleAsciiString(sURL);
        this.getClient().announce(mplew.getPacket());
    }

    public void openWebUI(String sURL) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserOpenURL.getValue());
        mplew.write((byte)0);
        mplew.write((byte)1);
        mplew.writeMapleAsciiString(sURL);
        this.getClient().announce(mplew.getPacket());
    }

    public void openWebUI(int id, String uiPath, String url) {
        this.getClient().announce(MaplePacketCreator.openWebUI(id, uiPath, url));
    }

    public void playExclSoundWithDownBGM(String data) {
        MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
        hh.writeOpcode(OutHeader.LP_UserEffectLocal);
        hh.write(EffectOpcode.UserEffect_PlayExclSoundWithDownBGM.getValue());
        hh.writeMapleAsciiString(data);
        hh.writeInt(100);
        this.getClient().announce(hh.getPacket());
    }

    public void playSoundWithMuteBGM(String wzPath) {
        this.getClient().announce(EffectPacket.playSoundWithMuteBGM(wzPath));
    }

    public void removeAdditionalEffect() {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.RemoveAdditionalEffect, null, null, null));
    }

    public void resetHyperSkill() {
        HashMap<Integer, SkillEntry> oldList = new HashMap<Integer, SkillEntry>(this.getPlayer().getSkills());
        HashMap<Integer, SkillEntry> newList = new HashMap<Integer, SkillEntry>();
        for (Map.Entry toRemove : oldList.entrySet()) {
            Skill skill = SkillFactory.getSkill((Integer)toRemove.getKey());
            if (skill == null || !skill.isHyperSkill() || this.getPlayer().getSkillLevel((Integer)toRemove.getKey()) != 1) continue;
            if (skill.canBeLearnedBy(this.getPlayer().getJobWithSub())) {
                newList.put((Integer)toRemove.getKey(), new SkillEntry(0, ((SkillEntry)toRemove.getValue()).masterlevel, ((SkillEntry)toRemove.getValue()).expiration));
                continue;
            }
            newList.put((Integer)toRemove.getKey(), new SkillEntry(0, 0, -1L));
        }
        oldList.clear();
        newList.clear();
        this.getClient().sendEnableActions();
    }

    public void resetHyperStatSkill(int pos) {
        HashMap<Integer, SkillEntry> oldList = new HashMap<Integer, SkillEntry>(this.getPlayer().getSkills());
        HashMap<Integer, SkillEntry> newList = new HashMap<Integer, SkillEntry>();
        for (Map.Entry toRemove : oldList.entrySet()) {
            Skill skill = SkillFactory.getSkill((Integer)toRemove.getKey());
            if (skill == null || !skill.isHyperStat() || this.getPlayer().getSkillLevel((Integer)toRemove.getKey()) <= 0) continue;
            newList.put((Integer)toRemove.getKey(), new SkillEntry(0, 0, -1L));
        }
        this.getPlayer().gainMeso(-10000000L, true, true);
        this.getPlayer().changeSkillsLevel(newList);
        for (MapleHyperStats right : this.getPlayer().loadHyperStats(pos)) {
            this.getPlayer().resetHyperStats(right.getPosition(), right.getSkillid());
        }
        this.getClient().announce(MaplePacketCreator.updateHyperPresets(this.getPlayer(), pos, (byte)1));
        oldList.clear();
        newList.clear();
        this.getClient().sendEnableActions();
    }

    public void resetSkills() {
        this.getPlayer().clearSkills();
    }

    public void resetVSkills() {
        this.getPlayer().resetVSkills();
    }

    public void resetStats(short str, short dex, short _int, short luk) {
        this.getPlayer().resetStats(str, dex, _int, luk);
    }

    public boolean revivePet(long uniqueId, int itemId) {
        Item item = this.getPlayer().getInventory(MapleInventoryType.CASH).findByLiSN(uniqueId);
        if (item == null) {
            return false;
        }
        if (!ItemConstants.類型.寵物(item.getItemId())) {
            return false;
        }
        if (item.getPet() == null) {
            return false;
        }
        if (MapleItemInformationProvider.getInstance().getLimitedLife(item.getItemId()) != 0) {
            return false;
        }
        if (item.getExpiration() < 0L || item.getExpiration() > System.currentTimeMillis()) {
            return false;
        }
        switch (itemId) {
            case 4070000: 
            case 5180000: 
            case 5689000: {
                item.setExpiration(System.currentTimeMillis() + 7776000000L);
                break;
            }
            case 5689005: {
                item.setExpiration(System.currentTimeMillis() + 23328000000L);
                break;
            }
            case 5180003: {
                item.setExpiration(-1L);
                break;
            }
            default: {
                return false;
            }
        }
        this.getPlayer().forceReAddItem(item);
        return true;
    }

    public void screenEffect(String name) {
        this.getPlayer().showScreenEffect(name);
    }

    public void scriptProgressItemMessage(int itemId, String msg) {
        this.getPlayer().send(UIPacket.ScriptProgressItemMessage(itemId, msg));
    }

    public void scriptProgressMessage(String msg) {
        this.getClient().announce(UIPacket.getTopMsg(msg));
    }

    public void setAvatarLook(int[] items) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.AvatarLookSet, null, items, null));
    }

    public void setDirectionMod(boolean bSet) {
        this.getClient().announce(UIPacket.setDirectionMod(bSet));
    }

    public void setDirection(boolean bSet) {
        this.getPlayer().setDirection(-1);
    }

    public void setFaceOff(int nFaceItemID) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.FaceOff, null, new int[]{nFaceItemID}, null));
    }

    public void setForcedAction(int n2, int n3) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.ForcedAction, null, new int[]{n2, n3}, null));
    }

    public void setForcedFlip(int n2) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.ForcedFlip, null, new int[]{n2}, null));
    }

    public void setForcedInput(int n2) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.ForcedInput, null, new int[]{n2}, null));
    }

    public void setForcedMove(int n2, int n3) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.ForcedMove, null, new int[]{n2, n3}, null));
    }

    public void setInGameCurNodeEventEnd(boolean inGameCurNode) {
        this.getPlayer().setInGameCurNode(inGameCurNode);
        this.getClient().announce(UIPacket.inGameCurNodeEventEnd(inGameCurNode));
    }

    public void setInGameDirectionMode(boolean b, boolean b2, boolean b3, boolean b4) {
        this.getClient().announce(UIPacket.SetInGameDirectionMode(b, b2, b3, b4));
    }

    public void inGameDirection22(int var) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.UNK_226_5, var));
    }

    public void sendDirectionEvent(String type, int var) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.valueOf((String)type), var));
    }

    public void showNpcEffectPlay(int n1, String string, int n2, int n3, int n4, int n7, int n8, int n9, int n10, String str) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.EffectPlay, string, new int[]{n2, n3, n4, 0, 0, n7, n8, n9, n10}, str));
    }

    public void setLayerBlind(boolean b, int n, int n2) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.blind((int)(b ? 1 : 0), (int)n, (int)0, (int)0, (int)0, (int)n2, (int)0)));
    }

    public void setLayerBlind(boolean enable, int n, int r, int g, int b, int n2, int unk3) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.blind((int)(enable ? 1 : 0), (int)n, (int)r, (int)g, (int)b, (int)n2, (int)unk3)));
    }

    public void setLayerBlindWhite(boolean b, int n, int n2) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.blind((int)(b ? 1 : 0), (int)n, (int)255, (int)255, (int)255, (int)n2, (int)0)));
    }

    public void setLayerMove(int n, String s, int n2, int n3) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.onOffLayer((int)1, (int)n, (String)s, (int)n2, (int)n3, (int)0, (String)"", (int)0, (boolean)false, (int)0, (boolean)false, (int)0, (int)0)));
    }

    public void setLayerOff(int n, String s) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.onOffLayer((int)2, (int)n, (String)s, (int)0, (int)0, (int)0, (String)"", (int)0, (boolean)false, (int)0, (boolean)false, (int)0, (int)0)));
    }

    public void setLayerOn(int n, String s, int n2, int n3, int n4, String s2, int n5) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.onOffLayer((int)0, (int)n, (String)s, (int)n2, (int)n3, (int)n4, (String)s2, (int)n5, (boolean)true, (int)-1, (boolean)false, (int)0, (int)0)));
    }

    public void setStandAloneMode(boolean enable) {
        this.getPlayer().send(UIPacket.SetStandAloneMode(enable));
    }

    public void setStaticScreenMessage(int n, String s, boolean b) {
        this.getClient().announce(UIPacket.setStaticScreenMessage(n, s, b));
    }

    public void setUserEmotionLocal(int n, int n2) {
        this.getClient().announce(UIPacket.UserEmotionLocal(n, n2));
    }

    public void setVansheeMode(int n2) {
        this.getClient().announce(UIPacket.getDirectionEvent(InGameDirectionEventType.VansheeMode, null, new int[]{n2}, null));
    }

    public void changeBGM(String name) {
        FieldPacket.fieldEffect((FieldEffect)FieldEffect.changeBGM((String)name, (int)0, (int)0, (int)0));
    }

    public void showAvatarOriented(String s, boolean toOther) {
        this.getClient().announce(EffectPacket.showAvatarOriented(s));
        if (toOther) {
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), EffectPacket.showAvatarOriented(this.getPlayer().getId(), s), false);
        }
    }

    public void showAvatarOrientedRepeat(boolean b, String s) {
        this.getClient().announce(EffectPacket.showAvatarOrientedRepeat(b, s));
    }

    public void showBlindEffect(boolean b) {
        MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
        hh.writeOpcode(OutHeader.LP_UserEffectLocal);
        hh.write(EffectOpcode.UserEffect_BlindEffect.getValue());
        hh.writeBool(b);
        this.getClient().announce(hh.getPacket());
    }

    public void showDoJangRank() {
        this.getClient().announce(MaplePacketCreator.getDojangRanking());
    }

    public void showProgressMessageFont(String msg, int fontNameType, int fontSize, int fontColorType, int fadeOutDelay) {
        this.getClient().announce(UIPacket.getSpecialTopMsg(msg, fontNameType, fontSize, fontColorType, fadeOutDelay));
    }

    public void showReservedEffect(boolean screenCoord, int rx, int ry, String data) {
        this.getClient().announce(EffectPacket.showReservedEffect(screenCoord, rx, ry, data));
    }

    public void showScreenAutoLetterBox(String s, int n) {
        this.getPlayer().showScreenAutoLetterBox(s, n);
    }

    public void showScreenDelayedEffect(String s, int n) {
        this.getPlayer().showScreenDelayedEffect(s, n);
    }

    public void showSpineScreen(int intro, String path, String aniamtionName, String str) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.showSpineScreen(intro, (String)path, (String)aniamtionName, str)));
    }

    public void offSpineScreen(String str, int val) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.offSpineScreen((String)str, (int)val, (String)"", (int)0)));
    }

    public void showSystemMessage(String msg) {
        this.getClient().announce(MaplePacketCreator.showRedNotice(msg));
    }

    public void showSpouseMessage(int type, String msg) {
        this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(type), msg);
    }

    public final void showTopScreenEffect(String s, int n) {
        this.getPlayer().send(FieldPacket.fieldEffect((FieldEffect)FieldEffect.getFieldBackgroundEffectFromWz((String)s, (int)n)));
    }

    public void showWeatherEffectNotice(String s, int n, int n2) {
        this.getPlayer().getMap().broadcastMessage(UIPacket.showWeatherEffectNotice(s, n, n2, true));
    }

    public void soundEffect(String s, int vol, int n1, int n2) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.playSound((String)s, (int)vol, (int)n1, (int)n2)));
    }

    public void soundEffect(String s) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.playSound((String)s, (int)100, (int)0, (int)0)));
    }

    public void destroyTempNpc(int npcId) {
        this.getPlayer().getMap().removeNpc(npcId);
    }

    public void spawnTempNpc(int npcId, int x, int y) {
        this.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public void teleport(int n, int n2, int x, int y) {
        this.getClient().announce(MaplePacketCreator.userTeleport(false, n, n2, new Point(x, y)));
    }

    public void teleportPortal(int n, int portal) {
        MapleMap map = this.getPlayer().getMap();
        if (map == null) {
            return;
        }
        MaplePortal portalPos = map.getPortal(portal);
        if (portalPos == null) {
            return;
        }
        this.getClient().announce(MaplePacketCreator.userTeleport(false, n, this.getPlayer().getId(), portalPos.getPosition()));
    }

    public void teleportPortal(int n, String portal) {
        MapleMap map = this.getPlayer().getMap();
        if (map == null) {
            return;
        }
        MaplePortal portalPos = map.getPortal(portal);
        if (portalPos == null) {
            return;
        }
        this.getClient().announce(MaplePacketCreator.userTeleport(false, n, this.getPlayer().getId(), portalPos.getPosition()));
    }

    public void teleportToPortalId(int portalID) {
        MapleMap map = this.getPlayer().getMap();
        if (map == null) {
            return;
        }
        MaplePortal portalPos = map.getPortal(portalID);
        if (portalPos == null) {
            return;
        }
        this.getClient().announce(MaplePacketCreator.userTeleport(false, 0, this.getPlayer().getId(), portalPos.getPosition()));
    }

    public void teleportToPortalId(int n, int portalID) {
        MapleMap map = this.getPlayer().getMap();
        if (map == null) {
            return;
        }
        MaplePortal portalPos = map.getPortal(portalID);
        if (portalPos == null) {
            return;
        }
        this.getClient().announce(MaplePacketCreator.userTeleport(false, n, this.getPlayer().getId(), portalPos.getPosition()));
    }

    public void updateDamageSkin(int id) {
        this.getPlayer().changeDamageSkin(id);
    }

    public void UseItemEffect(int itemId) {
        if (itemId == 0) {
            this.getPlayer().setItemEffect(0);
            this.getPlayer().setItemEffectType(0);
        } else {
            Item toUse = this.getPlayer().getInventory(MapleInventoryType.CASH).findById(itemId);
            if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
                this.getClient().sendEnableActions();
                return;
            }
            if (itemId != 5510000) {
                this.getPlayer().setItemEffect(itemId);
                this.getPlayer().setItemEffectType(ItemConstants.getInventoryType(itemId).getType());
            }
        }
        this.getPlayer().getMap().broadcastMessage(this.getPlayer(), MaplePacketCreator.itemEffect(this.getPlayer().getId(), itemId, ItemConstants.getInventoryType(itemId).getType()), false);
    }

    public void useSkillEffect(int skillId, int level) {
        SkillFactory.getSkillEffect(skillId, level).applyTo(this.getPlayer());
    }

    public void useSkillEffect(int skillId, int level, int duration) {
        SkillFactory.getSkillEffect(skillId, level).applyTo(this.getPlayer(), duration);
    }

    public void useMobSkillEffect(int skillId, int level, int duration) {
        SkillFactory.getSkillEffect(skillId, level).applyToMonster(this.getPlayer(), duration);
    }

    public boolean hasEffect(int sourceId) {
        return this.getPlayer().getEffects().get(sourceId).isEmpty();
    }

    public Point getPosition() {
        return this.getPlayer().getPosition();
    }

    public void setActionBar(int id) {
        ActionBarField.MapleFieldActionBar bm = ActionBarField.MapleFieldActionBar.createActionBar(id);
        if (bm != null) {
            this.getPlayer().setActionBar(bm);
        } else {
            this.getPlayer().setActionBar(null);
        }
    }

    public void modifyHonor(int val) {
        this.getPlayer().gainHonor(val);
    }

    public String getAccountName() {
        return this.getPlayer().getName();
    }

    public int getMapId() {
        return this.getPlayer().getMapId();
    }

    public boolean gainRandVSkill() {
        return this.gainRandVSkill(Randomizer.isSuccess(20) ? 0 : 1, false, false);
    }

    public boolean gainRandVSkill(int nCoreType, boolean indieJob, boolean onlyJob) {
        return this.getPlayer().gainRandVSkill(nCoreType, indieJob, onlyJob);
    }

    public boolean gainVCoreSkill(int vcoreoid, int nCount) {
        return this.getPlayer().gainVCoreSkill(vcoreoid, nCount, false);
    }

    public void runNpc(int npcId, int shopId) {
        MapleShopFactory.getInstance().getShop(shopId).sendShop(this.getClient(), npcId, true);
    }

    public void openShop(int npcId, int shopId) {
        MapleShopFactory.getInstance().getShop(shopId).sendShop(this.getClient(), npcId, true);
    }

    public void showSpecialUI(boolean b, String s) {
        this.getClient().announce(UIPacket.ShowSpecialUI(b, s));
    }

    public void showAchieveRate() {
        this.getClient().announce(MaplePacketCreator.achievementRatio(0));
    }

    public void setAchieveRate(int var1) {
        this.getClient().announce(MaplePacketCreator.achievementRatio(var1));
    }

    public void zeroTag(boolean beta) {
        this.getPlayer().zeroTag();
    }

    public void setKeyValue(String key, String value) {
        this.getPlayer().setKeyValue(key, value);
    }

    public String getKeyValue(String key) {
        return this.getPlayer().getKeyValue(key);
    }

    public int getIntKeyValue(String key) {
        return Integer.parseInt(this.getPlayer().getKeyValue(key));
    }

    public void addTrait(String t, int e) {
        this.getPlayer().getTrait(MapleTraitType.valueOf(t)).addExp(e, this.getPlayer());
    }

    public void completeMobCollection() {
        MobCollectionFactory.doneCollection(this.getPlayer());
    }

    public void registerMobCollection(int mobId) {
        MobCollectionFactory.registerMobCollection(this.getPlayer(), mobId);
    }

    public boolean checkMobCollection(int mobId) {
        return MobCollectionFactory.checkMobCollection(this.getPlayer(), mobId);
    }

    public boolean checkMobCollection(String s) {
        return MobCollectionFactory.checkMobCollection(this.getPlayer(), s);
    }

    public void handleRandCollection(int s) {
        MobCollectionFactory.handleRandCollection(this.getPlayer(), s);
    }

    public void registerMobCollectionQuest(int s) {
        MobCollectionFactory.registerMobCollection(this.getPlayer(), s);
    }

    public boolean hasAndroid() {
        return this.getPlayer().getAndroid() != null;
    }

    public int getAndroidFace() {
        return this.getPlayer().getAndroid().getFace();
    }

    public int getAndroidHair() {
        return this.getPlayer().getAndroid().getHair();
    }

    public int getAndroidSkin() {
        return this.getPlayer().getAndroid().getSkin();
    }

    public void updateTowerRank(int stage, int time) {
        byte world = this.getPlayer().getWorld();
        int chrId = this.getPlayer().getId();
        String chrName = this.getPlayer().getName();
        DatabaseLoader.DatabaseConnection.domain(con -> {
            ResultSet rs = SqlTool.query(con, "SELECT * FROM `zrank_lobby` WHERE `world` = ? AND `characters_id` = ?", world, chrId);
            if (rs.next()) {
                Calendar c = Calendar.getInstance();
                c.set(7, 2);
                c.set(11, 0);
                c.set(12, 0);
                c.set(13, 0);
                c.set(14, 0);
                SqlTool.update(con, "UPDATE `zrank_lobby` SET `stage` = ?, `time` = ? WHERE `world` = ? AND `characters_id` = ? AND ((`stage` < ? OR (`stage` = ? AND `time` > ?)) OR (`logtime` < ?))", stage, time, world, chrId, stage, stage, time, c.getTime().getTime());
                return null;
            }
            SqlTool.update("INSERT INTO `zrank_lobby` (`world`, `characters_id`, `characters_name`, `stage`, `time`)VALUES (?, ?, ?, ?, ?)", world, chrId, chrName, stage, time);
            return null;
        });
    }

    public void showTextEffect(String message, int second, int posY) {
        this.getClient().announce(EffectPacket.showCombustionMessage(message, second * 1000, posY));
    }

    public int getJobCategory() {
        return this.getPlayer().getCarteByJob();
    }

    public void removeWeaponSoul() {
        this.getPlayer().setSoulMP(0);
    }

    public void removeBuffs() {
        if (this.getPlayer().getAllBuffs().size() > 0) {
            this.getPlayer().removeBuffs(true);
        }
    }

    public int getMaxHp() {
        return this.getPlayer().getStat().getMaxHp();
    }

    public int getMaxMp() {
        return this.getPlayer().getStat().getMaxMp();
    }

    public long getExpNeededForLevel() {
        if (this.getPlayer().getLevel() < 300) {
            return GameConstants.getExpNeededForLevel(this.getPlayer().getLevel() + 1);
        }
        return 0L;
    }

    public void customBuff(String buffName, String levels) throws IOException {
        EnumMap<SecondaryStat, Integer> map = new EnumMap<SecondaryStat, Integer>(SecondaryStat.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Map jsonMap = (Map)objectMapper.readValue(new File("config/CustomBuff.json"), Map.class);
        Map buffMap = (Map)jsonMap.get(buffName);
        if (buffMap != null) {
            int buffId = ((Number)buffMap.get("buff_id")).intValue();
            if (!ItemConstants.類型.消耗(buffId)) {
                return;
            }
            if (Integer.parseInt(levels) < 1) {
                MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(buffId);
                effect.applyTo(this.getPlayer(), 1);
            } else {
                Map<String, Map<String, Integer>> levelsMap = (Map<String, Map<String, Integer>>) buffMap.get("levels");
                if (levelsMap.isEmpty()) {
                    return;
                }
                levelsMap.forEach((level, stats) -> {
                    if (levels.equals(level)) {
                        stats.forEach((stat, value) -> {
                            SecondaryStat secondaryStat = SecondaryStat.valueOf(stat);
                            ScriptPlayer.inc(map, secondaryStat, value);
                        });
                    }
                });
                if (map.isEmpty()) {
                    return;
                }
                MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(buffId);
                effect.setStatups(map);
                effect.applyTo(this.getPlayer(), 999999999);
            }
        }
    }

    public void showScreenShaking(int mapID, boolean stop) {
        this.getClient().announce(UIPacket.screenShake(mapID, stop));
    }

    public void showBalloonMsg(String path, int duration) {
        this.getPlayer().write(UserLocal.inGameDirectionEvent((InGameDirectionEvent)InGameDirectionEvent.effectPlay((String)path, (int)duration, (Position)new Position(0, -100), (int)0, (int)0, (boolean)true, (int)0, (String)"")));
    }

    public void showStageClear(int n) {
        this.getClient().announce(FieldPacket.fieldEffect((FieldEffect)FieldEffect.showClearStageExpWindow((int)n)));
    }

    public void startBurn(int type, long time) {
        this.getPlayer().setBurningChrType(type);
        this.getPlayer().setBurningChrTime(time);
    }

    public int getMarriageId() {
        return this.getPlayer().getMarriageId();
    }

    public void setMarriageId(int playerid) {
        this.getPlayer().setMarriageId(playerid);
    }

    public int getDressingRoomSlot(int style) {
        int defaultSlot = style == 1 ? 0 : 3;
        MapleCharacter chr = this.getPlayer();
        if (chr == null) {
            return defaultSlot;
        }
        List<Integer> slots = chr.getSalon().getOrDefault(style, null);
        if (slots == null) {
            slots = new LinkedList<Integer>();
            for (int i = 0; i < defaultSlot; ++i) {
                slots.add(0);
            }
            chr.getSalon().put(style, slots);
        }
        return slots.size();
    }

    public boolean setDressingRoomSlot(int style, int slot) {
        int maxSlot;
        int n = maxSlot = style == 1 ? 6 : 102;
        if (slot > maxSlot || this.getPlayer() == null) {
            return false;
        }
        int oldSlot = this.getDressingRoomSlot(style);
        List<Integer> slots = this.getPlayer().getSalon().get(style);
        if (slot < oldSlot) {
            for (int i = 0; i < oldSlot - slot; ++i) {
                slots.remove(slots.size() - 1);
            }
        } else if (slot > oldSlot) {
            for (int i = 0; i < slot - oldSlot; ++i) {
                slots.add(0);
            }
        }
        this.getPlayer().send(MaplePacketCreator.encodeUpdateCombingRoomSlotCount(3 - style, 0, slot, slot));
        this.getPlayer().send(MaplePacketCreator.encodeCombingRoomOldSlotCount(3 - style, 0, oldSlot));
        return true;
    }

    public boolean increaseTrunkCapacity(int gain) {
        if (this.getPlayer().getTrunk().getSlots() + gain > 128) {
            this.getPlayer().getTrunk().increaseSlots((byte)-128);
            return false;
        }
        this.getPlayer().getTrunk().increaseSlots((byte)gain);
        return true;
    }

    public void enterCS() {
        InterServerHandler.enterCS(this.getClient(), this.getClient().getPlayer());
    }

    public void increaseDamageSkinCapacity() {
        int damskinslot;
        String count = this.getPlayer().getOneInfo(56829, "count");
        int n = damskinslot = count == null ? 1 : Integer.valueOf(count);
        if (damskinslot < 48) {
            this.getPlayer().updateOneInfo(56829, "count", String.valueOf(damskinslot + 1));
            this.getPlayer().send(InventoryPacket.UserDamageSkinSaveResult(2, 4, this.getPlayer()));
            this.getPlayer().dropMessage(1, "傷害皮膚擴充成功，當前有：" + (damskinslot + 1) + " 格");
        } else {
            this.getPlayer().dropMessage(1, "傷害皮膚擴充失敗，欄位已超過上限。");
        }
    }

    public void enableEquipSlotExt(int days) {
        this.getClient().announce(MTSCSPacket.擴充項鏈(days));
    }

    public boolean runAntiMacro() {
        return MapleAntiMacro.startAnti(this.getPlayer(), this.getPlayer(), (byte)1, true);
    }

    public int getFame() {
        return this.getPlayer().getFame();
    }

    public void setFame(int fame) {
        this.getPlayer().setFame(fame);
    }

    public void updateFamiliars() {
        this.getPlayer().updateFamiliars();
    }

    public List<MonsterFamiliar> getFamiliars() {
        return this.getPlayer().getFamiliars();
    }

    public void setHexaCoreLevel(int coreId, int level) {
        MapleHexaSkill mhs = new MapleHexaSkill(coreId, level);
        this.getPlayer().updateHexaSkill(mhs);
    }

    public void clearHexaSkills() {
        for (Integer mhs : this.getPlayer().getHexaSkills().keySet()) {
            this.getPlayer().getHexaSkills().get(mhs).setSkilllv(0);
        }
    }

    public void createParty() {
        if (this.getPlayer().getParty() == null) {
            Party party = Party.createNewParty((boolean)false, (boolean)false, (String)(this.getPlayer().getName() + "的隊伍"), (World)this.getPlayer().getClient().getWorld());
            PartyMember pm = new PartyMember(this.getPlayer());
            party.setPartyLeaderID(pm.getCharID());
            party.getPartyMembers()[0] = pm;
            this.getPlayer().setParty(party);
            this.getPlayer().write(WvsContext.partyResult((PartyResult)PartyResult.createNewParty((Party)party)));
        }
    }

    public void disbandParty() {
        this.getPlayer().getParty().disband();
    }

    @Override
    public boolean isQuestFinished(int questId) {
        return this.getPlayer().getQuestStatus(questId) == 2;
    }

    public short getSpace(int type) {
        return this.getPlayer().getInventory(MapleInventoryType.getByType((byte)type)).getNumFreeSlot();
    }

    public void gainItemAndEquip(int itemId, short slot) {
        MapleInventoryManipulator.addItemAndEquip(this.getClient(), itemId, slot);
    }

    public MapleJob getMapleJob() {
        return MapleJob.getById(this.getPlayer().getJobWithSub());
    }

    public MapleJob getMapleJobById(int id) {
        return MapleJob.getById(id);
    }

    public MapleJob[] getAllMapleJobs() {
        return MapleJob.values();
    }

    public boolean getFreeAllSlots(int slot) {
        return this.getPlayer().canHoldSlots(slot);
    }

    public void removeSlot(int invType, short slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(this.getClient(), MapleInventoryType.getByType((byte)invType), slot, quantity, true);
    }

    public void giveItemForPlayerName(String name, int itemId, int quantity) {
        List<Map<String, Object>> result = SqlTool.customSqlResult("SELECT * FROM characters WHERE name =?", name);
        if (!result.isEmpty()) {
            MapleCharacter character = MapleCharacter.getCharacterById((Integer)((Map)result.getFirst()).get("id"));
            if (character.isOnline()) {
                new ScriptPlayer(character).gainItem(itemId, quantity);
                this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "成功給予。");
            } else {
                this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不在線。");
            }
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不存在。");
        }
    }

    public void giveItemForMap(int mapId, int itemId, int quantity) {
        List<MapleCharacter> characters = this.getClient().getChannelServer().getMapFactory().getMap(mapId).getCharacters();
        for (MapleCharacter character : characters) {
            new ScriptPlayer(character).gainItem(itemId, quantity);
        }
        this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "一共給予了" + characters.size() + "個角色。");
    }

    public void givePointForPlayerName(String name, String type, int quantity) {
        List<Map<String, Object>> result = SqlTool.customSqlResult("SELECT * FROM characters WHERE name =?", name);
        if (!result.isEmpty()) {
            MapleCharacter character = MapleCharacter.getCharacterById((Integer)((Map)result.getFirst()).get("id"));
            if (character.isOnline()) {
                switch (type) {
                    case "cash": {
                        new ScriptPlayer(character).modifyCashShopCurrency(1, quantity);
                        this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "成功給予。");
                        break;
                    }
                    case "point": {
                        new ScriptPlayer(character).modifyCashShopCurrency(2, quantity);
                        this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "成功給予。");
                        break;
                    }
                    case "meso": {
                        new ScriptPlayer(character).gainMeso(quantity);
                        this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "成功給予。");
                        break;
                    }
                    default: {
                        this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "givePoint指令格式錯誤。");
                        break;
                    }
                }
            } else {
                this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不在線。");
            }
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不存在。");
        }
    }

    public void gmInvincible() {
        if (this.getPlayer().isGm()) {
            this.getPlayer().setInvincible(!this.getPlayer().isInvincible());
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "GM無敵: " + this.getPlayer().isInvincible());
        }
    }

    public void gmHide() {
        if (this.getPlayer().isGm()) {
            Skill skill = SkillFactory.getSkill(9001004);
            if (this.getPlayer().isHidden()) {
                this.getPlayer().cancelEffect(skill.getEffect(1), false, -1L);
            } else {
                skill.getEffect(1).applyTo(this.getPlayer());
            }
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "GM隱身: " + this.getPlayer().isHidden());
        }
    }

    public void gmKillMap() {
        if (this.getPlayer().isGm()) {
            this.getPlayer().getMap().killAllMonsters(this.getPlayer(), true);
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "操作成功。");
        }
    }

    public void gmDebug(String debug) {
        if (this.getPlayer().isGm()) {
            String[] lis = new String[]{"quests", "npcs", "items", "maps", "items", "portals", "events", "reactors", "commands"};
            switch (debug) {
                case "quests": 
                case "npcs": 
                case "items": 
                case "portals": 
                case "events": 
                case "reactors": 
                case "commands": {
                    if (!this.getPlayer().getScriptManagerDebug().contains(debug)) {
                        this.getPlayer().getScriptManagerDebug().add(debug);
                        break;
                    }
                    this.getPlayer().getScriptManagerDebug().remove(debug);
                    break;
                }
                case "maps": {
                    if (this.getPlayer().getScriptManagerDebug().contains("maps")) {
                        this.getPlayer().getScriptManagerDebug().add("maps");
                        this.getPlayer().getScriptManagerDebug().add("maps/onUserEnter");
                        this.getPlayer().getScriptManagerDebug().add("maps/onFirstUserEnter");
                        break;
                    }
                    this.getPlayer().getScriptManagerDebug().remove("maps");
                    this.getPlayer().getScriptManagerDebug().remove("maps/onUserEnter");
                    this.getPlayer().getScriptManagerDebug().remove("maps/onFirstUserEnter");
                    break;
                }
                case "clear": {
                    this.getPlayer().getScriptManagerDebug().clear();
                    break;
                }
                case "show": {
                    break;
                }
                case "all": {
                    this.getPlayer().getScriptManagerDebug().clear();
                    for (String s : lis) {
                        this.getPlayer().getScriptManagerDebug().add(s);
                    }
                    this.getPlayer().getScriptManagerDebug().add("maps/onUserEnter");
                    this.getPlayer().getScriptManagerDebug().add("maps/onFirstUserEnter");
                    break;
                }
                default: {
                    this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "指令格式錯誤。");
                }
            }
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "當前過濾調試信息: " + String.join((CharSequence)",", this.getPlayer().getScriptManagerDebug()));
        }
    }

    public int getOnlinePlayersNum() {
        int ch = Math.min(ServerConfig.CHANNELS_PER_WORLD, 40);
        int count = 0;
        for (int i = 1; i <= ch; ++i) {
            Collection<MapleMap> maps = ChannelServer.getInstance(i).getMapFactory().getAllMaps();
            for (MapleMap map : maps) {
                count += map.getCharacters().size();
            }
        }
        return count;
    }

    public void gmGetMob() {
        if (this.getPlayer().isGm()) {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "──────────────────────────");
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "地圖名： " + this.getPlayer().getMap().getMapName());
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "地圖id： " + this.getPlayer().getMap().getId());
            List<MapleMonster> monsters = this.getPlayer().getMap().getMonsters();
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "怪物數量： " + monsters.size());
            for (MapleMonster monster : monsters) {
                this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), StringData.getMobStringById(monster.getId()) + "(id: " + monster.getId() + ") HP: " + monster.getHp() + "/" + monster.getMaxHP());
            }
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "──────────────────────────");
        }
    }

    public void KickForPlayerName(boolean all, String name) {
        if (all) {
            int ch = Math.min(ServerConfig.CHANNELS_PER_WORLD, 40);
            for (int i = 1; i <= ch; ++i) {
                Collection<MapleMap> maps = ChannelServer.getInstance(i).getMapFactory().getAllMaps();
                for (MapleMap map : maps) {
                    for (MapleCharacter chr : map.getCharacters()) {
                        new ScriptPlayer(chr).dissociateClient();
                    }
                }
            }
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "操作成功。");
        } else {
            List<Map<String, Object>> result = SqlTool.customSqlResult("SELECT * FROM characters WHERE name =?", name);
            if (!result.isEmpty()) {
                MapleCharacter character = MapleCharacter.getCharacterById((Integer)((Map)result.getFirst()).get("id"));
                if (character.isOnline()) {
                    new ScriptPlayer(character).dissociateClient();
                    this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "操作成功。");
                } else {
                    this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不在線。");
                }
            } else {
                this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不存在。");
            }
        }
    }

    public short getBeginner() {
        return JobConstants.getBeginner(this.getPlayer().getJob());
    }

    public void gmCooldown() {
        boolean cooldown = this.getPlayer().isGmcooldown();
        this.getPlayer().setGmcooldown(!cooldown);
        this.getPlayer().resetAllCooldowns(!cooldown);
        if (!cooldown) {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "技能無CD: 開啟");
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "技能無CD: 關閉");
        }
    }

    public void gmSeparation(int times) {
        if (times < 1) {
            times = 1;
        }
        this.getPlayer().setSeparation(times);
        this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "當前傷害倍數: " + times);
    }

    public void autoAttack(boolean trigger) {
        if (trigger) {
            this.getPlayer().getTimerInstance().scheduleAtFixedRate(() -> {
                MapleMap map = this.getPlayer().getMap();
                double range = Double.POSITIVE_INFINITY;
                List<MapleMapObject> targets = map.getMapObjectsInRange(this.getPlayer().getPosition(), range, Collections.singletonList(MapleMapObjectType.MONSTER));
                if (!targets.isEmpty()) {
                    long damage = this.getPlayer().getCalcDamage().getRandomDamage(this.getPlayer(), true);
                    for (MapleMapObject monstermo : targets) {
                        MapleMonster mob = (MapleMonster)monstermo;
                        map.broadcastMessage(MobPacket.damageMonster(mob.getObjectId(), damage));
                        mob.damage(this.getPlayer(), 0, damage, false);
                    }
                }
            }, 0L, 10000L, TimeUnit.MILLISECONDS);
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "自動攻擊: 開啟");
        } else {
            this.getPlayer().getTimerInstance().shutdownNow();
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "自動攻擊: 關閉");
        }
    }

    public void ban(String name, String reason, boolean mac) {
        List<Map<String, Object>> result = SqlTool.customSqlResult("SELECT * FROM characters WHERE name =?", name);
        if (!result.isEmpty()) {
            MapleCharacter character = MapleCharacter.getCharacterById((Integer)((Map)result.getFirst()).get("id"));
            if (character.isOnline()) {
                character.ban(reason, mac, false, false);
                this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "操作成功。");
            } else {
                this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不在線。");
            }
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色不存在。");
        }
    }

    public void gmExpRate(int channel, int rate) {
        ChannelServer cs = ChannelServer.getInstance(channel);
        if (cs != null) {
            cs.setExpRate(rate);
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "頻道: " + channel + " 當前EXP倍率為 " + cs.getExpRate());
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "頻道: " + channel + " 不存在。");
        }
    }

    public void gmDropRate(int channel, int rate) {
        ChannelServer cs = ChannelServer.getInstance(channel);
        if (cs != null) {
            cs.setDropRate(rate);
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "頻道: " + channel + " 當前掉寶倍率為 " + cs.getDropRate());
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "頻道: " + channel + " 不存在。");
        }
    }

    public void gmSpawn(int mobId, int count, int hp) {
        if (MapleLifeFactory.getMonster(mobId) != null) {
            for (int i = 0; i < count; ++i) {
                MapleMonster mob = MapleLifeFactory.getMonster(mobId);
                if (hp > 0) {
                    mob.changeBaseHp(hp);
                }
                this.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, this.getPlayer().getPosition());
            }
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "怪物不存在。");
        }
    }

    public ScriptItem getInventorySlot(byte type, short slot) {
        return new ScriptItem(this.getPlayer().getInventory(type).getItem(slot));
    }

    public void broadcastGachaponMessage(String notice, Item item, String text) {
        WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.getGachaponMega(this.getPlayer().getName(), notice, item, 3, this.getClient().getChannel()));
    }

    public void unban(String name) {
        boolean trigger;
        boolean bl = trigger = MapleClient.unban(name) >= 0;
        if (MapleClient.unbanIPMacs(name) < 0) {
            trigger = false;
        }
        if (MapleClient.unHellban(name) < 0) {
            trigger = false;
        }
        if (trigger) {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色名: " + name + " 解除封禁。");
        } else {
            this.getPlayer().dropSpouseMessage(UserChatMessageType.getByType(9), "角色名: " + name + " 不在封禁狀態");
        }
    }

    public void openPacketUIByAdmin() {
        new GMCommand.PacketUI().execute(this.getPlayer().getClient(), null);
    }

    public void reloadSkill() {
        Config.load();
        SkillFactory.loadSkillData();
        SkillFactory.loadDelays();
        SkillFactory.loadMemorySkills();
        this.getPlayer().dropMessage(5, "重載完畢.");
    }

    public void gainVCraftCore(int quantity) {
        this.getPlayer().gainVCraftCore(quantity);
    }

    public void reload(int type) {
        switch (type) {
            case 1: {
                Config.load();
                break;
            }
            case 2: {
                MapleMonsterInformationProvider.getInstance().clearDrops();
            }
            case 4: {
                MapleShopFactory.getInstance().clear();
                this.getPlayer().dropMessage(5, "重讀商店內容完成。");
            }
        }
        Config.load();
    }

    public boolean hasMesos(int meso) {
        return this.getPlayer().getMeso() >= (long)meso;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public int addHyPay(int hypay) {
        int pay = this.getHyPay(1);
        int payUsed = this.getHyPay(2);
        int payReward = this.getHyPay(4);
        if (hypay > pay) {
            return -1;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            int n;
            block15: {
                PreparedStatement ps = con.prepareStatement("UPDATE hypay SET pay = ? WHERE accname = ?");
                try {
                    ps.setInt(1, pay - hypay);
                    ps.setString(2, this.getClient().getAccountName());
                    ps.executeUpdate();
                    n = 1;
                    if (ps == null) break block15;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return n;
        }
        catch (SQLException e) {
            log.error("加減儲值信息發生錯誤", e);
            return -1;
        }
    }

    public int getModifyMileage() {
        return this.getPlayer().getMileage();
    }

    public int getMaplePoints() {
        return this.getMaplePoints(false);
    }

    public int getMaplePoints(boolean onlyMPoint) {
        return this.getClient().getMaplePoints(onlyMPoint);
    }

    public int getMileage() {
        return this.getClient().getMileage();
    }

    public int modifyMileage(int quantity) {
        return this.modifyMileage(quantity, 2, false, true, null);
    }

    public int modifyMileage(int quantity, int type) {
        return this.modifyMileage(quantity, type, false, true, null);
    }

    public int modifyMileage(int quantity, String log) {
        return this.modifyMileage(quantity, 2, false, true, log);
    }

    public int modifyMileage(int quantity, boolean show) {
        return this.modifyMileage(quantity, 2, show, true, null);
    }

    public int modifyMileage(int quantity, int type, boolean show, boolean limitMax, String log) {
        int result;
        if (quantity == 0) {
            return 0;
        }
        int itemID = 2431872;
        if (quantity > 0 && this.getMileage() + quantity < 0) {
            if (show) {
                this.getPlayer().send(UIPacket.ScriptProgressItemMessage(itemID, "里程已達到上限."));
            }
            return 3;
        }
        int n = result = quantity > 0 ? this.getClient().rechargeMileage(quantity, type, limitMax, log) : this.getClient().modifyMileage(quantity);
        if (show && result > 0 && result < 3) {
            switch (result) {
                case 1: {
                    this.getPlayer().send(UIPacket.ScriptProgressItemMessage(itemID, "里程已達到每日上限！"));
                    break;
                }
                case 2: {
                    this.getPlayer().send(UIPacket.ScriptProgressItemMessage(itemID, "里程已達到每月上限！"));
                }
            }
            return result;
        }
        if (result == 0 && show && quantity != 0) {
            this.getPlayer().send(UIPacket.ScriptProgressItemMessage(itemID, (quantity > 0 ? "獲得 " : "消耗 ") + Math.abs(quantity) + " 里程！"));
            this.getPlayer().send(EffectPacket.showSpecialEffect(EffectOpcode.UserEffect_ExpItemConsumed));
        }
        return result;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public int updateHypay() {
        int pay = this.getHyPay(1);
        int payUsed = this.getHyPay(2);
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            int n;
            block14: {
                PreparedStatement ps = con.prepareStatement("UPDATE hypay SET pay = 0, payUsed = ? WHERE accname = ?");
                try {
                    ps.setInt(1, payUsed + pay);
                    ps.setString(2, this.getClient().getAccountName());
                    ps.executeUpdate();
                    n = 1;
                    if (ps == null) break block14;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return n;
        }
        catch (SQLException e) {
            log.error("加減儲值信息發生錯誤", e);
            return -1;
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public int addPayReward(int hypay) {
        int pay = this.getHyPay(1);
        int payUsed = this.getHyPay(2);
        int payReward = this.getHyPay(4);
        if (hypay > pay) {
            return -1;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            int n;
            block15: {
                PreparedStatement ps = con.prepareStatement("UPDATE hypay SET payReward = ? WHERE accname = ?");
                try {
                    ps.setInt(1, payReward + hypay);
                    ps.setString(2, this.getClient().getAccountName());
                    ps.executeUpdate();
                    n = 1;
                    if (ps == null) break block15;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return n;
        }
        catch (SQLException e) {
            log.error("加減儲值信息發生錯誤", e);
            return -1;
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public int delPayReward(int pay) {
        int payReward = this.getHyPay(4);
        if (pay <= 0) {
            return -1;
        }
        if (pay > payReward) {
            return -1;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            int n;
            block16: {
                PreparedStatement ps = con.prepareStatement("UPDATE hypay SET payReward = ? WHERE accname = ?");
                try {
                    ps.setInt(1, payReward - pay);
                    ps.setString(2, this.getClient().getAccountName());
                    ps.executeUpdate();
                    n = 1;
                    if (ps == null) break block16;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return n;
        }
        catch (SQLException ex) {
            log.error("加減消費獎勵信息發生錯誤", ex);
            return -1;
        }
    }

    public void updateReward() {
        LinkedList<MapleReward> rewards = new LinkedList<MapleReward>();
        LinkedList<Integer> toRemove = new LinkedList<Integer>();
        try {
            DatabaseConnectionEx.getInstance();
            try (DruidPooledConnection con = DatabaseConnectionEx.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM rewards WHERE `accid` = ? OR (`accid` IS NULL AND `cid` = ?)");){
                ps.setInt(1, this.getPlayer().getAccountID());
                ps.setInt(2, this.getPlayer().getId());
                try (ResultSet rs = ps.executeQuery();){
                    while (rs.next()) {
                        if (rewards.size() >= 200) {
                            break;
                        }
                        if (rs.getLong("end") > 0L && rs.getLong("end") <= System.currentTimeMillis()) {
                            toRemove.add(rs.getInt("id"));
                            continue;
                        }
                        rewards.add(new MapleReward(rs.getInt("id"), rs.getLong("start"), rs.getLong("end"), rs.getInt("type"), rs.getInt("amount"), rs.getInt("itemId"), rs.getString("desc")));
                    }
                }
            }
        }
        catch (SQLException e) {
            log.error("Unable to update rewards: ", e);
        }
        Iterator iterator = toRemove.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            this.deleteReward(i, false);
        }
        this.getPlayer().send(MaplePacketCreator.updateReward(0, (byte)9, rewards, 9L));
    }

    public MapleReward getReward(int id) {
        MapleReward reward = null;
        try {
            DatabaseConnectionEx.getInstance();
            try (DruidPooledConnection con = DatabaseConnectionEx.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM rewards WHERE `id` = ? AND (`accid` = ? OR (`accid` IS NULL AND `cid` = ?))");){
                ps.setInt(1, id);
                ps.setInt(2, this.getPlayer().getAccountID());
                ps.setInt(3, this.getPlayer().getId());
                try (ResultSet rs = ps.executeQuery();){
                    if (rs.next()) {
                        reward = new MapleReward(rs.getInt("id"), rs.getLong("start"), rs.getLong("end"), rs.getInt("type"), rs.getInt("amount"), rs.getInt("itemId"), rs.getString("desc"));
                    }
                }
            }
        }
        catch (SQLException e) {
            log.error("Unable to obtain reward information: ", e);
        }
        return reward;
    }

    public void addReward(boolean acc, int type, long amount, int item, String desc) {
        this.addReward(acc, 0L, 0L, type, amount, item, desc);
    }

    public void addReward(boolean acc, long start, long end, int type, long amount, int itemId, String desc) {
        ScriptPlayer.addReward(acc ? this.getPlayer().getAccountID() : 0, this.getPlayer().getId(), start, end, type, amount, itemId, desc);
    }

    public static void addReward(int accid, int cid, long start, long end, int type, long amount, int itemId, String desc) {
        try {
            DatabaseConnectionEx.getInstance();
            try (DruidPooledConnection con = DatabaseConnectionEx.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO rewards (`accid`, `cid`, `start`, `end`, `type`, `amount`, `itemId`, `desc`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");){
                if (accid > 0) {
                    ps.setInt(1, accid);
                } else {
                    ps.setNull(1, 4);
                }
                if (cid > 0) {
                    ps.setInt(2, cid);
                } else {
                    ps.setNull(2, 4);
                }
                ps.setLong(3, start);
                ps.setLong(4, end);
                ps.setInt(5, type);
                ps.setLong(6, amount);
                ps.setInt(7, itemId);
                ps.setString(8, desc);
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            log.error("Unable to obtain reward: ", e);
        }
    }

    public void deleteReward(int id) {
        this.deleteReward(id, true);
    }

    public void deleteReward(int id, boolean update) {
        try {
            DatabaseConnectionEx.getInstance();
            try (DruidPooledConnection con = DatabaseConnectionEx.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM rewards WHERE `id` = ? AND (`accid` = ? OR (`accid` IS NULL AND `cid` = ?))");){
                ps.setInt(1, id);
                ps.setInt(2, this.getPlayer().getAccountID());
                ps.setInt(3, this.getPlayer().getId());
                ps.execute();
            }
        }
        catch (SQLException e) {
            log.error("Unable to delete reward: ", e);
        }
        if (update) {
            this.updateReward();
        }
    }

    public void makeNpc(int npcid) {
        int npcId = npcid;
        MapleNPC npc = MapleLifeFactory.getNPC(npcId, this.getPlayer().getMapId());
        if (npc != null && !npc.getName().equals("MISSINGNO")) {
            int xpos = this.getPlayer().getPosition().x;
            int ypos = this.getPlayer().getPosition().y;
            int fh = this.getPlayer().getMap().getFootholds().findBelow(this.getPlayer().getPosition()).getId();
            npc.setPosition(this.getPlayer().getPosition());
            npc.setCy(ypos);
            npc.setRx0(xpos + 50);
            npc.setRx1(xpos - 50);
            npc.setCurrentFh(fh);
            npc.setCustom(true);
            try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
                PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                ps.setInt(1, npcId);
                ps.setInt(2, 0);
                ps.setInt(3, fh);
                ps.setInt(4, ypos);
                ps.setInt(4, ypos);
                ps.setInt(5, xpos + 50);
                ps.setInt(6, xpos - 50);
                ps.setString(7, "n");
                ps.setInt(8, xpos);
                ps.setInt(9, ypos);
                ps.setInt(10, this.getPlayer().getMapId());
                ps.executeUpdate();
            }
            catch (SQLException e) {
                this.getPlayer().dropMessage(6, "儲存Npc訊息到資料庫中出現錯誤.");
            }
            this.getPlayer().getMap().addMapObject(npc);
            this.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc));
        } else {
            this.getPlayer().dropMessage(6, "你應該輸入一個正確的 Npc-Id.");
        }
    }

    public Map<String, Object> getTempValues() {
        return this.tempValues;
    }

    public final void setFamiliarsChanged(boolean change) {
        this.changed_familiars = change;
    }

    public MonsterFamiliar getSummonedFamiliar() {
        return this.summonedFamiliar;
    }

    public void removeFamiliarsInfo(int n) {
        if (this.familiars.containsKey(n)) {
            this.changed_familiars = true;
            this.familiars.remove(n);
        }
    }

    public void addFamiliarsInfo(MonsterFamiliar monsterFamiliar) {
        this.changed_familiars = true;
        this.familiars.put(monsterFamiliar.getId(), monsterFamiliar);
    }

    public void initFamiliar(MonsterFamiliar cbr) {
        if (this.summonedFamiliar != null) {
            this.summonedFamiliar.setSummoned(false);
        }
        this.summonedFamiliar = cbr;
        this.summonedFamiliar.setSummoned(true);
    }

    public void removeFamiliar() {
        if (this.summonedFamiliar != null) {
            this.summonedFamiliar.setSummoned(false);
            if (this.map != null) {
                this.map.disappearMapObject(this.summonedFamiliar);
            }
        }
        this.summonedFamiliar = null;
        this.getPlayer().write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.removeFamiliar((int)this.getId(), (boolean)true)));
    }

    public int getEventCount(String eventId) {
        return this.getEventCount(eventId, 0);
    }

    public int getEventCount(String eventId, int type) {
        return this.getEventCount(eventId, type, 1);
    }

    public int getEventCount(String eventId, int type, int resetDay) {
        int n;
        block13: {
            DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            try {
                int count = 0;
                PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_event WHERE accId = ? AND eventId = ?");
                ps.setInt(1, this.getAccountId());
                ps.setString(2, eventId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    count = rs.getInt("count");
                    Timestamp updateTime = rs.getTimestamp("updateTime");
                    if (type == 0) {
                        Calendar sqlcal = Calendar.getInstance();
                        if (updateTime != null) {
                            sqlcal.setTimeInMillis(updateTime.getTime());
                        }
                        if (sqlcal.get(5) + resetDay <= Calendar.getInstance().get(5) || sqlcal.get(2) + 1 <= Calendar.getInstance().get(2) || sqlcal.get(1) + 1 <= Calendar.getInstance().get(1)) {
                            count = 0;
                            PreparedStatement psu = con.prepareStatement("UPDATE accounts_event SET count = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND eventId = ?");
                            psu.setInt(1, this.getPlayer().getAccountID());
                            psu.setString(2, eventId);
                            psu.executeUpdate();
                            psu.close();
                        }
                    }
                } else {
                    PreparedStatement psu = con.prepareStatement("INSERT INTO accounts_event (accId, eventId, count, type) VALUES (?, ?, ?, ?)");
                    psu.setInt(1, this.getPlayer().getAccountID());
                    psu.setString(2, eventId);
                    psu.setInt(3, 0);
                    psu.setInt(4, type);
                    psu.executeUpdate();
                    psu.close();
                }
                rs.close();
                ps.close();
                n = count;
                if (con == null) break block13;
            }
            catch (Throwable throwable) {
                try {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception Ex) {
                    log.error("獲取 EventCount 次數.", Ex);
                    return -1;
                }
            }
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return n;
    }

    public void setEventCount(String eventId) {
        this.setEventCount(eventId, 0);
    }

    public void setEventCount(String eventId, int type) {
        this.setEventCount(eventId, type, 1);
    }

    public void setEventCount(String eventId, int type, int count) {
        this.setEventCount(eventId, type, count, 1, true);
    }

    public void setEventCount(String eventId, int type, int count, int date, boolean updateTime) {
        int eventCount = this.getEventCount(eventId, type, date);
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = updateTime ? con.prepareStatement("UPDATE accounts_event SET count = ?, type = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND eventId = ?") : con.prepareStatement("UPDATE accounts_event SET count = ?, type = ? WHERE accId = ? AND eventId = ?");
            ps.setInt(1, eventCount + count);
            ps.setInt(2, type);
            ps.setInt(3, this.getPlayer().getAccountID());
            ps.setString(4, eventId);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception Ex) {
            log.error("增加 EventCount 次數失敗.", Ex);
        }
    }

    public void resetEventCount(String eventId) {
        this.resetEventCount(eventId, 0);
    }

    public void resetEventCount(String eventId, int type) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("UPDATE accounts_event SET count = 0, type = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND eventId = ?");
            ps.setInt(1, type);
            ps.setInt(2, this.getAccountId());
            ps.setString(3, eventId);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception Ex) {
            log.error("重置 EventCount 次數失敗.", Ex);
        }
    }

    public void chatbyEffect(int showMS, String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_Enter_Field_UserChat.getValue());
        mplew.writeInt(0);
        mplew.writeInt(showMS);
        mplew.writeMapleAsciiString(message);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(300);
        mplew.writeInt(1);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        this.getPlayer().send(mplew.getPacket());
    }

    public void isBurning() {
        if (this.getPlayer().getBurningChrTime() > 0L) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.LP_FieldEffect.getValue());
            mplew.write(22);
            mplew.writeMapleAsciiString("Effect/EventEffect2.img/HyperBurning/startEff");
            mplew.writeInt(0);
            mplew.writeInt(-1);
            this.getPlayer().send(mplew.getPacket());
        }
    }

    public void userWzChat(int showTime, String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_AddPopupSay.getValue());
        mplew.writeInt(0);
        mplew.writeInt(showTime);
        mplew.writeMapleAsciiString(message);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(300);
        mplew.writeInt(1);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        this.getPlayer().send(mplew.getPacket());
    }

    public void playTeleport(int posx, int posy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserTeleport.getValue());
        mplew.write(0);
        mplew.write(3);
        mplew.writeInt(this.getPlayer().getId());
        mplew.writeShort(posx);
        mplew.writeShort(posy);
        this.getPlayer().send(mplew.getPacket());
    }

    public void changeJobEffect_Black() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_FadeInOut.getValue());
        mplew.writeInt(500);
        mplew.writeInt(1250);
        mplew.writeInt(2500);
        mplew.write(210);
        this.getPlayer().send(mplew.getPacket());
    }

    public void gainUnionCoins(int count) {
        String point;
        if (count == 0) {
            return;
        }
        if (count > 0) {
            String pt = this.getPlayer().updateWorldShareInfo(18797, "PT");
            if (pt == null || pt.isEmpty() || !pt.matches("^\\d+$")) {
                pt = "0";
            }
            this.getPlayer().updateWorldShareInfo(18797, "PT", String.valueOf(Integer.valueOf(pt) + count));
        }
        if ((point = this.getPlayer().updateWorldShareInfo(500629, "point")) == null || point.isEmpty() || !point.matches("^\\d+$")) {
            point = "0";
        }
        int nPoint = Integer.valueOf(point);
        if ((nPoint += count) < 0) {
            nPoint = 0;
        }
        this.getPlayer().updateWorldShareInfo(500629, "point", String.valueOf(nPoint));
    }
}

