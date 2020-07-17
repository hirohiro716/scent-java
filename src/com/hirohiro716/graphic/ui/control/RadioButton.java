package com.hirohiro716.graphic.ui.control;

import javax.swing.JRadioButton;

/**
 * ラジオボタンのクラス。
 * 
 * @author hiro
 *
 */
public class RadioButton extends MarkableControl {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected RadioButton(JRadioButton innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * このラジオボタンに表示するテキストを指定する。
     * 
     * @param text
     */
    public RadioButton(String text) {
        this(new JRadioButton(text));
    }

    /**
     * コンストラクタ。
     */
    public RadioButton() {
        this(new JRadioButton((String) null));
    }
    
    @Override
    public JRadioButton getInnerInstance() {
        return (JRadioButton) super.getInnerInstance();
    }
}
