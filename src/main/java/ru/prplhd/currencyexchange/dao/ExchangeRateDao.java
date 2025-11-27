package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
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

public class ExchangeRateDao {
    private static final String FIND_ALL_EXCHANGE_RATES_SQL = """
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

    private static final String FIND_EXCHANGE_RATE_BY_CURRENCY_CODES_SQL =
            FIND_ALL_EXCHANGE_RATES_SQL + """
            WHERE bc.code = ?
            AND tc.code = ?;
            """;

    private static final String INSERT_EXCHANGE_RATE_BY_CURRENCY_CODES_SQL = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
            SELECT bc.Id, tc.Id, ?
            FROM Currencies bc, Currencies tc
            WHERE bc.Code = ?
            AND tc.Code = ?
            """;

    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery(FIND_ALL_EXCHANGE_RATES_SQL)) {
                List<ExchangeRate> exchangeRates = new ArrayList<>();

                while (resultSet.next()) {
                    ExchangeRate exchangeRate = mapToExchangeRate(resultSet);
                    exchangeRates.add(exchangeRate);
                }
                return exchangeRates;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load exchange rates from database", e);
        }
    }

    public ExchangeRate findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_EXCHANGE_RATE_BY_CURRENCY_CODES_SQL)) {

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToExchangeRate(resultSet);
                } else {
                    throw new ExchangeRateNotFoundException("Exchange rate with codes '%s' abd '%s' not found"
                            .formatted(baseCurrencyCode, targetCurrencyCode));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load exchange rate from database", e);
        }
    }

    public ExchangeRate insert(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE_BY_CURRENCY_CODES_SQL)) {

            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setString(2, baseCurrencyCode);
            preparedStatement.setString(3, targetCurrencyCode);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new CurrencyNotFoundException("Base or target currency not found for codes '%s' and '%s'."
                        .formatted(baseCurrencyCode, targetCurrencyCode));
            }

        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new ExchangeRateAlreadyExistsException("Exchange rate for currency pair '%s' / '%s' already exists."
                        .formatted(baseCurrencyCode, targetCurrencyCode));
            }
            throw new DataAccessException("Failed to insert exchange rate into database", e);
        }

        return findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    }

    private ExchangeRate mapToExchangeRate(ResultSet resultSet) throws SQLException {
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
        String message = e.getMessage();
        return message != null && message.contains("UNIQUE constraint failed");
    }
}
