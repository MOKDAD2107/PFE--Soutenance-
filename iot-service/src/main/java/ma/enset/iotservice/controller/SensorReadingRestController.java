package ma.enset.iotservice.controller;


import ma.enset.iotservice.dtos.SensorReadingDto;
import ma.enset.iotservice.service.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading")
public class SensorReadingRestController {
    @Autowired
    private SensorReadingService  sensorReadingService;
    @PostMapping("/save")
    public ResponseEntity<SensorReadingDto.SensorReadingResponse> save(@RequestBody SensorReadingDto.SensorReadingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorReadingService.save(request));
    }
    @GetMapping("/sensor")
    public ResponseEntity<List<SensorReadingDto.SensorReadingResponse>> findByAll(){
        return ResponseEntity.ok().body(sensorReadingService.findAll());
    }
    @GetMapping("/sensor/{id}")
    public ResponseEntity<SensorReadingDto.SensorReadingResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(sensorReadingService.findById(id));
    }

    @GetMapping("/sensor/senorid/{sensorId}")
    public ResponseEntity<List<SensorReadingDto.SensorReadingResponse>> findBySensorId(@PathVariable Long sensorId){
        return ResponseEntity.ok().body(sensorReadingService.findBySensorId(sensorId));
    }
    @GetMapping("/sensor/order/{sensorId}")
    public ResponseEntity<List<SensorReadingDto.SensorReadingResponse>> findBySensor(@PathVariable Long sensorId){
        return ResponseEntity.ok().body(sensorReadingService.findBySensor(sensorId));
    }

}
