package ma.enset.weatherservice.controller;

import ma.enset.weatherservice.dtos.WeatherForecastDto;
import ma.enset.weatherservice.entities.WeatherForecast;
import ma.enset.weatherservice.service.WeatherForecastService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
public class WeatherForecastController {
    @Autowired
    private WeatherForecastService weatherForecastService;
    @PostMapping()
    public ResponseEntity<WeatherForecastDto.WeatherForecastResponse> save (@RequestBody WeatherForecastDto.WeatherForecastRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(weatherForecastService.save(request));
    }
    @GetMapping("/weatherforecast")
    public ResponseEntity<List<WeatherForecastDto.WeatherForecastResponse>> findAll(){
        return ResponseEntity.ok(weatherForecastService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<WeatherForecastDto.WeatherForecastResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(weatherForecastService.findById(id));
    }
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<WeatherForecastDto.WeatherForecastResponse>> findByLocationId(@PathVariable Long locationId){
        return ResponseEntity.ok(weatherForecastService.findByLocationId(locationId));
    }
}
