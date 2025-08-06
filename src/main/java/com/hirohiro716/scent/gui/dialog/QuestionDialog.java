package com.hirohiro716.scent.gui.dialog;

import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * 質問ダイアログのクラス。
 */
public class QuestionDialog extends MessageableDialog<ResultButton> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public QuestionDialog(Frame<?> owner) {
        super(owner);
    }
    
    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelable();
    }
    
    private Button buttonOfYes;
    
    private Button buttonOfNo;
    
    private Button buttonOfCancel;
    
    /**
     * この質問ダイアログがキャンセル可能な場合はtrueを返す。
     * 
     * @return
     */
    public boolean isCancelable() {
        return this.buttonOfCancel.isVisible();
    }
    
    /**
     * この質問ダイアログをキャンセル可能にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.buttonOfCancel.setVisible(isCancelable);
    }

    @Override
    protected Control createInputControl() {
        return null;
    }
    
    @Override
    protected Button[] createButtons() {
        QuestionDialog dialog = this;
        this.buttonOfYes = new Button("はい(Y)");
        this.buttonOfYes.setMnemonic(KeyCode.Y);
        this.buttonOfYes.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.result = ResultButton.YES;
                dialog.close();
            }
        });
        this.buttonOfNo = new Button("いいえ(N)");
        this.buttonOfNo.setMnemonic(KeyCode.N);
        this.buttonOfNo.addActionEventHandler(new EventHandler<ActionEvent>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.result = ResultButton.NO;
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
        return new Button[] {this.buttonOfYes, this.buttonOfNo, this.buttonOfCancel};
    }
    
    @Override
    protected Control getInitialFocusControl() {
        if (this.defaultValue != null) {
            switch (this.defaultValue) {
            case YES:
                return this.buttonOfYes;
            case NO:
                return this.buttonOfNo;
            case CANCEL:
            default:
                break;
            }
        }
        return this.buttonOfCancel;
    }

    private ResultButton defaultValue  = null;
    
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
