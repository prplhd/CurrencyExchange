package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CurrencyRequestDto;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.mapper.CurrencyMapper;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.validation.CurrencyValidator;

import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao dao) {
        this.currencyDao = dao;
    }

    public CurrencyResponseDto getCurrency(String code) {
        CurrencyValidator.validateCurrencyCode(code);
        Currency currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code '%s' not found".formatted(code)));

        return CurrencyMapper.toDto(currency);
    }

    public List<CurrencyResponseDto> getAllCurrencies() {
        List<Currency> currencies = currencyDao.findAll();
        return CurrencyMapper.toDtos(currencies);
    }

    public CurrencyResponseDto createCurrency(CurrencyRequestDto currencyRequestDto) {
        CurrencyValidator.validateCreateCurrencyDto(currencyRequestDto);
        Currency currencyToSave = CurrencyMapper.fromCreateDto(currencyRequestDto);
        Currency savedCurrency = currencyDao.save(currencyToSave);
        return CurrencyMapper.toDto(savedCurrency);
    }
}