package com.hirohiro716.property;

import java.util.LinkedHashMap;

/**
 * 設定項目など、プロパティのインターフェース。
 * 
 * @author hiro
 */
public interface PropertyInterface {
    
    /**
     * 論理名を取得する。
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
     * 初期値を取得する。
     * 
     * @return 結果。
     */
    public abstract Object getDefaultValue();
    
    /**
     * 最大文字数を取得する。-1は無制限。
     * 
     * @return 結果。
     */
    public abstract int getMaximumLength();

    /**
     * 指定された物理名とプロパティ列挙型のクラスから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param <P> プロパティ列挙型。
     * @param physicalName
     * @param propertyEnumClass このインターフェースを継承している列挙型のクラス。
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public static <P extends PropertyInterface> P propertyOf(String physicalName, Class<P> propertyEnumClass) {
        try {
            for (PropertyInterface propertyInterface : propertyEnumClass.getEnumConstants()) {
                if (propertyInterface.getPhysicalName().equals(physicalName)) {
                    return (P) propertyInterface;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
    
    /**
     * 指定されたプロパティ列挙型のすべての列挙子で、キーが物理名、値が論理名の連想配列を作成する。
     * 
     * @param <P> プロパティ列挙型。
     * @param propertyEnumClass このインターフェースを継承している列挙型のクラス。
     * @return 結果。
     */
    public static <P extends PropertyInterface> LinkedHashMap<String, String> createLinkedHashMap(Class<P> propertyEnumClass) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        for (PropertyInterface propertyInterface: propertyEnumClass.getEnumConstants()) {
            try {
                hashMap.put(propertyInterface.getPhysicalName(), propertyInterface.getLogicalName());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return hashMap;
    }
}
