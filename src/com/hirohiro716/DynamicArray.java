package com.hirohiro716;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.io.ByteArray;
import com.hirohiro716.reflection.Method;

/**
 * 動的な配列のクラス。<br>
 * ※内部でjava.util.LinkedHashMapを使用している
 * 
 * @author hiro
 *
 * @param <K> 配列のキー型。
 */
public class DynamicArray<K> implements Cloneable, Serializable {
    
    private static final long serialVersionUID = 1482400069206265316L;
    
    /**
     * コンストラクタ。
     */
    public DynamicArray() {
        this.hashMap = new LinkedHashMap<>();
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたMapをコピーして初期値とした連想配列を作成する。
     * 
     * @param map
     */
    public DynamicArray(Map<K, ?> map) {
        this();
        this.hashMap = new LinkedHashMap<>(map);
    }

    /**
     * コンストラクタ。<br>
     * 指定された配列を初期値として連想配列を作成する。キーは自動的に決定する。
     * 
     * @param values
     */
    public DynamicArray(Object[] values) {
        this();
        this.add(values);
    }
    
    /**
     * コンストラクタ。<br>
     * 指定された配列を初期値として連想配列を作成する。キーは自動的に決定する。
     * 
     * @param values
     */
    public DynamicArray(Collection<?> values) {
        this();
        this.add(values);
    }
    
    private LinkedHashMap<K, Object> hashMap;
    
    /**
     * 内部で使用しているjava.util.LinkedHashMapを取得する。
     * 
     * @return 結果。
     */
    public LinkedHashMap<K, Object> getLinkedHashMap() {
        return this.hashMap;
    }
    
    /**
     * 指定された値を指定されたキーにマップする。キーに以前のマッピングがあった場合、以前の値は置き換えられる。
     * 
     * @param key
     * @param value
     */
    public void put(K key, Object value) {
        this.hashMap.put(key, value);
    }

    /**
     * 指定されたキーを、Objectからこの配列のキー型への未検査キャストを行い、指定された値をとマップする。<br>
     * キーに以前のマッピングがあった場合、以前の値は置き換えられる。
     * 
     * @param key
     * @param value
     * @throws ClassCastException 
     */
    @SuppressWarnings("unchecked")
    public final void forciblyPut(Object key, Object value) throws ClassCastException {
        this.put((K) key, value);
    }

    /**
     * 指定された値を追加する。キーは配列のサイズに応じて自動的に決定する。
     * 
     * @param values
     */
    public void add(Object[] values) {
        try {
            for (Object value : values) {
                synchronized (this.hashMap) {
                    int key = this.size();
                    while (this.hashMap.containsKey(key)) {
                        key++;
                    }
                    this.forciblyPut(key, value);
                }
            }
        } catch (ClassCastException exception) {
        }
    }

    /**
     * 指定された値を追加する。キーは配列のサイズに応じて自動的に決定する。
     * 
     * @param value
     */
    public final void add(Object value) {
        this.add(new Object[] {value});
    }
    
    /**
     * 指定された値を追加する。キーは配列のサイズに応じて自動的に決定する。
     * 
     * @param values
     */
    public final void add(Collection<?> values) {
        this.add(values.toArray(new Object[] {}));
    }
    
    /**
     * 指定されたキーに関連づいた値を取得する。
     * 
     * @param <V> 値の型。
     * @param key
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <V> V get(K key) {
        return (V) this.hashMap.get(key);
    }
    
    /**
     * 指定されたキーに関連づいた値を、キャストするクラスを指定して取得する。
     * 
     * @param <V> 値の型。
     * @param key
     * @param valueType
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <V> V get(K key, Class<V> valueType) {
        Object value = this.hashMap.get(key);
        if (valueType.isInstance(value) == false) {
            StringObject stringObject = new StringObject(value);
            switch (valueType.getName()) {
            case "byte":
            case "java.lang.Byte":
                return (V) stringObject.toByte();
            case "short":
            case "java.lang.Short":
                return (V) stringObject.toShort();
            case "int":
            case "java.lang.Integer":
                return (V) stringObject.toInteger();
            case "long":
            case "java.lang.Long":
                return (V) stringObject.toLong();
            case "float":
            case "java.lang.Float":
                return (V) stringObject.toFloat();
            case "double":
            case "java.lang.Double":
            case "java.lang.Number":
                return (V) stringObject.toDouble();
            case "boolean":
            case "java.lang.Boolean":
                return (V) stringObject.toBoolean();
            case "java.lang.String":
                return (V) stringObject.toString();
            case "java.util.Date":
                return (V) stringObject.toDate();
            }
        }
        return (V) value;
    }
    
    /**
     * 指定されたキーに関連づいた値を、Booleanとして取得する。
     * 
     * @param key
     * @return 結果。
     */
    public final Boolean getBoolean(K key) {
        return this.get(key, Boolean.class);
    }
    
    /**
     * 指定されたキーに関連づいた値を、Integerとして取得する。
     * 
     * @param key
     * @return 結果。
     */
    public final Integer getInteger(K key) {
        return this.get(key, Integer.class);
    }
    
    /**
     * 指定されたキーに関連づいた値を、Longとして取得する。
     * 
     * @param key
     * @return 結果。
     */
    public final Long getLong(K key) {
        return this.get(key, Long.class);
    }
    
    /**
     * 指定されたキーに関連づいた値を、Floatとして取得する。
     * 
     * @param key
     * @return 結果。
     */
    public final Float getFloat(K key) {
        return this.get(key, Float.class);
    }
    
    /**
     * 指定されたキーに関連づいた値を、Doubleとして取得する。
     * 
     * @param key
     * @return 結果。
     */
    public final Double getDouble(K key) {
        return this.get(key, Double.class);
    }
    
    /**
     * 指定されたキーに関連づいた値を、Dateとして取得する。
     * 
     * @param key
     * @return 結果。
     */
    public final Date getDate(K key) {
        return this.get(key, Date.class);
    }

    /**
     * 指定されたキーに関連づいた値を、文字列として取得する。
     * 
     * @param key
     * @return 結果。
     */
    public final String getString(K key) {
        return this.get(key, String.class);
    }
    
    /**
     * この配列の要素数を返す。
     * 
     * @return 結果。
     */
    public int size() {
        return this.hashMap.size();
    }
    
    /**
     * この連想配列に指定されたキーのマッピングが含まれている場合trueを返す。
     * 
     * @param key
     * @return 結果。
     */
    public boolean containsKey(K key) {
        return this.hashMap.containsKey(key);
    }
    
    /**
     * この連想配列に指定された値のマッピングが含まれている場合trueを返す。
     * 
     * @param value
     * @return 結果。
     */
    public boolean containsValue(Object value) {
        return this.hashMap.containsValue(value);
    }
    
    /**
     * この連想配列の指定されたキーのマッピングを削除する。
     * 
     * @param key
     */
    public void removeKey(K key) {
        this.hashMap.remove(key);
    }
    
    /**
     * この連想配列に含まれる指定された値へのマッピングをすべて削除する。値の比較はequalsメソッドを使用して行われる。
     * 
     * @param value
     */
    public void removeValue(Object value) {
        for (Object key : this.hashMap.keySet()) {
            if (this.hashMap.get(key).equals(value)) {
                this.hashMap.remove(key);
            }
        }
    }
    
    /**
     * この連想配列からすべてのマッピングを削除する。
     */
    public void clear() {
        this.hashMap.clear();
    }
    
    /**
     * この連想配列に含まれるすべてのキーをjava.util.Setで取得する。Setは連想配列のマッピングに連動している。
     * 
     * @return 結果。
     */
    public Collection<K> getKeys() {
        return this.hashMap.keySet();
    }
    
    /**
     * この連想配列に含まれるすべての値をjava.util.Collectionで取得する。Collectionは連想配列のマッピングに連動している。
     * 
     * @param <V> 値の型。
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <V> Collection<V> getValues() {
        return (Collection<V>) this.hashMap.values();
    }
    
    /**
     * この連想配列にほかの連想配列を結合する。キーに重複のマッピングがあった場合、あとの値で上書きされる。
     * 
     * @param arrays
     */
    @SuppressWarnings("unchecked")
    public void merge(DynamicArray<K>... arrays) {
        for (DynamicArray<K> array : arrays) {
            for (K key : array.getKeys()) {
                this.put(key, array.get(key));
            }
        }
    }
    
    /**
     * この連想配列のコピーを作成する。連想配列の値にCloneableインターフェースの実装があった場合、値のcloneメソッドも呼び出されコピーされる。
     * 
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    @Override
    public DynamicArray<K> clone() {
        DynamicArray<K> array;
        try {
            array = (DynamicArray<K>) super.clone();
        } catch (CloneNotSupportedException exception) {
            return null;
        }
        array.hashMap = new LinkedHashMap<>(this.hashMap);
        for (K key : array.getKeys()) {
            Object value = array.get(key);
            if (value instanceof Cloneable) {
                try {
                    Method method = new Method(value);
                    array.put(key, method.invoke("clone"));
                } catch (Exception exception) {
                }
            }
        }
        return array;
    }
    
    /**
     * この連想配列の文字列表現を返す。
     * 
     * @return 結果。
     */
    @Override
    public String toString() {
        return this.hashMap.toString();
    }
    
    /**
     * この連想配列をserializeしたByteArrayを作成する。<br>
     * 連想配列内の参照型オブジェクトのserializeはサポートしない。
     * 
     * @return serializeされたByteArrayオブジェクト。
     * @throws IOException
     */
    public ByteArray serialize() throws IOException {
        return new ByteArray(this);
    }
    
    /**
     * serializeされた連想配列のインスタンスを復元する。
     * 
     * @param byteArray serializeされたByteArrayオブジェクト。
     * @return Array
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static <K> DynamicArray<K> deserialize(ByteArray byteArray) throws ClassNotFoundException, IOException {
        return byteArray.deserialize();
    }
    
    /**
     * 指定されたMapをコピーして初期値としたインスタンスを作成する。
     * 
     * @param map
     * @return 結果。
     */
    public static <K> DynamicArray<K> newInstance(Map<K, ?> map) {
        return new DynamicArray<>(map);
    }

    /**
     * 指定された配列を初期値としてインスタンスを作成する。キーは自動的に決定する。
     * 
     * @param values
     * @return 結果。
     */
    public static DynamicArray<Integer> newInstance(Object... values) {
        return new DynamicArray<>(values);
    }
    
    /**
     * 指定された配列を初期値としてインスタンスを作成する。キーは自動的に決定する。
     * 
     * @param values
     * @return 結果。
     */
    public static DynamicArray<Integer> newInstance(Collection<Object> values) {
        return DynamicArray.newInstance(values.toArray(new Object[] {}));
    }
    
    /**
     * 指定されたキーに関連づいた値をもとに連想配列を並び替える。
     * 
     * @param arrays
     * @param sortKey 並び替えに使用する値を取得するためのキー。
     * @param sortOrder
     * @return 結果。
     */
    public static <K> List<DynamicArray<K>> sort(Collection<DynamicArray<K>> arrays, K sortKey, SortOrder sortOrder) {
        List<DynamicArray<K>> result = new ArrayList<>();
        result.addAll(arrays);
        if (sortOrder != null) {
            switch (sortOrder) {
            case ASCENDING:
                Collections.sort(result, new Comparator<K>(sortKey));
                break;
            case DESCENDING:
                Collections.sort(result, new Comparator<K>(sortKey).reversed());
                break;
            }
        }
        return result;
    }

    /**
     * 指定されたキーに関連づいた値をもとに連想配列を並び替える。
     * 
     * @param arrays
     * @param sortKey 並び替えに使用する値を取得するためのキー。
     * @param sortOrder
     * @return 結果。
     */
    public static <K> List<DynamicArray<K>> sort(DynamicArray<K>[] arrays, K sortKey, SortOrder sortOrder) {
        List<DynamicArray<K>> list = Arrays.asList(arrays);
        return DynamicArray.sort(list, sortKey, sortOrder);
    }
    
    /**
     * 指定されたキーに関連づいた値同士を使用して2つの連想配列の順序付けをする比較クラス。
     * 
     * @author hiro
     * @param <K> 
     *
     */
    private static class Comparator<K> implements java.util.Comparator<DynamicArray<K>> {
        
        private K sortKey;
        
        /**
         * コンストラクタ。
         * 
         * @param sortKey 比較に使用する値を取得するためのキー。
         */
        public Comparator(K sortKey) {
            this.sortKey = sortKey;
        }
        
        @Override
        public int compare(DynamicArray<K> array1, DynamicArray<K> array2) {
            if (array1.get(this.sortKey) instanceof Number && array2.get(this.sortKey) instanceof Number) {
                Number value1 = array1.getDouble(this.sortKey);
                Number value2 = array2.getDouble(this.sortKey);
                if (value1.doubleValue() > value2.doubleValue()) {
                    return 1;
                } else if (value1.doubleValue() == value2.doubleValue()) {
                    return 0;
                } else {
                    return -1;
                }
            }
            return 0;
        }
    }
    
    /**
     * 並び替えに使用できる順序の列挙型。
     * 
     * @author hiro
     *
     */
    public enum SortOrder {
        /**
         * 昇順。
         */
        ASCENDING,
        /**
         * 降順。
         */
        DESCENDING,
    }
}
