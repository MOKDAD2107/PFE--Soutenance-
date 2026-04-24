package ma.enset.weatherservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Data
@Component
@ConfigurationProperties(prefix = "openweathermap.api")
public class OpenWeatherMapProperties {
    private String key;
    private String baseUrl;
    private String units;
    private List<String> cities =new ArrayList<>();
}
