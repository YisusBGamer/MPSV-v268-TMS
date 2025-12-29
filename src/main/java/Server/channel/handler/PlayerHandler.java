/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MapleCharacterUtil
 *  Client.PortableChair
 *  Client.SpecialChairTW
 *  Client.skills.KSPsychicSkillEntry
 *  Config.constants.MapConstants
 *  Config.constants.enums.BossList
 *  Config.constants.enums.BossListType
 *  Config.constants.enums.ZeroMask
 *  Database.mapper.LobbyRankMapper
 *  Net.server.events.DimensionMirrorEvent
 *  Net.server.events.MapleLobbyRank
 *  Net.server.maps.ForceAtomObject
 *  Net.server.unknown.B2BodyAttackInfo
 *  Packet.AdelePacket
 *  Packet.AndroidPacket
 *  Packet.CField
 *  Packet.WillPacket
 *  Server.channel.handler.PlayerHandler$1
 */
package Server.channel.handler;

import Client.MapleCharacter;
import Client.MapleCharacterUtil;
import Client.MapleClient;
import Client.MapleQuestStatus;
import Client.MapleReward;
import Client.MapleStat;
import Client.MapleUnionEntry;
import Client.PortableChair;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.SpecialChairTW;
import Client.VCoreSkillEntry;
import Client.VMatrixOption;
import Client.VMatrixSlot;
import Client.anticheat.CheatingOffense;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleAndroid;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.inventory.MapleWeapon;
import Client.skills.ExtraSkill;
import Client.skills.InnerSkillEntry;
import Client.skills.KSPsychicSkillEntry;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.SkillMacro;
import Client.stat.DeadDebuff;
import Client.stat.PlayerStats;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.MapConstants;
import Config.constants.SkillConstants;
import Config.constants.enums.BossList;
import Config.constants.enums.BossListType;
import Config.constants.enums.ChairType;
import Config.constants.enums.ScriptType;
import Config.constants.enums.UserChatMessageType;
import Config.constants.enums.ZeroMask;
import Config.constants.skills.通用V核心;
import Database.mapper.LobbyRankMapper;
import Database.tools.SqlTool;
import Handler.warpToGameHandler;
import Net.server.*;
import Net.server.buffs.MapleStatEffect;
import Net.server.collection.SoulCollectionEntry;
import Net.server.events.DimensionMirrorEvent;
import Net.server.events.MapleLobbyRank;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.life.MobSkillFactory;
import Net.server.maps.FieldLimitType;
import Net.server.maps.ForceAtomObject;
import Net.server.maps.MapleAffectedArea;
import Net.server.maps.MapleFieldAttackObj;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleSummon;
import Net.server.maps.SavedLocationType;
import Net.server.movement.LifeMovementFragment;
import Net.server.quest.MapleQuest;
import Net.server.shop.MapleShop;
import Net.server.shop.MapleShopFactory;
import Net.server.unknown.B2BodyAttackInfo;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.InHeader;
import Opcode.header.OutHeader;
import Packet.AdelePacket;
import Packet.AndroidPacket;
import Packet.BuffPacket;
import Packet.CField;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.InventoryPacket;
import Packet.MTSCSPacket;
import Packet.MaplePacketCreator;
import Packet.PacketHelper;
import Packet.SummonPacket;
import Packet.VCorePacket;
import Packet.WillPacket;
import Server.BossEventHandler.spider.spider;
import Server.channel.ChannelServer;
import Server.channel.handler.AttackInfo;
import Server.channel.handler.DamageParse;
import Server.channel.handler.MovementParse;
import Server.channel.handler.PlayerHandler;
import Server.channel.handler.SummonHandler;
import SwordieX.client.character.avatar.AvatarLook;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;
import tools.Pair;
import tools.Randomizer;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

import static Config.constants.enums.BossListType.Waiting;

public class PlayerHandler {
    private static final Logger log = LoggerFactory.getLogger(PlayerHandler.class);

