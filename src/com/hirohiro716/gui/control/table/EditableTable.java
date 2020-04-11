package com.hirohiro716.gui.control.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.UIManager;
import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.MouseCursor;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.control.AnchorPane;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.CheckBox;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.DatePicker;
import com.hirohiro716.gui.control.DropDownList;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.control.PasswordField;
import com.hirohiro716.gui.control.ScrollPane;
import com.hirohiro716.gui.control.ScrollPane.ScrollBarDisplayPolicy;
import com.hirohiro716.gui.control.TextField;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.KeyEvent;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * 編集に特化したテーブルのクラス。
 * 
 * @author hiro
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
        this.root = Pane.newInstance(this.getInnerInstance());
        this.root.setParent(this);
        GridBagLayout layout = new GridBagLayout();
        this.root.getInnerInstance().setLayout(layout);
        this.root.setBorder(Border.createLine(this.borderColor, 1));
        this.root.addSizeChangeListener(new ChangeListener<>() {

            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                editableTable.updateDisplay();
            }
        });
        // Header pane
        this.headerPane.getInnerInstance().setLayout(this.headerLayout);
        this.headerSpacer.setBackgroundColor(null);
        this.headerPane.getChildren().add(this.headerSpacer);
        ScrollPane headerScrollPane = new ScrollPane(this.headerPane);
        headerScrollPane.setHorizontalScrollBarDisplayPolicy(ScrollBarDisplayPolicy.NEVER);
        headerScrollPane.setVerticalScrollBarDisplayPolicy(ScrollBarDisplayPolicy.NEVER);
        headerScrollPane.setBorder(Border.createLine(this.borderColor, 0, 0, 1, 0));
        headerScrollPane.getInnerInstance().setWheelScrollingEnabled(false);
        this.headerPane.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                headerScrollPane.setMinimumHeight(changedValue.height);
                for (Pane controlPane : editableTable.rowControlPanes) {
                    controlPane.setWidth(changedValue.width);
                }
            }
        });
        this.root.getChildren().add(headerScrollPane);
        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerConstraints.gridy = 0;
        headerConstraints.weightx = 1;
        headerConstraints.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(headerScrollPane.getInnerInstance(), headerConstraints);
        // Rows pane
        this.rowsPane.getInnerInstance().setLayout(this.rowsLayout);
        this.bottomSpacer.setBackgroundColor(null);
        this.rowsPane.getChildren().add(this.bottomSpacer);
        this.rowsPane.addLocationChangeListener(new ChangeListener<Point>() {

            @Override
            protected void changed(Component<?> component, Point changedValue, Point valueBeforeChange) {
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
    
    private Color borderColor = new Color(UIManager.getColor("controlDkShadow").getRGB());
    
    private Color lightBorderColor = new Color(UIManager.getColor("controlShadow").getRGB());
    
    private Color textColor = new Color(UIManager.getColor("text").getRGB());
    
    private Color activatedForegroundColor = new Color(UIManager.getColor("textHighlightText").getRGB());
    
    private Color activatedBackgroundColor = new Color(UIManager.getColor("textHighlight").getRGB());
    
    private Pane root;
    
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
            for (Pane pane : this.rowControlPanes) {
                this.makeInactiveVisible(pane);
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
    
    @Override
    public void updateLayout() {
        for (R key : this.mapRowControlPanes.keySet()) {
            Pane rowControlPane = this.mapRowControlPanes.get(key);
            this.rowsPane.getChildren().remove(rowControlPane);
        }
        super.updateLayout();
        if (this.displayedLeadingIndex == null) {
            return;
        }
        Integer leadingIndex = this.displayedLeadingIndex;
        this.displayedLeadingIndex = null;
        this.displayRowControls(leadingIndex);
        this.headerPane.updateLayout();
        this.rowsPane.updateLayout();
    }

    @Override
    public void updateDisplay() {
        for (R key : this.mapRowControlPanes.keySet()) {
            Pane rowControlPane = this.mapRowControlPanes.get(key);
            this.rowsPane.getChildren().remove(rowControlPane);
        }
        super.updateDisplay();
        if (this.displayedLeadingIndex == null) {
            return;
        }
        Integer leadingIndex = this.displayedLeadingIndex;
        this.displayedLeadingIndex = null;
        this.displayRowControls(leadingIndex);
        this.headerPane.updateDisplay();
        this.rowsPane.updateDisplay();
    }

    private R activeRowInstance = null;
    
    /**
     * このテーブルのアクティブな行情報のインスタンスを取得する。
     * 
     * @return 結果。
     */
    public R getActiveRowInstance() {
        return this.activeRowInstance;
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
        for (ChangeListener<R> changeListener : this.activeRowChangeListeners) {
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
        Map<C, Control> mapRowControls = this.listOfRowControlMap.get(pane);
        for (C columnInstance : this.columnInstances) {
            Control control = mapRowControls.get(columnInstance);
            control.setDisabled(false);
            if (control instanceof Label) {
                Color defaultLabelTextColor = this.mapDefaultLabelColor.get(control);
                if (defaultLabelTextColor.getRGB() == this.textColor.getRGB()) {
                    control.setForegroundColor(this.activatedForegroundColor);
                }
            }
            if (control instanceof DatePicker) {
                DatePicker datePicker = (DatePicker) control;
                if (columnInstance != this.activeColumnInstance) {
                    datePicker.setDisabledPopup(false);
                }
            }
        }
        if (this.activeColumnInstance == null) {
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
        pane.setBackgroundColor(null);
        for (Control control : pane.getChildren()) {
            control.setDisabled(true);
            if (control instanceof Label) {
                Color defaultLabelTextColor = this.mapDefaultLabelColor.get(control);
                if (defaultLabelTextColor.getRGB() == this.textColor.getRGB()) {
                    control.setForegroundColor(this.mapDefaultLabelColor.get(control));
                }
            }
        }
    }
    
    private C activeColumnInstance = null;
    
    /**
     * このテーブルのアクティブなカラム情報のインスタンスを取得する。
     * 
     * @return 結果。
     */
    public C getActiveColumnInstance() {
        return this.activeColumnInstance;
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
    private void activateColumn(C columnInstance) {
        this.activeColumnInstance = columnInstance;
        for (ChangeListener<C> changeListener : this.activeColumnChangeListeners) {
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
     * このテーブルの行情報のインスタンスから値を抽出する。
     * 
     * @param row
     * @param columnInstance
     * @return 結果。
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
    
    private Collection<C> columnInstances = new Collection<>();
    
    private Map<C, ColumnType> mapColumnTypes = new HashMap<>();
    
    private Map<C, ControlFactory<C, R, Control>> mapControlFactories = new HashMap<>();
    
    private int columnIndex = 0;
    
    private Pane headerSpacer = new Pane();
    
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
        if (this.activeColumnInstance == null) {
            this.activateColumn(columnInstance);
        }
        // Clear rows
        this.rowInstances.clear();
        this.mapRowSpacers.clear();
        this.rowIndex = 0;
        // Header label
        Label label = new Label(columnInstance.toString());
        label.setPadding(label.getFont().getSize());
        label.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
        label.setFont(this.getFont());
        if (this.columnInstances.size() > 0) {
            label.setBorder(Border.createLine(this.borderColor, 0, 0, 0, 1));
        }
        label.addSizeChangeListener(new ChangeListener<Dimension>() {

            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                for (Map<C, Control> controls : editableTable.listOfRowControlMap.values()) {
                    Control control = controls.get(columnInstance);
                    control.setWidth(changedValue.width);
                    control.updateDisplay();
                }
            }
        });
        label.addMouseMovedEventHandler(this.headerLabelMouseMovedEventHandler);
        label.addMousePressedEventHandler(MouseButton.BUTTON1, this.headerLabelMousePressedEventHandler);
        label.addMouseReleasedEventHandler(MouseButton.BUTTON1, this.headerLabelMouseReleasedEventHandler);
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
     * @return 結果。
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
    
    private boolean isStartedResizeHeaderLabel = false;
    
    private int headerPaneWidthBeforeResize;
    
    private int headerLabelWidthBeforeResize;
    
    private int headerLabelResizeStartPointX;
    
    /**
     * ヘッダーラベルの端でカラムをリサイズするイベントハンドラー。
     */
    private EventHandler<MouseEvent> headerLabelMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            editableTable.isStartedResizeHeaderLabel = false;
            editableTable.isDisabledControlSizeChangeListener = true;
            if (event.getX() > event.getSource().getWidth() - 10) {
                editableTable.isStartedResizeHeaderLabel = true;
                editableTable.headerPaneWidthBeforeResize = editableTable.headerPane.getWidth();
                editableTable.headerLabelWidthBeforeResize = event.getSource().getWidth();
                editableTable.headerLabelResizeStartPointX = event.getScreenX();
            }
        }
    };

    /**
     * ヘッダーラベルの端でカラムをリサイズするイベントハンドラー。
     */
    private EventHandler<MouseEvent> headerLabelMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            editableTable.isDisabledControlSizeChangeListener = false;
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
            int labelWidth = editableTable.headerLabelWidthBeforeResize - editableTable.headerLabelResizeStartPointX + event.getScreenX();
            if (tableColumn.isResizable() == false || editableTable.isStartedResizeHeaderLabel == false
                    || labelWidth <= tableColumn.getMinimumWidth() || labelWidth >= tableColumn.getMaximumWidth()) {
                return;
            }
            editableTable.headerPane.setWidth(editableTable.headerPaneWidthBeforeResize - editableTable.headerLabelResizeStartPointX + event.getScreenX());
            event.getSource().setWidth(labelWidth);
            editableTable.headerPane.getInnerInstance().doLayout();
            for (Pane controlPane : editableTable.rowControlPanes) {
                controlPane.getInnerInstanceForLayout().revalidate();
            }
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
     * @return 結果。
     */
    public Collection<R> getRows() {
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
        if (this.activeRowInstance == null) {
            this.activateRow(rowInstance);
        }
        Pane spacer = new Pane();
        spacer.setSize(0, this.defaultRowHeight);
        spacer.setInstanceForUseLater(rowInstance);
        this.rowsPane.getChildren().add(spacer);
        this.mapRowSpacers.put(rowInstance, spacer);
        this.configureRowToEmpty(rowInstance, this.rowIndex);
        this.rowIndex++;
        GridBagConstraints bottomSpacerConstraints = new GridBagConstraints();
        bottomSpacerConstraints.gridx = 0;
        bottomSpacerConstraints.gridy = this.rowIndex;
        bottomSpacerConstraints.weighty = 1;
        bottomSpacerConstraints.fill = GridBagConstraints.VERTICAL;
        this.rowsLayout.setConstraints(this.bottomSpacer.getInnerInstance(), bottomSpacerConstraints);
    }

    /**
     * このテーブルの行情報のインスタンスが削除された際の処理。
     * 
     * @param rowInstance
     */
    private void removeRow(R rowInstance) {
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
            Map<C, Control> mapRowControls = this.listOfRowControlMap.get(controlPane);
            Control control = mapRowControls.get(this.activeColumnInstance);
            ControlFactory<C, R, Control> controlFactory = this.mapControlFactories.get(this.activeColumnInstance);
            controlFactory.setValueToRowInstance(control, rowInstance, this.activeColumnInstance);
        }
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
    
    private Map<R, Pane> mapRowControlPanes = new HashMap<>();
    
    private Integer displayedLeadingIndex = null;
    
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
            if (this.rowsPane.getChildren().contains(controlPane) == false) {
                this.rowsPane.getChildren().add(controlPane);
            }
            if (this.activeRowInstance == rowInstance) {
                this.makeActiveVisible(controlPane);
            } else {
                this.makeInactiveVisible(controlPane);
            }
            controlPane.setInstanceForUseLater(rowInstance);
            Map<C, Control> mapRowControl = this.listOfRowControlMap.get(controlPane);
            for (C columnInstance : this.columnInstances) {
                Control control = mapRowControl.get(columnInstance);
                this.mapControlFactories.get(columnInstance).setValueToControl(rowInstance, columnInstance, control);
            }
            this.mapRowControlPanes.put(rowInstance, controlPane);
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
     * このテーブルのコントロールを表示できる行数を指定する。
     * 
     * @param numberOfDisplayRows
     */
    public void setNumberOfDisplayRows(int numberOfDisplayRows) {
        this.numberOfDisplayRows = numberOfDisplayRows;
    }
    
    private Map<Pane, Map<C, Control>> listOfRowControlMap = new HashMap<>();
    
    private List<Pane> rowControlPanes = new ArrayList<>();
    
    private Map<Label, Color> mapDefaultLabelColor = new HashMap<>();
    
    /**
     * 1行分のコントロールを作成する。
     */
    private void createOneRowControls() {
        EditableTable<C, R> editableTable = this;
        Pane pane = new Pane();
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
        for (C columnInstance : this.columnInstances) {
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
                    protected void changed(Component<?> component, String changedValue, String valueBeforeChange) {
                        Control control = (Control) component;
                        R rowInstance = pane.getInstanceForUseLater();
                        controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
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
                datePicker.addTextChangeListener(new ChangeListener<String>() {

                    @Override
                    protected void changed(Component<?> component, String changedValue, String valueBeforeChange) {
                        Control control = (Control) component;
                        R rowInstance = pane.getInstanceForUseLater();
                        controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
                    }
                });
                break;
            case DROPDOWNLIST:
                @SuppressWarnings("unchecked")
                DropDownList<Object> dropDownList = (DropDownList<Object>) control;
                dropDownList.addSelectedItemChangeListener(new ChangeListener<Object>() {

                    @Override
                    protected void changed(Component<?> component, Object changedValue, Object valueBeforeChange) {
                        Control control = (Control) component;
                        R rowInstance = pane.getInstanceForUseLater();
                        controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
                    }
                });
                break;
            case CHECKBOX:
                CheckBox checkBox = (CheckBox) control;
                checkBox.addMarkChangeListener(new ChangeListener<Boolean>() {

                    @Override
                    protected void changed(Component<?> component, Boolean changedValue, Boolean valueBeforeChange) {
                        Control control = (Control) component;
                        R rowInstance = pane.getInstanceForUseLater();
                        controlFactory.setValueToRowInstance(control, rowInstance, columnInstance);
                    }
                });
                break;
            case BUTTON:
                AnchorPane buttonPane = new AnchorPane();
                buttonPane.setBackgroundColor(null);
                buttonPane.getChildren().add(control);
                buttonPane.setAnchor(control, 0, 5, 0, 5);
                control = buttonPane;
                break;
            }
            control.addSizeChangeListener(new ControlSizeChangeListener(columnInstance));
            if (control.getWidth() < tableColumn.getWidth()) {
                control.setWidth(tableColumn.getWidth());
            }
            pane.getChildren().add(control);
            mapControls.put(columnInstance, control);
            if (control instanceof Pane == false) {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = this.columnInstances.indexOf(columnInstance);
                constraints.gridy = 0;
                constraints.weightx = 0;
                constraints.anchor = GridBagConstraints.CENTER;
                layout.setConstraints(control.getInnerInstanceForLayout(), constraints);
            }
        }
        this.rowControlPanes.add(pane);
        this.listOfRowControlMap.put(pane, mapControls);
        Pane spacer = new Pane();
        spacer.setBackgroundColor(null);
        spacer.setSize(0, this.defaultRowHeight);
        pane.getChildren().add(spacer);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = this.columnInstances.size();
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(spacer.getInnerInstance(), constraints);
    }
    
    /**
     * このテーブルの指定された行にスクロールする。
     * 
     * @param rowInstance
     */
    public void scrollTo(R rowInstance) {
        EditableTable<C, R> editableTable = this;
        GUI.executeLater(new Runnable() {
            
            @Override
            public void run() {
                editableTable.displayRowControls(editableTable.rowInstances.indexOf(rowInstance));
                editableTable.rowsScrollPane.scrollTo(editableTable.mapRowControlPanes.get(rowInstance));
            }
        });
    }

    private boolean isDisabledControlSizeChangeListener = false;
    
    /**
     * このテーブルの行に配置されたコントロールのサイズが変更された場合のリスナー。
     * 
     * @author hiro
     *
     */
    private class ControlSizeChangeListener extends ChangeListener<Dimension> {

        /**
         * コンストラクタ。<br>
         * コントロールが属するカラムのインスタンスを指定する。
         * 
         * @param columnInstance
         */
        private ControlSizeChangeListener(C columnInstance) {
            this.columnInstance = columnInstance;
        }
        
        private C columnInstance;
        
        @Override
        protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
            EditableTable<C, R> editableTable = EditableTable.this;
            if (editableTable.isDisabledControlSizeChangeListener || changedValue.width == 0 || changedValue.height == 0) {
                return;
            }
            TableColumn tableColumn = editableTable.mapTableColumns.get(this.columnInstance);
            if (tableColumn.getWidth() < changedValue.width && changedValue.width < tableColumn.getMaximumWidth()) {
                tableColumn.setWidth(changedValue.width + editableTable.getFont().getSize());
            }
        }
    }
    
    /**
     * このテーブルの行に配置されたコントロールのフォーカスが変更された場合のリスナー。
     * 
     * @author hiro
     * 
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
        protected void changed(Component<?> component, Boolean changedValue, Boolean valueBeforeChange) {
            EditableTable<C, R> editableTable = EditableTable.this;
            if (changedValue) {
                editableTable.activateColumn(this.columnInstance);
            } else {
                editableTable.activateColumn(null);
            }
        }
    }
    
    /**
     * このテーブルの行に配置されたコントロールがクリックされた際のイベントハンドラー。
     * 
     * @author hiro
     *
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
     * 
     * @author hiro
     *
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
         * 同じ行の別のコントロールにスクロールしてフォーカスする。
         * 
         * @param columnInstance
         */
        private void scrollAndFocus(C columnInstance) {
            EditableTable<C, R> editableTable = EditableTable.this;
            Map<C, Control> mapRowControls = editableTable.listOfRowControlMap.get(this.rowControlPane);
            Control control = mapRowControls.get(columnInstance);
            if (editableTable.rowsScrollPane.isDisplayedEntireControl(control) == false) {
                editableTable.rowsScrollPane.scrollTo(control);
            }
            editableTable.activate(editableTable.activeRowInstance, columnInstance);
        }
        
        @Override
        protected void handle(KeyEvent event) {
            EditableTable<C, R> editableTable = EditableTable.this;
            switch (event.getKeyCode()) {
            case TAB:
                event.consume();
                R rowInstance = this.rowControlPane.getInstanceForUseLater();
                Map<C, Control> mapRowControls = editableTable.listOfRowControlMap.get(this.rowControlPane);
                R destinationRowInstance = null;
                C destinationColumnInstance = null;
                if (event.isShiftDown() == false) {
                    // Next control
                    boolean isPass = false;
                    for (C columnInstance : editableTable.columnInstances) {
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
                        editableTable.getInnerInstance().transferFocus();
                    }
                } else {
                    // Previous control
                    for (C columnInstance : editableTable.columnInstances) {
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
                        editableTable.getInnerInstance().transferFocusBackward();
                    }
                }
                // Another row
                if (destinationRowInstance != null) {
                    Pane destinationControlPane = editableTable.mapRowControlPanes.get(destinationRowInstance);
                    Control destinationControl = null;
                    Map<C, Control> mapDestinationRowControls = editableTable.listOfRowControlMap.get(destinationControlPane);
                    if (mapDestinationRowControls != null) {
                        destinationControl = mapDestinationRowControls.get(destinationColumnInstance);
                    }
                    if (destinationControl == null) {
                        destinationControl = destinationControlPane;
                    }
                    if (destinationControlPane == null || editableTable.rowsScrollPane.isDisplayedBounds(destinationControlPane.getX() + destinationControl.getX(), destinationControlPane.getY(), destinationControl.getWidth(), destinationControlPane.getHeight()) == false) {
                        editableTable.scrollTo(destinationRowInstance);
                    }
                    editableTable.activate(destinationRowInstance, destinationColumnInstance);
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
        protected void changed(Component<?> component, Integer changedValue, Integer valueBeforeChange) {
            EditableTable<C, R> editableTable = EditableTable.this;
            for (Pane rowControlPane : editableTable.rowControlPanes) {
                for (Control control : rowControlPane.getChildren()) {
                    if (control instanceof DatePicker) {
                        DatePicker datePicker = (DatePicker) control;
                        datePicker.hidePopup();
                        datePicker.setDisabledPopup(true);
                    }
                }
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
            int numberOfAttempt = 0;
            while (control == null && numberOfAttempt < 5) {
                control = editableTable.rowsPane.getChildren().findControlByPoint(1, changedValue + editableTable.defaultRowHeight / 5 * numberOfAttempt);
                numberOfAttempt++;
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
     * 
     * @author hiro
     *
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
            headerLabel.setMaximumWidth(Short.MAX_VALUE);
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
         * @return 結果。
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
     * 
     * @author hiro
     *
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
