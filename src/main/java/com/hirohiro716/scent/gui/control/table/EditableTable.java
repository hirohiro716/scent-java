package com.hirohiro716.scent.gui.control.table;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.gui.Border;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.MouseCursor;
import com.hirohiro716.scent.gui.collection.AddListener;
import com.hirohiro716.scent.gui.collection.Collection;
import com.hirohiro716.scent.gui.collection.RemoveListener;
import com.hirohiro716.scent.gui.control.AutocompleteTextField;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.CheckBox;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.DatePicker;
import com.hirohiro716.scent.gui.control.DropDownList;
import com.hirohiro716.scent.gui.control.Label;
import com.hirohiro716.scent.gui.control.Pane;
import com.hirohiro716.scent.gui.control.PasswordField;
import com.hirohiro716.scent.gui.control.ScrollPane;
import com.hirohiro716.scent.gui.control.TextField;
import com.hirohiro716.scent.gui.control.ScrollPane.ScrollBarDisplayPolicy;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.KeyEvent;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.MouseButton;

/**
 * 編集に特化したテーブルのクラス。
 *
 * @param <C> カラム情報インスタンスの型。
 * @param <R> 行情報インスタンスの型。
 */
public abstract class EditableTable<C, R> extends Control {
    
    /**
     * コンストラクタ。
     */
    protected EditableTable() {
        super(new JPanel());
        EditableTable<C, R> editableTable = this;
        // Root pane
        Font defaultFont = this.getInnerInstance().getFont();
        this.root = Pane.newInstance(this.getInnerInstance());
        this.root.setFont(defaultFont);
        this.root.setParent(this);
        GridBagLayout layout = new GridBagLayout();
        this.root.getInnerInstance().setLayout(layout);
        this.root.setBorder(Border.createLine(this.borderColor, 1));
        this.root.addSizeChangeListener(new ChangeListener<>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                editableTable.updateDisplay();
            }
        });
        // Header pane
        this.headerPane.getInnerInstance().setLayout(this.headerLayout);
        this.headerSpacer.setBackgroundColor(null);
        this.headerPane.getChildren().add(this.headerSpacer);
        this.headerScrollPane.setContent(this.headerPane);
        this.headerScrollPane.setHorizontalScrollBarDisplayPolicy(ScrollBarDisplayPolicy.NEVER);
        this.headerScrollPane.setVerticalScrollBarDisplayPolicy(ScrollBarDisplayPolicy.NEVER);
        this.headerScrollPane.setBorder(Border.createLine(this.borderColor, 0, 0, 1, 0));
        this.headerScrollPane.getInnerInstance().setWheelScrollingEnabled(false);
        this.headerPane.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                editableTable.headerScrollPane.setMinimumHeight(changedValue.getIntegerHeight());
                editableTable.rowsPane.setWidth(changedValue.getIntegerWidth());
                editableTable.rowsPane.setMinimumWidth(changedValue.getIntegerWidth());
                for (Pane controlPane: editableTable.rowControlPanes) {
                    controlPane.setWidth(changedValue.getIntegerWidth());
                    controlPane.setMinimumWidth(changedValue.getIntegerWidth());
                    controlPane.getInnerInstance().doLayout();
                }
                editableTable.headerScrollPane.getHorizontalScrollBar().setScrollPosition(editableTable.rowsScrollPane.getHorizontalScrollBar().getScrollPosition());
            }
        });
        this.root.getChildren().add(this.headerScrollPane);
        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerConstraints.gridy = 0;
        headerConstraints.weightx = 1;
        headerConstraints.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(this.headerScrollPane.getInnerInstance(), headerConstraints);
        // Rows pane
        this.rowsPane.getInnerInstance().setLayout(this.rowsLayout);
        this.bottomSpacer.setBackgroundColor(null);
        this.rowsPane.getChildren().add(this.bottomSpacer);
        this.rowsPane.addLocationChangeListener(new ChangeListener<Point>() {
            
            @Override
            protected void changed(Component<?> component, Point changedValue, Point previousValue) {
                editableTable.headerPane.setX(changedValue.x);
            }
        });
        this.rowsScrollPane.setContent(this.rowsPane);
        this.rowsScrollPane.setBorder(null);
        this.rowsScrollPane.getVerticalScrollBar().addScrollPositionChangeListener(this.scrollPositionChangeListener);
        this.adjustRowHeight();
        this.root.getChildren().add(this.rowsScrollPane);
        GridBagConstraints rowsConstraints = new GridBagConstraints();
        rowsConstraints.gridx = 0;
        rowsConstraints.gridy = 1;
        rowsConstraints.weightx = 1;
        rowsConstraints.weighty = 1;
        rowsConstraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(this.rowsScrollPane.getInnerInstance(), rowsConstraints);
        // Rows listener
        this.rowInstances.addListener(new AddListener<R>() {
            
            @Override
            protected void added(R added, int positionIndex) {
                editableTable.addRow(added);
            }
        });
        this.rowInstances.addListener(new RemoveListener<R>() {
            
            @Override
            protected void removed(R removed) {
                editableTable.removeRow(removed);
            }
        });
    }
    
    private Color borderColor = GUI.getBorderColor();
    
    private Color lightBorderColor = GUI.getBorderLightColor();
    
    private Color labelForegroundColor = GUI.getLabelForegroundColor();
    
    private Color activatedForegroundColor = GUI.getActiveForegroundColor();
    
    private Color activatedBackgroundColor = GUI.getActiveBackgroundColor();
    
    private Pane root;
    
    private ScrollPane headerScrollPane = new ScrollPane();
    
    private Pane headerPane = new Pane();
    
    private GridBagLayout headerLayout = new GridBagLayout();
    
    private ScrollPane rowsScrollPane = new ScrollPane();
    
    private Pane rowsPane = new Pane();
    
    private GridBagLayout rowsLayout = new GridBagLayout();
    
    private Pane bottomSpacer = new Pane();

    @Override
    public JPanel getInnerInstance() {
        return (JPanel) super.getInnerInstance();
    }
    
    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        this.headerPane.setDisabled(isDisabled);
        if (isDisabled) {
            for (Pane pane: this.rowControlPanes) {
                this.makeInactiveVisible(pane);
            }
        } else {
            for (Pane pane: this.rowControlPanes) {
                R rowInstance = pane.getInstanceForUseLater();
                if (rowInstance != null && rowInstance.equals(this.activeRowInstance)) {
                    this.makeActiveVisible(pane);
                }
            }
        }
    }
    
    private int defaultRowHeight = 0;
    
    /**
     * このテーブルのデフォルトの行の高さをセットする。
     * 
     * @param defaultRowHeight
     */
    public void setDefaultRowHeight(int defaultRowHeight) {
        this.defaultRowHeight = defaultRowHeight;
    }
    
    /**
     * このテーブルの行の高さをフォントサイズを元に調整する。
     */
    private void adjustRowHeight() {
        this.defaultRowHeight = this.getFont().getSize() * 3;
    }

    private int fixedRowHeight = -1;
    
    /**
     * このテーブルの行情報を表示するペインの高さを調整する。
     */
    private void adjustRowsPaneHeight() {
        int height = this.fixedRowHeight * (this.rowInstances.size() + 1);
        this.rowsPane.setHeight(height);
        this.rowsPane.setMinimumHeight(height);
    }
    
    /**
     * このテーブルの行情報を表示するペインのスペースレイアウトを更新する。
     */
    private void updateBottomSpacerLayout() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = this.rowIndex;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        this.rowsLayout.setConstraints(this.bottomSpacer.getInnerInstance(), constraints);
    }
    
    @Override
    public void updateLayout() {
        this.rowsPane.getChildren().clear();
        this.rowsPane.getChildren().add(this.bottomSpacer);
        this.updateBottomSpacerLayout();
        super.updateLayout();
        Integer leadingIndex = this.displayedLeadingIndex;
        if (leadingIndex == null) {
            leadingIndex = 0;
        }
        this.displayedLeadingIndex = null;
        this.displayRowControls(leadingIndex);
        this.headerPane.updateLayout();
        this.rowsPane.updateLayout();
        this.adjustRowsPaneHeight();
    }
    
    @Override
    public void updateDisplay() {
        this.rowsPane.getChildren().clear();
        this.rowsPane.getChildren().add(this.bottomSpacer);
        this.updateBottomSpacerLayout();
        super.updateDisplay();
        Integer leadingIndex = this.displayedLeadingIndex;
        if (leadingIndex == null) {
            leadingIndex = 0;
        }
        this.displayedLeadingIndex = null;
        this.displayRowControls(leadingIndex);
        this.headerPane.updateDisplay();
        this.rowsPane.updateDisplay();
        this.adjustRowsPaneHeight();
    }
    
    private R activeRowInstance = null;
    
    /**
     * このテーブルのアクティブな行情報のインスタンスを取得する。
     * 
     * @return
     */
    public R getActiveRowInstance() {
        if (this.rowInstances.contains(this.activeRowInstance)) {
            return this.activeRowInstance;
        }
        return null;
    }
    
    private List<ChangeListener<R>> activeRowChangeListeners = new ArrayList<>();
    
    /**
     * このテーブルのアクティブな行が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addActiveRowChangeListener(ChangeListener<R> changeListener) {
        this.activeRowChangeListeners.add(changeListener);
    }
    
    /**
     * このテーブルの指定された行をアクティブにする。
     * 
     * @param rowInstance
     */
    public void activateRow(R rowInstance) {
        R previousRowInstance = this.activeRowInstance;
        this.activeRowInstance = rowInstance;
        for (ChangeListener<R> changeListener: this.activeRowChangeListeners) {
            if (changeListener != null) {
                changeListener.executeWhenChanged(this, rowInstance);
            }
        }
        Pane previousPane = this.mapRowControlPanes.get(previousRowInstance);
        if (previousPane != null) {
            this.makeInactiveVisible(previousPane);
        }
        Pane activePane = this.mapRowControlPanes.get(rowInstance);
        if (activePane != null) {
            this.makeActiveVisible(activePane);
        }
    }
    
    /**
     * このテーブルの指定された行のペインがアクティブに見えるようにする。
     * 
     * @param pane
     */
    private void makeActiveVisible(Pane pane) {
        if (this.isDisabled()) {
            return;
        }
        pane.setBackgroundColor(this.activatedBackgroundColor);
        Map<C, Control> mapRowControls = this.mapOfRowControlMap.get(pane);
        for (C columnInstance: this.columnInstances) {
            Control control = mapRowControls.get(columnInstance);
            control.setDisabled(false);
            if (control instanceof Label) {
                Color defaultLabelTextColor = this.mapDefaultLabelColor.get(control);
                if (defaultLabelTextColor.getRGB() == this.labelForegroundColor.getRGB()) {
                    control.setForegroundColor(this.activatedForegroundColor);
                }
            }
            if (control instanceof DatePicker) {
                DatePicker datePicker = (DatePicker) control;
                if (columnInstance != this.activeColumnInstance) {
                    datePicker.setDisabledPopup(false);
                }
            }
            if (control instanceof AutocompleteTextField) {
                AutocompleteTextField autocompleteTextField = (AutocompleteTextField) control;
                if (columnInstance != this.activeColumnInstance) {
                    autocompleteTextField.setDisabledAutocomplete(false);
                }
            }
        }
        if (this.activeColumnInstance == null || mapRowControls.containsKey(this.activeColumnInstance) == false) {
            return;
        }
        mapRowControls.get(this.activeColumnInstance).requestFocus();
    }
    
    /**
     * このテーブルの指定された行のペインが非アクティブに見えるようにする。
     * 
     * @param pane
     */
    private void makeInactiveVisible(Pane pane) {
        pane.setBackgroundColor(this.getBackgroundColor());
        for (Control control: pane.getChildren()) {
            control.setDisabled(true);
            if (control instanceof Label) {
                Color defaultLabelTextColor = this.mapDefaultLabelColor.get(control);
                if (defaultLabelTextColor.getRGB() == this.labelForegroundColor.getRGB()) {
                    control.setForegroundColor(this.mapDefaultLabelColor.get(control));
                }
            }
        }
    }
    
    private C activeColumnInstance = null;
    
    /**
     * このテーブルのアクティブなカラム情報のインスタンスを取得する。
     * 
     * @return
     */
    public C getActiveColumnInstance() {
        if (this.columnInstances.contains(this.activeColumnInstance)) {
            return this.activeColumnInstance;
        }
        return null;
    }
    
    private List<ChangeListener<C>> activeColumnChangeListeners = new ArrayList<>();
    
    /**
     * このテーブルのアクティブなカラムが変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addActiveColumnChangeListener(ChangeListener<C> changeListener) {
        this.activeColumnChangeListeners.add(changeListener);
    }
    
    /**
     * このテーブルの指定された列をアクティブにする。
     * 
     * @param columnInstance
     */
    public void activateColumn(C columnInstance) {
        this.activeColumnInstance = columnInstance;
        for (ChangeListener<C> changeListener: this.activeColumnChangeListeners) {
            if (changeListener != null) {
                changeListener.executeWhenChanged(this, columnInstance);
            }
        }
    }
    
    /**
     * このテーブルの指定された行、指定されたカラムをアクティブにする。
     * 
     * @param rowInstance
     * @param columnInstance
     */
    public final void activate(R rowInstance, C columnInstance) {
        this.activateColumn(columnInstance);
        this.activateRow(rowInstance);
    }
    
    /**
     * このテーブルに指定された行情報のインスタンスに対応するコントロールが表示領域内に表示されている場合はtrueを返す。
     * 
     * @param rowInstance
     * @return
     */
    public boolean isDisplayRow(R rowInstance) {
        Pane pane = this.mapRowControlPanes.get(rowInstance);
        if (pane == null) {
            return false;
        }
        return this.rowsScrollPane.isDisplayEntireControl(pane);
    }
    
    /**
     * このテーブルの指定された行情報と列情報のインスタンスに対応したコントロールを取得する。<br>
     * 対応したコントロールが表示領域内ではない場合はnullを返す。
     * 
     * @param <T> コントロールの型。
     * @param rowInstance
     * @param columnInstance
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Control> T findControl(R rowInstance, C columnInstance) {
        Pane pane = this.mapRowControlPanes.get(rowInstance);
        if (pane == null) {
            return null;
        }
        Map<C, Control> mapRowControl = this.mapOfRowControlMap.get(pane);
        if (mapRowControl == null) {
            return null;
        }
        Control control = mapRowControl.get(columnInstance);
        while (control instanceof Pane) {
            Pane paneOfControl = (Pane) control;
            control = paneOfControl.getChildren().get(0);
        }
        return (T) control;
    }
    
    /**
     * 指定されたコントロールの行情報のインスタンスを取得する。失敗した場合はnullを返す。
     * 
     * @param control
     * @return
     */
    public R findRow(Control control) {
        for (Pane pane: this.mapOfRowControlMap.keySet()) {
            Map<C, Control> mapRowControl = this.mapOfRowControlMap.get(pane);
            if (mapRowControl.containsValue(control)) {
                return pane.getInstanceForUseLater();
            }
        }
        return null;
    }
    
    /**
     * このテーブルの行情報のインスタンスから値を抽出する。
     * 
     * @param row
     * @param columnInstance
     * @return
     */
    protected abstract Object getValueFromRow(R row, C columnInstance);
    
    /**
     * このテーブルの行情報のインスタンスに値をセットする。
     * 
     * @param row
     * @param columnInstance
     * @param value
     */
    protected abstract void setValueToRow(R row, C columnInstance, Object value);
    
    private int defaultColumnWidth = 100;
    
    /**
     * このテーブルのデフォルトのカラム幅を取得する。
     * 
     * @return
     */
    public int getDefaultColumnWidth() {
        return this.defaultColumnWidth;
    }
    
    /**
     * このテーブルのデフォルトのカラム幅をセットする。初期値は100。
     * 
     * @param width
     */
    public void setDefaultColumnWidth(int width) {
        this.defaultColumnWidth = width;
    }
    
    private Collection<C> columnInstances = new Collection<>();
    
    /**
     * このテーブルのカラム情報のインスタンスをすべて取得する。
     * 
     * @return
     */
    public Array<C> getColumnInstances() {
        return this.columnInstances.toArray();
    }
    
    private Map<C, ColumnType> mapColumnTypes = new HashMap<>();
    
    private Map<C, ControlFactory<C, R, Control>> mapControlFactories = new HashMap<>();
    
    private int columnIndex = 0;
    
    private Pane headerSpacer = new Pane();
    
    /**
     * このテーブルに配置されているコントロールの幅をセットする。
     * 
     * @param control
     * @param width
     */
    private void setControlWidth(Control control, int width) {
        control.setMinimumWidth(width);
        control.setWidth(width);
    }
    
    /**
     * このテーブルにカラムを追加する。
     * 
     * @param <T> 追加するカラムのコントロール型。
     * @param columnInstance
     * @param columnType
     * @param controlFactory
     */
    @SuppressWarnings("unchecked")
    protected <T extends Control> void addColumn(C columnInstance, ColumnType columnType, ControlFactory<C, R, T> controlFactory) {
        EditableTable<C, R> editableTable = this;
        // Fix row height
        if (this.fixedRowHeight == -1) {
            this.fixedRowHeight = this.defaultRowHeight;
        }
        // Clear rows
        this.rowInstances.clear();
        this.mapRowSpacers.clear();
        this.rowIndex = 0;
        // Header label
        Label label = new Label(columnInstance.toString());
        int headerHeight = this.getFont().getSize() * 3;
        label.setSize(this.defaultColumnWidth, headerHeight);
        label.setMaximumHeight(headerHeight);
        label.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
        label.setFont(this.getFont());
        if (this.columnInstances.size() > 0) {
            label.setBorder(Border.createLine(this.borderColor, 0, 0, 0, 1));
        }
        label.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                for (Map<C, Control> controls: editableTable.mapOfRowControlMap.values()) {
                    Control control = controls.get(columnInstance);
                    editableTable.setControlWidth(control, changedValue.getIntegerWidth());
                    control.getInnerInstanceForLayout().doLayout();
                    control.getParent().updateLayout();
                }
            }
        });
        label.addMouseMovedEventHandler(this.headerLabelMouseMovedEventHandler);
        label.addMousePressedEventHandler(MouseButton.BUTTON1, this.headerLabelMousePressedEventHandler);
        label.addMouseDraggedEventHandler(this.headerLabelMouseDraggedEventHandler);
        this.headerPane.getChildren().add(label);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = this.columnIndex;
        constraints.gridy = 0;
        constraints.weightx = 0;
        this.headerLayout.setConstraints(label.getInnerInstance(), constraints);
        // Keep instance
        this.columnInstances.add(columnInstance);
        this.mapColumnTypes.put(columnInstance, columnType);
        this.mapControlFactories.put(columnInstance, (ControlFactory<C, R, Control>) controlFactory);
        TableColumn tableColumn = new TableColumn(columnInstance, columnType, label);
        this.mapTableColumns.put(columnInstance, tableColumn);
        this.mapHeaderLabels.put(label, tableColumn);
        this.columnIndex++;
        // Header spacer for left align
        GridBagConstraints spacerConstraints = new GridBagConstraints();
        spacerConstraints.gridx = this.columnIndex;
        spacerConstraints.gridy = 0;
        spacerConstraints.weightx = 1;
        this.headerLayout.setConstraints(this.headerSpacer.getInnerInstance(), spacerConstraints);
    }
    
    private Map<C, TableColumn> mapTableColumns = new HashMap<>();
    
    private Map<Label, TableColumn> mapHeaderLabels = new HashMap<>();
    
    /**
     * このテーブルに追加されているカラムを識別するインスタンスから、テーブルカラムを取得する。
     * 
     * @param columnInstance
     * @return
     */
    public TableColumn getTableColumn(C columnInstance) {
        return this.mapTableColumns.get(columnInstance);
    }
    
    /**
     * ヘッダーラベルの端でカラムをリサイズするイベントハンドラー。
     */
    private EventHandler<MouseEvent> headerLabelMouseMovedEventHandler = new EventHandler<MouseEvent>() {
        
        @Override
        protected void handle(MouseEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            TableColumn tableColumn = editableTable.mapHeaderLabels.get(event.getSource());
            if (tableColumn.isResizable() == false) {
                return;
            }
            if (event.getX() > event.getSource().getWidth() - 7) {
                event.getSource().setMouseCursor(MouseCursor.RESIZE_EAST);
            } else {
                event.getSource().setMouseCursor(MouseCursor.DEFAULT);
            }
        }
    };
    
    private boolean isHeaderLabelResizeStarted = false;
    
    private int headerPaneWidthBeforeResizing;
    
    private int headerLabelWidthBeforeResizing;
    
    private int headerLabelResizeStartPointX;
    
    /**
     * ヘッダーラベルの端でカラムをリサイズするイベントハンドラー。
     */
    private EventHandler<MouseEvent> headerLabelMousePressedEventHandler = new EventHandler<MouseEvent>() {
        
        @Override
        protected void handle(MouseEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            editableTable.isHeaderLabelResizeStarted = false;
            if (event.getX() > event.getSource().getWidth() - 10) {
                editableTable.isHeaderLabelResizeStarted = true;
                editableTable.headerPaneWidthBeforeResizing = editableTable.headerPane.getWidth();
                editableTable.headerLabelWidthBeforeResizing = event.getSource().getWidth();
                editableTable.headerLabelResizeStartPointX = event.getScreenX();
            }
        }
    };
    
    /**
     * ヘッダーラベルの端でカラムをリサイズするイベントハンドラー。
     */
    private EventHandler<MouseEvent> headerLabelMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        
        @Override
        protected void handle(MouseEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            TableColumn tableColumn = editableTable.mapHeaderLabels.get(event.getSource());
            int labelWidth = editableTable.headerLabelWidthBeforeResizing - editableTable.headerLabelResizeStartPointX + event.getScreenX();
            if (tableColumn.isResizable() == false || editableTable.isHeaderLabelResizeStarted == false || labelWidth <= tableColumn.getMinimumWidth() || labelWidth >= tableColumn.getMaximumWidth()) {
                return;
            }
            editableTable.headerPane.setWidth(editableTable.headerPaneWidthBeforeResizing - editableTable.headerLabelResizeStartPointX + event.getScreenX());
            event.getSource().setWidth(labelWidth);
            editableTable.headerPane.getInnerInstance().doLayout();
            editableTable.rowsScrollPane.getInnerInstanceForLayout().doLayout();
        }
    };
    
    /**
     * このテーブルにラベルを表示するカラムを追加する。
     * 
     * @param columnInstance
     * @param controlFactory
     */
    public final void addColumnOfLabel(C columnInstance, ReadonlyControlFactory<C, R, Label> controlFactory) {
        this.addColumn(columnInstance, ColumnType.LABEL, controlFactory);
    }
    
    /**
     * このテーブルにテキストフィールドを表示するカラムを追加する。
     * 
     * @param columnInstance
     * @param controlFactory
     */
    public final void addColumnOfTextField(C columnInstance, ControlFactory<C, R, TextField> controlFactory) {
        this.addColumn(columnInstance, ColumnType.TEXT_FIELD, controlFactory);
    }
    
    /**
     * このテーブルにパスワードフィールドを表示するカラムを追加する。
     * 
     * @param columnInstance
     * @param controlFactory
     */
    public final void addColumnOfPasswordField(C columnInstance, ControlFactory<C, R, PasswordField> controlFactory) {
        this.addColumn(columnInstance, ColumnType.PASSWORD_FIELD, controlFactory);
    }
    
    /**
     * このテーブルに日付の入力に特化したテキストフィールドを表示するカラムを追加する。
     * 
     * @param columnInstance
     * @param controlFactory
     */
    public final void addColumnOfDatePicker(C columnInstance, ControlFactory<C, R, DatePicker> controlFactory) {
        this.addColumn(columnInstance, ColumnType.DATE_PICKER, controlFactory);
    }
    
    /**
     * このテーブルにドロップダウンリストを表示するカラムを追加する。
     * 
     * @param <T> 
     * @param columnInstance
     * @param controlFactory
     */
    public final <T> void addColumnOfDropDownList(C columnInstance, ControlFactory<C, R, DropDownList<T>> controlFactory) {
        this.addColumn(columnInstance, ColumnType.DROPDOWNLIST, controlFactory);
    }
    
    /**
     * このテーブルにチェックボックスを表示するカラムを追加する。
     * 
     * @param columnInstance
     * @param controlFactory
     */
    public final void addColumnOfCheckBox(C columnInstance, ControlFactory<C, R, CheckBox> controlFactory) {
        this.addColumn(columnInstance, ColumnType.CHECKBOX, controlFactory);
    }
    
    /**
     * このテーブルにボタンを表示するカラムを追加する。
     * 
     * @param columnInstance
     * @param controlFactory
     */
    public final void addColumnOfButton(C columnInstance, FixedControlFactory<C, R, Button> controlFactory) {
        this.addColumn(columnInstance, ColumnType.BUTTON, controlFactory);
    }
    
    private Collection<R> rowInstances = new Collection<>();
    
    /**
     * このテーブルの行情報のインスタンスを格納しているコレクションを取得する。
     * 
     * @return
     */
    public Collection<R> getRowInstances() {
        return this.rowInstances;
    }
    
    private int rowIndex = 0;
    
    private Map<R, Pane> mapRowSpacers = new HashMap<>();
    
    /**
     * このテーブルの行情報のインスタンスが追加された際の処理。
     * 
     * @param rowInstance
     */
    private void addRow(R rowInstance) {
        Pane spacer = new Pane();
        spacer.setSize(0, this.fixedRowHeight);
        spacer.setMinimumHeight(this.fixedRowHeight);
        spacer.setInstanceForUseLater(rowInstance);
        this.rowsPane.getChildren().add(spacer);
        this.mapRowSpacers.put(rowInstance, spacer);
        this.configureRowToEmpty(rowInstance, this.rowIndex);
        this.rowIndex++;
    }
    
    /**
     * このテーブルの行情報のインスタンスが削除された際の処理。
     * 
     * @param rowInstance
     */
    private void removeRow(R rowInstance) {
        if (this.mapRowSpacers.containsKey(rowInstance) == false) {
            return;
        }
        this.rowsPane.getChildren().remove(this.mapRowSpacers.get(rowInstance));
        this.mapRowSpacers.remove(rowInstance);
    }
    
    /**
     * 指定された行情報のインスタンスを空の行で表示する。
     * 
     * @param rowInstance
     * @param rowIndex
     */
    private void configureRowToEmpty(R rowInstance, int rowIndex) {
        if (this.mapRowControlPanes.containsKey(rowInstance) && this.activeColumnInstance != null) {
            Pane controlPane = this.mapRowControlPanes.get(rowInstance);
            Map<C, Control> mapRowControls = this.mapOfRowControlMap.get(controlPane);
            Control control = mapRowControls.get(this.activeColumnInstance);
            ControlFactory<C, R, Control> controlFactory = this.mapControlFactories.get(this.activeColumnInstance);
            controlFactory.setValueToRowInstance(control, rowInstance, this.activeColumnInstance);
        }
        if (this.mapRowSpacers.containsKey(rowInstance)) {
            Pane spacer = this.mapRowSpacers.get(rowInstance);
            if (this.rowsPane.getChildren().contains(spacer) == false) {
                this.rowsPane.getChildren().add(spacer);
            }
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = rowIndex;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.rowsLayout.setConstraints(spacer.getInnerInstance(), constraints);
        }
    }
    
    /**
     * このテーブルの指定された行情報インスタンスとカラム情報インスタンスのコントロール値を更新する。
     * 
     * @param rowInstance
     * @param columnInstances
     */
    public void updateControlValues(R rowInstance, C[] columnInstances) {
        try {
            for (C columnInstance: columnInstances) {
                Control control = this.findControl(rowInstance, columnInstance);
                if (control != null) {
                    this.mapControlFactories.get(columnInstance).setValueToControl(rowInstance, columnInstance, control);
                }
            }
        } catch (Exception exception) {
        }
    }

    /**
     * このテーブルの指定された行情報インスタンスとカラム情報インスタンスのコントロール値を更新する。
     * 
     * @param rowInstance
     * @param columnInstances
     */
    @SuppressWarnings("unchecked")
    public void updateControlValues(R rowInstance, List<C> columnInstances) {
        this.updateControlValues(rowInstance, (C[]) columnInstances.toArray());
    }
    
    /**
     * このテーブルの指定された行情報インスタンスとカラム情報インスタンスのコントロール値を更新する。
     * 
     * @param rowInstance
     * @param columnInstance
     */
    @SuppressWarnings("unchecked")
    public void updateControlValue(R rowInstance, C columnInstance) {
        this.updateControlValues(rowInstance, (C[]) new Object[] {columnInstance});
    }

    /**
     * このテーブルの指定された行情報インスタンスのコントロール値を更新する。
     * 
     * @param rowInstance
     */
    public void updateControlValues(R rowInstance) {
        this.updateControlValues(rowInstance, this.columnInstances.toUnmodifiableList());
    }
    
    private Map<R, Pane> mapRowControlPanes = new HashMap<>();
    
    private Integer displayedLeadingIndex = null;

    private Map<R, Map<C, Integer[]>> mapTextFieldSelection = new HashMap<>();
    
    /**
     * このテーブルの指定された行インデックスのコントロールを表示する。
     * 
     * @param leadingIndex
     */
    public void displayRowControls(int leadingIndex) {
        // If same position as last time || If there is no column
        if (this.displayedLeadingIndex != null && this.displayedLeadingIndex == leadingIndex || this.columnInstances.size() == 0 || this.rowInstances.size() == 0) {
            return;
        }
        // Create controls
        while (this.rowInstances.size() > this.rowControlPanes.size() && this.rowControlPanes.size() < this.numberOfDisplayRows) {
            this.createOneRowControls();
        }
        // Start and End indices
        int startDisplayIndex = leadingIndex;
        int endDisplayIndex = leadingIndex;
        if (leadingIndex > this.rowInstances.size() - 1) {
            endDisplayIndex = this.rowInstances.size() - 1;
        }
        int numberOfRows = 1;
        while (numberOfRows < this.numberOfDisplayRows) {
            if (startDisplayIndex > 0 && startDisplayIndex > leadingIndex - 1) {
                startDisplayIndex--;
            }
            numberOfRows = endDisplayIndex - startDisplayIndex + 1;
            if (numberOfRows >= this.numberOfDisplayRows) {
                break;
            }
            if (endDisplayIndex < this.rowInstances.size() - 1) {
                endDisplayIndex++;
            } else {
                break;
            }
            numberOfRows = endDisplayIndex - startDisplayIndex + 1;
        }
        // Display controls
        for (int index = 0; index < this.rowInstances.size(); index++) {
            R rowInstance = this.rowInstances.get(index);
            this.configureRowToEmpty(rowInstance, index);
        }
        this.mapRowControlPanes.clear();
        for (int index = startDisplayIndex; index <= endDisplayIndex; index++) {
            R rowInstance = this.rowInstances.get(index);
            Pane spacer = this.mapRowSpacers.get(rowInstance);
            if (this.rowsPane.getChildren().contains(spacer)) {
                this.rowsPane.getChildren().remove(spacer);
            }
            Pane controlPane = this.rowControlPanes.get(index - startDisplayIndex);
            this.mapRowControlPanes.put(rowInstance, controlPane);
            if (this.rowsPane.getChildren().contains(controlPane) == false) {
                this.rowsPane.getChildren().add(controlPane);
            }
            if (this.activeRowInstance == rowInstance) {
                this.makeActiveVisible(controlPane);
            } else {
                this.makeInactiveVisible(controlPane);
            }
            controlPane.setInstanceForUseLater(rowInstance);
            Map<C, Control> mapRowControl = this.mapOfRowControlMap.get(controlPane);
            Map<C, Integer[]> selectionOfTextField = this.mapTextFieldSelection.get(rowInstance);
            for (C columnInstance: this.columnInstances) {
                Control control = mapRowControl.get(columnInstance);
                this.mapControlFactories.get(columnInstance).setValueToControl(rowInstance, columnInstance, control);
                if (control instanceof TextField) {
                    TextField textField = (TextField) control;
                    try {
                        textField.select(selectionOfTextField.get(columnInstance)[0], selectionOfTextField.get(columnInstance)[1]);
                    } catch (Exception exception) {
                    }
                }
            }
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = index;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.rowsLayout.setConstraints(controlPane.getInnerInstance(), constraints);
        }
        this.displayedLeadingIndex = leadingIndex;
        this.rowsPane.getInnerInstance().doLayout();
    }
    
    private int numberOfDisplayRows = 30;
    
    /**
     * このテーブルのコントロールを表示できる行数を取得する。
     * 
     * @return
     */
    public int getNumberOfDisplayRows() {
        return this.numberOfDisplayRows;
    }
    
    /**
     * このテーブルのコントロールを表示できる行数を指定する。
     * 
     * @param numberOfDisplayRows
     */
    public void setNumberOfDisplayRows(int numberOfDisplayRows) {
        this.numberOfDisplayRows = numberOfDisplayRows;
    }
    
    private Map<Pane, Map<C, Control>> mapOfRowControlMap = new HashMap<>();
    
    private List<Pane> rowControlPanes = new ArrayList<>();
    
    private Map<Label, Color> mapDefaultLabelColor = new HashMap<>();
    
    /**
     * 1行分のコントロールを作成する。
     */
    private void createOneRowControls() {
        EditableTable<C, R> editableTable = this;
        Pane pane = new Pane();
        pane.setMinimumHeight(this.fixedRowHeight);
        pane.setBorder(Border.createLine(this.lightBorderColor, 0, 0, 1, 0));
        pane.addMouseClickedEventHandler(MouseButton.BUTTON1, new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                editableTable.activateRow(pane.getInstanceForUseLater());
            }
        });
        GridBagLayout layout = new GridBagLayout();
        pane.getInnerInstance().setLayout(layout);
        Map<C, Control> mapControls = new HashMap<>();
        for (C columnInstance: this.columnInstances) {
            TableColumn tableColumn = this.mapTableColumns.get(columnInstance);
            ControlFactory<C, R, Control> controlFactory = this.mapControlFactories.get(columnInstance);
            Control control = controlFactory.newInstance(columnInstance);
            control.addFocusChangeListener(new ControlFocusChangeListener(columnInstance));
            control.addKeyPressedEventHandler(new ControlKeyPressedEventHandler(pane, columnInstance));
            control.getInnerInstance().addMouseListener(new ControlClickEventHandler(pane, columnInstance));
            control.getInnerInstance().setFocusTraversalKeysEnabled(false);
            ColumnType columnType = this.mapColumnTypes.get(columnInstance);
            switch (columnType) {
            case LABEL:
                Label label = (Label) control;
                this.mapDefaultLabelColor.put(label, label.getForegroundColor());
                break;
            case TEXT_FIELD:
            case PASSWORD_FIELD:
                TextField textField = (TextField) control;
                textField.addTextChangeListener(new ChangeListener<String>() {
                    
                    @Override
                    protected void changed(Component<?> component, String changedValue, String previousValue) {
                        Control control = (Control) component;
                        if (control.isFocused()) {
                            R rowInstance = pane.getInstanceForUseLater();
                            controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
                        }
                    }
                });
                break;
            case DATE_PICKER:
                DatePicker datePicker = (DatePicker) control;
                datePicker.setDisabledPopup(true);
                datePicker.addMouseClickedEventHandler(new EventHandler<MouseEvent>() {
                    
                    @Override
                    protected void handle(MouseEvent event) {
                        datePicker.setDisabledPopup(false);
                        datePicker.showPopup();
                    }
                });
                datePicker.addDateChangeListener(new ChangeListener<Date>() {

                    @Override
                    protected void changed(Component<?> component, Date changedValue, Date previousValue) {
                        Control control = (Control) component;
                        if (control.isFocused()) {
                            R rowInstance = pane.getInstanceForUseLater();
                            controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
                        }
                    }
                });
                break;
            case DROPDOWNLIST:
                @SuppressWarnings("unchecked")
                DropDownList<Object> dropDownList = (DropDownList<Object>) control;
                dropDownList.addSelectedItemChangeListener(new ChangeListener<Object>() {
                    
                    @Override
                    protected void changed(Component<?> component, Object changedValue, Object previousValue) {
                        Control control = (Control) component;
                        if (control.isFocused()) {
                            R rowInstance = pane.getInstanceForUseLater();
                            controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
                        }
                    }
                });
                break;
            case CHECKBOX:
                CheckBox checkBox = (CheckBox) control;
                checkBox.setBackgroundColor(null);
                checkBox.addMarkChangeListener(new ChangeListener<Boolean>() {
                    
                    @Override
                    protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
                        Control control = (Control) component;
                        if (control.isFocused()) {
                            R rowInstance = pane.getInstanceForUseLater();
                            controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
                        }
                    }
                });
                break;
            case BUTTON:
                break;
            }
            if (control.getWidth() < tableColumn.getWidth()) {
                this.setControlWidth(control, tableColumn.getWidth());
            }
            pane.getChildren().add(control);
            mapControls.put(columnInstance, control);
        }
        this.rowControlPanes.add(pane);
        this.mapOfRowControlMap.put(pane, mapControls);
        Pane spacer = new Pane();
        spacer.setBackgroundColor(null);
        spacer.setSize(0, this.fixedRowHeight);
        pane.getChildren().add(spacer);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = this.columnInstances.size();
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(spacer.getInnerInstance(), constraints);
    }

    /**
     * このテーブルの指定された行情報のインスタンス、カラム情報のインスタンスにスクロールする。
     * 
     * @param rowInstance
     * @param columnInstance
     */
    public void scrollTo(R rowInstance, C columnInstance) {
        EditableTable<C, R> editableTable = this;
        int index;
        if (this.rowInstances.size() - this.rowInstances.indexOf(rowInstance) + 1 < this.numberOfDisplayRows) {
            index = this.rowInstances.size() - this.numberOfDisplayRows + 2;
        } else {
            index = this.rowInstances.indexOf(rowInstance) - this.numberOfDisplayRows + 2;
        }
        if (index < 0) {
            index = 0;
        }
        this.displayRowControls(index);
        GUI.executeLater(new Runnable() {
            
            @Override
            public void run() {
                if (editableTable.mapRowControlPanes.containsKey(rowInstance)) {
                    Pane rowControlPane = editableTable.mapRowControlPanes.get(rowInstance);
                    if (columnInstance == null) {
                        editableTable.rowsScrollPane.scrollTo(rowControlPane);
                    } else {
                        Map<C, Control> mapControl = editableTable.mapOfRowControlMap.get(rowControlPane);
                        editableTable.rowsScrollPane.scrollTo(mapControl.get(columnInstance));
                    }
                }
            }
        });
    }
    
    /**
     * このテーブルの指定された行情報のインスタンスにスクロールする。
     * 
     * @param rowInstance
     */
    public void scrollTo(R rowInstance) {
        this.scrollTo(rowInstance, null);
    }
    
    /**
     * このテーブルの行に配置されたコントロールのフォーカスが変更された場合のリスナー。
     */
    private class ControlFocusChangeListener extends ChangeListener<Boolean> {
        
        /**
         * コンストラクタ。<br>
         * コントロールが属するカラムのインスタンスを指定する。
         * 
         * @param columnInstance
         */
        private ControlFocusChangeListener(C columnInstance) {
            this.columnInstance = columnInstance;
        }
        
        private C columnInstance;
        
        @Override
        protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
            EditableTable<C, R> editableTable = EditableTable.this;
            if (changedValue) {
                editableTable.activateColumn(this.columnInstance);
            }
        }
    }
    
    /**
     * このテーブルの行に配置されたコントロールがクリックされた際のイベントハンドラー。
     */
    private class ControlClickEventHandler extends MouseAdapter {
        
        /**
         * コンストラクタ。<br>
         * コントロールが配置されているペインと、属するカラムのインスタンスを指定する。
         * 
         * @param rowControlPane
         * @param columnInstance
         */
        private ControlClickEventHandler(Pane rowControlPane, C columnInstance) {
            this.controlPane = rowControlPane;
            this.columnInstance = columnInstance;
        }
        
        private Pane controlPane;
        
        private C columnInstance;
        
        @Override
        public void mouseClicked(java.awt.event.MouseEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            java.awt.Component component = (java.awt.Component) event.getSource();
            if (component.isEnabled() == false) {
                R rowInstance = this.controlPane.getInstanceForUseLater();
                editableTable.activateColumn(this.columnInstance);
                editableTable.activateRow(rowInstance);
            }
        }
    }
    
    /**
     * このテーブルの行に配置されたコントロールでキーが押された際のイベントハンドラー。
     */
    private class ControlKeyPressedEventHandler extends EventHandler<KeyEvent> {
        
        /**
         * コンストラクタ。<br>
         * コントロールが配置されているペインと、属するカラムのインスタンスを指定する。
         * 
         * @param rowControlPane
         * @param columnInstance
         */
        private ControlKeyPressedEventHandler(Pane rowControlPane, C columnInstance) {
            this.rowControlPane = rowControlPane;
            this.columnInstance = columnInstance;
        }
        
        private Pane rowControlPane;
        
        private C columnInstance;
        
        /**
         * 指定された行情報のインスタンス、カラム情報のインスタンスのコントロールにスクロールしてフォーカスする。
         * 
         * @param rowInstance 
         * @param columnInstance
         */
        private void scrollAndFocus(R rowInstance, C columnInstance) {
            GUI.executeLater(new Runnable() {
                
                @Override
                public void run() {
                    EditableTable<C, R> editableTable = EditableTable.this;
                    Pane rowControlPane = editableTable.mapRowControlPanes.get(rowInstance);
                    Map<C, Control> mapRowControls = editableTable.mapOfRowControlMap.get(rowControlPane);
                    Control control = mapRowControls.get(columnInstance);
                    editableTable.activate(rowInstance, columnInstance);
                    if (editableTable.rowsScrollPane.isDisplayEntireControl(control) == false) {
                        editableTable.scrollTo(rowInstance, columnInstance);
                    }
                }
            });
        }

        /**
         * 指定されたカラム情報のインスタンスのコントロールにスクロールしてフォーカスする。
         * 
         * @param columnInstance
         */
        private void scrollAndFocus(C columnInstance) {
            EditableTable<C, R> editableTable = EditableTable.this;
            this.scrollAndFocus(editableTable.activeRowInstance, columnInstance);
        }

        @Override
        protected void handle(KeyEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            switch (event.getKeyCode()) {
            case TAB:
                event.consume();
                R rowInstance = this.rowControlPane.getInstanceForUseLater();
                Map<C, Control> mapRowControls = editableTable.mapOfRowControlMap.get(this.rowControlPane);
                R destinationRowInstance = null;
                C destinationColumnInstance = null;
                if (event.isShiftDown() == false) {
                    // Next control
                    boolean isPass = false;
                    for (C columnInstance: editableTable.columnInstances) {
                        Control control = mapRowControls.get(columnInstance);
                        if (control.isFocusable()) {
                            if (isPass) {
                                this.scrollAndFocus(columnInstance);
                                return;
                            }
                            if (destinationColumnInstance == null) {
                                destinationColumnInstance = columnInstance;
                            }
                        }
                        if (columnInstance == this.columnInstance) {
                            isPass = true;
                        }
                    }
                    int rowInstanceIndex = editableTable.rowInstances.indexOf(rowInstance);
                    if (rowInstanceIndex < editableTable.rowInstances.size() - 1) {
                        destinationRowInstance = editableTable.rowInstances.get(rowInstanceIndex + 1);
                    } else {
                        event.getSource().getInnerInstance().transferFocus();
                    }
                } else {
                    // Previous control
                    for (C columnInstance: editableTable.columnInstances) {
                        Control control = mapRowControls.get(columnInstance);
                        if (control.isFocusable()) {
                            if (destinationColumnInstance != null && columnInstance == this.columnInstance) {
                                this.scrollAndFocus(destinationColumnInstance);
                                return;
                            }
                            destinationColumnInstance = columnInstance;
                        }
                    }
                    int rowInstanceIndex = editableTable.rowInstances.indexOf(rowInstance);
                    if (rowInstanceIndex > 0) {
                        destinationRowInstance = editableTable.rowInstances.get(rowInstanceIndex - 1);
                    } else {
                        event.getSource().getInnerInstance().transferFocusBackward();
                    }
                }
                // Another row
                if (destinationRowInstance != null) {
                    Pane destinationControlPane = editableTable.mapRowControlPanes.get(destinationRowInstance);
                    Control destinationControl = null;
                    Map<C, Control> mapDestinationRowControls = editableTable.mapOfRowControlMap.get(destinationControlPane);
                    if (mapDestinationRowControls != null) {
                        destinationControl = mapDestinationRowControls.get(destinationColumnInstance);
                    }
                    if (destinationControl == null) {
                        destinationControl = destinationControlPane;
                    }
                    this.scrollAndFocus(destinationRowInstance, destinationColumnInstance);
                }
                break;
            default:
                break;
            }
        }
    }
    
    /**
     * 行情報をスクロールした際にコントロールの表示を切り替えるリスナー。
     */
    private ChangeListener<Integer> scrollPositionChangeListener = new ChangeListener<Integer>() {
        
        @Override
        protected void changed(Component<?> component, Integer changedValue, Integer previousValue) {
            EditableTable<C, R> editableTable = EditableTable.this;
            for (Pane rowControlPane: editableTable.rowControlPanes) {
                Map<C, Control> mapControl = editableTable.mapOfRowControlMap.get(rowControlPane);
                for (C columnInstance: editableTable.columnInstances) {
                    Control control = mapControl.get(columnInstance);
                    if (control instanceof DatePicker) {
                        DatePicker datePicker = (DatePicker) control;
                        datePicker.hidePopup();
                        datePicker.setDisabledPopup(true);
                    }
                    if (control instanceof AutocompleteTextField) {
                        AutocompleteTextField autocompleteTextField = (AutocompleteTextField) control;
                        autocompleteTextField.hidePopup();
                        autocompleteTextField.setDisabledAutocomplete(true);
                    }
                }
            }
            R activeRowInstance = editableTable.getActiveRowInstance();
            C activeColumnInstance = editableTable.getActiveColumnInstance();
            Control activeControl = editableTable.findControl(activeRowInstance, activeColumnInstance);
            if (activeControl != null && activeControl instanceof TextField) {
                TextField textField = (TextField) activeControl;
                if (editableTable.mapTextFieldSelection.containsKey(activeRowInstance) == false) {
                    editableTable.mapTextFieldSelection.put(activeRowInstance, new HashMap<>());
                }
                Map<C, Integer[]> hashMap = editableTable.mapTextFieldSelection.get(activeRowInstance);
                hashMap.put(activeColumnInstance, new Integer[] {textField.getSelectionStart(), textField.getSelectionEnd()});
            }
            if (changedValue == editableTable.rowsScrollPane.getVerticalScrollBar().getMinimumScrollPosition()) {
                editableTable.displayRowControls(0);
                return;
            }
            if (changedValue == editableTable.rowsScrollPane.getVerticalScrollBar().getMaximumScrollPosition()) {
                editableTable.displayRowControls(editableTable.rowInstances.size() - editableTable.numberOfDisplayRows);
                return;
            }
            Control control = editableTable.rowsPane.getChildren().findControlByPoint(1, changedValue);
            int numberOfAttempts = 0;
            while (control == null && numberOfAttempts < 5) {
                control = editableTable.rowsPane.getChildren().findControlByPoint(1, changedValue + editableTable.fixedRowHeight / 5 * numberOfAttempts);
                numberOfAttempts++;
            }
            if (control == null) {
                return;
            }
            if (control instanceof Pane == false) {
                control = control.getParent();
            }
            if (control.getInstanceForUseLater() != null) {
                int index = editableTable.rowInstances.indexOf(control.getInstanceForUseLater());
                editableTable.displayRowControls(index);
            }
        }
    };
    
    /**
     * テーブルカラムのクラス。
     */
    public class TableColumn implements TableColumnInterface<C> {
        
        /**
         * コンストラクタ。<br>
         * テーブルカラムを識別するためのインスタンス、テーブルカラムのタイプ、ヘッダーラベルを指定する。
         * 
         * @param columnInstance
         * @param columnType
         * @param headerLabel
         */
        private TableColumn(C columnInstance, ColumnType columnType, Label headerLabel) {
            EditableTable<C, R> editableTable = EditableTable.this;
            this.columnInstance = columnInstance;
            this.columnType = columnType;
            this.headerLabel = headerLabel;
            headerLabel.setMinimumWidth(editableTable.getFont().getSize() * 2);
        }
        
        private C columnInstance;
        
        @Override
        public C getColumnInstance() {
            return this.columnInstance;
        }
        
        private ColumnType columnType;
        
        /**
         * このテーブルカラムのタイプを取得する。
         * 
         * @return
         */
        public ColumnType getColumnType() {
            return this.columnType;
        }
        
        private Label headerLabel;
        
        @Override
        public String getHeaderText() {
            return this.headerLabel.getText();
        }
        
        @Override
        public void setHeaderText(String headerText) {
            this.headerLabel.setText(headerText);
        }
        
        @Override
        public HorizontalAlignment getHeaderHorizontalAlignment() {
            return this.headerLabel.getTextHorizontalAlignment();
        }
        
        @Override
        public void setHeaderHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
            this.headerLabel.setTextHorizontalAlignment(horizontalAlignment);
        }
        
        @Override
        public Integer getWidth() {
            return this.headerLabel.getWidth();
        }
        
        @Override
        public void setWidth(Integer width) {
            this.headerLabel.setWidth(width);
        }
        
        @Override
        public Integer getMinimumWidth() {
            return this.headerLabel.getMinimumWidth();
        }
        
        @Override
        public void setMinimumWidth(Integer width) {
            this.headerLabel.setMinimumWidth(width);
        }
        
        @Override
        public Integer getMaximumWidth() {
            return this.headerLabel.getMaximumWidth();
        }
        
        @Override
        public void setMaximumWidth(Integer width) {
            this.headerLabel.setMaximumWidth(width);
        }
        
        private boolean isResizable = true;
        
        @Override
        public boolean isResizable() {
            return this.isResizable;
        }
        
        @Override
        public void setResizable(boolean isResizable) {
            this.isResizable = isResizable;
        }
        
        @Override
        public void addHeaderMouseClickedEventHandler(EventHandler<MouseEvent> eventHandler) {
            this.headerLabel.addMouseClickedEventHandler(eventHandler);
        }
        
        @Override
        public void addHeaderMouseClickedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
            this.headerLabel.addMouseClickedEventHandler(mouseButton, eventHandler);
        }
        
        @Override
        public void addHeaderMousePressedEventHandler(EventHandler<MouseEvent> eventHandler) {
            this.headerLabel.addMousePressedEventHandler(eventHandler);
        }
        
        @Override
        public void addHeaderMousePressedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
            this.headerLabel.addMouseClickedEventHandler(mouseButton, eventHandler);
        }
        
        @Override
        public void addHeaderMouseReleasedEventHandler(EventHandler<MouseEvent> eventHandler) {
            this.headerLabel.addMouseReleasedEventHandler(eventHandler);
        }
        
        @Override
        public void addHeaderMouseReleasedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
            this.headerLabel.addMouseReleasedEventHandler(mouseButton, eventHandler);
        }
        
        @Override
        public void addHeaderMouseWheelEventHandler(EventHandler<MouseEvent> eventHandler) {
            this.headerLabel.addMouseWheelEventHandler(eventHandler);
        }
        
        @Override
        public void removeEventHandler(EventHandler<?> eventHandler) {
            this.headerLabel.removeEventHandler(eventHandler);
        }
    }
    
    /**
     * カラム種類の列挙型。
     */
    public enum ColumnType {
        /**
         * ラベルのセルを表示するカラム。
         */
        LABEL,
        /**
         * テキストフィールドのセルを表示するカラム。
         */
        TEXT_FIELD,
        /**
         * パスワードフィールドのセルを表示するカラム。
         */
        PASSWORD_FIELD,
        /**
         * 日付の入力に特化したテキストフィールドを表示するカラム。
         */
        DATE_PICKER,
        /**
         * ドロップダウンリストのセルを表示するカラム。
         */
        DROPDOWNLIST,
        /**
         * チェックボックスのセルを表示するカラム。
         */
        CHECKBOX,
        /**
         * ボタンのセルを表示するカラム。
         */
        BUTTON,
    }
}
