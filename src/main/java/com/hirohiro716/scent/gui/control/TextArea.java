package com.hirohiro716.scent.gui.control;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.hirohiro716.scent.gui.Border;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.KeyEvent;

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
        this.setWrapText(true);
        this.setTabInputAllow(false);
        this.scrollPane = new ScrollPane((JScrollPane) this.getInnerInstanceForLayout());
        this.setBorder(Border.create(this.scrollPane.getInnerInstance().getBorder()));
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

    private ScrollPane scrollPane;
    
    /**
     * このテキストエリアのスクロールペインを取得する。
     * 
     * @return 結果。
     */
    public ScrollPane getScrollPane() {
        return this.scrollPane;
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
     * このコントロールに表示する文字列を折り返して表示する場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isWrapText() {
        return this.getInnerInstance().getLineWrap();
    }
    
    /**
     * このコントロールに表示する文字列を折り返して表示する場合はtrueをセットする。初期値はtrue。
     * 
     * @param isWrapText
     */
    public void setWrapText(boolean isWrapText) {
        this.getInnerInstance().setLineWrap(isWrapText);
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
