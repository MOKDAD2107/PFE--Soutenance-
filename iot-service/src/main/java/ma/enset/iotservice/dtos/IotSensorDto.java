package ma.enset.iotservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.iotservice.enums.SensorType;
import ma.enset.iotservice.model.Location;

import java.time.LocalDateTime;

public class IotSensorDto {
    @Data @NoArgsConstructor
    @AllArgsConstructor
    @Builder
   public static class IotSensorRequest{
        @NotBlank(message = "Le nom est obligatoire")
       private String name;
        @NotNull(message = "Le type de capteur est obligatoire")
       private SensorType sensorType;
       private String unite;
       private String description;
       @NotNull(message = "L'Id de la location est obligatoire")
       private Location location;
   }
   @Data @NoArgsConstructor @AllArgsConstructor @Builder
   public static class IotSensorResponse{
        private Long id;
        private String name;
        private SensorType sensorType;
        private Location location;
        private String description;
        private String unite;
        private boolean active;
        private LocalDateTime lastReadingDate;
   }
}
