package ru.prplhd.currencyexchange.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DatabaseHealthCheckListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        verifyDatabaseConnection();
    }

    private void verifyDatabaseConnection() {
        try (Connection connection = ConnectionProvider.getConnection()) {

        } catch (SQLException | ExceptionInInitializerError e) {
            e.printStackTrace();
            throw new DatabaseException("Failed to initialize database connection", e);
        }
    }
}