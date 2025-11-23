import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Calendar, ChevronLeft, ChevronRight, Users, Plus, Trash2, X, Save } from 'lucide-react';
import { toast } from 'sonner';
import { useAuth } from '../AuthProvider';
import { customerService } from '../../services/customerService';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';

// --- Component Modal Chi Tiết Ca ---
const ShiftAssignmentDetails = ({ assignments, slot, date, onUnassign, onOpenChange,isSlotUnavailable }) => {
  // Changed locale to en-US for English format
  const displayDate = new Date(date).toLocaleDateString('en-US', { weekday: 'long', day: 'numeric', month: 'numeric' });
  const isPast = isSlotUnavailable(date, slot.start, slot.end);

  return (
    <Dialog open={assignments !== null} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Shift Details: {slot.label}</DialogTitle>
          <div className="text-sm text-muted-foreground">{displayDate} ({slot.start} - {slot.end})</div>
          {isPast && (
            <div className="text-xs text-amber-600 font-medium mt-1">
              ⚠️ This shift is in the past and cannot be modified
            </div>
          )}
        </DialogHeader>
        <div className="space-y-3 pt-4 max-h-96 overflow-y-auto">
          {assignments.length === 0 ? (
            <p className="text-center text-muted-foreground">No staff assigned to this shift.</p>
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
                  disabled={isPast} // THÊM DÒNG NÀY
                  className={isPast ? 'opacity-50 cursor-not-allowed' : ''} // THÊM DÒNG NÀY
                >
                  <Trash2 className="h-4 w-4 mr-2" />
                  Unassign
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
  const [pendingAssignments, setPendingAssignments] = useState([]);

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
    { code: 'SANG_1', label: 'Morning 1', start: '06:00:00', end: '10:00:00' },
    { code: 'SANG_2', label: 'Morning 2', start: '10:00:00', end: '14:00:00' },
    { code: 'CHIEU_1', label: 'Afternoon 1', start: '14:00:00', end: '18:00:00' },
    { code: 'CHIEU_2', label: 'Afternoon 2', start: '18:00:00', end: '22:00:00' },
    { code: 'DEM_1', label: 'Night 1', start: '22:00:00', end: '02:00:00' },
    { code: 'DEM_2', label: 'Night 2', start: '02:00:00', end: '06:00:00' },
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

  const isSlotUnavailable = (dateStr, slotStartTimeStr, slotEndTimeStr) => {
    const now = new Date();
    const slotStart = new Date(`${dateStr}T${slotStartTimeStr}`);
    const slotEnd = new Date(`${dateStr}T${slotEndTimeStr}`); 

    if (slotEnd <= slotStart) {
      // Logic for overnight shift check if needed
    }

    return slotStart <= now;
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
  }, [effectiveClubId, startOfWeek, endOfWeek]);

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
      if (slot.code === 'DEM_1') return sMin >= startMin || sMin < endMin;
      return sMin >= startMin && sMin < endMin;
    });
  };

  const handleUnassign = async (shift) => {
    const ok = window.confirm(`Unassign shift for ${shift.staffName || `#${shift.staffId}`}?`);
    if (!ok) return;
    try {
      setSubmitting(true);
      await customerService.deleteShift(shift.id);
      toast.success('Shift unassigned successfully');
      await loadShifts();
      setSelectedShiftDetails(null);
    } catch (e) {
      console.error('Delete shift failed', e);
      toast.error('Failed to unassign shift');
    } finally {
      setSubmitting(false);
    }
  };

  const isPending = (date, slot) => {
    return pendingAssignments.some(p =>
      p.date === date &&
      p.slotCode === slot.code &&
      String(p.staffId) === String(selectedEmployeeId)
    );
  };

  const handleSlotClick = (date, slot) => {
    // 1. Kiểm tra chế độ gán ca
    if (!isAssignmentMode) {
      const assigned = findAssignments(date, slot);
      setSelectedShiftDetails({ date, slot, assignments: assigned });
      return;
    }

    // 2. Kiểm tra quá khứ
    if (isSlotUnavailable(date, slot.start, slot.end)) {
      toast.warning("Cannot assign shifts in the past.");
      return;
    }

    // 3. Kiểm tra đã chọn nhân viên chưa
    if (!selectedEmployeeId) {
      toast.error('Please select a staff member first');
      return;
    }

    const assigned = findAssignments(date, slot);

    // Kiểm tra xem nhân viên này ĐÃ CÓ LỊCH (đã lưu trong DB) chưa
    const isAssignedToThisUser = assigned.some(
      a => String(a.staffId) === String(selectedEmployeeId)
    );

    // Kiểm tra xem nhân viên này ĐANG ĐƯỢC CHỌN (Pending) ở slot này chưa
    const isPendingForThisUser = pendingAssignments.some(
      p => p.date === date &&
        p.slotCode === slot.code &&
        String(p.staffId) === String(selectedEmployeeId)
    );

    // === SỬA ĐỔI Ở ĐÂY ===
    // Chỉ chặn nếu nhân viên ĐÃ ĐƯỢC LƯU (Assigned) trong DB.
    // KHÔNG chặn nếu đang ở trạng thái Pending để còn cho phép gỡ bỏ (toggle).
    if (isAssignedToThisUser) {
      toast.info("This staff member is already assigned to this shift.");
      return;
    }

    // Logic Toggle: Nếu đã chọn thì bỏ, chưa chọn thì thêm
    if (isPendingForThisUser) {
      // XỬ LÝ BỎ CHỌN (Unselect/Remove from pending)
      setPendingAssignments(prev => prev.filter(p =>
        !(p.date === date && p.slotCode === slot.code && String(p.staffId) === String(selectedEmployeeId))
      ));
    } else {
      // XỬ LÝ CHỌN (Select/Add to pending)
      setPendingAssignments(prev => [...prev, {
        date,
        slotCode: slot.code,
        startTime: slot.start,
        endTime: slot.end,
        staffId: selectedEmployeeId,
        staffName: employees.find(e => String(e.id) === String(selectedEmployeeId))?.name
      }]);
    }
  };

  const handleBulkSave = async () => {
    if (pendingAssignments.length === 0) return;
    if (!effectiveClubId) return;

    const ok = window.confirm(`Save these ${pendingAssignments.length} shifts?`);
    if (!ok) return;

    setSubmitting(true);
    try {
      const promises = pendingAssignments.map(item =>
        customerService.createShift({
          clubId: Number(effectiveClubId),
          staffId: Number(item.staffId),
          date: item.date,
          startTime: item.startTime,
          endTime: item.endTime,
          status: 'SCHEDULED',
          shiftCode: item.slotCode,
        })
      );

      await Promise.all(promises);

      toast.success("Saved successfully!");
      setPendingAssignments([]);
      await loadShifts();
    } catch (e) {
      toast.error('An error occurred.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleWeekChange = (offset) => {
    if (pendingAssignments.length > 0) {
      const ok = window.confirm(
        `⚠️ You have ${pendingAssignments.length} unsaved shifts.\n\nChanging weeks will CLEAR them all. Continue?`
      );
      if (!ok) return;
      setPendingAssignments([]);
    }
    setCurrentWeek(currentWeek + offset);
  };

  // --- Render Helpers ---
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
          <p className="text-muted-foreground">Manage work shifts (6 slots)</p>
        </div>
      </div>

      {/* Banner hiển thị pending và nút Lưu/Hủy */}
      {pendingAssignments.length > 0 && (
        <div className="flex items-center gap-3 bg-blue-50 p-3 rounded-lg border-2 border-blue-300 shadow-sm animate-in fade-in slide-in-from-top-2">
          <div className="flex-1">
            <p className="text-sm font-semibold text-blue-900">
              📋 Selected: {pendingAssignments.length} shifts
            </p>
            <p className="text-xs text-blue-600">
              For: <span className="font-medium">{employees.find(e => String(e.id) === String(selectedEmployeeId))?.name}</span>
            </p>
          </div>
          {/* NÚT LƯU - Variant Primary */}
          <Button
            onClick={handleBulkSave}
            disabled={submitting}
            variant="default"
            size="sm"
          >
            {submitting ? (
              <span className="flex items-center gap-2">
                Saving...
              </span>
            ) : (
              <>
                <Save className="w-4 h-4" />
                Save ({pendingAssignments.length})
              </>
            )}
          </Button>

          {/* NÚT HỦY - Variant Outline hoặc Destructive */}
          <Button
            variant="outline"
            size="sm"
            onClick={() => setPendingAssignments([])}
            disabled={submitting}
          >
            Cancel
          </Button>
        </div>
      )}

      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Select Club</CardTitle>
            <CardDescription>Select a Club to view and manage schedule</CardDescription>
          </CardHeader>
          <CardContent>
            <Select
              value={effectiveClubId ?? ''}
              onValueChange={(v) => {
                if (pendingAssignments.length > 0) {
                  const ok = window.confirm(
                    `⚠️ There are ${pendingAssignments.length} unsaved shifts. Changing club will CLEAR them all. Continue?`
                  );
                  if (!ok) return;
                  setPendingAssignments([]);
                }
                setEffectiveClubId(v);
                setSelectedEmployeeId(null);
              }}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Select Club" />
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
            <CardTitle>Staff</CardTitle>
            <CardDescription>Select a person to assign shifts in Assignment Mode</CardDescription>
          </CardHeader>
          <CardContent>
            <Select
              value={selectedEmployeeId ?? ''}
              onValueChange={(v) => {
                if (pendingAssignments.length > 0) {
                  const currentName = employees.find(e => String(e.id) === String(selectedEmployeeId))?.name;
                  const newName = employees.find(e => String(e.id) === String(v))?.name;
                  const ok = window.confirm(
                    `⚠️ There are ${pendingAssignments.length} shifts pending for "${currentName}".\n\nSwitching to "${newName}" will CLEAR selected shifts. Continue?`
                  );
                  if (!ok) return;
                  setPendingAssignments([]);
                }
                setSelectedEmployeeId(v);
              }}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Select a staff member" />
              </SelectTrigger>
              <SelectContent>
                {employees.map(emp => (
                  <SelectItem key={emp.id} value={String(emp.id)}>
                    {emp.name}
                  </SelectItem>
                ))}
                {employees.length === 0 && (
                  <div className="p-2 text-sm text-muted-foreground">No staff available</div>
                )}
              </SelectContent>
            </Select>
          </CardContent>
        </Card>
      </div>

      {/* Bảng Lịch Tuần */}
      <Card>
        <CardHeader>
          <div className="flex flex-wrap items-center justify-between gap-2">
            <div>
              <CardTitle>Weekly Schedule</CardTitle>
              <CardDescription>Week of {formatWeekRange()}</CardDescription>
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant={isAssignmentMode ? 'destructive' : 'default'}
                onClick={() => {
                  if (isAssignmentMode && pendingAssignments.length > 0) {
                    const ok = window.confirm(
                      `⚠️ Exiting mode will CLEAR ${pendingAssignments.length} selected shifts.\n\nDo you want to SAVE first?`
                    );
                    if (!ok) return;
                    setPendingAssignments([]);
                  }
                  setIsAssignmentMode(!isAssignmentMode);
                }}
                size="sm"
              >
                {isAssignmentMode ? (
                  <><X className="h-4 w-4 mr-1" size="sm" /> Exit Mode</>
                ) : (
                  <><Plus className="h-4 w-4 mr-1" size="sm" /> Assign Shift</>
                )}
              </Button>
              <Button variant="outline" size="sm" onClick={() => handleWeekChange(-1)}>
                <ChevronLeft className="h-4 w-4 mr-1" /> Previous Week
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  if (pendingAssignments.length > 0) {
                    const ok = window.confirm(`There are ${pendingAssignments.length} unsaved shifts. Clear and go to current week?`);
                    if (!ok) return;
                    setPendingAssignments([]);
                  }
                  setCurrentWeek(0);
                }}
              >
                Current Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => handleWeekChange(1)}>
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

                      const isPast = isSlotUnavailable(date, slot.start, slot.end);
                      const pending = isPending(date, slot);

                      const isDisabled = isPast;

                      return (
                        <div
                          key={slot.code}
                          className={`group rounded-xl border p-3 transition-all ${isDisabled
                            ? 'opacity-50 cursor-not-allowed bg-muted/30'
                            : isAssignmentMode
                              ? (pending
                                ? 'border-primary border-2 border-dashed bg-blue-50 cursor-pointer'
                                : isSelectedEmployeeAssigned
                                  ? 'border-primary bg-primary/10 hover:bg-primary/15 cursor-pointer'
                                  : 'hover:bg-green-50 border-dashed border-green-500/40 hover:border-green-500 cursor-pointer')
                              : 'hover:bg-muted/70 border-border cursor-pointer'
                            }`}
                          onClick={() => {
                            if (submitting) return;
                            if (isDisabled && isAssignmentMode) return;
                            handleSlotClick(date, slot);
                          }}
                        >
                          <div className="flex items-center justify-between gap-2">
                            <div className={`inline-flex items-center rounded px-2 py-1 text-sm font-medium ${slotBadgeClass(slot.code)}`}>
                              {slot.label}
                            </div>

                            {isAssignmentMode && isSelectedEmployeeAssigned && !pending && (
                              <Badge variant="default" className="text-xs h-5 w-5 p-0 flex items-center justify-center rounded-full">✓</Badge>
                            )}

                            {pending && (
                              <Badge variant="secondary" className="text-xs h-5 w-5 p-0 flex items-center justify-center rounded-full text-blue-600 bg-blue-100">+</Badge>
                            )}
                          </div>

                          <div className="text-xs text-muted-foreground mt-2 min-h-[1.5rem]">
                            {assigned.length > 0 && (
                              <div className="text-gray-700 text-xs">{names}</div>
                            )}

                            {pending && (
                              <div className="font-medium text-blue-600 mt-1 text-xs flex items-center gap-1">
                                <span className="text-lg">+</span>
                                {employees.find(e => String(e.id) === String(selectedEmployeeId))?.name}
                              </div>
                            )}

                            {assigned.length === 0 && !pending && (
                              <span className="italic opacity-60">No staff assigned</span>
                            )}
                          </div>

                          {isAssignmentMode && !isSelectedEmployeeAssigned && !pending && !isDisabled && (
                            <div className="text-xs text-green-600 mt-1.5 opacity-0 group-hover:opacity-100 transition-opacity">
                              + Assign {employees.find(e => String(e.id) === String(selectedEmployeeId))?.name}
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
            <div className="text-sm text-muted-foreground mt-2">Loading schedule...</div>
          )}
        </CardContent>
      </Card>

      {/* Stats Cards */}
      <div className="grid gap-4 md:grid-cols-3 items-stretch">
        <Card className="h-full">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Shifts</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.length}</div>
            <p className="text-xs text-muted-foreground">This week</p>
          </CardContent>
        </Card>

        <Card className="h-full">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Assigned</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {shifts.filter(s => (s.status || '').toUpperCase() === 'SCHEDULED').length}
            </div>
            <p className="text-xs text-muted-foreground">Scheduled shifts</p>
          </CardContent>
        </Card>
      </div>

      {/* Modal Chi Tiết */}
      {selectedShiftDetails && (
        <ShiftAssignmentDetails
          assignments={selectedShiftDetails.assignments}
          slot={selectedShiftDetails.slot}
          date={selectedShiftDetails.date}
          onUnassign={handleUnassign}
          onOpenChange={() => setSelectedShiftDetails(null)}
          isSlotUnavailable={isSlotUnavailable}
        />
      )}
    </div>
  );
}