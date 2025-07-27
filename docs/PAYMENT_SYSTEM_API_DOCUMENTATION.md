# Payment System API Documentation

## Overview

This document provides comprehensive API documentation for the Chemist-BE payment system, designed for frontend developers to integrate payment functionality into their applications.

The payment system consists of two main controllers:
- **PaymentDetailController**: Manages individual payment transactions
- **StudentPaymentController**: Manages payment obligations and summaries

## Base URL
All endpoints are prefixed with: `http://localhost:8080/api/v1`

---

## PaymentDetailController

**Base Path:** `/api/v1/payment-detail`

Manages individual payment records and transactions.

### Data Model - PaymentDetailDTO

```json
{
  "id": 1,
  "created_at": "2024-01-15T10:30:00+07:00",
  "updated_at": "2024-01-15T10:30:00+07:00",
  "end_at": null,
  "fee_id": 1,
  "fee_name": "Tuition Fee - Grade 10",
  "student_id": 5,
  "student_name": "John Doe",
  "pay_method": "BANK_TRANSFER",
  "amount": 1500000.00,
  "description": "Monthly tuition payment",
  "have_discount": 150000.00,
  "payment_status": "PAID",
  "due_date": "2024-01-31T23:59:59+07:00",
  "generated_amount": 1650000.00,
  "effective_discount": 150000.00,
  "is_overdue": false
}
```

#### Field Descriptions

| Field | Type | Description |
|-------|------|-------------|
| `id` | Integer | Unique payment detail ID |
| `fee_id` | Integer | ID of the fee structure |
| `fee_name` | String | Display name of the fee |
| `student_id` | Integer | ID of the student making payment |
| `student_name` | String | Display name of the student |
| `pay_method` | String | Payment method (CASH, BANK_TRANSFER, CREDIT_CARD, etc.) |
| `amount` | BigDecimal | **Final amount actually paid by student (after discounts)** |
| `description` | String | Payment description or notes |
| `have_discount` | BigDecimal | **Available discount amount for this payment** |
| `payment_status` | Enum | PENDING, PARTIAL, PAID, OVERDUE |
| `due_date` | OffsetDateTime | When payment is due |
| `generated_amount` | BigDecimal | **Original fee amount (before any discounts)** |
| `effective_discount` | BigDecimal | **Calculated discount: generatedAmount - amount** |
| `is_overdue` | Boolean | Whether payment is past due date |

### Endpoints

#### 1. Get All Active Payment Details
```http
GET /api/v1/payment-detail
```

**Response:** Array of PaymentDetailDTO

```json
[
  {
    "id": 1,
    "fee_name": "Tuition Fee",
    "student_name": "John Doe",
    "amount": 1500000.00,
    "payment_status": "PAID"
  }
]
```

#### 2. Get Payment Detail by ID
```http
GET /api/v1/payment-detail/{id}
```

**Parameters:**
- `id` (path): Payment detail ID

**Response:** PaymentDetailDTO

#### 3. Create Payment Detail
```http
POST /api/v1/payment-detail
Content-Type: application/json
```

**Request Body:**
```json
{
  "fee_id": 1,
  "student_id": 5,
  "pay_method": "BANK_TRANSFER",
  "amount": 1500000.00,
  "description": "Monthly tuition payment",
  "have_discount": 150000.00,
  "payment_status": "PAID",
  "due_date": "2024-01-31T23:59:59+07:00"
}
```

**Response:** Created PaymentDetailDTO (HTTP 201)

#### 4. Update Payment Detail
```http
PUT /api/v1/payment-detail/{id}
Content-Type: application/json
```

**Parameters:**
- `id` (path): Payment detail ID

**Request Body:** PaymentDetailDTO (same as create)

**Response:** Updated PaymentDetailDTO

#### 5. Delete Payment Detail
```http
DELETE /api/v1/payment-detail/{id}
```

**Parameters:**
- `id` (path): Payment detail ID

**Response:** HTTP 204 No Content

#### 6. Get Payments by Student
```http
GET /api/v1/payment-detail/student/{studentId}
```

**Parameters:**
- `studentId` (path): Student ID

**Response:** Array of PaymentDetailDTO for the student

#### 7. Get Payments by Fee
```http
GET /api/v1/payment-detail/fee/{feeId}
```

**Parameters:**
- `feeId` (path): Fee ID

**Response:** Array of PaymentDetailDTO for the fee

#### 8. Get Payments by Student and Fee
```http
GET /api/v1/payment-detail/student/{studentId}/fee/{feeId}
```

**Parameters:**
- `studentId` (path): Student ID
- `feeId` (path): Fee ID

**Response:** Array of PaymentDetailDTO matching both criteria

---

## StudentPaymentController

