/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MapleAntiMacro$MapleAntiMacroInfo
 *  Config.constants.MapConstants
 *  Server.world.WorldBroadcastService
 *  tools.CheckCodeImageCreator
 */
package Client;

import Client.MapleAntiMacro;
import Client.MapleCharacter;
import Config.constants.MapConstants;
import Config.constants.enums.UserChatMessageType;
import Net.server.Timer;
import Net.server.maps.MapleMap;
import Net.server.quest.MapleQuest;
import Packet.MaplePacketCreator;
import Server.world.WorldBroadcastService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import tools.CheckCodeImageCreator;
import tools.Randomizer;

public class MapleAntiMacro {
    private static final Map<String, MapleAntiMacroInfo> antiPlayers = new HashMap<String, MapleAntiMacroInfo>();
    private static final Map<String, Long> lastAntiTime = new HashMap<String, Long>();
    public static final byte SYSTEM_ANTI = 0;
    public static final byte ITEM_ANTI = 1;
    public static final byte GM_SKILL_ANTI = 2;
    public static final int CAN_ANTI = 0;
    public static final int NON_ATTACK = 1;
    public static final int ANTI_COOLING = 2;
    public static final int ANTI_NOW = 3;
    public static final int BOSS_MAP = 4;

    public static class MapleAntiMacroInfo {

        private final MapleCharacter source;
        private final int mode;
        private String code;
        private final long startTime;
        private ScheduledFuture<?> schedule;
        private int timesLeft = 2;

        MapleAntiMacroInfo(MapleCharacter from, int mode, String code, long time, ScheduledFuture<?> schedule) {
            source = from;
            this.mode = mode;
            this.code = code;
            startTime = time;
            this.schedule = schedule;
        }

        public MapleCharacter getSourcePlayer() {
            return source;
        }

        public int antiMode() {
            return mode;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setSchedule(ScheduledFuture<?> schedule) {
            cancelSchedule();
            this.schedule = schedule;
        }

        public void cancelSchedule() {
            if (schedule != null) {
                schedule.cancel(false);
            }
        }

        public int antiFailure() {
            return --timesLeft;
        }

        public int getTimesLeft() {
            return timesLeft - 1;
        }
    }


    public static int getCharacterState(MapleCharacter chr) {
        if (MapleAntiMacro.isAntiNow(chr.getName())) {
            return 3;
        }
        if (MapleAntiMacro.isCooling(chr.getName())) {
            return 2;
        }
        if (!chr.getCheatTracker().isAttacking()) {
            return 1;
        }
        if (MapConstants.isBossMap((int)chr.getMapId())) {
            return 4;
        }
        return 0;
    }

    public static void updateCooling(String name) {
        lastAntiTime.put(name, System.currentTimeMillis());
    }

    public static boolean isCooling(String name) {
        if (lastAntiTime.containsKey(name)) {
            if (System.currentTimeMillis() - lastAntiTime.get(name) < (long)(20 + Randomizer.nextInt(10)) * 60L * 1000L) {
                return true;
            }
            lastAntiTime.remove(name);
        }
        return false;
    }

    public static boolean isAntiNow(String name) {
        return antiPlayers.containsKey(name) && System.currentTimeMillis() - antiPlayers.get(name).getStartTime() < 300000L;
    }

    public static boolean startAnti(MapleCharacter chr, MapleCharacter victim, byte mode) {
        return MapleAntiMacro.startAnti(chr, victim, mode, false);
    }

