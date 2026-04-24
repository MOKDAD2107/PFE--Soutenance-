package ma.enset.weatherservice.entities;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Location {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String region;
    private String nameCity;
    private double  latitude;
    private double longitude;
    @OneToMany(mappedBy = "location")
    private List<WeatherData> weatherdata;
    @OneToMany(mappedBy = "location")
    private List<WeatherForecast> weatherForecasts;
}
