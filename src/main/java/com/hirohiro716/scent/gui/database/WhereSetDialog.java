package com.hirohiro716.scent.gui.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.ExceptionMessenger;
import com.hirohiro716.scent.Regex;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.database.ColumnInterface;
import com.hirohiro716.scent.database.WhereSet;
import com.hirohiro716.scent.database.WhereSet.Comparison;
import com.hirohiro716.scent.database.WhereSet.Where;
import com.hirohiro716.scent.datetime.Datetime;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.filesystem.File.WritingProcess;
import com.hirohiro716.scent.filesystem.File.ProcessAfterReadingLine;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.VerticalAlignment;
import com.hirohiro716.scent.gui.collection.AddListener;
import com.hirohiro716.scent.gui.collection.Collection;
import com.hirohiro716.scent.gui.collection.RemoveListener;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.CheckBox;
import com.hirohiro716.scent.gui.control.ContextMenu;
import com.hirohiro716.scent.gui.control.ContextMenuItem;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.DropDownList;
import com.hirohiro716.scent.gui.control.HorizontalPane;
import com.hirohiro716.scent.gui.control.Label;
import com.hirohiro716.scent.gui.control.ListView;
import com.hirohiro716.scent.gui.control.ScrollPane;
import com.hirohiro716.scent.gui.control.TextField;
import com.hirohiro716.scent.gui.control.VerticalPane;
import com.hirohiro716.scent.gui.dialog.DatetimeInputDialog;
import com.hirohiro716.scent.gui.dialog.DropDownListDialog;
import com.hirohiro716.scent.gui.dialog.FileOpenChooser;
import com.hirohiro716.scent.gui.dialog.FileSaveChooser;
import com.hirohiro716.scent.gui.dialog.InstantMessage;
import com.hirohiro716.scent.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.scent.gui.dialog.QuestionDialog;
import com.hirohiro716.scent.gui.dialog.TitledDialog;
import com.hirohiro716.scent.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.event.MouseEvent.MouseButton;
import com.hirohiro716.scent.io.json.JSONArray;
import com.hirohiro716.scent.io.json.JSONValue;

