package com.hirohiro716.gui.control;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.KeyEvent;

/**
 * テキストエリアのクラス。
 * 
 * @author hiro
 *
 */
public class TextArea extends TextInputControl {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected TextArea(JTextArea innerInstance) {
        super(innerInstance, new JScrollPane(innerInstance));
        this.setTabInputAllow(false);
    }

    /**
     * コンストラクタ。<br>
     * このテキストエリアの初期値を指定する。
     * 
     * @param text
     */
    public TextArea(String text) {
        this(new JTextArea());
        this.setText(text);
    }
    
    /**
     * コンストラクタ。
     */
    public TextArea() {
        this((String) null);
    }
    
    @Override
    public JTextArea getInnerInstance() {
        return (JTextArea) super.getInnerInstance();
    }
    
    @Override
    public HorizontalAlignment getTextHorizontalAlignment() {
        return HorizontalAlignment.LEFT;
    }
    
    @Override
    public void setTextHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment != HorizontalAlignment.LEFT) {
            throw new IllegalArgumentException("The horizontal position of the text area is fixed to the left.");
        }
    }
    
    /**
     * このテキストエリアでキーボードのキーを押した際のイベントハンドラー。
     */
    private EventHandler<KeyEvent> keyPressedEventHandler = new EventHandler<KeyEvent>() {

        @Override
        protected void handle(KeyEvent event) {
            TextArea control = TextArea.this;
            switch (event.getKeyCode()) {
            case TAB:
                if (event.isShiftDown()) {
                    control.getInnerInstance().transferFocusBackward();
                } else {
                    control.getInnerInstance().transferFocus();
                }
                event.consume();
                break;
            default:
                break;
            }
        }
    };
    
    /**
     * このテキストエリアにタブの入力を許可する場合はtrueをセットする。
     * 
     * @param isAllowTabInput
     */
    public void setTabInputAllow(boolean isAllowTabInput) {
        if (isAllowTabInput) {
            this.removeEventHandler(this.keyPressedEventHandler);
        } else {
            this.addKeyPressedEventHandler(this.keyPressedEventHandler);
        }
    }
}
