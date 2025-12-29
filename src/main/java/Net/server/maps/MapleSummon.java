/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.anticheat.CheatingOffense;
import Client.skills.SkillFactory;
import Config.constants.SkillConstants;
import Config.constants.enums.SummonAttackType;
import Net.server.buffs.MapleStatEffect;
import Net.server.maps.AnimatedMapleMapObject;
import Net.server.maps.MapleFoothold;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.SummonMovementType;
import Packet.SummonPacket;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public final class MapleSummon
extends AnimatedMapleMapObject {
    private final int ownerid;
    private final int skillLevel;
    private final int ownerLevel;
    private final MapleCharacter owner;
    private long createTime;
    private final SummonMovementType movementType;
    private int skillId;
    private int duration;
    private int moveRange;
    private MapleMap map;
    private int hp = 1;
    private boolean changedMap = false;
    private int lastSummonTickCount;
    private byte Summon_tickResetCount;
    private long Server_ClientSummonTickDiff;
    private long lastAttackTime;
    private int mobid;
    private boolean soul1 = false;
    private boolean soul2 = false;
    private MapleStatEffect effect;
    private int acState1;
    private int acState2;
    private final int[] state = new int[3];
    private int shadow;
    private int animated;
    private boolean resist = false;

    public MapleSummon(MapleCharacter owner, MapleStatEffect effect, Point pos, SummonMovementType movementType, int duration, int range, int mobid, long startTime) {
        this(owner, effect.getSourceId(), effect.getLevel(), pos, movementType, duration, range, mobid, startTime);
        this.effect = effect;
    }

    public MapleSummon(MapleCharacter owner, int sourceid, int level, Point pos, SummonMovementType movementType, int duration, int range, int mobid, long startTime) {
        MapleFoothold fh;
        this.owner = owner;
        this.map = owner.getMap();
        this.ownerid = owner.getId();
        this.ownerLevel = owner.getLevel();
        this.skillId = sourceid;
        this.skillLevel = level;
        this.movementType = movementType;
        this.createTime = startTime;
        this.duration = duration;
        switch (sourceid) {
            case 42111101: 
            case 42111102: {
                range = 0;
            }
        }
        this.moveRange = range;
        this.mobid = mobid;
        if (!this.is替身()) {
            this.lastSummonTickCount = 0;
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = 0L;
            this.lastAttackTime = 0L;
        }
        this.setPosition(pos);
        this.setCurrentFh(movementType != SummonMovementType.FLY && (fh = owner.getMap().getFootholds().findBelow(pos)) != null ? fh.getId() : 0);
        this.setStance(owner.isFacingLeft() ? 1 : 0);
        this.resetAncientCrystal();
    }

    public void resetAncientCrystal() {
        this.acState1 = 0;
        this.acState2 = 0;
        for (int i = 0; i < this.state.length; ++i) {
            this.state[i] = 1;
        }
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (client.getPlayer() != null) {
            client.announce(SummonPacket.spawnSummon(this));
        }
        if (this.resist) {
            client.announce(SummonPacket.summonedSetAbleResist(this.ownerid, this.getObjectId(), (byte)0));
        }
        if (this.skillId == 152101000 && client.getPlayer().getId() == this.getOwnerId()) {
            client.announce(SummonPacket.SummonedStateChange(this, 2, this.acState1, this.acState2));
        }
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(SummonPacket.removeSummon(this, false));
    }

    public void setMap(MapleMap map) {
        this.map = map;
    }

    public MapleCharacter getOwner() {
        return this.map.getPlayerObject(this.ownerid);
    }

    public int getOwnerId() {
        return this.ownerid;
    }

    public int getOwnerLevel() {
        return this.ownerLevel;
    }

    public int getSkillId() {
        return this.skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getSkillLevel() {
        return this.skillLevel;
    }

    public int getSummonHp() {
        return this.hp;
    }

    public void setSummonHp(int hp) {
        this.hp = hp;
    }

    public void addSummonHp(int delta) {
        this.hp = Math.max(0, this.hp + delta);
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDuration() {
        if (this.duration == 2100000000) {
            return this.duration;
        }
        if (this.skillId == 400011065) {
            return this.duration - 50;
        }
        return this.duration - 1000;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMoveRange() {
        return this.moveRange;
    }

    public void setMoveRange(int value) {
        this.moveRange = value;
    }

    public boolean is替身() {
        switch (this.skillId) {
            case 3221014: 
            case 4341006: 
            case 13111024: 
            case 13120007: 
            case 33111003: {
                return true;
            }
        }
        return this.is天使召喚獸();
    }

    public boolean is天使召喚獸() {
        return SkillConstants.is天使祝福戒指(this.skillId);
    }

    public boolean showCharLook() {
        if (this.getOwner() == null) {
            return false;
        }
        switch (this.skillId) {
            case 4341006: 
            case 14111024: 
            case 14121055: 
            case 14121056: 
            case 131001017: 
            case 131002017: 
            case 131003017: 
            case 400011005: 
            case 400031007: 
            case 400031008: 
            case 400031009: 
            case 400041028: {
                return true;
            }
        }
        return false;
    }

    public boolean is神箭幻影() {
        return this.skillId == 3221014;
    }

    public boolean is靈魂助力() {
        return this.skillId == 1301013;
    }

    public boolean is大漩渦() {
        return this.skillId == 12111022;
    }

    public boolean is黑暗雜耍() {
        return this.skillId == 4111007 || this.skillId == 4211007;
    }

    public boolean isSummon() {
        return this.is天使召喚獸() || SkillFactory.getSkill(this.skillId).isSummonSkill();
    }

    public SummonMovementType getMovementType() {
        return this.movementType;
    }

    public byte getAttackType() {
        switch (this.skillId) {
            case 400041038: {
                return SummonAttackType.ASSIST_ATTACK_REPET.value;
            }
            case 2111013: 
            case 3111017: 
            case 3211019: 
            case 4341006: 
            case 5221029: 
            case 12000022: 
            case 12100026: 
            case 0xB8C8C8: 
            case 12120007: 
            case 12120013: 
            case 12120014: 
            case 13111024: 
            case 13120007: 
            case 14000027: 
            case 14001027: 
            case 14111024: 
            case 14121055: 
            case 14121056: 
            case 35111002: 
            case 35121010: 
            case 164121006: 
            case 164121011: 
            case 400011005: 
            case 400031007: 
            case 400031008: 
            case 400031009: 
            case 400031016: 
            case 400031049: 
            case 400031051: 
            case 400041028: {
                return SummonAttackType.ASSIST_NONE.value;
            }
            case 1085: 
            case 1087: 
            case 1301013: 
            case 36121014: 
            case 80000052: 
            case 80000053: 
            case 80000054: 
            case 80000155: 
            case 80001154: 
            case 80001262: 
            case 80010067: 
            case 80010068: 
            case 80010069: 
            case 80010070: 
            case 80010071: 
            case 80010072: 
            case 80010075: 
            case 80010076: 
            case 80010077: 
            case 80010078: 
            case 80010079: 
            case 80010080: 
            case 80011103: 
            case 80011104: 
            case 80011105: 
            case 80011106: 
            case 80011107: 
            case 80011108: 
            case 400021032: {
                return SummonAttackType.ASSIST_HEAL.value;
            }
            case 23111008: 
            case 23111009: 
            case 23111010: 
            case 35111001: 
            case 35111009: 
            case 35111010: 
            case 400011001: {
                return SummonAttackType.ASSIST_ATTACK_EX.value;
            }
            case 35121009: 
            case 152121005: 
            case 400051022: {
                return SummonAttackType.ASSIST_SUMMON.value;
            }
            case 32001014: 
            case 32100010: 
            case 32110017: 
            case 32120019: 
            case 35121003: 
            case 42111103: 
            case 152001003: 
            case 152101000: 
            case 152121006: 
            case 400011077: 
            case 400011078: 
            case 400021033: 
            case 400021068: {
                return SummonAttackType.ASSIST_ATTACK_MANUAL.value;
            }
            case 14111010: {
                return SummonAttackType.ASSIST_ATTACK_COUNTER.value;
            }
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                return SummonAttackType.ASSIST_ATTACK_JAGUAR.value;
            }
            case 5211019: 
            case 5221022: 
            case 5221027: 
            case 42100010: {
                return SummonAttackType.ASSIST_UNKNOWN_12.value;
            }
            case 400011012: 
            case 400011013: 
            case 400011014: {
                return SummonAttackType.ASSIST_UNKNOWN_14.value;
            }
            case 400051009: 
            case 400051017: {
                return SummonAttackType.ASSIST_UNKNOWN_15.value;
            }
            case 400021047: 
            case 400021063: {
                return SummonAttackType.ASSIST_UNKNOWN_16.value;
            }
            case 5201012: 
            case 5210015: {
                return SummonAttackType.ASSIST_UNKNOWN_17.value;
            }
            case 162101003: 
            case 162101006: 
            case 162121012: 
            case 162121015: 
            case 400021071: 
            case 400021073: 
            case 400031047: 
            case 400041044: 
            case 400051038: 
            case 400051046: 
            case 400051052: 
            case 400051053: 
            case 400051068: {
                return SummonAttackType.ASSIST_UNKNOWN_18.value;
            }
            case 164111007: {
                return SummonAttackType.ASSIST_UNKNOWN_5.value;
            }
        }
        return SummonAttackType.ASSIST_ATTACK.value;
    }

    public byte getRemoveStatus() {
        if (this.is天使召喚獸()) {
            return 10;
        }
        switch (this.skillId) {
            case 5321003: 
            case 35111002: 
            case 35111008: 
            case 35111011: 
            case 35121009: 
            case 35121010: 
            case 35121011: 
            case 42100010: 
            case 42101001: 
            case 400011065: {
                return 5;
            }
            case 23111008: 
            case 23111009: 
            case 23111010: 
            case 35111001: 
            case 35111009: 
            case 35111010: 
            case 35121003: 
            case 400051017: {
                return 10;
            }
        }
        return (byte)this.animated;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    public void CheckSummonAttackFrequency(MapleCharacter chr, int tickcount) {
        long STime_TC;
        long S_C_Difference;
        int tickdifference = tickcount - this.lastSummonTickCount;
        if (tickdifference < SkillFactory.getSummonData((int)this.skillId).delay) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        if ((S_C_Difference = this.Server_ClientSummonTickDiff - (STime_TC = System.currentTimeMillis() - (long)tickcount)) > 500L) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        this.Summon_tickResetCount = (byte)(this.Summon_tickResetCount + 1);
        if (this.Summon_tickResetCount > 4) {
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = STime_TC;
        }
        this.lastSummonTickCount = tickcount;
    }

    public void CheckPVPSummonAttackFrequency(MapleCharacter chr) {
        long tickdifference = System.currentTimeMillis() - this.lastAttackTime;
        if (tickdifference < (long)SkillFactory.getSummonData((int)this.skillId).delay) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        this.lastAttackTime = System.currentTimeMillis();
    }

    public boolean checkLastAttackTime() {
        if (System.currentTimeMillis() - (long)this.getSkillCoolTime(this.skillId) * 1000L < this.lastAttackTime) {
            return false;
        }
        this.lastAttackTime = System.currentTimeMillis();
        return true;
    }

    public boolean isChangedMap() {
        return this.changedMap;
    }

    public void setChangedMap(boolean npc) {
        this.changedMap = npc;
    }

    public boolean isChangeMapCanceled() {
        return this.getMovementType() == SummonMovementType.STOP || this.getMovementType() == SummonMovementType.SMART || this.getMovementType() == SummonMovementType.WALK_RANDOM;
    }

    public int getMobid() {
        return this.mobid;
    }

    public void setMobid(int mobid) {
        this.mobid = mobid;
    }

    public int getSkillCoolTime(int skillId) {
        switch (skillId) {
            case 32001014: {
                return 9;
            }
            case 32100010: {
                return 8;
            }
            case 32110017: {
                return 6;
            }
            case 32120019: {
                return 5;
            }
        }
        return 0;
    }

    public int getSummonMaxCount() {
        return MapleSummon.getSummonMaxCount(this.skillId);
    }

    public static int getSummonMaxCount(int skillID) {
        switch (skillID) {
            case 35121011: 
            case 400021047: {
                return -1;
            }
            case 14000027: {
                return 2;
            }
            case 35111002: 
            case 400031051: {
                return 3;
            }
            case 162101012: {
                return 4;
            }
            case 400051023: {
                return 10;
            }
            case 0x111B1F: {
                return 12;
            }
        }
        return 1;
    }

    public boolean isSoul1() {
        return this.soul1;
    }

    public boolean isSoul2() {
        return this.soul2;
    }

    public boolean setSoul1(boolean soul1) {
        this.soul1 = soul1;
        return this.soul1;
    }

    public boolean setSoul2(boolean soul2) {
        this.soul2 = soul2;
        return this.soul2;
    }

    public int mp(int n) {
        return n - (int)(System.currentTimeMillis() - this.createTime);
    }

    public int getAcState1() {
        return this.acState1;
    }

    public void setAcState1(int acState1) {
        this.acState1 = acState1;
    }

    public int getAcState2() {
        return this.acState2;
    }

    public void setAcState2(int acState2) {
        this.acState2 = acState2;
    }

    public int getState(int n) {
        return this.state[n];
    }

    public void setState(int n, int n2) {
        this.state[n] = 0;
    }

    public int getShadow() {
        return this.shadow;
    }

    public void setShadow(int shadow) {
        this.shadow = shadow;
    }

    public MapleStatEffect getEffect() {
        return this.effect;
    }

    public void setAnimated(int animated) {
        this.animated = animated;
    }

    public int getAnimated() {
        return this.animated;
    }

    public boolean isResist() {
        return this.resist;
    }

    public void setResist(boolean b) {
        this.resist = b;
    }

    public static List<Integer> getLinkSummons(int skillID) {
        LinkedList<Integer> summons = new LinkedList<Integer>();
        switch (skillID) {
            case 5210015: {
                summons.add(5211019);
                break;
            }
            case 5321004: {
                summons.add(5320011);
                break;
            }
            case 400051038: {
                summons.add(400051052);
                summons.add(400051052);
            }
        }
        return summons;
    }

    public int getParentSummon() {
        switch (this.skillId) {
            case 5211019: {
                return 5210015;
            }
            case 5320011: {
                return 5321004;
            }
            case 400051052: 
            case 400051053: {
                return 400051038;
            }
        }
        return 0;
    }
}

