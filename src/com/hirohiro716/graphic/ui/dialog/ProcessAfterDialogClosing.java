package com.hirohiro716.graphic.ui.dialog;

/**
 * ダイアログが閉じられた後に実行される処理のインターフェース。
 * 
 * @author hiro
 *
 * @param <R> ダイアログの表示結果の型。
 */
public interface ProcessAfterDialogClosing<R> {
    
    /**
     * ダイアログが閉じられた後に実行される処理。
     * 
     * @param dialogResult
     */
    public abstract void execute(R dialogResult);
}