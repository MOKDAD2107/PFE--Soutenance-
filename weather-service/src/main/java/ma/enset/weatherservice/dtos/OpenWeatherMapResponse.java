package ma.enset.weatherservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherMapResponse {
    private Long id;
    private String name;
    @JsonProperty("weather")
    private List<WeatherDescription> weather;
    @JsonProperty("main")
    private MainData mainData;
    @JsonProperty("wind")
    private WindData wind;
    @JsonProperty("coord")
    private CoordData coord;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData{
        private double temp;
        private double humidity;
        private double pressure;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindData{
        private double speed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherDescription{
        private String description;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoordData{
        private double lon;
        private double lat;
    }

    // retourner la description du 1er element
    public String getMainDescription(){
        if(weather!=null&&!weather.isEmpty()){
            return weather.get(0).getDescription();
        }
        return null;
    }
}
