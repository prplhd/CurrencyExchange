package ru.prplhd.currencyexchange.dao;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.DatabaseException;
import ru.prplhd.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateDao implements ExchangeRateDao {
    private static final String FIND_ALL_QUERY = """
            SELECT
                er.id          AS exchangeRateId,
            
                bc.id          AS baseCurrencyId,
                bc.code        AS baseCurrencyCode,
                bc.fullname    AS baseCurrencyName,
                bc.sign        AS baseCurrencySign,
            
                tc.id          AS targetCurrencyId,
                tc.code        AS targetCurrencyCode,
                tc.fullname    AS targetCurrencyName,
                tc.sign        AS targetCurrencySign,
            
                er.rate
            FROM ExchangeRates er
            JOIN Currencies bc ON (er.baseCurrencyId = bc.id)
            JOIN Currencies tc ON (er.targetCurrencyId = tc.id)
            """;

    private static final String FIND_BY_CODES_QUERY =
            FIND_ALL_QUERY + """
            WHERE bc.code = ?
            AND tc.code = ?
            """;

    private static final String SAVE_QUERY = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
            VALUES (?, ?, ?)
            RETURNING id
            """;

    private static final String UPDATE_QUERY = """
            UPDATE ExchangeRates
            SET rate = ?
            WHERE baseCurrencyId = ?
            AND targetCurrencyId = ?
            RETURNING id
            """;

    @Override
    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY);

            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                ExchangeRate exchangeRate = getExchangeRate(resultSet);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to load exchange rates from database", e);
        }
    }

    @Override
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODES_QUERY)) {

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getExchangeRate(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to load exchange rate from database", e);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_QUERY)) {

            preparedStatement.setLong(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new DatabaseException("Failed to save exchange rate into database");
            }

            exchangeRate.setId(resultSet.getLong("id"));
            return exchangeRate;

        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new ExchangeRateAlreadyExistsException("Exchange rate for currency pair '%s' / '%s' already exists."
                        .formatted(exchangeRate.getBaseCurrency().getCode(), exchangeRate.getTargetCurrency().getCode()));
            }
            throw new DatabaseException("Failed to insert exchange rate into database", e);
        }
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setLong(2, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setLong(3, exchangeRate.getTargetCurrency().getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                ExchangeRate updatedExchangeRate = new ExchangeRate(id, exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate());
                return Optional.of(updatedExchangeRate);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update exchange rate into database", e);
        }
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("exchangeRateId");

        Long baseCurrencyId = resultSet.getLong("baseCurrencyId");
        String baseCurrencyCode = resultSet.getString("baseCurrencyCode");
        String baseCurrencyName = resultSet.getString("baseCurrencyName");
        String baseCurrencySign = resultSet.getString("baseCurrencySign");
        Currency baseCurrency = new Currency(
                baseCurrencyId,
                baseCurrencyCode,
                baseCurrencyName,
                baseCurrencySign
        );

        Long targetCurrencyId = resultSet.getLong("targetCurrencyId");
        String targetCurrencyCode = resultSet.getString("targetCurrencyCode");
        String targetCurrencyName = resultSet.getString("targetCurrencyName");
        String targetCurrencySign = resultSet.getString("targetCurrencySign");
        Currency targetCurrency = new Currency(
                targetCurrencyId,
                targetCurrencyCode,
                targetCurrencyName,
                targetCurrencySign
        );

        BigDecimal rate = resultSet.getBigDecimal("rate");

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }

    private boolean isUniqueConstraintViolation(SQLException e) {
        if (e instanceof SQLiteException exception) {
            if (exception.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                return true;
            }
        }

        return false;
    }
}