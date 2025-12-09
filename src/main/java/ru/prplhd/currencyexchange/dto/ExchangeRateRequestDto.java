package ru.prplhd.currencyexchange.dto;

public record ExchangeRateRequestDto(String baseCurrencyCode, String targetCurrencyCode, String rate) {}