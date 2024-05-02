package com.hirohiro716.scent.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hirohiro716.scent.IdentifiableEnum;
import com.hirohiro716.scent.StringObject;

/**
 * 年月日と時刻のクラス。
 * 
 * @author hiro
 *
 */
public class Datetime implements Cloneable {

    /**
     * 年月日だけを文字列で表す際のデフォルトフォーマットパターン。
     */
    public final static SimpleDateFormat DEFAULT_DATE_FORMAT_ONLY_DATE = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * 時刻だけを文字列で表す際のデフォルトフォーマットパターン。
     */
    public final static SimpleDateFormat DEFAULT_DATE_FORMAT_ONLY_TIME = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * 年月日と時刻を文字列で表す際のデフォルトフォーマットパターン。
     */
    public final static SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(StringObject.join(Datetime.DEFAULT_DATE_FORMAT_ONLY_DATE.toPattern(), " ", Datetime.DEFAULT_DATE_FORMAT_ONLY_TIME.toPattern()).toString());
    
    /**
     * コンストラクタ。<br>
     * 現在の時刻を初期値とする。
     */
    public Datetime() {
        this.calendar = (Calendar) Calendar.getInstance().clone();
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたjava.util.Dateの日時を初期値とする。
     * 
     * @param date
     * @throws NullPointerException 
     */
    public Datetime(Date date) throws NullPointerException {
        this();
        if (date == null) {
            throw new NullPointerException();
        }
        this.set(date);
    }
    
    /**
     * コンストラクタ。<br>
     * デフォルトフォーマットパターンに準じた形式の、指定された日時文字列を初期値とする。
     * 
     * @param datetimeString
     * @throws ParseException 
     */
    public Datetime(String datetimeString) throws ParseException {
        this();
        this.set(datetimeString);
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたフォーマットパターンに準じた形式の、日時文字列を初期値とする。
     * 
     * @param datetimeString
     * @param dateFormat
     * @throws ParseException 
     */
    public Datetime(String datetimeString, DateFormat dateFormat) throws ParseException {
        this();
        this.set(datetimeString, dateFormat);
    }
    
    private Calendar calendar;

    private DateFormat dateFormat = Datetime.DEFAULT_DATE_FORMAT;
    
    /**
     * このDatetimeの時刻に、指定されたjava.util.Dateの日時をセットする。
     * 
     * @param date
     */
    public void set(Date date) {
        this.calendar.setTime(date);
    }
    
    /**
     * このDatetimeの時刻を、指定された文字列からセットする。<br>
     * フォーマットパターンはコンストラクタ、setメソッドで指定していなければ、DEFAULT_FORMAT_PATTERNが使用される。
     * 
     * @param datetimeString
     * @throws ParseException 
     */
    public void set(String datetimeString) throws ParseException {
        try {
            this.calendar.setTime(this.dateFormat.parse(datetimeString));
        } catch (ParseException exception) {
            Date date = stringToDate(datetimeString);
            if (date != null) {
                this.set(date);
            } else {
                throw exception;
            }
        }
    }
    
    /**
     * このインスタンスの時刻を、指定された文字列からセットする。
     * 
     * @param datetimeString
     * @param dateFormat
     * @throws ParseException 
     */
    public void set(String datetimeString, DateFormat dateFormat) throws ParseException {
        this.dateFormat = dateFormat;
        this.set(datetimeString);
    }

    /**
     * このインスタンスの「年」を指定値に変更する。
     * 
     * @param year 西暦の年。
     */
    public void modifyYear(int year) {
        this.calendar.set(Calendar.YEAR, year);
    }
    
    /**
     * このインスタンスの「月」を指定値に変更する。
     * 
     * @param month 1から12の月。
     */
    public void modifyMonth(int month) {
        this.calendar.set(Calendar.MONTH, month - 1);
    }
    
    /**
     * このインスタンスの「日」を指定値に変更する。
     * 
     * @param day 1から31の日。
     */
    public void modifyDay(int day) {
        this.calendar.set(Calendar.DAY_OF_MONTH, day);
    }
    
    /**
     * このインスタンスの「時」を指定値に変更する。
     * 
     * @param hour 0から23の時。
     */
    public void modifyHour(int hour) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
    }
    
    /**
     * このインスタンスの「分」を指定値に変更する。
     * 
     * @param minute 0から59の分。
     */
    public void modifyMinute(int minute) {
        this.calendar.set(Calendar.MINUTE, minute);
    }
    
    /**
     * このインスタンスの「秒」を指定値に変更する。
     * 
     * @param second 0から59の秒。
     */
    public void modifySecond(int second) {
        this.calendar.set(Calendar.SECOND, second);
    }
    
    /**
     * このインスタンスの「ミリ秒」を指定値に変更する。
     * 
     * @param millisecond 0から999のミリ秒。
     */
    public void modifyMillisecond(int millisecond) {
        this.calendar.set(Calendar.MILLISECOND, millisecond);
    }
    
    /**
     * このインスタンスの年月日と時刻をセットする。
     * 
     * @param year 西暦の年。
     * @param month 1から12の月。
     * @param day 1から31の日。
     * @param hour 0から23の時。
     * @param minute 0から59の分。
     * @param second 0から59の秒。
     * @param millisecond 0から999のミリ秒。
     */
    public void set(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this.modifyYear(year);
        this.modifyMonth(month);
        this.modifyDay(day);
        this.modifyHour(hour);
        this.modifyMinute(minute);
        this.modifySecond(second);
        this.modifyMillisecond(millisecond);
    }

    /**
     * このインスタンスに年数を加算する。
     * 
     * @param year 加算する年数。
     */
    public void addYear(int year) {
        this.calendar.add(Calendar.YEAR, year);
    }
    
    /**
     * このインスタンスに月数を加算する。
     * 
     * @param month 加算する月数。
     */
    public void addMonth(int month) {
        this.calendar.add(Calendar.MONTH, month);
    }
    
    /**
     * このインスタンスに日数を加算する。
     * 
     * @param day 加算する日数。
     */
    public void addDay(int day) {
        this.calendar.add(Calendar.DAY_OF_MONTH, day);
    }
    
    /**
     * このインスタンスに時数を加算する。
     * 
     * @param hour 加算する時数。
     */
    public void addHour(int hour) {
        this.calendar.add(Calendar.HOUR, hour);
    }
    
    /**
     * このインスタンスに分数を加算する。
     * 
     * @param minute 加算する分数。
     */
    public void addMinute(int minute) {
        this.calendar.add(Calendar.MINUTE, minute);
    }
    
    /**
     * このインスタンスに秒数を加算する。
     * 
     * @param second 加算する秒数。
     */
    public void addSecond(int second) {
        this.calendar.add(Calendar.SECOND, second);
    }
    
    /**
     * このインスタンスにミリ秒数を加算する。
     * 
     * @param millisecond 加算するミリ秒数。
     */
    public void addMillisecond(int millisecond) {
        this.calendar.add(Calendar.MILLISECOND, millisecond);
    }
    
    /**
     * このインスタンスの「年」だけを西暦で取得する。
     * 
     * @return 結果。
     */
    public int getYear() {
        return this.calendar.get(Calendar.YEAR);
    }
    
    /**
     * このインスタンスの「月」だけを1から12の数値で取得する。
     * 
     * @return 結果。
     */
    public int getMonth() {
        return this.calendar.get(Calendar.MONTH) + 1;
    }
    
    /**
     * このインスタンスの「日」だけを1から31の数値で取得する。
     * 
     * @return 結果。
     */
    public int getDay() {
        return this.calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * このインスタンスの「時」だけを0から23の数値で取得する。
     * 
     * @return 結果。
     */
    public int getHour() {
        return this.calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * このインスタンスの「分」だけを0から59の数値で取得する。
     * 
     * @return 結果。
     */
    public int getMinute() {
        return this.calendar.get(Calendar.MINUTE);
    }
    
    /**
     * このインスタンスの秒だけを0から59の数値で取得する。
     * 
     * @return 結果。
     */
    public int getSecond() {
        return this.calendar.get(Calendar.SECOND);
    }

    /**
     * このインスタンスのミリ秒だけを数値で取得する。
     * 
     * @return 結果。
     */
    public int getMilliSecond() {
        return this.calendar.get(Calendar.MILLISECOND);
    }
    
    /**
     * このインスタンスの曜日を取得する。
     * 
     * @return 結果。
     */
    public DayOfWeek toDayOfWeek() {
        switch (this.calendar.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
            return DayOfWeek.SUNDAY;
        case Calendar.MONDAY:
            return DayOfWeek.MONDAY;
        case Calendar.TUESDAY:
            return DayOfWeek.TUESDAY;
        case Calendar.WEDNESDAY:
            return DayOfWeek.WEDNESDAY;
        case Calendar.THURSDAY:
            return DayOfWeek.THURSDAY;
        case Calendar.FRIDAY:
            return DayOfWeek.FRIDAY;
        case Calendar.SATURDAY:
            return DayOfWeek.SATURDAY;
        }
        return null;
    }
    
    /**
     * java.util.Dateのインスタンスを取得する。
     * 
     * @return 結果。
     */
    public Date getDate() {
        return this.calendar.getTime();
    }
    
    /**
     * このインスタンスの年月日と時刻を文字列にフォーマットした結果を取得する。
     * 
     * @param dateFormat
     * @return 結果。
     */
    public String toString(DateFormat dateFormat) {
        return dateFormat.format(this.calendar.getTime());
    }

    /**
     * このインスタンスの年月日と時刻を文字列にフォーマットした結果を取得する。<br>
     * フォーマットパターンはコンストラクタ、setメソッドで指定していなければ、DEFAULT_FORMAT_PATTERNが使用される。
     * 
     * @return 結果。
     */
    @Override
    public final String toString() {
        return this.toString(this.dateFormat);
    }

    @Override
    public int hashCode() {
        return String.valueOf(this.getDate().getTime()).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Date) {
            return this.equals((Date) object);
        }
        if (object instanceof Datetime) {
            return this.equals((Datetime) object);
        }
        return super.equals(object);
    }
    
    /**
     * このインスタンスと指定されたオブジェクトが等しい場合はtrueを返す。
     * 
     * @param date 
     * @return 結果。
     */
    public boolean equals(Date date) {
        if (date == null) {
            return false;
        }
        return this.calendar.getTime().equals(date);
    }

    /**
     * このインスタンスと指定されたインスタンスが等しい場合はtrueを返す。
     * 
     * @param datetime
     * @return 結果。
     */
    public boolean equals(Datetime datetime) {
        if (datetime == null) {
            return false;
        }
        return this.calendar.getTime().equals(datetime.getDate());
    }
    
    /**
     * このインスタンスと指定された年月日が等しい場合はtrueを返す。
     * 
     * @param year
     * @param month
     * @param day
     * @return 結果。
     */
    public boolean eqaulsDate(int year, int month, int day) {
        return (this.getYear() == year && this.getMonth() == month && this.getDay() == day);
    }
    
    /**
     * このインスタンスと指定されたインスタンスの年月日が等しい場合はtrueを返す。
     * 
     * @param datetime
     * @return 結果。
     */
    public boolean eqaulsDate(Datetime datetime) {
        if (datetime == null) {
            return false;
        }
        return this.eqaulsDate(datetime.getYear(), datetime.getMonth(), datetime.getDay());
    }

    /**
     * このインスタンスと指定された時刻が等しい場合はtrueを返す。
     * 
     * @param hour 
     * @param minute 
     * @param second 
     * @return 結果。
     */
    public boolean eqaulsTime(int hour, int minute, int second) {
        return (this.getHour() == hour && this.getMinute() == minute && this.getSecond() == second);
    }
    
    /**
     * このインスタンスと指定されたインスタンスの時刻が等しい場合はtrueを返す。
     * 
     * @param datetime
     * @return 結果。
     */
    public boolean eqaulsTime(Datetime datetime) {
        if (datetime == null) {
            return false;
        }
        return this.eqaulsTime(datetime.getHour(), datetime.getMinute(), datetime.getSecond());
    }
    
    @Override
    public Datetime clone() {
        return new Datetime(new Date(this.getDate().getTime()));
    }
    
    /**
     * このインスタンスの年月日を文字列にフォーマットした結果を取得する。<br>
     * フォーマットパターンは、DEFAULT_FORMAT_PATTERN_ONLY_DATEが使用される。
     * 
     * @return 結果。
     */
    public final String toStringOnlyDate() {
        return this.toString(Datetime.DEFAULT_DATE_FORMAT_ONLY_DATE);
    }
    
    /**
     * このインスタンスの時刻を文字列にフォーマットした結果を取得する。<br>
     * フォーマットパターンは、DEFAULT_FORMAT_PATTERN_ONLY_TIMEが使用される。
     * 
     * @return 結果。
     */
    public final String toStringOnlyTime() {
        return this.toString(Datetime.DEFAULT_DATE_FORMAT_ONLY_TIME);
    }
    
    /**
     * 下記の形式のいずれかのフォーマットパターンに準じた形式の文字列をjava.util.Dateに変換する。<br>
     * "yyyy/MM/dd"、"yyyy-MM-dd"、"yyyy/MM/dd HH:mm"、"yyyy-MM-dd HH:mm"、<br>
     * "yyyy/MM/dd HH:mm:ss"、"yyyy-MM-dd HH:mm:ss"、"HH:mm:ss"、"HH:mm"
     * 
     * @param datetimeString
     * @return java.util.Dateインスタンス。変換に失敗した場合はnull。
     */
    private Date stringToDate(String datetimeString) {
        try {
            SimpleDateFormat dateFormat = null;
            if (datetimeString.matches("^[0-9]{4}/[0-1]{1}[0-9]{1}/[0-3]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            }
            if (datetimeString.matches("^[0-9]{4}\\-[0-1]{1}[0-9]{1}\\-[0-3]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            if (datetimeString.toString().matches("^[0-9]{4}/[0-1]{1}[0-9]{1}/[0-3]{1}[0-9]{1} [0-2]{1}[0-9]{1}:[0-5]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            }
            if (datetimeString.toString().matches("^[0-9]{4}\\-[0-1]{1}[0-9]{1}\\-[0-3]{1}[0-9]{1} [0-2]{1}[0-9]{1}:[0-5]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
            if (datetimeString.toString().matches("^[0-9]{4}/[0-1]{1}[0-9]{1}/[0-3]{1}[0-9]{1} [0-2]{1}[0-9]{1}:[0-5]{1}[0-9]{1}:[0-5]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            }
            if (datetimeString.toString().matches("^[0-9]{4}\\-[0-1]{1}[0-9]{1}\\-[0-3]{1}[0-9]{1} [0-2]{1}[0-9]{1}:[0-5]{1}[0-9]{1}:[0-5]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            if (datetimeString.toString().matches("^[0-2]{1}[0-9]{1}:[0-5]{1}[0-9]{1}:[0-5]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("HH:mm:ss");
            }
            if (datetimeString.toString().matches("^[0-2]{1}[0-9]{1}:[0-5]{1}[0-9]{1}$")) {
                dateFormat = new SimpleDateFormat("HH:mm");
            }
            if (dateFormat != null) {
                return dateFormat.parse(datetimeString);
            }
        } catch (ParseException exception) {
        }
        return null;
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、現在の時刻を初期値とした新しいインスタンスを作成する。
     * 
     * @return 新しいインスタンス。
     */
    public static Datetime newInstance() {
        return new Datetime();
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、<br>
     * 指定されたjava.util.Dateの日時を初期値とした新しいインスタンスを作成する。
     * 
     * @param date
     * @return 新しいインスタンス。
     */
    public static Datetime newInstance(Date date) {
        return new Datetime(date);
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、<br>
     * デフォルトフォーマットパターンに準じた形式の、指定された日時文字列を初期値とした新しいインスタンスを作成する。
     * 
     * @param datetimeString 
     * @return 新しいインスタンス。
     * @throws ParseException 
     */
    public static Datetime newInstance(String datetimeString) throws ParseException {
        return new Datetime(datetimeString);
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、<br>
     * 指定されたフォーマットパターンに準じた形式の、日時文字列を初期値とした新しいインスタンスを作成する。
     * 
     * @param datetimeString 
     * @param formatPattern 
     * @return 新しいインスタンス。
     * @throws ParseException 
     */
    public static Datetime newInstance(String datetimeString, DateFormat formatPattern) throws ParseException {
        return new Datetime(datetimeString, formatPattern);
    }
    
    /**
     * 西暦の年のリストを作成する。
     * 
     * @param thisYear 
     * @param subtraction
     * @param addition
     * @return 結果。
     */
    public static List<Integer> createYearsList(int thisYear, int subtraction, int addition) {
        List<Integer> years = new ArrayList<>();
        int minimumYear = thisYear - subtraction;
        int maximumYear = thisYear + addition;
        for (int year = minimumYear; year <= maximumYear; year++) {
            years.add(year);
        }
        return years;
    }

    /**
     * プログラムを実行しているコンピューターの現在の年を基準に西暦の年のリストを作成する。
     * 
     * @param subtraction
     * @param addition
     * @return 結果。
     */
    public static List<Integer> createYearsList(int subtraction, int addition) {
        Datetime datetime = new Datetime();
        return Datetime.createYearsList(datetime.getYear(), subtraction, addition);
    }

    /**
     * プログラムを実行しているコンピューターの現在の年を基準に西暦の年のリストを作成する。
     * 
     * @param addition
     * @return 結果。
     */
    public static List<Integer> createYearsList(int addition) {
        Datetime datetime = new Datetime();
        return Datetime.createYearsList(datetime.getYear(), datetime.getYear() - Datetime.newInstance(new Date(0)).getYear(), addition);
    }
    
    /**
     * 西暦の月のリストを作成する。
     * 
     * @return 結果。
     */
    public static List<Integer> createMonthsList() {
        List<Integer> months = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            months.add(month);
        }
        return months;
    }
    
    /**
     * 曜日を表す列挙型。
     * 
     * @author hiro
     *
     */
    public static enum DayOfWeek implements IdentifiableEnum<Integer> {
        /**
         * 日曜日。
         */
        SUNDAY(1, "日曜日"),
        /**
         * 月曜日。
         */
        MONDAY(2, "月曜日"),
        /**
         * 火曜日。
         */
        TUESDAY(3, "火曜日"),
        /**
         * 水曜日。
         */
        WEDNESDAY(4, "水曜日"),
        /**
         * 木曜日。
         */
        THURSDAY(5, "木曜日"),
        /**
         * 金曜日。
         */
        FRIDAY(6, "金曜日"),
        /**
         * 土曜日。
         */
        SATURDAY(7, "土曜日"),
        ;
        
        private DayOfWeek(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private int id;
        
        @Override
        public Integer getID() {
            return this.id;
        }
        
        private String name;
        
        @Override
        public String getName() {
            return this.name;
        }
    }
}
