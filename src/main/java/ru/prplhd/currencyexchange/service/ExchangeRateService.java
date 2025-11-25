package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeRateDto;
import ru.prplhd.currencyexchange.mapper.ExchangeRateMapper;
import ru.prplhd.currencyexchange.model.ExchangeRate;

import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(ExchangeRateDao dao) {
        this.exchangeRateDao = dao;
    }

    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
        return ExchangeRateMapper.toDtos(exchangeRates);
    }
}
