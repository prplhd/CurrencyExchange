package ru.prplhd.currencyexchange.webutil.response;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ResponseWriter {
    void write(Object responseBody, HttpServletResponse response, int statusCode) throws IOException;
}
