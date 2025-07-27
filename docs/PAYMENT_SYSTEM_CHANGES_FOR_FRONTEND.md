# Payment System Changes - Frontend Developer Guide

## 🚨 BREAKING CHANGES & NEW FEATURES

This document outlines all changes made to the payment system in the latest session and how frontend developers should adapt their code.

---

## 📊 DATABASE SCHEMA CHANGES

### New Table: `student_payment_summary`
**Purpose**: Tracks payment obligations for each student per group/fee combination

```sql
CREATE TABLE student_payment_summary (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    fee_id INTEGER NOT NULL,
    academic_year_id INTEGER NOT NULL,
    group_id INTEGER,
    total_amount_due DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount_paid DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    outstanding_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date TIMESTAMP WITH TIME ZONE,
    enrollment_date TIMESTAMP WITH TIME ZONE NOT NULL,
    create_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    end_at TIMESTAMP WITH TIME ZONE
);
```

### Enhanced `payment_detail` Table
**New columns added**:
```sql
ALTER TABLE payment_detail ADD:
- payment_status VARCHAR(20) DEFAULT 'PENDING'
- due_date TIMESTAMP WITH TIME ZONE  
- generated_amount DECIMAL(10,2)
```

---

## 🔄 MODEL CHANGES

### 1. NEW: PaymentStatus Enum
```typescript
enum PaymentStatus {
  PENDING = "PENDING",    // Not yet paid
  PARTIAL = "PARTIAL",    // Partially paid
  PAID = "PAID",          // Fully paid  
  OVERDUE = "OVERDUE"     // Past due date and not fully paid
}
```

### 2. NEW: StudentPaymentSummaryDTO
```typescript
interface StudentPaymentSummaryDTO {
  id: number;
  student_id: number;
  student_name: string;
  fee_id: number;
  fee_name: string;
  academic_year_id: number;
  academic_year_name: string;
  group_id: number;
  group_name: string;
  total_amount_due: number;
  total_amount_paid: number;
  outstanding_amount: number;
  payment_status: PaymentStatus;
  due_date: string;           // ISO datetime
  enrollment_date: string;    // ISO datetime
  completion_rate: number;    // 0.0 to 1.0
  is_overdue: boolean;
  is_fully_paid: boolean;
  create_at: string;         // ISO datetime
  update_at: string;         // ISO datetime
}
```

### 3. ENHANCED: PaymentDetailDTO
**New fields added**:
```typescript
interface PaymentDetailDTO {
  // ... existing fields ...
  payment_status: PaymentStatus;      // NEW
  due_date: string;                   // NEW - ISO datetime
  generated_amount: number;           // NEW - original amount before discounts
  effective_discount: number;         // NEW - calculated discount
  is_overdue: boolean;               // NEW
}
```

### 4. NEW: FinancialStatisticsDTO
```typescript
interface FinancialStatisticsDTO {
  total_revenue: number;
  total_outstanding: number;
  total_amount_due: number;
  collection_rate: number;            // Percentage 0-100
  pending_payments_count: number;
  partial_payments_count: number;
  paid_payments_count: number;
  overdue_payments_count: number;
  overdue_amount: number;
  current_month_revenue: number;
  previous_month_revenue: number;
  monthly_growth_rate: number;        // Percentage
  average_payment_amount: number;
  total_transactions: number;
  active_students_count: number;
  student_participation_rate: number; // Percentage 0-100
}
```

---

## 🆕 NEW API ENDPOINTS

### Student Payment Management
**Base URL**: `/api/v1/student-payment`

```bash
# Generate payment obligation when student joins group
POST /api/v1/student-payment/student/{studentId}/group/{groupId}
Response: StudentPaymentSummaryDTO

# Generate payments for all students in a group (bulk operation)
POST /api/v1/student-payment/group/{groupId}/generate-all
Response: StudentPaymentSummaryDTO[]

# Get all payment summaries for a student
GET /api/v1/student-payment/student/{studentId}
Response: StudentPaymentSummaryDTO[]

# Get all payment summaries for a group
GET /api/v1/student-payment/group/{groupId}
Response: StudentPaymentSummaryDTO[]

# Get specific payment summary by ID
GET /api/v1/student-payment/summary/{summaryId}
Response: StudentPaymentSummaryDTO

# Update payment summary after payment is made
PUT /api/v1/student-payment/update-after-payment
Params: studentId, feeId, academicYearId, groupId
Response: 200 OK

# Delete payment summary (soft delete)
DELETE /api/v1/student-payment/summary/{summaryId}
Response: 204 No Content

# Recalculate all payment summaries (maintenance operation)
POST /api/v1/student-payment/recalculate-all
Response: 200 OK
```

### Financial Dashboard & Statistics
**Base URL**: `/api/v1/financial`

