package ma.enset.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    // routage static
    /*@Bean
   RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec
                        .path("/api/locations/**,/api/weathers/**,/api/forecast/**,/api/test/**")
                        .uri("http://localhost:8082"))
                .route(predicateSpec -> predicateSpec
                        .path("/api/waters/**,/api/sensors/**,/api/reading/**,/api/environment/**")
                        .uri("http://localhost:8083"))
                .build();
   }*/
    // routage dynamic
    //http://localhost:8888/WEATHER-SERVICE/api/locations/**
    @Bean
    DiscoveryClientRouteDefinitionLocator dynamicRouting(ReactiveDiscoveryClient reactive, DiscoveryLocatorProperties properties) {
        return new DiscoveryClientRouteDefinitionLocator(reactive, properties);
    }
}
