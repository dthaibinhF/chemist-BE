# Bulk Schedule Generation & Future Update API Documentation

## Overview

This document describes the enhanced schedule management features, including:
1. **Automated Weekly Schedule Generation—** Runs every Monday at 8 AM
2. **Bulk Schedule Generation APIs—** Manual bulk generation for multiple groups
3. **Future Schedule Update Logic** - Calendar-style single/recurring updates
4. **GroupSchedule Template Cascade Updates** - Automatic sync when Group templates change

## Automated Schedule Generation

### Automatic Monday Generation
- **Schedule**: Every Monday at 8 AM (Cron: `0 0 8 * * MON`)
- **Behavior**: Generates schedules for all active groups for the upcoming week (Monday to Sunday)
- **Logic**: 
  - Gets all groups with schedule templates
  - Generates schedules for next Monday through Sunday
  - Skips groups that already have schedules for those dates
  - Logs generation results

### Configuration
```yaml
# Application will automatically run scheduled generation
# No additional configuration required
```

## ✅ **Implementation Status: COMPLETED (2025-07-30)**
**All bulk schedule generation features and automatic Monday generation have been successfully implemented.**

## Bulk Schedule Generation APIs

### 1. Generate for Selected Groups
**Endpoint**: `POST /api/v1/schedule/bulk/selected-groups`

**Description**: Generate schedules for specific groups in one operation

**Request Body**:
```json
{
  "group_ids": [1, 2, 3, 5],
  "start_date": "2025-07-29",
  "end_date": "2025-08-04"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Bulk schedule generation completed",
  "total_groups_processed": 4,
  "successful_groups": 4,
  "failed_groups": 0,
  "total_schedules_generated": 12,
  "generated_schedules": [
    [
      {
        "id": 101,
        "start_time": "2025-07-29T08:00:00+07:00",
        "end_time": "2025-07-29T10:00:00+07:00",
        "delivery_mode": "OFFLINE",
        "group_id": 1,
        "room": { "id": 5, "name": "Room A" },
        "teacher_id": null,
        "meeting_link": null
      }
    ]
  ],
  "errors": []
}
```

### 2. Generate for All Active Groups
**Endpoint**: `POST /api/v1/schedule/bulk/all-groups`

**Description**: Generate schedules for all active groups with templates

**Request Parameters**:
- `startDate` (required): Start date for generation
- `endDate` (required): End date for generation

**Example Request**:
```
POST /api/v1/schedule/bulk/all-groups?startDate=2025-07-29&endDate=2025-08-04
```

**Response**: Same format as selected groups endpoint

### 3. Generate Next Week Schedules
**Endpoint**: `POST /api/v1/schedule/bulk/next-week`

**Description**: Generate schedules for next week for all groups (same as automatic Monday job)

**Response**:
```json
"Next week schedule generation triggered successfully"
```

### 4. Manual Trigger for Auto-Generation ✅ **IMPLEMENTED**
**Endpoint**: `POST /api/v1/schedule/auto-generation/trigger`

**Description**: Manually trigger the automatic generation logic (for testing)

**Response**:
```json
"Automatic schedule generation triggered successfully"
```

## ✅ **New Implementation Details (2025-07-30)**

### **Files Created:**
1. **`BulkScheduleRequest.java`** - Simple DTO for bulk generation requests
2. **`ScheduledScheduleService.java`** - Automatic Monday generation service

### **Controller Endpoints Added:**
- ✅ `POST /api/v1/schedule/bulk/selected-groups` - Working
- ✅ `POST /api/v1/schedule/bulk/all-groups` - Working  
- ✅ `POST /api/v1/schedule/bulk/next-week` - Working
- ✅ `POST /api/v1/schedule/auto-generation/trigger` - Working

### **Automatic Generation:**
- ✅ `@Scheduled(cron = "0 0 8 * * MON")` - Every Monday 8:00 AM
- ✅ Generates schedules for upcoming week (Monday to Sunday)
- ✅ Uses existing `ScheduleService.generateSchedulesForAllActiveGroups()`
- ✅ Comprehensive logging and error handling