```bash
# Get comprehensive financial dashboard
GET /api/v1/financial/dashboard
Response: FinancialStatisticsDTO

# Get financial statistics for date range
GET /api/v1/financial/statistics?startDate=2024-01-01&endDate=2024-12-31
Response: FinancialStatisticsDTO

# Get all overdue payment details
GET /api/v1/financial/overdue/details
Response: PaymentDetailDTO[]

# Get all overdue payment summaries
GET /api/v1/financial/overdue/summaries
Response: StudentPaymentSummaryDTO[]

# Get overdue payments for specific student
GET /api/v1/financial/overdue/student/{studentId}
Response: StudentPaymentSummaryDTO[]

# Batch update overdue payment statuses (run daily)
POST /api/v1/financial/overdue/update-statuses
Response: number (count of updated payments)

# Get overdue payment statistics
GET /api/v1/financial/overdue/statistics
Response: { 
  totalOverdueAmount: number,
  overduePaymentSummariesCount: number,
  overduePaymentDetailsCount: number,
  uniqueStudentsWithOverduePayments: number,
  asOfDate: string
}

# Check if student has overdue payments
GET /api/v1/financial/overdue/student/{studentId}/check
Response: boolean

# Get days overdue for payment summary
GET /api/v1/financial/overdue/summary/{summaryId}/days
Response: number (negative if not overdue)
```

### Enhanced PaymentDetail Endpoints
**Base URL**: `/api/v1/payment-detail`

```bash
# NEW: Get payments by status
GET /api/v1/payment-detail/status/{status}
Response: PaymentDetailDTO[]

# NEW: Get total amount paid by student for specific fee
GET /api/v1/payment-detail/student/{studentId}/fee/{feeId}/total
Response: number

# NEW: Get payments within date range
GET /api/v1/payment-detail/date-range?startDate=...&endDate=...
Response: PaymentDetailDTO[]

# ENHANCED: Create payment with summary update
POST /api/v1/payment-detail/with-summary-update
Body: PaymentDetailDTO + academicYearId + groupId
Response: PaymentDetailDTO
```

---

## 💻 FRONTEND INTEGRATION EXAMPLES

### 1. Student Enrollment Workflow
```javascript
// When student joins a group, automatically generate payment obligation
async function enrollStudentInGroup(studentId, groupId) {
  try {
    const response = await fetch(
      `/api/v1/student-payment/student/${studentId}/group/${groupId}`, 
      { method: 'POST' }
    );
    
    if (response.ok) {
      const paymentSummary = await response.json();
      console.log('Payment obligation created:', paymentSummary);
      
      // Update UI to show payment status
      updatePaymentStatus(paymentSummary);
    }
  } catch (error) {
    console.error('Failed to generate payment obligation:', error);
  }
}
```

### 2. Payment Status Display
```javascript
// Display payment status with proper styling
function renderPaymentStatus(status) {
  const statusConfig = {
    'PENDING': { color: '#FFA500', text: 'Pending Payment', icon: '⏳' },
    'PARTIAL': { color: '#FF6B6B', text: 'Partially Paid', icon: '⚠️' },
    'PAID': { color: '#4ECDC4', text: 'Fully Paid', icon: '✅' }, 
    'OVERDUE': { color: '#FF0000', text: 'Overdue', icon: '🚨' }
  };
  
  const config = statusConfig[status];
  return `
    <span class="payment-status" style="color: ${config.color}">
      ${config.icon} ${config.text}
    </span>
  `;
}

// Load student payment summaries
async function loadStudentPayments(studentId) {
  const response = await fetch(`/api/v1/student-payment/student/${studentId}`);
  const summaries = await response.json();
  
  summaries.forEach(summary => {
    console.log(`${summary.fee_name}: ${summary.payment_status}`);
    console.log(`Amount Due: ${summary.total_amount_due}`);
    console.log(`Amount Paid: ${summary.total_amount_paid}`);
    console.log(`Outstanding: ${summary.outstanding_amount}`);
  });
}
```

### 3. Financial Dashboard Integration
```javascript
// Load and display financial dashboard
async function loadFinancialDashboard() {
  const response = await fetch('/api/v1/financial/dashboard');
  const stats = await response.json();
  
  // Update dashboard widgets
  document.getElementById('total-revenue').textContent = 
    formatCurrency(stats.total_revenue);
  document.getElementById('collection-rate').textContent = 
    `${stats.collection_rate.toFixed(1)}%`;
  document.getElementById('overdue-count').textContent = 
    stats.overdue_payments_count;
  document.getElementById('monthly-growth').textContent = 
    `${stats.monthly_growth_rate >= 0 ? '+' : ''}${stats.monthly_growth_rate.toFixed(1)}%`;
  
  // Update status distribution chart
  updatePaymentStatusChart({
    pending: stats.pending_payments_count,
    partial: stats.partial_payments_count,
    paid: stats.paid_payments_count,
    overdue: stats.overdue_payments_count
  });
}

function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
}
```

