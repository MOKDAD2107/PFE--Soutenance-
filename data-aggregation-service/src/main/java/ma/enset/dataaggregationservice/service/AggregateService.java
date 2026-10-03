package ma.enset.dataaggregationservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.enset.dataaggregationservice.dto.aggregate.DashboardResponse;
import ma.enset.dataaggregationservice.dto.aggregate.GlobalSummaryResponse;
import ma.enset.dataaggregationservice.dto.aggregate.WaterStatusResponse;
import ma.enset.dataaggregationservice.dto.iot.EnvironmentAlertResponse;
import ma.enset.dataaggregationservice.dto.iot.IotSensorResponse;
import ma.enset.dataaggregationservice.dto.iot.SensorReadingResponse;
import ma.enset.dataaggregationservice.dto.iot.WaterRessourceResponse;
import ma.enset.dataaggregationservice.dto.weather.LatestWeatherResponse;
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

    // ----- Dashboard d'une ville ------
        public DashboardResponse getDashboard(Long locationId) {
            log.info("Dashboard pour locationId={}", locationId);

           List<WeatherDataResponse> weather =
                    safeCall(() -> weatherClient.getWeatherByLocation(locationId));
            List<WeatherForecastResponse> forecast =
                    safeCall(() -> weatherClient.getForecastByLocation(locationId));
            List<IotSensorResponse> sensor =
                    safeCall(() -> iotClient.getIotSensorByLocationId(locationId));

            List<SensorReadingResponse> rd = new ArrayList<>();
            for (IotSensorResponse s : sensor) {
                List<SensorReadingResponse> reading =
                        safeCall(() -> iotClient.getSensorReadingByLocationId(s.getId()));
                if (!reading.isEmpty()) {
                    rd.add(reading.get(reading.size()-1));
                }
            }
            WeatherDataResponse currentWeather = weather.isEmpty() ? null : weather.get(0);

            // Coordonnées GPS de la ville sélectionnée
            double cityLat = currentWeather != null && currentWeather.getLocation() != null
                    ? currentWeather.getLocation().getLatitude() : 0.0;
            double cityLon = currentWeather != null && currentWeather.getLocation() != null
                    ? currentWeather.getLocation().getLongitude() : 0.0;

            // Toutes les alertes actives + toutes les locations pour faire la correspondance GPS
                        List<EnvironmentAlertResponse> allAlerts =
                    safeCall(() -> iotClient.getAlertByStatus("ACTIVE"));
            List<EnvironmentAlertResponse> filteredAlerts = allAlerts.stream()
                    .filter(alert -> locationId.equals(alert.getLocationId()))
                    .collect(Collectors.toList());

            log.info("Alertes filtrées pour locationId={} : {} alertes retenues",
                    locationId, filteredAlerts.size());

            return DashboardResponse.builder()
                    .locationId(locationId)
                    .cityName(getCityName(currentWeather, sensor))
                    .country(currentWeather != null && currentWeather.getLocation() != null
                            ? currentWeather.getLocation().getCountry() : "MAROC")
                    .latitude(cityLat)
                    .longitude(cityLon)
                    .weatherData(currentWeather)
                    .forecastData(forecast)
                    .sensor(sensor)
                    .reading(rd)
                    .alerts(filteredAlerts)   // alertes géographiquement pertinentes
                    .generatedAt(LocalDateTime.now())
                    .build();
        }

        // ------ Résumé global --------
        public GlobalSummaryResponse globalSummary() {
            log.info("--------- Resume Globale ----------");
            List<EnvironmentAlertResponse> alerts =
                    safeCall(() -> iotClient.getAlertByStatus("ACTIVE"));
            List<IotSensorResponse> sensor =
                    safeCall(() -> iotClient.getIotSensorByActive());
            List<WaterRessourceResponse> criticalWater =
                    safeCall(() -> iotClient.getWaterByStatus("CRITIQUE"));
            List<GlobalSummaryResponse.CitySummary> cities = buildCitySummary(alerts, sensor);

            Double avgAirQuality=computeAvgForSensorType(sensor,"AIR_QUALITY");
            Double avgPoussiere= computeAvgForSensorType(sensor,"POUSSIERE");
            Double avgTemp=computeAvgForSensorType(sensor,"TEMPERATURE");

            return GlobalSummaryResponse.builder()
                    .allActivesAlerts(alerts)
                    .criticalWaterResources(criticalWater)
                    .activeSensor(sensor)
                    .cities(cities)
                    .totalActiveAlerts(alerts.size())
                    .totalActiveSensors(sensor.size())
                    .totalCriticalWaterResources(criticalWater.size())
                    .averageTemperature(avgTemp)
                    .averageAirQuality(avgAirQuality)
                    .averageDust(avgPoussiere)
                    .generatedAt(LocalDateTime.now())
                    .build();
        }

        // ------- État de toutes les ressources de l'eau -------
        public WaterStatusResponse statusResponse(Long cityLocationId) {
            List<WaterRessourceResponse> barrages = enrichLocations(safeCall(() -> iotClient.getWaterByType("BARRAGE")));
            List<WaterRessourceResponse> rivieres  = enrichLocations(safeCall(() -> iotClient.getWaterByType("RIVIERE")));
            List<WaterRessourceResponse> lacs      = enrichLocations(safeCall(() -> iotClient.getWaterByType("LAC")));
            List<WaterRessourceResponse> nappe     = enrichLocations(safeCall(() -> iotClient.getWaterByType("NAPPE_PHREATIQUE")));

            if (cityLocationId != null) {
                barrages = filterByCity(barrages, cityLocationId);
                rivieres = filterByCity(rivieres, cityLocationId);
                lacs     = filterByCity(lacs, cityLocationId);
                nappe    = filterByCity(nappe, cityLocationId);
            }

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
    private List<WaterRessourceResponse> enrichLocations(List<WaterRessourceResponse> resources) {
        for (WaterRessourceResponse w : resources) {
            if (w.getCityLocationId() != null) {
                try {
                    LocationResponse loc = weatherClient.getLocationInfo(w.getCityLocationId());
                    w.setLocation(loc);
                } catch (Exception e) {
                    log.warn("Impossible de résoudre la ville pour cityLocationId={}", w.getCityLocationId());
                }
            }
        }
        return resources;
    }
    private List<WaterRessourceResponse> filterByCity(List<WaterRessourceResponse> list, Long cityLocationId) {
        return list.stream()
                .filter(w -> cityLocationId.equals(w.getCityLocationId()))
                .toList();
    }

        // ---- Méthodes privées utilitaires ----
        private List<GlobalSummaryResponse.CitySummary> buildCitySummary(
                List<EnvironmentAlertResponse> alerts,
                List<IotSensorResponse> sensors) {

            List<LatestWeatherResponse> latestWeatherAllCities =
                    safeCall(() -> weatherClient.getLatestWeather());
            Map<Long, List<IotSensorResponse>> sensorByLocation = new HashMap<>();
            for (IotSensorResponse sensor : sensors) {
                if (sensor.getLocationId() != null) {
                    sensorByLocation.computeIfAbsent(
                            sensor.getLocationId(), k -> new ArrayList<>()).add(sensor);
                }
            }

            List<GlobalSummaryResponse.CitySummary> results = new ArrayList<>();

            for (LatestWeatherResponse weather : latestWeatherAllCities) {
                Long locId = weather.getLocationId();
                if (locId == null) continue;
                List<IotSensorResponse> locSensors =
                        sensorByLocation.getOrDefault(locId, Collections.emptyList());
                int alertCount = (int) alerts.stream()
                        .filter(alert -> locId.equals(alert.getLocationId()))
                        .count();

                results.add(GlobalSummaryResponse.CitySummary.builder()
                        .locationId(locId)
                        .city(weather.getCity())
                        .country(weather.getCountry())
                        .latitude(weather.getLatitude())
                        .longitude(weather.getLongitude())
                        .temperature(weather.getTemperature() != null ? weather.getTemperature() : 0.0)
                        .humidity(weather.getHumidity() != null ? weather.getHumidity() : 0.0)
                        .windSpeed(weather.getWindSpeed() != null ? weather.getWindSpeed() : 0.0)
                        .weatherDescription(weather.getWeatherDescription())
                        .weatherIcon(weather.getWeatherIcon())
                        .activeAlertCount(alertCount)
                        .activeSensorsCount((int) locSensors.stream()
                                .filter(IotSensorResponse::isActive).count())
                        .build());
            }
            return results;
        }
        private Map<String, Long> computeStat(List<WaterRessourceResponse> water) {
            return water.stream()
                    .filter(w -> w != null && w.getFillStatus() != null)
                    .collect(Collectors.groupingBy(WaterRessourceResponse::getFillStatus,
                            Collectors.counting()));
        }

        private <T> List<T> safeCall(Supplier<List<T>> supplier) {
            try {
                return supplier.get();
            } catch (Exception e) {
                log.warn("Erreur Appel Service: {}", e.getMessage());
                return Collections.emptyList();
            }
        }

        private String getCityName(WeatherDataResponse weather, List<IotSensorResponse> sensor) {
            if (weather != null && weather.getLocation() != null) {
                return weather.getLocation().getNameCity();
            }
            if (sensor != null && !sensor.isEmpty() && sensor.get(0).getLocation() != null) {
                return sensor.get(0).getLocation().getNameCity();
            }
            return "Ville inconnue";
       }

       private Double computeAvgForSensorType(List<IotSensorResponse> sensors,String type) {
            List<Double> values=new ArrayList<>();
            for (IotSensorResponse s :sensors) {
                if (type.equals(s.getSensorType())){
                    List<SensorReadingResponse> readings=safeCall(()->iotClient.getSensorReadingByLocationId(s.getId()));
                    if (!readings.isEmpty()){
                        values.add(readings.get(readings.size()-1).getValeur());
                    }
                }
            }
            return values.isEmpty() ? null : values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
       }

}
