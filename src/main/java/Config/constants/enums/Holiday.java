/*
 * Decompiled with CFR 0.152.
 */
package Config.constants.enums;

public enum Holiday {
    None,
    ChineseNewYear(1, 1, true),
    Halloween(10, 31, false);

    private int _month;
    private int _dayOfMonth;
    private final boolean _isLunar;

    private Holiday() {
        this._month = 0;
        this._dayOfMonth = 0;
        this._isLunar = false;
    }

    private Holiday(int month, int dayOfMonth, boolean isLunar) {
        this._month = month;
        this._dayOfMonth = dayOfMonth;
        this._isLunar = isLunar;
    }

    public int getMonth() {
        return this._month;
    }

    public int getDayOfMonth() {
        return this._dayOfMonth;
    }

    public boolean isLunar() {
        return this._isLunar;
    }

    public void setMonth(int month) {
        this._month = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this._dayOfMonth = dayOfMonth;
    }
}

