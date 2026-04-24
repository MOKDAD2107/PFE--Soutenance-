package ma.enset.iotservice.controller;

import ma.enset.iotservice.dtos.EnvironmentalAlertDto;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import ma.enset.iotservice.service.EnvironmentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/environement")
public class EnvironmentalRestController {
    @Autowired
    private EnvironmentalService environmentalService;

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<EnvironmentalAlertDto> resolve(@PathVariable Long id){
        return ResponseEntity.ok().body(environmentalService.resolve(id));
    }
    @GetMapping("/alerts")
    public ResponseEntity<List<EnvironmentalAlertDto>> findByAll(){
        return ResponseEntity.ok().body(environmentalService.findAll());
    }

    @GetMapping("/alerts/location/{locationId}")
    public ResponseEntity<List<EnvironmentalAlertDto>> findByLocationId(@PathVariable Long locationId){
        return ResponseEntity.ok().body(environmentalService.findByLocationId(locationId));
    }
    @GetMapping("/alerts/severity/{alertSeverity}")
    public ResponseEntity<List<EnvironmentalAlertDto>> findBySeverity(@PathVariable AlertSeverity alertSeverity){
        return ResponseEntity.ok().body(environmentalService.findBySeverity(alertSeverity));
    }
    @GetMapping("/alerts/status/{status}")
    public ResponseEntity<List<EnvironmentalAlertDto>> findByStatus(@PathVariable AlertStatus status){
        return ResponseEntity.ok().body(environmentalService.findByAlertStatus(status));
    }


}
