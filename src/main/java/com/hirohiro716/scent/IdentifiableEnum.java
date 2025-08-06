package com.hirohiro716.scent;

import java.util.LinkedHashMap;

/**
 * 列挙型を数値(int)で識別可能にするインターフェース。
 *
 * @param <T> IDの型。
 */
public interface IdentifiableEnum<T> {
    
    /**
     * 列挙子ごとのIDを取得する。
     * 
     * @return
     */
    public abstract T getID();

    /**
     * 列挙子の名前を取得する。
     * 
     * @return
     */
    public abstract String getName();
    
    /**
     * 指定されたIDと列挙型のクラスから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param <E>
     * @param id
     * @param identifiableEnumClass このインターフェースを継承している列挙型のクラス。
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E extends IdentifiableEnum<?>> E enumOf(Object id, Class<E> identifiableEnumClass) {
        try {
            for (IdentifiableEnum<?> identifiableEnum: identifiableEnumClass.getEnumConstants()) {
                if (identifiableEnum.getID().equals(id)) {
                    return (E) identifiableEnum;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
    
    /**
     * 指定された列挙型のすべての列挙子で、キーがID、値が名前の連想配列を作成する。
     * 
     * @param <E> 列挙型。
     * @param <T> IDの型。
     * @param identifiableEnumClass このインターフェースを継承している列挙型のクラス。
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E extends IdentifiableEnum<?>, T> LinkedHashMap<T, String> createLinkedHashMap(Class<E> identifiableEnumClass) {
        LinkedHashMap<T, String> hashMap = new LinkedHashMap<>();
        for (IdentifiableEnum<?> identifiableEnum: identifiableEnumClass.getEnumConstants()) {
            try {
                hashMap.put((T) identifiableEnum.getID(), identifiableEnum.getName());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return hashMap;
    }
}
