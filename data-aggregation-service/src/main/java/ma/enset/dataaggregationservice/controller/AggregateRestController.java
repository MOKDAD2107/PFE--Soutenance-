package ma.enset.dataaggregationservice.controller;

import ma.enset.dataaggregationservice.dto.aggregate.DashboardResponse;
import ma.enset.dataaggregationservice.dto.aggregate.GlobalSummaryResponse;
import ma.enset.dataaggregationservice.dto.aggregate.WaterStatusResponse;
import ma.enset.dataaggregationservice.service.AggregateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aggregate")
public class AggregateRestController {
    @Autowired
    private AggregateService aggregateService;

    @GetMapping("/dashboard/{locationId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable Long locationId) {
        return ResponseEntity.ok(aggregateService.getDashboard(locationId));
    }

    @GetMapping("/global")
    public ResponseEntity<GlobalSummaryResponse> getGlobalSummary(){
        return ResponseEntity.ok(aggregateService.globalSummary());
    }

    @GetMapping("/waterstatus")
    public ResponseEntity<WaterStatusResponse> getWaterStatus(){
        return ResponseEntity.ok(aggregateService.statusResponse());
    }
}
