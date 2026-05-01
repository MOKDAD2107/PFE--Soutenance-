package ma.enset.iotservice.entities;

import jakarta.persistence.*;
import lombok.*;

import ma.enset.iotservice.enums.RessourceType;
import ma.enset.iotservice.model.Location;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class WaterRessource {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private double capaciteMax;
        private double currentLevel;
        private double fillPercentage;// (currentLevel/capaciteMax)*100
        @Enumerated(EnumType.STRING)
        private RessourceType ressourceType;
        private String fillStatus;
        private Long locationId;
        @Transient
        private Location location;
        private LocalDateTime lastUpdate;

        @PrePersist @PreUpdate
        protected void computeField() {
            lastUpdate = LocalDateTime.now();
            if (ressourceType==null){
                fillPercentage=0.0;
                fillStatus="Non Disponible";
                return;
            }
            switch (ressourceType) {
                case BARRAGE:
                case RIVIERE :
                        if (capaciteMax >0 && currentLevel > 0){
                            double pct= (currentLevel/capaciteMax)*100;
                            fillPercentage=Math.min(100.0,Math.max(0.0,pct));
                            fillPercentage=Math.round(fillPercentage*10.0)/10.0;
                        }else {
                            fillPercentage=0.0;
                        }
                        break;
                case LAC:
                    fillPercentage=Math.min(100.0,Math.max(0.0,currentLevel));
                    fillPercentage=Math.round(fillPercentage*10.0)/10.0;
                    break;
                case NAPPE_PHREATIQUE : {
                    if (capaciteMax != 0 && currentLevel != 0) {
                        double pct = (Math.abs(capaciteMax) / Math.abs(currentLevel)) * 100;
                        //fillPercentage=Math.min(100.0,Math.max(0.0,pct));
                        fillPercentage=Math.round(pct *10.0)/10.0;
                    }else {
                        fillPercentage=0.0;
                    }
                    break;
                }
                default : {
                    fillPercentage=0.0;
                    break;
                }
                }
            fillStatus=computeFillStatus(fillPercentage);
        }
        private String computeFillStatus(double pct){
            if(pct<20) return "CRITIQUE";
            if(pct<40) return "BAS";
            if(pct<70) return "NORMAL";
            return "ELEVE";
        }
}

