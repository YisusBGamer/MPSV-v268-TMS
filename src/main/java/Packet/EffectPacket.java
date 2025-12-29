/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.skills.KSPsychicSkillEntry
 */
package Packet;

import Client.MapleCharacter;
import Client.skills.KSPsychicSkillEntry;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

public class EffectPacket {
    private static final Logger log = LoggerFactory.getLogger(EffectPacket.class);

    public static byte[] encodeUserEffectLocal(int skillid, EffectOpcode effect, int playerLevel, int skillLevel) {
        return EffectPacket.encodeUserEffectLocal(skillid, effect, playerLevel, skillLevel, (byte)4);
    }

    public static byte[] encodeUserEffectLocal(int skillid, EffectOpcode effect, int playerLevel, int skillLevel, byte direction) {
        return EffectPacket.encodeUserEffect(null, skillid, effect, playerLevel, skillLevel, direction);
    }

    public static byte[] onUserEffectRemote(MapleCharacter chr, int skillid, EffectOpcode effect, int playerLevel, int skillLevel) {
        return EffectPacket.encodeUserEffect(chr, skillid, effect, playerLevel, skillLevel, (byte)4);
    }

    public static byte[] encodeUserEffect(MapleCharacter chr, int skillid, EffectOpcode effect, int playerLevel, int skillLevel, byte direction) {
        if (EffectOpcode.UserEffect_SkillUse == effect) {
            return EffectPacket.showBuffEffect(chr, chr != null, skillid, skillLevel, playerLevel, new Point(0, 0));
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chr == null) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chr.getId());
        }
        mplew.write(1);
        if (effect == EffectOpcode.UserEffect_SkillUseBySummoned) {
            mplew.writeInt(0);
        }
        mplew.writeInt(skillid);
        switch (skillid) {
            case 80003224: {
                mplew.writeInt(261);
                mplew.writeInt(1);
                mplew.write(1);
                break;
            }
            case 400051076: {
                mplew.write(1);
                break;
            }
            case 65121052: {
                if (chr != null) {
                    mplew.writeInt(chr.getPosition().x);
                    mplew.writeInt(chr.getPosition().y);
                } else {
                    mplew.writeLong(0L);
                }
                mplew.write(1);
                break;
            }
            case 1320016: 
            case 22170074: {
                mplew.write(0);
                break;
            }
            case 4331006: {
                mplew.write(0);
                mplew.writeInt(0);
                break;
            }
            case 30001061: {
                mplew.writeInt(2);
                mplew.writeInt(1);
                mplew.write(0);
            }
        }
        if (skillid != 15001021 && skillid != 20051284 && skillid != 4211016) {
            switch (skillid) {
                case 4221052: 
                case 65121052: {
                    if (chr != null) {
                        mplew.writeInt(chr.getPosition().x);
                        mplew.writeInt(chr.getPosition().y);
                        break;
                    }
                    mplew.writeLong(0L);
                    break;
                }
                case 37000010: 
                case 37001001: 
                case 37100002: 
                case 37101001: 
                case 37110001: 
                case 37110004: 
                case 37111000: 
                case 37111003: {
                    mplew.writeInt(0);
                    break;
                }
                case 400041011: 
                case 400041012: 
                case 400041013: 
                case 400041014: 
                case 400041015: {
                    mplew.writeInt(0);
                }
            }
        }
        if (chr == null && skillid == 30001062) {
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
        }
        mplew.writeInt(0);
        if (skillid == 400041087) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (skillid == 3341503) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (skillid == 64001009) {
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        mplew.writeInt(playerLevel);
        mplew.writeInt(skillLevel);
        if (direction != 3) {
            mplew.write(direction);
        }
        return mplew.getPacket();
    }

    public static byte[] showBuffEffect(MapleCharacter chr, boolean other, int skillId, int skillLevel, int n3, Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (other) {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chr.getId());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        }
        mplew.write(EffectOpcode.UserEffect_SkillUse.getValue());
        mplew.writeInt(skillId);
        if (chr != null) {
            mplew.write(chr.getLevel());
        } else {
            mplew.write(0);
        }
        mplew.writeInt(skillLevel);
        switch (skillId) {
            case 22170074: {
                mplew.write(0);
                break;
            }
            case 1320016: {
                mplew.write(0);
                break;
            }
            case 4331006: {
                mplew.write(0);
                mplew.writeInt(0);
                break;
            }
            case 4211006: {
                break;
            }
            case 164111002: {
                break;
            }
            case 64001000: 
            case 64001007: 
            case 64001008: {
                mplew.write(0);
                break;
            }
            case 64001009: 
            case 64001010: 
            case 64001011: 
            case 64001012: {
                mplew.writeBool(chr != null && chr.isFacingLeft());
                mplew.writeInt(n3);
                if (pos == null) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    break;
                }
                mplew.writeInt(pos.x);
                mplew.writeInt(pos.y);
                break;
            }
            case 35001006: {
                break;
            }
            case 91001017: 
            case 91001020: {
                break;
            }
            case 33111007: {
                break;
            }
            case 30001062: {
                mplew.write(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
                break;
            }
            case 30001061: {
                mplew.write(0);
                break;
            }
            case 60001218: 
            case 60011218: 
            case 400001000: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 4211016: 
            case 15001021: 
            case 20041222: 
            case 20051284: 
            case 152001004: 
            case 400041026: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 4221052: 
            case 65121052: {
                if (chr == null) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    break;
                }
                mplew.writeInt(chr.getPosition().x);
                mplew.writeInt(chr.getPosition().y);
                break;
            }
            case 12001027: 
            case 12001028: 
            case 80001851: 
            case 142121008: {
                break;
            }
            case 37000010: 
            case 37001001: 
            case 37100002: 
            case 37101001: 
            case 37110001: 
            case 37110004: 
            case 37111000: 
            case 37111003: {
                mplew.writeInt(0);
                break;
            }
            case 400041019: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 400041009: {
                mplew.writeInt(0);
                break;
            }
            case 400041011: 
            case 400041012: 
            case 400041013: 
            case 400041014: 
            case 400041015: {
                mplew.writeInt(0);
                break;
            }
            case 400021000: {
                break;
            }
            case 400041036: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 152111005: 
            case 152111006: {
                break;
            }
            case 80002393: 
            case 80002394: 
            case 80002395: 
            case 80002421: {
                mplew.writeInt(0);
                break;
            }
            case 3311002: 
            case 3321006: {
                mplew.writeInt(0);
                break;
            }
            case 23111008: 
            case 23111009: 
            case 23111010: {
                break;
            }
            case 164001002: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 400041053: {
                break;
            }
            case 80002758: {
                break;
            }
            case 131003016: 
            case 400020009: 
            case 400020010: 
            case 400020011: 
            case 400031050: {
                mplew.write(true);
                if (chr == null) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    break;
                }
                mplew.writeInt((int)(chr.getPosition().getX() + (double)(chr.isFacingLeft() ? -658 : 658)));
                mplew.writeInt((int)(chr.getPosition().getY() - 150.0));
                break;
            }
            case 400051080: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 400051073: 
            case 400051081: {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            }
            case 63001004: {
                mplew.writeBool(chr != null && chr.isFacingLeft());
                mplew.writeInt(n3);
                if (pos == null) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    break;
                }
                mplew.writeInt(pos.x);
                mplew.writeInt(pos.y);
                break;
            }
            case 63001002: 
            case 63001003: 
            case 63001005: {
                mplew.write(true);
                if (chr == null) {
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                    break;
                }
                mplew.writeInt((int)(chr.getPosition().getX() + (double)(chr.isFacingLeft() ? -658 : 658)));
                mplew.writeInt((int)(chr.getPosition().getY() - 150.0));
                break;
            }
            case 80001132: {
                mplew.write(0);
                break;
            }
            default: {
                boolean result;
                if (skillId == 80011187 || skillId == 80011188) break;
                if (skillId > 0) {
                    int jobBySkill = skillId / 10000;
                    if (jobBySkill - 8000 <= 1) {
                        jobBySkill = skillId / 100;
                    }
                    result = jobBySkill != 9500;
                } else {
                    boolean bl = result = skillId - 90000000 < 10000000;
                }
                if (!result) break;
                mplew.write(n3);
                break;
            }
        }
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] showOwnDiceEffect(int skillid, int effectid, int effectid2, int level) {
        return EffectPacket.showDiceEffect(-1, skillid, level, effectid, effectid2, false);
    }

    public static byte[] showDiceEffect(int chrId, int skillid, int level, int effectid, int effectid2, boolean b) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_SkillAffected_Select.getValue());
        mplew.writeInt(effectid);
        mplew.writeInt(effectid2);
        mplew.writeInt(skillid);
        mplew.write(level);
        mplew.writeBool(b);
        return mplew.getPacket();
    }

    public static byte[] showFieldExpItemConsumed(int exp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_FieldExpItemConsumed.getValue());
        mplew.writeInt(exp);
        return mplew.getPacket();
    }

    public static byte[] showItemLevelupEffect() {
        return EffectPacket.showSpecialEffect(EffectOpcode.UserEffect_ItemLevelUp);
    }

    public static byte[] showForeignItemLevelupEffect(int chrId) {
        return EffectPacket.showForeignEffect(chrId, EffectOpcode.UserEffect_ItemLevelUp);
    }

    public static byte[] showSpecialEffect(EffectOpcode effect) {
        return EffectPacket.showForeignEffect(-1, effect);
    }

    public static byte[] showForeignEffect(int chrId, EffectOpcode effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
            mplew.writeInt(chrId);
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(effect.getValue());
        return mplew.getPacket();
    }

    public static byte[] showOwnHpHealed(int amount) {
        return EffectPacket.showHpHealed(-1, amount);
    }

    public static byte[] showHpHealed(int chrId, int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_IncDecHPRegenEffect.getValue());
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] showBlessOfDarkness(int chrId, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_SkillSpecial.getValue());
        mplew.writeInt(skillId);
        if (skillId == 3101009) {
            mplew.write(30);
        }
        return mplew.getPacket();
    }

    public static byte[] showOwnEffectUOL(String effect, int time, int itemId) {
        return EffectPacket.showEffectUOL(-1, effect, time, itemId);
    }

    public static byte[] showEffectUOL(int chrId, String effect, int time, int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_EffectUOL.getValue());
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(time);
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static byte[] showRewardItemAnimation(int itemId, String effect) {
        return EffectPacket.showRewardItemAnimation(itemId, effect, -1);
    }

    public static byte[] showRewardItemAnimation(int itemId, String effect, int chrId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_LotteryUse.getValue());
        mplew.writeInt(itemId);
        mplew.write(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            mplew.writeMapleAsciiString(effect);
        }
        return mplew.getPacket();
    }

    public static byte[] playPortalSE() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_PlayPortalSE.getValue());
        return mplew.getPacket();
    }

    public static byte[] ItemMaker_Success() {
        return EffectPacket.ItemMaker_Success_3rdParty(-1);
    }

    public static byte[] ItemMaker_Success_3rdParty(int chrId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_ItemMaker.getValue());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] showOwnPetLevelUp(byte index) {
        return EffectPacket.showPetLevelUp(-1, index);
    }

    public static byte[] showPetLevelUp(int chrId, byte index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_Pet.getValue());
        mplew.write(0);
        mplew.writeInt(index);
        return mplew.getPacket();
    }

    public static byte[] showAvatarOriented(String data) {
        return EffectPacket.showAvatarOriented(-1, data);
    }

    public static byte[] showAvatarOriented(int chrId, String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_AvatarOriented.getValue());
        mplew.writeMapleAsciiString(data);
        return mplew.getPacket();
    }

    public static byte[] showAvatarOrientedRepeat(boolean b, String s) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_AvatarOrientedRepeat.getValue());
        mplew.writeBool(b);
        if (b) {
            mplew.writeMapleAsciiString(s);
            mplew.writeInt(0);
            mplew.writeInt(1);
        }
        return mplew.getPacket();
    }

    public static byte[] playSoundWithMuteBGM(String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_PlaySoundWithMuteBGM.getValue());
        mplew.writeMapleAsciiString(data);
        return mplew.getPacket();
    }

    public static byte[] showReservedEffect(String data) {
        return EffectPacket.showReservedEffect(false, 0, 0, data);
    }

    public static byte[] showReservedEffect(boolean screenCoord, int rx, int ry, String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_ReservedEffect.getValue());
        mplew.write(screenCoord);
        mplew.writeInt(rx);
        mplew.writeInt(ry);
        mplew.writeMapleAsciiString(data);
        return mplew.getPacket();
    }

    public static byte[] getShowItemGain(List<Pair<Integer, Integer>> showItems) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_Quest.getValue());
        mplew.write(showItems.size());
        for (Pair<Integer, Integer> items : showItems) {
            mplew.writeInt((Integer)items.left);
            mplew.writeInt((Integer)items.right);
            mplew.writeBool(false);
        }
        return mplew.getPacket();
    }

    public static byte[] getShowItemGain(int itemid, short amount, boolean b) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_Quest.getValue());
        mplew.write(1);
        mplew.writeInt(itemid);
        mplew.writeInt(amount);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showOwnXenonPowerOn(String effect) {
        return EffectPacket.showXenonPowerOn(-1, effect);
    }

    public static byte[] showXenonPowerOn(int chrId, String effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrId == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrId);
        }
        mplew.write(EffectOpcode.UserEffect_UpgradeTombItemUse.getValue());
        mplew.writeMapleAsciiString(effect);
        return mplew.getPacket();
    }

    public static byte[] showHakuSkillUse(int skillType, int cid, int skillLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (cid == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(cid);
        }
        mplew.write(EffectOpcode.UserEffect_HakuSkill.getValue());
        mplew.writeShort(0);
        mplew.writeInt(skillType);
        mplew.write(1);
        mplew.writeShort(skillLevel);
        return mplew.getPacket();
    }

    public static byte[] playerDeadConfirm(int type, boolean voice, int value, int autoReviveTime, int reviveDelay, boolean reviveEnd) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_OpenUIOnDead.getValue());
        mplew.writeInt(type);
        mplew.write(voice);
        mplew.writeInt(value);
        mplew.writeInt(0);
        mplew.write(autoReviveTime > 0);
        mplew.writeInt(autoReviveTime);
        mplew.writeInt(reviveDelay);
        mplew.write(reviveEnd);
        return mplew.getPacket();
    }

    public static byte[] ProtectBuffGain(int itemID, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetBuffProtector.getValue());
        mplew.writeInt(itemID);
        mplew.write(value);
        return mplew.getPacket();
    }

    public static byte[] getEffectSwitch(int cid, List<Integer> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.EFFECT_SWITCH.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(items.size());
        for (int i : items) {
            mplew.writeInt(i);
        }
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] showKSPsychicGrab(int cid, int skillid, short skilllevel, List<KSPsychicSkillEntry> ksse, int n1, int n2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.GIVE_KSPSYCHIC.getValue());
        mplew.writeInt(cid);
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeShort(skilllevel);
        mplew.writeInt(n1);
        mplew.writeInt(n2);
        mplew.writeBool(true);
        for (int i = 0; i < ksse.size(); ++i) {
            KSPsychicSkillEntry k = ksse.get(i);
            if (i > 0) {
                mplew.write(1);
            }
            mplew.write(1);
            mplew.writeInt(k.getOid());
            mplew.writeInt(Math.abs(k.getOid()));
            mplew.writeInt(k.getMobOid());
            if (k.getMobOid() != 0) {
                mplew.writeShort(0);
                mplew.writeInt(k.getN5());
                mplew.writeLong(150520L);
                mplew.writeLong(150520L);
            } else {
                mplew.writeShort(Randomizer.nextInt(19) + 1);
                mplew.writeInt(k.getN5());
                mplew.writeLong(100L);
                mplew.writeLong(100L);
            }
            mplew.write(1);
            mplew.writeInt(k.getN1());
            mplew.writeInt(k.getN2());
            mplew.writeInt(k.getN3());
            mplew.writeInt(k.getN4());
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showKSPsychicAttack(int cid, int skillid, short skilllevel, int n1, int n2, byte n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.ATTACK_KSPSYCHIC.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(skillid);
        mplew.writeShort(skilllevel);
        mplew.writeInt(n1);
        mplew.writeInt(n2);
        mplew.write(n3);
        mplew.writeInt(n4);
        if (n4 != 0) {
            mplew.writeInt(n5);
            mplew.writeInt(n6);
        }
        mplew.writeInt(n7);
        mplew.writeInt(n8);
        mplew.writeInt(n9);
        mplew.writeZeroBytes(20);
        return mplew.getPacket();
    }

    public static byte[] showKSPsychicRelease(int cid, int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CANCEL_KSPSYCHIC.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        return mplew.getPacket();
    }

    public static byte[] showGiveKSUltimate(int chrid, int mode, int type, int oid, int skillid, short skilllevel, int n1, byte n2, short n3, short n4, short n5, int n6, int n7) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.GIVE_KSULTIMATE.getValue());
        mplew.writeInt(chrid);
        mplew.write(1);
        mplew.writeInt(mode);
        mplew.writeInt(type);
        mplew.writeInt(oid);
        mplew.writeInt(skillid);
        mplew.writeShort(skilllevel);
        mplew.writeInt(Math.abs(oid));
        mplew.writeInt(n1);
        mplew.write(n2);
        mplew.writeShort(n3);
        mplew.writeShort(n4);
        mplew.writeShort(n5);
        mplew.writeInt(n6);
        mplew.writeInt(n7);
        return mplew.getPacket();
    }

    public static byte[] showAttackKSUltimate(int oid, int attackcount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DoActivePsychicArea.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(attackcount);
        return mplew.getPacket();
    }

    public static byte[] showCancelKSUltimate(int chrid, int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CANCEL_KSULTIMATE.getValue());
        mplew.writeInt(chrid);
        mplew.writeInt(oid);
        return mplew.getPacket();
    }

    public static byte[] showExpertEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserRequestExJablin.getValue());
        return mplew.getPacket();
    }

    public static byte[] enforceMSG(String a, int id, int delay) {
        MaplePacketLittleEndianWriter packet = new MaplePacketLittleEndianWriter();
        packet.writeShort(OutHeader.LP_WeatherEffectNotice.getValue());
        packet.writeMapleAsciiString(a);
        packet.writeInt(id);
        packet.writeInt(delay);
        packet.write(0);
        return packet.getPacket();
    }

    public static byte[] ShowTimer(MapleCharacter p, int MS) {
        MaplePacketLittleEndianWriter packet = new MaplePacketLittleEndianWriter();
        packet.writeShort(OutHeader.CTX_Event_Field_Timer.getValue());
        packet.write(2);
        packet.writeInt(MS);
        p.send(packet.getPacket());
        return packet.getPacket();
    }

    public static byte[] showCombustionMessage(String text, int milliseconds, int posY) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_TextEffect.getValue());
        mplew.writeMapleAsciiString(text);
        mplew.writeInt(50);
        mplew.writeInt(milliseconds);
        mplew.writeInt(4);
        mplew.writeInt(0);
        mplew.writeInt(posY);
        mplew.writeInt(1);
        mplew.writeInt(4);
        mplew.writeInt(2);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString("");
        mplew.writeInt(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showHpHealed_Other(int chrId, int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
        mplew.writeInt(chrId);
        mplew.write(EffectOpcode.UserEffect_IncDecHPRegenEffect.getValue());
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] showBuffItemEffect(int chrID, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_BuffItemEffect.getValue());
        mplew.writeInt(skillId);
        return mplew.getPacket();
    }

    public static byte[] showSkillAffected(int sourceid, short effectid, int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(effectid);
        mplew.writeInt(sourceid);
        mplew.write(level);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showEffectById(int effectid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(16);
        return mplew.getPacket();
    }

    public static byte[] showMobSkillHit(int chrID, int skillID, int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_MobSkillHit.getValue());
        mplew.writeInt(skillID);
        mplew.writeInt(level);
        return mplew.getPacket();
    }

    public static byte[] showRoyalGuardAttack() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
        mplew.writeShort(OutHeader.LP_RoyalGuardAttack.getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showIncDecHPRegen(int chrID, int hpDiff) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_IncDecHPRegenEffect.getValue());
        mplew.writeInt(hpDiff);
        return mplew.getPacket();
    }

    public static byte[] showFlameWizardFlameWalk(int chrID) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FlameWizardFlameWalkEffect.getValue());
        mplew.writeInt(chrID);
        return mplew.getPacket();
    }

    public static byte[] showResetOnStateForOnOffSkill(int chrID) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_ResetOnStateForOnOffSkill.getValue());
        return mplew.getPacket();
    }

    public static byte[] showSkillMode(int chrID, int sourceid, int mode, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_SkillMode.getValue());
        mplew.writeInt(sourceid);
        mplew.writeInt(mode - 1);
        mplew.writeInt(value);
        return mplew.getPacket();
    }

    public static byte[] show黑暗重生(int chrID, int sourceid, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_SkillPreLoopEnd.getValue());
        mplew.writeInt(sourceid);
        mplew.writeInt(value);
        return mplew.getPacket();
    }

    public static byte[] showSkillAffected(int chrID, int sourceid, int level, int direction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_SkillAffected.getValue());
        mplew.writeInt(sourceid);
        mplew.write(level);
        if (sourceid != 400051076) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (sourceid == 31111003 || sourceid == 100001261 || sourceid == 25111206) {
            mplew.writeInt(direction);
        }
        return mplew.getPacket();
    }

    public static byte[] showQuestItemGain(Map<Integer, Integer> map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        mplew.write(EffectOpcode.UserEffect_Quest.getValue());
        mplew.write(map.size());
        if (map.isEmpty()) {
            mplew.writeMapleAsciiString("");
            mplew.writeInt(0);
        } else {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                mplew.writeInt(entry.getKey());
                mplew.writeInt(entry.getValue());
                mplew.writeBool(false);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] DragonWreckage(int id, Point position, int n2, int addWreckages, int sourceid, int n5, int size) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ADD_WRECKAGE.getValue());
        mplew.writeInt(id);
        mplew.writeInt(position.x);
        mplew.writeInt(position.y);
        mplew.writeInt(0);
        mplew.writeInt(n2);
        mplew.writeInt(addWreckages);
        mplew.writeInt(sourceid);
        mplew.writeInt(n5);
        mplew.writeInt(size);
        byte[] packet = mplew.getPacket();
        return packet;
    }

    public static byte[] PapulatusFieldEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.PapulatusFieldEffect);
        mplew.writeInt(2);
        mplew.writeInt(4);
        for (int i = 0; i < 4; ++i) {
            mplew.writeInt(i);
            mplew.writeInt(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static byte[] PapulatusFieldEffect(int n, int n2, int n3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.PapulatusFieldEffect);
        mplew.writeInt(2);
        mplew.writeInt(1);
        mplew.writeInt(n);
        mplew.writeInt(n3 <= 0 ? 6 : 5);
        mplew.writeInt(n3 <= 0 ? 210 : n2);
        mplew.writeInt(n3);
        return mplew.getPacket();
    }

    public static byte[] PapulatusFieldEffect(int n, int n2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.PapulatusFieldEffect);
        mplew.writeInt(0);
        mplew.writeInt(2);
        for (int i = 0; i < 2; ++i) {
            mplew.writeInt(n2 + i);
            mplew.writeInt(Randomizer.nextInt(2));
            mplew.writeShort(0);
            mplew.writeShort(24640);
            mplew.writeShort(64);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(Randomizer.rand(16436, 16450));
        }
        return mplew.getPacket();
    }

    public static byte[] showIncubatorEffect(int chrID, int effectId, String info) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_IncubatorUse.getValue());
        mplew.writeInt(effectId);
        mplew.writeMapleAsciiString(info);
        return mplew.getPacket();
    }

    public static byte[] showJobChanged(int chrID, int job) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_JobChanged.getValue());
        mplew.writeInt(job);
        return mplew.getPacket();
    }

    public static byte[] showHoYoungHeal(int chrID, int skillId, Point position, int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chrID == -1) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chrID);
        }
        mplew.write(EffectOpcode.UserEffect_HoYoungHeal.getValue());
        mplew.writeInt(skillId);
        mplew.writeInt(1);
        mplew.writePosInt(position);
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] BossFieldSkillEffect(int skillId, int skillLv, int delay, int unk1, int unk2, int screenDuration, int unk3, int attackDuration, int height, int unk4, int size, String packets) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_FieldSkillRequest);
        mplew.writeInt(skillId);
        mplew.writeInt(skillLv);
        mplew.writeInt(delay);
        mplew.writeInt(unk1);
        mplew.writeInt(unk2);
        mplew.writeInt(screenDuration);
        mplew.writeInt(unk3);
        mplew.writeInt(attackDuration);
        mplew.writeInt(height);
        mplew.writeInt(unk4);
        mplew.writeInt(size);
        for (String s : packets.split(",")) {
            mplew.writeInt(Integer.parseInt(s));
        }
        return mplew.getPacket();
    }

    public static byte[] BossSerenNoonFieldSkill(int mapLeft, int mapRight, int raysCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeOpcode(OutHeader.LP_FieldSkillRequest);
        mplew.writeInt(100023);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(300);
        mplew.writeInt(100);
        mplew.writeInt(3000);
        mplew.writeInt(150);
        mplew.writeInt(3000);
        mplew.writeInt(300);
        mplew.writeInt(0);
        mplew.writeInt(raysCount);
        for (int i = 0; i < raysCount; ++i) {
            int randX = 0;
            int randX2 = 0;
            if (i % 2 == 0) {
                randX = (int)Math.floor(Math.random() * (double)mapRight);
                randX2 = (int)Math.floor(Math.random() * (double)mapLeft);
            } else {
                randX = (int)Math.floor(Math.random() * (double)mapLeft);
                randX2 = (int)Math.floor(Math.random() * (double)mapRight);
            }
            mplew.writeInt(randX);
            mplew.writeInt(randX2);
            mplew.writeInt(i * 1000);
        }
        return mplew.getPacket();
    }

    public static byte[] encodeUserEffectByPickUpItem(MapleCharacter chr, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (chr == null) {
            mplew.writeShort(OutHeader.LP_UserEffectLocal.getValue());
        } else {
            mplew.writeShort(OutHeader.LP_UserEffectRemote.getValue());
            mplew.writeInt(chr.getId());
        }
        mplew.write(EffectOpcode.UserEffect_PickUpItem.getValue());
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }
}

