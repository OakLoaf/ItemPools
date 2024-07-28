package me.dave.itempools.data.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.dave.itempools.data.ItemPoolGoalData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/*
 * Tables:
 *   pool_data: id, world, pos1, pos2, completion-commands, goals
 */
public class SQLStorage implements Storage {
    private final HikariDataSource source;

    public SQLStorage(String host, int port, String databaseName, String user, String password) {
        this.source = initDataSource(host, port, databaseName, user, password);
    }

    @Override
    public ItemPoolGoalData loadPoolData(String poolId) {
        return null;
    }

    @Override
    public void savePoolData(ItemPoolGoalData itemPoolData) {

    }

    protected HikariDataSource initDataSource(String host, int port, String databaseName, String user, String password) {
        Properties properties = new Properties();
        properties.setProperty("dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource");
        properties.setProperty("dataSource.serverName", host);
        properties.setProperty("dataSource.portNumber", String.valueOf(port));
        properties.setProperty("dataSource.user", user);
        properties.setProperty("dataSource.password", password);
        properties.setProperty("dataSource.databaseName", databaseName);

        HikariConfig hikariConfig = new HikariConfig(properties);
        hikariConfig.setMaximumPoolSize(8);

        HikariDataSource source = new HikariDataSource(hikariConfig);
        testDataSource(source);

        return source;
    }

    protected void testDataSource(HikariDataSource source) {
        try (Connection conn = source.getConnection()) {
            if (!conn.isValid(1000)) {
                throw new SQLException("Could not establish database connection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() {
        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
