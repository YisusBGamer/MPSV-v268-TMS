/*
 * Decompiled with CFR 0.152.
 */
package Net.server.cashshop;

import Config.constants.enums.CashItemModFlag;
import Net.server.cashshop.CashItemFactory;

public class CashItemInfo {
    private final int itemId;
    private final int count;
    private final int price;
    private final int meso;
    private final int originalPrice;
    private final int sn;
    private final int period;
    private final int gender;
    private final byte csClass;
    private final byte priority;
    private final int termStart;
    private final int termEnd;
    private final boolean onSale;
    private final boolean bonus;
    private final boolean refundable;
    private final boolean discount;
    private final int mileageRate;
    private final boolean onlyMileage;
    private final int LimitMax;
    private final String wzName;

    public CashItemInfo(String wzName, int itemId, int count, int price, int originalPrice, int meso, int sn, int period, int gender, byte csClass, byte priority, int termStart, int termEnd, boolean sale, boolean bonus, boolean refundable, boolean discount, int mileageRate, boolean onlyMileage, int LimitMax) {
        this.wzName = wzName;
        this.itemId = itemId;
        this.count = count;
        this.price = price;
        this.originalPrice = originalPrice;
        this.meso = meso;
        this.sn = sn;
        this.period = period;
        this.gender = gender;
        this.csClass = csClass;
        this.priority = priority;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.onSale = sale;
        this.bonus = bonus;
        this.refundable = refundable;
        this.discount = discount;
        this.mileageRate = mileageRate;
        this.onlyMileage = onlyMileage;
        this.LimitMax = LimitMax;
    }

