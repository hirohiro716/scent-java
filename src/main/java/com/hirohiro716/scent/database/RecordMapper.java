package com.hirohiro716.scent.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.StringObject;

/**
 * データベースのレコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 * 
 */
public abstract class RecordMapper {
    
    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public RecordMapper(Database database) {
        this.database = database;
    }
    
    private Database database;
    
    /**
     * コンストラクタで指定したDatabaseインスタンスを取得する。
     * 
     * @return 結果。
     */
    public Database getDatabase() {
        return this.database;
    }
    
    /**
     * コンストラクタで指定したDatabaseインスタンスを新たにセットする。
     * 
     * @param database
     */
    public void setDatabase(Database database) {
        this.database = database;
    }
    
    /**
     * レコードが保存されているテーブルを取得する。
     * 
     * @return 結果。
     */
    public abstract TableInterface getTable();
    
    /**
     * レコードが保存されているテーブルを取得する。
     * 
     * @param <M>
     * @param <D>
     * @param recordMapperClass
     * @param databaseClass
     * @return 結果。
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
     * レコードに含まれるすべてのカラムを取得する。
     * 
     * @return 結果。
     */
    public ColumnInterface[] getColumns() {
        return this.getTable().getColumns();
    }
    
    /**
     * 初期値が入力されたレコードの配列を作成する。
     * 
     * @param <C> 
     * @return 結果。
     */
    public <C extends ColumnInterface> DynamicArray<C> createDefaultRecord() {
        return this.getTable().createRecord();
    }
    
    private WhereSet whereSet = null;
    
    /**
     * マップするレコードを特定するための検索条件を取得する。
     * 
     * @return WhereSet
     */
    public WhereSet getWhereSet() {
        return this.whereSet;
    }
    
    /**
     * マップするレコードを特定するための検索条件をセットする。
     * 
     * @param whereSet
     */
    public void setWhereSet(WhereSet whereSet) {
        this.whereSet = whereSet;
    }
    
    private List<DynamicArray<ColumnInterface>> editingRecords = new ArrayList<>();
    
    /**
     * このインスタンスにマップされているレコードを取得する。
     * 
     * @param <C> 
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> DynamicArray<C>[] getRecords() {
        return this.editingRecords.toArray(new DynamicArray[] {});
    }
    
    /**
     * このインスタンスにマップされているレコードに、指定されたレコードを追加する。
     * 
     * @param <C> 
     * @param record
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> void addRecord(DynamicArray<C> record) {
        this.editingRecords.add((DynamicArray<ColumnInterface>) record);
    }
    
    /**
     * このインスタンスにマップされているレコードを、指定されたレコードに置き換える。
     * 
     * @param <C> 
     * @param records
     */
    public <C extends ColumnInterface> void setRecords(Collection<DynamicArray<C>> records) {
        this.editingRecords.clear();
        for (DynamicArray<C> record : records) {
            this.addRecord(record);
        }
    }
    
    /**
     * このインスタンスにマップされているレコードを、指定されたレコードに置き換える。
     * 
     * @param <C> 
     * @param records
     */
    public <C extends ColumnInterface> void setRecords(DynamicArray<C>[] records) {
        this.editingRecords.clear();
        DynamicArray<Integer> arrayRecords = new DynamicArray<>(records);
        this.setRecords(arrayRecords.getValues());
    }
    
    /**
     * このインスタンスにマップされているレコードから、指定されたレコードを削除する。
     * 
     * @param <C> 
     * @param record
     */
    public <C extends ColumnInterface> void removeRecord(DynamicArray<C> record) {
        this.editingRecords.remove(record);
    }
    
    /**
     * このインスタンスにマップされているレコードをクリアする。
     */
    public void clearRecords() {
        this.editingRecords.clear();
    }

    /**
     * マップするレコードの並び順を定義する、カラム文字列の配列を取得する。<br>
     * このメソッドを呼び出すと、次のような値と同じ形式の配列を返す。<br>
     * new String[] {"column_name1", "column_name2 ASC", "column_name3 DESC"}
     * 
     * @return 並び順を定義するカラム文字列の配列。またはnull。
     */
    protected abstract String[] getOrderByColumnsForEdit();
    
    /**
     * マップするレコードを排他制御を行ってから連想配列で取得する。
     * 
     * @param orderByColumnsForEdit "column_name1 ASC" や "column_name2 DESC" などのレコードの並び順を定義するカラム文字列の配列。またはnull。
     * @return 結果。
     * @throws SQLException
     */
    protected abstract DynamicArray<String>[] fetchRecordsForEdit(String[] orderByColumnsForEdit) throws SQLException;
    
    /**
     * データベースからレコードを、排他制御を行ってから、このインスタンスにマップする。
     * 
     * @throws SQLException
     */
    public void edit() throws SQLException {
        List<DynamicArray<ColumnInterface>> records = new ArrayList<>();
        for (DynamicArray<String> fetchedRecord : this.fetchRecordsForEdit(this.getOrderByColumnsForEdit())) {
            DynamicArray<ColumnInterface> record = this.getTable().createRecord(fetchedRecord);
            records.add(record);
        }
        this.setRecords(records);
    }
    
    /**
     * 編集、削除するレコードを特定するための検索条件が未指定の場合でも、更新を許可している場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isPermittedUpdateWhenEmptySearchCondition();
    
    /**
     * データベースのレコードを、このインスタンスにマップされている連想配列の内容に置き換える。
     * 
     * @throws SQLException
     */
    public void update() throws SQLException {
        StringObject sql = new StringObject("DELETE FROM ");
        sql.append(this.getTable().getPhysicalName());
        if (this.getWhereSet() == null) {
            if (this.isPermittedUpdateWhenEmptySearchCondition() == false) {
                throw new SQLException("Search condition for updating has not been specified.");
            }
            sql.append(";");
            this.getDatabase().execute(sql.toString());
        } else {
            sql.append(" WHERE ");
            sql.append(this.getWhereSet().buildPlaceholderClause());
            sql.append(";");
            this.getDatabase().execute(sql.toString(), this.getWhereSet().buildParameters());
        }
        for (DynamicArray<ColumnInterface> record : this.getRecords()) {
            this.getDatabase().insert(record, this.getTable());
        }
    }
    
    /**
     * マップ対象を特定するための検索条件に該当するレコードが、データベースに存在する場合はtrueを返す。
     * 
     * @return 結果。
     * @throws SQLException
     */
    public boolean exists() throws SQLException {
        StringObject sql = new StringObject("SELECT COUNT(*) FROM ");
        sql.append(this.getTable().getPhysicalName());
        sql.append(" WHERE ");
        sql.append(this.getWhereSet().buildPlaceholderClause());
        sql.append(";");
        Integer numberOfRecords = StringObject.newInstance(this.getDatabase().fetchField(sql.toString(), this.getWhereSet().buildParameters())).toInteger();
        if (numberOfRecords != null && numberOfRecords > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * マップされているレコードが有効か検証する。
     * 
     * @throws ValidationException
     * @throws Exception
     */
    public abstract void validate() throws ValidationException, Exception;
    
    /**
     * マップされているレコードの値を標準化する。
     * 
     * @throws Exception
     */
    public abstract void normalize() throws Exception;
}
