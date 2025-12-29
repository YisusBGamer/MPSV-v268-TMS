/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.world.WorldBroadcastService
 *  lombok.Generated
 */
package Plugin.script.binding;

import Client.inventory.Equip;
import Net.server.MapleItemInformationProvider;
import Net.server.ShutdownServer;
import Packet.MaplePacketCreator;
import Plugin.script.binding.ScriptBase;
import Server.channel.ChannelServer;
import Server.world.WorldBroadcastService;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;

public class ScriptHelper
extends ScriptBase {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptHelper.class);

    public Point newPoint(int x, int y) {
        return new Point(x, y);
    }

    public String getItemName(int itemId) {
        return MapleItemInformationProvider.getInstance().getName(itemId);
    }

    public boolean itemExists(int itemId) {
        return MapleItemInformationProvider.getInstance().itemExists(itemId);
    }

    public Equip itemEquip(int equipId) {
        return MapleItemInformationProvider.getInstance().getEquipById(equipId);
    }

    public String formatDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public String formatDate(long timestamp, String format) {
        Date date = new Date(timestamp * 1000L);
        return new SimpleDateFormat(format).format(date);
    }

    public String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public byte[] getBytes(String str) {
        return str.getBytes();
    }

    public void shutdown(int time) {
        ShutdownServer server = ShutdownServer.getInstance();
        server.setTime(time * 1000);
        server.run();
    }

    public void worldBroadcastNotice(String notice) {
        WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(0, notice));
    }

    public void worldBroadcastMessage(String notice) {
        WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverMessage(notice));
    }

    public void channelBroadcastItemMessage(int channelID, String message, int itemId, int time) {
        if (ChannelServer.getInstance(channelID) != null) {
            ChannelServer.getInstance(channelID).startMapEffect(message, itemId, time);
        }
    }

    public void channelBroadcastNotice(int channelID, String notice) {
        log.error(ChannelServer.getInstance(channelID) != null ? "true" : "false");
        if (ChannelServer.getInstance(channelID) != null) {
            ChannelServer.getInstance(channelID).broadcastPacket(MaplePacketCreator.serverNotice(0, notice));
        }
    }

    public void channelBroadcastMessage(int channelID, String notice) {
        log.error(ChannelServer.getInstance(channelID) != null ? "true" : "false");
        if (ChannelServer.getInstance(channelID) != null) {
            ChannelServer.getInstance(channelID).broadcastPacket(MaplePacketCreator.serverMessage(notice));
        }
    }

    public int randInt(int arg0) {
        return Randomizer.nextInt(arg0);
    }

    public String getStringDate(String format) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentDateTime.format(formatter);
    }

    public String getStringDate(long timestamp, String format) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    public long getWeekStart(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        LocalDateTime monday = dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public long getWeekEnd(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        LocalDateTime sunday = dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return sunday.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}

