package ma.enset.iotservice.repository;

import ma.enset.iotservice.entities.WaterRessource;
import ma.enset.iotservice.enums.RessourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterRessourceRepository extends JpaRepository<WaterRessource,Long> {
    List<WaterRessource> findByLocationId(Long locationId);
    List<WaterRessource> findByRessourceType(RessourceType type);
    List<WaterRessource> findByFillStatus(String status);
}
