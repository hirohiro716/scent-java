package com.hirohiro716.scent.database.sqlite;

import java.sql.SQLException;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.database.Database;
import com.hirohiro716.scent.database.sqlite.SQLite.IsolationLevel;

/**
 * SQLiteデータベースの単一レコードとオブジェクトを楽観的ロックでマップするための抽象クラス。
 */
public abstract class OptimisticLockSingleRecordMapper extends com.hirohiro716.scent.database.SingleRecordMapper {
    
    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public OptimisticLockSingleRecordMapper(SQLite database) {
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
    protected DynamicArray<String> fetchCurrentRecordForDetectConflict() throws SQLException {
        if (this.getDatabase().getIsolationLevel() == IsolationLevel.NOLOCK) {
            throw new SQLException(Database.ERROR_MESSAGE_TRANSACTION_NOT_BEGUN);
        }
        return this.fetchRecordForEdit();
    }
}
