/*
 * Decompiled with CFR 0.152.
 */
package Config.constants;

import Client.MapleJob;
import Config.constants.SkillConstants;
import tools.Pair;

public class JobConstants {
    public static final int JOB_ORDER = 255;
    public static int[][][] JobStats = new int[24][2][6];

    public static int getEar(int job, int nEar) {
        int n3 = 0;
        if (JobConstants.is精靈遊俠(job)) {
            switch (nEar) {
                case 0: {
                    n3 = 1;
                    break;
                }
                case 1: {
                    n3 = 0;
                    break;
                }
                case 2: {
                    n3 = 2;
                    break;
                }
                case 3: {
                    n3 = 3;
                }
            }
        } else if (JobConstants.is伊利恩(job)) {
            switch (nEar) {
                case 0: {
                    n3 = 2;
                    break;
                }
                case 1: {
                    n3 = 1;
                    break;
                }
                case 2: {
                    n3 = 0;
                    break;
                }
                case 3: {
                    n3 = 3;
                }
            }
        } else if (JobConstants.is亞克(job) || JobConstants.is阿戴爾(job)) {
            switch (nEar) {
                case 0: {
                    n3 = 3;
                    break;
                }
                case 1: {
                    n3 = 1;
                    break;
                }
                case 2: {
                    n3 = 2;
                    break;
                }
                case 3: {
                    n3 = 0;
                }
            }
        } else {
            n3 = nEar;
        }
        return n3;
    }

    public static boolean is劍士(int job) {
        return JobConstants.getJobBranch(job) == 1;
    }

    public static boolean is法師(int job) {
        return JobConstants.getJobBranch(job) == 2;
    }

    public static boolean is弓箭手(int job) {
        return JobConstants.getJobBranch(job) == 3;
    }

    public static boolean is盜賊(int job) {
        return JobConstants.getJobBranch(job) == 4 || JobConstants.is傑諾(job);
    }

    public static boolean is海盜(int job) {
        return JobConstants.getJobBranch(job) == 5 || JobConstants.is傑諾(job);
    }

    public static boolean is冒險家(int job) {
        return job / 1000 == 0;
    }

    public static boolean is冒險家劍士(int job) {
        return job / 100 == 1;
    }

    public static boolean is英雄(int job) {
        return job / 10 == 11;
    }

    public static boolean is聖騎士(int job) {
        return job / 10 == 12;
    }

    public static boolean is黑騎士(int job) {
        return job / 10 == 13;
    }

    public static boolean is冒險家法師(int job) {
        return job / 100 == 2;
    }

    public static boolean is火毒(int job) {
        return job / 10 == 21;
    }

    public static boolean is冰雷(int job) {
        return job / 10 == 22;
    }

    public static boolean is主教(int job) {
        return job / 10 == 23;
    }

    public static boolean is冒險家弓箭手(int job) {
        return job / 100 == 3;
    }

    public static boolean is箭神(int job) {
        return job / 10 == 31;
    }

    public static boolean is神射手(int job) {
        return job / 10 == 32;
    }

    public static boolean is開拓者(int job) {
        return job == 301 || job / 10 == 33;
    }

    public static boolean is冒險家盜賊(int job) {
        return job / 100 == 4;
    }

    public static boolean is夜使者(int job) {
        return job / 10 == 41;
    }

    public static boolean is暗影神偷(int job) {
        return job / 10 == 42;
    }

    public static boolean is影武者(int job) {
        return job / 10 == 43 || job == 401;
    }

    public static boolean is冒險家海盜(int job) {
        return job / 100 == 5;
    }

    public static boolean is拳霸(int job) {
        return job / 10 == 51;
    }

    public static boolean is槍神(int job) {
        return job / 10 == 52;
    }

    public static boolean is拳霸新(int job) {
        return job == 509 || job / 10 == 58;
    }

    public static boolean is槍神新(int job) {
        return job == 509 || job / 10 == 59;
    }

    public static boolean is重砲指揮官(int job) {
        return job / 10 == 53 || job == 501;
    }

    public static boolean is管理員(int job) {
        return job == 800 || job == 900 || job == 910;
    }

