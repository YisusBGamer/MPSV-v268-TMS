/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.江湖;

import Client.skills.handler.AbstractSkillHandler;
import Config.constants.JobConstants;
import Config.constants.skills.通用V核心;
import java.lang.reflect.Field;

public class 江湖
extends AbstractSkillHandler {
    public 江湖() {
        for (Field field : 通用V核心.江湖通用.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean containsJob(int jobWithSub) {
        return JobConstants.is江湖(jobWithSub);
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 400001091: 
            case 400001093: 
            case 400001094: 
            case 400001095: 
            case 400001096: {
                return 400001092;
            }
        }
        return -1;
    }
}

