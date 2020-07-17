package com.hirohiro716.graphic.ui.dialog;

import java.util.concurrent.Callable;

import com.hirohiro716.graphic.ui.Frame;
import com.hirohiro716.graphic.ui.GUI;
import com.hirohiro716.graphic.ui.KeyCode;
import com.hirohiro716.graphic.ui.control.Button;
import com.hirohiro716.graphic.ui.control.CenterPane;
import com.hirohiro716.graphic.ui.control.Control;
import com.hirohiro716.graphic.ui.control.WaitCircle;
import com.hirohiro716.graphic.ui.event.ActionEvent;
import com.hirohiro716.graphic.ui.event.EventHandler;

/**
 * 待機中サークルダイアログのクラス。
 * 
 * @author hiro
 * 
 * @param <R> このダイアログで実行するコールバックの結果の型。
 */
public class WaitCircleDialog<R> extends MessageableDialog<R> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public WaitCircleDialog(Frame<?> owner) {
        super(owner);
    }
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーと、実行するコールバックを指定する。
     * 
     * @param owner
     * @param callable 
     */
    public WaitCircleDialog(Frame<?> owner, Callable<R> callable) {
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
    
    private WaitCircle waitCircle;
    
    @Override
    protected Control createInputControl() {
        CenterPane centerPane = new CenterPane();
        this.waitCircle = new WaitCircle();
        centerPane.setControl(this.waitCircle);
        return centerPane;
    }
    
    /**
     * このダイアログの待機中サークルを取得する。
     * 
     * @return 結果。
     */
    public WaitCircle getWaitCircle() {
        return this.waitCircle;
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
    
    @Override
    protected Button[] createButtons() {
        WaitCircleDialog<R> dialog = this;
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
    protected void processBeforeShowing() {
        super.processBeforeShowing();
        int size = this.waitCircle.getFont().getSize() * 5;
        this.waitCircle.setMinimumSize(size, size);
    }
    
    @Override
    protected Control getInitialFocusControl() {
        return null;
    }
    
    @Override
    protected void processAfterShowing() {
        WaitCircleDialog<R> dialog = this;
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
                        dialog.close();
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
