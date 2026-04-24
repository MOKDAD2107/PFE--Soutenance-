package ma.enset.weatherservice.controller;

import ma.enset.weatherservice.dtos.LocationDto;
import ma.enset.weatherservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/locations")
public class LocationRestController {
    @Autowired
    private LocationService locationService;
    // Controller -> Service->Mapper->Repository

    @PostMapping()
    public ResponseEntity<LocationDto.LocationResponse> save(@RequestBody LocationDto.LocationRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(locationService.save(request));
    }
    @GetMapping("/location")
    public ResponseEntity<List<LocationDto.LocationResponse>> findAll(){
        return ResponseEntity.ok(locationService.findAll());

    }
    @GetMapping("/location/{id}")
    public ResponseEntity<LocationDto.LocationResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(locationService.findById(id));
    }
}
