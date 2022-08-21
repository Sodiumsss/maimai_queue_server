package com.maimai.server.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig  {
    @Bean(name="serverEndpointExporter")
    public ServerEndpointExporter serverEndpointExporter()
    {
        return new ServerEndpointExporter();
    }
}
