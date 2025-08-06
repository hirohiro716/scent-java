package com.hirohiro716.scent.database;

import java.sql.SQLException;

import com.hirohiro716.scent.DynamicArray;

/**
 * データベースレコードがコンフリクトした場合に発生する例外クラス。
 */
public class RecordConflictException extends SQLException {
    
    private final static String MESSAGE = "データベースレコードが競合しました。";

    /**
     * コンフリクトした原因のレコードと例外メッセージを指定して新規例外を構築する。
     * 
     * @param <C>
     * @param message
     * @param conflictRecords ほかの操作によってコンフリクトしたレコード。
     * @param deletedRecords ほか操作によって削除されたレコード。
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> RecordConflictException(String message, DynamicArray<C>[] conflictRecords, DynamicArray<C>[] deletedRecords) {
        super(message);
        this.conflictRecords = new DynamicArray[] {};
        if (conflictRecords != null) {
            this.conflictRecords = (DynamicArray[]) conflictRecords;
        }
        this.deletedRecords = new DynamicArray[] {};
        if (deletedRecords != null) {
            this.deletedRecords = (DynamicArray[]) deletedRecords;
        }
    }

    /**
     * コンフリクトの原因のレコードを指定して、初期の例外メッセージを持つ新規例外を構築する。
     * 
     * @param <C>
     * @param conflictRecords ほかの操作によってコンフリクトしたレコード。
     * @param deletedRecords ほか操作によって削除されたレコード。
     */
    public <C extends ColumnInterface> RecordConflictException(DynamicArray<C>[] conflictRecords, DynamicArray<C>[] deletedRecords) {
        this(RecordConflictException.MESSAGE, conflictRecords, deletedRecords);
    }

    private DynamicArray<ColumnInterface>[] conflictRecords;
    
    /**
     * ほかの操作によってコンフリクトしたレコードを取得する。
     * 
     * @param <C> 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> DynamicArray<C>[] getConflictRecords() {
        return (DynamicArray[]) this.conflictRecords;
    }

    private DynamicArray<ColumnInterface>[] deletedRecords;
    
    /**
     * ほかの操作によって削除されたレコードを取得する。
     * 
     * @param <C> 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> DynamicArray<C>[] getDeletedRecords() {
        return (DynamicArray[]) this.deletedRecords;
    }
}
