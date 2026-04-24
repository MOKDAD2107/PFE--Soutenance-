package ma.enset.weatherservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherForecastResponse {
    @JsonProperty("list")
    private List<ForecastItem> list ;
    @JsonProperty("city")
    private CityData city;
    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CityData{
        private String name;
        private OpenWeatherMapResponse.CoordData coord;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem{
        @JsonProperty("dt_txt")
        private String dtTxt;
        @JsonProperty("main")
        private OpenWeatherMapResponse.MainData main;
        @JsonProperty("wind")
        private OpenWeatherMapResponse.WindData wind;
        @JsonProperty("weather")
        private List<OpenWeatherMapResponse.WeatherDescription> weather;
        //probabilite de pluie : 0.0 -> 1.0
        @JsonProperty("pop")
        private double pop;

        public String getMainDescription(){
            if (weather!=null&&!weather.isEmpty()){
                return weather.get(0).getDescription();
            }
            return null;
        }
    }
}
