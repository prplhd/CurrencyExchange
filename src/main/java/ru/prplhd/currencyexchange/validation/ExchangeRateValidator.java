package ru.prplhd.currencyexchange.validation;

import ru.prplhd.currencyexchange.dto.CreateExchangeRateDto;
import ru.prplhd.currencyexchange.exception.ValidationException;

import java.math.BigDecimal;

public final class ExchangeRateValidator {
    public static final int CURRENCY_CODE_LENGTH = 3;
    private static final int CURRENCY_PAIR_CODE_LENGTH = CURRENCY_CODE_LENGTH * 2;

    private static final int MAX_RATE_FRACTIONAL_PART_DIGITS = 6;
    private static final int MAX_RATE_INTEGER_PART_DIGITS = 6;

    private ExchangeRateValidator() {}

    public static void validateCurrencyPairCode(String currencyPairCode) {
        if (currencyPairCode.length() != CURRENCY_PAIR_CODE_LENGTH) {
            throw new ValidationException("Invalid format. Currency pair code must consist of exactly 6 uppercase English letters.");
        }

        String baseCurrencyCode = currencyPairCode.substring(0, CURRENCY_CODE_LENGTH);
        CurrencyValidator.validateCurrencyCode(baseCurrencyCode);

        String targetCurrencyCode = currencyPairCode.substring(CURRENCY_CODE_LENGTH);
        CurrencyValidator.validateCurrencyCode(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ValidationException("Invalid currency pair. Base and target currency codes must be different.");
        }
    }

    public static void validateCreateExchangeRateDto(CreateExchangeRateDto createExchangeRateDto) {
        String baseCurrencyCode = createExchangeRateDto.baseCurrencyCode();
        CurrencyValidator.validateCurrencyCode(baseCurrencyCode);

        String targetCurrencyCode = createExchangeRateDto.targetCurrencyCode();
        CurrencyValidator.validateCurrencyCode(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ValidationException("Invalid currency pair. Base and target currency codes must be different.");
        }

        BigDecimal rate;
        try {
            rate = new BigDecimal(createExchangeRateDto.rate());
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
