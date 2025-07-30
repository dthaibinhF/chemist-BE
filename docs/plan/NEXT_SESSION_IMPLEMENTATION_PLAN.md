# Next Session Implementation Plan: GroupSchedule Template Cascade Updates

## 🎯 Implementation Goal
Add template-level cascade functionality where changes to GroupSchedule templates (within Group updates) automatically propagate to all future generated Schedule entities using **Option A: Cascade During Group Update**.

## 📋 Implementation Checklist

### Phase 1: Core Infrastructure ⏳
- [ ] Create `CascadeOptions.java` DTO
- [ ] Create `GroupScheduleChange.java` DTO  
- [ ] Create `TemplateChangeImpact.java` DTO
- [ ] Create `CascadeUpdateMode.java` enum
- [ ] Create `GroupScheduleCascadeService.java`

### Phase 2: Repository Enhancements ⏳
- [ ] Add `findFutureSchedulesByGroupAndDayOfWeek()` to ScheduleRepository
- [ ] Add `deleteSchedulesByGroupAndDay()` to ScheduleRepository
- [ ] Add `updateSchedulesByGroupAndDay()` to ScheduleRepository
- [ ] Add `findGroupSchedulesByGroupId()` to GroupRepository

### Phase 3: Service Layer Updates ⏳
- [ ] Enhance `GroupService.updateGroup()` with cascade detection
- [ ] Add `detectGroupScheduleChanges()` method
- [ ] Add `cascadeGroupChanges()` method
- [ ] Add `previewGroupUpdateImpact()` method

### Phase 4: Controller Enhancement ⏳
- [ ] Update `GroupController.updateGroup()` to handle cascade_options
- [ ] Add request/response handling for preview mode
- [ ] Add error handling for cascade failures

### Phase 5: Testing & Documentation ⏳
- [ ] Update `test_bulk_schedule_generation.md`
- [ ] Update `BULK_SCHEDULE_API_DOCUMENTATION.md`
- [ ] Create test scenarios for template cascade
- [ ] Test day change, time change, room change scenarios

## 🔧 Technical Implementation Details

### 1. New DTOs Structure

```java
// CascadeOptions.java
public class CascadeOptions {
    private boolean updateFutureSchedules = false;
    private CascadeUpdateMode cascadeMode = CascadeUpdateMode.TEMPLATE_AND_FUTURE_SCHEDULES;
    private boolean previewOnly = false;
}

// CascadeUpdateMode.java
public enum CascadeUpdateMode {
    TEMPLATE_ONLY,
    TEMPLATE_AND_FUTURE_SCHEDULES,
    TEMPLATE_AND_ALL_SCHEDULES
}

// GroupScheduleChange.java
public class GroupScheduleChange {
    private Integer groupScheduleId;
    private String changeType; // DAY_CHANGE, TIME_CHANGE, ROOM_CHANGE
    private String oldValue;
    private String newValue;
    private int affectedSchedulesCount;
}

// TemplateChangeImpact.java
public class TemplateChangeImpact {
    private int totalChangesCount;
    private List<GroupScheduleChange> changes;
    private List<LocalDate> deletedDates;
    private List<LocalDate> createdDates;
    private List<LocalDate> modifiedDates;
}
```

### 2. Enhanced Group Update API

```java
// GroupController.java enhancement
@PutMapping("/{id}")
public ResponseEntity<GroupDTO> updateGroup(
    @PathVariable Integer id, 
    @RequestBody GroupUpdateRequest request) {
    
    CascadeOptions cascadeOptions = request.getCascadeOptions();
    
    if (cascadeOptions != null && cascadeOptions.isPreviewOnly()) {
        TemplateChangeImpact impact = groupService.previewGroupUpdateImpact(id, request);
        return ResponseEntity.ok(GroupDTO.withImpactPreview(impact));
    }
    
    GroupDTO result = groupService.updateGroup(id, request, cascadeOptions);
    return ResponseEntity.ok(result);
}
```

