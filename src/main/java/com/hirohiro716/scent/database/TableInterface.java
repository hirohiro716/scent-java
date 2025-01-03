package com.hirohiro716.scent.database;

import com.hirohiro716.scent.DynamicArray;

/**
 * データベーステーブルのインターフェース。
 * 
 * @author hiro
 */
public interface TableInterface {

    /**
     * 日本語名を取得する。
     * 
     * @return
     */
    public abstract String getLogicalName();
    
    /**
     * 物理名を取得する。
     * 
     * @return
     */
    public default String getPhysicalName() {
        return this.toString().toLowerCase();
    }
    
    /**
     * テーブルに属するカラムを取得する。
     * 
     * @return
     */
    public abstract ColumnInterface[] getColumns();

    /**
     * 指定された物理名とテーブル列挙型のクラスから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param <T> テーブル列挙型。
     * @param physicalName
     * @param tableEnumClass このインターフェースを継承している列挙型のクラス。
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends TableInterface> T tableOf(String physicalName, Class<T> tableEnumClass) {
        try {
            for (TableInterface tableEnum : tableEnumClass.getEnumConstants()) {
                if (tableEnum.getPhysicalName().equals(physicalName)) {
                    return (T) tableEnum;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
    
    /**
     * カラムの物理名から、テーブルに属するカラムを取得する。見つからなかった場合はnullを返す。
     * 
     * @param <C>
     * @param physicalColumnName
     * @return
     */
    @SuppressWarnings("unchecked")
    public default <C extends ColumnInterface> C findColumn(String physicalColumnName) {
        if (this.getColumns() != null) {
            for (ColumnInterface column : this.getColumns()) {
                if (column.getPhysicalName().equals(physicalColumnName) || column.getFullPhysicalName().equals(physicalColumnName)) {
                    return (C) column;
                }
            }
        }
        return null;
    }
    
    /**
     * 初期値が入力された、キーがカラム列挙型のレコード配列を作成する。
     * 
     * @param <C>
     * @return
     */
    @SuppressWarnings("unchecked")
    public default <C extends ColumnInterface> DynamicArray<C> createRecord() {
        DynamicArray<C> record = new DynamicArray<>();
        for (ColumnInterface column : this.getColumns()) {
            record.put((C) column, column.getDefaultValue());
        }
        return record;
    }
    
    /**
     * キーがカラム文字列の配列から、キーがカラム列挙型のレコード配列を作成する。
     * 
     * @param <C>
     * @param stringKeyRecord
     * @return
     */
    public default <C extends ColumnInterface> DynamicArray<C> createRecord(DynamicArray<String> stringKeyRecord) {
        DynamicArray<C> record = this.createRecord();
        for (String columnString : stringKeyRecord.getKeys()) {
            C column = this.findColumn(columnString);
            if (column != null) {
                record.put(column, stringKeyRecord.get(columnString));
            }
        }
        return record;
    }
}
