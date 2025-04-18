package com.hirohiro716.scent.gui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.collection.Collection;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.FlowPane;
import com.hirohiro716.scent.gui.control.ScrollPane;
import com.hirohiro716.scent.gui.control.ToggleButton;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * 複数のトグルボタンを表示するダイアログのクラス。
 * 
 * @author hiro
 * 
 * @param <T> このダイアログで選択する値の型。
 */
public class ToggleButtonDialog<T> extends MessageableDialog<Array<T>> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public ToggleButtonDialog(Frame<?> owner) {
        super(owner);
        this.getPane().setHeight(this.getPane().getFont().getSize() * 36);
    }

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーと選択できるアイテムを指定する。
     * 
     * @param owner
     * @param listItems 
     */
    public ToggleButtonDialog(Frame<?> owner, T[] listItems) {
        this(owner);
        this.pickableItems.addAll(listItems);
    }

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーと選択できるアイテムを指定する。
     * 
     * @param owner
     * @param listItems 
     */
    public ToggleButtonDialog(Frame<?> owner, java.util.Collection<T> listItems) {
        this(owner);
        this.pickableItems.addAll(listItems);
    }
    
    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelable();
    }
    
    private Collection<T> pickableItems = new Collection<>();

    /**
     * このダイアログに表示する選択できるアイテムのコレクションを取得する。
     * 
     * @return
     */
    public Collection<T> getPickableItems() {
        return this.pickableItems;
    }
    
    
    private int maximumNumberOfPickable = -1;
    
    /**
     * このダイアログで選択できるトグルボタンの最大数を取得する。初期値は無制限。
     * 
     * @return
     */
    public int getMaximumNumberOfPickable() {
        return this.maximumNumberOfPickable;
    }
    
    /**
     * このダイアログで選択できるトグルボタンの最大数をセットする。
     * 
     * @param maximumNumberOfPickable
     */
    public void setMaximumNumberOfPickable(int maximumNumberOfPickable) {
        this.maximumNumberOfPickable = maximumNumberOfPickable;
    }
    
    private Map<T, ToggleButton> mapToggleButton = new HashMap<>();

    /**
     * 指定されたアイテムに関連づいたトグルボタンを作成する。
     * 
     * @param pickableItem
     * @return
     */
    private ToggleButton createToggleButton(T pickableItem) {
        ToggleButton toggleButton = new ToggleButton();
        if (this.mapDisplayTextForPickableItem.containsKey(pickableItem)) {
            toggleButton.setText(this.mapDisplayTextForPickableItem.get(pickableItem));
        } else {
            toggleButton.setText(pickableItem.toString());
        }
        return toggleButton;
    }
    
    /**
     * 指定されたアイテムに関連づいたトグルボタンを取得する。
     * 
     * @param pickableItem
     * @return
     */
    public ToggleButton getToggleButton(T pickableItem) {
        return this.mapToggleButton.get(pickableItem);
    }
    
    private Map<T, String> mapDisplayTextForPickableItem = new HashMap<>();

    /**
     * 選択できるアイテムの文字列表現の代わりに表示するテキストを定義したマップを取得する。
     * 
     * @return
     */
    public Map<T, String> getMapDisplayTextForPickableItem() {
        return this.mapDisplayTextForPickableItem;
    }
    
    /**
     * 選択できるアイテムの文字列表現の代わりに表示するテキストを定義したマップをセットする。
     * 
     * @param map
     */
    public void setMapDisplayTextForPickableItem(Map<T, String> map) {
        this.mapDisplayTextForPickableItem = map;
    }

    private FlowPane flowPane;
    
    private ScrollPane scrollPane;
    
    @Override
    protected Control createInputControl() {
        this.flowPane = new FlowPane();
        this.flowPane.setPadding(this.flowPane.getFont().getSize());
        this.scrollPane = new ScrollPane(this.flowPane);
        return this.scrollPane;
    }

    @Override
    protected Control getInitialFocusControl() {
        return this.flowPane;
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
        ToggleButtonDialog<T> dialog = this;
        this.buttonOK = new Button("OK");
        this.buttonOK.setMnemonic(KeyCode.O);
        this.buttonOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                List<T> list = new ArrayList<>();
                for (T item: dialog.pickableItems) {
                    ToggleButton toggleButton = dialog.mapToggleButton.get(item);
                    if (toggleButton.isMarked()) {
                        list.add(item);
                    }
                }
                if (dialog.maximumNumberOfPickable > -1 && list.size() > dialog.maximumNumberOfPickable) {
                    InstantMessage.show(StringObject.join("選択できる最大数は", dialog.maximumNumberOfPickable, "個までです。").toString(), 3000, dialog.getOwner());
                    return;
                }
                dialog.result = new Array<>(list);
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
    protected void processBeforeShowing() {
        super.processBeforeShowing();
        ToggleButtonDialog<T> dialog = this;
        this.getVerticalPaneOfControls().getGrowableControls().clear();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.scrollPane);
        this.getVerticalPaneOfControls().addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                dialog.flowPane.setMaximumWidth(changedValue.getIntegerWidth());
            }
        });
        for (T item: this.pickableItems) {
            ToggleButton toggleButton = this.createToggleButton(item);
            if (this.defaultValue != null && this.defaultValue.contains(item)) {
                toggleButton.setMarked(true);
            }
            this.flowPane.getChildren().add(toggleButton);
            this.mapToggleButton.put(item, toggleButton);
        }
    }
    
    private List<T> defaultValue = null;
    
    @Override
    public void setDefaultValue(Array<T> value) {
        if (value != null) {
            this.defaultValue = value.getUnmodifiableList();
        } else {
            this.defaultValue = null;
        }
    }

    /**
     * このダイアログにデフォルトの値をセットする。
     * 
     * @param value
     */
    public void setDefaultSingleValue(T value) {
        this.setDefaultValue(new Array<>(value));
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
