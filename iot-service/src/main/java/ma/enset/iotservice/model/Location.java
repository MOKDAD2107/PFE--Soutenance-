package ma.enset.iotservice.model;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Location {
    private Long id;
    private String nameCity;
    private double latitude;
    private double longitude;

}
