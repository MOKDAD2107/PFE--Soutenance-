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
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AggregateService {
    @Autowired
    private IotServiceRestClient iotClient;
    @Autowired
    private WeatherServiceRestClient  weatherClient;
    private List<GlobalSummaryResponse.CitySummary>buildCitySummary(
            List<EnvironmentAlertResponse> alerts,List<IotSensorResponse>sensors){
        Map<Long,List<IotSensorResponse>> byLocation=new HashMap<>();
        // regrouper les capteurs par ville
        for (IotSensorResponse sensor: sensors){
            if (sensor.getLocationId()!=null){
                Long locId=sensor.getLocationId();
                byLocation.putIfAbsent(locId, new ArrayList<>());
                byLocation.get(locId).add(sensor);
            }
        }

        List<GlobalSummaryResponse.CitySummary> results=new ArrayList<>();
        //resumer pour chaque ville
        for (Long locId: byLocation.keySet()){
            List<IotSensorResponse> locSensors=byLocation.get(locId);
            IotSensorResponse first=locSensors.get(0);

            //appel meteo
            List<WeatherDataResponse> weather=safeCall(()->weatherClient.getWeatherByLocation(locId));
            WeatherDataResponse latest=weather.isEmpty()?null:weather.get(0);

            //compter les alertes
            int alertCount=0;
            for (EnvironmentAlertResponse alert:alerts){
                if (alert.getLocation()!=null&&locId.equals(alert.getLocation().getId())){
                    alertCount++;
                }
            }
            LocationResponse loc=weatherClient.getLocationInfo(locId);

        GlobalSummaryResponse.CitySummary city=GlobalSummaryResponse.CitySummary.builder()
                .locationId(locId)
                .city(loc.getNameCity())
                .country(loc.getCountry())
                .temperature(latest!=null?latest.getTemperature():0.0)
                .humidity(latest!=null?latest.getHumidity():0.0)
                .windSpeed(latest!=null?latest.getWindSpeed():0.0)
                .weatherDescription(latest!=null?latest.getDescription():null)
                .activeAlertCount(alertCount)
                .activeSensorsCount((int)locSensors.stream().filter(IotSensorResponse::isActive).count())
                .build();
            results.add(city);
    }
        return results;
    }

    private Map<String, Long> computeStat(List<WaterRessourceResponse> water){
        return water.stream()
                .filter(w -> w != null && w.getFillStatus() != null)
                .collect(Collectors.groupingBy(WaterRessourceResponse::getFillStatus,
                        Collectors.counting()));
    }

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
        List<SensorReadingResponse> rd=new ArrayList<>();
        for (IotSensorResponse s:sensor){
            List<SensorReadingResponse> reading=
                    safeCall(()->iotClient.getSensorReadingByLocationId(s.getId()));
            if (!reading.isEmpty()){
                rd.add(reading.get(0));
            }
        }

        List<EnvironmentAlertResponse> alert=
                safeCall(()->iotClient.getAlertByLocationId(locationId));
        //meteo actuelle , juste la 1ere
        WeatherDataResponse currentWeather=weather.isEmpty()?null:weather.get(0);
        return DashboardResponse.builder()
                .locationId(locationId)
                .cityName(getCityName(currentWeather,sensor))
                .country(currentWeather!=null&&currentWeather.getLocation()!=null
                        ?currentWeather.getLocation().getCountry():"MAROC")
                .latitude(currentWeather!=null&&currentWeather.getLocation()!=null
                        ?currentWeather.getLocation().getLatitude() :0.0)
                .longitude(currentWeather!=null&&currentWeather.getLocation()!=null
                        ?currentWeather.getLocation().getLongitude() : 0.0)
                .weatherData(currentWeather)
                .forecastData(forecast)
                .sensor(sensor)
                .reading(rd)
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
        List<GlobalSummaryResponse.CitySummary> cities=buildCitySummary(alerts,sensor);
        return GlobalSummaryResponse.builder()
                .allActivesAlerts(alerts)
                .criticalWaterResources(criticalWater)
                .activeSensor(sensor)
                .cities(cities)
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
            safeCall(()->iotClient.getWaterByType("LAC"));
        List<WaterRessourceResponse>nappe=
           safeCall(()->iotClient.getWaterByType("NAPPE_PHREATIQUE"));

        return WaterStatusResponse.builder()
                .barrages(barrages)
                .riviere(rivieres)
                .lacs(lacs)
                .nappe(nappe)
                .totalBarrages(barrages.size())
                .barrageStats(computeStat(barrages))
                .totalRiveries(rivieres.size())
                .riviereStats(computeStat(rivieres))
                .totalLacs(lacs.size())
                .lacsStats(computeStat(lacs))
                .totalNappe(nappe.size())
                .nappeStats(computeStat(nappe))
                .generatedAt(LocalDateTime.now())
                .build();
    }

}