    public static void ChangeSkillMacro(MaplePacketReader slea, MapleCharacter chr) {
        int num = slea.readByte();
        for (int i = 0; i < num; ++i) {
            String name = slea.readMapleAsciiString();
            byte shout = slea.readByte();
            int skill1 = slea.readInt();
            int skill2 = slea.readInt();
            int skill3 = slea.readInt();
            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
    }

    public static void ChangeKeymap(MaplePacketReader slea, MapleCharacter chr) {
        block12: {
            block11: {
                if (chr == null) break block11;
                byte act = slea.readByte();
                switch (act) {
                    case 0: {
                        byte slot = slea.readByte();
                        slea.readInt();
                        int numChanges = slea.readByte();
                        for (int i = 0; i < numChanges; ++i) {
                            Skill skil;
                            byte key = slea.readByte();
                            byte type = slea.readByte();
                            int action = slea.readInt();
                            if (type == 1 && action >= 1000 && (skil = SkillFactory.getSkill(action)) != null && (!skil.isFourthJob() && !skil.isBeginnerSkill() && skil.getId() != 80011261 && skil.isInvisible() && chr.getSkillLevel(skil) <= 0 || SkillConstants.isLinkedAttackSkill(action))) continue;
                            chr.changeKeybinding(slot, key, type, action);
                        }
                        break block12;
                    }
                    case 1: {
                        int data = slea.readInt();
                        if (data <= 0) {
                            chr.getQuestRemove(MapleQuest.getInstance(122221));
                            break;
                        }
                        chr.getQuestNAdd(MapleQuest.getInstance(122221)).setCustomData(String.valueOf(data));
                        break;
                    }
                    case 2: {
                        int data = slea.readInt();
                        if (data <= 0) {
                            chr.getQuestRemove(MapleQuest.getInstance(122223));
                            break;
                        }
                        chr.getQuestNAdd(MapleQuest.getInstance(122223)).setCustomData(String.valueOf(data));
                        break;
                    }
                    case 3: {
                        byte data = slea.readByte();
                        if (data >= 0 && data < 3) {
                            chr.updateOneInfo(100972, "no", String.valueOf(data));
                            break;
                        }
                        break block12;
                    }
                    case 4: {
                        break;
                    }
                    default: {
                        log.warn("function:  出現未處理的act:" + act);
                    }
                }
                break block12;
            }
            log.error("function: ChangeKeymap 無法取得玩家資訊.");
        }
    }

    public static void UseChair(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        chr.updateTick(slea.readInt());
        int itemId = slea.readInt();
        short slot = (short)slea.readInt();
        if (itemId > 0 && slot <= -1) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserSetActivePortableChair(chr), chr.getPosition());
            c.sendEnableActions();
            return;
        }
        Item item = chr.getInventory(MapleInventoryType.SETUP).getItem(slot);
        if (item == null || item.getItemId() != itemId || itemId / 10000 != 301) {
            c.sendEnableActions();
            return;
        }
        Map<Integer, Map<Integer, SpecialChairTW>> var20 = chr.getMap().getSpecialChairTWs();
        SpecialChairTW var6 = null;
        if (var20 != null) {
            block6: for (Map<Integer, SpecialChairTW> it : var20.values()) {
                for (SpecialChairTW specialChairTW : it.values()) {
                    if (!specialChairTW.vs().contains(chr.getPosition())) continue;
                    var6 = specialChairTW;
                    break block6;
                }
            }
        }
        PortableChair chair = new PortableChair(itemId);
        if (var6 != null) {
            chr.setSpecialChairTW(var6);
            chr.setChair(chair);
            if (!var6.lj() && var6.getItemId() == itemId) {
                chr.getMap().specialChair$C(var6.getItemId(), var6.V(), chr.getId());
            } else {
                chr.getMap().specialChair$D(var6.getItemId(), var6.V(), chr.getId());
            }
        } else {
            boolean var22 = slea.readBool();
            int var26 = slea.readInt();
            int var29 = slea.readInt();
            String msg = "";
            ChairType type = ItemConstants.getChairType(itemId);
            switch (type) {
                case MESO: {
                    long var10 = slea.readLong();
                    break;
                }
                case TEXT: {
                    msg = slea.readMapleAsciiString();
                    break;
                }
                case LV: {
                    if (!slea.readBool()) break;
                    chair.setUn2(slea.readInt());
                    int arrSize = slea.readInt();
                    if (arrSize > 0) {
                        Triple[] var35 = new Triple[arrSize];
                        for (int i = 0; i < arrSize; ++i) {
                            Triple<Integer, String, Pair<AvatarLook, AvatarLook>> var37;
                            int var14 = slea.readInt();
                            int var15 = slea.readInt();
                            String var34 = slea.readMapleAsciiString();
                            AvatarLook avatarLook = null;
                            AvatarLook avatarLook2 = null;
                            if (slea.readBool()) {
                                avatarLook = new AvatarLook();
                                avatarLook.decode(slea);
                            }
                            if (slea.readBool()) {
                                avatarLook2 = new AvatarLook();
                                avatarLook2.decode(slea);
                            }
                            var35[i] = var37 = new Triple<Integer, String, Pair<AvatarLook, AvatarLook>>(var14, var34, new Pair<AvatarLook, AvatarLook>(avatarLook, avatarLook2));
                        }
                        chair.setArr(var35);
                    }
                    slea.readInt();
                    break;
                }
                case HASH_TAG:
                case TRAITS: {
                    c.sendEnableActions();
                    c.announce(MaplePacketCreator.PortableChairUseResult(c));
                    return;
                }
                default: {
                    chair.setMeso(slea.readInt());
                    chair.setUn4(slea.readInt());
                    chair.setUn5(slea.readByte());
                }
            }
            if (itemId / 10000 == 302) {
                slea.readLong();
            }
            chair.setMsg(msg);
            chr.setChair(chair);
        }
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserSetActivePortableChair(chr), chr.getPosition());
        c.sendEnableActions();
        c.announce(MaplePacketCreator.PortableChairUseResult(c));
    }

    public static void CancelChair(short id, MapleClient c, MapleCharacter chr) {
        if (id == -1 && chr != null) {
            chr.cancelFishingTask();
            chr.setChair(null);
            c.announce(MaplePacketCreator.UserSitResult(chr.getId(), -1));
            if (chr.getMap() != null) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserSetActivePortableChair(chr), false);
            }
        } else {
            if (chr != null) {
                chr.setChair(new PortableChair((int)id));
            }
            c.announce(MaplePacketCreator.UserSitResult(id, -1));
        }
        if (chr != null) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.CANCEL_CHAIR_TRIGGER_UI.getValue());
            mplew.writeInt(chr.getId());
            mplew.write(0);
            c.announce(mplew.getPacket());
        }
    }

    public static void TrockAddMap(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        byte type = slea.readByte();
        byte vip = slea.readByte();
        if (type == 0) {
            int mapId = slea.readInt();
            if (vip == 1) {
                chr.deleteFromRegRocks(mapId);
            } else if (vip == 2) {
                chr.deleteFromRocks(mapId);
            } else if (vip == 3) {
                chr.deleteFromHyperRocks(mapId);
            }
            c.announce(MTSCSPacket.getTrockRefresh(chr, vip, true));
        } else if (type == 1) {
            if (!FieldLimitType.TELEPORTITEMLIMIT.check(chr.getMap().getFieldLimit())) {
                if (vip == 1) {
                    chr.addRegRockMap();
                } else if (vip == 2) {
                    chr.addRockMap();
                } else if (vip == 3) {
                    chr.addHyperRockMap();
                }
                c.announce(MTSCSPacket.getTrockRefresh(chr, vip, false));
            } else {
                chr.dropMessage(1, "你可能沒有保存此地圖.");
            }
        }
    }

    public static void CharInfoRequest(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int objectId = slea.readInt();
        if (chr == null || chr.getMap() == null) {
            return;
        }
        MapleCharacter player = objectId == 0 ? chr.getMap().getPlayerObject(chr.getId()) : chr.getMap().getPlayerObject(objectId);
        c.sendEnableActions();
        if (player != null && (!player.isGm() || chr.isGm())) {
            c.announce(MaplePacketCreator.showCharacterInfo(slea, chr));
        }
    }

    public static void AranCombo(MapleClient c, MapleCharacter chr, int toAdd) {
        if (c.getPlayer() != null && JobConstants.is狂狼勇士(c.getPlayer().getJob())) {
            if (toAdd > 0) {
                int combo = c.getPlayer().getAranCombo() + toAdd;
                if (combo >= 1000) {
                    c.getPlayer().setAranCombo(1000, true);
                    MapleStatEffect effect = chr.getSkillEffect(21111030);
                    c.getPlayer().getSkillEffect(effect.getSourceId()).applyTo(c.getPlayer());
                } else {
                    c.getPlayer().setAranCombo(combo, true);
                }
            } else {
                c.getPlayer().gainAranCombo(toAdd, true);
            }
        }
    }

    public static void UseItemEffect(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int itemType;
        int itemId = slea.readInt();
        int n = itemType = slea.available() >= 4L ? slea.readInt() : 0;
        if (itemId == 0) {
            chr.setItemEffect(0);
            chr.setItemEffectType(0);
        } else {
            Item toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
            if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
                c.sendEnableActions();
                return;
            }
            if (itemId != 5510000) {
                chr.setItemEffect(itemId);
                chr.setItemEffectType(itemType);
            }
        }
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.itemEffect(chr.getId(), itemId, itemType), false);
    }

    public static void UseTitleEffect(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.hasBlockedInventory()) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int itemId = slea.readInt();
        int slot = slea.readInt();
        if (itemId == 0) {
            chr.updateOneQuestInfo(19019, "id", "0");
            chr.updateOneQuestInfo(19019, "date", "0");
            chr.updateOneQuestInfo(19019, "expired", "1");
            chr.getQuestRemove(MapleQuest.getInstance(124000));
        } else {
            Item toUse = chr.getInventory(MapleInventoryType.SETUP).getItem((short)slot);
            String questInfo = chr.getQuestInfo(19019, "id");
            if (toUse != null && toUse.getItemId() == itemId && !String.valueOf(itemId).equals(questInfo) && itemId / 10000 == 370) {
                chr.updateOneQuestInfo(19019, "id", String.valueOf(itemId));
                if (toUse.getExpiration() >= 0L) {
                    chr.updateOneQuestInfo(19019, "expired", "1");
                    chr.updateOneQuestInfo(19019, "date", DateUtil.getFormatDate(new Date(toUse.getExpiration())).replace("-", "/") + " 00:00:00:000");
                } else {
                    chr.updateOneQuestInfo(19019, "expired", "0");
                    chr.updateOneQuestInfo(19019, "date", "2079/01/01 00:00:00:000");
                }
            } else {
                chr.updateOneQuestInfo(19019, "id", "0");
                chr.updateOneQuestInfo(19019, "date", "0");
                chr.updateOneQuestInfo(19019, "expired", "1");
                c.sendEnableActions();
                return;
            }
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.NickSkillExpired);
        mplew.writeInt(0);
        c.announce(mplew.getPacket());
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.showTitleEffect(chr.getId(), itemId), false);
    }

    public static void CancelItemEffect(int id, MapleCharacter chr) {
        if (-id == 2023519) {
            return;
        }
        chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), false, -1L);
    }

    public static void SpecialSkillUse(MaplePacketReader slea, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int skillid = slea.readInt();
        byte mode = slea.readByte();
        MapleStatEffect effect = chr.getSkillEffect(skillid);
        if (effect == null) {
            return;
        }
        Skill skill = SkillFactory.getSkill(skillid);
        if (skill != null && skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            if (effect.getCooldown() > 0) {
                if (SkillConstants.isKeydownSkillCancelGiveCD(skillid) && !chr.isSkillCooling(skillid)) {
                    chr.registerSkillCooldown(chr.getSkillEffect(skillid), true);
                }
                if (!chr.isSkillCooling(skillid)) {
                    chr.send(MaplePacketCreator.skillCooltimeSet(skillid, 0));
                }
            }
        }
        switch (skillid) {
            case 400051024: {
                chr.dispelEffect(400051024);
                break;
            }
            case 64121002: 
            case 155121341: 
            case 400041084: 
            case 400051334: {
                break;
            }
            default: {
                if (SkillConstants.getKeydownSkillCancelReduceTime(skillid, 30000) > 0) break;
                if (chr.isDebug()) {
                    chr.dropDebugMessage(1, "[Special Skill Use] SkillID:" + skillid + ", SkillName:" + SkillFactory.getSkillName(skillid) + ", mode:" + mode);
                }
                effect.applyTo(chr);
            }
        }
        if (skillid == 63121040 && (effect = chr.getSkillEffect(skillid)) != null) {
            int maxValue = effect.getW();
            int timeout = effect.getU() * 1000;
            Pair skillInfo = (Pair)chr.getTempValues().get("MultiSkill" + skillid);
            if (skillInfo != null) {
                Pair pair = skillInfo;
                pair.left = (Integer)pair.left - 1;
                if ((Integer)skillInfo.left < 0) {
                    skillInfo.left = 0;
                }
                skillInfo.right = System.currentTimeMillis();
                chr.getTempValues().put("MultiSkill" + skillid, skillInfo);
                chr.send(MaplePacketCreator.multiSkillInfo(skillid, (Integer)skillInfo.left, maxValue, timeout));
            }
        }
    }

    public static void CancelBuffHandler(int sourceid, MapleCharacter chr) {
        MapleStatEffect eff;
        int reduceTime;
        SecondaryStatValueHolder mbsvh;
        if (chr == null || chr.getMap() == null) {
            return;
        }
        Skill skill = SkillFactory.getSkill(sourceid);
        if (skill == null) {
            return;
        }
        int totalSkillLevel = chr.getTotalSkillLevel(skill);
        MapleStatEffect effect = skill.getEffect(totalSkillLevel > 0 ? totalSkillLevel : 1);
        if (effect == null) {
            return;
        }
        if (chr.isDebug()) {
            chr.dropDebugMessage(1, "[BUFF信息] 客戶端取消技能BUFF 技能ID:" + sourceid + " 技能名字:" + SkillFactory.getSkillName(sourceid));
        }
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, sourceid), false);
            MapleStatEffect eff2 = chr.getSkillEffect(sourceid);
            if (eff2 != null && eff2.getCooldown() > 0) {
                if (SkillConstants.isKeydownSkillCancelGiveCD(sourceid) && !chr.isSkillCooling(sourceid)) {
                    chr.registerSkillCooldown(chr.getSkillEffect(sourceid), true);
                }
                if (!chr.isSkillCooling(sourceid)) {
                    chr.send(MaplePacketCreator.skillCooltimeSet(sourceid, 0));
                }
            }
        } else {
            chr.getClient().announce(MaplePacketCreator.skillCancel(chr, sourceid));
        }
        if (sourceid == 400021086) {
            return;
        }
        if (sourceid == 20031205) {
            chr.registerSkillCooldown(effect, true);
        }
        if ((mbsvh = chr.getBuffStatValueHolder(sourceid)) != null) {
            switch (sourceid) {
                case 1221054: {
                    chr.reduceSkillCooldown(sourceid, mbsvh.getLeftTime() / effect.getY() * effect.getX());
                    break;
                }
                case 400011047: {
                    if (mbsvh.getLeftTime() <= 0) break;
                    ExtraSkill eskill = new ExtraSkill(400011085, chr.getPosition());
                    SecondaryStatValueHolder m = chr.getBuffStatValueHolder(SecondaryStat.DarknessAura);
                    eskill.Value = m == null ? 1 : Math.max(1, m.z / 3);
                    chr.send(MaplePacketCreator.RegisterExtraSkill(sourceid, Collections.singletonList(eskill)));
                }
            }
        }
        if (sourceid == 40011288) {
            chr.getClient().outPacket(OutHeader.LP_TemporaryStatReset.getValue(), "01 01 17 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
            chr.getClient().outPacket(OutHeader.LP_TemporaryStatSet.getValue(), "00 00 00 0A 00 00 00 05 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 64 00 1B 86 62 02 A9 7A 61 FC 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 1B 86 62 02 05 00 00 00 57 85 9E 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 1B 86 62 02 05 00 00 00 57 85 9E 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 1B 86 62 02 06 00 00 00 57 85 9E 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 1B 86 62 02 01 00 00 00 57 85 9E 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 01 00 00 00 00 00 18");
        }
        if ((reduceTime = SkillConstants.getKeydownSkillCancelReduceTime(mbsvh)) > 0) {
            chr.reduceSkillCooldown(sourceid, reduceTime);
        }
        if (effect.getStatups() != null) {
            chr.cancelEffect(skill.getEffect(1), false, -1L);
        }
        for (SecondaryStat stat : effect.getStatups().keySet()) {
            if (stat.canStack()) continue;
            chr.dispelEffect(stat);
        }
        if (sourceid == 27101202) {
            chr.send(BuffPacket.temporaryStatReset(Collections.singletonList(SecondaryStat.KeyDownAreaMoving), chr));
            chr.send_other(BuffPacket.cancelForeignBuff(chr, Collections.singletonList(SecondaryStat.KeyDownAreaMoving)), false);
        }
        if (sourceid == 400041009) {
            int[] arrn = new int[]{400041011, 400041012, 400041013, 400041014, 400041015};
            chr.getSkillEffect(arrn[Randomizer.nextInt(arrn.length)]).applyTo(chr, chr.getPosition());
        }
        if (sourceid == 400041029 && chr.getBuffedIntValue(SecondaryStat.SurplusSupply) > 20) {
            chr.getSkillEffect(30020232).unprimaryPassiveApplyTo(chr);
        }
        if (sourceid == 95001000) {
            chr.send(PlayerHandler.removeSpawnEffect(1, 95001000));
            chr.send(PlayerHandler.removeSpawnEffectCrc(0, 0));
        }
        if (sourceid == 162121022 && (eff = chr.getSkillEffect(162120038)) != null && (mbsvh = chr.getBuffStatValueHolder(SecondaryStat.IndieBarrier, eff.getSourceId())) != null) {
            int shield = mbsvh.value;
            eff.applyTo(chr, eff.getDuration());
            mbsvh = chr.getBuffStatValueHolder(SecondaryStat.IndieBarrier, eff.getSourceId());
            if (mbsvh != null) {
                mbsvh.value = shield;
                chr.send(BuffPacket.giveBuff(chr, mbsvh.effect, Collections.singletonMap(SecondaryStat.IndieBarrier, mbsvh.effect.getSourceId())));
            }
        }
    }

    public static byte[] removeSpawnEffect(int x, int skillid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FIELDATTACKOBJ_REMOVE_BYLIST.getValue());
        mplew.writeInt(x);
        mplew.writeInt(skillid);
        return mplew.getPacket();
    }

    public static byte[] removeSpawnEffectCrc(int x, int y) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FIELDATTACKOBJ_REMOVE_ALL.getValue());
        mplew.writeInt(x);
        mplew.writeInt(y);
        return mplew.getPacket();
    }

    public static void CancelMech(MaplePacketReader slea, MapleCharacter chr) {
        Skill skill;
        if (chr == null) {
            return;
        }
        int sourceid = slea.readInt();
        if (sourceid % 10000 < 1000 && SkillFactory.getSkill(sourceid) == null) {
            sourceid += 1000;
        }
        if ((skill = SkillFactory.getSkill(sourceid)) == null) {
            return;
        }
        if (sourceid == 32111016) {
            return;
        }
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, sourceid), false);
        } else {
            MapleStatEffect effect = skill.getEffect(chr.getSkillLevel(skill));
            if (effect == null) {
                return;
            }
            chr.cancelEffect(effect, false, -1L);
        }
    }

    public static void QuickSlot(MaplePacketReader slea, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        chr.getQuickSlot().resetQuickSlot();
        for (int i = 0; i < 32; ++i) {
            chr.getQuickSlot().addQuickSlot(i, slea.readInt());
        }
    }

    public static void UserSkillPrepareRequest(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        Skill skill;
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int skillId = slea.readInt();
        byte level = slea.readByte();
        byte display = slea.readByte();
        if (skillId == 400021072) {
            slea.readInt();
        }
        byte direction = slea.readByte();
        byte speed = slea.readByte();
        slea.readByte();
        Point position = null;
        if (slea.available() >= 4L) {
            position = slea.readPos();
        }
        if ((skill = SkillFactory.getSkill(skillId)) == null) {
            return;
        }
        MapleStatEffect effect = skill.getEffect(chr.getSkillLevel(SkillConstants.getLinkedAttackSkill(skillId)));
        if (effect == null) {
            return;
        }
        if (chr.isDebug()) {
            chr.dropMessage(5, "[Skill Use] Prepare Effect:" + String.valueOf(effect));
        }
        if (skillId == 400041053) {
            chr.dispelEffect(effect.getSourceId());
            return;
        }
        if (skillId == 400051089 || skillId == 175111004) {
            int godPower = 0;
            int time = 30000;
            if (chr.getSkillLevel(175110010) > 0) {
                godPower = (Integer)chr.getTempValues().getOrDefault("GodPower", 0);
                ++godPower;
                godPower = Math.min(5, godPower);
                chr.getTempValues().put("GodPower", godPower);
                if (chr.getSkillLevel(175120039) > 0) {
                    time += 10000;
                }
            }
        }
        if (skill.isChargeSkill()) {
            switch (skillId) {
                case 164121042: 
                case 400041009: 
                case 400041039: 
                case 400051041: 
                case 400051334: {
                    effect.applyTo(chr);
                    break;
                }
                case 155111306: 
                case 155121341: {
                    MapleStatEffect skillEffect = chr.getSkillEffect(155000007);
                    if (skillEffect != null) {
                        skillEffect.applyTo(chr);
                    }
                    effect.applyTo(chr, true);
                    break;
                }
                case 400021086: 
                case 400031025: {
                    break;
                }
                default: {
                    effect.applyTo(chr, true);
                    break;
                }
            }
        } else if (skillId == 64001007 || skillId == 64001008) {
            chr.registerSkillCooldown(chr.getSkillEffect(64001000), true);
        } else if (effect.getCooldown(chr) > 0 && skillId != 14121003 && skillId != 400031025) {
            if (skillId == 400051334 || 175111004 == skillId) {
                effect.applyTo(chr);
            } else if (skillId == 400031064) {
                effect.applyTo(chr, true);
            }
            chr.registerSkillCooldown(effect, true);
        }
        if (skillId == 400051040 && chr.getSkillEffect(5221013) != null && chr.getCooldownLeftTime(5221013) < 8000) {
            chr.registerSkillCooldown(5221013, 8000, true);
        }
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserSkillPrepare(chr.getId(), skillId, level, display, direction, speed, position), false);
        chr.setKeyDownSkill_Time(System.currentTimeMillis());
    }

    public static void UserThrowGrenade(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            c.sendEnableActions();
            return;
        }
        int pos_x = slea.readInt();
        int pos_y = slea.readInt();
        slea.skip(4);
        slea.skip(4);
        int display = slea.readInt();
        int skillId = slea.readInt();
        int i3 = slea.readInt();
        boolean isLeft = slea.readByte() > 0;
        int speed = slea.readInt();
        int tickCount = slea.readInt();
        Skill skill = SkillFactory.getSkill(SkillConstants.getLinkedAttackSkill(skillId));
        int skilllevel = chr.getTotalSkillLevel(skill);
        if (chr.isDebug()) {
            chr.dropDebugMessage(1, "[Throw Grenade] 技能: " + SkillFactory.getSkillName(skillId) + "(" + skillId + ") 技能等級: " + skilllevel);
        }
        if (skill == null || skilllevel <= 0) {
            c.sendEnableActions();
            return;
        }
        MapleStatEffect effect = skill.getEffect(chr.getTotalSkillLevel(skill));
        effect.applyTo(chr);
        chr.getMap().broadcastMessage(chr, EffectPacket.onUserEffectRemote(chr, skillId, EffectOpcode.UserEffect_SkillUse, chr.getLevel(), skilllevel), false);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.showSpecialAttack(chr.getId(), tickCount, pos_x, pos_y, display, skillId, skilllevel, isLeft, speed), chr.getPosition());
    }

    public static void UserDestroyGrenade(MaplePacketReader slea, MapleCharacter chr) {
        MapleStatEffect effect;
        if (chr == null) {
            return;
        }
        slea.readInt();
        slea.readByte();
        int skillId = slea.readInt();
        if (skillId == 400031036 && (effect = chr.getSkillEffect(3311009)) != null) {
            effect.applyTo(chr);
        }
    }

    public static void attackProcessing(MaplePacketReader slea, MapleClient c, InHeader header) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null || chr.getMap() == null) {
            return;
        }
        if (chr.hasBlockedInventory()) {
            chr.dropMessage(5, "觸發定身需要解卡請輸入@EA");
            c.sendEnableActions();
            return;
        }
        switch (header) {
            case CP_UserMeleeAttack: {
                PlayerHandler.UserMeleeAttack(slea, c, chr);
                break;
            }
            case CP_UserShootAttack: {
                PlayerHandler.UserShootAttack(slea, c, chr);
                break;
            }
            case CP_UserMagicAttack: {
                PlayerHandler.UserMagicAttack(slea, c, chr);
                break;
            }
            case CP_SummonedAttack: {
                SummonHandler.UserSummonAttack(slea, c, chr);
                break;
            }
            case CP_UserBodyAttack: {
                PlayerHandler.UserBodyAttack(slea, c, chr);
                break;
            }
            case CP_UserAreaDotAttack: {
                PlayerHandler.UserAreaDotAttack(slea, c, chr);
                break;
            }
            case UserSpotlightAttack: {
                PlayerHandler.UserSpotlightAttack(slea, c, chr);
                break;
            }
            case UserNonTargetForceAtomAttack: {
                PlayerHandler.UserNonTargetForceAtomAttack(slea, c, chr);
            }
        }
    }

    public static void UserBodyAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseAttack(InHeader.CP_UserBodyAttack, slea, chr);
        if (attack == null) {
            c.sendEnableActions();
            return;
        }
        int skillLevel = 0;
        int attackCount = 1;
        attack.attackType = AttackInfo.AttackType.BodyAttack;
        boolean hasMoonBuff = chr.getBuffedIntValue(SecondaryStat.PoseType) == 1;
        boolean hasShadow = chr.getBuffedValue(SecondaryStat.ShadowPartner) != null;
        MapleStatEffect effect = null;
        Skill skill = null;
        if (attack.skillId != 0) {
            int linkSkillId = SkillConstants.getLinkedAttackSkill(attack.skillId);
            skill = SkillFactory.getSkill(attack.skillId);
            if (skill == null) {
                c.sendEnableActions();
                return;
            }
            skillLevel = chr.getSkillLevel(linkSkillId);
            effect = skill.getEffect(skillLevel);
            if (skillLevel <= 0 || effect == null) {
                chr.dropMessage(3, "[Body Attack] " + String.valueOf(skill) + " Skill Lv: " + skillLevel + " Effect is Null!");
                c.sendEnableActions();
                return;
            }
            attackCount = effect.getAttackCount(chr);
        }
        DamageParse.calcDamage(attack, chr, attackCount * (hasShadow ? 2 : 1), effect);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserBodyAttack(chr, skillLevel, 0, attack, hasMoonBuff), chr.getPosition());
        DamageParse.applyAttack(attack, skill, chr, effect, true);
    }

    public static void UserAreaDotAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseAttack(InHeader.CP_UserAreaDotAttack, slea, chr);
        if (attack == null) {
            c.sendEnableActions();
            return;
        }
        int skillLevel = 0;
        int attackCount = 1;
        attack.attackType = AttackInfo.AttackType.AreaDotAttack;
        boolean hasMoonBuff = chr.getBuffedIntValue(SecondaryStat.PoseType) == 1;
        MapleStatEffect effect = null;
        Skill skill = null;
        if (attack.skillId != 0) {
            int linkSkillId = SkillConstants.getLinkedAttackSkill(attack.skillId);
            skill = SkillFactory.getSkill(attack.skillId);
            if (skill == null) {
                c.sendEnableActions();
                return;
            }
            skillLevel = chr.getSkillLevel(linkSkillId);
            effect = skill.getEffect(skillLevel);
            if (skillLevel <= 0 || effect == null) {
                chr.dropMessage(3, "[AreaDot Attack] " + String.valueOf(skill) + " Skill Lv: " + skillLevel + " Effect is Null!");
                c.sendEnableActions();
                return;
            }
            attackCount = effect.getAttackCount(chr);
        }
        DamageParse.calcDamage(attack, chr, attackCount, effect);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserBodyAttack(chr, skillLevel, 0, attack, hasMoonBuff), chr.getPosition());
        DamageParse.applyAttack(attack, skill, chr, effect, true);
    }

    public static void UserSpotlightAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseAttack(InHeader.UserSpotlightAttack, slea, chr);
        if (attack == null) {
            c.sendEnableActions();
            return;
        }
        attack.attackType = AttackInfo.AttackType.MeleeAttack;
        boolean hasMoonBuff = chr.getBuffedIntValue(SecondaryStat.PoseType) == 1;
        boolean hasShadow = chr.getBuffedValue(SecondaryStat.ShadowPartner) != null;
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
        int attackCount = shield != null && shield.getItemId() / 10000 == 134 && shield.getItemId() != 1342069 ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;
        int linkSkillId = 0;
        if (attack.skillId != 0) {
            linkSkillId = SkillConstants.getLinkedAttackSkill(attack.skillId);
            skill = SkillFactory.getSkill(attack.skillId);
            if (skill == null) {
                c.sendEnableActions();
                return;
            }
            skillLevel = chr.getSkillLevel(linkSkillId);
            effect = skill.getEffect(skillLevel);
            if (skillLevel <= 0 || effect == null) {
                chr.dropMessage(3, "[Spotlight Attack] " + String.valueOf(skill) + " Skill Lv: " + skillLevel + " Effect is Null!");
                c.sendEnableActions();
                return;
            }
            attackCount = effect.getAttackCount(chr);
        }
        DamageParse.calcDamage(attack, chr, attackCount * (hasShadow ? 2 : 1), effect);
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserMeleeAttack(chr, skillLevel, 0, attack, hasMoonBuff), chr.getPosition(), false);
        DamageParse.applyAttack(attack, skill, chr, effect, SkillConstants.isPassiveAttackSkill(linkSkillId));
    }

    public static void UserMeleeAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseAttack(InHeader.CP_UserMeleeAttack, slea, chr);
        if (attack == null) {
            c.sendEnableActions();
            return;
        }
        if (attack == null && c.getPlayer().getJob() / 100 == 175) {
            c.outPacket(OutHeader.EXTRA_SYSTEM_RESULT.getValue(), "B3 24 7D 3F 27 00 00 78 00 00 00 B8 0B 00 00 01 00 00 00 01 2B 07 02 00 00 0D");
            return;
        }
        int comboStage = -1;
        if (JobConstants.is墨玄(chr.getJob())) {
            c.getPlayer().send(PlayerHandler.XuanAttack(attack.skillId));
        }
        if (c.getPlayer().isGm() && attack.skillId > 0) {
            c.getPlayer().dropMessage(40, "[Melee] " + SkillFactory.getSkillName(attack.skillId) + " / skillID :" + attack.skillId + " / skillLV :" + attack.skllv);
        }
        attack.attackType = AttackInfo.AttackType.MeleeAttack;
        boolean hasMoonBuff = chr.getBuffedIntValue(SecondaryStat.PoseType) == 1;
        boolean hasShadow = chr.getBuffedValue(SecondaryStat.ShadowPartner) != null;
        Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
        int attackCount = shield != null && shield.getItemId() / 10000 == 134 && shield.getItemId() != 1342069 ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;
        int linkSkillId = 0;
        if (attack.skillId != 0) {
            SecondaryStatValueHolder holder;
            linkSkillId = SkillConstants.getLinkedAttackSkill(attack.skillId);
            skill = SkillFactory.getSkill(attack.skillId);
            if (skill == null) {
                c.sendEnableActions();
                return;
            }
            skillLevel = chr.getSkillLevel(linkSkillId);
            effect = skill.getEffect(skillLevel);
            if (effect == null && (holder = chr.getBuffStatValueHolder(linkSkillId)) != null && holder.effect != null) {
                effect = holder.effect;
                skillLevel = holder.effect.getLevel();
            }
            if (effect == null || skillLevel < 0) {
                chr.dropDebugMessage(2, "[Melee Attack] Effect is null 玩家[" + chr.getName() + " 職業： " + chr.getJob() + "] 技能： " + skill.getId() + " 等級： " + skillLevel);
                c.sendEnableActions();
                return;
            }
        }
        DamageParse.calcDamage(attack, chr, attackCount * (hasShadow ? 2 : 1), effect);
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserMeleeAttack(chr, skillLevel, 0, attack, hasMoonBuff), chr.getPosition(), false);
        DamageParse.applyAttack(attack, skill, chr, effect, SkillConstants.isPassiveAttackSkill(linkSkillId));
    }

    public static byte[] XuanAttack(int skillid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.EXTRA_SYSTEM_RESULT.getValue());
        mplew.writeInt(1065166003);
        mplew.writeShort(39);
        mplew.write(0);
        switch (skillid) {
            case 175001002: {
                mplew.writeInt(110);
                mplew.writeInt(3000);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(519);
                mplew.write(18);
                break;
            }
            case 175121006: {
                mplew.writeInt(210);
                mplew.writeInt(3000);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(519);
                mplew.write(18);
                break;
            }
            case 175101001: {
                mplew.writeInt(211);
                mplew.writeInt(3000);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(519);
                mplew.write(18);
                break;
            }
            case 175101004: {
                mplew.writeInt(0);
                mplew.writeInt(-297381);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(519);
                mplew.write(18);
                break;
            }
            case 175001003: {
                mplew.writeInt(0);
                mplew.writeInt(-157884);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(519);
                mplew.write(18);
                break;
            }
            case 175111001: {
                mplew.writeInt(0);
                mplew.writeInt(-243602);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(263);
                mplew.write(18);
                break;
            }
            case 175111002: {
                mplew.writeInt(0);
                mplew.writeInt(-427367);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(263);
                mplew.write(18);
                break;
            }
            case 175121001: {
                mplew.writeInt(120);
                mplew.writeInt(3000);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(519);
                mplew.write(18);
                break;
            }
            case 175121007: {
                mplew.writeInt(220);
                mplew.writeInt(3000);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(519);
                mplew.write(18);
                break;
            }
            case 175121003: 
            case 175121004: {
                mplew.writeInt(0);
                mplew.writeInt(-856473);
                mplew.writeInt(1);
                mplew.write(1);
                mplew.write(43);
                mplew.writeInt(775);
                mplew.write(127);
            }
        }
        return mplew.getPacket();
    }

    public static void UserShootAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseAttack(InHeader.CP_UserShootAttack, slea, chr);
        if (attack == null) {
            c.sendEnableActions();
            return;
        }
        if (SkillFactory.isBlockedSkill(attack.skillId)) {
            chr.dropMessage(5, "由於<" + SkillFactory.getSkillName(attack.skillId) + ">技能數據異常,暫未開放使用.");
            c.sendEnableActions();
            return;
        }
        int bulletCount = 1;
        int skillLevel = 0;
        int linkSkillId = 0;
        int attackCount = 1;
        MapleStatEffect effect = null;
        Skill skill = null;
        boolean hasShadow = chr.getBuffedValue(SecondaryStat.ShadowPartner) != null;
        attack.attackType = AttackInfo.AttackType.ShootAttack;
        int itemId = 0;
        if (attack.skillId != 0) {
            linkSkillId = SkillConstants.getLinkedAttackSkill(attack.skillId);
            skill = SkillFactory.getSkill(attack.skillId);
            if (skill == null) {
                c.sendEnableActions();
                return;
            }
            skillLevel = chr.getTotalSkillLevel(linkSkillId);
            effect = skill.getEffect(skillLevel);
            if (c.getPlayer().isGm() && attack.skillId > 0) {
                c.getPlayer().dropMessage(40, "[Shot] " + SkillFactory.getSkillName(attack.skillId) + " / skillID :" + attack.skillId + " / skillLV :" + attack.skllv);
            }
            if (skillLevel <= 0 || effect == null) {
                log.error("遠距離攻擊效果為空 玩家[" + chr.getName() + " 職業: " + chr.getJob() + "] 使用技能: " + skill.getId() + " - " + skill.getName() + " 技能等級: " + skillLevel);
                c.sendEnableActions();
                return;
            }
            int n4 = effect.getAttackCount(chr);
            if (effect.getBulletCount() > 1) {
                bulletCount = effect.getBulletCount(chr) * (hasShadow ? 2 : 1);
                MapleStatEffect effecForBuffStat = chr.getEffectForBuffStat(SecondaryStat.RideVehicle);
                if (effecForBuffStat != null && effecForBuffStat.getSourceId() == 33001001) {
                    n4 *= bulletCount;
                }
                n4 = Math.max(n4, bulletCount);
            }
            attackCount = n4 * (hasShadow ? 2 : 1);
        } else if (chr.getBuffSource(SecondaryStat.RideVehicle) == 35001002) {
            attackCount = 2;
        }
        if (attack.starSlot > 0) {
            Item item = chr.getInventory(MapleInventoryType.USE).getItem(attack.starSlot);
            if (item == null) {
                return;
            }
            itemId = item.getItemId();
        }
        if (attack.cashSlot > 0) {
            Item item2 = chr.getInventory(MapleInventoryType.CASH).getItem(attack.cashSlot);
            if (item2 == null) {
                return;
            }
            itemId = item2.getItemId();
        }
        DamageParse.calcDamage(attack, chr, attackCount, effect);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserShootAttack(chr, skillLevel, itemId, attack), chr.getPosition());
        DamageParse.applyAttack(attack, skill, chr, effect, SkillConstants.isPassiveAttackSkill(linkSkillId));
    }

    public static void handleFinalAttack(MapleCharacter chr, int skillId) {
        int finalSkillId = 0;
        int finalSkillLevel = 0;
        for (int id : SkillFactory.getFinalAttackSkills()) {
            int level = chr.getTotalSkillLevel(id);
            if (level <= 0) continue;
            finalSkillId = id;
            finalSkillLevel = level;
            break;
        }
        if (finalSkillId == 0 || finalSkillId == skillId) {
            return;
        }
        Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(JobConstants.is神之子(chr.getJob()) && chr.isBeta() ? -10 : -11));
        MapleWeapon weaponType = item == null ? MapleWeapon.沒有武器 : MapleWeapon.getByItemID(item.getItemId());
        if (weaponType == MapleWeapon.沒有武器) {
            return;
        }
        Skill finalSkill = SkillFactory.getSkill(finalSkillId);
        MapleStatEffect effect = finalSkill.getEffect(finalSkillLevel);
        boolean use = effect.makeChanceResult();
    }

    public static void UserMagicAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseAttack(InHeader.CP_UserMagicAttack, slea, chr);
        if (attack == null) {
            c.sendEnableActions();
            return;
        }
        if (SkillFactory.isBlockedSkill(attack.skillId)) {
            chr.dropMessage(5, "由於<" + SkillFactory.getSkillName(attack.skillId) + ">技能數據異常,暫未開放使用.");
            c.sendEnableActions();
            return;
        }
        int linkSkillId = 0;
        int skillLevel = 0;
        int attackCount = 1;
        attack.attackType = AttackInfo.AttackType.MagicAttack;
        MapleStatEffect effect = null;
        Skill skill = null;
        if (c.getPlayer().isGm() && attack.skillId > 0) {
            c.getPlayer().dropMessage(40, "[Magic] " + SkillFactory.getSkillName(attack.skillId) + " / skillID :" + attack.skillId + " / skillLV :" + attack.skllv);
        }
        if (attack.skillId != 0) {
            linkSkillId = SkillConstants.getLinkedAttackSkill(attack.skillId);
            skill = SkillFactory.getSkill(attack.skillId);
            if (skill == null) {
                c.sendEnableActions();
                return;
            }
            skillLevel = chr.getSkillLevel(linkSkillId);
            effect = skill.getEffect(skillLevel);
            if (skillLevel <= 0 || effect == null) {
                chr.dropMessage(3, "[Magic Attack] " + String.valueOf(skill) + " Skill Lv: " + skillLevel + " Effect is Null!");
                c.sendEnableActions();
                return;
            }
            attackCount = effect.getAttackCount(chr);
        }
        DamageParse.calcDamage(attack, chr, attackCount, effect);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserMagicAttack(chr, skillLevel, 0, attack), chr.getPosition());
        DamageParse.applyAttack(attack, skill, chr, effect, SkillConstants.isPassiveAttackSkill(linkSkillId));
    }

    public static void UserNonTargetForceAtomAttack(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseAttack(InHeader.UserNonTargetForceAtomAttack, slea, chr);
        if (attack == null) {
            c.sendEnableActions();
            return;
        }
        if (SkillFactory.isBlockedSkill(attack.skillId)) {
            chr.dropMessage(5, "由於<" + SkillFactory.getSkillName(attack.skillId) + ">技能數據異常,暫未開放使用.");
            c.sendEnableActions();
            return;
        }
        int skillLevel = 0;
        int attackCount = 1;
        attack.attackType = AttackInfo.AttackType.MagicAttack;
        MapleStatEffect effect = null;
        Skill skill = null;
        if (attack.skillId != 0) {
            int linkSkillId = SkillConstants.getLinkedAttackSkill(attack.skillId);
            skill = SkillFactory.getSkill(attack.skillId);
            if (skill == null) {
                c.sendEnableActions();
                return;
            }
            skillLevel = chr.getSkillLevel(linkSkillId);
            effect = skill.getEffect(skillLevel);
            if (skillLevel <= 0 || effect == null) {
                chr.dropMessage(5, "[Atom Attack] " + String.valueOf(skill) + " Skill Lv: " + skillLevel + " Effect is Null!");
                c.sendEnableActions();
                return;
            }
            attackCount = effect.getAttackCount(chr);
        }
        DamageParse.calcDamage(attack, chr, attackCount, effect);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.UserMagicAttack(chr, skillLevel, 0, attack), chr.getPosition());
        DamageParse.applyAttack(attack, skill, chr, effect, true);
    }

    public static void DropMeso(int meso, MapleCharacter chr) {
        if (!chr.isAlive() || meso < 10 || meso > 50000 || (long)meso > chr.getMeso()) {
            chr.getClient().sendEnableActions();
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getPosition(), chr, chr, true, (byte)0);
        chr.getCheatTracker().checkDrop(true);
    }

    public static void UserSupserCannotRequest(MaplePacketReader lea, MapleCharacter player) {
        if (player == null || player.getMap() == null || !JobConstants.is傑諾(player.getJob())) {
            return;
        }
        byte mode = lea.readByte();
        MapleStatEffect effect = player.getSkillEffect(400041007);
        if (effect != null && mode == 0) {
            if (player.getBuffedIntValue(SecondaryStat.MegaSmasher) > 0) {
                player.dispelEffect(400041007);
            } else {
                effect.applyTo(player, true);
            }
        }
    }

    public static void ChangeAndroidEmotion(int emote, MapleCharacter chr) {
        if (emote > 0 && chr != null && chr.getMap() != null && !chr.isHidden() && emote <= 17 && chr.getAndroid() != null) {
            chr.getMap().broadcastMessage(AndroidPacket.showAndroidEmotion((int)chr.getId(), (int)0, (int)emote));
        }
    }

    public static void MoveAndroid(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        slea.readInt();
        int gatherDuration = slea.readInt();
        int nVal1 = slea.readInt();
        Point mPos = slea.readPos();
        Point oPos = slea.readPos();
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 9);
        if (res != null && chr != null && !res.isEmpty() && chr.getMap() != null && chr.getAndroid() != null) {
            chr.getAndroid().updatePosition(res);
            chr.getMap().broadcastMessage(chr, AndroidPacket.moveAndroid((int)chr.getId(), (int)gatherDuration, (int)nVal1, (Point)mPos, (Point)oPos, res), false);
        }
    }

    public static void ChangeEmotion(int emote, MapleCharacter chr) {
        if (chr != null) {
            int emoteid;
            MapleInventoryType type;
            if (emote > 7 && (type = ItemConstants.getInventoryType(emoteid = 5159992 + emote)) != null && chr.getInventory(type).findById(emoteid) == null) {
                chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
                return;
            }
            if (emote > 0 && chr.getMap() != null && !chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.facialExpression(chr, emote), false);
            }
        }
    }

    public static void Heal(MaplePacketReader slea, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        chr.updateTick(slea.readInt());
        slea.skip(4);
        slea.skip(4);
        int healHP = slea.readShort();
        int healMP = slea.readShort();
        PlayerStats stats = chr.getStat();
        if (stats.getHp() <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (healHP != 0 && chr.canHP(now + 1000L)) {
            if ((float)healHP > stats.getHealHP()) {
                healHP = (int)stats.getHealHP();
            }
            chr.addHPMP(healHP, 0);
        }
        if (healMP != 0 && !JobConstants.isNotMpJob(chr.getJob()) && chr.canMP(now + 1000L)) {
            if ((float)healMP > stats.getHealMP()) {
                healMP = (int)stats.getHealMP();
            }
            chr.addHPMP(0, healMP);
        }
    }

    public static void MovePlayer(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        List<LifeMovementFragment> res;
        if (chr == null || chr.getMap() == null) {
            return;
        }
        Point Original_Pos = chr.getPosition();
        slea.readByte();
        slea.readInt();
        slea.readInt();
        slea.readByte();
        int gatherDuration = slea.readInt();
        int nVal1 = slea.readInt();
        Point mPos = slea.readPos();
        Point oPos = slea.readPos();
        try {
            res = MovementParse.parseMovement(slea, 1);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return;
        }
        if (res != null) {
            MapleMap map = c.getPlayer().getMap();
            MovementParse.updatePosition(res, chr, 0);
            Point pos = chr.getPosition();
            map.objectMove(chr.getId(), chr, MaplePacketCreator.movePlayer(chr.getId(), res, gatherDuration, nVal1, mPos, oPos));
            if (chr.getFollowId() > 0 && chr.isFollowOn() && chr.isFollowInitiator()) {
                MapleCharacter fol = map.getPlayerObject(chr.getFollowId());
                if (fol != null) {
                    Point original_pos = fol.getPosition();
                    fol.getClient().announce(MaplePacketCreator.moveFollow(gatherDuration, nVal1, Original_Pos, original_pos, pos, res));
                    MovementParse.updatePosition(res, fol, 0);
                    map.objectMove(fol.getId(), fol, MaplePacketCreator.movePlayer(fol.getId(), res, gatherDuration, nVal1, mPos, oPos));
                } else {
                    chr.checkFollow();
                }
            }
            int count = c.getPlayer().getFallCounter();
            boolean samepos = pos.y > c.getPlayer().getOldPosition().y && Math.abs(pos.x - c.getPlayer().getOldPosition().x) < 5;
            boolean bl = samepos;
            if (samepos && (pos.y > map.getBottom() + 250 || map.getFootholds().findBelow(pos) == null)) {
                if (count > 5) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    c.getPlayer().setFallCounter(++count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            if (c.getPlayer().getMap().getId() != 450013500 || !c.getPlayer().getMap().isBlackMage3thSkill() || c.getPlayer().getPosition().x >= 85) {
                // empty if block
            }
            c.getPlayer().setOldPosition(pos);
        }
    }

    public static void ChangeMapSpecial(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        byte unk = slea.readByte();
        String portal_name = slea.readMapleAsciiString();
        if (chr == null || chr.getMap() == null) {
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(portal_name);
        if (portal != null && !chr.hasBlockedInventory()) {
            portal.enterPortal(c);
        } else {
            c.sendEnableActions();
        }
    }

    public static void ChangeMap(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        if (chr.isBanned()) {
            MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
            chr.changeMap(to, to.getPortal(0));
            c.sendEnableActions();
            return;
        }
        slea.skip(1);
        slea.readByte();
        int targetid = slea.readInt();
        switch (targetid) {
            case -1: {
                MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
                Point pos = slea.readPos();
                if (portal == null) {
                    c.sendEnableActions();
                    return;
                }
                portal.enterPortal(c);
                return;
            }
            case 0: {
                slea.readShort();
                slea.readByte();
                byte type = slea.readByte();
                boolean protectBuff = slea.readBool();
                slea.readBool();
                PlayerHandler.OnRevive(chr, type, protectBuff);
                break;
            }
            default: {
                if (chr.isInGameCurNode()) {
                    chr.changeMap(targetid, 0);
                    return;
                }
                chr.sendEnableActions();
            }
        }
        if (chr.getAranCombo() > 0) {
            chr.gainAranCombo(0, true);
        }
        if (JobConstants.is墨玄(c.getPlayer().getJob())) {
            c.announce(warpToGameHandler.XuanWarpToMap());
        }
        if (c.getPlayer().hasEquipped(1202193)) {
            c.write(warpToGameHandler.EquipRuneSetting());
        }
    }

    public static void EventReviveRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        byte type = slea.readByte();
        boolean protectBuff = slea.readBool();
        PlayerHandler.OnRevive(player, type, protectBuff);
    }

    public static void OnRevive(MapleCharacter chr, byte type, boolean protectBuff) {
        if (type == 11) {
            protectBuff = false;
        } else if (protectBuff) {
            int itemID = -1;
            if (chr.getEventInstance() != null && chr.getEventInstance().isPractice()) {
                itemID = 5133000;
            } else if (MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.CASH, 5133000, 1, true, false)) {
                itemID = 5133000;
            } else if (MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.CASH, 5133001, 1, true, false)) {
                itemID = 5133001;
            }
            if (itemID == -1) {
                chr.send(EffectPacket.ProtectBuffGain(5133000, 2));
                protectBuff = false;
            } else {
                chr.send(EffectPacket.ProtectBuffGain(itemID, 1));
            }
        }
        boolean eventRevive = false;
        if (chr.getEventInstance() != null) {
            // empty if block
        }
        if (chr.getPyramidSubway() != null) {
            chr.getStat().setHp(50, chr);
            chr.getPyramidSubway().fail(chr);
            return;
        }
        chr.disappearSummons(true);
        chr.removeDebuffs();
        MapleMap map = chr.getMap().getReturnMap();
        Point pos = null;
        switch (type) {
            case 0: {
                int forcedReturnMapId;
                if (chr.inEvent() && chr.getDeathCount() > 0) {
                    map = chr.getMap();
                    chr.setDeathCount(chr.getDeathCount() - 1);
                    chr.getClient().announce(MaplePacketCreator.IndividualDeathCountInfo(chr.getId(), chr.getDeathCount()));
                    chr.send(MaplePacketCreator.userTeleportOnRevive(chr.getId(), map.getPortal(0).getPosition()));
                    if (!protectBuff) {
                        chr.removeBuffs(false);
                    }
                    chr.getStat().hp = chr.getStat().getCurrentMaxHP();
                    chr.getStat().mp = chr.getStat().getCurrentMaxMP();
                    chr.updateHPMP(false);
                    break;
                }
                protectBuff = true;
                if (chr.getMap() != null && (forcedReturnMapId = chr.getMap().getForcedReturnId()) > 0 && forcedReturnMapId < 999999999) {
                    map = chr.getClient().getChannelServer().getMapFactory().getMap(forcedReturnMapId);
                }
                if (!protectBuff) {
                    chr.removeBuffs(false);
                }
                chr.getStat().hp = chr.getStat().getCurrentMaxHP();
                chr.getStat().mp = chr.getStat().getCurrentMaxMP();
                chr.updateHPMP(false);
                chr.changeMap(map, map.getPortal(0));
                break;
            }
            case 1: {
                if (chr.getBossLog("原地復活") >= ServerConfig.CHANNEL_PLAYER_RESUFREECOUNT) break;
                map = chr.getMap();
                pos = chr.getPosition();
                chr.setBossLog("原地復活");
                chr.dropMessage(5, "恭喜您原地復活成功，您今天還可以使用: " + (ServerConfig.CHANNEL_PLAYER_RESUFREECOUNT - chr.getBossLog("原地復活")) + " 次。");
                chr.changeMap(map, map.getPortal(0));
                break;
            }
            case 3: {
                if (chr.getPQPoint() < 10L) break;
                chr.gainPQPoint(-10L);
                map = chr.getMap();
                pos = chr.getPosition();
                break;
            }
            case 4: {
                if (!MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.CASH, 5510000, 1, true, false)) break;
                chr.send(MTSCSPacket.useWheel((byte)chr.getItemQuantity(5510000, false)));
                map = chr.getMap();
                pos = chr.getPosition();
                break;
            }
            case 5: {
                if (chr.getEffectForBuffStat(SecondaryStat.SoulStone) == null) break;
                chr.dispelEffect(SecondaryStat.SoulStone);
                map = chr.getMap();
                pos = chr.getPosition();
                break;
            }
            case 10: {
                if (!MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.CASH, 5511001, 1, true, false)) break;
                chr.send(MTSCSPacket.useWheel((byte)chr.getItemQuantity(5511001, false)));
                map = chr.getMap();
                pos = chr.getPosition();
                break;
            }
            case 8: {
                if (chr.getBossLog("原地復活") >= ServerConfig.CHANNEL_PLAYER_RESUFREECOUNT) break;
                map = chr.getMap();
                pos = chr.getPosition();
                chr.setBossLog("原地復活");
                chr.dropMessage(5, "恭喜您原地復活成功，您今天還可以使用: " + (ServerConfig.CHANNEL_PLAYER_RESUFREECOUNT - chr.getBossLog("原地復活")) + " 次。");
                break;
            }
            case 11: {
                if (chr.getAndroid() == null || chr.getAndroid().getItemId() != 1662072 && chr.getAndroid().getItemId() != 1662073) break;
                DeadDebuff.cancelDebuff(chr, true);
                chr.send(MTSCSPacket.useCharm(4, (byte)0, (byte)0, chr.getAndroid().getItemId()));
                MapleItemInformationProvider.getInstance().getItemEffect(2002100).applyTo(chr);
                map = chr.getMap();
                protectBuff = true;
                break;
            }
        }
    }

    public static void InnerPortal(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        slea.skip(1);
        MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
        short toX = slea.readShort();
        short toY = slea.readShort();
        Point pos = slea.readPos();
        if (portal == null) {
            return;
        }
        if (portal.getPosition().distance(chr.getPosition()) > 150.0 && !chr.isGm()) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            return;
        }
        chr.setPosition(pos);
        chr.getMap().objectMove(-1, chr, null);
        if (chr.getAndroid() != null) {
            chr.getAndroid().setPos(pos);
            chr.getMap().objectMove(-1, chr, null);
        }
        chr.checkFollow();
    }

    public static void ReIssueMedal(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        short questId = slea.readShort();
        int itemId = slea.readInt();
        MapleQuest quest = MapleQuest.getInstance(questId);
        if (quest != null & quest.getMedalItem() > 0 && chr.getQuestStatus(quest.getId()) == 2 && quest.getMedalItem() == itemId) {
            if (!chr.haveItem(itemId)) {
                int price = 100;
                int infoQuestId = 29949;
                Object infoData = "count=1";
                if (chr.containsInfoQuest(infoQuestId, "count=")) {
                    String line = chr.getInfoQuest(infoQuestId);
                    String[] splitted = line.split("=");
                    if (splitted.length == 2) {
                        int data = Integer.parseInt(splitted[1]);
                        infoData = "count=" + (data + 1);
                        price = data == 1 ? 1000 : (data == 2 ? 10000 : (data == 3 ? 100000 : 1000000));
                    } else {
                        chr.dropMessage(1, "重新領取勳章出現錯誤");
                        c.sendEnableActions();
                        return;
                    }
                }
                if (chr.getMeso() < (long)price) {
                    chr.dropMessage(1, "本次重新需要楓幣: " + price + "\r\n請檢查楓幣是否足夠");
                    c.sendEnableActions();
                    return;
                }
                chr.gainMeso(-price, true, true);
                MapleInventoryManipulator.addById(c, itemId, 1, "");
                chr.updateInfoQuest(infoQuestId, (String)infoData);
                c.announce(MaplePacketCreator.updateMedalQuestInfo((byte)0, itemId));
            } else {
                c.announce(MaplePacketCreator.updateMedalQuestInfo((byte)3, itemId));
            }
        } else {
            c.sendEnableActions();
        }
    }

    public static void PlayerUpdate(MapleClient c, MapleCharacter chr) {
        boolean autoSave = true;
        if (!autoSave || chr == null || chr.getMap() == null) {
            return;
        }
        if (chr.getCheatTracker().canSaveDB()) {
            chr.saveToDB(false, false);
        }
    }

    public static void DelTeachSkill(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int skillid = slea.readInt();
        Skill skill = SkillFactory.getSkill(skillid);
        if (skill == null || !chr.getLinkSkills().containsKey(skillid) && SkillConstants.getTeamTeachSkillId(skillid) <= 0) {
            c.sendEnableActions();
            c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillid, 6));
            return;
        }
        int toSkillId = SkillConstants.getTeachSkillId(skillid);
        SkillEntry skillEntry = chr.getSkills().get(skillid);
        int[] skills = SkillConstants.getTeamTeachSkills(skillid);
        if (skills != null) {
            toSkillId = skillid;
        }
        if (toSkillId > 0 && skillEntry != null && chr.teachSkill(skillid, skillEntry.teachId, true) > -1) {
            chr.changeTeachSkill(skillid, skillEntry.teachId, skillEntry.skillevel, true);
        }
        c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillid, 0));
    }

    public static void SetTeachSkill(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int skillId = slea.readInt();
        if (chr == null || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.sendEnableActions();
            c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillId, 7));
            return;
        }
        if (chr.getSkillLevel(skillId) > 0) {
            c.sendEnableActions();
            c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillId, 3));
            return;
        }
        int toChrId = slea.readInt();
        Pair toChrInfo = MapleCharacterUtil.getNameById((int)toChrId, (int)chr.getWorld());
        if (toChrInfo == null) {
            c.sendEnableActions();
            c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillId, 6));
            return;
        }
        int toChrAccId = (Integer)toChrInfo.getRight();
        MapleQuest quest = MapleQuest.getInstance(7783);
        if (quest != null && chr.getAccountID() == toChrAccId) {
            int toSkillId = SkillConstants.getTeachSkillId(skillId);
            Pair<Integer, SkillEntry> skillPair = chr.getSonOfLinkedSkills().get(skillId);
            if (toSkillId <= 0 || !chr.getSonOfLinkedSkills().containsKey(skillId) || skillPair == null) {
                c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillId, 5));
                c.sendEnableActions();
                return;
            }
            if (chr.teachSkill(skillId, toChrId, false) > 0) {
                Map<Integer, SkillEntry> skills;
                chr.changeTeachSkill(skillId, toChrId, skillPair.getRight().skillevel, false);
                quest.forceComplete(chr, 0);
                int tSkillId = SkillConstants.getTeamTeachSkillId(skillId);
                if (tSkillId > 1 && (skills = chr.getSkills()) != null && skills.containsKey(tSkillId)) {
                    c.announce(MaplePacketCreator.updateSkills(Collections.singletonMap(tSkillId, skills.get(tSkillId))));
                }
                c.announce(MaplePacketCreator.SetLinkSkillResult(skillId, new Pair<Integer, SkillEntry>(toChrId, chr.getLinkSkills().get(skillId)), tSkillId, chr.getTotalSkillLevel(tSkillId)));
                c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillId, 1));
            } else {
                c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillId, 5));
            }
        } else {
            c.announce(MaplePacketCreator.UpdateLinkSkillResult(skillId, 6));
        }
        c.sendEnableActions();
    }

    public static void ChangeMarketMap(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.sendEnableActions();
            return;
        }
        int chc = slea.readByte() + 1;
        int toMapId = slea.readInt();
        if (toMapId >= 910000001 && toMapId <= 910000022) {
            if (chr.getMapId() != toMapId) {
                MapleMap to = ChannelServer.getInstance(chc).getMapFactory().getMap(toMapId);
                chr.setMap(to);
                chr.changeChannel(chc);
            } else {
                chr.changeChannel(chc);
            }
        } else {
            MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(toMapId);
            chr.changeMap(to, to.getPortal(0));
        }
        c.sendEnableActions();
    }

    public static void UseContentMap(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            c.sendEnableActions();
            return;
        }
        short op = slea.readShort();
        if (op != 0) {
            c.sendEnableActions();
            return;
        }
        int toMapId = slea.readInt();
        if (chr.hasBlockedInventory()) {
            c.sendEnableActions();
            return;
        }
        if (MapConstants.isBossMap((int)toMapId)) {
            c.announce(MTSCSPacket.getTrockMessage((byte)11));
            c.sendEnableActions();
            return;
        }
        MapleMap moveTo = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(toMapId);
        if (moveTo == null || FieldLimitType.TELEPORTITEMLIMIT.check(moveTo.getFieldLimit())) {
            c.announce(MTSCSPacket.getTrockMessage((byte)11));
            c.sendEnableActions();
            return;
        }
        if (chr.getLevel() < moveTo.getLevelLimit()) {
            chr.dropMessage(1, "只有" + moveTo.getLevelLimit() + "等級可以移動的地區。");
            c.sendEnableActions();
            return;
        }
        chr.changeMap(moveTo);
    }

    public static void useTempestBlades(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int skillId = slea.readInt();
        int mobCount = slea.readInt();
        ArrayList<Integer> moboids = new ArrayList<Integer>();
        for (int i = 0; i < mobCount; ++i) {
            int moboid = slea.readInt();
            moboids.add(moboid);
        }
        MapleStatEffect effect = chr.getEffectForBuffStat(SecondaryStat.StopForceAtomInfo);
        if (effect != null) {
            if (skillId == 400011058) {
                effect = chr.getSkillEffect(effect.getSourceId() == 65121011 ? 400011059 : 400011058);
            }
            chr.dispelEffect(SecondaryStat.StopForceAtomInfo);
            chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(chr, effect, 0, moboids)), true);
        }
    }

    public static void showPlayerCash(MaplePacketReader slea, MapleClient c) {
    }

    public static void quickBuyCashShopItem(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int accId = slea.readInt();
        int playerId = slea.readInt();
        int mode = slea.readInt();
        slea.skip(4);
        int toCharge = slea.readByte() == 1 ? 1 : 2;
        switch (mode) {
            case 10: {
                if (chr == null || chr.getMap() == null) {
                    c.sendEnableActions();
                    return;
                }
                if (chr.getId() != playerId || chr.getAccountID() != accId) {
                    c.sendEnableActions();
                    return;
                }
                if (chr.getCSPoints(toCharge) >= 600 && chr.getTrunk().getSlots() < 93) {
                    chr.modifyCSPoints(toCharge, -600, false);
                    chr.getTrunk().increaseSlots((byte)4);
                    chr.getTrunk().saveToDB();
                    c.announce(MaplePacketCreator.playerCashUpdate(mode, toCharge, chr));
                    break;
                }
                chr.dropMessage(5, "擴充失敗，點數餘額不足或者倉庫欄位已超過上限。");
                break;
            }
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 9111001: 
            case 9112001: 
            case 9113002: 
            case 9114001: {
                int cost;
                if (chr == null || chr.getMap() == null) {
                    c.sendEnableActions();
                    return;
                }
                if (chr.getId() != playerId || chr.getAccountID() != accId) {
                    c.sendEnableActions();
                    return;
                }
                int iv = switch (mode) {
                    case 11, 9111001 -> 1;
                    case 12, 9112001 -> 2;
                    case 13, 9113002 -> 3;
                    case 14, 9114001 -> 4;
                    case 15 -> 5;
                    default -> -1;
                };
                int n = cost = mode > 16 ? 180 : 90;
                if (iv > 0) {
                    MapleInventoryType tpye = MapleInventoryType.getByType((byte)iv);
                    if (chr.getCSPoints(toCharge) >= cost && chr.getInventory(tpye).getSlotLimit() < 127) {
                        chr.modifyCSPoints(toCharge, -cost, false);
                        chr.getInventory(tpye).addSlot((byte)(mode > 16 ? 8 : 4));
                        c.announce(MaplePacketCreator.playerCashUpdate(mode, toCharge, chr));
                        break;
                    }
                    chr.dropMessage(1, "擴充失敗，點數餘額不足或者欄位已超過上限。");
                    break;
                }
                chr.dropMessage(1, "擴充失敗，擴充的類型不正確。");
                break;
            }
            case 5430001: 
            case 5790002: {
                int neednx;
                int n = neednx = mode == 5430001 ? 3000 : 1000;
                if (c.modifyCSPoints(toCharge, -neednx) && (mode == 5430001 && c.gainAccCharSlot() || mode == 5790002 && c.gainAccCardSlot())) {
                    c.announce(MaplePacketCreator.playerSoltUpdate(mode, c.getCSPoints(1), c.getCSPoints(2)));
                    return;
                }
                c.dropMessage("擴充失敗，點數餘額不足或者欄位已超過上限。");
            }
        }
    }

    public static void zeroTag(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || !JobConstants.is神之子(chr.getJob())) {
            return;
        }
        chr.zeroTag();
    }

    public static void changeZeroLook(MaplePacketReader slea, MapleClient c, MapleCharacter chr, boolean end) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int type = slea.readInt();
        String oneInfo = chr.getOneInfo(52999, "zeroMask");
        long mask = oneInfo == null ? 0L : Long.parseLong(oneInfo);
        ZeroMask zm = ZeroMask.getByType((int)type);
        if (zm == null) {
            return;
        }
        mask = slea.readByte() == 1 ? (mask |= (long)zm.getFlag()) : (mask &= (long)(~zm.getFlag()));
        chr.updateOneInfo(52999, "zeroMask", String.valueOf(mask));
        c.announce(MaplePacketCreator.zeroInfo(chr, 256, chr.isBeta()));
    }

    public static void ExtraAttack(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int skillId = slea.readInt();
        if (skillId == 36110005 && slea.available() <= 2L) {
            return;
        }
        if (slea.available() < 12L) {
            if (player.isDebug()) {
                player.dropDebugMessage(1, "[額外攻擊] 額外技能長度異常，技能：" + skillId);
            }
            return;
        }
        int type = slea.readInt();
        int modOid = slea.readInt();
        int tick = slea.readInt();
        if (player.isDebug()) {
            player.dropDebugMessage(1, "[額外攻擊] 開始解析額外技能，技能：" + skillId + " 類型：" + type);
        }
        player.updateTick(tick);
        Item weapon = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(JobConstants.is神之子(player.getJob()) && player.isBeta() ? -10 : -11));
        MapleWeapon wt = weapon == null ? MapleWeapon.沒有武器 : MapleWeapon.getByItemID(weapon.getItemId());
        int finalskill = player.getStat().getFinalAttackSkill();
        if (finalskill > 0 && wt != MapleWeapon.沒有武器) {
            c.announce(MaplePacketCreator.FinalAttack(c.getPlayer(), tick, player.isFacingLeft(), skillId, finalskill, wt.getWeaponType(), Collections.emptyList()));
        }
    }

    public static void MoveEnergyBall(MaplePacketReader slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getMap() == null) {
            return;
        }
        B2BodyAttackInfo ai = new B2BodyAttackInfo();
        ai.type = slea.readShort();
        ai.fromId = slea.readInt();
        ai.oid = slea.readInt();
        ai.mapId = player.getMapId();
        switch (ai.type) {
            case 4: {
                ai.pos = slea.readPos();
                slea.readPos();
                ai.skillid = slea.readInt();
                ai.dir = slea.readByte() > 0;
                ai.time = 900;
                slea.skip(1);
                if (slea.readByte() == 1) {
                    slea.readMapleAsciiString();
                }
                slea.readInt();
                slea.readInt();
                ai.t0 = slea.readShort();
                ai.t1 = slea.readShort();
                ai.t2 = slea.readShort();
                slea.readByte();
                ai.un1 = slea.readInt();
                slea.readInt();
                slea.readInt();
                ai.dirx = slea.readInt();
                ai.diry = slea.readInt();
                break;
            }
            case 0: {
                ai.enerhe = slea.readByte();
                ai.pos = slea.readPos();
                if (ai.enerhe == 5) {
                    ai.dirx = slea.readShort();
                    ai.oidy = slea.readShort();
                } else if (ai.enerhe == 6) {
                    ai.dirx = slea.readInt();
                }
                ai.diry = slea.readShort();
                ai.skillid = slea.readInt();
                ai.level = slea.readShort();
                slea.skip(2);
                ai.t0 = slea.readShort();
                ai.t1 = slea.readShort();
                ai.t2 = slea.readShort();
                break;
            }
            case 3: {
                ai.akl = ai.fromId;
                ai.skillid = slea.readInt();
                ai.level = slea.readInt();
                ai.dirx = slea.readInt();
                ai.diry = slea.readInt();
                break;
            }
            case 5: {
                ai.skillid = slea.readInt();
                ai.level = slea.readInt();
            }
        }
        if (ai.skillid == 3111013) {
            player.addHPMP(0, -player.getSkillEffect(ai.skillid).getMpCon(), false);
        }
        MapleMap map = player.getMap();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_UserB2BodyResult);
        mplew.writeShort(ai.type);
        mplew.writeInt(ai.fromId);
        mplew.writeInt(ai.mapId);
        switch (ai.type) {
            case 4: {
                mplew.writeShort(1);
                for (int i = 0; i <= 0; ++i) {
                    mplew.write(0);
                    mplew.writePos(ai.pos);
                    mplew.writeInt(ai.time);
                    mplew.writeShort(ai.t0);
                    mplew.writeShort(ai.t1);
                    mplew.writeShort(ai.t2);
                    mplew.write(0);
                    mplew.writeInt(0);
                    mplew.writeInt(ai.skillid);
                    mplew.writeBool(ai.dir);
                    mplew.writeInt(ai.un1);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    mplew.write(0);
                    mplew.writeInt(ai.dirx * (ai.dir ? -1 : 1));
                    mplew.writeInt(ai.diry);
                }
                break;
            }
            case 3: {
                mplew.writeInt(ai.akl);
                mplew.writeInt(ai.skillid);
                mplew.writeInt(ai.dirx);
                mplew.writeInt(ai.diry);
                mplew.writeInt(ai.oid);
                break;
            }
            case 5: {
                mplew.writeInt(ai.skillid);
                mplew.writeInt(ai.level);
                break;
            }
            case 0: {
                mplew.writeShort(1);
                for (int j = 0; j <= 0; ++j) {
                    mplew.writeInt(ai.oid);
                    mplew.write(ai.enerhe);
                    mplew.writeBool(false);
                    mplew.writePos(ai.pos);
                    if (ai.enerhe == 5) {
                        mplew.writeShort(ai.dirx);
                        mplew.writeShort(ai.oidy);
                    } else if (ai.enerhe == 6) {
                        mplew.writeInt(ai.dirx);
                    }
                    mplew.writeShort(ai.diry);
                    mplew.writeShort(ai.t0);
                    mplew.writeShort(ai.t1);
                    mplew.writeShort(ai.t2);
                    mplew.writeInt(ai.skillid);
                    mplew.writeShort(ai.level);
                    mplew.write(0);
                }
                break;
            }
        }
        map.broadcastMessage(player, mplew.getPacket(), ai.type != 4);
        if (ai.type == 4) {
            mplew = new MaplePacketLittleEndianWriter();
            mplew.writeOpcode(OutHeader.UserB2BodyTarget);
            mplew.writeInt(4);
            mplew.writeInt(ai.oid);
            c.announce(mplew.getPacket());
        }
    }

    public static void SpawnArrowsTurret(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        byte side = slea.readByte();
        Point pos = slea.readPosInt();
        MapleStatEffect effect = chr.getSkillEffect(3111013);
        if (effect != null) {
            MapleFieldAttackObj tospawn = new MapleFieldAttackObj(1, chr.getId(), effect.getU() * 1000);
            tospawn.setPosition(pos);
            tospawn.setSide(side > 0);
            for (MapleFieldAttackObj obj : chr.getMap().getFieldAttackObject(chr)) {
                obj.cancel();
                chr.getMap().disappearMapObject(obj);
            }
            chr.getMap().createdFieldAttackObject(tospawn);
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.LP_FIELDATTACKOBJ_SETATTACK.getValue());
            mplew.writeInt(tospawn.getObjectId());
            mplew.writeInt(0);
            c.announce(mplew.getPacket());
        }
    }

    public static void showTrackFlames(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int skillId = slea.readInt();
        slea.skip(1);
        int skillLevel = chr.getSkillLevel(skillId);
        Skill skill = SkillFactory.getSkill(skillId);
        MapleStatEffect effect = skill.getEffect(skillLevel);
        if (effect != null) {
            int[] skills;
            for (int s : skills = new int[]{12120007, 0xB8C8C8, 12100026, 12000022}) {
                skillLevel = chr.getSkillLevel(s);
                if (skillLevel <= 0 || (effect = (skill = SkillFactory.getSkill(s)).getEffect(skillLevel)) == null) continue;
                effect.applyTo(chr);
                break;
            }
        }
    }

    public static void selectJaguar(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        slea.skip(4);
        int id = slea.readInt();
        chr.getQuestNAdd(MapleQuest.getInstance(111112)).setCustomData(String.valueOf((id + 1) * 10));
        c.announce(MaplePacketCreator.updateJaguar(chr));
    }

    public static void updateSoulEffect(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        boolean open = slea.readByte() == 1;
        int questid = 26535;
        MapleQuest q = MapleQuest.getInstance(questid);
        if (q == null) {
            return;
        }
        MapleQuestStatus status = c.getPlayer().getQuestNAdd(q);
        open = status.getCustomData() != null && !status.getCustomData().equalsIgnoreCase("effect=1");
        String data = open ? "effect=1" : "effect=0";
        status.setCustomData(data);
        c.getPlayer().updateInfoQuest(questid, data);
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.updateSoulEffect(c.getPlayer().getId(), open));
    }

    public static void onReward(MaplePacketReader slea, MapleClient c) {
        slea.readInt();
        int id = slea.readInt();
        slea.readInt();
        slea.readInt();
        slea.readInt();
        slea.readInt();
        slea.readLong();
        slea.readInt();
        slea.readInt();
        slea.readLong();
        slea.readLong();
        slea.readInt();
        slea.readInt();
        slea.readMapleAsciiString();
        slea.readMapleAsciiString();
        slea.readMapleAsciiString();
        byte mode = slea.readByte();
        if (mode == 1) {
            slea.readByte();
        }
        if (slea.available() > 0L) {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropDebugMessage(2, "[領取獎勵] 有未讀完數據");
            }
            return;
        }
        if (mode == 1) {
            MapleReward reward = c.getPlayer().getReward(id);
            if (reward == null) {
                c.announce(MaplePacketCreator.updateReward(0, (byte)12, null, 0L));
                c.sendEnableActions();
                return;
            }
            if (reward.getReceiveDate() > 0L && reward.getReceiveDate() > System.currentTimeMillis()) {
                c.announce(MaplePacketCreator.updateReward(0, (byte)12, null, 0L));
                c.sendEnableActions();
                return;
            }
            if (reward.getExpireDate() > 0L && reward.getExpireDate() <= System.currentTimeMillis()) {
                c.getPlayer().deleteReward(reward.getId());
                c.announce(MaplePacketCreator.updateReward(0, (byte)12, null, 0L));
                c.sendEnableActions();
                return;
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int msg = 33;
            long quantity = 0L;
            switch (reward.getType()) {
                case 1: 
                case 2: {
                    quantity = reward.getAmount();
                    if (!MapleInventoryManipulator.checkSpace(c, reward.getItemId(), (int)quantity, "")) {
                        msg = (byte)(!ii.isCash(reward.getItemId()) ? 21 : 22);
                        break;
                    }
                    msg = (byte)(!ii.isCash(reward.getItemId()) ? 12 : 13);
                    int period = 0;
                    if (ItemConstants.getInventoryType(reward.getItemId(), false).equals((Object)MapleInventoryType.EQUIP) && !ItemConstants.類型.可充值道具(reward.getItemId())) {
                        quantity = 1L;
                        Equip item = ii.getEquipById(reward.getItemId());
                        if (period > 0) {
                            item.setExpiration(System.currentTimeMillis() + (long)period * 24L * 60L * 60L * 1000L);
                        }
                        item.setGMLog("從獎勵箱中獲得, 時間 " + DateUtil.getCurrentDate());
                        String name = ii.getName(reward.getItemId());
                        if (reward.getItemId() / 10000 == 114 && name != null && name.length() > 0) {
                            String str = "<" + name + ">獲得稱號。";
                            c.getPlayer().dropMessage(-1, str);
                            c.getPlayer().dropMessage(5, str);
                        }
                        item.setSN(MapleInventoryManipulator.getUniqueId(item.getItemId(), -1));
                        MapleInventoryManipulator.addbyItem(c, item.copy());
                    } else {
                        MaplePet pet;
                        if (ItemConstants.類型.寵物(reward.getItemId())) {
                            pet = MaplePet.createPet(reward.getItemId());
                            period = ii.getLife(reward.getItemId());
                        } else {
                            pet = null;
                        }
                        MapleInventoryManipulator.addById(c, reward.getItemId(), (int)quantity, "", pet, period, "從獎勵箱中獲得, 時間 " + DateUtil.getCurrentDate());
                    }
                    c.getPlayer().deleteReward(reward.getId());
                    break;
                }
                case 3: {
                    if (c.getPlayer().getCSPoints(2) + (int)reward.getAmount() >= 0) {
                        c.getPlayer().modifyCSPoints(2, (int)reward.getAmount(), false);
                        c.getPlayer().deleteReward(reward.getId());
                        quantity = (int)reward.getAmount();
                        msg = 11;
                        break;
                    }
                    msg = 20;
                    break;
                }
                case 4: {
                    if (c.getPlayer().getMeso() + reward.getAmount() < Long.MAX_VALUE && c.getPlayer().getMeso() + reward.getAmount() > 0L) {
                        c.getPlayer().gainMeso(reward.getAmount(), true, true);
                        c.getPlayer().deleteReward(reward.getId());
                        quantity = (int)reward.getAmount();
                        msg = 14;
                        break;
                    }
                    msg = 23;
                    break;
                }
                case 5: {
                    if (c.getPlayer().getLevel() < ServerConfig.CHANNEL_PLAYER_MAXLEVEL) {
                        c.getPlayer().gainExp(reward.getAmount(), true, true, true);
                        c.getPlayer().deleteReward(reward.getId());
                        quantity = (int)reward.getAmount();
                        msg = 15;
                        break;
                    }
                    msg = 24;
                    break;
                }
                default: {
                    if (!c.getPlayer().isAdmin()) break;
                    c.getPlayer().dropDebugMessage(2, "[領取獎勵] 未處理領取類型[" + reward.getType() + "]");
                }
            }
            c.announce(MaplePacketCreator.receiveReward(reward.getId(), (byte)msg, quantity));
        } else if (mode == 2) {
            c.getPlayer().deleteReward(id);
            c.announce(MaplePacketCreator.updateReward(0, (byte)10, null, 0L));
            c.sendEnableActions();
        } else if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropDebugMessage(2, "[領取獎勵] 未處理操作類型[" + mode + "]");
        }
    }

    public static void effectSwitch(MaplePacketReader slea, MapleClient c) {
        int pos = slea.readInt();
        c.getPlayer().updateEffectSwitch(pos);
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.getEffectSwitch(c.getPlayer().getId(), c.getPlayer().getEffectSwitch()), false);
        c.announce(EffectPacket.getEffectSwitch(c.getPlayer().getId(), c.getPlayer().getEffectSwitch()));
        c.sendEnableActions();
    }

    public static void spawnSpecial(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int skillid = slea.readInt();
        slea.readInt();
        MapleStatEffect effect = chr.getSkillEffect(skillid);
        if (effect == null) {
            return;
        }
        Point pos = effect.getLt2() != null ? effect.getLt2() : effect.getLt();
        int total = slea.readInt();
        for (int i = 0; i < total; ++i) {
            slea.readInt();
            slea.readShort();
            int x1 = slea.readInt();
            int y1 = slea.readInt();
            int x2 = slea.readInt();
            int y2 = slea.readInt();
            Rectangle bounds = new Rectangle(x1, y1, x2 - x1, y2 - y1);
            MapleAffectedArea mist = new MapleAffectedArea(bounds, chr, effect, new Point(x1 - pos.x, y1 - pos.y));
            chr.getMap().createAffectedArea(mist);
        }
    }

    public static void showKSPsychicGrabHanlder(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int skillId = slea.readInt();
        short skilllevel = slea.readShort();
        int n1 = slea.readInt();
        int n2 = slea.readInt();
        if (chr.getSkillEffect(skillId) == null) {
            return;
        }
        LinkedList<KSPsychicSkillEntry> infos = new LinkedList<KSPsychicSkillEntry>();
        int count = chr.getSkillLevel(142120000) > 0 ? 5 : 3;
        for (int i = 0; i < count; ++i) {
            KSPsychicSkillEntry ksse = new KSPsychicSkillEntry();
            slea.skip(1);
            ksse.setOid(slea.readInt());
            slea.skip(4);
            ksse.setMobOid(slea.readInt());
            ksse.setObjectid(slea.readShort());
            ksse.setN5(slea.readInt());
            slea.readShort();
            slea.readByte();
            ksse.setN1(slea.readInt());
            ksse.setN2(slea.readInt());
            ksse.setN3(slea.readInt());
            ksse.setN4(slea.readInt());
            infos.add(ksse);
        }
        chr.getMap().addKSPsychicObject(chr.getId(), skillId, infos);
        if (!infos.isEmpty()) {
            chr.getMap().broadcastMessage(EffectPacket.showKSPsychicGrab(chr.getId(), skillId, skilllevel, infos, n1, n2), chr.getPosition());
        }
        infos.clear();
    }

    public static void showKSPsychicAttackHanlder(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int skillid = slea.readInt();
        short skilllevel = slea.readShort();
        int n1 = slea.readInt();
        int n2 = slea.readInt();
        byte n3 = slea.readByte();
        int n4 = slea.readInt();
        int n5 = -1;
        int n6 = -1;
        if (n4 != 0) {
            n5 = slea.readInt();
            n6 = slea.readInt();
        }
        int n8 = -1;
        int n9 = -1;
        int n7 = slea.readInt();
        if (n7 != 0) {
            n8 = slea.readInt();
            n9 = slea.readInt();
        }
        int n10 = -1;
        int n11 = -1;
        if (chr.getSkillLevel(SkillConstants.getLinkedAttackSkill(skillid)) != skilllevel) {
            return;
        }
        MapleStatEffect effect = SkillFactory.getSkill(skillid).getEffect(skilllevel);
        int ppcon = effect.getPPCon();
        if (ppcon > 0) {
            chr.gainPP(-ppcon);
        }
        chr.getMap().broadcastMessage(chr, EffectPacket.showKSPsychicAttack(chr.getId(), skillid, skilllevel, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11), false);
    }

    public static void showKSPsychicReleaseHanlder(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int skillid = slea.readInt();
        int skilllevel = slea.readInt();
        int moboid = slea.readInt();
        int objectid = slea.readInt();
        if (chr.getSkillEffect(skillid) == null) {
            return;
        }
        int oid = chr.getMap().removeKSPsychicObject(chr.getId(), skillid, moboid != 0 ? moboid : objectid);
        if (oid > 0) {
            chr.getMap().broadcastMessage(EffectPacket.showKSPsychicRelease(chr.getId(), oid), chr.getPosition());
        }
    }

    public static void showGiveKSUltimate(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int mode = slea.readInt();
        int type = slea.readInt();
        slea.skip(4);
        int oid = slea.readInt();
        int skillid = slea.readInt();
        short skilllevel = slea.readShort();
        int time = slea.readInt();
        byte n2 = slea.readByte();
        short n3 = slea.readShort();
        short n4 = slea.readShort();
        short n5 = slea.readShort();
        int n6 = slea.readInt();
        int n7 = slea.readInt();
        Skill skill = SkillFactory.getSkill(skillid);
        if (skill == null) {
            return;
        }
        MapleStatEffect effect = skill.getEffect(skilllevel);
        int ppcon = effect.getPPCon();
        if (ppcon > 0) {
            if (chr.getSpecialStat().getPP() >= ppcon) {
                chr.gainPP(-ppcon);
            } else {
                chr.dropMessage(5, "施展技能所需的心魂點數不足。");
                return;
            }
        }
        if (chr.getSkillEffect(skillid) == null) {
            return;
        }
        if (skillid == 142121005) {
            chr.getMap().addKSUltimateSkill(chr.getId(), Math.abs(oid));
            if (chr.getSpecialStat().getPP() > 0) {
                c.outPacket(OutHeader.LP_TemporaryStatSet.getValue(), "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 2D 98 78 08 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 00 00 00 00 00 01");
            }
        } else if (skillid == 142121030) {
            chr.gainPP(30);
        } else if (skillid == 400021008) {
            chr.registerSkillCooldown(400020009, 0, true);
            chr.registerSkillCooldown(400020010, 0, true);
            chr.registerSkillCooldown(400020011, 0, true);
            chr.getSkillEffect(400021008).applyTo(chr);
        }
        chr.send_other(EffectPacket.showGiveKSUltimate(chr.getId(), mode, type, oid, skillid, skilllevel, time, n2, n3, n4, n5, n6, n7), true);
    }

    public static void showAttackKSUltimate(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int oid = slea.readInt();
        short type = slea.readShort();
        slea.readInt();
        slea.readInt();
        if (chr.getMap().isKSUltimateSkill(chr.getId(), oid)) {
            chr.gainPP(-1);
        }
        chr.getMap().broadcastMessage(EffectPacket.showAttackKSUltimate(oid, type), chr.getPosition());
    }

    public static void showKSMonsterEffect(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
        MapleStatEffect effect;
        if (chr == null || chr.getMap() == null) {
            return;
        }
        if (!JobConstants.is凱內西斯(chr.getJob())) {
            return;
        }
        int skillid = lea.readInt();
        short skilllevel = lea.readShort();
        int areaid = lea.readInt();
        lea.skip(1);
        lea.skip(8);
        int size = lea.readShort();
        Skill skill = SkillFactory.getSkill(skillid);
        if (skill != null && skillid != 400021008 && (effect = skill.getEffect(skilllevel)) != null) {
            for (int i = 0; i < size; ++i) {
                MapleMonster monster = chr.getMap().getMonsterByOid(lea.readInt());
            }
        }
    }

    public static void showCancelKSUltimate(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int oid = slea.readInt();
        int skilleffect = slea.readInt();
        if (!JobConstants.is凱內西斯(chr.getJob())) {
            return;
        }
        switch (skilleffect) {
            case 142121005: {
                c.outPacket(OutHeader.LP_TemporaryStatReset.getValue(), "01 01 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
            }
        }
        chr.getMap().removeKSUltimateSkill(chr.getId(), oid);
        chr.getMap().broadcastMessage(EffectPacket.showCancelKSUltimate(chr.getId(), Math.abs(oid)), chr.getPosition());
    }

    public static void showTornadoKSUltimate(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int oid = slea.readInt();
        if (!JobConstants.is凱內西斯(chr.getJob())) {
            return;
        }
        chr.getMap().broadcastMessage(EffectPacket.showAttackKSUltimate(oid, 1), chr.getPosition());
    }

    public static void selectChair(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        chr.dropMessage(1, "該功能暫未開放。");
        c.sendEnableActions();
    }

    public static void startBattleStatistics(MaplePacketReader slea, MapleClient c) {
        if (slea.readByte() == 1) {
            c.announce(MaplePacketCreator.startBattleStatistics());
        }
    }

    public static void CashCheck(MaplePacketReader slea, MapleClient c) {
        if (slea.readByte() == 1) {
            // empty if block
        }
    }

    public static void UseTowerChairSetting(MaplePacketReader lea, MapleClient c, MapleCharacter player) {
        lea.skip(4);
        for (int i = 0; i < 6; ++i) {
            int n2 = lea.readInt();
            if (player.haveItem(n2)) {
                player.updateOneInfo(7266, String.valueOf(i), String.valueOf(n2));
                continue;
            }
            player.updateOneInfo(7266, String.valueOf(i), "0");
        }
        player.getClient().announce(MaplePacketCreator.useTowerChairSetting());
    }

    /*
     * Unable to fully structure code
     */
    public static void VCoreOperation(final MaplePacketReader slea, MapleClient c, final MapleCharacter player) {
        if (player == null || player.getMap() == null || player.getLevel() < 200) {
            return;
        }
        int action = slea.readInt();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        switch (action) {
            case 0: {
                int vcoreoid = slea.readInt();
                slea.readInt();
                slea.readInt();
                int index = slea.readInt();
                VCoreSkillEntry vcoreskill = player.getVCoreSkill().get(vcoreoid);
                if (vcoreskill == null || vcoreskill.getSlot() != 1) {
                    break;
                }
                if (player.checkVCoreSkill(vcoreskill.getSkill(1))) {
                    c.sendEnableActions();
                    return;
                }
                if (index < 0 && (index = player.getNextVMatrixSlot()) < 0) {
                    c.sendEnableActions();
                    return;
                }
                VMatrixSlot slot = player.getVMatrixSlot().get(index);
                final int left = (player.getLevel() - 200) / 5 + VMatrixOption.EquipSlotMin;
                final int n2 = index - left;
                final boolean b = (index < left || (index >= left && n2 >= 0 && n2 <= 1 && index < VMatrixOption.EquipSlotMax));
                if (slot == null || !b) {
                    c.sendEnableActions();
                    return;
                } else if (slot.getIndex() != -1) {
                    player.send(VCorePacket.updateVCoreList(player, true, 0, vcoreoid));
                    return;
                }
                if (index < left || slot.getUnlock() == 1) {
                    vcoreskill.setIndex(index);
                    player.setVCoreSkillSlot(vcoreoid, 2);
                    slot.setIndex(vcoreoid);
                    int extend = slot.getExtend();
                    for (int i = 1; i <= 3; ++i) {
                        if (vcoreskill.getSkill(i) > 0) {
                            player.changeSingleSkillLevel(vcoreskill.getSkill(i), player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));

                            if (vcoreskill.getSkill(i) == 通用V核心.海盜通用.滿載骰子 && (JobConstants.is傑諾(player.getJob()) || JobConstants.is隱月(player.getJob()) || JobConstants.is天使破壞者(player.getJob()) || JobConstants.is閃雷悍將(player.getJob()))) {
                                player.changeSingleSkillLevel(通用V核心.海盜通用.幸運骰子, player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));
                            }
                        }
                    }
                    player.send(VCorePacket.updateVCoreList(player, true, 0, vcoreoid));
                }
                break;
            }
            case 1: {
                int vcoreoid = slea.readInt();
                slea.readInt();
                VCoreSkillEntry vcoreskill = player.getVCoreSkill().get(vcoreoid);
                if (vcoreskill == null || vcoreskill.getSlot() != 2) {
                    break;
                }
                player.setVCoreSkillSlot(vcoreoid, 1);
                VMatrixSlot slot = player.getVMatrixSlot().get(vcoreskill.getIndex());
                if (slot != null && vcoreoid == slot.getIndex()) {
                    slot.setIndex(-1);
                    for (int i = 1; i <= 3; ++i) {
                        if (vcoreskill.getSkill(i) > 0) {
                            player.changeSingleSkillLevel(vcoreskill.getSkill(i), player.getVCoreSkillLevel(vcoreskill.getSkill(i)) > 0 ? player.getVCoreSkillLevel(vcoreskill.getSkill(i)) : -1, (byte) (vcoreskill.getType() == 2 ? 1 : vcoreskill.getType() == 0 ? 25 : 50));

                            if (vcoreskill.getSkill(i) == 通用V核心.海盜通用.滿載骰子 && (JobConstants.is傑諾(player.getJob()) || JobConstants.is隱月(player.getJob()) || JobConstants.is天使破壞者(player.getJob()) || JobConstants.is墨玄(player.getJob()) || JobConstants.is閃雷悍將(player.getJob()))) {
                                player.changeSingleSkillLevel(通用V核心.海盜通用.幸運骰子, player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));
                            }
                        }
                    }
                    player.send(VCorePacket.updateVCoreList(player, true, 1, vcoreoid));
                    break;
                }
            }
            case 4: {
                int allExp = 0;
                int vcoreoid_1 = slea.readInt();
                int size = slea.readInt();
                VCoreSkillEntry vcoreskill_1 = player.getVCoreSkill().get(vcoreoid_1);
                if (vcoreskill_1 == null || vcoreskill_1.getSlot() <= 0) {
                    break;
                }
                int currLevel = vcoreskill_1.getLevel();
                for (int k = 1; k <= size; ++k) {
                    int vcoreoid_x = slea.readInt();
                    VCoreSkillEntry vcoreskill_x = player.getVCoreSkill().get(vcoreoid_x);
                    Triple<Integer, Integer, Integer> vcoredata = ii.getVcores(vcoreskill_1.getType()).get(vcoreskill_1.getLevel());
                    int expEnforce = ii.getVcores(vcoreskill_x.getType()).get(vcoreskill_x.getLevel()).getMid();
                    if (vcoreskill_1.getLevel() < 25) {
                        vcoreskill_1.gainExp(expEnforce);
                        allExp += expEnforce;
                        if (vcoreskill_1.getExp() >= vcoredata.getLeft()) {
                            vcoreskill_1.levelUP();
                            if (vcoreskill_1.getLevel() >= 25) {
                                vcoreskill_1.setLevel(25);
                                vcoreskill_1.setExp(0);
                            } else {
                                vcoreskill_1.setExp(vcoreskill_1.getExp() - vcoredata.getLeft());
                            }
                        }
                        VMatrixSlot slot = player.getVMatrixSlot().get(vcoreskill_1.getIndex());
                        if (slot != null) {
                            int extend = slot.getExtend();
                            for (int i = 1; i <= 3; ++i) {
                                if (vcoreskill_1.getSkill(i) > 0) {
                                    player.changeSingleSkillLevel(vcoreskill_1.getSkill(i), player.getVCoreSkillLevel(vcoreskill_1.getSkill(i)), (byte) ((vcoreskill_1.getType() == 2) ? 1 : ((vcoreskill_1.getType() == 0) ? 25 : 50)));

                                    if (vcoreskill_1.getSkill(i) == 通用V核心.海盜通用.滿載骰子 && (JobConstants.is傑諾(player.getJob()) || JobConstants.is隱月(player.getJob()) || JobConstants.is天使破壞者(player.getJob()) || JobConstants.is墨玄(player.getJob()) || JobConstants.is閃雷悍將(player.getJob()))) {
                                        player.changeSingleSkillLevel(通用V核心.海盜通用.幸運骰子, player.getVCoreSkillLevel(vcoreskill_1.getSkill(i)), (byte) ((vcoreskill_1.getType() == 2) ? 1 : ((vcoreskill_1.getType() == 0) ? 25 : 50)));
                                    }
                                }
                            }
                        }
                        player.setVCoreSkillSlot(vcoreoid_x, 0);
                    } else {
                        player.dropMessage(1, "最多可強化到25級！");
                    }
                }
                player.send(VCorePacket.showVCoreSkillExpResult(vcoreoid_1, allExp, currLevel, vcoreskill_1.getLevel()));
                player.send(VCorePacket.updateVCoreList(player, true, 3, 0));
                break;
            }
            case 6: {
                int vcoreoid = slea.readInt();
                VCoreSkillEntry vcoreskill = player.getVCoreSkill().get(vcoreoid);
                if (vcoreskill != null && vcoreskill.getSlot() > 0) {
                    player.removeVCoreSkill(vcoreoid);
                    for (int i = 1; i <= 3; ++i) {
                        if (vcoreskill.getSkill(i) > 0) {
                            player.changeSingleSkillLevel(vcoreskill.getSkill(i), player.getVCoreSkillLevel(vcoreskill.getSkill(i)) > 0 ? player.getVCoreSkillLevel(vcoreskill.getSkill(i)) : -1, (byte) (vcoreskill.getType() == 2 ? 1 : 50));

                            if (vcoreskill.getSkill(i) == 通用V核心.海盜通用.滿載骰子 && (JobConstants.is傑諾(player.getJob()) || JobConstants.is隱月(player.getJob()) || JobConstants.is天使破壞者(player.getJob()) || JobConstants.is墨玄(player.getJob()) || JobConstants.is閃雷悍將(player.getJob()))) {
                                player.changeSingleSkillLevel(通用V核心.海盜通用.幸運骰子, player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));
                            }
                        }
                    }
                    Triple<Integer, Integer, Integer> vcoredata = ii.getVcores(vcoreskill.getType()).get(vcoreskill.getLevel());
                    player.gainVCraftCore(vcoredata.getRight());
                    player.send(VCorePacket.updateVCoreList(player, true, 5, 0));
                    player.send(VCorePacket.addVCorePieceResult(vcoredata.getRight()));
                }
                break;
            }
            case 7: {
                final int size = slea.readInt();
                int total = 0;
                for (int k = 0; k < size; ++k) {
                    final int vcoreoid = slea.readInt();
                    final VCoreSkillEntry vcoreskill = player.getVCoreSkill().get(vcoreoid);
                    if (vcoreskill != null && vcoreskill.getSlot() > 0) {
                        player.removeVCoreSkill(vcoreoid);
                        for (int i = 1; i <= 3; ++i) {
                            if (vcoreskill.getSkill(i) > 0) {
                                player.changeSingleSkillLevel(vcoreskill.getSkill(i), player.getVCoreSkillLevel(vcoreskill.getSkill(i)) > 0 ? player.getVCoreSkillLevel(vcoreskill.getSkill(i)) : -1, (byte) (vcoreskill.getType() == 2 ? 1 : 50));

                                if (vcoreskill.getSkill(i) == 通用V核心.海盜通用.滿載骰子 && (JobConstants.is傑諾(player.getJob()) || JobConstants.is隱月(player.getJob()) || JobConstants.is天使破壞者(player.getJob()) || JobConstants.is墨玄(player.getJob()) || JobConstants.is閃雷悍將(player.getJob()))) {
                                    player.changeSingleSkillLevel(通用V核心.海盜通用.幸運骰子, player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));
                                }
                            }
                        }
                        Triple<Integer, Integer, Integer> vcoredata = ii.getVcores(vcoreskill.getType()).get(vcoreskill.getLevel());
                        total += vcoredata.right;
                    }
                }
                player.gainVCraftCore(total);
                player.send(VCorePacket.updateVCoreList(player, true, 5, 0));
                player.send(VCorePacket.addVCorePieceResult(total));
                break;
            }
            case 8: {
                final int vcoreoid = slea.readInt();
                final int nCount = slea.readInt();
                final VCoreDataEntry vcoredata = ii.getCoreData(vcoreoid);
                if (vcoredata == null) {
                    return;
                }
                final int type = vcoredata.getType();
                final int needCore = (type == 0 ? 140 : type == 1 ? 70 : 250) * nCount;
                if (player.isAdmin()) {
                    player.dropMessage(5, "製作V核心ID：" + vcoreoid + "(" + nCount + "個) ,需要：" + needCore + "個核心碎片。");
                }
                final String oneInfo = player.getOneInfo(1477, "count");
                if (oneInfo == null) {
                    return;
                }
                int count = Integer.valueOf(oneInfo);
                if (count >= needCore && player.gainVCoreSkill(vcoreoid, nCount, true)) {
                    player.updateOneInfo(1477, "count", String.valueOf(count - needCore));
                }
                break;
            }
            case 11: {
                final int index = slea.readInt();
                final VMatrixSlot slot = player.getVMatrixSlot().get(index);
                if (slot == null || slot.getExtend() >= VMatrixOption.EquipSlotEnhanceMax) {
                    c.sendEnableActions();
                    return;
                }
                if (player.getVMatrixPoint() > 0) {
//                    player.gainVCraftCore(-VMatrixOption.CraftEnchantCoreCost);
                    slot.setExtend(Math.min(slot.getExtend() + 1, VMatrixOption.EquipSlotEnhanceMax));
                    player.setVCoreSkillChanged(true);
                    for (VCoreSkillEntry vcoreskill : player.getVCoreSkill().values()) {
                        if (vcoreskill.getSlot() == 2 && vcoreskill.getIndex() == index) {
                            int extend = slot.getExtend();
                            for (int i = 1; i <= 3; ++i) {
                                if (vcoreskill.getSkill(i) > 0) {
                                    player.changeSingleSkillLevel(vcoreskill.getSkill(i), player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));

                                    if (vcoreskill.getSkill(i) == 通用V核心.海盜通用.滿載骰子 && (JobConstants.is傑諾(player.getJob()) || JobConstants.is隱月(player.getJob()) || JobConstants.is天使破壞者(player.getJob()) || JobConstants.is墨玄(player.getJob()) || JobConstants.is閃雷悍將(player.getJob()))) {
                                        player.changeSingleSkillLevel(通用V核心.海盜通用.幸運骰子, player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));
                                    }
                                }
                            }
                            break;
                        }
                    }
                } else {
                    player.dropMessage(5, "矩陣點數不足。");
                    player.dropAlertNotice("矩陣點數不足。");
                }
                player.send(VCorePacket.updateVCoreList(player, false, 0, 0));
                break;
            }
            case 12: {
                final int index = slea.readInt();
                slea.readInt();
                final int n26;
                if ((n26 = index - (player.getLevel() - 200) / 5 - VMatrixOption.EquipSlotMin) < 0 || n26 > 1 || index >= VMatrixOption.EquipSlotMax) {
                    break;
                }
                final VMatrixSlot slot = player.getVMatrixSlot().get(index);
                if (slot == null) {
                    c.sendEnableActions();
                    return;
                }
                final Map<Integer, Long> map = VMatrixOption.SlotExpansionMeso.get(player.getLevel());
                if (slot.getUnlock() != 1 && (map) != null && map.containsKey(n26)) {
                    player.gainMeso(-map.get(n26), false);
                    slot.setUnlock(1);
                    player.setVCoreSkillChanged(true);
                }
                c.announce(VCorePacket.updateVCoreList(player, false, 0, 0));
                break;
            }
            case 13: { //初始化
                if (player.getMeso() >= VMatrixOption.MatrixPointResetMeso) {
                    player.gainMeso(-VMatrixOption.MatrixPointResetMeso, false);
                    player.getVMatrixSlot().values().forEach(k1027 -> k1027.setExtend(0));
                    for (VCoreSkillEntry vcoreskill : player.getVCoreSkill().values()) {
                        if (vcoreskill.getSlot() == 2) {
                            for (int i = 1; i <= 3; ++i) {
                                if (vcoreskill.getSkill(i) > 0) {
                                    player.changeSingleSkillLevel(vcoreskill.getSkill(i), player.getVCoreSkillLevel(vcoreskill.getSkill(i)) > 0 ? player.getVCoreSkillLevel(vcoreskill.getSkill(i)) : -1, (byte) (vcoreskill.getType() == 2 ? 1 : 50));
                                    if (vcoreskill.getSkill(i) == 通用V核心.海盜通用.滿載骰子 && (JobConstants.is傑諾(player.getJob()) || JobConstants.is隱月(player.getJob()) || JobConstants.is天使破壞者(player.getJob()) || JobConstants.is墨玄(player.getJob()) || JobConstants.is閃雷悍將(player.getJob()))) {
                                        player.changeSingleSkillLevel(通用V核心.海盜通用.幸運骰子, player.getVCoreSkillLevel(vcoreskill.getSkill(i)), (byte) ((vcoreskill.getType() == 2) ? 1 : ((vcoreskill.getType() == 0) ? 25 : 50)));
                                    }
                                }
                            }
                        }
                    }
                    player.setVCoreSkillChanged(true);
                }
                c.announce(VCorePacket.updateVCoreList(player, false, 0, 0));
                break;
            }
            case 14: { //locking
                c.getPlayer().dropMessage(-5, "上鎖");
                break;
            }
            case 15: { // unlock
                c.getPlayer().dropMessage(-5, "解鎖");
                break;
            }
            default: {
                player.dropMessage(5, "[VMatrix] Unhandle OpCode：" + action);
                break;
            }
        }
    }

    public static void VmatrixHelpRequest(MaplePacketReader lea, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        player.openNpc(1540945, "VMatrixHelp");
    }

    public static void VmatrixVerify(MaplePacketReader lea, MapleClient c) {
        if (c == null || lea.available() < 6L) {
            return;
        }
        lea.readInt();
        String secondPwd = lea.readMapleAsciiString();
        c.announce(VCorePacket.showVCoreWindowVerifyResult(c.CheckSecondPassword(secondPwd)));
    }

    public static void MicroBuffEndTime(MaplePacketReader lea, MapleCharacter player) {
    }

    public static void UseActivateDamageSkin(MaplePacketReader lea, MapleCharacter player) {
        int skinId = lea.readInt();
        player.getMap().broadcastMessage(InventoryPacket.showDamageSkin(player.getId(), skinId), player.getPosition());
    }

    public static void UseActivateDamageSkinPremium(MaplePacketReader lea, MapleCharacter player) {
        lea.skip(3);
        short skinId = lea.readShort();
        player.getMap().broadcastMessage(InventoryPacket.showDamageSkin_Premium(player.getId(), skinId), player.getPosition());
    }

    public static void MultiSkillAttackRequest(MaplePacketReader lea, MapleClient c, MapleCharacter player) {
        MapleStatEffect effect;
        if (player == null || player.getMap() == null) {
            return;
        }
        int skillid = lea.readInt();
        int skilllv = lea.readInt();
        int unk = lea.readInt();
        byte skilltype = lea.readByte();
        int infolist = lea.readInt();
        DamageParse.calcAttackPosition(lea, player, null);
        int unk_2 = lea.readInt();
        int unk_3 = lea.readInt();
        short unk_4 = lea.readShort();
        lea.skip(1);
        lea.skip(1);
        lea.readByte();
        lea.readByte();
        lea.readByte();
        int length = lea.readShort();
        for (int x = 0; x < length; ++x) {
            lea.readByte();
        }
        lea.readInt();
        lea.readShort();
        lea.readShort();
        lea.readByte();
        lea.readShort();
        lea.readShort();
        lea.readInt();
        lea.readInt();
        if (63101004 == skillid || 63111003 == skillid) {
            MapleStatEffect effect2 = player.getSkillEffect(skillid);
            if (effect2 != null) {
                int maxValue = effect2.getW();
                int timeout = effect2.getU() * 1000;
                Pair skillInfo = (Pair)player.getTempValues().get("MultiSkill" + skillid);
                if (skillInfo != null) {
                    Pair pair = skillInfo;
                    pair.left = (Integer)pair.left - 1;
                    if ((Integer)skillInfo.left < 0) {
                        skillInfo.left = 0;
                    }
                } else {
                    player.dropMessage(5, "沒有準備好的崩壞爆破元件。");
                    return;
                }
                skillInfo.right = System.currentTimeMillis();
                player.getTempValues().put("MultiSkill" + skillid, skillInfo);
                player.send(MaplePacketCreator.multiSkillInfo(skillid, (Integer)skillInfo.left, maxValue, timeout));
            } else {
                return;
            }
        }
        if (!((effect = player.getSkillEffect(skillid)) == null || player.isSkillCooling(skillid) && skillid != 3311010 && skillid != 3321038 && skillid != 400031034)) {
            MapleStatEffect eff;
            switch (SkillConstants.getLinkedAttackSkill(skillid)) {
                case 3301003: 
                case 3301004: 
                case 3310001: 
                case 3320002: {
                    break;
                }
                default: {
                    effect.applyTo(player, skilllv);
                }
            }
            c.announce(MaplePacketCreator.VSkillObjectAction(skillid, skilllv, skilltype, infolist));
            if (JobConstants.is伊利恩(player.getJob()) && (eff = SkillFactory.getSkill(152000009).getEffect(Math.min(10, player.getBuffedIntValue(SecondaryStat.LefBuffMastery) + 1))) != null) {
                eff.applyTo(player, skilllv);
            }
        }
    }

    public static void UserGrowthHelperRequest(MaplePacketReader lea, MapleClient c, MapleCharacter player) {
        if (player == null || player.hasBlockedInventory() || player.getMap() == null || player.getMapId() == 993073000) {
            c.sendEnableActions();
            return;
        }
        lea.skip(2);
        int mapId = lea.readInt();
        if (mapId == 0) {
            c.sendEnableActions();
            return;
        }
        player.changeMap(ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapId));
        if (mapId == 102000003) {
            player.getScriptManager().startNpcScript(10202, 0, null);
        }
    }

    public static void DotHealHPMPRequest(MapleCharacter player) {
        SecondaryStatValueHolder mbsvh;
        if (!player.isAlive()) {
            return;
        }
        int value = player.getBuffedIntValue(SecondaryStat.DotHealHPPerSecond);
        if (value > 0 && player.getEffectForBuffStat(SecondaryStat.DotHealHPPerSecond) != null && player.getStat().getHp() < player.getStat().getCurrentMaxHP()) {
            player.healHP(player.getStat().getCurrentMaxHP() * value / 100);
        }
        if ((value = player.getBuffedIntValue(SecondaryStat.DotHealMPPerSecond)) > 0 && player.getEffectForBuffStat(SecondaryStat.DotHealMPPerSecond) != null && player.getStat().getMp() < player.getStat().getCurrentMaxMP()) {
            player.healMP(player.getStat().getCurrentMaxMP() * value / 100);
        }
        if (JobConstants.is主教(player.getJob()) && (mbsvh = player.getBuffStatValueHolder(SecondaryStat.BishopPray)) != null) {
            int healR = 1;
            int intValue = player.getStat().getTotalInt();
            if (intValue >= mbsvh.effect.getY()) {
                healR = Math.max(healR + mbsvh.effect.getY() / intValue, mbsvh.effect.getZ());
            }
            for (MapleCharacter tchr : player.getMap().getCharactersInRect(mbsvh.effect.calculateBoundingBox(player.getPosition()))) {
                if (tchr == null || tchr == player || tchr.getParty() != player.getParty()) continue;
                if (tchr.getStat().getHp() < tchr.getStat().getCurrentMaxHP()) {
                    tchr.addHPMP(healR, healR);
                }
                LinkedList<SecondaryStatValueHolder> mbsvhs = new LinkedList();
                for (Map.Entry<SecondaryStat, List<SecondaryStatValueHolder>> entry : tchr.getAllEffects().entrySet()) {
                    if (!entry.getKey().isNormalDebuff() && !entry.getKey().isCriticalDebuff()) continue;
                    entry.getValue().stream().filter(mb -> mb.effect instanceof MobSkill).forEach(mbsvhs::add);
                }
                if (mbsvhs.size() <= 0) continue;
                mbsvhs.forEach(mb -> tchr.cancelEffect(mb.effect, mb.startTime));
            }
        }
    }

    public static void UserHowlingStormStack(MaplePacketReader lea, MapleCharacter player) {
        MapleStatEffect effect;
        if (JobConstants.is破風使者(player.getJob()) && (effect = SkillFactory.getSkill(400031003).getEffect(player.getTotalSkillLevel(400031003))) != null) {
            effect.applyTo(player, true);
        }
    }

    public static void UserJudgement(MaplePacketReader lea, MapleCharacter player) {
        if (JobConstants.is幻影俠盜(player.getJob())) {
            int maxJS;
            MapleStatEffect eff = player.getSkillEffect(20031210);
            int n = maxJS = player.getSkillEffect(24100003) == null ? 20 : 40;
            if (eff == null) {
                eff = player.getSkillEffect(20031209);
            }
            if (player.getJudgementStack() >= maxJS && eff.applyTo(player)) {
                player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(player, eff, 0)), true);
                player.setJudgementStack(0);
                player.getClient().announce(MaplePacketCreator.updateCardStack(0));
            }
        }
    }

    public static void MultiSkillChargeRequest(MaplePacketReader lea, MapleCharacter player) {
        int skillid = lea.readInt();
        MapleStatEffect effect = player.getSkillEffect(skillid);
        if (effect != null) {
            SecondaryStatValueHolder mbsvh = null;
            SecondaryStat stat = null;
            if (effect.getStatups().containsKey(SecondaryStat.CannonShooter_BFCannonBall) && (mbsvh = player.getBuffStatValueHolder(SecondaryStat.CannonShooter_BFCannonBall)) != null) {
                stat = SecondaryStat.CannonShooter_BFCannonBall;
            } else if (effect.getStatups().containsKey(SecondaryStat.CannonShooter_MiniCannonBall) && (mbsvh = player.getBuffStatValueHolder(SecondaryStat.CannonShooter_MiniCannonBall)) != null) {
                stat = SecondaryStat.CannonShooter_MiniCannonBall;
            }
            if (mbsvh != null && stat != null) {
                int value = mbsvh.value + 1;
                int t = effect.getT() * 1000;
                int maxValue = effect.getY();
                if (value < 0 || System.currentTimeMillis() < mbsvh.startTime + (long)t || value > maxValue) {
                    return;
                }
                mbsvh.value = value;
                mbsvh.startTime = System.currentTimeMillis();
                player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(stat, mbsvh.effect.getSourceId())));
            } else {
                switch (skillid) {
                    case 3311002: 
                    case 3321006: {
                        effect.unprimaryPassiveApplyTo(player);
                        break;
                    }
                    default: {
                        effect.applyTo(player, true);
                    }
                }
            }
        }
    }

    public static void MultiSkillTimeoutChargeRequest(MaplePacketReader lea, MapleCharacter player) {
        int skillid = lea.readInt();
        MapleStatEffect effect = player.getSkillEffect(skillid);
        if (effect != null) {
            int value = 0;
            int maxValue = effect.getW();
            int timeout = effect.getU() * 1000;
            Pair<Integer, Long> skillInfo = (Pair<Integer, Long>)player.getTempValues().get("MultiSkill" + skillid);
            if (skillInfo != null) {
                value = (Integer)skillInfo.left + 1;
                if (value < 0 || System.currentTimeMillis() < (Long)skillInfo.right + (long)timeout || value > maxValue) {
                    return;
                }
                skillInfo.left = value;
                skillInfo.right = System.currentTimeMillis();
            } else {
                skillInfo = new Pair<Integer, Long>(0, System.currentTimeMillis());
            }
            player.getTempValues().put("MultiSkill" + skillid, skillInfo);
            player.send(MaplePacketCreator.multiSkillInfo(skillid, value, maxValue, timeout));
        }
    }

    public static void UserTrumpSkillActionRequest(MaplePacketReader lea, MapleCharacter player) {
        block1: {
            Iterator<MapleMapObject> iterator;
            if (player == null || player.getMap() == null || !JobConstants.is幻影俠盜(player.getJob())) {
                return;
            }
            MapleStatEffect effect = player.getSkillEffect(400041009);
            if (effect == null || !(iterator = player.getMap().getMapObjectsInRect(effect.calculateBoundingBox(player.getPosition(), player.isFacingLeft(), 250), Collections.singletonList(MapleMapObjectType.MONSTER)).iterator()).hasNext()) break block1;
            MapleMapObject mapleMapObject = iterator.next();
            player.handleCarteGain(mapleMapObject.getObjectId(), true);
        }
    }

    public static void ChargeInfiniteFlame(MaplePacketReader lea, MapleClient c) {
        Skill skill = SkillFactory.getSkill(400021072);
        if (c.getPlayer().getTotalSkillLevel(skill) > 0) {
            int currentCharge = lea.readInt();
            c.announce(BuffPacket.setInfinitiFlameCharge(c.getPlayer(), currentCharge));
        }
    }

    public static void ChargePrimalGrenade(MaplePacketReader lea, MapleClient c) {
        Skill skill = SkillFactory.getSkill(400031032);
        if (skill != null && c.getPlayer().getTotalSkillLevel(skill) > 0) {
            lea.readInt();
            int currentCharge = lea.readInt();
            MapleStatEffect effect = skill.getEffect(c.getPlayer().getTotalSkillLevel(skill));
            if (effect != null) {
                effect.applyTo(c.getPlayer(), true);
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeOpcode(OutHeader.ENABLE_PRIMAL_GRENADE_CHARGE);
                mplew.write(1);
                c.announce(mplew.getPacket());
            }
        }
    }

    public static void MaliceChargeRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int skillid = slea.readInt();
        MapleStatEffect effect = player.getSkillEffect(63120000);
        if (effect == null) {
            effect = player.getSkillEffect(63101001);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.MaliceChargeResult);
        mplew.writeInt(skillid);
        mplew.writeInt(1);
        mplew.writeInt(3);
        mplew.writeInt(6000);
        mplew.write(1);
        c.announce(mplew.getPacket());
    }

    public static void LaraSkillChargeRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int skillId = slea.readInt();
        slea.readInt();
        MapleStatEffect effect = player.getSkillEffect(skillId);
        if (effect != null) {
            int value;
            boolean bufft;
            int maxValue;
            SecondaryStat buff;
            switch (skillId) {
                case 162101012: {
                    buff = SecondaryStat.AMPlanting;
                    int subTime = effect.getZ() * 1000;
                    maxValue = effect.getW2();
                    bufft = false;
                    break;
                }
                case 162111006: {
                    buff = SecondaryStat.AMEVTeleport;
                    int subTime = effect.getS2() * 1000;
                    maxValue = effect.getV();
                    bufft = true;
                    break;
                }
                case 162121042: {
                    buff = SecondaryStat.AMArtificialEarthVein;
                    int subTime = effect.getW() * 1000;
                    maxValue = effect.getU();
                    bufft = false;
                    break;
                }
                default: {
                    return;
                }
            }
            SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(buff);
            if (mbsvh == null) {
                value = 1;
                if (bufft) {
                    effect.applyTo(player, null, true);
                } else {
                    effect.unprimaryPassiveApplyTo(player);
                }
            } else {
                mbsvh.value = value = Math.min(Math.max(mbsvh.value + 1, 0), maxValue);
                mbsvh.startTime = System.currentTimeMillis();
                player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(buff, mbsvh.effect.getSourceId())));
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.LaraSkillChargeResult.getValue());
            mplew.writeInt(skillId);
            mplew.write(value);
            player.send(mplew.getPacket());
        }
    }

    public static void GameExit(MapleClient c) {
        c.getPlayer().getScriptManager().dispose();
    }

    public static void CP_OPEN_UNION_UI_REQUEST(MapleCharacter chr) {
        chr.checkMapleUnion(false);
    }

    public static void handleMapleUnion(MaplePacketReader slea, MapleCharacter chr) {
        slea.skip(41);
        int unionSize = slea.readInt();
        Map<Integer, MapleUnionEntry> fightingUnions = chr.getMapleUnion().getFightingUnions();
        fightingUnions.clear();
        chr.setMapleUnionChanged(true);
        for (int x = 0; x < unionSize; ++x) {
            int type = slea.readInt();
            int chrid = slea.readInt();
            int level = slea.readInt();
            int job = slea.readInt();
            int DummyUnk = slea.readInt();
            int rotate = slea.readInt();
            int boardIndex = slea.readInt();
            int local = slea.readInt();
            int unk264 = slea.readInt();
            int unk264_2 = slea.readInt();
            String name = slea.readMapleAsciiString();
            MapleUnionEntry union = new MapleUnionEntry(chrid, name, level, job);
            union.setType(type);
            union.setRotate(rotate);
            union.setBoardIndex(boardIndex);
            union.setLocal(local);
            fightingUnions.put(chrid, union);
        }
        chr.getMapleUnion().update();
        chr.send(MaplePacketCreator.updateMapleUnion(chr.getMapleUnion()));
    }

    public static void handleRemoteControlDice(MaplePacketReader slea, MapleCharacter chr) {
        MapleStatEffect effect;
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int dice = slea.readInt();
        if (!chr.isSkillCooling(400051000) && (effect = chr.getSkillEffect(400051000)) != null) {
            chr.getSpecialStat().setRemoteDice(dice);
            effect.applyTo(chr);
        }
    }

    public static void GhostArrowHandler(MaplePacketReader slea, MapleCharacter player) {
        MapleStatEffect effect;
        if (player == null || player.getMap() == null) {
            return;
        }
        int mobOid = slea.readInt();
        slea.readInt();
        if (player.getMap().getMobObject(mobOid) != null && (effect = player.getSkillEffect(400031020)) != null) {
            player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(player, effect, 0, mobOid, Collections.emptyList(), player.getPosition())), true);
        }
    }

    public static void PassiveSkillInfoUpdate(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        MapleStatEffect effect;
        if (player == null) {
            return;
        }
        player.updateTick(slea.readInt());
        slea.readInt();
        slea.readByte();
        player.getStat().recalcLocalStats(false, player);
        Pair<Integer, Integer> pair = player.getStat().getEquipSummon();
        if ((Integer)pair.left > 0 && (effect = SkillFactory.getSkill((Integer)pair.left).getEffect(1)) != null) {
            if ((Integer)pair.right > 0) {
                effect.applyTo(player, null, true);
                return;
            }
            player.dispelEffect((Integer)pair.left);
            int n = SkillConstants.eM((Integer)pair.left);
            if (n > 0) {
                player.dispelEffect(n);
            }
        }
    }

    public static void VAddSkillAttackRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        slea.readInt();
        c.announce(MaplePacketCreator.userBonusAttackRequest(40001000, 0, Collections.emptyList()));
    }

    public static void OverloadModeResult(MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        MapleStatEffect effect = player.getEffectForBuffStat(SecondaryStat.OverloadMode);
        if (effect != null) {
            int mpcost = player.getStat().getCurrentMaxMP() * effect.getQ() / 100 + effect.getY();
            if (player.getStat().getMp() < mpcost) {
                player.dispelEffect(SecondaryStat.OverloadMode);
                if (player.getBuffedIntValue(SecondaryStat.SurplusSupply) > 20) {
                    player.getSkillEffect(30020232).unprimaryPassiveApplyTo(player);
                }
                return;
            }
            player.addHPMP(0, -mpcost, false, false);
        }
    }

    public static void RequestSetHpBaseDamage(MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        MapleStatEffect effect = player.getSkillEffect(30010242);
        if (effect != null) {
            effect.unprimaryPassiveApplyTo(player);
        }
    }

    public static void UserForceAtomCollision(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int forceCount = slea.readInt();
        int forceSkillId = slea.readInt();
        int len = slea.readInt();
        for (int i = 0; i < len; ++i) {
            int skillid = slea.readInt();
            int attackCount = slea.readInt();
            int x = slea.readInt();
            int n = slea.readInt();
        }
        MapleStatEffect effect = player.getSkillEffect(forceSkillId);
        MapleMap map = player.getMap();
        MapleForceFactory mff = MapleForceFactory.getInstance();
        block14: for (int i = 0; i < forceCount; ++i) {
            int attackCount = forceCount;
            slea.readInt();
            int key = attackCount % 100;
            boolean b = slea.readByte() != 0;
            int oid = slea.readInt();
            slea.readInt();
            int oid2 = slea.readInt();
            slea.readInt();
            slea.readInt();
            slea.readByte();
            int skillID2 = slea.readInt();
            if (forceSkillId == 400021069) {
                slea.readInt();
            }
            if (player.isDebug()) {
                player.dropSpouseMessage(UserChatMessageType.聯盟群組, "[Force Atom] Now Count " + key);
            }
            MapleMonster mob = map.getMonsterByOid(oid);
            switch (forceSkillId) {
                case 0: {
                    if (skillID2 == 164001001 || (mob = map.getMonsterByOid(oid2)) == null) continue block14;
                    if (player.getSkillEffect(152120013) != null) {
                        effect = player.getSkillEffect(152120013);
                    } else if (player.getSkillEffect(152110010) != null) {
                        effect = player.getSkillEffect(152110010);
                    } else if (player.getSkillEffect(152100012) != null) {
                        effect = player.getSkillEffect(152100012);
                    }
                    if (effect == null || (effect = SkillFactory.getSkill(152000010).getEffect(effect.getLevel())) == null) continue block14;
                    effect.applyMonsterEffect(player, mob, effect.getMobDebuffDuration(player));
                    continue block14;
                }
                case 14000028: {
                    List<MapleMapObject> objects;
                    if (mob == null || key >= effect.getMobCount(player) || (objects = map.getMapObjectsInRange(mob.getPosition(), 633.0, Collections.singletonList(MapleMapObjectType.MONSTER))).isEmpty()) continue block14;
                    if (key == 1) {
                        player.addHPMP(1, 0);
                    }
                    MapleForceAtom force = mff.getMapleForce(player, effect, key + 1, oid);
                    force.setForcedTarget(mob.getPosition());
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(force), true);
                    continue block14;
                }
                case 65111007: {
                    if (mob == null || effect == null || effect.getSourceId() != 65111007 || key >= 8 || !Randomizer.isSuccess(player.getSkillEffect(65111100).getS())) continue block14;
                    MapleForceAtom force = mff.getMapleForce(player, effect, key + 1, oid);
                    force.setForcedTarget(mob.getPosition());
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(force), true);
                    continue block14;
                }
                case 25100010: 
                case 25120115: 
                case 31221014: 
                case 400021044: {
                    int ef = effect.getZ();
                    if (forceSkillId == 31221014 && player.getSkillEffect(31220050) != null) {
                        ef += 2;
                    }
                    if (mob == null || key >= ef || map.getMapObjectsInRange(mob.getPosition(), 633.0, Collections.singletonList(MapleMapObjectType.MONSTER)).isEmpty()) continue block14;
                    MapleForceAtom force = mff.getMapleForce(player, effect, key + 1, oid);
                    force.setForcedTarget(mob.getPosition());
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(force), true);
                    continue block14;
                }
                case 400011058: 
                case 400011059: {
                    if (mob == null) continue block14;
                    player.getSkillEffect(forceSkillId == 400011058 ? 400011060 : 400011061).applyAffectedArea(player, mob.getPosition());
                    continue block14;
                }
                case 400041023: {
                    boolean release = slea.readByte() == 1;
                    int mid = slea.readInt();
                    int x2 = slea.readInt();
                    int y2 = slea.readInt();
                    if (mob == null) continue block14;
                    if (release) {
                        c.announce(MaplePacketCreator.LiftSkillAction(400041080, player.getSkillLevel(400041022), 1, mob.getPosition().x, mob.getPosition().y));
                        continue block14;
                    }
                    if (Boolean.parseBoolean(player.getTempValues().get("useBlackJack").toString())) {
                        player.incAtomAttackRecord(400041080);
                        if (player.getAtomAttackRecord(400041080) >= 6) continue block14;
                        continue block14;
                    }
                    player.incAtomAttackRecord(400041022);
                    if (player.getAtomAttackRecord(400041022) <= player.getSkillEffect(400041022).getZ() && oid != 0) {
                        MapleForceAtom mfa = mff.getMapleForce(player, effect, oid, Collections.emptyList(), mob.getPosition());
                        mfa.setSkillId(400041023);
                        mfa.setOwnerId(player.getId());
                        mfa.setFirstMobID(oid);
                        player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mfa), true);
                        continue block14;
                    }
                    c.announce(MaplePacketCreator.LiftSkillAction(400041024, player.getSkillLevel(400041022), 1, mob.getPosition().x, mob.getPosition().y));
                    continue block14;
                }
                case 155100009: {
                    MapleStatEffect skillEffect2;
                    MapleMonster mobObject3 = map.getMobObject(oid2);
                    if (mobObject3 == null || (skillEffect2 = player.getSkillEffect(155111207)) == null || player.getEvanWreckages().size() >= skillEffect2.getZ() || !skillEffect2.makeChanceResult(player)) continue block14;
                    int addWreckages = player.addWreckages(new Point(mobObject3.getPosition()), skillEffect2.getDuration());
                    player.getMap().broadcastMessage(player, EffectPacket.DragonWreckage(player.getId(), mobObject3.getPosition(), skillEffect2.getDuration() / 1000, addWreckages, skillEffect2.getSourceId(), 0, player.getEvanWreckages().size()), true);
                    continue block14;
                }
                case 155121003: {
                    MapleStatEffect skillEffect3;
                    if (map.getMobObject(oid2) == null || (skillEffect3 = player.getSkillEffect(155121005)) == null) continue block14;
                    skillEffect3.unprimaryPassiveApplyTo(player);
                    continue block14;
                }
                case 155111003: {
                    MapleStatEffect skillEffect4;
                    if (map.getMobObject(oid2) == null || (skillEffect4 = player.getSkillEffect(155111005)) == null) continue block14;
                    skillEffect4.unprimaryPassiveApplyTo(player);
                    continue block14;
                }
                case 155101002: {
                    MapleStatEffect skillEffect5;
                    if (map.getMobObject(oid2) == null || (skillEffect5 = player.getSkillEffect(155101003)) == null) continue block14;
                    skillEffect5.unprimaryPassiveApplyTo(player);
                    continue block14;
                }
                case 155001000: {
                    MapleStatEffect skillEffect6;
                    if (map.getMobObject(oid2) == null || (skillEffect6 = player.getSkillEffect(155001001)) == null) continue block14;
                    skillEffect6.unprimaryPassiveApplyTo(player);
                    continue block14;
                }
            }
        }
    }

    public static void UserSaveDamageSkin(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        byte action = slea.readByte();
        switch (action) {
            case 0: {
                String count;
                String customData = player.getQuestNAdd(MapleQuest.getInstance(7291)).getCustomData();
                if (customData == null) {
                    MapleQuest.getInstance(7291).forceStart(player, 0, String.valueOf(0));
                    customData = player.getQuestNAdd(MapleQuest.getInstance(7291)).getCustomData();
                }
                int maxSize = (count = player.getOneInfo(56829, "count")) == null ? ServerConfig.defaultDamageSkinSlot : Integer.valueOf(count);
                List<Integer> list = player.getDamSkinList();
                if (customData == null || list.size() >= maxSize || list.contains(Integer.valueOf(customData))) break;
                player.getDamSkinList().add(0, Integer.valueOf(customData));
                StringBuilder sb = new StringBuilder();
                player.getDamSkinList().forEach(n -> sb.append(n).append(","));
                player.setKeyValue("DAMAGE_SKIN", sb.substring(0, sb.toString().length() - 1));
                c.announce(InventoryPacket.UserDamageSkinSaveResult(4, 0, player));
                c.announce(InventoryPacket.UserDamageSkinSaveResult(2, 0, player));
                break;
            }
            case 1: {
                short index = slea.readShort();
                short skinId = slea.readShort();
                if (!player.getDamSkinList().contains(skinId)) break;
                player.getDamSkinList().remove((Object)skinId);
                StringBuilder sb = new StringBuilder();
                player.getDamSkinList().forEach(n -> sb.append(n).append(","));
                player.setKeyValue("DAMAGE_SKIN", sb.substring(0, sb.toString().length() - 1));
                c.announce(InventoryPacket.UserDamageSkinSaveResult(4, 0, player));
                c.announce(InventoryPacket.UserDamageSkinSaveResult(1, 0, player));
                break;
            }
            case 2: {
                short index = slea.readShort();
                short skinId = slea.readShort();
                if (!player.getDamSkinList().contains(skinId)) {
                    player.dropMessage(11, "傷害皮膚應用出錯!你並沒有這個傷害皮膚!");
                    break;
                }
                player.changeDamageSkin(skinId);
                MapleQuest.getInstance(7291).forceStart(player, 0, String.valueOf(skinId));
                player.send(InventoryPacket.UserDamageSkinSaveResult(4, 0, player));
                player.send(InventoryPacket.UserDamageSkinSaveResult(2, 0, player));
                player.send(InventoryPacket.showDamageSkin(player.getId(), skinId));
                break;
            }
            default: {
                player.dropMessage(5, "Unhandled UserSaveDamageSkin Action");
            }
        }
    }

    public static void handleUserCustomSaveRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        int backgroundId = slea.readInt();
        if (backgroundId > 6 || backgroundId < 0) {
            return;
        }
        MapleQuest.getInstance(7295).forceStart(player, 0, String.valueOf(backgroundId));
    }

    public static void DimensionMirrorMove(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        player.updateTick(slea.readInt());
        int id = slea.readInt();
        for (DimensionMirrorEvent event : DimensionMirrorEvent.values()) {
            if (event.getID() != id || event.getMapID() <= 0 || player.getLevel() < event.getLimitLevel()) continue;
            player.saveLocation(SavedLocationType.MULUNG_TC);
            player.changeMap(event.getMapID(), 0);
            return;
        }
    }

    public static void DevilFrenzyResult(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        MapleStatEffect effect = player.getEffectForBuffStat(SecondaryStat.Frenzy);
        if (effect != null) {
            int cost = effect.getY();
            player.getBuffedIntZ(SecondaryStat.Frenzy);
            if (player.getStat().getHp() > cost && player.getStat().getHPPercent() >= 2) {
                if (player.getBuffedValue(SecondaryStat.Thaw) == null) {
                    player.addHPMP(-cost, 0, false, false);
                }
                effect.unprimaryPassiveApplyTo(player);
                player.getSkillEffect(400010010).applyAffectedArea(player, null);
            }
        }
    }

    public static void UserChangeSoulCollectionRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int id = slea.readInt();
        short slot = slea.readShort();
        SoulCollectionEntry soul = ii.getSoulCollection(id);
        Item item = player.getInventory(MapleInventoryType.USE).getItem(slot);
        if (soul == null || item == null || item.getQuantity() <= 0) {
            c.sendEnableActions();
            return;
        }
        int itemId = item.getItemId();
        if (soul.getItems().containsKey(itemId)) {
            int n = (int)Math.pow(2.0, soul.getItems().get(itemId).intValue());
            player.getSoulCollection().merge(id, n, (a, b) -> a | b);
            player.setSoulCollectionChanged(true);
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, true, false);
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeOpcode(OutHeader.LP_ChangeSoulCollectionResult);
            mplew.writeInt(id);
            mplew.writeInt(player.getSoulCollection().get(id));
            c.announce(mplew.getPacket());
        }
        c.sendEnableActions();
    }

    public static void ComboCheckRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int quest = slea.readInt();
        int value = slea.readInt();
        slea.readInt();
        player.updateOneQuestInfo(quest, "Total_Kill:", String.valueOf(value));
    }

    public static void JobFreeChangeRequest(MaplePacketReader slea, MapleCharacter player) {
        int jobId = slea.readInt();
        if (!JobConstants.is冒險家(player.getJob()) || !JobConstants.is冒險家(jobId) || jobId / 100 != player.getJob() / 100) {
            return;
        }
        byte unknown = slea.readByte();
        player.freeJobChange(jobId);
    }

    public static void ErosionsrReduce(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || !JobConstants.is亞克(player.getJob())) {
            return;
        }
        if (player.getBuffedValue(SecondaryStat.SpecterMode) != null && player.getBuffSource(SecondaryStat.IndieNotDamaged) != 400051334) {
            player.addErosions(-19);
            if (player.getErosions() <= 0) {
                player.dispelEffect(SecondaryStat.SpecterMode);
                player.registerSkillCooldown(player.getSkillEffect(155001008), true);
            }
        }
        int ArkGauge = slea.readInt();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_TemporaryStatSet.getValue());
        mplew.writeZeroBytes(73);
        mplew.writeInt(32);
        mplew.writeZeroBytes(55);
        mplew.writeShort(1);
        mplew.writeInt(15512);
        mplew.writeZeroBytes(13);
        mplew.writeInt(ArkGauge + 13);
        mplew.writeZeroBytes(5);
        mplew.write(1);
        mplew.write(1);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(5);
        c.getPlayer().send(mplew.getPacket());
    }

    public static void SelflessState(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        slea.readInt();
        MapleStatEffect effect = player.getSkillEffect(slea.readInt());
        if (effect != null) {
            effect.applyTo(player);
        }
    }

    public static void ReqMakingSkillEff(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int skillId = slea.readInt();
        int n = skillId / 10000 % 10;
        if (player.getSkillLevel(skillId) >> 24 >= 12) {
            if (player.getMeisterSkillEff() > 0) {
                player.updateOneQuestInfo(25948, "E", "0");
            } else {
                player.updateOneQuestInfo(25948, "E", String.valueOf(n));
            }
            player.updateOneQuestInfo(25948, "E", player.getMeisterSkillEff() > 0 ? String.valueOf(n) : "0");
        } else {
            player.updateOneQuestInfo(25948, "E", "0");
        }
        if (player.getMap().getRunesSize() >= 0) {
            c.getPlayer().getMap().showRuneCurseStage();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_UserMakingMeisterSkillEff);
        mplew.writeInt(player.getId());
        mplew.writeInt(player.getMeisterSkillEff());
        player.getMap().broadcastMessage(player, mplew.getPacket(), true);
    }

    public static void UserSetCustomizeEffect(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int skillId = slea.readInt();
        int n2 = 0;
        slea.readInt();
        MapleQuest quest = MapleQuest.getInstance(7292);
        String custom = player.getQuestNAdd(quest).getCustomData();
        if (skillId == 0 && custom != null) {
            n2 = skillId = Integer.valueOf(custom).intValue();
        }
        switch (skillId) {
            case 3110003: {
                if (n2 != 0) {
                    quest.forceStart(player, 0, "0");
                    player.changeSingleSkillLevel(80011504, -1, -1);
                    return;
                }
                quest.forceStart(player, 0, String.valueOf(skillId));
                player.changeSingleSkillLevel(80011504, 1, 0);
            }
            case 3110004: {
                if (n2 != 0) {
                    quest.forceStart(player, 0, "0");
                    player.changeSingleSkillLevel(80000269, -1, -1);
                    return;
                }
                quest.forceStart(player, 0, String.valueOf(skillId));
                player.changeSingleSkillLevel(80000269, 1, 0);
                break;
            }
            case 14110032: {
                quest.forceStart(player, 0, "0");
                player.changeSingleSkillLevel(14110032, 1, 1);
            }
        }
    }

    public static void TowerRankRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        List<MapleLobbyRank> lobbyRanks = SqlTool.queryAndGetList("SELECT * FROM `zrank_lobby` WHERE `world` = ? ORDER BY `stage` DESC, `time` ASC LIMIT 50", new LobbyRankMapper(), new Object[]{player.getWorld()});
        ArrayList<MapleLobbyRank> list = new ArrayList<MapleLobbyRank>();
        for (MapleLobbyRank rank : lobbyRanks) {
            Calendar instance = Calendar.getInstance();
            Calendar instance2 = Calendar.getInstance();
            instance.setFirstDayOfWeek(2);
            instance2.setFirstDayOfWeek(2);
            instance.setTimeInMillis(System.currentTimeMillis());
            instance2.setTimeInMillis(rank.logtime);
            int n = instance.get(1) - instance2.get(1);
            if (n == 0 || n == 1 && instance2.get(2) == 11) {
                if (instance.get(3) != instance2.get(3)) continue;
                list.add(rank);
                continue;
            }
            if (n != -1 || instance.get(2) != 11 || instance.get(3) != instance2.get(3)) continue;
            list.add(rank);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_TopTowerRankResult);
        mplew.writeInt(list.size());
        for (MapleLobbyRank rank : list) {
            mplew.writeInt(rank.playerID);
            mplew.writeInt(rank.world);
            mplew.writeAsciiString(rank.playerName, 15);
            mplew.writeInt(rank.stage);
            mplew.writeLong(rank.time);
            PacketHelper.addExpirationTime(mplew, rank.logtime);
        }
        c.announce(mplew.getPacket());
    }

    public static void UserCalcDamageStatSetRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null) {
            return;
        }
        player.getStat().recalcLocalStats(false, player);
    }

    public static void UserCharacterPotentialRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        player.updateTick(slea.readInt());
        if (slea.readByte() == 1) {
            for (InnerSkillEntry ise : player.getTempInnerSkills()) {
                player.changeInnerSkill(c.getPlayer(), (byte)0, ise);
            }
        }
        player.getTempInnerSkills().clear();
    }

    public static void UserTemporaryStatUpdateRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        MapleStatEffect effecForBuffStat = player.getEffectForBuffStat(SecondaryStat.PickPocket);
        if (effecForBuffStat != null) {
            effecForBuffStat.unprimaryPassiveApplyTo(player);
        }
        if (JobConstants.is爆拳槍神(player.getJob())) {
            player.dispelEffect(SecondaryStat.RWMovingEvar);
        }
    }

    public static void CrystalCharge(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || !JobConstants.is伊利恩(player.getJob())) {
            return;
        }
        int skillId = slea.readInt();
        MapleSummon summon = player.getSummonBySkillID(152101000);
        if (summon == null) {
            return;
        }
        int n = 0;
        if (player.getSkillEffect(152120014) != null) {
            n = 150;
        } else if (player.getSkillEffect(152110008) != null) {
            n = 150;
        } else if (player.getSkillEffect(152100010) != null) {
            n = 30;
        }
        int acState1 = summon.getAcState1();
        int acState2 = summon.getAcState2();
        int n2 = 1;
        switch (skillId) {
            case 152001002: 
            case 152120003: {
                n2 = 2;
            }
        }
        if (player.getBuffedValue(SecondaryStat.LefFastCharge) != null) {
            n2 <<= 1;
        }
        summon.setAcState1(Math.min(acState1 + n2, n));
        if (summon.getAcState1() >= 0 && summon.getAcState1() < 30) {
            summon.setAcState2(0);
        } else if (summon.getAcState1() >= 30 && summon.getAcState1() < 60) {
            summon.setAcState2(1);
            if (acState2 != 1) {
                c.announce(SummonPacket.SummonedSkillState(summon, 2));
            }
        } else if (summon.getAcState1() >= 60 && summon.getAcState1() < 90) {
            summon.setAcState2(2);
            if (acState2 != 2) {
                c.announce(SummonPacket.SummonedSkillState(summon, 2));
            }
        } else if (summon.getAcState1() >= 90 && summon.getAcState1() < 150) {
            summon.setAcState2(3);
            if (acState2 != 3) {
                c.announce(SummonPacket.SummonedSkillState(summon, 2));
            }
        } else if (summon.getAcState1() >= 150) {
            MapleStatEffect g;
            summon.setAcState2(4);
            if (acState2 != 4) {
                c.announce(SummonPacket.SummonedSkillState(summon, 2));
            }
            if ((g = SkillFactory.getSkill(152120014).getEffect(1)) != null) {
                g.applyTo(player);
            }
        }
        player.getMap().broadcastMessage(player, SummonPacket.SummonedStateChange(summon, 2, summon.getAcState1(), summon.getAcState2()), true);
        player.getMap().broadcastMessage(player, SummonPacket.SummonedSpecialEffect(summon, 2), true);
        player.getMap().broadcastMessage(player, SummonPacket.SummonedStateChange(summon, 3, 0, 0), true);
    }

    public static void RunScript(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        short script = slea.readShort();
        int mapId = slea.readInt();
        boolean npc = false;
        player.getScriptManager().startScript(9900000, "RunScript_" + script, ScriptType.Npc);
    }

    public static void MyHomeRunScript(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        short scriptCode = slea.readShort();
        player.getScriptManager().startNpcScript(9900000, 0, "custom_script_" + scriptCode);
    }

    public static void CTX_UNION_PRESET_REQUEST(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        if (player.getMapleUnion().getFightingUnions().size() < 1) {
            c.announce(PlayerHandler.getNullInFight());
            return;
        }
        c.announce(PlayerHandler.getNullInFight());
        int idx = 0;
        c.announce(MaplePacketCreator.MapleUnionPresetResult(idx, player.getMapleUnion()));
        c.getPlayer().getWorldShareInfo(500629);
    }

    public static byte[] getNullInFight() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.MapleUnionPresetResult.getValue());
        mplew.writeInt(1);
        mplew.writeInt(256);
        mplew.writeInt(512);
        mplew.writeInt(768);
        mplew.writeInt(1024);
        mplew.writeInt(1280);
        mplew.writeInt(1536);
        mplew.writeInt(1792);
        mplew.writeInt(0);
        mplew.writeInt(256);
        mplew.writeInt(65536);
        mplew.writeInt(131072);
        mplew.writeInt(196608);
        mplew.writeInt(262144);
        mplew.writeInt(327680);
        mplew.writeInt(393216);
        mplew.writeInt(458752);
        mplew.writeInt(0);
        mplew.writeInt(65536);
        mplew.writeInt(0x1000000);
        mplew.writeInt(0x2000000);
        mplew.writeInt(0x3000000);
        mplew.writeInt(0x4000000);
        mplew.writeInt(0x5000000);
        mplew.writeInt(0x6000000);
        mplew.writeInt(0x7000000);
        mplew.writeInt(0);
        mplew.writeInt(0x1000000);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(2);
        mplew.writeInt(3);
        mplew.writeInt(4);
        mplew.writeInt(5);
        mplew.writeInt(6);
        mplew.writeInt(7);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(256);
        mplew.writeInt(512);
        mplew.writeInt(768);
        mplew.writeInt(1024);
        mplew.writeInt(1280);
        mplew.writeInt(1536);
        mplew.writeInt(1792);
        mplew.writeInt(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static void AvatarEffectSkillOnOff(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        SecondaryStatValueHolder mbsvh;
        int skillId = slea.readInt();
        if (c.getPlayer().getSkillEffect(skillId) == null) {
            return;
        }
        String statData = c.getPlayer().getOneInfo(1544, String.valueOf(skillId));
        statData = statData == null || statData.equals("0") ? String.valueOf(1) : String.valueOf(0);
        c.getPlayer().updateOneInfo(1544, String.valueOf(skillId), statData, true);
        SecondaryStat stat = null;
        if (skillId == 1101013) {
            stat = SecondaryStat.ComboCounter;
        }
        if (stat != null && (mbsvh = player.getBuffStatValueHolder(stat)) != null) {
            player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(stat, mbsvh.effect.getSourceId())));
        }
    }

    public static void handleSkillOnOff(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        SecondaryStatValueHolder holder;
        int skillId = slea.readInt();
        String skillTag = null;
        int questID = 21770;
        switch (skillId) {
            case 21001012: {
                skillTag = "2";
                break;
            }
            case 21001013: {
                skillTag = "3";
                break;
            }
            case 21111017: {
                skillTag = "5";
                break;
            }
            case 21121029: {
                skillTag = "9";
                break;
            }
            case 30010110: {
                skillTag = "ds0";
                break;
            }
            case 27111009: {
                skillTag = "lm0";
                break;
            }
            case 151001004: {
                skillTag = "lw0";
                break;
            }
            default: {
                questID = 1544;
                skillTag = String.valueOf(skillId);
            }
        }
        String statData = c.getPlayer().getOneInfo(questID, skillTag);
        statData = statData == null || statData.equals("0") ? String.valueOf(1) : String.valueOf(0);
        c.getPlayer().updateOneInfo(questID, skillTag, statData, true);
        if (80011993 == skillId && (holder = player.getBuffStatValueHolder(skillId)) != null) {
            player.send(BuffPacket.giveBuff(player, holder.effect, Collections.singletonMap(SecondaryStat.ErdaStack, holder.sourceID)));
        }
    }

    public static void AndroidShop(MaplePacketReader slea, MapleCharacter chr) {
        int cid = slea.readInt();
        int androidType = slea.readInt();
        int unk1 = slea.readInt();
        int unk2 = slea.readInt();
        MapleAndroid android = chr.getAndroid();
        if (android == null || android.getType() != androidType || chr.getId() != cid) {
            chr.getClient().sendEnableActions();
            return;
        }
        if (!MapleItemInformationProvider.getInstance().getAndroidInfo((int)androidType).shopUsable && android.getShopTime() < System.currentTimeMillis()) {
            chr.getClient().sendEnableActions();
            return;
        }
        MapleShop shop = MapleShopFactory.getInstance().getShop(9330194);
        if (shop == null) {
            chr.dropMessage(1, "商店不存在，請回報給管理員。");
            chr.getClient().sendEnableActions();
            return;
        }
        shop.sendShop(chr.getClient());
    }

    public static void CompleteNpcSpeech(MaplePacketReader slea, MapleCharacter chr) {
        int questID = slea.readInt();
        int npcID = slea.readInt();
        byte questStatus = slea.readByte();
        int npcOID = slea.readInt();
        if (chr == null || chr.getMap() == null || chr.getMap().getNPCByOid(npcOID) == null || chr.getMap().getNPCByOid(npcOID).getId() != npcID || chr.getQuestStatus(questID) != questStatus) {
            return;
        }
        if (questID == 34307) {
            MapleQuest.getInstance(questID).forceComplete(chr, npcID);
        }
    }

    public static void ChangeAndroidAntenna(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getAndroid() == null) {
            return;
        }
        int slot = slea.readInt();
        int itemId = slea.readInt();
        Item toUse = player.getInventory(MapleInventoryType.USE).getItem((short)slot);
        if (toUse == null || toUse.getItemId() != itemId || itemId != 2892000 && itemId != 2892001 || player.getAndroid().getItemId() == 1662033 || player.getAndroid().getItemId() == 1662034) {
            player.dropMessage(1, "無法使用。");
            c.sendEnableActions();
            return;
        }
        if (MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short)slot, (short)1, true)) {
            player.getAndroid().setAntennaUsed(!player.getAndroid().isAntennaUsed());
            player.setAndroid(player.getAndroid());
            c.sendEnableActions();
            player.dropMessage(1, "更變完成。");
        } else {
            player.dropMessage(1, "無法使用。");
            c.sendEnableActions();
        }
    }

    public static void HoYoungHealRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        int skillID = slea.readInt();
        int skillID2 = slea.readInt();
        MapleStatEffect eff = null;
        eff = player.getEffectForBuffStat(SecondaryStat.MiracleDrug);
        if (eff != null) {
            player.handleHoYoungValue(eff.getX(), eff.getY());
        }
        switch (skillID) {
            case 400011047: {
                SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.IndieBarrier, skillID);
                if (mbsvh == null) {
                    return;
                }
                int maxHP = player.getStat().getCurrentMaxHP();
                int shield = Math.min(maxHP * mbsvh.effect.getY() / 100, maxHP);
                if (mbsvh.value < shield) {
                    return;
                }
                mbsvh.value = shield;
                player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(SecondaryStat.IndieBarrier, mbsvh.effect.getSourceId())));
                break;
            }
            case 162121022: {
                SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.IndieBarrier, 162120038);
                if (mbsvh == null) {
                    return;
                }
                int maxHP = player.getStat().getCurrentMaxHP();
                int shield = Math.min(mbsvh.value + maxHP * mbsvh.effect.getX() / 100, maxHP);
                if (mbsvh.value >= shield) {
                    return;
                }
                mbsvh.value = shield;
                player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(SecondaryStat.IndieBarrier, mbsvh.effect.getSourceId())));
                break;
            }
        }
    }

    public static void WarpTOBossEventMap(MaplePacketReader slea, MapleClient c) {
        int BossType = slea.readInt();
        int unk = slea.readInt();
        int mapid = slea.readInt();
        c.getPlayer().changeMap(mapid, 0);
    }

    public static void WaitQueueRequest(MaplePacketReader slea, MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        BossListType type = BossListType.getType((int)slea.readByte());
        int unk1 = slea.readInt();
        int bossType = slea.readInt();
        BossList boss = BossList.getType((int)bossType);
        int difficulty = slea.readInt();
        if (boss == null) {
            c.getPlayer().dropMessage(1, "BOSS傳送錯誤[" + bossType + "]請反饋給管理員.");
            return;
        }
        if (boss.getQuestId(difficulty) > 0 && c.getPlayer().getQuestStatus(boss.getQuestId(difficulty)) != 2) {
            c.getPlayer().dropMessage(1, "確認BOSS需要出現的任務.");
            return;
        }
        if (boss.getMinLevel(difficulty) > c.getPlayer().getLevel()) {
            c.getPlayer().dropMessage(1, "確認是否可進入的等級.");
            return;
        }
        switch (type) {
            case FindPart:
                c.announce(MaplePacketCreator.getShowBossListWait(c.getPlayer(), 11, new int[]{2, unk1, boss.getValue(), boss.getMapId(difficulty)}));
                c.announce(MaplePacketCreator.getShowBossListWait(c.getPlayer(), 20, new int[]{2}));
                c.announce(MaplePacketCreator.getShowBossListWait(c.getPlayer(), 18, new int[]{0, unk1, boss.getValue()}));
                break;
            case Waiting: {
                c.announce(MaplePacketCreator.getShowBossListWait(c.getPlayer(), 11, new int[]{5, unk1, boss.getValue(), 0}));
                break;
            }
            case Join: {
                MapleStatEffect effect;
                Skill skill;
                c.announce(MaplePacketCreator.getShowBossListWait(c.getPlayer(), 11, new int[]{12, unk1, boss.getValue(), 0}));
                c.announce(MaplePacketCreator.updateInfoQuest(7265, "mapR=" + c.getPlayer().getMapId()));
                c.sendEnableActions();
                c.getPlayer().saveLocation(SavedLocationType.BPReturn);
                if (boss.getSkillId() > 0 && (skill = SkillFactory.getSkill(boss.getSkillId())) != null && (effect = skill.getEffect(1)) != null) {
                    effect.applyTo(c.getPlayer());
                }
                c.getPlayer().changeMap(boss.getMapId(difficulty), 0);
                break;
            }
            case Exit: {
                c.announce(MaplePacketCreator.getShowBossListWait(c.getPlayer(), 11, new int[]{5, unk1, boss.getValue(), 0}));
                break;
            }
        }
    }

    public static void fieldTransferRequest(MaplePacketReader lea, MapleCharacter chr) {
        if (chr.getMap() == null || chr.checkEvent() || chr.getMap().isBossMap()) {
            chr.dropMessage(1, "所在區域無法執行該動作！");
            return;
        }
        int questID = lea.readInt();
        int toMap = 0;
        String mapID = String.valueOf(chr.getMapId());
        switch (questID) {
            case 7860: {
                toMap = 910001000;
                if (chr.getMapId() == toMap) {
                    chr.dropMessage(1, "所在區域無法執行該動作！");
                    return;
                }
                String cooltime = chr.getOneInfo(questID, "coolTime");
                long timeNow = System.currentTimeMillis();
                if (cooltime != null && !"".equals(cooltime) && DateUtil.getStringToTime(cooltime, "yyyy/MM/dd HH:mm:ss") > timeNow) {
                    chr.dropMessage(1, "現在還無法使用。");
                    return;
                }
                MapleQuest.getInstance(questID).forceStart(chr, 0, "link");
                chr.updateOneInfo(questID, "returnMap", mapID);
                chr.updateOneInfo(questID, "coolTime", DateUtil.getPreTime("m", 30));
                break;
            }
            case 26015: {
                toMap = 200000301;
                if (chr.getMapId() == toMap) {
                    chr.dropMessage(1, "所在區域無法執行該動作！");
                    return;
                }
                MapleQuest.getInstance(questID).forceStart(chr, 0, "link");
                chr.updateOneInfo(questID, "returnMap", mapID);
                MapleQuest.getInstance(26010).forceStart(chr, 0, mapID);
            }
        }
        if (toMap == 0) {
            return;
        }
        MapleMap map = chr.getClient().getChannelServer().getMapFactory().getMap(toMap);
        chr.changeMap(map);
    }

    public static void previewChoiceBeautyCard(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        int nGender;
        int slot = slea.readInt();
        int cardItemID = slea.readInt();
        byte showLookFlag = slea.readByte();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        boolean success = cardItemID / 10000 == 515 && ii.isChoice(cardItemID);
        Item card = chr.getInventory(MapleInventoryType.CASH).getItem((short)slot);
        long androidSN = -1L;
        boolean bl = success = success && card.getItemId() == cardItemID && card.getQuantity() > 0;
        if (showLookFlag == 100) {
            androidSN = slea.readLong();
            success = success && chr.getAndroid() != null && (long)chr.getAndroid().getUniqueId() == androidSN;
            nGender = chr.getAndroid().getGender();
        } else {
            nGender = showLookFlag == 2 ? 1 : chr.getGender();
        }
        LinkedList<Integer> beautyList = new LinkedList<Integer>();
        LinkedList<Integer> beautyList2 = new LinkedList<Integer>();
        if (success) {
            if (cardItemID / 1000 == 5151) {
                int base = showLookFlag == 100 ? chr.getAndroid().getHair() : (showLookFlag != 1 && showLookFlag != 2 ? chr.getHair() : chr.getSecondHair());
                base = ItemConstants.getStyleBaseID(base);
                int base2 = ItemConstants.getStyleBaseID(chr.getSecondHair());
                for (int i = 0; i < 8; ++i) {
                    if (ii.isHairExist(base + i)) {
                        beautyList.add(i);
                    }
                    if (showLookFlag != 101 || !ii.isHairExist(base2 + i)) continue;
                    beautyList2.add(i);
                }
            } else if (cardItemID / 100 == 51521) {
                int base = showLookFlag == 100 ? chr.getAndroid().getFace() : (showLookFlag != 1 && showLookFlag != 2 ? chr.getFace() : chr.getSecondFace());
                base = ItemConstants.getStyleBaseID(base);
                int base2 = ItemConstants.getStyleBaseID(chr.getSecondFace());
                for (int i = 0; i < 8; ++i) {
                    if (ii.isFaceExist(base + i * 100)) {
                        beautyList.add(i);
                    }
                    if (showLookFlag != 101 || !ii.isFaceExist(base2 + i * 100)) continue;
                    beautyList2.add(i);
                }
            } else {
                List<RaffleItem> itemList = RafflePool.getItems(cardItemID);
                for (RaffleItem item : itemList) {
                    if (item.getItemId() / 1000 == 0 || item.getItemId() / 1000 == 2 || item.getItemId() / 1000 == 12) {
                        beautyList.add(item.getItemId() % 1000);
                        if (showLookFlag != 101) continue;
                        beautyList2.add(item.getItemId() % 1000);
                        continue;
                    }
                    if (ItemConstants.類型.getGender(item.getItemId()) == nGender || ItemConstants.類型.getGender(item.getItemId()) >= 2) {
                        beautyList.add(item.getItemId());
                    }
                    if (showLookFlag != 101 || ItemConstants.類型.getGender(item.getItemId()) != 1 && ItemConstants.類型.getGender(item.getItemId()) < 2) continue;
                    beautyList2.add(item.getItemId());
                }
            }
        }
        if (!success || beautyList.size() <= 0 || showLookFlag == 101 && beautyList2.size() <= 0) {
            c.announce(MaplePacketCreator.getBeautyList(showLookFlag, cardItemID, 0, -1L, null, null));
            return;
        }
        c.announce(MaplePacketCreator.getBeautyList(showLookFlag, cardItemID, slot, androidSN, beautyList, showLookFlag == 101 ? beautyList2 : null));
    }

    public static void ChangeNameRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (c == null || player == null || player.getMap() == null) {
            c.announce(InventoryPacket.ChangeNameResult(1, 0));
            return;
        }
        int cid = slea.readInt();
        if (!slea.readBool()) {
            return;
        }
        int itemID = slea.readInt();
        String oldName = slea.readMapleAsciiString();
        String newName = slea.readMapleAsciiString();
        if (player.getId() != cid || !player.getName().equals(oldName) || oldName.equals(newName)) {
            c.announce(InventoryPacket.ChangeNameResult(2, 0));
            return;
        }
        if (!player.haveItem(itemID)) {
            c.announce(InventoryPacket.ChangeNameResult(3, 0));
            return;
        }
        if (!MapleCharacterUtil.canCreateChar((String)newName, (boolean)player.isIntern())) {
            if (MapleCharacterUtil.getIdByName((String)newName) == -1) {
                c.announce(InventoryPacket.ChangeNameResult(6, 0));
            } else {
                c.announce(InventoryPacket.ChangeNameResult(7, 0));
            }
            return;
        }
        player.removeItem(itemID, 1);
        player.setName(newName);
        c.announce(InventoryPacket.ChangeNameResult(0, 0));
    }

    public static void ChangeNamePwCheck(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (c == null || player == null || player.getMap() == null) {
            c.announce(InventoryPacket.ChangeNameResult(1, 0));
            return;
        }
        String secondPw = slea.readMapleAsciiString();
        if (!c.CheckSecondPassword(secondPw)) {
            c.announce(InventoryPacket.ChangeNameResult(10, 0));
            return;
        }
        int itemID = slea.readInt();
        if (!player.haveItem(itemID)) {
            c.announce(InventoryPacket.ChangeNameResult(3, 0));
            return;
        }
        c.announce(InventoryPacket.ChangeNameResult(9, itemID));
    }

    public static void CombingRoomActionReq(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        chr.updateTick(slea.readInt());
        byte styleType = slea.readByte();
        int base = 3 - styleType;
        byte action = slea.readByte();
        byte pos = slea.readByte();
        boolean isSecond = slea.readBool();
        Map<Integer, List<Integer>> combingRoomInventorys = chr.getSalon();
        switch (action) {
            case 2: {
                slea.readByte();
                if (base < 1 || base > 3) {
                    chr.dropMessage(1, "因未知錯誤，儲存失敗。");
                } else {
                    int styleID = 0;
                    int n = base == 1 ? chr.getSkinColor() : (styleID = base == 2 ? chr.getFace() : chr.getHair());
                    if ((JobConstants.is天使破壞者(chr.getJob()) || JobConstants.is神之子(chr.getJob())) && isSecond) {
                        styleID = base == 1 ? chr.getSecondSkinColor() : (base == 2 ? chr.getSecondFace() : chr.getSecondHair());
                    }
                    if (combingRoomInventorys != null && combingRoomInventorys.containsKey(base) && combingRoomInventorys.get(base).size() > pos && combingRoomInventorys.get(base).get(pos) == 0) {
                        combingRoomInventorys.get(base).remove(pos);
                        combingRoomInventorys.get(base).add(pos, styleID);
                        c.announce(MaplePacketCreator.encodeUpdateCombingRoomSlotRes(styleType, action, pos, styleID));
                    }
                }
                c.announce(MaplePacketCreator.encodeCombingRoomActionRes(styleType, action, 1));
                break;
            }
            case 3: {
                if (combingRoomInventorys != null && combingRoomInventorys.containsKey(base) && combingRoomInventorys.get(base).size() > pos && combingRoomInventorys.get(base).get(pos) != 0) {
                    slea.readInt();
                    combingRoomInventorys.get(base).remove(pos);
                    combingRoomInventorys.get(base).add(pos, 0);
                    c.announce(MaplePacketCreator.encodeUpdateCombingRoomSlotRes(styleType, action, pos, 0));
                } else {
                    chr.dropMessage(1, "因未知錯誤，刪除失敗。");
                }
                c.announce(MaplePacketCreator.encodeCombingRoomActionRes(styleType, action, 1));
                break;
            }
            case 4: {
                if (combingRoomInventorys == null || !combingRoomInventorys.containsKey(base) || combingRoomInventorys.get(base).size() < pos) {
                    chr.dropMessage(1, "因未知錯誤，套用失敗。");
                } else {
                    int chrValue = 0;
                    int value = combingRoomInventorys.get(base).get(pos);
                    int n = base == 1 ? chr.getSkinColor() : (chrValue = base == 2 ? chr.getFace() : chr.getHair());
                    if (!JobConstants.is天使破壞者(chr.getJob()) && !JobConstants.is神之子(chr.getJob())) {
                        isSecond = false;
                    }
                    if (isSecond) {
                        chrValue = base == 1 ? chr.getSecondSkinColor() : (base == 2 ? chr.getSecondFace() : chr.getSecondHair());
                    }
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    if (ItemConstants.類型.getGender(value) < 2 && (JobConstants.is神之子(chr.getJob()) && isSecond ? (byte)1 : chr.getGender()) != ItemConstants.類型.getGender(value) || value == chrValue || base == 1 && !ii.isSkinExist(value) || base == 2 && !ii.isFaceExist(value) || base == 3 && !ii.isHairExist(value)) {
                        chr.dropMessage(1, "因未知錯誤，套用失敗。");
                    } else {
                        if (base == 1) {
                            if (isSecond) {
                                chr.setSecondSkinColor((byte)value);
                                if (JobConstants.is天使破壞者(chr.getJob())) {
                                    c.announce(MaplePacketCreator.DressUpInfoModified(chr));
                                } else if (JobConstants.is神之子(chr.getJob())) {
                                    c.announce(MaplePacketCreator.zeroInfo(chr, 8, true));
                                }
                            } else {
                                chr.setSkinColor((byte)value);
                                chr.updateSingleStat(MapleStat.SKIN, value);
                            }
                        } else if (base == 2) {
                            if (isSecond) {
                                chr.setSecondFace(value);
                                if (JobConstants.is天使破壞者(chr.getJob())) {
                                    c.announce(MaplePacketCreator.DressUpInfoModified(chr));
                                } else if (JobConstants.is神之子(chr.getJob())) {
                                    c.announce(MaplePacketCreator.zeroInfo(chr, 32, true));
                                }
                            } else {
                                chr.setFace(value);
                                chr.updateSingleStat(MapleStat.FACE, value);
                            }
                        } else if (base == 3) {
                            if (isSecond) {
                                chr.setSecondHair(value);
                                if (JobConstants.is天使破壞者(chr.getJob())) {
                                    c.announce(MaplePacketCreator.DressUpInfoModified(chr));
                                } else if (JobConstants.is神之子(chr.getJob())) {
                                    c.announce(MaplePacketCreator.zeroInfo(chr, 16, true));
                                }
                            } else {
                                chr.setHair(value);
                                chr.updateSingleStat(MapleStat.HAIR, value);
                            }
                        }
                        combingRoomInventorys.get(base).remove(pos);
                        combingRoomInventorys.get(base).add(pos, chrValue);
                        c.announce(MaplePacketCreator.encodeUpdateCombingRoomSlotRes(styleType, action, pos, chrValue));
                        chr.equipChanged();
                    }
                }
                c.announce(MaplePacketCreator.encodeCombingRoomRes(styleType, action, 4));
                c.sendEnableActions();
            }
        }
    }

    public static void updateBulletCount(MaplePacketReader slea, MapleCharacter chr) {
        int skillid = slea.readInt();
        SecondaryStatValueHolder mbsvh = chr.getBuffStatValueHolder(SecondaryStat.CannonShooter_BFCannonBall, skillid);
        if (mbsvh == null) {
            return;
        }
        int value = mbsvh.value - 1;
        if (value < 0) {
            return;
        }
        mbsvh.value = value;
        mbsvh.startTime = System.currentTimeMillis();
        BuffPacket.giveBuff(chr, mbsvh.effect, Collections.singletonMap(SecondaryStat.CannonShooter_BFCannonBall, mbsvh.effect.getSourceId()));
    }

    public static void Auto5thRevenant_ReduceAnger(MaplePacketReader slea, MapleCharacter chr) {
        int a1 = slea.readInt();
        if (a1 == 1) {
            return;
        }
        SecondaryStatValueHolder mbsvh = chr.getBuffStatValueHolder(SecondaryStat.RevenantGauge);
        if (mbsvh != null && mbsvh.z > 0) {
            mbsvh.z = Math.max(0, mbsvh.z - (int)Math.ceil((double)(mbsvh.z * mbsvh.effect.getQ2()) / 100.0));
            chr.send(BuffPacket.giveBuff(chr, mbsvh.effect, Collections.singletonMap(SecondaryStat.RevenantGauge, mbsvh.effect.getSourceId())));
        }
    }

    public static void Auto5thRevenantReduceHP(MaplePacketReader slea, MapleCharacter chr) {
        SecondaryStatValueHolder mbsvh;
        int a1 = slea.readInt();
        if (a1 == 1) {
            return;
        }
        int a2 = slea.readInt();
        int a3 = slea.readInt();
        int skillid = slea.readInt();
        int a4 = slea.readInt();
        int a5 = slea.readInt();
        if (skillid == 400011129 && (mbsvh = chr.getBuffStatValueHolder(SecondaryStat.DeathDance)) != null && mbsvh.x > 0) {
            PlayerStats stats = chr.getStat();
            if (stats.getHp() > 1) {
                int hpChange = Math.min(stats.getHp() - 1, (mbsvh.z + stats.getCurrentMaxHP() * mbsvh.effect.getY() / 100) / 50);
                chr.addHPMP(-hpChange, 0, true);
            }
            --mbsvh.x;
            if (mbsvh.x <= 0) {
                chr.dispelEffect(SecondaryStat.DeathDance);
            } else {
                chr.send(BuffPacket.giveBuff(chr, mbsvh.effect, Collections.singletonMap(SecondaryStat.DeathDance, mbsvh.effect.getSourceId())));
            }
        }
    }

    public static void UseJupiterThunder(MaplePacketReader slea, MapleCharacter chr) {
        MapleStatEffect effect;
        int skillid = slea.readInt();
        if (skillid != 400021094 || chr.isSkillCooling(skillid) || (effect = chr.getSkillEffect(skillid)) == null) {
            return;
        }
        int a1 = slea.readInt();
        int a2 = slea.readInt();
        int a3 = slea.readInt();
        Point pos = slea.readPosInt();
        int a4 = slea.readInt();
        int a5 = slea.readInt();
        if (!effect.applyTo(chr)) {
            return;
        }
        chr.getMap().broadcastMessage(MaplePacketCreator.createJupiterThunder(chr.getId(), pos, a4, a5, skillid, 30, a1, a2, a3));
    }

    public static void JupiterThunderAction(MaplePacketReader slea, MapleCharacter chr) {
        int a1 = slea.readInt();
        int a2 = slea.readInt();
        int cid = slea.readInt();
        if (cid != chr.getId()) {
            return;
        }
        int skillid = slea.readInt();
        if (skillid != 400021094) {
            return;
        }
        int a3 = slea.readInt();
        int a4 = 0;
        int a5 = 0;
        int a6 = 0;
        int a7 = 0;
        if (a1 == 1) {
            a4 = slea.readInt();
            a5 = slea.readInt();
            a6 = slea.readInt();
            a7 = slea.readInt();
        }
        chr.getMap().broadcastMessage(MaplePacketCreator.jupiterThunderAction(chr.getId(), a1, a2, a3, a4, a5, a6, a7));
    }

    public static void JupiterThunderEnd(MaplePacketReader slea, MapleCharacter chr) {
        int a1 = slea.readInt();
        int a2 = slea.readInt();
        int bulletCount = slea.readInt();
        MapleStatEffect effect = chr.getSkillEffect(400021094);
        if (effect != null && bulletCount > 0) {
            chr.reduceSkillCooldown(400021094, bulletCount * (int)(effect.getInfoD().get((Object)MapleStatInfo.t) * 1000.0));
        }
        chr.getMap().broadcastMessage(MaplePacketCreator.jupiterThunderEnd(chr.getId(), a1, a2));
    }

    public static void JENO_ENERGY_STORAGE_SYSTEM(MaplePacketReader slea, MapleCharacter chr) {
        int value;
        MapleStatEffect effect = chr.getSkillEffect(30020232);
        int buffedIntValue = chr.getBuffedIntValue(SecondaryStat.OverloadMode);
        if (effect != null && chr.getCheatTracker().canNextRecoverPower(buffedIntValue > 0) && (value = chr.getBuffedIntValue(SecondaryStat.SurplusSupply)) < SkillConstants.dY(chr.getJob()) * 5 + buffedIntValue) {
            chr.setBuffStatValue(SecondaryStat.SurplusSupply, 30020232, value + 1);
            effect.unprimaryPassiveApplyTo(chr);
        }
    }

    public static void Auto5thGoddessBless(MaplePacketReader slea, MapleCharacter chr) {
        int a1 = slea.readInt();
        SecondaryStatValueHolder mbsvh = chr.getBuffStatValueHolder(SecondaryStat.XenonBursterLaser);
        if (mbsvh != null) {
            mbsvh.z = a1;
            chr.send(BuffPacket.giveBuff(chr, mbsvh.effect, Collections.singletonMap(SecondaryStat.XenonBursterLaser, mbsvh.effect.getSourceId())));
        }
    }

    public static void handleRevolvingCannon(MaplePacketReader slea, MapleCharacter chr) {
        slea.readByte();
        int skillId = slea.readInt();
        if (skillId != 37001001 || chr.getSkillEffect(skillId) != null) {
            // empty if block
        }
    }

    public static void CreateForceAtomObject(MaplePacketReader lea, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.getMap().isTown() || player.getBuffStatValueHolder(SecondaryStat.AMEarthVeinOnOff) == null) {
            return;
        }
        byte idk1 = lea.readByte();
        int idx = lea.readInt();
        byte b1 = lea.readByte();
        Point pos = lea.readPosInt();
        int b2 = lea.readInt();
        ForceAtomObject obj = new ForceAtomObject(player.getSpecialStat().gainForceCounter(), 20, 0, player.getId(), 0, 162101000);
        obj.Idk1 = idk1;
        obj.Position = new Point(0, 1);
        obj.Idk2 = b2;
        obj.ObjPosition = pos;
        obj.B1 = true;
        obj.addX((int)b1);
        obj.addX(idx);
        player.getForceAtomObjects().put(obj.Idx, obj);
        player.send(AdelePacket.ForceAtomObject((int)player.getId(), Collections.singletonList(obj), (int)0));
    }

    public static void SkillStageChangeRequest(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        SecondaryStatValueHolder mbsvh;
        int skillID = slea.readInt();
        int duration = slea.readInt();
        SecondaryStat stat = null;
        if (skillID == 400011072) {
            stat = SecondaryStat.GrandCross;
        }
        if (stat != null && (mbsvh = player.getBuffStatValueHolder(stat)) != null && mbsvh.effect != null) {
            ++mbsvh.value;
            player.send(BuffPacket.giveBuff(player, mbsvh.effect, Collections.singletonMap(stat, mbsvh.effect.getSourceId())));
        }
    }

    public static void ForceAtomNextTarget(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        slea.readInt();
        int skillid = slea.readInt();
        slea.readInt();
        int oid = slea.readInt();
        MapleMap map = player.getMap();
        MapleMonster mob = map.getMonsterByOid(oid);
    }

    public static void DivineJudgmentStatReset(MaplePacketReader slea, MapleCharacter player) {
        if (player == null) {
            return;
        }
        Map divineJudgmentInfos = (Map)player.getTempValues().computeIfAbsent("神聖審判計數", k -> new LinkedHashMap());
        int nCount = slea.readInt();
        for (int i = 0; i < nCount; ++i) {
            divineJudgmentInfos.remove(slea.readInt());
        }
    }

    public static void ReincarnationModeSelect(MaplePacketReader slea, MapleCharacter player) {
        if (player == null) {
            return;
        }
        int mode = slea.readInt();
        if (mode < 1 || mode > 3) {
            return;
        }
        MapleStatEffect effect = player.getSkillEffect(1321020);
        if (effect == null || player.isSkillCooling(effect.getSourceId())) {
            return;
        }
        player.getTempValues().put("ReincarnationMode", mode);
        effect.applyTo(player);
    }

    public static void PoisonAreaCreate(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        MapleStatEffect effect = player.getSkillEffect(2111013);
        if (player.getSummonBySkillID(2111013) == null || effect == null) {
            return;
        }
        byte action = slea.readByte();
        int nCount = slea.readInt();
        for (int i = 0; i < nCount; ++i) {
            effect.applyAffectedArea(player, slea.readPosInt());
        }
    }

    public static void PoisonAreaRemove(MaplePacketReader slea, MapleCharacter player) {
        int oid = slea.readInt();
        int unknown = slea.readInt();
        MapleAffectedArea area = player.getMap().getAffectedAreaByOid(oid);
        if (area != null) {
            area.cancel();
            player.getMap().disappearMapObject(area);
        }
    }

    public static void ApplyAffectAreaEffect(MaplePacketReader slea, MapleCharacter player) {
        if (player == null || player.getMap() == null) {
            return;
        }
        MapleAffectedArea area = player.getMap().getAffectedAreaByOid(slea.readInt());
        if (area == null || area.getOwnerId() != player.getId() || area.getSkillID() != slea.readInt()) {
            return;
        }
        area.handleMonsterEffect(player.getMap(), -1);
    }

    public static void EventUIBottonHandler(MaplePacketReader slea, MapleCharacter player) {
        if (player == null || player.getMap() == null || player.hasBlockedInventory()) {
            return;
        }
        int WINDOWN_ID = slea.readInt();
        int UI_ID = slea.readInt();
        player.getScriptManager().startScript(2008, "Run_" + WINDOWN_ID + "_" + UI_ID, ScriptType.Npc);
    }

    public static void BlackMageRecv(MaplePacketReader slea, MapleClient c) {
        int type = slea.readInt();
        if (type != 3) {
            c.getPlayer().getMap().broadcastMessage(CField.getSelectPower((int)8, (int)39));
            SkillFactory.getSkill(80002625).getEffect(1).applyTo(c.getPlayer());
        }
        Timer.EtcTimer.getInstance().schedule(() -> c.getPlayer().getMap().broadcastMessage(CField.getSelectPower((int)9, (int)39)), 4000L);
    }

    public static void unlockTrinity(MapleClient c) {
        if (c.getPlayer().getSkillLevel(65121101) > 0) {
            c.getPlayer().getMap().broadcastMessage(CField.unlockSkill());
            c.getPlayer().getMap().broadcastMessage(CField.showNormalEffect((MapleCharacter)c.getPlayer(), (int)49, (boolean)true));
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.showNormalEffect((MapleCharacter)c.getPlayer(), (int)49, (boolean)false), false);
        }
    }

    public static final void BlackMageBallRecv(MaplePacketReader slea, MapleCharacter chr) {
        int type = slea.readInt();
        if (chr.isAlive()) {
            if (chr.isGm()) {
                return;
            }
            EnumMap<SecondaryStat, Pair<Integer, Integer>> diseases = new EnumMap<SecondaryStat, Pair<Integer, Integer>>(SecondaryStat.class);
            switch (type) {
                case 1: {
                    diseases.put(SecondaryStat.BlackMageCreate, new Pair<Integer, Integer>(4, 6000));
                    break;
                }
                case 2: {
                    diseases.put(SecondaryStat.BlackMageDestroy, new Pair<Integer, Integer>(10, 6000));
                }
            }
            if (chr.getDiseases(SecondaryStat.BlackMageCreate) && type == 2 || chr.getDiseases(SecondaryStat.BlackMageDestroy) && type == 1) {
                chr.setDeathCount((byte)(chr.getDeathCount() - 1));
                chr.dispelDebuffs();
                if (chr.getDeathCount() > 0) {
                    chr.addHP(-chr.getStat().getCurrentMaxHp() * 30L / 100L);
                    if (chr.isAlive()) {
                        MobSkillFactory.getMobSkill(120, 39).applyEffect(chr, null, 1000, false);
                    }
                } else {
                    chr.addHP(-chr.getStat().getCurrentMaxHp());
                }
            }
        }
    }

    public static void blackMageBallDown(MaplePacketReader slea, MapleClient c, MapleCharacter player) {
        int type = slea.readInt();
        EnumMap<SecondaryStat, Pair<Integer, Integer>> diseases = new EnumMap<SecondaryStat, Pair<Integer, Integer>>(SecondaryStat.class);
        switch (type) {
            case 1: {
                diseases.put(SecondaryStat.BlackMageCreate, new Pair<Integer, Integer>(4, 6000));
                break;
            }
            case 2: {
                diseases.put(SecondaryStat.BlackMageDestroy, new Pair<Integer, Integer>(10, 6000));
            }
        }
        if (player.getDiseases(SecondaryStat.BlackMageCreate) && type == 2 || player.getDiseases(SecondaryStat.BlackMageDestroy) && type == 1) {
            player.setDeathCount((byte)(player.getDeathCount() - 1));
            if (player.getDeathCount() > 0) {
                player.addHP(-player.getStat().getCurrentMaxHp() * 30L / 100L);
                if (player.isAlive()) {
                    MobSkillFactory.getMobSkill(120, 39).applyEffect(player, null, player.getMeisterSkillEff(), false);
                }
            } else {
                player.addHP(-player.getStat().getCurrentMaxHp());
            }
        }
        player.giveDebuff(diseases, MobSkillFactory.getMobSkill(249, player.getBlackMageWB() == 2 ? 1 : 2));
    }

    public static void SpeedMirageObjectCreate(MaplePacketReader slea, MapleCharacter player) {
        if (player == null) {
            return;
        }
        MapleStatEffect effect = player.getSkillEffect(3120021);
        SecondaryStatValueHolder mbsvh = player.getBuffStatValueHolder(SecondaryStat.FlashMirage);
        if (mbsvh == null || effect == null && (effect = mbsvh.effect) == null) {
            return;
        }
        slea.readByte();
        int nCount = slea.readInt();
        if (nCount > effect.getW()) {
            return;
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(OutHeader.LP_UserEffectLocal);
        mplew.write(EffectOpcode.UserEffect_SpeedMirage.getValue());
        mplew.writeInt(3111015);
        mplew.writeInt(player.getId());
        mplew.writeInt(nCount);
        mplew.writePosInt(slea.readPosInt());
        for (int i = 0; i < nCount; ++i) {
            byte x = slea.readByte();
            mplew.write((int)x);
            mplew.writeInt(slea.readInt());
            MapleMonster mob = player.getMap().getMobObject(slea.readInt());
            if (mob != null) {
                ForceAtomObject obj = new ForceAtomObject(player.getSpecialStat().gainForceCounter(), 39, 0, player.getId(), Randomizer.isSuccess(50) ? 90 : -90, 3111016);
                obj.Target = mob.getObjectId();
                obj.CreateDelay = 480;
                obj.EnableDelay = 30;
                obj.Expire = 3000;
                obj.Position = new Point(0, 6);
                obj.ObjPosition = new Point(mob.getPosition());
                obj.addX((int)x);
                player.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)player.getId(), Collections.singletonList(obj), (int)0), player.getPosition());
            }
            mplew.writeInt(slea.readInt());
            if (i + 1 >= nCount) continue;
            mplew.writePosInt(slea.readPosInt());
        }
        if (slea.available() > 0L) {
            if (player.isDebug()) {
                player.dropDebugMessage(0, "閃光幻象處理出錯");
            }
        } else {
            player.getMap().broadcastMessage(player, mplew.getPacket(), true);
        }
        mbsvh.value = 0;
        mbsvh.effect.unprimaryPassiveApplyTo(player);
    }

    public static void SilhouetteMirageCharge(MaplePacketReader slea, MapleCharacter chr) {
        MapleStatEffect effect;
        int skillid = slea.readInt();
        if (skillid != 400031053 || (effect = chr.getSkillEffect(skillid)) == null) {
            return;
        }
        effect.applyBuffEffect(chr, chr, effect.getBuffDuration(chr), false, false, true, null);
    }

    public static void touchSpider(MaplePacketReader slea, MapleClient c, MapleMap map) {
        spider web = (spider)c.getPlayer().getMap().getMapObject(slea.readInt(), MapleMapObjectType.WEB);
        if (c.getPlayer().clearWeb > 0) {
            try {
                c.announce(WillPacket.willSpider((boolean)false, (spider)web));
                c.getPlayer().getMap().removeMapObject(web);
                --c.getPlayer().clearWeb;
            }
            catch (Throwable throwable) {}
        } else if (c.getPlayer().getBuffedValue(SecondaryStat.NotDamaged) == null && c.getPlayer().getBuffedValue(SecondaryStat.NotDamaged) == null && c.getPlayer().isAlive()) {
            c.announce(CField.DamagePlayer2((int)((int)(c.getPlayer().getStat().getCurrentMaxHp() / 100L) * 30)));
            c.getPlayer().setSkillCustomInfo(8880302, 0L, 5000L);
            if (!c.getPlayer().getDiseases(SecondaryStat.Seal)) {
                MobSkill ms1 = MobSkillFactory.getMobSkill(120, 40);
                ms1.setDuration(5000);
                c.getPlayer().getEffectForBuffStat(SecondaryStat.Seal);
            }
        }
    }

    public static void useMoonGauge(MapleClient c) {
        if (c.getPlayer().getMapId() == 450008150 || c.getPlayer().getMapId() == 450008750) {
            String name = c.getPlayer().getTruePosition().y > -1000 ? "ptup" : "ptdown";
            c.getPlayer().setMoonGauge(Math.max(0, c.getPlayer().getMoonGauge() - 45));
            c.announce(WillPacket.addMoonGauge((int)c.getPlayer().getMoonGauge()));
            c.announce(WillPacket.teleport());
            c.announce(CField.portalTeleport((String)name));
        } else if (c.getPlayer().getMapId() == 450008250 || c.getPlayer().getMapId() == 450008850) {
            c.getPlayer().getDiseases(SecondaryStat.DebuffIncHP);
            c.getPlayer().setMoonGauge(Math.max(0, c.getPlayer().getMoonGauge() - 50));
            c.announce(WillPacket.addMoonGauge((int)c.getPlayer().getMoonGauge()));
            c.announce(WillPacket.cooldownMoonGauge((int)7000));
            Timer.MobTimer.getInstance().schedule(() -> SkillFactory.getSkill(80002404).getEffect(1).applyTo(c.getPlayer(), true), 7000L);
        } else if (c.getPlayer().getMapId() == 450008350 || c.getPlayer().getMapId() == 450008950) {
            c.getPlayer().setMoonGauge(Math.max(0, c.getPlayer().getMoonGauge() - 5));
            c.getPlayer().clearWeb = 2;
            c.announce(WillPacket.addMoonGauge((int)c.getPlayer().getMoonGauge()));
            c.announce(WillPacket.cooldownMoonGauge((int)5000));
            Timer.MapTimer.getInstance().schedule(() -> {
                c.getPlayer().clearWeb = 0;
            }, 5000L);
        }
    }

    public static void PhantomShroud(MaplePacketReader slea, MapleClient c) {
        int skillid = slea.readInt();
        if (skillid == 20031205) {
            int count = 0;
            if (c.getPlayer().getTempValues().get("skill" + skillid) != null) {
                count = Integer.parseInt(c.getPlayer().getTempValues().get("skill" + skillid).toString());
            }
            c.getPlayer().getTempValues().put("skill" + skillid, ++count);
        }
    }

    public static byte[] SaveUnion(MaplePacketReader slea, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SAVE_UNION_QUEST.getValue());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static void handleUserSkillSwitchRequest(MaplePacketReader in, MapleClient c, MapleCharacter chr) {
        if (!chr.isAlive() || !JobConstants.is隱月(chr.getJob())) {
            return;
        }
        int beforeSkillId = in.readInt();
        int afterSkillId = in.readInt();
        chr.updateInfoQuest(21770, beforeSkillId + "=" + afterSkillId);
    }


}

