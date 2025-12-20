package ru.prplhd.currencyexchange.service;

import lombok.extern.slf4j.Slf4j;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeRateRequestDto;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.model.ExchangeRate;
import ru.prplhd.currencyexchange.validation.ExchangeRateValidator;

import java.math.BigDecimal;

@Slf4j
public class ExchangeRateService {
    private final CurrencyDao currencyDao;
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(CurrencyDao currencyDao, ExchangeRateDao exchangeRateDao) {
        this.currencyDao = currencyDao;
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeRate createExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) {
        ExchangeRate exchangeRate = extractExchangeRate(exchangeRateRequestDto);

        ExchangeRate saved = exchangeRateDao.save(exchangeRate);

        log.info("Exchange rate created: base={}, target={}, rate={}",
                saved.getBaseCurrency().getCode(), saved.getTargetCurrency().getCode(), saved.getRate());

        return saved;
    }

    public ExchangeRate updateExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) {
        ExchangeRate exchangeRate = extractExchangeRate(exchangeRateRequestDto);

        ExchangeRate updated = exchangeRateDao.update(exchangeRate)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate with codes '%s' and '%s' not found"
                        .formatted(exchangeRate.getBaseCurrency().getCode(), exchangeRate.getTargetCurrency().getCode())));

        log.info("Exchange rate updated: base={}, target={}, rate={}",
                updated.getBaseCurrency().getCode(), updated.getTargetCurrency().getCode(), updated.getRate());

        return updated;
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