package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dao.JdbcCurrencyDao;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.mapper.CurrencyMapper;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.validation.CurrencyValidator;
import ru.prplhd.currencyexchange.webutil.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.ResponseWriter;

import java.io.IOException;
import java.util.Locale;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ResponseWriter responseWriter =  new JsonResponseWriter();
    private CurrencyDao currencyDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        currencyDao = new JdbcCurrencyDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = extractCurrencyCode(request);
        CurrencyValidator.validateCurrencyCode(code);

        Currency currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code '%s' not found".formatted(code)));
        CurrencyResponseDto currencyResponseDto = CurrencyMapper.toDto(currency);

        responseWriter.write(
                currencyResponseDto,
                response,
                HttpServletResponse.SC_OK
        );
    }

    private String extractCurrencyCode(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new BadRequestException("Invalid currency path. Please use /currency/{CODE}");
        }
        return pathInfo.substring(1).trim().toUpperCase(Locale.ROOT);
    }
}