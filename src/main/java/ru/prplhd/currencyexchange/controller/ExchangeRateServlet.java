package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dao.JdbcCurrencyDao;
import ru.prplhd.currencyexchange.dao.JdbcExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeRateRequestDto;
import ru.prplhd.currencyexchange.dto.ExchangeRateResponseDto;
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.exception.ValidationException;
import ru.prplhd.currencyexchange.mapper.ExchangeRateMapper;
import ru.prplhd.currencyexchange.model.CurrencyPair;
import ru.prplhd.currencyexchange.model.ExchangeRate;
import ru.prplhd.currencyexchange.service.ExchangeRateService;
import ru.prplhd.currencyexchange.validation.CurrencyValidator;
import ru.prplhd.currencyexchange.validation.ExchangeRateValidator;
import ru.prplhd.currencyexchange.webutil.response.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.response.ResponseWriter;

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final int CURRENCY_CODE_LENGTH = 3;

    private final ResponseWriter responseWriter =  new JsonResponseWriter();
    private ExchangeRateService exchangeRateService;
    private ExchangeRateDao exchangeRateDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        CurrencyDao currencyDao = new JdbcCurrencyDao();
        exchangeRateDao = new JdbcExchangeRateDao();
        exchangeRateService = new ExchangeRateService(currencyDao, exchangeRateDao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrencyPair currencyPair = extractCurrencyPair(request);
        ExchangeRateValidator.validateCurrencyPair(currencyPair);

        String baseCurrencyCode = currencyPair.baseCurrencyCode();
        String targetCurrencyCode = currencyPair.targetCurrencyCode();

        ExchangeRate exchangeRate = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate with codes '%s' and '%s' not found"
                        .formatted(baseCurrencyCode, targetCurrencyCode)));

        ExchangeRateResponseDto exchangeRateResponseDto = ExchangeRateMapper.INSTANCE.toDto(exchangeRate);

        responseWriter.write(
                exchangeRateResponseDto,
                response,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrencyPair currencyPair = extractCurrencyPair(request);
        String rate = extractRate(request);
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(currencyPair.baseCurrencyCode(), currencyPair.targetCurrencyCode(), rate);

        ExchangeRate exchangeRate = exchangeRateService.updateExchangeRate(exchangeRateRequestDto);

        ExchangeRateResponseDto exchangeRateResponseDto = ExchangeRateMapper.INSTANCE.toDto(exchangeRate);

        responseWriter.write(
                exchangeRateResponseDto,
                response,
                HttpServletResponse.SC_OK
        );
    }

    private CurrencyPair extractCurrencyPair(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new BadRequestException("Invalid currency path. Please use /exchangeRate/{CODE}{CODE}");
        }

        String currencyPairCode = pathInfo.substring(1).trim();

        String baseCurrencyCode = currencyPairCode.substring(0, CURRENCY_CODE_LENGTH);
        CurrencyValidator.validateCurrencyCode(baseCurrencyCode);

        String targetCurrencyCode = currencyPairCode.substring(CURRENCY_CODE_LENGTH);
        CurrencyValidator.validateCurrencyCode(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ValidationException("Invalid currency pair. Base and target currency codes must be different.");
        }

        return new CurrencyPair(baseCurrencyCode, targetCurrencyCode);
    }

    // Manual extraction of "rate" from x-www-form-urlencoded PATCH body,
    // because Tomcat does not populate request parameters for PATCH by default.
    private String extractRate(HttpServletRequest request) throws IOException {
        String body = request
                .getReader()
                .lines()
                .collect(Collectors.joining("&"));

        String rate = null;
        for (String pair : body.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && "rate".equals(keyValue[0])) {
                rate = keyValue[1];
                break;
            }
        }

        if (rate == null || rate.isBlank()) {
            throw new BadRequestException("Missing or empty required parameter 'rate'");
        }

        return rate;
    }
}