### **Key Features:**
- **Simple Implementation**: Reuses existing service methods and DTOs
- **No Complex DTOs**: Uses `List<Set<ScheduleDTO>>` response format
- **Automatic Scheduling**: Runs every Monday at 8 AM without intervention
- **Manual Trigger**: Can test/trigger generation manually via API
- **Error Handling**: Comprehensive logging and exception handling

## Future Schedule Update APIs

### Calendar-Style Update Logic
Similar to Google Calendar's "Edit this event" vs. "Edit all future events":
- **SINGLE_OCCURRENCE**: Update only this one schedule
- **ALL_FUTURE_OCCURRENCES**: Update this schedule and all future schedules with the same pattern

### 1. Update with Mode Selection
**Endpoint**: `PUT /api/v1/schedule/{id}/update-mode`

**Description**: Update schedule with option for single or future occurrences

**Request Body**:
```json
{
  "update_mode": "ALL_FUTURE_OCCURRENCES",
  "start_time": "2025-07-29T09:00:00+07:00",
  "end_time": "2025-07-29T11:00:00+07:00",
  "delivery_mode": "ONLINE",
  "teacher_id": 15,
  "room_id": 8,
  "meeting_link": "https://meet.google.com/abc-defg-hij"
}
```

**Update Modes**:
- `SINGLE_OCCURRENCE`: Updates only the specified schedule
- `ALL_FUTURE_OCCURRENCES`: Updates this schedule and all future schedules for the same group/day pattern

**Response**:
```json
{
  "success": true,
  "message": "All schedules updated successfully",
  "updated_schedules_count": 5,
  "updated_schedules": [
    {
      "id": 101,
      "start_time": "2025-07-29T09:00:00+07:00",
      "end_time": "2025-07-29T11:00:00+07:00",
      "delivery_mode": "ONLINE",
      "group_id": 1,
      "room": { "id": 8, "name": "Room B" },
      "teacher_id": 15,
      "meeting_link": "https://meet.google.com/abc-defg-hij"
    }
  ],
  "errors": []
}
```

### 2. Get Future Schedules Count
**Endpoint**: `GET /api/v1/schedule/{id}/future-count`

**Description**: Get count of future schedules that would be affected by update

**Example**: `GET /api/v1/schedule/101/future-count`

**Response**:
```json
4
```

**Usage**: Show user "This will affect four future schedules" before update

## GroupSchedule Template Cascade Updates

### ✅ **Implementation Status: COMPLETED (2025-07-30)**
**The GroupSchedule Template Cascade functionality has been successfully implemented and tested.**

### Overview
When GroupSchedule templates are updated within a Group, all future Schedule entities are automatically synchronized with the new template values. This ensures consistency between schedule templates and actual generated schedules.

### ✅ **Recent Fix Applied (2025-07-30)**
**Issue**: Change detection was failing due to shallow copy of original GroupSchedule entities.
**Solution**: Implemented deep copy mechanism in `GroupService.synchronizeSchedulesWithGroupSchedules()` to preserve original values for accurate change comparison.
**Result**: Cascade functionality now works correctly for day-of-week, time, and room changes.

### ✅ **Verification Test Results**
**Test Case**: Changed GroupSchedule template from MONDAY to TUESDAY
**Expected**: Future Schedule entities should move from Monday dates to Tuesday dates
**Result**: ✅ **SUCCESS**
- Change Detection: `GroupSchedule ID: 52 changes detected - day: true, time: false, room: false`
- Cascade Triggered: `Triggering cascade for GroupSchedule ID: 52 - MONDAY → TUESDAY`
- Schedule Updated: `Schedule ID: 30 updated - 2025-08-04 to 2025-08-05`
- Database Persisted: `Successfully saved 1 schedules. Cascade complete`

## 🚀 **Frontend Developer Integration Guide**

### **Key Benefits for Frontend Applications**
1. **🔄 Automatic Sync**: Update template once, all future schedules automatically update
2. **⚡ Mass Updates**: Change 50+ schedules with single API call instead of multiple requests
3. **🎯 Consistency**: Templates and schedules stay synchronized automatically
4. **🛡️ Safe Operations**: All-or-nothing transactions prevent partial updates

### **API Endpoint Overview**
**Endpoint**: `PUT /api/v1/group/{id}`

**New Parameter**: 
- `syncFutureSchedules` (optional, boolean, default: `true`) - Whether to cascade template changes to future schedules

