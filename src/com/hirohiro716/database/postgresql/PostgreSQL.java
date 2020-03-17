package com.hirohiro716.database.postgresql;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;

import com.hirohiro716.StringObject;
import com.hirohiro716.database.Database;
import com.hirohiro716.database.TableInterface;

/**
 * JDBCドライバでPostgreSQLデータベースに接続するためのクラス。
 * 
 * @author hiro
 *
 */
public class PostgreSQL extends Database {
    
    /**
     * コンストラクタ。
     * 
     * @param jdbcDriverURL PostgreSQLのJDBCドライバURL。
     */
    public PostgreSQL(URL jdbcDriverURL) {
        super(jdbcDriverURL);
    }

    @Override
    protected String getJDBCDriverBinaryName() {
        return "org.postgresql.Driver";
    }
    
    /**
     * PostgreSQLデータベースに接続する。
     * 
     * @param serverAddress サーバーのIPアドレスやFQDNなど。
     * @param databaseName
     * @param userName
     * @param password
     * @param characterEncoding UTF8やSJISなどの文字セット。
     * @param portNumber
     * @throws SQLException
     */
    public void connect(String serverAddress, String databaseName, String userName, String password, String characterEncoding, int portNumber) throws SQLException {
        StringObject connectionString = new StringObject("jdbc:postgresql://");
        connectionString.append(serverAddress);
        if (portNumber > 0) {
            connectionString.append(":");
            connectionString.append(portNumber);
        }
        connectionString.append("/");
        connectionString.append(databaseName);
        connectionString.append("?user=");
        connectionString.append(userName);
        connectionString.append("&password=");
        connectionString.append(password);
        connectionString.append("&characterEncoding=");
        connectionString.append(characterEncoding);
        this.connect(connectionString.toString());
    }

    /**
     * PostgreSQLデータベースに接続する。
     * 
     * @param serverAddress サーバーのIPアドレスやFQDNなど。
     * @param databaseName
     * @param userName
     * @param password
     * @param characterEncoding UTF8やSJISなどの文字セット。
     * @throws SQLException
     */
    public void connect(String serverAddress, String databaseName, String userName, String password, String characterEncoding) throws SQLException {
        this.connect(serverAddress, databaseName, userName, password, characterEncoding, -1);
    }
    
    /**
     * テーブルの読み取りと書き込みをロックする。
     * 
     * @param physicalTableName
     * @throws SQLException
     */
    public void lockTable(String physicalTableName) throws SQLException {
        StringObject sql = new StringObject("LOCK TABLE ");
        sql.append(physicalTableName);
        sql.append(" IN ACCESS EXCLUSIVE MODE;");
        this.execute(sql.toString());
    }
    
    /**
     * テーブルの読み取りと書き込みをロックする。
     * 
     * @param table
     * @throws SQLException
     */
    public void lockTable(TableInterface table) throws SQLException {
        this.lockTable(table.getPhysicalName());
    }
    
    /**
     * テーブルの書き込みをロックする。
     * 
     * @param physicalTableName
     * @throws SQLException
     */
    public void lockTableReadonly(String physicalTableName) throws SQLException {
        StringObject sql = new StringObject("LOCK TABLE ");
        sql.append(physicalTableName);
        sql.append(" IN EXCLUSIVE MODE;");
        this.execute(sql.toString());
    }
    
    /**
     * テーブルの書き込みをロックする。
     * 
     * @param table
     * @throws SQLException
     */
    public void lockTableReadonly(TableInterface table) throws SQLException {
        this.lockTableReadonly(table.getPhysicalName());
    }
    
    /**
     * データベースサーバーの現在の時刻を取得する。
     * 
     * @return 結果。
     * @throws SQLException
     */
    public Date fetchNow() throws SQLException {
        try {
            return this.fetchField("SELECT CLOCK_TIMESTAMP();");
        } catch (Exception exception) {
            throw new SQLException(exception);
        }
    }
}
