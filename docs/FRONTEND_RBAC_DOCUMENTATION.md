# Frontend Role-Based Access Control (RBAC) Documentation

## Overview

This document provides comprehensive guidance for frontend developers on implementing role-based access control in the Chemist-BE application. The system uses JWT-based authentication with hierarchical role permissions.

## Role Hierarchy & Access Levels

### 🔴 **ADMIN** - Full System Access
**Highest privilege level with complete system control**

**Access Permissions:**
- ✅ **Full CRUD operations** on all entities
- ✅ **User management** - Create, update, delete accounts and roles
- ✅ **System configuration** - Academic years, schools, grades, rooms
- ✅ **Financial management** - All payment operations, salary calculations
- ✅ **Advanced operations** - Statistics dashboard, bulk operations
- ✅ **AI agent** - Full information access with no restrictions

**Restricted Endpoints (ADMIN Only):**
```javascript
// Role Management
POST /api/v1/role
GET /api/v1/role/{id}
PUT /api/v1/role/{id}
DELETE /api/v1/role/{id}

// Account Management
DELETE /api/v1/account/{id}

// Salary Management
PUT /api/v1/salary/teacher/{teacherId}/config
POST /api/v1/salary/teacher/{teacherId}/calculate
POST /api/v1/salary/calculate-all
PUT /api/v1/salary/teacher/{teacherId}/recalculate
```

---

### 🟠 **MANAGER** - Administrative Operations
**High-level management with broad system access**

**Access Permissions:**
- ✅ **CRUD operations** on groups, schedules, students, teachers
- ✅ **Academic management** - Group sessions, schedules, attendance
- ✅ **Fee management** - View and manage all fee-related operations
- ✅ **Statistics dashboard** - Access to system metrics and reports
- ✅ **Salary operations** - Calculate and manage teacher salaries
- ✅ **AI agent** - Access to group/fee information, limited personal data

**Cannot Access:**
- ❌ Role management (ADMIN only)
- ❌ Account deletion (ADMIN only)
- ❌ System-level configuration changes

**Shared Endpoints with ADMIN:**
```javascript
// Statistics
GET /api/v1/statistics/dashboard

// Group Management
POST /api/v1/group-session
PUT /api/v1/group-session/{id}
DELETE /api/v1/group-session/{id}

// Room Management
POST /api/v1/room
PUT /api/v1/room/{id}
DELETE /api/v1/room/{id}
```

---

### 🟡 **TEACHER** - Teaching Operations
**Teaching-focused access with student interaction capabilities**

**Access Permissions:**
- ✅ **View operations** - Students, groups, schedules, rooms
- ✅ **Attendance management** - Mark and update attendance for their classes
- ✅ **Grade management** - Enter and update scores for their students
- ✅ **Fee information** - Basic fee information (limited access)
- ✅ **Own salary data** - View their personal salary summaries and history
- ✅ **AI agent** - Students in their classes, class schedules, basic fees

**Cannot Access:**
- ❌ Create/Update/Delete operations on core entities
- ❌ System administration functions
- ❌ Other teachers' salary information
- ❌ Financial operations beyond viewing

**Teacher-Specific Endpoints:**
```javascript
// Own Salary Information (with isCurrentTeacher check)
GET /api/v1/salary/teacher/{teacherId}/summaries
GET /api/v1/salary/teacher/{teacherId}/summary/{year}/{month}
GET /api/v1/salary/teacher/{teacherId}/history

// Teaching Operations
GET /api/v1/group-session
GET /api/v1/room/{id}
GET /api/v1/fee/* (read-only access)
```

---

### 🟢 **STUDENT** - Student Information Access
**Limited access to personal academic information**

**Access Permissions:**
- ✅ **Personal information** - Own student details, grades, attendance
- ✅ **Payment information** - Own fee payments and summaries
- ✅ **Schedule access** - Own class schedules and group information
- ✅ **AI agent** - Own information, own fees and payments only

**Cannot Access:**
- ❌ Other students' information
- ❌ Administrative functions
- ❌ System configuration
- ❌ Teacher or management operations

---

### 🔵 **PARENT** - Family Information Access
**Access to their children's academic information**

**Access Permissions:**
- ✅ **Child information** - Student details, grades, attendance for their children
- ✅ **Payment information** - Fee payments and summaries for their children
- ✅ **Schedule access** - Class schedules and group information for their children
- ✅ **AI agent** - Own information, own fees and payments only