    public static boolean is皇家騎士團(int job) {
        return job / 1000 == 1;
    }

    public static boolean is聖魂劍士(int job) {
        return job / 100 == 11;
    }

    public static boolean is烈焰巫師(int job) {
        return job / 100 == 12;
    }

    public static boolean is破風使者(int job) {
        return job / 100 == 13;
    }

    public static boolean is暗夜行者(int job) {
        return job / 100 == 14;
    }

    public static boolean is閃雷悍將(int job) {
        return job / 100 == 15;
    }

    public static boolean is英雄團(int job) {
        return job / 1000 == 2;
    }

    public static boolean is狂狼勇士(int job) {
        return job / 100 == 21 || job == 2000;
    }

    public static boolean is龍魔導士(int job) {
        return job / 100 == 22 || job == 2001;
    }

    public static boolean is精靈遊俠(int job) {
        return job / 100 == 23 || job == 2002;
    }

    public static boolean is幻影俠盜(int job) {
        return job / 100 == 24 || job == 2003;
    }

    public static boolean is夜光(int job) {
        return job / 100 == 27 || job == 2004;
    }

    public static boolean is隱月(int job) {
        return job / 100 == 25 || job == 2005;
    }

    public static boolean is末日反抗軍(int job) {
        return job / 1000 == 3;
    }

    public static boolean is惡魔(int job) {
        return JobConstants.is惡魔殺手(job) || JobConstants.is惡魔復仇者(job) || job == 3001;
    }

    public static boolean is惡魔殺手(int job) {
        return job / 10 == 311 || job == 3100;
    }

    public static boolean is惡魔復仇者(int job) {
        return job / 10 == 312 || job == 3101;
    }

    public static boolean is煉獄巫師(int job) {
        return job / 100 == 32;
    }

    public static boolean is狂豹獵人(int job) {
        return job / 100 == 33;
    }

    public static boolean is機甲戰神(int job) {
        return job / 100 == 35;
    }

    public static boolean is傑諾(int job) {
        return job / 100 == 36 || job == 3002;
    }

    public static boolean is爆拳槍神(int job) {
        return job / 100 == 37;
    }

    public static boolean is曉の陣(int job) {
        return job / 1000 == 4;
    }

    public static boolean is劍豪(int job) {
        return job / 100 == 41 || job == 4001;
    }

    public static boolean is陰陽師(int job) {
        return job / 100 == 42 || job == 4002;
    }

    public static boolean is騎士團團長(int job) {
        return job / 1000 == 5;
    }

    public static boolean is米哈逸(int job) {
        return job / 100 == 51 || job == 5000;
    }

    public static boolean is超新星(int job) {
        return job / 1000 == 6;
    }

    public static boolean is凱撒(int job) {
        return job / 100 == 61 || job == 6000;
    }

    public static boolean is凱殷(int job) {
        return job / 100 == 63 || job == 6003;
    }

    public static boolean is卡蒂娜(int job) {
        return job / 100 == 64 || job == 6002;
    }

    public static boolean is天使破壞者(int job) {
        return job / 100 == 65 || job == 6001;
    }

    public static boolean is神之子(int job) {
        return job == 10000 || job / 100 == 101;
    }

    public static boolean is皮卡啾(int job) {
        return job / 100 == 131 || job == 13000;
    }

    public static boolean is雪吉拉(int job) {
        return job / 100 == 135 || job == 13001;
    }

    public static boolean is異界(int job) {
        return job / 1000 == 14;
    }

    public static boolean is凱內西斯(int job) {
        return job / 100 == 142 || job == 14000;
    }

    public static boolean is雷普族(int job) {
        return job / 1000 == 15;
    }

    public static boolean is阿戴爾(int job) {
        return job / 100 == 151 || job == 15002;
    }

    public static boolean is伊利恩(int job) {
        return job / 100 == 152 || job == 15000;
    }

    public static boolean is卡莉(int job) {
        return job / 100 == 154 || job == 15003;
    }

    public static boolean is亞克(int job) {
        return job / 100 == 155 || job == 15001;
    }

    public static boolean is阿尼瑪族(int job) {
        return job / 1000 == 16;
    }

