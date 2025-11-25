package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.database.ConnectionProvider;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
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
            JOIN Currencies tc ON (er.targetCurrencyId = tc.id);
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
}
