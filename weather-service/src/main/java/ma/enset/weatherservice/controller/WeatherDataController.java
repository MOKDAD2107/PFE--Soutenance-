package ma.enset.weatherservice.controller;

import ma.enset.weatherservice.dtos.WeatherDataDto;
import ma.enset.weatherservice.entities.WeatherData;
import ma.enset.weatherservice.service.WeatherDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weathers")
public class WeatherDataController {
    @Autowired
    private WeatherDataService weatherDataService;
    @PostMapping()
    public ResponseEntity<WeatherDataDto.WeatherDataResponse> save (@RequestBody WeatherDataDto.WeatherDataRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(weatherDataService.save(request));
    }
    @GetMapping("/weather")
    public ResponseEntity<List<WeatherDataDto.WeatherDataResponse>> findAll(){
        return ResponseEntity.ok(weatherDataService.findAll());
    }
    @GetMapping("/weather/{id}")
    public ResponseEntity<WeatherDataDto.WeatherDataResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(weatherDataService.findById(id));
    }
    @GetMapping("/weather/location/{locationId}")
    public ResponseEntity<List<WeatherDataDto.WeatherDataResponse>> findByLocationId(@PathVariable Long locationId){
        return ResponseEntity.ok(weatherDataService.findByLocationId(locationId));
    }
}
