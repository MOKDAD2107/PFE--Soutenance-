package ma.enset.iotservice.mappers;

import ma.enset.iotservice.dtos.WaterRessourceDto;
import ma.enset.iotservice.entities.WaterRessource;
import ma.enset.iotservice.model.Location;
import org.springframework.stereotype.Component;

@Component
public class WaterRessourceMapper {
    // WaterRessourceRequest to WaterRessource
    public WaterRessource fromWaterRessourceRequestToWaterRessource(WaterRessourceDto.WaterRessourceRequest request, Location location){
        return WaterRessource.builder()
                .name(request.getName())
                .ressourceType(request.getRessourceType())
                .capaciteMax(request.getCapaciteMax())
                .currentLevel(request.getCurrentLevel())
                .locationId(request.getLocationId())
                .cityLocationId(request.getLocationId())
                .location(location)
                .build();
    }
    // WaterRessource to WaterRessourceResponse
    public WaterRessourceDto.WaterRessourceResponse fromWaterRessourcetoWaterRessourceResponse(WaterRessource waterRessource){
        return WaterRessourceDto.WaterRessourceResponse.builder()
                .id(waterRessource.getId())
                .name(waterRessource.getName())
                .cityLocationId(waterRessource.getCityLocationId())
                .ressourceType(waterRessource.getRessourceType())
                .capaciteMax(waterRessource.getCapaciteMax())
                .currentLevel(waterRessource.getCurrentLevel())
                .fillPercentage(waterRessource.getFillPercentage())
                .fillStatus(waterRessource.getFillStatus())
                .seuilBas(waterRessource.getSeuilBas())
                .seuilCritique(waterRessource.getSeuilCritique())
                .location(waterRessource.getLocation())
                .lastUpdate(waterRessource.getLastUpdate())
                .build();
    }
}
