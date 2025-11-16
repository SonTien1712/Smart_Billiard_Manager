import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Badge } from '../ui/badge';
import { Switch } from '../ui/switch';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';
import { PageType } from '../Dashboard';
import { UserCog, Plus, Edit, Trash2, Eye, EyeOff, Key, Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { customerService } from '../../services/customerService';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "../ui/select"



import { useAuth } from '../AuthProvider';

export function StaffAccountManagement({ onPageChange }) {
    const { customerId } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [isPageLoading, setIsPageLoading] = useState(true);
  const [isLoading, setIsLoading] = useState(false); // State loading cho các nút (Save,...)
  const [error, setError] = useState(null); // State xử lý lỗi
  // --- THÊM MỚI: State cho việc tạo tài khoản ---
  // 1. Danh sách nhân viên CHƯA có tài khoản
  const [unassignedStaff, setUnassignedStaff] = useState([]);
  // 2. Loading khi bấm nút "Create Account" (để fetch staff)
  const [isFetchingStaff, setIsFetchingStaff] = useState(false);



  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingAccount, setEditingAccount] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    employeeId: '',
    username: '',
    employeeName: '', // Dùng cho Edit mode
    password: '',
    status: true,     // Sửa: Đảm bảo đây là boolean
    clubId: null,     // THÊM MỚI
    customerId: null  // THÊM MỚI
  });

  const fetchAccounts = async () => {
    setIsPageLoading(true);
    setError(null);
    try {
      console.log(`Đang tải tài khoản cho customer ID: ${customerId}`);
      const data = await customerService.getStaffAccountsByCustomerId(customerId);
      // Giả sử backend trả về 'employee' (tên), nếu là 'employeeName', cần sửa ở đây
      console.log("Dữ liệu nhận được TRƯỚC KHI setAccounts:", data);
      setAccounts(data);
    } catch (err) {
      console.error('Lỗi khi tải tài khoản:', err);
      setError('Không thể tải danh sách tài khoản. Vui lòng thử lại.');
      toast.error('Lỗi khi tải danh sách tài khoản.');
    } finally {
      setIsPageLoading(false);
    }
  };

  useEffect(() => {
    // Chỉ tải nếu có customerId
    if (!customerId) {
      toast.error('Không tìm thấy thông tin khách hàng (Customer ID).');
      setIsPageLoading(false);
      return;
    }



    fetchAccounts();
  }, [customerId]); // Tải lại nếu customerId thay đổi

  const handleEdit = (account) => {
    setEditingAccount(account);
    // Backend có thể trả về 'employeeName', form dùng 'employee'
    setFormData({ ...account, employeeName: account.employeeName || account.employeeName, password: '' });
    setIsDialogOpen(true);
  };

  // --- SỬA: HÀM MỞ DIALOG THÊM MỚI ---
  const handleAdd = async () => {
    setIsFetchingStaff(true); // Bắt đầu loading nút "Create Account"
    setEditingAccount(null);

    try {
      // 1. Gọi API mới để lấy danh sách nhân viên chưa có tài khoản
      const staffList = await customerService.getUnassignedStaff(customerId);

      if (!staffList || staffList.length === 0) {
        toast.info('Tất cả nhân viên đã có tài khoản.');
        setIsFetchingStaff(false);
        return; // Không mở dialog nếu không có ai để thêm
      }

      // 2. Lưu danh sách vào state
      setUnassignedStaff(staffList);

      // 3. Reset form data cho việc TẠO MỚI
      setFormData({
        employeeId: '',
        username: '',
        employeeName: '',
        password: '',
        status: true
      });

      setShowPassword(false);
      setIsDialogOpen(true); // 4. Mở dialog sau khi đã có data

    } catch (err) {
      console.error("Lỗi khi tải danh sách nhân viên:", err);
      toast.error('Không thể tải danh sách nhân viên. Vui lòng thử lại.');
    } finally {
      setIsFetchingStaff(false); // Dừng loading
    }
  };

  // --- TẠO MỚI (CREATE) / CẬP NHẬT (UPDATE) ---
  // --- SỬA: HÀM LƯU (CẬP NHẬT LOGIC 'CREATE') ---
  // --- TẠO MỚI (CREATE) / CẬP NHẬT (UPDATE) ---
  const handleSave = async () => {
    setIsLoading(true);
    try {
      if (editingAccount) {
        // --- LOGIC CẬP NHẬT (Đã sửa lỗi 'undefined') ---

        // 1. Kiểm tra
        if (!formData.password) {
          toast.info("Không có thay đổi nào. (Bạn chưa nhập mật khẩu mới)");
          setIsLoading(false);
          setIsDialogOpen(false);
          return;
        }

        // 2. Tạo payload
        const updateData = {
          password: formData.password
        };

        // 3. Gọi API (API này trả về undefined, không gán)
        await customerService.updateStaffAccount(editingAccount.id, updateData);

        // 4. Báo thành công
        toast.success(`Đã cập nhật mật khẩu!`, {
          description: (
            <div style={{ marginTop: '8px', lineHeight: '1.5' }}>
              {/* Chúng ta lấy 'username' và 'employeeName' 
                từ 'formData' (vì 'handleEdit' đã nạp nó vào)
              */}
              <div><strong>Tài khoản:</strong> {formData.username}</div>
              <div><strong>Nhân viên:</strong> {formData.employeeName}</div>
              <div style={{ marginTop: '4px', color: '#65a30d' /* màu xanh */ }}>
                Mật khẩu đã được thay đổi thành công.
              </div>
            </div>
          ),
          duration: 5000 // 5 giây
        });

        // 65a30d
        

        // #3B82F6

        // (Không cần setAccounts vì mật khẩu không hiển thị trên bảng)

      } else {
        // --- LOGIC TẠO MỚI (Giờ đã hoạt động) ---

        // 1. Validation (Giữ nguyên)
        if (!formData.employeeId || !formData.clubId || !formData.customerId) {
          toast.error("Vui lòng chọn một nhân viên (Thiếu thông tin ID).");
          setIsLoading(false);
          return;
        }
        if (!formData.username) { /* ... */ }
        if (!formData.password) { /* ... */ }

        // 2. Tạo payload (Giữ nguyên)
        const createData = {
          username: formData.username,
          password: formData.password,
          employeeId: formData.employeeId,
          clubId: formData.clubId,
          customerId: formData.customerId,
          isActive: formData.status
        };

        console.log("Đang gửi data:", createData);

        // 3. Gọi API (Giữ nguyên)
        await customerService.createStaffAccount(createData);

        // 4. Tải lại danh sách (Giờ đã gọi được)
        fetchAccounts();

        // 5. Hiển thị toast (Giữ nguyên)
        const staff = unassignedStaff.find(s => s.id === formData.employeeId);
        const staffName = staff ? staff.employeeName : 'nhân viên';
        toast.success(`Đã tạo tài khoản cho ${staffName}!`, {
          description: (
            <div style={{ marginTop: '8px', lineHeight: '1.5' }}>
              <div><strong>Username:</strong> {formData.username}</div>
              <div><strong>Password:</strong> {formData.password}</div>
              <div style={{ marginTop: '4px', color: '#FACC15' }}>
                Vui lòng copy và gửi mật khẩu này cho nhân viên.
              </div>
            </div>
          ),
          duration: 15000 // Cho 15 giây để copy
        });
      }
      setIsDialogOpen(false); // Đóng Dialog khi thành công

    } catch (err) {
      console.error('Lỗi khi lưu:', err);
      toast.error(err.response?.data?.message || 'Lưu thất bại. Vui lòng thử lại.');
    } finally {
      setIsLoading(false);
    }
  };

  // --- XÓA (DELETE) ---
  // const handleDelete = async (id) => {
  //   // TODO: Nên thêm một bước xác nhận (confirm dialog) trước khi xóa
  //   // if (!window.confirm('Bạn có chắc muốn xóa tài khoản này?')) return;

  //   try {
  //     await customerService.deleteStaffAccount(id);
  //     // Xóa khỏi state sau khi API thành công
  //     setAccounts(accounts.filter(acc => acc.id !== id));
  //     toast.success('Đã xóa tài khoản.');
  //   } catch (err) {
  //     console.error('Lỗi khi xóa:', err);
  //     toast.error('Xóa thất bại. Tài khoản có thể đang được sử dụng.');
  //   }
  // };

  // --- CẬP NHẬT TRẠNG THÁI (TOGGLE) ---
  // --- CẬP NHẬT TRẠNG THÁI (TOGGLE) ---
  const handleStatusToggle = async (id, currentIsActive) => {
    // currentIsActive là một boolean (true hoặc false)

    // 1. Dữ liệu cho State (Optimistic Update) -> Cần là boolean
    const newStatusBoolean = !currentIsActive;

    // 2. Dữ liệu cho API (Service) -> Cần là string
    const newStatusString = newStatusBoolean ? 'active' : 'inactive';

    // 3. Cập nhật giao diện ngay lập tức (dùng boolean)
    setAccounts(accounts.map(acc =>
      acc.id === id ? { ...acc, isActive: newStatusBoolean } : acc
    ));

    try {
      // 4. Gọi API (dùng string)
      await customerService.toggleStaffAccountStatus(id, newStatusString);
      toast.success('Cập nhật trạng thái thành công.');
    } catch (err) {
      console.error('Lỗi khi cập nhật trạng thái:', err);
      toast.error('Cập nhật thất bại, đang hoàn tác...');
      // 5. Hoàn tác (dùng boolean cũ)
      setAccounts(accounts.map(acc =>
        acc.id === id ? { ...acc, isActive: currentIsActive } : acc
      ));
    }
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
                  <TableCell>{account.employeeName}</TableCell>
                  <TableCell className="text-sm text-muted-foreground">{account.lastLogin}</TableCell>
                  <TableCell>
                    <div className="flex items-center space-x-2">
                      <Switch
                        checked={account.isActive === true}
                        onCheckedChange={() => handleStatusToggle(account.id, account.isActive)}
                      />
                      <Badge variant={account.isActive === true ? 'default' : 'secondary'}>
                        {account.isActive}
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
              {editingAccount ? 'Update password for this staff member' : 'Select a staff member to create a new login account'}
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4">

            {editingAccount ? (
              // --- TRƯỜNG HỢP 1: SỬA TÀI KHOẢN (EDITING) ---
              <div className="space-y-4">
                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="username_edit">Username</Label>
                    <Input
                      id="username_edit"
                      value={formData.username}
                      disabled // Không cho sửa username
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="employee_edit">Employee Name</Label>
                    <Input
                      id="employee_edit"
                      value={formData.employeeName}
                      disabled // Không cho sửa tên
                    />
                  </div>
                </div>
              </div>

            ) : (
              // --- TRƯỜNG HỢP 2: TẠO MỚI (CREATING) ---
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="employeeSelect">Chọn nhân viên</Label>
                  <Select
                    value={formData.employeeId}
                    onValueChange={(selectedEmployeeId) => {
                      // 1. Tìm nhân viên được chọn
                      const selectedStaff = unassignedStaff.find(
                        (staff) => staff.id === selectedEmployeeId
                      );

                      // 2. Tự động điền email vào username
                      setFormData({
                        ...formData,
                        employeeId: selectedStaff?.id || '',
                        username: selectedStaff?.email || '',
                        clubId: selectedStaff?.clubId || null,     // Lấy clubId
                        customerId: selectedStaff?.customerId || null // Lấy customerId
                      });
                    }}
                  >
                    <SelectTrigger id="employeeSelect">
                      <SelectValue placeholder="Chọn nhân viên chưa có tài khoản" />
                    </SelectTrigger>
                    <SelectContent>
                      {unassignedStaff.map(staff => (
                        <SelectItem key={staff.id} value={staff.id}>
                          {/* Giả sử DTO trả về 'employeeName' và 'email' */}
                          {staff.employeeName} ({staff.email})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="username_create">Username</Label>
                  <Input
                    id="username_create"
                    value={formData.username} // Hiển thị email đã chọn
                    placeholder="Tài khoản mặc định là email của nhân viên"
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  />
                </div>
              </div>
            )}

            {/* --- PHẦN CHUNG: MẬT KHẨU --- */}
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
            <Button variant="outline" onClick={() => setIsDialogOpen(false)} disabled={isLoading}>
              Cancel
            </Button>
            <Button onClick={handleSave} disabled={isLoading}>
              {isLoading && <Loader2 className="h-4 w-4 mr-2 animate-spin" />}
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
              {accounts.filter(acc => acc.isActive === true).length}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Inactive Accounts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-orange-600">
              {accounts.filter(acc => acc.isActive === false).length}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}