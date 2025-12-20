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
public class DatabaseLifecycleListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        verifyDatabaseConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionProvider.shutdown();
        System.out.println("DB pool shutdown complete");
    }

    private void verifyDatabaseConnection() {
        try (Connection connection = ConnectionProvider.getConnection()) {
            log.info("DB health check OK");

        } catch (SQLException | ExceptionInInitializerError e) {
            log.error("DB health check failed", e);

            throw new DatabaseException("Failed to initialize database connection", e);
        }
    }
}