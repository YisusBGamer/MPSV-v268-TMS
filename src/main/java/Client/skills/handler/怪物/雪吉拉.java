/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.怪物;

import Client.MapleJob;
import Client.skills.handler.AbstractSkillHandler;
import java.lang.reflect.Field;

public class 雪吉拉
extends AbstractSkillHandler {
    public 雪吉拉() {
        this.jobs = new MapleJob[]{MapleJob.雪吉拉, MapleJob.雪吉拉1轉};
        for (Field field : Config.constants.skills.雪吉拉.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 135001001: 
            case 135001002: 
            case 135002000: {
                return 135001000;
            }
            case 135001004: 
            case 135003003: 
            case 135003004: {
                return 135001003;
            }
            case 135003007: {
                return 135001007;
            }
            case 135002008: {
                return 135001008;
            }
            case 135001010: {
                return 135000010;
            }
            case 135002011: {
                return 135001011;
            }
            case 135002013: {
                return 135001013;
            }
            case 135002015: {
                return 135001015;
            }
            case 135002016: {
                return 135001016;
            }
            case 135002018: {
                return 135001018;
            }
            case 135002019: {
                return 135001019;
            }
            case 135001014: {
                return 135000014;
            }
            case 135002020: {
                return 135001020;
            }
            case 135002021: {
                return 135000021;
            }
            case 135002006: {
                return 135001006;
            }
            case 135002022: {
                return 135000022;
            }
        }
        return -1;
    }
}

