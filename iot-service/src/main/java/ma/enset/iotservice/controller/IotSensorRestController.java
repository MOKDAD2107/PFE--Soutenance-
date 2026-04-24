package ma.enset.iotservice.controller;

import ma.enset.iotservice.dtos.IotSensorDto;
import ma.enset.iotservice.enums.SensorType;
import ma.enset.iotservice.model.Location;
import ma.enset.iotservice.service.IotSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class IotSensorRestController {
    @Autowired
    private IotSensorService  iotSensorService;
    @PostMapping("/save")
    public ResponseEntity<IotSensorDto.IotSensorResponse> save(@RequestBody IotSensorDto.IotSensorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iotSensorService.save(request));
    }
    @GetMapping("/sensor")
    public ResponseEntity<List<IotSensorDto.IotSensorResponse>> findByAll(){
        return ResponseEntity.ok().body(iotSensorService.findAll());
    }
    @GetMapping("/sensor/{id}")
    public ResponseEntity<IotSensorDto.IotSensorResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(iotSensorService.findById(id));
    }
    @GetMapping("/sensor/active")
    public ResponseEntity<List<IotSensorDto.IotSensorResponse>> findByActive(){
        return ResponseEntity.ok().body(iotSensorService.findByActive());
    }
    @GetMapping("/sensor/location/{locationId}")
    public ResponseEntity<List<IotSensorDto.IotSensorResponse>> findByLocationId(@PathVariable Long locationId){
        return ResponseEntity.ok().body(iotSensorService.findByLocationId(locationId));
    }
    @GetMapping("/sensor/types/{type}")
    public ResponseEntity<List<IotSensorDto.IotSensorResponse>> findBySensorType(@PathVariable SensorType type){
        return ResponseEntity.ok().body(iotSensorService.findBySensorType(type));
    }
}