**Base Path:** `/api/v1/student-payment`

Manages payment obligations, summaries, and tracking for students.

### Data Model - StudentPaymentSummaryDTO

```json
{
  "id": 1,
  "created_at": "2024-01-15T10:30:00+07:00",
  "updated_at": "2024-01-15T10:30:00+07:00",
  "end_at": null,
  "student_id": 5,
  "student_name": "John Doe",
  "fee_id": 1,
  "fee_name": "Tuition Fee - Grade 10",
  "academic_year_id": 1,
  "academic_year_name": "2024-2025",
  "group_id": 3,
  "group_name": "Chemistry Advanced",
  "total_amount_due": 5000000.00,
  "total_amount_paid": 3000000.00,
  "outstanding_amount": 2000000.00,
  "payment_status": "PARTIALLY_PAID",
  "due_date": "2024-12-31T23:59:59+07:00",
  "enrollment_date": "2024-01-15T10:30:00+07:00",
  "completion_rate": 0.60,
  "is_overdue": false,
  "is_fully_paid": false
}
```

#### Field Descriptions

| Field | Type | Description |
|-------|------|-------------|
| `student_id` | Integer | ID of the student |
| `student_name` | String | Display name of the student |
| `fee_id` | Integer | ID of the fee structure |
| `fee_name` | String | Display name of the fee |
| `academic_year_id` | Integer | ID of the academic year |
| `academic_year_name` | String | Display name of academic year |
| `group_id` | Integer | ID of the group (optional) |
| `group_name` | String | Display name of the group |
| `total_amount_due` | BigDecimal | Total amount student should pay |
| `total_amount_paid` | BigDecimal | Total amount student has paid |
| `outstanding_amount` | BigDecimal | Remaining amount to be paid |
| `payment_status` | Enum | PENDING, PARTIALLY_PAID, PAID, OVERDUE |
| `due_date` | OffsetDateTime | Final due date for payment |
| `enrollment_date` | OffsetDateTime | When student enrolled |
| `completion_rate` | BigDecimal | Payment completion (0.0 to 1.0) |
| `is_overdue` | Boolean | Whether payment is overdue |
| `is_fully_paid` | Boolean | Whether payment is complete |

### Endpoints

#### Payment Generation

#### 1. Generate Payment for Student in Group
```http
POST /api/v1/student-payment/student/{studentId}/group/{groupId}
```

**Use Case:** When a student joins a group, create their payment obligation.

**Parameters:**
- `studentId` (path): Student ID
- `groupId` (path): Group ID

**Response:** StudentPaymentSummaryDTO (HTTP 201)

**Frontend Usage:**
```javascript
// When student enrolls in a group
const response = await fetch(`/api/v1/student-payment/student/${studentId}/group/${groupId}`, {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` }
});
const paymentSummary = await response.json();
```

#### 2. Generate Payments for Entire Group
```http
POST /api/v1/student-payment/group/{groupId}/generate-all
```

**Use Case:** Bulk generate payment obligations for all students in a group.

**Parameters:**
- `groupId` (path): Group ID

**Response:** Array of StudentPaymentSummaryDTO

#### Payment Summary Retrieval

#### 3. Get Student Payment Summaries
```http
GET /api/v1/student-payment/student/{studentId}
```

**Use Case:** Display all payment obligations for a student.

**Parameters:**
- `studentId` (path): Student ID

**Response:** Array of StudentPaymentSummaryDTO

**Frontend Usage:**
```javascript
// Display student's payment dashboard
const response = await fetch(`/api/v1/student-payment/student/${studentId}`);
const summaries = await response.json();

summaries.forEach(summary => {
  console.log(`${summary.fee_name}: ${summary.outstanding_amount} remaining`);
});
```

#### 4. Get Group Payment Summaries
```http
GET /api/v1/student-payment/group/{groupId}
```

**Use Case:** Display payment status for all students in a group.

**Parameters:**
- `groupId` (path): Group ID

**Response:** Array of StudentPaymentSummaryDTO

#### 5. Get Payment Summary by ID
```http
GET /api/v1/student-payment/summary/{summaryId}
```

**Parameters:**
- `summaryId` (path): Payment summary ID

**Response:** StudentPaymentSummaryDTO

#### Payment Management

#### 6. Update Payment Summary After Payment
```http
PUT /api/v1/student-payment/update-after-payment?studentId={studentId}&feeId={feeId}&academicYearId={academicYearId}&groupId={groupId}
```

**Use Case:** Recalculate payment summary after a payment detail is created/updated.

**Parameters:**
- `studentId` (query): Student ID
- `feeId` (query): Fee ID
- `academicYearId` (query): Academic year ID
- `groupId` (query): Group ID (optional)

**Response:** HTTP 200

**Frontend Usage:**
```javascript
// After creating a payment detail, update the summary
await fetch('/api/v1/payment-detail', {
  method: 'POST',
  body: JSON.stringify(paymentDetail)
});

