package ma.enset.weatherservice.service;


import lombok.extern.slf4j.Slf4j;
import ma.enset.weatherservice.client.OpenWeatherMapAPIClient;
import ma.enset.weatherservice.dtos.OpenWeatherForecastResponse;
import ma.enset.weatherservice.dtos.WeatherForecastDto;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherForecast;
import ma.enset.weatherservice.enums.ApiSource;
import ma.enset.weatherservice.exceptions.RessourceNotFoundException;
import ma.enset.weatherservice.mappers.WeatherForecastMapper;
import ma.enset.weatherservice.repository.LocationRepository;
import ma.enset.weatherservice.repository.WeatherForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@Slf4j
public class WeatherForecastService {
    @Autowired
    private WeatherForecastRepository weatherForecastRepository;
    @Autowired
    private WeatherForecastMapper weatherForecastMapper;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private OpenWeatherMapAPIClient openWeatherMapAPIClient;

    public WeatherForecastDto.WeatherForecastResponse save (WeatherForecastDto.WeatherForecastRequest request){
        Location location=locationRepository.findById(request.getLocationId()).orElseThrow(()->new RessourceNotFoundException("Location not Found:"+request.getLocationId()));
        WeatherForecast weatherForecast=weatherForecastMapper.fromWeatherForecastRequesttoWeatherForecast(request,location);
        WeatherForecast savedWeatherForecast=weatherForecastRepository.save(weatherForecast);
        return weatherForecastMapper.fromWeatherForecasttoWeatherForecastResponse(savedWeatherForecast);
    }

    public List<WeatherForecastDto.WeatherForecastResponse> findAll(){
        List<WeatherForecast> weatherForecasts=weatherForecastRepository.findAll();
        List<WeatherForecastDto.WeatherForecastResponse> responses=weatherForecasts.stream()
                .map(fc->weatherForecastMapper.fromWeatherForecasttoWeatherForecastResponse(fc))
                .toList();
        return responses;
    }
    public WeatherForecastDto.WeatherForecastResponse findById (Long id) {
       WeatherForecast weatherForecast=weatherForecastRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Weather invalid"+id));
       return weatherForecastMapper.fromWeatherForecasttoWeatherForecastResponse(weatherForecast);
    }

    public List<WeatherForecastDto.WeatherForecastResponse> findByLocationId(Long locationId){
        List<WeatherForecast> weatherForecasts=weatherForecastRepository.findByLocationIdOrderByDateAsc(locationId);
        List<WeatherForecastDto.WeatherForecastResponse> responses=weatherForecasts.stream()
                .map(fc->weatherForecastMapper.fromWeatherForecasttoWeatherForecastResponse(fc))
                .toList();
        return responses;
    }

    public void SaveForecastForLocation(Location location) {
        try {
            OpenWeatherForecastResponse forecastResponse =
                    openWeatherMapAPIClient.getForecastResponse(location.getNameCity());
            log.info("Ville = {}", location.getNameCity());

            if (forecastResponse == null) {
                log.error("forecastResponse == null");
                return;
            }

            log.info("Nombre de prévisions = {}", forecastResponse.getList().size());

            if (forecastResponse == null || forecastResponse.getList() == null) return;

            forecastResponse.getList().forEach(item -> {
                WeatherForecast forecast = WeatherForecast.builder()
                        .date(LocalDateTime.parse(item.getDtTxt(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .predictedTemp(item.getMain() != null ? item.getMain().getTemp() : null)
                        .predictedHumidity(item.getMain() != null ? item.getMain().getHumidity() : null)
                        .predictedWindSpeed(item.getWind() != null ? item.getWind().getSpeed() : null)
                        .precipitationProbability(item.getPop() * 100)
                        .description(item.getMainDescription())
                        .apiSource(ApiSource.OPEN_WEATHER_MAP)
                        .location(location)
                        .build();
                weatherForecastRepository.save(forecast);
            });

            log.info("Prévisions sauvegardées pour: {}", location.getNameCity());

        } catch (Exception e) {
            log.error("Erreur complète pour {}", location.getNameCity(), e);
        }
    }
}
