package ru.prplhd.currencyexchange.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@WebListener
public class DatabaseHealthCheckListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        verifyDatabaseConnection();
    }

    private void verifyDatabaseConnection() {
        try (Connection connection = ConnectionProvider.getConnection()) {

        } catch (SQLException | ExceptionInInitializerError e) {
            log.error("DB health check failed", e);

            throw new DatabaseException("Failed to initialize database connection", e);
        }
    }
}