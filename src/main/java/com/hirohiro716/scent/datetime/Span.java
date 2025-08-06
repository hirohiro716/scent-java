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
}
