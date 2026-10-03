package ma.enset.aiagentservice.feign;

import ma.enset.aiagentservice.dtos.WeatherDataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "WEATHER-SERVICE",configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface WeatherClient {

    @GetMapping("/api/weathers/weather/search/{cityName}")
    WeatherDataResponse findByCityName(@PathVariable String cityName);

}

