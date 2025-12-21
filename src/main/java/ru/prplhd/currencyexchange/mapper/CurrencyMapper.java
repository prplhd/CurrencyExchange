package ru.prplhd.currencyexchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.prplhd.currencyexchange.dto.CurrencyRequestDto;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.model.Currency;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper( CurrencyMapper.class );

    CurrencyResponseDto toDto(Currency currency);

    @Mapping(target = "id",  ignore = true)
    Currency toEntity(CurrencyRequestDto dto);
}