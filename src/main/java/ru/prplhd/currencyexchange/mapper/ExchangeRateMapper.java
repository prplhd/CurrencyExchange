package ru.prplhd.currencyexchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.prplhd.currencyexchange.dto.ExchangeRateResponseDto;
import ru.prplhd.currencyexchange.model.ExchangeRate;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper( ExchangeRateMapper.class );

    ExchangeRateResponseDto toDto(ExchangeRate exchangeRate);
}