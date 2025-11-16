import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Calendar, Clock, User, ChevronLeft, ChevronRight } from 'lucide-react';

export function WorkSchedule({ onPageChange }) {
  const [currentWeek, setCurrentWeek] = useState(0);

  const myShifts = [
    { id: '1', date: '2024-01-22', shift: 'afternoon', time: '2:00 PM - 10:00 PM', status: 'scheduled', club: 'Downtown Billiards Club' },
    { id: '2', date: '2024-01-23', shift: 'afternoon', time: '2:00 PM - 10:00 PM', status: 'scheduled', club: 'Downtown Billiards Club' },
    { id: '3', date: '2024-01-24', shift: 'morning', time: '6:00 AM - 2:00 PM', status: 'scheduled', club: 'Downtown Billiards Club' },
    { id: '4', date: '2024-01-25', shift: 'afternoon', time: '2:00 PM - 10:00 PM', status: 'scheduled', club: 'Downtown Billiards Club' },
    { id: '5', date: '2024-01-26', shift: 'evening', time: '6:00 PM - 2:00 AM', status: 'scheduled', club: 'Downtown Billiards Club' },
  ];

  const getShiftColor = (shift) => {
    switch (shift) {
      case 'morning': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'afternoon': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'evening': return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'night': return 'bg-gray-100 text-gray-800 border-gray-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'scheduled': return 'default';
      case 'completed': return 'outline';
      case 'missed': return 'destructive';
      default: return 'secondary';
    }
  };

  const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  const currentDate = new Date();
  const startOfWeek = new Date(currentDate);
  startOfWeek.setDate(currentDate.getDate() - currentDate.getDay() + 1 + (currentWeek * 7));

  const getDateForDay = (dayIndex) => {
    const date = new Date(startOfWeek);
    date.setDate(startOfWeek.getDate() + dayIndex);
    return date.toISOString().split('T')[0];
  };

  const getShiftsForDate = (date) => {
    return myShifts.filter(shift => shift.date === date);
  };

  const getTotalHoursThisWeek = () => {
    const thisWeekShifts = myShifts.filter(shift => {
      const shiftDate = new Date(shift.date);
      const weekStart = new Date(startOfWeek);
      const weekEnd = new Date(weekStart);
      weekEnd.setDate(weekStart.getDate() + 6);
      return shiftDate >= weekStart && shiftDate <= weekEnd;
    });
    
    return thisWeekShifts.length * 8; // Assuming 8 hours per shift
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-semibold">Work Schedule</h1>
        <p className="text-muted-foreground">View your assigned work shifts</p>
      </div>

      {/* Week Navigation */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>Weekly Schedule</CardTitle>
            <div className="flex items-center space-x-2">
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(currentWeek - 1)}>
                <ChevronLeft className="h-4 w-4" />
                Previous Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(0)}>
                Current Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(currentWeek + 1)}>
                Next Week
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
          <CardDescription>
            Week of {startOfWeek.toLocaleDateString()} - {new Date(startOfWeek.getTime() + 6 * 24 * 60 * 60 * 1000).toLocaleDateString()}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-7 gap-4">
            {weekDays.map((day, index) => {
              const date = getDateForDay(index);
              const dayShifts = getShiftsForDate(date);
              const isToday = date === new Date().toISOString().split('T')[0];
              
              return (
                <div key={day} className="space-y-2">
                  <div className={`text-center p-2 rounded-lg ${isToday ? 'bg-primary text-primary-foreground' : 'bg-muted'}`}>
                    <p className="font-medium text-sm">{day}</p>
                    <p className="text-xs opacity-80">
                      {new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    </p>
                  </div>
                  
                  <div className="space-y-2 min-h-[150px]">
                    {dayShifts.length > 0 ? dayShifts.map((shift) => (
                      <div
                        key={shift.id}
                        className={`p-3 rounded-lg border text-xs ${getShiftColor(shift.shift)}`}
                      >
                        <div className="space-y-1">
                          <div className="flex items-center space-x-1">
                            <Clock className="h-3 w-3" />
                            <span className="font-medium">{shift.shift}</span>
                          </div>
                          <p className="text-xs">{shift.time}</p>
                          <Badge 
                            variant={getStatusColor(shift.status)} 
                            className="text-xs"
                          >
                            {shift.status}
                          </Badge>
                        </div>
                      </div>
                    )) : (
                      <div className="p-3 text-center text-xs text-muted-foreground border-2 border-dashed rounded-lg">
                        No shifts
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </CardContent>
      </Card>

      {/* Upcoming Shifts */}
      <Card>
        <CardHeader>
          <CardTitle>Upcoming Shifts</CardTitle>
          <CardDescription>Your next scheduled work shifts</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {myShifts.slice(0, 5).map((shift) => (
              <div key={shift.id} className="flex items-center justify-between p-4 border rounded-lg">
                <div className="flex items-center space-x-4">
                  <div className={`w-3 h-3 rounded-full ${
                    shift.shift === 'morning' ? 'bg-yellow-500' :
                    shift.shift === 'afternoon' ? 'bg-blue-500' :
                    shift.shift === 'evening' ? 'bg-purple-500' : 'bg-gray-500'
                  }`}></div>
                  <div className="space-y-1">
                    <p className="font-medium">
                      {new Date(shift.date).toLocaleDateString('en-US', { 
                        weekday: 'long', 
                        month: 'short', 
                        day: 'numeric' 
                      })}
                    </p>
                    <p className="text-sm text-muted-foreground">{shift.time}</p>
                    <p className="text-xs text-muted-foreground">{shift.club}</p>
                  </div>
                </div>
                <div className="text-right space-y-1">
                  <Badge variant={getStatusColor(shift.status)}>
                    {shift.status}
                  </Badge>
                  <p className="text-xs text-muted-foreground capitalize">{shift.shift} shift</p>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Quick Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">This Week</CardTitle>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{getTotalHoursThisWeek()}h</div>
            <p className="text-xs text-muted-foreground">Total hours scheduled</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Next Shift</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Today</div>
            <p className="text-xs text-muted-foreground">2:00 PM - 10:00 PM</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Shifts</CardTitle>
            <User className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{myShifts.length}</div>
            <p className="text-xs text-muted-foreground">This month</p>
          </CardContent>
        </Card>
      </div>

      {/* Shift Types Legend */}
      <Card>
        <CardHeader>
          <CardTitle>Shift Types</CardTitle>
          <CardDescription>Your available shift schedules</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-4">
            <div className="flex items-center space-x-3 p-3 border rounded-lg">
              <div className="w-4 h-4 rounded bg-yellow-200 border border-yellow-300"></div>
              <div>
                <p className="font-medium text-sm">Morning</p>
                <p className="text-xs text-muted-foreground">6:00 AM - 2:00 PM</p>
              </div>
            </div>
            
            <div className="flex items-center space-x-3 p-3 border rounded-lg">
              <div className="w-4 h-4 rounded bg-blue-200 border border-blue-300"></div>
              <div>
                <p className="font-medium text-sm">Afternoon</p>
                <p className="text-xs text-muted-foreground">2:00 PM - 10:00 PM</p>
              </div>
            </div>
            
            <div className="flex items-center space-x-3 p-3 border rounded-lg">
              <div className="w-4 h-4 rounded bg-purple-200 border border-purple-300"></div>
              <div>
                <p className="font-medium text-sm">Evening</p>
                <p className="text-xs text-muted-foreground">6:00 PM - 2:00 AM</p>
              </div>
            </div>
            
            <div className="flex items-center space-x-3 p-3 border rounded-lg">
              <div className="w-4 h-4 rounded bg-gray-200 border border-gray-300"></div>
              <div>
                <p className="font-medium text-sm">Night</p>
                <p className="text-xs text-muted-foreground">10:00 PM - 6:00 AM</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
