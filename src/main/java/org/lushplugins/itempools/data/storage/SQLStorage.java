package org.lushplugins.itempools.data.storage;

import com.google.gson.JsonParser;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.data.ItemPoolGoalData;
import org.lushplugins.itempools.goal.GoalCollection;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;

/*
 * Tables:
 *   pool_data: id, goals, completed
 */
public class SQLStorage implements Storage {
    private final HikariDataSource source;

    public SQLStorage(String host, int port, String databaseName, String user, String password) {
        this.source = initDataSource(host, port, databaseName, user, password);
        setupDatabase("storage" + File.separator + "mysql_setup.sql");
    }

    @Override
    public ItemPoolGoalData loadPoolData(String poolId) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM pool_data WHERE id = ?;"
        )) {
            stmt.setString(1, poolId);

            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                String goalsRaw = result.getString("goals");
                boolean completed = result.getBoolean("completed");

                GoalCollection goals;
                if (goalsRaw != null) {
                    goals = GoalCollection.fromJson(JsonParser.parseString(goalsRaw));
//                    goals = ItemPools.getGson().fromJson(JsonParser.parseString(goalsRaw), GoalCollection.class);
                } else {
                    goals = new GoalCollection();
                }

                return new ItemPoolGoalData(poolId, goals, completed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void savePoolData(ItemPoolGoalData itemPoolData) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
            "REPLACE INTO pool_data (id, goals, completed) VALUES (?, ?, ?);"
        )) {
            stmt.setString(1, itemPoolData.id());
            stmt.setString(2, itemPoolData.goals().toJson().toString());
//            stmt.setString(2, ItemPools.getGson().toJson(itemPoolData.goals())); // TODO: Test
            stmt.setBoolean(3, itemPoolData.completed());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePoolData(String poolId) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
            "DELETE FROM pool_data WHERE id = ?;"
        )) {
            stmt.setString(1, poolId);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setupDatabase(String fileName) {
        String setup;
        try (InputStream in = SQLStorage.class.getClassLoader().getResourceAsStream(fileName)) {
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining(""));
        } catch (IOException e) {
            ItemPools.getInstance().getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
            e.printStackTrace();
            return;
        }

        String[] statements = setup.split("\\|");
        for (String statement : statements) {
            try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(statement)) {
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        ItemPools.getInstance().getLogger().info("Database setup complete.");
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

    protected Connection conn() {
        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
