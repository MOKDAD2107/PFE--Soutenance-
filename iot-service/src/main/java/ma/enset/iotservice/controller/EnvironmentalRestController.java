package ma.enset.iotservice.controller;

import ma.enset.iotservice.dtos.AlertActionRequest;
import ma.enset.iotservice.dtos.EnvironmentalAlertDto;
import ma.enset.iotservice.enums.AlertSeverity;
import ma.enset.iotservice.enums.AlertStatus;
import ma.enset.iotservice.service.EnvironmentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/environnement")
public class EnvironmentalRestController {
    @Autowired
    private EnvironmentalService environmentalService;

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EnvironmentalAlertDto> resolve(@PathVariable Long id,
                                                         @RequestBody (required = false) AlertActionRequest body){
        String note = body != null ? body.getNote() : null;
        return ResponseEntity.ok().body(environmentalService.resolve(id, note));
    }
    @PatchMapping("/{id}/ignore")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EnvironmentalAlertDto> ignore(@PathVariable Long id,
                                                        @RequestBody(required = false) AlertActionRequest body){
        String note = body != null ? body.getNote() : null;
        return ResponseEntity.ok().body(environmentalService.ignore(id, note));
    }
    @GetMapping("/alerts")
    public ResponseEntity<List<EnvironmentalAlertDto>> findByAll(){
        return ResponseEntity.ok().body(environmentalService.findAll());
    }

    @GetMapping("/alerts/location/{locationId}")
    public ResponseEntity<List<EnvironmentalAlertDto>> findByLocationId(@PathVariable Long locationId, @RequestParam(defaultValue = "5") int limit){
        return ResponseEntity.ok().body(environmentalService.findByLocationId(locationId,limit));
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
