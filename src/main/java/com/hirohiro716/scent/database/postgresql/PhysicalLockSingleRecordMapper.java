package com.hirohiro716.scent.database.postgresql;

import java.sql.SQLException;
import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.datetime.Datetime;

/**
 * PostgreSQLデータベースの単一レコードとオブジェクトを物理ロックでマップするための抽象クラス。
 * 
 * @author hiro
*/
public abstract class PhysicalLockSingleRecordMapper extends com.hirohiro716.scent.database.SingleRecordMapper {
    
    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public PhysicalLockSingleRecordMapper(PostgreSQL database) {
        super(database);
        this.setConflictIgnored(true);
    }

    @Override
    public PostgreSQL getDatabase() {
        return (PostgreSQL) super.getDatabase();
    }
    
    @Override
    public String getIdentifier(DynamicArray<String> record) {
        return null;
    }

    @Override
    protected Datetime getLastUpdateTime(DynamicArray<String> record) {
        return null;
    }
    
    @Override
    protected DynamicArray<String> fetchRecordForEdit() throws SQLException {
        StringObject sql = new StringObject("SELECT * FROM ");
        sql.append(this.getTable().getPhysicalName());
        sql.append(" WHERE ");
        sql.append(this.getWhereSet().buildPlaceholderClause());
        sql.append(" FOR UPDATE NOWAIT;");
        return this.getDatabase().fetchRecord(sql.toString(), this.getWhereSet().buildParameters());
    }
}
