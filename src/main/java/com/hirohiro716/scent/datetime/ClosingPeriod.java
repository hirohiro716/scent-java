package com.hirohiro716.scent.datetime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 締め日期間のクラス。
 */
public class ClosingPeriod {
    
    /**
     * コンストラクタ。<br>
     * 締める日を指定する。
     * 
     * @param days
     */
    public ClosingPeriod(Integer... days) {
        Arrays.sort(days);
        this.closingDays = days;
        this.baseDatetime = new Datetime();
        this.baseDatetime.modifyHour(0);
        this.baseDatetime.modifyMinute(0);
        this.baseDatetime.modifySecond(0);
        this.baseDatetime.modifyMillisecond(0);
    }
    
    /**
     * コンストラクタ。<br>
     * 締める日を指定する。
     * 
     * @param days
     */
    public ClosingPeriod(Collection<Integer> days) {
        this(days.toArray(new Integer[] {}));
    }
    
    private Integer[] closingDays;
    
    private Datetime baseDatetime;
    
    /**
     * 基準日を指定する。初期値は現在の時刻。
     * 
     * @param date
     */
    public void setBaseDate(Date date) {
        this.baseDatetime = new Datetime(date);
        this.baseDatetime.modifyHour(0);
        this.baseDatetime.modifyMinute(0);
        this.baseDatetime.modifySecond(0);
        this.baseDatetime.modifyMillisecond(0);
    }
    
    /**
     * 指定された日時を月末に変更する。
     * 
     * @param datetime
     */
    private void changeToLastDayOfMonth(Datetime datetime) {
        datetime.addMonth(1);
        datetime.modifyDay(1);
        datetime.addDay(-1);
    }
    
    /**
     * 指定された月数分、基準日の前後の期間を作成する。
     * 
     * @param numberOfMonths
     * @return
     */
    public Span[] createSpans(int numberOfMonths) {
        // Calculate the required closing date
        List<Long> timestampList = new ArrayList<>();
        for (int monthNumber = 0; monthNumber < numberOfMonths + 1; monthNumber++) {
            for (int closingDayIndex = 0; closingDayIndex < this.closingDays.length; closingDayIndex++) {
                int closingDay = this.closingDays[closingDayIndex];
                Datetime datetime = new Datetime(this.baseDatetime.getDate());
                if (monthNumber < numberOfMonths) {
                    Datetime forward = datetime.clone();
                    forward.addMonth(monthNumber);
                    if (closingDay >= 28) {
                        this.changeToLastDayOfMonth(forward);
                    } else {
                        forward.modifyDay(closingDay);
                    }
                    timestampList.add(forward.getDate().getTime());
                }
                if (monthNumber > 0) {
                    if (monthNumber < numberOfMonths || closingDayIndex == this.closingDays.length - 1) {
                        Datetime backward = datetime.clone();
                        backward.addMonth(monthNumber * -1);
                        if (closingDay >= 28) {
                            this.changeToLastDayOfMonth(backward);
                        } else {
                            backward.modifyDay(closingDay);
                        }
                        timestampList.add(backward.getDate().getTime());
                    }
                }
            }
        }
        // Sort
        Long[] timestamps = timestampList.toArray(new Long[] {});
        Arrays.sort(timestamps);
        // Create spans
        List<Span> spans = new ArrayList<>();
        for (int index = 0; index < timestamps.length; index++) {
            if (index == timestamps.length - 1) {
                break;
            }
            Datetime from = new Datetime(new Date(timestamps[index]));
            from.addDay(1);
            Datetime to = new Datetime(new Date(timestamps[index + 1]));
            to.modifyTime(23, 59, 59, 999);
            spans.add(new Span(from, to));
        }
        return spans.toArray(new Span[] {});
    }
    
    /**
     * 指定された日付が含まれる締め日期間を特定する。特定できなかった場合はnullを返す。
     * 
     * @param oneDate
     * @return
     */
    public Span findSpan(Date oneDate) {
        ClosingPeriod instance = new ClosingPeriod(this.closingDays);
        instance.setBaseDate(oneDate);
        for (Span span: instance.createSpans(2)) {
            if (span.getStartDatetime().getDate().getTime() <= oneDate.getTime() && oneDate.getTime() <= span.getEndDatetime().getDate().getTime()) {
                return span;
            }
        }
        return null;
    }
}
