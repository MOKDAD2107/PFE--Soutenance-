package ma.enset.dataaggregationservice.feign;

import ma.enset.dataaggregationservice.dto.iot.EnvironmentAlertResponse;
import ma.enset.dataaggregationservice.dto.iot.IotSensorResponse;
import ma.enset.dataaggregationservice.dto.iot.SensorReadingResponse;
import ma.enset.dataaggregationservice.dto.iot.WaterRessourceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "iot-service")
public interface IotServiceRestClient {
    @GetMapping("/api/waters/water/location/{locationId}")
    List<WaterRessourceResponse> getWaterByLocationId(@PathVariable Long locationId);
    @GetMapping("/api/waters/water/types/{type}")
    List<WaterRessourceResponse> getWaterByType(@PathVariable String type);
    @GetMapping("/api/waters/water/status/{status}")
    List<WaterRessourceResponse> getWaterByStatus(@PathVariable String status);
    @GetMapping("/api/sensors/sensor/location/{locationId}")
    List<IotSensorResponse> getIotSensorByLocationId(@PathVariable Long locationId);
    @GetMapping("/api/sensors/sensor/types/{type}")
    List<IotSensorResponse> getIotSensorByType(@PathVariable String type);
    @GetMapping("/api/sensors/sensor/active")
    List<IotSensorResponse> getIotSensorByActive();
    @GetMapping("/api/reading/sensor/senorid/{sensorId}")
    List<SensorReadingResponse> getSensorReadingByLocationId(@PathVariable Long sensorId);
    @GetMapping("/api/environement/alerts/location/{locationId}")
    List<EnvironmentAlertResponse> getAlertByLocationId(@PathVariable Long locationId);
    @GetMapping("/api/environement/alerts/severity/{severity}")
    List<EnvironmentAlertResponse> getAlertBySeverity(@PathVariable String severity);
    @GetMapping("/api/environement/alerts/status/{status}")
    List<EnvironmentAlertResponse> getAlertByStatus(@PathVariable String status);

}
