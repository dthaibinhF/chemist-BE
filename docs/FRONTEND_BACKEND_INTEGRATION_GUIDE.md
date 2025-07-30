# Frontend-Backend Integration Guide for Bulk Schedule Management

## Overview

This guide provides comprehensive integration instructions for implementing the new bulk schedule generation and future update features in your frontend application.

## Authentication

All API calls require JWT authentication in the Authorization header:

```javascript
const headers = {
  'Authorization': `Bearer ${accessToken}`,
  'Content-Type': 'application/json'
};
```

## 1. Bulk Schedule Generation Integration

### 1.1 Generate Schedules for Selected Groups

Replace multiple individual API calls with a single bulk operation:

#### Before (Old Approach—DON'T USE)
```javascript
// ❌ Old way - multiple API calls
const generateSchedulesOldWay = async (groupIds, startDate, endDate) => {
  const results = [];
  for (const groupId of groupIds) {
    try {
      const response = await fetch(`/api/v1/schedule/weekly?groupId=${groupId}&startDate=${startDate}&endDate=${endDate}`, {
        method: 'POST',
        headers
      });
      results.push(await response.json());
    } catch (error) {
      console.error(`Failed for group ${groupId}:`, error);
    }
  }
  return results;
};
```

#### After (New Approach—USE THIS)
```javascript
// ✅ New way - single bulk API call
const generateBulkSchedules = async (groupIds, startDate, endDate) => {
  try {
    const response = await fetch('/api/v1/schedule/bulk/selected-groups', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        group_ids: groupIds,
        start_date: startDate,
        end_date: endDate
      })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Bulk generation failed:', error);
    throw error;
  }
};

// Usage example
const handleBulkGeneration = async () => {
  const selectedGroupIds = [1, 2, 3, 4, 5];
  const startDate = '2025-07-29';
  const endDate = '2025-08-05';

  try {
    const result = await generateBulkSchedules(selectedGroupIds, startDate, endDate);
    
    console.log(`✅ Success: ${result.total_schedules_generated} schedules generated`);
    console.log(`📊 Groups processed: ${result.successful_groups}/${result.total_groups_processed}`);
    
    if (result.failed_groups > 0) {
      console.warn(`⚠️ ${result.failed_groups} groups failed:`, result.errors);
    }
    
    // Update UI with generated schedules
    updateScheduleDisplay(result.generated_schedules);
  } catch (error) {
    showErrorMessage('Failed to generate schedules. Please try again.');
  }
};
```

### 1.2 Generate for All Active Groups

```javascript
const generateForAllGroups = async (startDate, endDate) => {
  try {
    const response = await fetch(`/api/v1/schedule/bulk/all-groups?startDate=${startDate}&endDate=${endDate}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('All groups generation failed:', error);
    throw error;
  }
};
```

### 1.3 Generate Next Week (Automatic)

```javascript
const generateNextWeek = async () => {
  try {
    const response = await fetch('/api/v1/schedule/bulk/next-week', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });

    const message = await response.text();
    console.log('✅', message);
    return message;
  } catch (error) {
    console.error('Next week generation failed:', error);
    throw error;
  }
};
```

## 2. Calendar-Style Schedule Updates

### 2.1 Check Future Impact Before Update

Always check how many future schedules will be affected:

```javascript
const getFutureSchedulesCount = async (scheduleId) => {
  try {
    const response = await fetch(`/api/v1/schedule/${scheduleId}/future-count`, {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });

    const count = await response.json();
    return count;
  } catch (error) {
    console.error('Failed to get future count:', error);
    return 0;
  }
};
```

### 2.2 Update Schedule with Mode Selection

Implement Google Calendar-style update dialog:

```javascript
const updateScheduleWithMode = async (scheduleId, updates) => {
  // 1. Get future schedules count
  const futureCount = await getFutureSchedulesCount(scheduleId);
  
  // 2. Show user choice dialog
  let updateMode = 'SINGLE_OCCURRENCE';
  
  if (futureCount > 0) {
    const updateAll = await showUpdateDialog(futureCount);
    updateMode = updateAll ? 'ALL_FUTURE_OCCURRENCES' : 'SINGLE_OCCURRENCE';
  }
  
  // 3. Perform update
  try {
    const response = await fetch(`/api/v1/schedule/${scheduleId}/update-mode`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        update_mode: updateMode,
        start_time: updates.startTime,
        end_time: updates.endTime,
        delivery_mode: updates.deliveryMode,
        teacher_id: updates.teacherId,
        room_id: updates.roomId,
        meeting_link: updates.meetingLink
      })
    });

    const result = await response.json();
    
    if (result.success) {
      console.log(`✅ Updated ${result.updated_schedules_count} schedules`);
      return result;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Update failed:', error);
    throw error;
  }
};

