package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.db.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.model.Currency;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    private static final String FIND_ALL_CURRENCIES_SQL = "SELECT id, code, fullname, sign FROM CURRENCIES";

    public List<Currency> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(FIND_ALL_CURRENCIES_SQL);
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String code = resultSet.getString("code");
                String name = resultSet.getString("fullname");
                String sign = resultSet.getString("sign");
                Currency currency = new Currency(id, code, name, sign);
                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load currencies from database", e);
        }
    }
}
