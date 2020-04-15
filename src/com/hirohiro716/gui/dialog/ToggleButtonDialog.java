package com.hirohiro716.gui.dialog;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.Array;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.FlowPane;
import com.hirohiro716.gui.control.ScrollPane;
import com.hirohiro716.gui.control.ToggleButton;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;

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
        ToggleButtonDialog<T> dialog = this;
        this.pickableItems.addListener(new AddListener<T>() {
            
            @Override
            protected void added(T added, int positionIndex) {
                ToggleButton toggleButton = new ToggleButton();
                if (dialog.mapPickableItemText.containsKey(added)) {
                    toggleButton.setText(dialog.mapPickableItemText.get(added));
                } else {
                    toggleButton.setText(added.toString());
                }
                dialog.flowPane.getChildren().add(toggleButton, positionIndex);
                dialog.mapToggleButton.put(added, toggleButton);
            }
        });
        this.pickableItems.addListener(new RemoveListener<T>() {
            
            @Override
            protected void removed(T removed) {
                ToggleButton toggleButton = dialog.mapToggleButton.get(removed);
                dialog.flowPane.getChildren().remove(toggleButton);
            }
        });
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
     * @return 結果。
     */
    public Collection<T> getPickableItems() {
        return this.pickableItems;
    }
    
    private Map<T, ToggleButton> mapToggleButton = new HashMap<>();
    
    /**
     * 指定されたアイテムに関連づいたトグルボタンを取得する。
     * 
     * @param pickableItem
     * @return 結果。
     */
    public ToggleButton getToggleButton(T pickableItem) {
        return this.mapToggleButton.get(pickableItem);
    }
    
    private Map<T, String> mapPickableItemText = new HashMap<>();

    /**
     * 選択できるアイテムの文字列表現の代わりに表示するテキストを定義したマップを取得する。
     * 
     * @return 結果。
     */
    public Map<T, String> getMapPickableItemText() {
        return this.mapPickableItemText;
    }
    
    /**
     * 選択できるアイテムの文字列表現の代わりに表示するテキストを定義したマップをセットする。
     * 
     * @param map
     */
    public void setMapPickableItemText(Map<T, String> map) {
        this.mapPickableItemText = map;
    }

    private FlowPane flowPane;
    
    private ScrollPane scrollPane;
    
    @Override
    protected Control createInputControl() {
        this.flowPane = new FlowPane();
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
        ToggleButtonDialog<T> dialog = this;
        this.buttonOK = new Button("OK");
        this.buttonOK.setMnemonic(KeyCode.O);
        this.buttonOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                List<T> list = new ArrayList<>();
                for (T key : dialog.mapToggleButton.keySet()) {
                    ToggleButton toggleButton = dialog.mapToggleButton.get(key);
                    if (toggleButton.isMarked()) {
                        list.add(key);
                    }
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
    protected void processBeforeShow() {
        super.processBeforeShow();
        ToggleButtonDialog<T> dialog = this;
        this.getVerticalPaneOfControls().getGrowableControls().clear();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.scrollPane);
        this.getVerticalPaneOfControls().addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                dialog.flowPane.setMaximumWidth(changedValue.width);
            }
        });
    }
    
    private Array<T> result = null;
    
    @Override
    public void setDefaultValue(Array<T> value) {
        if (value != null) {
            for (T key : value) {
                ToggleButton toggleButton = this.mapToggleButton.get(key);
                if (toggleButton != null) {
                    toggleButton.setMarked(true);
                }
            }
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
