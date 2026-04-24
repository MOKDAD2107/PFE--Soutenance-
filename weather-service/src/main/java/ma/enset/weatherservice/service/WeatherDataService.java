package ma.enset.weatherservice.service;

import ma.enset.weatherservice.dtos.WeatherDataDto;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherData;
import ma.enset.weatherservice.exceptions.RessourceNotFoundException;
import ma.enset.weatherservice.mappers.WeatherDataMapper;
import ma.enset.weatherservice.repository.LocationRepository;
import ma.enset.weatherservice.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WeatherDataService {
    @Autowired
    private WeatherDataRepository weatherDataRepository;
    @Autowired
    private WeatherDataMapper weatherDataMapper;
    @Autowired
    private LocationRepository locationRepository;

    public WeatherDataDto.WeatherDataResponse save (WeatherDataDto.WeatherDataRequest request){
        Location location=locationRepository.findById(request.getLocationId()).orElseThrow(()->new RessourceNotFoundException("Location not Found"+request.getLocationId()));
       WeatherData weatherData=weatherDataMapper.fromWeatherDatatRequesttoWeatherData(request,location);
       WeatherData wethearsave= weatherDataRepository.save(weatherData);
       return weatherDataMapper.fromWeatherDatatoWeatherDataResponse(wethearsave);
    }

    public List<WeatherDataDto.WeatherDataResponse> findAll(){
        List<WeatherData> data=weatherDataRepository.findAll();
        List<WeatherDataDto.WeatherDataResponse> response=data.stream()
                .map(we->weatherDataMapper.fromWeatherDatatoWeatherDataResponse(we))
                .toList();
        return response;
    }
    public WeatherDataDto.WeatherDataResponse findById (Long id){
       WeatherData weatherData=weatherDataRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Weather invalide"+id));
       return weatherDataMapper.fromWeatherDatatoWeatherDataResponse(weatherData);
    }

    public List<WeatherDataDto.WeatherDataResponse> findByLocationId(Long locationId){
        List<WeatherData> weatherData= weatherDataRepository.findByLocationId(locationId);
        List<WeatherDataDto.WeatherDataResponse> responses=weatherData.stream()
                .map(wr->weatherDataMapper.fromWeatherDatatoWeatherDataResponse(wr))
                .toList();
        return responses;
    }
}
