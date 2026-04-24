package ma.enset.iotservice.mappers;

import ma.enset.iotservice.dtos.IotSensorDto;
import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.model.Location;
import org.springframework.stereotype.Component;

@Component
public class IotSensorMapper {
    // IotRequest to IotSensor
    public IotSensor fromIotRequestToIotSensor(IotSensorDto.IotSensorRequest iotSensorRequest, Location location){
        return IotSensor.builder()
                .name(iotSensorRequest.getName())
                .sensorType(iotSensorRequest.getSensorType())
                .unite(iotSensorRequest.getUnite())
                .description(iotSensorRequest.getDescription())
                .location(location)
                .build();
    }

    // IotSensor to IotResponse
    public IotSensorDto.IotSensorResponse fromIotSensorToIotResponse(IotSensor iotSensor){
        return IotSensorDto.IotSensorResponse.builder()
                .id(iotSensor.getId())
                .name(iotSensor.getName())
                .sensorType(iotSensor.getSensorType())
                .unite(iotSensor.getUnite())
                .description(iotSensor.getDescription())
                .location(iotSensor.getLocation())
                .active(iotSensor.isActive())
                .lastReadingDate(iotSensor.getLastReadingDate())
                .build();
    }
}
