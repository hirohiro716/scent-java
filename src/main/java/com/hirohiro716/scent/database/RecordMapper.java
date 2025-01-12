package com.hirohiro716.scent.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.datetime.Datetime;

/**
 * データベースのレコードとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
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
     * @return
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
     * @return
     */
    public abstract TableInterface getTable();
    
    /**
     * レコードが保存されているテーブルを取得する。
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
     * レコードに含まれるすべてのカラムを取得する。
     * 
     * @return
     */
    public ColumnInterface[] getColumns() {
        return this.getTable().getColumns();
    }
    
    /**
     * 初期値が入力されたレコードの配列を作成する。
     * 
     * @param <C> 
     * @return
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
     * @return
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
     * 指定されたレコードの識別子を取得する。
     * 
     * @param <C> 
     * @param record
     * @return
     */
    public abstract String getIdentifier(DynamicArray<String> record);

    /**
     * 指定されたレコードの最終更新日時を取得する。更新日時の概念が無い場合はnullを返す。
     * 
     * @param <C>
     * @param record
     * @return
     */
    protected abstract Datetime getLastUpdateTime(DynamicArray<String> record);

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
     * @return
     * @throws SQLException
     */
    protected abstract DynamicArray<String>[] fetchRecordsForEdit(String[] orderByColumnsForEdit) throws SQLException;

    private DynamicArray<String>[] preEditRecords = null;

    /**
     * コンフリクトの検出に使用される、編集開始時のデータベースレコードのクローンを取得する。
     * 
     * @return
     */
    public DynamicArray<String>[] getPreEditRecords() {
        return this.preEditRecords;
    }

    /**
     * コンフリクトの検出に使用される、編集開始時のデータベースレコードのをセットする。
     * 
     * @param records
     */
    public void setPreEditRecords(DynamicArray<String>[] records) {
        this.preEditRecords = records;
    }
                                                                        
    /**
     * データベースからレコードを、排他制御を行ってから、このインスタンスにマップする。
     * 
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void edit() throws SQLException {
        List<DynamicArray<String>> preEditRecords = new ArrayList<>();
        List<DynamicArray<ColumnInterface>> records = new ArrayList<>();
        DynamicArray<String>[] fetchedRecords = this.fetchRecordsForEdit(this.getOrderByColumnsForEdit());
        for (DynamicArray<String> fetchedRecord : fetchedRecords) {
            preEditRecords.add(fetchedRecord);
            DynamicArray<ColumnInterface> record = this.getTable().createRecord(fetchedRecord);
            records.add(record);
        }
        this.preEditRecords = preEditRecords.toArray(new DynamicArray[] {});
        this.setRecords(records);
    }

    /**
     * 指定されたレコードが、編集開始時のデータベースレコードと同じ内容の場合はtrueを返す。
     * 
     * @param <C> 
     * @param record
     */
    public <C extends ColumnInterface> boolean isSameAsPreEditRecord(DynamicArray<C> record) {
        DynamicArray<String> stringKeyRecord = new DynamicArray<>();
        for (C column : record.getKeys()) {
            stringKeyRecord.put(column.getPhysicalName(), record.get(column));
        }
        String id = this.getIdentifier(stringKeyRecord);
        for (DynamicArray<String> stringKeyPreEditRecord: this.preEditRecords) {
            if (id.equals(this.getIdentifier(stringKeyPreEditRecord))) {
                for (String physicalName : stringKeyPreEditRecord.getKeys()) {
                    if (StringObject.newInstance(stringKeyRecord.getString(physicalName)).equals(stringKeyPreEditRecord.getString(physicalName)) == false) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean isConflictIgnored = false;

    /**
     * コンフリクトを無視する場合はtrueを返す。
     * 
     * @return
     */
    public boolean isConflictIgnored() {
        return this.isConflictIgnored;
    }

    /**
     * コンフリクトを無視する場合はtrueをセットする。
     * 
     * @param isConflictIgnored
     */
    public void setConflictIgnored(boolean isConflictIgnored) {
        this.isConflictIgnored = isConflictIgnored;
    }

    /**
     * コンフリクトを検出する。
     * 
     * @throws RecordConflictException データベースレコードがコンフリクトした場合。
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    protected void detectConflict() throws RecordConflictException, SQLException {
        if (this.isConflictIgnored == false) {
            if (this.preEditRecords == null) {
                throw new SQLException("No pre-edit record has been set.");
            }
            Map<String, DynamicArray<String>> mapOfIdentifierAndPreEditRecord = new HashMap<>();
            for (DynamicArray<String> preEditRecord : this.preEditRecords) {
                mapOfIdentifierAndPreEditRecord.put(this.getIdentifier(preEditRecord), preEditRecord);
            }
            List<DynamicArray<ColumnInterface>> conflictRecords = new ArrayList<>();
            Map<String, DynamicArray<String>> mapOfIdentifierAndCurrentDatabaseRecord = new HashMap<>();
            for (DynamicArray<String> currentDatabaseRecord : this.fetchRecordsForEdit(this.getOrderByColumnsForEdit())) {
                String id = this.getIdentifier(currentDatabaseRecord);
                if (mapOfIdentifierAndPreEditRecord.containsKey(id)) {
                    mapOfIdentifierAndCurrentDatabaseRecord.put(id, currentDatabaseRecord);
                } else {
                    conflictRecords.add(this.getTable().createRecord(currentDatabaseRecord));
                }
            }
            List<DynamicArray<ColumnInterface>> deletedRecords = new ArrayList<>();
            for (DynamicArray<String> preEditRecord : this.preEditRecords) {
                String id = this.getIdentifier(preEditRecord);
                DynamicArray<String> currentDatabaseRecord = mapOfIdentifierAndCurrentDatabaseRecord.get(id);
                if (currentDatabaseRecord != null) {
                    Datetime preEditRecordUpdateTime = this.getLastUpdateTime(preEditRecord);
                    Datetime currentDatabaseRecordUpdateTime = this.getLastUpdateTime(currentDatabaseRecord);
                    if (preEditRecordUpdateTime != null && currentDatabaseRecordUpdateTime != null && preEditRecordUpdateTime.getAllMilliSecond() < currentDatabaseRecordUpdateTime.getAllMilliSecond()) {
                        conflictRecords.add(this.getTable().createRecord(currentDatabaseRecord));
                    }
                } else {
                    deletedRecords.add(this.getTable().createRecord(preEditRecord));
                }
            }
            if (conflictRecords.size() > 0) {
                throw new RecordConflictException(conflictRecords.toArray(new DynamicArray[] {}), null);
            }
            if (deletedRecords.size() > 0) {
                Map<String, DynamicArray<ColumnInterface>> mapOfIdentifierAndRecord = new HashMap<>();
                for (DynamicArray<ColumnInterface> record : this.getRecords()) {
                    DynamicArray<String> stringKeyRecord = RecordMapper.createStringKeyRecord(record);
                    String id = this.getIdentifier(stringKeyRecord);
                    mapOfIdentifierAndRecord.put(id, record);
                }
                for (DynamicArray<ColumnInterface> deletedRecord : deletedRecords) {
                    DynamicArray<String> stringKeyRecord = RecordMapper.createStringKeyRecord(deletedRecord);
                    String id = this.getIdentifier(stringKeyRecord);
                    if (mapOfIdentifierAndRecord.containsKey(id)) {
                        throw new RecordConflictException("編集中のレコードがほかの操作で削除され競合が発生しました。", null, deletedRecords.toArray(new DynamicArray[] {}));
                    }
                }
            }
        }
    }
    
    /**
     * 編集、削除するレコードを特定するための検索条件が未指定の場合でも、更新を許可している場合はtrueを返す。
     * 
     * @return
     */
    public abstract boolean isPermittedUpdateWhenEmptySearchCondition();
    
    /**
     * データベースのレコードを、このインスタンスにマップされている連想配列の内容に置き換える。
     * 
     * @throws RecordConflictException データベースレコードがコンフリクトした場合。
     * @throws SQLException
     */
    public void update() throws SQLException {
        this.detectConflict();
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
     * @return
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

    /**
     * キーがカラム列挙型のレコード配列から、キーがカラム文字列の配列を作成する。
     * 
     * @param <C>
     * @param record
     * @return
     */
    public static <C extends ColumnInterface> DynamicArray<String> createStringKeyRecord(DynamicArray<C> record) {
        DynamicArray<String> stringKeyRecord = new DynamicArray<>();
        for (C column : record.getKeys()) {
            stringKeyRecord.put(column.getPhysicalName(), record.get(column));
        }
        return stringKeyRecord;
    }
}
