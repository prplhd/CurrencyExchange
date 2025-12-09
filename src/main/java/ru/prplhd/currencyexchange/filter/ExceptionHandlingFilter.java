package ru.prplhd.currencyexchange.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import ru.prplhd.currencyexchange.dto.ErrorResponseDto;
import ru.prplhd.currencyexchange.exception.BadRequestException;
import ru.prplhd.currencyexchange.exception.CurrencyAlreadyExistsException;
import ru.prplhd.currencyexchange.exception.CurrencyNotFoundException;
import ru.prplhd.currencyexchange.exception.DatabaseException;
import ru.prplhd.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import ru.prplhd.currencyexchange.exception.ExchangeRateNotFoundException;
import ru.prplhd.currencyexchange.exception.ValidationException;
import ru.prplhd.currencyexchange.webutil.JsonResponseWriter;
import ru.prplhd.currencyexchange.webutil.ResponseWriter;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {
    private final ResponseWriter responseWriter =  new JsonResponseWriter();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            chain.doFilter(req, res);

        } catch (BadRequestException | ValidationException e) {
            responseWriter.write(
                    new ErrorResponseDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            responseWriter.write(new ErrorResponseDto(
                            e.getMessage()),
                    response,
                    HttpServletResponse.SC_NOT_FOUND
            );

        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            responseWriter.write(
                    new ErrorResponseDto(e.getMessage()),
                    response,
                    HttpServletResponse.SC_CONFLICT
            );

        } catch (DatabaseException e) {
            e.printStackTrace();
            responseWriter.write(
                    new ErrorResponseDto("Failed to process request. Please try again later."),
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

        } catch (Exception e) {
            e.printStackTrace();
            responseWriter.write(
                    new ErrorResponseDto("Unexpected server error. Please try again later."),
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}