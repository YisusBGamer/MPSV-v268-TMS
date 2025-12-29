/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.script.binding.ScriptParty
 *  Plugin.script.binding.ScriptPortal
 *  Plugin.script.binding.ScriptReactor
 *  SwordieX.client.character.Char
 *  lombok.Generated
 *  tools.AesUtil
 *  tools.ComputerUniqueIdentificationUtil
 */
package Plugin.script;

import Client.MapleCharacter;
import Client.inventory.Item;
import Config.configs.ServerConfig;
import Config.constants.enums.NpcMessageType;
import Config.constants.enums.ScriptParam;
import Config.constants.enums.ScriptType;
import Config.constants.enums.UserChatMessageType;
import Net.server.MapleItemInformationProvider;
import Net.server.MaplePortal;
import Net.server.ScriptedItem;
import Net.server.life.MapleLifeFactory;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleReactor;
import Net.server.maps.MapleReactorFactory;
import Net.server.quest.MapleQuest;
import Packet.UIPacket;
import Plugin.script.FieldTransferInfo;
import Plugin.script.NpcScriptInfo;
import Plugin.script.ScriptInfo;
import Plugin.script.ScriptMemory;
import Plugin.script.binding.ScriptField;
import Plugin.script.binding.ScriptHelper;
import Plugin.script.binding.ScriptNpc;
import Plugin.script.binding.ScriptParty;
import Plugin.script.binding.ScriptPlayer;
import Plugin.script.binding.ScriptPortal;
import Plugin.script.binding.ScriptReactor;
import SwordieX.client.character.Char;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import connection.packet.ScriptMan;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import lombok.Generated;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.io.IOAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.AesUtil;
import tools.ComputerUniqueIdentificationUtil;
import tools.StringUtil;

