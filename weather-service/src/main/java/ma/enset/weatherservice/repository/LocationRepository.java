package ma.enset.weatherservice.repository;

import ma.enset.weatherservice.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,Long> {
    Optional<Location> findByNameCity(String nameCity);
}
