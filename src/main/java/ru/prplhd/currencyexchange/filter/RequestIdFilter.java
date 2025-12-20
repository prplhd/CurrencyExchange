package ru.prplhd.currencyexchange.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class RequestIdFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String rid = request.getHeader("rid");

        if (rid == null || rid.isBlank()) {
            rid = String.format("%012d", ThreadLocalRandom.current().nextLong(1_000_000_000_000L));
        }

        MDC.put("rid", rid);

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove("rid");
        }
    }
}
