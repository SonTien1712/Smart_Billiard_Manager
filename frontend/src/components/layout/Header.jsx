import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from '../ui/dropdown-menu';
import { SidebarTrigger } from '../ui/sidebar';
import { useAuth } from '../AuthProvider';
import { User, Settings, LogOut } from 'lucide-react';


export function Header() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/signin');
  };

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

  return (
    <header className="flex h-16 shrink-0 items-center justify-between border-b bg-background px-4 md:px-6">
      <div className="flex items-center space-x-4">
        <SidebarTrigger className="-ml-1" />
        <div className="hidden sm:block">
          <h1 className="text-lg md:text-xl font-semibold text-foreground">Billiards Club Management</h1>
        </div>
      </div>

      <div className="flex items-center space-x-4">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="relative h-10 w-10 rounded-full">
              <Avatar className="h-10 w-10">
                <AvatarImage src={user?.avatar} alt={user ? getFullName(user?.firstName, user?.lastName, user) : ''} />
                <AvatarFallback className="bg-primary text-primary-foreground">
                  {user ? getInitials(user?.firstName, user?.lastName, user) : <User className="h-4 w-4" />}
                </AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent className="w-56" align="end" forceMount>
            <DropdownMenuLabel className="font-normal">
              <div className="flex flex-col space-y-1">
                <p className="text-sm font-medium leading-none">{user ? getFullName(user?.firstName, user?.lastName, user) : ''}</p>
                <p className="text-xs leading-none text-muted-foreground">{user?.email}</p>
                <p className="text-xs leading-none text-muted-foreground">
                  {user?.role && getRoleDisplayName(user.role)}
                </p>
              </div>
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={() => navigate('/profile')}>
              <User className="mr-2 h-4 w-4" />
              <span>Profile</span>
            </DropdownMenuItem>
            <DropdownMenuItem>
              <Settings className="mr-2 h-4 w-4" />
              <span>Settings</span>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={handleLogout}>
              <LogOut className="mr-2 h-4 w-4" />
              <span>Log out</span>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}