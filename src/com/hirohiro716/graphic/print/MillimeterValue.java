package com.hirohiro716.graphic.print;

/**
 * ミリメートル単位の値クラス。
 * 
 * @author hiro
 *
 */
public class MillimeterValue {
    
    /**
     * コンストラクタ。
     * 
     * @param millimeter
     */
    public MillimeterValue(double millimeter) {
        this.millimeter = millimeter;
    }
    
    private static final double MILLIMETER_TO_POINT_RATIO = 72.0 / 25.4;
    
    private double millimeter = 0;
    
    /**
     * ミリメートル単位の値をdoubleで取得する。
     * 
     * @return 結果。
     */
    public double get() {
        return this.millimeter;
    }
    
    /**
     * ポイント単位に変換する。
     * 
     * @return 結果。
     */
    public double toPoint() {
        return this.millimeter * MillimeterValue.MILLIMETER_TO_POINT_RATIO;
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param millimeter
     * @return 結果。
     */
    public static MillimeterValue newInstance(double millimeter) {
        return new MillimeterValue(millimeter);
    }
    
    /**
     * ポイント単位の値からミリメートル単位の値を作成する。
     * 
     * @param point
     * @return 結果。
     */
    public static MillimeterValue fromPoint(double point) {
        return new MillimeterValue(point / MillimeterValue.MILLIMETER_TO_POINT_RATIO);
    }
}
