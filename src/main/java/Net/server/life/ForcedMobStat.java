/*
 * Decompiled with CFR 0.152.
 */
package Net.server.life;

import Config.configs.ServerConfig;
import Net.server.life.MapleMonsterStats;

public final class ForcedMobStat {
    private int PDRate;
    private int watk;
    private int speed;
    private int level;
    private int userCount;
    private int pushed;
    private int MDRate;
    private int eva;
    private int acc;
    private boolean change;
    private int matk;
    private long exp;

    public ForcedMobStat(MapleMonsterStats stats, int newLevel, double r) {
        newLevel = Math.min(newLevel, ServerConfig.CHANNEL_PLAYER_MAXLEVEL);
        if (stats.isBoss()) {
            this.PDRate = stats.getPDRate();
            this.MDRate = stats.getMDRate();
        } else {
            this.PDRate = Math.min(50, (int)Math.round((double)stats.getPDRate() * r));
            this.MDRate = Math.min(50, (int)Math.round((double)stats.getMDRate() * r));
        }
        this.exp = (int)((double)stats.getExp() * r);
        this.watk = (int)((double)stats.getPhysicalAttack() * r);
        this.matk = (int)((double)stats.getMagicAttack() * r);
        this.acc = Math.round(stats.getAcc() + Math.max(0, newLevel - stats.getLevel()) * 2);
        this.eva = Math.round(stats.getEva() + Math.max(0, newLevel - stats.getLevel()));
        this.pushed = (int)((double)stats.getPushed() * r);
        this.speed = 0;
        this.level = newLevel;
        this.userCount = 0;
        this.change = true;
    }

    public int getUserCount() {
        return this.userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getPushed() {
        return this.pushed;
    }

    public void setPushed(int pushed) {
        this.pushed = pushed;
    }

    public int getMDRate() {
        return this.MDRate;
    }

    public void setMDRate(int mdRate) {
        this.MDRate = mdRate;
    }

    public int getPDRate() {
        return this.PDRate;
    }

    public void setPDRate(int pdRate) {
        this.PDRate = pdRate;
    }

    public int getEva() {
        return this.eva;
    }

    public void setEva(int eva) {
        this.eva = eva;
    }

    public int getAcc() {
        return this.acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public int getMatk() {
        return this.matk;
    }

    public void setMatk(int matk) {
        this.matk = matk;
    }

    public int getWatk() {
        return this.watk;
    }

    public void setWatk(int watk) {
        this.watk = watk;
    }

    public long getExp() {
        return this.exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public boolean isChange() {
        return this.change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }
}

