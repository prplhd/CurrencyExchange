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
import ru.prplhd.currencyexchange.service.ExchangeRateService;
import ru.prplhd.currencyexchange.webutil.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.RequestParamExtractor;
import ru.prplhd.currencyexchange.webutil.ResponseWriter;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ResponseWriter responseWriter =  new JsonResponseWriter();
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
        responseWriter.write(
                exchangeRateDtos,
                response,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CreateExchangeRateDto createExchangeRateDto = createExchangeRateDto(request);
        ExchangeRateDto exchangeRateDto = exchangeRateService.createExchangeRate(createExchangeRateDto);
        responseWriter.write(
                exchangeRateDto,
                response,
                HttpServletResponse.SC_CREATED);
    }

    private CreateExchangeRateDto createExchangeRateDto(HttpServletRequest request) {
        String baseCurrencyCode = RequestParamExtractor.requiredUppercaseParam(request, "baseCurrencyCode");
        String targetCurrencyCode = RequestParamExtractor.requiredUppercaseParam(request, "targetCurrencyCode");
        String rate = RequestParamExtractor.requiredParam(request, "rate");

        return new CreateExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);
    }
}