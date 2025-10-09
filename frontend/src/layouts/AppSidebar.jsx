import React from "react"
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem
} from "../components/ui/sidebar"
import { useAuth } from "../components/ui/AuthProvider"
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
  Clock
} from "lucide-react"

export function AppSidebar({ currentPage, onPageChange }) {
  const { user } = useAuth()

  const getMenuItems = () => {
    switch (user?.role) {
      case "admin":
        return [
          {
            title: "Overview",
            items: [
              {
                title: "Dashboard",
                page: "admin-dashboard",
                icon: LayoutDashboard
              }
            ]
          },
          {
            title: "Management",
            items: [
              { title: "Customers", page: "customer-list", icon: Users },
              { title: "Create Admin", page: "create-admin", icon: UserPlus }
            ]
          }
        ]

      case "customer":
        return [
          {
            title: "Overview",
            items: [
              {
                title: "Dashboard",
                page: "customer-dashboard",
                icon: LayoutDashboard
              }
            ]
          },
          {
            title: "Club Management",
            items: [
              { title: "Clubs", page: "clubs", icon: Building },
              { title: "Tables", page: "tables", icon: Table }
            ]
          },
          {
            title: "Staff Management",
            items: [
              { title: "Staff", page: "staff", icon: UserCheck },
              { title: "Work Shifts", page: "shifts", icon: Calendar },
              { title: "Staff Accounts", page: "staff-accounts", icon: UserCog }
            ]
          },
          {
            title: "Business",
            items: [
              { title: "Promotions", page: "promotions", icon: Percent },
              { title: "Products", page: "products", icon: Package }
            ]
          }
        ]

      case "staff":
        return [
          {
            title: "Overview",
            items: [
              {
                title: "Dashboard",
                page: "staff-dashboard",
                icon: LayoutDashboard
              }
            ]
          },
          {
            title: "Operations",
            items: [
              { title: "Bill Management", page: "bills", icon: Receipt },
              { title: "Work Schedule", page: "schedule", icon: ClipboardList },
              { title: "Attendance", page: "attendance", icon: Clock }
            ]
          }
        ]

      default:
        return []
    }
  }

  const menuItems = getMenuItems()

  return (
    <Sidebar>
      <SidebarContent>
        {menuItems.map(group => (
          <SidebarGroup key={group.title}>
            <SidebarGroupLabel>{group.title}</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {group.items.map(item => (
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
    </Sidebar>
  )
}
