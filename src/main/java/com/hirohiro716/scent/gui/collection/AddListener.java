package com.hirohiro716.scent.gui.collection;

/**
 * コレクションに対するオブジェクトの追加を検知するリスナーの抽象クラス。
 *
 * @param <T> 検知対象の型。
 */
public abstract class AddListener<T> {
    
    /**
     * コレクションにオブジェクトが追加されたときに呼び出される。
     * 
     * @param added
     * @param positionIndex 
     */
    protected abstract void added(T added, int positionIndex);
}
