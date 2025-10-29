import React, { useState, useMemo, useEffect, useCallback  } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Switch } from '../ui/switch';
import { Search, MoreHorizontal, Eye, UserX, UserCheck } from 'lucide-react';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '../ui/dropdown-menu';

import { useApi } from '../../hooks/useApi';
import { adminService } from '../../services/adminService';

export function CustomerList() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sort] = useState('dateJoined,desc');

  const fetchCustomers = useCallback(
    () => adminService.getCustomers({ page, size, sort }),
    [page, size, sort]
  );

  const {
    data: customerPage,
    loading,
    error,
    execute: loadCustomers,
  } = useApi(fetchCustomers);

  useEffect(() => {
    loadCustomers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size, sort]);

  const rows = useMemo(() => {
    const list = Array.isArray(customerPage?.content)
      ? customerPage.content
      : (Array.isArray(customerPage) ? customerPage : []);

    return list
      .map(c => ({
        id: c.id ?? c.customerId ?? String(Math.random()),
        name: c.name ?? c.fullName ?? c.customerName ?? 'N/A',
        email: c.email ?? 'N/A',
        phone: c.phone ?? c.phoneNumber ?? '',
        status: (c.isActive ?? c.active ?? c.enabled) ? 'active' : 'inactive',
        joinDate: c.dateJoined
          ? new Date(c.dateJoined).toISOString().slice(0, 10)
          : (c.joinDate ?? ''),
        clubs: c.clubsCount ?? c.clubCount ?? 0,
      }))
      // Lọc client-side theo name/email (an toàn nếu backend chưa hỗ trợ q)
      .filter(item => {
        const q = searchQuery.trim().toLowerCase();
        if (!q) return true;
        return item.name.toLowerCase().includes(q) || item.email.toLowerCase().includes(q);
      });
  }, [customerPage, searchQuery]);

  const totalElements = customerPage?.totalElements ?? rows.length;
  const totalPages = customerPage?.totalPages ?? 1;


  // Mock customer data
  // const customers = [
  //   { id: '1', name: 'John Smith', email: 'john@example.com', phone: '+1234567890', status: 'active', joinDate: '2024-01-15', clubs: 2 },
  //   { id: '2', name: 'Jane Doe', email: 'jane@example.com', phone: '+1234567891', status: 'active', joinDate: '2024-01-10', clubs: 1 },
  //   { id: '3', name: 'Mike Johnson', email: 'mike@example.com', phone: '+1234567892', status: 'inactive', joinDate: '2024-01-05', clubs: 3 },
  //   { id: '4', name: 'Sarah Wilson', email: 'sarah@example.com', phone: '+1234567893', status: 'active', joinDate: '2023-12-28', clubs: 1 },
  //   { id: '5', name: 'David Brown', email: 'david@example.com', phone: '+1234567894', status: 'active', joinDate: '2023-12-20', clubs: 2 },
  //   { id: '6', name: 'Lisa Garcia', email: 'lisa@example.com', phone: '+1234567895', status: 'inactive', joinDate: '2023-12-15', clubs: 1 },
  // ];

  // const filteredCustomers = customers.filter(customer =>
  //   customer.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
  //   customer.email.toLowerCase().includes(searchQuery.toLowerCase())
  // );

  const handleStatusToggle = async (customerId, currentStatus) => {
    const nextActive = currentStatus !== 'active';
    await adminService.updateCustomerStatus(customerId, nextActive);
    await loadCustomers(); // refresh
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Customer Management</h1>
          <p className="text-muted-foreground">Manage customer accounts and access</p>
        </div>
        <Button onClick={() => navigate('/dashboard/admin')}>
          Back to Dashboard
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Customer List</CardTitle>
          <CardDescription>
            View and manage all customer accounts
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center space-x-2 mb-6">
            <Search className="h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search customers by name or email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="max-w-sm"
            />
          </div>

          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Customer</TableHead>
                  <TableHead>Contact</TableHead>
                  <TableHead>Join Date</TableHead>
                  <TableHead>Clubs</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {rows.map((customer) => (
                  <TableRow key={customer.id}>
                    <TableCell>
                      <div className="space-y-1">
                        <p className="font-medium">{customer.name}</p>
                        <p className="text-sm text-muted-foreground">{customer.email}</p>
                      </div>
                    </TableCell>
                    <TableCell>
                      <p className="text-sm">{customer.phone}</p>
                    </TableCell>
                    <TableCell>
                      <p className="text-sm">{customer.joinDate}</p>
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline">{customer.clubs} clubs</Badge>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center space-x-2">
                        <Switch
                          checked={customer.status === 'active'}
                          onCheckedChange={() => handleStatusToggle(customer.id, customer.status)}
                        />
                        <Badge variant={customer.status === 'active' ? 'default' : 'secondary'}>
                          {customer.status}
                        </Badge>
                      </div>
                    </TableCell>
                    <TableCell>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" className="h-8 w-8 p-0">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => navigate(`/dashboard/admin/customers/${customer.id}`)}>
                            <Eye className="mr-2 h-4 w-4" />
                            View Details
                          </DropdownMenuItem>
                          <DropdownMenuItem 
                            onClick={() => handleStatusToggle(customer.id, customer.status)}
                          >
                            {customer.status === 'active' ? (
                              <>
                                <UserX className="mr-2 h-4 w-4" />
                                Deactivate
                              </>
                            ) : (
                              <>
                                <UserCheck className="mr-2 h-4 w-4" />
                                Activate
                              </>
                            )}
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          {!loading && rows.length === 0 && (
            <div className="text-center py-8">
              <p className="text-muted-foreground">No customers found matching your search.</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Summary Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Total Customers</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalElements}</div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Active Customers</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {rows.filter(c => c.status === 'active').length}
            </div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Inactive Customers</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-orange-600">
              {rows.filter(c => c.status === 'inactive').length}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}