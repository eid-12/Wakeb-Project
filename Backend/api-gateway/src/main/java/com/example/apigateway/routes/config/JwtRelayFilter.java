package com.example.apigateway.routes.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component // Registers this filter as a Spring-managed component
@RequiredArgsConstructor // Injects final fields via constructor
public class JwtRelayFilter extends OncePerRequestFilter {

    private final com.example.apigateway.JwtService jwt; // Service to provide the JWT key

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Allow preflight OPTIONS requests to go through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        // Try to extract JWT token from cookies or Authorization header
        String token = extractToken(request);
        System.out.println("Extracted token: " + token);

        if (token != null) {
            try {
                // Parse and validate JWT token
                var claims = Jwts.parser()
                        .verifyWith(jwt.getKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject(); // Extract username from token
                Object userId = claims.get("userId");  // Extract user ID from token

                System.out.println("✅ Authenticated user: " + username);
                System.out.println("🆔 User ID from token: " + userId);

                // Set authentication context for downstream services
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Wrap the original request to add user ID as a header (X-User-Id)
                HttpServletRequest wrapped =
                        new HeaderMapRequestWrapper(request, "X-User-Id", userId.toString());

                // Continue the filter chain with the modified request
                filterChain.doFilter(wrapped, response);
                return;

            } catch (Exception e) {
                // If token is invalid, return 401 Unauthorized
                System.out.println("❌ Invalid token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // No token found → Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // Helper method to extract token from cookie or Authorization header
    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }

    // Custom request wrapper to add headers to the original request
    private static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> extraHeaders = new HashMap<>();

        HeaderMapRequestWrapper(HttpServletRequest request, String name, String value) {
            super(request);
            extraHeaders.put(name, value); // Add custom header
        }

        @Override
        public String getHeader(String name) {
            String header = extraHeaders.get(name);
            return (header != null) ? header : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(extraHeaders.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (extraHeaders.containsKey(name)) {
                return Collections.enumeration(List.of(extraHeaders.get(name)));
            }
            return super.getHeaders(name);
        }
    }

}
