package ma.enset.weatherservice.scheduler;


import lombok.extern.slf4j.Slf4j;
import ma.enset.weatherservice.client.OpenWeatherMapAPIClient;
import ma.enset.weatherservice.dtos.OpenWeatherMapResponse;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherData;
import ma.enset.weatherservice.enums.ApiSource;
import ma.enset.weatherservice.exceptions.ExternalApiException;
import ma.enset.weatherservice.repository.LocationRepository;
import ma.enset.weatherservice.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j

public class WeatherScheduler {
    @Autowired
    private OpenWeatherMapAPIClient client; // appeler l'API meteo
    @Autowired
    private LocationRepository  locationRepository; //sauvegarder les villes
    @Autowired
    private WeatherDataRepository  weatherDataRepository; //sauvegarder la meteo actuelle

    // tous les 10 minutes
    @Scheduled(fixedRateString = "${openweathermap.scheduler.rate:600000}")
    public void fetchCurrentWeather(){
    log.info("============ Scheduler meteo demmare ============");
        List<Location> locations=locationRepository.findAll();
    for (Location location:locations){
     try {
         saveWeatherForCity(location);
     }catch (ExternalApiException e){
         log.error("Erreur pour {} : {}", location.getNameCity(), e.getMessage());
     }
    }
        log.info("=== Scheduler météo terminé ===");
    }
    private void saveWeatherForCity(Location location){
        OpenWeatherMapResponse response =client.getResponse(location.getNameCity());
        if (response==null)return ;

        WeatherData data = WeatherData.builder()
                .dateTime(LocalDateTime.now())
                .temperature(response.getMainData().getTemp())
                .humidity(response.getMainData().getHumidity())
                .pressure(response.getMainData().getPressure())
                .windSpeed(response.getWind()!=null ?response.getWind().getSpeed():null)
                .description(response.getMainDescription())
                .weatherIcon(response.getMainIcon())
                .apiSource(ApiSource.OPEN_WEATHER_MAP)
                .location(location)
                .build();
        weatherDataRepository.save(data);
    }

}
