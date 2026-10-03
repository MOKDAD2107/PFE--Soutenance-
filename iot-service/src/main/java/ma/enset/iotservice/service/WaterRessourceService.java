package ma.enset.iotservice.service;

import jakarta.transaction.Transactional;
import ma.enset.iotservice.dtos.WaterRessourceDto;
import ma.enset.iotservice.entities.WaterRessource;
import ma.enset.iotservice.enums.RessourceType;
import ma.enset.iotservice.exceptions.RessourceNotFoundException;
import ma.enset.iotservice.mappers.WaterRessourceMapper;
import ma.enset.iotservice.model.Location;
import ma.enset.iotservice.repository.WaterRessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaterRessourceService {
    @Autowired
    private WaterRessourceRepository  waterRessourceRepository;
    @Autowired
    private WaterRessourceMapper waterRessourceMapper;
    public WaterRessourceDto.WaterRessourceResponse save(WaterRessourceDto.WaterRessourceRequest request){
        WaterRessource waterRessource= waterRessourceMapper.fromWaterRessourceRequestToWaterRessource(request,null);
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


    // Dans WaterRessourceService, ajouter une méthode directe :
    @Transactional
    public void updateLevel(Long id, double newLevel) {
        WaterRessource ressource=waterRessourceRepository.findById(id).orElseThrow(()->new RessourceNotFoundException("Ressource non trouve"+id));
        double pct = ressource.getCapaciteMax() > 0
                ? Math.round((newLevel / ressource.getCapaciteMax()) * 1000.0) / 10.0
                : 0;
        ressource.setCurrentLevel(newLevel);
        ressource.setFillPercentage(pct);
        ressource.setLastUpdate(LocalDateTime.now());
        // déduire fillStatus selon pct
        ressource.setFillStatus(ressource.computeFillStatus(pct));
        waterRessourceRepository.save(ressource);
    }

    // Utiliser par le scheduler
    public List<WaterRessource> findAllEntities(){
        return waterRessourceRepository.findAll();
    }
    //update
    @Transactional
    public WaterRessourceDto.WaterRessourceResponse update(Long id, WaterRessourceDto.WaterRessourceRequest request){
        WaterRessource ressource = waterRessourceRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Ressource non trouvée " + id));
        ressource.setName(request.getName());
        ressource.setCapaciteMax(request.getCapaciteMax());
        ressource.setCurrentLevel(request.getCurrentLevel());
        ressource.setRessourceType(request.getRessourceType());
        ressource.setLocationId(request.getLocationId());
        ressource.setCityLocationId(request.getLocationId());
        // fillPercentage / fillStatus / lastUpdate recalculés automatiquement par @PreUpdate sur l'entité
        return waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(waterRessourceRepository.save(ressource));
    }
    // Delete
    @Transactional
    public void delete(Long id){
        if (!waterRessourceRepository.existsById(id)) {
            throw new RessourceNotFoundException("Ressource non trouvée " + id);
        }
        waterRessourceRepository.deleteById(id);
    }
    //update seuil
    @Transactional
    public WaterRessourceDto.WaterRessourceResponse updateSeuil(Long id, WaterRessourceDto.WaterRessourceSeuilRequest request){
        WaterRessource ressource = waterRessourceRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Ressource non trouvée " + id));
        ressource.setSeuilBas(request.getSeuilBas());
        ressource.setSeuilCritique(request.getSeuilCritique());
        return waterRessourceMapper.fromWaterRessourcetoWaterRessourceResponse(waterRessourceRepository.save(ressource));
    }
}
