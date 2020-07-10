package com.hirohiro716.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;

/**
 * データベースの単一レコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 * 
 */
public abstract class SingleRecordMapper extends RecordMapper {
    
    /**
     * コンストラクタ。<br>
     * 接続済みのデータベースインスタンスを指定する。
     * 
     * @param database
     */
    public SingleRecordMapper(Database database) {
        super(database);
        this.setRecord(this.createDefaultRecord());
    }
    
    /**
     * このインスタンスにマップされているレコードを取得する。
     * 
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> DynamicArray<C> getRecord() {
        return (DynamicArray<C>) super.getRecords()[0];
    }
    
    /**
     * このインスタンスにマップされているレコードを、指定されたレコードに置き換える。
     * 
     * @param record
     */
    public <C extends ColumnInterface> void setRecord(DynamicArray<C> record) {
        List<DynamicArray<C>> records = new ArrayList<>();
        records.add(record);
        super.setRecords(records);
    }
    
    /**
     * 新しいレコードをデータベースに追加する。
     * 
     * @throws SQLException
     */
    public void insert() throws SQLException {
        this.getDatabase().insert(this.getRecord(), this.getTable());
    }
    
    /**
     * マップするレコードを排他制御を行ってから連想配列で取得する。
     * 
     * @return 結果。
     * @throws SQLException
     */
    protected abstract DynamicArray<String> fetchRecordForEdit() throws SQLException;
    
    @SuppressWarnings("unchecked")
    protected DynamicArray<String>[] fetchRecordsForEdit(String[] orderByColumnsForEdit) throws SQLException {
        return new DynamicArray[] {this.fetchRecordForEdit()};
    }
    
    /**
     * マップされたレコードが、すでに削除処理されている場合trueを返す。<br>
     * 論理削除仕様テーブルの場合にtrueかfalseを返す。物理削除仕様テーブルの場合は常にfalseを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isDeleted();
    
    @Override
    public void edit() throws SQLException {
        super.edit();
        if (this.isDeleted()) {
            throw new DataNotFoundException();
        }
    }

    /**
     * データベースのレコードを、このインスタンスにマップされている連想配列の内容で上書きする。
     * 
     * @throws SQLException 
     */
    @Override
    public void update() throws SQLException {
        this.getDatabase().update(this.getRecord(), this.getTable(), this.getWhereSet());
    }
    
    @Override
    public boolean isPermittedUpdateWhenEmptySearchCondition() {
        return false;
    }
    
    /**
     * データベースのレコードを物理削除する。
     * 
     * @throws SQLException
     * @throws DataNotFoundException
     */
    protected void physicalDelete() throws SQLException, DataNotFoundException {
        StringObject sql = new StringObject("DELETE FROM ");
        sql.append(this.getTable().getPhysicalName());
        if (this.getWhereSet() == null) {
            throw new SQLException("Search condition for updating has not been specified.");
        }
        sql.append(" WHERE ");
        sql.append(this.getWhereSet().buildPlaceholderClause());
        sql.append(";");
        this.getDatabase().execute(sql.toString(), this.getWhereSet().buildParameters());
    }
    
    /**
     * マップされているレコードを削除処理する。
     * 
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public abstract void delete() throws SQLException, DataNotFoundException;
    
    @Override
    protected String[] getOrderByColumnsForEdit() {
        return null;
    }

    @Deprecated
    public <C extends ColumnInterface> DynamicArray<C>[] getRecords() {
        return super.getRecords();
    }
    
    @Deprecated
    public <C extends ColumnInterface> void addRecord(DynamicArray<C> record) {
        super.addRecord(record);
    }

    @Deprecated
    public final <C extends ColumnInterface> void setRecords(Collection<DynamicArray<C>> records) {
        this.setRecord(records.iterator().next());
    }
    
    @Deprecated
    public final <C extends ColumnInterface> void setRecords(DynamicArray<C>[] records) {
        this.setRecord(records[0]);
    }
    
    @Deprecated
    public <C extends ColumnInterface> void removeRecord(DynamicArray<C> record) {
        super.removeRecord(record);
    }
    
    @Deprecated
    public void clearRecords() {
        super.clearRecords();
    }
}
