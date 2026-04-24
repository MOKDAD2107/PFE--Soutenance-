package ma.enset.weatherservice.mappers;

import ma.enset.weatherservice.dtos.WeatherForecastDto;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WeatherForecastMapper {
    @Autowired
    private LocationMapper locationMapper;
    // WeatherForecastRequest to WeatherForecast
    public WeatherForecast fromWeatherForecastRequesttoWeatherForecast(WeatherForecastDto.WeatherForecastRequest weatherForecastRequest, Location location){
        return WeatherForecast.builder()
                .date(weatherForecastRequest.getDate())
                .predictedTemp(weatherForecastRequest.getPredictedTemp())
                .predictedHumidity(weatherForecastRequest.getPredictedHumidity())
                .predictedWindSpeed(weatherForecastRequest.getPredictedWindSpeed())
                .precipitationProbability(weatherForecastRequest.getPrecipitationProbability())
                .description(weatherForecastRequest.getDescription())
                .apiSource(weatherForecastRequest.getSourceApi())
                .location(location)
                .build();
    }

    // WeatherForecast to WeatherForecastResponse
    public WeatherForecastDto.WeatherForecastResponse fromWeatherForecasttoWeatherForecastResponse(WeatherForecast weatherForecast){

        return WeatherForecastDto.WeatherForecastResponse.builder()
                .id(weatherForecast.getId())
                .date(weatherForecast.getDate())
                .predictedTemp(weatherForecast.getPredictedTemp())
                .predictedHumidity(weatherForecast.getPredictedHumidity())
                .predictedWindSpeed(weatherForecast.getPredictedWindSpeed())
                .precipitationProbability(weatherForecast.getPrecipitationProbability())
                .description(weatherForecast.getDescription())
                .sourceApi(weatherForecast.getApiSource())
                .expired(weatherForecast.getDate().isBefore(LocalDateTime.now()))
                .location(locationMapper.fromLocationtoLocationSummary(weatherForecast.getLocation()))
                .build();
    }

}
