package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.db.ConnectionProvider;
import ru.prplhd.currencyexchange.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    public List<Currency> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
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
            throw new IllegalStateException("Failed to load currencies from database", e);
        }
    }
}
