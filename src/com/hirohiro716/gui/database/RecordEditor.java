package com.hirohiro716.gui.database;

import java.sql.SQLException;

import com.hirohiro716.OS;
import com.hirohiro716.StringObject;
import com.hirohiro716.database.DataNotFoundException;
import com.hirohiro716.database.Database;
import com.hirohiro716.database.RecordMapper;
import com.hirohiro716.gui.Editor;
import com.hirohiro716.gui.ProcessAfterDialogClose;
import com.hirohiro716.gui.dialog.QuestionDialog;
import com.hirohiro716.gui.dialog.MessageableDialog.ResultButton;

/**
 * GUIデータベースレコード編集ウィンドウの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <D> データベースの型。
 * @param <T> 編集するターゲットの型。
 */
public abstract class RecordEditor<D extends Database, T extends RecordMapper<?>> extends Editor<T> {
    
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
    }
    
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
     * データベースレコードの編集・排他処理を行う。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @param database 接続済みのデータベースインスタンス。
     * @return 編集処理済みのターゲットインスタンス。
     * @throws SQLException 
     */
    protected abstract T editRecordMapper(D database) throws SQLException;

    @Override
    protected T editTarget() throws Exception {
        this.database = this.createDatabase();
        this.connectDatabase(this.database);
        return this.editRecordMapper(this.database);
    }
    
    /**
     * レコードの編集に使用するデータベースの接続処理をする。<br>
     * 接続に失敗した場合は確認ダイアログを表示して再帰的に試行する。
     * 
     * @param processAfterSuccess
     */
    public void connectDatabaseWithRetryDialog(ProcessAfterSuccess<D> processAfterSuccess) {
        try {
            this.database = this.createDatabase();
            this.connectDatabase(this.database);
            if (processAfterSuccess != null) {
                processAfterSuccess.execute(this.database);
            }
        } catch (SQLException exception) {
            QuestionDialog dialog = new QuestionDialog(this.getWindow());
            dialog.setTitle("接続再試行の確認");
            StringObject message = new StringObject("データベースへの接続に失敗しました。再試行しますか？");
            message.append(OS.thisOS().getLineSeparator());
            message.append(OS.thisOS().getLineSeparator());
            message.append(exception.getMessage());
            dialog.setMessage(message.toString());
            dialog.setDefaultValue(ResultButton.YES);
            dialog.setProcessAfterClose(new ProcessAfterDialogClose<>() {
                
                @Override
                public void execute(ResultButton dialogResult) {
                    RecordEditor<D, T> editor = RecordEditor.this;
                    if (dialogResult == ResultButton.YES) {
                        editor.connectDatabaseWithRetryDialog(processAfterSuccess);
                    } else {
                        editor.close();
                    }
                }
            });
        }
    }
    
    /**
     * データベースレコードの編集・排他処理を行う。<br>
     * 接続に失敗した場合は確認ダイアログを表示して再帰的に試行する。
     * 
     * @param processAfterSuccess
     */
    public void editRecordMapperWithRetryDialog(ProcessAfterDialogClose<T> processAfterSuccess) {
        try {
            this.editRecordMapper(this.database);
            if (processAfterSuccess != null) {
                processAfterSuccess.execute(this.getTarget());
            }
        } catch (DataNotFoundException exception) {
            this.close();
        } catch (SQLException exception) {
            QuestionDialog dialog = new QuestionDialog(this.getWindow());
            dialog.setTitle("編集再試行の確認");
            StringObject message = new StringObject("レコードの編集に失敗しました。再試行しますか？");
            message.append(OS.thisOS().getLineSeparator());
            message.append(OS.thisOS().getLineSeparator());
            message.append(exception.getMessage());
            dialog.setMessage(message.toString());
            dialog.setDefaultValue(ResultButton.YES);
            dialog.setProcessAfterClose(new ProcessAfterDialogClose<>() {
                
                @Override
                public void execute(ResultButton dialogResult) {
                    RecordEditor<D, T> editor = RecordEditor.this;
                    if (dialogResult == ResultButton.YES) {
                        editor.connectDatabaseWithRetryDialog(new ProcessAfterSuccess<>() {

                            @Override
                            public void execute(D successedInstance) {
                                editor.editRecordMapperWithRetryDialog(processAfterSuccess);
                            }
                        });
                    } else {
                        editor.close();
                    }
                }
            });
        }
    }
}
