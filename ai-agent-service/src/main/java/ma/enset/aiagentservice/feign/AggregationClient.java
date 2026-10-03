package ma.enset.aiagentservice.feign;

import ma.enset.aiagentservice.dtos.DashboardResponse;
import ma.enset.aiagentservice.dtos.GlobalSummaryResponse;
import ma.enset.aiagentservice.dtos.WaterStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DATA-AGGREGATION-SERVICE",configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface AggregationClient {
    @GetMapping("/api/aggregate/dashboard/{locationId}")
    DashboardResponse getDashboard(@PathVariable Long locationId);

    @GetMapping("/api/aggregate/global")
    GlobalSummaryResponse getGlobalSummary();

    @GetMapping("/api/aggregate/waterstatus")
    WaterStatusResponse getWaterStatus(@RequestParam(required = false) Long cityLocationId);

 }