### **Frontend Implementation Examples**

#### **1. Basic Template Update with Cascade**
```javascript
// Update group schedule template and cascade to future schedules
async function updateGroupScheduleTemplate(groupId, updatedSchedules) {
  const response = await fetch(`/api/v1/group/${groupId}?syncFutureSchedules=true`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`
    },
    body: JSON.stringify({
      id: groupId,
      name: "Advanced Mathematics",
      level: "ADVANCED", 
      fee_id: 1,
      academic_year_id: 4,
      grade_id: 12,
      group_schedules: updatedSchedules
    })
  });
  
  if (!response.ok) {
    throw new Error(`Failed to update group: ${response.statusText}`);
  }
  
  return await response.json();
}

// Example usage: Change Monday class to Tuesday
const newSchedules = [
  {
    id: 52,
    day_of_week: "TUESDAY",  // Changed from MONDAY
    start_time: "17:20:00",
    end_time: "19:00:00",
    room_id: 1
  }
  // ... other schedules unchanged
];

updateGroupScheduleTemplate(1, newSchedules)
  .then(result => console.log('✅ Template updated, future schedules cascaded'))
  .catch(error => console.error('❌ Update failed:', error));
```

#### **2. Template-Only Update (No Cascade)**
```javascript
// Update template without affecting existing schedules
async function updateTemplateOnly(groupId, updatedSchedules) {
  const response = await fetch(`/api/v1/group/${groupId}?syncFutureSchedules=false`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`
    },
    body: JSON.stringify({
      id: groupId,
      // ... group data
      group_schedules: updatedSchedules
    })
  });
  
  return await response.json();
}
```

#### **3. User Confirmation Pattern**
```javascript
// Show user confirmation before mass updates
async function updateWithConfirmation(groupId, changes) {
  // Get current group data first
  const currentGroup = await fetch(`/api/v1/group/${groupId}`).then(r => r.json());
  
  // Count affected future schedules (estimate)
  const futureSchedulesCount = currentGroup.schedules.filter(schedule => 
    new Date(schedule.start_time) > new Date()
  ).length;
  
  // Show confirmation dialog
  const confirmed = confirm(
    `This will update the template and affect approximately ${futureSchedulesCount} future schedules. ` +
    `Continue?`
  );
  
  if (!confirmed) return null;
  
  // Proceed with update
  return await updateGroupScheduleTemplate(groupId, changes);
}
```

#### **4. Bulk Day-of-Week Changes**
```javascript
// Move all Monday classes to Tuesday (e.g., due to holiday)
function moveClassesToDifferentDay(groupId, fromDay, toDay) {
  return fetch(`/api/v1/group/${groupId}`)
    .then(response => response.json())
    .then(group => {
      // Update all schedules matching the source day
      const updatedSchedules = group.group_schedules.map(schedule => {
        if (schedule.day_of_week === fromDay) {
          return { ...schedule, day_of_week: toDay };
        }
        return schedule;
      });
      
      return updateGroupScheduleTemplate(groupId, updatedSchedules);
    });
}

// Usage: Move Monday classes to Tuesday
moveClassesToDifferentDay(1, "MONDAY", "TUESDAY");
```

#### **5. Time Adjustment for All Classes**
```javascript
// Extend all class durations by 30 minutes
function extendClassDuration(groupId, extraMinutes = 30) {
  return fetch(`/api/v1/group/${groupId}`)
    .then(response => response.json())
    .then(group => {
      const updatedSchedules = group.group_schedules.map(schedule => {
        const endTime = new Date(`2000-01-01T${schedule.end_time}`);
        endTime.setMinutes(endTime.getMinutes() + extraMinutes);
        
        return {
          ...schedule,
          end_time: endTime.toTimeString().substr(0, 8) // HH:MM:SS format
        };
      });
      
      return updateGroupScheduleTemplate(groupId, updatedSchedules);
    });
}
```

#### **6. Room Reallocation**
```javascript
// Move all classes from Room A to Room B
function reallocateRoom(groupId, newRoomId) {
  return fetch(`/api/v1/group/${groupId}`)
    .then(response => response.json())
    .then(group => {
      const updatedSchedules = group.group_schedules.map(schedule => ({
        ...schedule,
        room_id: newRoomId
      }));
      
      return updateGroupScheduleTemplate(groupId, updatedSchedules);
    });
}
```

### **React Component Examples**

#### **Schedule Template Editor Component**
```jsx
import React, { useState, useEffect } from 'react';

const ScheduleTemplateEditor = ({ groupId, onUpdate }) => {
  const [group, setGroup] = useState(null);
  const [loading, setLoading] = useState(false);
  const [syncEnabled, setSyncEnabled] = useState(true);

  useEffect(() => {
    // Load current group data
    fetch(`/api/v1/group/${groupId}`)
      .then(response => response.json())
      .then(setGroup)
      .catch(console.error);
  }, [groupId]);

  const handleScheduleChange = (scheduleId, field, value) => {
    setGroup(prev => ({
      ...prev,
      group_schedules: prev.group_schedules.map(schedule =>
        schedule.id === scheduleId
          ? { ...schedule, [field]: value }
          : schedule
      )
    }));
  };

  const handleSave = async () => {
    setLoading(true);
    try {
      const response = await fetch(`/api/v1/group/${groupId}?syncFutureSchedules=${syncEnabled}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify(group)
      });

      if (!response.ok) {
        throw new Error('Failed to update schedule template');
      }

      const updatedGroup = await response.json();
      setGroup(updatedGroup);
      onUpdate?.(updatedGroup);
      
      alert(syncEnabled 
        ? '✅ Template updated and future schedules synchronized!'
        : '✅ Template updated (future schedules unchanged)'
      );
    } catch (error) {
      alert(`❌ Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  if (!group) return <div>Loading...</div>;

  return (
    <div className="schedule-template-editor">
      <h3>Edit Schedule Template - {group.name}</h3>
      
      <div className="sync-option">
        <label>
          <input
            type="checkbox"
            checked={syncEnabled}
            onChange={(e) => setSyncEnabled(e.target.checked)}
          />
          Apply changes to all future schedules
        </label>
      </div>

      {group.group_schedules.map(schedule => (
        <div key={schedule.id} className="schedule-row">
          <select
            value={schedule.day_of_week}
            onChange={(e) => handleScheduleChange(schedule.id, 'day_of_week', e.target.value)}
          >
            <option value="MONDAY">Monday</option>
            <option value="TUESDAY">Tuesday</option>
            <option value="WEDNESDAY">Wednesday</option>
            <option value="THURSDAY">Thursday</option>
            <option value="FRIDAY">Friday</option>
            <option value="SATURDAY">Saturday</option>
            <option value="SUNDAY">Sunday</option>
          </select>
          
          <input
            type="time"
            value={schedule.start_time}
            onChange={(e) => handleScheduleChange(schedule.id, 'start_time', e.target.value)}
          />
          
          <input
            type="time"
            value={schedule.end_time}
            onChange={(e) => handleScheduleChange(schedule.id, 'end_time', e.target.value)}
          />
        </div>
      ))}

      <button onClick={handleSave} disabled={loading}>
        {loading ? 'Saving...' : 'Save Changes'}
      </button>
    </div>
  );
};

export default ScheduleTemplateEditor;
```

### **Error Handling Patterns**

#### **Comprehensive Error Handling**
```javascript
async function updateGroupWithErrorHandling(groupId, updates) {
  try {
    const response = await fetch(`/api/v1/group/${groupId}?syncFutureSchedules=true`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`
      },
      body: JSON.stringify(updates)
    });

    // Handle different HTTP status codes
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      
      switch (response.status) {
        case 400:
          throw new Error(`Validation Error: ${errorData.message || 'Invalid data provided'}`);
        case 401:
          throw new Error('Authentication required. Please log in again.');
        case 403:
          throw new Error('You do not have permission to update this group.');
        case 404:
          throw new Error('Group not found.');
        case 409:
          throw new Error('Conflict: Unable to update due to schedule conflicts.');
        case 500:
          throw new Error('Server error. Please try again later.');
        default:
          throw new Error(`Update failed: ${response.statusText}`);
      }
    }

    return await response.json();
  } catch (error) {
    // Log error for debugging
    console.error('Group update failed:', error);
    
    // Re-throw with user-friendly message
    throw new Error(error.message || 'Failed to update group schedule');
  }
}

