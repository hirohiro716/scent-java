package com.hirohiro716.gui;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hirohiro716.Dimension;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.RootPane;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;
import com.hirohiro716.gui.event.KeyEvent;

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
        Window window = this;
        this.setSize(400, 300);
        this.setCloseOperation(CloseOperation.DISPOSE);
        this.rootPane = RootPane.newInstance((JPanel) this.getInnerInstance().getContentPane());
        this.rootPane.setParent(this);
        this.addActivatedEventHandler(new EventHandler<FrameEvent>() {

            @Override
            protected void handle(FrameEvent event) {
                if (window.isAddedProcessWhenKeyTyped) {
                    return;
                }
                window.isAddedProcessWhenKeyTyped = true;
                for (KeyCode keyCode : window.mapProcessWhenKeyTyped.keySet()) {
                    Runnable runnable = window.mapProcessWhenKeyTyped.get(keyCode);
                    for (Control control : window.getRootPane().getChildren().findAll()) {
                        control.addKeyPressedEventHandler(new KeyPressedEventHandler(keyCode));
                        control.addKeyReleasedEventHandler(new KeyReleasedEventHandler(keyCode, runnable));
                    }
                }
            }
        });

    }
    
    /**
     * コンストラクタ。
     */
    public Window() {
        this(new JFrame());
    }
    
    private RootPane rootPane;

    @Override
    public RootPane getRootPane() {
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

    private Dimension maximumSize = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    
    @Override
    public Dimension getMaximumSize() {
        java.awt.Dimension awtDimension = this.getInnerInstance().getMaximumSize();
        if (this.getInnerInstance().getMaximizedBounds() != null) {
            awtDimension = this.getInnerInstance().getMaximizedBounds().getSize();
        }
        if (this.maximumSize.getWidth() != awtDimension.getWidth() || this.maximumSize.getHeight() != awtDimension.getHeight()) {
            this.maximumSize = new Dimension(awtDimension.width, awtDimension.height);
        }
        return this.maximumSize;
    }

    @Override
    public void setMaximumSize(Dimension dimension) {
        this.maximumSize = dimension;
        this.getInnerInstance().setMaximizedBounds(new Rectangle(dimension.getIntegerWidth(), dimension.getIntegerHeight()));
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
    
    private Map<KeyCode, Runnable> mapProcessWhenKeyTyped = new LinkedHashMap<>();
    
    private boolean isAddedProcessWhenKeyTyped = false;
    
    /**
     * このレコード検索ウィンドウでキーを押した際の処理を追加する。
     * 
     * @param keyCode 対象のキーコード。
     * @param runnable 実行する処理。
     */
    protected void addProcessWhenKeyTyped(KeyCode keyCode, Runnable runnable) {
        this.mapProcessWhenKeyTyped.put(keyCode, runnable);
    }
    
    private Map<Control, Boolean> mapKeyTyped = new HashMap<>();
    
    /**
     * このレコード検索ウィンドウでキーを押した際のイベントハンドラー。
     * 
     * @author hiro
     *
     */
    private class KeyPressedEventHandler extends EventHandler<KeyEvent> {
        
        /**
         * コンストラクタ。<br>
         * 対象のキーコードを指定する。
         * 
         * @param keyCode
         */
        public KeyPressedEventHandler(KeyCode keyCode) {
            this.keyCode = keyCode;
        }
        
        private KeyCode keyCode;
        
        @Override
        protected void handle(KeyEvent event) {
            Window window = Window.this;
            if (event.getKeyCode() == this.keyCode) {
                window.mapKeyTyped.put(event.getSource(), true);
            }
        }
    }

    /**
     * このレコード検索ウィンドウでキーを離した際のイベントハンドラー。
     * 
     * @author hiro
     *
     */
    private class KeyReleasedEventHandler extends EventHandler<KeyEvent> {

        /**
         * コンストラクタ。<br>
         * 対象のキーコード、処理を指定する。
         * 
         * @param keyCode
         * @param runnable 
         */
        public KeyReleasedEventHandler(KeyCode keyCode, Runnable runnable) {
            this.keyCode = keyCode;
            this.runnable = runnable;
        }
        
        private KeyCode keyCode;
        
        private Runnable runnable;
        
        @Override
        protected void handle(KeyEvent event) {
            Window window = Window.this;
            if (event.getKeyCode() == this.keyCode && window.mapKeyTyped.containsKey(event.getSource())) {
                this.runnable.run();
                window.mapKeyTyped.clear();
            }
        }
    }
}
