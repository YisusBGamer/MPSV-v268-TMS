/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character.skills;

import Net.server.MapleStatInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Generated;

public class Skill {
    private final int id;
    private boolean invisible;
    private int masterLevel;
    private int fixLevel;
    private boolean notRemoved;
    private boolean canNotStealableSkill;
    private boolean isPetAutoBuff;
    private int hyper;
    private String elemAttr = "";
    private Map<MapleStatInfo, String> common = new HashMap<MapleStatInfo, String>();
    private Map<MapleStatInfo, String> pVPcommon = new HashMap<MapleStatInfo, String>();
    private Map<String, String> info = new HashMap<String, String>();
    private Map<String, String> info2 = new HashMap<String, String>();
    private boolean psd = false;
    private Set<Integer> psdSkills = new HashSet<Integer>();

    public Skill(int id) {
        this.id = id;
    }

    @Generated
    public int getId() {
        return this.id;
    }

    @Generated
    public boolean isInvisible() {
        return this.invisible;
    }

    @Generated
    public int getMasterLevel() {
        return this.masterLevel;
    }

    @Generated
    public int getFixLevel() {
        return this.fixLevel;
    }

    @Generated
    public boolean isNotRemoved() {
        return this.notRemoved;
    }

    @Generated
    public boolean isCanNotStealableSkill() {
        return this.canNotStealableSkill;
    }

    @Generated
    public boolean isPetAutoBuff() {
        return this.isPetAutoBuff;
    }

    @Generated
    public int getHyper() {
        return this.hyper;
    }

    @Generated
    public String getElemAttr() {
        return this.elemAttr;
    }

    @Generated
    public Map<MapleStatInfo, String> getCommon() {
        return this.common;
    }

    @Generated
    public Map<MapleStatInfo, String> getPVPcommon() {
        return this.pVPcommon;
    }

    @Generated
    public Map<String, String> getInfo() {
        return this.info;
    }

    @Generated
    public Map<String, String> getInfo2() {
        return this.info2;
    }

    @Generated
    public boolean isPsd() {
        return this.psd;
    }

    @Generated
    public Set<Integer> getPsdSkills() {
        return this.psdSkills;
    }

    @Generated
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    @Generated
    public void setMasterLevel(int masterLevel) {
        this.masterLevel = masterLevel;
    }

    @Generated
    public void setFixLevel(int fixLevel) {
        this.fixLevel = fixLevel;
    }

    @Generated
    public void setNotRemoved(boolean notRemoved) {
        this.notRemoved = notRemoved;
    }

    @Generated
    public void setCanNotStealableSkill(boolean canNotStealableSkill) {
        this.canNotStealableSkill = canNotStealableSkill;
    }

    @Generated
    public void setPetAutoBuff(boolean isPetAutoBuff) {
        this.isPetAutoBuff = isPetAutoBuff;
    }

    @Generated
    public void setHyper(int hyper) {
        this.hyper = hyper;
    }

    @Generated
    public void setElemAttr(String elemAttr) {
        this.elemAttr = elemAttr;
    }

    @Generated
    public void setCommon(Map<MapleStatInfo, String> common) {
        this.common = common;
    }

    @Generated
    public void setPVPcommon(Map<MapleStatInfo, String> pVPcommon) {
        this.pVPcommon = pVPcommon;
    }

    @Generated
    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    @Generated
    public void setInfo2(Map<String, String> info2) {
        this.info2 = info2;
    }

    @Generated
    public void setPsd(boolean psd) {
        this.psd = psd;
    }

    @Generated
    public void setPsdSkills(Set<Integer> psdSkills) {
        this.psdSkills = psdSkills;
    }
}

