package ma.enset.iotservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import ma.enset.iotservice.model.Location;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentalAlertDto {

        private Long id;
        private String alertType;
        private String message;
        private AlertSeverity severity;
        private AlertStatus status;
        private Location location;
        private Long locationId;
        private Long sensorId;
        private Double triggerValue;
        private Double seuilDepasse;
        private String resolutionNote;
        private LocalDateTime triggeredAt;
        private LocalDateTime resolvedAt;
    }


