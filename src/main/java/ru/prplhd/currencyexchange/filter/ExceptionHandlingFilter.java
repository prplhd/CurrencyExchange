package ru.prplhd.currencyexchange.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dto.ErrorMessageDto;
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.exception.CurrencyAlreadyExistsException;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.DataAccessException;
import ru.prplhd.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.exception.ValidationException;
import ru.prplhd.currencyexchange.util.JsonResponseWriter;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            chain.doFilter(req, res);

        } catch (BadRequestException | ValidationException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            JsonResponseWriter.write(new ErrorMessageDto(
                            e.getMessage()),
                    response,
                    HttpServletResponse.SC_NOT_FOUND
            );

        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            JsonResponseWriter.write(
                    new ErrorMessageDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_CONFLICT
            );

        } catch (DataAccessException e) {
            e.printStackTrace();
            JsonResponseWriter.write(
                    new ErrorMessageDto("Failed to process request. Please try again later."),
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseWriter.write(
                    new ErrorMessageDto("Unexpected server error. Please try again later."),
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
