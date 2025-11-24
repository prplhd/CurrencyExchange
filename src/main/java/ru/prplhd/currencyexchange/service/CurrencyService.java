package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CreateCurrencyDto;
import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.mapper.CurrencyMapper;
import ru.prplhd.currencyexchange.model.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao dao) {
        currencyDao = dao;
    }

    public CurrencyDto getCurrencyByCode(String code) {
        Optional<Currency> optCurrency = currencyDao.findByCode(code);
        Currency currency = optCurrency.orElseThrow(() ->
                new CurrencyNotFoundException("Currency with code '%s' not found".formatted(code)));
        return CurrencyMapper.toDto(currency);
    }

    public List<CurrencyDto> getAllCurrencies() {
        return CurrencyMapper.toDtos(currencyDao.findAll());
    }

    public CurrencyDto createCurrency(CreateCurrencyDto createCurrencyDto) {
        return null;
    }
}
