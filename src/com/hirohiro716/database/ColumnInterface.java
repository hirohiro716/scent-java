package com.hirohiro716.database;

import java.util.LinkedHashMap;

import com.hirohiro716.StringObject;
import com.hirohiro716.property.PropertyInterface;

/**
 * データベースカラムのインターフェース。
 * 
 * @author hiro
 */
public interface ColumnInterface extends PropertyInterface {
    
    /**
     * カラムが属するテーブルを取得する。
     * 
     * @return 結果。
     */
    public abstract TableInterface getTable();
    
    /**
     * フルネームの物理名を取得する。
     * 
     * @return フルネームの物理名。
     */
    public default String getFullPhysicalName() {
        return StringObject.join(this.getTable().getPhysicalName(), ".", this.getPhysicalName()).toString();
    }
    
    /**
     * 指定された物理名とカラム列挙型のクラスから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param <C> カラム列挙型。
     * @param physicalName
     * @param columnEnumClass このインターフェースを継承している列挙型のクラス。
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public static <C extends ColumnInterface> C columnOf(String physicalName, Class<C> columnEnumClass) {
        try {
            for (ColumnInterface columnInterface : columnEnumClass.getEnumConstants()) {
                if (columnInterface.getFullPhysicalName().equals(physicalName)) {
                    return (C) columnInterface;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return PropertyInterface.propertyOf(physicalName, columnEnumClass);
    }
    
    /**
     * 指定されたカラム列挙型のすべての列挙子で、キーが物理名、値が論理名の連想配列を作成する。
     * 
     * @param <C> カラム列挙型。
     * @param columnEnumClass このインターフェースを継承している列挙型のクラス。
     * @return 結果。
     */
    public static <C extends ColumnInterface> LinkedHashMap<String, String> createLinkedHashMap(Class<C> columnEnumClass) {
        return PropertyInterface.createLinkedHashMap(columnEnumClass);
    }
}
