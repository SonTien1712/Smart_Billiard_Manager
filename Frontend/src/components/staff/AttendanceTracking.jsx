import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { PageType } from '../Dashboard';
import { Clock, LogIn, LogOut, Calendar, Timer, User } from 'lucide-react';


export function AttendanceTracking({ onPageChange }) {
  const [isCheckedIn, setIsCheckedIn] = useState(true);
  const [checkInTime] = useState('9:00 AM');
  const [currentTime, setCurrentTime] = useState(new Date().toLocaleTimeString());

  // Mock attendance history
  const attendanceHistory = [
    { date: '2024-01-22', checkIn: '9:00 AM', checkOut: '5:00 PM', totalHours: 8, status: 'completed' },
    { date: '2024-01-21', checkIn: '9:15 AM', checkOut: '5:10 PM', totalHours: 7.92, status: 'completed' },
    { date: '2024-01-20', checkIn: '8:55 AM', checkOut: '5:05 PM', totalHours: 8.17, status: 'completed' },
    { date: '2024-01-19', checkIn: '9:10 AM', checkOut: '4:55 PM', totalHours: 7.75, status: 'completed' },
    { date: '2024-01-18', checkIn: '9:00 AM', checkOut: '5:00 PM', totalHours: 8, status: 'completed' },
  ];

  const weeklyStats = {
    totalHours: 39.84,
    scheduledHours: 40,
    attendanceRate: 99.6,
    averageCheckIn: '9:04 AM'
  };

  // Update current time every second
  React.useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date().toLocaleTimeString());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const handleCheckIn = () => {
    setIsCheckedIn(true);
    // In real app, this would call an API to record check-in time
  };

  const handleCheckOut = () => {
    setIsCheckedIn(false);
    // In real app, this would call an API to record check-out time
  };

  const calculateHoursWorked = () => {
    if (!isCheckedIn) return 0;
    const checkInDate = new Date();
    checkInDate.setHours(9, 0, 0, 0); // Assuming check-in at 9:00 AM
    const now = new Date();
    const diff = now.getTime() - checkInDate.getTime();
    return Math.round((diff / (1000 * 60 * 60)) * 100) / 100; // Hours with 2 decimal places
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'completed': return 'default';
      case 'partial': return 'secondary';
      case 'absent': return 'destructive';
      default: return 'outline';
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Attendance Tracking</h1>
        <p className="text-muted-foreground">Track your work hours and attendance</p>
      </div>

      {/* Current Status */}
      <Card>
        <CardHeader>
          <CardTitle>Current Status</CardTitle>
          <CardDescription>Your attendance status for today</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-6 md:grid-cols-2">
            <div className="space-y-4">
              <div className="text-center p-6 border-2 border-dashed rounded-lg">
                <Clock className="h-12 w-12 mx-auto mb-4 text-primary" />
                <p className="text-2xl font-bold">{currentTime}</p>
                <p className="text-sm text-muted-foreground">Current Time</p>
              </div>
              
              <div className="flex space-x-4">
                {!isCheckedIn ? (
                  <Button 
                    onClick={handleCheckIn}
                    className="flex-1 bg-green-600 hover:bg-green-700"
                  >
                    <LogIn className="h-4 w-4 mr-2" />
                    Check In
                  </Button>
                ) : (
                  <Button 
                    onClick={handleCheckOut}
                    variant="outline"
                    className="flex-1"
                  >
                    <LogOut className="h-4 w-4 mr-2" />
                    Check Out
                  </Button>
                )}
              </div>
            </div>

            <div className="space-y-4">
              <div className="grid gap-4">
                <div className="p-4 border rounded-lg">
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-muted-foreground">Status</span>
                    <Badge variant={isCheckedIn ? 'default' : 'secondary'}>
                      {isCheckedIn ? 'Checked In' : 'Checked Out'}
                    </Badge>
                  </div>
                </div>

                {isCheckedIn && (
                  <>
                    <div className="p-4 border rounded-lg">
                      <div className="flex items-center justify-between">
                        <span className="text-sm text-muted-foreground">Check-in Time</span>
                        <span className="font-medium">{checkInTime}</span>
                      </div>
                    </div>

                    <div className="p-4 border rounded-lg">
                      <div className="flex items-center justify-between">
                        <span className="text-sm text-muted-foreground">Hours Worked</span>
                        <span className="font-medium">{calculateHoursWorked()}h</span>
                      </div>
                    </div>
                  </>
                )}

                <div className="p-4 border rounded-lg">
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-muted-foreground">Scheduled Shift</span>
                    <span className="font-medium">2:00 PM - 10:00 PM</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Weekly Summary */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Hours This Week</CardTitle>
            <Timer className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{weeklyStats.totalHours}h</div>
            <p className="text-xs text-muted-foreground">
              of {weeklyStats.scheduledHours}h scheduled
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Attendance Rate</CardTitle>
            <User className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{weeklyStats.attendanceRate}%</div>
            <p className="text-xs text-muted-foreground">This week</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Average Check-in</CardTitle>
            <LogIn className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{weeklyStats.averageCheckIn}</div>
            <p className="text-xs text-muted-foreground">This week</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Days Worked</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{attendanceHistory.length}</div>
            <p className="text-xs text-muted-foreground">This week</p>
          </CardContent>
        </Card>
      </div>

      {/* Attendance History */}
      <Card>
        <CardHeader>
          <CardTitle>Attendance History</CardTitle>
          <CardDescription>Your recent attendance records</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {attendanceHistory.map((record, index) => (
              <div key={index} className="flex items-center justify-between p-4 border rounded-lg">
                <div className="space-y-1">
                  <p className="font-medium">
                    {new Date(record.date).toLocaleDateString('en-US', { 
                      weekday: 'long', 
                      month: 'short', 
                      day: 'numeric' 
                    })}
                  </p>
                  <div className="flex items-center space-x-4 text-sm text-muted-foreground">
                    <div className="flex items-center space-x-1">
                      <LogIn className="h-3 w-3" />
                      <span>{record.checkIn}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <LogOut className="h-3 w-3" />
                      <span>{record.checkOut}</span>
                    </div>
                  </div>
                </div>
                <div className="text-right space-y-1">
                  <p className="font-medium">{record.totalHours}h</p>
                  <Badge variant={getStatusColor(record.status)}>
                    {record.status}
                  </Badge>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Quick Actions */}
      <Card>
        <CardHeader>
          <CardTitle>Quick Actions</CardTitle>
          <CardDescription>Common attendance actions</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-3">
            <Button variant="outline" className="h-16 flex flex-col space-y-2">
              <Calendar className="h-6 w-6" />
              <span>View Schedule</span>
            </Button>
            
            <Button variant="outline" className="h-16 flex flex-col space-y-2">
              <Timer className="h-6 w-6" />
              <span>Time Report</span>
            </Button>
            
            <Button variant="outline" className="h-16 flex flex-col space-y-2">
              <User className="h-6 w-6" />
              <span>Profile</span>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}