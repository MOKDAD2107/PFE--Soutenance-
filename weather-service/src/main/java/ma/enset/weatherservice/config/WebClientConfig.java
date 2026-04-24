package ma.enset.weatherservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientConfig {
    @Autowired
    private OpenWeatherMapProperties properties;
    @Bean
    public WebClient openWeatherMapClient() {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
