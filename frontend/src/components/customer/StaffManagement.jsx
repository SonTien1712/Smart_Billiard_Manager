import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { PageType } from '../Dashboard';
import { Users, Plus, Edit, Trash2, DollarSign, Phone, Mail } from 'lucide-react';


export function StaffManagement({ onPageChange }) {
  const [staff, setStaff] = useState([
    { id: '1', name: 'Alice Johnson', email: 'alice@club.com', phone: '+1234567890', contractType: 'full-time', salary: 2500, club: 'Downtown Billiards Club' },
    { id: '2', name: 'Bob Smith', email: 'bob@club.com', phone: '+1234567891', contractType: 'part-time', salary: 15, club: 'Downtown Billiards Club' },
    { id: '3', name: 'Carol Brown', email: 'carol@club.com', phone: '+1234567892', contractType: 'full-time', salary: 2800, club: 'Uptown Pool Hall' },
  ]);

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingStaff, setEditingStaff] = useState<any>(null);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    contractType: 'full-time',
    salary: 2500,
    club: 'Downtown Billiards Club'
  });

  const handleEdit = (staffMember) => {
    setEditingStaff(staffMember);
    setFormData(staffMember);
    setIsDialogOpen(true);
  };

  const handleAdd = () => {
    setEditingStaff(null);
    setFormData({ name: '', email: '', phone: '', contractType: 'full-time', salary: 2500, club: 'Downtown Billiards Club' });
    setIsDialogOpen(true);
  };

  const handleSave = () => {
    if (editingStaff) {
      setStaff(staff.map(s => s.id === editingStaff.id ? { ...formData, id: editingStaff.id } : s));
    } else {
      setStaff([...staff, { ...formData, id: Date.now().toString() }]);
    }
    setIsDialogOpen(false);
  };

  const handleDelete = (id) => {
    setStaff(staff.filter(s => s.id !== id));
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Staff Management</h1>
          <p className="text-muted-foreground">Manage your employees</p>
        </div>
        <Button onClick={handleAdd}>
          <Plus className="h-4 w-4 mr-2" />
          Add Staff
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Employee List</CardTitle>
          <CardDescription>Manage staff across all your clubs</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Employee</TableHead>
                <TableHead>Contact</TableHead>
                <TableHead>Club</TableHead>
                <TableHead>Contract</TableHead>
                <TableHead>Salary</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {staff.map((employee) => (
                <TableRow key={employee.id}>
                  <TableCell>
                    <div className="space-y-1">
                      <p className="font-medium">{employee.name}</p>
                      <div className="flex items-center space-x-2 text-sm text-muted-foreground">
                        <Mail className="h-3 w-3" />
                        <span>{employee.email}</span>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-2 text-sm">
                      <Phone className="h-3 w-3 text-muted-foreground" />
                      <span>{employee.phone}</span>
                    </div>
                  </TableCell>
                  <TableCell className="text-sm">{employee.club}</TableCell>
                  <TableCell>
                    <Badge variant={employee.contractType === 'full-time' ? 'default' : 'secondary'}>
                      {employee.contractType}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-1">
                      <DollarSign className="h-4 w-4 text-muted-foreground" />
                      <span>
                        {employee.contractType === 'full-time' 
                          ? `${employee.salary}/month` 
                          : `${employee.salary}/hour`
                        }
                      </span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-2">
                      <Button variant="ghost" size="sm" onClick={() => handleEdit(employee)}>
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="sm" onClick={() => handleDelete(employee.id)}>
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
            <DialogTitle>{editingStaff ? 'Edit Employee' : 'Add New Employee'}</DialogTitle>
            <DialogDescription>
              {editingStaff ? 'Update employee information' : 'Create a new staff member'}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="name">Full Name</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="Enter full name"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  placeholder="Enter email"
                />
              </div>
            </div>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="phone">Phone</Label>
                <Input
                  id="phone"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  placeholder="Enter phone number"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="contractType">Contract Type</Label>
                <Select value={formData.contractType} onValueChange={(value) => setFormData({ ...formData, contractType: value })}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select contract type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="full-time">Full-time</SelectItem>
                    <SelectItem value="part-time">Part-time</SelectItem>
                    <SelectItem value="contract">Contract</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="salary">
                {formData.contractType === 'full-time' ? 'Monthly Salary ($)' : 'Hourly Rate ($)'}
              </Label>
              <Input
                id="salary"
                type="number"
                value={formData.salary}
                onChange={(e) => setFormData({ ...formData, salary: parseInt(e.target.value) })}
                placeholder={formData.contractType === 'full-time' ? 'Enter monthly salary' : 'Enter hourly rate'}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleSave}>
              {editingStaff ? 'Update' : 'Create'} Employee
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}