/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Config.constants.ItemConstants$方塊
 *  Handler.OverseasHandler$1
 *  SwordieX.overseas.extraequip.ExtraEquipMagic
 *  SwordieX.overseas.extraequip.ExtraEquipResult
 *  SwordieX.util.Position
 *  connection.packet.OverseasPacket
 */
package Handler;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MonsterFamiliar;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.inventory.EnhanceResultType;
import Client.inventory.Equip;
import Client.inventory.FamiliarCard;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.MapleInventory;
import Client.inventory.MapleInventoryType;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Handler.Handler;
import Handler.OverseasHandler;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.movement.LifeMovementFragment;
import Opcode.header.InHeader;
import Packet.BuffPacket;
import Packet.ForcePacket;
import Packet.InventoryPacket;
import Server.channel.handler.MovementParse;
import SwordieX.overseas.extraequip.ExtraEquipMagic;
import SwordieX.overseas.extraequip.ExtraEquipResult;
import SwordieX.util.Position;
import connection.InPacket;
import connection.packet.OverseasPacket;
import java.awt.Point;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;

public class OverseasHandler {
    private static final Logger log = LoggerFactory.getLogger(OverseasHandler.class);

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Handler(op=InHeader.CP_EXTRA_EQUIP_REQUEST)
    public static void extraEquipRequest(MapleClient c, InPacket inPacket) {
        MapleCharacter player = c.getPlayer();
        if (player == null || player.getMap() == null) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte resultType = inPacket.decodeByte();
        byte ADD_V261_UNK = inPacket.decodeByte();
        short nPacketSize = inPacket.decodeShort();
        int useType = inPacket.decodeInt();
        ExtraEquipMagic spType = ExtraEquipMagic.NONE;
        for (ExtraEquipMagic sType : ExtraEquipMagic.values()) {
            if (sType.ordinal() != useType) continue;
            spType = sType;
            break;
        }
        switch (spType) {
            default: {
                if (!player.isGm()) return;
                player.dropDebugMessage(2, "[SpecialOperation] 操作錯誤" + spType.name() + "(" + useType + ")");
                return;
            }
            case SKILL_INNER_GLARE: {
                short mode = inPacket.decodeShort();
                if (mode != 21) return;
                int size = inPacket.decodeReversedVarints();
                ArrayList<Integer> skillIds = new ArrayList<Integer>();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; ++i) {
                    int skillId = inPacket.decodeInt();
                    if (player.getSkillLevel(skillId) <= 0) continue;
                    skillIds.add(skillId);
                    sb.append(skillId).append(",");
                }
                if (sb.length() > 0) {
                    player.setKeyValue("InnerGlareBuffs", sb.substring(0, sb.length() - 1));
                }
                c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.updateInnerGlareSkills((int)player.getId(), skillIds)));
                return;
            }
            case FAMILIAR_CARDS: {
                short mode = inPacket.decodeShort();
                switch (mode) {
                    case 20: {
                        int familiarSN = inPacket.decodeInt();
                        MonsterFamiliar mf2 = player.getFamiliars().stream().filter(m -> m.getId() == familiarSN).findAny().orElse(null);
                        if (mf2 == null) break;
                        player.spawnFamiliar(mf2);
                        player.setFamiliarsChanged(true);
                        break;
                    }
                    case 21: {
                        player.removeFamiliar();
                        player.setFamiliarsChanged(true);
                        break;
                    }
                    case 22: {
                        short slot = inPacket.decodeShort();
                        Item item = player.getInventory(MapleInventoryType.USE).getItem(slot);
                        if (item == null) break;
                        if (item.getQuantity() < 1 || item.getItemId() / 10000 != 287) {
                            c.sendEnableActions();
                            return;
                        }
                        int familiarID = ItemConstants.getFamiliarByItemID(item.getItemId());
                        if (familiarID == 0) {
                            player.dropMessage(1, "這個萌獸卡無法使用。");
                            c.sendEnableActions();
                            return;
                        }
                        if (player.getFamiliars().size() >= 60) {
                            player.dropMessage(1, "萌獸圖鑒數量已經達到最大值!");
                            c.sendEnableActions();
                            return;
                        }
                        if (item.getFamiliarCard() == null) {
                            item.setFamiliarCard(new FamiliarCard((byte)0));
                        }
                        if (!MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false, false)) break;
                        MonsterFamiliar mf3 = new MonsterFamiliar(c.getAccID(), player.getId(), familiarID, item.getFamiliarCard());
                        mf3.addFlag(1);
                        player.addFamiliarsInfo(mf3);
                        player.updateFamiliar(mf3);
                        break;
                    }
                    case 23: {
                        int familiarSN = (int)inPacket.decodeLong();
                        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
                        MonsterFamiliar familiar = player.getFamiliars().stream().filter(m -> m.getId() == familiarSN).findAny().orElse(null);
                        if (familiar == null) break;
                        int exp = 0;
                        int fee = 0;
                        int[] C_upgradeR = new int[]{30, 35, 40, 50, 60};
                        int[] B_upgradeR = new int[]{20, 30, 35, 40, 50};
                        int[] A_upgradeR = new int[]{5, 10, 15, 20, 30};
                        int[] S_upgradeR = new int[]{2, 5, 10, 15, 20};
                        int[] SS_upgradeR = new int[]{1, 2, 5, 10, 15};
                        int nCount = inPacket.decodeReversedVarints();
                        for (int i = 0; i < nCount; i = (int)((byte)(i + 1))) {
                            int selectSN = (int)inPacket.decodeLong();
                            MonsterFamiliar mf4 = player.getFamiliars().stream().filter(m -> m.getId() == familiarSN).findAny().orElse(null);
                            if (mf4 == null || mf4.isLock()) {
                                return;
                            }
                            int[] upgradeR = switch (familiar.getGrade()) {
                                case 1 -> B_upgradeR;
                                case 2 -> A_upgradeR;
                                case 3 -> S_upgradeR;
                                case 4 -> SS_upgradeR;
                                default -> C_upgradeR;
                            };
                            short shortValue = (short)(ii.getFamiliarTable_rchance().get(mf4.getGrade()).get(familiar.getGrade()) + 1);
                            short addExp = (short)(Randomizer.nextInt(100) < upgradeR[mf4.getGrade()] ? 100 : (Randomizer.nextInt(100) < shortValue * 10 ? shortValue * 10 : (int)shortValue));
                            exp = (short)(exp + addExp);
                            hashMap.put(selectSN, Integer.valueOf(addExp));
                            fee += 50000 * (familiar.getGrade() + 1);
                            if ((5 - familiar.getLevel()) * 100 <= exp) break;
                        }
                        if (player.getMeso() < (long)fee) break;
                        for (Integer integer : hashMap.keySet()) {
                            player.removeFamiliarsInfo(integer);
                        }
                        familiar.gainExp(exp);
                        player.gainMeso(-fee, false);
                        c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.familiarGainExp((int)player.getId(), hashMap)));
                        player.updateFamiliars();
                        break;
                    }
                    case 24: {
                        int base = (int)inPacket.decodeLong();
                        int material = (int)inPacket.decodeLong();
                        if (player.getFamiliars().stream().noneMatch(mf -> mf.getId() == base || mf.getId() == material)) break;
                        MonsterFamiliar baseObject = player.getFamiliars().stream().filter(mf -> mf.getId() == base).findAny().orElse(null);
                        MonsterFamiliar materialObject = player.getFamiliars().stream().filter(mf -> mf.getId() == material).findAny().orElse(null);
                        if (baseObject == null || materialObject == null || baseObject.isLock() || materialObject.isLock()) {
                            c.sendEnableActions();
                            return;
                        }
                        int cost = 50000 * (baseObject.getGrade() + 1) << 1;
                        if (baseObject.getGrade() != materialObject.getGrade() || materialObject.getLevel() != 5 || player.getMeso() < (long)cost) break;
                        baseObject.setGrade((byte)(baseObject.getGrade() + 1));
                        baseObject.setLevel((byte)1);
                        if (baseObject.getGrade() >= 4) {
                            baseObject.setGrade((byte)4);
                        }
                        player.removeFamiliarsInfo(material);
                        player.gainMeso(-cost, true);
                        c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.upgradeFamiliar((int)player.getId())));
                        player.updateFamiliars();
                        break;
                    }
                    case 27: {
                        int familiarSN = (int)inPacket.decodeLong();
                        MonsterFamiliar familiar = player.getFamiliars().stream().filter(mf -> mf.getId() == familiarSN).findAny().orElse(null);
                        if (familiar == null) break;
                        int size = inPacket.decodeReversedVarints();
                        if (size < 4 || size > 13) {
                            player.dropMessage(1, "不適用於萌獸名稱的長度。");
                            return;
                        }
                        familiar.setName(inPacket.decodeString(size));
                        player.updateFamiliar(familiar);
                        break;
                    }
                    case 28: {
                        int familiarSN = (int)inPacket.decodeLong();
                        MonsterFamiliar familiar = player.getFamiliars().stream().filter(mf -> mf.getId() == familiarSN).findAny().orElse(null);
                        if (familiar == null || familiar.isLock()) break;
                        player.removeFamiliarsInfo(familiarSN);
                        player.updateFamiliars();
                        break;
                    }
                    case 29: {
                        int familiarSN = (int)inPacket.decodeLong();
                        MonsterFamiliar familiar = player.getFamiliars().stream().filter(m -> m.getId() == familiarSN).findAny().orElse(null);
                        int price = ServerConfig.FAMILIAR_SEAL_COST;
                        if (familiar == null || price <= 0) {
                            player.dropAlertNotice("未知錯誤1");
                            break;
                        }
                        if (familiar.isLock()) {
                            player.dropAlertNotice("鎖定狀態下不可使用。");
                            break;
                        }
                        if (player.getCSPoints(2) < price) {
                            player.dropAlertNotice("沒有足夠的楓點!");
                            break;
                        }
                        if (player.getSpace(2) < 1) {
                            player.dropAlertNotice("消耗欄空間不足!");
                            break;
                        }
                        player.modifyCSPoints(2, -price);
                        if (player.removeFamiliarsInfo(familiarSN) == null) break;
                        int monsterCardID = ii.getFamiliar(familiar.getFamiliar()).getMonsterCardID();
                        Item card = new Item(monsterCardID, (short) 0, (short) 1);
                        card.setFamiliarCard(familiar.createFamiliarCard());
                        MapleInventoryManipulator.addbyItem(c, card, false);
                        player.updateFamiliars();
                        break;
                    }
                    case 30: {
                        short slot = inPacket.decodeShort();
                        inPacket.decodeArr(2);
                        int index = inPacket.decodeInt();
                        Item item = player.getInventory(MapleInventoryType.CASH).getItem(slot);
                        MonsterFamiliar mf5 = player.getFamiliars().stream().filter(m -> m.getId() == index).findAny().orElse(null);
                        if (item == null || item.getItemId() != 5743003 || mf5 == null || mf5.isLock()) {
                            if (mf5 != null && mf5.isLock()) {
                                player.dropMessage(1, "鎖定狀態下不可使用。");
                            }
                            c.sendEnableActions();
                            return;
                        }
                        player.getTempValues().put("resetOptionsFamiliar", mf5);
                        mf5.initOptions();
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short)1, false, false);
                        player.updateFamiliars();
                        break;
                    }
                    case 31: {
                        List<MonsterFamiliar> familiars = player.getFamiliars();
                        Map<Integer, MonsterFamiliar> fMap = familiars.stream().filter(fm -> fm.hasFlag(1)).collect(Collectors.toMap(familiars::indexOf, Function.identity()));
                        fMap.values().forEach(fm -> fm.removeFlag(1));
                        c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.updateFamiliarInfo((int)player.getId(), (int)familiars.size(), fMap)));
                        break;
                    }
                    case 32: {
                        int selected = inPacket.decodeInt();
                        if (!player.setSelectedFamiliarTeamStat(selected)) {
                            return;
                        }
                        c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.changeTeamStatSelected((MapleCharacter)player, (int)selected)));
                        break;
                    }
                    case 33: {
                        int optionIndex = inPacket.decodeInt();
                        if (!player.changeFamiliarTeamStat(optionIndex)) {
                            return;
                        }
                        c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.changeTeamStats((MapleCharacter)player, (int)(player.getSelectedFamiliarTeamStat() - 1), player.getFamiliarTeamStats())));
                        break;
                    }
                    case 40: {
                        String secondPw = inPacket.decodeString(inPacket.decodeReversedVarints());
                        int familiarSN = inPacket.decodeInt();
                        boolean lock = inPacket.decodeBoolean();
                        if (c.CheckSecondPassword(secondPw)) {
                            MonsterFamiliar mf6 = player.getFamiliars().stream().filter(m -> m.getId() == familiarSN).findAny().orElse(null);
                            if (mf6 == null) {
                                c.sendEnableActions();
                                return;
                            }
                            mf6.setLock(lock);
                            player.updateFamiliars();
                            c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.familiarLock((int)player.getId(), (boolean)true, (int)familiarSN, (boolean)lock)));
                            break;
                        }
                        c.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.familiarLock((int)player.getId(), (boolean)false, (int)familiarSN, (boolean)lock)));
                        break;
                    }
                }
            }
            case FAMILIAR_LIFE: {
                int mode = inPacket.readerIndex();
                switch (mode) {
                    case 18: {
                        inPacket.decodeReversedVarints();
                        int gatherDuration = inPacket.decodeInt();
                        int nVal1 = inPacket.decodeInt();
                        Position position = inPacket.decodePosition();
                        Point oPos = new Point(position.getX(), position.getY());
                        position = inPacket.decodePosition();
                        Point mPos = new Point(position.getX(), position.getY());
                        try {
                            List<LifeMovementFragment> res = MovementParse.parseMovement(inPacket.toPacketReader(), 6);
                            if (res == null) {
                                log.error("ParseMovement Null - Familiar Card");
                                if (!player.isDebug()) return;
                                player.dropMessage(-1, "萌獸移動包出錯 - ParseMovement Null");
                                return;
                            }
                            if (player.getSummonedFamiliar() == null || res.size() <= 0) return;
                            MovementParse.updatePosition(res, player.getSummonedFamiliar(), 0);
                            byte[] packet = OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.familiarMove((int)player.getId(), (int)gatherDuration, (int)nVal1, (Point)oPos, (Point)mPos, res)).getData();
                            player.getMap().objectMove(player.getId(), player.getSummonedFamiliar(), packet);
                            return;
                        }
                        catch (Exception e) {
                            log.error("ParseMovement Error - Familiar Card", e);
                            if (!player.isDebug()) return;
                            player.dropMessage(-1, "萌獸移動包出錯 - ParseMovement Error");
                        }
                        return;
                    }
                    case 19: {
                        MapleMonster mob;
                        int anInt = inPacket.decodeInt();
                        int nCount = inPacket.decodeReversedVarints();
                        MonsterFamiliar summonedFamiliar = player.getSummonedFamiliar();
                        HashMap<Integer, List<Integer>> hashMap = new HashMap<Integer, List<Integer>>();
                        if (summonedFamiliar == null) return;
                        for (int i = 0; i < nCount && i < 8; i = (int)((byte)(i + 1))) {
                            int oid = inPacket.decodeInt();
                            mob = player.getMap().getMobObject(oid);
                            if (mob == null || !mob.isAlive()) continue;
                            hashMap.put(oid, Collections.singletonList((int)Math.min((double)player.getCalcDamage().getRandomDamage(player, true) * summonedFamiliar.getPad() * (Math.max(0.1, (double)(100 - mob.getStats().getPDRate())) / 100.0), 9.9999999E7)));
                        }
                        inPacket.decodeByte();
                        player.getMap().broadcastMessage(player, OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.familiarAttack((int)player.getId(), (int)anInt, hashMap)).getData(), true);
                        for (Map.Entry entry : hashMap.entrySet()) {
                            mob = player.getMap().getMobObject((Integer)entry.getKey());
                            if (mob == null) continue;
                            mob.damage(player, 0, ((Integer)((List)entry.getValue()).get(0)).intValue(), false);
                        }
                        return;
                    }
                    default: {
                        return;
                    }
                }
            }
        }
    }

    @Handler(op=InHeader.TMSExtraSystemRequest)
    public static void tmsExtraSystemRequest(MapleClient c, InPacket inPacket) {
        int magicNumber = inPacket.decodeInt();
        short type1 = inPacket.decodeShort();
        switch (type1) {
            case 1: {
                byte type2 = inPacket.decodeByte();
                if (type2 != 7) break;
                String secondPwd = inPacket.decodeString();
                byte iv = inPacket.decodeByte();
                byte slot = inPacket.decodeByte();
                if (!c.CheckSecondPassword(secondPwd)) {
                    c.getPlayer().dropMessage(1, "第二組密碼不正確");
                    c.sendEnableActions();
                    return;
                }
                Item item = c.getPlayer().getInventory(iv).getItem(slot);
                if (item != null) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(iv), slot, item.getQuantity(), true, false);
                    c.getPlayer().dropMessage(1, "你丟掉了" + item.getName() + "。");
                } else {
                    c.getPlayer().dropMessage(1, "發生未知錯誤");
                }
                c.sendEnableActions();
                break;
            }
            case 2: {
                byte type2 = inPacket.decodeByte();
                if (type2 != 3) break;
                byte iv = inPacket.decodeByte();
                byte slot = inPacket.decodeByte();
                Item item = c.getPlayer().getInventory(iv).getItem(slot);
                if (item == null || !ItemConstants.類型.寵物(item.getItemId()) || !ItemAttribute.RegressScroll.check(item.getCAttribute())) {
                    c.getPlayer().dropMessage(1, "發生未知錯誤");
                } else if (MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(iv), slot, item.getQuantity(), true, false) && MapleInventoryManipulator.addById(c, 2434162, 1, "用寵物交換")) {
                    c.getPlayer().dropMessage(1, "獲得寵物硬幣。");
                } else {
                    c.getPlayer().dropMessage(1, "發生未知錯誤");
                }
                c.sendEnableActions();
                break;
            }
            case 26: {
                short eqpPos = inPacket.decodeShort();
                short usePos = inPacket.decodeShort();
                byte type2 = inPacket.decodeByte();
                if (type2 != 1) break;
                if (eqpPos <= 0) {
                    c.sendEnableActions();
                    return;
                }
                Equip eq = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(eqpPos);
                Item toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(usePos);
                if (eq == null || toUse == null || toUse.getQuantity() < 1 || eq.getState(false) < 17 || EnhanceResultType.EQUIP_MARK.check(eq.getEnchantBuff())) {
                    c.sendEnableActions();
                    return;
                }
                if (toUse.getItemId() != 5062026) {
                    if (c.getPlayer().isAdmin()) {
                        c.getPlayer().dropDebugMessage(2, "[使用方塊] 此方塊未處理");
                    }
                    c.sendEnableActions();
                    return;
                }
                c.getPlayer().updateOneQuestInfo(65132, "n", String.valueOf(eq.getSN()));
                int line = Randomizer.rand(0, eq.getPotential(3, false) > 0 ? 2 : 1);
                c.getPlayer().updateOneQuestInfo(65132, "i", String.valueOf(line));
                c.write(OverseasPacket.getUniCubeRes((short)inPacket.decodeShort(), (int)inPacket.decodeInt(), (int)line));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, toUse.getPosition(), (short)1, false);
                break;
            }
            case 27: {
                byte type2 = inPacket.decodeByte();
                inPacket.decodeByte();
                switch (type2) {
                    case 1: {
                        int line = Integer.parseInt(c.getPlayer().getOneInfo(65132, "i"));
                        long sn = Long.parseLong(c.getPlayer().getOneInfo(65132, "n"));
                        Equip eq = null;
                        for (Item it : c.getPlayer().getInventory(MapleInventoryType.EQUIP).list()) {
                            if (sn != (long)it.getSN()) continue;
                            eq = (Equip)it;
                            break;
                        }
                        if (eq != null) {
                            eq.useCube(5062026, c.getPlayer(), line + 1);
                        }
                    }
                    case 0: {
                        c.write(OverseasPacket.getUniCubeRes((short)inPacket.decodeShort(), (int)inPacket.decodeInt(), (int)1));
                        c.getPlayer().removeInfoQuest(65132);
                    }
                }
                break;
            }
            case 37: {
                MapleStatEffect attackEffect;
                int skillId = inPacket.decodeInt();
                MapleCharacter chr = c.getPlayer();
                if (chr == null || skillId != 175121008 && skillId != 400051086) break;
                SecondaryStatValueHolder holder = chr.getBuffStatValueHolder(SecondaryStat.IndieCooltimeReduce, skillId);
                if (holder == null || holder.value < 1) {
                    chr.dispelBuff(skillId);
                    break;
                }
                if (--holder.value < 1) {
                    chr.dispelBuff(skillId);
                } else {
                    chr.send(BuffPacket.giveBuff(chr, holder.effect, Collections.singletonMap(SecondaryStat.IndieCooltimeReduce, holder.sourceID)));
                }
                Skill skill = null;
                skill = skillId == 175121008 ? SkillFactory.getSkill(175121017) : SkillFactory.getSkill(400051087);
                if (skill == null || (attackEffect = chr.getSkillEffect(skill.getId())) == null) break;
                MapleForceFactory mff = MapleForceFactory.getInstance();
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(mff.getMapleForce(chr, attackEffect, 0, 0, Collections.emptyList(), chr.getPosition())), true);
                break;
            }
            case 38: {
                int powerType = inPacket.decodeInt();
                if (powerType == 0) break;
                int godPower = 0;
                int time = 30000;
                if (c.getPlayer().getSkillLevel(175110010) <= 0) break;
                godPower = (Integer)c.getPlayer().getTempValues().getOrDefault("GodPower", 0);
                --godPower;
                godPower = Math.max(0, godPower);
                c.getPlayer().getTempValues().put("GodPower", godPower);
                if (c.getPlayer().getSkillLevel(175120039) <= 0) break;
                time += 10000;
                break;
            }
            case 39: {
                MapleCharacter chr = c.getPlayer();
                if (chr == null) break;
                int godSkillMacroLine = inPacket.decodeInt();
                int godSkillMacroNumber = inPacket.decodeInt();
                int godSkillMacroSkillType = inPacket.decodeInt();
                if (godSkillMacroLine < 1 || godSkillMacroLine > 6 || godSkillMacroNumber < 1 || godSkillMacroNumber > 4 || godSkillMacroSkillType < 0 || godSkillMacroSkillType > 2) break;
                chr.updateOneInfo(65899, String.format("%d-%d", godSkillMacroLine, godSkillMacroNumber), godSkillMacroSkillType == 0 ? null : String.valueOf(godSkillMacroSkillType));
                break;
            }
        }
    }

    @Handler(op=InHeader.TMSEquipmentEnchantRequest)
    public static void tmsEquipmentEnchantRequest(MapleClient c, InPacket inPacket) {
        int magicMar = inPacket.decodeInt();
        short cube_I = inPacket.decodeShort();
        block0 : switch (cube_I) {
            case 1: {
                byte selType = inPacket.decodeByte();
                int unk = inPacket.decodeInt();
                byte aByte = inPacket.decodeByte();
                short opcodeCube = inPacket.decodeShort();
                inPacket.decodeByte();
                inPacket.decodeByte();
                inPacket.decodeByte();
                byte actionOpcode = inPacket.decodeByte();
                c.announce(InventoryPacket.showPotentialResetUseing(actionOpcode, 5062030));
                break;
            }
            case 0: {
                byte cube_II = inPacket.decodeByte();
                switch (cube_II) {
                    case 1: {
                        int src = inPacket.decodeInt();
                        inPacket.decodeByte();
                        Equip eq = (Equip)c.getPlayer().getInventory(src > 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED).getItem((short)src);
                        if (eq == null || EnhanceResultType.EQUIP_MARK.check(eq.getEnchantBuff())) {
                            c.sendEnableActions();
                            return;
                        }
                        MapleInventory iv = c.getPlayer().getInventory(MapleInventoryType.SETUP);
                        if (iv.findById(3994895) == null && iv.findById(3996222) == null) {
                            c.sendEnableActions();
                            return;
                        }
                        OverseasHandler.showAnimaCubeCost(inPacket.decodeShort(), inPacket.decodeInt(), c, eq);
                        break;
                    }
                    case 3: {
                        if (inPacket.getUnreadAmount() <= 11) {
                            short slot = inPacket.decodeShort();
                            short dst = inPacket.decodeShort();
                            Item toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
                            Equip eq = (Equip)c.getPlayer().getInventory(dst > 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED).getItem(dst);
                            if (toUse.getItemId() != 5062017 && toUse.getItemId() != 5062019 && toUse.getItemId() != 5062020 && toUse.getItemId() != 5062021 && toUse.getItemId() != 5062030) {
                                if (c.getPlayer().isAdmin()) {
                                    c.getPlayer().dropDebugMessage(2, "[使用方塊] 此方塊未處理");
                                }
                                c.sendEnableActions();
                                return;
                            }
                            if (EnhanceResultType.EQUIP_MARK.check(eq.getEnchantBuff()) || eq.getState(false) < 17) {
                                c.sendEnableActions();
                                return;
                            }
                            inPacket.decodeByte();
                            short opcodeCube = inPacket.decodeShort();
                            inPacket.decodeByte();
                            inPacket.decodeByte();
                            inPacket.decodeByte();
                            byte actionOpcode = inPacket.decodeByte();
                            if (!eq.useCube(opcodeCube, actionOpcode, toUse.getItemId(), c.getPlayer())) break block0;
                            MapleInventoryManipulator.removeFromSlot(c.getPlayer().getClient(), MapleInventoryType.CASH, slot, (short)1, false, true);
                            break;
                        }
                        short dst = (short)inPacket.decodeInt();
                        inPacket.decodeInt();
                        inPacket.decodeByte();
                        Equip eq = (Equip)c.getPlayer().getInventory(dst > 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED).getItem(dst);
                        if (eq == null || EnhanceResultType.EQUIP_MARK.check(eq.getEnchantBuff())) {
                            c.sendEnableActions();
                            return;
                        }
                        MapleInventory iv = c.getPlayer().getInventory(MapleInventoryType.SETUP);
                        if (iv.findById(3994895) == null && iv.findById(3996222) == null) {
                            c.sendEnableActions();
                            return;
                        }
                        if (OverseasHandler.useAnimaCube(inPacket.decodeShort(), inPacket.decodeInt(), c, eq)) break block0;
                        c.sendEnableActions();
                        break;
                    }
                    case 7: {
                        short cubeSlot = (short)inPacket.decodeInt();
                        short equipSlot = (short)inPacket.decodeInt();
                        Item useCube = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(cubeSlot);
                        Equip equip = (Equip)c.getPlayer().getInventory(equipSlot > 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED).getItem(equipSlot);
                        inPacket.decodeInt();
                        inPacket.decodeBoolean();
                        short opcode = inPacket.decodeShort();
                        inPacket.decodeByte();
                        inPacket.decodeByte();
                        inPacket.decodeByte();
                        byte cubeCrc = inPacket.decodeByte();
                        c.announce(InventoryPacket.showPotentialReset(c.getPlayer().getId(), true, useCube.getItemId(), 0, equip.getItemId()));
                        equip.useCube(useCube.getItemId(), c.getPlayer());
                        c.getPlayer().removeItem(useCube.getItemId(), 1);
                        c.announce(InventoryPacket.showPotentialResetUseing(cubeCrc, useCube.getQuantity()));
                        break;
                    }
                    case 15: {
                        Equip eq;
                        Item toUse;
                        if (inPacket.getUnreadAmount() == 19) {
                            short slot = inPacket.decodeShort();
                            short dst = inPacket.decodeShort();
                            toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
                            eq = (Equip)c.getPlayer().getInventory(dst > 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED).getItem(dst);
                        } else {
                            short dst = (short)inPacket.decodeLong();
                            toUse = c.getPlayer().getInventory(MapleInventoryType.SETUP).findById(3996222);
                            eq = (Equip)c.getPlayer().getInventory(dst > 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED).getItem(dst);
                        }
                        if (eq == null || toUse == null || EnhanceResultType.EQUIP_MARK.check(eq.getEnchantBuff())) {
                            c.sendEnableActions();
                            return;
                        }
                        int code = inPacket.decodeInt();
                        short slot = (short)inPacket.decodeInt();
                        inPacket.decodeByte();
                        Item rock = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
                        if (rock == null || rock.getQuantity() < 1) {
                            c.sendEnableActions();
                            return;
                        }
                        int rockId = rock.getItemId();
                        int toLock = 0;
                        boolean free = false;
                        if (rockId == 4132000) {
                            free = true;
                        } else {
                            toLock = code + 1;
                        }
                        boolean used = false;
                        if (toUse.getItemId() == 3996222) {
                            used = OverseasHandler.useAnimaCube(inPacket.decodeShort(), inPacket.decodeInt(), c, eq, toLock, free);
                        } else if (toUse.getItemId() == 5062017 || toUse.getItemId() == 5062030) {
                            if (eq.getState(false) < 17) {
                                c.sendEnableActions();
                                break;
                            }
                            boolean bl = used = eq.useCube(inPacket.decodeShort(), inPacket.decodeInt(), toUse.getItemId(), c.getPlayer(), toLock) && !free;
                            if (used) {
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, toUse.getPosition(), (short)1, false);
                            }
                        }
                        if (used) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short)1, false);
                            break;
                        }
                        c.sendEnableActions();
                        break;
                    }
                }
                break;
            }
            default: {
                c.getPlayer().dropMessage(1, "該道具還未賦予潛能");
                c.sendEnableActions();
            }
        }
    }

    private static boolean showAnimaCubeCost(short opcode, int action, MapleClient c, Equip eq) {
        return OverseasHandler.animaCubeAction(opcode, action, c, eq, false, 0, false);
    }

    private static boolean useAnimaCube(short opcode, int action, MapleClient c, Equip eq) {
        return OverseasHandler.animaCubeAction(opcode, action, c, eq, true, 0, false);
    }

    private static boolean useAnimaCube(short opcode, int action, MapleClient c, Equip eq, int toLock, boolean free) {
        return OverseasHandler.animaCubeAction(opcode, action, c, eq, true, toLock, false);
    }

    private static boolean animaCubeAction(short opcode, int action, MapleClient c, Equip eq, boolean use, int toLock, boolean free) {
        Date dateQuest;
        String[] potStates = new String[]{"n", "r", "e", "u", "l"};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Date dateNow = new Date();
        try {
            dateQuest = c.getPlayer().getOneInfo(28899, "d") != null ? fmt.parse(c.getPlayer().getOneInfo(28899, "d")) : new Date();
        }
        catch (ParseException ex) {
            dateQuest = new Date();
        }
        if (Integer.parseInt(sdf.format(dateNow)) - Integer.parseInt(sdf.format(dateQuest)) > 0) {
            c.getPlayer().updateOneInfo(28899, "d", sdf.format(dateNow), false);
            for (String s : potStates) {
                c.getPlayer().updateOneInfo(28899, s, "0", false);
            }
        }
        HashMap<String, Integer> animaCubePotTimes = new HashMap<String, Integer>();
        for (String s : potStates) {
            animaCubePotTimes.put(s, c.getPlayer().getOneInfo(28899, s) == null ? 0 : Integer.parseInt(c.getPlayer().getOneInfo(28899, s)));
        }
        int potentialState = eq.getState(false);
        if (potentialState >= 17) {
            potentialState -= 16;
        }
        if (eq.getCurrentUpgradeCount() == 0 && eq.getRestUpgradeCount() == 0 && !ItemConstants.類型.副手(eq.getItemId()) && !ItemConstants.類型.能源(eq.getItemId()) && !ItemConstants.類型.特殊潛能道具(eq.getItemId()) || MapleItemInformationProvider.getInstance().isCash(eq.getItemId()) || ItemConstants.類型.無法潛能道具(eq.getItemId())) {
            c.getPlayer().dropMessage(1, "在這道具無法使用。");
            c.write(OverseasPacket.getAnimaCubeRes((short)opcode, (int)action, (int)3, (long)0L));
            return false;
        }
        if (ItemConstants.類型.特殊潛能道具(eq.getItemId()) && potentialState == 0) {
            c.getPlayer().dropMessage(1, "此道具只能透過專用潛能捲軸來進行潛能設定.請設定潛能後再使用.");
            c.write(OverseasPacket.getAnimaCubeRes((short)opcode, (int)action, (int)3, (long)0L));
            return false;
        }
        String state = potStates[potentialState];
        int value = 0;
        if (use) {
            value = 2;
            long price = ItemConstants.方塊.getMapleCubeCost((int)((Integer)animaCubePotTimes.get(state)), (int)potentialState);
            if (c.getPlayer().getMeso() < price && !free) {
                return false;
            }
            if (potentialState == 0) {
                eq.renewPotential(false);
                eq.magnify();
                c.getPlayer().forceUpdateItem(eq);
                eq.addAttribute(ItemAttribute.TradeBlock.getValue());
            } else if (!eq.useCube(3996222, c.getPlayer(), toLock)) {
                return false;
            }
            eq.addAttribute(ItemAttribute.AnimaCube.getValue());
            if (!free) {
                c.getPlayer().gainMeso(-price, false);
            }
            c.getPlayer().updateOneInfo(28899, state, String.valueOf((Integer)animaCubePotTimes.get(state) + 1), false);
            for (String s : potStates) {
                animaCubePotTimes.put(s, c.getPlayer().getOneInfo(28899, s) == null ? 0 : Integer.parseInt(c.getPlayer().getOneInfo(28899, s)));
            }
            potentialState = eq.getState(false);
            if (potentialState >= 17) {
                potentialState -= 16;
            }
            state = potStates[potentialState];
        }
        c.write(OverseasPacket.getAnimaCubeRes((short)opcode, (int)action, (int)value, (long)ItemConstants.方塊.getMapleCubeCost((int)((Integer)animaCubePotTimes.get(state)), (int)potentialState)));
        return true;
    }
}

