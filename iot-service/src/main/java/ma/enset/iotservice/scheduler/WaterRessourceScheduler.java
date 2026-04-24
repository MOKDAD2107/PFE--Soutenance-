package ma.enset.iotservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.iotservice.entities.WaterRessource;
import ma.enset.iotservice.service.WaterRessourceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Slf4j
@RequiredArgsConstructor
public class WaterRessourceScheduler {
   private final WaterRessourceService waterRessourceService;
   private final Random random=new Random(); // generer des valeurs aleatoires

    @Scheduled(fixedRateString = "${iot.scheduler.water.rate:3600000}")
    public void simulatorWaterLevel(){
        log.info("=== Scheduler ressources eau démarré ===");
        List<WaterRessource> resources = waterRessourceService.findAllEntities();

        if (resources.isEmpty()) {
            log.warn("Aucune ressource eau trouvée ");
            return;
        }

        for (WaterRessource resource : resources) {
            // Variation aléatoire entre -2% et +1% (les barrages se vident lentement)
            double variation = (-2.0 + random.nextDouble() * 3.0);
            double newLevel = resource.getCurrentLevel() + (resource.getCapaciteMax() * variation / 100);

            // Garde le niveau dans les bornes [0, capaciteMax]
            newLevel = Math.max(0, Math.min(resource.getCapaciteMax(), newLevel));
            newLevel = Math.round(newLevel * 100.0) / 100.0;

            waterRessourceService.updateLevel(resource.getId(), newLevel);
            log.info("{} → niveau: {} / {} Mm³ ({}%)",
                    resource.getName(),
                    newLevel,
                    resource.getCapaciteMax(),
                    Math.round((newLevel / resource.getCapaciteMax()) * 1000.0) / 10.0);
        }

        log.info("=== Scheduler ressources eau terminé ===");
    }

}
