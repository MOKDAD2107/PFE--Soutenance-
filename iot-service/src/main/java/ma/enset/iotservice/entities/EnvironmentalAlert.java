package ma.enset.iotservice.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import ma.enset.iotservice.model.Location;

import java.time.LocalDateTime;
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class EnvironmentalAlert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String alertType;
    private String message;
    @Enumerated(EnumType.STRING)
    private AlertStatus alertStatus;
    @Enumerated(EnumType.STRING)
    private AlertSeverity alertSeverity;
    private Long sensorId; // capteur qui a declenche l'alerte
    private double triggerValue; //valeur qui a declenche l'alerte
    private double seuilDepasse; //seuil depasse
    private LocalDateTime triggerAt;
    private LocalDateTime resolvedAt;
    private Long locationId;
    @Transient
    private Location location;
    @PrePersist
    protected void onCreate(){
        triggerAt=LocalDateTime.now();
        alertStatus=AlertStatus.ACTIVE;
    }
}
