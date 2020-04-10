package com.hirohiro716.database;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.datetime.Datetime;

/**
 * JDBCドライバでデータベースに接続するための抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class Database implements Closeable {
    
    /**
     * データベースでエラーが発生した場合のユーザーインターフェース向けの表題。
     */
    public static final String ERROR_DIALOG_TITLE = "データベースエラー";
    
    /**
     * データベースからの情報の取得に失敗した場合のユーザーインターフェース向けの表題。
     */
    public static final String ERROR_DIALOG_TITLE_GET_FAILURE = "情報取得の失敗";
    
    /**
     * データベース情報の編集が排他制御によって失敗した場合のユーザーインターフェース向けのメッセージ。
     */
    public static final String ERROR_MESSAGE_EDITING_FAILURE = "ほかのユーザーが編集中です。";
    
    /**
     * トランザクションが必須な状況で、トランザクションが開始されていない場合のユーザーインターフェース向けのメッセージ。
     */
    public static final String ERROR_MESSAGE_TRANSACTION_NOT_BEGUN = "トランザクションが開始されていません。";
        
    /**
     * JDBCドライバのURLを取得する。
     * 
     * @return 結果。
     * @throws Exception 
     */
    public abstract URL getJDBCDriverURL() throws Exception;
    
    /**
     * JDBCドライバのバイナリ名を取得する。
     * 
     * @return 結果。
     */
    protected abstract String getJDBCDriverBinaryName();
    
    /**
     * 接続文字列が指定するデータベースに接続する。
     * 
     * @param connectionString 接続文字列。
     * @throws SQLException
     */
    protected void connect(String connectionString) throws SQLException {
        try {
            this.connect(this.getJDBCDriverURL(), this.getJDBCDriverBinaryName(), connectionString);
        } catch (Exception exception) {
            throw new SQLException(exception);
        }
    }
    
    /**
     * JDBCドライバを読み込んでデータベースに接続する。
     * 
     * @param driverURL JDBCドライバを含むjarファイルのURL。
     * @param driverName JDBCドライバのバイナリ名。
     * @param connectionString 接続文字列。
     * @throws ReflectiveOperationException
     * @throws IllegalAccessException
     * @throws RuntimeException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    protected void connect(URL driverURL, String driverName, String connectionString) throws ReflectiveOperationException, IllegalAccessException, RuntimeException, ClassNotFoundException, SQLException {
        try {
            DriverManager.getDriver(connectionString);
        } catch (SQLException exception) {
            this.registerDriver(driverURL, driverName);
        }
        this.connection = DriverManager.getConnection(connectionString);
    }
    
    /**
     * JDBCドライバを読み込んでDriverManagerに登録する。
     * 
     * @param driverURL JDBCドライバを含むjarファイルのURL。
     * @param driverName JDBCドライバのバイナリ名。
     * @throws ReflectiveOperationException
     * @throws IllegalAccessException
     * @throws RuntimeException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void registerDriver(URL driverURL, String driverName) throws ReflectiveOperationException, IllegalAccessException, RuntimeException, ClassNotFoundException, SQLException {
        if (driverURL == null) {
            return;
        }
        URLClassLoader loader = new URLClassLoader(new URL[] { driverURL });
        Driver driver = (Driver) Class.forName(driverName, true, loader).getConstructor().newInstance();
        Driver driverForRegister = new Driver() {
            
            @Override
            public boolean jdbcCompliant() {
                return driver.jdbcCompliant();
            }
            
            @Override
            public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
                return driver.getPropertyInfo(url, info);
            }
            
            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return driver.getParentLogger();
            }
            
            @Override
            public int getMinorVersion() {
                return driver.getMinorVersion();
            }
            
            @Override
            public int getMajorVersion() {
                return driver.getMajorVersion();
            }
            
            @Override
            public Connection connect(String url, Properties info) throws SQLException {
                return driver.connect(url, info);
            }
            
            @Override
            public boolean acceptsURL(String url) throws SQLException {
                return driver.acceptsURL(url);
            }
        };
        DriverManager.registerDriver(driverForRegister);
    }
    
    private Connection connection = null;
    
    /**
     * java.sql.Connectionのインスタンスを取得する。
     * 
     * @return 結果。
     */
    public Connection getConnection() {
        return this.connection;
    }
    
    private int queryTimeout = 0;
    
    /**
     * データベースに対する問い合わせをキャンセルするまでの秒数を取得する。
     * 
     * @return 秒数。
     */
    public int getQueryTimeout() {
        return this.queryTimeout;
    }
    
    /**
     * データベースに対する問い合わせをキャンセルするまでの秒数を指定する。
     * 
     * @param queryTimeout 秒数。
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }
    
    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * データベースに対する接続が閉じられている場合trueを返す。
     * 
     * @return 結果。
     * @throws SQLException
     */
    public boolean isClosed() throws SQLException {
        return this.connection == null || this.connection.isClosed();
    }
    
    /**
     * データベースに対して更新系のSQLを実行する。
     * 
     * @param sql
     * @return 影響を与えたレコード数。
     * @throws SQLException
     */
    public int execute(String sql) throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            statement.setQueryTimeout(this.queryTimeout);
            return statement.executeUpdate(sql);
        }
    }
    
    /**
     * データベースに対して更新系のSQLをバインド変数を使用して実行する。
     * 
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 影響を与えたレコード数。
     * @throws SQLException
     */
    public int execute(String sql, Object[] parameters) throws SQLException {
        try (java.sql.PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setQueryTimeout(this.queryTimeout);
            for (int index = 0; index < parameters.length; index++) {
                statement.setObject(index + 1, convertToBindParameter(parameters[index]));
            }
            return statement.executeUpdate();
        }
    }
    
    /**
     * データベースに対して更新系のSQLをバインド変数を使用して実行する。
     * 
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 影響を与えたレコード数。
     * @throws SQLException
     */
    public final int execute(String sql, Collection<Object> parameters) throws SQLException {
        return this.execute(sql, parameters.toArray(new Object[] {}));
    }
    
    /**
     * プリペアドステートメントを使用したクエリの結果の最初の行、最初のフィールドの値を取得する。
     * 
     * @param <T> 取得する型。
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 結果。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    @SuppressWarnings("unchecked")
    public <T> T fetchField(String sql, Object[] parameters) throws SQLException, DataNotFoundException {
        try (java.sql.PreparedStatement statement = this.connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            statement.setQueryTimeout(this.queryTimeout);
            for (int index = 0; index < parameters.length; index++) {
                statement.setObject(index + 1, convertToBindParameter(parameters[index]));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return (T) convertFromResultSetValue(resultSet.getObject(1));
                }
            }
        } catch (SQLException exception) {
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        throw new DataNotFoundException();
    }

    /**
     * プリペアドステートメントを使用したクエリの結果の最初の行、最初のフィールドの値を取得する。
     * 
     * @param <T> 取得する型。
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 結果。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public final <T> T fetchField(String sql, Collection<Object> parameters) throws SQLException, DataNotFoundException {
        return this.fetchField(sql, parameters.toArray(new Object[] {}));
    }
    
    /**
     * クエリの結果の最初の行、最初のフィールドの値を取得する。
     * 
     * @param <T> 取得する型。
     * @param sql
     * @return 結果。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public final <T> T fetchField(String sql) throws SQLException, DataNotFoundException {
        return this.fetchField(sql, new Object[] {});
    }
    
    /**
     * プリペアドステートメントを使用したクエリの結果の最初の行を連想配列で取得する。
     * 
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 結果。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public DynamicArray<String> fetchRecord(String sql, Object[] parameters) throws SQLException, DataNotFoundException {
        try (java.sql.PreparedStatement statement = this.connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            statement.setQueryTimeout(this.queryTimeout);
            for (int index = 0; index < parameters.length; index++) {
                statement.setObject(index + 1, convertFromResultSetValue(parameters[index]));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    DynamicArray<String> row = new DynamicArray<>();
                    ResultSetMetaData meta = resultSet.getMetaData();
                    for (int index = 1; index <= meta.getColumnCount(); index++) {
                        row.put(meta.getColumnName(index), convertFromResultSetValue(resultSet.getObject(index)));
                    }
                    return row;
                }
                throw new DataNotFoundException();
            }
        }
    }
    
    /**
     * プリペアドステートメントを使用したクエリの結果の最初の行を連想配列で取得する。
     * 
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 結果。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public final DynamicArray<String> fetchRecord(String sql, Collection<Object> parameters) throws SQLException, DataNotFoundException {
        return this.fetchRecord(sql, parameters.toArray(new Object[] {}));
    }

    /**
     * クエリの結果の最初の行を連想配列で取得する。
     * 
     * @param sql
     * @return 結果。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public final DynamicArray<String> fetchRecord(String sql) throws SQLException, DataNotFoundException {
        return this.fetchRecord(sql, new Object[] {});
    }
    
    /**
     * プリペアドステートメントを使用したクエリの結果すべてを連想配列で取得する。
     * 
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 結果。
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public DynamicArray<String>[] fetchRecords(String sql, Object[] parameters) throws SQLException {
        try (java.sql.PreparedStatement statement = this.connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            statement.setQueryTimeout(this.queryTimeout);
            for (int index = 0; index < parameters.length; index++) {
                statement.setObject(index + 1, convertFromResultSetValue(parameters[index]));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<DynamicArray<String>> rows = new ArrayList<>();
                while (resultSet.next()) {
                    DynamicArray<String> row = new DynamicArray<>();
                    ResultSetMetaData meta = resultSet.getMetaData();
                    for (int index = 1; index <= meta.getColumnCount(); index++) {
                        row.put(meta.getColumnName(index), convertFromResultSetValue(resultSet.getObject(index)));
                    }
                    rows.add(row);
                }
                return rows.toArray(new DynamicArray[] {});
            }
        }
    }
    
    /**
     * プリペアドステートメントを使用したクエリの結果すべてを連想配列で取得する。
     * 
     * @param sql プレースホルダを使用したSQL。
     * @param parameters バインド変数。
     * @return 結果。
     * @throws SQLException
     */
    public final DynamicArray<String>[] fetchRecords(String sql, Collection<Object> parameters) throws SQLException {
        return this.fetchRecords(sql, parameters.toArray(new Object[] {}));
    }

    /**
     * クエリの結果すべてを連想配列で取得する。
     * 
     * @param sql
     * @return 結果。
     * @throws SQLException
     */
    public final DynamicArray<String>[] fetchRecords(String sql) throws SQLException {
        return this.fetchRecords(sql, new Object[] {});
    }
    
    /**
     * テーブルのレコード数を取得する。
     * 
     * @param tableName
     * @return 結果。
     * @throws SQLException
     */
    public long fetchNumberOfRecords(String tableName) throws SQLException {
        try {
            return this.fetchField(StringObject.join("SELECT COUNT(*) FROM ", tableName, ";").toString());
        } catch (DataNotFoundException exception) {
            return 0;
        }
    }
    
    /**
     * キーにカラム名を持つ連想配列を使用してテーブルにレコードを挿入する。
     * 
     * @param values
     * @param tableName
     * @throws SQLException
     */
    public void insert(DynamicArray<String> values, String tableName) throws SQLException {
        StringObject sql = new StringObject("INSERT INTO ");
        sql.append(tableName);
        sql.append(" (");
        String[] columns = values.getKeys().toArray(new String[] {});
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                sql.append(", ");
            }
            sql.append(columns[index]);
        }
        List<Object> convertedValues = new ArrayList<>();
        for (Object value : values.getValues()) {
            convertedValues.add(convertToBindParameter(value));
        }
        sql.append(") VALUES (");
        for (int index = 0; index < convertedValues.size(); index++) {
            if (index > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(");");
        this.execute(sql.toString(), convertedValues);
    }
    
    /**
     * カラムと挿入する値の連想配列を使用してテーブルにレコードを挿入する。
     * 
     * @param <C>
     * @param values
     * @param table
     * @throws SQLException
     */
    public <C extends ColumnInterface> void insert(DynamicArray<C> values, TableInterface table) throws SQLException {
        DynamicArray<String> stringKeyValues = new DynamicArray<>();
        for (C column : values.getKeys()) {
            stringKeyValues.put(column.getPhysicalName(), values.get(column));
        }
        this.insert(stringKeyValues, table.getPhysicalName());
    }

    /**
     * キーにカラム名を持つ連想配列を使用してテーブルの特定のレコードを更新する。
     * 
     * @param values
     * @param tableName
     * @param whereSet レコードを特定するための検索条件。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public void update(DynamicArray<String> values, String tableName, WhereSet whereSet) throws SQLException, DataNotFoundException {
        StringObject sql = new StringObject("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        String[] columns = values.getKeys().toArray(new String[] {});
        for (int index = 0; index < columns.length; index++) {
            String column = columns[index];
            if (index > 0) {
                sql.append(", ");
            }
            sql.append(column);
            sql.append(" = ?");
        }
        sql.append(" WHERE ");
        sql.append(whereSet.buildPlaceholderClause());
        sql.append(";");
        DynamicArray<Integer> parameters = new DynamicArray<>();
        for (Object value : values.getValues()) {
            parameters.add(convertToBindParameter(value));
        }
        parameters.add(whereSet.buildParameters());
        int result = this.execute(sql.toString(), parameters.getValues());
        if (result == 0) {
            throw new DataNotFoundException();
        }
    }
    
    /**
     * カラムと更新する値の連想配列を使用してテーブルの特定のレコードを更新する。
     * 
     * @param <C>
     * @param values
     * @param table
     * @param whereSet レコードを特定するための検索条件。
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public <C extends ColumnInterface> void update(DynamicArray<C> values, TableInterface table, WhereSet whereSet) throws SQLException, DataNotFoundException {
        DynamicArray<String> stringKeyValues = new DynamicArray<>();
        for (C column : values.getKeys()) {
            stringKeyValues.put(column.getPhysicalName(), values.get(column));
        }
        this.update(stringKeyValues, table.getPhysicalName(), whereSet);
    }
    
    /**
     * 自動コミットが有効な場合はtrueを返す。
     * 
     * @return 結果。
     * @throws SQLException
     */
    public boolean isAutoCommit() throws SQLException {
        return this.connection.getAutoCommit();
    }
    
    /**
     * 自動コミットモードを有効、または無効に設定する。
     * 
     * @param isAutoCommit
     * @throws SQLException
     */
    public void setAutoCommit(boolean isAutoCommit) throws SQLException {
        if (this.isAutoCommit() != isAutoCommit) {
            this.connection.setAutoCommit(isAutoCommit);
        }
    }
    
    /**
     * 前のコミット/ロールバック以降の変更をすべてコミットし、現在保持しているデータベースロックをすべて解除する。<br>
     * このメソッドは、自動コミットモードが無効になっている場合にのみ使用できる。
     * 
     * @throws SQLException
     */
    public void commit() throws SQLException {
        this.connection.commit();
    }
    
    /**
     * 前のコミット/ロールバック以降の変更をすべて元に戻し、現在保持しているデータベースロックをすべて解除する。<br>
     * このメソッドは、自動コミットモードが無効になっている場合にのみ使用できる。
     * 
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        this.connection.rollback();
    }
    
    /**
     * Mapの関連付けを利用してSQLのCASE句を作成する。<br>
     * Mapの中身が{1=aaa, 2=bbb}で実行すると下記のような結果になる。<br>
     * "CASE column_name WHEN 1 THEN 'aaa' WHEN 2 THEN 'bbb' END"
     * 
     * @param map
     * @param columnName
     * @return 結果。
     */
    public static String makeCaseClauseFromHashMap(Map<?, String> map, String columnName) {
        if (map.size() == 0) {
            return columnName;
        }
        StringBuilder builder = new StringBuilder("CASE ");
        builder.append(columnName);
        builder.append(" ");
        for (Object key : map.keySet()) {
            boolean isNumber = key instanceof Number;
            builder.append("WHEN ");
            if (isNumber == false) {
                builder.append("'");
            }
            builder.append(key);
            if (isNumber == false) {
                builder.append("'");
            }
            builder.append(" THEN '");
            builder.append(map.get(key));
            builder.append("' ");
        }
        builder.append("END");
        return builder.toString();
    }
    
    /**
     * Mapの関連付けを利用してSQLのCASE句を作成する。
     * 
     * @param map
     * @param column
     * @return 結果。
     */
    public static String makeCaseClauseFromHashMap(Map<?, String> map, ColumnInterface column) {
        return Database.makeCaseClauseFromHashMap(map, column.getFullPhysicalName());
    }
    
    /**
     * バインド変数に使用できる型に変換する。
     * 
     * @param originalValue
     * @return 変換後の値。
     */
    protected static Object convertToBindParameter(Object originalValue) {
        if (originalValue == null) {
            return null;
        }
        if (originalValue instanceof Datetime) {
            Datetime datatime = (Datetime) originalValue;
            return new Date(datatime.getDate().getTime());
        }
        if (originalValue instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) originalValue;
            return new Date(date.getTime());
        }
        return originalValue;
    }
    
    /**
     * ResultSetから取得した値を一般的なjavaの型に変換する。
     * 
     * @param databaseValue
     * @return 変換後の値。
     */
    protected static Object convertFromResultSetValue(Object databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        if (databaseValue instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) databaseValue;
            return new Date(timestamp.getTime());
        }
        if (databaseValue instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) databaseValue;
            return bigDecimal.doubleValue();
        }
        return databaseValue;
    }
    
    /**
     * プリペアドステートメントを利用して更新系のSQLを実行する。
     * 
     * @author hiro
     * 
     */
    public class PreparedStatement implements Closeable {
        
        /**
         * コンストラクタ。
         * 
         * @param sql プレースホルダを使用したSQL。
         * @throws SQLException
         */
        public PreparedStatement(String sql) throws SQLException {
            @SuppressWarnings("resource")
            Database database = Database.this;
            this.statement = database.connection.prepareStatement(sql);
            this.statement.setQueryTimeout(database.queryTimeout);
        }
        
        private java.sql.PreparedStatement statement;
        
        private int numberOfChanged = 0;
        
        /**
         * 更新された件数を取得する。
         * 
         * @return 結果。
         */
        public int getNumberOfChanged() {
            return this.numberOfChanged;
        }
        
        /**
         * バインド変数を代入して更新を実行する。
         * 
         * @param parameters バインド変数。
         * @throws SQLException
         */
        public void execute(Object[] parameters) throws SQLException {
            for (int i = 0; i < parameters.length; i++) {
                this.statement.setObject(i + 1, convertToBindParameter(parameters[i]));
            }
            this.numberOfChanged += this.statement.executeUpdate();
        }
        
        @Override
        public void close() throws IOException {
            try {
                this.statement.close();
            } catch (Exception exception) {
            }
        }
    }
}
