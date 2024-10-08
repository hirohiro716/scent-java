package com.hirohiro716.scent.datetime;

import com.hirohiro716.scent.StringObject;

/**
 * 期間のクラス。
 * 
 * @author hiro
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
