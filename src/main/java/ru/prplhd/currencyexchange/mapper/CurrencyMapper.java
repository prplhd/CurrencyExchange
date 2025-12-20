package ru.prplhd.currencyexchange.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.prplhd.currencyexchange.dto.CurrencyRequestDto;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.model.Currency;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CurrencyMapper {

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

    public static Currency toModel(CurrencyRequestDto dto) {
        return new Currency(
                null,
                dto.code(),
                dto.name(),
                dto.sign()
        );
    }
}