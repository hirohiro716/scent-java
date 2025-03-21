package com.hirohiro716.scent.gui.database;

import java.sql.SQLException;

import com.hirohiro716.scent.database.Database;
import com.hirohiro716.scent.database.RecordConflictException;
import com.hirohiro716.scent.database.RecordMapper;
import com.hirohiro716.scent.gui.Editor;
import com.hirohiro716.scent.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.scent.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.scent.gui.dialog.QuestionDialog;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.FrameEvent;

/**
 * GUIデータベースレコード編集ウィンドウの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <D> データベースの型。
 * @param <T> 編集するターゲットの型。
 */
public abstract class RecordEditor<D extends Database, T extends RecordMapper> extends Editor<T> {
    
    /**
     * コンストラクタ。<br>
     * 表示するウィンドウのタイトル、幅、高さを指定する。
     * 
     * @param title 
     * @param width
     * @param height
     */
    public RecordEditor(String title, int width, int height) {
        super(title, width, height);
        RecordEditor<D, T> editor = this;
        this.addClosedEventHandler(new EventHandler<>() {
            
            @Override
            protected void handle(FrameEvent event) {
                editor.closeDatabase(editor.database);
            }
        });
    }

    @Override
    protected String getGenericNameOfTarget() {
        return "レコード";
    }

    /**
     * レコードが変更されている場合はtrueを返す。
     * 
     * @return
     */
    public abstract boolean isRecordChanged();
    
    private D database;
    
    /**
     * レコードの編集に使用しているデータベースのインスタンスを取得する。
     * 
     * @return
     */
    protected D getDatabase() {
        return this.database;
    }
    
    /**
     * レコードの編集に使用するデータベースのインスタンスを作成する。<br>
     * データベースへの接続やトランザクションの開始処理は行わない。
     * 
     * @return
     */
    protected abstract D createDatabase();

    /**
     * レコードの編集に使用するデータベースの接続処理をする。<br>
     * トランザクションの開始処理は行わない。
     * 
     * @param database
     * @throws SQLException
     */
    protected abstract void connectDatabase(D database) throws SQLException;
    
    /**
     * レコードの編集に使用するデータベースの切断処理をする。
     * 
     * @param database
     */
    protected abstract void closeDatabase(D database);
    
    /**
     * データベースレコードの編集・排他処理を行う。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @param database 接続済みのデータベースインスタンス。
     * @return 編集処理済みのターゲットインスタンス。
     * @throws SQLException 
     */
    protected abstract T editRecordMapper(D database) throws SQLException;

    @Override
    protected T editTarget() throws SQLException {
        if (this.database != null) {
            this.closeDatabase(this.database);
        }
        this.database = this.createDatabase();
        this.connectDatabase(this.database);
        return this.editRecordMapper(this.database);
    }

    /**
     * レコードがコンフリクトした場合の質問メッセージを表示する。
     * 
     * @param message 質問のメッセージ。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    protected void showRecordConflictQuestionDialog(String message, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        RecordEditor<D, T> editor = this;
        QuestionDialog dialog = this.createQuestionDialog();
        dialog.setTitle("データベースレコードの競合");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setDefaultValue(ResultButton.NO);
        dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<ResultButton>() {

            @Override
            public void execute(ResultButton dialogResult) {
                if (dialogResult == ResultButton.YES) {
                    editor.getTarget().setConflictIgnored(true);
                }
                if (processAfterDialogClosing != null) {
                    processAfterDialogClosing.execute(dialogResult);
                }
            }
        });
        dialog.show();
    }

    /**
     * レコードがコンフリクトした場合の質問メッセージを表示する。
     * 
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    protected void showRecordConflictQuestionDialog(RecordConflictException exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        this.showRecordConflictQuestionDialog(exception.getMessage() + "ほかで行われた変更を取り消して保存を続行しますか？", processAfterDialogClosing);
    }
}
