/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.MapleSlideMenu$SlideMenu0
 *  Net.server.maps.MapleSlideMenu$SlideMenu1
 *  Net.server.maps.MapleSlideMenu$SlideMenu2
 *  Net.server.maps.MapleSlideMenu$SlideMenu3
 *  Net.server.maps.MapleSlideMenu$SlideMenu4
 *  Net.server.maps.MapleSlideMenu$SlideMenu5
 *  Net.server.maps.MapleSlideMenu$SlideMenu6
 *  Net.server.quest.MapleQuestRequirement
 *  Net.server.quest.MapleQuestRequirementType
 *  Plugin.script.EventManager
 *  SwordieX.client.party.Party
 *  SwordieX.client.party.PartyMember
 *  SwordieX.client.party.PartyResult
 *  connection.packet.WvsContext
 *  lombok.Generated
 *  tools.SearchGenerator
 *  tools.SearchGenerator$SearchType
 */
package Plugin.script.binding;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Config.constants.ItemConstants;
import Config.constants.enums.NpcMessageType;
import Config.constants.enums.ScriptParam;
import Config.constants.enums.ScriptType;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.maps.MapleSlideMenu;
import Net.server.quest.MapleQuest;
import Net.server.quest.MapleQuestRequirement;
import Net.server.quest.MapleQuestRequirementType;
import Opcode.header.OutHeader;
import Packet.MaplePacketCreator;
import Packet.UIPacket;
import Plugin.script.EventManager;
import Plugin.script.NpcScriptInfo;
import Plugin.script.binding.PlayerScriptInteraction;
import Plugin.script.binding.ScriptEvent;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import SwordieX.client.party.PartyResult;
import SwordieX.world.World;
import connection.packet.ScriptMan;
import connection.packet.WvsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.SearchGenerator;
import tools.data.MaplePacketLittleEndianWriter;

import static Config.constants.enums.NpcMessageType.*;

