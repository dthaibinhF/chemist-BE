# Salary System - Frontend Developer Guide

## Overview
This document outlines the new Teacher Salary Management system implemented in the backend API. It provides comprehensive endpoints for salary configuration, calculation, and reporting.

## What's New

### New Models & Entities
1. **SalaryType Enum** - `PER_LESSON` or `FIXED`
2. **TeacherMonthlySummary Entity** - Monthly salary summaries for teachers
3. **Enhanced Teacher Model** - Added salary configuration fields

### New API Endpoints
All salary endpoints are under `/api/v1/salary` prefix.

---

## 🔧 Salary Configuration Endpoints

### 1. Update Teacher Salary Configuration
**PUT** `/api/v1/salary/teacher/{teacherId}/config`

Configure how a teacher's salary is calculated.

**Parameters:**
- `teacherId` (path) - Teacher ID
- `salaryType` (query) - `PER_LESSON` or `FIXED`
- `baseRate` (query) - Base rate amount (BigDecimal)

**Request Example:**
```bash
PUT /api/v1/salary/teacher/1/config?salaryType=PER_LESSON&baseRate=500000.00
```

**Response:**
```json
{
  "id": 1,
  "first_name": "John",
  "last_name": "Doe",
  "salary_type": "PER_LESSON",
  "base_rate": 500000.00,
  // ... other teacher fields
}
```

### 2. Get Teacher Salary Configuration
**GET** `/api/v1/salary/teacher/{teacherId}/config`

Retrieve current salary configuration for a teacher.

**Response:**
```json
{
  "id": 1,
  "first_name": "John",
  "last_name": "Doe",
  "salary_type": "PER_LESSON",
  "base_rate": 500000.00,
  // ... other teacher fields
}
```

---

## 💰 Salary Calculation Endpoints

### 3. Calculate Monthly Salary (Single Teacher)
**POST** `/api/v1/salary/teacher/{teacherId}/calculate`

Calculate and save monthly salary for a specific teacher.

**Parameters:**
- `teacherId` (path) - Teacher ID
- `month` (query) - Month (1-12)
- `year` (query) - Year (2020-2100)

**Request Example:**
```bash
POST /api/v1/salary/teacher/1/calculate?month=12&year=2024
```

**Response:**
```json
{
  "id": 123,
  "teacher_id": 1,
  "teacher_name": "John Doe",
  "month": 12,
  "year": 2024,
  "scheduled_lessons": 20,
  "completed_lessons": 18,
  "completion_rate": 0.9000,
  "rate_per_lesson": 500000.00,
  "base_salary": 9000000.00,
  "performance_bonus": 900000.00,
  "total_salary": 9900000.00,
  "created_at": "2024-12-01T10:00:00+07:00",
  "updated_at": "2024-12-01T10:00:00+07:00"
}
```

### 4. Calculate Monthly Salaries (All Teachers)
**POST** `/api/v1/salary/calculate-all`

Calculate monthly salaries for all active teachers.

**Parameters:**
- `month` (query) - Month (1-12)
- `year` (query) - Year (2020-2100)

**Request Example:**
```bash
POST /api/v1/salary/calculate-all?month=12&year=2024
```

**Response:**
```json
[
  {
    "id": 123,
    "teacher_id": 1,
    "teacher_name": "John Doe",
    "month": 12,
    "year": 2024,
    // ... full summary data
  },
  {
    "id": 124,
    "teacher_id": 2,
    "teacher_name": "Jane Smith",
    "month": 12,
    "year": 2024,
    // ... full summary data
  }
]
```

### 5. Recalculate Monthly Salary
**PUT** `/api/v1/salary/teacher/{teacherId}/recalculate`

Recalculate existing monthly salary (useful after attendance updates).

**Parameters:**
- `teacherId` (path) - Teacher ID
- `month` (query) - Month (1-12)
- `year` (query) - Year (2020-2100)

**Request Example:**
```bash
PUT /api/v1/salary/teacher/1/recalculate?month=12&year=2024
```

**Response:** Same as calculate endpoint.

---

## 📊 Salary History & Reporting Endpoints

### 6. Get Teacher Salary Summaries (Paginated)
**GET** `/api/v1/salary/teacher/{teacherId}/summaries`

Get paginated salary summaries for a teacher.

**Parameters:**
- `teacherId` (path) - Teacher ID
- `page` (query, optional) - Page number (default: 0)
- `size` (query, optional) - Page size (default: 12)
- `sort` (query, optional) - Sort fields (default: year,month)

**Request Example:**
```bash
GET /api/v1/salary/teacher/1/summaries?page=0&size=12&sort=year,desc&sort=month,desc
```

**Response:**
```json
{
  "content": [
    {
      "id": 123,
      "teacher_id": 1,
      "teacher_name": "John Doe",
      "month": 12,
      "year": 2024,
      // ... full summary data
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 12,
    "sort": {
      "sorted": true,
      "orderBy": ["year", "month"]
    }
  },
  "totalElements": 24,
  "totalPages": 2,
  "last": false,
  "first": true
}
```

