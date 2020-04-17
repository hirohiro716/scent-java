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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.hirohiro716.Array;
import com.hirohiro716.DynamicArray;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.CenterPane;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.InnerInstanceCreator;
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
        this.getInnerInstance().setModel(this.tableModel);
        this.getInnerInstance().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.getInnerInstance().getTableHeader().setDefaultRenderer(new HeaderRenderer());
        this.getInnerInstance().setDefaultRenderer(String.class, new CellRenderer());
        this.getInnerInstance().setDefaultRenderer(Number.class, new CellRenderer());
        this.getInnerInstance().setDefaultRenderer(Datetime.class, new CellRenderer());
        this.getInnerInstance().setDefaultRenderer(Boolean.class, new CheckBoxCellRenderer());
        this.getInnerInstance().setDefaultRenderer(Button.class, new ButtonCellRenderer());
        this.getInnerInstance().setDefaultEditor(Button.class, new ButtonCellRenderer());
        this.adjustRowHeight();
    }
    
    /**
     * コンストラクタ。
     */
    public TableView() {
        this(new JTable());
    }
    
    private TableModel tableModel = new TableModel();
    
    @Override
    public JTable getInnerInstance() {
        return (JTable) super.getInnerInstance();
    }
    
    @Override
    public JScrollPane getInnerInstanceForLayout() {
        return (JScrollPane) super.getInnerInstanceForLayout();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.adjustRowHeight();
    }
    
    /**
     * このテーブルビューの行の高さをフォントサイズを元に調整する。
     */
    private void adjustRowHeight() {
        this.getInnerInstance().setRowHeight(this.getFont().getSize() * 3);
        JTableHeader tableHeader = this.getInnerInstance().getTableHeader();
        tableHeader.setPreferredSize(new Dimension(tableHeader.getSize().width, this.getFont().getSize() * 3));
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
    private TableColumn addColumn(C columnInstance, ColumnType columnType, HorizontalAlignment horizontalAlignment) {
        this.columnInstances.add(columnInstance);
        TableColumn tableColumn = new TableColumn(this.getInnerInstance().getTableHeader(), columnInstance, columnType);
        this.mapTableColumns.put(columnInstance, tableColumn);
        this.mapColumnHorizontalAlignment.put(columnInstance, horizontalAlignment);
        this.tableModel.updateStructure();
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
     * このテーブルビューのカラムを削除する。
     * 
     * @param column
     */
    public void removeColumn(C column) {
        this.columnInstances.remove(column);
    }

    private Collection<R> rowInstances = new Collection<>();
    
    /**
     * このテーブルビューの行情報のインスタンスを格納しているコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<R> getRows() {
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
            return rowInstance;
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

    /**
     * このテーブルビューの選択範囲の1番目が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addSelectedRowChangeListener(ChangeListener<R> changeListener) {
        TableView<C, R> tableView = this;
        ListSelectionListener innerInstance = changeListener.createInnerInstance(tableView, new InnerInstanceCreator<>() {

            @Override
            public ListSelectionListener create() {
                return new ListSelectionListener() {
                    
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        if (event.getValueIsAdjusting()) {
                            changeListener.executeWhenChanged(tableView, tableView.getSelectedRow());
                        }
                    }
                };
            }
        });
        this.getInnerInstance().getSelectionModel().addListSelectionListener(innerInstance);
    }
    
    /**
     * このテーブルビューの選択範囲が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addSelectedRowsChangeListener(ChangeListener<Array<R>> changeListener) {
        TableView<C, R> tableView = this;
        ListSelectionListener innerInstance = changeListener.createInnerInstance(tableView, new InnerInstanceCreator<>() {

            @Override
            public ListSelectionListener create() {
                return new ListSelectionListener() {
                    
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        if (event.getValueIsAdjusting()) {
                            changeListener.executeWhenChanged(tableView, tableView.getSelectedRows());
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
    public void updateLayout() {
        super.updateLayout();
        this.tableModel.updateRows();
        this.tableModel.updateStructure();
    }
    
    @Override
    public void updateDisplay() {
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
    private enum ColumnType {
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
            JCheckBox checkBox = (JCheckBox) this.defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
            button.addActionEventHandler(tableView.mapColumnButtonEventHandler.get(columnInstance));
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
         * このコンポーネントがラップする、GUIライブラリに依存したインスタンスと、<br>
         * テーブルカラムを識別するためのインスタンス、テーブルカラムのタイプを指定する。
         * 
         * @param jTableHeader 
         * @param columnInstance
         * @param columnType 
         */
        private TableColumn(JTableHeader jTableHeader, C columnInstance, ColumnType columnType) {
            this.jTableHeader = jTableHeader;
            this.columnInstance = columnInstance;
            this.headerText = columnInstance.toString();
            this.columnType = columnType;
        }
        
        private JTableHeader jTableHeader;
        
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
            this.headerText = headerText;
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
         * このコンポーネントがラップしている、GUIライブラリに依存したインスタンスを取得する。
         * 
         * @return 結果。
         */
        private javax.swing.table.TableColumn getInnerInstance() {
            for (int index = 0; index < this.jTableHeader.getColumnModel().getColumnCount(); index++) {
                javax.swing.table.TableColumn tableColumn = this.jTableHeader.getColumnModel().getColumn(index);
                if (tableColumn.getIdentifier().equals(this.columnInstance)) {
                    return tableColumn;
                }
            }
            return null;
        }
        
        private Integer width = null;
        
        @Override
        public Integer getWidth() {
            return this.getInnerInstance().getWidth();
        }
        
        @Override
        public void setWidth(Integer width) {
            javax.swing.table.TableColumn innerInstance = this.getInnerInstance();
            this.width = width;
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
            if (width != null) {
                this.getInnerInstance().setMinWidth(width);
            } else {
                this.getInnerInstance().setMinWidth(0);
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
            if (width != null) {
                this.getInnerInstance().setMaxWidth(width);
            } else {
                this.getInnerInstance().setMaxWidth(Integer.MAX_VALUE);
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
            this.getInnerInstance().setResizable(isResizable);
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
                            int columnIndex = tableColumn.jTableHeader.columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.jTableHeader.addMouseListener(innerInstance);
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
                            int columnIndex = tableColumn.jTableHeader.columnAtPoint(event.getPoint());
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
            this.jTableHeader.addMouseListener(innerInstance);
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
                            int columnIndex = tableColumn.jTableHeader.columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.jTableHeader.addMouseListener(innerInstance);
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
                            int columnIndex = tableColumn.jTableHeader.columnAtPoint(event.getPoint());
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
            this.jTableHeader.addMouseListener(innerInstance);
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
                            int columnIndex = tableColumn.jTableHeader.columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.jTableHeader.addMouseListener(innerInstance);
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
                            int columnIndex = tableColumn.jTableHeader.columnAtPoint(event.getPoint());
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
            this.jTableHeader.addMouseListener(innerInstance);
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
                            int columnIndex = tableColumn.jTableHeader.columnAtPoint(event.getPoint());
                            if (tableView.columnInstances.get(columnIndex).equals(tableColumn.columnInstance)) {
                                eventHandler.executeWhenControlEnabled(new MouseEvent(tableView, event));
                            }
                        }
                    };
                }
            });
            this.jTableHeader.addMouseWheelListener(innerInstance);
        }
        
        @Override
        public void removeEventHandler(EventHandler<?> eventHandler) {
            TableView<C, R> tableView = TableView.this;
            for (Object innerInstance : eventHandler.getInnerInstances(tableView)) {
                if (innerInstance instanceof MouseListener) {
                    this.jTableHeader.removeMouseListener((MouseListener) innerInstance);
                }
                if (innerInstance instanceof MouseMotionListener) {
                    this.jTableHeader.removeMouseMotionListener((MouseMotionListener) innerInstance);
                }
                if (innerInstance instanceof MouseWheelListener) {
                    this.jTableHeader.removeMouseWheelListener((MouseWheelListener) innerInstance);
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
            this.jTableHeader.doLayout();
            this.jTableHeader.repaint();
        }
    }
}
