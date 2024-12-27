package com.hirohiro716.scent.gui.control;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.graphic.GraphicalString;
import com.hirohiro716.scent.gui.collection.Collection;
import com.hirohiro716.scent.gui.collection.RemoveListener;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.KeyEvent;

/**
 * リストから選択するコントロールの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> 選択させるリストアイテムの型。
 */
public abstract class ListSelectControl<T> extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     * @param innerInstanceForLayout
     */
    protected ListSelectControl(JComponent innerInstance, JComponent innerInstanceForLayout) {
        super(innerInstance, innerInstanceForLayout);
        ListSelectControl<T> control = this;
        this.updateItemDisplay();
        this.items.addListener(new RemoveListener<T>() {

            @Override
            protected void removed(T removed) {
                if (control.getSelectedItem() != null && control.getSelectedItem().equals(removed)) {
                    control.setSelectedItem(null);
                }
            }
        });
        this.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                
                if (event.isShiftDown() || event.isControlDown() || event.isAltDown()) {
                    return;
                }
                switch (event.getKeyCode()) {
                case UP:
                case PAGE_UP:
                    if (control.getSelectedItems().length() == 0 && control.getItems().size() > 0) {
                        control.setSelectedItem(control.getItems().get(control.getItems().size() - 1));
                        event.consume();
                    }
                    break;
                case DOWN:
                case PAGE_DOWN:
                    if (control.getSelectedItems().length() == 0 && control.getItems().size() > 0) {
                        control.setSelectedItem(control.getItems().get(0));
                        event.consume();
                    }
                    break;
                default:
                    break;
                }
            }
        });
    }
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected ListSelectControl(JComponent innerInstance) { 
        this(innerInstance, innerInstance);
    }
    
    /**
     * このコントロールのリストアイテムの高さを取得する。
     * 
     * @return
     */
    protected int getItemHeight() {
        return (int) (this.getFont().getSize2D() * 2.4);
    }

    private Graphics2D graphics = GraphicalString.createGraphics2D();

    /**
     * このコントロールのリストアイテムの表示を更新する。
     */
    protected void updateItemDisplay() {
        ListSelectControl<T> control = ListSelectControl.this;
        DefaultListCellRenderer cellRenderer = new DefaultListCellRenderer() {
            
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (jLabel == null) {
                    return null;
                }
                Label label = new Label(jLabel);
                label.setFont(control.getFont());
                if (control.getMapDisplayTextForItem() != null && control.getMapDisplayTextForItem().containsKey(value)) {
                    label.setText(control.getMapDisplayTextForItem().get(value));
                }
                int height = control.getItemHeight();
                control.graphics.setFont(control.getFont());
                GraphicalString graphicalString = new GraphicalString(label.getText(), control.graphics);
                int width = (int) graphicalString.createDimension().getWidth() + control.getItemHeight();
                label.setSize(width, height);
                int padding = (int) (height * 0.32);
                label.setPadding(0, padding);
                label.setDisabled(control.isDisabled());
                control.itemLabelCallback(label, (T) value, index, isSelected);
                return label.getInnerInstance();
            }
        };
        this.mapDisplayTextToItem(cellRenderer);
    }
    
    /**
     * このコントロールで生成されるリストアイテムのラベルを作成する際に呼び出される。
     * 
     * @param label
     * @param value
     * @param index 
     * @param isSelected 
     */
    protected abstract void itemLabelCallback(Label label, T value, int index, boolean isSelected);
    
    private Collection<T> items = new Collection<>();
    
    /**
     * このコントロールで選択可能なリストアイテムを取得する。
     * 
     * @return
     */
    public Collection<T> getItems() {
        return this.items;
    }
    
    /**
     * このコントロールで選択されているリストアイテムを取得する。
     * 
     * @return
     */
    public abstract Array<T> getSelectedItems();
    
    /**
     * このコントロールで選択されているリストアイテムの1番目を取得する。選択されているものがなければnullを返す。
     * 
     * @return
     */
    public T getSelectedItem() {
        for (T item : this.getSelectedItems()) {
            return item;
        }
        return null;
    }
    
    /**
     * このコントロールの指定されたリストアイテムを選択状態にする。
     * 
     * @param selectedItems
     */
    public abstract void setSelectedItems(java.util.Collection<T> selectedItems);
    
    /**
     * このコントロールの指定されたリストアイテムを選択状態にする。
     * 
     * @param selectedItems
     */
    public final void setSelectedItems(T[] selectedItems) {
        this.setSelectedItems(DynamicArray.newInstance(selectedItems).getValues());
    }
    
    /**
     * このコントロールの指定されたリストアイテムを選択状態にする。
     * 
     * @param selectedItem
     */
    public final void setSelectedItem(T selectedItem) {
        List<T> list = new ArrayList<>();
        list.add(selectedItem);
        this.setSelectedItems(list);
    }
    
    /**
     * このコントロールの選択状態を解除する。
     */
    public void clearSelection() {
        this.setSelectedItems(new ArrayList<>());
    }
    
    /**
     * このリストビューの選択範囲の1番目が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public abstract void addSelectedItemChangeListener(ChangeListener<T> changeListener);
    
    private Map<T, String> mapDisplayTextForItem = new HashMap<>();
    
    /**
     * リストアイテムの文字列表現の代わりに表示するテキストを定義したマップを取得する。
     * 
     * @return
     */
    public Map<T, String> getMapDisplayTextForItem() {
        return this.mapDisplayTextForItem;
    }
    
    /**
     * リストアイテムの文字列表現の代わりに表示するテキストを定義したマップをセットする。
     * 
     * @param map
     */
    public void setMapDisplayTextForItem(Map<T, String> map) {
        this.mapDisplayTextForItem = map;
    }
    
    /**
     * リストアイテムの文字列表現の代わりに定義したテキストを表示するレンダラーをセットする。<br>
     * このメソッドはコンストラクタで自動的に呼び出される。
     * 
     * @param renderer
     */
    protected abstract void mapDisplayTextToItem(DefaultListCellRenderer renderer);
}
