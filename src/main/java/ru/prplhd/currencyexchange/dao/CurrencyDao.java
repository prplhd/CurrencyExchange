package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    private static final String URL = "jdbc:sqlite:C:/Users/purplehead/dev/sqlite-3.51.0.0/currency_exchange.db";
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found in classpath");
        }
    }
    public List<Currency> findAll() {
        try (Connection connection = DriverManager.getConnection(URL);
             Statement statement = connection.createStatement()) {

            String selectSql = "SELECT id, code, fullname, sign FROM currencies";
            ResultSet resultSet = statement.executeQuery(selectSql);
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("fullname");
                String sign = resultSet.getString("sign");
                Currency currency = new Currency(id, code, fullName, sign);
                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to connect to database.");
        }
    }
}
