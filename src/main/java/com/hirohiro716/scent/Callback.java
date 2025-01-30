package com.hirohiro716.scent;

/**
 * コールバックのインターフェース。
 * 
 * @author hiro
 *
 * @param <P> パラメーターの型。
 * @param <R> 処理結果の型。
 */
public interface Callback<P, R> {

    /**
     * コールバックを実行する。
     * 
     * @param parameter
     * @return
     */
    public abstract R call(P parameter);
}
