package com.hirohiro716.database.postgresql;

import java.sql.SQLException;
import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.database.ColumnInterface;

/**
 * PostgreSQLデータベースのレコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 *
 * @param <C> カラムの型。
 */
public abstract class RecordMapper<C extends ColumnInterface> extends com.hirohiro716.database.RecordMapper<C> {
    
    /**
     * コンストラクタ。
     * 
     * @param database
     */
    public RecordMapper(PostgreSQL database) {
        super(database);
    }
    
    @Override
    public PostgreSQL getDatabase() {
        return (PostgreSQL) super.getDatabase();
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
            this.getDatabase().lockTableReadonly(this.getTable().getPhysicalName());
            return this.getDatabase().fetchRecords(sql.toString());
        }
        sql.append(" WHERE ");
        sql.append(this.getWhereSet().buildPlaceholderClause());
        sql.append(" ");
        sql.append(orderBy);
        sql.append(" FOR UPDATE NOWAIT;");
        return this.getDatabase().fetchRecords(sql.toString(), this.getWhereSet().buildParameters());
    }
}
