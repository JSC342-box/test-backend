package com.biketaxi.security;

import com.biketaxi.entity.User;
import com.biketaxi.service.ClerkService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class ClerkJwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(ClerkJwtAuthenticationFilter.class);
    
    private final ClerkService clerkService;

    @Autowired
    public ClerkJwtAuthenticationFilter(ClerkService clerkService) {
        this.clerkService = clerkService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token)) {
                logger.debug("Processing JWT token for request: {}", request.getRequestURI());
                
                Optional<User> userOpt = clerkService.verifyTokenAndGetUser(token);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    String role = user.getRole() != null ? user.getRole().name() : "RIDER";
                    
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    
                    logger.debug("Authentication successful for user: {} with role: {}", 
                                user.getEmail(), role);
                } else {
                    logger.warn("Invalid JWT token provided for request: {}", request.getRequestURI());
                    // Return 401 for invalid tokens in production
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                    return;
                }
            } else {
                logger.debug("No JWT token found in request: {}", request.getRequestURI());
            }
            
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage(), e);
            // Return 401 for token processing errors in production
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token processing error\"}");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        // Check Authorization header first
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        
        // Check for token in query parameter (for WebSocket connections)
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        
        // Check for token in custom header
        String customHeader = request.getHeader("X-Auth-Token");
        if (StringUtils.hasText(customHeader)) {
            return customHeader;
        }
        
        return null;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip filtering for public endpoints
        return path.startsWith("/api/registration/") ||
               path.startsWith("/api/payments/") ||
               path.startsWith("/api/images/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/info") ||
               path.equals("/api/auth/config");
    }
} 