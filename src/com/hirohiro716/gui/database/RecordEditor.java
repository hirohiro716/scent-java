package com.hirohiro716.gui.database;

import java.sql.SQLException;

import com.hirohiro716.OS;
import com.hirohiro716.StringObject;
import com.hirohiro716.database.Database;
import com.hirohiro716.database.RecordMapper;
import com.hirohiro716.gui.Editor;
import com.hirohiro716.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.gui.dialog.QuestionDialog;
import com.hirohiro716.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;

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
    
    /**
     * レコードが変更されている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isRecordChanged();
    
    private D database;
    
    /**
     * レコードの編集に使用しているデータベースのインスタンスを取得する。
     * 
     * @return 結果。
     */
    protected D getDatabase() {
        return this.database;
    }
    
    /**
     * レコードの編集に使用するデータベースのインスタンスを作成する。<br>
     * データベースへの接続やトランザクションの開始処理は行わない。
     * 
     * @return 結果。
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
     * データベースレコードの編集・排他処理を行う。<br>
     * 接続に失敗した場合は確認ダイアログを表示して再帰的に試行する。
     * 
     * @param processAfterSuccess 編集に成功した場合の処理。
     * @param processAfterFailure 編集に失敗し再試行しなかった場合の処理。
     */
    public void editRecordMapperWithRetryDialog(ProcessAfterDialogClosing<T> processAfterSuccess, ProcessAfterFailure processAfterFailure) {
        try {
            this.setTarget(this.editTarget());
            if (processAfterSuccess != null) {
                processAfterSuccess.execute(this.getTarget());
            }
        } catch (SQLException exception) {
            QuestionDialog dialog = new QuestionDialog(this);
            dialog.setTitle("編集再試行の確認");
            StringObject message = new StringObject("レコードの編集に失敗しました。再試行しますか？");
            message.append(OS.thisOS().getLineSeparator());
            message.append(OS.thisOS().getLineSeparator());
            message.append(exception.getMessage());
            dialog.setMessage(message.toString());
            dialog.setDefaultValue(ResultButton.YES);
            dialog.setCancelable(false);
            dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<>() {
                
                @Override
                public void execute(ResultButton dialogResult) {
                    RecordEditor<D, T> editor = RecordEditor.this;
                    if (dialogResult == ResultButton.YES) {
                        editor.editRecordMapperWithRetryDialog(processAfterSuccess, processAfterFailure);
                    } else {
                        if (processAfterFailure != null) {
                            processAfterFailure.execute();
                        }
                    }
                }
            });
            dialog.show();
        }
    }

    /**
     * データベースレコードの編集・排他処理を行う。<br>
     * 接続に失敗した場合は確認ダイアログを表示して再帰的に試行する。
     * 
     * @param processAfterSuccess 編集に成功した場合の処理。
     */
    public void editRecordMapperWithRetryDialog(ProcessAfterDialogClosing<T> processAfterSuccess) {
        this.editRecordMapperWithRetryDialog(processAfterSuccess, null);
    }

    /**
     * データベースレコードの編集・排他処理を行う。<br>
     * 接続に失敗した場合は確認ダイアログを表示して再帰的に試行する。
     * 
     * @param processAfterFailure 編集に失敗し再試行しなかった場合の処理。
     */
    public void editRecordMapperWithRetryDialog(ProcessAfterFailure processAfterFailure) {
        this.editRecordMapperWithRetryDialog(null, processAfterFailure);
    }

    /**
     * データベースレコードの編集・排他処理を行う。<br>
     * 接続に失敗した場合は確認ダイアログを表示して再帰的に試行する。
     */
    public void editRecordMapperWithRetryDialog() {
        this.editRecordMapperWithRetryDialog(null, null);
    }
}
