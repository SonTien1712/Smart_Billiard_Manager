import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { PageType } from '../Dashboard';
import { Receipt, Calendar, Clock, DollarSign, CheckCircle, AlertCircle } from 'lucide-react';


export function StaffDashboard({ onPageChange }) {
  // Mock data
  const stats = {
    todayBills: 12,
    todayRevenue: 480,
    hoursWorked: 6.5,
    shiftStatus: 'active',
    checkInTime: '09:00 AM',
    nextShift: 'Tomorrow 2:00 PM'
  };

  const recentBills = [
    { id: '#001', table: 'Table 1', amount: 45, time: '2:30 PM', status: 'completed' },
    { id: '#002', table: 'Table 3', amount: 38, time: '2:15 PM', status: 'completed' },
    { id: '#003', table: 'Table 2', amount: 52, time: '1:45 PM', status: 'completed' },
    { id: '#004', table: 'Table 5', amount: 29, time: '1:20 PM', status: 'completed' },
  ];

  const upcomingShifts = [
    { date: 'Today', time: '2:00 PM - 10:00 PM', status: 'current' },
    { date: 'Tomorrow', time: '2:00 PM - 10:00 PM', status: 'scheduled' },
    { date: 'Wed, Jan 22', time: '9:00 AM - 5:00 PM', status: 'scheduled' },
    { date: 'Thu, Jan 23', time: '2:00 PM - 10:00 PM', status: 'scheduled' },
  ];

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
            <div className="text-2xl font-bold">${stats.todayRevenue}</div>
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
                <div key={bill.id} className="flex items-center justify-between p-3 border rounded-lg">
                  <div className="space-y-1">
                    <p className="font-medium">{bill.id}</p>
                    <p className="text-sm text-muted-foreground">{bill.table}</p>
                    <p className="text-xs text-muted-foreground">{bill.time}</p>
                  </div>
                  <div className="text-right space-y-1">
                    <p className="font-medium">${bill.amount}</p>
                    <Badge variant="outline" className="text-xs">
                      {bill.status}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Upcoming Shifts */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Upcoming Shifts</CardTitle>
              <CardDescription>Your scheduled work hours</CardDescription>
            </div>
            <Button onClick={() => onPageChange('schedule')}>
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {upcomingShifts.map((shift, index) => (
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

      {/* Quick Actions */}
      <Card>
        <CardHeader>
          <CardTitle>Quick Actions</CardTitle>
          <CardDescription>Common tasks for your shift</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-3">
            <Button 
              variant="outline" 
              className="h-20 flex flex-col space-y-2"
              onClick={() => onPageChange('bills')}
            >
              <Receipt className="h-6 w-6" />
              <span>Manage Bills</span>
            </Button>
            <Button 
              variant="outline" 
              className="h-20 flex flex-col space-y-2"
              onClick={() => onPageChange('schedule')}
            >
              <Calendar className="h-6 w-6" />
              <span>View Schedule</span>
            </Button>
            <Button 
              variant="outline" 
              className="h-20 flex flex-col space-y-2"
              onClick={() => onPageChange('attendance')}
            >
              <Clock className="h-6 w-6" />
              <span>Attendance</span>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}