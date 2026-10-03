package ma.enset.iotservice.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ma.enset.iotservice.dtos.EnvironmentalAlertDto;

import ma.enset.iotservice.entities.EnvironmentalAlert;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import ma.enset.iotservice.exceptions.RessourceNotFoundException;
import ma.enset.iotservice.mappers.EnvironmentalAlertMapper;
import ma.enset.iotservice.repository.EnvironmentalAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Slf4j
public class EnvironmentalService {
    @Autowired
    private EnvironmentalAlertRepository environmentalAlertRepository;
    @Autowired
    private EnvironmentalAlertMapper environmentalAlertMapper ;


    public List<EnvironmentalAlertDto> findAll(){
        List<EnvironmentalAlert> sensors=environmentalAlertRepository.findAll();
        List<EnvironmentalAlertDto> responses=sensors.stream()
                .map(en->environmentalAlertMapper.fromAlertToAlertResponse(en))
                .toList();
        return responses;
    }
    public List<EnvironmentalAlertDto> findByLocationId(Long locationId, int limit){
        return environmentalAlertRepository
                .findByLocationIdAndAlertStatusOrderByTriggerAtDesc(locationId, AlertStatus.ACTIVE)
                .stream()
                .limit(limit)
                .map(en -> environmentalAlertMapper.fromAlertToAlertResponse(en))
                .toList();
    }
    public List<EnvironmentalAlertDto> findByAlertStatus(AlertStatus alertStatus){
        List<EnvironmentalAlert> sensors=environmentalAlertRepository.findByAlertStatus(alertStatus);
        List<EnvironmentalAlertDto> responses=sensors.stream()
                .map(en->environmentalAlertMapper.fromAlertToAlertResponse(en))
                .toList();
        return responses;
    }
    public List<EnvironmentalAlertDto> findBySeverity(AlertSeverity alertSeverity){
        List<EnvironmentalAlert> sensors=environmentalAlertRepository.findByAlertSeverity(alertSeverity);
        List<EnvironmentalAlertDto> responses=sensors.stream()
                .map(en->environmentalAlertMapper.fromAlertToAlertResponse(en))
                .toList();
        return responses;
    }


    public boolean existActiveAlertForSensor(Long sensorId){
        return environmentalAlertRepository.existsBySensorIdAndAlertStatus(sensorId, AlertStatus.ACTIVE);
    }

    public void resolveObsoleteAlertsForSensor(Long sensorId, String currentAlertType) {
        List<EnvironmentalAlert> actives = environmentalAlertRepository
                .findBySensorIdAndAlertStatus(sensorId, AlertStatus.ACTIVE);
        actives.stream()
                .filter(a -> !a.getAlertType().equals(currentAlertType))
                .forEach(a -> {
                    a.setAlertStatus(AlertStatus.RESOLVED);
                    a.setResolvedAt(LocalDateTime.now());
                    environmentalAlertRepository.save(a);
                    log.info("Alerte résolue automatiquement : {}", a.getMessage());
                });
    }
    public boolean existsBySensorIdAndAlertTypeAndStatus(Long sensorId, String alertType) {
        return environmentalAlertRepository
                .existsBySensorIdAndAlertTypeAndAlertStatus(sensorId, alertType, AlertStatus.ACTIVE);
    }

    @Transactional
    public EnvironmentalAlertDto resolve(Long id,String note){
        EnvironmentalAlert alert=environmentalAlertRepository.findById(id).
                orElseThrow(()->new RessourceNotFoundException("Alerte non trouve"+id));
        alert.setAlertStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolutionNote(note);
        environmentalAlertRepository.save(alert);
        return environmentalAlertMapper.fromAlertToAlertResponse(alert);
    }
    @Transactional
    public EnvironmentalAlertDto ignore(Long id, String note){
        EnvironmentalAlert alert = environmentalAlertRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Alerte non trouvee " + id));
        alert.setAlertStatus(AlertStatus.IGNORED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolutionNote(note);
        environmentalAlertRepository.save(alert);
        return environmentalAlertMapper.fromAlertToAlertResponse(alert);
    }

    //utilise par scheduler pour creer des alertes
    public void createAlert(EnvironmentalAlert alert){
        environmentalAlertRepository.save(alert);
    }
}
