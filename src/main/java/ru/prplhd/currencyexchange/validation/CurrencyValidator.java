package ru.prplhd.currencyexchange.validation;

import ru.prplhd.currencyexchange.dto.CreateCurrencyDto;
import ru.prplhd.currencyexchange.exception.ValidationException;

public final class CurrencyValidator {
    private static final String CURRENCY_CODE_REGEX = "^[A-Z]{3}$";
    private static final String CURRENCY_NAME_REGEX = "^[A-Za-z ]+$";

    private static final int MIN_CURRENCY_NAME_LENGTH = 3;
    private static final int MAX_CURRENCY_NAME_LENGTH = 50;
    private static final int MAX_CURRENCY_SIGN_LENGTH = 3;

    private CurrencyValidator() {}

    public static void validateCurrencyCode(String code) {
        if (!code.matches(CURRENCY_CODE_REGEX)) {
            throw new ValidationException("Invalid format. Currency code must consist of 3 uppercase English letters.");
        }
    }

    public static void validateCreateCurrencyDto(CreateCurrencyDto createCurrencyDto) {
        String name = createCurrencyDto.name();
        if (name.length() < MIN_CURRENCY_NAME_LENGTH || name.length() > MAX_CURRENCY_NAME_LENGTH) {
            throw new ValidationException("Invalid name length. Currency name must be between %d and %d characters."
                    .formatted(MIN_CURRENCY_NAME_LENGTH, MAX_CURRENCY_NAME_LENGTH));
        }

        if (!name.matches(CURRENCY_NAME_REGEX)) {
            throw new ValidationException("Invalid format. Currency name must consist of English letters.");
        }

        String code = createCurrencyDto.code();
        validateCurrencyCode(code);


        String sign = createCurrencyDto.sign();
        if (sign != null && sign.length() > MAX_CURRENCY_SIGN_LENGTH) {
            throw new ValidationException("Invalid sign length. When provided, currency sign must not be longer than %d characters."
                    .formatted(MAX_CURRENCY_SIGN_LENGTH));
        }
    }
}