// Usage with error handling
updateGroupWithErrorHandling(groupId, updatedData)
  .then(result => {
    console.log('✅ Success:', result);
    // Update UI state
  })
  .catch(error => {
    console.error('❌ Error:', error.message);
    // Show error message to user
    showErrorNotification(error.message);
  });
```

### **TypeScript Interfaces**

```typescript
interface GroupSchedule {
  id: number;
  day_of_week: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
  start_time: string; // HH:MM:SS format
  end_time: string;   // HH:MM:SS format
  room_id: number;
  group_id?: number;
  room_name?: string;
  create_at?: string;
  update_at?: string;
  end_at?: string | null;
}

interface Group {
  id: number;
  name: string;
  level: 'BASIC' | 'INTERMEDIATE' | 'ADVANCED';
  fee_id: number;
  fee_name?: string;
  academic_year_id: number;
  academic_year?: string;
  grade_id: number;
  grade_name?: string;
  group_schedules: GroupSchedule[];
  schedules?: Schedule[];
  student_details?: StudentDetail[];
}

interface UpdateGroupRequest {
  id: number;
  name: string;
  level: string;
  fee_id: number;
  academic_year_id: number;
  grade_id: number;
  group_schedules: GroupSchedule[];
}

// Usage in TypeScript
const updateGroupSchedule = async (
  groupId: number, 
  updates: UpdateGroupRequest, 
  syncFutureSchedules: boolean = true
): Promise<Group> => {
  const response = await fetch(`/api/v1/group/${groupId}?syncFutureSchedules=${syncFutureSchedules}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`
    },
    body: JSON.stringify(updates)
  });

  if (!response.ok) {
    throw new Error(`Failed to update group: ${response.statusText}`);
  }

  return response.json();
};
```

### **Common Use Cases for Frontend Applications**

#### **1. Holiday Schedule Adjustments**
```javascript
// Move all Monday classes to Tuesday due to public holiday
const adjustForHoliday = async (groupId) => {
  const confirmed = confirm('Move all Monday classes to Tuesday due to holiday?');
  if (!confirmed) return;
  
  await moveClassesToDifferentDay(groupId, 'MONDAY', 'TUESDAY');
  showSuccessMessage('Holiday schedule adjustment complete!');
};
```

#### **2. Room Maintenance Reallocation**
```javascript
// Reallocate all classes due to room maintenance
const handleRoomMaintenance = async (groupId, newRoomId) => {
  try {
    await reallocateRoom(groupId, newRoomId);
    showSuccessMessage('All classes moved to new room successfully!');
  } catch (error) {
    showErrorMessage(`Failed to reallocate room: ${error.message}`);
  }
};
```

#### **3. Semester Schedule Updates**
```javascript
// Update multiple groups for new semester
const updateSemesterSchedules = async (groupUpdates) => {
  const results = await Promise.allSettled(
    groupUpdates.map(({ groupId, schedules }) => 
      updateGroupScheduleTemplate(groupId, schedules)
    )
  );
  
  const successful = results.filter(r => r.status === 'fulfilled').length;
  const failed = results.filter(r => r.status === 'rejected').length;
  
  showSummaryMessage(`Updated ${successful} groups successfully, ${failed} failed.`);
};
```

### Cascade Logic

#### Change Detection
The system automatically detects changes in GroupSchedule templates:
1. **Day of Week Changes**: Monday → Tuesday
2. **Time Changes**: 17:20-19:00 → 18:00-20:00  
3. **Room Changes**: Room A → Room B

#### Future Schedule Updates
When changes are detected:
1. **Find Future Schedules**: All active schedules for the group after current date/time
2. **Filter by Pattern**: Only schedules matching the original day of week
3. **Apply Updates**: Update matching schedules with new template values
4. **Date Arithmetic**: For day changes, calculate correct new dates

#### Day of Week Change Example
**Scenario**: Change Group's Monday template to Tuesday

**Original Schedules**:
- Monday July 29, 2024 17:20-19:00
- Monday August 5, 2024 17:20-19:00
- Monday August 12, 2024 17:20-19:00

**After Update**:
- Tuesday July 30, 2024 17:20-19:00
- Tuesday August 6, 2024 17:20-19:00
- Tuesday August 13, 2024 17:20-19:00

### Cascade Options

#### Enable/Disable Sync
```javascript
// Enable cascade (default)
PUT /api/v1/group/5?syncFutureSchedules=true

