package ma.enset.weatherservice.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LocationDto {
    // locationRequest : creer une villes avec ses coordonnes de GPS
    // ce qu'on recoit du client
    @Data @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class LocationRequest{
        @NotBlank(message = "Le nom du pays est obligatoire")
    private String country;
        @NotBlank(message = "Le nom de la region est obligatoire")
    private String region;
        @NotBlank(message = "Le nom de la ville est obligatoire")
    private String nameCity;
        @NotNull(message = "La latitude est obligatoire")
        @DecimalMin(value = "-90.0",message = "Latitude invalide")
        @DecimalMax(value = "90.0",message = "Latitude invalide")
    private double  latitude;
        @NotNull(message = "La longitude est obligatoire")
        @DecimalMin(value = "-180.0",message = "Longitude invalide")
        @DecimalMax(value = "180.0",message = "Longitude invalide")
    private double  longitude;
    }
    // locationResponse : pour afficher les details complets
    // ce qu'on envoie au client
    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class LocationResponse{
        private Long id;
        private String nameCity;
        private String country;
        private String region;
        private Double latitude;
        private Double longitude;
    }
    // locationSummary : un resumer lege pour les listes (Id,Nom)
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LocationSummary {
        private Long id;
        private String nameCity;
        private String country;
        private Double latitude;
        private Double longitude;
    }
}
