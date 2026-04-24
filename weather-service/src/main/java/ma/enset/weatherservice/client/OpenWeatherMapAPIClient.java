package ma.enset.weatherservice.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.weatherservice.config.OpenWeatherMapProperties;
import ma.enset.weatherservice.dtos.OpenWeatherForecastResponse;
import ma.enset.weatherservice.dtos.OpenWeatherMapResponse;
import ma.enset.weatherservice.exceptions.ExternalApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Service
@RequiredArgsConstructor
@Slf4j
public class OpenWeatherMapAPIClient {

    private final OpenWeatherMapProperties properties;

    private final WebClient openWeatherMapClient;

    public OpenWeatherMapResponse getResponse(String cityname) {

        log.info("Appel OpenWeatherMap -> ville: {}", cityname);
        try {
            return openWeatherMapClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("q", cityname + ",MA")
                            .queryParam("appid", properties.getKey())
                            .queryParam("units", properties.getUnits())
                            .queryParam("lang", "fr")
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(OpenWeatherMapResponse.class)
                    .block();
        }catch (WebClientResponseException e) {
            log.error("Erreur API pour: {}:{}",cityname,e.getMessage());
            throw new ExternalApiException("Erreur Open Weather Map "+cityname,e);
        }
    }
    public OpenWeatherForecastResponse getForecastResponse(String cityname){
        log.info("Appel prevision OpenWeatherMap -> ville: {}", cityname);
        try {
            return openWeatherMapClient.get().uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("q", cityname + ",MA")
                            .queryParam("appid", properties.getKey())
                            .queryParam("units", properties.getUnits())
                            .queryParam("lang", "fr")
                            .queryParam("cnt", 40)// prevision de 5 jours : 5*8 =40
                            .build())
                    .retrieve()
                    .bodyToMono(OpenWeatherForecastResponse.class)
                    .block();
        }catch (WebClientResponseException e) {
            log.error("Erreur prevision pour: {}:{}",cityname,e.getMessage());
            throw new ExternalApiException("Erreur prevision "+cityname,e);
        }

    }

}
