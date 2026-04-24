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
                .location(location)
                .build();
    }
    // WaterRessource to WaterRessourceResponse
    public WaterRessourceDto.WaterRessourceResponse fromWaterRessourcetoWaterRessourceResponse(WaterRessource waterRessource){
        return WaterRessourceDto.WaterRessourceResponse.builder()
                .id(waterRessource.getId())
                .name(waterRessource.getName())
                .ressourceType(waterRessource.getRessourceType())
                .capaciteMax(waterRessource.getCapaciteMax())
                .currentLevel(waterRessource.getCurrentLevel())
                .fillPercentage(waterRessource.getFillPercentage())
                .fillStatus(waterRessource.getFillStatus())
                .location(waterRessource.getLocation())
                .lastUpdate(waterRessource.getLastUpdate())
                .build();
    }
}
