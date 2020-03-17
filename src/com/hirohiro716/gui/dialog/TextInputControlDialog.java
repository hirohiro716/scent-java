package com.hirohiro716.gui.dialog;

import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.TextInputControl;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.EventHandler;

/**
 * テキスト入力コントロールを表示するダイアログのクラス。
 * 
 * @author hiro
 * 
 * @param <C> テキスト入力コントロール実装クラスの型。
 */
public abstract class TextInputControlDialog<C extends TextInputControl> extends MessageableDialog<String> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public TextInputControlDialog(Frame<?> owner) {
        super(owner);
    }

    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelable();
    }
    
    /**
     * このダイアログに表示するテキスト入力コントロールを作成する。
     * 
     * @return 結果。
     */
    protected abstract C createTextInputControl();
    
    @Override
    protected Control createInputControl() {
        this.control = this.createTextInputControl();
        return this.control;
    }

    private C control;
    
    /**
     * このダイアログに表示するテキスト入力コントロールを取得する。
     * 
     * @return 結果。
     */
    public C getTextInputControl() {
        return this.control;
    }
    
    private Button buttonOK;
    
    private Button buttonCancel;
    
    /**
     * このダイアログがキャンセル可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isCancelable() {
        return this.buttonCancel.isVisible();
    }
    
    /**
     * このダイアログをキャンセル可能にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.buttonCancel.setVisible(isCancelable);
    }
    
    @Override
    protected Button[] createButtons() {
        TextInputControlDialog<C> dialog = this;
        this.buttonOK = new Button("OK");
        this.buttonOK.setMnemonic(KeyCode.O);
        this.buttonOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.result = dialog.getTextInputControl().getText();
                dialog.close();
            }
        });
        this.buttonCancel = new Button("キャンセル(C)");
        this.buttonCancel.setMnemonic(KeyCode.C);
        this.buttonCancel.addActionEventHandler(new EventHandler<ActionEvent>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.close();
            }
        });
        return new Button[] {this.buttonOK, this.buttonCancel};
    }

    @Override
    protected Control getInitialFocusControl() {
        return this.control;
    }
    
    private String result = null;
    
    @Override
    public void setDefaultValue(String defaultResultValue) {
        this.result = defaultResultValue;
    }
    
    @Override
    public String getDialogResult() {
        return this.result;
    }
}
