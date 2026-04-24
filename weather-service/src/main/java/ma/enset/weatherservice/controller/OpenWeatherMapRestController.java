package ma.enset.weatherservice.controller;

import ma.enset.weatherservice.client.OpenWeatherMapAPIClient;
import ma.enset.weatherservice.dtos.OpenWeatherForecastResponse;
import ma.enset.weatherservice.dtos.OpenWeatherMapResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class OpenWeatherMapRestController {
    @Autowired
    private OpenWeatherMapAPIClient openWeatherMapAPIClient;

    @GetMapping("/test-weather/{city}")
    public OpenWeatherMapResponse test(@PathVariable String city){
        return openWeatherMapAPIClient.getResponse(city);
    }

    @GetMapping("/test-forecast/{city}")
    public OpenWeatherForecastResponse testforecast(@PathVariable String city){
        return openWeatherMapAPIClient.getForecastResponse(city);
    }
}
