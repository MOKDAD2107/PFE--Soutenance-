package ma.enset.weatherservice.mappers;

import ma.enset.weatherservice.dtos.WeatherDataDto;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherDataMapper {
    @Autowired
    private LocationMapper locationMapper;
    // WeatherDataRequest to WeatherData
    public WeatherData fromWeatherDatatRequesttoWeatherData(WeatherDataDto.WeatherDataRequest weatherDataRequest, Location location){
        return WeatherData.builder()
                .dateTime(weatherDataRequest.getDateTime())
                .temperature(weatherDataRequest.getTemperature())
                .humidity(weatherDataRequest.getHumidity())
                .pressure(weatherDataRequest.getPressure())
                .uvIndex(weatherDataRequest.getUvIndex())
                .windSpeed(weatherDataRequest.getWindSpeed())
                .description(weatherDataRequest.getDescription())
                .apiSource(weatherDataRequest.getSourceApi())
                .location(location)
                .build();
    }
    //WeatherData to WeatherDataResponse
    public WeatherDataDto.WeatherDataResponse fromWeatherDatatoWeatherDataResponse(WeatherData weatherData){
        return WeatherDataDto.WeatherDataResponse.builder()
                .id(weatherData.getId())
                .dateTime(weatherData.getDateTime())
                .temperature(weatherData.getTemperature())
                .humidity(weatherData.getHumidity())
                .pressure(weatherData.getPressure())
                .uvIndex(weatherData.getUvIndex())
                .windSpeed(weatherData.getWindSpeed())
                .description(weatherData.getDescription())
                .sourceApi(weatherData.getApiSource())
                .location(locationMapper.fromLocationtoLocationSummary(weatherData.getLocation()))
                .build();
    }

}
