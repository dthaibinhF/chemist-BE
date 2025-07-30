package dthaibinhf.project.chemistbe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledScheduleService {
    
    private final ScheduleService scheduleService;
    
    /**
     * Automatically generate schedules for all active groups every Monday at 8:00 AM
     * Generates schedules for the upcoming week (Monday to Sunday)
     */
    @Scheduled(cron = "0 0 8 * * MON")
    public void generateWeeklySchedulesForAllGroups() {
        try {
            log.info("Starting automatic weekly schedule generation");
            
            // Calculate next week dates (Monday to Sunday)
            LocalDate nextMonday = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7);
            LocalDate nextSunday = nextMonday.plusDays(6);
            
            log.info("Generating schedules for week: {} to {}", nextMonday, nextSunday);
            
            // Generate schedules for all active groups
            var results = scheduleService.generateSchedulesForAllActiveGroups(nextMonday, nextSunday);
            
            // Count total schedules generated
            int totalSchedules = results.stream()
                    .mapToInt(scheduleSet -> scheduleSet.size())
                    .sum();
            
            log.info("Automatic weekly generation completed successfully. Generated {} schedules for {} groups", 
                    totalSchedules, results.size());
                    
        } catch (Exception e) {
            log.error("Error during automatic weekly schedule generation", e);
        }
    }
    
    /**
     * Manual trigger for weekly generation (for testing or manual execution)
     */
    public void triggerWeeklyGeneration() {
        log.info("Manual trigger for weekly schedule generation");
        generateWeeklySchedulesForAllGroups();
    }
}