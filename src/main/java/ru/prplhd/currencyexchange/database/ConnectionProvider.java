package ru.prplhd.currencyexchange.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionProvider {
    private static final String PROPERTIES_FILE = "db.properties";
    private static final HikariDataSource HIKARI_DATA_SOURCE;

    static {
        HikariConfig config = new HikariConfig(PROPERTIES_FILE);
        HIKARI_DATA_SOURCE = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return HIKARI_DATA_SOURCE.getConnection();
    }

    public static void shutdown() {
        HIKARI_DATA_SOURCE.close();
    }
}