### 7. Get Specific Monthly Summary
**GET** `/api/v1/salary/teacher/{teacherId}/summary/{year}/{month}`

Get a specific monthly salary summary.

**Request Example:**
```bash
GET /api/v1/salary/teacher/1/summary/2024/12
```

**Response:** Same as TeacherMonthlySummaryDTO format.

### 8. Get Salary History (Date Range)
**GET** `/api/v1/salary/teacher/{teacherId}/history`

Get salary history within a date range.

**Parameters:**
- `teacherId` (path) - Teacher ID
- `fromYear` (query) - Starting year
- `fromMonth` (query) - Starting month
- `toYear` (query) - Ending year
- `toMonth` (query) - Ending month

**Request Example:**
```bash
GET /api/v1/salary/teacher/1/history?fromYear=2024&fromMonth=1&toYear=2024&toMonth=12
```

**Response:** Array of TeacherMonthlySummaryDTO objects.

---

## 🎯 Frontend Integration Guidelines

### Salary Configuration Flow
1. **Display Current Config**: Use GET `/teacher/{id}/config` to show current settings
2. **Update Config**: Use PUT `/teacher/{id}/config` with form data
3. **Validation**: Ensure baseRate > 0 and salaryType is valid enum

### Salary Calculation Flow
1. **Monthly Calculation**: Use POST `/teacher/{id}/calculate` for individual teachers
2. **Bulk Calculation**: Use POST `/calculate-all` for payroll processing
3. **Recalculation**: Use PUT `/teacher/{id}/recalculate` after attendance updates

### Dashboard & Reporting
1. **Teacher Salary History**: Use GET `/teacher/{id}/summaries` with pagination
2. **Specific Month**: Use GET `/teacher/{id}/summary/{year}/{month}`
3. **Date Range Reports**: Use GET `/teacher/{id}/history` with date filters

---

## 📋 Data Models

### TeacherMonthlySummaryDTO Structure
```typescript
interface TeacherMonthlySummaryDTO {
  id: number;
  teacher_id: number;
  teacher_name: string;
  month: number;           // 1-12
  year: number;            // 2020-2100
  scheduled_lessons: number;
  completed_lessons: number;
  completion_rate: number; // 0.0000-1.0000
  rate_per_lesson: number;
  base_salary: number;
  performance_bonus: number;
  total_salary: number;
  created_at: string;      // ISO datetime
  updated_at: string;      // ISO datetime
}
```

### SalaryType Enum
```typescript
enum SalaryType {
  PER_LESSON = "PER_LESSON",  // Salary = completed_lessons * rate_per_lesson
  FIXED = "FIXED"             // Salary = base_rate (monthly fixed amount)
}
```

### Enhanced Teacher Model
```typescript
interface TeacherDTO {
  id: number;
  first_name: string;
  last_name: string;
  // ... existing fields
  salary_type: SalaryType;    // NEW FIELD
  base_rate: number;          // NEW FIELD
}
```

---

## ⚠️ Important Notes

### Performance Bonus Calculation
- **95%+ completion rate**: 15% bonus
- **85-94% completion rate**: 10% bonus
- **Below 85%**: No bonus

### Error Handling
- **400**: Invalid parameters, duplicate calculation, future months
- **404**: Teacher not found, summary not found
- **500**: Calculation errors, database issues

### Validation Rules
- Month: 1-12
- Year: 2020-2100
- Cannot calculate future months
- Cannot duplicate existing summaries (use recalculate instead)
- BaseRate must be positive

### Business Logic
- **PER_LESSON**: `base_salary = completed_lessons * rate_per_lesson`
- **FIXED**: `base_salary = base_rate` (regardless of lesson count)
- **Total Salary**: `base_salary + performance_bonus`
- **Completion Rate**: `completed_lessons / scheduled_lessons`

---

## 🚀 Migration from Previous System

### If Upgrading from Basic Teacher CRUD:
1. **New Fields**: Update Teacher forms to include `salary_type` and `base_rate`
2. **New Endpoints**: Integrate salary calculation endpoints
3. **New UI Components**: Build salary configuration and reporting screens
4. **Data Migration**: Set default salary configurations for existing teachers

### Testing Recommendations
1. Test with different salary types (PER_LESSON vs FIXED)
2. Test performance bonus calculations at different completion rates
3. Test pagination and sorting in salary history
4. Test error scenarios (future months, duplicates, missing teachers)

---

## 📞 Support

For questions about this API, refer to:
- **Swagger Documentation**: `/swagger-ui.html` (when application is running)
- **API Base URL**: `http://localhost:8080/api/v1/salary`
- **Timezone**: All calculations use `Asia/Ho_Chi_Minh` timezone