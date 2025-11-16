import React, { useMemo, useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Calendar } from 'lucide-react';
import { staffService } from '../../services/staffService';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { formatVND } from '../../utils/currency';
import { useAuth } from '../AuthProvider';

export function Payroll() {
  const { user } = useAuth();
  const today = new Date();
  const [monthOffset, setMonthOffset] = useState(0);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(false);
  const [history, setHistory] = useState([]);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const startDate = useMemo(() => {
    const d = new Date(today.getFullYear(), today.getMonth() + monthOffset, 1);
    return d.toISOString().split('T')[0];
  }, [monthOffset]);
  const endDate = useMemo(() => {
    const d = new Date(today.getFullYear(), today.getMonth() + monthOffset + 1, 0);
    return d.toISOString().split('T')[0];
  }, [monthOffset]);

  const load = async () => {
    setLoading(true);
    try {
      if (!user?.accountId && !user?.employeeId) {
        setSummary(null);
        return;
      }
      const result = await staffService.getPayrollSummary({ accountId: user?.accountId, employeeId: user?.employeeId, startDate, endDate });
      setSummary(result);
    } catch (e) {
      console.error('Failed to load payroll summary', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [monthOffset, user?.accountId, user?.employeeId]);

  const loadHistory = async () => {
    setLoadingHistory(true);
    try {
      if (!user?.accountId && !user?.employeeId) {
        setHistory([]);
        return;
      }
      const result = await staffService.getPayrollHistory({ accountId: user?.accountId, employeeId: user?.employeeId });
      setHistory(Array.isArray(result) ? result : []);
    } catch (e) {
      console.error('Failed to load payroll history', e);
    } finally {
      setLoadingHistory(false);
    }
  };

  useEffect(() => { loadHistory(); }, [user?.accountId, user?.employeeId]);

  const monthTitle = useMemo(() => {
    const d = new Date(startDate);
    return d.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  }, [startDate]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Payroll</h1>
        <p className="text-muted-foreground">Monthly hours and earnings</p>
      </div>

      <Card className="card-elevated">
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center gap-2"><Calendar className="h-4 w-4" /> {monthTitle}</CardTitle>
            <div className="space-x-2">
              <Button variant="outline" size="sm" onClick={() => setMonthOffset((m) => m - 1)}>Previous</Button>
              <Button variant="outline" size="sm" onClick={() => setMonthOffset(0)}>Current</Button>
              <Button variant="outline" size="sm" onClick={() => setMonthOffset((m) => m + 1)}>Next</Button>
            </div>
          </div>
          <CardDescription>
            Period: {startDate} - {endDate}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="text-sm text-muted-foreground">Loading…</div>
          ) : (
            <>
            <div className="grid gap-4 md:grid-cols-3">
              <Card className="card-elevated">
                <CardHeader className="pb-2"><CardTitle className="text-sm">Total Hours</CardTitle></CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{Number(summary?.totalHours ?? 0).toFixed(2)}h</div>
                </CardContent>
              </Card>
              <Card className="card-elevated">
                <CardHeader className="pb-2"><CardTitle className="text-sm">Hourly Rate</CardTitle></CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{formatVND(summary?.hourlyRate ?? 0)}</div>
                </CardContent>
              </Card>
              <Card className="card-elevated">
                <CardHeader className="pb-2"><CardTitle className="text-sm">Total Pay</CardTitle></CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{formatVND(summary?.totalPay ?? 0)}</div>
                </CardContent>
              </Card>
            </div>
            <div className="grid gap-4 md:grid-cols-3 mt-4">
              <Card className="card-elevated">
                <CardHeader className="pb-2"><CardTitle className="text-sm">Total Công</CardTitle></CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{summary?.totalShifts ?? 0}/{summary?.scheduledShifts ?? 0}</div>
                </CardContent>
              </Card>
              <Card className="card-elevated">
                <CardHeader className="pb-2"><CardTitle className="text-sm">Công Đêm</CardTitle></CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{summary?.nightShifts ?? 0}</div>
                </CardContent>
              </Card>
              <Card className="card-elevated">
                <CardHeader className="pb-2"><CardTitle className="text-sm">Night Bonus</CardTitle></CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{formatVND(summary?.nightBonus ?? 0)}</div>
                </CardContent>
              </Card>
            </div>
            </>
          )}
        </CardContent>
      </Card>

      <Card className="card-elevated">
        <CardHeader>
          <CardTitle>Lịch sử lương</CardTitle>
          <CardDescription>Thống kê theo tháng từ lúc bắt đầu</CardDescription>
        </CardHeader>
        <CardContent>
          {loadingHistory ? (
            <div className="text-sm text-muted-foreground">Loading…</div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Period</TableHead>
                  <TableHead>Total Hours</TableHead>
                  <TableHead>Total Shifts</TableHead>
                  <TableHead>Night Shifts</TableHead>
                  <TableHead>Hourly Rate</TableHead>
                  <TableHead>Night Bonus</TableHead>
                  <TableHead>Total Pay</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {(history || []).map((m, idx) => {
                  const period = m?.startDate ? new Date(m.startDate).toLocaleDateString('en-US', { month: 'long', year: 'numeric' }) : '-';
                  return (
                    <TableRow key={idx}>
                      <TableCell>{period}</TableCell>
                      <TableCell>{Number(m?.totalHours ?? 0).toFixed(2)}h</TableCell>
                      <TableCell>{m?.totalShifts ?? 0}/{m?.scheduledShifts ?? 0}</TableCell>
                      <TableCell>{m?.nightShifts ?? 0}</TableCell>
                      <TableCell>{formatVND(m?.hourlyRate ?? 0)}</TableCell>
                      <TableCell>{formatVND(m?.nightBonus ?? 0)}</TableCell>
                      <TableCell className="font-medium">{formatVND(m?.totalPay ?? 0)}</TableCell>
                    </TableRow>
                  );
                })}
                {(!history || history.length === 0) && (
                  <TableRow>
                    <TableCell colSpan={7} className="text-center text-muted-foreground">
                      No data
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
