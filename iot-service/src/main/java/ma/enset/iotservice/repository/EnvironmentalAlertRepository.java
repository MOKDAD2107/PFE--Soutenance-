package ma.enset.iotservice.repository;


import ma.enset.iotservice.entities.EnvironmentalAlert;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvironmentalAlertRepository extends JpaRepository<EnvironmentalAlert,Long> {
    List<EnvironmentalAlert> findByAlertSeverity(AlertSeverity alertSeverity);
    List<EnvironmentalAlert> findByAlertStatus(AlertStatus alertStatus);
    List<EnvironmentalAlert> findBySensorIdAndAlertStatus(Long sensorId, AlertStatus alertStatus);

    boolean existsBySensorIdAndAlertStatus(Long sensorId, AlertStatus alertStatus);
    boolean existsByLocationIdAndAlertTypeAndAlertStatus(Long locationId,String alertType,AlertStatus alertStatus);
    boolean existsBySensorIdAndAlertTypeAndAlertStatus(Long sensorId,String alertType,AlertStatus alertStatus);
    List<EnvironmentalAlert> findByLocationIdAndAlertStatusOrderByTriggerAtDesc(Long locationId, AlertStatus alertStatus);
}
