package com.hirohiro716.database.postgresql;

import java.sql.SQLException;
import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.database.ColumnInterface;

/**
 * PostgreSQLデータベースの単一レコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 *
 * @param <C> カラムの型。
 */
public abstract class SingleRecordMapper<C extends ColumnInterface> extends com.hirohiro716.database.SingleRecordMapper<C> {
    
    /**
     * コンストラクタ。
     * 
     * @param database
     */
    public SingleRecordMapper(PostgreSQL database) {
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
        sql.append(" FOR UPDATE NOWAIT;");
        return this.getDatabase().fetchRecord(sql.toString(), this.getWhereSet().buildParameters());
    }
}
