package com.hirohiro716.gui.dialog;

import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.control.TextArea;

/**
 * テキストエリアを表示するダイアログのクラス。
 * 
 * @author hiro
 *
 */
public class TextAreaDialog extends TextInputControlDialog<TextArea> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public TextAreaDialog(Frame<?> owner) {
        super(owner);
        this.getPane().setHeight(this.getPane().getFont().getSize() * 32);
    }

    @Override
    protected TextArea createTextInputControl() {
        TextArea control = new TextArea();
        return control;
    }

    @Override
    protected void processBeforeShow() {
        super.processBeforeShow();
        this.getVerticalPaneOfControls().getGrowableControls().clear();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.getTextInputControl());
    }
}
