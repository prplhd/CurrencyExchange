package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.ExchangeRateDao;
import ru.prplhd.currencyexchange.dto.ErrorMessageDto;
import ru.prplhd.currencyexchange.dto.ExchangeRateDto;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.service.ExchangeRateService;
import ru.prplhd.currencyexchange.utils.JsonResponseWriter;

import java.io.IOException;
import java.util.List;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<ExchangeRateDto> exchangeRateDtos = exchangeRateService.getAllExchangeRates();
            JsonResponseWriter.write(exchangeRateDtos, response, HttpServletResponse.SC_OK);

        } catch (DataAccessException e) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto("Failed to load exchange rates. Please try again later.");
            JsonResponseWriter.write(errorMessageDto, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
