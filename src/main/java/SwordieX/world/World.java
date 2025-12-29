/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.client.party.Party
 *  SwordieX.enums.ServerStatus
 */
package SwordieX.world;

import SwordieX.client.party.Party;
import SwordieX.enums.ServerStatus;
import SwordieX.enums.WorldId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class World {
    private final WorldId worldId;
    private final int worldState;
    private final int worldEventEXP_WSE;
    private final int worldEventDrop_WSE;
    private final int boomUpEventNotice;
    private final String name;
    private String worldEventDescription;
    private final Map<Integer, Party> parties = new HashMap<Integer, Party>();
    private final AtomicInteger partyIDCounter = new AtomicInteger(1);
    private boolean reboot;

    public World(WorldId worldId, String name, int worldState, String worldEventDescription, int worldEventEXP_WSE, int worldEventDrop_WSE, int boomUpEventNotice, int amountOfChannels, boolean reboot) {
        this.worldId = worldId;
        this.name = name;
        this.worldState = worldState;
        this.worldEventDescription = worldEventDescription;
        this.worldEventEXP_WSE = worldEventEXP_WSE;
        this.worldEventDrop_WSE = worldEventDrop_WSE;
        this.boomUpEventNotice = boomUpEventNotice;
        this.reboot = reboot;
    }

    public World(WorldId worldId, String name, int amountOfChannels, String worldEventMsg) {
        this(worldId, name, 0, worldEventMsg, 100, 100, 0, amountOfChannels, false);
    }

    public WorldId getWorldId() {
        return this.worldId;
    }

    public String getName() {
        return this.name;
    }

    public int getWorldState() {
        return this.worldState;
    }

    public int getWorldEventEXP_WSE() {
        return this.worldEventEXP_WSE;
    }

    public int getWorldEventDrop_WSE() {
        return this.worldEventDrop_WSE;
    }

    public int getBoomUpEventNotice() {
        return this.boomUpEventNotice;
    }

    public String getWorldEventDescription() {
        return this.worldEventDescription;
    }

    public void setWorldEventDescription(String worldEventDescription) {
        this.worldEventDescription = worldEventDescription;
    }

    public ServerStatus getStatus() {
        return ServerStatus.NORMAL;
    }

    public Map<Integer, Party> getParties() {
        return this.parties;
    }

    public void addParty(Party p) {
        int id = this.getPartyIdAndIncrement();
        this.getParties().put(id, p);
        p.setId(id);
        if (p.getWorld() == null) {
            p.setWorld(this);
        }
    }

    public void removeParty(Party p) {
        this.getParties().remove(p.getId());
    }

    public Party getPartybyId(int partyID) {
        return this.getParties().get(partyID);
    }

    public Party getPartybyMemberId(int memberId) {
        return this.parties.values().stream().filter(party -> party.getPartyMemberByID(memberId) != null).findFirst().orElse(null);
    }

    public boolean isReboot() {
        return this.reboot;
    }

    public void setReboot(boolean reboot) {
        this.reboot = reboot;
    }

    public int getPartyIdAndIncrement() {
        return this.partyIDCounter.getAndIncrement();
    }

    public int getPartyIDCounter() {
        return this.partyIDCounter.get();
    }

    public void setPartyIDCounter(int partyIDCounter) {
        this.partyIDCounter.set(partyIDCounter);
    }

}

