package com.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        
        // Skip JWT check for auth endpoints and public paths
        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "No JWT token found");
                return;
            }

            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            
            if (username == null) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
            
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during authentication");
        }
    }
    
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/v1/auth/") || 
               requestURI.startsWith("/v3/api-docs") ||
               requestURI.startsWith("/swagger-ui") ||
               requestURI.startsWith("/h2-console") ||
               requestURI.equals("/error");
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
            
            String jsonResponse = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status,
                error,
                message.replace("\"", "\\\"") // Escape double quotes in message
            );
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            log.error("Failed to send error response: {}", e.getMessage(), e);
            throw e;
        }
    }
}
