package ru.prplhd.currencyexchange.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@WebFilter("/*")
public class RequestIdFilter extends HttpFilter {
    private static final String HEADER = "X-Request-Id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String rid = request.getHeader(HEADER);

        if (rid == null || rid.isBlank()) {
            rid = String.format("%012d", ThreadLocalRandom.current().nextLong(1_000_000_000_000L));
        }

        MDC.put("rid", rid);

        response.setHeader(HEADER, rid);

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove("rid");
        }
    }
}
