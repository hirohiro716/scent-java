package com.hirohiro716.graphic.ui;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.hirohiro716.graphic.ui.control.Control;
import com.hirohiro716.graphic.ui.control.Pane;
import com.hirohiro716.graphic.ui.event.ChangeListener;

/**
 * GUIの子ウィンドウのクラス。
 * 
 * @author hiro
 *
 */
public class ChildWindow extends Frame<JDialog> {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param jDialog
     */
    protected ChildWindow(JDialog jDialog) {
        super(jDialog);
        ChildWindow window = ChildWindow.this;
        this.getInnerInstance().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.rootPane = Pane.newInstance((JPanel) this.getInnerInstance().getContentPane());
        this.rootPane.setParent(this);
        this.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                Dimension maximumSize = window.getMaximumSize();
                if (changedValue.width > maximumSize.width) {
                    window.setWidth(maximumSize.width);
                    window.setResizable(false);
                    window.createResizableThread().start();
                }
                if (changedValue.height > maximumSize.height) {
                    window.setHeight(maximumSize.height);
                    window.setResizable(false);
                    window.createResizableThread().start();
                }
            }
        });
    }
    
    /**
     * コンストラクタ。<br>
     * この子ウィンドウのオーナーを指定する。
     * 
     * @param owner
     */
    public ChildWindow(Frame<?> owner) {
        this(new JDialog(owner.getInnerInstance()));
        this.owner = owner;
    }
    
    /**
     * コンストラクタ。
     */
    public ChildWindow() {
        this(new JDialog());
    }
    
    private Frame<?> owner = null;
    
    /**
     * この子ウィンドウのオーナーを取得する。
     * 
     * @return 結果。
     */
    public Frame<?>  getOwner() {
        return this.owner;
    }
    
    private Pane rootPane;

    @Override
    public Pane getRootPane() {
        return this.rootPane;
    }

    @Override
    public void setContent(Control control) {
        this.rootPane.getChildren().clear();
        this.rootPane.getChildren().add(control);
    }
    
    @Override
    public String getTitle() {
        return this.getInnerInstance().getTitle();
    }
    
    @Override
    public void setTitle(String title) {
        this.getInnerInstance().setTitle(title);
    }
    
    @Override
    public Dimension getMaximumSize() {
        return this.getInnerInstance().getMaximumSize();
    }
    
    @Override
    public void setMaximumSize(Dimension dimension) {
        this.getInnerInstance().setMaximumSize(dimension);
    }
    
    @Override
    public boolean isResizable() {
        return this.getInnerInstance().isResizable();
    }
    
    @Override
    public void setResizable(boolean isResizable) {
        this.getInnerInstance().setResizable(isResizable);
    }
    
    @Override
    public boolean isClosable() {
        return this.getInnerInstance().getDefaultCloseOperation() != JDialog.DO_NOTHING_ON_CLOSE;
    }
    
    @Override
    public void setClosable(boolean isClosable) {
        if (isClosable) {
            this.getInnerInstance().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        } else {
            this.getInnerInstance().setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        }
    }

    @Override
    public boolean isUndecorated() {
        return this.getInnerInstance().isUndecorated();
    }

    @Override
    public void setUndecorated(boolean isUndecorated) {
        this.getInnerInstance().setUndecorated(isUndecorated);
    }
    
    /**
     * この子ウィンドウをモーダルに設定する場合はtrueをセットする。
     * 
     * @param isModal
     */
    public void setModal(boolean isModal) {
        this.getInnerInstance().setModal(isModal);
        this.getInnerInstance().setModalityType(ModalityType.DOCUMENT_MODAL);
    }
    
    /**
     * 遅延してウィンドウをリサイズ可能にするスレッドを作成する。
     * 
     * @return 結果。
     */
    private ResizableThread createResizableThread() {
        return new ResizableThread();
    }
    
    /**
     * 遅延してウィンドウをリサイズ可能にするスレッドクラス。
     * 
     * @author hiro
     *
     */
    private class ResizableThread extends Thread {
        
        /**
         * コンストラクタ。
         */
        private ResizableThread() {
            super(new Runnable() {
                
                @Override
                public void run() {
                    ChildWindow window = ChildWindow.this;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException exception) {
                    }
                    window.setResizable(true);
                }
            });
        }
    }
}
