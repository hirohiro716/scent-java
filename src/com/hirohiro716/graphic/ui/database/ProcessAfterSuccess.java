package com.hirohiro716.graphic.ui.database;

/**
 * データベースに対する処理が成功した後に実行される処理のインターフェース。
 * 
 * @author hiro
 *
 * @param <T> データベースに対する処理結果の型。
 */
public interface ProcessAfterSuccess<T> {

    /**
     * データベースに対する処理が成功した後に実行される処理。
     * 
     * @param successedInstance
     */
    public abstract void execute(T successedInstance);
}
