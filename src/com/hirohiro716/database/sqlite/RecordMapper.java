package com.hirohiro716.database.sqlite;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.database.sqlite.SQLite.IsolationLevel;
import com.hirohiro716.filesystem.File;

/**
 * SQLiteデータベースのレコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class RecordMapper extends com.hirohiro716.database.RecordMapper implements Closeable {
    
    /**
     * コンストラクタ。
     * 
     * @param database
     */
    public RecordMapper(SQLite database) {
        super(database);
    }
    
    @Override
    public SQLite getDatabase() {
        return (SQLite) super.getDatabase();
    }
    
    @Override
    protected DynamicArray<String>[] fetchRecordsForEdit(String[] orderByColumnsForEdit) throws SQLException {
        StringObject orderBy = new StringObject();
        if (orderByColumnsForEdit != null && orderByColumnsForEdit.length > 0) {
            orderBy.append("ORDER BY ");
            for (String orderByColumn: orderByColumnsForEdit) {
                if (orderBy.length() > 9) {
                    orderBy.append(", ");
                }
                orderBy.append(orderByColumn);
            }
        }
        StringObject sql = new StringObject("SELECT * FROM ");
        sql.append(this.getTable().getPhysicalName());
        if (this.getWhereSet() == null) {
            sql.append(" ");
            sql.append(orderBy);
            sql.append(";");
            return this.getDatabase().fetchRecords(sql.toString());
        }
        sql.append(" WHERE ");
        sql.append(this.getWhereSet().buildPlaceholderClause());
        sql.append(" ");
        sql.append(orderBy);
        sql.append(";");
        return this.getDatabase().fetchRecords(sql.toString(), this.getWhereSet().buildParameters());
    }
    
    /**
     * マップしようとしているレコードが、ほかで編集中かどうかを判定するメソッド。<br>
     * このメソッドはスーパークラスの編集処理時に自動的に呼び出され、編集できるかの判定に使用される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @return ほかで編集中の場合trueを返す。
     * @throws SQLException 
     */
    public abstract boolean isEditingByOther(SQLite sqlite) throws SQLException;
    
    /**
     * マップしたレコードをSQLiteデータベース上で編集中としてマークし、ほかのインスタンスからの編集を拒否する。<br>
     * このメソッドはスーパークラスの編集処理時に自動的に呼び出される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @throws SQLException 
     */
    protected abstract void updateToEditing(SQLite sqlite) throws SQLException;
    
    /**
     * マップしたレコードのSQLiteデータベース上での編集中マークを解除する。<br>
     * このメソッドはスーパークラスの閉じる処理で自動的に呼び出される。
     * 
     * @param sqlite 分離レベルEXCLUSIVEでトランザクションが開始されたSQLite。
     * @throws SQLException
     */
    protected abstract void updateToEditingFinish(SQLite sqlite) throws SQLException;
    
    /**
     * データベースに対して排他処理を行うための新しいデータベースインスタンスを作成する。<br>
     * 接続処理はスーパークラスで自動的に行われる。
     * 
     * @return 結果。
     */
    public abstract SQLite createDatabaseForEditing();

    private boolean isEditing = false;
    
    /**
     * データベースからレコードを、排他制御を行ってから、このインスタンスにマップする。<br>
     * SQLiteでは、編集時と編集解除時にデータベースをロックして処理しているため、このメソッド以前にロックしている場合は処理が停止する。
     */
    @Override
    public void edit() throws SQLException {
        if (this.isEditing) {
            return;
        }
        super.edit();
        try (SQLite sqlite = this.createDatabaseForEditing()) {
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
            this.clearRecords();
        } catch (SQLException exception) {
            throw new IOException(exception);
        }
    }
}
