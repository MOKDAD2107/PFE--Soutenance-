package ma.enset.iotservice.controller;

import ma.enset.iotservice.dtos.WaterRessourceDto;
import ma.enset.iotservice.enums.RessourceType;
import ma.enset.iotservice.service.WaterRessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waters")
public class WaterRessourceRestController {
    @Autowired
    private WaterRessourceService waterRessourceService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<WaterRessourceDto.WaterRessourceResponse> save(@RequestBody WaterRessourceDto.WaterRessourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(waterRessourceService.save(request));

    }
    // ── Nouveau : mise à jour complète (ADMIN) ──
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<WaterRessourceDto.WaterRessourceResponse> update(@PathVariable Long id, @RequestBody WaterRessourceDto.WaterRessourceRequest request) {
        return ResponseEntity.ok().body(waterRessourceService.update(id, request));
    }

    // ── Nouveau : suppression (ADMIN) ──
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waterRessourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/seuil")
    public ResponseEntity<WaterRessourceDto.WaterRessourceResponse> updateThreshold(@PathVariable Long id, @RequestBody WaterRessourceDto.WaterRessourceSeuilRequest request) {
        return ResponseEntity.ok().body(waterRessourceService.updateSeuil(id, request));
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
