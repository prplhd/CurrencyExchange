package ru.prplhd.currencyexchange.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dto.CurrencyDto;
import ru.prplhd.currencyexchange.dto.ErrorMessageDto;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.service.CurrencyService;

import java.io.IOException;
import java.util.Locale;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final Gson GSON = new Gson();
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        CurrencyDao dao = new CurrencyDao();
        currencyService = new CurrencyService(dao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorMessageDto errorMessageDto = new ErrorMessageDto("Invalid currency path. Please use /currency/{CODE}");
            GSON.toJson(errorMessageDto, resp.getWriter());
            return;
        }

        String code = path.substring(1).trim().toUpperCase(Locale.ROOT);
        if (!code.matches("[A-Z]{3}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorMessageDto errorMessageDto = new ErrorMessageDto("Invalid format. Currency code must be 3 uppercase letters");
            GSON.toJson(errorMessageDto, resp.getWriter());
            return;
        }

        try {
            CurrencyDto currencyDto = currencyService.getCurrencyByCode(code);
            resp.setStatus(HttpServletResponse.SC_OK);
            GSON.toJson(currencyDto, resp.getWriter());
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ErrorMessageDto errorMessageDto = new ErrorMessageDto(e.getMessage());
            GSON.toJson(errorMessageDto, resp.getWriter());
        } catch (DataAccessException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorMessageDto errorMessageDto = new ErrorMessageDto("Failed to load currency. Please try again later.");
            GSON.toJson(errorMessageDto, resp.getWriter());
        }
    }
}
