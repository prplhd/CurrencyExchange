package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.mapper.CurrencyMapper;

import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao dao) {
        currencyDao = dao;
    }

    public List<CurrencyDto> getAllCurrencies() {
        return CurrencyMapper.toDtos(currencyDao.findAll());
    }
}
