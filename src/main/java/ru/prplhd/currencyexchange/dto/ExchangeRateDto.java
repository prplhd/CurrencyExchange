package ru.prplhd.currencyexchange.dto;

import java.math.BigDecimal;

public record ExchangeRateDto(Long id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {}
