package ma.enset.weatherservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestWeatherDto {
    private Long locationId;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String weatherDescription;
    private String weatherIcon;
    }

