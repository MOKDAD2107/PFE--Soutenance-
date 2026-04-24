package ma.enset.weatherservice.repository;

import ma.enset.weatherservice.entities.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast,Long> {
    List<WeatherForecast> findByLocationId(Long locationId);
}
