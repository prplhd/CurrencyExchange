package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.JdbcExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ExchangeResponseDto;
import ru.prplhd.currencyexchange.dto.ExchangeRequestDto;
import ru.prplhd.currencyexchange.service.ExchangeService;
import ru.prplhd.currencyexchange.webutil.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.RequestParamExtractor;
import ru.prplhd.currencyexchange.webutil.ResponseWriter;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ResponseWriter responseWriter =  new JsonResponseWriter();

    private ExchangeService exchangeService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
        exchangeService = new ExchangeService(jdbcExchangeRateDao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRequestDto exchangeRequestDto = createExchangeRequestDto(request);
        ExchangeResponseDto exchangeResponseDto = exchangeService.getExchange(exchangeRequestDto);
        responseWriter.write(
                exchangeResponseDto,
                response,
                HttpServletResponse.SC_OK
        );
    }

    private ExchangeRequestDto createExchangeRequestDto(HttpServletRequest request) {
        String fromCurrencyCode = RequestParamExtractor.requiredUppercaseParam(request, "from");
        String toCurrencyCode = RequestParamExtractor.requiredUppercaseParam(request, "to");
        String amount = RequestParamExtractor.requiredParam(request, "amount");

        return new ExchangeRequestDto(fromCurrencyCode, toCurrencyCode, amount);
    }
}