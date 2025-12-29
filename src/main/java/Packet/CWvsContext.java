/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Packet;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleExpStat;
import Client.MapleQuestStatus;
import Client.MapleTraitType;
import Client.MessageOption;
import Client.SecondaryStat;
import Net.server.life.MobSkill;
import Opcode.header.OutHeader;
import Packet.PacketHelper;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

public class CWvsContext
extends MapleClient {
    private static MapleClient c;

    public static byte[] onFieldSetVariable(String key, String value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FieldValue.getValue());
        mplew.writeMapleAsciiString(key);
        mplew.writeMapleAsciiString(value);
        return mplew.getPacket();
    }

    public static byte[] giveDisease(Map<SecondaryStat, Pair<Integer, Integer>> statups, MobSkill skill, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ForcedStatSet.getValue());
        List<Pair<SecondaryStat, Pair<Integer, Integer>>> newstatups = PacketHelper.sortBuffStats(statups);
        PacketHelper.writeBuffMask(mplew, newstatups);
        for (Pair<SecondaryStat, Pair<Integer, Integer>> pair : newstatups) {
            if (pair.getLeft().canStack() || pair.getLeft().isSpecialBuff()) continue;
            if (SecondaryStat.isEncode4Byte(statups)) {
                if (pair.left == SecondaryStat.ReturnTeleport) {
                    mplew.writeShort(chr.getPosition().y);
                    mplew.writeShort(chr.getPosition().x);
                } else {
                    mplew.writeInt(pair.getLeft().getX() != 0 ? pair.getLeft().getX() : ((Integer)pair.getRight().left).intValue());
                }
            } else {
                mplew.writeShort((Integer)pair.getRight().left);
            }
            mplew.writeShort(skill.getSkillId());
            mplew.writeShort(skill.getSkillId() == 237 ? 0 : skill.getSkillLevel());
            mplew.writeInt(skill.getSkillId() == 237 ? 0 : (Integer)((Pair)pair.right).right);
        }
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(0);
        if (statups.containsKey(SecondaryStat.Slow)) {
            mplew.write(0);
        }
        if (statups.containsKey(SecondaryStat.BlackMageCreate)) {
            mplew.writeInt(10);
        }
        if (statups.containsKey(SecondaryStat.BlackMageDestroy)) {
            mplew.writeInt(15);
        }
        if (statups.containsKey(SecondaryStat.Stigma)) {
            mplew.writeInt(7);
        }
        mplew.writeInt(0);
        for (Pair<SecondaryStat, Pair<Integer, Integer>> pair : newstatups) {
            if (((SecondaryStat)pair.left).canStack() || !((SecondaryStat)pair.left).isSpecialBuff()) continue;
            mplew.writeInt((Integer)((Pair)pair.right).left);
            mplew.writeShort(skill.getSkillId());
            mplew.writeShort(skill.getSkillId() == 237 ? 0 : skill.getSkillLevel());
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeShort((Integer)((Pair)pair.right).right);
        }
        if (statups.containsKey(SecondaryStat.VampDeath)) {
            mplew.writeInt(0);
        }
        if (statups.containsKey(SecondaryStat.OutSide)) {
            mplew.writeInt(1000);
        }
        if (statups.containsKey(SecondaryStat.BossWill_Infection)) {
            mplew.writeInt(30);
        }
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(true);
        mplew.write(true);
        mplew.writeInt(0);
        mplew.writeZeroBytes(100);
        return mplew.getPacket();
    }

    public static byte[] sendMessage(int type, MessageOption option) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_Message.getValue());
        mplew.write(type);
        block0 : switch (type) {
            case 0: {
                mplew.writeInt(0);
                mplew.write(0);
                mplew.write(option.getMode());
                switch (option.getMode() + 5) {
                    case 5: {
                        mplew.writeInt(option.getObjectId());
                        mplew.writeInt(option.getAmount());
                        mplew.write(0);
                        break;
                    }
                    case 6: {
                        mplew.write(0);
                        mplew.writeInt(option.getLongGain());
                        mplew.writeShort(0);
                    }
                    case 7: {
                        mplew.writeInt(0);
                        mplew.writeLong(0L);
                        break;
                    }
                    case 11: {
                        mplew.writeInt(0);
                        break;
                    }
                    case 13: {
                        mplew.writeInt(0);
                        mplew.writeShort(0);
                    }
                }
                break;
            }
            case 1: {
                MapleQuestStatus quest = option.getQuestStatus();
                mplew.writeInt(quest.getQuest().getId());
                mplew.write(quest.getStatus());
                switch (quest.getStatus()) {
                    case 0: {
                        mplew.write(1);
                        break block0;
                    }
                    case 1: {
                        mplew.writeMapleAsciiString(quest.getCustomData() != null ? quest.getCustomData() : "");
                        break block0;
                    }
                    case 2: {
                        mplew.writeLong(PacketHelper.getTime(quest.getCompletionTime()));
                        break block0;
                    }
                }
                System.out.println("未知LP_Message(" + type + ")值:" + quest.getStatus());
                break;
            }
            case 2: {
                mplew.writeInt(option.getObjectId());
                break;
            }
            case 3: {
                boolean nexon_encry;
                Pair expStat;
                mplew.write(option.getColor());
                mplew.writeInt(option.getLongGain());
                mplew.writeInt(0);
                mplew.write(option.getOnQuest());
                mplew.writeInt(option.getDiseaseType());
                if (option.getDiseaseType() != 0) {
                    mplew.writeLong(option.getExpLost());
                }
                Map<MapleExpStat, Object> expStats = option.getExpGainData();
                long expMask = 0L;
                for (MapleExpStat statupdate : expStats.keySet()) {
                    expMask |= statupdate.getFlag();
                }
                mplew.writeLong(expMask);
                if (expStats.getOrDefault((Object)MapleExpStat.活動獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.活動獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.活動組隊經驗值, null) != null) {
                    mplew.write((Byte)expStats.get((Object)MapleExpStat.活動組隊經驗值));
                }
                int nQuestBonusRate = 0;
                if (option.getOnQuest()) {
                    mplew.write(nQuestBonusRate);
                }
                if (nQuestBonusRate > 0) {
                    mplew.write(0);
                }
                if (expStats.getOrDefault((Object)MapleExpStat.結婚紅利經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.結婚紅利經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.組隊額外經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.組隊額外經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.道具裝備紅利經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.道具裝備紅利經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.高級服務贈送經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.高級服務贈送經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.彩虹週獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.彩虹週獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.爆發獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.爆發獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.秘藥額外經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.秘藥額外經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.極限屬性經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.極限屬性經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.加持獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.加持獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.休息獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.休息獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.道具獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.道具獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.依道具趴增加經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.依道具趴增加經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.超值包獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.超值包獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.依道具的組隊任務趴增加經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.依道具的組隊任務趴增加經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.累積狩獵數紅利經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.累積狩獵數紅利經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.家族經驗值獎勵, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.家族經驗值獎勵));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.冷凍勇士經驗值獎勵, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.冷凍勇士經驗值獎勵));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.燃燒場地獎勵經驗, null) != null) {
                    expStat = (Pair)expStats.get((Object)MapleExpStat.燃燒場地獎勵經驗);
                    mplew.writeLong((Long)expStat.getLeft());
                    mplew.writeInt((Integer)expStat.getRight());
                }
                if (expStats.getOrDefault((Object)MapleExpStat.HP風險經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.HP風險經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.場地紅利經驗, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.場地紅利經驗));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.累計打獵數量獎勵經驗, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.累計打獵數量獎勵經驗));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.活動獎勵經驗值2, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.活動獎勵經驗值2));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.網咖摯友獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.網咖摯友獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.場地紅利經驗2, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.場地紅利經驗2));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.超級小豬幸運_攻擊額外經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.超級小豬幸運_攻擊額外經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.伺服器計量條活動獎勵經驗值, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.伺服器計量條活動獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.未知2, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.未知2));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.組隊經驗值增加x趴, null) != null) {
                    expStat = (Pair)expStats.get((Object)MapleExpStat.道具名經驗值);
                    mplew.writeLong((Long)expStat.getLeft());
                }
                if (expStats.getOrDefault((Object)MapleExpStat.蛋糕vs派餅_EXP紅利, null) != null) {
                    mplew.writeLong((Long)expStats.get((Object)MapleExpStat.蛋糕vs派餅_EXP紅利));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.道具名經驗值, null) != null) {
                    expStat = (Pair)expStats.get((Object)MapleExpStat.道具名經驗值);
                    mplew.writeLong((Long)expStat.getLeft());
                    mplew.writeInt((Integer)expStat.getRight());
                }
                if (expStats.getOrDefault((Object)MapleExpStat.組隊經驗值增加x趴, null) != null) {
                    expStat = (Pair)expStats.get((Object)MapleExpStat.道具名經驗值);
                    mplew.writeInt((Integer)expStat.getRight());
                }
                if (expStats.getOrDefault((Object)MapleExpStat.寵物訓練紅利經驗值, null) != null) {
                    mplew.writeInt((Integer)expStats.get((Object)MapleExpStat.寵物訓練紅利經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.組合道具獎勵經驗值, null) != null) {
                    mplew.writeInt((Integer)expStats.get((Object)MapleExpStat.組合道具獎勵經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.組合道具獎勵組隊經驗值, null) != null) {
                    mplew.writeInt((Integer)expStats.get((Object)MapleExpStat.組合道具獎勵組隊經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.伺服器加持經驗值, null) != null) {
                    mplew.writeInt((Integer)expStats.get((Object)MapleExpStat.伺服器加持經驗值));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.累積狩獵數紅利經驗值2, null) != null) {
                    mplew.writeInt((Integer)expStats.get((Object)MapleExpStat.累積狩獵數紅利經驗值2));
                }
                if (expStats.getOrDefault((Object)MapleExpStat.艾爾達斯還原追加經驗值, null) != null) {
                    mplew.writeInt((Integer)expStats.get((Object)MapleExpStat.艾爾達斯還原追加經驗值));
                }
                int 遠征隊Bonus經驗值 = 0;
                int 遠征隊關係效果Bonus經驗值 = 0;
                int expedExpMask = 0;
                if (遠征隊Bonus經驗值 > 0) {
                    expedExpMask |= 1;
                }
                if (遠征隊關係效果Bonus經驗值 > 0) {
                    expedExpMask |= 2;
                }
                mplew.writeInt(expedExpMask);
                if ((expedExpMask & 1) != 0) {
                    mplew.writeInt(遠征隊Bonus經驗值);
                }
                if ((expedExpMask & 2) != 0) {
                    mplew.writeInt(遠征隊關係效果Bonus經驗值);
                }
                if (!(nexon_encry = true)) break;
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(8);
                break;
            }
            case 4: {
                mplew.writeShort(option.getJob());
                mplew.write(option.getAmount());
                break;
            }
            case 5: {
                mplew.writeInt(option.getAmount());
                break;
            }
            case 6: {
                mplew.writeLong(option.getLongGain());
                mplew.writeInt(option.getMode());
                if (option.getMode() != 24) break;
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
            case 7: {
                mplew.writeInt(option.getAmount());
                break;
            }
            case 8: {
                mplew.writeInt(option.getAmount());
                mplew.writeInt(option.getAmount());
                mplew.writeInt(option.getAmount());
                break;
            }
            case 9: {
                mplew.writeInt(option.getObjectId());
                break;
            }
            case 10: {
                mplew.write(option.getIntegerData().length);
                for (int itemID : option.getIntegerData()) {
                    mplew.writeInt(itemID);
                }
                break;
            }
            case 11: {
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
            case 12: 
            case 13: 
            case 14: {
                mplew.writeInt(option.getObjectId());
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
            case 15: {
                boolean type2 = false;
                mplew.writeBool(type2);
                break;
            }
            case 16: {
                int v9 = 0;
                mplew.write(v9);
                if (v9 <= 0) break;
                do {
                    mplew.writeMapleAsciiString("");
                } while (--v9 < 0);
                break;
            }
            case 17: {
                mplew.write(option.getIntegerData().length);
                for (int value : option.getIntegerData()) {
                    mplew.writeInt(value);
                }
                break;
            }
            case 18: {
                mplew.write(option.getIntegerData().length);
                for (int skillID : option.getIntegerData()) {
                    mplew.writeInt(skillID);
                }
                break;
            }
            case 19: {
                MapleTraitType[] traitTypes;
                int[] data = option.getIntegerData();
                if (data == null) {
                    data = new int[]{};
                }
                long mask = 0L;
                for (MapleTraitType traitType : traitTypes = MapleTraitType.values()) {
                    if (data.length < traitType.ordinal() + 1 || data[traitType.ordinal()] <= 0) continue;
                    mask |= traitType.getStat().getValue();
                }
                mplew.writeLong(mask);
                for (int i = 0; i < traitTypes.length; ++i) {
                    if (data.length < i + 1 || data[i] <= 0) continue;
                    mplew.writeInt(data[i]);
                }
                break;
            }
            case 20: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 21: {
                mplew.writeLong(option.getMask());
                break;
            }
            case 23: {
                break;
            }
            case 24: {
                break;
            }
            case 25: {
                mplew.writeInt(option.getAmount());
                mplew.writeInt(0);
                break;
            }
            case 26: {
                mplew.writeMapleAsciiString(option.getText());
                mplew.writeMapleAsciiString(option.getText2());
                break;
            }
            case 27: {
                mplew.write(0);
                break;
            }
            case 28: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 29: {
                mplew.writeMapleAsciiString(option.getText());
            }
            case 30: {
                int type2 = 0;
                mplew.write(type2);
                switch (type2) {
                    case 0: {
                        mplew.writeShort(0);
                        mplew.writeInt(0);
                        mplew.writeInt(0);
                        break block0;
                    }
                    case 1: {
                        mplew.writeInt(0);
                        break block0;
                    }
                    case 6: {
                        mplew.writeInt(0);
                        mplew.writeInt(0);
                        mplew.writeInt(0);
                        break block0;
                    }
                }
                break;
            }
            case 31: {
                mplew.writeInt(option.getObjectId());
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
            case 32: {
                mplew.writeInt(0);
                break;
            }
            case 33: {
                mplew.writeInt(option.getAmount());
                break;
            }
            case 34: {
                mplew.writeInt(option.getAmount());
                break;
            }
            case 35: {
                mplew.writeBool(option.getMode() > 0);
                if (option.getMode() > 0) {
                    int color = Randomizer.rand(1, 6);
                    mplew.writeInt(option.getCombo());
                    mplew.writeInt(option.getObjectId());
                    mplew.writeInt(color);
                    mplew.writeInt(option.getAmount());
                    break;
                }
                mplew.writeLong(option.getLongExp());
                mplew.writeInt(0);
                mplew.writeInt(option.getCombo());
                mplew.writeInt(option.getColor());
                break;
            }
            case 36: {
                break;
            }
            case 37: {
                break;
            }
            case 38: {
                mplew.writeInt(option.getObjectId());
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
            case 39: {
                break;
            }
            case 41: {
                mplew.writeInt(option.getObjectId());
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
            case 42: {
                mplew.writeInt(0);
                break;
            }
            case 43: {
                break;
            }
            case 44: {
                int type2 = 0;
                mplew.writeInt(type2);
                for (int i = 0; i < type2; ++i) {
                    mplew.writeInt(0);
                }
                break;
            }
            case 45: {
                break;
            }
            case 46: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeLong(0L);
                break;
            }
            case 48: {
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
            case 50: {
                mplew.writeMapleAsciiString(option.getText());
                break;
            }
        }
        return mplew.getPacket();
    }

    public static byte[] sendHexaEnforcementInfo() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_HEXA_ENFORCEMENTINFO.getValue());
        mplew.writeInt(4);
        mplew.writeInt(0);
        mplew.writeInt(5);
        mplew.writeInt(100);
        mplew.writeInt(1);
        mplew.writeInt(3);
        mplew.writeInt(50);
        mplew.writeInt(2);
        mplew.writeInt(4);
        mplew.writeInt(75);
        mplew.writeInt(3);
        mplew.writeInt(7);
        mplew.writeInt(125);
        mplew.writeInt(4);
        mplew.writeInt(0);
        mplew.writeInt(29);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(30);
        mplew.writeInt(2);
        mplew.writeInt(1);
        mplew.writeInt(35);
        mplew.writeInt(3);
        mplew.writeInt(1);
        mplew.writeInt(40);
        mplew.writeInt(4);
        mplew.writeInt(2);
        mplew.writeInt(45);
        mplew.writeInt(5);
        mplew.writeInt(2);
        mplew.writeInt(50);
        mplew.writeInt(6);
        mplew.writeInt(2);
        mplew.writeInt(55);
        mplew.writeInt(7);
        mplew.writeInt(3);
        mplew.writeInt(60);
        mplew.writeInt(8);
        mplew.writeInt(3);
        mplew.writeInt(65);
        mplew.writeInt(9);
        mplew.writeInt(10);
        mplew.writeInt(200);
        mplew.writeInt(10);
        mplew.writeInt(3);
        mplew.writeInt(80);
        mplew.writeInt(11);
        mplew.writeInt(3);
        mplew.writeInt(90);
        mplew.writeInt(12);
        mplew.writeInt(4);
        mplew.writeInt(100);
        mplew.writeInt(13);
        mplew.writeInt(4);
        mplew.writeInt(110);
        mplew.writeInt(14);
        mplew.writeInt(4);
        mplew.writeInt(120);
        mplew.writeInt(15);
        mplew.writeInt(4);
        mplew.writeInt(130);
        mplew.writeInt(16);
        mplew.writeInt(4);
        mplew.writeInt(140);
        mplew.writeInt(17);
        mplew.writeInt(4);
        mplew.writeInt(150);
        mplew.writeInt(18);
        mplew.writeInt(5);
        mplew.writeInt(160);
        mplew.writeInt(19);
        mplew.writeInt(15);
        mplew.writeInt(350);
        mplew.writeInt(20);
        mplew.writeInt(5);
        mplew.writeInt(170);
        mplew.writeInt(21);
        mplew.writeInt(5);
        mplew.writeInt(180);
        mplew.writeInt(22);
        mplew.writeInt(5);
        mplew.writeInt(190);
        mplew.writeInt(23);
        mplew.writeInt(5);
        mplew.writeInt(200);
        mplew.writeInt(24);
        mplew.writeInt(5);
        mplew.writeInt(210);
        mplew.writeInt(25);
        mplew.writeInt(6);
        mplew.writeInt(220);
        mplew.writeInt(26);
        mplew.writeInt(6);
        mplew.writeInt(230);
        mplew.writeInt(27);
        mplew.writeInt(6);
        mplew.writeInt(240);
        mplew.writeInt(28);
        mplew.writeInt(7);
        mplew.writeInt(250);
        mplew.writeInt(29);
        mplew.writeInt(20);
        mplew.writeInt(500);
        mplew.writeInt(1);
        mplew.writeInt(29);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(15);
        mplew.writeInt(2);
        mplew.writeInt(1);
        mplew.writeInt(18);
        mplew.writeInt(3);
        mplew.writeInt(1);
        mplew.writeInt(20);
        mplew.writeInt(4);
        mplew.writeInt(1);
        mplew.writeInt(23);
        mplew.writeInt(5);
        mplew.writeInt(1);
        mplew.writeInt(25);
        mplew.writeInt(6);
        mplew.writeInt(1);
        mplew.writeInt(28);
        mplew.writeInt(7);
        mplew.writeInt(2);
        mplew.writeInt(30);
        mplew.writeInt(8);
        mplew.writeInt(2);
        mplew.writeInt(33);
        mplew.writeInt(9);
        mplew.writeInt(5);
        mplew.writeInt(100);
        mplew.writeInt(10);
        mplew.writeInt(2);
        mplew.writeInt(40);
        mplew.writeInt(11);
        mplew.writeInt(2);
        mplew.writeInt(45);
        mplew.writeInt(12);
        mplew.writeInt(2);
        mplew.writeInt(50);
        mplew.writeInt(13);
        mplew.writeInt(2);
        mplew.writeInt(55);
        mplew.writeInt(14);
        mplew.writeInt(2);
        mplew.writeInt(60);
        mplew.writeInt(15);
        mplew.writeInt(2);
        mplew.writeInt(65);
        mplew.writeInt(16);
        mplew.writeInt(2);
        mplew.writeInt(70);
        mplew.writeInt(17);
        mplew.writeInt(2);
        mplew.writeInt(75);
        mplew.writeInt(18);
        mplew.writeInt(3);
        mplew.writeInt(80);
        mplew.writeInt(19);
        mplew.writeInt(8);
        mplew.writeInt(175);
        mplew.writeInt(20);
        mplew.writeInt(3);
        mplew.writeInt(85);
        mplew.writeInt(21);
        mplew.writeInt(3);
        mplew.writeInt(90);
        mplew.writeInt(22);
        mplew.writeInt(3);
        mplew.writeInt(95);
        mplew.writeInt(23);
        mplew.writeInt(3);
        mplew.writeInt(100);
        mplew.writeInt(24);
        mplew.writeInt(3);
        mplew.writeInt(105);
        mplew.writeInt(25);
        mplew.writeInt(3);
        mplew.writeInt(110);
        mplew.writeInt(26);
        mplew.writeInt(3);
        mplew.writeInt(115);
        mplew.writeInt(27);
        mplew.writeInt(3);
        mplew.writeInt(120);
        mplew.writeInt(28);
        mplew.writeInt(4);
        mplew.writeInt(125);
        mplew.writeInt(29);
        mplew.writeInt(10);
        mplew.writeInt(250);
        mplew.writeInt(2);
        mplew.writeInt(29);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(23);
        mplew.writeInt(2);
        mplew.writeInt(1);
        mplew.writeInt(27);
        mplew.writeInt(3);
        mplew.writeInt(1);
        mplew.writeInt(30);
        mplew.writeInt(4);
        mplew.writeInt(2);
        mplew.writeInt(34);
        mplew.writeInt(5);
        mplew.writeInt(2);
        mplew.writeInt(38);
        mplew.writeInt(6);
        mplew.writeInt(2);
        mplew.writeInt(42);
        mplew.writeInt(7);
        mplew.writeInt(3);
        mplew.writeInt(45);
        mplew.writeInt(8);
        mplew.writeInt(3);
        mplew.writeInt(49);
        mplew.writeInt(9);
        mplew.writeInt(8);
        mplew.writeInt(150);
        mplew.writeInt(10);
        mplew.writeInt(3);
        mplew.writeInt(60);
        mplew.writeInt(11);
        mplew.writeInt(3);
        mplew.writeInt(68);
        mplew.writeInt(12);
        mplew.writeInt(3);
        mplew.writeInt(75);
        mplew.writeInt(13);
        mplew.writeInt(3);
        mplew.writeInt(83);
        mplew.writeInt(14);
        mplew.writeInt(3);
        mplew.writeInt(90);
        mplew.writeInt(15);
        mplew.writeInt(3);
        mplew.writeInt(98);
        mplew.writeInt(16);
        mplew.writeInt(3);
        mplew.writeInt(105);
        mplew.writeInt(17);
        mplew.writeInt(3);
        mplew.writeInt(113);
        mplew.writeInt(18);
        mplew.writeInt(4);
        mplew.writeInt(120);
        mplew.writeInt(19);
        mplew.writeInt(12);
        mplew.writeInt(263);
        mplew.writeInt(20);
        mplew.writeInt(4);
        mplew.writeInt(128);
        mplew.writeInt(21);
        mplew.writeInt(4);
        mplew.writeInt(135);
        mplew.writeInt(22);
        mplew.writeInt(4);
        mplew.writeInt(143);
        mplew.writeInt(23);
        mplew.writeInt(4);
        mplew.writeInt(150);
        mplew.writeInt(24);
        mplew.writeInt(4);
        mplew.writeInt(158);
        mplew.writeInt(25);
        mplew.writeInt(5);
        mplew.writeInt(165);
        mplew.writeInt(26);
        mplew.writeInt(5);
        mplew.writeInt(173);
        mplew.writeInt(27);
        mplew.writeInt(5);
        mplew.writeInt(180);
        mplew.writeInt(28);
        mplew.writeInt(6);
        mplew.writeInt(188);
        mplew.writeInt(29);
        mplew.writeInt(15);
        mplew.writeInt(375);
        mplew.writeInt(3);
        mplew.writeInt(29);
        mplew.writeInt(1);
        mplew.writeInt(2);
        mplew.writeInt(38);
        mplew.writeInt(2);
        mplew.writeInt(2);
        mplew.writeInt(44);
        mplew.writeInt(3);
        mplew.writeInt(2);
        mplew.writeInt(50);
        mplew.writeInt(4);
        mplew.writeInt(3);
        mplew.writeInt(57);
        mplew.writeInt(5);
        mplew.writeInt(3);
        mplew.writeInt(63);
        mplew.writeInt(6);
        mplew.writeInt(3);
        mplew.writeInt(69);
        mplew.writeInt(7);
        mplew.writeInt(5);
        mplew.writeInt(75);
        mplew.writeInt(8);
        mplew.writeInt(5);
        mplew.writeInt(82);
        mplew.writeInt(9);
        mplew.writeInt(14);
        mplew.writeInt(300);
        mplew.writeInt(10);
        mplew.writeInt(5);
        mplew.writeInt(110);
        mplew.writeInt(11);
        mplew.writeInt(5);
        mplew.writeInt(124);
        mplew.writeInt(12);
        mplew.writeInt(6);
        mplew.writeInt(138);
        mplew.writeInt(13);
        mplew.writeInt(6);
        mplew.writeInt(152);
        mplew.writeInt(14);
        mplew.writeInt(6);
        mplew.writeInt(165);
        mplew.writeInt(15);
        mplew.writeInt(6);
        mplew.writeInt(179);
        mplew.writeInt(16);
        mplew.writeInt(6);
        mplew.writeInt(193);
        mplew.writeInt(17);
        mplew.writeInt(6);
        mplew.writeInt(207);
        mplew.writeInt(18);
        mplew.writeInt(7);
        mplew.writeInt(220);
        mplew.writeInt(19);
        mplew.writeInt(17);
        mplew.writeInt(525);
        mplew.writeInt(20);
        mplew.writeInt(7);
        mplew.writeInt(234);
        mplew.writeInt(21);
        mplew.writeInt(7);
        mplew.writeInt(248);
        mplew.writeInt(22);
        mplew.writeInt(7);
        mplew.writeInt(262);
        mplew.writeInt(23);
        mplew.writeInt(7);
        mplew.writeInt(275);
        mplew.writeInt(24);
        mplew.writeInt(7);
        mplew.writeInt(289);
        mplew.writeInt(25);
        mplew.writeInt(9);
        mplew.writeInt(303);
        mplew.writeInt(26);
        mplew.writeInt(9);
        mplew.writeInt(317);
        mplew.writeInt(27);
        mplew.writeInt(9);
        mplew.writeInt(330);
        mplew.writeInt(28);
        mplew.writeInt(10);
        mplew.writeInt(344);
        mplew.writeInt(29);
        mplew.writeInt(20);
        mplew.writeInt(750);
        mplew.writeInt(2);
        mplew.writeInt(50000000);
        mplew.writeInt(5);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(11);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(2);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(3);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(4);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(5);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(6);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(7);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(8);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(9);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(50);
        mplew.writeInt(10);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(50);
        mplew.writeInt(10);
        mplew.writeInt(0);
        mplew.writeInt(0x66666666);
        mplew.writeInt(1071015526);
        mplew.writeInt(1);
        mplew.writeInt(0x66666666);
        mplew.writeInt(1071015526);
        mplew.writeInt(2);
        mplew.writeInt(0x66666666);
        mplew.writeInt(1071015526);
        mplew.writeInt(3);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(4);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(5);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(6);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(7);
        mplew.writeInt(0x33333333);
        mplew.writeInt(0x3FC33333);
        mplew.writeInt(8);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1069128089);
        mplew.writeInt(9);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1068079513);
        mplew.writeInt(21);
        mplew.writeInt(10);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(11);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(12);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(13);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(14);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(15);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(16);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(17);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(18);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(19);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(21);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(22);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(23);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(24);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(25);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(26);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(27);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(28);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(29);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(10000000);
        mplew.writeInt(0);
        mplew.writeInt(31);
        mplew.writeInt(0);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(2);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(3);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(4);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(5);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(6);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(7);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(8);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(9);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(11);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(12);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(13);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(14);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(15);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(16);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(17);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(18);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(19);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(21);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(22);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(23);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(24);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(25);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(26);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(27);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(28);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(29);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(50000001);
        mplew.writeInt(10);
        mplew.writeInt(0);
        mplew.writeInt(200);
        mplew.writeInt(11);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(2);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(3);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(4);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(5);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(6);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(7);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(8);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(9);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(50);
        mplew.writeInt(10);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(50);
        mplew.writeInt(10);
        mplew.writeInt(0);
        mplew.writeInt(0x66666666);
        mplew.writeInt(1071015526);
        mplew.writeInt(1);
        mplew.writeInt(0x66666666);
        mplew.writeInt(1071015526);
        mplew.writeInt(2);
        mplew.writeInt(0x66666666);
        mplew.writeInt(1071015526);
        mplew.writeInt(3);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(4);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(5);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(6);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1070176665);
        mplew.writeInt(7);
        mplew.writeInt(0x33333333);
        mplew.writeInt(0x3FC33333);
        mplew.writeInt(8);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1069128089);
        mplew.writeInt(9);
        mplew.writeInt(-1717986918);
        mplew.writeInt(1068079513);
        mplew.writeInt(21);
        mplew.writeInt(10);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(11);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(12);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(13);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(14);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(15);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(16);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(17);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(18);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(19);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(21);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(22);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(23);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(24);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(25);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(26);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(27);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(28);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(29);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(20000000);
        mplew.writeInt(0);
        mplew.writeInt(31);
        mplew.writeInt(0);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(2);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(3);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(4);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(5);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(6);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(7);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(8);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(9);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(11);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(12);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(13);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(14);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(15);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(16);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(17);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(18);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(19);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(20);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(21);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(22);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(23);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(24);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(25);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(26);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(27);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(28);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(29);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(30);
        mplew.writeInt(100000000);
        mplew.writeInt(0);
        mplew.writeInt(2);
        mplew.writeInt(4009548);
        mplew.writeInt(4009547);
        return mplew.getPacket();
    }

    public static byte[] sendHexaStatsInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_HEXA_STATS_INFO.getValue());
        PacketHelper.encodeSixStats(mplew, chr);
        return mplew.getPacket();
    }

    public static byte[] sendHexaSkillInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_HEXA_SKILL_INFO.getValue());
        PacketHelper.encodeHexaSkills(mplew, chr);
        return mplew.getPacket();
    }

    public static byte[] sendHexaActionResult(int type, int value, int value2, int value3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_HEXA_ACTION_RESULT.getValue());
        mplew.writeInt(type);
        switch (type) {
            case 0: {
                mplew.writeInt(0);
                mplew.writeInt(value);
                break;
            }
            case 1: {
                mplew.writeInt(value);
                mplew.writeInt(value2);
                mplew.writeInt(value3);
                break;
            }
            case 2: {
                mplew.writeInt(value);
                mplew.writeInt(value2);
                break;
            }
            case 4: {
                mplew.writeInt(value);
                break;
            }
            case 3: {
                mplew.writeInt(value);
                break;
            }
            case 5: {
                mplew.writeInt(value);
                break;
            }
            case 6: {
                mplew.writeInt(value);
                break;
            }
            case 7: {
                mplew.writeInt(value);
                break;
            }
            case 8: {
                mplew.writeInt(value);
                break;
            }
        }
        return mplew.getPacket();
    }

    public static byte[] updateDailyGift(String key) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_Message.getValue());
        mplew.write(31);
        mplew.writeInt(15);
        mplew.writeMapleAsciiString(key);
        return mplew.getPacket();
    }

    @Generated
    public static MapleClient getC() {
        return c;
    }
}

