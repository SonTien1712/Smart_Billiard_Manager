import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Badge } from '../ui/badge';
import { Switch } from '../ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { PageType } from '../Dashboard';
import { ArrowLeft, Edit, Save, X, Building, Table, Users } from 'lucide-react';

import { useApi } from '../../hooks/useApi';
import { adminService } from '../../services/adminService';

export function CustomerDetails({ customerId, onPageChange }) {
  const [isEditing, setIsEditing] = useState(false);
  
  // Mock customer data - in real app, fetch based on customerId
  // const [customer, setCustomer] = useState({
  //   id: customerId || '1',
  //   name: 'John Smith',
  //   email: 'john@example.com',
  //   phone: '+1234567890',
  //   address: '123 Main St, New York, NY 10001',
  //   status: 'active',
  //   joinDate: '2024-01-15',
  //   lastLogin: '2024-01-20 10:30 AM'
  // });

  const fetchCustomer = useCallback(
    () => adminService.getCustomerById(customerId),
    [customerId]
  );

  const fetchClubs = useCallback(
    () => adminService.getCustomerClubs(customerId),
    [customerId]
  );

  // const customerClubs = [
  //   { id: '1', name: 'Downtown Billiards Club', address: '456 Downtown Ave', tables: 8, status: 'active' },
  //   { id: '2', name: 'Uptown Pool Hall', address: '789 Uptown Blvd', tables: 12, status: 'active' }
  // ];

  const {
    data: customerRaw,
    loading: loadingCustomer,
    execute: loadCustomer,
  } = useApi(fetchCustomer);

  const {
    data: clubsRaw,
    loading: loadingClubs,
    execute: loadClubs,
  } = useApi(fetchClubs);

  useEffect(() => {
    if (customerId) {
      loadCustomer();
      loadClubs();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [customerId]);

  const customer = useMemo(() => {
    const c = customerRaw || {};
    return {
      id: c.id ?? c.customerId ?? customerId,
      name: c.name ?? c.fullName ?? c.customerName ?? 'N/A',
      email: c.email ?? 'N/A',
      phone: c.phone ?? c.phoneNumber ?? '',
      address: c.address ?? '',
      status: (c.isActive ?? c.active ?? c.enabled) ? 'active' : 'inactive',
      joinDate: c.dateJoined
        ? new Date(c.dateJoined).toISOString().slice(0, 10)
        : (c.joinDate ?? ''),
      lastLogin: c.lastLogin
        ? new Date(c.lastLogin).toLocaleString()
        : (c.lastLoginAt ?? ''),
    };
  }, [customerRaw, customerId]);

  const customerClubs = useMemo(() => {
    const list = Array.isArray(clubsRaw?.content) ? clubsRaw.content
                : Array.isArray(clubsRaw) ? clubsRaw
                : [];
    return list.map((club) => ({
      id: club.id ?? club.clubId ?? String(Math.random()),
      name: club.name ?? club.clubName ?? 'N/A',
      address: club.address ?? '',
      tables: club.tables ?? club.tablesCount ?? 0,
      status: (club.active ?? club.isActive) ? 'active' : 'inactive'
    }));
  }, [clubsRaw]);

  const customerStats = {
    totalRevenue: 12450,
    totalTables: 20,
    totalStaff: 15,
    monthlyRevenue: 2100
  };

  const [editData, setEditData] = useState(customer);
  useEffect(() => setEditData(customer), [customer]);


  const handleSave = async () => {
    // TODO: call API update nếu backend có
    setIsEditing(false);
  };

  const handleCancel = () => {
    setEditData(customer);
    setIsEditing(false);
  };

  const handleStatusToggle = async () => {
    const nextActive = customer.status !== 'active';
    await adminService.updateCustomerStatus(customer.id, nextActive);
    await loadCustomer(); // refresh lại trạng thái mới
  };

  const anyLoading = loadingCustomer || loadingClubs;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onPageChange('customer-list')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Customers
          </Button>
          <div>
            <h1 className="text-3xl font-semibold">{anyLoading ? 'Loading...' : 'Customer Details'}</h1>
            <p className="text-muted-foreground">View and manage customer information</p>
          </div>
        </div>
        
        <div className="flex items-center space-x-2">
          {isEditing ? (
            <>
              <Button variant="outline" onClick={handleCancel}>
                <X className="h-4 w-4 mr-2" />
                Cancel
              </Button>
              <Button onClick={handleSave}>
                <Save className="h-4 w-4 mr-2" />
                Save Changes
              </Button>
            </>
          ) : (
            <Button onClick={() => setIsEditing(true)} disabled={anyLoading}>
              <Edit className="h-4 w-4 mr-2" />
              Edit Customer
            </Button>
          )}
        </div>
      </div>

      <Tabs defaultValue="details" className="space-y-6">
        <TabsList>
          <TabsTrigger value="details">Customer Details</TabsTrigger>
          <TabsTrigger value="clubs">Clubs</TabsTrigger>
          <TabsTrigger value="analytics">Analytics</TabsTrigger>
        </TabsList>

        <TabsContent value="details" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Personal Information</CardTitle>
              <CardDescription>
                Customer account details and contact information
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid gap-4 md:grid-cols-2">
                <div className="space-y-2">
                  <Label htmlFor="name">Full Name</Label>
                  <Input
                    id="name"
                    value={isEditing ? editData.customerName : customer.customerName}
                    onChange={(e) => setEditData({ ...editData, customerName: e.target.value })}
                    disabled={!isEditing}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    value={isEditing ? editData.email : customer.email}
                    onChange={(e) => setEditData({ ...editData, email: e.target.value })}
                    disabled={!isEditing}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="phone">Phone</Label>
                  <Input
                    id="phone"
                    value={isEditing ? editData.phone : customer.phone}
                    onChange={(e) => setEditData({ ...editData, phone: e.target.value })}
                    disabled={!isEditing}
                  />
                </div>

                <div className="space-y-2">
                  <Label>Status</Label>
                  <div className="flex items-center space-x-2">
                    <Switch
                      checked={customer.status === 'active'}
                      onCheckedChange={handleStatusToggle}
                      disabled={isEditing || anyLoading}
                    />
                    <Badge variant={customer.status === 'active' ? 'default' : 'secondary'}>
                      {customer.status}
                    </Badge>
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="address">Address</Label>
                <Input
                  id="address"
                  value={isEditing ? editData.address : customer.address}
                  onChange={(e) => setEditData({ ...editData, address: e.target.value })}
                  disabled={!isEditing}
                />
              </div>

              <div className="grid gap-4 md:grid-cols-2">
                <div className="space-y-2">
                  <Label>Join Date</Label>
                  <Input value={customer.joinDate} disabled />
                </div>
                
                <div className="space-y-2">
                  <Label>Last Login</Label>
                  <Input value={customer.lastLogin} disabled />
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="clubs" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Customer Clubs</CardTitle>
              <CardDescription>
                Billiards clubs owned and managed by this customer
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {customerClubs.map((club) => (
                  <div key={club.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="flex items-center space-x-4">
                      <Building className="h-8 w-8 text-primary" />
                      <div className="space-y-1">
                        <p className="font-medium">{club.name}</p>
                        <p className="text-sm text-muted-foreground">{club.address}</p>
                        <div className="flex items-center space-x-4 text-xs text-muted-foreground">
                          <span>{club.tables} tables</span>
                        </div>
                      </div>
                    </div>
                    <Badge variant={club.status === 'active' ? 'default' : 'secondary'}>
                      {club.status}
                    </Badge>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="analytics" className="space-y-6">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Total Revenue</CardTitle>
                <Building className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">${customerStats.totalRevenue.toLocaleString()}</div>
                <p className="text-xs text-muted-foreground">All time revenue</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Total Tables</CardTitle>
                <Table className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{customerStats.totalTables}</div>
                <p className="text-xs text-muted-foreground">Across all clubs</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Total Staff</CardTitle>
                <Users className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{customerStats.totalStaff}</div>
                <p className="text-xs text-muted-foreground">Employees managed</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Monthly Revenue</CardTitle>
                <Building className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">${customerStats.monthlyRevenue.toLocaleString()}</div>
                <p className="text-xs text-muted-foreground">This month</p>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}