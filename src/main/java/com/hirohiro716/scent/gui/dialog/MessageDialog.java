package com.hirohiro716.scent.gui.dialog;

import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * メッセージダイアログのクラス。
 * 
 * @author hiro
*/
public class MessageDialog extends MessageableDialog<ResultButton> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public MessageDialog(Frame<?> owner) {
        super(owner);
    }

    @Override
    public boolean isCancelableByClickBackground() {
        return false;
    }

    @Override
    protected Control createInputControl() {
        return null;
    }
    
    private Button button;
    
    @Override
    protected Button[] createButtons() {
        MessageDialog dialog = this;
        this.button = new Button("OK");
        this.button.setMnemonic(KeyCode.O);
        this.button.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.result = ResultButton.OK;
                dialog.close();
            }
        });
        return new Button[] {this.button};
    }
    
    @Override
    protected Control getInitialFocusControl() {
        return this.button;
    }

    @Override
    public void setDefaultValue(ResultButton value) {
    }

    private ResultButton result = null;
    
    @Override
    public ResultButton getDialogResult() {
        return this.result;
    }

    @Override
    protected void setDialogResult(ResultButton result) {
        this.result = result;
    }

    @Override
    protected void setCanceledDialogResult() {
        this.result = null;
    }
}
