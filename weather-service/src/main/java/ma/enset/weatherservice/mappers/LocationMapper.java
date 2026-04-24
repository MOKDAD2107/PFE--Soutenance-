package ma.enset.weatherservice.mappers;

import ma.enset.weatherservice.dtos.LocationDto;
import ma.enset.weatherservice.entities.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
    // locationRequest to location
    public Location fromLocationRequesttoLocation(LocationDto.LocationRequest request) {
        return Location.builder()
                .country(request.getCountry())
                .region(request.getRegion())
                .nameCity(request.getNameCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    // Location to LocationResponse
    public LocationDto.LocationResponse fromlocationtoLocationResponse(Location location){
        return LocationDto.LocationResponse.builder()
                .id(location.getId())
                .country(location.getCountry())
                .region(location.getRegion())
                .nameCity(location.getNameCity())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    // location to LocationSummary
    public LocationDto.LocationSummary fromLocationtoLocationSummary(Location location){
        return LocationDto.LocationSummary.builder()
                .id(location.getId())
                .country(location.getCountry())
                .nameCity(location.getNameCity())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
