/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MaplePartTimeJob
 *  Client.inventory.MaplePotionPot
 *  Client.inventory.MapleRing
 *  Net.server.shop.BuyLimitData
 *  Net.server.shop.NpcShopBuyLimit
 *  Net.server.shops.AbstractPlayerStore
 *  Net.server.shops.MapleMiniGame
 */
package Packet;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleCoolDownValueHolder;
import Client.MaplePartTimeJob;
import Client.MapleQuestStatus;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.hexa.MapleHexaSkill;
import Client.hexa.MapleHexaStat;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleAndroid;
import Client.inventory.MapleInventory;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.inventory.MaplePotionPot;
import Client.inventory.MapleRing;
import Client.skills.InnerSkillEntry;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.stat.MapleHyperStats;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Net.server.MapleItemInformationProvider;
import Net.server.movement.LifeMovementFragment;
import Net.server.quest.MapleQuest;
import Net.server.shop.BuyLimitData;
import Net.server.shop.MapleShop;
import Net.server.shop.MapleShopFactory;
import Net.server.shop.MapleShopItem;
import Net.server.shop.NpcShopBuyLimit;
import Net.server.shops.AbstractPlayerStore;
import Net.server.shops.IMaplePlayerShop;
import Net.server.shops.MapleMiniGame;
import Packet.VCorePacket;
import connection.OutPacket;
import java.awt.Point;
import java.util.*;

