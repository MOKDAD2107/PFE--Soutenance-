package ma.enset.weatherservice.service;

import ma.enset.weatherservice.dtos.LocationDto;
import ma.enset.weatherservice.entities.Location;
import ma.enset.weatherservice.exceptions.RessourceNotFoundException;
import ma.enset.weatherservice.mappers.LocationMapper;
import ma.enset.weatherservice.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {
        @Autowired
        private LocationRepository locationRepository;
        @Autowired
        private LocationMapper locationMapper;

        /*public Location save( Location location){
            return locationRepository.save(location);
        }*/
        // Request -> Entity->save->response
        public LocationDto.LocationResponse save(LocationDto.LocationRequest request){
            Location location = locationMapper.fromLocationRequesttoLocation(request);
            Location savedLocation = locationRepository.save(location);
            return locationMapper.fromlocationtoLocationResponse(savedLocation);
        }

        public List<LocationDto.LocationResponse> findAll(){
            List<Location> locations = locationRepository.findAll();
            List<LocationDto.LocationResponse> locationResponses = locations.stream()
                    .map(loc->locationMapper.fromlocationtoLocationResponse(loc))
                    .toList();
            return locationResponses;
        }

        public LocationDto.LocationResponse findById(Long id){
         Location location = locationRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Location non trouve:"+id));
         LocationDto.LocationResponse locationResponse = locationMapper.fromlocationtoLocationResponse(location);
            return locationResponse;
        }
}
