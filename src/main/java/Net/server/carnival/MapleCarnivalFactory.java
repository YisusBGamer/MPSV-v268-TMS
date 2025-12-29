/*
 * Decompiled with CFR 0.152.
 */
package Net.server.carnival;

import Net.server.life.MobSkill;
import Net.server.life.MobSkillFactory;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.util.HashMap;
import java.util.Map;

public class MapleCarnivalFactory {
    private static final MapleCarnivalFactory instance = new MapleCarnivalFactory();
    private final Map<Integer, MCSkill> skills = new HashMap<Integer, MCSkill>();
    private final Map<Integer, MCSkill> guardians = new HashMap<Integer, MCSkill>();
    private final MapleDataProvider dataRoot = MapleDataProviderFactory.getSkill();

    public MapleCarnivalFactory() {
        this.initialize();
    }

    public static MapleCarnivalFactory getInstance() {
        return instance;
    }

    private void initialize() {
        if (!this.skills.isEmpty()) {
            return;
        }
        for (MapleData z : this.dataRoot.getData("MCSkill.img")) {
            new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), MapleDataTool.getInt("target", z, 1) > 1);
        }
        for (MapleData z : this.dataRoot.getData("MCGuardian.img")) {
            new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), true);
        }
    }

    public MCSkill getSkill(int id) {
        return this.skills.get(id);
    }

    public MCSkill getGuardian(int id) {
        return this.guardians.get(id);
    }

    public static class MCSkill {
        public final int cpLoss;
        public final int skillid;
        public final int level;
        public final boolean targetsAll;

        public MCSkill(int _cpLoss, int _skillid, int _level, boolean _targetsAll) {
            this.cpLoss = _cpLoss;
            this.skillid = _skillid;
            this.level = _level;
            this.targetsAll = _targetsAll;
        }

        public MobSkill getSkill() {
            return MobSkillFactory.getMobSkill(this.skillid, 1);
        }
    }
}