// Then update the summary
await fetch(`/api/v1/student-payment/update-after-payment?studentId=${studentId}&feeId=${feeId}&academicYearId=${academicYearId}`, {
  method: 'PUT'
});
```

#### 7. Delete Payment Summary
```http
DELETE /api/v1/student-payment/summary/{summaryId}
```

**Parameters:**
- `summaryId` (path): Payment summary ID

**Response:** HTTP 204

#### 8. Recalculate All Payment Summaries
```http
POST /api/v1/student-payment/recalculate-all
```

**Use Case:** Maintenance operation to recalculate all payment summaries.

**Response:** HTTP 200

---

## Payment Status Enum

```javascript
const PaymentStatus = {
  PENDING: 'PENDING',           // Payment obligation created but not yet paid
  PARTIAL: 'PARTIAL',           // Some amount paid but not fully paid
  PAID: 'PAID',                 // Fully paid (amount >= required amount after discount)
  OVERDUE: 'OVERDUE',           // Past due date and not fully paid
  CANCELLED: 'CANCELLED'        // Payment cancelled
};
```

---

## Payment Logic Explanation

### **Updated Business Logic (Fixed)**

The payment system now correctly handles discounts with the following logic:

#### **Field Meanings:**
- **`amount`**: Final amount actually paid by student (after discount applied)
- **`generatedAmount`**: Original fee amount (before any discounts) 
- **`haveDiscount`**: Available discount amount
- **`effectiveDiscount`**: Calculated as `generatedAmount - amount`

#### **Payment Status Calculation:**
```javascript
// Example payment with discount:
const payment = {
  generatedAmount: 1200000,  // Original fee: 1.2M VND
  haveDiscount: 200000,      // Available discount: 200K VND  
  amount: 1000000,           // Final amount paid: 1M VND
  effectiveDiscount: 200000  // Calculated: 1.2M - 1M = 200K
};

// Status logic:
// requiredAmount = generatedAmount - haveDiscount = 1.2M - 200K = 1M
// if (amount >= requiredAmount) → PAID
// if (amount > 0 && amount < requiredAmount) → PARTIAL  
// if (amount = 0) → PENDING
// if (overdue && not PAID) → OVERDUE
```

#### **Key Scenarios:**

1. **Full Payment with Discount:**
   - `generatedAmount`: 1,000,000 VND
   - `haveDiscount`: 100,000 VND
   - `amount`: 900,000 VND (student pays this)
   - **Status**: `PAID` (because 900K >= (1M - 100K))

2. **Partial Payment:**
   - `generatedAmount`: 1,000,000 VND
   - `haveDiscount`: 100,000 VND
   - `amount`: 500,000 VND (student pays this)
   - **Status**: `PARTIAL` (because 500K < (1M - 100K))

3. **Overpayment:**
   - `generatedAmount`: 1,000,000 VND
   - `haveDiscount`: 100,000 VND
   - `amount`: 950,000 VND (student pays more than required)
   - **Status**: `PAID` (because 950K >= (1M - 100K))

---

## Frontend Integration Examples

### 1. Student Payment Dashboard

```javascript
class StudentPaymentDashboard {
  async loadStudentPayments(studentId) {
    try {
      const response = await fetch(`/api/v1/student-payment/student/${studentId}`);
      const summaries = await response.json();
      
      this.displayPaymentSummaries(summaries);
    } catch (error) {
      console.error('Failed to load payment summaries:', error);
    }
  }
  
  displayPaymentSummaries(summaries) {
    summaries.forEach(summary => {
      const statusColor = this.getStatusColor(summary.payment_status);
      const isOverdue = summary.is_overdue ? 'OVERDUE' : '';
      
      console.log(`
        Fee: ${summary.fee_name}
        Due: ${summary.total_amount_due.toLocaleString()} VND
        Paid: ${summary.total_amount_paid.toLocaleString()} VND
        Outstanding: ${summary.outstanding_amount.toLocaleString()} VND
        Status: ${summary.payment_status} ${isOverdue}
        Progress: ${(summary.completion_rate * 100).toFixed(1)}%
      `);
    });
  }
  
