/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.script.EventManager
 *  Plugin.script.EventManipulator
 *  Server.world.WorldBroadcastService
 *  lombok.Generated
 */
package Plugin.script.binding;

import Net.server.Timer;
import Net.server.maps.MapleMap;
import Packet.UIPacket;
import Plugin.script.EventManager;
import Plugin.script.EventManipulator;
import Plugin.script.binding.ScriptField;
import Plugin.script.binding.ScriptPlayer;
import Server.channel.ChannelServer;
import Server.world.WorldBroadcastService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import javax.script.ScriptEngine;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptEvent {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptEvent.class);
    private static final AtomicInteger runningInstanceMapId = new AtomicInteger(0);
    private final String scriptName;
    private final byte channel;
    private final Map<String, Object> variables;
    private final Map<String, ScheduledFuture<?>> timers;
    private final Map<Integer, ScriptField> fields;
    private final ScriptEngine globalScope;
    private final EventManipulator hooks;
    private boolean practice = false;
    private boolean practiceMode = false;
    private static final Timer timerInstance = new Timer();
    private final List<Integer> onFirstUserMapIds = new ArrayList<Integer>();

    public ScriptEvent(String scriptName, byte channel, EventManipulator hooks, ScriptEngine globalScope) {
        this.globalScope = globalScope;
        this.scriptName = scriptName;
        this.channel = channel;
        this.hooks = hooks;
        this.variables = new ConcurrentHashMap<String, Object>();
        this.timers = new ConcurrentHashMap();
        this.fields = new ConcurrentHashMap<Integer, ScriptField>();
    }

    protected EventManipulator getScriptInterface() {
        return this.hooks;
    }

    public Map<String, Object> getVariables() {
        return this.variables;
    }

    public void setVariable(String key, Object value) {
        this.variables.put(key, value);
    }

    public Object getVariable(String key) {
        return this.variables.get(key);
    }

    public void clearVariables() {
        this.variables.clear();
    }

    public ScriptField makeMap(int mapId) {
        int assignedid = EventManager.getNewInstanceMapId();
        MapleMap m = ChannelServer.getInstance(this.channel).getMapFactory().CreateInstanceMap(mapId, true, true, true, assignedid);
        ScriptField map = new ScriptField(m);
        this.fields.put(mapId, map);
        return map;
    }

    public void initMap(int[] mapIds) {
        for (int mapId : mapIds) {
            this.makeMap(mapId);
        }
    }

    public ScriptField getMap(int mapId) {
        return this.fields.get(mapId);
    }

    public void destroyMap(ScriptField map) {
        map.endFieldEvent();
        ChannelServer.getInstance(this.channel).getMapFactory().removeInstanceMap(map.getInstanceId());
        this.fields.remove(map.getId());
    }

    public void destroyMaps() {
        for (ScriptField map : this.fields.values()) {
            map.endFieldEvent();
            ChannelServer.getInstance(this.channel).getMapFactory().removeInstanceMap(map.getInstanceId());
        }
        this.fields.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startTimer(String key, int millisDelay) {
        Map<String, ScheduledFuture<?>> map = this.timers;
        synchronized (map) {
            this.stopTimer(key);
            this.timers.put(key, ScriptEvent.getTimerInstance().schedule(() -> {
                synchronized (map) {
                    this.timers.remove(key);
                }
                try {
                    this.hooks.timerExpired(key);
                }
                catch (Exception e) {
                    log.error("error: startTimer. {}", (Object)e.getMessage());
                }
            }, millisDelay));
        }
    }

    public void stopTimer(String key) {
        ScheduledFuture<?> future = this.timers.remove(key);
        if (future != null) {
            future.cancel(true);
        }
    }

    public void stopTimers() {
        for (ScheduledFuture<?> future : this.timers.values()) {
            future.cancel(true);
        }
        this.timers.clear();
    }

    public int getChannel() {
        return this.channel;
    }

    public void destroyEvent() {
        this.hooks.deinit();
        this.destroyMaps();
        this.stopTimers();
    }

    public void runScript(ScriptPlayer player, String scriptName, int npcId) {
        player.getPlayer().getScriptManager().startNpcScript(npcId, 0, scriptName);
    }

    public void broadcastWeatherEffectNotice(String s, int n, int n2) {
        WorldBroadcastService.getInstance().broadcastMessage(UIPacket.showWeatherEffectNotice(s, n, n2, true));
    }

    public String getName() {
        return this.scriptName;
    }

    @Generated
    public String getScriptName() {
        return this.scriptName;
    }

    @Generated
    public Map<Integer, ScriptField> getFields() {
        return this.fields;
    }

    @Generated
    public EventManipulator getHooks() {
        return this.hooks;
    }

    @Generated
    public void setPractice(boolean practice) {
        this.practice = practice;
    }

    @Generated
    public boolean isPractice() {
        return this.practice;
    }

    @Generated
    public boolean isPracticeMode() {
        return this.practiceMode;
    }

    @Generated
    public void setPracticeMode(boolean practiceMode) {
        this.practiceMode = practiceMode;
    }

    @Generated
    public static Timer getTimerInstance() {
        return timerInstance;
    }

    @Generated
    public List<Integer> getOnFirstUserMapIds() {
        return this.onFirstUserMapIds;
    }
}

