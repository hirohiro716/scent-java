package com.hirohiro716.gui;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.event.ChangeListener;

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
        this.getInnerInstance().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.pane = Pane.newInstance((JPanel) this.getInnerInstance().getContentPane());
        this.pane.setParent(this);
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
    
    private Pane pane;
    
    @Override
    public Pane getPane() {
        return this.pane;
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
    public void setMaximumSize(int width, int height) {
        this.getInnerInstance().setMaximumSize(new Dimension(width, height));
        this.getPane().addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                ChildWindow dialog = ChildWindow.this;
                if (changedValue.width > width || changedValue.height > height) {
                    dialog.setSize(width, height);
                    dialog.setResizable(false);
                    Thread thread = new Thread(new Runnable() {
                        
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException exception) {
                            }
                            dialog.setResizable(true);
                        }
                    });
                    thread.start();
                }
            }
        });
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
}
