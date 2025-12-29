/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character;

import Client.MapleCharacter;
import Config.constants.JobConstants;
import SwordieX.client.character.SPSet;
import SwordieX.util.Util;
import connection.OutPacket;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class ExtendSP {
    private List<SPSet> spSet = new ArrayList<SPSet>();

    public ExtendSP() {
        this(0);
    }

    public ExtendSP(int subJobs) {
        for (int i = 1; i <= subJobs; ++i) {
            this.spSet.add(new SPSet((byte)i, 0));
        }
    }

    public ExtendSP(MapleCharacter chr) {
        boolean isDualBlade = JobConstants.is影武者(chr.getJob());
        int sp1 = 0;
        int sp2 = 0;
        for (int i = 0; i < chr.getRemainingSps().length; ++i) {
            if (chr.getRemainingSps()[i] <= 0) continue;
            if (i < 2) {
                sp1 += chr.getRemainingSp(i);
            } else if (i < 4) {
                sp2 += chr.getRemainingSp(i);
            }
            if (isDualBlade && i < 4) {
                if (i == 1 && sp1 > 0) {
                    this.spSet.add(new SPSet((byte)1, sp1));
                    this.spSet.add(new SPSet((byte)2, sp1));
                }
                if (i != 3 || sp2 <= 0) continue;
                this.spSet.add(new SPSet((byte)3, sp2));
                this.spSet.add(new SPSet((byte)4, sp2));
                continue;
            }
            this.spSet.add(new SPSet((byte)(i + 1), chr.getRemainingSps()[i]));
        }
    }

    public int getTotalSp() {
        return this.spSet.stream().mapToInt(SPSet::getSp).sum();
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(this.getSpSet().size());
        for (SPSet spSet : this.getSpSet()) {
            outPacket.encodeByte(spSet.getJobLevel());
            outPacket.encodeInt(spSet.getSp());
        }
    }

    public void setSpToJobLevel(int jobLevel, int sp) {
        this.getSpSet().stream().filter(sps -> sps.getJobLevel() == jobLevel).findFirst().ifPresent(spSet -> spSet.setSp(sp));
    }

    public int getSpByJobLevel(byte jobLevel) {
        SPSet spSet = Util.findWithPred(this.getSpSet(), sps -> sps.getJobLevel() == jobLevel);
        if (spSet != null) {
            return spSet.getSp();
        }
        return -1;
    }

    @Generated
    public void setSpSet(List<SPSet> spSet) {
        this.spSet = spSet;
    }

    @Generated
    public List<SPSet> getSpSet() {
        return this.spSet;
    }
}

