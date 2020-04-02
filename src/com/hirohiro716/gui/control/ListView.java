package com.hirohiro716.gui.control;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hirohiro716.Array;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.InnerInstanceCreator;
import com.hirohiro716.gui.event.MouseEvent;

/**
 * リストビューのクラス。
 * 
 * @author hiro
 *
 * @param <T> 選択させるリストアイテムの型。
 */
public class ListView<T> extends ListSelectControl<T> {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected ListView(JList<T> innerInstance) {
        super(innerInstance, new JScrollPane(innerInstance));
        this.scrollPane = new ScrollPane(this.getInnerInstanceForLayout());
        this.setAllowMultipleSelection(false);
        this.getInnerInstance().setListData(this.vector);
        ListView<T> listView = this;
        this.getItems().addListener(new AddListener<T>() {

            @Override
            protected void added(T added, int positionIndex) {
                listView.vector.add(positionIndex, added);
                listView.getInnerInstance().repaint();
            }
        });
        this.getItems().addListener(new RemoveListener<T>() {

            @Override
            protected void removed(T removed) {
                listView.vector.remove(removed);
                listView.getInnerInstance().repaint();
            }
        });
        this.addMouseMovedEventHandler(new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                listView.mouseHoverItemIndex = listView.getInnerInstance().locationToIndex(new Point(event.getX(), event.getY()));
                listView.getInnerInstance().repaint();
            }
        });
        this.addMouseExitedEventHandler(new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                listView.mouseHoverItemIndex = -1;
                listView.getInnerInstance().repaint();
            }
        });
    }
    
    private ScrollPane scrollPane;
    
    private Vector<T> vector = new Vector<>();
    
    /**
     * コンストラクタ。<br>
     * このリストビューのリストアイテムを指定する。
     * 
     * @param items
     */
    public ListView(T[] items) {
        this(new JList<>());
        this.getItems().addAll(items);
    }
    
    /**
     * コンストラクタ。<br>
     * このリストビューのリストアイテムを指定する。
     * 
     * @param items
     */
    public ListView(java.util.Collection<T> items) {
        this(new JList<>());
        this.getItems().addAll(items);
    }

    /**
     * コンストラクタ。
     */
    public ListView() {
        this(new JList<>());
    }
    
    private int mouseHoverItemIndex = -1;
    
    private Color mouseHoverBackgroundColor;
    
    @Override
    protected void itemLabelCallback(Label label, T value, int index, boolean isSelected) {
        if (isSelected) {
            return;
        }
        if (index == this.mouseHoverItemIndex) {
            if (this.mouseHoverBackgroundColor == null) {
                Color foregroundColor = this.getInnerInstance().getForeground();
                this.mouseHoverBackgroundColor = GUI.createAlphaColor(foregroundColor, 0.05);
            }
            label.setBackgroundColor(this.mouseHoverBackgroundColor);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public JList<T> getInnerInstance() {
        return (JList<T>) super.getInnerInstance();
    }
    
    @Override
    public JScrollPane getInnerInstanceForLayout() {
        return (JScrollPane) super.getInnerInstanceForLayout();
    }
    
    @Override
    public Array<T> getSelectedItems() {
        return new Array<>(this.getInnerInstance().getSelectedValuesList());
    }
    
    @Override
    public void setSelectedItems(java.util.Collection<T> selectedItems) {
        this.getInnerInstance().clearSelection();
        for (T item : selectedItems) {
            int index = this.getItems().indexOf(item);
            if (index > -1) {
                this.getInnerInstance().addSelectionInterval(index, index);
            }
        }
    }
    
    @Override
    public void addSelectedItemChangeListener(ChangeListener<T> changeListener) {
        ListView<T> listView = this;
        ListSelectionListener innerInstance = changeListener.createInnerInstance(listView, new InnerInstanceCreator<>() {

            @Override
            public ListSelectionListener create() {
                return new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        if (event.getValueIsAdjusting()) {
                            changeListener.executeWhenChanged(listView, listView.getSelectedItem());
                        }
                    }
                };
            }
        });
        this.getInnerInstance().getSelectionModel().addListSelectionListener(innerInstance);
    }
    
    @Override
    public void addSelectedItemsChangeListener(ChangeListener<Array<T>> changeListener) {
        ListView<T> listView = this;
        ListSelectionListener innerInstance = changeListener.createInnerInstance(listView, new InnerInstanceCreator<>() {

            @Override
            public ListSelectionListener create() {
                return new ListSelectionListener() {
                    
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        if (event.getValueIsAdjusting()) {
                            changeListener.executeWhenChanged(listView, listView.getSelectedItems());
                        }
                    }
                };
            }
        });
        this.getInnerInstance().getSelectionModel().addListSelectionListener(innerInstance);
    }
    
    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof ListSelectionListener) {
                this.getInnerInstance().getSelectionModel().removeListSelectionListener((ListSelectionListener) innerInstance);
            }
        }
    }
    
    @Override
    protected void mapDisplayTextToItem(DefaultListCellRenderer renderer) {
        this.getInnerInstance().setCellRenderer(renderer);
    }
    
    /**
     * このリストビューの指定されたインデックスにスクロールして表示する。
     * 
     * @param index
     */
    public void scrollTo(int index) {
        this.getInnerInstance().ensureIndexIsVisible(index);
    }
    
    /**
     * このリストビューの指定されたリストアイテムをスクロールして表示する。
     * 
     * @param listItem
     */
    public void scrollTo(T listItem) {
        int index = this.getItems().indexOf(listItem);
        if (index > -1) {
            this.scrollTo(index);
        }
    }

    /**
     * このリストビューで複数選択が可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isAllowMultipleSelection() {
        return this.getInnerInstance().getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    }
    
    /**
     * このリストビューで複数選択を可能にする場合はtrueをセットする。
     * 
     * @param isAllowMultipleSelection
     */
    public void setAllowMultipleSelection(boolean isAllowMultipleSelection) {
        if (isAllowMultipleSelection) {
            this.getInnerInstance().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            this.getInnerInstance().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }
    
    /**
     * このリストビューのリストアイテムの高さを指定する。
     * 
     * @param height
     */
    public void setItemHeight(int height) {
        this.getInnerInstance().setFixedCellHeight(height);
    }
    
    /**
     * このリストビューで使用している水平方向のスクロールバーを取得する。
     * 
     * @return 結果。
     */
    public ScrollBar getHorizontalScrollBar() {
        return this.scrollPane.getHorizontalScrollBar();
    }

    /**
     * このリストビューで使用している垂直方向のスクロールバーを取得する。
     * 
     * @return 結果。
     */
    public ScrollBar getVerticalScrollBar() {
        return this.scrollPane.getVerticalScrollBar();
    }
}
