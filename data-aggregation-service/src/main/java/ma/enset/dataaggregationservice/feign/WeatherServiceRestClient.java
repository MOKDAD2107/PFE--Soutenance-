package ma.enset.dataaggregationservice.feign;

import ma.enset.dataaggregationservice.dto.weather.LatestWeatherResponse;
import ma.enset.dataaggregationservice.dto.weather.LocationResponse;
import ma.enset.dataaggregationservice.dto.weather.WeatherDataResponse;
import ma.enset.dataaggregationservice.dto.weather.WeatherForecastResponse;
import ma.enset.dataaggregationservice.interceptor.FeignInterceptors;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "WEATHER-SERVICE",configuration = FeignInterceptors.class)
public interface WeatherServiceRestClient {
    @GetMapping("/api/weathers/weather/location/{locationId}")
    List<WeatherDataResponse> getWeatherByLocation(@PathVariable Long locationId);
    @GetMapping("/api/weathers/weather/search/{cityName}")
    WeatherDataResponse searchCity(@PathVariable String cityName);
    @GetMapping("/api/weathers/latest")
    List<LatestWeatherResponse> getLatestWeather();
    @GetMapping("/api/forecast/location/{locationId}")
    List<WeatherForecastResponse>  getForecastByLocation(@PathVariable Long locationId);
    @GetMapping("/api/locations/location/{id}")
    LocationResponse getLocationInfo(@PathVariable Long id);
    @GetMapping("/api/locations/location")
    List<LocationResponse> getAllLocations();
}
