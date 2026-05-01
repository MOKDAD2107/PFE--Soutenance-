package ma.enset.iotservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.iotservice.entities.EnvironmentalAlert;
import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.entities.SensorReading;
import ma.enset.iotservice.entities.WaterRessource;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import ma.enset.iotservice.enums.SensorType;
import ma.enset.iotservice.model.Location;
import ma.enset.iotservice.repository.SensorReadingRepository;
import ma.enset.iotservice.service.EnvironmentalService;
import ma.enset.iotservice.service.WaterRessourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlertScheduler {
    private final EnvironmentalService environmentalService;
    private final SensorReadingRepository sensorReadingRepository;
    private final WaterRessourceService  waterRessourceService;

    @Value("${iot.alert.air.quality.max:150}")
    private double airQualityMax;
    @Value("${iot.alert.water.level.crtitique:20}")
    private double waterLevelCritical;
    @Value("${iot.alert.water.level.low:40}")
    private double waterLevelLow;
    @Value("${iot.alert.poussiere.max:75}")
    private double poussiereMax;
    @Value("${iot.alert.temperature.max:40}")
    private double temperatureMax;
    @Value("${iot.alert.temperature.min:0}")
    private double temperatureMin;
    @Value("${iot.alert.co2.max:1000}")
    private double co2Max;
    @Value("${iot.alert.humiditySol.max:30}")
    private double humiditySolMax;

    // toutes les 10 minutes
    @Scheduled(fixedRateString = "${iot.scheduler.alert.rate:6000}", initialDelay = 0)
    public void checkAlert(){
        log.info("========== Scheduler vérification alertes demarre ============");
        checkSensorAlert();
        checkWaterRessourceAlert();
        log.info("========== Scheduler vérification alertes termine ============");
    }

    private void checkSensorAlert() {

        // recuperer les 100 derniers lectures de danger
        List<SensorReading> dangerReading = sensorReadingRepository.findAll().stream()
                .filter(r -> "DANGER".equals(r.getStatus()))
                .limit(100)
                .toList();

        for (SensorReading reading : dangerReading) {
            IotSensor sensor = reading.getIotSensor();

            if (sensor == null) {
                log.warn("Lecture sans capteur associé, ignorée");
                continue;
            }

            if (sensor.getLocationId() == null) {
                log.warn("Capteur {} sans locationId, ignoré", sensor.getId());
                continue;
            }
            String alertType = sensor.getSensorType().name() + "_HIGH";
            double seuil = getSeuilBySensorType(sensor.getSensorType());
            boolean alertDejaExists=environmentalService.existActiveAlertForSensor(sensor.getLocationId());

            if (!alertDejaExists) {
                AlertSeverity severity = getSeverityBySensorType(sensor.getSensorType());
                EnvironmentalAlert alert = EnvironmentalAlert.builder()
                        .alertType(alertType)
                        .message("Seuil critique depasse : " + reading.getIotSensor().getName() + ":" + reading.getValeur() + reading.getUnite())
                        .alertSeverity(severity)
                        .locationId(sensor.getLocationId())
                        .location(Location.builder()
                                .id(sensor.getLocationId())
                                .build())
                        .sensorId(reading.getIotSensor() != null ? reading.getIotSensor().getId() : null)
                        .triggerValue(reading.getValeur())
                        .seuilDepasse(seuil)
                        .build();
                environmentalService.createAlert(alert);
                log.warn("Alerte cree : {}", alert.getMessage());

            }else {
                log.info("Alerte déjà active pour capteur {} ({}), ignorée", sensor.getName(), alertType);
            }
        }
    }
    private void checkWaterRessourceAlert() {

        List<WaterRessource> ressources=waterRessourceService.findAllEntities();
        for (WaterRessource ressource : ressources){
            if (ressource.getLocationId() == null) {
                log.warn("Ressource eau {} sans locationId, ignorée", ressource.getId());
                continue;
            }

            if (ressource.getFillPercentage()==0)continue;
            double pct =ressource.getFillPercentage();
            AlertSeverity severity=null;
            String message =null ;

            if (pct<=waterLevelCritical){
                severity=AlertSeverity.CRITICAL;
                message="Niveau critique:"+ressource.getName()+":"+pct+"%"+("seuil:"+waterLevelCritical+"%");
            } else if (pct<=waterLevelLow) {
                severity=AlertSeverity.MEDIUM;
                message="Niveau bas:"+ressource.getName()+":"+pct+"%"+("seuil:"+waterLevelLow+"%");

            }
            String type="WATER_LEVEL "+ressource.getFillStatus();
            boolean alertType=environmentalService.existsLocationIdAlertTyeStatus(ressource.getLocationId(),type);
            if (severity!=null&&!alertType){
                EnvironmentalAlert alerts= EnvironmentalAlert.builder()
                        .alertType(type)
                        .message(message)
                        .alertSeverity(severity)
                        .locationId(ressource.getLocationId())
                        .sensorId(ressource.getId() != null ? ressource.getId() : null)
                        .location(Location.builder()
                                .id(ressource.getLocationId()).build())
                        .triggerValue(pct)
                        .seuilDepasse(pct<=waterLevelCritical ?waterLevelCritical:waterLevelLow)
                        .build();
                environmentalService.createAlert(alerts);
                log.warn("Alerte eau créée : {}", message);
            }else {
                log.info("Alerte déjà active pour water {} ({}), ignorée", ressource.getName(), alertType);
            }
        }

    }
    private double getSeuilBySensorType(SensorType sensorType) {
        return switch (sensorType){
            case AIR_QUALITY -> airQualityMax;
            case POUSSIERE -> poussiereMax;
            case TEMPERATURE -> temperatureMax;
            case CO2 -> co2Max;
            case HUMIDITY_SOL ->humiditySolMax;
            default -> 0;
        };
    }

    private AlertSeverity getSeverityBySensorType(SensorType sensorType) {
        return switch (sensorType){
            case AIR_QUALITY -> AlertSeverity.HIGH;
            case POUSSIERE -> AlertSeverity.MEDIUM;
            case CO2 -> AlertSeverity.HIGH;
            case TEMPERATURE -> AlertSeverity.MEDIUM;
            case HUMIDITY_SOL -> AlertSeverity.LOW;

        };

    }
}
