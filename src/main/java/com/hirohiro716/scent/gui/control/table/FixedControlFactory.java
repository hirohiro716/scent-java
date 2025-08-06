package com.hirohiro716.scent.gui.control.table;

import com.hirohiro716.scent.gui.control.Control;

/**
 * テーブルに表示する、固定表示のコントロールを作成するファクトリークラス。
 *
 * @param <C> テーブルのカラム情報インスタンスの型。
 * @param <R> テーブルの行情報のインスタンスの型。
 * @param <T> 作成するコントロールの型。
 */
public interface FixedControlFactory<C, R, T extends Control> extends ReadonlyControlFactory<C, R, T> {
    
    @Override
    default void setValueToControl(R rowInstance, C columnInstance, T control) {
    }
}
