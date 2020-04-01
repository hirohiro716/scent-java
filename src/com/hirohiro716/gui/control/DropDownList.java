package com.hirohiro716.gui.control;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import com.hirohiro716.Array;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.InnerInstanceCreator;
import com.hirohiro716.gui.event.MouseEvent;

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
    protected DropDownList(JComboBox<T> innerInstance) {
        super(innerInstance);
        DropDownList<T> control = this;
        this.getItems().addListener(new AddListener<T>() {

            @Override
            protected void added(T added, int positionIndex) {
                control.getInnerInstance().addItem(added);
                if (positionIndex == 0) {
                    control.getInnerInstance().setSelectedItem(null);
                }
            }
        });
        this.getItems().addListener(new RemoveListener<T>() {

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
    protected ChangeListener<Dimension> createBugFixChangeListener() {
        DropDownList<T> control = this;
        return new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                control.adjustSize();
            }
        };
    }

    private int baseWidth = 0;
    
    @Override
    protected void itemLabelCallback(Label label, T value, int index, boolean isSelected) {
        if (this.baseWidth < label.getWidth()) {
            this.baseWidth = label.getWidth();
        }
    }
    
    private boolean isSizeInitialized = false;
    
    /**
     * このGUIライブラリにはドロップダウンリストの最小幅をアイテムの文字幅に合わせて無限に拡大するバグがある。
     */
    private void adjustSize() {
        if (this.isSizeInitialized == false && this.baseWidth > 0) {
            this.isSizeInitialized = true;
            super.setMinimumHeight(this.getItemHeight());
            super.setSize(this.baseWidth, this.getItemHeight());
        }
        if (this.getWidth() < this.getMinimumWidth() && this.getMinimumWidth() < this.getMaximumWidth()) {
            this.setWidth(this.getMinimumWidth());
            return;
        }
        if (this.getHeight() < this.getMinimumHeight() && this.getMinimumHeight() < this.getMaximumHeight()) {
            this.setHeight(this.getMinimumHeight());
            return;
        }
        if (this.getWidth() > this.getMaximumWidth() && this.getMinimumWidth() < this.getMaximumWidth()) {
            this.setWidth(this.getMaximumWidth());
            return;
        }
        if (this.getHeight() > this.getMaximumHeight() && this.getMinimumHeight() < this.getMaximumHeight()) {
            this.setHeight(this.getMaximumHeight());
            return;
        }
        this.updateItemDisplay();
    }
    
    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        this.adjustSize();
    }
    
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.adjustSize();
    }
    
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.adjustSize();
    }
    
    @Override
    public void setMinimumSize(int width, int height) {
        super.setMinimumSize(width, height);
        this.adjustSize();
    }
    
    @Override
    public void setMinimumWidth(int width) {
        super.setMinimumWidth(width);
        this.adjustSize();
    }
    
    @Override
    public void setMinimumHeight(int height) {
        super.setMinimumHeight(height);
        this.adjustSize();
    }

    @Override
    public void setMaximumSize(int width, int height) {
        super.setMaximumSize(width, height);
        this.adjustSize();
    }

    @Override
    public void setMaximumWidth(int width) {
        super.setMaximumWidth(width);
        this.adjustSize();
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
    }
    
    @Override
    public void addSelectedItemChangeListener(ChangeListener<T> changeListener) {
        DropDownList<T> dropDownList = this;
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
    public void addSelectedItemsChangeListener(ChangeListener<Array<T>> changeListener) {
        DropDownList<T> dropDownList = this;
        ActionListener innerInstance = changeListener.createInnerInstance(dropDownList, new InnerInstanceCreator<>() {

            @Override
            public ActionListener create() {
                return new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        changeListener.executeWhenChanged(dropDownList, dropDownList.getSelectedItems());
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
    }
    
    @Override
    protected void mapDisplayTextToItem(DefaultListCellRenderer renderer) {
        this.getInnerInstance().setRenderer(renderer);
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
}
