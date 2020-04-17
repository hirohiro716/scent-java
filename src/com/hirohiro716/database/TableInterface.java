package com.hirohiro716.database;

/**
 * データベーステーブルのインターフェース。
 * 
 * @author hiro
 */
public interface TableInterface {

    /**
     * 日本語名を取得する。
     * 
     * @return 結果。
     */
    public abstract String getLogicalName();
    
    /**
     * 物理名を取得する。
     * 
     * @return 結果。
     */
    public default String getPhysicalName() {
        return this.toString().toLowerCase();
    }
    
    /**
     * テーブルに属するカラムを取得する。
     * 
     * @return 結果。
     */
    public abstract ColumnInterface[] getColumns();

    /**
     * 指定された物理名とテーブル列挙型のクラスから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param <T> テーブル列挙型。
     * @param physicalName
     * @param tableEnumClass このインターフェースを継承している列挙型のクラス。
     * @return 結果。
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
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public default <C extends ColumnInterface> C findColumn(String physicalColumnName) {
        for (ColumnInterface column : this.getColumns()) {
            if (column.getPhysicalName().equals(physicalColumnName) || column.getFullPhysicalName().equals(physicalColumnName)) {
                return (C) column;
            }
        }
        return null;
    }
}
