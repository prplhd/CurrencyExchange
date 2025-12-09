package ru.prplhd.currencyexchange.mapper;

import ru.prplhd.currencyexchange.dto.CurrencyRequestDto;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.model.Currency;
import java.util.List;

public final class CurrencyMapper {
    private CurrencyMapper() {}

    public static CurrencyResponseDto toDto(Currency currency) {
        return new CurrencyResponseDto(
                currency.getId(),
                currency.getCode(),
                currency.getName(),
                currency.getSign()
        );
    }

    public static List<CurrencyResponseDto> toDtos(List<Currency> currencies) {
        return currencies.stream()
                .map(CurrencyMapper::toDto)
                .toList();
    }

    public static Currency fromCreateDto(CurrencyRequestDto dto) {
        return new Currency(
                null,
                dto.code(),
                dto.name(),
                dto.sign()
        );
    }
}