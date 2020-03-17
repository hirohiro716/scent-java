package com.hirohiro716.gui.control;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.hirohiro716.gui.HorizontalAlignment;

/**
 * テキストフィールドのクラス。
 * 
 * @author hiro
 *
 */
public class TextField extends TextInputControl {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected TextField(JTextField innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * このテキストフィールドの初期値を指定する。
     * 
     * @param text
     */
    public TextField(String text) {
        this(new JTextField());
        this.setText(text);
    }
    
    /**
     * コンストラクタ。
     */
    public TextField() {
        this((String) null);
    }
    
    @Override
    public JTextField getInnerInstance() {
        return (JTextField) super.getInnerInstance();
    }
    
    @Override
    public HorizontalAlignment getTextHorizontalAlignment() {
        switch (this.getInnerInstance().getHorizontalAlignment()) {
        case SwingConstants.LEFT:
            return HorizontalAlignment.LEFT;
        case SwingConstants.CENTER:
            return HorizontalAlignment.CENTER;
        case SwingConstants.RIGHT:
            return HorizontalAlignment.RIGHT;
        }
        return null;
    }
    
    @Override
    public void setTextHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        switch (horizontalAlignment) {
        case LEFT:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.LEFT);
            break;
        case CENTER:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.CENTER);
            break;
        case RIGHT:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.RIGHT);
            break;
        }
    }
}
