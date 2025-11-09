import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { PageType } from '../Dashboard';
import { Calendar, Clock, Users, Plus } from 'lucide-react';



export function ShiftManagement({ onPageChange }) {
  const [currentWeek, setCurrentWeek] = useState(0);

  const shifts = [
    { id: '1', employee: 'Alice Johnson', shift: 'morning', time: '6:00 AM - 2:00 PM', date: '2025-11-05', status: 'assigned' },
    { id: '2', employee: 'Bob Smith', shift: 'afternoon', time: '2:00 PM - 10:00 PM', date: '2025-11-06', status: 'assigned' },
    { id: '3', employee: 'Carol Brown', shift: 'evening', time: '6:00 PM - 2:00 AM', date: '2025-11-07', status: 'assigned' },
    { id: '4', employee: 'Alice Johnson', shift: 'morning', time: '6:00 AM - 2:00 PM', date: '2025-11-05', status: 'assigned' },
    { id: '5', employee: 'Bob Smith', shift: 'afternoon', time: '2:00 PM - 10:00 PM', date: '2025-11-06', status: 'pending' },
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
      case 'assigned': return 'default';
      case 'pending': return 'secondary';
      case 'completed': return 'outline';
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
    return shifts.filter(shift => shift.date === date);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold">Work Shifts</h1>
          <p className="text-muted-foreground">Manage employee work schedules</p>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Add Shift
        </Button>
      </div>

      {/* Week Navigation */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>Weekly Schedule</CardTitle>
            <div className="flex items-center space-x-2">
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(currentWeek - 1)}>
                Previous Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(0)}>
                Current Week
              </Button>
              <Button variant="outline" size="sm" onClick={() => setCurrentWeek(currentWeek + 1)}>
                Next Week
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
              
              return (
                <div key={day} className="space-y-2">
                  <div className="text-center p-2 bg-muted rounded-lg">
                    <p className="font-medium text-sm">{day}</p>
                    <p className="text-xs text-muted-foreground">
                      {new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    </p>
                  </div>
                  
                  <div className="space-y-2 min-h-[200px]">
                    {dayShifts.map((shift) => (
                      <div
                        key={shift.id}
                        className={`p-2 rounded-lg border text-xs ${getShiftColor(shift.shift)}`}
                      >
                        <p className="font-medium">{shift.employee}</p>
                        <p className="text-xs">{shift.time}</p>
                        <Badge 
                          variant={getStatusColor(shift.status)} 
                          className="text-xs mt-1"
                        >
                          {shift.status}
                        </Badge>
                      </div>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        </CardContent>
      </Card>

      {/* Shift Types Legend */}
      <Card>
        <CardHeader>
          <CardTitle>Shift Types</CardTitle>
          <CardDescription>Available shift schedules</CardDescription>
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

      {/* Quick Stats */}
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
            <div className="text-2xl font-bold">
              {shifts.filter(s => s.status === 'assigned').length}
            </div>
            <p className="text-xs text-muted-foreground">Confirmed shifts</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Pending</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {shifts.filter(s => s.status === 'pending').length}
            </div>
            <p className="text-xs text-muted-foreground">Awaiting confirmation</p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}