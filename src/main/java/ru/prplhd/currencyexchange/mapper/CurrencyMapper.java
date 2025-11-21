package ru.prplhd.currencyexchange.mapper;

import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.model.Currency;

public final class CurrencyMapper {
    private CurrencyMapper() {}

    public static CurrencyDto toDto(Currency currency) {
        if (currency == null) {
            throw new NullPointerException("Currency must not be null.");
        }

        return new CurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }
}
