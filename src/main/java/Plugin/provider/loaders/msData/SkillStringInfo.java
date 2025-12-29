/*
 * Decompiled with CFR 0.152.
 */
package Plugin.provider.loaders.msData;

public class SkillStringInfo {
    private String name;
    private String desc;
    private String h;

    public SkillStringInfo(String name, String desc, String h) {
        this.name = name;
        this.desc = desc;
        this.h = h;
    }

    public SkillStringInfo() {
        this.name = "";
        this.desc = "";
        this.h = "";
    }

    public String getName() {
        return this.name == null ? "" : this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc == null ? "" : this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getH() {
        return this.h == null ? "" : this.h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String toString() {
        return "Name: " + this.name + ", Desc: " + this.desc + ", H: " + this.h;
    }
}

