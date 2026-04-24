package ma.enset.iotservice.service;

import ma.enset.iotservice.dtos.SensorReadingDto;
import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.entities.SensorReading;
import ma.enset.iotservice.exceptions.RessourceNotFoundException;
import ma.enset.iotservice.mappers.SensorReadingMapper;
import ma.enset.iotservice.repository.IotSensorRepository;
import ma.enset.iotservice.repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorReadingService {
    @Autowired
    private SensorReadingRepository sensorReadingRepository;
    @Autowired
    private IotSensorRepository iotSensorRepository;
    @Autowired
    private SensorReadingMapper sensorReadingMapper;

    public SensorReadingDto.SensorReadingResponse save(SensorReadingDto.SensorReadingRequest request){
        IotSensor iotSensor=iotSensorRepository.findById(request.getSensorId()).orElseThrow(()->new RessourceNotFoundException("Capteur non trouve"+request.getSensorId()));
        SensorReading sensorReading=sensorReadingMapper.fromSensorRequestToSensorReading(request,iotSensor);
        sensorReading.setReadingDate(LocalDateTime.now());
        sensorReading.setStatus(computeStatus(request.getValeur(), iotSensor));
        return sensorReadingMapper.fromSensorToSensorReadingResponse(sensorReadingRepository.save(sensorReading));
    }
    public List<SensorReadingDto.SensorReadingResponse> findAll(){
        List<SensorReading> sensors=sensorReadingRepository.findAll();
        List<SensorReadingDto.SensorReadingResponse> responses=sensors.stream()
                .map(se->sensorReadingMapper.fromSensorToSensorReadingResponse(se))
                .toList();
        return responses;
    }

    public SensorReadingDto.SensorReadingResponse findById(Long id){
        SensorReading sensorReading=sensorReadingRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Lecteur non trouve"+id));
        return sensorReadingMapper.fromSensorToSensorReadingResponse(sensorReading);
    }

    public List<SensorReadingDto.SensorReadingResponse> findBySensorId(Long sensorId){
        List<SensorReading> sensors=sensorReadingRepository.findBySensorId(sensorId);
        List<SensorReadingDto.SensorReadingResponse> responses=sensors.stream()
                .map(se->sensorReadingMapper.fromSensorToSensorReadingResponse(se))
                .toList();
        return responses;
    }
    public List<SensorReadingDto.SensorReadingResponse> findBySensor(Long sensorId){
        List<SensorReading> sensors=sensorReadingRepository.findBySensorIdOrderByReadingDateDesc(sensorId);
        List<SensorReadingDto.SensorReadingResponse> responses=sensors.stream()
                .map(se->sensorReadingMapper.fromSensorToSensorReadingResponse(se))
                .toList();
        return responses;
    }

    private String computeStatus(double value,IotSensor iotSensor){
        return switch (iotSensor.getSensorType()){
            case AIR_QUALITY -> value<50 ?"NORMAL" : value<100 ?"WARNING" :"DANGER";
            case POUSSIERE -> value<35 ?"NORMAL": value<75 ?"WARNING" :"DANGER";
            case TEMPERATURE -> value<35 ?"NORMAL" : value<40 ?"WARNING" :"DANGER";
            case HUMIDITY_SOL -> value>35 ?"NORMAL":value>15 ?"WARNING":"DANGER";
            case CO2 -> value<800 ?"NORMAL":value<1500 ?"WARNING":"DANGER";
        };
    }

}
