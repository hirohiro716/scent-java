package com.hirohiro716.graphic.ui.control;

import javax.swing.JCheckBox;

import com.hirohiro716.graphic.ui.HorizontalAlignment;

/**
 * チェックボックスのクラス。
 * 
 * @author hiro
 *
 */
public class CheckBox extends MarkableControl {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected CheckBox(JCheckBox innerInstance) {
        super(innerInstance);
        this.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
    }
    
    /**
     * コンストラクタ。<br>
     * このチェックボックスに表示するテキストを指定する。
     * 
     * @param text
     */
    public CheckBox(String text) {
        this(new JCheckBox(text));
    }
    
    /**
     * コンストラクタ。
     */
    public CheckBox() {
        this(new JCheckBox((String) null));
    }
    
    @Override
    public JCheckBox getInnerInstance() {
        return (JCheckBox) super.getInnerInstance();
    }
}
