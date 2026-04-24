package ma.enset.iotservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.iotservice.enums.RessourceType;
import ma.enset.iotservice.model.Location;

import java.time.LocalDateTime;

public class WaterRessourceDto {
    @Data @NoArgsConstructor
    @AllArgsConstructor @Builder
    public static class WaterRessourceRequest{
        @NotBlank(message = "Le nom est obligatoire")
        private String name;
        @NotNull(message = "La capacite maximal est obligatoire")
        @Min(value = 0,message = "La capacité ne peut pas être négative")
        private double capaciteMax;
        @NotNull(message = "Le niveau actuel est obligatoire")
        @Min(value = 0, message = "Le niveau ne peut pas être négatif")
        private double currentLevel;
        @NotNull(message = "Le type de ressource est obligatoire")
        private RessourceType ressourceType;
        @NotNull(message = "L'id de location est obligatoire")
        private Long locationId;
    }
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WaterRessourceResponse{
        private Long id;
        private String name;
        private RessourceType ressourceType;
        private Double capaciteMax;
        private Double currentLevel;
        private Double fillPercentage;
        private String fillStatus;
        private LocalDateTime lastUpdate;
        private Location location;

    }
}
