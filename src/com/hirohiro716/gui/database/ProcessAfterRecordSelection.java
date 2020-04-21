package com.hirohiro716.gui.database;

import com.hirohiro716.DynamicArray;

/**
 * データベースレコードが選択された後に実行される処理のインターフェース。
 * 
 * @author hiro
 *
 */
public interface ProcessAfterRecordSelection {

    /**
     * データベースレコードが選択された後に実行される処理。
     * 
     * @param selected
     */
    public abstract void execute(DynamicArray<String> selected);
}
