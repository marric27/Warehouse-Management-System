package com.relatech.warehouse_management_system.common.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class LogMdcFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        HttpServletRequest req = (HttpServletRequest) request;

        // Popola il path
        MDC.put("RequestPath", req.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            // Calcola la durata e popola Elapsed
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("Elapsed", String.valueOf(duration));
            MDC.clear(); // Importante pulire per evitare leak su altri thread
        }
    }
}