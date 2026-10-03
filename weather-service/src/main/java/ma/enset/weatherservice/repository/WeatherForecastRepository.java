package ma.enset.weatherservice.repository;

import jakarta.transaction.Transactional;
import ma.enset.weatherservice.entities.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast,Long> {
    List<WeatherForecast> findByLocationIdOrderByDateAsc(Long locationId);
    @Transactional
    void deleteByLocationId(Long locationId);
}