    public static boolean is菈菈(int job) {
        return job / 100 == 162 || job == 16001;
    }

    public static boolean is虎影(int job) {
        return job / 100 == 164 || job == 16000;
    }

    public static boolean is江湖(int job) {
        return job / 1000 == 17;
    }

    public static boolean is墨玄(int job) {
        return job / 100 == 175 || job == 17000;
    }

    public static boolean is琳恩(int job) {
        return job / 100 == 172 || job == 17001;
    }

    public static boolean is格蘭蒂斯(int job) {
        return JobConstants.is超新星(job) || JobConstants.is雷普族(job) || JobConstants.is阿尼瑪族(job);
    }

    public static boolean notNeedSPSkill(int job) {
        return SkillConstants.dZ(job) || JobConstants.is零轉職業(job);
    }

    public static boolean is零轉職業(int job) {
        return job % 1000 < 10;
    }

    public static short getBeginner(short jobid) {
        if (jobid % 1000 < 10) {
            return jobid;
        }
        int jobBranch = jobid % 1000 / 100;
        switch (jobid / 1000) {
            case 2: {
                switch (jobBranch) {
                    case 1: {
                        return (short)MapleJob.傳說.getId();
                    }
                    case 2: {
                        return (short)MapleJob.龍魔導士.getId();
                    }
                    case 3: {
                        return (short)MapleJob.精靈遊俠.getId();
                    }
                    case 4: {
                        return (short)MapleJob.幻影俠盜.getId();
                    }
                    case 5: {
                        return (short)MapleJob.隱月.getId();
                    }
                    case 7: {
                        return (short)MapleJob.夜光.getId();
                    }
                }
                break;
            }
            case 3: {
                switch (jobBranch) {
                    case 1: {
                        return (short)MapleJob.惡魔.getId();
                    }
                    case 2: 
                    case 3: 
                    case 5: 
                    case 7: {
                        return (short)MapleJob.市民.getId();
                    }
                    case 6: {
                        return (short)MapleJob.傑諾.getId();
                    }
                }
                break;
            }
            case 4: {
                switch (jobBranch) {
                    case 1: {
                        return (short)MapleJob.劍豪.getId();
                    }
                    case 2: {
                        return (short)MapleJob.陰陽師.getId();
                    }
                }
                break;
            }
            case 6: {
                switch (jobBranch) {
                    case 1: {
                        return (short)MapleJob.凱撒.getId();
                    }
                    case 3: {
                        return (short)MapleJob.凱殷.getId();
                    }
                    case 4: {
                        return (short)MapleJob.卡蒂娜.getId();
                    }
                    case 5: {
                        return (short)MapleJob.天使破壞者.getId();
                    }
                }
                break;
            }
            case 13: {
                switch (jobBranch) {
                    case 1: {
                        return (short)MapleJob.皮卡啾.getId();
                    }
                    case 5: {
                        return (short)MapleJob.雪吉拉.getId();
                    }
                }
                break;
            }
            case 15: {
                switch (jobBranch) {
                    case 1: {
                        return (short)MapleJob.阿戴爾.getId();
                    }
                    case 2: {
                        return (short)MapleJob.伊利恩.getId();
                    }
                    case 4: {
                        return (short)MapleJob.卡莉.getId();
                    }
                    case 5: {
                        return (short)MapleJob.亞克.getId();
                    }
                }
                break;
            }
            case 16: {
                switch (jobBranch) {
                    case 2: {
                        return (short)MapleJob.菈菈.getId();
                    }
                    case 4: {
                        return (short)MapleJob.虎影.getId();
                    }
                }
                break;
            }
            case 17: {
                switch (jobBranch) {
                    case 2: {
                        return (short)MapleJob.琳恩.getId();
                    }
                    case 5: {
                        return (short)MapleJob.墨玄.getId();
                    }
                }
            }
        }
        return (short)(jobid / 1000 * 1000);
    }

