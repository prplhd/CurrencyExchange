package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.dto.ErrorMessageDto;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.service.CurrencyService;
import ru.prplhd.currencyexchange.utils.JsonResponseWriter;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        CurrencyDao dao = new CurrencyDao();
        currencyService = new CurrencyService(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<CurrencyDto> currencyDtos = currencyService.getAllCurrencies();
            JsonResponseWriter.write(currencyDtos, response, HttpServletResponse.SC_OK);
        } catch (DataAccessException e) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto("Failed to load currencies. Please try again later.");
            JsonResponseWriter.write(errorMessageDto, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }
    }
}
