package ru.prplhd.currencyexchange.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.dto.ExchangeRateResponseDto;
import ru.prplhd.currencyexchange.model.ExchangeRate;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExchangeRateMapper {

    public static ExchangeRateResponseDto toDto(ExchangeRate exchangeRate) {
        CurrencyResponseDto baseCurrencyDto = CurrencyMapper.toDto(exchangeRate.getBaseCurrency());
        CurrencyResponseDto targetCurrencyDto = CurrencyMapper.toDto(exchangeRate.getTargetCurrency());
        return new ExchangeRateResponseDto(
          exchangeRate.getId(),
          baseCurrencyDto,
          targetCurrencyDto,
          exchangeRate.getRate()
        );
    }

    public static List<ExchangeRateResponseDto> toDtos(List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .map(ExchangeRateMapper::toDto)
                .toList();
    }
}