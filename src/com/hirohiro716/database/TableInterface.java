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
     * カラムの物理名から、テーブルに属するカラムを取得する。見つからなかった場合はnullを返す。
     * 
     * @param <C>
     * @param physicalColumnName
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public default <C extends ColumnInterface> C find(String physicalColumnName) {
        for (ColumnInterface column : this.getColumns()) {
            if (column.getPhysicalName().equals(physicalColumnName) || column.getFullPhysicalName().equals(physicalColumnName)) {
                return (C) column;
            }
        }
        return null;
    }
}