**Cannot Access:**
- ❌ Other families' information
- ❌ Administrative functions
- ❌ System configuration
- ❌ Teacher or management operations

---

### ⚪ **PUBLIC** - Limited Public Access
**Minimal access for unauthenticated users**

**Access Permissions:**
- ✅ **Basic fee information** - General fee structures and payment methods
- ✅ **General schedules** - Public schedule information (no personal data)
- ✅ **AI agent** - Basic fee info, general schedules, no personal data

**Cannot Access:**
- ❌ Personal information
- ❌ Administrative functions
- ❌ Detailed student or teacher data
- ❌ Financial operations

---

## JWT Token Implementation

### Token Structure
```javascript
// JWT Token Header
Authorization: Bearer <jwt_token>

// Token Claims Structure
{
  "sub": "user@example.com",
  "authorities": ["ROLE_ADMIN"], // or ["ROLE_TEACHER"], etc.
  "iat": 1640995200,
  "exp": 1640998800
}
```

### Role Extraction
The system extracts roles from JWT tokens using the following pattern:
- Token contains `authorities` claim with role information
- Roles stored as `ROLE_<ROLE_NAME>` (e.g., `ROLE_ADMIN`, `ROLE_TEACHER`)
- Application logic uses role names without `ROLE_` prefix

### Fallback Behavior
- **No Token Provided**: User treated as `PUBLIC` role
- **Invalid Token**: User treated as `PUBLIC` role
- **Token Without Role**: User treated as `PUBLIC` role

---

## Frontend Implementation Guide

### 1. **Authentication Setup**

```javascript
// Store JWT token after login
const token = response.data.access_token;
localStorage.setItem('authToken', token);

// Extract role from token (client-side utility)
function extractRoleFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const authorities = payload.authorities || [];
    
    // Extract role from authorities array
    const roleAuthority = authorities.find(auth => auth.startsWith('ROLE_'));
    return roleAuthority ? roleAuthority.replace('ROLE_', '') : 'PUBLIC';
  } catch (error) {
    return 'PUBLIC';
  }
}

// Usage
const userRole = extractRoleFromToken(token);
```

### 2. **API Request Headers**

```javascript
// Axios interceptor for automatic token inclusion
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
```

### 3. **Role-Based UI Components**

```jsx
// React component for role-based rendering
const RoleBasedComponent = ({ allowedRoles, children, userRole }) => {
  if (!allowedRoles.includes(userRole)) {
    return null;
  }
  return children;
};

// Usage examples
<RoleBasedComponent allowedRoles={['ADMIN']} userRole={userRole}>
  <AdminPanel />
</RoleBasedComponent>

<RoleBasedComponent allowedRoles={['ADMIN', 'MANAGER']} userRole={userRole}>
  <StatisticsDashboard />
</RoleBasedComponent>

<RoleBasedComponent allowedRoles={['ADMIN', 'MANAGER', 'TEACHER']} userRole={userRole}>
  <StudentList />
</RoleBasedComponent>
```

### 4. **Route Protection**

```javascript
// React Router v6 with role-based route protection
const ProtectedRoute = ({ children, allowedRoles }) => {
  const userRole = useAuth().role;
  
  if (!allowedRoles.includes(userRole)) {
    return <Navigate to="/unauthorized" replace />;
  }
  
  return children;
};

// Route configuration
<Routes>
  <Route path="/admin/*" element={
    <ProtectedRoute allowedRoles={['ADMIN']}>
      <AdminRoutes />
    </ProtectedRoute>
  } />
  
  <Route path="/statistics" element={
    <ProtectedRoute allowedRoles={['ADMIN', 'MANAGER']}>
      <StatisticsPage />
    </ProtectedRoute>
  } />
  
  <Route path="/salary" element={
    <ProtectedRoute allowedRoles={['ADMIN', 'MANAGER', 'TEACHER']}>
      <SalaryPage />
    </ProtectedRoute>
  } />
</Routes>
```

### 5. **Error Handling**