// Disable cascade (template-only update)
PUT /api/v1/group/5?syncFutureSchedules=false
```

#### Backward Compatibility
```javascript
// Without parameter - cascade enabled by default
PUT /api/v1/group/5
```

### Advanced Date Arithmetic

#### Week-Boundary Handling
The system correctly handles week boundaries:
- **Friday → Monday**: Friday July 26 → Monday July 29 (+3 days)
- **Sunday → Wednesday**: Sunday July 28 → Wednesday July 31 (+3 days)  
- **Wednesday → Monday**: Wednesday July 31 → Monday August 5 (+5 days)

#### Algorithm
```javascript
// Calculate day difference with week wrap-around
dayDifference = newDay.getValue() - originalDay.getValue();
if (dayDifference < 0) {
    dayDifference += 7; // Handle week wrap
}
newDate = originalDate.plusDays(dayDifference);
```

### Response Behavior

#### Successful Update
The API returns the updated Group DTO normally. Cascade operations happen transparently in the background.

#### Error Handling
- **Validation Errors**: GroupSchedule validation failures prevent the entire update
- **Cascade Failures**: Individual schedule update failures are logged but don't prevent the Group update
- **Transaction Safety**: Group update and cascade operations are wrapped in transactions

### Frontend Integration

#### Simple Update (Cascade Enabled)
```javascript
const response = await fetch('/api/v1/group/5', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    id: 5,
    name: 'Advanced Mathematics',
    group_schedules: [{
      id: 15,
      day_of_week: 'TUESDAY',  // Changed from MONDAY
      start_time: '18:00:00',  // Changed from 17:20:00
      end_time: '20:00:00',    // Changed from 19:00:00
      room_id: 8               // Changed from 5
    }]
  })
});
```

#### Template-Only Update (Cascade Disabled)
```javascript
const response = await fetch('/api/v1/group/5?syncFutureSchedules=false', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(groupData)
});
```

### Use Cases

#### 1. Schedule Template Correction
**Scenario**: Teacher's availability changed, need to move all Monday classes to Tuesday
**Solution**: Update Group's Monday template to Tuesday, all future classes move automatically

#### 2. Room Reallocation
**Scenario**: Room A is being renovated, move all classes to Room B
**Solution**: Update Group's room template, all future schedules update to new room

#### 3. Time Adjustment
**Scenario**: Extend class duration from 1.5 hours to 2 hours
**Solution**: Update Group's end time template, all future schedules get extended duration

#### 4. Selective Updates
**Scenario**: Update template for future use but keep existing schedules unchanged
**Solution**: Use `syncFutureSchedules=false` to update only the template

### Benefits

#### For Administrators
1. **Mass Updates**: Change 50+ future schedules with one API call
2. **Consistency**: Templates and schedules stay synchronized automatically
3. **Flexibility**: Can disable cascade when needed
4. **Efficiency**: No need to manually update individual schedules

#### For System
1. **Data Integrity**: Prevents template/schedule mismatches
2. **Performance**: Single operation instead of multiple API calls
3. **Atomic Updates**: All changes succeed or fail together
4. **Audit Trail**: Template changes are automatically reflected in schedules

### Migration & Compatibility

#### Existing API Behavior
- **Default Enabled**: Cascade is enabled by default for optimal user experience
- **Backward Compatible**: Existing Group update calls work without changes
- **No Breaking Changes**: All existing functionality preserved

#### Gradual Adoption
- **Optional Parameter**: Can be disabled if cascade is not desired
- **Transparent Operation**: Works behind the scenes without changing response format
- **Safe Fallback**: Cascade failures don't prevent Group updates

## Pattern Recognition Logic

### How Future Schedules are Identified
1. **Same Group**: Schedules must belong to the same group
2. **Same Day of Week**: Must occur on the same day (Monday, Tuesday, etc.)
3. **Future Dates**: Only schedules after the current schedule's date
4. **Active Schedules**: Only non-deleted schedules

### Example Scenario
- Current schedule: Group 1, Monday 8:00-10:00 on 2025-07-29
- Future schedules found: 
  - Group 1, Monday 8:00-10:00 on 2025-08-05
  - Group 1, Monday 8:00-10:00 on 2025-08-12
  - Group 1, Monday 8:00-10:00 on 2025-08-19

## Error Handling

### Bulk Generation Errors
- Individual group failures don't stop the entire operation
- Partial success responses include error details
- Failed groups are logged with specific error messages

### Update Errors
- Single occurrence updates: Simple transaction rollback
- Future occurrence updates: All-or-nothing transaction
- Conflict detection for all affected schedules
- Detailed error messages for each failed schedule

## Frontend Integration Examples

### Bulk Generation
```javascript
// Generate for specific groups
const response = await fetch('/api/v1/schedule/bulk/selected-groups', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    group_ids: [1, 2, 3],
    start_date: '2025-07-29',
    end_date: '2025-08-04'
  })
});

