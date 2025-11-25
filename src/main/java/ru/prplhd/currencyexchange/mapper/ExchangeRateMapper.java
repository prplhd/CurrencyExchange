package ru.prplhd.currencyexchange.mapper;

import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.dto.ExchangeRateDto;
import ru.prplhd.currencyexchange.model.ExchangeRate;

import java.util.List;

public final class ExchangeRateMapper {
    private ExchangeRateMapper() {}

    public static ExchangeRateDto toDto(ExchangeRate exchangeRate) {
        CurrencyDto baseCurrencyDto = CurrencyMapper.toDto(exchangeRate.baseCurrency());
        CurrencyDto targetCurrencyDto = CurrencyMapper.toDto(exchangeRate.targetCurrency());
        return new ExchangeRateDto(
          exchangeRate.id(),
          baseCurrencyDto,
          targetCurrencyDto,
          exchangeRate.rate()
        );
    }

    public static List<ExchangeRateDto> toDtos(List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .map(ExchangeRateMapper::toDto)
                .toList();
    }

}
