package ru.prplhd.currencyexchange.mapper;

import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.model.Currency;

public class CurrencyMapper {
    public static CurrencyDto toDto(Currency currency) {
        return new CurrencyDto(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }
}
