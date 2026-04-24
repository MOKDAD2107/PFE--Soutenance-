package ma.enset.iotservice.mappers;

import ma.enset.iotservice.dtos.SensorReadingDto;
import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.entities.SensorReading;
import org.springframework.stereotype.Component;

@Component
public class SensorReadingMapper {
    // SensorRequest to Sensor
    public SensorReading fromSensorRequestToSensorReading(SensorReadingDto.SensorReadingRequest request, IotSensor sensor){
        return SensorReading.builder()
                .valeur(request.getValeur())
                .unite(request.getUnite() !=null ? request.getUnite() : sensor.getUnite())
                .iotSensor(sensor)
                .build();
    }

    // Sensor to SensorResponse
    public SensorReadingDto.SensorReadingResponse fromSensorToSensorReadingResponse(SensorReading sensorReading){
        return SensorReadingDto.SensorReadingResponse.builder()
                .id(sensorReading.getId())
                .valeur(sensorReading.getValeur())
                .unite(sensorReading.getUnite())
                .readingDate(sensorReading.getReadingDate())
                .status(sensorReading.getStatus())
                .sensorId(sensorReading.getIotSensor().getId())
                .build();
    }
}
