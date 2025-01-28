package com.hirohiro716.scent.gui.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hirohiro716.scent.Array;

/**
 * GUIのコレクションクラス。
 * 
 * @author hiro
 *
 * @param <T>
 */
public class Collection<T> implements Iterable<T> {
    
    private List<T> list = new ArrayList<>();

    /**
     * コレクション内の指定された位置の値を取得する。
     * 
     * @param index
     * @return
     * @throws IndexOutOfBoundsException 
     */
    public T get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }
    
    /**
     * コレクションの値の数を取得する。
     * 
     * @return
     */
    public int size() {
        return this.list.size();
    }
    
    private List<AddListener<T>> addListeners = new ArrayList<>();
    
    /**
     * コレクションに対するオブジェクトの追加を検知するリスナーを取得する。
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public AddListener<T>[] getAddListeners() {
        return this.addListeners.toArray(new AddListener[] {});
    }
    
    /**
     * コレクションに対するオブジェクトの追加を検知するリスナーを追加する。
     * 
     * @param addListener
     */
    public void addListener(AddListener<T> addListener) {
        this.addListeners.add(addListener);
    }
    
    /**
     * コレクションに対するオブジェクトの追加を検知するリスナーを削除する。
     * 
     * @param addListener
     */
    public void removeListener(AddListener<T> addListener) {
        this.addListeners.remove(addListener);
    }
    
    private List<RemoveListener<T>> removeListeners = new ArrayList<>();

    /**
     * コレクションに対するオブジェクトの削除を検知するリスナーを取得する。
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public RemoveListener<T>[] getRemoveListeners() {
        return this.removeListeners.toArray(new RemoveListener[] {});
    }
    
    /**
     * コレクションに対するオブジェクトの削除を検知するリスナーを追加する。
     * 
     * @param removeListener
     */
    public void addListener(RemoveListener<T> removeListener) {
        this.removeListeners.add(removeListener);
    }
    
    /**
     * コレクションに対するオブジェクトの削除を検知するリスナーを削除する。
     * 
     * @param removeListener
     */
    public void removeListener(RemoveListener<T> removeListener) {
        this.removeListeners.remove(removeListener);
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
     * コレクション内に指定された値を、指定された位置に追加する。
     * 
     * @param value
     * @param positionIndex
     */
    public void add(T value, int positionIndex) {
        if (this.list.contains(value)) {
            return;
        }
        this.list.add(positionIndex, value);
        for (AddListener<T> listener: this.addListeners) {
            listener.added(value, positionIndex);
        }
    }
    
    /**
     * コレクションの末尾に指定された値を追加する。
     * 
     * @param value
     */
    public final void add(T value) {
        this.add(value, this.size());
    }
    
    /**
     * コレクション内の末尾に指定されたコレクションのすべての値を追加する。
     * 
     * @param values
     */
    public final void addAll(java.util.Collection<T> values) {
        for (T value: new Array<>(values)) {
            this.add(value);
        }
    }

    /**
     * コレクション内の末尾に指定された配列のすべての値を追加する。
     * 
     * @param values
     */
    public final void addAll(T[] values) {
        for (T value: values) {
            this.add(value);
        }
    }
    
    /**
     * コレクション内の指定された値を削除する。
     * 
     * @param value
     */
    public void remove(T value) {
        this.list.remove(value);
        for (RemoveListener<T> listener: this.removeListeners) {
            listener.removed(value);
        }
    }
    
    /**
     * コレクション内のすべての値を削除する。
     */
    public final void clear() {
        for (T value: this.toArray()) {
            this.remove(value);
        }
    }

    /**
     * コレクション内のすべての値を格納した配列を返す。
     * 
     * @return
     */
    public Array<T> toArray() {
        return new Array<>(this.list);
    }

    /**
     * コレクション内のすべての値を格納している読み取り専用のリストを作成する。
     * 
     * @return
     */
    public List<T> toUnmodifiableList() {
        List<T> list = new ArrayList<>(this.list);
        Collections.unmodifiableList(list);
        return list;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                Collection<T> collection = Collection.this;
                if (this.index >= collection.size()) {
                    return false;
                }
                return true;
            }
            
            @Override
            public T next() {
                Collection<T> collection = Collection.this;
                T value = collection.get(this.index);
                this.index++;
                return value;
            }
        };
    }
}
