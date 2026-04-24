package ma.enset.weatherservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ma.enset.weatherservice.enums.ApiSource;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Builder
public class WeatherData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateTime;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Double pressure;
    private Double uvIndex;
    private String description;
    @Enumerated(EnumType.STRING)
    private ApiSource apiSource;
    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Location location;
}
