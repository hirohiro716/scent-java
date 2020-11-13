package com.hirohiro716.gui.control.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.hirohiro716.Array;
import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.CenterPane;
import com.hirohiro716.gui.control.ContextMenu;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.control.ScrollPane;
import com.hirohiro716.gui.dialog.InstantMessage;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.InnerInstanceCreator;
import com.hirohiro716.gui.event.KeyEvent;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * テーブルビューのクラス。
 * 
 * @author hiro
 * 
 * @param <C> カラム情報インスタンスの型。
 * @param <R> 行情報インスタンスの型。
 */
public abstract class TableView<C, R> extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected TableView(JTable innerInstance) {
        super(innerInstance, new JScrollPane(innerInstance));
        this.scrollPane = ScrollPane.newInstance(this.getInnerInstanceForLayout());
        this.getInnerInstance().setModel(this.tableModel);
        this.getInnerInstance().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.getInnerInstance().getTableHeader().setDefaultRenderer(new HeaderRenderer());
        this.getInnerInstance().setDefaultRenderer(String.class, new CellRenderer());
        this.getInnerInstance().setDefaultRenderer(Number.class, new CellRenderer());
        this.getInnerInstance().setDefaultRenderer(Datetime.class, new CellRenderer());
        this.getInnerInstance().setDefaultRenderer(Boolean.class, new CheckBoxCellRenderer());
        this.getInnerInstance().setDefaultRenderer(Button.class, new ButtonCellRenderer());
        this.getInnerInstance().setDefaultEditor(Button.class, new ButtonCellRenderer());
        this.getInnerInstance().setFocusable(true);
        this.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {

            @Override
            protected void handle(KeyEvent event) {
                switch (event.getKeyCode()) {
                case F2:
                    event.consume();
                    break;
                case TAB:
                    if (event.isShiftDown() == false) {
                        innerInstance.transferFocus();
                    } else {
                        innerInstance.transferFocusBackward();
                    }
                    event.consume();
                    break;
                default:
                    break;
                }
            }
        });
        this.adjustRowHeight();
    }
    
    /**
     * コンストラクタ。
     */
    public TableView() {
        this(new JTable());
    }
    
    private TableModel tableModel = new TableModel();
    
    private ScrollPane scrollPane;
    
    @Override
    public JTable getInnerInstance() {
        return (JTable) super.getInnerInstance();
    }
    
    @Override
    public JScrollPane getInnerInstanceForLayout() {
        return (JScrollPane) super.getInnerInstanceForLayout();
    }
    
    /**
     * このテーブルビューのスクロールペインを取得する。
     * 
     * @return 結果。
     */
    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    /**
     * このテーブルビューで複数選択が可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isAllowMultipleSelection() {
        return this.getInnerInstance().getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    }
    
    /**
     * このテーブルビューで複数選択を可能にする場合はtrueをセットする。
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

    private int defaultRowHeight = this.getFont().getSize() * 3;
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.defaultRowHeight = font.getSize() * 3;
        this.adjustRowHeight();
    }
    
    /**
     * このテーブルビューのデフォルトの行の高さをセットする。
     * 
     * @param height
     */
    public void setDefaultRowHeight(int height) {
        this.defaultRowHeight = height;
        this.adjustRowHeight();
    }
    
    /**
     * このテーブルビューの行の高さをフォントサイズを元に調整する。
     */
    private void adjustRowHeight() {
        this.getInnerInstance().setRowHeight(this.defaultRowHeight);
        JTableHeader tableHeader = this.getInnerInstance().getTableHeader();
        tableHeader.setPreferredSize(new Dimension(tableHeader.getSize().width, this.defaultRowHeight));
    }
    
    /**
     * このテーブルビューの行情報のインスタンスから値を抽出する。
     * 
     * @param rowInstance
     * @param columnInstance
     * @return 結果。
     */
    protected abstract Object getValueFromRow(R rowInstance, C columnInstance);
    
    /**
     * このテーブルビューの行情報のインスタンスに値をセットする。
     * 
     * @param rowInstance
     * @param columnInstance
     * @param value
     */
    protected abstract void setValueToRow(R rowInstance, C columnInstance, Object value);
    
    private Collection<C> columnInstances = new Collection<>();
    
    /**
     * このテーブルビューに追加されているカラムを識別するためのインスタンスをすべて取得する。
     * 
     * @return 結果。
     */
    public Array<C> getColumnInstances() {
        return this.columnInstances.toArray();
    }
    
    private Map<C, TableColumn> mapTableColumns = new HashMap<>();
    
    /**
     * このテーブルビューに追加されているカラムを識別するインスタンスから、テーブルカラムを取得する。
     * 
     * @param columnInstance
     * @return 結果。
     */
    public TableColumn getTableColumn(C columnInstance) {
        return this.mapTableColumns.get(columnInstance);
    }
    
    private HashMap<C, HorizontalAlignment> mapColumnHorizontalAlignment = new HashMap<>();
    
    /**
     * このテーブルビューにテーブルカラムを追加する。
     * 
     * @param columnInstance
     * @param columnType
     * @param horizontalAlignment 
     * @return 結果。
     */
    protected TableColumn addColumn(C columnInstance, ColumnType columnType, HorizontalAlignment horizontalAlignment) {
        this.columnInstances.add(columnInstance);
        TableColumn tableColumn = new TableColumn(columnInstance, columnType);
        this.mapTableColumns.put(columnInstance, tableColumn);
        this.mapColumnHorizontalAlignment.put(columnInstance, horizontalAlignment);
        this.tableModel.updateStructure();
        tableColumn.addHeaderMouseClickedEventHandler(MouseButton.BUTTON3, new HeaderClickedToSortEventHandler(columnInstance));
        return tableColumn;
    }
    
    /**
     * このテーブルビューに通常のカラムを追加する。
     * 
     * @param columnInstance
     * @param horizontalAlignment
     * @param width
     */
    public final void addColumn(C columnInstance, HorizontalAlignment horizontalAlignment, int width) {
        TableColumn tableColumn = this.addColumn(columnInstance, ColumnType.TEXT, horizontalAlignment);
        tableColumn.setWidth(width);
    }

    /**
     * このテーブルビューに通常のカラムを追加する。
     * 
     * @param columnInstance
     * @param horizontalAlignment
     */
    public final void addColumn(C columnInstance, HorizontalAlignment horizontalAlignment) {
        this.addColumn(columnInstance, ColumnType.TEXT, horizontalAlignment);
    }
    
    /**
     * このテーブルビューに数値のカラムを追加する。
     * 
     * @param columnInstance
     * @param numberFormat
     * @param width
     */
    public final void addColumnOfNumber(C columnInstance, NumberFormat numberFormat, int width) {
        TableColumn tableColumn = this.addColumn(columnInstance, ColumnType.NUMBER, HorizontalAlignment.RIGHT);
        tableColumn.setWidth(width);
        tableColumn.setNumberFormat(numberFormat);
    }

    /**
     * このテーブルビューに数値のカラムを追加する。
     * 
     * @param columnInstance
     * @param numberFormat
     */
    public final void addColumnOfNumber(C columnInstance, NumberFormat numberFormat) {
        TableColumn tableColumn = this.addColumn(columnInstance, ColumnType.NUMBER, HorizontalAlignment.RIGHT);
        tableColumn.setNumberFormat(numberFormat);
    }
    
    /**
     * このテーブルビューに日時のカラムを追加する。
     * 
     * @param columnInstance
     * @param dateFormat
     * @param width
     */
    public final void addColumnOfDatetime(C columnInstance, DateFormat dateFormat, int width) {
        TableColumn tableColumn = this.addColumn(columnInstance, ColumnType.DATETIME, HorizontalAlignment.CENTER);
        tableColumn.setWidth(width);
        tableColumn.setDateFormat(dateFormat);
    }

    /**
     * このテーブルビューに日時のカラムを追加する。
     * 
     * @param columnInstance
     * @param dateFormat
     */
    public final void addColumnOfDatetime(C columnInstance, DateFormat dateFormat) {
        TableColumn tableColumn = this.addColumn(columnInstance, ColumnType.DATETIME, HorizontalAlignment.CENTER);
        tableColumn.setDateFormat(dateFormat);
    }
    
    private Map<C, Boolean> mapColumnEditable = new HashMap<>();
    
    /**
     * このテーブルビューにチェックボックスのカラムを追加する。
     * 
     * @param columnInstance
     * @param isEditable
     */
    public final void addColumnOfCheckBox(C columnInstance, boolean isEditable) {
        this.addColumn(columnInstance, ColumnType.CHECKBOX, HorizontalAlignment.CENTER);
        this.mapColumnEditable.put(columnInstance, isEditable);
    }
    
    private Map<C, String> mapColumnButtonText = new HashMap<>();
    
    private Map<C, EventHandler<ActionEvent>> mapColumnButtonEventHandler = new HashMap<>();
    
    /**
     * このテーブルビューにボタンのカラムを追加する。
     * 
     * @param columnInstance
     * @param buttonText
     * @param eventHandler
     */
    public final void addColumnOfButton(C columnInstance, String buttonText, EventHandler<ActionEvent> eventHandler) {
        this.addColumn(columnInstance, ColumnType.BUTTON, HorizontalAlignment.CENTER);
        this.mapColumnButtonText.put(columnInstance, buttonText);
        this.mapColumnButtonEventHandler.put(columnInstance, eventHandler);
    }
    
    /**
     * このテーブルビューのカラムをクリアする。
     */
    public void clearColumnInstances() {
        this.columnInstances.clear();
        this.mapTableColumns.clear();
        this.mapColumnHorizontalAlignment.clear();
    }

    private Collection<R> rowInstances = new Collection<>();
    
    /**
     * このテーブルビューの行情報のインスタンスを格納しているコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<R> getRowInstances() {
        return this.rowInstances;
    }

    /**
     * このテーブルビューで選択されている行情報のインスタンスを取得する。
     * 
     * @return 結果。
     */
    public Array<R> getSelectedRows() {
        List<R> list = new ArrayList<>();
        for (int index : this.getInnerInstance().getSelectedRows()) {
            list.add(this.rowInstances.get(index));
        }
        return new Array<>(list);
    }
    
    /**
     * このテーブルビューで選択されている行情報のインスタンスの1番目を取得する。選択されているものがなければnullを返す。
     * 
     * @return 結果。
     */
    public R getSelectedRow() {
        for (R rowInstance : this.getSelectedRows()) {
            if (this.rowInstances.contains(rowInstance)) {
                return rowInstance;
            }
        }
        return null;
    }
    
    /**
     * このテーブルビューの指定された行情報のインスタンスを選択状態にする。
     * 
     * @param selectedRows
     */
    public void setSelectedRows(java.util.Collection<R> selectedRows) {
        this.getInnerInstance().getSelectionModel().clearSelection();
        for (R rowInstance : selectedRows) {
            int index = this.rowInstances.indexOf(rowInstance);
            this.getInnerInstance().getSelectionModel().addSelectionInterval(index, index);
        }
        if (selectedRows.size() > 0) {
            for (ChangeListener<R> changeListener : this.selectedRowChangeListeners) {
                changeListener.executeWhenChanged(this, this.getSelectedRow());
            }
        }
        for (ChangeListener<Array<R>> changeListener : this.selectedRowsChangeListeners) {
            changeListener.executeWhenChanged(this, this.getSelectedRows());
        }
    }
    
    /**
     * このテーブルビューの指定された行情報のインスタンスを選択状態にする。
     * 
     * @param selectedRows
     */
    public final void setSelectedRows(R[] selectedRows) {
        this.setSelectedRows(DynamicArray.newInstance(selectedRows).getValues());
    }
    
    /**
     * このテーブルビューの指定された行情報のインスタンスを選択状態にする。
     * 
     * @param selectedRow
     */
    public final void setSelectedRow(R selectedRow) {
        List<R> list = new ArrayList<>();
        list.add(selectedRow);
        this.setSelectedRows(list);
        
    }
    
    /**
     * このテーブルビューの選択状態を解除する。
     */
    public final void clearSelection() {
        this.setSelectedRows(new ArrayList<>());
    }
    
    private List<ChangeListener<R>> selectedRowChangeListeners = new ArrayList<>();
    
    /**
     * このテーブルビューの選択範囲の1番目が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addSelectedRowChangeListener(ChangeListener<R> changeListener) {
        TableView<C, R> tableView = this;
        this.selectedRowChangeListeners.add(changeListener);
        ListSelectionListener innerInstance = changeListener.createInnerInstance(tableView, new InnerInstanceCreator<>() {

            @Override
            public ListSelectionListener create() {
                return new ListSelectionListener() {
                    
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        // Get focus and then run listener
                        GUI.executeLater(new Runnable() {
                            
                            @Override
                            public void run() {
                                changeListener.executeWhenChanged(tableView, tableView.getSelectedRow());
                            }
                        });
                    }
                };
            }
        });
        this.getInnerInstance().getSelectionModel().addListSelectionListener(innerInstance);
    }

    private List<ChangeListener<Array<R>>> selectedRowsChangeListeners = new ArrayList<>();

    /**
     * このテーブルビューの選択範囲が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addSelectedRowsChangeListener(ChangeListener<Array<R>> changeListener) {
        TableView<C, R> tableView = this;
        this.selectedRowsChangeListeners.add(changeListener);
        ListSelectionListener innerInstance = changeListener.createInnerInstance(tableView, new InnerInstanceCreator<>() {

            @Override
            public ListSelectionListener create() {
                return new ListSelectionListener() {
                    
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        // Get focus and then run listener
                        GUI.executeLater(new Runnable() {
                            
                            @Override
                            public void run() {
                                changeListener.executeWhenChanged(tableView, tableView.getSelectedRows());
                            }
                        });
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
        this.selectedRowChangeListeners.remove(changeListener);
        this.selectedRowsChangeListeners.remove(changeListener);
    }
    
    /**
     * カラムの幅を維持する。
     */
    private void keepTableColumnWidth() {
        for (C column : this.columnInstances) {
            TableColumn tableColumn = this.getTableColumn(column);
            tableColumn.setWidth(tableColumn.getWidth());
        }
    }

    @Override
    public void updateLayout() {
        this.keepTableColumnWidth();
        super.updateLayout();
        this.tableModel.updateRows();
        this.tableModel.updateStructure();
    }
    
    @Override
    public void updateDisplay() {
        this.keepTableColumnWidth();
        super.updateDisplay();
        this.tableModel.updateRows();
        this.tableModel.updateStructure();
    }
    
    /**
     * カラム種類の列挙型。
     * 
     * @author hiro
     *
     */
    protected enum ColumnType {
        /**
         * 文字列のセルを表示するカラム。
         */
        TEXT,
        /**
         * 数値のセルを表示するカラム。
         */
        NUMBER,
        /**
         * 日時のセルを表示するカラム。
         */
        DATETIME,
        /**
         * チェックボックスのセルを表示するカラム。
         */
        CHECKBOX,
        /**
         * ボタンのセルを表示するカラム。
         */
        BUTTON,
    }
    
    /**
     * テーブルモデルのクラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    private class TableModel extends AbstractTableModel {
        
        /**
         * テーブルビューの情報を更新する。
         */
        public void updateRows() {
            this.fireTableDataChanged();
        }
        
        /**
         * テーブルビューの構造を更新する。
         */
        public void updateStructure() {
            this.fireTableStructureChanged();
            TableView<C, R> tableView = TableView.this;
            for (TableColumn tableColumn : tableView.mapTableColumns.values()) {
                tableColumn.updateLayoutAndDisplay();
            }
        }
        
        @Override
        public String getColumnName(int columnIndex) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(columnIndex);
            return tableView.mapTableColumns.get(columnInstance).getHeaderText();
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(columnIndex);
            switch (tableView.mapTableColumns.get(columnInstance).getColumnType()) {
            case TEXT:
                return String.class;
            case NUMBER:
                return Number.class;
            case DATETIME:
                return Datetime.class;
            case CHECKBOX:
                return Boolean.class;
            case BUTTON:
                return Button.class;
            }
            return Object.class;
        }
        
        @Override
        public int getRowCount() {
            TableView<C, R> tableView = TableView.this;
            return tableView.rowInstances.size();
        }
        
        @Override
        public int getColumnCount() {
            TableView<C, R> tableView = TableView.this;
            return tableView.columnInstances.size();
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(columnIndex);
            TableColumn tableColumn = tableView.mapTableColumns.get(columnInstance);
            R rowInstance = tableView.rowInstances.get(rowIndex);
            Object value = tableView.getValueFromRow(rowInstance, columnInstance);
            if (value != null) {
                switch (tableView.mapTableColumns.get(columnInstance).getColumnType()) {
                case NUMBER:
                    NumberFormat numberFormat = tableColumn.getNumberFormat();
                    if (numberFormat != null) {
                        if (value instanceof Number) {
                            Number numberValue = (Number) value;
                            return numberFormat.format(numberValue.doubleValue());
                        }
                    }
                    break;
                case DATETIME:
                    DateFormat dateFormat = tableColumn.getDateFormat();
                    if (dateFormat == null) {
                        dateFormat = Datetime.DEFAULT_DATE_FORMAT;
                    }
                    if (value instanceof Datetime) {
                        Datetime datetimeValue = (Datetime) value;
                        return datetimeValue.toString(dateFormat);
                    }
                    if (value instanceof Date) {
                        return Datetime.newInstance((Date) value).toString(dateFormat);
                    }
                    break;
                case TEXT:
                case CHECKBOX:
                case BUTTON:
                    break;
                }
            }
            return value;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(columnIndex);
            TableColumn tableColumn = tableView.mapTableColumns.get(columnInstance);
            switch (tableColumn.getColumnType()) {
            case CHECKBOX:
                boolean isEditable = tableView.mapColumnEditable.get(columnInstance);
                return isEditable;
            case BUTTON:
                return true;
            case TEXT:
            case NUMBER:
            case DATETIME:
                break;
            }
            return false;
        }
        
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(columnIndex);
            R rowInstance = tableView.rowInstances.get(rowIndex);
            tableView.setValueToRow(rowInstance, columnInstance, aValue);
        }
    }

    /**
     * テーブルヘッダーのレンダラークラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    private class HeaderRenderer extends DefaultTableCellRenderer {
        
        /**
         * コンストラクタ。
         */
        public HeaderRenderer() {
            TableView<C, R> tableView = TableView.this;
            this.defaultRenderer = tableView.getInnerInstance().getTableHeader().getDefaultRenderer();
        }
        
        private javax.swing.table.TableCellRenderer defaultRenderer;

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(column);
            TableColumn tableColumn = tableView.mapTableColumns.get(columnInstance);
            JLabel jLabel = (JLabel) this.defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Label label = Label.newInstance(jLabel);
            label.setFont(tableView.getFont());
            label.setTextHorizontalAlignment(tableColumn.getHeaderHorizontalAlignment());
            return label.getInnerInstance();
        }
    }
    
    /**
     * テーブルセルのレンダラークラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    private class CellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(column);
            JLabel jLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Label label = Label.newInstance(jLabel);
            if (tableView.mapColumnHorizontalAlignment.containsKey(columnInstance)) {
                label.setTextHorizontalAlignment(tableView.mapColumnHorizontalAlignment.get(columnInstance));
            }
            return label.getInnerInstance();
        }
    }

    /**
     * チェックボックスセルのレンダラークラス。
     * 
     * @author hiro
     *
     */
    private class CheckBoxCellRenderer implements TableCellRenderer {
        
        /**
         * コンストラクタ。
         */
        public CheckBoxCellRenderer() {
            TableView<C, R> tableView = TableView.this;
            this.defaultRenderer = tableView.getInnerInstance().getDefaultRenderer(Boolean.class);
        }
        
        private TableCellRenderer defaultRenderer;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TableView<C, R> tableView = TableView.this;
            Boolean booleanValue = StringObject.newInstance(value).toBoolean();
            JCheckBox checkBox = (JCheckBox) this.defaultRenderer.getTableCellRendererComponent(table, booleanValue, isSelected, hasFocus, row, column);
            checkBox.setFont(tableView.getFont());
            checkBox.setBackground(new Color(checkBox.getBackground().getRGB()));
            checkBox.setOpaque(true);
            return checkBox;
        }
    }
    
    /**
     * ボタンセルのレンダラークラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    private class ButtonCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        
        /**
         * コンストラクタ。
         */
        public ButtonCellRenderer() {
            TableView<C, R> tableView = TableView.this;
            this.defaultRenderer = tableView.getInnerInstance().getDefaultRenderer(String.class);
        }
        
        private TableCellRenderer defaultRenderer;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TableView<C, R> tableView = TableView.this;
            JLabel jLabel = (JLabel) this.defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            C columnInstance = tableView.columnInstances.get(column);
            Button button = new Button(tableView.mapColumnButtonText.get(columnInstance));
            button.setFont(tableView.getFont());
            CenterPane pane = new CenterPane();
            pane.setBackgroundColor(new Color(jLabel.getBackground().getRGB()));
            pane.setControl(button);
            return pane.getInnerInstance();
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            TableView<C, R> tableView = TableView.this;
            C columnInstance = tableView.columnInstances.get(column);
            Button button = new Button(tableView.mapColumnButtonText.get(columnInstance));
            button.setFont(tableView.getFont());
            button.setFocusable(false);
            EventHandler<ActionEvent> eventHandler = tableView.mapColumnButtonEventHandler.get(columnInstance);
            if (eventHandler != null) {
                button.addActionEventHandler(eventHandler);
            }
            CenterPane pane = new CenterPane();
            pane.setBackgroundColor(new Color(tableView.getInnerInstance().getSelectionBackground().getRGB()));
            pane.setControl(button);
            return pane.getInnerInstance();
        }
        
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
    
    /**
     * テーブルビューカラムのクラス。
     * 
     * @author hiro
     *
     */
    public class TableColumn implements TableColumnInterface<C> {
        
        /**
         * コンストラクタ。<br>
         * テーブルカラムを識別するためのインスタンス、テーブルカラムのタイプを指定する。
         * 
         * @param columnInstance
         * @param columnType 
         */
        private TableColumn(C columnInstance, ColumnType columnType) {
            this.columnInstance = columnInstance;
            this.headerText = columnInstance.toString();
            this.columnType = columnType;
        }
        
        private C columnInstance;
        
        @Override
        public C getColumnInstance() {
            return this.columnInstance;
        }
        
        private String headerText;
        
        @Override
        public String getHeaderText() {
            return this.headerText;
        }
        
        @Override
        public void setHeaderText(String headerText) {
            TableView<C, R> tableView = TableView.this;
            this.headerText = headerText;
            tableView.tableModel.updateStructure();
        }
        
        private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
        
        @Override
        public HorizontalAlignment getHeaderHorizontalAlignment() {
            return this.horizontalAlignment;
        }
        
        @Override
        public void setHeaderHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
        }
        
        private ColumnType columnType;
        
        /**
         * このテーブルカラムのタイプを取得する。
         * 
         * @return 結果。
         */
        public ColumnType getColumnType() {
            return this.columnType;
        }
        
        private NumberFormat numberFormat = null;
        
        /**
         * このテーブルカラムの数値変換定義を取得する。
         * 
         * @return 結果。
         */
        public NumberFormat getNumberFormat() {
            return this.numberFormat;
        }
        
        /**
         * このテーブルカラムの数値変換定義をセットする。
         * 
         * @param numberFormat
         */
        public void setNumberFormat(NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
        }
        
        private DateFormat dateFormat = null;
        
        /**
         * このテーブルカラムの日時変換定義を取得する。
         * 
         * @return 結果。
         */
        public DateFormat getDateFormat() {
            return this.dateFormat;
        }
        
        /**
         * このテーブルカラムの日時変換定義をセットする。
         * 
         * @param dateFormat
         */
        public void setDateFormat(DateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }
        
        /**
         * JTable内のJTableHeaderインスタンスを取得する。
         * 
         * @return 結果。
         */
        private JTableHeader getJTableHeader() {
            TableView<C, R> tableView = TableView.this;
            return tableView.getInnerInstance().getTableHeader();
        }
        
        /**
         * このコンポーネントがラップしている、GUIライブラリに依存したインスタンスを取得する。
         * 
         * @return 結果。
         */
        private javax.swing.table.TableColumn getInnerInstance() {
            TableView<C, R> tableView = TableView.this;
            return this.getJTableHeader().getColumnModel().getColumn(tableView.columnInstances.indexOf(this.columnInstance));
        }
        
        private Integer width = null;
        
        @Override
        public Integer getWidth() {
            return this.getInnerInstance().getWidth();
        }
        
        @Override
        public void setWidth(Integer width) {
            this.width = width;
            javax.swing.table.TableColumn innerInstance = this.getInnerInstance();
            if (width != null) {
                innerInstance.setWidth(width);
                innerInstance.setPreferredWidth(width);
            } else {
                innerInstance.sizeWidthToFit();
            }
        }
        
        private Integer minimumWidth = null;
        
        @Override
        public Integer getMinimumWidth() {
            return this.getInnerInstance().getMinWidth();
        }
        
        @Override
        public void setMinimumWidth(Integer width) {
            this.minimumWidth = width;
            javax.swing.table.TableColumn innerInstance = this.getInnerInstance();
            if (width != null) {
                innerInstance.setMinWidth(width);
            } else {
                innerInstance.setMinWidth(0);
            }
        }
        
        private Integer maximumWidth = null;
        
        @Override
        public Integer getMaximumWidth() {
            return this.getInnerInstance().getMaxWidth();
        }
        
        @Override
        public void setMaximumWidth(Integer width) {
            this.maximumWidth = width;
            javax.swing.table.TableColumn innerInstance = this.getInnerInstance();
            if (width != null) {
                innerInstance.setMaxWidth(width);
            } else {
                innerInstance.setMaxWidth(Integer.MAX_VALUE);
            }
        }
        
        private Boolean isResizable = null;
        
        @Override
        public boolean isResizable() {
            return this.getInnerInstance().getResizable();
        }
        
        @Override
        public void setResizable(boolean isResizable) {
            this.isResizable = isResizable;
            javax.swing.table.TableColumn innerInstance = this.getInnerInstance();
            innerInstance.setResizable(isResizable);
        }
        
        @Override
        public void addHeaderMouseClickedEventHandler(EventHandler<MouseEvent> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            TableColumn tableColumn = this;
            MouseListener innerInstance = eventHandler.createInnerInstance(tableView, new InnerInstanceCreator<>() {

                @Override
                public MouseListener create() {
                    return new MouseAdapter() {

                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent event) {
                            int columnIndex = tableColumn.getJTableHeader().columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.getJTableHeader().addMouseListener(innerInstance);
        }
        
        @Override
        public void addHeaderMouseClickedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            TableColumn tableColumn = this;
            MouseListener innerInstance = eventHandler.createInnerInstance(tableView, new InnerInstanceCreator<>() {

                @Override
                public MouseListener create() {
                    return new MouseAdapter() {

                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent event) {
                            int columnIndex = tableColumn.getJTableHeader().columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                MouseEvent mouseEvent = new MouseEvent(tableView, event);
                                if (mouseEvent.getMouseButton() == mouseButton) {
                                    eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                                }
                            }
                        }
                    };
                }
            });
            this.getJTableHeader().addMouseListener(innerInstance);
        }
        
        @Override
        public void addHeaderMousePressedEventHandler(EventHandler<MouseEvent> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            TableColumn tableColumn = this;
            MouseListener innerInstance = eventHandler.createInnerInstance(tableView, new InnerInstanceCreator<>() {

                @Override
                public MouseListener create() {
                    return new MouseAdapter() {

                        @Override
                        public void mousePressed(java.awt.event.MouseEvent event) {
                            int columnIndex = tableColumn.getJTableHeader().columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.getJTableHeader().addMouseListener(innerInstance);
        }
        
        @Override
        public void addHeaderMousePressedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            TableColumn tableColumn = this;
            MouseListener innerInstance = eventHandler.createInnerInstance(tableView, new InnerInstanceCreator<>() {

                @Override
                public MouseListener create() {
                    return new MouseAdapter() {
                        
                        @Override
                        public void mousePressed(java.awt.event.MouseEvent event) {
                            int columnIndex = tableColumn.getJTableHeader().columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                MouseEvent mouseEvent = new MouseEvent(tableView, event);
                                if (mouseEvent.getMouseButton() == mouseButton) {
                                    eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                                }
                            }
                        }
                    };
                }
            });
            this.getJTableHeader().addMouseListener(innerInstance);
        }
        
        @Override
        public void addHeaderMouseReleasedEventHandler(EventHandler<MouseEvent> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            TableColumn tableColumn = this;
            MouseListener innerInstance = eventHandler.createInnerInstance(tableView, new InnerInstanceCreator<>() {

                @Override
                public MouseListener create() {
                    return new MouseAdapter() {

                        @Override
                        public void mouseReleased(java.awt.event.MouseEvent event) {
                            int columnIndex = tableColumn.getJTableHeader().columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.getJTableHeader().addMouseListener(innerInstance);
        }
        
        @Override
        public void addHeaderMouseReleasedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            TableColumn tableColumn = this;
            MouseListener innerInstance = eventHandler.createInnerInstance(tableView, new InnerInstanceCreator<>() {

                @Override
                public MouseListener create() {
                    return new MouseAdapter() {

                        @Override
                        public void mouseReleased(java.awt.event.MouseEvent event) {
                            int columnIndex = tableColumn.getJTableHeader().columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                MouseEvent mouseEvent = new MouseEvent(tableView, event);
                                if (mouseEvent.getMouseButton() == mouseButton) {
                                    eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                                }
                            }
                        }
                    };
                }
            });
            this.getJTableHeader().addMouseListener(innerInstance);
        }
        
        @Override
        public void addHeaderMouseWheelEventHandler(EventHandler<MouseEvent> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            TableColumn tableColumn = this;
            MouseWheelListener innerInstance = eventHandler.createInnerInstance(tableView, new InnerInstanceCreator<>() {

                @Override
                public MouseWheelListener create() {
                    return new MouseWheelListener() {
                        
                        @Override
                        public void mouseWheelMoved(MouseWheelEvent event) {
                            int columnIndex = tableColumn.getJTableHeader().columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.getJTableHeader().addMouseWheelListener(innerInstance);
        }
        
        @Override
        public void removeEventHandler(EventHandler<?> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            for (Object innerInstance : eventHandler.getInnerInstances(tableView)) {
                if (innerInstance instanceof MouseListener) {
                    this.getJTableHeader().removeMouseListener((MouseListener) innerInstance);
                }
                if (innerInstance instanceof MouseMotionListener) {
                    this.getJTableHeader().removeMouseMotionListener((MouseMotionListener) innerInstance);
                }
                if (innerInstance instanceof MouseWheelListener) {
                    this.getJTableHeader().removeMouseWheelListener((MouseWheelListener) innerInstance);
                }
            }
        }
        
        @Override
        public String toString() {
            return this.columnInstance.toString();
        }
        
        /**
         * このコンポーネントのレイアウトと表示を更新する。
         */
        public void updateLayoutAndDisplay() {
            javax.swing.table.TableColumn innerInstance = this.getInnerInstance();
            if (this.width != null) {
                innerInstance.setWidth(this.width);
                innerInstance.setPreferredWidth(this.width);
            } else {
                innerInstance.sizeWidthToFit();
            }
            if (this.minimumWidth != null) {
                innerInstance.setMinWidth(this.minimumWidth);
            }
            if (this.maximumWidth != null) {
                innerInstance.setMaxWidth(this.maximumWidth);
            }
            if (this.isResizable != null) {
                innerInstance.setResizable(this.isResizable);
            }
            this.getJTableHeader().doLayout();
            this.getJTableHeader().repaint();
        }
    }

    /**
     * テーブルビューのヘッダーをクリックしてソートするイベントハンドラー。
     * 
     * @author hiro
     *
     */
    private class HeaderClickedToSortEventHandler extends EventHandler<MouseEvent> {
        
        /**
         * コンストラクタ。<br>
         * カラムを識別するインスタンスを指定する。
         * 
         * @param columnInstance
         */
        protected HeaderClickedToSortEventHandler(C columnInstance) {
            this.columnInstance = columnInstance;
        }
        
        private C columnInstance;
        
        /**
         * 降順かどうかを指定して並び替えを実行する。
         * 
         * @param isDescent
         */
        private void sort(boolean isDescent) {
            TableView<C, R> tableView = TableView.this;
            HeaderClickedToSortEventHandler handler = this;
            InstantMessage message = new InstantMessage(tableView.getFrame());
            message.setText("並び替えを処理しています。");
            message.show();
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    List<R> sorted = new ArrayList<>(tableView.rowInstances.toUnmodifiableList());
                    java.util.Comparator<R> comparator = new Comparator(handler.columnInstance);
                    if (isDescent) {
                        comparator = comparator.reversed();
                    }
                    Collections.sort(sorted, comparator);
                    GUI.executeLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            tableView.getRowInstances().clear();
                            tableView.getRowInstances().addAll(sorted);
                            tableView.updateDisplay();
                            message.close();
                        }
                    });
                }
            });
            thread.start();
        }
        
        @Override
        protected void handle(MouseEvent event) {
            TableView<C, R> tableView = TableView.this;
            HeaderClickedToSortEventHandler handler = this;
            if (event.getClickCount() != 1) {
                return;
            }
            ContextMenu menu = new ContextMenu(event.getSource());
            menu.addContextMenuItem("昇順で並び替え(A)", KeyCode.A, new Runnable() {
                
                @Override
                public void run() {
                    handler.sort(false);
                }
            });
            menu.addContextMenuItem("降順で並び替え(D)", KeyCode.D, new Runnable() {
                
                @Override
                public void run() {
                    handler.sort(true);
                }
            });
            menu.show(event.getX(), event.getY() + tableView.getScrollPane().getVerticalScrollBar().getScrollPosition() - tableView.getInnerInstance().getTableHeader().getHeight());
        }
    }

    /**
     * 指定されたカラムの値同士を使用して2つの行情報のインスタンスの順序付けをする比較クラス。
     * 
     * @author hiro
     *
     */
    private class Comparator implements java.util.Comparator<R> {
        
        private C columnInstance;
        
        /**
         * コンストラクタ。
         * 
         * @param sortKey 比較に使用する値を取得するためのキー。
         */
        public Comparator(C sortKey) {
            this.columnInstance = sortKey;
        }
        
        @Override
        public int compare(R row1, R row2) {
            TableView<C, R> tableView = TableView.this;
            Object value1 = tableView.getValueFromRow(row1, this.columnInstance);
            Object value2 = tableView.getValueFromRow(row2, this.columnInstance);
            if (value1 instanceof Number || value2 instanceof Number) {
                Number number1 = StringObject.newInstance(value1).toDouble();
                if (number1 == null) {
                    number1 = Double.MIN_VALUE;
                }
                Number number2 = StringObject.newInstance(value2).toDouble();
                if (number2 == null) {
                    number2 = Double.MIN_VALUE;
                }
                if (number1.doubleValue() > number2.doubleValue()) {
                    return 1;
                } else if (number1.doubleValue() == number2.doubleValue()) {
                    return 0;
                } else {
                    return -1;
                }
            }
            if (value1 instanceof Date || value2 instanceof Date) {
                Date date1 = (Date) value1;
                if (date1 == null) {
                    date1 = new Date(Long.MIN_VALUE);
                }
                Date date2 = (Date) value2;
                if (date2 == null) {
                    date2 = new Date(Long.MIN_VALUE);
                }
                if (date1.getTime() > date2.getTime()) {
                    return 1;
                } else if (date1.getTime() == date2.getTime()) {
                    return 0;
                } else {
                    return -1;
                }
            }
            String string1 = new StringObject(value1).toString();
            String string2 = new StringObject(value2).toString();
            return string1.compareTo(string2);
        }
    }
    
}