### 3. Service Layer Logic

```java
// GroupService.java enhancement
@Transactional
public GroupDTO updateGroup(Integer groupId, GroupUpdateRequest request, CascadeOptions options) {
    // 1. Detect GroupSchedule changes
    List<GroupScheduleChange> scheduleChanges = detectGroupScheduleChanges(groupId, request);
    
    // 2. Update Group entity
    Group updatedGroup = performGroupUpdate(groupId, request);
    
    // 3. Apply cascade if enabled and changes exist
    if (!scheduleChanges.isEmpty() && 
        options != null && 
        options.isUpdateFutureSchedules()) {
        
        groupScheduleCascadeService.cascadeChanges(groupId, scheduleChanges, options);
    }
    
    return groupMapper.toDto(updatedGroup);
}

private List<GroupScheduleChange> detectGroupScheduleChanges(Integer groupId, GroupUpdateRequest request) {
    Group existingGroup = findGroupById(groupId);
    List<GroupScheduleChange> changes = new ArrayList<>();
    
    // Compare old vs new GroupSchedules
    for (GroupScheduleDTO newSchedule : request.getGroupSchedules()) {
        GroupSchedule oldSchedule = findExistingGroupSchedule(existingGroup, newSchedule.getId());
        
        if (oldSchedule != null) {
            // Check for day change
            if (!oldSchedule.getDayOfWeek().equals(newSchedule.getDayOfWeek())) {
                changes.add(new GroupScheduleChange(
                    newSchedule.getId(),
                    "DAY_CHANGE",
                    oldSchedule.getDayOfWeek(),
                    newSchedule.getDayOfWeek(),
                    countAffectedSchedules(groupId, oldSchedule.getDayOfWeekEnum())
                ));
            }
            
            // Check for time change
            if (!oldSchedule.getStartTime().equals(newSchedule.getStartTime()) ||
                !oldSchedule.getEndTime().equals(newSchedule.getEndTime())) {
                changes.add(new GroupScheduleChange(
                    newSchedule.getId(),
                    "TIME_CHANGE",
                    oldSchedule.getStartTime() + "-" + oldSchedule.getEndTime(),
                    newSchedule.getStartTime() + "-" + newSchedule.getEndTime(),
                    countAffectedSchedules(groupId, oldSchedule.getDayOfWeekEnum())
                ));
            }
            
            // Check for room change
            if (!oldSchedule.getRoom().getId().equals(newSchedule.getRoomId())) {
                changes.add(new GroupScheduleChange(
                    newSchedule.getId(),
                    "ROOM_CHANGE",
                    oldSchedule.getRoom().getName(),
                    getRoomName(newSchedule.getRoomId()),
                    countAffectedSchedules(groupId, oldSchedule.getDayOfWeekEnum())
                ));
            }
        }
    }
    
    return changes;
}
```

### 4. Cascade Service Implementation

```java
// GroupScheduleCascadeService.java
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GroupScheduleCascadeService {
    
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    
    public void cascadeChanges(Integer groupId, List<GroupScheduleChange> changes, CascadeOptions options) {
        for (GroupScheduleChange change : changes) {
            switch (change.getChangeType()) {
                case "DAY_CHANGE":
                    handleDayOfWeekChange(groupId, change);
                    break;
                case "TIME_CHANGE":
                    handleTimeChange(groupId, change);
                    break;
                case "ROOM_CHANGE":
                    handleRoomChange(groupId, change);
                    break;
            }
        }
    }
    
    private void handleDayOfWeekChange(Integer groupId, GroupScheduleChange change) {
        DayOfWeek oldDay = getDayOfWeek(change.getOldValue());
        DayOfWeek newDay = getDayOfWeek(change.getNewValue());
        
        // Find all future schedules for old day
        List<Schedule> oldSchedules = scheduleRepository.findFutureSchedulesByGroupAndDayOfWeek(
            groupId, oldDay, OffsetDateTime.now());
        
        // Delete old schedules and create new ones for new day
        List<Schedule> newSchedules = new ArrayList<>();
        for (Schedule oldSchedule : oldSchedules) {
            LocalDate oldDate = oldSchedule.getStartTime().toLocalDate();
            LocalDate newDate = convertDayOfWeek(oldDate, oldDay, newDay);
            
            if (newDate != null) {
                Schedule newSchedule = createScheduleForNewDay(oldSchedule, newDate);
                newSchedules.add(newSchedule);
            }
        }
        
        // Atomic operation: delete old, create new
        scheduleRepository.deleteAll(oldSchedules);
        scheduleRepository.saveAll(newSchedules);
        
        log.info("Day change cascade: {} old schedules deleted, {} new schedules created", 
                oldSchedules.size(), newSchedules.size());
    }
    
    private void handleTimeChange(Integer groupId, GroupScheduleChange change) {
        // Implementation for time updates
    }
    
    private void handleRoomChange(Integer groupId, GroupScheduleChange change) {
        // Implementation for room updates
    }
}
```

