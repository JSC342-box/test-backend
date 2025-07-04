package com.biketaxi.config;

import com.biketaxi.security.ClerkJwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	private final ClerkJwtAuthenticationFilter clerkJwtAuthenticationFilter;

    public SecurityConfig(ClerkJwtAuthenticationFilter clerkJwtAuthenticationFilter) {
        this.clerkJwtAuthenticationFilter = clerkJwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Production security configuration with JWT authentication
        http
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public endpoints (no authentication required)
                .requestMatchers("/api/auth/config").permitAll()
                .requestMatchers("/api/registration/**").permitAll()
                .requestMatchers("/api/payments/**").permitAll()
                .requestMatchers("/api/images/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Protected endpoints (authentication required)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/drivers/**").hasAnyRole("DRIVER", "ADMIN")
                .requestMatchers("/api/users/**").hasAnyRole("RIDER", "DRIVER", "ADMIN")
                .requestMatchers("/api/rides/**").hasAnyRole("RIDER", "DRIVER", "ADMIN")
                .requestMatchers("/api/fares/**").hasAnyRole("RIDER", "DRIVER", "ADMIN")
                .requestMatchers("/api/ratings/**").hasAnyRole("RIDER", "DRIVER", "ADMIN")
                .requestMatchers("/api/invoices/**").hasAnyRole("RIDER", "DRIVER", "ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(clerkJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Production CORS - restrict to specific domains
        config.setAllowedOriginPatterns(Arrays.asList(
            "https://*.yourdomain.com",
            "https://yourdomain.com",
            "http://localhost:8081",
            "http://localhost:19002"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
} 