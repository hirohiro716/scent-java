package com.hirohiro716.scent.gui.control;

import javax.swing.JToggleButton;

/**
 * トグルボタンのクラス。
 */
public class ToggleButton extends MarkableControl {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected ToggleButton(JToggleButton innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * このトグルボタンに表示するテキストを指定する。
     * 
     * @param text
     */
    public ToggleButton(String text) {
        this(new JToggleButton(text));
    }

    /**
     * コンストラクタ。
     */
    public ToggleButton() {
        this(new JToggleButton((String) null));
    }
    
    @Override
    public JToggleButton getInnerInstance() {
        return (JToggleButton) super.getInnerInstance();
    }
}