### 5. Frontend Integration Pattern

```javascript
// Frontend integration example
const updateGroupWithCascade = async (groupId, groupData) => {
  // Step 1: Check if GroupSchedules changed
  const hasScheduleChanges = detectGroupScheduleChanges(originalGroup, groupData);
  
  if (hasScheduleChanges) {
    // Step 2: Preview impact
    const previewResponse = await fetch(`/api/v1/group/${groupId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...groupData,
        cascade_options: {
          update_future_schedules: true,
          preview_only: true
        }
      })
    });
    
    const impact = await previewResponse.json();
    
    // Step 3: Show user confirmation
    if (impact.template_change_impact.totalChangesCount > 0) {
      const confirmed = showCascadeDialog(impact.template_change_impact);
      if (!confirmed) return;
    }
  }
  
  // Step 4: Apply actual update
  const updateResponse = await fetch(`/api/v1/group/${groupId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ...groupData,
      cascade_options: {
        update_future_schedules: hasScheduleChanges,
        preview_only: false
      }
    })
  });
  
  return updateResponse.json();
};
```

## 📝 Documentation Updates Required

### 1. Update test_bulk_schedule_generation.md
Add new section:
```markdown
## 5. GroupSchedule Template Cascade Updates ⏳

### Template-Level Change Propagation
- Edit Group containing GroupSchedule changes → all future schedules sync automatically
- Day changes: Monday → Tuesday converts all future Monday classes to Tuesday
- Time/Room changes: Update existing schedules in place
- Selective impact: Only changed day/time affected, other days unchanged

### Integration with Group Update
- Enhanced `PUT /api/v1/group/{id}` with cascade_options
- Preview mode shows impact before applying
- User confirmation for template changes
- Atomic operations with rollback support
```

### 2. Update BULK_SCHEDULE_API_DOCUMENTATION.md
Add comprehensive section with API examples, change type handling, and frontend integration patterns.

## 🚨 Important Notes for Next Session

### Priority Order
1. **Start with DTOs and enums** - Foundation for all other work
2. **Repository methods** - Database layer support
3. **Service layer logic** - Core cascade functionality  
4. **Controller integration** - API endpoint enhancement
5. **Testing and documentation** - Verify and document

### Key Technical Challenges
- **Date arithmetic** for day-of-week changes (Monday July 29 → Tuesday July 30)
- **Attendance data preservation** when schedules are moved/deleted
- **Conflict detection** for new schedule slots
- **Transaction management** for atomic Group + Schedule updates

### Testing Scenarios
- Day change: Monday → Tuesday for group with 10 future Monday classes
- Time change: 17:20-19:00 → 18:00-20:00 for all Wednesday classes
- Room change: Room A → Room B for all Friday classes
- Combined changes: Monday → Tuesday + time change + room change
- Conflict scenarios: New Tuesday slot conflicts with existing booking

This plan provides everything needed to implement the GroupSchedule template cascade functionality in the next session! 🚀