package at.fhtw.tourplannerserver.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ors")
public class OpenRouteConfig {
    private static final Logger log = LogManager.getLogger(OpenRouteConfig.class);

    /*Persönlicher API-Key von openrouteservice.org
     wird über application.properties gesetzt
     */
    private String apiKey;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        log.info("Open route initialized with api key-length: {}",
                apiKey == null ? 0 : apiKey.length());
    }
}
