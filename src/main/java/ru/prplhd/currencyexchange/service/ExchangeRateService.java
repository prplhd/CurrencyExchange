package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.CreateExchangeRateDto;
import ru.prplhd.currencyexchange.dto.ExchangeRateDto;
import ru.prplhd.currencyexchange.mapper.ExchangeRateMapper;
import ru.prplhd.currencyexchange.model.ExchangeRate;
import ru.prplhd.currencyexchange.validation.ExchangeRateValidator;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
        return ExchangeRateMapper.toDtos(exchangeRates);
    }

    public ExchangeRateDto createExchangeRate(CreateExchangeRateDto createExchangeRateDto) {
        ExchangeRateValidator.validateCreateExchangeRateDto(createExchangeRateDto);

        String baseCurrencyCode = createExchangeRateDto.baseCurrencyCode();
        String targetCurrencyCode = createExchangeRateDto.targetCurrencyCode();
        BigDecimal rate = new BigDecimal(createExchangeRateDto.rate());

        ExchangeRate exchangeRate = exchangeRateDao.insert(baseCurrencyCode, targetCurrencyCode, rate);

        return ExchangeRateMapper.toDto(exchangeRate);
    }

    public ExchangeRateDto getByCurrencyPairCode(String currencyPairCode) {
        ExchangeRateValidator.validateCurrencyPairCode(currencyPairCode);

        String baseCurrencyCode = currencyPairCode.substring(0, ExchangeRateValidator.CURRENCY_CODE_LENGTH);
        String targetCurrencyCode = currencyPairCode.substring(ExchangeRateValidator.CURRENCY_CODE_LENGTH);
        ExchangeRate exchangeRate = exchangeRateDao.findByCurrencyPairCode(baseCurrencyCode, targetCurrencyCode);
        return ExchangeRateMapper.toDto(exchangeRate);
    }
}