### 4. Overdue Payment Management
```javascript
// Load and display overdue payments
async function loadOverduePayments() {
  const response = await fetch('/api/v1/financial/overdue/summaries');
  const overduePayments = await response.json();
  
  const overdueList = document.getElementById('overdue-list');
  overdueList.innerHTML = '';
  
  overduePayments.forEach(payment => {
    const item = document.createElement('div');
    item.className = 'overdue-item';
    item.innerHTML = `
      <div class="student-info">
        <strong>${payment.student_name}</strong>
        <span class="fee-name">${payment.fee_name}</span>
      </div>
      <div class="payment-info">
        <span class="amount">${formatCurrency(payment.outstanding_amount)}</span>
        <span class="due-date">Due: ${formatDate(payment.due_date)}</span>
      </div>
    `;
    overdueList.appendChild(item);
  });
}

// Run daily overdue status update
async function updateOverdueStatuses() {
  const response = await fetch('/api/v1/financial/overdue/update-statuses', {
    method: 'POST'
  });
  const updatedCount = await response.json();
  console.log(`Updated ${updatedCount} payments to overdue status`);
}
```

### 5. Bulk Payment Generation
```javascript
// Generate payments for all students in a group
async function generateGroupPayments(groupId) {
  try {
    const response = await fetch(
      `/api/v1/student-payment/group/${groupId}/generate-all`,
      { method: 'POST' }
    );
    
    const generatedSummaries = await response.json();
    console.log(`Generated ${generatedSummaries.length} payment obligations`);
    
    // Show success message
    showNotification(`Successfully generated payments for ${generatedSummaries.length} students`);
    
    // Refresh group payment display
    loadGroupPayments(groupId);
    
  } catch (error) {
    console.error('Failed to generate group payments:', error);
    showErrorNotification('Failed to generate payments for group');
  }
}
```

### 6. Payment Creation with Auto-Summary Update
```javascript
// Create payment with automatic summary update
async function createPaymentWithSummaryUpdate(paymentData, academicYearId, groupId) {
  const response = await fetch('/api/v1/payment-detail/with-summary-update', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ...paymentData,
      academicYearId,
      groupId
    })
  });
  
  if (response.ok) {
    const createdPayment = await response.json();
    console.log('Payment created and summary updated:', createdPayment);
    
    // Refresh payment displays
    loadStudentPayments(paymentData.student_id);
    loadFinancialDashboard();
  }
}
```

---

## 🔧 MIGRATION CONSIDERATIONS

### 1. **Existing PaymentDetail Records**
- All existing records will have default values set:
  - `payment_status`: `'PENDING'`
  - `generated_amount`: Set to current `amount` value
  - `due_date`: Set to 30 days after `create_at`

### 2. **New Required Fields**
- When creating new `PaymentDetail` records, include:
  - `payment_status` (defaults to `PENDING`)
  - `due_date` (required for proper overdue detection)
  - `generated_amount` (for discount tracking)

### 3. **Payment Summary Generation**
- For existing student-group relationships, run:
  ```javascript
  // Generate summaries for existing enrollments
  POST /api/v1/student-payment/group/{groupId}/generate-all
  ```

---

## ⚡ PERFORMANCE NOTES

### 1. **Caching Recommendations**
- Cache financial dashboard data for 5-10 minutes
- Cache overdue payment lists for 1 hour
- Real-time updates for individual payment status changes

### 2. **Batch Operations**
- Use bulk payment generation for large groups (100+ students)
- Run overdue status updates during off-peak hours
- Consider pagination for large payment lists

### 3. **Real-time Updates**
- Payment status changes should trigger summary recalculation
- Dashboard metrics should refresh after payment creation
- Overdue notifications should update after status changes

---

## 🚨 BREAKING CHANGE CHECKLIST

### ✅ **Required Frontend Updates**

1. **Update PaymentDetail interfaces** to include new fields
2. **Add PaymentStatus enum** to your TypeScript definitions
3. **Create StudentPaymentSummary interface** for new data structure
4. **Update payment creation flows** to use enhanced endpoints
5. **Implement financial dashboard** using new statistics endpoint
6. **Add overdue payment management** to admin interfaces
7. **Update payment status displays** with new status options
8. **Implement bulk payment generation** for group management

### ✅ **Optional Enhancements**

1. **Add payment reminder system** using overdue detection
2. **Implement payment analytics** with growth rate tracking
3. **Create payment status filters** for better UX
4. **Add automated overdue notifications** for students
5. **Implement pro-rata calculation display** for mid-period enrollments

---

## 📞 SUPPORT & TESTING

### **Testing Endpoints**
1. Test payment generation when students join groups
2. Verify payment status updates work correctly  
3. Test overdue detection and status updates
4. Validate financial dashboard calculations
5. Test bulk operations with large datasets

### **Error Handling**
- All new endpoints include proper error responses
- Payment summary updates are wrapped in try-catch blocks
- Graceful degradation if summary updates fail

### **Documentation**
- Full API documentation available at `/swagger-ui.html`
- Database schema documented in migration files
- Service layer has comprehensive JavaDoc comments

This enhanced payment system provides powerful automation and tracking capabilities while maintaining backward compatibility with existing payment functionality.