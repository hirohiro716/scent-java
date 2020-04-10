package com.hirohiro716.database;

import java.sql.SQLException;

/**
 * データが存在しない場合に発生する例外クラス。
 * 
 * @author hiro
 * 
 */
@SuppressWarnings("serial")
public class DataNotFoundException extends SQLException {
    
    private final static String MESSAGE = "データが存在しません。";
    
    /**
     * 初期の例外メッセージを持つ新規例外を構築する。
     */
    public DataNotFoundException() {
        super(DataNotFoundException.MESSAGE);
    }
    
    /**
     * 指定された例外メッセージを持つ新規例外を構築する。
     * 
     * @param message
     */
    public DataNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 初期の例外メッセージを持つ新規例外を構築する。
     * 
     * @param cause
     */
    public DataNotFoundException(Throwable cause) {
        super(DataNotFoundException.MESSAGE, cause);
    }
    
    /**
     * 指定された例外メッセージおよび、原因を使用して新規例外を構築する。causeのメッセージがこの例外のメッセージに自動的に統合されることはない。
     * 
     * @param message
     * @param cause
     */
    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
