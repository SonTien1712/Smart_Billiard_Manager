import React from 'react';
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
  User
} from 'lucide-react';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';


export function AppSidebar({ currentPage, onPageChange }) {
  const { user } = useAuth();
  const { state } = useSidebar();

  const getInitials = (firstName, lastName, user) => {
    const base =
      [firstName, lastName].filter(Boolean).join(' ').trim() ||
      user?.username ||
      user?.email ||
      'U';
    const parts = base.trim().split(/\s+/);
    const a = parts[0]?.[0] || 'U';
    const b = parts[1]?.[0] || '';
    return `${a}${b}`.toUpperCase();
  };

  const getFullName = (firstName, lastName, user) => {
    const name = [firstName, lastName].filter(Boolean).join(' ').trim();
    return name || user?.username || user?.email || 'Guest';
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
              { title: 'Dashboard', page: 'admin-dashboard', icon: LayoutDashboard },
            ]
          },
          {
            title: 'Management',
            items: [
              { title: 'Customers', page: 'customer-list', icon: Users },
              { title: 'Create Admin', page: 'create-admin', icon: UserPlus },
            ]
          }
        ];

      case 'CUSTOMER':
        return [
          {
            title: 'Overview',
            items: [
              { title: 'Dashboard', page: 'customer-dashboard', icon: LayoutDashboard },
            ]
          },
          {
            title: 'Club Management',
            items: [
              { title: 'Clubs', page: 'clubs', icon: Building },
              { title: 'Tables', page: 'tables', icon: Table },
            ]
          },
          {
            title: 'Staff Management',
            items: [
              { title: 'Staff', page: 'staff', icon: UserCheck },
              { title: 'Work Shifts', page: 'shifts' , icon: Calendar },
              { title: 'Staff Accounts', page: 'staff-accounts', icon: UserCog },
            ]
          },
          {
            title: 'Business',
            items: [
              { title: 'Promotions', page: 'promotions', icon: Percent },
              { title: 'Products', page: 'products', icon: Package },
            ]
          }
        ];

      case 'STAFF':
        return [
          {
            title: 'Overview',
            items: [
              { title: 'Dashboard', page: 'staff-dashboard', icon: LayoutDashboard },
            ]
          },
          {
            title: 'Operations',
            items: [
              { title: 'Bill Management', page: 'bills', icon: Receipt },
              { title: 'Work Schedule', page: 'schedule', icon: ClipboardList },
              { title: 'Attendance', page: 'attendance', icon: Clock },
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
        <div className="flex items-center gap-2 px-4 py-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground shadow-sm">
            <Gamepad2 className="h-5 w-5" />
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
                  <SidebarMenuItem key={item.page}>
                    <SidebarMenuButton 
                      onClick={() => onPageChange(item.page)}
                      isActive={currentPage === item.page}
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
            <AvatarImage src={user?.avatar} alt={user ? getFullName(user?.firstName, user?.lastName, user) : ''} />
            <AvatarFallback className="rounded-lg bg-primary text-primary-foreground">
              {user ? getInitials(user?.firstName, user?.lastName, user) : <User className="h-4 w-4" />}
            </AvatarFallback>
          </Avatar>
          <div className="grid flex-1 text-left text-sm leading-tight">
            <span className="truncate font-semibold text-sidebar-foreground">
              {user ? getFullName(user?.firstName, user?.lastName, user) : 'Guest'}
            </span>
            <span className="truncate text-xs text-sidebar-foreground/70">
              {user?.role && getRoleDisplayName(user.role)}
            </span>
          </div>
        </div>
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}
