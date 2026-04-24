package ma.enset.iotservice.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.enset.iotservice.enums.SensorType;
import ma.enset.iotservice.model.Location;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class IotSensor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String name ;
    private SensorType sensorType;
    private String unite;
    private boolean active;
    private Long locationId;
    private LocalDateTime lastReadingDate;
    private String description;
    @OneToMany(mappedBy = "iotSensor")
    private List<SensorReading> sensorReadings;
    @Transient
    private Location location;

    @PrePersist
    protected void onCreate() {
        active=true;
    }


}
