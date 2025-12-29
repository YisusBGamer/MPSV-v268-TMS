/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Config.constants.ItemConstants$卷軸
 *  Packet.AndroidPacket
 *  Server.world.WorldBroadcastService
 *  SwordieX.overseas.extraequip.ExtraEquipResult
 *  connection.packet.OverseasPacket
 */
package Net.server;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleQuestStatus;
import Client.MapleTraitType;
import Client.SecondaryStat;
import Client.inventory.Equip;
import Client.inventory.InventoryException;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.MapleAndroid;
import Client.inventory.MapleInventoryIdentifier;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.inventory.MapleWeapon;
import Client.inventory.ModifyInventory;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.stat.PlayerStats;
import Config.configs.Config;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Config.constants.enums.UserChatMessageType;
import Handler.warpToGameHandler;
import Net.server.AutobanManager;
import Net.server.MapleItemInformationProvider;
import Net.server.StructExclusiveEquip;
import Net.server.StructItemOption;
import Net.server.buffs.MapleStatEffect;
import Net.server.cashshop.CashItemFactory;
import Net.server.cashshop.CashItemInfo;
import Net.server.quest.MapleQuest;
import Opcode.header.OutHeader;
import Packet.AndroidPacket;
import Packet.EffectPacket;
import Packet.InventoryPacket;
import Packet.MTSCSPacket;
import Packet.MaplePacketCreator;
import Server.world.WorldBroadcastService;
import SwordieX.overseas.extraequip.ExtraEquipResult;
import connection.packet.OverseasPacket;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

public class MapleInventoryManipulator {
    private static final Logger log = LoggerFactory.getLogger("ItemLog");

    public static void addRing(MapleCharacter chr, int itemId, int ringId, int sn) {
        CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        Item ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getSN() != ringId || ring.getSN() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.send(MTSCSPacket.CashItemBuyDone(ring, sn, chr.getClient().getAccID()));
    }

    public static boolean addbyItem(MapleClient c, Item item) {
        return MapleInventoryManipulator.addbyItem(c, item, false) >= 0;
    }

