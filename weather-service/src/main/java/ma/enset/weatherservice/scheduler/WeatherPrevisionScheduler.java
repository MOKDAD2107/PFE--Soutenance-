package ma.enset.weatherservice.scheduler;


import lombok.extern.slf4j.Slf4j;
import ma.enset.weatherservice.client.OpenWeatherMapAPIClient;
import ma.enset.weatherservice.dtos.OpenWeatherForecastResponse;
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
import java.util.List;

@Component
@Slf4j
public class WeatherPrevisionScheduler {
    @Autowired
    private OpenWeatherMapAPIClient client; // appeler l'API meteo
    @Autowired
    private LocationRepository locationRepository; //sauvegarder les villes
    @Autowired
    private WeatherForecastRepository weatherForecastRepository; // sauvegarder les previsions
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //convertir une chaine de date en localDateTime

    // toutes les heures
    @Scheduled(fixedRateString = "${openweathermap.scheduler.forecast-rate:3600000}")
    public void fetchForecast(){
        log.info("============= Scheduler Prevision demarre ================");
        List<Location> locations = locationRepository.findAll();
        log.info("Nombre de villes à traiter : {}", locations.size());

        for (Location location : locations){
        try{
            saveForecastForCity(location);
        }catch (ExternalApiException e){
            log.error("Erreur pour {} : {}",location.getNameCity(),e.getMessage());
        }
        }
        log.info("================= Scheduler prevision terminer ===============");
    }
    private void saveForecastForCity(Location location){
        OpenWeatherForecastResponse response =client.getForecastResponse(location.getNameCity());
        if (response == null) {
            log.error("OpenWeather n'a retourné aucune réponse pour {}", location.getNameCity());
            return;
        }

        if (response.getList() == null) {
            log.error("Liste des prévisions vide pour {}", location.getNameCity());
            return;
        }

        //  Supprimer les anciennes prévisions avant de réinsérer
        weatherForecastRepository.deleteByLocationId(location.getId());
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
        log.info("Prévisions sauvegardées pour {}", location.getNameCity());
    }

}
