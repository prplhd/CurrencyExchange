package ru.prplhd.currencyexchange.validation;

import ru.prplhd.currencyexchange.dto.ExchangeRateRequestDto;
import ru.prplhd.currencyexchange.exception.ValidationException;
import ru.prplhd.currencyexchange.model.CurrencyPair;

import java.math.BigDecimal;

public final class ExchangeRateValidator {
    private static final int MAX_RATE_FRACTIONAL_PART_DIGITS = 6;
    private static final int MAX_RATE_INTEGER_PART_DIGITS = 6;

    private ExchangeRateValidator() {}

    public static void validateCurrencyPair(CurrencyPair currencyPair) {
        String baseCurrencyCode = currencyPair.baseCurrencyCode();
        CurrencyValidator.validateCurrencyCode(baseCurrencyCode);

        String targetCurrencyCode = currencyPair.targetCurrencyCode();
        CurrencyValidator.validateCurrencyCode(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ValidationException("Invalid currency pair. Base and target currency codes must be different.");
        }
    }

    public static void validateExchangeRateRequestDto(ExchangeRateRequestDto exchangeRateRequestDto) {
        String baseCurrencyCode = exchangeRateRequestDto.baseCurrencyCode();
        CurrencyValidator.validateCurrencyCode(baseCurrencyCode);

        String targetCurrencyCode = exchangeRateRequestDto.targetCurrencyCode();
        CurrencyValidator.validateCurrencyCode(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ValidationException("Invalid currency pair. Base and target currency codes must be different.");
        }

        validateRate(exchangeRateRequestDto.rate());
    }

    public static void validateRate(String rawRate) {
        BigDecimal rate = null;
        try {
            rate = new BigDecimal(rawRate);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid format. Rate must be a valid decimal number.");
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid format. Rate must be positive.");
        }

        int fractionalPartDigits = rate.scale();
        if (fractionalPartDigits > MAX_RATE_FRACTIONAL_PART_DIGITS) {
            throw new ValidationException("Invalid format. Rate must have at most %d digits after the decimal point"
                    .formatted(MAX_RATE_FRACTIONAL_PART_DIGITS));
        }

        int integerPartDigits = rate.precision() - fractionalPartDigits;
        if (integerPartDigits > MAX_RATE_INTEGER_PART_DIGITS) {
            throw new ValidationException("Invalid format. Rate must have at most %d digits in the integer part"
                    .formatted(MAX_RATE_INTEGER_PART_DIGITS));
        }
    }
}