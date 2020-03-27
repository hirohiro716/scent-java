package com.hirohiro716.gui.dialog;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;

import com.hirohiro716.Array;
import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.Popup;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.control.ScrollPane;
import com.hirohiro716.gui.control.VerticalPane;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * 複数の値をソートするダイアログのクラス。
 * 
 * @author hiro
 *
 * @param <T> 並び替える値の型。
 */
public class SortDialog<T> extends MessageableDialog<Array<T>> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public SortDialog(Frame<?> owner) {
        super(owner);
        SortDialog<T> dialog = this;
        this.sortableItems.addListener(new AddListener<T>() {
            
            @Override
            protected void added(T added, int positionIndex) {
                Label label = dialog.createSortableItemLabel(added);
                label.addMousePressedEventHandler(MouseButton.BUTTON1, dialog.mousePressedEventHandler);
                label.addMouseDraggedEventHandler(dialog.mouseDraggedEventHandler);
                label.addMouseReleasedEventHandler(MouseButton.BUTTON1, dialog.mouseReleasedEventHandler);
                dialog.verticalPane.getChildren().add(label, positionIndex);
                dialog.mapSortableItemLabels.put(added, label);
            }
        });
        this.sortableItems.addListener(new RemoveListener<T>() {
            
            @Override
            protected void removed(T removed) {
                Label label = dialog.mapSortableItemLabels.get(removed);
                dialog.verticalPane.getChildren().remove(label);
            }
        });
        