/**
 * SQLの検索条件を作成するダイアログのクラス。
 * 
 * @author hiro
*/
public class WhereSetDialog extends TitledDialog<Array<WhereSet>> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public WhereSetDialog(Frame<?> owner) {
        super(owner);
        this.setTitle("検索条件の指定");
        int baseSize = this.getPane().getFont().getSize();
        int width = baseSize * 50;
        int height = baseSize * 40;
        this.getPane().setSize(width, height);
    }

    @Override
    protected boolean isShowCloseSymbol() {
        return false;
    }
    
    private List<String> searchableColumns = new ArrayList<>();
    
    private Map<String, String> mapLogicalName = new HashMap<>();
    
    private Map<String, ColumnType> mapColumnType = new HashMap<>();
    
    private Map<String, Map<Object, String>> mapSelectableItems = new HashMap<>();
    
    /**
     * このダイアログで検索条件の作成に使用可能なカラムを追加する。
     * 
     * @param physicalName カラムの物理名。
     * @param logicalName カラムの論理名。
     * @param columnType カラムの種類。
     */
    public void addSearchableColumn(String physicalName, String logicalName, ColumnType columnType) {
        this.searchableColumns.add(physicalName);
        this.mapLogicalName.put(physicalName, logicalName);
        this.mapColumnType.put(physicalName, columnType);
    }
    
    /**
     * このダイアログで検索条件の作成に使用可能なカラムを追加する。
     * 
     * @param column カラム。
     * @param columnType カラムの種類。
     */
    public void addSearchableColumn(ColumnInterface column, ColumnType columnType) {
        this.addSearchableColumn(column.getFullPhysicalName(), column.getLogicalName(), columnType);
    }

    /**
     * このダイアログで検索条件の作成に使用可能な選択できるカラムを追加する。
     * 
     * @param physicalName カラムの物理名。
     * @param logicalName カラムの論理名。
     * @param selectableItems カラムの選択可能なアイテム。
     */
    public void addSearchableColumn(String physicalName, String logicalName, Map<?, String> selectableItems) {
        this.addSearchableColumn(physicalName, logicalName, ColumnType.SELECTABLE);
        if (selectableItems != null) {
            Map<Object, String> map = new LinkedHashMap<>();
            map.putAll(selectableItems);
            this.mapSelectableItems.put(physicalName, map);
        }
    }
    
    /**
     * このダイアログで検索条件の作成に使用可能な選択できるカラムを追加する。
     * 
     * @param column カラム。
     * @param selectableItems カラムの選択可能なアイテム。
     */
    public void addSearchableColumn(ColumnInterface column, Map<?, String> selectableItems) {
        this.addSearchableColumn(column.getFullPhysicalName(), column.getLogicalName(), selectableItems);
    }
    
    /**
     * このダイアログで検索条件の作成に使用可能な選択できるカラムを追加する。
     * 
     * @param physicalName カラムの物理名。
     * @param logicalName カラムの論理名。
     * @param selectableItems カラムの選択可能なアイテム。
     */
    public void addSearchableColumn(String physicalName, String logicalName, List<?> selectableItems) {
        Map<Object, String> map = new LinkedHashMap<>();
        for (Object value: selectableItems) {
            map.put(value, (String) null);
        }
        this.addSearchableColumn(physicalName, logicalName, map);
    }
    
    /**
     * このダイアログで検索条件の作成に使用可能な選択できるカラムを追加する。
     * 
     * @param column カラム。
     * @param selectableItems カラムの選択可能なアイテム。
     */
    public void addSearchableColumn(ColumnInterface column, List<?> selectableItems) {
        this.addSearchableColumn(column.getFullPhysicalName(), column.getLogicalName(), selectableItems);
    }
    
    private Map<Comparison, String> mapStringComparison = new LinkedHashMap<>();
    
    /**
     * 文字列検索の比較演算子を、表示テキストを指定して追加する。
     * 
     * @param comparison
     * @param text
     */
    public void addStringComparison(Comparison comparison, String text) {
        this.mapStringComparison.put(comparison, text);
    }
    
    private Map<Comparison, String> mapNumberStringComparison = new LinkedHashMap<>();
    
    /**
     * 数字だけの文字列検索の比較演算子を、表示テキストを指定して追加する。
     * 
     * @param comparison
     * @param text
     */
    public void addNumberStringComparison(Comparison comparison, String text) {
        this.mapNumberStringComparison.put(comparison, text);
    }

    private Collection<RowControlCallback> rowControlCallbacks = new Collection<>();

    /**
     * このダイアログの行で生成されるコントロールに対するコールバックを格納しているコレクションを取得する。
     * 
     * @return
     */
    public Collection<RowControlCallback> getRowControlCallbacks() {
        return this.rowControlCallbacks;
    }

    /**
     * 行で生成されたコントロールに対するコールバックをすべて呼び出す。
     * 
     * @param columnPhysicalName
     * @param control
     */
    private void callControlCallbackAll(String columnPhysicalName, Control control) {
        for (RowControlCallback callback: this.rowControlCallbacks) {
            callback.call(columnPhysicalName, control);
        }
    }
    
    @Override
    public boolean isCancelableByClickBackground() {
        return true;
    }
    
    @Override
    protected Control getInitialFocusControl() {
        return null;
    }
    
    private Array<WhereSet> defaultWhereSets = null;
    
    @Override
    public void setDefaultValue(Array<WhereSet> defaultValue) {
        this.defaultWhereSets = defaultValue;
    }
    
    private Array<WhereSet> result = null;
    
    @Override
    public Array<WhereSet> getDialogResult() {
        return this.result;
    }

    @Override
    protected void setDialogResult(Array<WhereSet> result) {
        this.result = result;
    }
    
    @Override
    protected void setCanceledDialogResult() {
        this.result = null;
    }

    @Override
    protected void processAfterShowing() {
        super.processAfterShowing();
        if (this.defaultWhereSets == null) {
            this.listView.getItems().add(new WhereSet());
        } else {
            this.listView.getItems().addAll(this.defaultWhereSets.getUnmodifiableList());
        }
        this.listView.setSelectedItem(this.listView.getItems().get(0));
    }
    
    @Override
    protected Control[] createControls() {
        // HorizontalPane
        HorizontalPane horizontalPane = new HorizontalPane(VerticalAlignment.CENTER);
        horizontalPane.setFillChildToPaneHeight(true);
        horizontalPane.setSpacing(5);
        this.getVerticalPaneOfControls().getGrowableControls().add(horizontalPane);
        // Left pane
        VerticalPane paneOfLeft = new VerticalPane();
        paneOfLeft.setFillChildToPaneWidth(true);
        paneOfLeft.setSpacing(5);
        paneOfLeft.getChildren().add(this.createButtonOfAddColumn());
        Control whereSetEditor = this.createWhereSetEditor();
        paneOfLeft.getChildren().add(whereSetEditor);
        paneOfLeft.getGrowableControls().add(whereSetEditor);
        horizontalPane.getChildren().add(paneOfLeft);
        horizontalPane.getGrowableControls().add(paneOfLeft);
        // Right pane
        VerticalPane paneOfRight = new VerticalPane();
        paneOfRight.setWidth(100);
        paneOfRight.setMinimumWidth(100);
        paneOfRight.setFillChildToPaneWidth(true);
        paneOfRight.setSpacing(5);
        paneOfRight.getChildren().add(this.createButtonOfAddWhereSet());
        this.createListViewOfWhereSet();
        paneOfRight.getChildren().add(this.listView);
        paneOfRight.getGrowableControls().add(this.listView);
        horizontalPane.getChildren().add(paneOfRight);
        // Left buttons
        HorizontalPane paneOfButton = new HorizontalPane();
        paneOfButton.setSpacing(5);
        HorizontalPane paneOfLeftButton = new HorizontalPane();
        paneOfLeftButton.setSpacing(5);
        paneOfButton.getChildren().add(paneOfLeftButton);
        paneOfButton.getGrowableControls().add(paneOfLeftButton);
        paneOfLeftButton.getChildren().add(this.createButtonOfExport());
        paneOfLeftButton.getChildren().add(this.createButtonOfImport());
        // Right buttons
        paneOfButton.getChildren().add(this.createButtonOfOK());
        paneOfButton.getChildren().add(this.createButtonOfCancel());
        return new Control[] {horizontalPane, paneOfButton};
    }

    /**
     * OKボタンを作成する。
     * 
     * @return
     */
    private Button createButtonOfOK() {
        WhereSetDialog dialog = this;
        Button button = new Button("OK");
        button.setMnemonic(KeyCode.O);
        button.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.importWhereSetFromEditor(dialog.listView.getSelectedItem());
                dialog.result = dialog.listView.getItems().toArray();
                dialog.close();
            }
        });
        return button;
    }
    
    /**
     * キャンセルボタンを作成する。
     * 
     * @return
     */
    private Button createButtonOfCancel() {
        WhereSetDialog dialog = this;
        Button button = new Button("キャンセル(C)");
        button.setMnemonic(KeyCode.C);
        button.addActionEventHandler(new EventHandler<ActionEvent>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.close();
            }
        });
        return button;
    }

    /**
     * 質問ダイアログのインスタンスを作成する。
     * 
     * @return
     */
    protected QuestionDialog createQuestionDialog() {
        return new QuestionDialog(this.getOwner());
    }
    
    /**
     * 保存ボタンを作成する。
     * 
     * @return
     */
    private Button createButtonOfExport() {
        WhereSetDialog dialog = this;
        Button button = new Button("保存(E)");
        button.setMnemonic(KeyCode.E);
        button.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.importWhereSetFromEditor(dialog.listView.getSelectedItem());
                FileSaveChooser chooser = new FileSaveChooser(dialog.getOwner());
                chooser.setTitle("検索条件の保存");
                if (chooser.showAndWait() == false) {
                    return;
                }
                File file = (File) chooser.getChoosedFilesystemItem();
                Runnable processOfExport = new Runnable() {
                    
                    @Override
                    public void run() {
                        JSONArray json = new JSONArray();
                        for (WhereSet whereSet: dialog.listView.getItems()) {
                            json.add(whereSet.createJSON());
                        }
                        try {
                            file.write("UTF-8", new WritingProcess() {
                                
                                @Override
                                public void call(OutputStreamWriter writer) throws IOException {
                                    writer.write(json.toString());
                                }
                            });
                            InstantMessage.show("保存しました。", 3000, dialog.getOwner());
                        } catch (IOException exception) {
                            InstantMessage.show(ExceptionMessenger.newInstance(exception).make("保存できませんでした。"), 3000, dialog.getOwner());
                        }
                    }
                };
                if (file.exists()) {
                    QuestionDialog questionDialog = dialog.createQuestionDialog();
                    questionDialog.setTitle("ファイルの上書き確認");
                    questionDialog.setMessage("指定されたファイルは既に存在します。上書きしますか？");
                    questionDialog.setCancelable(false);
                    questionDialog.setProcessAfterClosing(new ProcessAfterDialogClosing<ResultButton>() {

                        @Override
                        public void execute(ResultButton dialogResult) {
                            if (dialogResult != ResultButton.YES) {
                                return;
                            }
                            processOfExport.run();
                        }
                    });
                    questionDialog.show();
                } else {
                    processOfExport.run();
                }
            }
        });
        return button;
    }

    /**
     * 復元ボタンを作成する。
     * 
     * @return
     */
    private Button createButtonOfImport() {
        WhereSetDialog dialog = this;
        Button button = new Button("復元(I)");
        button.setMnemonic(KeyCode.I);
        button.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                FileOpenChooser chooser = new FileOpenChooser(dialog.getOwner());
                chooser.setTitle("検索条件の復元");
                if (chooser.showAndWait() == false) {
                    return;
                }
                File file = (File) chooser.getChoosedFilesystemItem();
                try {
                    StringObject fileContents = new StringObject();
                    file.read("UTF-8", new ProcessAfterReadingLine() {
                        
                        @Override
                        public void call(String line, BufferedReader bufferedReader) throws IOException {
                            fileContents.append(line);
                        }
                    });
                    JSONArray json = new JSONArray(fileContents.toString());
                    if (json.getContent().size() > 0) {
                        dialog.listView.getItems().clear();
                        dialog.whereSetNumber = 1;
                        for (JSONValue<?> jsonValue: json.getContent()) {
                            WhereSet whereSet = new WhereSet((JSONArray) jsonValue);
                            dialog.listView.getItems().add(whereSet);
                        }
                        dialog.listView.setSelectedItem(dialog.listView.getItems().get(0));
                    }
                    InstantMessage.show("復元しました。", 3000, dialog.getOwner());
                } catch (Exception exception) {
                    InstantMessage.show(ExceptionMessenger.newInstance(exception).make("復元できませんでした。"), 3000, dialog.getOwner());
                }
            }
        });
        return button;
    }

    /**
     * WhereSet追加のボタンを作成する。
     * 
     * @return
     */
    private Button createButtonOfAddWhereSet() {
        WhereSetDialog dialog = this;
        Button button = new Button("追加(N)");
        button.setMnemonic(KeyCode.N);
        button.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                WhereSet whereSet = new WhereSet();
                dialog.listView.getItems().add(whereSet);
                dialog.listView.setSelectedItem(whereSet);
            }
        });
        return button;
    }
    
    private ListView<WhereSet> listView;
    
    private int whereSetNumber = 1;
    
    /**
     * WhereSet一覧を表示するリストビューを作成する。
     */
    private void createListViewOfWhereSet() {
        WhereSetDialog dialog = this;
        this.listView = new ListView<>();
        this.listView.setWidth(this.listView.getFont().getSize() * 30);
        // When added items
        this.listView.getItems().addListener(new AddListener<>() {
            
            @Override
            protected void added(WhereSet added, int positionIndex) {
                dialog.listView.getMapDisplayTextForItem().put(added, "条件セット" + dialog.whereSetNumber);
                dialog.whereSetNumber++;
            }
        });
        // When removed items
        this.listView.getItems().addListener(new RemoveListener<>() {
            
            @Override
            protected void removed(WhereSet removed) {
                dialog.listView.getMapDisplayTextForItem().remove(removed);
            }
        });
        // When selected items
        this.listView.addSelectedItemChangeListener(new ChangeListener<WhereSet>() {

            @Override
            protected void changed(Component<?> component, WhereSet changedValue, WhereSet previousValue) {
                if (previousValue != null) {
                    dialog.importWhereSetFromEditor(previousValue);
                }
                dialog.exportWhereSetToEditor(changedValue);
            }
        });
        // Context menu
        ContextMenu contextMenu = new ContextMenu(this.listView);
        contextMenu.addContextMenuItem("複製", new Runnable() {
            
            @Override
            public void run() {
                WhereSet whereSet = dialog.listView.getSelectedItem();
                if (whereSet == null) {
                    return;
                }
                dialog.importWhereSetFromEditor(whereSet);
                dialog.listView.getItems().add(whereSet.clone());
            }
        });
        ContextMenuItem contextMenuItemRemove = new ContextMenuItem("削除");
        contextMenuItemRemove.setAction(new Runnable() {
            
            @Override
            public void run() {
                WhereSet whereSet = dialog.listView.getSelectedItem();
                if (whereSet == null) {
                    return;
                }
                dialog.listView.getItems().remove(whereSet);
            }
        });
        contextMenu.addContextMenuItem(contextMenuItemRemove);
        this.listView.addMouseClickedEventHandler(MouseButton.BUTTON3, new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                if (dialog.listView.getSelectedItem() == null || dialog.listView.getSelectedItem().equals(dialog.listView.getMouseHoverItem()) == false) {
                    return;
                }
                contextMenuItemRemove.setDisabled(dialog.listView.getItems().size() <= 1);
                contextMenu.show(event.getX(), event.getY());
            }
        });
    }
    
    /**
     * WhereSetの内容をエディターに表示する。
     * 
     * @param whereSet
     */
    private void exportWhereSetToEditor(WhereSet whereSet) {
        this.paneWhereSetEditor.getChildren().clear();
        if (whereSet != null) {
            for (Where where: whereSet.getWheres()) {
                // Searchable column
                String searchableColumn = where.getColumn();
                // Pane
                HorizontalPane paneOfWhere = this.createPaneOfSearchColumn(searchableColumn);
                // Comparison
                DropDownList<Comparison> dropDownList = paneOfWhere.getChildren().findDropDownListByName(WhereSetDialog.NAME_OF_COMPARISON_CONTROL);
                Comparison comparison = where.getComparison();
                dropDownList.setSelectedItem(comparison);
                this.paneWhereSetEditor.getChildren().add(paneOfWhere);
                // Negate
                CheckBox checkBoxNegate = paneOfWhere.getChildren().findCheckBoxByName(WhereSetDialog.NAME_OF_NEGATE_CHECKBOX);
                if (checkBoxNegate != null) {
                    checkBoxNegate.setMarked(where.isNegate());
                }
                // Values
                if (comparison != null) {
                    ColumnType columnType = this.mapColumnType.get(searchableColumn);
                    Control control1;
                    Control control2;
                    switch (comparison) {
                    case IS_NULL:
                        break;
                    case LIKE:
                        control1 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
                        String valueOfLike = StringObject.newInstance(where.getValue()).extract(1, -1).toString();
                        this.setValueToControl(columnType, control1, valueOfLike);
                        break;
                    case BETWEEN:
                        control1 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
                        this.setValueToControl(columnType, control1, where.getValue());
                        control2 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL2);
                        this.setValueToControl(columnType, control2, where.getValue2());
                        break;
                    case EQUAL:
                    default:
                        control1 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
                        this.setValueToControl(columnType, control1, where.getValue());
                        break;
                    }
                }
            }
        }
        this.paneWhereSetEditor.updateLayout();
        this.paneWhereSetEditor.updateDisplay();
    }
    
    private final static String WILDCARD = "%"; 
    
    /**
     * エディターからWhereSetに情報を入力する。
     * 
     * @param whereSet
     */
    private void importWhereSetFromEditor(WhereSet whereSet) {
        whereSet.clear();
        for (Control child: this.paneWhereSetEditor.getChildren()) {
            HorizontalPane paneOfWhere = (HorizontalPane) child;
            // Searchable column
            String searchableColumn = paneOfWhere.getInstanceForUseLater();
            // Comparison
            DropDownList<Comparison> dropDownListComparison = paneOfWhere.getChildren().findDropDownListByName(WhereSetDialog.NAME_OF_COMPARISON_CONTROL);
            Comparison comparison = dropDownListComparison.getSelectedItem();
            // Negate
            CheckBox checkBoxNegate = paneOfWhere.getChildren().findCheckBoxByName(WhereSetDialog.NAME_OF_NEGATE_CHECKBOX);
            boolean isNegate = false;
            if (checkBoxNegate != null && checkBoxNegate.isMarked()) {
                isNegate = true;
            }
            // Values
            if (comparison != null) {
                ColumnType columnType = this.mapColumnType.get(searchableColumn);
                Control control1;
                Control control2;
                switch (comparison) {
                case IS_NULL:
                    whereSet.addIsNull(isNegate, searchableColumn);
                    break;
                case LIKE:
                    control1 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
                    String valueOfLike = StringObject.newInstance(this.getValueFromControl(columnType, control1)).prepend(WhereSetDialog.WILDCARD).append(WhereSetDialog.WILDCARD).toString();
                    whereSet.add(isNegate, searchableColumn, comparison, valueOfLike);
                    break;
                case BETWEEN:
                    control1 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
                    control2 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL2);
                    whereSet.addBetween(isNegate, searchableColumn, this.getValueFromControl(columnType, control1), this.getValueFromControl(columnType, control2));
                    break;
                case EQUAL:
                default:
                    control1 = paneOfWhere.getChildren().findControlByName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
                    whereSet.add(isNegate, searchableColumn, comparison, this.getValueFromControl(columnType, control1));
                    break;
                }
            }
        }
    }
    
    /**
     * ドロップダウンリストを表示するダイアログのインスタンスを作成する。
     * 
     * @return
     */
    protected DropDownListDialog<String> createDropDownListDialog() {
        return new DropDownListDialog<>(this.getOwner());
    }

    /**
     * カラム追加のボタンを作成する。
     * 
     * @return
     */
    private Button createButtonOfAddColumn() {
        WhereSetDialog ownerDialog = this;
        Button button = new Button("追加(A)");
        button.setMnemonic(KeyCode.A);
        button.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                DropDownListDialog<String> dialog = ownerDialog.createDropDownListDialog();
                dialog.getDropDownList().getItems().addAll(ownerDialog.searchableColumns);
                dialog.getDropDownList().setMapDisplayTextForItem(ownerDialog.mapLogicalName);
                dialog.setTitle("検索するカラムの追加");
                dialog.setMessage("検索条件に追加するカラムを選択してください。");
                dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<>() {
                    
                    @Override
                    public void execute(String dialogResult) {
                        if (dialogResult == null) {
                            return;
                        }
                        HorizontalPane pane = ownerDialog.createPaneOfSearchColumn(dialogResult);
                        ownerDialog.paneWhereSetEditor.getChildren().add(pane);
                    }
                });
                dialog.show();
            }
        });
        return button;
    }

    private VerticalPane paneWhereSetEditor;
    
    /**
     * WhereSetを編集するコントロールを作成する。
     * 
     * @return
     */
    private Control createWhereSetEditor() {
        this.paneWhereSetEditor = new VerticalPane();
        this.paneWhereSetEditor.setPadding(5);
        this.paneWhereSetEditor.setSpacing(5);
        ScrollPane scrollPane = new ScrollPane(this.paneWhereSetEditor);
        return scrollPane;
    }
    
    private static final String NAME_OF_COMPARISON_CONTROL = "comparison";
    
    private static final String NAME_OF_VALUES_PANE = "values";
    
    /**
     * 指定された検索カラムから、検索条件を編集するペインを作成する。
     * 
     * @param searchableColumn
     * @return
     */
    private HorizontalPane createPaneOfSearchColumn(String searchableColumn) {
        WhereSetDialog dialog = this;
        HorizontalPane pane = new HorizontalPane(VerticalAlignment.CENTER);
        pane.setSpacing(5);
        pane.setInstanceForUseLater(searchableColumn);
        // Remove button
        Button buttonOfRemove = new Button("削除");
        buttonOfRemove.addActionEventHandler(new EventHandler<ActionEvent>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.paneWhereSetEditor.getChildren().remove(pane);
                dialog.paneWhereSetEditor.updateLayout();
                dialog.paneWhereSetEditor.updateDisplay();
            }
        });
        pane.getChildren().add(buttonOfRemove);
        // Label of logical name
        Label label = new Label(this.mapLogicalName.get(searchableColumn));
        pane.getChildren().add(label);
        // DropDownList of comparison
        ColumnType columnType = this.mapColumnType.get(searchableColumn);
        DropDownList<Comparison> dropDownList = this.createDropDownListOfComparison(columnType);
        dropDownList.setName(WhereSetDialog.NAME_OF_COMPARISON_CONTROL);
        dropDownList.addSelectedItemChangeListener(this.comparisonChangeListener);
        pane.getChildren().add(dropDownList);
        // Pane of values
        HorizontalPane paneValues = new HorizontalPane(VerticalAlignment.CENTER);
        paneValues.setName(WhereSetDialog.NAME_OF_VALUES_PANE);
        paneValues.setSpacing(5);
        pane.getChildren().add(paneValues);
        for (Control control: pane.getChildren()) {
            dialog.callControlCallbackAll(searchableColumn, control);
        }
        return pane;
    }
    
    /**
     * カラムの種類から比較演算子を指定させるドロップダウンリストを作成する。
     * 
     * @param columnType
     * @return
     */
    private DropDownList<Comparison> createDropDownListOfComparison(ColumnType columnType) {
        Map<Comparison, String> mapComparison = new LinkedHashMap<>();
        int width = this.getPane().getFont().getSize();
        switch (columnType) {
        case STRING:
            width *= 10;
            mapComparison.put(Comparison.EQUAL, "検索値と等しい");
            mapComparison.put(Comparison.LIKE, "検索値を含む");
            for (Comparison comparison: this.mapStringComparison.keySet()) {
                mapComparison.put(comparison, this.mapStringComparison.get(comparison));
            }
            mapComparison.put(Comparison.IS_NULL, "値が未設定");
            break;
        case NUMBER_STRING:
            width *= 14;
            mapComparison.put(Comparison.EQUAL, "検索値と等しい");
            mapComparison.put(Comparison.BETWEEN, "検索値１～検索値２の間");
            mapComparison.put(Comparison.LIKE, "検索値を含む");
            for (Comparison comparison: this.mapNumberStringComparison.keySet()) {
                mapComparison.put(comparison, this.mapNumberStringComparison.get(comparison));
            }
            mapComparison.put(Comparison.IS_NULL, "値が未設定");
            break;
        case NUMBER:
        case DATE:
        case DATE_STRING:
            width *= 14;
            mapComparison.put(Comparison.EQUAL, "検索値と等しい");
            mapComparison.put(Comparison.BETWEEN, "検索値１～検索値２の間");
            mapComparison.put(Comparison.IS_NULL, "値が未設定");
            break;
        case DATETIME:
        case DATETIME_STRING:
            width *= 14;
            mapComparison.put(Comparison.BETWEEN, "検索値１～検索値２の間");
            mapComparison.put(Comparison.IS_NULL, "値が未設定");
            break;
        case BOOLEAN:
        case SELECTABLE:
            width *= 10;
            mapComparison.put(Comparison.EQUAL, "検索値と等しい");
            mapComparison.put(Comparison.IS_NULL, "値が未設定");
            break;
        }
        DropDownList<Comparison> dropDownList = new DropDownList<>();
        dropDownList.setMinimumWidth(width);
        dropDownList.getItems().addAll(mapComparison.keySet());
        dropDownList.setMapDisplayTextForItem(mapComparison);
        return dropDownList;
    }
    
    /**
     * 比較演算子ドロップダウンリストの値変更時のリスナー。
     */
    private ChangeListener<Comparison> comparisonChangeListener = new ChangeListener<>() {

        @Override
        protected void changed(Component<?> component, Comparison changedValue, Comparison previousValue) {
            WhereSetDialog dialog = WhereSetDialog.this;
            DropDownList<?> dropDownList = (DropDownList<?>) component;
            HorizontalPane parent = dropDownList.getParent();
            String searchableColumn = parent.getInstanceForUseLater();
            ColumnType columnType = dialog.mapColumnType.get(searchableColumn);
            HorizontalPane paneValues = parent.getChildren().findControlsByClass(HorizontalPane.class).get(0);
            paneValues.getChildren().clear();
            if (changedValue == null) {
                return;
            }
            Control[] controls = dialog.createValueControls(searchableColumn, columnType, changedValue);
            for (Control control: controls) {
                dialog.callControlCallbackAll(searchableColumn, control);
            }
            paneValues.getChildren().addAll(controls);
            paneValues.updateLayout();
        }
    };

    private static final String NAME_OF_VALUE_CONTROL1 = "value1";
    
    private static final String NAME_OF_VALUE_CONTROL2 = "value2";
    
    private static final String NAME_OF_NEGATE_CHECKBOX = "negate";
    
    /**
     * 検索カラム、種類、比較演算子を指定して検索値を入力するコントロールを作成する。
     * 
     * @param searchableColumn
     * @param columnType 
     * @param comparison
     * @return
     */
    private Control[] createValueControls(String searchableColumn, ColumnType columnType, Comparison comparison) {
        List<Control> controls = new ArrayList<>();
        switch (comparison) {
        case IS_NULL:
            break;
        case BETWEEN:
            Control control1 = this.createValueControl(searchableColumn, columnType);
            control1.setName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
            controls.add(control1);
            controls.add(new Label("〜"));
            Control control2 = this.createValueControl(searchableColumn, columnType);
            control2.setName(WhereSetDialog.NAME_OF_VALUE_CONTROL2);
            controls.add(control2);
            break;
        case EQUAL:
        case LIKE:
        default:
            Control control = this.createValueControl(searchableColumn, columnType);
            control.setName(WhereSetDialog.NAME_OF_VALUE_CONTROL1);
            controls.add(control);
            break;
        }
        switch (columnType) {
        case STRING:
        case NUMBER_STRING:
        case NUMBER:
        case DATE:
        case DATE_STRING:
        case DATETIME:
        case DATETIME_STRING:
        case SELECTABLE:
            CheckBox checkBox = new CheckBox("否定");
            checkBox.setName(WhereSetDialog.NAME_OF_NEGATE_CHECKBOX);
            controls.add(checkBox);
            break;
        case BOOLEAN:
            break;
        }
        return controls.toArray(new Control[] {});
    }
    
    /**
     * 検索カラム、種類を指定して検索値を入力するコントロールを作成する。
     * 
     * @param searchableColumn
     * @param columnType
     * @return
     */
    private Control createValueControl(String searchableColumn, ColumnType columnType) {
        int baseSize = this.getPane().getFont().getSize();
        TextField textField;
        CheckBox checkBox;
        DropDownList<Object> dropDownList;
        switch (columnType) {
        case STRING:
            textField = new TextField();
            textField.setWidth(baseSize * 7);
            return textField;
        case NUMBER_STRING:
            textField = new TextField();
            textField.setWidth(baseSize * 5);
            textField.setDisableInputMethod(true);
            textField.addLimitByRegex(Regex.INTEGER.getPattern(), false);
            return textField;
        case NUMBER:
            textField = new TextField();
            textField.setWidth(baseSize * 5);
            textField.setTextHorizontalAlignment(HorizontalAlignment.RIGHT);
            textField.setDisableInputMethod(true);
            textField.addLimitByRegex(Regex.DECIMAL_NEGATIVE.getPattern(), false);
            return textField;
        case DATE:
        case DATE_STRING:
            textField = new TextField();
            textField.setWidth(baseSize * 7);
            textField.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
            textField.setEditable(false);
            textField.addMouseClickedEventHandler(MouseButton.BUTTON1, this.dateMouseClickedEventHandler);
            return textField;
        case DATETIME:
        case DATETIME_STRING:
            textField = new TextField();
            textField.setWidth(baseSize * 10);
            textField.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
            textField.setEditable(false);
            textField.addMouseClickedEventHandler(MouseButton.BUTTON1, this.datetimeMouseClickedEventHandler);
            return textField;
        case BOOLEAN:
            checkBox = new CheckBox();
            return checkBox;
        case SELECTABLE:
            dropDownList = new DropDownList<>();
            dropDownList.setWidth(baseSize * 9);
            Map<Object, String> selectableItems = this.mapSelectableItems.get(searchableColumn);
            if (selectableItems != null) {
                dropDownList.getItems().addAll(selectableItems.keySet().toArray());
                dropDownList.setMapDisplayTextForItem(selectableItems);
            }
            return dropDownList;
        }
        return null;
    }
    
    /**
     * 日付と時刻を入力するダイアログのインスタンスを作成する。
     * 
     * @return
     */
    protected DatetimeInputDialog createDatetimeInputDialog() {
        return new DatetimeInputDialog(this.getOwner());
    }

    /**
     * 日付の検索値を入力するダイアログを表示するイベントハンドラー。
     */
    private EventHandler<MouseEvent> dateMouseClickedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            WhereSetDialog ownerDialog = WhereSetDialog.this;
            TextField textField = (TextField) event.getSource();
            DatetimeInputDialog dialog = ownerDialog.createDatetimeInputDialog();
            dialog.setTitle("日付の入力");
            dialog.setMessage("日付を選択してください。");
            dialog.setTimeInput(false);
            try {
                dialog.setDefaultValue(Datetime.newInstance(textField.getText()));
            } catch (ParseException exception) {
            }
            dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<>() {

                @Override
                public void execute(Datetime dialogResult) {
                    textField.setInstanceForUseLater(dialogResult);
                    if (dialogResult != null) {
                        textField.setText(dialogResult.toStringOnlyDate());
                    } else {
                        textField.setText(null);
                    }
                }
            });
            dialog.show();
        }
    };
    
    /**
     * 日時の検索値を入力するダイアログを表示するイベントハンドラー。
     */
    private EventHandler<MouseEvent> datetimeMouseClickedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            WhereSetDialog ownerDialog = WhereSetDialog.this;
            TextField textField = (TextField) event.getSource();
            DatetimeInputDialog dialog = ownerDialog.createDatetimeInputDialog();
            dialog.setTitle("日付と時刻の入力");
            dialog.setMessage("日付と時刻を入力してください。");
            try {
                dialog.setDefaultValue(Datetime.newInstance(textField.getText()));
            } catch (ParseException exception) {
            }
            dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<>() {

                @Override
                public void execute(Datetime dialogResult) {
                    textField.setInstanceForUseLater(dialogResult);
                    if (dialogResult != null) {
                        textField.setText(dialogResult.toString(new SimpleDateFormat("yyyy-MM-dd HH:mm")));
                    }
                }
            });
            dialog.show();
        }
    };

    /**
     * 検索値を入力するコントロール、種類を指定して検索値を取得する。
     * 
     * @param columnType 
     * @param control
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object getValueFromControl(ColumnType columnType, Control control) {
        TextField textField;
        CheckBox checkBox;
        DropDownList<Object> dropDownList;
        switch (columnType) {
        case STRING:
        case NUMBER_STRING:
        case DATE_STRING:
        case DATETIME_STRING:
            textField = (TextField) control;
            return textField.getText();
        case NUMBER:
            textField = (TextField) control;
            return StringObject.newInstance(textField.getText()).toDouble();
        case DATE:
        case DATETIME:
            Datetime datetime = control.getInstanceForUseLater();
            if (datetime != null) {
                return datetime.getDate();
            }
            break;
        case BOOLEAN:
            checkBox = (CheckBox) control;
            return checkBox.isMarked();
        case SELECTABLE:
            dropDownList = (DropDownList<Object>) control;
            return dropDownList.getSelectedItem();
        }
        return null;
    }

    /**
     * 検索値を入力するコントロール、種類を指定して検索値をセットする。
     * 
     * @param columnType 
     * @param control
     * @param value
     */
    @SuppressWarnings("unchecked")
    private void setValueToControl(ColumnType columnType, Control control, Object value) {
        if (value == null) {
            return;
        }
        TextField textField;
        CheckBox checkBox;
        DropDownList<Object> dropDownList;
        switch (columnType) {
        case STRING:
        case DATE_STRING:
        case DATETIME_STRING:
            textField = (TextField) control;
            textField.setText(value.toString());
            break;
        case NUMBER_STRING:
        case NUMBER:
            textField = (TextField) control;
            textField.setText(StringObject.newInstance(value.toString()).removeMeaninglessDecimalPoint().toString());
            break;
        case DATE:
            textField = (TextField) control;
            Date dateForDate = (Date) value;
            Datetime datetimeForDate = new Datetime(dateForDate);
            textField.setInstanceForUseLater(datetimeForDate);
            textField.setText(datetimeForDate.toStringOnlyDate());
            break;
        case DATETIME:
            textField = (TextField) control;
            Date dateForDatetime = (Date) value;
            Datetime datetimeForDatetime = new Datetime(dateForDatetime);
            textField.setInstanceForUseLater(datetimeForDatetime);
            textField.setText(datetimeForDatetime.toString(new SimpleDateFormat("yyyy-MM-dd HH:mm")));
            break;
        case BOOLEAN:
            checkBox = (CheckBox) control;
            checkBox.setMarked((boolean) value);
            break;
        case SELECTABLE:
            dropDownList = (DropDownList<Object>) control;
            dropDownList.setSelectedItem(value);
            break;
        }
    }
    
    /**
     * 検索するカラムの種類列挙型。
     * 
     * @author hiro
     */
    public enum ColumnType {
        /**
         * 文字列。
         */
        STRING,
        /**
         * 数字だけの文字列。
         */
        NUMBER_STRING,
        /**
         * 数値。
         */
        NUMBER,
        /**
         * 日付。
         */
        DATE,
        /**
         * 日付の文字列。
         */
        DATE_STRING,
        /**
         * 日付と時刻。
         */
        DATETIME,
        /**
         * 日付と時刻の文字列。
         */
        DATETIME_STRING,
        /**
         * 真偽値。
         */
        BOOLEAN,
        /**
         * 選択可能。
         */
        SELECTABLE,
        ;
    }

    /**
     * 検索条件の行で生成されたコントロールに対するコールバックインターフェイス。
     */
    public interface RowControlCallback {
        
        /**
         * 指定されたカラム、コントロールに対して処理を実行する。
         * 
         * @param column
         * @param control
         */
        public abstract void call(String column, Control control);
    }
}
