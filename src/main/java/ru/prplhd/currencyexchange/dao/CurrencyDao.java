package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.model.Currency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {
    private static final String FIND_ALL_CURRENCIES_SQL = "SELECT id, code, fullname, sign FROM CURRENCIES";
    private static final String FIND_CURRENCY_BY_CODE_SQL = "SELECT id, code, fullname, sign FROM CURRENCIES WHERE code = ?";


    public List<Currency> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(FIND_ALL_CURRENCIES_SQL);
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                Currency currency = mapToCurrency(resultSet);
                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load currencies from database", e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_CURRENCY_BY_CODE_SQL)) {

            preparedStatement.setString(1, code);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapToCurrency(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load currencies from database", e);
        }
    }

    private Currency mapToCurrency (ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String code = resultSet.getString("code");
        String name = resultSet.getString("fullname");
        String sign = resultSet.getString("sign");
        return new Currency(id, code, name, sign);
    }
}
