package com.hirohiro716.datetime;

/**
 * 期間のクラス。
 * 
 * @author hiro
 *
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
     * @return 結果。
     */
    public Datetime getStartDatetime() {
        return this.startDatetime;
    }
    
    Datetime endDatetime;
    
    /**
     * 終了日時のDatetimeを取得する。
     * 
     * @return 結果。
     */
    public Datetime getEndDatetime() {
        return this.endDatetime;
    }
}
