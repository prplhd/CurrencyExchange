package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeRequestDto;
import ru.prplhd.currencyexchange.dto.ExchangeResponseDto;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.mapper.CurrencyMapper;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.model.ExchangeRate;
import ru.prplhd.currencyexchange.validation.ExchangeValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {
    private static final String REFERENCE_CURRENCY_CODE = "USD";
    private static final int CONVERTED_AMOUNT_SCALE = 6;
    private static final int EXCHANGE_RATE_SCALE = 6;
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeResponseDto getExchange(ExchangeRequestDto exchangeRequestDto) {
        ExchangeValidator.validateExchangeRequestDto(exchangeRequestDto);

        String baseCurrencyCode = exchangeRequestDto.baseCurrencyCode();
        String targetCurrencyCode = exchangeRequestDto.targetCurrencyCode();

        ExchangeRate exchangeRate = findExchangeRate(baseCurrencyCode, targetCurrencyCode);
        Currency baseCurrency = exchangeRate.getBaseCurrency();
        Currency targetCurrency = exchangeRate.getTargetCurrency();


        BigDecimal rate = exchangeRate.getRate();
        BigDecimal amount = new BigDecimal(exchangeRequestDto.amount());
        BigDecimal convertedAmount = rate.multiply(amount);
        convertedAmount = convertedAmount.setScale(CONVERTED_AMOUNT_SCALE, RoundingMode.HALF_UP);

        return new ExchangeResponseDto(
                CurrencyMapper.INSTANCE.toDto(baseCurrency),
                CurrencyMapper.INSTANCE.toDto(targetCurrency),
                rate,
                amount,
                convertedAmount
        );
    }

    private ExchangeRate findExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> optExchangeRate = findDirectExchangeRate(baseCurrencyCode, targetCurrencyCode);
        if(optExchangeRate.isPresent()) {
            return optExchangeRate.get();
        }

        optExchangeRate = findReverseExchangeRate(baseCurrencyCode, targetCurrencyCode);
        if(optExchangeRate.isPresent()) {
            return optExchangeRate.get();
        }

        optExchangeRate = findViaReferenceExchangeRate(baseCurrencyCode, targetCurrencyCode);
        if(optExchangeRate.isPresent()) {
            return optExchangeRate.get();
        }

        throw new ExchangeRateNotFoundException("Exchange rate with codes '%s' and '%s' not found"
                .formatted(baseCurrencyCode, targetCurrencyCode));
    }

    private Optional<ExchangeRate> findDirectExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
    }

    private Optional<ExchangeRate> findReverseExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> optReverseExchangeRate = exchangeRateDao.findByCodes(targetCurrencyCode, baseCurrencyCode);
        if(optReverseExchangeRate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate reverseExchangeRate = optReverseExchangeRate.get();
        BigDecimal reverseRate = reverseExchangeRate.getRate();
        BigDecimal directRate = BigDecimal.ONE.divide(reverseRate, EXCHANGE_RATE_SCALE, RoundingMode.HALF_UP);

        ExchangeRate directExchangeRate = new ExchangeRate(
                reverseExchangeRate.getTargetCurrency(),
                reverseExchangeRate.getBaseCurrency(),
                directRate
        );

        return Optional.of(directExchangeRate);
    }

    private Optional<ExchangeRate> findViaReferenceExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> optReferenceToBaseExchangeRate = exchangeRateDao.findByCodes(REFERENCE_CURRENCY_CODE, baseCurrencyCode);
        Optional<ExchangeRate> optReferenceToTargetExchangeRate = exchangeRateDao.findByCodes(REFERENCE_CURRENCY_CODE, targetCurrencyCode);

        if(optReferenceToBaseExchangeRate.isEmpty() || optReferenceToTargetExchangeRate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate referenceToBaseExchangeRate = optReferenceToBaseExchangeRate.get();
        ExchangeRate referenceToTargetExchangeRate = optReferenceToTargetExchangeRate.get();

        BigDecimal referenceToBaseRate = referenceToBaseExchangeRate.getRate();
        BigDecimal referenceToTargetRate = referenceToTargetExchangeRate.getRate();
        BigDecimal rate = referenceToTargetRate.divide(referenceToBaseRate, EXCHANGE_RATE_SCALE, RoundingMode.HALF_UP);
        
        ExchangeRate directExchangeRate = new ExchangeRate(
                referenceToBaseExchangeRate.getTargetCurrency(),
                referenceToTargetExchangeRate.getTargetCurrency(),
                rate
        );
        
        return Optional.of(directExchangeRate);
    }
}