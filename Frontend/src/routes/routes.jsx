import React, { Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../components/AuthProvider';
import { Toaster } from '../components/ui/sonner.jsx';
import  Landing  from '../components/prelogin/Landing.jsx';

// Import components directly instead of lazy loading to avoid the conversion error
import { SignIn } from '../components/auth/SignIn.jsx';
import { SignUp } from '../components/auth/SignUp.jsx';
import { ForgotPassword } from '../components/auth/ForgotPassword.jsx';
import { ProfileUpdate } from '../components/auth/ProfileUpdate.jsx';
import { Dashboard } from '../components/Dashboard.jsx';

// Admin Components
import { CustomerList } from '../components/admin/CustomerList.jsx';
import { CustomerDetails } from '../components/admin/CustomerDetails.jsx';
import { CreateAdmin } from '../components/admin/CreateAdmin.jsx';

 // Customer Components
 import { Premium } from '../components/auth/Premium.jsx';
import { ClubManagement } from '../components/customer/ClubManagement.jsx';
import { TableManagement } from '../components/customer/TableManagement.jsx';
import { StaffManagement } from '../components/customer/StaffManagement.jsx';
import { ShiftManagement } from '../components/customer/ShiftManagement.jsx';
import { StaffAccountManagement } from '../components/customer/StaffAccountManagement.jsx';
import { PromotionManagement } from '../components/customer/PromotionManagement.jsx';
import { ProductManagement } from '../components/customer/ProductManagement.jsx';

// Staff Components
import { BillManagement } from '../components/staff/BillManagement.jsx';
import { WorkAndAttendance } from '../components/staff/WorkAndAttendance.jsx';
import { Payroll } from '../components/staff/Payroll.jsx';

// Dashboard Components
import { AdminDashboard } from '../components/dashboards/AdminDashboard.jsx';
import CustomerDashboard from '../components/dashboards/CustomerDashboard.jsx';
import { StaffDashboard } from '../components/dashboards/StaffDashboard.jsx';

// Define routes inline to avoid lazy loading issues
const routes = [
  // Public routes (no authentication required)
  {
    path: '/landing',
    component: Landing,
    public: true,
  },
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
    path: '/premium',
    component: Premium,
    roles: ['CUSTOMER'],
  },
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
    redirect: (user) => user ? '/dashboard' : '/landing',
  },
];

/**
 * Helper function to check if user has required role for a route
 * @param {string[]} roles - Required roles for the route
 * @param {Object} user - Current user object
 * @returns {boolean} - Whether user has access
 */
const hasAccess = (roles, user) => {
  if (!roles || roles.length === 0) return true;
  if (!user || !user.role) return false;
  return roles.includes(user.role);
};

/**
 * Helper function to get default route for user role
 * @param {string} role - User role
 * @returns {string} - Default route path
 */
const getDefaultRoute = (role) => {
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
const isRouteAccessible = (route, user) => {
  // Public routes are accessible without authentication
  if (route.public) return true;

  // Private routes require authentication
  if (!user) return false;

  // Check role-based access
  if (route.roles && !hasAccess(route.roles, user)) return false;

  return true;
};

const isSubscriptionActive = (expiry) => {
  if (!expiry) return false; // chưa mua
  return new Date(expiry).getTime() >= new Date().getTime();
};


 

function ErrorBoundary({ children }) {
  const [hasError, setHasError] = React.useState(false);
  const [error, setError] = React.useState(null);

  React.useEffect(() => {
    const handleError = (error) => {
      console.error('Error caught by boundary:', error);
      setHasError(true);
      setError(error);
    };

    window.addEventListener('error', handleError);
    return () => window.removeEventListener('error', handleError);
  }, []);

  if (hasError) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <h2 className="text-xl font-semibold mb-2">Something went wrong</h2>
          <p className="text-muted-foreground mb-4">
            {error?.message || 'An unexpected error occurred'}
          </p>
          <button
            onClick={() => window.location.reload()}
            className="px-4 py-2 bg-primary text-primary-foreground rounded"
          >
            Reload Page
          </button>
        </div>
      </div>
    );
  }

  return children;
}

