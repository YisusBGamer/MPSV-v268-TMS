/*
 * Decompiled with CFR 0.152.
 */
package Config.constants;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassFetcher;
import Client.stat.PlayerStats;
import Client.status.MonsterStatus;
import Config.constants.JobConstants;
import Net.server.buffs.MapleStatEffect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.Randomizer;

public class SkillConstants {
    public MapleClient c;
    private static final int[] innerSkills = new int[]{70000036, 70000039, 70000000, 70000001, 70000002, 70000003, 70000051, 70000052, 70000053, 70000054, 70000055, 70000056, 70000057, 70000058, 70000059, 70000060, 70000061, 70000062, 70000015, 70000021, 70000022, 70000023, 70000024, 70000031, 70000032, 70000006, 70000005, 70000048, 70000049, 70000050, 70000012, 70000013, 70000043, 70000035, 70000041, 70000025, 70000026, 70000008, 70000008, 70000029, 70000045, 70000016, 70000046, 70000047};
    public int skillid;
    public static Map<Integer, Integer> TeachSkillMap = new LinkedHashMap<Integer, Integer>();

    public MapleClient getClient() {
        return this.c;
    }

    public static boolean isForceIncrease(int skillId) {
        switch (skillId) {
            case 31000004: 
            case 31001006: 
            case 31001007: 
            case 31001008: 
            case 31100007: 
            case 31110010: 
            case 31120011: {
                return true;
            }
        }
        return false;
    }

    public static boolean isRecoveryIncSkill(int skillId) {
        switch (skillId) {
            case 1110000: 
            case 51110000: 
            case 61110006: {
                return true;
            }
        }
        return false;
    }

    public static boolean isLinkedAttackSkill(int skillId) {
        return SkillConstants.getLinkedAttackSkill(skillId) != skillId;
    }

