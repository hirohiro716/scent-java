package com.hirohiro716.scent.gui.control.table;

import com.hirohiro716.scent.gui.control.Control;

/**
 * テーブルに表示する、読み取り専用のコントロールを作成するファクトリークラス。
 *
 * @param <C> テーブルのカラム情報インスタンスの型。
 * @param <R> テーブルの行情報のインスタンスの型。
 * @param <T> 作成するコントロールの型。
 */
public interface ReadonlyControlFactory<C, R, T extends Control> extends ControlFactory<C, R, T> {

    @Override
    default void setValueToRowInstance(T control, R rowInstance, C columnInstance) {
    }
}
