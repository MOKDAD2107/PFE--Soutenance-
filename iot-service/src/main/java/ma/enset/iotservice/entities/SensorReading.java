package ma.enset.iotservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class SensorReading {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private double valeur;
    private String unite;
    private String status; // Normal , WARNING, DANGER
    private LocalDateTime readingDate;
    @Column(name = "iot_sensor_id", insertable = false, updatable = false)
    private Long sensorId;
    @ManyToOne
    @JoinColumn(name = "iot_sensor_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private IotSensor iotSensor;
}
