package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.StatisticsDTO;
import dthaibinhf.project.chemistbe.service.StatisticsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class StatisticsController {

    StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<StatisticsDTO> getDashboardStatistics() {
        return ResponseEntity.ok(statisticsService.getDashboardStatistics());
    }
}