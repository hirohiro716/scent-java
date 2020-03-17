package com.hirohiro716.database.sqlite;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.database.ColumnInterface;
import com.hirohiro716.database.DataNotFoundException;
import com.hirohiro716.database.sqlite.SQLite.IsolationLevel;
import com.hirohiro716.filesystem.File;

/**
 * PostgreSQLデータベースの単一レコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 *
 * @param <C> カラムの型。
 */
public abstract class SingleRecordMapper<C extends ColumnInterface> extends com.hirohiro716.database.SingleRecordMapper<C> implements Closeable {
    
    /**
     * コンストラクタ。
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

    @Override
    public void edit() throws SQLException {
        if (this.isEditing) {
            return;
        }
        super.edit();
        try (SQLite sqlite = new SQLite(this.getDatabase().getJDBCDriverURL())) {
            sqlite.connect(this.getDatabase().getDatabaseFile());
            sqlite.begin(IsolationLevel.EXCLUSIVE);
            if (this.isEditingByOther(sqlite)) {
                throw new SQLException(SQLite.ERROR_MESSAGE_EDITING_FAILURE);
            }
            this.updateToEditing(sqlite);
            sqlite.commit();
            this.isEditing = true;
        }
    }
    
    /**
     * マップしようとしているレコードが、ほかで編集中かどうかを判定するメソッド。<br>
     * このメソッドは編集処理時に自動的に呼び出され、編集できるかの判定に使用される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @return ほかで編集中の場合trueを返す。
     * @throws SQLException 
     */
    public abstract boolean isEditingByOther(SQLite sqlite) throws SQLException;
    
    /**
     * マップしたレコードをSQLiteデータベース上で編集中としてマークし、ほかのインスタンスからの編集を拒否する。<br>
     * このメソッドは編集処理時に自動的に呼び出される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @throws SQLException 
     * @throws DataNotFoundException 
     */
    protected abstract void updateToEditing(SQLite sqlite) throws SQLException, DataNotFoundException;
    
    /**
     * マップしたレコードのSQLiteデータベース上での編集中マークを解除する。<br>
     * このメソッドはclose()メソッドから自動的に呼び出される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @throws SQLException
     * @throws DataNotFoundException 
     */
    protected abstract void updateToEditingFinish(SQLite sqlite) throws SQLException, DataNotFoundException;
    
    private boolean isEditing = false;
    
    @Override
    public void close() throws IOException {
        try {
            if (this.isEditing) {
                URL jdbcDriverURL = this.getDatabase().getJDBCDriverURL();
                File databaseFile = this.getDatabase().getDatabaseFile();
                this.getDatabase().close();
                try (SQLite sqlite = new SQLite(jdbcDriverURL)) {
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
}
