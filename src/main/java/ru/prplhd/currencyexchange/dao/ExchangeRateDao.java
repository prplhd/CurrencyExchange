package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.model.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends BaseDao<ExchangeRate>{
    Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode);

    Optional<ExchangeRate> update(ExchangeRate exchangeRate);
}