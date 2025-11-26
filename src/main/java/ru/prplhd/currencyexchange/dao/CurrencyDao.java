package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.CurrencyAlreadyExistsException;
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
    private static final String FIND_ALL_CURRENCIES_SQL = """
            SELECT id, code, fullname, sign
            FROM currencies
            """;
    private static final String FIND_CURRENCY_BY_CODE_SQL = """
            SELECT id, code, fullname, sign
            FROM currencies WHERE code = ?
            """;
    private static final String INSERT_CURRENCY_SQL = """
            INSERT INTO currencies (code, fullname, sign)
            VALUES (?, ?, ?)
            """;

    public List<Currency> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery(FIND_ALL_CURRENCIES_SQL)) {
                List<Currency> currencies = new ArrayList<>();

                while (resultSet.next()) {
                    Currency currency = mapToCurrency(resultSet);
                    currencies.add(currency);
                }
                return currencies;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load currencies from database", e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_CURRENCY_BY_CODE_SQL)) {

            preparedStatement.setString(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToCurrency(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load currencies from database", e);
        }
    }

    public Currency insert(Currency currencyToInsert) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CURRENCY_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currencyToInsert.code());
            preparedStatement.setString(2, currencyToInsert.name());
            preparedStatement.setString(3, currencyToInsert.sign());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected != 1) {
                throw new DataAccessException("Failed to insert currency into database");
            }

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DataAccessException("Failed to insert currency into database (no generated id)");
                }
                Long id = keys.getLong(1);
                return new Currency(id, currencyToInsert.code(), currencyToInsert.name(), currencyToInsert.sign());
            }
        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new CurrencyAlreadyExistsException("Currency with code %s already exists.".formatted(currencyToInsert.code()));
            }
            throw new DataAccessException("Failed to insert currency into database", e);
        }
    }

    private Currency mapToCurrency (ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String code = resultSet.getString("code");
        String name = resultSet.getString("fullname");
        String sign = resultSet.getString("sign");
        return new Currency(id, code, name, sign);
    }

    private boolean isUniqueConstraintViolation(SQLException e) {
        String message = e.getMessage();
        return message != null && message.contains("UNIQUE constraint failed");
    }

}
