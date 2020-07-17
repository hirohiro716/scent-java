package com.hirohiro716.gui.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import com.hirohiro716.Array;
import com.hirohiro716.DynamicArray;
import com.hirohiro716.database.RecordSearcher;
import com.hirohiro716.database.WhereSet;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.Window;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.table.TableView;
import com.hirohiro716.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.gui.dialog.WaitCircleDialog;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;
import com.hirohiro716.gui.event.KeyEvent;

/**
 * GUIデータベースレコード検索ウィンドウの抽象クラス。
 * 
 * @author hiro
 *
 * @param <S> 検索に使用するインスタンスの型。
 */
public abstract class RecordSearchWindow<S extends RecordSearcher> extends Window {
    
    /**
     * コンストラクタ。<br>
     * 表示するウィンドウのタイトル、幅、高さを指定する。
     * 
     * @param title 
     * @param width
     * @param height
     */
    public RecordSearchWindow(String title, int width, int height) {
        super();
        RecordSearchWindow<S> window = this;
        this.setTitle(title);
        this.setSize(width, height);
        this.addOpenedEventHandler(new EventHandler<FrameEvent>() {

            @Override
            protected void handle(FrameEvent event) {
                window.tableView = window.createTableView();
                window.setContent(window.createContentUsingTableView(window.tableView));
                window.searchedRecords.addAll(window.defaultRecords);
                window.updateDisplayOfTableView();
            }
        });
        this.addClosedEventHandler(new EventHandler<FrameEvent>() {

            @Override
            protected void handle(FrameEvent event) {
                if (window.processAfterSelectingAndClosing != null) {
                    window.processAfterSelectingAndClosing.execute(window.selectedRecord);
                }
            }
        });
    }

    private TableView<String, DynamicArray<String>> tableView;
    
    /**
     * レコード検索結果を表示するテーブルビューを作成する。<br>
     * このメソッドはウィンドウを表示した後に自動的に呼び出される。
     * 
     * @return 結果。
     */
    protected abstract TableView<String, DynamicArray<String>> createTableView();
    
    /**
     * レコード検索結果を表示するテーブルビューを取得する。
     * 
     * @return 結果。
     */
    public TableView<String, DynamicArray<String>> getTableView() {
        return this.tableView;
    }
    
    private List<DynamicArray<String>> defaultRecords = new ArrayList<>();
    
    /**
     * このレコード検索ウィンドウに初期表示するレコードをセットする。
     * 
     * @param records
     */
    public void setDefaultRecords(DynamicArray<String>[] records) {
        this.defaultRecords.clear();
        this.defaultRecords.addAll(Arrays.asList(records));
    }

    /**
     * このレコード検索ウィンドウに初期表示するレコードをセットする。
     * 
     * @param records
     */
    public final void setDefaultRecords(Collection<DynamicArray<String>> records) {
        this.setDefaultRecords(records);
    }
    
    /**
     * このレコード検索ウィンドウに表示されているコントロールから検索条件を作成する。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @return 結果。
     */
    protected abstract WhereSet[] createWhereSetFromWindowContent();
    
    /**
     * レコード検索に使用するインスタンスを作成する。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @return 結果。
     * @throws Exception 
     */
    protected abstract S createRecordSearcher() throws Exception;
    
    /**
     * WHERE句の前までのSELECT句からFROM句までのSQLを取得する。
     * 
     * @return 結果。
     */
    protected String createSelectSQL() {
        return null;
    }
    
    /**
     * WHERE句の後の構文。GROUP BY句、HAVING句、ORDER BY句、LIMIT句などを取得する。
     * 
     * @return 結果。
     */
    protected String createPartAfterWhereSQL() {
        return null;
    }
    
    /**
     * 検索を実行する前に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @param whereSets 検索に使用される検索条件。
     */
    protected abstract void processBeforeSearching(WhereSet... whereSets);
    
    /**
     * 検索を実行した後に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     */
    protected abstract void processAfterSearching();

