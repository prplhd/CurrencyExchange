package ru.prplhd.currencyexchange.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.prplhd.currencyexchange.dto.ExchangeRequestDto;
import ru.prplhd.currencyexchange.exception.ValidationException;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExchangeValidator {
    private static final int MAX_AMOUNT_FRACTIONAL_PART_DIGITS = 6;
    private static final int MAX_AMOUNT_INTEGER_PART_DIGITS = 12;

    public static void validateExchangeRequestDto(ExchangeRequestDto exchangeRequestDto) {
        String baseCurrencyCode = exchangeRequestDto.baseCurrencyCode();
        CurrencyValidator.validateCurrencyCode(baseCurrencyCode);

        String targetCurrencyCode = exchangeRequestDto.targetCurrencyCode();
        CurrencyValidator.validateCurrencyCode(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ValidationException("Invalid currency pair. 'from' and 'to' currency codes must be different.");
        }

        validateAmount(exchangeRequestDto.amount());
    }

    public static void validateAmount(String rawAmount) {
        BigDecimal amount;
        try {
            amount = new BigDecimal(rawAmount);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid format. Amount must be a valid decimal number.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid format. Amount must be positive.");
        }

        int fractionalPartDigits = amount.scale();
        if (fractionalPartDigits > MAX_AMOUNT_FRACTIONAL_PART_DIGITS) {
            throw new ValidationException("Invalid format. Amount must have at most %d digits after the decimal point"
                    .formatted(MAX_AMOUNT_FRACTIONAL_PART_DIGITS));
        }

        int integerPartDigits = amount.precision() - fractionalPartDigits;
        if (integerPartDigits > MAX_AMOUNT_INTEGER_PART_DIGITS) {
            throw new ValidationException("Invalid format. Amount must have at most %d digits in the integer part"
                    .formatted(MAX_AMOUNT_INTEGER_PART_DIGITS));
        }
    }
}