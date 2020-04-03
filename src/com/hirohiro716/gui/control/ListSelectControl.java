package com.hirohiro716.gui.control;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import com.hirohiro716.Array;
import com.hirohiro716.DynamicArray;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.event.ChangeListener;

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
        this.updateItemDisplay();
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
     * @return 結果。
     */
    protected int getItemHeight() {
        return (int) (this.getFont().getSize2D() * 2.4);
    }
    
    /**
     * このコントロールのリストアイテムの表示を更新する。
     */
    protected void updateItemDisplay() {
        ListSelectControl<T> control = ListSelectControl.this;
        @SuppressWarnings("serial")
        DefaultListCellRenderer cellRenderer = new DefaultListCellRenderer() {
            
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (jLabel == null) {
                    return null;
                }
                Label label = new Label(jLabel);
                if (control.getMapForDisplayTextAndItem() != null && control.getMapForDisplayTextAndItem().containsKey(value)) {
                    label.setText(control.getMapForDisplayTextAndItem().get(value));
                }
                int height = control.getItemHeight();
                label.setHeight(height);
                label.setPadding(0, (height - this.getFont().getSize()) / 2);
                label.setTextHorizontalAlignment(control.getTextHorizontalAlignment());
                label.setParent(control);
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
     * @return 結果。
     */
    public Collection<T> getItems() {
        return this.items;
    }
    
    /**
     * このコントロールで選択されているリストアイテムを取得する。
     * 
     * @return 結果。
     */
    public abstract Array<T> getSelectedItems();
    
    /**
     * このコントロールで選択されているリストアイテムの1番目を取得する。選択されているものがなければnullを返す。
     * 
     * @return 結果。
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
    
    private HorizontalAlignment textHorizontalAlignment = HorizontalAlignment.LEFT;
    
    /**
     * このコントロールの水平方向のテキスト表示位置を取得する。
     * 
     * @return 結果。
     */
    public HorizontalAlignment getTextHorizontalAlignment() {
        return this.textHorizontalAlignment;
    }

    /**
     * このコントロールの水平方向のテキスト表示位置を指定する。
     * 
     * @param horizontalAlignment
     */
    public void setTextHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.textHorizontalAlignment = horizontalAlignment;
    }
    
    private Map<T, String> mapForDisplayTextAndItem = new HashMap<>();
    
    /**
     * リストアイテムの文字列表現の代わりに表示するテキストを定義したマップを取得する。
     * 
     * @return 結果。
     */
    public Map<T, String> getMapForDisplayTextAndItem() {
        return this.mapForDisplayTextAndItem;
    }
    
    /**
     * リストアイテムの文字列表現の代わりに表示するテキストを定義したマップをセットする。
     * 
     * @param map
     */
    public void setTextForItem(Map<T, String> map) {
        this.mapForDisplayTextAndItem = map;
    }
    
    /**
     * リストアイテムの文字列表現の代わりに定義したテキストを表示するレンダラーをセットする。<br>
     * このメソッドはコンストラクタで自動的に呼び出される。
     * 
     * @param renderer
     */
    protected abstract void mapDisplayTextToItem(DefaultListCellRenderer renderer);
}
