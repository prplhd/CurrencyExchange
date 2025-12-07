package ru.prplhd.currencyexchange.webutil;

import jakarta.servlet.http.HttpServletRequest;
import ru.prplhd.currencyexchange.exception.BadRequestException;

import java.util.Locale;

public final class RequestParamExtractor {
    private  RequestParamExtractor() {}

    public static String requiredParam(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Missing or empty required parameter '" + paramName + "'");
        }
        return value.trim();
    }

    public static String requiredUppercaseParam(HttpServletRequest request, String paramName) {
        return requiredParam(request, paramName).toUpperCase(Locale.ROOT);
    }

    public static String optionalParam(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null) {
            return null;
        }
        value = value.trim();

        return value.isBlank() ? null : value;
    }
}