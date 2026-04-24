package ma.enset.iotservice.service;

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
    public List<EnvironmentalAlertDto> findByLocationId(Long locationId){
        List<EnvironmentalAlert> sensors=environmentalAlertRepository.findByLocationId(locationId);
        List<EnvironmentalAlertDto> responses=sensors.stream()
                .map(en->environmentalAlertMapper.fromAlertToAlertResponse(en))
                .toList();
        return responses;
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
    public boolean existsLocationIdAlertTyeStatus(Long locationId,String alertType){
        return environmentalAlertRepository.existsByLocationIdAndAlertTypeAndAlertStatus(locationId,alertType,AlertStatus.ACTIVE);
    }

    public EnvironmentalAlertDto resolve(Long id){
        EnvironmentalAlert alert=environmentalAlertRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Alerte non trouve"+id));
        alert.setAlertStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        return environmentalAlertMapper.fromAlertToAlertResponse(alert);
    }

    //utilise par scheduler pour creer des alertes
    public void createAlert(EnvironmentalAlert alert){
        environmentalAlertRepository.save(alert);
    }
}
