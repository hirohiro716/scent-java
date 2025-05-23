package com.hirohiro716.scent.database.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.database.DataNotFoundException;
import com.hirohiro716.scent.database.Database;
import com.hirohiro716.scent.datetime.Datetime;
import com.hirohiro716.scent.filesystem.File;

/**
 * JDBCドライバでSQLiteデータベースに接続するためのクラス。
 * 
 * @author hiro
*/
public abstract class SQLite extends Database {
    
    @Override
    protected String getJDBCDriverBinaryName() {
        return "org.sqlite.JDBC";
    }
    
    @Override
    protected void connect(String connectionString) throws SQLException {
        super.connect(connectionString);
        this.setIsolationLevel(IsolationLevel.NOLOCK);
    }
    
    private static IsolationLevel ISOLATION_LEVEL = IsolationLevel.NOLOCK;
    
    private boolean isOwnTransaction = false;
    
    /**
     * 開始されているトランザクションの分離レベルを取得する。
     * 
     * @return
     */
    public IsolationLevel getIsolationLevel() {
        synchronized (SQLite.ISOLATION_LEVEL) {
            return SQLite.ISOLATION_LEVEL;
        }
    }
    
    /**
     * トランザクションの分離レベルをセットする。
     * 
     * @param isolationLevel
     * @throws SQLException
     */
    private void setIsolationLevel(IsolationLevel isolationLevel) throws SQLException {
        synchronized (SQLite.ISOLATION_LEVEL) {
            if (SQLite.ISOLATION_LEVEL != IsolationLevel.NOLOCK && this.isOwnTransaction == false) {
                throw new SQLException("Transaction has already been started.");
            }
            SQLite.ISOLATION_LEVEL = isolationLevel;
            if (SQLite.ISOLATION_LEVEL != IsolationLevel.NOLOCK) {
                this.isOwnTransaction = true;
            } else {
                this.isOwnTransaction = false;
            }
        }
    }
    
    private File databaseFile;
    
    /**
     * 指定されたSQLiteデータベースファイルに接続する。
     * 
     * @param databaseFile
     * @throws SQLException
     */
    public void connect(File databaseFile) throws SQLException {
        this.connect("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        this.databaseFile = databaseFile;
    }

    /**
     * 指定されたSQLiteデータベースファイルに接続する。
     * 
     * @param databaseFile
     * @throws SQLException
     */
    public void connect(java.io.File databaseFile) throws SQLException {
        this.connect(new File(databaseFile));
    }
    
    /**
     * 接続されているSQLiteデータベースファイルを取得する。
     * 
     * @return SQLiteデータベースファイル。
     */
    public File getDatabaseFile() {
        return this.databaseFile;
    }
    
    /**
     * SQLiteにはBoolean型が無いのでINTEGERで代用する際の有効を表す数値。
     */
    public static final int BOOLEAN_VALUE_ENABLED = 1;
    
    /**
     * SQLiteにはBoolean型が無いのでINTEGERで代用する際の無効を表す数値。
     */
    public static final int BOOLEAN_VALUE_DISABLED = 0;

    @Override
    public Object[] castBindParameters(Object[] parameters) {
        List<Object> bindParameters = new ArrayList<>();
        for (Object parameter: parameters) {
            if (parameter instanceof Date) {
                bindParameters.add(Datetime.newInstance((Date) parameter).toString());
            } else {
                bindParameters.add(parameter);
            }
        }
        return bindParameters.toArray();
    }

    /**
     * SQLiteデータベースに指定された物理名のテーブルが存在するか確認する。
     * 
     * @param physicalName
     * @return
     * @throws SQLException
     */
    public boolean existsTable(String physicalName) throws SQLException {
        try {
            Number numberOfTables = this.fetchField(StringObject.join("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='", physicalName, "';").toString());
            if (numberOfTables.intValue() == 0) {
                return false;
            }
            return true;
        } catch (DataNotFoundException exception) {
            return false;
        }
    }

    /**
     * トランザクションを開始する。
     * 
     * @param isolationLevel トランザクション分離レベル。
     * @throws SQLException
     */
    public void begin(IsolationLevel isolationLevel) throws SQLException {
        this.setIsolationLevel(isolationLevel);
        try {
            this.execute(StringObject.join("BEGIN ", SQLite.ISOLATION_LEVEL.toString(), ";").toString());
        } catch (SQLException exception) {
            this.setIsolationLevel(IsolationLevel.NOLOCK);
            throw exception;
        }
    }
    
    @Override
    public void commit() throws SQLException {
        this.execute("COMMIT;");
        this.setIsolationLevel(IsolationLevel.NOLOCK);
    }

    @Override
    public void rollback() throws SQLException {
        this.execute("ROLLBACK;");
        this.setIsolationLevel(IsolationLevel.NOLOCK);
    }

    @Override
    public void close() {
        super.close();
        try {
            this.setIsolationLevel(IsolationLevel.NOLOCK);
        } catch (SQLException exception) {
        }
    }

    @Deprecated
    @Override
    public void setAutoCommit(boolean isAutoCommit) throws SQLException {
        throw new SQLException("SQLite should not use this method.");
    }
    
    @Deprecated
    @Override
    public boolean isAutoCommit() throws SQLException {
        return super.isAutoCommit();
    }

    /**
     * SQLiteのトランザクション分離レベル列挙型。
     * 
     * @author hiro
     */
    public enum IsolationLevel {
        /**
         * ロックしない。
         */
        NOLOCK,
        /**
         * SQL実行時にロックをかける。
         */
        DEFERRED,
        /**
         * トランザクション開始時に書き込みロックをかける。
         */
        IMMEDIATE,
        /**
         * トランザクション開始時に排他ロックをかける。
         */
        EXCLUSIVE,
    }
}
