// Updated CustomerDashboard.jsx
import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { ChartContainer, ChartTooltip, ChartTooltipContent } from '../ui/chart';
import { Building, Table, Users, Calendar, Package, DollarSign, TrendingUp, Loader2, AlertCircle } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, ResponsiveContainer, LineChart, Line } from 'recharts';
import { customerService } from '../../services/customerService';

const formatCurrency = (value) => {
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

    // Example: get current customerId from auth or environment.
    // Replace this with actual auth context or prop.
    const CUSTOMER_ID = window.__APP__?.customerId ?? null; // fallback - set in app bootstrap

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                setError(null);

                const data = await customerService.getDashboardStats();
                console.log('Backend returned:', data);
                setStats(data);
            } catch (err) {
                console.error('Error:', err);
                setError('Failed to load dashboard data');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    // Export handlers (use CUSTOMER_ID or fallback)
    const handleExportRevenue = async (clubId) => {
        try {
            const cid = CUSTOMER_ID ?? (stats && stats.customerId) ?? 0;
            await customerService.exportRevenueExcel(cid, clubId);
        } catch (err) {
            console.error('Export revenue failed', err);
            alert('Export revenue failed: ' + err.message);
        }
    };

    const handleExportSalaries = async (clubId) => {
        try {
            const cid = CUSTOMER_ID ?? (stats && stats.customerId) ?? 0;
            await customerService.exportSalariesExcel(cid, clubId);
        } catch (err) {
            console.error('Export salaries failed', err);
            alert('Export salaries failed: ' + err.message);
        }
    };

    const handleExportTopProducts = async (clubId) => {
        try {
            const cid = CUSTOMER_ID ?? (stats && stats.customerId) ?? 0;
            await customerService.exportTopProductsExcel(cid, clubId, 5);
        } catch (err) {
            console.error('Export top products failed', err);
            alert('Export top products failed: ' + err.message);
        }
    };

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

    // Map backend DTO to frontend format (fallback safe)
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
        clubs: stats.clubs || [] // new: clubs array if backend returns dashboard by clubs
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
        clubs: []
    };

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-semibold">Club Dashboard</h1>
                <p className="text-muted-foreground">Overview of your billiards club</p>
            </div>

            {/* Stats Cards (unchanged) */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
                {/* ... existing cards unchanged (kept for brevity) ... */}
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
                {/* [other cards here - unchanged from previous implementation] */}
            </div>

            {/* Charts (unchanged) */}
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

            {/* Clubs list with export buttons (NEW) */}
            <Card>
                <CardHeader>
                    <CardTitle>Clubs</CardTitle>
                    <CardDescription>Export reports per club</CardDescription>
                </CardHeader>
                <CardContent>
                    {data.clubs.length === 0 ? (
                        <div className="text-sm text-muted-foreground">No clubs found for your account.</div>
                    ) : (
                        <div className="grid gap-4">
                            {data.clubs.map(club => (
                                <div key={club.clubId} className="flex items-center justify-between p-4 border rounded">
                                    <div>
                                        <div className="font-medium">{club.clubName ?? `Club ${club.clubId}`}</div>
                                        <div className="text-xs text-muted-foreground">Club ID: {club.clubId}</div>
                                    </div>
                                    <div className="flex space-x-2">
                                        <Button size="sm" onClick={() => handleExportRevenue(club.clubId)}>Export Revenue</Button>
                                        <Button size="sm" onClick={() => handleExportSalaries(club.clubId)}>Export Salaries</Button>
                                        <Button size="sm" onClick={() => handleExportTopProducts(club.clubId)}>Export Top Products</Button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </CardContent>
            </Card>
        </div>
    );
}
