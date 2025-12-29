/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Config.constants.enums.InGameDirectionEventType
 *  Net.server.events.DimensionMirrorEvent
 *  Packet.UIPacket$1
 *  SwordieX.util.Position
 *  connection.packet.InGameDirectionEvent
 *  connection.packet.UserLocal
 */
package Packet;

import Client.MapleCharacter;
import Client.MessageOption;
import Config.constants.enums.InGameDirectionEventType;
import Net.server.events.DimensionMirrorEvent;
import Opcode.header.OutHeader;
import Packet.CWvsContext;
import Packet.UIPacket;
import SwordieX.util.Position;
import connection.packet.InGameDirectionEvent;
import connection.packet.UserLocal;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class UIPacket {
    private static final Logger log = LoggerFactory.getLogger(UIPacket.class);

    public static byte[] getSPMsg(byte sp, short job) {
        MessageOption option = new MessageOption();
        option.setJob(job);
        option.setAmount(sp);
        return CWvsContext.sendMessage(4, option);
    }

    public static byte[] getGPMsg(int amount) {
        MessageOption option = new MessageOption();
        option.setAmount(amount);
        return CWvsContext.sendMessage(7, option);
    }

    public static byte[] getGPContribution(int amount) {
        MessageOption option = new MessageOption();
        option.setAmount(amount);
        return CWvsContext.sendMessage(8, option);
    }

    public static byte[] getStatusMsg(int itemid) {
        MessageOption option = new MessageOption();
        option.setObjectId(itemid);
        return CWvsContext.sendMessage(9, option);
    }

    public static byte[] getBPMsg(int amount) {
        MessageOption option = new MessageOption();
        option.setAmount(amount);
        return CWvsContext.sendMessage(25, option);
    }

    public static byte[] getTopMsg(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ScriptProgressMessage.getValue());
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static byte[] ScriptProgressItemMessage(int n, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ScriptProgressItemMessage.getValue());
        mplew.writeInt(n);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static byte[] getMidMsg(int index, String msg, boolean keep) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetStaticScreenMessage.getValue());
        mplew.write(index);
        mplew.writeMapleAsciiString(msg);
        mplew.write(keep ? 0 : 1);
        return mplew.getPacket();
    }

    public static byte[] setStaticScreenMessage(int index, String msg, boolean keep) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetStaticScreenMessage.getValue());
        mplew.write(index);
        mplew.writeMapleAsciiString(msg);
        mplew.writeBool(keep);
        return mplew.getPacket();
    }

    public static byte[] offStaticScreenMessage() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_OffStaticScreenMessage.getValue());
        return mplew.getPacket();
    }

    public static byte[] showWeatherEffectNotice(String s, int n, int n2, boolean b) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_WeatherEffectNotice.getValue());
        mplew.writeMapleAsciiString(s);
        mplew.writeInt(n);
        mplew.writeInt(n2);
        mplew.writeBool(b);
        return mplew.getPacket();
    }

    public static byte[] WeatherEffectNoticeY(String s, int n, int n2, int n3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_WeatherEffectNoticeY.getValue());
        mplew.writeMapleAsciiString(s);
        mplew.writeInt(n);
        mplew.writeInt(n2);
        mplew.writeInt(n3);
        return mplew.getPacket();
    }

    public static byte[] getSpecialTopMsg(String msg, int fontNameType, int fontSize, int fontColorType, int fadeOutDelay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ProgressMessageFont.getValue());
        mplew.writeInt(fontNameType);
        mplew.writeInt(fontSize);
        mplew.writeInt(fontColorType);
        mplew.writeInt(fadeOutDelay);
        mplew.write(0);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static byte[] playMovie(String data, boolean show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserPlayMovieClip.getValue());
        mplew.writeMapleAsciiString(data);
        mplew.write(show ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] summonHelper(boolean summon) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserHireTutor.getValue());
        mplew.write(summon ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] summonMessage(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserTutorMsg.getValue());
        mplew.write(1);
        mplew.writeInt(type);
        mplew.writeInt(7000);
        return mplew.getPacket();
    }

    public static byte[] summonMessage(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserTutorMsg.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(200);
        mplew.writeInt(10000);
        return mplew.getPacket();
    }

    public static byte[] setDirectionMod(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetDirectionMode.getValue());
        if (enable) {
            mplew.write(1);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
        }
        if (!enable) {
            mplew.write(0);
            mplew.write(1);
        }
        return mplew.getPacket();
    }

    public static byte[] getDirectionEffectPlay(String data, int value, int s) {
        return UIPacket.getDirectionEvent(InGameDirectionEventType.EffectPlay, data, new int[]{value, 0, s, 0, 0}, null);
    }

    public static byte[] getDirectionEffectPlay(String data, int value, int x, int y, int pro) {
        return UIPacket.getDirectionEvent(InGameDirectionEventType.EffectPlay, data, new int[]{value, x, y, pro, 0, 0}, null);
    }

    public static byte[] getDirectionEffectPlay(String data, int value, int x, int y, int a, int b) {
        return UIPacket.getDirectionEvent(InGameDirectionEventType.EffectPlay, data, new int[]{value, x, y, a, b, 0, 0, 0, 0}, null);
    }

    public static byte[] getDirectionEffectPlay(String data, int value, int x, int y) {
        return UIPacket.getDirectionEffectPlayNpc(data, value, x, y, 0);
    }

    public static byte[] getDirectionEffectPlayNpc(String data, int value, int x, int y, int z) {
        return UIPacket.getDirectionEvent(InGameDirectionEventType.EffectPlay, data, new int[]{value, x, y, 1, 1, 0, z, z > 0 ? 0 : 1, 0}, null);
    }

    public static byte[] getDirectionCameraMove(byte type, int value) {
        return UIPacket.getDirectionEvent(InGameDirectionEventType.CameraMove, null, new int[]{type, value, 0, 0, 0, 0, 0, 0}, null);
    }

    public static byte[] getDirectionCameraMove(byte type, int x, int y, int z) {
        return UIPacket.getDirectionEvent(InGameDirectionEventType.CameraMove, null, new int[]{type, x, y, z, 0, 0, 0, 0}, null);
    }

    public static byte[] getDirectionEvent(InGameDirectionEventType type, int value) {
        return UIPacket.getDirectionEvent(type, null, new int[]{value, 0, 0, 0, 0, 0, 0, 0, 0}, null);
    }

    public static byte[] getDirectionEvent(InGameDirectionEventType mod, String data, int[] values, String data2) {
        InGameDirectionEvent ide;
        switch (mod) {
            case ForcedAction:
                ide = InGameDirectionEvent.forcedAction(values[0], values.length > 1 ? values[1] : 0);
                break;
            case Delay:
                ide = InGameDirectionEvent.delay(values[0]);
                break;
            case EffectPlay:
                ide = InGameDirectionEvent.effectPlay(data, values[0], new Position(values[1], values[2]), values[5], values[6], values[7] > 0, values[8], data2);
                break;
            case ForcedInput:
                ide = InGameDirectionEvent.forcedInput(values[0]);
                break;
            case PatternInputRequest:
                ide = InGameDirectionEvent.patternInputRequest(data, values[0], values[1], values[2]);
                break;
            case CameraMove:
                ide = InGameDirectionEvent.cameraMove(values[0] > 0, values[1], values.length >= 4 ? new Position(values[2], values[3]) : null);
                break;
            case CameraOnCharacter:
                ide = InGameDirectionEvent.cameraOnCharacter(values[0]);
                break;
            case CameraZoom:
                if (values.length > 1)
                    ide = InGameDirectionEvent.cameraZoom(values[0] > 0, values[1], values[2], values[3], new Position(values[4], values[5]));
                else
                    ide = InGameDirectionEvent.cameraZoom(false, 0, 0, 0, null);
                break;
            case VansheeMode:
                ide = InGameDirectionEvent.vansheeMode(values[0] > 0);
                break;
            case FaceOff:
                ide = InGameDirectionEvent.faceOff(values[0]);
                break;
            case Monologue:
                ide = InGameDirectionEvent.monologue(data, values[0] > 0);
                break;
            case MonologueScroll:
                ide = InGameDirectionEvent.monologueScroll(data, values[0] > 0, (short) values[1], values[2], values[3]);
                break;
            case AvatarLookSet:
                ide = InGameDirectionEvent.avatarLookSet(values);
                break;
            case RemoveAdditionalEffect:
                ide = InGameDirectionEvent.removeAdditionalEffect();
                break;
            case ForcedMove:
                ide = InGameDirectionEvent.forcedMove(values[0] == 1, 0);
                break;
            case ForcedFlip:
                ide = InGameDirectionEvent.forcedFlip(values[0] == -1);
                break;
            case InputUI:
                ide = InGameDirectionEvent.inputUI(values[0]);
                break;
            default:
                ide = null;
        }
        return ide == null ? new byte[0] : UserLocal.inGameDirectionEvent(ide).getData();
    }


    public static byte[] UserEmotionLocal(int expression, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserEmotionLocal.getValue());
        mplew.writeInt(expression);
        mplew.writeInt(duration);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] IntroEnableUI(int wtf) {
        return UIPacket.IntroEnableUI(wtf, true);
    }

    public static byte[] IntroEnableUI(int wtf, boolean block) {
        return UIPacket.SetInGameDirectionMode(wtf > 0, wtf > 0 ? block : wtf < 0, false, false);
    }

    public static byte[] SetInGameDirectionMode(boolean lockUI, boolean blackFrame, boolean forceMouseOver, boolean showUI) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetInGameDirectionMode.getValue());
        mplew.writeBool(lockUI);
        if (lockUI) {
            mplew.write(blackFrame);
            mplew.write(forceMouseOver);
            mplew.write(showUI);
        }
        if (!lockUI) {
            mplew.write(blackFrame);
        }
        return mplew.getPacket();
    }

    public static byte[] SetStandAloneMode(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_SetStandAloneMode.getValue());
        if (enable) {
            mplew.write(1);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
        } else {
            mplew.write(0);
            mplew.write(1);
        }
        return mplew.getPacket();
    }

    public static byte[] addPopupSay(int npcid, int time, String msg, String sound) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_AddPopupSay.getValue());
        mplew.writeInt(npcid);
        mplew.writeInt(time);
        mplew.writeMapleAsciiString(msg);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(300);
        mplew.writeInt(1);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showFreeMarketMiniMap(boolean show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_MiniMapOnOff.getValue());
        mplew.writeReversedBool(show);
        return mplew.getPacket();
    }

    public static byte[] sendOpenWindow(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserOpenUI.getValue());
        mplew.writeInt(id);
        return mplew.getPacket();
    }

    public static byte[] sendCloseWindow(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserCloseUI.getValue());
        mplew.writeInt(id);
        return mplew.getPacket();
    }

    public static byte[] sendPVPWindow(int npc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserOpenUI.getValue());
        mplew.writeInt(50);
        if (npc > 0) {
            mplew.writeInt(npc);
        }
        return mplew.getPacket();
    }

    public static byte[] sendEventWindow(int npc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserOpenUI.getValue());
        mplew.writeInt(55);
        if (npc > 0) {
            mplew.writeInt(npc);
        }
        return mplew.getPacket();
    }

    public static byte[] inGameCurNodeEventEnd(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_InGameCurNodeEventEnd.getValue());
        mplew.writeBool(enable);
        return mplew.getPacket();
    }

    public static byte[] sendSceneUI() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SCENE_UI.getValue());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] sendUIWindow(int op, int npc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserOpenUIWithOption.getValue());
        mplew.writeInt(op);
        mplew.writeInt(npc);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] showPQEffect(int n2, String string, String string2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.PQ_EFFECT.getValue());
        mplew.writeHexString("09 00 43 61 70 45 66 66 65 63 74 00 00 00 00 07 00 00 00 06 03 00 00 27 02 00 00 12 01 00 00 27 02 00 00 1E FF FF FF 27 02 00 00 24 FE FF FF 27 02 00 00 00 04 00 00 27 02 00 00 18 00 00 00 27 02 00 00 2A FD FF FF 27 02 00 00");
        return mplew.getPacket();
    }

    public static byte[] sendDynamicObj(boolean animation, Pair<Integer, Triple<String, String, String>> dynamicObj) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_DynamicObjShowHide.getValue());
        mplew.writeInt(dynamicObj == null ? 0 : dynamicObj.getLeft());
        mplew.writeInt(dynamicObj == null || animation ? 0 : 1);
        mplew.writeInt(0);
        if (dynamicObj != null) {
            if (dynamicObj.getLeft() == 2) {
                mplew.writeMapleAsciiString(dynamicObj.getRight().getLeft());
                mplew.writeMapleAsciiString(dynamicObj.getRight().getMid());
                mplew.writeMapleAsciiString(dynamicObj.getRight().getRight());
            } else {
                mplew.writeInt(0);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] screenShake(int n2, boolean bl2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_UserSetFieldFloating.getValue());
        mplew.writeInt(n2);
        mplew.writeInt(bl2 ? 0 : 20);
        mplew.writeInt(bl2 ? 0 : 50);
        mplew.writeInt(bl2 ? 0 : 20);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] fishingCaught(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.FISHING_CAUGHT.getValue());
        mplew.writeInt(id);
        return mplew.getPacket();
    }

    public static byte[] showDimensionMirror(List<DimensionMirrorEvent> list) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.DimensionMirror.getValue());
        mplew.writeInt(list.size());
        for (DimensionMirrorEvent event : list) {
            mplew.writeMapleAsciiString(event.getName());
            mplew.writeMapleAsciiString(event.getInfo());
            mplew.writeInt(event.getLimitLevel());
            mplew.writeInt(event.getPos());
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(event.getID());
            mplew.writeBool(event.isTeam());
            mplew.writeInt(0);
            mplew.writeInt(event.getRewards().size());
            for (Integer o : event.getRewards()) {
                mplew.writeInt(o);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] ShowSpecialUI(boolean b, String s) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.ShowSpecialUI.getValue());
        mplew.writeBool(b);
        mplew.writeMapleAsciiString(s);
        return mplew.getPacket();
    }

    public static byte[] setAreaControl(List<String> ctrl_1, List<String> ctrl_2, List<String> ctrl_3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.AREA_CTRLS.getValue());
        mplew.write(ctrl_1 != null);
        if (ctrl_1 != null) {
            mplew.writeInt(ctrl_1.size());
            for (String s : ctrl_1) {
                mplew.writeMapleAsciiString(s);
            }
        }
        mplew.write(ctrl_2 != null);
        if (ctrl_2 != null) {
            mplew.writeInt(ctrl_2.size());
            for (String s : ctrl_2) {
                mplew.writeMapleAsciiString(s);
            }
        }
        mplew.write(ctrl_3 != null);
        if (ctrl_3 != null) {
            mplew.writeInt(ctrl_3.size());
            for (String s : ctrl_3) {
                mplew.writeMapleAsciiString(s);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getGold(MapleCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.EVENT_GOLD_DAY.getValue());
        player.dropMessage(-5, "開啟黃金馬車簽到UI");
        mplew.writeHexString("50 8C 01 00 01 01 00 A8 1C 84 A0 E0 D9 01 80 E9 A8 F3 57 43 DA 01 69 00 00 00 50 8C 01 00 00 00 00 00 00 00 00 00 00 FC 00 00 00 FD 00 00 00 FE 00 00 00 10 0E 00 00 0C 00 63 68 61 72 69 6F 74 49 6E 66 6F 38 00 00 0C 00 63 68 61 72 69 6F 74 50 61 73 73 38 0E 00 63 68 61 72 69 6F 74 41 74 74 65 6E 64 38 00 00 00 00 0F 00 00 00 3F 00 00 00 10 38 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 38 00 00 00 8F 2F 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 31 00 00 00 DA 30 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 69 00 00 00 15 38 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 2A 00 00 00 13 38 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 62 00 00 00 99 2F 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 23 00 00 00 8F 2F 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 5B 00 00 00 0D 32 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1C 00 00 00 D9 30 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 54 00 00 00 14 38 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 15 00 00 00 1B 28 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 4D 00 00 00 99 2F 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0E 00 00 00 1D 28 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 46 00 00 00 DB 30 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 00 00 0C 38 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 E6 04 00 00");
        return mplew.getPacket();
    }

    public static byte[] getAchievementUI(MaplePacketReader slea, MapleCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.ACHIEVEMENT_UI.getValue());
        mplew.writeHexString("04 00 00 00");
        player.dropMessage(15, "201_:" + Arrays.toString(mplew.getPacket()));
        return mplew.getPacket();
    }

    public static byte[] getShowUserEffectNotice(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.BOSS_EVENT_SHOW_USER_EFFECT_NOTICE.getValue());
        mplew.write(0);
        mplew.writeInt(4000);
        mplew.write(102);
        mplew.writeAsciiString(msg);
        mplew.writeInt(67);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] startDamageRecord() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_StartDamageRecord.getValue());
        mplew.writeBool(true);
        return mplew.getPacket();
    }
}