    public static int getLinkedAttackSkill(int skillId) {
        int linkedSkillID;
        AbstractSkillHandler handler = SkillClassFetcher.getHandlerBySkill(skillId);
        if (handler != null && (linkedSkillID = handler.getLinkedSkillID(skillId)) != -1) {
            return linkedSkillID;
        }
        switch (skillId) {
            case 21000006: 
            case 21000007: {
                return 21001010;
            }
            case 21111032: 
            case 21111033: 
            case 21121022: {
                return 21101004;
            }
            case 21121016: {
                return 21110031;
            }
            case 21121017: {
                return 21120002;
            }
            case 3141501: 
            case 3141502: {
                return 3141500;
            }
            case 104020000: {
                return 80003365;
            }
            case 1141002: {
                return 0x111B1C;
            }
            case 1241004: {
                return 1221021;
            }
            case 1341001: {
                return 1321012;
            }
            case 1341003: {
                return 1321052;
            }
            case 2141000: {
                return 2121006;
            }
            case 2141003: {
                return 2121011;
            }
            case 2141005: {
                return 2121003;
            }
            case 2241000: {
                return 2221006;
            }
            case 2241002: {
                return 2221012;
            }
            case 2241003: {
                return 2221007;
            }
            case 2241004: {
                return 2220014;
            }
            case 2341000: {
                return 2321007;
            }
            case 2341002: {
                return 2321001;
            }
            case 2341004: {
                return 2311015;
            }
            case 3141000: {
                return 3121020;
            }
            case 3141004: {
                return 3111013;
            }
            case 3241000: {
                return 3221007;
            }
            case 3241001: {
                return 3221025;
            }
            case 3241002: {
                return 3221026;
            }
            case 3341001: {
                return 3321005;
            }
            case 4241007: {
                return 4241006;
            }
            case 4361001: {
                return 4341052;
            }
            case 5141009: {
                return 5121013;
            }
            case 5141501: 
            case 5141502: 
            case 5141503: 
            case 5141504: 
            case 5141505: 
            case 5141506: {
                return 5141500;
            }
            case 5241002: {
                return 5221022;
            }
            case 5241003: {
                return 5241002;
            }
            case 5341005: {
                return 5321003;
            }
            case 5341006: {
                return 5321001;
            }
            case 11141002: {
                return 11111029;
            }
            case 21141001: 
            case 21141002: 
            case 21141003: {
                return 21141000;
            }
            case 21141501: 
            case 21141502: 
            case 21141503: 
            case 21141504: 
            case 21141505: 
            case 21141506: {
                return 21141500;
            }
            case 23141003: {
                return 23121052;
            }
            case 23141005: {
                return 23121002;
            }
            case 24141000: {
                return 24121005;
            }
            case 25141504: {
                return 400051043;
            }
            case 27141000: {
                return 27121303;
            }
            case 31141000: {
                return 31121001;
            }
            case 31141003: 
            case 31141004: 
            case 31141005: {
                return 31141002;
            }
            case 31241000: {
                return 31221001;
            }
            case 31241001: {
                return 31241000;
            }
            case 32141000: {
                return 32001014;
            }
            case 32141002: {
                return 32121052;
            }
            case 32141003: {
                return 32120055;
            }
            case 33141006: {
                return 33001016;
            }
            case 33141007: {
                return 33001025;
            }
            case 33141008: {
                return 33101115;
            }
            case 33141010: {
                return 33111015;
            }
            case 33141011: {
                return 33121017;
            }
            case 33141012: 
            case 33141013: {
                return 33121155;
            }
            case 37141000: {
                return 37001000;
            }
            case 37141001: {
                return 37101000;
            }
            case 63141000: {
                return 63121002;
            }
            case 63141004: {
                return 63121006;
            }
            case 63141007: {
                return 63001000;
            }
            case 63141010: {
                return 63111007;
            }
            case 63141011: {
                return 63121004;
            }
            case 63141100: {
                return 63121102;
            }
            case 63141107: {
                return 63101100;
            }
            case 63141109: {
                return 63101104;
            }
            case 101141000: {
                return 101121100;
            }
            case 101141014: {
                return 101111100;
            }
            case 101141017: {
                return 101111200;
            }
            case 142141000: {
                return 142001002;
            }
            case 151141002: {
                return 151001001;
            }
            case 151141003: {
                return 151101013;
            }
            case 151141004: {
                return 151111003;
            }
            case 151141005: {
                return 151121002;
            }
            case 154141003: {
                return 154121009;
            }
            case 154141008: {
                return 154121003;
            }
            case 154141001: {
                return 154101001;
            }
            case 154141002: {
                return 154101002;
            }
            case 154141009: {
                return 154121001;
            }
            case 155141000: {
                return 155001100;
            }
            case 155141004: {
                return 155101100;
            }
            case 155141005: {
                return 155101101;
            }
            case 155141006: {
                return 155101112;
            }
            case 155141007: {
                return 155101013;
            }
            case 155141008: {
                return 155101015;
            }
            case 155141011: {
                return 155111102;
            }
            case 155141012: {
                return 155111111;
            }
            case 155141016: {
                return 155121102;
            }
            case 155141017: {
                return 155121002;
            }
            case 155141021: {
                return 155101200;
            }
            case 155141022: {
                return 155101201;
            }
            case 155141024: {
                return 155111202;
            }
            case 155141025: {
                return 155111211;
            }
            case 155141026: {
                return 155111212;
            }
            case 155141027: {
                return 155121202;
            }
            case 155141028: {
                return 155121215;
            }
            case 164141000: {
                return 164121000;
            }
            case 164141005: {
                return 164111003;
            }
            case 164141011: {
                return 164121003;
            }
            case 164141030: {
                return 164001000;
            }
            case 164141033: {
                return 164101000;
            }
            case 164141035: {
                return 164111000;
            }
            case 5221027: {
                return 5221022;
            }
            case 400051097: {
                return 400051046;
            }
            case 400011024: 
            case 400011025: {
                return 400011015;
            }
            case 400041085: 
            case 400041086: {
                return 400041084;
            }
            case 2221055: {
                return 2221054;
            }
            case 400001064: {
                return 400001036;
            }
            case 162101006: 
            case 162101007: {
                return 162100005;
            }
            case 162101003: 
            case 162101004: {
                return 162100002;
            }
            case 162121044: {
                return 162121043;
            }
            case 400021131: {
                return 400021130;
            }
            case 162121003: 
            case 162121004: {
                return 162120002;
            }
            case 162121006: 
            case 162121007: {
                return 162120005;
            }
            case 162121009: 
            case 162121010: {
                return 162120008;
            }
            case 162111010: {
                return 162111002;
            }
            case 162101009: 
            case 162101010: 
            case 162101011: {
                return 162100008;
            }
            case 162121012: 
            case 162121013: 
            case 162121014: 
            case 162121015: 
            case 162121016: 
            case 162121017: 
            case 162121018: 
            case 162121019: {
                return 162120011;
            }
            case 135001004: 
            case 135003003: 
            case 135003004: {
                return 135001003;
            }
            case 400041023: 
            case 400041024: 
            case 400041080: {
                return 400041022;
            }
            case 400001052: {
                return 400001007;
            }
            case 131002025: {
                return 131001025;
            }
            case 131002026: 
            case 131003026: {
                return 131001026;
            }
            case 131002015: {
                return 131001015;
            }
            case 131002023: 
            case 131003023: 
            case 131004023: 
            case 131005023: 
            case 131006023: {
                return 131001023;
            }
            case 131002022: 
            case 131003022: 
            case 131004022: 
            case 131005022: 
            case 131006022: {
                return 131001022;
            }
            case 131001113: 
            case 131001213: 
            case 131001313: {
                return 131001013;
            }
            case 132001017: 
            case 133001017: {
                return 131001017;
            }
            case 63001003: 
            case 63001005: {
                return 63001002;
            }
            case 135002018: {
                return 135001018;
            }
            case 400001060: {
                return 400001059;
            }
            case 14001031: {
                return 14001023;
            }
            case 155121004: {
                return 155121102;
            }
            case 400011065: {
                return 400011055;
            }
            case 400011092: 
            case 400011093: 
            case 400011094: 
            case 400011095: 
            case 400011096: 
            case 400011097: 
            case 400011103: {
                return 400011091;
            }
            case 37120055: 
            case 37120056: 
            case 37120057: 
            case 37120058: 
            case 37120059: {
                return 37121052;
            }
            case 37110001: 
            case 37110002: {
                return 37111000;
            }
            case 14001030: {
                return 14001026;
            }
            case 2121055: {
                return 2121052;
            }
            case 23111009: 
            case 23111010: 
            case 23111011: {
                return 23111008;
            }
            case 400001011: {
                return 400001010;
            }
            case 400041056: {
                return 400041055;
            }
            case 400021100: 
            case 400021111: {
                return 400021099;
            }
            case 400041058: {
                return 400041057;
            }
            case 400011135: {
                return 400011134;
            }
            case 400021097: 
            case 400021098: 
            case 400021104: {
                return 400021096;
            }
            case 400011119: 
            case 400011120: {
                return 400011118;
            }
            case 400051069: {
                return 400051068;
            }
            case 400011111: {
                return 400011110;
            }
            case 400021088: 
            case 400021089: {
                return 400021087;
            }
            case 400011113: 
            case 400011114: 
            case 400011115: 
            case 400011129: {
                return 400011112;
            }
            case 400021112: {
                return 400021094;
            }
            case 400031045: {
                return 400031044;
            }
            case 400011085: {
                return 400011047;
            }
            case 400051079: {
                return 400051078;
            }
            case 400051075: 
            case 400051076: 
            case 400051077: {
                return 400051074;
            }
            case 400011132: {
                return 400011131;
            }
            case 400011122: {
                return 400011121;
            }
            case 400031059: {
                return 400031058;
            }
            case 400041060: {
                return 400041059;
            }
            case 400051059: 
            case 400051060: 
            case 400051061: 
            case 400051062: 
            case 400051063: 
            case 400051064: 
            case 400051065: 
            case 400051066: 
            case 400051067: {
                return 400051058;
            }
            case 400031047: 
            case 400031048: 
            case 400031049: 
            case 400031050: 
            case 400031051: {
                return 400031057;
            }
            case 400051071: {
                return 400051070;
            }
            case 400041070: 
            case 400041071: 
            case 400041072: 
            case 400041073: {
                return 400041069;
            }
            case 400041076: 
            case 400041077: 
            case 400041078: {
                return 400041075;
            }
            case 400041062: 
            case 400041079: {
                return 400041061;
            }
            case 5120021: {
                return 5121013;
            }
            case 25111211: {
                return 25111209;
            }
            case 400031031: {
                return 400031030;
            }
            case 400031054: {
                return 400031053;
            }
            case 400031056: {
                return 400031055;
            }
            case 30001078: 
            case 30001079: 
            case 30001080: {
                return 30001068;
            }
            case 61121026: {
                return 61121102;
            }
            case 400001040: 
            case 400001041: {
                return 400001039;
            }
            case 400041051: {
                return 400041050;
            }
            case 400001044: {
                return 400001043;
            }
            case 151101004: 
            case 151101010: {
                return 151101003;
            }
            case 131001001: 
            case 131001002: 
            case 131001003: {
                return 131001000;
            }
            case 131001106: 
            case 131001206: 
            case 131001306: 
            case 131001406: 
            case 131001506: {
                return 131001006;
            }
            case 131001107: 
            case 131001207: 
            case 131001307: {
                return 131001007;
            }
            case 24121010: {
                return 24121003;
            }
            case 24111008: {
                return 24111006;
            }
            case 151101007: 
            case 151101008: {
                return 151101006;
            }
            case 142120001: {
                return 142120000;
            }
            case 142110003: {
                return 142111002;
            }
            case 400041049: {
                return 400041048;
            }
            case 400041053: {
                return 400041052;
            }
            case 37000009: {
                return 37001001;
            }
            case 37100008: {
                return 37100007;
            }
            case 151001003: {
                return 151001002;
            }
            case 400001051: 
            case 400001053: 
            case 400001054: 
            case 400001055: {
                return 400001050;
            }
            case 95001000: {
                return 3111013;
            }
            case 95001016: {
                return 3141004;
            }
            case 400031018: 
            case 400031019: {
                return 400031017;
            }
            case 164111016: {
                return 164111003;
            }
            case 164111001: 
            case 164111002: 
            case 164111009: 
            case 164111010: 
            case 164111011: {
                return 164110000;
            }
            case 400001047: 
            case 400001048: 
            case 400001049: {
                return 400001046;
            }
            case 164001002: {
                return 164001001;
            }
            case 151121011: {
                return 151121004;
            }
            case 164101001: 
            case 164101002: {
                return 164100000;
            }
            case 164101004: {
                return 164101003;
            }
            case 164121001: 
            case 164121002: 
            case 164121014: {
                return 164120000;
            }
            case 164121004: {
                return 164121003;
            }
            case 164121015: {
                return 164121008;
            }
            case 164120007: {
                return 164121007;
            }
            case 164121044: {
                return 164121043;
            }
            case 164121011: 
            case 164121012: {
                return 164121006;
            }
            case 164111004: 
            case 164111005: 
            case 164111006: {
                return 164111003;
            }
            case 400031035: {
                return 400031034;
            }
            case 400031038: 
            case 400031039: 
            case 400031040: 
            case 400031041: 
            case 400031042: 
            case 400031043: {
                return 400031037;
            }
            case 31011004: 
            case 31011005: 
            case 31011006: 
            case 31011007: {
                return 31011000;
            }
            case 31201007: 
            case 31201008: 
            case 31201009: 
            case 31201010: {
                return 31201000;
            }
            case 31211007: 
            case 31211008: 
            case 31211009: 
            case 31211010: {
                return 31211000;
            }
            case 31221009: 
            case 31221010: 
            case 31221011: 
            case 31221012: {
                return 31221000;
            }
            case 3311011: {
                return 3311010;
            }
            case 3011006: 
            case 3011007: 
            case 3011008: {
                return 3011005;
            }
            case 3301009: {
                return 3301008;
            }
            case 3301004: {
                return 3301003;
            }
            case 3321003: 
            case 3321004: 
            case 3321005: 
            case 3321006: 
            case 3321007: {
                return 3320002;
            }
            case 3321036: 
            case 3321037: 
            case 3321038: 
            case 3321039: 
            case 3321040: {
                return 3321035;
            }
            case 3321016: 
            case 3321017: 
            case 3321018: 
            case 3321019: 
            case 3321020: 
            case 3321021: {
                return 3321014;
            }
            case 21000004: {
                return 21001009;
            }
            case 142100010: {
                return 142101009;
            }
            case 142100008: {
                return 142101002;
            }
            case 27120211: {
                return 27121201;
            }
            case 33121255: {
                return 33121155;
            }
            case 33101115: {
                return 33101215;
            }
            case 37000005: {
                return 37001004;
            }
            case 400011074: 
            case 400011075: 
            case 400011076: {
                return 400011073;
            }
            case 33001202: {
                return 33001102;
            }
            case 152000009: {
                return 152000007;
            }
            case 152001005: {
                return 152001004;
            }
            case 152120002: {
                return 152120001;
            }
            case 152101000: 
            case 152101004: {
                return 152101003;
            }
            case 152121006: {
                return 152121005;
            }
            case 400051019: 
            case 400051020: {
                return 400051018;
            }
            case 152110004: 
            case 152120016: 
            case 152120017: {
                return 152001001;
            }
            case 400021064: 
            case 400021065: {
                return 400021063;
            }
            case 1100012: {
                return 1101012;
            }
            case 1111014: {
                return 1111008;
            }
            case 2100010: {
                return 2101010;
            }
            case 61111114: 
            case 61111221: {
                return 61111008;
            }
            case 14121055: 
            case 14121056: {
                return 14121054;
            }
            case 61121220: {
                return 61121015;
            }
            case 400031008: 
            case 400031009: {
                return 400031007;
            }
            case 142120030: {
                return 142121030;
            }
            case 400051039: 
            case 400051052: 
            case 400051053: {
                return 400051038;
            }
            case 400021043: 
            case 400021044: 
            case 400021045: {
                return 400021042;
            }
            case 400051049: 
            case 400051050: {
                return 400051040;
            }
            case 400040006: {
                return 400041006;
            }
            case 155001204: {
                return 155001104;
            }
            case 400031026: 
            case 400031027: {
                return 400031025;
            }
            case 61121222: {
                return 61121105;
            }
            case 400020046: 
            case 400020051: 
            case 400021013: 
            case 400021014: 
            case 400021015: 
            case 400021016: {
                return 400021012;
            }
            case 61121116: 
            case 61121124: 
            case 61121221: 
            case 61121223: 
            case 61121225: {
                return 61121104;
            }
            case 400011002: {
                return 400011001;
            }
            case 400010030: {
                return 400011031;
            }
            case 400051051: {
                return 400051041;
            }
            case 400021077: {
                return 400021070;
            }
            case 2120013: {
                return 2121007;
            }
            case 2220014: {
                return 2221007;
            }
            case 32121011: {
                return 32121004;
            }
            case 400011059: 
            case 400011060: 
            case 400011061: {
                return 400011058;
            }
            case 400021075: 
            case 400021076: {
                return 400021074;
            }
            case 400011033: 
            case 400011034: 
            case 400011035: 
            case 400011036: 
            case 400011037: 
            case 400011067: {
                return 400011032;
            }
            case 400011080: 
            case 400011081: 
            case 400011082: {
                return 400011079;
            }
            case 400011084: {
                return 400011083;
            }
            case 21120026: {
                return 21120019;
            }
            case 400020009: 
            case 400020010: 
            case 400020011: 
            case 400021010: 
            case 400021011: {
                return 400021008;
            }
            case 400041026: 
            case 400041027: {
                return 400041025;
            }
            case 400040008: 
            case 400041019: {
                return 400041008;
            }
            case 400041003: 
            case 400041004: 
            case 400041005: {
                return 400041002;
            }
            case 400051045: {
                return 400051044;
            }
            case 400011078: {
                return 400011077;
            }
            case 400031016: {
                return 400031015;
            }
            case 400031013: 
            case 400031014: {
                return 400031012;
            }
            case 400011102: {
                return 400011090;
            }
            case 400020002: {
                return 400021002;
            }
            case 22140023: {
                return 22140014;
            }
            case 22140024: {
                return 22140015;
            }
            case 22141012: {
                return 22140022;
            }
            case 22110014: 
            case 22110025: {
                return 22110014;
            }
            case 22170061: {
                return 22170060;
            }
            case 22170093: {
                return 22170064;
            }
            case 22171083: {
                return 22171080;
            }
            case 22170094: {
                return 22170065;
            }
            case 400011069: {
                return 400011068;
            }
            case 400031033: {
                return 400031032;
            }
            case 25121133: {
                return 25121131;
            }
            case 23121015: {
                return 23121014;
            }
            case 24120055: {
                return 24121052;
            }
            case 31221014: {
                return 31221001;
            }
            case 400021031: 
            case 400021040: {
                return 400021030;
            }
            case 0x3EDDD3: {
                return 4100012;
            }
            case 37000010: {
                return 37001001;
            }
            case 155001000: {
                return 155001001;
            }
            case 155001009: {
                return 155001104;
            }
            case 155100009: {
                return 155101008;
            }
            case 155101002: {
                return 155101003;
            }
            case 155101013: 
            case 155101015: 
            case 155101101: 
            case 155101112: {
                return 155101100;
            }
            case 155101114: {
                return 155101104;
            }
            case 155101214: {
                return 155101204;
            }
            case 155101201: 
            case 155101212: {
                return 155101200;
            }
            case 155111002: 
            case 155111111: {
                return 155111102;
            }
            case 155111103: 
            case 155111104: {
                return 155111105;
            }
            case 155111106: {
                return 155111102;
            }
            case 155111211: 
            case 155111212: {
                return 155111202;
            }
            case 155121002: {
                return 155121102;
            }
            case 155121003: {
                return 155121005;
            }
            case 155121006: 
            case 155121007: {
                return 155121306;
            }
            case 155121215: {
                return 155121202;
            }
            case 400041010: 
            case 400041011: 
            case 400041012: 
            case 400041013: 
            case 400041014: 
            case 400041015: {
                return 400041009;
            }
            case 400011099: {
                return 400011098;
            }
            case 400011101: {
                return 400011100;
            }
            case 400011053: {
                return 400011052;
            }
            case 400001016: {
                return 400001013;
            }
            case 400021029: {
                return 400021028;
            }
            case 400030002: {
                return 400031002;
            }
            case 400021049: 
            case 400021050: {
                return 400021041;
            }
            case 14000027: 
            case 14000028: 
            case 14000029: {
                return 14001027;
            }
            case 4100012: {
                return 0x3E9393;
            }
            case 5211015: 
            case 5211016: {
                return 5211011;
            }
            case 5220023: 
            case 5220024: 
            case 5220025: {
                return 5221022;
            }
            case 51001006: 
            case 51001007: 
            case 51001008: 
            case 51001009: 
            case 51001010: 
            case 51001011: 
            case 51001012: 
            case 51001013: {
                return 51001005;
            }
            case 51141003: 
            case 51141004: 
            case 51141005: 
            case 51141006: 
            case 51141007: 
            case 51141008: 
            case 51141009: 
            case 51141010: 
            case 51141011: 
            case 51141012: {
                return 51141002;
            }
            case 25120115: {
                return 25120110;
            }
            case 5201005: {
                return 5201011;
            }
            case 5320011: {
                return 5321004;
            }
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                return 33001007;
            }
            case 65120011: {
                return 65121011;
            }
            case 400041034: {
                return 400041033;
            }
            case 400041036: {
                return 400041035;
            }
            case 21110027: 
            case 21110028: 
            case 21111021: {
                return 21110020;
            }
            case 100000276: 
            case 100000277: {
                return 100000267;
            }
            case 400001025: 
            case 400001026: 
            case 400001027: 
            case 400001028: 
            case 400001029: 
            case 400001030: {
                return 400001024;
            }
            case 400001015: {
                return 400001014;
            }
            case 400011013: 
            case 400011014: {
                return 400011012;
            }
            case 400001022: {
                return 400001019;
            }
            case 400021033: 
            case 400021052: {
                return 400021032;
            }
            case 400041016: {
                return 4001344;
            }
            case 400041017: {
                return 4111010;
            }
            case 400041018: {
                return 4121013;
            }
            case 400051003: 
            case 400051004: 
            case 400051005: {
                return 400051002;
            }
            case 400051025: 
            case 400051026: {
                return 400051024;
            }
            case 400051023: {
                return 400051022;
            }
            case 2321055: {
                return 2321052;
            }
            case 5121055: {
                return 5121052;
            }
            case 61111220: {
                return 61111002;
            }
            case 36121013: 
            case 36121014: 
            case 0x22777CC: 
            case 36141005: 
            case 36141006: {
                return 36121002;
            }
            case 36121011: 
            case 36121012: {
                return 36121001;
            }
            case 400010010: {
                return 400011010;
            }
            case 10001253: 
            case 10001254: 
            case 14001026: {
                return 10000252;
            }
            case 142000006: {
                return 142001004;
            }
            case 4321001: {
                return 4321000;
            }
            case 33101006: 
            case 33101007: {
                return 33101005;
            }
            case 33101008: {
                return 33101004;
            }
            case 35101009: 
            case 35101010: {
                return 35100008;
            }
            case 35111009: 
            case 35111010: {
                return 35111001;
            }
            case 35121013: {
                return 35111005;
            }
            case 35121011: {
                return 35121009;
            }
            case 3000008: 
            case 3000009: 
            case 3000010: {
                return 3001007;
            }
            case 32001007: 
            case 32001008: 
            case 32001009: 
            case 32001010: 
            case 32001011: {
                return 32001001;
            }
            case 64001007: 
            case 64001008: 
            case 64001009: 
            case 64001010: 
            case 64001011: 
            case 64001012: {
                return 64001000;
            }
            case 64001013: {
                return 64001002;
            }
            case 64100001: {
                return 64100000;
            }
            case 64001006: {
                return 64001001;
            }
            case 64101008: {
                return 64101002;
            }
            case 64111012: {
                return 64111004;
            }
            case 64121012: 
            case 64121013: 
            case 64121014: 
            case 64121015: 
            case 64121017: 
            case 64121018: 
            case 64121019: {
                return 64121001;
            }
            case 64121022: 
            case 64121023: 
            case 64121024: {
                return 64121021;
            }
            case 64121016: {
                return 64121003;
            }
            case 64121055: {
                return 64121053;
            }
            case 5300007: {
                return 5301001;
            }
            case 23101007: {
                return 23101001;
            }
            case 31001006: 
            case 31001007: 
            case 31001008: {
                return 31000004;
            }
            case 30010183: 
            case 30010184: 
            case 30010186: {
                return 30010110;
            }
            case 25000001: {
                return 25001000;
            }
            case 25000003: {
                return 25001002;
            }
            case 25100001: 
            case 25100002: {
                return 25101000;
            }
            case 25100010: {
                return 25100009;
            }
            case 25110001: 
            case 25110002: 
            case 25110003: {
                return 25111000;
            }
            case 25120001: 
            case 25120002: 
            case 25120003: {
                return 25121000;
            }
            case 101000102: {
                return 101000101;
            }
            case 101000202: {
                return 101000201;
            }
            case 0x606AAAA: {
                return 101100201;
            }
            case 101110201: 
            case 101110204: {
                return 101110203;
            }
            case 101120101: {
                return 101120100;
            }
            case 101120103: {
                return 101120102;
            }
            case 101120105: 
            case 101120106: {
                return 101120104;
            }
            case 101120203: {
                return 101120202;
            }
            case 400031021: {
                return 400031020;
            }
            case 101120205: 
            case 101120206: {
                return 101120204;
            }
            case 101120200: 
            case 101141006: {
                return 101121200;
            }
            case 100001266: 
            case 0x5F5E5F5: {
                return 100001265;
            }
            case 1111002: {
                return 1101013;
            }
            case 3120019: {
                return 3111009;
            }
            case 5201013: 
            case 5201014: {
                return 5201012;
            }
            case 5210016: 
            case 5210017: 
            case 5210018: {
                return 5210015;
            }
            case 11121055: {
                return 11121052;
            }
            case 12120011: {
                return 12121001;
            }
            case 12121055: {
                return 12121054;
            }
            case 12120013: 
            case 12120014: {
                return 12121004;
            }
            case 14101029: {
                return 14101028;
            }
            case 61110211: 
            case 61120007: 
            case 61121217: {
                return 61101002;
            }
            case 61120008: {
                return 61111008;
            }
            case 61121201: {
                return 61121100;
            }
            case 65111007: {
                return 65111100;
            }
            case 36111009: 
            case 36111010: {
                return 36111000;
            }
        }
        if (skillId == 155101204) {
            return 155101104;
        }
        return skillId;
    }

    public static boolean is超越攻擊(int skillId) {
        switch (skillId) {
            case 31011000: 
            case 31011004: 
            case 31011005: 
            case 31011006: 
            case 31011007: 
            case 31201000: 
            case 31201007: 
            case 31201008: 
            case 31201009: 
            case 31201010: 
            case 31211000: 
            case 31211007: 
            case 31211008: 
            case 31211009: 
            case 31211010: 
            case 31221000: 
            case 31221009: 
            case 31221010: 
            case 31221011: 
            case 31221012: {
                return true;
            }
        }
        return false;
    }

    public static int getMPEaterForJob(int job) {
        switch (job) {
            case 210: 
            case 211: 
            case 212: {
                return 0x200B20;
            }
            case 220: 
            case 221: 
            case 222: {
                return 2200000;
            }
            case 230: 
            case 231: 
            case 232: {
                return 2300000;
            }
        }
        return 0x200B20;
    }

    public static boolean isPyramidSkill(int skill) {
        return JobConstants.is零轉職業(skill / 10000) && skill % 10000 == 1020;
    }

    public static boolean isInflationSkill(int skill) {
        return JobConstants.is零轉職業(skill / 10000) && skill % 10000 >= 1092 && skill % 10000 <= 1095;
    }

    public static boolean isMulungSkill(int skill) {
        return JobConstants.is零轉職業(skill / 10000) && (skill % 10000 == 1009 || skill % 10000 == 1010 || skill % 10000 == 1011);
    }

    public static boolean isIceKnightSkill(int skill) {
        return JobConstants.is零轉職業(skill / 10000) && (skill % 10000 == 1098 || skill % 10000 == 99 || skill % 10000 == 100 || skill % 10000 == 103 || skill % 10000 == 104 || skill % 10000 == 1105);
    }

    public static boolean is騎乘技能(int skill) {
        return JobConstants.is零轉職業(skill / 10000) && skill % 10000 == 1004;
    }

    public static int getAttackDelay(int skillId, Skill skill) {
        switch (skillId) {
            case 2111003: {
                return 0;
            }
            case 1311011: 
            case 3111009: 
            case 3111013: 
            case 5221022: 
            case 5221027: 
            case 0x4FAAA4: 
            case 5221029: 
            case 23121000: 
            case 31201001: 
            case 33121009: 
            case 35111004: 
            case 35121005: 
            case 35121013: {
                return 40;
            }
            case 4111010: {
                return 99;
            }
            case 13111020: 
            case 13121001: {
                return 120;
            }
            case 4100012: 
            case 0x3EDDD3: 
            case 24100003: 
            case 24120002: 
            case 24121000: 
            case 24121005: 
            case 27121201: 
            case 36001005: 
            case 61101002: 
            case 61110211: 
            case 61120007: 
            case 61121217: 
            case 400041082: 
            case 400041087: {
                return 30;
            }
            case 2221012: 
            case 32121003: {
                return 180;
            }
            case 27120211: 
            case 61111100: 
            case 61111113: {
                return 210;
            }
            case 21110007: 
            case 21110008: 
            case 21120009: 
            case 21120010: {
                return 390;
            }
            case 31001006: 
            case 31001007: 
            case 31001008: {
                return 270;
            }
            case 31121005: {
                return 510;
            }
            case 61001000: 
            case 61001004: 
            case 61001005: {
                return 240;
            }
            case 60011216: 
            case 65121003: {
                return 180;
            }
            case 36121000: {
                return 120;
            }
            case 0: {
                return 330;
            }
        }
        if (skill != null && skill.getSkillType() == 3) {
            return 0;
        }
        if (skill != null && skill.getDelay() > 0 && skillId != 21101003 && skillId != 33101004 && skillId != 32111010 && skillId != 2111007 && skillId != 2211007 && skillId != 2311007 && skillId != 22161005 && skillId != 35121003 && skillId != 22150004 && skillId != 22181004) {
            return skill.getDelay();
        }
        return 330;
    }

    public static boolean isAdminSkill(int skillId) {
        int jobId = skillId / 10000;
        return jobId == 800 || jobId == 900;
    }

    public static boolean isSpecialSkill(int skillId) {
        int jobId = skillId / 10000;
        return jobId == 7000 || jobId == 7100 || jobId == 8000 || jobId == 9000 || jobId == 9100 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
    }

    public static boolean isApplicableSkill(int skillId) {
        return (skillId < 80000000 || skillId >= 100000000) && (skillId % 10000 < 8000 || skillId % 10000 > 8006) && !SkillConstants.is天使祝福戒指(skillId) || skillId >= 92000000 || skillId >= 80000000 && skillId < 80020000;
    }

    public static boolean isApplicableSkill_(int skillId) {
        for (int i : PlayerStats.pvpSkills) {
            if (skillId != i) continue;
            return true;
        }
        return skillId >= 90000000 && skillId < 92000000 || skillId % 10000 >= 8000 && skillId % 10000 <= 8003 || SkillConstants.is天使祝福戒指(skillId);
    }

    public static boolean isNoDelaySkill(int skillId) {
        switch (skillId) {
            case 2111007: 
            case 2211007: 
            case 2221012: 
            case 2311007: 
            case 4221052: 
            case 4341052: 
            case 21000014: 
            case 22000015: 
            case 22110022: 
            case 22110023: 
            case 22110025: 
            case 22111012: 
            case 22170072: 
            case 25101009: 
            case 31121005: 
            case 32001014: 
            case 32100010: 
            case 32110017: 
            case 32111016: 
            case 32120019: 
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: 
            case 33111013: 
            case 33121016: 
            case 35001002: 
            case 35111003: 
            case 35121003: 
            case 61101002: 
            case 61120007: 
            case 80011133: 
            case 142101009: 
            case 142121005: 
            case 142121030: {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoApplyAttack(int skillId) {
        switch (skillId) {
            case 2301002: 
            case 80002890: 
            case 162101012: 
            case 175111004: 
            case 400011109: 
            case 400011136: {
                return true;
            }
        }
        return false;
    }

    public static boolean is召喚獸戒指(int skillID) {
        switch (skillID) {
            case 1085: 
            case 1087: 
            case 80000052: 
            case 80000053: 
            case 80000054: 
            case 80000086: 
            case 80000155: 
            case 80001154: 
            case 80001262: 
            case 80001518: 
            case 80001519: 
            case 80001520: 
            case 80001521: 
            case 80001522: 
            case 80001523: 
            case 80001524: 
            case 80001525: 
            case 80001526: 
            case 80001527: 
            case 80001528: 
            case 80001529: 
            case 80001530: 
            case 80001531: 
            case 80010067: 
            case 80010068: 
            case 80010069: 
            case 80010070: 
            case 80010071: 
            case 80010072: 
            case 80010075: 
            case 80010076: 
            case 80010077: 
            case 80010078: 
            case 80010079: 
            case 80010080: 
            case 80011103: 
            case 80011104: 
            case 80011105: 
            case 80011106: 
            case 80011107: 
            case 80011108: {
                return true;
            }
        }
        return SkillConstants.dZ(SkillConstants.getSkillRoot(skillID)) && (skillID % 10000 == 1085 || skillID % 10000 == 1087 || skillID % 10000 == 1090 || skillID % 10000 == 1179);
    }

    public static boolean is天使祝福戒指(int skillId) {
        return JobConstants.is零轉職業(skillId / 10000) && (skillId % 10000 == 1085 || skillId % 10000 == 1087 || skillId % 10000 == 1090 || skillId % 10000 == 1179);
    }

    public static boolean is天氣戒指(int skillId) {
        return JobConstants.is零轉職業(skillId / 10000) && skillId / 10000 == 8001 && skillId % 10000 >= 67 && skillId % 10000 <= 80;
    }

    public static int getCardSkillLevel(int level) {
        if (level >= 60 && level < 100) {
            return 2;
        }
        if (level >= 100 && level < 200) {
            return 3;
        }
        if (level >= 200) {
            return 4;
        }
        return 1;
    }

    public static int getLuminousSkillMode(int skillId) {
        switch (skillId) {
            case 27001100: 
            case 27101100: 
            case 27101101: 
            case 27111100: 
            case 27111101: 
            case 27121100: {
                return 20040216;
            }
            case 27001201: 
            case 27101202: 
            case 27111202: 
            case 27120211: 
            case 27121201: 
            case 27121202: {
                return 20040217;
            }
            case 27111303: 
            case 27121303: {
                return 20040219;
            }
        }
        return -1;
    }

    public static int getSoulMasterAttackMode(int skillid) {
        switch (skillid) {
            case 11001226: 
            case 11100228: 
            case 11110228: 
            case 11111230: 
            case 11120217: 
            case 0xAA0050: {
                return 1;
            }
            case 11001126: 
            case 11100128: 
            case 11110128: 
            case 11111130: 
            case 11120117: 
            case 11141100: {
                return 2;
            }
        }
        return -1;
    }

    public static boolean isShowForgenBuff(SecondaryStat buff) {
        switch (buff) {
            case CoalitionSupportSoldierStorm: 
            case IndieMDF: 
            case DarkSight: 
            case SoulArrow: 
            case Stun: 
            case Poison: 
            case Seal: 
            case Darkness: 
            case ComboCounter: 
            case BlessedHammer: 
            case BlessedHammerActive: 
            case WeaponCharge: 
            case ShadowPartner: 
            case Weakness: 
            case Curse: 
            case Slow: 
            case Morph: 
            case Stance: 
            case Attract: 
            case NoBulletConsume: 
            case BanMap: 
            case Ghost: 
            case Barrier: 
            case ReverseInput: 
            case RespectPImmune: 
            case RespectMImmune: 
            case DefenseState: 
            case DojangBerserk: 
            case DojangInvincible: 
            case DojangShield: 
            case WindBreakerFinal: 
            case HideAttack: 
            case RepeatEffect: 
            case StopPortion: 
            case StopMotion: 
            case Fear: 
            case HiddenPieceOn: 
            case MagicShield: 
            case Flying: 
            case Frozen: 
            case DrawBack: 
            case NotDamaged: 
            case FinalCut: 
            case Dance: 
            case Sneak: 
            case Mechanic: 
            case BlessingArmor: 
            case Beholder: 
            case Inflation: 
            case Web: 
            case DisOrder: 
            case Thread: 
            case Team: 
            case Explosion: 
            case PvPRaceEffect: 
            case WeaknessMdamage: 
            case Frozen2: 
            case Shock: 
            case HolyMagicShell: 
            case DamAbsorbShield: 
            case DevilishPower: 
            case SpiritLink: 
            case Event: 
            case Lapidification: 
            case PyramidEffect: 
            case KeyDownMoving: 
            case IgnoreTargetDEF: 
            case Invisible: 
            case Judgement: 
            case Magnet: 
            case MagnetArea: 
            case GuidedArrow: 
            case StraightForceAtomTargets: 
            case LefBuffMastery: 
            case TempSecondaryStat: 
            case KeyDownAreaMoving: 
            case Larkness: 
            case StackBuff: 
            case AntiMagicShell: 
            case SmashStack: 
            case ReshuffleSwitch: 
            case StopForceAtomInfo: 
            case SoulGazeCriDamR: 
            case PowerTransferGauge: 
            case AffinitySlug: 
            case MobZoneState: 
            case ComboUnlimited: 
            case SoulExalt: 
            case IgnorePImmune: 
            case IceAura: 
            case FireAura: 
            case VengeanceOfAngel: 
            case HeavensDoor: 
            case BleedingToxin: 
            case IgnoreMobDamR: 
            case Asura: 
            case MegaSmasher: 
            case ReturnTeleport: 
            case CapDebuff: 
            case OverloadCount: 
            case SurplusSupply: 
            case NewFlying: 
            case AmaranthGenerator: 
            case CygnusElementSkill: 
            case StrikerHyperElectric: 
            case Translucence: 
            case PoseType: 
            case CosmicForge: 
            case ElementSoul: 
            case FullSoulMP: 
            case ElementalCharge: 
            case Reincarnation: 
            case NaviFlying: 
            case QuiverCatridge: 
            case UserControlMob: 
            case ImmuneBarrier: 
            case ZeroAuraStr: 
            case SpiritGuard: 
            case JaguarSummoned: 
            case BMageAuraYellow: 
            case DarkLighting: 
            case AttackCountX: 
            case FireBarrier: 
            case Frenzy: 
            case ShadowSpear: 
            case MastemaGuard: 
            case BattlePvP_Helena_Mark: 
            case BattlePvP_LangE_Protection: 
            case PinkbeanRollingGrade: 
            case MichaelSoulLink: 
            case MichaelStanceLink: 
            case KinesisPsychicEnergeShield: 
            case Fever: 
            case AdrenalinBoost: 
            case RWVulkanPunch: 
            case RWMagnumBlow: 
            case RWBarrier: 
            case MahaInstall: 
            case TransformOverMan: 
            case EnergyBust: 
            case LightningUnion: 
            case BulletParty: 
            case BishopPray: 
            case Kinesis_DustTornado: 
            case FifthAdvWarriorShield: 
            case FreudBlessing: 
            case OverloadMode: 
            case FifthSpotLight: 
            case OutSide: 
            case LefGloryWing: 
            case ConvertAD: 
            case EtherealForm: 
            case ReadyToDie: 
            case Cr2CriDamR: 
            case HitStackDamR: 
            case BuffControlDebuff: 
            case DispersionDamage: 
            case HarmonyLink: 
            case LefFastCharge: 
            case BattlePvP_Ryude_Frozen: 
            case RepeatEffect2: 
            case AntiEvilShield: 
            case BladeStanceMode: 
            case BladeStancePower: 
            case SelfHyperBodyIncPAD: 
            case SelfHyperBodyMaxHP: 
            case SelfHyperBodyMaxMP: 
            case CriticalBuffAdd: 
            case BossDamageRate: 
            case KenjiCounter: 
            case SkillDeployment: 
            case DashSpeed: 
            case DashJump: 
            case RideVehicle: 
            case PartyBooster: 
            case GuidedBullet: 
            case Undead: 
            case RelicGauge: 
            case RideVehicleExpire: 
            case SecondAtomLockOn: {
                return true;
            }
        }
        return false;
    }

    public static boolean isMovementAffectingStat(SecondaryStat buffStat) {
        switch (buffStat) {
            case DarkSight: 
            case Stun: 
            case Weakness: 
            case Slow: 
            case Morph: 
            case Attract: 
            case Ghost: 
            case Flying: 
            case Frozen: 
            case Dance: 
            case Mechanic: 
            case Frozen2: 
            case Shock: 
            case Lapidification: 
            case KeyDownMoving: 
            case Magnet: 
            case MagnetArea: 
            case SmashStack: 
            case FireAura: 
            case CapDebuff: 
            case NewFlying: 
            case NaviFlying: 
            case UserControlMob: 
            case DashSpeed: 
            case DashJump: 
            case RideVehicle: 
            case Speed: 
            case Jump: 
            case IndieStatR: 
            case IndieSpeed: 
            case IndieJump: 
            case VampDeath: 
            case VampDeathSummon: 
            case GiveMeHeal: 
            case DarkTornado: 
            case SelfWeakness: 
            case BattlePvP_Helena_WindSpirit: 
            case BattlePvP_LeeMalNyun_ScaleUp: 
            case TouchMe: 
            case IndieForceSpeed: 
            case IndieForceJump: {
                return true;
            }
        }
        return false;
    }

    public static boolean isWriteBuffIntValue(SecondaryStat buffStat) {
        return switch (buffStat) {
            case SecondaryStat.ShadowPartner, SecondaryStat.Dance, SecondaryStat.SpiritLink, SecondaryStat.MagnetArea, SecondaryStat.SoulGazeCriDamR, SecondaryStat.PowerTransferGauge, SecondaryStat.MegaSmasher, SecondaryStat.ReturnTeleport, SecondaryStat.NaviFlying, SecondaryStat.QuiverCatridge, SecondaryStat.ImmuneBarrier, SecondaryStat.RWBarrier, SecondaryStat.FifthAdvWarriorShield, SecondaryStat.RideVehicle, SecondaryStat.RideVehicleExpire, SecondaryStat.VampDeath, SecondaryStat.Cyclone, SecondaryStat.CarnivalDefence, SecondaryStat.DojangLuckyBonus, SecondaryStat.BossShield, SecondaryStat.SetBaseDamage, SecondaryStat.DotHealHPPerSecond, SecondaryStat.DotHealMPPerSecond, SecondaryStat.SetBaseDamageByBuff, SecondaryStat.PairingUser -> true;
            default -> false;
        };
    }

    public static boolean isSpecialStackBuff(SecondaryStat buffStat) {
        return switch (buffStat) {
            case SecondaryStat.Curse, SecondaryStat.DashSpeed, SecondaryStat.DashJump, SecondaryStat.RideVehicle, SecondaryStat.PartyBooster, SecondaryStat.GuidedBullet, SecondaryStat.Undead, SecondaryStat.RelicGauge, SecondaryStat.RideVehicleExpire, SecondaryStat.SecondAtomLockOn -> true;
            default -> false;
        };
    }

    public static boolean is美洲豹(int skillId) {
        switch (skillId) {
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                return true;
            }
        }
        return false;
    }

    public static boolean isRuneSkill(int skillid) {
        switch (SkillConstants.getLinkedAttackSkill(skillid)) {
            case 80001428: 
            case 80001430: 
            case 80001432: 
            case 80001752: 
            case 80001753: 
            case 80001754: 
            case 80001755: 
            case 80001757: 
            case 80001762: {
                return true;
            }
        }
        return false;
    }

    public static boolean isGeneralSkill(int skillid) {
        if (skillid == 22171095) {
            return false;
        }
        switch (skillid) {
            case 80001242: 
            case 80001429: 
            case 80001431: 
            case 80001761: 
            case 80001762: 
            case 80011133: {
                return true;
            }
        }
        return skillid % 10000 == 1095 || skillid % 10000 == 1094;
    }

    public static boolean isExtraSkill(int skillid) {
        int group = skillid % 10000;
        switch (group) {
            case 8000: 
            case 8001: 
            case 8002: 
            case 8003: 
            case 8004: 
            case 8005: 
            case 8006: {
                return true;
            }
        }
        return false;
    }

    public static int getCooldownLinkSourceId(int skillId) {
        switch (skillId) {
            case 400011033: 
            case 400011034: 
            case 400011035: 
            case 400011036: 
            case 400011037: {
                return 400011032;
            }
            case 400041026: 
            case 400041027: {
                return 400041025;
            }
            case 400011053: {
                return 400011052;
            }
            case 40011290: {
                return 40011289;
            }
            case 400021115: {
                return 400021114;
            }
            case 3321016: 
            case 3321018: 
            case 3321020: {
                return 3321014;
            }
            case 3321036: 
            case 3321038: 
            case 3321040: {
                return 3321035;
            }
            case 400031038: 
            case 400031039: 
            case 400031040: {
                return 400031037;
            }
            case 400031047: 
            case 400031049: 
            case 400031051: {
                return 400031057;
            }
            case 33121255: {
                return 33121155;
            }
            case 400041056: {
                return 400041055;
            }
            case 400001047: {
                return 400001046;
            }
        }
        return skillId;
    }

    public static byte getLinkSkillslevel(Skill skill, int cid, int defchrlevel) {
        int chrlevel;
        if (skill == null) {
            return 0;
        }
        if (cid > 0 && skill.isLinkSkills()) {
            chrlevel = MapleCharacter.getLevelbyid(cid);
        } else if (skill.isTeachSkills()) {
            chrlevel = defchrlevel;
        } else {
            return 0;
        }
        if (skill.getMaxLevel() == 5 ? chrlevel < 110 : chrlevel < 70) {
            return 0;
        }
        switch (skill.getMaxLevel()) {
            case 1: {
                return 1;
            }
            case 5: {
                if (chrlevel >= 200) {
                    return 5;
                }
                if (chrlevel >= 175) {
                    return 4;
                }
                if (chrlevel >= 150) {
                    return 3;
                }
                if (chrlevel >= 125) {
                    return 2;
                }
                return 1;
            }
        }
        if (chrlevel >= 120) {
            return 2;
        }
        return 1;
    }

    public static boolean isTeachSkills(int id) {
        return TeachSkillMap.containsKey(id);
    }

    public static boolean isLinkSkills(int id) {
        for (int skillId : TeachSkillMap.values()) {
            if (skillId != id) continue;
            return true;
        }
        return false;
    }

    public static int[] getTeamTeachSkills(int skillid) {
        switch (skillid) {
            case 80000055: {
                return new int[]{80000066, 80000067, 0x4C4B444, 80000069, 80000070};
            }
            case 80000329: {
                return new int[]{80000333, 80000334, 80000335, 80000378};
            }
            case 80002758: {
                return new int[]{80002759, 80002760, 80002761};
            }
            case 80002762: {
                return new int[]{80002763, 80002764, 80002765};
            }
            case 80002766: {
                return new int[]{80002767, 80002768, 80002769};
            }
            case 80002770: {
                return new int[]{80002771, 80002772, 80002773};
            }
            case 80002774: {
                return new int[]{80000000, 80002775, 80002776};
            }
        }
        return null;
    }

    public static int getTeamTeachSkillId(int skillid) {
        switch (skillid) {
            case 80000066: 
            case 80000067: 
            case 0x4C4B444: 
            case 80000069: 
            case 80000070: {
                return 80000055;
            }
            case 80000333: 
            case 80000334: 
            case 80000335: 
            case 80000378: {
                return 80000329;
            }
            case 80002759: 
            case 80002760: 
            case 80002761: {
                return 80002758;
            }
            case 80002763: 
            case 80002764: 
            case 80002765: {
                return 80002762;
            }
            case 80002767: 
            case 80002768: 
            case 80002769: {
                return 80002766;
            }
            case 80002771: 
            case 80002772: 
            case 80002773: {
                return 80002770;
            }
            case 80000000: 
            case 80002775: 
            case 80002776: {
                return 80002774;
            }
            case 80000055: 
            case 80000329: 
            case 80002758: 
            case 80002762: 
            case 80002766: 
            case 80002770: 
            case 80002774: {
                return 1;
            }
        }
        return 0;
    }

    public static int getTeachSkillId(int skillid) {
        for (Map.Entry<Integer, Integer> entry : TeachSkillMap.entrySet()) {
            if (entry.getValue() != skillid) continue;
            return entry.getKey();
        }
        return -1;
    }

    public static int getLinkSkillId(int skillid) {
        if (TeachSkillMap.containsKey(skillid)) {
            return TeachSkillMap.get(skillid);
        }
        return -1;
    }

    public static int getStolenHyperSkillColltime(int skillId) {
        switch (skillId) {
            case 0x111B1E: {
                return 300;
            }
            case 1221054: {
                return 600;
            }
            case 1321054: {
                return 180;
            }
            case 2121054: {
                return 75;
            }
            case 2221054: {
                return 90;
            }
            case 2321054: {
                return 300;
            }
            case 3121054: {
                return 120;
            }
            case 3221054: {
                return 150;
            }
            case 4121054: {
                return 120;
            }
            case 4221054: {
                return 45;
            }
            case 5121054: {
                return 75;
            }
            case 5221054: {
                return 60;
            }
        }
        return 0;
    }

    public static boolean isAngelRebornSkill(int skillID) {
        switch (skillID) {
            case 65001100: 
            case 65101100: 
            case 65111100: 
            case 65111101: 
            case 65121100: 
            case 65121101: {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> getUnstableMemorySkillsByJob(short job) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2001008);
        switch (job) {
            case 212: {
                list.add(2101004);
                list.add(2101005);
                list.add(2101001);
                list.add(2111002);
                list.add(2111003);
                list.add(2110015);
                list.add(2121006);
                list.add(2121007);
                list.add(2121011);
                list.add(2121004);
                list.add(2121005);
                list.add(2121000);
                list.add(2121008);
                break;
            }
            case 222: {
                list.add(2201008);
                list.add(2201005);
                list.add(2201001);
                list.add(2200012);
                list.add(2211002);
                list.add(2211011);
                list.add(2221006);
                list.add(2221007);
                list.add(2221012);
                list.add(2221004);
                list.add(2221005);
                list.add(2221000);
                list.add(2221008);
                break;
            }
            case 232: {
                list.add(2301002);
                list.add(2301004);
                list.add(2301005);
                list.add(2311004);
                list.add(2311011);
                list.add(2311001);
                list.add(2311003);
                list.add(2321007);
                list.add(2321001);
                list.add(2321006);
                list.add(2321004);
                list.add(2321003);
                list.add(2321005);
                list.add(2321000);
                list.add(2321009);
            }
        }
        return list;
    }

    public static boolean isMoveImpactStatus(MonsterStatus monsterStatus) {
        switch (monsterStatus) {
            case Speed: 
            case Stun: 
            case Freeze: 
            case Seal: 
            case Web: 
            case RiseByToss: {
                return true;
            }
        }
        return false;
    }

    public static boolean isSmiteStatus(MonsterStatus monsterStatus) {
        switch (monsterStatus) {
            case Freeze: 
            case Web: 
            case RiseByToss: 
            case Smite: {
                return true;
            }
        }
        return false;
    }

    public static int getDiceValue(int i, int value, MapleStatEffect effect) {
        int result = 0;
        while (value > 0) {
            int dice = value % 10;
            value /= 10;
            switch (i) {
                case 7: {
                    result += dice == 2 ? effect.getPddR() : 0;
                    break;
                }
                case 0: {
                    result += dice == 3 ? effect.getS() : 0;
                    break;
                }
                case 1: {
                    result += dice == 4 ? effect.getCritical() : 0;
                    break;
                }
                case 11: {
                    result += dice == 5 ? effect.getDamR() : 0;
                    break;
                }
                case 16: {
                    result += dice == 6 ? effect.getExpR() : 0;
                    break;
                }
                case 17: {
                    result += dice == 7 ? effect.getIgnoreMobpdpR() : 0;
                }
            }
        }
        return result;
    }

    public static boolean isRapidAttackSkill(int skillID) {
        Skill skill = SkillFactory.getSkill(skillID);
        return skill != null && skill.isRapidAttack();
    }

    public static int getSkillRoot(int id) {
        int root = id / 10000;
        if (root == 8000) {
            root = id / 100;
        }
        return root;
    }

    public static int dY(int n) {
        if (SkillConstants.dZ(n) || n % 100 == 0 || n == 501 || n == 3101 || n == 508) {
            return 1;
        }
        if (JobConstants.is龍魔導士(n)) {
            switch (n) {
                case 2200: 
                case 2210: {
                    return 1;
                }
                case 2211: 
                case 2212: 
                case 2213: {
                    return 2;
                }
                case 2214: 
                case 2215: 
                case 2216: {
                    return 3;
                }
                case 2217: 
                case 2218: {
                    return 4;
                }
            }
            return 0;
        }
        n = JobConstants.is影武者(n) ? n % 10 / 2 : (n %= 10);
        if (n <= 2) {
            return n + 2;
        }
        return 0;
    }

    public static boolean dZ(int n) {
        boolean b;
        if (n > 6002) {
            if (n == 8001 || n == 13000) {
                return true;
            }
            b = n == 14000 || n == 15000 || n == 15001;
        } else {
            if (n >= 6000) {
                return true;
            }
            if (n <= 4002) {
                return n >= 4001 || n <= 3002 && (n >= 3001 || n >= 2001 && n <= 2005) || n - 40000 > 5 && n % 1000 == 0;
            }
            b = n == 5000;
        }
        return b || n - 40000 > 5 && (n % 1000 == 0 || n / 100 == 8000);
    }

    public static boolean isPassiveAttackSkill(int skillId) {
        switch (skillId) {
            case 1141002: 
            case 2121054: 
            case 2221012: 
            case 3111013: 
            case 4221052: 
            case 4341054: 
            case 0x4DD5D4: 
            case 5121052: 
            case 5220019: 
            case 5301001: 
            case 12120011: 
            case 12121001: 
            case 13121055: 
            case 14101028: 
            case 14101029: 
            case 27120211: 
            case 27121201: 
            case 31101002: 
            case 31121005: 
            case 31221014: 
            case 32111016: 
            case 37000007: 
            case 61111100: 
            case 65111007: 
            case 80002890: 
            case 101121200: 
            case 151111003: 
            case 152001001: 
            case 152110002: 
            case 152120001: 
            case 152120002: 
            case 152120017: 
            case 400001018: 
            case 400011004: 
            case 400011052: 
            case 400011053: 
            case 400011055: 
            case 400011102: 
            case 400011108: 
            case 400011124: 
            case 400011125: 
            case 400011126: 
            case 400021004: 
            case 400021017: 
            case 400021018: 
            case 400021028: 
            case 400021029: 
            case 400031000: 
            case 400031003: 
            case 400031004: 
            case 400031020: 
            case 400031021: 
            case 400031022: 
            case 400041002: 
            case 400041003: 
            case 400041004: 
            case 400041005: 
            case 400041009: 
            case 400041010: 
            case 400041020: 
            case 400041021: 
            case 400041038: 
            case 400041039: 
            case 400051007: 
            case 400051008: 
            case 400051015: 
            case 400051017: 
            case 400051041: {
                return true;
            }
        }
        return false;
    }

    public static Map<Integer, Integer> hn() {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        switch (Randomizer.nextInt(8)) {
            case 0: {
                hashMap.put(25101000, 0);
                hashMap.put(25100001, 720);
                break;
            }
            case 1: {
                hashMap.put(25101003, 0);
                break;
            }
            case 2: {
                hashMap.put(25111012, 0);
                break;
            }
            case 3: {
                hashMap.put(25101012, 0);
                break;
            }
            case 4: {
                hashMap.put(25121000, 0);
                hashMap.put(25120001, 360);
                hashMap.put(25120002, 720);
                hashMap.put(25120003, 1080);
                break;
            }
            case 5: {
                hashMap.put(25121005, 0);
                break;
            }
            case 6: {
                hashMap.put(25121006, 0);
                break;
            }
            case 7: {
                hashMap.put(25121055, 0);
            }
        }
        return hashMap;
    }

    public static boolean eD(int n) {
        switch (n) {
            case 22110013: 
            case 22110014: 
            case 22110024: 
            case 22110025: 
            case 22140013: 
            case 22140014: 
            case 22140015: 
            case 22140023: 
            case 22140024: 
            case 22170064: 
            case 22170065: 
            case 22170066: 
            case 22170067: 
            case 22170093: 
            case 22170094: {
                return true;
            }
        }
        return false;
    }

    public static boolean eF(int n) {
        switch (n) {
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: 
            case 33001016: 
            case 33101115: 
            case 33111015: 
            case 33121017: 
            case 33121155: 
            case 33121255: {
                return true;
            }
        }
        return false;
    }

    public static boolean eH(int ee) {
        ee = SkillConstants.getLinkedAttackSkill(ee);
        switch (ee) {
            case 33001105: 
            case 33101113: 
            case 33111112: 
            case 33121114: {
                return true;
            }
        }
        return false;
    }

    public static int eM(int n) {
        switch (n) {
            case 80000086: {
                return 2023189;
            }
            case 86: 
            case 91: {
                return 2022746;
            }
            case 88: {
                return 2022747;
            }
            case 80000052: {
                return 2023148;
            }
            case 80000053: {
                return 0x1EDEED;
            }
            case 80000054: {
                return 0x1EDEEE;
            }
            case 80000155: {
                return 2022823;
            }
        }
        return -1;
    }

    public static boolean ej(int skillId) {
        int n = skillId / 10000;
        if (skillId / 10000 == 8000) {
            n = skillId / 100;
        }
        return n >= 800000 && n <= 800099 || n == 8001;
    }

    public static boolean i0(int n) {
        if (n <= 22140022) {
            if (n == 22140022) {
                return true;
            }
            if (n <= 22111012) {
                return n >= 22111011 || n >= 22110022 && n <= 22110023;
            }
            return n == 22111017;
        }
        if (n > 22171063) {
            return n == 80001894 || n == 400021046;
        }
        return n >= 22171062 || n >= 22141011 && n <= 22141012;
    }

    public static int getSkillByJob(int skillId, int job) {
        return skillId + JobConstants.getBeginner((short)job) * 10000;
    }

    public static boolean isMasterLevelSkill(int skillId) {
        if (SkillConstants.is4thNotNeedMasterLevel(skillId) > 0 || skillId / 1000000 == 92 && skillId % 10000 == 0 || SkillConstants.isMakingSkillRecipe(skillId) || SkillConstants.isCommonSkill(skillId) || SkillConstants.isNoviceSkill(skillId) || SkillConstants.isFieldAttackSKill(skillId)) {
            return false;
        }
        int skillRoot = SkillConstants.getSkillRootFromSkill(skillId);
        int jobLevel = SkillConstants.getJobLevel(skillRoot);
        return (skillRoot > 40005 || skillRoot < 40000) && skillId != 42120024 && (SkillConstants.isAddedSpDualAndZeroSkill(skillId) || jobLevel == 4 && !JobConstants.is神之子(skillRoot));
    }

    private static int is4thNotNeedMasterLevel(int skillID) {
        boolean v1;
        if (skillID > 5321006) {
            if (skillID > 33120010) {
                if (skillID <= 152120003) {
                    boolean v12;
                    if (skillID == 152120003 || skillID == 35120014 || skillID == 51120000) {
                        return 1;
                    }
                    boolean bl = v12 = skillID == 80001913;
                    if (v12) {
                        return 1;
                    }
                    return 0;
                }
                if (skillID > 152121006) {
                    boolean v13;
                    boolean bl = v13 = skillID == 152121010;
                    if (v13) {
                        return 1;
                    }
                    return 0;
                }
                if (skillID != 152121006 && (skillID < 152120012 || skillID > 152120013)) {
                    return 0;
                }
            } else if (skillID != 33120010) {
                boolean v14;
                if (skillID > 22171069) {
                    boolean v15;
                    if (skillID == 23120013 || skillID == 23121008) {
                        return 1;
                    }
                    boolean bl = v15 = skillID - 23121008 == 3;
                    if (v15) {
                        return 1;
                    }
                    return 0;
                }
                if (skillID != 22171069) {
                    v14 = skillID == 21121008;
                } else {
                    if (skillID == 21120011) {
                        return 1;
                    }
                    boolean bl = v14 = skillID - 21120011 == 3;
                }
                if (v14) {
                    return 1;
                }
                return 0;
            }
            return 1;
        }
        if (skillID == 5321006) {
            return 1;
        }
        if (skillID > 4340010) {
            if (skillID > 5220014) {
                boolean v16;
                if (skillID == 5221022 || skillID == 5320007) {
                    return 1;
                }
                boolean bl = v16 = skillID == 5321004;
                if (v16) {
                    return 1;
                }
                return 0;
            }
            if (skillID != 5220014) {
                if (skillID > 5120012) {
                    boolean v17;
                    boolean bl = v17 = skillID == 5220012;
                    if (v17) {
                        return 1;
                    }
                    return 0;
                }
                if (skillID < 5120011) {
                    boolean v18;
                    boolean bl = v18 = skillID == 4340012;
                    if (v18) {
                        return 1;
                    }
                    return 0;
                }
            }
            return 1;
        }
        if (skillID == 4340010) {
            return 1;
        }
        if (skillID > 2321010) {
            boolean v19;
            if (skillID == 3210015 || skillID == 4110012) {
                return 1;
            }
            boolean bl = v19 = skillID == 4210012;
            if (v19) {
                return 1;
            }
            return 0;
        }
        if (skillID == 2321010) {
            return 1;
        }
        if (skillID > 2121009) {
            v1 = skillID == 2221009;
        } else {
            if (skillID == 2121009 || skillID == 1120012) {
                return 1;
            }
            boolean bl = v1 = skillID == 1320011;
        }
        if (v1) {
            return 1;
        }
        return 0;
    }

    private static boolean isMakingSkillRecipe(int recipeID) {
        int v1;
        boolean result = false;
        if ((recipeID / 1000000 == 92 || recipeID % 10000 > 0) && (v1 = 10000 * (recipeID / 10000)) / 1000000 == 92 && v1 % 10000 == 0) {
            result = true;
        }
        return result;
    }

    private static boolean isCommonSkill(int nSkillID) {
        int branch = nSkillID / 10000;
        if (nSkillID / 10000 == 8000) {
            branch = nSkillID / 100;
        }
        return branch >= 800000 && branch <= 800099;
    }

    private static boolean isNoviceSkill(int skillID) {
        int branch = skillID / 10000;
        if (skillID / 10000 == 8000) {
            branch = skillID / 100;
        }
        return JobConstants.is零轉職業(branch);
    }

    private static boolean isFieldAttackSKill(int skillID) {
        if (skillID == 0 || (skillID & Integer.MIN_VALUE) != 0) {
            return false;
        }
        int v1 = skillID / 10000;
        if (skillID / 10000 == 8000) {
            v1 = skillID / 100;
        }
        return v1 == 9500;
    }

    private static boolean isAddedSpDualAndZeroSkill(int skillId) {
        if (skillId == 101120104 || skillId == 101120204) {
            return true;
        }
        if (skillId == 101110203 || skillId == 101100201 || skillId == 101110102) {
            return true;
        }
        if (skillId == 101110200) {
            return true;
        }
        if (skillId == 101100101) {
            return true;
        }
        if (skillId == 4340007 || skillId == 4341004) {
            return true;
        }
        if (skillId == 4331002 || skillId == 4311003 || skillId == 0x41EEEE) {
            return true;
        }
        return skillId == 4330009;
    }

    private static int getJobLevel(int job) {
        int result;
        if (JobConstants.is零轉職業(job) || job % 100 == 0 || job == 501 || job == 3101 || job == 301 || job == 508) {
            return 1;
        }
        if (JobConstants.is龍魔導士(job)) {
            return JobConstants.get龍魔轉數(job);
        }
        if (JobConstants.is影武者(job)) {
            result = 0;
            int dual_job_level = (job - 430) / 2;
            if (dual_job_level <= 2) {
                result = dual_job_level + 2;
            }
        } else {
            result = 0;
            if (job % 10 <= 2) {
                result = job % 10 + 2;
            }
        }
        return result;
    }

    private static int getSkillRootFromSkill(int nSkillID) {
        int result = nSkillID / 10000;
        if (nSkillID / 10000 == 8000) {
            result = nSkillID / 100;
        }
        return result;
    }

    public static boolean hD(int n) {
        boolean b = (n / 1000000 != 92 || n % 10000 != 0) && (n = 10000 * (n / 10000)) / 1000000 == 92 && n % 10000 == 0;
        return b;
    }

    public static int hA(int n) {
        int n2 = n / 10000;
        if (n / 10000 == 8000) {
            n2 = n / 100;
        }
        return n2;
    }

    public static boolean sub_140A60E40(int id) {
        boolean result = SkillConstants.sub_140A36D60(id) && SkillConstants.sub_140A37730(id) ? id == 152141001 : false;
        return result;
    }

    public static boolean sub_140A36D60(int id) {
        boolean v4;
        boolean v2 = false;
        if (id > 61121217) {
            int v3 = id - 400011058;
            if (v3 == 0) {
                return true;
            }
            v2 = v3 == 1;
        } else {
            if (id == 61121217 || id == 61101002 || id == 61110211 || id == 61120007 || id == 36001005 || id == 36100010 || id == 36110012) {
                return true;
            }
            boolean bl = v2 = id == 36120015;
        }
        if (v2 || id == 4100012 || id == 0x3EDDD3 || id == 35101002 || id == 35110017 || SkillConstants.sub_84ABA0(id) || SkillConstants.sub_884580(id) || SkillConstants.sub_5B9DA0(id) || Integer.toUnsignedLong(id - 80002602) <= 19L || SkillConstants.sub_86B470(id)) {
            return true;
        }
        if (id > 155141002) {
            if (id == 155141009 || id == 155141013) {
                return true;
            }
            v4 = id == 155141018;
        } else {
            if (id == 155141002 || id == 155001000 || id == 155101002 || id == 155111003) {
                return true;
            }
            v4 = id == 155121003;
        }
        boolean v5 = false;
        if (!(v4 || SkillConstants.sub_140AA5580(id) || SkillConstants.sub_140A3EC80(id) || id == 3011004 || id == 3300002 || id == 3321003)) {
            if (id > 36110004) {
                if (id > 175121017) {
                    if (id > 400041010) {
                        if (id > 400051017) {
                            int v34 = id - 400051087;
                            if (v34 == 0) {
                                return true;
                            }
                            int v35 = v34 - 100009928;
                            if (v35 == 0) {
                                return true;
                            }
                            int v36 = v35 - 2;
                            if (v36 == 0) {
                                return true;
                            }
                            v5 = v36 == 17;
                        } else {
                            if (id == 400051017) {
                                return true;
                            }
                            int v31 = id - 400041023;
                            if (v31 == 0) {
                                return true;
                            }
                            int v32 = v31 - 15;
                            if (v32 == 0) {
                                return true;
                            }
                            int v33 = v32 - 11;
                            if (v33 == 0) {
                                return true;
                            }
                            v5 = v33 == 19;
                        }
                    } else {
                        if (id == 400041010) {
                            return true;
                        }
                        if (id > 400031021) {
                            int v28 = id - 400031022;
                            if (v28 == 0) {
                                return true;
                            }
                            int v29 = v28 - 7;
                            if (v29 == 0) {
                                return true;
                            }
                            int v30 = v29 - 2;
                            if (v30 == 0) {
                                return true;
                            }
                            v5 = v30 == 23;
                        } else {
                            if (id == 400031021) {
                                return true;
                            }
                            int v24 = id - 400011131;
                            if (v24 == 0) {
                                return true;
                            }
                            int v25 = v24 - 9870;
                            if (v25 == 0) {
                                return true;
                            }
                            int v26 = v25 - 44;
                            if (v26 == 0) {
                                return true;
                            }
                            int v27 = v26 - 9955;
                            if (v27 == 0) {
                                return true;
                            }
                            v5 = v27 == 20;
                        }
                    }
                } else {
                    if (id == 175121017) {
                        return true;
                    }
                    if (id > 142110011) {
                        if (id > 152141002) {
                            if (id == 155100009 || id == 164101004 || id == 164120007) {
                                return true;
                            }
                            v5 = id == 175121007;
                        } else {
                            if (id == 152141002) {
                                return true;
                            }
                            int v20 = id - 152001001;
                            if (v20 == 0) {
                                return true;
                            }
                            int v21 = v20 - 119000;
                            if (v21 == 0) {
                                return true;
                            }
                            int v22 = v21 - 1;
                            if (v22 == 0) {
                                return true;
                            }
                            int v23 = v22 - 20998;
                            if (v23 == 0) {
                                return true;
                            }
                            v5 = v23 == 1;
                        }
                    } else {
                        if (id == 142110011 || id == 80001890 || id == 42110002 || id == 65111007 || id == 65120011 || id == 65141502 || id == 80001588 || id == 80002811 || id == 112110005 || id == 131003016) {
                            return true;
                        }
                        v5 = id == 135002015;
                    }
                }
            } else {
                if (id == 36110004) {
                    return true;
                }
                if (id > 13100027) {
                    if (id > 14120018) {
                        if (id == 25100010 || id == 14120020 || id == 24100003 || id == 24120002 || id == 24121011 || id == 25120115 || id == 25141505 || id == 31221014) {
                            return true;
                        }
                        v5 = id == 31241001;
                    } else {
                        if (id == 14120018) {
                            return true;
                        }
                        if (id > 13121017) {
                            int v17 = id - 14000028;
                            if (v17 == 0) {
                                return true;
                            }
                            int v18 = v17 - 1;
                            if (v18 == 0) {
                                return true;
                            }
                            int v19 = v18 - 110005;
                            if (v19 == 0) {
                                return true;
                            }
                            v5 = v19 == 1;
                        } else {
                            if (id == 13121017) {
                                return true;
                            }
                            int v13 = id - 13101022;
                            if (v13 == 0) {
                                return true;
                            }
                            int v14 = v13 - 9000;
                            if (v14 == 0) {
                                return true;
                            }
                            int v15 = v14 - 5;
                            if (v15 == 0) {
                                return true;
                            }
                            int v16 = v15 - 9976;
                            if (v16 == 0) {
                                return true;
                            }
                            v5 = v16 == 7;
                        }
                    }
                } else {
                    if (id == 13100027) {
                        return true;
                    }
                    if (id > 12110030) {
                        if (id > 12121059) {
                            int v10 = id - 12141001;
                            if (v10 == 0) {
                                return true;
                            }
                            int v11 = v10 - 1;
                            if (v11 == 0) {
                                return true;
                            }
                            int v12 = v11 - 2;
                            if (v12 == 0) {
                                return true;
                            }
                            v5 = v12 == 1;
                        } else {
                            if (id == 12121059) {
                                return true;
                            }
                            int v6 = id - 12120010;
                            if (v6 == 0) {
                                return true;
                            }
                            int v7 = v6 - 7;
                            if (v7 == 0) {
                                return true;
                            }
                            int v8 = v7 - 2;
                            if (v8 == 0) {
                                return true;
                            }
                            int v9 = v8 - 1;
                            if (v9 == 0) {
                                return true;
                            }
                            v5 = v9 == 1037;
                        }
                    } else {
                        if (id == 12110030 || id == 4210014 || id == 3100010 || id == 3120017 || id == 3300005 || id == 3301009 || id == 3321037 || id == 4220021 || id == 12000026 || id == 12100028) {
                            return true;
                        }
                        v5 = id == 0xB8C8CC;
                    }
                }
            }
            return v5;
        }
        return true;
    }

    public static boolean sub_84ABA0(int a1) {
        return a1 == 152110004 || a1 == 152120016 || a1 == 155121003;
    }

    public static boolean sub_884580(int a1) {
        boolean v1;
        if (a1 > 155111003) {
            v1 = a1 == 155121003;
        } else {
            if (a1 == 155111003 || a1 == 155001000) {
                return true;
            }
            v1 = a1 == 155101002;
        }
        return v1;
    }

    public static boolean sub_5B9DA0(int a1) {
        return a1 == 22141017 || a1 == 22170070 || a1 == 155111207;
    }

    public static boolean sub_86B470(int a1) {
        return a1 == 3011004 || a1 == 3300002 || a1 == 3321003;
    }

    public static boolean sub_140A60DA0(int id) {
        return id - 152141004 <= 2 || id == 152110004 || id == 152120016 || id == 155121003 || id == 155141018;
    }

    public static boolean sub_140AA5580(int a1) {
        return a1 == 22141017 || a1 == 22170070 || a1 == 63111010 || a1 == 155111207;
    }

    public static boolean sub_140A3EC80(int a1) {
        return false;
    }

    public static boolean sub_140A60DE0(int id) {
        boolean result = false;
        if ((id - 152141004 <= 2 || id == 152110004 || id == 152120016 || id == 155121003 || id == 155141018) && SkillConstants.sub_140A37730(id)) {
            result = id == 152141006;
        }
        return result;
    }

    public static boolean sub_140A37730(int id) {
        boolean v1;
        if (id > 152141005) {
            if (id == 162121010 || id == 152141006 || id == 162101011 || id == 162111005 || id == 162121019) {
                return true;
            }
            v1 = id == 400021122;
        } else {
            if (id == 152141005) {
                return true;
            }
            if (id > 31241001) {
                int v2 = id - 152141001;
                if (v2 == 0) {
                    return true;
                }
                v1 = v2 == 1;
            } else {
                if (id == 31241001 || id == 2121052 || id == 3341002) {
                    return true;
                }
                v1 = id == 11121018;
            }
        }
        return v1;
    }

    public static boolean isKeyDownSkill(int skillID) {
        switch (skillID) {
            case 0: 
            case 4341052: 
            case 101110104: 
            case 400041007: 
            case 400051006: 
            case 400051073: {
                return false;
            }
        }
        Skill skill = SkillFactory.getSkill(skillID);
        return skill != null && skill.isChargeSkill();
    }

    public static boolean isEvanForceSkill(int skillID) {
        return switch (skillID) {
            case 22110022, 22110023, 22111011, 22111012, 22111017, 22140022, 22141011, 22141012, 22171062, 22171063, 22201002, 80001894, 400021012, 400021046 -> true;
            default -> false;
        };
    }

    public static boolean isSuperNovaSkill(int skillID) {
        return skillID == 4221052 || skillID == 65121052;
    }

    public static boolean isRushBombSkill(int skillID) {
        return switch (skillID) {
            case 2221012, 2241002, 5300007, 5301001, 11101029, 11101030, 12121001, 12141007, 14101028, 14101029, 15101028, 22140015, 22140024, 31201001, 40021186, 42120000, 42120003, 61111100, 61111113, 61111218, 64101002, 64101008, 80002247, 80002300, 80011386, 80011390, 101120200, 101120203, 101120205, 101141010, 400001018, 400021131, 400031003, 400031004, 400031036, 400031067, 400031068 -> true;
            default -> false;
        };
    }

    public static boolean isZeroSkill(int skillID) {
        int prefix = skillID / 10000;
        if (skillID / 10000 == 8000 || skillID / 10000 == 8001) {
            prefix = skillID / 100;
        }
        return prefix == 10000 || prefix == 10100 || prefix == 10110 || prefix == 10111 || prefix == 10112;
    }

    public static boolean isUsercloneSummonedAbleSkill(int skillID) {
        int v38;
        int v37;
        int v36;
        int v35;
        switch (skillID) {
            case 11100027: 
            case 11101029: 
            case 11110027: 
            case 11110032: 
            case 11110033: 
            case 11111029: 
            case 11111230: 
            case 11120016: 
            case 11121014: 
            case 11121102: 
            case 11121156: 
            case 11121157: 
            case 14001020: 
            case 14101020: 
            case 14101028: 
            case 14101029: 
            case 14111020: 
            case 14111021: 
            case 14120045: 
            case 14121001: 
            case 14121002: 
            case 23001000: 
            case 23100004: 
            case 23101000: 
            case 23101001: 
            case 23101007: 
            case 23110006: 
            case 23111000: 
            case 23111001: 
            case 23111002: 
            case 23111003: 
            case 23120013: 
            case 23121000: 
            case 23121002: 
            case 23121003: 
            case 23121011: 
            case 23121052: 
            case 131001000: 
            case 131001001: 
            case 131001002: 
            case 131001003: 
            case 131001004: 
            case 131001005: 
            case 131001008: 
            case 131001010: 
            case 131001011: 
            case 131001012: 
            case 131001013: 
            case 131001101: 
            case 131001102: 
            case 131001103: 
            case 131001104: 
            case 131001108: 
            case 131001113: 
            case 131001201: 
            case 131001202: 
            case 131001203: 
            case 131001208: 
            case 131001213: 
            case 131001313: 
            case 131002010: 
            case 400031024: 
            case 400041059: 
            case 400041060: {
                return true;
            }
        }
        if (skillID <= 101110200) {
            if (skillID == 101110200) {
                return true;
            }
            if (skillID <= 23111002) {
                boolean v8;
                if (skillID == 23111002) {
                    return true;
                }
                if (skillID <= 14141000) {
                    boolean v82;
                    if (skillID == 14141000) {
                        return true;
                    }
                    if (skillID <= 14101029) {
                        boolean v3;
                        if (skillID == 14101029) {
                            return true;
                        }
                        if (skillID > 14101020) {
                            int v4 = skillID - 14101021;
                            if (v4 == 0) {
                                return true;
                            }
                            v3 = v4 == 7;
                        } else {
                            if (skillID == 14101020) {
                                return true;
                            }
                            int v1 = skillID - 11111130;
                            if (v1 == 0) {
                                return true;
                            }
                            int v2 = v1 - 100;
                            if (v2 == 0) {
                                return true;
                            }
                            v3 = v2 == 2889790;
                        }
                        return v3;
                    }
                    int v5 = skillID - 14111020;
                    if (v5 == 0) {
                        return true;
                    }
                    int v6 = v5 - 1;
                    if (v6 == 0) {
                        return true;
                    }
                    int v7 = v6 - 9024;
                    if (v7 == 0) {
                        return true;
                    }
                    int v9 = v7 - 956;
                    boolean bl = v82 = v9 == 0;
                    if (v82) {
                        return true;
                    }
                    boolean v3 = v9 == 1;
                    return v3;
                }
                if (skillID <= 23101000) {
                    if (skillID == 23101000) {
                        return true;
                    }
                    int v10 = skillID - 14141001;
                    if (v10 == 0) {
                        return true;
                    }
                    int v11 = v10 - 1;
                    if (v11 == 0) {
                        return true;
                    }
                    int v12 = v11 - 1;
                    if (v12 == 0) {
                        return true;
                    }
                    int v13 = v12 - 8859997;
                    if (v13 == 0) {
                        return true;
                    }
                    boolean v3 = v13 == 99004;
                    return v3;
                }
                int v14 = skillID - 23101001;
                if (v14 == 0) {
                    return true;
                }
                int v15 = v14 - 6;
                if (v15 == 0) {
                    return true;
                }
                int v16 = v15 - 8999;
                if (v16 == 0) {
                    return true;
                }
                int v9 = v16 - 994;
                boolean bl = v8 = v9 == 0;
                if (v8) {
                    return true;
                }
                boolean v3 = v9 == 1;
                return v3;
            }
            if (skillID <= 101000201) {
                boolean v22;
                if (skillID == 101000201) {
                    return true;
                }
                if (skillID <= 23121011) {
                    boolean v8;
                    if (skillID == 23121011) {
                        return true;
                    }
                    int v17 = skillID - 23111003;
                    if (v17 == 0) {
                        return true;
                    }
                    int v18 = v17 - 9010;
                    if (v18 == 0) {
                        return true;
                    }
                    int v19 = v18 - 987;
                    if (v19 == 0) {
                        return true;
                    }
                    int v9 = v19 - 2;
                    boolean bl = v8 = v9 == 0;
                    if (v8) {
                        return true;
                    }
                    boolean v3 = v9 == 1;
                    return v3;
                }
                int v20 = skillID - 23121052;
                if (v20 == 0) {
                    return true;
                }
                int v21 = v20 - 19948;
                if (v21 == 0) {
                    return true;
                }
                int v23 = v21 - 77859100;
                boolean bl = v22 = v23 == 0;
                if (v22) {
                    return true;
                }
                int v24 = v23 - 1;
                if (v24 == 0) {
                    return true;
                }
                boolean v3 = v24 == 99;
                return v3;
            }
            if (skillID <= 101100201) {
                boolean v22;
                if (skillID == 101100201) {
                    return true;
                }
                int v25 = skillID - 101001100;
                if (v25 == 0) {
                    return true;
                }
                int v26 = v25 - 100;
                if (v26 == 0) {
                    return true;
                }
                int v23 = v26 - 98900;
                boolean bl = v22 = v23 == 0;
                if (v22) {
                    return true;
                }
                int v24 = v23 - 1;
                if (v24 == 0) {
                    return true;
                }
                boolean v3 = v24 == 99;
                return v3;
            }
            int v27 = skillID - 101101100;
            if (v27 == 0) {
                return true;
            }
            int v28 = v27 - 100;
            if (v28 == 0) {
                return true;
            }
            int v29 = v28 - 8901;
            if (v29 == 0) {
                return true;
            }
            int v30 = v29 - 1;
            if (v30 == 0) {
                return true;
            }
            boolean v3 = v30 == 2;
            return v3;
        }
        if (skillID > 131001000) {
            boolean v8;
            if (skillID <= 131001313) {
                if (skillID != 131001313) {
                    switch (skillID) {
                        case 131001001: 
                        case 131001002: 
                        case 131001003: 
                        case 131001004: 
                        case 131001005: 
                        case 131001008: 
                        case 131001010: 
                        case 131001011: 
                        case 131001012: 
                        case 131001013: 
                        case 131001101: 
                        case 131001102: 
                        case 131001103: 
                        case 131001104: 
                        case 131001108: 
                        case 131001113: 
                        case 131001201: 
                        case 131001202: 
                        case 131001203: 
                        case 131001208: 
                        case 131001213: {
                            return true;
                        }
                    }
                    return false;
                }
                return true;
            }
            int v39 = skillID - 131002010;
            if (v39 == 0) {
                return true;
            }
            int v40 = v39 - 269029014;
            if (v40 == 0) {
                return true;
            }
            int v9 = v40 - 10035;
            boolean bl = v8 = v9 == 0;
            if (v8) {
                return true;
            }
            boolean v3 = v9 == 1;
            return v3;
        }
        if (skillID == 131001000) {
            return true;
        }
        if (skillID <= 101120200) {
            boolean v3;
            if (skillID == 101120200) {
                return true;
            }
            if (skillID > 101111200) {
                int v33 = skillID - 101120100;
                if (v33 == 0) {
                    return true;
                }
                int v34 = v33 - 2;
                if (v34 == 0) {
                    return true;
                }
                v3 = v34 == 2;
            } else {
                if (skillID == 101111200) {
                    return true;
                }
                int v31 = skillID - 101110202;
                if (v31 == 0) {
                    return true;
                }
                int v32 = v31 - 1;
                if (v32 == 0) {
                    return true;
                }
                v3 = v32 == 897;
            }
            return v3;
        }
        if (skillID > 101141000) {
            switch (skillID) {
                case 101141001: 
                case 101141003: 
                case 101141006: 
                case 101141007: 
                case 101141008: 
                case 101141009: {
                    return true;
                }
            }
            return false;
        }
        if (skillID != 101141000 && (v35 = skillID - 101120201) > 0 && (v36 = v35 - 1) > 0 && (v37 = v36 - 2) > 0 && (v38 = v37 - 896) > 0) {
            boolean v3 = v38 == 100;
            return v3;
        }
        return true;
    }

    public static boolean isScreenCenterAttackSkill(int skillID) {
        switch (skillID) {
            case 13121052: 
            case 14121052: 
            case 15121052: 
            case 21121057: 
            case 24121052: 
            case 80001431: 
            case 0x4C4C00C: 
            case 80011562: 
            case 100001283: 
            case 400011124: 
            case 400011125: 
            case 400011126: 
            case 400011127: {
                return true;
            }
        }
        return false;
    }

    public static boolean isAranFallingStopSkill(int skillID) {
        switch (skillID) {
            case 21000006: 
            case 21000007: 
            case 21001010: 
            case 80001925: 
            case 80001926: 
            case 80001927: 
            case 80001936: 
            case 80001937: 
            case 80001938: {
                return true;
            }
        }
        return false;
    }

    public static boolean sub_140E6E0B0(int sourceSkillID, int skillID) {
        return switch (sourceSkillID) {
            case 3221019 -> {
                if (skillID == 3221019) {
                    yield true;
                }
                yield false;
            }
            case 11120217 -> {
                if (skillID == 0xAA0050) {
                    yield true;
                }
                yield false;
            }
            case 31000004 -> {
                if (skillID == 31141002) {
                    yield true;
                }
                yield false;
            }
            case 31001006 -> {
                if (skillID == 31141003) {
                    yield true;
                }
                yield false;
            }
            case 31001007 -> {
                if (skillID == 31141004) {
                    yield true;
                }
                yield false;
            }
            case 31001008 -> {
                if (skillID == 31141005) {
                    yield true;
                }
                yield false;
            }
            case 31221001 -> {
                if (skillID == 31241000) {
                    yield true;
                }
                yield false;
            }
            case 32111020 -> {
                if (skillID == 32141000) {
                    yield true;
                }
                yield false;
            }
            case 33001016 -> {
                if (skillID == 33141006) {
                    yield true;
                }
                yield false;
            }
            case 33001025 -> {
                if (skillID == 33141007) {
                    yield true;
                }
                yield false;
            }
            case 33101115 -> {
                if (skillID == 33141008) {
                    yield true;
                }
                yield false;
            }
            case 33101215 -> {
                if (skillID == 33141009) {
                    yield true;
                }
                yield false;
            }
            case 33111015 -> {
                if (skillID == 33141010) {
                    yield true;
                }
                yield false;
            }
            case 33121017 -> {
                if (skillID == 33141011) {
                    yield true;
                }
                yield false;
            }
            case 33121255 -> {
                if (skillID == 33141012) {
                    yield true;
                }
                yield false;
            }
            case 51001005 -> {
                if (skillID == 51141002) {
                    yield true;
                }
                yield false;
            }
            case 51001011 -> {
                if (skillID == 51141008) {
                    yield true;
                }
                yield false;
            }
            case 14111036 -> {
                if (skillID == 14141011) {
                    yield true;
                }
                yield false;
            }
            case 14120017 -> {
                if (skillID == 14141013) {
                    yield true;
                }
                yield false;
            }
            case 14120018 -> {
                if (skillID == 14141014) {
                    yield true;
                }
                yield false;
            }
            case 14120019 -> {
                if (skillID == 14141015) {
                    yield true;
                }
                yield false;
            }
            case 14120020 -> {
                if (skillID == 14141016) {
                    yield true;
                }
                yield false;
            }
            case 14000028 -> {
                if (skillID == 14141006) {
                    yield true;
                }
                yield false;
            }
            case 14000029 -> {
                if (skillID == 14141007) {
                    yield true;
                }
                yield false;
            }
            case 14001027 -> {
                if (skillID == 14141004) {
                    yield true;
                }
                yield false;
            }
            case 14109033 -> {
                if (skillID == 14141008) {
                    yield true;
                }
                yield false;
            }
            case 14109034 -> {
                if (skillID == 14141009) {
                    yield true;
                }
                yield false;
            }
            case 14109035 -> {
                if (skillID == 14141010) {
                    yield true;
                }
                yield false;
            }
            case 15121001 -> {
                if (skillID == 15141000) {
                    yield true;
                }
                yield false;
            }
            case 15121002 -> {
                if (skillID == 15141003) {
                    yield true;
                }
                yield false;
            }
            case 21120004 -> {
                if (skillID == 21141000) {
                    yield true;
                }
                yield false;
            }
            case 21120026 -> {
                if (skillID == 21141004) {
                    yield true;
                }
                yield false;
            }
            case 21120027 -> {
                if (skillID == 21141008) {
                    yield true;
                }
                yield false;
            }
            case 22170060 -> {
                if (skillID == 22201000) {
                    yield true;
                }
                yield false;
            }
            case 22170061 -> {
                if (skillID == 22201001) {
                    yield true;
                }
                yield false;
            }
            case 24121000 -> {
                if (skillID == 24141001) {
                    yield true;
                }
                yield false;
            }
            case 24121005 -> {
                if (skillID == 24141000) {
                    yield true;
                }
                yield false;
            }
            case 25101009 -> {
                if (skillID == 25141001) {
                    yield true;
                }
                yield false;
            }
            case 25121005 -> {
                if (skillID == 25141000) {
                    yield true;
                }
                yield false;
            }
            case 27121100 -> {
                if (skillID == 27141001) {
                    yield true;
                }
                yield false;
            }
            case 27121303 -> {
                if (skillID == 27141000) {
                    yield true;
                }
                yield false;
            }
            case 37101000 -> {
                if (skillID == 37141001) {
                    yield true;
                }
                yield false;
            }
            case 37110010 -> {
                if (skillID == 37141005) {
                    yield true;
                }
                yield false;
            }
            case 37120013 -> {
                if (skillID == 37141006) {
                    yield true;
                }
                yield false;
            }
            case 37000008 -> {
                if (skillID == 37141003) {
                    yield true;
                }
                yield false;
            }
            case 37000009 -> {
                if (skillID == 37000009) {
                    yield true;
                }
                yield false;
            }
            case 37001000 -> {
                if (skillID == 37141000) {
                    yield true;
                }
                yield false;
            }
            case 37100008 -> {
                if (skillID == 37100008) {
                    yield true;
                }
                yield false;
            }
            case 37101009 -> {
                if (skillID == 37141004) {
                    yield true;
                }
                yield false;
            }
            case 36121014 -> {
                if (skillID == 36141006) {
                    yield true;
                }
                yield false;
            }
            case 36111015 -> {
                if (skillID == 36141007) {
                    yield true;
                }
                yield false;
            }
            case 142120000 -> {
                if (skillID == 142140001) {
                    yield true;
                }
                yield false;
            }
            case 142001002 -> {
                if (skillID == 142141000) {
                    yield true;
                }
                yield false;
            }
            case 142120001 -> {
                if (skillID == 142140002) {
                    yield true;
                }
                yield false;
            }
            case 142120002 -> {
                if (skillID == 142140002) {
                    yield true;
                }
                yield false;
            }
            case 142120003 -> {
                if (skillID == 142140003) {
                    yield true;
                }
                yield false;
            }
            case 142120013 -> {
                if (skillID == 142140003) {
                    yield true;
                }
                yield false;
            }
            case 142120014 -> {
                if (skillID == 142140004) {
                    yield true;
                }
                yield false;
            }
            case 151121000 -> {
                if (skillID == 151141000 || skillID == 151141001) {
                    yield true;
                }
                yield false;
            }
            case 151121002 -> {
                if (skillID == 151141005) {
                    yield true;
                }
                yield false;
            }
            case 162101001 -> {
                if (skillID == 162141001) {
                    yield true;
                }
                yield false;
            }
            case 162121012 -> {
                if (skillID == 162141002) {
                    yield true;
                }
                yield false;
            }
            case 162121015 -> {
                if (skillID == 162141005) {
                    yield true;
                }
                yield false;
            }
            case 162121016 -> {
                if (skillID == 162141006) {
                    yield true;
                }
                yield false;
            }
            case 162121017 -> {
                if (skillID == 162141007) {
                    yield true;
                }
                yield false;
            }
            case 162121018 -> {
                if (skillID == 162141008) {
                    yield true;
                }
                yield false;
            }
            case 162121019 -> {
                if (skillID == 162141009) {
                    yield true;
                }
                yield false;
            }
            case 162121021 -> {
                if (skillID == 162141000) {
                    yield true;
                }
                yield false;
            }
            case 164001000 -> {
                if (skillID == 164141030) {
                    yield true;
                }
                yield false;
            }
            case 164101000 -> {
                if (skillID == 164141005) {
                    yield true;
                }
                yield false;
            }
            case 164101001 -> {
                if (skillID == 164141033) {
                    yield true;
                }
                yield false;
            }
            case 164101002 -> {
                if (skillID == 164141034) {
                    yield true;
                }
                yield false;
            }
            case 164111000 -> {
                if (skillID == 164141035) {
                    yield true;
                }
                yield false;
            }
            case 164111001 -> {
                if (skillID == 164141037) {
                    yield true;
                }
                yield false;
            }
            case 164111002 -> {
                if (skillID == 164141038) {
                    yield true;
                }
                yield false;
            }
            case 164111003 -> {
                if (skillID == 164141005) {
                    yield true;
                }
                yield false;
            }
            case 164111009 -> {
                if (skillID == 164141039) {
                    yield true;
                }
                yield false;
            }
            case 164111010 -> {
                if (skillID == 164141040) {
                    yield true;
                }
                yield false;
            }
            case 164121000 -> {
                if (skillID == 164141000) {
                    yield true;
                }
                yield false;
            }
            case 164121003 -> {
                if (skillID == 164141011) {
                    yield true;
                }
                yield false;
            }
            case 155001100 -> {
                if (skillID == 155141000) {
                    yield true;
                }
                yield false;
            }
            case 155101100 -> {
                if (skillID == 155141004) {
                    yield true;
                }
                yield false;
            }
            case 155101200 -> {
                if (skillID == 155141021) {
                    yield true;
                }
                yield false;
            }
            case 155101201 -> {
                if (skillID == 155141022) {
                    yield true;
                }
                yield false;
            }
            case 155101212 -> {
                if (skillID == 155141023) {
                    yield true;
                }
                yield false;
            }
            case 155111102 -> {
                if (skillID == 155141011) {
                    yield true;
                }
                yield false;
            }
            case 155111202 -> {
                if (skillID == 155141024) {
                    yield true;
                }
                yield false;
            }
            case 155111211 -> {
                if (skillID == 155141025) {
                    yield true;
                }
                yield false;
            }
            case 155111212 -> {
                if (skillID == 155141026) {
                    yield true;
                }
                yield false;
            }
            case 155121215 -> {
                if (skillID == 155141028) {
                    yield true;
                }
                yield false;
            }
            case 172121000 -> {
                if (skillID == 172141000) {
                    yield true;
                }
                yield false;
            }
            case 175001000 -> {
                if (skillID == 175141000) {
                    yield true;
                }
                yield false;
            }
            case 175101003 -> {
                if (skillID == 175141003) {
                    yield true;
                }
                yield false;
            }
            case 95001000 -> {
                if (skillID == 95001016) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    public static boolean sub_140E62810(int skillID) {
        return skillID - 152141004 <= 2 || skillID == 152110004 || skillID == 152120016 || skillID == 155121003 || skillID == 155141018;
    }

    public static boolean sub_140E5D870(int skillID) {
        switch (skillID) {
            case 14111024: 
            case 14121054: 
            case 14121055: 
            case 14121056: 
            case 131001017: 
            case 131002017: 
            case 131003017: 
            case 400001071: 
            case 400031007: 
            case 400031008: 
            case 400031009: 
            case 400041028: 
            case 500061004: 
            case 500061046: 
            case 500061047: 
            case 500061048: {
                return true;
            }
        }
        return false;
    }

    public static int getHyperAPByLevel(int level) {
        return level >= 140 ? level / 10 - 11 : 0;
    }

    public static int getHyperStatAPNeedByLevel(int level) {
        switch (level) {
            case 11: {
                return 50;
            }
            case 12: {
                return 65;
            }
            case 13: {
                return 80;
            }
            case 14: {
                return 95;
            }
            case 15: {
                return 110;
            }
        }
        return level < 5 ? (int)Math.pow(2.0, level - 1) : (level - 3) * 5;
    }

    public static int getHyperAP(MapleCharacter chr) {
        int ap = 0;
        if (chr.getLevel() >= 140) {
            for (int i = 140; i <= chr.getLevel(); ++i) {
                ap += SkillConstants.getHyperAPByLevel(i);
            }
        }
        for (Map.Entry<Integer, SkillEntry> entry : chr.getSkills().entrySet()) {
            Skill skill;
            if (entry.getValue().skillevel <= 0 || (skill = SkillFactory.getSkill(entry.getKey())) == null || !skill.isHyperStat()) continue;
            for (int i = 1; i <= entry.getValue().skillevel; ++i) {
                ap -= SkillConstants.getHyperStatAPNeedByLevel(i);
            }
        }
        return ap;
    }

    public static boolean isKeydownSkillRectMoveXY(int skillID) {
        return skillID == 13111020;
    }

    public static boolean isFieldAttackObjSkill(int skillId) {
        if (skillId <= 0) {
            return false;
        }
        int prefix = skillId / 10000;
        if (skillId / 10000 == 8000) {
            prefix = skillId / 100;
        }
        return prefix == 9500;
    }

    public static int getRandomInnerSkill() {
        return innerSkills[Randomizer.nextInt(innerSkills.length)];
    }

    public static boolean isKeydownSkillCancelGiveCD(int skillId) {
        switch (skillId) {
            case 20031205: 
            case 42121000: 
            case 42121100: {
                return true;
            }
        }
        return false;
    }

    public static boolean isOnOffSkill(int skillId) {
        switch (skillId) {
            case 11001022: 
            case 12101024: 
            case 13001022: 
            case 14001021: 
            case 15001022: 
            case 21000014: 
            case 27111008: 
            case 32001016: 
            case 32101009: 
            case 32111012: 
            case 32111016: 
            case 32111021: 
            case 32121010: 
            case 32121017: 
            case 32121018: {
                return true;
            }
        }
        return false;
    }

    public static int getKeydownSkillCancelReduceTime(SecondaryStatValueHolder mbsvh) {
        if (mbsvh == null || mbsvh.effect == null) {
            return 0;
        }
        return SkillConstants.getKeydownSkillCancelReduceTime(mbsvh.effect.getSourceId(), mbsvh.getLeftTime());
    }

    public static int getKeydownSkillCancelReduceTime(int skillID, int leftTime) {
        switch (skillID) {
            case 151121004: 
            case 162121022: {
                return 3500 * (leftTime / 1000);
            }
            case 164121042: {
                return 9700 * (leftTime / 1000);
            }
        }
        return 0;
    }

    static {
        TeachSkillMap.put(252, 80002759);
        TeachSkillMap.put(253, 80002760);
        TeachSkillMap.put(254, 80002761);
        TeachSkillMap.put(255, 80002763);
        TeachSkillMap.put(256, 80002764);
        TeachSkillMap.put(257, 80002765);
        TeachSkillMap.put(258, 80002767);
        TeachSkillMap.put(259, 80002768);
        TeachSkillMap.put(260, 80002769);
        TeachSkillMap.put(261, 80002771);
        TeachSkillMap.put(262, 80002772);
        TeachSkillMap.put(263, 80002773);
        TeachSkillMap.put(264, 80002775);
        TeachSkillMap.put(265, 80002776);
        TeachSkillMap.put(110, 80000000);
        TeachSkillMap.put(10000255, 80000066);
        TeachSkillMap.put(10000256, 80000067);
        TeachSkillMap.put(10000257, 0x4C4B444);
        TeachSkillMap.put(10000258, 80000069);
        TeachSkillMap.put(10000259, 80000070);
        TeachSkillMap.put(20000297, 80000370);
        TeachSkillMap.put(20010294, 80000369);
        TeachSkillMap.put(20021110, 80001040);
        TeachSkillMap.put(20030204, 80000002);
        TeachSkillMap.put(20040218, 80000005);
        TeachSkillMap.put(20050286, 80000169);
        TeachSkillMap.put(30010112, 80000001);
        TeachSkillMap.put(30010241, 80000050);
        TeachSkillMap.put(30000077, 80000378);
        TeachSkillMap.put(30000074, 80000333);
        TeachSkillMap.put(30000075, 80000334);
        TeachSkillMap.put(30000076, 80000335);
        TeachSkillMap.put(30020233, 80000047);
        TeachSkillMap.put(40010001, 80000003);
        TeachSkillMap.put(40020002, 80000004);
        TeachSkillMap.put(50001214, 80001140);
        TeachSkillMap.put(60000222, 80000006);
        TeachSkillMap.put(60030241, 80003015);
        TeachSkillMap.put(60020218, 80000261);
        TeachSkillMap.put(60011219, 80001155);
        TeachSkillMap.put(100000271, 80000110);
        TeachSkillMap.put(140000292, 0x4C4B4BC);
        TeachSkillMap.put(150020241, 80002857);
        TeachSkillMap.put(150000017, 80000268);
        TeachSkillMap.put(150030241, 80003224);
        TeachSkillMap.put(150010241, 80000514);
        TeachSkillMap.put(160010001, 80003058);
        TeachSkillMap.put(160000001, 80000609);
        TeachSkillMap.put(170000241, 80011964);
        TeachSkillMap.put(170010241, 80010006);
    }
}

