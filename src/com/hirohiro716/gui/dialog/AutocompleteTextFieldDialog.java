package com.hirohiro716.gui.dialog;

import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.control.AutocompleteTextField;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.KeyEvent;

/**
 * オートコンプリート機能付きのテキストフィールドを表示するダイアログのクラス。
 * 
 * @author hiro
 *
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
    
    @Override
    protected AutocompleteTextField createTextInputControl() {
        AutocompleteTextFieldDialog dialog = this;
        AutocompleteTextField control = new AutocompleteTextField();
        control.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                if (event.getKeyCode() == KeyCode.ENTER && control.getText().length() > 0 && control.getPopup().isVisible() == false) {
                    dialog.setDialogResult(control.getText());
                    dialog.close();
                }
            }
        });
        return control;
    }
}
