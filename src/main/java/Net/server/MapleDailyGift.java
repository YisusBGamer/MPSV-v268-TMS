/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.MapleDailyGift$SIGNIN_TYPE
 *  lombok.Generated
 */
package Net.server;

import Database.DatabaseConnection;
import Database.tools.SqlTool;
import Net.server.MapleDailyGift;
import Opcode.header.OutHeader;
import java.lang.invoke.LambdaMetafactory;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.Generated;
import tools.data.MaplePacketLittleEndianWriter;

public class MapleDailyGift {
    private static final Map<Integer, DailyGiftMonth> rewards = new HashMap<Integer, DailyGiftMonth>();

    public DailyGiftMonth getRewards() {
        return rewards.get(Calendar.getInstance().get(2) + 1);
    }

    public static void initialize() {
        rewards.clear();
        DatabaseConnection.domain(con -> {
            ResultSet rs = SqlTool.query(con, "SELECT * FROM `zdata_dailygifts`");
            while (rs.next()) {
                int id = rs.getInt("id");
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                int itemid = rs.getInt("itemid");
                int count = rs.getInt("count");
                int commodityid = rs.getInt("commodityid");
                int term = rs.getInt("term");
                rewards.computeIfAbsent(month, m -> new DailyGiftMonth(getDaysInMonth(month)))
                        .dailyGifts.put(day, new DailyGiftInfo(id, month, day, itemid, count, commodityid, term));
            }
            return null;
        }, "已經完成重新加載:[HAPPY_DAY].");
    }

    private static int getDaysInMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2, month - 1);
        return calendar.getActualMaximum(5);
    }

    public static byte[] dailyGiftResult(int n, int n2, int n3, int n4) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SIGIN_INFO.getValue());
        mplew.write(n);
        if (n > 0) {
            if (n > 1) {
                if (n == 2) {
                    mplew.writeInt(n2);
                    mplew.writeInt(n3);
                    if (n2 == 14) {
                        mplew.writeInt(n4);
                    }
                }
            } else {
                mplew.writeInt(0);
            }
        }
        return mplew.getPacket();
    }

    private static void addDailyGiftInfo(MaplePacketLittleEndianWriter mplew, DailyGiftMonth a1318) {
        mplew.writeInt(0);
    }

    public static byte[] getSigninReward(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.SIGIN_INFO.getValue());
        mplew.write(1);
        mplew.writeInt(SIGNIN_TYPE.領取獎勵.ordinal());
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static class DailyGiftMonth {
        Map<Integer, DailyGiftInfo> dailyGifts;

        public DailyGiftMonth(int daysInMonth) {
            this.dailyGifts = new HashMap<Integer, DailyGiftInfo>(daysInMonth);
        }
    }

    public static class DailyGiftInfo {
        int id;
        int month;
        int day;
        int itemid;
        int count;
        int commodityid;
        int term;

        public DailyGiftInfo(int id, int month, int day, int itemid, int count, int commodityid, int term) {
            this.id = id;
            this.month = month;
            this.day = day;
            this.itemid = itemid;
            this.count = count;
            this.commodityid = commodityid;
            this.term = term;
        }

        @Generated
        public int getId() {
            return this.id;
        }

        @Generated
        public int getMonth() {
            return this.month;
        }

        @Generated
        public int getDay() {
            return this.day;
        }

        @Generated
        public int getItemid() {
            return this.itemid;
        }

        @Generated
        public int getCount() {
            return this.count;
        }

        @Generated
        public int getCommodityid() {
            return this.commodityid;
        }

        @Generated
        public int getTerm() {
            return this.term;
        }
    }

    enum SIGNIN_TYPE {
        UNKNOWN,
        簽到窗口,
        領取獎勵,;
    }
}

