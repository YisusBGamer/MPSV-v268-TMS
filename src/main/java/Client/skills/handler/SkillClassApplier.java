/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler;

import Client.SecondaryStat;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.status.MonsterStatus;
import Net.server.buffs.MapleStatEffect;
import Server.channel.handler.AttackInfo;
import java.awt.Point;
import java.util.Map;
import tools.Pair;

public class SkillClassApplier {
    public MapleStatEffect effect;
    public boolean primary;
    public boolean att;
    public boolean passive;
    public boolean b3;
    public boolean b4;
    public boolean b5;
    public boolean b7;
    public boolean overwrite;
    public boolean cancelEffect;
    public boolean applySummon;
    public Point pos;
    public int duration;
    public int maskedDuration;
    public int cooldown;
    public int buffz;
    public int mobOid;
    public int hpHeal;
    public int mpHeal;
    public int prop;
    public int unk;
    public int plus;
    public Map<SecondaryStat, Integer> localstatups;
    public Map<SecondaryStat, Integer> maskedstatups;
    public Map<SecondaryStat, Pair<Integer, Integer>> sendstatups;
    public Map<MonsterStatus, Integer> localmobstatups;
    public Map<Integer, SkillEntry> skillMap;
    public long startChargeTime;
    public long startTime;
    public long totalDamage;
    public Skill theSkill;
    public AttackInfo ai = null;
}

