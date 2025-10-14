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
import { WorkSchedule } from './staff/WorkSchedule';
import { AttendanceTracking } from './staff/AttendanceTracking';

export type PageType = 
  // Admin pages
  | 'admin-dashboard' | 'customer-list' | 'customer-details' | 'create-admin'
  // Customer pages  
  | 'customer-dashboard' | 'clubs' | 'tables' | 'staff' | 'shifts' | 'staff-accounts' | 'promotions' | 'products'
  // Staff pages
  | 'staff-dashboard' | 'bills' | 'schedule' | 'attendance';

interface DashboardProps {
  onNavigate: (page: 'signin' | 'signup' | 'forgot-password' | 'profile' | 'dashboard') => void;
}

export function Dashboard({ onNavigate }: DashboardProps) {
  const { user } = useAuth();
  const [currentPage, setCurrentPage] = useState<PageType>(() => {
    switch (user?.role) {
      case 'ADMIN': return 'admin-dashboard';
      case 'CUSTOMER': return 'customer-dashboard';
      case 'STAFF': return 'staff-dashboard';
      default: return 'admin-dashboard';
    }
  });
  const [selectedCustomerId, setSelectedCustomerId] = useState<string | null>(null);

  const handlePageChange = (page: PageType) => {
    setCurrentPage(page);
  };

  const handleCustomerSelect = (customerId: string) => {
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
      case 'schedule':
        return <WorkSchedule onPageChange={handlePageChange} />;
      case 'attendance':
        return <AttendanceTracking onPageChange={handlePageChange} />;

      default:
        return <div>Page not found</div>;
    }
  };

  return (
    <SidebarProvider>
      <AppSidebar currentPage={currentPage} onPageChange={handlePageChange} />
      <SidebarInset>
        <Header onNavigate={onNavigate} />
        <main className="flex-1 overflow-y-auto bg-muted/30 p-4 md:p-6">
          <div className="mx-auto max-w-7xl">
            {renderContent()}
          </div>
        </main>
      </SidebarInset>
    </SidebarProvider>
  );
}