package com.hirohiro716.scent.graphic;

/**
 * 長さの単位の列挙型。
 */
public enum LengthUnit {
    /**
     * ポイント。
     */
    POINT(1f),
    /**
     * インチ。
     */
    INCH(72f),
    /**
     * ミリメートル。
     */
    MILLIMETER(72.0f / 25.4f),
    ;

    /**
     * コンストラクタ。<br>
     * その単位になるようポイント単位の数値に除算する率を指定する。
     * 
     * @param ratio
     */
    private LengthUnit(float ratio) {
        this.ratio = ratio;
    }

    private float ratio;

    /**
     * 単位がポイント値から、この単位の値を計算する。
     * 
     * @param point
     * @return
     */
    public float fromPoint(float point) {
        return point / this.ratio;
    }

    /**
     * この単位の値から、ポイント単位の値を計算する。
     * 
     * @param valueOfThisUnit
     * @return
     */
    public float toPoint(float valueOfThisUnit) {
        return valueOfThisUnit * this.ratio;
    }
}
