package ma.enset.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Configuration
public class CorsConfig {

    // ① Filtre CORS principal
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    // ② Filtre global qui supprime les headers CORS dupliqués
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter dedupeCorsFilter() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            // Garde uniquement la première valeur de Access-Control-Allow-Origin
            String origin = null;
            for (String value : headers.getOrEmpty(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)) {
                if (origin == null) origin = value;
            }
            if (origin != null) {
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            }
            // Même chose pour Allow-Credentials
            String cred = null;
            for (String value : headers.getOrEmpty(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)) {
                if (cred == null) cred = value;
            }
            if (cred != null) {
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, cred);
            }
        }));
    }
}