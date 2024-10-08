package com.hirohiro716.scent.gui.dialog;

import java.util.Collection;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.ListView;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * リストビューを表示するダイアログのクラス。
 * 
 * @author hiro
 *
 * @param <T> リストビューで選択する値の型。
 */
public class ListViewDialog<T> extends MessageableDialog<Array<T>> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public ListViewDialog(Frame<?> owner) {
        super(owner);
        this.getPane().setHeight(this.getPane().getFont().getSize() * 36);
    }

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーとリストビューのアイテムを指定する。
     * 
     * @param owner
     * @param listItems 
     */
    public ListViewDialog(Frame<?> owner, T[] listItems) {
        this(owner);
        this.listView.getItems().addAll(listItems);
    }

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーとリストビューのアイテムを指定する。
     * 
     * @param owner
     * @param listItems 
     */
    public ListViewDialog(Frame<?> owner, Collection<T> listItems) {
        this(owner);
        this.listView.getItems().addAll(listItems);
    }

    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelable();
    }
    
    private ListView<T> listView;
    
    /**
     * このダイアログのリストビューを取得する。
     * 
     * @return
     */
    public ListView<T> getListView() {
        return this.listView;
    }
    
    @Override
    protected Control createInputControl() {
        this.listView = new ListView<>();
        return this.listView;
    }
    
    private Button buttonOK;
    
    private Button buttonCancel;
    
    /**
     * このダイアログがキャンセル可能な場合はtrueを返す。
     * 
     * @return
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
        ListViewDialog<T> dialog = this;
        this.buttonOK = new Button("OK");
        this.buttonOK.setMnemonic(KeyCode.O);
        this.buttonOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.result = dialog.listView.getSelectedItems();
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
        return this.listView;
    }
    
    @Override
    protected void processBeforeShowing() {
        super.processBeforeShowing();
        this.getVerticalPaneOfControls().getGrowableControls().clear();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.listView);
    }

    /**
     * このダイアログにデフォルトの値をセットする。
     * 
     * @param value
     */
    public void setDefaultSingleValue(T value) {
        this.setDefaultValue(new Array<>(value));
        this.listView.scrollTo(value);
    }
    
    @Override
    public void setDefaultValue(Array<T> defaultValue) {
        if (defaultValue == null) {
            return;
        }
        this.listView.setSelectedItems(defaultValue.getUnmodifiableList());
        if (defaultValue.length() > 0) {
            this.listView.scrollTo(defaultValue.get(0));
        }
    }
    
    private Array<T> result = null;
    
    @Override
    public Array<T> getDialogResult() {
        return this.result;
    }

    @Override
    protected void setDialogResult(Array<T> result) {
        this.result = result;
    }

    @Override
    protected void setCanceledDialogResult() {
        this.result = null;
    }
}
