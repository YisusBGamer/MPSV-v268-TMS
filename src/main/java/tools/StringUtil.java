/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtil {
    private static final Pattern NaturalNumberPattern = Pattern.compile("^[0-9]+$");

    public static String getLeftPaddedStr(String in, char padchar, int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int x = in.getBytes().length; x < length; ++x) {
            builder.append(padchar);
        }
        builder.append(in);
        return builder.toString();
    }

    public static String getRightPaddedStr(int in, char padchar, int length) {
        return StringUtil.getRightPaddedStr(String.valueOf(in), padchar, length);
    }

    public static String getRightPaddedStr(long in, char padchar, int length) {
        return StringUtil.getRightPaddedStr(String.valueOf(in), padchar, length);
    }

    public static String getRightPaddedStr(String in, char padchar, int length) {
        StringBuilder builder = new StringBuilder(in);
        for (int x = in.getBytes().length; x < length; ++x) {
            builder.append(padchar);
        }
        return builder.toString();
    }

    public static String joinStringFrom(String[] arr, int start) {
        return StringUtil.joinStringFrom(arr, start, " ");
    }

    public static String joinStringFrom(String[] arr, int start, String sep) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; ++i) {
            builder.append(arr[i]);
            if (i == arr.length - 1) continue;
            builder.append(sep);
        }
        return builder.toString();
    }

    public static String makeEnumHumanReadable(String enumName) {
        StringBuilder builder = new StringBuilder(enumName.length() + 1);
        for (String word : enumName.split("_")) {
            if (word.length() <= 2) {
                builder.append(word);
            } else {
                builder.append(word.charAt(0));
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        return builder.substring(0, enumName.length());
    }

    public static int countCharacters(String str, char chr) {
        int ret = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) != chr) continue;
            ++ret;
        }
        return ret;
    }

    public static String getReadableMillis(long startMillis, long endMillis) {
        StringBuilder sb = new StringBuilder();
        double elapsedSeconds = (double)(endMillis - startMillis) / 1000.0;
        int elapsedSecs = (int)elapsedSeconds % 60;
        int elapsedMinutes = (int)(elapsedSeconds / 60.0);
        int elapsedMins = elapsedMinutes % 60;
        int elapsedHrs = elapsedMinutes / 60;
        int elapsedHours = elapsedHrs % 24;
        int elapsedDays = elapsedHrs / 24;
        if (elapsedDays > 0) {
            boolean mins = elapsedHours > 0;
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedDays), '0', 2));
            sb.append("天");
            if (mins) {
                boolean secs = elapsedMins > 0;
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedHours), '0', 2));
                sb.append("时");
                if (secs) {
                    boolean millis = elapsedSecs > 0;
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedMins), '0', 2));
                    sb.append("分");
                    if (millis) {
                        sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedSecs), '0', 2));
                        sb.append("秒");
                    }
                }
            }
        } else if (elapsedHours > 0) {
            boolean mins = elapsedMins > 0;
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedHours), '0', 2));
            sb.append("时");
            if (mins) {
                boolean secs = elapsedSecs > 0;
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedMins), '0', 2));
                sb.append("分");
                if (secs) {
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedSecs), '0', 2));
                    sb.append("秒");
                }
            }
        } else if (elapsedMinutes > 0) {
            boolean secs = elapsedSecs > 0;
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedMins), '0', 2));
            sb.append("分");
            if (secs) {
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedSecs), '0', 2));
                sb.append("秒");
            }
        } else if (elapsedSeconds > 0.0) {
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(elapsedSecs), '0', 2));
            sb.append("秒");
        } else {
            sb.append("None.");
        }
        return sb.toString();
    }

    public static int[] StringtoInt(String str, String separator) {
        StringTokenizer strTokens = new StringTokenizer(str, separator);
        int[] strArray = new int[strTokens.countTokens()];
        int i = 0;
        while (strTokens.hasMoreTokens()) {
            strArray[i] = Integer.parseInt(strTokens.nextToken().trim());
            ++i;
        }
        return strArray;
    }

    public static boolean[] StringtoBoolean(String str, String separator) {
        StringTokenizer strTokens = new StringTokenizer(str, separator);
        boolean[] strArray = new boolean[strTokens.countTokens()];
        int i = 0;
        while (strTokens.hasMoreTokens()) {
            strArray[i] = Boolean.parseBoolean(strTokens.nextToken().trim());
            ++i;
        }
        return strArray;
    }

    public static boolean isNumber(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static boolean isNaturalNumber(String str) {
        return NaturalNumberPattern.matcher(str).matches();
    }

    public static String codeString(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = "GBK";
        switch (p) {
            case 61371: {
                code = "UTF-8";
                return code;
            }
            case 65534: {
                code = "Unicode";
                return code;
            }
            case 65279: {
                code = "UTF-16BE";
                return code;
            }
            case 23669: {
                code = "ANSI|ASCII";
                return code;
            }
        }
        return code;
    }

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.replaceAll("\\s", "").equals("");
    }
}

