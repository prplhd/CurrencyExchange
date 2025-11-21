package ru.prplhd.currencyexchange.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionProvider {
    private static final String PROPERTIES_FILE = "db.properties";
    private static final String DRIVER_KEY = "db.driver";
    private static final String URL_KEY = "db.url";
    private static final String URL;

    private ConnectionProvider() {}

    static {
        try (InputStream inputStream = ConnectionProvider.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (inputStream == null) {
                throw new IllegalStateException(PROPERTIES_FILE + " not found in classpath");
            }

            Properties  properties = new Properties();
            properties.load(inputStream);
            String driver = properties.getProperty(DRIVER_KEY);
            URL = properties.getProperty(URL_KEY);

            Class.forName(driver);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database configuration from "
                    + PROPERTIES_FILE, e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBC driver class not found.", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to connect to database.", e);
        }
    }
}
