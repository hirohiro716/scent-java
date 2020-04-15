package com.hirohiro716.gui.dialog;

import java.util.Collection;

import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.DropDownList;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.EventHandler;

/**
 * ドロップダウンリストを表示するダイアログのクラス。
 * 
 * @author hiro
 *
 * @param <T> ドロップダウンリストで選択する値の型。
 */
public class DropDownListDialog<T> extends MessageableDialog<T> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public DropDownListDialog(Frame<?> owner) {
        super(owner);
        this.getPane().setHeight(this.getPane().getFont().getSize() * 26);
    }
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーとドロップダウンリストのアイテムを指定する。
     * 
     * @param owner
     * @param listItems 
     */
    public DropDownListDialog(Frame<?> owner, T[] listItems) {
        this(owner);
        this.dropDownList.getItems().addAll(listItems);
    }

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーとドロップダウンリストのアイテムを指定する。
     * 
     * @param owner
     * @param listItems 
     */
    public DropDownListDialog(Frame<?> owner, Collection<T> listItems) {
        this(owner);
        this.dropDownList.getItems().addAll(listItems);
    }

    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelable();
    }
    
    private DropDownList<T> dropDownList;
    
    /**
     * このダイアログのドロップダウンリストを取得する。
     * 
     * @return 結果。
     */
    public DropDownList<T> getDropDownList() {
        return this.dropDownList;
    }
    
    @Override
    protected Control createInputControl() {
        this.dropDownList = new DropDownList<>();
        return this.dropDownList;
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
        DropDownListDialog<T> dialog = this;
        this.buttonOK = new Button("OK");
        this.buttonOK.setMnemonic(KeyCode.O);
        this.buttonOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.result = dialog.dropDownList.getSelectedItem();
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
        return this.dropDownList;
    }
    
    @Override
    public void setDefaultValue(T defaultResultValue) {
        this.dropDownList.setSelectedItem(defaultResultValue);
    }
    
    private T result = null;
    
    @Override
    public T getDialogResult() {
        return this.result;
    }

    @Override
    protected void setDialogResult(T result) {
        this.result = result;
    }

    @Override
    protected void setCanceledDialogResult() {
        this.result = null;
    }
}
