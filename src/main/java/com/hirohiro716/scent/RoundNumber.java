package com.hirohiro716.scent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

/**
 * 数値の端数処理の列挙型。
 * 
 * @author hiro
 *
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
     * 端数処理を行い整数の結果を取得する。
     * 
     * @param target
     * @return 結果。
     */
    public long calculate(double target) {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(target));
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
     * Examples:<br>
     * RoundNumber.ROUND.execute(2.5, 0) returns 3.0<br>
     * RoundNumber.FLOOR.execute(2.59, 1) returns 2.5<br>
     * RoundNumber.CEIL.execute(2.001, 2) returns 2.01
     * 
     * @param target 
     * @param digit 
     * @return 結果。
     */
    public double calculate(double target, int digit) {
        if (digit < 0) {
            throw new IllegalArgumentException("Processing with negative digits is not supported: " + digit);
        }
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(target));
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
     * @return 結果。
     */
    public static RoundNumber enumOf(Integer id) {
        return IdentifiableEnum.enumOf(id, RoundNumber.class);
    }

    /**
     * すべての列挙子で、キーがID、値が名前の連想配列を作成する。
     * 
     * @return 結果。
     */
    public static LinkedHashMap<Integer, String> createLinkedHashMap() {
        return IdentifiableEnum.createLinkedHashMap(RoundNumber.class);
    }
}
