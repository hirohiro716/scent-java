package com.hirohiro716.scent.gui.dialog;

import java.util.concurrent.Callable;

import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.ProgressBar;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * プログレスバーダイアログのクラス。
 * 
 * @author hiro
 * 
 * @param <R> このダイアログで実行するコールバックの結果の型。
 */
public class ProgressBarDialog<R> extends MessageableDialog<R> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public ProgressBarDialog(Frame<?> owner) {
        super(owner);
    }
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーと、実行するコールバックを指定する。
     * 
     * @param owner
     * @param callable 
     */
    public ProgressBarDialog(Frame<?> owner, Callable<R> callable) {
        this(owner);
        this.callable = callable;
    }

    private Callable<R> callable;
    
    /**
     * このダイアログで実行するコールバックを取得する。
     * 
     * @return 結果。
     */
    public Callable<R> getCallable() {
        return this.callable;
    }
    
    /**
     * このダイアログで実行するコールバックをセットする。
     * 
     * @param callable
     */
    public void setCallable(Callable<R> callable) {
        this.callable = callable;
    }

    @Override
    public boolean isCancelableByClickBackground() {
        return false;
    }
    
    private ProgressBar progressBar;
    
    @Override
    protected Control createInputControl() {
        this.progressBar = new ProgressBar();
        return this.progressBar;
    }
    
    /**
     * このダイアログのプログレスバーを取得する。
     * 
     * @return 結果。
     */
    public ProgressBar getProgressBar() {
        return this.progressBar;
    }
    
    /**
     * このダイアログのプログレスバーの最大進捗をセットする。
     * 
     * @param progress
     */
    public void setMaximumProgress(int progress) {
        this.progressBar.setMaximumProgress(progress);
    }
    
    /**
     * このダイアログのプログレスバーの進捗をセットする。
     * 
     * @param progress
     */
    public void setProgress(int progress) {
        this.progressBar.setProgress(progress);
    }

    private Button buttonCancel;
    
    /**
     * このダイアログがキャンセル可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isCancelable() {
        return this.buttonCancel.isVisible();
    }
    
    /**
     * このダイアログをキャンセル可能にする場合はtrueをセットする。初期値はfalse。
     * 
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.buttonCancel.setVisible(isCancelable);
    }
    
    private boolean isCanceled = false;
    
    /**
     * このダイアログのキャンセルボタンが押されている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isCanceled() {
        return this.isCanceled;
    }

    private boolean isCloseAutomaticallyWhenFinished = true;
    
    /**
     * 実行するコールバックが終了した際に自動的にダイアログを閉じる場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isCloseAutomaticallyWhenFinished() {
        return this.isCloseAutomaticallyWhenFinished;
    }
    
    /**
     * 実行するコールバックが終了した際に自動的にダイアログを閉じる場合はtrueをセットする。初期値はtrue。
     * 
     * @param isCloseAutomaticallyWhenFinished
     */
    public void setCloseAutomaticallyWhenFinished(boolean isCloseAutomaticallyWhenFinished) {
        this.isCloseAutomaticallyWhenFinished = isCloseAutomaticallyWhenFinished;
    }
    
    @Override
    protected Button[] createButtons() {
        ProgressBarDialog<R> dialog = this;
        this.buttonCancel = new Button("キャンセル(C)");
        this.buttonCancel.setMnemonic(KeyCode.C);
        this.buttonCancel.setVisible(false);
        this.buttonCancel.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.buttonCancel.setDisabled(true);
                dialog.isCanceled = true;
            }
        });
        return new Button[] { this.buttonCancel };
    }
    
    @Override
    protected Control getInitialFocusControl() {
        return null;
    }
    
    @Override
    protected void processAfterShowing() {
        ProgressBarDialog<R> dialog = this;
        super.processAfterShowing();
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                if (dialog.callable != null) {
                    try {
                        dialog.result = dialog.callable.call();
                    } catch (Exception exception) {
                        dialog.exception = exception;
                    }
                }
                GUI.executeLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        if (dialog.isCloseAutomaticallyWhenFinished) {
                            dialog.close();
                        }
                    }
                });
            }
        });
        thread.start();
    }
    
    private R result;
    
    @Override
    public void setDefaultValue(R defaultValue) {
        this.result = defaultValue;
    }
    
    @Override
    public R getDialogResult() {
        return this.result;
    }
    
    @Override
    protected void setDialogResult(R result) {
        this.result = result;
    }
    
    @Override
    protected void setCanceledDialogResult() {
    }
    
    private Exception exception = null;
    
    /**
     * このダイアログの処理中に発生した例外を取得する。
     * 
     * @return 結果。
     */
    public Exception getException() {
        return this.exception;
    }
}
