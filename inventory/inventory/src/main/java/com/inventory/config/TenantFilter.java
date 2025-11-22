package com.inventory.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class TenantFilter implements Filter {

    private static final List<String> ALLOWED_PATHS = List.of(
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/api/auth"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Skip tenant validation for public endpoints
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String tenantId = httpRequest.getHeader("X-Tenant-Id");
        
        if (tenantId == null || tenantId.trim().isEmpty()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write("X-Tenant-Id header is required");
            return;
        }

        // Set tenant in a thread-local variable for the current request
        TenantContext.setCurrentTenant(tenantId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            // Clear the tenant context after the request is processed
            TenantContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        return ALLOWED_PATHS.stream().anyMatch(path::startsWith);
    }
}
