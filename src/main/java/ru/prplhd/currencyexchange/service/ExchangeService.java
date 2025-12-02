package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeDto;
import ru.prplhd.currencyexchange.dto.ExchangeRequestDto;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.mapper.ExchangeMapper;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.model.Exchange;
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

    public ExchangeDto getExchange(ExchangeRequestDto exchangeRequestDto) {
        ExchangeValidator.validateExchangeRequestDto(exchangeRequestDto);

        String fromCurrencyCode = exchangeRequestDto.fromCurrencyCode();
        String toCurrencyCode = exchangeRequestDto.toCurrencyCode();
        BigDecimal amount = new BigDecimal(exchangeRequestDto.amount());

        ExchangeRateResult exchangeRateResult = getExchangeRateResult(fromCurrencyCode, toCurrencyCode);
        BigDecimal convertedAmount = exchangeRateResult.rate().multiply(amount);
        convertedAmount = convertedAmount.setScale(CONVERTED_AMOUNT_SCALE, RoundingMode.HALF_UP);

        Exchange exchange = new Exchange(
                exchangeRateResult.fromCurrency(),
                exchangeRateResult.toCurrency(),
                exchangeRateResult.rate(),
                amount,
                convertedAmount
        );

        return ExchangeMapper.toDto(exchange);
    }

    private ExchangeRateResult getExchangeRateResult(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> optDirectExchangeRate = exchangeRateDao.findByCurrencyPairCode(baseCurrencyCode, targetCurrencyCode);
        if (optDirectExchangeRate.isPresent()) {
            ExchangeRate directExchangeRate = optDirectExchangeRate.get();

            return new ExchangeRateResult(
                    directExchangeRate.baseCurrency(),
                    directExchangeRate.targetCurrency(),
                    directExchangeRate.rate()
            );
        }

        Optional<ExchangeRate> optReverseExchangeRate = exchangeRateDao.findByCurrencyPairCode(targetCurrencyCode, baseCurrencyCode);
        if (optReverseExchangeRate.isPresent()) {
            ExchangeRate reverseExchangeRate = optReverseExchangeRate.get();
            BigDecimal reverseRate = reverseExchangeRate.rate();
            BigDecimal rate = BigDecimal.ONE.divide(reverseRate, EXCHANGE_RATE_SCALE, RoundingMode.HALF_UP);

            return new ExchangeRateResult(
                    reverseExchangeRate.targetCurrency(),
                    reverseExchangeRate.baseCurrency(),
                    rate
            );
        }

        Optional<ExchangeRate> optReferenceToBaseExchangeRate = exchangeRateDao.findByCurrencyPairCode(REFERENCE_CURRENCY_CODE, baseCurrencyCode);
        Optional<ExchangeRate> optReferenceToTargetExchangeRate = exchangeRateDao.findByCurrencyPairCode(REFERENCE_CURRENCY_CODE, targetCurrencyCode);
        if(optReferenceToBaseExchangeRate.isPresent() && optReferenceToTargetExchangeRate.isPresent()) {
            ExchangeRate usdToBaseExchangeRate = optReferenceToBaseExchangeRate.get();
            ExchangeRate usdToTargetExchangeRate = optReferenceToTargetExchangeRate.get();

            BigDecimal baseRate = usdToBaseExchangeRate.rate();
            BigDecimal targetRate = usdToTargetExchangeRate.rate();

            BigDecimal rate = targetRate.divide(baseRate, EXCHANGE_RATE_SCALE, RoundingMode.HALF_UP);

            return new ExchangeRateResult(
                    usdToBaseExchangeRate.targetCurrency(),
                    usdToTargetExchangeRate.targetCurrency(),
                    rate
            );
        }

        throw new ExchangeRateNotFoundException("Exchange rate with codes '%s' and '%s' not found"
                .formatted(baseCurrencyCode, targetCurrencyCode));
    }

    private record ExchangeRateResult(
            Currency fromCurrency,
            Currency toCurrency,
            BigDecimal rate
    ) {}
}