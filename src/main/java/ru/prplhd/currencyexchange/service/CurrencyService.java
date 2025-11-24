package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CreateCurrencyDto;
import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.ValidationException;
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
        List<Currency> currencies = currencyDao.findAll();
        return CurrencyMapper.toDtos(currencies);
    }

    public CurrencyDto createCurrency(CreateCurrencyDto createCurrencyDto) {
        validateCreateCurrencyDto(createCurrencyDto);
        Currency currencyToSave = CurrencyMapper.fromCreateDto(createCurrencyDto);
        Currency savedCurrency = currencyDao.insert(currencyToSave);
        return CurrencyMapper.toDto(savedCurrency);
    }

    private void validateCreateCurrencyDto(CreateCurrencyDto createCurrencyDto) {
        String code = createCurrencyDto.code();
        if (!code.matches("[A-Z]{3}")) {
            throw new ValidationException("Invalid format. Currency code must consist of 3 uppercase English letters.");
        }

        String name = createCurrencyDto.name();
        if (name.length() < 3 || name.length() > 50) {
            throw new ValidationException("Invalid name length. Currency name must be between 3 and 50 characters.");
        }

        String sign = createCurrencyDto.sign();
        if (sign != null && sign.length() > 3) {
            throw new ValidationException("Invalid sign length. When provided, currency sign must consist of exactly 1 character.");
        }
    }
}
