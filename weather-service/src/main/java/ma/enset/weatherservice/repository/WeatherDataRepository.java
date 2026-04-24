package ma.enset.weatherservice.repository;

import ma.enset.weatherservice.entities.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData,Long> {
   List<WeatherData> findByLocationId(Long locationId);
}