// User dialog component
const showUpdateDialog = (futureCount) => {
  return new Promise((resolve) => {
    // Example using native confirm (replace with your UI library)
    const message = `This schedule repeats ${futureCount} times in the future.\n\nDo you want to update:\n- Just this occurrence (Cancel)\n- This and all future occurrences (OK)`;
    const updateAll = confirm(message);
    resolve(updateAll);
  });
};

// React/Vue component example
const ScheduleUpdateDialog = ({ futureCount, onConfirm, onCancel }) => {
  return (
    <div className="modal">
      <h3>Update Recurring Schedule</h3>
      <p>This schedule repeats <strong>{futureCount}</strong> times in the future.</p>
      <p>What would you like to update?</p>
      
      <div className="buttons">
        <button onClick={() => onConfirm('SINGLE_OCCURRENCE')}>
          Just this occurrence
        </button>
        <button onClick={() => onConfirm('ALL_FUTURE_OCCURRENCES')} className="primary">
          This and all future occurrences
        </button>
        <button onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
};
```

## 3. UI Component Examples

### 3.1 Bulk Generation Component (React)

```jsx
import React, { useState } from 'react';

const BulkScheduleGenerator = ({ groups, onComplete }) => {
  const [selectedGroups, setSelectedGroups] = useState([]);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleGenerate = async () => {
    setLoading(true);
    try {
      const result = await generateBulkSchedules(selectedGroups, startDate, endDate);
      setResult(result);
      onComplete(result);
    } catch (error) {
      setResult({ success: false, message: error.message });
    } finally {
      setLoading(false);
    }
  };

  const handleSelectAll = () => {
    setSelectedGroups(groups.map(g => g.id));
  };

  return (
    <div className="bulk-generator">
      <h3>Generate Schedules for Multiple Groups</h3>
      
      {/* Group Selection */}
      <div className="group-selection">
        <div className="controls">
          <button onClick={handleSelectAll}>Select All</button>
          <span>{selectedGroups.length} of {groups.length} groups selected</span>
        </div>
        
        <div className="group-list">
          {groups.map(group => (
            <label key={group.id}>
              <input
                type="checkbox"
                checked={selectedGroups.includes(group.id)}
                onChange={(e) => {
                  if (e.target.checked) {
                    setSelectedGroups([...selectedGroups, group.id]);
                  } else {
                    setSelectedGroups(selectedGroups.filter(id => id !== group.id));
                  }
                }}
              />
              {group.name}
            </label>
          ))}
        </div>
      </div>

      {/* Date Range */}
      <div className="date-range">
        <label>
          Start Date:
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
        </label>
        <label>
          End Date:
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
        </label>
      </div>

      {/* Generate Button */}
      <button
        onClick={handleGenerate}
        disabled={loading || selectedGroups.length === 0 || !startDate || !endDate}
        className="generate-btn"
      >
        {loading ? 'Generating...' : `Generate Schedules for ${selectedGroups.length} Groups`}
      </button>

      {/* Results */}
      {result && (
        <div className={`result ${result.success ? 'success' : 'error'}`}>
          {result.success ? (
            <div>
              <p>✅ Successfully generated {result.total_schedules_generated} schedules</p>
              <p>📊 {result.successful_groups}/{result.total_groups_processed} groups processed</p>
              {result.failed_groups > 0 && (
                <details>
                  <summary>⚠️ {result.failed_groups} groups failed</summary>
                  <ul>
                    {result.errors.map((error, index) => (
                      <li key={index}>{error}</li>
                    ))}
                  </ul>
                </details>
              )}
            </div>
          ) : (
            <p>❌ {result.message}</p>
          )}
        </div>
      )}
    </div>
  );
};
```

### 3.2 Schedule Update Component (React)

```jsx
import React, { useState } from 'react';

const ScheduleEditor = ({ schedule, onUpdate, onCancel }) => {
  const [formData, setFormData] = useState({
    startTime: schedule.start_time,
    endTime: schedule.end_time,
    deliveryMode: schedule.delivery_mode,
    teacherId: schedule.teacher_id,
    roomId: schedule.room.id,
    meetingLink: schedule.meeting_link
  });
  const [showUpdateDialog, setShowUpdateDialog] = useState(false);
  const [futureCount, setFutureCount] = useState(0);

  const handleSubmit = async () => {
    // Check future impact
    const count = await getFutureSchedulesCount(schedule.id);
    
    if (count > 0) {
      setFutureCount(count);
      setShowUpdateDialog(true);
    } else {
      // No future schedules, update directly
      await performUpdate('SINGLE_OCCURRENCE');
    }
  };

  const performUpdate = async (updateMode) => {
    try {
      const result = await updateScheduleWithMode(schedule.id, {
        ...formData,
        updateMode
      });
      onUpdate(result);
    } catch (error) {
      console.error('Update failed:', error);
    }
    setShowUpdateDialog(false);
  };

  return (
    <div className="schedule-editor">
      <h3>Edit Schedule</h3>
      
      {/* Form fields */}
      <div className="form-group">
        <label>Start Time:</label>
        <input
          type="datetime-local"
          value={formData.startTime?.slice(0, 16)}
          onChange={(e) => setFormData({...formData, startTime: e.target.value + '+07:00'})}
        />
      </div>

      <div className="form-group">
        <label>End Time:</label>
        <input
          type="datetime-local"
          value={formData.endTime?.slice(0, 16)}
          onChange={(e) => setFormData({...formData, endTime: e.target.value + '+07:00'})}
        />
      </div>

      <div className="form-group">
        <label>Delivery Mode:</label>
        <select
          value={formData.deliveryMode}
          onChange={(e) => setFormData({...formData, deliveryMode: e.target.value})}
        >
          <option value="OFFLINE">Offline</option>
          <option value="ONLINE">Online</option>
          <option value="HYBRID">Hybrid</option>
        </select>
      </div>

      {/* Action buttons */}
      <div className="actions">
        <button onClick={handleSubmit} className="primary">Update Schedule</button>
        <button onClick={onCancel}>Cancel</button>
      </div>

      {/* Update mode dialog */}
      {showUpdateDialog && (
        <div className="modal-overlay">
          <div className="modal">
            <h4>Update Recurring Schedule</h4>
            <p>This schedule repeats <strong>{futureCount}</strong> times in the future.</p>
            <p>What would you like to update?</p>
            
            <div className="modal-actions">
              <button onClick={() => performUpdate('SINGLE_OCCURRENCE')}>
                Just this occurrence
              </button>
              <button onClick={() => performUpdate('ALL_FUTURE_OCCURRENCES')} className="primary">
                This and all future occurrences
              </button>
              <button onClick={() => setShowUpdateDialog(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
```

## 4. Error Handling Best Practices

### 4.1 HTTP Status Code Handling

```javascript
const handleApiResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    
    switch (response.status) {
      case 400:
        throw new Error(errorData.message || 'Invalid request data');
      case 401:
        // Redirect to login
        window.location.href = '/login';
        break;
      case 403:
        throw new Error('Access denied. Check your permissions.');
      case 404:
        throw new Error('Schedule or group not found');
      case 409:
        throw new Error('Schedule conflict detected');
      case 500:
        throw new Error('Server error. Please try again later.');
      default:
        throw new Error(`Unexpected error: ${response.status}`);
    }
  }
  
  return response.json();
};
```

### 4.2 Bulk Operation Error Handling

```javascript
const handleBulkResult = (result) => {
  if (result.success) {
    // All successful
    showSuccessMessage(`Generated ${result.total_schedules_generated} schedules successfully`);
  } else if (result.successful_groups > 0) {
    // Partial success
    showWarningMessage(
      `Partially successful: ${result.successful_groups}/${result.total_groups_processed} groups completed. ` +
      `${result.failed_groups} groups failed.`
    );
    
    // Show detailed errors
    if (result.errors.length > 0) {
      console.group('Failed Groups:');
      result.errors.forEach(error => console.error(error));
      console.groupEnd();
    }
  } else {
    // Complete failure
    showErrorMessage('Schedule generation failed completely. Please check the logs.');
  }
};
```

## 5. Performance Optimizations

### 5.1 Debounced Date Range Updates

```javascript
import { debounce } from 'lodash';

const ScheduleRangePicker = ({ onRangeChange }) => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Debounce range updates to avoid excessive API calls
  const debouncedRangeChange = debounce((start, end) => {
    if (start && end && start <= end) {
      onRangeChange(start, end);
    }
  }, 500);

  useEffect(() => {
    debouncedRangeChange(startDate, endDate);
  }, [startDate, endDate]);

  return (
    <div className="date-range-picker">
      <input
        type="date"
        value={startDate}
        onChange={(e) => setStartDate(e.target.value)}
        placeholder="Start Date"
      />
      <input
        type="date"
        value={endDate}
        onChange={(e) => setEndDate(e.target.value)}
        placeholder="End Date"
      />
    </div>
  );
};
```

### 5.2 Loading States and Progress

```javascript
const BulkOperationProgress = ({ groups, onComplete }) => {
  const [progress, setProgress] = useState({ current: 0, total: 0, status: 'idle' });

  const generateWithProgress = async (groupIds, startDate, endDate) => {
    setProgress({ current: 0, total: groupIds.length, status: 'running' });

    try {
      // Use bulk API (much faster than individual calls)
      const result = await generateBulkSchedules(groupIds, startDate, endDate);
      
      setProgress({ 
        current: result.successful_groups, 
        total: result.total_groups_processed, 
        status: 'completed' 
      });
      
      onComplete(result);
    } catch (error) {
      setProgress(prev => ({ ...prev, status: 'error' }));
      throw error;
    }
  };

  return (
    <div className="progress-container">
      {progress.status === 'running' && (
        <div className="progress-bar">
          <div 
            className="progress-fill" 
            style={{ width: `${(progress.current / progress.total) * 100}%` }}
          />
          <span className="progress-text">
            Processing {progress.current}/{progress.total} groups...
          </span>
        </div>
      )}
    </div>
  );
};
```

## 6. Testing Integration

### 6.1 Unit Tests for API Functions

```javascript
// Jest tests
describe('Bulk Schedule API', () => {
  beforeEach(() => {
    fetch.resetMocks();
  });

  test('generateBulkSchedules success', async () => {
    const mockResponse = {
      success: true,
      total_schedules_generated: 15,
      successful_groups: 3,
      failed_groups: 0
    };

    fetch.mockResponseOnce(JSON.stringify(mockResponse));

    const result = await generateBulkSchedules([1, 2, 3], '2025-07-29', '2025-08-05');

    expect(fetch).toHaveBeenCalledWith('/api/v1/schedule/bulk/selected-groups', {
      method: 'POST',
      headers: expect.objectContaining({
        'Content-Type': 'application/json'
      }),
      body: JSON.stringify({
        group_ids: [1, 2, 3],
        start_date: '2025-07-29',
        end_date: '2025-08-05'
      })
    });

    expect(result.total_schedules_generated).toBe(15);
  });

  test('updateScheduleWithMode future count check', async () => {
    fetch
      .mockResponseOnce('5') // future count
      .mockResponseOnce(JSON.stringify({ success: true, updated_schedules_count: 6 })); // update result

    const result = await updateScheduleWithMode(1, { startTime: '2025-07-29T09:00:00+07:00' });

    expect(fetch).toHaveBeenCalledTimes(2);
    expect(result.updated_schedules_count).toBe(6);
  });
});
```

## 7. Migration Guide

### 7.1 Updating Existing Code

```javascript
// ❌ OLD: Multiple API calls
const oldGenerateSchedules = async (groups) => {
  const promises = groups.map(group => 
    fetch(`/api/v1/schedule/weekly?groupId=${group.id}&startDate=${startDate}&endDate=${endDate}`, {
      method: 'POST'
    })
  );
  return Promise.all(promises);
};

// ✅ NEW: Single bulk API call
const newGenerateSchedules = async (groups, startDate, endDate) => {
  const groupIds = groups.map(g => g.id);
  return generateBulkSchedules(groupIds, startDate, endDate);
};
```

### 7.2 Backward Compatibility

The old individual endpoints still work, so you can migrate gradually:

```javascript
const generateSchedules = async (groups, startDate, endDate, useBulk = true) => {
  if (useBulk && groups.length > 1) {
    // Use new bulk API for multiple groups
    return generateBulkSchedules(groups.map(g => g.id), startDate, endDate);
  } else {
    // Use old API for single group or fallback
    return generateSingleSchedule(groups[0].id, startDate, endDate);
  }
};
```

## 8. Deployment Checklist

### 8.1 Backend Deployment
- ✅ Verify `@EnableScheduling` is active
- ✅ Check Monday 8 AM cron job works in production timezone
- ✅ Test all bulk endpoints with authentication
- ✅ Verify database performance with large group sets

### 8.2 Frontend Deployment
- ✅ Update API base URLs for production
- ✅ Test bulk generation with real data
- ✅ Verify update dialog UX works correctly
- ✅ Test error handling with network failures
- ✅ Add loading states for better UX

## 9. Monitoring and Analytics

### 9.1 Track Usage

```javascript
const trackBulkGeneration = (result) => {
  // Analytics tracking
  analytics.track('bulk_schedule_generation', {
    groups_count: result.total_groups_processed,
    schedules_generated: result.total_schedules_generated,
    success_rate: result.successful_groups / result.total_groups_processed,
    errors: result.errors.length
  });
};

const trackScheduleUpdate = (scheduleId, updateMode, futureCount) => {
  analytics.track('schedule_update', {
    schedule_id: scheduleId,
    update_mode: updateMode,
    future_schedules_affected: futureCount
  });
};
```

This integration guide provides everything needed to successfully implement the new bulk schedule management features in your frontend application, with improved performance, better UX, and comprehensive error handling.