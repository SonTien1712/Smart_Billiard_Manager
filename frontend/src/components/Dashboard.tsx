import React, { useState } from 'react';
// @ts-ignore
import { SidebarProvider, SidebarInset } from './ui/sidebar';
// @ts-ignore
import { AppSidebar } from './layout/AppSidebar';
// @ts-ignore
import { Header } from './layout/Header';
import { useAuth } from './AuthProvider';

// Role-specific dashboard components
// @ts-ignore
import { AdminDashboard } from './dashboards/AdminDashboard';
// @ts-ignore
import { CustomerDashboard } from './dashboards/CustomerDashboard';
// @ts-ignore
import { StaffDashboard } from './dashboards/StaffDashboard';

// Page components
// @ts-ignore
import { CustomerList } from './admin/CustomerList';
// @ts-ignore
import { CustomerDetails } from './admin/CustomerDetails';
// @ts-ignore
import { CreateAdmin } from './admin/CreateAdmin';

// @ts-ignore
import { ClubManagement } from './customer/ClubManagement';
// @ts-ignore
import { TableManagement } from './customer/TableManagement';
// @ts-ignore
import { StaffManagement } from './customer/StaffManagement';
// @ts-ignore
import { ShiftManagement } from './customer/ShiftManagement';
// @ts-ignore
import { StaffAccountManagement } from './customer/StaffAccountManagement';
// @ts-ignore
import { PromotionManagement } from './customer/PromotionManagement';
// @ts-ignore
import { ProductManagement } from './customer/ProductManagement';

// @ts-ignore
import { BillManagement } from './staff/BillManagement';
// @ts-ignore
import { WorkSchedule } from './staff/WorkSchedule';
// @ts-ignore
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