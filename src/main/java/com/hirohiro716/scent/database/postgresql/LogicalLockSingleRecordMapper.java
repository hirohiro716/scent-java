package com.hirohiro716.scent.database.postgresql;

import java.sql.SQLException;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.StringObject;

/**
 * PostgreSQLデータベースの単一レコードとオブジェクトを論理ロックでマップするための抽象クラス。
 */
public abstract class LogicalLockSingleRecordMapper extends com.hirohiro716.scent.database.SingleRecordMapper {
    
    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public LogicalLockSingleRecordMapper(PostgreSQL database) {
        super(database);
    }

    @Override
    public PostgreSQL getDatabase() {
        return (PostgreSQL) super.getDatabase();
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
        StringObject sql = new StringObject("SELECT * FROM ");
        sql.append(this.getTable().getPhysicalName());
        sql.append(" WHERE ");
        sql.append(this.getWhereSet().buildPlaceholderClause());
        sql.append(" FOR UPDATE NOWAIT;");
        return this.getDatabase().fetchRecord(sql.toString(), this.getWhereSet().buildParameters());
    }
}
