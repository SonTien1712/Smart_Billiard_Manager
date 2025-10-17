import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { PageType } from '../Dashboard';
import { Receipt, Calendar, Clock, DollarSign, CheckCircle, AlertCircle } from 'lucide-react';
import { formatVND } from '../../utils/currency';
import { staffService } from '../../services/staffService';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '../ui/dialog';
import { useAuth } from '../AuthProvider';


export function StaffDashboard({ onPageChange }) {
  const { user } = useAuth();
  const [stats, setStats] = useState({ todayBills: 0, todayRevenue: 0, hoursWorked: 6.5, shiftStatus: 'active', checkInTime: '09:00 AM', nextShift: 'Tomorrow 2:00 PM' });
  const [recentBills, setRecentBills] = useState([]);
  const [billDetail, setBillDetail] = useState(null);
  const [detailOpen, setDetailOpen] = useState(false);
  const [upcoming, setUpcoming] = useState([]);

  const clubId = useMemo(() => user?.clubId || user?.club?.id || user?.employeeClubId || user?.employee?.clubId, [user]);

  useEffect(() => {
    const load = async () => {
      try {
        const [s, bills] = await Promise.all([
          staffService.getTodayStats(clubId ? { clubId } : undefined),
          staffService.getBills({ limit: 4, status: 'Paid', ...(clubId ? { clubId } : {}), ...(user?.employeeId ? { employeeId: user.employeeId } : {}) })
        ]);
        setStats(prev => ({ ...prev, todayBills: s?.todayBills || 0, todayRevenue: s?.todayRevenue || 0 }));
        const mapped = (bills || []).map(b => ({
          rawId: b.id,
          id: `#${String(b.id).padStart(3,'0')}`,
          table: b.tableName || 'Table',
          amount: Number(b.amount || 0),
          time: b.createdAt ? new Date(b.createdAt).toLocaleTimeString() : '',
          status: (b.status || '').toLowerCase() || 'completed'
        }));
        setRecentBills(mapped);
      } catch (e) {
        console.error('Failed to load dashboard', e);
        setRecentBills([]);
      }
    };
    load();
  }, [clubId]);

  const openBillDetail = async (id) => {
    try {
      const detail = await staffService.getBillById(id);
      setBillDetail(detail || null);
      setDetailOpen(true);
    } catch (e) {
      console.error('load bill detail failed', e);
      setBillDetail(null);
      setDetailOpen(true);
    }
  };

  // Load upcoming shifts (today + next 6 days)
  useEffect(() => {
    const toLocalYMD = (d) => {
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${y}-${m}-${day}`;
    };
    const labelForDate = (dateStr) => {
      const d = new Date(dateStr + 'T00:00:00');
      const today = new Date(); today.setHours(0,0,0,0);
      const tomorrow = new Date(today.getTime() + 24*60*60*1000);
      if (d.getTime() === today.getTime()) return 'Today';
      if (d.getTime() === tomorrow.getTime()) return 'Tomorrow';
      return d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
    };
    const toTimeLabel = (start, end) => {
      const fmt = (t) => {
        const [h,m] = (t || '00:00:00').split(':').map(Number);
        const d = new Date(); d.setHours(h||0, m||0, 0, 0);
        return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      };
      return `${fmt(start)} - ${fmt(end)}`;
    };
    const isCurrent = (dateStr, start) => {
      const now = new Date();
      const [h,m] = (start || '00:00:00').split(':').map(Number);
      const [yy,mm,dd] = dateStr.split('-').map(Number);
      const begin = new Date(yy, (mm||1)-1, dd||1, h||0, m||0, 0);
      const end = new Date(begin.getTime() + 8*60*60*1000); // assume <=8h window if no end
      const todayYMD = toLocalYMD(now);
      return (dateStr === todayYMD) && now >= begin && now <= end;
    };

    const loadShifts = async () => {
      try {
        const today = new Date();
        const end = new Date(today.getTime() + 6*24*60*60*1000);
        const params = {
          accountId: user?.accountId,
          employeeId: user?.employeeId,
          startDate: toLocalYMD(today),
          endDate: toLocalYMD(end)
        };
        const data = await staffService.getSchedule(params);
        const items = (Array.isArray(data) ? data : [])
          .filter(s => !!s.shiftDate)
          .sort((a,b) => (a.shiftDate + ' ' + (a.startTime||'00:00:00')).localeCompare(b.shiftDate + ' ' + (b.startTime||'00:00:00')))
          .slice(0, 4)
          .map(s => ({
            date: labelForDate(s.shiftDate),
            time: toTimeLabel(s.startTime, s.endTime),
            status: (isCurrent(s.shiftDate, s.startTime) || ((s.status||'').toLowerCase()==='present' && !s.actualEndTime)) ? 'current' : 'scheduled'
          }));
        setUpcoming(items);
      } catch (e) {
        console.error('load upcoming shifts failed', e);
        setUpcoming([]);
      }
    };

    loadShifts();
  }, [user]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Staff Dashboard</h1>
        <p className="text-muted-foreground">Your daily work overview and tasks</p>
      </div>

      {/* Stats Cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Today's Bills</CardTitle>
            <Receipt className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.todayBills}</div>
            <p className="text-xs text-muted-foreground">
              Bills processed today
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Today's Revenue</CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatVND(stats.todayRevenue)}</div>
            <p className="text-xs text-muted-foreground">
              Revenue generated
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Hours Worked</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.hoursWorked}h</div>
            <p className="text-xs text-muted-foreground">
              Since check-in at {stats.checkInTime}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Shift Status</CardTitle>
            {stats.shiftStatus === 'active' ? (
              <CheckCircle className="h-4 w-4 text-green-500" />
            ) : (
              <AlertCircle className="h-4 w-4 text-yellow-500" />
            )}
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold capitalize">{stats.shiftStatus}</div>
            <p className="text-xs text-muted-foreground">
              Current status
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Main Content */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* Recent Bills */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recent Bills</CardTitle>
              <CardDescription>Latest transactions processed</CardDescription>
            </div>
            <Button onClick={() => onPageChange('bills')}>
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentBills.map((bill) => (
                <div key={bill.id} className="flex items-center justify-between p-3 border rounded-lg cursor-pointer hover:bg-accent/40" onClick={() => openBillDetail(bill.rawId)}>
                  <div className="space-y-1">
                    <p className="font-medium">{bill.id}</p>
                    <p className="text-sm text-muted-foreground">{bill.table}</p>
                    <p className="text-xs text-muted-foreground">{bill.time}</p>
                  </div>
                  <div className="text-right space-y-1">
                    <p className="font-medium">{formatVND(bill.amount)}</p>
                    <Badge variant="outline" className="text-xs">
                      {bill.status}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Bill Detail */}
        <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
          <DialogContent className="max-w-xl">
            <DialogHeader>
              <DialogTitle>Bill #{billDetail?.id}</DialogTitle>
              <DialogDescription>{billDetail?.tableName || ''} — {(billDetail?.status || '').toLowerCase()}</DialogDescription>
            </DialogHeader>
            <div className="space-y-3">
              <div className="grid grid-cols-2 gap-2 text-sm">
                <div>Start: {billDetail?.startedAt ? new Date(billDetail.startedAt).toLocaleString() : '—'}</div>
                <div>End: {billDetail?.endedAt ? new Date(billDetail.endedAt).toLocaleString() : '—'}</div>
                <div>Total Hours: {billDetail?.totalHours ?? 0}</div>
                <div>Table Cost: {formatVND(Number(billDetail?.totalTableCost || 0))}</div>
                <div>Products: {formatVND(Number(billDetail?.totalProductCost || 0))}</div>
                <div>Discount: {formatVND(Number(billDetail?.discount || 0))}</div>
                <div className="col-span-2 font-semibold">Final: {formatVND(Number(billDetail?.finalAmount || 0))}</div>
              </div>
              <div className="border-t pt-2">
                <p className="font-medium mb-2">Items</p>
                {Array.isArray(billDetail?.items) && billDetail.items.length > 0 ? (
                  <div className="space-y-1 text-sm">
                    {billDetail.items.map(it => (
                      <div key={it.id} className="flex justify-between">
                        <span>{it.productName || `#${it.productId}` } x{it.quantity}</span>
                        <span>{formatVND(Number(it.subTotal || 0))}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-muted-foreground">No items</p>
                )}
              </div>
            </div>
          </DialogContent>
        </Dialog>

        {/* Upcoming Shifts */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Upcoming Shifts</CardTitle>
              <CardDescription>Your scheduled work hours</CardDescription>
            </div>
            <Button onClick={() => onPageChange('work')}>
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {(upcoming || []).map((shift, index) => (
                <div key={index} className="flex items-center justify-between p-3 border rounded-lg">
                  <div className="space-y-1">
                    <p className="font-medium">{shift.date}</p>
                    <p className="text-sm text-muted-foreground">{shift.time}</p>
                  </div>
                  <Badge variant={shift.status === 'current' ? 'default' : 'secondary'}>
                    {shift.status}
                  </Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions removed as requested */}
    </div>
  );
}
