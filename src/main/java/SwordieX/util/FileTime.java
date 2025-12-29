/*
 * Decompiled with CFR 0.152.
 */
package SwordieX.util;

import connection.OutPacket;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class FileTime
implements Serializable {
    private int id;
    private int lowDateTime;
    private int highDateTime;
    private boolean isConvertedForClient;

    public long getLongValue() {
        return (long)this.getLowDateTime() + (long)this.getHighDateTime() << 32;
    }

    public boolean isConvertedForClient() {
        return this.isConvertedForClient;
    }

    public void setConvertedForClient(boolean convertedForClient) {
        this.isConvertedForClient = convertedForClient;
    }

    public FileTime(int lowDateTime, int highDateTime) {
        this.lowDateTime = lowDateTime;
        this.highDateTime = highDateTime;
    }

    public FileTime() {
    }

    public FileTime(long time, boolean isConvertedForClient) {
        this(time);
        this.isConvertedForClient = isConvertedForClient;
    }

    public FileTime deepCopy() {
        return new FileTime(this.getLowDateTime(), this.getHighDateTime());
    }

    public static FileTime fromType(Type type) {
        return new FileTime(type.getVal(), true);
    }

    public FileTime(long time) {
        this.lowDateTime = (int)time;
        this.highDateTime = (int)(time >>> 32);
    }

    public FileTime addDays(int day) {
        return new FileTime(this.toLong() + (long)(day * 24 * 60 * 60) * 1000L);
    }

    public int getLowDateTime() {
        return this.lowDateTime;
    }

    public int getHighDateTime() {
        return this.highDateTime;
    }

    public static FileTime currentTime() {
        return FileTime.fromEpochMillis(System.currentTimeMillis());
    }

    public static FileTime fromEpochMillis(long time) {
        return FileTime.fromLong(time);
    }

    public FileTime toClientFormat() {
        FileTime ft = FileTime.fromLong((this.toLong() - 116444736000000000L) * 100000L);
        ft.setConvertedForClient(true);
        return ft;
    }

    public static LocalDateTime fromDate(LocalDateTime localDateTime) {
        return FileTime.fromEpochMillis(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).toLocalDateTime();
    }

    public Instant toInstant() {
        return this.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant();
    }

    public long toMillis() {
        if (this.isConvertedForClient()) {
            return (this.toLong() - 116444736000000000L) / 10000L;
        }
        return this.toLong();
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.toMillis()), ZoneId.systemDefault());
    }

    public static FileTime fromLong(long value) {
        return new FileTime((int)value, (int)(value >>> 32));
    }

    public void encode(OutPacket outPacket) {
        if (!this.isConvertedForClient()) {
            outPacket.encodeLong(this.toLong());
        } else {
            outPacket.encodeInt(this.getHighDateTime());
            outPacket.encodeInt(this.getLowDateTime());
        }
    }

    public long toLong() {
        return (long)this.getLowDateTime() & 0xFFFFFFFFL | (long)this.getHighDateTime() << 32;
    }

    public boolean isExpired() {
        return !this.isPermanent() && this.toMillis() < System.currentTimeMillis();
    }

    private boolean isPermanent() {
        return this.equals(FileTime.fromType(Type.MAX_TIME)) || this.equals(new FileTime(21968699, -35635200));
    }

    public boolean isBefore(LocalDateTime localDateTime) {
        return this.toLocalDateTime().isBefore(localDateTime);
    }

    public boolean isBefore(FileTime fileTime) {
        return this.toLocalDateTime().isBefore(fileTime.toLocalDateTime());
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FileTime fileTime = (FileTime)o;
        return this.lowDateTime == fileTime.lowDateTime && this.highDateTime == fileTime.highDateTime;
    }

    public int hashCode() {
        return Objects.hash(this.lowDateTime, this.highDateTime);
    }

    public String toString() {
        return "FileTime{lowDateTime=" + this.lowDateTime + ", highDateTime=" + this.highDateTime + "}";
    }

    public boolean isMaxTime() {
        return this.equals(FileTime.fromType(Type.MAX_TIME));
    }

    public boolean isMinTime() {
        return this.equals(FileTime.fromType(Type.ZERO_TIME));
    }

    public String toSqlFormat() {
        if (this.isMaxTime()) {
            return "9999-01-01 00:00:01.000";
        }
        if (this.isMinTime()) {
            return "1970-01-01 00:00:01.000";
        }
        LocalDateTime ldt = this.toLocalDateTime();
        return String.format("%04d-%02d-%02d %02d:%02d:%02d.%03d", ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute(), ldt.getSecond(), 0);
    }

    public int toYYMMDDintValue() {
        return Long.valueOf(this.toYYMMDD().replaceAll("/", "")).intValue();
    }

    public int toYYMMDDHHintValue() {
        return Long.valueOf(this.toYYMMDDHH()).intValue();
    }

    public int toYYYYMMDDintValue() {
        return Long.valueOf(this.toYYYYMMDD()).intValue();
    }

    public String toYYMMDD() {
        if (this.isMaxTime()) {
            return "99/01/01";
        }
        if (this.isMinTime()) {
            return "70/01/01";
        }
        LocalDateTime ldt = this.toLocalDateTime();
        return String.format("%02d/%02d/%02d", ldt.getYear() % 100, ldt.getMonthValue(), ldt.getDayOfMonth());
    }

    public String toYYMMDDHH() {
        if (this.isMaxTime()) {
            return "99010101";
        }
        if (this.isMinTime()) {
            return "70010101";
        }
        LocalDateTime ldt = this.toLocalDateTime();
        return String.format("%02d%02d%02d%02d", ldt.getYear() % 100, ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour());
    }

    public String toYYYYMMDD() {
        if (this.isMaxTime()) {
            return "99991231";
        }
        if (this.isMinTime()) {
            return "00010101";
        }
        LocalDateTime ldt = this.toLocalDateTime();
        return String.format("%04d%02d%02d", ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth());
    }

    public String toYYMMDDHHMMSS() {
        if (this.isMaxTime()) {
            return "990101010101";
        }
        if (this.isMinTime()) {
            return "700101010101";
        }
        LocalDateTime ldt = this.toLocalDateTime();
        return String.format("%02d%02d%02d%02d%02d%02d", ldt.getYear() % 100, ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute(), ldt.getSecond());
    }

    public static enum Type {
        MAX_TIME(35120710, -1157267456),
        ZERO_TIME(21968699, -35635200),
        PERMANENT(150841440000000000L),
        FT_UT_OFFSET(116444592000000000L),
        QUEST_TIME(27111903L),
        PLAIN_ZERO(0L);

        private long val;

        private Type(long val) {
            this.val = val;
        }

        private Type(int lowPart, int highPart) {
            this.val = (long)lowPart + ((long)highPart << 32);
        }

        public long getVal() {
            return this.val;
        }
    }
}

