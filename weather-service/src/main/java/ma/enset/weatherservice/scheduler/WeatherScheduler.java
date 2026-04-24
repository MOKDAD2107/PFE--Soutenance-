package ma.enset.weatherservice.scheduler;


import lombok.extern.slf4j.Slf4j;
import ma.enset.weatherservice.client.OpenWeatherMapAPIClient;
import ma.enset.weatherservice.config.OpenWeatherMapProperties;
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
import java.time.format.DateTimeFormatter;

@Component
@Slf4j

public class WeatherScheduler {
    @Autowired
    private OpenWeatherMapAPIClient client; // appeler l'API meteo
    @Autowired
    private OpenWeatherMapProperties properties; //acceder a la config pour les listes des villes
    @Autowired
    private LocationRepository  locationRepository; //sauvegarder les villes
    @Autowired
    private WeatherDataRepository  weatherDataRepository; //sauvegarder la meteo actuelle

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //convertir une chaine de date en localDateTime

    // tous les 10 minutes
    @Scheduled(fixedRateString = "${openweathermap.scheduler.rate:600000}")
    public void fetchCurrentWeather(){
    log.info("============ Scheduler meteo demmare ============");
    for (String city : properties.getCities()){
     try {
         saveweatherForcity(city);
     }catch (ExternalApiException e){
         log.error("Erreur pour {} : {}", city, e.getMessage());
     }
    }
        log.info("=== Scheduler météo terminé ===");
    }
    private void saveweatherForcity(String cityname){
        OpenWeatherMapResponse response =client.getResponse(cityname);
        if (response==null)return ;
        Location location = findOrCreateLocation(cityname,response);
        WeatherData data = WeatherData.builder()
                .dateTime(LocalDateTime.now())
                .temperature(response.getMainData().getTemp())
                .humidity(response.getMainData().getHumidity())
                .pressure(response.getMainData().getPressure())
                .windSpeed(response.getWind()!=null ?response.getWind().getSpeed():null)
                .description(response.getMainDescription())
                .apiSource(ApiSource.OPEN_WEATHER_MAP)
                .location(location)
                .build();
        weatherDataRepository.save(data);
    }
    private Location findOrCreateLocation(String cityname ,OpenWeatherMapResponse response){
        return locationRepository.findByNameCity(cityname).orElseGet(()->locationRepository.save(
                Location.builder()
                        .nameCity(cityname)
                        .country("Maroc")

                        .latitude(response.getCoord().getLat())
                        .longitude(response.getCoord().getLon())
                        .build()
        ));
    }
}
