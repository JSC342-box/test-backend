package com.biketaxi.config;

import com.biketaxi.websocket.SocketIOAuthHandler;
import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Value("${socketio.host:localhost}")
    private String host;

    @Value("${socketio.port:9092}")
    private Integer port;

    @Autowired
    private SocketIOAuthHandler authHandler;

    @Bean
    public SocketIOServer socketIOServer() {
    	com.corundumstudio.socketio.Configuration config = 
                new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        
        // CORS configuration
        config.setOrigin("*");
        
        // Enable all transports
        config.setTransports(com.corundumstudio.socketio.Transport.POLLING, 
                            com.corundumstudio.socketio.Transport.WEBSOCKET);
        
        // Add authentication
        config.setAuthorizationListener(authHandler.createAuthListener());
        
        // Additional CORS headers
        config.setRandomSession(true);
        config.setPingTimeout(60000);
        config.setPingInterval(25000);
        
        return new SocketIOServer(config);
    }
}