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
import ru.prplhd.currencyexchange.service.CurrencyService;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    Gson gson = new Gson();
    CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        CurrencyDao dao = new CurrencyDao();
        this.currencyService = new CurrencyService(dao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        List<CurrencyDto> currencyDtos = currencyService.getAllCurrencies();
        String json = gson.toJson(currencyDtos);

//        String json = """
//            [
//              {"code":"USD","name":"Доллар США"},
//              {"code":"EUR","name":"Евро"}
//            ]
//            """;

        resp.getWriter().write(json);
    }
}