  getStatusColor(status) {
    const colors = {
      'PENDING': '#ffc107',      // Yellow - waiting for payment
      'PARTIAL': '#17a2b8',      // Blue - partially paid 
      'PAID': '#28a745',         // Green - fully paid
      'OVERDUE': '#dc3545',      // Red - overdue
      'CANCELLED': '#6c757d'     // Gray - cancelled
    };
    return colors[status] || '#6c757d';
  }
}
```

### 2. Making a Payment

```javascript
class PaymentProcessor {
  async makePayment(studentId, feeId, amount, payMethod) {
    try {
      // 1. Create payment detail
      const paymentDetail = {
        fee_id: feeId,
        student_id: studentId,
        amount: amount,
        pay_method: payMethod,
        payment_status: 'PAID',
        description: 'Payment via web portal'
      };
      
      const paymentResponse = await fetch('/api/v1/payment-detail', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.token}`
        },
        body: JSON.stringify(paymentDetail)
      });
      
      if (!paymentResponse.ok) {
        throw new Error('Payment creation failed');
      }
      
      // 2. Update payment summary
      await fetch(`/api/v1/student-payment/update-after-payment?studentId=${studentId}&feeId=${feeId}&academicYearId=${this.academicYearId}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${this.token}` }
      });
      
      // 3. Refresh payment dashboard
      this.refreshPaymentDashboard(studentId);
      
    } catch (error) {
      console.error('Payment processing failed:', error);
      throw error;
    }
  }
}
```

### 3. Group Payment Overview

```javascript
class GroupPaymentOverview {
  async loadGroupPayments(groupId) {
    try {
      const response = await fetch(`/api/v1/student-payment/group/${groupId}`);
      const summaries = await response.json();
      
      return this.analyzeGroupPayments(summaries);
    } catch (error) {
      console.error('Failed to load group payments:', error);
    }
  }
  
  analyzeGroupPayments(summaries) {
    const analytics = {
      totalStudents: summaries.length,
      fullyPaid: summaries.filter(s => s.is_fully_paid).length,
      overdue: summaries.filter(s => s.is_overdue).length,
      totalDue: summaries.reduce((sum, s) => sum + s.total_amount_due, 0),
      totalPaid: summaries.reduce((sum, s) => sum + s.total_amount_paid, 0),
      outstandingTotal: summaries.reduce((sum, s) => sum + s.outstanding_amount, 0)
    };
    
    analytics.collectionRate = (analytics.totalPaid / analytics.totalDue * 100).toFixed(1);
    
    return {
      analytics,
      summaries: summaries.sort((a, b) => b.outstanding_amount - a.outstanding_amount)
    };
  }
}
```

---

## Error Handling

### Common HTTP Status Codes

| Code | Status | Description |
|------|--------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | Resource deleted successfully |
| 400 | Bad Request | Invalid request data |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Server error |

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00+07:00",
  "status": 404,
  "error": "Not Found",
  "message": "Student not found with ID: 999",
  "path": "/api/v1/student-payment/student/999"
}
```

### Frontend Error Handling

```javascript
async function handleApiCall(url, options = {}) {
  try {
    const response = await fetch(url, options);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(`${error.status}: ${error.message}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('API call failed:', error);
    
    // Handle specific error types
    if (error.message.includes('404')) {
      showNotification('Resource not found', 'error');
    } else if (error.message.includes('400')) {
      showNotification('Invalid input data', 'error');
    } else {
      showNotification('An unexpected error occurred', 'error');
    }
    
    throw error;
  }
}
```

---

## Authentication

All endpoints require authentication. Include the JWT token in the Authorization header:

```javascript
const headers = {
  'Authorization': `Bearer ${jwtToken}`,
  'Content-Type': 'application/json'
};
```

---

## Rate Limiting

Consider implementing client-side rate limiting for batch operations:

```javascript
class RateLimitedApiClient {
  constructor(maxRequestsPerSecond = 10) {
    this.maxRequests = maxRequestsPerSecond;
    this.requests = [];
  }
  
  async makeRequest(url, options) {
    await this.waitForRateLimit();
    this.requests.push(Date.now());
    return fetch(url, options);
  }
  
  async waitForRateLimit() {
    const now = Date.now();
    this.requests = this.requests.filter(time => now - time < 1000);
    
    if (this.requests.length >= this.maxRequests) {
      const delay = 1000 - (now - this.requests[0]);
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }
}
```

---

## Testing

Use these test scenarios to validate your integration:

### Test Data

```javascript
const testData = {
  student: {
    id: 1,
    name: "Test Student"
  },
  fee: {
    id: 1,
    name: "Test Fee",
    amount: 1000000.00
  },
  group: {
    id: 1,
    name: "Test Group"
  }
};
```

### Test Cases

1. **Create payment obligation**
2. **Make partial payment**
3. **Complete payment**
4. **Handle overdue payments**
5. **Generate group payments**
6. **Error scenarios (invalid IDs, etc.)**

This documentation provides everything needed to integrate the payment system into your frontend application. For additional support, refer to the Swagger documentation at `/swagger-ui.html` when the application is running.