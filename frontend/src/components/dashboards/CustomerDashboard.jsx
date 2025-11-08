import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { ChartContainer, ChartTooltip, ChartTooltipContent } from '../ui/chart';
import { Building, Table, Users, Calendar, Package, DollarSign, TrendingUp, Loader2, AlertCircle } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, ResponsiveContainer, LineChart, Line } from 'recharts';
import { customerService } from '../../services/customerService';

const formatCurrency = (value) => {
    // Handle BigDecimal or regular numbers
    const numValue = typeof value === 'object' && value !== null ? parseFloat(value) : value;
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
    }).format(numValue || 0);
};

export function CustomerDashboard({ onPageChange }) {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                setError(null);

                // Call your service here
                const data = await customerService.getDashboardStats();

                // Log to see what backend returns
                console.log('Backend returned:', data);

                setStats(data);
            } catch (err) {
                console.error('Error:', err);
                setError('Failed to load dashboard data');

                // TEMPORARY: Use mock data for testing
                // Remove this after backend is fixed
                setStats({
                    totalRevenue: 0,
                    totalTables: 0,
                    totalEmployees: 0,
                    activeShifts: 0,
                    totalProducts: 0,
                    monthlyGrowth: 0,
                    revenueData: [],
                    tableUsageData: [],
                });
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex flex-col items-center justify-center h-64 text-red-600">
                <AlertCircle className="h-8 w-8 mb-2" />
                <p>{error}</p>
                <Button onClick={() => window.location.reload()} variant="outline" className="mt-4">
                    Try Again
                </Button>
            </div>
        );
    }

    // Map backend DTO to frontend format
    const data = stats ? {
        totalRevenue: stats.todayRevenue || 0,
        todayBills: stats.todayBills || 0,
        totalTables: stats.totalTables || 0,
        totalEmployees: stats.totalEmployees || 0,
        activeShifts: stats.activeShifts || 0,
        totalProducts: stats.totalProducts || 0,
        monthlyGrowth: stats.monthlyGrowth || 0,
        revenueData: (stats.revenueData || []).map(item => ({
            date: item.date,
            revenue: typeof item.revenue === 'object' ? parseFloat(item.revenue) : item.revenue
        })),
        tableUsageData: stats.tableUsageData || [],
    } : {
        totalRevenue: 0,
        todayBills: 0,
        totalTables: 0,
        totalEmployees: 0,
        activeShifts: 0,
        totalProducts: 0,
        monthlyGrowth: 0,
        revenueData: [],
        tableUsageData: [],
    };

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-semibold">Club Dashboard</h1>
                <p className="text-muted-foreground">Overview of your billiards club</p>
            </div>

            {/* Stats Cards */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Today Revenue</CardTitle>
                        <DollarSign className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{formatCurrency(data.totalRevenue)}</div>
                        <p className="text-xs text-muted-foreground">{data.todayBills} bills today</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Tables</CardTitle>
                        <Table className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{data.totalTables}</div>
                        <p className="text-xs text-muted-foreground">Billiard tables</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Employees</CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{data.totalEmployees}</div>
                        <p className="text-xs text-muted-foreground">Total staff</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Active Shifts</CardTitle>
                        <Calendar className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{data.activeShifts}</div>
                        <p className="text-xs text-muted-foreground">Currently working</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Products</CardTitle>
                        <Package className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{data.totalProducts}</div>
                        <p className="text-xs text-muted-foreground">Available items</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Growth</CardTitle>
                        <TrendingUp className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">+{data.monthlyGrowth}%</div>
                        <p className="text-xs text-muted-foreground">Monthly growth</p>
                    </CardContent>
                </Card>
            </div>

            {/* Charts */}
            <div className="grid gap-4 md:grid-cols-2">
                <Card>
                    <CardHeader>
                        <CardTitle>Revenue Trend</CardTitle>
                        <CardDescription>Daily revenue for the past week</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer
                            config={{
                                revenue: {
                                    label: "Revenue",
                                    color: "hsl(var(--chart-1))",
                                },
                            }}
                            className="h-[200px]"
                        >
                            <ResponsiveContainer width="100%" height="100%">
                                <LineChart data={data.revenueData}>
                                    <XAxis dataKey="date" />
                                    <YAxis />
                                    <ChartTooltip content={<ChartTooltipContent />} />
                                    <Line type="monotone" dataKey="revenue" stroke="hsl(var(--chart-1))" strokeWidth={2} />
                                </LineChart>
                            </ResponsiveContainer>
                        </ChartContainer>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Table Usage</CardTitle>
                        <CardDescription>Hours played per table today</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer
                            config={{
                                hours: {
                                    label: "Hours",
                                    color: "hsl(var(--chart-2))",
                                },
                            }}
                            className="h-[200px]"
                        >
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={data.tableUsageData}>
                                    <XAxis dataKey="table" />
                                    <YAxis />
                                    <ChartTooltip content={<ChartTooltipContent />} />
                                    <Bar dataKey="hours" fill="hsl(var(--chart-2))" radius={[4, 4, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </ChartContainer>
                    </CardContent>
                </Card>
            </div>

            {/* Quick Actions */}
            <Card>
                <CardHeader>
                    <CardTitle>Quick Actions</CardTitle>
                    <CardDescription>Manage your club efficiently</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2"
                            onClick={() => onPageChange('tables')}
                        >
                            <Table className="h-6 w-6" />
                            <span>Manage Tables</span>
                        </Button>
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2"
                            onClick={() => onPageChange('staff')}
                        >
                            <Users className="h-6 w-6" />
                            <span>Manage Staff</span>
                        </Button>
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2"
                            onClick={() => onPageChange('shifts')}
                        >
                            <Calendar className="h-6 w-6" />
                            <span>Work Shifts</span>
                        </Button>
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2"
                            onClick={() => onPageChange('products')}
                        >
                            <Package className="h-6 w-6" />
                            <span>Manage Products</span>
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}