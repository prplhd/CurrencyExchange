package ru.prplhd.currencyexchange.database;

import ru.prplhd.currencyexchange.exception.DataAccessException;
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
    private static String url;
    private static String initializationErrorMessage;

    private ConnectionProvider() {}

    static {
        try (InputStream inputStream = ConnectionProvider.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            Properties properties = new Properties();
            properties.load(inputStream);
            String driver = properties.getProperty(DRIVER_KEY);
            url = properties.getProperty(URL_KEY);

            Class.forName(driver);
        } catch (IOException e) {
            initializationErrorMessage = "Failed to load database configuration from "
                    + PROPERTIES_FILE;
        } catch (ClassNotFoundException e) {
            initializationErrorMessage = "JDBC driver class not found.";
        }
    }

    public static Connection getConnection() {
        if (initializationErrorMessage != null) {
            throw new DataAccessException(initializationErrorMessage);
        }

        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new DataAccessException("Unable to connect to database.", e);
        }
    }
}
