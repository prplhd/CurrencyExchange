package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.CurrencyAlreadyExistsException;
import ru.prplhd.currencyexchange.exception.DatabaseException;
import ru.prplhd.currencyexchange.model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyDao implements CurrencyDao {
    private static final String FIND_ALL_QUERY = """
            SELECT id, code, fullname, sign
            FROM currencies
            """;

    private static final String FIND_BY_ID_QUERY =
            FIND_ALL_QUERY + """
            WHERE id = ?
            """;

    private static final String FIND_BY_CODE_QUERY =
            FIND_ALL_QUERY + """
            WHERE code = ?
            """;

    private static final String SAVE_QUERY = """
            INSERT INTO currencies (code, fullname, sign)
            VALUES (?, ?, ?)
            RETURNING *
            """;

    @Override
    public List<Currency> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY)) {

            ResultSet resultSet = statement.executeQuery();

            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(getCurrency(resultSet));
            }
            return currencies;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to read currencies from database", e);
        }
    }

    @Override
    public Optional<Currency> findById(Long id) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return Optional.of(getCurrency(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to read currencies from database", e);
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE_QUERY)) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return Optional.of(getCurrency(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to read currencies from database", e);
        }
    }

    @Override
    public Currency save(Currency currency) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_QUERY)) {

            preparedStatement.setString(1, currency.code());
            preparedStatement.setString(2, currency.name());
            preparedStatement.setString(3, currency.sign());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new DatabaseException("Failed to save currency into database");
            }

            return getCurrency(resultSet);

        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new CurrencyAlreadyExistsException("Currency with code %s already exists.".formatted(currency.code()));
            }
            throw new DatabaseException("Failed to save currency into database", e);
        }
    }

    private Currency getCurrency(ResultSet resultSet) throws SQLException {
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