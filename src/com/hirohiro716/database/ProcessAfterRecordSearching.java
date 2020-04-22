package com.hirohiro716.database;

import com.hirohiro716.Array;
import com.hirohiro716.DynamicArray;

/**
 * データベースレコードを検索した結果、複数のレコードを処理するインターフェース。
 * 
 * @author hiro
 * 
 * @param <C> カラムの型。
 */
public interface ProcessAfterRecordSearching<C extends ColumnInterface> {

    /**
     * データベースレコードを検索した後に実行される処理。
     * 
     * @param searchResult 検索結果のレコード。
     */
    public abstract void execute(Array<DynamicArray<C>> searchResult);
}
