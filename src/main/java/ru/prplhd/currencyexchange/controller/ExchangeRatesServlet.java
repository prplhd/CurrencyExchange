package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.CreateExchangeRateDto;
import ru.prplhd.currencyexchange.dto.ExchangeRateDto;
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.service.ExchangeRateService;
import ru.prplhd.currencyexchange.util.JsonResponseWriter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        exchangeRateService = new ExchangeRateService(exchangeRateDao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ExchangeRateDto> exchangeRateDtos = exchangeRateService.getAllExchangeRates();
        JsonResponseWriter.write(
                exchangeRateDtos,
                response,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CreateExchangeRateDto createExchangeRateDto = createExchangeRateDto(request);
        ExchangeRateDto exchangeRateDto = exchangeRateService.createExchangeRate(createExchangeRateDto);
        JsonResponseWriter.write(
                exchangeRateDto,
                response,
                HttpServletResponse.SC_CREATED);
    }

    private CreateExchangeRateDto createExchangeRateDto(HttpServletRequest request) {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new BadRequestException("Missing or empty required parameter 'baseCurrencyCode'");
        }
        baseCurrencyCode = baseCurrencyCode.trim().toUpperCase(Locale.ROOT);

        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new BadRequestException("Missing or empty required parameter 'targetCurrencyCode'");
        }
        targetCurrencyCode = targetCurrencyCode.trim().toUpperCase(Locale.ROOT);

        String rate = request.getParameter("rate");
        if (rate == null || rate.isBlank()) {
            throw new BadRequestException("Missing or empty required parameter 'rate'");
        }
        rate = rate.trim();

        return new CreateExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);
    }
}
