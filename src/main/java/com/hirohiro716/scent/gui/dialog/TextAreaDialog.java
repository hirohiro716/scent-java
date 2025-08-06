package com.hirohiro716.scent.gui.dialog;

import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.control.TextArea;

/**
 * テキストエリアを表示するダイアログのクラス。
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
    
    private boolean isCancelableByClickBackground = false;

    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelableByClickBackground;
    }
    
    /**
     * このダイアログを背景のクリックでキャンセル可能にする場合はtrueをセットする。初期値はfalse。
     * 
     * @param isCancelableByClickBackground
     */
    public void setCancelableByClickBackground(boolean isCancelableByClickBackground) {
        this.isCancelableByClickBackground = isCancelableByClickBackground;
    }
    
    @Override
    protected void processBeforeShowing() {
        super.processBeforeShowing();
        this.getVerticalPaneOfControls().getGrowableControls().clear();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.getTextInputControl());
    }
}
