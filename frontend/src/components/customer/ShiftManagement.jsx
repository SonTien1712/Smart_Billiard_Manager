import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Calendar, ChevronLeft, ChevronRight, Users, Plus, Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { useAuth } from '../AuthProvider';
import { customerService } from '../../services/customerService';

export function ShiftManagement() {
  const { user } = useAuth();
  const [currentWeek, setCurrentWeek] = useState(0);
  const [effectiveClubId, setEffectiveClubId] = useState(null);
  const [clubs, setClubs] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState(null);
  const [shifts, setShifts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  const startOfWeek = useMemo(() => {
    const d = new Date();
    d.setDate(d.getDate() - d.getDay() + 1 + currentWeek * 7);
    d.setHours(0, 0, 0, 0);
    return d;
  }, [currentWeek]);
  const endOfWeek = useMemo(() => new Date(startOfWeek.getTime() + 6 * 24 * 60 * 60 * 1000), [startOfWeek]);

  // 6 slots per day
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

  // Resolve club list and default club
  const directClubId = useMemo(() => user?.clubId || user?.club?.id || user?.customerClubId, [user]);
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
        if (!selectedEmployeeId && mapped.length > 0) setSelectedEmployeeId(String(mapped[0].id));
      } catch (e) {
        console.error('Load staff failed', e);
        setEmployees([]);
      }
    };
    loadStaff();
  }, [effectiveClubId]);

  // Load shifts for the week
  const loadShifts = async () => {
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
  };

  useEffect(() => { loadShifts(); }, [effectiveClubId, currentWeek]);

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
    } catch (e) {
      console.error('Delete shift failed', e);
      toast.error('Gỡ lịch thất bại');
    } finally {
      setSubmitting(false);
    }
  };

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

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Work Shifts</h1>
          <p className="text-muted-foreground">Quản lý lịch làm việc theo ca (6 slot)</p>
        </div>
      </div>

      {/* Weekly schedule full-width like the mock */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle>Weekly Schedule</CardTitle>
              <CardDescription>Week of {formatWeekRange()}</CardDescription>
            </div>
            <div className="flex items-center gap-2">
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
                      return (
                        <div key={slot.code}
                             className="rounded-xl border p-3 hover:bg-muted/70"
                             onClick={() => selectedEmployeeId && handleAssign(date, slot)}>
                          <div className={`inline-flex items-center rounded px-2 py-0.5 text-xs font-medium ${slotBadgeClass(slot.code)}`}>
                            {slot.label}
                          </div>
                          <div className="text-xs text-muted-foreground mt-1">
                            {assigned.length === 0 ? 'No shift' : names}
                          </div>
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

      {/* Optional controls for choosing club and employee for assigning */}
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Chọn Club</CardTitle>
            <CardDescription>Áp dụng khi gán ca bằng cách click slot</CardDescription>
          </CardHeader>
          <CardContent>
            <Select value={effectiveClubId ?? ''} onValueChange={(v) => setEffectiveClubId(v)}>
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
            <CardDescription>Chọn 1 người rồi click slot để gán</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-2 max-h-72 overflow-auto pr-1">
              {employees.map(emp => (
                <button
                  key={emp.id}
                  onClick={() => setSelectedEmployeeId(String(emp.id))}
                  className={`w-full text-left p-2 rounded border ${String(selectedEmployeeId) === String(emp.id) ? 'bg-primary/10 border-primary' : 'hover:bg-muted'}`}
                >
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium">{emp.name}</span>
                    {String(selectedEmployeeId) === String(emp.id) && (
                      <Badge variant="default" className="text-xxs">Selected</Badge>
                    )}
                  </div>
                </button>
              ))}
              {employees.length === 0 && (
                <div className="text-sm text-muted-foreground">Không có nhân viên</div>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Shifts</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.length}</div>
            <p className="text-xs text-muted-foreground">This week</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Assigned</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.filter(s => (s.status || '').toUpperCase() === 'SCHEDULED').length}</div>
            <p className="text-xs text-muted-foreground">Confirmed shifts</p>
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
    </div>
  );
}
