package com.hirohiro716.gui.database;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.database.ColumnInterface;
import com.hirohiro716.database.Database;
import com.hirohiro716.database.RecordMapper;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.control.AnchorPane;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.VerticalPane;
import com.hirohiro716.gui.control.table.EditableTable;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.EventHandler;

/**
 * GUIデータベースレコードをテーブルで編集するウィンドウの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <D> データベースの型。
 * @param <T> 編集するレコードマッパーの型。
 * @param <C> レコードマッパーのカラムの型。
 */
public abstract class RecordEditorOfTable<D extends Database, T extends RecordMapper, C extends ColumnInterface> extends RecordEditor<D, T> {
    
    /**
     * コンストラクタ。<br>
     * 表示するウィンドウのタイトル、幅、高さを指定する。
     * 
     * @param title
     * @param width
     * @param height
     */
    public RecordEditorOfTable(String title, int width, int height) {
        super(title, width, height);
    }

    /**
     * このエディターのテーブルコントロールに指定されたレコードの表示を許可する場合はtrueを返す。
     * 
     * @param record
     * @return 結果。
     */
    protected abstract boolean isAllowViewRecord(DynamicArray<C> record);
    
    private Collection<DynamicArray<C>> records = new Collection<>();
    
    /**
     * このテーブルコントロールの表示を更新する。
     */
    protected void updateDisplayOfEditableTable() {
        this.editableTable.getRows().clear();
        for (DynamicArray<C> record : this.records) {
            if (this.isAllowViewRecord(record)) {
                this.editableTable.getRows().add(record);
            }
        }
        if (this.editableTable.getRows().size() > 0) {
            this.editableTable.activate(this.editableTable.getRows().get(0), this.getInitialFocusColumn());
        }
        this.editableTable.displayRowControls(0);
        this.editableTable.updateDisplay();
    }
    
    private EditableTable<C, DynamicArray<C>> editableTable;
    
    /**
     * このエディターのテーブルコントロールを取得する。
     * 
     * @return 結果。
     */
    protected EditableTable<C, DynamicArray<C>> getEditableTable() {
        return this.editableTable;
    }
    
    /**
     * このテーブルコントロールで最初にフォーカスするカラムを取得する。
     * 
     * @return 結果。
     */
    protected abstract C getInitialFocusColumn();
    
    @Override
    public void outputToEditor() {
        this.records.clear();
        this.records.addAll(this.getTarget().getRecords());
        this.updateDisplayOfEditableTable();
    }

    @Override
    public void inputFromEditor() {
        this.getTarget().clearRecords();
        for (DynamicArray<C> record : this.records) {
            this.getTarget().addRecord(record);
        }
    }

    @Override
    protected Control createContent() throws Exception {
        // Root pane
        VerticalPane rootPane = new VerticalPane();
        rootPane.setFillChildToPaneWidth(true);
        // Top content
        Control topContent = this.createTopContent();
        if (topContent != null) {
            rootPane.getChildren().add(topContent);
        }
        // EditableTable
        this.editableTable = this.createEditableTable();
        rootPane.getChildren().add(this.editableTable);
        rootPane.getGrowableControls().add(this.editableTable);
        // Bottom content
        Control bottomContent = this.createBottomContent();
        if (bottomContent != null) {
            rootPane.getChildren().add(bottomContent);
        }
        // Button pane
        AnchorPane anchorPaneButton = new AnchorPane();
        anchorPaneButton.setPadding(15);
        rootPane.getChildren().add(anchorPaneButton);
        // Add button
        Button buttonAdd = new Button("追加");
        buttonAdd.addActionEventHandler(this.addEventHandler);
        anchorPaneButton.getChildren().add(buttonAdd);
        anchorPaneButton.setAnchor(buttonAdd, null, null, null, 0);
        // Save button
        Button buttonSave = new Button("保存");
        buttonSave.addActionEventHandler(this.saveEventHandler);
        anchorPaneButton.getChildren().add(buttonSave);
        anchorPaneButton.setAnchor(buttonSave, null, 0, null, null);
        return rootPane;
    }

    /**
     * レコードの追加イベントハンドラー。
     */
    private EventHandler<ActionEvent> addEventHandler = new EventHandler<ActionEvent>() {

        @Override
        protected void handle(ActionEvent event) {
            RecordEditorOfTable<D, T, C> editor = RecordEditorOfTable.this;
            editor.addRecord();
        }
    };
    
    /**
     * このテーブルコントロールに新しい行情報を追加する。
     */
    protected void addRecord() {
        DynamicArray<C> record = this.getTarget().createDefaultRecord();
        this.records.add(record);
        this.editableTable.getRows().add(record);
        this.editableTable.activate(record, this.getInitialFocusColumn());
        this.editableTable.updateLayout();
        this.editableTable.updateDisplay();
        this.editableTable.scrollTo(record);
    }
    
    /**
     * このテーブルコントロールからアクティブな行情報を削除する。
     */
    protected void removeActiveRecord() {
        DynamicArray<C> record = this.editableTable.getActiveRowInstance();
        this.records.remove(record);
        this.editableTable.getRows().remove(record);
        this.editableTable.updateLayout();
        this.editableTable.updateDisplay();
    }
    
    /**
     * このエディターの情報をデータベースに保存する。
     */
    protected abstract void saveToDatabase();
    
    /**
     * データの保存イベントハンドラー。
     */
    private EventHandler<ActionEvent> saveEventHandler = new EventHandler<ActionEvent>() {

        @Override
        protected void handle(ActionEvent event) {
            RecordEditorOfTable<D, T, C> editor = RecordEditorOfTable.this;
            editor.saveToDatabase();
        }
    };

    /**
     * このウィンドウのテーブルコントロールの上部に表示するコントロールを作成する。
     * 
     * @return 結果。
     * @throws Exception
     */
    protected abstract Control createTopContent() throws Exception;

    /**
     * このウィンドウのテーブルコントロールの下部に表示するコントロールを作成する。
     * 
     * @return 結果。
     * @throws Exception
     */
    protected abstract Control createBottomContent() throws Exception;
    
    /**
     * レコードを編集するテーブルを作成する。
     * 
     * @return 結果。
     * @throws Exception 
     */
    protected abstract EditableTable<C, DynamicArray<C>> createEditableTable() throws Exception;
}
