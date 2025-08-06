package com.hirohiro716.scent.gui.dialog;

import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.AutocompleteTextField;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.KeyEvent;

/**
 * オートコンプリート機能付きのテキストフィールドを表示するダイアログのクラス。
 */
public class AutocompleteTextFieldDialog extends TextInputControlDialog<AutocompleteTextField> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public AutocompleteTextFieldDialog(Frame<?> owner) {
        super(owner);
        int fontSize = this.getPane().getFont().getSize();
        this.getPane().setHeight(fontSize * 26);
        this.getTextInputControl().setPadding(fontSize / 4);
    }
    
    private boolean isEnterKeyToOkayDisabled = false;
    
    /**
     * テキスト入力コントロール上でEnterキーを押下した際の決定を無効化する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isEnterKeyToOkayDisabled
     */
    public void setEnterKeyToOkayDisabled(boolean isEnterKeyToOkayDisabled) {
        this.isEnterKeyToOkayDisabled = isEnterKeyToOkayDisabled;
    }
    
    @Override
    protected AutocompleteTextField createTextInputControl() {
        AutocompleteTextFieldDialog dialog = this;
        AutocompleteTextField control = new AutocompleteTextField();
        control.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                if (dialog.isEnterKeyToOkayDisabled == false && event.getKeyCode() == KeyCode.ENTER && control.getText().length() > 0 && control.getPopup().isVisible() == false) {
                    dialog.setDialogResult(control.getText());
                    dialog.close();
                }
            }
        });
        return control;
    }
}
