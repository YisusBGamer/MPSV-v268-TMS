/*
 * Decompiled with CFR 0.152.
 */
package Client.stat;

import Client.MapleCharacter;
import Client.MapleTraitType;
import Config.constants.JobConstants;
import Packet.MaplePacketCreator;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DeadDebuff {
    public long Time;
    public int DecDropR = 80;
    public int DecExpR = 80;
    public int Total = 600;

    public static DeadDebuff getDebuff(MapleCharacter chr, int type) {
        String info = chr.getInfoQuest(100716);
        if (info == null || info.isEmpty()) {
            return null;
        }
        DeadDebuff ret = new DeadDebuff();
        String[] split = info.split(";");
        boolean found = false;
        block12: for (String x : split) {
            String[] split2 = x.split("=");
            if (split2.length != 2) continue;
            switch (split2[0]) {
                case "decDropR": {
                    ret.DecDropR = Integer.parseInt(split2[1]);
                    continue block12;
                }
                case "decExpR": {
                    ret.DecExpR = Integer.parseInt(split2[1]);
                    continue block12;
                }
                case "total": {
                    ret.Total = Integer.parseInt(split2[1]);
                    continue block12;
                }
                case "time": {
                    ret.Time = Long.parseLong(split2[1]);
                    found = true;
                }
            }
        }
        if (!found) {
            return null;
        }
        int remain = ret.getRemain();
        if (type != -1) {
            chr.send(MaplePacketCreator.onDeadDebuffSet(type, ret));
            chr.updateOneInfo(100716, "remain", String.valueOf(remain));
        }
        if (remain <= 0) {
            DeadDebuff.cancelDebuff(chr, true);
            return null;
        }
        return ret;
    }

    public static DeadDebuff setDebuff(MapleCharacter chr) {
        return DeadDebuff.setDebuff(chr, 80, 80, 0);
    }

    public static DeadDebuff setDebuff(MapleCharacter chr, int decDropR, int decExpR, int total) {
        DeadDebuff.cancelDebuff(chr, false);
        if (total == 0) {
            int level = chr.getLevel();
            if (level >= 30) {
                total += 300;
            }
            if (level >= 200) {
                total += 300;
            }
            if (level >= 210) {
                total += 300 * ((level - 200) / 10);
            }
        }
        if (JobConstants.getJobGrade(chr.getJob()) == 0 || total <= 0) {
            return null;
        }
        DeadDebuff ret = new DeadDebuff();
        ret.Time = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()));
        ret.DecDropR = decDropR;
        ret.DecExpR = decExpR;
        ret.Total = (int)(((double)total * 100.0 - (double)chr.getTrait(MapleTraitType.charisma).getLevel() / 2.0) / 100.0);
        chr.updateInfoQuest(100716, String.format("decDropR=%d;time=%d;remain=%d;total=%d;decExpR=%d", decDropR, ret.Time, total, total, decExpR));
        return ret;
    }

    public static void cancelDebuff(MapleCharacter chr, boolean send) {
        if (send) {
            chr.send(MaplePacketCreator.onDeadDebuffSet(2, null));
        }
        chr.updateInfoQuest(100716, null);
    }

    public int getRemain() {
        try {
            return Math.max((int)Math.ceil((double)(new SimpleDateFormat("yyyyMMddHHmmss").parse(String.valueOf(this.Time)).getTime() + (long)(this.Total * 1000) - System.currentTimeMillis()) / 1000.0), 0);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

