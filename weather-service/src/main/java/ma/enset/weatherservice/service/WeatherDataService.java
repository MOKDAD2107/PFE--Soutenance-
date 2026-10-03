package ma.enset.weatherservice.service;

import ma.enset.weatherservice.client.OpenWeatherMapAPIClient;
import ma.enset.weatherservice.dtos.LatestWeatherDto;
import ma.enset.weatherservice.dtos.OpenWeatherMapResponse;
import ma.enset.weatherservice.dtos.WeatherDataDto;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.entities.WeatherData;
import ma.enset.weatherservice.enums.ApiSource;
import ma.enset.weatherservice.exceptions.RessourceNotFoundException;
import ma.enset.weatherservice.mappers.WeatherDataMapper;
import ma.enset.weatherservice.repository.LocationRepository;
import ma.enset.weatherservice.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherDataService {

    @Autowired
    private WeatherDataRepository weatherDataRepository;
    @Autowired
    private WeatherDataMapper weatherDataMapper;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private OpenWeatherMapAPIClient openWeatherMapAPIClient;
    @Autowired
    private WeatherForecastService weatherForecastService;

    public WeatherDataDto.WeatherDataResponse save(WeatherDataDto.WeatherDataRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RessourceNotFoundException("Location not Found" + request.getLocationId()));
        WeatherData weatherData = weatherDataMapper.fromWeatherDatatRequesttoWeatherData(request, location);
        WeatherData savedWeather = weatherDataRepository.save(weatherData);
        return weatherDataMapper.fromWeatherDatatoWeatherDataResponse(savedWeather);
    }

    public List<WeatherDataDto.WeatherDataResponse> findAll() {
        List<WeatherData> data = weatherDataRepository.findAll();
        return data.stream()
                .map(we -> weatherDataMapper.fromWeatherDatatoWeatherDataResponse(we))
                .toList();
    }

    public WeatherDataDto.WeatherDataResponse findById(Long id) {
        WeatherData weatherData = weatherDataRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Weather invalide" + id));
        return weatherDataMapper.fromWeatherDatatoWeatherDataResponse(weatherData);
    }

    public List<WeatherDataDto.WeatherDataResponse> findByLocationId(Long locationId) {
        List<WeatherData> weatherData = weatherDataRepository.findByLocationIdOrderByDateTimeDesc(locationId);
        return weatherData.stream()
                .map(wr -> weatherDataMapper.fromWeatherDatatoWeatherDataResponse(wr))
                .toList();
    }

    public WeatherDataDto.WeatherDataResponse findByCityName(String cityName) {
        // Cherche si la location existe déjà
        Optional<Location> cityExisting = locationRepository.findByNameCityIgnoreCase(cityName);

        if (cityExisting.isPresent()) {
            // Retourne la dernière donnée météo connue, sinon va la chercher et la sauvegarder
            return weatherDataRepository.findTopByLocationIdOrderByDateTimeDesc(cityExisting.get().getId())
                    .map(weatherDataMapper::fromWeatherDatatoWeatherDataResponse)
                    .orElseGet(() -> fetchAndSave(cityName));
        } else {
            // Ville inconnue en base -> on va la chercher via l'API et créer la location
            return fetchAndSave(cityName);
        }
    }

    private WeatherDataDto.WeatherDataResponse fetchAndSave(String cityName) {
        OpenWeatherMapResponse response = openWeatherMapAPIClient.getResponse(cityName);
        if (response == null) {
            throw new RessourceNotFoundException("Ville introuvable : " + cityName);
        }
        Location location = findOrCreateLocation(cityName, response);
        WeatherData saved = saveWeatherData(location, response);
        weatherForecastService.SaveForecastForLocation(location);
        return weatherDataMapper.fromWeatherDatatoWeatherDataResponse(saved);
    }

    private Location findOrCreateLocation(String cityName, OpenWeatherMapResponse response) {
        return locationRepository.findByNameCityIgnoreCase(cityName)
                .orElseGet(() -> locationRepository.save(Location.builder()
                        .nameCity(cityName)
                        .country("Maroc")
                        .region("Inconnue")
                        .latitude(response.getCoord() != null ? response.getCoord().getLat() : 0.0)
                        .longitude(response.getCoord() != null ? response.getCoord().getLon() : 0.0)
                        .build()));
    }

    private WeatherData saveWeatherData(Location location, OpenWeatherMapResponse response) {

        WeatherData weatherData = WeatherData.builder()
                .dateTime(LocalDateTime.now())
                .temperature(response.getMainData() != null ? response.getMainData().getTemp() : null)
                .humidity(response.getMainData() != null ? response.getMainData().getHumidity() : null)
                .pressure(response.getMainData() != null ? response.getMainData().getPressure() : null)
                .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : null)
                .description(response.getMainDescription())
                .weatherIcon(response.getMainIcon())
                .apiSource(ApiSource.OPEN_WEATHER_MAP)
                .location(location)
                .build();

        return weatherDataRepository.save(weatherData);
    }
    public List<LatestWeatherDto> findLatestWeatherForAllCities() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream().map(location -> {
                    var weather = weatherDataRepository.findTopByLocationIdOrderByDateTimeDesc(location.getId());
                    return LatestWeatherDto.builder()
                            .locationId(location.getId())
                            .city(location.getNameCity())
                            .country(location.getCountry())
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .temperature(weather.map(WeatherData::getTemperature).orElse(null))
                            .humidity(weather.map(WeatherData::getHumidity).orElse(null))
                            .windSpeed(weather.map(WeatherData::getWindSpeed).orElse(null))
                            .weatherDescription(weather.map(WeatherData::getDescription).orElse(null))
                            .weatherIcon(weather.map(WeatherData::getWeatherIcon).orElse(null))
                            .build();
                })
                .toList();
    }
}