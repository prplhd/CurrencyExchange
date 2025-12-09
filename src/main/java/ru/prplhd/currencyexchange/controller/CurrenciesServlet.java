package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dao.JdbcCurrencyDao;
import ru.prplhd.currencyexchange.dto.CurrencyRequestDto;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.service.CurrencyService;
import ru.prplhd.currencyexchange.webutil.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.RequestParamExtractor;
import ru.prplhd.currencyexchange.webutil.ResponseWriter;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ResponseWriter responseWriter =  new JsonResponseWriter();
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        CurrencyDao currencyDao = new JdbcCurrencyDao();
        currencyService = new CurrencyService(currencyDao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CurrencyResponseDto> currencyDtos = currencyService.getAllCurrencies();
        responseWriter.write(
                currencyDtos,
                response,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrencyRequestDto currencyRequestDto = createCurrencyDto(request);
        CurrencyResponseDto currencyDto = currencyService.createCurrency(currencyRequestDto);
        responseWriter.write(
                currencyDto,
                response,
                HttpServletResponse.SC_CREATED
        );
    }

    private CurrencyRequestDto createCurrencyDto(HttpServletRequest request) {
        String name = RequestParamExtractor.requiredParam(request, "name");
        String code = RequestParamExtractor.requiredUppercaseParam(request, "code");
        String sign = RequestParamExtractor.optionalParam(request, "sign");

        return new CurrencyRequestDto(name, code, sign);
    }
}