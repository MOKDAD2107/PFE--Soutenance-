package ma.enset.weatherservice.scheduler;


import lombok.extern.slf4j.Slf4j;
import ma.enset.weatherservice.client.OpenWeatherMapAPIClient;
import ma.enset.weatherservice.config.OpenWeatherMapProperties;
import ma.enset.weatherservice.dtos.OpenWeatherForecastResponse;
import ma.enset.weatherservice.dtos.OpenWeatherMapResponse;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherForecast;
import ma.enset.weatherservice.enums.ApiSource;
import ma.enset.weatherservice.exceptions.ExternalApiException;
import ma.enset.weatherservice.repository.LocationRepository;
import ma.enset.weatherservice.repository.WeatherForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class WeatherPrevisionScheduler {
    @Autowired
    private OpenWeatherMapAPIClient client; // appeler l'API meteo
    @Autowired
    private OpenWeatherMapProperties properties; //acceder a la config pour les listes des villes
    @Autowired
    private LocationRepository locationRepository; //sauvegarder les villes
    @Autowired
    private WeatherForecastRepository weatherForecastRepository; // sauvegarder les previsions
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //convertir une chaine de date en localDateTime

    // toutes les heures
    @Scheduled(fixedRateString = "${openweathermap.scheduler.forecast-rate:3600000}")
    public void fetchForecast(){
        log.info("============= Scheduler Prevision demarre ================");
        for (String city : properties.getCities()){
        try{
            saveForecastForCity(city);
        }catch (ExternalApiException e){
            log.error("Erreur pour {} : {}",city,e.getMessage());
        }
        }
        log.info("================= Scheduler prevision terminer ===============");
    }
    private void saveForecastForCity(String city){
        OpenWeatherForecastResponse response =client.getForecastResponse(city);
        if(response==null ||response.getList()==null)return;

        Location location =findOrCreateLocations(city,response);
        if(location==null){
            log.warn("Location'{}' non trouvee - Lance d'abord fetchCurrentWeather", city);
            return;
        };
    response.getList().stream()
            .filter(item->item.getDtTxt().contains("12:00:00"))
            .forEach(item->{
                WeatherForecast forecast = WeatherForecast.builder()
                        .date(LocalDateTime.parse(item.getDtTxt(), FORMATTER))
                        .predictedTemp(item.getMain().getTemp())
                        .predictedHumidity(item.getMain().getHumidity())
                        .predictedWindSpeed(item.getWind()!=null ?item.getWind().getSpeed():null)
                        .precipitationProbability(item.getPop()*100)
                        .description(item.getMainDescription())
                        .apiSource(ApiSource.OPEN_WEATHER_MAP)
                        .location(location)
                        .build();
                weatherForecastRepository.save(forecast);
            });
        log.info("Prévisions sauvegardées pour {}", city);
    }
    private Location findOrCreateLocations(String cityname , OpenWeatherForecastResponse response){
        return locationRepository.findByNameCity(cityname).orElseGet(()->locationRepository.save(
                Location.builder()
                        .nameCity(cityname)
                        .country("Maroc")
                        .latitude(response.getCity().getCoord().getLat())
                        .longitude(response.getCity().getCoord().getLon())
                        .build()
        ));
    }
}
