/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.commands.SuperGMCommand$HotTime
 *  Net.server.events.DimensionMirrorEvent
 *  Packet.EldasPacket
 *  Packet.GuildPacket
 *  Packet.MessengerPacket
 *  Plugin.script.EventManager
 *  Plugin.script.binding.ScriptParty
 *  Server.BossEventHandler.Caning
 *  Server.world.WorldBroadcastService
 *  SwordieX.client.party.Party
 *  SwordieX.client.party.PartyMember
 *  SwordieX.client.party.PartyResult
 *  connection.packet.WvsContext
 *  java.net.http.HttpClient
 *  java.net.http.HttpRequest
 *  java.net.http.HttpRequest$BodyPublishers
 *  java.net.http.HttpResponse
 *  java.net.http.HttpResponse$BodyHandlers
 *  lombok.Generated
 */
package Plugin.script.binding;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.MaplePet;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.enums.ScriptType;
import Config.constants.enums.UserChatMessageType;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.RaffleItem;
import Net.server.RafflePool;
import Net.server.commands.SuperGMCommand;
import Net.server.events.DimensionMirrorEvent;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterInformationProvider;
import Net.server.life.MapleMonsterStats;
import Net.server.life.MapleNPC;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleReactor;
import Net.server.maps.MapleReactorFactory;
import Net.server.quest.MapleQuest;
import Opcode.Opcode.EffectOpcode;
import Opcode.header.OutHeader;
import Packet.EffectPacket;
import Packet.EldasPacket;
import Packet.GuildPacket;
import Packet.MaplePacketCreator;
import Packet.MessengerPacket;
import Packet.UIPacket;
import Plugin.script.EventManager;
import Plugin.script.ScriptManager;
import Plugin.script.binding.ScriptBase;
import Plugin.script.binding.ScriptEvent;
import Plugin.script.binding.ScriptField;
import Plugin.script.binding.ScriptHelper;
import Plugin.script.binding.ScriptNpc;
import Plugin.script.binding.ScriptParty;
import Plugin.script.binding.ScriptPlayer;
import Server.BossEventHandler.Caning;
import Server.channel.ChannelServer;
import Server.world.WorldBroadcastService;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import SwordieX.client.party.PartyResult;
import SwordieX.world.World;
import connection.packet.WvsContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;

