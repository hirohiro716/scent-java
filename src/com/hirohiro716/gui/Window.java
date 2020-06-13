package com.hirohiro716.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.Pane;

/**
 * GUIのウィンドウのクラス。
 * 
 * @author hiro
 *
 */
public class Window extends Frame<JFrame> {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param jFrame
     */
    protected Window(JFrame jFrame) {
        super(jFrame);
        this.setSize(400, 300);
        this.setCloseOperation(CloseOperation.DISPOSE);
        this.rootPane = Pane.newInstance((JPanel) this.getInnerInstance().getContentPane());
        this.rootPane.setParent(this);
    }
    
    /**
     * コンストラクタ。
     */
    public Window() {
        this(new JFrame());
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
        if (this.getInnerInstance().getMaximizedBounds() != null) {
            return this.getInnerInstance().getMaximizedBounds().getSize();
        }
        return this.getInnerInstance().getMaximumSize();
    }

    @Override
    public void setMaximumSize(Dimension dimension) {
        this.getInnerInstance().setMaximizedBounds(new Rectangle(dimension));
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
    public boolean isUndecorated() {
        return this.getInnerInstance().isUndecorated();
    }

    @Override
    public void setUndecorated(boolean isUndecorated) {
        this.getInnerInstance().setUndecorated(isUndecorated);
    }
    
    /**
     * このウィンドウを最大化する。
     */
    public void maximize() {
        this.getInnerInstance().setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    /**
     * このウィンドウが最大化されている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isMaximized() {
        return this.getInnerInstance().getExtendedState() == JFrame.MAXIMIZED_BOTH;
    }
    
    /**
     * このウィンドウを最小化する。
     */
    public void minimize() {
        this.getInnerInstance().setExtendedState(JFrame.ICONIFIED);
    }

    /**
     * このウィンドウが最小化されている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isMinimized() {
        return this.getInnerInstance().getExtendedState() == JFrame.ICONIFIED;
    }
    
    /**
     * ウィンドウに閉じる要求があった場合の処理を表す列挙型。
     * 
     * @author hiro
     *
     */
    public enum CloseOperation {
        /**
         * ウィンドウを破棄する。
         */
        DISPOSE,
        /**
         * ウインドウを非表示にする。
         */
        HIDE,
        /**
         * ウィンドウを閉じない。
         */
        DO_NOT_CLOSE,
        /**
         * アプリケーションの終了。
         */
        APPLICATION_EXIT,
    }
    
    @Override
    public boolean isClosable() {
        return this.getInnerInstance().getDefaultCloseOperation() != JFrame.DO_NOTHING_ON_CLOSE;
    }
    
    @Override
    public void setClosable(boolean isClosable) {
        if (isClosable) {
            this.setCloseOperation(CloseOperation.DISPOSE);
        } else {
            this.setCloseOperation(CloseOperation.DO_NOT_CLOSE);
        }
    }

    /**
     * ウィンドウに閉じる要求があった場合の処理をセットする。DISPOSEが初期値。
     * 
     * @param closeOperation
     */
    public void setCloseOperation(CloseOperation closeOperation) {
        if (closeOperation == null) {
            return;
        }
        switch (closeOperation) {
        case DISPOSE:
            this.getInnerInstance().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            break;
        case HIDE:
            this.getInnerInstance().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            break;
        case DO_NOT_CLOSE:
            this.getInnerInstance().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            break;
        case APPLICATION_EXIT:
            this.getInnerInstance().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            break;
        }
    }
}
