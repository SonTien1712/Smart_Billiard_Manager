import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Chart, ChartContainer, ChartTooltip, ChartTooltipContent } from '../ui/chart';
import { PageType } from '../Dashboard';
import { Building, Table, Users, Calendar, Package, DollarSign, TrendingUp, Activity, Loader2, AlertCircle } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, ResponsiveContainer, LineChart, Line } from 'recharts';

import { useAuth } from '../AuthProvider';
// Giả sử bạn có một service để gọi API, nếu không, bạn có thể dùng fetch/axios trực tiếp
import { api } from '../../services/api'; // Giả sử file api.js của bạn export một instance axios

// Hàm helper để format tiền tệ
const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
    }).format(value || 0);
};

export function CustomerDashboard({ onPageChange }) {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchDashboardStats = async () => {
            try {
                setLoading(true);
                setError(null);

                // Gọi API thật, sử dụng token từ useAuth nếu cần
                const response = await api.get('/dashboard/stats'); // Thay đổi URL nếu cần

                setStats(response.data);
            } catch (err) {
                console.error('Failed to fetch dashboard stats:', err);
                setError('Failed to load dashboard statistics.');
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardStats();
    }, []);

    // Xử lý trạng thái Loading
    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
            </div>
        );
    }

    // Xử lý trạng thái Error
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

    // Khi có dữ liệu (stats không còn null)
    const defaultStats = {
        totalRevenue: 0,
        totalTables: 0,
        totalEmployees: 0,
        activeShifts: 0,
        totalProducts: 0,
        monthlyGrowth: 0,
        revenueData: [],
        tableUsageData: [],
    };

    const currentStats = stats || defaultStats;

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-semibold">Club Dashboard</h1>
                <p className="text-muted-foreground">Overview of your billiards club operations</p>
            </div>

            {/* KPI Cards */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Revenue</CardTitle>
                        <DollarSign className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{formatCurrency(currentStats.totalRevenue)}</div>
                        <p className="text-xs text-muted-foreground">
                            +{currentStats.monthlyGrowth}% from last month
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Tables</CardTitle>
                        <Table className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{currentStats.totalTables}</div>
                        <p className="text-xs text-muted-foreground">
                            Billiard tables
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Employees</CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{currentStats.totalEmployees}</div>
                        <p className="text-xs text-muted-foreground">
                            Total staff members
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Active Shifts</CardTitle>
                        <Calendar className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{currentStats.activeShifts}</div>
                        <p className="text-xs text-muted-foreground">
                            Currently working
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Products</CardTitle>
                        <Package className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{currentStats.totalProducts}</div>
                        <p className="text-xs text-muted-foreground">
                            Available items
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Growth</CardTitle>
                        <TrendingUp className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">+{currentStats.monthlyGrowth}%</div>
                        <p className="text-xs text-muted-foreground">
                            Monthly growth
                        </p>
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
                                    color: "var(--chart-1)",
                                },
                            }}
                            className="h-[200px]"
                        >
                            <ResponsiveContainer width="100%" height="100%">
                                <LineChart data={currentStats.revenueData}>
                                    <XAxis dataKey="date" tickFormatter={(value) => new Date(value).toLocaleDateString()} />
                                    <YAxis tickFormatter={(value) => formatCurrency(value)} />
                                    <ChartTooltip content={<ChartTooltipContent formatter={(value) => formatCurrency(value)} />} />
                                    <Line type="monotone" dataKey="revenue" stroke="var(--chart-1)" strokeWidth={2} />
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
                                    color: "var(--chart-2)",
                                },
                            }}
                            className="h-[200px]"
                        >
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={currentStats.tableUsageData}>
                                    <XAxis dataKey="table" />
                                    <YAxis />
                                    <ChartTooltip content={<ChartTooltipContent />} />
                                    <Bar dataKey="hours" fill="var(--chart-2)" radius={[4, 4, 0, 0]} />
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