/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MemoEntry
 *  Client.inventory.MaplePotionPot
 *  Config.constants.enums.MemoOptType
 *  Net.server.MTSStorage$MTSItemInfo
 */
package Packet;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleStat;
import Client.MemoEntry;
import Client.inventory.Item;
import Client.inventory.MaplePotionPot;
import Config.configs.CSInfoConfig;
import Config.constants.enums.CashItemModFlag;
import Config.constants.enums.MemoOptType;
import Net.server.MTSStorage;
import Net.server.RaffleItem;
import Net.server.RafflePool;
import Net.server.cashshop.CashItemFactory;
import Net.server.cashshop.CashItemInfo;
import Net.server.cashshop.CashShop;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import Packet.PacketHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class MTSCSPacket {
    public static byte[] warpchartoCS(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetCashShop.getValue());
        PacketHelper.addCharacterInfo(mplew, c.getPlayer(), -1L);
        return mplew.getPacket();
    }

    public static byte[] warpCS(boolean custom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetCashShopInfo.getValue());
        if (custom) {
            mplew.writeHexString(CSInfoConfig.CASH_CASHSHOPPACK);
        } else {
            int i;
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
            mplew.writeZeroBytes(6);
            mplew.writeInt(2);
            mplew.writeInt(110100165);
            mplew.writeInt(110100166);
            Map<Integer, CashItemInfo.CashModInfo> allModInfo = CashItemFactory.getInstance().getAllModInfo();
            mplew.writeShort(allModInfo.size());
            for (Map.Entry<Integer, CashItemInfo.CashModInfo> it : allModInfo.entrySet()) {
                CashItemInfo.CashModInfo cmi = it.getValue();
                mplew.writeInt(cmi.getSn());
                MTSCSPacket.writeModItemData(mplew, cmi);
            }
            int nCashPackageNameSize = 0;
            mplew.writeShort(nCashPackageNameSize);
            for (int i2 = 0; i2 < nCashPackageNameSize; ++i2) {
                mplew.writeInt(0);
                mplew.writeMapleAsciiString("");
            }
            Map<Integer, List<Pair<Integer, Integer>>> randomItemInfo = CashItemFactory.getInstance().getRandomItemInfo();
            int rndItemSize = (int)randomItemInfo.keySet().stream().filter(k -> k / 1000 == 5533).count();
            mplew.writeInt(rndItemSize);
            for (Map.Entry<Integer, List<Pair<Integer, Integer>>> it : randomItemInfo.entrySet()) {
                mplew.writeInt(it.getKey());
                if (it.getKey() / 1000 != 5533) break;
                mplew.writeInt(it.getValue().size());
                for (Pair<Integer, Integer> itt : it.getValue()) {
                    mplew.writeInt((Integer)itt.left);
                }
            }
            int nCount = 0;
            mplew.writeInt(nCount);
            for (int i3 = 0; i3 < nCount; ++i3) {
                mplew.writeInt(i3);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeLong(0L);
                mplew.writeLong(0L);
                mplew.writeInt(0);
                int[] unkData = new int[]{0, 20, 30, 40, 50};
                mplew.writeInt(unkData.length);
                for (int j = 0; j < unkData.length; j++) {
                    mplew.writeInt(unkData[j]);
                }
            }
            Map<Integer, Byte> allBaseNewInfo = CashItemFactory.getInstance().getAllBaseNewInfo();
            mplew.writeInt(allBaseNewInfo.size());
            for (Map.Entry<Integer, Byte> it : allBaseNewInfo.entrySet()) {
                mplew.write(it.getValue());
                mplew.writeInt(it.getKey());
            }
            nCount = 0;
            mplew.writeInt(nCount);
            for (i = 0; i < nCount; ++i) {
                mplew.writeInt(0);
                mplew.writeLong(0L);
                mplew.writeLong(0L);
                mplew.writeLong(0L);
                mplew.writeMapleAsciiString("");
            }
            nCount = 0;
            mplew.writeInt(nCount);
            for (i = 0; i < nCount; ++i) {
                mplew.write(i);
                mplew.writeInt(0);
            }
            nCount = 0;
            mplew.writeInt(nCount);
            for (i = 0; i < nCount; ++i) {
                mplew.writeMapleAsciiString("");
                mplew.writeMapleAsciiString("");
            }
            nCount = 0;
            mplew.writeShort(nCount);
            for (i = 0; i < nCount; ++i) {
                mplew.writeLong(0L);
            }
            Map<Integer, CashItemInfo.CashModInfo> customPackages = new HashMap<>();
            mplew.writeShort(customPackages.size());
            if (customPackages.size() > 0) {
                mplew.writeInt(customPackages.size());
                for (Map.Entry it : customPackages.entrySet()) {
                    mplew.writeInt((Integer)it.getKey());
                    mplew.writeInt(((CashItemInfo.CashModInfo)it.getValue()).getSn());
                    mplew.writeZeroBytes(36);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.write(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    for (int i4 = 0; i4 < 7; ++i4) {
                        mplew.writeInt(0);
                    }
                    mplew.write(0);
                    mplew.writeMapleAsciiString("");
                    mplew.writeInt(60);
                }
            }
            nCount = 0;
            mplew.writeInt(nCount);
            for (int i5 = 0; i5 < nCount; ++i5) {
                mplew.writeInt(0);
                int nCount2 = 0;
                mplew.writeInt(nCount2);
                for (int j = 0; j < nCount2; ++j) {
                    mplew.writeZeroBytes(24);
                }
            }
            int[] boxes = new int[]{5060048, 5060086, 5680796, 5222138, 5222123, 5537000};
            mplew.writeInt(boxes.length);
            for (int boxID : boxes) {
                byte sw = 1;
                mplew.write(sw);
                if (sw <= 0) continue;
                mplew.writeInt(boxID);
                List<RaffleItem> mainRewards = RafflePool.getMainReward(boxID);
                mplew.writeShort(mainRewards.size());
                for (RaffleItem item : mainRewards) {
                    mplew.writeInt(item.getItemId());
                }
            }
        }
        return mplew.getPacket();
    }

    public static void writeModItemData(MaplePacketLittleEndianWriter mplew, CashItemInfo.CashModInfo cmi) {
        String date;
        long flags = cmi.getFlags();
        mplew.writeLong(flags);
        if (CashItemModFlag.ITEM_ID.contains(flags)) {
            mplew.writeInt(cmi.getItemid());
        }
        if (CashItemModFlag.COUNT.contains(flags)) {
            mplew.writeShort(cmi.getCount());
        }
        if (CashItemModFlag.PRIORITY.contains(flags)) {
            mplew.write(cmi.getPriority());
        }
        if (CashItemModFlag.PRICE.contains(flags)) {
            mplew.writeInt(cmi.getPrice());
        }
        if (CashItemModFlag.ORIGINAL_PRICE.contains(flags)) {
            mplew.writeInt(cmi.getOriginalPrice());
        }
        if (CashItemModFlag.TOKEN.contains(flags)) {
            mplew.writeInt(0);
        }
        if (CashItemModFlag.BONUS.contains(flags)) {
            mplew.write(cmi.getCsClass());
        }
        if (CashItemModFlag.ZERO.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.PERIOD.contains(flags)) {
            mplew.writeShort(cmi.getPeriod());
        }
        if (CashItemModFlag.REQ_POP.contains(flags)) {
            mplew.writeShort(cmi.getFameLimit());
        }
        if (CashItemModFlag.REQ_LEV.contains(flags)) {
            mplew.writeShort(cmi.getLevelLimit());
        }
        if (CashItemModFlag.MAPLE_POINT.contains(flags)) {
            mplew.writeInt(0);
        }
        if (CashItemModFlag.MESO.contains(flags)) {
            mplew.writeInt(cmi.getMeso());
        }
        if (CashItemModFlag.FOR_PREMIUM_USER.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.COMMODITY_GENDER.contains(flags)) {
            mplew.write(cmi.getGender());
        }
        if (CashItemModFlag.ON_SALE.contains(flags)) {
            mplew.writeBool(cmi.isShowUp());
        }
        if (CashItemModFlag.CLASS.contains(flags)) {
            mplew.write(cmi.getMark());
        }
        if (CashItemModFlag.LIMIT.contains(flags)) {
            mplew.write(1);
        }
        if (CashItemModFlag.PB_CASH.contains(flags)) {
            mplew.writeShort(0);
        }
        if (CashItemModFlag.PB_POINT.contains(flags)) {
            mplew.writeShort(0);
        }
        if (CashItemModFlag.PB_GIFT.contains(flags)) {
            mplew.writeShort(0);
        }
        if (CashItemModFlag.PACKAGE_SN.contains(flags)) {
            int[] sns = new int[]{};
            mplew.write(sns.length);
            for (int sn : sns) {
                mplew.writeInt(sn);
            }
        }
        if (CashItemModFlag.TERM_START.contains(flags)) {
            date = String.valueOf(cmi.getTermStart());
            if (date.length() >= 4) {
                mplew.writeShort(Short.parseShort(date.substring(0, 4)));
                date = date.substring(4);
            } else {
                mplew.writeShort(0);
                date = "";
            }
            for (int i = 0; i < 5; ++i) {
                if (date.length() >= 2) {
                    mplew.writeShort(Short.parseShort(date.substring(0, 2)));
                    date = date.substring(2);
                    continue;
                }
                mplew.writeShort(0);
            }
        }
        if (CashItemModFlag.TERM_END.contains(flags)) {
            date = String.valueOf(cmi.getTermEnd());
            if (date.length() >= 4) {
                mplew.writeShort(Short.parseShort(date.substring(0, 4)));
                date = date.substring(4);
            } else {
                mplew.writeShort(0);
                date = "";
            }
            for (int i = 0; i < 5; ++i) {
                if (date.length() >= 2) {
                    mplew.writeShort(Short.parseShort(date.substring(0, 2)));
                    date = date.substring(2);
                    continue;
                }
                mplew.writeShort(0);
            }
        }
        if (CashItemModFlag.REFUNDABLE.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.BOMB_SALE.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.CATEGORY_INFO.contains(flags)) {
            mplew.writeShort(cmi.getCategories());
        }
        if (CashItemModFlag.WORLD_LIMIT.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.LIMIT_MAX.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.CHECK_QUEST_ID.contains(flags)) {
            mplew.writeInt(0);
        }
        if (CashItemModFlag.DISCOUNT.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.DISCOUNT_RATE.contains(flags)) {
            mplew.writeDouble(0.0);
        }
        if (CashItemModFlag.MILEAGE_INFO.contains(flags)) {
            mplew.write(0);
            mplew.write(0);
        }
        if (CashItemModFlag.CHECK_QUEST_ID_2.contains(flags)) {
            mplew.writeInt(0);
        }
        if (CashItemModFlag.UNK34.contains(flags)) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (CashItemModFlag.UNK35.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.UNK36.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.COUPON_TYPE.contains(flags)) {
            mplew.writeInt(0);
        }
        if (CashItemModFlag.UNK38.contains(flags)) {
            mplew.writeInt(0);
        }
        if (CashItemModFlag.UNK39.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.UNK40.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.UNK41.contains(flags)) {
            mplew.write(0);
        }
        if (CashItemModFlag.UNK42.contains(flags)) {
            mplew.write(0);
        }
    }

    public static byte[] playCashSong(int itemid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_PlayJukeBox.getValue());
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static byte[] addCharBox(MapleCharacter c, int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserMiniRoomBalloon.getValue());
        mplew.writeInt(c.getId());
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static byte[] removeCharBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserMiniRoomBalloon.getValue());
        mplew.writeInt(c.getId());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] useCharm(int type, byte charmsleft, byte daysleft, int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_ProtectOnDieItemUse.getValue());
        mplew.writeInt(type);
        mplew.write(charmsleft);
        mplew.write(daysleft);
        switch (type) {
            case 1: 
            case 2: {
                break;
            }
            default: {
                mplew.writeInt(itemId);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] useWheel(byte charmsleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_UpgradeTombItemUse.getValue());
        mplew.write(charmsleft);
        return mplew.getPacket();
    }

    public static byte[] sendGoldHammerResult(int n, int n2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_GoldHammerResult.getValue());
        mplew.write(0);
        mplew.write(n);
        mplew.writeInt(n2);
        return mplew.getPacket();
    }

    public static byte[] sendPlatinumHammerResult(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_PlatinumHammerResult.getValue());
        mplew.write(value);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] changePetFlag(int uniqueId, boolean added, int flagAdded) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserPetSkillChanged.getValue());
        mplew.writeLong(uniqueId);
        mplew.write(added ? 1 : 0);
        mplew.writeShort(flagAdded);
        return mplew.getPacket();
    }

    public static byte[] changePetName(MapleCharacter chr, String newname, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_PetNameChanged.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(0);
        mplew.writeMapleAsciiString(newname);
        mplew.writeInt(slot);
        return mplew.getPacket();
    }

    public static byte[] MemoLoad(List<MemoEntry> memos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MemoResult.getValue());
        mplew.write(MemoOptType.MemoRes_Load.getValue());
        mplew.write(memos.size());
        for (MemoEntry memo : memos) {
            mplew.writeInt(memo.id);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(memo.sender);
            mplew.writeMapleAsciiString(memo.message);
            mplew.writeLong(PacketHelper.getTime(memo.timestamp));
            mplew.write(memo.pop);
        }
        return mplew.getPacket();
    }

    public static byte[] MemoSend() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MemoResult.getValue());
        mplew.write(MemoOptType.MemoRes_Send_Succeed.getValue());
        return mplew.getPacket();
    }

    public static byte[] MemoWarn(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MemoResult.getValue());
        mplew.write(MemoOptType.MemoRes_Send_Warning.getValue());
        mplew.write(type);
        return mplew.getPacket();
    }

    public static byte[] MemoReceive() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MemoResult.getValue());
        mplew.write(MemoOptType.MemoNotify_Receive.getValue());
        return mplew.getPacket();
    }

    public static byte[] MemoDelete() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MemoResult.getValue());
        mplew.write(MemoOptType.MemoRes_Delete_Succeed.getValue());
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] useChalkboard(int charid, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserADBoard.getValue());
        mplew.writeInt(charid);
        if (msg == null || msg.length() == 0) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static byte[] getTrockRefresh(MapleCharacter chr, byte vip, boolean delete) {
        MaplePacketLittleEndianWriter mplew;
        block4: {
            block5: {
                block3: {
                    mplew = new MaplePacketLittleEndianWriter();
                    mplew.writeShort(OutHeader.LP_MapTransferResult.getValue());
                    mplew.write(delete ? 2 : 3);
                    mplew.write(vip);
                    if (vip != 1) break block3;
                    int[] map = chr.getRegRocks();
                    for (int i = 0; i <= 4; ++i) {
                        mplew.writeInt(map[i]);
                    }
                    break block4;
                }
                if (vip != 2) break block5;
                int[] map = chr.getRocks();
                for (int i = 0; i <= 9; ++i) {
                    mplew.writeInt(map[i]);
                }
                break block4;
            }
            if (vip != 3) break block4;
            int[] map = chr.getHyperRocks();
            for (int i = 0; i <= 12; ++i) {
                mplew.writeInt(map[i]);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getTrockMessage(byte op) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MapTransferResult.getValue());
        mplew.writeShort(op);
        return mplew.getPacket();
    }

    public static byte[] enableCSUse(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CS_USE.getValue());
        mplew.write(type);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] updatePotionPot(MaplePotionPot potionPot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        return mplew.getPacket();
    }

    public static byte[] CashShopQueryCashResult(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopQueryCashResult.getValue());
        mplew.writeInt(chr.getCSPoints(1));
        mplew.writeInt(chr.getCSPoints(2));
        mplew.writeInt(chr.getMileage());
        return mplew.getPacket();
    }

    public static byte[] SetItemDayTime(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SET_ITEM_DAY_TIME.getValue());
        long Time2 = System.currentTimeMillis();
        mplew.writeInt(Time2);
        return mplew.getPacket();
    }

    public static byte[] showMileageInfo(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CS_CheckMileageResult.getValue());
        mplew.writeInt(c.getMileage());
        List<Pair<Triple<Integer, Integer, Integer>, Long>> rechargeRecordList = c.getMileageRechargeRecords();
        mplew.writeInt(rechargeRecordList.size());
        for (Pair<Triple<Integer, Integer, Integer>, Long> pair : rechargeRecordList) {
            mplew.writeLong(PacketHelper.getTime(pair.getRight()));
            mplew.writeInt(pair.getLeft().getLeft());
            mplew.writeInt(pair.getLeft().getMid());
            mplew.writeInt(pair.getLeft().getRight());
        }
        List<Pair<Triple<Integer, Integer, Integer>, Long>> purchaseRecordList = c.getMileagePurchaseRecords();
        mplew.writeInt(purchaseRecordList.size());
        for (Pair<Triple<Integer, Integer, Integer>, Long> pair : purchaseRecordList) {
            mplew.writeLong(PacketHelper.getTime(pair.getRight()));
            mplew.writeInt(pair.getLeft().getLeft());
            mplew.writeInt(pair.getLeft().getMid());
            mplew.writeInt(pair.getLeft().getRight());
            mplew.writeInt(1);
        }
        List<Pair<Integer, Long>> list = c.getMileageRecords();
        mplew.writeInt(list.size());
        for (Pair<Integer, Long> record : list) {
            mplew.writeInt(record.getLeft());
            mplew.writeLong(PacketHelper.getTime(record.getRight()));
        }
        return mplew.getPacket();
    }

    public static byte[] updataMeso(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCharStatChanged.getValue());
        mplew.writeLong(MapleStat.MONEY.getValue());
        mplew.writeLong(chr.getMeso());
        return mplew.getPacket();
    }

    public static byte[] loadLockerDone(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)6);
        CashShop mci = c.getPlayer().getCashInventory();
        mplew.write(0);
        mplew.writeShort(mci.getItemsSize());
        for (Item itemz : mci.getInventory()) {
            MTSCSPacket.addCashItemInfo(mplew, itemz, c.getAccID(), 0);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
        }
        mplew.writeInt(0);
        mplew.writeShort(c.getPlayer().getTrunk().getSlots());
        mplew.writeShort(c.getAccCharSlots());
        mplew.writeShort(0);
        mplew.writeShort(c.getPlayer().getMapleUnion().getAllUnions().size());
        return mplew.getPacket();
    }

    public static byte[] 商城禮物信息(List<? extends Pair<Item, String>> gifts) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)9);
        mplew.writeShort(gifts.size());
        for (Pair<Item, String> pair : gifts) {
            mplew.writeLong(pair.getLeft().getSN());
            mplew.writeInt(pair.getLeft().getItemId());
            mplew.writeAsciiString(pair.getLeft().getGiftFrom(), 15);
            mplew.writeAsciiString(pair.getRight(), 75);
        }
        return mplew.getPacket();
    }

    public static byte[] sendWishList(MapleCharacter chr, boolean update) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write(update ? (byte)13 : 11);
        int[] list = chr.getWishlist();
        for (int i = 0; i < 12; ++i) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }
        return mplew.getPacket();
    }

    public static byte[] CashItemBuyDone(Item item, int sn, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)15);
        MTSCSPacket.addCashItemInfo(mplew, item, accid, sn);
        return mplew.getPacket();
    }

    public static byte[] 商城送禮(int itemid, int quantity, String receiver) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)24);
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemid);
        mplew.writeShort(quantity);
        return mplew.getPacket();
    }

    public static byte[] 擴充道具欄(int inv, int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)26);
        mplew.write(inv);
        mplew.writeShort(slots);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] 擴充倉庫(int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)28);
        mplew.writeShort(slots);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] 購買角色卡(int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)-2);
        mplew.writeShort(slots);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] 擴充項鏈(int days) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)31);
        mplew.writeShort(0);
        mplew.writeShort(days);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] moveItemToInvFormCs(Item item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)32);
        mplew.write(item.getQuantity());
        mplew.writeShort(item.getPosition());
        PacketHelper.GW_ItemSlotBase_Encode(mplew, item);
        mplew.writeInt(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] moveItemToCsFromInv(Item item, int accId, int sn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)34);
        mplew.write(0);
        MTSCSPacket.addCashItemInfo(mplew, item, accId, sn);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] 商城刪除道具(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)36);
        mplew.writeLong(uniqueid);
        return mplew.getPacket();
    }

    public static byte[] cashItemExpired(long uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)38);
        mplew.writeLong(uniqueid);
        return mplew.getPacket();
    }

    public static byte[] 商城換購道具(int uniqueId, int Money) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)67);
        mplew.writeLong(uniqueId);
        mplew.writeInt(Money);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] 商城購買禮包(Map<Integer, ? extends Item> packageItems, int accId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)75);
        mplew.write(packageItems.size());
        for (Map.Entry<Integer, ? extends Item> it : packageItems.entrySet()) {
            MTSCSPacket.addCashItemInfo(mplew, it.getValue(), accId, it.getKey());
            mplew.write(0);
        }
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] 商城送禮包(int itemId, int quantity, String receiver) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)77);
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static byte[] 商城購買任務道具(int price, short quantity, byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)81);
        mplew.writeInt(1);
        mplew.writeInt(quantity);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static byte[] 楓點兌換道具() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)-106);
        return mplew.getPacket();
    }

    public static byte[] 購買記錄(int sn, int quantity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)104);
        mplew.writeInt(sn);
        mplew.writeInt(quantity);
        return mplew.getPacket();
    }

    public static void addCashItemInfo(MaplePacketLittleEndianWriter mplew, Item item, int accId, int sn) {
        CashItemFactory cashinfo = CashItemFactory.getInstance();
        mplew.writeInt(item.getSN() > 0 ? item.getSN() : 0);
        mplew.writeInt(0);
        mplew.writeInt(accId);
        mplew.writeInt(0);
        mplew.writeInt(item.getItemId());
        mplew.writeInt(sn);
        mplew.writeShort(item.getQuantity());
        mplew.writeZeroBytes(15);
        mplew.writeLong(150842304000000000L);
        mplew.writeZeroBytes(23);
        mplew.write(1);
        mplew.write(2);
        mplew.writeInt(item.getItemId());
        mplew.write(1);
        mplew.writeInt(sn > 0 ? sn : cashinfo.getSnFromId(cashinfo.getLinkItemId(item.getItemId())));
        mplew.writeInt(0);
        mplew.writeLong(150842304000000000L);
        mplew.writeInt(-1);
        mplew.write(0);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.write(3);
        mplew.write(2);
        mplew.write(3);
        mplew.writeInt(3);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
    }

    public static byte[] 商城錯誤提示(int err) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)25);
        mplew.write(err);
        return mplew.getPacket();
    }

    public static byte[] showCouponRedeemedItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.writeShort(-2);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(26);
        mplew.writeInt(itemid);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] showCouponRedeemedItem(Map<Integer, ? extends Item> items, int mesos, int maplePoints, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)-2);
        mplew.write(items.size());
        for (Map.Entry<Integer, ? extends Item> it : items.entrySet()) {
            MTSCSPacket.addCashItemInfo(mplew, it.getValue(), c.getAccID(), it.getKey());
            mplew.write(0);
        }
        mplew.writeInt(maplePoints);
        mplew.writeInt(0);
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static byte[] redeemResponse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write((byte)-2);
        mplew.writeInt(0);
        mplew.writeInt(1);
        return mplew.getPacket();
    }

    public static byte[] 商城打開箱子(Item item, Long uniqueId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopCashItemResult.getValue());
        mplew.write(114);
        mplew.writeLong(uniqueId);
        mplew.writeInt(0);
        PacketHelper.GW_ItemSlotBase_Encode(mplew, item);
        mplew.writeInt(item.getPosition());
        mplew.writeZeroBytes(3);
        return mplew.getPacket();
    }

    public static int getTime() {
        return Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    }

    public static byte[] sendMesobagFailed() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MesoGive_Failed.getValue());
        return mplew.getPacket();
    }

    public static byte[] sendMesobagSuccess(int mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MesoGive_Succeeded.getValue());
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static byte[] sendMTS(List<? extends MTSStorage.MTSItemInfo> items, int tab, int type, int page, int pages) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(21);
        mplew.writeInt(pages);
        mplew.writeInt(items.size());
        mplew.writeInt(tab);
        mplew.writeInt(type);
        mplew.writeInt(page);
        mplew.write(1);
        mplew.write(1);
        for (MTSStorage.MTSItemInfo mTSItemInfo : items) {
            MTSCSPacket.addMTSItemInfo(mplew, mTSItemInfo);
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showMTSCash(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.GET_MTS_TOKENS.getValue());
        mplew.writeInt(chr.getCSPoints(2));
        return mplew.getPacket();
    }

    public static byte[] getMTSWantedListingOver(int nx, int items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(61);
        mplew.writeInt(nx);
        mplew.writeInt(items);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmSell() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(29);
        return mplew.getPacket();
    }

    public static byte[] getMTSFailSell() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(30);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmBuy() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(51);
        return mplew.getPacket();
    }

    public static byte[] getMTSFailBuy() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(52);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmCancel() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(37);
        return mplew.getPacket();
    }

    public static byte[] getMTSFailCancel() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(38);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmTransfer(int quantity, int pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(39);
        mplew.writeInt(quantity);
        mplew.writeInt(pos);
        return mplew.getPacket();
    }

    private static void addMTSItemInfo(MaplePacketLittleEndianWriter mplew, MTSStorage.MTSItemInfo item) {
        PacketHelper.GW_ItemSlotBase_Encode(mplew, item.getItem());
        mplew.writeInt(item.getId());
        mplew.writeInt(item.getTaxes());
        mplew.writeInt(item.getPrice());
        mplew.writeZeroBytes(8);
        mplew.writeLong(PacketHelper.getTime(item.getEndingDate()));
        mplew.writeMapleAsciiString(item.getSeller());
        mplew.writeMapleAsciiString(item.getSeller());
        mplew.writeZeroBytes(28);
    }

    public static byte[] getNotYetSoldInv(List<? extends MTSStorage.MTSItemInfo> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(35);
        mplew.writeInt(items.size());
        for (MTSStorage.MTSItemInfo mTSItemInfo : items) {
            MTSCSPacket.addMTSItemInfo(mplew, mTSItemInfo);
        }
        return mplew.getPacket();
    }

    public static byte[] getTransferInventory(List<? extends Item> items, boolean changed) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write(33);
        mplew.writeInt(items.size());
        int i = 0;
        for (Item item : items) {
            PacketHelper.GW_ItemSlotBase_Encode(mplew, item);
            mplew.writeInt(Integer.MAX_VALUE - i);
            mplew.writeZeroBytes(56);
            ++i;
        }
        mplew.writeInt(-47 + i - 1);
        mplew.write(changed ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] addToCartMessage(boolean fail, boolean remove) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        if (remove) {
            if (fail) {
                mplew.write(44);
                mplew.writeInt(-1);
            } else {
                mplew.write(43);
            }
        } else if (fail) {
            mplew.write(42);
            mplew.writeInt(-1);
        } else {
            mplew.write(41);
        }
        return mplew.getPacket();
    }

    public static byte[] sendBuyFailed(byte result) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write((byte)16);
        mplew.write(result);
        return mplew.getPacket();
    }

    public static byte[] sendCoupleDone(int accId, String s, int sn, Item item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write((byte)73);
        MTSCSPacket.addCashItemInfo(mplew, item, accId, sn);
        mplew.write(0);
        mplew.writeMapleAsciiString(s);
        mplew.writeInt(item.getItemId());
        mplew.writeShort(item.getQuantity());
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] sendCoupleFailed(byte result) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MTS_OPERATION.getValue());
        mplew.write((byte)74);
        mplew.write(result);
        if (result == 31 || result == 30) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static byte[] RechargeWeb(String website) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopChargeParamResult.getValue());
        mplew.writeMapleAsciiString(website);
        return mplew.getPacket();
    }

    public static byte[] showAvatarRandomBox(boolean full, long cashId, int quantity, Item item, int accid, boolean showItemName, boolean smega) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CS_AVATAR_RANDOM_BOX.getValue());
        int ful = 148;
        int notful = 147;
        mplew.write(full ? ful : notful);
        if (!full) {
            mplew.writeLong(cashId);
            mplew.writeInt(quantity);
            MTSCSPacket.addCashItemInfo(mplew, item, accid, 0);
            mplew.write(0);
            PacketHelper.GW_ItemSlotBase_Encode(mplew, item);
            mplew.writeInt(item.getItemId());
            mplew.write(showItemName);
            mplew.write(smega);
        }
        return mplew.getPacket();
    }

    public static byte[] getCashShopPreviewInfo() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_CashShopPreviewInfo.getValue());
        int unkCouponTypeSize = 0;
        mplew.write((byte)unkCouponTypeSize);
        for (int i = 0; i < unkCouponTypeSize; ++i) {
            int unkCouponSize = 0;
            mplew.write(unkCouponSize > 0);
            if (unkCouponSize <= 0) continue;
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(unkCouponSize);
            for (int j = 0; j < unkCouponSize; ++j) {
                mplew.writeInt(0);
                mplew.writeInt(0);
            }
        }
        int unkCoupon2TypeSize = 0;
        mplew.write((byte)unkCoupon2TypeSize);
        for (int i = 0; i < unkCoupon2TypeSize; ++i) {
            int unkCoupon2Size = 0;
            mplew.write(unkCoupon2Size > 0);
            if (unkCoupon2Size <= 0) continue;
            mplew.writeInt(0);
            int unkCoupon2RewardListSize = 0;
            mplew.writeShort(unkCoupon2RewardListSize);
            for (int j = 0; j < unkCoupon2RewardListSize; ++j) {
                int unkCoupon2RewardSize = 0;
                mplew.write(unkCoupon2RewardSize > 0);
                if (unkCoupon2RewardSize <= 0) continue;
                mplew.writeShort(unkCoupon2RewardSize);
                for (int k = 0; k < unkCoupon2RewardSize; ++k) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                }
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getCashShopStyleCouponPreviewInfo() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CashShopStyleCouponPreviewInfo.getValue());
        mplew.write(0);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        Map<Integer, Pair<List<Integer>, List<Integer>>> couponList = RafflePool.getRoyalCouponList();
        mplew.writeInt(couponList.size());
        for (Map.Entry<Integer, Pair<List<Integer>, List<Integer>>> couponInfo : couponList.entrySet()) {
            mplew.writeInt(0);
            mplew.writeInt(couponInfo.getKey());
            mplew.writeInt((couponInfo.getValue().getLeft() == null ? 0 : couponInfo.getValue().getLeft().size()) + (couponInfo.getValue().getRight() == null ? 0 : couponInfo.getValue().getRight().size()));
            mplew.writeLong(PacketHelper.getTime(-2L));
            mplew.writeLong(PacketHelper.getTime(-1L));
            mplew.writeInt((couponInfo.getValue().getLeft() == null || couponInfo.getValue().getLeft().isEmpty() ? 0 : 1) + (couponInfo.getValue().getRight() == null || couponInfo.getValue().getRight().isEmpty() ? 0 : 1));
            if (couponInfo.getValue().getLeft() != null && !couponInfo.getValue().getLeft().isEmpty()) {
                mplew.write(0);
                mplew.writeInt(2);
                mplew.writeInt(couponInfo.getValue().getLeft().size());
                for (int styleID : couponInfo.getValue().getLeft()) {
                    mplew.writeInt(styleID);
                }
            }
            if (couponInfo.getValue().getRight() == null || couponInfo.getValue().getRight().isEmpty()) continue;
            mplew.write(1);
            mplew.writeInt(2);
            mplew.writeInt(couponInfo.getValue().getRight().size());
            for (int styleID : couponInfo.getValue().getRight()) {
                mplew.writeInt(styleID);
            }
        }
        return mplew.getPacket();
    }
}