```javascript
// Handle authorization errors
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 403) {
      // Insufficient permissions
      showNotification('Access denied. Insufficient permissions.', 'error');
      // Redirect to appropriate page based on role
      const userRole = getCurrentUserRole();
      redirectBasedOnRole(userRole);
    } else if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## API Endpoint Access Matrix

| Endpoint Category | ADMIN | MANAGER | TEACHER | STUDENT | PARENT | PUBLIC |
|-------------------|--------|---------|---------|---------|--------|--------|
| **Authentication** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Role Management** | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **Account Management** | ✅ | 📝 | 📝 | 📝 | 📝 | ❌ |
| **Fee Operations** | ✅ | ✅ | 📖 | 📖¹ | 📖¹ | 📖² |
| **Student Operations** | ✅ | ✅ | 📖 | 📖¹ | 📖¹ | ❌ |
| **Teacher Operations** | ✅ | ✅ | 📖¹ | ❌ | ❌ | ❌ |
| **Schedule Operations** | ✅ | ✅ | 📖 | 📖¹ | 📖¹ | 📖² |
| **Group Operations** | ✅ | ✅ | 📖 | 📖¹ | 📖¹ | ❌ |
| **Attendance Operations** | ✅ | ✅ | ✅³ | 📖¹ | 📖¹ | ❌ |
| **Score Operations** | ✅ | ✅ | ✅³ | 📖¹ | 📖¹ | ❌ |
| **Salary Operations** | ✅ | ✅ | 📖¹ | ❌ | ❌ | ❌ |
| **Statistics Dashboard** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **AI Agent** | ✅⁴ | ✅⁵ | ✅⁶ | ✅¹ | ✅¹ | ✅² |

**Legend:**
- ✅ Full Access (Create, Read, Update, Delete)
- 📝 Limited Write Access (Update own information)
- 📖 Read-Only Access
- ❌ No Access
- ¹ Own information only
- ² Basic/General information only
- ³ For own classes/students only
- ⁴ Full information access with no restrictions
- ⁵ All group/fee information, limited personal data
- ⁶ Students in their classes, class schedules, basic fees

---

## Security Best Practices

### 1. **Token Security**
```javascript
// Secure token storage (consider using secure cookies for production)
const secureStorage = {
  setToken: (token) => {
    // Use httpOnly cookies in production
    localStorage.setItem('authToken', token);
  },
  
  getToken: () => {
    return localStorage.getItem('authToken');
  },
  
  removeToken: () => {
    localStorage.removeItem('authToken');
  }
};
```

### 2. **Client-Side Validation**
```javascript
// Always validate user permissions before showing UI elements
const hasPermission = (userRole, requiredRoles) => {
  if (!userRole) return false;
  return requiredRoles.includes(userRole);
};

// Example usage
if (hasPermission(userRole, ['ADMIN', 'MANAGER'])) {
  // Show admin/manager specific UI
}
```

### 3. **Server-Side Validation**
**⚠️ Important:** Client-side role checks are for UI purposes only. Always rely on server-side validation for security. The backend validates all requests using Spring Security annotations.

---

## Common Frontend Patterns

### 1. **Navigation Menu Based on Role**
```jsx
const NavigationMenu = ({ userRole }) => {
  const menuItems = [];
  
  // Everyone can see dashboard
  menuItems.push({ path: '/dashboard', label: 'Dashboard' });
  
  // Admin/Manager specific items
  if (['ADMIN', 'MANAGER'].includes(userRole)) {
    menuItems.push({ path: '/statistics', label: 'Statistics' });
    menuItems.push({ path: '/groups', label: 'Manage Groups' });
  }
  
  // Admin only items
  if (userRole === 'ADMIN') {
    menuItems.push({ path: '/users', label: 'User Management' });
    menuItems.push({ path: '/roles', label: 'Role Management' });
  }
  
  // Teacher specific items
  if (userRole === 'TEACHER') {
    menuItems.push({ path: '/my-classes', label: 'My Classes' });
    menuItems.push({ path: '/my-salary', label: 'My Salary' });
  }
  
  return (
    <nav>
      {menuItems.map(item => (
        <Link key={item.path} to={item.path}>{item.label}</Link>
      ))}
    </nav>
  );
};
```

### 2. **Data Filtering Based on Role**
```javascript
// Filter data based on user role and permissions
const filterDataByRole = (data, userRole, userId) => {
  switch (userRole) {
    case 'ADMIN':
    case 'MANAGER':
      return data; // Return all data
      
    case 'TEACHER':
      // Return only data related to teacher's classes
      return data.filter(item => item.teacherId === userId);
      
    case 'STUDENT':
      // Return only student's own data
      return data.filter(item => item.studentId === userId);
      
    case 'PARENT':
      // Return only data for parent's children
      return data.filter(item => 
        item.studentId && parentChildIds.includes(item.studentId)
      );
      
    default:
      return []; // PUBLIC or unknown role
  }
};
```

### 3. **Feature Flags Based on Role**
```javascript
// Define feature availability by role
const features = {
  canCreateStudents: ['ADMIN', 'MANAGER'],
  canViewSalaries: ['ADMIN', 'MANAGER', 'TEACHER'],
  canManageRoles: ['ADMIN'],
  canViewStatistics: ['ADMIN', 'MANAGER'],
  canMarkAttendance: ['ADMIN', 'MANAGER', 'TEACHER'],
  canViewOwnData: ['STUDENT', 'PARENT', 'TEACHER'],
};

