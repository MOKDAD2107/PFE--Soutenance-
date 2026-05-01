package ma.enset.iotservice.service;

import ma.enset.iotservice.dtos.WaterRessourceDto;
import ma.enset.iotservice.entities.WaterRessource;
import ma.enset.iotservice.enums.RessourceType;
import ma.enset.iotservice.exceptions.RessourceNotFoundException;
import ma.enset.iotservice.mappers.WaterRessourceMapper;
import ma.enset.iotservice.model.Location;
import ma.enset.iotservice.repository.WaterRessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaterRessourceService {
    @Autowired
    private WaterRessourceRepository  waterRessourceRepository;
    @Autowired
    private WaterRessourceMapper waterRessourceMapper;
    public WaterRessourceDto.WaterRessourceResponse save(WaterRessourceDto.WaterRessourceRequest request, Location location){
        WaterRessource waterRessource= waterRessourceMapper.fromWaterRessourceRequestToWaterRessource(request,location);
        return waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(waterRessourceRepository.save(waterRessource));
    }
    public List<WaterRessourceDto.WaterRessourceResponse> findAll(){
        List<WaterRessource> waterRessources= waterRessourceRepository.findAll();
        List<WaterRessourceDto.WaterRessourceResponse>responses =waterRessources.stream()
                .map(wt->waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(wt))
                .toList();
        return responses;
    }

    public WaterRessourceDto.WaterRessourceResponse findById(Long id){
        WaterRessource waterRessource=waterRessourceRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Ressource non trouve"+id));
        return waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(waterRessource);
    }
    public List<WaterRessourceDto.WaterRessourceResponse> findByLocationId(Long locationId){
        List<WaterRessource> waterRessources=waterRessourceRepository.findByLocationId(locationId);
        List<WaterRessourceDto.WaterRessourceResponse> response =waterRessources.stream()
                .map(wt->waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(wt))
                .toList();
        return response;
    }

    public List<WaterRessourceDto.WaterRessourceResponse> findByWaterRessourceType(RessourceType type) {
        List<WaterRessource> waterRessources=waterRessourceRepository.findByRessourceType(type);
        List<WaterRessourceDto.WaterRessourceResponse> responses=waterRessources.stream()
                .map(wt->waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(wt))
                .toList();
        return responses;
    }
    public List<WaterRessourceDto.WaterRessourceResponse> findByFillStatus(String status) {
        List<WaterRessource> water =waterRessourceRepository.findByFillStatus(status);
        List<WaterRessourceDto.WaterRessourceResponse> response=water.stream()
                .map(wt->waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(wt))
                .toList();
        return response;
    }
    public void updateLevel(Long id, Double newLevel){
    WaterRessource ressource=waterRessourceRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Ressource non trouve"+id));
    ressource.setCurrentLevel(newLevel);
    waterRessourceRepository.save(ressource);
    }
    // Utiliser par le scheduler
    public List<WaterRessource> findAllEntities(){
        return waterRessourceRepository.findAll();
    }
}
