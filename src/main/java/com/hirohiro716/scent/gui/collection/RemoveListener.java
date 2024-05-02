package com.hirohiro716.scent.gui.collection;

/**
 * コレクションに対するオブジェクトの削除を検知するリスナーの抽象クラス。
 * 
 * @author hiro
 *
 * @param <T> 検知対象の型。
 */
public abstract class RemoveListener<T> {
    
    /**
     * コレクションにオブジェクトが削除されたときに呼び出される。
     * 
     * @param removed
     */
    protected abstract void removed(T removed);
}
