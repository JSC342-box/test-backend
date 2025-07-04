package com.biketaxi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClerkConfig {
    
    @Value("${clerk.publishable-key}")
    private String publishableKey;
    
    @Value("${clerk.secret-key}")
    private String secretKey;
    
    @Value("${clerk.jwt-issuer}")
    private String jwtIssuer;
    
    @Value("${clerk.webhook-secret:}")
    private String webhookSecret;

    public String getPublishableKey() {
        return publishableKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getJwtIssuer() {
        return jwtIssuer;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
} 