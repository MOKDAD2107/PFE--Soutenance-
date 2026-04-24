package ma.enset.weatherservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ma.enset.weatherservice.enums.ApiSource;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString @Builder
public class WeatherForecast {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private double predictedTemp;
    private Double predictedHumidity;
    private Double predictedWindSpeed;
    private Double precipitationProbability; // probabilité de pluie en %
    private String description;
    @Enumerated(EnumType.STRING)
    private ApiSource apiSource;
    @ManyToOne()
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Location location;
}
