package com.payment.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        
        // Skip JWT check for auth endpoints and public paths
        if (isPublicEndpoint(requestURI)) {
            log.debug("Skipping JWT check for public endpoint: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Processing JWT authentication for: {}", requestURI);

        try {
            final String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("No JWT token found in Authorization header");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "No JWT token found");
                return;
            }

            final String jwt = authHeader.substring(7);
            
            try {
                final String username = jwtService.extractUsername(jwt);
                
                if (username == null) {
                    log.warn("Invalid JWT token - could not extract username");
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
                
                log.debug("Loading user details for username: {}", username);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.debug("JWT token is valid for user: {}", username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("Invalid or expired JWT token for user: {}", username);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                log.error("Error processing JWT token: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Authentication error for request {}: {}", requestURI, e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An error occurred during authentication: " + e.getMessage());
        }
    }
    
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/v1/auth/") || 
               requestURI.startsWith("/v3/api-docs") ||
               requestURI.startsWith("/swagger-ui") ||
               requestURI.startsWith("/h2-console") ||
               requestURI.equals("/error") ||
               requestURI.equals("/actuator/health") ||
               requestURI.equals("/api/v1/auth/refresh-token") ||
               requestURI.equals("/api/v1/auth/register") ||
               requestURI.equals("/api/v1/auth/login");
    }
    
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        try {
            response.setStatus(status);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String error = status == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : 
                          status == HttpServletResponse.SC_FORBIDDEN ? "Forbidden" :
                          status == HttpServletResponse.SC_NOT_FOUND ? "Not Found" :
                          status == HttpServletResponse.SC_BAD_REQUEST ? "Bad Request" : "Error";
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", status);
            errorResponse.put("error", error);
            errorResponse.put("message", message);
            
            objectMapper.writeValue(response.getWriter(), errorResponse);
        } catch (Exception e) {
            log.error("Error sending error response: {}", e.getMessage(), e);
            throw e;
        }
    }
}
