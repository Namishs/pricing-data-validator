package com.example.pricingvalidator.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter implements Filter {

    private static final String MDC_REQUEST_ID = "reqId";

    @Override
    public void init(FilterConfig filterConfig) { /* no-op */ }

    @Override
    public void destroy() { /* no-op */ }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestId = UUID.randomUUID().toString();
        MDC.put(MDC_REQUEST_ID, requestId);

        long start = System.currentTimeMillis();
        HttpServletRequest http = (HttpServletRequest) request;

        try {
            log.info("[{}] {} {}", requestId, http.getMethod(), http.getRequestURI());
            chain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("[{}] Completed {} {} in {}ms",
                    requestId, http.getMethod(), http.getRequestURI(), elapsed);

            MDC.remove(MDC_REQUEST_ID);
        }
    }
}
