package com.hirohiro716.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import com.hirohiro716.Array;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.InnerInstanceCreator;
import com.hirohiro716.gui.event.KeyEvent;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * ドロップダウンリストのクラス。
 * 
 * @author hiro
 *
 * @param <T> ドロップダウンリストで選択する値の型。
 */
public class DropDownList<T> extends ListSelectControl<T> {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected DropDownList(JComboBox<String> innerInstance) {
        super(innerInstance);
        DropDownList<T> control = this;
        super.getItems().addListener(new AddListener<T>() {

            @Override
            protected void added(T added, int positionIndex) {
                control.getInnerInstance().addItem(added);
                if (positionIndex == 0) {
                    control.getInnerInstance().setSelectedItem(null);
                }
            }
        });
        super.getItems().addListener(new RemoveListener<T>() {

            @Override
            protected void removed(T removed) {
                control.getInnerInstance().removeItem(removed);
            }
        });
        this.addMouseWheelEventHandler(new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                if (control.isEnableChangeValueWithMouseWheelRotation() == false) {
                    return;
                }
                int index = control.getInnerInstance().getSelectedIndex();
                if (event.getWheelRotationAmount() > 0) {
                    if (index < control.getItems().size() - 1) {
                        index++;
                    }
                } else {
                    if (index > 0) {
                        index--;
                    }
                }
                control.getInnerInstance().setSelectedIndex(index);
            }
        });
        this.addMouseClickedEventHandler(MouseButton.BUTTON3, new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                ContextMenu menu = control.createContextMenu();
                if (menu != null) {
                    menu.show(event.getX(), event.getY());
                }
            }
        });
        this.addKeyReleasedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                switch (event.getKeyCode()) {
                case F10:
                    if (event.isShiftDown()) {
                        ContextMenu menu = control.createContextMenu();
                        if (menu != null) {
                            menu.show(control.getWidth() - control.getFont().getSize() / 2, control.getHeight() / 2);
                        }
                    }
                    break;
                case DELETE:
                case BACKSPACE:
                    if (control.isClearable) {
                        control.setSelectedItem(null);
                    }
                    break;
                default:
                    break;
                }
            }
        });
        this.setHeight(this.getItemHeight());
        this.adjustSize();
    }
    
    /**
     * コンストラクタ。<br>
     * このドロップダウンリストのリストアイテムを指定する。
     * 
     * @param items
     */
    public DropDownList(T[] items) {
        this(new JComboBox<>());
        this.getItems().addAll(items);
    }

    /**
     * コンストラクタ。<br>
     * このドロップダウンリストのリストアイテムを指定する。
     * 
     * @param items
     */
    public DropDownList(java.util.Collection<T> items) {
        this(new JComboBox<>());
        this.getItems().addAll(items);
    }
    
    /**
     * コンストラクタ。
     */
    public DropDownList() {
        this(new JComboBox<>());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public JComboBox<T> getInnerInstance() {
        return (JComboBox<T>) super.getInnerInstance();
    }
    
    @Override
    protected void itemLabelCallback(Label label, T value, int index, boolean isSelected) {
        label.setWidth(this.getWidth());
    }
    
    private boolean isSizeInitialized = false;
    
    /**
     * このGUIライブラリにはドロップダウンリストの最小幅をアイテムの文字幅に合わせて無限に拡大するバグがある。
     */
    protected void adjustSize() {
        if (this.isSizeInitialized == false) {
            this.isSizeInitialized = true;
            this.setMinimumHeight(this.getItemHeight());
            super.getItems().addAll(this.itemsForInitialization.toUnmodifiableList());
            for (AddListener<T> addListener : this.itemsForInitialization.getAddListeners()) {
                super.getItems().addListener(addListener);
            }
            for (RemoveListener<T> removeListener : this.itemsForInitialization.getRemoveListeners()) {
                super.getItems().addListener(removeListener);
            }
        }
        super.adjustSize();
        this.updateItemDisplay();
    }

    private com.hirohiro716.gui.collection.Collection<T> itemsForInitialization = new com.hirohiro716.gui.collection.Collection<>();
        
    @Override
    public com.hirohiro716.gui.collection.Collection<T> getItems() {
        if (this.isSizeInitialized) {
            return super.getItems();
        }
        return this.itemsForInitialization;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSelectedItem() {
        return (T) this.getInnerInstance().getSelectedItem();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Array<T> getSelectedItems() {
        return new Array<T>((T[]) this.getInnerInstance().getSelectedObjects());
    }
    
    @Override
    public void setSelectedItems(Collection<T> selectedItems) {
        this.getInnerInstance().setSelectedItem(null);
        for (T item : selectedItems) {
            this.getInnerInstance().setSelectedItem(item);
            break;
        }
        for (ChangeListener<T> changeListener : this.selectedRowChangeListeners) {
            changeListener.executeWhenChanged(this, this.getSelectedItem());
        }
    }

    private List<ChangeListener<T>> selectedRowChangeListeners = new ArrayList<>();
    
    @Override
    public void addSelectedItemChangeListener(ChangeListener<T> changeListener) {
        DropDownList<T> dropDownList = this;
        this.selectedRowChangeListeners.add(changeListener);
        ActionListener innerInstance = changeListener.createInnerInstance(dropDownList, new InnerInstanceCreator<>() {

            @Override
            public ActionListener create() {
                return new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        changeListener.executeWhenChanged(dropDownList, dropDownList.getSelectedItem());
                    }
                };
            }
        });
        this.getInnerInstance().addActionListener(innerInstance);
    }
    
    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof ActionListener) {
                this.getInnerInstance().removeActionListener((ActionListener) innerInstance);
            }
        }
        this.selectedRowChangeListeners.remove(changeListener);
    }
    
    @Override
    protected void mapDisplayTextToItem(DefaultListCellRenderer renderer) {
        this.getInnerInstance().setRenderer(renderer);
    }
    
    private boolean isClearable = true;
    
    /**
     * このコントロールの選択アイテムをDeleteキー、Backspaceキーでクリア可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isClearable() {
        return this.isClearable;
    }
    
    /**
     * このコントロールの選択アイテムをDeleteキー、Backspaceキーでクリアできるようにする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isClearable
     */
    public void setClearable(boolean isClearable) {
        this.isClearable = isClearable;
    }

    private boolean isEnableChangeValueWithMouseWheelRotation = false;
    
    /**
     * このコントロールの値がマウスホイールの回転で変更されるようになっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isEnableChangeValueWithMouseWheelRotation() {
        return this.isEnableChangeValueWithMouseWheelRotation;
    }
    
    /**
     * このコントロールの値をマウスホイールの回転で変更する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isEnableChangeValueWithMouseWheelRotation
     */
    public void setEnableChangeValueWithMouseWheelRotation(boolean isEnableChangeValueWithMouseWheelRotation) {
        this.isEnableChangeValueWithMouseWheelRotation = isEnableChangeValueWithMouseWheelRotation;
    }
    
    /**
     * ドロップダウンリスト用のコンテキストメニューを作成する。<br>
     * 値が未選択の場合はnullを返す。
     * 
     * @return 結果。
     */
    public ContextMenu createContextMenu() {
        DropDownList<T> dropDownList = this;
        Object value = dropDownList.getSelectedItem();
        if (value == null) {
            return null;
        }
        ContextMenu menu = new ContextMenu(this);
        if (dropDownList.getMapDisplayTextForItem().containsKey(value)) {
            value = dropDownList.getMapDisplayTextForItem().get(value);
        }
        String text = value.toString();
        ContextMenuItem copy = new ContextMenuItem(text + " をコピー(C)");
        copy.setMnemonic(KeyCode.C);
        copy.setAction(new Runnable() {
            
            @Override
            public void run() {
                GUI.setClipboardString(text);
            }
        });
        menu.addContextMenuItem(copy);
        return menu;
    }
}
