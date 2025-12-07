package ru.prplhd.currencyexchange.dao;

import ru.prplhd.currencyexchange.model.Currency;

import java.util.Optional;

public interface CurrencyDao extends BaseDao<Currency> {
    Optional<Currency> findByCode(String code);
}