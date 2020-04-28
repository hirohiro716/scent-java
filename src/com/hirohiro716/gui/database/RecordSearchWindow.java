package com.hirohiro716.gui.database;

import java.util.ArrayList;
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
        this.setTitle(title);
        this.setSize(width, height);
        this.tableView = this.createTableView();
        this.setContent(this.createContentUsingTableView(this.tableView));
    }
    
    private TableView<String, DynamicArray<String>> tableView;
    
    /**
     * レコード検索結果を表示するテーブルビューを作成する。<br>
     * このメソッドはスーバークラスのコンストラクタで自動的に呼び出される。
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
                DynamicArray<String>[] rows = searcher.search(window.createSelectSQL(), window.createPartAfterWhereSQL(), whereSets);
                window.tableView.getRows().clear();
                for (DynamicArray<String> row : rows) {
                    window.tableView.getRows().add(row);
                }
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
    
    private ProcessAfterRecordSelection processAfterRecordSelection = null;
    
    /**
     * レコードの検索結果のうち一つのレコードを選択した後に実行する処理をセットする。
     * 
     * @param processAfterRecordSelection
     */
    public void setProcessAfterRecordSelect(ProcessAfterRecordSelection processAfterRecordSelection) {
        this.processAfterRecordSelection = processAfterRecordSelection;
    }
    
    /**
     * レコードの検索結果のうち一つのレコードを選択してウィンドウを閉じる。
     * 
     * @param record
     */
    protected void selectRecord(DynamicArray<String> record) {
        if (this.processAfterRecordSelection == null || record == null) {
            return;
        }
        this.processAfterRecordSelection.execute(record);
        this.close();
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
     * このメソッドはスーバークラスのコンストラクタで自動的に呼び出される。
     * 
     * @param tableView 検索結果が表示されるテーブルビュー。
     * @return 結果。
     */
    protected abstract Control createContentUsingTableView(TableView<String, DynamicArray<String>> tableView);
}
