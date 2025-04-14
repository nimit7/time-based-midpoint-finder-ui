package com.nimz.midpointfinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${openrouteservice.api.key}")
    private String orsApiKey;

    public String getOrsApiKey() {
        return orsApiKey;
    }
}
