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
        protected void computeFiled(){
            lastUpdate = LocalDateTime.now();
            if (capaciteMax>0&&currentLevel>=0){
                fillPercentage=Math.round((currentLevel/capaciteMax)*1000.0)/10.0;
                fillStatus=computeFillStatus(fillPercentage);
            }
        }

        private String computeFillStatus(double pct){
            if(pct<20) return "CRITIQUE";
            if(pct<40) return "BAS";
            if(pct<70) return "NORMAL";
            return "ELEVE";
        }
}

