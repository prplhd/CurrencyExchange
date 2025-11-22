package ru.prplhd.currencyexchange.mapper;

import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.model.Currency;
import java.util.List;

public final class CurrencyMapper {
    private CurrencyMapper() {}

    public static CurrencyDto toDto(Currency currency) {
        return new CurrencyDto(
                currency.id(),
                currency.code(),
                currency.name(),
                currency.sign()
        );
    }

    public static List<CurrencyDto> toDtos(List<Currency> currencies) {
        return currencies.stream()
                .map(CurrencyMapper::toDto)
                .toList();
    }
}
