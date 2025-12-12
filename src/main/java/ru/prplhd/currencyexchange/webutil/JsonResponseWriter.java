package ru.prplhd.currencyexchange.webutil;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class JsonResponseWriter implements ResponseWriter {
    private static final Gson gson = new Gson();

    @Override
    public void write(Object responseBody, HttpServletResponse response, int statusCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        gson.toJson(responseBody, response.getWriter());
    }
}