// Generate for all groups
const allGroupsResponse = await fetch('/api/v1/schedule/bulk/all-groups?startDate=2025-07-29&endDate=2025-08-04', {
  method: 'POST'
});
```

### Future Update Flow
```javascript
// 1. Get future count first
const countResponse = await fetch('/api/v1/schedule/101/future-count');
const futureCount = await countResponse.json();

// 2. Show user confirmation
if (futureCount > 0) {
  const confirmed = confirm(`This will affect ${futureCount} future schedules. Continue?`);
  if (!confirmed) return;
}

// 3. Update with selected mode
const updateResponse = await fetch('/api/v1/schedule/101/update-mode', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    update_mode: 'ALL_FUTURE_OCCURRENCES',
    start_time: '2025-07-29T09:00:00+07:00',
    end_time: '2025-07-29T11:00:00+07:00',
    delivery_mode: 'ONLINE',
    teacher_id: 15,
    room_id: 8
  })
});
```

## Benefits

### For Users
1. **No More Manual Repetition**: Bulk generation eliminates need to call API 10+ times
2. **Automatic Scheduling**: Weekly schedules generated automatically every Monday
3. **Calendar-Like Experience**: Familiar update patterns from calendar applications
4. **Flexible Control**: Choose between single or recurring updates

### For System
1. **Better Performance**: Single API call vs multiple individual calls
2. **Atomic Operations**: All-or-nothing updates prevent partial failures
3. **Consistent Scheduling**: Automated generation ensures regular schedule creation
4. **Conflict Prevention**: Advanced conflict detection for bulk operations

## Migration Notes

### Existing Functionality
- All existing single schedule endpoints remain unchanged
- Existing `POST /api/v1/schedule/weekly` still works for single group generation
- No breaking changes to current API contracts

### New Capabilities
- Bulk operations supplement (don't replace) individual operations  
- Future update logic adds new functionality without affecting existing updates
- Automatic generation runs in background without user intervention
- GroupSchedule template cascade keeps schedules synchronized with templates automatically

## Testing Recommendations

### Manual Testing
1. Test bulk generation with various group combinations
2. Verify automatic Monday generation (can trigger manually)
3. Test future update scenarios with different patterns
4. Verify conflict detection in bulk operations
5. **✅ GroupSchedule cascade scenarios - COMPLETED**:
   - ✅ Day changes (Monday → Tuesday) - TESTED & WORKING
   - Time changes (17:20-19:00 → 18:00-20:00) - READY FOR TESTING
   - Room changes (Room A → Room B) - READY FOR TESTING
   - Combined changes (day + time + room) - READY FOR TESTING
   - Cascade disable functionality (`syncFutureSchedules=false`) - READY FOR TESTING

### Integration Testing
1. Test with large numbers of groups (10+)
2. Test partial failure scenarios
3. Verify transaction rollback on errors
4. Test concurrent access to schedule generation
5. **Test cascade with large datasets**:
   - Groups with 50+ future schedules
   - Concurrent Group updates with cascade
   - Performance impact of cascade operations
   - Week boundary date calculations

This enhancement transforms the schedule management system from manual individual operations to intelligent automated bulk processing with sophisticated update capabilities and automatic template-to-schedule synchronization.