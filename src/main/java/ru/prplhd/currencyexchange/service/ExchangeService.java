package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.JdbcExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeResponseDto;
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
    private final JdbcExchangeRateDao jdbcExchangeRateDao;

    public ExchangeService(JdbcExchangeRateDao jdbcExchangeRateDao) {
        this.jdbcExchangeRateDao = jdbcExchangeRateDao;
    }

    public ExchangeResponseDto getExchange(ExchangeRequestDto exchangeRequestDto) {
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
        Optional<ExchangeRate> optDirectExchangeRate = jdbcExchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
        if (optDirectExchangeRate.isPresent()) {
            ExchangeRate directExchangeRate = optDirectExchangeRate.get();

            return new ExchangeRateResult(
                    directExchangeRate.getBaseCurrency(),
                    directExchangeRate.getTargetCurrency(),
                    directExchangeRate.getRate()
            );
        }

        Optional<ExchangeRate> optReverseExchangeRate = jdbcExchangeRateDao.findByCodes(targetCurrencyCode, baseCurrencyCode);
        if (optReverseExchangeRate.isPresent()) {
            ExchangeRate reverseExchangeRate = optReverseExchangeRate.get();
            BigDecimal reverseRate = reverseExchangeRate.getRate();
            BigDecimal rate = BigDecimal.ONE.divide(reverseRate, EXCHANGE_RATE_SCALE, RoundingMode.HALF_UP);

            return new ExchangeRateResult(
                    reverseExchangeRate.getTargetCurrency(),
                    reverseExchangeRate.getBaseCurrency(),
                    rate
            );
        }

        Optional<ExchangeRate> optReferenceToBaseExchangeRate = jdbcExchangeRateDao.findByCodes(REFERENCE_CURRENCY_CODE, baseCurrencyCode);
        Optional<ExchangeRate> optReferenceToTargetExchangeRate = jdbcExchangeRateDao.findByCodes(REFERENCE_CURRENCY_CODE, targetCurrencyCode);
        if(optReferenceToBaseExchangeRate.isPresent() && optReferenceToTargetExchangeRate.isPresent()) {
            ExchangeRate usdToBaseExchangeRate = optReferenceToBaseExchangeRate.get();
            ExchangeRate usdToTargetExchangeRate = optReferenceToTargetExchangeRate.get();

            BigDecimal baseRate = usdToBaseExchangeRate.getRate();
            BigDecimal targetRate = usdToTargetExchangeRate.getRate();

            BigDecimal rate = targetRate.divide(baseRate, EXCHANGE_RATE_SCALE, RoundingMode.HALF_UP);

            return new ExchangeRateResult(
                    usdToBaseExchangeRate.getTargetCurrency(),
                    usdToTargetExchangeRate.getTargetCurrency(),
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