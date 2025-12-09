package ru.prplhd.currencyexchange.mapper;

import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.dto.ExchangeResponseDto;
import ru.prplhd.currencyexchange.model.Exchange;

public final class ExchangeMapper {
    private ExchangeMapper() {}

    public static ExchangeResponseDto toDto(Exchange exchange) {
        CurrencyResponseDto baseCurrencyDto = CurrencyMapper.toDto(exchange.baseCurrency());
        CurrencyResponseDto targetCurrencyDto = CurrencyMapper.toDto(exchange.targetCurrency());
        return new ExchangeResponseDto(
                baseCurrencyDto,
                targetCurrencyDto,
                exchange.rate(),
                exchange.amount(),
                exchange.convertedAmount()
        );
    }
}