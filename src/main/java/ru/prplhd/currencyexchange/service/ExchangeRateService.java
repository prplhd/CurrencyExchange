package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeRateRequestDto;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.model.ExchangeRate;
import ru.prplhd.currencyexchange.validation.ExchangeRateValidator;

import java.math.BigDecimal;

public class ExchangeRateService {
    private final CurrencyDao currencyDao;
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(CurrencyDao currencyDao, ExchangeRateDao exchangeRateDao) {
        this.currencyDao = currencyDao;
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeRate createExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) {
        ExchangeRate exchangeRate = extractExchangeRate(exchangeRateRequestDto);

        return exchangeRateDao.save(exchangeRate);
    }

    public ExchangeRate updateExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) {
        ExchangeRate exchangeRate = extractExchangeRate(exchangeRateRequestDto);

        return exchangeRateDao.update(exchangeRate)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate with codes '%s' and '%s' not found"
                        .formatted(exchangeRate.getBaseCurrency().getCode(), exchangeRate.getTargetCurrency().getCode())));
    }

    private ExchangeRate extractExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) {
        ExchangeRateValidator.validateExchangeRateRequestDto(exchangeRateRequestDto);

        String baseCurrencyCode = exchangeRateRequestDto.baseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequestDto.targetCurrencyCode();

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code '" + baseCurrencyCode + "' not found"));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code '" + targetCurrencyCode + "' not found"));
        BigDecimal rate = new BigDecimal(exchangeRateRequestDto.rate());

        return new ExchangeRate(baseCurrency, targetCurrency, rate);
    }
}