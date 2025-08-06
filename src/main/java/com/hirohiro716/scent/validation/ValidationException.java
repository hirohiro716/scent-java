package com.hirohiro716.scent.validation;

/**
 * 検証に失敗した場合に発生する例外クラス。
 */
public class ValidationException extends Exception {

    /**
     * 指定された例外メッセージを持つ新規例外を構築する。
     * 
     * @param message
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * 指定された例外メッセージおよび、原因を使用して新規例外を構築する。causeのメッセージがこの例外のメッセージに自動的に統合されることはない。
     * 
     * @param message
     * @param cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
