package ru.prplhd.currencyexchange.dto;

public record ExchangeRequestDto(String fromCurrencyCode, String toCurrencyCode, String amount) {}