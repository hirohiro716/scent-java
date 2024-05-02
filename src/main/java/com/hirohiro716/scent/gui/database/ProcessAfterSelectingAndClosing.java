package com.hirohiro716.scent.gui.database;

import com.hirohiro716.scent.DynamicArray;

/**
 * データベースレコードが選択された後に実行される処理のインターフェース。
 * 
 * @author hiro
 *
 */
public interface ProcessAfterSelectingAndClosing {

    /**
     * データベースレコードが選択された後に実行される処理。
     * 
     * @param selected
     */
    public abstract void execute(DynamicArray<String> selected);
}
