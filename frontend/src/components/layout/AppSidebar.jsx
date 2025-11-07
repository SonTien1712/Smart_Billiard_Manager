import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarRail,
  useSidebar,
} from '../ui/sidebar';
import { useAuth } from '../AuthProvider';
  import { 
    LayoutDashboard, 
    Users, 
    UserPlus, 
    Building, 
    Table, 
    UserCheck, 
    Calendar, 
    UserCog, 
    Percent, 
    Package,
    Receipt,
    ClipboardList,
    Clock,
    Gamepad2,
    User,
    DollarSign
  } from 'lucide-react';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { Button } from '../ui/button';
import { LogOut } from 'lucide-react';


export function AppSidebar({ onNavigate }) {
  const { user, logout } = useAuth();
  const { state } = useSidebar();
  const location = useLocation();
  const navigate = useNavigate();

  const getInitials = (user) => {
    let base = '';
    if (user?.fullName) base = user.fullName;
    else if (user?.customerName) base = user.customerName;
    else if (user?.name) base = user.name;
    else if (user?.username) base = user.username;
    else if (user?.email) base = user.email;
    else base = 'U';
    const parts = base.trim().split(/\s+/);
    const a = parts[0]?.[0] || 'U';
    const b = parts[1]?.[0] || '';
    return `${a}${b}`.toUpperCase();
  };

  const getFullName = (user) => {
    if (user?.fullName) return user.fullName;
    if (user?.customerName) return user.customerName;
    if (user?.name) return user.name;
    if (user?.username) return user.username;
    if (user?.email) return user.email;
    return 'Guest';
  };

  const getRoleDisplayName = (role) => {
    switch (role) {
      case 'ADMIN': return 'Administrator';
      case 'CUSTOMER': return 'Club Owner';
      case 'STAFF': return 'Staff Member';
      default: return role;
    }
  };

  const getMenuItems = () => {
    switch (user?.role) {
      case 'ADMIN':
        return [
          {
            title: 'Overview',
            items: [
              { title: 'Dashboard', path: '/dashboard/admin', icon: LayoutDashboard },
            ]
          },
          {
            title: 'Management',
            items: [
              { title: 'Customers', path: '/dashboard/admin/customers', icon: Users },
              { title: 'Create Admin', path: '/dashboard/admin/create-admin', icon: UserPlus },
            ]
          }
        ];

      case 'CUSTOMER':
        return [
          {
            title: 'Overview',
            items: [
              { title: 'Dashboard', path: '/dashboard/customer', icon: LayoutDashboard },
            ]
          },
          {
            title: 'Club Management',
            items: [
              { title: 'Clubs', path: '/dashboard/customer/clubs', icon: Building },
              { title: 'Tables', path: '/dashboard/customer/tables', icon: Table },
            ]
          },
          {
            title: 'Staff Management',
            items: [
              { title: 'Staff', path: '/dashboard/customer/staff', icon: UserCheck },
              { title: 'Work Shifts', path: '/dashboard/customer/shifts', icon: Calendar },
              { title: 'Staff Accounts', path: '/dashboard/customer/staff-accounts', icon: UserCog },
            ]
          },
          {
            title: 'Business',
            items: [
              { title: 'Promotions', path: '/dashboard/customer/promotions', icon: Percent },
              { title: 'Products', path: '/dashboard/customer/products', icon: Package },
            ]
          }
        ];

      case 'STAFF':
        return [
          {
            title: 'Overview',
            items: [
              { title: 'Dashboard', path: '/dashboard/staff', icon: LayoutDashboard },
            ]
          },
          {
            title: 'Operations',
            items: [
              { title: 'Bill Management', path: '/dashboard/staff/bills', icon: Receipt },
              { title: 'Work & Attendance', path: '/dashboard/staff/work', icon: Clock },
              { title: 'Payroll', path: '/dashboard/staff/payroll', icon: DollarSign },
            ]
          }
        ];

      default:
        return [];
    }
  };

  const menuItems = getMenuItems();

  return (
    <Sidebar>
      <SidebarHeader className="border-b border-sidebar-border">
        <div className="flex items-center gap-3 px-4 py-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-black text-white shadow-sm ring-1 ring-white/10">
            <span className="text-sm font-semibold">8</span>
          </div>
          <div className="grid flex-1 text-left text-sm leading-tight">
            <span className="truncate font-semibold text-sidebar-foreground">Billiards Club</span>
            <span className="truncate text-xs text-sidebar-foreground/70">Management System</span>
          </div>
        </div>
      </SidebarHeader>
      <SidebarContent>
        {menuItems.map((group) => (
          <SidebarGroup key={group.title}>
            <SidebarGroupLabel>{group.title}</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {group.items.map((item) => (
                  <SidebarMenuItem key={item.path}>
                    <SidebarMenuButton
                      onClick={() => navigate(item.path)}
                      isActive={location.pathname === item.path}
                    >
                      <item.icon className="h-4 w-4" />
                      <span>{item.title}</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                ))}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>
        ))}
      </SidebarContent>
      <SidebarFooter>
        <div className="flex items-center gap-2 px-2 py-1">
          <Avatar className="h-8 w-8 rounded-lg">
            <AvatarImage src={user?.avatar} alt={user ? getFullName(user) : ''} />
            <AvatarFallback className="rounded-lg bg-primary text-primary-foreground">
              {user ? getInitials(user) : <User className="h-4 w-4" />}
            </AvatarFallback>
          </Avatar>
          <div className="grid flex-1 text-left text-sm leading-tight">
            <span className="truncate font-semibold text-sidebar-foreground">
              {user ? getFullName(user) : 'Guest'}
            </span>
            <span className="truncate text-xs text-sidebar-foreground/70">
              {user?.role && getRoleDisplayName(user.role)}
            </span>
          </div>
        </div>
        <div className="px-2 pb-2 grid grid-cols-2 gap-2">
          <Button variant="outline" size="sm" onClick={() => navigate('/profile')}>
            <User className="h-4 w-4 mr-1" /> Profile
          </Button>
          <Button variant="destructive" size="sm" onClick={() => { logout(); navigate('/signin'); }}>
            <LogOut className="h-4 w-4 mr-1" /> Logout
          </Button>
        </div>
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}
