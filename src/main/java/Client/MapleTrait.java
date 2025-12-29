/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Client.MapleCharacter;
import Client.MapleTraitType;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Packet.MaplePacketCreator;

public class MapleTrait {
    private MapleTraitType type;
    private int totalExp = 0;
    private int localTotalExp = 0;
    private short exp = 0;
    private byte level = 0;

    public MapleTrait() {
    }

    public MapleTrait(MapleTraitType traitTyp) {
        this.type = traitTyp;
    }

    public void setExp(int exp, MapleCharacter chr) {
        if (exp >= 0) {
            this.totalExp = exp;
            this.localTotalExp = exp;
            chr.updateSingleStat(this.type.getStat(), this.totalExp);
            chr.send(MaplePacketCreator.showTraitGain(this.type, exp));
            this.recalcLevel();
        }
    }

    public void addExp(int exp) {
        this.totalExp += exp;
        this.localTotalExp += exp;
        if (exp != 0) {
            this.recalcLevel();
        }
    }

    public void addExp(int exp, MapleCharacter chr) {
        this.addTrueExp(exp * ServerConfig.CHANNEL_RATE_TRAIT, chr);
    }

    public void addTrueExp(int exp, MapleCharacter chr) {
        if (exp != 0) {
            this.totalExp += exp;
            this.localTotalExp += exp;
            chr.updateSingleStat(this.type.getStat(), this.totalExp);
            chr.send(MaplePacketCreator.showTraitGain(this.type, exp));
            this.recalcLevel();
        }
    }

    public boolean recalcLevel() {
        if (this.totalExp < 0) {
            this.totalExp = 0;
            this.localTotalExp = 0;
            this.level = 0;
            this.exp = 0;
            return false;
        }
        byte oldLevel = this.level;
        for (int i = 0; i < 100; i = (int)((byte)(i + 1))) {
            if (GameConstants.getTraitExpNeededForLevel(i) <= this.localTotalExp) continue;
            this.exp = (short)(GameConstants.getTraitExpNeededForLevel(i) - this.localTotalExp);
            this.level = (byte)(i - 1);
            return this.level > oldLevel;
        }
        this.exp = 0;
        this.level = (byte)100;
        this.localTotalExp = this.totalExp = GameConstants.getTraitExpNeededForLevel(this.level);
        return this.level > oldLevel;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int newLevel, MapleCharacter chr) {
        if (newLevel <= this.level) {
            return;
        }
        this.localTotalExp = this.totalExp = GameConstants.getTraitExpNeededForLevel(newLevel);
        this.recalcLevel();
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.totalExp = exp;
        this.localTotalExp = exp;
        this.recalcLevel();
    }

    public int getTotalExp() {
        return this.totalExp;
    }

    public int getLocalTotalExp() {
        return this.localTotalExp;
    }

    public void addLocalExp(int exp) {
        this.localTotalExp += exp;
    }

    public void clearLocalExp() {
        this.localTotalExp = this.totalExp;
    }

    public MapleTraitType getType() {
        return this.type;
    }
}