    public static boolean startAnti(MapleCharacter chr, MapleCharacter victim, byte mode, boolean force) {
        int antiState = MapleAntiMacro.getCharacterState(victim);
        switch (antiState) {
            case 0: {
                break;
            }
            case 1: {
                if (force) break;
                if (chr != null) {
                    chr.dropMessage(1, "角色不在攻擊狀態");
                }
                return false;
            }
            case 2: 
            case 4: {
                if (force) break;
                if (chr != null) {
                    chr.dropMessage(1, "角色已經通過測謊");
                }
                return false;
            }
            case 3: {
                if (chr != null && !force) {
                    chr.dropMessage(1, "角色正在被測謊");
                }
                return false;
            }
            default: {
                System.out.println("測謊機狀態出現未知類型：" + antiState);
                return false;
            }
        }
        MapleAntiMacroInfo ami = new MapleAntiMacroInfo(chr, (int)mode, CheckCodeImageCreator.getRandCode((boolean)true), System.currentTimeMillis(), Timer.MapTimer.getInstance().schedule(() -> {
            if (antiPlayers.containsKey(victim.getName())) {
                MapleAntiMacro.antiFailure(victim);
            }
        }, 300000L));
        antiPlayers.put(victim.getName(), ami);
        if (chr != null) {
            chr.dropMessage(1, "已經對\"" + victim.getName() + "\"進行測謊");
        }
        return true;
    }

    public static void antiSuccess(MapleCharacter victim) {
        MapleAntiMacroInfo ami = null;
        if (antiPlayers.containsKey(victim.getName())) {
            MapleCharacter chr;
            ami = antiPlayers.get(victim.getName());
            if (ami.antiMode() == 1) {
                victim.gainMeso(5000L, true);
            }
            if ((chr = ami.getSourcePlayer()) != null) {
                chr.dropMessage(1, "玩家\"" + victim.getName() + "\"已經通過測謊");
            }
        }
        victim.setAntiMacroFailureTimes(0);
        victim.dropMessage(1, "您已通過測謊" + (ami != null && ami.antiMode() == 1 ? ", 獲得 5000 楓幣獎勵" : ""));
        MapleAntiMacro.stopAnti(victim.getName());
        MapleAntiMacro.updateCooling(victim.getName());
    }

    public static void antiFailure(MapleCharacter victim) {
        MapleAntiMacroInfo ami = null;
        if (antiPlayers.containsKey(victim.getName())) {
            ami = antiPlayers.get(victim.getName());
            MapleCharacter mapleCharacter = ami.getSourcePlayer();
        }
        MapleAntiMacro.stopAnti(victim.getName());
        if (victim.addAntiMacroFailureTimes() < 5) {
            victim.changeMap(victim.getMap().getReturnMap());
            victim.dropMessage(1, "已通過測謊。");
            victim.dropMessage(5, "連續五次失敗,就會被關進監獄。");
        } else {
            victim.setAntiMacroFailureTimes(0);
            MapleMap map = victim.getClient().getChannelServer().getMapFactory().getMap(993073000);
            victim.getQuestNAdd(MapleQuest.getInstance(123456)).setCustomData(String.valueOf(1800));
            victim.changeMap(map, map.getPortal(0));
            WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM消息] 玩家: " + victim.getName() + " (等級 " + victim.getLevel() + ") 未通過測謊機檢器，系統將其監禁30分鐘！"));
        }
    }

    public static void stopAnti(String name) {
        if (antiPlayers.containsKey(name)) {
            antiPlayers.get(name).cancelSchedule();
        }
        antiPlayers.remove(name);
    }

    public static void antiReduce(MapleCharacter victim) {
        if (antiPlayers.containsKey(victim.getName())) {
            MapleAntiMacroInfo ami = antiPlayers.get(victim.getName());
            if (ami.antiFailure() > 0) {
                MapleAntiMacro.refreshCode(victim);
            } else {
                MapleAntiMacro.antiFailure(victim);
            }
        }
    }

    public static boolean verifyCode(String name, String code) {
        if (!antiPlayers.containsKey(name)) {
            return false;
        }
        return antiPlayers.get(name).getCode().equalsIgnoreCase(code);
    }

    public static void refreshCode(MapleCharacter victim) {
        if (antiPlayers.containsKey(victim.getName())) {
            MapleAntiMacroInfo ami = antiPlayers.get(victim.getName());
            ami.setCode(CheckCodeImageCreator.getRandCode((boolean)true));
            ami.setSchedule(Timer.MapTimer.getInstance().schedule(() -> {
                if (antiPlayers.containsKey(victim.getName())) {
                    MapleAntiMacro.antiFailure(victim);
                }
            }, 300000L));
        }
    }
}

