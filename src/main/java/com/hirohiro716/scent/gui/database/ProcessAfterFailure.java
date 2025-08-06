package com.hirohiro716.scent.gui.database;

/**
 * データベースに対する処理が失敗した後に実行される処理のインターフェース。
 */
public interface ProcessAfterFailure {

    /**
     * データベースに対する処理が失敗した後に実行される処理。
     */
    public abstract void execute();
}
