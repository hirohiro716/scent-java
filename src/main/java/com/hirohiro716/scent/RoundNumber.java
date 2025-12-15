package com.hirohiro716.scent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

/**
 * 数値の端数処理の列挙型。
 */
public enum RoundNumber implements IdentifiableEnum<Integer> {
    /**
     * 四捨五入。
     */
    ROUND(1, "四捨五入"),
    /**
     * 切り下げ。
     */
    FLOOR(2, "切り下げ"),
    /**
     * 切り上げ。
     */
    CEIL(3, "切り上げ"),
    ;
    
    /**
     * コンストラクタ。
     * 
     * @param id
     * @param name
     */
    private RoundNumber(int id, String name) {
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

    /**
     * 指定された数と一番近い整数を比較して、差が0.000001未満の場合は整数を、それ以上の場合は元の値を返す。
     * 
     * @param target
     * @return
     */
    private BigDecimal normalize(double target) {
        String stringValue = String.valueOf(target);
        BigDecimal bigDecimal = new BigDecimal(stringValue);
        BigDecimal nearest = bigDecimal.setScale(0, RoundingMode.HALF_UP);
        BigDecimal difference = bigDecimal.subtract(nearest).abs();
        BigDecimal epsilon = new BigDecimal("0.000001");
        if (difference.compareTo(epsilon) < 0) {
            return nearest;
        }
        return bigDecimal;
    }
    
    /**
     * 端数処理を行い整数の結果を取得する。<br>
     * 引数に渡される少数は、その数と一番近い整数の差が0.000001未満の場合、整数として扱われる。
     * 
     * @param target
     * @return
     */
    public long calculate(double target) {
        BigDecimal bigDecimal = this.normalize(target);
        switch (this) {
        case FLOOR:
            break;
        case CEIL:
            bigDecimal = bigDecimal.add(new BigDecimal("0.9"));
            break;
        case ROUND:
            bigDecimal = bigDecimal.add(new BigDecimal("0.5"));
            break;
        }
        return bigDecimal.setScale(0, RoundingMode.FLOOR).longValue();
    }

    /**
     * 指定された桁で端数処理を行い結果を取得する。<br>
     * 引数に渡される少数は、その数と一番近い整数の差が0.000001未満の場合、整数として扱われる。<br>
     * Examples:<br>
     * RoundNumber.ROUND.execute(2.5, 0) returns 3.0<br>
     * RoundNumber.FLOOR.execute(2.59, 1) returns 2.5<br>
     * RoundNumber.CEIL.execute(2.001, 2) returns 2.01
     * 
     * @param target 
     * @param digit 
     * @return
     */
    public double calculate(double target, int digit) {
        if (digit < 0) {
            throw new IllegalArgumentException("Processing with negative digits is not supported: " + digit);
        }
        BigDecimal bigDecimal = this.normalize(target);
        StringObject leftPart = new StringObject("0.");
        leftPart.append(StringObject.repeat("0", digit));
        switch (this) {
        case FLOOR:
            break;
        case CEIL:
            bigDecimal = bigDecimal.add(new BigDecimal(leftPart.append("9").toString()));
            break;
        case ROUND:
            bigDecimal = bigDecimal.add(new BigDecimal(leftPart.append("5").toString()));
            break;
        }
        return bigDecimal.setScale(digit, RoundingMode.FLOOR).doubleValue();
    }
    
    /**
     * 指定されたIDから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param id
     * @return
     */
    public static RoundNumber enumOf(Integer id) {
        return IdentifiableEnum.enumOf(id, RoundNumber.class);
    }

    /**
     * すべての列挙子で、キーがID、値が名前の連想配列を作成する。
     * 
     * @return
     */
    public static LinkedHashMap<Integer, String> createLinkedHashMap() {
        return IdentifiableEnum.createLinkedHashMap(RoundNumber.class);
    }
}