public class ScriptManager {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptManager.class);
    private static final String SCRIPT_PATH_PREFIX = ServerConfig.WORLD_SCRIPTSPATH + File.separator;
    private static final String ENCRYPTED_SCRIPT = ".jse";
    private static final String CUSTOM_SCRIPT = ".jsc";
    private static final String DEFAULT_SCRIPT = ".js";
    public static final String INTENDED_NPE_MSG = "Intended NPE by forceful Plugin.script stop.";
    private static final Lock fileReadLock = new ReentrantLock();
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final boolean isField;
    private NpcScriptInfo npcScriptInfo;
    private final Map<ScriptType, ScriptInfo> scripts = new HashMap<ScriptType, ScriptInfo>();
    private ScriptType lastActiveScriptType;
    private final MapleCharacter chr;
    private final MapleMap field;
    private final ScriptMemory memory = new ScriptMemory();
    private final FieldTransferInfo fieldTransferInfo = new FieldTransferInfo();
    private final Map<String, String> bossUIMap = new HashMap<String, String>();

    public ScriptManager(MapleCharacter chr, MapleMap field) {
        this.chr = chr;
        this.field = field;
        this.npcScriptInfo = new NpcScriptInfo();
        this.isField = chr == null;
        this.lastActiveScriptType = ScriptType.None;
        this.initBossUIMap();
    }

    public ScriptManager(Char chr) {
        this(chr.getCharacter());
    }

    public ScriptManager(MapleCharacter chr) {
        this(chr, chr.getMap());
    }

    public ScriptManager(MapleMap field) {
        this(null, field);
    }

    public static String getScriptString(String path) throws Exception {
        String script = "";
        File file = new File(ScriptManager.getScriptPath(path));
        if (file.exists()) {
            script = Files.readString((Path)file.toPath());
            if (file.getName().endsWith(ENCRYPTED_SCRIPT)) {
                script = AesUtil.decryptScriptByMachineHash((String)ComputerUniqueIdentificationUtil.getHashCache(), (String)"MapleStory", (String)script);
            }
            return script;
        }
        return null;
    }

    public static String getScriptPath(String scriptName) {
        String path = ScriptManager.getScriptPath(scriptName, CUSTOM_SCRIPT);
        if (new File(path).exists()) {
            log.debug("使用自訂腳本:{}", (Object)path);
            return path;
        }
        path = ScriptManager.getScriptPath(scriptName, DEFAULT_SCRIPT);
        if (new File(path).exists()) {
            log.debug("使用預設腳本:{}", (Object)path);
            return path;
        }
        path = ScriptManager.getScriptPath(scriptName, ENCRYPTED_SCRIPT);
        if (new File(path).exists()) {
            log.debug("使用加密腳本:{}", (Object)path);
            return path;
        }
        log.debug("未找到腳本");
        return "";
    }

    private static String getScriptPath(String scriptName, String extension) {
        return SCRIPT_PATH_PREFIX + scriptName + extension;
    }

    public void initBossUIMap() {
        this.bossUIMap.put("0", "balog");
        this.bossUIMap.put("1", "zakum");
        this.bossUIMap.put("2", "hontale");
        this.bossUIMap.put("3", "hillah");
        this.bossUIMap.put("4", "pierre");
        this.bossUIMap.put("5", "banban");
        this.bossUIMap.put("6", "bloody");
        this.bossUIMap.put("7", "bellum");
        this.bossUIMap.put("8", "vanleon");
        this.bossUIMap.put("9", "akayrum");
        this.bossUIMap.put("10", "magnus");
        this.bossUIMap.put("11", "pinkbeen");
        this.bossUIMap.put("12", "shinas");
        this.bossUIMap.put("13", "suu");
        this.bossUIMap.put("14", "ursus");
        this.bossUIMap.put("15", "demian");
        this.bossUIMap.put("16", "beidler");
        this.bossUIMap.put("17", "ranmaru");
        this.bossUIMap.put("18", "princessno");
        this.bossUIMap.put("19", "lucid");
        this.bossUIMap.put("21", "caoong");
        this.bossUIMap.put("22", "papulatus");
        this.bossUIMap.put("23", "will");
        this.bossUIMap.put("24", "jinhillah");
        this.bossUIMap.put("25", "blackmage");
        this.bossUIMap.put("26", "dusk");
        this.bossUIMap.put("27", "dunkel");
        this.bossUIMap.put("28", "seren");
        this.bossUIMap.put("29", "slime");
        this.bossUIMap.put("30", "kalos");
        this.bossUIMap.put("31", "karing");
    }

    private Bindings getBindingsByType(ScriptType scriptType) {
        ScriptInfo si = this.getScriptInfoByType(scriptType);
        return si == null ? null : si.getBindings();
    }

    public ScriptInfo getScriptInfoByType(ScriptType scriptType) {
        return this.scripts.getOrDefault((Object)scriptType, null);
    }

    public Invocable getInvocableByType(ScriptType scriptType) {
        return this.getScriptInfoByType(scriptType).getInvocable();
    }

    public int getParentIDByScriptType(ScriptType scriptType) {
        return this.getScriptInfoByType(scriptType) != null ? this.getScriptInfoByType(scriptType).getParentID() : 2007;
    }

    public String startScript(int parentID, String scriptName, ScriptType scriptType) {
        return this.startScript(parentID, 0, scriptName, scriptType, null);
    }

    public String startNpcScript(int parentID, int objID, String scriptName) {
        String wzScriptName;
        int npcID = 0;
        if (parentID >= 0) {
            npcID = parentID;
        }
        if (scriptName != null && StringUtil.isNaturalNumber((String)scriptName)) {
            Object object = scriptName = npcID > 0 ? "npc_" + npcID + "_" + (String)scriptName : "expand_" + (String)scriptName;
        }
        if ((wzScriptName = MapleLifeFactory.getNpcScriptName(npcID)) == null && (scriptName == null || ((String)scriptName).isEmpty())) {
            scriptName = "npc_" + npcID;
        }
        return this.startScript(npcID, objID, wzScriptName, ScriptType.Npc, scriptName);
    }

    public String startBossUIScript(int parentID, String bossId, String difficulty) {
        int npcID = 0;
        if (parentID >= 0) {
            npcID = parentID;
        }
        String bossName = this.bossUIMap.get(bossId);
        return this.startScript(npcID, 0, bossName, ScriptType.BossUI, difficulty);
    }

    public String startItemScript(Item item, int parentID, String scriptName) {
        if (item == null) {
            return "";
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ScriptedItem info = ii.getScriptedItemInfo(item.getItemId());
        int npcID = 0;
        if (parentID >= 0) {
            npcID = parentID;
        } else if (info != null) {
            npcID = info.getNpc();
        }
        if (scriptName == null || ((String)scriptName).isEmpty()) {
            Object object = scriptName = info == null ? null : info.getScript();
            if (scriptName == null || ((String)scriptName).isEmpty()) {
                switch (item.getItemId() / 1000000) {
                    case 1: {
                        scriptName = "equip_";
                        break;
                    }
                    case 2: {
                        scriptName = "consume_";
                        break;
                    }
                    case 3: {
                        scriptName = "install_";
                        break;
                    }
                    case 4: {
                        scriptName = "etc_";
                        break;
                    }
                    case 5: {
                        scriptName = "cash_";
                        break;
                    }
                    default: {
                        scriptName = "item_";
                    }
                }
                scriptName = (String)scriptName + item.getItemId();
            }
        }
        return this.startScript(npcID, 0, (String)scriptName, ScriptType.Item, item);
    }

    public String startQuestSScript(int parentID, int questId) {
        return this.startQuestScript(parentID, questId, true);
    }

    public String startQuestEScript(int parentID, int questId) {
        return this.startQuestScript(parentID, questId, false);
    }

    private String startQuestScript(int parentID, int questId, boolean isStartScript) {
        MapleQuest quest = MapleQuest.getInstance(questId);
        Object scriptName = "";
        if (quest != null) {
            Object object = scriptName = isStartScript ? quest.getStartScript() : quest.getEndScript();
        }
        if (((String)scriptName).isEmpty()) {
            scriptName = "q" + questId + (isStartScript ? "s" : "e");
        }
        return this.startScript(parentID, 0, (String)scriptName, isStartScript ? ScriptType.QuestStart : ScriptType.QuestEnd, questId);
    }

    public String startPortalScript(MaplePortal portal) {
        return this.startScript(0, 0, portal.getScriptName(), ScriptType.Portal, portal);
    }

    public String startOnUserScript(String scriptName) {
        return this.startScript(0, 0, scriptName, ScriptType.onUserEnter, scriptName);
    }

    public String startOnFirstUserScript(String scriptName) {
        if (this.chr.getEventInstance() != null) {
            if (!this.chr.getEventInstance().getOnFirstUserMapIds().contains(this.chr.getMapId())) {
                this.chr.getEventInstance().getOnFirstUserMapIds().add(this.chr.getMapId());
                return this.startScript(0, 0, scriptName, ScriptType.onFirstUserEnter, scriptName);
            }
            return "";
        }
        return this.startScript(0, 0, scriptName, ScriptType.onFirstUserEnter, scriptName);
    }

    public String startRandomPortalScript(int parentID, int objID, String scriptName) {
        return this.startScript(parentID, objID, scriptName, ScriptType.Npc, null);
    }

    public String startReactorScript(MapleReactor reactor) {
        int id = reactor.getReactorId();
        Object scriptName = MapleReactorFactory.getAction(id);
        if (scriptName == null || ((String)scriptName).isEmpty()) {
            scriptName = "reactor_" + id;
        }
        return this.startScript(reactor.getReactorId(), reactor.getObjectId(), (String)scriptName, ScriptType.Reactor, reactor);
    }

    public String startCommandScript(String[] line, int parentID, String scriptName) {
        return this.startScript(parentID, 0, scriptName, ScriptType.Command, line);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String startScript(int parentID, int objID, String scriptName, ScriptType scriptType, Object obj) {
        if (scriptName == null || scriptName.isEmpty()) {
            return "";
        }
        if (scriptType == ScriptType.None) {
            log.error(String.format("Did not allow Plugin.script %s to go through (type %s)  |  Active Script Type: %s", new Object[]{scriptName, scriptType, this.getLastActiveScriptType()}));
            return "";
        }
        if (this.isActive(scriptType) && scriptType != ScriptType.Map && scriptType != ScriptType.onFirstUserEnter) {
            return "您當前已經開啟對話. 如果不是請輸入 @ea 命令進行解卡。";
        }
        this.setLastActiveScriptType(scriptType);
        GraalJSScriptEngine engine = GraalJSScriptEngine.create(Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build(), Context.newBuilder("js").allowHostAccess(HostAccess.ALL).allowHostClassLookup(s -> true).allowHostClassLoading(true).allowAllAccess(true).allowNativeAccess(true).allowCreateThread(true).allowCreateProcess(true).allowExperimentalOptions(true).allowValueSharing(true).allowIO(IOAccess.ALL).option("js.nashorn-compat", "true").option("js.ecmascript-version", "2022").option("js.syntax-extensions", "true").option("js.strict", "false"));
        engine.getContext().setAttribute("javax.script.filename", "script.mjs", 100);
        this.getNpcScriptInfo().setParam(0);
        Bindings bindings = engine.createBindings();
        bindings.put("party", (Object)(this.getChr().getParty() == null ? null : new ScriptParty(this.getChr().getParty())));
        bindings.put("player", (Object)new ScriptPlayer(this.getChr()));
        bindings.put("map", (Object)new ScriptField(this.getChr().getMap()));
        bindings.put("sh", (Object)new ScriptHelper());
        switch (String.valueOf((Object)scriptType)) {
            case "Portal": {
                bindings.put("portal", (Object)new ScriptPortal(this.getChr().getClient(), (MaplePortal)obj));
                break;
            }
            case "Reactor": {
                this.getNpcScriptInfo().setObjectID(((MapleReactor)obj).getObjectId());
                bindings.put("reactor", (Object)new ScriptReactor(this.getChr().getClient(), (MapleReactor)obj));
                break;
            }
            case "onFirstUserEnter": {
                this.getNpcScriptInfo().setTemplateID(parentID);
                bindings.put("npc", (Object)new ScriptNpc(this.getChr().getClient(), parentID, scriptName, ScriptType.Npc, obj));
                break;
            }
            case "BossUI": {
                bindings.put("difficulty", (Object)Integer.parseInt((String)obj));
                this.getNpcScriptInfo().setTemplateID(parentID);
                bindings.put("npc", (Object)new ScriptNpc(this.getChr().getClient(), parentID, scriptName, ScriptType.Npc, obj));
                break;
            }
            case "Item": 
            case "Npc": 
            case "QuestStart": 
            case "QuestEnd": 
            case "Command": 
            case "onUserEnter": {
                this.getNpcScriptInfo().setTemplateID(parentID);
                bindings.put("npc", (Object)new ScriptNpc(this.getChr().getClient(), parentID, scriptName, scriptType, obj));
            }
        }
        String scriptPath = scriptType == ScriptType.Npc && obj instanceof String && !((String)obj).isEmpty() ? String.format("expands" + File.separator + "%s", obj) : String.format("%s" + File.separator + "%s", scriptType.getDir(), scriptName);
        this.getNpcScriptInfo().setObjectID(parentID);
        String result = scriptPath;
        UserChatMessageType messageColor = UserChatMessageType.青;
        try {
            fileReadLock.lock();
            String scriptString = ScriptManager.getScriptString(scriptPath);
            if (scriptString == null) {
                throw new FileNotFoundException();
            }
            ScriptInfo scriptInfo = new ScriptInfo(scriptType, bindings, parentID, scriptPath);
            scriptInfo.setObjectID(objID);
            this.getScripts().put(scriptType, scriptInfo);
            CompletableFuture.runAsync(() -> this.startScript(engine, scriptString, scriptPath, scriptType));
        }
        catch (IOException ex) {
            messageColor = UserChatMessageType.粉;
            result = "";
        }
        catch (Exception e) {
            String firstLine = e.getMessage().split("\n")[0];
            if (!firstLine.contains(INTENDED_NPE_MSG)) {
                if (this.chr.isGm()) {
                    this.chr.dropSpouseMessage(UserChatMessageType.粉, "[" + String.valueOf((Object)scriptType) + "] " + scriptPath + " " + firstLine);
                }
                e.printStackTrace();
            }
        }
        finally {
            fileReadLock.unlock();
        }
        if (!this.isField() && this.chr.isGm() && !this.chr.getScriptManagerDebug().contains(scriptType.getDir())) {
            switch (scriptType) {
                case Npc: {
                    this.chr.dropSpouseMessage(messageColor, "[Npc] npcs/" + scriptName + DEFAULT_SCRIPT);
                    break;
                }
                case QuestEnd: 
                case QuestStart: {
                    this.chr.dropSpouseMessage(messageColor, "[Quest] quests/" + scriptName + DEFAULT_SCRIPT);
                    break;
                }
                case Item: {
                    this.chr.dropSpouseMessage(messageColor, "[Item] items/" + scriptName + DEFAULT_SCRIPT);
                    break;
                }
                case onFirstUserEnter: {
                    this.chr.dropSpouseMessage(messageColor, "[onFirstUserEnter] maps/onFirstUserEnter/" + scriptName + DEFAULT_SCRIPT);
                    break;
                }
                case onUserEnter: {
                    this.chr.dropSpouseMessage(messageColor, "[onUserEnter] maps/onUserEnter/" + scriptName + DEFAULT_SCRIPT);
                    break;
                }
                case Command: {
                    this.chr.dropSpouseMessage(messageColor, "[Command] commands/" + scriptName + DEFAULT_SCRIPT);
                    break;
                }
                case Reactor: {
                    this.chr.dropSpouseMessage(messageColor, "[Reactor] reactors/" + scriptName + DEFAULT_SCRIPT);
                    break;
                }
                case Portal: {
                    this.chr.dropSpouseMessage(messageColor, "[Portal] portals/" + scriptName + DEFAULT_SCRIPT);
                }
            }
            System.err.println("[" + String.valueOf((Object)scriptType) + "] " + scriptPath + DEFAULT_SCRIPT);
        }
        return result;
    }

    public String processSource(String data) {
        return data.replaceAll("(require\\([\"'])([^\"']+)(?<!\\.js)(?<!\\.jse)(?<!\\.jsc)([\"']\\))", "$1$2.js$3").replaceAll("(require\\([\"'])([^\"']+[.jse|.jsc|.js])([\"']\\))", "$1$2$3").replaceAll("import +\\{(.+)} +from +(['\"][^'\"]+)(?<!\\.js)(?<!\\.jse)(?<!\\.jsc)(['\"])$", "const {$1} = require($2.js$3)").replaceAll("import +\\{(.+)} +from +(['\"][^'\"]+[.jse|.jsc|.js])(['\"])", "const {$1} = require($2$3)").replaceAll("import +\\* +as +(\\w+) +from +(['\"][^'\"]+)(?<!\\.js)(?<!\\.jse)(?<!\\.jsc)(['\"])", "const $1 = require($2.js$3)").replaceAll("import +\\* +as +(\\w+) +from +(['\"][^'\"]+[.jse|.jsc|.js])(['\"])", "const $1 = require($2$3)").replaceAll("import +(\\w+) +from +(['\"][^'\"]+)(?<!\\.js)(?<!\\.jse)(?<!\\.jsc)(['\"])", "const $1 = require($2.js$3)").replaceAll("import +(\\w+) +from +(['\"][^'\"]+[.jse|.jsc|.js])(['\"])", "const $1 = require($2$3)");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startScript(ScriptEngine engine, String data, String name, ScriptType scriptType) {
        ScriptInfo si = this.getScriptInfoByType(scriptType);
        si.setActive(true);
        if (this.chr != null) {
            this.chr.setConversation(1);
            if (this.chr.getClient() != null) {
                this.chr.getClient().setClickedNPC();
            }
        }
        Bindings bindings = this.getBindingsByType(scriptType);
        si.setBindings(bindings);
        si.setInvocable((Invocable)((Object)engine));
        try {
            CompiledScript cs = ((Compilable)((Object)engine)).compile(data);
            cs.eval(bindings);
            si.setInvocable((Invocable)((Object)engine));
        }
        catch (Exception e) {
            String firstLine = e.getMessage().split("\n")[0];
            if (!firstLine.contains(INTENDED_NPE_MSG)) {
                if (this.chr.isGm()) {
                    this.chr.dropSpouseMessage(UserChatMessageType.粉, "[" + String.valueOf((Object)scriptType) + "] " + name + " " + firstLine);
                }
                e.printStackTrace();
                this.lockInGameUI(false);
            }
        }
        finally {
            FieldTransferInfo fti;
            if (si.isActive() && name.equals(si.getScriptName()) && this.chr != null) {
                this.stop(scriptType);
            }
            if (!(fti = this.getFieldTransferInfo()).isInit()) {
                if (fti.isField()) {
                    fti.warp(this.field);
                } else {
                    fti.warp(this.chr);
                }
            }
        }
    }

    public void stop(ScriptType scriptType) {
        ScriptInfo si;
        NpcScriptInfo nsi = this.getNpcScriptInfo();
        nsi.removeParam(ScriptParam.PlayerAsSpeaker);
        boolean isNotCancellable = nsi.hasParam(ScriptParam.NoEsc);
        nsi.setTemplateID(0);
        if (isNotCancellable) {
            nsi.addParam(ScriptParam.NoEsc);
        }
        if (this.getLastActiveScriptType() == scriptType) {
            this.setLastActiveScriptType(ScriptType.None);
        }
        if ((si = this.getScriptInfoByType(scriptType)) != null) {
            si.reset();
        }
        this.npcScriptInfo = new NpcScriptInfo();
        this.getMemory().clear();
        if (this.chr != null) {
            this.chr.dispose();
            this.chr.setConversation(0);
            if (this.chr.getClient() != null) {
                this.chr.getClient().removeClickedNPC();
            }
        }
    }

    public void handleAction(ScriptType scriptType, NpcMessageType lastType, int response, long answer, String text) {
        block0 : switch (response) {
            case -1: 
            case 5: {
                this.stop(scriptType);
                break;
            }
            default: {
                ScriptMemory sm = this.getMemory();
                if (sm.get().isPrevPossible() && response == 0) {
                    NpcScriptInfo prev = sm.decrementAndGet();
                    this.chr.write(ScriptMan.scriptMessage(prev, prev.getMessageType()));
                    break;
                }
                if (sm.hasNext()) {
                    NpcScriptInfo next = sm.incrementAndGet();
                    this.chr.write(ScriptMan.scriptMessage(next, next.getMessageType()));
                    break;
                }
                ScriptInfo si = this.getScriptInfoByType(scriptType);
                if (!this.isActive(scriptType)) break;
                switch (lastType.getResponseType()) {
                    case Response: {
                        si.addResponse((long) response);
                        break block0;
                    }
                    case Answer: {
                        si.addResponse(answer);
                        break block0;
                    }
                    case Text: {
                        si.addResponse(text);
                    }
                }
            }
        }
    }

    public boolean isActive(ScriptType scriptType) {
        return this.getScriptInfoByType(scriptType) != null && this.getScriptInfoByType(scriptType).isActive();
    }

    public NpcScriptInfo getNpcScriptInfo() {
        return this.npcScriptInfo;
    }

    public Map<ScriptType, ScriptInfo> getScripts() {
        return this.scripts;
    }

    public int getParentID() {
        int res = 0;
        for (ScriptType type : ScriptType.values()) {
            if (this.getScriptInfoByType(type) == null) continue;
            res = this.getScriptInfoByType(type).getParentID();
        }
        return res;
    }

    public boolean isField() {
        return this.isField;
    }

    public MapleMap getField() {
        return this.field;
    }

    public ScriptType getLastActiveScriptType() {
        return this.lastActiveScriptType;
    }

    public void setLastActiveScriptType(ScriptType lastActiveScriptType) {
        this.lastActiveScriptType = lastActiveScriptType;
    }

    public FieldTransferInfo getFieldTransferInfo() {
        return this.fieldTransferInfo;
    }

    public void lockInGameUI(boolean lock) {
        this.lockInGameUI(lock, true);
    }

    public void lockInGameUI(boolean lock, boolean blackFrame) {
        if (this.chr != null) {
            this.chr.send(UIPacket.SetInGameDirectionMode(lock, blackFrame, false, !lock));
        }
    }

    public void dispose() {
        this.dispose(false);
    }

    public void dispose(boolean stop) {
        if (this.chr != null) {
            this.chr.getClient().removeClickedNPC();
        }
        this.npcScriptInfo = new NpcScriptInfo();
        this.getMemory().clear();
        ScriptType st = this.getLastActiveScriptType();
        this.stop(st);
        this.dispose(st);
        if (stop) {
            throw new NullPointerException(INTENDED_NPE_MSG);
        }
    }

    public void dispose(ScriptType scriptType) {
        this.getMemory().clear();
        this.stop(scriptType);
    }

    @Generated
    public void setNpcScriptInfo(NpcScriptInfo npcScriptInfo) {
        this.npcScriptInfo = npcScriptInfo;
    }

    @Generated
    public MapleCharacter getChr() {
        return this.chr;
    }

    @Generated
    public ScriptMemory getMemory() {
        return this.memory;
    }
}

