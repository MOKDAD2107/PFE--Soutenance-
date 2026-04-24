package ma.enset.iotservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SensorReadingDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SensorReadingRequest{
        @NotNull(message = "L'Id du capteur est obligatoire")
        private Long sensorId;
        @NotNull(message = "La valeur est obligatoire")
        private double valeur;
        private String unite;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SensorReadingResponse{
        private Long id;
        private Double valeur;
        private String unite;
        private Long sensorId;
        private String status;
        private LocalDateTime readingDate;
    }
}
