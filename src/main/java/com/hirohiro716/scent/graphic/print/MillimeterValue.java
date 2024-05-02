package com.hirohiro716.scent.graphic.print;

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
    public MillimeterValue(float millimeter) {
        this.millimeter = millimeter;
    }
    
    private static final float MILLIMETER_TO_POINT_RATIO = 72.0f / 25.4f;
    
    private float millimeter = 0;
    
    /**
     * ミリメートル単位の値をfloatで取得する。
     * 
     * @return 結果。
     */
    public float get() {
        return this.millimeter;
    }
    
    /**
     * ポイント単位に変換する。
     * 
     * @return 結果。
     */
    public float toPoint() {
        return this.millimeter * MillimeterValue.MILLIMETER_TO_POINT_RATIO;
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param millimeter
     * @return 結果。
     */
    public static MillimeterValue newInstance(float millimeter) {
        return new MillimeterValue(millimeter);
    }
    
    /**
     * ポイント単位の値からミリメートル単位の値を作成する。
     * 
     * @param point
     * @return 結果。
     */
    public static MillimeterValue fromPoint(float point) {
        return new MillimeterValue(point / MillimeterValue.MILLIMETER_TO_POINT_RATIO);
    }
}
