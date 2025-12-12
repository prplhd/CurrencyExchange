package ru.prplhd.currencyexchange.dto;

public record ExchangeRequestDto(String baseCurrencyCode, String targetCurrencyCode, String amount) {}