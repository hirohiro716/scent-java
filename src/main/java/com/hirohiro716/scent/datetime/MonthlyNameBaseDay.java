package com.hirohiro716.scent.datetime;

import java.util.LinkedHashMap;

import com.hirohiro716.scent.IdentifiableEnum;

/**
 * 月度名決定基準の定数。
 */
public enum MonthlyNameBaseDay implements IdentifiableEnum<String> {
    /**
     * 初日。
     */
    START_DAY("初日"),
    /**
     * 末日。
     */
    LAST_DAY("末日"),
    ;

    /**
     * コンストラクタ。<br>
     * 名前を指定する。
     * 
     * @param name
     */
    private MonthlyNameBaseDay(String name) {
        this.name = name;
    }

    @Override
    public String getID() {
        return this.toString().toLowerCase();
    }

    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * 指定されたIDから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param id
     * @return
     */
    public static MonthlyNameBaseDay enumOf(String id) {
        return IdentifiableEnum.enumOf(id, MonthlyNameBaseDay.class);
    }
    
    /**
     * すべての列挙子で、キーがID、値が名前の連想配列を作成する。
     * 
     * @return
     */
    public static LinkedHashMap<String, String> createLinkedHashMap() {
        return IdentifiableEnum.createLinkedHashMap(MonthlyNameBaseDay.class);
    }
}
