package ma.enset.iotservice.mappers;

import ma.enset.iotservice.dtos.EnvironmentalAlertDto;
import ma.enset.iotservice.entities.EnvironmentalAlert;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentalAlertMapper {
    // Alert to AlertResponse
    public EnvironmentalAlertDto fromAlertToAlertResponse(EnvironmentalAlert alert){
        return EnvironmentalAlertDto.builder()
                .id(alert.getId())
                .alertType(alert.getAlertType())
                .message(alert.getMessage())
                .alertType(alert.getAlertType())
                .severity(alert.getAlertSeverity())
                .status(alert.getAlertStatus())
                .location(alert.getLocation())
                .locationId(alert.getLocationId())
                .sensorId(alert.getSensorId())
                .triggerValue(alert.getTriggerValue())
                .seuilDepasse(alert.getSeuilDepasse())
                .resolutionNote(alert.getResolutionNote())
                .triggeredAt(alert.getTriggerAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }
}