import tools.DateUtil;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class PacketHelper {
    public static final long MAX_TIME = 150842304000000000L;
    public static final long ZERO_TIME = 94354848000000000L;
    public static final long PERMANENT = 150841440000000000L;

    public static long getKoreanTimestamp(long realTimestamp) {
        return realTimestamp * 10000L + 116445060000000000L;
    }

    public static final long getTime(long realTimestamp) {
        if (realTimestamp == -1L) {
            return 150842304000000000L;
        }
        if (realTimestamp == -2L) {
            return 94354848000000000L;
        }
        if (realTimestamp == -3L) {
            return 94354848000000000L;
        }
        if (realTimestamp == -4L) {
            return 94354848000000000L;
        }
        return realTimestamp * 10000L + 116445060000000000L;
    }

    public static void addPotionPotInfo(MaplePacketLittleEndianWriter mplew, MaplePotionPot potionPot) {
        mplew.writeInt(potionPot.getItmeId());
        mplew.writeInt(potionPot.getChrId());
        mplew.writeInt(potionPot.getMaxValue());
        mplew.writeInt(potionPot.getHp());
        mplew.writeInt(potionPot.getMp());
        mplew.writeLong(PacketHelper.getTime(potionPot.getStartDate()));
        mplew.writeLong(PacketHelper.getTime(potionPot.getEndDate()));
    }

    public static void writeBuffMask(MaplePacketLittleEndianWriter mplew, Collection<Pair<SecondaryStat, Pair<Integer, Integer>>> statups) {
        int[] mask = new int[31];
        for (Pair<SecondaryStat, Pair<Integer, Integer>> statup : statups) {
            mask[((SecondaryStat)statup.left).getPosition() - 1] = mask[((SecondaryStat)statup.left).getPosition() - 1] | ((SecondaryStat)statup.left).getValue();
        }
        for (int i = mask.length; i >= 1; --i) {
            mplew.writeInt(mask[i - 1]);
        }
    }

    public static List<Pair<SecondaryStat, List<SecondaryStatValueHolder>>> sortIndieBuffStats(Map<SecondaryStat, List<SecondaryStatValueHolder>> statups) {
        boolean changed;
        List<Pair<SecondaryStat, List<SecondaryStatValueHolder>>> statvals = new ArrayList<>();
        for (Map.Entry<SecondaryStat, List<SecondaryStatValueHolder>> stat : statups.entrySet()) {
            statvals.add(new Pair<SecondaryStat, List<SecondaryStatValueHolder>>(stat.getKey(), stat.getValue()));
        }
        do {
            changed = false;
            int i = 0;
            int k = 1;
            for (int iter = 0; iter < statvals.size() - 1; ++iter) {
                Pair<SecondaryStat, List<SecondaryStatValueHolder>> a = statvals.get(i);
                Pair<SecondaryStat, List<SecondaryStatValueHolder>> b = statvals.get(k);
                if (a != null && b != null && ((SecondaryStat)a.left).getFlag() > ((SecondaryStat)b.left).getFlag()) {
                    Pair<SecondaryStat, List<SecondaryStatValueHolder>> swap = new Pair<>(a.left, a.right);
                    statvals.remove(i);
                    statvals.add(i, b);
                    statvals.remove(k);
                    statvals.add(k, swap);
                    changed = true;
                }
                ++i;
                ++k;
            }
        } while (changed);
        return statvals;
    }

    public static void addPartTimeJob(MaplePacketLittleEndianWriter mplew, MaplePartTimeJob parttime) {
        mplew.write(parttime.getJob());
        if (parttime.getJob() > 0 && parttime.getJob() <= 5) {
            mplew.writeReversedLong(parttime.getTime());
        } else {
            mplew.writeReversedLong(PacketHelper.getTime(-2L));
        }
        mplew.writeInt(parttime.getReward());
        mplew.writeBool(parttime.getReward() > 0);
    }

    public static void addExpirationTime(MaplePacketLittleEndianWriter mplew, long time) {
        mplew.writeLong(PacketHelper.getTime(time));
    }

    public static void addItemPosition(MaplePacketLittleEndianWriter mplew, Item item, boolean trade, boolean bagSlot) {
        short pos;
        if (item == null) {
            pos = 0;
        } else {
            pos = item.getPosition();
            if (pos < 0 && (pos = (short)Math.abs(pos)) > 100 && pos < 1000) {
                pos = (short)(pos - 100);
            }
            if (bagSlot) {
                pos = (short)(pos % 100 - 1);
            }
        }
        if (bagSlot) {
            mplew.writeInt(pos);
        } else if (!trade) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
    }

    public static void GW_ItemSlotBase_Encode(MaplePacketLittleEndianWriter mplew, Item item) {
        if (item == null) {
            throw new NullPointerException("addItemInfo item is null.");
        }
        OutPacket outPacket = new OutPacket();
        item.encode(outPacket);
        mplew.write(outPacket.getData());
    }

    public static void serializeMovementList(MaplePacketLittleEndianWriter mplew, int gatherDuration, int nVal1, Point mPos, Point oPos, List<LifeMovementFragment> moves, int[] arrays) {
        mplew.writeInt(gatherDuration);
        mplew.writeInt(nVal1);
        mplew.writePos(mPos);
        mplew.writePos(oPos);
        if (moves == null) {
            mplew.writeShort(0);
        } else {
            mplew.writeShort(moves.size());
            for (LifeMovementFragment move : moves) {
                move.serialize(mplew);
            }
        }
        if (arrays != null) {
            mplew.writeShort(arrays.length);
            for (int nVal : arrays) {
                mplew.writeInt(nVal); // Assuming nVal should be written as an integer
            }
        }
    }

    public static void addAnnounceBox(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1 && chr.getPlayerShop().isAvailable()) {
            PacketHelper.addInteraction(mplew, chr.getPlayerShop());
        } else {
            mplew.write(0);
        }
    }

    public static void addInteraction(MaplePacketLittleEndianWriter mplew, IMaplePlayerShop shop) {
        mplew.write(shop.getGameType());
        mplew.writeInt(((AbstractPlayerStore)shop).getObjectId());
        mplew.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            mplew.write(shop.getPassword().length() > 0 ? 1 : 0);
        }
        int id = shop.getItemId() % 100;
        if (shop instanceof MapleMiniGame) {
            MapleMiniGame mini = (MapleMiniGame)shop;
            id = mini.getPieceType();
        } else if (shop.getShopType() == 75) {
            id = 0;
        }
        mplew.write(id);
        mplew.write(shop.getShopType() != 75 ? shop.getSize() : 0);
        mplew.write(shop.getMaxSize());
        if (shop.getShopType() != 1) {
            mplew.write(shop.isOpen() ? 0 : 1);
        }
        PacketHelper.addChaterName(mplew, "", "");
    }

    /*
     * WARNING - void declaration
     */
    public static void addCharacterInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, long flag) {

        for(int i = 0; i < 100; ++i) {
            mplew.write(1);
        }

        mplew.write(0);

        for(int i = 0; i < 3; ++i) {
            mplew.writeInt(!JobConstants.is皇家騎士團(chr.getJob()) && !JobConstants.is米哈逸(chr.getJob()) ? -1 : -6);
        }

        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(0);
        if ((flag & 1L) != 0L) {
            chr.getCharacterStat().encode(mplew);
            mplew.write(chr.getBuddylist().getCapacity());
            mplew.write(chr.getBlessOfFairyOrigin() != null);
            if (chr.getBlessOfFairyOrigin() != null) {
                mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
            }

            mplew.write(chr.getBlessOfEmpressOrigin() != null);
            if (chr.getBlessOfEmpressOrigin() != null) {
                mplew.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
            }

            MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
            mplew.write(ultExplorer != null && ultExplorer.getCustomData() != null);
            if (ultExplorer != null && ultExplorer.getCustomData() != null) {
                mplew.writeMapleAsciiString(ultExplorer.getCustomData());
            }

            mplew.writeLong(133709900400000000L);
            mplew.writeHexString("00 40 E0 FD 3B 37 4F 01");
            int v7 = 2;

            do {
                mplew.writeInt(0);

                while(true) {
                    int res = -1;
                    mplew.write(res);
                    if (res == -1) {
                        v7 += 36;
                        break;
                    }

                    mplew.writeInt(0);
                }
            } while(v7 < 74);
        }

        mplew.writeShort(0);
        mplew.writeLong(getTime(-2L));
        String questInfo = chr.getOneInfo(56829, "count");
        mplew.writeInt(questInfo == null ? ServerConfig.defaultDamageSkinSlot : Integer.valueOf(questInfo));
        addDamageSkinInfo(mplew, chr);
        if ((flag & 2L) != 0L) {
            mplew.writeLong(chr.getMeso());
            mplew.writeInt(chr.getId());
            mplew.writeInt(chr.getBeans());
            mplew.writeInt(chr.getCSPoints(2));
        }

        if ((flag & 64L) != 0L) {
            mplew.writeInt(chr.getInventory(MapleInventoryType.EQUIPPED).getSlotLimit());
            mplew.writeInt(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            mplew.writeInt(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
            mplew.writeInt(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
            mplew.writeInt(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
            mplew.writeInt(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());
            mplew.writeInt(chr.getInventory(MapleInventoryType.DECORATION).getSlotLimit());
        }

        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        List<Item> equippedList = iv.newList();
        Collections.sort(equippedList);
        List<Item> equipped = new ArrayList();
        List<Item> equippedCash = new ArrayList();
        List<Item> equippedDragon = new ArrayList();
        List<Item> equippedMechanic = new ArrayList();
        List<Item> equippedAndroid = new ArrayList();
        List<Item> equippedLolitaCash = new ArrayList();
        List<Item> equippedBit = new ArrayList();
        List<Item> equippedZeroBetaCash = new ArrayList();
        List<Item> equippedArcane = new ArrayList();
        List<Item> equippedAuthenticSymbol = new ArrayList();
        List<Item> equippedTotem = new ArrayList();
        List<Item> equippedMonsterEqp = new ArrayList<>(); // 獸魔裝備
        List<Item> equippedHakuFan = new ArrayList();
        List<Item> equippedUnknown = new ArrayList();
        List<Item> equippedCashPreset = new ArrayList();
        Iterator equppedIterator = equippedList.iterator();

        while(true) {
            while(equppedIterator.hasNext()) {
                Item item = (Item)equppedIterator.next();
                if (item.getPosition() < 0 && item.getPosition() > -100) {
                    equipped.add(item);
                } else if (item.getPosition() <= -1000 && item.getPosition() > -1100) {
                    equippedDragon.add(item);
                } else if (item.getPosition() <= -1100 && item.getPosition() > -1200) {
                    equippedMechanic.add(item);
                } else if (item.getPosition() <= -1400 && item.getPosition() > -1500) {
                    equippedBit.add(item);
                } else if (item.getPosition() <= -5000 && item.getPosition() >= -5002) {
                    equippedTotem.add(item);
                } else if (item.getPosition() <= -1600 && item.getPosition() > -1700) {
                    equippedArcane.add(item);
                } else if (item.getPosition() <= -1700 && item.getPosition() > -1800) {
                    equippedAuthenticSymbol.add(item);
                } else if (item.getPosition() <= -1200 && item.getPosition() > -1300) {
                    equippedAndroid.add(item);
                } else if (item.getPosition() <= -1300 && item.getPosition() > -1400) {
                    equippedLolitaCash.add(item);
                } else if (item.getPosition() <= -1500 && item.getPosition() > -1600) {
                    equippedZeroBetaCash.add(item);
                } else if (item.getPosition() <= -100 && item.getPosition() > -1000) {
                    equippedCash.add(item);
                } else if (item.getPosition() > 10000 && item.getPosition() < 10200) {
                    chr.getSkillSkin().put(MapleItemInformationProvider.getInstance().getSkillSkinFormSkillId(item.getItemId()), item.getItemId());
                    equippedUnknown.add(item);
                }
            }

//            Iterator var26;
//            ArrayList items;
            if ((flag & 128L) != 0L) {
                mplew.writeBool(false);
                iv = chr.getInventory(MapleInventoryType.EQUIP);
                List<MapleAndroid> androids = new LinkedList<>();
                List<Item> items = new ArrayList();

                List<Item> items20000 = new ArrayList<>();
                List<Item> items21000 = new ArrayList<>();
                List<Item> equip = new ArrayList<>();
                for (Item item : iv.list()) {
                    if (((Equip) item).getAndroid() != null) {
                        androids.add(((Equip) item).getAndroid());
                    }
                    if (item.getPosition() >= 21000) {
                        items21000.add(item);
                    } else if (item.getPosition() >= 20000) {
                        items20000.add(item);
                    } else {
                        equip.add(item);
                    }
                }

                mplew.writeShort(0);
                encodeInventory(mplew, equipped, chr);
                encodeInventory(mplew, equip, chr);
                encodeInventory(mplew, equippedDragon, chr);
                encodeInventory(mplew, equippedMechanic, chr);
                encodeInventory(mplew, equippedBit, chr);
                encodeInventory(mplew, equippedTotem, chr);
                encodeInventory(mplew, equippedArcane, chr);
                encodeInventory(mplew, equippedAuthenticSymbol, chr);
                encodeInventory(mplew, equippedHakuFan, chr);
                mplew.writeShort(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
                encodeInventory(mplew, equippedTotem, chr);
                encodeInventory(mplew, equippedUnknown, chr);
                encodeInventory(mplew, items, chr);
                encodeInventory(mplew, items21000, chr);
            }

            if ((flag & 16L) != 0L) {
                encodeInventory(mplew, Collections.emptyList(), chr);
                encodeInventory(mplew, Collections.emptyList(), chr);
            }

            Item item;
            if ((flag & 8192L) != 0L) {
                mplew.writeBool(false);
                encodeInventory(mplew, equippedCash, chr);
                iv = chr.getInventory(MapleInventoryType.DECORATION);
                List pets = new ArrayList();
                Iterator decorationIterator = iv.list().iterator();

                while(decorationIterator.hasNext()) {
                    item = (Item)decorationIterator.next();
                    if (item.getPosition() < 129) {
                        pets.add(item);
                    }
                }

                encodeInventory(mplew, pets, chr);
                encodeInventory(mplew, equippedAndroid, chr);
                encodeInventory(mplew, equippedLolitaCash, chr);
                encodeInventory(mplew, equippedZeroBetaCash, chr);
                encodeInventory(mplew, equippedCashPreset, chr);
                mplew.writeShort(0);
                mplew.writeShort(0);
            }

            if ((flag & 8L) != 0L) {
                iv = chr.getInventory(MapleInventoryType.USE);
                List pets = new ArrayList();
                Iterator useIterator = iv.list().iterator();

                while(useIterator.hasNext()) {
                    item = (Item)useIterator.next();
                    if (item.getPosition() < 129) {
                        pets.add(item);
                    }
                }

                encodeInventory(mplew, pets, chr);
            }

            if ((flag & 16L) != 0L) {
                iv = chr.getInventory(MapleInventoryType.SETUP);
                List pets = new ArrayList();
                Iterator setUpIterator = iv.list().iterator();

                while(setUpIterator.hasNext()) {
                    item = (Item)setUpIterator.next();
                    if (item.getPosition() < 129) {
                        pets.add(item);
                    }
                }

                encodeInventory(mplew, pets, chr);
            }

            if ((flag & 128L) != 0L) {
                iv = chr.getInventory(MapleInventoryType.ETC);
                List etcList = new ArrayList();
                Iterator etcIterator = iv.list().iterator();

                while(etcIterator.hasNext()) {
                    item = (Item)etcIterator.next();
                    if (item.getPosition() < 129) {
                        etcList.add(item);
                    }
                }

                encodeInventory(mplew, etcList, chr);
            }


            List<MaplePet>pets = new ArrayList();
            Iterator var47;
            if ((flag & 64L) != 0L) {
                iv = chr.getInventory(MapleInventoryType.CASH);
                List<Item>items = new ArrayList();
                var47 = iv.list().iterator();

                while(var47.hasNext()) {
                    Item cashItem = (Item)var47.next();
                    items.add(cashItem);
                    if (cashItem.getPet() != null) {
                        pets.add(cashItem.getPet());
                    }
                }

                encodeInventory(mplew, items, chr);
            }

            // 加载扩展背包
            MapleInventoryType eiv;
            if ((flag & 32L) != 0L) {
                eiv = MapleInventoryType.USE;
                List<Item> started = chr.getExtendedSlots(eiv.getType());
                mplew.writeInt(started.size());
                Iterator itemIterator = started.iterator();

                while(itemIterator.hasNext()) {
                    Item useItem = (Item)itemIterator.next();
                    mplew.writeInt(useItem.getExtendSlot());
                    mplew.writeInt(useItem.getItemId());
                    chr.getInventory(eiv).list().stream().filter((itemx) -> {
                        return itemx.getPosition() > useItem.getExtendSlot() * 100 + 10100 && itemx.getPosition() < useItem.getExtendSlot() * 100 + 10200;
                    }).forEach((itemx) -> {
                        addItemPosition(mplew, itemx, false, true);
                        GW_ItemSlotBase_Encode(mplew, itemx);
                    });
                    mplew.writeInt(-1);
                }
            }

            if ((flag & 48L) != 0L) {
                eiv = MapleInventoryType.SETUP;
                List<Item> started = chr.getExtendedSlots(eiv.getType());
                mplew.writeInt(started.size());
                Iterator setupIterator = started.iterator();

                while(setupIterator.hasNext()) {
                    Item setupItem = (Item)setupIterator.next();
                    mplew.writeInt(setupItem.getExtendSlot());
                    mplew.writeInt(setupItem.getItemId());
                    chr.getInventory(eiv).list().stream().filter((itemx) -> {
                        return itemx.getPosition() > setupItem.getExtendSlot() * 100 + 10100 && itemx.getPosition() < setupItem.getExtendSlot() * 100 + 10200;
                    }).forEach((itemx) -> {
                        addItemPosition(mplew, itemx, false, true);
                        GW_ItemSlotBase_Encode(mplew, itemx);
                    });
                    mplew.writeInt(-1);
                }
            }

            if ((flag & 64L) != 0L) {
                eiv = MapleInventoryType.ETC;
                List<Item> started = chr.getExtendedSlots(eiv.getType());
                mplew.writeInt(started.size());
                Iterator etcIterator = started.iterator();

                while(etcIterator.hasNext()) {
                    Item etcItem = (Item)etcIterator.next();
                    mplew.writeInt(etcItem.getExtendSlot());
                    mplew.writeInt(etcItem.getItemId());
                    chr.getInventory(eiv).list().stream().filter((itemx) -> {
                        return itemx.getPosition() > etcItem.getExtendSlot() * 100 + 10100 && itemx.getPosition() < etcItem.getExtendSlot() * 100 + 10200;
                    }).forEach((itemx) -> {
                        addItemPosition(mplew, itemx, false, true);
                        GW_ItemSlotBase_Encode(mplew, itemx);
                    });
                    mplew.writeInt(-1);
                }
            }

            if ((flag & 16777216L) != 0L) {
                mplew.writeInt(0);
            }

            if ((flag & 1073741824L) != 0L) {
                mplew.writeInt(0);
            }

            if ((flag & 8388608L) != 0L) {
                mplew.write(0);
            }

            Map.Entry entry;
            if ((flag & 256L) != 0L) {
                Map<Integer, SkillEntry> skills = chr.getSkills(true);
                mplew.write(1);
                mplew.writeShort(skills.size());
                Iterator<Map.Entry<Integer, SkillEntry>> skillIterator = skills.entrySet().iterator();

                label955:
                while(true) {
                    Skill skill;
                    do {
                        if (!skillIterator.hasNext()) {
                            Map<Integer, SkillEntry> teachList = chr.getLinkSkills();
                            mplew.writeShort(teachList.size());
                            Iterator<Map.Entry<Integer, SkillEntry>> teachIterator = teachList.entrySet().iterator();

                            while(teachIterator.hasNext()) {
                                Map.Entry<Integer, SkillEntry> skillEntry = (Map.Entry)teachIterator.next();
                                mplew.writeInt((Integer)skillEntry.getKey());
                                mplew.writeShort(((SkillEntry)skillEntry.getValue()).skillevel - 1);
                            }

                            Map<Integer, Pair<Integer, SkillEntry>> sonOfLinkedSkills = chr.getSonOfLinkedSkills();
                            mplew.writeInt(sonOfLinkedSkills.size());
                            Iterator<Map.Entry<Integer, Pair<Integer, SkillEntry>>> linkSkilliterator = sonOfLinkedSkills.entrySet().iterator();

                            while(linkSkilliterator.hasNext()) {
                                Map.Entry<Integer, Pair<Integer, SkillEntry>> linkEntry = (Map.Entry)linkSkilliterator.next();
                                writeSonOfLinkedSkill(mplew, (Integer)linkEntry.getKey(), (Pair)linkEntry.getValue());
                            }

                            mplew.write((int)chr.getInfoQuestValueWithKey(2498, "hyperstats"));

                            for(int buffId = 0; buffId <= 2; ++buffId) {
                                mplew.writeInt(chr.loadHyperStats(buffId).size());
                                for (MapleHyperStats mhsz : chr.loadHyperStats(buffId)) {
                                    mplew.writeInt(mhsz.getPosition());
                                    mplew.writeInt(mhsz.getSkillid());
                                    mplew.writeInt(mhsz.getSkillLevel());
                                }
                            }
                            break label955;
                        }

                        entry = skillIterator.next();
                        skill = SkillFactory.getSkill((Integer)entry.getKey());
                        mplew.writeInt(skill.getId());
                        if (skill.isLinkSkills()) {
                            mplew.writeInt(((SkillEntry)entry.getValue()).teachId);
                        } else if (skill.isTeachSkills()) {
                            mplew.writeInt(((SkillEntry)entry.getValue()).teachId > 0 ? ((SkillEntry)entry.getValue()).teachId : chr.getId());
                        } else {
                            mplew.writeInt(((SkillEntry)entry.getValue()).skillevel);
                        }

                        addExpirationTime(mplew, ((SkillEntry)entry.getValue()).expiration);
                        if (skill.isFourthJob()) {
                            mplew.writeInt(((SkillEntry)entry.getValue()).masterlevel);
                        }
                    } while(skill.getId() != 40020002 && skill.getId() != 80000004);

                    mplew.writeInt(((SkillEntry)entry.getValue()).masterlevel);
                }
            }

            if ((flag & 32768L) != 0L) {
                List<MapleCoolDownValueHolder> cooldowns = chr.getCooldowns();
                mplew.writeShort(cooldowns.size());
                for (MapleCoolDownValueHolder cooling : cooldowns) {
                    mplew.writeInt(cooling.skillId);
                    int timeLeft = (int)((long)cooling.length + cooling.startTime - System.currentTimeMillis());
                    mplew.writeInt(timeLeft / 1000);
                }
            }

            int j;
            if ((flag & 1L) != 0L) {
                for(j = 0; j < 6; ++j) {
                    mplew.writeInt(0);
                }

                for(j = 0; j < 6; ++j) {
                    mplew.write(0);
                }
            }

            int kills;
            MapleQuestStatus q;
            Iterator var70;
            if ((flag & 512L) != 0L) {
                List<MapleQuestStatus> started = chr.getStartedQuests();
                boolean bUnk = true;
                mplew.write(bUnk);
                mplew.writeShort(started.size());
                Iterator<MapleQuestStatus> questIterator = started.iterator();

                while(true) {
                    while(questIterator.hasNext()) {
                        q = (MapleQuestStatus)questIterator.next();
                        mplew.writeInt(q.getQuest().getId());
                        if (q.hasMobKills()) {
                            StringBuilder sb = new StringBuilder();
                            var70 = q.getMobKills().values().iterator();

                            while(var70.hasNext()) {
                                kills = (Integer)var70.next();
                                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
                            }

                            mplew.writeMapleAsciiString(sb.toString());
                        } else {
                            mplew.writeMapleAsciiString(q.getCustomData() == null ? "" : q.getCustomData());
                        }
                    }

                    if (!bUnk) {
                        mplew.writeShort(0);
                    }

                    mplew.writeShort(0);
                    break;
                }
            }

//            int itemId;
//            int nQ;
//            List completed;
//            List mRing;
            if ((flag & 16384L) != 0L) {
                boolean bUnk = true;
                mplew.write(bUnk);
                List<MapleQuestStatus> completed = chr.getCompletedQuests();
                if (ServerConfig.HideBulbQuest) {
                    List<MapleQuest> questList = new LinkedList();
                    for (MapleQuestStatus mapleQuestStatus : completed) {
                        questList.add(mapleQuestStatus.getQuest());
                    }

                    List<Integer>ignoreQuest = Arrays.asList(5741, 5742, 5743, 5744, 5745);
                    List<MapleQuest> mapleQuests = MapleQuest.GetBulbQuest();
                    for (MapleQuest quest : mapleQuests) {
                        if (!questList.contains(quest) && !ignoreQuest.contains(quest.getId())) {
                            completed.add(new MapleQuestStatus(quest, 2));
                            questList.add(quest);
                        }
                    }

                    int[] extraQuests = new int[]{32510};
                    for (int nQ : extraQuests) {
                        MapleQuest quest = MapleQuest.getInstance(nQ);
                        if (!questList.contains(quest)) {
                            completed.add(new MapleQuestStatus(quest, 2));
                            questList.add(quest);
                        }
                    }
                }

                mplew.writeShort(completed.size());
                for (MapleQuestStatus mapleQuestStatus : completed) {
                    mplew.writeInt(mapleQuestStatus.getQuest().getId());
                    mplew.writeLong(getTime(mapleQuestStatus.getCompletionTime()));
                }

                if (!bUnk) {
                    mplew.writeShort(0);
                }
            }

            if ((flag & 1024L) != 0L) {
                mplew.writeShort(0);
            }
            // ring INFO

            if ((flag & 2048L) != 0L) {
                Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
                List<MapleRing> cRing = aRing.getLeft();
                mplew.writeShort(cRing.size());

                for (MapleRing ring : cRing) {
                    mplew.writeInt(ring.getPartnerChrId());
                    mplew.writeAsciiString(ring.getPartnerName(), 15);
                    mplew.writeLong((long)ring.getRingId());
                    mplew.writeLong((long)ring.getPartnerRingId());
                }

                List<MapleRing> fRing = (List)aRing.getMid();
                mplew.writeShort(fRing.size());
                for (MapleRing ring : fRing) {
                    mplew.writeInt(ring.getPartnerChrId());
                    mplew.writeAsciiString(ring.getPartnerName(), 15);
                    mplew.writeLong((long)ring.getRingId());
                    mplew.writeLong((long)ring.getPartnerRingId());
                    mplew.writeInt(ring.getItemId());
                }

                List<MapleRing> mRing = (List)aRing.getRight();
                mplew.writeShort(mRing.size());
                for (MapleRing ring : mRing) {
                    mplew.writeInt(chr.getMarriageId());
                    mplew.writeInt(chr.getId());
                    mplew.writeInt(ring.getPartnerChrId());
                    mplew.writeShort(3);
                    mplew.writeInt(ring.getItemId());
                    mplew.writeInt(ring.getItemId());
                    mplew.writeAsciiString(chr.getName(), 15);
                    mplew.writeAsciiString(ring.getPartnerName(), 15);
                }
            }

            // rocksInfo

            if ((flag & 0x1000L) != 0x0L) {
                int[] mapz = chr.getRegRocks();
                for (int i = 0; i < 5; i++) { // VIP teleport map
                    mplew.writeInt(mapz[i]);
                }
                int[] map = chr.getRocks();
                for (int i = 0; i < 10; i++) { // VIP teleport map
                    mplew.writeInt(map[i]);
                }
                int[] maps = chr.getHyperRocks();
                for (int i = 0; i < 13; i++) { // VIP teleport map
                    mplew.writeInt(maps[i]);
                }
            }

            // 268

            if ((flag & 262144L) != 0L) {
                LinkedHashMap wsInfos = new LinkedHashMap();
                Iterator<Map.Entry<Integer, String>> infoQuestIterator = chr.getInfoQuest_Map().entrySet().iterator();

                while(infoQuestIterator.hasNext()) {
                    entry = (Map.Entry)infoQuestIterator.next();
                    wsInfos.put((Integer)entry.getKey(), (String)entry.getValue());
                }

                Iterator<Map.Entry<Integer, String>> worldShareIterator = chr.getWorldShareInfo().entrySet().iterator();

                while(worldShareIterator.hasNext()) {
                    entry = (Map.Entry)worldShareIterator.next();
                    if (!GameConstants.isWorldShareQuest((Integer)entry.getKey())) {
                        wsInfos.put((Integer)entry.getKey(), (String)entry.getValue());
                    }
                }

                mplew.writeShort(wsInfos.size());
                Iterator wsIterator = wsInfos.entrySet().iterator();

                while(wsIterator.hasNext()) {
                    entry = (Map.Entry)wsIterator.next();
                    mplew.writeInt((Integer)entry.getKey());
                    mplew.writeMapleAsciiString(entry.getValue() == null ? "" : (String)entry.getValue());
                }
            }

            byte nCount;
            if ((flag & 524288L) != 0L) {
                nCount = 0;
                mplew.writeShort(nCount);

                for(int num = 0; num < nCount; ++num) {
                    mplew.writeInt(0);
                    mplew.writeShort(0);
                }
            }

            mplew.writeBool(true);
            if ((flag & 549755813888L) != 0L) {
                nCount = 0;
                mplew.writeInt(nCount);

                for(int num = 0; num < nCount; ++num) {
                    mplew.writeInt(26);
                    mplew.writeMapleAsciiString("Present=7");
                }
            }

            if ((flag & 17592186044416L) != 0L) {
                nCount = 1;
                mplew.writeInt(nCount);

                for(int  num = 0; num < nCount; ++num) {
                    mplew.writeInt(4475);
                    mplew.writeInt(-1);
                }
            }

            if ((flag & 2097152L) != 0L) {
                addJaguarInfo(mplew, chr);
            }

            if ((flag & 2048L) != 0L && JobConstants.is神之子(chr.getJob())) {
                chr.getStat().zeroData(mplew, chr, 65535, chr.isBeta());
            }

            long date;
            MapleShop shop;
            Map.Entry o2;
            NpcShopBuyLimit buyLimit;
            BuyLimitData data;
            int i;
            if ((flag & 67108864L) != 0L) {
                mplew.writeShort(chr.getBuyLimit().size() + chr.getAccountBuyLimit().size());
                var47 = chr.getBuyLimit().entrySet().iterator();

                label769:
                while(true) {
                    do {
                        if (!var47.hasNext()) {
                            break label769;
                        }

                        entry = (Map.Entry)var47.next();
                        i = (Integer)entry.getKey();
                        buyLimit = (NpcShopBuyLimit)entry.getValue();
                        shop = MapleShopFactory.getInstance().getShop(i);
                        mplew.writeInt(i);
                        mplew.writeShort(shop != null ? buyLimit.getData().size() : 0);
                    } while(shop == null);

                    var70 = buyLimit.getData().entrySet().iterator();

                    while(var70.hasNext()) {
                        o2 = (Map.Entry)var70.next();
                        int itemId = (Integer)o2.getKey();
                        data = (BuyLimitData)o2.getValue();
                        i = data.getCount();
                        date = data.getDate();
                        mplew.writeInt(i);
                        mplew.writeShort(shop.getBuyLimitItemIndex((Integer)o2.getKey()));
                        mplew.writeInt(itemId);
                        mplew.writeShort(i);
                        addExpirationTime(mplew, date);
                        mplew.writeMapleAsciiString("");
                        mplew.writeInt(0);
                    }
                }
            }

            if ((flag & 67108864L) != 0L) {
                mplew.writeShort(chr.getBuyLimit().size() + chr.getAccountBuyLimit().size());
                var47 = chr.getBuyLimit().entrySet().iterator();

                label752:
                while(true) {
                    do {
                        if (!var47.hasNext()) {
                            break label752;
                        }

                        entry = (Map.Entry)var47.next();
                        i = (Integer)entry.getKey();
                        buyLimit = (NpcShopBuyLimit)entry.getValue();
                        shop = MapleShopFactory.getInstance().getShop(i);
                        mplew.writeInt(i);
                        mplew.writeShort(shop != null ? buyLimit.getData().size() : 0);
                    } while(shop == null);

                    var70 = buyLimit.getData().entrySet().iterator();

                    while(var70.hasNext()) {
                        o2 = (Map.Entry)var70.next();
                        int itemId = (Integer)o2.getKey();
                        data = (BuyLimitData)o2.getValue();
                        i = data.getCount();
                        date = data.getDate();
                        mplew.writeInt(i);
                        mplew.writeShort(shop.getBuyLimitItemIndex((Integer)o2.getKey()));
                        mplew.writeInt(itemId);
                        mplew.writeShort(i);
                        addExpirationTime(mplew, date);
                        mplew.writeMapleAsciiString("");
                        mplew.writeInt(0);
                    }
                }
            }

            if ((flag & 67108864L) != 0L) {
                mplew.writeShort(0);
            }

            mplew.writeInt(0);
            if ((flag & 536870912L) != 0L) {
                for(j = 0; j < 16; ++j) {
                    mplew.writeInt(chr.getStealMemorySkill(j));
                }
            }

            if ((flag & 268435456L) != 0L) {
                int[] p_skills = new int[]{24001001, 24101001, 24111001, 24121001, 24121054};
                for (int pSkill : p_skills) {
                    mplew.writeInt(chr.getEquippedStealSkill(pSkill));
                }
            }

            if ((flag & 2147483648L) != 0L) {
                for(j = 0; j < 3; ++j) {
                    mplew.writeShort(chr.getInnerSkillSize());

                    for(int num = 0; num < chr.getInnerSkillSize(); ++num) {
                        InnerSkillEntry innerSkill = chr.getInnerSkills()[num];
                        if (innerSkill != null) {
                            mplew.write(innerSkill.getPosition());
                            mplew.writeInt(innerSkill.getSkillId());
                            mplew.write(innerSkill.getSkillLevel());
                            mplew.write(innerSkill.getRank());
                        } else {
                            mplew.writeZeroBytes(7);
                        }
                    }
                }
            }

            if ((flag & 18014398509481984L) != 0L) {
                mplew.writeShort(chr.getSoulCollection().size());
                var47 = chr.getSoulCollection().entrySet().iterator();

                while(var47.hasNext()) {
                    entry = (Map.Entry)var47.next();
                    mplew.writeInt((Integer)entry.getKey());
                    mplew.writeInt((Integer)entry.getValue());
                }
            }

            if ((flag & 4294967296L) != 0L) {
                mplew.writeInt(1);
                mplew.writeInt(chr.getHonor());
            }

            if ((flag & 140737488355328L) != 0L) {
                nCount = 0;
                mplew.writeShort(nCount);

                for(int num = 0; num < nCount; ++num) {
                    mplew.writeZeroBytes(20);
                }
            }

            if ((flag & 281474976710656L) != 0L) {
                addRedLeafInfo(mplew, chr);
            }

            if ((flag & 562949953421312L) != 0L) {
                mplew.writeShort(0);
            }

            if ((flag & 8589934592L) != 0L) {
                mplew.write(1);
                mplew.writeShort(0);
            }

            if ((flag & 17179869184L) != 0L) {
                mplew.write(0);
            }

            if ((flag & 34359738368L) != 0L) {
                writeDressUpInfo(mplew, chr);
            }

            if ((flag & 9007199254740992L) != 0L) {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeLong(getTime(-2L));
            }

            if ((flag & 2199023255552L) != 0L) {
                mplew.write(0);
            }

            if ((flag & 4611686018427387904L) != 0L) {
                mplew.writeInt(-1);
                mplew.writeInt(-1157267456);
                mplew.writeLong(0L);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeShort(0);
            }

            if ((flag & 4398046511104L) != 0L) {
                mplew.writeInt(chr.getLove());
                mplew.writeLong(getTime(-2L));
                mplew.writeInt(0);
            }

            if ((flag & 36028797018963968L) != 0L) {
                mplew.writeInt(chr.getId());
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeLong(getTime(-2L));
                mplew.writeInt(10);
            }

            if ((flag & 144115188075855872L) != 0L) {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeLong(0L);
                mplew.write(0);
                mplew.write(0);
            }

            // 共享任务数据
            Map<Integer, String>wsInfos = new LinkedHashMap();
            Iterator<Map.Entry<Integer, String>> worldShareIterator = chr.getWorldShareInfo().entrySet().iterator();

            while(worldShareIterator.hasNext()) {
                entry = (Map.Entry)worldShareIterator.next();
                if (GameConstants.isWorldShareQuest((Integer)entry.getKey())) {
                    wsInfos.put((Integer)entry.getKey(), (String)entry.getValue());
                }
            }

            mplew.writeShort(wsInfos.size());
            Set<Map.Entry<Integer, String>> wsinfoSet = wsInfos.entrySet();
            for (Map.Entry<Integer, String> wsEntry : wsinfoSet) {
                mplew.writeInt((Integer)wsEntry.getKey());
                mplew.writeMapleAsciiString(wsEntry.getValue() == null ? "" : (String)wsEntry.getValue());
            }

            if ((flag & 72057594037927936L) != 0L) {
                mplew.writeShort(chr.getMobCollection().size());
                Set<Map.Entry<Integer, String>> mobCollectionSet = chr.getMobCollection().entrySet();
                for (Map.Entry<Integer, String> mobColEntry : mobCollectionSet) {

                    mplew.writeInt((Integer)mobColEntry.getKey());
                    mplew.writeMapleAsciiString((String)mobColEntry.getValue());
                }
            }

            mplew.writeInt(0);
            if ((flag & 288230376151711744L) != 0L) {
                mplew.writeShort(0);
            }

            if ((flag & 288230376151711744L) != 0L) {
                mplew.writeShort(0);
            }

            if ((flag & 576460752303423488L) != 0L) {
                VCorePacket.writeVCoreSkillData(mplew, chr);
            }

            chr.loadHexSkills();
            encodeHexaSkills(mplew, chr);
            chr.loadHexStats();
            encodeSixStats(mplew, chr);

            if ((flag & 1152921504606846976L) != 0L) {
                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);

                    for(int buffId = 0; buffId < 3; ++buffId) {
                        mplew.writeInt(0);
                    }

                    mplew.writeLong(0L);
                }
            }

            if ((flag & 1152921504606846976L) != 0L) {
                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeInt(0);
                }
            }

            byte a2;
            if ((flag & 1152921504606846976L) != 0L) {
                mplew.writeInt(chr.getClient().getAccID());
                mplew.writeInt(chr.getId());
                mplew.writeInt(0);
                mplew.writeInt(-1);
                mplew.writeInt(Integer.MAX_VALUE);
                mplew.writeLong(getTime(-2L));
                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeInt(0);
                    mplew.write(0);
                    mplew.write(0);
                    mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
                    a2 = 41;
                    mplew.writeInt(a2);
                    if (a2 == 42) {
                        mplew.writeMapleAsciiString("");
                    } else {
                        mplew.writeLong(0L);
                    }
                }

                int v6 = 0;
                mplew.writeInt(v6);

                for(int buffId = 0; buffId < v6; ++buffId) {
                    mplew.writeInt(0);
                    mplew.write(0);
                    mplew.writeLong(0L);
                }
            }

            if ((flag & 32L) != 0L) {
                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeLong(0L);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeLong(0L);
                    mplew.writeLong(0L);
                    mplew.writeLong(0L);
                }
            }

            for(int num = 3; num > 0; --num) {
                if ((flag & (num <= 2 ? Long.MIN_VALUE : 268435456L)) != 0L) {
                    encodeCombingRoomInventory(mplew, (List)chr.getSalon().getOrDefault(num, new LinkedList()));
                }
            }

            if ((flag & 134217728L) != 0L) {
                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeInt(0);
                    mplew.writeZeroBytes(14);
                }

                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeShort(0);
                    mplew.writeInt(0);
                }

                mplew.writeShort(8);
                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeShort(0);
                    mplew.writeZeroBytes(25);
                }

                nCount = 0;
                mplew.writeInt(nCount);

                for(i = 0; i < nCount; ++i) {
                    mplew.writeMapleAsciiString("");
                    mplew.writeZeroBytes(25);
                }
            }

            if ((flag & 2305843009213693952L) != 0L) {
                mplew.writeInt(pets.size());
                for (MaplePet pet : pets) {
                    mplew.writeLong((long)pet.getUniqueId());
                    a2 = 0;
                    mplew.writeInt(a2);

                    for(j = 0; j < a2; ++j) {
                        mplew.writeInt(0);
                    }
                }
            }

            if ((flag & 1125899906842624L) != 0L) {
                mplew.write(1);
                String string = chr.getOneInfo(17008, "T");
                String string2 = chr.getOneInfo(17008, "L");
                String string3 = chr.getOneInfo(17008, "E");
                mplew.write(string == null ? 0 : Integer.valueOf(string));
                mplew.writeInt(string2 == null ? 1 : Integer.valueOf(string2));
                mplew.writeInt(string3 == null ? 0 : Integer.valueOf(string3));
                mplew.writeInt(100 - chr.getPQLog("航海能量"));
                mplew.writeLong(getTime(System.currentTimeMillis()));
                String questinfo = chr.getInfoQuest(17018);
                String[] questinfos = questinfo.split(";");
                mplew.writeShort(!questinfo.isEmpty() ? questinfos.length : 0);
                for (String questinfo1 : questinfos) {
                    if (!questinfo1.isEmpty()) {
                        String[] split = questinfo1.split("=");
                        mplew.write(Integer.valueOf(split[0]));
                        mplew.writeInt(Integer.valueOf(split[1]));
                        mplew.writeInt(0);
                    }
                }

                mplew.writeShort(ItemConstants.航海材料.length);
                int[] hanghaiMa = ItemConstants.航海材料;
                for (int cailiao : hanghaiMa) {
                    mplew.writeInt(cailiao);
                    mplew.writeInt(chr.getPQLog(String.valueOf(cailiao)));
                    mplew.writeLong(getTime(System.currentTimeMillis()));
                }
            }

            if ((flag & 2251799813685248L) != 0L) {
                List<Integer> buffs = new LinkedList();
                if (chr.getKeyValue("InnerGlareBuffs") != null) {
                    String[] var109 = chr.getKeyValue("InnerGlareBuffs").split(",");
                    for (String s : var109) {
                        if (!s.isEmpty()) {
                            buffs.add(Integer.parseInt(s));
                        }
                    }
                }

                mplew.writeReversedVarints(buffs.size());
                for (Integer buff : buffs) {
                    mplew.writeInt(buff);
                }
            }

            mplew.write(new byte[37]);
            return;
        }
    }

    public static void showCharacterInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, long flag) {
        if ((flag & 0x80L) != 0L) {
            mplew.writeInt(chr.getInventory(MapleInventoryType.EQUIPPED).getSlotLimit());
            MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
            List<Item> Inbudy = iv.newList();
            Collections.sort(Inbudy);
            for (short slotEquip = 0; slotEquip <= 128; slotEquip = (short)(slotEquip + 1)) {
                if (iv.getItem(slotEquip) == null) continue;
                mplew.writeShort(iv.getItem(slotEquip).getPosition());
                mplew.write(iv.getItem(slotEquip).getType());
                mplew.writeInt(iv.getItem(slotEquip).getItemId());
                mplew.write(0);
                mplew.writeLong(150842304000000000L);
                mplew.writeInt(iv.getItem(slotEquip).getExtendSlot());
                mplew.writeBool(false);
                mplew.writeShort(0);
            }
        }
    }

    public static void encodeHexaSkills(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getHexaSkills().size());
        for (MapleHexaSkill mhs : chr.getHexaSkills().values()) {
            mplew.writeInt(mhs.getId());
            mplew.writeInt(mhs.getSkilllv());
            mplew.write(1);
            mplew.write(0);
        }
        mplew.write(0);
        int HEXA2_Size = 0;
        mplew.writeInt(HEXA2_Size);
        for (int i = 0; i < HEXA2_Size; ++i) {
            mplew.writeInt(0);
        }
    }

    public static void encodeSixStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getHexaStats().size());
        for (Map.Entry<Integer, MapleHexaStat> msss : chr.getHexaStats().entrySet()) {
            mplew.writeInt(msss.getValue().getSolt());
            mplew.writeInt(msss.getValue().getSolt());
            mplew.writeInt(0);
            mplew.writeInt(1);
            mplew.writeInt(msss.getValue().getMain0());
            mplew.writeInt(msss.getValue().getMain0Lv());
            mplew.writeInt(msss.getValue().getAddit0S1());
            mplew.writeInt(msss.getValue().getAddit0S1Lv());
            mplew.writeInt(msss.getValue().getAddit0S2());
            mplew.writeInt(msss.getValue().getAddit0S2Lv());
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : msss.getValue().getSolt());
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : 1);
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : 1);
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : msss.getValue().getMain1());
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : msss.getValue().getMain1Lv());
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : msss.getValue().getAddit1S1());
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : msss.getValue().getAddit1S1Lv());
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : msss.getValue().getAddit1S2());
            mplew.writeInt(msss.getValue().getMain1() == -1 ? 0 : msss.getValue().getAddit1S2Lv());
        }
        mplew.writeInt(chr.getHexaStats().size());
        for (Map.Entry<Integer, MapleHexaStat> msss : chr.getHexaStats().entrySet()) {
            mplew.writeInt(msss.getValue().getSolt());
            mplew.writeInt(msss.getValue().getSolt());
            mplew.writeInt(msss.getValue().getPreset());
        }
        int HEXA5_Size = 0;
        mplew.writeInt(HEXA5_Size);
        for (int i = 0; i < HEXA5_Size; ++i) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        int HEXA6_Size = 0;
        mplew.writeInt(HEXA6_Size);
        for (int i = 0; i < HEXA6_Size; ++i) {
            mplew.writeInt(0);
        }
    }

    public static void encodeCombingRoomInventory(MaplePacketLittleEndianWriter mplew, List<Integer> styles) {
        mplew.write(styles.size());
        mplew.write(styles.size());
        for (int i = 1; i <= 102; ++i) {
            mplew.write(styles.size() >= i);
            if (styles.size() < i) continue;
            PacketHelper.encodeCombingRoomSlot(mplew, styles.get(i - 1));
        }
    }

    public static void encodeCombingRoomSlot(MaplePacketLittleEndianWriter mplew, int styleID) {
        mplew.write(0);
        mplew.writeInt(styleID);
        mplew.writeInt(styleID);
    }

    public static void addRedLeafInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        int[] idarr = new int[]{9410165, 9410166, 9410167, 9410168, 9410198};
        mplew.writeInt(chr.getClient().getAccID());
        mplew.writeInt(chr.getId());
        mplew.writeInt(idarr.length);
        mplew.writeInt(0);
        for (int i = 0; i < idarr.length; ++i) {
            mplew.writeInt(idarr[i]);
            mplew.writeInt(chr.getFriendShipPoints()[i]);
        }
    }

    public static void encodeInventory(MaplePacketLittleEndianWriter mplew, List<Item> list, MapleCharacter chr) {
        LinkedList<Item> items = new LinkedList<Item>(list);
        items.add(null);
        for (Item item : items) {
            PacketHelper.addItemPosition(mplew, item, false, false);
            if (item == null) break;
            PacketHelper.GW_ItemSlotBase_Encode(mplew, item);
        }
    }

    public static void addShopInfo(MaplePacketLittleEndianWriter mplew, MapleShop shop, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.writeInt(0);
        mplew.writeInt(2025021205);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        List<MapleShopItem> shopItems = shop.getItems(c);
        mplew.writeShort(shopItems.size());
        for (MapleShopItem item : shopItems) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(item.getItemId());
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeLong(94354848000000000L);
            mplew.writeInt(0);
            mplew.writeInt(item.getPrice());
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(item.getPointQuestID());
            mplew.writeInt(0);
            mplew.writeInt(item.getPointPrice());
            mplew.writeInt(item.getBuyLimitWorldAccount());
            mplew.writeInt(item.getMinLevel());
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
            mplew.writeLong(94354848000000000L);
            mplew.writeLong(150842304000000000L);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeInt(1);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("");
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeMapleAsciiString("");
            short slotMax = ii.getSlotMax(item.getItemId());
            if (ItemConstants.類型.可充值道具(item.getItemId())) {
                mplew.writeDouble(ii.getUnitPrice(item.getItemId()));
            } else {
                short quantity = item.getQuantity() == 0 ? slotMax : item.getQuantity();
                mplew.writeShort(quantity);
                slotMax = quantity > 1 ? (short)1 : (item.getBuyable() == 0 ? slotMax : item.getBuyable());
            }
            mplew.writeShort(slotMax);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("1900010100");
            mplew.writeMapleAsciiString("2079010100");
            for (int i = 0; i < 4; ++i) {
                mplew.writeInt(0);
            }
            mplew.writeInt(9410165);
            mplew.writeInt(0);
            mplew.writeInt(9410166);
            mplew.writeInt(0);
            mplew.writeInt(9410167);
            mplew.writeInt(0);
            mplew.writeInt(9410168);
            mplew.writeInt(0);
            mplew.writeInt(9410198);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
            Item rebuy = item.getRebuy();
            mplew.write(rebuy == null ? 0 : 1);
            if (rebuy == null) continue;
            PacketHelper.GW_ItemSlotBase_Encode(mplew, rebuy);
        }
    }

    public static void addJaguarInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if (JobConstants.is狂豹獵人(chr.getJob())) {
            mplew.write(chr.getIntNoRecord(111112));
            for (int i = 1; i <= 5; ++i) {
                mplew.writeInt(0);
            }
        }
    }

    public static void addSkillPets(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if (JobConstants.is陰陽師(chr.getJob()) && chr.getHaku() != null) {
            mplew.write(1);
            mplew.writeInt(chr.getHaku().getObjectId());
            mplew.writeInt(40020109);
            mplew.write(1);
            mplew.writePos(chr.getHaku().getPosition());
            mplew.write(0);
            mplew.writeShort(chr.getHaku().getStance());
        }
    }

    public static void writeDressUpInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter player) {
        boolean bl2 = JobConstants.is天使破壞者(player.getJob());
        if (bl2 && player.getKeyValue("Longcoat") == null) {
            player.setKeyValue("Longcoat", "1051291");
        }
        mplew.writeInt(bl2 ? player.getSecondFace() : 0);
        mplew.writeInt(bl2 ? player.getSecondHair() : 0);
        mplew.writeInt(bl2 ? Integer.valueOf(player.getKeyValue("Longcoat")) : 0);
        mplew.write(bl2 ? player.getSecondSkinColor() : (byte)0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(0);
    }

    public static void write劍刃之壁(MaplePacketLittleEndianWriter mplew, MapleCharacter player, int sourceid) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        int n4 = 0;
        switch (sourceid) {
            case 61101002: {
                n4 = 1;
                break;
            }
            case 61120007: {
                n4 = 2;
                break;
            }
            case 61110211: {
                n4 = 3;
                break;
            }
            case 61121217: {
                n4 = 4;
            }
        }
        int n5 = sourceid == 61101002 || sourceid == 61110211 ? 3 : (sourceid == 0 ? 0 : 5);
        for (int i2 = 0; i2 < n5; ++i2) {
            arrayList.add(0);
        }
        mplew.writeInt(n4);
        mplew.writeInt(n5);
        mplew.writeInt(player.getBuffedIntZ(SecondaryStat.StopForceAtomInfo));
        mplew.writeInt(arrayList.size());
        arrayList.forEach(mplew::writeInt);
    }

    public static void writeSonOfLinkedSkill(MaplePacketLittleEndianWriter mplew, int skillId, Pair<Integer, SkillEntry> skillinfo) {
        mplew.writeInt(skillinfo.getLeft());
        mplew.writeInt(skillinfo.getRight().teachId);
        mplew.writeInt(skillId);
        mplew.writeShort(skillinfo.getRight().skillevel);
        mplew.writeLong(PacketHelper.getTime(skillinfo.getRight().expiration));
        mplew.writeInt(skillinfo.getRight().teachTimes);
    }

    public static void writeEsInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        MapleInventory inventory = chr.getInventory(MapleInventoryType.ELAB);
        List<Item> list = inventory.newList();
        ArrayList<Item> list2 = new ArrayList<Item>();
        for (Item item : list) {
            if (item.getESPos() == 0) continue;
            list2.add(item);
        }
        mplew.writeShort(list2.size());
        for (Item item : list2) {
            mplew.writeShort(item.getESPos() - 1);
            mplew.writeInt(item.getItemId());
            mplew.writeInt(item.getQuantity());
        }
        mplew.writeShort(inventory.list().size());
        for (Item item : inventory.list()) {
            mplew.writeShort(item.getESPos() - 1);
            mplew.writeInt(item.getItemId());
            mplew.writeInt(item.getQuantity());
        }
    }

    public static void addDamageSkinInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        int skinId = chr.getDamageSkin();
        mplew.writeInt(chr.getDamSkinList().size());
        if (chr.isGm()) {
            chr.dropMessage(40, "[GM] 傷害字型總數欄位:" + chr.getDamSkinList().size() + ", 選擇的字型已經變更儲存。");
        }
        Iterator<Integer> iterator = chr.getDamSkinList().iterator();
        while (iterator.hasNext()) {
            int id = 0;
            PacketHelper.addDamageSkinInfo0(mplew, id, (id = iterator.next().intValue()) >= 0 ? MapleItemInformationProvider.getInstance().getDamageSkinItemId(id) : 0, 0, "");
        }
        PacketHelper.addDamageSkinInfo0(mplew, skinId, skinId >= 0 ? MapleItemInformationProvider.getInstance().getDamageSkinItemId(skinId) : 0, 0, "");
        PacketHelper.addDamageSkinInfo0(mplew, -1, 0, 1, "");
        PacketHelper.addDamageSkinInfo0(mplew, -1, 0, 1, "");
    }

    private static void addDamageSkinInfo0(MaplePacketLittleEndianWriter mplew, int skinId, int skinItemId, int b, String s) {
        mplew.writeInt(skinId);
        mplew.writeInt(skinItemId);
        mplew.writeLong(PacketHelper.getTime(-2L));
    }

    public static void addChaterName(MaplePacketLittleEndianWriter mplew, String speekerName, String text) {
        PacketHelper.addChaterName(mplew, speekerName, text, 0);
    }

    public static void addChaterName(MaplePacketLittleEndianWriter mplew, String speekerName, String text, int chrId) {
        mplew.writeMapleAsciiString(speekerName);
        mplew.writeMapleAsciiString(text);
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeShort(6);
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(3);
        mplew.write(0);
        mplew.write(6);
    }

    public static List<Pair<SecondaryStat, Pair<Integer, Integer>>> sortBuffStats(Map<SecondaryStat, Pair<Integer, Integer>> statups) {
        boolean changed;
        ArrayList<Pair<SecondaryStat, Pair<Integer, Integer>>> statvals = new ArrayList<Pair<SecondaryStat, Pair<Integer, Integer>>>();
        for (Map.Entry<SecondaryStat, Pair<Integer, Integer>> stat : statups.entrySet()) {
            statvals.add(new Pair<SecondaryStat, Pair<Integer, Integer>>(stat.getKey(), stat.getValue()));
        }
        do {
            changed = false;
            int i = 0;
            int k = 1;
            for (int iter = 0; iter < statvals.size() - 1; ++iter) {
                Pair<SecondaryStat, Pair<Integer, Integer>> a = statvals.get(i);
                Pair<SecondaryStat, Pair<Integer, Integer>> b = statvals.get(k);
                if (a != null && b != null && ((SecondaryStat)a.left).getFlag() > ((SecondaryStat)b.left).getFlag()) {
                    Pair<SecondaryStat, Pair<Integer, Integer>> swap = new Pair<>(a.left, a.right);
                    statvals.remove(i);
                    statvals.add(i, b);
                    statvals.remove(k);
                    statvals.add(k, swap);
                    changed = true;
                }
                ++i;
                ++k;
            }
        } while (changed);
        return statvals;
    }
}

