package ru.prplhd.currencyexchange.validation;

import ru.prplhd.currencyexchange.dto.CurrencyRequestDto;
import ru.prplhd.currencyexchange.exception.ValidationException;

import java.util.regex.Pattern;

public final class CurrencyValidator {
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
    private static final Pattern CURRENCY_NAME_PATTERN = Pattern.compile("^[A-Za-z ]+$");

    private static final int MIN_CURRENCY_NAME_LENGTH = 3;
    private static final int MAX_CURRENCY_NAME_LENGTH = 50;
    private static final int MAX_CURRENCY_SIGN_LENGTH = 3;

    private CurrencyValidator() {}

    public static void validateCurrencyCode(String code) {
        if (!CURRENCY_CODE_PATTERN.matcher(code).matches()) {
            throw new ValidationException("Invalid format. Currency code must consist of 3 uppercase English letters.");
        }
    }

    public static void validateCurrencyRequestDto(CurrencyRequestDto currencyRequestDto) {
        String name = currencyRequestDto.name();
        if (name.length() < MIN_CURRENCY_NAME_LENGTH || name.length() > MAX_CURRENCY_NAME_LENGTH) {
            throw new ValidationException("Invalid name length. Currency name must be between %d and %d characters."
                    .formatted(MIN_CURRENCY_NAME_LENGTH, MAX_CURRENCY_NAME_LENGTH));
        }

        if (!CURRENCY_NAME_PATTERN.matcher(name).matches()) {
            throw new ValidationException("Invalid format. Currency name must consist of English letters.");
        }

        String code = currencyRequestDto.code();
        validateCurrencyCode(code);


        String sign = currencyRequestDto.sign();
        if (sign != null && sign.length() > MAX_CURRENCY_SIGN_LENGTH) {
            throw new ValidationException("Invalid sign length. When provided, currency sign must not be longer than %d characters."
                    .formatted(MAX_CURRENCY_SIGN_LENGTH));
        }
    }
}