    /**
     * レコード検索結果テーブルに指定されたレコードの表示を許可する場合はtrueを返す。
     * 
     * @param record
     * @return 結果。
     */
    protected abstract boolean isAllowViewRecord(DynamicArray<String> record);
    
    private List<DynamicArray<String>> searchedRecords = new ArrayList<>();
    
    /**
     * このウィンドウで検索されたレコードを取得する。
     * 
     * @return 結果。
     */
    protected List<DynamicArray<String>> getSearchedRecords() {
        return this.searchedRecords;
    }
    
    /**
     * レコード検索結果テーブルの表示を更新する。
     */
    protected void updateDisplayOfTableView() {
        this.tableView.getRowInstances().clear();
        for (DynamicArray<String> record : this.searchedRecords) {
            if (this.isAllowViewRecord(record)) {
                this.tableView.getRowInstances().add(record);
            }
        }
        this.tableView.updateDisplay();
    }
    
    /**
     * 検索条件を指定して、ダイアログを表示しながら検索を実行する。
     * 
     * @param whereSets
     */
    private void searchWithDialog(WhereSet... whereSets) {
        this.processBeforeSearching(whereSets);
        RecordSearchWindow<S> window = this;
        WaitCircleDialog<Void> dialog = new WaitCircleDialog<>(this, new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                S searcher = window.createRecordSearcher();
                String selectSQL = window.createSelectSQL();
                DynamicArray<String>[] rows;
                if (selectSQL == null) {
                    rows = searcher.search(window.createPartAfterWhereSQL(), whereSets);
                } else {
                    rows = searcher.search(selectSQL, window.createPartAfterWhereSQL(), whereSets);
                }
                window.searchedRecords.clear();
                for (DynamicArray<String> row : rows) {
                    window.searchedRecords.add(row);
                }
                window.updateDisplayOfTableView();
                return null;
            }
        });
        dialog.setTitle("検索処理中");
        dialog.setMessage("データベースのレコードを検索しています。");
        dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<Void>() {
            
            @Override
            public void execute(Void dialogResult) {
                window.tableView.updateDisplay();
                if (dialog.getException() != null) {
                    window.showException(dialog.getException());
                }
                window.processAfterSearching();
            }
        });
        dialog.show();
    }

    /**
     * このレコード検索ウィンドウに表示されているコントロールの検索条件で検索を実行する。
     */
    protected void searchFromWindowContent() {
        WhereSet[] whereSets = this.createWhereSetFromWindowContent();
        this.searchWithDialog(whereSets);
    }
    
    /**
     * 高度な検索に使用する検索条件作成ダイアログを作成する。
     * 
     * @return 結果。
     */
    protected abstract WhereSetDialog createAdvancedSearchDialog();
    
    private Array<WhereSet> lastTimeWhereSets = null;
    
    /**
     * このレコード検索ウィンドウで高度な検索ダイアログを表示する。
     */
    protected void showAdvancedSearchDialog() {
        RecordSearchWindow<S> window = this;
        WhereSetDialog dialog = this.createAdvancedSearchDialog();
        dialog.setDefaultValue(this.lastTimeWhereSets);
        dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<>() {

            @Override
            public void execute(Array<WhereSet> dialogResult) {
                if (dialogResult == null) {
                    return;
                }
                window.lastTimeWhereSets = dialogResult;
                List<WhereSet> listWhereSet = new ArrayList<>();
                for (WhereSet whereSet : dialogResult) {
                    WhereSet copyWhereSet = whereSet.clone();
                    listWhereSet.add(copyWhereSet);
                }
                window.searchWithDialog(listWhereSet.toArray(new WhereSet[] {}));
            }
        });
        dialog.show();
    }
    
    private ProcessAfterSelectingAndClosing processAfterSelectingAndClosing = null;
    
    /**
     * レコードの検索結果のうち一つのレコードを選択してウィンドウを閉じた後に実行する処理を取得する。
     * 
     * @return 結果。
     */
    public ProcessAfterSelectingAndClosing getProcessAfterSelectingAndClosing() {
        return this.processAfterSelectingAndClosing;
    }
    
    /**
     * レコードの検索結果のうち一つのレコードを選択してウィンドウを閉じた後に実行する処理をセットする。
     * 
     * @param processAfterSelectingAndClosing
     */
    public void setProcessAfterSelectingAndClosing(ProcessAfterSelectingAndClosing processAfterSelectingAndClosing) {
        this.processAfterSelectingAndClosing = processAfterSelectingAndClosing;
    }
    
    private DynamicArray<String> selectedRecord = null;
    
    /**
     * レコードの検索結果のうち一つのレコードを選択した状態にする。
     * 
     * @param record
     */
    protected void selectRecord(DynamicArray<String> record) {
        this.selectedRecord = record;
    }

    /**
     * 指定されたコードのキーを離した際の、レコード選択イベントハンドラーを、指定されたコントロールに追加する。
     * 
     * @param keyCode
     * @param controls
     */
    protected void addKeyReleasedEventHandlerForSelect(KeyCode keyCode, Control... controls) {
        for (Control control : controls) {
            control.addKeyReleasedEventHandler(new EventHandler<KeyEvent>() {

                @Override
                protected void handle(KeyEvent event) {
                    RecordSearchWindow<S> window = RecordSearchWindow.this;
                    DynamicArray<String> selected = window.tableView.getSelectedRow();
                    if (event.getKeyCode() == keyCode && selected != null) {
                        window.selectRecord(selected);
                    }
                }
            });
        }
    }
    
    /**
     * レコードの検索結果のうち一つのレコードを編集する。
     * 
     * @param record
     */
    protected abstract void editRecord(DynamicArray<String> record);
    
    /**
     * 指定されたコードのキーを離した際の、レコード編集イベントハンドラーを、指定されたコントロールに追加する。
     * 
     * @param keyCode
     * @param controls
     */
    protected void addKeyReleasedEventHandlerForEdit(KeyCode keyCode, Control... controls) {
        for (Control control : controls) {
            control.addKeyReleasedEventHandler(new EventHandler<KeyEvent>() {

                @Override
                protected void handle(KeyEvent event) {
                    RecordSearchWindow<S> window = RecordSearchWindow.this;
                    DynamicArray<String> selected = window.tableView.getSelectedRow();
                    if (event.getKeyCode() == keyCode && selected != null) {
                        window.editRecord(selected);
                    }
                }
            });
        }
    }
    
    /**
     * レコードの検索結果のうち一つのレコードを削除する。
     * 
     * @param record
     */
    protected abstract void deleteRecord(DynamicArray<String> record);
    
    /**
     * 指定されたコードのキーを離した際の、レコード削除イベントハンドラーを、指定されたコントロールに追加する。
     * 
     * @param keyCode
     * @param controls
     */
    protected void addKeyReleasedEventHandlerForDelete(KeyCode keyCode, Control... controls) {
        for (Control control : controls) {
            control.addKeyReleasedEventHandler(new EventHandler<KeyEvent>() {

                @Override
                protected void handle(KeyEvent event) {
                    RecordSearchWindow<S> window = RecordSearchWindow.this;
                    DynamicArray<String> selected = window.tableView.getSelectedRow();
                    if (event.getKeyCode() == keyCode && selected != null) {
                        window.deleteRecord(selected);
                    }
                }
            });
        }
    }

    /**
     * このレコード検索ウィンドウに表示するコンテンツを作成する。<br>
     * このメソッドはウィンドウを表示した後に自動的に呼び出される。
     * 
     * @param tableView 検索結果が表示されるテーブルビュー。
     * @return 結果。
     */
    protected abstract Control createContentUsingTableView(TableView<String, DynamicArray<String>> tableView);
    
    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