        this.getPane().setHeight(this.getPane().getFont().getSize() * 36);
    }
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーとソートするアイテムを指定する。
     * 
     * @param owner
     * @param sortableItems
     */
    public SortDialog(Frame<?> owner, T[] sortableItems) {
        this(owner);
        this.sortableItems.addAll(sortableItems);
    }
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーとソートするアイテムを指定する。
     * 
     * @param owner
     * @param sortableItems
     */
    public SortDialog(Frame<?> owner, java.util.Collection<T> sortableItems) {
        this(owner);
        this.sortableItems.addAll(sortableItems);
    }
    
    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelable();
    }
    
    private Collection<T> sortableItems = new Collection<>();
    
    /**
     * このダイアログに表示するソートできるアイテムのコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<T> getSortableItems() {
        return this.sortableItems;
    }
    
    private Map<T, Label> mapSortableItemLabels = new HashMap<>();
    
    /**
     * 指定されたアイテムに関連づいたラベルを作成する。
     * 
     * @param sortableItem
     * @return 結果。
     */
    private Label createSortableItemLabel(T sortableItem) {
        Label label = new Label();
        if (this.mapSortableItemText.containsKey(sortableItem)) {
            label.setText(this.mapSortableItemText.get(sortableItem));
        } else {
            label.setText(sortableItem.toString());
        }
        label.setPadding(label.getFont().getSize() / 2);
        label.setInstanceForUseLater(sortableItem);
        return label;
    }
    
    /**
     * 指定されたアイテムに関連づいたラベルを取得する。
     * 
     * @param sortableItem
     * @return 結果。
     */
    public Label getSortableItemLabel(T sortableItem) {
        return this.mapSortableItemLabels.get(sortableItem);
    }
    
    private Map<T, String> mapSortableItemText = new HashMap<>();
    
    /**
     * ソートできるアイテムの文字列表現の代わりに表示するテキストを定義したマップを取得する。
     * 
     * @return 結果。
     */
    public Map<T, String> getMapSortableItemText() {
        return this.mapSortableItemText;
    }
    
    /**
     * ソートできるアイテムの文字列表現の代わりに表示するテキストを定義したマップをセットする。
     * 
     * @param map
     */
    public void setMapSortableItemText(Map<T, String> map) {
        this.mapSortableItemText = map;
    }
    
    private Popup popup = null;
    
    private Color labelForegroundColor = null;
    
    private Color movingForegroundColor = new Color(UIManager.getColor("textHighlightText").getRGB());
    
    private Color movingBackgroundColor = new Color(UIManager.getColor("textHighlight").getRGB());
    
    private EventHandler<MouseEvent> mousePressedEventHandler = new EventHandler<>() {
        
        @Override
        protected void handle(MouseEvent event) {
            SortDialog<T> dialog = SortDialog.this;
            Popup popup = dialog.popup;
            Label source = (Label) event.getSource();
            dialog.labelForegroundColor = source.getForegroundColor();
            source.setForegroundColor(dialog.movingBackgroundColor);
            source.setBorder(Border.createLine(dialog.movingBackgroundColor, 1));
            Label label = dialog.createSortableItemLabel(source.getInstanceForUseLater());
            label.setSize(source.getSize());
            label.setForegroundColor(dialog.movingForegroundColor);
            label.setBackgroundColor(dialog.movingBackgroundColor);
            if (popup == null) {
                popup = new Popup(dialog.getOwner());
                dialog.popup = popup;
            }
            popup.getChildren().clear();
            popup.getChildren().add(label);
            popup.setSize(label.getWidth(), label.getHeight());
            dialog.dragOffsetX = event.getX();
            dialog.dragOffsetY = event.getY();
            popup.setLocation(event.getScreenX() - dialog.dragOffsetX, event.getScreenY() - dialog.dragOffsetY);
            popup.show();
        }
    };
    
    private int dragOffsetX = 0;
    
    private int dragOffsetY = 0;
    
    private EventHandler<MouseEvent> mouseDraggedEventHandler = new EventHandler<>() {
        
        @Override
        protected void handle(MouseEvent event) {
            SortDialog<T> dialog = SortDialog.this;
            Popup popup = dialog.popup;
            popup.setLocation(event.getScreenX() - dialog.dragOffsetX, event.getScreenY() - dialog.dragOffsetY);
            Label source = (Label) event.getSource();
            int x = source.getX();
            int y = source.getY();
            y += event.getY();
            Label target = dialog.verticalPane.getChildren().findControlByPoint(x, y);
            if (target == null) {
                return;
            }
            int targetIndex = dialog.verticalPane.getChildren().indexOf(target);
            dialog.verticalPane.getChildren().remove(source);
            dialog.verticalPane.getChildren().add(source, targetIndex);
            dialog.verticalPane.updateDisplay();
        }
    };
    
    private EventHandler<MouseEvent> mouseReleasedEventHandler = new EventHandler<>() {
        
        @Override
        protected void handle(MouseEvent event) {
            SortDialog<T> dialog = SortDialog.this;
            Popup popup = dialog.popup;
            if (popup == null) {
                return;
            }
            popup.hide();
            Label source = (Label) event.getSource();
            source.setForegroundColor(dialog.labelForegroundColor);
            source.setBorder(null);
        }
    };
    
    private VerticalPane verticalPane;
    
    private ScrollPane scrollPane;
    
    @Override
    protected Control createInputControl() {
        this.verticalPane = new VerticalPane();
        this.verticalPane.setPadding(this.verticalPane.getFont().getSize());
        this.scrollPane = new ScrollPane(this.verticalPane);
        return this.scrollPane;
    }
    
    @Override
    protected Control getInitialFocusControl() {
        return this.verticalPane;
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
        SortDialog<T> dialog = this;
        this.buttonOK = new Button("OK");
        this.buttonOK.setMnemonic(KeyCode.O);
        this.buttonOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                List<T> list = new ArrayList<>();
                for (Control control : dialog.verticalPane.getChildren()) {
                    Label label = (Label) control;
                    list.add(label.getInstanceForUseLater());
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
        return new Button[] { this.buttonOK, this.buttonCancel };
    }
    
    @Override
    protected void processBeforeShow() {
        super.processBeforeShow();
        this.getVerticalPaneOfControls().getGrowableControls().clear();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.scrollPane);
    }
    
    @Override
    public void setDefaultValue(Array<T> defaultResultValue) {
        List<T> items = new ArrayList<>();
        items.addAll(this.sortableItems.toArray().getUnmodifiableList());
        this.sortableItems.clear();
        for (T value : defaultResultValue) {
            this.sortableItems.add(value);
            items.remove(value);
        }
        for (T value : items) {
            this.sortableItems.add(value);
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
}
