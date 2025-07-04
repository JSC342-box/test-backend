package com.biketaxi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseHealthCheck implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("🔍 Checking database connection...");
        
        try (Connection connection = dataSource.getConnection()) {
            logger.info("✅ Database connection successful!");
            logger.info("📊 Database: {}", connection.getMetaData().getDatabaseProductName());
            logger.info("🔢 Version: {}", connection.getMetaData().getDatabaseProductVersion());
            logger.info("👤 User: {}", connection.getMetaData().getUserName());
            logger.info("🔗 URL: {}", connection.getMetaData().getURL());
        } catch (SQLException e) {
            logger.error("❌ Database connection failed!", e);
            logger.error("💡 Please ensure PostgreSQL is running on localhost:5432");
            logger.error("💡 Database 'biketaxi' exists and is accessible");
            logger.error("💡 User 'postgres' has proper permissions");
        }
    }
} 