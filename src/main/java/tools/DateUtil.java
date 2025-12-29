/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {
    private static final int ITEM_YEAR2000 = -1085019342;
    private static final long REAL_YEAR2000 = 946681229830L;
    private static final int QUEST_UNIXAGE = 27111908;
    private static final long FT_UT_OFFSET = 116444520000000000L;

    public static int getItemTimestamp(long realTimestamp) {
        int time = (int)((realTimestamp - 946681229830L) / 1000L / 60L);
        return (int)((double)time * 35.762787) + -1085019342;
    }

    public static int getQuestTimestamp(long realTimestamp) {
        int time = (int)(realTimestamp / 1000L / 60L);
        return (int)((double)time * 0.1396987) + 27111908;
    }

    public static boolean isDST() {
        return TimeZone.getDefault().inDaylightTime(new Date());
    }

    public static long getFileTimestamp(long timeStampinMillis) {
        return DateUtil.getFileTimestamp(timeStampinMillis, false);
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (DateUtil.isDST()) {
            timeStampinMillis -= 3600000L;
        }
        long time = roundToMinutes ? timeStampinMillis / 1000L / 60L * 600000000L : (timeStampinMillis += 50400000L) * 10000L;
        return time + 116444520000000000L;
    }

    public static int getIntDate() {
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", "");
        return Long.valueOf(time).intValue();
    }

    public static int getTime() {
        String time = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date()).replace("-", "");
        return Long.valueOf(time).intValue();
    }

    public static int getTime(long realTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        return Long.valueOf(sdf.format(realTimestamp)).intValue();
    }

    public static long getKoreanTimestamp(long realTimestamp) {
        return realTimestamp * 10000L + 116444592000000000L;
    }

    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒");
        return sdf.format(new Date());
    }

    public static int getSpecialNowiTime() {
        return (int)(System.currentTimeMillis() % 100000000L);
    }

    public static String getCurrentDate() {
        return DateUtil.getCurrentDate("yyyy-MM-dd");
    }

    public static String getCurrentDate(String dateFormat) {
        return DateUtil.getFormatDate(new Date(), dateFormat);
    }

    public static String getFormatDate(Object date) {
        return DateUtil.getFormatDate(date, "yyyy-MM-dd");
    }

    public static String getFormatDate(Object date, String dateFormat) {
        assert (date instanceof Date || date instanceof Number);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    public static String getPreDate(String field, int amount) {
        return DateUtil.getPreDate(new Date(), field, amount);
    }

    public static String getPreDate(Date d, String field, int amount) {
        String result = DateUtil.getPreTime(d, field, amount);
        if (result == null) {
            return null;
        }
        return result.split(" ")[0].replace("/", "-");
    }

    public static String getPreTime(String field, int amount) {
        return DateUtil.getPreTime(new Date(), field, amount);
    }

    public static String getPreTime(Date d, String field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        if (field != null && !field.equals("")) {
            switch (field) {
                case "y": {
                    calendar.add(1, amount);
                    break;
                }
                case "M": {
                    calendar.add(2, amount);
                    break;
                }
                case "d": {
                    calendar.add(5, amount);
                    break;
                }
                case "H": {
                    calendar.add(10, amount);
                    break;
                }
                case "m": {
                    calendar.add(12, amount);
                    break;
                }
                case "s": {
                    calendar.add(13, amount);
                }
            }
        } else {
            return null;
        }
        return DateUtil.getFormatDate(calendar.getTime(), "yyyy/MM/dd HH:mm:ss");
    }

    public static String getPreDate(String date) throws ParseException {
        Date d = new SimpleDateFormat().parse(date);
        String preD = DateUtil.getPreDate(d, "d", 1);
        Date preDate = new SimpleDateFormat().parse(preD);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(preDate);
    }

    public static long getStringToTime(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmm");
        try {
            Date date = df.parse(dateString);
            return date.getTime();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return -1L;
        }
    }

    public static long getStringToTime(String dateString, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            Date date = df.parse(dateString);
            return date.getTime();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return -1L;
        }
    }

    public static long getNextDayTime(int day) {
        Calendar date = Calendar.getInstance();
        date.set(date.get(1), date.get(2), date.get(5) + day, 0, 0, 0);
        return date.getTime().getTime();
    }

    public static long getNextDayDiff(int day) {
        Calendar date = Calendar.getInstance();
        date.set(date.get(1), date.get(2), date.get(5) + day, 0, 0);
        return date.getTime().getTime() - System.currentTimeMillis();
    }

    public static String getDayInt(int day) {
        if (day == 1) {
            return "SUN";
        }
        if (day == 2) {
            return "MON";
        }
        if (day == 3) {
            return "TUE";
        }
        if (day == 4) {
            return "WED";
        }
        if (day == 5) {
            return "THU";
        }
        if (day == 6) {
            return "FRI";
        }
        if (day == 7) {
            return "SAT";
        }
        return null;
    }

    public static int getDate() {
        String q = DateUtil.getTimeNow();
        return Integer.parseInt(q.substring(0, 4) + q.substring(5, 7) + q.substring(8, 10));
    }

    public static String getTimeNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static long getLastTimeOfMonth() {
        Calendar date = Calendar.getInstance();
        date.add(2, 1);
        date.set(5, 1);
        date.set(11, 6);
        date.set(12, 0);
        date.set(13, 0);
        date.set(14, 0);
        return date.getTimeInMillis();
    }
}

