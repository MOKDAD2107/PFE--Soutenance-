package ma.enset.dataaggregationservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.enset.dataaggregationservice.dto.aggregate.DashboardResponse;
import ma.enset.dataaggregationservice.dto.aggregate.GlobalSummaryResponse;
import ma.enset.dataaggregationservice.dto.aggregate.WaterStatusResponse;
import ma.enset.dataaggregationservice.dto.iot.EnvironmentAlertResponse;
import ma.enset.dataaggregationservice.dto.iot.IotSensorResponse;
import ma.enset.dataaggregationservice.dto.iot.SensorReadingResponse;
import ma.enset.dataaggregationservice.dto.iot.WaterRessourceResponse;
import ma.enset.dataaggregationservice.dto.weather.LocationResponse;
import ma.enset.dataaggregationservice.dto.weather.WeatherDataResponse;
import ma.enset.dataaggregationservice.dto.weather.WeatherForecastResponse;
import ma.enset.dataaggregationservice.feign.IotServiceRestClient;
import ma.enset.dataaggregationservice.feign.WeatherServiceRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Service
@Slf4j
public class AggregateService {

    private IotServiceRestClient iotClient;

    private WeatherServiceRestClient  weatherClient;


    private <T> List<T> safeCall(Supplier<List<T>> supplier) {
        try {
            return supplier.get();
        }catch (Exception e) {
         log.warn("Erreur Appel Service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String getCityName(WeatherDataResponse weather, List<IotSensorResponse> sensor) {
        if (weather!=null&&weather.getLocation()!=null){
            return weather.getLocation().getNameCity();
        }
        if (sensor!=null&&sensor.get(0).getLocation()!=null){
            return sensor.get(0).getLocation().getNameCity();
        }
        return "Ville inconnue";
    }
    // ----- Dashboard d'une ville ------
    public DashboardResponse getDashboard(Long locationId){
        log.info("Dashboard pour locationId={}", locationId);
        List<WeatherDataResponse> weather =
                safeCall(()->weatherClient.getWeatherByLocation(locationId));
        List<WeatherForecastResponse>  forecast =
                safeCall(()->weatherClient.getForecastByLocation(locationId));
        List<IotSensorResponse> sensor=
                safeCall(()->iotClient.getIotSensorByLocationId(locationId));
        List<SensorReadingResponse> reading=
                safeCall(()->iotClient.getSensorReadingByLocationId(locationId));
        List<EnvironmentAlertResponse> alert=
                safeCall(()->iotClient.getAlertByLocationId(locationId));
        //meteo actuelle , juste la 1ere
        WeatherDataResponse currentWeather=weather.isEmpty()?null:weather.get(0);
        return DashboardResponse.builder()
                .locationId(locationId)
                .cityName(getCityName(currentWeather,sensor))
                .weatherData(currentWeather)
                .forecastData(forecast)
                .sensor(sensor)
                .reading(reading)
                .alerts(alert)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    // ------ resume global --------
    public GlobalSummaryResponse globalSummary(){
        log.info("--------- Resume Globale ----------");
        List<EnvironmentAlertResponse>alerts=
                safeCall(()->iotClient.getAlertByStatus("ACTIVE"));
        List<IotSensorResponse> sensor=
                safeCall(()->iotClient.getIotSensorByActive());
        List<WaterRessourceResponse> criticalWater =
                safeCall(()->iotClient.getWaterByStatus("CRITIQUE"));
        return GlobalSummaryResponse.builder()
                .allActivesAlerts(alerts)
                .criticalWaterResources(criticalWater)
                .activeSensor(sensor)
                .totalActiveAlerts(alerts.size())
                .totalActiveSensors(sensor.size())
                .totalCriticalWaterResources(criticalWater.size())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    // ------- etat de toutes les ressources de l'eau -------
    public WaterStatusResponse statusResponse(){
        List<WaterRessourceResponse>barrages=
                safeCall(()->iotClient.getWaterByType("BARRAGE"));
        List<WaterRessourceResponse>rivieres=
                safeCall(()->iotClient.getWaterByType("RIVIERE"));
        List<WaterRessourceResponse>lacs=
            safeCall(()->iotClient.getWaterByType("LACS"));
        List<WaterRessourceResponse>nappe=
           safeCall(()->iotClient.getWaterByType("NAPPE_PHREATIQUE"));

        long critique=barrages.stream().filter(b->"CRITIQUE".equals(b.getFillStatus())).count();


        return WaterStatusResponse.builder()
                .barrages(barrages)
                .riviere(rivieres)
                .lacs(lacs)
                .nappe(nappe)
                .totalBarrages(barrages.size())
                .totalRiveries(rivieres.size())
                .totalLacs(lacs.size())
                .totalNappe(nappe.size())
                .build();

    }
}
