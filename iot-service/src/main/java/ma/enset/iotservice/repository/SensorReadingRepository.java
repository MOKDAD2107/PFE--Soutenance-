package ma.enset.iotservice.repository;

import ma.enset.iotservice.entities.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading,Long> {
    List<SensorReading> findBySensorId(Long sensorId);
    //List<SensorReading> findBySensorIdOrderByReadingDateDesc(Long sensorId);
}
