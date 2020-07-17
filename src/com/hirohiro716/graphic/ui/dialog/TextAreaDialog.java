package com.hirohiro716.graphic.ui.dialog;

import com.hirohiro716.graphic.ui.Frame;
import com.hirohiro716.graphic.ui.control.TextArea;

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
    protected void processBeforeShowing() {
        super.processBeforeShowing();
        this.getVerticalPaneOfControls().getGrowableControls().clear();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.getTextInputControl());
    }
}
