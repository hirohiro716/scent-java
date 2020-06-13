package com.hirohiro716.gui;

import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.dialog.ConfirmationDialog;
import com.hirohiro716.gui.dialog.MessageDialog;
import com.hirohiro716.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;

/**
 * GUIエディターの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> 編集するターゲットの型。
 */
public abstract class Editor<T> extends Window {
    
    /**
     * コンストラクタ。<br>
     * 表示するウィンドウのタイトル、幅、高さを指定する。
     * 
     * @param title 
     * @param width 
     * @param height 
     */
    public Editor(String title, int width, int height) {
        this.setTitle(title);
        this.setSize(width, height);
        this.addClosingEventHandler(new CloseEventHandler());
    }
    
    /**
     * ターゲットのインスタンス編集処理を行う。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @return 編集処理済みのターゲットインスタンス。
     * @throws Exception
     */
    protected abstract T editTarget() throws Exception;
    
    private T target;
    
    /**
     * 編集中のターゲットインスタンスを取得する。エディター表示前はnullを返す。
     * 
     * @return 結果。
     */
    public T getTarget() {
        return this.target;
    }
    
    /**
     * 編集中のターゲットとしてインスタンスをセットする。
     * 
     * @param target
     */
    protected void setTarget(T target) {
        this.target = target;
    }
    
    /**
     * このエディターを表示する直前に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @throws Exception 
     */
    protected abstract void processBeforeShowing() throws Exception;

    /**
     * このエディターを表示した直後に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @throws Exception 
     */
    protected abstract void processAfterShowing() throws Exception;

    @Override
    public void show() throws Exception {
        // Edit target
        this.target = this.editTarget();
        // Create content
        Control content = this.createContent();
        this.setContent(content);
        // Show
        this.processBeforeShowing();
        super.show();
        this.processAfterShowing();
    }
    
    private boolean isShowConfirmationBeforeClosing = true;
    
    /**
     * このエディターを閉じる前に確認を表示する場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isShowConfirmationBeforeClosing() {
        return this.isShowConfirmationBeforeClosing;
    }
    
    /**
     * このエディターを閉じる前に確認を表示する場合はtrueをセットする。
     * 
     * @param isShowConfirmationBeforeClosing
     */
    public void setShowConfirmationBeforeClosing(boolean isShowConfirmationBeforeClosing) {
        this.isShowConfirmationBeforeClosing = isShowConfirmationBeforeClosing;
    }

    /**
     * このエディターを閉じる直前に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @throws Exception 
     */
    protected abstract void processBeforeClosing() throws Exception;
    
    /**
     * 編集中のターゲットの情報をエディターに出力する。<br>
     * このメソッドはスーバークラスで自動的に実行されない。
     */
    public abstract void outputToEditor();
    
    /**
     * 編集中のターゲットにエディターの情報を入力する。<br>
     * このメソッドはスーバークラスで自動的に実行されない。
     */
    public abstract void inputFromEditor();
    
    /**
     * バリデーションに失敗した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    protected void showValidationException(Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        MessageDialog dialog = new MessageDialog(this);
        dialog.setTitle("バリデーションに失敗");
        dialog.setMessage(exception.getMessage());
        dialog.setProcessAfterClosing(processAfterDialogClosing);
        dialog.show();
    }

    /**
     * バリデーションに失敗した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     */
    protected final void showValidationException(Exception exception) {
        this.showValidationException(exception, null);
    }
    
    /**
     * このエディターを閉じる。
     */
    public void close() {
        this.isShowConfirmationBeforeClosing = false;
        super.close();
    }

    /**
     * このエディターを閉じる際のイベントハンドラー。
     * 
     * @author hiro
     *
     */
    private class CloseEventHandler extends EventHandler<FrameEvent> {
        
        private boolean isAgree = false;
        
        private ConfirmationDialog dialog = null;
        
        private final static String DIALOG_TITLE = "閉じる確認";
        
        private final static String DIALOG_MESSAGE = "この画面を閉じようとしています。";
        
        @Override
        protected void handle(FrameEvent event) {
            Editor<T> editor = Editor.this;
            if (editor.isShowConfirmationBeforeClosing && this.isAgree == false) {
                if (this.dialog != null) {
                    return;
                }
                editor.setCloseOperation(CloseOperation.DO_NOT_CLOSE);
                this.dialog = new ConfirmationDialog(editor);
                this.dialog.setTitle(CloseEventHandler.DIALOG_TITLE);
                this.dialog.setMessage(CloseEventHandler.DIALOG_MESSAGE);
                this.dialog.setDefaultValue(ResultButton.OK);
                this.dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<>() {
                    
                    @Override
                    public void execute(ResultButton dialogResult) {
                        CloseEventHandler handler = CloseEventHandler.this;
                        if (dialogResult == ResultButton.OK) {
                            try {
                                editor.processBeforeClosing();
                                handler.isAgree = true;
                                editor.close();
                            } catch (Exception exception) {
                            }
                        }
                        handler.dialog = null;
                    }
                });
                this.dialog.show();
            } else {
                editor.setCloseOperation(CloseOperation.DISPOSE);
            }
        }
    };
    
    /**
     * このエディターに表示するコンテンツを作成する。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @return 結果。
     * @throws Exception 
     */
    protected abstract Control createContent() throws Exception;
}
