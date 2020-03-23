package com.hirohiro716.gui;

import java.awt.Dimension;

import com.hirohiro716.gui.Window.CloseOperation;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.dialog.ConfirmDialog;
import com.hirohiro716.gui.dialog.ProcessAfterClose;
import com.hirohiro716.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;

/**
 * GUIのエディターの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> 編集するターゲットの型。
 */
public abstract class Editor<T> {
    
    /**
     * コンストラクタ。<br>
     * 表示するウィンドウの幅と高さを指定する。
     * 
     * @param width 
     * @param height 
     */
    public Editor(int width, int height) {
        this.window = new Window();
        this.window.setSize(width, height);
        this.window.addClosingEventHandler(new CloseEventHandler());
    }
    
    /**
     * ターゲットのインスタンス編集処理を行う。
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
    
    private Window window;
    
    /**
     * このエディターのウィンドウを取得する。
     * 
     * @return 結果。
     */
    public Window getWindow() {
        return this.window;
    }
    
    /**
     * このエディターを表示する直前に実行される処理。
     * 
     * @throws Exception 
     */
    protected abstract void processBeforeShow() throws Exception;
    
    /**
     * このエディターを表示する。
     * 
     * @throws Exception
     */
    public void show() throws Exception {
        this.editTarget();
        Control control = this.createContent();
        this.window.getPane().getChildren().add(control);
        this.window.getPane().addSizeChangeListener(new ChangeListener<>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                control.setSize(changedValue);
            }
        });
        this.processBeforeShow();
        this.window.show();
    }
    
    private boolean isShowConfirmationBeforeClose = true;
    
    /**
     * このエディターを閉じる前に確認を表示する場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isShowConfirmationBeforeClose() {
        return this.isShowConfirmationBeforeClose;
    }
    
    /**
     * このエディターを閉じる前に確認を表示する場合はtrueをセットする。
     * 
     * @param isShowConfirmationBeforeClose
     */
    public void setShowConfirmationBeforeClose(boolean isShowConfirmationBeforeClose) {
        this.isShowConfirmationBeforeClose = isShowConfirmationBeforeClose;
    }

    /**
     * このエディターを閉じる直前に実行される処理。
     * 
     * @throws Exception 
     */
    protected abstract void processBeforeClose() throws Exception;
    
    /**
     * このエディターを閉じる。
     */
    public void close() {
        this.window.close();
    }
    
    /**
     * このエディターに表示するコンテンツを作成する。
     * 
     * @return 結果。
     */
    protected abstract Control createContent(); 
    
    /**
     * このエディターを閉じる際のイベントハンドラー。
     * 
     * @author hiro
     *
     */
    private class CloseEventHandler extends EventHandler<FrameEvent> {
        
        private boolean isAgree = false;
        
        private ConfirmDialog dialog = null;
        
        private final static String DIALOG_TITLE = "閉じる確認";
        
        private final static String DIALOG_MESSAGE = "この画面を閉じようとしています。";
        
        @Override
        protected void handle(FrameEvent event) {
            Editor<T> editor = Editor.this;
            if (editor.isShowConfirmationBeforeClose && this.isAgree == false) {
                if (this.dialog != null) {
                    return;
                }
                editor.window.setCloseOperation(CloseOperation.DO_NOT_CLOSE);
                this.dialog = new ConfirmDialog(editor.window);
                this.dialog.setTitle(DIALOG_TITLE);
                this.dialog.setMessage(DIALOG_MESSAGE);
                this.dialog.setDefaultValue(ResultButton.OK);
                this.dialog.setProcessAfterClose(new ProcessAfterClose<>() {
                    
                    @Override
                    public void execute(ResultButton dialogResult) {
                        CloseEventHandler handler = CloseEventHandler.this;
                        if (dialogResult == ResultButton.OK) {
                            try {
                                editor.processBeforeClose();
                                handler.isAgree = true;
                                editor.window.close();
                            } catch (Exception exception) {
                            }
                        }
                        handler.dialog = null;
                    }
                });
                this.dialog.show();
            } else {
                editor.window.setCloseOperation(CloseOperation.DISPOSE);
            }
        }
    };
}
