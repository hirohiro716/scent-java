package com.hirohiro716.scent.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.StringObject;

/**
 * データベースのレコードと検索するための抽象クラス。
 */
public abstract class RecordSearcher {

    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public RecordSearcher(Database database) {
        this.database = database;
    }
    
    private Database database;
    
    /**
     * コンストラクタで指定したDatabaseインスタンスを取得する。
     * 
     * @return
     */
    public Database getDatabase() {
        return this.database;
    }
    
    /**
     * レコードを検索するテーブルを取得する。
     * 
     * @return
     */
    public abstract TableInterface getTable();
    
    /**
     * レコードを検索するテーブルを取得する。
     * 
     * @param <M>
     * @param <D>
     * @param recordMapperClass
     * @param databaseClass
     * @return
     */
    public static <M extends RecordMapper, D extends Database> TableInterface getTable(Class<M> recordMapperClass, Class<D> databaseClass) {
        try {
            Database database = null;
            M instance = recordMapperClass.getConstructor(databaseClass).newInstance(database);
            return instance.getTable();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * 指定されたSELECT句、WHERE句の後の構文、検索条件を使用してレコードを検索する。
     * 
     * @param selectSQL WHERE句の前までのSELECT句からFROM句までのSQL。
     * @param partAfterWhere WHERE句の後の構文。GROUP BY句、HAVING句、ORDER BY句、LIMIT句など。
     * @param whereSets 検索条件。複数の指定がある場合はOR演算子で連結される。
     * @return 検索結果。
     * @throws SQLException
     */
    public final DynamicArray<String>[] search(String selectSQL, String partAfterWhere, WhereSet... whereSets) throws SQLException {
        StringObject sql = new StringObject();
        if (selectSQL != null && selectSQL.trim().length() > 0) {
            sql.append(selectSQL);
        } else {
            sql.append("SELECT * FROM ");
            sql.append(this.getTable().getPhysicalName());
        }
        StringObject where = new StringObject();
        List<Object> parameters = new ArrayList<>();
        if (whereSets != null && whereSets.length > 0) {
            for (WhereSet whereSet: whereSets) {
                if (whereSet.getWheres().size() > 0) {
                    if (where.length() == 0) {
                        where.append(" WHERE ");
                    } else {
                        where.append(" OR ");
                    }
                    where.append(whereSet.buildPlaceholderClause());
                    for (Object parameter: whereSet.buildParameters()) {
                        parameters.add(parameter);
                    }
                }
            }
        }
        sql.append(where);
        sql.append(" ");
        sql.append(partAfterWhere);
        sql.append(";");
        return this.getDatabase().fetchRecords(sql.toString(), parameters.toArray(new Object[] {}));
    }
    
    /**
     * 指定されたWHERE句の後の構文、検索条件を使用してレコードを検索する。
     * 
     * @param partAfterWhere WHERE句の後の構文。GROUP BY句、HAVING句、ORDER BY句、LIMIT句など。
     * @param whereSets 検索条件。複数の指定がある場合はOR演算子で連結される。
     * @return 検索結果。
     * @throws SQLException
     */
    public DynamicArray<String>[] search(String partAfterWhere, WhereSet... whereSets) throws SQLException {
        return this.search(null, partAfterWhere, whereSets);
    }
    
    /**
     * 指定された検索条件を使用してレコードを検索する。
     * 
     * @param whereSets 検索条件。複数の指定がある場合はOR演算子で連結される。
     * @return 検索結果。
     * @throws SQLException
     */
    public DynamicArray<String>[] search(WhereSet... whereSets) throws SQLException {
        return this.search(null, whereSets);
    }
}
