import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Switch } from '../ui/switch';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { PageType } from '../Dashboard';
import { UserCog, Plus, Edit, Trash2, Eye, EyeOff, Key } from 'lucide-react';


export function StaffAccountManagement({ onPageChange }) {
  const [accounts, setAccounts] = useState([
    { id: '1', username: 'alice.johnson', employee: 'Alice Johnson', email: 'alice@club.com', status: 'active', lastLogin: '2024-01-20 10:30 AM' },
    { id: '2', username: 'bob.smith', employee: 'Bob Smith', email: 'bob@club.com', status: 'active', lastLogin: '2024-01-19 2:15 PM' },
    { id: '3', username: 'carol.brown', employee: 'Carol Brown', email: 'carol@club.com', status: 'inactive', lastLogin: '2024-01-15 9:45 AM' },
  ]);

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingAccount, setEditingAccount] = useState<any>(null);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    employee: '',
    email: '',
    password: '',
    status: 'active'
  });

  const handleEdit = (account) => {
    setEditingAccount(account);
    setFormData({ ...account, password: '' });
    setIsDialogOpen(true);
  };

  const handleAdd = () => {
    setEditingAccount(null);
    setFormData({ username: '', employee: '', email: '', password: '', status: 'active' });
    setIsDialogOpen(true);
  };

  const handleSave = () => {
    if (editingAccount) {
      setAccounts(accounts.map(acc => acc.id === editingAccount.id ? { ...formData, id: editingAccount.id, lastLogin: acc.lastLogin } : acc));
    } else {
      setAccounts([...accounts, { ...formData, id: Date.now().toString(), lastLogin: 'Never' }]);
    }
    setIsDialogOpen(false);
  };

  const handleDelete = (id) => {
    setAccounts(accounts.filter(acc => acc.id !== id));
  };

  const handleStatusToggle = (id, currentStatus) => {
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    setAccounts(accounts.map(acc => acc.id === id ? { ...acc, status: newStatus } : acc));
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Staff Account Management</h1>
          <p className="text-muted-foreground">Manage staff login accounts and access</p>
        </div>
        <Button onClick={handleAdd}>
          <Plus className="h-4 w-4 mr-2" />
          Create Account
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Staff Accounts</CardTitle>
          <CardDescription>Login accounts for your staff members</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Account</TableHead>
                <TableHead>Employee</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Last Login</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {accounts.map((account) => (
                <TableRow key={account.id}>
                  <TableCell>
                    <div className="flex items-center space-x-2">
                      <UserCog className="h-4 w-4 text-primary" />
                      <span className="font-medium">{account.username}</span>
                    </div>
                  </TableCell>
                  <TableCell>{account.employee}</TableCell>
                  <TableCell className="text-sm">{account.email}</TableCell>
                  <TableCell className="text-sm text-muted-foreground">{account.lastLogin}</TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-2">
                      <Switch
                        checked={account.status === 'active'}
                        onCheckedChange={() => handleStatusToggle(account.id, account.status)}
                      />
                      <Badge variant={account.status === 'active' ? 'default' : 'secondary'}>
                        {account.status}
                      </Badge>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-2">
                      <Button variant="ghost" size="sm" onClick={() => handleEdit(account)}>
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="sm" onClick={() => handleDelete(account.id)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingAccount ? 'Edit Staff Account' : 'Create Staff Account'}</DialogTitle>
            <DialogDescription>
              {editingAccount ? 'Update staff account information' : 'Create a new login account for staff member'}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="username">Username</Label>
                <Input
                  id="username"
                  value={formData.username}
                  onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  placeholder="Enter username"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="employee">Employee Name</Label>
                <Input
                  id="employee"
                  value={formData.employee}
                  onChange={(e) => setFormData({ ...formData, employee: e.target.value })}
                  placeholder="Enter employee name"
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="Enter email address"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">
                {editingAccount ? 'New Password (leave blank to keep current)' : 'Password'}
              </Label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  placeholder={editingAccount ? 'Enter new password' : 'Enter password'}
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4 text-muted-foreground" />
                  ) : (
                    <Eye className="h-4 w-4 text-muted-foreground" />
                  )}
                </Button>
              </div>
            </div>
            
            {!editingAccount && (
              <div className="p-4 bg-muted/50 rounded-lg">
                <div className="flex items-center space-x-2 mb-2">
                  <Key className="h-4 w-4 text-primary" />
                  <p className="font-medium text-sm">Account Permissions</p>
                </div>
                <p className="text-sm text-muted-foreground">
                  Staff accounts will have access to:
                </p>
                <ul className="text-sm text-muted-foreground mt-1 space-y-1">
                  <li>• Bill management and processing</li>
                  <li>• Work schedule viewing</li>
                  <li>• Attendance tracking</li>
                  <li>• Product ordering for customers</li>
                </ul>
              </div>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleSave}>
              {editingAccount ? 'Update' : 'Create'} Account
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Summary Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Total Accounts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{accounts.length}</div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Active Accounts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {accounts.filter(acc => acc.status === 'active').length}
            </div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Inactive Accounts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-orange-600">
              {accounts.filter(acc => acc.status === 'inactive').length}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}