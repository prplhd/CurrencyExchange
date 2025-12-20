package ru.prplhd.currencyexchange.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ru.prplhd.currencyexchange.dao.CurrencyDao;
import ru.prplhd.currencyexchange.dao.JdbcCurrencyDao;
import ru.prplhd.currencyexchange.dto.CurrencyRequestDto;
import ru.prplhd.currencyexchange.dto.CurrencyResponseDto;
import ru.prplhd.currencyexchange.mapper.CurrencyMapper;
import ru.prplhd.currencyexchange.model.Currency;
import ru.prplhd.currencyexchange.validation.CurrencyValidator;
import ru.prplhd.currencyexchange.webutil.response.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.request.RequestParamExtractor;
import ru.prplhd.currencyexchange.webutil.response.ResponseWriter;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ResponseWriter responseWriter =  new JsonResponseWriter();
    private CurrencyDao currencyDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        currencyDao = new JdbcCurrencyDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Currency> currencies = currencyDao.findAll();
        List<CurrencyResponseDto> currencyResponseDtos = CurrencyMapper.toDtos(currencies);

        responseWriter.write(
                currencyResponseDtos,
                response,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrencyRequestDto currencyRequestDto = createCurrencyRequestDto(request);
        CurrencyValidator.validateCurrencyRequestDto(currencyRequestDto);

        Currency currency = currencyDao.save(CurrencyMapper.toModel(currencyRequestDto));

        log.info("Currency created: code={}, name={}, sign={}",
                currency.getCode(), currency.getName(), currency.getSign());

        CurrencyResponseDto currencyResponseDto = CurrencyMapper.toDto(currency);

        responseWriter.write(
                currencyResponseDto,
                response,
                HttpServletResponse.SC_CREATED
        );
    }

    private CurrencyRequestDto createCurrencyRequestDto(HttpServletRequest request) {
        String name = RequestParamExtractor.requiredParam(request, "name");
        String code = RequestParamExtractor.requiredUppercaseParam(request, "code");
        String sign = RequestParamExtractor.optionalParam(request, "sign");

        return new CurrencyRequestDto(name, code, sign);
    }
}