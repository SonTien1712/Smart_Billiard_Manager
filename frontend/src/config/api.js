// API Configuration for Spring Boot backend
export const API_CONFIG = {
  BASE_URL: 'http://localhost:8080/api', // In production, this would come from environment variables
  ENDPOINTS: {
    // Authentication endpoints
    AUTH: {
      LOGIN: '/auth/login',
      REGISTER: '/auth/register',
      LOGOUT: '/auth/logout',
      REFRESH: '/auth/refresh',
      FORGOT_PASSWORD: '/auth/forgot-password',
      RESET_PASSWORD: '/auth/reset-password',
      PROFILE: '/auth/profile',
      GOOGLE_AUTH: '/auth/google'
    },
    // Admin endpoints
    ADMIN: {
      CUSTOMERS: '/admin/customers',
      ADMINS: '/admin/admins',
      STATISTICS: '/admin/statistics'
    },
    // Customer endpoints
    CUSTOMER: {
      CLUBS: '/customer/clubs',
      CLUBS_BY_CUSTOMER: (customerId) => `/customer/clubs/customer/${customerId}`,
      TABLES_BY_CUSTOMER: (customerId) => `/tables/customer/${customerId}`,
      TABLES: '/tables',
      STAFF: '/customer/staff',
      SHIFTS: '/customer/shifts',
      PROMOTIONS: '/customer/promotions',
      PRODUCTS: '/customer/products',
      STAFF_ACCOUNTS: '/customer/staff-accounts'
    },
    // Staff endpoints
    STAFF: {
      BILLS: '/staff/bills',
      SCHEDULE: '/staff/schedule',
      ATTENDANCE: '/staff/attendance',
      TABLES: '/staff/tables'
    }
  }
};

// HTTP status codes
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500
};