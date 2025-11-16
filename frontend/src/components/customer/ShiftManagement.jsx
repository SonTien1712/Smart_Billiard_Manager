import React, { useEffect, useMemo, useState, useCallback } from 'react'; // Thêm 'useCallback'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Calendar, ChevronLeft, ChevronRight, Users, Plus, Trash2, X } from 'lucide-react';
import { toast } from 'sonner';
import { useAuth } from '../AuthProvider';
import { customerService } from '../../services/customerService';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';

// --- Component Modal Chi Tiết Ca (Giữ nguyên, rất tốt) ---
const ShiftAssignmentDetails = ({ assignments, slot, date, onUnassign, onOpenChange }) => {
  const displayDate = new Date(date).toLocaleDateString('vi-VN', { weekday: 'long', day: 'numeric', month: 'numeric' });

  return (
    <Dialog open={assignments !== null} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Chi Tiết Ca Làm: {slot.label}</DialogTitle>
          <div className="text-sm text-muted-foreground">{displayDate} ({slot.start} - {slot.end})</div>
        </DialogHeader>
        <div className="space-y-3 pt-4 max-h-96 overflow-y-auto">
          {assignments.length === 0 ? (
            <p className="text-center text-muted-foreground">Chưa có nhân viên nào được gán ca này.</p>
          ) : (
            assignments.map((shift) => (
              <div key={shift.id} className="flex items-center justify-between p-3 border rounded-lg bg-card shadow-sm">
                <div className="space-y-0.5">
                  <p className="font-medium text-sm">{shift.staffName || `ID: #${shift.staffId}`}</p>
                  <Badge variant={shift.status === 'SCHEDULED' ? 'default' : 'secondary'} className="text-xs">
                    {shift.status}
                  </Badge>
                </div>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => onUnassign(shift)}
                >
                  <Trash2 className="h-4 w-4 mr-2" />
                  Gỡ Ca
                </Button>
              </div>
            ))
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

// --- Component Chính: ShiftManagement ---
export function ShiftManagement() {
  // --- State Management ---
  const { user } = useAuth();
  const [currentWeek, setCurrentWeek] = useState(0);
  const [effectiveClubId, setEffectiveClubId] = useState(null);
  const [clubs, setClubs] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState(null);
  const [shifts, setShifts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [isAssignmentMode, setIsAssignmentMode] = useState(false);
  const [selectedShiftDetails, setSelectedShiftDetails] = useState(null);

  // --- Constants & Date Utils ---
  const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  const startOfWeek = useMemo(() => {
    const d = new Date();
    d.setDate(d.getDate() - d.getDay() + 1 + currentWeek * 7);
    d.setHours(0, 0, 0, 0);
    return d;
  }, [currentWeek]);
  const endOfWeek = useMemo(() => new Date(startOfWeek.getTime() + 6 * 24 * 60 * 60 * 1000), [startOfWeek]);

  const daySlots = [
    { code: 'SANG_1', label: 'Sáng 1', start: '06:00:00', end: '10:00:00' },
    { code: 'SANG_2', label: 'Sáng 2', start: '10:00:00', end: '14:00:00' },
    { code: 'CHIEU_1', label: 'Chiều 1', start: '14:00:00', end: '18:00:00' },
    { code: 'CHIEU_2', label: 'Chiều 2', start: '18:00:00', end: '22:00:00' },
    { code: 'DEM_1', label: 'Đêm 1', start: '22:00:00', end: '02:00:00' },
    { code: 'DEM_2', label: 'Đêm 2', start: '02:00:00', end: '06:00:00' },
  ];

  const toLocalYMD = (d) => {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  };
  const toMinutes = (hhmmss) => {
    if (!hhmmss) return null;
    const [h, m] = hhmmss.split(':').map(Number);
    return (h % 24) * 60 + (m || 0);
  };
  const getDateForDay = (dayIndex) => {
    const date = new Date(startOfWeek);
    date.setDate(startOfWeek.getDate() + dayIndex);
    return toLocalYMD(date);
  };

  // --- Data Fetching ---
  const directClubId = useMemo(() => user?.clubId || user?.club?.id || user?.customerClubId, [user]);

  // Load club list
  useEffect(() => {
    const resolveClub = async () => {
      const custId = user?.customerId || user?.id;
      try {
        let list = [];
        if (custId) list = await customerService.getClubsByCustomer(custId);
        const normalized = (Array.isArray(list) ? list : []).map(c => ({
          id: c.id ?? c.clubId ?? c.ClubID ?? null,
          name: c.clubName ?? c.name ?? `Club #${c.id ?? c.clubId}`,
        })).filter(x => x.id);
        if (normalized.length > 0) {
          setClubs(normalized);
          setEffectiveClubId(String(normalized[0].id));
          return;
        }
        if (directClubId) {
          setClubs([{ id: directClubId, name: `Club #${directClubId}` }]);
          setEffectiveClubId(String(directClubId));
        }
      } catch (_) {
        if (directClubId) {
          setClubs([{ id: directClubId, name: `Club #${directClubId}` }]);
          setEffectiveClubId(String(directClubId));
        }
      }
    };
    resolveClub();
  }, [directClubId, user]);

  // Load employees for selected club
  useEffect(() => {
    const loadStaff = async () => {
      if (!effectiveClubId) return;
      try {
        const list = await customerService.getStaff(Number(effectiveClubId));
        const mapped = (Array.isArray(list) ? list : []).map(s => {
          const id = s.id ?? s.staffId ?? s.employeeId ?? s.userId ?? s.EmpID ?? null;
          let name = s.name ?? s.fullName;
          if (name == null) {
            const full = [s.firstName, s.lastName].filter(Boolean).join(' ');
            name = full ? full : undefined;
          }
          if (name == null) name = s.user?.fullName;
          if (name == null) name = `Staff #${s.id ?? s.staffId}`;
          return { id, name };
        }).filter(x => x.id);
        setEmployees(mapped);
        // [TỐI ƯU GỌN GÀNG] Tự động chọn nhân viên đầu tiên (hoặc xóa lựa chọn cũ)
        if (mapped.length > 0) {
          setSelectedEmployeeId(String(mapped[0].id));
        } else {
          setSelectedEmployeeId(null);
        }
      } catch (e) {
        console.error('Load staff failed', e);
        setEmployees([]);
      }
    };
    loadStaff();
  }, [effectiveClubId]);

  // [TỐI ƯU 1 - GỌN GÀNG] Bọc `loadShifts` trong `useCallback`
  const loadShifts = useCallback(async () => {
    if (!effectiveClubId) return;
    setLoading(true);
    try {
      const params = { startDate: toLocalYMD(startOfWeek), endDate: toLocalYMD(endOfWeek) };
      const data = await customerService.getShifts(Number(effectiveClubId), params);
      const normalized = (Array.isArray(data) ? data : (Array.isArray(data?.data) ? data.data : [])).map(x => ({
        id: x.id ?? x.shiftId ?? x.attendanceId ?? Math.random().toString(36).slice(2),
        date: x.date ?? x.shiftDate,
        startTime: x.startTime,
        endTime: x.endTime,
        staffId: x.staffId ?? x.employeeId ?? x.userId ?? x.staff?.id,
        staffName: x.staff?.user?.fullName ?? x.staff?.name ?? x.employeeName ?? x.staffName,
        status: (x.status || 'SCHEDULED').toString().toUpperCase(),
      }));
      setShifts(normalized);
    } catch (e) {
      console.error('Load shifts failed', e);
      setShifts([]);
    } finally {
      setLoading(false);
    }
  }, [effectiveClubId, startOfWeek, endOfWeek]); // Dependencies của hàm

  // `useEffect` này bây giờ phụ thuộc vào hàm `loadShifts` đã được useCallback
  useEffect(() => {
    loadShifts();
  }, [loadShifts]);

  // --- Event Handlers ---
  const findAssignments = (date, slot) => {
    const startMin = toMinutes(slot.start);
    const endMin = toMinutes(slot.end);
    return shifts.filter(s => {
      if (!s.date || s.date !== date) return false;
      const sMin = toMinutes(s.startTime);
      if (sMin == null) return false;
      if (slot.code === 'DEM_1') return sMin >= startMin || sMin < endMin; // wrap midnight
      return sMin >= startMin && sMin < endMin;
    });
  };

  const handleAssign = async (date, slot) => {
    if (!effectiveClubId) {
      toast.error('Chưa chọn Club');
      return;
    }
    if (!selectedEmployeeId) {
      toast.error('Vui lòng chọn nhân viên trước');
      return;
    }
    const emp = employees.find(e => String(e.id) === String(selectedEmployeeId));
    const ok = window.confirm(`Thêm lịch cho ${emp?.name || 'nhân viên'}\nNgày ${date} - ${slot.label} (${slot.start} ~ ${slot.end})?`);
    if (!ok) return;
    try {
      setSubmitting(true);
      await customerService.createShift({
        clubId: Number(effectiveClubId),
        staffId: Number(selectedEmployeeId),
        date,
        startTime: slot.start,
        endTime: slot.end,
        status: 'SCHEDULED',
        shiftCode: slot.code,
      });
      toast.success('Đã tạo lịch làm');
      await loadShifts();
    } catch (e) {
      console.error('Create shift failed', e);
      toast.error('Tạo lịch thất bại. Vui lòng thử lại.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleUnassign = async (shift) => {
    const ok = window.confirm(`Gỡ lịch của ${shift.staffName || `#${shift.staffId}`}?`);
    if (!ok) return;
    try {
      setSubmitting(true);
      await customerService.deleteShift(shift.id);
      toast.success('Đã gỡ lịch');
      await loadShifts();
      setSelectedShiftDetails(null); // Đóng modal
    } catch (e) {
      console.error('Delete shift failed', e);
      toast.error('Gỡ lịch thất bại');
    } finally {
      setSubmitting(false);
    }
  };

  const handleSlotClick = (date, slot) => {
    const assigned = findAssignments(date, slot);
    if (isAssignmentMode) {
      handleAssign(date, slot);
    } else {
      setSelectedShiftDetails({ date, slot, assignments: assigned });
    }
  };

  // --- Render Helpers ---
  const getStatusVariant = (status) => {
    const s = (status || '').toString().toLowerCase();
    if (s.includes('cancel')) return 'secondary';
    if (s.includes('complete')) return 'outline';
    return 'default';
  };

  const formatWeekRange = () => {
    const fmt = (d) => d.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' });
    return `${fmt(startOfWeek)} - ${fmt(endOfWeek)}`;
  };

  const slotBadgeClass = (code) => {
    if (code.startsWith('SANG')) return 'bg-yellow-100 text-yellow-800 border border-yellow-200';
    if (code.startsWith('CHIEU')) return 'bg-blue-100 text-blue-800 border border-blue-200';
    return 'bg-gray-100 text-gray-800 border border-gray-200';
  };

  // --- Component Render ---
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Work Shifts</h1>
          <p className="text-muted-foreground">Quản lý lịch làm việc theo ca (6 slot)</p>
        </div>
      </div>

      {/* [TỐI ƯU 3 - HỢP LÍ] Sửa lại mô tả Card cho đúng logic */}
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Chọn Club</CardTitle>
            <CardDescription>Chọn Club để xem và quản lý lịch</CardDescription>
          </CardHeader>
          <CardContent>
            {/* [TỐI ƯU 2 - HỢP LÍ] Reset nhân viên khi đổi club */}
            <Select
              value={effectiveClubId ?? ''}
              onValueChange={(v) => {
                setEffectiveClubId(v);
                setSelectedEmployeeId(null);
              }}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Chọn Club" />
              </SelectTrigger>
              <SelectContent>
                {clubs.map(c => (
                  <SelectItem key={c.id} value={String(c.id)}>{c.name}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Nhân viên</CardTitle>
            <CardDescription>Chọn 1 người để gán ca khi ở Chế Độ Gán</CardDescription>
          </CardHeader>

          {/* THAY THẾ TOÀN BỘ CardContent BẰNG CODE NÀY */}
          <CardContent>
            <Select
              value={selectedEmployeeId ?? ''}
              onValueChange={(v) => setSelectedEmployeeId(v)}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Chọn một nhân viên" />
              </SelectTrigger>
              <SelectContent>
                {employees.map(emp => (
                  <SelectItem key={emp.id} value={String(emp.id)}>
                    {emp.name}
                  </SelectItem>
                ))}
                {employees.length === 0 && (
                  // Hiển thị text này bên trong dropdown nếu không có nhân viên
                  <div className="p-2 text-sm text-muted-foreground">Không có nhân viên</div>
                )}
              </SelectContent>
            </Select>
          </CardContent>
        </Card>
      </div>

      {/* Bảng Lịch Tuần */}
      <Card>
        <CardHeader>
          <div className="flex flex-wrap items-center justify-between gap-2"> {/* Thêm flex-wrap và gap */}
            <div>
              <CardTitle>Weekly Schedule</CardTitle>
              <CardDescription>Week of {formatWeekRange()}</CardDescription>
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant={isAssignmentMode ? 'destructive' : 'default'}
                onClick={() => setIsAssignmentMode(!isAssignmentMode)}
                size="sm"
              >
                {isAssignmentMode ? (
                  <><X className="h-4 w-4 mr-1" size="sm" /> Thoát Chế Độ</>
                ) : (
                  <><Plus className="h-4 w-4 mr-1" size="sm" /> Gán Ca</>
                )}
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(currentWeek - 1)}>
                <ChevronLeft className="h-4 w-4 mr-1" /> Previous Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(0)}>
                Current Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(currentWeek + 1)}>
                Next Week <ChevronRight className="h-4 w-4 ml-1" />
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-7 gap-4">
            {weekDays.map((day, idx) => {
              const date = getDateForDay(idx);
              return (
                <div key={day} className="space-y-3">
                  <div className="text-center px-3 py-2 bg-muted rounded-xl">
                    <p className="font-medium text-sm">{day}</p>
                    <p className="text-xs text-muted-foreground">
                      {new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    </p>
                  </div>
                  <div className="space-y-3">
                    {daySlots.map(slot => {
                      const assigned = findAssignments(date, slot);
                      const names = assigned.map(a => a.staffName || `#${a.staffId}`).join(', ');
                      const isSelectedEmployeeAssigned = assigned.some(a => String(a.staffId) === String(selectedEmployeeId));
                      const isDisabled = isAssignmentMode && isSelectedEmployeeAssigned;

                      return (
                        <div
                          key={slot.code}
                          className={`group rounded-xl border p-3 transition-all ${isDisabled
                              ? 'opacity-50 cursor-not-allowed bg-muted/30' // Disabled state
                              : isAssignmentMode
                                ? (isSelectedEmployeeAssigned
                                  ? 'border-primary bg-primary/10 hover:bg-primary/15 cursor-pointer'
                                  : 'hover:bg-green-50 border-dashed border-green-500/40 hover:border-green-500 cursor-pointer')
                                : 'hover:bg-muted/70 border-border cursor-pointer'
                            }`}
                          onClick={() => !isDisabled && handleSlotClick(date, slot)} // Không cho click nếu disabled
                        >
                          <div className="flex items-center justify-between gap-2">
                            <div className={`inline-flex items-center rounded px-2 py-1 text-sm font-medium ${slotBadgeClass(slot.code)}`}>
                              {slot.label}
                            </div>
                            {isAssignmentMode && isSelectedEmployeeAssigned && (
                              <Badge variant="default" className="text-xs h-5 w-5 p-0 flex items-center justify-center rounded-full">✓</Badge>
                            )}
                          </div>

                          <div className="text-xs text-muted-foreground mt-2 min-h-[1.5rem]">
                            {assigned.length === 0 ? (
                              <span className="italic opacity-60">Chưa có nhân viên</span>
                            ) : (
                              names
                            )}
                          </div>

                          {/* Chỉ hiển thị hint khi hover VÀ chưa được gán */}
                          {isAssignmentMode && !isSelectedEmployeeAssigned && (
                            <div className="text-xs text-green-600 mt-1.5 opacity-0 group-hover:opacity-100 transition-opacity">
                              + Gán {employees.find(e => String(e.id) === String(selectedEmployeeId))?.name}
                            </div>
                          )}
                        </div>
                      );
                    })}
                  </div>
                </div>
              );
            })}
          </div>
          {loading && (
            <div className="text-sm text-muted-foreground mt-2">Đang tải lịch...</div>
          )}
        </CardContent>
      </Card>

      {/* Stats Cards (Giữ nguyên) */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Shifts</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.length}</div>
            <p className="text-xs text-muted-foreground">Tuần này</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Assigned</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.filter(s => (s.status || '').toUpperCase() === 'SCHEDULED').length}</div>
            <p className="text-xs text-muted-foreground">Ca đã xếp</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Completed</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.filter(s => (s.status || '').toUpperCase() === 'COMPLETED').length}</div>
            <p className="text-xs text-muted-foreground">Chấm công xong</p>
          </CardContent>
        </Card>
      </div>

      {/* Modal được render ở đây là chính xác */}
      {
        selectedShiftDetails && (
          <ShiftAssignmentDetails
            assignments={selectedShiftDetails.assignments}
            slot={selectedShiftDetails.slot}
            date={selectedShiftDetails.date}
            onUnassign={handleUnassign}
            onOpenChange={() => setSelectedShiftDetails(null)}
          />
        )
      }
    </div >
  );
}