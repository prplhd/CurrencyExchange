package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CreateCurrencyDto;
import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.service.CurrencyService;
import ru.prplhd.currencyexchange.util.JsonResponseWriter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CurrencyDto> currencyDtos = currencyService.getAllCurrencies();
        JsonResponseWriter.write(
                currencyDtos,
                response,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CreateCurrencyDto createCurrencyDto = createCurrencyDto(request);
        CurrencyDto currencyDto = currencyService.createCurrency(createCurrencyDto);
        JsonResponseWriter.write(
                currencyDto,
                response,
                HttpServletResponse.SC_CREATED
        );
    }

    private CreateCurrencyDto createCurrencyDto(HttpServletRequest request) {
        String name = request.getParameter("name");
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Missing or empty required parameter 'name'");
        }
        name = name.trim();

        String code = request.getParameter("code");
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Missing or empty required parameter 'code'");
        }
        code = code.trim().toUpperCase(Locale.ROOT);

        String sign = request.getParameter("sign");
        if (sign != null) {
            sign = sign.trim();
            if (sign.isBlank()) {
                sign = null;
            }
        }

        return new CreateCurrencyDto(name, code, sign);
    }
}
