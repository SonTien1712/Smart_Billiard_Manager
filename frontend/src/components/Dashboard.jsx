import React, { useState } from 'react';
import { SidebarProvider, SidebarInset } from './ui/sidebar';
import { AppSidebar } from './layout/AppSidebar';
import { Header } from './layout/Header';
import { useAuth } from './AuthProvider';

// Role-specific dashboard components
import { AdminDashboard } from './dashboards/AdminDashboard';
import { CustomerDashboard } from './dashboards/CustomerDashboard';
import { StaffDashboard } from './dashboards/StaffDashboard';

// Page components
import { CustomerList } from './admin/CustomerList';
import { CustomerDetails } from './admin/CustomerDetails';
import { CreateAdmin } from './admin/CreateAdmin';

import { ClubManagement } from './customer/ClubManagement';
import { TableManagement } from './customer/TableManagement';
import { StaffManagement } from './customer/StaffManagement';
import { ShiftManagement } from './customer/ShiftManagement';
import { StaffAccountManagement } from './customer/StaffAccountManagement';
import { PromotionManagement } from './customer/PromotionManagement';
import { ProductManagement } from './customer/ProductManagement';

import { BillManagement } from './staff/BillManagement';
import { WorkAndAttendance } from './staff/WorkAndAttendance';
import { Payroll } from './staff/Payroll';

/**
 * @typedef {'admin-dashboard' | 'customer-list' | 'customer-details' | 'create-admin' | 
 *           'customer-dashboard' | 'clubs' | 'tables' | 'staff' | 'shifts' | 'staff-accounts' | 'promotions' | 'products' |
 *           'staff-dashboard' | 'bills' | 'work' | 'payroll'} PageType
 */

/**
 * Dashboard component that manages the main application interface
 * @param {Object} props
 * @param {function('signin' | 'signup' | 'forgot-password' | 'profile' | 'dashboard'): void} props.onNavigate - Navigation callback
 */
export const PageType = null;

export function Dashboard({ onNavigate }) {
  const { user } = useAuth();
  const [currentPage, setCurrentPage] = useState(() => {
    switch (user?.role) {
      case 'ADMIN': return 'admin-dashboard';
      case 'CUSTOMER': return 'customer-dashboard';
      case 'STAFF': return 'staff-dashboard';
      default: return 'admin-dashboard';
    }
  });
  const [selectedCustomerId, setSelectedCustomerId] = useState(null);

  /**
   * Handle page navigation
   * @param {PageType} page
   */
  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  /**
   * Handle customer selection
   * @param {string} customerId
   */
  const handleCustomerSelect = (customerId) => {
    setSelectedCustomerId(customerId);
    setCurrentPage('customer-details');
  };

  const renderContent = () => {
    switch (currentPage) {
      // Admin pages
      case 'admin-dashboard':
        return <AdminDashboard onPageChange={handlePageChange} />;
      case 'customer-list':
        return <CustomerList onCustomerSelect={handleCustomerSelect} onPageChange={handlePageChange} />;
      case 'customer-details':
        return <CustomerDetails customerId={selectedCustomerId} onPageChange={handlePageChange} />;
      case 'create-admin':
        return <CreateAdmin onPageChange={handlePageChange} />;

      // Customer pages
      case 'customer-dashboard':
        return <CustomerDashboard onPageChange={handlePageChange} />;
      case 'clubs':
        return <ClubManagement onPageChange={handlePageChange} />;
      case 'tables':
        return <TableManagement onPageChange={handlePageChange} />;
      case 'staff':
        return <StaffManagement onPageChange={handlePageChange} />;
      case 'shifts':
        return <ShiftManagement onPageChange={handlePageChange} />;
      case 'staff-accounts':
        return <StaffAccountManagement onPageChange={handlePageChange} />;
      case 'promotions':
        return <PromotionManagement onPageChange={handlePageChange} />;
      case 'products':
        return <ProductManagement onPageChange={handlePageChange} />;

      // Staff pages
      case 'staff-dashboard':
        return <StaffDashboard onPageChange={handlePageChange} />;
      case 'bills':
        return <BillManagement onPageChange={handlePageChange} />;
      case 'work':
        return <WorkAndAttendance />;
      case 'payroll':
        return <Payroll />;

      default:
        return <div>Page not found</div>;
    }
  };

  return (
    <SidebarProvider>
      <AppSidebar currentPage={currentPage} onPageChange={handlePageChange} onNavigate={onNavigate} />
      <SidebarInset>
        <Header />
        <main className="flex-1 overflow-y-auto felt-bg p-4 md:p-6">
          <div className="mx-auto max-w-7xl">
            {renderContent()}
          </div>
        </main>
      </SidebarInset>
    </SidebarProvider>
  );
}
