package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CreateCurrencyDto;
import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.mapper.CurrencyMapper;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.validation.CurrencyValidator;

import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao dao) {
        this.currencyDao = dao;
    }

    public CurrencyDto getCurrency(String code) {
        CurrencyValidator.validateCurrencyCode(code);
        Currency currency = currencyDao.findByCode(code);
        return CurrencyMapper.toDto(currency);
    }

    public List<CurrencyDto> getAllCurrencies() {
        List<Currency> currencies = currencyDao.findAll();
        return CurrencyMapper.toDtos(currencies);
    }

    public CurrencyDto createCurrency(CreateCurrencyDto createCurrencyDto) {
        CurrencyValidator.validateCreateCurrencyDto(createCurrencyDto);
        Currency currencyToSave = CurrencyMapper.fromCreateDto(createCurrencyDto);
        Currency savedCurrency = currencyDao.insert(currencyToSave);
        return CurrencyMapper.toDto(savedCurrency);
    }
}