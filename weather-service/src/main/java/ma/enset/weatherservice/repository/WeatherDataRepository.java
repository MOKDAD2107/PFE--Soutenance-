package ma.enset.weatherservice.repository;

import ma.enset.weatherservice.entities.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData,Long> {
   List<WeatherData> findByLocationIdOrderByDateTimeDesc(Long locationId);
   Optional<WeatherData> findTopByLocationIdOrderByDateTimeDesc(Long locationId);
}