    public static int get龍魔轉數(int jobid) {
        switch (jobid) {
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

    public static int getJobBranch(int job) {
        int result = job % 1000 / 100;
        switch (job / 100) {
            case 27: {
                return 2;
            }
            case 36: {
                return 4;
            }
            case 37: {
                return 1;
            }
        }
        return result;
    }

    public static String getJobBranchName(int job) {
        if (JobConstants.is傑諾(job)) {
            return "xenon";
        }
        return switch (JobConstants.getJobBranch(job)) {
            case 1 -> "warrior";
            case 2 -> "magician";
            case 3 -> "archer";
            case 4 -> "rogue";
            case 5 -> "pirate";
            default -> "beginner";
        };
    }

    public static int getJobFlag(int job) {
        int n = 1 << JobConstants.getJobBranch(job) - 1;
        if (job / 100 == 36) {
            n |= 0x10;
        }
        return n;
    }

    public static boolean isJobFamily(int baseJob, int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }

    public static boolean noBulletJob(int job) {
        return JobConstants.is狂狼勇士(job) || JobConstants.is機甲戰神(job) || JobConstants.is拳霸(job) || JobConstants.is重砲指揮官(job) || JobConstants.is精靈遊俠(job) || JobConstants.is米哈逸(job) || JobConstants.is凱撒(job) || JobConstants.is天使破壞者(job) || JobConstants.is傑諾(job) || JobConstants.is開拓者(job);
    }

    public static boolean isNotMpJob(int job) {
        return JobConstants.is陰陽師(job) || JobConstants.is惡魔殺手(job) || JobConstants.is惡魔復仇者(job) || JobConstants.is神之子(job) || JobConstants.is凱內西斯(job);
    }

    public static int getTeachSkillID(int Job) {
        int skillId = -1;
        if (JobConstants.is冒險家(Job) && JobConstants.getJobNumber(Job) > 1) {
            if (JobConstants.is英雄(Job)) {
                skillId = 252;
            } else if (JobConstants.is聖騎士(Job)) {
                skillId = 253;
            } else if (JobConstants.is黑騎士(Job)) {
                skillId = 254;
            } else if (JobConstants.is火毒(Job)) {
                skillId = 255;
            } else if (JobConstants.is冰雷(Job)) {
                skillId = 256;
            } else if (JobConstants.is主教(Job)) {
                skillId = 257;
            } else if (JobConstants.is箭神(Job)) {
                skillId = 258;
            } else if (JobConstants.is神射手(Job)) {
                skillId = 259;
            } else if (JobConstants.is開拓者(Job)) {
                skillId = 260;
            } else if (JobConstants.is夜使者(Job)) {
                skillId = 261;
            } else if (JobConstants.is暗影神偷(Job)) {
                skillId = 262;
            } else if (JobConstants.is影武者(Job)) {
                skillId = 263;
            } else if (JobConstants.is拳霸(Job)) {
                skillId = 264;
            } else if (JobConstants.is槍神(Job)) {
                skillId = 265;
            } else if (JobConstants.is重砲指揮官(Job)) {
                skillId = 110;
            }
        } else if (JobConstants.is皇家騎士團(Job)) {
            int jobBranch = JobConstants.getJobBranch(Job);
            switch (jobBranch) {
                case 1: {
                    skillId = 10000255;
                    break;
                }
                case 2: {
                    skillId = 10000256;
                    break;
                }
                case 3: {
                    skillId = 10000257;
                    break;
                }
                case 4: {
                    skillId = 10000258;
                    break;
                }
                case 5: {
                    skillId = 10000259;
                }
            }
        } else if (JobConstants.is狂狼勇士(Job)) {
            skillId = 20000297;
        } else if (JobConstants.is龍魔導士(Job)) {
            skillId = 20010294;
        } else if (JobConstants.is精靈遊俠(Job)) {
            skillId = 20021110;
        } else if (JobConstants.is幻影俠盜(Job)) {
            skillId = 20030204;
        } else if (JobConstants.is夜光(Job)) {
            skillId = 20040218;
        } else if (JobConstants.is隱月(Job)) {
            skillId = 20050286;
        } else if (JobConstants.is惡魔殺手(Job)) {
            skillId = 30010112;
        } else if (JobConstants.is惡魔復仇者(Job)) {
            skillId = 30010241;
        } else if (JobConstants.is爆拳槍神(Job)) {
            skillId = 30000077;
        } else if (JobConstants.is煉獄巫師(Job)) {
            skillId = 30000074;
        } else if (JobConstants.is狂豹獵人(Job)) {
            skillId = 30000075;
        } else if (JobConstants.is機甲戰神(Job)) {
            skillId = 30000076;
        } else if (JobConstants.is傑諾(Job)) {
            skillId = 30020233;
        } else if (JobConstants.is劍豪(Job)) {
            skillId = 40010001;
        } else if (JobConstants.is陰陽師(Job)) {
            skillId = 40020002;
        } else if (JobConstants.is米哈逸(Job)) {
            skillId = 50001214;
        } else if (JobConstants.is凱撒(Job)) {
            skillId = 60000222;
        } else if (JobConstants.is凱殷(Job)) {
            skillId = 60030241;
        } else if (JobConstants.is卡蒂娜(Job)) {
            skillId = 60020218;
        } else if (JobConstants.is天使破壞者(Job)) {
            skillId = 60011219;
        } else if (JobConstants.is神之子(Job)) {
            skillId = 100000271;
        } else if (JobConstants.is凱內西斯(Job)) {
            skillId = 140000292;
        } else if (JobConstants.is阿戴爾(Job)) {
            skillId = 150020241;
        } else if (JobConstants.is伊利恩(Job)) {
            skillId = 150000017;
        } else if (JobConstants.is卡莉(Job)) {
            skillId = 150030241;
        } else if (JobConstants.is亞克(Job)) {
            skillId = 150010241;
        } else if (JobConstants.is菈菈(Job)) {
            skillId = 160010001;
        } else if (JobConstants.is虎影(Job)) {
            skillId = 160000001;
        } else if (JobConstants.is墨玄(Job)) {
            skillId = 170000241;
        } else if (JobConstants.is琳恩(Job)) {
            skillId = 170010241;
        }
        return skillId;
    }

    public static Pair<Integer, Integer> getDefaultFaceAndHair(int job, int gender) {
        int hair;
        int face = gender == 0 ? 20100 : 21700;
        int n = hair = gender == 0 ? 30030 : 31002;
        if (JobConstants.is影武者(job)) {
            face = gender == 0 ? 20265 : 21261;
            hair = gender == 0 ? 33830 : 34820;
        } else if (JobConstants.is精靈遊俠(job)) {
            face = gender == 0 ? 20549 : 21547;
            hair = gender == 0 ? 33453 : 34423;
        } else if (JobConstants.is幻影俠盜(job)) {
            face = gender == 0 ? 20659 : 21656;
            hair = gender == 0 ? 33703 : 34703;
        } else if (JobConstants.is夜光(job)) {
            face = gender == 0 ? 20174 : 21169;
            hair = gender == 0 ? 36190 : 37070;
        } else if (JobConstants.is惡魔殺手(job)) {
            face = gender == 0 ? 20248 : 21246;
            hair = gender == 0 ? 33531 : 34411;
        } else if (JobConstants.is惡魔復仇者(job)) {
            face = gender == 0 ? 20248 : 21280;
            hair = gender == 0 ? 36460 : 37450;
        } else if (JobConstants.is傑諾(job)) {
            face = gender == 0 ? 20185 : 21182;
            hair = gender == 0 ? 36470 : 37490;
        } else if (JobConstants.is米哈逸(job)) {
            face = gender == 0 ? 20169 : 21700;
            hair = gender == 0 ? 36033 : 31002;
        } else if (JobConstants.is凱撒(job)) {
            face = gender == 0 ? 20576 : 21571;
            hair = gender == 0 ? 36245 : 37125;
        } else if (JobConstants.is天使破壞者(job)) {
            face = gender == 0 ? 20576 : 21374;
            hair = gender == 0 ? 36245 : 37242;
        }
        return new Pair<Integer, Integer>(face, hair);
    }

    public static int getSkillBookBySkill(int skillId) {
        return JobConstants.getSkillBookByJob(skillId / 10000, skillId);
    }

    public static int getJobNumber(int jobz) {
        int job = jobz % 1000;
        if (jobz > 40000) {
            return 5;
        }
        if (JobConstants.is龍魔導士(jobz)) {
            return JobConstants.get龍魔轉數(jobz);
        }
        if (jobz / 10 == 43) {
            if ((jobz - 430) / 2 <= 2) {
                return (jobz - 430) / 2 + 2;
            }
            return 0;
        }
        if (job < 100) {
            return 0;
        }
        if (job / 10 % 10 == 0) {
            return 1;
        }
        return 2 + job % 10;
    }

    public static int getJobGrade(int jobz) {
        if (JobConstants.is龍魔導士(jobz)) {
            return JobConstants.get龍魔轉數(jobz);
        }
        int job = jobz % 1000;
        if (job / 10 == 0) {
            return 0;
        }
        if (job / 10 % 10 == 0) {
            return 1;
        }
        return job % 10 + 2;
    }

    public static boolean isSameJob(int job, int job2) {
        int jobNum = JobConstants.getJobGrade(job);
        int job2Num = JobConstants.getJobGrade(job2);
        if (jobNum == 0 || job2Num == 0) {
            return JobConstants.getBeginner((short)job) == JobConstants.getBeginner((short)job2);
        }
        if (JobConstants.getJobGroup(job) != JobConstants.getJobGroup(job2)) {
            return false;
        }
        if (JobConstants.is管理員(job) || JobConstants.is管理員(job2)) {
            return JobConstants.is管理員(job) && JobConstants.is管理員(job2);
        }
        if (JobConstants.is重砲指揮官(job) || JobConstants.is重砲指揮官(job2)) {
            return JobConstants.is重砲指揮官(job) && JobConstants.is重砲指揮官(job2);
        }
        if (JobConstants.is惡魔復仇者(job) || JobConstants.is惡魔復仇者(job2)) {
            return JobConstants.is惡魔復仇者(job) && JobConstants.is惡魔復仇者(job2);
        }
        if (jobNum == 1 || job2Num == 1) {
            return job / 100 == job2 / 100;
        }
        return job / 10 == job2 / 10;
    }

    public static int getJobGroup(int job) {
        return job / 1000;
    }

    public static int getSkillBookByLevel(int job, int level) {
        if (JobConstants.is龍魔導士(job)) {
            switch (job) {
                case 2211: 
                case 2212: 
                case 2213: {
                    return 1;
                }
                case 2214: 
                case 2215: 
                case 2216: {
                    return 2;
                }
                case 2217: 
                case 2218: {
                    return 3;
                }
            }
        } else {
            if (JobConstants.is影武者(job)) {
                if (level > 100) {
                    return 5;
                }
                if (level > 60) {
                    return 4;
                }
                if (level > 45) {
                    return 3;
                }
                if (level > 30) {
                    return 2;
                }
                if (level > 20) {
                    return 1;
                }
                return 0;
            }
            if (JobConstants.isSeparatedSpJob(job)) {
                if (level > 100) {
                    return 3;
                }
                if (level > 60) {
                    return 2;
                }
                if (level > 30) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public static int getSkillBookByJob(int job) {
        return JobConstants.getSkillBookByJob(job, 0);
    }

    public static int getSkillBookByJob(int job, int skillId) {
        if (JobConstants.is龍魔導士(job)) {
            switch (job) {
                case 2211: 
                case 2212: 
                case 2213: {
                    return 1;
                }
                case 2214: 
                case 2215: 
                case 2216: {
                    return 2;
                }
                case 2217: 
                case 2218: {
                    return 3;
                }
            }
            return 0;
        }
        if (JobConstants.is影武者(job)) {
            return job - 429;
        }
        if (JobConstants.is神之子(job)) {
            if (skillId > 0) {
                return skillId % 1000 / 100 == 1 ? 1 : 0;
            }
            return 0;
        }
        if (JobConstants.notNeedSPSkill(job) || job % 100 == 0) {
            return 0;
        }
        switch (job) {
            case 301: 
            case 501: 
            case 508: 
            case 3101: {
                return 0;
            }
        }
        return job % 10 + 1;
    }

    public static int getBOF_ForJob(int job) {
        return SkillConstants.getSkillByJob(12, job);
    }

    public static int getEmpress_ForJob(int job) {
        return SkillConstants.getSkillByJob(73, job);
    }

    public static String getJobBasicNameById(int job) {
        if (JobConstants.is劍士(job)) {
            return "劍士";
        }
        if (JobConstants.is法師(job)) {
            return "法師";
        }
        if (JobConstants.is弓箭手(job)) {
            return "弓箭手";
        }
        if (JobConstants.is盜賊(job)) {
            return "盜賊";
        }
        if (JobConstants.is海盜(job)) {
            return "海盜";
        }
        return "初心者";
    }

    public static boolean isSeparatedSpJob(int job) {
        return !JobConstants.is管理員(job) && !JobConstants.is皮卡啾(job) && !JobConstants.is雪吉拉(job);
    }

    public static int getLevelUpMp(int job, boolean b) {
        int mp = JobConstants.getJobStat(job, b)[3];
        return mp;
    }

    public static int getLevelUpHp(int job, int level, boolean b) {
        int hp = JobConstants.getJobStat(job, b)[0];
        return hp;
    }

    private static int[] getJobStat(int job, boolean b) {
        int n3;
        int n2 = job / 1000;
        int jobGrade = JobConstants.getJobBranch(job);
        int[] array = null;
        int n = n3 = b ? 0 : 1;
        if (jobGrade <= 9) {
            array = JobStats[jobGrade][n3];
            if (job / 10 == 53 || job == 501) {
                array = JobStats[6][n3];
            } else if (JobConstants.is夜光(job)) {
                array = JobStats[15][n3];
            } else if (n2 == 2) {
                switch (jobGrade) {
                    case 1: {
                        array = JobStats[11][n3];
                        break;
                    }
                    case 2: {
                        array = JobStats[12][n3];
                        break;
                    }
                    case 3: {
                        array = JobStats[13][n3];
                        break;
                    }
                    case 4: {
                        array = JobStats[14][n3];
                    }
                }
            } else if (JobConstants.is卡蒂娜(job)) {
                array = JobStats[11][n3];
            } else if (job / 100 == 32) {
                array = JobStats[16][n3];
            } else if (JobConstants.is狂豹獵人(job)) {
                array = JobStats[17][n3];
            } else if (JobConstants.is機甲戰神(job)) {
                array = JobStats[18][n3];
            } else if (JobConstants.is惡魔殺手(job)) {
                array = JobStats[19][n3];
            } else if (JobConstants.is天使破壞者(job)) {
                array = JobStats[20][n3];
            } else if (JobConstants.is惡魔復仇者(job)) {
                array = JobStats[21][n3];
            } else if (JobConstants.is傑諾(job)) {
                array = JobStats[22][n3];
            } else if (JobConstants.is神之子(job)) {
                array = JobStats[23][n3];
            } else if (JobConstants.is凱內西斯(job) || JobConstants.is伊利恩(job)) {
                array = JobStats[16][n3];
            } else if (JobConstants.is爆拳槍神(job)) {
                array = JobStats[1][n3];
            }
        }
        return array;
    }

    public static boolean hasDecorate(int job) {
        return JobConstants.is惡魔(job) || JobConstants.is傑諾(job) || JobConstants.is亞克(job) || JobConstants.is虎影(job);
    }

    public static boolean isDexPirate(int job) {
        return JobConstants.is槍神(job) || JobConstants.is機甲戰神(job) || JobConstants.is天使破壞者(job) || JobConstants.is墨玄(job);
    }

    static {
        JobConstants.JobStats[0][0] = new int[]{12, 16, 0, 10, 12, 0};
        JobConstants.JobStats[0][1] = new int[]{8, 12, 0, 6, 8, 15};
        JobConstants.JobStats[1][0] = new int[]{64, 68, 0, 4, 6, 0};
        JobConstants.JobStats[1][1] = new int[]{50, 54, 0, 2, 4, 15};
        JobConstants.JobStats[2][0] = new int[]{10, 14, 0, 22, 24, 0};
        JobConstants.JobStats[2][1] = new int[]{6, 10, 0, 18, 20, 15};
        JobConstants.JobStats[3][0] = new int[]{20, 24, 0, 14, 16, 0};
        JobConstants.JobStats[3][1] = new int[]{16, 20, 0, 10, 12, 15};
        JobConstants.JobStats[4][0] = new int[]{20, 24, 0, 14, 16, 0};
        JobConstants.JobStats[4][1] = new int[]{16, 20, 0, 10, 12, 15};
        JobConstants.JobStats[5][0] = new int[]{22, 26, 0, 18, 22, 0};
        JobConstants.JobStats[5][1] = new int[]{18, 20, 0, 14, 16, 15};
        JobConstants.JobStats[6][0] = new int[]{37, 41, 0, 18, 22, 0};
        JobConstants.JobStats[6][1] = new int[]{28, 30, 0, 14, 16, 15};
        JobConstants.JobStats[7][0] = new int[]{0, 0, 0, 0, 0, 0};
        JobConstants.JobStats[7][1] = new int[]{0, 0, 0, 0, 0, 0};
        JobConstants.JobStats[8][0] = new int[]{0, 0, 0, 0, 0, 0};
        JobConstants.JobStats[8][1] = new int[]{0, 0, 0, 0, 0, 0};
        JobConstants.JobStats[9][0] = new int[]{0, 0, 0, 0, 0, 0};
        JobConstants.JobStats[9][1] = new int[]{0, 0, 0, 0, 0, 0};
        JobConstants.JobStats[10][0] = new int[]{20, 24, 0, 14, 16, 20};
        JobConstants.JobStats[10][1] = new int[]{16, 20, 0, 10, 12, 15};
        JobConstants.JobStats[11][0] = new int[]{44, 48, 0, 4, 8, 0};
        JobConstants.JobStats[11][1] = new int[]{30, 34, 0, 2, 4, 15};
        JobConstants.JobStats[12][0] = new int[]{16, 20, 0, 35, 39, 0};
        JobConstants.JobStats[12][1] = new int[]{12, 16, 0, 21, 25, 15};
        JobConstants.JobStats[13][0] = new int[]{20, 24, 0, 14, 16, 0};
        JobConstants.JobStats[13][1] = new int[]{16, 20, 0, 10, 12, 15};
        JobConstants.JobStats[14][0] = new int[]{20, 24, 0, 14, 16, 0};
        JobConstants.JobStats[14][1] = new int[]{16, 20, 0, 10, 12, 15};
        JobConstants.JobStats[15][0] = new int[]{16, 20, 0, 198, 200, 0};
        JobConstants.JobStats[15][1] = new int[]{12, 16, 0, 21, 25, 15};
        JobConstants.JobStats[16][0] = new int[]{34, 38, 0, 22, 24, 0};
        JobConstants.JobStats[16][1] = new int[]{20, 24, 0, 18, 20, 15};
        JobConstants.JobStats[17][0] = new int[]{20, 24, 0, 14, 16, 0};
        JobConstants.JobStats[17][1] = new int[]{16, 20, 0, 10, 12, 15};
        JobConstants.JobStats[18][0] = new int[]{22, 26, 0, 18, 22, 0};
        JobConstants.JobStats[18][1] = new int[]{18, 20, 0, 14, 16, 15};
        JobConstants.JobStats[19][0] = new int[]{52, 56, 0, 0, 0, 0};
        JobConstants.JobStats[19][1] = new int[]{38, 40, 0, 0, 0, 0};
        JobConstants.JobStats[20][0] = new int[]{28, 32, 0, 0, 0, 0};
        JobConstants.JobStats[20][1] = new int[]{24, 26, 0, 0, 0, 0};
        JobConstants.JobStats[21][0] = new int[]{15, 15, 0, 0, 0, 0};
        JobConstants.JobStats[21][1] = new int[]{15, 15, 0, 0, 0, 0};
        JobConstants.JobStats[22][0] = new int[]{20, 24, 0, 14, 16, 0};
        JobConstants.JobStats[22][1] = new int[]{16, 20, 0, 10, 12, 15};
        JobConstants.JobStats[23][0] = new int[]{64, 68, 0, 0, 0, 0};
        JobConstants.JobStats[23][1] = new int[]{50, 54, 0, 0, 0, 0};
    }
}

