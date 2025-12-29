/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.inventory.EnchantScrollEntry
 *  Client.inventory.Equip$ScrollResult
 *  Client.inventory.MaplePotionPot
 *  Config.constants.enums.ScrollIconType
 *  Packet.InventoryPacket$1
 *  Server.channel.handler.ItemScrollHandler
 */
package Packet;

import Client.MapleCharacter;
import Client.MessageOption;
import Client.inventory.EnchantScrollEntry;
import Client.inventory.EnchantScrollFlag;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MaplePet;
import Client.inventory.MaplePotionPot;
import Client.inventory.ModifyInventory;
import Client.skills.InnerSkillEntry;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Config.constants.enums.ScrollIconType;
import Net.server.MapleItemInformationProvider;
import Net.server.maps.MapleMapItem;
import Opcode.header.OutHeader;
import Packet.CWvsContext;
import Packet.InventoryPacket;
import Packet.PacketHelper;
import Server.channel.handler.EnchantHandler;
import Server.channel.handler.ItemScrollHandler;
import connection.OutPacket;
import java.awt.Point;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;
import tools.data.MaplePacketLittleEndianWriter;

public class InventoryPacket {
    private static final Logger log = LoggerFactory.getLogger(InventoryPacket.class);

    public static byte[] updateInventorySlotLimit(byte invType, byte newSlots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_InventoryGrow.getValue());
        mplew.write(invType);
        mplew.write(newSlots);
        return mplew.getPacket();
    }

    public static byte[] updatePet(MaplePet pet, Item item, boolean summoned) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_InventoryOperation.getValue());
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(2);
        mplew.write(0);
        mplew.write(3);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(0);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(3);
        OutPacket outPacket = new OutPacket();
        item.encodePetRaw(outPacket, pet, summoned);
        mplew.write(outPacket.getData());
        return mplew.getPacket();
    }

    public static byte[] petAddSkillEffect(MapleCharacter player, MaplePet pet) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.PET_ADD_SKILL_EFFECT.getValue());
        mplew.write(player.getId());
        mplew.write(1);
        mplew.writeInt(pet.getAddSkill());
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] modifyInventory(boolean updateTick, List<ModifyInventory> mods) {
        return InventoryPacket.modifyInventory(updateTick, mods, null);
    }

    public static byte[] modifyInventory(boolean updateTick, List<ModifyInventory> mods, MapleCharacter chr) {
        return InventoryPacket.modifyInventory(updateTick, mods, chr, false);
    }

    public static byte[] modifyInventory(boolean updateTick, List<ModifyInventory> mods, MapleCharacter chr, boolean active) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_InventoryOperation.getValue());
        mplew.writeBool(updateTick);
        mplew.write(0);
        mplew.writeInt(mods.size());
        mplew.write(0);
        int addMovement = -1;
        for (ModifyInventory mod : mods) {
            mplew.write(mod.getMode());
            mplew.write(mod.getInventoryType());
            boolean oldpos = mod.getMode() == 2 || mod.getMode() == 9 || mod.getMode() == 6 && !mod.switchSrcDst();
            mplew.writeShort(oldpos ? mod.getOldPosition() : mod.getPosition());
            switch (mod.getMode()) {
                case 0: {
                    PacketHelper.GW_ItemSlotBase_Encode(mplew, mod.getItem());
                    break;
                }
                case 1: {
                    mplew.writeShort(mod.getQuantity());
                    break;
                }
                case 2: {
                    mplew.writeShort(mod.getPosition());
                    if (mod.getPosition() >= 0 && mod.getOldPosition() >= 0) break;
                    addMovement = mod.getOldPosition() < 0 ? 1 : 2;
                    break;
                }
                case 3: {
                    if (mod.getPosition() >= 0) break;
                    addMovement = 2;
                    break;
                }
                case 4: {
                    mplew.writeLong(((Equip)mod.getItem()).getSealedExp());
                    break;
                }
                case 6: {
                    mplew.writeShort(!mod.switchSrcDst() ? mod.getPosition() : mod.getOldPosition());
                    if (mod.getIndicator() == -1) break;
                    mplew.writeShort(mod.getIndicator());
                    break;
                }
                case 7: {
                    mplew.writeShort(mod.getQuantity());
                    break;
                }
                case 8: {
                    break;
                }
                case 9: {
                    mplew.writeShort(mod.getPosition());
                    break;
                }
                case 10: {
                    PacketHelper.GW_ItemSlotBase_Encode(mplew, mod.getItem());
                }
            }
            mod.clear();
        }
        if (addMovement > -1) {
            mplew.write(addMovement);
        }
        return mplew.getPacket();
    }

    public static byte[] getInventoryFull() {
        return InventoryPacket.modifyInventory(true, Collections.emptyList());
    }

    public static byte[] getInventoryStatus() {
        return InventoryPacket.modifyInventory(false, Collections.emptyList());
    }

    public static byte[] getShowInventoryFull() {
        return InventoryPacket.getShowInventoryStatus(255);
    }

    public static byte[] showItemUnavailable() {
        return InventoryPacket.getShowInventoryStatus(254);
    }

    public static byte[] getShowInventoryStatus(int mode) {
        MessageOption option = new MessageOption();
        option.setMode(mode);
        return CWvsContext.sendMessage(0, option);
    }

    public static byte[] showScrollTip(boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SHOW_SCROLL_TIP.getValue());
        mplew.writeInt(success ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] getScrollEffect(int chrId, Equip.ScrollResult scrollSuccess, boolean legendarySpirit, boolean whiteScroll, int scroll, int toScroll) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        //[DB 00] [46 57 01 00] [00] [00] [00] [00] 沒有成功
        mplew.writeShort(OutHeader.LP_UserItemUpgradeEffect.getValue());
        mplew.writeInt(chrId);
        switch (scrollSuccess) {
            case 失敗:
                mplew.write(0);
                break;
            case 成功:
                mplew.write(1);
                break;
            case 消失:
                mplew.write(2);
                break;
            default:
                throw new IllegalArgumentException("effect in illegal range");
        }
        mplew.write(legendarySpirit ? 1 : 0);
        mplew.writeInt(scroll);
        mplew.writeInt(toScroll);
        mplew.write(0); // 1祝福
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showMagnifyingEffect(int chrId, short pos, boolean isPotAdd) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserItemReleaseEffect.getValue());
        MapleCharacter player = MapleCharacter.getCharacterById(chrId);
        mplew.writeInt(chrId);
        mplew.writeShort(pos);
        mplew.write(isPotAdd ? 1 : 0);
        if (player.isGm()) {
            player.dropMessage(40, "[GM] 鑑定道具潛能成功。");
        }
        return mplew.getPacket();
    }

    public static byte[] showPotentialReset(int chrId, boolean bonus, int itemid, int debris, int equipId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserItemUnreleaseEffect.getValue());
        mplew.writeInt(chrId);
        mplew.write(true);
        mplew.writeInt(itemid);
        mplew.writeInt(debris);
        mplew.writeInt(equipId);
        return mplew.getPacket();
    }

    public static byte[] showPotentialResetUseing(byte cubeType, int count) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_Mystery_Cube.getValue());
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(cubeType);
        mplew.writeInt(0);
        mplew.write(15);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeLong(7793993675480411107L);
        mplew.writeInt(count);
        return mplew.getPacket();
    }

    public static byte[] showBlackCubeResults() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MemorialCubeModified.getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showPotentialEx(int chrId, boolean success, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserItemAdditionalSlotExtendEffect.getValue());
        mplew.writeInt(chrId);
        mplew.write(success);
        mplew.writeInt(itemid);
        mplew.writeInt(0);
        mplew.writeInt(1012478);
        return mplew.getPacket();
    }

    public static byte[] showFireWorksEffect(int chrId, boolean success, int itemid, boolean bUnk, int nUnk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserItemFireWorksEffect.getValue());
        mplew.writeInt(chrId);
        mplew.write(success);
        mplew.writeInt(itemid);
        mplew.write(bUnk);
        if (!bUnk) {
            mplew.writeInt(nUnk);
        }
        return mplew.getPacket();
    }

    public static byte[] showSynthesizingMsg(int itemId, int giveItemId, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashLookChangeResult.getValue());
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemId);
        mplew.writeInt(giveItemId);
        return mplew.getPacket();
    }

    public static byte[] dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte nDropType) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DropEnterField.getValue());
        mplew.write(0);
        mplew.write(nDropType);
        mplew.writeInt(drop.getObjectId());
        boolean bIsMoney = drop.getMeso() > 0;
        mplew.writeBool(bIsMoney);
        mplew.writeInt(drop.getDropMotionType());
        mplew.writeInt(drop.getDropSpeed());
        mplew.writeInt(drop.getRand());
        mplew.writeInt(drop.getItemId());
        mplew.writeInt(drop.getOwnerID());
        mplew.write(drop.getOwnType());
        mplew.writePos(dropto);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(drop.getSourceOID());
        mplew.writeInt(0);
        mplew.writeLong(0L);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeLong(0L);
        mplew.writeInt(0);
        mplew.writeLong(0L);
        mplew.write(bIsMoney && drop.getSkill() == 4221018 ? 4 : 0);
        mplew.write(false);
        mplew.write(false);
        mplew.writeInt(0);
        if (nDropType != 2 && nDropType < 5) {
            mplew.writePos(dropfrom);
            mplew.writeInt(drop.getDelay());
        }
        mplew.write(false);
        mplew.write(false);
        if (drop.getItemId() != 2910000 && drop.getItemId() != 2910001 && !bIsMoney) {
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        mplew.write(drop.isPlayerDrop() ? 0 : 1);
        mplew.write(false);
        mplew.writeShort(0);
        mplew.write(false);
        mplew.writeInt(drop.isCollisionPickUp() ? 1 : 0);
        mplew.write((drop.getPointType() > 0 ? 2 : drop.getState()) & 0xF);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] explodeDrop(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DropLeaveField.getValue());
        mplew.write(4);
        mplew.writeInt(oid);
        mplew.writeShort(655);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] removeItemFromMap(int oid, int animation, int chrId) {
        return InventoryPacket.removeItemFromMap(oid, animation, chrId, 0);
    }

    public static byte[] removeItemFromMap(int oid, int animation, int chrId, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DropLeaveField.getValue());
        mplew.write(animation);
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(chrId);
            if (animation == 5) {
                mplew.writeInt(slot);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] showPotionPotMsg(int reason) {
        return InventoryPacket.showPotionPotMsg(reason, 0);
    }

    public static byte[] showPotionPotMsg(int reason, int msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.POTION_POT_MSG.getValue());
        mplew.write(reason);
        if (reason == 0) {
            mplew.write(msg);
        }
        return mplew.getPacket();
    }

    public static byte[] updataPotionPot(MaplePotionPot potionPot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.POTION_POT_UPDATE.getValue());
        PacketHelper.addPotionPotInfo(mplew, potionPot);
        return mplew.getPacket();
    }

    public static byte[] showSkillSkin(Map<Integer, Integer> skillskinlist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SHOW_SKILL_SKIN.getValue());
        mplew.writeInt(2);
        mplew.writeInt(skillskinlist.size());
        for (Map.Entry<Integer, Integer> skillskin : skillskinlist.entrySet()) {
            mplew.writeInt(skillskin.getKey());
            mplew.writeInt(skillskin.getValue());
        }
        return mplew.getPacket();
    }

    public static byte[] showDamageSkin(int chrId, int skinId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserSetDamageSkin.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(skinId);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static final byte[] showDamageSkin_Premium(int chrId, int skinId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserSetDamageSkin.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(skinId);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] enchantingOperation(int mode, int success, Item toScroll, Item scrolled, List<EnchantScrollEntry> scrollEntries, String string, boolean safe, boolean pcDiscount, int sfDiscount, int mvpLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_EquipmentEnchantDisplay.getValue());
        mplew.write(mode);
        switch (mode) {
            case 50: {
                mplew.write(0);
                mplew.write(scrollEntries.size());
                for (EnchantScrollEntry scroll : scrollEntries) {
                    mplew.writeInt(scroll.getIcon());
                    mplew.writeMapleAsciiString(scroll.getName());
                    mplew.writeInt(scroll.getType());
                    mplew.writeInt(scroll.getIcon() == ScrollIconType.亞克回真卷軸.ordinal() ? 6 : (scroll.getIcon() > ScrollIconType.卷軸_15Percentage.ordinal() ? 1 : 0));
                    mplew.writeInt(scroll.getOption());
                    mplew.writeInt(scroll.getMask());
                    Iterator iterator = scroll.getEnchantScrollStat().values().iterator();
                    while (iterator.hasNext()) {
                        int val = (Integer)iterator.next();
                        mplew.writeInt(val);
                    }
                    mplew.writeInt(scroll.getCost());
                    mplew.writeInt(scroll.getCost());
                    mplew.write(scroll.getSuccessRate() == 100 ? 1 : 0);
                }
                break;
            }
            case 51: {
                mplew.write(0);
                break;
            }
            case 52: {
                int destroyType;
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                Equip equip = (Equip)toScroll;
                byte enhance = equip.getStarForceLevel();
                safe = safe ? enhance >= 12 && enhance <= 16 : safe;
                int reqLevel = ii.getReqLevel(equip.getItemId());
                boolean isSuperior = ii.isSuperiorEquip(equip.getItemId());
                long meso = EnchantHandler.getStarForceMeso(reqLevel, enhance, isSuperior);
                if (meso < 0L) {
                    log.error("出現錯誤：找不到強化需求楓幣");
                    meso = 0L;
                }
                int successprop = (isSuperior ? ItemScrollHandler.sfSuccessPropSup[enhance] : ItemScrollHandler.sfSuccessProp[enhance]) * 10;
                int n = destroyType = isSuperior ? ItemScrollHandler.sfDestroyPropSup[enhance] : ItemScrollHandler.sfDestroyProp[enhance];
                destroyType = destroyType > 0 ? (enhance < 13 ? 1 : (enhance < 16 ? 2 : (enhance < 22 ? 3 : (enhance < 24 ? 4 : 5)))) : 0;
                boolean fall = isSuperior && enhance > 0 || enhance >= 10 && enhance % 5 != 0;
                mplew.writeBool(fall);
                long discountMeso = meso *= safe ? 2L : 1L;
                if (sfDiscount > 0) {
                    discountMeso = (long)((double)discountMeso * ((double)sfDiscount / 100.0));
                }
                if (mvpLevel >= 5 && enhance <= 17 && !isSuperior) {
                    discountMeso = (long)((double)discountMeso * (mvpLevel == 5 ? 0.97 : (mvpLevel == 6 ? 0.95 : 0.9)));
                }
                mplew.writeLong(discountMeso);
                mplew.writeLong(discountMeso != meso && sfDiscount > 0 ? meso : 0L);
                mplew.writeLong(discountMeso != meso && mvpLevel >= 5 ? meso : 0L);
                mplew.write(mvpLevel >= 5);
                mplew.write(pcDiscount);
                mplew.writeLong(ServerConfig.SF_MP_AMOUNT);
                mplew.writeLong(ServerConfig.SF_MP_SAFE_AMOUNT);
                mplew.writeInt(successprop);
                mplew.writeInt(destroyType);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeBool(equip.getFailCount() >= 2);
                InventoryPacket.writeMaskEnchantScroll(mplew, toScroll);
                break;
            }
            case 53: {
                mplew.write(0);
                mplew.writeInt(DateUtil.getTime());
                break;
            }
            case 100: {
                mplew.write(0);
                mplew.writeInt(success);
                mplew.writeMapleAsciiString(string);
                PacketHelper.GW_ItemSlotBase_Encode(mplew, toScroll);
                if (success == 2) {
                    mplew.writeShort(0);
                    break;
                }
                PacketHelper.GW_ItemSlotBase_Encode(mplew, scrolled);
                break;
            }
            case 101: {
                mplew.write(success);
                mplew.writeInt(0);
                PacketHelper.GW_ItemSlotBase_Encode(mplew, toScroll);
                if (success == 2) {
                    mplew.writeShort(0);
                    break;
                }
                PacketHelper.GW_ItemSlotBase_Encode(mplew, scrolled);
                break;
            }
            case 102: {
                mplew.writeInt(success);
                break;
            }
            case 103: {
                PacketHelper.GW_ItemSlotBase_Encode(mplew, toScroll);
                PacketHelper.GW_ItemSlotBase_Encode(mplew, scrolled);
                break;
            }
            case 104: {
                mplew.write(1);
                break;
            }
            case 105: {
                PacketHelper.GW_ItemSlotBase_Encode(mplew, toScroll);
                PacketHelper.GW_ItemSlotBase_Encode(mplew, scrolled);
            }
        }
        return mplew.getPacket();
    }

    public static void writeMaskEnchantScroll(MaplePacketLittleEndianWriter mplew, Item item) {
        Map<EnchantScrollFlag, Integer> scrollList = EnchantHandler.getEnchantScrollList(item);
        int mask = 0;
        for (EnchantScrollFlag flag : scrollList.keySet()) {
            if (!scrollList.containsKey((Object)flag) || scrollList.get((Object)flag) <= 0) continue;
            mask |= flag.getValue();
        }
        mplew.writeInt(mask);
        if (mask != 0) {
            for (EnchantScrollFlag flag : EnchantScrollFlag.values()) {
                if (!scrollList.containsKey((Object)flag) || scrollList.get((Object)flag) <= 0) continue;
                mplew.writeInt(scrollList.get((Object)flag));
            }
        }
    }

    public static byte[] getZeroWeaponInfo(int weaponlevel, int level, int weapon1, int weapon2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SHOW_ZERO_WEAPON_INFO.getValue());
        mplew.writeShort(0);
        mplew.writeInt(weaponlevel);
        mplew.writeInt(level);
        mplew.writeInt(weapon1);
        mplew.writeInt(weapon2);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] getZeroWeaponChangePotential(int meso, int wp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SHOW_CHANGE_POTENTIAL_MESO.getValue());
        mplew.writeInt(1);
        mplew.writeInt(meso);
        mplew.writeInt(wp);
        mplew.writeShort(1);
        return mplew.getPacket();
    }

    public static byte[] showZeroWeaponChangePotentialResult(boolean succ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SHOW_CHANGE_POTENTIAL_RESULT.getValue());
        mplew.write(1);
        mplew.writeBool(succ);
        return mplew.getPacket();
    }

    public static byte[] showHyunPotentialResult(List<Integer> potids) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SHOW_POTENTIAL_RESULT.getValue());
        mplew.writeShort(potids == null ? 1 : 0);
        mplew.writeInt(0);
        if (potids != null) {
            mplew.writeInt(potids.size() / 2);
            mplew.writeInt(potids.size());
            potids.forEach(mplew::writeInt);
        }
        return mplew.getPacket();
    }

    public static byte[] encodeReturnEffectConfirm(Item toScroll, int scrollId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ReturnEffectConfirm.getValue());
        mplew.writeLong(toScroll == null ? -1L : (long)toScroll.getSN());
        mplew.write(toScroll != null);
        if (toScroll != null) {
            PacketHelper.GW_ItemSlotBase_Encode(mplew, toScroll);
            mplew.writeInt(scrollId);
        }
        return mplew.getPacket();
    }

    public static byte[] encodeReturnEffectModified(Item toScroll, int scrollId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ReturnEffectModified.getValue());
        mplew.write(toScroll != null);
        if (toScroll != null) {
            PacketHelper.GW_ItemSlotBase_Encode(mplew, toScroll);
            mplew.writeInt(scrollId);
        }
        return mplew.getPacket();
    }

    public static byte[] showCubeResetResult(int n, Item toScroll, int n2, int n3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(n2 == 5062090 ? OutHeader.LP_MemorialCubeResult.getValue() : (n2 == 5062503 ? OutHeader.LP_WhiteAdditionalCubeResult.getValue() : OutHeader.LP_BlackCubeResult.getValue()));
        mplew.writeLong(toScroll.getSN());
        mplew.write(1);
        PacketHelper.GW_ItemSlotBase_Encode(mplew, toScroll);
        mplew.writeInt(n2);
        mplew.writeInt(n);
        mplew.writeInt(n3);
        return mplew.getPacket();
    }

    public static byte[] showCubeResult(int chrid, boolean upgrade, int cubeid, int position, int cube_quantity, Item equip) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        short opcode = 0;
        switch (cubeid) {
            case 5062022: {
                opcode = OutHeader.SHOW_SHININGMIRROR_CUBE_RESULT.getValue();
                break;
            }
            case 5062009: {
                opcode = OutHeader.LP_UserItemRedCubeResult.getValue();
                break;
            }
            case 5062500: 
            case 5062501: 
            case 5062502: {
                opcode = OutHeader.LP_UserItemInGameCubeResult.getValue();
                break;
            }
            case 2730000: 
            case 2730001: 
            case 2730002: 
            case 2730004: 
            case 2730005: 
            case 2730006: {
                opcode = OutHeader.LP_AttachCube.getValue();
                break;
            }
            default: {
                opcode = OutHeader.LP_UserItemInGameCubeResult.getValue();
            }
        }
        mplew.writeShort(opcode);
        mplew.writeInt(chrid);
        mplew.write(upgrade);
        mplew.writeInt(cubeid);
        mplew.writeInt(position);
        mplew.writeInt(cube_quantity);
        PacketHelper.GW_ItemSlotBase_Encode(mplew, equip);
        return mplew.getPacket();
    }

    public static byte[] showTapJoyInfo(int slot, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        int i1 = itemid % 10 - 1;
        mplew.writeShort(OutHeader.TAP_JOY_INFO.getValue());
        mplew.write(5);
        mplew.writeInt(i1);
        mplew.writeInt(slot);
        mplew.writeInt(itemid);
        mplew.write(0);
        mplew.writeInt(350 * (i1 / 2 + 2));
        int size = ItemConstants.TapJoyReward.getStages().size() / 2;
        mplew.writeInt(5840000 + i1);
        mplew.writeInt(size);
        for (int i = 0; i < size; ++i) {
            mplew.writeInt(i);
            mplew.writeInt(ItemConstants.TapJoyReward.getItemIdAndSN(i * 2).getLeft());
            mplew.writeInt(ItemConstants.TapJoyReward.getItemIdAndSN(i * 2).getRight());
            mplew.writeInt(ItemConstants.TapJoyReward.getItemIdAndSN(i * 2 + 1).getLeft());
            mplew.writeInt(ItemConstants.TapJoyReward.getItemIdAndSN(i * 2 + 1).getLeft());
            mplew.writeInt(100);
            mplew.writeInt(350 * (i / 2 + 2));
            mplew.writeInt(4009441 + i);
            mplew.writeInt(5840000 + i);
        }
        return mplew.getPacket();
    }

    public static byte[] showTapJoy(int reward) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.TAP_JOY.getValue());
        mplew.writeInt(reward);
        return mplew.getPacket();
    }

    public static byte[] showTapJoyDone(int mode, int itemid, int intValue3, int gainslot, int intValue) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.TAP_JOY_DONE.getValue());
        mplew.write(mode);
        mplew.writeInt(itemid);
        mplew.writeInt(intValue3);
        mplew.writeInt(4);
        mplew.writeInt(gainslot);
        mplew.writeInt(0);
        mplew.writeInt(intValue);
        return mplew.getPacket();
    }

    public static byte[] showTapJoyNextStage(MapleCharacter player, int n, int n2, int n3, int n4) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.TAP_JOY_NEXT_STAGE.getValue());
        mplew.write(n);
        mplew.writeInt(n3);
        mplew.writeInt(5);
        mplew.writeInt(n2);
        mplew.writeInt(n3);
        mplew.write(n4);
        mplew.writeInt(player.getCSPoints(n4));
        return mplew.getPacket();
    }

    public static byte[] UserToadsHammerResult(short mode, Equip equip, short n2, List<EnchantScrollEntry> scrollEntryList) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ToadsHammerRequestResult.getValue());
        mplew.writeShort(mode);
        switch (mode) {
            case 1: {
                PacketHelper.GW_ItemSlotBase_Encode(mplew, equip);
                mplew.writeShort(n2);
                break;
            }
            case 2: {
                mplew.write(0);
                mplew.write(scrollEntryList.size());
                for (EnchantScrollEntry scrollEntry : scrollEntryList) {
                    mplew.writeInt(scrollEntry.getIcon());
                    mplew.writeMapleAsciiString(scrollEntry.getName());
                    mplew.writeInt(scrollEntry.getType());
                    mplew.writeInt(scrollEntry.getOption());
                    mplew.writeInt(scrollEntry.getMask());
                    Iterator iterator = scrollEntry.getEnchantScrollStat().values().iterator();
                    while (iterator.hasNext()) {
                        int val = (Integer)iterator.next();
                        mplew.writeInt(val);
                    }
                    mplew.writeInt(scrollEntry.getCost());
                    mplew.writeInt(scrollEntry.getCost());
                    mplew.write(scrollEntry.getSuccessRate() == 100 ? 1 : 0);
                }
                break;
            }
            case 0: {
                PacketHelper.GW_ItemSlotBase_Encode(mplew, equip);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] ChangeNameResult(int i, int i1) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserRenameResult.getValue());
        mplew.write(i);
        if (i == 9) {
            mplew.writeInt(i1);
        }
        return mplew.getPacket();
    }

    public static byte[] UserDamageSkinSaveResult(int type, int i1, MapleCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserDamageSkinSaveResult.getValue());
        mplew.write(type);
        switch (type) {
            case 0: 
            case 1: 
            case 2: {
                mplew.write(0);
                break;
            }
            case 3: {
                PacketHelper.addDamageSkinInfo(mplew, player);
                break;
            }
            case 4: {
                String questInfo = player.getOneInfo(56829, "count");
                mplew.writeInt(questInfo == null ? ServerConfig.defaultDamageSkinSlot : Integer.valueOf(questInfo));
                PacketHelper.addDamageSkinInfo(mplew, player);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] CharacterPotentialResult(List<InnerSkillEntry> list, int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CharacterPotentialResult.getValue());
        mplew.writeInt(list.size());
        for (InnerSkillEntry ise : list) {
            mplew.writeInt(ise.getSkillId());
            mplew.write(ise.getSkillLevel());
            mplew.write(ise.getPosition());
            mplew.write(ise.getRank());
        }
        mplew.writeInt(itemId);
        mplew.writeLong(0L);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] hiddenTailAndEar(int id, boolean b, boolean equals) {
        MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
        hh.writeOpcode(OutHeader.HIDE_SHAMAN_INFO);
        hh.writeInt(id);
        hh.writeBool(b);
        hh.writeBool(equals);
        return hh.getPacket();
    }
}

