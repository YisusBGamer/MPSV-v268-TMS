/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Packet.TownPortalPacket
 *  SwordieX.client.party.PartyResult
 *  SwordieX.client.party.TownPortal
 *  SwordieX.util.Position
 *  connection.packet.WvsContext
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Config.constants.GameConstants;
import Net.server.MaplePortal;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.MaplePacketCreator;
import Packet.TownPortalPacket;
import SwordieX.client.party.PartyResult;
import SwordieX.util.Position;
import connection.packet.WvsContext;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;

public class TownPortal
extends MapleMapObject {
    private final WeakReference<MapleCharacter> owner;
    private final MapleMap townMap;
    private final MaplePortal townPortal;
    private final MapleMap fieldMap;
    private final int skillId;
    private final int ownerId;
    private final Point fieldPosition;
    private int state = 0;

    public TownPortal(MapleCharacter owner, int skillId) {
        this.owner = new WeakReference<MapleCharacter>(owner);
        this.ownerId = owner.getId();
        this.fieldMap = owner.getMap();
        this.fieldPosition = owner.getPosition();
        this.townMap = this.fieldMap.getReturnMap();
        this.townPortal = this.getFreePortal();
        this.skillId = skillId;
        this.setPosition(this.fieldPosition);
    }

    public TownPortal(TownPortal originTownPortal) {
        this.owner = originTownPortal.owner;
        this.ownerId = originTownPortal.ownerId;
        this.fieldMap = originTownPortal.fieldMap;
        this.fieldPosition = originTownPortal.fieldPosition;
        this.townMap = originTownPortal.townMap;
        this.townPortal = originTownPortal.townPortal;
        this.skillId = originTownPortal.skillId;
        this.setPosition(this.townPortal.getPosition());
    }

    public int getSkillId() {
        return this.skillId;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        MapleCharacter player = client.getPlayer();
        if (player == null) {
            return;
        }
        if (this.fieldMap.getId() != client.getPlayer().getMapId() && this.townMap.getId() != client.getPlayer().getMapId() && this.getOwnerId() != client.getPlayer().getId() && (this.getOwner() == null || this.getOwner().getParty() == null || client.getPlayer().getParty() == null || this.getOwner().getParty().getId() != client.getPlayer().getParty().getId())) {
            return;
        }
        boolean isTown = this.getTownMap().getId() == player.getMapId();
        Point doorPoint = isTown ? this.getTownPortal().getPosition() : this.getFieldPosition();
        client.announce(TownPortalPacket.onTownPortalCreated((TownPortal)this));
        if (this.getOwner().getParty() != null && (this.getOwner() == player || this.getOwner().getParty().getPartyMemberByID(player.getId()) != null)) {
            SwordieX.client.party.TownPortal tp = new SwordieX.client.party.TownPortal();
            tp.setTownID(this.getTownMap().getId());
            tp.setFieldID(this.getFieldMap().getId());
            tp.setSkillID(this.getSkillId());
            tp.setFieldPortal(new Position(doorPoint.x, doorPoint.y));
            client.write(WvsContext.partyResult((PartyResult)PartyResult.townPortalChanged((int)1, (SwordieX.client.party.TownPortal)tp)));
        }
        client.announce(MaplePacketCreator.onTownPortal(this.getTownMap().getId(), this.getFieldMap().getId(), this.getSkillId(), doorPoint));
        player.enableActions(true);
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        MapleCharacter player = client.getPlayer();
        if (player == null) {
            return;
        }
        client.announce(TownPortalPacket.onTownPortalRemoved((int)this.getOwnerId(), (boolean)true));
        if (this.fieldMap.getId() == client.getPlayer().getMapId() || this.getOwnerId() == client.getPlayer().getId() || this.getOwner() != null && this.getOwner().getParty() != null && client.getPlayer().getParty() != null && this.getOwner().getParty().getId() == client.getPlayer().getParty().getId() || this.getTownMap().getId() == client.getPlayer().getMapId()) {
            if (this.getOwner().getParty() != null && (this.getOwnerId() == client.getPlayer().getId() || this.getOwner().getParty().getPartyMemberByID(client.getPlayer().getId()) != null)) {
                SwordieX.client.party.TownPortal tp = new SwordieX.client.party.TownPortal();
                tp.setTownID(999999999);
                tp.setFieldID(999999999);
                tp.setFieldPortal(new Position(-1, -1));
                client.write(WvsContext.partyResult((PartyResult)PartyResult.townPortalChanged((int)1, (SwordieX.client.party.TownPortal)tp)));
            }
            client.announce(MaplePacketCreator.onTownPortal(999999999, 999999999, 0, null));
        }
    }

    public void warp(MapleCharacter chr, boolean toTown) {
        if (chr.getId() != this.getOwnerId() && (this.getOwner() == null || this.getOwner().getParty() == null || chr.getParty() == null || this.getOwner().getParty().getId() != chr.getParty().getId())) {
            chr.getClient().sendEnableActions();
        } else if (toTown) {
            chr.changeMapToPosition(this.townMap, this.townPortal.getPosition());
        } else {
            chr.changeMapToPosition(this.fieldMap, this.fieldPosition);
        }
    }

    private MaplePortal getFreePortal() {
        ArrayList<MaplePortal> freePortals = new ArrayList<MaplePortal>();
        for (MaplePortal port : this.townMap.getPortals()) {
            if (port.getType() != 6) continue;
            freePortals.add(port);
        }
        freePortals.sort(Comparator.comparingInt(MaplePortal::getId));
        for (TownPortal townPortal : this.townMap.getAllTownPortalsThreadsafe()) {
            if (townPortal.getOwner() == null || townPortal.getOwner().getParty() == null || this.getOwner() == null || this.getOwner().getParty() == null || this.getOwner().getParty().getPartyMemberByID(townPortal.getOwnerId()) == null) continue;
            freePortals.remove(townPortal.getTownPortal());
        }
        if (freePortals.size() <= 0) {
            return null;
        }
        return (MaplePortal)freePortals.iterator().next();
    }

    public MapleCharacter getOwner() {
        return (MapleCharacter)this.owner.get();
    }

    public MapleMap getTownMap() {
        return this.townMap;
    }

    public MaplePortal getTownPortal() {
        return this.townPortal;
    }

    public MapleMap getFieldMap() {
        return this.fieldMap;
    }

    public Point getFieldPosition() {
        return this.fieldPosition;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.TOWN_PORTAL;
    }

    @Override
    public int getRange() {
        return GameConstants.maxViewRange();
    }

    @Override
    public int getObjectId() {
        return this.ownerId;
    }
}