    public static short addbyItem(MapleClient c, Item item, boolean fromcs) {
        if (item.getSN() <= 0) {
            item.setSN(MapleInventoryManipulator.getUniqueId(item.getItemId(), item.getSN()));
        }
        MapleInventoryType type = ItemConstants.getInventoryType(item.getItemId());
        short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.announce(InventoryPacket.getInventoryFull());
                c.announce(InventoryPacket.getShowInventoryFull());
            }
            return newSlot;
        }
        if (ItemConstants.類型.採集道具(item.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        if (item.hasSetOnlyId()) {
            item.setSN(MapleInventoryIdentifier.getInstance());
        }
        if (ItemConstants.類型.秘法符文(item.getItemId())) {
            ((Equip)item).setNewArcInfo(c.getPlayer().getJob());
        }
        if (ItemConstants.類型.真實符文(item.getItemId())) {
            ((Equip)item).setNewAutInfo(c.getPlayer().getJob());
        }
        if (ItemConstants.類型.寵物(item.getItemId())) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MaplePet pet = item.getPet();
            if (pet == null) {
                pet = MaplePet.createPet(item.getItemId(), item.getSN());
                item.setPet(pet);
                pet.setInventoryPosition(newSlot);
            }
            item.setSN(MapleInventoryManipulator.getUniqueId(item.getItemId(), pet == null ? item.getSN() : pet.getUniqueId()));
            if (ii.getLife(item.getItemId()) == 0) {
                item.setExpiration(-1L);
            } else if (item.getExpiration() <= 0L && item.getExpiration() != -1L) {
                item.setExpiration(System.currentTimeMillis() + (long)(ii.getLife(item.getItemId()) * 24 * 60 * 60) * 1000L);
            }
            c.announce(InventoryPacket.modifyInventory(false, Collections.singletonList(new ModifyInventory(0, item))));
        } else {
            c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, item))));
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (!fromcs && (type.equals((Object)MapleInventoryType.EQUIP) || type.equals((Object)MapleInventoryType.DECORATION))) {
            c.getPlayer().checkCopyItems();
        }
        return newSlot;
    }

    public static int getUniqueId(int itemId, int uniqueid) {
        if (uniqueid > -1) {
            return uniqueid;
        }
        if (ItemConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        return uniqueid;
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, String gmLog) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, null, null, 0L, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, int state, String gmLog) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, null, null, 0L, state, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, long period, String gmLog) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, null, null, period, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, long period, int state, String gmLog) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, null, null, period, state, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, String owner, String gmLog) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, owner, null, 0L, 0, gmLog);
    }

    public static byte addId(MapleClient c, int itemId, int quantity, String owner, String gmLog) {
        return MapleInventoryManipulator.addId(c, itemId, quantity, owner, null, 0L, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, String owner, MaplePet pet, String gmLog) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, owner, pet, 0L, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, String owner, MaplePet pet, long period, String gmLog) {
        return MapleInventoryManipulator.addById(c, itemId, quantity, owner, pet, period, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, int quantity, String owner, MaplePet pet, long period, int state, String gmLog) {
        return MapleInventoryManipulator.addId(c, itemId, quantity, owner, pet, period, state, gmLog) >= 0;
    }

    public static byte addId(MapleClient c, int itemId, int quantity, String owner, MaplePet pet, long period, int state, String gmLog) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false) || !ii.itemExists(itemId)) {
            c.announce(InventoryPacket.getInventoryFull());
            c.announce(InventoryPacket.showItemUnavailable());
            return -1;
        }
        if (ItemConstants.類型.寵物(itemId) && pet == null) {
            if (c.getPlayer().isDebug()) {
                c.getPlayer().dropMessage(6, "增加道具出錯, 道具是寵物, 可是沒有傳入寵物實例。");
            }
            return -1;
        }
        long comparePeriod = -1L;
        if (period > 0L) {
            long cur = System.currentTimeMillis();
            if (period < 1000L) {
                period = period * 24L * 60L * 60L * 1000L + cur;
            } else if (period < cur) {
                period += cur;
            }
            comparePeriod = period / 1000L * 1000L;
        }
        MapleInventoryType type = ItemConstants.getInventoryType(itemId);
        int uniqueid = MapleInventoryManipulator.getUniqueId(itemId, pet == null ? -1 : pet.getUniqueId());
        short newSlot = -1;
        if (!type.equals((Object)MapleInventoryType.EQUIP) && !type.equals((Object)MapleInventoryType.DECORATION)) {
            short slotMax = ii.getSlotMax(itemId);
            List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!ItemConstants.類型.可充值道具(itemId)) {
                if (existing.size() > 0) {
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        Item eItem = i.next();
                        short oldQ = eItem.getQuantity();
                        if (oldQ >= slotMax || !eItem.getOwner().equals(owner) && owner != null || eItem.getExpiration() != comparePeriod) continue;
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ - oldQ;
                        eItem.setQuantity(newQ);
                        c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(1, eItem))));
                        newSlot = eItem.getPosition();
                    }
                }
                while (quantity > 0) {
                    short newQ = (short)Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        Item nItem = new Item(itemId, (short)0, (short)newQ, 0, uniqueid, (short)0);
                        newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1) {
                            c.announce(InventoryPacket.getInventoryFull());
                            c.announce(InventoryPacket.getShowInventoryFull());
                            return -1;
                        }
                        if (gmLog != null) {
                            nItem.setGMLog(gmLog);
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        if (period > 0L) {
                            nItem.setExpiration(period);
                        }
                        if (pet != null) {
                            nItem.setPet(pet);
                            pet.setInventoryPosition(newSlot);
                        }
                        c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
                        if (!ItemConstants.類型.可充值道具(itemId) || quantity != 0) continue;
                        break;
                    }
                    c.getPlayer().havePartyQuest(itemId);
                    c.sendEnableActions();
                    return (byte)newSlot;
                }
            } else {
                Item nItem = new Item(itemId, (short)0, (short)Math.min(quantity, slotMax), 0, uniqueid, (short)0);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.announce(InventoryPacket.getInventoryFull());
                    c.announce(InventoryPacket.getShowInventoryFull());
                    return -1;
                }
                if (period > 0L) {
                    nItem.setExpiration(period);
                }
                if (gmLog != null) {
                    nItem.setGMLog(gmLog);
                }
                c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
            }
        } else if (quantity == 1) {
            Equip nEquip = ii.getEquipById(itemId, uniqueid);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            if (gmLog != null) {
                nEquip.setGMLog(gmLog);
            }
            if (period > 0L) {
                nEquip.setExpiration(period);
            }
            if (state > 0) {
                ii.setPotentialState(nEquip, state);
            }
            if (nEquip.hasSetOnlyId()) {
                nEquip.setSN(MapleInventoryIdentifier.getInstance());
            }
            if (ItemConstants.類型.秘法符文(nEquip.getItemId())) {
                nEquip.setNewArcInfo(c.getPlayer().getJob());
            }
            if (ItemConstants.類型.真實符文(nEquip.getItemId())) {
                nEquip.setNewAutInfo(c.getPlayer().getJob());
            }
            if ((newSlot = (short)c.getPlayer().getInventory(type).addItem(nEquip)) == -1) {
                c.announce(InventoryPacket.getInventoryFull());
                c.announce(InventoryPacket.getShowInventoryFull());
                return -1;
            }
            c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nEquip))));
            if (ItemConstants.類型.採集道具(itemId)) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            c.getPlayer().checkCopyItems();
        } else {
            throw new InventoryException("Trying to create equip with non-one quantity");
        }
        c.getPlayer().havePartyQuest(itemId);
        return (byte)newSlot;
    }

    public static Item addbyId_Gachapon(MapleClient c, int itemId, int quantity) {
        return MapleInventoryManipulator.addbyId_Gachapon(c, itemId, quantity, null, 0L);
    }

    public static Item addbyId_Gachapon(MapleClient c, int itemId, int quantity, String gmLog) {
        return MapleInventoryManipulator.addbyId_Gachapon(c, itemId, quantity, null, 0L);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Item addbyId_Gachapon(MapleClient c, int itemId, int quantity, String gmLog, long period) {
        MapleInventoryType type;
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.DECORATION).getNextFreeSlot() == -1) {
            return null;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false) || !ii.itemExists(itemId)) {
            c.announce(InventoryPacket.getInventoryFull());
            c.announce(InventoryPacket.showItemUnavailable());
            return null;
        }
        long comparePeriod = -1L;
        if (period > 0L) {
            period = period < 1000L ? period * 24L * 60L * 60L * 1000L + System.currentTimeMillis() : (period += System.currentTimeMillis());
            comparePeriod = period / 1000L * 1000L;
        }
        if (!(type = ItemConstants.getInventoryType(itemId)).equals((Object)MapleInventoryType.EQUIP) && !type.equals((Object)MapleInventoryType.DECORATION)) {
            short slotMax = ii.getSlotMax(itemId);
            List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!ItemConstants.類型.可充值道具(itemId)) {
                short newQ;
                Item nItem = null;
                boolean recieved = false;
                if (existing.size() > 0) {
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        nItem = i.next();
                        short oldQ = nItem.getQuantity();
                        if (oldQ >= slotMax || nItem.getOwner() != null && !nItem.getOwner().isEmpty() || nItem.getExpiration() != comparePeriod) continue;
                        recieved = true;
                        short newQ2 = (short)Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ2 - oldQ;
                        nItem.setQuantity(newQ2);
                        c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(1, nItem))));
                    }
                }
                while (quantity > 0 && (newQ = (short)Math.min(quantity, slotMax)) != 0) {
                    quantity -= newQ;
                    nItem = new Item(itemId, (short)0, (short)newQ, 0, MapleInventoryManipulator.getUniqueId(itemId, -1), (short)0);
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1 && recieved) {
                        return nItem;
                    }
                    if (newSlot == -1) {
                        return null;
                    }
                    recieved = true;
                    if (gmLog != null) {
                        nItem.setGMLog(gmLog);
                    }
                    if (period > 0L) {
                        nItem.setExpiration(period);
                    }
                    c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
                    if (!ItemConstants.類型.可充值道具(itemId) || quantity != 0) continue;
                    break;
                }
                if (!recieved || nItem == null) return null;
                c.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
            Item nItem = new Item(itemId, (short)0, (short)Math.min(quantity, slotMax), 0, MapleInventoryManipulator.getUniqueId(itemId, -1), (short)0);
            short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
            if (newSlot == -1) {
                return null;
            }
            if (gmLog != null) {
                nItem.setGMLog(gmLog);
            }
            if (period > 0L) {
                nItem.setExpiration(period);
            }
            c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nItem))));
            c.getPlayer().havePartyQuest(nItem.getItemId());
            return nItem;
        }
        if (quantity != 1) throw new InventoryException("Trying to create equip with non-one quantity");
        Equip nEquip = ii.randomizeStats(ii.getEquipById(itemId));
        short newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
        if (newSlot == -1) {
            return null;
        }
        if (gmLog != null) {
            nEquip.setGMLog(gmLog);
        }
        if (period > 0L) {
            nEquip.setExpiration(period);
        }
        if (nEquip.hasSetOnlyId()) {
            nEquip.setSN(MapleInventoryIdentifier.getInstance());
        }
        c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, nEquip))));
        c.getPlayer().havePartyQuest(nEquip.getItemId());
        return nEquip;
    }

    public static boolean addFromDrop(MapleClient c, Item item, boolean show) {
        return MapleInventoryManipulator.addFromDrop(c, item, show, false);
    }

    public static boolean addFromDrop(MapleClient c, Item item, boolean show, boolean enhance) {
        return MapleInventoryManipulator.addFromDrop(c, item, show, enhance, true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean addFromDrop(MapleClient c, Item item, boolean show, boolean enhance, boolean updateTick) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c.getPlayer() == null || ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false) || !ii.itemExists(item.getItemId())) {
            c.announce(InventoryPacket.getInventoryFull());
            c.announce(InventoryPacket.showItemUnavailable());
            return false;
        }
        int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        MapleInventoryType type = ItemConstants.getInventoryType(item.getItemId());
        if (ItemConstants.類型.寵物(item.getItemId())) {
            short newSlot;
            if (quantity != 1) throw new RuntimeException("玩家[" + c.getPlayer().getName() + "] 獲得寵物但寵物的數量不為1 寵物ID: " + item.getItemId());
            long period = item.getTrueExpiration();
            MaplePet pet = item.getPet();
            if (pet == null) {
                pet = MaplePet.createPet(item.getItemId());
                if (pet != null && period == -1L && ii.getLife(item.getItemId()) > 0) {
                    period = System.currentTimeMillis() + (long)(ii.getLife(item.getItemId()) * 24 * 60) * 1000L;
                }
                item.setPet(pet);
                item.setExpiration(period);
            }
            if ((newSlot = c.getPlayer().getInventory(type).addItem(item)) == -1) {
                c.announce(InventoryPacket.getInventoryFull());
                c.announce(InventoryPacket.getShowInventoryFull());
                return false;
            }
            if (item.getPet() != null) {
                item.getPet().setInventoryPosition(item.getPosition());
            }
            c.announce(InventoryPacket.modifyInventory(updateTick, Collections.singletonList(new ModifyInventory(0, item)), c.getPlayer()));
        } else if (!type.equals((Object)MapleInventoryType.EQUIP) && !type.equals((Object)MapleInventoryType.DECORATION)) {
            short slotMax = ii.getSlotMax(item.getItemId());
            List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!ItemConstants.類型.可充值道具(item.getItemId())) {
                if (quantity <= 0) {
                    c.announce(InventoryPacket.getInventoryFull());
                    c.announce(InventoryPacket.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0 && i.hasNext()) {
                        Item eItem = i.next();
                        short oldQ = eItem.getQuantity();
                        if (oldQ >= slotMax || !item.getOwner().equals(eItem.getOwner()) || item.getExpiration() != eItem.getExpiration() || item.getFamiliarCard() != null) continue;
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity = (short)(quantity - (newQ - oldQ));
                        eItem.setQuantity(newQ);
                        eItem.setSN(item.getSN());
                        c.announce(InventoryPacket.modifyInventory(updateTick, Collections.singletonList(new ModifyInventory(1, eItem)), c.getPlayer()));
                    }
                }
                while (quantity > 0) {
                    short newSlot;
                    short newQ = (short)Math.min(quantity, slotMax);
                    quantity = (short)(quantity - newQ);
                    Item nItem = new Item(item.getItemId(), (short)0, (short)newQ, item.getAttribute(), MapleInventoryManipulator.getUniqueId(item.getItemId(), item.getSN()), (short)0);
                    nItem.setExpiration(item.getTrueExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setGMLog(item.getGMLog());
                    nItem.setFamiliarCard(item.getFamiliarCard());
                    nItem.setFamiliarid(item.getFamiliarid());
                    if (item.getSN() != -1) {
                        nItem.setSN(item.getSN());
                    }
                    if ((newSlot = c.getPlayer().getInventory(type).addItem(nItem)) == -1) {
                        c.announce(InventoryPacket.getInventoryFull());
                        c.announce(InventoryPacket.getShowInventoryFull());
                        item.setQuantity((short)(quantity + newQ));
                        return false;
                    }
                    c.announce(InventoryPacket.modifyInventory(updateTick, Collections.singletonList(new ModifyInventory(0, nItem)), c.getPlayer()));
                }
            } else {
                short newSlot;
                Item nItem = new Item(item.getItemId(), (short)0, (short)quantity, item.getAttribute(), MapleInventoryManipulator.getUniqueId(item.getItemId(), item.getSN()), (short)0);
                nItem.setExpiration(item.getTrueExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                nItem.setGMLog(item.getGMLog());
                nItem.setFamiliarCard(item.getFamiliarCard());
                nItem.setFamiliarid(item.getFamiliarid());
                if (item.getSN() != -1) {
                    nItem.setSN(item.getSN());
                }
                if ((newSlot = c.getPlayer().getInventory(type).addItem(nItem)) == -1) {
                    c.announce(InventoryPacket.getInventoryFull());
                    c.announce(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                if (item.getPet() != null) {
                    item.getPet().setInventoryPosition(newSlot);
                }
                c.announce(InventoryPacket.modifyInventory(updateTick, Collections.singletonList(new ModifyInventory(0, nItem)), c.getPlayer()));
                c.sendEnableActions();
            }
        } else {
            short newSlot;
            if (quantity != 1) throw new RuntimeException("玩家[" + c.getPlayer().getName() + "] 獲得裝備但裝備的數量不為1 裝備ID: " + item.getItemId());
            if (enhance) {
                item = MapleInventoryManipulator.checkEnhanced(item, c.getPlayer());
            }
            if (ItemConstants.類型.秘法符文(item.getItemId())) {
                ((Equip)item).setNewArcInfo(c.getPlayer().getJob());
            }
            if (ItemConstants.類型.真實符文(item.getItemId())) {
                ((Equip)item).setNewAutInfo(c.getPlayer().getJob());
            }
            if (item.hasSetOnlyId()) {
                item.setSN(MapleInventoryIdentifier.getInstance());
            }
            if ((newSlot = c.getPlayer().getInventory(type).addItem(item)) == -1) {
                c.announce(InventoryPacket.getInventoryFull());
                c.announce(InventoryPacket.getShowInventoryFull());
                return false;
            }
            c.announce(InventoryPacket.modifyInventory(updateTick, Collections.singletonList(new ModifyInventory(0, item)), c.getPlayer()));
            if (ItemConstants.類型.採集道具(item.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            c.getPlayer().checkCopyItems();
        }
        if (item.getQuantity() >= 50 && item.getItemId() == 2340000) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4001128: {
                    break;
                }
                case 4001246: {
                    break;
                }
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        switch (item.getType()) {
            case 1: {
                c.announce(EffectPacket.getShowItemGain(item.getItemId(), item.getQuantity(), false));
                return true;
            }
            case 2: 
            case 4: 
            case 5: 
            case 6: {
                c.announce(MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
            }
        }
        return true;
    }

    public static boolean addItemAndEquip(MapleClient c, int itemId, short slot) {
        return MapleInventoryManipulator.addItemAndEquip(c, itemId, slot, 0);
    }

    public static boolean addItemAndEquip(MapleClient c, int itemId, short slot, boolean removeItem) {
        return MapleInventoryManipulator.addItemAndEquip(c, itemId, slot, 0, removeItem);
    }

    public static boolean addItemAndEquip(MapleClient c, int itemId, short slot, int state) {
        return MapleInventoryManipulator.addItemAndEquip(c, itemId, slot, state, true);
    }

    public static boolean addItemAndEquip(MapleClient c, int itemId, short slot, int state, boolean removeItem) {
        return MapleInventoryManipulator.addItemAndEquip(c, itemId, slot, null, 0L, state, "系統贈送 時間: " + DateUtil.getCurrentDate(), removeItem);
    }

    public static boolean addItemAndEquip(MapleClient c, int itemId, short slot, int state, String gmLog) {
        return MapleInventoryManipulator.addItemAndEquip(c, itemId, slot, null, 0L, state, gmLog, true);
    }

    public static boolean addItemAndEquip(MapleClient c, int itemId, short slot, String owner, long period, int state, String gmLog, boolean removeItem) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleInventoryType type = ItemConstants.getInventoryType(itemId);
        if (!ii.itemExists(itemId) || !type.equals((Object)MapleInventoryType.EQUIP) && !type.equals((Object)MapleInventoryType.DECORATION)) {
            c.sendEnableActions();
            return false;
        }
        Equip nEquip = ii.getEquipById(itemId);
        return MapleInventoryManipulator.addItemAndEquip(c, nEquip, slot, owner, period, state, gmLog, removeItem);
    }

    public static boolean addItemAndEquip(MapleClient c, Equip equip, short slot, String owner, long period, int state, String gmLog, boolean removeItem) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (slot > 0) {
            c.sendEnableActions();
            return false;
        }
        Item toRemove = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        if (toRemove != null) {
            if (removeItem) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIPPED, toRemove.getPosition(), toRemove.getQuantity(), false);
            } else {
                short nextSlot = c.getPlayer().getInventory(ItemConstants.getInventoryType(toRemove.getItemId())).getNextFreeSlot();
                if (nextSlot > -1) {
                    MapleInventoryManipulator.unequip(c, toRemove.getPosition(), nextSlot);
                }
            }
        }
        if (owner != null) {
            equip.setOwner(owner);
        }
        if (gmLog != null) {
            equip.setGMLog(gmLog);
        }
        if (period > 0L) {
            if (period < 1000L) {
                equip.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            } else {
                equip.setExpiration(System.currentTimeMillis() + period);
            }
        }
        if (state > 0) {
            ii.setPotentialState(equip, state);
        }
        if (ItemConstants.類型.秘法符文(equip.getItemId())) {
            equip.setNewArcInfo(c.getPlayer().getJob());
        }
        if (ItemConstants.類型.真實符文(equip.getItemId())) {
            equip.setNewAutInfo(c.getPlayer().getJob());
        }
        if (equip.hasSetOnlyId()) {
            equip.setSN(MapleInventoryIdentifier.getInstance());
        }
        equip.setPosition(slot);
        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(0, equip))));
        return true;
    }

    private static Item checkEnhanced(Item before, MapleCharacter chr) {
        Equip eq;
        if (before instanceof Equip && (eq = (Equip)before).getState(false) == 0 && (eq.getRestUpgradeCount() >= 1 || eq.getCurrentUpgradeCount() >= 1) && ItemConstants.卷軸.canScroll((int)eq.getItemId()) && Randomizer.nextInt(100) >= 90) {
            eq.renewPotential(false);
        }
        return before;
    }

    public static boolean checkSpace(MapleClient c, int itemid, int quantity, String owner) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c == null || c.getPlayer() == null || ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false) || !ii.itemExists(itemid)) {
            return false;
        }
        if (quantity <= 0 && !ItemConstants.類型.可充值道具(itemid)) {
            return false;
        }
        MapleInventoryType type = ItemConstants.getInventoryType(itemid);
        if (c.getPlayer().getInventory(type) == null) {
            return false;
        }
        if (!type.equals((Object)MapleInventoryType.EQUIP) && !type.equals((Object)MapleInventoryType.DECORATION)) {
            short slotMax = ii.getSlotMax(itemid);
            List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
            if (!ItemConstants.類型.可充值道具(itemid) && existing.size() > 0) {
                for (Item eItem : existing) {
                    short oldQ = eItem.getQuantity();
                    if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ - oldQ;
                    }
                    if (quantity > 0) continue;
                    break;
                }
            }
            int numSlotsNeeded = slotMax > 0 && !ItemConstants.類型.可充值道具(itemid) ? (int)Math.ceil((double)quantity / (double)slotMax) : 1;
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        }
        return !c.getPlayer().getInventory(type).isFull(quantity - 1);
    }

    public static Item removeFromSlotCopy(MapleClient c, MapleInventoryType type, short slot, short quantity) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return null;
        }
        Item item = c.getPlayer().getInventory(type).getItem(slot);
        Item copy = null;
        if (item != null) {
            copy = item.copy();
            if (ItemConstants.類型.可充值道具(item.getItemId())) {
                quantity = item.getQuantity();
            }
            c.getPlayer().getInventory(type).removeItem(slot, quantity, false);
            if (item.getQuantity() <= 0 || item.getType() != 2) {
                c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(3, item))));
            } else {
                copy.setQuantity(quantity);
                copy.setSN(MapleInventoryIdentifier.getInstance());
                c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(1, item))));
            }
        }
        return copy;
    }

    public static boolean removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop) {
        return MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }

    public static boolean removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            if ((item.getItemId() == 5370000 || item.getItemId() == 5370001) && c.getPlayer().getChalkboard() != null) {
                c.getPlayer().setChalkboard(null);
            }
            boolean allowZero = consume && ItemConstants.類型.可充值道具(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (ItemConstants.類型.採集道具(item.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            if (item.getQuantity() == 0 && !allowZero) {
                c.announce(InventoryPacket.modifyInventory(fromDrop, Collections.singletonList(new ModifyInventory(3, item))));
            } else {
                c.announce(InventoryPacket.modifyInventory(fromDrop, Collections.singletonList(new ModifyInventory(1, item))));
            }
            return true;
        }
        return false;
    }

    public static boolean removeById(MapleClient c, MapleInventoryType type, int itemId, int quantity, boolean fromDrop, boolean consume) {
        int remremove = quantity;
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            short theQ = item.getQuantity();
            if (remremove <= theQ && MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), (short)remremove, fromDrop, consume)) {
                remremove = 0;
                break;
            }
            if (remremove <= theQ || !MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume)) continue;
            remremove -= theQ;
        }
        return remremove <= 0;
    }

    public static boolean removeFromSlot_Lock(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            return !ItemAttribute.Seal.check(item.getAttribute()) && !ItemAttribute.TradeBlock.check(item.getCAttribute()) && MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, fromDrop, consume);
        }
        return false;
    }

    public static boolean removeById_Lock(MapleClient c, MapleInventoryType type, int itemId) {
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (!MapleInventoryManipulator.removeFromSlot_Lock(c, type, item.getPosition(), (short)1, false, false)) continue;
            return true;
        }
        return false;
    }

    public static void removeAllById(MapleClient c, int itemId, boolean checkEquipped) {
        Item ii;
        MapleInventoryType type = ItemConstants.getInventoryType(itemId);
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (item == null) continue;
            MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), true, false);
        }
        if (checkEquipped && (ii = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(itemId)) != null) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIPPED, ii.getPosition(), ii.getQuantity(), true, false);
            c.getPlayer().equipChanged();
        }
    }

    public static void removeAll(MapleClient c, MapleInventoryType type) {
        ArrayList<ModifyInventory> mods = new ArrayList<ModifyInventory>();
        for (Item item : c.getPlayer().getInventory(type).list()) {
            if (item == null) continue;
            mods.add(new ModifyInventory(3, item));
        }
        if (!mods.isEmpty()) {
            c.announce(InventoryPacket.modifyInventory(false, mods));
        }
        c.getPlayer().getInventory(type).removeAll();
    }

    public static void removeAllBySN(MapleClient c, int sn) {
        if (c.getPlayer() == null) {
            return;
        }
        boolean locked = false;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Item> copyEquipItems = c.getPlayer().getInventory(MapleInventoryType.EQUIP).listBySN(sn);
        for (Item item : copyEquipItems) {
            if (item == null) continue;
            if (!locked) {
                int flag = item.getAttribute();
                flag |= ItemAttribute.Seal.getValue();
                flag |= ItemAttribute.TradeBlock.getValue();
                item.setAttribute(flag |= ItemAttribute.Crafted.getValue());
                item.setOwner("複製裝備");
                c.getPlayer().forceUpdateItem(item);
                c.getPlayer().dropMessage(-11, "在背包中發現複製裝備[" + ii.getName(item.getItemId()) + "]已經將其鎖定。");
                String msgtext = "玩家 " + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等級 " + c.getPlayer().getLevel() + ") 地圖: " + c.getPlayer().getMapId() + " 在玩家背包中發現複製裝備[" + ii.getName(item.getItemId()) + "]已經將其鎖定。";
                WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM消息] " + msgtext));
                log.warn(msgtext + " 道具唯一ID: " + item.getSN());
                locked = true;
                continue;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item.getPosition(), item.getQuantity(), true, false);
            c.getPlayer().dropMessage(-11, "在背包中發現複製裝備[" + ii.getName(item.getItemId()) + "]已經將其刪除。");
        }
        List<Item> copyEquipedItems = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).listBySN(sn);
        for (Item item : copyEquipedItems) {
            if (item == null) continue;
            if (!locked) {
                int flag = item.getAttribute();
                flag |= ItemAttribute.Seal.getValue();
                flag |= ItemAttribute.TradeBlock.getValue();
                item.setAttribute(flag |= ItemAttribute.Crafted.getValue());
                item.setOwner("複製裝備");
                c.getPlayer().forceUpdateItem(item);
                c.getPlayer().dropMessage(-11, "在穿戴中發現複製裝備[" + ii.getName(item.getItemId()) + "]已經將其鎖定。");
                String msgtext = "玩家 " + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等級 " + c.getPlayer().getLevel() + ") 地圖: " + c.getPlayer().getMapId() + " 在玩家穿戴中發現複製裝備[" + ii.getName(item.getItemId()) + "]已經將其鎖定。";
                WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM消息] " + msgtext));
                log.warn(msgtext + " 道具唯一ID: " + item.getSN());
                locked = true;
                continue;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIPPED, item.getPosition(), item.getQuantity(), true, false);
            c.getPlayer().dropMessage(-11, "在穿戴中發現複製裝備[" + ii.getName(item.getItemId()) + "]已經將其刪除。");
            c.getPlayer().equipChanged();
        }
        List<Item> list = c.getPlayer().getInventory(MapleInventoryType.DECORATION).listBySN(sn);
        for (Item item : list) {
            if (item == null) continue;
            if (!locked) {
                int flag = item.getAttribute();
                flag |= ItemAttribute.Seal.getValue();
                flag |= ItemAttribute.TradeBlock.getValue();
                item.setAttribute(flag |= ItemAttribute.Crafted.getValue());
                item.setOwner("複製裝備");
                c.getPlayer().forceUpdateItem(item);
                c.getPlayer().dropMessage(-11, "在背包中發現複製時裝[" + ii.getName(item.getItemId()) + "]已經將其鎖定。");
                String msgtext = "玩家 " + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等級 " + c.getPlayer().getLevel() + ") 地圖: " + c.getPlayer().getMapId() + " 在玩家背包中發現複製時裝[" + ii.getName(item.getItemId()) + "]已經將其鎖定。";
                WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM消息] " + msgtext));
                log.warn(msgtext + " 道具唯一ID: " + item.getSN());
                locked = true;
                continue;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.DECORATION, item.getPosition(), item.getQuantity(), true, false);
            c.getPlayer().dropMessage(-11, "在背包中發現複製時裝[" + ii.getName(item.getItemId()) + "]已經將其刪除。");
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    public static void move(MapleClient c, MapleInventoryType type, short src, short dst) {
        if (src < 0 || dst < 0 || src == dst || type == MapleInventoryType.EQUIPPED) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Item source = c.getPlayer().getInventory(type).getItem(src);
        Item initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            c.getPlayer().dropMessage(1, "移動道具失敗，找不到移動道具的信息。");
            c.sendEnableActions();
            return;
        }
        boolean bag = false;
        boolean switchSrcDst = false;
        boolean bothBag = false;
        short eqIndicator = -1;
        ArrayList<ModifyInventory> mods = new ArrayList<ModifyInventory>();
        if (dst > c.getPlayer().getInventory(type).getSlotLimit()) {
            if ((type == MapleInventoryType.ETC || type == MapleInventoryType.SETUP || type == MapleInventoryType.USE) && dst > 10000 && dst % 10000 != 0) {
                int eId = c.getPlayer().getExtendedItemId(type.getType(), dst % 1000 / 100 - 1);
                if (eId <= 0) {
                    c.getPlayer().dropMessage(1, "無法將該道具移動到小背包.");
                    c.sendEnableActions();
                    return;
                }
                MapleStatEffect itemEffect = ii.getItemEffect(eId);
                boolean canMove = false;
                block0 : switch (type.getType()) {
                    case 2: {
                        switch (itemEffect.getType()) {
                            case 1: {
                                canMove = source.getItemId() / 10000 == 251;
                                break;
                            }
                            case 2: {
                                canMove = source.getItemId() / 1000 == 2591 || source.getItemId() / 1000000 == 2 && itemEffect.getType() == ii.getBagType(source.getItemId());
                                break;
                            }
                            case 3: {
                                canMove = source.getItemId() / 10000 == 204;
                                break;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (itemEffect.getType()) {
                            case 1: {
                                canMove = source.getItemId() / 10000 == 370;
                                break;
                            }
                            case 2: {
                                canMove = source.getItemId() / 10000 == 301 || source.getItemId() / 10000 == 302;
                                break;
                            }
                        }
                        break;
                    }
                    case 4: {
                        switch (itemEffect.getType()) {
                            case 5: {
                                canMove = source.getItemId() / 10000 == 431;
                                break block0;
                            }
                        }
                        canMove = source.getItemId() / 1000000 == 4 && itemEffect.getType() == ii.getBagType(source.getItemId());
                        break;
                    }
                }
                if (dst % 100 > itemEffect.getSlotCount() || itemEffect.getType() <= 0 || !canMove) {
                    c.getPlayer().dropMessage(1, "無法將該道具移動到小背包.");
                    c.sendEnableActions();
                    return;
                }
                eqIndicator = 0;
                bag = true;
            } else {
                c.getPlayer().dropMessage(1, "無法進行此操作.");
                c.sendEnableActions();
                return;
            }
        }
        if (src > c.getPlayer().getInventory(type).getSlotLimit() && (type == MapleInventoryType.ETC || type == MapleInventoryType.SETUP || type == MapleInventoryType.USE) && src > 10000 && src % 10000 != 0) {
            if (!bag) {
                switchSrcDst = true;
                eqIndicator = 0;
                bag = true;
            } else {
                bothBag = true;
            }
        }
        int olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        short oldsrcQ = source.getQuantity();
        short slotMax = ii.getSlotMax(source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        if (ItemConstants.類型.採集道具(source.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        if (!(type.equals((Object)MapleInventoryType.EQUIP) || type.equals((Object)MapleInventoryType.DECORATION) || initialTarget == null || initialTarget.getItemId() != source.getItemId() || !initialTarget.getOwner().equals(source.getOwner()) || initialTarget.getExpiration() != source.getExpiration() || ItemConstants.類型.可充值道具(source.getItemId()) || type.equals((Object)MapleInventoryType.CASH))) {
            if (olddstQ + oldsrcQ > slotMax) {
                mods.add(new ModifyInventory(bag && (switchSrcDst || bothBag) ? 7 : 1, source));
                mods.add(new ModifyInventory(bag && (switchSrcDst || bothBag) ? 7 : 1, initialTarget));
            } else {
                mods.add(new ModifyInventory(bag && (switchSrcDst || bothBag) ? 8 : 3, source));
                mods.add(new ModifyInventory(bag && (!switchSrcDst || bothBag) ? 7 : 1, initialTarget));
            }
        } else {
            mods.add(new ModifyInventory(bag ? (bothBag ? 9 : 6) : 2, source, src, eqIndicator, switchSrcDst));
        }
        c.announce(InventoryPacket.modifyInventory(true, mods));
    }

    public static void equip(MapleClient c, MapleInventoryType iType, short src, short dst) {
        Equip equiped;
        StructExclusiveEquip exclusive;
        MapleQuestStatus stat;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        PlayerStats statst = chr.getStat();
        Equip source = (Equip)chr.getInventory(iType).getItem(src);
        if (source == null || source.getDurability() == 0 || ItemConstants.類型.採集道具(source.getItemId())) {
            c.sendEnableActions();
            return;
        }
        if (chr.isAdmin() && Config.isDevelop()) {
            chr.dropMessage(5, "穿戴裝備  " + source.getItemId() + " src: " + src + " dst: " + dst);
        }
        if (!(source.getItemId() != 1003142 && source.getItemId() != 1002140 && source.getItemId() != 1042003 && source.getItemId() != 1062007 && source.getItemId() != 1322013 && source.getItemId() != 1003824 || chr.isIntern())) {
            chr.dropMessage(1, "無法佩帶此物品");
            log.info("[作弊] 非管理員玩家: " + chr.getName() + " 非法穿戴GM裝備 " + source.getItemId());
            MapleInventoryManipulator.removeById(c, iType, source.getItemId(), 1, true, false);
            AutobanManager.getInstance().autoban(chr.getClient(), "非法穿戴GM裝備");
            c.sendEnableActions();
            return;
        }
        if (!ii.itemExists(source.getItemId())) {
            c.sendEnableActions();
            return;
        }
        if (dst > -1200 && dst < -999 && !ItemConstants.類型.龍裝備(source.getItemId()) && !ItemConstants.類型.機械(source.getItemId())) {
            if (chr.isAdmin()) {
                chr.dropMessage(5, "穿戴裝備 - 1 " + source.getItemId());
            }
            c.sendEnableActions();
            return;
        }
        if ((dst > -6000 && dst < -5002 || dst >= -999 && dst < -99) && !ii.isCash(source.getItemId()) && dst != -5200) {
            if (chr.isAdmin()) {
                chr.dropMessage(5, "穿戴裝備 - 2 " + source.getItemId() + " dst: " + dst + " 檢測1: " + (dst <= -1200) + " 檢測2: " + (dst >= -999 && dst < -99) + " 檢測3: " + !ii.isCash(source.getItemId()));
            }
            c.sendEnableActions();
            return;
        }
        if (dst <= -1200 && dst > -1300 && chr.getAndroid() == null) {
            if (chr.isAdmin()) {
                chr.dropMessage(5, "穿戴裝備 - 3 " + source.getItemId() + " dst: " + dst + " 檢測1: " + (dst <= -1200 && dst > -1300) + " 檢測2: " + (chr.getAndroid() == null));
            }
            c.sendEnableActions();
            return;
        }
        if (dst <= -1300 && dst > -1306 && !JobConstants.is天使破壞者(chr.getJob())) {
            if (chr.isAdmin()) {
                chr.dropMessage(5, "穿戴裝備 - 4 " + source.getItemId() + " dst: " + dst + " 檢測1: " + (dst <= -1300 && dst > -1306) + " 檢測2: " + !JobConstants.is天使破壞者(chr.getJob()));
            }
            c.sendEnableActions();
            return;
        }
        if (!ii.canEquip(source, chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), chr.getStat().getLevelBonus())) {
            if (ServerConfig.WORLD_EQUIPCHECKFAME && chr.getFame() < 0) {
                chr.dropMessage(1, "人氣度小於0，無法穿戴裝備。");
            }
            c.sendEnableActions();
            return;
        }
        if (MapleWeapon.扇子.check(source.getItemId()) && dst != -10 && dst != -11 && dst != -5200) {
            c.sendEnableActions();
            return;
        }
        if (ItemConstants.類型.武器(source.getItemId()) && !MapleWeapon.雙刀.check(source.getItemId()) && dst != -10 && dst != -11 && !MapleWeapon.扇子.check(source.getItemId())) {
            c.sendEnableActions();
            return;
        }
        if (dst == -23 && !GameConstants.isMountItemAvailable(source.getItemId(), chr.getJob())) {
            c.sendEnableActions();
            return;
        }
        if (dst == -118 && source.getItemId() / 10000 != 190) {
            c.sendEnableActions();
            return;
        }
        if (dst == -119 && source.getItemId() / 10000 != 191) {
            c.sendEnableActions();
            return;
        }
        if (dst <= -5000 && dst >= -5002 && source.getItemId() / 10000 != 120) {
            chr.dropMessage(1, "無法將此裝備佩戴這個地方，該位置只能裝備圖騰道具");
            c.sendEnableActions();
            return;
        }
        if (source.getItemId() == 1202193) {
            c.write(warpToGameHandler.EquipRuneSetting());
        }
        if (dst == -5200 && source.getItemId() / 10000 != 155) {
            chr.dropMessage(1, "無法將此裝備佩戴這個地方，該位置只能裝備扇子");
            c.sendEnableActions();
            return;
        }
        if (dst == -31 && source.getItemId() / 10000 != 116) {
            chr.dropMessage(1, "無法將此裝備佩戴這個地方，該位置只能裝備口袋物品道具");
            c.sendEnableActions();
            return;
        }
        if (dst == -34 && source.getItemId() / 10000 != 118) {
            chr.dropMessage(1, "無法將此裝備佩戴這個地方，該位置只能裝備胸章道具");
            c.sendEnableActions();
            return;
        }
        if (dst == -36 && ((stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700))) == null || stat.getCustomData() == null || !"0".equals(stat.getCustomData()) && Long.parseLong(stat.getCustomData()) < System.currentTimeMillis())) {
            c.sendEnableActions();
            return;
        }
        if (MapleWeapon.雙刀.check(source.getItemId()) || source.getItemId() / 10000 == 135) {
            dst = (byte)(ii.isCash(source.getItemId()) ? -110 : -10);
        }
        if (ItemConstants.類型.龍裝備(source.getItemId()) && JobConstants.is龍魔導士(chr.getJob())) {
            c.sendEnableActions();
            return;
        }
        if (ItemConstants.類型.機械(source.getItemId()) && JobConstants.is機甲戰神(chr.getJob())) {
            c.sendEnableActions();
            return;
        }
        if (ii.isExclusiveEquip(source.getItemId()) && (exclusive = ii.getExclusiveEquipInfo(source.getItemId())) != null) {
            List<Integer> theList = chr.getInventory(MapleInventoryType.EQUIPPED).listIds();
            for (Integer i : exclusive.itemIDs) {
                if (!theList.contains(i)) continue;
                chr.dropMessage(1, exclusive.msg);
                c.sendEnableActions();
                return;
            }
        }
        if (ItemConstants.類型.秘法符文(source.getItemId())) {
            boolean exist = false;
            for (short i = -1605; i <= -1600; i = (short)(i + 1)) {
                equiped = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                if (equiped == null || source.getItemId() != equiped.getItemId()) continue;
                exist = true;
            }
            if (exist) {
                c.sendEnableActions();
                return;
            }
        }
        if (ItemConstants.類型.真實符文(source.getItemId())) {
            boolean exist = false;
            for (short i = -1705; i <= -1700; i = (short)(i + 1)) {
                equiped = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                if (equiped == null || source.getItemId() != equiped.getItemId()) continue;
                exist = true;
            }
            if (exist) {
                c.sendEnableActions();
                return;
            }
        }
        Equip target2 = null;
        switch (dst) {
            case -6: {
                target2 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-5);
                if (target2 != null && ItemConstants.類型.套服(target2.getItemId())) {
                    if (!chr.getInventory(iType).isFull(1)) break;
                    c.announce(InventoryPacket.getInventoryFull());
                    c.announce(InventoryPacket.getShowInventoryFull());
                    return;
                }
                target2 = null;
                break;
            }
            case -5: {
                target2 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-6);
                if (target2 != null && ItemConstants.類型.套服(source.getItemId())) {
                    if (!chr.getInventory(iType).isFull(1)) break;
                    c.announce(InventoryPacket.getInventoryFull());
                    c.announce(InventoryPacket.getShowInventoryFull());
                    return;
                }
                target2 = null;
                break;
            }
            case -10: {
                target2 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
                if (MapleWeapon.雙刀.check(source.getItemId())) {
                    if (!(chr.getJob() == 900 || JobConstants.is影武者(chr.getJob()) && target2 != null && MapleWeapon.短劍.check(target2.getItemId()))) {
                        c.announce(InventoryPacket.getInventoryFull());
                        c.announce(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    target2 = null;
                    break;
                }
                if (target2 != null && ItemConstants.類型.雙手武器(target2.getItemId(), chr.getJob()) && !ItemConstants.類型.特殊副手(source.getItemId())) {
                    if (!chr.getInventory(iType).isFull(1)) break;
                    c.announce(InventoryPacket.getInventoryFull());
                    c.announce(InventoryPacket.getShowInventoryFull());
                    return;
                }
                target2 = null;
                break;
            }
            case -11: {
                target2 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
                if (target2 != null && ItemConstants.類型.雙手武器(source.getItemId(), chr.getJob()) && !ItemConstants.類型.特殊副手(target2.getItemId())) {
                    if (!chr.getInventory(iType).isFull(1)) break;
                    c.announce(InventoryPacket.getInventoryFull());
                    c.announce(InventoryPacket.getShowInventoryFull());
                    return;
                }
                target2 = null;
            }
        }
        source = (Equip)chr.getInventory(iType).getItem(src);
        Equip target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null) {
            c.sendEnableActions();
            return;
        }
        boolean itemChanged = false;
        if (ii.isEquipTradeBlock(source.getItemId()) && !ItemAttribute.TradeBlock.check(source.getAttribute())) {
            source.addAttribute(ItemAttribute.TradeBlock.getValue());
            itemChanged = true;
        }
        if (ii.isCash(source.getItemId()) && ItemAttribute.TradeOnce.check(source.getAttribute()) && !ItemAttribute.TradeBlock.check(source.getAttribute())) {
            source.removeAttribute(ItemAttribute.TradeOnce.getValue());
            source.addAttribute(ItemAttribute.TradeBlock.getValue());
            itemChanged = true;
        }
        if (ItemConstants.類型.機器人(source.getItemId())) {
            if (source.getAndroid() == null) {
                int uid = MapleInventoryIdentifier.getInstance();
                source.setSN(uid);
                source.setAndroid(MapleAndroid.create(source.getItemId(), uid));
                source.addAttribute(ItemAttribute.AndroidActivated.getValue());
                itemChanged = true;
            }
            chr.removeAndroid();
            chr.setAndroid(source.getAndroid());
        } else if (chr.getAndroid() != null && dst > -1300 && dst <= -1200) {
            chr.updateAndroidEquip(false, new Pair<Integer, Integer>(source.getItemId(), source.getItemSkin()));
        }
        int charmEXP = source.getCharmEXP();
        if (ii.isCash(source.getItemId()) && charmEXP <= 0) {
            if (ItemConstants.類型.帽子(source.getItemId())) {
                charmEXP = 50;
            } else if (ItemConstants.類型.眼飾(source.getItemId())) {
                charmEXP = 40;
            } else if (ItemConstants.類型.臉飾(source.getItemId())) {
                charmEXP = 40;
            } else if (ItemConstants.類型.耳環(source.getItemId())) {
                charmEXP = 40;
            } else if (ItemConstants.類型.套服(source.getItemId())) {
                charmEXP = 60;
            } else if (ItemConstants.類型.上衣(source.getItemId())) {
                charmEXP = 30;
            } else if (ItemConstants.類型.褲裙(source.getItemId())) {
                charmEXP = 30;
            } else if (ItemConstants.類型.手套(source.getItemId())) {
                charmEXP = 40;
            } else if (ItemConstants.類型.鞋子(source.getItemId())) {
                charmEXP = 40;
            } else if (ItemConstants.類型.披風(source.getItemId())) {
                charmEXP = 30;
            } else if (ItemConstants.類型.武器(source.getItemId()) && !ItemConstants.類型.副手(source.getItemId())) {
                charmEXP = 60;
            } else if (ItemConstants.類型.盾牌(source.getItemId())) {
                charmEXP = 10;
            }
        }
        if (charmEXP > 0 && !ItemAttribute.GetCharm.check(source.getAttribute())) {
            chr.getTrait(MapleTraitType.charm).addExp(charmEXP, chr);
            source.setCharmEXP((short)0);
            source.addAttribute(ItemAttribute.GetCharm.getValue());
            itemChanged = true;
        }
        chr.getInventory(iType).removeSlot(src);
        ArrayList<ModifyInventory> mods = new ArrayList<ModifyInventory>();
        if (itemChanged) {
            mods.add(new ModifyInventory(3, source));
            mods.add(new ModifyInventory(0, source));
        }
        source.setPosition(dst);
        mods.add(new ModifyInventory(2, source, src));
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
            target.setPosition(src);
            chr.getInventory(iType).addFromDB(target);
        }
        if (target2 != null) {
            short slot = target2.getPosition();
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(slot);
            target2.setPosition(target == null ? src : chr.getInventory(iType).getNextFreeSlot());
            chr.getInventory(iType).addFromDB(target2);
            mods.add(new ModifyInventory(2, target2, slot));
        }
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        c.announce(InventoryPacket.modifyInventory(true, mods));
        if (source.getItemId() == 1113228) {
            c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.equipInnerStorm((int)chr.getAccountID(), (int)chr.getId())));
        }
        if (source.getItemId() == 0x110122 && chr.getMap() != null && chr.getMap().getBarrierArc() > 0) {
            SkillFactory.getSkill(80011993).getEffect(1).applyTo(chr);
        }
        if (ItemConstants.類型.武器(source.getItemId()) && !ii.isCash(source.getItemId())) {
            chr.dispelEffect(SecondaryStat.Booster);
            chr.dispelEffect(SecondaryStat.NoBulletConsume);
            chr.dispelEffect(SecondaryStat.SoulArrow);
            chr.dispelEffect(SecondaryStat.WeaponCharge);
            chr.dispelEffect(SecondaryStat.AssistCharge);
            chr.dispelEffect(SecondaryStat.StopForceAtomInfo);
            if (dst != -5200) {
                chr.setSoulMP(0);
            }
            if (chr.getHaku() != null && dst == -5200) {
                chr.getHaku().setWeapon(source.getItemId());
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.LP_FoxManModified.getValue());
                mplew.writeInt(chr.getHaku().getOwner());
                mplew.write(1);
                mplew.writeInt(chr.getHaku().getWeapon());
                mplew.write(0);
                chr.getMap().broadcastMessage(mplew.getPacket());
            }
        }
        if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            chr.dispelEffect(SecondaryStat.RideVehicle);
            chr.dispelEffect(SecondaryStat.Mechanic);
        }
        if (source.getState(false) >= 17) {
            HashMap<Integer, SkillEntry> skills = new HashMap<Integer, SkillEntry>();
            int[] potentials = new int[]{source.getPotential1(), source.getPotential2(), source.getPotential3(), source.getPotential4(), source.getPotential5(), source.getPotential6()};
            for (int i : potentials) {
                List<StructItemOption> potentialInfo;
                StructItemOption pot;
                if (i <= 0 || (pot = (potentialInfo = ii.getPotentialInfo(i)).get(Math.min(potentialInfo.size() - 1, (source.getReqLevel() - 1) / 10))) == null || pot.get("skillID") <= 0) continue;
                skills.put(SkillConstants.getSkillByJob(pot.get("skillID"), chr.getJob()), new SkillEntry(1, 0, -1L));
            }
            chr.changeSkillLevel_Skip(skills, true);
        }
        if (source.getSocketState() >= 19) {
            int[] sockets;
            HashMap<Integer, SkillEntry> skills = new HashMap<Integer, SkillEntry>();
            for (int i : sockets = new int[]{source.getSocket1(), source.getSocket2(), source.getSocket3()}) {
                StructItemOption soc;
                if (i <= 0 || (soc = ii.getSocketInfo(i)) == null || soc.get("skillID") <= 0) continue;
                skills.put(SkillConstants.getSkillByJob(soc.get("skillID"), chr.getJob()), new SkillEntry(1, 0, -1L));
            }
            chr.changeSkillLevel_Skip(skills, true);
        }
        chr.equipChanged();
    }

    public static void unequip(MapleClient c, short src, short dst) {
        HashMap<Integer, SkillEntry> skills;
        MapleCharacter player = c.getPlayer();
        if (player == null) {
            return;
        }
        Equip source = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        if (dst < 0 || source == null) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleInventoryType iType = ii.isCash(source.getItemId()) ? MapleInventoryType.DECORATION : MapleInventoryType.EQUIP;
        Equip target = (Equip)player.getInventory(iType).getItem(dst);
        if (target != null && src <= 0) {
            c.announce(InventoryPacket.getInventoryFull());
            return;
        }
        int sourceItemID = source.getItemId();
        player.getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            player.getInventory(iType).removeSlot(dst);
        }
        source.setPosition(dst);
        player.getInventory(iType).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            player.getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }
        if (ItemConstants.類型.武器(source.getItemId()) && !ii.isCash(source.getItemId())) {
            player.dispelEffect(SecondaryStat.Booster);
            player.dispelEffect(SecondaryStat.NoBulletConsume);
            player.dispelEffect(SecondaryStat.SoulArrow);
            player.dispelEffect(SecondaryStat.WeaponCharge);
            player.dispelEffect(SecondaryStat.AssistCharge);
            player.dispelEffect(SecondaryStat.StopForceAtomInfo);
            if (src != -5200) {
                player.setSoulMP(0);
            }
            if (player.getHaku() != null && src == -5200) {
                player.getHaku().setWeapon(0);
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.LP_FoxManModified.getValue());
                mplew.writeInt(player.getHaku().getOwner());
                mplew.write(1);
                mplew.writeInt(player.getHaku().getWeapon());
                mplew.write(0);
                player.getMap().broadcastMessage(mplew.getPacket());
            }
        } else if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            player.dispelEffect(SecondaryStat.RideVehicle);
            player.dispelEffect(SecondaryStat.Mechanic);
        } else if (ItemConstants.類型.機器人(source.getItemId())) {
            player.removeAndroid();
        } else if (ItemConstants.類型.心臟(source.getItemId()) && player.getAndroid() != null) {
            c.announce(AndroidPacket.removeAndroidHeart());
            player.removeAndroid();
        } else if (player.getAndroid() != null && src > -1300 && src <= -1200) {
            player.updateAndroidEquip(true, new Pair<Integer, Integer>(sourceItemID, 0));
        }
        if (source.getState(false) >= 17) {
            skills = new HashMap<Integer, SkillEntry>();
            int[] potentials = new int[]{source.getPotential1(), source.getPotential2(), source.getPotential3(), source.getPotential4(), source.getPotential5(), source.getPotential6()};
            for (int i : potentials) {
                List<StructItemOption> potentialInfo;
                StructItemOption pot;
                if (i <= 0 || (pot = (potentialInfo = ii.getPotentialInfo(i)).get(Math.min(potentialInfo.size() - 1, (source.getReqLevel() - 1) / 10))) == null || pot.get("skillID") <= 0) continue;
                skills.put(SkillConstants.getSkillByJob(pot.get("skillID"), player.getJob()), new SkillEntry(0, 0, -1L));
            }
            player.changeSkillLevel_Skip(skills, true);
        }
        if (source.getSocketState() >= 19) {
            int[] sockets;
            skills = new HashMap();
            for (int i : sockets = new int[]{source.getSocket1(), source.getSocket2(), source.getSocket3()}) {
                StructItemOption soc;
                if (i <= 0 || (soc = ii.getSocketInfo(i)) == null || soc.get("skillID") <= 0) continue;
                skills.put(SkillConstants.getSkillByJob(soc.get("skillID"), player.getJob()), new SkillEntry(0, 0, -1L));
            }
            player.changeSkillLevel_Skip(skills, true);
        }
        c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(2, source, src))));
        if (source.getItemId() == 1113228) {
            c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.unequipInnerStorm((int)player.getId())));
        }
        if (source.getItemId() == 0x110122 && player.getBuffStatValueHolder(80011993) != null) {
            player.dispelBuff(80011993);
        }
        player.equipChanged();
    }

    public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity) {
        return MapleInventoryManipulator.drop(c, type, src, quantity, false);
    }

    public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity, boolean npcInduced) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return false;
        }
        Item source = c.getPlayer().getInventory(type).getItem(src);
        if (quantity < 0 || source == null || !npcInduced && ItemConstants.類型.寵物(source.getItemId()) || quantity == 0 && !ItemConstants.類型.可充值道具(source.getItemId()) || c.getPlayer().inPVP()) {
            c.sendEnableActions();
            return false;
        }
        if (!npcInduced && source.getItemId() == 4000463) {
            c.getPlayer().dropMessage(1, "該道具無法丟棄.");
            c.sendEnableActions();
            return false;
        }
        int flag = source.getCAttribute();
        if (quantity > source.getQuantity() && !ItemConstants.類型.可充值道具(source.getItemId())) {
            c.sendEnableActions();
            return false;
        }
        if (ItemAttribute.Seal.check(flag) || quantity != 1 && (type == MapleInventoryType.EQUIP || type == MapleInventoryType.DECORATION)) {
            c.sendEnableActions();
            return false;
        }
        Point dropPos = new Point(c.getPlayer().getPosition());
        c.getPlayer().getCheatTracker().checkDrop();
        if (quantity < source.getQuantity() && !ItemConstants.類型.可充值道具(source.getItemId())) {
            Item target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short)(source.getQuantity() - quantity));
            c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(1, source))));
            MapleInventoryManipulator.applyDrop(c, target, flag, ii, dropPos);
        } else {
            c.getPlayer().getInventory(type).removeSlot(src);
            if (ItemConstants.類型.採集道具(source.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            c.announce(InventoryPacket.modifyInventory(true, Collections.singletonList(new ModifyInventory(3, source))));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }
            if ((source.getItemId() / 10000 == 265 || source.getItemId() / 10000 == 308 || source.getItemId() / 10000 == 433) && source.getExtendSlot() > 0) {
                c.getPlayer().getInventory(type).removeExtendedSlot(source.getExtendSlot());
                c.getPlayer().getExtendedSlots(type.getType()).remove(source);
            }
            MapleInventoryManipulator.applyDrop(c, source, flag, ii, dropPos);
        }
        return true;
    }

    private static void applyDrop(MapleClient c, Item item, int flag, MapleItemInformationProvider ii, Point dropPos) {
        if (ItemConstants.類型.寵物(item.getItemId()) || ItemAttribute.TradeBlock.check(flag) || ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId()) || ItemAttribute.AccountSharable.check(flag)) {
            if (ItemAttribute.TradeOnce.check(flag) && !ItemAttribute.AccountSharable.check(flag)) {
                if (ItemAttribute.CutUsed.check(flag &= ~ItemAttribute.TradeOnce.getValue())) {
                    flag &= ~ItemAttribute.CutUsed.getValue();
                }
                item.setAttribute(flag);
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, dropPos, true, true);
            } else {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), item, dropPos);
            }
        } else {
            c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, dropPos, true, true);
        }
    }

    public static void updateItem(MapleClient c, List<? extends Item> items, boolean b) {
        if (c.getPlayer() == null) {
            return;
        }
        List<ModifyInventory> collect = items.stream().map(it -> new ModifyInventory(0, (Item)it)).collect(Collectors.toList());
        c.announce(InventoryPacket.modifyInventory(b, collect));
    }
}

