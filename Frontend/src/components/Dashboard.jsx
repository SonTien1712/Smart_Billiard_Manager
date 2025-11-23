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
