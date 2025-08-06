package com.hirohiro716.scent.gui.control.table;

import com.hirohiro716.scent.gui.control.Control;

/**
 * テーブルに表示する、編集可能なコントロールを作成するファクトリークラス。
 *
 * @param <C> テーブルのカラム情報インスタンスの型。
 * @param <R> テーブルの行情報のインスタンスの型。
 * @param <T> 作成するコントロールの型。
 */
public interface ControlFactory<C, R, T extends Control> {
    
    /**
     * テーブルに表示するコントロールのインスタンスを作成する。
     * 
     * @param columnInstance 
     * @return
     */
    public abstract T newInstance(C columnInstance);
    
    /**
     * テーブルの行情報のインスタンスから値をコントロールにセットする。
     * 
     * @param rowInstance
     * @param columnInstance 
     * @param control
     */
    public abstract void setValueToControl(R rowInstance, C columnInstance, T control);
    
    /**
     * コントロールから値をテーブルの行情報のインスタンスにセットする。
     * 
     * @param control
     * @param rowInstance
     * @param columnInstance 
     */
    public abstract void setValueToRowInstance(T control, R rowInstance, C columnInstance);
}
