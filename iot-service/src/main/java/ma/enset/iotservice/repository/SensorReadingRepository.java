package ma.enset.iotservice.repository;

import ma.enset.iotservice.entities.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading,Long> {
    List<SensorReading> findByIotSensorId(Long sensorId);
    List<SensorReading> findByStatusAndReadingDateAfter(String status, LocalDateTime after);

}
