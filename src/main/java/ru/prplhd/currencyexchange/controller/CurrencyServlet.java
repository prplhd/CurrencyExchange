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
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.exception.ValidationException;
import ru.prplhd.currencyexchange.service.CurrencyService;
import ru.prplhd.currencyexchange.util.JsonResponseWriter;

import java.io.IOException;
import java.util.Locale;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        CurrencyDao dao = new CurrencyDao();
        currencyService = new CurrencyService(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String code = extractCurrencyCode(request);
            CurrencyDto currencyDto = currencyService.getCurrencyByCode(code);
            JsonResponseWriter.write(
                    currencyDto,
                    response,
                    HttpServletResponse.SC_OK
            );

        } catch (BadRequestException | ValidationException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (CurrencyNotFoundException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_NOT_FOUND
            );

        } catch (DataAccessException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto("Failed to load currency. Please try again later."),
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private String extractCurrencyCode(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new BadRequestException("Invalid currency path. Please use /currency/{CODE}");
        }
        return pathInfo.substring(1).trim().toUpperCase(Locale.ROOT);
    }
}
