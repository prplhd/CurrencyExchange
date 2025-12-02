package ru.prplhd.currencyexchange.mapper;

import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.dto.ExchangeDto;
import ru.prplhd.currencyexchange.model.Exchange;

public final class ExchangeMapper {
    private ExchangeMapper() {}

    public static ExchangeDto toDto(Exchange exchange) {
        CurrencyDto baseCurrencyDto = CurrencyMapper.toDto(exchange.baseCurrency());
        CurrencyDto targetCurrencyDto = CurrencyMapper.toDto(exchange.targetCurrency());
        return new ExchangeDto(
                baseCurrencyDto,
                targetCurrencyDto,
                exchange.rate(),
                exchange.amount(),
                exchange.convertedAmount()
        );
    }
}