/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import Client.MapleCharacter;
import Client.MapleClient;
import Net.server.maps.MapleMap;
import Server.channel.ChannelServer;
import java.awt.Point;

public class MaplePortal {
    public static final int DOOR_PORTAL = 6;
    public static int MAP_PORTAL = 2;
    private final int type;
    private String name;
    private String target;
    private String scriptName;
    private Point position;
    private int targetmap;
    private int id;
    private boolean portalState = true;

    public MaplePortal(int type) {
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getTargetMapId() {
        return this.targetmap;
    }

    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    public int getType() {
        return this.type;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public void enterPortal(MapleClient c) {
        MapleCharacter player = c.getPlayer();
        MapleMap currentmap = c.getPlayer().getMap();
        if (this.getScriptName() != null) {
            c.getPlayer().checkFollow();
            c.getPlayer().getScriptManager().startPortalScript(this);
        } else if (this.getTargetMapId() != 999999999) {
            MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(this.getTargetMapId());
            if (to == null) {
                c.sendEnableActions();
                return;
            }
            if (!c.getPlayer().isGm() && to.getLevelLimit() > 0 && to.getLevelLimit() > c.getPlayer().getLevel()) {
                c.getPlayer().dropMessage(-1, "未達到等級要求，無法進入該地區.");
                c.sendEnableActions();
                return;
            }
            c.getPlayer().changeMap(to, to.getPortal(this.getTarget()) == null ? to.getPortal(0) : to.getPortal(this.getTarget()));
        }
        if (c.getPlayer() != null && c.getPlayer().getMap() == currentmap) {
            c.sendEnableActions();
        }
    }

    public boolean getPortalState() {
        return this.portalState;
    }

    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }
}

