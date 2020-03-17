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
    protected void itemLabelCallback(Label label, T value, int index, boolean isSelected) {
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
                control.bugFix();
            }
        };
    }
    
    private int minimumWidthForBugFix = 0;
    
    private int minimumHeightForBugFix = 0;

    /**
     * このGUIライブラリにはドロップダウンリストの最小幅をアイテムの文字幅に合わせて無限に拡大するバグがある。
     */
    private void bugFix() {
        if (this.minimumHeightForBugFix == 0) {
            int defaultSize = (int) (this.getFont().getSize2D() * 2.6);
            this.minimumWidthForBugFix = defaultSize;
            this.minimumHeightForBugFix = defaultSize;
            this.getInnerInstance().setSize(defaultSize, defaultSize);
        }
        this.getInnerInstance().setMinimumSize(new Dimension(this.minimumWidthForBugFix, this.minimumHeightForBugFix));
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
        this.bugFix();
    }
    
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.bugFix();
    }
    
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.bugFix();
    }
    
    @Override
    public void setMinimumSize(int width, int height) {
        super.setMinimumSize(width, height);
        this.minimumWidthForBugFix = width;
        this.minimumHeightForBugFix = height;
        this.bugFix();
    }
    
    @Override
    public void setMinimumWidth(int width) {
        super.setMinimumWidth(width);
        this.minimumWidthForBugFix = width;
        this.bugFix();
    }
    
    @Override
    public void setMinimumHeight(int height) {
        super.setMinimumHeight(height);
        this.minimumHeightForBugFix = height;
        this.bugFix();
    }

    @Override
    public void setMaximumSize(int width, int height) {
        super.setMaximumSize(width, height);
        this.bugFix();
    }

    @Override
    public void setMaximumWidth(int width) {
        super.setMaximumWidth(width);
        this.bugFix();
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
