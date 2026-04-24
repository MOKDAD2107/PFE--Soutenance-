package ma.enset.weatherservice;

import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherData;
import ma.enset.weatherservice.entities.WeatherForecast;
import ma.enset.weatherservice.enums.ApiSource;
import ma.enset.weatherservice.repository.LocationRepository;

import ma.enset.weatherservice.repository.WeatherDataRepository;
import ma.enset.weatherservice.repository.WeatherForecastRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class WeatherServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherServiceApplication.class, args);
    }
  /*@Bean
  CommandLineRunner cm(WeatherDataRepository weatherDataRepository, LocationRepository locationRepository,
                       WaterRessourceRepository waterRessourceRepository, WeatherForecastRepository  weatherForecastRepository) {
        return args -> {

            locationRepository.save(Location.builder()
                            .country("Maroc").region("Casablanca-Settat")
                    .nameCity("Casablanca").latitude(33.57).longitude(-7.58)
                    .build());
            locationRepository.save(Location.builder()
                            .country("Maroc").region("Rabat-Sale-Kenitra")
                    .nameCity("Rabat").latitude(34.02).longitude(-6.83)
                    .build());

            locationRepository.save(Location.builder()
                    .country("Maroc").region("Fes-Meknes")
                    .nameCity("Fes").latitude(31.63).longitude(-7.99)
                    .build());
            locationRepository.findAll().forEach(location ->
            {
                for (int i=0; i<5; i++) {
                    double rawTemp = 20 + Math.random() * 10;
                    double roundedTemp = Math.round(rawTemp * 100.0) / 100.0;

                    double rawHum = 40 + Math.random() * 30;
                    double roundedHum = Math.round(rawHum * 100.0) / 100.0;
                    WeatherData weatherData =WeatherData.builder()
                            .dateTime(LocalDateTime.now())
                            .temperature(roundedTemp)
                            .humidity(roundedHum)
                            .apiSource(ApiSource.OPEN_WEATHER_MAP)
                            .location(location)
                            .build();
                    weatherDataRepository.save(weatherData);
                }
                double capMax = 500 + Math.random() * 1000;
                WaterRessource waterRessource =WaterRessource.builder()
                        .name("Nom Barrage").ressourceType(RessourceType.BARRAGE).fillStatus(FillStatus.NORMAL)
                                .capacitemax(Math.round(capMax * 100.0) / 100.0)
                                .currentlevel(Math.round((capMax * 0.3) * 100.0) / 100.0)
                        .fillPercentage((Math.round((capMax * 0.3) * 100.0) / 100.0)/(Math.round(capMax * 100.0) / 100.0)*100)
                                .location(location)
                                .build();
                        waterRessourceRepository.save(waterRessource);
                       for (int i=0; i<5; i++) {
                           WeatherForecast weatherForecast =WeatherForecast.builder()
                                   .apiSource(ApiSource.NASA)
                                   .date(LocalDateTime.now())
                                   .predictedTemp(Math.round((20 + Math.random() * 8) * 100.0) / 100.0)
                                   .location(location)
                                   .build();
                           weatherForecastRepository.save(weatherForecast);
                       }
                    });
  };
  }*/
}
