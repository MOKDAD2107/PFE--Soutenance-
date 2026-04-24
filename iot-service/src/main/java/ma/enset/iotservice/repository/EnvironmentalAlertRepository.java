package ma.enset.iotservice.repository;


import ma.enset.iotservice.entities.EnvironmentalAlert;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvironmentalAlertRepository extends JpaRepository<EnvironmentalAlert,Long> {
    List<EnvironmentalAlert> findByLocationId(Long locationId);
    List<EnvironmentalAlert> findByAlertSeverity(AlertSeverity alertSeverity);
    List<EnvironmentalAlert> findByAlertStatus(AlertStatus alertStatus);

    boolean existsBySensorIdAndAlertStatus(Long sensorId, AlertStatus alertStatus);
    boolean existsByLocationIdAndAlertTypeAndAlertStatus(Long locationId,String alertType,AlertStatus alertStatus);
}
