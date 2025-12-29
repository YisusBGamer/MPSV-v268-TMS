/*
 * Decompiled with CFR 0.152.
 */
package Client.skills;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class ExtraSkill {
    public int SkillID;
    public int TriggerSkillID = 0;
    public int FaceLeft = 0;
    public int Delay = 0;
    public int Value = 0;
    public int TargetOID = 0;
    public Point Position;
    public List<Integer> MobOIDs = new LinkedList<Integer>();
    public List<Integer> UnkList = new LinkedList<Integer>();

    public ExtraSkill(int skillId, Point pos) {
        this.SkillID = skillId;
        this.Position = pos;
    }
}

