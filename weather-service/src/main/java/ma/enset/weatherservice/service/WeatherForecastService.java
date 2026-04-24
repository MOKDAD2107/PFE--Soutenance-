package ma.enset.weatherservice.service;


import ma.enset.weatherservice.dtos.WeatherForecastDto;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherForecast;
import ma.enset.weatherservice.exceptions.RessourceNotFoundException;
import ma.enset.weatherservice.mappers.WeatherForecastMapper;
import ma.enset.weatherservice.repository.LocationRepository;
import ma.enset.weatherservice.repository.WeatherForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WeatherForecastService {
    @Autowired
    private WeatherForecastRepository weatherForecastRepository;
    @Autowired
    private WeatherForecastMapper weatherForecastMapper;
    @Autowired
    private LocationRepository locationRepository;

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
        List<WeatherForecast> weatherForecasts=weatherForecastRepository.findByLocationId(locationId);
        List<WeatherForecastDto.WeatherForecastResponse> responses=weatherForecasts.stream()
                .map(fc->weatherForecastMapper.fromWeatherForecasttoWeatherForecastResponse(fc))
                .toList();
        return responses;
    }
}
