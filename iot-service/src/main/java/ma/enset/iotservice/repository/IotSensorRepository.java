package ma.enset.iotservice.repository;

import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.enums.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IotSensorRepository extends JpaRepository<IotSensor,Long> {
    List<IotSensor> findByLocationId(Long locationId);
    List<IotSensor> findBySensorType(SensorType sensorType);
    List<IotSensor> findByActive(Boolean active);
}
