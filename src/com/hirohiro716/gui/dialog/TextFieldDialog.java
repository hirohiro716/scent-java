package com.hirohiro716.gui.dialog;

import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.control.TextField;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.KeyEvent;

/**
 * テキストフィールドを表示するダイアログのクラス。
 * 
 * @author hiro
 *
 */
public class TextFieldDialog extends TextInputControlDialog<TextField> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public TextFieldDialog(Frame<?> owner) {
        super(owner);
        int fontSize = this.getPane().getFont().getSize();
        this.getPane().setHeight(fontSize * 26);
        this.getTextInputControl().setPadding(fontSize / 4);
    }
    
    @Override
    protected TextField createTextInputControl() {
        TextFieldDialog dialog = this;
        TextField control = new TextField();
        control.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                if (event.getKeyCode() == KeyCode.ENTER && control.getText().length() > 0) {
                    dialog.setDefaultValue(control.getText());
                    dialog.close();
                }
            }
        });
        return control;
    }
}
