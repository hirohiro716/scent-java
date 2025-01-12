package com.hirohiro716.scent.database.sqlite;

import java.sql.SQLException;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.database.Database;
import com.hirohiro716.scent.database.sqlite.SQLite.IsolationLevel;

/**
 * SQLiteデータベースのレコードとオブジェクトを楽観的ロックでマップするための抽象クラス。
 * 
 * @author hiro
*/
public abstract class OptimisticLockRecordMapper extends com.hirohiro716.scent.database.RecordMapper {
    
    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public OptimisticLockRecordMapper(SQLite database) {
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

    @Override
    protected DynamicArray<String>[] fetchCurrentRecordsForDetectConflict() throws SQLException {
        if (this.getDatabase().getIsolationLevel() == IsolationLevel.NOLOCK) {
            throw new SQLException(Database.ERROR_MESSAGE_TRANSACTION_NOT_BEGUN);
        }
        return this.fetchRecordsForEdit(null);
    }
}
