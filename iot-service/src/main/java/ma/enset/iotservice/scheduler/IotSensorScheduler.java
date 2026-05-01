package ma.enset.iotservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.entities.SensorReading;
import ma.enset.iotservice.repository.IotSensorRepository;
import ma.enset.iotservice.repository.SensorReadingRepository;
import ma.enset.iotservice.service.IotSensorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
@RequiredArgsConstructor
public class IotSensorScheduler {
    private final IotSensorService iotSensorService;
    private final IotSensorRepository iotSensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final Random random=new Random();
    // toutes les 5 min
    @Scheduled(fixedRateString="${iot.scheduler.sensor.rate:6000}",initialDelayString = "${iot.scheduler.initial.delay:30000}")
    public void simulateSensorReading(){
        log.info("================== Scheduler capteur Iot demarre ===================");
        List<IotSensor> sensors=iotSensorService.findActiveEntities();
        if (sensors.isEmpty()){
            log.warn("Aucun capter actif trouve");
            return;
        }
        for (IotSensor sensor:sensors){
            double value=generateSimulateValue(sensor);
            String status =computeStatus(value,sensor);

            SensorReading reading=SensorReading.builder()
                    .valeur(value)
                    .unite(sensor.getUnite())
                    .readingDate(LocalDateTime.now())
                    .status(status)
                    .iotSensor(sensor)
                    .build();
            sensorReadingRepository.save(reading);
            sensor.setLastReadingDate(LocalDateTime.now());
            iotSensorRepository.save(sensor);
        }
        log.info("=== Scheduler capteurs IoT terminé ===");

    }
    // Génère une valeur simulée réaliste selon le type de capteur
    private double generateSimulateValue(IotSensor sensor){
        double value =switch (sensor.getSensorType()){
            case AIR_QUALITY -> 30+random.nextDouble()*120;
            case POUSSIERE -> 10+random.nextDouble()*80;
            case HUMIDITY_SOL ->  20+random.nextDouble()*60;
            case TEMPERATURE -> 18+random.nextDouble()*25;
            case CO2 ->  400+random.nextDouble()*1200;
        };
        return Math.round(value*10.0)/10.0;
    }

    private String computeStatus(double value,IotSensor sensor){
        return switch (sensor.getSensorType()){
            case AIR_QUALITY -> value<50 ?"NORMAL" : value<100 ?"WARNING" :"DANGER";
            case POUSSIERE -> value<35 ?"NORMAL": value<75 ?"WARNING" :"DANGER";
            case TEMPERATURE -> value<35 ?"NORMAL" : value<40 ?"WARNING" :"DANGER";
            case HUMIDITY_SOL -> value>35 ?"NORMAL":value>15 ?"WARNING":"DANGER";
            case CO2 -> value<800 ?"NORMAL":value<1500 ?"WARNING":"DANGER";
        };
    }
}