    public String getName() {
        return this.wzName;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getCount() {
        return this.count;
    }

    public int getPrice() {
        return this.price;
    }

    public int getOriginalPrice() {
        return this.originalPrice;
    }

    public int getSN() {
        return this.sn;
    }

    public int getPeriod() {
        return this.period;
    }

    public int getGender() {
        return this.gender;
    }

    public boolean onSale() {
        CashModInfo modInfo = CashItemFactory.getInstance().getModInfo(this.sn);
        if (modInfo != null) {
            return modInfo.showUp;
        }
        return this.onSale;
    }

    public boolean genderEquals(int g) {
        return g == this.gender || this.gender == 2;
    }

    public boolean isBonus() {
        return this.bonus;
    }

    public boolean isRefundable() {
        return this.refundable;
    }

    public boolean isDiscount() {
        return this.discount;
    }

    public int getMeso() {
        return this.meso;
    }

    public byte getCsClass() {
        return this.csClass;
    }

    public byte getPriority() {
        return this.priority;
    }

    public int getTermStart() {
        return this.termStart;
    }

    public int getTermEnd() {
        return this.termEnd;
    }

    public int getMileageRate() {
        return this.mileageRate;
    }

    public boolean isOnlyMileage() {
        return this.onlyMileage;
    }

    public int getLimitMax() {
        return this.LimitMax;
    }

    public static class CashModInfo {
        private int price;
        private int originalPrice;
        private int mark;
        private int priority;
        private int sn;
        private int itemid;
        private int period;
        private int gender;
        private int count;
        private int meso;
        private int csClass;
        private int termStart;
        private int termEnd;
        private int fameLimit;
        private int levelLimit;
        private int categories;
        private long flags;
        private boolean showUp;
        private boolean packagez;
        private boolean base_new;
        private CashItemInfo cii;

        public CashModInfo(int sn, int price, int mark, boolean show, int itemid, int priority, boolean packagez, int period, int gender, int count, int meso, int csClass, int termStart, int termEnd, int fameLimit, int levelLimit, int categories, boolean base_new) {
            this.sn = sn;
            this.itemid = itemid;
            this.price = price;
            this.originalPrice = 0;
            this.mark = mark;
            this.showUp = show;
            this.priority = priority;
            this.packagez = packagez;
            this.period = period;
            this.gender = gender;
            this.count = count;
            this.meso = meso;
            this.csClass = csClass;
            this.termStart = termStart;
            this.termEnd = termEnd;
            this.flags = 0L;
            this.fameLimit = fameLimit;
            this.levelLimit = levelLimit;
            this.categories = categories;
            this.base_new = base_new;
            if (this.itemid > 0) {
                this.flags |= CashItemModFlag.ITEM_ID.getValue();
            }
            if (this.count > 0) {
                this.flags |= CashItemModFlag.COUNT.getValue();
            }
            if (this.price > 0) {
                this.flags |= CashItemModFlag.PRICE.getValue();
            }
            if (this.csClass > 0) {
                this.flags |= CashItemModFlag.BONUS.getValue();
            }
            if (this.priority >= 0) {
                this.flags |= CashItemModFlag.PRIORITY.getValue();
            }
            if (this.period > 0) {
                this.flags |= CashItemModFlag.PERIOD.getValue();
            }
            if (this.meso > 0) {
                this.flags |= CashItemModFlag.MESO.getValue();
            }
            if (this.gender >= 0) {
                this.flags |= CashItemModFlag.COMMODITY_GENDER.getValue();
            }
            this.flags |= CashItemModFlag.ON_SALE.getValue();
            if (this.mark >= -1 || this.mark <= 15) {
                this.flags |= CashItemModFlag.CLASS.getValue();
            }
            if (this.fameLimit > 0) {
                this.flags |= CashItemModFlag.REQ_POP.getValue();
            }
            if (this.levelLimit > 0) {
                this.flags |= CashItemModFlag.REQ_LEV.getValue();
            }
            if (this.termStart > 0) {
                this.flags |= CashItemModFlag.TERM_START.getValue();
            }
            if (this.termEnd > 0) {
                this.flags |= CashItemModFlag.TERM_END.getValue();
            }
            if (this.categories > 0) {
                this.flags |= CashItemModFlag.CATEGORY_INFO.getValue();
            }
        }

        public int getPrice() {
            return this.price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getOriginalPrice() {
            return this.originalPrice;
        }

        public void setOriginalPrice(int originalPrice) {
            this.originalPrice = originalPrice;
        }

        public int getMark() {
            return this.mark;
        }

        public void setMark(int mark) {
            this.mark = mark;
        }

        public int getPriority() {
            return this.priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getSn() {
            return this.sn;
        }

        public void setSn(int sn) {
            this.sn = sn;
        }

        public int getItemid() {
            return this.itemid;
        }

        public void setItemid(int itemid) {
            this.itemid = itemid;
        }

        public long getFlags() {
            return this.flags;
        }

        public void setFlags(long flags) {
            this.flags = flags;
        }

        public int getPeriod() {
            return this.period;
        }

        public void setPeriod(int period) {
            this.period = period;
        }

        public int getGender() {
            return this.gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getMeso() {
            return this.meso;
        }

        public void setMeso(int meso) {
            this.meso = meso;
        }

        public int getCsClass() {
            return this.csClass;
        }

        public void setCsClass(int csClass) {
            this.csClass = csClass;
        }

        public int getTermStart() {
            return this.termStart;
        }

        public void setTermStart(int termStart) {
            this.termStart = termStart;
        }

        public int getTermEnd() {
            return this.termEnd;
        }

        public void setTermEnd(int termEnd) {
            this.termEnd = termEnd;
        }

        public int getFameLimit() {
            return this.fameLimit;
        }

        public void setFameLimit(int fameLimit) {
            this.fameLimit = fameLimit;
        }

        public int getLevelLimit() {
            return this.levelLimit;
        }

        public void setLevelLimit(int levelLimit) {
            this.levelLimit = levelLimit;
        }

        public int getCategories() {
            return this.categories;
        }

        public void setCategories(int categories) {
            this.categories = categories;
        }

        public boolean isShowUp() {
            return this.showUp;
        }

        public void setShowUp(boolean showUp) {
            this.showUp = showUp;
        }

        public boolean isPackagez() {
            return this.packagez;
        }

        public void setPackagez(boolean packagez) {
            this.packagez = packagez;
        }

        public boolean isBase_new() {
            return this.base_new;
        }

        public void setBase_new(boolean base_new) {
            this.base_new = base_new;
        }

        public CashItemInfo getCii() {
            return this.cii;
        }

        public void setCii(CashItemInfo cii) {
            this.cii = cii;
        }

        public CashItemInfo toCItem(CashItemInfo backup) {
            int price;
            if (this.cii != null) {
                return this.cii;
            }
            int item = this.itemid <= 0 ? backup.getItemId() : this.itemid;
            int c = this.count <= 0 ? backup.getCount() : this.count;
            if (this.meso <= 0) {
                price = this.price <= 0 ? backup.price : this.price;
                if (this.price > 0 && (backup.originalPrice > 0 ? backup.originalPrice : backup.price) < this.price) {
                    this.flags |= CashItemModFlag.ORIGINAL_PRICE.getValue();
                    this.originalPrice = backup.price > 0 && backup.originalPrice > 0 && backup.originalPrice > backup.price ? price * backup.originalPrice / backup.price : price;
                } else {
                    this.originalPrice = backup.originalPrice;
                }
            } else {
                price = this.meso;
                this.originalPrice = backup.originalPrice;
            }
            int expire = this.period <= 0 ? backup.getPeriod() : this.period;
            int gen = this.gender < 0 ? backup.getGender() : this.gender;
            boolean onSale = !this.showUp ? backup.onSale() : this.showUp;
            this.cii = new CashItemInfo(backup.wzName, item, c, price, this.originalPrice, this.meso, this.sn, expire, gen, backup.csClass, backup.priority, backup.termStart, backup.termEnd, onSale, backup.bonus, backup.refundable, backup.discount, backup.mileageRate, backup.onlyMileage, backup.LimitMax);
            return this.cii;
        }
    }
}

