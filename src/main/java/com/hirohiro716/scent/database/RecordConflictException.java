package com.hirohiro716.scent.database;

import java.sql.SQLException;

import com.hirohiro716.scent.DynamicArray;

/**
 * データベースレコードがコンフリクトした場合に発生する例外クラス。
 * 
 * @author hiro
 */
public class RecordConflictException extends SQLException {
    
    private final static String MESSAGE = "データベースレコードが競合しました。";

    /**
     * コンフリクトした原因のレコードと例外メッセージを指定して新規例外を構築する。
     * 
     * @param <C>
     * @param message
     * @param causeRecords
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> RecordConflictException(String message, DynamicArray<C>[] causeRecords) {
        super(message);
        this.causeRecords = (DynamicArray[]) causeRecords;
    }

    /**
     * コンフリクトの原因のレコードを指定して、初期の例外メッセージを持つ新規例外を構築する。
     * 
     * @param <C>
     * @param causeRecords コンフリクトした原因のレコード。
     */
    public <C extends ColumnInterface> RecordConflictException(DynamicArray<C>[] causeRecords) {
        this(RecordConflictException.MESSAGE, causeRecords);
    }

    private DynamicArray<ColumnInterface>[] causeRecords;
    
    /**
     * コンフリクトした原因のレコードを取得する。
     * 
     * @param <C> 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> DynamicArray<C>[] getCauseRecord() {
        return (DynamicArray[]) this.causeRecords;
    }
}
