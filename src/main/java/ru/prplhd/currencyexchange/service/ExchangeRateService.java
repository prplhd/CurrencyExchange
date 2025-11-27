package ru.prplhd.currencyexchange.service;

import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.CreateExchangeRateDto;
import ru.prplhd.currencyexchange.dto.ExchangeRateDto;
import ru.prplhd.currencyexchange.exception.ValidationException;
import ru.prplhd.currencyexchange.mapper.ExchangeRateMapper;
import ru.prplhd.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateService {
    private static final String CURRENCY_CODE_REGEX = "[A-Z]{3}";
    private static final int MAX_RATE_FRACTIONAL_PART_DIGITS = 6;
    private static final int MAX_RATE_INTEGER_PART_DIGITS = 6;
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public List<ExchangeRateDto> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
        return ExchangeRateMapper.toDtos(exchangeRates);
    }

    public ExchangeRateDto createExchangeRate(CreateExchangeRateDto createExchangeRateDto) {
        validateCreateExchangeRateDto(createExchangeRateDto);

        String baseCurrencyCode = createExchangeRateDto.baseCurrencyCode();
        String targetCurrencyCode = createExchangeRateDto.targetCurrencyCode();
        BigDecimal rate = new BigDecimal(createExchangeRateDto.rate());

        ExchangeRate exchangeRate = exchangeRateDao.insert(baseCurrencyCode, targetCurrencyCode, rate);
        return ExchangeRateMapper.toDto(exchangeRate);
    }

    private void validateCreateExchangeRateDto(CreateExchangeRateDto createExchangeRateDto) {
        String baseCurrencyCode = createExchangeRateDto.baseCurrencyCode();
        String targetCurrencyCode = createExchangeRateDto.targetCurrencyCode();

        if (!baseCurrencyCode.matches(CURRENCY_CODE_REGEX) || !targetCurrencyCode.matches(CURRENCY_CODE_REGEX)) {
            throw new ValidationException("Invalid format. Currency code must consist of 3 uppercase English letters.");
        }
        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ValidationException("Invalid currency pair. Base and target currency codes must be different.");
        }

        BigDecimal rate;
        try {
            rate = new BigDecimal(createExchangeRateDto.rate());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid format. Rate must be a valid decimal number.");
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid format. Rate must be positive.");
        }

        int fractionalPartDigits = rate.scale();
        if (fractionalPartDigits > MAX_RATE_FRACTIONAL_PART_DIGITS) {
            throw new ValidationException("Invalid format. Rate must have at most %d digits after the decimal point"
                    .formatted(MAX_RATE_FRACTIONAL_PART_DIGITS));
        }

        int integerPartDigits = rate.precision() - fractionalPartDigits;
        if (integerPartDigits > MAX_RATE_INTEGER_PART_DIGITS) {
            throw new ValidationException("Invalid format. Rate must have at most %d digits in the integer part"
                    .formatted(MAX_RATE_INTEGER_PART_DIGITS));
        }
    }
}
