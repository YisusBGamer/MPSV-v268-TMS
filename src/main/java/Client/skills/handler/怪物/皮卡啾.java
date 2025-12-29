/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.怪物;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import java.lang.reflect.Field;
import tools.data.MaplePacketReader;

public class 皮卡啾
extends AbstractSkillHandler {
    public 皮卡啾() {
        this.jobs = new MapleJob[]{MapleJob.皮卡啾, MapleJob.皮卡啾1轉};
        for (Field field : Config.constants.skills.皮卡啾.class.getDeclaredFields()) {
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
            case 131001001: 
            case 131001002: 
            case 131001003: 
            case 131001101: 
            case 131001102: 
            case 131001103: 
            case 131002000: {
                return 131001000;
            }
            case 131001104: 
            case 131002004: {
                return 131001004;
            }
            case 131002016: 
            case 131003016: {
                return 131000016;
            }
            case 131001113: 
            case 131001213: 
            case 131001313: {
                return 131001013;
            }
            case 131001108: 
            case 131001208: {
                return 131001008;
            }
            case 131001106: 
            case 131001206: 
            case 131001306: 
            case 131001406: 
            case 131001506: {
                return 131001006;
            }
            case 131002014: {
                return 131000014;
            }
            case 131001107: 
            case 131001207: 
            case 131001307: {
                return 131001007;
            }
            case 131001011: 
            case 131002010: {
                return 131001010;
            }
            case 131002015: {
                return 131001015;
            }
            case 131002012: {
                return 131001012;
            }
            case 131002017: 
            case 131003017: {
                return 131001017;
            }
            case 131002018: {
                return 131001018;
            }
            case 131002020: {
                return 131001020;
            }
            case 131002021: {
                return 131001021;
            }
            case 131002022: 
            case 131003022: 
            case 131004022: 
            case 131005022: 
            case 131006022: {
                return 131001022;
            }
            case 131002023: 
            case 131003023: 
            case 131004023: 
            case 131005023: {
                return 131001023;
            }
            case 131002025: {
                return 131001025;
            }
            case 131002026: 
            case 131003026: {
                return 131001026;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 131001017) {
            chr.getSkillEffect(131002017).applyTo(chr);
            chr.getSkillEffect(131003017).applyTo(chr);
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 131001010: 
            case 131001011: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.PinkbeanYoYoStack);
                int value = Math.min(applyto.getBuffedIntValue(SecondaryStat.PinkbeanYoYoStack) + (applier.passive ? 1 : -1), 8);
                if (mbsvh != null && applier.passive && System.currentTimeMillis() < mbsvh.startTime + 1500L) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.PinkbeanYoYoStack, value);
                return 1;
            }
            case 131001018: {
                applier.localstatups.put(SecondaryStat.IndieStatR, applyfrom.getLevel() / applier.effect.getY());
                return 1;
            }
        }
        return -1;
    }
}

