package com.hirohiro716.scent.gui.database;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.database.ColumnInterface;
import com.hirohiro716.scent.database.Database;
import com.hirohiro716.scent.database.RecordMapper;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.VerticalAlignment;
import com.hirohiro716.scent.gui.collection.Collection;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.HorizontalPane;
import com.hirohiro716.scent.gui.control.VerticalPane;
import com.hirohiro716.scent.gui.control.table.EditableTable;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;

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
     * @return
     */
    protected abstract boolean isAllowRecordView(DynamicArray<C> record);
    
    private Collection<DynamicArray<C>> records = new Collection<>();
    
    /**
     * このテーブルコントロールの表示を更新する。
     */
    protected void updateDisplayOfEditableTable() {
        this.editableTable.getRowInstances().clear();
        for (DynamicArray<C> record: this.records) {
            if (this.isAllowRecordView(record)) {
                this.editableTable.getRowInstances().add(record);
            }
        }
        if (this.editableTable.getRowInstances().size() > 0) {
            this.editableTable.activate(this.editableTable.getRowInstances().get(0), this.getInitialFocusColumn());
        }
        this.editableTable.displayRowControls(0);
        this.editableTable.updateDisplay();
    }
    
    private EditableTable<C, DynamicArray<C>> editableTable;
    
    /**
     * このエディターのテーブルコントロールを取得する。
     * 
     * @return
     */
    protected EditableTable<C, DynamicArray<C>> getEditableTable() {
        return this.editableTable;
    }
    
    /**
     * このテーブルコントロールで最初にフォーカスするカラムを取得する。
     * 
     * @return
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
        for (DynamicArray<C> record: this.records) {
            this.getTarget().addRecord(record);
        }
    }

    private VerticalPane paneOfContent;

    @Override
    protected Control createContent() throws Exception {
        // Content pane
        this.paneOfContent = new VerticalPane();
        this.paneOfContent.setFillChildToPaneWidth(true);
        // Top content
        Control topContent = this.createTopContent();
        if (topContent != null) {
            this.paneOfContent.getChildren().add(topContent);
        }
        // EditableTable
        this.editableTable = this.createEditableTable();
        this.paneOfContent.getChildren().add(this.editableTable);
        this.paneOfContent.getGrowableControls().add(this.editableTable);
        // Bottom content
        Control bottomContent = this.createBottomContent();
        if (bottomContent != null) {
            this.paneOfContent.getChildren().add(bottomContent);
        }
        // Button pane
        this.paneOfBottomButton.setPadding(15);
        this.paneOfContent.getChildren().add(this.paneOfBottomButton);
        // Add button
        Button buttonAdd = new Button("追加(A)");
        buttonAdd.setMnemonic(KeyCode.A);
        buttonAdd.addActionEventHandler(this.addEventHandler);
        this.paneOfBottomButton.getChildren().add(buttonAdd);
        // Save button
        this.paneOfBottomButton.addStretchableSpacer(1);
        Button buttonSave = new Button("保存(S)");
        buttonSave.setMnemonic(KeyCode.S);
        buttonSave.addActionEventHandler(this.saveEventHandler);
        this.paneOfBottomButton.getChildren().add(buttonSave);
        return this.paneOfContent;
    }

    /**
     * このエディターの内容を表示するペインを取得する。
     * 
     * @return
     */
    public VerticalPane getContentPane() {
        return this.paneOfContent;
    }

    private HorizontalPane paneOfBottomButton = new HorizontalPane(VerticalAlignment.CENTER);
    
    /**
     * ウィンドウ下部のボタンが表示されているペインを取得する。
     * 
     * @return
     */
    protected HorizontalPane getPaneOfBottomButton() {
        return this.paneOfBottomButton;
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
        this.editableTable.getRowInstances().add(record);
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
        this.editableTable.getRowInstances().remove(record);
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
     * @return
     * @throws Exception
     */
    protected abstract Control createTopContent() throws Exception;

    /**
     * このウィンドウのテーブルコントロールの下部に表示するコントロールを作成する。
     * 
     * @return
     * @throws Exception
     */
    protected abstract Control createBottomContent() throws Exception;
    
    /**
     * レコードを編集するテーブルを作成する。
     * 
     * @return
     * @throws Exception 
     */
    protected abstract EditableTable<C, DynamicArray<C>> createEditableTable() throws Exception;
}
