import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { SidebarProvider, SidebarInset } from './ui/sidebar';
import { AppSidebar } from './layout/AppSidebar';
import { Header } from './layout/Header';
import { useAuth } from './AuthProvider';

/**
 * @typedef {'admin-dashboard' | 'customer-list' | 'customer-details' | 'create-admin' |
 *           'customer-dashboard' | 'clubs' | 'tables' | 'staff' | 'shifts' | 'staff-accounts' | 'promotions' | 'products' |
 *           'staff-dashboard' | 'bills' | 'work' | 'payroll'} PageType
 */

/**
 * Dashboard component that provides the main layout and navigation
 * Now uses React Router for nested routing instead of state-based navigation
 */
export const PageType = null;

export function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  /**
   * Handle navigation for external routes (signin, signup, etc.)
   * @param {string} page - Target page/route
   */
  const handleNavigate = (page) => {
    switch (page) {
      case 'signin':
        navigate('/signin');
        break;
      case 'signup':
        navigate('/signup');
        break;
      case 'forgot-password':
        navigate('/forgot-password');
        break;
      case 'profile':
        navigate('/profile');
        break;
      case 'dashboard':
        // Navigate to appropriate dashboard based on role
        const rolePath = user?.role?.toLowerCase();
        navigate(`/dashboard/${rolePath}`);
        break;
      default:
        navigate('/dashboard');
    }
  };

  /**
   * Handle page change for internal routing
   * @param {string} page - Target page within dashboard
   */
  const handlePageChange = (page) => {
    const role = user?.role?.toLowerCase();

    // Map old page types to new route paths
    const routeMap = {
      // Admin pages
      'admin-dashboard': `/dashboard/admin`,
      'customer-list': `/dashboard/admin/customers`,
      'customer-details': `/dashboard/admin/customers/${location.pathname.split('/').pop()}`, // Keep customer ID
      'create-admin': `/dashboard/admin/create-admin`,

      // Customer pages
      'customer-dashboard': `/dashboard/customer`,
      'clubs': `/dashboard/customer/clubs`,
      'tables': `/dashboard/customer/tables`,
      'staff': `/dashboard/customer/staff`,
      'shifts': `/dashboard/customer/shifts`,
      'staff-accounts': `/dashboard/customer/staff-accounts`,
      'promotions': `/dashboard/customer/promotions`,
      'products': `/dashboard/customer/products`,

      // Staff pages
      'staff-dashboard': `/dashboard/staff`,
      'bills': `/dashboard/staff/bills`,
      'work': `/dashboard/staff/work`,
      'payroll': `/dashboard/staff/payroll`,
    };

    const route = routeMap[page];
    if (route) {
      navigate(route);
    } else {
      // Default fallback
      navigate(`/dashboard/${role}`);
    }
  };

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <Header />
        <main className="flex-1 overflow-y-auto felt-bg p-4 md:p-6">
          <div className="mx-auto max-w-7xl">
            {/* Outlet renders the matched child route component */}
            <Outlet />
          </div>
        </main>
      </SidebarInset>
    </SidebarProvider>
  );
}
