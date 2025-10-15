import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Calendar, Clock, LogIn, LogOut, ChevronLeft, ChevronRight } from 'lucide-react';
import { staffService } from '../../services/staffService';
import { useAuth } from '../AuthProvider';

export function WorkAndAttendance() {
  const { user } = useAuth();
  const [currentWeek, setCurrentWeek] = useState(0);
  const [loading, setLoading] = useState(false);
  const [shifts, setShifts] = useState([]);

  const today = new Date();
  const startOfWeek = useMemo(() => {
    const d = new Date();
    d.setDate(d.getDate() - d.getDay() + 1 + currentWeek * 7);
    d.setHours(0,0,0,0);
    return d;
  }, [currentWeek]);
  const endOfWeek = useMemo(() => new Date(startOfWeek.getTime() + 6 * 24 * 60 * 60 * 1000), [startOfWeek]);

  // Format date as YYYY-MM-DD using local time to avoid UTC shift
  const toLocalYMD = (d) => {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  };

  const fetchShifts = async () => {
    setLoading(true);
    try {
      const data = await staffService.getSchedule({
        accountId: user?.id,
        employeeId: user?.employeeId,
        startDate: toLocalYMD(startOfWeek),
        endDate: toLocalYMD(endOfWeek)
      });
      setShifts(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error('Failed to load schedule', e);
      setShifts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchShifts(); }, [currentWeek]);

  const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  // Six slots from 06:00, 4 daytime + 2 night slots
  const daySlots = [
    { label: 'Sáng 1', start: '06:00:00', end: '10:00:00' },
    { label: 'Sáng 2', start: '10:00:00', end: '14:00:00' },
    { label: 'Chiều 1', start: '14:00:00', end: '18:00:00' },
    { label: 'Chiều 2', start: '18:00:00', end: '22:00:00' },
    { label: 'Đêm 1', start: '22:00:00', end: '02:00:00' },
    { label: 'Đêm 2', start: '02:00:00', end: '06:00:00' },
  ];

  const slotLabelToCode = {
    'Sáng 1': 'SANG_1',
    'Sáng 2': 'SANG_2',
    'Chiều 1': 'CHIEU_1',
    'Chiều 2': 'CHIEU_2',
    'Đêm 1': 'DEM_1',
    'Đêm 2': 'DEM_2',
  };

  const toMinutes = (hhmmss) => {
    if (!hhmmss) return null;
    const [h, m, s] = hhmmss.split(':').map(Number);
    return (h % 24) * 60 + (m || 0);
  };

  const getDateForDay = (dayIndex) => {
    const date = new Date(startOfWeek);
    date.setDate(startOfWeek.getDate() + dayIndex);
    return toLocalYMD(date);
  };

  const getShiftsForDate = (date) => shifts.filter((s) => s.shiftDate === date);

  // Match a DB shift into a visual slot by start time falling within slot window
  const inSlot = (shift, slot) => {
    const sMin = toMinutes(shift.startTime);
    if (sMin == null) return false;
    const start = toMinutes(slot.start);
    const end = toMinutes(slot.end);
    if (slot.label.startsWith('Đêm 1')) {
      // 22:00 -> 02:00 wraps over midnight
      return sMin >= start || sMin < end;
    }
    if (slot.label.startsWith('Đêm 2')) {
      return sMin >= start && sMin < end;
    }
    return sMin >= start && sMin < end;
  };

  const isNightByStart = (start) => {
    const mins = toMinutes(start);
    return mins >= 22 * 60 || mins < 6 * 60;
  };

  const handleCheckIn = async (shiftId) => {
    try {
      await staffService.checkIn(shiftId);
      await fetchShifts();
    } catch (e) { console.error('check-in failed', e); }
  };
  const handleCheckOut = async (shiftId) => {
    try {
      await staffService.checkOut(shiftId);
      await fetchShifts();
    } catch (e) { console.error('check-out failed', e); }
  };

  const hasActive = useMemo(() => shifts.some(s => s.actualStartTime && !s.actualEndTime), [shifts]);

  const canCheckInNow = (slot, shift) => {
    if (!shift) return false;
    if (hasActive) return false;
    const now = new Date();
    const [sh, sm] = (shift.startTime || '00:00:00').split(':').map(Number);
    const [yy, mm, dd] = (shift.shiftDate || '').split('-').map(Number);
    const start = new Date(yy || now.getFullYear(), (mm ? mm - 1 : now.getMonth()), dd || now.getDate(), sh, sm, 0);
    return now >= start;
  };

  const getShiftPill = (t) => {
    const key = (t || '').toLowerCase();
    if (key.includes('sáng') || key.includes('morning')) return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    if (key.includes('chiều') || key.includes('afternoon')) return 'bg-blue-100 text-blue-800 border-blue-200';
    if (key.includes('tối') || key.includes('evening')) return 'bg-purple-100 text-purple-800 border-purple-200';
    if (key.includes('đêm') || key.includes('night')) return 'bg-gray-100 text-gray-800 border-gray-200';
    return 'bg-gray-100 text-gray-800 border-gray-200';
  };

  const getStatusBadge = (status) => {
    switch ((status || '').toLowerCase()) {
      case 'scheduled': return 'secondary';
      case 'present':
      case 'in progress': return 'default';
      case 'completed': return 'outline';
      case 'absent': return 'destructive';
      default: return 'secondary';
    }
  };

  const totalHours = useMemo(() => {
    return (shifts || []).reduce((sum, s) => sum + (Number(s.hoursWorked) || 0), 0);
  }, [shifts]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Work & Attendance</h1>
        <p className="text-muted-foreground">Your weekly shifts and check-ins</p>
      </div>

      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>Weekly Schedule</CardTitle>
            <div className="flex items-center space-x-2">
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek((w) => w - 1)}>
                <ChevronLeft className="h-4 w-4" />
                Previous Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(0)}>
                Current Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek((w) => w + 1)}>
                Next Week
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
          <CardDescription>
            Week of {startOfWeek.toLocaleDateString()} - {endOfWeek.toLocaleDateString()}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="text-sm text-muted-foreground">Loading…</div>
          ) : (
            <div className="grid grid-cols-7 gap-4">
              {weekDays.map((day, index) => {
                const date = getDateForDay(index);
                const dayShifts = getShiftsForDate(date);
                const isToday = toLocalYMD(today) === date;
                return (
                  <div key={day} className="space-y-2">
                    <div className={`text-center p-2 rounded-lg ${isToday ? 'bg-primary text-primary-foreground' : 'bg-muted'}`}>
                      <p className="font-medium text-sm">{day}</p>
                      <p className="text-xs opacity-80">{new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}</p>
                    </div>
                    <div className="space-y-2 min-h-[150px]">
                      {daySlots.map((slot) => {
                        const code = slotLabelToCode[slot.label];
                        const s = dayShifts.find((x) => (x.slotCode && x.slotCode.toUpperCase() === code) || inSlot(x, slot));
                        return (
                          <div key={slot.label} className={`p-3 rounded-lg border ${s ? 'bg-background' : 'bg-muted/40'}`}>
                            <div className="flex items-center justify-between mb-1">
                              <span className={`px-2 py-1 text-xs rounded border ${getShiftPill(slot.label)}`}>{slot.label}</span>
                              {s && <Badge variant={getStatusBadge(s.status)} className="capitalize">{s.status || 'scheduled'}</Badge>}
                            </div>
                            {s ? (
                              <>
                                <div className="text-sm text-muted-foreground">{s.startTime || ''} - {s.endTime || ''} {isNightByStart(s.startTime) && <span className="ml-2 text-xs">(Đêm +20k)</span>}</div>
                                <div className="flex items-center space-x-2 mt-2">
                                  {!s.actualStartTime && (
                                    <Button size="sm" className="bg-green-600 hover:bg-green-700" disabled={!canCheckInNow(slot, s)} onClick={() => handleCheckIn(s.id)}>
                                      <LogIn className="h-4 w-4 mr-2" />Check In
                                    </Button>
                                  )}
                                  {s.actualStartTime && !s.actualEndTime && (
                                    <Button size="sm" variant="outline" onClick={() => handleCheckOut(s.id)}>
                                      <LogOut className="h-4 w-4 mr-2" />Check Out
                                    </Button>
                                  )}
                                  {s.actualStartTime && s.actualEndTime && (
                                    <span className="text-xs text-muted-foreground">Worked: {Number(s.hoursWorked || 0).toFixed(2)}h</span>
                                  )}
                                </div>
                              </>
                            ) : (
                              <div className="text-xs text-muted-foreground">No shift</div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </CardContent>
      </Card>

      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">This Week</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{Number(totalHours).toFixed(2)}h</div>
            <p className="text-xs text-muted-foreground">Total hours worked</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Now</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{new Date().toLocaleTimeString()}</div>
            <p className="text-xs text-muted-foreground">Local time</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Shifts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.length}</div>
            <p className="text-xs text-muted-foreground">This week</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Night Shifts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{shifts.filter(s => isNightByStart(s.startTime) && (s.status || '').toLowerCase() !== 'absent').length}</div>
            <p className="text-xs text-muted-foreground">Đêm (+20k/công)</p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
