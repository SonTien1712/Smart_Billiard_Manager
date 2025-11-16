import React, { useState, useEffect } from 'react';
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
import { customerService } from '../../services/customerService';


import { useAuth } from '../AuthProvider';

export function StaffManagement({ onPageChange }) {
    const { customerId } = useAuth();
  const [staff, setStaff] = useState([]);
  const [isSaving, setIsSaving] = useState(false);

  // ✅ 1. STATE MỚI CHO CLUBS
  const [clubs, setClubs] = useState([]);
  const [isClubsLoading, setIsClubsLoading] = useState(true);
  const [clubsError, setClubsError] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingStaff, setEditingStaff] = useState(null);
  const [formData, setFormData] = useState({
    employeeName: '', // Thay vì name
    email: '',
    phoneNumber: '', // Thay vì phone
    employeeType: 'FullTime', // Thay vì contractType ('FullTime' hoặc 'PartTime')
    salaryValue: 0, // Giá trị lương/rate (sẽ được map thành hourlyRate/monthlySalary)
    clubId: null,
    // Thêm các trường API bắt buộc khác: address, bankNumber, bankName...
    address: '',
    bankNumber: '',
    bankName: '',
  });

  const fetchClubs = async () => {
    if (!customerId) return;

    setIsClubsLoading(true);
    setClubsError(null);

    try {
      const data = await customerService.getClubsByCustomer(customerId);

      if (Array.isArray(data) && data.length > 0) {
        setClubs(data);

        // Set clubId mặc định cho formData nếu đây là lần đầu load form
        // Chỉ set nếu formData.clubId chưa có (null)
        setFormData(prev => ({
          ...prev,
          clubId: prev.clubId || data[0].id // Lấy ID của club đầu tiên
        }));
      } else {
        setClubs([]);
        setClubsError("No clubs found for this customer.");
      }
    } catch (err) {
      console.error("Failed to fetch clubs:", err);
      setClubsError("Failed to load club list.");
    } finally {
      setIsClubsLoading(false);
    }
  };
  // 1. Hàm gọi API để fetch staff
  const fetchStaff = async () => {
    // Console log để kiểm tra hàm được gọi
    console.log('--- fetchStaff started ---');
    console.log(`Attempting to fetch staff for Customer ID: ${customerId}`);

    setIsLoading(true);
    setError(null);
    try {
      const data = await customerService.getStaffByCustomerId(customerId);

      // ✅ CONSOLE LOG KẾT QUẢ THÀNH CÔNG:
      console.log('Staff list successfully fetched:', data);
      console.log(`Total staff members loaded: ${data.length}`);
      if (Array.isArray(data)) {
        console.log(`Staff list successfully fetched: ${data.length} items`);
        setStaff(data);
      } else {
        // Trường hợp response không phải mảng (như undefined hoặc object khác)
        console.error("API returned data is not an array:", data);
        setStaff([]); // Đặt staff thành mảng rỗng để tránh lỗi
        setError("Error: API response format is invalid.");
      }


    } catch (err) {
      // CONSOLE LOG KẾT QUẢ THẤT BẠI:
      console.error("Failed to fetch staff:", err);
      setError("Failed to load staff list. Please try again.");
    } finally {
      console.log('--- fetchStaff finished ---');
      setIsLoading(false);
    }
  };

  // 2. useEffect để gọi API khi component được mount
  useEffect(() => {
    // Gọi fetchClubs và fetchStaff khi customerId tồn tại
    if (customerId) {
      fetchClubs();
      fetchStaff(); // Giữ lại fetchStaff ở đây
    }
  }, [customerId]); // Chạy lại khi customerId thay đổi
  const handleEdit = (staffMember) => {
    setEditingStaff(staffMember);

    // Ánh xạ dữ liệu API (employeeName, hourlyRate/monthlySalary) sang form state
    setFormData({
      id: staffMember.id,
      employeeName: staffMember.employeeName || '',
      email: staffMember.email || '',
      phoneNumber: staffMember.phoneNumber || '',
      employeeType: staffMember.employeeType || 'FullTime',

      // Lấy giá trị lương dựa trên loại hợp đồng
      salaryValue: staffMember.employeeType === 'FullTime'
        ? staffMember.salary || 0
        : staffMember.hourlyRate || 0,


      clubId: staffMember.clubId || defaultClubId,
      address: staffMember.address || '',
      bankNumber: staffMember.bankNumber || '',
      bankName: staffMember.bankName || '',
    });
    setIsDialogOpen(true);
  };

  const handleAdd = () => {
    setEditingStaff(null);
    setFormData({
      employeeName: '',
      email: '',
      phoneNumber: '',
      employeeType: 'FullTime',
      salaryValue: 2500,
      clubId: clubs[0]?.id || null, // Lấy clubId đầu tiên đã fetch
      address: '',
      bankNumber: '',
      bankName: '',
    });
    setIsDialogOpen(true);
  };

  const handleSave = async () => {
    setIsSaving(true);
    setError(null);

    // Chuẩn bị payload: Map salaryValue ngược lại thành hourlyRate/monthlySalary
    const payload = {
      id: editingStaff?.id, // Chỉ cần cho update
      employeeName: formData.employeeName,
      email: formData.email,
      phoneNumber: formData.phoneNumber,
      employeeType: formData.employeeType,
      address: formData.address,
      bankNumber: formData.bankNumber,
      bankName: formData.bankName,
      clubId: formData.clubId,
      customerId: customerId, // Gửi customerId để tạo/cập nhật

      // Logic mapping lương/rate
      salary: formData.employeeType === 'FullTime' ? formData.salaryValue : null,
      hourlyRate: formData.employeeType === 'PartTime' ? formData.salaryValue : null,
    };

    try {
      if (editingStaff) {
        // UPDATE (PUT)
        await customerService.updateStaff(editingStaff.id, payload);
      } else {
        // CREATE (POST)
        await customerService.createStaff(payload);
      }

      setIsDialogOpen(false);

      // Refresh danh sách staff sau khi lưu thành công
      await fetchStaff();

    } catch (err) {
      console.error('Failed to save staff data:', err);
      setError(`Failed to save staff: ${err.message || 'Check network connection.'}`);
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this staff member?')) {
      return;
    }

    setError(null);

    try {
      // Xóa qua API
      await customerService.deleteStaff(id);

      // Refresh danh sách staff để hiển thị thay đổi
      await fetchStaff();

    } catch (err) {
      console.error('Failed to delete staff:', err);
      setError(`Failed to delete staff member: ${err.message || 'Check network connection.'}`);
      // Có thể hiển thị lại danh sách cũ nếu delete thất bại
    }
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
                      <p className="font-medium">{employee.employeeName}</p>
                      <div className="flex items-center space-x-2 text-sm text-muted-foreground">
                        <Mail className="h-3 w-3" />
                        <span>{employee.email}</span>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-2 text-sm">
                      <Phone className="h-3 w-3 text-muted-foreground" />
                      <span>{employee.phoneNumber}</span>
                    </div>
                  </TableCell>
                  <TableCell className="text-sm">{employee.clubName}</TableCell>
                  <TableCell>
                    <Badge variant={employee.employeeType === 'FullTime' ? 'default' : 'secondary'}>
                      {employee.employeeType}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-1">
                      <DollarSign className="h-4 w-4 text-muted-foreground" />
                      <span>
                        {employee.employeeType === 'FullTime'
                          ? `${employee.salary || 0}/month`
                          : `${employee.hourlyRate || 0}/hour`}

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
                  value={formData.employeeName}
                  onChange={(e) => setFormData({ ...formData, employeeName: e.target.value })}
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
                  value={formData.phoneNumber}
                  onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                  placeholder="Enter phone number"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="contractType">Contract Type</Label>
                <Select value={formData.employeeType} onValueChange={(value) => setFormData({ ...formData, employeeType: value })}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select contract type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="FullTime">Full-time</SelectItem>
                    <SelectItem value="PartTime">Part-time</SelectItem>
                    <SelectItem value="contract">Contract</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="club">Club</Label>
                <Select
                  value={formData.clubId}
                  onValueChange={(value) => setFormData({ ...formData, clubId: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a club" />
                  </SelectTrigger>
                  <SelectContent>
                    {clubs.map((club) => (
                      <SelectItem key={club.id} value={club.id}>
                        {club.clubName}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="salary">
                {formData.employeeType === 'FullTime' ? 'Monthly Salary ($)' : 'Hourly Rate ($)'}
              </Label>
              <Input
                id="salary"
                type="number"
                value={formData.salaryValue}
                onChange={(e) => setFormData({ ...formData, salaryValue: parseInt(e.target.value) || 0 })}
                placeholder={formData.employeeType === 'FullTime' ? 'Enter monthly salary' : 'Enter hourly rate'}
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