package ma.enset.iotservice.service;

import ma.enset.iotservice.dtos.IotSensorDto;
import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.enums.SensorType;
import ma.enset.iotservice.exceptions.RessourceNotFoundException;
import ma.enset.iotservice.mappers.IotSensorMapper;
import ma.enset.iotservice.model.Location;
import ma.enset.iotservice.repository.IotSensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IotSensorService {
    @Autowired
    private IotSensorRepository iotSensorRepository;
    @Autowired
    private IotSensorMapper iotSensorMapper;
    public IotSensorDto.IotSensorResponse save(IotSensorDto.IotSensorRequest request ){
        IotSensor sensor=iotSensorMapper.fromIotRequestToIotSensor(request,request.getLocation());
        return iotSensorMapper.fromIotSensorToIotResponse(iotSensorRepository.save(sensor));
    }

    public List<IotSensorDto.IotSensorResponse> findAll(){
        List<IotSensor> sensors=iotSensorRepository.findAll();
        List<IotSensorDto.IotSensorResponse> responses=sensors.stream()
                .map(se->iotSensorMapper.fromIotSensorToIotResponse(se))
                .toList();
        return responses;
    }

    public IotSensorDto.IotSensorResponse findById(Long id){
        IotSensor sensor=iotSensorRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Capteur non trouve"+id));
        return iotSensorMapper.fromIotSensorToIotResponse(sensor);
    }

    public List<IotSensorDto.IotSensorResponse> findBySensorType(SensorType type){
        List<IotSensor> sensors =iotSensorRepository.findBySensorType(type);
        List<IotSensorDto.IotSensorResponse>responses=sensors.stream()
                .map(se->iotSensorMapper.fromIotSensorToIotResponse(se))
                .toList();
        return responses;
    }
    public List<IotSensorDto.IotSensorResponse> findByActive(){
        List<IotSensor> sensors =iotSensorRepository.findByActive(true);
        List<IotSensorDto.IotSensorResponse>responses=sensors.stream()
                .map(se->iotSensorMapper.fromIotSensorToIotResponse(se))
                .toList();
        return responses;
    }
    public List<IotSensorDto.IotSensorResponse> findByLocationId(Long locationId){
        List<IotSensor> sensors =iotSensorRepository.findByLocationId(locationId);
        List<IotSensorDto.IotSensorResponse>responses=sensors.stream()
                .map(se->iotSensorMapper.fromIotSensorToIotResponse(se))
                .toList();
        return responses;
    }
    public List<IotSensor> findActiveEntities(){
        return iotSensorRepository.findByActive(true);
    }

}
