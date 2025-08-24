package com.hirohiro716.scent.datetime;

import com.hirohiro716.scent.StringObject;

/**
 * 期間のクラス。
 */
public class Span {
    
    /**
     * コンストラクタ。
     * 
     * @param startDatetime
     * @param endDatetime
     */
    public Span(Datetime startDatetime, Datetime endDatetime) {
        this.startDatetime = startDatetime;
        this.endDatetime =  endDatetime;
    }
    
    Datetime startDatetime;
    
    /**
     * 開始日時のDatetimeを取得する。
     * 
     * @return
     */
    public Datetime getStartDatetime() {
        return this.startDatetime;
    }
    
    Datetime endDatetime;
    
    /**
     * 終了日時のDatetimeを取得する。
     * 
     * @return
     */
    public Datetime getEndDatetime() {
        return this.endDatetime;
    }

    /**
     * ミリ秒数で取得する。
     * 
     * @returns
     */
    public long toMilliseconds() {
        return this.endDatetime.getAllMilliSecond() - this.startDatetime.getAllMilliSecond();
    }

    /**
     * 秒数で取得する。
     * 
     * @returns
     */
    public double toSeconds() {
        return this.toMilliseconds() / 1000d;
    }
    
    /**
     * 分数で取得する。
     * 
     * @returns
     */
    public double toMinutes() {
        return this.toMilliseconds() / 1000d / 60d;
    }

    /**
     * 時間数で取得する。
     * 
     * @returns
     */
    public double toHours() {
        return this.toMilliseconds() / 1000d / 60d / 60d;
    }

    /**
     * 日数で取得する。
     * 
     * @returns
     */
    public double toDays() {
        return this.toMilliseconds() / 1000d / 60d / 60d / 24d;
    }

    /**
     * 月度で取得する。
     * 
     * @param monthlyNameBaseDay
     * @return
     */
    public YearAndMonth toYearAndMonth(MonthlyNameBaseDay monthlyNameBaseDay) {
        if (monthlyNameBaseDay != null) {
            switch (monthlyNameBaseDay) {
                case START_DAY:
                    return new YearAndMonth(this.getStartDatetime().getYear(), this.getStartDatetime().getMonth());
                case LAST_DAY:
                    return new YearAndMonth(this.getEndDatetime().getYear(), this.getEndDatetime().getMonth());
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return StringObject.joinWithSeparator(new Object[] {this.startDatetime.toString(), this.endDatetime.toString()}, " - ").toString();
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Span == false) {
            return false;
        }
        Span span = (Span) obj; 
        return  this.getStartDatetime().getDate().getTime() == span.getStartDatetime().getDate().getTime()
                && this.getEndDatetime().getDate().getTime() == span.getEndDatetime().getDate().getTime();
    }

    /**
     * 年月のクラス。
     */
    public static class YearAndMonth {
        
        /**
         * コンストラクタ。<br>
         * 年月を指定する。
         * 
         * @param year
         * @param month
         */
        public YearAndMonth(int year, int month) {
            this.year = year;
            this.month = month;
        }

        private int year;

        /**
         * 年を取得する。
         * 
         * @return
         */
        public int getYear() {
            return this.year;
        }

        private int month;

        /**
         * 月を取得する。
         * 
         * @return
         */
        public int getMonth() {
            return this.month;
        }
    }
}
