package com.hirohiro716.database.sqlite;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.database.DataNotFoundException;
import com.hirohiro716.database.sqlite.SQLite.IsolationLevel;
import com.hirohiro716.filesystem.File;

/**
 * SQLiteデータベースの単一レコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class SingleRecordMapper extends com.hirohiro716.database.SingleRecordMapper implements Closeable {
    
    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public SingleRecordMapper(SQLite database) {
        super(database);
    }
    
    @Override
    public SQLite getDatabase() {
        return (SQLite) super.getDatabase();
    }
    
    @Override
    protected DynamicArray<String> fetchRecordForEdit() throws SQLException {
        StringObject sql = new StringObject("SELECT * FROM ");
        sql.append(this.getTable().getPhysicalName());
        sql.append(" WHERE ");
        sql.append(this.getWhereSet().buildPlaceholderClause());
        sql.append(";");
        return this.getDatabase().fetchRecord(sql.toString(), this.getWhereSet().buildParameters());
    }

    /**
     * マップしようとしているレコードが、ほかで編集中かどうかを判定するメソッド。<br>
     * このメソッドはスーパークラスの編集処理時に自動的に呼び出され、編集できるかの判定に使用される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @return ほかで編集中の場合trueを返す。
     * @throws SQLException 
     */
    public abstract boolean isEditingByAnother(SQLite sqlite) throws SQLException;
    
    /**
     * マップしたレコードをSQLiteデータベース上で編集中としてマークし、ほかのインスタンスからの編集を拒否する。<br>
     * このメソッドはスーパークラスの編集処理時に自動的に呼び出される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @throws SQLException 
     * @throws DataNotFoundException 
     */
    protected abstract void updateToEditing(SQLite sqlite) throws SQLException, DataNotFoundException;
    
    /**
     * マップしたレコードのSQLiteデータベース上での編集中マークを解除する。<br>
     * このメソッドはスーパークラスの閉じる処理で自動的に呼び出される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @throws SQLException
     * @throws DataNotFoundException 
     */
    protected abstract void updateToEditingFinish(SQLite sqlite) throws SQLException, DataNotFoundException;

    /**
     * データベースに対して排他処理を行うための新しいデータベースインスタンスを作成する。<br>
     * 接続処理はスーパークラスで自動的に行われる。
     * 
     * @return 結果。
     */
    public abstract SQLite createDatabaseForEditing();
    
    private boolean isEditing = false;
    
    @Override
    public void edit() throws SQLException {
        if (this.isEditing) {
            return;
        }
        super.edit();
        try (SQLite sqlite = this.createDatabaseForEditing()) {
            sqlite.connect(this.getDatabase().getDatabaseFile());
            sqlite.begin(IsolationLevel.EXCLUSIVE);
            if (this.isEditingByAnother(sqlite)) {
                throw new SQLException(SQLite.ERROR_MESSAGE_EDITING_FAILURE);
            }
            this.updateToEditing(sqlite);
            sqlite.commit();
            this.isEditing = true;
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (this.isEditing) {
                File databaseFile = this.getDatabase().getDatabaseFile();
                this.getDatabase().close();
                try (SQLite sqlite = this.createDatabaseForEditing()) {
                    sqlite.connect(databaseFile);
                    sqlite.begin(IsolationLevel.EXCLUSIVE);
                    this.updateToEditingFinish(sqlite);
                    sqlite.commit();
                }
                this.isEditing = false;
            }
            this.setRecord(this.createDefaultRecord());
        } catch (DataNotFoundException exception) {
            exception.printStackTrace();
        } catch (SQLException exception) {
            throw new IOException(exception);
        }
    }

    /**
     * マップしようとしているレコードの編集中を強制的に解除するメソッド。
     * 
     * @throws IOException
     */
    public void forciblyClose() throws IOException {
        try (SQLite sqlite = this.createDatabaseForEditing()) {
            sqlite.connect(this.getDatabase().getDatabaseFile());
            this.updateToEditingFinish(sqlite);
        } catch (SQLException exception) {
            throw new IOException(exception);
        }
    }
}
