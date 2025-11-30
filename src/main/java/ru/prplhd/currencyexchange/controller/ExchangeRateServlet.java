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
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.exception.ValidationException;
import ru.prplhd.currencyexchange.service.ExchangeRateService;
import ru.prplhd.currencyexchange.util.JsonResponseWriter;

import java.io.IOException;
import java.util.Locale;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        exchangeRateService = new ExchangeRateService(exchangeRateDao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String currencyPairCode = extractCurrencyPairCode(request);
            ExchangeRateDto exchangeRateDto = exchangeRateService.getByCurrencyPairCode(currencyPairCode);
            JsonResponseWriter.write(
                    exchangeRateDto,
                    response,
                    HttpServletResponse.SC_OK
            );

        } catch (BadRequestException | ValidationException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (ExchangeRateNotFoundException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_NOT_FOUND
            );

        } catch (DataAccessException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto("Failed to load exchange rate. Please try again later."),
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private String extractCurrencyPairCode(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new BadRequestException("Invalid currency path. Please use /exchangeRate/{CODE}{CODE}");
        }
        return pathInfo.substring(1).trim().toUpperCase(Locale.ROOT);
    }
}
