package ru.prplhd.currencyexchange.dto;

public record CreateExchangeRateDto(String baseCurrencyCode, String targetCurrencyCode, String rate) {}