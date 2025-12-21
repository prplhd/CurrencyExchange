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
import ru.prplhd.currencyexchange.mapper.ExchangeRateMapper;
import ru.prplhd.currencyexchange.model.ExchangeRate;
import ru.prplhd.currencyexchange.service.ExchangeRateService;
import ru.prplhd.currencyexchange.webutil.request.RequestParamExtractor;
import ru.prplhd.currencyexchange.webutil.response.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.response.ResponseWriter;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
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
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
        List<ExchangeRateResponseDto> exchangeRateResponseDtos = exchangeRates.stream()
                .map(ExchangeRateMapper.INSTANCE::toDto)
                .toList();

        responseWriter.write(
                exchangeRateResponseDtos,
                response,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRateRequestDto exchangeRateRequestDto = createExchangeRateRequestDto(request);
        ExchangeRate exchangeRate = exchangeRateService.createExchangeRate(exchangeRateRequestDto);
        ExchangeRateResponseDto exchangeRateResponseDto = ExchangeRateMapper.INSTANCE.toDto(exchangeRate);

        responseWriter.write(
                exchangeRateResponseDto,
                response,
                HttpServletResponse.SC_CREATED);
    }

    private ExchangeRateRequestDto createExchangeRateRequestDto(HttpServletRequest request) {
        String baseCurrencyCode = RequestParamExtractor.requiredUppercaseParam(request, "baseCurrencyCode");
        String targetCurrencyCode = RequestParamExtractor.requiredUppercaseParam(request, "targetCurrencyCode");
        String rate = RequestParamExtractor.requiredParam(request, "rate");

        return new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
    }
}