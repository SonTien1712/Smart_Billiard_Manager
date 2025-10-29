// Routes configuration moved to routes.jsx to avoid lazy loading issues
// This file is kept for backward compatibility

/**
 * Route configuration for the application
 * Uses lazy loading for code splitting and better performance
 */
export const routes = [
  // Public routes (no authentication required)
  {
    path: '/signin',
    component: SignIn,
    public: true,
  },
  {
    path: '/signup',
    component: SignUp,
    public: true,
  },
  {
    path: '/forgot-password',
    component: ForgotPassword,
    public: true,
  },
  {
    path: '/profile',
    component: ProfileUpdate,
    public: false, // Requires authentication
  },

  // Protected routes (require authentication)
  {
    path: '/dashboard',
    component: Dashboard,
    public: false,
  },
  // Admin routes
  {
    path: '/dashboard/admin',
    component: AdminDashboard,
    roles: ['ADMIN'],
  },
  {
    path: '/dashboard/admin/customers',
    component: CustomerList,
    roles: ['ADMIN'],
  },
  {
    path: '/dashboard/admin/customers/:customerId',
    component: CustomerDetails,
    roles: ['ADMIN'],
  },
  {
    path: '/dashboard/admin/create-admin',
    component: CreateAdmin,
    roles: ['ADMIN'],
  },

  // Customer routes
  {
    path: '/dashboard/customer',
    component: CustomerDashboard,
    roles: ['CUSTOMER'],
  },
  {
    path: '/dashboard/customer/clubs',
    component: ClubManagement,
    roles: ['CUSTOMER'],
  },
  {
    path: '/dashboard/customer/tables',
    component: TableManagement,
    roles: ['CUSTOMER'],
  },
  {
    path: '/dashboard/customer/staff',
    component: StaffManagement,
    roles: ['CUSTOMER'],
  },
  {
    path: '/dashboard/customer/shifts',
    component: ShiftManagement,
    roles: ['CUSTOMER'],
  },
  {
    path: '/dashboard/customer/staff-accounts',
    component: StaffAccountManagement,
    roles: ['CUSTOMER'],
  },
  {
    path: '/dashboard/customer/promotions',
    component: PromotionManagement,
    roles: ['CUSTOMER'],
  },
  {
    path: '/dashboard/customer/products',
    component: ProductManagement,
    roles: ['CUSTOMER'],
  },

  // Staff routes
  {
    path: '/dashboard/staff',
    component: StaffDashboard,
    roles: ['STAFF'],
  },
  {
    path: '/dashboard/staff/bills',
    component: BillManagement,
    roles: ['STAFF'],
  },
  {
    path: '/dashboard/staff/work',
    component: WorkAndAttendance,
    roles: ['STAFF'],
  },
  {
    path: '/dashboard/staff/payroll',
    component: Payroll,
    roles: ['STAFF'],
  },

  // Default route - redirect to dashboard if logged in, otherwise signin
  {
    path: '/',
    redirect: (user) => user ? '/dashboard' : '/signin',
  },
];

/**
 * Helper function to check if user has required role for a route
 * @param {string[]} roles - Required roles for the route
 * @param {Object} user - Current user object
 * @returns {boolean} - Whether user has access
 */
export const hasAccess = (roles, user) => {
  if (!roles || roles.length === 0) return true;
  if (!user || !user.role) return false;
  return roles.includes(user.role);
};

/**
 * Helper function to get default route for user role
 * @param {string} role - User role
 * @returns {string} - Default route path
 */
export const getDefaultRoute = (role) => {
  switch (role) {
    case 'ADMIN':
      return '/dashboard/admin';
    case 'CUSTOMER':
      return '/dashboard/customer';
    case 'STAFF':
      return '/dashboard/staff';
    default:
      return '/signin';
  }
};

/**
 * Helper function to validate route access
 * @param {Object} route - Route configuration
 * @param {Object} user - Current user object
 * @returns {boolean} - Whether route is accessible
 */
export const isRouteAccessible = (route, user) => {
  // Public routes are accessible without authentication
  if (route.public) return true;

  // Private routes require authentication
  if (!user) return false;

  // Check role-based access
  if (route.roles && !hasAccess(route.roles, user)) return false;

  return true;
};

export default routes;