public class PlayerScriptInteraction
extends ScriptBase {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(PlayerScriptInteraction.class);
    private static MapleNPC npc;
    private final MapleCharacter player;
    private final MapleClient client;
    private int quest;
    private boolean start;
    private JFrame frame;
    private JTextField handlerField;
    private JTextArea packetContentArea;

    public PlayerScriptInteraction(MapleCharacter player) {
        this.player = player;
        this.client = player.getClient();
    }

    public static int getNpc() {
        return npc.getId();
    }

    public ScriptEvent getEvent() {
        return this.getPlayer().getEventInstance();
    }

    public ScriptEvent makeEvent(String script, Object attachment) {
        ScriptEvent event = new EventManager(script, this.getPlayer().getClient().getChannel(), null).runScript(this.getPlayer(), script, true, attachment);
        return event;
    }

    public ScriptEvent getEvent(String event) {
        for (MapleMap map : ChannelServer.getInstance(this.getPlayer().getClient().getChannel()).getMapFactory().getAllMaps()) {
            if (map.getEvent() == null || !map.getEvent().getName().equals(event)) continue;
            return map.getEvent();
        }
        return null;
    }

    public List<MapleCharacter> getChannelPlayers() {
        int channel = this.getClient().getChannelServer().getChannel();
        return ChannelServer.getInstance(channel).getPlayerStorage().getAllCharacters();
    }

    public int[] resetRememberedMap(String variable) {
        String rMap = this.getPlayer().getQuestInfo(100642, variable + "_rMap");
        String rPoratl = this.getPlayer().getQuestInfo(100642, variable + "_rPoratl");
        if (rMap == null || rMap.equals("")) {
            rMap = "100000000";
        } else {
            this.getPlayer().updateOneQuestInfo(100642, variable + "_rMap", "");
        }
        if (rPoratl == null || rPoratl.equals("")) {
            rPoratl = "0";
        } else {
            this.getPlayer().updateOneQuestInfo(100642, variable + "_rPoratl", "");
        }
        return new int[]{Integer.parseInt(rMap), Integer.parseInt(rPoratl)};
    }

    public void rememberMap(String variable) {
        this.getPlayer().updateOneQuestInfo(100642, variable + "_rMap", Integer.toString(this.getPlayer().getMapId()));
        this.getPlayer().updateOneQuestInfo(100642, variable + "_rPoratl", null);
    }

    public String getRememberedMap(String variable) {
        return this.getPlayer().getQuestInfo(100642, variable + "_rMap");
    }

    public Item makeItemWithId(int itemId) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!ii.itemExists(itemId)) {
            this.getPlayer().dropMessage(5, itemId + " 這個道具不存在.");
            return null;
        }
        short flag = (short)ItemAttribute.Seal.getValue();
        MaplePet pet = ItemConstants.類型.寵物(itemId) ? MaplePet.createPet(itemId) : null;
        Item item = new Item(itemId, (short) 0,  (short)1, 0);
        item.setPet(pet);
        return item;
    }

    public void setVariable(String key, Object value) {
        if (value == null || "".equals(value)) {
            this.getPlayer().getVariable().remove(key);
        } else {
            this.getPlayer().getVariable().put(key, value);
        }
    }

    public Object getVariable(String key) {
        return this.getPlayer().getVariable().get(key);
    }

    public String httpGet(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).header("Content-Type", "application/json").GET().timeout(Duration.ofSeconds(5L)).build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();
            if (responseCode == 200) {
                return (String)response.body();
            }
            return null;
        }
        catch (Exception e) {
            log.error("[httpPost]error:", e);
            return null;
        }
    }

    public String httpPost(String url, String body) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString((String)body, (Charset)StandardCharsets.UTF_8)).timeout(Duration.ofSeconds(5L)).build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();
            if (responseCode == 200) {
                return (String)response.body();
            }
            return null;
        }
        catch (Exception e) {
            log.error("[httpPost]error:", e);
            return null;
        }
    }

    public void startBossUI(int BossType, int[] difficulty) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CTX_PORTAL_BOSS_EVENT_UI.getValue());
        mplew.writeInt(BossType);
        mplew.writeBool(false);
        mplew.writeInt(difficulty.length);
        for (int i = 0; i < difficulty.length; ++i) {
            mplew.write(difficulty[i]);
            mplew.writeHexString("00 00 00 05 00 00 00 00 00 00 00 01 05 00 00 00");
        }
        mplew.writeInt(3);
        mplew.writeInt(0);
        mplew.writeInt(8);
        mplew.writeInt(0);
        mplew.writeInt(80003600);
        mplew.writeInt(1);
        mplew.writeInt(80003601);
        mplew.writeInt(2);
        mplew.writeInt(80003602);
        mplew.writeInt(3);
        mplew.writeInt(80003603);
        mplew.writeInt(4);
        mplew.writeInt(80003604);
        mplew.writeInt(5);
        mplew.writeInt(80003605);
        mplew.writeInt(6);
        mplew.writeInt(80003606);
        mplew.writeInt(7);
        mplew.writeInt(80003607);
        mplew.writeInt(1);
        mplew.writeInt(11);
        mplew.writeInt(0);
        mplew.writeInt(-2024172);
        mplew.writeInt(1);
        mplew.writeInt(-2024173);
        mplew.writeInt(2);
        mplew.writeInt(-2024174);
        mplew.writeInt(3);
        mplew.writeInt(-2024175);
        mplew.writeInt(4);
        mplew.writeInt(-2024176);
        mplew.writeInt(5);
        mplew.writeInt(-2024177);
        mplew.writeInt(6);
        mplew.writeInt(-2024178);
        mplew.writeInt(7);
        mplew.writeInt(-2024179);
        mplew.writeInt(8);
        mplew.writeInt(-2024180);
        mplew.writeInt(9);
        mplew.writeInt(-2024181);
        mplew.writeInt(10);
        mplew.writeInt(-2024182);
        mplew.writeInt(2);
        mplew.writeInt(14);
        mplew.writeInt(0);
        mplew.writeInt(-2024193);
        mplew.writeInt(1);
        mplew.writeInt(-2024187);
        mplew.writeInt(2);
        mplew.writeInt(-2024186);
        mplew.writeInt(3);
        mplew.writeInt(-2024194);
        mplew.writeInt(4);
        mplew.writeInt(-2024195);
        mplew.writeInt(5);
        mplew.writeInt(-2024188);
        mplew.writeInt(6);
        mplew.writeInt(-2024183);
        mplew.writeInt(7);
        mplew.writeInt(-2024184);
        mplew.writeInt(8);
        mplew.writeInt(-2024185);
        mplew.writeInt(9);
        mplew.writeInt(-2024189);
        mplew.writeInt(10);
        mplew.writeInt(-2024190);
        mplew.writeInt(11);
        mplew.writeInt(-2024191);
        mplew.writeInt(12);
        mplew.writeInt(-2024192);
        mplew.writeInt(13);
        mplew.writeInt(-2024237);
        mplew.writeInt(3);
        mplew.writeInt(0);
        mplew.writeInt(8);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(2);
        mplew.writeInt(2);
        mplew.writeInt(3);
        mplew.writeInt(3);
        mplew.writeInt(4);
        mplew.writeInt(4);
        mplew.writeInt(5);
        mplew.writeInt(5);
        mplew.writeInt(6);
        mplew.writeInt(6);
        mplew.writeInt(7);
        mplew.writeInt(7);
        mplew.writeInt(1);
        mplew.writeInt(11);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(2);
        mplew.writeInt(2);
        mplew.writeInt(3);
        mplew.writeInt(3);
        mplew.writeInt(4);
        mplew.writeInt(4);
        mplew.writeInt(5);
        mplew.writeInt(5);
        mplew.writeInt(6);
        mplew.writeInt(6);
        mplew.writeInt(7);
        mplew.writeInt(7);
        mplew.writeInt(8);
        mplew.writeInt(8);
        mplew.writeInt(9);
        mplew.writeInt(9);
        mplew.writeInt(10);
        mplew.writeInt(10);
        mplew.writeInt(2);
        mplew.writeInt(14);
        mplew.writeInt(0);
        mplew.writeInt(13);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(2);
        mplew.writeInt(1);
        mplew.writeInt(3);
        mplew.writeInt(2);
        mplew.writeInt(4);
        mplew.writeInt(3);
        mplew.writeInt(5);
        mplew.writeInt(4);
        mplew.writeInt(6);
        mplew.writeInt(5);
        mplew.writeInt(7);
        mplew.writeInt(6);
        mplew.writeInt(8);
        mplew.writeInt(7);
        mplew.writeInt(9);
        mplew.writeInt(8);
        mplew.writeInt(10);
        mplew.writeInt(9);
        mplew.writeInt(11);
        mplew.writeInt(10);
        mplew.writeInt(12);
        mplew.writeInt(11);
        mplew.writeInt(13);
        mplew.writeInt(12);
        mplew.writeInt(0);
        mplew.writeInt(6);
        mplew.writeInt(-2024195);
        mplew.writeInt(1);
        mplew.writeInt(-2024194);
        mplew.writeInt(-2024194);
        mplew.writeInt(1);
        mplew.writeInt(-2024195);
        mplew.writeInt(-2024187);
        mplew.writeInt(1);
        mplew.writeInt(-2024186);
        mplew.writeInt(-2024186);
        mplew.writeInt(1);
        mplew.writeInt(-2024187);
        mplew.writeInt(80003606);
        mplew.writeInt(1);
        mplew.writeInt(80003607);
        mplew.writeInt(80003607);
        mplew.writeInt(1);
        mplew.writeInt(80003606);
        mplew.writeInt(1);
        mplew.writeInt(-2024237);
        mplew.writeInt(200);
        mplew.writeInt(503417);
        mplew.writeMapleAsciiString("qState");
        mplew.write(1);
        mplew.write(0);
        mplew.write(49);
        this.getPlayer().send(mplew.getPacket());
        if (this.getPlayer().getParty() == null) {
            Party party = Party.createNewParty((boolean)false, (boolean)false, (String)(this.getPlayer().getName() + "的隊伍"), (World)this.getPlayer().getClient().getWorld());
            PartyMember pm = new PartyMember(this.getPlayer());
            party.setPartyLeaderID(pm.getCharID());
            party.getPartyMembers()[0] = pm;
            this.getPlayer().setParty(party);
            this.getPlayer().write(WvsContext.partyResult((PartyResult)PartyResult.createNewParty((Party)party)));
        }
    }

    public int pecketToolUI() {
        this.frame = new JFrame("[Tools] PhantomTMS_Packet Tools by Hertz.");
        this.frame.setDefaultCloseOperation(3);
        this.frame.setSize(500, 200);
        this.frame.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 5));
        JLabel handlerLabel = new JLabel("請輸入封包包頭:");
        this.handlerField = new JTextField();
        JLabel packetContentLabel = new JLabel("請輸入封包內容:");
        this.packetContentArea = new JTextArea();
        inputPanel.add(handlerLabel);
        inputPanel.add(this.handlerField);
        inputPanel.add(packetContentLabel);
        inputPanel.add(this.packetContentArea);
        JButton sendButton = new JButton("發送");
        sendButton.addActionListener(e -> this.sendPacket());
        this.frame.add((Component)inputPanel, "Center");
        this.frame.add((Component)sendButton, "South");
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(2);
        return 1;
    }

    public void guildDisband(int guildId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_GuildResult.getValue());
        mplew.writeInt(40);
        mplew.writeInt(guildId);
        this.getPlayer().send(mplew.getPacket());
    }

    private void sendPacket() {
        String handlerText = this.handlerField.getText();
        String packetContentText = this.packetContentArea.getText();
        try {
            int sendOpcode = Integer.parseInt(handlerText);
            String packetContent = packetContentText.replaceAll("\\s+", "");
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(sendOpcode);
            if (packetContent == null) {
                return;
            }
            mplew.writeHexString(packetContent);
            this.client.getPlayer().dropMessage(15, "[發送封包包頭]: " + sendOpcode + " / 封包內容:[" + Arrays.toString(mplew.getPacket()) + "]");
            this.client.announce(mplew.getPacket());
            JOptionPane.showMessageDialog(this.frame, "資料包發送成功.");
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.frame, "處理程序格式無效.");
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this.frame, "資料包內容格式無效.");
        }
    }

    public void setDelay(int delay) {
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setSpirtValue(int value) {
        for (MapleCharacter player : this.getPlayer().getMap().getAllChracater()) {
            Caning.setSpirtValue((int)value, (MapleClient)player.getClient());
        }
    }

    public void setSelMapLoad() {
        for (MapleCharacter player : this.getPlayer().getMap().getAllChracater()) {
            Caning.setSelMapLoad((MapleClient)player.getClient());
        }
    }

    public void setSelMapLoadNext() {
        for (MapleCharacter player : this.getPlayer().getMap().getAllChracater()) {
            Caning.setSelMapLoadNext((MapleClient)player.getClient());
        }
    }

    public void setSelMapLoadParty() {
        for (MapleCharacter player : this.getPlayer().getMap().getAllChracater()) {
            Caning.setSelMapLoadParty((MapleClient)player.getClient());
        }
    }

    public void showHint(String message, int heigth, int width) {
        this.getClient().announce(MaplePacketCreator.sendHint(message, heigth, width, null));
    }

    public MapleCharacter sendReward(int cid, int itemId, long amount, String desc) {
        MapleCharacter player = MapleCharacter.getCharacterById(cid);
        return this.sendAccRewardPeriod(player.getAccountID(), cid, itemId, amount, desc);
    }

    public MapleCharacter sendAccReward(int accountId, int itemId, long amount, String desc) {
        return this.sendAccRewardPeriod(accountId, 0, itemId, amount, desc);
    }

    public MapleCharacter sendAccRewardPeriod(int accountId, int day, int itemId, long amount, String desc) {
        return this.sendReward(accountId, 0, DateUtil.getNextDayTime(0), day <= 0 ? 0L : DateUtil.getNextDayTime(day) - 60000L, itemId, amount, desc);
    }

    public MapleMonsterInformationProvider getMonsterInfo() {
        return MapleMonsterInformationProvider.getInstance();
    }

    public MapleReactor getReactor(int id) {
        return new MapleReactor(MapleReactorFactory.getReactor(id), id);
    }

    public final MapleMonster getMonster(int mobId) {
        return MapleLifeFactory.getMonster(mobId);
    }

    public MapleMonster getEliteMonster(int id) {
        return MapleLifeFactory.getEliteMonster(id);
    }

    public MapleMonster getEliteMonster(int mobId, MapleMonsterStats stats) {
        return MapleLifeFactory.getEliteMonster(mobId, stats);
    }

    public MapleMonster getEliteMonster(int mobId, MapleMonsterStats stats, int eliteGrade) {
        return MapleLifeFactory.getEliteMonster(mobId, stats, eliteGrade);
    }

    public MapleMonster getEliteMonster(int mobId, MapleMonsterStats stats, int eliteGrade, int eliteType) {
        return MapleLifeFactory.getEliteMonster(mobId, stats, eliteGrade, eliteType);
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public List<Pair<Integer, Integer>> getAllHotTimeItems() {
        return SuperGMCommand.HotTime.HotTimeItems;
    }

    public List<RaffleItem> getRaffleMainReward(int type) {
        return RafflePool.getMainReward(type);
    }

    public MapleItemInformationProvider getItemInfo() {
        return MapleItemInformationProvider.getInstance();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public MapleCharacter sendReward(int accountId, int characterId, long start, long end, int itemId, long amount, String desc) {
        int type;
        if (itemId < 1000000) {
            if (itemId <= 2 || itemId >= 6) return null;
            type = itemId;
        } else if (this.getItemInfo().isCash(itemId)) {
            type = 2;
        } else {
            if (!this.getItemInfo().itemExists(itemId)) return null;
            type = 1;
        }
        MapleCharacter.addReward(accountId, characterId, start, end, type, amount, itemId, desc);
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                if (mch.getAccountID() != accountId && mch.getId() != characterId) continue;
                mch.updateReward();
                return mch;
            }
        }
        return null;
    }

    public void runScript(String scriptName) throws Exception {
        this.runScript(0, "expands/" + scriptName);
    }

    public void runScript(int npcId, String scriptName) throws Exception {
        String scriptString = ScriptManager.getScriptString(scriptName);
        if (!scriptString.isEmpty()) {
            ScriptEngine engine = (ScriptEngine)((Object)this.getPlayer().getScriptManager().getInvocableByType(this.getPlayer().getScriptManager().getLastActiveScriptType()));
            this.getPlayer().dropSpouseMessage(UserChatMessageType.青, "[runScript] " + scriptName + ".js");
            Bindings bindings = engine.createBindings();
            bindings.put("party", (Object)(this.getPlayer().getParty() == null ? null : new ScriptParty(this.getPlayer().getParty())));
            bindings.put("player", (Object)new ScriptPlayer(this.getPlayer()));
            bindings.put("map", (Object)new ScriptField(this.getPlayer().getMap()));
            bindings.put("sh", (Object)new ScriptHelper());
            bindings.put("npc", (Object)new ScriptNpc(this.getPlayer().getClient(), npcId, scriptName, ScriptType.Npc, null));
            CompiledScript cs = ((Compilable)((Object)engine)).compile(scriptString);
            cs.eval(bindings);
        }
        throw new NullPointerException("Intended NPE by forceful Plugin.script stop.");
    }

    public void showUnityPortal() {
        List<DimensionMirrorEvent> list = Arrays.stream(DimensionMirrorEvent.values()).filter(it -> it.getMapID() > 0).collect(Collectors.toList());
        this.getClient().announce(UIPacket.showDimensionMirror(list));
    }

    public void SayEldasMessage(String Notice) {
        this.getPlayer().send(EldasPacket.Eldas_200((String)Notice));
        this.getPlayer().removeItem(2636883, 1);
    }

    public void eventSay(String Notice) {
        this.getPlayer().send(MessengerPacket.npcEffectChat_BlackLock((String)Notice));
    }

    public void addEdraCount(int Count) {
        this.getPlayer().addEdraSoul(Count);
    }

    public void CreatGuildName() {
        this.getPlayer().send(GuildPacket.genericGuildMessage((byte)0));
    }

    public void playMusicBox(String music) {
        MaplePacketLittleEndianWriter ctx = new MaplePacketLittleEndianWriter();
        ctx.writeShort(-2);
        ctx.write(9);
        ctx.writeMapleAsciiString(music);
        ctx.writeInt(0);
        ctx.writeInt(0);
        ctx.writeInt(0);
        this.getPlayer().send(ctx.getPacket());
    }

    public void playMusicBox2(String music) {
        MaplePacketLittleEndianWriter ctx = new MaplePacketLittleEndianWriter();
        ctx.writeShort(-2);
        ctx.write(9);
        ctx.writeMapleAsciiString(music);
        ctx.writeInt(0);
        ctx.writeInt(0);
        ctx.writeInt(0);
        this.getPlayer().send(ctx.getPacket());
    }

    public void playMusicBox3(String music) {
        MaplePacketLittleEndianWriter ctx = new MaplePacketLittleEndianWriter();
        ctx.writeShort(-2);
        ctx.write(9);
        ctx.writeMapleAsciiString(music);
        ctx.writeInt(0);
        ctx.writeInt(0);
        ctx.writeInt(0);
        this.getPlayer().send(ctx.getPacket());
    }

    public void playMusicBox4(String music) {
        MaplePacketLittleEndianWriter ctx = new MaplePacketLittleEndianWriter();
        ctx.writeShort(-2);
        ctx.write(9);
        ctx.writeMapleAsciiString(music);
        ctx.writeInt(0);
        ctx.writeInt(0);
        ctx.writeInt(0);
        this.getPlayer().send(ctx.getPacket());
    }

    public void playMusicBox5(String music) {
        MaplePacketLittleEndianWriter ctx = new MaplePacketLittleEndianWriter();
        ctx.writeShort(-2);
        ctx.write(9);
        ctx.writeMapleAsciiString(music);
        ctx.writeInt(0);
        ctx.writeInt(0);
        ctx.writeInt(0);
        this.getPlayer().send(ctx.getPacket());
    }

    public void WeatherMessage(String fieldMessage, int type, int ms) {
        this.getPlayer().getMap().showWeatherEffectNotice(fieldMessage, type, ms);
    }

    public boolean isQuestStarted(int questId) {
        return this.getPlayer().getQuestStatus(questId) == 1;
    }

    public boolean isQuestFinished(int questId) {
        return this.getPlayer().getQuestStatus(questId) == 2;
    }

    public boolean isQuestCompleted(int questId) {
        return this.getPlayer().getQuestStatus(questId) == 2;
    }

    public int getQuest() {
        return this.quest;
    }

    public boolean isStart() {
        return this.start;
    }

    public void forceStartQuest() {
        this.forceStartQuest(false);
    }

    public void forceStartQuest(boolean isWorldShare) {
        this.forceStartQuest(null, isWorldShare);
    }

    public void forceStartQuest(String customData) {
        this.forceStartQuest(customData, false);
    }

    public void forceStartQuest(String customData, boolean isWorldShare) {
        MapleQuest.getInstance(this.quest).forceStart(this.getPlayer(), PlayerScriptInteraction.getNpc(), customData, isWorldShare);
    }

    public void forceCompleteQuest() {
        this.forceCompleteQuest(false);
    }

    public void forceCompleteQuest(int questId) {
        MapleQuest.getInstance(questId).forceComplete(this.getPlayer(), 0, false);
    }

    public void forceCompleteQuest(boolean isWorldShare) {
        MapleQuest.getInstance(this.quest).forceComplete(this.getPlayer(), 0, isWorldShare);
    }

    public void resetQuest() {
        MapleQuest.getInstance(this.quest).reset(this.getPlayer());
    }

    public String getQuestCustomData() {
        return this.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.quest)).getCustomData();
    }

    public void setQuestCustomData(String customData) {
        this.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.quest)).setCustomData(customData);
    }

    public void showCompleteQuestEffect() {
        this.getPlayer().getClient().announce(EffectPacket.showSpecialEffect(EffectOpcode.UserEffect_QuestComplete));
        this.getPlayer().getMap().broadcastMessage(this.getPlayer(), EffectPacket.showForeignEffect(this.getPlayer().getId(), EffectOpcode.UserEffect_QuestComplete), false);
    }

    public final void spawnNpcForPlayer(int npcId, int x, int y) {
        this.getPlayer().getMap().spawnNpcForPlayer(this.getClient(), npcId, new Point(x, y));
    }

    public int gainGachaponItem(int id, int quantity) {
        return this.gainGachaponItem(id, quantity, this.getPlayer().getMap().getStreetName() + " - " + this.getPlayer().getMap().getMapName());
    }

    public int gainGachaponItem(int id, int quantity, String msg) {
        byte rareness = GameConstants.gachaponRareItem(id);
        return this.gainGachaponItem(id, quantity, msg, rareness == 1 || rareness == 2 || rareness == 3);
    }

    public int gainGachaponItem(int id, int quantity, String msg, boolean smega) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            if (!ii.itemExists(id)) {
                return -1;
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(this.getClient(), id, quantity, "從 " + msg + " 中獲得時間: " + DateUtil.getNowTime());
            if (item == null) {
                return -1;
            }
            if (smega) {
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.gachaponMsg("恭喜" + this.getPlayer().getName() + "從" + msg + "獲得{" + ii.getName(item.getItemId()) + "}", item));
            }
            return item.getItemId();
        }
        catch (Exception e) {
            log.error("gainGachaponItem 錯誤", e);
            return -1;
        }
    }

    public int gainGachaponItem(int id, int quantity, String msg, int rareness) {
        return this.gainGachaponItem(id, quantity, msg, rareness, false, 0L);
    }

    public int gainGachaponItem(int id, int quantity, String msg, int rareness, long period) {
        return this.gainGachaponItem(id, quantity, msg, rareness, false, period);
    }

    public int gainGachaponItem(int id, int quantity, String msg, int rareness, boolean buy) {
        return this.gainGachaponItem(id, quantity, msg, rareness, buy, 0L);
    }

    public int gainGachaponItem(int id, int quantity, String msg, int rareness, boolean buy, long period) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            if (!ii.itemExists(id)) {
                return -1;
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(this.getClient(), id, quantity, "從 " + msg + " 中" + (buy ? "購買" : "獲得") + "時間: " + DateUtil.getNowTime(), period);
            if (item == null) {
                return -1;
            }
            if (rareness == 1 || rareness == 2 || rareness == 3) {
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.getGachaponMega(this.getPlayer().getName(), " : 從" + msg + "中" + (buy ? "購買" : "獲得") + "{" + ii.getName(item.getItemId()) + "}！大家一起恭喜他（她）吧！！！！", item, (byte)rareness, this.getClient().getChannel()));
            }
            return item.getItemId();
        }
        catch (Exception e) {
            log.error("gainGachaponItem 錯誤", e);
            return -1;
        }
    }

    @Generated
    public MapleCharacter getPlayer() {
        return this.player;
    }

    @Generated
    public MapleClient getClient() {
        return this.client;
    }
}