/**
 * Protected Route component that checks authentication and role access
 */
function ProtectedRoute({ route, children }) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  // If route requires authentication but user is not logged in
  if (!route.public && !user) {
    return <Navigate to="/signin" state={{ from: location }} replace />;
  }

  // If route has specific roles and user doesn't have access
  if (!isRouteAccessible(route, user)) {
    // Redirect to appropriate dashboard or signin
    const redirectPath = user ? getDefaultRoute(user.role) : '/signin';
    return <Navigate to={redirectPath} replace />;
  }

  return (
    <Suspense fallback={
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    }>
      {children}
    </Suspense>
  );
}

/**
 * Component to render routes with authentication context
 */
function RouteRenderer() {
  const { user } = useAuth();

  return (
    <Routes>
      {routes.map((route, index) => {
        if (user?.role === 'CUSTOMER') {
          const active = isSubscriptionActive(user?.expiryDate);
          console.log('Customer subscription check:', active, 'expiryDate:', user?.expiryDate);
          if (!active) {
            if (route.path !== '/premium') {
              return (
                <Route
                  key={index}
                  path={route.path}
                  element={<Navigate to="/premium" replace />}
                />
              );
            }
          } else if (route.path === '/premium') {
            console.log('Redirecting to /dashboard/customer');
            return (
              <Route
                key={index}
                path={route.path}
                element={<Navigate to="/dashboard/customer" replace />}
              />
            );
          }
        }
        // Handle redirect routes
        if (route.redirect) {
          const redirectPath = route.redirect(user);
          return (
            <Route
              key={index}
              path={route.path}
              element={<Navigate to={redirectPath} replace />}
            />
          );
        }

        // Render route with component
        if (route.component) {
          const element = (
            <ProtectedRoute route={route}>
              <route.component />
            </ProtectedRoute>
          );

          // For dashboard route, wrap with Outlet for nested routes
          if (route.path === '/dashboard') {
            return (
              <Route key={index} path={route.path} element={element}>
                {/* Admin nested routes */}
                <Route path="admin" element={<AdminDashboard />} />
                <Route path="admin/customers" element={<CustomerList />} />
                <Route path="admin/customers/:customerId" element={<CustomerDetails />} />
                <Route path="admin/create-admin" element={<CreateAdmin />} />

                {/* Customer nested routes */}
                <Route path="customer" element={<CustomerDashboard />} />
                <Route path="customer/clubs" element={<ClubManagement />} />
                <Route path="customer/tables" element={<TableManagement />} />
                <Route path="customer/staff" element={<StaffManagement />} />
                <Route path="customer/shifts" element={<ShiftManagement />} />
                <Route path="customer/staff-accounts" element={<StaffAccountManagement />} />
                <Route path="customer/promotions" element={<PromotionManagement />} />
                <Route path="customer/products" element={<ProductManagement />} />

                {/* Staff nested routes */}
                <Route path="staff" element={<StaffDashboard />} />
                <Route path="staff/bills" element={<BillManagement />} />
                <Route path="staff/work" element={<WorkAndAttendance />} />
                <Route path="staff/payroll" element={<Payroll />} />

                {/* Default redirect based on role */}
                <Route
                  path=""
                  element={
                    user?.role === 'ADMIN' ? <Navigate to="/dashboard/admin" replace /> :
                    user?.role === 'CUSTOMER' ? <Navigate to="/dashboard/customer" replace /> :
                    user?.role === 'STAFF' ? <Navigate to="/dashboard/staff" replace /> :
                    <Navigate to="/signin" replace />
                  }
                />
              </Route>
            );
          }

          return (
            <Route key={index} path={route.path} element={element} />
          );
        }

        return null;
      })}
    </Routes>
  );
}

/**
 * Main App Router component
 */
function AppRouter() {
  return (
    <ErrorBoundary>
      <Router>
        <Suspense fallback={
          <div className="min-h-screen flex items-center justify-center bg-background">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        }>
          <RouteRenderer />
        </Suspense>
        <Toaster />
      </Router>
    </ErrorBoundary>
  );
}

export default AppRouter;
