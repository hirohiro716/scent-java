package com.hirohiro716;

/**
 * 例外を処理するハンドラーのインターフェース。
 * 
 * @author hiro
 *
 */
public interface ExceptionHandler {
    
    /**
     * 例外に対する処理を実行する。
     * 
     * @param exception
     */
    public abstract void handle(Exception exception);
}