package ma.enset.iotservice.controller;

import ma.enset.iotservice.dtos.WaterRessourceDto;
import ma.enset.iotservice.enums.RessourceType;
import ma.enset.iotservice.model.Location;
import ma.enset.iotservice.service.WaterRessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waters")
public class WaterRessourceRestController {
    @Autowired
    private WaterRessourceService waterRessourceService;

    @PostMapping("/save")
    public ResponseEntity<WaterRessourceDto.WaterRessourceResponse> save(@RequestBody WaterRessourceDto.WaterRessourceRequest request,@RequestBody Location location) {
        return ResponseEntity.status(HttpStatus.CREATED).body(waterRessourceService.save(request,location));

    }

    @GetMapping("/allwater")
    public ResponseEntity<List<WaterRessourceDto.WaterRessourceResponse>> findAll(){
        return ResponseEntity.ok().body(waterRessourceService.findAll());
    }

    @GetMapping("/allwater/{id}")
    public ResponseEntity<WaterRessourceDto.WaterRessourceResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(waterRessourceService.findById(id));
    }
    @GetMapping("/water/location/{locationId}")
    public ResponseEntity<List<WaterRessourceDto.WaterRessourceResponse>> findByLocationId(@PathVariable Long locationId){
        return ResponseEntity.ok().body(waterRessourceService.findByLocationId(locationId));
    }
    @GetMapping("/water/types/{type}")
    public ResponseEntity<List<WaterRessourceDto.WaterRessourceResponse>> findByWaterRessourceType(@PathVariable RessourceType type){
        return ResponseEntity.ok().body(waterRessourceService.findByWaterRessourceType(type));
    }
    @GetMapping("/water/status/{status}")
    public ResponseEntity<List<WaterRessourceDto.WaterRessourceResponse>> findByFillStatus(@PathVariable String status){
        return ResponseEntity.ok().body(waterRessourceService.findByFillStatus(status));
    }

}
