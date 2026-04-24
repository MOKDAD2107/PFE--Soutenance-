package ma.enset.weatherservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.weatherservice.enums.ApiSource;

import java.time.LocalDateTime;

public class WeatherDataDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherDataRequest {
        @NotNull(message = "L'ID de la location est obligatoire")
        private Long locationId;
        @NotNull(message = "La date est obligatoire")
        private LocalDateTime dateTime;
        @NotNull(message = "La température est obligatoire")
        private Double temperature;
        private Double humidity;
        private Double windSpeed;
        private Double pressure;
        private Double uvIndex;
        private String description;
        @NotNull(message = "La source API est obligatoire")
        private ApiSource sourceApi;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WeatherDataResponse {
        private Long id;
        private LocalDateTime dateTime;
        private Double temperature;
        private Double humidity;
        private Double windSpeed;
        private Double pressure;
        private Double uvIndex;
        private String description;
        private ApiSource sourceApi;
        private LocationDto.LocationSummary location;
    }

}