// Check feature availability
const canAccess = (feature, userRole) => {
  return features[feature]?.includes(userRole) || false;
};

// Usage
if (canAccess('canCreateStudents', userRole)) {
  // Show create student button
}
```

---

## Testing Role-Based Features

### 1. **Test Data Setup**
```javascript
// Mock users for testing different roles
const mockUsers = {
  admin: {
    id: 1,
    email: 'admin@chemist.edu',
    role: 'ADMIN',
    token: 'mock_admin_token'
  },
  manager: {
    id: 2,
    email: 'manager@chemist.edu',
    role: 'MANAGER',
    token: 'mock_manager_token'
  },
  teacher: {
    id: 3,
    email: 'teacher@chemist.edu',
    role: 'TEACHER',
    token: 'mock_teacher_token'
  },
  student: {
    id: 4,
    email: 'student@chemist.edu',
    role: 'STUDENT',
    token: 'mock_student_token'
  }
};
```

### 2. **Unit Tests for Role Functions**
```javascript
// Test role-based access control functions
describe('Role-based Access Control', () => {
  test('ADMIN should have access to all features', () => {
    expect(hasPermission('ADMIN', ['ADMIN'])).toBe(true);
    expect(hasPermission('ADMIN', ['ADMIN', 'MANAGER'])).toBe(true);
    expect(canAccess('canManageRoles', 'ADMIN')).toBe(true);
  });
  
  test('TEACHER should not have admin privileges', () => {
    expect(hasPermission('TEACHER', ['ADMIN'])).toBe(false);
    expect(canAccess('canManageRoles', 'TEACHER')).toBe(false);
  });
  
  test('PUBLIC should have minimal access', () => {
    expect(hasPermission('PUBLIC', ['ADMIN', 'MANAGER'])).toBe(false);
    expect(canAccess('canViewStatistics', 'PUBLIC')).toBe(false);
  });
});
```

---

## Troubleshooting Common Issues

### 1. **403 Forbidden Errors**
- **Cause**: User role doesn't have permission for the requested endpoint
- **Solution**: Check user role and endpoint requirements in this documentation
- **Frontend Action**: Hide UI elements that user cannot access

### 2. **401 Unauthorized Errors**
- **Cause**: Missing, expired, or invalid JWT token
- **Solution**: Redirect to login page and clear stored token
- **Frontend Action**: Implement automatic token refresh or re-authentication

### 3. **Role Not Detected**
- **Cause**: JWT token doesn't contain role information or role extraction failed
- **Solution**: Ensure token contains `authorities` claim with `ROLE_` prefix
- **Frontend Action**: Treat as `PUBLIC` user and limit access accordingly

### 4. **UI Shows Wrong Permissions**
- **Cause**: Client-side role detection differs from server-side role
- **Solution**: Always sync with server-side role validation
- **Frontend Action**: Implement role verification endpoint call after login

---

## Support and Contact

For frontend implementation support or questions about role-based access control:

1. **Review API Documentation**: Check `/docs/INSTRUCTION.md` for endpoint specifications
2. **Test with Postman**: Use provided examples to verify backend behavior
3. **Check Server Logs**: Backend logs contain detailed authorization information
4. **AI Agent Testing**: Use `/api/v1/ai/chat` endpoints to test role-based responses

**Remember**: Always implement defense in depth - client-side role checks for UI enhancement, server-side validation for security enforcement.