package ma.enset.weatherservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.weatherservice.enums.ApiSource;

import java.time.LocalDateTime;

public class WeatherForecastDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherForecastRequest {

        @NotNull(message = "L'ID de la location est obligatoire")
        private Long locationId;

        @NotNull(message = "La date de prévision est obligatoire")
        private LocalDateTime date;

        @NotNull(message = "La température prévue est obligatoire")
        private Double predictedTemp;
        private Double predictedHumidity;
        private Double predictedWindSpeed;
        private Double precipitationProbability; // probabilité de pluie en %
        private String description;

        @NotNull(message = "La source API est obligatoire")
        private ApiSource sourceApi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherForecastResponse {
        private Long id;
        private LocalDateTime date;
        private Double predictedTemp;
        private Double predictedHumidity;
        private Double predictedWindSpeed;
        private Double precipitationProbability;
        private String description;
        private ApiSource sourceApi;
        private boolean expired;  // calculé : date < aujourd'hui
        private LocationDto.LocationSummary location;
    }
}