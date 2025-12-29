/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import java.util.ArrayList;

public final class VCoreDataEntry {
    private int id = 0;
    private String name;
    private String desc;
    private int type = 0;
    private int maxLevel = 0;
    private final ArrayList<String> jobs = new ArrayList();
    private final ArrayList<Integer> connectSkill = new ArrayList();
    private boolean nobAbleGemStone = false;
    private boolean notAbleCraft = false;
    private boolean noDisassemble = false;

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

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public ArrayList<String> getJobs() {
        return this.jobs;
    }

    public boolean haveJob(String job) {
        return this.haveJob(job, false);
    }

    public boolean haveJob(String job, boolean onlyJob) {
        return this.jobs.contains(job) || !onlyJob && this.jobs.contains("all");
    }

    public void addJob(String job) {
        this.jobs.add(job);
    }

    public ArrayList<Integer> getConnectSkill() {
        return this.connectSkill;
    }

    public boolean isNobAbleGemStone() {
        return this.nobAbleGemStone;
    }

    public void setNobAbleGemStone(boolean b) {
        this.nobAbleGemStone = b;
    }

    public boolean isNotAbleCraft() {
        return this.notAbleCraft;
    }

    public void setNotAbleCraft(boolean b) {
        this.notAbleCraft = b;
    }

    public boolean isDisassemble() {
        return this.noDisassemble;
    }

    public void setDisassemble(boolean b) {
        this.noDisassemble = b;
    }
}

