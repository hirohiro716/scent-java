package com.hirohiro716.gui.database;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.database.ColumnInterface;
import com.hirohiro716.database.Database;
import com.hirohiro716.database.RecordMapper;
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
    
    private EditableTable<C, DynamicArray<C>> editableTable;
    
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
        this.editableTable.getRows().clear();
        DynamicArray<C>[] records = this.getTarget().getRecords();
        for (DynamicArray<C> record : records) {
            this.editableTable.getRows().add(record);
        }
        if (records.length > 0) {
            this.editableTable.activate(records[0], this.getInitialFocusColumn());
        }
    }

    @Override
    public void inputFromEditor() {
        this.getTarget().clearRecords();
        for (DynamicArray<C> record : this.editableTable.getRows()) {
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
            DynamicArray<C> record = editor.getTarget().createDefaultRecord();
            editor.editableTable.getRows().add(record);
            editor.editableTable.activate(record, editor.getInitialFocusColumn());
            editor.editableTable.updateLayout();
            editor.editableTable.scrollTo(record);
        }
    };
    
    /**
     * このエディターの情報をデータベースに保存する。
     */
    protected abstract void save();
    
    /**
     * データの保存イベントハンドラー。
     */
    private EventHandler<ActionEvent> saveEventHandler = new EventHandler<ActionEvent>() {

        @Override
        protected void handle(ActionEvent event) {
            RecordEditorOfTable<D, T, C> editor = RecordEditorOfTable.this;
            editor.save();
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
     * レコードを編集するテーブルを作成する。
     * 
     * @return 結果。
     * @throws Exception 
     */
    protected abstract EditableTable<C, DynamicArray<C>> createEditableTable() throws Exception;
}