public class ScriptNpc
extends PlayerScriptInteraction {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptNpc.class);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final String scriptPath;
    private final int npcId;
    private final MapleClient client;
    private final Object obj;
    private ScriptType scriptType = ScriptType.None;
    private int lastSMType;

    public ScriptNpc(MapleClient client, int npcId, String scriptPath, ScriptType scriptType, Object obj) {
        super(client.getPlayer());
        this.client = client;
        this.npcId = npcId;
        this.obj = obj;
        this.scriptPath = scriptPath;
        this.scriptType = scriptType;
        if (scriptType == ScriptType.Command) {
            this.setVariable("commands", obj);
        }
    }

    public int getNpcId() {
        return this.npcId;
    }

    public int getItemId() {
        if (this.scriptType == ScriptType.Item) {
            return ((Item)this.obj).getItemId();
        }
        return 0;
    }

    private Object sendScriptMessage(String text, NpcMessageType nmt) throws NullPointerException {
        String checkText = text.replaceAll("[\r\n]", "");
        if (checkText.matches("(.)*#[lL][0-9]+#(.)*")) {
            nmt = NpcMessageType.AskMenu;
        }
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo().deepCopy();
        nsi.setText(text);
        nsi.setMessageType(nmt);
        if (nmt != NpcMessageType.Say) {
            nsi.setNextPossible(false);
            nsi.setPrevPossible(false);
        }
        this.getPlayer().getScriptManager().getMemory().add(nsi);
        this.getPlayer().write(ScriptMan.scriptMessage(nsi, nmt));
        Object response = null;
        if (this.getPlayer().getScriptManager().isActive(this.getPlayer().getScriptManager().getLastActiveScriptType())) {
            response = this.getPlayer().getScriptManager().getScriptInfoByType(this.getPlayer().getScriptManager().getLastActiveScriptType()).awaitResponse();
        }
        if (response == null) {
            throw new NullPointerException("Intended NPE by forceful Plugin.script stop.");
        }
        return response;
    }

    private int sendGeneralSay(String text, NpcMessageType nmt, boolean hasNext) throws NullPointerException {
        String checkText = text.replaceAll("[\r\n]", "");
        if (checkText.matches("(.)*#[lL][0-9]+#(.)*")) {
            nmt = NpcMessageType.AskMenu;
        }
        if (nmt == NpcMessageType.Say) {
            NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
            nsi.setNextPossible(hasNext);
            nsi.setPrevPossible(this.getPlayer().getScriptManager().getMemory().hasBack());
        }
        return (int) (long) sendScriptMessage(text, nmt);
    }

    private int sendGeneralSay(String message, NpcMessageType nmt) throws NullPointerException {
        return this.sendGeneralSay(message, nmt, false);
    }

    public String askQuiz(byte type, String title, String problemText, String hintText, int min, int max, int time) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setType(type);
        nsi.setTitle(title);
        nsi.setProblemText(problemText);
        nsi.setHintText(hintText);
        nsi.setMin(min);
        nsi.setMax(max);
        nsi.setTime(time);
        return (String)this.sendScriptMessage("", NpcMessageType.InitialQuiz);
    }

    public String askSpeedQuiz(byte type, int quizType, int answer, int correctAnswers, int remaining, int time) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setType(type);
        nsi.setQuizType(quizType);
        nsi.setAnswer(answer);
        nsi.setCorrectAnswers(correctAnswers);
        nsi.setRemaining(remaining);
        nsi.setTime(time);
        return (String)this.sendScriptMessage("", NpcMessageType.InitialSpeedQuiz);
    }

    public String askText(String message, String defaultText, short minLength, short maxLength) throws NullPointerException {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setMin(minLength);
        nsi.setMax(maxLength);
        nsi.setDefaultText(defaultText);
        return (String)this.sendScriptMessage(message, NpcMessageType.AskText);
    }

    public String askBoxText(String def, short columns, short rows) throws NullPointerException {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setDefaultText(def);
        nsi.setCol(columns);
        nsi.setLine(rows);
        return (String)this.sendScriptMessage("", NpcMessageType.AskBoxtext);
    }

    public long askNumber(String message, long def, long min, long max) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setDefaultNumber(def);
        nsi.setMin(min);
        nsi.setMax(max);
        return (long)this.sendScriptMessage(message, NpcMessageType.AskNumber);
    }

    public int askPet(String message, List<Item> list) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setItems(list);
        return (int)(long) sendScriptMessage(message, AskPet);
    }

    public int askAvatar(String message, int itemId, int secondLookValue, int srcBeauty, int[] styles) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setItemID(itemId);
        nsi.setSecondLookValue(secondLookValue);
        nsi.setOptions(styles);
        nsi.setSrcBeauty(srcBeauty);
        return (int) (long) sendScriptMessage(message, AskAvatar);
    }

    public int askAvatarZero(String message, int itemId, int srcBeauty, int srcBeauty2, int[] styles, int[] styles2) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setItemID(itemId);
        nsi.setOptions(styles);
        nsi.setOptions2(styles2);
        nsi.setSrcBeauty(srcBeauty);
        nsi.setSrcBeauty2(srcBeauty2);
        return (int) (long) sendScriptMessage(message, AskAvatarZero);
    }

    public boolean askAngelicBuster() {
        return (long)this.sendScriptMessage("", NpcMessageType.AskAngelicBuster) != 0L;
    }

    public int askAvatarMixColor(int cardID, String msg, int secondLookValue, int srcBeauty) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setItemID(cardID);
        nsi.setSecondLookValue(secondLookValue);
        nsi.setSrcBeauty(srcBeauty);
        return (int) (long) sendScriptMessage(msg, AskAvatarMixColor);
    }

    public boolean askAvatarRandomMixColor(String msg) {
        return this.askAvatarRandomMixColor(null, null, msg);
    }

    public boolean askAvatarRandomMixColor(Integer itemID, Integer secondLookValue, String msg) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        if (itemID != null) {
            nsi.setItemID(itemID);
        }
        if (secondLookValue != null) {
            nsi.setSecondLookValue(secondLookValue);
        }
        return (long)this.sendScriptMessage(msg, NpcMessageType.AskAvatarRandomMixColor) != 0L;
    }

    public int sayAvatarMixColorChanged(String msg, int srcBeauty, int drtBeauty, int srcBeauty2, int drtBeauty2) {
        return this.sayAvatarMixColorChanged(msg, null, srcBeauty, drtBeauty, srcBeauty2, drtBeauty2);
    }

    public int sayAvatarMixColorChanged(String msg, Integer itemID, int srcBeauty, int drtBeauty, int srcBeauty2, int drtBeauty2) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        if (itemID != null) {
            nsi.setItemID(itemID);
        }
        nsi.setSrcBeauty(srcBeauty);
        nsi.setDrtBeauty(drtBeauty);
        nsi.setSrcBeauty2(srcBeauty2);
        nsi.setDrtBeauty2(drtBeauty2);
        return (int) (long) sendScriptMessage(msg, SayAvatarMixColorChanged);
    }

    public boolean askConfirmAvatarChange(int srcBeauty, int srcBeauty2) {
        return this.askConfirmAvatarChange(null, null, srcBeauty, srcBeauty2);
    }

    public boolean askConfirmAvatarChange(Integer itemID, Integer secondLookValue, int srcBeauty, int srcBeauty2) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        if (itemID != null) {
            nsi.setItemID(itemID);
        }
        if (secondLookValue != null) {
            nsi.setSecondLookValue(secondLookValue);
        }
        nsi.setSrcBeauty(srcBeauty);
        nsi.setSrcBeauty2(srcBeauty2);
        return (long)this.sendScriptMessage("", NpcMessageType.AskConfirmAvatarChange) != 0L;
    }

    public int sendOkN(String text) {
        return this.sendOkE(text, this.npcId);
    }

    public int sayN(String text) {
        return this.sendOkE(text, this.npcId);
    }

    public int sendOkN(String text, int idd) {
        return this.sendOkE(text, idd);
    }

    public int sayN(String text, int idd) {
        return this.sendOkE(text, idd);
    }

    public int sendOkS(String text, byte type) {
        return this.sendOkS(text, type, this.npcId);
    }

    public int sayS(String text, byte type) {
        return this.sendOkS(text, type, this.npcId);
    }

    public int sendOkS(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(false);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);

    }

    public int sendPlayerToNpc(String text) {
        return this.sendNextS(text, (byte)3, this.npcId);
    }

    public int sayPlayerToNpc(String text) {
        return this.sendNextS(text, (byte)3, this.npcId);
    }

    public int sendNextS(String text, byte type) {
        return this.sendNextS(text, type, this.npcId);
    }

    public int sayNextS(String text, byte type) {
        return this.sendNextS(text, type, this.npcId);
    }

    public int sendNextS(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);

    }

    public int sendNextN(String text) {
        return this.sendNextN(text, NpcMessageType.Say.getVal(), this.getNpcId());
    }

    public int sayNextN(String text) {
        return this.sendNextN(text, NpcMessageType.Say.getVal(), this.getNpcId());
    }

    public int sendNextN(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(4);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendPrevS(String text, byte type) {
        return this.sendPrevS(text, type, this.npcId);
    }

    public int sayPrevS(String text, byte type) {
        return this.sendPrevS(text, type, this.npcId);
    }

    public int sendPrevS(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(true);
        nsi.setNextPossible(false);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendPrevN(String text) {
        return this.sendPrevN(text, NpcMessageType.Say.getVal(), this.npcId);
    }

    public int sayPrevN(String text) {
        return this.sendPrevN(text, NpcMessageType.Say.getVal(), this.npcId);
    }

    public int sendPrevN(String text, byte type) {
        return this.sendPrevN(text, type, this.getNpcId());
    }

    public int sayPrevN(String text, byte type) {
        return this.sendPrevN(text, type, this.getNpcId());
    }

    public int sendPrevN(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(4);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int PlayerToNpc(String text) {
        return this.sendNextPrevS(text, (byte)3);
    }

    public int sendNextPrevS(String text, byte type) {
        return this.sendNextPrevS(text, type, this.npcId);
    }

    public int sayNextPrevS(String text, byte type) {
        return this.sendNextPrevS(text, type, this.npcId);
    }

    public int sendNextPrevS(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(true);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendNextPrevN(String text) {
        return this.sendNextPrevS(text, NpcMessageType.Say.getVal());
    }

    public int sayNextPrevN(String text) {
        return this.sendNextPrevS(text, NpcMessageType.Say.getVal());
    }

    public int sendNextPrevN(String text, byte type) {
        return this.sendNextPrevN(text, type, this.getNpcId());
    }

    public int sayNextPrevN(String text, byte type) {
        return this.sendNextPrevN(text, type, this.getNpcId());
    }

    public int sendNextPrevN(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(4);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(true);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendAcceptDecline(String text) {
        return this.askAccept(text);
    }

    public int sayAcceptDecline(String text) {
        return this.askAccept(text);
    }

    public int sendAcceptDeclineNoESC(String text) {
        return this.askAcceptNoESC(text);
    }

    public int sayAcceptDeclineNoESC(String text) {
        return this.askAcceptNoESC(text);
    }

    public int askAcceptDecline(String text) {
        return this.askAcceptDecline(text, this.npcId);
    }

    public int askAcceptDecline(String text, int id) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(id);
        nsi.setSpeakerType(4);
        nsi.setParam(0);
        nsi.setText(text);
        return (int) (long) sendScriptMessage(text, AskAccept);

    }

    public int askAcceptDeclineNoESC(String text) {
        return this.askAcceptDeclineNoESC(text, this.npcId);
    }

    public int askAcceptDeclineNoESC(String text, int id) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(id);
        nsi.setSpeakerType(4);
        nsi.setParam(1);
        nsi.setText(text);
        return (int) (long) sendScriptMessage(text, AskAccept);
    }

    public int askMapSelection(String sel) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(4);
        nsi.setParam(0);
        nsi.setText(sel);
        nsi.setDefaultText("");
        nsi.setCol((short)(this.npcId == 3000012 ? 5 : (this.npcId == 9010000 ? 3 : (this.npcId == 2083006 ? 1 : 0))));
        nsi.setLine((short)(this.npcId == 9010022 ? 1 : 0));
        return (int) (long) sendScriptMessage(sel, AskBoxtext);

    }

    public int sendSimple(String text) {
        return this.askMenu(text);
    }

    public int sendSimple(String text, int id) {
        return this.askMenu(text, id);
    }

    public int sendSimpleS(String text, byte type) {
        return this.sendSimpleS(text, type, this.npcId);
    }

    public int sendSimpleS(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(3);
        nsi.setParam(type);
        nsi.setText(text);
        return (int) (long) sendScriptMessage(text, AskMenu);

    }

    public int sendSimpleN(String text) {
        return this.sendSimpleN(text, NpcMessageType.askZeroNext.getVal(), this.getNpcId());
    }

    public int askZeroWeaponSayNext(int npcid, String text) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setSpeakerType(1);
        nsi.setTemplateID(npcid);
        return (int) (long) sendScriptMessage(text, askZeroNext);

    }

    public int sendSimpleN(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(3);
        nsi.setColor(0);
        nsi.setParam(type);
        nsi.setText(text);
        return (int) (long) sendScriptMessage(text, Say);

    }

    public int askAvatar(String text, int[] styles, int card, boolean isSecond) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(4);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setSecondLookValue(isSecond ? 1 : 0);
        nsi.setText(text);
        nsi.setOptions(styles);
        nsi.setItemID(card);
        nsi.setSrcBeauty(0);
        return (int) (long) sendScriptMessage(text, AskAvatar);

    }

    public int sendZeroSpirt(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeInt(405996);
        mplew.write(3);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(type);
        mplew.writeShort(0);
        mplew.writeInt(1);
        mplew.writeInt(idd);
        mplew.writeMapleAsciiString(text);
        mplew.write(0);
        mplew.write(1);
        mplew.write(0);
        mplew.writeInt(0);
        return (int) (long) sendScriptMessage(text, AskAvatarZero);

    }

    public int sendStyle(String text, int[] styles, int card, boolean isSecond) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(4);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setText(text);
        nsi.setOptions(styles);
        nsi.setItemID(card);
        nsi.setSrcBeauty(0);
        return (int) (long) sendScriptMessage(text, AskAndroid);

    }

    public int sendAStyle(String text, int[] styles, int card) {
        return this.askAndroid(text, styles, card);
    }

    public int sendGetNumber(String text, long def, long min, long max) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(4);
        nsi.setParam(0);
        nsi.setText(text);
        nsi.setDefaultNumber(def);
        nsi.setMin(min);
        nsi.setMax(max);
        return (int) (long) sendScriptMessage(text, AskNumber);

    }

    public String sendGetText(String text) {
        return this.sendGetText(text, this.npcId);
    }

    public String sendGetText(String text, int id) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(id);
        nsi.setSpeakerType(4);
        nsi.setParam(0);
        nsi.setText(text);
        nsi.setDefaultText("");
        nsi.setMin(0L);
        nsi.setMax(0L);
        return (String)this.sendScriptMessage(text, NpcMessageType.AskText);
    }

    public int sendPlayerOk(String text) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(ScriptParam.PlayerAsSpeakerFlip.getValue());
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(false);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);

    }

    public int sendPlayerOk(String text, byte type, int npcId) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(npcId);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(false);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);

    }

    public int sendPlayerPrev(String text, byte type, int npcId) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(npcId);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(true);
        nsi.setNextPossible(false);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendPlayerNext(String text) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam((short)(ScriptParam.PlayerAsSpeaker.getValue() | ScriptParam.PlayerAsSpeakerFlip.getValue()));
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendPlayerNext(String text, byte type, int npcId) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(npcId);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(false);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendPlayerNextPrev(String text) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam((short)(ScriptParam.PlayerAsSpeaker.getValue() | ScriptParam.PlayerAsSpeakerFlip.getValue()));
        nsi.setText(text);
        nsi.setPrevPossible(true);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendPlayerNextPrev(String text, byte type, int npcId) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(npcId);
        nsi.setSpeakerType(3);
        nsi.setOverrideSpeakerTemplateID(0);
        nsi.setParam(type);
        nsi.setText(text);
        nsi.setPrevPossible(true);
        nsi.setNextPossible(true);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(text, Say);
    }

    public int sendRevivePet(String text) {
        return this.askPetRevive(text);
    }

    public int sendPlayerStart(String text) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(3);
        nsi.setParam(ScriptParam.PlayerAsSpeakerFlip.getValue());
        nsi.setText(text);
        return (int) (long) sendScriptMessage(text, AskAccept);

    }

    public int sendSlideMenu(int type, String sel) {
        String[] arrstring = sel.split("#");
        if (arrstring.length < 3) {
            return -1;
        }
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(NpcMessageType.AskText.getVal());
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setDlgType(type);
        nsi.setDefaultSelect(Integer.valueOf(arrstring[arrstring.length - 2]));
        nsi.setText(sel);
        return (int) (long) sendScriptMessage(sel, AskSlideMenu);

    }

    public String getSlideMenuSelection(int type) {
        switch (type) {
            case 1: {
                return MapleSlideMenu.SlideMenu1.getSelectionInfo((MapleCharacter)this.getPlayer(), (int)this.npcId);
            }
            case 2: {
                return MapleSlideMenu.SlideMenu2.getSelectionInfo((MapleCharacter)this.getPlayer(), (int)this.npcId);
            }
            case 3: {
                return MapleSlideMenu.SlideMenu3.getSelectionInfo((MapleCharacter)this.getPlayer(), (int)this.npcId);
            }
            case 4: {
                return MapleSlideMenu.SlideMenu4.getSelectionInfo((MapleCharacter)this.getPlayer(), (int)this.npcId);
            }
            case 5: {
                return MapleSlideMenu.SlideMenu5.getSelectionInfo((MapleCharacter)this.getPlayer(), (int)this.npcId);
            }
            case 6: {
                return MapleSlideMenu.SlideMenu6.getSelectionInfo((MapleCharacter)this.getPlayer(), (int)this.npcId);
            }
        }
        return MapleSlideMenu.SlideMenu0.getSelectionInfo((MapleCharacter)this.getPlayer(), (Integer)this.npcId);
    }

    public int[] getSlideMenuDataIntegers(int type, int selection) {
        switch (type) {
            case 1: {
                return MapleSlideMenu.SlideMenu1.getDataIntegers((int)selection);
            }
            case 2: {
                return MapleSlideMenu.SlideMenu2.getDataIntegers((int)selection);
            }
            case 3: {
                return MapleSlideMenu.SlideMenu3.getDataIntegers((int)selection);
            }
            case 4: {
                return MapleSlideMenu.SlideMenu4.getDataIntegers((int)selection);
            }
            case 5: {
                return MapleSlideMenu.SlideMenu5.getDataIntegers((int)selection);
            }
            case 6: {
                return MapleSlideMenu.SlideMenu6.getDataIntegers((int)selection);
            }
        }
        return MapleSlideMenu.SlideMenu0.getDataIntegers((Integer)selection);
    }

    public int sendOk(String s) {
        return this.sendOk(s, this.npcId, ScriptParam.Normal, true);
    }

    public int say(String s) {
        return this.sendOk(s, this.npcId, ScriptParam.Normal, true);
    }

    public int sendOk(String s, int n) {
        return this.sendOk(s, n, ScriptParam.Normal, true);
    }

    public int say(String s, int n) {
        return this.sendOk(s, n, ScriptParam.Normal, true);
    }

    public int sendOk(String message, boolean bLeft) {
        return this.sendOk(message, this.getNpcId(), ScriptParam.Normal, bLeft);
    }

    public int say(String message, boolean bLeft) {
        return this.sendOk(message, this.getNpcId(), ScriptParam.Normal, bLeft);
    }

    public int sendOkNoESC(String message) {
        return this.sendOkNoESC(message, true);
    }

    public int sayNoESC(String message) {
        return this.sendOkNoESC(message, true);
    }

    public int sendOkNoESC(String message, boolean bLeft) {
        return this.sendOk(message, this.getNpcId(), ScriptParam.NoEsc, bLeft);
    }

    public int sayNoESC(String message, boolean bLeft) {
        return this.sendOk(message, this.getNpcId(), ScriptParam.NoEsc, bLeft);
    }

    public int sendOkS(String s) {
        return this.sendOkS(s, ScriptParam.Normal, true);
    }

    public int sayS(String s) {
        return this.sendOkS(s, ScriptParam.Normal, true);
    }

    public int sendOkS(String s, boolean b) {
        return this.sendOkS(s, ScriptParam.Normal, b);
    }

    public int sayS(String s, boolean b) {
        return this.sendOkS(s, ScriptParam.Normal, b);
    }

    public int sendOkE(String s) {
        return this.sendOkE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sayE(String s) {
        return this.sendOkE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sendOkE(String s, int n) {
        return this.sendOkE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sayE(String s, int n) {
        return this.sendOkE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sendOkENoESC(String s) {
        return this.sendOkE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sayENoESC(String s) {
        return this.sendOkE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sendOkENoESC(String s, int n) {
        return this.sendOkE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sayENoESC(String s, int n) {
        return this.sendOkE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sayENoESC(String s, int n, int n2) {
        return this.sendOkE(s, n, ScriptParam.NoEsc, n < 0, n2);
    }

    public int sendNext(String s) {
        return this.sendNext(s, 0, ScriptParam.Normal, true);
    }

    public int sayNext(String s) {
        return this.sendNext(s, 0, ScriptParam.Normal, true);
    }

    public int sendNext(String s, int n) {
        return this.sendNext(s, n, ScriptParam.Normal, false);
    }

    public int sayNext(String s, int n) {
        return this.sendNext(s, n, ScriptParam.Normal, false);
    }

    public int sendNext(String s, boolean b) {
        return this.sendNext(s, this.getNpcId(), ScriptParam.Normal, b);
    }

    public int sayNext(String s, boolean b) {
        return this.sendNext(s, this.getNpcId(), ScriptParam.Normal, b);
    }

    public int sendNextNoESC(String s) {
        return this.sendNext(s, this.getNpcId(), ScriptParam.NoEsc, true);
    }

    public int sayNextNoESC(String s) {
        return this.sendNext(s, this.getNpcId(), ScriptParam.NoEsc, true);
    }

    public int sendNextNoESC(String s, boolean b) {
        return this.sendNext(s, this.getNpcId(), ScriptParam.NoEsc, b);
    }

    public int sayNextNoESC(String s, boolean b) {
        return this.sendNext(s, this.getNpcId(), ScriptParam.NoEsc, b);
    }

    public int sendNextNoESC(String s, int n) {
        return this.sendNext(s, n, ScriptParam.NoEsc, false);
    }

    public int sayNextNoESC(String s, int n) {
        return this.sendNext(s, n, ScriptParam.NoEsc, false);
    }

    public int sendNextS(String s) {
        return this.sendNextS(s, ScriptParam.Normal, true);
    }

    public int sayNextS(String s) {
        return this.sendNextS(s, ScriptParam.Normal, true);
    }

    public int sendNextS(String s, boolean b) {
        return this.sendNextS(s, ScriptParam.Normal, b);
    }

    public int sayNextS(String s, boolean b) {
        return this.sendNextS(s, ScriptParam.Normal, b);
    }

    public int sendNextSNoESC(String s) {
        return this.sendNextS(s, ScriptParam.NoEsc, true);
    }

    public int sayNextSNoESC(String s) {
        return this.sendNextS(s, ScriptParam.NoEsc, true);
    }

    public int sendNextE(String s) {
        return this.sendNextE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sayNextE(String s) {
        return this.sendNextE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sendNextE(String s, int n) {
        return this.sendNextE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sayNextE(String s, int n) {
        return this.sendNextE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sendNextENoESC(String s) {
        return this.sendNextE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sayNextENoESC(String s) {
        return this.sendNextE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sendNextENoESC(String s, int n) {
        return this.sendNextE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sayNextENoESC(String s, int n) {
        return this.sendNextE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sendNextENoESC(String s, int n, int n2) {
        return this.sendNextE(s, n, ScriptParam.NoEsc, n < 0, n2);
    }

    public int sayNextENoESC(String s, int n, int n2) {
        return this.sendNextE(s, n, ScriptParam.NoEsc, n < 0, n2);
    }

    public int sendPrev(String s) {
        return this.sendPrev(s, 0, ScriptParam.Normal, true);
    }

    public int sayPrev(String s) {
        return this.sendPrev(s, 0, ScriptParam.Normal, true);
    }

    public int sendPrevS(String s) {
        return this.sendPrevS(s, ScriptParam.Normal, true);
    }

    public int sayPrevS(String s) {
        return this.sendPrevS(s, ScriptParam.Normal, true);
    }

    public int sendPrevE(String s) {
        return this.sendPrevE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sayPrevE(String s) {
        return this.sendPrevE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sendPrevE(String s, int n) {
        return this.sendPrevE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sayPrevE(String s, int n) {
        return this.sendPrevE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sendPrevENoESC(String s) {
        return this.sendPrevE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sayPrevENoESC(String s) {
        return this.sendPrevE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sendPrevENoESC(String s, int n) {
        return this.sendPrevE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sayPrevENoESC(String s, int n) {
        return this.sendPrevE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sendPrevENoESC(String s, int n, int n2) {
        return this.sendPrevE(s, n, ScriptParam.NoEsc, n < 0, n2);
    }

    public int sayPrevENoESC(String s, int n, int n2) {
        return this.sendPrevE(s, n, ScriptParam.NoEsc, n < 0, n2);
    }

    public int sendNextPrev(String s) {
        return this.sendNextPrev(s, 0, ScriptParam.Normal, true);
    }

    public int sayNextPrev(String s) {
        return this.sendNextPrev(s, 0, ScriptParam.Normal, true);
    }

    public int sendNextPrev(String s, boolean b) {
        return this.sendNextPrev(s, this.getNpcId(), ScriptParam.Normal, b);
    }

    public int sayNextPrev(String s, boolean b) {
        return this.sendNextPrev(s, this.getNpcId(), ScriptParam.Normal, b);
    }

    public int sendNextPrev(String s, int n) {
        return this.sendNextPrev(s, n, ScriptParam.Normal, false);
    }

    public int sayNextPrev(String s, int n) {
        return this.sendNextPrev(s, n, ScriptParam.Normal, false);
    }

    public int sendNextPrevNoESC(String s) {
        return this.sendNextPrevNoESC(s, true);
    }

    public int sayNextPrevNoESC(String s) {
        return this.sendNextPrevNoESC(s, true);
    }

    public int sendNextPrevNoESC(String s, boolean b) {
        return this.sendNextPrev(s, this.getNpcId(), ScriptParam.NoEsc, b);
    }

    public int sayNextPrevNoESC(String s, boolean b) {
        return this.sendNextPrev(s, this.getNpcId(), ScriptParam.NoEsc, b);
    }

    public int sendNextPrevNoESC(String s, int n) {
        return this.sendNextPrev(s, n, ScriptParam.NoEsc, false);
    }

    public int sayNextPrevNoESC(String s, int n) {
        return this.sendNextPrev(s, n, ScriptParam.NoEsc, false);
    }

    public int sendNextPrevS(String s) {
        return this.sendNextPrevS(s, ScriptParam.Normal, true);
    }

    public int sayNextPrevS(String s) {
        return this.sendNextPrevS(s, ScriptParam.Normal, true);
    }

    public int sendNextPrevS(String s, boolean b) {
        return this.sendNextPrevS(s, ScriptParam.Normal, b);
    }

    public int sayNextPrevS(String s, boolean b) {
        return this.sendNextPrevS(s, ScriptParam.Normal, b);
    }

    public int sendNextPrevSNoESC(String s) {
        return this.sendNextPrevS(s, ScriptParam.NoEsc, true);
    }

    public int sayNextPrevSNoESC(String s) {
        return this.sendNextPrevS(s, ScriptParam.NoEsc, true);
    }

    public int sendNextPrevSNoESC(String s, boolean b) {
        return this.sendNextPrevS(s, ScriptParam.NoEsc, b);
    }

    public int sayNextPrevSNoESC(String s, boolean b) {
        return this.sendNextPrevS(s, ScriptParam.NoEsc, b);
    }

    public int sendNextPrevE(String s) {
        return this.sendNextPrevE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sayNextPrevE(String s) {
        return this.sendNextPrevE(s, 0, ScriptParam.Normal, false, 0);
    }

    public int sendNextPrevE(String s, int n) {
        return this.sendNextPrevE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sayNextPrevE(String s, int n) {
        return this.sendNextPrevE(s, n, ScriptParam.Normal, n < 0, 0);
    }

    public int sendNextPrevENoESC(String s) {
        return this.sendNextPrevE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sayNextPrevENoESC(String s) {
        return this.sendNextPrevE(s, 0, ScriptParam.NoEsc, false, 0);
    }

    public int sendNextPrevENoESC(String s, int n) {
        return this.sendNextPrevE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sayNextPrevENoESC(String s, int n) {
        return this.sendNextPrevE(s, n, ScriptParam.NoEsc, n < 0, 0);
    }

    public int sendNextPrevENoESC(String s, int n, int n2) {
        return this.sendNextPrevE(s, n, ScriptParam.NoEsc, n < 0, n2);
    }

    public int sayNextPrevENoESC(String s, int n, int n2) {
        return this.sendNextPrevE(s, n, ScriptParam.NoEsc, n < 0, n2);
    }

    public int askReplace(String s) {
        return this.askYesNo(s, this.getNpcId(), ScriptParam.Replace, true);
    }

    public int askYesNo(String s) {
        return this.askYesNo(s, true);
    }

    public int askYesNo(String s, int n) {
        return this.askYesNo(s, n, ScriptParam.Normal, false);
    }

    public int askYesNo(String s, boolean b) {
        return this.askYesNo(s, this.getNpcId(), ScriptParam.Normal, b);
    }

    public int askYesNoNoESC(String s) {
        return this.askYesNo(s, this.getNpcId(), ScriptParam.NoEsc, true);
    }

    public int askYesNoNoESC(String s, boolean b) {
        return this.askYesNo(s, this.getNpcId(), ScriptParam.NoEsc, b);
    }

    public int askYesNoS(String s) {
        return this.askYesNoS(s, ScriptParam.Normal, true);
    }

    public int askYesNoS(String s, boolean b) {
        return this.askYesNoS(s, ScriptParam.Normal, b);
    }

    public int askYesNoE(String s) {
        return this.askYesNoE(s, 0, ScriptParam.Normal, false);
    }

    public int askYesNoE(String s, int n) {
        return this.askYesNoE(s, n, ScriptParam.Normal, n < 0);
    }

    public int askMenu(String s) {
        return this.askMenu(s, this.npcId, ScriptParam.Normal, false);
    }

    public int askMenu(String s, int n) {
        return this.askMenu(s, n, ScriptParam.Normal, false);
    }

    public int askMenu(String s, boolean b) {
        return this.askMenu(s, this.npcId, ScriptParam.Normal, b);
    }

    public int askMenuNoESC(String s) {
        return this.askMenu(s, this.getNpcId(), ScriptParam.NoEsc, true);
    }

    public int askMenuNoESC(String s, boolean b) {
        return this.askMenu(s, this.getNpcId(), ScriptParam.NoEsc, b);
    }

    public int askMenuNoESC(String s, int n) {
        return this.askMenu(s, n, ScriptParam.NoEsc, false);
    }

    public int askMenuS(String s) {
        return this.askMenuS(s, ScriptParam.Normal, true);
    }

    public int askMenuE(String s) {
        return this.askMenuE(s, false);
    }

    public int askMenuE(String s, boolean b) {
        return this.askMenuE(s, 0, ScriptParam.Normal, b);
    }

    public int askMenuA(String s) {
        return this.askMenuA(s, false);
    }

    public int askMenuA(String s, boolean b) {
        return this.askMenuA(s, 0, ScriptParam.Normal, b);
    }

    public int askMenuA(String msg, int diffnpc) {
        return this.askMenuA(msg, diffnpc, ScriptParam.Normal, false);
    }

    public int askAccept(String s) {
        return this.askAccept(s, 0, ScriptParam.Normal, true);
    }

    public int askAccept(String msg, int diffNpcID) {
        return this.askAccept(msg, diffNpcID, ScriptParam.Normal, true);
    }

    public int askAccept(String s, boolean bLeft) {
        return this.askAccept(s, this.getNpcId(), ScriptParam.Normal, bLeft);
    }

    public int askAcceptNoESC(String s) {
        return this.askAccept(s, this.getNpcId(), ScriptParam.NoEsc, true);
    }

    public int askAcceptNoESC(String s, boolean b) {
        return this.askAccept(s, this.getNpcId(), ScriptParam.NoEsc, b);
    }

    public int askAcceptS(String s) {
        return this.askAcceptS(s, ScriptParam.Normal, true);
    }

    public int askAcceptE(String s) {
        return this.askAcceptE(s, 0, ScriptParam.Normal, false);
    }

    public void askText(String s, short n, short n2) {
        this.askText(s, "", n, n2);
    }

    public void askTextNoESC(String s, short n, short n2) {
        this.askTextNoESC(s, "", n, n2);
    }

    public String askTextNoESC(String s, String def, short n, short n2) {
        return this.askText(s, this.getNpcId(), ScriptParam.NoEsc, n, n2, true, def);
    }

    public String askTextS(String s, short n, short n2) {
        return this.askTextS(s, "", n, n2);
    }

    public String askTextS(String s, String def, short n, short n2) {
        return this.askTextS(s, n, n2, true, def);
    }

    public String askTextE(String s, short n, short n2) {
        return this.askTextE(s, "", n, n2);
    }

    public String askTextE(String s, String def, short n, short n2) {
        return this.askTextE(s, n, n2, false, def);
    }

    public int askNumber(String s, int n, int n2, int n3) {
        return this.askNumber(s, 0, n, n2, n3, true);
    }

    public int askNumberKeypad(int n) {
        return this.askNumberKeypad((byte)4, this.getNpcId(), 0, ScriptParam.Normal.getValue(), n);
    }

    public int askUserSurvey(int n, String s) {
        return this.askUserSurvey((byte)4, this.getNpcId(), 0, ScriptParam.Normal.getValue(), n, s);
    }

    public int askNumberS(String s, int n, int n2, int n3) {
        return this.askNumberS(s, n, n2, n3, true);
    }

    public int askNumberE(String s, int n, int n2, int n3) {
        return this.askNumberE(s, n, n2, n3, false);
    }

    public String askBoxText(String s, String s2, short n, short n2) {
        return this.askBoxText(s, s2, 0, n, n2, true);
    }

    public String askBoxTextS(String s, String s2, short n, short n2) {
        return this.askBoxTextS(s, s2, n, n2, true);
    }

    public String askBoxTextE(String s, String s2, short n, short n2) {
        return this.askBoxTextE(s, s2, n, n2, false);
    }

    public int askSlideMenu(int n, String s) {
        String[] split = s.split("#");
        if (split.length < 3) {
            return -1;
        }
        return this.askSlideMenu((byte)4, this.getNpcId(), n, Integer.valueOf(split[split.length - 2]), s);
    }

    public int askAvatar(String message, int[] array, int needItem, boolean isangel, boolean isbeta) {
        return this.askAvatar((byte)4, this.getNpcId(), needItem, isangel, isbeta, array, message);
    }

    public int askAndroid(String s, int[] array, int n) {
        return this.askAndroid((byte)4, this.getNpcId(), n, array, s);
    }

    public int askPetRevive(String s) {
        ArrayList<Item> list = new ArrayList<Item>();
        for (Item pet : this.getAllPetItem()) {
            if (pet.getExpiration() <= 0L || pet.getExpiration() >= System.currentTimeMillis()) continue;
            list.add(pet);
        }
        if (list.isEmpty()) {
            return this.sendOk("你沒有失去魔法的寵物.");
        }
        return this.askPet(s, list);
    }

    public List<Item> getAllPetItem() {
        ArrayList<Item> list = new ArrayList<Item>();
        for (Item item : this.getPlayer().getInventory(MapleInventoryType.CASH).getInventory().values()) {
            if (!ItemConstants.類型.寵物(item.getItemId())) continue;
            list.add(item);
        }
        return list;
    }

    public int askSelectMenu(int n) {
        return this.askSelectMenu((byte)3, n, 1, null);
    }

    public int askSelectMenu(int n, int n2, String[] array) {
        return this.askSelectMenu((byte)4, n, n2, array);
    }

    public int askPetEvolution(String s, List<Item> list) {
        return this.askPetEvolution((byte)4, this.getNpcId(), list, s);
    }

    public int askPetAll(String s) {
        return this.askPetAll(s, this.getAllPetItem());
    }

    public int askPetAll(String s, List<Item> list) {
        return this.askPetAll((byte)4, this.getNpcId(), list, s);
    }

    public int sayImage(String[] array) {
        return this.sayImage((byte)4, 3, 0, (short)3, array);
    }

    public String askQuiz(boolean b, int n, int n2, String s, String s2, String s3) {
        return this.askQuiz((byte)0, this.getNpcId(), 0, 0, b, n, n2, s, s2, s3);
    }

    public String askSpeedQuiz(boolean b, int n, int n2, int n3, int n4, int n5) {
        return this.askSpeedQuiz((byte)0, this.getNpcId(), 0, 0, b, n, n2, n3, n4, n5);
    }

    public String askICQuiz(boolean b, String s, String s2, int n) {
        return this.askICQuiz((byte)0, this.getNpcId(), 0, 0, b, s, s2, n);
    }

    public String askOlympicQuiz(boolean b, int n, int n2, int n3, int n4, int n5) {
        return this.askOlympicQuiz((byte)0, this.getNpcId(), 0, 0, b, n, n2, n3, n4, n5);
    }

    public String sendGetText(String text, String def, int col, int line) {
        return this.askText((byte)4, this.npcId, this.npcId, ScriptParam.OverrideSpeakerID.getValue(), (short)col, (short)line, text, def);
    }

    public int sendOkIllu(String s, int n, int n2, boolean b) {
        return this.sendOkIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sayIllu(String s, int n, int n2, boolean b) {
        return this.sendOkIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sendOkIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendOkIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    public int sayIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendOkIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    public int sendNextIllu(String s, int n, int n2, boolean b) {
        return this.sendNextIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sayNextIllu(String s, int n, int n2, boolean b) {
        return this.sendNextIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sendNextIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendNextIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    public int sayNextIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendNextIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    public int sendPrevIllu(String s, int n, int n2, boolean b) {
        return this.sendPrevIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sayPrevIllu(String s, int n, int n2, boolean b) {
        return this.sendPrevIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sendPrevIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendPrevIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    public int sayPrevIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendPrevIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    public int sendNextPrevIllu(String s, int n, int n2, boolean b) {
        return this.sendNextPrevIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sayNextPrevIllu(String s, int n, int n2, boolean b) {
        return this.sendNextPrevIllu(s, n, ScriptParam.Normal, true, n, n2, b);
    }

    public int sendNextPrevIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendNextPrevIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    public int sayNextPrevIlluNoESC(String s, int n, int n2, boolean b) {
        return this.sendNextPrevIllu(s, n, ScriptParam.NoEsc, true, n, n2, b);
    }

    private int sendSay(byte type, int npcId, int n3, int u2, boolean bPrev, boolean bNext, String sText, int n5) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(npcId);
        nsi.setSpeakerType(type);
        nsi.setOverrideSpeakerTemplateID(npcId);
        nsi.setParam((short)u2);
        nsi.setText(sText);
        nsi.setPrevPossible(bPrev);
        nsi.setNextPossible(bNext);
        nsi.setDelay(0);
        return (int) (long) sendScriptMessage(sText, Say);
        
    }

    private int sendOk(String s, int n, ScriptParam j906, boolean bLeft) {
        return this.sendSay((byte)4, this.npcId, n, (bLeft ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), false, false, s, 0);
    }

    private int sendOkS(String s, ScriptParam j906, boolean b) {
        return this.sendSay((byte)3, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue()) | j906.getValue(), false, false, s, 0);
    }

    private int sendOkE(String msg, int npcId, ScriptParam j906, boolean b, int n2) {
        return this.sendSay((byte)(b ? 3 : 4), npcId, npcId, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.BoxChat.getValue() | j906.getValue(), false, false, msg, n2);
    }

    private int sendNext(String s, int n, ScriptParam j906, boolean b) {
        return this.sendSay((byte)4, this.npcId, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), false, true, s, 0);
    }

    private int sendNextS(String s, ScriptParam j906, boolean b) {
        return this.sendSay((byte)3, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue()) | j906.getValue(), false, true, s, 0);
    }

    private int sendNextE(String s, int n, ScriptParam j906, boolean b, int n2) {
        return this.sendSay((byte)(b ? 3 : 4), this.npcId, n, (b ? ScriptParam.PlayerAsSpeaker.getValue() : (n > 0 ? ScriptParam.OverrideSpeakerID.getValue() : ScriptParam.Normal.getValue())) | ScriptParam.BoxChat.getValue() | j906.getValue(), false, true, s, n2);
    }

    private int sendPrev(String s, int n, ScriptParam j906, boolean b) {
        return this.sendSay((byte)4, this.npcId, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), true, false, s, 0);
    }

    private int sendPrevS(String s, ScriptParam j906, boolean b) {
        return this.sendSay((byte)3, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue()) | j906.getValue(), true, false, s, 0);
    }

    private int sendPrevE(String s, int n, ScriptParam j906, boolean b, int n2) {
        return this.sendSay((byte)(b ? 3 : 4), this.npcId, n, (b ? ScriptParam.PlayerAsSpeaker.getValue() : (n > 0 ? ScriptParam.OverrideSpeakerID.getValue() : ScriptParam.Normal.getValue())) | ScriptParam.BoxChat.getValue() | j906.getValue(), true, false, s, n2);
    }

    private int sendNextPrev(String s, int n, ScriptParam j906, boolean b) {
        return this.sendSay((byte)4, this.npcId, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), true, true, s, 0);
    }

    private int sendNextPrevS(String s, ScriptParam j906, boolean b) {
        return this.sendSay((byte)3, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue()) | j906.getValue(), true, true, s, 0);
    }

    private int sendNextPrevE(String s, int n, ScriptParam j906, boolean b, int n2) {
        return this.sendSay((byte)(b ? 3 : 4), this.npcId, n, (b ? ScriptParam.PlayerAsSpeaker.getValue() : (n > 0 ? ScriptParam.OverrideSpeakerID.getValue() : ScriptParam.Normal.getValue())) | ScriptParam.BoxChat.getValue() | j906.getValue(), true, true, s, n2);
    }

    private int askYesNo(byte b, int n, int n2, int n3, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n3);
        nsi.setText(s);
        return (int) (long) sendScriptMessage(s, AskYesNo);

    }

    private int askYesNo(String s, int n, ScriptParam j906, boolean b) {
        return this.askYesNo((byte)4, this.npcId, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), s);
    }

    public int sendYesNo(String text) {
        return this.sendYesNo(text, this.npcId);
    }

    public int sendYesNo(String text, int idd) {
        return this.askYesNo(text, idd);
    }

    public int sendYesNoS(String text, byte type) {
        return this.sendYesNoS(text, type, this.npcId);
    }

    public int sendYesNoS(String text, byte type, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(this.npcId);
        nsi.setSpeakerType(3);
        nsi.setParam(type);
        nsi.setText(text);
        return (int)(long)this.sendScriptMessage(text, NpcMessageType.AskYesNo);
    }

    public int sendYesNoN(String text) {
        return this.sendYesNoN(text, this.getNpcId());
    }

    public int sendYesNoN(String text, int idd) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(idd);
        nsi.setSpeakerType(4);
        nsi.setParam((short)(ScriptParam.OverrideSpeakerID.getValue() | ScriptParam.BoxChat.getValue()));
        nsi.setText(text);
        return (int)(long)this.sendScriptMessage(text, NpcMessageType.AskYesNo);
    }

    private int askYesNoS(String s, ScriptParam j906, boolean b) {
        return this.askYesNo((byte)3, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue()) | j906.getValue(), s);
    }

    private int askYesNoE(String s, int n, ScriptParam j906, boolean b) {
        return this.askYesNo((byte)(b ? 3 : 4), this.npcId, n, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.BoxChat.getValue() | j906.getValue(), s);
    }

    private int askMenu(byte b, int n, int diffnpc, int n3, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(diffnpc > 0 ? diffnpc : n);
        nsi.setSpeakerType(b);
        nsi.setParam(n3);
        nsi.setText(s);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskMenu);
    }

    private int askMenu(String s, int n, ScriptParam j906, boolean b) {
        return this.askMenu((byte)4, this.npcId, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), s);
    }

    private int askMenuS(String s, ScriptParam j906, boolean b) {
        return this.askMenu((byte)3, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue()) | j906.getValue(), s);
    }

    private int askMenuE(String s, int n, ScriptParam j906, boolean b) {
        return this.askMenu((byte)(b ? 3 : 4), this.npcId, n, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.BoxChat.getValue() | j906.getValue(), s);
    }

    private int askMenuA(String s, int diffnpc, ScriptParam j906, boolean b) {
        return this.askMenu((byte)(b ? 3 : 4), this.npcId, diffnpc, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.LargeBoxChat.getValue() | j906.getValue(), s);
    }

    private int askAccept(byte b, int n, int diffnpc, int n3, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n3);
        nsi.setText(s);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskAccept);
    }

    private int askAccept(String s, int diffnpc, ScriptParam j906, boolean b) {
        return this.askAccept((byte)4, this.npcId, diffnpc, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), s);
    }

    private int askAcceptS(String s, ScriptParam j906, boolean b) {
        return this.askAccept((byte)3, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue()) | j906.getValue(), s);
    }

    private int askAcceptE(String s, int diffnpc, ScriptParam j906, boolean b) {
        return this.askAccept((byte)(b ? 3 : 4), this.npcId, diffnpc, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.BoxChat.getValue() | j906.getValue(), s);
    }

    private String askText(byte b, int n, int n2, int n3, short nLenMin, short nLenMax, String sMsg, String sMsgDefault) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam((short)n3);
        nsi.setText(sMsg);
        nsi.setDefaultText(sMsgDefault);
        nsi.setMin(nLenMin);
        nsi.setMax(nLenMax);
        return (String)this.sendScriptMessage(sMsg.isEmpty() ? sMsgDefault : sMsg, NpcMessageType.AskText);
    }

    private String askText(String sMsg, int n, ScriptParam j906, short nLenMin, short nLenMax, boolean bLeft, String sMsgDefault) {
        return this.askText((byte)4, this.npcId, n, (bLeft ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), nLenMin, nLenMax, sMsg, sMsgDefault);
    }

    private String askTextS(String s, short n, short n2, boolean b, String sMsgDefault) {
        return this.askText((byte)3, this.npcId, 0, b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue(), n, n2, s, sMsgDefault);
    }

    private String askTextE(String s, short n, short n2, boolean b, String sMsgDefault) {
        return this.askText((byte)4, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.BoxChat.getValue(), n, n2, s, sMsgDefault);
    }

    private int askNumber(byte b, int n, int n2, int n3, long n4, long n5, long n6, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam((short)n3);
        nsi.setText(s);
        nsi.setDefaultNumber(n4);
        nsi.setMin(n5);
        nsi.setMax(n6);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskNumber);
    }

    private int askNumber(String s, int n, int n2, int n3, int n4, boolean b) {
        return this.askNumber((byte)4, this.npcId, n, b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue(), n2, n3, n4, s);
    }

    private int askNumberS(String s, int n, int n2, int n3, boolean b) {
        return this.askNumber((byte)3, this.npcId, 0, b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue(), n, n2, n3, s);
    }

    private int askNumberE(String s, int n, int n2, int n3, boolean b) {
        return this.askNumber((byte)4, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.BoxChat.getValue(), n, n2, n3, s);
    }

    private String askBoxText(byte b, int n, int n2, int n3, short n4, short n5, String s, String s2) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n3);
        nsi.setText(s);
        nsi.setDefaultText(s2);
        nsi.setCol(n4);
        nsi.setLine(n4);
        return (String)this.sendScriptMessage(s.isEmpty() ? s2 : s, NpcMessageType.AskBoxtext);
    }

    private String askBoxText(String s, String s2, int n, short n2, short n3, boolean b) {
        return this.askBoxText((byte)4, this.npcId, n, b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue(), n2, n3, s, s2);
    }

    private String askBoxTextS(String s, String s2, short n, short n2, boolean b) {
        return this.askBoxText((byte)3, this.npcId, 0, b ? ScriptParam.PlayerAsSpeakerFlip.getValue() : ScriptParam.PlayerAsSpeaker.getValue(), n, n2, s, s2);
    }

    private String askBoxTextE(String s, String s2, short n, short n2, boolean b) {
        return this.askBoxText((byte)4, this.npcId, 0, (b ? ScriptParam.PlayerAsSpeaker.getValue() : ScriptParam.Normal.getValue()) | ScriptParam.BoxChat.getValue(), n, n2, s, s2);
    }

    private int askSlideMenu(byte b, int n, int n2, int n3, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setDlgType(n2);
        nsi.setDefaultSelect(n3);
        nsi.setText(s);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskSlideMenu);
    }

    private int askAvatar(byte b, int n, int n2, boolean b2, boolean b3, int[] array, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setSecondLookValue(b2 ? 1 : (b3 ? 2 : 0));
        nsi.setText(s);
        nsi.setOptions(array);
        nsi.setItemID(n2);
        nsi.setSrcBeauty(0);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskAvatar);
    }

    public void askAvatarZero(int cardID, int[] array, int[] array2, String s) {
        this.askAvatarZero((byte)4, this.npcId, cardID, array, array2, s);
    }

    public int askAvatarZero(byte nSpeakerTypeID, int nSpeakerTemplateID, int cardID, int[] array, int[] array2, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(nSpeakerTemplateID);
        nsi.setSpeakerType(nSpeakerTypeID);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setText(s);
        nsi.setItemID(cardID);
        nsi.setOptions(array);
        nsi.setOptions2(array2);
        nsi.setSrcBeauty(0);
        nsi.setSrcBeauty2(0);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskAvatarZero);
    }

    public void askAndroid(int cardID, int[] array, String s) {
        this.askAndroid((byte)4, this.npcId, cardID, array, s);
    }

    public int askAndroid(byte nSpeakerTypeID, int nSpeakerTemplateID, int cardID, int[] array, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(nSpeakerTemplateID);
        nsi.setSpeakerType(nSpeakerTypeID);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setText(s);
        nsi.setOptions(array);
        nsi.setItemID(cardID);
        nsi.setSrcBeauty(0);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskAndroid);
    }

    private int askPet(byte b, int n, List<Item> list, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setText(s);
        nsi.setItems(list);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskPet);
    }

    private int askSelectMenu(byte b, int n, int n2, String[] array) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setOverrideSpeakerTemplateID(n);
        nsi.setDlgType(n2);
        nsi.setDefaultSelect(0);
        nsi.setSelectText(array);
        return (int)(long)this.sendScriptMessage("", NpcMessageType.AskSelectMenu);
    }

    private int askPetEvolution(byte b, int n, List<Item> list, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setText(s);
        nsi.setItems(list);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskActionPetEvolution);
    }

    private int askPetAll(byte b, int n, List<Item> list, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(0);
        nsi.setColor(0);
        nsi.setText(s);
        nsi.setItems(list);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskPetAll);
    }

    private int sayImage(byte b, int n, int n2, short n3, String[] array) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n2);
        nsi.setImages(array);
        return (int)(long)this.sendScriptMessage("", NpcMessageType.AskPetAll);
    }

    private int sendSayIllu(byte b, int n, int n2, int n3, boolean b2, boolean b3, String s, int n4, int n5, boolean b4) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n3);
        nsi.setText(s);
        nsi.setPrevPossible(b2);
        nsi.setNextPossible(b3);
        nsi.setDelay(n4);
        nsi.setUnk(n5);
        nsi.setBUnk(b4);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.SayIllustration);
    }

    private int sendOkIllu(String s, int n, ScriptParam j906, boolean b, int n2, int n3, boolean b2) {
        return this.sendSayIllu((byte)4, n, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), false, false, s, n2, n3, b2);
    }

    private int sendNextIllu(String s, int n, ScriptParam j906, boolean b, int n2, int n3, boolean b2) {
        return this.sendSayIllu((byte)4, n, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), false, true, s, n2, n3, b2);
    }

    private int sendPrevIllu(String s, int n, ScriptParam j906, boolean b, int n2, int n3, boolean b2) {
        return this.sendSayIllu((byte)4, n, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), true, false, s, n2, n3, b2);
    }

    private int sendNextPrevIllu(String s, int n, ScriptParam j906, boolean b, int n2, int n3, boolean b2) {
        return this.sendSayIllu((byte)4, n, n, (b ? ScriptParam.Normal.getValue() : ScriptParam.OverrideSpeakerID.getValue()) | j906.getValue(), true, true, s, n2, n3, b2);
    }

    private String askQuiz(byte b, int n, int n2, int n3, boolean b2, int n4, int n5, String s, String s2, String s3) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n2);
        nsi.setType(b2 ? 0 : 1);
        nsi.setTitle(s);
        nsi.setProblemText(s2);
        nsi.setHintText(s3);
        nsi.setMin(n4);
        nsi.setMax(n5);
        nsi.setTime(0);
        return (String)this.sendScriptMessage("", NpcMessageType.InitialQuiz);
    }

    private String askSpeedQuiz(byte b, int n, int n2, int n3, boolean b2, int n4, int n5, int n6, int n7, int n8) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n2);
        nsi.setType(b2 ? 0 : 1);
        nsi.setQuizType(n4);
        nsi.setAnswer(n5);
        nsi.setCorrectAnswers(n6);
        nsi.setRemaining(n7);
        nsi.setTime(n8);
        return (String)this.sendScriptMessage("", NpcMessageType.InitialSpeedQuiz);
    }

    private String askICQuiz(byte b, int n, int n2, int n3, boolean b2, String s, String s2, int n4) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n3);
        nsi.setType(b2 ? 0 : 1);
        nsi.setText(s);
        nsi.setHintText(s2);
        nsi.setTime(n4);
        return (String)this.sendScriptMessage(s, NpcMessageType.ICQuiz);
    }

    private String askOlympicQuiz(byte b, int n, int n2, int n3, boolean b2, int nType, int nQuestion, int nCorrect, int nRemain, int tRemainInitialQuiz) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(1);
        nsi.setTemplateID(n);
        nsi.setSpeakerType(b);
        nsi.setParam(n3);
        nsi.setType(b2 ? 0 : 1);
        nsi.setQuizType(nType);
        nsi.setAnswer(nQuestion);
        nsi.setCorrectAnswers(nCorrect);
        nsi.setRemaining(nRemain);
        nsi.setTime(tRemainInitialQuiz);
        return (String)this.sendScriptMessage("", NpcMessageType.AskOlympicQuiz);
    }

    private int askNumberKeypad(byte b, int n, int n2, int n3, int n4) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(3);
        nsi.setParam(n3);
        nsi.setColor(0);
        nsi.setDefaultNumber(n4);
        return (int)(long)this.sendScriptMessage("", NpcMessageType.OnAskNumberUseKeyPad);
    }

    private int askUserSurvey(byte b, int n, int n2, int n3, int n4, String s) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(n);
        nsi.setSpeakerType(3);
        nsi.setParam(n3);
        nsi.setColor(0);
        nsi.setDefaultNumber(n4);
        nsi.setText(s);
        return (int)(long)this.sendScriptMessage(s, NpcMessageType.AskUserSurvey);
    }

    public ScriptNpc id(int npcId) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTemplateID(npcId);
        return this;
    }

    public ScriptNpc overrideSpeakerId(int npcId) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        return this;
    }

    public ScriptNpc noEsc() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.addParam(ScriptParam.NoEsc);
        return this;
    }

    public ScriptNpc me() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.addParam(ScriptParam.PlayerAsSpeakerFlip);
        return this;
    }

    public ScriptNpc npcFlip() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.addParam(ScriptParam.FlipSpeaker);
        return this;
    }

    public ScriptNpc meFlip() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.addParam(ScriptParam.PlayerAsSpeaker);
        return this;
    }

    public ScriptNpc npcRightSide() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.addParam(ScriptParam.OverrideSpeakerID);
        return this;
    }

    public ScriptNpc replace() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.addParam(ScriptParam.Replace);
        return this;
    }

    public ScriptNpc line(int line) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setLine(line);
        return this;
    }

    public ScriptNpc ui(int ui) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setColor(ui);
        return this;
    }

    public ScriptNpc uiMax() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.addParam(ScriptParam.LargeBoxChat);
        return this;
    }

    public ScriptNpc prev() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setPrevPossible(true);
        return this;
    }

    public ScriptNpc next() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setNextPossible(true);
        return this;
    }

    public ScriptNpc defText(String defText) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setDefaultText(defText);
        return this;
    }

    public ScriptNpc minLen(int minLen) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setMin(minLen);
        return this;
    }

    public ScriptNpc maxLen(int maxLen) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setMax(maxLen);
        return this;
    }

    public ScriptNpc defNum(int defNum) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setDefaultNumber(defNum);
        return this;
    }

    public ScriptNpc minNum(int minNum) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setMin(minNum);
        return this;
    }

    public ScriptNpc maxNum(int maxNum) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setMax(maxNum);
        return this;
    }

    public ScriptNpc time(int time) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setTime(time);
        return this;
    }

    public ScriptNpc delay(int delay) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setDelay(delay);
        return this;
    }

    public ScriptNpc cardId(int cardId) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setItemID(cardId);
        return this;
    }

    public ScriptNpc col(int col) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setCol(col);
        return this;
    }

    public ScriptNpc styles(int[] styles) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setOptions(styles);
        return this;
    }

    public ScriptNpc styles2(int[] styles) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setOptions2(styles);
        return this;
    }

    public ScriptNpc defSel(int defSel) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setDefaultSelect(defSel);
        return this;
    }

    public ScriptNpc speakerType(int speakerType) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setSpeakerType(speakerType);
        return this;
    }

    public ScriptNpc items(List<Item> list) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setItems(list);
        return this;
    }

    public ScriptNpc setSrcBeautyX(int setSrcBeauty) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setSrcBeauty(setSrcBeauty);
        return this;
    }

    public ScriptNpc setSrcBeautyX2(int setSrcBeauty) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setSrcBeauty2(setSrcBeauty);
        return this;
    }

    public ScriptNpc secondLookValueX(int secondLookValue) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setSecondLookValue(secondLookValue);
        return this;
    }

    public void sayX(String text) {
        this.sendScriptMessage(text, NpcMessageType.Say);
    }

    public void sayImageX(String[] text) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setImages(text);
        this.sendScriptMessage("", NpcMessageType.SayImage);
        this.resetNpc();
    }

    public String askTextX(String text) {
        String result = (String)this.sendScriptMessage(text, NpcMessageType.AskText);
        this.resetNpc();
        return result;
    }

    public String askBoxTextX(String text) {
        String result = (String)this.sendScriptMessage(text, NpcMessageType.AskBoxtext);
        this.resetNpc();
        return result;
    }

    public int askMenuX(String text) {
        long result = (long)this.sendScriptMessage(text, NpcMessageType.AskMenu);
        this.resetNpc();
        return (int)result;
    }

    public boolean askYesNoX(String text) {
        boolean result = (long)this.sendScriptMessage(text, NpcMessageType.AskYesNo) == 1L;
        this.resetNpc();
        return result;
    }

    public boolean askAcceptX(String text) {
        boolean result = (long)this.sendScriptMessage(text, NpcMessageType.AskAccept) == 1L;
        this.resetNpc();
        return result;
    }

    public long askNumberX(String message) {
        long result = (long)this.sendScriptMessage(message, NpcMessageType.AskNumber);
        this.resetNpc();
        return result;
    }

    public boolean askAngelicBusterX() {
        boolean result = (long)this.sendScriptMessage("", NpcMessageType.AskAngelicBuster) != 0L;
        this.resetNpc();
        return result;
    }

    public int askAvatarX(String message) {
        int result = (int)(long)this.sendScriptMessage(message, NpcMessageType.AskAvatar);
        this.resetNpc();
        return result;
    }

    public long askPetX(String message) {
        long result = (long)this.sendScriptMessage(message, NpcMessageType.AskPet);
        this.resetNpc();
        return result;
    }

    public long askAndroidX(String message) {
        long result = (long)this.sendScriptMessage(message, NpcMessageType.AskAndroid);
        this.resetNpc();
        return result;
    }

    public int askAvatarZeroX(String message) {
        int result = (int)(long)this.sendScriptMessage(message, NpcMessageType.AskAvatarZero);
        this.resetNpc();
        return result;
    }

    public int askAvatarMixColorX(String msg) {
        int result = (int)(long)this.sendScriptMessage(msg, NpcMessageType.AskAvatarMixColor);
        this.resetNpc();
        return result;
    }

    public boolean askAvatarRandomMixColorX(String msg) {
        boolean result = (long)this.sendScriptMessage(msg, NpcMessageType.AskAvatarRandomMixColor) != 0L;
        this.resetNpc();
        return result;
    }

    public int sayAvatarMixColorChangedX(String msg) {
        int result = (int)(long)this.sendScriptMessage(msg, NpcMessageType.SayAvatarMixColorChanged);
        this.resetNpc();
        return result;
    }

    public boolean askConfirmAvatarChangeX() {
        boolean result = (long)this.sendScriptMessage("", NpcMessageType.AskConfirmAvatarChange) != 0L;
        this.resetNpc();
        return result;
    }

    private void resetNpc() {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        NpcScriptInfo new_nsi = new NpcScriptInfo();
        new_nsi.setTemplateID(nsi.getTemplateID());
        this.getPlayer().getScriptManager().setNpcScriptInfo(new_nsi);
    }

    public void openWebUI(int n, String sUOL, String sURL) {
        this.getClient().announce(MaplePacketCreator.openWebUI(n, sUOL, sURL));
    }

    public void openWeb(String sURL) {
        this.getClient().announce(MaplePacketCreator.openWeb((byte)0, (byte)1, sURL));
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
        this.getClient().getPlayer().getParty().disband();
    }

    public void startQuest() {
        MapleQuest quest = MapleQuest.getInstance((Integer)this.obj);
        int npc_id = this.npcId;
        for (MapleQuestRequirement qr : quest.getStartReqs()) {
            if (qr.getType() != MapleQuestRequirementType.npc) continue;
            npc_id = qr.getIntStore();
            break;
        }
        quest.forceStart(this.getPlayer(), npc_id, "");
    }

    public void completeQuest() {
        MapleQuest quest = MapleQuest.getInstance((Integer)this.obj);
        int npc_id = this.npcId;
        for (MapleQuestRequirement qr : quest.getCompleteReqs()) {
            if (qr.getType() != MapleQuestRequirementType.npc) continue;
            npc_id = qr.getIntStore();
            break;
        }
        quest.forceComplete(this.getPlayer(), npc_id, false);
    }

    @Override
    public ScriptEvent makeEvent(String script, Object attachment) {
        ScriptEvent event = new EventManager(script, this.getPlayer().getClient().getChannel(), null).runScript(this.getPlayer(), script, true, attachment);
        return event;
    }

    public short getPosition() {
        return ((Item)this.obj).getPosition();
    }

    public boolean used() {
        return this.used(1);
    }

    public boolean used(int q) {
        return MapleInventoryManipulator.removeFromSlot(this.getClient(), ItemConstants.getInventoryType(this.getItemId()), this.getPosition(), (short)q, true, false);
    }

    public void showPopupSay(int npcid, int time, String msg, String sound) {
        this.getClient().announce(UIPacket.addPopupSay(npcid, time, msg, sound));
    }

    @Override
    public MapleItemInformationProvider getItemInfo() {
        return MapleItemInformationProvider.getInstance();
    }

    public Map<Integer, String> getSearchData(int type, String search) {
        return SearchGenerator.getSearchData((int)type, (String)search);
    }

    public String searchData(int type, String search) {
        return SearchGenerator.searchData((SearchGenerator.SearchType)SearchGenerator.SearchType.valueOf((String)SearchGenerator.SearchType.nameOf((int)type)), (String)search);
    }

    public void greenTip(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ProgressMessageFont.getValue());
        mplew.writeInt(3);
        mplew.writeInt(20);
        mplew.writeInt(20);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(message);
        this.getPlayer().send(mplew.getPacket());
    }

    public void saying(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_ScriptMessage.getValue());
        mplew.writeInt(1401380);
        mplew.write(3);
        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeShort(57);
        mplew.write(1);
        mplew.write(1);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(message);
        mplew.write(0);
        mplew.write(1);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        this.getPlayer().send(mplew.getPacket());
    }

    public int zeroSayNext(int npc, String message) {
        NpcScriptInfo nsi = this.getPlayer().getScriptManager().getNpcScriptInfo();
        nsi.setParam(1);
        nsi.setColor(1);
        nsi.setTemplateID(npc);
        return (int)(long)this.sendScriptMessage(message, NpcMessageType.askZeroNext);
    }

    public void playScreen(String s) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FieldEffect.getValue());
        mplew.write(19);
        mplew.writeMapleAsciiString(s);
        this.getPlayer().send(mplew.getPacket());
    }

    public void playSound(String s) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FieldEffect.getValue());
        mplew.write(7);
        mplew.writeMapleAsciiString(s);
        mplew.writeInt(100);
        mplew.writeInt(0);
        mplew.writeInt(0);
        this.getPlayer().send(mplew.getPacket());
    }

    @Generated
    public String getScriptPath() {
        return this.scriptPath;
    }

    @Override
    @Generated
    public MapleClient getClient() {
        return this.client;
    }

    @Generated
    public int getLastSMType() {
        return this.lastSMType;
    }

    @Generated
    public void setLastSMType(int lastSMType) {
        this.lastSMType = lastSMType;
    }
}

