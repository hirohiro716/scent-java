package com.hirohiro716.scent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 配列のクラス。
 * 
 * @author hiro
 *
 * @param <T>
 */
public class Array<T> implements Iterable<T> {
    
    /**
     * コンストラクタ。<br>
     * 指定された配列のすべての値を元にインスタンスを生成する。
     * 
     * @param value
     */
    public Array(T value) {
        this.list.add(value);
        Collections.unmodifiableList(this.list);
    }
    
    /**
     * コンストラクタ。<br>
     * 指定された配列のすべての値を元にインスタンスを生成する。
     * 
     * @param values
     */
    public Array(T[] values) {
        for (T value : values) {
            this.list.add(value);
        }
        Collections.unmodifiableList(this.list);
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたコレクションのすべての値を元にインスタンスを生成する。
     * 
     * @param values
     */
    public Array(Collection<T> values) {
        this.list.addAll(values);
        Collections.unmodifiableList(this.list);
    }
    
    private List<T> list = new ArrayList<>();
    
    /**
     * 指定された位置の値を取得する。
     * 
     * @param index
     * @return
     * @throws IndexOutOfBoundsException 
     */
    public T get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }
    
    /**
     * 値の数を取得する。
     * 
     * @return
     */
    public int length() {
        return this.list.size();
    }

    /**
     * 指定された値がコレクションに存在する場合はtrueを返す。
     * 
     * @param value
     * @return
     */
    public boolean contains(T value) {
        return this.list.contains(value);
    }
    
    /**
     * 指定された値がコレクションのどの位置かを取得する。見つからなかった場合は-1を返す。
     * 
     * @param value
     * @return
     */
    public int indexOf(T value) {
        return this.list.indexOf(value);
    }
    
    /**
     * すべての値を格納している読み取り専用のリストを取得する。
     * 
     * @return
     */
    public List<T> getUnmodifiableList() {
        return this.list;
    }
    
    @Override
    public String toString() {
        return this.list.toString();
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                Array<T> collection = Array.this;
                if (this.index >= collection.length()) {
                    return false;
                }
                return true;
            }
            
            @Override
            public T next() {
                Array<T> collection = Array.this;
                T value = collection.get(this.index);
                this.index++;
                return value;
            }
        };
    }
}
