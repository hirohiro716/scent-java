package com.hirohiro716.scent.gui.dialog;

import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * 確認ダイアログのクラス。
 * 
 * @author hiro
*/
public class ConfirmationDialog extends MessageableDialog<ResultButton> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public ConfirmationDialog(Frame<?> owner) {
        super(owner);
    }
    
    @Override
    public boolean isCancelableByClickBackground() {
        return true;
    }

    @Override
    protected Control createInputControl() {
        return null;
    }
    
    private Button buttonOfOK;
    
    private Button buttonOfCancel;
    
    @Override
    protected Button[] createButtons() {
        ConfirmationDialog dialog = this;
        this.buttonOfOK = new Button("OK");
        this.buttonOfOK.setMnemonic(KeyCode.O);
        this.buttonOfOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.result = ResultButton.OK;
                dialog.close();
            }
        });
        this.buttonOfCancel = new Button("キャンセル(C)");
        this.buttonOfCancel.setMnemonic(KeyCode.C);
        this.buttonOfCancel.addActionEventHandler(new EventHandler<ActionEvent>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.result = ResultButton.CANCEL;
                dialog.close();
            }
        });
        return new Button[] {this.buttonOfOK, this.buttonOfCancel};
    }

    @Override
    protected Control getInitialFocusControl() {
        if (this.defaultValue != null) {
            switch (this.defaultValue) {
            case OK:
                return this.buttonOfOK;
            case CANCEL:
            default:
                break;
            }
        }
        return this.buttonOfCancel;
    }
    
    private ResultButton defaultValue = null;
    
    @Override
    public void setDefaultValue(ResultButton value) {
        this.defaultValue = value;
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
        this.result = ResultButton.CANCEL;
    }
}
